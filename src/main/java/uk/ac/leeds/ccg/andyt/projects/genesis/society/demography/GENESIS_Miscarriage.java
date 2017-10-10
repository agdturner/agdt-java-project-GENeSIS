package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.XMLConverter;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.MiscarriageFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBoundRate;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.miscarriage.MiscarriageType;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Collections;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;

/**
 * A class for storing and handling information about Miscarriage in a
 * demographic simulation. In the first implementation, only two values for
 * miscarriage were used: Early Pregnancy Loss (EPL) rate - the proportion of
 * pregnancies that were lost in the first 42 days of pregnancy (early models
 * ran with a value of 0.25 or 1 in 4); and, Clinical Miscarriage (CM) rate -
 * the proportion of pregnancies that were lost from 42 days up to pregnancy
 * term (early models ran with a value of 0.08 or 2 in 25 - which now seems
 * somewhat high). (In the initial implementation, pregnancy term was fixed at
 * 266 days when in reality it varies about this date).
 *
 * For more information about miscarriage see:
 * http://en.wikipedia.org/wiki/Miscarriage (Accessed on 2010-04-23)
 * http://en.wikipedia.org/w/index.php?title=Miscarriage&oldid=384978346
 *
 * GENESIS_Miscarriage extends MiscarriageType which handles the XML input and
 * output. GENESIS_Miscarriage holds other fields and methods that are used in
 * simulation models. The class is serializable so that the entire instance can
 * swapped to and from disk.
 */
public class GENESIS_Miscarriage extends MiscarriageType implements Serializable {

    /*
     * serialVersionUID is a version identifier for controlling serialisation.
     * If a serialised instance is on disk it will only load in here if the 
     * serialVersionUID is the same. 
     */
    static final long serialVersionUID = 1L;
    /**
     * Used for Logging
     */
    private static final String sourceClass = GENESIS_Miscarriage.class.getName();
    private static final String sourcePackage = GENESIS_Miscarriage.class.getPackage().getName();
    /**
     * GENESIS_Environment reference
     */
    public transient GENESIS_Environment _GENESIS_Environment;
    /**
     * For storing the number of days expected in early stage pregnancy. In
     * first implementation, this value was fixed at 42 days.
     */
    public static int _NumberOfDaysExpectedInPregnancyStageLate_int;
    /**
     * For storing ageBound specific probabilities. The keys are ages in years
     * and the values represent the rate of a pregnant female in early stage
     * pregnancy (&lt;42 days) experiencing a miscarriage on a day.
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _DailyEarlyPregnancyLossAgeBoundProbability_TreeMap;
    /**
     * For storing ageBound specific probabilities. The keys are ages in years
     * and the values represent the rate of a pregnant female in late stage
     * pregnancy (42 to 266 days) experiencing a miscarriage on a day.
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _DailyClinicalMiscarriageAgeBoundProbability_TreeMap;

    public GENESIS_Miscarriage() {
        String sourceMethod = "GENESIS_Miscarriage()";
        getLogger().entering(sourceClass, sourceMethod);
        MiscarriageFactory.init();
        getLogger().exiting(sourceClass, sourceMethod);
    }

    public GENESIS_Miscarriage(
            GENESIS_Environment a_GENESIS_Environment) {
        this._GENESIS_Environment = a_GENESIS_Environment;
    }

    public GENESIS_Miscarriage(GENESIS_Miscarriage a_Miscarriage) {
        this(a_Miscarriage._GENESIS_Environment,
                a_Miscarriage);
    }

    public GENESIS_Miscarriage(
            GENESIS_Environment a_GENESIS_Environment,
            MiscarriageType a_MiscarriageType) {
        String sourceMethod = "GENESIS_Miscarriage(GENESIS_Environment,MiscarriageType)";
        getLogger().entering(sourceClass, sourceMethod);
        getClinicalMiscarriageAgeRate().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                a_MiscarriageType.getClinicalMiscarriageAgeRate()));
        getEarlyPregnancyLossAgeRate().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                a_MiscarriageType.getEarlyPregnancyLossAgeRate()));
        setExpectedNumberOfDaysInFullTermPregnancy(
                a_MiscarriageType.getExpectedNumberOfDaysInFullTermPregnancy());
        setNumberOfDaysInEarlyPregnancy(
                a_MiscarriageType.getNumberOfDaysInEarlyPregnancy());
        init_DailyClinicalMiscarriageProbability_TreeMap();
        init_DailyEarlyPregnancyLossProbability_TreeMap();
        init_NumberOfDaysInLateFullTermPregnancy_int();
        getLogger().exiting(sourceClass, sourceMethod);
    }

    /**
     * @param a_GENESIS_Environment
     * @param miscarriage_File
     */
    public GENESIS_Miscarriage(
            GENESIS_Environment a_GENESIS_Environment,
            File miscarriage_File) {
        String sourceMethod = "GENESIS_Miscarriage(GENESIS_Environment,File)";
        getLogger().entering(sourceClass, sourceMethod);
        MiscarriageFactory.init();
        _GENESIS_Environment = a_GENESIS_Environment;
        if (miscarriage_File.getName().endsWith("csv")) {
            /**
                 * The file is expected to be along the following lines: Number
                 * of Days in Full Term Pregnancy 266 Number of Days in Early
                 * Pregnancy 42 Early Pregnancy Loss Probability
                 * Gender,MinumumAgeInYears,MaximumAgeInYears,AnnualFertilityProbability
                 * 0,15,30,0.25 0,31,40,0.3 0,41,44,0.5 Clinical Miscarriage
                 * Probability
                 * Gender,MinumumAgeInYears,MaximumAgeInYears,AnnualFertilityProbability
                 * 0,15,30,0.08 0,31,40,0.09 0,41,44,0.1
                 */
            BufferedReader br = null;
            try {
                br = Generic_StaticIO.getBufferedReader(miscarriage_File);
                StreamTokenizer aStreamTokenizer = new StreamTokenizer(br);
                Generic_StaticIO.setStreamTokenizerSyntax1(aStreamTokenizer);
                String line;
                int gender = 0;
                long minimumAgeInYears = 0;
                long maximumAgeInYears = 0;
                BigDecimal rate = null;
                int tokenType;
                // Set expectedNumberOfDaysInFullTermPregnancy ...
                // ... read a line ...
                tokenType = aStreamTokenizer.nextToken();
                line = aStreamTokenizer.sval;
                //System.out.println(line);
                log(Level.FINE, line);
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                // ... read another line ...
                tokenType = aStreamTokenizer.nextToken();
                line = aStreamTokenizer.sval;
                //System.out.println(line);
                log(Level.FINE, line);
                this.expectedNumberOfDaysInFullTermPregnancy = new Integer(line);
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                // Set numberOfDaysInEarlyPregnancy ...
                // ... read a line ...
                tokenType = aStreamTokenizer.nextToken();
                line = aStreamTokenizer.sval;
                //System.out.println(line);
                log(Level.FINE, line);
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                // ... read another line ...
                tokenType = aStreamTokenizer.nextToken();
                line = aStreamTokenizer.sval;
                //System.out.println(line);
                log(Level.FINE, line);
                this.numberOfDaysInEarlyPregnancy = new Integer(line);
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                init_NumberOfDaysInLateFullTermPregnancy_int();
                // Set earlyPregnancyLossAgeProbability ...
                // ... read a line ...
                tokenType = aStreamTokenizer.nextToken();
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                // ... read another line ...
                tokenType = aStreamTokenizer.nextToken();
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                tokenType = aStreamTokenizer.nextToken();
                boolean earlyPregnancyLossAgeProbabilityListSet = false;
                while (!earlyPregnancyLossAgeProbabilityListSet) {
                    switch (tokenType) {
                        case StreamTokenizer.TT_EOL:
                            if (gender == 0) {
                                for (long age = minimumAgeInYears; age <= maximumAgeInYears; age++) {
                                    AgeBoundRate ageBoundRate = new AgeBoundRate();
                                    AgeBound ageBound = new GENESIS_AgeBound(age);
                                    ageBoundRate.setAgeBound(ageBound);
                                    ageBoundRate.setRate(rate);
                                    getEarlyPregnancyLossAgeRate().add(ageBoundRate);
                                }
                            }
                            break;
                        case StreamTokenizer.TT_WORD:
                            line = aStreamTokenizer.sval;
                            if (line.equalsIgnoreCase("Clinical Miscarriage Rate")) {
                                earlyPregnancyLossAgeProbabilityListSet = true;
                            } else {
                                String[] splitline = line.split(",");
                                gender = new Integer(splitline[0]);
                                minimumAgeInYears = new Integer(splitline[1]);
                                maximumAgeInYears = new Integer(splitline[2]);
                                rate = new BigDecimal(splitline[3]);
                                //log(line);
                                log(Level.FINE,
                                        gender + ","
                                        + minimumAgeInYears + ","
                                        + maximumAgeInYears + ","
                                        + rate);
                            }
                            break;
                    }
                    tokenType = aStreamTokenizer.nextToken();
                }
                // Set clinicalMiscarriageAgeProbability
                // ... read a line ...
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                // ... read another line ...
                tokenType = aStreamTokenizer.nextToken();
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                tokenType = aStreamTokenizer.nextToken();
                while (tokenType != StreamTokenizer.TT_EOF) {
                    switch (tokenType) {
                        case StreamTokenizer.TT_EOL:
                            if (gender == 0) {
                                for (long age = minimumAgeInYears; age <= maximumAgeInYears; age++) {
                                    AgeBoundRate ageBoundRate = new AgeBoundRate();
                                    AgeBound ageBound = new GENESIS_AgeBound(age);
                                    ageBoundRate.setAgeBound(ageBound);
                                    ageBoundRate.setRate(rate);
                                    getClinicalMiscarriageAgeRate().add(ageBoundRate);
                                }
                            }
                            break;
                        case StreamTokenizer.TT_WORD:
                            line = aStreamTokenizer.sval;
//                            if (line.equalsIgnoreCase(line)) {
//                                earlyPregnancyLossAgeProbabilityListSet = true;
//                            }
                            String[] splitline = line.split(",");
                            gender = new Integer(splitline[0]);
                            minimumAgeInYears = new Long(splitline[1]);
                            maximumAgeInYears = new Long(splitline[2]);
                            rate = new BigDecimal(splitline[3]);
                            //log(line);
                            log(Level.FINE,
                                    gender + ","
                                    + minimumAgeInYears + ","
                                    + maximumAgeInYears + ","
                                    + rate);
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
                    Logger.getLogger(GENESIS_Fertility.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            if (!miscarriage_File.getName().endsWith("xml")) {
                System.err.println(
                        "System.exit("
                        + GENESIS_ErrorAndExceptionHandler.IOException
                        + ") from " + sourceMethod + " !miscarriage_File.getName().endsWith(xml)");
                System.exit(Generic_ErrorAndExceptionHandler.IOException);
            }
            MiscarriageType miscarriage = XMLConverter.loadMiscarriageFromXMLFile(miscarriage_File);
            getClinicalMiscarriageAgeRate().addAll(
                    GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                    miscarriage.getClinicalMiscarriageAgeRate()));
            getEarlyPregnancyLossAgeRate().addAll(
                    GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                    miscarriage.getEarlyPregnancyLossAgeRate()));
            setExpectedNumberOfDaysInFullTermPregnancy(
                    miscarriage.getExpectedNumberOfDaysInFullTermPregnancy());
            setNumberOfDaysInEarlyPregnancy(
                    miscarriage.getNumberOfDaysInEarlyPregnancy());
        }
        init_DailyClinicalMiscarriageProbability_TreeMap();
        init_DailyEarlyPregnancyLossProbability_TreeMap();
        init_NumberOfDaysInLateFullTermPregnancy_int();
        getLogger().exiting(sourceClass, sourceMethod);
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
//            GENESIS_Log.initLog(
//                    logDirectory,
//                    directory,
//                    logname,
//                    logFilename);
            run(args, directory);
            //test(args, directory);
            GENESIS_Log.reset();
        } catch (Error e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void test(
            String[] args,
            File directory) {
        GENESIS_Miscarriage instance = new GENESIS_Miscarriage();
        instance._GENESIS_Environment = new GENESIS_Environment();
        instance._GENESIS_Environment._Directory = directory;
        File miscarriageRate_File = new File(
                directory.toString() + "/InputData/DemographicData/"
                + "MiscarriageRate/MiscarriageRate_Leeds_1991.csv");
        File mortalityRate_File = new File(
                directory.toString() + "/InputData/DemographicData/"
                + "MortalityRate/MortalityRate_Leeds_1991.xml");
        GENESIS_Mortality mortality = new GENESIS_Mortality(
                instance._GENESIS_Environment,
                mortalityRate_File);
        instance.processCSVtoXML(
                miscarriageRate_File,
                mortality);
    }

    public static void run(String[] args,
            File directory) {
        GENESIS_Miscarriage instance = new GENESIS_Miscarriage();
        instance._GENESIS_Environment = new GENESIS_Environment();
        instance._GENESIS_Environment._Directory = directory;
        instance.formatData(directory);
    }

    public void formatData(File directory) {
        String sourceMethod = "formatData(File)";
        getLogger().entering(sourceClass, sourceMethod);
        for (int i = 1991; i < 2001; i++) {
            log(Level.FINE, "" + i);
            File miscarriageRate_File = new File(
                    directory.toString()
                    + "/InputData/DemographicData/MiscarriageRate/MiscarriageRate_Leeds_" + i + ".csv");
            File mortalityRate_File = new File(
                    directory.toString() + "/InputData/DemographicData/"
                    + "MortalityRate/MortalityRate_Leeds_" + i + ".xml");
            GENESIS_Mortality mortality = new GENESIS_Mortality(
                    _GENESIS_Environment,
                    mortalityRate_File);
            processCSVtoXML(miscarriageRate_File,
                    mortality);
        }
        getLogger().exiting(sourceClass, sourceMethod);
    }

    public void processCSVtoXML(
            File miscarriageRate_File,
            GENESIS_Mortality mortality) {
        String sourceMethod = "processCSVtoXML(File)";
        getLogger().entering(sourceClass, sourceMethod);
        _GENESIS_Environment._Time = new GENESIS_Time(1991, 0);
        _GENESIS_Environment._Directory = new File("C:/Work/Projects/GENESIS/Workspace/");
        //_GENESIS_Environment._Directory = new File("/scratch01/Work/Projects/GENESIS/workspace/");
        GENESIS_Environment a_GENESIS_Environment = new GENESIS_Environment();
        a_GENESIS_Environment._Time = new GENESIS_Time(1991, 0);
        a_GENESIS_Environment._Directory = new File("C:/Work/Projects/GENESIS/Workspace/");
        //a_GENESIS_Environment._Directory = new File("/scratch01/Work/Projects/GENESIS/workspace/");
        String[] a_Filename_String_prefixSuffix = miscarriageRate_File.getName().split("\\.");
        GENESIS_Miscarriage a_Miscarriage = new GENESIS_Miscarriage(
                a_GENESIS_Environment,
                miscarriageRate_File);
        TreeMap<GENESIS_AgeBound, BigDecimal> dailyCMP = a_Miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap;
        TreeMap<GENESIS_AgeBound, BigDecimal> newDailyCMP = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = dailyCMP.entrySet().iterator();
        while (ite.hasNext()) {
            Entry<GENESIS_AgeBound, BigDecimal> entry = ite.next();
            GENESIS_AgeBound ageBound = entry.getKey();
            BigDecimal survival = BigDecimal.ONE.subtract(
                    mortality._FemaleDailyMortalityAgeBoundProbability_TreeMap.get(ageBound));
            BigDecimal adjustedMiscarraigeProbabilty = entry.getValue().multiply(survival);
            newDailyCMP.put(ageBound, adjustedMiscarraigeProbabilty);
        }
//        a_Miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap = newDailyCMP;
//        a_Miscarriage.clinicalMiscarriageAgeRate.clear();
//        a_Miscarriage.clinicalMiscarriageAgeRate.addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
//                a_Miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap));
        TreeMap<GENESIS_AgeBound, BigDecimal> dailyEPL = a_Miscarriage._DailyEarlyPregnancyLossAgeBoundProbability_TreeMap;
        TreeMap<GENESIS_AgeBound, BigDecimal> newDailyEPL = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        ite = dailyEPL.entrySet().iterator();
        while (ite.hasNext()) {
            Entry<GENESIS_AgeBound, BigDecimal> entry = ite.next();
            GENESIS_AgeBound ageBound = entry.getKey();
            BigDecimal survival = BigDecimal.ONE.subtract(
                    mortality._FemaleDailyMortalityAgeBoundProbability_TreeMap.get(ageBound));
            BigDecimal adjustedMiscarraigeProbabilty = entry.getValue().multiply(survival);
            newDailyEPL.put(ageBound, adjustedMiscarraigeProbabilty);
        }
        a_Miscarriage._DailyEarlyPregnancyLossAgeBoundProbability_TreeMap = newDailyEPL;
//        a_Miscarriage.earlyPregnancyLossAgeRate.clear();
//        a_Miscarriage.earlyPregnancyLossAgeRate.addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
//                a_Miscarriage._DailyEarlyPregnancyLossAgeBoundProbability_TreeMap));
        File b_File = new File(
                miscarriageRate_File.getParentFile(),
                a_Filename_String_prefixSuffix[0] + "YearAges.csv");
        a_Miscarriage.writeToCSV(b_File);
        GENESIS_Miscarriage b_Miscarriage = new GENESIS_Miscarriage(
                a_GENESIS_Environment,
                b_File);
        File c_File = new File(
                miscarriageRate_File.getParentFile(),
                a_Filename_String_prefixSuffix[0] + ".xml");
        XMLConverter.saveMiscarriageToXMLFile(c_File, b_Miscarriage);
        GENESIS_Miscarriage c_Miscarriage = new GENESIS_Miscarriage(
                a_GENESIS_Environment,
                c_File);
        log(Level.FINE, c_Miscarriage.toString());
        getLogger().exiting(sourceClass, sourceMethod);
    }

    protected final void init_NumberOfDaysInLateFullTermPregnancy_int() {
        _NumberOfDaysExpectedInPregnancyStageLate_int =
                getExpectedNumberOfDaysInFullTermPregnancy() - getNumberOfDaysInEarlyPregnancy();
    }

    protected final void init_DailyEarlyPregnancyLossProbability_TreeMap() {
        _DailyEarlyPregnancyLossAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        ListIterator<AgeBoundRate> listIterator;
        AgeBoundRate ageBoundRate;
        //Age ageBound;
        GENESIS_AgeBound ageBound;
        BigDecimal earlyPregnancyLossProbability;
        BigDecimal dailyEarlyPregnancyLossProbability;
        listIterator = earlyPregnancyLossAgeRate.listIterator();
        while (listIterator.hasNext()) {
            ageBoundRate = listIterator.next();
            ageBound = new GENESIS_AgeBound(ageBoundRate.getAgeBound());
            earlyPregnancyLossProbability = ageBoundRate.getRate();
            if (earlyPregnancyLossProbability != null) {
                if (earlyPregnancyLossProbability.compareTo(BigDecimal.ZERO) != 0) {
                    if (earlyPregnancyLossProbability.compareTo(BigDecimal.ONE) == 1) {
                        // @TODO This needs a logic check...
                        // Something about this is right, but then why 
                        // distinguish this rate from other rates? Perhaps a 
                        // new method for converting from output rates is 
                        // needed...
                        getLogger().log(
                                Level.FINE,
                                "earlyPregnancyLossProbability is greater than one "
                                + "most likely because it was calculated as "
                                + "((no of miscarriages) / ((no of days) / (no of days in year))");
                        BigDecimal dailyProbability =
                                Generic_BigDecimal.divideRoundIfNecessary(
                                earlyPregnancyLossProbability,
                                GENESIS_Time.NormalDaysInYear_BigInteger,
                                GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                                GENESIS_Environment.RoundingModeForPopulationProbabilities);
                        _DailyEarlyPregnancyLossAgeBoundProbability_TreeMap.put(ageBound, dailyProbability);
                    } else {
                        double exponent = 1.0d / (double) numberOfDaysInEarlyPregnancy;
                        double earlyPregnancyLossProbability_double = earlyPregnancyLossProbability.doubleValue();
                        double earlyPregnancyLossSurvivalProbability_double = 1.0d - earlyPregnancyLossProbability_double;
                        double dailySurvivalProbabilityForEarlyPregnancy_double = Math.pow(
                                earlyPregnancyLossSurvivalProbability_double,
                                exponent);
                        double dailyEarlyPregnancyLossProbability_double = 1.0d - dailySurvivalProbabilityForEarlyPregnancy_double;
                        dailyEarlyPregnancyLossProbability = BigDecimal.valueOf(dailyEarlyPregnancyLossProbability_double);
                        _DailyEarlyPregnancyLossAgeBoundProbability_TreeMap.put(ageBound, dailyEarlyPregnancyLossProbability);
                        log(Level.FINEST,
                                "dailyEarlyPregnancyLossProbability_double " + dailyEarlyPregnancyLossProbability_double);
                    }
                } else {
                    _DailyEarlyPregnancyLossAgeBoundProbability_TreeMap.put(ageBound, BigDecimal.ZERO);
                }
            } else {
                _DailyEarlyPregnancyLossAgeBoundProbability_TreeMap.put(ageBound, BigDecimal.ZERO);
            }
        }
    }

    protected final void init_DailyClinicalMiscarriageProbability_TreeMap() {
        _DailyClinicalMiscarriageAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        ListIterator<AgeBoundRate> listIterator;
        AgeBoundRate ageBoundRate;
        GENESIS_AgeBound ageBound;
        BigDecimal clinicalMiscarriageProbability;
        BigDecimal dailyClinicalMiscarriageProbability;
        listIterator = clinicalMiscarriageAgeRate.listIterator();
        while (listIterator.hasNext()) {
            ageBoundRate = listIterator.next();
            ageBound = new GENESIS_AgeBound(ageBoundRate.getAgeBound());
            clinicalMiscarriageProbability = ageBoundRate.getRate();
            if (clinicalMiscarriageProbability != null) {
                if (clinicalMiscarriageProbability.compareTo(BigDecimal.ZERO) != 0) {
                    if (clinicalMiscarriageProbability.compareTo(BigDecimal.ONE) == 1) {
                        // @TODO This needs a logic check...
                        // Something about this is right, but then why 
                        // distinguish this rate from other rates? Perhaps a 
                        // new method for converting from output rates is 
                        // needed...
                        getLogger().log(
                                Level.FINE,
                                "clinicalMiscarriageProbability is greater than one "
                                + "most likely because it was calculated as "
                                + "((no of miscarriages) / ((no of days) / (no of days in year))");
                        BigDecimal dailyProbability =
                                Generic_BigDecimal.divideRoundIfNecessary(
                                clinicalMiscarriageProbability,
                                GENESIS_Time.NormalDaysInYear_BigInteger,
                                GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                                GENESIS_Environment.RoundingModeForPopulationProbabilities);
                        _DailyClinicalMiscarriageAgeBoundProbability_TreeMap.put(ageBound, dailyProbability);
                    } else {
                        double exponent = 1.0d / getNumberOfDaysInLatePregnancy_double();
                        double clinicalMiscarriageProbability_double = clinicalMiscarriageProbability.doubleValue();
                        double clinicalMiscarriageSurvivalProbability_double = 1.0d - clinicalMiscarriageProbability_double;
                        double dailySurvivalProbabilityForLatePregnancy_double = Math.pow(
                                clinicalMiscarriageSurvivalProbability_double,
                                exponent);
                        double dailyClinicalMiscarriageProbability_double = 1.0d - dailySurvivalProbabilityForLatePregnancy_double;
                        dailyClinicalMiscarriageProbability = BigDecimal.valueOf(dailyClinicalMiscarriageProbability_double);
                        _DailyClinicalMiscarriageAgeBoundProbability_TreeMap.put(ageBound, dailyClinicalMiscarriageProbability);
                        log(Level.FINEST,
                                "dailyClinicalMiscarriageProbability_double " + dailyClinicalMiscarriageProbability_double);
                    }
                } else {
                    _DailyClinicalMiscarriageAgeBoundProbability_TreeMap.put(ageBound, BigDecimal.ZERO);
                }
            } else {
                _DailyClinicalMiscarriageAgeBoundProbability_TreeMap.put(ageBound, BigDecimal.ZERO);
            }
        }
    }

    public double getNumberOfDaysInLatePregnancy_double() {
        return expectedNumberOfDaysInFullTermPregnancy - getNumberOfDaysInEarlyPregnancy();
    }

    public void writeToXML(File a_File) {
        XMLConverter.saveMiscarriageToXMLFile(a_File, this);
    }

    public void writeToCSV(File a_File) {
        PrintWriter a_PrintWriter;
        try {
            a_PrintWriter = new PrintWriter(new FileOutputStream(a_File));
            String line;
            line = "Number of Days in Full Term Pregnacy";
            a_PrintWriter.println(line);
            a_PrintWriter.println(expectedNumberOfDaysInFullTermPregnancy);
            line = "Number of Days in Early Pregnacy";
            a_PrintWriter.println(line);
            a_PrintWriter.println(numberOfDaysInEarlyPregnancy);
            ListIterator<AgeBoundRate> listIterator;
            AgeBoundRate ageBoundRate;
            line = "Early Pregnancy Loss Rate";
            a_PrintWriter.println(line);
            line = "Gender,MinumumAgeInYears,MaximumAgeInYears,MiscarriageRate";
            a_PrintWriter.println(line);
            listIterator = earlyPregnancyLossAgeRate.listIterator();
            while (listIterator.hasNext()) {
                line = "0,";
                ageBoundRate = listIterator.next();
                line += ageBoundRate.getAgeBound().getAgeMin().getYear();
                line += ",";
                line += ageBoundRate.getAgeBound().getAgeMax().getYear();
                line += ",";
                line += ageBoundRate.getRate();
                a_PrintWriter.println(line);
            }
            line = "Clinical Miscarriage Rate";
            a_PrintWriter.println(line);
            line = "Gender,MinumumAgeInYears,MaximumAgeInYears,MiscarriageRate";
            a_PrintWriter.println(line);
            listIterator = clinicalMiscarriageAgeRate.listIterator();
            while (listIterator.hasNext()) {
                line = "0,";
                ageBoundRate = listIterator.next();
                line += ageBoundRate.getAgeBound().getAgeMin().getYear();
                line += ",";
                line += ageBoundRate.getAgeBound().getAgeMax().getYear();
                line += ",";
                line += ageBoundRate.getRate();
                a_PrintWriter.println(line);
            }
            a_PrintWriter.close();
        } catch (IOException a_IOException) {
            a_IOException.printStackTrace();
            log(Level.FINE,
                    "Exception in " + this.getClass().getName() + ".writeToCSV_AnnualAgeMiscarriage(File)");
        }
    }

    public Long getMinAgeYears() {
        return GENESIS_Collections.getMinAgeYears(_DailyClinicalMiscarriageAgeBoundProbability_TreeMap);
    }

    public Long getMaxAgeYears() {
        return GENESIS_Collections.getMaxAgeYears(_DailyClinicalMiscarriageAgeBoundProbability_TreeMap);
    }

    @Override
    public final List<AgeBoundRate> getClinicalMiscarriageAgeRate() {
        if (clinicalMiscarriageAgeRate == null) {
            clinicalMiscarriageAgeRate = new ArrayList<AgeBoundRate>();
        }
        return clinicalMiscarriageAgeRate;
    }

    @Override
    public final List<AgeBoundRate> getEarlyPregnancyLossAgeRate() {
        if (earlyPregnancyLossAgeRate == null) {
            earlyPregnancyLossAgeRate = new ArrayList<AgeBoundRate>();
        }
        return earlyPregnancyLossAgeRate;
    }

    /**
     * updates clinicalMiscarriageAgeRate and earlyPregnancyLossAgeRate
     */
    public final void updateLists() {
        clinicalMiscarriageAgeRate = getClinicalMiscarriageAgeRate();
        clinicalMiscarriageAgeRate.clear();
        clinicalMiscarriageAgeRate.addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                _DailyClinicalMiscarriageAgeBoundProbability_TreeMap));
        earlyPregnancyLossAgeRate = getEarlyPregnancyLossAgeRate();
        earlyPregnancyLossAgeRate.clear();
        earlyPregnancyLossAgeRate.addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                _DailyEarlyPregnancyLossAgeBoundProbability_TreeMap));
    }

    private static void log(Level level, String message) {
        getLogger().log(level, message);
    }

//    public static Logger getLogger() {
//        return getLogger();
//    }
    public static Logger getLogger() {
        return GENESIS_Log.logger;
    }
}
