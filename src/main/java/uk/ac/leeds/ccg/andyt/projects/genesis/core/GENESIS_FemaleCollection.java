package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;

/**
 * A class for storing GENESIS_Female instances.
 */
public class GENESIS_FemaleCollection
        extends GENESIS_AgentCollection
        implements Serializable, Comparable {

    static final long serialVersionUID = 1L;

    public GENESIS_FemaleCollection() {
    }

    public GENESIS_FemaleCollection(
            GENESIS_Environment a_GENESIS_Environment,
            long _AgentCollection_ID,
            String _Type) {
        this.ge = a_GENESIS_Environment;
        this._AgentCollection_ID = _AgentCollection_ID;
        this._Type = _Type;
        this._GENESIS_AgentCollectionManager =
                ge._GENESIS_AgentEnvironment._AgentCollectionManager;
        init_AgentID_Agent_HashMap();
    }

    public final void init(
            GENESIS_Environment a_GENESIS_Environment,
            long a_AgentCollection_ID,
            String a_Type) {
        this.ge = a_GENESIS_Environment;
        this._AgentCollection_ID = a_AgentCollection_ID;
        this._Type = a_Type;
        if (a_Type.equalsIgnoreCase(GENESIS_Person.getTypeLivingFemale_String())) {
            this._GENESIS_AgentCollectionManager =
                    ge._GENESIS_AgentEnvironment._AgentCollectionManager;
            this._GENESIS_AgentCollectionManager._GENESIS_Environment = a_GENESIS_Environment;
            this._GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap.put(
                    _AgentCollection_ID,
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
        GENESIS_AgentCollectionManager theGENESIS_AgentCollectionManager = get_AgentCollectionManager();
        long agentID = getAgentCollection_ID();
        if (_Type.equalsIgnoreCase(GENESIS_Person.getTypeLivingFemale_String())) {
            Long indexOfLastBornFemale = theGENESIS_AgentCollectionManager._IndexOfLastBornFemale;
            Long indexOfLastBornFemaleCollection = theGENESIS_AgentCollectionManager.getAgentCollection_ID(
                    indexOfLastBornFemale);
//            Long indexOfLastBornFemaleCollection = theGENESIS_AgentCollectionManager.getFemaleCollection_ID(
//                    indexOfLastBornFemale, _Type, false);
//            Long indexOfLastBornFemaleCollection = theGENESIS_AgentCollectionManager._IndexOfLastLivingFemaleCollection;
//            Long indexOfLastBornFemaleCollection =
//                    get_AgentCollectionManager().getFemaleCollection_ID(
//                    get_AgentCollectionManager()._IndexOfLastBornFemale,
//                    _Type,
//                    _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
            _Directory = new File(
                    Generic_StaticIO.getObjectDirectory(
                    theGENESIS_AgentCollectionManager._LivingFemaleDirectory,
                    agentID,
                    indexOfLastBornFemaleCollection,
                    theGENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory),
                    "" + agentID);
            File result = new File(this._Directory.toString());
            return result;
        } else {
            Long highestLeaf = Generic_StaticIO.getArchiveHighestLeaf(
                    get_AgentCollectionManager().getDeadFemaleDirectory(), "_");
            _Directory = new File(
                    Generic_StaticIO.getObjectDirectory(
                    theGENESIS_AgentCollectionManager.getDeadFemaleDirectory(),
                    agentID,
                    highestLeaf,
                    theGENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory),
                    "" + getAgentCollection_ID());
            File result = new File(this._Directory.toString());
            return result;
        }
    }

    public GENESIS_Female getFemale(
            Long a_Agent_ID,
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_Female result = getFemale(a_Agent_ID);
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                    get_AgentCollectionManager();
            Long a_FemaleCollection_ID =
                    a_GENESIS_AgentCollectionManager.getFemaleCollection_ID(
                    a_Agent_ID,
                    _Type,
                    handleOutOfMemoryError);
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection =
                    a_GENESIS_AgentCollectionManager.getFemaleCollection(
                    a_FemaleCollection_ID,
                    _Type);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                        get_AgentCollectionManager();
                Long a_FemaleCollection_ID =
                        a_GENESIS_AgentCollectionManager.getFemaleCollection_ID(
                        a_Agent_ID,
                        _Type,
                        ge.HandleOutOfMemoryErrorFalse);
                GENESIS_FemaleCollection a_GENESIS_FemaleCollection =
                        a_GENESIS_AgentCollectionManager.getFemaleCollection(
                        a_FemaleCollection_ID,
                        _Type);
                ge.swapToFile_DataAnyExcept(a_GENESIS_FemaleCollection);
                ge.init_MemoryReserve(
                        this, ge.HandleOutOfMemoryErrorFalse);
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
     * _Agent_ID_Agent_HashMap = get_Agent_ID_Agent_HashMap();
     * if (_Agent_ID_Agent_HashMap.containsKey(a_Agent_ID)){
     * GENESIS_Female result = (GENESIS_Female) _Agent_ID_Agent_HashMap.get(a_Agent_ID);
     * } else {
     * return null;
     * }
     * </code>
     *
     * @param a_Agent_ID
     * @return GENESIS_Female if it exists in _Agent_ID_Agent_HashMap and null
     * otherwise. If null is returned, the GENESIS_Female is probably Dead and
     * if so can be retrieved from a collection of the dead.
     */
    //protected GENESIS_Female getFemale(
    public GENESIS_Female getFemale(
            Long a_Agent_ID) {
        _Agent_ID_Agent_HashMap = get_Agent_ID_Agent_HashMap();
        if (_Agent_ID_Agent_HashMap.containsKey(a_Agent_ID)) {
            // Female is Living
            GENESIS_Female result = (GENESIS_Female) _Agent_ID_Agent_HashMap.get(a_Agent_ID);
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
//            GENESIS_AgentCollectionManager a_AgentCollectionManager = get_AgentCollectionManager();
//            File a_Agent_Directory =
//                    a_AgentCollectionManager.getFemaleCollectionDirectory(
//                    a_Agent_ID,_Type);
//            if (a_Agent_Directory.exists()) {
//                File[] agentFiles = a_Agent_Directory.listFiles();
//                if (agentFiles.length > 0) {
//                    result = (GENESIS_Female) Generic_StaticIO.readObject(agentFiles[0]);
//                    result.init(
//                            _GENESIS_Environment,
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
                ge.clear_MemoryReserve();
                ge.swapToFile_DataAnyExcept(this);
                ge.init_MemoryReserve(
                        this,
                        ge.HandleOutOfMemoryErrorFalse);
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
        if (oGENESIS_FemaleCollection._AgentCollection_ID < this._AgentCollection_ID) {
            return 1;
        } else {
            if (oGENESIS_FemaleCollection._AgentCollection_ID > this._AgentCollection_ID) {
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
        hash = 11 * hash + (int) (this._AgentCollection_ID ^ (this._AgentCollection_ID >>> 32));
        return hash;
    }
}
