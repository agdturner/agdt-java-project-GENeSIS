/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.visualisation;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.generic.math.statistics.Generic_Statistics;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Execution;
import uk.ac.leeds.ccg.andyt.generic.visualisation.Generic_Visualisation;
import uk.ac.leeds.ccg.andyt.generic.visualisation.charts.Generic_AgeGenderLineChart;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Population;

/**
 * An implementation of
 * <code>Generic_JApplet_AgeGenderLineChart<\code>
 *
 * If you run this class it will attempt to generate an Age by Gender
 * Population Box Plot Visualization of some default data and display it on
 * screen.
 */
public class GENESIS_AgeGenderLineChart extends Generic_AgeGenderLineChart {

    public File resultsDirectory;
    public GENESIS_Environment ge;
    public TreeMap resultsToCompare;
    public String format;
    public File outputImageFile;

    public GENESIS_AgeGenderLineChart() {
    }

    public GENESIS_AgeGenderLineChart(
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
            title = "Age Gender Population Line Chart";
            System.out.println("Use default title: " + title);
            file = new File(
                    new File(System.getProperty("user.dir")),
                    title.replace(" ", "_") + "." + format);
            System.out.println("Use default File: " + file.toString());
        } else {
            title = args[0];
            file = new File(args[1]);
        }
        int dataWidth = 700;//250;
        int dataHeight = 600;
        String xAxisLabel = "Population";
        String yAxisLabel = "Age";
        boolean drawOriginLinesOnPlot = true;
        //int legendHeight = 0;
        int ageInterval = 3;//5;//3;//2;//1;//50;//5;
        Long startAgeOfStartYearInterval = 0L;
        Long startAgeOfEndYearInterval = 95L;
        while (startAgeOfEndYearInterval % 3 != 0) {
            startAgeOfEndYearInterval--;
        }
        int decimalPlacePrecisionForCalculations = 10;
        int decimalPlacePrecisionForDisplay = 3;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        title += " (" + ageInterval
                + " year age intervals, last age interval from age "
                + startAgeOfEndYearInterval + ")";
        // Add to title and set other parameters to default values
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
        a_GENESIS_Environment._Directory = workspaceDirectory;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        GENESIS_AgeGenderLineChart chart = new GENESIS_AgeGenderLineChart(
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
                startAgeOfEndYearInterval.intValue(),
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                a_GENESIS_Environment);
        chart.setData(
                ageInterval,
                startAgeOfStartYearInterval,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                roundingMode,
                resultsDirectory,
                a_GENESIS_Environment);
        chart.run();
        Generic_Execution.shutdownExecutorService(
                executorService, chart.future, chart);

    }

    public void setData(
            int ageInterval,
            Long startAgeOfStartYearInterval,
            Long startAgeOfEndYearInterval,
            int decimalPlacePrecisionForCalculations,
            RoundingMode roundingMode,
            File resultsDirectory,
            GENESIS_Environment a_GENESIS_Environment) {
        HashSet<File> population_Files = new HashSet<File>();
        HashSet<File> results_Files = Generic_StaticIO.getArchiveLeafFiles(
                resultsDirectory, "_");
        Iterator<File> ite = results_Files.iterator();
        while (ite.hasNext()) {
            File result_File = ite.next();
            File populationFile = new File(
                    result_File,
                    "/data/Demographics/0_99/0/Total/Total_Population__Simulated_Dead__Year_1991.xml");
            population_Files.add(populationFile);
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
            Long minAge,
            Long maxAge,
            int decimalPlacePrecisionForCalculations,
            RoundingMode roundingMode) {
        Object[] data = new Object[5];
        // Deal with 
        // Convert the TreeMap<GENESIS_AgeBound, BigDecimal> to 
        // TreeMap<Integer, BigDecimal> using minAge and calculate max 
        // population at any age_int at the same time for efficiency.
        Long theMinAgeInYears;
        Long theMaxAgeInYears;
        if (minAge == null) {
            theMinAgeInYears = getMinAge(pops);
        } else {
            theMinAgeInYears = new Long(minAge.longValue());
        }
        if (maxAge == null) {
            theMaxAgeInYears = getMaxAge(pops);
        } else {
            theMaxAgeInYears = new Long(maxAge.longValue());
        }

        // Aggregate pops into ageInterval groups
        HashMap<Integer, GENESIS_Population> indexedAggregatedPops = new HashMap<Integer, GENESIS_Population>();
        int index = 0;
        Iterator<GENESIS_Population> ite;
        ite = pops.iterator();
        Long interval = Long.valueOf(ageInterval);
        BigDecimal maxPop = BigDecimal.ZERO;
        while (ite.hasNext()) {
            GENESIS_Population pop = ite.next();
            Object[] aggregatedPop = GENESIS_Population.getAggregateGENESIS_Population(pop,
                    theMinAgeInYears,
                    theMaxAgeInYears,
                    interval,
                    pop.ge);
            indexedAggregatedPops.put(index, (GENESIS_Population) aggregatedPop[0]);
            index++;
            maxPop = maxPop.max((BigDecimal) aggregatedPop[1]);
        }

        System.out.println("Get Box Plot Statistics for " + indexedAggregatedPops.size() + " results");
        // Female
        BigDecimal maxValue = BigDecimal.ZERO;
        TreeMap<Integer, Object[]> femaleSummaryStatistics =
                new TreeMap<Integer, Object[]>();
        ArrayList<BigDecimal> values;
        for (long age = theMinAgeInYears; age <= theMaxAgeInYears; age += ageInterval) {
//            System.out.println("Female age " + age);
            values = new ArrayList<BigDecimal>();
            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(
                    Long.valueOf(age),
                    Long.valueOf(age + ageInterval));
            for (int pop = 0; pop < indexedAggregatedPops.size(); pop++) {
                BigDecimal value = indexedAggregatedPops.get(pop).getFemalePopulation(ageBound);
                values.add(value);
                maxValue = maxValue.max(value);
            }
            Object[] summaryStatistics_1 = Generic_Statistics.getSummaryStatistics_1(
                    values, decimalPlacePrecisionForCalculations, roundingMode);
            // Set maxValue to be the maximum of the mean added to the standard 
            // deviation
            BigDecimal[] firstOrderStatistics = (BigDecimal[]) summaryStatistics_1[0];
            BigDecimal[] secondOrderStatistics = (BigDecimal[]) summaryStatistics_1[1];
            maxValue = maxValue.max(
                    firstOrderStatistics[1].add(secondOrderStatistics[5]));
            femaleSummaryStatistics.put(
                    (int) age,
                    summaryStatistics_1);
        }
        // Male
        TreeMap<Integer, Object[]> maleSummaryStatistics =
                new TreeMap<Integer, Object[]>();
        for (long age = theMinAgeInYears; age <= theMaxAgeInYears; age += ageInterval) {
//            System.out.println("Male age " + age);
            values = new ArrayList<BigDecimal>();
            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(
                    Long.valueOf(age),
                    Long.valueOf(age + ageInterval));
            for (int pop = 0; pop < indexedAggregatedPops.size(); pop++) {
                BigDecimal value = indexedAggregatedPops.get(pop).getMalePopulation(ageBound);
                values.add(value);
                maxValue = maxValue.max(value);
            }
            Object[] summaryStatistics_1 = Generic_Statistics.getSummaryStatistics_1(
                    values, decimalPlacePrecisionForCalculations, roundingMode);
            // Set maxValue to be the maximum of the mean added to the standard 
            // deviation
            BigDecimal[] firstOrderStatistics = (BigDecimal[]) summaryStatistics_1[0];
            BigDecimal[] secondOrderStatistics = (BigDecimal[]) summaryStatistics_1[1];
            maxValue = maxValue.max(
                    firstOrderStatistics[1].add(secondOrderStatistics[5]));
            maleSummaryStatistics.put(
                    (int) age,
                    summaryStatistics_1);
        }
        data[0] = femaleSummaryStatistics;
        data[1] = maleSummaryStatistics;
        data[2] = maxValue;
        //result[3] = BigDecimal.valueOf(Math.max(maxFemaleAge, maxMaleAge));
        data[3] = BigDecimal.valueOf(theMaxAgeInYears);
        data[4] = BigDecimal.valueOf(theMinAgeInYears);
        setData(data);
    }

    public Long getMinAge(HashSet<GENESIS_Population> pops) {
        long minAge = Long.MAX_VALUE;
        Iterator<GENESIS_Population> ite = pops.iterator();
        while (ite.hasNext()) {
            GENESIS_Population pop = ite.next();
            Long minAgeOfPop = pop.getMinAgeYears();
            if (minAgeOfPop != null) {
                minAge = Math.min(minAge, minAgeOfPop.longValue());
            }
        }
        if (minAge == Long.MAX_VALUE) {
            return 0L;
        } else {
            return minAge;
        }
    }

    public Long getMaxAge(HashSet<GENESIS_Population> pops) {
        long maxAge = Long.MIN_VALUE;
        Iterator<GENESIS_Population> ite = pops.iterator();
        while (ite.hasNext()) {
            GENESIS_Population pop = ite.next();
            Long maxAgeOfPop = pop.getMaxAgeYears();
            if (maxAgeOfPop != null) {
                maxAge = Math.max(maxAge, maxAgeOfPop.longValue());
            }
        }
        if (maxAge == Long.MIN_VALUE) {
            return 0L;
        } else {
            return maxAge;
        }
    }

    /**
     * Override to ensure a gap between the title and the data by adding
     * ageInterval to maxY.
     *
     * @param data
     */
    @Override
    public void initialiseParameters(Object[] data) {
        BigDecimal maxX = new BigDecimal(((BigDecimal) data[2]).toString());
        setMaxX(maxX);
        setMinX(maxX.negate());
        BigDecimal maxY = new BigDecimal(((BigDecimal) data[3]).toString());
        maxY = maxY.add(BigDecimal.valueOf(getAgeInterval()));
        setMaxY(maxY);
        BigDecimal minY = new BigDecimal(((BigDecimal) data[4]).toString());
        setMinY(minY);
        setCellHeight();
        setCellWidth();
        setOriginRow();
        setOriginCol();
    }

    /**
     * @TODO refactor - duplicate of drawYAxis in GENESIS_AgeGenderLineChart
     * @param interval
     * @param textHeight
     * @param startAgeOfEndYearInterval
     * @param scaleTickLength
     * @param scaleTickAndTextSeparation
     * @param partTitleGap
     * @param seperationDistanceOfAxisAndData
     * @return
     */
    @Override
    public int[] drawYAxis(
            int interval,
            int textHeight,
            int startAgeOfEndYearInterval,
            int scaleTickLength,
            int scaleTickAndTextSeparation,
            int partTitleGap,
            int seperationDistanceOfAxisAndData) {
        int[] result = new int[1];
        int yAxisExtraWidthLeft = 0;
        int originCol = getOriginCol();
        int dataStartRow = getDataStartRow();
        int dataEndRow = getDataEndRow();
        Line2D ab;
        // Draw origin
        if (isDrawOriginLinesOnPlot()) {
            setPaint(Color.LIGHT_GRAY);
            ab = new Line2D.Double(
                    originCol,
                    dataStartRow,
                    originCol,
                    dataEndRow);
            draw(ab);
        }
//        // Draw Y axis scale to the left side
//        setPaint(Color.GRAY);
//        int col = getDataStartCol();
//        ab = new Line2D.Double(
//                col,
//                dataEndRow,
//                col,
//                dataStartRow);
//        draw(ab);
        setPaint(Color.GRAY);
        BigDecimal cellHeight = getCellHeight();
        int barHeight;
        if (cellHeight.compareTo(BigDecimal.ZERO) == 0) {
            barHeight = 1;
        } else {
            barHeight = Generic_BigDecimal.divideRoundIfNecessary(
                    BigDecimal.valueOf(interval),
                    getCellHeight(),
                    0,
                    getRoundingMode()).intValue();
        }
        int barHeightdiv2 = barHeight / 2;

        int increment = interval;
        int dataHeight = getDataHeight();
        while (((startAgeOfEndYearInterval * textHeight) + 4) / increment > dataHeight) {
            increment += interval;
        }
        String text;
        int maxTickTextWidth = 0;
        int col = getDataStartCol();
        int miny_int = getMinY().intValue();
        //for (int i = miny_int; i <= startAgeOfEndYearInterval; i += increment) {
        //for (int i = miny_int; i <= getMaxY().intValue(); i += increment) {
        for (int i = miny_int; i < getMaxY().intValue(); i += increment) {

            // int row = coordinateToScreenRow(BigDecimal.valueOf(i));
            int row = coordinateToScreenRow(BigDecimal.valueOf(i)) - barHeightdiv2;
            //int row = coordinateToScreenRow(BigDecimal.valueOf(i)) - barHeight;

            setPaint(Color.GRAY);
//            ab = new Line2D.Double(
//                    col,
//                    row,
//                    col - scaleTickLength,
//                    row);
//            draw(ab);
            //text = "" + i + " - " + (i + increment);
            text = "" + i;
            int textWidth = getTextWidth(text);
            drawString(
                    text,
                    col - scaleTickAndTextSeparation - scaleTickLength - textWidth,
                    //row);
                    row + (textHeight / 3));
            maxTickTextWidth = Math.max(maxTickTextWidth, textWidth);
        }
        yAxisExtraWidthLeft += scaleTickLength + scaleTickAndTextSeparation + maxTickTextWidth;
        // Y axis label
        setPaint(Color.BLACK);
        String yAxisLabel = getyAxisLabel();
        int textWidth = getTextWidth(yAxisLabel);
        double angle = 3.0d * Math.PI / 2.0d;
        col = 3 * textHeight / 2;
        writeText(
                yAxisLabel,
                angle,
                col,
                getDataMiddleRow() + (textWidth / 2));
        yAxisExtraWidthLeft += (textHeight * 2) + partTitleGap;
        result[0] = yAxisExtraWidthLeft;
        return result;
    }

    protected static void log(Level level, String message) {
        getLogger().log(level, message);
    }

    public static Logger getLogger() {
        return GENESIS_Log.logger;
    }
}
