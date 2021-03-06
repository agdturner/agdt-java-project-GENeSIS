package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.grids.GENESIS_Grids;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.GENESIS_Files;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.process.GENESIS_AbstractModelTraffic;
import uk.ac.leeds.ccg.andyt.projects.genesis.process.GENESIS_AbstractModel;
import uk.ac.leeds.ccg.andyt.projects.genesis.travelingsalesman.GENESIS_TravelingSalesman;
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

    protected GENESIS_Files Files;

    /**
     *
     */
    public File Directory;
    /**
     * Always initialised
     */
    public transient GENESIS_AbstractModel AbstractModel;
//    public transient CommonFactory _CommonFactory;
//    public transient PopulationFactory _PopulationFactory;
//    public transient MortalityFactory _MortalityFactory;
//    public transient FertilityFactory _FertilityFactory;
//    public transient MiscarriageFactory _MiscarriageFactory;
//    public transient MetadataFactory _MetadataFactory;
//    public transient ParametersFactory _ParametersFactory;
//    public GENESIS_AbstractModelTraffic _TrafficModel;
    public transient GENESIS_PersonFactory PersonFactory;
    public transient GENESIS_AgentEnvironment AgentEnvironment;
    /**
     * For mathematical uses
     */
    public transient Math_BigDecimal _Math_BigDecimal;
    // Grids Stuff
    /**
     * A store and reference for all AbstractGrid2DSquareCell
     */
    public transient Grids_Environment ge;
    //public AbstractGrid2DSquareCellFactory _AbstractGrid2DSquareCellFactory;
    /**
     * A factory for creating Grid2DSquareCellDouble of reporting dimensions
     */
    public transient Grids_GridDoubleFactory ReportingGridDoubleFactory;
    /**
     * An example of a reporting grid for tidy coding.
     */
    public transient Grids_GridDouble ReportingGridDouble;
    /**
     * A grid for storing Population Density at any tick.
     */
    public transient Grids_GridDouble ReportingPopulationDensityGridDouble;
    /**
     * A grid for storing cumulative Population Density.
     */
    public transient Grids_GridDouble ReportingPopulationDensityAggregateGridDouble;
    /**
     * A grid for storing cumulative Population Density of people that are
     * moving.
     */
    public transient Grids_GridDouble ReportingPopulationDensityMovingAggregateGridDouble;

    public GENESIS_Grids Grids;
    /**
     * For storing the Envelope containing reporting grids. It is here for
 convenience instead of repeatedly generating it from
 ReportingGridDouble._Dimensions
     */
    public transient Vector_Envelope2D ReportingEnvelope;
    /**
     * A factory for creating Grid2DSquareCellDouble of network dimensions
     */
    public transient Grids_GridDoubleFactory NetworkGridDoubleFactory;
    /**
     * An example of a network grid for tidy coding.
     */
    public transient Grids_GridDouble NetworkGridDouble;
    public final int DecimalPlacePrecisionForPopulationProbabilities = 8;
    public final RoundingMode RoundingModeForPopulationProbabilities = RoundingMode.HALF_EVEN;
    public final MathContext MathContextForPopulationProbabilities = new MathContext(
            DecimalPlacePrecisionForPopulationProbabilities,
            RoundingModeForPopulationProbabilities);
    public int DecimalPlacePrecisionForCalculations;
    public int DecimalPlacePrecisionForNetworkCalculations;
    public int DecimalPlacePrecisionForNetwork;
    public BigDecimal _ToRoundToX_BigDecimal;
    public BigDecimal _ToRoundToY_BigDecimal;
//    public Grid2DSquareCellDouble _World_Grid2DSquareCellDouble;
//    public double _XRange_double;
//    public double _YRange_double;
//    public double _XMin_double;
//    public double _YMin_double;
    public transient Grids_AbstractGridNumber ResourceGridDouble;
//    public Random _Random;
    public GENESIS_Time Time;
    public GENESIS_Time InitialTime;
//    public VectorNetwork2D _Network2D;
    public transient GENESIS_TravelingSalesman TS;
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
     * Creates a new ge based on a_GENESIS_Environment. Because an instance of
     * ge holds a references to all the data in a simulation this does not deep
     * copy everything. Also most GENESIS Objects contain references to a ge
     * instance, so that duplication is necessarily a multi stage process that
     * has to use dummies to get all the references set up. This can be
     * implemented more comprehensively as needed, but the depth of the copy is
     * unlikely to go to the rootRoundIfNecessary of everything.
     *
     * @param ge The ge to deep copy.
     */
    public GENESIS_Environment(GENESIS_Environment ge) {
        this.AbstractModel = ge.AbstractModel;
//        this.DecimalPlacePrecisionForCalculations = a_GENESIS_Environment.DecimalPlacePrecisionForCalculations;
//        this.DecimalPlacePrecisionForNetwork = a_GENESIS_Environment.DecimalPlacePrecisionForNetwork;
//        this.DecimalPlacePrecisionForNetworkCalculations = a_GENESIS_Environment.DecimalPlacePrecisionForNetworkCalculations;
//        this.DecimalPlacePrecisionForPopulationProbabilities = a_GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities;
//        this.Directory = new File(a_GENESIS_Environment.Directory.toString());
////        // Create a new dummy GENESIS_AgentEnvironment using 
////        // a_GENESIS_Environment
////        this.AgentEnvironment = new GENESIS_AgentEnvironment(
////                a_GENESIS_Environment);
//        this.AgentEnvironment = new GENESIS_AgentEnvironment(
//                a_GENESIS_Environment,
//                a_GENESIS_Environment.AgentEnvironment);
//        this._Math_BigDecimal = new Math_BigDecimal(a_GENESIS_Environment._Math_BigDecimal);
//        this._Grids_Environment = new Grids_Environment(a_GENESIS_Environment._Grids_Environment);
//        this.HOOME = a_GENESIS_Environment.HOOME;
//        this.MemoryReserve = a_GENESIS_Environment.MemoryReserve;
//        this.PersonFactory = a_GENESIS_Environment.PersonFactory;
//        this.RoundingModeForPopulationProbabilities = a_GENESIS_Environment.RoundingModeForPopulationProbabilities;
        this.Time = new GENESIS_Time(ge.Time);
//        this = a_GENESIS_Environment.;
    }

    private void init_GENESIS_Environment(File Directory) {
        Files = new GENESIS_Files(Directory);
        AgentEnvironment = new GENESIS_AgentEnvironment(this);
        ge = new Grids_Environment(Files.getGridsDirectory());
        _Math_BigDecimal = new Math_BigDecimal();
    }

    public GENESIS_Environment(
            File Directory,
            GENESIS_AbstractModel a_Model,
            GENESIS_Time a_Time) {
        init_GENESIS_Environment(
                Directory,
                a_Model,
                a_Time);
    }

    private void init_GENESIS_Environment(
            File Directory,
            GENESIS_AbstractModel a_Model,
            GENESIS_Time a_Time) {
        init_GENESIS_Environment(Directory);
        this.AbstractModel = a_Model;
        this.Time = new GENESIS_Time(a_Time);
    }

    public GENESIS_Environment(
            File Directory,
            GENESIS_AbstractModel a_Model,
            GENESIS_Time a_Time,
            Grids_GridDoubleFactory network_Grid2DSquareCellDoubleFactory,
            Grids_GridDoubleFactory reporting_Grid2DSquareCellDoubleFactory,
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
            GENESIS_AbstractModel a_Model,
            GENESIS_Time a_Time,
            Grids_GridDoubleFactory network_Grid2DSquareCellDoubleFactory,
            Grids_GridDoubleFactory reporting_Grid2DSquareCellDoubleFactory,
            boolean handleOutOfMemoryError) {
        init_GENESIS_Environment(
                Directory,
                a_Model,
                a_Time);
        this.NetworkGridDoubleFactory = network_Grid2DSquareCellDoubleFactory;
        this.ReportingGridDoubleFactory = reporting_Grid2DSquareCellDoubleFactory;
        this.HOOME = handleOutOfMemoryError;
    }

    private void init_Math_BigDecimal() {
        _Math_BigDecimal = new Math_BigDecimal();
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
            GENESIS_AbstractModel a_Model,
            GENESIS_Time a_Time,
            Grids_GridDoubleFactory a_Grid2DSquareCellDoubleFactory,
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
            GENESIS_AbstractModel a_Model,
            GENESIS_Time _Time,
            Grids_GridDoubleFactory _Grid2DSquareCellDoubleFactory,
            Grids_GridDouble _World_Grid2DSquareCellDouble,
            boolean handleOutOfMemoryError) {
        this.AbstractModel = a_Model;
        this.Time = new GENESIS_Time(_Time);
        this.NetworkGridDoubleFactory = _Grid2DSquareCellDoubleFactory;
        this.HOOME = handleOutOfMemoryError;
    }

    public GENESIS_AbstractModelTraffic getTrafficModel() {
        if (AbstractModel instanceof GENESIS_AbstractModelTraffic) {
            return (GENESIS_AbstractModelTraffic) AbstractModel;
        }
        return null;
    }

    public Vector_Envelope2D get_reporting_VectorEnvelope2D() {
        if (ReportingEnvelope == null) {
            //BigDecimal[] a_Grid2DSquareCell_Dimensions = ReportingPopulationDensityGridDouble.get_Dimensions(HOOME);
            Grids_Dimensions dimensions;
            dimensions = ReportingPopulationDensityGridDouble.getDimensions();
            Vector_Point2D a_VectorPoint2D = new Vector_Point2D(
                    ve,
                    dimensions.getXMin(),
                    dimensions.getYMin());
            Vector_Point2D b_VectorPoint2D = new Vector_Point2D(
                    ve,
                    dimensions.getXMax(),
                    dimensions.getYMax());
            Vector_Envelope2D a_VectorEnvelope2D = new Vector_Envelope2D(
                    a_VectorPoint2D);
            Vector_Envelope2D b_VectorEnvelope2D = new Vector_Envelope2D(
                    b_VectorPoint2D);
            ReportingEnvelope = a_VectorEnvelope2D.envelope(b_VectorEnvelope2D);
        }
        return ReportingEnvelope;
    }

    public long initMemoryReserve_AccountAgentCollections(
            boolean hoome) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountAgentCollections(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = AgentEnvironment.AgentCollectionManager.swapToFile_AgentCollection_Account(HOOMEF);
                if (result < 1) {
                    swapChunk();
                }
                result += initMemoryReserve_AccountAgentCollections(hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @return
     */
    //public HashSet<Long> initMemoryReserve_AccountDetailAgentCollections(
    public Object[] initMemoryReserve_AccountDetailAgentCollections(
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            return tryToEnsureThereIsEnoughMemoryToContinue_AccountDetailAgentCollections(
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                Object[] result = AgentEnvironment.AgentCollectionManager.swapToFile_AgentCollection_AccountDetail(HOOMEF);
                if (!(Boolean) result[0]) {
                    swapChunk();
                }
                Object[] potentialPartResult = initMemoryReserve_AccountDetailAgentCollections(
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

    public final void initMemoryReserve(
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            a_GENESIS_FemaleCollection.ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                swapDataAnyExcept(a_GENESIS_FemaleCollection);
                initMemoryReserve(
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public final void initMemoryReserve(
            GENESIS_MaleCollection a_GENESIS_MaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            initMemoryReserve();
            a_GENESIS_MaleCollection.ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                swapDataAnyExcept(a_GENESIS_MaleCollection);
                initMemoryReserve(
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
//    public final void initMemoryReserve(
//            long a_AgentCollection_ID,
//            boolean handleOutOfMemoryError) {
//        try {
//            GENESIS_AgentCollection a_GENESIS_AgentCollection = AgentEnvironment._GENESIS_AgentCollectionManager.getAgentCollection(
//                    a_AgentCollection_ID,
//                    handleOutOfMemoryError);
//            initMemoryReserve(
//                    a_GENESIS_AgentCollection,
//                    handleOutOfMemoryError);
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                clearMemoryReserve();
//                GENESIS_AgentCollection a_GENESIS_AgentCollection = AgentEnvironment._GENESIS_AgentCollectionManager.getAgentCollection(
//                        a_AgentCollection_ID,
//                        HOOMEF);
//                if (AgentEnvironment._GENESIS_AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                        a_GENESIS_AgentCollection) < 1) {
//                    swapChunk();
//                }
//                initMemoryReserve(
//                        a_GENESIS_AgentCollection,
//                        handleOutOfMemoryError);
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
    @Override
    public boolean swapDataAny(boolean handleOutOfMemoryError) {
        try {
            boolean result = swapDataAny();
            checkAndMaybeFreeMemory();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                clearMemoryReserve();
                boolean result = swapDataAny(HOOMEF);
                initMemoryReserve();
                return result;
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Override
    public boolean swapDataAny() {
        long swapChunk_Account = 0;
        // Probably better to query here and know there is nothing to swap out
        // instead of an expensive try and fail
        try {
            swapChunk_Account += swapChunk_Account();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            log(
                    "Caught OutOfMemoryError in "
                    + this.getClass().getName() + ".swapDataAny()");
        }
        if (swapChunk_Account < 1) {
            if (swapToFile_AgentCollection_Account() < 1) {
                log(
                        "Found no data to swap in "
                        + this.getClass().getName()
                        + ".swapDataAny()");
//                throw new OutOfMemoryError();
                return false;
            }
        }
        return true;
    }

    public boolean swapDataAnyExcept(
            GENESIS_FemaleCollection fc,
            boolean hoome) {
        try {
            boolean result = swapDataAnyExcept(                    fc);
            tryToEnsureThereIsEnoughMemoryToContinue(fc,hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                boolean result = swapDataAnyExcept(                        fc);
                initMemoryReserve();
                return result;
            } else {
                throw e;
            }
        }
    }

    public boolean swapDataAnyExcept(
            GENESIS_MaleCollection mc,
            boolean hoome) {
        try {
            boolean result = swapDataAnyExcept(                    mc);
            tryToEnsureThereIsEnoughMemoryToContinue(mc,hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                boolean result = swapDataAnyExcept(                        mc);
                initMemoryReserve();
                return result;
            } else {
                throw e;
            }
        }
    }

    protected boolean swapDataAnyExcept(
            GENESIS_MaleCollection mc) {
        long swapChunk_Account = 0;
        try {
            swapChunk_Account += swapChunk_Account();
        } catch (OutOfMemoryError e) {
            log(
                    "Caught OutOfMemoryError in "
                    + this.getClass().getName() + ".swapDataAnyExcept(GENESIS_MaleCollection)");
        }
        if (swapChunk_Account < 1) {
            if (AgentEnvironment.AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(
                    mc) < 1) {
                if (AgentEnvironment.AgentCollectionManager.swapToFile_FemaleCollection_Account() < 1) {
                    log(
                            "Found no data to swap in "
                            + this.getClass().getName()
                            + ".swapDataAnyExcept(GENESIS_MaleCollection)");
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean swapDataAnyExcept(
            GENESIS_FemaleCollection fc) {
        long swapChunk_Account = 0;
        try {
            swapChunk_Account += swapChunk_Account();
        } catch (OutOfMemoryError e) {
            log(
                    "Caught OutOfMemoryError in "
                    + this.getClass().getName() + ".swapDataAnyExcept(GENESIS_FemaleCollection)");
        }
        if (swapChunk_Account < 1) {
            if (AgentEnvironment.AgentCollectionManager.swapToFile_MaleCollection_Account() < 1) {
                if (AgentEnvironment.AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
                        fc) < 1) {
                    log(
                            "Found no data to swap in "
                            + this.getClass().getName()
                            + ".swapDataAnyExcept(GENESIS_FemaleCollection)");
                    return false;
                }
            }
        }
        return true;
    }

//    protected boolean swapDataAnyExcept(
//            GENESIS_AgentCollection a_GENESIS_AgentCollection) {
//        long swapChunk_Account = 0;
//        try {
//            swapChunk_Account += swapChunk_Account();
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            log(
//                    "Caught OutOfMemoryError in "
//                    + this.getClass().getName() + ".swapDataAnyExcept()");
//        }
//        if (swapChunk_Account < 1) {
//            if (AgentEnvironment._GENESIS_AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                    a_GENESIS_AgentCollection) < 1) {
//                log(
//                        "Found no data to swap in "
//                        + this.getClass().getName()
//                        + ".swapDataAnyExcept()");
//                return false;
//            }
//        }
//        return true;
//    }
    public void swapToFile_Data() {
        swapChunks();
        AgentEnvironment.AgentCollectionManager.swapToFile_AgentCollections();
    }

    public void swapChunks(
            boolean hoome) {
        try {
            swapChunks();
            checkAndMaybeFreeMemory();
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                if (swapChunks_Account() < 1) {
                    AgentEnvironment.AgentCollectionManager.swapAgentCollection();
                }
                initMemoryReserve();
            } else {
                throw e;
            }
        }
    }

    protected void swapChunks() {
        ge.swapChunks(HOOMEF);
    }

    public long swapChunks_Account(
            boolean hoome) {
        try {
            long result = swapChunks_Account();
            checkAndMaybeFreeMemory();
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = swapChunk_Account();
                if (result < 1) {
                    AgentEnvironment.AgentCollectionManager.swapAgentCollection();
                }
                initMemoryReserve();
                result += swapChunks_Account(                        hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    protected long swapChunks_Account() {
        return ge.swapChunks_Account(HOOMEF);
    }

    protected long swapChunk_Account() {
        if (ge.isDataToSwap()) {
            if (ge.swapChunk(HOOMEF)) {
                return 1L;
            }
        }
        return 0L;
    }

    public void swapChunk(
            boolean hoome) {
        try {
            swapChunk();
            checkAndMaybeFreeMemory();
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = swapChunk_Account();
                if (result < 1) {
                    AgentEnvironment.AgentCollectionManager.swapAgentCollection();
                }
                initMemoryReserve();
            } else {
                throw e;
            }
        }
    }

    /**
     * Not needed, but makes code tidier and probably adds little in terms of
     * overhead...
     */
    protected void swapChunk() {
        ge.swapChunk(HOOMEF);
    }

    public void swapToFile_AgentCollections(
            boolean hoome) {
        try {
            AgentEnvironment.AgentCollectionManager.swapToFile_AgentCollections();
            checkAndMaybeFreeMemory();
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                if (swapToFile_AgentCollection_Account() < 1) {
                    swapChunk();
                }
                initMemoryReserve();
            } else {
                throw e;
            }
        }
    }

    public long swapToFile_AgentCollections_Account(
            boolean hoome) {
        try {
            long result = swapToFile_AgentCollections_Account();
            checkAndMaybeFreeMemory();
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = swapToFile_AgentCollections_Account();
                initMemoryReserve();
                return result;
            } else {
                throw e;
            }
        }
    }

    protected long swapToFile_AgentCollections_Account() {
        return AgentEnvironment.AgentCollectionManager.swapToFile_AgentCollections_Account();
    }

    public long swapToFile_AgentCollection_Account(
            boolean hoome) {
        try {
            long result = swapToFile_AgentCollection_Account();
            checkAndMaybeFreeMemory();
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                long result = swapToFile_AgentCollection_Account();
                if (result < 1) {
                    if (swapChunk_Account() < 1) {
                        throw e;
                    }
                }
                initMemoryReserve();
                return result;
            } else {
                throw e;
            }
        }
    }

    protected long swapToFile_AgentCollection_Account() {
        return AgentEnvironment.AgentCollectionManager.swapAgentCollection_Account();
    }

    /**
     * A method to try to ensure there is enough memory to continue.
     *
     * @return
     */
    @Override
    public boolean checkAndMaybeFreeMemory() {
        while (getTotalFreeMemory() < Memory_Threshold) {
            if (!swapDataAny()) {
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
//    public void checkAndMaybeFreeMemory(
//            GENESIS_AgentCollection a_GENESIS_AgentCollection,
//            boolean handleOutOfMemoryError) {
//        try {
//            if (!checkAndMaybeFreeMemory(
//                    a_GENESIS_AgentCollection)) {
//                String message = new String(
//                        "Warning! Not enough data to swap in "
//                        + this.getClass().getName()
//                        + ".checkAndMaybeFreeMemory(GENESIS_AgentCollection,boolean)");
//                log(message);
//                // Set to exit method with OutOfMemoryError
//                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError();
//            }
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                clearMemoryReserve();
//                if (AgentEnvironment._GENESIS_AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                        a_GENESIS_AgentCollection,
//                        HOOMEF) < 1) {
//                    if (swapChunk_Account() < 1) {
//                        String message = new String(
//                                "Warning! Not enough data to swap in "
//                                + this.getClass().getName()
//                                + ".checkAndMaybeFreeMemory(GENESIS_AgentCollection,boolean)");
//                        log(message);
//                        throw a_OutOfMemoryError;
//                    }
//                }
//                boolean createdRoom = false;
//                while (!createdRoom) {
//                    if (!swapDataAny()) {
//                        throw a_OutOfMemoryError;
//                    }
//                    try {
//                        initMemoryReserve(
//                                a_GENESIS_AgentCollection,
//                                HOOMEF);
//                        createdRoom = true;
//                    } catch (OutOfMemoryError b_OutOfMemoryError) {
//                        log(
//                                "Struggling to ensure there is enough memory in "
//                                + this.getClass().getName()
//                                + ".checkAndMaybeFreeMemory(GENESIS_AgentCollection,boolean)");
//                    }
//                }
//                checkAndMaybeFreeMemory(
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
                if (AgentEnvironment.AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(a_GENESIS_FemaleCollection,
                        HOOMEF) < 1) {
                    if (swapChunk_Account() < 1) {
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
                    if (!swapDataAny()) {
                        throw a_OutOfMemoryError;
                    }
                    try {
                        initMemoryReserve(a_GENESIS_FemaleCollection,
                                HOOMEF);
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
                if (AgentEnvironment.AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(a_GENESIS_MaleCollection,
                        HOOMEF) < 1) {
                    if (swapChunk_Account() < 1) {
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
                    if (!swapDataAny()) {
                        throw a_OutOfMemoryError;
                    }
                    try {
                        initMemoryReserve(a_GENESIS_MaleCollection,
                                HOOMEF);
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
//            if (!AgentEnvironment.AgentCollectionManager._MaleCollection_HashMap.isEmpty()) {
//                swappedMaleCollections = AgentEnvironment.AgentCollectionManager.swapToFile_MaleCollection_Account();
//            }
//            if (swappedMaleCollections < 1) {
            if (AgentEnvironment.AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
                    a_FemaleCollection) < 1) {
                if (AgentEnvironment.AgentCollectionManager.swapToFile_MaleCollection_Account() < 1) {
                    if (swapChunk_Account() < 1) {
                        return false;
                    }
                }
            }
//            if (AgentEnvironment.AgentCollectionManager.swapToFile_MaleCollection_Account() < 1){
//                if (AgentEnvironment.AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
//                        a_FemaleCollection) < 1) {
//                    if (swapChunk_Account() < 1) {
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
            if (AgentEnvironment.AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(
                    a_MaleCollection) < 1) {
                if (AgentEnvironment.AgentCollectionManager.swapToFile_FemaleCollection_Account() < 1) {
                    if (swapChunk_Account() < 1) {
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
//    public void checkAndMaybeFreeMemory(
//            long a_AgentCollection_ID,
//            boolean handleOutOfMemoryError) {
//        try {
//            GENESIS_AgentCollection a_GENESIS_AgentCollection =
//                    AgentEnvironment._GENESIS_AgentCollectionManager.getAgentCollection(
//                    a_AgentCollection_ID,
//                    HOOMEF);
//            if (!checkAndMaybeFreeMemory(
//                    a_GENESIS_AgentCollection)) {
//                String message = new String(
//                        "Warning! Not enough data to swap in "
//                        + this.getClass().getName()
//                        + ".checkAndMaybeFreeMemory(long,boolean)");
//                log(message);
//                // Set to exit method with OutOfMemoryError
//                handleOutOfMemoryError = false;
//                throw new OutOfMemoryError();
//            }
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                clearMemoryReserve();
//                GENESIS_AgentCollection a_GENESIS_AgentCollection =
//                        AgentEnvironment._GENESIS_AgentCollectionManager.getAgentCollection(
//                        a_AgentCollection_ID,
//                        HOOMEF);
//                if (AgentEnvironment._GENESIS_AgentCollectionManager.swapToFile_AgentCollectionExcept_Account(
//                        a_GENESIS_AgentCollection,
//                        HOOMEF) < 1) {
//                    if (swapChunk_Account() < 1) {
//                        String message = new String(
//                                "Warning! Not enough data to swap in "
//                                + this.getClass().getName()
//                                + ".checkAndMaybeFreeMemory(long,boolean)");
//                        log(message);
//                        throw a_OutOfMemoryError;
//                    }
//                }
//                boolean createdRoom = false;
//                while (!createdRoom) {
//                    if (!swapDataAny()) {
//                        throw a_OutOfMemoryError;
//                    }
//                    try {
//                        initMemoryReserve(
//                                a_GENESIS_AgentCollection,
//                                HOOMEF);
//                        createdRoom = true;
//                    } catch (OutOfMemoryError b_OutOfMemoryError) {
//                        log(
//                                "Struggling to ensure there is enough memory in "
//                                + this.getClass().getName()
//                                + ".checkAndMaybeFreeMemory(long,boolean)");
//                    }
//                }
//                checkAndMaybeFreeMemory(
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
                    if (swapChunk_Account() < 1) {
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
                    if (!swapDataAny()) {
                        throw a_OutOfMemoryError;
                    }
                    try {
                        result += initMemoryReserve_AccountAgentCollections(HOOMEF);
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
                    long b_long = swapChunk_Account();
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
                GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager = AgentEnvironment.getAgentCollectionManager();
                Object[] result = a_GENESIS_AgentCollectionManager.swapToFile_MaleCollection_AccountDetail(HOOMEF);
                if (!(Boolean) result[0]) {
                    Object[] potentialPartResult = a_GENESIS_AgentCollectionManager.swapToFile_FemaleCollection_AccountDetail(HOOMEF);
                    if (!(Boolean) potentialPartResult[0]) {
                        if (swapChunk_Account() < 1) {
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
                potentialPartResult = AgentEnvironment.AgentCollectionManager.swapToFile_AgentCollection_AccountDetail(HOOMEF);
                if (potentialPartResult == null) {
                    if (swapChunk_Account() < 1) {
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
     * @param hoome
     * @return Directory
     */
    public File get_Directory(
            boolean hoome) {
        try {
            File result = get_Directory();
            checkAndMaybeFreeMemory();
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                clearMemoryReserve();
                swapDataAny();
                initMemoryReserve();
                return get_Directory(                        hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Directory
     */
    protected File get_Directory() {
        return Directory;
    }

    private static void log(
            String message) {
        log(GENESIS_Log.GENESIS_DefaultLogLevel, message);
    }

    private static void log(
            Level level,
            String message) {
        Logger.getLogger(GENESIS_Log.DefaultLoggerName).log(level, message);
    }

    public GENESIS_Files getGENESIS_Files() {
        return Files;
    }

    public GENESIS_Grids getGENESIS_Grids() {
        return Grids;
    }

}
