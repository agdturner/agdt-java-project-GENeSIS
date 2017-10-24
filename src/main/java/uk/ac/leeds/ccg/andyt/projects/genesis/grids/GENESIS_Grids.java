/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.grids;

import java.math.BigDecimal;
import java.util.Random;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Object;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_LineSegment2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 * A class for holding static methods that might be best pushed into grids...
 */
public class GENESIS_Grids extends GENESIS_Object {

    protected GENESIS_Grids() {
    }

    public GENESIS_Grids(GENESIS_Environment ge) {
        super(ge);
    }

    public static Vector_Point2D getNextCentroid_Point2D(
            Vector_Point2D origin_Point2D,
            Vector_Point2D destination_Point2D) {
        Vector_Point2D result = null;
        return result;
    }

    public Vector_Point2D getCellCentroid_Point2D(
            Grids_AbstractGrid2DSquareCell g,
            Vector_Point2D p,
            BigDecimal toRoundToX_BigDecimal,
            BigDecimal toRoundToY_BigDecimal,
            boolean handleOutOfMemoryError) {
        //getRounded_BigDecimal(
        //   BigDecimal toRoundBigDecimal,
        //  BigDecimal toRoundToBigDecimal);
        Vector_Point2D result;
        long row = g.getCellRowIndex(p.Y, handleOutOfMemoryError);
        long col = g.getCellColIndex(p.X, handleOutOfMemoryError);
        result = new Vector_Point2D(
                null,
                g.getCellXBigDecimal(
                        col, handleOutOfMemoryError),
                g.getCellYBigDecimal(
                        row, handleOutOfMemoryError),
                toRoundToX_BigDecimal,
                toRoundToY_BigDecimal);
        return result;
    }

    /**
     * @param g
     * @param random
     * @param handleOutOfMemoryError
     * @param distance
     * @param p
     * @return a Vector_Point2D within distance of a_Point2D using double
     * precision arithmetic.
     */
    public Vector_Point2D getRandom_Point2D(
            Grids_AbstractGrid2DSquareCell g,
            Random random,
            Vector_Point2D p,
            double distance,
            boolean handleOutOfMemoryError) {
        double xdiff;
        double ydiff;
        double x;
        double y;
        double counter = 0;
        do {
            do {
                xdiff = (0.5d - random.nextDouble()) * distance * 2.0d;
                ydiff = (0.5d - random.nextDouble()) * distance * 2.0d;
                //xdiff = (a_Random.nextDouble() - 0.5d) * distance * 2.0d;
                //ydiff = (a_Random.nextDouble() - 0.5d) * distance * 2.0d;
                counter++;
                if (counter > 1000) {
                    System.out.println("Getting stuck in " + GENESIS_Grids.class.getName() + ".getRandom_Point2D(AbstractGrid2DSquareCell,Random,Point2D,double,boolean )");
                }
            } while (Math.sqrt((Math.pow(xdiff, 2.0d) + Math.pow(ydiff, 2.0d))) >= distance);
            x = p.X.doubleValue() + xdiff;
            y = p.Y.doubleValue() + ydiff;
            if (counter > 1000) {
                System.out.println("Getting stuck in " + GENESIS_Grids.class.getName() + ".getRandom_Point2D(AbstractGrid2DSquareCell,Random,Point2D,double,boolean )");
            }
        } while (!g.isInGrid(x, y, handleOutOfMemoryError));
        return new Vector_Point2D(
                p.ve,
                new BigDecimal(x),
                new BigDecimal(y),
                p.getDecimalPlacePrecision());
    }

    /**
     * @param g
     * @param random
     * @param handleOutOfMemoryError
     * @param distance
     * @param p
     * @return a Vector_Point2D within distance of p using double precision
     * arithmetic.
     */
    public Vector_Point2D getRandom_Point2D(
            Grids_AbstractGrid2DSquareCell g,
            Random random,
            Vector_Point2D p,
            BigDecimal distance,
            boolean handleOutOfMemoryError) {
        return getRandom_Point2D(
                g,
                random,
                p,
                distance.doubleValue(),
                handleOutOfMemoryError);
    }

    /**
     * @param g
     * @param random
     * @param p
     * @param distance
     * @param decimalPlacePrecision
     * @param handleOutOfMemoryError
     * @param toRoundToY_BigDecimal
     * @param toRoundToX_BigDecimal
     * @return A randomly selected cell centroid in a_Grid2DSquareCell
     */
    public Vector_Point2D getRandomCellCentroid_Point2D(
            Grids_AbstractGrid2DSquareCell g,
            Random random,
            Vector_Point2D p,
            BigDecimal distance,
            int decimalPlacePrecision,
            BigDecimal toRoundToX_BigDecimal,
            BigDecimal toRoundToY_BigDecimal,
            boolean handleOutOfMemoryError) {
        Vector_Point2D result;
        int counter = 0;
        do {
            Vector_Point2D pb = getRandom_Point2D(
                    g,
                    random,
                    p,
                    distance,
                    handleOutOfMemoryError);
            result = getCellCentroid_Point2D(
                    g,
                    pb,
                    toRoundToX_BigDecimal,
                    toRoundToY_BigDecimal,
                    handleOutOfMemoryError);
            counter++;
            if (counter > 1000) {
                System.out.println("Stuck in " + this.getClass().getName()
                        + ".getRandomCellCentroid_Point2D("
                        + g.getClass().getName() + ",Random,Point2D,"
                        + "BigDecimal,int,boolean)");
            }
        } while (result.getDistance(p, decimalPlacePrecision).compareTo(distance) == 1);
        return result;
    }

    /**
     *
     * @param g
     * @param random
     * @param decimalPlacePrecision
     * @param handleOutOfMemoryError
     * @return
     */
    public Vector_Point2D getRandomCellCentroid_Point2D(
            Grids_AbstractGrid2DSquareCell g,
            Random random,
            int decimalPlacePrecision,
            boolean handleOutOfMemoryError) {
        Vector_Point2D result;
        int counter = 0;
        do {
            long col = g.getCellColIndex(random, handleOutOfMemoryError);
            long row = g.getCellColIndex(random, handleOutOfMemoryError);
            result = new Vector_Point2D(
                    null,
                    g.getCellXBigDecimal(col, handleOutOfMemoryError),
                    g.getCellYBigDecimal(row, handleOutOfMemoryError),
                    decimalPlacePrecision);
            counter++;
            if (counter > 1000) {
                System.out.println("Stuck in " + this.getClass().getName()
                        + ".getRandomCellCentroid_Point2D("
                        + g.getClass().getName() + ",Random,int,boolean)");
            }
        } while (!g.isInGrid(result.X, result.Y, handleOutOfMemoryError));
        return result;
    }

    /**
     * @param l
     * @param xmin
     * @param ymin
     * @param xmax
     * @param ymax
     * @param ignoreStartPoint
     * @param tollerance
     * @param decimalPlacePrecision
     * @param handleOutOfMemoryError
     * @return 0 if no intersection; +---+---+---+ | 7 | 8 | 1 | +---+---+---+ |
     * 6 | 0 | 2 | +---+---+---+ | 5 | 4 | 3 | +---+---+---+
     */
    public int getCellBoundaryIntersect(
            Vector_LineSegment2D l,
            BigDecimal xmin,
            BigDecimal ymin,
            BigDecimal xmax,
            BigDecimal ymax,
            boolean ignoreStartPoint,
            BigDecimal tollerance,
            int decimalPlacePrecision,
            boolean handleOutOfMemoryError) {
        /*
        * +---+---+---+
        * | 7 | 8 | 1 |
        * +---+---+---+
        * | 6 | 0 | 2 |
        * +---+---+---+
        * | 5 | 4 | 3 |
        * +---+---+---+
        */
        Vector_LineSegment2D bottom = new Vector_LineSegment2D(
                new Vector_Point2D(
                        l.ve,
                        xmin,
                        ymin),
                new Vector_Point2D(
                        l.ve,
                        xmax,
                        ymin));
        boolean l_intersect_bottom = l.getIntersects(
                bottom,
                ignoreStartPoint,
                tollerance,
                decimalPlacePrecision,
                handleOutOfMemoryError);
        if (l_intersect_bottom) {
            Vector_LineSegment2D left = new Vector_LineSegment2D(
                    new Vector_Point2D(
                            l.ve,
                            xmin,
                            ymin),
                    new Vector_Point2D(
                            l.ve,
                            xmin,
                            ymax));
            boolean l_intersect_left = l.getIntersects(
                    left,
                    ignoreStartPoint,
                    tollerance,
                    decimalPlacePrecision,
                    handleOutOfMemoryError);
            if (l_intersect_left) {
                return 5;
            } else {
                Vector_LineSegment2D right = new Vector_LineSegment2D(
                        new Vector_Point2D(
                                l.ve,
                                xmax,
                                ymin),
                        new Vector_Point2D(
                                l.ve,
                                xmax,
                                ymax));
                boolean l_intersect_right = l.getIntersects(
                        right,
                        ignoreStartPoint,
                        tollerance,
                        decimalPlacePrecision,
                        handleOutOfMemoryError);
                if (l_intersect_right) {
                    return 3;
                } else {
                    return 4;
                }
            }
        } else {
            Vector_LineSegment2D top = new Vector_LineSegment2D(
                    new Vector_Point2D(
                            l.ve,
                            xmin,
                            ymax),
                    new Vector_Point2D(
                            l.ve,
                            xmax,
                            ymax));
            boolean l_intersect_top = l.getIntersects(
                    top,
                    ignoreStartPoint,
                    tollerance,
                    decimalPlacePrecision,
                    handleOutOfMemoryError);
            if (l_intersect_top) {
                Vector_LineSegment2D left = new Vector_LineSegment2D(
                        new Vector_Point2D(
                                l.ve,
                                xmin,
                                ymin),
                        new Vector_Point2D(
                                l.ve,
                                xmin,
                                ymax));
                boolean l_intersect_left = l.getIntersects(
                        left,
                        ignoreStartPoint,
                        tollerance,
                        decimalPlacePrecision,
                        handleOutOfMemoryError);
                if (l_intersect_left) {
                    return 7;
                } else {
                    Vector_LineSegment2D right = new Vector_LineSegment2D(
                            new Vector_Point2D(
                                    l.ve,
                                    xmax,
                                    ymin),
                            new Vector_Point2D(
                                    l.ve,
                                    xmax,
                                    ymax));
                    boolean l_intersect_right = l.getIntersects(
                            right,
                            ignoreStartPoint,
                            tollerance,
                            decimalPlacePrecision,
                            handleOutOfMemoryError);
                    if (l_intersect_right) {
                        return 1;
                    } else {
                        return 8;
                    }
                }
            } else {
                Vector_LineSegment2D left = new Vector_LineSegment2D(
                        new Vector_Point2D(
                                l.ve,
                                xmin,
                                ymin),
                        new Vector_Point2D(
                                l.ve,
                                xmin,
                                ymax));
                boolean l_intersect_left = l.getIntersects(
                        left,
                        ignoreStartPoint,
                        tollerance,
                        decimalPlacePrecision,
                        handleOutOfMemoryError);
                if (l_intersect_left) {
                    return 6;
                } else {
                    Vector_LineSegment2D right = new Vector_LineSegment2D(
                            new Vector_Point2D(
                                    l.ve,
                                    xmax,
                                    ymin),
                            new Vector_Point2D(
                                    l.ve,
                                    xmax,
                                    ymax));
                    boolean l_intersect_right = l.getIntersects(
                            right,
                            ignoreStartPoint,
                            tollerance,
                            decimalPlacePrecision,
                            handleOutOfMemoryError);
                    if (l_intersect_right) {
                        return 2;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }
}
