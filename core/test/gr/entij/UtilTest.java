package gr.entij;

import org.junit.Test;
import static org.junit.Assert.*;

public class UtilTest {
    
    long data = Long.parseLong("11001000010101011010100", 2);
    
    public UtilTest() {
    }

    /**
     * Test of getBit method, of class Util.
     */
    @Test
    public void testGetBit() {
        System.out.println("getBit");
        assertEquals(true, Util.getBit(data, 2));
        assertEquals(false, Util.getBit(data, 5));
        assertEquals(false, Util.getBit(data, 0));
        
        try {
            Util.getBit(data, -1);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.getBit(data, -6);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.getBit(data, 64);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.getBit(data, 69);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
    }

    /**
     * Test of setBit method, of class Util.
     */
    @Test
    public void testSetBit() {
        System.out.println("setBit");
        // original = 11001000010101011010100
        assertEquals(Long.parseLong("11001000010101011011100", 2), Util.setBit(data, 3, true));
        assertEquals(Long.parseLong("11001000010101011010100", 2), Util.setBit(data, 3, false));
        assertEquals(Long.parseLong("11001000010101011000100", 2), Util.setBit(data, 4, false));
        assertEquals(Long.parseLong("11001000010101011010100", 2), Util.setBit(data, 4, true));
        
        try {
            Util.setBit(data, -1, true);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.setBit(data, -6, true);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.setBit(data, 64, false);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.setBit(data, 69, false);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
    }

    /**
     * Test of getBitRange method, of class Util.
     */
    @Test
    public void testGetBitRange() {
        System.out.println("getBitRange");
        // original = 11001000010101011010100
        assertEquals(Long.parseLong("100", 2), Util.getBitRange(data, 0, 3));
        assertEquals(Long.parseLong("1101", 2), Util.getBitRange(data, 4, 4));
        
        try {
            Util.getBitRange(data, 20, -2);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.getBitRange(data, -6, 10);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.getBitRange(data, 70, 5);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.getBitRange(data, 60, 10);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
    }

    /**
     * Test of setBitRange method, of class Util.
     */
    @Test
    public void testSetBitRange() {
        System.out.println("setBitRange");
        // original = 11001000010101011010100
        long data2 = Long.parseLong("11101", 2);
        assertEquals(Long.parseLong("11001000010101011010101", 2), Util.setBitRange(data, 0, 3, data2));
        assertEquals(Long.parseLong("11001000010101011010100", 2), Util.setBitRange(data, 4, 4, data2));
        
        try {
            Util.setBitRange(data, 20, -2, data2);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.setBitRange(data, -6, 10, data2);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.setBitRange(data, 70, 5, data2);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
        try {
            Util.setBitRange(data, 60, 10, data2);
            fail("An IllegalArgumentException should had been thrown");
        } catch (IllegalArgumentException e) {};
    }
    
}
