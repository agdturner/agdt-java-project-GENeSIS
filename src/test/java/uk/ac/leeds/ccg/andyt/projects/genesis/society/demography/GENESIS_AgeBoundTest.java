/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.Time;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;

/**
 *
 * @author geoagdt
 */
public class GENESIS_AgeBoundTest {
    
    public GENESIS_AgeBoundTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

//    /**
//     * Test of toString method, of class GENESIS_AgeBound.
//     */
//    @Test
//    public void testToString() {
//        System.out.println("toString");
//        GENESIS_AgeBound instance = new GENESIS_AgeBound();
//        String expResult = "";
//        String result = instance.toString();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAgeMinBound method, of class GENESIS_AgeBound.
//     */
//    @Test
//    public void testGetAgeMinBound() {
//        System.out.println("getAgeMinBound");
//        GENESIS_AgeBound instance = new GENESIS_AgeBound();
//        Time expResult = null;
//        Time result = instance.getAgeMinBound();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setAgeMinBound method, of class GENESIS_AgeBound.
//     */
//    @Test
//    public void testSetAgeMinBound() {
//        System.out.println("setAgeMinBound");
//        Time ageMinBound = null;
//        GENESIS_AgeBound instance = new GENESIS_AgeBound();
//        instance.setAgeMinBound(ageMinBound);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAgeMaxBound method, of class GENESIS_AgeBound.
//     */
//    @Test
//    public void testGetAgeMaxBound() {
//        System.out.println("getAgeMaxBound");
//        GENESIS_AgeBound instance = new GENESIS_AgeBound();
//        Time expResult = null;
//        Time result = instance.getAgeMaxBound();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setAgeMaxBound method, of class GENESIS_AgeBound.
//     */
//    @Test
//    public void testSetAgeMaxBound() {
//        System.out.println("setAgeMaxBound");
//        Time ageMaxBound = null;
//        GENESIS_AgeBound instance = new GENESIS_AgeBound();
//        instance.setAgeMaxBound(ageMaxBound);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of compareTo method, of class GENESIS_AgeBound.
//     */
//    @Test
//    public void testCompareTo() {
//        System.out.println("compareTo");
//        Object o = null;
//        GENESIS_AgeBound instance = new GENESIS_AgeBound();
//        int expResult = 0;
//        int result = instance.compareTo(o);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of equals method, of class GENESIS_AgeBound.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        GENESIS_AgeBound aGENESIS_AgeBound = new GENESIS_AgeBound(0L,1L);
        GENESIS_AgeBound instance = new GENESIS_AgeBound(0L,1L);
        boolean expResult = true;
        boolean result = instance.equals(aGENESIS_AgeBound);
        assertEquals(expResult, result);
        
        aGENESIS_AgeBound.ageMinBound = new GENESIS_Time(0,0);
        aGENESIS_AgeBound.ageMaxBound = new GENESIS_Time(1,0);
        aGENESIS_AgeBound.setAgeMin(new GENESIS_Time(0,0));
        aGENESIS_AgeBound.setAgeMax(new GENESIS_Time(1,0));
        result = instance.equals(aGENESIS_AgeBound);
        assertEquals(expResult, result);
        
        HashSet<GENESIS_AgeBound> aH = new HashSet<GENESIS_AgeBound>();
        aH.add(instance);
        
        if (aH.contains(aGENESIS_AgeBound)) {
            result = true;
        } else {
            result = false;
        }
        assertEquals(expResult, result);
        
        instance.setAgeMin(new GENESIS_Time(1,0));
        result = instance.equals(aGENESIS_AgeBound);
        expResult = false;
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

//    /**
//     * Test of hashCode method, of class GENESIS_AgeBound.
//     */
//    @Test
//    public void testHashCode() {
//        System.out.println("hashCode");
//        GENESIS_AgeBound instance = new GENESIS_AgeBound();
//        int expResult = 0;
//        int result = instance.hashCode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
