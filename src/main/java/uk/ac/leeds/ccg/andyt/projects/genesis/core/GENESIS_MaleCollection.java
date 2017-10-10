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

    static final long serialVersionUID = 1L;

    public GENESIS_MaleCollection() {
    }

    public GENESIS_MaleCollection(
            GENESIS_Environment a_GENESIS_Environment,
            Long _AgentCollection_ID,
            String _Type) {
        this._GENESIS_Environment = a_GENESIS_Environment;
        this._AgentCollection_ID = _AgentCollection_ID;
        this._Type = _Type;
        this._GENESIS_AgentCollectionManager =
                _GENESIS_Environment._GENESIS_AgentEnvironment._AgentCollectionManager;
        init_AgentID_Agent_HashMap();
    }

    public final void init(
            GENESIS_Environment a_GENESIS_Environment,
            long a_AgentCollection_ID,
            String a_Type) {
        this._GENESIS_Environment = a_GENESIS_Environment;
        this._AgentCollection_ID = a_AgentCollection_ID;
        _Type = a_Type;
        if (a_Type.equalsIgnoreCase(GENESIS_Person.getTypeLivingMale_String())) {
            this._GENESIS_AgentCollectionManager =
                    _GENESIS_Environment._GENESIS_AgentEnvironment._AgentCollectionManager;
            this._GENESIS_AgentCollectionManager._GENESIS_Environment = a_GENESIS_Environment;
            this._GENESIS_AgentCollectionManager._LivingMaleCollection_HashMap.put(
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
        // Recalculation is done as archive may have grown and _Directory might
        // be out of date. There is scope for optimisation here...
        GENESIS_AgentCollectionManager theGENESIS_AgentCollectionManager = get_AgentCollectionManager();
        long agentID = getAgentCollection_ID();
        if (_Type.equalsIgnoreCase(GENESIS_Person.getTypeLivingMale_String())) {
            Long indexOfLastBornMale = theGENESIS_AgentCollectionManager._IndexOfLastBornMale;
            Long indexOfLastBornMaleCollection = theGENESIS_AgentCollectionManager.getAgentCollection_ID(
                    indexOfLastBornMale);
//            Long indexOfLastBornMaleCollection = theGENESIS_AgentCollectionManager.getMaleCollection_ID(
//                    indexOfLastBornMale, _Type, false);
//            Long indexOfLastBornMaleCollection = theGENESIS_AgentCollectionManager._IndexOfLastLivingMaleCollection;
//            Long indexOfLastBornMaleCollection =
//                    get_AgentCollectionManager().getMaleCollection_ID(
//                    get_AgentCollectionManager()._IndexOfLastBornMale,
//                    _Type,
//                    _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
            _Directory = new File(
                    Generic_StaticIO.getObjectDirectory(
                    theGENESIS_AgentCollectionManager._LivingMaleDirectory,
                    agentID,
                    indexOfLastBornMaleCollection,
                    theGENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory),
                    "" + agentID);
            File result = new File(this._Directory.toString());
            return result;
        } else {
            Long highestLeaf = Generic_StaticIO.getArchiveHighestLeaf(
                    get_AgentCollectionManager().getDeadMaleDirectory(), "_");
            _Directory = new File(
                    Generic_StaticIO.getObjectDirectory(
                    theGENESIS_AgentCollectionManager.getDeadMaleDirectory(),
                    agentID,
                    highestLeaf,
                    theGENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory),
                    "" + getAgentCollection_ID());
            File result = new File(this._Directory.toString());
            return result;
        }
    }

    public GENESIS_Male getMale(
            Long a_Agent_ID,
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_Male result = getMale(a_Agent_ID);
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                    get_AgentCollectionManager();
            Long a_MaleCollection_ID =
                    a_GENESIS_AgentCollectionManager.getMaleCollection_ID(
                    a_Agent_ID,
                    _Type,
                    handleOutOfMemoryError);
            GENESIS_MaleCollection a_GENESIS_MaleCollection =
                    a_GENESIS_AgentCollectionManager.getMaleCollection(
                    a_MaleCollection_ID,
                    _Type);
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _GENESIS_Environment.clear_MemoryReserve();
                GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                        get_AgentCollectionManager();
                Long a_MaleCollection_ID =
                        a_GENESIS_AgentCollectionManager.getMaleCollection_ID(
                        a_Agent_ID,
                        _Type,
                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                GENESIS_MaleCollection a_GENESIS_MaleCollection =
                        a_GENESIS_AgentCollectionManager.getMaleCollection(
                        a_MaleCollection_ID,
                        _Type);
                _GENESIS_Environment.swapToFile_DataAnyExcept(a_GENESIS_MaleCollection);
                _GENESIS_Environment.init_MemoryReserve(
                        a_GENESIS_MaleCollection,
                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
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
        _Agent_ID_Agent_HashMap = get_Agent_ID_Agent_HashMap();
        if (_Agent_ID_Agent_HashMap.containsKey(a_Agent_ID)) {
            GENESIS_Male result = (GENESIS_Male) _Agent_ID_Agent_HashMap.get(a_Agent_ID);
            result._GENESIS_Environment = _GENESIS_Environment;

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
//        GENESIS_Male result = (GENESIS_Male) _Agent_ID_Agent_HashMap.get(a_Agent_ID);
//        if (result == null) {
//            // Try loading from Agent Directory
//            GENESIS_AgentCollectionManager a_AgentCollectionManager = get_AgentCollectionManager();
//            File a_Agent_Directory = a_AgentCollectionManager.getMaleCollectionDirectory(a_Agent_ID, _Type);
//            if (a_Agent_Directory.exists()) {
//                File[] agentFiles = a_Agent_Directory.listFiles();
//                if (agentFiles.length > 0) {
//                    result = (GENESIS_Male) Generic_StaticIO.readObject(agentFiles[0]);
//                    result.init(
//                            _GENESIS_Environment,
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
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    this,
                    handleOutOfMemoryError);
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _GENESIS_Environment.clear_MemoryReserve();
                _GENESIS_Environment.swapToFile_DataAnyExcept(this);
                _GENESIS_Environment.init_MemoryReserve(
                        this,
                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
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
        if (oGENESIS_MaleCollection._AgentCollection_ID < this._AgentCollection_ID) {
            return 1;
        } else {
            if (oGENESIS_MaleCollection._AgentCollection_ID > this._AgentCollection_ID) {
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
        hash = 11 * hash + (int) (this._AgentCollection_ID ^ (this._AgentCollection_ID >>> 32));
        return hash;
    }
}
