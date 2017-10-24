package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.XMLConverter;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.CommonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.population.PopulationType;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;

/**
 * A class for loading annual death counts by age and gender, annual birth
 * counts by age of mother for a single area and for estimating the annual
 * mortality and fertility rates for the area based on the start population and
 * estimated annual mortality and fertility rates. (The estimates are usually
 * the mortality and fertility rates from the previous year or some general
 * mortality or fertility rates). The resulting estimates are produced by an
 * iterative method which adjust expected rates until there is a convergence and
 * the observed numbers of birth and death are expected from the estimated rates
 * from the start population.
 */
public class GENESIS_MortalityAndFertilityEstimator extends PopulationType implements Serializable {

    protected static final long serialVersionUID = 1L;
    public transient GENESIS_Environment ge;
    /**
     * Used for Logging
     */
    private static final String sourceClass = GENESIS_MortalityAndFertilityEstimator.class.getName();
    private static final String sourcePackage = GENESIS_MortalityAndFertilityEstimator.class.getPackage().getName();
    //private static final Logger logger = getLogger().getLogger(sourcePackage);
    /**
     * For storing the Observed Annual Death Count data
     */
    public GENESIS_Population _ObservedAnnualDeaths;
    /**
     * For storing the Observed Annual Birth Count data
     */
    public GENESIS_Population _ObservedAnnualBirthsByAgeOfMother;
    /**
     * Initial Population
     */
    public GENESIS_Population _StartYearPopulation;
    /**
     * Initial Mortality Estimate
     */
    public GENESIS_Mortality _InitialEstimateOfMortality;
    /**
     * Initial Fertility Estimate
     */
    public GENESIS_Fertility _InitialEstimateOfFertility;
    /**
     * Estimated End Year Population
     */
    public GENESIS_Population _EstimatedEndYearPopulation;
    /**
     * Estimated Mid Year Population
     */
    public GENESIS_Population _EstimatedMidYearPopulation;

    public GENESIS_MortalityAndFertilityEstimator() {
    }

    public GENESIS_MortalityAndFertilityEstimator(
            GENESIS_Environment _GENESIS_Environment) {
        String sourceMethod = "GENESIS_Death()";
        getLogger().entering(sourceClass, sourceMethod);
        this.ge = _GENESIS_Environment;
        init();
        getLogger().exiting(sourceClass, sourceMethod);
    }

    public GENESIS_MortalityAndFertilityEstimator(
            GENESIS_Environment _GENESIS_Environment,
            GENESIS_Population _ObservedAnnualDeaths,
            GENESIS_Population _ObservedAnnualBirthsByAgeOfMother,
            GENESIS_Population _StartYearPopulation,
            GENESIS_Mortality _InitialEstimateOfMortality,
            GENESIS_Fertility _InitialEstimateOfFertility) {
        String sourceMethod = "GENESIS_MortalityAndFertilityEstimator("
                + "GENESIS_Environment,GENESIS_Population,"
                + "GENESIS_Population,GENESIS_Population,"
                + "GENESIS_Mortality,GENESIS_Fertility)";
        getLogger().entering(sourceClass, sourceMethod);
        this.ge = _GENESIS_Environment;
        this._ObservedAnnualDeaths = _ObservedAnnualDeaths;
        this._ObservedAnnualBirthsByAgeOfMother = _ObservedAnnualBirthsByAgeOfMother;
        this._StartYearPopulation = _StartYearPopulation;
        this._InitialEstimateOfMortality = _InitialEstimateOfMortality;
        this._InitialEstimateOfFertility = _InitialEstimateOfFertility;
        getLogger().exiting(sourceClass, sourceMethod);
    }

    private void init() {
        String sourceMethod = "init()";
        getLogger().entering(sourceClass, sourceMethod);
        LogManager.getLogManager().addLogger(Logger.getLogger(GENESIS_Log.DefaultLoggerName));
        this._ObservedAnnualDeaths = new GENESIS_Population(ge);
        this._ObservedAnnualBirthsByAgeOfMother = new GENESIS_Population(ge);
        this._StartYearPopulation = new GENESIS_Population(ge);
        this._InitialEstimateOfMortality = new GENESIS_Mortality(ge);
        this._InitialEstimateOfFertility = new GENESIS_Fertility(ge);
    }

    public static void main(String[] args) {
        try {
            System.out.println(System.getProperties().keySet().toString());
            System.out.println(System.getProperties().values().toString());
            System.out.println(System.getProperty("java.util.logging.config.file"));
            File directory = new File(args[0]);
            File logDirectory = new File(
                    directory,
                    GENESIS_Log.Generic_DefaultLogDirectoryName);
            String logname = sourcePackage;
            GENESIS_Log.parseLoggingProperties(
                    directory,
                    logDirectory,
                    logname);
            // Run main processing task
            //directory = new File("/scratch01/Work/Projects/GENESIS/");
            GENESIS_Environment ge = new GENESIS_Environment(directory);
            GENESIS_MortalityAndFertilityEstimator instance =
                    new GENESIS_MortalityAndFertilityEstimator(ge);
            instance.runFormatData();
            //instance.runTest();
            GENESIS_Log.reset();
        } catch (Error e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void runTest() {
        int decimalPlaces = 10;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        int year = 1991;
        String aLADCode = "00DB";//"00DA";
        BigDecimal twinProbability = new BigDecimal("0.01464695");
        BigDecimal tripletProbability = new BigDecimal("0.00022305");

        File dataDirectory = new File(
                ge.get_Directory(true).getParentFile(),
                "data");
        PopulationType birthPopulation = loadBirthXMLData(
                dataDirectory,
                "BirthDataXML",
                year, aLADCode);
        GENESIS_Population birthPop = new GENESIS_Population(
                ge,
                birthPopulation);
        PopulationType deathPopulation = loadDeathXMLData(
                dataDirectory,
                "DeathDataXML", year, aLADCode);
        GENESIS_Population deathPop = new GENESIS_Population(
                ge,
                deathPopulation);
        // Load Population Data
//        File popDataDir = new File(
//                dataDirectory.getParentFile(),
//                "/workspace/DemographicModel_Aspatial_1/0_99/0/metadata/population/start/");
////        File popDataDir = new File(
////                dataDirectory.getParentFile(),
////                "ls workspace/DemographicModel_Aspatial_1/0_99/0/metadata/population/start/InputData/InputData/DemographicData/Population/1991UKCensusData/" + aLADCode + "/");
//        TreeMap<String, GENESIS_Population> pops = GENESIS_Population.loadInputPopulation(
//                _GENESIS_Environment,
//                popDataDir);
//        GENESIS_Population initialPop = pops.get(GENESIS_Demographics.TotalPopulationName_String);
//        File popDataFile = new File(
//                dataDirectory,
//                "testPop.xml");
//        XMLConverter.savePopulationToXMLFile(popDataFile, initialPop);
        File popDataFile = new File(
                dataDirectory,
                "testPop.xml");
        GENESIS_Population initialPop = new GENESIS_Population(
                ge,
                XMLConverter.loadPopulationFromXMLFile(popDataFile));

        GENESIS_Mortality initialEstimateOfMortality = getInitialEstimateOfMortality(initialPop,
                deathPop,
                ge,
                decimalPlaces,
                roundingMode);

        GENESIS_Fertility initialEstimateOfFertility = getInitialEstimateOfFertility(initialPop,
                birthPop,
                twinProbability,
                tripletProbability,
                ge,
                decimalPlaces,
                roundingMode);
        Object[] estimatedMortalityAndFertility = run(
                year,
                aLADCode,
                twinProbability,
                tripletProbability,
                initialPop,
                initialEstimateOfMortality,
                initialEstimateOfFertility,
                birthPop,
                deathPop,
                decimalPlaces,
                roundingMode);
        writeEstimatedMortalityAndFertility(
                dataDirectory,
                estimatedMortalityAndFertility);
    }

    public Object[] run(
            int year,
            String aLADCode,
            BigDecimal twinProbability,
            BigDecimal tripletProbability,
            GENESIS_Population initialPop,
            GENESIS_Mortality initialEstimateOfMortality,
            GENESIS_Fertility initialEstimateOfFertility,
            GENESIS_Population birthPop,
            GENESIS_Population deathPop,
            int decimalPlaces,
            RoundingMode roundingMode) {
//        int decimalPlaces = 10;
//        RoundingMode roundingMode = RoundingMode.HALF_UP;
//        int year = 1991;
//        String aLADCode = "00DA";
//        BigDecimal twinProbability = new BigDecimal("0.01464695");
//        BigDecimal tripletProbability = new BigDecimal("0.00022305");
        Object[] result = new Object[2];
        File dataDirectory = new File(
                ge.get_Directory(true).getParentFile(),
                "data");
        System.out.println("Initial ReEstimation ");
        Object[] estimatedRates = getEstimateRates(
                initialPop,
                initialEstimateOfMortality,
                initialEstimateOfFertility,
                birthPop,
                deathPop,
                decimalPlaces,
                roundingMode);
        // Iteratively adjust the mortality and fertility until observed deaths 
        // and observed births match (convergence criteria required).
        for (int i = 0; i < 10; i++) {
            System.out.println("Recursive ReEstimation Iteration " + i);
            estimatedRates = getEstimateRates(
                    initialPop,
                    (GENESIS_Mortality) estimatedRates[0],
                    (GENESIS_Fertility) estimatedRates[1],
                    birthPop,
                    deathPop,
                    decimalPlaces,
                    roundingMode);
        }
        writeEstimatedMortalityAndFertility(
                dataDirectory,
                estimatedRates);
        result[0] = (GENESIS_Mortality) estimatedRates[0];
        result[1] = (GENESIS_Fertility) estimatedRates[1];
        return result;
    }

    public void writeEstimatedMortalityAndFertility(
            File dataDirectory,
            Object[] estimatedMortalityAndFertility) {
        File outputMortalityFile = new File(
                dataDirectory,
                "Mortality.xml");
        GENESIS_Mortality mortality = (GENESIS_Mortality) estimatedMortalityAndFertility[0];
        XMLConverter.saveMortalityToXMLFile(
                outputMortalityFile,
                mortality);
        File outputFertilityFile = new File(
                dataDirectory,
                "Fertility.xml");
        GENESIS_Fertility fertility = (GENESIS_Fertility) estimatedMortalityAndFertility[1];
        fertility.updateAgeBoundRates();
        XMLConverter.saveFertilityToXMLFile(
                outputFertilityFile,
                fertility);
    }

    /**
     *
     * @param roundingMode
     * @param decimalPlaces
     * @param theGENESIS_Environment
     * @return Object[] result where result[0] is the Annual Mortality,
     * result[1] is the Annual Fertility
     */
    public static Object[] getMortalityAndFertilityEstimate(
            String aLADCode,
            int year,
            BigDecimal twinProbability,
            BigDecimal tripletProbability,
            GENESIS_Environment theGENESIS_Environment,
            int decimalPlaces,
            RoundingMode roundingMode) {
        Object[] result = new Object[2];
//        int decimalPlaces = 10;
//        RoundingMode roundingMode = RoundingMode.HALF_UP;
//        int year = 1991;
//        String aLADCode = "00DA";
//        BigDecimal twinProbability = new BigDecimal("0.01464695");
//        BigDecimal tripletProbability = new BigDecimal("0.00022305");
        File dataDirectory = new File(
                theGENESIS_Environment.get_Directory(true).getParentFile(),
                "data");
        PopulationType birthPopulation = loadBirthXMLData(
                dataDirectory,
                "Birth",
                year, aLADCode);
        GENESIS_Population birthPop = new GENESIS_Population(
                theGENESIS_Environment,
                birthPopulation);
        PopulationType deathPopulation = loadDeathXMLData(
                dataDirectory,
                "Death",
                year, aLADCode);
        GENESIS_Population deathPop = new GENESIS_Population(
                theGENESIS_Environment,
                deathPopulation);
        // Load Population Data
//        File popDataDir = new File(
//                dataDirectory.getParentFile(),
//                "/workspace/DemographicModel_Aspatial_1/0_99/0/metadata/population/start/");
////        File popDataDir = new File(
////                dataDirectory.getParentFile(),
////                "ls workspace/DemographicModel_Aspatial_1/0_99/0/metadata/population/start/InputData/InputData/DemographicData/Population/1991UKCensusData/" + aLADCode + "/");
//        TreeMap<String, GENESIS_Population> pops = GENESIS_Population.loadInputPopulation(
//                _GENESIS_Environment,
//                popDataDir);
//        GENESIS_Population initialPop = pops.get(GENESIS_Demographics.TotalPopulationName_String);
//        File popDataFile = new File(
//                dataDirectory,
//                "testPop.xml");
//        XMLConverter.savePopulationToXMLFile(popDataFile, initialPop);
        File popDataFile = new File(
                dataDirectory,
                "testPop.xml");
        GENESIS_Population initialPop = new GENESIS_Population(
                theGENESIS_Environment,
                XMLConverter.loadPopulationFromXMLFile(popDataFile));

        GENESIS_Mortality initialEstimateOfMortality = getInitialEstimateOfMortality(
                initialPop,
                deathPop,
                theGENESIS_Environment,
                decimalPlaces,
                roundingMode);

        GENESIS_Fertility initialEstimateOfFertility = getInitialEstimateOfFertility(
                initialPop,
                birthPop,
                twinProbability,
                tripletProbability,
                theGENESIS_Environment,
                decimalPlaces,
                roundingMode);

        System.out.println("Initial ReEstimation ");
        Object[] estimatedRates = getEstimateRates(
                initialPop,
                initialEstimateOfMortality,
                initialEstimateOfFertility,
                birthPop,
                deathPop,
                decimalPlaces,
                roundingMode);
        // Iteratively adjust the mortality and fertility until observed deaths 
        // and observed births match (convergence criteria required).
        for (int i = 0; i < 10; i++) {
            System.out.println("Recursice ReEstimation Iteration " + i);
            estimatedRates = getEstimateRates(
                    initialPop,
                    (GENESIS_Mortality) estimatedRates[0],
                    (GENESIS_Fertility) estimatedRates[1],
                    birthPop,
                    deathPop,
                    decimalPlaces,
                    roundingMode);
        }
        File outputMortalityFile = new File(
                dataDirectory,
                "Mortality.xml");
        GENESIS_Mortality mortality = (GENESIS_Mortality) estimatedRates[0];
        mortality.updateGenderedAgeBoundRates();
        XMLConverter.saveMortalityToXMLFile(
                outputMortalityFile,
                (GENESIS_Mortality) estimatedRates[0]);
        File outputFertilityFile = new File(
                dataDirectory,
                "Fertility.xml");
        GENESIS_Fertility fertility = (GENESIS_Fertility) estimatedRates[1];
        fertility.updateAgeBoundRates();
        XMLConverter.saveFertilityToXMLFile(
                outputFertilityFile,
                fertility);
        result[0] = mortality;
        result[1] = fertility;
        return result;
    }

    public static GENESIS_Fertility getInitialEstimateOfFertility(
            GENESIS_Population initialPop,
            GENESIS_Population birthPop,
            BigDecimal twinProbability,
            BigDecimal tripletProbability,
            GENESIS_Environment theGENESIS_Environment,
            int decimalPlaces,
            RoundingMode roundingMode) {
        GENESIS_Fertility result = new GENESIS_Fertility(theGENESIS_Environment);
        result._AnnualLiveBirthFertilityAgeBoundRate_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        result._TwinPregnancyAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        result._TripletPregnancyAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        Iterator<GENESIS_AgeBound> ite = birthPop._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound aAgeBound = ite.next();
            Long minYear = aAgeBound.getAgeMin().getYear();
            Long maxYear = aAgeBound.getAgeMax().getYear();
            BigDecimal femaleBirths = birthPop.getFemalePopulation(aAgeBound);
//            BigDecimal femalePop = initialPop.getFemalePopulation(aAgeBound);
            BigDecimal femalePop = initialPop.getFemalePopulationSum(minYear, maxYear);
//            System.out.println("aAgeBound " + aAgeBound);
//            System.out.println("femaleBirths " + femaleBirths);
//            System.out.println("femalePop " + femalePop);
            BigDecimal femaleFertility;
            if (femalePop.compareTo(BigDecimal.ZERO) == 0) {
                femaleFertility = BigDecimal.ZERO;
            } else {
                femaleFertility = Generic_BigDecimal.divideRoundIfNecessary(
                        femaleBirths,
                        femalePop,
                        decimalPlaces,
                        roundingMode);
            }
            //System.out.println("femaleFertility " + femaleFertility);
            for (long ageInYears = minYear; ageInYears < maxYear; ageInYears++) {
                GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(ageInYears, ageInYears + 1L);
                result._AnnualLiveBirthFertilityAgeBoundRate_TreeMap.put(
                        newAgeBound, femaleFertility);
                result._TwinPregnancyAgeBoundProbability_TreeMap.put(
                        newAgeBound, twinProbability);
                result._TripletPregnancyAgeBoundProbability_TreeMap.put(
                        newAgeBound, tripletProbability);
            }
        }
        return result;
    }

    public static GENESIS_Mortality getInitialEstimateOfMortality(
            GENESIS_Population initialPop,
            GENESIS_Population deathPop,
            GENESIS_Environment theGENESIS_Environment,
            int decimalPlaces,
            RoundingMode roundingMode) {
        GENESIS_Mortality result = new GENESIS_Mortality(theGENESIS_Environment);
        Iterator<GENESIS_AgeBound> ite = deathPop._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound aAgeBound = ite.next();
            Long minYear = aAgeBound.getAgeMin().getYear();
            Long maxYear = aAgeBound.getAgeMax().getYear();
            BigDecimal femaleDeaths = deathPop.getFemalePopulation(aAgeBound);
            BigDecimal maleDeaths = deathPop.getMalePopulation(aAgeBound);
            BigDecimal femalePop = initialPop.getFemalePopulationSum(minYear, maxYear);
            BigDecimal malePop = initialPop.getMalePopulationSum(minYear, maxYear);
//            System.out.println("aAgeBound " + aAgeBound);
//            System.out.println("femaleDeaths " + femaleDeaths);
//            System.out.println("femalePop " + femalePop);
            BigDecimal femaleMortality;
            if (femalePop.compareTo(BigDecimal.ZERO) == 0) {
                femaleMortality = BigDecimal.ZERO;
            } else {
                femaleMortality = Generic_BigDecimal.divideRoundIfNecessary(
                        femaleDeaths,
                        femalePop,
                        decimalPlaces,
                        roundingMode);
            }
            //System.out.println("femaleMortality " + femaleMortality);
            for (long ageInYears = aAgeBound.getAgeMin().getYear(); ageInYears < aAgeBound.getAgeMax().getYear(); ageInYears++) {
                GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(ageInYears, ageInYears + 1L);
                result._FemaleAnnualMortalityAgeBoundRate_TreeMap.put(
                        newAgeBound, femaleMortality);
            }
            BigDecimal maleMortality;
            if (malePop.compareTo(BigDecimal.ZERO) == 0) {
                maleMortality = BigDecimal.ZERO;
            } else {
                maleMortality = Generic_BigDecimal.divideRoundIfNecessary(
                        maleDeaths,
                        malePop,
                        decimalPlaces,
                        roundingMode);
            }
            //System.out.println("maleMortality " + maleMortality);
            for (long ageInYears = aAgeBound.getAgeMin().getYear(); ageInYears < aAgeBound.getAgeMax().getYear(); ageInYears++) {
                GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(ageInYears, ageInYears + 1L);
                result._MaleAnnualMortalityAgeBoundRate_TreeMap.put(
                        newAgeBound, maleMortality);
            }
        }
        return result;
    }

    public static PopulationType loadBirthXMLData(
            File dataDirectory,
            String birthDataDirectoyName,
            int year,
            String aLADCode) {
        PopulationType result;
        File birthDataXMLFileDirectory = new File(
                dataDirectory,
                birthDataDirectoyName);
        File birthDataXMLYearFileDirectory = new File(
                birthDataXMLFileDirectory,
                "" + year);
        File birthDataXMLYearLADCodeFile = new File(
                birthDataXMLYearFileDirectory,
                "BirthData" + aLADCode + ".xml");
        result = XMLConverter.loadPopulationFromXMLFile(birthDataXMLYearLADCodeFile);
        return result;
    }

    public static PopulationType loadDeathXMLData(
            File dataDirectory,
            String deathDataDirectoyName,
            int year,
            String aLADCode) {
        PopulationType result;
        File deathDataXMLFileDirectory = new File(
                dataDirectory,
                deathDataDirectoyName);
        File deathDataXMLYearFileDirectory = new File(
                deathDataXMLFileDirectory,
                "" + year);
        File deathDataXMLYearLADCodeFile = new File(
                deathDataXMLYearFileDirectory,
                "DeathData" + aLADCode + ".xml");
        result = XMLConverter.loadPopulationFromXMLFile(deathDataXMLYearLADCodeFile);
        return result;
    }

    public void runFormatData() {
        File dataDirectory = new File(
                ge.get_Directory(true).getParentFile(),
                "data");
        File birthAndDeathCountDataDirectory = new File(
                dataDirectory,
                "BirthAndDeathCountData");
        // Load deathData from single csv file and write to GENESIS_Population 
        // XML files for each year and each Local Authority District
        String femaleDeathsFilename = "E&W-DeathsFemales-LA-1981-2006.csv";
        TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>> femaleDeathData =
                formatSourceDeathData(birthAndDeathCountDataDirectory, femaleDeathsFilename);
        String maleDeathsFilename = "E&W-DeathsMales-LA-1981-2006.csv";
        TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>> maleDeathData =
                formatSourceDeathData(birthAndDeathCountDataDirectory, maleDeathsFilename);
        saveDeathDataToXML(dataDirectory, femaleDeathData, maleDeathData);
        // Load birthData from single csv file and write to GENESIS_Population 
        // XML files for each year and each Local Authority District        
        String birthsFilename = "E&W-Births-LA-1981-2006.csv";
        TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>> birthData =
                formatSourceBirthData(birthAndDeathCountDataDirectory, birthsFilename);
        saveBirthDataToXML(dataDirectory, birthData);

    }

    public TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>> formatSourceDeathData(File directory, String filename) {
        String sourceMethod = "readDeathData(File,String)";
        TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>> result =
                new TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>>();
        getLogger().entering(sourceClass, sourceMethod);
        try {
            File file = new File(directory,
                    filename);
            BufferedReader aBufferedReader =
                    new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(file)));
            StreamTokenizer aStreamTokenizer =
                    new StreamTokenizer(aBufferedReader);
            Generic_StaticIO.setStreamTokenizerSyntax1(aStreamTokenizer);
            aStreamTokenizer.wordChars(':', ':');
            aStreamTokenizer.wordChars('<', '<');
            aStreamTokenizer.wordChars('&', '&');
            aStreamTokenizer.wordChars('\'', '\'');
            String line = "";
            //Skip the first 5 lines
            for (int i = 0; i < 5; i++) {
                Generic_StaticIO.skipline(aStreamTokenizer);
            }
            int tokenType = aStreamTokenizer.nextToken();
            // Hardcoded Age Categories
            //<1,01-04,05-09,10-14,15-19,20-24,25-29,30-34,35-39,40-44,45-49,50-54,55-59,60-64,65-69,70-74,75-79,80-84,85+
            TreeSet<GENESIS_AgeBound> ageCategories = new TreeSet<GENESIS_AgeBound>();
            GENESIS_AgeBound aAgeBound;
            aAgeBound = new GENESIS_AgeBound();
            aAgeBound.setAgeMin(CommonFactory.newTime(0L));
            aAgeBound.setAgeMax(CommonFactory.newTime(1L));
            ageCategories.add(aAgeBound);
            aAgeBound = new GENESIS_AgeBound();
            aAgeBound.setAgeMin(CommonFactory.newTime(1L));
            aAgeBound.setAgeMax(CommonFactory.newTime(5L));
            ageCategories.add(aAgeBound);
            for (long l = 5L; l < 90L; l += 5L) {
                aAgeBound = new GENESIS_AgeBound();
                aAgeBound.setAgeMin(CommonFactory.newTime(l));
                aAgeBound.setAgeMax(CommonFactory.newTime(l + 5L));
                ageCategories.add(aAgeBound);
            }
            TreeMap<String, TreeMap<GENESIS_AgeBound, Long>> anLADLevelDeathData =
                    new TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>();
            int lineCounter = 0;
            int regyr0 = 1981;
            int regyr = 0;
            while (tokenType != StreamTokenizer.TT_EOF) {
                switch (tokenType) {
                    case StreamTokenizer.TT_EOL:
//                        System.out.println(lineCounter + " " + line);
                        String[] fields = line.split("\",");
                        if (fields.length == 2) {
//                            System.out.println(lineCounter + " " + line);
                            String[] fields0 = fields[0].split(",");
                            regyr = Integer.valueOf(fields0[0]);
                            if (regyr != regyr0) {
                                result.put(regyr0, anLADLevelDeathData);
                                anLADLevelDeathData =
                                        new TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>();
                            }
                            String anLADCode = fields0[1];
                            TreeMap<GENESIS_AgeBound, Long> values = new TreeMap<GENESIS_AgeBound, Long>();
                            String[] fields2 = fields[1].split(",");
                            int index = 1;
                            Iterator<GENESIS_AgeBound> ite = ageCategories.iterator();
                            while (ite.hasNext()) {
                                aAgeBound = ite.next();
                                values.put(aAgeBound, Long.valueOf(fields2[index]));
                                index++;
                            }
                            anLADLevelDeathData.put(anLADCode, values);
                        } else {
//                            System.out.println(lineCounter + " " + line);
                            fields = line.split(",");
                            regyr = Integer.valueOf(fields[0]);
                            if (regyr != regyr0) {
                                result.put(regyr0, anLADLevelDeathData);
                                anLADLevelDeathData =
                                        new TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>();
                                regyr0 = regyr;
                            }
                            String anLADCode = fields[1];
                            TreeMap<GENESIS_AgeBound, Long> values = new TreeMap<GENESIS_AgeBound, Long>();
                            int index = 4;
                            Iterator<GENESIS_AgeBound> ite = ageCategories.iterator();
                            while (ite.hasNext()) {
                                aAgeBound = ite.next();
                                values.put(aAgeBound, Long.valueOf(fields[index]));
                                index++;
                            }
                            anLADLevelDeathData.put(anLADCode, values);
                        }
                        lineCounter++;
                        break;
                    case StreamTokenizer.TT_WORD:
                        line = aStreamTokenizer.sval;
                        break;
                }
                tokenType = aStreamTokenizer.nextToken();
            }
            result.put(regyr, anLADLevelDeathData);
        } catch (IOException aIOException) {
            System.err.println(aIOException.getMessage() + " in "
                    + this.getClass().getName()
                    + "." + sourceMethod);
            System.exit(GENESIS_ErrorAndExceptionHandler.IOException);
        }
        getLogger().exiting(sourceClass, sourceMethod);
        return result;
    }

    public TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>> formatSourceBirthData(File directory, String filename) {
        String sourceMethod = "readBirthData(File,String)";
        TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>> result =
                new TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>>();
        getLogger().entering(sourceClass, sourceMethod);
        try {
            File file = new File(directory,
                    filename);
            BufferedReader aBufferedReader =
                    new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(file)));
            StreamTokenizer aStreamTokenizer =
                    new StreamTokenizer(aBufferedReader);
            Generic_StaticIO.setStreamTokenizerSyntax1(aStreamTokenizer);
            aStreamTokenizer.wordChars(':', ':');
            aStreamTokenizer.wordChars('<', '<');
            aStreamTokenizer.wordChars('"', '"');
            aStreamTokenizer.wordChars('&', '&');
            aStreamTokenizer.wordChars('\'', '\'');
            String line = "";
            //Skip the first 2 lines
            for (int i = 0; i < 2; i++) {
                Generic_StaticIO.skipline(aStreamTokenizer);
            }
            int tokenType = aStreamTokenizer.nextToken();
            // Hardcoded Age Categories
            //<20,20-24,25-29,30-34,35-39,40+
            //15-20,20-24,25-29,30-34,35-39,40-45
            TreeSet<GENESIS_AgeBound> ageCategories = new TreeSet<GENESIS_AgeBound>();
            GENESIS_AgeBound aAgeBound;
            for (long l = 15L; l < 45L; l += 5L) {
                aAgeBound = new GENESIS_AgeBound();
                aAgeBound.setAgeMin(CommonFactory.newTime(l));
                aAgeBound.setAgeMax(CommonFactory.newTime(l + 5L));
                ageCategories.add(aAgeBound);
            }

            int lineCounter = 0;
            int startYear = 1981;
            int endYear = 2006;
            for (int year = startYear; year <= endYear; year++) {
                result.put(year, new TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>());
            }
            int years = endYear - startYear + 1;
            while (tokenType != StreamTokenizer.TT_EOF) {
                switch (tokenType) {
                    case StreamTokenizer.TT_EOL:
//                        System.out.println(lineCounter + " " + line);
                        String[] fields = line.split("\",");
                        if (fields.length == 2) {
                            String[] fields0 = fields[0].split(",");
                            String anLADCode = fields0[0];
//                            if (!anLADCode.isEmpty()) {
                            String[] fields1 = fields[1].split(",");
                            int index = 1;
                            for (int year = startYear; year <= endYear; year++) {
                                TreeMap<String, TreeMap<GENESIS_AgeBound, Long>> anLADLevelBirthData = result.get(year);
                                TreeMap<GENESIS_AgeBound, Long> anLADAgeBoundValue = new TreeMap<GENESIS_AgeBound, Long>();
                                Iterator<GENESIS_AgeBound> ite = ageCategories.iterator();
                                while (ite.hasNext()) {
                                    aAgeBound = ite.next();
                                    anLADAgeBoundValue.put(aAgeBound, Long.valueOf(fields1[index]));
                                    index++;
                                }
                                anLADLevelBirthData.put(anLADCode, anLADAgeBoundValue);
                                index++;
                            }
//                            } else {
//                                int debug = 1;
//                            }
                        } else {
                            fields = line.split(",");
                            if (fields.length == (7 * years) + 2) {
                                int index = 2;
                                String anLADCode = fields[0];
                                if (!anLADCode.isEmpty()) {
//                                    System.out.println(lineCounter + " " + line);
                                    for (int year = startYear; year <= endYear; year++) {
                                        TreeMap<String, TreeMap<GENESIS_AgeBound, Long>> anLADLevelBirthData = result.get(year);
                                        TreeMap<GENESIS_AgeBound, Long> anLADAgeBoundValue = new TreeMap<GENESIS_AgeBound, Long>();
                                        Iterator<GENESIS_AgeBound> ite = ageCategories.iterator();
                                        while (ite.hasNext()) {
                                            aAgeBound = ite.next();
                                            anLADAgeBoundValue.put(aAgeBound, Long.valueOf(fields[index]));
                                            index++;
                                        }
                                        anLADLevelBirthData.put(anLADCode, anLADAgeBoundValue);
                                        index++;
                                    }
//                                } else {
//                                    // This was for dealing with the final row of sums of births in all LADs for each year
//                                    System.out.println(lineCounter + " " + line);
                                }
                            }
                        }
                        lineCounter++;
                        break;
                    case StreamTokenizer.TT_WORD:
                        line = aStreamTokenizer.sval;
                        break;
                }
                tokenType = aStreamTokenizer.nextToken();
            }
        } catch (IOException aIOException) {
            System.err.println(aIOException.getMessage() + " in "
                    + this.getClass().getName()
                    + "." + sourceMethod);
            System.exit(GENESIS_ErrorAndExceptionHandler.IOException);
        }
        getLogger().exiting(sourceClass, sourceMethod);
        return result;
    }

//    public void skipLine(StreamTokenizer aStreamTokenizer) {
//        try {
//            int tokenType = aStreamTokenizer.nextToken();
//            while (tokenType != StreamTokenizer.TT_EOL) {
//                tokenType = aStreamTokenizer.nextToken();
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(GENESIS_MortalityAndFertilityEstimator.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public static void saveBirthDataToXML(
            File dataDirectory,
            TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>> birthData) {
        File birthCountXMLFileDirectory = new File(
                dataDirectory,
                "BirthCountXML");
        Iterator<Integer> ite = birthData.keySet().iterator();
        while (ite.hasNext()) {
            Integer year = ite.next();
            TreeMap<String, TreeMap<GENESIS_AgeBound, Long>> anLADLevelBirthData = birthData.get(year);
            Iterator<String> ite2 = anLADLevelBirthData.keySet().iterator();
            while (ite2.hasNext()) {
                String aLADCode = ite2.next();
                File birthCountLADDirectory = new File(
                        birthCountXMLFileDirectory,
                        "" + aLADCode);
                File birthCountXMLYearFileDirectory = new File(
                        birthCountLADDirectory,
                        "" + year);
                File birthCountXMLYearLADCodeFile = new File(
                        birthCountXMLYearFileDirectory,
                        "BirthCount_" + aLADCode + "_" + year + ".xml");
                TreeMap<GENESIS_AgeBound, Long> anLADAgeBoundValue = anLADLevelBirthData.get(aLADCode);
                GENESIS_Population pop = new GENESIS_Population();
                Iterator<GENESIS_AgeBound> ite3 = anLADAgeBoundValue.keySet().iterator();
                while (ite3.hasNext()) {
                    GENESIS_AgeBound aAgeBound = ite3.next();
                    Long popLong = anLADAgeBoundValue.get(aAgeBound);
                    pop._FemaleAgeBoundPopulationCount_TreeMap.put(
                            aAgeBound, BigDecimal.valueOf(popLong));
                }
                pop.updateGenderedAgePopulation();
                XMLConverter.savePopulationToXMLFile(birthCountXMLYearLADCodeFile, pop);
            }
        }
    }

    public static void saveDeathDataToXML(
            File dataDirectory,
            TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>> femaleDeathData,
            TreeMap<Integer, TreeMap<String, TreeMap<GENESIS_AgeBound, Long>>> maleDeathData) {
        File deathCountXMLFileDirectory = new File(
                dataDirectory,
                "DeathCountXML");
        Iterator<Integer> ite = femaleDeathData.keySet().iterator();
        while (ite.hasNext()) {
            Integer year = ite.next();
            TreeMap<String, TreeMap<GENESIS_AgeBound, Long>> anLADLevelFemaleDeathData = femaleDeathData.get(year);
            TreeMap<String, TreeMap<GENESIS_AgeBound, Long>> anLADLevelMaleDeathData = maleDeathData.get(year);
            Iterator<String> ite2 = anLADLevelFemaleDeathData.keySet().iterator();
            while (ite2.hasNext()) {
                String aLADCode = ite2.next();
                File deathCountLADDirectory = new File(
                        deathCountXMLFileDirectory,
                        "" + aLADCode);
                File deathCountXMLYearFileDirectory = new File(
                        deathCountLADDirectory,
                        "" + year);
                File deathCountXMLYearLADCodeFile = new File(
                        deathCountXMLYearFileDirectory,
                        "DeathCount_" + aLADCode + "_" + year + ".xml");
                GENESIS_Population pop = new GENESIS_Population();
                TreeMap<GENESIS_AgeBound, Long> anLADFemaleAgeBoundValue = anLADLevelFemaleDeathData.get(aLADCode);
                Iterator<GENESIS_AgeBound> ite3;
                ite3 = anLADFemaleAgeBoundValue.keySet().iterator();
                while (ite3.hasNext()) {
                    GENESIS_AgeBound aAgeBound = ite3.next();
                    Long popLong = anLADFemaleAgeBoundValue.get(aAgeBound);
                    pop._FemaleAgeBoundPopulationCount_TreeMap.put(
                            aAgeBound, BigDecimal.valueOf(popLong));
                }
                TreeMap<GENESIS_AgeBound, Long> anLADMaleAgeBoundValue = anLADLevelMaleDeathData.get(aLADCode);
                ite3 = anLADMaleAgeBoundValue.keySet().iterator();
                while (ite3.hasNext()) {
                    GENESIS_AgeBound aAgeBound = ite3.next();
                    Long popLong = anLADMaleAgeBoundValue.get(aAgeBound);
                    pop._MaleAgeBoundPopulationCount_TreeMap.put(
                            aAgeBound, BigDecimal.valueOf(popLong));
                }
                pop.updateGenderedAgePopulation();
                XMLConverter.savePopulationToXMLFile(deathCountXMLYearLADCodeFile, pop);
            }
        }
    }

    /**
     *
     * @param startYearPopulation
     * @param initialEstimateOfMortality
     * @param initialEstimateOfFertility
     * @param birthPopulation
     * @param deathPopulation
     * @param decimalPlacePrecision
     * @param roundingMode
     * @return result where; result[0] is the estimated mortality rate;
     * result[1] is the estimated fertility rate
     */
    public static Object[] getEstimateRates(
            GENESIS_Population startYearPopulation,
            GENESIS_Mortality initialEstimateOfMortality,
            GENESIS_Fertility initialEstimateOfFertility,
            GENESIS_Population birthPopulation,
            GENESIS_Population deathPopulation,
            int decimalPlacePrecision,
            RoundingMode roundingMode) {
        GENESIS_Environment a_GENESIS_Environment = initialEstimateOfMortality.ge;
        Object[] result = new Object[2];
        Object[] theoreticalEndYearPopulationsAndFertility;
        theoreticalEndYearPopulationsAndFertility =
                GENESIS_Demographics.getTheoreticalEndYearPopulationsAndFertility(
                startYearPopulation,
                initialEstimateOfMortality,
                initialEstimateOfFertility,
                decimalPlacePrecision,
                false);
        /*
         * theoreticalEndYearPopulationsAndFertility[0] is a 
         * GENESIS_Population of the survived population
         * theoreticalEndYearPopulationsAndFertility[1] is a 
         * GENESIS_Population of the dead population
         * theoreticalEndYearPopulationsAndFertility[2] is a 
         * TreeMap<GENESIS_AgeBound,BigDecimal> live birth by ageBound of mother 
         * theoreticalEndYearPopulationsAndFertility[3] is a 
         * TreeMap<GENESIS_AgeBound,BigDecimal> of twins by ageBound of mother
         * theoreticalEndYearPopulationsAndFertility[4] is a 
         * TreeMap<GENESIS_AgeBound,BigDecimal> of triplets by ageBound of mother;
         */
        Object[] theoreticalFertilityAndNewBornPopulation =
                GENESIS_Demographics.getTheoreticalFertilityAndNewBornPopulation(
                startYearPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                ((GENESIS_Population) theoreticalEndYearPopulationsAndFertility[0])._FemaleAgeBoundPopulationCount_TreeMap,
                initialEstimateOfMortality,
                initialEstimateOfFertility,
                decimalPlacePrecision);
        /*
         * theoreticalFertilityAndNewBornPopulation[0] = birthsByAgeOfMother;
         * theoreticalFertilityAndNewBornPopulation[1] = twinsByAgeOfMother;
         * theoreticalFertilityAndNewBornPopulation[2] = tripletsByAgeOfMother;
         * theoreticalFertilityAndNewBornPopulation[3] = totalSurvivingFemaleBirths;
         * theoreticalFertilityAndNewBornPopulation[4] = totalSurvivingMaleBirths;
         * theoreticalFertilityAndNewBornPopulation[5] = deadFemaleBabies;
         * theoreticalFertilityAndNewBornPopulation[6] = deadMaleBabies;
         */
        Iterator<GENESIS_AgeBound> ite;
        BigDecimal two = new BigDecimal("2");
        // Calculate Mid-Year Population Estimates
        System.out.println("Calculate Mid-Year Population Estimates");
        GENESIS_Population midYearPopulation = new GENESIS_Population(startYearPopulation);
        GENESIS_Population theoreticalSurvivedPopulation = (GENESIS_Population) theoreticalEndYearPopulationsAndFertility[0];
        GENESIS_Population theoreticalDeadPopulation = (GENESIS_Population) theoreticalEndYearPopulationsAndFertility[1];
        System.out.println("Female");
        System.out.println(
                "AgeBoundMinYear, AgeBoundMaxYear, StartPop, ExpectedBirths, "
                + "TheoreticalBirths, ExpectedDeaths, TheoreticalDeaths, "
                + "TheoreticalMidYearPop, TheoreticalEndYearPop");
        ite = startYearPopulation._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound aAgeBound = ite.next();
            long minAgeYear = aAgeBound.getAgeMin().getYear();
            long maxAgeYear = aAgeBound.getAgeMax().getYear();
            BigDecimal theoreticalDeaths = theoreticalDeadPopulation.getFemalePopulation(aAgeBound);
            BigDecimal theoreticalMidYearPop;
            BigDecimal startPop = startYearPopulation._FemaleAgeBoundPopulationCount_TreeMap.get(aAgeBound);
            if (startPop == null) {
                startPop = BigDecimal.ZERO;
            }
            if (minAgeYear == 0) {
                BigDecimal theoreticalBirths = (BigDecimal) theoreticalFertilityAndNewBornPopulation[3];
                BigDecimal expectedBirths = Generic_BigDecimal.divideRoundIfNecessary(
                        birthPopulation.getFemalePopulationTotal(),
                        two, decimalPlacePrecision, roundingMode);
                BigDecimal expectedDeaths = deathPopulation.getFemalePopulation(aAgeBound);
                theoreticalMidYearPop = Generic_BigDecimal.divideRoundIfNecessary(
                        startPop.add(theoreticalBirths).subtract(theoreticalDeaths),
                        two, decimalPlacePrecision, roundingMode);
                BigDecimal endYearPop = theoreticalSurvivedPopulation._FemaleAgeBoundPopulationCount_TreeMap.get(aAgeBound);
                System.out.println(
                        "" + minAgeYear + ", " + maxAgeYear + ", " + startPop + ", "
                        + expectedBirths + ", " + theoreticalBirths + ", "
                        + expectedDeaths + ", " + theoreticalDeaths + ", "
                        + theoreticalMidYearPop + ", " + endYearPop);
            } else {
                BigDecimal endYearPop = theoreticalSurvivedPopulation._FemaleAgeBoundPopulationCount_TreeMap.get(aAgeBound);
                if (endYearPop == null) {
                    endYearPop = BigDecimal.ZERO;
                }
                BigDecimal expectedDeaths = deathPopulation.getFemalePopulation(aAgeBound);
                theoreticalMidYearPop = Generic_BigDecimal.divideRoundIfNecessary(
                        startPop.add(endYearPop), two, decimalPlacePrecision, roundingMode);
                System.out.println(
                        "" + minAgeYear + ", " + maxAgeYear + ", " + startPop + ", "
                        + " 0, 0, "
                        + expectedDeaths + ", " + theoreticalDeaths + ", "
                        + theoreticalMidYearPop + ", " + endYearPop);
            }
            midYearPopulation._FemaleAgeBoundPopulationCount_TreeMap.put(aAgeBound, theoreticalMidYearPop);
        }
        System.out.println("Male");
        System.out.println(
                "AgeBoundMinYear, AgeBoundMaxYear, StartPop, ExpectedBirths, "
                + "TheoreticalBirths, ExpectedDeaths, TheoreticalDeaths, "
                + "TheoreticalMidYearPop, TheoreticalEndYearPop");
        ite = midYearPopulation._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound aAgeBound = ite.next();
            long minAgeYear = aAgeBound.getAgeMin().getYear();
            long maxAgeYear = aAgeBound.getAgeMax().getYear();
            BigDecimal theoreticalDeaths = theoreticalDeadPopulation.getMalePopulation(aAgeBound);
            BigDecimal theoreticalMidYearPop;
            BigDecimal startPop = startYearPopulation._MaleAgeBoundPopulationCount_TreeMap.get(aAgeBound);
            if (startPop == null) {
                startPop = BigDecimal.ZERO;
            }
            if (minAgeYear == 0) {
                BigDecimal theoreticalBirths = (BigDecimal) theoreticalFertilityAndNewBornPopulation[4];
                BigDecimal expectedBirths = Generic_BigDecimal.divideRoundIfNecessary(
                        birthPopulation.getFemalePopulationTotal(),
                        two, decimalPlacePrecision, roundingMode);
                BigDecimal expectedDeaths = deathPopulation.getMalePopulation(aAgeBound);
                theoreticalMidYearPop = Generic_BigDecimal.divideRoundIfNecessary(
                        startPop.add(theoreticalBirths).subtract(theoreticalDeaths),
                        two, decimalPlacePrecision, roundingMode);
                BigDecimal endYearPop = theoreticalSurvivedPopulation._MaleAgeBoundPopulationCount_TreeMap.get(aAgeBound);
                System.out.println(
                        "" + minAgeYear + ", " + maxAgeYear + ", " + startPop + ", "
                        + expectedBirths + ", " + theoreticalBirths + ", "
                        + expectedDeaths + ", " + theoreticalDeaths + ", "
                        + theoreticalMidYearPop + ", " + endYearPop);
            } else {
                BigDecimal endYearPop = theoreticalSurvivedPopulation._MaleAgeBoundPopulationCount_TreeMap.get(aAgeBound);
                if (endYearPop == null) {
                    endYearPop = BigDecimal.ZERO;
                }
                BigDecimal expectedDeaths = deathPopulation.getMalePopulation(aAgeBound);
                theoreticalMidYearPop = Generic_BigDecimal.divideRoundIfNecessary(
                        startPop.add(endYearPop), two, decimalPlacePrecision, roundingMode);
                System.out.println(
                        "" + minAgeYear + ", " + maxAgeYear + ", " + startPop + ", "
                        + " 0, 0, " 
                        + expectedDeaths + ", " + theoreticalDeaths + ", "
                        + theoreticalMidYearPop + ", " + endYearPop);
            }
            midYearPopulation._MaleAgeBoundPopulationCount_TreeMap.put(aAgeBound, theoreticalMidYearPop);
        }
        // Compare the dead populations and birth populations and calculate 
        // re-estimated mortality and fertility.
        // ----------------------------------------------------------------
        // Compare the death populations and calculate reEstimatedMortality
        GENESIS_Mortality reEstimatedMortality = new GENESIS_Mortality(a_GENESIS_Environment);
        reEstimatedMortality._FemaleAnnualMortalityAgeBoundRate_TreeMap =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        // Females
        ite = deathPopulation._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        System.out.println("Female Death");
        System.out.println(
                "AgeBoundMinYear, AgeBoundMaxYear, StartPop, "
                + "EstimatedMidYearPop, EstimatedEndYearPop, "
                + "InitialEstimatedMortalityRate, "
                + "ReEstimatedMortalityRate, "
                + "ExpectedDead, ModelledDead, "
                + "NewBornDead, NewBornSurvivers");
        while (ite.hasNext()) {
            GENESIS_AgeBound aAgeBound = ite.next();
            long aAgeBoundMinYear = aAgeBound.getAgeMin().getYear();
            long aAgeBoundMaxYear = aAgeBound.getAgeMax().getYear();
            BigDecimal startYearPop = startYearPopulation.getFemalePopulationSum(aAgeBoundMinYear, aAgeBoundMaxYear);
            BigDecimal midYearPop = midYearPopulation.getFemalePopulationSum(aAgeBoundMinYear, aAgeBoundMaxYear);
            BigDecimal endYearPop = theoreticalSurvivedPopulation.getFemalePopulationSum(aAgeBoundMinYear, aAgeBoundMaxYear);
            BigDecimal initialEstimatedMortalityRate = initialEstimateOfMortality.getAnnualMortalityFemale(aAgeBound);
            BigDecimal expectedDead = deathPopulation._FemaleAgeBoundPopulationCount_TreeMap.get(aAgeBound);
            BigDecimal modelledDead = BigDecimal.ZERO;
            BigDecimal halfExpectedDead = Generic_BigDecimal.divideRoundIfNecessary(
                    expectedDead, two, decimalPlacePrecision, roundingMode);
//            BigDecimal reEstimatedMortalityRate = Generic_BigDecimal.divideRoundIfNecessary(
//                    expectedDead, midYearPop, decimalPlacePrecision, roundingMode);
//            BigDecimal reEstimatedMortalityRate = Generic_BigDecimal.divideRoundIfNecessary(
//                    expectedDead, endYearPop, decimalPlacePrecision, roundingMode);
            BigDecimal reEstimatedMortalityRate;
            if (halfExpectedDead.compareTo(BigDecimal.ZERO) == 0) {
                reEstimatedMortalityRate = BigDecimal.ZERO;
            } else {
                if (midYearPop.compareTo(BigDecimal.ZERO) == 0) {
                    reEstimatedMortalityRate = BigDecimal.ZERO;
                } else {
                    reEstimatedMortalityRate = Generic_BigDecimal.divideRoundIfNecessary(
                            halfExpectedDead, midYearPop, decimalPlacePrecision, roundingMode);
                }
            }
            for (long ageInYear = aAgeBoundMinYear; ageInYear < aAgeBoundMaxYear; ageInYear++) {
                GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(ageInYear, ageInYear + 1L);
                BigDecimal modelledDeadPart =
                        ((GENESIS_Population) theoreticalEndYearPopulationsAndFertility[1])._FemaleAgeBoundPopulationCount_TreeMap.get(yearAgeBound);
                if (modelledDeadPart != null) {
                    modelledDead = modelledDead.add(modelledDeadPart);
                }
                if (initialEstimatedMortalityRate == null) {
                    initialEstimatedMortalityRate = initialEstimateOfMortality.getAnnualMortalityFemale(yearAgeBound);
                }
                reEstimatedMortality._FemaleAnnualMortalityAgeBoundRate_TreeMap.put(yearAgeBound, reEstimatedMortalityRate);
            }
            if (aAgeBoundMinYear == 0L) {
                BigDecimal newBornDead = (BigDecimal) theoreticalFertilityAndNewBornPopulation[5];
                BigDecimal newBornSurvivers = (BigDecimal) theoreticalFertilityAndNewBornPopulation[3];
                System.out.println(
                        aAgeBoundMinYear + ", " + aAgeBoundMaxYear + ", "
                        + startYearPop + ", " + midYearPop + ", " + endYearPop
                        + ", " + initialEstimatedMortalityRate + ", "
                        + reEstimatedMortalityRate + ", "
                        + expectedDead + ", " + modelledDead + ", "
                        + newBornDead + ", " + newBornSurvivers);
            } else {
                System.out.println(
                        aAgeBoundMinYear + ", " + aAgeBoundMaxYear + ", "
                        + startYearPop + ", " + midYearPop + ", " + endYearPop
                        + ", " + initialEstimatedMortalityRate + ", "
                        + reEstimatedMortalityRate + ", "
                        + expectedDead + ", " + modelledDead + ", NA, NA");
            }
        }
        // Males
        System.out.println("Male Death");
        reEstimatedMortality._MaleAnnualMortalityAgeBoundRate_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        ite = deathPopulation._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        System.out.println(
                "AgeBoundMinYear, AgeBoundMaxYear, StartPop, "
                + "EstimatedMidYearPop, EstimatedEndYearPop, "
                + "InitialEstimatedMortalityRate, "
                + "ReEstimatedMortalityRate, "
                + "ExpectedDead, ModelledDead, "
                + "NewBornDead, NewBornSurvivers");
        while (ite.hasNext()) {
            GENESIS_AgeBound aAgeBound = ite.next();
            long aAgeBoundMinYear = aAgeBound.getAgeMin().getYear();
            long aAgeBoundMaxYear = aAgeBound.getAgeMax().getYear();
            BigDecimal startYearPop = startYearPopulation.getMalePopulationSum(aAgeBoundMinYear, aAgeBoundMaxYear);
            BigDecimal midYearPop = midYearPopulation.getMalePopulationSum(aAgeBoundMinYear, aAgeBoundMaxYear);
            BigDecimal endYearPop = theoreticalSurvivedPopulation.getMalePopulationSum(aAgeBoundMinYear, aAgeBoundMaxYear);
            BigDecimal initialEstimatedMortalityRate = initialEstimateOfMortality.getAnnualMortalityMale(aAgeBound);
            BigDecimal expectedDead = deathPopulation._MaleAgeBoundPopulationCount_TreeMap.get(aAgeBound);
            BigDecimal modelledDead = BigDecimal.ZERO;
            BigDecimal halfExpectedDead = Generic_BigDecimal.divideRoundIfNecessary(
                    expectedDead, two, decimalPlacePrecision, roundingMode);
//            BigDecimal reEstimatedMortalityRate = Generic_BigDecimal.divideRoundIfNecessary(
//                    expectedDead, midYearPop, decimalPlacePrecision, roundingMode);
//            BigDecimal reEstimatedMortalityRate = Generic_BigDecimal.divideRoundIfNecessary(
//                    expectedDead, endYearPop, decimalPlacePrecision, roundingMode);
            BigDecimal reEstimatedMortalityRate;
            if (halfExpectedDead.compareTo(BigDecimal.ZERO) == 0) {
                reEstimatedMortalityRate = BigDecimal.ZERO;
            } else {
                if (midYearPop.compareTo(BigDecimal.ZERO) == 0) {
                    reEstimatedMortalityRate = BigDecimal.ZERO;
                } else {
                    reEstimatedMortalityRate = Generic_BigDecimal.divideRoundIfNecessary(
                            halfExpectedDead, midYearPop, decimalPlacePrecision, roundingMode);
                }
            }
            for (long ageInYear = aAgeBoundMinYear; ageInYear < aAgeBoundMaxYear; ageInYear++) {
                GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(ageInYear, ageInYear + 1L);
                BigDecimal modelledDeadPart =
                        ((GENESIS_Population) theoreticalEndYearPopulationsAndFertility[1])._MaleAgeBoundPopulationCount_TreeMap.get(yearAgeBound);
                if (modelledDeadPart != null) {
                    modelledDead = modelledDead.add(modelledDeadPart);
                }
                if (initialEstimatedMortalityRate == null) {
                    initialEstimatedMortalityRate = initialEstimateOfMortality.getAnnualMortalityMale(yearAgeBound);
                }
                reEstimatedMortality._MaleAnnualMortalityAgeBoundRate_TreeMap.put(yearAgeBound, reEstimatedMortalityRate);
            }
            if (aAgeBoundMinYear == 0L) {
                BigDecimal newBornDead = (BigDecimal) theoreticalFertilityAndNewBornPopulation[6];
                BigDecimal newBornSurvivers = (BigDecimal) theoreticalFertilityAndNewBornPopulation[4];
                System.out.println(
                        aAgeBoundMinYear + ", " + aAgeBoundMaxYear + ", "
                        + startYearPop + ", " + midYearPop + ", " + endYearPop
                        + ", " + initialEstimatedMortalityRate + ", "
                        + reEstimatedMortalityRate + ", "
                        + expectedDead + ", " + modelledDead + ", "
                        + newBornDead + ", " + newBornSurvivers);
            } else {
                System.out.println(
                        aAgeBoundMinYear + ", " + aAgeBoundMaxYear + ", "
                        + startYearPop + ", " + midYearPop + ", " + endYearPop
                        + ", " + initialEstimatedMortalityRate + ", "
                        + reEstimatedMortalityRate + ", "
                        + expectedDead + ", " + modelledDead + ", NA, NA");
            }
        }

        // Compare the birth populations and calculate ReEstimatedFertilityRate
        GENESIS_Fertility reEstimatedFertility = new GENESIS_Fertility(a_GENESIS_Environment);
        reEstimatedFertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        ite = birthPopulation._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        System.out.println("Female Birth");
        System.out.println(
                "AgeBoundMinYear, AgeBoundMaxYear, StartPop, "
                + "EstimatedMidYearPop, EstimatedEndYearPop, "
                + "InitialEstimatedLiveBirthFertilityRate, "
                + "ReEstimatedLiveBirthFertilityRate, "
                + "ExpectedBirths, "
                + "ModelledBirths");
        while (ite.hasNext()) {
            GENESIS_AgeBound aAgeBound = ite.next();
            long aAgeBoundMinYear = aAgeBound.getAgeMin().getYear();
            long aAgeBoundMaxYear = aAgeBound.getAgeMax().getYear();
            BigDecimal startYearPop = startYearPopulation.getFemalePopulationSum(aAgeBoundMinYear, aAgeBoundMaxYear);
            BigDecimal midYearPop = midYearPopulation.getFemalePopulationSum(aAgeBoundMinYear, aAgeBoundMaxYear);
            BigDecimal endYearPop = theoreticalSurvivedPopulation.getFemalePopulationSum(aAgeBoundMinYear, aAgeBoundMaxYear);
            BigDecimal initialEstimatedFertilityRate = initialEstimateOfFertility.getAnnualLiveBirthFertility(aAgeBound);
            BigDecimal expectedBirth = birthPopulation._FemaleAgeBoundPopulationCount_TreeMap.get(aAgeBound);
            BigDecimal modelledBirth = BigDecimal.ZERO;
            BigDecimal reEstimatedFertilityRate = Generic_BigDecimal.divideRoundIfNecessary(
                    expectedBirth, midYearPop, decimalPlacePrecision, roundingMode);
            for (long ageInYear = aAgeBoundMinYear; ageInYear < aAgeBoundMaxYear; ageInYear++) {
                GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(ageInYear, ageInYear + 1L);
                BigDecimal modelledBirthPart =
                        ((TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[2]).get(yearAgeBound);
                if (modelledBirthPart != null) {
                    modelledBirth = modelledBirth.add(modelledBirthPart);
                }
                if (initialEstimatedFertilityRate == null) {
                    initialEstimatedFertilityRate = initialEstimateOfFertility.getAnnualLiveBirthFertility(yearAgeBound);
                }
                reEstimatedFertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap.put(yearAgeBound, reEstimatedFertilityRate);
            }
            System.out.println(
                    aAgeBoundMinYear + ", " + aAgeBoundMaxYear + ", "
                    + startYearPop + ", " + midYearPop + ", " + endYearPop
                    + ", " + initialEstimatedFertilityRate + ", "
                    + reEstimatedFertilityRate + ", "
                    + expectedBirth + ", " + modelledBirth);
        }
        reEstimatedFertility._TwinPregnancyAgeBoundProbability_TreeMap =
                initialEstimateOfFertility._TwinPregnancyAgeBoundProbability_TreeMap;
        reEstimatedFertility._TripletPregnancyAgeBoundProbability_TreeMap =
                initialEstimateOfFertility._TripletPregnancyAgeBoundProbability_TreeMap;
        //theoreticalEndYearPopulationsAndFertility[0];
        result[0] = reEstimatedMortality;
        result[1] = reEstimatedFertility;
        return result;
    }

//    public Object[] getEstimateRates(
//            int decimalPlacePrecision,
//            RoundingMode roundingMode) {
//        return getEstimateRates(
//                _StartYearPopulation,
//                _InitialEstimateOfMortality,
//                _InitialEstimateOfFertility,
//                _ObservedAnnualDeaths,
//                _ObservedAnnualBirthsByAgeOfMother,
//                decimalPlacePrecision,
//                roundingMode);
//    }
    private static void log(Level level, String message) {
        //System.out.println(message);
        getLogger().log(level, message);
    }

//    public static Logger getLogger() {
//        return getLogger();
//    }
    public static Logger getLogger() {
        return GENESIS_Log.logger;
    }
}
