package org.bitcoinj.core;

public class TransactionFlags {
    public static final long    TX_F_NONE               = 0x00000000L;
    public static final long    TX_F_IS_OVER_CONSENT    = 0x00010000L;
    public static final long    TX_F_IS_OVER_18         = 0x00020000L;
    public static final long    TX_F_IS_OVER_21         = 0x00040000L;
    public static final long    TX_F_4                  = 0x00080000L;
    public static final long    TX_F_5                  = 0x00100000L;
    public static final long    TX_F_6                  = 0x00200000L;
    public static final long    TX_F_7                  = 0x00400000L;
    public static final long    TX_F_8                  = 0x00800000L;
    public static final long    TX_F_9                  = 0x01000000L;
    public static final long    TX_F_10                 = 0x02000000L;
    public static final long    TX_F_11                 = 0x04000000L;
    public static final long    TX_F_12                 = 0x08000000L;
    public static final long    TX_F_13                 = 0x10000000L;
    public static final long    TX_F_14                 = 0x20000000L;
    public static final long    TX_F_15                 = 0x40000000L;
    public static final long    TX_F_INVALID_CODE       = 0x80000000L;
}
