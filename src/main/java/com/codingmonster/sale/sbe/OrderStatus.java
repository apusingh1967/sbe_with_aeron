/* Generated SBE (Simple Binary Encoding) message codec. */
package com.codingmonster.sale.sbe;

@SuppressWarnings("all")
public enum OrderStatus
{
    Accepted((short)0),

    Rejected((short)1),

    Filled((short)2),

    PartiallyFilled((short)3),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((short)255);

    private final short value;

    OrderStatus(final short value)
    {
        this.value = value;
    }

    /**
     * The raw encoded value in the Java type representation.
     *
     * @return the raw value encoded.
     */
    public short value()
    {
        return value;
    }

    /**
     * Lookup the enum value representing the value.
     *
     * @param value encoded to be looked up.
     * @return the enum value representing the value.
     */
    public static OrderStatus get(final short value)
    {
        switch (value)
        {
            case 0: return Accepted;
            case 1: return Rejected;
            case 2: return Filled;
            case 3: return PartiallyFilled;
            case 255: return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
