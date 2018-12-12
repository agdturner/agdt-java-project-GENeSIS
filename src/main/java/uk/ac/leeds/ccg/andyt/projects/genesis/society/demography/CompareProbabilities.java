/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Collections;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.projects.genesis.visualisation.GENESIS_ScatterPlotAndLinearRegression;

/**
 *
 * @author geoagdt
 */
public class CompareProbabilities {

    /**
     * Used for Logging
     */
    private static final String sourceClass = CompareProbabilities.class.getName();
    private static final String sourcePackage = CompareProbabilities.class.getPackage().getName();
    public GENESIS_Environment _GENESIS_Environment;
    //public File Directory;

    public CompareProbabilities() {
    }

    public CompareProbabilities(GENESIS_Environment _GENESIS_Environment) {
        this._GENESIS_Environment = _GENESIS_Environment;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new CompareProbabilities().test();
        //testCreateScatterPlot0();
        // TODO code application logic here
    }

    public void test() {
        //test0();
        //test1();
        //test2();
        //test3();
        //test4();
    }

//    /**
//     * This plots expected against observed probabilities for a single run
//     */
//    public void test0() {
//        System.out.println("<test0>");
//        _GENESIS_Environment = new GENESIS_Environment();
//        File Directory = new File("/scratch01/Work/Projects/GENESIS/workspace/");
//        File output_Directory = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/0/data/Demographics/0_99/0/");
//        _GENESIS_Environment.Directory = Directory;
//        _GENESIS_Environment.Time = new GENESIS_Time(1993, 0);
//        _GENESIS_Environment.DecimalPlacePrecisionForCalculations = 10;
//        String fileSeparator = System.getProperty("file.separator");
//        /**
//         * Initialise input_Demographics
//         */
//        GENESIS_Demographics input_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//        File inputMortalityRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/0/metadata/inputMortalityRate.xml");
//        File inputMiscarriageRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/0/metadata/inputMiscarriageRate.xml");
//        File inputFertilityRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/0/metadata/inputFertilityRate.xml");
////        input_Demographics._Mortality = new GENESIS_Mortality(
////                _GENESIS_Environment,
////                inputMortalityRate_File);
////        input_Demographics._Miscarriage = new GENESIS_Miscarriage(
////                _GENESIS_Environment,
////                inputMiscarriageRate_File);
////        input_Demographics._Fertility = new GENESIS_Fertility(
////                _GENESIS_Environment,
////                input_Demographics._Mortality,
////                input_Demographics._Miscarriage,
////                inputFertilityRate_File);
//        /**
//         * Initialise output_Demographics
//         */
//        GENESIS_Demographics output_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//        File outputMortalityRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/0/data/Demographics/0_99/0/Mortality_1992.xml");
////        File outputMiscarriageRate_File = new File(
////                Directory + "/DemographicModel_Aspatial_1/0_99/0/data/Demographics/0_99/0/Miscarriage_1992.xml");
//        File outputFertilityRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/0/data/Demographics/0_99/0/Fertility_1992.xml");
//        output_Demographics._Mortality = new GENESIS_Mortality(
//                _GENESIS_Environment,
//                outputMortalityRate_File);
////        output_Demographics._Fertility = new GENESIS_Fertility(
////                _GENESIS_Environment,
////                outputFertilityRate_File);
//        BigDecimal sumOfSquaredErrors = compare(
//                _GENESIS_Environment,
//                input_Demographics,
//                output_Demographics,
//                output_Directory,
//                _GENESIS_Environment.DecimalPlacePrecisionForCalculations);
//        System.out.println("sumOfSquaredErrors " + sumOfSquaredErrors);
//        System.out.println("</test0>");
//    }
//
//    /**
//     * This plots expected against observed probabilities for two runs 
//     */
//    public void test1() {
//        System.out.println("<test1>");
//        _GENESIS_Environment = new GENESIS_Environment();
//        File Directory = new File("/scratch01/Work/Projects/GENESIS/workspace/test/");
//        File output_Directory = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/6/data/Demographics/0_99/0/");
//        _GENESIS_Environment.Directory = Directory;
//        _GENESIS_Environment.Time = new GENESIS_Time(1993, 0);
//        XMLConverter a_XMLConverter = new XMLConverter(_GENESIS_Environment);
//        String fileSeparator = System.getProperty("file.separator");
//        /**
//         * Initialise input_Demographics
//         */
//        GENESIS_Demographics input_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//        File inputMortalityRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/6/metadata/MortalityRate_Leeds_1992.xml");
//        File inputFertilityRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/6/metadata/FertilityRate_Leeds_1992.xml");
//        input_Demographics._Mortality = new GENESIS_Mortality(
//                _GENESIS_Environment,
//                a_XMLConverter,
//                inputMortalityRate_File);
//        input_Demographics._Fertility = new GENESIS_Fertility(
//                _GENESIS_Environment,
//                a_XMLConverter,
//                input_Demographics._Mortality,
//                inputFertilityRate_File);
//        /**
//         * Initialise output_Demographics
//         */
//        HashMap<Integer, GENESIS_Demographics> output_Demographics_HashMap = new HashMap<Integer, GENESIS_Demographics>();
//        GENESIS_Demographics a_Output_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//        File a_outputMortalityRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/6/data/Demographics/0_99/0/Mortality_1993.xml");
//        File a_outputFertilityRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/6/data/Demographics/0_99/0/Fertility_1993.xml");
//        a_Output_Demographics._Mortality = new GENESIS_Mortality(
//                _GENESIS_Environment,
//                a_XMLConverter,
//                a_outputMortalityRate_File);
//        a_Output_Demographics._Fertility = new GENESIS_Fertility(
//                _GENESIS_Environment,
//                a_XMLConverter,
//                a_Output_Demographics._Mortality,
//                a_outputFertilityRate_File);
//        output_Demographics_HashMap.put(0, a_Output_Demographics);
//        GENESIS_Demographics b_Output_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//        File b_outputMortalityRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/7/data/Demographics/0_99/0/Mortality_1993.xml");
//        File b_outputFertilityRate_File = new File(
//                Directory + "/DemographicModel_Aspatial_1/0_99/7/data/Demographics/0_99/0/Fertility_1993.xml");
//        b_Output_Demographics._Mortality = new GENESIS_Mortality(
//                _GENESIS_Environment,
//                a_XMLConverter,
//                b_outputMortalityRate_File);
//        b_Output_Demographics._Fertility = new GENESIS_Fertility(
//                _GENESIS_Environment,
//                a_XMLConverter,
//                b_Output_Demographics._Mortality,
//                b_outputFertilityRate_File);
//        output_Demographics_HashMap.put(1, b_Output_Demographics);
//        compare(
//                _GENESIS_Environment,
//                input_Demographics,
//                output_Demographics_HashMap,
//                output_Directory);
//        System.out.println("</test1>");
//    }
//
//    /**
//     * This plots expected against observed probabilities for four runs 
//     */
//    public void test2() {
//        System.out.println("<test2>");
//        _GENESIS_Environment = new GENESIS_Environment();
//        File Directory = new File("/scratch01/Work/Projects/GENESIS/workspace/");
//        File _DirectoryTest = new File(Directory, "test");
//        File output_Directory = new File(
//                _DirectoryTest + "/DemographicModel_Aspatial_1/0_99/6/data/Demographics/0_99/0/");
//        _GENESIS_Environment.Directory = _DirectoryTest;
//        _GENESIS_Environment.Time = new GENESIS_Time(1993, 0);
//        XMLConverter a_XMLConverter = new XMLConverter(_GENESIS_Environment);
//        String fileSeparator = System.getProperty("file.separator");
//        /**
//         * Initialise input_Demographics
//         */
//        GENESIS_Demographics input_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//        File inputMortalityRate_File = new File(
//                Directory + "/0_99/6/metadata/MortalityRate_Leeds_1992.xml");
//        File inputFertilityRate_File = new File(
//                Directory + "/0_99/6/metadata/FertilityRate_Leeds_1992.xml");
//        input_Demographics._Mortality = new GENESIS_Mortality(
//                _GENESIS_Environment,
//                a_XMLConverter,
//                inputMortalityRate_File);
//        input_Demographics._Fertility = new GENESIS_Fertility(
//                _GENESIS_Environment,
//                a_XMLConverter,
//                input_Demographics._Mortality,
//                inputFertilityRate_File);
//        /**
//         * Initialise output_Demographics
//         */
//        HashMap<Integer, GENESIS_Demographics> output_Demographics_HashMap = new HashMap<Integer, GENESIS_Demographics>();
//        int firstRunID = 4;
//        for (int i = 0; i < 4; i++) {
//            GENESIS_Demographics a_Output_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//            File a_outputMortalityRate_File = new File(
//                    Directory + "/0_99/"
//                    + (firstRunID + i)
//                    + "/data/Demographics/0_99/0/Mortality_1993.xml");
//            File a_outputFertilityRate_File = new File(
//                    Directory + "/0_99/"
//                    + (firstRunID + i)
//                    + "/data/Demographics/0_99/0/Fertility_1993.xml");
//            a_Output_Demographics._Mortality = new GENESIS_Mortality(
//                    _GENESIS_Environment,
//                    a_XMLConverter,
//                    a_outputMortalityRate_File);
//            a_Output_Demographics._Fertility = new GENESIS_Fertility(
//                    _GENESIS_Environment,
//                    a_XMLConverter,
//                    a_Output_Demographics._Mortality,
//                    a_outputFertilityRate_File);
//            output_Demographics_HashMap.put(i, a_Output_Demographics);
//        }
//        compare(
//                _GENESIS_Environment,
//                input_Demographics,
//                output_Demographics_HashMap,
//                output_Directory);
//        System.out.println("</test2>");
//    }
//
//    /**
//     * This plots expected against observed probabilities for four runs 
//     */
//    public void test3() {
//        System.out.println("<test3>");
//        _GENESIS_Environment = new GENESIS_Environment();
//        File Directory = new File("/scratch01/Work/Projects/GENESIS/workspace/");
//        //File _DirectoryTest = new File(Directory, "test");
//        File _DirectoryTest = Directory;
//        _GENESIS_Environment.Directory = _DirectoryTest;
//        XMLConverter a_XMLConverter = new XMLConverter(_GENESIS_Environment);
//
//        int index = 0;
//        int firstRunID = 0;
//        int numberOfRunsPerYear = 4;
//
//        int year;
//        File output_Directory;
//        String fileSeparator = System.getProperty("file.separator");
//        GENESIS_Demographics input_Demographics;
//        File inputMortalityRate_File;
//        File inputFertilityRate_File;
//        HashMap<Integer, GENESIS_Demographics> output_Demographics_HashMap;
//        GENESIS_Demographics a_Output_Demographics;
//        File a_outputMortalityRate_File;
//        File a_outputFertilityRate_File;
//
//        // Comparison for first year when there is input population statistics       
//        year = 1991;
//        output_Directory = new File(
//                _DirectoryTest + "/0_99/comparison/" + (year) + "/");
//        _GENESIS_Environment.Time = new GENESIS_Time(year + 1, 0);
//        output_Directory = new File(
//                _DirectoryTest + "/0_99/comparison/" + (year) + "/");
//        _GENESIS_Environment.Time = new GENESIS_Time(year + 1, 0);
//        /**
//         * Initialise input_Demographics
//         */
//        input_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//        inputMortalityRate_File = new File(
//                Directory + "/0_99/" + firstRunID + "/metadata/MortalityRate_Leeds_" + year + ".xml");
//        inputFertilityRate_File = new File(
//                Directory + "/0_99/" + firstRunID + "/metadata/FertilityRate_Leeds_" + year + ".xml");
//        input_Demographics._Mortality = new GENESIS_Mortality(
//                _GENESIS_Environment,
//                a_XMLConverter,
//                inputMortalityRate_File);
//        input_Demographics._Fertility = new GENESIS_Fertility(
//                _GENESIS_Environment,
//                a_XMLConverter,
//                input_Demographics._Mortality,
//                inputFertilityRate_File);
//        /**
//         * Initialise output_Demographics
//         */
//        output_Demographics_HashMap = new HashMap<Integer, GENESIS_Demographics>();
//        for (int i = 0; i < numberOfRunsPerYear; i++) {
//            index = firstRunID + i;
//            a_Output_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//            a_outputMortalityRate_File = new File(
//                    Directory + "/0_99/"
//                    + index
//                    + "/data/Demographics/0_99/0/Mortality_" + (year + 1) + ".xml");
//            a_outputFertilityRate_File = new File(
//                    Directory + "/0_99/"
//                    + index
//                    + "/data/Demographics/0_99/0/Fertility_" + (year + 1) + ".xml");
//            a_Output_Demographics._Mortality = new GENESIS_Mortality(
//                    _GENESIS_Environment,
//                    a_XMLConverter,
//                    a_outputMortalityRate_File);
//            a_Output_Demographics._Fertility = new GENESIS_Fertility(
//                    _GENESIS_Environment,
//                    a_XMLConverter,
//                    a_Output_Demographics._Mortality,
//                    a_outputFertilityRate_File);
//            output_Demographics_HashMap.put(i, a_Output_Demographics);
//        }
//        compare(
//                _GENESIS_Environment,
//                input_Demographics,
//                output_Demographics_HashMap,
//                output_Directory);
//        firstRunID += numberOfRunsPerYear;
//
//
//        for (year = 1992; year < 2001; year++) {
//            // output_Directory = new File(
//            //        _DirectoryTest + "/DemographicModel_Aspatial_1/0_99/" + (year + 1) + "/");
//            output_Directory = new File(
//                    _DirectoryTest + "/0_99/comparison/" + (year) + "/");
//            _GENESIS_Environment.Time = new GENESIS_Time(year + 1, 0);
//
//
//            /**
//             * Initialise input_Demographics
//             */
//            input_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//            inputMortalityRate_File = new File(
//                    Directory + "/0_99/" + firstRunID + "/metadata/MortalityRate_Leeds_" + year + ".xml");
//            inputFertilityRate_File = new File(
//                    Directory + "/0_99/" + firstRunID + "/metadata/FertilityRate_Leeds_" + year + ".xml");
//            input_Demographics._Mortality = new GENESIS_Mortality(
//                    _GENESIS_Environment,
//                    a_XMLConverter,
//                    inputMortalityRate_File);
//            input_Demographics._Fertility = new GENESIS_Fertility(
//                    _GENESIS_Environment,
//                    a_XMLConverter,
//                    input_Demographics._Mortality,
//                    inputFertilityRate_File);
//            /**
//             * Initialise output_Demographics
//             */
//            output_Demographics_HashMap = new HashMap<Integer, GENESIS_Demographics>();
//            for (int i = 0; i < numberOfRunsPerYear; i++) {
//                index = firstRunID + i;
//                a_Output_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//                a_outputMortalityRate_File = new File(
//                        Directory + "/0_99/"
//                        + index
//                        + "/data/Demographics/0_99/0/Mortality_" + (year + 1) + ".xml");
//                a_outputFertilityRate_File = new File(
//                        Directory + "/0_99/"
//                        + index
//                        + "/data/Demographics/0_99/0/Fertility_" + (year + 1) + ".xml");
//                a_Output_Demographics._Mortality = new GENESIS_Mortality(
//                        _GENESIS_Environment,
//                        a_XMLConverter,
//                        a_outputMortalityRate_File);
//                a_Output_Demographics._Fertility = new GENESIS_Fertility(
//                        _GENESIS_Environment,
//                        a_XMLConverter,
//                        a_Output_Demographics._Mortality,
//                        a_outputFertilityRate_File);
//                output_Demographics_HashMap.put(i, a_Output_Demographics);
//            }
//            compare(
//                    _GENESIS_Environment,
//                    input_Demographics,
//                    output_Demographics_HashMap,
//                    output_Directory);
//            firstRunID += numberOfRunsPerYear;
//        }
//        System.out.println("</test4>");
//    }
//    /**
//     * This plots expected against observed probabilities for four runs 
//     */
//    public void test4() {
//        System.out.println("<test4>");
//        _GENESIS_Environment = new GENESIS_Environment();
//        File Directory = new File("/scratch01/Work/Projects/GENESIS/workspace/DemographicModel_Aspatial_1/");
//        //File _DirectoryTest = new File(Directory, "test");
//        File _DirectoryTest = Directory;
//        _GENESIS_Environment.Directory = _DirectoryTest;
//        int index = 0;
//        int firstRunID = 5;
//        int numberOfRunsPerYear = 2;
//
//        int year;
//        File output_Directory;
//        String fileSeparator = System.getProperty("file.separator");
//        GENESIS_Demographics input_Demographics;
//        File inputPopulation_File;
//        File inputMortalityRate_File;
//        File inputFertilityRate_File;
//        HashMap<Integer, GENESIS_Demographics> output_Demographics_HashMap;
//        GENESIS_Demographics a_Output_Demographics;
//        File a_outputPopulation_File;
//        File a_outputMortalityRate_File;
//        File a_outputFertilityRate_File;
//
//        // Comparison for first year when there is input population statistics       
//        year = 1991;
//        output_Directory = new File(
//                _DirectoryTest + "/0_99/comparison/" + (year) + "/");
//        _GENESIS_Environment.Time = new GENESIS_Time(year + 1, 0);
//        output_Directory = new File(
//                _DirectoryTest + "/0_99/comparison/" + (year) + "/");
//        _GENESIS_Environment.Time = new GENESIS_Time(year + 1, 0);
//        /**
//         * Initialise input_Demographics
//         */
//        input_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//        inputPopulation_File = new File(
//                Directory + "/0_99/" + firstRunID + "/metadata/inputPopulation.xml");
//        inputMortalityRate_File = new File(
//                Directory + "/0_99/" + firstRunID + "/metadata/inputMortalityRate.xml");
//        inputFertilityRate_File = new File(
//                Directory + "/0_99/" + firstRunID + "/metadata/inputFertilityRate.xml");
//        input_Demographics._GENESIS_Population_TreeMap = new GENESIS_Population(
//                _GENESIS_Environment,
//                inputPopulation_File,
//                BigInteger.ONE);
//        input_Demographics._Mortality = new GENESIS_Mortality(
//                _GENESIS_Environment,
//                inputMortalityRate_File);
//        input_Demographics._Fertility = new GENESIS_Fertility(
//                _GENESIS_Environment,
//                input_Demographics._Mortality,
//                inputFertilityRate_File);
//        /**
//         * Initialise output_Demographics
//         */
//        output_Demographics_HashMap = new HashMap<Integer, GENESIS_Demographics>();
//        for (int i = 0; i < numberOfRunsPerYear; i++) {
//            index = firstRunID + i;
//            a_Output_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
//            a_outputPopulation_File = new File(
//                    Directory + "/0_99/"
//                    + index
//                    + "/data/Demographics/0_99/0/Population_" + (year + 1) + ".xml");
//            a_outputMortalityRate_File = new File(
//                    Directory + "/0_99/"
//                    + index
//                    + "/data/Demographics/0_99/0/Mortality_" + (year + 1) + ".xml");
//            a_outputFertilityRate_File = new File(
//                    Directory + "/0_99/"
//                    + index
//                    + "/data/Demographics/0_99/0/Fertility_" + (year + 1) + ".xml");
//            a_Output_Demographics._GENESIS_Population_TreeMap = new GENESIS_Population(
//                    _GENESIS_Environment,
//                    a_XMLConverter,
//                    a_outputPopulation_File);
//            a_Output_Demographics._Mortality = new GENESIS_Mortality(
//                    _GENESIS_Environment,
//                    a_XMLConverter,
//                    a_outputMortalityRate_File);
//            a_Output_Demographics._Fertility = new GENESIS_Fertility(
//                    _GENESIS_Environment,
//                    a_XMLConverter,
//                    a_Output_Demographics._Mortality,
//                    a_outputFertilityRate_File);
//            output_Demographics_HashMap.put(i, a_Output_Demographics);
//        }
//        compare(
//                _GENESIS_Environment,
//                input_Demographics,
//                output_Demographics_HashMap,
//                output_Directory);
//        firstRunID += numberOfRunsPerYear;
////        for (year = 1992; year < 2001; year++) {
////            // output_Directory = new File(
////            //        _DirectoryTest + "/DemographicModel_Aspatial_1/0_99/" + (year + 1) + "/");
////            output_Directory = new File(
////                    _DirectoryTest + "/0_99/comparison/" + (year) + "/");
////            _GENESIS_Environment.Time = new GENESIS_Time(year + 1, 0);
////
////
////            /**
////             * Initialise input_Demographics
////             */
////            input_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
////            inputPopulation_File = new File(
////                    Directory + "/0_99/" + firstRunID + "/data/MortalityRate_Leeds_" + year + ".xml");
////            inputMortalityRate_File = new File(
////                    Directory + "/0_99/" + firstRunID + "/metadata/MortalityRate_Leeds_" + year + ".xml");
////            inputFertilityRate_File = new File(
////                    Directory + "/0_99/" + firstRunID + "/metadata/FertilityRate_Leeds_" + year + ".xml");
////            input_Demographics._Mortality = new GENESIS_Mortality(
////                    _GENESIS_Environment,
////                    inputMortalityRate_File);
////            input_Demographics._Fertility = new GENESIS_Fertility(
////                    _GENESIS_Environment,
////                    input_Demographics._Mortality,
////                    inputFertilityRate_File);
////            /**
////             * Initialise output_Demographics
////             */
////            output_Demographics_HashMap = new HashMap<Integer, GENESIS_Demographics>();
////            for (int i = 0; i < numberOfRunsPerYear; i++) {
////                index = firstRunID + i;
////                a_Output_Demographics = new GENESIS_Demographics(_GENESIS_Environment);
////                a_outputMortalityRate_File = new File(
////                        Directory + "/0_99/"
////                        + index
////                        + "/data/Demographics/0_99/0/Mortality_" + (year + 1) + ".xml");
////                a_outputFertilityRate_File = new File(
////                        Directory + "/0_99/"
////                        + index
////                        + "/data/Demographics/0_99/0/Fertility_" + (year + 1) + ".xml");
////                a_Output_Demographics._Mortality = new GENESIS_Mortality(
////                        _GENESIS_Environment,
////                        a_outputMortalityRate_File);
////                a_Output_Demographics._Fertility = new GENESIS_Fertility(
////                        _GENESIS_Environment,
////                        a_Output_Demographics._Mortality,
////                        a_outputFertilityRate_File);
////                output_Demographics_HashMap.put(i, a_Output_Demographics);
////            }
////            compare(
////                    _GENESIS_Environment,
////                    input_Demographics,
////                    output_Demographics_HashMap,
////                    output_Directory);
////            firstRunID += numberOfRunsPerYear;
////        }
//        System.out.println("</test4>");
//    }

    
    public static BigDecimal compare(
            String areaCode,
            GENESIS_Environment _GENESIS_Environment,
            GENESIS_Demographics startDemographics,
            GENESIS_Demographics endDemographics,
            File output_Directory,
            int decimalPlacePrecisionForCalculations) {
        String sourceMethod = "compare(GENESIS_Environment,Demographics,Demographics,File)";
        getLogger().entering(sourceClass, sourceMethod);
        /**
         * Compare input_Demographics and output_Demographics
         */
        String name;
        Integer year = new Integer((int) _GENESIS_Environment.Time.getYear());

        // Get expected births using fertility rate and
        Object[] expectedAndSumOfSquaredDifference = GENESIS_Demographics.getExpectedAndSumOfSquaredDifference(areaCode,
                startDemographics,
                endDemographics,
                decimalPlacePrecisionForCalculations,
                _GENESIS_Environment.HOOME);
        BigDecimal sumOfSquaredErrors = (BigDecimal) expectedAndSumOfSquaredDifference[0];
        TreeMap<Integer, BigDecimal> femaleTheoreticalEndYearAgedPopulation = (TreeMap<Integer, BigDecimal>) expectedAndSumOfSquaredDifference[1];
        TreeMap<Integer, BigDecimal> maleTheoreticalEndYearAgedPopulation = (TreeMap<Integer, BigDecimal>) expectedAndSumOfSquaredDifference[2];
        TreeMap<Integer, BigDecimal> femaleTheoreticalDeadPopulation = (TreeMap<Integer, BigDecimal>) expectedAndSumOfSquaredDifference[3];
        TreeMap<Integer, BigDecimal> maleTheoreticalDeadPopulation = (TreeMap<Integer, BigDecimal>) expectedAndSumOfSquaredDifference[4];
        TreeMap<Integer, BigDecimal> femaleTheoreticalFertility = (TreeMap<Integer, BigDecimal>) expectedAndSumOfSquaredDifference[5];


        String title;
        String rangeAxisLabel;
        boolean convertToDays;
//Visualisations now output as the simulation runs...
//        /**
//         * Visualise theoretical end year populations and fertility
//         * (This could be done just once in a comparison step as it will be the 
//         * same for any simulation with the same input)
//         */
//        convertToDays = false;
//        title = "Population _Theoretically_Expected_Living_ Year " + _GENESIS_Environment.Time._Year;
//        rangeAxisLabel = "Population";
//        input_Demographics.outputAgeGenderPlot(
//                title,
//                rangeAxisLabel,
//                output_Directory,
//                convertToDays,
//                femaleTheoreticalEndYearAgedPopulation,
//                maleTheoreticalEndYearAgedPopulation);
//
//        name = "Fertility Female";
//        output(
//                input_Demographics._Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap,
//                output_Demographics._Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap,
//                name,
//                year,
//                output_Directory);
//
//        name = "Mortality Female";
//        output(
//                input_Demographics._Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap,
//                output_Demographics._Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap,
//                name,
//                year,
//                output_Directory);
//
//        name = "Mortality Male";
//        output(
//                input_Demographics._Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap,
//                output_Demographics._Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap,
//                name,
//                year,
//                output_Directory);
        getLogger().exiting(sourceClass, sourceMethod);
        return sumOfSquaredErrors;
    }

//    /**
//     * Produces some simulation outputs and returns a set of statistics which 
//     * represent the difference between the modelled and expected populations
//     * which may be used as a basis of examining the model sensitivities and the 
//     * spread of results and may also be used to pick a trend or more extreme 
//     * output.
//     * @param _GENESIS_Environment
//     * @param input_Demographics
//     * @param output_Demographics_HashMap
//     * @param output_Directory
//     * @return 
//     */
//    public static HashMap<GENESIS_AgeBound, BigDecimal> compare(
//            GENESIS_Environment _GENESIS_Environment,
//            int decimalPlacePrecisionForCalculation,
//            int decimalPlacePrecisionForDisplay,
//            GENESIS_Demographics input_Demographics,
//            HashMap<GENESIS_AgeBound, GENESIS_Demographics> output_Demographics_HashMap,
//            File output_Directory) {
//        String sourceMethod = "compare(GENESIS_Environment,Demographics,HashMap,File)";
//        getLogger().entering(sourceClass, sourceMethod);
//        HashMap<GENESIS_AgeBound, BigDecimal> result;
//        /**
//         * Compare input_Demographics and output_Demographics
//         */
//        BigDecimal inputProbability;
//        BigDecimal outputProbability;
//        String name;
//        Integer year = new Integer((int) _GENESIS_Environment.Time.getYear());
//
//        Iterator<GENESIS_AgeBound> ite;
//        GENESIS_AgeBound key;
//
//        // Get expected births using fertility rate and
//        Object[] expectedAndSumOfSquaredDifference = input_Demographics.getExpectedAndSumOfSquaredDifferences(
//                input_Demographics,
//                output_Demographics_HashMap);
//        HashMap<GENESIS_AgeBound, BigDecimal> sumOfSquaredErrors_HashMap = 
//                (HashMap<GENESIS_AgeBound, BigDecimal>) expectedAndSumOfSquaredDifference[0];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalEndYearAgedPopulation = 
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) expectedAndSumOfSquaredDifference[1];
//        TreeMap<GENESIS_AgeBound, BigDecimal> maleTheoreticalEndYearAgedPopulation = 
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) expectedAndSumOfSquaredDifference[2];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalDeadPopulation = 
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) expectedAndSumOfSquaredDifference[3];
//        TreeMap<GENESIS_AgeBound, BigDecimal> maleTheoreticalDeadPopulation = 
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) expectedAndSumOfSquaredDifference[4];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalFertility = 
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) expectedAndSumOfSquaredDifference[5];
//
//        String title;
//        String rangeAxisLabel;
//        boolean convertDaysToYears;
//
//        /**
//         * Visualise theoretical end year populations and fertility
//         * (This could be done just once in a comparison step as it will be the 
//         * same for any simulation with the same input)
//         */
//        convertDaysToYears = true;
//        title = "Theoretical Age Gender Plot Living Population Year " + _GENESIS_Environment.Time.getYear();
//        rangeAxisLabel = "Population";
//        input_Demographics.outputAgeGenderPlot(
//                title,
//                rangeAxisLabel,
//                output_Directory,
//                convertDaysToYears,
//                decimalPlacePrecisionForCalculation,
//                decimalPlacePrecisionForDisplay,
//                femaleTheoreticalEndYearAgedPopulation,
//                maleTheoreticalEndYearAgedPopulation);
//
//        name = "Fertility Female";
//        if (true) {
//            HashMap<GENESIS_AgeBound, TreeMap<GENESIS_AgeBound, BigDecimal>> fertilityProbabilitiesByAgeForFemales = 
//                    new HashMap<GENESIS_AgeBound, TreeMap<GENESIS_AgeBound, BigDecimal>>();
//            ite = output_Demographics_HashMap.keySet().iterator();
//            while (ite.hasNext()) {
//                key = ite.next();
//                GENESIS_Demographics output_Demographics = output_Demographics_HashMap.get(key);
//                fertilityProbabilitiesByAgeForFemales.put(
//                        key, 
//                        output_Demographics._Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap);
//            }
//            output(
//                    input_Demographics._Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap,
//                    fertilityProbabilitiesByAgeForFemales,
//                    name,
//                    year,
//                    output_Directory);
//        }
//
//        name = "Mortality Female";
//        if (true) {
//            HashMap<GENESIS_AgeBound, TreeMap<GENESIS_AgeBound, BigDecimal>> mortalityProbabilitiesByAgeForFemales = 
//                    new HashMap<GENESIS_AgeBound, TreeMap<GENESIS_AgeBound, BigDecimal>>();
//            ite = output_Demographics_HashMap.keySet().iterator();
//            while (ite.hasNext()) {
//                key = ite.next();
//                GENESIS_Demographics output_Demographics = output_Demographics_HashMap.get(key);
//                mortalityProbabilitiesByAgeForFemales.put(
//                        key, 
//                        output_Demographics._Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap);
//            }
//            output(
//                    input_Demographics._Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap,
//                    mortalityProbabilitiesByAgeForFemales,
//                    name,
//                    year,
//                    output_Directory);
//        }
//
//        name = "Mortality Male";
//        if (true) {
//            HashMap<GENESIS_AgeBound, TreeMap<GENESIS_AgeBound, BigDecimal>> mortalityProbabilitiesByAgeForMales = 
//                    new HashMap<GENESIS_AgeBound, TreeMap<GENESIS_AgeBound, BigDecimal>>();
//            ite = output_Demographics_HashMap.keySet().iterator();
//            while (ite.hasNext()) {
//                key = ite.next();
//                GENESIS_Demographics output_Demographics = output_Demographics_HashMap.get(key);
//                mortalityProbabilitiesByAgeForMales.put(
//                        key, 
//                        output_Demographics._Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap);
//            }
//            output(
//                    input_Demographics._Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap,
//                    mortalityProbabilitiesByAgeForMales,
//                    name,
//                    year,
//                    output_Directory);
//        }
//        result = sumOfSquaredErrors_HashMap;
//        getLogger().exiting(sourceClass, sourceMethod);
//        return result;
//    }
    /**
     * expected are the y values observed or modelled are the x values expected
     * are the input probabilities to a simulation observed are the resulting
     * probabilities of a simulation
     *
     * @param expected
     * @param observed
     * @param name
     * @param output_Directory
     * @param year
     * @param roundingMode
     * @param decimalPlacePrecisionForDisplay
     */
    public static void output(
            ExecutorService executorService,
            TreeMap<GENESIS_AgeBound, BigDecimal> expected,
            TreeMap<GENESIS_AgeBound, BigDecimal> observed,
            String title,
            int dataWidth,
            int dataHeight,
            String xAxisLabel,
            String yAxisLabel,
            boolean drawOriginLinesOnPlot,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode roundingMode,
            Long year,
            File output_Directory) {
        String sourceMethod = "output(TreeMap,TreeMap,String,Long,File)";
        getLogger().entering(sourceClass, sourceMethod);
        String format = "PNG";
        String outputImageFileNamePrefix;
        outputImageFileNamePrefix = title.replaceAll(" ", "_");
        //@TODO Check this is correct outFile.
        File outFile = new File(output_Directory,
                outputImageFileNamePrefix + "." + format);
        GENESIS_ScatterPlotAndLinearRegression plot = new GENESIS_ScatterPlotAndLinearRegression(
                executorService,
                outFile,
                format,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode);
        plot.setData(expected, observed);
        plot.run();
        getLogger().exiting(sourceClass, sourceMethod);
    }

//    /**
//     * expected are the y values
//     * observed or modelled are the x values
//     * expected are the input probabilities to a simulation
//     * observed are the resulting probabilities of a simulation
//     * @param expected
//     * @param observed
//     * @param name
//     * @param output_Directory
//     */
//    public static void output(
//            TreeMap<GENESIS_AgeBound, BigDecimal> expected,
//            HashMap<GENESIS_AgeBound, TreeMap<GENESIS_AgeBound, BigDecimal>> observed_HashMap,
//            String name,
//            Integer year,
//            File output_Directory) {
//        String sourceMethod = "output(TreeMap,HashMap,String,Integer,File)";
//        getLogger().entering(sourceClass, sourceMethod);
//        String xAxisLabel_String;
//        String yAxisLabel_String;
//        xAxisLabel_String = "X = Input " + name + " " + year + " Probability";
//        yAxisLabel_String = "Y = Model " + name + " " + year + " Probability";
//        BigDecimal multiplicand = new BigDecimal("1");
//
//        XYPlot a_XYPlot = new XYPlot(); // Parent plot
//
//        XYPlot[] componentXYPlots = new XYPlot[observed_HashMap.size()];
//
//        Color[] colors = GENESIS_JFreeChartRendering.getChartColorPaintArray(observed_HashMap.size() + 1);
//
//        // Create component plots
//        int index = 0;
//        String seriesName;
//        Iterator<GENESIS_AgeBound> ite = observed_HashMap.keySet().iterator();
//        GENESIS_AgeBound key;
//        while (ite.hasNext()) {
//            key = ite.next();
//            TreeMap<GENESIS_AgeBound, BigDecimal> observed = observed_HashMap.get(key);
//            double[][] data = getDataArray(
//                    expected,
//                    observed,
//                    multiplicand);
//// Juggling the data here is wrong!
////            double[][] dataJuggled = juggleData(data);
////            data = dataJuggled;
//            seriesName = key.toString();
//            int age_int = key.getAgeMin().intValue();
//            componentXYPlots[age_int] = GENESIS_RegressionXYPlot.createXYPlot(
//                    name, seriesName,
//                    colors[age_int + 1],
//                    colors[0],
//                    xAxisLabel_String,
//                    yAxisLabel_String,
//                    data);
//        }
//        // Standardise the ranges
//        // It may be that for some plots lines would want rebounding to extend 
//        // within new range, but this is not wanted for these particular plots. 
//        Range rangeAxisRange = GENESIS_XYPlot.getRangeAxisRange(componentXYPlots);
//        NumberAxis rangeNumberAxis = new NumberAxis();
//        rangeNumberAxis.setLabel(yAxisLabel_String);
//        rangeNumberAxis.setRange(rangeAxisRange);
//        a_XYPlot.setRangeAxis(rangeNumberAxis);
//        Range domainAxisRange = GENESIS_XYPlot.getDomainAxisRange(componentXYPlots);
//        NumberAxis domainNumberAxis = new NumberAxis();
//        domainNumberAxis.setLabel(xAxisLabel_String);
//        domainNumberAxis.setRange(domainAxisRange);
//        a_XYPlot.setDomainAxis(domainNumberAxis);
//        for (int j = 0; j < componentXYPlots.length; j++) {
//            XYPlot a_RegressionXYPlot = componentXYPlots[j];
//            a_RegressionXYPlot.setRangeAxis(rangeNumberAxis);
//            a_RegressionXYPlot.setDomainAxis(domainNumberAxis);
//            a_RegressionXYPlot.getSeriesCount();
//            int regressionXYPlotDatasetCount = a_RegressionXYPlot.getDatasetCount();
//            for (int i = 0; i < regressionXYPlotDatasetCount; i++) {
//                XYDataset a_XYDataset = a_RegressionXYPlot.getDataset(i);
//                a_XYPlot.setDataset(i + index, a_XYDataset);
//                XYItemRenderer a_XYItemRenderer = a_RegressionXYPlot.getRendererForDataset(a_XYDataset);
//                a_XYPlot.setRenderer(i + index, a_XYItemRenderer);
////                a_XYPlot.setDomainAxis(a_RegressionXYPlot.getDomainAxis());
////                a_XYPlot.setRangeAxis(a_RegressionXYPlot.getRangeAxis());
//                a_XYPlot.mapDatasetToRangeAxis(i + index, 0);
//                a_XYPlot.mapDatasetToDomainAxis(i + index, 0);
//
//            }
//            index += regressionXYPlotDatasetCount;
//        }
//        // Variables for chart rendering and output
//        int width = 1000;
//        int height = 1000;
//        String outputImageFileNamePrefix;
//        String type = "PNG";
//        PlotOrientation orientation = PlotOrientation.VERTICAL;
//        // Test parent plot creation (points and regression line)
//        a_XYPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
//        //a_XYPlot.setOrientation(PlotOrientation.VERTICAL);
//        //a_XYPlot.mapDatasetToRangeAxis(index, 0);
//        outputImageFileNamePrefix = name.replaceAll(" ", "_");
//        outputImageFileNamePrefix += "_Probability_Comparison_YEqualXLineRegressionLineAndScatterPlot" + index + "_" + year;
//        GENESIS_XYPlot.testXYPlotChartCreation(name, a_XYPlot,
//                outputImageFileNamePrefix, output_Directory, type);
//        getLogger().exiting(sourceClass, sourceMethod);
//    }
//    /**
//     * expected are the y values
//     * observed or modelled are the x values
//     * expected are the input probabilities to a simulation
//     * observed are the resulting probabilities of a simulation
//     * @param expected
//     * @param observed
//     * @param name
//     * @param output_Directory
//     */
//    public static void output(
//            TreeMap<Integer, BigDecimal> expected,
//            HashMap<Integer, TreeMap<Integer, BigDecimal>> observed_HashMap,
//            String name,
//            Integer year,
//            File output_Directory) {
//        String xAxisLabel_String = null;
//        String yAxisLabel_String = null;
//        xAxisLabel_String = "Model " + name + " " + year + " Probability";
//        yAxisLabel_String = "Input " + name + " " + year + " Probability";
//        BigDecimal multiplicand = new BigDecimal("1");
//
//        HashMap<Integer, double[][]> juggledData_HashMap = new HashMap<Integer, double[][]>();
//
//        Iterator<Integer> ite = observed_HashMap.keySet().iterator();
//        Integer key;
//        while (ite.hasNext()) {
//            key = ite.next();
//            TreeMap<Integer, BigDecimal> observed = observed_HashMap.get(key);
//            double[][] data = getDataArray(
//                    expected,
//                    observed,
//                    multiplicand);
//            double[][] juggledData = juggleData(data);
//            juggledData_HashMap.put(key, juggledData);
//        }
//
//
//        JFreeChart a_ScatterPlot_JFreeChart = GENESIS_ScatterPlot.createJFreeChart(
//                name,
//                xAxisLabel_String,
//                yAxisLabel_String,
//                false,
//                juggledData_HashMap);
//        //printSimpleRegression(data);
//        int width = 1000;
//        int height = 1000;
//        String outputImageFileNamePrefix = name.replaceAll(" ", "_");
//        outputImageFileNamePrefix += "_Probability_Comparison_" + year;
//        String type = "PNG";
////        ImageHandler.outputImage(
////                fertilityFemales_JFreeChart,
////                width,
////                height,
////                Directory,
////                outputImageFileNamePrefix,
////                type);
//        Object[] a_RegressionParametersAndCreateXYLineChart =
//                getRegressionParametersAndCreateXYLineChart(
//                juggledData_HashMap,
//                xAxisLabel_String,
//                yAxisLabel_String);
////        ImageHandler.outputImage(
////                (JFreeChart) a_RegressionParametersAndCreateXYLineChart[1],
////                width,
////                height,
////                Directory,
////                outputImageFileNamePrefix,
////                type);
//        JFreeChart a_YEqualsXLineChart = getYEqualsXLineChart(
//                juggledData_HashMap,
//                xAxisLabel_String,
//                yAxisLabel_String);
//        JFreeChart regression_JFreeChart = createRegressionChart1(
//                name,
//                a_ScatterPlot_JFreeChart,
//                a_YEqualsXLineChart,
//                a_RegressionParametersAndCreateXYLineChart);
//        GENESIS_JFreeChart.outputJFreeChartImage(
//                regression_JFreeChart,
//                width,
//                height,
//                output_Directory,
//                outputImageFileNamePrefix,
//                type);
//    }
    /**
     * BigDecimal values in observed and expected are multiplied by multiplicand
     * and the these are converted to double precision numbers. These are
     * returned in a two dimensional array.
     *
     * @param expected
     * @param observed
     * @param multiplicand
     * @return double[][] data integrating the expected as data[0][] and the
     * observed as data[1][]
     */
    public static double[][] getDataArray(
            TreeMap<GENESIS_AgeBound, BigDecimal> expected,
            TreeMap<GENESIS_AgeBound, BigDecimal> observed,
            BigDecimal multiplicand) {
        String sourceMethod = "getDataArray(TreeMap,TreeMap,BigDecimal)";
        Logger logger = getLogger();
        logger.entering(sourceClass, sourceMethod);
        double[][] result;
        GENESIS_AgeBound key;
        BigDecimal expected_BigDecimal;
        BigDecimal observed_BigDecimal;
        Iterator<GENESIS_AgeBound> ite;
        HashSet<GENESIS_AgeBound> keys = GENESIS_Collections.getCombined_HashSet_AgeBound(
                observed.keySet(),
                expected.keySet());
        int ndata = keys.size();
//        int ndata = 0;
//        ite = keys.iterator();
//        while (ite.hasNext()) {
//            key = ite.next();
//            expected_BigDecimal = expected.get(key);
//            if (observed_BigDecimal != null || expected != null) {
//                ndata++;
//            }
//        }
        result = new double[2][ndata];
        int dataIndex = 0;
        ite = expected.keySet().iterator();
        logger.log(Level.FINEST, "Age, expected, observed");
        while (ite.hasNext()) {
            key = ite.next();
            expected_BigDecimal = expected.get(key);
            if (expected_BigDecimal == null) {
                expected_BigDecimal = BigDecimal.ZERO;
            }
            observed_BigDecimal = observed.get(key);
            if (observed_BigDecimal == null) {
                observed_BigDecimal = BigDecimal.ZERO;
            }
            result[0][dataIndex] = expected_BigDecimal.multiply(multiplicand).doubleValue();
            //result[1][dataIndex] = expected_BigDecimal.multiply(multiplicand).doubleValue();
            result[1][dataIndex] = observed_BigDecimal.multiply(multiplicand).doubleValue();
            //result[0][dataIndex] = observed_BigDecimal.multiply(multiplicand).doubleValue();
            dataIndex++;
            logger.log(Level.FINEST, "{0}, {1}, {2}", new Object[]{key, expected_BigDecimal, observed_BigDecimal});
        }
        return result;
    }

    /**
     * data[0][] are the y values data[1][] are the x values
     *
     * @param data
     * @param xAxisLabel_String
     * @param yAxisLabel_String
     * @return Object[] result where: result[0] result[1]
     *
     */
//    public static Object[] getRegressionParametersAndLinePlot(
//            double[][] data,
//            String xAxisLabel_String,
//            String yAxisLabel_String) {
//        Object[] result = new Object[2];
//        JFreeChart a_XYLineChart = null;
//        String seriesName = "Regression Line";
//        Object[] regressionParametersAndDefaultXYDataset = getRegressionParametersAndXYLineData(
//                data);
//        double[][] lineChartData = (double[][]) regressionParametersAndDefaultXYDataset[0];
//        double[] a_RegressionParameters = (double[]) regressionParametersAndDefaultXYDataset[1];
//        DefaultXYDataset regressionLineDefaultXYDataset = new DefaultXYDataset();
//        regressionLineDefaultXYDataset.addSeries(
//                seriesName,
//                lineChartData);
//        int seriesCount = regressionLineDefaultXYDataset.getSeriesCount();
//        System.out.println("seriesCount " + seriesCount);
//        String title = null;
//        boolean legend = false;
//        boolean tooltips = false;
//        boolean urls = false;
//        a_XYLineChart =
//                ChartFactory.createXYLineChart(
//                title,
//                xAxisLabel_String,
//                yAxisLabel_String,
//                regressionLineDefaultXYDataset,
//                PlotOrientation.VERTICAL,
//                legend,
//                tooltips,
//                urls);
//        result[0] = a_XYLineChart;
//        result[1] = a_RegressionParameters;
//        return result;
//    }
//    /**
//     * data[0][] are the y values
//     * data[1][] are the x values
//     * addData(double x, double y)
//     */
//    public static JFreeChart createRegressionChart0(
//            String titlePrefix,
//            double[][] juggledData,//JFreeChart a_ScatterPlot,
//            JFreeChart a_YEqualsXLineChart,
//            Object[] a_RegressionParametersAndCreateXYLineChart) {
//        JFreeChart result = null;
//        JFreeChart a_XYLineChart = (JFreeChart) a_RegressionParametersAndCreateXYLineChart[0];
//        double[] a_RegressionParameters = (double[]) a_RegressionParametersAndCreateXYLineChart[1];
//        int maxLengthOfIntString = 6;
//        String b = String.valueOf(a_RegressionParameters[0]);
//        String a = String.valueOf(a_RegressionParameters[1]);
//        String rsquare = String.valueOf(a_RegressionParameters[2]);
//        String title = titlePrefix;
//        title += "    Y="
//                + a.substring(0, Math.min(a.length(),
//                maxLengthOfIntString)) + "X";
//        if (a_RegressionParameters[0] < 0) {
//            title += b.substring(0, Math.min(b.length(),
//                    maxLengthOfIntString));
//        } else {
//            title += "+"
//                    + b.substring(0, Math.min(b.length(),
//                    maxLengthOfIntString));
//        }
//        if (a_RegressionParameters.length > 2) {
//            title += "    RSquare "
//                    + rsquare.substring(0, Math.min(rsquare.length(),
//                    maxLengthOfIntString));
//        }
//        XYLineAndShapeRenderer points_XYLineAndShapeRenderer = new XYLineAndShapeRenderer(
//                false, true);
//        String name = "Series 0";
//        DefaultXYDataset a_DefaultXYDataset = GENESIS_JFreeChart_Data.createDefaultXYDataset(
//                name,
//                juggledData);
//        XYPlot a_ScatterPlotXYPlot = new XYPlot();
//        a_ScatterPlotXYPlot.setDataset(0, a_DefaultXYDataset);
////        XYPlot a_ScatterPlotXYPlot = (XYPlot) a_ScatterPlot.getPlot();
////        XYDataset a_ScatterPlot_XYDataset = a_ScatterPlotXYPlot.getDataset();
////        a_ScatterPlotXYPlot.setDataset(0, a_ScatterPlot_XYDataset);
//        points_XYLineAndShapeRenderer.setSeriesPaint(0, Color.blue);
//        Shape a_Shape = getCross();
//        points_XYLineAndShapeRenderer.setSeriesShape(0, a_Shape);
//        a_ScatterPlotXYPlot.setRenderer(0, points_XYLineAndShapeRenderer);
//
//        // Add Lines
//        XYLineAndShapeRenderer line_XYLineAndShapeRenderer = null;
//        // Add Regression Line in red
//        XYDataset a_RegressionLineChartXYDataset = ((XYPlot) a_XYLineChart.getPlot()).getDataset();
//        line_XYLineAndShapeRenderer = new XYLineAndShapeRenderer(
//                true, false);
//        a_ScatterPlotXYPlot.setDataset(1, a_RegressionLineChartXYDataset);
//        line_XYLineAndShapeRenderer.setPaint(Color.RED);
//        MultipleXYSeriesLabelGenerator a_MultipleXYSeriesLabelGenerator = new MultipleXYSeriesLabelGenerator(title);
//        a_MultipleXYSeriesLabelGenerator.addSeriesLabel(1, title);
//        line_XYLineAndShapeRenderer.setLegendItemLabelGenerator(a_MultipleXYSeriesLabelGenerator);
//
//        a_ScatterPlotXYPlot.setRenderer(1, line_XYLineAndShapeRenderer);
//        // Add Y=X Line in green
//        XYDataset a_YEqualsXLineChartXYDataset =
//                ((XYPlot) a_YEqualsXLineChart.getPlot()).getDataset();
//        line_XYLineAndShapeRenderer = new XYLineAndShapeRenderer(
//                true, false);
//        a_ScatterPlotXYPlot.setDataset(2, a_YEqualsXLineChartXYDataset);
//        line_XYLineAndShapeRenderer.setPaint(Color.GREEN);
//        a_ScatterPlotXYPlot.setRenderer(2, line_XYLineAndShapeRenderer);
//
//        result = new JFreeChart(title, a_ScatterPlotXYPlot);
//        return result;
//    }
//
//    public static JFreeChart createRegressionChart1(
//            String titlePrefix,
//            JFreeChart a_ScatterPlot,
//            JFreeChart a_YEqualsXLineChart,
//            Object[] a_RegressionParametersAndCreateXYLineChart) {
//        JFreeChart result = null;
//        JFreeChart a_XYLineChart = (JFreeChart) a_RegressionParametersAndCreateXYLineChart[0];
//        String title = titlePrefix;
//
//        int redPart = 235;
//        int greenPart = 235;
//        int bluePart = 235;
//        Color a_Colour = new Color(redPart, greenPart, bluePart);
//
//        XYLineAndShapeRenderer points_XYLineAndShapeRenderer = new XYLineAndShapeRenderer(
//                false, true);
//        Shape a_Shape = getCross();
//        points_XYLineAndShapeRenderer.setSeriesShape(0, a_Shape);
//        XYPlot a_ScatterPlotXYPlot = (XYPlot) a_ScatterPlot.getPlot();
//        a_ScatterPlotXYPlot.setDataset(0, a_ScatterPlotXYPlot.getDataset());
//        //points_XYLineAndShapeRenderer.setSeriesPaint(0, a_Colour);
//        a_ScatterPlotXYPlot.setRenderer(0, points_XYLineAndShapeRenderer);
//
//        HashMap<Integer, double[]> regressionsParameters =
//                (HashMap<Integer, double[]>) a_RegressionParametersAndCreateXYLineChart[1];
//        Iterator<Integer> ite = regressionsParameters.keySet().iterator();
//        Integer key;
//        Integer index = 1;
//        while (ite.hasNext()) {
//            key = ite.next();
//            double[] a_RegressionParameters = (double[]) regressionsParameters.get(key);
//            int maxLengthOfIntString = 6;
//            String b = String.valueOf(a_RegressionParameters[0]);
//            String a = String.valueOf(a_RegressionParameters[1]);
//            String rsquare = String.valueOf(a_RegressionParameters[2]);
//            title += "    Y="
//                    + a.substring(0, Math.min(a.length(),
//                    maxLengthOfIntString)) + "X";
//            if (a_RegressionParameters[0] < 0) {
//                title += b.substring(0, Math.min(b.length(),
//                        maxLengthOfIntString));
//            } else {
//                title += "+"
//                        + b.substring(0, Math.min(b.length(),
//                        maxLengthOfIntString));
//            }
//            if (a_RegressionParameters.length > 2) {
//                title += "    RSquare "
//                        + rsquare.substring(0, Math.min(rsquare.length(),
//                        maxLengthOfIntString));
//            }
//            // Add Lines
//            XYLineAndShapeRenderer line_XYLineAndShapeRenderer = null;
//            // Add Regression Line in red
//            XYDataset a_RegressionLineChartXYDataset = ((XYPlot) a_XYLineChart.getPlot()).getDataset();
//            line_XYLineAndShapeRenderer = new XYLineAndShapeRenderer(
//                    true, false);
//            a_ScatterPlotXYPlot.setDataset(index, a_RegressionLineChartXYDataset);
//            line_XYLineAndShapeRenderer.setSeriesPaint(0, a_Colour);
//            a_ScatterPlotXYPlot.setRenderer(index, line_XYLineAndShapeRenderer);
//            index++;
//            // Add Y=X Line in green
//            XYDataset a_YEqualsXLineChartXYDataset =
//                    ((XYPlot) a_YEqualsXLineChart.getPlot()).getDataset();
//            line_XYLineAndShapeRenderer = new XYLineAndShapeRenderer(
//                    true, false);
//            a_ScatterPlotXYPlot.setDataset(index, a_YEqualsXLineChartXYDataset);
//            line_XYLineAndShapeRenderer.setPaint(Color.GREEN);
//            a_ScatterPlotXYPlot.setRenderer(index, line_XYLineAndShapeRenderer);
//            index++;
//        }
//        result = new JFreeChart(title, a_ScatterPlotXYPlot);
//        return result;
//    }
    /**
     * @param data double[2][n]
     * @return double[][] dataJuggled where: dataJuggled[0][] = data[1][];
     * dataJuggled[1][] = data[0][];      * <code>
    double[][] dataJuggled = new double[data.length][data[0].length];
     * for (int i = 0; i < data[0].length; i++) {
     * dataJuggled[0][i] = data[1][i];
     * dataJuggled[1][i] = data[0][i];
     * }
     * return dataJuggled;
     * </code>
     */
    public static double[][] juggleData(double[][] data) {
        double[][] juggledData = new double[data.length][data[0].length];
        for (int i = 0; i < data[0].length; i++) {
            juggledData[0][i] = data[1][i];
            juggledData[1][i] = data[0][i];
        }
        return juggledData;
    }

    private static Logger getLogger() {
        return GENESIS_Log.LOGGER;
    }
//    private static void log(
//            Level level,
//            String message) {
//        getLogger().log(level, message);
//    }
}
