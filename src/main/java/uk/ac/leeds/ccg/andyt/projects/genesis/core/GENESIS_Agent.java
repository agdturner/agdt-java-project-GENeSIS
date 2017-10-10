package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.io.Serializable;

/**
 * To create and Agent first an AgentCollection is needed. A reference to this
 * AgentCollection is set in the Agent. Agents may be stored on disk in a File
 * store. The File can be found from a numerical identifier _Agent_ID. So if the
 * Agent does not exist in memory it can be loaded from a File.
 */
public abstract class GENESIS_Agent implements Serializable {

    static final long serialVersionUID = 1L;
    /**
     * A reference to the main AgentCollection that registers this.
     */
    //protected transient AgentCollection _GENESIS_AgentCollection;
    /**
     * A reference to the _GENESIS_Environment of this simulation. Declared
 transient so that Object can be swapped to file and reloaded without
 swapping _GENESIS_Environment. When reloading, care is needed to
 initialise this...
     */
    public transient GENESIS_Environment _GENESIS_Environment;
//    /**
//     * A reference to an AgentCollection that holds a reference to this.
//     * This might sensibly be made a collection if agents change so as to belong
//     * to more than one collection...
//     */
//    public transient GENESIS_AgentCollection _GENESIS_AgentCollection;
    public transient GENESIS_AgentCollectionManager _GENESIS_AgentCollectionManager;
    /**
     * For storing the type of GENESIS_Agent
     */
    protected String _Type;

//    /**
//     * This was trying to be clever and set a new _GENESIS_AgentCollection when needed,
//     * that is probably not sensible and some accounting should be done outside
//     * to test if this collection is full.
//     * @param _GENESIS_AgentEnvironment
//     * @param _Type
//     * @param _Agent_ID
//     * @param _AgentCollection_ID
//     */
//    protected final void init(
//            _GENESIS_Environment a_GENESIS_Environment,
//            long _Agent_ID,
//            long _AgentCollection_ID) {
////            AgentCollection _GENESIS_AgentCollection){
//        this._GENESIS_Environment = a_GENESIS_Environment;
//        this._Agent_ID = _Agent_ID;
//        this._AgentCollection_ID = _AgentCollection_ID;
//        //this._GENESIS_AgentCollection = get_AgentCollection();
//        this._Directory = get_Directory();
//        if (!this._Directory.mkdir()) {
//            this._Directory.mkdirs();
//        }
//        //this._GENESIS_AgentCollection.get_Agent_ID_Agent_HashMap().put(_Agent_ID, this);
//    }
//
//    protected void init(
//            GENESIS_Agent a_Agent,
//            Long a_Agent_ID) {
//        this._GENESIS_Environment = a_Agent._GENESIS_Environment;
//        this._Agent_ID = a_Agent_ID;
//        this._AgentCollection_ID =
//                a_Agent._GENESIS_Environment._GENESIS_AgentEnvironment._AgentCollectionManager.getAgentCollection_ID(
//                a_Agent_ID);
//        this._GENESIS_AgentCollection = get_AgentCollection();
//        this._Directory = get_Directory();
//        if (!this._Directory.mkdir()) {
//            this._Directory.mkdirs();
//        }
//        this._GENESIS_AgentCollection.get_Agent_ID_Agent_HashMap().put(_Agent_ID, this);
//    }
//    public GENESIS_AgentCollection get_AgentCollection(
//            boolean handleOutOfMemoryError) {
//        try {
//            GENESIS_AgentCollection result = get_AgentCollection();
//            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                    result,
//                    handleOutOfMemoryError);
//            return result;
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                _GENESIS_Environment.clear_MemoryReserve();
//                //_GENESIS_Environment.swapToFile_DataAny();
//                if (_GENESIS_Environment._GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollection_Account() < 1) {
//                    _GENESIS_Environment.swapToFile_Grid2DSquareCellChunk();
//                }
//                _GENESIS_Environment.init_MemoryReserve(
//                        handleOutOfMemoryError);
//                return get_AgentCollection(handleOutOfMemoryError);
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
    /**
     * @return
     * this._GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.getAgentCollection(_AgentCollection_ID)
     */
    protected abstract GENESIS_AgentCollection get_AgentCollection();

    protected GENESIS_AgentCollectionManager get_AgentCollectionManager() {
        if (_GENESIS_AgentCollectionManager == null) {
            _GENESIS_AgentCollectionManager = _GENESIS_Environment._GENESIS_AgentEnvironment._AgentCollectionManager;
        }
        if (_GENESIS_AgentCollectionManager._GENESIS_Environment == null) {
            _GENESIS_AgentCollectionManager._GENESIS_Environment = _GENESIS_Environment;
        }
        return _GENESIS_AgentCollectionManager;
    }

//    public void init(
//            Environment _GENESIS_AgentEnvironment,
//            AgentCollection _GENESIS_AgentCollection){
//        this._GENESIS_AgentEnvironment = _GENESIS_AgentEnvironment;
//        this._GENESIS_AgentEnvironment._AgentCollection_HashMap = _GENESIS_AgentEnvironment._AgentCollection_HashMap;
//        this._GENESIS_AgentCollection = _GENESIS_AgentCollection;
//        this._AgentCollection_ID = _GENESIS_AgentCollection._AgentCollection_ID;
//        this._ID = _GENESIS_AgentCollection.getNextAgentID();
//        if ( this._ID >= _GENESIS_AgentCollection._MaximumNumberOfAgents){
//            // This is a hack
//            // What is wanted is a manager to get the next AgentCollection ID from
//            // Each Agent Collection should probably be typed.
//            this._AgentCollection_ID ++;
//            this._ID = 0L;
//            AgentCollection b_AgentCollection = new AgentCollection(
//                    _GENESIS_AgentCollection._Registry_Directory,
//                    this._AgentCollection_ID,
//                    this._ID,
//                    _GENESIS_AgentCollection._MaximumNumberOfAgents);
//            this._GENESIS_AgentEnvironment._AgentCollection_HashMap.put(
//                    _AgentCollection_ID, b_AgentCollection);
//                    this._GENESIS_AgentCollection = b_AgentCollection;
//        }
//        this._Directory = this._GENESIS_AgentCollection.getDirectory(
//                this._GENESIS_AgentCollection.get_Registry_Directory(),
//                this._ID);
//        if (!this._Directory.mkdir()){
//            this._Directory.mkdirs();
//        }
//        this._GENESIS_AgentCollection.get_Registry_HashMap().put(_ID,_Directory);
//    }
//    public void init(
//            Environment _GENESIS_AgentEnvironment,
//            long ID,
//            long _AgentCollection_ID){
//        this._GENESIS_AgentEnvironment = _GENESIS_AgentEnvironment;
//        this._ID = ID;
//        this._AgentCollection_ID = _AgentCollection_ID;
//        if (_GENESIS_AgentEnvironment._AgentCollection_HashMap == null){
//            _GENESIS_AgentEnvironment.init_AgentCollection_HashMap();
//        }
//        this._Directory = this._GENESIS_AgentEnvironment._GENESIS_AgentCollection.getDirectory(
//                this._GENESIS_AgentEnvironment._GENESIS_AgentCollection.getRegistry_Directory(),
//                _Agent_ID);
//        if (!this._Directory.mkdir()){
//            this._Directory.mkdirs();
//        }
//        getAgentCollection().getRegistry_HashMap().put(_ID,_Directory);
//    }
    public abstract Long get_Agent_ID(boolean handleOutOfMemoryError);

//    /**
//     * @return A copy of this._Agent_ID
//     */
//    protected Long get_Agent_ID() {
//        return _Agent_ID;
//    }
    protected abstract File get_Directory();

//     public abstract File get_Directory(
//            long a_Agent_ID,
//            GENESIS_AgentCollectionManager a_AgentCollectionManager);
//    public AgentCollection getAgentCollection() {
////        if (_AgentStore == null){
////            loadAgentStore();
////        }
//        return _GENESIS_AgentEnvironment._GENESIS_AgentCollection;
//    }
//    /**
//     * For storing the state of this. If it is null, it can be pulled from a
//     * File in _Directory via loadState_HashMap().
//     */
//    protected HashMap _State_HashMap;
//
//    protected void initState_HashMap() {
//        this._State_HashMap = new HashMap();
//    }
//
//    public HashMap getState_HashMap() {
//        if (_State_HashMap == null) {
//            readState_HashMap();
//        }
//        return _State_HashMap;
//    }
//
//    protected void readState_HashMap() {
//        File _State_HashMap_File = new File(
//                getDirectory(),
//                "_State_HashMap.thisFile");
//        if (!_State_HashMap_File.exists()) {
//            initState_HashMap();
//        } else {
//            _State_HashMap = (HashMap) Generic_StaticIO.readObject(
//                    _State_HashMap_File);
//        }
//    }
//
//    protected void writeState_HashMap() {
//        File _State_HashMap_File = new File(
//                getDirectory(),
//                "_State_HashMap.thisFile");
////            if (_State_HashMap_File.exists()) {
////                //Overwriting
////            }
//        Generic_StaticIO.writeObject(
//                _State_HashMap,
//                _State_HashMap_File);
//    }
//
//    public void putState(Object key, Object value) {
//        getState_HashMap().put(key, value);
//    }
    public abstract void write(boolean handleOutOfMemoryError);

    protected abstract void write();

    /**
     * @return the _Type
     */
    public String getType() {
        return _Type;
    }

    /**
     * @param Type the _Type to set
     */
    public void setType(String Type) {
        this._Type = Type;
    }
}
