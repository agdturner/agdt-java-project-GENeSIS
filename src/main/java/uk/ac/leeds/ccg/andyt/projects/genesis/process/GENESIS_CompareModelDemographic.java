package uk.ac.leeds.ccg.andyt.projects.genesis.process;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Execution;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.*;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.StartNameFileFilter;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.XMLConverter;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.CommonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.MetadataFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.fertility.FertilityType;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.metadata.MetadataType;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.parameters.ParametersType;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.math.statistics.GENESIS_Statistics;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.*;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Fertility;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Collections;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.projects.genesis.visualisation.GENESIS_AgeGenderLineChart;

/**
 * A class to generate a society. Designed so that the population can be
 * optionally and where appropriate, stored on file. This should scale to larger
 * populations although disk IO speed may be an issue...
 */
public class GENESIS_CompareModelDemographic extends GENESIS_AbstractModel {

    /**
     * Used for Logging
     */
    private static final String sourceClass = GENESIS_CompareModelDemographic.class.getName();
    private static final String sourcePackage = GENESIS_CompareModelDemographic.class.getPackage().getName();
    //private static final Logger logger = Logger.getLogger(sourcePackage);
    private TreeMap<Long, File> resultsToCompare_TreeMap;
    private PrintWriter comparisonTest_PrintWriter;
    private long _StartYear;
    private long _EndYear;
    private HashSet<Future> futures;
    //private PrintWriter comparisonDetail_PrintWriter;

    public GENESIS_CompareModelDemographic() {
    }

    /* -------------------------------------------------------------------------
     * args[0] File location containing Type 3 results
     * -------------------------------------------------------------------------
     */
    public static void main(String[] args) {
        try {
            System.out.println(System.getProperties().keySet().toString());
            System.out.println(System.getProperties().values().toString());
            //System.out.println(System.getProperty("java.util.logging.config.file").trim());
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
            String fileSeparator = System.getProperty("file.separator");
            String underscore = "_";
            GENESIS_CompareModelDemographic instance = new GENESIS_CompareModelDemographic();
//            instance._GENESIS_Environment = new _GENESIS_Environment();
//            File t_GENESIS_Environment_Directory_File = new File(args[0]);
//            if (!t_GENESIS_Environment_Directory_File.exists()) {
//                t_GENESIS_Environment_Directory_File.mkdirs();
//            }
//            File theCompare_DemographicModel_Aspatial_File = new File(
//                    t_GENESIS_Environment_Directory_File,
//                    "GENESIS_CompareModelDemographic");
//            theCompare_DemographicModel_Aspatial_File.mkdirs();
//            long range = 100;
//            instance._GENESIS_Environment.Directory = theCompare_DemographicModel_Aspatial_File;
//            if (instance._GENESIS_Environment.Directory.list().length == 0) {
//                instance._GENESIS_Environment.Directory = Generic_StaticIO.initialiseArchive(
//                        instance._GENESIS_Environment.Directory, 
//                        range);
//            } else {
//                instance._GENESIS_Environment.Directory = Generic_StaticIO.addToArchive(
//                        instance._GENESIS_Environment.Directory,
//                        range);
//            }
//            instance._GENESIS_Environment.Directory.mkdirs();
//            instance.executorService = instance.getExecutorService();
//            instance.futures = new HashSet<Future>();
            //            doGenesisSimulatorRun(args,fileSeparator,underscore);
            instance.doLocalPCRun(args, fileSeparator, underscore);
            GENESIS_Log.reset();

        } catch (Error e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This is for grid comparison runs. In this case, all the results to be
     * compared are in a directory containing other data so the. These results
     * all start with the name "output" and so this is used to filter for these
     * results which are then dug into to find a more specific file location
     * within. It is assumed that there is only one result output per result,
     * i.e. the output is not a multiple run.
     *
     * @param args args[0] is a directory location containing the outputs to be
     * compared. These should be the only files or directories with names
     * starting with the string "output" args[1] is the location that a results
     * file comparison.out will be written.
     * @param fileSeparator
     * @param underscore
     * @throws IOException
     */
    public void doGenesisSimulatorRun(
            String[] args,
            String fileSeparator,
            String underscore) throws IOException {
        int expectedArgsLength = 2;
        if (args.length != expectedArgsLength) {
            System.err.println(
                    "Expected " + expectedArgsLength + " arguments, each "
                    + "being a File location, but for some reason have "
                    + args.length);
            System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        }
        String message = args[0] + " being used as directory location for the "
                + "results to be compared.";
        log(Level.INFO, message);
        System.out.println(message);
        File a_File = new File(args[0]);
        if (a_File == null) {
            message = "args[0] " + args[0] + " is not an existing File";
            log(Level.WARNING, message);
            System.err.println(message);
            System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        }
        StartNameFileFilter filter = new StartNameFileFilter("output");
        File[] resultsToCompare = a_File.listFiles(filter);
        if (resultsToCompare == null) {
            message = "args[0] " + args[0] + " is empty 1";
            log(Level.WARNING, message);
            System.err.println(message);
            System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        } else {
            if (resultsToCompare.length == 0) {
                message = "args[0] " + args[0] + " is empty 2";
                log(Level.WARNING, message);
                System.err.println(message);
                System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
            } else {
                // Init resultsToCompare_Treemap
                resultsToCompare_TreeMap = new TreeMap<Long, File>();
                message = "<list of results to compare>";
                log(Level.INFO, message);
                System.out.println(message);
                message = "There are " + resultsToCompare.length + " results to compare";
                log(Level.INFO, message);
                System.out.println(message);
                for (int i = 0; i < resultsToCompare.length; i++) {
                    File resultToCompare = Generic_StaticIO.getArchiveHighestLeafFile(
                            new File(resultsToCompare[i].toString() + "/DemographicModel_Aspatial_1/"),
                            "_");

                    //<Debugging code>
                    System.out.println("resultToCompare File " + resultToCompare);
                    System.out.println("resultsToCompare[" + i + "] File " + resultsToCompare[i]);
                    System.out.println("No archive at " + new File(resultsToCompare[i].toString() + "/DemographicModel_Aspatial_1/"));
                    //</Debugging code>



                    addResultToCompare(resultToCompare, resultsToCompare[i], fileSeparator);
                }
                message = "</list of results to compare>";
                log(Level.INFO, message);
                System.out.println(message);
            }
        }
        message = "Initialising comparison test results file";
        log(Level.INFO, message);
        System.out.println(message);
        File comparisonResultsDirectory = new File(args[1]);
        comparisonResultsDirectory.mkdirs();
        File comparisonTest_File = new File(
                comparisonResultsDirectory,
                "comparison.out");
        comparisonTest_PrintWriter = new PrintWriter(comparisonTest_File);
        message = "Results of comparison will be written to file " + comparisonTest_File.toString();
        log(Level.INFO, message);
        System.out.println(message);
//        b_File = new File(args[1]);
//        if (!b_File.exists()) {
//            System.err.println(
//                    "args[1] " + args[1]
//                    + " is not an existing File");
//            System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
//        }

        long range = 100L;
        ge.Directory = comparisonResultsDirectory;
        ge.Directory = Generic_StaticIO.initialiseArchive(ge.Directory,
                range);
        executorService = getExecutorService();
        futures = new HashSet<Future>();
        ge.AbstractModel = this;

        _StartYear = getStartYearForResult(resultsToCompare[0], underscore);
        _EndYear = _StartYear + 1L;

        File bestResult = compare(fileSeparator, underscore);

        // Write comparison plots first as Grid Automated Framework checks for 
        // the bestResult to be written then exits.
        message = "<generateComparisonPlots>";
        log(Level.INFO, message);
        System.out.println(message);
        generateComparisonPlots();
        message = "</generateComparisonPlots>";
        log(Level.INFO, message);
        System.out.println(message);
        Generic_Execution.shutdownExecutorService(
                executorService, futures, this);


        message = "Best result is in File " + bestResult.toString();
        log(Level.INFO, message);
        System.out.println(message);
        comparisonTest_PrintWriter.println(bestResult.toString());
        comparisonTest_PrintWriter.close();
        message = "Written best result file";
        log(Level.INFO, message);
        System.out.println(message);
    }

    /**
     * Updates resultsToCompare_TreeMap to include resultToCompare mapping in
     * the pseudoRandomNumberSeed from the metadata directory as the key for the
     * map. Generic_StaticIO.recursiveFileList(resultToCompareBase, 2) is
     * reported if resultToCompareBase is not null.
     *
     * @param resultToCompare
     * @param resultToCompareBase
     * @param fileSeparator
     */
    protected void addResultToCompare(
            File resultToCompare,
            File resultToCompareBase,
            String fileSeparator) {
        String message;
        MetadataType metadataType = GENESIS_DataHandler.getMetadataType(
                resultToCompare);
        long pseudoRandomNumberSeed = metadataType.getPseudoRandomNumberSeed();
        resultsToCompare_TreeMap.put(pseudoRandomNumberSeed, resultToCompare);
        if (resultToCompareBase != null) {
            message = resultToCompareBase.toString();
            log(Level.INFO, message);
            System.out.println(message);
            TreeSet<String> recursiveFileList = Generic_StaticIO.recursiveFileList(resultToCompareBase, 2);
            Iterator<String> ite = recursiveFileList.iterator();
            while (ite.hasNext()) {
                message = ite.next();
                log(Level.INFO, message);
                System.out.println(message);
            }
        }
    }

    /**
     * @param args args[0] is the directory containing the results to compare
     * args[1] is the directory where the comparison.out file will be written
     * @param fileSeparator
     * @param underscore
     * @throws IOException
     */
    public void doLocalPCRun(
            String[] args,
            String fileSeparator,
            String underscore) throws IOException {
        log(Level.FINE, "<doLocalPCRun>");
        File t_GENESIS_Environment_Directory_File = new File(args[0]);
        if (!t_GENESIS_Environment_Directory_File.exists()) {
            t_GENESIS_Environment_Directory_File.mkdirs();
        }
        File theCompare_DemographicModel_Aspatial_File = new File(
                t_GENESIS_Environment_Directory_File,
                "Compare_GENESIS_DemographicModel");
        theCompare_DemographicModel_Aspatial_File.mkdirs();
        long range = 100;
        ge.Directory = theCompare_DemographicModel_Aspatial_File;
        if (ge.Directory.list().length == 0) {
            ge.Directory = Generic_StaticIO.initialiseArchive(ge.Directory,
                    range);
        } else {
            ge.Directory = Generic_StaticIO.addToArchive(ge.Directory,
                    range);
        }
        ge.Directory.mkdirs();
        executorService = getExecutorService();
        futures = new HashSet<Future>();

        _StartYear = getStartYearForResult(t_GENESIS_Environment_Directory_File, underscore);
        _EndYear = _StartYear + 1L;
//            int expectedArgsLength = 4;
//            if (args.length != expectedArgsLength) {
//                System.err.println(
//                        "Expected " + expectedArgsLength + " arguments, but for "
//                        + "some reason have "
//                        + args.length);
//                System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
//            }
        File a_File = new File(args[0],
                "GENESIS_DemographicModel/");
        if (!a_File.exists()) {
            System.err.println(
                    "args[0] " + args[0]
                    + " is not an existing File");
            System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        }

        // Initialise the TreeMap<Long, File> resultsToCompare
        TreeMap<Long, File> resultsToCompare;
        if (args.length == 2) {
            resultsToCompare = Generic_StaticIO.getArchiveLeafFiles_TreeMap(
                    a_File,
                    underscore);
        } else {
            resultsToCompare = Generic_StaticIO.getArchiveLeafFiles_TreeMap(
                    a_File,
                    underscore,
                    Long.valueOf(args[2]).longValue(),
                    Long.valueOf(args[3]).longValue());
        }
        resultsToCompare_TreeMap = new TreeMap<Long, File>();
        String message = "<list of results to compare>";
        log(Level.INFO, message);
        System.out.println(message);
        message = "There are " + resultsToCompare.size() + " results to compare";
        log(Level.INFO, message);
        System.out.println(message);
        Iterator<Entry<Long, File>> ite = resultsToCompare.entrySet().iterator();
        Entry<Long, File> entry;
        while (ite.hasNext()) {
            entry = ite.next();
            File resultToCompare = entry.getValue();
            addResultToCompare(resultToCompare, null, fileSeparator);
        }
        message = "</list of results to compare>";
        log(Level.INFO, message);
        System.out.println(message);
        ge.AbstractModel = this;
        //_HandleOutOfMemoryError = false;
        // Generate comparison plots
        generateComparisonPlots();
        File comparisonTest_File = new File(
                ge.Directory,
                "comparison.out");
        comparisonTest_PrintWriter = new PrintWriter(comparisonTest_File);
        File bestFile = compare(fileSeparator, underscore);
        comparisonTest_PrintWriter.println(bestFile.toString());
        comparisonTest_PrintWriter.close();
        Generic_Execution.shutdownExecutorService(
                executorService, futures, this);
        log(Level.FINE, "</doLocalPCRun>");
    }

    protected void generateComparisonPlots() {
        String title;
        String format = "PNG";
        int dataWidthOfAllAgesPopulationDisplays = 300;
        int dataHeightOfAllAgesPopulationDisplays = 400;
        int dataWidthOfFertilityAgesPopulationDisplays = 300;
        int dataHeightOfFertilityAgesPopulationDisplays = 100;
        String xAxisLabel;
        String yAxisLabel = "Age";
        boolean drawOriginLinesOnPlot = false;
        int ageIntervals = 1;
        int startAgeOfAllAgesEndYearInterval = 119;
        int startAgeOfFertilityAgesEndYearInterval = 43;
        Long minAgeYearsForAllAgesPopulationDisplays = 0L;
        Long maxAgeYearsForAllAgesPopulationDisplays = Long.valueOf(startAgeOfAllAgesEndYearInterval + 1);
        Long minAgeYearsForFertilityAgesPopulationDisplays = Long.valueOf(15);
        Long maxAgeYearsForFertilityAgesPopulationDisplays = Long.valueOf(startAgeOfFertilityAgesEndYearInterval + 1);

        int decimalPlacePrecisionForCalculations = 10;
        int decimalPlacePrecisionForDisplay = 1;
        //int significantDigits = 3;
        RoundingMode aRoundingMode = RoundingMode.HALF_UP;

        // Initialise highestLeaf and range:
        // highestLeaf is the number of different areaCodes for which there is 
        // output to compare.
        Entry<Long, File> entry = resultsToCompare_TreeMap.firstEntry();
        File resultDir = entry.getValue();
        File resultDataDir = new File(
                resultDir,
                "data");
        File demographicsArchiveTopLevelDirectory_File = new File(
                resultDataDir,
                "Demographics");
        //            String demographicsTopLevelDirectoryName = demographicsArchiveAboveTopLevelDirectory_File.list()[0];
        //            File demographicsArchiveTopLevelDirectory_File = new File(
        //                    demographicsArchiveAboveTopLevelDirectory_File,
        //                    demographicsTopLevelDirectoryName);
        long highestLeaf = Generic_StaticIO.getArchiveHighestLeaf(
                demographicsArchiveTopLevelDirectory_File,
                "_");
        long range = Generic_StaticIO.getArchiveRange(
                demographicsArchiveTopLevelDirectory_File,
                "_");
        // Initialise Archive for results and create numeric look up for resultsDirectories
        TreeMap<Long, File> areaCode_TreeMap = null;
        try {
            areaCode_TreeMap = Generic_StaticIO.initialiseArchiveReturnTreeMapLongFile(ge.Directory,
                    range,
                    highestLeaf);
        } catch (IOException ex) {
            System.err.println(ex.getMessage() + " in " + GENESIS_CompareModelDemographic.class.getName() + ".generateComparisonPlots()");
            Logger.getLogger(GENESIS_CompareModelDemographic.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(GENESIS_ErrorAndExceptionHandler.IOException);
        }

        String partFilename;
        for (long l = 0; l <= highestLeaf; l++) {
            System.out.println("Leaf " + l);
            File resultDirectory = areaCode_TreeMap.get(Long.valueOf(l));
            String areaCode = new File(
                    Generic_StaticIO.getObjectDirectory(
                    demographicsArchiveTopLevelDirectory_File,
                    l,
                    highestLeaf,
                    range),
                    "" + l).listFiles()[0].getName();
            //String areaCode = resultDirectory.getName();
            title = "Variation in Total Population Simulated Living Days " + _StartYear;
            partFilename = "_Population__Simulated_Living_Days__Year_" + _EndYear;
            xAxisLabel = "Population";
            popOutput(
                    areaCode,
                    resultDirectory,
                    title, format,
                    dataWidthOfAllAgesPopulationDisplays,
                    dataHeightOfAllAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, ageIntervals,
                    startAgeOfAllAgesEndYearInterval,
                    minAgeYearsForAllAgesPopulationDisplays,
                    maxAgeYearsForAllAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
            // Output not needed as Variation in Total Population Simulated Living Days says it all!
            //            title = "Variation in Total Population Simulated Living Years " + _Year;
            //            partFilename = "_Population__Simulated_Living__Year_";
            //            popOutput(resultDirectory, title, format, dataWidth, dataHeight, xAxisLabel,
            //                    yAxisLabel, drawOriginLinesOnPlot, ageIntervals,
            //                    startAgeOfEndYearInterval,
            //                    decimalPlacePrecisionForCalculations,
            //                    decimalPlacePrecisionForDisplay, aRoundingMode,
            //                    range, highestLeaf, l, partFilename);
            title = "Variation in Total Clinical Miscarriages " + _StartYear;
            partFilename = "_Clinical_Miscarriage_" + _EndYear;
            popOutput(
                    areaCode,
                    resultDirectory, title, format,
                    dataWidthOfFertilityAgesPopulationDisplays,
                    dataHeightOfFertilityAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, ageIntervals,
                    startAgeOfFertilityAgesEndYearInterval,
                    minAgeYearsForFertilityAgesPopulationDisplays,
                    maxAgeYearsForFertilityAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
            title = "Variation in Early Pregnancy Loss " + _StartYear;
            partFilename = "_Early_Pregnancy_Loss_" + _EndYear;
            popOutput(
                    areaCode,
                    resultDirectory, title, format,
                    dataWidthOfFertilityAgesPopulationDisplays,
                    dataHeightOfFertilityAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, ageIntervals,
                    startAgeOfFertilityAgesEndYearInterval,
                    minAgeYearsForFertilityAgesPopulationDisplays,
                    maxAgeYearsForFertilityAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
            title = "Variation in Early Pregnancy Days " + _StartYear;
            partFilename = "_Early_Pregnancy_Days_" + _EndYear;
            popOutput(
                    areaCode,
                    resultDirectory, title, format,
                    dataWidthOfFertilityAgesPopulationDisplays,
                    dataHeightOfFertilityAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, ageIntervals,
                    startAgeOfFertilityAgesEndYearInterval,
                    minAgeYearsForFertilityAgesPopulationDisplays,
                    maxAgeYearsForFertilityAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
            title = "Variation in Late Pregnancy Days " + _StartYear;
            partFilename = "_Late_Pregnancy_Days_" + _EndYear;
            popOutput(
                    areaCode,
                    resultDirectory, title, format,
                    dataWidthOfFertilityAgesPopulationDisplays,
                    dataHeightOfFertilityAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, ageIntervals,
                    startAgeOfFertilityAgesEndYearInterval,
                    minAgeYearsForFertilityAgesPopulationDisplays,
                    maxAgeYearsForFertilityAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
            // Problem loading populations!
            title = "Variation in Dead Population " + _StartYear;
            partFilename = "_Population__Simulated_Dead__Year_" + _EndYear;
            popOutput(
                    areaCode,
                    resultDirectory, title, format,
                    dataWidthOfAllAgesPopulationDisplays,
                    dataHeightOfAllAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, ageIntervals,
                    startAgeOfAllAgesEndYearInterval,
                    minAgeYearsForAllAgesPopulationDisplays,
                    maxAgeYearsForAllAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
            title = "Variation in Birth Parents " + _StartYear;
            partFilename = "_Population__Simulated_Birth_Parents__Year_" + _EndYear;
            popOutput(
                    areaCode,
                    resultDirectory, title, format,
                    dataWidthOfFertilityAgesPopulationDisplays,
                    dataHeightOfFertilityAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel,
                    drawOriginLinesOnPlot, ageIntervals,
                    startAgeOfFertilityAgesEndYearInterval,
                    minAgeYearsForFertilityAgesPopulationDisplays,
                    maxAgeYearsForFertilityAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
            title = "Variation in Twin Parents " + _StartYear;
            partFilename = "_Population__Simulated_Twin_Parents__Year_" + _EndYear;
            popOutput(
                    areaCode,
                    resultDirectory, title, format,
                    dataWidthOfFertilityAgesPopulationDisplays,
                    dataHeightOfFertilityAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel,
                    drawOriginLinesOnPlot, ageIntervals,
                    startAgeOfFertilityAgesEndYearInterval,
                    minAgeYearsForFertilityAgesPopulationDisplays,
                    maxAgeYearsForFertilityAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
            title = "Variation in Triplet Parents " + _StartYear;
            partFilename = "_Population__Simulated_Triplet_Parents__Year_" + _EndYear;
            popOutput(
                    areaCode,
                    resultDirectory, title, format,
                    dataWidthOfFertilityAgesPopulationDisplays,
                    dataHeightOfFertilityAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel,
                    drawOriginLinesOnPlot, ageIntervals,
                    startAgeOfFertilityAgesEndYearInterval,
                    minAgeYearsForFertilityAgesPopulationDisplays,
                    maxAgeYearsForFertilityAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
// Temproarily removed: first need to calculate Miscarriage Rates from counts and output!
//            title = "Variation in Miscarriage Rates " + _StartYear; // Set here for clarity of the code, but actually never used...
//            partFilename = "_Miscarriage__Simulated__Year_" + _EndYear;
//            miscarriageOutput(
//                    areaCode,
//                    resultDirectory, title, format,
//                    dataWidthOfFertilityAgesPopulationDisplays,
//                    dataHeightOfFertilityAgesPopulationDisplays,
//                    xAxisLabel,
//                    yAxisLabel,
//                    drawOriginLinesOnPlot, ageIntervals,
//                    startAgeOfFertilityAgesEndYearInterval,
//                    minAgeYearsForFertilityAgesPopulationDisplays,
//                    maxAgeYearsForFertilityAgesPopulationDisplays,
//                    decimalPlacePrecisionForCalculations,
//                    decimalPlacePrecisionForDisplay, aRoundingMode,
//                    range, highestLeaf, l, partFilename);
            xAxisLabel = "Rate";
            title = "Variation in Mortality Rates " + _StartYear;
            partFilename = "_Mortality_Rate_Simulated_Year_" + _EndYear;
            mortalityOutput(
                    areaCode,
                    resultDirectory, title, format,
                    dataWidthOfAllAgesPopulationDisplays,
                    dataHeightOfAllAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel,
                    drawOriginLinesOnPlot,
                    ageIntervals,
                    startAgeOfAllAgesEndYearInterval,
                    minAgeYearsForAllAgesPopulationDisplays,
                    maxAgeYearsForAllAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
            title = "Variation in Fertility Rates " + _StartYear;
            partFilename = "_Fertility__Simulated__Year_" + _EndYear;
            fertilityOutput(
                    areaCode,
                    resultDirectory, title, format,
                    dataWidthOfFertilityAgesPopulationDisplays,
                    dataHeightOfFertilityAgesPopulationDisplays,
                    xAxisLabel,
                    yAxisLabel,
                    drawOriginLinesOnPlot, ageIntervals,
                    startAgeOfFertilityAgesEndYearInterval,
                    minAgeYearsForFertilityAgesPopulationDisplays,
                    maxAgeYearsForFertilityAgesPopulationDisplays,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, aRoundingMode,
                    range, highestLeaf, l, partFilename);
        }

    }

    public void popOutput(
            String areaCode,
            File resultDirectory,
            String title,
            String format,
            int dataWidth,
            int dataHeight,
            String xAxisLabel,
            String yAxisLabel,
            boolean drawOriginLinesOnPlot,
            int ageIntervals,
            int startAgeOfEndYearInterval,
            Long minAgeYears,
            Long maxAgeYears,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode roundingMode,
            long range,
            long highestLeaf,
            long l,
            String partFilename) {
        File outputImageFile = new File(
                resultDirectory,
                "" + areaCode);
        outputImageFile.mkdirs();
        outputImageFile = new File(
                outputImageFile,
                areaCode + "_" + title.replace(" ", "_") + "." + format);
        GENESIS_AgeGenderLineChart livingPop_GENESIS_AgeGenderLineChart =
                new GENESIS_AgeGenderLineChart(
                executorService,
                outputImageFile,
                format,
                _ResultDataDirectory_File,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                ageIntervals,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                ge);
        HashSet<GENESIS_Population> pops = new HashSet<GENESIS_Population>();
        Iterator<Entry<Long, File>> ite = resultsToCompare_TreeMap.entrySet().iterator();
        Entry<Long, File> entry;
        while (ite.hasNext()) {
            entry = ite.next();
            File resultDir = entry.getValue();
            File resultDataDir = new File(
                    resultDir,
                    "data");
            File demographicsArchiveTopLevelDirectory_File = new File(
                    resultDataDir,
                    "Demographics");
//            String demographicsTopLevelDirectoryName = demographicsArchiveAboveTopLevelDirectory_File.list()[0];
//            File demographicsArchiveTopLevelDirectory_File = new File(
//                    demographicsArchiveAboveTopLevelDirectory_File,
//                    demographicsTopLevelDirectoryName);
            // Get directory ./0_99/0 or ./0_9999/0_99/0 for example
            File popDir = Generic_StaticIO.getObjectDirectory(
                    demographicsArchiveTopLevelDirectory_File,
                    l,
                    highestLeaf,
                    range);
            popDir = new File(
                    popDir,
                    "" + l);
            // Get directory popDir/areaCode/
            String filename = popDir.list()[0];
            areaCode = filename;
            popDir = new File(
                    popDir,
                    filename);
            // Get file popDir/areaCode/areaCode_Population__Simulated_Living_Days_Year_1992.xml
            //filename = popDir.list()[0];
            //filename = filename.substring(0, filename.length() - 4);
            //filename += ".xml";
            filename = areaCode + partFilename + ".xml";
            File popFile = new File(
                    popDir,
                    filename);
            GENESIS_Population pop = new GENESIS_Population(
                    ge,
                    popFile);
            pops.add(pop);
        }
        livingPop_GENESIS_AgeGenderLineChart.setTitle("" + areaCode + " " + title);
        livingPop_GENESIS_AgeGenderLineChart.setData(
                pops,
                ageIntervals,
                minAgeYears,
                maxAgeYears,
                decimalPlacePrecisionForCalculations,
                roundingMode);
        livingPop_GENESIS_AgeGenderLineChart.run();
        futures.add(livingPop_GENESIS_AgeGenderLineChart.future);
    }

    public void miscarriageOutput(
            String areaCode,
            File resultDirectory,
            String title,
            String format,
            int dataWidth,
            int dataHeight,
            String xAxisLabel,
            String yAxisLabel,
            boolean drawOriginLinesOnPlot,
            int ageIntervals,
            int startAgeOfEndYearInterval,
            Long minAgeYears,
            Long maxAgeYears,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode roundingMode,
            long range,
            long highestLeaf,
            long l,
            String partFilename) {

        File outputImageFile;
        HashSet<GENESIS_Population> pops;
        Iterator<Entry<Long, File>> ite;
        Entry<Long, File> entry;
        title = "Variation in Clinical Miscarriage Rates " + _StartYear;
        outputImageFile = new File(
                resultDirectory,
                "" + areaCode);
        outputImageFile.mkdirs();
        outputImageFile = new File(
                outputImageFile,
                areaCode + "_" + title.replace(" ", "_") + "." + format);
        GENESIS_AgeGenderLineChart livingPop_GENESIS_AgeGenderLineChart0 =
                new GENESIS_AgeGenderLineChart(
                executorService,
                outputImageFile,
                format,
                _ResultDataDirectory_File,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                ageIntervals,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                ge);
        pops = new HashSet<GENESIS_Population>();
        ite = resultsToCompare_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            File resultDir = entry.getValue();
            File resultDataDir = new File(
                    resultDir,
                    "data");
            File demographicsArchiveTopLevelDirectory_File = new File(
                    resultDataDir,
                    "Demographics");
//            String demographicsTopLevelDirectoryName = demographicsArchiveAboveTopLevelDirectory_File.list()[0];
//            File demographicsArchiveTopLevelDirectory_File = new File(
//                    demographicsArchiveAboveTopLevelDirectory_File,
//                    demographicsTopLevelDirectoryName);
            // Get directory ./0_99/0 or ./0_9999/0_99/0 for example
            File popDir = Generic_StaticIO.getObjectDirectory(
                    demographicsArchiveTopLevelDirectory_File,
                    l,
                    highestLeaf,
                    range);
            popDir = new File(
                    popDir,
                    "" + l);
            // Get directory popDir/areaCode/
            String filename = popDir.list()[0];
            areaCode = filename;
            popDir = new File(
                    popDir,
                    filename);
            // Get file popDir/areaCode/areaCode_Population__Simulated_Living_Days_Year_1992.xml
            //filename = popDir.list()[0];
            //filename = filename.substring(0, filename.length() - 4);
            //filename += ".xml";
            filename = areaCode + partFilename + ".xml";
            File popFile = new File(
                    popDir,
                    filename);
//            GENESIS_Population pop = new GENESIS_Population(
//                    _GENESIS_Environment,
//                    popFile);
//            pops.add(pop);
            GENESIS_Miscarriage miscarriage = new GENESIS_Miscarriage(ge, popFile);
            TreeMap<GENESIS_AgeBound, BigDecimal> female = miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap;
            GENESIS_Population pop = new GENESIS_Population(ge);
            pop._FemaleAgeBoundPopulationCount_TreeMap = GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(female);
            pop.updateGenderedAgePopulation();
            pops.add(pop);
        }
        livingPop_GENESIS_AgeGenderLineChart0.setTitle("" + areaCode + " " + title);
        livingPop_GENESIS_AgeGenderLineChart0.setData(
                pops,
                ageIntervals,
                minAgeYears,
                maxAgeYears,
                decimalPlacePrecisionForCalculations,
                roundingMode);
        livingPop_GENESIS_AgeGenderLineChart0.run();
        futures.add(livingPop_GENESIS_AgeGenderLineChart0.future);

        title = "Variation in Early Pregnancy Loss Rates " + _StartYear;
        outputImageFile = new File(
                resultDirectory,
                "" + areaCode);
        outputImageFile.mkdirs();
        outputImageFile = new File(
                outputImageFile,
                areaCode + "_" + title.replace(" ", "_") + "." + format);
        GENESIS_AgeGenderLineChart livingPop_GENESIS_AgeGenderLineChart1 =
                new GENESIS_AgeGenderLineChart(
                executorService,
                outputImageFile,
                format,
                _ResultDataDirectory_File,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                ageIntervals,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                ge);
        pops = new HashSet<GENESIS_Population>();
        ite = resultsToCompare_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            File resultDir = entry.getValue();
            File resultDataDir = new File(
                    resultDir,
                    "data");
            File demographicsArchiveTopLevelDirectory_File = new File(
                    resultDataDir,
                    "Demographics");
//            String demographicsTopLevelDirectoryName = demographicsArchiveAboveTopLevelDirectory_File.list()[0];
//            File demographicsArchiveTopLevelDirectory_File = new File(
//                    demographicsArchiveAboveTopLevelDirectory_File,
//                    demographicsTopLevelDirectoryName);
            // Get directory ./0_99/0 or ./0_9999/0_99/0 for example
            File popDir = Generic_StaticIO.getObjectDirectory(
                    demographicsArchiveTopLevelDirectory_File,
                    l,
                    highestLeaf,
                    range);
            popDir = new File(
                    popDir,
                    "" + l);
            // Get directory popDir/areaCode/
            String filename = popDir.list()[0];
            areaCode = filename;
            popDir = new File(
                    popDir,
                    filename);
            // Get file popDir/areaCode/areaCode_Population__Simulated_Living_Days_Year_1992.xml
            //filename = popDir.list()[0];
            //filename = filename.substring(0, filename.length() - 4);
            //filename += ".xml";
            filename = areaCode + partFilename + ".xml";
            File popFile = new File(
                    popDir,
                    filename);
//            GENESIS_Population pop = new GENESIS_Population(
//                    _GENESIS_Environment,
//                    popFile);
//            pops.add(pop);
            GENESIS_Miscarriage miscarriage = new GENESIS_Miscarriage(ge, popFile);
            TreeMap<GENESIS_AgeBound, BigDecimal> female = miscarriage._DailyEarlyPregnancyLossAgeBoundProbability_TreeMap;
            GENESIS_Population pop = new GENESIS_Population(ge);
            pop._FemaleAgeBoundPopulationCount_TreeMap = GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(female);
            pop.updateGenderedAgePopulation();
            pops.add(pop);
        }
        livingPop_GENESIS_AgeGenderLineChart1.setTitle("" + areaCode + " " + title);
        livingPop_GENESIS_AgeGenderLineChart1.setData(
                pops,
                ageIntervals,
                minAgeYears,
                maxAgeYears,
                decimalPlacePrecisionForCalculations,
                roundingMode);
        livingPop_GENESIS_AgeGenderLineChart1.run();
        futures.add(livingPop_GENESIS_AgeGenderLineChart1.future);
    }

    public void mortalityOutput(
            String areaCode,
            File resultDirectory,
            String title,
            String format,
            int dataWidth,
            int dataHeight,
            String xAxisLabel,
            String yAxisLabel,
            boolean drawOriginLinesOnPlot,
            int ageIntervals,
            int startAgeOfEndYearInterval,
            Long minAgeYears,
            Long maxAgeYears,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode roundingMode,
            long range,
            long highestLeaf,
            long l,
            String partFilename) {
        File outputImageFile;
        GENESIS_AgeGenderLineChart livingPop_GENESIS_AgeGenderLineChart;
        HashSet<GENESIS_Population> pops;
        Iterator<Entry<Long, File>> ite;
        Entry<Long, File> entry;
        title = "Variation in Mortality Rates";
        outputImageFile = new File(
                resultDirectory,
                "" + areaCode);
        outputImageFile.mkdirs();
        outputImageFile = new File(
                outputImageFile,
                areaCode + "_" + title.replace(" ", "_") + "." + format);
        livingPop_GENESIS_AgeGenderLineChart =
                new GENESIS_AgeGenderLineChart(
                executorService,
                outputImageFile,
                format,
                _ResultDataDirectory_File,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                ageIntervals,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                ge);
        pops = new HashSet<GENESIS_Population>();
        ite = resultsToCompare_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            File resultDir = entry.getValue();
            File resultDataDir = new File(
                    resultDir,
                    "data");
            File demographicsArchiveTopLevelDirectory_File = new File(
                    resultDataDir,
                    "Demographics");
//            String demographicsTopLevelDirectoryName = demographicsArchiveAboveTopLevelDirectory_File.list()[0];
//            File demographicsArchiveTopLevelDirectory_File = new File(
//                    demographicsArchiveAboveTopLevelDirectory_File,
//                    demographicsTopLevelDirectoryName);
            // Get directory ./0_99/0 or ./0_9999/0_99/0 for example
            File popDir = Generic_StaticIO.getObjectDirectory(
                    demographicsArchiveTopLevelDirectory_File,
                    l,
                    highestLeaf,
                    range);
            popDir = new File(
                    popDir,
                    "" + l);
            // Get directory popDir/areaCode/
            String filename = popDir.list()[0];
            areaCode = filename;
            popDir = new File(
                    popDir,
                    filename);
            // Get file popDir/areaCode/areaCode_Population__Simulated_Living_Days_Year_1992.xml
            //filename = popDir.list()[0];
            //filename = filename.substring(0, filename.length() - 4);
            //filename += ".xml";
            filename = areaCode + partFilename + ".xml";
            File popFile = new File(
                    popDir,
                    filename);
//            GENESIS_Population pop = new GENESIS_Population(
//                    _GENESIS_Environment,
//                    popFile);
//            pops.add(pop);
            GENESIS_Mortality mortality = new GENESIS_Mortality(ge, popFile);
            TreeMap<GENESIS_AgeBound, BigDecimal> female = mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap;
            TreeMap<GENESIS_AgeBound, BigDecimal> male = mortality._MaleAnnualMortalityAgeBoundRate_TreeMap;
            GENESIS_Population pop = new GENESIS_Population(ge);
            pop._FemaleAgeBoundPopulationCount_TreeMap = GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(female);
            pop._MaleAgeBoundPopulationCount_TreeMap = GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(male);
            pop.updateGenderedAgePopulation();
            pops.add(pop);
        }
        livingPop_GENESIS_AgeGenderLineChart.setTitle("" + areaCode + " " + title);
        livingPop_GENESIS_AgeGenderLineChart.setData(
                pops,
                ageIntervals,
                minAgeYears,
                maxAgeYears,
                decimalPlacePrecisionForCalculations,
                roundingMode);
        livingPop_GENESIS_AgeGenderLineChart.run();
        futures.add(livingPop_GENESIS_AgeGenderLineChart.future);
    }

    public void fertilityOutput(
            String areaCode,
            File resultDirectory,
            String title,
            String format,
            int dataWidth,
            int dataHeight,
            String xAxisLabel,
            String yAxisLabel,
            boolean drawOriginLinesOnPlot,
            int ageIntervals,
            int startAgeOfEndYearInterval,
            Long minAgeYears,
            Long maxAgeYears,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode roundingMode,
            long range,
            long highestLeaf,
            long l,
            String partFilename) {
        File outputImageFile;
        GENESIS_AgeGenderLineChart livingPop_GENESIS_AgeGenderLineChart;
        HashSet<GENESIS_Population> pops;
        Iterator<Entry<Long, File>> ite;
        Entry<Long, File> entry;
        outputImageFile = new File(
                resultDirectory,
                "" + areaCode);
        outputImageFile.mkdirs();
        outputImageFile = new File(
                outputImageFile,
                areaCode + "_" + title.replace(" ", "_") + "." + format);
        livingPop_GENESIS_AgeGenderLineChart =
                new GENESIS_AgeGenderLineChart(
                executorService,
                outputImageFile,
                format,
                _ResultDataDirectory_File,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                ageIntervals,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                ge);
        pops = new HashSet<GENESIS_Population>();
        ite = resultsToCompare_TreeMap.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            File resultDir = entry.getValue();
            File resultDataDir = new File(
                    resultDir,
                    "data");
            File demographicsArchiveTopLevelDirectory_File = new File(
                    resultDataDir,
                    "Demographics");
//            String demographicsTopLevelDirectoryName = demographicsArchiveAboveTopLevelDirectory_File.list()[0];
//            File demographicsArchiveTopLevelDirectory_File = new File(
//                    demographicsArchiveAboveTopLevelDirectory_File,
//                    demographicsTopLevelDirectoryName);
            // Get directory ./0_99/0 or ./0_9999/0_99/0 for example
            File popDir = Generic_StaticIO.getObjectDirectory(
                    demographicsArchiveTopLevelDirectory_File,
                    l,
                    highestLeaf,
                    range);
            popDir = new File(
                    popDir,
                    "" + l);
            // Get directory popDir/areaCode/
            String filename = popDir.list()[0];
            areaCode = filename;
            popDir = new File(
                    popDir,
                    filename);
            // Get file popDir/areaCode/areaCode_Population__Simulated_Living_Days_Year_1992.xml
            //filename = popDir.list()[0];
            //filename = filename.substring(0, filename.length() - 4);
            //filename += ".xml";
            filename = areaCode + partFilename + ".xml";
            File popFile = new File(
                    popDir,
                    filename);
//            GENESIS_Population pop = new GENESIS_Population(
//                    _GENESIS_Environment,
//                    popFile);
//            pops.add(pop);
            FertilityType fertilityType = XMLConverter.loadFertilityFromXMLFile(popFile);
            GENESIS_Fertility fertility = new GENESIS_Fertility(ge, fertilityType);
            TreeMap<GENESIS_AgeBound, BigDecimal> female = fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap;
            GENESIS_Population pop = new GENESIS_Population(ge);
            pop._FemaleAgeBoundPopulationCount_TreeMap = GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_BigDecimal(female);
            pop.updateGenderedAgePopulation();
            pops.add(pop);
        }
        livingPop_GENESIS_AgeGenderLineChart.setTitle("" + areaCode + " " + title);
        livingPop_GENESIS_AgeGenderLineChart.setData(
                pops,
                ageIntervals,
                minAgeYears,
                maxAgeYears,
                decimalPlacePrecisionForCalculations,
                roundingMode);
        livingPop_GENESIS_AgeGenderLineChart.run();
        futures.add(livingPop_GENESIS_AgeGenderLineChart.future);
    }

    /**
     * Compare all results in resultsToCompare_TreeMap using
     * getSimilarity(String,File,File,String) The File location of the result
     * with the closest similarity to another estimate (theoretically expected
     * estimate based on estimated mid-year populations) is returned.
     * @param fileSeparator
     * @param underscore
     * @return 
     */
    public File compare(
            String fileSeparator,
            String underscore) {
        File a_File;
        BigDecimal closestSimilarity = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal similarity;
        String message;
        File bestResult = null;
        File parameters_File;
        Iterator<Entry<Long, File>> ite = resultsToCompare_TreeMap.entrySet().iterator();
        Entry<Long, File> entry;
        long pseudoRandomSeed;
        while (ite.hasNext()) {
            entry = ite.next();
            pseudoRandomSeed = entry.getKey();
            a_File = entry.getValue();
            message = "Comparing result in file " + a_File.toString();
            log(Level.INFO, message);
            System.out.println(message);
            parameters_File = new File(
                    a_File.getAbsolutePath() + fileSeparator
                    + "metadata" + fileSeparator + "parameters.xml");
            message = "Checking parameters file " + parameters_File.toString()
                    + " exists";
            log(Level.INFO, message);
            System.out.println(message);
            // Check a_parameters_File exists and can be read
            if (!Generic_StaticIO.fileExistsAndCanBeRead(parameters_File)) {
                message = parameters_File.toString() + " cannot be read";
                log(Level.INFO, message);
                System.err.println(message);
                System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
            }
            message = "Parameters file " + parameters_File.toString()
                    + " exists.";
            log(Level.INFO, message);
            System.out.println(message);
            similarity = getSimilarity(
                    fileSeparator,
                    parameters_File,
                    a_File,
                    underscore);
            message = a_File.toString()
                    + " PseudoRandomSeed " + pseudoRandomSeed
                    + " Result similarity " + similarity.toPlainString();
            log(Level.INFO, message);
            System.out.println(message);
            //getLogger().log(Level.FINE, "{0} result similarity {1}", new Object[]{a_File.toString(), similarity.toPlainString()});
            if (similarity.compareTo(closestSimilarity) == -1) {
                closestSimilarity = similarity;
                bestResult = new File(a_File.toString());
            }
        }
        message = "Closest similarity " + closestSimilarity + " is in file " + bestResult.toString();
        log(Level.INFO, message);
        System.out.println(message);
        return bestResult;
    }

//    /**
//     * This run is to compare the mortality and fertility rates generated in the
//     * simulation with those input and to inform which result is closer to the
//     * input probabilities.
//     */
//    public void runProbabilities1Input(boolean multipleRun) {
//        String fileSeparator = System.getProperty("file.separator");
//        String underscore = "_";
//        // The following will return errors if these are not archives (from multiple runs)
//        long numberOfAResults = Generic_StaticIO.getArchiveHighestLeaf(
//                a_File,
//                underscore);
//        a_File = Generic_StaticIO.getArchiveHighestLeafFile(a_File, underscore).getParentFile();
//        File a_File0 = new File(a_File.toString());
//        BigDecimal closestSimilarity = BigDecimal.valueOf(Double.MAX_VALUE);
//        BigDecimal similarity;
//        long rangeA = Generic_StaticIO.getArchiveRange(
//                a_File,
//                underscore);
//        File bestResult = null;
//        File parameters_File;
//        for (long a = 0; a < numberOfAResults; a++) {
//            if (multipleRun) {
//                a_File = Generic_StaticIO.getObjectDirectory(
//                        a_File0,
//                        a,
//                        numberOfAResults,
//                        rangeA);
//            }
//            a_File = new File(a_File, Long.toString(a));
//            parameters_File = new File(
//                    a_File.getAbsolutePath() + fileSeparator
//                    + "metadata" + fileSeparator + "parameters.xml");
//            // Check a_parameters_File exists and can be read
//            if (parameters_File.exists()) {
//                if (!parameters_File.canRead()) {
//                    System.err.println(
//                            parameters_File.toString()
//                            + " exists, but cannot be read");
//                    System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
//                }
//            } else {
//                System.err.println(
//                        parameters_File.toString()
//                        + " is not an existing File");
//                System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
//            }
//            similarity = getSimilarity(
//                    fileSeparator,
//                    parameters_File,
//                    a_File,
//                    underscore);
//            getLogger().log(Level.FINE, "{0} result similarity {1}", new Object[]{a_File.toString(), similarity.toPlainString()});
//            if (similarity.compareTo(closestSimilarity) == -1) {
//                closestSimilarity = similarity;
//                bestResult = new File(a_File.toString());
//            }
//            a_File = a_File0;
//        }
//        System.out.println("Best result is in File " + bestResult.toString());
//
//        comparisonTest_PrintWriter.println(bestResult.toString());
//        comparisonTest_PrintWriter.close();
//    }
    private GENESIS_Demographics getEndDemographics(
            GENESIS_Environment a_GENESIS_Environment,
            File a_DataDirectory_File,
            Set<String> regionIDs,
            String underscore,
            int lastYear) {

        /*
         * Set up logging and log entry to method
         */
        String sourceMethod = "getEndDemographics(GENESIS_Environment,File,String,String,int)";
        Logger logger = getLogger();
        logger.entering(sourceClass, sourceMethod);
        System.out.println("<" + sourceMethod + ">");
        
        /*
         * Initialise result
         */
        GENESIS_Demographics result = new GENESIS_Demographics(a_GENESIS_Environment);
        File demographicsDirectory = new File(
                a_DataDirectory_File,
                "Demographics");
        result._Population = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        result._Mortality = new TreeMap<String, TreeMap<String, GENESIS_Mortality>>();
//        result._Miscarriage = new TreeMap<String, TreeMap<String, GENESIS_Miscarriage>>();
//        result._Fertility = new TreeMap<String, TreeMap<String, GENESIS_Fertility>>();
        long range = Generic_StaticIO.getArchiveRange(
                    demographicsDirectory,
                    underscore);
        long highestLeaf = Generic_StaticIO.getArchiveHighestLeaf(
                demographicsDirectory,
                underscore);
        for (long l = 0; l <= highestLeaf; l ++) {
            File dir = Generic_StaticIO.getObjectDirectory(
                    demographicsDirectory,
                    l,
                    highestLeaf,
                    range);
            dir = new File(
                    dir,
                    "" + l);
            dir = dir.listFiles()[0];
            String dirName = dir.getName();
            String regionID = dirName.split(underscore)[0];
            // Only get region level data
            if (regionIDs.contains(regionID)) {
                TreeMap<String, GENESIS_Population> regionPopulation;
                regionPopulation = new TreeMap<String, GENESIS_Population>();
                result._Population.put(regionID, regionPopulation);
                TreeMap<String, GENESIS_Mortality> regionMortality;
                regionMortality = new TreeMap<String, GENESIS_Mortality>();
                result._Mortality.put(regionID, regionMortality);
//                TreeMap<String, GENESIS_Miscarriage> regionMiscarriage;
//                regionMiscarriage = new TreeMap<String, GENESIS_Miscarriage>();
//                result._Miscarriage.put(regionID, regionMiscarriage);
//                TreeMap<String, GENESIS_Fertility> regionFertility;
//                regionFertility = new TreeMap<String, GENESIS_Fertility>();
//                result._Fertility.put(regionID, regionFertility);
                // Population
                File a_Population_File = new File(
                    dir,
                    regionID + "_Population__Simulated_Living__Year_" + lastYear + ".xml");
                GENESIS_Population pop = new GENESIS_Population(
                a_GENESIS_Environment,
                a_Population_File);
                regionPopulation.put(
                        regionID,
                        pop);
                // Mortality
                // @TODO Change to load in death counts to compare
                File a_MortalityRate_File = new File(
                    dir,
                    regionID + "_Mortality_Rate_Simulated_Year_" + lastYear + ".xml");
                GENESIS_Mortality mort = new GENESIS_Mortality(
                a_GENESIS_Environment,
                a_MortalityRate_File);
                regionMortality.put(
                        regionID,
                        mort);
                // @TODO Miscarriage/fertility. For fertility compare counts for 
                // birth with those used to estimate fertility rates.
                // Without count data for miscarriage, comparison can only be 
                // done with rates...
//                File a_MiscarriageRate_File = new File(
//                dir,
//                regionID + "_Clinical_Miscarriage_" + lastYear + ".xml");
//                result._Miscarriage = new GENESIS_Miscarriage(
//                a_GENESIS_Environment,
//                a_MiscarriageRate_File);
            }
        }
        return result;
    }

    /**
     * Reads input GENESIS_Mortality and GENESIS_Fertility from files stored in
     * the metadataDirectory_File
     *
     * @param metadataDirectory_File
     * @return Object[] result where: result[0] is the input GENESIS_Mortality;
     * result[1] is the input GENESIS_Fertility;
     */
    private GENESIS_Demographics getStartDemographics(
            GENESIS_Environment a_GENESIS_Environment,
            File metadataDirectory) {

        /*
         * Set up logging and log entry to method
         */
        String sourceMethod = "getStartDemographics(GENESIS_Environment,File)";
        Logger logger = getLogger();
        logger.entering(sourceClass, sourceMethod);
        System.out.println("<" + sourceMethod + ">");

        /*
         * Initialise result
         */
        GENESIS_Demographics result = new GENESIS_Demographics(a_GENESIS_Environment);

        String underscore = "_";

        // Get start Total Population File 
        result._Population = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        File populationDirectory_File = new File(
                metadataDirectory,
                "population");
        File basePopultionDir = new File(
                populationDirectory_File,
                "start");
        String[] regionFiles = basePopultionDir.list();
        for (int i = 0; i < regionFiles.length; i++) {
            String regionID = regionFiles[i];
            TreeMap<String, GENESIS_Population> regionPop = new TreeMap<String, GENESIS_Population>();
            result._Population.put(regionID, regionPop);
            File dir = new File(
                    basePopultionDir,
                    regionID);
            Long highestLeaf = Generic_StaticIO.getArchiveHighestLeaf(
                    dir,
                    underscore);
            long range = Generic_StaticIO.getArchiveRange(
                    dir,
                    underscore);
            for (long l = 0; l <= highestLeaf; l++) {
                File dir2 = Generic_StaticIO.getObjectDirectory(
                        dir,
                        l,
                        highestLeaf,
                        range);
                File file = new File(
                        dir2,
                        "" + l);
                String filename = file.list()[0];
                filename = filename.substring(0, filename.length() - 4);
                filename += ".xml";
                String[] split = filename.split("_");
                String subregionID = split[0];
                File inputPopulation_File = new File(file,
                        filename);
                regionPop.put(
                        subregionID,
                        new GENESIS_Population(
                        a_GENESIS_Environment,
                        inputPopulation_File));
            }
        }

        /*
         * Initialise GENESIS_Miscarriage
         */
        File inputMiscarriageDir = new File(
                metadataDirectory,
                "inputMiscarriageRate");
        File inputMiscarriage_File = new File(
                inputMiscarriageDir,
                "MiscarriageRate.xml");
        GENESIS_Miscarriage input_Miscarriage = new GENESIS_Miscarriage(
                a_GENESIS_Environment,
                inputMiscarriage_File);

        // Get start Mortality
        result._Mortality = new TreeMap<String, TreeMap<String, GENESIS_Mortality>>();
        result._Fertility = new TreeMap<String, TreeMap<String, GENESIS_Fertility>>();
        underscore = "_";
        File baseMortalityDir = new File(
                metadataDirectory,
                "mortality");
        File baseFertilityDir = new File(
                metadataDirectory,
                "fertility");
        regionFiles = baseFertilityDir.list();
        for (int i = 0; i < regionFiles.length; i++) {
            String regionID = regionFiles[i];
            TreeMap<String, GENESIS_Mortality> regionMortality = new TreeMap<String, GENESIS_Mortality>();
            result._Mortality.put(regionID, regionMortality);
            TreeMap<String, GENESIS_Fertility> regionFertility = new TreeMap<String, GENESIS_Fertility>();
            result._Fertility.put(regionID, regionFertility);
            File mortalityDir = new File(
                    baseMortalityDir,
                    regionID);
            File fertilityDir = new File(
                    baseFertilityDir,
                    regionID);
            Long highestLeaf = Generic_StaticIO.getArchiveHighestLeaf(
                    fertilityDir,
                    underscore);
            long range = Generic_StaticIO.getArchiveRange(
                    fertilityDir,
                    underscore);
            for (long l = 0; l <= highestLeaf; l++) {
                File dirMortality2 = Generic_StaticIO.getObjectDirectory(
                        mortalityDir,
                        l,
                        highestLeaf,
                        range);
                File dirFertility2 = Generic_StaticIO.getObjectDirectory(
                        fertilityDir,
                        l,
                        highestLeaf,
                        range);
                File fileMortality = new File(
                        dirMortality2,
                        "" + l);
                File fileFertility = new File(
                        dirFertility2,
                        "" + l);
                String filenameMortality = fileMortality.list()[0];
                filenameMortality = filenameMortality.substring(0, filenameMortality.length() - 4);
                filenameMortality += ".xml";
                String filenameFertility = fileFertility.list()[0];
                filenameFertility = filenameFertility.substring(0, filenameFertility.length() - 4);
                filenameFertility += ".xml";
                String[] splitFertility = filenameFertility.split("_");
                String subregionID = splitFertility[0];
                File inputMortality_File = new File(
                        fileMortality,
                        filenameMortality);
                
                // Debug
                System.out.println("inputMortality_File " + inputMortality_File);
                
                GENESIS_Mortality mort = new GENESIS_Mortality(
                        a_GENESIS_Environment,
                        inputMortality_File);
                regionMortality.put(
                        subregionID,
                        mort);
                File inputFertility_File = new File(
                        fileFertility,
                        filenameFertility);
                
                // Debug
                System.out.println("inputFertility_File " + inputFertility_File);
                
                GENESIS_Fertility fert = new GENESIS_Fertility(
                        a_GENESIS_Environment,
                        mort,
                        input_Miscarriage,
                        inputFertility_File);
                regionFertility.put(
                        subregionID,
                        fert);
            }
        }

        /*
         * Log exit from method and return result
         */
        logger.exiting(sourceClass, sourceMethod);
        System.out.println("</" + sourceMethod + ">");

        return result;
    }

    /**
     * @param inputMortality_Mortality
     * @param outputMortality_Mortality
     * @return SSE
     */
    public BigDecimal compareMortality(
            GENESIS_Mortality inputMortality_Mortality,
            GENESIS_Mortality outputMortality_Mortality) {
        BigDecimal result;
        Object[] firstOrderStatistics = GENESIS_Statistics.getFirstOrderStatistics2(
                inputMortality_Mortality._FemaleDailyMortalityAgeBoundProbability_TreeMap,
                outputMortality_Mortality._FemaleDailyMortalityAgeBoundProbability_TreeMap,
                "inputMortality_Mortality._DailyAgeMortalityFemale_TreeMap",
                "outputMortality_Mortality._DailyAgeMortalityFemale_TreeMap",
                "age");
        result = (BigDecimal) firstOrderStatistics[2];
        return result;
    }

    /**
     * @param inputFertility_Fertility
     * @param outputFertility_Fertility
     * @return SSE
     */
    public BigDecimal compareFertility(
            GENESIS_Fertility inputFertility_Fertility,
            GENESIS_Fertility outputFertility_Fertility) {
        BigDecimal result;
        Object[] firstOrderStatistics = GENESIS_Statistics.getFirstOrderStatistics2(
                inputFertility_Fertility._DailyPregnancyAgeBoundProbabilities_TreeMap,
                outputFertility_Fertility._DailyPregnancyAgeBoundProbabilities_TreeMap,
                "inputFertility_Fertility._DailyPregnancyRateProbabilities_TreeMap",
                "outputFertility_Fertility._DailyPregnancyRateProbabilities_TreeMap",
                "age");
        result = (BigDecimal) firstOrderStatistics[2];
        return result;
    }

    public BigDecimal getSimilarity(
            String fileSeparator,
            File parameters_File,
            File base_File,
            String underscore) {
        String message = "Calculating similarity";
        log(Level.INFO, message);
        //log(Level.FINE, message);
        System.out.println(message);
        File dataDirectory_File = new File(
                base_File,
                "data");
        File metadataDirectory_File = new File(
                base_File,
                "metadata");
        File metadata_File = new File(
                metadataDirectory_File,
                "metadata.xml");
        message = "Checking metadata file " + metadata_File + " exists";
        log(Level.INFO, message);
        //log(Level.FINE, message);
        System.out.println(message);
        if (!metadata_File.exists()) {
            message = metadata_File.toString() + " is not an existing File";
            log(Level.WARNING, message);
            System.err.println(message);
            System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        }
        if (!metadata_File.canRead()) {
            message = metadata_File.toString() + " exists, but cannot be read";
            log(Level.WARNING, message);
            System.err.println(message);
            System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        }
        message = "Loading metadata";
        log(Level.INFO, message);
        System.out.println(message);
        MetadataType metadata_Metadata =
                XMLConverter.loadMetadataFromXMLFile(metadata_File);
        message = "Loaded metadata";
        log(Level.INFO, message);
        System.out.println(message);
        message = "Loading parameters";
        log(Level.INFO, message);
        System.out.println(message);
        ParametersType parameters = XMLConverter.loadParametersFromXMLFile(
                parameters_File);
        message = "Loaded parameters";
        log(Level.INFO, message);
        System.out.println(message);

        // Set _GENESIS_Environment.Time
        GENESIS_Time a_Time = new GENESIS_Time(
                new Integer(parameters.getStartYear()),
                new Integer(parameters.getStartDay()));
        a_Time.addYears(parameters.getYears());
        ge.Time = a_Time;

        // Initialise _GENESIS_Environment
        //String fileSeparator = System.getProperty("file.separator");
        int lastYear = metadata_Metadata.getEndYear();
        ge.Time = new GENESIS_Time(lastYear, 0);
        ge.AbstractModel.ge = ge;

        // Initialise start GENESIS_Demographics
        message = "Initialise start Demographics";
        log(Level.INFO, message);
        System.out.println(message);
        GENESIS_Demographics startDemographics = getStartDemographics(ge,
                metadataDirectory_File);

        // Initialise end GENESIS_Demographics
        message = "Initialise end Demographics";
        log(Level.INFO, message);
        System.out.println(message);
        GENESIS_Demographics endDemographics = getEndDemographics(ge,
                dataDirectory_File,
                startDemographics._Population.keySet(),
                underscore,
                lastYear);

        // Compare and return the ID or something of the better fitting result...
        // Sum of Squared Error (SSE) a run GENESIS_Mortality Female
        // This will currently fail because there is no population in the demographics...
        message = "Compare Probabilities";
        log(Level.INFO, message);
        System.out.println(message);
        BigDecimal sse = BigDecimal.ZERO;
        Iterator<String> ite = startDemographics._Population.keySet().iterator();
        String areaCode;
        while (ite.hasNext()) {
            areaCode = ite.next();
            sse = sse.add(CompareProbabilities.compare(areaCode,
                    ge,
                    startDemographics,
                    endDemographics,
                    dataDirectory_File,
                    ge.DecimalPlacePrecisionForCalculations));
            message = "Similarity (sum of squared differences between simulated "
                    + "and theoretical results) for area " + areaCode + " is " + sse;
        }
        log(Level.INFO, message);
        System.out.println(message);
        log(Level.FINE, "</getSimilarity>");
        return sse;
    }

    /**
     * Compares simulation results in file a_File with result in File b_File and
     * reports differences via the report(String) method.
     * @param a_File
     * @param b_File
     */
    public void compare(
            File a_File,
            File b_File) {
        File comparisonTest_File = new File(
                a_File.getParentFile(),
                "comparison.out");
        try {
            comparisonTest_PrintWriter = new PrintWriter(comparisonTest_File);
        } catch (IOException e) {
            log(Level.SEVERE, e.getMessage());
        }
        boolean metadataTheSame = true;
        boolean resultsTheSame = true;
        String message;
        String fileSeparator = System.getProperty("file.separator");
        File a_parameters_File = new File(
                a_File.getAbsolutePath() + fileSeparator + "metadata"
                + fileSeparator + "parameters.xml");
        // Check a_parameters_File exists and can be read
        if (a_parameters_File.exists()) {
            if (!a_parameters_File.canRead()) {
                System.err.println(
                        a_parameters_File.toString()
                        + " exists, but cannot be read");
                System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
            }
        } else {
            System.err.println(
                    a_parameters_File.toString()
                    + " is not an existing File");
            System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        }
        File a_DataDirectory_File = new File(
                a_File,
                "data");
        File a_MetadataDirectory_File = new File(
                a_File,
                "metadata");

        File a_Metadata_File = new File(
                a_MetadataDirectory_File,
                "metadata.xml");
        MetadataType a_Metadata =
                XMLConverter.loadMetadataFromXMLFile(a_Metadata_File);

        File a_Parameters_File = new File(
                a_MetadataDirectory_File,
                "inputParameters.xml");
        ParametersType a_parameters = XMLConverter.loadParametersFromXMLFile(
                a_Parameters_File);

        File b_DataDirectory_File = new File(
                b_File,
                "data");
        File b_MetadataDirectory_File = new File(
                b_File,
                "metadata");

        File b_Metadata_File = new File(
                b_MetadataDirectory_File,
                "metadata.xml");
        MetadataType b_Metadata =
                XMLConverter.loadMetadataFromXMLFile(b_Metadata_File);

        File b_Parameters_File = new File(
                b_MetadataDirectory_File,
                "inputParameters.xml");
        ParametersType b_parameters = XMLConverter.loadParametersFromXMLFile(
                b_Parameters_File);

        /**
         * Compare Metadata
         */
        int a_startYear = new Integer(a_Metadata.getEndYear());
        int b_startYear = new Integer(b_Metadata.getEndYear());
        if (a_startYear == b_startYear) {
            message = "a_startYear == b_startYear " + a_startYear;
            report(message);
        } else {
            message = "a_startYear != b_startYear";
            report(message);
            metadataTheSame = false;
        }

        int a_startDay = new Integer(a_Metadata.getEndDay());
        int b_startDay = new Integer(b_Metadata.getEndDay());
        if (a_startDay == b_startDay) {
            message = "a_startDay == b_startDay " + a_startDay;
            report(message);
        } else {
            message = "a_startDay != b_startDay";
            report(message);
            metadataTheSame = false;
        }
        long a_RandomNumberSeed = a_Metadata.getPseudoRandomNumberSeed();
        long b_RandomNumberSeed = b_Metadata.getPseudoRandomNumberSeed();
        if (a_RandomNumberSeed == b_RandomNumberSeed) {
            message = "a_RandomNumberSeed == b_RandomNumberSeed "
                    + a_RandomNumberSeed;
            report(message);
        } else {
            message = "a_RandomNumberSeed != b_RandomNumberSeed";
            report(message);
            metadataTheSame = false;
        }

        /**
         * Compare parameters
         */
        int a_MaximumNumberOfAgentsPerAgentCollection =
                a_parameters.getMaximumNumberOfAgentsPerAgentCollection();
        int b_MaximumNumberOfAgentsPerAgentCollection =
                b_parameters.getMaximumNumberOfAgentsPerAgentCollection();
        if (a_MaximumNumberOfAgentsPerAgentCollection == b_MaximumNumberOfAgentsPerAgentCollection) {
            message = "a_MaximumNumberOfAgentsPerAgentCollection "
                    + "== b_MaximumNumberOfAgentsPerAgentCollection "
                    + a_MaximumNumberOfAgentsPerAgentCollection;
            report(message);
        } else {
            message = "a_MaximumNumberOfAgentsPerAgentCollection "
                    + "!= b_MaximumNumberOfAgentsPerAgentCollection)";
            report(message);
            metadataTheSame = false;
        }

        int a_MaximumNumberOfObjectsPerDirectory =
                a_parameters.getMaximumNumberOfObjectsPerDirectory();
        int b_MaximumNumberOfObjectsPerDirectory =
                b_parameters.getMaximumNumberOfObjectsPerDirectory();
        if (a_MaximumNumberOfObjectsPerDirectory == b_MaximumNumberOfObjectsPerDirectory) {
            message = "a_MaximumNumberOfObjectsPerDirectory "
                    + "== b_MaximumNumberOfObjectsPerDirectory "
                    + a_MaximumNumberOfObjectsPerDirectory;
            report(message);
        } else {
            message = "a_MaximumNumberOfObjectsPerDirectory "
                    + "!= b_MaximumNumberOfObjectsPerDirectory)";
            report(message);
            metadataTheSame = false;
        }

        /**
         * Compare results
         */
        /**
         * Set a_GENESIS_Environment.GENESIS_Time and
         * b_GENESIS_Environment.GENESIS_Time
         */
        ge.Time = new GENESIS_Time(
                new Integer(a_Metadata.getEndYear()),
                new Integer(a_Metadata.getEndDay()));
        ge.Time = new GENESIS_Time(
                new Integer(b_Metadata.getEndYear()),
                new Integer(b_Metadata.getEndDay()));

        GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                new GENESIS_AgentCollectionManager(ge);
        a_GENESIS_AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection =
                new Integer(a_MaximumNumberOfAgentsPerAgentCollection);
        a_GENESIS_AgentCollectionManager.MaximumNumberOfObjectsPerDirectory =
                new Integer(a_MaximumNumberOfObjectsPerDirectory);

        // LivingFemales
        File a_FemaleDirectory_File = new File(
                a_DataDirectory_File,
                "LivingFemales");
        a_GENESIS_AgentCollectionManager.setLivingFemaleDirectory(
                a_FemaleDirectory_File);
        long aIndexOfLastLivingFemaleCollection =
                Generic_StaticIO.getArchiveHighestLeaf(
                a_FemaleDirectory_File,
                "_");
        GENESIS_FemaleCollection a_LastLivingFemaleCollection =
                a_GENESIS_AgentCollectionManager.getFemaleCollection(aIndexOfLastLivingFemaleCollection,
                GENESIS_Person.getTypeLivingFemale_String(),
                HandleOutOfMemoryError);
        a_GENESIS_AgentCollectionManager._IndexOfLastBornFemale =
                a_LastLivingFemaleCollection.getMaxAgentID();

        // LivingMales
        File a_MaleDirectory_File = new File(
                a_DataDirectory_File,
                "LivingMales");
        a_GENESIS_AgentCollectionManager.setLivingMaleDirectory(
                a_MaleDirectory_File);
        long aIndexOfLastLivingMaleCollection =
                Generic_StaticIO.getArchiveHighestLeaf(
                a_MaleDirectory_File,
                "_");
        GENESIS_MaleCollection a_LastLivingMaleCollection =
                a_GENESIS_AgentCollectionManager.getMaleCollection(aIndexOfLastLivingMaleCollection,
                GENESIS_Person.getTypeLivingMale_String(),
                HandleOutOfMemoryError);
        a_GENESIS_AgentCollectionManager.IndexOfLastBornMale =
                a_LastLivingMaleCollection.getMaxAgentID();

        GENESIS_AgentCollectionManager b_GENESIS_AgentCollectionManager =
                new GENESIS_AgentCollectionManager(ge);
        b_GENESIS_AgentCollectionManager._MaximumNumberOfAgentsPerAgentCollection =
                new Integer(b_MaximumNumberOfAgentsPerAgentCollection);
        b_GENESIS_AgentCollectionManager.MaximumNumberOfObjectsPerDirectory =
                new Integer(b_MaximumNumberOfObjectsPerDirectory);

        // LivingFemales
        File b_FemaleDirectory_File = new File(
                b_DataDirectory_File,
                "LivingFemales");
        b_GENESIS_AgentCollectionManager.setLivingFemaleDirectory(
                b_FemaleDirectory_File);
        long bIndexOfLastLivingFemaleCollection =
                Generic_StaticIO.getArchiveHighestLeaf(
                b_FemaleDirectory_File,
                "_");
        GENESIS_FemaleCollection b_LastLivingFemaleCollection =
                b_GENESIS_AgentCollectionManager.getFemaleCollection(bIndexOfLastLivingFemaleCollection,
                GENESIS_Person.getTypeLivingFemale_String(),
                HandleOutOfMemoryError);
        b_GENESIS_AgentCollectionManager._IndexOfLastBornFemale =
                b_LastLivingFemaleCollection.getMaxAgentID();

        // LivingMales
        File b_MaleDirectory_File = new File(
                b_DataDirectory_File,
                "LivingMales");
        b_GENESIS_AgentCollectionManager.setLivingMaleDirectory(
                b_MaleDirectory_File);
        long bIndexOfLastLivingMaleCollection =
                Generic_StaticIO.getArchiveHighestLeaf(
                b_MaleDirectory_File,
                "_");
        GENESIS_MaleCollection b_LastLivingMaleCollection =
                b_GENESIS_AgentCollectionManager.getMaleCollection(bIndexOfLastLivingMaleCollection,
                GENESIS_Person.getTypeLivingMale_String(),
                HandleOutOfMemoryError);
        b_GENESIS_AgentCollectionManager.IndexOfLastBornMale =
                b_LastLivingMaleCollection.getMaxAgentID();

        if (a_GENESIS_AgentCollectionManager._IndexOfLastBornFemale
                == b_GENESIS_AgentCollectionManager._IndexOfLastBornFemale) {
            message =
                    "a_GENESIS_AgentCollectionManager._IndexOfLastBornFemale == "
                    + "b_GENESIS_AgentCollectionManager._IndexOfLastBornFemale "
                    + a_GENESIS_AgentCollectionManager._IndexOfLastBornFemale;
            report(message);
        } else {
            message =
                    "a_GENESIS_AgentCollectionManager._IndexOfLastBornFemale != "
                    + "b_GENESIS_AgentCollectionManager._IndexOfLastBornFemale";
            report(message);
            resultsTheSame = false;
        }

        if (a_GENESIS_AgentCollectionManager.IndexOfLastBornMale
                == b_GENESIS_AgentCollectionManager.IndexOfLastBornMale) {
            message =
                    "a_GENESIS_AgentCollectionManager._IndexOfLastBornMale == "
                    + "b_GENESIS_AgentCollectionManager._IndexOfLastBornMale "
                    + a_GENESIS_AgentCollectionManager.IndexOfLastBornMale;
            report(message);
        } else {
            message =
                    "a_GENESIS_AgentCollectionManager._IndexOfLastBornMale != "
                    + "b_GENESIS_AgentCollectionManager._IndexOfLastBornMale";
            report(message);
            resultsTheSame = false;
        }

        File inputFile;

        /**
         * Initialise: a_PregnantFemale_ID_HashSet
         * a_NearlyDuePregnantFemale_ID_HashSet a_NotPregnantFemale_ID_HashSet
         * a_Female_ID_HashSet a_Male_ID_HashSet
         */
        inputFile = new File(
                a_DataDirectory_File,
                "PregnantFemale_ID_HashSet.thisFile");
        HashSet<Long> a_PregnantFemale_ID_HashSet = (HashSet<Long>) Generic_StaticIO.readObject(inputFile);
        inputFile = new File(
                a_DataDirectory_File,
                "NearlyDuePregnantFemale_ID_HashSet.thisFile");
        HashSet<Long> a_NearlyDuePregnantFemale_ID_HashSet = (HashSet<Long>) Generic_StaticIO.readObject(inputFile);
        inputFile = new File(
                a_DataDirectory_File,
                "NotPregnantFemale_ID_HashSet.thisFile");
        HashSet<Long> a_NotPregnantFemale_ID_HashSet = (HashSet<Long>) Generic_StaticIO.readObject(inputFile);
        inputFile = new File(
                a_DataDirectory_File,
                "Female_ID_HashSet.thisFile");
        HashSet<Long> a_Female_ID_HashSet = (HashSet<Long>) Generic_StaticIO.readObject(inputFile);
        inputFile = new File(
                a_DataDirectory_File,
                "Male_ID_HashSet.thisFile");
        HashSet<Long> a_Male_ID_HashSet = (HashSet<Long>) Generic_StaticIO.readObject(inputFile);

        /**
         * Dead
         */
        a_GENESIS_AgentCollectionManager.setDeadFemaleDirectory(
                new File(
                a_DataDirectory_File,
                "DeadFemales"));
        a_GENESIS_AgentCollectionManager._LargestIndexOfDeadFemaleCollection =
                Generic_StaticIO.getArchiveHighestLeaf(
                a_FemaleDirectory_File,
                "_");
        a_GENESIS_AgentCollectionManager.setDeadMaleDirectory(
                new File(
                a_DataDirectory_File,
                "DeadMales"));
        a_GENESIS_AgentCollectionManager._LargestIndexOfDeadMaleCollection =
                Generic_StaticIO.getArchiveHighestLeaf(
                a_MaleDirectory_File,
                "_");

        /**
         * Initialise _GENESIS_AgentCollectionManager._DeadFemaleCollection
         */
        a_GENESIS_AgentCollectionManager._DeadFemaleCollection =
                (GENESIS_FemaleCollection) Generic_StaticIO.readObject(new File(
                a_DataDirectory_File,
                "Dead_GENESIS_FemaleCollection.thisFile"));
        a_GENESIS_AgentCollectionManager._DeadFemaleCollection.ge = ge;
        /**
         * Initialise _GENESIS_AgentCollectionManager._DeadMaleCollection
         */
        a_GENESIS_AgentCollectionManager._DeadMaleCollection =
                (GENESIS_MaleCollection) Generic_StaticIO.readObject(new File(
                a_DataDirectory_File,
                "Dead_GENESIS_MaleCollection.thisFile"));
        a_GENESIS_AgentCollectionManager._DeadMaleCollection.ge = ge;
        /**
         * Initialise
         * _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap
         */
        a_GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap =
                (HashMap) Generic_StaticIO.readObject(new File(
                a_DataDirectory_File,
                "Dead_GENESIS_Female_HashMap.thisFile"));
        /**
         * Initialise
         * _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap
         */
        a_GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap =
                (HashMap) Generic_StaticIO.readObject(new File(
                a_DataDirectory_File,
                "Dead_GENESIS_Male_HashMap.thisFile"));
        /**
         * Load _Demographics._Population
         */
        GENESIS_Demographics a_Demographics =
                (GENESIS_Demographics) Generic_StaticIO.readObject(
                new File(
                a_DataDirectory_File,
                "Demographics.thisFile"));
//        a_Demographics._Population.get(
//                GENESIS_Demographics.TotalPopulationName_String)._GENESIS_Environment = _GENESIS_Environment;

        /**
         * Initialise: b_PregnantFemale_ID_HashSet
         * b_NearlyDuePregnantFemale_ID_HashSet b_NotPregnantFemale_ID_HashSet
         * b_Female_ID_HashSet b_Male_ID_HashSet
         */
        inputFile = new File(
                b_DataDirectory_File,
                "PregnantFemale_ID_HashSet.thisFile");
        HashSet<Long> b_PregnantFemale_ID_HashSet = (HashSet<Long>) Generic_StaticIO.readObject(inputFile);
        inputFile = new File(
                b_DataDirectory_File,
                "NearlyDuePregnantFemale_ID_HashSet.thisFile");
        HashSet<Long> b_NearlyDuePregnantFemale_ID_HashSet = (HashSet<Long>) Generic_StaticIO.readObject(inputFile);
        inputFile = new File(
                b_DataDirectory_File,
                "NotPregnantFemale_ID_HashSet.thisFile");
        HashSet<Long> b_NotPregnantFemale_ID_HashSet = (HashSet<Long>) Generic_StaticIO.readObject(inputFile);
        inputFile = new File(
                b_DataDirectory_File,
                "Female_ID_HashSet.thisFile");
        HashSet<Long> b_Female_ID_HashSet = (HashSet<Long>) Generic_StaticIO.readObject(inputFile);
        inputFile = new File(
                b_DataDirectory_File,
                "Male_ID_HashSet.thisFile");
        HashSet<Long> b_Male_ID_HashSet = (HashSet<Long>) Generic_StaticIO.readObject(inputFile);

        /**
         * Dead
         */
        b_GENESIS_AgentCollectionManager.setDeadFemaleDirectory(
                new File(
                b_DataDirectory_File,
                "DeadFemales"));
        b_GENESIS_AgentCollectionManager._LargestIndexOfDeadFemaleCollection =
                Generic_StaticIO.getArchiveHighestLeaf(
                b_FemaleDirectory_File,
                "_");
        b_GENESIS_AgentCollectionManager.setDeadMaleDirectory(
                new File(
                b_DataDirectory_File,
                "DeadMales"));
        b_GENESIS_AgentCollectionManager._LargestIndexOfDeadMaleCollection =
                Generic_StaticIO.getArchiveHighestLeaf(
                b_MaleDirectory_File,
                "_");

        /**
         * Initialise _GENESIS_AgentCollectionManager._DeadFemaleCollection
         */
        b_GENESIS_AgentCollectionManager._DeadFemaleCollection =
                (GENESIS_FemaleCollection) Generic_StaticIO.readObject(new File(
                b_DataDirectory_File,
                "Dead_GENESIS_FemaleCollection.thisFile"));
        b_GENESIS_AgentCollectionManager._DeadFemaleCollection.ge = ge;
        /**
         * Initialise _GENESIS_AgentCollectionManager._DeadMaleCollection
         */
        b_GENESIS_AgentCollectionManager._DeadMaleCollection =
                (GENESIS_MaleCollection) Generic_StaticIO.readObject(new File(
                b_DataDirectory_File,
                "Dead_GENESIS_MaleCollection.thisFile"));
        b_GENESIS_AgentCollectionManager._DeadMaleCollection.ge = ge;
        /**
         * Initialise
         * _GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap
         */
        b_GENESIS_AgentCollectionManager._DeadFemaleCollection_HashMap =
                (HashMap) Generic_StaticIO.readObject(new File(
                b_DataDirectory_File,
                "Dead_GENESIS_Female_HashMap.thisFile"));
        /**
         * Initialise
         * _GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap
         */
        b_GENESIS_AgentCollectionManager._DeadMaleCollection_HashMap =
                (HashMap) Generic_StaticIO.readObject(new File(
                b_DataDirectory_File,
                "Dead_GENESIS_Male_HashMap.thisFile"));
        /**
         * Load _Demographics._Population
         */
        GENESIS_Demographics b_Demographics =
                (GENESIS_Demographics) Generic_StaticIO.readObject(
                new File(
                b_DataDirectory_File,
                "Demographics.thisFile"));
//        b_Demographics._Population.get(
//                GENESIS_Demographics.TotalPopulationName_String)._GENESIS_Environment = _GENESIS_Environment;


        /**
         * Compare populations In order to do this, we need ordered populations
         * which we probably should have, but in initialisation of a population
         * we are somewhat randomising birth dates which is a problem. If
         * Individual IDs were in a guaranteed birth order, then for two
         * populations, a check of birth dates, death dates and other attributes
         * for each female/male with consecutive IDs would work...
         */
        /**
         * First check Living Collections
         */
        if (resultsTheSame) {
//            long a_MaxLivingMaleCollectionID = a_GENESIS_AgentCollectionManager.getMaxLivingMaleCollectionID();
//            GENESIS_MaleCollection a_GENESIS_MaleCollection;
//            for (long a_MaleCollectionID = 0; a_MaleCollectionID < a_MaxLivingMaleCollectionID; a_MaleCollectionID++) {
//                a_GENESIS_MaleCollection = a_GENESIS_AgentCollectionManager.LivingMales.get(
//                        a_MaleCollectionID);
//            }
            GENESIS_Male a_GENESIS_Male;
            GENESIS_Male b_GENESIS_Male;
            for (long a_ID = 0L; a_ID < b_GENESIS_AgentCollectionManager.IndexOfLastBornMale; a_ID++) {
                a_GENESIS_Male = a_GENESIS_AgentCollectionManager.getMale(a_ID,
                        GENESIS_Person.getTypeLivingMale_String(),
                        HandleOutOfMemoryError);
                b_GENESIS_Male = b_GENESIS_AgentCollectionManager.getMale(a_ID, GENESIS_Person.getTypeLivingMale_String(), HandleOutOfMemoryError);
                if (a_GENESIS_Male == null) {
                    if (b_GENESIS_Male == null) {
                        a_GENESIS_Male = a_GENESIS_AgentCollectionManager.getMale(a_ID,
                                GENESIS_Person.getTypeDeadMale_String(),
                                HandleOutOfMemoryError);
                        b_GENESIS_Male = b_GENESIS_AgentCollectionManager.getMale(a_ID,
                                GENESIS_Person.getTypeDeadMale_String(),
                                HandleOutOfMemoryError);
                    } else {
                        resultsTheSame = false;
                    }
                }
                if (a_GENESIS_Male.toString().equalsIgnoreCase(b_GENESIS_Male.toString())) {
                    message = "a_GENESIS_Male == b_GENESIS_Male " + a_GENESIS_Male;
                    report(message);
                } else {
                    resultsTheSame = false;
                }
            }
            if (resultsTheSame) {
                message = "Living Males the same";
                report(message);
            }
        }
        if (resultsTheSame) {
            GENESIS_Female a_GENESIS_Female;
            GENESIS_Female b_GENESIS_Female;
            for (long a_ID = 0L; a_ID < b_GENESIS_AgentCollectionManager._IndexOfLastBornFemale; a_ID++) {
                a_GENESIS_Female = a_GENESIS_AgentCollectionManager.getFemale(a_ID, GENESIS_Person.getTypeLivingFemale_String(), HandleOutOfMemoryError);
                b_GENESIS_Female = b_GENESIS_AgentCollectionManager.getFemale(a_ID, GENESIS_Person.getTypeLivingFemale_String(), HandleOutOfMemoryError);
                if (a_GENESIS_Female == null) {
                    if (b_GENESIS_Female == null) {
                        a_GENESIS_Female = a_GENESIS_AgentCollectionManager.getFemale(a_ID, GENESIS_Person.getTypeDeadFemale_String(), HandleOutOfMemoryError);
                        b_GENESIS_Female = b_GENESIS_AgentCollectionManager.getFemale(a_ID, GENESIS_Person.getTypeDeadFemale_String(), HandleOutOfMemoryError);
                    } else {
                        resultsTheSame = false;
                    }
                }
                if (a_GENESIS_Female.toString().equalsIgnoreCase(b_GENESIS_Female.toString())) {
                    message = "a_GENESIS_Female == b_GENESIS_Female " + a_GENESIS_Female;
                    report(message);
                } else {
                    resultsTheSame = false;
                }
            }
            if (resultsTheSame) {
                message = "Living Females the same";
                report(message);
            }
        }
        if (resultsTheSame) {
            comparisonTest_PrintWriter.println("0");
        } else {
            comparisonTest_PrintWriter.println("1");
        }
        comparisonTest_PrintWriter.close();
    }

    private void report(String message) {
        comparisonTest_PrintWriter.println(message);
        System.out.println(message);
    }

    public long getStartYearForResult(
            File resultDirectory,
            String underscore) {
        MetadataType m = getMetadataForResult(
                resultDirectory,
                underscore);
        return m.getStartYear();
    }

    public MetadataType getMetadataForResult(
            File resultDirectory,
            String underscore) {
        File theDemographicModel_Aspatial_File = new File(
                resultDirectory,
                "GENESIS_DemographicModel");
        File aResult = Generic_StaticIO.getArchiveHighestLeafFile(
                theDemographicModel_Aspatial_File.listFiles()[0],
                underscore);
        File metadataDir = new File(
                aResult,
                "metadata");
        File metadataFile = new File(
                metadataDir,
                "metadata.xml");
        if (!metadataFile.exists()) {
            System.err.print("" + metadataFile + " does not exist!");
        }
        MetadataFactory.init();
        MetadataType m = null;
        try {
            m = MetadataFactory.read(metadataFile);
        } catch (JAXBException ex) {
            Logger.getLogger(GENESIS_CompareModelDemographic.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }
}
