/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;

/**
 * Each Agent has a reference to at least one AgentCollection. This class
 * contains methods for creating and deleting the file store/cache. Contains
 * methods and collections for managing access to Agents with this store.
 */
public class GENESIS_MaleCollection
        extends GENESIS_AgentCollection
        implements Serializable, Comparable {

    //static final long serialVersionUID = 1L;

    protected GENESIS_MaleCollection() {}

    public GENESIS_MaleCollection(
            GENESIS_Environment ge,
            Long _AgentCollection_ID,
            String Type) {
        super(ge);
        this.AgentCollectionID = _AgentCollection_ID;
        this.Type = Type;
        this.AgentCollectionManager = ge.AgentEnvironment.AgentCollectionManager;
        initAgentID_Agent_Map();
    }

    public final void init(
            long agentCollectionID,
            String type) {
        this.AgentCollectionID = agentCollectionID;
        Type = type;
        if (type.equalsIgnoreCase(GENESIS_Person.getTypeLivingMale_String())) {
            this.AgentCollectionManager = ge.AgentEnvironment.AgentCollectionManager;
            this.AgentCollectionManager.ge = ge;
            this.AgentCollectionManager.LivingMales.put(AgentCollectionID, this);
        }
    }

    /**
     * This could be buggy due to rounding errors...
     *
     * @return File directory in which Object with _AgentID_Agent_HashMap is (to
     * be) stored.
     */
    @Override
    public File getDirectory() {
        // Recalculation is done as archive may have grown and Directory might
        // be out of date. There is scope for optimisation here...
        GENESIS_AgentCollectionManager theGENESIS_AgentCollectionManager = getAgentCollectionManager();
        long agentID = getAgentCollectionID();
        if (Type.equalsIgnoreCase(GENESIS_Person.getTypeLivingMale_String())) {
            Long indexOfLastBornMale = theGENESIS_AgentCollectionManager._IndexOfLastBornMale;
            Long indexOfLastBornMaleCollection = theGENESIS_AgentCollectionManager.getAgentCollection_ID(
                    indexOfLastBornMale);
//            Long indexOfLastBornMaleCollection = theGENESIS_AgentCollectionManager.getMaleCollection_ID(
//                    indexOfLastBornMale, Type, false);
//            Long indexOfLastBornMaleCollection = theGENESIS_AgentCollectionManager._IndexOfLastLivingMaleCollection;
//            Long indexOfLastBornMaleCollection =
//                    getAgentCollectionManager().getMaleCollection_ID(
//                    getAgentCollectionManager()._IndexOfLastBornMale,
//                    Type,
//                    ge.HOOMEF);
            Directory = new File(
                    Generic_StaticIO.getObjectDirectory(theGENESIS_AgentCollectionManager._LivingMaleDirectory,
                    agentID,
                    indexOfLastBornMaleCollection,
                    theGENESIS_AgentCollectionManager.MaximumNumberOfObjectsPerDirectory),
                    "" + agentID);
            File result = new File(Directory.toString());
            return result;
        } else {
            Long highestLeaf = Generic_StaticIO.getArchiveHighestLeaf(
                    getAgentCollectionManager().getDeadMaleDirectory(), "_");
            Directory = new File(
                    Generic_StaticIO.getObjectDirectory(theGENESIS_AgentCollectionManager.getDeadMaleDirectory(),
                    agentID,
                    highestLeaf,
                    theGENESIS_AgentCollectionManager.MaximumNumberOfObjectsPerDirectory),
                    "" + getAgentCollectionID());
            File result = new File(Directory.toString());
            return result;
        }
    }

    public GENESIS_Male getMale(
            Long a_Agent_ID,
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_Male result = getMale(a_Agent_ID);
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                    getAgentCollectionManager();
            Long a_MaleCollection_ID =
                    a_GENESIS_AgentCollectionManager.getMaleCollection_ID(
                    a_Agent_ID,
                    Type,
                    handleOutOfMemoryError);
            GENESIS_MaleCollection a_GENESIS_MaleCollection =
                    a_GENESIS_AgentCollectionManager.getMaleCollection(
                    a_MaleCollection_ID,
                    Type);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                        getAgentCollectionManager();
                Long a_MaleCollection_ID =
                        a_GENESIS_AgentCollectionManager.getMaleCollection_ID(a_Agent_ID,
                        Type,
                        ge.HOOMEF);
                GENESIS_MaleCollection a_GENESIS_MaleCollection =
                        a_GENESIS_AgentCollectionManager.getMaleCollection(
                        a_MaleCollection_ID,
                        Type);
                ge.swapDataAnyExcept(a_GENESIS_MaleCollection);
                ge.initMemoryReserve(a_GENESIS_MaleCollection,
                        ge.HOOMEF);
                return getMale(
                        a_Agent_ID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public GENESIS_Male getMale(
            Long a_Agent_ID) {
        AgentID_Agent_Map = getAgentID_Agent_Map();
        if (AgentID_Agent_Map.containsKey(a_Agent_ID)) {
            GENESIS_Male result = (GENESIS_Male) AgentID_Agent_Map.get(a_Agent_ID);
            result.ge = ge;

            // Debug
            if (result == null) {
                // what this may mean here is that the person is not dead, but 
                // has somehow been removed from the map...
                int debug = 1;
            }

            return result;
        } else {
            return null;
        }
//        GENESIS_Male result = (GENESIS_Male) AgentID_Agent_Map.get(a_Agent_ID);
//        if (result == null) {
//            // Try loading from Agent Directory
//            GENESIS_AgentCollectionManager a_AgentCollectionManager = getAgentCollectionManager();
//            File a_Agent_Directory = a_AgentCollectionManager.getMaleCollectionDirectory(a_Agent_ID, Type);
//            if (a_Agent_Directory.exists()) {
//                File[] agentFiles = a_Agent_Directory.listFiles();
//                if (agentFiles.length > 0) {
//                    result = (GENESIS_Male) Generic_StaticIO.readObject(agentFiles[0]);
//                    result.init(
//                            ge,
//                            a_Agent_ID, this.getAgentCollection_ID());
//                }
//            }
//        }
//        return result;
    }

    /**
     *
     * @param handleOutOfMemoryError
     */
    @Override
    public void write(boolean handleOutOfMemoryError) {
        try {
            write();
            if (handleOutOfMemoryError) {
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    this,
                    handleOutOfMemoryError);
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(this);
                ge.initMemoryReserve(this,
                        ge.HOOMEF);
                write(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof GENESIS_MaleCollection)) {
            return 1;
        }
        GENESIS_MaleCollection oGENESIS_MaleCollection = (GENESIS_MaleCollection) o;
        if (oGENESIS_MaleCollection.AgentCollectionID < this.AgentCollectionID) {
            return 1;
        } else {
            if (oGENESIS_MaleCollection.AgentCollectionID > this.AgentCollectionID) {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GENESIS_MaleCollection)) {
            return false;
        }
        if (this.compareTo(o) == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (int) (this.AgentCollectionID ^ (this.AgentCollectionID >>> 32));
        return hash;
    }
}
