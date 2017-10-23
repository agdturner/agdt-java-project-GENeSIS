package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.io.Serializable;

/**
 * To create and Agent first an AgentCollection is needed. A reference to this
 * AgentCollection is set in the Agent. Agents may be stored on disk in a File
 * store. The File can be found from a numerical identifier _Agent_ID. So if the
 * Agent does not exist in memory it can be loaded from a File.
 */
public abstract class GENESIS_Agent extends GENESIS_Object implements Serializable {

    public transient GENESIS_AgentCollectionManager AgentCollectionManager;

    /**
     * For storing the type of GENESIS_Agent
     */
    protected String Type;
    
    GENESIS_Agent(){}
    
    GENESIS_Agent(GENESIS_Environment ge) {
        super(ge);
    }

    protected abstract GENESIS_AgentCollection get_AgentCollection();

    protected GENESIS_AgentCollectionManager getAgentCollectionManager() {
        if (AgentCollectionManager == null) {
            AgentCollectionManager = ge.AgentEnvironment.AgentCollectionManager;
        }
        if (AgentCollectionManager.ge == null) {
            AgentCollectionManager.ge = ge;
        }
        return AgentCollectionManager;
    }

    public abstract Long getAgentID(boolean handleOutOfMemoryError);

    protected abstract File getDirectory();

    public abstract void write(boolean handleOutOfMemoryError);

    protected abstract void write();

    /**
     * @return the _Type
     */
    public String getType() {
        return Type;
    }

    /**
     * @param Type the _Type to set
     */
    public void setType(String Type) {
        this.Type = Type;
    }
}
