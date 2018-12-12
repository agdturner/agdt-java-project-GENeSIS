/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.visualisation;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.andyt.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.math.stats.Generic_Statistics;
import uk.ac.leeds.ccg.andyt.generic.execution.Generic_Execution;
import uk.ac.leeds.ccg.andyt.generic.visualisation.Generic_Visualisation;
import uk.ac.leeds.ccg.andyt.chart.Generic_AgeGenderBoxPlot;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_DataHandler;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.CommonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Population;

/**
 * An implementation of
 * <code>Generic_JApplet_AgeGenderBoxPlot<\code>
 *
 * If you run this class it will attempt to generate an Age by Gender
 * Population Box Plot Visualization of some default data and display it on
 * screen.
 */
public class GENESIS_AgeGenderBoxPlot extends Generic_AgeGenderBoxPlot {

    public File resultsDirectory;
    public GENESIS_Environment ge;

    public GENESIS_AgeGenderBoxPlot() {
    }

    public GENESIS_AgeGenderBoxPlot(
            ExecutorService executorService,
            File file,
            String format,
            File resultsDirectory,
            String title,
            int dataWidth,
            int dataHeight,
            String xAxisLabel,
            String yAxisLabel,
            boolean drawOriginLinesOnPlot,
            int ageInterval,
            int startAgeOfEndYearInterval,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode aRoundingMode,
            GENESIS_Environment a_GENESIS_Environment) {
        init(
                executorService,
                file,
                format,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                ageInterval,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                aRoundingMode);
        this.resultsDirectory = resultsDirectory;
        this.ge = a_GENESIS_Environment;
    }

    protected final void init(
            ExecutorService executorService,
            File file,
            String format,
            File resultsDirectory,
            String title,
            int dataWidth,
            int dataHeight,
            String xAxisLabel,
            String yAxisLabel,
            boolean drawOriginLinesOnPlot,
            int ageInterval,
            int startAgeOfEndYearInterval,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode aRoundingMode,
            GENESIS_Environment a_GENESIS_Environment) {
        super.init(
                executorService,
                file,
                format,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                ageInterval,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                aRoundingMode);
        this.resultsDirectory = resultsDirectory;
        this.ge = a_GENESIS_Environment;
    }

    public static void main(String[] args) {
        Generic_Visualisation.getHeadlessEnvironment();
        /*
         * Initialise title and File to write image to
         */
        String title;
        File file;
        String format = "PNG";
        if (args.length != 2) {
            System.out.println(
                    "Expected 2 args:"
                    + " args[0] title;"
                    + " args[1] File."
                    + " Recieved " + args.length + " args.");
            // Use defaults
            title = "Age Gender Population Box Plot";
            System.out.println("Use default title: " + title);
            file = new File(
                    new File(System.getProperty("user.dir")),
                    title.replace(" ", "_") + "." + format);
            System.out.println("Use default File: " + file.toString());
        } else {
            title = args[0];
            file = new File(
                    new File(args[1]),
                    title + "." + format);
        }
        File workspaceDirectory = new File(
                "/nfs/see-fs-02_users/geoagdt/src/andyt/java/projects/genesis/genesis_1/workspace/");
//        File workspaceDirectory = new File(
//                "/scratch01/Work/Projects/GENESIS/workspace/");
//        File resultsDirectory = new File(
//                workspaceDirectory,
//                "DemographicModel_Aspatial_1");
        File resultsDirectory = new File(
                workspaceDirectory.getParentFile().toString() + "/data/testXML/");
        GENESIS_Environment a_GENESIS_Environment = new GENESIS_Environment(workspaceDirectory);
        int dataWidth = 700;//250;
        int dataHeight = 600;
        String xAxisLabel = "Population";
        String yAxisLabel = "Age";
        boolean drawOriginLinesOnPlot = true;
        //int legendHeight = 0;
        int ageInterval = 3;//5;//3;//2;//1;//50;//5;
        int startAgeOfStartYearInterval = 0;
        int startAgeOfEndYearInterval = 95;
        while (startAgeOfEndYearInterval % 3 != 0) {
            startAgeOfEndYearInterval--;
        }
        int decimalPlacePrecisionForCalculations = 10;
        int decimalPlacePrecisionForDisplay = 3;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        title += " (" + ageInterval
                + " year age intervals, last age interval from age "
                + startAgeOfEndYearInterval + ")";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        GENESIS_AgeGenderBoxPlot plot = new GENESIS_AgeGenderBoxPlot(
                executorService,
                file,
                format,
                resultsDirectory,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                ageInterval,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                a_GENESIS_Environment);
        plot.setData(
                ageInterval,
                startAgeOfStartYearInterval,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                roundingMode,
                resultsDirectory,
                a_GENESIS_Environment);
        plot.run();
        Generic_Execution.shutdownExecutorService(executorService, plot.future, plot);
    }

    public void setData(
            int ageInterval,
            int startAgeOfStartYearInterval,
            int startAgeOfEndYearInterval,
            int decimalPlacePrecisionForCalculations,
            RoundingMode roundingMode,
            File resultsDirectory,
            GENESIS_Environment a_GENESIS_Environment) {
        HashSet<File> population_Files = new HashSet<File>();
        HashSet<File> results_Files = Generic_IO.getArchiveLeafFiles(
                resultsDirectory, "_");
        Iterator<File> ite = results_Files.iterator();
        while (ite.hasNext()) {
            File result_File = ite.next();
            File populationFile = new File(
                    result_File,
                    "/data/Demographics/0_99/0/Total/Total_Population__Simulated_Dead__Year_1991.xml");
            population_Files.add(populationFile);
//            File[] xmlfiles = result_File.listFiles();
//            for (int i = 0; i < xmlfiles.length; i++) {
//                File xmlfile = xmlfiles[i];
//                if (!xmlfile.getName().startsWith("Total")) {
//                    population_Files.add(xmlfile);
//                }
//            }
        }
        HashSet<GENESIS_Population> pops = new HashSet<GENESIS_Population>();
        ite = population_Files.iterator();
        while (ite.hasNext()) {
            pops.add(new GENESIS_Population(
                    a_GENESIS_Environment,
                    ite.next()));
        }
        setData(
                pops,
                ageInterval,
                startAgeOfStartYearInterval,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                roundingMode);
    }

    public void setData(
            HashSet<GENESIS_Population> pops,
            int ageInterval,
            int startAgeOfStartYearInterval,
            int startAgeOfEndYearInterval,
            int decimalPlacePrecisionForCalculations,
            RoundingMode roundingMode) {
        Object[] data = new Object[5];
        // Aggregate pops into ageInterval groups
        HashMap<Integer, GENESIS_Population> indexedAggregatedPops = new HashMap<Integer, GENESIS_Population>();
        int index = 0;
        Iterator<GENESIS_Population> ite;
        ite = pops.iterator();
        Long minAge = Long.valueOf(startAgeOfStartYearInterval);
        Long maxAge = Long.valueOf(startAgeOfEndYearInterval);
        Long interval = Long.valueOf(ageInterval);
        BigDecimal maxPop = BigDecimal.ZERO;
        while (ite.hasNext()) {
            GENESIS_Population pop = ite.next();
            Object[] aggregatedPop = GENESIS_Population.getAggregateGENESIS_Population(pop,
                    minAge,
                    maxAge,
                    interval,
                    pop.ge);
            indexedAggregatedPops.put(index, (GENESIS_Population) aggregatedPop[0]);
            index++;
            maxPop = maxPop.max((BigDecimal) aggregatedPop[1]);
        }

        System.out.println("Get Box Plot Statistics for " + indexedAggregatedPops.size() + " results");
        // Female
        BigDecimal maxValue = BigDecimal.ZERO;
        TreeMap<Integer, BigDecimal[]> femaleBoxPlotStatistics =
                new TreeMap<Integer, BigDecimal[]>();
        ArrayList<BigDecimal> values;
        for (int age = startAgeOfStartYearInterval; age < startAgeOfEndYearInterval + ageInterval; age += ageInterval) {
            values = new ArrayList<BigDecimal>();
            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(
                    Long.valueOf(age),
                    Long.valueOf(age + ageInterval));
            for (int pop = 0; pop < indexedAggregatedPops.size(); pop++) {
                BigDecimal value = indexedAggregatedPops.get(pop).getFemalePopulation(ageBound);
                values.add(value);
                maxValue = maxValue.max(value);
            }
            BigDecimal[] boxPlotStatistics =
                    Generic_Statistics.getSummaryStatistics_0(
                    values, decimalPlacePrecisionForCalculations, roundingMode);
            femaleBoxPlotStatistics.put(
                    age + ageInterval,
                    boxPlotStatistics);
        }
        // Male
        TreeMap<Integer, BigDecimal[]> maleBoxPlotStatistics =
                new TreeMap<Integer, BigDecimal[]>();
        for (int age = startAgeOfStartYearInterval; age < startAgeOfEndYearInterval + ageInterval; age += ageInterval) {
            values = new ArrayList<BigDecimal>();
            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(
                    Long.valueOf(age),
                    Long.valueOf(age + ageInterval));
            for (int pop = 0; pop < indexedAggregatedPops.size(); pop++) {
                BigDecimal value = indexedAggregatedPops.get(pop).getMalePopulation(ageBound);
                values.add(value);
                maxValue = maxValue.max(value);
            }
            BigDecimal[] boxPlotStatistics =
                    Generic_Statistics.getSummaryStatistics_0(
                    values, decimalPlacePrecisionForCalculations, roundingMode);
            maleBoxPlotStatistics.put(
                    age + ageInterval,
                    boxPlotStatistics);
        }

        data[0] = femaleBoxPlotStatistics;
        data[1] = maleBoxPlotStatistics;
        data[2] = maxValue;
        //result[3] = BigDecimal.valueOf(Math.max(maxFemaleAge, maxMaleAge));
        data[3] = BigDecimal.valueOf(startAgeOfEndYearInterval + ageInterval);
        data[4] = BigDecimal.ZERO;
        setData(data);
    }

    protected static void log(Level level, String message) {
        getLogger().log(level, message);
    }

    public static Logger getLogger() {
        return GENESIS_Log.LOGGER;
    }
}
