package com.codingmonster.sale;

import com.codingmonster.sale.sbe.*;
import io.aeron.Aeron;
import io.aeron.FragmentAssembler;
import io.aeron.Publication;
import io.aeron.Subscription;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());
  private static CountDownLatch latch;

  // none of these following fields is thread safe,
  // but we are going single thread, and plan to scale using thread per shard of instruments

  // Single subscription to channel where all trade requests received
  private final Subscription subscription;

  // one publication per client/trader which is key of map
  private final Publication publication;

  // reusable publish off heap buffer
  private final UnsafeBuffer publishBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(4096));
  // Create and wrap the header and message encoder
  private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
  private final OrderMessageDecoder orderDecoder = new OrderMessageDecoder();
  private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
  private final OrderResponseEncoder orderResponseEncoder = new OrderResponseEncoder();

  // This uses BackoffIdleStrategy, which is a progressive idle strategy that escalates from busy
  // spinning → yielding → parking (sleeping).
  // In dedicated single thread, just use busy spin. It will ensure cpu core is occupied.
  // The rest here is just for demonstrating other parameters.
  //
  // The constructor parameters mean:
  // spins = 100: First, the thread will busy-spin in a loop for up to 100 iterations (fastest,
  // lowest latency, but burns CPU).
  // yields = 10: If still idle, the thread will call Thread.yield() up to 10 times (lets the OS
  // scheduler run other threads).
  // minParkPeriodNs = 1 microsecond: After spins and yields, the thread will
  // LockSupport.parkNanos() for a small time (here: 1 µs).
  // maxParkPeriodNs = 1 millisecond: If it remains idle for longer, the park time backs off
  // exponentially up to 1 ms max.
  private final IdleStrategy idleStrategy =
      new BackoffIdleStrategy(
          100, 10, TimeUnit.MICROSECONDS.toNanos(1), TimeUnit.MILLISECONDS.toNanos(1));

  public static void main(String[] args) {
    latch = new CountDownLatch(1);

    Thread t = new Thread(Main::new);
    t.setDaemon(true);
    t.start();

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public Main() {
    // not thread safe, create in each thread
    Aeron.Context aeronCtx = new Aeron.Context().aeronDirectoryName("/tmp/aeron");

    try (Aeron aeron = Aeron.connect(aeronCtx)) {
      Subscription subscription = aeron.addSubscription("aeron:ipc", 10);
      LOG.info("Subscribed to: " + subscription.toString());
      this.subscription = subscription;

      // two clients setup so far
      this.publication = aeron.addPublication("aeron:ipc", 11);
      LOG.info(String.format("Matching Engine Instance Ready for: %s", this.publication));

      runEventLoop();
    }
  }

  private void runEventLoop() {
    // there is a more primitive alternative called FragmentHandler which handles one MTU only
    // but FragmentAssembler assembles all MTU of same message into single call
    //
    // buffer → internal receive buffer
    // offset → where your SBE message starts in receive buffer
    // length → total message size (you rarely need it)
    // header → Aeron transport header, not your SBE header
    //      Aeron internal buffer
    // ┌──────────────────────────────┬───────────────────────────┬──────────────┐
    // │  (Aeron transport framing)   │  SBE Message Header       │ Message Body │
    // │  (not visible to you here)   │ blockLen, templateId, ... │ your fields  │
    // └──────────────────────────────┴───────────────────────────┴──────────────┘
    //                              ^ offset passed to you

    FragmentAssembler assembler =
        new FragmentAssembler(
            (buffer, offset, length, header) -> {
              headerDecoder.wrap(buffer, offset);
              offset += headerDecoder.encodedLength();
              if (headerDecoder.templateId() == OrderMessageDecoder.TEMPLATE_ID) {
                orderDecoder.wrap(
                    buffer, offset, headerDecoder.blockLength(), headerDecoder.version());
                long orderId = orderDecoder.orderId();
                OrderType orderType = orderDecoder.orderType();
                byte[] noteBuffer = new byte[256]; // big enough for expected note
                int noteLength = orderDecoder.getCustomerNote(noteBuffer, 0, noteBuffer.length);
                String note = new String(noteBuffer, 0, noteLength, StandardCharsets.UTF_8);
                LOG.info(
                    "Received orderID: {}, ordType: {}, itemCount: {}, note: {}",
                    orderDecoder.orderId(),
                    orderType,
                    orderDecoder.items().count(),
                    note);
                for (int i = 0; i < orderDecoder.items().count(); i++) {
                  OrderMessageDecoder.ItemsDecoder item = orderDecoder.items().next();
                  orderResponseEncoder.wrapAndApplyHeader(
                      this.publishBuffer, 0, this.headerEncoder);
                  orderResponseEncoder
                      .orderId(orderId)
                      .timestamp(System.nanoTime())
                      .status(OrderStatus.Filled)
                      .filledQty(item.quantity())
                      .fillPrice()
                      .mantissa(item.unitPrice().mantissa());
                  orderResponseEncoder.serverNote("Hi from server");
                  LOG.info("Sending response");
                  this.publication.offer(
                      publishBuffer,
                      0,
                      headerEncoder.encodedLength() + orderResponseEncoder.encodedLength());
                }
              } else {
                LOG.warn("Unknown TemplateID: {}", headerDecoder.templateId());
              }
            });

    while (latch.getCount() > 0) {
      // Poll from the subscription
      // result can be zero if no message available
      // fragmentLimit is tunable
      // - Low for latency
      // - High for throughput
      int fragmentsRead = subscription.poll(assembler, 10);
      if (fragmentsRead != 0) {
        LOG.info("Fragments Read: {}", fragmentsRead);
      } else { // idle a bit
        idleStrategy.idle();
      }
    }
  }
}
