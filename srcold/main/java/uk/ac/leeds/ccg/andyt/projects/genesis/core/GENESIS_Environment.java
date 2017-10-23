package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.GENESIS_Files;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.process.AbstractTrafficModel;
import uk.ac.leeds.ccg.andyt.projects.genesis.process.Abstract_GENESIS_Model;
import uk.ac.leeds.ccg.andyt.projects.genesis.resource.AbstractResource;
import uk.ac.leeds.ccg.andyt.projects.genesis.travelingsalesman.TSMisc;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 * A general class to be instantiated and the Object accessible from all GENESIS
 * components for access to general factories and other Objects and memory
 * handling methods.
 */
public class GENESIS_Environment
        //extends Generic_OutOfMemoryErrorHandler
        extends GENESIS_OutOfMemoryErrorHandler
        implements Serializable {

    /**
     * Used for Logging
     */
    private static final String sourceClass = GENESIS_Environment.class.getName();
    private static final String sourcePackage = GENESIS_Environment.class.getPackage().getName();
    //= Logger.getLogger(sourcePackage);

    protected GENESIS_Files gf;

    
    public File _Directory;
    /**
     * Always initialised
     */
    public transient Abstract_GENESIS_Model _AbstractModel;
//    public transient CommonFactory _CommonFactory;
//    public transient PopulationFactory _PopulationFactory;
//    public transient MortalityFactory _MortalityFactory;
//    public transient FertilityFactory _FertilityFactory;
//    public transient MiscarriageFactory _MiscarriageFactory;
//    public transient MetadataFactory _MetadataFactory;
//    public transient ParametersFactory _ParametersFactory;
//    public AbstractTrafficModel _TrafficModel;
    public transient GENESIS_PersonFactory _PersonFactory;
    public transient GENESIS_AgentEnvironment _GENESIS_AgentEnvironment;
    /**
     * For mathematical uses
     */
    public transient Generic_BigDecimal _Generic_BigDecimal;
    // Grids Stuff
    /**
     * A store and reference for all AbstractGrid2DSquareCell
     */
    public transient Grids_Environment ge;
    //public AbstractGrid2DSquareCellFactory _AbstractGrid2DSquareCellFactory;
    /**
     * A factory for creating Grid2DSquareCellDouble of reporting dimensions
     */
    public transient Grids_Grid2DSquareCellDoubleFactory _reporting_Grid2DSquareCellDoubleFactory;
    /**
     * An example of a reporting grid for tidy coding.
     */
    public transient Grids_Grid2DSquareCellDouble _reporting_Grid2DSquareCellDouble;
    /**
     * A grid for storing Population Density at any tick.
     */
    public transient Grids_Grid2DSquareCellDouble _reportingPopulationDensity_Grid2DSquareCellDouble;
    /**
     * A grid for storing cumulative Population Density.
     */
    public transient Grids_Grid2DSquareCellDouble _reportingPopulationDensityAggregate_Grid2DSquareCellDouble;
    /**
     * A grid for storing cumulative Population Density of people that are
     * moving.
     */
    public transient Grids_Grid2DSquareCellDouble _reportingPopulationDensityMovingAggregate_Grid2DSquareCellDouble;
    /**
     * For storing the Envelope containing reporting grids. It is here for
     * convenience instead of repeatedly generating it from
     * _reporting_Grid2DSquareCellDouble._Dimensions
     */
    public transient Vector_Envelope2D _reporting_VectorEnvelope2D;
    /**
     * A factory for creating Grid2DSquareCellDouble of network dimensions
     */
    public transient Grids_Grid2DSquareCellDoubleFactory _network_Grid2DSquareCellDoubleFactory;
    /**
     * An example of a network grid for tidy coding.
     */
    public transient Grids_Grid2DSquareCellDouble _network_Grid2DSquareCellDouble;
    public static final int DecimalPlacePrecisionForPopulationProbabilities = 8;
    public static final RoundingMode RoundingModeForPopulationProbabilities = RoundingMode.HALF_EVEN;
    public static final MathContext MathContextForPopulationProbabilities = new MathContext(
            DecimalPlacePrecisionForPopulationProbabilities,
            RoundingModeForPopulationProbabilities);
    public int _DecimalPlacePrecisionForCalculations;
    public int _DecimalPlacePrecisionForNetworkCalculations;
    public int _DecimalPlacePrecisionForNetwork;
    public BigDecimal _ToRoundToX_BigDecimal;
    public BigDecimal _ToRoundToY_BigDecimal;
//    public Grid2DSquareCellDouble _World_Grid2DSquareCellDouble;
//    public double _XRange_double;
//    public double _YRange_double;
//    public double _XMin_double;
//    public double _YMin_double;
    public transient Grids_AbstractGrid2DSquareCell _Resource_Grid2DSquareCellDouble;
//    public Random _Random;
    public GENESIS_Time _Time;
    public GENESIS_Time _initial_Time;
    public transient HashSet<AbstractResource> _Resource_HashSet;
//    public VectorNetwork2D _Network2D;
    public transient TSMisc _TSMisc;
//    protected static long Memory_Threshold = 10000000L;
    //private static long Memory_Threshold_TenThousand = 10000L;
    //private static long Memory_Threshold_TwoMillion = 2000000L;
    public Vector_Environment ve;

    private GENESIS_Environment() {
    }

    public GENESIS_Environment(File Directory) {
        init_GENESIS_Environment(Directory);
    }

    /**
     * Creates a new _GENESIS_Environment based on a_GENESIS_Environment.
     * Because an instance of _GENESIS_Environment holds a references to all the
     * data in a simulation this does not deep copy everything. Also most
     * GENESIS Objects contain references to a _GENESIS_Environment instance, so
     * that duplication is necessarily a multi stage process that has to use
     * dummies to get all the references set up. This can be implemented more
     * comprehensively as needed, but the depth of the copy is unlikely to go to
     * the rootRoundIfNecessary of everything.
     *
     * @param ge The _GENESIS_Environment to deep copy.
     */
    public GENESIS_Environment(GENESIS_Environment ge) {
        this._AbstractModel = ge._AbstractModel;
//        this._DecimalPlacePrecisionForCalculations = a_GENESIS_Environment._DecimalPlacePrecisionForCalculations;
//        this._DecimalPlacePrecisionForNetwork = a_GENESIS_Environment._DecimalPlacePrecisionForNetwork;
//        this._DecimalPlacePrecisionForNetworkCalculations = a_GENESIS_Environment._DecimalPlacePrecisionForNetworkCalculations;
//        this.DecimalPlacePrecisionForPopulationProbabilities = a_GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities;
//        this._Directory = new File(a_GENESIS_Environment._Directory.toString());
////        // Create a new dummy GENESIS_AgentEnvironment using 
////        // a_GENESIS_Environment
////        this._GENESIS_AgentEnvironment = new GENESIS_AgentEnvironment(
////                a_GENESIS_Environment);
//        this._GENESIS_AgentEnvironment = new GENESIS_AgentEnvironment(
//                a_GENESIS_Environment,
//                a_GENESIS_Environment._GENESIS_AgentEnvironment);
//        this._Generic_BigDecimal = new Generic_BigDecimal(a_GENESIS_Environment._Generic_BigDecimal);
//        this._Grids_Environment = new Grids_Environment(a_GENESIS_Environment._Grids_Environment);
//        this._HandleOutOfMemoryError_boolean = a_GENESIS_Environment._HandleOutOfMemoryError_boolean;
//        this._MemoryReserve = a_GENESIS_Environment._MemoryReserve;
//        this._PersonFactory = a_GENESIS_Environment._PersonFactory;
//        this.RoundingModeForPopulationProbabilities = a_GENESIS_Environment.RoundingModeForPopulationProbabilities;
        this._Time = new GENESIS_Time(ge._Time);
//        this = a_GENESIS_Environment.;
    }

    private void init_GENESIS_Environment(File Directory) {
        gf = new GENESIS_Files(Directory);
        _GENESIS_AgentEnvironment = new GENESIS_AgentEnvironment(this);
        ge = new Grids_Environment(gf.getGridsDirectory());
        _Generic_BigDecimal = new Generic_BigDecimal();
    }

    public GENESIS_Environment(
            File Directory,
            Abstract_GENESIS_Model a_Model,
            GENESIS_Time a_Time) {
        init_GENESIS_Environment(
                Directory,
                a_Model,
                a_Time);
    }

    private void init_GENESIS_Environment(
            File Directory,
            Abstract_GENESIS_Model a_Model,
            GENESIS_Time a_Time) {
        init_GENESIS_Environment(Directory);
        this._AbstractModel = a_Model;
        this._Time = new GENESIS_Time(a_Time);
    }

    public GENESIS_Environment(
            File Directory,
            Abstract_GENESIS_Model a_Model,
            GENESIS_Time a_Time,
            Grids_Grid2DSquareCellDoubleFactory network_Grid2DSquareCellDoubleFactory,
            Grids_Grid2DSquareCellDoubleFactory reporting_Grid2DSquareCellDoubleFactory,
            boolean handleOutOfMemoryError) {
        init_GENESIS_Environment(
                Directory,
                a_Model,
                a_Time,
                network_Grid2DSquareCellDoubleFactory,
                reporting_Grid2DSquareCellDoubleFactory,
                handleOutOfMemoryError);
    }

    private void init_GENESIS_Environment(
            File Directory,
            Abstract_GENESIS_Model a_Model,
            GENESIS_Time a_Time,
            Grids_Grid2DSquareCellDoubleFactory network_Grid2DSquareCellDoubleFactory,
            Grids_Grid2DSquareCellDoubleFactory reporting_Grid2DSquareCellDoubleFactory,
            boolean handleOutOfMemoryError) {
        init_GENESIS_Environment(
                Directory,
                a_Model,
                a_Time);
        this._network_Grid2DSquareCellDoubleFactory = network_Grid2DSquareCellDoubleFactory;
        this._reporting_Grid2DSquareCellDoubleFactory = reporting_Grid2DSquareCellDoubleFactory;
        this._HandleOutOfMemoryError_boolean = handleOutOfMemoryError;
    }

    private void init_Generic_BigDecimal() {
        _Generic_BigDecimal = new Generic_BigDecimal();
    }

    /**
     *
     * @param a_Model
     * @param a_Time
     * @param a_Grid2DSquareCellDoubleFactory
     * @param handleOutOfMemoryError
     */
    @Deprecated
    public GENESIS_Environment(
            File Directory,
            Abstract_GENESIS_Model a_Model,
            GENESIS_Time a_Time,
            Grids_Grid2DSquareCellDoubleFactory a_Grid2DSquareCellDoubleFactory,
            boolean handleOutOfMemoryError) {
        init_GENESIS_Environment(
                Directory,
                a_Model,
                a_Time,
                a_Grid2DSquareCellDoubleFactory,
                null,
                handleOutOfMemoryError);
    }

    @Deprecated
    public GENESIS_Environment(
            Abstract_GENESIS_Model a_Model,
            GENESIS_Time _Time,
            Grids_Grid2DSquareCellDoubleFactory _Grid2DSquareCellDoubleFactory,
            Grids_Grid2DSquareCellDouble _World_Grid2DSquareCellDouble,
            boolean handleOutOfMemoryError) {
        this._AbstractModel = a_Model;
        this._Time = new GENESIS_Time(_Time);
        this._network_Grid2DSquareCellDoubleFactory = _Grid2DSquareCellDoubleFactory;
        this._HandleOutOfMemoryError_boolean = handleOutOfMemoryError;
    }

    public AbstractTrafficModel getTrafficModel() {
        if (_AbstractModel instanceof AbstractTrafficModel) {
            return (AbstractTrafficModel) _AbstractModel;
        }
        return null;
    }

    public Vector_Envelope2D get_reporting_VectorEnvelope2D() {
        if (_reporting_VectorEnvelope2D == null) {
            BigDecimal[] a_Grid2DSquareCell_Dimensions = _reportingPopulationDensity_Grid2DSquareCellDouble.get_Dimensions(_HandleOutOfMemoryError_boolean);
            Vector_Point2D a_VectorPoint2D = new Vector_Point2D(
                    ve,
                    a_Grid2DSquareCell_Dimensions[1],
                    a_Grid2DSquareCell_Dimensions[2]);
            Vector_Point2D b_VectorPoint2D = new Vector_Point2D(
                    ve,
                    a_Grid2DSquareCell_Dimensions[3],
                    a_Grid2DSquareCell_Dimensions[4]);
            Vector_Envelope2D a_VectorEnvelope2D = new Vector_Envelope2D(
                    a_VectorPoint2D);
            Vector_Envelope2D b_VectorEnvelope2D = new Vector_Envelope2D(
                    b_VectorPoint2D);
            _reporting_VectorEnvelope2D = a_VectorEnvelope2D.envelope(b_VectorEnvelope2D);
        }
        return _reporting_VectorEnvelope2D;
    }

    @Override
    public void init_MemoryReserve(
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (!swapToFile_DataAny()) {
                    throw a_OutOfMemoryError;
                }
                init_MemoryReserve(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public long init_MemoryReserve_AccountAgentCollections(
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections(
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollection_Account(
                        HandleOutOfMemoryErrorFalse);
                if (result < 1) {
                    swapToFile_Grid2DSquareCellChunk();
                }
                result += init_MemoryReserve_AccountAgentCollections(
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @return
     */
    //public HashSet<Long> init_MemoryReserve_AccountDetailAgentCollections(
    public Object[] init_MemoryReserve_AccountDetailAgentCollections(
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections(
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                Object[] result = _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollection_AccountDetail(
                        HandleOutOfMemoryErrorFalse);
                if (!(Boolean) result[0]) {
                    swapToFile_Grid2DSquareCellChunk();
                }
                Object[] potentialPartResult = init_MemoryReserve_AccountDetailAgentCollections(
                        handleOutOfMemoryError);
                if (potentialPartResult != null) {
                    if (result[1] == null) {
                        if (potentialPartResult[1] != null) {
                            result[1] = potentialPartResult[1];
                        }
                    } else {
                        ((HashSet<Long>) result[1]).addAll(
                                (HashSet<Long>) potentialPartResult[1]);
                    }
                    if (result[2] == null) {
                        if (potentialPartResult[2] != null) {
                            result[2] = potentialPartResult[2];
                        }
                    } else {
                        ((HashSet<Long>) result[2]).addAll(
                                (HashSet<Long>) potentialPartResult[2]);
                    }
                }
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public final void init_MemoryReserve(
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            a_GENESIS_FemaleCollection.ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                swapToFile_DataAnyExcept(a_GENESIS_FemaleCollection);
                init_MemoryReserve(
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public final void init_MemoryReserve(
            GENESIS_MaleCollection a_GENESIS_MaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            init_MemoryReserve();
            a_GENESIS_MaleCollection.ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                swapToFile_DataAnyExcept(a_GENESIS_MaleCollection);
                init_MemoryReserve(
                        a_GENESIS_MaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public static int getDefaultMaximumNumberOfObjectsPerDirectory() {
        return 100;
    }

//    /**
//     * @TODO Wnat some way to distinguish Male and Female...
//     */
//    public final void init_MemoryReserve(
//            long a_AgentCollection_ID,
//            boolean handleOutOfMemoryError) {
//        try {
//            GENESIS_AgentCollection a_GENESIS_AgentCollection = _GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.getAgentCollection(
//                    a_AgentCollection_ID,
//                    handleOutOfMemoryError);
//            init_MemoryReserve(
//                    a_GENESIS_AgentCollection,
//                    handleOutOfMemoryError);
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                clearMemoryReserve();
//                GENESIS_AgentCollection a_GENESIS_AgentCollection = _GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.getAgentCollection(
//                        a_AgentCollection_ID,
//                        HandleOutOfMemoryErrorFalse);
//                if (_GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                        a_GENESIS_AgentCollection) < 1) {
//                    swapChunk();
//                }
//                init_MemoryReserve(
//                        a_GENESIS_AgentCollection,
//                        handleOutOfMemoryError);
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
    @Override
    public boolean swapToFile_DataAny(boolean handleOutOfMemoryError) {
        try {
            boolean result = swapToFile_DataAny();
            tryToEnsureThereIsEnoughMemoryToContinue(
                    HandleOutOfMemoryErrorFalse);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean result = swapToFile_DataAny(
                        HandleOutOfMemoryErrorFalse);
                init_MemoryReserve(HandleOutOfMemoryErrorFalse);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Override
    public boolean swapToFile_DataAny() {
        long swapToFile_Grid2DSquareCellChunk_Account = 0;
        // Probably better to query here and know there is nothing to swap out
        // instead of an expensive try and fail
        try {
            swapToFile_Grid2DSquareCellChunk_Account += swapToFile_Grid2DSquareCellChunk_Account();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            log(
                    "Caught OutOfMemoryError in "
                    + this.getClass().getName() + ".swapToFile_DataAny()");
        }
        if (swapToFile_Grid2DSquareCellChunk_Account < 1) {
            if (swapToFile_AgentCollection_Account() < 1) {
                log(
                        "Found no data to swap in "
                        + this.getClass().getName()
                        + ".swapToFile_DataAny()");
//                throw new OutOfMemoryError();
                return false;
            }
        }
        return true;
    }

    public boolean swapToFile_DataAnyExcept(
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = swapToFile_DataAnyExcept(
                    a_GENESIS_FemaleCollection);
            tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    HandleOutOfMemoryErrorFalse);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean result = swapToFile_DataAnyExcept(
                        a_GENESIS_FemaleCollection);
                init_MemoryReserve(HandleOutOfMemoryErrorFalse);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public boolean swapToFile_DataAnyExcept(
            GENESIS_MaleCollection a_GENESIS_MaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = swapToFile_DataAnyExcept(
                    a_GENESIS_MaleCollection);
            tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    HandleOutOfMemoryErrorFalse);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean result = swapToFile_DataAnyExcept(
                        a_GENESIS_MaleCollection);
                init_MemoryReserve(HandleOutOfMemoryErrorFalse);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected boolean swapToFile_DataAnyExcept(
            GENESIS_MaleCollection a_GENESIS_MaleCollection) {
        long swapToFile_Grid2DSquareCellChunk_Account = 0;
        try {
            swapToFile_Grid2DSquareCellChunk_Account += swapToFile_Grid2DSquareCellChunk_Account();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            log(
                    "Caught OutOfMemoryError in "
                    + this.getClass().getName() + ".swapToFile_DataAnyExcept(GENESIS_MaleCollection)");
        }
        if (swapToFile_Grid2DSquareCellChunk_Account < 1) {
            if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(
                    a_GENESIS_MaleCollection) < 1) {
                if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_FemaleCollection_Account() < 1) {
                    log(
                            "Found no data to swap in "
                            + this.getClass().getName()
                            + ".swapToFile_DataAnyExcept(GENESIS_MaleCollection)");
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean swapToFile_DataAnyExcept(
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection) {
        long swapToFile_Grid2DSquareCellChunk_Account = 0;
        try {
            swapToFile_Grid2DSquareCellChunk_Account += swapToFile_Grid2DSquareCellChunk_Account();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            log(
                    "Caught OutOfMemoryError in "
                    + this.getClass().getName() + ".swapToFile_DataAnyExcept(GENESIS_FemaleCollection)");
        }
        if (swapToFile_Grid2DSquareCellChunk_Account < 1) {
            if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_MaleCollection_Account() < 1) {
                if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
                        a_GENESIS_FemaleCollection) < 1) {
                    log(
                            "Found no data to swap in "
                            + this.getClass().getName()
                            + ".swapToFile_DataAnyExcept(GENESIS_FemaleCollection)");
                    return false;
                }
            }
        }
        return true;
    }

//    protected boolean swapToFile_DataAnyExcept(
//            GENESIS_AgentCollection a_GENESIS_AgentCollection) {
//        long swapToFile_Grid2DSquareCellChunk_Account = 0;
//        try {
//            swapToFile_Grid2DSquareCellChunk_Account += swapToFile_Grid2DSquareCellChunk_Account();
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            log(
//                    "Caught OutOfMemoryError in "
//                    + this.getClass().getName() + ".swapToFile_DataAnyExcept()");
//        }
//        if (swapToFile_Grid2DSquareCellChunk_Account < 1) {
//            if (_GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                    a_GENESIS_AgentCollection) < 1) {
//                log(
//                        "Found no data to swap in "
//                        + this.getClass().getName()
//                        + ".swapToFile_DataAnyExcept()");
//                return false;
//            }
//        }
//        return true;
//    }
    public void swapToFile_Data() {
        swapToFile_Grid2DSquareCellChunks();
        _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollections();
    }

    public void swapToFile_Grid2DSquareCellChunks(
            boolean handleOutOfMemoryError) {
        try {
            swapToFile_Grid2DSquareCellChunks();
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapToFile_Grid2DSquareCellChunks_Account() < 1) {
                    _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollection();
                }
                init_MemoryReserve(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected void swapToFile_Grid2DSquareCellChunks() {
        ge.swapToFile_Grid2DSquareCellChunks(
                HandleOutOfMemoryErrorFalse);
    }

    public long swapToFile_Grid2DSquareCellChunks_Account(
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_Grid2DSquareCellChunks_Account();
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunk_Account();
                if (result < 1) {
                    _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollection();
                }
                init_MemoryReserve(handleOutOfMemoryError);
                result += swapToFile_Grid2DSquareCellChunks_Account(
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected long swapToFile_Grid2DSquareCellChunks_Account() {
        return ge.swapToFile_Grid2DSquareCellChunks_Account(
                HandleOutOfMemoryErrorFalse);
    }

    protected long swapToFile_Grid2DSquareCellChunk_Account() {
        if (ge.isDataToSwap()) {
            return ge.swapToFile_Grid2DSquareCellChunk_Account(
                    HandleOutOfMemoryErrorFalse);
        } else {
            return 0;
        }
    }

    public void swapToFile_Grid2DSquareCellChunk(
            boolean handleOutOfMemoryError) {
        try {
            swapToFile_Grid2DSquareCellChunk();
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapToFile_Grid2DSquareCellChunk_Account();
                if (result < 1) {
                    _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollection();
                }
                init_MemoryReserve(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Not needed, but makes code tidier and probably adds little in terms of
     * overhead...
     */
    protected void swapToFile_Grid2DSquareCellChunk() {
        ge.swapChunk(
                HandleOutOfMemoryErrorFalse);
    }

    public void swapToFile_AgentCollections(
            boolean handleOutOfMemoryError) {
        try {
            _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollections();
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (swapToFile_AgentCollection_Account() < 1) {
                    swapToFile_Grid2DSquareCellChunk();
                }
                init_MemoryReserve(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public long swapToFile_AgentCollections_Account(
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_AgentCollections_Account();
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapToFile_AgentCollections_Account();
                init_MemoryReserve(handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected long swapToFile_AgentCollections_Account() {
        return _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollections_Account();
    }

    public long swapToFile_AgentCollection_Account(
            boolean handleOutOfMemoryError) {
        try {
            long result = swapToFile_AgentCollection_Account();
            tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                long result = swapToFile_AgentCollection_Account();
                if (result < 1) {
                    if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                        throw a_OutOfMemoryError;
                    }
                }
                init_MemoryReserve(HandleOutOfMemoryErrorFalse);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected long swapToFile_AgentCollection_Account() {
        return _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollection_Account();
    }

    /**
     * A method to ensure there is enough memory to continue.
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
                String message
                        = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(boolean)";
                log(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError();
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean createdRoom = false;
                while (!createdRoom) {
                    if (!swapToFile_DataAny()) {
                        String message = "Warning! Not enough data to swap in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(boolean)";
                        log(message);
                        throw a_OutOfMemoryError;
                    }
                    try {
                        init_MemoryReserve(
                                HandleOutOfMemoryErrorFalse);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        log(
                                "Struggling to ensure there is enough memory in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(boolean)");
                    }
                }
                return tryToEnsureThereIsEnoughMemoryToContinue(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * A method to try to ensure there is enough memory to continue.
     *
     * @return
     */
    @Override
    protected boolean tryToEnsureThereIsEnoughMemoryToContinue() {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (!swapToFile_DataAny()) {
                return false;
            }
        }
        return true;
    }

//    /**
//     * A method to ensure there is enough memory to continue whilst not swapping
//     * to disk a_GENESIS_AgentCollection
//     * @param a_GENESIS_AgentCollection An AgentCollection not to be swapped.
//     * @param handleOutOfMemoryError
//     */
//    public void tryToEnsureThereIsEnoughMemoryToContinue(
//            GENESIS_AgentCollection a_GENESIS_AgentCollection,
//            boolean handleOutOfMemoryError) {
//        try {
//            if (!tryToEnsureThereIsEnoughMemoryToContinue(
//                    a_GENESIS_AgentCollection)) {
//                String message = new String(
//                        "Warning! Not enough data to swap in "
//                        + this.getClass().getName()
//                        + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_AgentCollection,boolean)");
//                log(message);
//                // Set to exit method with OutOfMemoryError
//                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError();
//            }
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                clearMemoryReserve();
//                if (_GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                        a_GENESIS_AgentCollection,
//                        HandleOutOfMemoryErrorFalse) < 1) {
//                    if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
//                        String message = new String(
//                                "Warning! Not enough data to swap in "
//                                + this.getClass().getName()
//                                + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_AgentCollection,boolean)");
//                        log(message);
//                        throw a_OutOfMemoryError;
//                    }
//                }
//                boolean createdRoom = false;
//                while (!createdRoom) {
//                    if (!swapToFile_DataAny()) {
//                        throw a_OutOfMemoryError;
//                    }
//                    try {
//                        init_MemoryReserve(
//                                a_GENESIS_AgentCollection,
//                                HandleOutOfMemoryErrorFalse);
//                        createdRoom = true;
//                    } catch (OutOfMemoryError b_OutOfMemoryError) {
//                        log(
//                                "Struggling to ensure there is enough memory in "
//                                + this.getClass().getName()
//                                + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_AgentCollection,boolean)");
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
    /**
     * A method to ensure there is enough memory to continue whilst not swapping
     * to disk a_GENESIS_FemaleCollection
     *
     * @param a_GENESIS_FemaleCollection An FemaleCollection not to be swapped.
     * @param handleOutOfMemoryError
     */
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            if (!tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection)) {
                String message
                        = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_FemaleCollection,boolean)";
                log(message);
                //System.err.println(message);
                message = "handleOutOfMemoryError " + handleOutOfMemoryError;
                log(message);
                //System.err.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError();
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
                        a_GENESIS_FemaleCollection,
                        HandleOutOfMemoryErrorFalse) < 1) {
                    if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                        String message = "Warning! Not enough data to swap in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_FemaleCollection,boolean)";
                        log(message);
                        //System.err.println(message);
                        throw a_OutOfMemoryError;
                    }
                }
                boolean createdRoom = false;
                while (!createdRoom) {
                    if (!swapToFile_DataAny()) {
                        throw a_OutOfMemoryError;
                    }
                    try {
                        init_MemoryReserve(
                                a_GENESIS_FemaleCollection,
                                HandleOutOfMemoryErrorFalse);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = "Struggling to ensure there is enough memory in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_FemaleCollection,boolean)";
                        log(message);
                        //System.err.println(message);
                    }
                }
                tryToEnsureThereIsEnoughMemoryToContinue(
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue whilst not swapping
     * to disk a_GENESIS_MaleCollection
     *
     * @param a_GENESIS_MaleCollection An MaleCollection not to be swapped.
     * @param handleOutOfMemoryError
     */
    public void tryToEnsureThereIsEnoughMemoryToContinue(
            GENESIS_MaleCollection a_GENESIS_MaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            if (!tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection)) {
                String message = "Warning! No GENESIS_AgentCollections to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_MaleCollection,boolean)";
                log(message);
                //System.err.println(message);
                message = "handleOutOfMemoryError " + handleOutOfMemoryError;
                log(message);
                //System.err.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError();
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(
                        a_GENESIS_MaleCollection,
                        HandleOutOfMemoryErrorFalse) < 1) {
                    if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                        String message = "Warning! Not enough data to swap in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_MaleCollection,boolean)";
                        log(message);
                        //System.err.println(message);
                        throw a_OutOfMemoryError;
                    }
                }
                boolean createdRoom = false;
                while (!createdRoom) {
                    if (!swapToFile_DataAny()) {
                        throw a_OutOfMemoryError;
                    }
                    try {
                        init_MemoryReserve(
                                a_GENESIS_MaleCollection,
                                HandleOutOfMemoryErrorFalse);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = "Struggling to ensure there is enough memory in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue(GENESIS_MaleCollection,boolean)";
                        log(message);
                        //System.err.println(message);
                    }
                }
                tryToEnsureThereIsEnoughMemoryToContinue(
                        a_GENESIS_MaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * A method to ensure there is enough memory to continue whilst not swapping
     * to disk a_GENESIS_FemaleCollection.
     *
     * @param a_FemaleCollection
     * @return
     */
    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            GENESIS_FemaleCollection a_FemaleCollection) {
        while (getTotalFreeMemory() < Memory_Threshold) {
//            long swappedMaleCollections = 0L;
//            if (!_GENESIS_AgentEnvironment._AgentCollectionManager._MaleCollection_HashMap.isEmpty()) {
//                swappedMaleCollections = _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_MaleCollection_Account();
//            }
//            if (swappedMaleCollections < 1) {
            if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
                    a_FemaleCollection) < 1) {
                if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_MaleCollection_Account() < 1) {
                    if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                        return false;
                    }
                }
            }
//            if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_MaleCollection_Account() < 1){
//                if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
//                        a_FemaleCollection) < 1) {
//                    if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
//                        return false;
//                    }
//                }
//            }
        }
        return true;
    }

    /**
     * A method to ensure there is enough memory to continue whilst not swapping
     * to disk a_GENESIS_MaleCollection.
     *
     * @param a_MaleCollection
     * @return
     */
    protected boolean tryToEnsureThereIsEnoughMemoryToContinue(
            GENESIS_MaleCollection a_MaleCollection) {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(
                    a_MaleCollection) < 1) {
                if (_GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_FemaleCollection_Account() < 1) {
                    if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

//    /**
//     * A method to ensure there is enough memory to continue whilst not swapping
//     * to disk a_GENESIS_AgentCollection.
//     * @param a_AgentCollection_ID ID of an AgentCollection not to be swapped.
//     * @param handleOutOfMemoryError
//     */
//    public void tryToEnsureThereIsEnoughMemoryToContinue(
//            long a_AgentCollection_ID,
//            boolean handleOutOfMemoryError) {
//        try {
//            GENESIS_AgentCollection a_GENESIS_AgentCollection =
//                    _GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.getAgentCollection(
//                    a_AgentCollection_ID,
//                    HandleOutOfMemoryErrorFalse);
//            if (!tryToEnsureThereIsEnoughMemoryToContinue(
//                    a_GENESIS_AgentCollection)) {
//                String message = new String(
//                        "Warning! Not enough data to swap in "
//                        + this.getClass().getName()
//                        + ".tryToEnsureThereIsEnoughMemoryToContinue(long,boolean)");
//                log(message);
//                // Set to exit method with OutOfMemoryError
//                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError();
//            }
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                clearMemoryReserve();
//                GENESIS_AgentCollection a_GENESIS_AgentCollection =
//                        _GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.getAgentCollection(
//                        a_AgentCollection_ID,
//                        HandleOutOfMemoryErrorFalse);
//                if (_GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                        a_GENESIS_AgentCollection,
//                        HandleOutOfMemoryErrorFalse) < 1) {
//                    if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
//                        String message = new String(
//                                "Warning! Not enough data to swap in "
//                                + this.getClass().getName()
//                                + ".tryToEnsureThereIsEnoughMemoryToContinue(long,boolean)");
//                        log(message);
//                        throw a_OutOfMemoryError;
//                    }
//                }
//                boolean createdRoom = false;
//                while (!createdRoom) {
//                    if (!swapToFile_DataAny()) {
//                        throw a_OutOfMemoryError;
//                    }
//                    try {
//                        init_MemoryReserve(
//                                a_GENESIS_AgentCollection,
//                                HandleOutOfMemoryErrorFalse);
//                        createdRoom = true;
//                    } catch (OutOfMemoryError b_OutOfMemoryError) {
//                        log(
//                                "Struggling to ensure there is enough memory in "
//                                + this.getClass().getName()
//                                + ".tryToEnsureThereIsEnoughMemoryToContinue(long,boolean)");
//                    }
//                }
//                tryToEnsureThereIsEnoughMemoryToContinue(
//                        a_AgentCollection_ID,
//                        handleOutOfMemoryError);
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
    public long tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections(
            boolean handleOutOfMemoryError) {
        try {
            Object[] test = tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections();
            if (test == null) {
                return 0;
            }
            boolean test0 = (Boolean) test[0];
            if (!test0) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections(boolean)";
                log(message);
                System.err.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
                throw new OutOfMemoryError();
            }
            return (Long) test[1];
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                Long result = swapToFile_AgentCollection_Account();
                if (result < 1) {
                    if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                        String message = "Warning! Not enough data to swap in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections(boolean)";
                        log(message);
                        System.err.println(message);
                        throw a_OutOfMemoryError;
                    }
                }
                boolean createdRoom = false;
                while (!createdRoom) {
                    if (!swapToFile_DataAny()) {
                        throw a_OutOfMemoryError;
                    }
                    try {
                        result += init_MemoryReserve_AccountAgentCollections(
                                HandleOutOfMemoryErrorFalse);
                        createdRoom = true;
                    } catch (OutOfMemoryError b_OutOfMemoryError) {
                        String message = "Struggling to ensure there is enough memory in "
                                + this.getClass().getName()
                                + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections(boolean)";
                        log(message);
                        System.err.println(message);
                    }
                }
                result += tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections(
                        handleOutOfMemoryError);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections() {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[2];
            long result1 = 0;
            long a_long;
            while (getTotalFreeMemory() < Memory_Threshold) {
                a_long = swapToFile_AgentCollection_Account();
                if (a_long < 1) {
                    long b_long = swapToFile_Grid2DSquareCellChunk_Account();
                    if (b_long < 1) {
                        result[0] = false;
                        result[1] = result1;
                        return result;
                    }
                }
                result1 += a_long;
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
     * @return
     */
    //public HashSet<Long> tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections(
    public Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections(
            boolean handleOutOfMemoryError) {
        try {
            Object[] result = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections();
            if (!(Boolean) result[0]) {
                String message = "Warning! Not enough data to swap in "
                        + this.getClass().getName()
                        + ".tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections(boolean)";
                log(message);
                System.err.println(message);
                // Set to exit method with OutOfMemoryError
                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError(message);
                throw new OutOfMemoryError();
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager = _GENESIS_AgentEnvironment.get_AgentCollectionManager();
                Object[] result = a_GENESIS_AgentCollectionManager.swapToFile_MaleCollection_AccountDetail(
                        HandleOutOfMemoryErrorFalse);
                if (!(Boolean) result[0]) {
                    Object[] potentialPartResult = a_GENESIS_AgentCollectionManager.swapToFile_FemaleCollection_AccountDetail(
                            HandleOutOfMemoryErrorFalse);
                    if (!(Boolean) potentialPartResult[0]) {
                        if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                            throw a_OutOfMemoryError;
                        }
                    } else {
                        result = potentialPartResult;
                    }
                }
                Object[] potentialPartResult
                        = tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections(
                                handleOutOfMemoryError);
                GENESIS_AgentCollectionManager.combine(result, potentialPartResult);
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * A method to try to ensure there is enough memory to continue whilst
     * accounting any Agent collections that are swapped in the process. If
     * nothing need be swapped then null is quickly returned. Otherwise an
     * Object[] of size 3 is returned the first element of which is a Boolean
     * which indicates the success of the goal. The second element is a
     * HashSet<Long> containing the ID of any GENESIS_FemaleCollections swapped.
     * The third element is a HashSet<Long> containing the ID of any
     * GENESIS_MaleCollections swapped. Firstly this method will try to swap
     * GENESIS_MaleCollections, the GENESIS_FemaleCollections, then
     * AbstractGrid2DSquareCellChunks if there are no GENESIS_AgentCollections
     * to be swapped.
     *
     * @return
     */
    protected Object[] tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections() {
        if (getTotalFreeMemory() < Memory_Threshold) {
            Object[] result = new Object[3];
            Object[] potentialPartResult;
            while (getTotalFreeMemory() < Memory_Threshold) {
                potentialPartResult = _GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollection_AccountDetail(
                        HandleOutOfMemoryErrorFalse);
                if (potentialPartResult == null) {
                    if (swapToFile_Grid2DSquareCellChunk_Account() < 1) {
                        result[0] = false;
                        return result;
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

    /**
     * @param handleOutOfMemoryError
     * @return _Directory
     */
    public File get_Directory(
            boolean handleOutOfMemoryError) {
        try {
            File result = get_Directory();
            tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                swapToFile_DataAny();
                init_MemoryReserve();
                return get_Directory(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return _Directory
     */
    protected File get_Directory() {
        return _Directory;
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
    
    public GENESIS_Files getGENESIS_Files(){
        return gf;
    }
}
