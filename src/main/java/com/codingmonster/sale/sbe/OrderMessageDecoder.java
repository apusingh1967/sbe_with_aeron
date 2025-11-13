/* Generated SBE (Simple Binary Encoding) message codec. */
package com.codingmonster.sale.sbe;

import org.agrona.MutableDirectBuffer;
import org.agrona.DirectBuffer;


/**
 * Represents a customer order with multiple items.
 */
@SuppressWarnings("all")
public final class OrderMessageDecoder
{
    public static final int BLOCK_LENGTH = 25;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 100;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "1.0.0";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final OrderMessageDecoder parentMessage = this;
    private DirectBuffer buffer;
    private int offset;
    private int limit;
    int actingBlockLength;
    int actingVersion;

    public int sbeBlockLength()
    {
        return BLOCK_LENGTH;
    }

    public int sbeTemplateId()
    {
        return TEMPLATE_ID;
    }

    public int sbeSchemaId()
    {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion()
    {
        return SCHEMA_VERSION;
    }

    public String sbeSemanticType()
    {
        return "";
    }

    public DirectBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public OrderMessageDecoder wrap(
        final DirectBuffer buffer,
        final int offset,
        final int actingBlockLength,
        final int actingVersion)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        this.actingBlockLength = actingBlockLength;
        this.actingVersion = actingVersion;
        limit(offset + actingBlockLength);

        return this;
    }

    public OrderMessageDecoder wrapAndApplyHeader(
        final DirectBuffer buffer,
        final int offset,
        final MessageHeaderDecoder headerDecoder)
    {
        headerDecoder.wrap(buffer, offset);

        final int templateId = headerDecoder.templateId();
        if (TEMPLATE_ID != templateId)
        {
            throw new IllegalStateException("Invalid TEMPLATE_ID: " + templateId);
        }

        return wrap(
            buffer,
            offset + MessageHeaderDecoder.ENCODED_LENGTH,
            headerDecoder.blockLength(),
            headerDecoder.version());
    }

    public OrderMessageDecoder sbeRewind()
    {
        return wrap(buffer, offset, actingBlockLength, actingVersion);
    }

    public int sbeDecodedLength()
    {
        final int currentLimit = limit();
        sbeSkip();
        final int decodedLength = encodedLength();
        limit(currentLimit);

        return decodedLength;
    }

    public int actingVersion()
    {
        return actingVersion;
    }

    public int encodedLength()
    {
        return limit - offset;
    }

    public int limit()
    {
        return limit;
    }

    public void limit(final int limit)
    {
        this.limit = limit;
    }

    public static int orderIdId()
    {
        return 2;
    }

    public static int orderIdSinceVersion()
    {
        return 0;
    }

    public static int orderIdEncodingOffset()
    {
        return 0;
    }

    public static int orderIdEncodingLength()
    {
        return 8;
    }

    public static String orderIdMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long orderIdNullValue()
    {
        return 0xffffffffffffffffL;
    }

    public static long orderIdMinValue()
    {
        return 0x0L;
    }

    public static long orderIdMaxValue()
    {
        return 0xfffffffffffffffeL;
    }

    public long orderId()
    {
        return buffer.getLong(offset + 0, BYTE_ORDER);
    }


    public static int clientIdId()
    {
        return 3;
    }

    public static int clientIdSinceVersion()
    {
        return 0;
    }

    public static int clientIdEncodingOffset()
    {
        return 8;
    }

    public static int clientIdEncodingLength()
    {
        return 8;
    }

    public static String clientIdMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long clientIdNullValue()
    {
        return 0xffffffffffffffffL;
    }

    public static long clientIdMinValue()
    {
        return 0x0L;
    }

    public static long clientIdMaxValue()
    {
        return 0xfffffffffffffffeL;
    }

    public long clientId()
    {
        return buffer.getLong(offset + 8, BYTE_ORDER);
    }


    public static int timestampId()
    {
        return 4;
    }

    public static int timestampSinceVersion()
    {
        return 0;
    }

    public static int timestampEncodingOffset()
    {
        return 16;
    }

    public static int timestampEncodingLength()
    {
        return 8;
    }

    public static String timestampMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long timestampNullValue()
    {
        return 0xffffffffffffffffL;
    }

    public static long timestampMinValue()
    {
        return 0x0L;
    }

    public static long timestampMaxValue()
    {
        return 0xfffffffffffffffeL;
    }

    public long timestamp()
    {
        return buffer.getLong(offset + 16, BYTE_ORDER);
    }


    public static int orderTypeId()
    {
        return 5;
    }

    public static int orderTypeSinceVersion()
    {
        return 0;
    }

    public static int orderTypeEncodingOffset()
    {
        return 24;
    }

    public static int orderTypeEncodingLength()
    {
        return 1;
    }

    public static String orderTypeMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public short orderTypeRaw()
    {
        return ((short)(buffer.getByte(offset + 24) & 0xFF));
    }

    public OrderType orderType()
    {
        return OrderType.get(((short)(buffer.getByte(offset + 24) & 0xFF)));
    }


    private final ItemsDecoder items = new ItemsDecoder(this);

    public static long itemsDecoderId()
    {
        return 20;
    }

    public static int itemsDecoderSinceVersion()
    {
        return 0;
    }

    /**
     * List of items in the order.
     *
     * @return ItemsDecoder : List of items in the order.
     */
    public ItemsDecoder items()
    {
        items.wrap(buffer);
        return items;
    }

    /**
     * List of items in the order.
     */

    public static final class ItemsDecoder
        implements Iterable<ItemsDecoder>, java.util.Iterator<ItemsDecoder>
    {
        public static final int HEADER_SIZE = 3;
        private final OrderMessageDecoder parentMessage;
        private DirectBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int blockLength;

        ItemsDecoder(final OrderMessageDecoder parentMessage)
        {
            this.parentMessage = parentMessage;
        }

        public void wrap(final DirectBuffer buffer)
        {
            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }

            index = 0;
            final int limit = parentMessage.limit();
            parentMessage.limit(limit + HEADER_SIZE);
            blockLength = (buffer.getShort(limit + 0, BYTE_ORDER) & 0xFFFF);
            count = ((short)(buffer.getByte(limit + 2) & 0xFF));
        }

        public ItemsDecoder next()
        {
            if (index >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + blockLength);
            ++index;

            return this;
        }

        public static short countMinValue()
        {
            return (short)0;
        }

        public static short countMaxValue()
        {
            return (short)254;
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 14;
        }

        public int actingBlockLength()
        {
            return blockLength;
        }

        public int actingVersion()
        {
            return parentMessage.actingVersion;
        }

        public int count()
        {
            return count;
        }

        public java.util.Iterator<ItemsDecoder> iterator()
        {
            return this;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext()
        {
            return index < count;
        }

        public static int productIdId()
        {
            return 21;
        }

        public static int productIdSinceVersion()
        {
            return 0;
        }

        public static int productIdEncodingOffset()
        {
            return 0;
        }

        public static int productIdEncodingLength()
        {
            return 4;
        }

        public static String productIdMetaAttribute(final MetaAttribute metaAttribute)
        {
            if (MetaAttribute.PRESENCE == metaAttribute)
            {
                return "required";
            }

            return "";
        }

        public static long productIdNullValue()
        {
            return 4294967295L;
        }

        public static long productIdMinValue()
        {
            return 0L;
        }

        public static long productIdMaxValue()
        {
            return 4294967294L;
        }

        public long productId()
        {
            return (buffer.getInt(offset + 0, BYTE_ORDER) & 0xFFFF_FFFFL);
        }


        public static int quantityId()
        {
            return 22;
        }

        public static int quantitySinceVersion()
        {
            return 0;
        }

        public static int quantityEncodingOffset()
        {
            return 4;
        }

        public static int quantityEncodingLength()
        {
            return 2;
        }

        public static String quantityMetaAttribute(final MetaAttribute metaAttribute)
        {
            if (MetaAttribute.PRESENCE == metaAttribute)
            {
                return "required";
            }

            return "";
        }

        public static int quantityNullValue()
        {
            return 65535;
        }

        public static int quantityMinValue()
        {
            return 0;
        }

        public static int quantityMaxValue()
        {
            return 65534;
        }

        public int quantity()
        {
            return (buffer.getShort(offset + 4, BYTE_ORDER) & 0xFFFF);
        }


        public static int unitPriceId()
        {
            return 23;
        }

        public static int unitPriceSinceVersion()
        {
            return 0;
        }

        public static int unitPriceEncodingOffset()
        {
            return 6;
        }

        public static int unitPriceEncodingLength()
        {
            return 8;
        }

        public static String unitPriceMetaAttribute(final MetaAttribute metaAttribute)
        {
            if (MetaAttribute.PRESENCE == metaAttribute)
            {
                return "required";
            }

            return "";
        }

        private final DecimalDecoder unitPrice = new DecimalDecoder();

        /**
         * Unit price as Decimal (mantissa * 10^exponent).
         *
         * @return DecimalDecoder : Unit price as Decimal (mantissa * 10^exponent).
         */
        public DecimalDecoder unitPrice()
        {
            unitPrice.wrap(buffer, offset + 6);
            return unitPrice;
        }

        public StringBuilder appendTo(final StringBuilder builder)
        {
            if (null == buffer)
            {
                return builder;
            }

            builder.append('(');
            builder.append("productId=");
            builder.append(this.productId());
            builder.append('|');
            builder.append("quantity=");
            builder.append(this.quantity());
            builder.append('|');
            builder.append("unitPrice=");
            final DecimalDecoder unitPrice = this.unitPrice();
            if (null != unitPrice)
            {
                unitPrice.appendTo(builder);
            }
            else
            {
                builder.append("null");
            }
            builder.append(')');

            return builder;
        }
        
        public ItemsDecoder sbeSkip()
        {

            return this;
        }
    }

    public static int customerNoteId()
    {
        return 30;
    }

    public static int customerNoteSinceVersion()
    {
        return 0;
    }

    public static String customerNoteCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String customerNoteMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int customerNoteHeaderLength()
    {
        return 2;
    }

    public int customerNoteLength()
    {
        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, BYTE_ORDER) & 0xFFFF);
    }

    public int skipCustomerNote()
    {
        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, BYTE_ORDER) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getCustomerNote(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, BYTE_ORDER) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getCustomerNote(final byte[] dst, final int dstOffset, final int length)
    {
        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, BYTE_ORDER) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapCustomerNote(final DirectBuffer wrapBuffer)
    {
        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, BYTE_ORDER) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String customerNote()
    {
        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, BYTE_ORDER) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        final OrderMessageDecoder decoder = new OrderMessageDecoder();
        decoder.wrap(buffer, offset, actingBlockLength, actingVersion);

        return decoder.appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        if (null == buffer)
        {
            return builder;
        }

        final int originalLimit = limit();
        limit(offset + actingBlockLength);
        builder.append("[OrderMessage](sbeTemplateId=");
        builder.append(TEMPLATE_ID);
        builder.append("|sbeSchemaId=");
        builder.append(SCHEMA_ID);
        builder.append("|sbeSchemaVersion=");
        if (parentMessage.actingVersion != SCHEMA_VERSION)
        {
            builder.append(parentMessage.actingVersion);
            builder.append('/');
        }
        builder.append(SCHEMA_VERSION);
        builder.append("|sbeBlockLength=");
        if (actingBlockLength != BLOCK_LENGTH)
        {
            builder.append(actingBlockLength);
            builder.append('/');
        }
        builder.append(BLOCK_LENGTH);
        builder.append("):");
        builder.append("orderId=");
        builder.append(this.orderId());
        builder.append('|');
        builder.append("clientId=");
        builder.append(this.clientId());
        builder.append('|');
        builder.append("timestamp=");
        builder.append(this.timestamp());
        builder.append('|');
        builder.append("orderType=");
        builder.append(this.orderType());
        builder.append('|');
        builder.append("items=[");
        final int itemsOriginalOffset = items.offset;
        final int itemsOriginalIndex = items.index;
        final ItemsDecoder items = this.items();
        if (items.count() > 0)
        {
            while (items.hasNext())
            {
                items.next().appendTo(builder);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
        items.offset = itemsOriginalOffset;
        items.index = itemsOriginalIndex;
        builder.append(']');
        builder.append('|');
        builder.append("customerNote=");
        builder.append('\'').append(customerNote()).append('\'');

        limit(originalLimit);

        return builder;
    }
    
    public OrderMessageDecoder sbeSkip()
    {
        sbeRewind();
        ItemsDecoder items = this.items();
        if (items.count() > 0)
        {
            while (items.hasNext())
            {
                items.next();
                items.sbeSkip();
            }
        }
        skipCustomerNote();

        return this;
    }
}
