package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.GENESIS_Household;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 * A factory for creating Male and Female Persons.
 */
public class GENESIS_PersonFactory extends GENESIS_Object implements Serializable {

    //static final long serialVersionUID = 1L;
    protected transient GENESIS_AgentCollectionManager AgentCollectionManager;

    public GENESIS_PersonFactory() {
    }

    public GENESIS_PersonFactory(
            GENESIS_PersonFactory pf) {
        this(pf.ge,
                pf);
    }

    public GENESIS_PersonFactory(
            GENESIS_Environment ge,
            GENESIS_PersonFactory pf) {
        super(ge);
        AgentCollectionManager = new GENESIS_AgentCollectionManager(
                ge,
                pf.AgentCollectionManager);
    }

    public GENESIS_PersonFactory(
            GENESIS_Environment ge,
            GENESIS_AgentCollectionManager acm) {
        super(ge);
        AgentCollectionManager = acm;
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
            GENESIS_Household a_Household,
            Vector_Point2D a_VectorPoint2D,
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
//            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                    ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    handleOutOfMemoryError);
            GENESIS_Female result = new GENESIS_Female(
                    ge,
                    AgentCollectionManager,
                    age,
                    a_Household,
                    a_VectorPoint2D);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
            AgentCollectionManager._IndexOfLastBornFemale =
                    result.getAgentID(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
//                GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                    ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    handleOutOfMemoryError);
                if (AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(a_GENESIS_FemaleCollection,
                        ge.HandleOutOfMemoryErrorFalse) < 1) {
                    if (AgentCollectionManager.swapToFile_MaleCollection_Account(ge.HandleOutOfMemoryErrorFalse) < 1) {
                        ge.swapChunk(ge.HandleOutOfMemoryErrorFalse);
                    }
                }
                ge.initMemoryReserve(a_GENESIS_FemaleCollection,
                        ge.HandleOutOfMemoryErrorFalse);
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
            GENESIS_Household a_Household,
            Vector_Point2D a_VectorPoint2D,
            boolean handleOutOfMemoryError) {
        try {
//            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                    ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    handleOutOfMemoryReserve);
            GENESIS_Female result = new GENESIS_Female(
                    ge,
                    AgentCollectionManager,
                    age,
                    a_Household,
                    a_VectorPoint2D);
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection =
                    result.get_FemaleCollection(ge.HandleOutOfMemoryErrorFalse);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
            AgentCollectionManager._IndexOfLastBornFemale =
                    result.getAgentID(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                GENESIS_Female result = new GENESIS_Female(
                        ge,
                        AgentCollectionManager,
                        age,
                        a_Household,
                        a_VectorPoint2D);
                GENESIS_FemaleCollection a_GENESIS_FemaleCollection =
                        result.get_FemaleCollection(ge.HandleOutOfMemoryErrorFalse);
                ge.swapDataAnyExcept(a_GENESIS_FemaleCollection,
                        ge.HandleOutOfMemoryErrorFalse);
                ge.initMemoryReserve(ge.HandleOutOfMemoryErrorFalse);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public GENESIS_Male createMale(
            GENESIS_Age age,
            GENESIS_Household a_Household,
            Vector_Point2D a_VectorPoint2D,
            boolean handleOutOfMemoryError) {
        try {
//            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                    ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    handleOutOfMemoryReserve);
            GENESIS_Male result = new GENESIS_Male(
                    ge,
                    AgentCollectionManager,
                    age,
                    a_Household,
                    a_VectorPoint2D);
            GENESIS_MaleCollection a_GENESIS_MaleCollection =
                    result.get_MaleCollection(ge.HandleOutOfMemoryErrorFalse);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
            AgentCollectionManager._IndexOfLastBornMale =
                    result.getAgentID(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                GENESIS_Male result = new GENESIS_Male(
                        ge,
                        AgentCollectionManager,
                        age,
                        a_Household,
                        a_VectorPoint2D);
                GENESIS_MaleCollection a_GENESIS_MaleCollection =
                        result.get_MaleCollection(ge.HandleOutOfMemoryErrorFalse);
                ge.swapDataAnyExcept(a_GENESIS_MaleCollection,
                        ge.HandleOutOfMemoryErrorFalse);
                ge.initMemoryReserve(ge.HandleOutOfMemoryErrorFalse);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public GENESIS_Male createMale(
            GENESIS_Age age,
            GENESIS_Household a_Household,
            Vector_Point2D a_VectorPoint2D,
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
//            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                    ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    handleOutOfMemoryReserve);
            GENESIS_Male result = new GENESIS_Male(
                    ge,
                    AgentCollectionManager,
                    age,
                    a_Household,
                    a_VectorPoint2D);
            GENESIS_MaleCollection a_GENESIS_MaleCollection =
                    result.get_MaleCollection(ge.HandleOutOfMemoryErrorFalse);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
            AgentCollectionManager._IndexOfLastBornMale =
                    result.getAgentID(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(a_GENESIS_FemaleCollection,
                        ge.HandleOutOfMemoryErrorFalse);
                ge.initMemoryReserve(ge.HandleOutOfMemoryErrorFalse);
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
