/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.utilities;

import java.math.BigInteger;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author geoagdt
 */
public class GENESIS_TimeTest {

    public GENESIS_TimeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

//    /**
//     * Test of checkConsistency method, of class GENESIS_Time.
//     */
//    @Test
//    public void testCheckConsistency() {
//        System.out.println("checkConsistency");
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.checkConsistency();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of getDayOfYear_BigInteger method, of class GENESIS_Time.
     */
    @Test
    public void testGetDayOfYear_BigInteger() {
        System.out.println("getDayOfYear_BigInteger");
        long year = 1991;
        int day = 123;
        GENESIS_Time instance = new GENESIS_Time(year, day);
        BigInteger expResult = BigInteger.valueOf(day);
        BigInteger result = instance.getDayOfYear_BigInteger();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSecondOfDay_BigInteger method, of class GENESIS_Time.
     */
    @Test
    public void testGetSecondOfDay_BigInteger() {
        System.out.println("getSecondOfDay_BigInteger");
        long year = 1991;
        int day = 123;
        int secondOfDay = 1234;
        GENESIS_Time instance = new GENESIS_Time(year, day, secondOfDay);
        BigInteger expResult = BigInteger.valueOf(secondOfDay);
        BigInteger result = instance.getSecondOfDay_BigInteger();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMinuteOfHour_int method, of class GENESIS_Time.
     */
    @Test
    public void testGetMinuteOfHour_int() {
        System.out.println("getMinuteOfHour_int");
        GENESIS_Time instance;
        int expResult;
        int result;
        // There are 60 minutes and 3600 seconds in an hour. 
        instance = new GENESIS_Time(1991, 10, 120);
        expResult = 2;
        result = instance.getMinuteOfHour_int();
        assertEquals(expResult, result);
        instance = new GENESIS_Time(1991, 10, 1200);
        expResult = 20;
        result = instance.getMinuteOfHour_int();
        assertEquals(expResult, result);
        instance = new GENESIS_Time(1991, 10, 1210);
        expResult = 20;
        result = instance.getMinuteOfHour_int();
        assertEquals(expResult, result);
        instance = new GENESIS_Time(1991, 10, 12060);
        expResult = 21;
        result = instance.getMinuteOfHour_int();
        assertEquals(expResult, result);
    }

    /**
     * Test of addSecond method, of class GENESIS_Time.
     */
    @Test
    public void testAddSecond() {
        System.out.println("addSecond");
        GENESIS_Time instance;
        GENESIS_Time expResult;
        // There are 60 minutes and 3600 seconds in an hour. 
        instance = new GENESIS_Time(1991, 10, 120);
        instance.addSecond();
        expResult = new GENESIS_Time(1991, 10, 121);
        assertEquals(expResult, instance);
    }

    /**
     * Test of addSecond method, of class GENESIS_Time.
     */
    @Test
    public void testSubtractSecond() {
        System.out.println("subtractSecond");
        GENESIS_Time instance;
        GENESIS_Time expResult;
        // There are 60 minutes and 3600 seconds in an hour. 
        instance = new GENESIS_Time(1991, 10, 120);
        instance.subtractSecond();
        expResult = new GENESIS_Time(1991, 10, 119);
        assertEquals(expResult, instance);
    }

    /**
     * Test of addSeconds method, of class GENESIS_Time.
     */
    @Test
    public void testAddSeconds_long() {
        System.out.println("addSeconds");
        long secondsInDay = GENESIS_Time.NormalSecondsInDay_int;
        long secondsInHour = GENESIS_Time.NormalSecondsInHour_int;
        System.out.println("Seconds In a Day " + secondsInDay);
        System.out.println("Seconds In an Hour " + secondsInHour);
        // 2 days, 3 hours, 520 seconds,
        long seconds = (2L * (secondsInDay)) + (3L * (secondsInHour)) + 520L;
        GENESIS_Time instance;
        GENESIS_Time expResult;
        // There are 60 minutes and 3600 seconds in an hour. 
        // There are 86400 seconds in a day. 
        instance = new GENESIS_Time(1991, 10, 120);
        instance.addSeconds(seconds);
        //System.out.println(""+ instance);
        expResult = new GENESIS_Time(1991, 12, 11440);
        assertEquals(expResult, instance);
    }

//    /**
//     * Test of addSecondsExclusivelyBetweenZeroAndNormalSecondsInDay method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddSecondsExclusivelyBetweenZeroAndNormalSecondsInDay() {
//        System.out.println("addSecondsExclusivelyBetweenZeroAndNormalSecondsInDay");
//        long seconds = 0L;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addSecondsExclusivelyBetweenZeroAndNormalSecondsInDay(seconds);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addSecondsExclusivelyBetweenMinusNormalHoursInDayAndZero method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddSecondsExclusivelyBetweenMinusNormalHoursInDayAndZero() {
//        System.out.println("addSecondsExclusivelyBetweenMinusNormalHoursInDayAndZero");
//        long seconds = 0L;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addSecondsExclusivelyBetweenMinusNormalHoursInDayAndZero(seconds);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addSeconds method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddSeconds_BigInteger() {
//        System.out.println("addSeconds");
//        BigInteger seconds = null;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addSeconds(seconds);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addMinute method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddMinute() {
//        System.out.println("addMinute");
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addMinute();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addMinutes method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddMinutes() {
//        System.out.println("addMinutes");
//        long minutes = 0L;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addMinutes(minutes);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addHour method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddHour() {
//        System.out.println("addHour");
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addHour();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addHours method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddHours() {
//        System.out.println("addHours");
//        long hours = 0L;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addHours(hours);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addHoursExclusivelyBetweenZeroAndNormalHoursInDay method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddHoursExclusivelyBetweenZeroAndNormalHoursInDay() {
//        System.out.println("addHoursExclusivelyBetweenZeroAndNormalHoursInDay");
//        long hours = 0L;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addHoursExclusivelyBetweenZeroAndNormalHoursInDay(hours);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addHoursExclusivelyBetweenMinusNormalHoursInDayAndZero method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddHoursExclusivelyBetweenMinusNormalHoursInDayAndZero() {
//        System.out.println("addHoursExclusivelyBetweenMinusNormalHoursInDayAndZero");
//        long hours = 0L;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addHoursExclusivelyBetweenMinusNormalHoursInDayAndZero(hours);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addDay method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddDay() {
//        System.out.println("addDay");
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addDay();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of subtractDay method, of class GENESIS_Time.
//     */
//    @Test
//    public void testSubtractDay() {
//        System.out.println("subtractDay");
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.subtractDay();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addDays method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddDays() {
//        System.out.println("addDays");
//        long days = 0L;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addDays(days);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addDaysExclusivelyBetweenZeroAndNormalDaysInYear method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddDaysExclusivelyBetweenZeroAndNormalDaysInYear() {
//        System.out.println("addDaysExclusivelyBetweenZeroAndNormalDaysInYear");
//        long days = 0L;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addDaysExclusivelyBetweenZeroAndNormalDaysInYear(days);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addDaysExclusivelyBetweenMinusNormalDaysInYearAndZero method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddDaysExclusivelyBetweenMinusNormalDaysInYearAndZero() {
//        System.out.println("addDaysExclusivelyBetweenMinusNormalDaysInYearAndZero");
//        long days = 0L;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addDaysExclusivelyBetweenMinusNormalDaysInYearAndZero(days);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addYear method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddYear() {
//        System.out.println("addYear");
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addYear();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addYears method, of class GENESIS_Time.
//     */
//    @Test
//    public void testAddYears() {
//        System.out.println("addYears");
//        long years = 0L;
//        GENESIS_Time instance = new GENESIS_Time();
//        instance.addYears(years);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSecondOfYear_BigInteger method, of class GENESIS_Time.
//     */
//    @Test
//    public void testGetSecondOfYear_BigInteger() {
//        System.out.println("getSecondOfYear_BigInteger");
//        GENESIS_Time instance = new GENESIS_Time();
//        BigInteger expResult = null;
//        BigInteger result = instance.getSecondOfYear_BigInteger();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isBetween method, of class GENESIS_Time.
//     */
//    @Test
//    public void testIsBetween() {
//        System.out.println("isBetween");
//        GENESIS_Time _Start_Time = null;
//        GENESIS_Time _End_Time = null;
//        GENESIS_Time instance = new GENESIS_Time();
//        boolean expResult = false;
//        boolean result = instance.isBetween(_Start_Time, _End_Time);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of getDifference method, of class GENESIS_Time.
     */
    @Test
    public void testGetDifference() {
        System.out.println("getDifference");
        GENESIS_Time aTime;
        GENESIS_Time bTime;
        GENESIS_Time expResult;
        GENESIS_Time result;
        boolean test;
        // Test1
        aTime = new GENESIS_Time(10, 1);
        bTime = new GENESIS_Time(1, 10);
        expResult = new GENESIS_Time(8, 356, 0);
        result = (GENESIS_Time) aTime.subtract(bTime);
        System.out.println(result);
        System.out.println(expResult);
        //System.out.println(test);
        test = result.equals(expResult);
        assertTrue(test);
        assertEquals(expResult, result);


//        // Test2
//        expResult = new GENESIS_Time(-8,356,0);
//        result = (GENESIS_Time) bTime.getDifference(aTime);
//        test = result.equals(expResult);
//        System.out.println(result);
//        System.out.println(expResult);
//        System.out.println(test);
//        assertTrue(test);

    }

    /**
     * Test of getDifferenceInDays_BigInteger method, of class GENESIS_Time.
     */
    @Test
    public void testGetDifferenceInDays_BigInteger() {
        System.out.println("getDifferenceInDays_BigInteger");
        GENESIS_Time aTime;
        GENESIS_Time bTime;
        BigInteger expResult;
        BigInteger result;
        aTime = new GENESIS_Time(1, 10);
        bTime = new GENESIS_Time(10, 1);
        expResult = BigInteger.valueOf((8 * GENESIS_Time.NormalDaysInYear_int) + 356);
        result = aTime.getDifferenceInDays_BigInteger(bTime);
//        System.out.println(result);
        boolean test = result.equals(expResult);
        assertTrue(test);
        assertEquals(result, expResult);
    }

    /**
     * Test of getDifferenceInDays_long method, of class GENESIS_Time.
     */
    @Test
    public void testGetDifferenceInDays_long() {
        System.out.println("getDifferenceInDays_long");
        GENESIS_Time aTime;
        GENESIS_Time bTime;
        aTime = new GENESIS_Time(1, 10);
        bTime = new GENESIS_Time(10, 1);
        long expResult = 8L * 365L + 356L;
        long result = aTime.getDifferenceInDays_long(bTime);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDifferenceInSeconds_BigInteger method, of class GENESIS_Time.
     */
    @Test
    public void testGetDifferenceInSeconds_BigInteger() {
        System.out.println("getDifferenceInSeconds_BigInteger");
        GENESIS_Time aTime;
        GENESIS_Time bTime;
        aTime = new GENESIS_Time(1, 10);
        bTime = new GENESIS_Time(10, 1);
        BigInteger expResult = BigInteger.valueOf(
                ((8 * GENESIS_Time.NormalDaysInYear_int) + 356)
                * GENESIS_Time.NormalSecondsInDay_int);
        BigInteger result = aTime.getDifferenceInSeconds_BigInteger(bTime);
        //System.out.println(result);
        boolean test = result.equals(expResult);
        assertTrue(test);
    }

    /**
     * Test of getDifferenceInSeconds_long method, of class GENESIS_Time.
     */
    @Test
    public void testGetDifferenceInSeconds_long() {
        System.out.println("getDifferenceInSeconds_long");
        GENESIS_Time aTime;
        GENESIS_Time bTime;
        aTime = new GENESIS_Time(1, 10);
        bTime = new GENESIS_Time(10, 1);
        long expResult =
                ((8L * (long) GENESIS_Time.NormalDaysInYear_int) + 356L)
                * (long) GENESIS_Time.NormalSecondsInDay_int;
        long result = aTime.getDifferenceInSeconds_long(bTime);
        //System.out.println(result);
        assertEquals(expResult, result);
    }

    /**
     * Test of subtract method, of class GENESIS_Time.
     */
    @Test
    public void testSubtract() {
        System.out.println("subtract");
        GENESIS_Time a_Time0 = new GENESIS_Time(1, 10);
        GENESIS_Time a_Time1 = new GENESIS_Time(10, 1);
        GENESIS_Time expResult = new GENESIS_Time();
        expResult.setYear(8);
        expResult.setDayOfYear(356);
        expResult.setSecondOfDay(0);
        GENESIS_Time result = a_Time1.subtract(a_Time0);
        boolean test = result.equals(expResult);
//        System.out.println(result);
//        System.out.println(expResult);
//        System.out.println(test);
        assertTrue(test);
    }

    /**
     * Test of isNormalised method, of class GENESIS_Time.
     */
    @Test
    public void testIsNormalised() {
        System.out.println("isNormalised");
        GENESIS_Time a_Time = new GENESIS_Time();
        a_Time.setYear(0);
        a_Time.setDayOfYear(-1);
        a_Time.setSecondOfDay(0);
        boolean expResult = false;
        boolean result = a_Time.isNormalised();
        assertEquals(expResult, result);
    }

    /**
     * Test of normalise method, of class GENESIS_Time.
     */
    @Test
    public void testNormalise() {
        System.out.println("normalise");
        GENESIS_Time a_Time = new GENESIS_Time();
        a_Time.setYear(10);
        a_Time.setDayOfYear(-1);
        a_Time.setSecondOfDay(0);
        a_Time.normalise();
        GENESIS_Time expResult = new GENESIS_Time(9, 364);
        boolean test = a_Time.equals(expResult);
        assertTrue(test);
    }

    /**
     * Test of add method, of class GENESIS_Time.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        GENESIS_Time a_Time = new GENESIS_Time();
        a_Time.setYear(10);
        a_Time.setDayOfYear(-1);
        a_Time.setSecondOfDay(0);
        GENESIS_Time expResult = new GENESIS_Time(19, 363, 0);
        GENESIS_Time result = a_Time.add(a_Time);
        //System.out.println(result);
        boolean test = result.equals(expResult);
        assertTrue(test);
    }

    /**
     * Test of toString method, of class GENESIS_Time.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        GENESIS_Time instance = new GENESIS_Time(2, 0);
        String expResult = "GENESIS_Time(year 2, dayOfYear 0, secondOfDay 0)";
        String result = instance.toString();
        System.out.println(result);
        assertEquals(expResult, result);
    }

    /**
     * Test of toStringCSV method, of class GENESIS_Time.
     */
    @Test
    public void testToStringCSV() {
        System.out.println("toStringCSV");
        GENESIS_Time instance = new GENESIS_Time(2, 0);
        String expResult = "2, 0, 0";
        String result = instance.toStringCSV();
        System.out.println(result);
        assertEquals(expResult, result);
    }

    /**
     * Test of compareTo method, of class GENESIS_Time.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        GENESIS_Time a_Time = new GENESIS_Time();
        a_Time.setYear(10);
        a_Time.setDayOfYear(20);
        a_Time.setSecondOfDay(0);
        GENESIS_Time b_Time = new GENESIS_Time();
        b_Time.setYear(10);
        b_Time.setDayOfYear(21);
        b_Time.setSecondOfDay(0);
        int expResult = -1;
        int result = a_Time.compareTo(b_Time);
        assertEquals(expResult, result);
        b_Time.setYear(10);
        b_Time.setDayOfYear(19);
        b_Time.setSecondOfDay(0);
        result = a_Time.compareTo(b_Time);
        expResult = 1;
        assertEquals(expResult, result);
        b_Time.setYear(10);
        b_Time.setDayOfYear(20);
        b_Time.setSecondOfDay(0);
        result = a_Time.compareTo(b_Time);
        expResult = 0;
        assertEquals(expResult, result);

    }

    /**
     * Test of equals method, of class GENESIS_Time.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        GENESIS_Time a_Time = new GENESIS_Time();
        a_Time.setYear(10);
        a_Time.setDayOfYear(-1);
        a_Time.setSecondOfDay(0);
        a_Time.normalise();
        GENESIS_Time b_Time = new GENESIS_Time(9, 364);
        boolean expResult = true;
        boolean result = a_Time.equals(b_Time);
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class GENESIS_Time.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        GENESIS_Time instance = new GENESIS_Time(12, 13, 2);
        int expResult = 270176;
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }
}
