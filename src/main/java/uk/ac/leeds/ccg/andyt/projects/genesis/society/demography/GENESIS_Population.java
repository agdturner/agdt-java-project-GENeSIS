package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.andyt.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.math.Generic_BigInteger;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.XMLConverter;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.CommonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.PopulationFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBoundPopulation;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.GenderedAgeBoundPopulation;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.Time;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.population.PopulationType;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Collections;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.projects.genesis.visualisation.GENESIS_AgeGenderBarChart;
import uk.ac.leeds.ccg.andyt.census.core.Census_AbstractDataRecord;
import uk.ac.leeds.ccg.andyt.census.cas.Census_CAS001DataHandler;
import uk.ac.leeds.ccg.andyt.census.cas.Census_CAS001DataRecord;
import uk.ac.leeds.ccg.andyt.census.core.Census_CASDataHandler;

/**
 * A class for storing population count statistics. The statistics are stored in
 * two ways. They are stored as a TreeMaps, one for female counts, another for
 * male counts where the keys are GENESIS_AgeBound instances and values are
 * BigDecimal instances. It is designed that they are also read into and
 * exported from the <code>super.genderedAgeBoundPopulation</code> which
 * contains Lists of the data. In general the data is manipulated and altered
 * via the TreeMaps. Instantiated objects can be serialised for swapping, but in
 * general, before exporting data to a XML file, the
 * <code>super.genderedAgeBoundPopulation</code> are updated from the TreeMaps.
 */
public class GENESIS_Population extends PopulationType implements Serializable {

    static final long serialVersionUID = 1L;
    /**
     * Used for Logging
     */
    private static final String sourceClass = GENESIS_Population.class.getName();
    private static final String sourcePackage = GENESIS_Population.class.getPackage().getName();
    public transient GENESIS_Environment ge;
    /**
     * Female GENESIS_Population Key Integer are Age in Years Value Long are
     * counts, this could be the number of people of an ageBound at a given time
     * or an aggregate over a period (e.g. number of person years or number of
     * person days)
     *
     * @TODO Rename and rewrite documentation.
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _FemaleAgeBoundPopulationCount_TreeMap;
    /**
     * Male GENESIS_Population Key Integer are Age in Years Value Long are
     * counts, this could be the number of people of an ageBound at a given time
     * or an aggregate over a period (e.g. number of person years or number of
     * person days)
     *
     * @TODO Rename and rewrite documentation.
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _MaleAgeBoundPopulationCount_TreeMap;

    public GENESIS_Population() {
        String sourceMethod = "GENESIS_Population()";
        Logger.getLogger(sourcePackage).entering(sourceClass, sourceMethod);
        PopulationFactory.init();
        updateGenderAgeBoundPopulation_TreeMaps();
        Logger.getLogger(sourcePackage).exiting(sourceClass, sourceMethod);
    }

    public GENESIS_Population(GENESIS_Population a_Population) {
        this(a_Population.ge,
                a_Population);
    }

    public GENESIS_Population(
            GENESIS_Environment a_GENESIS_Environment,
            PopulationType a_PopulationType) {
        String sourceMethod = "GENESIS_Population(GENESIS_Environment,PopulationType)";
        Logger.getLogger(sourcePackage).entering(sourceClass, sourceMethod);
        ge = a_GENESIS_Environment;
        PopulationFactory.init();
        PopulationType newPop = GENESIS_Collections.deepCopy(a_PopulationType);
        this.genderedAgeBoundPopulation = newPop.getGenderedAgeBoundPopulation();
        updateGenderAgeBoundPopulation_TreeMaps();
        Logger.getLogger(sourcePackage).exiting(sourceClass, sourceMethod);
    }

    /**
     * By default the population multiplicand is equal to one.
     *
     * @param a_GENESIS_Environment
     * @param population_File
     */
    public GENESIS_Population(
            GENESIS_Environment a_GENESIS_Environment,
            File population_File) {
        this(a_GENESIS_Environment,
                population_File,
                BigDecimal.ONE);
    }

    /**
     * By default assume that the data is supplied in single years of ageBound.
     *
     * @param a_GENESIS_Environment
     * @param population_File
     * @param multiplicand A number greater than zero used to scale up or down
     * the population as read from population_File
     */
    public GENESIS_Population(
            GENESIS_Environment a_GENESIS_Environment,
            File population_File,
            BigDecimal multiplicand) {
        this(a_GENESIS_Environment,
                population_File,
                multiplicand,
                true);
    }

    /**
     * By default an even spread of population within ageBound ranges is
     * assumed. An even spread of population within any ageBound ranges is
     * assumed.
     *
     * @param a_GENESIS_Environment
     * @param file
     * @param multiplicand A number greater than zero used to scale up or down
     * the population as read from population_File
     * @param singleYearsOfAge
     */
    public GENESIS_Population(
            GENESIS_Environment a_GENESIS_Environment,
            File file,
            BigDecimal multiplicand,
            boolean singleYearsOfAge) {
        String sourceMethod = "GENESIS_Population(GENESIS_Environment,File,BigDecimal)";
        Logger.getLogger(sourcePackage).entering(sourceClass, sourceMethod);
        PopulationFactory.init();
        this.genderedAgeBoundPopulation = CommonFactory.newGenderedAgeBoundPopulation();
        ge = a_GENESIS_Environment;
        if (file.getName().endsWith("csv")) {
            BufferedReader br = null;
            try {
                br = Generic_IO.getBufferedReader(file);
                StreamTokenizer aStreamTokenizer = new StreamTokenizer(br);
                Generic_IO.setStreamTokenizerSyntax1(aStreamTokenizer);
                String line;
                int gender = 0;
                Long minimumAgeInYears = 0L;
                Long maximumAgeInYears = 0L;
//                BigInteger ageInYearsRange;
                BigDecimal population = null;
                // Skip the first line
                int tokenType = aStreamTokenizer.nextToken();
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                tokenType = aStreamTokenizer.nextToken();
                while (tokenType != StreamTokenizer.TT_EOF) {
                    switch (tokenType) {
                        case StreamTokenizer.TT_EOL:
//                            initPop(gender, minimumAgeInYears, maximumAgeInYears, population, populationPerYear);
                            AgeBoundPopulation ageBoundPopulation = CommonFactory.newAgeBoundPopulation();
                            AgeBound ageBound = new GENESIS_AgeBound(
                                    minimumAgeInYears,
                                    maximumAgeInYears);
                            ageBoundPopulation.setAgeBound(ageBound);
                            ageBoundPopulation.setPopulation(population);
                            if (gender == 0) {
                                this.genderedAgeBoundPopulation.getFemale().add(ageBoundPopulation);
                            } else {
                                this.genderedAgeBoundPopulation.getMale().add(ageBoundPopulation);
                            }
                            break;
                        case StreamTokenizer.TT_WORD:
                            line = aStreamTokenizer.sval;
                            String[] splitline = line.split(",");
                            gender = new Integer(splitline[0]);
                            minimumAgeInYears = new Long(splitline[1]);
                            if (splitline.length < 4) {
                                maximumAgeInYears = minimumAgeInYears;
                                population = new BigDecimal(splitline[2]).multiply(multiplicand);
//                                populationPerYear = population;
                            } else {
                                maximumAgeInYears = new Long(splitline[2]);
                                population = new BigDecimal(splitline[3]).multiply(multiplicand);
//                                ageInYearsRange = new BigInteger("" + (maximumAgeInYears - minimumAgeInYears + 1));
//                                populationPerYear = Generic_BigDecimal.divideRoundIfNecessary(
//                                        population,
//                                        ageInYearsRange,
//                                        0,
//                                        RoundingMode.DOWN);
                            }
                            log(Level.FINE, line);
                            log(Level.FINE,
                                    gender + ","
                                    + minimumAgeInYears + ","
                                    + maximumAgeInYears + ","
                                    + population);
                            break;
                    }
                    tokenType = aStreamTokenizer.nextToken();
                }
                updateGenderAgeBoundPopulation_TreeMaps();
            } catch (IOException e) {
                Logger.getLogger(GENESIS_Log.class.getName()).log(Level.SEVERE, null, e);
                e.printStackTrace(System.err);
                System.err.println(
                        "System.exit("
                        + GENESIS_ErrorAndExceptionHandler.IOException
                        + ") from " + sourceMethod + " reading CSV");
                System.exit(Generic_ErrorAndExceptionHandler.IOException);
            } finally {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(GENESIS_Fertility.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            if (!file.getName().endsWith("xml")) {
                System.err.println(
                        "System.exit("
                        + GENESIS_ErrorAndExceptionHandler.IOException
                        + ") from " + sourceMethod + " !population_File.getName().endsWith(xml)");
                System.exit(Generic_ErrorAndExceptionHandler.IOException);
            }
            PopulationType population
                    = XMLConverter.loadPopulationFromXMLFile(file);
            if (population == null) {
                log(Level.WARNING, "Population not created in " + sourceClass + "." + sourceMethod);
            }
            if (multiplicand.compareTo(BigDecimal.ONE) != 0) {
                //PopulationType pop = GENESIS_Collections.deepCopyTo_TreeMap_String_Population(population);
                Iterator<AgeBoundPopulation> ite;
                if (true) {
                    List<AgeBoundPopulation> females = getGenderedAgeBoundPopulation().getFemale();
                    ite = females.iterator();
                    while (ite.hasNext()) {
                        AgeBoundPopulation agePop = ite.next();
                        agePop.setPopulation(agePop.getPopulation().multiply(multiplicand));
                    }
                }
                if (true) {
                    List<AgeBoundPopulation> males = getGenderedAgeBoundPopulation().getMale();
                    ite = males.iterator();
                    while (ite.hasNext()) {
                        AgeBoundPopulation agePop = ite.next();
                        agePop.setPopulation(agePop.getPopulation().multiply(multiplicand));
                    }
                }
            }
            if (singleYearsOfAge) {
                _FemaleAgeBoundPopulationCount_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
                _MaleAgeBoundPopulationCount_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
                Iterator<AgeBoundPopulation> ite;
                if (true) {
                    List<AgeBoundPopulation> females = population.getGenderedAgeBoundPopulation().getFemale();
                    if (females != null) {
                        ite = females.iterator();
                        while (ite.hasNext()) {
                            AgeBoundPopulation ageBoundPopulation = ite.next();
                            _FemaleAgeBoundPopulationCount_TreeMap.put(
                                    new GENESIS_AgeBound(ageBoundPopulation.getAgeBound()),
                                    ageBoundPopulation.getPopulation());
                        }
                    }
                }
                if (true) {
                    List<AgeBoundPopulation> males = population.getGenderedAgeBoundPopulation().getMale();
                    if (males != null) {
                        ite = males.iterator();
                        while (ite.hasNext()) {
                            AgeBoundPopulation ageBoundPopulation = ite.next();
                            _MaleAgeBoundPopulationCount_TreeMap.put(
                                    new GENESIS_AgeBound(ageBoundPopulation.getAgeBound()),
                                    ageBoundPopulation.getPopulation());
                        }
                    }
                }
            } else {
                _FemaleAgeBoundPopulationCount_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
                _MaleAgeBoundPopulationCount_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
                Iterator<AgeBoundPopulation> ite;
                if (true) {
                    List<AgeBoundPopulation> females = population.getGenderedAgeBoundPopulation().getFemale();
                    if (females != null) {
                        ite = females.iterator();
                        while (ite.hasNext()) {
                            AgeBoundPopulation ageBoundPopulation = ite.next();
                            BigDecimal population_BigDecimal = ageBoundPopulation.getPopulation();
                            BigDecimal remainingPopulation = new BigDecimal(population_BigDecimal.toString());
                            AgeBound ageBound = ageBoundPopulation.getAgeBound();
                            long ageMin = ageBound.getAgeMin().getYear();
                            long ageMax = ageBound.getAgeMax().getYear();
                            for (long age = ageMin; age < ageMax; age++) {
                                GENESIS_AgeBound aAgeBound = new GENESIS_AgeBound(ageMin + age);
                                BigDecimal partPopulation_BigDecimal = Generic_BigDecimal.getRandom(ge._Generic_BigDecimal._Generic_BigInteger,
                                        0,
                                        BigDecimal.ZERO,
                                        remainingPopulation);
                                remainingPopulation = population_BigDecimal.subtract(partPopulation_BigDecimal);
                                _FemaleAgeBoundPopulationCount_TreeMap.put(
                                        aAgeBound,
                                        partPopulation_BigDecimal);
                            }
                            GENESIS_AgeBound aAgeBound = new GENESIS_AgeBound(ageMax);
                            _FemaleAgeBoundPopulationCount_TreeMap.put(
                                    aAgeBound,
                                    remainingPopulation);
                        }
                    }
                }
                if (true) {
                    List<AgeBoundPopulation> males = population.getGenderedAgeBoundPopulation().getMale();
                    if (males != null) {
                        ite = males.iterator();
                        while (ite.hasNext()) {
                            AgeBoundPopulation ageBoundPopulation = ite.next();
                            BigDecimal population_BigDecimal = ageBoundPopulation.getPopulation();
                            BigDecimal remainingPopulation = new BigDecimal(population_BigDecimal.toString());
                            AgeBound ageBound = ageBoundPopulation.getAgeBound();
                            long ageMin = ageBound.getAgeMin().getYear();
                            long ageMax = ageBound.getAgeMax().getYear();
                            for (long age = ageMin; age < ageMax; age++) {
                                GENESIS_AgeBound aAgeBound = new GENESIS_AgeBound(ageMin + age);
                                BigDecimal partPopulation_BigDecimal = Generic_BigDecimal.getRandom(ge._Generic_BigDecimal._Generic_BigInteger,
                                        0,
                                        BigDecimal.ZERO,
                                        remainingPopulation);
                                remainingPopulation = population_BigDecimal.subtract(partPopulation_BigDecimal);
                                _MaleAgeBoundPopulationCount_TreeMap.put(
                                        aAgeBound,
                                        partPopulation_BigDecimal);
                            }
                            GENESIS_AgeBound aAgeBound = new GENESIS_AgeBound(ageMax);
                            _MaleAgeBoundPopulationCount_TreeMap.put(
                                    aAgeBound,
                                    remainingPopulation);
                        }
                    }
                }
            }
            updateGenderedAgePopulation();
        }
        Logger.getLogger(sourcePackage).exiting(sourceClass, sourceMethod);
    }

    public GENESIS_Population(
            GENESIS_Environment a_GENESIS_Environment) {
        String sourceMethod = "GENESIS_Population(GENESIS_Environment)";
        Logger.getLogger(sourcePackage).entering(sourceClass, sourceMethod);
        this.ge = a_GENESIS_Environment;
        init();
        Logger.getLogger(sourcePackage).exiting(sourceClass, sourceMethod);
    }

    public final void init() {
        _MaleAgeBoundPopulationCount_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        _FemaleAgeBoundPopulationCount_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
    }

    @Override
    public String toString() {
        String result = "GENESIS_Population(";
        Long minAgeInYearsWithPositivePopulationFemale = getMinAgeInYearsWithPositivePopulationFemale();
        Long maxAgeInYearsWithPositivePopulationFemale = getMaxAgeInYearsWithPositivePopulationFemale();
        Long minAgeInYearsWithPositivePopulationMale = getMinAgeInYearsWithPositivePopulationMale();
        Long maxAgeInYearsWithPositivePopulationMale = getMaxAgeInYearsWithPositivePopulationMale();
        BigDecimal totalPopulationFemale = null;
        if (minAgeInYearsWithPositivePopulationFemale != null) {
            totalPopulationFemale = getFemalePopulationSum(
                    minAgeInYearsWithPositivePopulationFemale,
                    maxAgeInYearsWithPositivePopulationFemale);
        }
        BigDecimal totalPopulationMale = null;
        if (minAgeInYearsWithPositivePopulationMale != null) {
            totalPopulationMale = getMalePopulationSum(
                    minAgeInYearsWithPositivePopulationMale,
                    maxAgeInYearsWithPositivePopulationMale);
        }
        BigDecimal maxPopulationInAnyAgeBoundFemale = getMaxPopulationInAnyAgeBoundFemale();
        BigDecimal minPopulationInAnyAgeBoundFemale = getMinPopulationInAnyAgeBoundFemale();
        BigDecimal maxPopulationInAnyAgeBoundMale = getMaxPopulationInAnyAgeBoundMale();
        BigDecimal minPopulationInAnyAgeBoundMale = getMinPopulationInAnyAgeBoundMale();
        result += "minAgeInYearsWithPositivePopulationFemale " + minAgeInYearsWithPositivePopulationFemale;
        result += ", maxAgeInYearsWithPositivePopulationFemale " + maxAgeInYearsWithPositivePopulationFemale;
        result += ", minAgeInYearsWithPositivePopulationMale " + minAgeInYearsWithPositivePopulationMale;
        result += ", maxAgeInYearsWithPositivePopulationMale " + maxAgeInYearsWithPositivePopulationMale;
        result += ", minPopulationInAnyAgeBoundFemale " + minPopulationInAnyAgeBoundFemale;
        result += ", maxPopulationInAnyAgeBoundFemale " + maxPopulationInAnyAgeBoundFemale;
        result += ", minPopulationInAnyAgeBoundMale " + minPopulationInAnyAgeBoundMale;
        result += ", maxPopulationInAnyAgeBoundMale " + maxPopulationInAnyAgeBoundMale;
        result += ", totalPopulationFemale " + totalPopulationFemale;
        result += ", totalPopulationMale " + totalPopulationMale;
        result += ")";
        return result;
    }

    public static void main(String[] args) {
        if (args == null) {
            args = new String[1];
            args[0] = "/scratch01/Work/Projects/GENESIS/workspace/";
        } else {
            if (args.length == 0) {
                args = new String[1];
                args[0] = "/scratch01/Work/Projects/GENESIS/workspace/";
            }
        }
        //new GENESIS_Population().runCSVToXML(args);
        //new GENESIS_Population().runCensusData(args);
        new GENESIS_Population().runCensusDataTest(args);
        //new GENESIS_Population().writeLUTsForOAToMSOAHashMap(args);
    }

    public void runCSVToXML(String[] args) {
        try {
            System.out.println(System.getProperties().keySet().toString());
            System.out.println(System.getProperties().values().toString());
            System.out.println(System.getProperty("java.util.logging.config.file"));
            File directory = new File(args[0]);
            File logDirectory = new File(directory, GENESIS_Log.NAME);
            String logname = sourcePackage;
            GENESIS_Log.parseLoggingProperties(
                    directory,
                    logDirectory,
                    logname);
            run(args, directory);
            GENESIS_Log.reset();
        } catch (Error e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static void writeLUTsForOAToMSOAHashMap() {
        File projectDir = new File(
                "/scratch01/Work/Projects/");
        File theGENESISDir = new File(
                projectDir,
                "GENESIS");
        File theMoSeSDir = new File(
                projectDir,
                "MoSeS");
        // Initialisation
        File theMOSESWorkspace = new File(
                theMoSeSDir,
                "Workspace");
        File theGENESISWorkspace = new File(
                theGENESISDir,
                "workspace");
        File theInputDataGENESISWorkspace = new File(
                theGENESISWorkspace,
                "InputData");
        File theLUTDataGENESISWorkspace = new File(
                theInputDataGENESISWorkspace,
                "LUTs");
        theLUTDataGENESISWorkspace.mkdirs();
        File lut_File = new File(
                theLUTDataGENESISWorkspace,
                "LookUpMSOAfromOAHashMap.thisFile");
        Census_CASDataHandler aCASDataHandler = new Census_CASDataHandler(
                theMOSESWorkspace, "OA");
        HashMap<String, String> lut = aCASDataHandler.get_LookUpMSOAfromOAHashMap();
        Generic_IO.writeObject(lut, lut_File);
    }

    public static void writeLUTsForOAToMSOATreeMap() {
        File projectDir = new File(
                "/scratch01/Work/Projects/");
        File theGENESISDir = new File(
                projectDir,
                "GENESIS");
        File theMoSeSDir = new File(
                projectDir,
                "MoSeS");
        // Initialisation
        File theMOSESWorkspace = new File(
                theMoSeSDir,
                "Workspace");
        File theGENESISWorkspace = new File(
                theGENESISDir,
                "workspace");
        File theInputDataGENESISWorkspace = new File(
                theGENESISWorkspace,
                "InputData");
        File theLUTDataGENESISWorkspace = new File(
                theInputDataGENESISWorkspace,
                "LUTs");
        theLUTDataGENESISWorkspace.mkdirs();
        File lut_File = new File(
                theLUTDataGENESISWorkspace,
                "LookUpMSOAfromOATreeMap.thisFile");
        Census_CASDataHandler aCASDataHandler = new Census_CASDataHandler(
                theMOSESWorkspace, "OA");
        HashMap<String, String> lutHashMap = aCASDataHandler.get_LookUpMSOAfromOAHashMap();
        TreeMap<String, String> lutTreeMap = new TreeMap<String, String>();
        lutTreeMap.putAll(lutHashMap);
        Generic_IO.writeObject(lutTreeMap, lut_File);
    }

    public void runCensusData(String[] args) {
        try {
//            int decimalPlacePrecisionForCalculations = 10;
//            int significantDigits = 3;
            File projectDir = new File(
                    "/scratch01/Work/Projects/");
            File theGENESISDir = new File(
                    projectDir,
                    "GENESIS");
            File theMoSeSDir = new File(
                    projectDir,
                    "MoSeS");
            // Initialisation
            File theMOSESWorkspace = new File(
                    theMoSeSDir,
                    "Workspace");
            File theGENESISWorkspace = new File(
                    theGENESISDir,
                    "workspace");
            ge = new GENESIS_Environment(theGENESISDir);
            ge.Directory = theGENESISWorkspace;
            File theGENESISUKCensusDataDir = new File(
                    theGENESISWorkspace,
                    "UKCensusData");
            theGENESISUKCensusDataDir.mkdirs();
            // Set areaCode to load OA populations for Leeds
            String inputAreaCode = "00DB"; //"00DAFA";//"00DA";
            TreeMap<String, GENESIS_Population> pops = loadOAPopulations(
                    theMOSESWorkspace,
                    theGENESISWorkspace,
                    inputAreaCode);
//            TreeMap<String, GENESIS_Population> pops = loadMSOAPopulations(
//                    theMOSESWorkspace,
//                    theGENESISWorkspace,
//                    inputAreaCode);
            int maximumNumberOfObjectsPerDirectory
                    = GENESIS_Environment.getDefaultMaximumNumberOfObjectsPerDirectory();
            Iterator<String> ite = pops.keySet().iterator();
            File dir = null;
            while (ite.hasNext()) {
                String areaCode = ite.next();
                GENESIS_Population pop = pops.get(areaCode);
                if (dir == null) {
                    dir = Generic_IO.initialiseArchive(
                            theGENESISUKCensusDataDir,
                            maximumNumberOfObjectsPerDirectory);
                } else {
                    dir = Generic_IO.addToArchive(
                            theGENESISUKCensusDataDir,
                            maximumNumberOfObjectsPerDirectory);
                }
                File popFile;
//                popfile = new File(
//                        dir,
//                        "" + areaCode + "_InputPopulation.csv");
//                pop.writeToCSV(popFile);
                popFile = new File(
                        dir,
                        "" + areaCode + "_InputPopulation.xml");
                pop.writeToXML(popFile);
//                /*
//                 * Initialise title and File to write image to
//                 */
//                String title;
//                File file;
//                String format = "PNG";
//                if (args.length != 2) {
//                    System.out.println(
//                            "Expected 2 args:"
//                            + " args[0] title;"
//                            + " args[1] File."
//                            + " Recieved " + args.length + " args.");
//                    // Use defaults
//                    title = "Age Gender Population Bar Chart";
//                    System.out.println("Use default title: " + title);
//                    file = new File(
//                            new File(System.getProperty("user.dir")),
//                            title.replace(" ", "_") + "." + format);
//                    System.out.println("Use default File: " + file.toString());
//                } else {
//                    title = args[0];
//                    file = new File(args[1]);
//                }
//                int dataWidth = 300;//250;
//                int dataHeight = 400;
//                String xAxisLabel = "Population";
//                String yAxisLabel = "Age";
//                boolean drawOriginLinesOnPlot = false;//true;
//                RoundingMode roundingMode = RoundingMode.HALF_UP;
//                int startAgeOfEndYearInterval = 99;
//                BigDecimal maxPopulationInAnyAgeBound = pop.getMaxPopulationInAnyAgeBound();
//                int decimalPlacePrecisionForDisplay =
//                        Generic_BigDecimal.getDecimalPlacePrecision(
//                        maxPopulationInAnyAgeBound, significantDigits);
//                //int startAgeOfEndYearInterval = ((Integer) data[3]).intValue();
//                GENESIS_AgeGenderBarChart chart = new GENESIS_AgeGenderBarChart(
//                        file,
//                        format,
//                        dir,
//                        title,
//                        dataWidth,
//                        dataHeight,
//                        xAxisLabel,
//                        yAxisLabel,
//                        drawOriginLinesOnPlot,
//                        startAgeOfEndYearInterval,
//                        decimalPlacePrecisionForCalculations,
//                        decimalPlacePrecisionForDisplay,
//                        roundingMode,
//                        _GENESIS_Environment);
//                BigDecimal maxAge = null;
//                BigDecimal minAge = null;
//                chart.setData(pop, minAge, maxAge);
//                popFile = new File(
//                        dir,
//                        "" + areaCode + "_InputPopulation.PNG");
//                chart.setFile(popFile);
//                chart.run();
            }
            GENESIS_Demographics.loadInputPopulation(ge,
                    theGENESISUKCensusDataDir);
        } catch (Error e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void runCensusDataTest(String[] args) {
        try {
            //int decimalPlacePrecisionForCalculations = 10;
            //int significantDigits = 3;
            File projectDir = new File(
                    "/scratch01/Work/Projects/");
            File theGENESISDir = new File(
                    projectDir,
                    "GENESIS");
            File theMoSeSDir = new File(
                    projectDir,
                    "MoSeS");
            // Initialisation
            File theMOSESWorkspace = new File(
                    theMoSeSDir,
                    "Workspace");
            File theGENESISWorkspace = new File(
                    theGENESISDir,
                    "workspace");
            ge = new GENESIS_Environment(theGENESISDir);
            ge.Directory = theGENESISWorkspace;
            File theGENESISUKCensusDataDir = new File(
                    theGENESISWorkspace,
                    "2001UKCensusDataTest");

            // Set areaCode to load OA populations for Leeds
            // West Yorkshire: 00CX Bradford; 00CY Calderdale; 00CZ Kirklees; 00DA Leeds; 00DB Wakefield
            String regionID = "00CZ"; //"00CY";//"00CX";//"00DA"; //"00DAFA";//"00DA";

            File dir = new File(
                    theGENESISUKCensusDataDir,
                    "" + regionID);
            dir.mkdirs();

            TreeMap<String, GENESIS_Population> pops = loadOAPopulations(
                    theMOSESWorkspace,
                    theGENESISWorkspace,
                    regionID);
//            TreeMap<String, GENESIS_Population> pops = loadMSOAPopulations(
//                    theMOSESWorkspace,
//                    theGENESISWorkspace,
//                    inputAreaCode);
            int maximumNumberOfObjectsPerDirectory
                    = GENESIS_Environment.getDefaultMaximumNumberOfObjectsPerDirectory();
            Iterator<String> ite = pops.keySet().iterator();
            long max_ID = pops.size() - 1;
            Generic_IO.initialiseArchive(
                    dir,
                    maximumNumberOfObjectsPerDirectory,
                    max_ID);
            long a_ID = 0;
            while (ite.hasNext()) {
                String subregionID = ite.next();
                GENESIS_Population pop = pops.get(subregionID);
                pop.divide(BigDecimal.TEN, 0, RoundingMode.UP);
                File dir2 = Generic_IO.getObjectDirectory(
                        dir,
                        a_ID,
                        max_ID,
                        maximumNumberOfObjectsPerDirectory);
                dir2 = new File(
                        dir2,
                        "" + a_ID);
                File popFile;
//                popfile = new File(
//                        dir,
//                        "" + areaCode + "_InputPopulation.csv");
//                pop.writeToCSV(popFile);
                popFile = new File(
                        dir2,
                        "" + subregionID + "_InputPopulation.xml");
                pop.writeToXML(popFile);
//                /*
//                 * Initialise title and File to write image to
//                 */
//                String title;
//                File file;
//                String format = "PNG";
//                if (args.length != 2) {
//                    System.out.println(
//                            "Expected 2 args:"
//                            + " args[0] title;"
//                            + " args[1] File."
//                            + " Recieved " + args.length + " args.");
//                    // Use defaults
//                    title = "Age Gender Population Bar Chart";
//                    System.out.println("Use default title: " + title);
//                    file = new File(
//                            new File(System.getProperty("user.dir")),
//                            title.replace(" ", "_") + "." + format);
//                    System.out.println("Use default File: " + file.toString());
//                } else {
//                    title = args[0];
//                    file = new File(args[1]);
//                }
//                int dataWidth = 300;//250;
//                int dataHeight = 400;
//                String xAxisLabel = "Population";
//                String yAxisLabel = "Age";
//                boolean drawOriginLinesOnPlot = false;//true;
//                RoundingMode roundingMode = RoundingMode.HALF_UP;
//                int startAgeOfEndYearInterval = 99;
//                BigDecimal maxPopulationInAnyAgeBound = pop.getMaxPopulationInAnyAgeBound();
//                int decimalPlacePrecisionForDisplay =
//                        Generic_BigDecimal.getDecimalPlacePrecision(
//                        maxPopulationInAnyAgeBound, significantDigits);
//                //int startAgeOfEndYearInterval = ((Integer) data[3]).intValue();
//                GENESIS_AgeGenderBarChart chart = new GENESIS_AgeGenderBarChart(
//                        file,
//                        format,
//                        dir,
//                        title,
//                        dataWidth,
//                        dataHeight,
//                        xAxisLabel,
//                        yAxisLabel,
//                        drawOriginLinesOnPlot,
//                        startAgeOfEndYearInterval,
//                        decimalPlacePrecisionForCalculations,
//                        decimalPlacePrecisionForDisplay,
//                        roundingMode,
//                        _GENESIS_Environment);
//                BigDecimal maxAge = null;
//                BigDecimal minAge = null;
//                chart.setData(pop, minAge, maxAge);
//                popFile = new File(
//                        dir,
//                        "" + areaCode + "_InputPopulation.PNG");
//                chart.setFile(popFile);
//                chart.run();
                a_ID++;
            }
            GENESIS_Demographics.loadInputPopulation(ge,
                    theGENESISUKCensusDataDir);
        } catch (Error e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void writeToImage(
            ExecutorService executorService,
            File outfile,
            String format,
            String title,
            int dataWidth,
            int dataHeight,
            String xAxisLabel,
            String yAxisLabel,
            boolean drawOriginLinesOnPlot,
            int decimalPlacePrecisionForCalculations,
            int significantDigits,
            RoundingMode roundingMode,
            int startAgeOfEndYearInterval) {
//        /*
//         * Initialise title and File to write image to
//         */
//        String title;
//        File file;
//        String format = "PNG";
//        if (args.length != 2) {
//            System.out.println(
//                    "Expected 2 args:"
//                    + " args[0] title;"
//                    + " args[1] File."
//                    + " Recieved " + args.length + " args.");
//            // Use defaults
//            title = "Age Gender Population Bar Chart";
//            System.out.println("Use default title: " + title);
//            file = new File(
//                    new File(System.getProperty("user.dir")),
//                    title.replace(" ", "_") + "." + format);
//            System.out.println("Use default File: " + file.toString());
//        } else {
//            title = args[0];
//            file = new File(args[1]);
//        }
//        int dataWidth = 300;//250;
//        int dataHeight = 400;
//        String xAxisLabel = "Population";
//        String yAxisLabel = "Age";
//        boolean drawOriginLinesOnPlot = false;//true;
//        RoundingMode roundingMode = RoundingMode.HALF_UP;
//        int startAgeOfEndYearInterval = 99;
        BigDecimal maxPopulationInAnyAgeBound = this.getMaxPopulationInAnyAgeBound();
        int decimalPlacePrecisionForDisplay
                = Generic_BigDecimal.getDecimalPlacePrecision(
                        maxPopulationInAnyAgeBound, significantDigits);
        //int startAgeOfEndYearInterval = ((Integer) data[3]).intValue();
        GENESIS_AgeGenderBarChart chart = new GENESIS_AgeGenderBarChart(
                executorService,
                outfile,
                format,
                null,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                ge);
        Long maxAge = Long.valueOf(startAgeOfEndYearInterval);
        Long minAge = null;
        chart.setData(this, minAge, maxAge);
        chart.setFile(outfile);
        chart.run();
    }

    public static TreeMap<String, GENESIS_Population> loadOAPopulations(
            File theMOSESWorkspace,
            File theGENESISWorkspace,
            String areaCode) {
        TreeMap<String, GENESIS_Population> result = new TreeMap<String, GENESIS_Population>();
        // Initialisation
        Census_CASDataHandler aCASDataHandler = new Census_CASDataHandler(
                theMOSESWorkspace, "OA");
        HashSet<String> outputAreas = aCASDataHandler.getOACodes_HashSet(areaCode);
        System.out.println(outputAreas.size());
        Long n = aCASDataHandler.getNDataRecords();
        System.out.println(n);
        File aRecordIDZoneCode_HashMap_File = new File(
                theGENESISWorkspace,
                "RecordIDOAZoneCode_HashMap.thisFile");
        File aZoneCodeRecordID_HashMap_File = new File(
                theGENESISWorkspace,
                "OAZoneCodeRecordID_HashMap.thisFile");
        HashMap<Long, String> aRecordIDZoneCode_HashMap;
        HashMap<String, Long> aZoneCodeRecordID_HashMap;
        /*
         * Create lookups if they don't already exist in Filespace
         */
//        aRecordIDZoneCode_HashMap = aCASDataHandler.getRecordIDZoneCode_HashMap();
//        aZoneCodeRecordID_HashMap = aCASDataHandler.getZoneCodeRecordID_HashMap();
//        Generic_IO.writeObject(
//                aRecordIDZoneCode_HashMap,
//                aRecordIDZoneCode_HashMap_File);
//        Generic_IO.writeObject(
//                aZoneCodeRecordID_HashMap,
//                aZoneCodeRecordID_HashMap_File);
        aRecordIDZoneCode_HashMap = (HashMap<Long, String>) Generic_IO.readObject(
                aRecordIDZoneCode_HashMap_File);
        aZoneCodeRecordID_HashMap = (HashMap<String, Long>) Generic_IO.readObject(
                aZoneCodeRecordID_HashMap_File);

        Census_CAS001DataHandler aCAS001DataHandler = aCASDataHandler.getCAS001DataHandler();
        Census_AbstractDataRecord aCASDataRecord;

        Iterator<String> ite = outputAreas.iterator();

        Long recordID;
        String outputAreaCode;
        Census_CAS001DataRecord aCAS001DataRecord;// = aCAS001DataHandler.getCAS001DataRecord(0);
        while (ite.hasNext()) {
            outputAreaCode = ite.next();
            recordID = aZoneCodeRecordID_HashMap.get(outputAreaCode);
            System.out.println("outputAreaCode " + outputAreaCode + " recordID " + recordID);
            aCAS001DataRecord = aCAS001DataHandler.getCAS001DataRecord(recordID);
            System.out.println(aCAS001DataRecord.toString());
            GENESIS_Population pop = new GENESIS_Population();
            pop._FemaleAgeBoundPopulationCount_TreeMap
                    = new TreeMap<GENESIS_AgeBound, BigDecimal>();
            pop._MaleAgeBoundPopulationCount_TreeMap
                    = new TreeMap<GENESIS_AgeBound, BigDecimal>();
            int gender;
            gender = 0;
            for (int age = 0; age < 25; age++) {
                initPop(gender, age, age + 1, pop, aCAS001DataRecord);
            }
            initPop(gender, 25, 30, pop, aCAS001DataRecord);
            initPop(gender, 30, 35, pop, aCAS001DataRecord);
            initPop(gender, 35, 40, pop, aCAS001DataRecord);
            initPop(gender, 40, 45, pop, aCAS001DataRecord);
            initPop(gender, 45, 50, pop, aCAS001DataRecord);
            initPop(gender, 50, 55, pop, aCAS001DataRecord);
            initPop(gender, 55, 60, pop, aCAS001DataRecord);
            initPop(gender, 60, 65, pop, aCAS001DataRecord);
            initPop(gender, 65, 70, pop, aCAS001DataRecord);
            initPop(gender, 70, 75, pop, aCAS001DataRecord);
            initPop(gender, 75, 80, pop, aCAS001DataRecord);
            initPop(gender, 80, 85, pop, aCAS001DataRecord);
            initPop(gender, 85, 90, pop, aCAS001DataRecord);
            initPop(gender, 90, 100, pop, aCAS001DataRecord);
            gender = 1;
            for (int age = 0; age < 25; age++) {
                initPop(gender, age, age + 1, pop, aCAS001DataRecord);
            }
            initPop(gender, 25, 30, pop, aCAS001DataRecord);
            initPop(gender, 30, 35, pop, aCAS001DataRecord);
            initPop(gender, 35, 40, pop, aCAS001DataRecord);
            initPop(gender, 40, 45, pop, aCAS001DataRecord);
            initPop(gender, 45, 50, pop, aCAS001DataRecord);
            initPop(gender, 50, 55, pop, aCAS001DataRecord);
            initPop(gender, 55, 60, pop, aCAS001DataRecord);
            initPop(gender, 60, 65, pop, aCAS001DataRecord);
            initPop(gender, 65, 70, pop, aCAS001DataRecord);
            initPop(gender, 70, 75, pop, aCAS001DataRecord);
            initPop(gender, 75, 80, pop, aCAS001DataRecord);
            initPop(gender, 80, 85, pop, aCAS001DataRecord);
            initPop(gender, 85, 90, pop, aCAS001DataRecord);
            initPop(gender, 90, 100, pop, aCAS001DataRecord);
            result.put(outputAreaCode, pop);
        }
        return result;
    }

    public static void initPop(
            int gender,
            long minimumAgeInYears,
            long maximumAgeInYears,
            GENESIS_Population pop,
            Census_CAS001DataRecord aCAS001DataRecord) {
        BigDecimal population = null;
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(
                minimumAgeInYears,
                maximumAgeInYears);
        if (gender == 0) {
            if (minimumAgeInYears == 0 && maximumAgeInYears == 1) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge0()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge0());
            }
            if (minimumAgeInYears == 1 && maximumAgeInYears == 2) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge1()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge1());
            }
            if (minimumAgeInYears == 2 && maximumAgeInYears == 3) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge2()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge2());
            }
            if (minimumAgeInYears == 3 && maximumAgeInYears == 4) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge3()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge3());
            }
            if (minimumAgeInYears == 4 && maximumAgeInYears == 5) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge4()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge4());
            }
            if (minimumAgeInYears == 5 && maximumAgeInYears == 6) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge5()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge5());
            }
            if (minimumAgeInYears == 6 && maximumAgeInYears == 7) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge6()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge6());
            }
            if (minimumAgeInYears == 7 && maximumAgeInYears == 8) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge7()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge7());
            }
            if (minimumAgeInYears == 8 && maximumAgeInYears == 9) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge8()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge8());
            }
            if (minimumAgeInYears == 9 && maximumAgeInYears == 10) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge9()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge9());
            }
            if (minimumAgeInYears == 10 && maximumAgeInYears == 11) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge10()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge10());
            }
            if (minimumAgeInYears == 11 && maximumAgeInYears == 12) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge11()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge11());
            }
            if (minimumAgeInYears == 12 && maximumAgeInYears == 13) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge12()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge12());
            }
            if (minimumAgeInYears == 13 && maximumAgeInYears == 14) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge13()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge13());
            }
            if (minimumAgeInYears == 14 && maximumAgeInYears == 15) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge14()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge14());
            }
            if (minimumAgeInYears == 15 && maximumAgeInYears == 16) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge15()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge15());
            }
            if (minimumAgeInYears == 16 && maximumAgeInYears == 17) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge16()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge16());
            }
            if (minimumAgeInYears == 17 && maximumAgeInYears == 18) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge17()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge17());
            }
            if (minimumAgeInYears == 18 && maximumAgeInYears == 19) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge18()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge18());
            }
            if (minimumAgeInYears == 19 && maximumAgeInYears == 20) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge19()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge19());
            }
            if (minimumAgeInYears == 20 && maximumAgeInYears == 21) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge20()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge20());
            }
            if (minimumAgeInYears == 21 && maximumAgeInYears == 22) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge21()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge21());
            }
            if (minimumAgeInYears == 22 && maximumAgeInYears == 23) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge22()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge22());
            }
            if (minimumAgeInYears == 23 && maximumAgeInYears == 24) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge23()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge23());
            }
            if (minimumAgeInYears == 24 && maximumAgeInYears == 25) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge24()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge24());
            }
            if (minimumAgeInYears == 25 && maximumAgeInYears == 30) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge25to29()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge25to29());
            }
            if (minimumAgeInYears == 30 && maximumAgeInYears == 35) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge30to34()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge30to34());
            }
            if (minimumAgeInYears == 35 && maximumAgeInYears == 40) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge35to39()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge35to39());
            }
            if (minimumAgeInYears == 40 && maximumAgeInYears == 45) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge40to44()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge40to44());
            }
            if (minimumAgeInYears == 45 && maximumAgeInYears == 50) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge45to49()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge45to49());
            }
            if (minimumAgeInYears == 50 && maximumAgeInYears == 55) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge50to54()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge50to54());
            }
            if (minimumAgeInYears == 55 && maximumAgeInYears == 60) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge55to59()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge55to59());
            }
            if (minimumAgeInYears == 60 && maximumAgeInYears == 65) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge60to64()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge60to64());
            }
            if (minimumAgeInYears == 65 && maximumAgeInYears == 70) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge65to69()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge65to69());
            }
            if (minimumAgeInYears == 70 && maximumAgeInYears == 75) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge70to74()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge70to74());
            }
            if (minimumAgeInYears == 75 && maximumAgeInYears == 80) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge75to79()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge75to79());
            }
            if (minimumAgeInYears == 80 && maximumAgeInYears == 85) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge80to84()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge80to84());
            }
            if (minimumAgeInYears == 85 && maximumAgeInYears == 90) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge85to89()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge85to89());
            }
            if (minimumAgeInYears == 90 && maximumAgeInYears == 100) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsFemalesAge90AndOver()
                        + aCAS001DataRecord.getHouseholdResidentsFemalesAge90AndOver());
            }
            GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                    pop._FemaleAgeBoundPopulationCount_TreeMap,
                    ageBound,
                    population,
                    false);
        } else {
            if (minimumAgeInYears == 0 && maximumAgeInYears == 1) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge0()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge0());
            }
            if (minimumAgeInYears == 1 && maximumAgeInYears == 2) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge1()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge1());
            }
            if (minimumAgeInYears == 2 && maximumAgeInYears == 3) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge2()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge2());
            }
            if (minimumAgeInYears == 3 && maximumAgeInYears == 4) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge3()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge3());
            }
            if (minimumAgeInYears == 4 && maximumAgeInYears == 5) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge4()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge4());
            }
            if (minimumAgeInYears == 5 && maximumAgeInYears == 6) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge5()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge5());
            }
            if (minimumAgeInYears == 6 && maximumAgeInYears == 7) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge6()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge6());
            }
            if (minimumAgeInYears == 7 && maximumAgeInYears == 8) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge7()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge7());
            }
            if (minimumAgeInYears == 8 && maximumAgeInYears == 9) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge8()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge8());
            }
            if (minimumAgeInYears == 9 && maximumAgeInYears == 10) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge9()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge9());
            }
            if (minimumAgeInYears == 10 && maximumAgeInYears == 11) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge10()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge10());
            }
            if (minimumAgeInYears == 11 && maximumAgeInYears == 12) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge11()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge11());
            }
            if (minimumAgeInYears == 12 && maximumAgeInYears == 13) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge12()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge12());
            }
            if (minimumAgeInYears == 13 && maximumAgeInYears == 14) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge13()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge13());
            }
            if (minimumAgeInYears == 14 && maximumAgeInYears == 15) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge14()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge14());
            }
            if (minimumAgeInYears == 15 && maximumAgeInYears == 16) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge15()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge15());
            }
            if (minimumAgeInYears == 16 && maximumAgeInYears == 17) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge16()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge16());
            }
            if (minimumAgeInYears == 17 && maximumAgeInYears == 18) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge17()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge17());
            }
            if (minimumAgeInYears == 18 && maximumAgeInYears == 19) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge18()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge18());
            }
            if (minimumAgeInYears == 19 && maximumAgeInYears == 20) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge19()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge19());
            }
            if (minimumAgeInYears == 20 && maximumAgeInYears == 21) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge20()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge20());
            }
            if (minimumAgeInYears == 21 && maximumAgeInYears == 22) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge21()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge21());
            }
            if (minimumAgeInYears == 22 && maximumAgeInYears == 23) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge22()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge22());
            }
            if (minimumAgeInYears == 23 && maximumAgeInYears == 24) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge23()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge23());
            }
            if (minimumAgeInYears == 24 && maximumAgeInYears == 25) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge24()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge24());
            }
            if (minimumAgeInYears == 25 && maximumAgeInYears == 30) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge25to29()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge25to29());
            }
            if (minimumAgeInYears == 30 && maximumAgeInYears == 35) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge30to34()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge30to34());
            }
            if (minimumAgeInYears == 35 && maximumAgeInYears == 40) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge35to39()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge35to39());
            }
            if (minimumAgeInYears == 40 && maximumAgeInYears == 45) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge40to44()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge40to44());
            }
            if (minimumAgeInYears == 45 && maximumAgeInYears == 50) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge45to49()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge45to49());
            }
            if (minimumAgeInYears == 50 && maximumAgeInYears == 55) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge50to54()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge50to54());
            }
            if (minimumAgeInYears == 55 && maximumAgeInYears == 60) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge55to59()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge55to59());
            }
            if (minimumAgeInYears == 60 && maximumAgeInYears == 65) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge60to64()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge60to64());
            }
            if (minimumAgeInYears == 65 && maximumAgeInYears == 70) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge65to69()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge65to69());
            }
            if (minimumAgeInYears == 70 && maximumAgeInYears == 75) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge70to74()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge70to74());
            }
            if (minimumAgeInYears == 75 && maximumAgeInYears == 80) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge75to79()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge75to79());
            }
            if (minimumAgeInYears == 80 && maximumAgeInYears == 85) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge80to84()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge80to84());
            }
            if (minimumAgeInYears == 85 && maximumAgeInYears == 90) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge85to89()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge85to89());
            }
            if (minimumAgeInYears == 90 && maximumAgeInYears == 100) {
                population = BigDecimal.valueOf(
                        aCAS001DataRecord.getCommunalEstablishmentResidentsMalesAge90AndOver()
                        + aCAS001DataRecord.getHouseholdResidentsMalesAge90AndOver());
            }
            GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                    pop._MaleAgeBoundPopulationCount_TreeMap,
                    ageBound,
                    population,
                    false);
        }
//        BigInteger ageInYearsRange = new BigInteger("" + (maximumAgeInYears - minimumAgeInYears + 1));
//        BigDecimal populationPerYear = Generic_BigDecimal.divideRoundIfNecessary(
//                population,
//                ageInYearsRange,
//                0,
//                RoundingMode.DOWN);
//        pop.initPop(gender, minimumAgeInYears, maximumAgeInYears, population, population);
        pop.updateGenderedAgePopulation();
    }

    public static void run(
            String[] args,
            File directory) {
        int decimalPlacePrecisionForCalculations = 10;
        //int significantDigits = 3;
        File a_File = new File(
                directory.toString()
                + "/InputData/DemographicData/Population/"
                + "Leeds1991GenderAgeCount.csv");
//        File a_File = new File(
//                directory.toString()
//                + "/InputData/DemographicData/Population/
//                + "GenderAgeCount_Test.csv");
        GENESIS_Environment ge = new GENESIS_Environment(directory);
        ge.Time = new GENESIS_Time(1991, 0);
        String[] a_Filename_String_prefixSuffix = a_File.getName().split("\\.");
        GENESIS_Population a_Population = new GENESIS_Population(
                ge,
                a_File,
                BigDecimal.ONE,
                false);
        File b_File = new File(
                a_File.getParentFile(),
                a_Filename_String_prefixSuffix[0] + "Reformatted.csv");
        a_Population.writeToCSV(b_File);
        File c_File = new File(
                a_File.getParentFile(),
                a_Filename_String_prefixSuffix[0] + ".xml");
        XMLConverter.savePopulationToXMLFile(c_File, a_Population);
        ge._Generic_BigDecimal = new Generic_BigDecimal(100);
        GENESIS_Population c_Population = new GENESIS_Population(
                ge,
                c_File,
                BigDecimal.ONE,
                false);
        System.out.println(c_Population.toString());
        int decimalPlacePrecision = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int dataWidth = 300;
        int dataHeight = 600;
        String xAxisLabel = "Population";
        String yAxisLabel = "Age";

        //Future result = GENESIS_Demographics.outputAgeGenderPlot(
        GENESIS_Demographics.outputAgeGenderPlot(
                executorService,
                c_Population,
                "Population Initialised in 1991",
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                a_File.getParentFile(),
                c_Population,
                c_Population.getMinAgeYears(),// BigDecimal.ZERO, 
                c_Population.getMaxAgeYears(),
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecision);

    }

    public static TreeMap<String, GENESIS_Population> aggregateSubregion(
            TreeMap<String, GENESIS_Population> theOAPops,
            TreeMap<String, String> subregionToAggregateRegionLookup,
            GENESIS_Environment aGENESIS_Environment) {
        TreeMap<String, GENESIS_Population> result = new TreeMap<String, GENESIS_Population>();
        Iterator<String> ite;
        String aOACode;
        String aMSOACode;
        // Intitialise Result
        ite = theOAPops.keySet().iterator();
        while (ite.hasNext()) {
            aOACode = ite.next();
            aMSOACode = subregionToAggregateRegionLookup.get(aOACode);
            if (!result.containsKey(aMSOACode)) {
                result.put(aMSOACode, new GENESIS_Population(aGENESIS_Environment));
            }
        }
        ite = theOAPops.keySet().iterator();
        while (ite.hasNext()) {
            aOACode = ite.next();
            aMSOACode = subregionToAggregateRegionLookup.get(aOACode);
            GENESIS_Population popToAdd = theOAPops.get(aOACode);
            result.get(aMSOACode).addPopulation(popToAdd);
        }
        return result;
    }

    /**
     * Adds population from populationToAdd
     *
     * @param populationToAdd
     */
    public void addPopulationNoUpdate(GENESIS_Population populationToAdd) {
//        Iterator<Entry<Age, BigDecimal>> ite;
//        Entry<Age, BigDecimal> entry;
//        Age ageBound;
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite;
        Entry<GENESIS_AgeBound, BigDecimal> entry;
        GENESIS_AgeBound ageBound;
        BigDecimal popToAdd;
        BigDecimal pop;
        ite = populationToAdd._FemaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            popToAdd = entry.getValue();
            pop = getFemalePopulation(ageBound);
            this._FemaleAgeBoundPopulationCount_TreeMap.put(
                    ageBound,
                    popToAdd.add(pop));
        }
        ite = populationToAdd._MaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            popToAdd = entry.getValue();
            pop = getMalePopulation(ageBound);
            this._MaleAgeBoundPopulationCount_TreeMap.put(
                    ageBound,
                    popToAdd.add(pop));
        }
    }

    /**
     * Adds population from populationToAdd
     *
     * @param populationToAdd
     */
    public void addPopulation(GENESIS_Population populationToAdd) {
        addPopulationNoUpdate(populationToAdd);
        updateGenderedAgePopulation();
    }

    /**
     * Adds population from populationToAdd
     *
     * @param populationToSubtract
     */
    public void subtractPopulation(GENESIS_Population populationToSubtract) {
//        Iterator<Entry<Age, BigDecimal>> ite;
//        Entry<Age, BigDecimal> entry;
//        Age ageBound;
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite;
        Entry<GENESIS_AgeBound, BigDecimal> entry;
        GENESIS_AgeBound ageBound;
        BigDecimal popToSubtract;
        BigDecimal pop;
        ite = populationToSubtract._FemaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            popToSubtract = entry.getValue();
            pop = getFemalePopulation(ageBound);
            this._FemaleAgeBoundPopulationCount_TreeMap.put(
                    ageBound,
                    pop.subtract(popToSubtract));
        }
        ite = populationToSubtract._MaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            popToSubtract = entry.getValue();
            pop = getMalePopulation(ageBound);
            this._MaleAgeBoundPopulationCount_TreeMap.put(
                    ageBound,
                    pop.subtract(popToSubtract));
        }
        updateGenderedAgePopulation();
    }

    /**
     * Adds valueToAdd to all age gender counts in this
     *
     * @param valueToAdd
     */
    public void addNoUpdate(BigDecimal valueToAdd) {
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite;
        Entry<GENESIS_AgeBound, BigDecimal> entry;
        GENESIS_AgeBound ageBound;
        BigDecimal pop;
        ite = _FemaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            pop = entry.getValue().add(valueToAdd);
            this._FemaleAgeBoundPopulationCount_TreeMap.put(
                    ageBound,
                    pop);
        }
        ite = _MaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            pop = entry.getValue().add(valueToAdd);
            this._MaleAgeBoundPopulationCount_TreeMap.put(
                    ageBound,
                    pop);
        }
    }

    /**
     * Adds valueToAdd to all age gender counts in this
     *
     * @param valueToAdd
     */
    public void add(BigDecimal valueToAdd) {
        addNoUpdate(valueToAdd);
        updateGenderedAgePopulation();
    }

    /**
     * Multiplies all age gender counts by value
     *
     * @param value
     */
    public void multiplyNoUpdate(BigDecimal value) {
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite;
        Entry<GENESIS_AgeBound, BigDecimal> entry;
        GENESIS_AgeBound ageBound;
        BigDecimal pop;
        ite = _FemaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            pop = entry.getValue().multiply(value);
            this._FemaleAgeBoundPopulationCount_TreeMap.put(
                    ageBound,
                    pop);
        }
        ite = _MaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            pop = entry.getValue().multiply(value);
            this._MaleAgeBoundPopulationCount_TreeMap.put(
                    ageBound,
                    pop);
        }
    }

    /**
     * Multiplies all age gender counts by value
     *
     * @param value
     */
    public void multiply(BigDecimal value) {
        multiplyNoUpdate(value);
        updateGenderedAgePopulation();
    }

    /**
     * Divide all age gender counts by value value expected to be non-zero
     *
     * @param value
     * @param roundingMode
     * @param decimalPlaces
     */
    public void divideNoUpdate(
            BigDecimal value,
            int decimalPlaces,
            RoundingMode roundingMode) {
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite;
        Entry<GENESIS_AgeBound, BigDecimal> entry;
        GENESIS_AgeBound ageBound;
        BigDecimal pop;
        ite = _FemaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            pop = Generic_BigDecimal.divideRoundIfNecessary(
                    entry.getValue(),
                    value,
                    decimalPlaces, roundingMode);
            this._FemaleAgeBoundPopulationCount_TreeMap.put(
                    ageBound,
                    pop);
        }
        ite = _MaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            pop = Generic_BigDecimal.divideRoundIfNecessary(
                    entry.getValue(),
                    value,
                    decimalPlaces, roundingMode);
            this._MaleAgeBoundPopulationCount_TreeMap.put(
                    ageBound,
                    pop);
        }
    }

    /**
     * Divide all age gender counts by value value expected to be non-zero
     *
     * @param value
     * @param roundingMode
     * @param decimalPlaces
     */
    public void divide(
            BigDecimal value,
            int decimalPlaces,
            RoundingMode roundingMode) {
        divideNoUpdate(
                value,
                decimalPlaces,
                roundingMode);
        updateGenderedAgePopulation();
    }

    /**
     * divideByPopulation assumes same population structure.
     *
     * @param roundingMode
     * @param decimalPlaces
     * @return
     */
    public GENESIS_Population divideByPopulationNoUpdate(
            GENESIS_Population populationToDivideBy,
            int decimalPlaces,
            RoundingMode roundingMode) {
        GENESIS_Population result = new GENESIS_Population(ge);
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite;
        Entry<GENESIS_AgeBound, BigDecimal> entry;
        GENESIS_AgeBound ageBound;
        BigDecimal numerator;
        BigDecimal divisor;
        ite = _FemaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            numerator = entry.getValue();
            divisor = populationToDivideBy._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (divisor.compareTo(BigDecimal.ZERO) == 0) {
                result._FemaleAgeBoundPopulationCount_TreeMap.put(ageBound, BigDecimal.ZERO);
            } else {
                BigDecimal value = Generic_BigDecimal.divideRoundIfNecessary(numerator, divisor, decimalPlaces, roundingMode);
                result._FemaleAgeBoundPopulationCount_TreeMap.put(ageBound, value);
            }
        }
        ite = _MaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            ageBound = entry.getKey();
            numerator = entry.getValue();
            divisor = populationToDivideBy._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (divisor.compareTo(BigDecimal.ZERO) == 0) {
                result._MaleAgeBoundPopulationCount_TreeMap.put(ageBound, BigDecimal.ZERO);
            } else {
                BigDecimal value = Generic_BigDecimal.divideRoundIfNecessary(numerator, divisor, decimalPlaces, roundingMode);
                result._MaleAgeBoundPopulationCount_TreeMap.put(ageBound, value);
            }
        }
        return result;
    }

    /**
     * divideByPopulation assumes same population structure.
     *
     * @param roundingMode
     * @param decimalPlaces
     * @return
     */
    public GENESIS_Population divideByPopulation(
            GENESIS_Population populationToDivideBy,
            int decimalPlaces,
            RoundingMode roundingMode) {
        GENESIS_Population result = divideByPopulationNoUpdate(
                populationToDivideBy,
                decimalPlaces,
                roundingMode);
        result.updateGenderedAgePopulation();
        return result;
    }

    /**
     * Initialises genderedAgeBoundPopulation if it is null using
     * CommonFactory.newGenderedAgeBoundPopulation() and returns a reference to
     * it.
     *
     * @return genderedAgeBoundPopulation or
     */
    @Override
    public final GenderedAgeBoundPopulation getGenderedAgeBoundPopulation() {
        if (super.getGenderedAgeBoundPopulation() == null) {
            genderedAgeBoundPopulation = CommonFactory.newGenderedAgeBoundPopulation();
        }
        return genderedAgeBoundPopulation;
    }

    /**
     * updates genderAgePopulation using _FemaleAgeBoundPopulationCount_TreeMap
     * and _MaleAgeBoundPopulationCount_TreeMap
     */
    public final void updateGenderedAgePopulation() {
        GenderedAgeBoundPopulation thisGenderedAgeBoundPopulation = getGenderedAgeBoundPopulation();
        thisGenderedAgeBoundPopulation.getFemale().clear();
        thisGenderedAgeBoundPopulation.getFemale().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBound_Population(
                        _FemaleAgeBoundPopulationCount_TreeMap));
        thisGenderedAgeBoundPopulation.getMale().clear();
        thisGenderedAgeBoundPopulation.getMale().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBound_Population(
                        _MaleAgeBoundPopulationCount_TreeMap));
    }

    /**
     * updates genderAgePopulation using _FemaleAgeBoundPopulationCount_TreeMap
     * and _MaleAgeBoundPopulationCount_TreeMap
     *
     * @param pops
     */
    public static void updateGenderedAgePopulation(TreeMap<String, GENESIS_Population> pops) {
        Iterator<Entry<String, GENESIS_Population>> ite = pops.entrySet().iterator();
        Entry<String, GENESIS_Population> entry;
        while (ite.hasNext()) {
            entry = ite.next();
            entry.getValue().updateGenderedAgePopulation();
        }
    }

    /**
     * updates _FemaleAgeBoundPopulationCount_TreeMap and
     * _MaleAgeBoundPopulationCount_TreeMap using genderAgePopulation
     */
    private void updateGenderAgeBoundPopulation_TreeMaps() {
        _FemaleAgeBoundPopulationCount_TreeMap
                = GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Population(
                        getGenderedAgeBoundPopulation().getFemale());
        _MaleAgeBoundPopulationCount_TreeMap
                = GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Population(
                        getGenderedAgeBoundPopulation().getMale());
    }

//    /**
//     * Returns the sum of female population from ages start Age up to but not
//     * including endAge
//     *
//     * @param startAge
//     * @param endAge
//     * @return
//     */
//    public final BigDecimal getFemalePopulationSum(
//            Integer startAge,
//            Integer endAge) {
//        BigDecimal result = BigDecimal.ZERO;
//        for (int age = startAge; age < endAge; age++) {
//            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
//            BigDecimal ageResult = getFemalePopulation(ageBound);
//            result = result.add(ageResult);
//        }
//        return result;
//    }
    /**
     * Returns the sum of female population from ages start Age up to endAge
     * inclusive.
     *
     * @param startAgeInYears
     * @param startAge
     * @param endAgeInYears
     * @return BigDecimal
     */
    public BigDecimal getFemalePopulationSum(
            long startAgeInYears,
            long endAgeInYears) {
        return getPopulationSum(
                startAgeInYears,
                endAgeInYears,
                _FemaleAgeBoundPopulationCount_TreeMap);
    }

    /**
     * Returns the sum of female population from getMinAgeYearsFemale() to
     * getMaxAgeYearsFemale() inclusive.
     *
     * @return BigDecimal
     */
    public BigDecimal getFemalePopulationTotal() {
        return getPopulationSum(
                getMinAgeYearsFemale(),
                getMaxAgeYearsFemale(),
                _FemaleAgeBoundPopulationCount_TreeMap);
    }

    /**
     * Returns the sum of male population from ages start Age up to endAge
     * inclusive.
     *
     * @param startAgeInYears
     * @param startAge
     * @param endAgeInYears
     * @return BigDecimal
     */
    public BigDecimal getMalePopulationSum(
            long startAgeInYears,
            long endAgeInYears) {
        return getPopulationSum(
                startAgeInYears,
                endAgeInYears,
                _MaleAgeBoundPopulationCount_TreeMap);
    }

    /**
     * Returns the sum of male population from getMinAgeYearsMale() to
     * getMaxAgeYearsMale() inclusive.
     *
     * @return BigDecimal
     */
    public BigDecimal getMalePopulationTotal() {
        return getPopulationSum(
                getMinAgeYearsMale(),
                getMaxAgeYearsMale(),
                _MaleAgeBoundPopulationCount_TreeMap);
    }

    /**
     * Returns the sum total population.
     *
     * @return BigDecimal
     */
    public BigDecimal getPopulationTotal() {
        BigDecimal femalePop = getFemalePopulationTotal();
        BigDecimal malePop = getMalePopulationTotal();
        return femalePop.add(malePop);
    }

    /**
     * Returns the sum of population values from pop.
     *
     * @param pop
     * @param endAgeInYears
     * @return
     */
    public static BigDecimal getPopulationSum(
            Long startAgeInYears,
            Long endAgeInYears,
            TreeMap<GENESIS_AgeBound, BigDecimal> pop) {
        //BigDecimal startAgeInYearsBigDecimal
        BigDecimal result = BigDecimal.ZERO;
        if (pop != null) {
            Entry<GENESIS_AgeBound, BigDecimal> e;
            Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = pop.entrySet().iterator();
            AgeBound ageBound;
            while (ite.hasNext()) {
                e = ite.next();
                ageBound = e.getKey();
                Time ageMinTime = ageBound.getAgeMin();
                Long ageMin;
                if (ageMinTime == null) {
                    ageMin = 0L;
                } else {
                    ageMin = ageMinTime.getYear();
                }
                Time ageMaxTime = ageBound.getAgeMax();
                Long ageMax;
                if (ageMaxTime == null) {
                    ageMax = 200L;
                } else {
                    ageMax = ageMaxTime.getYear();
                }
                if (endAgeInYears.compareTo(ageMax) != -1 && startAgeInYears.compareTo(ageMin) != 1) {
                    BigDecimal v = e.getValue();
                    if (v != null) {
                        result = result.add(v);
                    }
                }
            }
        }
        return result;
    }

//    public BigDecimal getFemalePopulationSum(
//            int startAge,
//            int endAge) {
//        return getFemalePopulationSum(
//                Integer.valueOf(startAge),
//                Integer.valueOf(endAge));
//    }
//    /**
//     * Returns the sum of male population from ages start Age up to but not
//     * including endAge
//     *
//     * @param startAge
//     * @param endAge
//     * @return
//     */
//    public BigDecimal getMalePopulationSum(
//            Integer startAge,
//            Integer endAge) {
//        BigDecimal result = BigDecimal.ZERO;
//        for (int age = startAge; age < endAge; age++) {
//            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
//            BigDecimal ageResult = getMalePopulation(ageBound);
//            result = result.add(ageResult);
//        }
//        return result;
//    }
//
//    public BigDecimal getMalePopulationSum(
//            long startAge,
//            long endAge) {
//        return getMalePopulationSum(
//                Integer.valueOf(startAge),
//                Integer.valueOf(endAge));
//    }
//    public BigDecimal getFemalePopulation(Age ageBound) {
//        BigDecimal result = _FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
//        if (result == null) {
//            result = BigDecimal.ZERO;
//        }
//        return result;
//    }
    public BigDecimal getFemalePopulation(GENESIS_AgeBound ageBound) {
        BigDecimal result = BigDecimal.ZERO;
        if (_FemaleAgeBoundPopulationCount_TreeMap != null) {
            result = _FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (result == null) {
                result = BigDecimal.ZERO;
            }
        }
        return result;
    }

    public BigDecimal getFemalePopulationNullAllowed(GENESIS_AgeBound ageBound) {
        return _FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
    }

    public BigDecimal getMalePopulation(GENESIS_AgeBound ageBound) {
        BigDecimal result = BigDecimal.ZERO;
        if (_MaleAgeBoundPopulationCount_TreeMap != null) {
            result = _MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (result == null) {
                result = BigDecimal.ZERO;
            }
        }
        return result;
    }

    public BigDecimal getMalePopulationNullAllowed(GENESIS_AgeBound ageBound) {
        return _MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
    }

    /**
     * @return Either the MaxPopulation (value) in
     * _FemaleAgeBoundPopulationCount_TreeMap and
     * _MaleAgeBoundPopulationCount_TreeMap or BigDecimal.ZERO.
     */
    public BigDecimal getMaxPopulationInAnyAgeBound() {
        BigDecimal result;
        BigDecimal maxFemale = getMaxPopulationInAnyAgeBoundFemale();
        BigDecimal maxMale = getMaxPopulationInAnyAgeBoundMale();
        if (maxFemale != null) {
            if (maxMale != null) {
                result = maxFemale.max(maxMale);
            } else {
                result = maxFemale;
            }
        } else {
            if (maxMale != null) {
                result = maxMale;
            } else {
                result = BigDecimal.ZERO;
            }
        }
        return result;
    }

    /**
     * @return Either the MaxPopulation (value) in
     * _FemaleAgeBoundPopulationCount_TreeMap or BigDecimal.ZERO.
     */
    public BigDecimal getMaxPopulationInAnyAgeBoundFemale() {
        return getMaxPopulationInAnyAgeBound(_FemaleAgeBoundPopulationCount_TreeMap);
    }

    /**
     * @return Either the MaxPopulation (value) in
     * _MaleAgeBoundPopulationCount_TreeMap or BigDecimal.ZERO.
     */
    public BigDecimal getMaxPopulationInAnyAgeBoundMale() {
        return getMaxPopulationInAnyAgeBound(_MaleAgeBoundPopulationCount_TreeMap);
    }

    /**
     * @param pop
     * @return Either the MaxPopulation (value) in pop or BigDecimal.ZERO.
     */
    public BigDecimal getMaxPopulationInAnyAgeBound(
            TreeMap<GENESIS_AgeBound, BigDecimal> pop) {
        BigDecimal result = BigDecimal.ZERO;
        if (pop != null) {
            result = BigDecimal.ZERO;
            Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = pop.entrySet().iterator();
            Entry<GENESIS_AgeBound, BigDecimal> entry;

            while (ite.hasNext()) {
                entry = ite.next();
                BigDecimal population = entry.getValue();
                if (population != null) {
                    result = result.max(population);
                }
            }
        }
        return result;
    }

//    private BigDecimal getMaxPopulationInAnyAgeBoundFemale() {
//        return getMaxPopulationInAnyAgeBound(genderedAgeBoundPopulation.getFemale());
//    }
//
//    private BigDecimal getMaxPopulationInAnyAgeBoundMale() {
//        return getMaxPopulationInAnyAgeBound(genderedAgeBoundPopulation.getMale());
//    }
//
//    private BigDecimal getMaxPopulationInAnyAgeBound(List<AgeBoundPopulation> pop) {
//        BigDecimal max = BigDecimal.ZERO;
//        Iterator<AgeBoundPopulation> ite = pop.iterator();
//        AgeBoundPopulation ageBoundPopulation;
//        while (ite.hasNext()) {
//            ageBoundPopulation = ite.next();
//            BigDecimal aPopulation = ageBoundPopulation.getPopulation();
//            if (aPopulation != null) {
//                max = max.max(aPopulation);
//            }
//        }
//        return max;
//    }
    private BigDecimal getMinPopulationInAnyAgeBoundFemale() {
        return getMinPopulationInAnyAgeBound(genderedAgeBoundPopulation.getFemale());
    }

    private BigDecimal getMinPopulationInAnyAgeBoundMale() {
        return getMinPopulationInAnyAgeBound(genderedAgeBoundPopulation.getMale());
    }

    private BigDecimal getMinPopulationInAnyAgeBound(List<AgeBoundPopulation> pop) {
        BigDecimal min = new BigDecimal(Generic_BigInteger.Long_MAX_VALUE);
        Iterator<AgeBoundPopulation> ite = pop.iterator();
        AgeBoundPopulation ageBoundPopulation;
        while (ite.hasNext()) {
            ageBoundPopulation = ite.next();
            BigDecimal aPopulation = ageBoundPopulation.getPopulation();
            if (aPopulation != null) {
                min = min.min(aPopulation);
            }
        }
        return min;
    }

    public Long getMinAgeYearsFemale() {
        return GENESIS_Collections.getMinAgeYears(_FemaleAgeBoundPopulationCount_TreeMap);
    }

    public Long getMaxAgeYearsFemale() {
        return GENESIS_Collections.getMaxAgeYears(_FemaleAgeBoundPopulationCount_TreeMap);
    }

    public Long getMinAgeYearsMale() {
        return GENESIS_Collections.getMinAgeYears(_MaleAgeBoundPopulationCount_TreeMap);
    }

    public Long getMaxAgeYearsMale() {
        return GENESIS_Collections.getMaxAgeYears(_MaleAgeBoundPopulationCount_TreeMap);
    }

    public Long getMinAgeYears() {
        Long minAgeYearsFemale = getMinAgeYearsFemale();
        Long minAgeYearsMale = getMinAgeYearsMale();
        if (minAgeYearsFemale == null) {
            return minAgeYearsMale;
        } else {
            if (minAgeYearsMale == null) {
                return minAgeYearsFemale;
            }
        }
        return Math.min(minAgeYearsFemale, minAgeYearsMale);
    }

    public Long getMaxAgeYears() {
        Long maxAgeYearsFemale = getMaxAgeYearsFemale();
        Long maxAgeYearsMale = getMaxAgeYearsMale();
        if (maxAgeYearsFemale == null) {
            return maxAgeYearsMale;
        } else {
            if (maxAgeYearsMale == null) {
                return maxAgeYearsFemale;
            }
        }
        return Math.max(maxAgeYearsFemale, maxAgeYearsMale);
    }

    public Long getMinAgeInYearsWithPositivePopulation() {
        Long minFemale = getMinAgeInYearsWithPositivePopulationFemale();
        Long minMale = getMinAgeInYearsWithPositivePopulationMale();
        if (minFemale == null) {
            return minMale;
        } else {
            if (minMale == null) {
                return minFemale;
            }
        }
        return Math.min(minFemale, minMale);
    }

    public Long getMaxAgeInYearsWithPositivePopulation() {
        Long maxFemale = getMaxAgeInYearsWithPositivePopulationFemale();
        Long maxMale = getMaxAgeInYearsWithPositivePopulationMale();
        if (maxFemale == null) {
            return maxMale;
        } else {
            if (maxMale == null) {
                return maxFemale;
            }
        }
        return Math.max(maxFemale, maxMale);
    }

    public Long getMinAgeInYearsWithPositivePopulationFemale() {
        return getMinAgeInYearsWithPositivePopulation(
                getGenderedAgeBoundPopulation().getFemale());
    }

    public Long getMinAgeInYearsWithPositivePopulationMale() {
        return getMinAgeInYearsWithPositivePopulation(
                getGenderedAgeBoundPopulation().getMale());
    }

    public Long getMinAgeInYearsWithPositivePopulation(List<AgeBoundPopulation> l) {
        long minAge = Long.MAX_VALUE;
        if (l != null) {
            Iterator<AgeBoundPopulation> ite = l.iterator();
            AgeBoundPopulation ageBoundPopulation;
            while (ite.hasNext()) {
                ageBoundPopulation = ite.next();
                if (ageBoundPopulation.getPopulation().compareTo(BigDecimal.ZERO) == 1) {
                    minAge = Math.min(
                            minAge,
                            ageBoundPopulation.getAgeBound().getAgeMin().getYear());
                }
            }
        }
        if (minAge == Long.MAX_VALUE) {
            return null;
        }
        return minAge;
    }

    public Long getMaxAgeInYearsWithPositivePopulationFemale() {
        return getMaxAgeInYearsWithPositivePopulation(
                getGenderedAgeBoundPopulation().getFemale());
    }

    public Long getMaxAgeInYearsWithPositivePopulationMale() {
        return getMaxAgeInYearsWithPositivePopulation(
                getGenderedAgeBoundPopulation().getMale());
    }

    public Long getMaxAgeInYearsWithPositivePopulation(List<AgeBoundPopulation> l) {
        Long maxAge = Long.MIN_VALUE;
        if (l != null) {
            Iterator<AgeBoundPopulation> ite = l.iterator();
            AgeBoundPopulation ageBoundPopulation;
            while (ite.hasNext()) {
                ageBoundPopulation = ite.next();
                if (ageBoundPopulation.getPopulation().compareTo(BigDecimal.ZERO) == 1) {
                    maxAge = Math.max(
                            maxAge,
                            ageBoundPopulation.getAgeBound().getAgeMax().getYear());
                }
            }
        }
        if (maxAge == Long.MIN_VALUE) {
            return null;
        }
        return maxAge;
    }

//    public Long getMaxAgeYears(TreeMap<GENESIS_AgeBound, BigDecimal> pop) {
//        Long maxAge = null;
//        if (pop != null) {
//            GENESIS_AgeBound ageBound;
//            if (!pop.isEmpty()) {
//                ageBound = pop.lastKey();
//                maxAge = ageBound.getAgeMax().getYear();
//            }
//        }
//        return maxAge;
//    }
//
//    
////    public int getMaxAgeYears() {
////        int maxAge = getMaxAgeYearsFemale();
////        maxAge = Math.max(maxAge, getMaxAgeYearsMale());
////        return maxAge;
////    }
    public void writeToXML(File a_File) {
        XMLConverter.savePopulationToXMLFile(a_File, this);
//        try {
//            PopulationFactory.writeFileOut(this, new FileOutputStream(a_File));
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(GENESIS_Population.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (JAXBException ex) {
//            Logger.getLogger(GENESIS_Population.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void writeToCSV(File a_File) {
        PrintWriter a_PrintWriter = null;
        try {
            File parent = a_File.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            } else {
                if (!parent.isDirectory()) {
                    parent.mkdir();
                }
            }
            a_PrintWriter = new PrintWriter(new FileOutputStream(a_File));
            String header = "Gender,AgeYearsMin,AgeDaysMin,AgeYearsMax,AgeDaysMax,Population";
            //String header = "Gender,AgeMin,AgeMax,Population";
            a_PrintWriter.println(header);
            Iterator<AgeBoundPopulation> ite;
            AgeBoundPopulation ageBoundPopulation;
            GENESIS_AgeBound ageBound;
            GENESIS_Time minTime;
            GENESIS_Time maxTime;
            ite = genderedAgeBoundPopulation.getFemale().iterator();
            while (ite.hasNext()) {
                ageBoundPopulation = ite.next();
                ageBound = new GENESIS_AgeBound(ageBoundPopulation.getAgeBound());
                String minYear;
                String minDay;
                String maxYear;
                String maxDay;
                if (ageBound.getAgeMin() == null) {
                    minYear = "null";
                    minDay = "null";
                } else {
                    minTime = new GENESIS_Time(ageBound.getAgeMin());
                    minYear = "" + minTime.getYear();
                    minDay = "" + minTime.getDayOfYear();
                }
                if (ageBound.getAgeMax() == null) {
                    maxYear = "null";
                    maxDay = "null";
                } else {
                    maxTime = new GENESIS_Time(ageBound.getAgeMax());
                    maxYear = "" + maxTime.getYear();
                    maxDay = "" + maxTime.getDayOfYear();
                }

                a_PrintWriter.println(
                        "0,"
                        + minYear + ","
                        + minDay + ","
                        + maxYear + ","
                        + maxDay + ","
                        + ageBoundPopulation.getPopulation().toPlainString());
            }
            ite = genderedAgeBoundPopulation.getMale().iterator();
            while (ite.hasNext()) {
                ageBoundPopulation = ite.next();
                ageBound = new GENESIS_AgeBound(ageBoundPopulation.getAgeBound());
                String minYear;
                String minDay;
                String maxYear;
                String maxDay;
                if (ageBound.getAgeMin() == null) {
                    minYear = "null";
                    minDay = "null";
                } else {
                    minTime = new GENESIS_Time(ageBound.getAgeMin());
                    minYear = "" + minTime.getYear();
                    minDay = "" + minTime.getDayOfYear();
                }
                if (ageBound.getAgeMax() == null) {
                    maxYear = "null";
                    maxDay = "null";
                } else {
                    maxTime = new GENESIS_Time(ageBound.getAgeMax());
                    maxYear = "" + maxTime.getYear();
                    maxDay = "" + maxTime.getDayOfYear();
                }

                a_PrintWriter.println(
                        "1,"
                        + minYear + ","
                        + minDay + ","
                        + maxYear + ","
                        + maxDay + ","
                        + ageBoundPopulation.getPopulation().toPlainString());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Tring to handle " + e.getLocalizedMessage());
            System.err.println("Wait for 2 seconds then trying again to writeToCSV.");
            // This can happen because of too many open files.
            // Try waiting for 2 seconds and then repeating...
            try {
                synchronized (a_File) {
                    a_File.wait(2000L);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
            writeToCSV(a_File);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            log(Level.SEVERE,
                    "Exception in " + this.getClass().getName()
                    + ".writeToCSV(File)");
        } finally {
            a_PrintWriter.close();
        }
    }

//    public GENESIS_Population getAggregatePopulation(
//            HashSet<GENESIS_AgeBound> groups) {
//    }
    /**
     * Aggregates and returns a new GENESIS_Population using
     * GENESIS_AgeBound.getAgeMin() for aggregation.
     *
     * @param minAge The value for which any GENESIS_AgeBound.getAgeMin() in the
     * population TreeMaps are aggregated to if they are less than or equal to.
     * @param maxAge The value for which any GENESIS_AgeBound.getAgeMin() in the
     * population TreeMaps are aggregated to if they are greater than or equal
     * to.
     * @return An Object[] result where: result[0] is the aggregated
     * GENESIS_Population; result[1] is the maximum population in any
     * GENESIS_AgeBound group.
     */
    public Object[] getAggregateGENESIS_Population(
            Long minAge,
            Long maxAge) {
        return getAggregateGENESIS_Population(this, minAge, maxAge);
    }

    /**
     * Aggregates and returns a new GENESIS_Population using
     * GENESIS_AgeBound.getAgeMin() for aggregation.
     *
     * @param pop
     * @param minAgeInYears The value for which any GENESIS_AgeBound.getAgeMin()
     * in the population TreeMaps are aggregated to if they are less than or
     * equal to.
     * @param maxAgeInYears The value for which any GENESIS_AgeBound.getAgeMin()
     * in the population TreeMaps are aggregated to if they are greater than or
     * equal to.
     * @return An Object[] result where: result[0] is the aggregated
     * GENESIS_Population; result[1] is the maximum population in any
     * GENESIS_AgeBound group.
     */
    public static Object[] getAggregateGENESIS_Population(
            GENESIS_Population pop,
            Long minAgeInYears,
            Long maxAgeInYears) {
        Object[] result = new Object[2];
        // Convert the TreeMap<GENESIS_AgeBound, BigDecimal> to 
        // TreeMap<Integer, BigDecimal> using minAge and calculate max 
        // population at any age_long at the same time for efficiency.
        GENESIS_Population resultPopulation = new GENESIS_Population();
        GENESIS_AgeBound ageBound;
        GENESIS_AgeBound minAgeBound;
        GENESIS_AgeBound maxAgeBound;
        Long theMinAgeInYears = new Long(minAgeInYears.longValue());
        Long theMaxAgeInYears = new Long(maxAgeInYears.longValue());
        if (minAgeInYears == null) {
            theMinAgeInYears = pop.getMinAgeYears();
        }
        if (maxAgeInYears == null) {
            theMaxAgeInYears = pop.getMaxAgeYears();
        }
        if (minAgeInYears == null) {
            theMinAgeInYears = 0L;
        }
        if (maxAgeInYears == null) {
            theMaxAgeInYears = 0L;
        }
        BigDecimal population;
        BigDecimal maxPop = BigDecimal.ZERO;
        minAgeBound = new GENESIS_AgeBound(minAgeInYears);
        maxAgeBound = new GENESIS_AgeBound(maxAgeInYears);
        resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
                minAgeBound,
                BigDecimal.ZERO);
        resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
                maxAgeBound,
                BigDecimal.ZERO);
        resultPopulation._MaleAgeBoundPopulationCount_TreeMap.put(
                minAgeBound,
                BigDecimal.ZERO);
        resultPopulation._MaleAgeBoundPopulationCount_TreeMap.put(
                maxAgeBound,
                BigDecimal.ZERO);
        Entry<GENESIS_AgeBound, BigDecimal> entry;
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite;
        if (pop._FemaleAgeBoundPopulationCount_TreeMap != null) {
            ite = pop._FemaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
            while (ite.hasNext()) {
                entry = ite.next();
                ageBound = entry.getKey();
                Long ageMin = ageBound.getAgeMin().getYear();
                Long ageMax = ageBound.getAgeMin().getYear(); // Aggregation done on the basis of getAgeMin().getYear()
                population = entry.getValue();
                if (ageMin.compareTo(theMinAgeInYears) != 1) {
                    BigDecimal minPopulation = resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.get(
                            minAgeBound);
                    population = population.add(minPopulation);
                    maxPop = maxPop.max(population);
                    resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
                            minAgeBound,
                            population);
                } else {
                    if (ageMax.compareTo(theMaxAgeInYears) != -1) {
                        BigDecimal maxPopulation = resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.get(
                                maxAgeBound);
                        population = population.add(maxPopulation);
                        maxPop = maxPop.max(population);
                        resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
                                maxAgeBound,
                                population);
                    } else {
                        maxPop = maxPop.max(population);
                        resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
                                ageBound,
                                population);
                    }
                }
            }
        }
        if (pop._MaleAgeBoundPopulationCount_TreeMap != null) {
            ite = pop._MaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
            while (ite.hasNext()) {
                entry = ite.next();
                ageBound = entry.getKey();
                Long ageMin = ageBound.getAgeMin().getYear();
                Long ageMax = ageBound.getAgeMin().getYear(); // Aggregation done on the basis of getAgeMin().getYear()
                population = entry.getValue();
                if (ageMin.compareTo(theMinAgeInYears) != 1) {
                    BigDecimal minPopulation = resultPopulation._MaleAgeBoundPopulationCount_TreeMap.get(
                            minAgeBound);
                    population = population.add(minPopulation);
                    maxPop = maxPop.max(population);
                    resultPopulation._MaleAgeBoundPopulationCount_TreeMap.put(
                            minAgeBound,
                            population);
                } else {
                    if (ageMax.compareTo(theMaxAgeInYears) != -1) {
                        BigDecimal maxPopulation = resultPopulation._MaleAgeBoundPopulationCount_TreeMap.get(
                                maxAgeBound);
                        population = population.add(maxPopulation);
                        maxPop = maxPop.max(population);
                        resultPopulation._MaleAgeBoundPopulationCount_TreeMap.put(
                                maxAgeBound,
                                population);
                    } else {
                        maxPop = maxPop.max(population);
                        resultPopulation._MaleAgeBoundPopulationCount_TreeMap.put(
                                ageBound,
                                population);
                    }
                }
            }
        }
        pop.updateGenderedAgePopulation();
        result[0] = resultPopulation;
        result[1] = maxPop;
        return result;
    }

    /**
     * Warning! This is not a general implementation that examines the ageMax of
     * AgeBounds. It assumes nesting AgeBounds for aggregation. For instance it
     * will work as expected for aggregating some two year age bounds into other
     * four year age bounds so long as the starting age bounds match. Aggregates
     * and returns a new GENESIS_Population using GENESIS_AgeBound.getAgeMin()
     * for aggregation.
     *
     * @param minAge The value below which any GENESIS_AgeBound.getAgeMin() in
     * pop TreeMaps are aggregated to if less than or equal to.
     * @param maxAge The value above which any GENESIS_AgeBound.getAgeMin() in
     * pop TreeMaps are aggregated to if greater than or equal to.
     * @param maxAgeInYears
     * @param a_GENESIS_Environment
     * @param ageIncrement
     * @return An Object[] result where: result[0] is the aggregated
     * GENESIS_Population; result[1] is the maximum population in any
     * GENESIS_AgeBound group.
     */
    public static Object[] getAggregateGENESIS_Population(
            GENESIS_Population pop,
            Long minAgeInYears,
            Long maxAgeInYears,
            Long ageIncrement,
            GENESIS_Environment a_GENESIS_Environment) {
        Object[] result = new Object[2];
        // Convert the TreeMap<GENESIS_AgeBound, BigDecimal> to 
        // TreeMap<Integer, BigDecimal> using minAge and calculate max 
        // population at any age_long at the same time for efficiency.
        GENESIS_Population resultPopulation = new GENESIS_Population(
                a_GENESIS_Environment);
        GENESIS_AgeBound ageBound;
        GENESIS_AgeBound minAgeBound;
        //GENESIS_AgeBound maxAgeBound;
        Long theMinAgeInYears = new Long(minAgeInYears.longValue());
        Long theMaxAgeInYears = new Long(maxAgeInYears.longValue());
        if (minAgeInYears == null) {
            theMinAgeInYears = pop.getMinAgeYears();
        }
        if (maxAgeInYears == null) {
            theMaxAgeInYears = pop.getMaxAgeYears();
        }
        if (minAgeInYears == null) {
            theMinAgeInYears = 0L;
        }
        if (maxAgeInYears == null) {
            theMaxAgeInYears = 0L;
        }
        BigDecimal population;
        BigDecimal maxPop = BigDecimal.ZERO;
        minAgeBound = new GENESIS_AgeBound(
                0L,
                theMinAgeInYears.longValue() + ageIncrement);
        GENESIS_AgeBound currentAgeBound;
        currentAgeBound = new GENESIS_AgeBound(minAgeBound);
        //maxAgeBound = new GENESIS_AgeBound(CommonFactory.newAgeBound(maxAge, null));

        resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
                minAgeBound,
                BigDecimal.ZERO);
//        resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
//                maxAgeBound,
//                BigDecimal.ZERO);
        resultPopulation._MaleAgeBoundPopulationCount_TreeMap.put(
                minAgeBound,
                BigDecimal.ZERO);
//        resultPopulation._MaleAgeBoundPopulationCount_TreeMap.put(
//                maxAgeBound,
//                BigDecimal.ZERO);
        Entry<GENESIS_AgeBound, BigDecimal> entry;
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite;
        if (pop._FemaleAgeBoundPopulationCount_TreeMap != null) {
            ite = pop._FemaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
            while (ite.hasNext()) {
                entry = ite.next();
                ageBound = entry.getKey();
                Long ageMin = ageBound.getAgeMin().getYear();
                if (currentAgeBound.getAgeMax() != null) {
                    while (ageMin.compareTo(currentAgeBound.getAgeMax().getYear()) != -1) {
                        if (new Long(currentAgeBound.getAgeMin().getYear()).compareTo(theMaxAgeInYears) != -1) {
                            currentAgeBound = new GENESIS_AgeBound(
                                    currentAgeBound.getAgeMin().getYear(),
                                    null);
                            break;
                        }
//                resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
//                            currentAgeBound,
//                            BigDecimal.ZERO);
                        currentAgeBound = new GENESIS_AgeBound(
                                currentAgeBound.getAgeMax().getYear(),
                                currentAgeBound.getAgeMax().getYear() + ageIncrement);
                        resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
                                currentAgeBound,
                                BigDecimal.ZERO);
                    }
                }
                population = entry.getValue();
                if (population != null) {
                    if (population.compareTo(BigDecimal.ZERO) == 1) {
                        BigDecimal currentAgeBoundPop = resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.get(currentAgeBound);
                        if (currentAgeBoundPop != null) {
                            population = population.add(currentAgeBoundPop);
                            resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
                                    currentAgeBound,
                                    population);
                            maxPop = maxPop.max(population);
                        } else {
                            resultPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(
                                    currentAgeBound,
                                    population);
                            maxPop = maxPop.max(population);
                        }
                    }
                }
            }
        }
        currentAgeBound = new GENESIS_AgeBound(minAgeBound);
        if (pop._MaleAgeBoundPopulationCount_TreeMap != null) {
            ite = pop._MaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
            while (ite.hasNext()) {
                entry = ite.next();
                ageBound = entry.getKey();
                Long ageMin = ageBound.getAgeMin().getYear();
                if (currentAgeBound.getAgeMax() != null) {
                    while (ageMin.compareTo(currentAgeBound.getAgeMax().getYear()) != -1) {
                        if (new Long(currentAgeBound.getAgeMin().getYear()).compareTo(theMaxAgeInYears) != -1) {
                            currentAgeBound = new GENESIS_AgeBound(
                                    CommonFactory.newAgeBound(
                                            currentAgeBound.getAgeMin(),
                                            null));
                            break;
                        }
                        currentAgeBound = new GENESIS_AgeBound(
                                currentAgeBound.getAgeMax().getYear(),
                                currentAgeBound.getAgeMax().getYear() + ageIncrement);
                        resultPopulation._MaleAgeBoundPopulationCount_TreeMap.put(
                                currentAgeBound,
                                BigDecimal.ZERO);
                    }
                }
                population = entry.getValue();
                BigDecimal currentAgeBoundPop = resultPopulation._MaleAgeBoundPopulationCount_TreeMap.get(currentAgeBound);
                if (currentAgeBoundPop != null) {
                    population = population.add(currentAgeBoundPop);
                    resultPopulation._MaleAgeBoundPopulationCount_TreeMap.put(
                            currentAgeBound,
                            population);
                    maxPop = maxPop.max(population);
                } else {
                    resultPopulation._MaleAgeBoundPopulationCount_TreeMap.put(
                            currentAgeBound,
                            population);
                    maxPop = maxPop.max(population);
                }
            }
        }
        result[0] = resultPopulation;
        result[1] = maxPop;
        return result;
    }

    private void log(Level level, String message) {
        getLogger().log(level, message);
    }

//    public static Logger getLogger() {
//        return Logger.getLogger(sourcePackage);
//    }
    public Logger getLogger() {
        return GENESIS_Log.LOGGER;
    }
}
