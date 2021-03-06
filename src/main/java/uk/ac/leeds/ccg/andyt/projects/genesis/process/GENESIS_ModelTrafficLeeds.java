package uk.ac.leeds.ccg.andyt.projects.genesis.process;

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleMapFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import java.io.File;
import java.io.InvalidClassException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.andyt.generic.memory.Generic_TestMemory;
import uk.ac.leeds.ccg.andyt.generic.util.Generic_Time;
import uk.ac.leeds.ccg.andyt.grids.core.*;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.*;
import uk.ac.leeds.ccg.andyt.projects.genesis.grids.GENESIS_Grids;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.GENESIS_Household;
import uk.ac.leeds.ccg.andyt.projects.genesis.travelingsalesman.GENESIS_TravelingSalesman;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.census.cas.Census_CASAreaEastingNorthingDataHandler;
import uk.ac.leeds.ccg.andyt.census.cas.Census_CASAreaEastingNorthingDataRecord;
import uk.ac.leeds.ccg.andyt.census.sws.Census_SWSDataHandler;
import uk.ac.leeds.ccg.andyt.census.sws.Census_SWSDataRecord;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStatsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Network2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;
import uk.ac.leeds.ccg.andyt.vector.projection.Vector_OSGBtoLatLon;

public class GENESIS_ModelTrafficLeeds extends GENESIS_AbstractModelTraffic {

    static final long serialVersionUID = 1L;

    /**
     * A class to generate a society Commuting to work in Leeds SWS.
     */
    protected GENESIS_ModelTrafficLeeds() {
    }

    public GENESIS_ModelTrafficLeeds(GENESIS_Environment ge) {
        super(ge);
    }

    public static void main(String[] args) {
        // Model Configuration Parameters
        //String area_String = "00DA";
        String area_String = "00DAFA";
        BigDecimal minx_BigDecimal = new BigDecimal("-1.8");
        BigDecimal miny_BigDecimal = new BigDecimal("53.6");
        // Network resolution
        BigDecimal networkCellsize_BigDecimal = new BigDecimal("0.00001");
        long networkNRows = 40000;//40000;//360;//720;//2048;//1024;//2048;//4096;//768;//512;//256;//128;//512;//128;//256;//128;//512;//64;//128;//256;//512;
        long networkNCols = 60000;//00;//183;//640;//1280;//2048;//1024;//2048;//4096;//768;//512;//128;//512;//256;//1024;//128;//256;//512;//1024;
        // Reporting grid resolution
        BigDecimal reportingCellsize_BigDecimal = new BigDecimal("0.0001");
        //long reportingNRows = 400;
        //long reportingNCols = 600;

//        File directory = new File(
//                "C:/Work/Projects/GENESIS/workspace/" + GENESIS_ModelTrafficLeeds.class.getName());
//        File map_File = new File("C:/Work/data/Open/OSM/Leeds/map.osm");
        File directory = new File(
                "/scratch01/Work/Projects/GENESIS/workspace/" + GENESIS_ModelTrafficLeeds.class.getName());
        File map_File = new File("/scratch01/Work/data/Open/OSM/Leeds/map.osm");
        int randomSeed = 0;
        long maximumNumberOfAgents = 1000000L; //10000000;
        int maximumNumberOfAgentsPerAgentCollection = 1000; //1000;
        int maximumNumberOfObjectsPerDirectory = 100;
        new GENESIS_ModelTrafficLeeds().run(
                area_String,
                minx_BigDecimal,
                miny_BigDecimal,
                networkCellsize_BigDecimal,
                networkNRows,
                networkNCols,
                reportingCellsize_BigDecimal,
                directory,
                map_File,
                randomSeed,
                maximumNumberOfAgents,
                maximumNumberOfAgentsPerAgentCollection,
                maximumNumberOfObjectsPerDirectory);
    }

    public void run(
            String area_String,
            BigDecimal minx_BigDecimal,
            BigDecimal miny_BigDecimal,
            BigDecimal networkCellsize_BigDecimal,
            long networkNRows_long,
            long networkNCols_long,
            BigDecimal reportingCellsize_BigDecimal,
            File aDirectory_File,
            File aMap_File,
            long aRandomSeed,
            long aMaximumNumberOfAgents_long,
            int aMaximumNumberOfAgentsPerAgentCollection,
            int aMaximumNumberOfObjectsPerDirectory) {
        Generic_TestMemory gtm = new Generic_TestMemory();
        System.out.println("_TestMemory.getTotalFreeMemory() " + gtm.getTotalFreeMemory());
        // Initialisation
        System.out.println("Initialise Environment");
        init_RandomArrayMinLength(
                0,
                aRandomSeed,
                1L);
        ge = new GENESIS_Environment(
                aDirectory_File,
                this,
                new GENESIS_Time(0, 0));
        ge.initMemoryReserve();
        ge.DecimalPlacePrecisionForCalculations = 10;
        int[] memoryReserve = ge.getMemoryReserve();
        ge.Generic_TestMemory = gtm;
        ge.Directory = aDirectory_File;
        boolean handleOutOfMemoryError = true;
        this.HandleOutOfMemoryError = handleOutOfMemoryError;
        ge.HOOME = handleOutOfMemoryError;
        // Initialise AgentEnvironment._AbstractGrid2DSquareCell_HashSet
        ge.ge = new Grids_Environment(ge.getGENESIS_Files().getGridsDirectory());
        // Initialise network_Grid2DSquareCellDoubleFactory and network_Grid2DSquareCellDouble
        BigDecimal networkNRows_BigDecimal = new BigDecimal(networkNRows_long);
        BigDecimal networkNCols_BigDecimal = new BigDecimal(networkNCols_long);
        initNetwork_Grid2DSquareCellDoubleFactory_Grid2DSquareCellDouble(
                aDirectory_File,
                networkNRows_long,
                networkNCols_long,
                networkNRows_BigDecimal,
                networkNCols_BigDecimal,
                networkCellsize_BigDecimal,
                minx_BigDecimal,
                miny_BigDecimal,
                memoryReserve,
                handleOutOfMemoryError);
        System.out.println("Initialised Network");
        // Initialise reporting_Grid2DSquareCellDoubleFactory, reportingPopulationDenisty_Grid2DSquareCellDouble and reportingPopulationDenistyAggregate_Grid2DSquareCellDouble
        Grids_Dimensions network_Dimensions = ge.NetworkGridDouble.getDimensions();
        initReporting_Grid2DSquareCellDoubleFactory_Grid2DSquareCellDouble(
                aDirectory_File,
                networkNRows_BigDecimal,
                networkNCols_BigDecimal,
                network_Dimensions,
                reportingCellsize_BigDecimal,
                minx_BigDecimal,
                miny_BigDecimal,
                memoryReserve,
                handleOutOfMemoryError);
        System.out.println("Initialised Reporting Raster");
        //All initialisation needs to be able to cope with OutOfMemoryError
        //Need to consider carefully when ensureThereIsEnoughMemory to continue is called for efficiency reasons.
        // Initialise rounding and decimal place precision variables
        init_Rounding(
                networkCellsize_BigDecimal,
                minx_BigDecimal,
                miny_BigDecimal,
                handleOutOfMemoryError);
        init_DecimalPlacePrecision(handleOutOfMemoryError);

        // Initialise _GENESIS_Environment.AgentEnvironment
        init_AgentCollectionManager(
                aDirectory_File,
                aMaximumNumberOfAgents_long,
                aMaximumNumberOfAgentsPerAgentCollection,
                aMaximumNumberOfObjectsPerDirectory,
                handleOutOfMemoryError);

        // Initialise AgentEnvironment.TS
        init_TSMisc(
                aMap_File,
                handleOutOfMemoryError);

        System.out.println("_TestMemory.getTotalFreeMemory() " + gtm.getTotalFreeMemory());
        // Initialise Shifts
        init_Shifts(handleOutOfMemoryError);
        System.out.println("Initialised Shifts");

        // Initialise _ImageExporter
        init_ImageExporter(handleOutOfMemoryError);
        System.out.println("Initialised Image Exporter");

        // Initialise Population
        init_Population_HashSet(
                reportingCellsize_BigDecimal,
                networkCellsize_BigDecimal,
                network_Dimensions,
                aDirectory_File,
                area_String,
                handleOutOfMemoryError);
        System.out.println("Initialised Population");

        simulate();

        visualiseNetworkOnGrid1(ge.ReportingPopulationDensityAggregateGridDouble,
                ge.get_reporting_VectorEnvelope2D());
    }

    protected Census_CASAreaEastingNorthingDataHandler init_CASAreaEastingNorthingDataHandler(
            boolean handleOutOfMemoryError) {
        try {
            // CASAreaEastingNorthingDataRecords
            Census_CASAreaEastingNorthingDataHandler a_CASAreaEastingNorthingDataHandler;
            a_CASAreaEastingNorthingDataHandler = new Census_CASAreaEastingNorthingDataHandler();
//        file = new File("C:/Work/data/Census/2001/OA/");//England_OA_ZoneCode_Area_Easting_Northing.csv");
//        a_CASAreaEastingNorthingDataHandler.formatSourceData(file, 20);
            File file = new File(
                    ge.Directory.getParentFile(),
                    Census_CASAreaEastingNorthingDataHandler.class.getCanonicalName() + ".thisFile");
            Generic_IO.writeObject(a_CASAreaEastingNorthingDataHandler, file);
            ge.checkAndMaybeFreeMemory();
            return a_CASAreaEastingNorthingDataHandler;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve();
                return init_CASAreaEastingNorthingDataHandler(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    // SWSDataRecords
    protected Census_SWSDataHandler init_SWSDataHandler(
            boolean handleOutOfMemoryError) {
        try {
            File file = new File(
                    ge.Directory.getParentFile(),
                    Census_SWSDataHandler.class.getCanonicalName() + ".thisFile");
            Census_SWSDataHandler a_SWSDataHandler = new Census_SWSDataHandler();
            Generic_IO.writeObject(a_SWSDataHandler, file);
            System.out.println("a_SWSDataHandler.getNDataRecords() " + a_SWSDataHandler.getNDataRecords());
            Census_SWSDataRecord a_SWSDataRecord = (Census_SWSDataRecord) a_SWSDataHandler.getDataRecord(0L);
            System.out.println("a_SWSDataRecord " + a_SWSDataRecord);
            ge.checkAndMaybeFreeMemory();
            return a_SWSDataHandler;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve();
                return init_SWSDataHandler(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    // Initialise Population
    protected void init_Population_HashSet(
            BigDecimal reportingCellsize_BigDecimal,
            BigDecimal networkCellsize_BigDecimal,
            Grids_Dimensions network_Dimensions,
            File aDirectory_File,
            String area_String,
            boolean handleOutOfMemoryError) {
        try {
            System.out.println("Initialise Population");
            _SpeedDefault_BigDecimal = BigDecimal.TEN.multiply(reportingCellsize_BigDecimal.divide(networkCellsize_BigDecimal).multiply(network_Dimensions.getCellsize()));
            _PersonFactory = new GENESIS_PersonFactory(
                    ge,
                    _GENESIS_AgentCollectionManager);
            Long a_AgentCollection_ID = 0L;
            String a_Agent_Type = GENESIS_Agent.class.getName();
            GENESIS_FemaleCollection a_FemaleCollection
                    = new GENESIS_FemaleCollection(
                            ge,
                            a_AgentCollection_ID,
                            GENESIS_Person.getTypeLivingFemale_String());
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager
                    = ge.AgentEnvironment.get_AgentCollectionManager(
                            handleOutOfMemoryError);
            a_GENESIS_AgentCollectionManager.LivingFemales.put(
                    a_AgentCollection_ID,
                    a_FemaleCollection);
            // Initialise Agent cache
            //Don't always need to initialise file cache
            File directory_Agents = new File(aDirectory_File, "Agents");
            directory_Agents.mkdirs();
//        AgentEnvironment.AgentCollectionManager.initialise_FileCache(
//                AgentEnvironment.AgentCollectionManager._MaximumNumberOfAgents,
//                AgentEnvironment.AgentCollectionManager.MaximumNumberOfObjectsPerDirectory,//64,
//                directory_Agents);
//            a_GENESIS_AgentCollectionManager.delete_FileCache(aDirectory_File);
            //_GENESIS_AgentEnvironment.AgentCollectionManager.delete_FileCache(new File(directory.toString() + "old"));
            // Initialise AgentCollection cache
            File directory_AgentCollections = new File(aDirectory_File, "AgentCollections");
            directory_AgentCollections.mkdirs();
            File SWSDataHandler_File = new File(aDirectory_File, Census_SWSDataHandler.class.getName() + ".thisFile");
            //SWSDataHandler a_SWSDataHandler = getSWSDataHandler(SWSDataHandler_File);
            Census_SWSDataHandler a_SWSDataHandler = new Census_SWSDataHandler();
            File SWSDataFile = new File(aDirectory_File, "SWSDataRecords.dat");
            a_SWSDataHandler.setFile(SWSDataFile);
            Generic_IO.writeObject(a_SWSDataHandler, SWSDataHandler_File);
            Census_CASAreaEastingNorthingDataHandler a_CASAreaEastingNorthingDataHandler;
            a_CASAreaEastingNorthingDataHandler = init_CASAreaEastingNorthingDataHandler(
                    handleOutOfMemoryError);
            _FemalePopulation_HashSet = new HashSet<>();
            _MalePopulation_HashSet = new HashSet<>();
            HashMap a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap = a_CASAreaEastingNorthingDataHandler.get_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap(area_String);
            long nDataRecords = a_SWSDataHandler.getNDataRecords();

            System.out.println("nDataRecords should be non-zero...");

            init_Population_HashSet(
                    area_String,
                    a_SWSDataHandler,
                    a_CASAreaEastingNorthingDataHandler,
                    a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap,
                    nDataRecords,
                    a_FemaleCollection,
                    handleOutOfMemoryError);
            ge.checkAndMaybeFreeMemory();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve();
                init_Population_HashSet(
                        reportingCellsize_BigDecimal,
                        networkCellsize_BigDecimal,
                        network_Dimensions,
                        aDirectory_File,
                        area_String,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected void init_ImageExporter(
            boolean handleOutOfMemoryError) {
        try {
            _ImageExporter = new Grids_ImageExporter(ge.ge);
            ge.checkAndMaybeFreeMemory();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve();
                init_ImageExporter(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected void init_TSMisc(
            File aMap_File,
            boolean handleOutOfMemoryError) {
        try {
            ge.TS = new GENESIS_TravelingSalesman(
                    ge,
                    aMap_File);
            ge.checkAndMaybeFreeMemory();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.swapDataAny();
                ge.initMemoryReserve();
                init_TSMisc(
                        aMap_File,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected void init_AgentCollectionManager(
            File aDirectory_File,
            long aMaximumNumberOfAgents_long,
            int aMaximumNumberOfAgentsPerAgentCollection,
            int aMaximumNumberOfObjectsPerDirectory,
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_AgentEnvironment a_GENESIS_AgentEnvironment = new GENESIS_AgentEnvironment(ge);
            _GENESIS_AgentCollectionManager = new GENESIS_AgentCollectionManager(
                    ge,
                    aDirectory_File);
            _GENESIS_AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection = aMaximumNumberOfAgentsPerAgentCollection;
            _GENESIS_AgentCollectionManager.MaximumNumberOfObjectsPerDirectory = aMaximumNumberOfObjectsPerDirectory;
            a_GENESIS_AgentEnvironment.set_AgentCollectionManager(
                    _GENESIS_AgentCollectionManager,
                    handleOutOfMemoryError);
            ge.AgentEnvironment = a_GENESIS_AgentEnvironment;
            ge.checkAndMaybeFreeMemory();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve();
                init_AgentCollectionManager(
                        aDirectory_File,
                        aMaximumNumberOfAgents_long,
                        aMaximumNumberOfAgentsPerAgentCollection,
                        aMaximumNumberOfObjectsPerDirectory,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected void init_DecimalPlacePrecision(
            boolean handleOutOfMemoryError) {
        try {
            ge.DecimalPlacePrecisionForNetwork = Math.min(ge.DecimalPlacePrecisionForCalculations,
                    Math.max(ge._ToRoundToX_BigDecimal.scale(),
                            ge._ToRoundToY_BigDecimal.scale()));
            ge.DecimalPlacePrecisionForNetworkCalculations = ge.DecimalPlacePrecisionForNetwork + 2;
            ge.checkAndMaybeFreeMemory();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve();
                init_DecimalPlacePrecision(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Initialise network_Grid2DSquareCellDoubleFactory and
     * network_Grid2DSquareCellDouble
     *
     * @param handleOutOfMemoryError
     * @param memoryReserve
     * @param miny
     */
    public void initNetwork_Grid2DSquareCellDoubleFactory_Grid2DSquareCellDouble(
            File dir,
            long networkNRows_long,
            long networkNCols_long,
            BigDecimal networkNRows,
            BigDecimal networkNCols,
            BigDecimal reportingCellsize,
            BigDecimal minx,
            BigDecimal miny,
            int[] memoryReserve,
            boolean handleOutOfMemoryError) {
        // Initialise AgentEnvironment.NetworkGridDoubleFactory
        int networkChunkNCols = 512;
        int networkChunkNRows = 512;
        Grids_AbstractGridChunkDoubleFactory network_GDCF;
        network_GDCF = new Grids_GridChunkDoubleMapFactory();
        Grids_Environment gridsEnv;
        gridsEnv = ge.ge;
        Grids_Processor gp;
        gp = gridsEnv.getProcessor();
        ge.NetworkGridDoubleFactory = new Grids_GridDoubleFactory(
                gridsEnv,
                gp.GridChunkDoubleFactory,
                network_GDCF,
                //gp.DefaultGridChunkDoubleFactory,
                -Double.MAX_VALUE,
                networkChunkNRows,
                networkChunkNCols,
                new Grids_Dimensions(networkNRows_long, networkNCols_long),
                new Grids_GridDoubleStatsNotUpdated(ge.ge));
        Grids_Dimensions network_Dimensions = new Grids_Dimensions(
                reportingCellsize,
                minx,
                miny,
                (networkNCols.multiply(reportingCellsize)).add(minx),
                (networkNRows.multiply(reportingCellsize)).add(miny));
        ge.NetworkGridDoubleFactory.setDimensions(network_Dimensions);
        //_GENESIS_Environment.NetworkGridDoubleFactory.setMemoryReserve(memoryReserve);
        //network_Grid2DSquareCellDoubleFactory.initMemoryReserve(handleOutOfMemoryError);
        // Initialise AgentEnvironment.NetworkGridDouble
        ge.NetworkGridDouble = (Grids_GridDouble) ge.NetworkGridDoubleFactory.create(
                new File(dir, "Network"),
                networkNRows_long,
                networkNCols_long,
                network_Dimensions);
        // Swapped here as they contain so little data that this makes sense in terms of memory handling
        ge.NetworkGridDouble.swapChunks(handleOutOfMemoryError);
    }

    public void initReporting_Grid2DSquareCellDoubleFactory_Grid2DSquareCellDouble(
            File aDirectory_File,
            BigDecimal networkNRows_BigDecimal,
            BigDecimal networkNCols_BigDecimal,
            Grids_Dimensions network_Dimensions,
            BigDecimal reportingCellsize,
            BigDecimal minX,
            BigDecimal minY,
            int[] memoryReserve,
            boolean handleOutOfMemoryError) {
        // Initialise AgentEnvironment.ReportingGridDoubleFactory
        int reportingChunkNCols = 512;
        int reportingChunkNRows = 512;
        Grids_AbstractGridChunkDoubleFactory reportingChunkFactory = new Grids_GridChunkDoubleMapFactory();
        Grids_Environment gridsEnv;
        gridsEnv = ge.ge;
        Grids_Processor gp;
        gp = gridsEnv.getProcessor();
        ge.ReportingGridDoubleFactory = new Grids_GridDoubleFactory(
                gridsEnv,
                gp.GridChunkDoubleFactory,
                reportingChunkFactory,
                //gp.DefaultGridChunkDoubleFactory,
                -Double.MAX_VALUE,
                reportingChunkNRows,
                reportingChunkNCols,
                network_Dimensions,
                new Grids_GridDoubleStatsNotUpdated(gridsEnv));
        BigDecimal maxX;
        BigDecimal maxY;
        BigDecimal reportingScaleGeneralisation = network_Dimensions.getCellsize().divide(reportingCellsize);
        BigDecimal reportingNRows_BigDecimal = networkNRows_BigDecimal.multiply(reportingScaleGeneralisation);
        long reportingNRows_long = reportingNRows_BigDecimal.longValueExact();
        BigDecimal reportingNCols_BigDecimal = networkNCols_BigDecimal.multiply(reportingScaleGeneralisation);
        long reportingNCols_long = reportingNCols_BigDecimal.longValueExact();
        maxX = (reportingNCols_BigDecimal.multiply(reportingCellsize)).add(minX);
        maxY = (reportingNRows_BigDecimal.multiply(reportingCellsize)).add(minY);
        Grids_Dimensions reporting_Dimensions = new Grids_Dimensions(
                reportingCellsize, minX, minY, maxX, maxY);
        ge.ReportingGridDoubleFactory.setDimensions(reporting_Dimensions);
        //_GENESIS_Environment.ReportingGridDoubleFactory.initMemoryReserve(handleOutOfMemoryError);
        // Initialise AgentEnvironment.ReportingPopulationDensityGridDouble
        ge.ReportingPopulationDensityGridDouble
                = (Grids_GridDouble) ge.ReportingGridDoubleFactory.create(
                        new File(aDirectory_File, "PopulationDensity"),
                        reportingNRows_long,
                        reportingNCols_long,
                        reporting_Dimensions);
        ge.ReportingPopulationDensityGridDouble.initCells(0);
        // Initialise AgentEnvironment.ReportingPopulationDensityAggregateGridDouble
        ge.ReportingPopulationDensityAggregateGridDouble
                = (Grids_GridDouble) ge.ReportingGridDoubleFactory.create(
                        new File(aDirectory_File, "PopulationDensityAggregate"),
                        reportingNRows_long, reportingNCols_long, reporting_Dimensions);
        ge.ReportingPopulationDensityAggregateGridDouble.initCells(0);
        // Initialise AgentEnvironment.ReportingPopulationDensityMovingAggregateGridDouble
        ge.ReportingPopulationDensityMovingAggregateGridDouble
                = (Grids_GridDouble) ge.ReportingGridDoubleFactory.create(
                        new File(aDirectory_File, "PopulationDensityMovingAggregate"),
                        reportingNRows_long, reportingNCols_long, reporting_Dimensions);
        ge.ReportingPopulationDensityMovingAggregateGridDouble.initCells(0);
    }

    @Override
    public void simulate() {
        System.out.println("Simulating");
        Generic_Time t = new Generic_Time();
        //String type = "PNG";
        //String type = "JPEG";
        //File file;
        int maxite = 60 * 24 * 7;//60*24*7;
        BigDecimal tollerance;
        tollerance = new BigDecimal("0.0000001");
        //int maxite = 10;
        long time0 = System.currentTimeMillis();
        GENESIS_Person a_Person = null;
        GENESIS_Grids grids = ge.getGENESIS_Grids();
        BigDecimal halfCellsize = ge.NetworkGridDouble.getDimensions().getHalfCellsize();
        for (int i = 0; i < maxite; i++) {
            if (i == 24) {
                int debug = 1;
            }
            System.out.println("Simulate iteration " + i + " out of " + maxite + " iterations:");
            simulateMovement(grids, halfCellsize, tollerance);
            ge.Time.addSecond();
            // Write out results
            visualiseNetworkOnGrid1(ge.ReportingPopulationDensityMovingAggregateGridDouble,
                    ge.get_reporting_VectorEnvelope2D());
            System.out.print("Time taken in simulation ");
            Generic_Time.printTime(System.currentTimeMillis() - time0);
        }
        System.out.println(System.currentTimeMillis());
    }

    /**
     *
     * @param area_String A filter for the area for which journeys are
     * considered
     * @param a_SWSDataHandler
     * @param a_CASAreaEastingNorthingDataHandler
     * @param a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap
     * @param nDataRecords
     * @param a_GENESIS_FemaleCollection
     * @param handleOutOfMemoryError
     */
    public void init_Population_HashSet(
            String area_String,
            Census_SWSDataHandler a_SWSDataHandler,
            Census_CASAreaEastingNorthingDataHandler a_CASAreaEastingNorthingDataHandler,
            HashMap a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap,
            long nDataRecords,
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            for (long a_RecordID = 0; a_RecordID < nDataRecords; a_RecordID++) {
                init_Population_HashSet(
                        area_String,
                        a_SWSDataHandler,
                        a_CASAreaEastingNorthingDataHandler,
                        a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap,
                        nDataRecords,
                        a_RecordID,
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(
                        a_GENESIS_FemaleCollection,
                        false);
                ge.initMemoryReserve(a_GENESIS_FemaleCollection,
                        ge.HOOMEF);
                init_Population_HashSet(
                        area_String,
                        a_SWSDataHandler,
                        a_CASAreaEastingNorthingDataHandler,
                        a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap,
                        nDataRecords,
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public void init_Population_HashSet(
            String area_String,
            Census_SWSDataHandler a_SWSDataHandler,
            Census_CASAreaEastingNorthingDataHandler a_CASAreaEastingNorthingDataHandler,
            HashMap a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap,
            long nDataRecords,
            long a_RecordID,
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            boolean handleOutOfMemoryError) {
        try {
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
            Vector_OSGBtoLatLon OSGBtoLatLon = ge.ve.getOSGBtoLatLon();
            GENESIS_Grids grids = ge.getGENESIS_Grids();
            Census_SWSDataRecord a_SWSDataRecord;
            Census_CASAreaEastingNorthingDataRecord home_CASAreaEastingNorthingDataRecord;
            Census_CASAreaEastingNorthingDataRecord work_CASAreaEastingNorthingDataRecord;
            //if (a_RecordID % 6923 == 0) {
            //if (a_RecordID % 1000 = 0) {
            System.out.println("Flow " + a_RecordID + " out of " + nDataRecords);
            //}
            a_SWSDataRecord = a_SWSDataHandler.getSWSDataRecord(a_RecordID);
            String a_ZoneCode = new String(a_SWSDataRecord.getZone_Code());
            if (a_ZoneCode.startsWith(area_String)
                    && a_SWSDataRecord.get_Destination_Zone_Code().startsWith(area_String)) {
                home_CASAreaEastingNorthingDataRecord = (Census_CASAreaEastingNorthingDataRecord) a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap.get(new String(a_SWSDataRecord.getZone_Code()));
                if (home_CASAreaEastingNorthingDataRecord == null) {
                    int debug = 1;
                    System.out.println("home_CASAreaEastingNorthingDataRecord == null _RecordID " + a_RecordID);
                } else {
                    Vector_Point2D home_Point2D = get_OSGB_To_LatLon_Point2D(OSGBtoLatLon,
                            home_CASAreaEastingNorthingDataRecord,
                            ge.DecimalPlacePrecisionForNetworkCalculations);
                    // All origins and destinations need to be at cell centroids
                    Vector_Point2D homeCentroid_Point2D = grids.getCellCentroid_Point2D(ge.NetworkGridDouble,
                            home_Point2D,
                            ge._ToRoundToX_BigDecimal,
                            ge._ToRoundToY_BigDecimal);
                    // Everyone in an output area travelling to the same destination
                    // is collected into the same household which is probably not right!!!!
                    GENESIS_Household a_Household = new GENESIS_Household(homeCentroid_Point2D);
                    work_CASAreaEastingNorthingDataRecord = (Census_CASAreaEastingNorthingDataRecord) a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap.get(a_SWSDataRecord.get_Destination_Zone_Code());
                    if (work_CASAreaEastingNorthingDataRecord == null) {
                        int debug = 1;
                        System.out.println("work_CASAreaEastingNorthingDataRecord == null _RecordID " + a_RecordID);
                    } else {
                        Vector_Point2D work_Point2D = get_OSGB_To_LatLon_Point2D(OSGBtoLatLon,
                                work_CASAreaEastingNorthingDataRecord,
                                ge.DecimalPlacePrecisionForNetworkCalculations);
                        Vector_Point2D workCentroid_Point2D = grids.getCellCentroid_Point2D(ge.NetworkGridDouble,
                                work_Point2D,
                                ge._ToRoundToX_BigDecimal,
                                ge._ToRoundToY_BigDecimal);
                        for (int flow = 0; flow < a_SWSDataRecord.get_Total() * 10; flow++) {
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                                    a_GENESIS_FemaleCollection,
                                    handleOutOfMemoryError);
                            GENESIS_Female a_Female;
                            GENESIS_Time aBirthMin_Time = new GENESIS_Time(0, 0, 0);
                            GENESIS_Time aBirthMax_Time = new GENESIS_Time(15, 0, 0);
                            GENESIS_Time aBirth_Time;
                            aBirth_Time = GENESIS_Time.getRandomTime(
                                    aBirthMin_Time,
                                    aBirthMax_Time,
                                    _RandomArray[0],
                                    _RandomArray[1]);
                            a_Female = _PersonFactory.createFemale(
                                    new GENESIS_Age(ge, aBirth_Time),
                                    a_Household,
                                    a_Household._Point2D,
                                    a_GENESIS_FemaleCollection,
                                    handleOutOfMemoryError);
                            //a_Female._resource_double = resourcePersonInitial_double;
                            //a_Female._resourceMax_double = resourcePersonMax_double;
                            a_Female.setPreviousPoint2D(a_Female.Location);
                            a_Female._Work_Point2D = workCentroid_Point2D;
                            //a_Female.setMovement();
                            int shift = _RandomArray[0].nextInt(_Shifts.keySet().size());
                            GENESIS_Time[] _Shift_Times = (GENESIS_Time[]) _Shifts.get(shift);
                            a_Female._Work_Time = new GENESIS_Time[2];
                            //_Female._Time_Work[0] = new GENESIS_Time(0, 0, 60 * 60 * 9);
                            //_Female._Time_Work[1] = new GENESIS_Time(0, 0, 60 * 60 * 17);
                            a_Female._Work_Time[0] = _Shift_Times[0];
                            a_Female._Work_Time[1] = _Shift_Times[1];
                            a_Female._SetOffToWork_Time = new GENESIS_Time(a_Female._Work_Time[0]);
                            a_Female._SetOffToWork_0_Time = new GENESIS_Time(a_Female._Work_Time[0]);
                            a_Female._SpeedDefault_BigDecimal = _SpeedDefault_BigDecimal;
                            a_Female._Network2D = new Vector_Network2D(null);
                            a_Female._reporting_VectorNetwork2D = new Vector_Network2D(null);
                            a_Female._reporting_CellID = ge.ReportingPopulationDensityAggregateGridDouble.getCellID(ge.ReportingPopulationDensityAggregateGridDouble.getRow(a_Household._Point2D.Y),
                                    ge.ReportingPopulationDensityAggregateGridDouble.getCol(a_Household._Point2D.X));
//                        a_Female._Network2D.addToNetwork(
//                                a_Female.Location,
//                                //a_Female.HeadingLocation,
//                                a_Female._Work_Point2D,
//                                4);
                            _FemalePopulation_HashSet.add(
                                    a_Female.getAgentID(ge.HOOME));
                            //a_Female.write(AgentEnvironment.HOOME);
                        }
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                System.out.println("Need to work...");
                ge.swapDataAnyExcept(a_GENESIS_FemaleCollection, ge.HOOMEF);
                ge.initMemoryReserve();
                init_Population_HashSet(
                        area_String,
                        a_SWSDataHandler,
                        a_CASAreaEastingNorthingDataHandler,
                        a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap,
                        nDataRecords,
                        a_RecordID,
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    public Census_SWSDataHandler getSWSDataHandler(File file) {
        Census_SWSDataHandler result = null;
        try {
            result = (Census_SWSDataHandler) Generic_IO.readObject(file);
            result.init_Logger(Level.ALL, _Directory);
        } catch (Exception e) {
            if (e instanceof InvalidClassException) {
                //} catch (InvalidClassException e) {
                result = new Census_SWSDataHandler(this.ge.get_Directory(false), true);
                Generic_IO.writeObject(result, file);
                result.init_Logger(Level.ALL, _Directory);
            }
        }
        return result;
    }
}
