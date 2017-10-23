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
public abstract class GENESIS_AgentCollection extends GENESIS_Object implements Serializable {

    protected File Directory;
    protected long AgentCollectionID;
    protected transient GENESIS_AgentCollectionManager AgentCollectionManager;
    
    /**
     * For recording the type (getClass().getName())of Agents
     * _AgentID_Agent_HashMap contains.
     */
    protected String Type;
    
    /**
     * keys are long _AgentID, values are Agents.
     */
    protected HashMap<Long, GENESIS_Agent> AgentID_Agent_Map;

    protected GENESIS_AgentCollection(){}
    
    public GENESIS_AgentCollection(GENESIS_Environment ge) {
        super(ge);
    }
    
    public HashMap<Long, GENESIS_Agent> getAgentID_Agent_Map() {
        if (AgentID_Agent_Map == null) {
            AgentID_Agent_Map = (HashMap<Long, GENESIS_Agent>) Generic_StaticIO.readObject(
                    get_File());
        }
        return AgentID_Agent_Map;
    }

    public boolean isInMemory() {
        return AgentID_Agent_Map != null;
    }

    public GENESIS_AgentCollectionManager getAgentCollectionManager() {
        if (AgentCollectionManager == null) {
            AgentCollectionManager = ge.AgentEnvironment.AgentCollectionManager;
        }
        if (AgentCollectionManager.ge == null) {
            AgentCollectionManager.ge = ge;
        }
        return AgentCollectionManager;
    }

    protected String getType() {
        return Type;
    }

    public abstract File getDirectory();

    protected File get_File() {
        Directory = getDirectory();
        return new File(
                Directory,
                //this.AgentCollectionID + "_" + this.getClass().getName() + ".thisFile");
                this.getClass().getName() + getType() + ".thisFile");
    }

    protected final void initAgentID_Agent_Map() {
        AgentID_Agent_Map = new HashMap<>();
    }

    public boolean remove(Long a_AgentID) {
        AgentID_Agent_Map.remove(a_AgentID);
        return AgentID_Agent_Map.isEmpty();
    }

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
        AgentID_Agent_Map = getAgentID_Agent_Map();
        Iterator<Long> _Agent_ID_Agent_HashMapKeySetIterator = AgentID_Agent_Map.keySet().iterator();
        while (_Agent_ID_Agent_HashMapKeySetIterator.hasNext()) {
            Long a_Agent_ID = _Agent_ID_Agent_HashMapKeySetIterator.next();
            GENESIS_Agent a_Agent = AgentID_Agent_Map.get(a_Agent_ID);
            a_Agent.write();
            //_Agent_ID_Agent_HashMap.remove(a_Agent_ID);
            // Set a_Agent to null to free resources
            a_Agent = null;
        }
        //_Agent_ID_Agent_HashMap.clear();
    }

    protected void swapToFileAgent(Long a_Agent_ID) {
        AgentID_Agent_Map = getAgentID_Agent_Map();
        GENESIS_Agent a_Agent = AgentID_Agent_Map.get(a_Agent_ID);
        a_Agent.write();
        //_Agent_ID_Agent_HashMap.remove(a_Agent_ID);
        // Set a_Agent to null to free resources
        a_Agent = null;
    }

    protected void swapToFileAgentExcept(Long a_Agent_ID) {
        AgentID_Agent_Map = getAgentID_Agent_Map();
        Iterator<Long> _Agent_ID_Agent_HashMapKeySetIterator = AgentID_Agent_Map.keySet().iterator();
//        HashMap<Long,Agent> agentsToRemove
        while (_Agent_ID_Agent_HashMapKeySetIterator.hasNext()) {
            long b_Agent_ID = _Agent_ID_Agent_HashMapKeySetIterator.next();
            if (a_Agent_ID != b_Agent_ID) {
                GENESIS_Agent a_Agent = AgentID_Agent_Map.get(a_Agent_ID);
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
        AgentID_Agent_Map = getAgentID_Agent_Map();
        Iterator<Long> _Agent_ID_Agent_HashMapKeySetIterator = AgentID_Agent_Map.keySet().iterator();
        while (_Agent_ID_Agent_HashMapKeySetIterator.hasNext()) {
            long b_Agent_ID = _Agent_ID_Agent_HashMapKeySetIterator.next();
            if (a_Agent_ID != b_Agent_ID) {
                GENESIS_Agent a_Agent = AgentID_Agent_Map.get(a_Agent_ID);
                a_Agent.write();
                //_Agent_ID_Agent_HashMap.remove(a_Agent_ID);
                // Set a_Agent to null to free resources
                a_Agent = null;
            }
        }
    }

    /**
     * @return the AgentCollectionID
     */
    public long getAgentCollectionID() {
        return AgentCollectionID;
    }

    public Long getMinAgentID() {
        return AgentCollectionID * AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection;
    }

    /**
     * @param minAgentID
     * @return The maximum AgentID this collection will store
     */
    public Long getMaxAgentID(Long minAgentID) {
        return minAgentID + AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection - 1L;
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
        Iterator<Long> ite = AgentID_Agent_Map.keySet().iterator();
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
                AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection);
        for (Long agentID = minAgentID; agentID <= maxAgentID; agentID++) {
            result.add(agentID);
        }
        return result;
    }
}
