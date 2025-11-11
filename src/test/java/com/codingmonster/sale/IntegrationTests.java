package com.codingmonster.sale;

import com.codingmonster.sale.sbe.*;
import io.aeron.Aeron;
import io.aeron.FragmentAssembler;
import io.aeron.Publication;
import io.aeron.Subscription;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTests {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  // Prepare a direct buffer to write the message into, only sender needs to prepare on same machine
  private final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(4096));

  // Create and wrap the header and message encoder
  private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
  private final OrderMessageEncoder orderEncoder = new OrderMessageEncoder();
  private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
  private final OrderMessageDecoder orderDecoder = new OrderMessageDecoder();

  // This uses BackoffIdleStrategy, which is a progressive idle strategy that escalates from busy
  // spinning → yielding → parking (sleeping).
  // The constructor parameters mean:
  // spins = 100
  //   First, the thread will busy-spin in a loop for up to 100 iterations (fastest, lowest latency,
  // but burns CPU).
  // yields = 10
  //   If still idle, the thread will call Thread.yield() up to 10 times (lets the OS scheduler run
  // other threads).
  // minParkPeriodNs = 1 microsecond
  //   After spins and yields, the thread will LockSupport.parkNanos() for a small time (here: 1
  // µs).
  // maxParkPeriodNs = 1 millisecond
  //   If it remains idle for longer, the park time backs off exponentially up to 1 ms max.
  private final IdleStrategy idleStrategy =
      new BackoffIdleStrategy(
          100, 10, TimeUnit.MICROSECONDS.toNanos(1), TimeUnit.MILLISECONDS.toNanos(1));

  @Test
  public void given_sell_and_matched_buy_then_filled_er() {
    Aeron.Context ctx =
        new Aeron.Context()
            .aeronDirectoryName("/tmp/aeron"); // match the name with server process if using IPC
    try (Aeron aeron = Aeron.connect(ctx)) {
      publish(aeron);
      processResponse(aeron);
    }
  }

  private void publish(Aeron aeron) {
    Publication publication = aeron.addPublication("aeron:ipc", 10);
    // or udp if sending to another machine e.g. - "aeron:udp?endpoint=localhost:40123"

    int offset = 0;

    for(int i = 0; i < 3; i++) {
      offset = encodeOrder(offset, i);
    }

    // Send the message over Aeron
    LOG.info("Publishing to: " + publication.toString());
    long result;
    do {
      result = publication.offer(buffer, 0, offset);
      if (result < 0) {
        if (result == Publication.BACK_PRESSURED) {
          idleStrategy.idle();
        } else if (result == Publication.NOT_CONNECTED) {
          idleStrategy.idle();
        } else if (result == Publication.ADMIN_ACTION) {
          idleStrategy.idle();
        } else {
          LOG.warn("Unknown error " + result);
        }
      } else {
        LOG.info("Message sent at position: " + result);
      }
    } while (result <= 0);
  }

  int encodeOrder(int offset, int seq) {
    orderEncoder.wrapAndApplyHeader(buffer, offset, headerEncoder);
    orderEncoder.orderId(seq + 1)
            .timestamp(System.nanoTime())
            .orderType(OrderType.New);

    final OrderMessageEncoder.ItemsEncoder items = orderEncoder.itemsCount(1);
    items.next()
            .productId(1000 + seq)
            .quantity((short) 2)
            .unitPrice().mantissa(1234);

    String note = "Batch order " + (seq + 1);
    byte[] noteBytes = note.getBytes(StandardCharsets.UTF_8);
    orderEncoder.putCustomerNote(noteBytes, 0, noteBytes.length);

    // return total length of this message
    return offset + headerEncoder.encodedLength() + orderEncoder.encodedLength();
  }


  private void processResponse(Aeron aeron) {
    FragmentAssembler handler =
        new FragmentAssembler(
            (buffer, offset, length, header) -> {

            });

    Subscription subscription = aeron.addSubscription("aeron:ipc", 11);
    int result;
    do {
      result = subscription.poll(handler, 1);
      if (result == 0) {
        idleStrategy.idle();
      }
    } while (result == 0);
  }
}
