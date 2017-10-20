package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.*;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Female;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Male;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.XMLConverter;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.CommonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.MortalityFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.GenderedAgeBoundRates;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.mortality.MortalityType;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Collections;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;

public class GENESIS_Mortality extends MortalityType implements Serializable {

    protected static final long serialVersionUID = 1L;
    public transient GENESIS_Environment ge;
    /**
     * Used for Logging
     */
    private static final String sourceClass = GENESIS_Mortality.class.getName();
    private static final String sourcePackage = GENESIS_Mortality.class.getPackage().getName();
    //private static final Logger logger = getLogger().getLogger(sourcePackage);
    /**
     * TreeMap copy of femaleAgeProbability;
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _FemaleAnnualMortalityAgeBoundRate_TreeMap;
    /**
     * TreeMap copy of maleAgeProbability;
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _MaleAnnualMortalityAgeBoundRate_TreeMap;
    /**
     * For storing daily mortality probabilities of females
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _FemaleDailyMortalityAgeBoundProbability_TreeMap;
    /**
     * For storing daily mortality probabilities of males
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _MaleDailyMortalityAgeBoundProbability_TreeMap;

    public GENESIS_Mortality() {
        String sourceMethod = "GENESIS_Mortality()";
        getLogger().entering(sourceClass, sourceMethod);
        MortalityFactory.init();
        getLogger().entering(sourceClass, sourceMethod);
    }

    public GENESIS_Mortality(GENESIS_Mortality a_Mortality) {
        this(a_Mortality.ge,
                a_Mortality);
    }

    public GENESIS_Mortality(
            GENESIS_Environment a_GENESIS_Environment,
            MortalityType mortality) {
        String sourceMethod = "GENESIS_Mortality(GENESIS_Environment,MortalityType)";
        getLogger().entering(sourceClass, sourceMethod);
        ge = a_GENESIS_Environment;
        _FemaleAnnualMortalityAgeBoundRate_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Rate(
                mortality.getGenderedAgeBoundRates().getFemale());
        _MaleAnnualMortalityAgeBoundRate_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Rate(
                mortality.getGenderedAgeBoundRates().getMale());
        getGenderedAgeBoundRates().getFemale().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                mortality.getGenderedAgeBoundRates().getFemale()));
        getGenderedAgeBoundRates().getMale().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                mortality.getGenderedAgeBoundRates().getMale()));
        initDailyAgeMortalityTreeMaps();
        getLogger().exiting(sourceClass, sourceMethod);
    }

    public GENESIS_Mortality(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_Mortality a_Mortality) {
        String sourceMethod = "GENESIS_Mortality(GENESIS_Environment,GENESIS_Mortality)";
        getLogger().entering(sourceClass, sourceMethod);
        MortalityFactory.init();
        ge = a_GENESIS_Environment;
        _FemaleAnnualMortalityAgeBoundRate_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(
                a_Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap);
        _MaleAnnualMortalityAgeBoundRate_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(
                a_Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap);
        getGenderedAgeBoundRates().getFemale().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                a_Mortality.getGenderedAgeBoundRates().getFemale()));
        getGenderedAgeBoundRates().getMale().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                a_Mortality.getGenderedAgeBoundRates().getMale()));
        _FemaleDailyMortalityAgeBoundProbability_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(
                a_Mortality._FemaleDailyMortalityAgeBoundProbability_TreeMap);
        _MaleDailyMortalityAgeBoundProbability_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(
                a_Mortality._MaleDailyMortalityAgeBoundProbability_TreeMap);
        getLogger().exiting(sourceClass, sourceMethod);
    }

    /**
     * @param a_GENESIS_Environment
     * @param mortality_File
     */
    public GENESIS_Mortality(
            GENESIS_Environment a_GENESIS_Environment,
            File mortality_File) {
        String sourceMethod = "GENESIS_Mortality(GENESIS_Environment,File)";
        getLogger().entering(sourceClass, sourceMethod);
        MortalityFactory.init();
        ge = a_GENESIS_Environment;
        if (mortality_File.getName().endsWith("csv")) {
            BufferedReader br = null;
            try {
                _FemaleAnnualMortalityAgeBoundRate_TreeMap =
                        new TreeMap<GENESIS_AgeBound, BigDecimal>();
                _MaleAnnualMortalityAgeBoundRate_TreeMap =
                        new TreeMap<GENESIS_AgeBound, BigDecimal>();
                br = Generic_StaticIO.getBufferedReader(mortality_File);
                StreamTokenizer aStreamTokenizer =
                        new StreamTokenizer(br);
                Generic_StaticIO.setStreamTokenizerSyntax1(aStreamTokenizer);
                String line;
                int gender = 0;
                long minimumAgeInYears = 0;
                long maximumAgeInYears = 0;
                BigDecimal probability = null;
                // Skip the first line
                int tokenType = aStreamTokenizer.nextToken();
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                tokenType = aStreamTokenizer.nextToken();
                while (tokenType != StreamTokenizer.TT_EOF) {
                    switch (tokenType) {
                        case StreamTokenizer.TT_EOL:
                            if (gender == 0) {
                                for (long age = minimumAgeInYears; age <= maximumAgeInYears; age++) {
                                    GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
                                    _FemaleAnnualMortalityAgeBoundRate_TreeMap.put(
                                            ageBound,
                                            probability);
                                }
                            } else {
                                for (long age = minimumAgeInYears; age <= maximumAgeInYears; age++) {
                                    GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
                                    _MaleAnnualMortalityAgeBoundRate_TreeMap.put(
                                            ageBound,
                                            probability);
                                }
                            }
//                        Gender_Age a_Gender_Age;
//                        for (int age = minimumAgeInYears; age <= maximumAgeInYears; age++) {
//                            a_Gender_Age = new Gender_Age(gender, age);
//                            _AnnualMortalityProbabilities.put(a_Gender_Age, probability);
//                        }
                            break;
                        case StreamTokenizer.TT_WORD:
                            line = aStreamTokenizer.sval;
                            String[] splitline = line.split(",");
                            gender = new Integer(splitline[0]);
                            minimumAgeInYears = new Integer(splitline[1]);
                            maximumAgeInYears = new Integer(splitline[2]);
                            probability = new BigDecimal(splitline[3]);
                            getLogger().log(Level.FINEST, line);
//                            System.out.println(
//                                    gender + ","
//                                    + minimumAgeInYears + ","
//                                    + maximumAgeInYears + ","
//                                    + probability);
                            break;
                    }
                    tokenType = aStreamTokenizer.nextToken();
                }
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
                    Logger.getLogger(GENESIS_Mortality.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            getGenderedAgeBoundRates().getFemale().addAll(
                    GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                    _FemaleAnnualMortalityAgeBoundRate_TreeMap));
            getGenderedAgeBoundRates().getMale().addAll(
                    GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                    _MaleAnnualMortalityAgeBoundRate_TreeMap));
        } else {
            if (!mortality_File.getName().endsWith("xml")) {
                System.err.println(
                        "System.exit("
                        + GENESIS_ErrorAndExceptionHandler.IOException
                        + ") from " + sourceMethod + " !mortality_File.getName().endsWith(xml)");
                System.exit(Generic_ErrorAndExceptionHandler.IOException);
            }
            MortalityType mortality = XMLConverter.loadMortalityFromXMLFile(mortality_File);
            _FemaleAnnualMortalityAgeBoundRate_TreeMap =
                    GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Rate(
                    mortality.getGenderedAgeBoundRates().getFemale());
            _MaleAnnualMortalityAgeBoundRate_TreeMap =
                    GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Rate(
                    mortality.getGenderedAgeBoundRates().getMale());
            getGenderedAgeBoundRates().getFemale().addAll(
                    GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                    mortality.getGenderedAgeBoundRates().getFemale()));
            getGenderedAgeBoundRates().getMale().addAll(
                    GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                    mortality.getGenderedAgeBoundRates().getMale()));
        }
        initDailyAgeMortalityTreeMaps();
        getLogger().exiting(sourceClass, sourceMethod);
    }

//    public GENESIS_Mortality(
//            GENESIS_Environment a_GENESIS_Environment,
//            XMLConverter a_XMLConverter,
//            File mortality_File) {
//        _GENESIS_Environment = a_GENESIS_Environment;
//        log("<Contruct GENESIS_Mortality>");
//        //_AnnualMortalityProbabilities = new HashMap<Gender_Age, BigDecimal>();
//        _FemaleAnnualMortalityAgeBoundRate_TreeMap = new TreeMap<Integer, BigDecimal>();
//        _MaleAnnualMortalityAgeBoundRate_TreeMap = new TreeMap<Integer, BigDecimal>();
//        if (mortality_File.getName().endsWith("csv")) {
//            try {
//                BufferedReader aBufferedReader =
//                        new BufferedReader(
//                        new InputStreamReader(
//                        new FileInputStream(mortality_File)));
//                StreamTokenizer aStreamTokenizer =
//                        new StreamTokenizer(aBufferedReader);
//                Generic_StaticIO.setStreamTokenizerSyntax1(aStreamTokenizer);
//                String line;
//                int gender = 0;
//                int minimumAgeInYears = 0;
//                int maximumAgeInYears = 0;
//                BigDecimal probability = null;
//                // Skip the first line
//                int tokenType = aStreamTokenizer.nextToken();
//                while (tokenType != StreamTokenizer.TT_EOL) {
//                    tokenType = aStreamTokenizer.nextToken();
//                }
//                tokenType = aStreamTokenizer.nextToken();
//                while (tokenType != StreamTokenizer.TT_EOF) {
//                    switch (tokenType) {
//                        case StreamTokenizer.TT_EOL:
//                            if (gender == 0) {
//                                for (int age = minimumAgeInYears; age <= maximumAgeInYears; age++) {
//                                    _FemaleAnnualMortalityAgeBoundRate_TreeMap.put(age, probability);
//                                }
//                            } else {
//                                for (int age = minimumAgeInYears; age <= maximumAgeInYears; age++) {
//                                    _MaleAnnualMortalityAgeBoundRate_TreeMap.put(age, probability);
//                                }
//                            }
////                        Gender_Age a_Gender_Age;
////                        for (int age = minimumAgeInYears; age <= maximumAgeInYears; age++) {
////                            a_Gender_Age = new Gender_Age(gender, age);
////                            _AnnualMortalityProbabilities.put(a_Gender_Age, probability);
////                        }
//                            break;
//                        case StreamTokenizer.TT_WORD:
//                            line = aStreamTokenizer.sval;
//                            String[] splitline = line.split(",");
//                            gender = new Integer(splitline[0]);
//                            minimumAgeInYears = new Integer(splitline[1]);
//                            maximumAgeInYears = new Integer(splitline[2]);
//                            probability = new BigDecimal(splitline[3]);
//                            log(line);
//                            log(gender + ","
//                                    + minimumAgeInYears + ","
//                                    + maximumAgeInYears + ","
//                                    + probability);
//                            break;
//                    }
//                    tokenType = aStreamTokenizer.nextToken();
//                }
//            } catch (IOException a_IOException) {
//                System.exit(Generic_ErrorAndExceptionHandler.IOException);
//            }
//        } else {
//            if (!mortality_File.getName().endsWith("xml")) {
//                System.exit(Generic_ErrorAndExceptionHandler.IOException);
//            }
//            MortalityBean a_MortalityBean = a_XMLConverter.loadMortalityFromXMLFile(mortality_File);
//            _FemaleAnnualMortalityAgeBoundRate_TreeMap = a_MortalityBean.getFemaleAgeMortality_TreeMap();
//            _MaleAnnualMortalityAgeBoundRate_TreeMap = a_MortalityBean.getMaleAgeMortality_TreeMap();
//        }
//        initDailyAgeMortalityTreeMaps();
//        log("</Contruct GENESIS_Mortality>");
//    }
    public GENESIS_Mortality(
            GENESIS_Environment a_GENESIS_Environment) {
        String sourceMethod = "GENESIS_Mortality(GENESIS_Environment)";
        getLogger().entering(sourceClass, sourceMethod);
        MortalityFactory.init();
        ge = a_GENESIS_Environment;
        init();
        getLogger().exiting(sourceClass, sourceMethod);
    }

    private void init() {
        String sourceMethod = "init()";
        getLogger().entering(sourceClass, sourceMethod);
        LogManager.getLogManager().addLogger(Logger.getLogger(GENESIS_Log.GENESIS_DefaultLoggerName));
        _FemaleDailyMortalityAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        _MaleDailyMortalityAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        _FemaleAnnualMortalityAgeBoundRate_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        _MaleAnnualMortalityAgeBoundRate_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        initDailyAgeMortalityTreeMaps();
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
            GENESIS_Mortality instance = new GENESIS_Mortality();
            instance.ge = new GENESIS_Environment(directory);
            instance.formatData(directory);
            GENESIS_Log.reset();
        } catch (Error e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void formatData(File directory) {
        String sourceMethod = "formatData(File)";
        getLogger().entering(sourceClass, sourceMethod);
        for (int i = 1991; i < 2001; i++) {
            log(Level.FINE, "" + i);
            File a_File = new File(
                    directory.toString()
                    + "/InputData/DemographicData/MortalityRate/MortalityRate_Leeds_" + i + ".csv");
            processCSVtoXML(a_File);
        }
        getLogger().exiting(sourceClass, sourceMethod);
    }

    public void processCSVtoXML(File a_File) {
        String sourceMethod = "processCSVtoXML(File)";
        getLogger().entering(sourceClass, sourceMethod);
        ge._Time = new GENESIS_Time(1991, 0);
        //a_GENESIS_Environment.Directory = new File("C:/Work/Projects/GENESIS/Workspace/");
        ge.Directory = new File("/scratch01/Work/Projects/GENESIS/workspace/");
        String a_Filename_String = a_File.getName();
        String[] a_Filename_String_prefixSuffix = a_Filename_String.split("\\.");
        GENESIS_Mortality a_Mortality = new GENESIS_Mortality(
                ge,
                a_File);
        File b_File = new File(
                a_File.getParentFile(),
                a_Filename_String_prefixSuffix[0] + "YearAges.csv");
        a_Mortality.writeToCSV(b_File);
        GENESIS_Mortality b_Mortality = new GENESIS_Mortality(
                ge,
                b_File);
        File c_File = new File(
                a_File.getParentFile(),
                a_Filename_String_prefixSuffix[0] + ".xml");
        XMLConverter.saveMortalityToXMLFile(
                c_File,
                b_Mortality);
        GENESIS_Mortality c_Mortality = new GENESIS_Mortality(
                ge,
                c_File);
        log(Level.FINE, c_Mortality.toString());
        getLogger().exiting(sourceClass, sourceMethod);
    }

    @Override
    public final GenderedAgeBoundRates getGenderedAgeBoundRates() {
        if (super.getGenderedAgeBoundRates() == null) {
            genderedAgeBoundRates = CommonFactory.newGenderedAgeBoundRates();
        }
        return genderedAgeBoundRates;
    }

    /**
     * updates genderAgeBoundRates using
     * _FemaleAnnualMortalityAgeBoundRate_TreeMap and
     * _MaleAnnualMortalityAgeBoundRate_TreeMap
     */
    public final void updateGenderedAgeBoundRates() {
        GenderedAgeBoundRates thisGenderedAgeBoundRates = getGenderedAgeBoundRates();
        thisGenderedAgeBoundRates.getFemale().clear();
        thisGenderedAgeBoundRates.getFemale().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                _FemaleAnnualMortalityAgeBoundRate_TreeMap));
        thisGenderedAgeBoundRates.getMale().clear();
        thisGenderedAgeBoundRates.getMale().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                _MaleAnnualMortalityAgeBoundRate_TreeMap));
    }

    public void init_DailyAgeMortalityFemale_TreeMap() {
        double annualMortalityProbability_double;
        double annualSurvivalProbability_double;
        double dailyMortalityProbability_double;
        double dailySurvivalProbability_double;
        double exponent;
        BigDecimal doubleComparison_BigDecimal;
        _FemaleDailyMortalityAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        Iterator<GENESIS_AgeBound> a_Iterator = _FemaleAnnualMortalityAgeBoundRate_TreeMap.keySet().iterator();
        BigDecimal annualMortalityProbability_BigDecimal;
        BigDecimal dailyMortalityProbability_BigDecimal;
        BigDecimal dailySurvivalProbability_BigDecimal;
        GENESIS_AgeBound ageBound;
        int normalDaysInYear_int = GENESIS_Time.NormalDaysInYear_int;
        while (a_Iterator.hasNext()) {
            ageBound = a_Iterator.next();
            annualMortalityProbability_BigDecimal = _FemaleAnnualMortalityAgeBoundRate_TreeMap.get(ageBound);
            if (annualMortalityProbability_BigDecimal != null) {
                if (annualMortalityProbability_BigDecimal.compareTo(BigDecimal.ZERO) != 0) {
                    if (annualMortalityProbability_BigDecimal.compareTo(BigDecimal.ONE) == 1) {
                        // @TODO This needs a logic check...
                        // Something about this is right, but then why 
                        // distinguish this rate from other rates? Perhaps a 
                        // new method for converting from output rates is 
                        // needed...
                        getLogger().log(
                                Level.FINE,
                                "annualMortalityProbability_BigDecimal is greater than one "
                                + "most likely because it was calculated as "
                                + "((no of dead) / ((no of days) / (no of days in year)) "
                                + "consequently setting ");
                        BigDecimal dailyMortality =
                                Generic_BigDecimal.divideRoundIfNecessary(
                                annualMortalityProbability_BigDecimal,
                                GENESIS_Time.NormalDaysInYear_BigInteger,
                                GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                                GENESIS_Environment.RoundingModeForPopulationProbabilities);
                        _FemaleDailyMortalityAgeBoundProbability_TreeMap.put(ageBound, dailyMortality);
                    } else {
                        //annualMortalityProbability_BigDecimal = new BigDecimal("0.1"); // For testing
                        annualMortalityProbability_double = annualMortalityProbability_BigDecimal.doubleValue();
                        annualSurvivalProbability_double = 1.0d - annualMortalityProbability_double;

// For comparing annualMortalityProbability_BigDecimal and annualMortalityProbability_double
//            doubleComparison_BigDecimal = new BigDecimal("" + annualMortalityProbability_double);
//            log(Level.FINEST, 
//                    "annualMortalityProbability_BigDecimal " + annualMortalityProbability_BigDecimal);
//            log(Level.FINEST, 
//                    "annualMortalityProbability_double " + annualMortalityProbability_double);
//            log(Level.FINEST, 
//                    "Difference between annualMortalityProbability_BigDecimal and annualMortalityProbability_double "
//                    + doubleComparison_BigDecimal.subtract(annualMortalityProbability_BigDecimal));
// Currently (2011-12-02) the Generic_BigDecimal.power method throws an UnsupportedOperationException
//            BigDecimal dayFractionOfYear = BigDecimal.ONE.divide(
//                    new BigDecimal("" + normalDaysInYear_int),
//                    GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    GENESIS_Environment.RoundingModeForPopulationProbabilities);
//            dailySurvivalProbability_BigDecimal = Generic_BigDecimal.power(
//                    annualMortalityProbability_BigDecimal,
//                    dayFractionOfYear,
//                    GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    GENESIS_Environment.RoundingModeForPopulationProbabilities);
//            System.out.println(
//                    "dailySurvivalProbability_BigDecimal " + dailySurvivalProbability_BigDecimal);
// Computationally expensive but returns higher precision probablity at this stage (2011-12-02), this greater precision is deemed not to be worth it.
//             dailySurvivalProbability_BigDecimal = Generic_BigDecimal.rootRoundIfNecessary(
//                    annualMortalityProbability_BigDecimal,
//                    normalDaysInYear_int,
//                    GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    GENESIS_Environment.RoundingModeForPopulationProbabilities);
//            log(Level.FINEST,
//                    "dailySurvivalProbability_BigDecimal " + dailySurvivalProbability_BigDecimal);
//            dailyMortalityProbability_BigDecimal =
//                    BigDecimal.ONE.subtract(dailySurvivalProbability_BigDecimal);
// Testing and reporting code to see if BigDecimal Precision makes a difference to double precision...
                        exponent = 1.0d / (double) normalDaysInYear_int;
                        //System.out.println("exponent " + exponent);
                        dailySurvivalProbability_double = Math.pow(
                                annualSurvivalProbability_double,
                                exponent);
                        log(Level.FINEST, "dailySurvivalProbability_double " + dailySurvivalProbability_double);
                        dailyMortalityProbability_double = 1.0d - dailySurvivalProbability_double;

                        dailyMortalityProbability_BigDecimal = new BigDecimal(dailyMortalityProbability_double);

//            System.out.println("dailyMortalityProbability_double " + dailyMortalityProbability_double);
//            double survivors_double = 1000000d;
//            // Print out the expected survival for each day with no additional population
//            // Calculation with double numbers
//            for (int i = 0; i < 365; i++) {
//                survivors_double = survivors_double * dailySurvivalProbability_double;
//                System.out.println("" + survivors_double + " survivors on day " + i);
//            }
//            // Calculation with BigDecimal numbers
//            BigDecimal survival_BigDecimal = new BigDecimal("1000000");
//            for (int i = 0; i < 365; i++) {
//                //survival_BigDecimal = survival_BigDecimal.multiply(dailySurvivalProbability_BigDecimal);
//                survival_BigDecimal = Generic_BigDecimal.multiplyRoundIfNecessary(
//                        survival_BigDecimal,
//                        dailySurvivalProbability_BigDecimal,
//                        GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                        GENESIS_Environment.RoundingModeForPopulationProbabilities);
//                System.out.println("" + survival_BigDecimal + " survivors on day " + i);
//            }
//
//            // Another way to get to the root 
//            dailySurvivalProbability_double = 1.0d - annualMortalityProbability_double;
//            for (int i = 1; i < normalDaysInYear_int; i *= 2) {
//                dailySurvivalProbability_double = Math.sqrt(dailySurvivalProbability_double);
//                System.out.println(
//                        "dailySurvivalProbability_double " + dailySurvivalProbability_double);
//            }
//
//            System.out.println(
//                    "dailySurvivalProbability_double " + dailySurvivalProbability_double);
//            dailyMortalityProbability_double = 1.0d - dailySurvivalProbability_double;
//            System.out.println(
//                    "dailyMortalityProbability_double " + dailyMortalityProbability_double);
//
//            doubleComparison_BigDecimal = new BigDecimal("" + dailyMortalityProbability_double);
//            System.out.println(
//                    "dailyMortalityProbability_BigDecimal " + dailyMortalityProbability_BigDecimal);
//            System.out.println(
//                    "dailySurvivalProbability_BigDecimal " + dailySurvivalProbability_BigDecimal);
//            System.out.println(
//                    "Difference between dailyMortalityProbability_BigDecimal "
//                    + "and dailyMortalityProbability_double "
//                    + doubleComparison_BigDecimal.subtract(dailyMortalityProbability_BigDecimal));
                        log(Level.FINEST, "dailyMortalityProbability " + dailyMortalityProbability_BigDecimal);
                        _FemaleDailyMortalityAgeBoundProbability_TreeMap.put(ageBound, dailyMortalityProbability_BigDecimal.stripTrailingZeros());
                    }
                } else {
                    _FemaleDailyMortalityAgeBoundProbability_TreeMap.put(ageBound, BigDecimal.ZERO);
                }
            } else {
                _FemaleDailyMortalityAgeBoundProbability_TreeMap.put(ageBound, BigDecimal.ZERO);
            }
        }
    }

    public void init_DailyAgeMortalityMale_TreeMap() {
        _MaleDailyMortalityAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        Iterator<GENESIS_AgeBound> a_Iterator = _MaleAnnualMortalityAgeBoundRate_TreeMap.keySet().iterator();
        BigDecimal annualMortalityProbability_BigDecimal;
        BigDecimal dailyMortalityProbability_BigDecimal;
        double annualMortalityProbability_double;
        double annualSurvivalProbability_double;
        double dailyMortalityProbability_double;
        double dailySurvivalProbability_double;
        double exponent;
        GENESIS_AgeBound ageBound;
        int normalDaysInYear_int = GENESIS_Time.NormalDaysInYear_int;
        while (a_Iterator.hasNext()) {
            ageBound = a_Iterator.next();
            annualMortalityProbability_BigDecimal = _MaleAnnualMortalityAgeBoundRate_TreeMap.get(ageBound);
            if (annualMortalityProbability_BigDecimal != null) {
                if (annualMortalityProbability_BigDecimal.compareTo(BigDecimal.ZERO) != 0) {
                    if (annualMortalityProbability_BigDecimal.compareTo(BigDecimal.ONE) == 1) {
                        // @TODO This needs a logic check...
                        // Something about this is right, but then why 
                        // distinguish this rate from other rates? Perhaps a 
                        // new method for converting from output rates is 
                        // needed...
                        getLogger().log(
                                Level.FINE,
                                "annualMortalityProbability_BigDecimal is greater than one "
                                + "most likely because it was calculated as "
                                + "((no of dead) / ((no of days) / (no of days in year)) "
                                + "consequently setting ");
                        BigDecimal dailyMortality =
                                Generic_BigDecimal.divideRoundIfNecessary(
                                annualMortalityProbability_BigDecimal,
                                GENESIS_Time.NormalDaysInYear_BigInteger,
                                GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                                GENESIS_Environment.RoundingModeForPopulationProbabilities);
                        _MaleDailyMortalityAgeBoundProbability_TreeMap.put(ageBound, dailyMortality);
                    } else {
                        //annualMortalityProbability_BigDecimal = new BigDecimal("0.1"); // For testing
                        annualMortalityProbability_double = annualMortalityProbability_BigDecimal.doubleValue();
                        annualSurvivalProbability_double = 1.0d - annualMortalityProbability_double;
// For comparing annualMortalityProbability_BigDecimal and annualMortalityProbability_double
//            doubleComparison_BigDecimal = new BigDecimal("" + annualMortalityProbability_double);
//            log(Level.FINEST, 
//                    "annualMortalityProbability_BigDecimal " + annualMortalityProbability_BigDecimal);
//            log(Level.FINEST, 
//                    "annualMortalityProbability_double " + annualMortalityProbability_double);
//            log(Level.FINEST, 
//                    "Difference between annualMortalityProbability_BigDecimal and annualMortalityProbability_double "
//                    + doubleComparison_BigDecimal.subtract(annualMortalityProbability_BigDecimal));
// Currently (2011-12-02) the Generic_BigDecimal.power method throws an UnsupportedOperationException
//            BigDecimal dayFractionOfYear = BigDecimal.ONE.divide(
//                    new BigDecimal("" + normalDaysInYear_int),
//                    GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    GENESIS_Environment.RoundingModeForPopulationProbabilities);
//            dailySurvivalProbability_BigDecimal = Generic_BigDecimal.power(
//                    annualMortalityProbability_BigDecimal,
//                    dayFractionOfYear,
//                    GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    GENESIS_Environment.RoundingModeForPopulationProbabilities);
//            System.out.println(
//                    "dailySurvivalProbability_BigDecimal " + dailySurvivalProbability_BigDecimal);
// Computationally expensive but returns higher precision probablity at this stage (2011-12-02), this greater precision is deemed not to be worth it.
//             dailySurvivalProbability_BigDecimal = Generic_BigDecimal.rootRoundIfNecessary(
//                    annualMortalityProbability_BigDecimal,
//                    normalDaysInYear_int,
//                    GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    GENESIS_Environment.RoundingModeForPopulationProbabilities);
//            log(Level.FINEST,
//                    "dailySurvivalProbability_BigDecimal " + dailySurvivalProbability_BigDecimal);
//            dailyMortalityProbability_BigDecimal =
//                    BigDecimal.ONE.subtract(dailySurvivalProbability_BigDecimal);
// Testing and reporting code to see if BigDecimal Precision makes a difference to double precision...
                        exponent = 1.0d / (double) normalDaysInYear_int;
                        //System.out.println("exponent " + exponent);
                        dailySurvivalProbability_double = Math.pow(
                                annualSurvivalProbability_double,
                                exponent);
                        log(Level.FINEST, "dailySurvivalProbability_double " + dailySurvivalProbability_double);
                        dailyMortalityProbability_double = 1.0d - dailySurvivalProbability_double;
                        try {
                            dailyMortalityProbability_BigDecimal = new BigDecimal(dailyMortalityProbability_double);
                        } catch (NumberFormatException e) {
                            //if (dailyMortalityProbability_double == Double.NaN) {
                            int debug = 1;
                            //}
                            //if (dailyMortalityProbability_double == Double.NEGATIVE_INFINITY || ) {
                            //    int debug = 1;
                            //}
                        }

                        dailyMortalityProbability_BigDecimal = new BigDecimal(dailyMortalityProbability_double);

//            System.out.println("dailyMortalityProbability_double " + dailyMortalityProbability_double);
//            double survivors_double = 1000000d;
//            // Print out the expected survival for each day with no additional population
//            // Calculation with double numbers
//            for (int i = 0; i < 365; i++) {
//                survivors_double = survivors_double * dailySurvivalProbability_double;
//                System.out.println("" + survivors_double + " survivors on day " + i);
//            }
//            // Calculation with BigDecimal numbers
//            BigDecimal survival_BigDecimal = new BigDecimal("1000000");
//            for (int i = 0; i < 365; i++) {
//                //survival_BigDecimal = survival_BigDecimal.multiply(dailySurvivalProbability_BigDecimal);
//                survival_BigDecimal = Generic_BigDecimal.multiplyRoundIfNecessary(
//                        survival_BigDecimal,
//                        dailySurvivalProbability_BigDecimal,
//                        GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                        GENESIS_Environment.RoundingModeForPopulationProbabilities);
//                System.out.println("" + survival_BigDecimal + " survivors on day " + i);
//            }
//
//            // Another way to get to the root 
//            dailySurvivalProbability_double = 1.0d - annualMortalityProbability_double;
//            for (int i = 1; i < normalDaysInYear_int; i *= 2) {
//                dailySurvivalProbability_double = Math.sqrt(dailySurvivalProbability_double);
//                System.out.println(
//                        "dailySurvivalProbability_double " + dailySurvivalProbability_double);
//            }
//
//            System.out.println(
//                    "dailySurvivalProbability_double " + dailySurvivalProbability_double);
//            dailyMortalityProbability_double = 1.0d - dailySurvivalProbability_double;
//            System.out.println(
//                    "dailyMortalityProbability_double " + dailyMortalityProbability_double);
//
//            doubleComparison_BigDecimal = new BigDecimal("" + dailyMortalityProbability_double);
//            System.out.println(
//                    "dailyMortalityProbability_BigDecimal " + dailyMortalityProbability_BigDecimal);
//            System.out.println(
//                    "dailySurvivalProbability_BigDecimal " + dailySurvivalProbability_BigDecimal);
//            System.out.println(
//                    "Difference between dailyMortalityProbability_BigDecimal "
//                    + "and dailyMortalityProbability_double "
//                    + doubleComparison_BigDecimal.subtract(dailyMortalityProbability_BigDecimal));
                        log(Level.FINEST, "dailyMortalityProbability " + dailyMortalityProbability_BigDecimal);
                        _MaleDailyMortalityAgeBoundProbability_TreeMap.put(ageBound, dailyMortalityProbability_BigDecimal.stripTrailingZeros());
                    }
                } else {
                    _MaleDailyMortalityAgeBoundProbability_TreeMap.put(ageBound, BigDecimal.ZERO);
                }
            } else {
                _MaleDailyMortalityAgeBoundProbability_TreeMap.put(ageBound, BigDecimal.ZERO);
            }
        }
    }

    public final void initDailyAgeMortalityTreeMaps() {
        init_DailyAgeMortalityFemale_TreeMap();
        init_DailyAgeMortalityMale_TreeMap();
    }

    public BigDecimal getAnnualMortality(
            GENESIS_Female female) {
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(female.getAge().getAgeInYears());
        return getAnnualMortalityFemale(ageBound);
    }

    public BigDecimal getAnnualMortalityFemale(
            GENESIS_AgeBound ageBound) {
        if (_FemaleAnnualMortalityAgeBoundRate_TreeMap == null) {
            init_DailyAgeMortalityFemale_TreeMap();
        }
        return getRate(
                ageBound,
                _FemaleAnnualMortalityAgeBoundRate_TreeMap);
    }

    public static BigDecimal getRate(
            GENESIS_AgeBound ageBound,
            TreeMap<GENESIS_AgeBound, BigDecimal> m) {
        BigDecimal result = m.get(ageBound);
        if (result == null) {
            long ageBoundMinYear = ageBound.getAgeMin().getYear();
            if (ageBoundMinYear < m.firstKey().getAgeMin().getYear()) {
                GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(ageBoundMinYear + 1);
                return getRate(newAgeBound, m);
            } else {
                if (ageBoundMinYear > m.lastKey().getAgeMin().getYear()) {
                    GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(ageBoundMinYear - 1);
                    return getRate(newAgeBound, m);
                }
            }
        }
        return result;
    }

    public BigDecimal getAnnualMortality(
            GENESIS_Male male) {
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(male.getAge().getAgeInYears());
        return getAnnualMortalityMale(ageBound);
    }

    public BigDecimal getAnnualMortalityMale(
            GENESIS_AgeBound ageBound) {
        if (_MaleAnnualMortalityAgeBoundRate_TreeMap == null) {
            init_DailyAgeMortalityMale_TreeMap();
        }
        return getRate(
                ageBound,
                _MaleAnnualMortalityAgeBoundRate_TreeMap);
    }

    public BigDecimal getDailyMortality(
            GENESIS_Female female) {
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(
                female.get_Age().getAgeInYears_long(ge._Time));
        //GENESIS_AgeBound ageBound = new GENESIS_AgeBound(a_Female.getAge().getAgeInYears());
        return getDailyMortalityFemale(ageBound);
    }

    public BigDecimal getDailyMortalityFemale(
            GENESIS_AgeBound ageBound) {
        if (_FemaleDailyMortalityAgeBoundProbability_TreeMap == null) {
            init_DailyAgeMortalityFemale_TreeMap();
        }
        return getRate(
                ageBound,
                _FemaleDailyMortalityAgeBoundProbability_TreeMap);
    }

    public BigDecimal getDailyMortality(
            GENESIS_Male male) {
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(
                male.get_Age().getAgeInYears_long(ge._Time));
        //GENESIS_AgeBound ageBound = new GENESIS_AgeBound(a_Male.getAge().getAgeInYears());
        return getDailyMortalityMale(ageBound);
    }

    public BigDecimal getDailyMortalityMale(
            GENESIS_AgeBound ageBound) {
        if (_MaleDailyMortalityAgeBoundProbability_TreeMap == null) {
            init_DailyAgeMortalityMale_TreeMap();
        }
        return getRate(
                ageBound,
                _MaleDailyMortalityAgeBoundProbability_TreeMap);
    }

    public void writeToXML(File file) {
        XMLConverter.saveMortalityToXMLFile(file, this);
    }

    public void writeToCSV(File file) {
        PrintWriter a_PrintWriter = null;
        try {
            a_PrintWriter = new PrintWriter(new FileOutputStream(file));
            String header = "Gender,MinumumAgeInYears,MaximumAgeInYears,AnnualMortalityProbability";
            a_PrintWriter.println(header);
            BigDecimal probability;
            Iterator<GENESIS_AgeBound> ite;
            GENESIS_AgeBound ageBound;
            int gender;
            gender = 0;
            ite = _FemaleAnnualMortalityAgeBoundRate_TreeMap.keySet().iterator();
            while (ite.hasNext()) {
                ageBound = ite.next();
                probability = (BigDecimal) _FemaleAnnualMortalityAgeBoundRate_TreeMap.get(ageBound);
                a_PrintWriter.println(
                        "" + gender + ","
                        + ageBound.getAgeMin().getYear() + ","
                        + ageBound.getAgeMax().getYear() + ","
                        + probability.toPlainString());
            }
            gender = 1;
            ite = _MaleAnnualMortalityAgeBoundRate_TreeMap.keySet().iterator();
            while (ite.hasNext()) {
                ageBound = ite.next();
                probability = (BigDecimal) _MaleAnnualMortalityAgeBoundRate_TreeMap.get(ageBound);
                a_PrintWriter.println(
                        "" + gender + ","
                        + ageBound.getAgeMin().getYear() + ","
                        + ageBound.getAgeMax().getYear() + ","
                        + probability.toPlainString());
            }
            a_PrintWriter.close();
        } catch (FileNotFoundException e) {
            System.err.println("Tring to handle " + e.getLocalizedMessage());
            System.err.println("Wait for 2 seconds then trying again to writeToCSV.");
            // This can happen because of too many open files.
            // Try waiting for 2 seconds and then repeating...
            try {
                synchronized (file) {
                    file.wait(2000L);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
            writeToCSV(file);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            log(Level.SEVERE,
                    "Exception in " + this.getClass().getName()
                    + ".writeToCSV(File)");
        } finally {
            a_PrintWriter.close();
        }
    }

//    public int getDecimalPlacePrecision() {
//        return GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities;
//    }
//
//    private RoundingMode getRoundingMode() {
//        return GENESIS_Environment.RoundingModeForPopulationProbabilities;
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
