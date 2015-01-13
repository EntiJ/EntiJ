
package gr.entij;


public class Util {
    private static final long ONES = ~0L;
    
    private Util() {}
    
    public static boolean getBit(long data, int bit) {
        checkInLongBitRange("bit", bit);
        return (data & (1L << bit)) != 0;
    }
    
    public static long setBit(long data, int bit, boolean value) {
        checkInLongBitRange("bit", bit);
        return value
          ? data | (1L << bit)
          : data & ~(1L << bit);
    }
    
    public static long getBitRange(long data, int start, int length) {
        checkInLongBitRange("start", start);
        checkInLongBitRange("end", start+length);
        checkGreatedThanZero("length", length);
        
        data &= (ONES << start) & (ONES >>> (64-(start+length)));
        return data >>> start;
    }
    
    public static long setBitRange(long data, int start, int length, long value) {
        checkInLongBitRange("start", start);
        checkInLongBitRange("end", start+length);
        checkGreatedThanZero("length", length);
        
        value &= ~(ONES << length);
        value <<= start;
        data &= ~((ONES << start) & (ONES >>> (64-(start+length))));
        return data | value;
    }
    
    private static void checkInLongBitRange(String valName, int index)
            throws IllegalArgumentException {
        if (index < 0 || index > 63)
            throw new IllegalArgumentException(valName+" out of range 0..63: "+index);
    }
    
    private static void checkGreatedThanZero(String valName, int val)
            throws IllegalArgumentException {
        if (val <= 0)
            throw new IllegalArgumentException(valName+" must be > 0; found: "+val);
    }
}
