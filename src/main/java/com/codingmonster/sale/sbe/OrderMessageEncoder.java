/* Generated SBE (Simple Binary Encoding) message codec. */
package com.codingmonster.sale.sbe;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

/** Represents a customer order with multiple items. */
@SuppressWarnings("all")
public final class OrderMessageEncoder {
  public static final int BLOCK_LENGTH = 25;
  public static final int TEMPLATE_ID = 1;
  public static final int SCHEMA_ID = 100;
  public static final int SCHEMA_VERSION = 1;
  public static final String SEMANTIC_VERSION = "1.0.0";
  public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

  private final OrderMessageEncoder parentMessage = this;
  private MutableDirectBuffer buffer;
  private int offset;
  private int limit;

  public int sbeBlockLength() {
    return BLOCK_LENGTH;
  }

  public int sbeTemplateId() {
    return TEMPLATE_ID;
  }

  public int sbeSchemaId() {
    return SCHEMA_ID;
  }

  public int sbeSchemaVersion() {
    return SCHEMA_VERSION;
  }

  public String sbeSemanticType() {
    return "";
  }

  public MutableDirectBuffer buffer() {
    return buffer;
  }

  public int offset() {
    return offset;
  }

  public OrderMessageEncoder wrap(final MutableDirectBuffer buffer, final int offset) {
    if (buffer != this.buffer) {
      this.buffer = buffer;
    }
    this.offset = offset;
    limit(offset + BLOCK_LENGTH);

    return this;
  }

  public OrderMessageEncoder wrapAndApplyHeader(
      final MutableDirectBuffer buffer,
      final int offset,
      final MessageHeaderEncoder headerEncoder) {
    headerEncoder
        .wrap(buffer, offset)
        .blockLength(BLOCK_LENGTH)
        .templateId(TEMPLATE_ID)
        .schemaId(SCHEMA_ID)
        .version(SCHEMA_VERSION);

    return wrap(buffer, offset + MessageHeaderEncoder.ENCODED_LENGTH);
  }

  public int encodedLength() {
    return limit - offset;
  }

  public int limit() {
    return limit;
  }

  public void limit(final int limit) {
    this.limit = limit;
  }

  public static int orderIdId() {
    return 2;
  }

  public static int orderIdSinceVersion() {
    return 0;
  }

  public static int orderIdEncodingOffset() {
    return 0;
  }

  public static int orderIdEncodingLength() {
    return 8;
  }

  public static String orderIdMetaAttribute(final MetaAttribute metaAttribute) {
    if (MetaAttribute.PRESENCE == metaAttribute) {
      return "required";
    }

    return "";
  }

  public static long orderIdNullValue() {
    return 0xffffffffffffffffL;
  }

  public static long orderIdMinValue() {
    return 0x0L;
  }

  public static long orderIdMaxValue() {
    return 0xfffffffffffffffeL;
  }

  public OrderMessageEncoder orderId(final long value) {
    buffer.putLong(offset + 0, value, BYTE_ORDER);
    return this;
  }

  public static int clientIdId() {
    return 3;
  }

  public static int clientIdSinceVersion() {
    return 0;
  }

  public static int clientIdEncodingOffset() {
    return 8;
  }

  public static int clientIdEncodingLength() {
    return 8;
  }

  public static String clientIdMetaAttribute(final MetaAttribute metaAttribute) {
    if (MetaAttribute.PRESENCE == metaAttribute) {
      return "required";
    }

    return "";
  }

  public static long clientIdNullValue() {
    return 0xffffffffffffffffL;
  }

  public static long clientIdMinValue() {
    return 0x0L;
  }

  public static long clientIdMaxValue() {
    return 0xfffffffffffffffeL;
  }

  public OrderMessageEncoder clientId(final long value) {
    buffer.putLong(offset + 8, value, BYTE_ORDER);
    return this;
  }

  public static int timestampId() {
    return 4;
  }

  public static int timestampSinceVersion() {
    return 0;
  }

  public static int timestampEncodingOffset() {
    return 16;
  }

  public static int timestampEncodingLength() {
    return 8;
  }

  public static String timestampMetaAttribute(final MetaAttribute metaAttribute) {
    if (MetaAttribute.PRESENCE == metaAttribute) {
      return "required";
    }

    return "";
  }

  public static long timestampNullValue() {
    return 0xffffffffffffffffL;
  }

  public static long timestampMinValue() {
    return 0x0L;
  }

  public static long timestampMaxValue() {
    return 0xfffffffffffffffeL;
  }

  public OrderMessageEncoder timestamp(final long value) {
    buffer.putLong(offset + 16, value, BYTE_ORDER);
    return this;
  }

  public static int orderTypeId() {
    return 5;
  }

  public static int orderTypeSinceVersion() {
    return 0;
  }

  public static int orderTypeEncodingOffset() {
    return 24;
  }

  public static int orderTypeEncodingLength() {
    return 1;
  }

  public static String orderTypeMetaAttribute(final MetaAttribute metaAttribute) {
    if (MetaAttribute.PRESENCE == metaAttribute) {
      return "required";
    }

    return "";
  }

  public OrderMessageEncoder orderType(final OrderType value) {
    buffer.putByte(offset + 24, (byte) value.value());
    return this;
  }

  private final ItemsEncoder items = new ItemsEncoder(this);

  public static long itemsId() {
    return 20;
  }

  /**
   * List of items in the order.
   *
   * @param count of times the group will be encoded.
   * @return ItemsEncoder : encoder for the group.
   */
  public ItemsEncoder itemsCount(final int count) {
    items.wrap(buffer, count);
    return items;
  }

  /** List of items in the order. */
  public static final class ItemsEncoder {
    public static final int HEADER_SIZE = 3;
    private final OrderMessageEncoder parentMessage;
    private MutableDirectBuffer buffer;
    private int count;
    private int index;
    private int offset;
    private int initialLimit;

    ItemsEncoder(final OrderMessageEncoder parentMessage) {
      this.parentMessage = parentMessage;
    }

    public void wrap(final MutableDirectBuffer buffer, final int count) {
      if (count < 0 || count > 254) {
        throw new IllegalArgumentException("count outside allowed range: count=" + count);
      }

      if (buffer != this.buffer) {
        this.buffer = buffer;
      }

      index = 0;
      this.count = count;
      final int limit = parentMessage.limit();
      initialLimit = limit;
      parentMessage.limit(limit + HEADER_SIZE);
      buffer.putShort(limit + 0, (short) 14, BYTE_ORDER);
      buffer.putByte(limit + 2, (byte) count);
    }

    public ItemsEncoder next() {
      if (index >= count) {
        throw new java.util.NoSuchElementException();
      }

      offset = parentMessage.limit();
      parentMessage.limit(offset + sbeBlockLength());
      ++index;

      return this;
    }

    public int resetCountToIndex() {
      count = index;
      buffer.putByte(initialLimit + 2, (byte) count);

      return count;
    }

    public static short countMinValue() {
      return (short) 0;
    }

    public static short countMaxValue() {
      return (short) 254;
    }

    public static int sbeHeaderSize() {
      return HEADER_SIZE;
    }

    public static int sbeBlockLength() {
      return 14;
    }

    public static int productIdId() {
      return 21;
    }

    public static int productIdSinceVersion() {
      return 0;
    }

    public static int productIdEncodingOffset() {
      return 0;
    }

    public static int productIdEncodingLength() {
      return 4;
    }

    public static String productIdMetaAttribute(final MetaAttribute metaAttribute) {
      if (MetaAttribute.PRESENCE == metaAttribute) {
        return "required";
      }

      return "";
    }

    public static long productIdNullValue() {
      return 4294967295L;
    }

    public static long productIdMinValue() {
      return 0L;
    }

    public static long productIdMaxValue() {
      return 4294967294L;
    }

    public ItemsEncoder productId(final long value) {
      buffer.putInt(offset + 0, (int) value, BYTE_ORDER);
      return this;
    }

    public static int quantityId() {
      return 22;
    }

    public static int quantitySinceVersion() {
      return 0;
    }

    public static int quantityEncodingOffset() {
      return 4;
    }

    public static int quantityEncodingLength() {
      return 2;
    }

    public static String quantityMetaAttribute(final MetaAttribute metaAttribute) {
      if (MetaAttribute.PRESENCE == metaAttribute) {
        return "required";
      }

      return "";
    }

    public static int quantityNullValue() {
      return 65535;
    }

    public static int quantityMinValue() {
      return 0;
    }

    public static int quantityMaxValue() {
      return 65534;
    }

    public ItemsEncoder quantity(final int value) {
      buffer.putShort(offset + 4, (short) value, BYTE_ORDER);
      return this;
    }

    public static int unitPriceId() {
      return 23;
    }

    public static int unitPriceSinceVersion() {
      return 0;
    }

    public static int unitPriceEncodingOffset() {
      return 6;
    }

    public static int unitPriceEncodingLength() {
      return 8;
    }

    public static String unitPriceMetaAttribute(final MetaAttribute metaAttribute) {
      if (MetaAttribute.PRESENCE == metaAttribute) {
        return "required";
      }

      return "";
    }

    private final DecimalEncoder unitPrice = new DecimalEncoder();

    /**
     * Unit price as Decimal (mantissa * 10^exponent).
     *
     * @return DecimalEncoder : Unit price as Decimal (mantissa * 10^exponent).
     */
    public DecimalEncoder unitPrice() {
      unitPrice.wrap(buffer, offset + 6);
      return unitPrice;
    }
  }

  public static int customerNoteId() {
    return 30;
  }

  public static String customerNoteCharacterEncoding() {
    return java.nio.charset.StandardCharsets.UTF_8.name();
  }

  public static String customerNoteMetaAttribute(final MetaAttribute metaAttribute) {
    if (MetaAttribute.PRESENCE == metaAttribute) {
      return "required";
    }

    return "";
  }

  public static int customerNoteHeaderLength() {
    return 2;
  }

  public OrderMessageEncoder putCustomerNote(
      final DirectBuffer src, final int srcOffset, final int length) {
    if (length > 65534) {
      throw new IllegalStateException("length > maxValue for type: " + length);
    }

    final int headerLength = 2;
    final int limit = parentMessage.limit();
    parentMessage.limit(limit + headerLength + length);
    buffer.putShort(limit, (short) length, BYTE_ORDER);
    buffer.putBytes(limit + headerLength, src, srcOffset, length);

    return this;
  }

  public OrderMessageEncoder putCustomerNote(
      final byte[] src, final int srcOffset, final int length) {
    if (length > 65534) {
      throw new IllegalStateException("length > maxValue for type: " + length);
    }

    final int headerLength = 2;
    final int limit = parentMessage.limit();
    parentMessage.limit(limit + headerLength + length);
    buffer.putShort(limit, (short) length, BYTE_ORDER);
    buffer.putBytes(limit + headerLength, src, srcOffset, length);

    return this;
  }

  public OrderMessageEncoder customerNote(final String value) {
    final byte[] bytes =
        (null == value || value.isEmpty())
            ? org.agrona.collections.ArrayUtil.EMPTY_BYTE_ARRAY
            : value.getBytes(java.nio.charset.StandardCharsets.UTF_8);

    final int length = bytes.length;
    if (length > 65534) {
      throw new IllegalStateException("length > maxValue for type: " + length);
    }

    final int headerLength = 2;
    final int limit = parentMessage.limit();
    parentMessage.limit(limit + headerLength + length);
    buffer.putShort(limit, (short) length, BYTE_ORDER);
    buffer.putBytes(limit + headerLength, bytes, 0, length);

    return this;
  }

  public String toString() {
    if (null == buffer) {
      return "";
    }

    return appendTo(new StringBuilder()).toString();
  }

  public StringBuilder appendTo(final StringBuilder builder) {
    if (null == buffer) {
      return builder;
    }

    final OrderMessageDecoder decoder = new OrderMessageDecoder();
    decoder.wrap(buffer, offset, BLOCK_LENGTH, SCHEMA_VERSION);

    return decoder.appendTo(builder);
  }
}
