package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.Household;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 * A factory for creating Male and Female Persons.
 */
public class GENESIS_PersonFactory implements Serializable {

    static final long serialVersionUID = 1L;
    protected transient GENESIS_Environment _GENESIS_Environment;
    protected transient GENESIS_AgentCollectionManager _GENESIS_AgentCollectionManager;

    public GENESIS_PersonFactory() {
    }

    public GENESIS_PersonFactory(
            GENESIS_PersonFactory a_GENESIS_PersonFactory) {
        this(a_GENESIS_PersonFactory._GENESIS_Environment,
                a_GENESIS_PersonFactory);
    }

    public GENESIS_PersonFactory(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_PersonFactory a_GENESIS_PersonFactory) {
        this._GENESIS_Environment = a_GENESIS_Environment;
        this._GENESIS_AgentCollectionManager = new GENESIS_AgentCollectionManager(
                a_GENESIS_Environment,
                a_GENESIS_PersonFactory._GENESIS_AgentCollectionManager);
    }

    public GENESIS_PersonFactory(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager) {
        _GENESIS_Environment = a_GENESIS_Environment;
        _GENESIS_AgentCollectionManager = a_GENESIS_AgentCollectionManager;
    }

    /**
     *
     * a_GENESIS_AgentCollection Not to be swapped
     * @param handleOutOfMemoryError
     * @param a_GENESIS_FemaleCollection
     * @return 
     */
    public GENESIS_Female createFemale(
            GENESIS_Age age,
            Household a_Household,
            Vector_Point2D a_VectorPoint2D,
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
//            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                    _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    handleOutOfMemoryError);
            GENESIS_Female result = new GENESIS_Female(
                    _GENESIS_Environment,
                    _GENESIS_AgentCollectionManager,
                    age,
                    a_Household,
                    a_VectorPoint2D);
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
            _GENESIS_AgentCollectionManager._IndexOfLastBornFemale =
                    result.get_Agent_ID(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _GENESIS_Environment.clear_MemoryReserve();
//                GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                    _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    handleOutOfMemoryError);
                if (_GENESIS_AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(a_GENESIS_FemaleCollection,
                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse) < 1) {
                    if (_GENESIS_AgentCollectionManager.swapToFile_MaleCollection_Account(_GENESIS_Environment.HandleOutOfMemoryErrorFalse) < 1) {
                        _GENESIS_Environment.swapToFile_Grid2DSquareCellChunk(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                    }
                }
                _GENESIS_Environment.init_MemoryReserve(a_GENESIS_FemaleCollection,
                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                return createFemale(
                        age,
                        a_Household,
                        a_VectorPoint2D,
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @param a_VectorPoint2D
     * @return 
     */
    public GENESIS_Female createFemale(
            GENESIS_Age age,
            Household a_Household,
            Vector_Point2D a_VectorPoint2D,
            boolean handleOutOfMemoryError) {
        try {
//            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                    _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    handleOutOfMemoryReserve);
            GENESIS_Female result = new GENESIS_Female(
                    _GENESIS_Environment,
                    _GENESIS_AgentCollectionManager,
                    age,
                    a_Household,
                    a_VectorPoint2D);
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection =
                    result.get_FemaleCollection(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
            _GENESIS_AgentCollectionManager._IndexOfLastBornFemale =
                    result.get_Agent_ID(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _GENESIS_Environment.clear_MemoryReserve();
                GENESIS_Female result = new GENESIS_Female(
                        _GENESIS_Environment,
                        _GENESIS_AgentCollectionManager,
                        age,
                        a_Household,
                        a_VectorPoint2D);
                GENESIS_FemaleCollection a_GENESIS_FemaleCollection =
                        result.get_FemaleCollection(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                _GENESIS_Environment.swapToFile_DataAnyExcept(a_GENESIS_FemaleCollection,
                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                _GENESIS_Environment.init_MemoryReserve(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public GENESIS_Male createMale(
            GENESIS_Age age,
            Household a_Household,
            Vector_Point2D a_VectorPoint2D,
            boolean handleOutOfMemoryError) {
        try {
//            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                    _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    handleOutOfMemoryReserve);
            GENESIS_Male result = new GENESIS_Male(
                    _GENESIS_Environment,
                    _GENESIS_AgentCollectionManager,
                    age,
                    a_Household,
                    a_VectorPoint2D);
            GENESIS_MaleCollection a_GENESIS_MaleCollection =
                    result.get_MaleCollection(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
            _GENESIS_AgentCollectionManager._IndexOfLastBornMale =
                    result.get_Agent_ID(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _GENESIS_Environment.clear_MemoryReserve();
                GENESIS_Male result = new GENESIS_Male(
                        _GENESIS_Environment,
                        _GENESIS_AgentCollectionManager,
                        age,
                        a_Household,
                        a_VectorPoint2D);
                GENESIS_MaleCollection a_GENESIS_MaleCollection =
                        result.get_MaleCollection(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                _GENESIS_Environment.swapToFile_DataAnyExcept(a_GENESIS_MaleCollection,
                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                _GENESIS_Environment.init_MemoryReserve(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public GENESIS_Male createMale(
            GENESIS_Age age,
            Household a_Household,
            Vector_Point2D a_VectorPoint2D,
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
//            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                    _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    handleOutOfMemoryReserve);
            GENESIS_Male result = new GENESIS_Male(
                    _GENESIS_Environment,
                    _GENESIS_AgentCollectionManager,
                    age,
                    a_Household,
                    a_VectorPoint2D);
            GENESIS_MaleCollection a_GENESIS_MaleCollection =
                    result.get_MaleCollection(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
            _GENESIS_AgentCollectionManager._IndexOfLastBornMale =
                    result.get_Agent_ID(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _GENESIS_Environment.clear_MemoryReserve();
                _GENESIS_Environment.swapToFile_DataAnyExcept(a_GENESIS_FemaleCollection,
                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                _GENESIS_Environment.init_MemoryReserve(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                return createMale(
                        age,
                        a_Household,
                        a_VectorPoint2D,
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }
}
