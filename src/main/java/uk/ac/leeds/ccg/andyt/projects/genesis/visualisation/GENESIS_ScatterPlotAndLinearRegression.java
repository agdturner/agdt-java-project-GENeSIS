package uk.ac.leeds.ccg.andyt.projects.genesis.visualisation;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import uk.ac.leeds.ccg.andyt.data.Data_BiNumeric;
import uk.ac.leeds.ccg.andyt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.andyt.generic.execution.Generic_Execution;
import uk.ac.leeds.ccg.andyt.generic.visualisation.Generic_Visualisation;
import uk.ac.leeds.ccg.andyt.chart.examples.Chart_ScatterAndLinearRegression;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_AgeBound;

/**
 * An implementation of
 * <code>Generic_ScatterPlotAndLinearRegression<\code>.
 */
public class GENESIS_ScatterPlotAndLinearRegression extends Chart_ScatterAndLinearRegression {

    public File resultsDirectory;
    public GENESIS_Environment _GENESIS_Environment;

    public GENESIS_ScatterPlotAndLinearRegression() {
    }

    public GENESIS_ScatterPlotAndLinearRegression(
            ExecutorService executorService,
            File file,
            String format,
            String title,
            int dataWidth,
            int dataHeight,
            String xAxisLabel,
            String yAxisLabel,
            boolean drawOriginLinesOnPlot,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode roundingMode) {
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
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode);
    }

    public static void main(String[] args) {
        Generic_Visualisation.getHeadlessEnvironment();
        /*
         * Initialise title and File to write image to
         */
        String title;
        File aFile;
        String format = "PNG";
        if (args.length != 2) {
            System.out.println(
                    "Expected 2 args:"
                    + " args[0] title;"
                    + " args[1] File."
                    + " Recieved " + args.length + " args.");
            // Use defaults
            title = "Scatter Plot And Linear Regression";
            System.out.println("Use default title: " + title);
            aFile = new File(
                    new File(System.getProperty("user.dir")),
                    title.replace(" ", "_") + "." + format);
            System.out.println("Use default File: " + aFile.toString());
        } else {
            title = args[0];
            aFile = new File(args[1]);
        }
        int dataWidth = 700;//250;
        int dataHeight = 600;
        String xAxisLabel = "Expected (X)";
        String yAxisLabel = "Observed (Y)";
        //boolean drawOriginLinesOnPlot = true;
        boolean drawOriginLinesOnPlot = false;
        int decimalPlacePrecisionForCalculations = 10;
        int decimalPlacePrecisionForDisplay = 3;
        RoundingMode aRoundingMode = RoundingMode.HALF_UP;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        GENESIS_ScatterPlotAndLinearRegression plot = new GENESIS_ScatterPlotAndLinearRegression(
                executorService,
                aFile,
                format,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                aRoundingMode);
        plot.setData(getDefaultData(drawOriginLinesOnPlot));
        plot.run();
        Generic_Execution.shutdownExecutorService(
                executorService, plot.future, plot);

    }

    public void setData(
            TreeMap<GENESIS_AgeBound, BigDecimal> expected,
            TreeMap<GENESIS_AgeBound, BigDecimal> observed) {
        ArrayList<Data_BiNumeric> theGeneric_XYNumericalData = new ArrayList<>();
//        Set<GENESIS_AgeBound> keys = GENESIS_Collections.getCombined_HashSet_AgeBound(expected.keySet(), observed.keySet());
//        Iterator<GENESIS_AgeBound> ite = keys.iterator();
        Iterator<GENESIS_AgeBound> ite = expected.keySet().iterator();
        GENESIS_AgeBound ageBound;
        BigDecimal exp;
        BigDecimal obs;
        while (ite.hasNext()) {
            ageBound = ite.next();
            exp = expected.get(ageBound);
            if (exp == null) {
                exp = BigDecimal.ZERO;
            }
            obs = observed.get(ageBound);
            if (obs == null) {
                obs = BigDecimal.ZERO;
            }
            Data_BiNumeric point = new Data_BiNumeric(                    exp,                    obs);
            theGeneric_XYNumericalData.add(point);
        }
        setData(theGeneric_XYNumericalData);
    }

    public void setData(ArrayList<Data_BiNumeric> theGeneric_XYNumericalData) {
        Object[] data = new Object[5];
        BigDecimal maxx = BigDecimal.valueOf(Double.MIN_VALUE);
        BigDecimal minx = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal maxy = BigDecimal.valueOf(Double.MIN_VALUE);
        BigDecimal miny = BigDecimal.valueOf(Double.MAX_VALUE);
        Iterator<Data_BiNumeric> ite = theGeneric_XYNumericalData.iterator();
        Data_BiNumeric v;
        BigDecimal x;
        BigDecimal y;
        while (ite.hasNext()) {
            v = ite.next();
            x = v.getX();
            y = v.getY();
            maxx = maxx.max(x);
            minx = minx.min(x);
            maxy = maxy.max(y);
            miny = miny.min(y);
        }

        System.out.println("minx " + minx);
        System.out.println("maxx " + maxx);
        System.out.println("miny " + miny);
        System.out.println("maxy " + maxy);

        data[0] = theGeneric_XYNumericalData;
        data[1] = maxx;
        data[2] = minx;
        data[3] = maxy;
        data[4] = miny;
        setData(data);
    }

    @Override
    public void setOriginCol() {
        setOriginCol(
                minX,
                dataStartCol,
                getCellWidth(),
                getRoundingMode());
    }

    public static int setOriginCol(
            BigDecimal minX,
            int dataStartCol,
            BigDecimal cellWidth,
            RoundingMode roundingMode) {
        int originCol = 0;
        if (minX != null) {
            if (minX.compareTo(BigDecimal.ZERO) == 0) {
                originCol = dataStartCol;
                //originCol = dataStartCol - dataEndCol / 2;
            } else {
                if (cellWidth.compareTo(BigDecimal.ZERO) == 0) {
                    originCol = dataStartCol;
                } else {
                    originCol = Math_BigDecimal.divideRoundIfNecessary(
                            minX,
                            cellWidth,
                            0,
                            roundingMode).intValueExact()
                            + dataStartCol;
                }
            }
        }
        return originCol;
    }
}
