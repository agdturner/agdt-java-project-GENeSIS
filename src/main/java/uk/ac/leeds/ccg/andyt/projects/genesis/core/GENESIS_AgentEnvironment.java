package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.memory.Generic_OutOfMemoryErrorHandler;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;

public class GENESIS_AgentEnvironment
        //extends GENESIS_OutOfMemoryErrorHandler
        extends Generic_OutOfMemoryErrorHandler
        implements Serializable, GENESIS_OutOfMemoryErrorHandlerInterface {

    public transient GENESIS_Environment _GENESIS_Environment;
    protected transient GENESIS_AgentCollectionManager _AgentCollectionManager;
    protected File _Directory;// Used?
    protected GENESIS_PersonFactory _PersonFactory;// Used?

    /**
     * Default constructor
     */
    public GENESIS_AgentEnvironment() {
    }

    /**
     * Creates a new GENESIS_AgentEnvironment based on
     * a_GENESIS_AgentEnvironment. An instance of GENESIS_AgentCollection holds
     * a reference to an instance of GENESIS_Environment which holds references
     * to all the data in a simulation. This does not deep copy everything.
     * a_GENESIS_AgentEnvironment holds a reference to an instance of
     * GENESIS_AgentCollectionManager which holds both a reference to the same
     * instance of GENESIS_Environment as
     * a_GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager. Full
     * instantiation would necessarily be a multi stage process that would use
     * dummies to get all the references set up. This method can be implemented
     * more comprehensively as needed, but the depth of the copy wanted is
     * unlikely to be to the root of everything.
     *
     * @param a_GENESIS_AgentCollectionManager The
     * GENESIS_AgentCollectionManager upon which the new instance is based.
     */
    /**
     * For creating a deep copy of toDeepCopy_GENESIS_AgentEnvironment This is
 not straight forward because of the linked references between
 toDeepCopy_GENESIS_AgentEnvironment._GENESIS_Environment and
 toDeepCopy_GENESIS_AgentEnvironment._GENESIS_Environment._GENESIS_AgentEnvironment
     *
     * @param a_GENESIS_AgentEnvironment
     */
    public GENESIS_AgentEnvironment(GENESIS_AgentEnvironment a_GENESIS_AgentEnvironment) {
        this(a_GENESIS_AgentEnvironment._GENESIS_Environment,
                a_GENESIS_AgentEnvironment);
        //throw new UnsupportedOperationException();
    }

    public GENESIS_AgentEnvironment(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_AgentEnvironment a_GENESIS_AgentEnvironment) {
        this._AgentCollectionManager = new GENESIS_AgentCollectionManager(
                a_GENESIS_Environment,
                a_GENESIS_AgentEnvironment._AgentCollectionManager);
//        this._Directory = new File(a_GENESIS_AgentEnvironment._Directory.toString());
        this._GENESIS_Environment = a_GENESIS_Environment;
//        //this._Generic_TestMemory no need to set this
//        this._HandleOutOfMemoryError_boolean = a_GENESIS_AgentEnvironment._HandleOutOfMemoryError_boolean;
//        this._PersonFactory = a_GENESIS_AgentEnvironment._PersonFactory;
        //throw new UnsupportedOperationException();
    }

    public GENESIS_AgentEnvironment(
            GENESIS_Environment a_GENESIS_Environment) {
        _AgentCollectionManager = new GENESIS_AgentCollectionManager(a_GENESIS_Environment);
        _GENESIS_Environment = a_GENESIS_Environment;
    }

    public GENESIS_AgentCollectionManager get_AgentCollectionManager(
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_AgentCollectionManager result = get_AgentCollectionManager();
            /*
             * To use:
             * tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
             * It's success needs to be assessed and appropriate action 
             * performed to prevent a loop.
             */
            tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (_AgentCollectionManager.swapToFile_AgentCollection_Account(_GENESIS_Environment.HandleOutOfMemoryErrorFalse) < 1) {
                    throw a_OutOfMemoryError;
                }
                init_MemoryReserve(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                return get_AgentCollectionManager(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected GENESIS_AgentCollectionManager get_AgentCollectionManager() {
        if (_AgentCollectionManager == null) {
            //_AgentCollectionManager = new GENESIS_AgentCollectionManager();
            _AgentCollectionManager = _GENESIS_Environment._GENESIS_AgentEnvironment._AgentCollectionManager;
            _AgentCollectionManager._GENESIS_Environment = _GENESIS_Environment;
        }
        return _AgentCollectionManager;
    }

    public void set_AgentCollectionManager(
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager,
            boolean handleOutOfMemoryError) {
        try {
            set_AgentCollectionManager(a_GENESIS_AgentCollectionManager);
            tryToEnsureThereIsEnoughMemoryToContinue();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (_AgentCollectionManager.swapToFile_AgentCollection_Account(_GENESIS_Environment.HandleOutOfMemoryErrorFalse) < 1) {
                    throw a_OutOfMemoryError;
                }
                init_MemoryReserve(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                set_AgentCollectionManager(
                        a_GENESIS_AgentCollectionManager,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected void set_AgentCollectionManager(
            GENESIS_AgentCollectionManager _AgentCollectionManager) {
        this._AgentCollectionManager = _AgentCollectionManager;
    }

    /**
     * Initialises _MemoryReserve.
     *
     */
    @Override
    public void init_MemoryReserve(
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            /*
             * To use:
             * tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
             * It's success needs to be assessed and appropriate action
             * performed to prevent a loop.
             */
            tryToEnsureThereIsEnoughMemoryToContinue();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                if (_AgentCollectionManager.swapToFile_AgentCollection_Account(_GENESIS_Environment.HandleOutOfMemoryErrorFalse) < 1) {
                    throw a_OutOfMemoryError;
                }
                init_MemoryReserve(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public long init_MemoryReserve_Account(
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = _AgentCollectionManager.swapToFile_AgentCollection_Account(
                        false);
                if (result < 1) {
                    throw a_OutOfMemoryError;
                }
                result += init_MemoryReserve_Account(
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    //public HashSet<Long> init_MemoryReserve_AccountDetail(
    public Object[] init_MemoryReserve_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                Object[] result =
                        _AgentCollectionManager.swapToFile_AgentCollection_AccountDetail(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
//                HashSet<Long> result = _AgentCollectionManager.swapToFile_AgentCollection_AccountDetail(
//                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                if ((Boolean) result[0]) {
                    throw a_OutOfMemoryError;
                }
                Object[] potentialPartResult = init_MemoryReserve_AccountDetail(
                        HandleOutOfMemoryErrorFalse);
                GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public long init_MemoryReserve_Account(
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = _AgentCollectionManager.swapToFile_MaleCollection_Account();
                if (result < 1) {
                    result = _AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(a_GENESIS_FemaleCollection,
                            _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                    if (result < 1) {
                        throw a_OutOfMemoryError;
                    }
                }
                result += init_MemoryReserve_Account(
                        a_GENESIS_FemaleCollection,
                        HandleOutOfMemoryErrorFalse);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public long init_MemoryReserve_Account(
            GENESIS_MaleCollection a_GENESIS_MaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = _AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(a_GENESIS_MaleCollection,
                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                if (result < 1) {
                    result = _AgentCollectionManager.swapToFile_FemaleCollection_Account();
                    if (result < 1) {
                        throw a_OutOfMemoryError;
                    }
                }
                result += init_MemoryReserve_Account(
                        a_GENESIS_MaleCollection,
                        HandleOutOfMemoryErrorFalse);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

//    public long init_MemoryReserve_Account(
//            GENESIS_AgentCollection a_GENESIS_AgentCollection,
//            boolean handleOutOfMemoryError) {
//        try {
//            init_MemoryReserve();
//            return tryToEnsureThereIsEnoughMemoryToContinue_Account(
//                    a_GENESIS_AgentCollection,
//                    handleOutOfMemoryError);
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                clear_MemoryReserve();
//                long result = _AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                        a_GENESIS_AgentCollection,
//                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
//                if (result < 1) {
//                    throw a_OutOfMemoryError;
//                }
//                result += init_MemoryReserve_Account(
//                        a_GENESIS_AgentCollection,
//                        HandleOutOfMemoryErrorFalse);
//                return result;
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
    public Object[] init_MemoryReserve_AccountDetail(
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                Object[] result = _AgentCollectionManager.swapToFile_MaleCollection_AccountDetail(
                        HandleOutOfMemoryErrorFalse);
                if (!(Boolean) result[0]) {
                    Object[] potentialPartResult = _AgentCollectionManager.swapToFile_FemaleCollectionExcept_AccountDetail(
                            a_GENESIS_FemaleCollection);
                    if (!(Boolean) result[0]) {
                        if (_GENESIS_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                            throw a_OutOfMemoryError;
                        }
                    } else {
                        GENESIS_AgentCollectionManager.combine(
                                result, potentialPartResult);
                    }
                }
                Object[] potentialPartResult = init_MemoryReserve_AccountDetail(
                        a_GENESIS_FemaleCollection,
                        HandleOutOfMemoryErrorFalse);
                GENESIS_AgentCollectionManager.combine(
                        result, potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public Object[] init_MemoryReserve_AccountDetail(
            GENESIS_MaleCollection a_GENESIS_MaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                Object[] result = _AgentCollectionManager.swapToFile_MaleCollectionExcept_AccountDetail(
                        a_GENESIS_MaleCollection);
                if (!(Boolean) result[0]) {
                    Object[] potentialPartResult =
                            _AgentCollectionManager.swapToFile_FemaleCollection_AccountDetail(
                            HandleOutOfMemoryErrorFalse);
                    if (!(Boolean) result[0]) {
                        if (_GENESIS_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                            throw a_OutOfMemoryError;
                        }
                    } else {
                        GENESIS_AgentCollectionManager.combine(
                                result, potentialPartResult);
                    }
                }
                Object[] potentialPartResult = init_MemoryReserve_AccountDetail(
                        a_GENESIS_MaleCollection,
                        HandleOutOfMemoryErrorFalse);
                GENESIS_AgentCollectionManager.combine(
                        result, potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

//    public HashSet<Long> init_MemoryReserve_AccountDetail(
//            GENESIS_AgentCollection a_GENESIS_AgentCollection,
//            boolean handleOutOfMemoryError) {
//        try {
//            init_MemoryReserve();
//            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
//                    a_GENESIS_AgentCollection,
//                    handleOutOfMemoryError);
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                clear_MemoryReserve();
//                HashSet<Long> result = _AgentCollectionManager.swapToFile_AgentCollectionExcept_AccountDetail(
//                        a_GENESIS_AgentCollection);
//                if (result.isEmpty()) {
//                    throw a_OutOfMemoryError;
//                }
//                HashSet<Long> potentialPartResult = init_MemoryReserve_AccountDetail(
//                        a_GENESIS_AgentCollection,
//                        HandleOutOfMemoryErrorFalse);
//                if (!potentialPartResult.isEmpty()) {
//                    result.addAll(potentialPartResult);
//                }
//                return result;
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
//    public final void init_MemoryReserve(
//            long a_AgentCollection_ID,
//            boolean handleOutOfMemoryError) {
//        try {
//            init_MemoryReserve();
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                clear_MemoryReserve();
//                try {
//                    boolean createdRoom = false;
//                    GENESIS_AgentCollection a_GENESIS_AgentCollection = _AgentCollectionManager.getAgentCollection(
//                            a_AgentCollection_ID,
//                            HandleOutOfMemoryErrorFalse);
//                    while (!createdRoom) {
//                        try {
//                            if (_AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                                    a_GENESIS_AgentCollection,
//                                    false) < 1) {
//                                throw a_OutOfMemoryError;
//                            }
//                            init_MemoryReserve(
//                                    a_GENESIS_AgentCollection,
//                                    HandleOutOfMemoryErrorFalse);
//                            tryToEnsureThereIsEnoughMemoryToContinue(
//                                    a_GENESIS_AgentCollection,
//                                    handleOutOfMemoryError);
//                            createdRoom = true;
//                        } catch (OutOfMemoryError c_OutOfMemoryError) {
//                            log("Creating room in " + this.getClass().getName() + ".init_MemoryReserve(long,boolean)");
//                        }
//                    }
//                } catch (OutOfMemoryError b_OutOfMemoryError) {
//                    b_OutOfMemoryError.printStackTrace();
//                    System.exit(GENESIS_ErrorAndExceptionHandler.OutOfMemoryErrorExitStatus);
//                    throw b_OutOfMemoryError;
//                }
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
    /**
     * A method to try to ensure there is enough memory to continue.
     *
     * @param handleOutOfMemoryError
     * @return 
     */
    @Override
    public boolean tryToEnsureThereIsEnoughMemoryToContinue(
            boolean handleOutOfMemoryError) {
        try {
            if (tryToEnsureThereIsEnoughMemoryToContinue()) {
                return true;
            } else {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(boolean)";
                log(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError(message);
                throw new OutOfMemoryError();
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _GENESIS_Environment.clear_MemoryReserve();
                long account;
                boolean createdRoom = false;
                while (!createdRoom) {
                    try {
                        account = _AgentCollectionManager.swapToFile_AgentCollection_Account();
                        if (account < 1) {
                            // handleOutOfMemoryError changed to avoid problematic 
                            // recursion and allow error to be thrown from method
                            handleOutOfMemoryError = false;
                            throw a_OutOfMemoryError;
                        }
                        _GENESIS_Environment.init_MemoryReserve(
                                HandleOutOfMemoryErrorFalse);
                        tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = "Struggling to create room in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(boolean)";
                        log(message);
                        if (!handleOutOfMemoryError) {
                            throw b_OutOfMemoryError;
                        }
                    }
                }
                return true;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Override
    protected boolean tryToEnsureThereIsEnoughMemoryToContinue() {
        while (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
            if (_AgentCollectionManager.swapToFile_AgentCollection_Account() < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to try to ensure there is enough memory to continue.
     *
     * @param a_FemaleCollection
     * @param handleOutOfMemoryError
     */
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            GENESIS_FemaleCollection a_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            if (tryToEnsureThereIsEnoughMemoryToContinue(
                    a_FemaleCollection)) {
            } else {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_FemaleCollection,boolean)";
                log(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError(message);
                throw new OutOfMemoryError();
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _GENESIS_Environment.clear_MemoryReserve();
                long account;
                //long totalAccount = 0;
                boolean createdRoom = false;
                while (!createdRoom) {
                    try {
                        account = _AgentCollectionManager.swapToFile_MaleCollection_Account();
                        if (account < 1) {
                            account = _AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
                                    a_FemaleCollection);
                            if (account < 1) {
                                // handleOutOfMemoryError changed to avoid problematic
                                // recursion and allow error to be thrown from method
                                handleOutOfMemoryError = false;
                                throw a_OutOfMemoryError;
                            } else {
                                //totalAccount += account;
                            }
                        } else {
                            //totalAccount += account;
                        }
                        _GENESIS_Environment.init_MemoryReserve(
                                a_FemaleCollection,
                                HandleOutOfMemoryErrorFalse);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = 
                                "Struggling to create room in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_FemaleCollection,boolean)";
                        log(message);
                        if (!handleOutOfMemoryError) {
                            throw b_OutOfMemoryError;
                        }
                    }
                }
                tryToEnsureThereIsEnoughMemoryToContinue(
                        a_FemaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * A method to try to ensure there is enough memory to continue.
     *
     * @param a_MaleCollection
     * @param handleOutOfMemoryError
     */
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            GENESIS_MaleCollection a_MaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            if (tryToEnsureThereIsEnoughMemoryToContinue(
                    a_MaleCollection)) {
            } else {
                String message = 
                        "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_MaleCollection,boolean)";
                log(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError(message);
                throw new OutOfMemoryError();
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _GENESIS_Environment.clear_MemoryReserve();
                long account;
                boolean createdRoom = false;
                while (!createdRoom) {
                    try {
                        account = _AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(
                                a_MaleCollection);
                        if (account < 1) {
                            account = _AgentCollectionManager.swapToFile_FemaleCollection_Account();
                            if (account < 1) {
                                // handleOutOfMemoryError changed to avoid problematic
                                // recursion and allow error to be thrown from method
                                handleOutOfMemoryError = false;
                                throw a_OutOfMemoryError;
                            }
                        }
                        _GENESIS_Environment.init_MemoryReserve(
                                a_MaleCollection,
                                HandleOutOfMemoryErrorFalse);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = 
                                "Struggling to create room in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_MaleCollection,boolean)";
                        log(message);
                        if (!handleOutOfMemoryError) {
                            throw b_OutOfMemoryError;
                        }
                    }
                }
                tryToEnsureThereIsEnoughMemoryToContinue(
                        a_MaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * A method to try to ensure there is enough memory to continue whilst not
     * swapping to disk a_FemaleCollection.
     *
     * @param a_FemaleCollection An FemaleCollection not to be swapped.
     * @return 
     */
    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            GENESIS_FemaleCollection a_FemaleCollection) {
        while (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
            if (_AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
                    a_FemaleCollection) < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to try to ensure there is enough memory to continue whilst not
     * swapping to disk a_MaleCollection.
     *
     * @param a_MaleCollection An MaleCollection not to be swapped.
     * @return 
     */
    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            GENESIS_MaleCollection a_MaleCollection) {
        while (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
            if (_AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(
                    a_MaleCollection) < 1) {
                return false;
            }
        }
        return true;
    }

//    /**
//     * A method to ensure there is enough memory to continue whilst not swapping
//     * to disk a_AgentCollection.
//     * @param a_AgentCollection_ID ID of an AgentCollection not to be swapped.
//     * @param handleOutOfMemoryError
//     */
//    public void tryToEnsureThereIsEnoughMemoryToContinue(
//            long a_AgentCollection_ID,
//            boolean handleOutOfMemoryError) {
//        try {
//            GENESIS_AgentCollection a_GENESIS_AgentCollection =
//                    _AgentCollectionManager.getAgentCollection(
//                    a_AgentCollection_ID,
//                    handleOutOfMemoryError);
//            tryToEnsureThereIsEnoughMemoryToContinue(
//                    a_GENESIS_AgentCollection,
//                    handleOutOfMemoryError);
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                _GENESIS_Environment.clear_MemoryReserve();
//                GENESIS_AgentCollection a_GENESIS_AgentCollection =
//                        _AgentCollectionManager.getAgentCollection(
//                        a_AgentCollection_ID,
//                        HandleOutOfMemoryErrorFalse);
//                long account = 0;
//                boolean createdRoom = false;
//                while (!createdRoom) {
//                    try {
//                        account = _AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                                a_GENESIS_AgentCollection);
//                        if (account < 1) {
//                            // handleOutOfMemoryError changed to avoid problematic
//                            // recursion and allow error to be thrown from method
//                            handleOutOfMemoryError = false;
//                            throw a_OutOfMemoryError;
//                        }
//                        _GENESIS_Environment.init_MemoryReserve(
//                                a_GENESIS_AgentCollection,
//                                HandleOutOfMemoryErrorFalse);
//                        createdRoom = true;
//                    } catch (OutOfMemoryError b_OutOfMemoryError) {
//                        String message = new String(
//                                "Struggling to create room in "
//                                + this.getClass().getName()
//                                + ".tryToEnsureThereIsEnoughMemoryToContinue(long,boolean)");
//                        log(message);
//                        if (!handleOutOfMemoryError) {
//                            throw b_OutOfMemoryError;
//                        }
//                    }
//                }
//                tryToEnsureThereIsEnoughMemoryToContinue(
//                        a_GENESIS_AgentCollection,
//                        handleOutOfMemoryError);
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_Account();
            if (test == null) {
                return 0;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = 
                        "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(boolean)";
                log(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError(message);
                throw new OutOfMemoryError();
            }
            return (Long) test[1];
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long account;
                long totalAccount = 0;
                boolean createdRoom = false;
                while (!createdRoom) {
                    try {
                        account = _AgentCollectionManager.swapToFile_AgentCollection_Account();
                        if (account < 1) {
                            // handleOutOfMemoryError changed to avoid problematic
                            // recursion and allow error to be thrown from method
                            handleOutOfMemoryError = false;
                            throw a_OutOfMemoryError;
                        } else {
                            totalAccount += account;
                        }
                        totalAccount += init_MemoryReserve_Account(
                                HandleOutOfMemoryErrorFalse);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = 
                                "Struggling to create room in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(boolean)";
                        log(message);
                        if (!handleOutOfMemoryError) {
                            throw b_OutOfMemoryError;
                        }
                    }
                }
                totalAccount += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        handleOutOfMemoryError);
                return totalAccount;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * To try to ensure there is enough memory to continue whilst accounting a
     * tally of the number of AgentCollections swapped to disk.
     *
     * @return An Object[] of size 2 where the first element is a boolean
     * indicating the success of trying to ensure if there is enough memory to
     * continue and the second element being a count of the total number of 
     * agent collections swapped in the process.
     */
    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_Account() {
        if (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
            Object[] result = new Object[2];
            long totalCount = 0L;
            long count;
            while (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
                count = _AgentCollectionManager.swapToFile_AgentCollection_Account(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                if (count < 1) {
                    result[0] = false;
                    result[1] = totalCount;
                    return result;
                } else {
                    totalCount += count;
                }
            }
            result[0] = true;
            result[1] = totalCount;
            return result;
        }
        return null;
    }

    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_GENESIS_FemaleCollection);
            if (test == null) {
                return 0;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = 
                        "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(GENESIS_FemaleCollection,boolean)";
                log(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError(message);
                throw new OutOfMemoryError();
            }
            return (Long) test[1];
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = 0;
                long account;
                boolean createdRoom = false;
                while (!createdRoom) {
                    try {
                        account = _AgentCollectionManager.swapToFile_MaleCollection_Account();
                        if (account < 1) {
                            account = _AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
                                    a_GENESIS_FemaleCollection);
                            if (account < 1) {
                                // handleOutOfMemoryError changed to avoid problematic
                                // recursion and allow error to be thrown from method
                                handleOutOfMemoryError = false;
                                throw a_OutOfMemoryError;
                            } else {
                                result += account;
                            }
                        } else {
                            result += account;
                        }
                        result += init_MemoryReserve_Account(
                                a_GENESIS_FemaleCollection,
                                HandleOutOfMemoryErrorFalse);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = 
                                "Struggling to create room in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(GENESIS_FemaleCollection,boolean)";
                        log(message);
                        if (!handleOutOfMemoryError) {
                            throw b_OutOfMemoryError;
                        }
                    }
                }
                result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public long tryToEnsureThereIsEnoughMemoryToContinue_Account(
            GENESIS_MaleCollection a_GENESIS_MaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_Account(
                    a_GENESIS_MaleCollection);
            if (test == null) {
                return 0;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = 
                        "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(GENESIS_MaleCollection,boolean)";
                log(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError(message);
                throw new OutOfMemoryError();
            }
            return (Long) test[1];
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                long result = 0;
                long account;
                boolean createdRoom = false;
                while (!createdRoom) {
                    try {
                        account = _AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(
                                a_GENESIS_MaleCollection);
                        if (account < 1) {
                            account = _AgentCollectionManager.swapToFile_FemaleCollection_Account(
                                    HandleOutOfMemoryErrorFalse);
                            if (account < 1) {
                                // handleOutOfMemoryError changed to avoid problematic
                                // recursion and allow error to be thrown from method
                                handleOutOfMemoryError = false;
                                throw a_OutOfMemoryError;
                            } else {
                                result += account;
                            }
                        } else {
                            result += account;
                        }
                        result += init_MemoryReserve_Account(
                                a_GENESIS_MaleCollection,
                                HandleOutOfMemoryErrorFalse);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = 
                                "Struggling to create room in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(GENESIS_MaleCollection,boolean)";
                        log(message);
                        if (!handleOutOfMemoryError) {
                            throw b_OutOfMemoryError;
                        }
                    }
                }
                result += tryToEnsureThereIsEnoughMemoryToContinue_Account(
                        a_GENESIS_MaleCollection,
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue whilst not swapping
     * to disk a_AgentCollection and accounting by returning a HashSet<Long>
     * identifying which AgentCollections have been swapped in the process.
     *
     * @param a_GENESIS_FemaleCollection
     * @return A HashSet<Long> of identifiers for AgentCollections swapped or
     * null if none are swapped
     */
    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_Account(
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection) {
        if (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
            Object[] result = new Object[2];
            long result1 = 0L;
            long account;
            while (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
                account = _AgentCollectionManager.swapToFile_MaleCollection_Account();
                if (account < 1) {
                    account = _AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(a_GENESIS_FemaleCollection,
                            _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                    if (account < 1) {
                        result[0] = false;
                        result[1] = result1;
                        return result;
                    } else {
                        result1 += account;
                    }
                } else {
                    result1 += account;
                }
            }
            result[0] = true;
            result[1] = result1;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue whilst not swapping
     * to disk a_AgentCollection and accounting by returning a HashSet<Long>
     * identifying which AgentCollections have been swapped in the process.
     *
     * @param a_GENESIS_MaleCollection
     * @return A HashSet<Long> of identifiers for AgentCollections swapped or
     * null if none are swapped
     */
    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_Account(
            GENESIS_MaleCollection a_GENESIS_MaleCollection) {
        if (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
            Object[] result = new Object[2];
            long result1 = 0L;
            long account;
            while (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
                account = _AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(a_GENESIS_MaleCollection,
                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                if (account < 1) {
                    account = _AgentCollectionManager.swapToFile_FemaleCollection_Account();
                    if (account < 1) {
                        result[0] = false;
                        result[1] = result1;
                        return result;
                    } else {
                        result1 += account;
                    }
                } else {
                    result1 += account;
                }
            }
            result[0] = true;
            result[1] = result1;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue that returns a
     * HashSet<Long> identifying any AgentCollections swapped in the process.
     *
     * @param handleOutOfMemoryError
     * @return A HashSet<Long> of identifiers for AgentCollections swapped or
     * null if no AgentCollections are swapped.
     */
    //public HashSet<Long> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
    public Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            boolean handleOutOfMemoryError) {
        try {
            Object[] result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail();
            if (result == null) {
                return null;
            }
            boolean result0 = (Boolean) result[0];
            if (!result0) {
                String message = 
                        "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(boolean)";
                log(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError(message);
                throw new OutOfMemoryError();
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                Object[] result = new Object[3];
                boolean createdRoom = false;
                while (!createdRoom) {
                    try {
                        Object[] potentialPartResult =
                                _AgentCollectionManager.swapToFile_MaleCollection_AccountDetail(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                        if (!(Boolean) potentialPartResult[0]) {
                            potentialPartResult =
                                    _AgentCollectionManager.swapToFile_FemaleCollection_AccountDetail(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                            if (!(Boolean) potentialPartResult[0]) {
                                // handleOutOfMemoryError changed to avoid problematic
                                // recursion and allow error to be thrown from method
                                handleOutOfMemoryError = false;
                                throw a_OutOfMemoryError;
                            } else {
                                GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                            }
                        } else {
                            GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                        }
                        potentialPartResult = init_MemoryReserve_AccountDetail(
                                HandleOutOfMemoryErrorFalse);
                        GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                        potentialPartResult = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                                handleOutOfMemoryError);
                        GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message =
                                "Struggling to create room in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(boolean)";
                        log(message);
                        if (!handleOutOfMemoryError) {
                            throw b_OutOfMemoryError;
                        }
                    }
                }
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue whilst accounting
     * by returning a HashSet<Long> identifying which AgentCollections have been
     * swapped in the process.
     *
     * @return A Object[] of size 3 with the first element being a Boolean
     * indicating the success of reaching the target. The second element is for
     * a HashSet<Long> of identifiers for FemaleCollections swapped, but may be
     * null if none are swapped. The third element is for a HashSet<Long> of
     * identifiers for MaleCollections swapped, but this may be null if nothing
     * is swapped. Null is returned fast if nothing is swapped and there is
     * enough memory to continue...
     */
    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail() {
        if (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
            Object[] result = new Object[3];
            HashSet<Long> swapped_FemaleCollectionID_HashSet = null;
            HashSet<Long> swapped_MaleCollectionID_HashSet = null;
            HashSet<Long> swapped_FemaleCollectionID_HashSet0;
            HashSet<Long> swapped_MaleCollectionID_HashSet0;
            while (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
                swapped_MaleCollectionID_HashSet0 = _AgentCollectionManager.swapToFile_MaleCollection_AccountDetail();
                if (swapped_MaleCollectionID_HashSet0 == null) {
                    swapped_FemaleCollectionID_HashSet0 =
                            _AgentCollectionManager.swapToFile_FemaleCollection_AccountDetail();
                    if (swapped_FemaleCollectionID_HashSet0 == null) {
                        result[0] = false;
                        result[1] = swapped_FemaleCollectionID_HashSet;
                        result[2] = swapped_MaleCollectionID_HashSet;
                        return result;
                    } else {
                        swapped_FemaleCollectionID_HashSet.addAll(
                                swapped_MaleCollectionID_HashSet0);
                    }
                } else {
                    swapped_MaleCollectionID_HashSet.addAll(
                            swapped_MaleCollectionID_HashSet0);
                }
            }
            result[0] = true;
            result[1] = swapped_FemaleCollectionID_HashSet;
            result[2] = swapped_MaleCollectionID_HashSet;
            return result;
        }
        return null;
    }

    /**
     * A method to ensure there is enough memory to continue whilst not swapping
     * to disk a_FemaleCollection
     *
     * @param a_GENESIS_FemaleCollection
     * @param handleOutOfMemoryError
     * @return A HashSet<Long> of identifiers for AgentCollections swapped or
     * null if no AgentCollections are swapped.
     */
    public Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            Object[] result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_GENESIS_FemaleCollection);
            if (result == null) {
                return null;
            }
            boolean result0 = (Boolean) result[0];
            if (!result0) {
                String message = 
                        "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(GENESIS_FemaleCollection,boolean)";
                log(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError(message);
                throw new OutOfMemoryError();
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                Object[] result = new Object[3];
                boolean createdRoom = false;
                while (!createdRoom) {
                    try {
                        Object[] potentialPartResult =
                                _AgentCollectionManager.swapToFile_MaleCollection_AccountDetail(
                                HandleOutOfMemoryErrorFalse);
                        if (!(Boolean) potentialPartResult[0]) {
                            potentialPartResult = _AgentCollectionManager.swapToFile_FemaleCollectionExcept_AccountDetail(
                                    a_GENESIS_FemaleCollection);
                            if (!(Boolean) potentialPartResult[0]) {
                                // handleOutOfMemoryError changed to avoid problematic
                                // recursion and allow error to be thrown from method
                                handleOutOfMemoryError = false;
                                throw a_OutOfMemoryError;
                            } else {
                                GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                            }
                        } else {
                            GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                        }
                        potentialPartResult = init_MemoryReserve_AccountDetail(
                                a_GENESIS_FemaleCollection,
                                HandleOutOfMemoryErrorFalse);
                        GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                        potentialPartResult = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                                a_GENESIS_FemaleCollection,
                                handleOutOfMemoryError);
                        GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = 
                                "Struggling to create room in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(boolean)";
                        log(message);
                        if (!handleOutOfMemoryError) {
                            throw b_OutOfMemoryError;
                        }
                    }
                }
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue whilst not swapping
     * to disk a_MaleCollection
     *
     * @param a_GENESIS_MaleCollection
     * @param handleOutOfMemoryError
     * @return A HashSet<Long> of identifiers for AgentCollections swapped or
     * null if no AgentCollections are swapped.
     */
    public Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            GENESIS_MaleCollection a_GENESIS_MaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            Object[] result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                    a_GENESIS_MaleCollection);
            if (result == null) {
                return null;
            }
            boolean result0 = (Boolean) result[0];
            if (!result0) {
                String message = 
                        "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(GENESIS_MaleCollection,boolean)";
                log(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError(message);
                throw new OutOfMemoryError();
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                Object[] result = new Object[3];
                boolean createdRoom = false;
                while (!createdRoom) {
                    try {
                        Object[] potentialPartResult = _AgentCollectionManager.swapToFile_MaleCollectionExcept_AccountDetail(
                                a_GENESIS_MaleCollection);
                        if (!(Boolean) potentialPartResult[0]) {
                            potentialPartResult =
                                    _AgentCollectionManager.swapToFile_FemaleCollection_AccountDetail(
                                    HandleOutOfMemoryErrorFalse);
                            if (!(Boolean) potentialPartResult[0]) {
                                // handleOutOfMemoryError changed to avoid problematic
                                // recursion and allow error to be thrown from method
                                handleOutOfMemoryError = false;
                                throw a_OutOfMemoryError;
                            } else {
                                GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                            }
                        } else {
                            GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                        }
                        potentialPartResult = init_MemoryReserve_AccountDetail(
                                a_GENESIS_MaleCollection,
                                HandleOutOfMemoryErrorFalse);
                        GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                        potentialPartResult = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
                                a_GENESIS_MaleCollection,
                                handleOutOfMemoryError);
                        GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = 
                                "Struggling to create room in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(boolean)";
                        log(message);
                        if (!handleOutOfMemoryError) {
                            throw b_OutOfMemoryError;
                        }
                    }
                }
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            GENESIS_FemaleCollection a_FemaleCollection) {
        if (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
            Object[] result = new Object[3];
            while (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
                Object[] potentialPartResult =
                        _AgentCollectionManager.swapToFile_MaleCollection_AccountDetail(
                        HandleOutOfMemoryErrorFalse);
                if (!(Boolean) potentialPartResult[0]) {
                    potentialPartResult =
                            _AgentCollectionManager.swapToFile_FemaleCollectionExcept_AccountDetail(
                            a_FemaleCollection);
                    if (!(Boolean) potentialPartResult[0]) {
                        result[0] = false;
                        return result;
                    } else {
                        GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                    }
                } else {
                    GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                }
            }
            result[0] = true;
            return result;
        }
        return null;
    }

    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
            GENESIS_MaleCollection a_MaleCollection) {
        if (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
            Object[] result = new Object[3];
            while (getTotalFreeMemory() < _GENESIS_Environment.Memory_Threshold) {
                Object[] potentialPartResult =
                        _AgentCollectionManager.swapToFile_MaleCollectionExcept_AccountDetail(
                        a_MaleCollection);
                if (!(Boolean) potentialPartResult[0]) {
                    potentialPartResult =
                            _AgentCollectionManager.swapToFile_FemaleCollection_AccountDetail(
                            HandleOutOfMemoryErrorTrue);
                    if (!(Boolean) potentialPartResult[0]) {
                        result[0] = false;
                        return result;
                    } else {
                        GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                    }
                } else {
                    GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                }
            }
            result[0] = true;
            return result;
        }
        return null;
    }

//    /**
//     * A method to ensure there is enough memory to continue whilst not swapping
//     * to disk a_AgentCollection and accounting by returning a HashSet<Long>
//     * identifying which AgentCollections have been swapped in the process.
//     * @param a_AgentCollection An AgentCollection not to be swapped.
//     * @param a_AgentCollection_ID ID of an AgentCollection not to be swapped.
//     * @param handleOutOfMemoryError
//     * @return A HashSet<Long> of identifiers for AgentCollections swapped or
//     * null if none are swapped
//     */
//    public HashSet<Long> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
//            long a_AgentCollection_ID,
//            boolean handleOutOfMemoryError) {
//        try {
//            GENESIS_AgentCollection a_GENESIS_AgentCollection =
//                    _AgentCollectionManager.getAgentCollection(
//                    a_AgentCollection_ID,
//                    _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
//            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
//                    a_GENESIS_AgentCollection,
//                    handleOutOfMemoryError);
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                clear_MemoryReserve();
//                GENESIS_AgentCollection a_GENESIS_AgentCollection =
//                        _AgentCollectionManager.getAgentCollection(
//                        a_AgentCollection_ID,
//                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
//                HashSet<Long> result = new HashSet<Long>();
//                boolean createdRoom = false;
//                while (!createdRoom) {
//                    try {
//                        HashSet<Long> potentialPartResult = _AgentCollectionManager.swapToFile_AgentCollectionExcept_AccountDetail(
//                                a_GENESIS_AgentCollection);
//                        if (potentialPartResult == null) {
//                            // handleOutOfMemoryError changed to avoid problematic
//                            // recursion and allow error to be thrown from method
//                            handleOutOfMemoryError = false;
//                            throw a_OutOfMemoryError;
//                        } else {
//                            if (potentialPartResult.isEmpty()) {
//                                // handleOutOfMemoryError changed to avoid problematic
//                                // recursion and allow error to be thrown from method
//                                handleOutOfMemoryError = false;
//                                throw a_OutOfMemoryError;
//                            } else {
//                                result.addAll(potentialPartResult);
//                            }
//                        }
//                        potentialPartResult = init_MemoryReserve_AccountDetail(
//                                a_GENESIS_AgentCollection,
//                                HandleOutOfMemoryErrorFalse);
//                        if (potentialPartResult != null) {
//                            if (!potentialPartResult.isEmpty()) {
//                                result.addAll(potentialPartResult);
//                            }
//                        }
//                        potentialPartResult = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetail(
//                                a_GENESIS_AgentCollection,
//                                handleOutOfMemoryError);
//                        if (potentialPartResult != null) {
//                            if (!potentialPartResult.isEmpty()) {
//                                result.addAll(potentialPartResult);
//                            }
//                        }
//                        createdRoom = true;
//                    } catch (OutOfMemoryError b_OutOfMemoryError) {
//                        String message = new String(
//                                "Struggling to create room in "
//                                + this.getClass().getName()
//                                + ".tryToEnsureThereIsEnoughMemoryToContinue_Account(boolean)");
//                        log(message);
//                        if (!handleOutOfMemoryError) {
//                            throw b_OutOfMemoryError;
//                        }
//                    }
//                }
//                return result;
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
    /**
     * @return _Directory
     */
    public File get_Directory() {
        return _Directory;
    }

    @Override
    public boolean swapToFile_DataAny(boolean handleOutOfMemoryError) {
        try {
            boolean result = _AgentCollectionManager.swapToFile_AgentCollection();
            /*
             * To use:
             * tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
             * It's success needs to be assessed and appropriate action
             * performed to prevent a loop.
             */
            tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clear_MemoryReserve();
                boolean result = swapToFile_DataAny(
                        HandleOutOfMemoryErrorFalse);
                if (!result) {
                    throw a_OutOfMemoryError;
                }
                init_MemoryReserve();
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Override
    public boolean swapToFile_DataAny() {
        return _AgentCollectionManager.swapToFile_AgentCollection();
    }

    private static void log(
            String message) {
        log(GENESIS_Log.GENESIS_DefaultLogLevel, message);
    }

    private static void log(
            Level level,
            String message) {
        Logger.getLogger(GENESIS_Log.GENESIS_DefaultLoggerName).log(level, message);
    }
}
