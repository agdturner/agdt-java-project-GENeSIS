package uk.ac.leeds.ccg.andyt.projects.genesis.visualisation;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Collections;
import uk.ac.leeds.ccg.andyt.generic.visualisation.Generic_Visualisation;
import uk.ac.leeds.ccg.andyt.generic.visualisation.charts.Generic_AgeGenderBarChart;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Population;

/**
 * An implementation of
 * <code>Generic_AgeGenderBarChart<\code>
 *
 * If you run this class it will attempt to generate an Age by Gender
 * Population Bar Chart Visualization of some default data and display it on
 * screen.
 */
public class GENESIS_AgeGenderBarChart extends Generic_AgeGenderBarChart {

    public File resultsDirectory;
    public GENESIS_Environment _GENESIS_Environment;

    public GENESIS_AgeGenderBarChart() {
    }

    public GENESIS_AgeGenderBarChart(
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
            Integer startAgeOfEndYearInterval,
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
                1,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                aRoundingMode);
        this.resultsDirectory = resultsDirectory;
        this._GENESIS_Environment = a_GENESIS_Environment;
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
            Integer startAgeOfEndYearInterval,
            int decimalPlacePrecisionForCalculations,
            int significantDigits,
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
                1,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                significantDigits,
                aRoundingMode);
        this.resultsDirectory = resultsDirectory;
        this._GENESIS_Environment = a_GENESIS_Environment;
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
            title = "Age Gender Population Bar Chart";
            System.out.println("Use default title: " + title);
            file = new File(
                    new File(System.getProperty("user.dir")),
                    title.replace(" ", "_") + "." + format);
            System.out.println("Use default File: " + file.toString());
        } else {
            title = args[0];
            file = new File(args[1]);
        }
        int decimalPlacePrecisionForCalculations = 10;
        int decimalPlacePrecisionForDisplay = 3;
        File workspaceDirectory = new File(
                "/scratch01/Work/Projects/GENESIS/workspace/DemographicModel_Aspatial_1/0_99/0/");
//        File workspaceDirectory = new File(
//                "/nfs/see-fs-02_users/geoagdt/src/andyt/java/projects/genesis/genesis_1/workspace/");
        GENESIS_Environment ge = new GENESIS_Environment(workspaceDirectory);
//        File resultsDirectory = new File(
//                workspaceDirectory,
//                "DemographicModel_Aspatial_1");
//        resultsDirectory = new File(
//                workspaceDirectory,
//                "0_99");
        File populationFile = new File(
                workspaceDirectory.toString()
                + "/data/Demographics/0_99/0/Total/Total_Population__Theoretically_Expected_Living__Year_1991.xml");
        GENESIS_Population pop = new GENESIS_Population(
                ge,
                populationFile);
        int dataWidth = 700;//250;
        int dataHeight = 600;
        String xAxisLabel = "Population";
        String yAxisLabel = "Age";
        //boolean drawOriginLinesOnPlot = true;
        boolean drawOriginLinesOnPlot = false;
        RoundingMode roundingMode = RoundingMode.HALF_DOWN;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        GENESIS_AgeGenderBarChart chart = new GENESIS_AgeGenderBarChart(
                executorService,
                file,
                format,
                workspaceDirectory,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                dataHeight,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                ge);
        chart.setData(pop, null, null);
        chart.run();
    }

//    public void run(
//            String title,
//            String format,
//            File aFile,
//            GENESIS_Population pop,
//            int decimalPlacePrecisionForCalculations,
//            int decimalPlacePrecisionForDisplay) {
//        run(title,
//                format,
//                aFile,
//                pop,
//                decimalPlacePrecisionForCalculations,
//                decimalPlacePrecisionForDisplay,
//                pop.getMinAgeInYearsWithPositivePopulation(),
//                pop.getMaxAgeInYearsWithPositivePopulation());
//    }
//    
//    public void run(
//            String title,
//            String format,
//            File aFile,
//            GENESIS_Population pop,
//            int decimalPlacePrecisionForCalculations,
//            int decimalPlacePrecisionForDisplay,
//            BigDecimal minAge,
//            BigDecimal maxAge) {
//        setDecimalPlacePrecisionForCalculations(decimalPlacePrecisionForCalculations);
//        setDecimalPlacePrecisionForDisplay(decimalPlacePrecisionForDisplay);
//        //setStartAgeOfEndYearInterval((Integer) getData()[3]);
//        //this.startAgeOfEndYearInterval = (Integer) data[3];
//        run(title,
//                format,
//                aFile,
//                pop._GENESIS_Environment,
//                decimalPlacePrecisionForCalculations,
//                decimalPlacePrecisionForDisplay);
//        setData(pop, minAge, maxAge);
//        
//    }
//    public void run(
//            String title,
//            String format,
//            File aFile,
//            GENESIS_Environment a_GENESIS_Environment,
//            int decimalPlacePrecisionForCalculations,
//            int decimalPlacePrecisionForDisplay) {
//        run(title,
//                format,
//                aFile,
//                resultsDirectory,
//                a_GENESIS_Environment,
//                dataWidth,
//                dataHeight,
//                xAxisLabel,
//                yAxisLabel,
//                drawOriginLinesOnPlot,
//                getStartAgeOfEndYearInterval(),
//                decimalPlacePrecisionForCalculations,
//                decimalPlacePrecisionForDisplay,
//                roundingMode);
//    }
//    public void run(
//            File file,
//            String format,
//            String title,
//            File resultsDirectory,
//            GENESIS_Environment a_GENESIS_Environment,
//            int dataWidth,
//            int dataHeight,
//            String xAxisLabel,
//            String yAxisLabel,
//            boolean drawOriginLinesOnPlot,
//            int startAgeOfEndYearInterval,
//            int decimalPlacePrecisionForCalculations,
//            int decimalPlacePrecisionForDisplay,
//            RoundingMode aRoundingMode) {
//        init(
//                file,
//                format,
//                resultsDirectory,
//                title,
//                dataWidth,
//                dataHeight,
//                xAxisLabel,
//                yAxisLabel,
//                drawOriginLinesOnPlot,
//                startAgeOfEndYearInterval,
//                decimalPlacePrecisionForCalculations,
//                decimalPlacePrecisionForDisplay,
//                aRoundingMode,
//                a_GENESIS_Environment);
//        run(format, aFile);
//    }
    /**
     * Currently deals only with int converted minAge and maxAge If minAge is
     * null then the minAge in pop is used. If maxAge is null then the maxAge in
     * pop is used.
     *
     * @param pop
     * @param minAge
     * @param maxAge
     */
    public void setData(
            GENESIS_Population pop,
            Long minAge,
            Long maxAge) {
        Object[] data = new Object[5];
        // Convert the TreeMap<GENESIS_AgeBound, BigDecimal> to 
        // TreeMap<Integer, BigDecimal> using minAge and calculate max 
        // population at any age_int at the same time for efficiency.
        long age;
        Long theMinAgeInYears;
        Long theMaxAgeInYears;
        if (minAge == null) {
            theMinAgeInYears = pop.getMinAgeYears();
        } else {
            theMinAgeInYears = new Long(minAge);
        }
        if (maxAge == null) {
            theMaxAgeInYears = pop.getMaxAgeYears();
        } else {
            theMaxAgeInYears = new Long(maxAge);
        }
        BigDecimal population;
        BigDecimal maxPop = BigDecimal.ZERO;
        TreeMap<Long, BigDecimal> femaleAgeInYearsPopulationCount_TreeMap = new TreeMap<Long, BigDecimal>();
        femaleAgeInYearsPopulationCount_TreeMap.put(
                theMinAgeInYears,
                BigDecimal.ZERO);
        femaleAgeInYearsPopulationCount_TreeMap.put(
                theMaxAgeInYears,
                BigDecimal.ZERO);
        TreeMap<Long, BigDecimal> maleAgeInYearsPopulationCount_TreeMap = new TreeMap<Long, BigDecimal>();
        maleAgeInYearsPopulationCount_TreeMap.put(
                theMinAgeInYears,
                BigDecimal.ZERO);
        maleAgeInYearsPopulationCount_TreeMap.put(
                theMaxAgeInYears,
                BigDecimal.ZERO);
        Entry<GENESIS_AgeBound, BigDecimal> entry;
        Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite;
        if (pop._FemaleAgeBoundPopulationCount_TreeMap != null) {
            ite = pop._FemaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
            while (ite.hasNext()) {
                entry = ite.next();
                age = (int) entry.getKey().getAgeMin().getYear();
                population = entry.getValue();
                if (age <= theMinAgeInYears) {
                    BigDecimal min_population = femaleAgeInYearsPopulationCount_TreeMap.get(
                            theMinAgeInYears);
                    population = population.add(min_population);
                } else {
                    if (age >= theMaxAgeInYears) {
                        BigDecimal max_population = femaleAgeInYearsPopulationCount_TreeMap.get(
                                theMaxAgeInYears);
                        population = population.add(max_population);
                    }
                }
                maxPop = maxPop.max(population);
                femaleAgeInYearsPopulationCount_TreeMap.put(
                        age,
                        population);
            }
        }
        if (pop._MaleAgeBoundPopulationCount_TreeMap != null) {
            ite = pop._MaleAgeBoundPopulationCount_TreeMap.entrySet().iterator();
            while (ite.hasNext()) {
                entry = ite.next();
                age = entry.getKey().getAgeMin().getYear();
                population = entry.getValue();
                if (age <= theMinAgeInYears) {
                    BigDecimal min_population = maleAgeInYearsPopulationCount_TreeMap.get(
                            theMinAgeInYears);
                    population = population.add(min_population);
                } else {
                    if (age >= theMaxAgeInYears) {
                        BigDecimal max_population = maleAgeInYearsPopulationCount_TreeMap.get(
                                theMaxAgeInYears);
                        population = population.add(max_population);
                    }
                }
                maxPop = maxPop.max(population);
                maleAgeInYearsPopulationCount_TreeMap.put(
                        age,
                        population);
            }
        }
        data[0] = femaleAgeInYearsPopulationCount_TreeMap;
        data[1] = maleAgeInYearsPopulationCount_TreeMap;
        data[2] = maxPop;
        data[3] = theMaxAgeInYears;
        data[4] = theMinAgeInYears;
        setData(data);
        setStartAgeOfEndYearInterval(((Long) data[3]).intValue() - 1);
    }

    /**
     * @return Object[] result where: result[0]
     * femaleAgeInYearsPopulationCount_TreeMap; result[1] =
     * maleAgeInYearsPopulationCount_TreeMap; result[2] = maxPop; result[3] =
     * maxAge_int; result[3] = minAge_int;
     */
    @Override
    public Object[] getDefaultData() {
        Object[] result = new Object[5];
        Object[] partResult = super.getDefaultData();
        TreeMap<Integer, BigDecimal> femaleAgeInYearsPopulationCount_TreeMap = (TreeMap<Integer, BigDecimal>) partResult[0];
        TreeMap<Integer, BigDecimal> maleAgeInYearsPopulationCount_TreeMap = (TreeMap<Integer, BigDecimal>) partResult[1];
        Integer minFemaleAge = Generic_Collections.getMinKey_Integer(femaleAgeInYearsPopulationCount_TreeMap, Integer.MIN_VALUE);
        Integer minMaleAge = Generic_Collections.getMinKey_Integer(maleAgeInYearsPopulationCount_TreeMap, Integer.MIN_VALUE);
        int minAge = Math.min(minFemaleAge, minMaleAge);
        Integer maxFemaleAge = Generic_Collections.getMaxKey_Integer(femaleAgeInYearsPopulationCount_TreeMap, Integer.MIN_VALUE);
        Integer maxMaleAge = Generic_Collections.getMaxKey_Integer(maleAgeInYearsPopulationCount_TreeMap, Integer.MIN_VALUE);
        int maxAge = Math.max(maxFemaleAge, maxMaleAge);
        result[0] = partResult[0];
        result[1] = partResult[1];
        result[2] = partResult[2];
        result[3] = maxAge;
        result[4] = minAge;
        return result;
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
        BigDecimal maxY = BigDecimal.valueOf((Long) data[3]);
        maxY = maxY.add(BigDecimal.valueOf(getAgeInterval()));
        setMaxY(maxY);
        BigDecimal minY = BigDecimal.valueOf((Long) data[4]);
        setMinY(minY);
        setCellHeight();
        setCellWidth();
        setOriginRow();
        setOriginCol();
    }

    /**
     * Draws the Y axis. Override as due to ensuring a gap between the title and
     * the data by adding ageInterval to maxY there could be an extra unwanted Y
     * value.
     *
     * @param seperationDistanceOfAxisAndData
     * @param partTitleGap
     * @param scaleTickAndTextSeparation
     * @return an int[] result for setting display parameters where: result[0] =
     * yAxisExtraWidthLeft;
     * @TODO Better handle case when yAxisLabel has a text width wider than
     * image is high
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
}
