/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.entij.graphics2d;

import java.awt.Rectangle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sta
 */
public class AdditiveRectangeTest {
    
    static final Rectangle rec1;
    static final Rectangle rec2;
    static final Rectangle union;
    
    static {
        rec1 = new Rectangle(0, 0, 10, 15);
        rec2 = new Rectangle(5, 10, 20, 30);
        union = rec1.union(rec2);
    }
    
    public AdditiveRectangeTest() {
    }
    
    @Before
    public void setUp() {
    }

    /**
     * Test of getMinX method, of class AdditiveRectange.
     */
    @Test
    public void testGetMinX() {
        System.out.println("getMinX");
        AdditiveRectange instance = new AdditiveRectange();
        instance.addRectangle(rec2.x, rec2.y, rec2.width, rec2.height);
        int expResult = rec2.x;
        int result = instance.getMinX();
        assertEquals(expResult, result);
        
        instance.addRectangle(rec1.x, rec1.y, rec1.width, rec1.height);
        expResult = union.x;
        result = instance.getMinX();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMinY method, of class AdditiveRectange.
     */
    @Test
    public void testGetMinY() {
        System.out.println("getMinX");
        AdditiveRectange instance = new AdditiveRectange();
        instance.addRectangle(rec2.x, rec2.y, rec2.width, rec2.height);
        int expResult = rec2.y;
        int result = instance.getMinY();
        assertEquals(expResult, result);
        
        instance.addRectangle(rec1.x, rec1.y, rec1.width, rec1.height);
        expResult = union.y;
        result = instance.getMinY();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxX method, of class AdditiveRectange.
     */
    @Test
    public void testGetMaxX() {
        System.out.println("getMaxX");
        AdditiveRectange instance = new AdditiveRectange();
        instance.addRectangle(rec1.x, rec1.y, rec1.width, rec1.height);
        int expResult = rec1.x+rec1.width;
        int result = instance.getMaxX();
        assertEquals(expResult, result);
        
        instance.addRectangle(rec2.x, rec2.y, rec2.width, rec2.height);
        expResult = union.x+union.width;
        result = instance.getMaxX();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxY method, of class AdditiveRectange.
     */
    @Test
    public void testGetMaxY() {
        System.out.println("getMaxY");
        AdditiveRectange instance = new AdditiveRectange();
        instance.addRectangle(rec1.x, rec1.y, rec1.width, rec1.height);
        int expResult = rec1.y+rec1.height;
        int result = instance.getMaxY();
        assertEquals(expResult, result);
        
        instance.addRectangle(rec2.x, rec2.y, rec2.width, rec2.height);
        expResult = union.y+union.height;
        result = instance.getMaxY();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWidth method, of class AdditiveRectange.
     */
    @Test
    public void testGetWidth() {
        System.out.println("getWidth");
        AdditiveRectange instance = new AdditiveRectange();
        instance.addRectangle(rec1.x, rec1.y, rec1.width, rec1.height);
        int expResult = rec1.width;
        int result = instance.getWidth();
        assertEquals(expResult, result);
        
        instance.addRectangle(rec2.x, rec2.y, rec2.width, rec2.height);
        expResult = union.width;
        result = instance.getWidth();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHeight method, of class AdditiveRectange.
     */
    @Test
    public void testGetHeight() {
        System.out.println("getHeight");
        AdditiveRectange instance = new AdditiveRectange();
        instance.addRectangle(rec1.x, rec1.y, rec1.width, rec1.height);
        int expResult = rec1.height;
        int result = instance.getHeight();
        assertEquals(expResult, result);
        
        instance.addRectangle(rec2.x, rec2.y, rec2.width, rec2.height);
        expResult = union.height;
        result = instance.getHeight();
        assertEquals(expResult, result);
    }

    /**
     * Test of addArea method, of class AdditiveRectange.
     */
    @Test
    public void testAddArea() {
        System.out.println("addArea");

        AdditiveRectange instance = new AdditiveRectange();
        boolean expResult = true;
        boolean result = instance.addArea(rec1.x, rec1.y, rec1.x+rec1.width, rec1.y+rec1.height);
        assertEquals(expResult, result);
        assertEquals(instance.getRectangle(), rec1);
        
        expResult = true;
        result = instance.addArea(union.x, union.y, union.x+union.width, union.y+union.height);
        assertEquals(expResult, result);
        assertEquals(instance.getRectangle(), union);
        
        expResult = false;
        result = instance.addArea(rec2.x, rec2.y, rec2.x+rec2.width, rec2.y+rec2.height);
        assertEquals(expResult, result);
    }

    /**
     * Test of addRectangle method, of class AdditiveRectange.
     */
    @Test
    public void testAddRectangle() {
        System.out.println("addRectangle");
        AdditiveRectange instance = new AdditiveRectange();
        AdditiveRectange instance2 = new AdditiveRectange();
        
        boolean expResult = true;
        boolean result = instance.addRectangle(rec1.x, rec1.y, rec1.width, rec1.height);
        instance2.addArea(rec1.x, rec1.y, rec1.x+rec1.width, rec1.y+rec1.height);
        assertEquals(expResult, result);
        assertEquals(instance.getRectangle(), instance2.getRectangle());
        
        expResult = true;
        result = instance.addRectangle(union.x, union.y, union.width, union.height);
        instance2.addArea(union.x, union.y, union.x+union.width, union.y+union.height);
        assertEquals(expResult, result);
        assertEquals(instance.getRectangle(), instance2.getRectangle());
        
        
        expResult = false;
        result = instance.addRectangle(rec2.x, rec2.y, rec2.width, rec2.height);
        assertEquals(expResult, result);
        assertEquals(instance.getRectangle(), instance2.getRectangle());
    }

    /**
     * Test of isValid method, of class AdditiveRectange.
     */
    @Test
    public void testIsValid() {
        System.out.println("isValid");
        AdditiveRectange instance = new AdditiveRectange();
        assertFalse(instance.isValid());
        instance.addArea(100, 100, 0, 0);
        assertFalse(instance.isValid());
        instance.addRectangle(rec1.x, rec1.y, rec1.width, rec1.height);
        Assert.assertTrue(instance.isValid());
    }

    /**
     * Test of getRectangle method, of class AdditiveRectange.
     */
    @Test
    public void testGetRectangle() {
        System.out.println("getRectangle");
        AdditiveRectange instance = new AdditiveRectange();
        assertEquals(instance.getRectangle(), null);
        instance.addRectangle(rec1.x, rec1.y, rec1.width, rec1.height);
        Rectangle expResult = rec1;
        Rectangle result = instance.getRectangle();
        assertEquals(expResult, result);
        
        instance.addRectangle(rec2.x, rec2.y, rec2.width, rec2.height);
        expResult = union;
        result = instance.getRectangle();
        assertEquals(expResult, result);
        
        instance = new AdditiveRectange();
        instance.addArea(100, 100, 0, 0);
        assertEquals(instance.getRectangle(), null);
    }
    
}
