/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;

/**
 * Each Agent has a reference to at least one AgentCollection. This class
 * contains methods for creating and deleting the file store/cache. Contains
 * methods and collections for managing access to Agents with this store.
 */
public abstract class GENESIS_AgentCollection implements Serializable {

    static final long serialVersionUID = 1L;
    public transient GENESIS_Environment _GENESIS_Environment;
    /**
     * The File which is used to swap data.
     */
    protected File _Directory;
    protected long _AgentCollection_ID;
    protected transient GENESIS_AgentCollectionManager _GENESIS_AgentCollectionManager;
    /**
     * For recording the type (getClass().getName())of Agents
     * _AgentID_Agent_HashMap contains.
     */
    protected String _Type;
    /**
     * keys are long _AgentID, values are Agents.
     */
    protected HashMap<Long, GENESIS_Agent> _Agent_ID_Agent_HashMap;

    public HashMap<Long, GENESIS_Agent> get_Agent_ID_Agent_HashMap() {
        if (_Agent_ID_Agent_HashMap == null) {
            _Agent_ID_Agent_HashMap = (HashMap<Long, GENESIS_Agent>) Generic_StaticIO.readObject(
                    get_File());
        }
        return _Agent_ID_Agent_HashMap;
    }

    public boolean isInMemory() {
        if (_Agent_ID_Agent_HashMap == null) {
            return false;
        }
        return true;
    }

    public File get_AgentCollectionManager_Directory() {
        return get_AgentCollectionManager().getDirectory();
    }

    public GENESIS_AgentCollectionManager get_AgentCollectionManager() {
        if (_GENESIS_AgentCollectionManager == null) {
            _GENESIS_AgentCollectionManager =
                    _GENESIS_Environment._GENESIS_AgentEnvironment._AgentCollectionManager;
        }
        if (_GENESIS_AgentCollectionManager._GENESIS_Environment == null) {
            _GENESIS_AgentCollectionManager._GENESIS_Environment = _GENESIS_Environment;
        }
        return _GENESIS_AgentCollectionManager;
    }

    protected String get_Type() {
        return _Type;
    }

    public abstract File getDirectory();

    protected File get_File() {
        _Directory = getDirectory();
        return new File(
                _Directory,
                //this._AgentCollection_ID + "_" + this.getClass().getName() + ".thisFile");
                this.getClass().getName() + get_Type() + ".thisFile");
    }

    protected void init_AgentID_Agent_HashMap() {
        _Agent_ID_Agent_HashMap = new HashMap<Long, GENESIS_Agent>();
    }

    /**
     * <code>
     * _Agent_ID_Agent_HashMap.remove(a_AgentID);
     * if (_Agent_ID_Agent_HashMap.isEmpty()){
     * return true;
     * } else {
     * return false;
     * }
     * </code>
     *
     * @param a_AgentID
     * @return true iff after removing entry _Agent_ID_Agent_HashMap is empty
     */
    public boolean remove(Long a_AgentID) {
        _Agent_ID_Agent_HashMap.remove(a_AgentID);
        if (_Agent_ID_Agent_HashMap.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

//    public abstract GENESIS_Agent getAgent(
//            Long a_Agent_ID,
//            boolean handleOutOfMemoryError);
//
//    protected GENESIS_Agent getAgent(
//            Long a_Agent_ID){
//        _Agent_ID_Agent_HashMap = get_Agent_ID_Agent_HashMap();
//        GENESIS_Agent result = _Agent_ID_Agent_HashMap.get(a_Agent_ID);
//        if (result == null) {
//            // Try loading from Agent Directory
//            File a_Agent_Directory = result.getDirectory(
//                    a_Agent_ID,
//                    get_AgentCollectionManager());
//            if (a_Agent_Directory.exists()) {
//                File[] agentFiles = a_Agent_Directory.listFiles();
//                if (agentFiles.length > 0) {
//                    result = (GENESIS_Agent) Generic_StaticIO.readObject(agentFiles[0]);
//                    result.init(
//                            _GENESIS_Environment,
//                            a_Agent_ID,
//                            this._AgentCollection_ID);
//                }
//            }
//        }
//        return result;
//    }
    public abstract void write(boolean handleOutOfMemoryError);

    //protected abstract void write();
    protected void write() {
        File file = get_File();
        File parent_File = file.getParentFile();
        if (!parent_File.exists()) {
            parent_File.mkdirs();
        }
        Generic_StaticIO.writeObject(
                this,
                file);
    }

    protected void swapToFileAgents() {
        _Agent_ID_Agent_HashMap = get_Agent_ID_Agent_HashMap();
        Iterator<Long> _Agent_ID_Agent_HashMapKeySetIterator = _Agent_ID_Agent_HashMap.keySet().iterator();
        while (_Agent_ID_Agent_HashMapKeySetIterator.hasNext()) {
            Long a_Agent_ID = _Agent_ID_Agent_HashMapKeySetIterator.next();
            GENESIS_Agent a_Agent = _Agent_ID_Agent_HashMap.get(a_Agent_ID);
            a_Agent.write();
            //_Agent_ID_Agent_HashMap.remove(a_Agent_ID);
            // Set a_Agent to null to free resources
            a_Agent = null;
        }
        //_Agent_ID_Agent_HashMap.clear();
    }

    protected void swapToFileAgent(Long a_Agent_ID) {
        _Agent_ID_Agent_HashMap = get_Agent_ID_Agent_HashMap();
        GENESIS_Agent a_Agent = _Agent_ID_Agent_HashMap.get(a_Agent_ID);
        a_Agent.write();
        //_Agent_ID_Agent_HashMap.remove(a_Agent_ID);
        // Set a_Agent to null to free resources
        a_Agent = null;
    }

    protected void swapToFileAgentExcept(Long a_Agent_ID) {
        _Agent_ID_Agent_HashMap = get_Agent_ID_Agent_HashMap();
        Iterator<Long> _Agent_ID_Agent_HashMapKeySetIterator = _Agent_ID_Agent_HashMap.keySet().iterator();
//        HashMap<Long,Agent> agentsToRemove
        while (_Agent_ID_Agent_HashMapKeySetIterator.hasNext()) {
            long b_Agent_ID = _Agent_ID_Agent_HashMapKeySetIterator.next();
            if (a_Agent_ID != b_Agent_ID) {
                GENESIS_Agent a_Agent = _Agent_ID_Agent_HashMap.get(a_Agent_ID);
                a_Agent.write();
                // Might need to break here and dispose outside iterator to 
                // avoid concurrent modification. Other option is to catch and 
                // ignore it...
                //_Agent_ID_Agent_HashMap.remove(a_Agent_ID);
                // Set a_Agent to null to free resources
                a_Agent = null;
                return;
            }
        }
    }

    protected void swapToFileAgentsExcept(Long a_Agent_ID) {
        _Agent_ID_Agent_HashMap = get_Agent_ID_Agent_HashMap();
        Iterator<Long> _Agent_ID_Agent_HashMapKeySetIterator = _Agent_ID_Agent_HashMap.keySet().iterator();
        while (_Agent_ID_Agent_HashMapKeySetIterator.hasNext()) {
            long b_Agent_ID = _Agent_ID_Agent_HashMapKeySetIterator.next();
            if (a_Agent_ID != b_Agent_ID) {
                GENESIS_Agent a_Agent = _Agent_ID_Agent_HashMap.get(a_Agent_ID);
                a_Agent.write();
                //_Agent_ID_Agent_HashMap.remove(a_Agent_ID);
                // Set a_Agent to null to free resources
                a_Agent = null;
            }
        }
    }

    /**
     * @return the _AgentCollection_ID
     */
    public long getAgentCollection_ID() {
        return _AgentCollection_ID;
    }

    public Long getMinAgentID() {
        return _AgentCollection_ID * _GENESIS_AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection;
    }

    /**
     * @param minAgentID
     * @return The maximum AgentID this collection will store
     */
    public Long getMaxAgentID(Long minAgentID) {
        return minAgentID + _GENESIS_AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection - 1L;
    }

    /**
     * This would be a lot easier if the data were stored in a TreeMap rather
     * than a HashMap!
     *
     * @return The maximum AgentID stored in this collection (which may not be
     * full).
     */
    public Long getMaxAgentID() {
        Long result = Long.MIN_VALUE;
        Iterator<Long> ite = _Agent_ID_Agent_HashMap.keySet().iterator();
        while (ite.hasNext()) {
            result = Math.max(result, ite.next());
        }
        return result;
    }

    public HashSet getPossibleAgentIDs_HashSet() {
        Long minAgentID = getMinAgentID();
        Long maxAgentID = getMaxAgentID(minAgentID);
        //Long agentID = new Long(minAgentID);
        HashSet result = new HashSet(
                _GENESIS_AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection);
        for (Long agentID = minAgentID; agentID <= maxAgentID; agentID++) {
            result.add(agentID);
        }
        return result;
    }
}
