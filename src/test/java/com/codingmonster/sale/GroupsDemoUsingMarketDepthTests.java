package com.codingmonster.sale;

import com.codingmonster.common.sbe.trade.*;
import java.nio.ByteBuffer;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;

/**
 * Showing an example of how to encode and decode MarketDepth which has two levels of groups Group
 * is like an array Market depth has an array of: bids, asks, mid. Bids/asks/mid is an array of
 * elements each of which is a price & qty
 */
public class GroupsDemoUsingMarketDepthTests {

  @Test
  public void basicTest() {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4096);
    MutableDirectBuffer buf = new UnsafeBuffer(byteBuffer);
    encodeMarketDepth(buf);
    decodeMarketDepth(buf);
  }

  private static void encodeMarketDepth(MutableDirectBuffer buf) {
    MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    MarketDepthEncoder mde = new MarketDepthEncoder().wrapAndApplyHeader(buf, 0, headerEncoder);
    mde.sequenceId(123)
        .instrumentId(1)
        .updateType((short) 1)
        .timestamp(12345678)
        .exchangeTimestamp(12345555);
    // not so efficient, use short to represent instead of strings
    // symbol() and exchange() return encoders which are then used
    mde.symbol().appendTo(new StringBuilder("AAPL"));
    mde.exchange().appendTo(new StringBuilder("NASDAQ"));

    // Start the outer group with a count (number of bookLevels)
    MarketDepthEncoder.BookLevelEncoder bookLevels = mde.bookLevelCount(3);

    bookLevels.next(); // advance to first group entry
    bookLevels.bookType(MDEntryType.BID); // set the type enum

    // First level, bids
    MarketDepthEncoder.BookLevelEncoder.EntriesEncoder entries = bookLevels.entriesCount(3);
    add_entry(entries, 9995, 2000);
    add_entry(entries, 9990, 1500);
    add_entry(entries, 9985, 1000);
    // only three entries allowed!

    // ---- MID ----
    bookLevels.next();
    bookLevels.bookType(MDEntryType.MID);
    entries = bookLevels.entriesCount(1);
    add_entry(entries, 1000, 1500);
    // one entry for mid

    // ---- third book level, offers
    bookLevels.next();
    bookLevels.bookType(MDEntryType.OFFER);

    entries = bookLevels.entriesCount(2);
    add_entry(entries, 1005, 2000);
    add_entry(entries, 1010, 1000);
    // only two entries allowed!

    // One could batch multiple messages in the byteBuffer for efficiency, e.g.
    //  encode message 1
    //   offset += mde.encodedLength();
    //   mde.wrap(buffer, offset);
    //  encode message 2
    //        offset += mde.encodedLength();
    //        mde.wrap(buffer, offset);
  }

  private static void add_entry(
      MarketDepthEncoder.BookLevelEncoder.EntriesEncoder entries, int price, int quantity) {
    MarketDepthEncoder.BookLevelEncoder.EntriesEncoder entry = entries.next();
    entry.mdEntryPx().exponent((byte) -2).mantissa(price);
    entry.mdEntryQty().exponent((byte) -2).mantissa(quantity);
  }

  public static void decodeMarketDepth(DirectBuffer buffer) {
    final MessageHeaderDecoder header = new MessageHeaderDecoder();
    final MarketDepthDecoder mdd = new MarketDepthDecoder();
    mdd.wrapAndApplyHeader(buffer, 0, header);

    System.out.printf(
        "Seq=%d, Inst=%d, Type=%d%n", mdd.sequenceId(), mdd.instrumentId(), mdd.updateType());
    System.out.printf("Symbol=%s Exchange=%s%n", mdd.symbol(), mdd.exchange());

    for (MarketDepthDecoder.BookLevelDecoder bl : mdd.bookLevel()) {
      System.out.println("BookType: " + bl.bookType());
      for (MarketDepthDecoder.BookLevelDecoder.EntriesDecoder e : bl.entries()) {
        System.out.printf(
            "  Px=%.2f Qty=%.2f%n",
            e.mdEntryPx().mantissa() * Math.pow(10, e.mdEntryPx().exponent()),
            e.mdEntryQty().mantissa() * Math.pow(10, e.mdEntryQty().exponent()));
      }
    }
  }
}
