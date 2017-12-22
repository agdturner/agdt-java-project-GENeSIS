package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Female;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.XMLConverter;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.FertilityFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBoundRate;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.Time;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.fertility.FertilityType;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Collections;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;

/**
 * A class for storing live birth fertility rates and for calculating pregnancy
 * probabilities from these using mortality and miscarriage rates.
 *
 * @TODO Add greater detail for age specific probability of twins/multiple baby
 * births (this task is more a data processing one than a code refactoring one).
 * http://www.statistics.gov.uk/StatBase/ssdataset.asp?vlnk=6378&Pos=&ColRank=2&Rank=816
 */
public class GENESIS_Fertility extends FertilityType implements Serializable {

    static final long serialVersionUID = 1L;
    /**
     * Used for Logging
     */
    private static final String sourceClass = GENESIS_Fertility.class.getName();
    private static final String sourcePackage = GENESIS_Fertility.class.getPackage().getName();
    //private static final Logger logger = getLogger().getLogger(sourcePackage);
    /**
     * GENESIS_Environment reference
     */
    public transient GENESIS_Environment ge;
    // References
    //
    // Probability of having twins by mother's age:
    // http://www.statistics.gov.uk/StatBase/ssdataset.asp?vlnk=6378&Pos=&ColRank=2&Rank=816
    // (Accessed on 2010-04-23)
    public GENESIS_Mortality _Mortality;
    public GENESIS_Miscarriage _Miscarriage;
    /**
     * For storing Daily Pregnancy Probabilities for Females by
     * GENESIS_AgeBound. To get the pregnancy probability for a female use the
     * getDailyPregnancyProbability(Female) method which controls for the age of
     * female to ensure that people too young, and people too old do not have a
     * chance of giving birth in the simulation. This is artificial, and such
     * controls are quite difficult to justify.
     *
     * @TODO Fertility statistics are usually made available specified by age of
     * mother at birth. A pregnancy probabilities as applied in this model are
     * done so for conception. So in order to calculate the probability of
     * conception, some adjustment of live birth fertility statistics is wanted
     * to account for pregnancy term and the (un)likelihood of the mother
     * miscarrying. It is most likely given that pregnancy term is around 266
     * days (almost 3/4 of a year) that a mother will have a birthday after
     * conceiving and before giving birth. The mortality rates are specified by
     * age in years, so before and after a birthday, the mortality probabilities
     * for a mother may be significantly different. It may be more appropriate
     * to have age in days based mortality and fertility rates, but this is
     * perhaps too much detail also. For the time being this model has assumed a
     * fixed pregnancy term of 266 days. Pregnancy term actually varies and it
     * may be known to vary differently based on the age of mother and by the
     * nature of previous pregnancies and other characteristics of the mother.
     * The differences may also be captured in data for different
     * sub-populations to be simulated. This model could be enhanced by trying
     * to account for such variation, but none of this is straightforward and it
     * requires more detailed data from a birth register than what are currently
     * made available for this research.
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _DailyPregnancyAgeBoundProbabilities_TreeMap;
    /**
     * For storing Pregnancy Rate probabilities for Females by GENESIS_AgeBound.
     * The detail is for age to the nearest day.
     *
     * Fertility is often given by age of female at birth rather than at
     * conception, so this all needs shifting by the term of a pregnancy. That
     * in itself is not a great solution, because the length of term is actually
     * variable. The timing of a pregnancy is also open to debate as to when in
     * the process to start counting. Being confident about what this time is
     * with any detailed precision is also something that is hard to be. To get
     * the pregnancy probability for a female it is recommended to use the
     * getDailyPregnancyProbability(Female) method. The keys are Age. The values
     * are TreeMap&lt;Integer, BigDecimal&gt; where: keys are the number of days
     * into term of pregnancy; and, values are the probabilities that a female
     * of a given age (will be however many days - as indicated by the key)
     * pregnant. In the TreeMap&lt;Integer, BigDecimal&gt of values: A key of -1
     * is used to store the probability that a female of the age is pregnant. A
     * key of -2 is used to store the probability that a female of the age is
     * pregnant in early stage. A key of -3 is used to store the probability
     * that a female of the age is pregnant in late stage. A key of -4 is used
     * to store the probability that a female of the age is not pregnant
     */
    public HashMap<GENESIS_AgeBound, TreeMap<Integer, BigDecimal>> _PregnancyAgeBoundRateProbabilities_HashMap;
//    /**
//     * Keys are the number of days into pregnancy, values are the cumulative 
//     * pregnancy probabilities
//     */
//    public TreeMap<GENESIS_AgeBound, BigDecimal> _CumulativePregnancyRateProbabilities_HashMap;
    /**
     * For storing Annual Live Birth Rate for Females by AgeBound of mother
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _AnnualLiveBirthFertilityAgeBoundRate_TreeMap;
    /**
     * For storing twin pregnancy probabilities by AgeBound of mother. It is the
     * twinPregnancyAgeProbability converted into a TreeMap for convenience.
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _TwinPregnancyAgeBoundProbability_TreeMap;
    public static BigDecimal DefaultTwinPregnancyProbability = new BigDecimal("0.01464695");
    /**
     * For storing triplet pregnancy probabilities by age of mother. It is the
     * tripletPregnancyAgeProbability converted into a TreeMap for convenience.
     */
    public TreeMap<GENESIS_AgeBound, BigDecimal> _TripletPregnancyAgeBoundProbability_TreeMap;
    public static BigDecimal DefaultTripletPregnancyProbability = new BigDecimal("0.00022305");

    BigDecimal normalDaysInYear_BigDecimal = new BigDecimal(GENESIS_Time.NormalDaysInYear_BigInteger);

    public GENESIS_Fertility() {
        String sourceMethod = "GENESIS_Fertility()";
        getLogger().entering(sourceClass, sourceMethod);
        FertilityFactory.init();
        LogManager.getLogManager().addLogger(Logger.getLogger(GENESIS_Log.DefaultLoggerName));
    }

    public GENESIS_Fertility(GENESIS_Fertility a_Fertility) {
        this(a_Fertility.ge,
                a_Fertility);
    }

    public GENESIS_Fertility(
            GENESIS_Environment a_GENESIS_Environment,
            FertilityType fertility) {
        String sourceMethod = "GENESIS_Fertility(GENESIS_Environment,FertilityType)";
        getLogger().entering(sourceClass, sourceMethod);
        ge = a_GENESIS_Environment;
        _AnnualLiveBirthFertilityAgeBoundRate_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Rate(
                fertility.getLiveBirthFertilityRates());
        getLiveBirthFertilityRates().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                fertility.getLiveBirthFertilityRates()));
        getTwinBirthRates().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                fertility.getTwinBirthRates()));
        getTripletBirthRates().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                fertility.getTripletBirthRates()));
        //init_PregnancyProbabilities();
        getLogger().exiting(sourceClass, sourceMethod);
    }

    public GENESIS_Fertility(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_Fertility a_Fertility) {
        String sourceMethod = "GENESIS_Fertility(GENESIS_Environment,GENESIS_Fertility)";
        getLogger().entering(sourceClass, sourceMethod);
        FertilityFactory.init();
        this.ge = a_GENESIS_Environment;
        log(Level.FINER, "<Contruct Fertility>");
        this._Mortality = a_Fertility._Mortality;
        this._Miscarriage = a_Fertility._Miscarriage;
        getLiveBirthFertilityRates().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                a_Fertility.getLiveBirthFertilityRates()));
        getTwinBirthRates().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                a_Fertility.getTwinBirthRates()));
        getTripletBirthRates().addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                a_Fertility.getTripletBirthRates()));
        this._AnnualLiveBirthFertilityAgeBoundRate_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(
                a_Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap);
        this._DailyPregnancyAgeBoundProbabilities_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(
                a_Fertility._DailyPregnancyAgeBoundProbabilities_TreeMap);
        this._TwinPregnancyAgeBoundProbability_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(
                a_Fertility._TwinPregnancyAgeBoundProbability_TreeMap);
        this._TripletPregnancyAgeBoundProbability_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(
                a_Fertility._TripletPregnancyAgeBoundProbability_TreeMap);
        this.normalDaysInYear_BigDecimal = a_Fertility.normalDaysInYear_BigDecimal;
        log(Level.FINER, "</Contruct Fertility>");
    }

    /**
     * If the fertility file is a csv file then it is assumed the fertility are
     * in terms of live births. If the fertility file is an XML file it is
     * assumed that the fertility are in terms of pregnancies!!!!!!!!
     *
     * @param a_GENESIS_Environment
     * @param a_Mortality
     * @param a_Miscarriage
     * @param fertility_File
     */
    public GENESIS_Fertility(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_Mortality a_Mortality,
            GENESIS_Miscarriage a_Miscarriage,
            File fertility_File) {
        String sourceMethod = "GENESIS_Fertility(GENESIS_Environment,GENESIS_Mortality,GENESIS_Miscarriage,File)";
        getLogger().entering(sourceClass, sourceMethod);
        FertilityFactory.init();
        ge = a_GENESIS_Environment;
        this._Mortality = a_Mortality;
        this._Miscarriage = a_Miscarriage;
        log(Level.FINER, "<Contruct Fertility>");
        _AnnualLiveBirthFertilityAgeBoundRate_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        _TwinPregnancyAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        _TripletPregnancyAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        if (fertility_File.getName().endsWith("csv")) {
            BufferedReader br = null;
            try {
                br = Generic_StaticIO.getBufferedReader(fertility_File);
                StreamTokenizer aStreamTokenizer = new StreamTokenizer(br);
                Generic_StaticIO.setStreamTokenizerSyntax1(aStreamTokenizer);
                String line;
                int gender = 0;
                long minimumAgeInYears = 0;
                long maximumAgeInYears = 0;
                BigDecimal probability = null;
                int tokenType;
                boolean readLiveBirthFertility = false;
                // Read Live Birth Fertility Probabilities
                // ... read a line ...
                tokenType = aStreamTokenizer.nextToken();
                //System.out.println(aStreamTokenizer.sval);
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                // ... read another line ...
                tokenType = aStreamTokenizer.nextToken();
                //System.out.println(aStreamTokenizer.sval);
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                tokenType = aStreamTokenizer.nextToken();
                while (!readLiveBirthFertility) {
                    switch (tokenType) {
                        case StreamTokenizer.TT_EOL:
                            if (gender == 0) {
                                for (long age = minimumAgeInYears; age <= maximumAgeInYears; age++) {
                                    GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
                                    _AnnualLiveBirthFertilityAgeBoundRate_TreeMap.put(
                                            ageBound,
                                            probability);
                                }
                            }
                            break;
                        case StreamTokenizer.TT_WORD:
                            line = aStreamTokenizer.sval;
                            if (line.equalsIgnoreCase("Twin")) {
                                readLiveBirthFertility = true;
                            } else {
                                String[] splitline = line.split(",");
                                gender = new Integer(splitline[0]);
                                minimumAgeInYears = new Long(splitline[1]);
                                maximumAgeInYears = new Long(splitline[2]);
                                probability = new BigDecimal(splitline[3]);
                                //log(line);
                                log(Level.FINE,
                                        gender + ","
                                        + minimumAgeInYears + ","
                                        + maximumAgeInYears + ","
                                        + probability);
                            }
                            break;
                    }
                    tokenType = aStreamTokenizer.nextToken();
                }
                // Read Twin Birth Probabilities
                boolean readTwinFertility = false;
                // ... read another line ...
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                tokenType = aStreamTokenizer.nextToken();
                //System.out.println(aStreamTokenizer.sval);
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                tokenType = aStreamTokenizer.nextToken();
                while (!readTwinFertility) {
                    switch (tokenType) {
                        case StreamTokenizer.TT_EOL:
                            if (gender == 0) {
                                for (long age = minimumAgeInYears; age <= maximumAgeInYears; age++) {
                                    GENESIS_AgeBound ageBound = new GENESIS_AgeBound(age);
                                    _TwinPregnancyAgeBoundProbability_TreeMap.put(ageBound, probability);
                                }
                            }
                            break;
                        case StreamTokenizer.TT_WORD:
                            line = aStreamTokenizer.sval;
                            if (line.equalsIgnoreCase("Triplet")) {
                                readTwinFertility = true;
                            } else {
                                String[] splitline = line.split(",");
                                gender = new Integer(splitline[0]);
                                minimumAgeInYears = new Long(splitline[1]);
                                maximumAgeInYears = new Long(splitline[2]);
                                probability = new BigDecimal(splitline[3]);
                                //log(line);
                                log(Level.FINE,
                                        gender + ","
                                        + minimumAgeInYears + ","
                                        + maximumAgeInYears + ","
                                        + probability);
                            }
                    }
                    tokenType = aStreamTokenizer.nextToken();
                }
                // Read Triplet Birth Probabilities
                // ... read another line ...
                while (tokenType != StreamTokenizer.TT_EOL) {
                    tokenType = aStreamTokenizer.nextToken();
                }
                tokenType = aStreamTokenizer.nextToken();
                //System.out.println(aStreamTokenizer.sval);
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
                                    _TripletPregnancyAgeBoundProbability_TreeMap.put(ageBound, probability);
                                }
                            }
                            break;
                        case StreamTokenizer.TT_WORD:
                            line = aStreamTokenizer.sval;
                            String[] splitline = line.split(",");
                            gender = new Integer(splitline[0]);
                            minimumAgeInYears = new Long(splitline[1]);
                            maximumAgeInYears = new Long(splitline[2]);
                            probability = new BigDecimal(splitline[3]);
                            //log(line);
                            log(Level.FINE,
                                    gender + ","
                                    + minimumAgeInYears + ","
                                    + maximumAgeInYears + ","
                                    + probability);
                            break;
                    }
                    tokenType = aStreamTokenizer.nextToken();
                }
                getLiveBirthFertilityRates().addAll(
                        GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                        _AnnualLiveBirthFertilityAgeBoundRate_TreeMap));
                getTwinBirthRates().addAll(
                        GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                        _TwinPregnancyAgeBoundProbability_TreeMap));
                getTripletBirthRates().addAll(
                        GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                        _TripletPregnancyAgeBoundProbability_TreeMap));
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
            if (!fertility_File.getName().endsWith("xml")) {
                System.err.println(
                        "System.exit("
                        + GENESIS_ErrorAndExceptionHandler.IOException
                        + ") from " + sourceMethod + "!fertility_File.getName().endsWith(xml)");
                System.exit(Generic_ErrorAndExceptionHandler.IOException);
            }
            FertilityType fertility = null;
            try {
                fertility = FertilityFactory.read(fertility_File);
            } catch (JAXBException aJAXBException) {
                log(Level.SEVERE, aJAXBException.toString());
            }
            getLiveBirthFertilityRates().addAll(
                    fertility.getLiveBirthFertilityRates());
            getTwinBirthRates().addAll(
                    fertility.getTwinBirthRates());
            getTripletBirthRates().addAll(
                    fertility.getTripletBirthRates());
            init_AnnualLiveBirthFertilityRates_TreeMap(fertility);
            _TwinPregnancyAgeBoundProbability_TreeMap =
                    GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Rate(
                    getTwinBirthRates());
            _TripletPregnancyAgeBoundProbability_TreeMap =
                    GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Rate(
                    getTripletBirthRates());
        }
        init_PregnancyProbabilities();
        getLogger().exiting(sourceClass, sourceMethod);
    }

    public GENESIS_Fertility(
            GENESIS_Environment a_GENESIS_Environment) {
        this.ge = a_GENESIS_Environment;
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
            GENESIS_Fertility instance = new GENESIS_Fertility();
            instance.ge = new GENESIS_Environment(directory);
            instance.ge.Directory = directory;
            instance.formatData(directory);
            GENESIS_Log.reset();
        } catch (Error e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void formatData(File directory) {
        String sourceMethod = "formatData(File)";
        getLogger().entering(sourceClass, sourceMethod);
        for (int i = 1991; i < 2001; i++) {
            log(Level.FINE, "" + i);
            File mortalityRate_File = new File(
                    directory.toString()
                    + "/InputData/DemographicData/MortalityRate/MortalityRate_Leeds_" + i + ".xml");
            File miscarriageRate_File = new File(
                    directory.toString()
                    + "/InputData/DemographicData/MiscarriageRate/MiscarriageRate_Leeds_" + i + ".xml");
            File fertilityRate_File = new File(
                    directory.toString()
                    + "/InputData/DemographicData/FertilityRate/FertilityRate_Leeds_" + i + ".csv");
            processCSVtoXML(
                    mortalityRate_File,
                    miscarriageRate_File,
                    fertilityRate_File);
        }
        getLogger().exiting(sourceClass, sourceMethod);
    }

    public void processCSVtoXML(
            File mortalityRate_File,
            File miscarriageRate_File,
            File fertilityRate_File) {
        String sourceMethod = "processCSVtoXML(File)";
        getLogger().entering(sourceClass, sourceMethod);
        ge.Time = new GENESIS_Time(1991, 0);
        //a_GENESIS_Environment.Directory = new File("C:/Work/Projects/GENESIS/Workspace/");
        ge.Directory = new File("/scratch01/Work/Projects/GENESIS/workspace/");
        ge.Time = new GENESIS_Time(1991, 0);
        String[] a_Filename_String_prefixSuffix = fertilityRate_File.getName().split("\\.");
        GENESIS_Mortality a_Mortality = new GENESIS_Mortality(
                ge,
                mortalityRate_File);
        GENESIS_Miscarriage a_Miscarriage = new GENESIS_Miscarriage(
                ge,
                miscarriageRate_File);
        GENESIS_Fertility a_Fertility = new GENESIS_Fertility(
                ge,
                a_Mortality,
                a_Miscarriage,
                fertilityRate_File);
        File b_File = new File(
                fertilityRate_File.getParentFile(),
                a_Filename_String_prefixSuffix[0] + "YearAges.csv");
        a_Fertility.writeToCSV(b_File);
        //XMLConverter.saveFertilityToXMLFile(b_File, a_Fertility);
        GENESIS_Fertility b_Fertility = new GENESIS_Fertility(
                ge,
                a_Mortality,
                a_Miscarriage,
                b_File);
        File c_File = new File(
                fertilityRate_File.getParentFile(),
                a_Filename_String_prefixSuffix[0] + ".xml");
        XMLConverter.saveFertilityToXMLFile(c_File, b_Fertility);
        GENESIS_Fertility c_Fertility = new GENESIS_Fertility(
                ge,
                a_Mortality,
                a_Miscarriage,
                c_File);
        log(Level.FINE, c_Fertility.toString());
        getLogger().exiting(sourceClass, sourceMethod);
    }

    /**
     * updates genderAgeBoundRates using
     * _FemaleAnnualMortalityAgeBoundRate_TreeMap and
     * _MaleAnnualMortalityAgeBoundRate_TreeMap
     */
    public final void updateAgeBoundRates() {
        List<AgeBoundRate> theLiveBirthFertilityRates = getLiveBirthFertilityRates();
        theLiveBirthFertilityRates.clear();
        theLiveBirthFertilityRates.addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                _AnnualLiveBirthFertilityAgeBoundRate_TreeMap));
        List<AgeBoundRate> theTwinBirthRates = getTwinBirthRates();
        theTwinBirthRates.clear();
        theTwinBirthRates.addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                _TwinPregnancyAgeBoundProbability_TreeMap));
        List<AgeBoundRate> theTripletBirthRates = getTripletBirthRates();
        theTripletBirthRates.clear();
        theTripletBirthRates.addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                _TripletPregnancyAgeBoundProbability_TreeMap));
    }

    private void init_AnnualLiveBirthFertilityRates_TreeMap(FertilityType fertility) {
        this._AnnualLiveBirthFertilityAgeBoundRate_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        Iterator<AgeBoundRate> a_Iterator = getLiveBirthFertilityRates().iterator();
        AgeBoundRate ageBoundRate;
        BigDecimal fertilityRate;
        while (a_Iterator.hasNext()) {
            ageBoundRate = a_Iterator.next();
            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(ageBoundRate.getAgeBound());
            fertilityRate = ageBoundRate.getRate();
            this._AnnualLiveBirthFertilityAgeBoundRate_TreeMap.put(
                    ageBound, fertilityRate);
        }
    }

    public BigDecimal getTwinProbability_BigDecimal(
            GENESIS_Female aGENESIS_Female) {
        long ageInYears = aGENESIS_Female.getCopyOfAge().getAgeInYears();
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(ageInYears);
        return getTwinProbability_BigDecimal(ageBound);
    }

    public BigDecimal getTwinProbability_BigDecimal(
            GENESIS_AgeBound ageBound) {
        BigDecimal result = _TwinPregnancyAgeBoundProbability_TreeMap.get(ageBound);
        if (result == null) {
            result = BigDecimal.ZERO;
        }
        return result;
    }

    public BigDecimal getTripletProbability_BigDecimal(
            GENESIS_Female aGENESIS_Female) {
        long ageInYears = aGENESIS_Female.getCopyOfAge().getAgeInYears();
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(ageInYears);
        return getTripletProbability_BigDecimal(ageBound);
    }

    public BigDecimal getTripletProbability_BigDecimal(
            GENESIS_AgeBound ageBound) {
        BigDecimal result = _TripletPregnancyAgeBoundProbability_TreeMap.get(ageBound);
        if (result == null) {
            result = BigDecimal.ZERO;
        }
        return result;
    }

    public TreeMap<GENESIS_AgeBound, BigDecimal> get_DailyPregnancyAgeBoundProbabilies_TreeMap() {
        if (_DailyPregnancyAgeBoundProbabilities_TreeMap == null) {
            init_PregnancyProbabilities();
        }
        return _DailyPregnancyAgeBoundProbabilities_TreeMap;
    }

    // http://www.statistics.gov.uk/downloads/theme_population/Table_2.xls
    // Age 15-19, 20-24, 25-29, 30-34, 35-39, 40-44 and
    /**
     * Consider using getDailyPregnancyRateProbabilty(Female) as this takes into
     * account the age the mother would be at term date.
     *
     * @param a_Female
     * @return
     */
//    public BigDecimal getDailyPregnancyRateProbabilty(GENESIS_AgeBound ageBound) {
//        BigDecimal result = get_DailyPregnancyAgeBoundProbabilies_TreeMap().get(ageBound);
//        GENESIS_AgeBound aGENESIS_AgeBound = new GENESIS_AgeBound(ageBound);
//        if (result == null) {
//            if (aGENESIS_AgeBound.compareTo(
//                    new GENESIS_AgeBound(_DailyPregnancyAgeBoundProbabilities_TreeMap.lastKey())) == 1) {
//                result = BigDecimal.ZERO;
//            } else {
//                if (aGENESIS_AgeBound.compareTo(
//                        new GENESIS_AgeBound(_DailyPregnancyAgeBoundProbabilities_TreeMap.firstKey())) == -1) {
//                    result = BigDecimal.ZERO;
//                } else {
//                    GENESIS_AgeBound smallerAgeBound = new GENESIS_AgeBound(ageBound.getAgeMin().longValue() - 1);
//                    result = getDailyPregnancyRateProbabilty(smallerAgeBound);
//                }
//            }
//        }
////        Integer ageInYears_Integer = Integer.valueOf(ageInYears_int);
////        if (result == null) {
////            if (ageInYears_Integer.compareTo(_DailyPregnancyAgeBoundProbabilities_TreeMap.lastKey()) == 1) {
////                result = BigDecimal.ZERO;
////            } else {
////                if (ageInYears_Integer.compareTo(_DailyPregnancyAgeBoundProbabilities_TreeMap.firstKey()) == -1) {
////                    result = BigDecimal.ZERO;
////                } else {
////                    result = getDailyPregnancyRateProbabilty(ageInYears_int - 1);
////                }
////            }
////        }
////Debug code
////        if (result == null) {
////            int debug = 1;
////        }
//        return result;
//    }
//    public BigDecimal getDailyPregnancyRateProbabilty(GENESIS_Female a_Female) {
//        // Calculate age when a baby would be due at full term
//        GENESIS_Time a_Time = new GENESIS_Time(_GENESIS_Environment.Time);
//        a_Time.addDays(GENESIS_Female.NormalGestationPeriod_int);
//        long ageAtTimeDue = a_Female.getCopyOfAge().getAgeInYears();
//        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(ageAtTimeDue);
//        return getDailyPregnancyRateProbabilty(ageBound);
//    }
//    public BigDecimal getDailyFertilityMale(int ageInYears_int) {
//        Object result = getDailyAgeFertilityMale_TreeMap().get(ageInYears_int);
//        if (result == null) {
//            if (ageInYears_int > _FertilityProbabilitiesByAgeForMalesDaily_TreeMap.lastKey()) {
//                result = BigDecimal.ZERO;
//            } else {
//                if (ageInYears_int < _FertilityProbabilitiesByAgeForMalesDaily_TreeMap.firstKey()) {
//                    result = BigDecimal.ZERO;
//                } else {
//                    result = getDailyFertilityMale(ageInYears_int);
//                    if (result == null) {
//                        if (ageInYears_int > _FertilityProbabilitiesByAgeForMalesDaily_TreeMap.firstKey()) {
//                            result = getDailyFertilityMale(ageInYears_int - 1);
//                        }
//                    }
//                }
//            }
//        }
//        return (BigDecimal) result;
//    }
//    public int initialisePregnancies() {
//        _GENESIS_Environment.checkAndMaybeFreeMemory(
//                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//        log(Level.FINE, "<initialisePregnancies>");
//        String type = GENESIS_Person.getTypeLivingFemale_String();
//        /*
//         * The pregnancyProbabilities keys are age in years.
//         * The values are TreeMap&lt;Integer, BigDecimal&gt; where:
//         * keys are the number of days into term of pregnancy; and,
//         * values are the probabilities that a female of a given age (will be 
//         * however many days - as indicated by the key) pregnant.
//         * In the TreeMap&lt;Integer, BigDecimal&gt of values:
//         * A key of -1 is used to store the probability that a female of the age is 
//         * pregnant.
//         */
////      HashMap<Integer, HashMap<Integer, BigDecimal>> pregnancyProbabilities = _Demographics._Fertility._PregnancyAgeBoundRateProbabilities_HashMap;
//        HashMap<Integer, TreeMap<Integer, BigDecimal>> pregnancyProbabilities = _Demographics._Fertility._PregnancyAgeBoundRateProbabilities_HashMap;
//        //HashMap<Integer,BigDecimal> dailyFertility = _Fertility.get_Age_Fertitility_Female();
//        int _TotalPregnancies = 0;
//        Iterator<Long> a_Iterator;
//        GENESIS_Female a_Female;
//        Integer ageInYears_Integer;
//
//        // Calculate the proportions of the population that are not pregnant as 
//        // well as those that are and in each stage of pregnancy
//
//        // Calculate cumulative probabilities for setting the day of pregnancy
//        Integer age;
//        Integer day;
//        BigDecimal probability;
//        BigDecimal populationProportion;
//        BigDecimal cumulativePopulationProportion;
//        TreeMap<BigDecimal, Integer> probabiltyDay;
//        TreeMap<Integer, BigDecimal> dayProbability;
//        // ageCumulativePregnancyProportions stored for each age as a key, a 
//        // TreeMap which for each day of term gives the proportion of population
//        // for a day of pregnancy that are expected to be at this stage of 
//        // pregnancy
//        HashMap<Integer, TreeMap<BigDecimal, Integer>> ageCumulativePregnancyProportions = new HashMap<Integer, TreeMap<BigDecimal, Integer>>();
//        for (Entry<Integer, TreeMap<Integer, BigDecimal>> entry : pregnancyProbabilities.entrySet()) {
//            cumulativePopulationProportion = BigDecimal.ZERO;
//            age = entry.getKey();
//            dayProbability = entry.getValue();
//            BigDecimal femalePopulation = _Demographics._Population._FemaleAgeInYearsPopulationCount_TreeMap.get(age);
//            TreeMap<BigDecimal, Integer> cumulativePopulationProportions = new TreeMap<BigDecimal, Integer>();
//            for (Entry<Integer, BigDecimal> entry2 : dayProbability.entrySet()) {
//                day = entry2.getKey();
//                if (day > -1) {
//                    //System.out.println(day); // To check these are in the correct order
//                    probability = entry2.getValue();
//                    populationProportion = femalePopulation.multiply(probability);
//                    cumulativePopulationProportion = cumulativePopulationProportion.add(populationProportion);
//                    cumulativePopulationProportions.put(cumulativePopulationProportion, day);
//                }
//            }
//            ageCumulativePregnancyProportions.put(age, cumulativePopulationProportions);
//            // The cumulativePopulationProportion now gives the proportion of 
//            // femalePopulation that are likely to be pregnant on any day.
//            // Thus, the proportion that are likely not to be pregnant on any 
//            // can be calculated. This is not done here, but in the next step 
//            // this logic is used for setting pregnancies.
////            System.out.println(
////                    "femaleAgePopulation " + age + ", " + femalePopulation
////                    + "; cumulativePopulationProportion " + cumulativePopulationProportion);
//            log(Level.FINEST,
//                    "femaleAgePopulation " + age + ", " + femalePopulation
//                    + "; cumulativePopulationProportion " + cumulativePopulationProportion);
//        }
//        TreeMap<Integer, BigDecimal> ageSpecificPregnancyProbability_TreeMap;
//        HashSet<Long> newlyConceived = new HashSet<Long>();
//        a_Iterator = _NotPregnantFemale_ID_HashSet.iterator();
//        while (a_Iterator.hasNext()) {
//            _GENESIS_Environment.checkAndMaybeFreeMemory(
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            Long a_Agent_ID = a_Iterator.next();
//            a_Female = _GENESIS_AgentCollectionManager.getFemale(
//                    a_Agent_ID,
//                    type,
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            // a_Female.AgentEnvironment is null!
//            ageInYears_Integer = a_Female.get_AgeInYears_int();
//            BigDecimal femalePopulation = _Demographics._Population._FemaleAgeInYearsPopulationCount_TreeMap.get(ageInYears_Integer);
//            if (femalePopulation.compareTo(BigDecimal.ZERO) != 0) {
//
//
//                ageSpecificPregnancyProbability_TreeMap = pregnancyProbabilities.get(ageInYears_Integer);
//                if (ageSpecificPregnancyProbability_TreeMap != null) {
//                    BigDecimal pregnancyProbability = ageSpecificPregnancyProbability_TreeMap.get(-1);
////                TreeMap<Integer, BigDecimal> cumulativePregnancyRateProbabilities_TreeMap = new TreeMap<Integer, BigDecimal>();
////                BigDecimal probability;
//                    TreeMap<BigDecimal, Integer> cumulativePopulationProportions = ageCumulativePregnancyProportions.get(ageInYears_Integer);
////                BigDecimal cumulativePopulationProportion = BigDecimal.ZERO;
////                for (int i = 0; i < GENESIS_Female.NormalGestationPeriod_int; i++) {
////                    probability = ageSpecificPregnancyProbability_HashMap.get(i);
////                    cumulativePopulationProportion = cumulativePopulationProportion.add(probability);
////                    cumulativePregnancyRateProbabilities_TreeMap.put(
////                            i,
////                            new BigDecimal(cumulativePopulationProportion.toString()));
////                }
//
////            for (Entry<Integer,BigDecimal> entry: cumulativePregnancyRateProbabilities_TreeMap.entrySet()){
////                  Integer day = entry.getKey();
////                  BigDecimal pregnancyProbability = entry.getValue();
//                    //pregnancyProbability = ageSpecificPregnancyProbability_HashMap.get(-1);
//                    if (pregnancyProbability.compareTo(BigDecimal.ZERO) == 1) {
////                    if (Generic_BigDecimal.randomUniformTest(
////                            _GENESIS_Environment.AbstractModel._Random,
////                            pregnancyProbability,
////                            //_GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
////                            GENESIS_Environment.RoundingModeForPopulationProbabilities)) {
//
//                        // Get random value between 0 and population
//                        BigDecimal val = _GENESIS_Environment._Generic_BigDecimal.getRandom(
//                                _GENESIS_Environment._Generic_BigDecimal._Generic_BigInteger,
//                                GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                                //cumulativeProbabilities.firstKey(),
//                                BigDecimal.ZERO,
//                                femalePopulation);
////                        // Get random value between 0 and maximum cumulative probability
////                        BigDecimal val = _GENESIS_Environment._Generic_BigDecimal.getRandom(
////                                _GENESIS_Environment._Generic_BigDecimal._Generic_BigInteger,
////                                GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
////                                //cumulativeProbabilities.firstKey(),
////                                BigDecimal.ZERO,
////                                cumulativePopulationProportions.lastKey());
//                        // Get day for this val
//                        Entry<BigDecimal, Integer> cumulativePopulationProportionEntry = cumulativePopulationProportions.ceilingEntry(val);
//                        // if cumulativePopulationProportionEntry is null then 
//                        // the female is not pregnant
//                        if (cumulativePopulationProportionEntry != null) {
//                            int dueDay = cumulativePopulationProportionEntry.getValue();
//                            log(Level.FINE, "<dayOfPregnancy>");
//                            log(Level.FINE, "" + dueDay);
//                            log(Level.FINE, "</dayOfPregnancy>");
//                            a_Female.set_Pregnant(
//                                    _Demographics._Fertility,
//                                    dueDay,
//                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                            //a_Female.set_UnbornsFather(_Father);
//                            //_PregnantFemales.add(a_Female);
//                            if (dueDay <= 50) {
//                                _NearlyDuePregnantFemale_ID_HashSet.add(a_Agent_ID);
//                            }
//                            _PregnantFemale_ID_HashSet.add(a_Agent_ID);
//                            newlyConceived.add(a_Agent_ID);
//                            _TotalPregnancies++;
//                            //log(Level.FINE,"Pregnancy");
//                        }
//                        //}
//                    }
//                }
//            }
//        }
//        _NotPregnantFemale_ID_HashSet.removeAll(newlyConceived);
//        log(Level.FINE, "<initialisePregnancies>");
//        return _TotalPregnancies;
//    }
    public BigDecimal getAnnualLiveBirthFertility(
            GENESIS_Female a_Female) {
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(a_Female.getCopyOfAge().getAgeInYears());
        return getAnnualLiveBirthFertility(ageBound);
    }

    public BigDecimal getAnnualLiveBirthFertility(
            GENESIS_AgeBound ageBound) {
        return getRate(
                ageBound,
                _AnnualLiveBirthFertilityAgeBoundRate_TreeMap);
    }

    public static BigDecimal getRate(
            GENESIS_AgeBound ageBound,
            TreeMap<GENESIS_AgeBound, BigDecimal> m) {
        BigDecimal result = m.get(ageBound);
        if (result == null) {
            long ageBoundMinYear = ageBound.getAgeMin().getYear();
            if (ageBoundMinYear < m.firstKey().getAgeMin().getYear()) {
                return BigDecimal.ZERO;
            } else {
                if (ageBoundMinYear > m.lastKey().getAgeMin().getYear()) {
                    return BigDecimal.ZERO;
                } else {
                    if (ageBoundMinYear > m.firstKey().getAgeMin().getYear()) {
                        //GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(ageBoundMinYear - 1);
                        GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(ageBoundMinYear);
                        result = getRate(newAgeBound, m);
                    }
                }
            }
        }
        return result;
    }

    public BigDecimal getDailyPregnancyRate(
            GENESIS_Female a_Female) {
        // If age at birth would be greater than we allow then return zero!
        GENESIS_Age age = a_Female.getCopyOfAge();
        Time time = age.getAge_Time(ge.Time);
        GENESIS_Time age_Time = new GENESIS_Time(time);
        age_Time.addDays(GENESIS_Female.NormalGestationPeriod_int);
        long ageInYearsAtDueDate = age_Time.getYear();
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(ageInYearsAtDueDate);
        return getDailyPregnancyRate(ageBound);
    }

    public BigDecimal getDailyPregnancyRate(
            GENESIS_AgeBound ageBound) {
        return getRate(
                ageBound,
                _DailyPregnancyAgeBoundProbabilities_TreeMap);
    }

    public BigDecimal getTwinProbability(
            GENESIS_Female a_Female) {
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(a_Female.getCopyOfAge().getAgeInYears());
        return getTwinProbability(ageBound);
    }

    public BigDecimal getTwinProbability(
            GENESIS_AgeBound ageBound) {
        return getRate(
                ageBound,
                _TwinPregnancyAgeBoundProbability_TreeMap);
    }

    public BigDecimal getTripletProbability(
            GENESIS_Female a_Female) {
        GENESIS_AgeBound ageBound = new GENESIS_AgeBound(a_Female.getCopyOfAge().getAgeInYears());
        return getTripletProbability(ageBound);
    }

    public BigDecimal getTripletProbability(
            GENESIS_AgeBound ageBound) {
        return getRate(
                ageBound,
                _TripletPregnancyAgeBoundProbability_TreeMap);
    }

    /**
     * Calculates probabilities for pregnancy by the age of mother.
     *
     * @TODO This can be updated to give probabilities based on age in years.
     * What are the expected number of days that a female will be pregnant in a
     * year?
     */
    public final void init_PregnancyProbabilities() {
        _PregnancyAgeBoundRateProbabilities_HashMap =
                new HashMap<GENESIS_AgeBound, TreeMap<Integer, BigDecimal>>();
        _DailyPregnancyAgeBoundProbabilities_TreeMap =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        HashMap<GENESIS_AgeBound, TreeMap<Integer, BigDecimal>> counts =
                new HashMap<GENESIS_AgeBound, TreeMap<Integer, BigDecimal>>();
        Entry<GENESIS_AgeBound, BigDecimal> entry;
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = _AnnualLiveBirthFertilityAgeBoundRate_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            TreeMap<Integer, BigDecimal> daysCount = new TreeMap<Integer, BigDecimal>();
            TreeMap<Integer, BigDecimal> daysProbability = new TreeMap<Integer, BigDecimal>();
            GENESIS_AgeBound ageBound = entry.getKey();
            // Using an arbitrary non-zero population
            //BigDecimal population = femalePopulation.get(age);
            BigDecimal cumulativeProbability = BigDecimal.ZERO;
            // Population of 1Million
            BigDecimal population = BigDecimal.valueOf(1000000L);
            //System.out.println("population " + population);
            BigDecimal sumOfAllPregnancies = BigDecimal.ZERO;
            BigDecimal aLBFR = entry.getValue();
//            BigDecimal expectedBirthsInAYear = population.multiply(aLBFR);
//            System.out.println("expectedBirthsInAYear " + expectedBirthsInAYear);
//            BigDecimal expectedBirthsInADay = Generic_BigDecimal.divideRoundIfNecessary(
//                    expectedBirthsInAYear,
//                    GENESIS_Time.NormalDaysInYear_BigInteger,
//                    GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    GENESIS_Environment.RoundingModeForPopulationProbabilities);
//            System.out.println("expectedBirthsInADay " + expectedBirthsInADay);            
            BigDecimal probabilityNotTwins = BigDecimal.ONE.subtract(
                    getTwinProbability_BigDecimal(ageBound));
            BigDecimal probabilityNotTriplets = BigDecimal.ONE.subtract(
                    getTripletProbability_BigDecimal(ageBound));
            BigDecimal probabilityOfLabour = population.multiply(
                    aLBFR.multiply(probabilityNotTwins).multiply(probabilityNotTriplets));
            BigDecimal dayAmount = Generic_BigDecimal.divideRoundIfNecessary(
                    probabilityOfLabour,
                    GENESIS_Time.NormalDaysInYear_BigInteger,
                    ge.DecimalPlacePrecisionForPopulationProbabilities,
                    ge.RoundingModeForPopulationProbabilities);
            //System.out.println("expectedLaboursInADay " + dayAmount);            
            int day = GENESIS_Female.NormalGestationPeriod_int;
            sumOfAllPregnancies = sumOfAllPregnancies.add(dayAmount);
            daysCount.put(day, dayAmount);
            BigDecimal probability = Generic_BigDecimal.divideRoundIfNecessary(
                    dayAmount,
                    population,
                    ge.DecimalPlacePrecisionForPopulationProbabilities,
                    ge.RoundingModeForPopulationProbabilities);
            daysProbability.put(day, probability);
            cumulativeProbability = cumulativeProbability.add(probability);
            BigDecimal mortalitySurvivalProbability = BigDecimal.ONE.subtract(
                    _Mortality.getDailyMortalityFemale(ageBound));
            int numberOfDaysInEarlyPregnancy = _Miscarriage.getNumberOfDaysInEarlyPregnancy();
            BigDecimal miscarriageClinical = _Miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap.get(ageBound);
            BigDecimal pregnancySurvivalInLateStage;
            if (miscarriageClinical == null) {
                pregnancySurvivalInLateStage = BigDecimal.ZERO;
            } else {
                pregnancySurvivalInLateStage = BigDecimal.ONE.subtract(
                        miscarriageClinical);
            }
            pregnancySurvivalInLateStage = pregnancySurvivalInLateStage.multiply(mortalitySurvivalProbability);
            while (day > numberOfDaysInEarlyPregnancy) {
                if (pregnancySurvivalInLateStage.compareTo(BigDecimal.ZERO) == 0) {
                    dayAmount = BigDecimal.ZERO;
                } else {
                    dayAmount = Generic_BigDecimal.divideRoundIfNecessary(
                            dayAmount,
                            pregnancySurvivalInLateStage,
                            ge.DecimalPlacePrecisionForPopulationProbabilities,
                            ge.RoundingModeForPopulationProbabilities);
                }
                daysCount.put(day, dayAmount);
                sumOfAllPregnancies = sumOfAllPregnancies.add(dayAmount);
                probability = Generic_BigDecimal.divideRoundIfNecessary(
                        dayAmount,
                        population,
                        ge.DecimalPlacePrecisionForPopulationProbabilities,
                        ge.RoundingModeForPopulationProbabilities);
                daysProbability.put(day, probability);
                cumulativeProbability = cumulativeProbability.add(probability);
                day--;
            }
            BigDecimal pregnancyInLateStageProbability = new BigDecimal(cumulativeProbability.toString());
            BigDecimal miscarraigeEPL = _Miscarriage._DailyEarlyPregnancyLossAgeBoundProbability_TreeMap.get(ageBound);
            BigDecimal pregnancySurvivalInEarlyStage;
            if (miscarraigeEPL == null) {
                pregnancySurvivalInEarlyStage = BigDecimal.ZERO;
            } else {
                pregnancySurvivalInEarlyStage = BigDecimal.ONE.subtract(
                        miscarraigeEPL);
            }
            while (day > -1) {
                if (pregnancySurvivalInEarlyStage.compareTo(BigDecimal.ZERO) == 0) {
                    dayAmount = BigDecimal.ZERO;
                } else {
                    dayAmount = Generic_BigDecimal.divideRoundIfNecessary(
                            dayAmount,
                            pregnancySurvivalInEarlyStage,
                            ge.DecimalPlacePrecisionForPopulationProbabilities,
                            ge.RoundingModeForPopulationProbabilities);
                }
                /*
                 * Removed for the time being and assumed that death of mother
                 * is accounted for in miscarriage statistics // Correct for
                 * death of mother dayAmount =
                 * Generic_BigDecimal.divideRoundIfNecessary( dayAmount,
                 * mortalitySurvivalProbability,
                 * GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                 * GENESIS_Environment.RoundingModeForPopulationProbabilities);
                 */
                daysCount.put(day, dayAmount);
                sumOfAllPregnancies = sumOfAllPregnancies.add(dayAmount);
                probability = Generic_BigDecimal.divideRoundIfNecessary(
                        dayAmount,
                        population,
                        ge.DecimalPlacePrecisionForPopulationProbabilities,
                        ge.RoundingModeForPopulationProbabilities);
                daysProbability.put(day, probability);
                cumulativeProbability = cumulativeProbability.add(probability);
                day--;
            }
            daysProbability.put(-1, cumulativeProbability);
            BigDecimal pregnancyInEarlyStageProbability = new BigDecimal(
                    (cumulativeProbability.subtract(pregnancyInLateStageProbability)).toString());
            BigDecimal numberOfNonPregnancies = population.subtract(sumOfAllPregnancies);
            BigDecimal proportionOfNonPregnancies = Generic_BigDecimal.divideRoundIfNecessary(
                    numberOfNonPregnancies,
                    population,
                    ge.DecimalPlacePrecisionForPopulationProbabilities,
                    ge.RoundingModeForPopulationProbabilities);
            _DailyPregnancyAgeBoundProbabilities_TreeMap.put(ageBound, probability);
            daysProbability.put(-2, pregnancyInEarlyStageProbability);
            daysProbability.put(-3, pregnancyInLateStageProbability);
            daysProbability.put(-4, proportionOfNonPregnancies);
//            // Cumulative Probability should be 1...
//            cumulativeProbability = cumulativeProbability.add(proportionOfNonPregnancies);
//            System.out.println("cumulativeProbability " + cumulativeProbability);
            _PregnancyAgeBoundRateProbabilities_HashMap.put(ageBound, daysProbability);
        }
        int debug = 1;
    }

//    /** Backup
//     * Calculates probabilities for pregnancy by the age of mother. It is
//     * assumed that there is no variation in death of mother or miscarriage for
//     * multiple pregnancies. It is also assumed that there is no variation in
//     * birth, death, pregnancy and miscarriage rates throughout a year.
//     *
//     * What are the expected number of days that a female will be pregnant in a
//     * year?
//     */
//    private void init_PregnancyProbabilities() {
//        _PregnancyAgeBoundRateProbabilities_HashMap =
//                new HashMap<GENESIS_Age, TreeMap<Integer, BigDecimal>>();
//        _DailyPregnancyAgeBoundProbabilities_TreeMap =
//                new TreeMap<GENESIS_AgeBound, BigDecimal>();
//        HashMap<GENESIS_AgeBound, TreeMap<Integer, BigDecimal>> counts =
//                new HashMap<GENESIS_AgeBound, TreeMap<Integer, BigDecimal>>();
//        Entry<GENESIS_AgeBound, BigDecimal> entry;
//        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = _AnnualLiveBirthFertilityAgeBoundRate_TreeMap.entrySet().iterator();
//        while (ite.hasNext()) {
//            entry = ite.next();
//            TreeMap<Integer, BigDecimal> daysCount = new TreeMap<Integer, BigDecimal>();
//            TreeMap<Integer, BigDecimal> daysProbability = new TreeMap<Integer, BigDecimal>();
//            GENESIS_AgeBound ageBound = entry.getKey();
//            // Using an arbitrary non-zero population
//            //BigDecimal population = femalePopulation.get(age);
//            BigDecimal cumulativeProbability = BigDecimal.ZERO;
//            BigDecimal population = BigDecimal.valueOf(1);
//            BigDecimal sumOfAllPregnancies = BigDecimal.ZERO;
//            BigDecimal aLBFR = entry.getValue();
//            BigDecimal probabilityNotTwins = BigDecimal.ONE.subtract(
//                    getTwinProbability_BigDecimal(ageBound));
//            BigDecimal probabilityNotTriplets = BigDecimal.ONE.subtract(
//                    getTripletProbability_BigDecimal(ageBound));
//            BigDecimal probabilityOfLabour = population.multiply(
//                    aLBFR.multiply(probabilityNotTwins).multiply(probabilityNotTriplets));
//            BigDecimal dayAmount = Generic_BigDecimal.divideRoundIfNecessary(
//                    probabilityOfLabour,
//                    GENESIS_Time.NormalDaysInYear_BigInteger,
//                    GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    GENESIS_Environment.RoundingModeForPopulationProbabilities);
//            int day = 256;
//            sumOfAllPregnancies = sumOfAllPregnancies.add(dayAmount);
//            daysCount.put(day, dayAmount);
//            BigDecimal probability = Generic_BigDecimal.divideRoundIfNecessary(
//                    dayAmount,
//                    population,
//                    GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    GENESIS_Environment.RoundingModeForPopulationProbabilities);
//            daysProbability.put(day, probability);
//            cumulativeProbability = cumulativeProbability.add(probability);
//            BigDecimal mortalitySurvivalProbability = BigDecimal.ONE.subtract(
//                    _Mortality.getDailyMortalityFemale(ageBound));
//            BigDecimal miscarriageClinical = _Miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap.get(ageBound);
//            BigDecimal pregnancySurvivalInLateStage;
//            if (miscarriageClinical == null) {
//                pregnancySurvivalInLateStage = BigDecimal.ZERO;
//            } else {
//                pregnancySurvivalInLateStage = BigDecimal.ONE.subtract(
//                        miscarriageClinical);
//            }
//            for (day = 255; day > 41; day--) {
//                if (pregnancySurvivalInLateStage.compareTo(BigDecimal.ZERO) == 0) {
//                    dayAmount = BigDecimal.ZERO;
//                } else {
//                    dayAmount = Generic_BigDecimal.divideRoundIfNecessary(
//                            dayAmount,
//                            pregnancySurvivalInLateStage,
//                            GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                            GENESIS_Environment.RoundingModeForPopulationProbabilities);
//                }/*
//                 * Removed for the time being and assumed that death of mother
//                 * is accounted for in miscarriage statistics // Correct for
//                 * death of mother dayAmount =
//                 * Generic_BigDecimal.divideRoundIfNecessary( dayAmount,
//                 * mortalitySurvivalProbability,
//                 * GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                 * GENESIS_Environment.RoundingModeForPopulationProbabilities);
//                 */
//                daysCount.put(day, dayAmount);
//                sumOfAllPregnancies = sumOfAllPregnancies.add(dayAmount);
//                probability = Generic_BigDecimal.divideRoundIfNecessary(
//                        dayAmount,
//                        population,
//                        GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                        GENESIS_Environment.RoundingModeForPopulationProbabilities);
//                daysProbability.put(day, probability);
//                cumulativeProbability = cumulativeProbability.add(probability);
//            }
//            BigDecimal pregnancyInLateStageProbability = new BigDecimal(cumulativeProbability.toString());
//            BigDecimal miscarraigeEPL = _Miscarriage._DailyEarlyPregnancyLossAgeBoundProbability_TreeMap.get(ageBound);
//            BigDecimal pregnancySurvivalInEarlyStage;
//            if (miscarraigeEPL == null) {
//                pregnancySurvivalInEarlyStage = BigDecimal.ZERO;
//            } else {
//                pregnancySurvivalInEarlyStage = BigDecimal.ONE.subtract(
//                        miscarraigeEPL);
//            }
//            for (day = 41; day > -1; day--) {
//                if (pregnancySurvivalInEarlyStage.compareTo(BigDecimal.ZERO) == 0) {
//                    dayAmount = BigDecimal.ZERO;
//                } else {
//                    dayAmount = Generic_BigDecimal.divideRoundIfNecessary(
//                            dayAmount,
//                            pregnancySurvivalInEarlyStage,
//                            GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                            GENESIS_Environment.RoundingModeForPopulationProbabilities);
//                }
//                /*
//                 * Removed for the time being and assumed that death of mother
//                 * is accounted for in miscarriage statistics // Correct for
//                 * death of mother dayAmount =
//                 * Generic_BigDecimal.divideRoundIfNecessary( dayAmount,
//                 * mortalitySurvivalProbability,
//                 * GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                 * GENESIS_Environment.RoundingModeForPopulationProbabilities);
//                 */
//                daysCount.put(day, dayAmount);
//                sumOfAllPregnancies = sumOfAllPregnancies.add(dayAmount);
//                probability = Generic_BigDecimal.divideRoundIfNecessary(
//                        dayAmount,
//                        population,
//                        GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                        GENESIS_Environment.RoundingModeForPopulationProbabilities);
//                daysProbability.put(day, probability);
//                cumulativeProbability = cumulativeProbability.add(probability);
//            }
//            daysProbability.put(-1, cumulativeProbability);
//            BigDecimal pregnancyInEarlyStageProbability = new BigDecimal(
//                    (cumulativeProbability.subtract(pregnancyInLateStageProbability)).toString());
//            BigDecimal numberOfNonPregnancies = population.subtract(sumOfAllPregnancies);
//            BigDecimal proportionOfNonPregnancies = Generic_BigDecimal.divideRoundIfNecessary(
//                    numberOfNonPregnancies,
//                    population,
//                    GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
//                    GENESIS_Environment.RoundingModeForPopulationProbabilities);
//            _DailyPregnancyAgeBoundProbabilities_TreeMap.put(ageBound, probability);
//            daysProbability.put(-2, pregnancyInEarlyStageProbability);
//            daysProbability.put(-3, pregnancyInLateStageProbability);
//            daysProbability.put(-4, proportionOfNonPregnancies);
//            // Cumulative Probability should be 1...
//            cumulativeProbability = cumulativeProbability.add(proportionOfNonPregnancies);
//            _PregnancyAgeBoundRateProbabilities_HashMap.put(age, daysProbability);
//        }
//    }
//    /**
//     * Calculates probabilities for pregnancy by the age of mother. It is 
//     * assumed that there is no variation in death of mother or miscarriage 
//     * for multiple pregnancies. It is also assumed that there is no variation
//     * in birth, death, pregnancy and miscarriage rates throughout a year.
//     * 
//     * What are the expected number of days that a female will be pregnant in a 
//     * year?
//     */
//    private void init_PregnancyProbabilities() {
//        _PregnancyAgeBoundRateProbabilities_HashMap = new HashMap<Integer, TreeMap<Integer, BigDecimal>>();
//        _DailyPregnancyAgeBoundProbabilities_TreeMap = new TreeMap<Integer, BigDecimal>();
//        RoundingMode a_RoundingMode = RoundingMode.HALF_UP;
//        MathContext a_MathContext = new MathContext(20, a_RoundingMode);
//
//        int expectedNumberOfDaysInFullTermPregnancy = this._Miscarriage.getExpectedNumberOfDaysInFullTermPregnancy();
//        int numberOfDaysInEarlyPregnancy = this._Miscarriage.getNumberOfDaysInEarlyPregnancy();
//
//        for (Entry<Integer, BigDecimal> entry : _AnnualLiveBirthFertilityAgeBoundRate_TreeMap.entrySet()) {
//            Integer age = entry.getKey();
//            BigDecimal annualLiveBirthFertilityProbability = entry.getValue();
//            BigDecimal dailyEarlyPregnancyLossProbability =
//                    _Miscarriage._DailyEarlyPregnancyLossAgeBoundProbability_TreeMap.get(age);
//            BigDecimal dailyEarlyPregnancyLossSurvivalProbability = null;
//            if (dailyEarlyPregnancyLossProbability == null) {
//                dailyEarlyPregnancyLossSurvivalProbability = BigDecimal.ONE;
//            } else {
//                dailyEarlyPregnancyLossSurvivalProbability = BigDecimal.ONE.subtract(dailyEarlyPregnancyLossProbability);
//            }
//            BigDecimal dailyClinicalMiscarriageProbability =
//                    _Miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap.get(age);
//            BigDecimal dailyClinicalMiscarriageSurvivalProbability = null;
//            if (dailyClinicalMiscarriageProbability == null) {
//                dailyClinicalMiscarriageSurvivalProbability = BigDecimal.ONE;
//            } else {
//                dailyClinicalMiscarriageSurvivalProbability = BigDecimal.ONE.subtract(dailyClinicalMiscarriageProbability);
//            }
//            if (dailyEarlyPregnancyLossProbability != null && dailyClinicalMiscarriageProbability != null) {
//                TreeMap<Integer, BigDecimal> daysProbability = (TreeMap<Integer, BigDecimal>) _PregnancyAgeBoundRateProbabilities_HashMap.get(age);
//                if (daysProbability == null) {
//                    daysProbability = new TreeMap<Integer, BigDecimal>();
//                }
//                // Calculate dailyProbabilityOfLabour - the daily probability 
//                // of labour for any female of this age selected at random
//                BigDecimal probabilityOfLabourOnAnyDayOfTheYear = annualLiveBirthFertilityProbability.divide(
//                        normalDaysInYear_BigDecimal,
//                        a_MathContext);
//                // Because there are multiple births, dailyProbabilityOfLabour 
//                // is adjusted to be lower
//                log(Level.FINER, "<adjust annualLiveBirthFertilityProbability into daily probability of labour>");
//                BigDecimal twinProbability = getTwinProbability_BigDecimal(age);
//                BigDecimal tripletProbability = getTripletProbability_BigDecimal(age);
//                log(Level.FINER, "<adjust for multiple births>");
//                log(Level.FINER, "<adjust for twins>");
//                probabilityOfLabourOnAnyDayOfTheYear =
//                        probabilityOfLabourOnAnyDayOfTheYear.multiply(
//                        BigDecimal.ONE.subtract(twinProbability),
//                        a_MathContext);
//                log(Level.FINER, "" + probabilityOfLabourOnAnyDayOfTheYear);
//                log(Level.FINER, "<adjust for triplets>");
//                probabilityOfLabourOnAnyDayOfTheYear =
//                        probabilityOfLabourOnAnyDayOfTheYear.multiply(
//                        BigDecimal.ONE.subtract(tripletProbability),
//                        a_MathContext);
//                probabilityOfLabourOnAnyDayOfTheYear =
//                        probabilityOfLabourOnAnyDayOfTheYear.multiply(
//                        BigDecimal.ONE.subtract(tripletProbability),
//                        a_MathContext); // Twice for triplets.
//                log(Level.FINER, "" + probabilityOfLabourOnAnyDayOfTheYear);
//                log(Level.FINER, "</adjust for triplets>");
//                log(Level.FINER, "</adjust for multiple births>");
//
//                BigDecimal cumprob = BigDecimal.ZERO;
//                // Track back
//                BigDecimal probabilityPregnantNDays = new BigDecimal(probabilityOfLabourOnAnyDayOfTheYear.toString());
//                BigDecimal dailyMortalityProbability = _Mortality._DailyAgeMortalityFemale_TreeMap.get(age);
//                BigDecimal dailySurvivalProbability = BigDecimal.ONE.subtract(dailyMortalityProbability);
//                int pregnancyDay;
//                pregnancyDay = 255;
//                TreeMap<Integer, BigDecimal> probabilityPregnantDayN = new TreeMap<Integer, BigDecimal>();
//                probabilityPregnantDayN.put(pregnancyDay, probabilityOfLabourOnAnyDayOfTheYear);
////                /*
////                 * Factoring for mother mortality is important. Is this correct?
////                 */
////                // Adjusting for nonpregnancy days may not be the right thing to 
////                // do! It might be better to apply the other corrections 
////                // proportionally for the year... i.e. if 365 days are split in 
////                // the ration 244:42 (approximately 302.18:62.82) then should 
////                // the correction be for 302 late stage pregnacy days and 63 
////                // early stage pregnancy days. The proportional way makes more 
////                // sense when considering if full term pregnancy were greater 
////                // than 365 days.
////                // Adjust for nonpregnancy days
////                for (int i = GENESIS_Time.NormalDaysInYear_int - 1;
////                i > expectedNumberOfDaysInFullTermPregnancy - 1;
////                i--) {
////                // Adjust for death of mother
////                probabilityPregnantNDays = probabilityPregnantNDays.divide(
////                dailySurvivalProbability,
////                a_MathContext);
////                cumprob = cumprob.add(probabilityPregnantNDays);
////                }
//                // Adjust for late stage pregnancy days
//                for (int i = expectedNumberOfDaysInFullTermPregnancy - 1;
//                        i > numberOfDaysInEarlyPregnancy - 1;
//                        i--) {
//                    /*
//                     * Factoring for mother mortality is important.
//                     */
//                    // Adjust for death of mother
//                    probabilityPregnantNDays = probabilityPregnantNDays.divide(
//                    dailySurvivalProbability,
//                    a_MathContext);
//                    // Adjust for miscarriage
//                    probabilityPregnantNDays = probabilityPregnantNDays.divide(
//                            dailyClinicalMiscarriageSurvivalProbability,
//                            a_MathContext);
//                    daysProbability.put(i, new BigDecimal(probabilityPregnantNDays.toString()));
//                    cumprob = cumprob.add(probabilityPregnantNDays);
//                }
//                // Adjust for early stage pregnancy days
//                for (int i = numberOfDaysInEarlyPregnancy - 1;
//                        i > - 1;
//                        i--) {
//                    /*
//                     * Factoring for mother mortality is important.
//                     */
//                    probabilityPregnantNDays = probabilityPregnantNDays.divide(
//                    dailySurvivalProbability,
//                    a_MathContext);
//                    // Adjust for miscarriage
//                    probabilityPregnantNDays = probabilityPregnantNDays.divide(
//                            dailyEarlyPregnancyLossSurvivalProbability,
//                            a_MathContext);
//                    if (daysProbability == null) {
//                        daysProbability = new TreeMap<Integer, BigDecimal>();
//                    }
//                    daysProbability.put(i, new BigDecimal(probabilityPregnantNDays.toString()));
//                    cumprob = cumprob.add(probabilityPregnantNDays);
//                }
//                // Assumed that:
//                // cumprob now approximates the probability that a female of age 
//                // age is pregnant on any day of the year; and,
//                // probabilityPregnantNDays now approximates the probability of 
//                // a female getting pregnant on any day...
//
//                //System.out.println(cumprob);
//                daysProbability.put(-1, new BigDecimal(cumprob.toString()));
//                _PregnancyAgeBoundRateProbabilities_HashMap.put(age, daysProbability);
//                _DailyPregnancyAgeBoundProbabilities_TreeMap.put(age, probabilityPregnantNDays);
////                BigDecimal probabilityOfLabourInFullTermPregnancyPeriod =
////                        probabilityOfLabourOnAnyDayOfTheYear.multiply(
////                        new BigDecimal("" + expectedNumberOfDaysInFullTermPregnancy));
////                //BigDecimal dailyMortalityProbability = a_GENESIS_Mortality.getDailyMortalityFemale(age);
////                //BigDecimal dailySurvivalProbability = BigDecimal.ONE.subtract(dailyMortalityProbability);
////                BigDecimal annualMortalityProbability = a_GENESIS_Mortality._AnnualAgeMortalityFemale_TreeMap.get(age);
////                BigDecimal annualSurvivalProbability = BigDecimal.ONE.subtract(annualMortalityProbability);
////                dailyProbabilityOfLabour =
////                        dailyProbabilityOfLabour.multiply(
////                        annualSurvivalProbability,
////                        a_MathContext);
////
////
////                // Calculate probability not pregnant in a year
////                BigDecimal probabilityNotPregnantOrDeadWithoutGivingBirthInYear = BigDecimal.ONE.subtract(
////                        dailyProbabilityOfLabour);
////                log(Level.FINER, "<probabilityNotPregnantOrDeadWithoutGivingBirthInYear>");
////                log(Level.FINER, "" + probabilityNotPregnantOrDeadWithoutGivingBirthInYear);
////                log(Level.FINER, "</probabilityNotPregnantOrDeadWithoutGivingBirthInYear>");
////                // Adjust to daily
////                log(Level.FINER, "<adjust to daily>");
////                dailyProbabilityOfLabour = dailyProbabilityOfLabour.divide(
////                        normalDaysInYear_BigDecimal,
////                        a_MathContext);
////                log(Level.FINER, "<dailyProbabilityOfLabour>");
////                log(Level.FINER, "" + dailyProbabilityOfLabour);
////                log(Level.FINER, "<dailyProbabilityOfLabour>");
////                log(Level.FINER, "</adjust to daily>");
////                log(Level.FINER, "<adjust to pregnancy term>");
////                dailyProbabilityOfLabour = dailyProbabilityOfLabour.multiply(
////                        GENESIS_Female.NormalGestationPeriod_BigDecimal,
////                        a_MathContext);
////                dailyProbabilityOfLabour = dailyProbabilityOfLabour.divide(
////                        normalDaysInYear_BigDecimal,
////                        a_MathContext);
////                log(Level.FINER, "<dailyProbabilityOfLabour>");
////                log(Level.FINER, "" + dailyProbabilityOfLabour);
////                log(Level.FINER, "<dailyProbabilityOfLabour>");
////                log(Level.FINER, "</adjust to pregnancy term>");
////                log(Level.FINER, "</adjust annualLiveBirthFertilityProbability into daily probability of labour>");
////
////                HashMap<Integer, BigDecimal> ageSpecificPregnancyProbability_HashMap = new HashMap<Integer, BigDecimal>();
////                BigDecimal probabilityPregnantDayN = new BigDecimal(dailyProbabilityOfLabour.toString());
////                BigDecimal probabilityNotPregnantDayN;
////                BigDecimal probabilityNotPregnant = BigDecimal.ONE;
////
////                BigDecimal probabilityPregnant = BigDecimal.ZERO;
////                BigDecimal probabilityPregnantDayNOrMore = BigDecimal.ZERO;
////
////                int day;
////                log(Level.FINEST, "<adjust for clinical (late stage) miscarriage and death of mother>");
////                log(Level.FINEST, "day, probabilityPregnantDayN, probabilityPregnantDayNOrMore");
////                for (int i = expectedNumberOfDaysInFullTermPregnancy - 1;
////                        i > numberOfDaysInEarlyPregnancy - 1;
////                        i--) {
//////                probabilityPregnantDayN = probabilityPregnantDayN.divide(
//////                        probabilityPregnancyContinuationLateStageDaily,
//////                        scale, a_RoundingMode);
////                    probabilityPregnantDayN = probabilityPregnantDayN.multiply(
////                            dailyClinicalMiscarriageProbability,
////                            a_MathContext);
////                    probabilityPregnantDayN = probabilityPregnantDayN.multiply(
////                            dailySurvivalProbability,
////                            a_MathContext);
////                    probabilityPregnantDayNOrMore = probabilityPregnantDayNOrMore.add(
////                            probabilityPregnantDayN);
////                    probabilityNotPregnantDayN = BigDecimal.ONE.subtract(
////                            probabilityPregnantDayN);
//////                probabilityNotPregnant = probabilityNotPregnant.multiply(
//////                        probabilityNotPregnantDayN,
//////                        a_MathContext);
////                    day = GENESIS_Female.NormalGestationPeriod_int - i;
////                    ageSpecificPregnancyProbability_HashMap.put(day, probabilityPregnantDayN);
////                    //probabilityPregnant = probabilityPregnant.add(probabilityPregnantDayN);
////                    log(Level.FINEST, "" + day
////                            + ", " + probabilityPregnantDayN.round(display_MathContext).toPlainString()
////                            + ", " + probabilityPregnantDayNOrMore.round(display_MathContext).toPlainString());
////                }
////                log(Level.FINEST, "</adjust for clinical (late stage) miscarriage and death of mother>");
////                log(Level.FINEST, "<adjust for early stage miscarriage and death of mother>");
////                for (int i = 0; i <= a_GENESIS_Miscarriage.getNumberOfDaysInEarlyPregnancy() - 1; i++) {
//////                probabilityPregnantDayN = probabilityPregnantDayN.divide(
//////                        probabilityPregnancyContinuationEarlyStageDaily,
//////                        scale, a_RoundingMode);
////                    probabilityPregnantDayN = probabilityPregnantDayN.multiply(
////                            dailyEarlyPregnancyLossProbability,
////                            a_MathContext);
////                    probabilityPregnantDayN = probabilityPregnantDayN.multiply(
////                            dailySurvivalProbability,
////                            a_MathContext);
////                    probabilityPregnantDayNOrMore = probabilityPregnantDayNOrMore.add(
////                            probabilityPregnantDayN);
////                    probabilityNotPregnantDayN = BigDecimal.ONE.subtract(probabilityPregnantDayN);
//////                probabilityNotPregnant = probabilityNotPregnant.multiply(
//////                        probabilityNotPregnantDayN,
//////                        a_MathContext);
////                    day = a_GENESIS_Miscarriage.getNumberOfDaysInEarlyPregnancy() - i;
////                    ageSpecificPregnancyProbability_HashMap.put(day, probabilityPregnantDayN);
////                    //probabilityPregnant = probabilityPregnant.add(probabilityPregnantDayN);
////                    log(Level.FINEST, "" + day
////                            + ", " + probabilityPregnantDayN.round(display_MathContext)
////                            + ", " + probabilityPregnantDayNOrMore.round(display_MathContext));
////                }
////                log(Level.FINEST, "</adjust for early stage miscarriage and death of mother>");
////                log(Level.FINEST, "<adjust for pregnancy>");
////                log(Level.FINEST, "<dailyPregnancyProbability>");
////                _DailyPregnancyAgeBoundProbabilities_TreeMap.put(age, probabilityPregnantDayN);
////                log(Level.FINEST, "</adjust for pregnancy>");
////                log(Level.FINER, "<probabilityPregnant>");
////                log(Level.FINER, "" + probabilityPregnantDayNOrMore);
////                log(Level.FINER, "</probabilityPregnant>");
////                ageSpecificPregnancyProbability_HashMap.put(-1, probabilityPregnantDayNOrMore);
////                log(Level.FINER, "<probabilityNotPregnant>");
////                log(Level.FINER, "" + BigDecimal.ONE.subtract(probabilityNotPregnant));
////                log(Level.FINER, "</probabilityNotPregnant>");
////                ageSpecificPregnancyProbability_HashMap.put(-2, probabilityPregnant);
////                _PregnancyAgeBoundRateProbabilities_HashMap.put(age, ageSpecificPregnancyProbability_HashMap);
//            }
//        }
//        log(Level.FINEST, "</getPregnancyProbabilities>");
//    }
    public void writeToXML(File a_File) {
        XMLConverter.saveFertilityToXMLFile(a_File, this);
    }

    public void writeToCSV(File a_File) {
        PrintWriter a_PrintWriter = null;
        try {
            a_PrintWriter = new PrintWriter(new FileOutputStream(a_File));
            BigDecimal probability;
            Iterator<GENESIS_AgeBound> a_Iterator;
            GENESIS_AgeBound ageBound;
            int gender;
            gender = 0;
            a_PrintWriter.println("Annual Live Birth Fertility Rate");
            a_PrintWriter.println("Gender,MinumumAgeInYears,MaximumAgeInYears,AnnualFertilityProbability");
            a_Iterator = _AnnualLiveBirthFertilityAgeBoundRate_TreeMap.keySet().iterator();
            while (a_Iterator.hasNext()) {
                ageBound = a_Iterator.next();
                probability = (BigDecimal) _AnnualLiveBirthFertilityAgeBoundRate_TreeMap.get(ageBound);
                a_PrintWriter.println(
                        "" + gender + ","
                        + ageBound.getAgeMin().getYear() + ","
                        + ageBound.getAgeMax().getYear() + ","
                        + probability.toPlainString());
            }
            a_PrintWriter.println("Twin");
            a_PrintWriter.println("Gender,MinumumAgeInYears,MaximumAgeInYears,AnnualFertilityProbability");
            a_Iterator = _TwinPregnancyAgeBoundProbability_TreeMap.keySet().iterator();
            while (a_Iterator.hasNext()) {
                ageBound = a_Iterator.next();
                probability = (BigDecimal) _TwinPregnancyAgeBoundProbability_TreeMap.get(ageBound);
                a_PrintWriter.println(
                        "" + gender + ","
                        + ageBound.getAgeMin().getYear() + ","
                        + ageBound.getAgeMax().getYear() + ","
                        + probability.toPlainString());
            }
            a_PrintWriter.println("Triplet");
            a_PrintWriter.println("Gender,MinumumAgeInYears,MaximumAgeInYears,AnnualFertilityProbability");
            a_Iterator = _TripletPregnancyAgeBoundProbability_TreeMap.keySet().iterator();
            while (a_Iterator.hasNext()) {
                ageBound = a_Iterator.next();
                probability = (BigDecimal) _TripletPregnancyAgeBoundProbability_TreeMap.get(ageBound);
                a_PrintWriter.println(
                        "" + gender + ","
                        + ageBound.getAgeMin().getYear() + ","
                        + ageBound.getAgeMax().getYear() + ","
                        + probability.toPlainString());
            }
//            gender = 1;
//            a_Iterator = _FertilityProbabilitiesByAgeForMalesAnnual_TreeMap.keySet().iterator();
//            while (a_Iterator.hasNext()) {
//                age = a_Iterator.next();
//                probability = (BigDecimal) _FertilityProbabilitiesByAgeForMalesAnnual_TreeMap.get(age);
//                a_PrintWriter.println(
//                        "" + gender + "," + age + "," + age + "," + probability.toPlainString());
//            }
            a_PrintWriter.close();
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

    @Override
    public final List<AgeBoundRate> getLiveBirthFertilityRates() {
        if (liveBirthFertilityRates == null) {
            liveBirthFertilityRates = new ArrayList<AgeBoundRate>();
        }
        return liveBirthFertilityRates;
    }

    @Override
    public final List<AgeBoundRate> getTwinBirthRates() {
        if (twinBirthRates == null) {
            twinBirthRates = new ArrayList<AgeBoundRate>();
        }
        return twinBirthRates;
    }

    @Override
    public final List<AgeBoundRate> getTripletBirthRates() {
        if (tripletBirthRates == null) {
            tripletBirthRates = new ArrayList<AgeBoundRate>();
        }
        return tripletBirthRates;
    }

    /**
     * updates genderAgePopulation using _FemaleAgeBoundPopulationCount_TreeMap
     * and _MaleAgeBoundPopulationCount_TreeMap
     */
    public final void updateLists() {
        liveBirthFertilityRates = getLiveBirthFertilityRates();
        liveBirthFertilityRates.clear();
        liveBirthFertilityRates.addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                _AnnualLiveBirthFertilityAgeBoundRate_TreeMap));
        twinBirthRates = getTwinBirthRates();
        twinBirthRates.clear();
        twinBirthRates.addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                _TwinPregnancyAgeBoundProbability_TreeMap));
        tripletBirthRates = getTwinBirthRates();
        tripletBirthRates.clear();
        tripletBirthRates.addAll(
                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
                _TripletPregnancyAgeBoundProbability_TreeMap));
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
