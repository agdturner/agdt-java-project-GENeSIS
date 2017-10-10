package uk.ac.leeds.ccg.andyt.projects.genesis.process;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.logging.Level;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Execution;
import uk.ac.leeds.ccg.andyt.generic.visualisation.Generic_Visualisation;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.*;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.XMLConverter;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.metadata.MetadataType;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.parameters.ParametersType;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Demographics;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Fertility;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Migration;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Miscarriage;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Mortality;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_MortalityAndFertilityEstimator;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Population;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Collections;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;

/**
 * A class for running a basic demographic simulation model as specified.
 *
 * The model simulates pregnancy/conception, miscarriage, birth, migration from
 * one sub-region to another, and death.
 *
 * It inputs age specific birth counts, age specific miscarriage rates, age and
 * gender specific migration counts for regions and sub-regions, and age and
 * gender specific death counts. These are converted into fertility, migration
 * and mortality rates and probabilities given an initialised individual level
 * population dataset. Conception is only maternally specified, fatherly
 * relationships are not in the model. The initial population is generated from
 * age by gender counts, or it may be the output from a previous simulation.
 *
 * The model ticks with a daily time step and at each tick, death, miscarriage,
 * pregnancy, birth and migration are stochastically determined using
 * probabilities and pseudo random tests. The simulation is designed to run for
 * a year and output various statistics at that stage as well as all the data
 * needed to continue the simulation.
 */
public class GENESIS_DemographicModel
        extends Abstract_GENESIS_DemographicModel
        implements Serializable {

    /**
     * Used for Logging
     */
    private static final String sourceClass = GENESIS_DemographicModel.class.getName();
    private static final String sourcePackage = GENESIS_DemographicModel.class.getPackage().getName();
    private static final int NearlyDueNumberOfDaysTillDueDate = 50;
    private static TreeMap<String, String> theOAtoMSOALookup;
    private transient TreeSet<String> theMSOAAreaCodes_TreeSet;

    /**
     * _GENESIS_Environment reference
     */
    public GENESIS_DemographicModel() {
        this(new GENESIS_Environment());
    }

    public GENESIS_DemographicModel(
            GENESIS_Environment a_GENESIS_Environment) {
        String sourceMethod = "GENESIS_DemographicModel(GENESIS_Environment)";
        getLogger().entering(sourceClass, sourceMethod);
        init(a_GENESIS_Environment);
        getLogger().exiting(sourceClass, sourceMethod);
    }

    public GENESIS_DemographicModel(
            GENESIS_DemographicModel a_Demographic_Aspatial_1) {
        String sourceMethod = "GENESIS_DemographicModel(DemographicModel_Aspatial_1)";
        getLogger().entering(sourceClass, sourceMethod);
        init(a_Demographic_Aspatial_1._GENESIS_Environment);
        this._RandomArray = a_Demographic_Aspatial_1._RandomArray;
        this._Directory = a_Demographic_Aspatial_1._Directory;
        this._LivingMaleIDs = a_Demographic_Aspatial_1._LivingMaleIDs;
        this._LivingFemaleIDs = a_Demographic_Aspatial_1._LivingFemaleIDs;
        this._NotPregnantFemaleIDs = a_Demographic_Aspatial_1._NotPregnantFemaleIDs;
        this._PregnantFemaleIDs = a_Demographic_Aspatial_1._PregnantFemaleIDs;
        this._NearlyDuePregnantFemaleIDs = a_Demographic_Aspatial_1._NearlyDuePregnantFemaleIDs;
        this._Demographics = a_Demographic_Aspatial_1._Demographics;
//        this._Mortality = a_Demographic_Aspatial_1._Mortality;
//        this._Fertility = a_Demographic_Aspatial_1._Fertility;
        this._ImageExporter = a_Demographic_Aspatial_1._ImageExporter;
        getLogger().entering(sourceClass, sourceMethod);
    }

    public GENESIS_DemographicModel(
            File thisFile) {
        this((GENESIS_DemographicModel) Generic_StaticIO.readObject(thisFile));
    }

    private void init(GENESIS_Environment _GENESIS_Environment) {
        init_Environment(_GENESIS_Environment);
    }

    /**
     * This method wraps the entire process: It sets up logs, calls run(args),
     * then after completion, it flushes the logs and returns.
     *
     * @param args There are two expected arguments: args[0] long (psuedo random
     * sequence seed). A negative value is for continuing a simulation run by
     * loading the state of the pseudo random seed sequences of a continuation
     * run. If a continuation run is not being performed the program will exit
     * in error. args[1] String (workspace File path)
     */
    public static void main(String[] args) {
        try {
            System.out.println(System.getProperties().keySet().toString());
            System.out.println(System.getProperties().values().toString());
            Object[] he = Generic_Visualisation.getHeadlessEnvironment();
            Generic_Visualisation.print_headless_check((GraphicsEnvironment) he[1]);
            //System.out.println(System.getProperty("java.util.logging.config.file").trim());
            boolean multipleRun = false;
//            if (args.length != 2) {
//                args = new String[2];
//                args[1] = "/scratch01/Work/Projects/GENESIS/workspace/";
//                multipleRun = true;
//            }
            File directory = new File(args[1]);
            File logDirectory = new File(
                    directory,
                    GENESIS_Log.Generic_DefaultLogDirectoryName);
            //String logname = sourcePackage;
            String logname = "uk.ac.leeds.ccg.andyt.projects.genesis";
            GENESIS_Log.parseLoggingProperties(
                    directory,
                    logDirectory,
                    logname);
            // Switch for running on the grid. This must be set true for grid
            // runs and false otherwise.
            boolean grid = false;
            //boolean grid = true;
            if (multipleRun) {
                for (int i = 0; i < 5; i++) {
                    args[0] = "" + i;
                    run(args, grid);
                }
            } else {
                run(args, grid);
            }
            GENESIS_Log.reset();
        } catch (Error e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * @param args There are two expected arguments: args[0] long (psuedo random
     * sequence seed). A negative value is for continuing a simulation. A
     * continued simulation is run by loading check pointed serialised Random
     * instances in such a way that another result can be generated in a single
     * step at a later date without resetting Random seeds at arbitrary steps.
     * If a continuation run is not being performed the program will exit in
     * error. args[1] String (workspace File path)
     * @param grid
     */
    public static void run(
            String[] args,
            boolean grid) {
        String sourceMethod = "run(String[],boolean)";
        getLogger().entering(sourceClass, sourceMethod);
        String message;
        //int expArgsLength = 2;
        int argsLength = args.length;
        Long randomSeed_Long = null;
        File workspace_File;
        /*
         * --------------- Check arguments ---------------
         */
//        if (argsLength != expArgsLength) {
////            args = new String[2];
////            args[0] = "0";
////            args[1] = "/scratch01/Work/Projects/GENESIS/workspace/GENESIS_DemographicModel/";
//            System.err.println(
//                    "Expected " + expArgsLength + " args:"
//                    + " args[0] Long (psuedo random sequence seed);"
//                    + " args[1] String (parameters File)."
//                    + " Recieved " + argsLength + " args.");
//            System.exit(
//                    GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
//        }
        System.out.println(
                "Received " + argsLength + " args:");
        // args[0] long (psuedo random sequence seed)
        if (args[0].equalsIgnoreCase("null")) {
            System.err.println(
                    "args[0] psuedo random sequence seed should be a "
                    + "positive integer in the long range.");
            System.exit(
                    GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        }
        try {
            randomSeed_Long = new Long(args[0]);
        } catch (NumberFormatException a_NumberFormatException) {
            System.err.println(
                    a_NumberFormatException.toString()
                    + "args[0] psuedo random sequence seed should be a "
                    + "positive integer in the long range.");
            System.exit(
                    GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        }
        if (randomSeed_Long < 0L) {
            log(Level.INFO,
                    "Expecting a continuation simulation and will attempt to "
                    + "reload the random instances and continue else return "
                    + "an error...");
        }
        message = "args[0] (psuedo random sequence seed) "
                + randomSeed_Long;
        log(Level.INFO, message);
        System.out.println(message);
        // args[1] String (workspace File)
        workspace_File = new File(args[1]);
        if (!workspace_File.exists()) {
            workspace_File.mkdirs();
        }
        message = "args[1] (workspace File path) "
                + workspace_File.toString();
        log(Level.INFO, message);
        System.out.println(message);
        // Set parameters
        String fileSeparator = System.getProperty("file.separator");
        ParametersType parameters;
        File parameters_File;
        if (args.length == 2) {
            /**
             * Load Parameters File
             */
            parameters_File = new File(
                    workspace_File.toString() + fileSeparator
                    + "InputData" + fileSeparator
                    + "Parameters" + fileSeparator
                    + "parameters.xml");
            parameters =
                    XMLConverter.loadParametersFromXMLFile(parameters_File);
            message = "parameters_File "
                    + parameters_File.toString()
                    + " loaded";
            log(Level.INFO, message);
            System.out.println(message);
        } else {
            long year = Long.valueOf(args[2]);
            parameters_File = new File(
                    workspace_File.toString() + fileSeparator
                    + "InputData" + fileSeparator
                    + "Parameters" + fileSeparator
                    + "parameters.xml");
            //+ "parametersLeeds" + year + ".xml");
            parameters =
                    XMLConverter.loadParametersFromXMLFile(parameters_File);
            message = "parameters_File "
                    + parameters_File.toString()
                    + " loaded";
            String[] fileparts;
            if (fileSeparator.equalsIgnoreCase("/")) {
                fileparts = args[3].split(fileSeparator);
            } else {
                /* This coding may seems odd, but \ is a special character in 
                 * several contexts, so if fileSeparator is \ we need to use 
                 * the following to split the string as desired
                 */
                fileparts = args[3].split("\\\\");
            }
            Long idOfResultToContinue = Long.valueOf(fileparts[fileparts.length - 1]);
            // @TODO change so parameters.setIndexOfSeedPopulationRun(Integer) is parameters.setIndexOfSeedPopulationRun(Long)
            parameters.setIndexOfSeedPopulationRun(idOfResultToContinue.intValue());
            parameters.setContinueSimulation(true);
            log(Level.INFO, message);
            System.out.println(message);
        }
        File archiveDirectory_File = null;
        try {
            archiveDirectory_File = new File(
                    workspace_File,
                    GENESIS_DemographicModel.class.getSimpleName()).getCanonicalFile();
        } catch (IOException e) {
            System.err.println(
                    e.toString()
                    + "unable to get cannonical file for " + workspace_File.toString());
            e.printStackTrace(System.err);
            System.exit(
                    GENESIS_ErrorAndExceptionHandler.IOException);
        }
        GENESIS_Environment a_GENESIS_Environment =
                new GENESIS_Environment(getLogger());
        //a_GENESIS_Environment.logger = logger;          
        a_GENESIS_Environment._Directory = archiveDirectory_File;
        a_GENESIS_Environment._HandleOutOfMemoryError_boolean = true;
        /*
         * ---------------------------------------------------------------------
         * Create instance and set running
         * ---------------------------------------------------------------------
         */
        GENESIS_DemographicModel instance =
                new GENESIS_DemographicModel(
                a_GENESIS_Environment);
        instance._Directory = archiveDirectory_File;
        instance.run(
                randomSeed_Long,
                //parameters_File,
                parameters,
                fileSeparator,
                grid);
        getLogger().exiting(sourceClass, sourceMethod);
    }

    /**
     *
     * @param randomSeed_Long
     * @param parameters
     * @param parameters_File
     * @param fileSeparator
     * @param a_Parameters
     * @param grid
     * @return
     */
    public String run(
            Long randomSeed_Long,
            //File parameters_File,
            ParametersType parameters,
            String fileSeparator,
            boolean grid) {

        /*
         * ---------------------------------------------------------------------
         * Check a_Parameters and initialise fields
         * ---------------------------------------------------------------------
         */
        /*
         * Initialise _Years using parameters.getYears();
         * ---------------------------------------------------------------------
         */
        _Years = parameters.getYears();
        log(Level.FINE, "_Years " + _Years);
        /*
         * Initialise _GENESIS_Environment._Time using parameters.getStartYear()
         * and parameters.getStartDay();
         * ---------------------------------------------------------------------
         */
        int startYear = parameters.getStartYear();
        log(Level.FINE, "startYear " + startYear);
        int startDay = parameters.getStartDay();
        log(Level.FINE, "startDay " + startDay);
        _GENESIS_Environment._Time = new GENESIS_Time(startYear, startDay);
        _GENESIS_Environment._initial_Time = new GENESIS_Time(startYear, startDay); // To be changed if this is a continuation simulation
        /*
         * Set _InitialPopulation_File using
         * a_Parameters.getPopulationCountFile(); or Set
         * indexOfSeedPopulationRun_String using
         * a_Parameters.getIndexOfSeedPopulationRun(); GENESIS_Population is not
         * loaded yet as the results directory still needs to be set up.
         * ---------------------------------------------------------------------
         */
        String populationCountFile_String =
                parameters.getPopulationFile();
        BigDecimal populationMultiplicand =
                parameters.getPopulationMultiplicand();
        Long indexOfSeedPopulationRun =
                parameters.getIndexOfSeedPopulationRun().longValue();
        File inputDataDirectory = new File(
                _Directory.getParentFile(),
                "InputData");
        File demographicDataDirectory = new File(
                inputDataDirectory,
                "DemographicData");
        boolean continueSimulation =
                parameters.isContinueSimulation();
        if (!continueSimulation) {
            if (randomSeed_Long < 0) {
                throw new Error(
                        "InvalidArgumentException random seed "
                        + randomSeed_Long + " < 0 and this is not a continuation "
                        + "simulation.");
            }
            _InitialPopulation_File = initialisePopulation_File(
                    parameters,
                    demographicDataDirectory);
        } else {
            /*
             * For grid continuation simulations assume that we continue from
             * the highest leaf result in _Directory
             */
            if (grid) {
                indexOfSeedPopulationRun = Generic_StaticIO.getArchiveHighestLeaf(
                        _Directory, "_");
                String message = "Running a continuation simulation on the grid using "
                        + indexOfSeedPopulationRun + " as input";
                log(Level.INFO, message);
                System.out.println(message);
            }
        }
        /*
         * Set _InitialDeathCount_File
         */
        _InitialDeathCount_File = initialiseDeathCount_File(
                parameters,
                demographicDataDirectory);
        /*
         * Set _InitialMiscarriageRate_File
         */
        _InitialMiscarriageRate_File = initialiseMiscarriageRate_File(
                parameters,
                demographicDataDirectory);
        /*
         * Set _InitialBirthCount_File
         */
        _InitialBirthCount_File = initialiseBirthCount_File(
                parameters,
                demographicDataDirectory);
        /*
         * Further field initialisation
         */
        _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory =
                parameters.getMaximumNumberOfObjectsPerDirectory().intValue();
        _GENESIS_AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection =
                parameters.getMaximumNumberOfAgentsPerAgentCollection().intValue();
        _RandomSeed = randomSeed_Long;
        _GENESIS_Environment._Generic_BigDecimal = new Generic_BigDecimal();
        _GENESIS_Environment._Directory = _Directory;
//        _GENESIS_AgentCollectionManager = new GENESIS_AgentCollectionManager(
//                _GENESIS_Environment,
//                _Directory);
//        _GENESIS_AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection =
//                _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                _GENESIS_Environment._HandleOutOfMemoryError_boolean)._MaximumNumberOfAgentsPerAgentCollection;
//        _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory =
//                _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                _GENESIS_Environment._HandleOutOfMemoryError_boolean)._MaximumNumberOfObjectsPerDirectory;
        _GENESIS_Environment._GENESIS_AgentEnvironment.set_AgentCollectionManager(
                _GENESIS_AgentCollectionManager,
                _HandleOutOfMemoryError);
        _GENESIS_Environment._PersonFactory = new GENESIS_PersonFactory(
                _GENESIS_Environment,
                _GENESIS_AgentCollectionManager);
        log(Level.FINE,
                "_TestMemory.getTotalFreeMemory() "
                + _GENESIS_Environment.getTotalFreeMemory(_GENESIS_Environment._HandleOutOfMemoryError_boolean));
        Long lastRunIndex_Long;
        Long runIndex_Long;
        String underscore = "_";
        if (!_Directory.exists()) {
            _Directory.mkdirs();
        }
        String[] directoryList = _Directory.list();
        if (directoryList == null) {
            // Testing for directoryList == null should not be necessary...
            lastRunIndex_Long = -1L;
            _ResultDataDirectory_File = Generic_StaticIO.initialiseArchive(
                    _Directory,
                    _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory);
        } else {
            if (directoryList.length == 0) {
                lastRunIndex_Long = -1L;
                _ResultDataDirectory_File = Generic_StaticIO.initialiseArchive(
                        _Directory,
                        _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory);
            } else {
                lastRunIndex_Long = Generic_StaticIO.getArchiveHighestLeaf(
                        _Directory, underscore);
                _ResultDataDirectory_File = Generic_StaticIO.addToArchive(
                        _Directory,
                        _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory);
            }
        }
        runIndex_Long = lastRunIndex_Long + 1;
        /*
         * Create metadata and data directories
         * ---------------------------------------------------------------------
         */
        File resultMetadataDirectory_File = new File(
                _ResultDataDirectory_File,
                "metadata");
        resultMetadataDirectory_File.mkdir();
        _ResultDataDirectory_File = new File(
                _ResultDataDirectory_File,
                "data");
        _ResultDataDirectory_File.mkdir();
        /*
         * Copy input metadata files into metadata directory
         * ---------------------------------------------------------------------
         */
        //if (indexOfSeedPopulationRun_String == null) {
        //if (lastRunIndex_Long == -1L){
        //if (indexOfSeedPopulationRun_Long == null) {
        if (!continueSimulation) {
//            Generic_StaticIO.copyFile(
//                    _InitialPopulation_File,
//                    resultMetadataDirectory_File,
//                    "inputPopulation.xml");
            File inputPopulationDirectory = new File(
                    resultMetadataDirectory_File,
                    "population");
            File nonInitialisedInputPopulationDirectory = new File(
                    inputPopulationDirectory,
                    "input");
            nonInitialisedInputPopulationDirectory.mkdirs();
            Generic_StaticIO.copy(
                    _InitialPopulation_File,
                    //_InitialPopulation_File.listFiles()[0],
                    nonInitialisedInputPopulationDirectory);
            /**
             * 0 population initialisation age female (for Year); 1 population
             * initialisation age female (for Days); 2 population initialisation
             * age female (for Seconds); 3 population initialisation age female
             * (for Year); 4 population initialisation age female (for Days); 5
             * population initialisation age female (for Seconds); 6 mortality
             * Female; 7 mortality Male test 7 pregnancy test 8 pregnancy
             * initialisation 9 pregnancy initialisation 10 twins 11 triplets 12
             * gender of babies
             */
            init_RandomArrayMinLength(
                    16,
                    randomSeed_Long,
                    1L);
        } else {
            // @TODO Copy the previous result metadata into the result metadata
        }
        // export parameters object as xml file here
        XMLConverter.saveParametersToXMLFile(new File(resultMetadataDirectory_File, "parameters.xml"), parameters);
//        Generic_StaticIO.copyFile(
//                parameters_File,
//                resultMetadataDirectory_File,
//                "parameters.xml");
//        Generic_StaticIO.copyFile(
        Generic_StaticIO.copy(
                _InitialDeathCount_File,
                new File(resultMetadataDirectory_File,
                "inputDeathCount"));
        Generic_StaticIO.copy(
                //        Generic_StaticIO.copyDirectory(
                _InitialMiscarriageRate_File,
                new File(resultMetadataDirectory_File,
                "inputMiscarriageRate"));
        //        Generic_StaticIO.copyFile(
        Generic_StaticIO.copy(
                _InitialBirthCount_File,
                new File(resultMetadataDirectory_File,
                "inputBirthCount"));
        /*
         * Initialise _Demographics.
         */
        _Demographics = new GENESIS_Demographics(
                _GENESIS_Environment);

        /*
         * Initialise _initial_Demographics
         */
        _initial_Demographics = new GENESIS_Demographics(_Demographics);

        /*
         * Create metadata file
         * ---------------------------------------------------------------------
         */
        File runMetadata_File = new File(
                resultMetadataDirectory_File,
                "metadata.xml");
        MetadataType run_Metadata = new MetadataType();
        run_Metadata.setPseudoRandomNumberSeed(randomSeed_Long);

        TreeMap<String, TreeMap<String, GENESIS_Population>> singleYearOfAgeRegionPopulation;

        if (!continueSimulation) {
            // Load populations here. Populations are initialised later once 
            // mortality and fertility rates are estimated.
            //_Demographics._Population = 
            TreeMap<String, TreeMap<String, GENESIS_Population>> inputPopulation;
            inputPopulation = GENESIS_Demographics.loadInputPopulation(_GENESIS_Environment,
                    _InitialPopulation_File);
            writeOutInputPopulations(
                    inputPopulation,
                    resultMetadataDirectory_File);

            // Convert Input population into single years of age and initialise _regionIDs
            singleYearOfAgeRegionPopulation = getSingleYearsOfAgePopulations(inputPopulation,
                    _GENESIS_Environment._Time);
            _Demographics._Population = singleYearOfAgeRegionPopulation;

            // Check this loads
            File migrationDir = new File(
                    _InitialPopulation_File.getParentFile().getParentFile(),
                    "Migration");
            File migrationFile = new File(
                    migrationDir,
                    "GENESIS_Migration.thisFile");
            _Demographics._Migration = (GENESIS_Migration) Generic_StaticIO.readObject(
                    migrationFile);
            _Demographics._Migration._GENESIS_Environment = _GENESIS_Environment;

            BigDecimal migrationFactor = BigDecimal.ONE;
            BigDecimal migrationMin = BigDecimal.ZERO;
//        BigDecimal migrationFactor = BigDecimal.ONE;
//        BigDecimal migrationMin = BigDecimal.ZERO;
            _Demographics._Migration.processCounts(
                    _Demographics._Population,
                    migrationFactor,
                    migrationMin);

            // write migration data
            this._Demographics._Migration.writeMigrationData();
            this._Demographics._Migration.rationaliseMigrationData();

//            // check it loads again.
//            this._Demographics._Migration.loadMigrationData();
//            this._Demographics._Migration.rationaliseMigrationData();

        } else {
            String message;
            message = "Loading existing result";
            log(Level.FINE, message);
            System.out.println(message);
            /*
             * Load existing result @TODO load in Random used in previous run so
             * that continuing a run will produce the same result over a period.
             * This will require the Random instances used in the seed run to be
             * serialised as that run is output.
             */
            // Debug output
            message = "_Directory " + _Directory;
            log(Level.INFO, message);
            System.out.println(message);
            message = "indexOfSeedPopulationRun " + indexOfSeedPopulationRun;
            log(Level.INFO, message);
            System.out.println(message);
            message = "runIndex_Long " + runIndex_Long;
            log(Level.INFO, message);
            System.out.println(message);
            message = "_GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory "
                    + _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory;
            log(Level.INFO, message);
            System.out.println(message);
            _SeedDirectory_File = new File(
                    Generic_StaticIO.getObjectDirectory(
                    _Directory,
                    indexOfSeedPopulationRun,
                    runIndex_Long,
                    _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory),
                    "" + indexOfSeedPopulationRun);
            File previousResultDataDirectory_File = new File(
                    _SeedDirectory_File,
                    "data");
            File previousResultMetadata_File = new File(
                    _SeedDirectory_File.getPath() + "/metadata/metadata.xml");
            MetadataType seedRun_Metadata =
                    XMLConverter.loadMetadataFromXMLFile(previousResultMetadata_File);
            /*
             * Set GENESIS_Time
             */
            startYear = new Integer(seedRun_Metadata.getEndYear());
            startDay = new Integer(seedRun_Metadata.getEndDay());
            _GENESIS_Environment._Time = new GENESIS_Time(startYear, startDay);
            //_GENESIS_Environment._Time.addDay();
            int initialYear = new Integer(seedRun_Metadata.getInitialYear());
            int initialDay = new Integer(seedRun_Metadata.getInitialDay());
            _GENESIS_Environment._initial_Time = new GENESIS_Time(initialYear, initialDay);
            // Initialise metadata
            run_Metadata.setInitialYear(seedRun_Metadata.getInitialYear());
            run_Metadata.setInitialDay(seedRun_Metadata.getInitialDay());
            run_Metadata.setStartYear((int) _GENESIS_Environment._Time.getYear());
            run_Metadata.setStartDay(_GENESIS_Environment._Time.getDayOfYear());
            // If input randomSeed_Long is negative then the simulation is 
            // continued from the previous simulation.
            if (randomSeed_Long < 0) {
                run_Metadata.setPseudoRandomNumberSeed(randomSeed_Long);
            }
            /*
             * Initialise Collection cache
             * -----------------------------------------------------------------
             */
            message = "Initialise Collection cache";
            log(Level.INFO, message);
            System.out.println(message);
            // LivingFemales
            _GENESIS_AgentCollectionManager.setLivingFemaleDirectory(
                    new File(
                    _ResultDataDirectory_File,
                    "LivingFemales"));
            File previousFemaleDirectory_File = new File(
                    previousResultDataDirectory_File,
                    "LivingFemales");
            Generic_StaticIO.copy(previousFemaleDirectory_File,
                    _ResultDataDirectory_File);
            long aIndexOfLastLivingFemaleCollection =
                    Generic_StaticIO.getArchiveHighestLeaf(
                    previousFemaleDirectory_File,
                    "_");
            GENESIS_FemaleCollection lastLivingFemaleCollection =
                    _GENESIS_AgentCollectionManager.getFemaleCollection(
                    aIndexOfLastLivingFemaleCollection,
                    GENESIS_Person.getTypeLivingFemale_String(),
                    _HandleOutOfMemoryError);
            _GENESIS_AgentCollectionManager._IndexOfLastBornFemale =
                    lastLivingFemaleCollection.getMaxAgentID();
            // LivingMales
            _GENESIS_AgentCollectionManager.setLivingMaleDirectory(
                    new File(
                    _ResultDataDirectory_File,
                    "LivingMales"));
            File previousMaleDirectory_File = new File(
                    previousResultDataDirectory_File,
                    "LivingMales");
            Generic_StaticIO.copy(previousMaleDirectory_File,
                    _ResultDataDirectory_File);
            long aIndexOfLastLivingMaleCollection =
                    Generic_StaticIO.getArchiveHighestLeaf(
                    previousMaleDirectory_File,
                    "_");
            GENESIS_MaleCollection lastLivingMaleCollection =
                    _GENESIS_AgentCollectionManager.getMaleCollection(
                    aIndexOfLastLivingMaleCollection,
                    GENESIS_Person.getTypeLivingMale_String(),
                    _HandleOutOfMemoryError);
            _GENESIS_AgentCollectionManager._IndexOfLastBornMale =
                    lastLivingMaleCollection.getMaxAgentID();
            /*
             * Initialise: _PregnantFemaleIDs;
             * _NearlyDuePregnantFemaleIDs;
             * _NotPregnantFemaleIDs; _LivingFemaleIDs;
             * _LivingMaleIDs.
             */
            message = "Load ID Maps";
            log(Level.INFO, message);
            System.out.println(message);
            File inputFile;
            inputFile = new File(
                    previousResultDataDirectory_File,
                    "PregnantFemale_ID_HashMap.thisFile");
            _PregnantFemaleIDs = (TreeMap<String, TreeMap<String, TreeSet<Long>>>) Generic_StaticIO.readObject(inputFile);
            inputFile = new File(
                    previousResultDataDirectory_File,
                    "NearlyDuePregnantFemale_ID_HashMap.thisFile");
            _NearlyDuePregnantFemaleIDs = (TreeMap<String, TreeMap<String, TreeSet<Long>>>) Generic_StaticIO.readObject(inputFile);
            inputFile = new File(
                    previousResultDataDirectory_File,
                    "NotPregnantFemale_ID_HashMap.thisFile");
            _NotPregnantFemaleIDs = (TreeMap<String, TreeMap<String, TreeSet<Long>>>) Generic_StaticIO.readObject(inputFile);
            inputFile = new File(
                    previousResultDataDirectory_File,
                    "Female_ID_HashMap.thisFile");
            _LivingFemaleIDs = (TreeMap<String, TreeMap<String, TreeSet<Long>>>) Generic_StaticIO.readObject(inputFile);
            inputFile = new File(
                    previousResultDataDirectory_File,
                    "Male_ID_HashMap.thisFile");
            _LivingMaleIDs = (TreeMap<String, TreeMap<String, TreeSet<Long>>>) Generic_StaticIO.readObject(inputFile);
            message = "Copy dead";
            log(Level.INFO, message);
            System.out.println(message);
            /*
             * DeadFemales @TODO Copying these can be expensive. Maybe they can
             * be left in place and loaded as and when needed. Any modifications
             * to a dead persons data should be stored, but what is a good way
             * to do this?
             */
            _GENESIS_AgentCollectionManager.setDeadFemaleDirectory(
                    new File(
                    _ResultDataDirectory_File,
                    "DeadFemales"));
            previousFemaleDirectory_File = new File(
                    previousResultDataDirectory_File,
                    "DeadFemales");
            Generic_StaticIO.copy(
                    previousFemaleDirectory_File,
                    _ResultDataDirectory_File);
            _GENESIS_AgentCollectionManager._LargestIndexOfDeadFemaleCollection =
                    Generic_StaticIO.getArchiveHighestLeaf(
                    previousFemaleDirectory_File,
                    "_");
            /*
             * DeadMales @TODO Copying these can be expensive. Maybe they can be
             * left in place and loaded as and when needed. Any modifications to
             * a dead persons data should be stored, but what is a good way to
             * do this?
             */
            _GENESIS_AgentCollectionManager.setDeadMaleDirectory(
                    new File(
                    _ResultDataDirectory_File,
                    "DeadMales"));
            previousMaleDirectory_File = new File(
                    previousResultDataDirectory_File,
                    "DeadMales");
            Generic_StaticIO.copy(
                    previousMaleDirectory_File,
                    _ResultDataDirectory_File);
            _GENESIS_AgentCollectionManager._LargestIndexOfDeadMaleCollection =
                    Generic_StaticIO.getArchiveHighestLeaf(
                    previousMaleDirectory_File,
                    "_");
            /*
             * Initialise _GENESIS_AgentCollectionManager._DeadFemaleCollection.
             */
            _GENESIS_AgentCollectionManager._DeadFemaleCollection =
                    (GENESIS_FemaleCollection) Generic_StaticIO.readObject(new File(
                    previousResultDataDirectory_File,
                    "Dead_GENESIS_FemaleCollection.thisFile"));
            _GENESIS_AgentCollectionManager._DeadFemaleCollection._GENESIS_Environment = _GENESIS_Environment;
            /*
             * Initialise _GENESIS_AgentCollectionManager._DeadMaleCollection.
             */
            _GENESIS_AgentCollectionManager._DeadMaleCollection =
                    (GENESIS_MaleCollection) Generic_StaticIO.readObject(new File(
                    previousResultDataDirectory_File,
                    "Dead_GENESIS_MaleCollection.thisFile"));
            _GENESIS_AgentCollectionManager._DeadMaleCollection._GENESIS_Environment = _GENESIS_Environment;
            /*
             * Initialise
             * _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap.
             */
            _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap =
                    (HashMap) Generic_StaticIO.readObject(new File(
                    previousResultDataDirectory_File,
                    "Dead_GENESIS_Female_HashMap.thisFile"));
            /*
             * Initialise
             * _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap.
             */
            _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap =
                    (HashMap) Generic_StaticIO.readObject(new File(
                    previousResultDataDirectory_File,
                    "Dead_GENESIS_Male_HashMap.thisFile"));
            /*
             * Load _Demographics._Population.
             */
            message = "Load _Demographics._GENESIS_Population_TreeMap";
            log(Level.INFO, message);
            System.out.println(message);
            GENESIS_Demographics previousResult_Demographics =
                    (GENESIS_Demographics) Generic_StaticIO.readObject(
                    new File(
                    previousResultDataDirectory_File,
                    "Demographics.thisFile"));

            // Set populations ensuring _GENESIS_Environment variables all set to
            // the new correct values.
            _regionIDs = new TreeMap<String, TreeSet<String>>();
            Iterator<String> ite2;
            ite2 = previousResult_Demographics._Population.keySet().iterator();
            while (ite2.hasNext()) {
                String regionID = ite2.next();
                TreeSet<String> subregionIDs = new TreeSet<String>();
                _regionIDs.put(regionID, subregionIDs);
                TreeMap<String, GENESIS_Population> regionPopulation;
                regionPopulation = previousResult_Demographics._Population.get(regionID);
                Iterator<String> ite3 = regionPopulation.keySet().iterator();
                while (ite3.hasNext()) {
                    String subregionID = ite3.next();
                    if (!subregionID.equalsIgnoreCase(regionID)) {
                        subregionIDs.add(subregionID);
                    }
                    GENESIS_Population subregionPopulation = regionPopulation.get(subregionID);
                    subregionPopulation._GENESIS_Environment = _GENESIS_Environment;
                    subregionPopulation.getGenderedAgeBoundPopulation().getFemale().addAll(
                            GENESIS_Collections.deepCopyTo_ArrayList_AgeBound_Population(
                            subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap));
                    subregionPopulation.getGenderedAgeBoundPopulation().getMale().addAll(
                            GENESIS_Collections.deepCopyTo_ArrayList_AgeBound_Population(
                            subregionPopulation._MaleAgeBoundPopulationCount_TreeMap));
                }
            }
            _Demographics._Population = previousResult_Demographics._Population;
            // Init _regionIDs and set _GENESIS_Environment
            ite2 = _Demographics._Population.keySet().iterator();
            while (ite2.hasNext()) {
                String regionID = ite2.next();
                TreeSet<String> subregionIDs = new TreeSet<String>();
                _regionIDs.put(regionID, subregionIDs);
                TreeMap<String, GENESIS_Population> regionPopulation;
                regionPopulation = _Demographics._Population.get(regionID);
                Iterator<String> ite3 = regionPopulation.keySet().iterator();
                while (ite3.hasNext()) {
                    String subregionID = ite3.next();
                    subregionIDs.add(subregionID);
                    regionPopulation.get(subregionID)._GENESIS_Environment = _GENESIS_Environment;
                }
            }

            _Demographics._Migration = previousResult_Demographics._Migration;
            _Demographics._Migration._GENESIS_Environment = _GENESIS_Environment;
            // write migration data
            this._Demographics._Migration.writeMigrationData();
            this._Demographics._Migration.rationaliseMigrationData();

            writeOutStartPopulations(resultMetadataDirectory_File);
            if (randomSeed_Long < 0) {
                /*
                 * Load previous results Random instances for continuing a
                 * simulation...
                 */
                _GENESIS_Environment._Generic_BigDecimal =
                        (Generic_BigDecimal) Generic_StaticIO.readObject(
                        new File(
                        previousResultDataDirectory_File,
                        "GENESIS_Environment_Generic_BigDecimal.thisFile"));
                _RandomArray =
                        (Random[]) Generic_StaticIO.readObject(
                        new File(
                        previousResultDataDirectory_File,
                        "Random.thisFile"));
            } else {
                /**
                 * 0 population initialisation age female (for Year); 1
                 * population initialisation age female (for Days); 2 population
                 * initialisation age female (for Seconds); 3 population
                 * initialisation age female (for Year); 4 population
                 * initialisation age female (for Days); 5 population
                 * initialisation age female (for Seconds); 6 mortality Female;
                 * 7 mortality Male test 7 pregnancy test 8 pregnancy
                 * initialisation 9 pregnancy initialisation 10 twins 11
                 * triplets 12 gender of babies
                 */
                init_RandomArrayMinLength(
                        16,
                        randomSeed_Long,
                        1L);
            }
//            // Set _GENESIS_Environment in Loaded Populations.
//            Iterator<String> ite3 = this._regionIDs.iterator();
//            while (ite3.hasNext()) {
//                String areaCode = ite3.next();
//                HashSet<Long> femaleIDs = _LivingFemaleIDs.get(areaCode);
//                HashSet<Long> maleIDs = _LivingMaleIDs.get(areaCode);
//                Iterator<Long> ite4;
//                ite4 = femaleIDs.iterator();
//                String type = GENESIS_Female.getTypeLivingFemale_String();
//                while (ite3.hasNext()) {
//                    _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                    Long a_Agent_ID = ite4.next();
//                    Long a_Collection_ID =
//                            _GENESIS_AgentCollectionManager.getFemaleCollection_ID(
//                            a_Agent_ID,
//                            type,
//                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                    GENESIS_FemaleCollection a_FemaleCollection =
//                            _GENESIS_AgentCollectionManager.getFemaleCollection(
//                            a_Collection_ID,
//                            type,
//                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                    GENESIS_Female a_Female = a_FemaleCollection.getFemale(
//                            a_Agent_ID,
//                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                    a_Female._GENESIS_Environment = _GENESIS_Environment;
//                }
//                ite4 = maleIDs.iterator();
//                while (ite4.hasNext()) {
//                    _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                    Long a_Agent_ID = ite4.next();
//                    Long a_Collection_ID =
//                            _GENESIS_AgentCollectionManager.getMaleCollection_ID(
//                            a_Agent_ID,
//                            type,
//                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                    GENESIS_MaleCollection a_MaleCollection =
//                            _GENESIS_AgentCollectionManager.getMaleCollection(
//                            a_Collection_ID,
//                            type,
//                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                    GENESIS_Male a_Male = a_MaleCollection.getMale(
//                            a_Agent_ID,
//                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                    a_Male._GENESIS_Environment = _GENESIS_Environment;
//                }
//            }
            //_GENESIS_Environment._AbstractModel._Random = _Random;
//            run_Metadata.setPseudoRandomNumberSeed(
//                    seedRun_Metadata.getPseudoRandomNumberSeed());
            singleYearOfAgeRegionPopulation = _Demographics._Population;
        }
        // Load input Death Count
        TreeMap<String, GENESIS_Population> inputDeathCount;
        inputDeathCount = GENESIS_Demographics.loadInputDeathCount(_GENESIS_Environment,
                parameters.getStartYear(),
                _InitialDeathCount_File,
                _regionIDs);

        // Calculate initial Annual Mortality rates
        int decimalPlacePrecision = _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        TreeMap<String, GENESIS_Mortality> initialMortalityRate;
        initialMortalityRate = GENESIS_Demographics.getInitialMortalityRate(_GENESIS_Environment,
                singleYearOfAgeRegionPopulation,
                inputDeathCount,
                decimalPlacePrecision,
                roundingMode);

        // Load input Birth Count
        TreeMap<String, GENESIS_Population> inputBirthCount;
        inputBirthCount = GENESIS_Demographics.loadInputBirthCount(_GENESIS_Environment,
                parameters.getStartYear(),
                _InitialBirthCount_File,
                _regionIDs);

        // Calculate initial Annual Fertility rates
        TreeMap<String, GENESIS_Fertility> initialFertilityRate;
        initialFertilityRate = GENESIS_Demographics.getInitialFertilityRate(_GENESIS_Environment,
                singleYearOfAgeRegionPopulation,
                inputBirthCount,
                decimalPlacePrecision,
                roundingMode);

        // Estimate Mortality and Fertility rates
        Iterator<String> ite = singleYearOfAgeRegionPopulation.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            GENESIS_Population regionPopulation = singleYearOfAgeRegionPopulation.get(regionID).get(regionID);
            GENESIS_Fertility initialFertility = initialFertilityRate.get(regionID);
            GENESIS_Mortality initialMortality = initialMortalityRate.get(regionID);
            GENESIS_Population birthPop = inputBirthCount.get(regionID);
            GENESIS_Population deathPop = inputDeathCount.get(regionID);
            // Estimate mortality and fertility
            Object[] mortalityAndFertility = GENESIS_MortalityAndFertilityEstimator.getEstimateRates(
                    regionPopulation,
                    initialMortality,
                    initialFertility,
                    birthPop,
                    deathPop,
                    decimalPlacePrecision,
                    roundingMode);
            GENESIS_Mortality mort = (GENESIS_Mortality) mortalityAndFertility[0];
            mort.initDailyAgeMortalityTreeMaps();
            //mort.updateGenderedAgeBoundRates();
            GENESIS_Fertility fert = (GENESIS_Fertility) mortalityAndFertility[1];
            fert._Mortality = mort;
            fert._Miscarriage = new GENESIS_Miscarriage(
                    _GENESIS_Environment,
                    _InitialMiscarriageRate_File);
            fert.init_PregnancyProbabilities();
            //fert.updateAgeBoundRates();
            TreeMap<String, GENESIS_Mortality> regionMortality;
            regionMortality = new TreeMap<String, GENESIS_Mortality>();
            regionMortality.put(regionID, mort);
            _Demographics._Mortality.put(regionID, regionMortality);
            TreeMap<String, GENESIS_Fertility> regionFertility;
            regionFertility = new TreeMap<String, GENESIS_Fertility>();
            regionFertility.put(regionID, fert);
            _Demographics._Fertility.put(regionID, regionFertility);
            TreeMap<String, GENESIS_Miscarriage> regionMiscarriage;
            regionMiscarriage = new TreeMap<String, GENESIS_Miscarriage>();
            regionMiscarriage.put(regionID, fert._Miscarriage);
            _Demographics._Miscarriage.put(regionID, regionMiscarriage);
        }

        // Write out estimate Mortality and Fertility rates for comparison
        writeOutStartPopulations(resultMetadataDirectory_File);
        writeOutEstimatedMortality(resultMetadataDirectory_File);
        writeOutEstimatedFertility(resultMetadataDirectory_File);

        //if (indexOfSeedPopulationRun_Long != null) {
        if (continueSimulation) {
//            String message;
//            message = "Loading existing result";
//            log(Level.FINE, message);
//            System.out.println(message);
//            /*
//             * Load existing result @TODO load in Random used in previous run so
//             * that continuing a run will produce the same result over a period.
//             * This will require the Random instances used in the seed run to be
//             * serialised as that run is output.
//             */
//            // Debug output
//            message = "_Directory " + _Directory;
//            log(Level.INFO, message);
//            System.out.println(message);
//            message = "indexOfSeedPopulationRun " + indexOfSeedPopulationRun;
//            log(Level.INFO, message);
//            System.out.println(message);
//            message = "runIndex_Long " + runIndex_Long;
//            log(Level.INFO, message);
//            System.out.println(message);
//            message = "_GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory "
//                    + _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory;
//            log(Level.INFO, message);
//            System.out.println(message);
//            _SeedDirectory_File = new File(
//                    Generic_StaticIO.getObjectDirectory(
//                    _Directory,
//                    indexOfSeedPopulationRun,
//                    runIndex_Long,
//                    _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory),
//                    "" + indexOfSeedPopulationRun);
//            File previousResultDataDirectory_File = new File(
//                    _SeedDirectory_File,
//                    "data");
//            File previousResultMetadata_File = new File(
//                    _SeedDirectory_File.getPath() + "/metadata/metadata.xml");
//            MetadataType seedRun_Metadata =
//                    XMLConverter.loadMetadataFromXMLFile(previousResultMetadata_File);
//            /*
//             * Set GENESIS_Time
//             */
//            startYear = new Integer(seedRun_Metadata.getEndYear());
//            startDay = new Integer(seedRun_Metadata.getEndDay());
//            _GENESIS_Environment._Time = new GENESIS_Time(startYear, startDay);
//            //_GENESIS_Environment._Time.addDay();
//            int initialYear = new Integer(seedRun_Metadata.getInitialYear());
//            int initialDay = new Integer(seedRun_Metadata.getInitialDay());
//            _GENESIS_Environment._initial_Time = new GENESIS_Time(initialYear, initialDay);
//            // Initialise metadata
//            run_Metadata.setInitialYear(seedRun_Metadata.getInitialYear());
//            run_Metadata.setInitialDay(seedRun_Metadata.getInitialDay());
//            run_Metadata.setStartYear((int) _GENESIS_Environment._Time.getYear());
//            run_Metadata.setStartDay(_GENESIS_Environment._Time.getDayOfYear());
//            // If input randomSeed_Long is negative then the simulation is 
//            // continued from the previous simulation.
//            if (randomSeed_Long < 0) {
//                run_Metadata.setPseudoRandomNumberSeed(randomSeed_Long);
//            }
//            /*
//             * Initialise Collection cache
//             * -----------------------------------------------------------------
//             */
//            message = "Initialise Collection cache";
//            log(Level.INFO, message);
//            System.out.println(message);
//            // LivingFemales
//            _GENESIS_AgentCollectionManager.setLivingFemaleDirectory(
//                    new File(
//                    _ResultDataDirectory_File,
//                    "LivingFemales"));
//            File previousFemaleDirectory_File = new File(
//                    previousResultDataDirectory_File,
//                    "LivingFemales");
//            Generic_StaticIO.copy(previousFemaleDirectory_File,
//                    _ResultDataDirectory_File);
//            long aIndexOfLastLivingFemaleCollection =
//                    Generic_StaticIO.getArchiveHighestLeaf(
//                    previousFemaleDirectory_File,
//                    "_");
//            GENESIS_FemaleCollection lastLivingFemaleCollection =
//                    _GENESIS_AgentCollectionManager.getFemaleCollection(
//                    aIndexOfLastLivingFemaleCollection,
//                    GENESIS_Person.getTypeLivingFemale_String(),
//                    _HandleOutOfMemoryError);
//            _GENESIS_AgentCollectionManager._IndexOfLastBornFemale =
//                    lastLivingFemaleCollection.getMaxAgentID();
//            // LivingMales
//            _GENESIS_AgentCollectionManager.setLivingMaleDirectory(
//                    new File(
//                    _ResultDataDirectory_File,
//                    "LivingMales"));
//            File previousMaleDirectory_File = new File(
//                    previousResultDataDirectory_File,
//                    "LivingMales");
//            Generic_StaticIO.copy(previousMaleDirectory_File,
//                    _ResultDataDirectory_File);
//            long aIndexOfLastLivingMaleCollection =
//                    Generic_StaticIO.getArchiveHighestLeaf(
//                    previousMaleDirectory_File,
//                    "_");
//            GENESIS_MaleCollection lastLivingMaleCollection =
//                    _GENESIS_AgentCollectionManager.getMaleCollection(
//                    aIndexOfLastLivingMaleCollection,
//                    GENESIS_Person.getTypeLivingMale_String(),
//                    _HandleOutOfMemoryError);
//            _GENESIS_AgentCollectionManager._IndexOfLastBornMale =
//                    lastLivingMaleCollection.getMaxAgentID();
//            /*
//             * Initialise: _PregnantFemaleIDs;
//             * _NearlyDuePregnantFemaleIDs;
//             * _NotPregnantFemaleIDs; _LivingFemaleIDs;
//             * _LivingMaleIDs.
//             */
//            message = "Load ID Maps";
//            log(Level.INFO, message);
//            System.out.println(message);
//            File inputFile;
//            inputFile = new File(
//                    previousResultDataDirectory_File,
//                    "PregnantFemale_ID_HashMap.thisFile");
//            _PregnantFemaleIDs = (TreeMap<String, TreeMap<String, TreeSet<Long>>>) Generic_StaticIO.readObject(inputFile);
//            inputFile = new File(
//                    previousResultDataDirectory_File,
//                    "NearlyDuePregnantFemale_ID_HashMap.thisFile");
//            _NearlyDuePregnantFemaleIDs = (TreeMap<String, TreeMap<String, TreeSet<Long>>>) Generic_StaticIO.readObject(inputFile);
//            inputFile = new File(
//                    previousResultDataDirectory_File,
//                    "NotPregnantFemale_ID_HashMap.thisFile");
//            _NotPregnantFemaleIDs = (TreeMap<String, TreeMap<String, TreeSet<Long>>>) Generic_StaticIO.readObject(inputFile);
//            inputFile = new File(
//                    previousResultDataDirectory_File,
//                    "Female_ID_HashMap.thisFile");
//            _LivingFemaleIDs = (TreeMap<String, TreeMap<String, TreeSet<Long>>>) Generic_StaticIO.readObject(inputFile);
//            inputFile = new File(
//                    previousResultDataDirectory_File,
//                    "Male_ID_HashMap.thisFile");
//            _LivingMaleIDs = (TreeMap<String, TreeMap<String, TreeSet<Long>>>) Generic_StaticIO.readObject(inputFile);
//            message = "Copy dead";
//            log(Level.INFO, message);
//            System.out.println(message);
//            /*
//             * DeadFemales @TODO Copying these can be expensive. Maybe they can
//             * be left in place and loaded as and when needed. Any modifications
//             * to a dead persons data should be stored, but what is a good way
//             * to do this?
//             */
//            _GENESIS_AgentCollectionManager.setDeadFemaleDirectory(
//                    new File(
//                    _ResultDataDirectory_File,
//                    "DeadFemales"));
//            previousFemaleDirectory_File = new File(
//                    previousResultDataDirectory_File,
//                    "DeadFemales");
//            Generic_StaticIO.copy(
//                    previousFemaleDirectory_File,
//                    _ResultDataDirectory_File);
//            _GENESIS_AgentCollectionManager._LargestIndexOfDeadFemaleCollection =
//                    Generic_StaticIO.getArchiveHighestLeaf(
//                    previousFemaleDirectory_File,
//                    "_");
//            /*
//             * DeadMales @TODO Copying these can be expensive. Maybe they can be
//             * left in place and loaded as and when needed. Any modifications to
//             * a dead persons data should be stored, but what is a good way to
//             * do this?
//             */
//            _GENESIS_AgentCollectionManager.setDeadMaleDirectory(
//                    new File(
//                    _ResultDataDirectory_File,
//                    "DeadMales"));
//            previousMaleDirectory_File = new File(
//                    previousResultDataDirectory_File,
//                    "DeadMales");
//            Generic_StaticIO.copy(
//                    previousMaleDirectory_File,
//                    _ResultDataDirectory_File);
//            _GENESIS_AgentCollectionManager._LargestIndexOfDeadMaleCollection =
//                    Generic_StaticIO.getArchiveHighestLeaf(
//                    previousMaleDirectory_File,
//                    "_");
//            /*
//             * Initialise _GENESIS_AgentCollectionManager._DeadFemaleCollection.
//             */
//            _GENESIS_AgentCollectionManager._DeadFemaleCollection =
//                    (GENESIS_FemaleCollection) Generic_StaticIO.readObject(new File(
//                    previousResultDataDirectory_File,
//                    "Dead_GENESIS_FemaleCollection.thisFile"));
//            _GENESIS_AgentCollectionManager._DeadFemaleCollection._GENESIS_Environment = _GENESIS_Environment;
//            /*
//             * Initialise _GENESIS_AgentCollectionManager._DeadMaleCollection.
//             */
//            _GENESIS_AgentCollectionManager._DeadMaleCollection =
//                    (GENESIS_MaleCollection) Generic_StaticIO.readObject(new File(
//                    previousResultDataDirectory_File,
//                    "Dead_GENESIS_MaleCollection.thisFile"));
//            _GENESIS_AgentCollectionManager._DeadMaleCollection._GENESIS_Environment = _GENESIS_Environment;
//            /*
//             * Initialise
//             * _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap.
//             */
//            _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap =
//                    (HashMap) Generic_StaticIO.readObject(new File(
//                    previousResultDataDirectory_File,
//                    "Dead_GENESIS_Female_HashMap.thisFile"));
//            /*
//             * Initialise
//             * _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap.
//             */
//            _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap =
//                    (HashMap) Generic_StaticIO.readObject(new File(
//                    previousResultDataDirectory_File,
//                    "Dead_GENESIS_Male_HashMap.thisFile"));
//            /*
//             * Load _Demographics._Population.
//             */
//            message = "Load _Demographics._GENESIS_Population_TreeMap";
//            log(Level.INFO, message);
//            System.out.println(message);
//            GENESIS_Demographics previousResult_Demographics =
//                    (GENESIS_Demographics) Generic_StaticIO.readObject(
//                    new File(
//                    previousResultDataDirectory_File,
//                    "Demographics.thisFile"));
//
//            // Set populations ensuring _GENESIS_Environment variables all set to
//            // the new correct values.
//            _regionIDs = new TreeMap<String, TreeSet<String>>();
//            Iterator<String> ite2;
//            ite2 = previousResult_Demographics._Population.keySet().iterator();
//            while (ite2.hasNext()) {
//                String regionID = ite2.next();
//                TreeSet<String> subregionIDs = new TreeSet<String>();
//                _regionIDs.put(regionID, subregionIDs);
//                TreeMap<String, GENESIS_Population> regionPopulation;
//                regionPopulation = previousResult_Demographics._Population.get(regionID);
//                Iterator<String> ite3 = regionPopulation.keySet().iterator();
//                while (ite3.hasNext()) {
//                    String subregionID = ite3.next();
//                    if (subregionID.equalsIgnoreCase(regionID)) {
//                        subregionIDs.add(subregionID);
//                    }
//                    GENESIS_Population subregionPopulation = regionPopulation.get(subregionID);
//                    subregionPopulation._GENESIS_Environment = _GENESIS_Environment;
//                    subregionPopulation.getGenderedAgeBoundPopulation().getFemale().addAll(
//                            GENESIS_Collections.deepCopyTo_ArrayList_AgeBound_Population(
//                            subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap));
//                    subregionPopulation.getGenderedAgeBoundPopulation().getMale().addAll(
//                            GENESIS_Collections.deepCopyTo_ArrayList_AgeBound_Population(
//                            subregionPopulation._MaleAgeBoundPopulationCount_TreeMap));
//                }
//            }
//            _Demographics._Population = previousResult_Demographics._Population;
//            writeOutStartPopulations(resultMetadataDirectory_File);
//            if (randomSeed_Long < 0) {
//                /*
//                 * Load previous results Random instances for continuing a
//                 * simulation...
//                 */
//                _GENESIS_Environment._Generic_BigDecimal =
//                        (Generic_BigDecimal) Generic_StaticIO.readObject(
//                        new File(
//                        previousResultDataDirectory_File,
//                        "GENESIS_Environment_Generic_BigDecimal.thisFile"));
//                _RandomArray =
//                        (Random[]) Generic_StaticIO.readObject(
//                        new File(
//                        previousResultDataDirectory_File,
//                        "Random.thisFile"));
//            } else {
////                /**
////                 * 0 population initialisation age female (for Year); 1 population
////                 * initialisation age female (for Days); 2 population
////                 * initialisation age female (for Seconds); 3 population
////                 * initialisation age female (for Year); 4 population
////                 * initialisation age female (for Days); 5 population
////                 * initialisation age female (for Seconds); 6 mortality Female;
////                 * 7 mortality Male test 7 pregnancy test 8 pregnancy
////                 * initialisation 9 pregnancy initialisation 10 twins 11
////                 * triplets 12 gender of babies
////                 */
////                init_RandomArrayMinLength(
////                        15,
////                        randomSeed_Long,
////                        1L);
//            }
////            // Set _GENESIS_Environment in Loaded Populations.
////            Iterator<String> ite3 = this._regionIDs.iterator();
////            while (ite3.hasNext()) {
////                String areaCode = ite3.next();
////                HashSet<Long> femaleIDs = _LivingFemaleIDs.get(areaCode);
////                HashSet<Long> maleIDs = _LivingMaleIDs.get(areaCode);
////                Iterator<Long> ite4;
////                ite4 = femaleIDs.iterator();
////                String type = GENESIS_Female.getTypeLivingFemale_String();
////                while (ite3.hasNext()) {
////                    _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
////                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
////                    Long a_Agent_ID = ite4.next();
////                    Long a_Collection_ID =
////                            _GENESIS_AgentCollectionManager.getFemaleCollection_ID(
////                            a_Agent_ID,
////                            type,
////                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
////                    GENESIS_FemaleCollection a_FemaleCollection =
////                            _GENESIS_AgentCollectionManager.getFemaleCollection(
////                            a_Collection_ID,
////                            type,
////                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
////                    GENESIS_Female a_Female = a_FemaleCollection.getFemale(
////                            a_Agent_ID,
////                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
////                    a_Female._GENESIS_Environment = _GENESIS_Environment;
////                }
////                ite4 = maleIDs.iterator();
////                while (ite4.hasNext()) {
////                    _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
////                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
////                    Long a_Agent_ID = ite4.next();
////                    Long a_Collection_ID =
////                            _GENESIS_AgentCollectionManager.getMaleCollection_ID(
////                            a_Agent_ID,
////                            type,
////                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
////                    GENESIS_MaleCollection a_MaleCollection =
////                            _GENESIS_AgentCollectionManager.getMaleCollection(
////                            a_Collection_ID,
////                            type,
////                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
////                    GENESIS_Male a_Male = a_MaleCollection.getMale(
////                            a_Agent_ID,
////                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
////                    a_Male._GENESIS_Environment = _GENESIS_Environment;
////                }
////            }
//            //_GENESIS_Environment._AbstractModel._Random = _Random;
////            run_Metadata.setPseudoRandomNumberSeed(
////                    seedRun_Metadata.getPseudoRandomNumberSeed());
        } else {
            /**
             * Initialise _RandomArray 0 pregnancy initialisation age Male (for
             * Days) 1 pregnancy initialisation age Male (for Seconds) 2
             * pregnancy initialisation age Female (for Days) 3 pregnancy
             * initialisation age Female (for Seconds) 5 mortality Female test 6
             * mortality Male test 7 pregnancy test 8 pregnancy initialisation 9
             * pregnancy initialisation 10 twins 11 triplets 12 gender of babies
             */
            init_RandomArrayMinLength(
                    16,
                    randomSeed_Long,
                    1L);
            // Initialise in memory stores
            log(Level.FINE, "Initialise in memory stores");
            /**
             * Initialise Collections of the Living
             */
            Long a_AgentCollection_ID = 0L;
            // Females
            GENESIS_FemaleCollection a_LivingFemaleCollection =
                    new GENESIS_FemaleCollection(
                    _GENESIS_Environment,
                    a_AgentCollection_ID,
                    GENESIS_Person.getTypeLivingFemale_String());
            _GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap.put(
                    a_AgentCollection_ID,
                    a_LivingFemaleCollection);
            _GENESIS_AgentCollectionManager.setLivingFemaleDirectory(
                    new File(
                    _ResultDataDirectory_File,
                    "LivingFemales"));
            Generic_StaticIO.initialiseArchive(
                    _GENESIS_AgentCollectionManager.getLivingFemaleDirectory(),
                    _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory);
            // Males
            GENESIS_MaleCollection a_MaleCollection =
                    new GENESIS_MaleCollection(
                    _GENESIS_Environment,
                    a_AgentCollection_ID,
                    GENESIS_Person.getTypeLivingMale_String());
            _GENESIS_AgentCollectionManager._LivingMaleCollection_HashMap.put(
                    a_AgentCollection_ID,
                    a_MaleCollection);
            _GENESIS_AgentCollectionManager.setLivingMaleDirectory(
                    new File(
                    _ResultDataDirectory_File,
                    "LivingMales"));
            Generic_StaticIO.initialiseArchive(
                    _GENESIS_AgentCollectionManager.getLivingMaleDirectory(),
                    _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory);
            /*
             * Initialise Collections of the Dead.
             */
            // Females
            _GENESIS_AgentCollectionManager._DeadFemaleCollection =
                    new GENESIS_FemaleCollection(
                    _GENESIS_Environment,
                    a_AgentCollection_ID,
                    GENESIS_Person.getTypeDeadFemale_String());
            _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap =
                    new HashMap<Long, Long>();
            _GENESIS_AgentCollectionManager.setDeadFemaleDirectory(
                    new File(
                    _ResultDataDirectory_File,
                    "DeadFemales"));
            Generic_StaticIO.initialiseArchive(
                    _GENESIS_AgentCollectionManager.getDeadFemaleDirectory(),
                    _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory);
            // Males
            _GENESIS_AgentCollectionManager._DeadMaleCollection =
                    new GENESIS_MaleCollection(
                    _GENESIS_Environment,
                    a_AgentCollection_ID,
                    GENESIS_Person.getTypeDeadMale_String());
            _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap =
                    new HashMap<Long, Long>();
            _GENESIS_AgentCollectionManager.setDeadMaleDirectory(
                    new File(
                    _ResultDataDirectory_File,
                    "DeadMales"));
            Generic_StaticIO.initialiseArchive(
                    _GENESIS_AgentCollectionManager.getDeadMaleDirectory(),
                    _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory);

//            // Load populations that may not be in single years of age
//            _Demographics._Population = GENESIS_Population.loadInputPopulation(
//                    _GENESIS_Environment,
//                    _InitialPopulation_File);


//            _Demographics._Total_Population = _Demographics._Population.remove(
//                    GENESIS_Demographics.TotalPopulationName_String);
            // Prepare maps of area code and identifier sets for specific types of people
            _regionIDs = new TreeMap<String, TreeSet<String>>();
            _LivingFemaleIDs = new TreeMap<String, TreeMap<String, TreeSet<Long>>>();
            _LivingMaleIDs = new TreeMap<String, TreeMap<String, TreeSet<Long>>>();
            _NotPregnantFemaleIDs = new TreeMap<String, TreeMap<String, TreeSet<Long>>>();
            _PregnantFemaleIDs = new TreeMap<String, TreeMap<String, TreeSet<Long>>>();
            _NearlyDuePregnantFemaleIDs = new TreeMap<String, TreeMap<String, TreeSet<Long>>>();
//            ite = _Demographics._Population.keySet().iterator();
//            while (ite.hasNext()) {
//                String regionCode = ite.next();
//                TreeSet<String> subregionIDs = new TreeMap<String, TreeSet<String>>();
//                _regionIDs.put(regionCode, subregionIDs);
//                _LivingFemaleIDs.put(regionCode, new TreeSet<Long>());
//                _LivingMaleIDs.put(regionCode, new TreeSet<Long>());
//                _NotPregnantFemaleIDs.put(regionCode, new TreeSet<Long>());
//                _PregnantFemaleIDs.put(regionCode, new TreeSet<Long>());
//                _NearlyDuePregnantFemaleIDs.put(regionCode, new TreeSet<Long>());
//            }
            init_Demographics(); // init_Demographics() does very little in this context
            initialiseSubregionPopulations(singleYearOfAgeRegionPopulation);
            writeOutStartPopulations(resultMetadataDirectory_File);
            log(Level.FINE,
                    "_TestMemory.getTotalFreeMemory() "
                    + _GENESIS_Environment.getTotalFreeMemory(_GENESIS_Environment._HandleOutOfMemoryError_boolean));
            //_GENESIS_Environment._Time.subtractDay();
            // Initialise metadata
            run_Metadata.setInitialYear(startYear);
            run_Metadata.setInitialDay(startDay);
            run_Metadata.setStartYear(startYear);
            run_Metadata.setStartDay(startDay);
        }
        // Initialise theOAtoMSOALookup
        initLookUpMSOAfromOAHashMap(fileSeparator);
        run0();
        run_Metadata.setEndYear((int) _GENESIS_Environment._Time.getYear());
        run_Metadata.setEndDay(_GENESIS_Environment._Time.getDayOfYear());
        XMLConverter.saveMetadataToXMLFile(runMetadata_File, run_Metadata);
        return _Directory.toString();
    }

    public void run0() {
        // Initialise executorService
        this.executorService = getExecutorService();
        HashSet<Future> futures = simulate(
                _Years);
        System.out.println("Simulation complete. Writing out data for restart");
        // Store living population so they can be reloaded and run for more iterations
        _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(_GENESIS_Environment._HandleOutOfMemoryError_boolean).swapToFile_AgentCollections(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
        File outputFile;
        /*
         * Write out _Demographics.
         */
        System.out.println("Write out _Demographics...");
        outputFile = new File(
                _ResultDataDirectory_File,
                "Demographics.thisFile");
        Generic_StaticIO.writeObject(
                _Demographics,
                outputFile);
        /*
         * Write out: _PregnantFemaleIDs;
         * _NearlyDuePregnantFemaleIDs; _NotPregnantFemaleIDs;
         * _LivingFemaleIDs; _LivingMaleIDs.
         */
        System.out.println("Write out ID indexes...");
        outputFile = new File(
                _ResultDataDirectory_File,
                "PregnantFemale_ID_HashMap.thisFile");
        Generic_StaticIO.writeObject(
                _PregnantFemaleIDs,
                outputFile);
        outputFile = new File(
                _ResultDataDirectory_File,
                "NearlyDuePregnantFemale_ID_HashMap.thisFile");
        Generic_StaticIO.writeObject(
                _NearlyDuePregnantFemaleIDs,
                outputFile);
        outputFile = new File(
                _ResultDataDirectory_File,
                "NotPregnantFemale_ID_HashMap.thisFile");
        Generic_StaticIO.writeObject(
                _NotPregnantFemaleIDs,
                outputFile);
        outputFile = new File(
                _ResultDataDirectory_File,
                "Female_ID_HashMap.thisFile");
        Generic_StaticIO.writeObject(
                _LivingFemaleIDs,
                outputFile);
        outputFile = new File(
                _ResultDataDirectory_File,
                "Male_ID_HashMap.thisFile");
        Generic_StaticIO.writeObject(
                _LivingMaleIDs,
                outputFile);

        //writeOutLivingCollectionNotAlreadyStoredOnFile();

        //writeOutDeadCollectionNotAlreadyStoredOnFile();

        /**
         * Write out Random instances for a check point restart of a simulation:
         * _GENESIS_Environment._Generic_BigDecimal _Random
         */
        System.out.println("Write out Random instances...");
        outputFile = new File(
                _ResultDataDirectory_File,
                "GENESIS_Environment_Generic_BigDecimal.thisFile");
        Generic_StaticIO.writeObject(_GENESIS_Environment._Generic_BigDecimal,
                outputFile);
        outputFile = new File(
                _ResultDataDirectory_File,
                "Random.thisFile");
        Generic_StaticIO.writeObject(
                _RandomArray,
                outputFile);
        Generic_Execution.shutdownExecutorService(executorService, futures, this);
    }

    /**
     * Write out: _GENESIS_AgentCollectionManager._DeadFemaleCollection;
     * _GENESIS_AgentCollectionManager._DeadMaleCollection;
     * _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap;
     * _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap.
     */
    protected void writeOutDeadCollectionNotAlreadyStoredOnFile() {
        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                _HandleOutOfMemoryError);
        File outputFile;
        System.out.println("Write out Dead Collections...");
        // Dead Female Collection
        outputFile = new File(
                _ResultDataDirectory_File,
                "Dead_GENESIS_FemaleCollection.thisFile");
        Generic_StaticIO.writeObject(
                _GENESIS_AgentCollectionManager._DeadFemaleCollection,
                outputFile);
        _GENESIS_AgentCollectionManager._DeadFemaleCollection = null;
        // Dead Male Collection
        outputFile = new File(
                _ResultDataDirectory_File,
                "Dead_GENESIS_MaleCollection.thisFile");
        Generic_StaticIO.writeObject(
                _GENESIS_AgentCollectionManager._DeadMaleCollection,
                outputFile);
        _GENESIS_AgentCollectionManager._DeadMaleCollection = null;
        // Dead Female Collection HashMap
        outputFile = new File(
                _ResultDataDirectory_File,
                //"Dead_GENESIS_Female_HashMap<Long,Long>.thisFile"); Invalid on Windows
                "Dead_GENESIS_Female_HashMap.thisFile");
        Generic_StaticIO.writeObject(
                _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap,
                outputFile);
        _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap = null;
        // Dead Male Collection HashMap
        outputFile = new File(
                _ResultDataDirectory_File,
                //"Dead_GENESIS_Male_HashMap<Long,Long>.thisFile"); Invalid on Windows
                "Dead_GENESIS_Male_HashMap.thisFile");
        Generic_StaticIO.writeObject(
                _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap,
                outputFile);
        _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap = null;
    }

    /**
     * Writes out Living Collections that are not already stored on File from:
     * _GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap; and,
     * _GENESIS_AgentCollectionManager._LivingMaleCollection_HashMap.
     * @param handleOutOfMemoryError
     */
    protected void writeOutLivingCollectionNotAlreadyStoredOnFile(
            boolean handleOutOfMemoryError) {
        try {
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
            writeOutLivingCollectionNotAlreadyStoredOnFile();
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                _GENESIS_Environment.clear_MemoryReserve();
                _GENESIS_Environment.swapToFile_DataAny();
                _GENESIS_Environment.init_MemoryReserve(_GENESIS_Environment.HandleOutOfMemoryErrorFalse);
                writeOutLivingCollectionNotAlreadyStoredOnFile(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Writes out Living Collections that are not already stored on File from:
     * _GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap; and,
     * _GENESIS_AgentCollectionManager._LivingMaleCollection_HashMap.
     */
    protected void writeOutLivingCollectionNotAlreadyStoredOnFile() {
        String methodName = "Write out Living Collections not already stored on File...";
        System.out.println(methodName);
//        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                    _HandleOutOfMemoryError);
        Iterator<Long> iterator;
        // Female
        iterator = _GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap.keySet().iterator();
        while (iterator.hasNext()) {
            Long a_ID = iterator.next();
            GENESIS_FemaleCollection c;
            c = _GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap.get(a_ID);
            if (c != null) {
                c.write(false); // Has to be false otherwise a concurrent modification exception may occur!
                c = null;
            }
        }
        // Male
        iterator = _GENESIS_AgentCollectionManager._LivingMaleCollection_HashMap.keySet().iterator();
        while (iterator.hasNext()) {
            Long a_ID = iterator.next();
            GENESIS_MaleCollection c;
            c = _GENESIS_AgentCollectionManager._LivingMaleCollection_HashMap.get(a_ID);
            if (c != null) {
                c.write(false); // Has to be false otherwise a concurrent modification exception may occur!
                c = null;
            }
        }
    }

    public void initCollections(
            String underscore) {
        _GENESIS_AgentCollectionManager.init_LivingFemaleCollection_HashMap();
        long aIndexOfLastLivingFemaleCollection =
                Generic_StaticIO.getArchiveHighestLeaf(
                _GENESIS_AgentCollectionManager.getLivingFemaleDirectory(),
                underscore);
        for (long i = 0; i <= aIndexOfLastLivingFemaleCollection; i++) {
            _GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap.put(i, null);
        }
        _GENESIS_AgentCollectionManager.init_LivingMaleCollection_HashMap();
        long aIndexOfLastLivingMaleCollection =
                Generic_StaticIO.getArchiveHighestLeaf(
                _GENESIS_AgentCollectionManager.getLivingMaleDirectory(),
                underscore);
        for (long i = 0; i <= aIndexOfLastLivingMaleCollection; i++) {
            _GENESIS_AgentCollectionManager._LivingMaleCollection_HashMap.put(i, null);
        }
    }

    /**
     * Initialises _Demographics. _Demographics._Population are loaded from
     * existing GENESIS_FemaleCollections and GENESIS_MaleCollections
     */
    public void init_Demographics() {
        boolean handleOutOfMemoryError = false;
        try {
            String underscore = "_";
            initCollections(underscore);
            //_Demographics._Population = new GENESIS_Population(_GENESIS_Environment);
            long age;
            Iterator<Long> ite3;
            ite3 = _GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap.keySet().iterator();
            while (ite3.hasNext()) {
                GENESIS_FemaleCollection a_GENESIS_FemaleCollection = _GENESIS_AgentCollectionManager.getFemaleCollection(
                        ite3.next(),
                        GENESIS_Person.getTypeLivingFemale_String(),
                        _HandleOutOfMemoryError);
                HashMap<Long, GENESIS_Agent> agent_ID_HashMap =
                        a_GENESIS_FemaleCollection.get_Agent_ID_Agent_HashMap();
                Iterator<Long> ite2 = agent_ID_HashMap.keySet().iterator();
                while (ite2.hasNext()) {
                    Long a_Agent_ID = ite2.next();
                    GENESIS_Female female = _GENESIS_AgentCollectionManager.getFemale(
                            a_Agent_ID,
                            GENESIS_Person.getTypeLivingFemale_String(),
                            _HandleOutOfMemoryError);
                    female._GENESIS_Environment = _GENESIS_Environment;
                    female._GENESIS_AgentCollectionManager =
                            _GENESIS_AgentCollectionManager;
                    age = female.getAge().getAgeInYears();
                    GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
                    String regionID = female.getRegionID();
                    TreeMap<String, TreeSet<Long>> regionLivingFemaleIDs = _LivingFemaleIDs.get(regionID);
                    TreeMap<String, GENESIS_Population> regionPopulation;
                    regionPopulation = _Demographics._Population.get(regionID);
                    GENESIS_Population subregionPopulation = null;
                    Iterator<String> ite = regionPopulation.keySet().iterator();
                    while (ite.hasNext()) {
                        String subregionID = ite.next();
                        regionLivingFemaleIDs.get(subregionID).add(a_Agent_ID);
                        subregionPopulation = regionPopulation.get(subregionID);
                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                                subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                                ageBound,
                                BigDecimal.ONE,
                                handleOutOfMemoryError);
                    }
                    subregionPopulation.updateGenderedAgePopulation();
                }
            }
            ite3 = _GENESIS_AgentCollectionManager._LivingMaleCollection_HashMap.keySet().iterator();
            while (ite3.hasNext()) {
                GENESIS_MaleCollection a_GENESIS_MaleCollection = _GENESIS_AgentCollectionManager.getMaleCollection(
                        ite3.next(),
                        GENESIS_Person.getTypeLivingMale_String(),
                        _HandleOutOfMemoryError);
                HashMap<Long, GENESIS_Agent> agent_ID_HashMap =
                        a_GENESIS_MaleCollection.get_Agent_ID_Agent_HashMap();
                Iterator<Long> ite2 = agent_ID_HashMap.keySet().iterator();
                while (ite2.hasNext()) {
                    Long a_Agent_ID = ite2.next();
                    GENESIS_Male male = _GENESIS_AgentCollectionManager.getMale(
                            a_Agent_ID,
                            GENESIS_Person.getTypeLivingMale_String(),
                            _HandleOutOfMemoryError);
                    male._GENESIS_Environment = _GENESIS_Environment;
                    male._GENESIS_AgentCollectionManager =
                            _GENESIS_AgentCollectionManager;
                    age = male.getAge().getAgeInYears();
                    GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
                    String regionID = male.getRegionID();
                    TreeMap<String, TreeSet<Long>> regionLivingMaleIDs = _LivingMaleIDs.get(regionID);
                    TreeMap<String, GENESIS_Population> regionPopulation;
                    regionPopulation = _Demographics._Population.get(regionID);
                    GENESIS_Population subregionPopulation = null;
                    Iterator<String> ite = regionPopulation.keySet().iterator();
                    while (ite.hasNext()) {
                        String subregionID = ite.next();
                        regionLivingMaleIDs.get(subregionID).add(a_Agent_ID);
                        subregionPopulation = regionPopulation.get(subregionID);
                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                                subregionPopulation._MaleAgeBoundPopulationCount_TreeMap,
                                ageBound,
                                BigDecimal.ONE,
                                handleOutOfMemoryError);
                    }
                    subregionPopulation.updateGenderedAgePopulation();
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

//    /**
//     * Initialises persons in a simple society.
//     * There are a number of households each consisting of 100 Males and 100
//     * Females as yet unrelated.
//     */
//    public void initialisePopulation(
//            GENESIS_Time a_Time) {
//        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//        _LivingMaleIDs = new HashMap<String,HashSet<Long>>();
//        _LivingFemaleIDs = new HashMap<String,HashSet<Long>>();
//        _NotPregnantFemaleIDs = new HashSet<Long>();
//        _PregnantFemaleIDs = new HashSet<Long>();
//        _NearlyDuePregnantFemaleIDs = new HashSet<Long>();
//        //Integer eldestFemaleAgeInYears = _Demographics._Population._FemaleAgeBoundPopulationCount_TreeMap.lastKey();
//        //Integer eldestMaleAgeInYears = _Demographics._Population._MaleAgeBoundPopulationCount_TreeMap.lastKey();
//        //int eldestPersonAgeInYears = Math.max(eldestFemaleAgeInYears, eldestMaleAgeInYears);
//        GENESIS_Time minBirth_Time;
//        GENESIS_Time maxBirth_Time;
//        GENESIS_Time a_Birth_Time;
//        GENESIS_Female a_Female;
//        GENESIS_Male a_Male;
//        BigDecimal population;
//        BigInteger population_BigInteger;
//        Iterator<Integer> ite3;
//        Integer age;
//        Household a_Household = null;
//        Vector_Point2D a_VectorPoint2D = null;
//        ite3 = _Demographics._Population.get(GENESIS_Demographics.TotalPopulationName_String)._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
//        while (ite3.hasNext()) {
//            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            age = ite3.next();
//            population = _Demographics._Population.get(GENESIS_Demographics.TotalPopulationName_String)._FemaleAgeBoundPopulationCount_TreeMap.get(age);
//            population_BigInteger = population.toBigInteger();
//            log(Level.FINE, "Initialising " + population + " Females aged " + age);
//            for (BigInteger i = BigInteger.ZERO; i.compareTo(population_BigInteger) == -1; i = i.add(BigInteger.ONE)) {
//                minBirth_Time = a_Time.add(
//                        new GENESIS_Time(-age - 1, 0, 0));
//                maxBirth_Time = a_Time.add(
//                        new GENESIS_Time(-age, 0, 0));
//                maxBirth_Time.subtractDay();
//                a_Birth_Time = GENESIS_Time.getRandomTime(
//                        minBirth_Time,
//                        maxBirth_Time,
//                        _GENESIS_Environment._AbstractModel._Random);
//                a_Female = _GENESIS_Environment._PersonFactory.createFemale(
//                        a_Birth_Time,
//                        a_Household,
//                        a_VectorPoint2D,
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                Long a_Agent_ID = a_Female.get_Agent_ID(
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                _LivingFemaleIDs.add(a_Agent_ID);
//                _NotPregnantFemaleIDs.add(a_Agent_ID);
//            }
//        }
//        ite3 = _Demographics._Population.get(GENESIS_Demographics.TotalPopulationName_String)._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
//        while (ite3.hasNext()) {
//            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            age = ite3.next();
//            population = _Demographics._Population.get(GENESIS_Demographics.TotalPopulationName_String)._MaleAgeBoundPopulationCount_TreeMap.get(age);
//            population_BigInteger = population.toBigInteger();
//            log(Level.FINE, "Initialising " + population + " Males aged " + age);
//            for (BigInteger i = BigInteger.ZERO; i.compareTo(population_BigInteger) == -1; i = i.add(BigInteger.ONE)) {
//                //for (int i = 0; i < population; i++) {
//                minBirth_Time = a_Time.add(
//                        new GENESIS_Time(-age - 1, 0, 0));
//                maxBirth_Time = a_Time.add(
//                        new GENESIS_Time(-age, 0, 0));
//                maxBirth_Time.subtractDay();
////                minBirth_Time = a_Time.add(
////                        new GENESIS_Time(age - 1, 0, 0));
////                maxBirth_Time = a_Time.add(
////                        new GENESIS_Time(age, 0, 0));
//                a_Birth_Time = GENESIS_Time.getRandomTime(
//                        minBirth_Time,
//                        maxBirth_Time,
//                        _GENESIS_Environment._AbstractModel._Random);
//                a_Male = _GENESIS_Environment._PersonFactory.createMale(
//                        a_Birth_Time,
//                        a_Household,
//                        a_VectorPoint2D,
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                Long a_Agent_ID = a_Male.get_Agent_ID(
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                _LivingMaleIDs.add(a_Agent_ID);
//            }
//        }
//        int totalInitialisedPregnancies = initialisePregnancies();
//        log(Level.FINE, "totalInitialisedPregnancies " + totalInitialisedPregnancies);
//        log(Level.FINE, "_NearlyDuePregnantFemaleIDs.size() " + _NearlyDuePregnantFemaleIDs.size());
//    }
    /**
     * @TODO using a distribution of known population, the age group assigned
     * can be done so by randomly sampling from this distribution rather than
     * assuming an even distribution. This is often more of an issue for the
     * last open ended age group.
     * @param inputPopulation The population registered to age groups which is
     * not necessarily in single years of age.
     * @param a_Time
     * @return <code>TreeMap<String,TreeMap<String, GENESIS_Population></code>
     * the population in single years of age.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Population>> getSingleYearsOfAgePopulations(
            TreeMap<String, TreeMap<String, GENESIS_Population>> inputPopulation,
            GENESIS_Time a_Time) {
        try {
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
            TreeMap<String, TreeMap<String, GENESIS_Population>> result;
            result = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
            BigDecimal population;
            BigInteger population_BigInteger;
            long ageMin;
            long ageMax;
            GENESIS_AgeBound ageBound;
            Iterator<String> ite;
            _regionIDs = new TreeMap<String, TreeSet<String>>();
            // Iterate over inputPopulation LADs
            ite = inputPopulation.keySet().iterator();
            while (ite.hasNext()) {
                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                String regionID = ite.next();
                if (!regionID.equalsIgnoreCase("Total")) {
                    TreeSet<String> subregionIDs = new TreeSet<String>();
                    _regionIDs.put(regionID, subregionIDs);
                    TreeMap<String, GENESIS_Population> inputLADPopulation;
                    inputLADPopulation = inputPopulation.get(regionID);
                    TreeMap<String, GENESIS_Population> individualYearLADPopulation;
                    individualYearLADPopulation = new TreeMap<String, GENESIS_Population>();
                    result.put(regionID, individualYearLADPopulation);
                    // Iterate over inputLADPopulation OAs
                    Iterator<String> ite2 = inputLADPopulation.keySet().iterator();
                    while (ite2.hasNext()) {
                        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        String subregionID = ite2.next();
                        if (!subregionID.equalsIgnoreCase(regionID)) {
                            subregionIDs.add(regionID);
                            GENESIS_Population inputLADPopulationNotNecessarilyInSingleYearsOfAge;
                            inputLADPopulationNotNecessarilyInSingleYearsOfAge = inputLADPopulation.get(subregionID);
                            GENESIS_Population individualYearAgeBoundPopulation = new GENESIS_Population(_GENESIS_Environment);
                            individualYearLADPopulation.put(subregionID, individualYearAgeBoundPopulation);
                            Iterator<GENESIS_AgeBound> ite3;
                            // Females
                            ite3 = inputLADPopulationNotNecessarilyInSingleYearsOfAge._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
                            while (ite3.hasNext()) {
                                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                ageBound = ite3.next();
                                population = inputLADPopulationNotNecessarilyInSingleYearsOfAge._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                                if (population.compareTo(BigDecimal.ZERO) == 1) {
                                    ageMin = ageBound.getAgeMin().getYear();
                                    ageMax = ageBound.getAgeMax().getYear();
                                    GENESIS_Time ageMinTime = new GENESIS_Time(ageMin, 0);
                                    GENESIS_Time ageMaxTime = new GENESIS_Time(ageMax, 0);
                                    population_BigInteger = population.toBigInteger();
                                    //msg = "Initial assignment of single year of age to " + population + " Females aged between " + ageMin + " and " + ageMax;
                                    //log(Level.FINE, msg);
                                    //System.out.println(msg);
                                    for (BigInteger i = BigInteger.ZERO; i.compareTo(population_BigInteger) == -1; i = i.add(BigInteger.ONE)) {
                                        GENESIS_Time age_Time = GENESIS_Time.getRandomTime(ageMinTime,
                                                ageMaxTime,
                                                _GENESIS_Environment._AbstractModel._RandomArray[0],
                                                _GENESIS_Environment._AbstractModel._RandomArray[1]);
                                        GENESIS_Time birth_Time = a_Time.subtract(age_Time);
                                        GENESIS_Age age = new GENESIS_Age(
                                                _GENESIS_Environment,
                                                birth_Time,
                                                ageMinTime,
                                                ageMaxTime);
                                        GENESIS_AgeBound individualYearAgeBound = new GENESIS_AgeBound(
                                                age.getAgeInYears());
                                        individualYearAgeBound.setAgeMinBound(ageMinTime);
                                        individualYearAgeBound.setAgeMaxBound(ageMaxTime);
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(individualYearAgeBoundPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                                                individualYearAgeBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    }
                                }
                            }
                            // Males
                            ite3 = inputLADPopulationNotNecessarilyInSingleYearsOfAge._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
                            while (ite3.hasNext()) {
                                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                ageBound = ite3.next();
                                population = inputLADPopulationNotNecessarilyInSingleYearsOfAge._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                                if (population.compareTo(BigDecimal.ZERO) == 1) {
                                    ageMin = ageBound.getAgeMin().getYear();
                                    ageMax = ageBound.getAgeMax().getYear();
                                    GENESIS_Time ageMinTime = new GENESIS_Time(ageMin, 0);
                                    GENESIS_Time ageMaxTime = new GENESIS_Time(ageMax, 0);
                                    population_BigInteger = population.toBigInteger();
                                    //msg = "Initial assignment of single year of age to " + population + " Females aged between " + ageMin + " and " + ageMax;
                                    //log(Level.FINE, msg);
                                    //System.out.println(msg);
                                    for (BigInteger i = BigInteger.ZERO; i.compareTo(population_BigInteger) == -1; i = i.add(BigInteger.ONE)) {
                                        GENESIS_Time age_Time = GENESIS_Time.getRandomTime(ageMinTime,
                                                ageMaxTime,
                                                _GENESIS_Environment._AbstractModel._RandomArray[0],
                                                _GENESIS_Environment._AbstractModel._RandomArray[1]);
                                        GENESIS_Time birth_Time = a_Time.subtract(age_Time);
                                        GENESIS_Age age = new GENESIS_Age(
                                                _GENESIS_Environment,
                                                birth_Time,
                                                ageMinTime,
                                                ageMaxTime);
                                        GENESIS_AgeBound individualYearAgeBound = new GENESIS_AgeBound(
                                                age.getAgeInYears());
                                        individualYearAgeBound.setAgeMinBound(ageMinTime);
                                        individualYearAgeBound.setAgeMaxBound(ageMaxTime);
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(individualYearAgeBoundPopulation._MaleAgeBoundPopulationCount_TreeMap,
                                                individualYearAgeBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Iterate over individualYearPopulation result to updateGenderedAgePopulation 
            // and calculate totals
            ite = result.keySet().iterator();
            while (ite.hasNext()) {
                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                String aLADCode = ite.next();
                TreeMap<String, GENESIS_Population> individualYearLADPopulation;
                individualYearLADPopulation = result.get(aLADCode);
                GENESIS_Population individualYearLADPopulationTotal;
                individualYearLADPopulationTotal = new GENESIS_Population(
                        _GENESIS_Environment);
                // Iterate over individualYearLADPopulation
                Iterator<String> ite2 = individualYearLADPopulation.keySet().iterator();
                while (ite2.hasNext()) {
                    _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    String aOACode = ite2.next();
                    GENESIS_Population individualYearAgeBoundPopulation;
                    individualYearAgeBoundPopulation = individualYearLADPopulation.get(aOACode);
                    individualYearAgeBoundPopulation.updateGenderedAgePopulation();
                    individualYearLADPopulationTotal.addPopulation(individualYearAgeBoundPopulation);
                }
                individualYearLADPopulationTotal.updateGenderedAgePopulation();
                individualYearLADPopulation.put(aLADCode, individualYearLADPopulationTotal);
            }
            return result;
        } catch (OutOfMemoryError e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    /**
     * From the single year age population counts initialise the individuals
     * giving each a date of birth and for females evaluating pregnancy status.
     * The IDs of individuals are assigned to the appropriate collections. This
     * also initialises _regionIDs TreeSets
     *
     * @param singleYearAgeRegionPopulation
     */
    public void initialiseSubregionPopulations(
            TreeMap<String, TreeMap<String, GENESIS_Population>> singleYearAgeRegionPopulation) {
        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
        _GENESIS_Environment._Time.subtractDay();
        Iterator<String> ite = singleYearAgeRegionPopulation.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            TreeSet<String> subregionIDs = new TreeSet<String>();
            _regionIDs.put(regionID, subregionIDs);
            String msg;
            msg = "Initialising population for regionID " + regionID;
            log(Level.FINE, msg);
            System.out.println(msg);
            TreeMap<String, TreeSet<Long>> regionLivingFemaleIDs;
            regionLivingFemaleIDs = new TreeMap<String, TreeSet<Long>>();
            _LivingFemaleIDs.put(regionID, regionLivingFemaleIDs);
            TreeMap<String, TreeSet<Long>> regionLivingMaleIDs;
            regionLivingMaleIDs = new TreeMap<String, TreeSet<Long>>();
            _LivingMaleIDs.put(regionID, regionLivingMaleIDs);
            TreeMap<String, TreeSet<Long>> regionNotPregnantFemaleIDs;
            regionNotPregnantFemaleIDs = _NotPregnantFemaleIDs.get(regionID);
            if (regionNotPregnantFemaleIDs == null) {
                regionNotPregnantFemaleIDs = new TreeMap<String, TreeSet<Long>>();
                _NotPregnantFemaleIDs.put(regionID, regionNotPregnantFemaleIDs);
            }
            TreeMap<String, GENESIS_Population> regionPopulation;
            regionPopulation = singleYearAgeRegionPopulation.get(regionID);
            Iterator<String> ite2 = regionPopulation.keySet().iterator();
            while (ite2.hasNext()) {
                String subregionID = ite2.next();

//                // Debug
//                if (subregionID.equalsIgnoreCase("00DBFT0036")) {
//                    int debug = 1;
//                }

                subregionIDs.add(subregionID);
                if (!regionID.equalsIgnoreCase(subregionID)) {
                    GENESIS_Population subregionPopulation;
                    subregionPopulation = regionPopulation.get(subregionID);
                    TreeSet<Long> subregionLivingFemaleIDs;
                    subregionLivingFemaleIDs = new TreeSet<Long>();
                    regionLivingFemaleIDs.put(subregionID, subregionLivingFemaleIDs);
                    TreeSet<Long> subregionLivingMaleIDs;
                    subregionLivingMaleIDs = new TreeSet<Long>();
                    regionLivingMaleIDs.put(subregionID, subregionLivingMaleIDs);
                    TreeSet<Long> subregionNotPregnantFemaleIDs;
                    subregionNotPregnantFemaleIDs = new TreeSet<Long>();
                    regionNotPregnantFemaleIDs.put(subregionID, subregionNotPregnantFemaleIDs);

//                    msg = "Initialising population for regionID "
//                            + regionID + " subregionID " + subregionID;
//                    log(Level.FINE, msg);
//                    System.out.println(msg);
                    Iterator<GENESIS_AgeBound> ite3;
                    // Initialise females in subregion
                    ite3 = subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
                    while (ite3.hasNext()) {
                        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        GENESIS_AgeBound ageBound = ite3.next();
                        Long population = subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound).longValue();
                        for (int p = 0; p < population; p++) {
                            getNewInitialisedFemale(
                                    ageBound,
                                    null,
                                    subregionID,
                                    subregionLivingFemaleIDs,
                                    subregionNotPregnantFemaleIDs);
                        }
                    }
                    // Initialise males in subregion
                    ite3 = subregionPopulation._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
                    while (ite3.hasNext()) {
                        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        GENESIS_AgeBound ageBound = ite3.next();
                        Long population = subregionPopulation._MaleAgeBoundPopulationCount_TreeMap.get(ageBound).longValue();
                        for (int p = 0; p < population; p++) {
                            getNewInitialisedMale(
                                    ageBound,
                                    null,
                                    subregionID,
                                    subregionLivingMaleIDs);
                        }
                    }
                }
//                msg = "Initialised population for regionID " 
//                            + regionID + " subregionID " + subregionID;
//                log(Level.FINE, msg);
//                System.out.println(msg);
            }
//            msg = "Initialised population for regionID " + regionID;
//            log(Level.FINE, msg);
//            System.out.println(msg);
        }
        System.out.println("IndexOfLastBornFemale " + this._GENESIS_AgentCollectionManager._IndexOfLastBornFemale);
        System.out.println("IndexOfLastBornMale " + this._GENESIS_AgentCollectionManager._IndexOfLastBornMale);
        _GENESIS_Environment._Time.addDay();
        int totalInitialisedPregnancies = initialisePregnancies();
        log(Level.FINE, "totalInitialisedPregnancies " + totalInitialisedPregnancies);
        log(Level.FINE, "_NearlyDuePregnantFemale_ID_HashSet.size() " + _NearlyDuePregnantFemaleIDs.size());
    }

    /**
     * Creates and returns a new GENESIS_Female. The result returned has a time
     * of birth initialised within the ageBound. The result.ID is added to
     * subregionLivingFemaleIDs and subregionNotPregnantFemaleIDs. The
     * result._ResidentialSubregionIDs has two values added, firstly
     * birthSubregionID, secondly thisSubregionID.
     *
     * @param ageBound
     * @param birthSubregionID
     * @param thisSubregionID
     * @param subregionLivingFemaleIDs
     * @param subregionNotPregnantFemaleIDs
     * @return
     */
    public GENESIS_Female getNewInitialisedFemale(
            GENESIS_AgeBound ageBound,
            String birthSubregionID,
            String thisSubregionID,
            TreeSet<Long> subregionLivingFemaleIDs,
            TreeSet<Long> subregionNotPregnantFemaleIDs) {
        GENESIS_Female result = getNewInitialisedFemale(
                ageBound,
                subregionLivingFemaleIDs,
                subregionNotPregnantFemaleIDs);
        result._ResidentialSubregionIDs.add(birthSubregionID);
        result._ResidentialSubregionIDs.add(thisSubregionID);
        return result;
    }

    /**
     * Creates and returns a new GENESIS_Female. The result returned has a time
     * of birth initialised within the ageBound. The result.ID is added to
     * subregionLivingFemaleIDs and subregionNotPregnantFemaleIDs.
     *
     * @param ageBound
     * @param subregionLivingFemaleIDs
     * @param birthSubregionID
     * @param subregionNotPregnantFemaleIDs
     * @return
     */
    public GENESIS_Female getNewInitialisedFemale(
            GENESIS_AgeBound ageBound,
            TreeSet<Long> subregionLivingFemaleIDs,
            TreeSet<Long> subregionNotPregnantFemaleIDs) {
        GENESIS_Female result;
        GENESIS_Age age = getRandomAge(ageBound);
        result = _GENESIS_Environment._PersonFactory.createFemale(age,
                null,
                null,
                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
        Long a_Agent_ID = result.get_Agent_ID(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
        subregionLivingFemaleIDs.add(a_Agent_ID);
        subregionNotPregnantFemaleIDs.add(a_Agent_ID);

//        // debug
//        if (a_Agent_ID == 145256) {
//            int debug = 1;
//        }

        return result;
    }

    /**
     * Creates and returns a new GENESIS_Male. The result returned has a time of
     * birth initialised within the ageBound. The result.ID is added to
     * subregionLivingMaleIDs. The result._ResidentialSubregionIDs has two
     * values added, firstly birthSubregionID, secondly thisSubregionID.
     *
     * @param ageBound
     * @param birthSubregionID
     * @param thisSubregionID
     * @param subregionLivingMaleIDs
     * @return
     */
    public GENESIS_Male getNewInitialisedMale(
            GENESIS_AgeBound ageBound,
            String birthSubregionID,
            String thisSubregionID,
            TreeSet<Long> subregionLivingMaleIDs) {
        GENESIS_Male result = getNewInitialisedMale(
                ageBound,
                subregionLivingMaleIDs);
        result._ResidentialSubregionIDs.add(birthSubregionID);
        result._ResidentialSubregionIDs.add(thisSubregionID);
        return result;
    }

    /**
     * Creates and returns a new GENESIS_Male. The result returned has a time of
     * birth initialised within the ageBound. The result.ID is added to
     * subregionLivingMaleIDs.
     *
     * @param ageBound
     * @param subregionLivingMaleIDs
     * @return
     */
    public GENESIS_Male getNewInitialisedMale(
            GENESIS_AgeBound ageBound,
            TreeSet<Long> subregionLivingMaleIDs) {
        GENESIS_Male result;
        GENESIS_Age age = getRandomAge(ageBound);
        result = _GENESIS_Environment._PersonFactory.createMale(age,
                null,
                null,
                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
        Long a_Agent_ID = result.get_Agent_ID(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
        subregionLivingMaleIDs.add(a_Agent_ID);
        return result;
    }

    /**
     * Used for initialising a time of birth.
     *
     * @param ageBound
     * @return GENESIS_age at random from within bounds of ageBound.
     */
    public GENESIS_Age getRandomAge(GENESIS_AgeBound ageBound) {
        GENESIS_Age result;
        long ageMin = ageBound.getAgeMin().getYear();
        long ageMax;
        if (ageBound.getAgeMax() == null) {
            ageMax = ageMin + 10;
        } else {
            ageMax = ageBound.getAgeMax().getYear();
        }
        GENESIS_Time ageMinTime = new GENESIS_Time(ageMin, 0);
        GENESIS_Time ageMaxTime = new GENESIS_Time(ageMax, 0);
        GENESIS_Time age_Time = GENESIS_Time.getRandomTime(ageMinTime,
                ageMaxTime,
                _GENESIS_Environment._AbstractModel._RandomArray[0],
                _GENESIS_Environment._AbstractModel._RandomArray[1]);
        GENESIS_Time birth_Time = _GENESIS_Environment._Time.subtract(age_Time);
        result = new GENESIS_Age(
                _GENESIS_Environment,
                birth_Time,
                ageBound.getAgeMinBound(),
                ageBound.getAgeMaxBound());

//        if (birth_Time.getDayOfYear() == 0 && birth_Time.getYear() == 2001) {
//            int debug = 1;
//        }

        return result;
    }

//    /**
//     * Initialises persons in a simple society.
//     * There are a number of households each consisting of 100 Males and 100 
//     * Females as yet unrelated.
//     */
//    public void initialisePopulation(
//            long initialFemalePopulation,
//            long initialMalePopulation,
//            GENESIS_Time _Time,
//            int pregnancyInitialisationYears) {
//        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//        _LivingMaleIDs = new HashSet<Long>();
//        _LivingFemaleIDs = new HashSet<Long>();
//        _NotPregnantFemaleIDs = new HashSet<Long>();
//        _PregnantFemaleIDs = new HashSet<Long>();
//        _NearlyDuePregnantFemaleIDs = new HashSet<Long>();
//        GENESIS_Time minBirth_Time = new GENESIS_Time(0, 0, 0);
//        GENESIS_Time maxBirth_Time = new GENESIS_Time(15, 0, 0);
//        GENESIS_Time a_Birth_Time;
//        // Generate Females
//        GENESIS_Female a_Female;
//        long numberOfFemales_div_10 = initialFemalePopulation / 10;
//        //long numberOfFemales_div_100 = initialFemalePopulation / 100;
//        //Calendar _DateOfBirth_Calendar;
//        for (int i = 0; i < initialFemalePopulation; i++) {
//            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            if (i % numberOfFemales_div_10 == 0) {
//                log(Level.FINE, "Initialising female " + i);
//            }
//            a_Birth_Time = GENESIS_Time.getRandomTime(
//                    minBirth_Time,
//                    maxBirth_Time,
//                    _GENESIS_Environment._AbstractModel._Random);
//            a_Female = _GENESIS_Environment._PersonFactory.createFemale(
//                    a_Birth_Time,
//                    null,
//                    null,
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            //a_Female.write();
//            Long a_Agent_ID = a_Female.get_Agent_ID(
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            _LivingFemaleIDs.add(a_Agent_ID);
//            _NotPregnantFemaleIDs.add(a_Agent_ID);
//        }
//        // Generate Males
//        GENESIS_MaleCollection a_MaleCollection = null;
//        GENESIS_Male a_Male;
//        long numberOfMales_div_10 = initialMalePopulation / 10;
//        for (int i = 0; i < initialMalePopulation; i++) {
//            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            if (i % numberOfMales_div_10 == 0) {
//                log(Level.FINE, "Initialising female " + i);
//            }
//            a_Birth_Time = GENESIS_Time.getRandomTime(
//                    minBirth_Time,
//                    maxBirth_Time,
//                    _GENESIS_Environment._AbstractModel._Random);
//            a_Male = _GENESIS_Environment._PersonFactory.createMale(
//                    a_Birth_Time,
//                    null,
//                    null,
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            //a_Male.write();
//            _LivingMaleIDs.add(a_Male.get_Agent_ID(
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean));
//        }
////        initialisePregnancies(
////                pregnancyInitialisationYears);
//        int totalInitialisedPregnancies = initialisePregnancies();
//        log(Level.FINE, "totalInitialisedPregnancies " + totalInitialisedPregnancies);
//    }
    // Write out initial estimated Fertility
    public void writeOutEstimatedFertility(File resultMetadataDirectory_File) {
        File inputFertilityBaseDir = new File(
                resultMetadataDirectory_File,
                "fertility");
        inputFertilityBaseDir.mkdirs();
        long range = _GENESIS_Environment.getDefaultMaximumNumberOfObjectsPerDirectory();
        Iterator<String> ite;
        ite = _Demographics._Fertility.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            File regionInputFertilityBaseDir = new File(
                    inputFertilityBaseDir,
                    "" + regionID);
            regionInputFertilityBaseDir.mkdir();
            TreeMap<String, GENESIS_Fertility> regionFertility;
            regionFertility = _Demographics._Fertility.get(regionID);
            Iterator<String> ite2 = regionFertility.keySet().iterator();
            while (ite2.hasNext()) {
                String subregionID = ite2.next();
                GENESIS_Fertility subregionFertility;
                subregionFertility = regionFertility.get(subregionID);
                File dir;
                if (regionInputFertilityBaseDir.list().length == 0) {
                    dir = Generic_StaticIO.initialiseArchive(
                            regionInputFertilityBaseDir,
                            range);
                } else {
                    dir = Generic_StaticIO.addToArchive(
                            regionInputFertilityBaseDir,
                            range);
                }
                File file = new File(
                        dir,
                        "" + subregionID + "_InitialisedFertility.xml");
                subregionFertility.updateAgeBoundRates();
                subregionFertility.writeToXML(file);
                if (regionID.equalsIgnoreCase(subregionID)) {
                    file = new File(
                            dir,
                            "" + subregionID + "_InitialisedFertility.csv");
                    subregionFertility.writeToCSV(file);
                }
            }
        }
    }

    // Write out initial estimated Mortality
    public void writeOutEstimatedMortality(File resultMetadataDirectory_File) {
        File inputMortalityBaseDir = new File(
                resultMetadataDirectory_File,
                "mortality");
        inputMortalityBaseDir.mkdirs();
        long range = _GENESIS_Environment.getDefaultMaximumNumberOfObjectsPerDirectory();
        Iterator<String> ite;
        ite = _Demographics._Mortality.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            File regionInputMortalityBaseDir = new File(
                    inputMortalityBaseDir,
                    "" + regionID);
            regionInputMortalityBaseDir.mkdir();
            TreeMap<String, GENESIS_Mortality> regionMortality;
            regionMortality = _Demographics._Mortality.get(regionID);
            Iterator<String> ite2 = regionMortality.keySet().iterator();
            while (ite2.hasNext()) {
                String subregionID = ite2.next();
                GENESIS_Mortality subregionMortality;
                subregionMortality = regionMortality.get(subregionID);
                File dir;
                if (regionInputMortalityBaseDir.list().length == 0) {
                    dir = Generic_StaticIO.initialiseArchive(
                            regionInputMortalityBaseDir,
                            range);
                } else {
                    dir = Generic_StaticIO.addToArchive(
                            regionInputMortalityBaseDir,
                            range);
                }
                File file = new File(
                        dir,
                        "" + subregionID + "_InitialisedMortality.xml");
                subregionMortality.updateGenderedAgeBoundRates();
                subregionMortality.writeToXML(file);
                if (regionID.equalsIgnoreCase(subregionID)) {
                    file = new File(
                            dir,
                            "" + subregionID + "_InitialisedMortality.csv");
                    subregionMortality.writeToCSV(file);
                }
            }
        }
    }

    // Write out input populations in metadata for reference
    public void writeOutInputPopulations(
            TreeMap<String, TreeMap<String, GENESIS_Population>> inputPopulation,
            File resultMetadataDirectory_File) {
        File dir = new File(
                resultMetadataDirectory_File,
                "population");
        dir = new File(
                dir,
                "input");
        dir = new File(
                dir,
                "2001UKCensusData");
        dir.mkdirs();
        //long range = _GENESIS_Environment.getDefaultMaximumNumberOfObjectsPerDirectory();
        String format = "PNG";
        String title;
        int dataWidth = 300;
        int dataHeight = 400;
        String xAxisLabel = "Population";
        String yAxisLabel = "Age";
        boolean drawOriginLinesOnPlot = false;
        //int decimalPlacePrecisionForCalculations = _GENESIS_Environment._DecimalPlacePrecisionForCalculations;
        int decimalPlacePrecisionForCalculations = 10;
        int significantDigits = 3;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        int startAgeOfEndYearInterval = 120;
        Iterator<String> ite;
        ite = inputPopulation.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            File dir2 = new File(
                    dir,
                    "" + regionID);
            dir2.mkdir();
            TreeMap<String, GENESIS_Population> regionPopulation;
            regionPopulation = inputPopulation.get(regionID);
            Iterator<String> ite2 = regionPopulation.keySet().iterator();
            while (ite2.hasNext()) {
                String subregionID = ite2.next();
                // Only write out region and totals
                if (subregionID.equals(regionID)) {
                    GENESIS_Population subregionPopulation;
                    subregionPopulation = regionPopulation.get(subregionID);
                    File file = new File(
                            dir2,
                            "" + subregionID + "_InputPopulation.xml");
                    subregionPopulation.writeToXML(file);
                    if (regionID.equalsIgnoreCase(subregionID)) {
                        file = new File(
                                dir2,
                                "" + subregionID + "_InputPopulation.csv");
                        subregionPopulation.writeToCSV(file);
                        // As directory structure changes this needs to be done seperately
                        file = new File(
                                dir2,
                                "" + subregionID + "_InputPopulation.PNG");
                        title = "" + subregionID + " Input Population";
                        subregionPopulation.writeToImage(
                                executorService,
                                file,
                                format,
                                title,
                                dataWidth,
                                dataHeight,
                                xAxisLabel,
                                yAxisLabel,
                                drawOriginLinesOnPlot,
                                decimalPlacePrecisionForCalculations,
                                significantDigits,
                                roundingMode, startAgeOfEndYearInterval);
                    }
                }
            }
        }
    }

    // Write out start populations in metadata for comparison and reference
    public void writeOutStartPopulations(File resultMetadataDirectory_File) {
        File inputPopulationBaseDir = new File(
                resultMetadataDirectory_File,
                "population");
        File inputPopulationInitialisedPopulationDirectory = new File(
                inputPopulationBaseDir,
                "start");
        inputPopulationInitialisedPopulationDirectory.mkdirs();
        long range = _GENESIS_Environment.getDefaultMaximumNumberOfObjectsPerDirectory();
        Iterator<String> ite;
        ite = _Demographics._Population.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            File regionInputPopulationInitialisedPopulationDirectory = new File(
                    inputPopulationInitialisedPopulationDirectory,
                    "" + regionID);
            regionInputPopulationInitialisedPopulationDirectory.mkdir();
            TreeMap<String, GENESIS_Population> regionPopulation;
            regionPopulation = _Demographics._Population.get(regionID);
            Iterator<String> ite2 = regionPopulation.keySet().iterator();
            while (ite2.hasNext()) {
                String subregionID = ite2.next();
                GENESIS_Population subregionPopulation;
                subregionPopulation = regionPopulation.get(subregionID);
                File dir;
                if (regionInputPopulationInitialisedPopulationDirectory.list().length == 0) {
                    dir = Generic_StaticIO.initialiseArchive(
                            regionInputPopulationInitialisedPopulationDirectory,
                            range);
                } else {
                    dir = Generic_StaticIO.addToArchive(
                            regionInputPopulationInitialisedPopulationDirectory,
                            range);
                }
                File file = new File(
                        dir,
                        "" + subregionID + "_InitialisedPopulation.xml");
                subregionPopulation.writeToXML(file);
                if (regionID.equalsIgnoreCase(subregionID)) {
                    file = new File(
                            dir,
                            "" + subregionID + "_InitialisedPopulation.csv");
                    subregionPopulation.writeToCSV(file);
//                    // As directory structure changes this needs to be done seperately
//                    file = new File(
//                            dir,
//                            "" + subregionID + "_InitialisedPopulation.PNG");
//                    title = "" + subregionID + " Initialised Population";
//                    subregionPopulation.writeToImage(
//                            executorService,
//                            file,
//                            format,
//                            title,
//                            dataWidth,
//                            dataHeight,
//                            xAxisLabel,
//                            yAxisLabel,
//                            drawOriginLinesOnPlot,
//                            decimalPlacePrecisionForCalculations,
//                            significantDigits,
//                            roundingMode, startAgeOfEndYearInterval);
                }
            }
        }
        String format = "PNG";
        String title;
        int dataWidth = 300;
        int dataHeight = 400;
        String xAxisLabel = "Population";
        String yAxisLabel = "Age";
        boolean drawOriginLinesOnPlot = false;
        //int decimalPlacePrecisionForCalculations = _GENESIS_Environment._DecimalPlacePrecisionForCalculations;
        int decimalPlacePrecisionForCalculations = 10;
        int significantDigits = 3;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        int startAgeOfEndYearInterval = 99;
        ite = _Demographics._Population.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            File regionInputPopulationInitialisedPopulationDirectory = new File(
                    inputPopulationInitialisedPopulationDirectory,
                    "" + regionID);
            regionInputPopulationInitialisedPopulationDirectory.mkdir();
            TreeMap<String, GENESIS_Population> regionPopulation;
            regionPopulation = _Demographics._Population.get(regionID);
            long ID = 0;
            long maxID = Generic_StaticIO.getArchiveHighestLeaf(
                    regionInputPopulationInitialisedPopulationDirectory,
                    "_");
            Iterator<String> ite2 = regionPopulation.keySet().iterator();
            while (ite2.hasNext()) {
                String subregionID = ite2.next();
                if (regionID.equalsIgnoreCase(subregionID)) {
                    GENESIS_Population subregionPopulation;
                    subregionPopulation = regionPopulation.get(subregionID);
                    File dir = Generic_StaticIO.getObjectDirectory(
                            regionInputPopulationInitialisedPopulationDirectory,
                            ID,
                            maxID,
                            range);
                    // As directory structure changes this is done seperately
                    File file = new File(
                            dir,
                            "" + subregionID + "_InitialisedPopulation.PNG");
                    title = "" + subregionID + " Initialised Population";
                    subregionPopulation.writeToImage(
                            executorService,
                            file,
                            format,
                            title,
                            dataWidth,
                            dataHeight,
                            xAxisLabel,
                            yAxisLabel,
                            drawOriginLinesOnPlot,
                            decimalPlacePrecisionForCalculations,
                            significantDigits,
                            roundingMode, startAgeOfEndYearInterval);
                }
            }
        }
    }

    /**
     * Dynamically simulates a population over time. We go through the entire
     * population multiple times for the number of years input. We evaluate
     * every person on a daily basis. Each day we check a persons age and look
     * up the daily mortality probability for someone of their age and gender.
     * We then perform a stochastic pseudo random test which determines if they
     * survive or die. For females we also check pregnancy status and if they
     * are pregnant and due then they give birth, otherwise, we stochastically
     * determine if they experience a miscarriage. If they are not pregnant we
     * stochastically simulate if they become pregnant. Various aggregate counts
     * are kept and reported on an annual basis.
     *
     * @param years
     * @param _Demographics
     * @return 
     */
    public HashSet<Future> simulate(
            int years) {
        HashSet<Future> result = new HashSet<Future>();
        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
        int significantDigits = 4;
        int decimalPlacePrecisionForCalculations = 10;
        int dataWidthForScatterAndRegressionPlots = 300;
        int dataHeightForScatterAndRegressionPlots = 300;
        int dataWidthForAllAgesAgeGenderPlots = 300;
        int dataHeightForAllAgesAgeGenderPlots = 400;
        int dataWidthForFertileAgesAgeGenderPlots = 300;
        int dataHeightForFertileAgesAgeGenderPlots = 100;
        RoundingMode roundingMode = RoundingMode.HALF_DOWN;
        Long minAgeYearsForPopulationDisplays = 0L;
        Long maxAgeYearsForPopulationDisplays = Long.valueOf(120);
        Long minAgeYearsForFertilityDisplays = Long.valueOf(15);
        Long maxAgeYearsForFertilityDisplays = Long.valueOf(44);
        //GENESIS_Population localPopulation;
        GENESIS_FemaleCollection a_FemaleCollection;
        GENESIS_MaleCollection a_MaleCollection;
        Object[] birth;
        String type;
        int allBirths_int = 0;
        int births_int = 0;
        int twinBirths_int = 0;
        int tripletBirths_int = 0;
        int totalDeaths_int = 0;
        int totalPregnancies_int = 0;
        int totalEarlyPregnancyLossMiscarriages_int = 0;
        int totalClinicalMiscarriages_int = 0;
        boolean isDeath;
        boolean isMiscarriage;
        boolean isPregnancy;
        boolean isBirthOccured;
        boolean isPregnant;
        boolean isMigration;
        //_GENESIS_Environment._Time.addDay();
//        int startYear = (int) _GENESIS_Environment._Time.getYear();
//        int endYear = startYear + years;
        int startDay = _GENESIS_Environment._Time.getDayOfYear();
        //_GENESIS_Environment._Time.subtractDay();
        // For storing the number of days in a year that males and females (of 
        // integer year ages) are alive
        TreeMap<String, TreeMap<String, GENESIS_Population>> aliveDays;
        // For storing the number of males and females (of integer year ages) 
        // that die
        TreeMap<String, TreeMap<String, GENESIS_Population>> deaths;
        // For storing the IDs of the females that die
        TreeMap<String, TreeMap<String, TreeSet<Long>>> deadFemaleIDs;
        // For storing the IDs of the males that die
        TreeMap<String, TreeMap<String, TreeSet<Long>>> deadMaleIDs;
        // For storing the IDs of the feales that migrate        
        TreeMap<String, TreeMap<String, HashSet<Long>>> outMigratingFemaleIDs;
        // For storing the IDs of the males that migrate
        TreeMap<String, TreeMap<String, HashSet<Long>>> outMigratingMaleIDs;
        // For storing a count of the number of labours (N.B. To get the total 
        // number of births, one needs to add the twins and triplets). The total
        // number of births is given by femaleAgeInYearsCountOfBirths_TreeMap
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> labours;
        // A count of the total number of births
        // @TODO With counts of births for pregnancies that happen within a 
        // year, an alternative set of fertility rates can be calculated.
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> births;
        // For miscarriage accounting
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> earlyPregnancyLosses;
        // For miscarriage accounting
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> clinicalMiscarriages;
        // For pregnancy and miscarriage accounting
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> daysInEarlyPregnancy;
        // @TODO With counts of miscarriage for pregnancies that happen within a
        // year, an alternative set of miscarriage rates can be calculated
        // For pregnancy and miscarriage accounting
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> daysInLatePregnancy;
        // For single birth accounting
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> singleBirths;
        // For twin birth accounting
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> twins;
        // For triplet birth accounting
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> triplets;
        // For pregnancy accounting
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> pregnancies;
        // For immigration accounting
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> immigration;
        // For accounting for people migrating out of a region to another region
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> outMigration;
        // For accounting for people migrating into a region from out of it (excluding immigration)
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> inMigration;
        // For accounting for people migrating internally within a region
        TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> internalMigration;

        GENESIS_Migration migration = _Demographics._Migration;
//        // Loop for all years
//        for (int year = startYear; year < endYear; year++) {
        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
        int allBirthsInYear = 0;
        int singleBirthsInYear = 0;
        int twinBirthsInYear = 0;
        int tripletBirthsInYear = 0;
        int totalDeathsInYear = 0;
        int totalPregnanciesInYear = 0;
        int totalEarlyPregnancyLossMiscarriagesInYear = 0;
        int totalClinicalMiscarriagesInYear = 0;
        long daysInEarlyPregnancyInYear = 0;
        long daysInLatePregnancyInYear = 0;
        long allMigrationInYear = 0;
        long allMigrationIntoStudyRegionInYear = 0;
        long allImmigrationInYear = 0;
        long allMigrationOutOfStudyRegionInYear = 0;
        long allMigrationWithinStudyRegionInYear = 0;
        long allMigrationWithinRegionsInYear = 0;
        BigDecimal minusOne_BigDecimal = BigDecimal.ONE.negate();
        // Initialise indexes indexed by regionID and subregionID
        deadFemaleIDs =
                new TreeMap<String, TreeMap<String, TreeSet<Long>>>();
        deadMaleIDs =
                new TreeMap<String, TreeMap<String, TreeSet<Long>>>();
        outMigratingFemaleIDs =
                new TreeMap<String, TreeMap<String, HashSet<Long>>>();
        outMigratingMaleIDs =
                new TreeMap<String, TreeMap<String, HashSet<Long>>>();
        // Initialise annual collections indexed by regionID and subregionID
        aliveDays =
                new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        deaths =
                new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        labours =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        births =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        earlyPregnancyLosses =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        clinicalMiscarriages =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        daysInEarlyPregnancy =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        daysInLatePregnancy =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        singleBirths =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        twins =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        triplets =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        pregnancies =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        immigration =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        outMigration =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        inMigration =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();
        internalMigration =
                new TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>>();

        Iterator<String> ite = this._regionIDs.keySet().iterator();
        while (ite.hasNext()) {
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
            String regionID = ite.next();
            // Indexes
            // Death
            deadFemaleIDs.put(regionID, new TreeMap<String, TreeSet<Long>>());
            TreeMap<String, TreeSet<Long>> regionDeadFemaleIDs;
            regionDeadFemaleIDs = new TreeMap<String, TreeSet<Long>>();
            deadMaleIDs.put(regionID, new TreeMap<String, TreeSet<Long>>());
            TreeMap<String, TreeSet<Long>> regionDeadMaleIDs;
            regionDeadMaleIDs = new TreeMap<String, TreeSet<Long>>();
            // Migration
            TreeMap<String, HashSet<Long>> regionOutMigratingFemaleIDs;
            regionOutMigratingFemaleIDs = new TreeMap<String, HashSet<Long>>();
            outMigratingFemaleIDs.put(regionID, regionOutMigratingFemaleIDs);
            TreeMap<String, HashSet<Long>> regionOutMigratingMaleIDs;
            regionOutMigratingMaleIDs = new TreeMap<String, HashSet<Long>>();
            outMigratingMaleIDs.put(regionID, regionOutMigratingMaleIDs);
            // Populations
            TreeMap<String, GENESIS_Population> regionAliveDays;
            regionAliveDays = new TreeMap<String, GENESIS_Population>();
            aliveDays.put(regionID, regionAliveDays);
            TreeMap<String, GENESIS_Population> regionDeaths;
            regionDeaths = new TreeMap<String, GENESIS_Population>();
            deaths.put(regionID, regionDeaths);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionLabours;
            regionLabours = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            labours.put(regionID, regionLabours);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionBirths;
            regionBirths = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            births.put(regionID, regionBirths);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionEarlyPregnancyLosses;
            regionEarlyPregnancyLosses = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            earlyPregnancyLosses.put(regionID, regionEarlyPregnancyLosses);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionClinicalMiscarriages;
            regionClinicalMiscarriages = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            clinicalMiscarriages.put(regionID, regionClinicalMiscarriages);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionDaysInEarlyPregnancy;
            regionDaysInEarlyPregnancy = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            daysInEarlyPregnancy.put(regionID, regionDaysInEarlyPregnancy);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionDaysInLatePregnancy;
            regionDaysInLatePregnancy = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            daysInLatePregnancy.put(regionID, regionDaysInLatePregnancy);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionSingleBirths;
            regionSingleBirths = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            singleBirths.put(regionID, regionSingleBirths);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionTwins;
            regionTwins = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            twins.put(regionID, regionTwins);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionTriplets;
            regionTriplets = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            triplets.put(regionID, regionTriplets);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionPregnancies;
            regionPregnancies = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            pregnancies.put(regionID, regionPregnancies);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionImmigration;
            regionImmigration = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            immigration.put(regionID, regionPregnancies);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionOutMigration;
            regionOutMigration = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            outMigration.put(regionID, regionOutMigration);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionInMigration;
            regionInMigration = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            inMigration.put(regionID, regionInMigration);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionInternalMigration;
            regionInternalMigration = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
            internalMigration.put(regionID, regionInternalMigration);
            TreeSet<String> subregionIDs = _regionIDs.get(regionID);
            Iterator<String> ite2;
            ite2 = subregionIDs.iterator();
            while (ite2.hasNext()) {
                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                String subregionID = ite2.next();
                regionDeadFemaleIDs.put(
                        subregionID, new TreeSet<Long>());
                regionDeadMaleIDs.put(
                        subregionID, new TreeSet<Long>());
                regionOutMigratingFemaleIDs.put(
                        subregionID, new HashSet<Long>());
                regionOutMigratingMaleIDs.put(
                        subregionID, new HashSet<Long>());
                regionAliveDays.put(subregionID,
                        new GENESIS_Population(_GENESIS_Environment));
                regionDeaths.put(subregionID,
                        new GENESIS_Population(_GENESIS_Environment));
                regionLabours.put(
                        subregionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
                regionBirths.put(
                        subregionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
                regionEarlyPregnancyLosses.put(
                        subregionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
                regionClinicalMiscarriages.put(
                        subregionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
                regionDaysInEarlyPregnancy.put(
                        subregionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
                regionDaysInLatePregnancy.put(
                        subregionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
                regionSingleBirths.put(
                        subregionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
                regionTwins.put(
                        subregionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
                regionTriplets.put(
                        subregionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
                regionPregnancies.put(
                        subregionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
            }
        }
        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
        GENESIS_Demographics startYear_Demographics = new GENESIS_Demographics(
                new GENESIS_Environment(_GENESIS_Environment),
                _Demographics);
        // Loop for all days in a year
        // (currently assumes a fixed number of days in a year)
        //for (int day = startDay; day < startDay + 2; day++) {
        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
        for (int day = startDay; day < startDay + GENESIS_Time.NormalDaysInYear_int; day++) {
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
            String message;
//                message = "Year " + year + " day " + day;
//                log(Level.FINE, message);
//                System.out.println(message);
            message = "Year " + _GENESIS_Environment._Time.getYear() + " day " + _GENESIS_Environment._Time.getDayOfYear();
            log(Level.FINE, message);
            System.out.println(message);
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
            // Iterate over each region
            ite = this._regionIDs.keySet().iterator();
            while (ite.hasNext()) {
                String regionID = ite.next();
                System.out.println("regionID " + regionID);

                // Swapping out data for other regions and loading in data for 
                // this region might be sensible given the way swapping is done?

                int dailySubregionCounter = 0;
                GENESIS_Fertility fertility = get_Fertility(regionID, regionID);
                GENESIS_Mortality mortality = get_Mortality(regionID, regionID);
                // region indexes
                TreeMap<String, HashSet<Long>> regionOutMigratingFemaleIDs;
                regionOutMigratingFemaleIDs = outMigratingFemaleIDs.get(regionID);
                TreeMap<String, HashSet<Long>> regionOutMigratingMaleIDs;
                regionOutMigratingMaleIDs = outMigratingMaleIDs.get(regionID);
                TreeMap<String, TreeSet<Long>> regionDeadFemaleIDs;
                regionDeadFemaleIDs = deadFemaleIDs.get(regionID);
                TreeMap<String, TreeSet<Long>> regionDeadMaleIDs;
                regionDeadMaleIDs = deadMaleIDs.get(regionID);
                TreeMap<String, TreeSet<Long>> regionLivingFemaleIDs;
                regionLivingFemaleIDs = _LivingFemaleIDs.get(regionID);
                TreeMap<String, TreeSet<Long>> regionLivingMaleIDs;
                regionLivingMaleIDs = _LivingMaleIDs.get(regionID);
                TreeMap<String, TreeSet<Long>> regionPregnantFemaleIDs;
                regionPregnantFemaleIDs = _PregnantFemaleIDs.get(regionID);
                TreeMap<String, TreeSet<Long>> regionNearlyDuePregnantFemaleIDs;
                regionNearlyDuePregnantFemaleIDs = _NearlyDuePregnantFemaleIDs.get(regionID);
                TreeMap<String, TreeSet<Long>> regionNotPregnantFemaleIDs;
                regionNotPregnantFemaleIDs = _NotPregnantFemaleIDs.get(regionID);
                // region statistics
                TreeMap<String, GENESIS_Population> regionPopulation;
                regionPopulation = _Demographics._Population.get(regionID);
                GENESIS_Population regionTotalPopulation = regionPopulation.get(regionID);
                TreeMap<String, GENESIS_Population> regionAliveDays;
                regionAliveDays = aliveDays.get(regionID);
                //GENESIS_Population regionTotalAliveDays = regionAliveDays.get(regionID);
                TreeMap<String, GENESIS_Population> regionDeaths;
                regionDeaths = deaths.get(regionID);
                //GENESIS_Population regionTotalDeaths = regionDeaths.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionLabours;
                regionLabours = labours.get(regionID);
                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalLabours;
                regionTotalLabours = regionLabours.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionBirths;
                regionBirths = births.get(regionID);
                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalBirths;
                regionTotalBirths = regionBirths.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionEarlyPregnancyLosses;
                regionEarlyPregnancyLosses = earlyPregnancyLosses.get(regionID);
                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalEarlyPregnancyLosses;
                regionTotalEarlyPregnancyLosses = regionEarlyPregnancyLosses.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionClinicalMiscarriages;
                regionClinicalMiscarriages = clinicalMiscarriages.get(regionID);
                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalClinicalMiscarriages;
                regionTotalClinicalMiscarriages = regionClinicalMiscarriages.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionDaysInEarlyPregnancy;
                regionDaysInEarlyPregnancy = daysInEarlyPregnancy.get(regionID);
                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalDaysInEarlyPregnancy;
                regionTotalDaysInEarlyPregnancy = regionDaysInEarlyPregnancy.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionDaysInLatePregnancy;
                regionDaysInLatePregnancy = daysInLatePregnancy.get(regionID);
                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalDaysInLatePregnancy;
                regionTotalDaysInLatePregnancy = regionDaysInLatePregnancy.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionSingleBirths;
                regionSingleBirths = singleBirths.get(regionID);
                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalSingleBirths;
                regionTotalSingleBirths = regionSingleBirths.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionTwins;
                regionTwins = twins.get(regionID);
                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalTwins;
                regionTotalTwins = regionTwins.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionTriplets;
                regionTriplets = triplets.get(regionID);
                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalTriplets;
                regionTotalTriplets = regionTriplets.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionPregnancies;
                regionPregnancies = pregnancies.get(regionID);
                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalPregnancies;
                regionTotalPregnancies = regionPregnancies.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionImmigration;
                regionImmigration = immigration.get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalImmigration;
//                regionTotalImmigration = regionImmigration.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionOutMigration;
                regionOutMigration = outMigration.get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalOutMigration;
//                regionTotalOutMigration = regionOutMigration.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionInMigration;
                regionInMigration = pregnancies.get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalInMigration;
//                regionTotalInMigration = regionInMigration.get(regionID);
                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionInternalMigration;
                regionInternalMigration = internalMigration.get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalInternalMigration;
//                regionTotalInternalMigration = regionInternalMigration.get(regionID);
// Iterate over each subregion
                TreeSet<String> subregionIDs = _regionIDs.get(regionID);
                Iterator<String> ite2;
                ite2 = subregionIDs.iterator();
                while (ite2.hasNext()) {
                    _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    String subregionID = ite2.next();
                    HashSet<Long> dead_Female_ID_HashSet = null;
                    HashSet<Long> dead_Male_ID_HashSet = null;
                    HashSet<Long> subregionOutMigratingFemaleIDs;// = regionOutMigratingFemaleIDs.get(subregionID);
                    HashSet<Long> subregionOutMigratingMaleIDs;// = regionOutMigratingMaleIDs.get(subregionID);
                    if (!regionID.equalsIgnoreCase(subregionID)) {
                        //System.out.println("subregionID " + subregionID);
//                        if (dailySubregionCounter % 500 == 0) {
//                            System.out.println("Subregion " + dailySubregionCounter
//                                    + " out of " + subregionIDs.size()
//                                    + " subregionID " + subregionID);
//                        }
                        dailySubregionCounter++;
                        // Indexes
                        TreeSet<Long> subregionDeadFemaleIDs = regionDeadFemaleIDs.get(subregionID);
                        TreeSet<Long> subregionDeadMaleIDs = regionDeadMaleIDs.get(subregionID);
                        TreeSet<Long> subregionLivingFemaleIDs = regionLivingFemaleIDs.get(subregionID);
                        TreeSet<Long> subregionLivingMaleIDs = regionLivingMaleIDs.get(subregionID);
                        // get subregionPregnantFemaleIDs
                        TreeSet<Long> subregionPregnantFemaleIDs = regionPregnantFemaleIDs.get(subregionID);
                        subregionPregnantFemaleIDs = getSubregionIDs(
                                regionPregnantFemaleIDs,
                                subregionPregnantFemaleIDs,
                                subregionID);
                        // get subregionNearlyDuePregnantFemaleIDs
                        TreeSet<Long> subregionNearlyDuePregnantFemaleIDs = regionNearlyDuePregnantFemaleIDs.get(subregionID);
                        subregionNearlyDuePregnantFemaleIDs = getSubregionIDs(
                                regionNearlyDuePregnantFemaleIDs,
                                subregionNearlyDuePregnantFemaleIDs,
                                subregionID);
                        // get subregionNotPregnantFemaleIDs
                        TreeSet<Long> subregionNotPregnantFemaleIDs = regionNotPregnantFemaleIDs.get(subregionID);
                        subregionNotPregnantFemaleIDs = getSubregionIDs(
                                regionNotPregnantFemaleIDs,
                                subregionNotPregnantFemaleIDs,
                                subregionID);
                        // Populations
                        GENESIS_Population subregionPopulation;
                        subregionPopulation = regionPopulation.get(subregionID);
                        GENESIS_Population subregionAliveDays;
                        subregionAliveDays = regionAliveDays.get(subregionID);
                        GENESIS_Population subregionDeaths;
                        subregionDeaths = regionDeaths.get(subregionID);
                        // Maps
                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionLabours;
                        subregionLabours = regionLabours.get(subregionID);
                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionBirths;
                        subregionBirths = regionBirths.get(subregionID);
                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionEarlyPregnancyLosses;
                        subregionEarlyPregnancyLosses = regionEarlyPregnancyLosses.get(subregionID);
                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionClinicalMiscarriages;
                        subregionClinicalMiscarriages = regionClinicalMiscarriages.get(subregionID);
                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionDaysInEarlyPregnancy;
                        subregionDaysInEarlyPregnancy = regionDaysInEarlyPregnancy.get(subregionID);
                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionDaysInLatePregnancy;
                        subregionDaysInLatePregnancy = regionDaysInLatePregnancy.get(subregionID);
                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionSingleBirths;
                        subregionSingleBirths = regionSingleBirths.get(subregionID);
                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionTwins;
                        subregionTwins = regionTwins.get(subregionID);
                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionTriplets;
                        subregionTriplets = regionTriplets.get(subregionID);
                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionPregnancies;
                        subregionPregnancies = regionPregnancies.get(subregionID);
                        HashSet<Long> babyGirl_IDs = null;
                        HashSet<Long> babyBoy_IDs = null;
                        Iterator<Long> ite3;
                        // Simulate Males first as Males do not give birth.
                        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        type = GENESIS_Person.getTypeLivingMale_String();
                        ite3 = subregionLivingMaleIDs.iterator();
                        while (ite3.hasNext()) {
//                                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            Long a_Agent_ID = ite3.next();
//                            Long a_Collection_ID =
//                                    _GENESIS_AgentCollectionManager.getAgentCollection_ID(
//                                    a_Agent_ID);
//                            a_MaleCollection =
//                                    _GENESIS_AgentCollectionManager.getMaleCollection(
//                                    a_Collection_ID,
//                                    type);
//                            GENESIS_Male a_Male = a_MaleCollection.getMale(
//                                    a_Agent_ID);
                            Long a_Collection_ID =
                                    _GENESIS_AgentCollectionManager.getMaleCollection_ID(a_Agent_ID,
                                    type,
                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            a_MaleCollection =
                                    _GENESIS_AgentCollectionManager.getMaleCollection(a_Collection_ID,
                                    type,
                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            GENESIS_Male a_Male = a_MaleCollection.getMale(a_Agent_ID,
                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                        //DEBUG                        
//                        if (a_Male == null) {
//                            // Agent_ID 4131, 10204, 1100
//                            //areaCode 00DAFA0007 Male is null, Agent_ID 1100, Collection_ID 1
//                            System.err.println("areaCode " + areaCode + " Male is null, Agent_ID " + a_Agent_ID + ", Collection_ID " + a_Collection_ID);
//                            if (dead_Male_ID_HashSet.contains(a_Agent_ID)) {
//                                System.err.println("Male is in dead_Male_ID_HashSet");
//                            }
//                        } else {
//                            if (a_Male.getAge() == null) {
//                                System.err.println("a_Male.getAge() is null, Agent_ID " + a_Agent_ID + ", Collection_ID " + a_Collection_ID);
//                            } else {
//                            }
//                        }

                            long age = a_Male.getAge().getAgeInYears_long(_GENESIS_Environment._Time);
                            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
                            // Age and update _Demographics._Population._MaleAgeBoundPopulationCount_TreeMap
                            if (a_Male.getIsBirthday()) {

//                                if (age == 0) {
//                                    int debug = 1;
//                                }

                                // Account birthday
                                // Account subregionPopulation._MaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPopulation._MaleAgeBoundPopulationCount_TreeMap,
                                        new GENESIS_AgeBound(age - 1),
                                        minusOne_BigDecimal,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPopulation._MaleAgeBoundPopulationCount_TreeMap,
                                        ageBound,
                                        BigDecimal.ONE,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                // Account regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap                       
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap,
                                        new GENESIS_AgeBound(age - 1),
                                        minusOne_BigDecimal,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap,
                                        ageBound,
                                        BigDecimal.ONE,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            }
                            // Simulate death and account for change in living populations
                            isDeath = simulateDeath(
                                    mortality,
                                    a_Male,
                                    a_Agent_ID,
                                    a_MaleCollection,
                                    a_Collection_ID,
                                    ageBound,
                                    subregionPopulation,
                                    regionTotalPopulation,
                                    minusOne_BigDecimal);
                            if (isDeath) {
                                if (dead_Male_ID_HashSet == null) {
                                    dead_Male_ID_HashSet = new HashSet<Long>();
                                }
//                            // Debug
//                            if (a_Agent_ID == 4131 || a_Agent_ID == 10204) {
//                                System.out.println("DEBUG areaCode " + areaCode + " a_Agent_ID " + a_Agent_ID + " died");
//                            }
                                subregionDeadMaleIDs = getSubregionIDs(
                                        regionDeadMaleIDs,
                                        subregionDeadMaleIDs,
                                        subregionID);
                                subregionDeadMaleIDs.add(a_Agent_ID);
                                dead_Male_ID_HashSet.add(a_Agent_ID);
                                // Account for death
                                totalDeathsInYear++;
                                // Account subregionDeaths._MaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionDeaths._MaleAgeBoundPopulationCount_TreeMap,
                                        ageBound,
                                        BigDecimal.ONE,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
// Now done at end of annual loop
//                                // Account regionTotalDeaths._MaleAgeBoundPopulationCount_TreeMap
//                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                        regionTotalDeaths._MaleAgeBoundPopulationCount_TreeMap,
//                                        ageBound,
//                                        BigDecimal.ONE);
                            } else {
                                // Account a day lived
                                // Account localPopulationAliveDays._MaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionAliveDays._MaleAgeBoundPopulationCount_TreeMap,
                                        ageBound,
                                        BigDecimal.ONE,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                // regionAliveDays.get(regionID) is modified at the 
                                // end of the loop for computational efficiency
                                // Simulate migration
                                // Debug
//                                if (a_Agent_ID == 498800) {
//                                    System.out.println("a_Agent_ID " + a_Agent_ID);
//                                    System.out.println(a_Male);
//                                    int debug = 1;
//
////                                System.out.println("DEBUG areaCode " + areaCode + " a_Agent_ID " + a_Agent_ID + " died");
//                                    //}
//                                }
// The following determined destination region and subregion which is now done 
// at the end of the day so the data for this only need be loaded then.
//                                isMigration = simulateOutMigration(
//                                        migration,
//                                        a_Male,
//                                        a_Agent_ID,
//                                        a_MaleCollection,
//                                        a_Collection_ID,
//                                        ageBound);
                                isMigration = simulateOutMigrationWithoutDeterminingDestination(
                                        migration,
                                        a_Male,
                                        a_Agent_ID,
                                        a_MaleCollection,
                                        a_Collection_ID,
                                        ageBound);
                                if (isMigration) {
                                    // Store IDs to remove from lists
                                    subregionOutMigratingMaleIDs = regionOutMigratingMaleIDs.get(subregionID);
                                    if (subregionOutMigratingMaleIDs == null) {
                                        subregionOutMigratingMaleIDs = new HashSet<Long>();
                                        regionOutMigratingMaleIDs.put(subregionID, subregionOutMigratingMaleIDs);
                                    }
                                    subregionOutMigratingMaleIDs.add(a_Agent_ID);
                                    // Account Populations
                                    // Account subregionPopulation._MaleAgeBoundPopulationCount_TreeMap
                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPopulation._MaleAgeBoundPopulationCount_TreeMap,
                                            ageBound,
                                            minusOne_BigDecimal,
                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    // Account regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap
                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap,
                                            ageBound,
                                            minusOne_BigDecimal,
                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                                    System.out.println("Male Out Migration");
//                                } else {
//                                    isMigration = simulateInternalMigrationInStudyRegion(
//                                            migration,
//                                            a_Male,
//                                            a_Agent_ID,
//                                            a_MaleCollection,
//                                            a_Collection_ID,
//                                            ageBound,
//                                            regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap,
//                                            decimalPlacePrecisionForCalculations,
//                                            roundingMode);
//                                    if (isMigration) {
//                                        allInternalMigrationsInYear ++;
//                                        if (internalStudyRegionMigratingMaleIDs == null) {
//                                            internalStudyRegionMigratingMaleIDs = new HashSet<Long>();
//                                        }
//                                        internalStudyRegionMigratingMaleIDs.add(a_Agent_ID);
//                                        // Account origin Populations
//                                        // Account localPopulation._MaleAgeBoundPopulationCount_TreeMap
//                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                                subregionPopulation._MaleAgeBoundPopulationCount_TreeMap,
//                                                ageBound,
//                                                minusOne_BigDecimal);
//                                        // Account _Demographics._Total_Population._MaleAgeBoundPopulationCount_TreeMap
//                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                                regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap,
//                                                ageBound,
//                                                minusOne_BigDecimal);
//                                        // Migration completed at the end of the daily loop
////                                        System.out.println("Male Internal Migration");
//                                    }
                                }
                            }
                        }
                        // Simulate Females
                        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        type = GENESIS_Person.getTypeLivingFemale_String();
                        ite3 = subregionLivingFemaleIDs.iterator();
                        while (ite3.hasNext()) {
//                                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            Long a_Agent_ID = ite3.next();
//                            Long a_Collection_ID =
//                                    _GENESIS_AgentCollectionManager.getAgentCollection_ID(
//                                    a_Agent_ID);
//                            a_FemaleCollection =
//                                    _GENESIS_AgentCollectionManager.getFemaleCollection(
//                                    a_Collection_ID,
//                                    type);
//                            GENESIS_Female a_Female = a_FemaleCollection.getFemale(
//                                    a_Agent_ID);
                            Long a_Collection_ID =
                                    _GENESIS_AgentCollectionManager.getFemaleCollection_ID(a_Agent_ID,
                                    type,
                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            a_FemaleCollection =
                                    _GENESIS_AgentCollectionManager.getFemaleCollection(a_Collection_ID,
                                    type,
                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            GENESIS_Female a_Female = a_FemaleCollection.getFemale(a_Agent_ID,
                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);

//                            // debug
//                            if (a_Female._GENESIS_Environment == null) {
//                                int debug = 1;
//                            }

//                            // debug
//                            //if (a_Agent_ID == 58061 && day == 20) {
//                            if (a_Agent_ID == 145256 && day >= 7) {
//                                int debug = 1;
//                            }

                            long age = a_Female.getAge().getAgeInYears_long(_GENESIS_Environment._Time);
                            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
                            // Age and update _Demographics._Population._FemaleAgeBoundPopulationCount_TreeMap
                            if (a_Female.getIsBirthday()) {

//                                if (age == 0) {
//                                    int debug = 1;
//                                }

                                // Account birthday
                                // Account subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                                        new GENESIS_AgeBound(age - 1),
                                        minusOne_BigDecimal,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                                        ageBound,
                                        BigDecimal.ONE,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                // Account regionTotalPopulation._FemaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                                        new GENESIS_AgeBound(age - 1),
                                        minusOne_BigDecimal,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                                        ageBound,
                                        BigDecimal.ONE,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            }
                            if (subregionPregnantFemaleIDs.contains(a_Agent_ID)) {
                                isPregnant = true;

//                                // debug
//                                if (a_Female._Time_DueToGiveBirth == null) {
//                                    int debug = 1;
//                                }

                                if (day == 0 || day % (NearlyDueNumberOfDaysTillDueDate - 1) == 0) {
                                    updateSubregionNearlyDuePregnantFemaleIDs(
                                            a_Female,
                                            a_Agent_ID,
                                            subregionNearlyDuePregnantFemaleIDs);
                                }
                            } else {
                                isPregnant = false;
                            }
                            // Simulate death and account for change in living populations
                            isDeath = simulateDeath(
                                    mortality,
                                    a_Female,
                                    a_Agent_ID,
                                    a_FemaleCollection,
                                    a_Collection_ID,
                                    ageBound,
                                    subregionPopulation,
                                    regionTotalPopulation,
                                    subregionPregnantFemaleIDs,
                                    subregionNearlyDuePregnantFemaleIDs,
                                    subregionNotPregnantFemaleIDs,
                                    minusOne_BigDecimal);
                            if (isDeath) {
                                if (dead_Female_ID_HashSet == null) {
                                    dead_Female_ID_HashSet = new HashSet<Long>();
                                }
                                dead_Female_ID_HashSet.add(a_Agent_ID);
                                subregionDeadFemaleIDs = getSubregionIDs(
                                        regionDeadFemaleIDs,
                                        subregionDeadFemaleIDs,
                                        subregionID);
                                subregionDeadFemaleIDs.add(a_Agent_ID);
                                // Account for death
                                totalDeathsInYear++;
                                // Account subregionDeaths._FemaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionDeaths._FemaleAgeBoundPopulationCount_TreeMap,
                                        ageBound,
                                        BigDecimal.ONE,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
// Now done at end of annual loop
//                                // Account regionTotalDeaths._FemaleAgeBoundPopulationCount_TreeMap
//                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                        regionTotalDeaths._FemaleAgeBoundPopulationCount_TreeMap,
//                                        ageBound,
//                                        BigDecimal.ONE);
                                // If a female dies and they are pregnant count this also as a miscarriage
                                if (isPregnant) {
                                    int daysTillDue = a_Female.getDaysToDueDate();
                                    if (daysTillDue < _Demographics._Miscarriage.get(regionID).get(regionID).getNumberOfDaysInLatePregnancy_double()) {
                                        // Account Clinical miscarriage
                                        totalClinicalMiscarriagesInYear++;
                                        // Account subregionClinicalMiscarriages
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionClinicalMiscarriages,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                        // Account regionTotalClinicalMiscarriages
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalClinicalMiscarriages,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                        // Account a day in pregnancy
                                        daysInLatePregnancyInYear++;
                                        // Account localFemaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionDaysInLatePregnancy,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                        // Account annualTotalFemaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalDaysInLatePregnancy,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    } else {
                                        // Account Early Pregnancy Loss miscarriage
                                        totalEarlyPregnancyLossMiscarriagesInYear++;
                                        // Account localFemaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionEarlyPregnancyLosses,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                        // Account annualTotalFemaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalEarlyPregnancyLosses,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                        // Account a day in pregnancy
                                        daysInEarlyPregnancyInYear++;
                                        // Account localFemaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionDaysInEarlyPregnancy,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                        // Account annualTotalFemaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalDaysInEarlyPregnancy,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    }
                                }
                            } else {
                                // Account a day lived
                                // Account localPopulationAliveDaysThisYear._FemaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionAliveDays._FemaleAgeBoundPopulationCount_TreeMap,
                                        ageBound,
                                        BigDecimal.ONE,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                // annualTotalPopulationAliveDays is modified at the 
                                // end of the loop for computational efficiency
                                if (isPregnant) {

//                                    //debug
//                                    if (a_Female == null) {
//                                        int debug = 1;
//                                    }
//                                    if (a_Female.getDaysToDueDate() == null) {
//                                        int debug = 1;
//                                        System.out.println(a_Female);
//                                    }

                                    int daysTillDue = a_Female.getDaysToDueDate();
//                                    // The following should be unneccessary as every NearlyDueNumberOfDaysTillDueDate subregionNearlyDuePregnantFemaleIDs should be updated.
//                                    if (daysTillDue <= NearlyDueNumberOfDaysTillDueDate) {
//                                        subregionNearlyDuePregnantFemaleIDs = regionNearlyDuePregnantFemaleIDs.get(subregionID);
//                                        if (subregionNearlyDuePregnantFemaleIDs != null) {
//                                            subregionNearlyDuePregnantFemaleIDs = new TreeSet<Long>();
//                                            regionNearlyDuePregnantFemaleIDs.put(subregionID, subregionNearlyDuePregnantFemaleIDs);
//                                        }
//                                        subregionNearlyDuePregnantFemaleIDs.add(a_Agent_ID);
//                                    }
                                    double daysInLatePregnancy_double = _Demographics._Miscarriage.get(regionID).get(regionID).getNumberOfDaysInLatePregnancy_double();
                                    if (daysTillDue < daysInLatePregnancy_double) {
                                        // Account a day in late pregnancy
                                        daysInLatePregnancyInYear++;
                                        // Account localFemaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionDaysInLatePregnancy,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                        // Account annualTotalFemaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalDaysInLatePregnancy,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    } else {
                                        // Account a day in early pregnancy
                                        daysInEarlyPregnancyInYear++;
                                        // Account localFemaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionDaysInEarlyPregnancy,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                        // Account annualTotalFemaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalDaysInEarlyPregnancy,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    }
                                    isBirthOccured = false;
                                    if (subregionNearlyDuePregnantFemaleIDs != null) {
                                        //if (subregionNearlyDuePregnantFemaleIDs.contains(a_Agent_ID)) {
                                        if (subregionNearlyDuePregnantFemaleIDs.contains(a_Agent_ID)
                                                || daysTillDue == 0) {
                                            // Birth?
                                            birth = simulateBirth(
                                                    a_Female,
                                                    a_Agent_ID,
                                                    subregionPopulation,
                                                    subregionPregnantFemaleIDs,
                                                    subregionNearlyDuePregnantFemaleIDs,
                                                    subregionNotPregnantFemaleIDs);
                                            if (birth != null) {
                                                isBirthOccured = true;
                                                // Account labour
                                                // Account localFemaleAgeInYearsCountOfLabours_TreeMap
                                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionLabours,
                                                        ageBound,
                                                        BigDecimal.ONE,
                                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                // Account annualTotalFemaleAgeInYearsCountOfLabours_TreeMap
                                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalLabours,
                                                        ageBound,
                                                        BigDecimal.ONE,
                                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                if (birth[0] != null) {
                                                    if (babyGirl_IDs == null) {
                                                        babyGirl_IDs = new HashSet<Long>();
                                                    }
                                                    babyGirl_IDs.addAll((HashSet<Long>) birth[0]);
                                                }
                                                if (birth[1] != null) {
                                                    if (babyBoy_IDs == null) {
                                                        babyBoy_IDs = new HashSet<Long>();
                                                    }
                                                    babyBoy_IDs.addAll((HashSet<Long>) birth[1]);
                                                }
                                                if ((Integer) birth[2] == 1) {
                                                    // Account single birth
                                                    singleBirthsInYear++;
                                                    // Account subregionBirths
                                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionBirths,
                                                            ageBound,
                                                            BigDecimal.ONE,
                                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                    // Account regionTotalBirths
                                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalBirths,
                                                            ageBound,
                                                            BigDecimal.ONE,
                                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                    // Account subregionSingleBirths
                                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionSingleBirths,
                                                            ageBound,
                                                            BigDecimal.ONE,
                                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                    // Account regionTotalSingleBirths
                                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalSingleBirths,
                                                            ageBound,
                                                            BigDecimal.ONE,
                                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                } else {
                                                    if ((Integer) birth[2] == 2) {
                                                        // Account twin birth
                                                        twinBirthsInYear++;
                                                        // Account subregionBirths
                                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionBirths,
                                                                ageBound,
                                                                BigDecimal.valueOf(2),
                                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                        // Account regionTotalBirths
                                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalBirths,
                                                                ageBound,
                                                                BigDecimal.valueOf(2),
                                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                        // Account twin
                                                        // Account subregionTwins
                                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionTwins,
                                                                ageBound,
                                                                BigDecimal.ONE,
                                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                        // Account regionTotalTwins
                                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalTwins,
                                                                ageBound,
                                                                BigDecimal.ONE,
                                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                    } else {
                                                        // Account triplet birth
                                                        tripletBirthsInYear++;
                                                        // Account subregionBirths
                                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionBirths,
                                                                ageBound,
                                                                BigDecimal.valueOf(3),
                                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                        // Account regionTotalBirths
                                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalBirths,
                                                                ageBound,
                                                                BigDecimal.valueOf(3),
                                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                        // Account triplet
                                                        // Account subregionTriplets
                                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionTriplets,
                                                                ageBound,
                                                                BigDecimal.ONE,
                                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                        // Account regionTotalTriplets
                                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalTriplets,
                                                                ageBound,
                                                                BigDecimal.ONE,
                                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (!isBirthOccured) {
                                        // Miscarriage?
                                        subregionNearlyDuePregnantFemaleIDs = getSubregionIDs(
                                                regionNearlyDuePregnantFemaleIDs,
                                                subregionNearlyDuePregnantFemaleIDs,
                                                subregionID);
                                        subregionNotPregnantFemaleIDs = getSubregionIDs(
                                                regionNotPregnantFemaleIDs,
                                                subregionNotPregnantFemaleIDs,
                                                subregionID);
                                        isMiscarriage = simulateMiscarriage(
                                                regionID,
                                                a_Female,
                                                a_Agent_ID,
                                                ageBound,
                                                subregionPregnantFemaleIDs,
                                                subregionNearlyDuePregnantFemaleIDs,
                                                subregionNotPregnantFemaleIDs);
                                        if (isMiscarriage) {
                                            // Account miscarriage
                                            if (daysTillDue < daysInLatePregnancy_double) {
                                                // Account Clinical Miscarriage
                                                totalClinicalMiscarriagesInYear++;
                                                // Account subregionClinicalMiscarriages
                                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionClinicalMiscarriages,
                                                        ageBound,
                                                        BigDecimal.ONE,
                                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                // Account regionTotalClinicalMiscarriages
                                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalClinicalMiscarriages,
                                                        ageBound,
                                                        BigDecimal.ONE,
                                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                            } else {
                                                // Account Early Pregnancy Loss Miscarriage
                                                totalEarlyPregnancyLossMiscarriagesInYear++;
                                                // Account localFemaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap
                                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionEarlyPregnancyLosses,
                                                        ageBound,
                                                        BigDecimal.ONE,
                                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                                // Account annualTotalFemaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap
                                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalEarlyPregnancyLosses,
                                                        ageBound,
                                                        BigDecimal.ONE,
                                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                            }
                                        }
                                    }
                                } else {

//                                        // For Debugging
//                                        if (regionID.equalsIgnoreCase("00DAFA0077") && day == 364 && a_Agent_ID == 306) {
//                                            System.out.println("DEBUG: Simulating Female " + a_Agent_ID);
//                                        }

                                    // Pregnancy?
                                    isPregnancy = simulatePregnancy(
                                            fertility,
                                            a_Female,
                                            a_Agent_ID,
                                            //ageBound,
                                            subregionPregnantFemaleIDs,
                                            subregionNotPregnantFemaleIDs);
                                    if (isPregnancy) {
                                        totalPregnanciesInYear++;
                                        // Account Pregnancy
                                        // Account localFemaleAgeInYearsCountOfPregnancies_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPregnancies,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                        // Account annualTotalFemaleAgeInYearsCountOfPregnancies_TreeMap
                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPregnancies,
                                                ageBound,
                                                BigDecimal.ONE,
                                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                        /*
                                         * Although it might be neater to
                                         * pre-increment the count of the number of
                                         * days in early pregnancy, because the
                                         * simulation may begin with already 
                                         * pregnant females this is not 
                                         * straightforward.
                                         */
                                    }
                                }
                                // Migration
// The following determined destination region and subregion which is now done 
// at the end of the day so the data for this only need be loaded then.
//                                isMigration = simulateOutMigration(
//                                        migration,
//                                        a_Female,
//                                        a_Agent_ID,
//                                        a_FemaleCollection,
//                                        a_Collection_ID,
//                                        ageBound);
                                isMigration = simulateOutMigrationWithoutDeterminingDestination(
                                        migration,
                                        a_Female,
                                        a_Agent_ID,
                                        a_FemaleCollection,
                                        a_Collection_ID,
                                        ageBound);

//                                // debug
//                                if (a_Agent_ID == 499029) {
//                                    int debug = 1;
//                                    System.out.println("day " + day);
//                                    System.out.println(a_Female);
//                                    System.out.println("isMigration " + isMigration);
//                                }

                                if (isMigration) {
                                    subregionOutMigratingFemaleIDs = regionOutMigratingFemaleIDs.get(subregionID);
                                    if (subregionOutMigratingFemaleIDs == null) {
                                        subregionOutMigratingFemaleIDs = new HashSet<Long>();
                                        regionOutMigratingFemaleIDs.put(subregionID, subregionOutMigratingFemaleIDs);
                                    }
                                    subregionOutMigratingFemaleIDs.add(a_Agent_ID);
                                    // Account origin Populations
                                    // Account subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap
                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                                            ageBound,
                                            minusOne_BigDecimal,
                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    // Account regionTotalPopulation._FemaleAgeBoundPopulationCount_TreeMap
                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                                            ageBound,
                                            minusOne_BigDecimal,
                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                                    System.out.println("Female Out Migration");
//                                } else {
//                                    isMigration = simulateInternalMigrationInStudyRegion(
//                                            migration,
//                                            a_Female,
//                                            a_Agent_ID,
//                                            a_FemaleCollection,
//                                            a_Collection_ID,
//                                            ageBound,
//                                            regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap,
//                                            decimalPlacePrecisionForCalculations,
//                                            roundingMode);
//                                    if (isMigration) {
//                                        allInternalMigrationsInYear ++;
//                                        if (internalStudyRegionMigratingFemaleIDs == null) {
//                                            internalStudyRegionMigratingFemaleIDs = new HashSet<Long>();
//                                        }
//                                        internalStudyRegionMigratingFemaleIDs.add(a_Agent_ID);
//                                        // Account origin Populations
//                                        // Account localPopulation._FemaleAgeBoundPopulationCount_TreeMap
//                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                                subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap,
//                                                ageBound,
//                                                minusOne_BigDecimal);
//                                        // Account _Demographics._Total_Population._FemaleAgeBoundPopulationCount_TreeMap
//                                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                                regionTotalPopulation._FemaleAgeBoundPopulationCount_TreeMap,
//                                                ageBound,
//                                                minusOne_BigDecimal);
//                                        // Migration is completed at the end of the daily loop.
////                                        System.out.println("Female Internal Migration");
//                                    }
                                }
                            }
                        }
                        // Update indexes and total counts for births and deaths
                        if (babyGirl_IDs != null) {
                            subregionLivingFemaleIDs.addAll(babyGirl_IDs);
                            allBirthsInYear += babyGirl_IDs.size();
                            // Account for births
                            if (babyGirl_IDs.size() > 0) {
                                // Account subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                                        new GENESIS_AgeBound(0L),
                                        BigDecimal.valueOf(babyGirl_IDs.size()),
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                // Account regionTotalPopulation._FemaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                                        new GENESIS_AgeBound(0L),
                                        BigDecimal.valueOf(babyGirl_IDs.size()),
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            }
                        }
                        if (babyBoy_IDs != null) {
                            subregionLivingMaleIDs.addAll(babyBoy_IDs);
                            allBirthsInYear += babyBoy_IDs.size();
                            // Account for births
                            if (babyBoy_IDs.size() > 0) {
                                // Account subregionPopulation._MaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPopulation._MaleAgeBoundPopulationCount_TreeMap,
                                        new GENESIS_AgeBound(0L),
                                        BigDecimal.valueOf(babyBoy_IDs.size()),
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                // Account regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap
                                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap,
                                        new GENESIS_AgeBound(0L),
                                        BigDecimal.valueOf(babyBoy_IDs.size()),
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            }
                        }
                        if (dead_Female_ID_HashSet != null) {
                            subregionLivingFemaleIDs.removeAll(dead_Female_ID_HashSet);
                            subregionPregnantFemaleIDs.removeAll(dead_Female_ID_HashSet);
                            subregionNotPregnantFemaleIDs.removeAll(dead_Female_ID_HashSet);
                            if (subregionNearlyDuePregnantFemaleIDs != null) {
                                subregionNearlyDuePregnantFemaleIDs.removeAll(dead_Female_ID_HashSet);
                            }
                        }
                        if (dead_Male_ID_HashSet != null) {
                            subregionLivingMaleIDs.removeAll(dead_Male_ID_HashSet);
                        }
                    }
                }
            }
            // Swap out all data
            _GENESIS_Environment.swapToFile_Data();
            // Load migration destination data
            _Demographics._Migration.loadMigrationData();
            // Simulate In migration and Immigration
            System.out.println("Simulate In migration and Immigration");
            ite = _regionIDs.keySet().iterator();
            while (ite.hasNext()) {
                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
                String regionID = ite.next();
                System.out.println("regionID " + regionID);
//                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionImmigration;
//                regionImmigration = immigration.get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalImmigration;
//                regionTotalImmigration = regionImmigration.get(regionID);
//                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionOutMigration;
//                regionOutMigration = outMigration.get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalOutMigration;
//                regionTotalOutMigration = regionOutMigration.get(regionID);
//                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionInMigration;
//                regionInMigration = pregnancies.get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalInMigration;
//                regionTotalInMigration = regionInMigration.get(regionID);
//                TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionInternalMigration;
//                regionInternalMigration = internalMigration.get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalInternalMigration;
//                regionTotalInternalMigration = regionInternalMigration.get(regionID);

                // Simulate In migration from other lad in the UK that are not part of the study region.
                long inMigrationCount = simulateInMigrationFromRestOfUK(
                        migration,
                        regionID,
                        //regionTotalImmigration,
                        _LivingFemaleIDs.get(regionID),
                        _NotPregnantFemaleIDs.get(regionID),
                        _LivingMaleIDs.get(regionID));
                allMigrationIntoStudyRegionInYear += inMigrationCount;
                allMigrationInYear += inMigrationCount;
//                // Debug
//                if (_LivingMaleIDs.get(regionID) == null) {
//                    int debug = 1;
//                }
                // Simulate Immigration
                long immigrationCount = simulateImmigrationToStudyRegion(
                        migration,
                        regionID,
                        _LivingFemaleIDs.get(regionID),
                        _NotPregnantFemaleIDs.get(regionID),
                        _LivingMaleIDs.get(regionID));
                allImmigrationInYear += immigrationCount;
                allMigrationInYear += immigrationCount;
            }

//            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionImmigration;
//            regionImmigration = immigration.get(regionID);
//            TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalImmigration;
//            regionTotalImmigration = regionImmigration.get(regionID);
//            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionOutMigration;
//            regionOutMigration = outMigration.get(regionID);
//            TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalOutMigration;
//            regionTotalOutMigration = regionOutMigration.get(regionID);
//            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionInMigration;
//            regionInMigration = pregnancies.get(regionID);
//            TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalInMigration;
//            regionTotalInMigration = regionInMigration.get(regionID);
//            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionInternalMigration;
//            regionInternalMigration = internalMigration.get(regionID);
//            TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalInternalMigration;
//            regionTotalInternalMigration = regionInternalMigration.get(regionID);
//
//            TreeMap<GENESIS_AgeBound, BigDecimal> subregionImmigration;
//            subregionImmigration = regionImmigration.get(subregionID);
//            TreeMap<GENESIS_AgeBound, BigDecimal> subregionOutMigration;
//            subregionOutMigration = regionOutMigration.get(subregionID);
//            TreeMap<GENESIS_AgeBound, BigDecimal> subregionInMigration;
//            subregionInMigration = regionInMigration.get(subregionID);
//            TreeMap<GENESIS_AgeBound, BigDecimal> subregionInternalMigration;
//            subregionInternalMigration = regionInternalMigration.get(subregionID);

//            TreeMap<GENESIS_AgeBound, BigDecimal> subregionImmigration;
//            subregionImmigration = regionImmigration.get(subregionID);
//            TreeMap<GENESIS_AgeBound, BigDecimal> subregionOutMigration;
//            subregionOutMigration = regionOutMigration.get(subregionID);
//            TreeMap<GENESIS_AgeBound, BigDecimal> subregionInMigration;
//            subregionInMigration = regionInMigration.get(subregionID);
//            TreeMap<GENESIS_AgeBound, BigDecimal> subregionInternalMigration;
//            subregionInternalMigration = regionInternalMigration.get(subregionID);


            // Move migrating people
            Iterator<String> ites;
            // Female
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
            System.out.println("Move migrating people");

            ites = outMigratingFemaleIDs.keySet().iterator();
            type = GENESIS_Person.getTypeLivingFemale_String();
            System.out.println("females");
            String subregionID = "XXXXXXXX";
            String regionID = "XXXX";
            int messageLength = 100;
            String message2;
            message2 = this._GENESIS_Environment.initString(messageLength,
                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
            String destinationRegionID = "XXXX";
            String destinationSubregionID = destinationRegionID + "XXXX";
            String femaleSubregionID = destinationRegionID + "XXXX";
            String maleSubregionID = destinationRegionID + "XXXX";
            HashSet<Long> agentIDs = new HashSet<Long>();
            Iterator<Long> itel = agentIDs.iterator();
            Long a_Agent_ID = 0L;
            Long a_Collection_ID = 0L;
            String originRegionID = "XXXX";
            String originSubregionID = originRegionID + "XXXX";
            long age = 0L;
            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
            TreeMap<String, GENESIS_Population> destinationRegionPopulation = new TreeMap<String, GENESIS_Population>();
            TreeMap<String, HashSet<Long>> regionOutMigratingFemaleIDs = new TreeMap<String, HashSet<Long>>();
            Iterator<String> ites2 = regionOutMigratingFemaleIDs.keySet().iterator();
            GENESIS_Female a_Female;
            while (ites.hasNext()) {
                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                regionID = ites.next();
                message2 = "regionID " + regionID;
                System.out.println(message2);
                message2 = this._GENESIS_Environment.initString(messageLength,
                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                regionOutMigratingFemaleIDs = outMigratingFemaleIDs.get(regionID);
//                TreeMap<String, TreeSet<Long>> regionLivingFemaleIDs;
//                regionLivingFemaleIDs = _LivingFemaleIDs.get(regionID);
                ites2 = regionOutMigratingFemaleIDs.keySet().iterator();
                while (ites2.hasNext()) {
                    _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    subregionID = ites2.next();
                    message2 = "subregionID " + subregionID;
                    System.out.println(message2);

                    // DEBUG
                    if (subregionID.equalsIgnoreCase("00DBFK0029")) {
                        int debug = 1;
                    }

                    message2 = this._GENESIS_Environment.initString(messageLength,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                    if (subregionID.equalsIgnoreCase("00DBFT0029")) {
//                        System.out.println("subregionID " + subregionID);
//                        int debug = 1;
//                    }
                    agentIDs = regionOutMigratingFemaleIDs.get(subregionID);
                    itel = agentIDs.iterator();
                    while (itel.hasNext()) {
                        try {
                            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            a_Agent_ID = itel.next();

                            // DEBUG
                            if (subregionID.equalsIgnoreCase("00DBFK0029")) {
                                // OutOfMemoryError when migrationg first female ID 438932
                                System.out.println("Migrating Female " + a_Agent_ID);
                            }

//                            Long a_Collection_ID =
//                                    _GENESIS_AgentCollectionManager.getAgentCollection_ID(
//                                    a_Agent_ID);
//                            a_FemaleCollection =
//                                    _GENESIS_AgentCollectionManager.getFemaleCollection(
//                                    a_Collection_ID,
//                                    type);
//                            GENESIS_Female a_Female = a_FemaleCollection.getFemale(
//                                    a_Agent_ID);
                            a_Collection_ID =
                                    _GENESIS_AgentCollectionManager.getFemaleCollection_ID(a_Agent_ID,
                                    type,
                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            a_FemaleCollection =
                                    _GENESIS_AgentCollectionManager.getFemaleCollection(a_Collection_ID,
                                    type,
                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                            a_Female = a_FemaleCollection.getFemale(a_Agent_ID,
                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                        String femaleRegionID = a_Female.getRegionID();
//                        // debug
//                        if (femaleRegionID == null) {
//                            int debug = 1;
//                        }
//                        if (!_regionIDs.containsKey(femaleRegionID)) {


                            /*
                             * Previously migration destination was already 
                             * determined by this stage, but now it is determined 
                             * here for data management reasons.
                             */
                            destinationRegionID = migration.getOutMigrationRegionDestination(a_Female,
                                    a_Female.getRegionID(),
                                    _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                                    _RandomArray[14]);
                            destinationSubregionID = destinationRegionID + "XXXX";
                            if (_regionIDs.containsKey(destinationRegionID)) {
                                destinationSubregionID = migration.getInternalMigrationSubregionDestinationFromStudyRegion(a_Female,
                                        destinationRegionID,
                                        _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                                        _RandomArray[14]);
                            }
                            a_Female._ResidentialSubregionIDs.add(destinationSubregionID);

                            femaleSubregionID = a_Female.getSubregionID();
                            if (femaleSubregionID.endsWith("XXXX")) {
                                try {
                                    // Migration out of study region
                                    originRegionID = a_Female.getPreviousRegionID();

//                            // Check
//                            if (!regionID.equalsIgnoreCase(originRegionID)) {
//                                int debug = 1;
//                            }

                                    originSubregionID = a_Female.getPreviousSubregionID();
                                    _LivingFemaleIDs.get(originRegionID).get(originSubregionID).remove(a_Agent_ID);
                                    if (_PregnantFemaleIDs.get(originRegionID).get(originSubregionID).remove(a_Agent_ID)) {
                                        _NearlyDuePregnantFemaleIDs.get(originRegionID).get(originSubregionID).remove(a_Agent_ID);
                                    } else {
                                        _NotPregnantFemaleIDs.get(originRegionID).get(originSubregionID).remove(a_Agent_ID);
                                    }
                                    allMigrationInYear++;
                                    allMigrationOutOfStudyRegionInYear++;
                                } catch (OutOfMemoryError e) {
                                    throw e;
                                }
                            } else {
                                // InternalMigration within study region
                                try {
//                            //debug
//                            if (a_Female.get_Agent_ID(true ) == 499029) {
//                                int debug = 1;
//                            }

                                    originRegionID = a_Female.getPreviousRegionID();
                                    originSubregionID = a_Female.getPreviousSubregionID();
                                    //String destinationRegionID = a_Female.getRegionID();
                                    //String destinationSubregionID = a_Female.getSubregionID();
//                            System.out.println("InternalMigration within study region");
                                    //For debugging
//                            System.out.println("originRegionID " + originRegionID
//                                    + ", originSubregionID " + originSubregionID
//                                    + ", destinationRegionID " + destinationRegionID
//                                    + ", destinationSubregionID " + destinationSubregionID);
//                    if (originSubregionID.equalsIgnoreCase("00DBFT0036") &&
//                            destinationSubregionID.equalsIgnoreCase("00DAGK0083")) {
////00DBFX0050
//                        int debug = 1;
//                    }

                                    age = a_Female.getAge().getAgeInYears_long(_GENESIS_Environment._Time);
                                    ageBound = new GENESIS_AgeBound(age);
//                            TreeMap<String, GENESIS_Population> originRegionPopulation;
//                            originRegionPopulation = _Demographics._Population.get(originRegionID);
// Origin accounting already done
//                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                        originRegionPopulation.get(originRegionID)._FemaleAgeBoundPopulationCount_TreeMap,
//                        ageBound,
//                        minusOne_BigDecimal);
//                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                        originRegionPopulation.get(originSubregionID)._FemaleAgeBoundPopulationCount_TreeMap,
//                        ageBound,
//                        minusOne_BigDecimal);
                                    destinationRegionPopulation = _Demographics._Population.get(destinationRegionID);
                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(destinationRegionPopulation.get(destinationRegionID)._FemaleAgeBoundPopulationCount_TreeMap,
                                            ageBound,
                                            BigDecimal.ONE,
                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(destinationRegionPopulation.get(destinationSubregionID)._FemaleAgeBoundPopulationCount_TreeMap,
                                            ageBound,
                                            BigDecimal.ONE,
                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    _LivingFemaleIDs.get(originRegionID).get(originSubregionID).remove(a_Agent_ID);
                                    _LivingFemaleIDs.get(destinationRegionID).get(destinationSubregionID).add(a_Agent_ID);
                                    if (_PregnantFemaleIDs.get(originRegionID).get(originSubregionID).remove(a_Agent_ID)) {
                                        _PregnantFemaleIDs.get(destinationRegionID).get(destinationSubregionID).add(a_Agent_ID);
                                    } else {
                                        if (_NotPregnantFemaleIDs.get(originRegionID).get(originSubregionID).remove(a_Agent_ID)) {
                                            _NotPregnantFemaleIDs.get(destinationRegionID).get(destinationSubregionID).add(a_Agent_ID);
                                            if (_NearlyDuePregnantFemaleIDs.get(originRegionID).get(originSubregionID).remove(a_Agent_ID)) {
                                                _NearlyDuePregnantFemaleIDs.get(destinationRegionID).get(destinationSubregionID).add(a_Agent_ID);
                                            }
                                        }
                                    }
                                    allMigrationInYear++;
                                    allMigrationWithinStudyRegionInYear++;
                                    if (originRegionID.equalsIgnoreCase(destinationRegionID)) {
                                        allMigrationWithinRegionsInYear++;
                                    }
                                } catch (OutOfMemoryError e) {
                                    throw e;
                                }
                            }
                        } catch (OutOfMemoryError e) {
                            throw e;
                        }
                    }
                    agentIDs.clear();
                }
            }
            // Male 
            System.out.println("male");
            GENESIS_Male a_Male;
            TreeMap<String, HashSet<Long>> regionOutMigratingMaleIDs = new TreeMap<String, HashSet<Long>>();
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
            ites = outMigratingMaleIDs.keySet().iterator();
            type = GENESIS_Person.getTypeLivingMale_String();
            while (ites.hasNext()) {
                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
                regionID = ites.next();
                message2 = "regionID " + regionID;
                System.out.println(message2);
                message2 = this._GENESIS_Environment.initString(messageLength,
                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                regionOutMigratingMaleIDs = outMigratingMaleIDs.get(regionID);
//                TreeMap<String, TreeSet<Long>> regionLivingMaleIDs;
//                regionLivingMaleIDs = _LivingMaleIDs.get(regionID);
                ites2 = regionOutMigratingMaleIDs.keySet().iterator();
                while (ites2.hasNext()) {
                    try {
                        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        subregionID = ites2.next();
                        message2 = "subregionID " + subregionID;
                        System.out.println(message2);

                        // DEBUG
//                        if (subregionID.equalsIgnoreCase("00DAFH0046")) {
//                  if (subregionID.equalsIgnoreCase("00DAFH0029")) {
                        if (subregionID.equalsIgnoreCase("00DAFN0036")) {
                            int debug = 1;
                        }

                        message2 = this._GENESIS_Environment.initString(messageLength,
                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        agentIDs = regionOutMigratingMaleIDs.get(subregionID);
                        itel = agentIDs.iterator();
                        while (itel.hasNext()) {
                            try {
                                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                a_Agent_ID = itel.next();

                                // DEBUG
                                //if (subregionID.equalsIgnoreCase("00DAFH0046")) {
                                if (subregionID.equalsIgnoreCase("00DAFN0036")) {
                                     // OutOfMemoryError when migrationg first male ID 130837
                                    System.out.println("Migrating Male " + a_Agent_ID);
                                }


//                            Long a_Collection_ID =
//                                    _GENESIS_AgentCollectionManager.getAgentCollection_ID(
//                                    a_Agent_ID);
//                            a_MaleCollection =
//                                    _GENESIS_AgentCollectionManager.getMaleCollection(
//                                    a_Collection_ID,
//                                    type);
//                            GENESIS_Male a_Male = a_MaleCollection.getMale(
//                                    a_Agent_ID);
                                a_Collection_ID =
                                        _GENESIS_AgentCollectionManager.getMaleCollection_ID(a_Agent_ID,
                                        type,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                a_MaleCollection =
                                        _GENESIS_AgentCollectionManager.getMaleCollection(a_Collection_ID,
                                        type,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                a_Male = a_MaleCollection.getMale(a_Agent_ID,
                                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);

//                        String maleRegionID = a_Male.getRegionID();
//                        // debug
//                        if (maleRegionID == null) {
//                            int debug = 1;
//                        }
//                        if (!_regionIDs.containsKey(maleRegionID)) {

                                /*
                                 * Previously migration destination was already 
                                 * determined by this stage, but now it is determined 
                                 * here for data management reasons.
                                 */
                                destinationRegionID = migration.getOutMigrationRegionDestination(a_Male,
                                        a_Male.getRegionID(),
                                        _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                                        _RandomArray[14]);
                                destinationSubregionID = destinationRegionID + "XXXX";
                                if (_regionIDs.containsKey(destinationRegionID)) {
                                    destinationSubregionID = migration.getInternalMigrationSubregionDestinationFromStudyRegion(a_Male,
                                            destinationRegionID,
                                            _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                                            _RandomArray[14]);
                                }
                                a_Male._ResidentialSubregionIDs.add(destinationSubregionID);

                                maleSubregionID = a_Male.getSubregionID();
                                if (maleSubregionID.endsWith("XXXX")) {
                                    // Migration out of study region
                                    originRegionID = a_Male.getPreviousRegionID();

//                            // Check
//                            if (!regionID.equalsIgnoreCase(originRegionID)) {
//                                int debug = 1;
//                            }

                                    originSubregionID = a_Male.getPreviousSubregionID();
                                    _LivingMaleIDs.get(originRegionID).get(originSubregionID).remove(a_Agent_ID);
                                    allMigrationInYear++;
                                    allMigrationOutOfStudyRegionInYear++;
                                } else {
                                    // InternalMigration within study region
                                    originRegionID = a_Male.getPreviousRegionID();
                                    originSubregionID = a_Male.getPreviousSubregionID();
                                    //String destinationRegionID = a_Male.getRegionID();
                                    //String destinationSubregionID = a_Male.getSubregionID();
//                            System.out.println("InternalMigration within study region");
                                    //For debugging
//                            System.out.println("originRegionID " + originRegionID
//                                    + ", originSubregionID " + originSubregionID
//                                    + ", destinationRegionID " + destinationRegionID
//                                    + ", destinationSubregionID " + destinationSubregionID);
//                    if (originSubregionID.equalsIgnoreCase("00DBFT0036") &&
//                            destinationSubregionID.equalsIgnoreCase("00DAGK0083")) {
////00DBFX0050
//                        int debug = 1;
//                    }

                                    age = a_Male.getAge().getAgeInYears_long(_GENESIS_Environment._Time);
                                    ageBound = new GENESIS_AgeBound(age);
//                            TreeMap<String, GENESIS_Population> originRegionPopulation;
//                            originRegionPopulation = _Demographics._Population.get(originRegionID);
// Origin accounting already done
//                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                        originRegionPopulation.get(originRegionID)._MaleAgeBoundPopulationCount_TreeMap,
//                        ageBound,
//                        minusOne_BigDecimal);
//                GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                        originRegionPopulation.get(originSubregionID)._MaleAgeBoundPopulationCount_TreeMap,
//                        ageBound,
//                        minusOne_BigDecimal);
                                    destinationRegionPopulation = _Demographics._Population.get(destinationRegionID);
                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(destinationRegionPopulation.get(destinationRegionID)._MaleAgeBoundPopulationCount_TreeMap,
                                            ageBound,
                                            BigDecimal.ONE,
                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(destinationRegionPopulation.get(destinationSubregionID)._MaleAgeBoundPopulationCount_TreeMap,
                                            ageBound,
                                            BigDecimal.ONE,
                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    _LivingMaleIDs.get(originRegionID).get(originSubregionID).remove(a_Agent_ID);
                                    _LivingMaleIDs.get(destinationRegionID).get(destinationSubregionID).add(a_Agent_ID);
                                    allMigrationInYear++;
                                    allMigrationWithinStudyRegionInYear++;
                                    if (originRegionID.equalsIgnoreCase(destinationRegionID)) {
                                        allMigrationWithinRegionsInYear++;
                                    }
                                }
                            } catch (OutOfMemoryError e) {
                                throw e;
                            }
                        }
                    } catch (OutOfMemoryError e) {
                        throw e;
                    }
                    agentIDs.clear();
                }
            }
            System.out.println("<Move migrating people>");
            _Demographics._Migration.rationaliseMigrationData();

            // Debug
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
            String msg = "totalDeathsInYear " + totalDeathsInYear
                    + ", allBirthsInYear " + allBirthsInYear
                    + ", singleBirthsInYear " + singleBirthsInYear
                    + ", twinBirthsInYear " + twinBirthsInYear
                    + ", twinRate " + (double) twinBirthsInYear / (double) allBirthsInYear
                    + ", tripletBirthsInYear " + tripletBirthsInYear
                    + ", tripletRate " + (double) tripletBirthsInYear / (double) allBirthsInYear
                    + ", totalPregnanciesInYear " + totalPregnanciesInYear
                    + ", totalEarlyPregnancyLossMiscarriagesInYear " + totalEarlyPregnancyLossMiscarriagesInYear
                    + ", totalClinicalMiscarriagesInYear " + totalClinicalMiscarriagesInYear
                    + ", daysInEarlyPregnancyInYear " + daysInEarlyPregnancyInYear
                    + ", daysInLatePregnancyInYear " + daysInLatePregnancyInYear
                    + ", early pregnancy loss miscarriage rate "
                    //                        + totalEarlyPregnancyLossMiscarriagesInYear
                    //                        / (double) daysInEarlyPregnancyInYear
                    + totalEarlyPregnancyLossMiscarriagesInYear
                    / ((double) daysInEarlyPregnancyInYear
                    / (double) _Demographics._Miscarriage.get(_regionIDs.firstKey()).get(_regionIDs.firstKey()).getNumberOfDaysInEarlyPregnancy())
                    //                        + totalEarlyPregnancyLossMiscarriagesInYear
                    //                        / ((double) (daysInEarlyPregnancyInYear
                    //                        / ((double) _Demographics._Miscarriage.getNumberOfDaysInEarlyPregnancy()
                    //                        / (double) GENESIS_Time.NormalDaysInYear_int)))
                    + ", clinical miscarriage rate "
                    //                        + totalClinicalMiscarriagesInYear
                    //                        / (double) daysInLatePregnancyInYear
                    + totalClinicalMiscarriagesInYear
                    / ((double) daysInLatePregnancyInYear
                    / (double) _Demographics._Miscarriage.get(_regionIDs.firstKey()).get(_regionIDs.firstKey()).getNumberOfDaysInLatePregnancy_double())
                    //                        + totalClinicalMiscarriagesInYear
                    //                        / ((double) (daysInLatePregnancyInYear
                    //                        / ((double) _Demographics._Miscarriage.getNumberOfDaysInLatePregnancy_double()
                    //                        / (double) GENESIS_Time.NormalDaysInYear_int)))
                    + ", general miscarriage rate "
                    + (totalEarlyPregnancyLossMiscarriagesInYear + totalClinicalMiscarriagesInYear)
                    / ((double) (daysInEarlyPregnancyInYear + daysInLatePregnancyInYear)
                    / (double) _Demographics._Miscarriage.get(_regionIDs.firstKey()).get(_regionIDs.firstKey()).getExpectedNumberOfDaysInFullTermPregnancy())
                    + ", allMigrationsInYear " + allMigrationInYear
                    + ", allInMigrationsFromOutOfStudyRegionInYear " + allMigrationIntoStudyRegionInYear
                    + ", allImmigrationsInYear " + allImmigrationInYear
                    + ", allMigrationOutOfStudyRegionInYear " + allMigrationOutOfStudyRegionInYear
                    + ", allMigrationWithinStudyRegionInYear " + allMigrationWithinStudyRegionInYear
                    + ", allMigrationWithinRegionsInYear " + allMigrationWithinRegionsInYear;
//                        + (totalEarlyPregnancyLossMiscarriagesInYear + totalClinicalMiscarriagesInYear)
//                        / (double) (daysInEarlyPregnancyInYear + daysInLatePregnancyInYear);
            log(Level.FINE, msg);
            System.out.println(msg);
            _GENESIS_Environment._Time.addDay();
        }
        // Update region counts that need updating and updateGenderedAgePopulations
        ite = _regionIDs.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            TreeMap<String, GENESIS_Population> regionPopulation = _Demographics._Population.get(regionID);
            GENESIS_Population regionTotalPopulation = regionPopulation.get(regionID);
            TreeMap<String, GENESIS_Population> regionAliveDays = aliveDays.get(regionID);
            GENESIS_Population regionTotalAliveDays = regionAliveDays.get(regionID);

            // Mortality
            // regionDeaths
            TreeMap<String, GENESIS_Population> regionDeaths = deaths.get(regionID);
            //TreeMap<String, GENESIS_Population> regionDeaths = _Demographics._Deaths.get(regionID);
            GENESIS_Population regionTotalDeaths = regionDeaths.get(regionID);
            // regionMortality
            //TreeMap<String, GENESIS_Mortality> regionMortality = _Demographics._Mortality.get(regionID);

            // Miscarriage
            // regionClinicalMiscarriage
            //TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionClinicalMiscarriage = clinicalMiscarriages.get(regionID);
            //TreeMap<String, GENESIS_Population> regionClinicalMiscarriage = _Demographics._ClinicalMiscarriage.get(regionID);
            // regionEarlyPregnancyLoss
            //TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionEarlyPregnancyLoss = earlyPregnancyLosses.get(regionID);
            //TreeMap<String, GENESIS_Population> regionEarlyPregnancyLoss = _Demographics._EarlyPregnancyLoss.get(regionID);
            // regionMiscarriage
            //TreeMap<String, GENESIS_Miscarriage> regionMiscarriage = _Demographics._Miscarriage.get(regionID);

            // Fertility
            // regionFertility
            //TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionFertility = births.get(regionID);
            //TreeMap<String, GENESIS_Fertility> regionFertility = _Demographics._Fertility.get(regionID);
            // regionSingleBirths
            //TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionSingleBirths = singleBirths.get(regionID);
            //TreeMap<String, GENESIS_Population> regionSingleBirths = _Demographics._SingleBirths.get(regionID);
            // regionTwinBirths
            //TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionTwinBirths = twins.get(regionID);
            //TreeMap<String, GENESIS_Population> regionTwinBirths = _Demographics._TwinBirths.get(regionID);
            // regionTripletBirths
            //TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionTripletBirths = triplets.get(regionID);
            //TreeMap<String, GENESIS_Population> regionTripletBirths = _Demographics._TripletBirths.get(regionID);

//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalLabours = labours.get(regionID).get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalBirths = births.get(regionID).get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalEarlyPregnancyLosses = earlyPregnancyLosses.get(regionID).get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalClinicalMiscarriages = clinicalMiscarriages.get(regionID).get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalDaysInEarlyPregnancy = daysInEarlyPregnancy.get(regionID).get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalDaysInLatePregnancy = daysInLatePregnancy.get(regionID).get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalTwins = twins.get(regionID).get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalTriplets = triplets.get(regionID).get(regionID);
//                TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalPregnancies = pregnancies.get(regionID).get(regionID);
            TreeSet<String> subregionIDs = _regionIDs.get(regionID);
            Iterator<String> ite2;
            ite2 = subregionIDs.iterator();
            while (ite2.hasNext()) {
                String subregionID = ite2.next();
                if (!regionID.equalsIgnoreCase(subregionID)) {
                    // Subregion population updates
                    // subregionAliveDays
                    GENESIS_Population subregionAliveDays;
                    subregionAliveDays = regionAliveDays.get(subregionID);
                    regionTotalAliveDays.addPopulationNoUpdate(subregionAliveDays);
                    //subregionAliveDays.updateGenderedAgePopulation();
                    // subregionDeaths
                    GENESIS_Population subregionDeaths;
                    subregionDeaths = regionDeaths.get(subregionID);
                    regionTotalDeaths.addPopulationNoUpdate(subregionDeaths);
                    //subregionDeaths.updateGenderedAgePopulation();

                    GENESIS_Population regionSubpopulation = regionPopulation.get(subregionID);
                    regionSubpopulation.updateGenderedAgePopulation();

// @TODO The various regionTotals are updated in simulation loop. 
// It may be better to do the updating here
//                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionLabours;
//                        subregionLabours = labours.get(regionID).get(subregionID);
//                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                regionTotalLabours, subregionLabours);
//                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionBirths;
//                        subregionBirths = births.get(regionID).get(subregionID);
//                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                regionTotalBirths, subregionBirths);
//                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionEarlyPregnancyLosses;
//                        subregionEarlyPregnancyLosses = earlyPregnancyLosses.get(regionID).get(subregionID);
//                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                regionTotalEarlyPregnancyLosses, subregionEarlyPregnancyLosses);
//                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionClinicalMiscarriages;
//                        subregionClinicalMiscarriages = clinicalMiscarriages.get(regionID).get(subregionID);
//                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                regionTotalClinicalMiscarriages, subregionClinicalMiscarriages);
//                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionDaysInEarlyPregnancy;
//                        subregionDaysInEarlyPregnancy = daysInEarlyPregnancy.get(regionID).get(subregionID);
//                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                regionTotalDaysInEarlyPregnancy, subregionDaysInEarlyPregnancy);
//                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionDaysInLatePregnancy;
//                        subregionDaysInLatePregnancy = daysInLatePregnancy.get(regionID).get(subregionID);
//                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                regionTotalDaysInLatePregnancy, subregionDaysInLatePregnancy);
//                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionTwins;
//                        subregionTwins = twins.get(regionID).get(subregionID);
//                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                regionTotalTwins, subregionTwins);
//                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionTriplets;
//                        subregionTriplets = triplets.get(regionID).get(subregionID);
//                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                regionTotalTriplets, subregionTriplets);
//                        TreeMap<GENESIS_AgeBound, BigDecimal> subregionPregnancies;
//                        subregionPregnancies = pregnancies.get(regionID).get(subregionID);
//                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                                regionTotalPregnancies, subregionPregnancies);
                }
            }
            regionTotalAliveDays.updateGenderedAgePopulation();
            regionTotalDeaths.updateGenderedAgePopulation();
            regionTotalPopulation.updateGenderedAgePopulation();
        }
        totalDeaths_int += totalDeathsInYear;
        allBirths_int += allBirthsInYear;
        births_int += singleBirthsInYear;
        twinBirths_int += twinBirthsInYear;
        tripletBirths_int += tripletBirthsInYear;
        totalPregnancies_int += totalPregnanciesInYear;
        totalEarlyPregnancyLossMiscarriages_int += totalEarlyPregnancyLossMiscarriagesInYear;
        totalClinicalMiscarriages_int += totalClinicalMiscarriagesInYear;
//            String msg = "year " + year
//                + ", totalDeathsInYear " + totalDeathsInYear
        String msg = "totalDeathsInYear " + totalDeathsInYear
                + ", allBirthsInYear " + allBirthsInYear
                + ", singleBirthsInYear " + singleBirthsInYear
                + ", twinBirthsInYear " + twinBirthsInYear
                + ", twinRate " + (double) twinBirthsInYear / (double) allBirthsInYear
                + ", tripletBirthsInYear " + tripletBirthsInYear
                + ", tripletRate " + (double) tripletBirthsInYear / (double) allBirthsInYear
                + ", totalPregnanciesInYear " + totalPregnanciesInYear
                + ", totalEarlyPregnancyLossMiscarriages " + totalEarlyPregnancyLossMiscarriages_int
                + ", totalClinicalMiscarriages " + totalClinicalMiscarriages_int;
        log(Level.FINE, msg);
        System.out.println(msg);

        // Swap out data before trying to output all images.
        writeOutLivingCollectionNotAlreadyStoredOnFile(_HandleOutOfMemoryError);
        writeOutDeadCollectionNotAlreadyStoredOnFile();
        //_GENESIS_Environment.swapToFile_Data();
        //_GENESIS_Environment.swapToFile_AgentCollections(_HandleOutOfMemoryError);

        // Output
        HashSet< Future> newfutures = _Demographics.output(executorService,
                _regionIDs,
                theOAtoMSOALookup,
                theMSOAAreaCodes_TreeSet,
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                dataWidthForAllAgesAgeGenderPlots,
                dataHeightForAllAgesAgeGenderPlots,
                dataWidthForFertileAgesAgeGenderPlots,
                dataHeightForFertileAgesAgeGenderPlots,
                decimalPlacePrecisionForCalculations,
                significantDigits,
                roundingMode,
                startYear_Demographics,
                minAgeYearsForPopulationDisplays,
                maxAgeYearsForPopulationDisplays,
                minAgeYearsForFertilityDisplays,
                maxAgeYearsForFertilityDisplays,
                aliveDays,
                deaths,
                labours,
                births,
                earlyPregnancyLosses,
                clinicalMiscarriages,
                daysInEarlyPregnancy,
                daysInLatePregnancy,
                twins,
                triplets,
                pregnancies,
                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
        result.addAll(newfutures);
//          }
        log(Level.FINE,
                "TotalDeaths " + totalDeaths_int
                + ", allBirths " + allBirths_int
                + ", singleBirths " + births_int
                + ", twinBirths " + twinBirths_int
                + ", twinRate " + (double) twinBirths_int / (double) allBirths_int
                + ", tripletBirths " + tripletBirths_int
                + ", tripletRate " + (double) tripletBirths_int / (double) allBirths_int
                + ", totalPregnancies " + totalPregnancies_int);
        return result;
    }

    /**
     * @param regionIDMap
     * @param subregionIDs
     * @param subregionID
     * @return
     */
    protected TreeSet<Long> getSubregionIDs(
            TreeMap<String, TreeSet<Long>> regionIDMap,
            TreeSet<Long> subregionIDs,
            String subregionID) {
        TreeSet<Long> result = subregionIDs;
        if (subregionIDs == null) {
            result = new TreeSet<Long>();
            regionIDMap.put(subregionID, result);
        }
        return result;
    }

    /**
     *
     * @param mortality
     * @param a_Female
     * @param a_Female_ID
     * @param a_FemaleCollection
     * @param a_Collection_ID
     * @param ageBound
     * @param subregionPopulation
     * @param regionTotalPopulation
     * @param pregnantFemaleIDs
     * @param nearlyDuePregnantFemaleIDs
     * @param notPregnantFemaleIDs
     * @param minusOne_BigDecimal
     * @return true if there is a death
     */
    public boolean simulateDeath(
            GENESIS_Mortality mortality,
            GENESIS_Female a_Female,
            Long a_Female_ID,
            GENESIS_FemaleCollection a_FemaleCollection,
            Long a_Collection_ID,
            GENESIS_AgeBound ageBound,
            GENESIS_Population subregionPopulation,
            GENESIS_Population regionTotalPopulation,
            TreeSet<Long> pregnantFemaleIDs,
            TreeSet<Long> nearlyDuePregnantFemaleIDs,
            TreeSet<Long> notPregnantFemaleIDs,
            BigDecimal minusOne_BigDecimal) {
        boolean result = false;
        BigDecimal a_DailyDeathRate = mortality.getDailyMortality(
                a_Female);
//        if (Generic_BigDecimal.randomUniformTest(
//                _RandomArray[4],
//                a_DailyDeathRate,
////                _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                _GENESIS_Environment.RoundingModeForPopulationProbabilities)) {
        if (Generic_BigDecimal.randomUniformTest(_RandomArray[5],
                a_DailyDeathRate,
                _GENESIS_Environment.MathContextForPopulationProbabilities)) {
            a_Female.set_Death_Time(_GENESIS_Environment._Time);
            result = true;
            if (pregnantFemaleIDs.remove(a_Female_ID)) {
                nearlyDuePregnantFemaleIDs.remove(a_Female_ID);
            } else {
                notPregnantFemaleIDs.remove(a_Female_ID);
            }
            //_Female_ID_HashSet.remove(a_Female_ID); Done out of this method to prevent ConcurrentModificationException
            a_Female.setType(GENESIS_Person.getTypeDeadFemale_String());
            // Add Female to deadFemaleCollection and get its ID
            Long deadFemaleCollectionID =
                    _GENESIS_AgentCollectionManager.addToDeadFemaleCollection(
                    a_Female_ID,
                    a_Female);
            // Add Mapping to _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap
            _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap.put(
                    a_Female_ID,
                    deadFemaleCollectionID);
            // Remove a_Female from a_FemaleCollection
            boolean collectionEmpty = a_FemaleCollection.remove(a_Female_ID);
            // If a_FemaleCollection is now empty get mappings and persist to
            // file and free fast access memory resources.
            if (collectionEmpty) {
                HashSet<Long> collectionPossibleAgentIDs =
                        a_FemaleCollection.getPossibleAgentIDs_HashSet();
                // Get mappings from _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap
                // and add them to mapping to be written out
                HashMap<Long, Long> _DeadFemaleCollectionMap =
                        new HashMap<Long, Long>(collectionPossibleAgentIDs.size());
                Iterator<Long> ite = collectionPossibleAgentIDs.iterator();
                Long agentID;
                Long agentCollectionID;
                while (ite.hasNext()) {
                    agentID = ite.next();
                    agentCollectionID = _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap.get(agentID);
                    _DeadFemaleCollectionMap.put(agentID, agentCollectionID);
                    _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap.remove(agentID);
                }
                // Write out _DeadFemaleCollectionMap
                File _DeadFemaleCollectionMap_File = new File(
                        a_FemaleCollection.getDirectory(),
                        "DeadCollection_HashMap.thisFile");
                Generic_StaticIO.writeObject(
                        _DeadFemaleCollectionMap,
                        _DeadFemaleCollectionMap_File);
                a_FemaleCollection = null;
                // Remove Mapping from _GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap
                _GENESIS_AgentCollectionManager._LivingFemaleCollection_HashMap.remove(a_Collection_ID);
            }
            // Account Populations
            //GENESIS_AgeBound singleYearAgeBound = new GENESIS_AgeBound(a_Female.getAge().getAgeInYears());
            // Account subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap
            GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                    ageBound,
                    minusOne_BigDecimal,
                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
            // Account regionTotalPopulation._FemaleAgeBoundPopulationCount_TreeMap
            GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                    ageBound,
                    minusOne_BigDecimal,
                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
        }
        return result;
    }

    /**
     *
     * @param mortality
     * @param a_Male
     * @param a_Male_ID
     * @param a_MaleCollection
     * @param a_Collection_ID
     * @param ageBound
     * @param subregionPopulation
     * @param regionTotalPopulation
     * @param minusOne_BigDecimal
     * @return true if there is a death
     */
    public boolean simulateDeath(
            GENESIS_Mortality mortality,
            GENESIS_Male a_Male,
            Long a_Male_ID,
            GENESIS_MaleCollection a_MaleCollection,
            Long a_Collection_ID,
            GENESIS_AgeBound ageBound,
            GENESIS_Population subregionPopulation,
            GENESIS_Population regionTotalPopulation,
            BigDecimal minusOne_BigDecimal) {
        boolean result = false;
//        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
        BigDecimal a_DailyDeathRate;
        a_DailyDeathRate = mortality.getDailyMortality(a_Male);
//        if (Generic_BigDecimal.randomUniformTest(
//                _RandomArray[5],
//                a_DailyDeathRate,
////                _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                _GENESIS_Environment.RoundingModeForPopulationProbabilities)) {
        if (Generic_BigDecimal.randomUniformTest(_RandomArray[5],
                a_DailyDeathRate,
                _GENESIS_Environment.MathContextForPopulationProbabilities)) {
            a_Male.set_Death_Time(_GENESIS_Environment._Time);
            result = true;
            //_Male_ID_HashSet.remove(a_Male_ID); Done out of this method to prevent ConcurrentModificationException
            a_Male.setType(GENESIS_Person.getTypeDeadMale_String());
            // Add Male to deadMaleCollection and get its ID
            Long deadMaleCollectionID =
                    _GENESIS_AgentCollectionManager.addToDeadMaleCollection(
                    a_Male_ID,
                    a_Male);
            // Add Mapping to _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap
            _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap.put(
                    a_Male_ID,
                    deadMaleCollectionID);
            // Remove a_Male from a_MaleCollection
            boolean collectionEmpty = a_MaleCollection.remove(a_Male_ID);
            // If a_MaleCollection is now empty get mappings and persist to
            // file and free fast access memory resources.
            if (collectionEmpty) {
                HashSet<Long> collectionPossibleAgentIDs =
                        a_MaleCollection.getPossibleAgentIDs_HashSet();
                // Get mappings from _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap
                // and add them to mapping to be written out
                HashMap<Long, Long> theDeadMaleCollectionMap =
                        new HashMap<Long, Long>(collectionPossibleAgentIDs.size());
                Iterator<Long> ite = collectionPossibleAgentIDs.iterator();
                Long agentID;
                Long agentCollectionID;
                while (ite.hasNext()) {
                    agentID = ite.next();
                    agentCollectionID = _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap.get(agentID);
                    theDeadMaleCollectionMap.put(agentID, agentCollectionID);
                    _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap.remove(agentID);
                }
                // Write out _DeadMaleCollectionMap
                File theDeadMaleCollectionMap_File = new File(
                        a_MaleCollection.getDirectory(),
                        "DeadCollection_HashMap.thisFile");
                Generic_StaticIO.writeObject(
                        theDeadMaleCollectionMap,
                        theDeadMaleCollectionMap_File);
                a_MaleCollection = null;
                // Remove Mapping from _GENESIS_AgentCollectionManager._LivingMaleCollection_HashMap
                _GENESIS_AgentCollectionManager._LivingMaleCollection_HashMap.remove(a_Collection_ID);
            }
            // Account Populations
            //GENESIS_AgeBound singleYearAgeBound = new GENESIS_AgeBound(a_Male.getAge().getAgeInYears());
            // Account subregionPopulation._MaleAgeBoundPopulationCount_TreeMap
            GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(subregionPopulation._MaleAgeBoundPopulationCount_TreeMap,
                    ageBound,
                    minusOne_BigDecimal,
                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
            // Account regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap
            GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(regionTotalPopulation._MaleAgeBoundPopulationCount_TreeMap,
                    ageBound,
                    minusOne_BigDecimal,
                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
        }
        return result;
    }

    public boolean simulateOutMigration(
            GENESIS_Migration migration,
            GENESIS_Female a_Female,
            Long a_Female_ID,
            GENESIS_FemaleCollection a_FemaleCollection,
            Long a_Collection_ID,
            GENESIS_AgeBound ageBound) {
        boolean result = false;
        BigDecimal a_DailyMigrationRate;
        a_DailyMigrationRate = migration.getDailyOutMigrationProbability(
                a_Female,
                ageBound);
        if (Generic_BigDecimal.randomUniformTest(_RandomArray[14],
                a_DailyMigrationRate,
                _GENESIS_Environment.MathContextForPopulationProbabilities)) {
            result = true;
            String destinationRegionID = migration.getOutMigrationRegionDestination(a_Female,
                    a_Female.getRegionID(),
                    _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                    _RandomArray[14]);
            String destinationSubregionID = destinationRegionID + "XXXX";
            if (_regionIDs.containsKey(destinationRegionID)) {
                destinationSubregionID = migration.getInternalMigrationSubregionDestinationFromStudyRegion(a_Female,
                        destinationRegionID,
                        _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                        _RandomArray[14]);
            }
            a_Female._ResidentialSubregionIDs.add(destinationSubregionID);
        }
        return result;
    }

    public boolean simulateOutMigrationWithoutDeterminingDestination(
            GENESIS_Migration migration,
            GENESIS_Female a_Female,
            Long a_Female_ID,
            GENESIS_FemaleCollection a_FemaleCollection,
            Long a_Collection_ID,
            GENESIS_AgeBound ageBound) {
        boolean result = false;
        BigDecimal a_DailyMigrationRate;
        a_DailyMigrationRate = migration.getDailyOutMigrationProbability(
                a_Female,
                ageBound);
        if (Generic_BigDecimal.randomUniformTest(_RandomArray[14],
                a_DailyMigrationRate,
                _GENESIS_Environment.MathContextForPopulationProbabilities)) {
            result = true;
//            String destinationRegionID = migration.getOutMigrationRegionDestination(
//                    a_Female,
//                    a_Female.getRegionID(),
//                    _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    _RandomArray[14]);
//            String destinationSubregionID = destinationRegionID + "XXXX";
//            if (_regionIDs.containsKey(destinationRegionID)) {
//                destinationSubregionID = migration.getInternalMigrationSubregionDestinationFromStudyRegion(
//                        a_Female,
//                        destinationRegionID,
//                        _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                        _RandomArray[14]);
//            }
//            a_Female._ResidentialSubregionIDs.add(destinationSubregionID);
        }
        return result;
    }

    public boolean simulateOutMigration(
            GENESIS_Migration migration,
            GENESIS_Male a_Male,
            Long a_Male_ID,
            GENESIS_MaleCollection a_MaleCollection,
            Long a_Collection_ID,
            GENESIS_AgeBound ageBound) {
        boolean result = false;
        BigDecimal a_DailyMigrationRate;
        a_DailyMigrationRate = migration.getDailyOutMigrationProbability(
                a_Male,
                ageBound);
        if (Generic_BigDecimal.randomUniformTest(_RandomArray[14],
                a_DailyMigrationRate,
                _GENESIS_Environment.MathContextForPopulationProbabilities)) {
            result = true;
            String destinationRegionID = migration.getOutMigrationRegionDestination(a_Male,
                    a_Male.getRegionID(),
                    _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                    _RandomArray[14]);
            String destinationSubregionID = destinationRegionID + "XXXX";
            if (_regionIDs.containsKey(destinationRegionID)) {
                destinationSubregionID = migration.getInternalMigrationSubregionDestinationFromStudyRegion(a_Male,
                        destinationRegionID,
                        _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                        _RandomArray[14]);
            }
            a_Male._ResidentialSubregionIDs.add(destinationSubregionID);
        }
        return result;
    }

    public boolean simulateOutMigrationWithoutDeterminingDestination(
            GENESIS_Migration migration,
            GENESIS_Male a_Male,
            Long a_Male_ID,
            GENESIS_MaleCollection a_MaleCollection,
            Long a_Collection_ID,
            GENESIS_AgeBound ageBound) {
        boolean result = false;
        BigDecimal a_DailyMigrationRate;
        a_DailyMigrationRate = migration.getDailyOutMigrationProbability(
                a_Male,
                ageBound);
        if (Generic_BigDecimal.randomUniformTest(_RandomArray[14],
                a_DailyMigrationRate,
                _GENESIS_Environment.MathContextForPopulationProbabilities)) {
            result = true;
//            String destinationRegionID = migration.getOutMigrationRegionDestination(
//                    a_Male,
//                    a_Male.getRegionID(),
//                    _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    _RandomArray[14]);
//            String destinationSubregionID = destinationRegionID + "XXXX";
//            if (_regionIDs.containsKey(destinationRegionID)) {
//                destinationSubregionID = migration.getInternalMigrationSubregionDestinationFromStudyRegion(
//                        a_Male,
//                        destinationRegionID,
//                        _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                        _RandomArray[14]);
//            }
//            a_Male._ResidentialSubregionIDs.add(destinationSubregionID);
        }
        return result;
    }

    public long simulateInMigrationFromRestOfUK(
            GENESIS_Migration migration,
            String regionID,
            //TreeMap<GENESIS_AgeBound, BigDecimal> regionTotalImmigration,
            TreeMap<String, TreeSet<Long>> regionLivingFemaleIDs,
            TreeMap<String, TreeSet<Long>> regionNotPregnantFemaleIDs,
            TreeMap<String, TreeSet<Long>> regionLivingMaleIDs) {
        System.out.println("<simulateInMigrationFromRestOfUK>");
//        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
        long result = 0;
        int decimalPlaces = 10;
        //RoundingMode roundingMode = RoundingMode.HALF_UP;
        //HashSet<GENESIS_Female> females = new HashSet<GENESIS_Female>();
        //HashSet<GENESIS_Male> males = new HashSet<GENESIS_Male>();
        GENESIS_Population pop = migration.getDailyInMigrationRate(regionID);
        if (pop != null) {
            Iterator<GENESIS_AgeBound> ite;
            ite = pop._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
            while (ite.hasNext()) {
//                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                GENESIS_AgeBound ageBound = ite.next();
                BigDecimal dailyCount_BigDecimal = pop._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                long dailyCount_long = dailyCount_BigDecimal.toBigInteger().longValue();
                if (dailyCount_long > 1) {
                    // Create in migrants
                    for (long i = 0; i < dailyCount_long; i++) {
                        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
                        String subregionID = migration.getInternalMigrationSubregionDestinationFromRestOfUKFemale(
                                ageBound,
                                regionID,
                                decimalPlaces,
                                _RandomArray[14]);

//                        // debug code
//                        if (regionLivingFemaleIDs.get(subregionID) == null) {
//                            TreeSet<Long> subregionLivingFemaleIDs = new TreeSet<Long>();
//                            regionLivingFemaleIDs.put(subregionID, subregionLivingFemaleIDs);
//                        }

                        GENESIS_Female female = getNewInitialisedFemale(
                                ageBound,
                                null,
                                subregionID,
                                regionLivingFemaleIDs.get(subregionID),
                                regionNotPregnantFemaleIDs.get(subregionID));

                        // Account
                        GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(female.getAge().getAgeInYears(_GENESIS_Environment._Time));
                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(regionID)._FemaleAgeBoundPopulationCount_TreeMap,
                                yearAgeBound,
                                BigDecimal.ONE,
                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(subregionID)._FemaleAgeBoundPopulationCount_TreeMap,
                                yearAgeBound,
                                BigDecimal.ONE,
                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);

                        //females.add(female);
                        //regionLivingFemaleIDs.get(subregionID).add(female.get_Agent_ID(_HandleOutOfMemoryError));
                        result++;
                    }
//                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                            regionTotalImmigration,
//                            ageBound,
//                            BigDecimal.valueOf(dailyCount_long));
                }
                // for the remaining population add based on random
                //BigDecimal remainingDailyCount_BigDecimal = dailyCount_BigDecimal.subtract(new BigDecimal(dailyCount_BigDecimal.toBigInteger()));
                BigDecimal remainingDailyCount_BigDecimal = dailyCount_BigDecimal.remainder(BigDecimal.ONE);
                if (Generic_BigDecimal.randomUniformTest(_RandomArray[14],
                        remainingDailyCount_BigDecimal,
                        _GENESIS_Environment.MathContextForPopulationProbabilities)) {
                    String subregionID = migration.getInternalMigrationSubregionDestinationFromRestOfUKFemale(
                            ageBound,
                            regionID,
                            decimalPlaces,
                            _RandomArray[14]);

//                    // debug code
//                    if (regionLivingFemaleIDs.get(subregionID) == null) {
//                        TreeSet<Long> subregionLivingFemaleIDs = new TreeSet<Long>();
//                        regionLivingFemaleIDs.put(subregionID, subregionLivingFemaleIDs);
//                    }

                    TreeSet<Long> subregionLivingFemaleIDs =
                            regionLivingFemaleIDs.get(subregionID);
                    TreeSet<Long> subregionNotPregnantFemaleIDs =
                            regionLivingFemaleIDs.get(subregionID);
                    GENESIS_Female female = getNewInitialisedFemale(
                            ageBound,
                            null,
                            subregionID,
                            subregionLivingFemaleIDs,
                            subregionNotPregnantFemaleIDs);

                    // Account
                    GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(female.getAge().getAgeInYears(_GENESIS_Environment._Time));
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(regionID)._FemaleAgeBoundPopulationCount_TreeMap,
                            yearAgeBound,
                            BigDecimal.ONE,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(subregionID)._FemaleAgeBoundPopulationCount_TreeMap,
                            yearAgeBound,
                            BigDecimal.ONE,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);

                    //females.add(female);
                    //regionLivingFemaleIDs.get(subregionID).add(female.get_Agent_ID(_HandleOutOfMemoryError));
                    result++;
//                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                            regionTotalImmigration,
//                            ageBound,
//                            BigDecimal.ONE);
                }
            }
            ite = pop._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
            while (ite.hasNext()) {
                GENESIS_AgeBound ageBound = ite.next();
                BigDecimal dailyCount_BigDecimal = pop._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                long dailyCount_long = dailyCount_BigDecimal.toBigInteger().longValue();
                if (dailyCount_long > 1) {
                    // Create immigrants
                    for (long i = 0; i < dailyCount_long; i++) {
                        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
                        String subregionID = migration.getInternalMigrationSubregionDestinationFromRestOfUKMale(
                                ageBound,
                                regionID,
                                decimalPlaces,
                                _RandomArray[14]);

//                        // debug code
//                        if (regionLivingMaleIDs.get(subregionID) == null) {
//                            TreeSet<Long> subregionLivingMaleIDs = new TreeSet<Long>();
//                            regionLivingMaleIDs.put(subregionID, subregionLivingMaleIDs);
//                        }
                        TreeSet<Long> subregionLivingMaleIDs =
                                regionLivingMaleIDs.get(subregionID);
                        GENESIS_Male male = getNewInitialisedMale(
                                ageBound,
                                null,
                                subregionID,
                                subregionLivingMaleIDs);

                        // Account
                        GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(male.getAge().getAgeInYears(_GENESIS_Environment._Time));
                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(regionID)._MaleAgeBoundPopulationCount_TreeMap,
                                yearAgeBound,
                                BigDecimal.ONE,
                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(subregionID)._MaleAgeBoundPopulationCount_TreeMap,
                                yearAgeBound,
                                BigDecimal.ONE,
                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);

                        //males.add(male);
                        //regionLivingMaleIDs.get(subregionID).add(male.get_Agent_ID(_HandleOutOfMemoryError));
                        result++;
                    }
//                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                            regionTotalImmigration,
//                            ageBound,
//                            BigDecimal.valueOf(dailyCount_long));
                }
                // for the remaining population add based on random
                //BigDecimal remainingDailyCount_BigDecimal = dailyCount_BigDecimal.subtract(new BigDecimal(dailyCount_BigDecimal.toBigInteger()));
                BigDecimal remainingDailyCount_BigDecimal = dailyCount_BigDecimal.remainder(BigDecimal.ONE);
                if (Generic_BigDecimal.randomUniformTest(_RandomArray[14],
                        remainingDailyCount_BigDecimal,
                        _GENESIS_Environment.MathContextForPopulationProbabilities)) {
                    String subregionID = migration.getInternalMigrationSubregionDestinationFromRestOfUKMale(
                            ageBound,
                            regionID,
                            decimalPlaces,
                            _RandomArray[14]);

//                    // debug code
//                    if (regionLivingMaleIDs.get(subregionID) == null) {
//                        TreeSet<Long> subregionLivingMaleIDs = new TreeSet<Long>();
//                        regionLivingMaleIDs.put(subregionID, subregionLivingMaleIDs);
//                    }

                    GENESIS_Male male = getNewInitialisedMale(
                            ageBound,
                            null,
                            subregionID,
                            regionLivingMaleIDs.get(subregionID));

                    // Account
                    GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(male.getAge().getAgeInYears(_GENESIS_Environment._Time));
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(regionID)._MaleAgeBoundPopulationCount_TreeMap,
                            yearAgeBound,
                            BigDecimal.ONE,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(subregionID)._MaleAgeBoundPopulationCount_TreeMap,
                            yearAgeBound,
                            BigDecimal.ONE,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    //males.add(male);
                    //regionLivingMaleIDs.get(subregionID).add(male.get_Agent_ID(_HandleOutOfMemoryError));
                    result++;
//                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                            regionTotalImmigration,
//                            ageBound,
//                            BigDecimal.ONE);
                }
            }
        }
        System.out.println("</simulateInMigrationFromRestOfUK>");
        return result;
    }

    public long simulateImmigrationToStudyRegion(
            GENESIS_Migration migration,
            String regionID,
            TreeMap<String, TreeSet<Long>> regionLivingFemaleIDs,
            TreeMap<String, TreeSet<Long>> regionNotPregnantFemaleIDs,
            TreeMap<String, TreeSet<Long>> regionLivingMaleIDs) {
        long result = 0;
        int decimalPlaces = 10;
        //RoundingMode roundingMode = RoundingMode.HALF_UP;
        //HashSet<GENESIS_Female> females = new HashSet<GENESIS_Female>();
        //HashSet<GENESIS_Male> males = new HashSet<GENESIS_Male>();

        System.out.println("<simulateImmigrationToStudyRegion>");

        GENESIS_Population pop = migration.getDailyImmigrationRate(regionID);
        if (pop != null) {
            Iterator<GENESIS_AgeBound> ite;
            ite = pop._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
            while (ite.hasNext()) {
                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
                GENESIS_AgeBound ageBound = ite.next();
                BigDecimal dailyCount_BigDecimal = pop._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                long dailyCount_long = dailyCount_BigDecimal.toBigInteger().longValue();
                if (dailyCount_long > 1) {
                    _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
                    // Create immigrants
                    for (long i = 0; i < dailyCount_long; i++) {
                        // Assume same distribution for Immigration as Internal to UK migration!
                        String subregionID = migration.getInternalMigrationSubregionDestinationFromRestOfUKFemale(
                                ageBound,
                                regionID,
                                decimalPlaces,
                                _RandomArray[14]);

//                        // debug code
//                        if (regionLivingFemaleIDs.get(subregionID) == null) {
//                            TreeSet<Long> subregionLivingFemaleIDs = new TreeSet<Long>();
//                            regionLivingFemaleIDs.put(subregionID, subregionLivingFemaleIDs);
//                        }
//                        if (regionNotPregnantFemaleIDs.get(subregionID) == null) {
//                            TreeSet<Long> subregionNotPregnantFemaleIDs = new TreeSet<Long>();
//                            regionNotPregnantFemaleIDs.put(subregionID, subregionNotPregnantFemaleIDs);
//                        }

                        GENESIS_Female female = getNewInitialisedFemale(
                                ageBound,
                                null,
                                subregionID,
                                regionLivingFemaleIDs.get(subregionID),
                                regionNotPregnantFemaleIDs.get(subregionID));

                        // Account
                        GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(female.getAge().getAgeInYears(_GENESIS_Environment._Time));
                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(regionID)._FemaleAgeBoundPopulationCount_TreeMap,
                                yearAgeBound,
                                BigDecimal.ONE,
                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(subregionID)._FemaleAgeBoundPopulationCount_TreeMap,
                                yearAgeBound,
                                BigDecimal.ONE,
                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        //females.add(female);
                        //regionLivingFemaleIDs.get(subregionID).add(female.get_Agent_ID(_HandleOutOfMemoryError));
                    }
                    result++;
                }
                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
// for the remaining population add based on random
                //BigDecimal remainingDailyCount_BigDecimal = dailyCount_BigDecimal.subtract(new BigDecimal(dailyCount_BigDecimal.toBigInteger()));
                BigDecimal remainingDailyCount_BigDecimal = dailyCount_BigDecimal.remainder(BigDecimal.ONE);
                if (Generic_BigDecimal.randomUniformTest(_RandomArray[14],
                        remainingDailyCount_BigDecimal,
                        _GENESIS_Environment.MathContextForPopulationProbabilities)) {
                    String subregionID = migration.getInternalMigrationSubregionDestinationFromRestOfUKFemale(
                            ageBound,
                            regionID,
                            decimalPlaces,
                            _RandomArray[14]);

//                    // debug code
//                    if (regionLivingFemaleIDs.get(subregionID) == null) {
//                        TreeSet<Long> subregionLivingFemaleIDs = new TreeSet<Long>();
//                        regionLivingFemaleIDs.put(subregionID, subregionLivingFemaleIDs);
//                    }
//                    if (regionNotPregnantFemaleIDs.get(subregionID) == null) {
//                        TreeSet<Long> subregionNotPregnantFemaleIDs = new TreeSet<Long>();
//                        regionNotPregnantFemaleIDs.put(subregionID, subregionNotPregnantFemaleIDs);
//                    }

                    GENESIS_Female female = getNewInitialisedFemale(
                            ageBound,
                            null,
                            subregionID,
                            regionLivingFemaleIDs.get(subregionID),
                            regionNotPregnantFemaleIDs.get(subregionID));

                    // Account
                    GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(female.getAge().getAgeInYears(_GENESIS_Environment._Time));
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(regionID)._FemaleAgeBoundPopulationCount_TreeMap,
                            yearAgeBound,
                            BigDecimal.ONE,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(subregionID)._FemaleAgeBoundPopulationCount_TreeMap,
                            yearAgeBound,
                            BigDecimal.ONE,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    //females.add(female);
                    //regionLivingFemaleIDs.get(subregionID).add(female.get_Agent_ID(_HandleOutOfMemoryError));
                    result++;
                }
            }
            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
            ite = pop._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
            while (ite.hasNext()) {
                GENESIS_AgeBound ageBound = ite.next();
                BigDecimal dailyCount_BigDecimal = pop._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                long dailyCount_long = dailyCount_BigDecimal.toBigInteger().longValue();
                if (dailyCount_long > 1) {
                    _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
// Create immigrants
                    for (long i = 0; i < dailyCount_long; i++) {
                        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
// Assume same distribution for Immigration as Internal to UK migration!
                        String subregionID = migration.getInternalMigrationSubregionDestinationFromRestOfUKMale(
                                ageBound,
                                regionID,
                                decimalPlaces,
                                _RandomArray[14]);

//                        // debug code
//                        System.out.println("subregionID " + subregionID);

//                        if (regionLivingMaleIDs.get(subregionID) == null) {
//                            TreeSet<Long> subregionLivingMaleIDs = new TreeSet<Long>();
//                            regionLivingMaleIDs.put(subregionID, subregionLivingMaleIDs);
//                        }

                        GENESIS_Male male = getNewInitialisedMale(
                                ageBound,
                                null,
                                subregionID,
                                regionLivingMaleIDs.get(subregionID));

                        // Account
                        GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(male.getAge().getAgeInYears(_GENESIS_Environment._Time));
                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(regionID)._MaleAgeBoundPopulationCount_TreeMap,
                                yearAgeBound,
                                BigDecimal.ONE,
                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(subregionID)._MaleAgeBoundPopulationCount_TreeMap,
                                yearAgeBound,
                                BigDecimal.ONE,
                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        //males.add(male);
                        //regionLivingMaleIDs.get(subregionID).add(male.get_Agent_ID(_HandleOutOfMemoryError));
                        result++;
                    }
                }
                // for the remaining population add based on random
                //BigDecimal remainingDailyCount_BigDecimal = dailyCount_BigDecimal.subtract(new BigDecimal(dailyCount_BigDecimal.toBigInteger()));
                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(true);
                BigDecimal remainingDailyCount_BigDecimal = dailyCount_BigDecimal.remainder(BigDecimal.ONE);
                if (Generic_BigDecimal.randomUniformTest(_RandomArray[14],
                        remainingDailyCount_BigDecimal,
                        _GENESIS_Environment.MathContextForPopulationProbabilities)) {

//                    // debug code
//                    System.out.println("ageBound " + ageBound);
//                    System.out.println("regionID " + regionID);

                    // Assume same distribution for Immigration as Internal to UK migration!
                    String subregionID = migration.getInternalMigrationSubregionDestinationFromRestOfUKMale(
                            ageBound,
                            regionID,
                            decimalPlaces,
                            _RandomArray[14]);

//                    // debug code
//                    if (regionLivingMaleIDs.get(subregionID) == null) {
//                        TreeSet<Long> subregionLivingMaleIDs = new TreeSet<Long>();
//                        regionLivingMaleIDs.put(subregionID, subregionLivingMaleIDs);
//                    }

                    GENESIS_Male male = getNewInitialisedMale(
                            ageBound,
                            null,
                            subregionID,
                            regionLivingMaleIDs.get(subregionID));

                    // Account
                    GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(male.getAge().getAgeInYears(_GENESIS_Environment._Time));
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(regionID)._MaleAgeBoundPopulationCount_TreeMap,
                            yearAgeBound,
                            BigDecimal.ONE,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(_Demographics._Population.get(regionID).get(subregionID)._MaleAgeBoundPopulationCount_TreeMap,
                            yearAgeBound,
                            BigDecimal.ONE,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    //males.add(male);
                    //regionLivingMaleIDs.get(subregionID).add(male.get_Agent_ID(_HandleOutOfMemoryError));
                    result++;
                }
            }
        }
        System.out.println("</simulateImmigrationToStudyRegion>");
        return result;
    }

    /**
     * @param a_Female
     * @param a_Female_ID
     * @param a_FemaleCollection
     * @param pregnantFemaleIDs
     * @param age
     * @param nearlyDuePregnantFemaleIDs
     * @param notPregnantFemaleIDs
     * @return GENESIS_Person[] of babies if there are births and null otherwise
     */
    public Object[] simulateBirth(
            GENESIS_Female a_Female,
            long a_Female_ID,
            //AgeBound ageBound,
            GENESIS_Population localPopulation,
            TreeSet<Long> pregnantFemaleIDs,
            TreeSet<Long> nearlyDuePregnantFemaleIDs,
            TreeSet<Long> notPregnantFemaleIDs) {
        Object[] result = null;
        if (a_Female._Time_DueToGiveBirth.compareTo(_GENESIS_Environment._Time) == 0) {
            result = a_Female.giveBirthSimple(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
            Iterator<Long> ite;
            Long agent_ID;
            GENESIS_AgeBound ageBound0 = new GENESIS_AgeBound(0L);
            HashSet<Long> babyGirl_IDs = (HashSet<Long>) result[0];
            BigDecimal babyGirlCount = BigDecimal.valueOf(babyGirl_IDs.size());
// Accounting is now done later in the algorithm for computational efficiency
//            // Update populations
//            // Deal with local Population
//            GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                    localPopulation._FemaleAgeBoundPopulationCount_TreeMap,
//                    ageBound0,
//                    babyGirlCount);
//            // Deal with total Population
//            GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                    _Demographics._Total_Population._FemaleAgeBoundPopulationCount_TreeMap,
//                    ageBound0,
//                    babyGirlCount);
            // Deal with baby girls
            ite = babyGirl_IDs.iterator();
            while (ite.hasNext()) {
                agent_ID = ite.next();
                notPregnantFemaleIDs.add(agent_ID);
            }
            // Deal with baby boys
            HashSet<Long> babyBoy_IDs = (HashSet<Long>) result[1];
            BigDecimal babyBoyCount = BigDecimal.valueOf(babyBoy_IDs.size());
// Accounting is now done later in the algorithm for computational efficiency
//            // Update populations
//            // Deal with local Population
//            GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                    localPopulation._MaleAgeBoundPopulationCount_TreeMap,
//                    ageBound0,
//                    babyBoyCount);
//            // Deal with total Population
//            GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
//                    _Demographics._Total_Population._MaleAgeBoundPopulationCount_TreeMap,
//                    ageBound0,
//                    babyBoyCount);
            pregnantFemaleIDs.remove(a_Female_ID);
            nearlyDuePregnantFemaleIDs.remove(a_Female_ID);
            notPregnantFemaleIDs.add(a_Female_ID);
        }
        return result;
    }

    public void updateSubregionNearlyDuePregnantFemaleIDs(
            GENESIS_Female a_Female,
            long a_Female_ID,
            TreeSet<Long> nearlyDuePregantFemaleIDs_HashSet) {
        long differenceInDays = _GENESIS_Environment._Time.getDifferenceInDays_long(
                a_Female._Time_DueToGiveBirth);
        if (differenceInDays <= NearlyDueNumberOfDaysTillDueDate) {
            //String regionID = a_Female.GENESIS_Population_ID;
            //_NearlyDuePregnantFemaleIDs_HashMap.get(regionID).add(a_Female_ID);
            nearlyDuePregantFemaleIDs_HashSet.add(a_Female_ID);
        }
    }

    @Deprecated
    public void updateNearlyDuePregnantFemaleIDs() {
        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
        String type = GENESIS_Person.getTypeLivingFemale_String();
        Iterator<String> ite = _regionIDs.keySet().iterator();
        String regionID;
        while (ite.hasNext()) {
            regionID = ite.next();
            TreeMap<String, TreeSet<Long>> regionPregnantFemaleIDs;
            regionPregnantFemaleIDs = _PregnantFemaleIDs.get(regionID);
            if (regionPregnantFemaleIDs != null) {
                TreeMap<String, TreeSet<Long>> regionNearlyDuePregnantFemaleIDs;
                regionNearlyDuePregnantFemaleIDs = _NearlyDuePregnantFemaleIDs.get(regionID);
                Iterator<String> ite2 = regionPregnantFemaleIDs.keySet().iterator();
                while (ite2.hasNext()) {
                    String subregionID = ite2.next();
                    TreeSet<Long> subregionPregnantFemaleIDs;
                    subregionPregnantFemaleIDs = regionPregnantFemaleIDs.get(subregionID);
                    TreeSet<Long> subregionNearlyDuePregnantFemaleIDs;
                    subregionNearlyDuePregnantFemaleIDs = regionNearlyDuePregnantFemaleIDs.get(subregionID);
                    Iterator<Long> ite3 = subregionPregnantFemaleIDs.iterator();
                    while (ite3.hasNext()) {
                        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        long a_Agent_ID = (Long) ite3.next();
                        GENESIS_Female a_Female = _GENESIS_AgentCollectionManager.getFemale(a_Agent_ID,
                                type,
                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                        long differenceInDays = _GENESIS_Environment._Time.getDifferenceInDays_long(a_Female._Time_DueToGiveBirth);
                        //long differenceInDays = a_Female._Time_DueToGiveBirth.getDifferenceInDays_long(_GENESIS_Environment._Time);
                        if (differenceInDays <= NearlyDueNumberOfDaysTillDueDate) {
                            subregionNearlyDuePregnantFemaleIDs.add(a_Agent_ID);
                        }
                    }
                }
            }
        }
        //return nearlyDuePregnantFemale_ID_HashSet;
    }

    /**
     * @param fertility
     * @param a_Female
     * @param a_Female_ID
     * @param a_FemaleCollection
     * @param pregnantFemaleIDs
     * @param age
     * @param notPregnantFemaleIDs
     * @return true if pregnancy occurs
     */
    public boolean simulatePregnancy(
            GENESIS_Fertility fertility,
            GENESIS_Female a_Female,
            long a_Female_ID,
            TreeSet<Long> pregnantFemaleIDs,
            TreeSet<Long> notPregnantFemaleIDs) {
        boolean result = false;
        BigDecimal pregnancyProbability;
        pregnancyProbability = fertility.getDailyPregnancyRate(a_Female);
        if (pregnancyProbability != null) {
            if (pregnancyProbability.compareTo(BigDecimal.ZERO) == 1) {
                if (Generic_BigDecimal.randomUniformTest(_RandomArray[6],
                        pregnancyProbability,
                        //_GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                        _GENESIS_Environment.RoundingModeForPopulationProbabilities)) {
                    a_Female.set_Pregnant(fertility,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    //a_Female.set_UnbornsFather(_Father);
                    //_PregnantFemales.add(a_Female);
                    pregnantFemaleIDs.add(a_Female_ID);
                    notPregnantFemaleIDs.remove(a_Female_ID);
                    result = true;

//                    // debug
//                    if (a_Female.get_Agent_ID(false) == 58061) {
//                        int debug = 1;
//                        System.out.println(a_Female);
//                    }

                }
            }
        }
        return result;
    }

    /**
     * This should only get called if a_Female is known to be pregnant. If a
     * miscarriage is simulated then the method returns true having first:
     * removed a_Female_ID from _PregnantFemaleIDs and
     * _NearlyDuePregnantFemaleIDs (should it also have been in there); and
     * added a_Female_ID to _NotPregnantFemaleIDs.
     *
     * @param a_Female
     * @param a_Female_ID
     * @param ageBound
     * @param notPregantFemaleIDs
     * @param pregnantFemaleIDs
     * @param nearlyDuePregantFemaleIDs
     * @return
     */
    public boolean simulateMiscarriage(
            String regionID,
            GENESIS_Female a_Female,
            long a_Female_ID,
            GENESIS_AgeBound ageBound,
            TreeSet<Long> pregnantFemaleIDs,
            TreeSet<Long> nearlyDuePregantFemaleIDs,
            TreeSet<Long> notPregantFemaleIDs) {
        boolean result = simulateMiscarriageNoRemoval(
                regionID,
                a_Female,
                a_Female_ID,
                ageBound);
        if (result) {

//            // debug
//            if (a_Female.get_Agent_ID(false) == 58061) {
//                int debug = 1;
//                System.out.println("Miscarriage");
//                System.out.println(a_Female);
//            }

            pregnantFemaleIDs.remove(a_Female_ID);
            nearlyDuePregantFemaleIDs.remove(a_Female_ID);
            notPregantFemaleIDs.add(a_Female_ID);
        }
        return result;
    }

    /**
     * This should only get called if a_Female is known to be pregnant.
     *
     * @param regionID
     * @param a_Female
     * @param a_Female_ID
     * @param ageBound
     * @return
     */
    public boolean simulateMiscarriageNoRemoval(
            String regionID,
            GENESIS_Female a_Female,
            long a_Female_ID,
            GENESIS_AgeBound ageBound) {
        BigDecimal miscarriageProbability;
        long differenceInDays_long =
                _GENESIS_Environment._Time.getDifferenceInDays_long(
                a_Female._Time_DueToGiveBirth);
        GENESIS_Miscarriage miscarriage = _Demographics._Miscarriage.get(regionID).get(regionID);
        if (differenceInDays_long < GENESIS_Miscarriage._NumberOfDaysExpectedInPregnancyStageLate_int) {
            miscarriageProbability =
                    miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap.get(
                    ageBound);
            // If miscarriageProbability is null then there is no miscarriage probability for this age
        } else {
            miscarriageProbability =
                    miscarriage._DailyEarlyPregnancyLossAgeBoundProbability_TreeMap.get(
                    ageBound);
            // If miscarriageProbability is null then there is no miscarriage probability for this age
        }
//        if (miscarriageProbability == null) {
//            int debug = 1;
//        }
        // RandomUniformTest
        if (Generic_BigDecimal.randomUniformTest(_RandomArray[7],
                miscarriageProbability,
                //_GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                _GENESIS_Environment.RoundingModeForPopulationProbabilities)) {
            a_Female.miscarriage();
            return true;
        } else {
            return false;
        }
    }

    /**
     * If at the start of the simulations there is no data about pregnancies
     * this method can be used to initialise pregnancies so that due dates are
     * spread out and in the first tick of a simulation then births may be due.
     * @return 
     */
    public int initialisePregnancies() {
        _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
        log(Level.FINE, "<initialisePregnancies>");
        String type = GENESIS_Person.getTypeLivingFemale_String();
        int totalPregnancies = 0;
        Iterator<String> ite = _regionIDs.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            int regionPregnancies = 0;
            System.out.println("Initialise pregnancies for region " + regionID);
            /**
             * The pregnancyProbabilities keys are age in years. The values are
             * TreeMap<Integer, BigDecimal> where: keys are the number of days
             * into term of pregnancy; and, values are the probabilities that a
             * female of a given age (will be however many days - as indicated
             * by the key) pregnant. In the TreeMap&lt;Integer, BigDecimal&gt of
             * values: A key of -1 is used to store the probability that a
             * female of the age is pregnant. A key of -2 is used to store the
             * probability that a female of the age is pregnant in early stage.
             * A key of -3 is used to store the probability that a female of the
             * age is pregnant in late stage. A key of -4 is used to store the
             * probability that a female of the age is not pregnant
             */
//            HashMap<Integer, HashMap<Integer, BigDecimal>> pregnancyProbabilities
//            pregnancyProbabilities = _Demographics._Fertility._PregnancyAgeBoundRateProbabilities_HashMap;
            // The pregnancyProbabilities can be worked out on a more daily basis.
            // Most women that give birth have a birthday after conceiving and 
            // before giving birth. So for instance, there is sometimes a non-zero 
            // proabblity od say a 14 year old getting pregnant as there are 15 year 
            // olds in the data which give birth. If there are no 14 year olds that 
            // give birth, then any 14 year old getting pregnant should have a 
            // birthday before they are due to give birth otherwise the model may 
            // simulate 14 year olds giving birth. Whether such control is needed 
            // in the model, is open to debate...
            HashMap<GENESIS_AgeBound, TreeMap<Integer, BigDecimal>> pregnancyProbabilities;
            pregnancyProbabilities = _Demographics._Fertility.get(regionID).get(regionID)._PregnancyAgeBoundRateProbabilities_HashMap;
            //HashMap<Integer,BigDecimal> dailyFertility = _Fertility.get_Age_Fertitility_Female();
            int earlyStagePregnancies = 0;
            int lateStagePregnancies = 0;
            GENESIS_Female a_Female;
            //Long age;

            // Calculate the proportions of the population that are not pregnant as 
            // well as those that are and in each stage of pregnancy

            // Calculate cumulative probabilities for setting the day of pregnancy
            GENESIS_AgeBound ageBound;
            Integer day;
            BigDecimal probability;
            BigDecimal populationProportion;
            BigDecimal cumulativePopulationProportion;
            //TreeMap<BigDecimal, Integer> probabilityDay;
            TreeMap<Integer, BigDecimal> dayProbability;
            // ageCumulativePregnancyProportions stored for each age as a key, a 
            // TreeMap which for each day of term gives the proportion of population
            // for a day of pregnancy that are expected to be at this stage of 
            // pregnancy
            HashMap<GENESIS_AgeBound, TreeMap<BigDecimal, Integer>> ageCumulativePregnancyProportions =
                    new HashMap<GENESIS_AgeBound, TreeMap<BigDecimal, Integer>>();
            //for (Entry<GENESIS_AgeBound, TreeMap<Integer, BigDecimal>> entry : pregnancyProbabilities.entrySet()) {
            Entry<GENESIS_AgeBound, TreeMap<Integer, BigDecimal>> entry;
            Iterator<Entry<GENESIS_AgeBound, TreeMap<Integer, BigDecimal>>> ite2 = pregnancyProbabilities.entrySet().iterator();
            while (ite2.hasNext()) {
                entry = ite2.next();
                cumulativePopulationProportion = BigDecimal.ZERO;
                ageBound = entry.getKey();
                //<Debug>
                //System.out.println(ageBound.toString());
                //</Debug>
                dayProbability = entry.getValue();

                BigDecimal femalePopulation = BigDecimal.ONE;
//            BigDecimal femalePopulation = _Demographics._Total_GENESIS_Population._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
//            if (femalePopulation != null) {
                TreeMap<BigDecimal, Integer> cumulativePopulationProportions = new TreeMap<BigDecimal, Integer>();
                for (Entry<Integer, BigDecimal> entry2 : dayProbability.entrySet()) {
                    day = entry2.getKey();
                    if (day.intValue() > -1) {
                        //System.out.println(day); // To check these are in the correct order
                        probability = entry2.getValue();
                        populationProportion = femalePopulation.multiply(probability);
                        cumulativePopulationProportion = cumulativePopulationProportion.add(populationProportion);
                        cumulativePopulationProportions.put(cumulativePopulationProportion, day);
                    }
                }
//            BigDecimal populationProportionNotPregnant = femalePopulation.multiplyNoUpdate(dayProbability.get(-4));
//            cumulativePopulationProportion = cumulativePopulationProportion.add(populationProportionNotPregnant);
//            cumulativePopulationProportions.put(cumulativePopulationProportion, -1);
                ageCumulativePregnancyProportions.put(ageBound, cumulativePopulationProportions);
                // The cumulativePopulationProportion now gives the proportion of 
                // femalePopulation that are likely to be pregnant on any day.
                // Thus, the proportion that are likely not to be pregnant on any 
                // can be calculated. This is not done here, but in the next step 
                // this logic is used for setting pregnancies.
//            System.out.println(
//                    "femaleAgePopulation " + age + ", " + femalePopulation
//                    + "; cumulativePopulationProportion " + cumulativePopulationProportion);
//                log(Level.FINEST,
//                        "female AgeBound " + ageBound + ", Population" + femalePopulation
//                        + "; cumulativePopulationProportion " + cumulativePopulationProportion);
//            }
            }
            TreeMap<Integer, BigDecimal> ageSpecificPregnancyProbability_TreeMap;
            HashSet<Long> newlyConceived = new HashSet<Long>();

            // Loop for each local area
            //TreeSet<String> subregionIDs = _regionIDs.get(regionID);
            //Iterator<String> ite3;
            //ite3 = subregionIDs.iterator();
            //while (ite3.hasNext()) {
            //    String subregionID = ite3.next();
            TreeMap<String, TreeSet<Long>> regionNearlyDuePregnantFemaleIDs = _NearlyDuePregnantFemaleIDs.get(regionID);
            if (regionNearlyDuePregnantFemaleIDs == null) {
                regionNearlyDuePregnantFemaleIDs = new TreeMap<String, TreeSet<Long>>();
                _NearlyDuePregnantFemaleIDs.put(regionID, regionNearlyDuePregnantFemaleIDs);
            }
            TreeMap<String, TreeSet<Long>> regionPregnantFemaleIDs = _PregnantFemaleIDs.get(regionID);
            if (regionPregnantFemaleIDs == null) {
                regionPregnantFemaleIDs = new TreeMap<String, TreeSet<Long>>();
                _PregnantFemaleIDs.put(regionID, regionPregnantFemaleIDs);
            }
            TreeMap<String, TreeSet<Long>> regionNotPregnantFemaleIDs = _NotPregnantFemaleIDs.get(regionID);
            if (regionNotPregnantFemaleIDs == null) {
                regionNotPregnantFemaleIDs = new TreeMap<String, TreeSet<Long>>();
                _NotPregnantFemaleIDs.put(regionID, regionNotPregnantFemaleIDs);
            }
            Iterator<String> ite4 = regionNotPregnantFemaleIDs.keySet().iterator();
            while (ite4.hasNext()) {
                String subregionID = ite4.next();
                newlyConceived.clear();
                TreeSet<Long> subregionNotPregnantFemaleIDs = regionNotPregnantFemaleIDs.get(subregionID);
                if (subregionNotPregnantFemaleIDs == null) {
                    subregionNotPregnantFemaleIDs = new TreeSet<Long>();
                    regionNotPregnantFemaleIDs.put(
                            subregionID, subregionNotPregnantFemaleIDs);
                }
                TreeSet<Long> subregionPregnantFemaleIDs = regionPregnantFemaleIDs.get(subregionID);
                if (subregionPregnantFemaleIDs == null) {
                    subregionPregnantFemaleIDs = new TreeSet<Long>();
                    regionPregnantFemaleIDs.put(
                            subregionID, subregionPregnantFemaleIDs);
                }
//                TreeSet<Long> subregionNearlyDuePregnantFemaleIDs
//                        = regionNearlyDuePregnantFemaleIDs.get(subregionID);
//                if (subregionNearlyDuePregnantFemaleIDs == null) {
//                    subregionNearlyDuePregnantFemaleIDs = new TreeSet<Long>();
//                    regionNearlyDuePregnantFemaleIDs.put(
//                            subregionID, subregionNearlyDuePregnantFemaleIDs);
//                }
                Iterator<Long> ite5 = subregionNotPregnantFemaleIDs.iterator();
                // Loop over notPregnant population (which is all to begin with)
                // Set them to be pregnant based on probabilities
                while (ite5.hasNext()) {
                    _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(_GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    Long a_Agent_ID = ite5.next();

//                    // debug
//                    if (a_Agent_ID == 145256) {
//                        int debug = 1;
//                    }


                    a_Female = _GENESIS_AgentCollectionManager.getFemale(a_Agent_ID,
                            type,
                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                    GENESIS_Age age = a_Female.getAge();
                    GENESIS_Time age_Time = new GENESIS_Time(age.getAge_Time(_GENESIS_Environment._Time));
                    age_Time.addDays(GENESIS_Female.NormalGestationPeriod_int);
                    long ageInYearsAtDueDate = age_Time.getYear();
                    ageBound = new GENESIS_AgeBound(ageInYearsAtDueDate);
////                return getDailyPregnancyRate(ageBound);
//                double age_double = age.getAge_double(_GENESIS_Environment._Time);
////                long ageInYears = age.getAgeInYears(_GENESIS_Environment._Time);
////                long ageInDays = age.getAgeInDays(_GENESIS_Environment._Time);
//                age_double += ((double) GENESIS_Female.NormalGestationPeriod_int) / ((double) GENESIS_Time.NormalDaysInYear_int);
//                long age_long = (long) age_double;
//                ageBound = new GENESIS_AgeBound(age_long);
                    ageSpecificPregnancyProbability_TreeMap = pregnancyProbabilities.get(ageBound);
                    if (ageSpecificPregnancyProbability_TreeMap != null) {
                        BigDecimal pregnancyProbability = ageSpecificPregnancyProbability_TreeMap.get(-1);
                        TreeMap<BigDecimal, Integer> cumulativePopulationProportions = ageCumulativePregnancyProportions.get(ageBound);

//                    // Debugging code
//                    if (cumulativePopulationProportions == null) {
//                        int debug = 1;
//                    }
//                    if (cumulativePopulationProportions.isEmpty()) {
//                        int debug = 1;
//                    }

                        if (pregnancyProbability.compareTo(BigDecimal.ZERO) == 1) {
                            if (Generic_BigDecimal.randomUniformTest(_RandomArray[8],
                                    pregnancyProbability,
                                    //_GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                                    _GENESIS_Environment.RoundingModeForPopulationProbabilities)) {

                                // Get random value between 0 and cumulativePopulationProportions.lastKey()
                                BigDecimal val = Generic_BigDecimal.getRandom(_GENESIS_Environment._Generic_BigDecimal._Generic_BigInteger,
                                        _RandomArray[9],
                                        _GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                                        //cumulativeProbabilities.firstKey(),
                                        BigDecimal.ZERO,
                                        cumulativePopulationProportions.lastKey());
//                        // Get day for this val
                                Entry<BigDecimal, Integer> cumulativePopulationProportionEntry =
                                        cumulativePopulationProportions.ceilingEntry(val);
                                // if cumulativePopulationProportionEntry.getValue() is -1 then 
                                // the female is not pregnant
                                if (cumulativePopulationProportionEntry.getValue() != -1) {
                                    int numberOfDaysUntilDue = GENESIS_Female.NormalGestationPeriod_int - cumulativePopulationProportionEntry.getValue();
                                    if (numberOfDaysUntilDue < _Demographics._Miscarriage.get(regionID).get(regionID).getNumberOfDaysInLatePregnancy_double()) {
                                        //earlyStagePregnancies++;
                                        lateStagePregnancies++;
                                    } else {
                                        earlyStagePregnancies++;
                                        //lateStagePregnancies++;
                                    }
                                    log(Level.FINE, "<dayOfPregnancy>");
                                    log(Level.FINE, "" + numberOfDaysUntilDue);
                                    log(Level.FINE, "</dayOfPregnancy>");
                                    boolean isPregnant = a_Female.set_Pregnant(_Demographics._Fertility.get(regionID).get(regionID),
                                            numberOfDaysUntilDue,
                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
                                    if (isPregnant) {
                                        //a_Female.set_UnbornsFather(_Father);

//                                        // debug
//                                        if (a_Agent_ID == 145256) {
//                                            int debug = 1;
//                                        }

                                        subregionPregnantFemaleIDs = regionPregnantFemaleIDs.get(subregionID);
                                        if (subregionPregnantFemaleIDs == null) {
                                            subregionPregnantFemaleIDs = new TreeSet<Long>();
                                            regionPregnantFemaleIDs.put(subregionID, subregionPregnantFemaleIDs);
                                        }
                                        subregionPregnantFemaleIDs.add(a_Agent_ID);
                                        newlyConceived.add(a_Agent_ID);
                                        if (numberOfDaysUntilDue <= NearlyDueNumberOfDaysTillDueDate) {
                                            TreeSet<Long> subregionNearlyDuePregnantFemaleIDs = regionNearlyDuePregnantFemaleIDs.get(subregionID);
                                            if (subregionNearlyDuePregnantFemaleIDs == null) {
                                                subregionNearlyDuePregnantFemaleIDs = new TreeSet<Long>();
                                                regionNearlyDuePregnantFemaleIDs.put(subregionID, subregionNearlyDuePregnantFemaleIDs);
                                            }
                                            subregionNearlyDuePregnantFemaleIDs.add(a_Agent_ID);
                                        }
                                        totalPregnancies++;
                                        regionPregnancies++;
                                        //log(Level.FINE,"Pregnancy");
                                    }
                                }
                            }
                        }
                    }
                }
                subregionNotPregnantFemaleIDs.removeAll(newlyConceived);
            }
            //updateNearlyDuePregnantFemaleIDs();
            //}
            System.out.println("Initialised pregnancies " + regionPregnancies);
            System.out.println("Early stage pregnancies " + earlyStagePregnancies);
            System.out.println("Late stage pregnancies " + lateStagePregnancies);
        }
        System.out.println("Initialised pregnancies " + totalPregnancies);
        log(Level.FINE, "<initialisePregnancies>");
        return totalPregnancies;
    }

    /**
     * CASDataHandler aCASDataHandler = new CASDataHandler(); theOAtoMSOALookup
     * = aCASDataHandler.get_LookUpMSOAfromOAHashMap();
     * Generic_StaticIO.writeObject(theOAtoMSOALookup,inputFile)
     * @param fileSeparator
     */
    public void initLookUpMSOAfromOAHashMap(String fileSeparator) {
        File inputFile = new File(
                _Directory.getParent() + fileSeparator
                + "InputData" + fileSeparator
                + "LUTs" + fileSeparator
                + "LookUpMSOAfromOATreeMap.thisFile");
        if (!inputFile.exists()) {
            GENESIS_Population.writeLUTsForOAToMSOATreeMap();
        }
        theOAtoMSOALookup = (TreeMap<String, String>) Generic_StaticIO.readObject(inputFile);
        theMSOAAreaCodes_TreeSet = new TreeSet<String>();
        Iterator<String> ite = _regionIDs.keySet().iterator();
        String aOA_AreaCode;
        String aMSOA_AreaCode;
        while (ite.hasNext()) {
            String regionID = ite.next();
            TreeSet<String> subregionIDs = _regionIDs.get(regionID);
            Iterator<String> ite2 = subregionIDs.iterator();
            while (ite2.hasNext()) {
                aOA_AreaCode = ite2.next();
                aMSOA_AreaCode = theOAtoMSOALookup.get(aOA_AreaCode);
                if (aMSOA_AreaCode == null) {
                    int DEBUG = 1;
                } else {
                    theMSOAAreaCodes_TreeSet.add(aMSOA_AreaCode);
                }
            }
        }
        System.out.println("theOAtoMSOALookup.size() " + theOAtoMSOALookup.size());
    }
}
