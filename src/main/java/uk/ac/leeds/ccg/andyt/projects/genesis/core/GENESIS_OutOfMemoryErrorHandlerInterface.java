/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import uk.ac.leeds.ccg.andyt.generic.memory.Generic_OutOfMemoryErrorHandlerInterface;

/**
 *
 * @author Andy
 */
public interface GENESIS_OutOfMemoryErrorHandlerInterface
    extends Generic_OutOfMemoryErrorHandlerInterface {

    //long tryToEnsureThereIsEnoughMemoryToContinue_Account(
    //        boolean handleOutOfMemoryError);
    //long tryToEnsureThereIsEnoughMemoryToContinue_Account(
    //        GENESIS_AgentCollection a_GENESIS_AgentCollection,
    //        boolean handleOutOfMemoryError);
    /**
     * A method to ensure there is enough memory to continue that returns a
     * HashSet<Long> identifying any AgentCollections swapped in the process.
     *
     * @param handleOutOfMemoryError
     */
    //HashSet<Long> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
    //        boolean handleOutOfMemoryError);
    /**
     * A method to ensure there is enough memory to continue whilst not swapping
     * to disk a_AgentCollection
     *
     * @param a_AgentCollection An AgentCollection not to be swapped.
     * @param handleOutOfMemoryError
     * @return A HashSet<Long> of identifiers for AgentCollections swapped or
     * null if no AgentCollections are swapped.
     */
//    HashSet<Long> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
//            GENESIS_AgentCollection a_GENESIS_AgentCollection,
//            boolean handleOutOfMemoryError);
    /**
     * A method to ensure there is enough memory to continue whilst not swapping
     * to disk a_AgentCollection and accounting by returning a HashSet<Long>
     * identifying which AgentCollections have been swapped in the process.
     *
     * @param a_AgentCollection An AgentCollection not to be swapped.
     * @param a_AgentCollection_ID ID of an AgentCollection not to be swapped.
     * @param handleOutOfMemoryError
     * @return A HashSet<Long> of identifiers for AgentCollections swapped or
     * null if none are swapped
     */
    //HashSet<Long> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
    //        long a_AgentCollection_ID,
    //        boolean handleOutOfMemoryError);
    /**
     * Initialises _MemoryReserve.
     *
     * @param handleOutOfMemoryError
     */
    //long init_MemoryReserve_Account(boolean handleOutOfMemoryError);
    //long init_MemoryReserve_Account(GENESIS_AgentCollection a_GENESIS_AgentCollection, boolean handleOutOfMemoryError);
    //HashSet<Long> init_MemoryReserve_AccountDetail(boolean handleOutOfMemoryError);
    //HashSet<Long> init_MemoryReserve_AccountDetail(GENESIS_AgentCollection a_GENESIS_AgentCollection, boolean handleOutOfMemoryError);
}
