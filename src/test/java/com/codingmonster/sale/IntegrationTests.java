package com.codingmonster.sale;

import com.codingmonster.common.sbe.trade.*;
import com.codingmonster.common.sbe.trade.OrderType;
import com.codingmonster.common.sbe.trade.Side;
import io.aeron.Aeron;
import io.aeron.FragmentAssembler;
import io.aeron.Publication;
import io.aeron.Subscription;
import java.nio.ByteBuffer;
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
  private final NewOrderSingleEncoder orderEncoder = new NewOrderSingleEncoder();

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
    // this is redundant! Use wrapAndApplyHeader
    //        headerEncoder
    //            .wrap(buffer, offset)
    //            .blockLength(orderEncoder.sbeBlockLength())
    //            .templateId(orderEncoder.sbeTemplateId())
    //            .schemaId(orderEncoder.sbeSchemaId())
    //            .version(orderEncoder.sbeSchemaVersion());
    //    offset += headerEncoder.encodedLength();
    //    orderEncoder.wrap(buffer, offset);

    orderEncoder
        // shortcut for adding header data and moving offset
        .wrapAndApplyHeader(buffer, offset, headerEncoder)
        .clOrdID(54321)
        .side(Side.Buy) // e.g., Buy
        .orderQty(200)
        .orderType(OrderType.Market);
    // exponent two decimal for equity
    orderEncoder.price().exponent((byte) 2).mantissa(22345);
    orderEncoder.senderCompID("trader1").symbol("IBM");

    int length = headerEncoder.encodedLength() + orderEncoder.encodedLength();

    // Send the message over Aeron
    LOG.info("Publishing to: " + publication.toString());
    long result;
    do {
      result = publication.offer(buffer, offset, length);
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

  private void processResponse(Aeron aeron) {
    FragmentAssembler handler =
        new FragmentAssembler(
            (buffer, offset, length, header) -> {
              // Decode header
              MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
              headerDecoder.wrap(buffer, offset); // will read only header from buffer
              offset += MessageHeaderDecoder.ENCODED_LENGTH;

              int templateId = headerDecoder.templateId();
              if (templateId == ExecutionReportDecoder.TEMPLATE_ID) {
                ExecutionReportDecoder executionReportDecoder = new ExecutionReportDecoder();
                executionReportDecoder.wrap(
                    buffer, offset, headerDecoder.blockLength(), headerDecoder.version());
                LOG.info("Exec Report: " + executionReportDecoder);
              } else {
                LOG.error("Unknown message with templateId: " + templateId);
              }
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
