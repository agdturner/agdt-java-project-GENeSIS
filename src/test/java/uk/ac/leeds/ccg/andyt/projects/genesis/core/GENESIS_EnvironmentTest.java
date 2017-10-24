/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.leeds.ccg.andyt.projects.genesis.process.GENESIS_AbstractModelTraffic;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;

/**
 *
 * @author geoagdt
 */
public class GENESIS_EnvironmentTest {

    public GENESIS_EnvironmentTest() {
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
//     * Test of getTrafficModel method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testGetTrafficModel() {
//        System.out.println("getTrafficModel");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        GENESIS_AbstractModelTraffic expResult = null;
//        GENESIS_AbstractModelTraffic result = instance.getTrafficModel();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of get_reporting_VectorEnvelope2D method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testGet_reporting_VectorEnvelope2D() {
//        System.out.println("get_reporting_VectorEnvelope2D");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        Vector_Envelope2D expResult = null;
//        Vector_Envelope2D result = instance.get_reporting_VectorEnvelope2D();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of init_MemoryReserve method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testInit_MemoryReserve_boolean() {
//        System.out.println("init_MemoryReserve");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.init_MemoryReserve(handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of init_MemoryReserve_AccountAgentCollections method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testInit_MemoryReserve_AccountAgentCollections() {
//        System.out.println("init_MemoryReserve_AccountAgentCollections");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        long expResult = 0L;
//        long result = instance.init_MemoryReserve_AccountAgentCollections(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of init_MemoryReserve_AccountDetailAgentCollections method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testInit_MemoryReserve_AccountDetailAgentCollections() {
//        System.out.println("init_MemoryReserve_AccountDetailAgentCollections");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        Object[] expResult = null;
//        Object[] result = instance.init_MemoryReserve_AccountDetailAgentCollections(handleOutOfMemoryError);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of init_MemoryReserve method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testInit_MemoryReserve_GENESIS_FemaleCollection_boolean() {
//        System.out.println("init_MemoryReserve");
//        GENESIS_FemaleCollection a_GENESIS_FemaleCollection = null;
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.init_MemoryReserve(a_GENESIS_FemaleCollection, handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of init_MemoryReserve method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testInit_MemoryReserve_GENESIS_MaleCollection_boolean() {
//        System.out.println("init_MemoryReserve");
//        GENESIS_MaleCollection a_GENESIS_MaleCollection = null;
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.init_MemoryReserve(a_GENESIS_MaleCollection, handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_DataAny method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_DataAny_boolean() {
//        System.out.println("swapToFile_DataAny");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        boolean expResult = false;
//        boolean result = instance.swapToFile_DataAny(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_DataAny method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_DataAny_0args() {
//        System.out.println("swapToFile_DataAny");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        boolean expResult = false;
//        boolean result = instance.swapToFile_DataAny();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_DataAnyExcept method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_DataAnyExcept_GENESIS_FemaleCollection_boolean() {
//        System.out.println("swapToFile_DataAnyExcept");
//        GENESIS_FemaleCollection a_GENESIS_FemaleCollection = null;
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        boolean expResult = false;
//        boolean result = instance.swapToFile_DataAnyExcept(a_GENESIS_FemaleCollection, handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_DataAnyExcept method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_DataAnyExcept_GENESIS_MaleCollection_boolean() {
//        System.out.println("swapToFile_DataAnyExcept");
//        GENESIS_MaleCollection a_GENESIS_MaleCollection = null;
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        boolean expResult = false;
//        boolean result = instance.swapToFile_DataAnyExcept(a_GENESIS_MaleCollection, handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_DataAnyExcept method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_DataAnyExcept_GENESIS_MaleCollection() {
//        System.out.println("swapToFile_DataAnyExcept");
//        GENESIS_MaleCollection a_GENESIS_MaleCollection = null;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        boolean expResult = false;
//        boolean result = instance.swapToFile_DataAnyExcept(a_GENESIS_MaleCollection);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_DataAnyExcept method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_DataAnyExcept_GENESIS_FemaleCollection() {
//        System.out.println("swapToFile_DataAnyExcept");
//        GENESIS_FemaleCollection a_GENESIS_FemaleCollection = null;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        boolean expResult = false;
//        boolean result = instance.swapToFile_DataAnyExcept(a_GENESIS_FemaleCollection);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_Data method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_Data() {
//        System.out.println("swapToFile_Data");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.swapToFile_Data();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_Grid2DSquareCellChunks method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_Grid2DSquareCellChunks_boolean() {
//        System.out.println("swapToFile_Grid2DSquareCellChunks");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.swapToFile_Grid2DSquareCellChunks(handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_Grid2DSquareCellChunks method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_Grid2DSquareCellChunks_0args() {
//        System.out.println("swapToFile_Grid2DSquareCellChunks");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.swapToFile_Grid2DSquareCellChunks();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_Grid2DSquareCellChunks_Account method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_Grid2DSquareCellChunks_Account_boolean() {
//        System.out.println("swapToFile_Grid2DSquareCellChunks_Account");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        long expResult = 0L;
//        long result = instance.swapToFile_Grid2DSquareCellChunks_Account(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_Grid2DSquareCellChunks_Account method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_Grid2DSquareCellChunks_Account_0args() {
//        System.out.println("swapToFile_Grid2DSquareCellChunks_Account");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        long expResult = 0L;
//        long result = instance.swapToFile_Grid2DSquareCellChunks_Account();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_Grid2DSquareCellChunk_Account method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_Grid2DSquareCellChunk_Account() {
//        System.out.println("swapToFile_Grid2DSquareCellChunk_Account");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        long expResult = 0L;
//        long result = instance.swapToFile_Grid2DSquareCellChunk_Account();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_Grid2DSquareCellChunk method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_Grid2DSquareCellChunk_boolean() {
//        System.out.println("swapToFile_Grid2DSquareCellChunk");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.swapToFile_Grid2DSquareCellChunk(handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_Grid2DSquareCellChunk method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_Grid2DSquareCellChunk_0args() {
//        System.out.println("swapToFile_Grid2DSquareCellChunk");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.swapToFile_Grid2DSquareCellChunk();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_AgentCollections method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_AgentCollections() {
//        System.out.println("swapToFile_AgentCollections");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.swapToFile_AgentCollections(handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_AgentCollections_Account method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_AgentCollections_Account_boolean() {
//        System.out.println("swapToFile_AgentCollections_Account");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        long expResult = 0L;
//        long result = instance.swapToFile_AgentCollections_Account(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_AgentCollections_Account method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_AgentCollections_Account_0args() {
//        System.out.println("swapToFile_AgentCollections_Account");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        long expResult = 0L;
//        long result = instance.swapToFile_AgentCollections_Account();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_AgentCollection_Account method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_AgentCollection_Account_boolean() {
//        System.out.println("swapToFile_AgentCollection_Account");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        long expResult = 0L;
//        long result = instance.swapToFile_AgentCollection_Account(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of swapToFile_AgentCollection_Account method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testSwapToFile_AgentCollection_Account_0args() {
//        System.out.println("swapToFile_AgentCollection_Account");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        long expResult = 0L;
//        long result = instance.swapToFile_AgentCollection_Account();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryToEnsureThereIsEnoughMemoryToContinue method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testTryToEnsureThereIsEnoughMemoryToContinue_boolean() {
//        System.out.println("tryToEnsureThereIsEnoughMemoryToContinue");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        boolean expResult = false;
//        boolean result = instance.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryToEnsureThereIsEnoughMemoryToContinue method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testTryToEnsureThereIsEnoughMemoryToContinue_0args() {
//        System.out.println("tryToEnsureThereIsEnoughMemoryToContinue");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        boolean expResult = false;
//        boolean result = instance.tryToEnsureThereIsEnoughMemoryToContinue();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryToEnsureThereIsEnoughMemoryToContinue method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testTryToEnsureThereIsEnoughMemoryToContinue_GENESIS_FemaleCollection_boolean() {
//        System.out.println("tryToEnsureThereIsEnoughMemoryToContinue");
//        GENESIS_FemaleCollection a_GENESIS_FemaleCollection = null;
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.tryToEnsureThereIsEnoughMemoryToContinue(a_GENESIS_FemaleCollection, handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryToEnsureThereIsEnoughMemoryToContinue method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testTryToEnsureThereIsEnoughMemoryToContinue_GENESIS_MaleCollection_boolean() {
//        System.out.println("tryToEnsureThereIsEnoughMemoryToContinue");
//        GENESIS_MaleCollection a_GENESIS_MaleCollection = null;
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        instance.tryToEnsureThereIsEnoughMemoryToContinue(a_GENESIS_MaleCollection, handleOutOfMemoryError);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryToEnsureThereIsEnoughMemoryToContinue method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testTryToEnsureThereIsEnoughMemoryToContinue_GENESIS_FemaleCollection() {
//        System.out.println("tryToEnsureThereIsEnoughMemoryToContinue");
//        GENESIS_FemaleCollection a_FemaleCollection = null;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        boolean expResult = false;
//        boolean result = instance.tryToEnsureThereIsEnoughMemoryToContinue(a_FemaleCollection);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryToEnsureThereIsEnoughMemoryToContinue method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testTryToEnsureThereIsEnoughMemoryToContinue_GENESIS_MaleCollection() {
//        System.out.println("tryToEnsureThereIsEnoughMemoryToContinue");
//        GENESIS_MaleCollection a_MaleCollection = null;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        boolean expResult = false;
//        boolean result = instance.tryToEnsureThereIsEnoughMemoryToContinue(a_MaleCollection);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testTryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections_boolean() {
//        System.out.println("tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        long expResult = 0L;
//        long result = instance.tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testTryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections_0args() {
//        System.out.println("tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        Object[] expResult = null;
//        Object[] result = instance.tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testTryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections_boolean() {
//        System.out.println("tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        Object[] expResult = null;
//        Object[] result = instance.tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections(handleOutOfMemoryError);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testTryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections_0args() {
//        System.out.println("tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        Object[] expResult = null;
//        Object[] result = instance.tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of get_Directory method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testGet_Directory_boolean() {
//        System.out.println("get_Directory");
//        boolean handleOutOfMemoryError = false;
//        GENESIS_Environment instance = new GENESIS_Environment();
//        File expResult = null;
//        File result = instance.get_Directory(handleOutOfMemoryError);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of get_Directory method, of class GENESIS_Environment.
//     */
//    @Test
//    public void testGet_Directory_0args() {
//        System.out.println("get_Directory");
//        GENESIS_Environment instance = new GENESIS_Environment();
//        File expResult = null;
//        File result = instance.get_Directory();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
