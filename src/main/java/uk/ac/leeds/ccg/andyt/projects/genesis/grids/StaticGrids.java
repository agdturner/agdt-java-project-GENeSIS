/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.grids;

import java.math.BigDecimal;
import java.util.Random;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_LineSegment2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 * A class for holding static methods that might be best pushed into grids...
 */
public class StaticGrids {

    public static Vector_Point2D getNextCentroid_Point2D(
            Vector_Point2D origin_Point2D,
            Vector_Point2D destination_Point2D) {
        Vector_Point2D result = null;
        return result;
    }

    public static Vector_Point2D getCellCentroid_Point2D(
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell,
            Vector_Point2D a_Point2D,
            BigDecimal toRoundToX_BigDecimal,
            BigDecimal toRoundToY_BigDecimal,
            boolean handleOutOfMemoryError) {
        //getRounded_BigDecimal(
        //   BigDecimal toRoundBigDecimal,
        //  BigDecimal toRoundToBigDecimal);
        Vector_Point2D result;
        long row = a_Grid2DSquareCell.getCellRowIndex(
                a_Point2D._y, handleOutOfMemoryError);
        long col = a_Grid2DSquareCell.getCellColIndex(
                a_Point2D._x, handleOutOfMemoryError);
        result = new Vector_Point2D(
                                null,
                a_Grid2DSquareCell.getCellXBigDecimal(
                col, handleOutOfMemoryError),
                a_Grid2DSquareCell.getCellYBigDecimal(
                row, handleOutOfMemoryError),
                toRoundToX_BigDecimal,
                toRoundToY_BigDecimal);
        return result;
    }

    /**
     * @param handleOutOfMemoryError
     * @param distance
     * @param a_Point2D
     * @return a Vector_Point2D within distance of a_Point2D using double
     * precision arithmetic.
     */
    public static Vector_Point2D getRandom_Point2D(
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell,
            Random a_Random,
            Vector_Point2D a_Point2D,
            double distance,
            boolean handleOutOfMemoryError) {
        double xdiff;
        double ydiff;
        double x;
        double y;
        double counter = 0;
        do {
            do {
                xdiff = (0.5d - a_Random.nextDouble()) * distance * 2.0d;
                ydiff = (0.5d - a_Random.nextDouble()) * distance * 2.0d;
                //xdiff = (a_Random.nextDouble() - 0.5d) * distance * 2.0d;
                //ydiff = (a_Random.nextDouble() - 0.5d) * distance * 2.0d;
                counter++;
                if (counter > 1000) {
                    System.out.println("Getting stuck in " + StaticGrids.class.getName() + ".getRandom_Point2D(AbstractGrid2DSquareCell,Random,Point2D,double,boolean )");
                }
            } while (Math.sqrt((Math.pow(xdiff, 2.0d) + Math.pow(ydiff, 2.0d))) >= distance);
            x = a_Point2D._x.doubleValue() + xdiff;
            y = a_Point2D._y.doubleValue() + ydiff;
            if (counter > 1000) {
                System.out.println("Getting stuck in " + StaticGrids.class.getName() + ".getRandom_Point2D(AbstractGrid2DSquareCell,Random,Point2D,double,boolean )");
            }
        } while (!a_Grid2DSquareCell.isInGrid(x, y, handleOutOfMemoryError));
        return new Vector_Point2D(
                a_Point2D._Vector_Environment,
                new BigDecimal(x),
                new BigDecimal(y),
                Math.max(
                a_Point2D.get_DecimalPlacePrecision(),
                a_Point2D.getDefaultDecimalPlacePrecision_int()));
    }

    /**
     * @param handleOutOfMemoryError
     * @param distance_BigDecimal
     * @param a_Point2D
     * @return a Vector_Point2D within distance of a_Point2D using double
     * precision arithmetic.
     */
    public static Vector_Point2D getRandom_Point2D(
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell,
            Random a_Random,
            Vector_Point2D a_Point2D,
            BigDecimal distance_BigDecimal,
            boolean handleOutOfMemoryError) {
        return getRandom_Point2D(
                a_Grid2DSquareCell,
                a_Random,
                a_Point2D,
                distance_BigDecimal.doubleValue(),
                handleOutOfMemoryError);
    }

    /**
     * @param a_Grid2DSquareCell
     * @param a_Random
     * @param decimalPlacePrecision
     * @param handleOutOfMemoryError
     * @param a_DecimalPlacePrecision
     * @param toRoundToY_BigDecimal
     * @param toRoundToX_BigDecimal
     * @return A randomly selected cell centroid in a_Grid2DSquareCell
     */
    public static Vector_Point2D getRandomCellCentroid_Point2D(
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell,
            Random a_Random,
            Vector_Point2D a_Point2D,
            BigDecimal distance_BigDecimal,
            int a_DecimalPlacePrecision,
            BigDecimal toRoundToX_BigDecimal,
            BigDecimal toRoundToY_BigDecimal,
            boolean handleOutOfMemoryError) {
        Vector_Point2D result;
        int counter = 0;
        do {
            Vector_Point2D b_Point2D = getRandom_Point2D(
                    a_Grid2DSquareCell,
                    a_Random,
                    a_Point2D,
                    distance_BigDecimal,
                    handleOutOfMemoryError);
            result = getCellCentroid_Point2D(
                    a_Grid2DSquareCell,
                    b_Point2D,
                    toRoundToX_BigDecimal,
                    toRoundToY_BigDecimal,
                    handleOutOfMemoryError);
            counter++;
            if (counter > 1000) {
                System.out.println("Getting stuck in StaticGrids.getRandomCellCentroid_Point2D(AbstractGrid2DSquareCell,Random,Point2D,BigDecimal,int,boolean)");
            }
        } while (result.getDistance(a_Point2D, a_DecimalPlacePrecision).compareTo(distance_BigDecimal) == 1);
        return result;
    }

    /**
     *
     * @param a_Grid2DSquareCell
     * @param a_Random
     * @param a_DecimalPlacePrecision
     * @param handleOutOfMemoryError
     * @return
     */
    public static Vector_Point2D getRandomCellCentroid_Point2D(
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell,
            Random a_Random,
            int a_DecimalPlacePrecision,
            boolean handleOutOfMemoryError) {
        Vector_Point2D result;
        int counter = 0;
        do {
            long col = a_Grid2DSquareCell.getCellColIndex(a_Random, handleOutOfMemoryError);
            long row = a_Grid2DSquareCell.getCellColIndex(a_Random, handleOutOfMemoryError);
            result = new Vector_Point2D(
                    null,
                    a_Grid2DSquareCell.getCellXBigDecimal(col, handleOutOfMemoryError),
                    a_Grid2DSquareCell.getCellYBigDecimal(row, handleOutOfMemoryError),
                    a_DecimalPlacePrecision);
            counter++;
            if (counter > 1000) {
                System.out.println("Getting stuck in StaticGrids.getRandomCellCentroid_Point2D(AbstractGrid2DSquareCell,Random,int decimalPlacePrecision,boolean");
            }
        } while (!a_Grid2DSquareCell.isInGrid(result._x, result._y, handleOutOfMemoryError));
        return result;
    }

    /**
     * @param a_LineSegment2D
     * @param bounds
     * @param ignore_a_LineSegment2D_Start_Point2D
     * @param a_DecimalPlacePrecision
     * @return 0 if no intersection; +---+---+---+ | 7 | 8 | 1 | +---+---+---+ |
     * 6 | 0 | 2 | +---+---+---+ | 5 | 4 | 3 | +---+---+---+
     */
    public static int getCellBoundaryIntersect(
            Vector_LineSegment2D a_LineSegment2D,
            BigDecimal xmin,
        BigDecimal ymin,
        BigDecimal xmax,
        BigDecimal ymax,
            boolean ignore_a_LineSegment2D_Start_Point2D,
            BigDecimal tollerance,
            int a_DecimalPlacePrecision,
            boolean handleOutOfMemoryError) {
        Vector_LineSegment2D bottom = new Vector_LineSegment2D(
                new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                xmin,
                ymin),
                new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                xmax,
                ymin));
        boolean a_LineSegment2D_intersect_bottom = a_LineSegment2D.getIntersects(
                bottom,
                ignore_a_LineSegment2D_Start_Point2D,
                tollerance,
                a_DecimalPlacePrecision,
                handleOutOfMemoryError);
        if (a_LineSegment2D_intersect_bottom) {
            Vector_LineSegment2D left = new Vector_LineSegment2D(
                    new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                    xmin,
                    ymin),
                    new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                    xmin,
                    ymax));
            boolean a_LineSegment2D_intersect_left = a_LineSegment2D.getIntersects(
                    left,
                    ignore_a_LineSegment2D_Start_Point2D,
                    tollerance,
                    a_DecimalPlacePrecision,
                    handleOutOfMemoryError);
            if (a_LineSegment2D_intersect_left) {
                return 5;
            } else {
                Vector_LineSegment2D right = new Vector_LineSegment2D(
                        new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                        xmax,
                        ymin),
                        new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                        xmax,
                        ymax));
                boolean a_LineSegment2D_intersect_right = a_LineSegment2D.getIntersects(
                        right,
                        ignore_a_LineSegment2D_Start_Point2D,
                        tollerance,
                        a_DecimalPlacePrecision,
                        handleOutOfMemoryError);
                if (a_LineSegment2D_intersect_right) {
                    return 3;
                } else {
                    return 4;
                }
            }
        } else {
            Vector_LineSegment2D top = new Vector_LineSegment2D(
                    new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                    xmin,
                    ymax),
                    new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                    xmax,
                    ymax));
            boolean a_LineSegment2D_intersect_top = a_LineSegment2D.getIntersects(
                    top,
                    ignore_a_LineSegment2D_Start_Point2D,
                    tollerance,
                    a_DecimalPlacePrecision,
                    handleOutOfMemoryError);
            if (a_LineSegment2D_intersect_top) {
                Vector_LineSegment2D left = new Vector_LineSegment2D(
                        new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                        xmin,
                        ymin),
                        new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                        xmin,
                        ymax));
                boolean a_LineSegment2D_intersect_left = a_LineSegment2D.getIntersects(
                        left,
                        ignore_a_LineSegment2D_Start_Point2D,
                        tollerance,
                        a_DecimalPlacePrecision,
                        handleOutOfMemoryError);
                if (a_LineSegment2D_intersect_left) {
                    return 7;
                } else {
                    Vector_LineSegment2D right = new Vector_LineSegment2D(
                            new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                            xmax,
                            ymin),
                            new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                            xmax,
                            ymax));
                    boolean a_LineSegment2D_intersect_right = a_LineSegment2D.getIntersects(
                            right,
                            ignore_a_LineSegment2D_Start_Point2D,
                            tollerance,
                            a_DecimalPlacePrecision,
                            handleOutOfMemoryError);
                    if (a_LineSegment2D_intersect_right) {
                        return 1;
                    } else {
                        return 8;
                    }
                }
            } else {
                Vector_LineSegment2D left = new Vector_LineSegment2D(
                        new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                        xmin,
                        ymin),
                        new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                        xmin,
                        ymax));
                boolean a_LineSegment2D_intersect_left = a_LineSegment2D.getIntersects(
                        left,
                        ignore_a_LineSegment2D_Start_Point2D,
                        tollerance,
                        a_DecimalPlacePrecision,
                        handleOutOfMemoryError);
                if (a_LineSegment2D_intersect_left) {
                    return 6;
                } else {
                    Vector_LineSegment2D right = new Vector_LineSegment2D(
                            new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                            xmax,
                            ymin),
                            new Vector_Point2D(
                        a_LineSegment2D._Vector_Environment,
                            xmax,
                            ymax));
                    boolean a_LineSegment2D_intersect_right = a_LineSegment2D.getIntersects(
                            right,
                            ignore_a_LineSegment2D_Start_Point2D,
                            tollerance,
                            a_DecimalPlacePrecision,
                            handleOutOfMemoryError);
                    if (a_LineSegment2D_intersect_right) {
                        return 2;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }
}
