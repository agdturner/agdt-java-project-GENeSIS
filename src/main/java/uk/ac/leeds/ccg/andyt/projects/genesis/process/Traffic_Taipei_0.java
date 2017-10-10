package uk.ac.leeds.ccg.andyt.projects.genesis.process;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.*;
import uk.ac.leeds.ccg.andyt.projects.genesis.grids.StaticGrids;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.Household;
import uk.ac.leeds.ccg.andyt.projects.genesis.travelingsalesman.TSMisc;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Network2D;

/**
 *
 * @author geoagdt
 */
public class Traffic_Taipei_0 extends AbstractTrafficModel {

    static final long serialVersionUID = 1L;
    public int accessibilityMax_int;
    public int accessibilityMin_int;
    double cellsize;

    /**
     * A class to generate a society Commuting to work in Leeds SWS.
     */
    public Traffic_Taipei_0() {
    }

    public static void main(String[] args) {
        new Traffic_Taipei_0().run();
    }

    public void run() {
        // Initialisation
        _Directory = new File("C:/Work/Projects/GENESIS/workspace/" + this.getClass().getName());
        // Initialise Environment
        GENESIS_Time _Time;
        _Time = new GENESIS_Time(20, 0);
        boolean handleOutOfMemoryError = true;
        int scaleNew = 1;//2(scale cellsize_BigDecimal manually)
        int a_NRows = (int) scaleNew * 173;//360;//720;//2048;//1024;//2048;//4096;//768;//512;//256;//128;//512;//128;//256;//128;//512;//64;//128;//256;//512;
        int a_NCols = (int) scaleNew * 366;//183;//640;//1280;//2048;//1024;//2048;//4096;//768;//512;//128;//512;//256;//1024;//128;//256;//512;//1024;
        Grids_Grid2DSquareCellDoubleFactory a_Grid2DSquareCellDoubleFactory = new Grids_Grid2DSquareCellDoubleFactory(
                _Directory,
                new Grids_Environment(),
                handleOutOfMemoryError);
        //cellsize, minx, miny, maxx, maxy
//BigDecimal cellsize_BigDecimal = new BigDecimal("0.00100");//new BigDecimal("1.00");
        BigDecimal cellsize_BigDecimal = new BigDecimal("0.00100");
        BigDecimal minx = new BigDecimal("121.45800");
        BigDecimal miny = new BigDecimal("24.96020");
        //BigDecimal maxx = new BigDecimal(121.6406d);
        //BigDecimal maxy = new BigDecimal(25.1329d);
        BigDecimal _NRows_BigDecimal = new BigDecimal(a_NRows);
        BigDecimal _NCols_BigDecimal = new BigDecimal(a_NCols);
        BigDecimal[] _Dimensions = new BigDecimal[5];
        _Dimensions[0] = cellsize_BigDecimal;
        _Dimensions[1] = minx;
        _Dimensions[2] = miny;
        _Dimensions[3] = (_NCols_BigDecimal.multiply(_Dimensions[0])).add(_Dimensions[1]);
        _Dimensions[4] = (_NRows_BigDecimal.multiply(_Dimensions[0])).add(_Dimensions[2]);
        a_Grid2DSquareCellDoubleFactory.set_Dimensions(_Dimensions);
        init_RandomArrayMinLength(
                0,
                0L,
                1L);
        _GENESIS_Environment = new GENESIS_Environment(
                this,
                new GENESIS_Time());
        _GENESIS_Environment.init_MemoryReserve(false);
        _GENESIS_Environment._DecimalPlacePrecisionForCalculations = 10;
        init_Rounding(cellsize_BigDecimal, minx, miny);
        _GENESIS_Environment._DecimalPlacePrecisionForNetwork = Math.min(
                _GENESIS_Environment._DecimalPlacePrecisionForCalculations,
                Math.max(_GENESIS_Environment._ToRoundToX_BigDecimal.scale(),
                        _GENESIS_Environment._ToRoundToY_BigDecimal.scale()));
        _GENESIS_Environment._DecimalPlacePrecisionForNetworkCalculations = _GENESIS_Environment._DecimalPlacePrecisionForNetwork + 2;

        _SpeedDefault_BigDecimal = new BigDecimal(3.0 * _Dimensions[0].doubleValue());

        _GENESIS_Environment = new GENESIS_Environment(
                this,
                _Time,
                a_Grid2DSquareCellDoubleFactory,
                handleOutOfMemoryError);
        _GENESIS_Environment._Directory = _Directory;
        _GENESIS_Environment._Directory = _Directory;
        _GENESIS_Environment._HandleOutOfMemoryError_boolean = handleOutOfMemoryError;
        _GENESIS_Environment.init_MemoryReserve(
                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
        GENESIS_AgentEnvironment a_GENESIS_AgentEnvironment
                = new GENESIS_AgentEnvironment(_GENESIS_Environment);
        _GENESIS_Environment._GENESIS_AgentEnvironment = a_GENESIS_AgentEnvironment;
        GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager
                = new GENESIS_AgentCollectionManager(
                        _GENESIS_Environment,
                        _Directory);
        a_GENESIS_AgentEnvironment.set_AgentCollectionManager(
                a_GENESIS_AgentCollectionManager,
                handleOutOfMemoryError);
        _PersonFactory = new GENESIS_PersonFactory(
                _GENESIS_Environment,
                _GENESIS_AgentCollectionManager);
//        a_GENESIS_AgentCollectionManager._MaximumNumberOfAgents = 100000000000L;
//        a_GENESIS_AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection = 10000;
//        a_GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory = 100;
        a_GENESIS_AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection = 10000; //1000;
        a_GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory = 100;

        _GENESIS_Environment._Directory = _Directory;
        //File map_File = new File("C:/Work/data/OpenStreetMap/Taipei/map.osm");
        File map_File = new File("C:/Work/data/Open/OSM/Taipei/map.osm");
        _GENESIS_Environment._TSMisc = new TSMisc(
                _GENESIS_Environment,
                map_File);
        //_GENESIS_AgentEnvironment._TSMisc.

        Long a_AgentCollection_ID = 0L;
        String a_Agent_Type = GENESIS_Agent.class.getName();
        GENESIS_FemaleCollection a_FemaleCollection = new GENESIS_FemaleCollection(
                _GENESIS_Environment,
                a_AgentCollection_ID,
                GENESIS_Person.getTypeLivingFemale_String());
        a_GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap.put(
                a_AgentCollection_ID,
                a_FemaleCollection);

        // Initialise Agent cache
        //Don't always need to initialise file cache
        File directory_Agents = new File(_Directory, "Agents");
        directory_Agents.mkdirs();

//        _GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.initialise_FileCache(
//                _GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager._MaximumNumberOfAgents,
//                _GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory,//64,
//                directory_Agents);
//        a_GENESIS_AgentCollectionManager.delete_FileCache(_Directory);
        //_GENESIS_AgentEnvironment._GENESIS_AgentCollectionManager.delete_FileCache(new File(directory.toString() + "old"));
        // Initialise AgentCollection cache
        File directory_AgentCollections = new File(_Directory, "AgentCollections");
        directory_AgentCollections.mkdirs();

        // Init Shifts
        init_Shifts();

        // Initialise Grids
        // PopulationDensity
        _GENESIS_Environment._reportingPopulationDensity_Grid2DSquareCellDouble = (Grids_Grid2DSquareCellDouble) _GENESIS_Environment._network_Grid2DSquareCellDoubleFactory.create(
                new File(_Directory, "PopulationDensity"),
                a_NRows,
                a_NCols);
        // A grid of aggregatePopulationDensity
        _GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble = (Grids_Grid2DSquareCellDouble) _GENESIS_Environment._network_Grid2DSquareCellDoubleFactory.create(
                new File(_Directory, "AggregatePopulationDensity"),
                a_NRows,
                a_NCols);
//        // A grid of accessibility
//        _Accessibility_Grid2DSquareCellDouble = (Grid2DSquareCellDouble) _GENESIS_AgentEnvironment._network_Grid2DSquareCellDoubleFactory.create(
//                new File(_Directory, "Accessibility"),
//                _NRows,
//                _NCols);
//        // A grid of resources
//        _Resources_Grid2DSquareCellDouble = (Grid2DSquareCellDouble) _GENESIS_AgentEnvironment._network_Grid2DSquareCellDoubleFactory.create(
//                new File(_Directory, "Resources"),
//                _NRows,
//                _NCols);
        for (long row = 0; row < a_NRows; row++) {
            for (long col = 0; col < a_NCols; col++) {
                _GENESIS_Environment._reportingPopulationDensity_Grid2DSquareCellDouble.setCell(
                        row,
                        col,
                        0,
                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                _GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.setCell(
                        row,
                        col,
                        0,
                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                _Accessibility_Grid2DSquareCellDouble.setCell(
//                        row,
//                        col,
//                        0,
//                        _GENESIS_AgentEnvironment._HandleOutOfMemoryError);
//                _Resources_Grid2DSquareCellDouble.setCell(
//                        row,
//                        col,
//                        0,
//                        _GENESIS_AgentEnvironment._HandleOutOfMemoryError);
            }
        }
        cellsize = _GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellsizeDouble(
                _GENESIS_Environment._HandleOutOfMemoryError_boolean);

        //BigDecimal a_CellXBigDecimal = _GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.getCellXBigDecimal(0L, handleOutOfMemoryError);
        //BigDecimal a_CellYBigDecimal = _GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.getCellYBigDecimal(0L, handleOutOfMemoryError);
        //Need to specify a dimensions scale
        // _GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.
        //_Distance = cellsize * 5.0d;
        // Initialise resource
//        initialiseResources();
        int numberOfResourcePoint2Ds = 1000;
//        initialiseResources(
//                numberOfResourcePoint2Ds);

//        accessibilityMax_int = 100;
//        accessibilityMin_int = 1;
//        initialiseAccessibility();
        _ImageExporter = new Grids_ImageExporter(_GENESIS_Environment._Grids_Environment);

        int numberOfHouseholds = 10;//100;
        int populationPerHousehold = 2;
        initialisePopulation(numberOfHouseholds, populationPerHousehold);

        //simulatetest();
        simulate();

        HashSet<Vector_Network2D> a_Network2D_HashSet = new HashSet<Vector_Network2D>();
        Iterator<Long> a_Iterator = _FemalePopulation_HashSet.iterator();
        GENESIS_Person a_Person;
        while (a_Iterator.hasNext()) {
            a_Person = getFemale(
                    a_Iterator.next(),
                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
            a_Network2D_HashSet.add(a_Person._Network2D);
        }

        double scale = 4 / scaleNew;//4;

//        visualiseNetworkOnGrid1(
//                a_Network2D_HashSet,
//                _AggregatePopulationDensity_Grid2DSquareCellDouble,
//                scale);
//        RenderNetwork2D a_RenderNetwork2D = render(
//                a_Network2D_HashSet,
//                scale);
//        a_RenderNetwork2D.getGraphics();
    }

//    public RenderNetwork2D render(
//            HashSet<Network2D> aNetwork2D_HashSet,
//            int scale) {
//        JFrame _JFrame = new JFrame("City");
//        _JFrame.addWindowListener(new WindowAdapter() {
//
//            @Override
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });
//        return new RenderNetwork2D(
//                _JFrame,
//                (int) _NCols,
//                (int) _NRows,
//                scale,
//                aNetwork2D_HashSet);
//    }
    @Override
    public void simulate() {
        //String type = "PNG";
        //String type = "JPEG";
        //File file;
        BigDecimal tollerance;
        tollerance = new BigDecimal("0.0000001");
        int maxite = 60 * 24 * 7;//60*24*7;
        System.out.println(System.currentTimeMillis());
        GENESIS_Person a_Person;
        for (int i = 0; i < maxite; i++) {
//            if (i == 24) {
//                int debug = 1;
//            }
            System.out.println("simulate iteration " + i + " out of " + maxite);
            simulateMovement(tollerance);
            _GENESIS_Environment._Time.addSecond();
            //for (int t = 0; t < 10; t++) {
            //_GENESIS_AgentEnvironment._Time.addMinute();
            //}
            //_GENESIS_AgentEnvironment._Time.addHour();
            // Write out results
//            file = new File(
//                    _GENESIS_AgentEnvironment._Directory,
//                    "out_aggregate_population_density_" + i + "." + type);
//            aImageExporter.toGreyScaleImage(
//                    _AggregatePopulationDensity_Grid2DSquareCellDouble,
//                    file,
//                    type,
//                    _GENESIS_AgentEnvironment._HandleOutOfMemoryError);

            HashSet<Vector_Network2D> a_Network2D_HashSet = new HashSet<Vector_Network2D>();
            Iterator<Long> a_Iterator = _FemalePopulation_HashSet.iterator();
            while (a_Iterator.hasNext()) {
                a_Person = getFemale(
                        a_Iterator.next(),
                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                a_Network2D_HashSet.add(a_Person._Network2D);
            }

            double scale = 4;

//            visualiseNetworkOnGrid1(
//                    a_Network2D_HashSet,
//                    _AggregatePopulationDensity_Grid2DSquareCellDouble,
//                    scale);
        }
        System.out.println(System.currentTimeMillis());
    }

    public void initialisePopulation(
            int numberOfHouseholds,
            int populationPerHousehold) {
        _FemalePopulation_HashSet = new HashSet<Long>();
        // distance is for assigning a random destination for each person
        //double speed = cellsize * 10.0d;
        GENESIS_Female a_Female;
        GENESIS_Time aBirthMin_Time = new GENESIS_Time(0, 0, 0);
        GENESIS_Time aBirthMax_Time = new GENESIS_Time(15, 0, 0);
        GENESIS_Time aBirth_Time;
        for (int aHouseholdID = 0; aHouseholdID < numberOfHouseholds; aHouseholdID++) {

            if (aHouseholdID % 10 == 0) {
                System.out.println("initialising Household " + aHouseholdID);
            }
            Household a_Household = new Household(
                    StaticGrids.getRandomCellCentroid_Point2D(
                            _GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble,
                            _RandomArray[0],
                            _GENESIS_Environment._DecimalPlacePrecisionForNetwork,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean));
            for (int i = 0; i < populationPerHousehold; i++) {
                aBirth_Time = GENESIS_Time.getRandomTime(
                        aBirthMin_Time,
                        aBirthMax_Time,
                        _RandomArray[0],
                        _RandomArray[1]);
                a_Female = _GENESIS_Environment._PersonFactory.createFemale(
                        new GENESIS_Age(_GENESIS_Environment, aBirth_Time),
                        a_Household,
                        a_Household._Point2D,
                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                a_Female._resource_double = resourcePersonInitial_double;
//                a_Female._resourceMax_double = resourcePersonMax_double;
                a_Female.set_Previous_Point2D(a_Female._Point2D);

//                a_Female._Heading_Point2D =
//                        StaticGrids.getRandomCellCentroid_Point2D(
//                        _GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble,
//                        _GENESIS_AgentEnvironment._Random,
//                        a_Female._Point2D,
//                        //new BigDecimal(_Distance),
//                        new BigDecimal(_Speed),
//                        a_DecimalPlacePrecision,//Point2D.DefaultDecimalPlacePrecision,
//                        _GENESIS_AgentEnvironment._HandleOutOfMemoryError);
                a_Female._Heading_Point2D = StaticGrids.getRandomCellCentroid_Point2D(
                        _GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble,
                        _RandomArray[0],
                        _GENESIS_Environment._DecimalPlacePrecisionForNetwork,
                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);

                a_Female._Work_Point2D = a_Female._Heading_Point2D;

                //a_Female.setMovement();
                a_Female.setMovement();

                int shift = _RandomArray[0].nextInt(_Shifts.keySet().size());
                GENESIS_Time[] _Shift_Times = (GENESIS_Time[]) _Shifts.get(shift);
                a_Female._Work_Time = new GENESIS_Time[2];
                //_Female._Time_Work[0] = new GENESIS_Time(0, 0, 60 * 60 * 9);
                //_Female._Time_Work[1] = new GENESIS_Time(0, 0, 60 * 60 * 17);
                a_Female._Work_Time[0] = _Shift_Times[0];
                a_Female._Work_Time[1] = _Shift_Times[1];
                //a_Female._SetOffToWork_Time = new GENESIS_Time(a_Female._Work_Time[0]);
                a_Female._Network2D = new Vector_Network2D(null);
                a_Female._Network2D.addToNetwork(
                        a_Female._Point2D,
                        a_Female._Heading_Point2D,
                        4);
                a_Female._SpeedDefault_BigDecimal = _SpeedDefault_BigDecimal;
                _FemalePopulation_HashSet.add(a_Female.get_Agent_ID(true));
            }
        }
    }
//    public void initialisePopulation(
//            int numberOfHouseholds,
//            int populationPerHousehold) {
//    //public void initialisePopulation() {
//        //_Male_ID_HashSet = new HashSet<Long>();
//        //_Female_ID_HashSet = new HashSet<Long>();
//        _population_HashSet = new HashSet<Long>();
//
////        //String area = "00DAFY";
////        String area = "00DAF";
////        //String area = "00DA";
////        File directory = new File("C:/Work/Projects/GENESIS/Workspace/");
////
////        File file;
////        // SWSDataRecords
////        SWSDataHandler a_SWSDataHandler;
////        file = new File(
////                directory,
////                SWSDataHandler.class.getCanonicalName() + ".thisFile");
////        a_SWSDataHandler = new SWSDataHandler(file);
////
////        System.out.println("a_SWSDataHandler.getNDataRecords() " + a_SWSDataHandler.getNDataRecords());
////        SWSDataRecord a_SWSDataRecord = (SWSDataRecord) a_SWSDataHandler.getDataRecord(0L);
////        System.out.println("a_SWSDataRecord " + a_SWSDataRecord);
////
////        // CASAreaEastingNorthingDataRecords
////        CASAreaEastingNorthingDataHandler a_CASAreaEastingNorthingDataHandler;
////        file = new File(
////                directory,
////                CASAreaEastingNorthingDataHandler.class.getCanonicalName() + ".thisFile");
////        a_CASAreaEastingNorthingDataHandler = new CASAreaEastingNorthingDataHandler(file);
////        System.out.println("a_CASAreaEastingNorthingDataHandler.getNDataRecords() " + a_CASAreaEastingNorthingDataHandler.getNDataRecords());
////        CASAreaEastingNorthingDataRecord a_CASAreaEastingNorthingDataRecord = (CASAreaEastingNorthingDataRecord) a_CASAreaEastingNorthingDataHandler.getDataRecord(0L);
////        System.out.println("a_CASAreaEastingNorthingDataRecord " + a_CASAreaEastingNorthingDataRecord);
////        HashMap a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap = a_CASAreaEastingNorthingDataHandler.get_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap(area);
////
////        Iterator a_Iterator;
////        a_Iterator = a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap.keySet().iterator();
////        String a_CASZoneCode;
////        BigDecimal xmin;
////        BigDecimal ymin;
////        BigDecimal xmax;
////        BigDecimal ymax;
//        double xmin = Double.MAX_VALUE;
//        double ymin = Double.MAX_VALUE;
//        double xmax = Double.MIN_VALUE;
//        double ymax = Double.MIN_VALUE;
//        double easting;
//        double northing;
//        //CASAreaEastingNorthingDataRecord a_CASAreaEastingNorthingDataRecord;
//        while (a_Iterator.hasNext()) {
//            a_CASZoneCode = (String) a_Iterator.next();
//            if (a_CASZoneCode.startsWith(area)) {
//                a_CASAreaEastingNorthingDataRecord = (CASAreaEastingNorthingDataRecord) a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap.get(a_CASZoneCode);
//                easting = a_CASAreaEastingNorthingDataRecord.get_Easting();
//                northing = a_CASAreaEastingNorthingDataRecord.get_Northing();
//                xmin = Math.min(xmin, easting);
//                xmax = Math.max(xmax, easting);
//                ymin = Math.min(ymin, northing);
//                ymax = Math.max(ymax, northing);
//            }
//        }
//        // Might need to add some for routes that go outside area...
//        _GENESIS_AgentEnvironment._XRange_double = xmax - xmin;
//        _GENESIS_AgentEnvironment._YRange_double = ymax - ymin;
//        _GENESIS_AgentEnvironment._XMin_double = xmin;
//        _GENESIS_AgentEnvironment._YMin_double = ymin;
//        double xrange = xmax - xmin;
//        double yrange = ymax - ymin;
//        //xmin += 1.0d;
//        //xmax += 1.0d;
//        //ymin += 1.0d;
//        //ymax += 1.0d;
//
//        long nDataRecords = a_SWSDataHandler.getNDataRecords();
//        //long nDataRecords = 10;
//        for (long _RecordID = 0; _RecordID < nDataRecords; _RecordID++) {
//
//            a_SWSDataRecord = a_SWSDataHandler.getSWSDataRecord(_RecordID);
//            String a_ZoneCode = new String(a_SWSDataRecord.getZone_Code());
//            if (a_ZoneCode.startsWith(area) &&
//                    a_SWSDataRecord.get_Destination_Zone_Code().startsWith(area)) {
//                if (_RecordID % 1000 == 0) {
//                    System.out.println("init flow " + _RecordID + " out of " + nDataRecords);
//                }
//                a_CASAreaEastingNorthingDataRecord = (CASAreaEastingNorthingDataRecord) a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap.get(new String(a_SWSDataRecord.getZone_Code()));
//                if (a_CASAreaEastingNorthingDataRecord == null) {
//                    int debug = 1;
//                    System.out.println("a_CASAreaEastingNorthingDataRecord == null _RecordID " + _RecordID);
//                } else {
//                    double home_x = ((a_CASAreaEastingNorthingDataRecord.get_Easting() - xmin) * (double) _NCols) / xrange;
//                    double home_y = ((a_CASAreaEastingNorthingDataRecord.get_Northing() - ymin) * (double) _NRows) / yrange;
//                    long home_row = this._GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.getCellRowIndex(home_y, _GENESIS_AgentEnvironment._HandleOutOfMemoryError);
//                    long home_col = this._GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.getCellRowIndex(home_x, _GENESIS_AgentEnvironment._HandleOutOfMemoryError);
//                    Point2D home_Point2D = new Point2D(
//                            this._GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.getCellXBigDecimal(home_col, _GENESIS_AgentEnvironment._HandleOutOfMemoryError),
//                            this._GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.getCellYBigDecimal(home_row, _GENESIS_AgentEnvironment._HandleOutOfMemoryError),
//                            3);
//                    // Everyone in an output area travelling to the same destination
//                    // is collected into the same household which is not right!!!!
//                    Household a_Household = new Household(home_Point2D);
//                    a_CASAreaEastingNorthingDataRecord = (CASAreaEastingNorthingDataRecord) a_CASZoneCode_CASAreaEastingNorthingDataRecord_HashMap.get(a_SWSDataRecord.get_Destination_Zone_Code());
//                    double work_x = ((a_CASAreaEastingNorthingDataRecord.get_Easting() - xmin) * (double) _NCols) / xrange;
//                    double work_y = ((a_CASAreaEastingNorthingDataRecord.get_Northing() - ymin) * (double) _NRows) / yrange;
//                    long work_row = this._GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.getCellRowIndex(work_y, _GENESIS_AgentEnvironment._HandleOutOfMemoryError);
//                    long work_col = this._GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.getCellRowIndex(work_x, _GENESIS_AgentEnvironment._HandleOutOfMemoryError);
//                    Point2D work_Point2D = new Point2D(
//                            this._GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.getCellXBigDecimal(work_col, _GENESIS_AgentEnvironment._HandleOutOfMemoryError),
//                            this._GENESIS_AgentEnvironment._World_Grid2DSquareCellDouble.getCellYBigDecimal(work_row, _GENESIS_AgentEnvironment._HandleOutOfMemoryError),
//                            3);
//
//                    for (int flow = 0; flow < a_SWSDataRecord.get_Total(); flow++) {
//                        GENESIS_Female a_Female;
//                        GENESIS_Time aBirthMin_Time = new GENESIS_Time(0, 0, 0);
//                        GENESIS_Time aBirthMax_Time = new GENESIS_Time(15, 0, 0);
//                        GENESIS_Time aBirth_Time;
//                        aBirth_Time = GENESIS_Time.getRandomTime(
//                                aBirthMin_Time,
//                                aBirthMax_Time,
//                                _GENESIS_AgentEnvironment._Random);
//                        a_Female = _PersonFactory.createFemale(
//                                _GENESIS_AgentEnvironment,
//                                //new File(_GENESIS_AgentEnvironment._Directory,_ID.toString()),
//                                aBirth_Time,
//                                a_Household,
//                                a_Household._Point2D);
//                        a_Female._resource_double = resourcePersonInitial_double;
//                        a_Female._resourceMax_double = resourcePersonMax_double;
//                        a_Female._Previous_Point2D = new Point2D(a_Female._Point2D);
//                        a_Female._Work_Point2D = work_Point2D;
//                        //a_Female.setMovement();
//                        int shift = _GENESIS_AgentEnvironment._Random.nextInt(_Shifts.keySet().size());
//                        GENESIS_Time[] _Shift_Times = (GENESIS_Time[]) _Shifts.get(shift);
//                        a_Female._Work_Time = new GENESIS_Time[2];
//                        //_Female._Time_Work[0] = new GENESIS_Time(0, 0, 60 * 60 * 9);
//                        //_Female._Time_Work[1] = new GENESIS_Time(0, 0, 60 * 60 * 17);
//                        a_Female._Work_Time[0] = _Shift_Times[0];
//                        a_Female._Work_Time[1] = _Shift_Times[1];
//                        a_Female._SetOffToWork_Time = new GENESIS_Time(a_Female._Work_Time[0]);
//                        a_Female._SetOffToWork_0_Time = new GENESIS_Time(a_Female._Work_Time[0]);
//                        a_Female._Network2D = new Network2D();
////                        a_Female._Network2D.addToNetwork(
////                                a_Female._Point2D,
////                                a_Female._Heading_Point2D,
////                                4);
//                        _population_HashSet.add(a_Female.get_Agent_ID(_GENESIS_AgentEnvironment._HandleOutOfMemoryError));
//                        //a_Female.write(_GENESIS_AgentEnvironment._HandleOutOfMemoryError);
//                    }
//                    //}
//                    //}
//                }
//            }
//        }
//        return;
//    }
}
