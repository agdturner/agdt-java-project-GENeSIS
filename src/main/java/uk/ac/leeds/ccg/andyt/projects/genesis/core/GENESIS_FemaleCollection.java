package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;

/**
 * A class for storing GENESIS_Female instances.
 */
public class GENESIS_FemaleCollection
        extends GENESIS_AgentCollection
        implements Serializable, Comparable {

    //static final long serialVersionUID = 1L;

    public GENESIS_FemaleCollection() {
    }

    public GENESIS_FemaleCollection(
            GENESIS_Environment age,
            long _AgentCollection_ID,
            String Type) {
        this.ge = age;
        this.AgentCollectionID = _AgentCollection_ID;
        this.Type = Type;
        this.AgentCollectionManager =
                ge.AgentEnvironment.AgentCollectionManager;
        initAgentID_Agent_Map();
    }

    public final void init(
            GENESIS_Environment age,
            long a_AgentCollection_ID,
            String aType) {
        this.ge = age;
        this.AgentCollectionID = a_AgentCollection_ID;
        this.Type = aType;
        if (aType.equalsIgnoreCase(GENESIS_Person.getTypeLivingFemale_String())) {
            this.AgentCollectionManager =
                    ge.AgentEnvironment.AgentCollectionManager;
            this.AgentCollectionManager.ge = age;
            this.AgentCollectionManager.LivingFemales.put(AgentCollectionID,
                    this);
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
        if (Type.equalsIgnoreCase(GENESIS_Person.getTypeLivingFemale_String())) {
            Long indexOfLastBornFemale = theGENESIS_AgentCollectionManager._IndexOfLastBornFemale;
            Long indexOfLastBornFemaleCollection = theGENESIS_AgentCollectionManager.getAgentCollection_ID(
                    indexOfLastBornFemale);
//            Long indexOfLastBornFemaleCollection = theGENESIS_AgentCollectionManager.getFemaleCollection_ID(
//                    indexOfLastBornFemale, Type, false);
//            Long indexOfLastBornFemaleCollection = theGENESIS_AgentCollectionManager._IndexOfLastLivingFemaleCollection;
//            Long indexOfLastBornFemaleCollection =
//                    getAgentCollectionManager().getFemaleCollection_ID(
//                    getAgentCollectionManager()._IndexOfLastBornFemale,
//                    Type,
//                    ge.HOOMEF);
            Directory = new File(
                    Generic_IO.getObjectDir(theGENESIS_AgentCollectionManager._LivingFemaleDirectory,
                    agentID,
                    indexOfLastBornFemaleCollection,
                    theGENESIS_AgentCollectionManager.MaximumNumberOfObjectsPerDirectory),
                    "" + agentID);
            File result = new File(this.Directory.toString());
            return result;
        } else {
            Long highestLeaf = Generic_IO.getArchiveHighestLeaf(
                    getAgentCollectionManager().getDeadFemaleDirectory(), "_");
            Directory = new File(
                    Generic_IO.getObjectDir(theGENESIS_AgentCollectionManager.getDeadFemaleDirectory(),
                    agentID,
                    highestLeaf,
                    theGENESIS_AgentCollectionManager.MaximumNumberOfObjectsPerDirectory),
                    "" + getAgentCollectionID());
            File result = new File(this.Directory.toString());
            return result;
        }
    }

    public GENESIS_Female getFemale(
            Long a_Agent_ID,
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_Female result = getFemale(a_Agent_ID);
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                    getAgentCollectionManager();
            Long a_FemaleCollection_ID =
                    a_GENESIS_AgentCollectionManager.getFemaleCollection_ID(
                    a_Agent_ID,
                    Type,
                    handleOutOfMemoryError);
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection =
                    a_GENESIS_AgentCollectionManager.getFemaleCollection(
                    a_FemaleCollection_ID,
                    Type);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                        getAgentCollectionManager();
                Long a_FemaleCollection_ID =
                        a_GENESIS_AgentCollectionManager.getFemaleCollection_ID(a_Agent_ID,
                        Type,
                        ge.HOOMEF);
                GENESIS_FemaleCollection a_GENESIS_FemaleCollection =
                        a_GENESIS_AgentCollectionManager.getFemaleCollection(
                        a_FemaleCollection_ID,
                        Type);
                ge.swapDataAnyExcept(a_GENESIS_FemaleCollection);
                ge.initMemoryReserve(this, ge.HOOMEF);
                return getFemale(
                        a_Agent_ID,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * <code>
 AgentID_Agent_Map = getAgentID_Agent_Map();
 if (AgentID_Agent_Map.containsKey(a_Agent_ID)){
 GENESIS_Female result = (GENESIS_Female) AgentID_Agent_Map.get(a_Agent_ID);
 } else {
 return null;
 }
 </code>
     *
     * @param a_Agent_ID
     * @return GENESIS_Female if it exists in AgentID_Agent_Map and null
 otherwise. If null is returned, the GENESIS_Female is probably Dead and
 if so can be retrieved from a collection of the dead.
     */
    //protected GENESIS_Female getFemale(
    public GENESIS_Female getFemale(
            Long a_Agent_ID) {
        AgentID_Agent_Map = getAgentID_Agent_Map();
        if (AgentID_Agent_Map.containsKey(a_Agent_ID)) {
            // Female is Living
            GENESIS_Female result = (GENESIS_Female) AgentID_Agent_Map.get(a_Agent_ID);
            result.ge = ge;

            // Debug
            if (result == null) {
                // what this may mean here is that the person is not dead, but 
                // has somehow been removed from the map...
                int debug = 1;
            }

            return result;
        } else {
            // Female is Dead
            return null;
        }
//        if (result == null) {
//            GENESIS_AgentCollectionManager a_AgentCollectionManager = getAgentCollectionManager();
//            File a_AgentDirectory =
//                    a_AgentCollectionManager.getFemaleCollectionDirectory(
//                    a_Agent_ID,Type);
//            if (a_AgentDirectory.exists()) {
//                File[] agentFiles = a_AgentDirectory.listFiles();
//                if (agentFiles.length > 0) {
//                    result = (GENESIS_Female) Generic_IO.readObject(agentFiles[0]);
//                    result.init(
//                            ge,
//                            a_Agent_ID, this.getAgentCollection_ID());
//                }
//            }
//        }
//        return result;
//        //} else {
//            // Female is Dead
//
//        //}
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
        if (!(o instanceof GENESIS_FemaleCollection)) {
            return 1;
        }
        GENESIS_FemaleCollection oGENESIS_FemaleCollection = (GENESIS_FemaleCollection) o;
        if (oGENESIS_FemaleCollection.AgentCollectionID < this.AgentCollectionID) {
            return 1;
        } else {
            if (oGENESIS_FemaleCollection.AgentCollectionID > this.AgentCollectionID) {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GENESIS_FemaleCollection)) {
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
