package gr.entij.util;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static gr.entij.util.Pair.pair;

public class PairTest {
    
    public PairTest() {
    }
    
    /**
     * Test of pair method, of class Pair.
     */
    @Test
    public void testPair() {
        System.out.println("pair");
        String val1 = "hello";
        Integer val2 = 157;
        Pair<String, Integer> result = pair(val1, val2);
        assertEquals(val1, result.val1);
        assertEquals(val2, result.val2);
    }

    /**
     * Test of hashCode method, of class Pair.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Map<Pair, Object> map = new HashMap<>();
        Pair pair1 = pair(15, 69);
        Object val1 = 98;
        Pair pair2 = pair(null, "lala");
        Object val2 = new HashMap();
        Pair pair3 = pair(17L, null);
        Object val3 = 78L;
        Pair pair4 = pair(null, null);
        Object val4 = 1254;
        
        map.put(pair1, val1);
        map.put(pair2, val2);
        map.put(pair3, val3);
        map.put(pair4, val4);
        
        assertEquals(map.get(pair1), val1);
        assertEquals(map.get(pair2), val2);
        assertEquals(map.get(pair3), val3);
        assertEquals(map.get(pair4), val4);
    }

    @Test
    public void testEquals() {
        assertTrue(pair(15, 25).equals(pair(15, 25)));
        assertFalse(pair(15, 25).equals(pair(15, 28)));
        assertFalse(pair(15, 25).equals(pair(18, 25)));
        assertFalse(pair(15, 25).equals(pair(18, 28)));
        assertFalse(pair(15, 25).equals(null));
        assertFalse(pair(15, 25).equals(15));
    }
}
