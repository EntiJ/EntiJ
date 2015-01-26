/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.entij.util;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sta
 */
public class SingleLinkedMapTest {
    SingleLinkedMap<String, Integer> instance;
    Random random = new Random();
    
    static final String key1 = "lala";
    static final String key2 = "lola";
    static final String key3 = "anna";
    static final String key4 = "jim";
    static final Integer val1 = 15;
    static final Integer val2 = null;
    static final Integer val3 = -69;
    static final Integer val4 = 200;
    
    static final String noKey = "john";
    static final Integer noVal = 1200;
    
    public SingleLinkedMapTest() {
    }
    
    @Before
    public void setUp() {
        instance = new SingleLinkedMap<>(key1, val1);
        instance.put(key2, val2);
        instance.put(key3, val3);
        instance.put(key4, val4);
    }
    
    @Test
    public void testConstructors() {
        SingleLinkedMap<String, Integer> instance2 = new SingleLinkedMap<>(key1, val1);
        SingleLinkedMap<String, Integer> instance3 = new SingleLinkedMap<>();
        instance3.put(key1, val1);
        Map<String, Integer> helper = new HashMap<>();
        helper.put(key1, val1);
        assertEquals(helper, instance2);
        assertEquals(helper, instance3);
    }

    @Test
    public void testGet() {
        assertEquals(instance.get(key1), val1);
        assertEquals(instance.get(noKey), null);
    }
    
    @Test
    public void testPut() {
        Map<String, Integer> copy = new HashMap<>(instance);
        assertEquals(instance.put(noKey, noVal), null);
        copy.put(noKey, noVal);
        assertEquals(instance, copy);
        assertEquals(instance.put(key1, val2), val1);
        copy.put(key1, val2);
        assertEquals(instance, copy);
        
        try {
            instance.put(null, val1);
            fail("NullPointerException should have been thrown");
        } catch (NullPointerException e) {}
    }
    
    @Test
    public void testContainsKey() {
        assertTrue(instance.containsKey(key1));
        assertFalse(instance.containsKey(noKey));
        try {
            instance.containsKey(null);
            fail("NullPointerException should have been thrown");
        } catch (NullPointerException e) {}
    }
    
    @Test
    public void testContainsValue() {
        assertTrue(instance.containsValue(val1));
        assertFalse(instance.containsValue(noVal));
    }

    
    @Test
    public void testRemove() {
        Map<String, Integer> copy = new HashMap<>(instance);
        assertEquals(instance.remove(noKey), null);
        assertEquals(instance, copy);
        assertEquals(instance.remove(key1), val1);
        copy.remove(key1);
        assertEquals(instance, copy);
        assertEquals(instance.remove(key4), val4);
        copy.remove(key4);
        assertEquals(instance, copy);
        
        try {
            instance.remove(null);
            fail("NullPointerException should have been thrown");
        } catch (NullPointerException e) {}
    }

    @Test
    public void testClear() {
        instance.clear();
        assertTrue(instance.isEmpty());
        assertEquals(instance.size(), 0);
        assertEquals(instance, Collections.EMPTY_MAP);
    }
    
    @Test
    public void testEquals() {
        Map<String, Integer> copy = new HashMap<>(instance);
        assertEquals(copy, instance);
        assertEquals(instance, copy);
    }
    
    @Test
    public void testEntrySet() {
        Map<String, Integer> helper = new HashMap<>();
        
        for (Map.Entry<String, Integer> entry : instance.entrySet()) {
            helper.put(entry.getKey(), entry.getValue());
        }
        assertEquals(helper, instance);
        
        helper.remove(key1);
        assertFalse(instance.entrySet().remove("not instance of Entry"));
        assertFalse(instance.entrySet().remove(new AbstractMap.SimpleEntry<>("non existent", 15)));
        assertTrue(instance.entrySet().remove(new AbstractMap.SimpleEntry<>(key1, val1)));
        assertEquals(helper, instance);
        
        int indexToRemove = random.nextInt(instance.size());
        Iterator<Map.Entry<String, Integer>> it = instance.entrySet().iterator();
        try {
            it.remove(); // try remove without next called first
            fail("IllegalStateException should have been thrown");
        } catch (IllegalStateException e) {}
        for (int i = 0; i < indexToRemove; i++) {
            it.next();
        }
        Map.Entry<String, Integer> entryToRemove = it.next();
        helper.remove(entryToRemove.getKey());
        it.remove(); // remove via it.remove
        assertEquals(helper, instance);
        try {
            it.remove(); // try remove twice in a row
            fail("IllegalStateException should have been thrown");
        } catch (IllegalStateException e) {}
        
        instance.entrySet().clear();
        it = instance.entrySet().iterator();
        try {
            it.next();
            fail("NoSuchElementException should have been thrown");
        } catch (NoSuchElementException e) {}
        assertFalse(it.hasNext());
    }
    
    @Test
    public void testEntrySetValue() {
        Map<String, Integer> helper = new HashMap<>(instance);
        assertEquals(helper, instance);
        
        for (Map.Entry<String, Integer> entry : instance.entrySet()) {
            int rand = random.nextInt(100);
            helper.put(entry.getKey(), rand);
            entry.setValue(rand);
        }
        assertEquals(helper, instance);
    }
    
    @Test
    public void testEntrySetEquals() {
        for (Map.Entry<String, Integer> entry : instance.entrySet()) {
            assertEquals(entry, new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            assertFalse(entry.equals("not an instance"));
            assertFalse(entry.equals(new AbstractMap.SimpleEntry<>(noKey, entry.getValue())));
            assertFalse(entry.equals(new AbstractMap.SimpleEntry<>(entry.getKey(), noVal)));
            assertFalse(entry.equals(new AbstractMap.SimpleEntry<>(noKey, noVal)));
        }
    }
}
