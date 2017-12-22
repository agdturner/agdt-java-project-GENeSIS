/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.transport;

//import java.lang.Comparable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.ojalgo.function.implementation.BigFunction;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.Route.RoutingStep;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.grids.GENESIS_Grids;
import uk.ac.leeds.ccg.andyt.projects.genesis.travelingsalesman.GENESIS_TravelingSalesman;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Network2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 *
 * @author geoagdt
 */
public class GENESIS_Movement implements Serializable {

    public transient GENESIS_Environment _Environment;
    public Vector_Point2D _Origin_Point2D;
    public Vector_Point2D _Destination_Point2D;
    public Vector_Network2D Route;

    public GENESIS_Movement() {
    }

    public GENESIS_Movement(
            GENESIS_Environment _Environment) {
        this._Environment = _Environment;
    }

    public GENESIS_Movement(
            GENESIS_Movement _Movement) {
        this._Environment = _Movement._Environment;
        this._Origin_Point2D = _Movement._Origin_Point2D;
        this._Destination_Point2D = _Movement._Destination_Point2D;
        this.Route = _Movement.Route;
    }

    public GENESIS_Movement(
            GENESIS_Environment _Environment,
            Vector_Point2D _Origin_Point2D,
            Vector_Point2D _Destination_Point2D) {
        this._Environment = _Environment;
        this._Origin_Point2D = _Origin_Point2D;
        this._Destination_Point2D = _Destination_Point2D;
    }

    @Override
    public boolean equals(Object _Object) {
        if (_Object instanceof GENESIS_Movement) {
            GENESIS_Movement _Object_Movement = (GENESIS_Movement) _Object;
            return _Object_Movement._Origin_Point2D == this._Origin_Point2D
                    && _Object_Movement._Destination_Point2D == this._Destination_Point2D;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this._Origin_Point2D != null ? this._Origin_Point2D.hashCode() : 0);
        hash = 97 * hash + (this._Destination_Point2D != null ? this._Destination_Point2D.hashCode() : 0);
        return hash;
    }

    /**
     * @TODO Precision for calculations....
     * @param a_Point2D
     * @param b_Point2D
     * @param distance
     * @param a_DecimalPlacePrecision
     * @return Vector_Point2D which lies in a straight line in the direction
     * from a_Point2D to b_Point2D and which is distance units from a_Point2D
     */
    public static Vector_Point2D getPoint2D(
            Vector_Point2D a_Point2D,
            Vector_Point2D b_Point2D,
            double distance,
            int a_DecimalPlacePrecision) {
        Vector_Point2D result;
        double angle = a_Point2D.getAngle_double(b_Point2D);
        double xdiff = Math.sin(angle) * distance;
        double ydiff = Math.cos(angle) * distance;
        BigDecimal x = a_Point2D.X.add(new BigDecimal(xdiff));
        BigDecimal y = a_Point2D.Y.add(new BigDecimal(ydiff));
        result = new Vector_Point2D(
                a_Point2D.ve,
                x,
                y,
                a_DecimalPlacePrecision);
        return result;
    }

    /**
     * @param distance_BigDecimal
     * @TODO Precision for calculations....
     * @param a_Point2D
     * @param b_Point2D
     * @param a_DecimalPlacePrecision
     * @return Vector_Point2D which lies in a straight line in the direction
     * from a_Point2D to b_Point2D and which is distance units from a_Point2D
     */
    public static Vector_Point2D getPoint2D(
            Vector_Point2D a_Point2D,
            Vector_Point2D b_Point2D,
            BigDecimal distance_BigDecimal,
            int a_DecimalPlacePrecision) {
        Vector_Point2D result;
        BigDecimal angle = a_Point2D.getAngle_BigDecimal(b_Point2D);
        BigDecimal xdiff
                = BigFunction.SIN.invoke(angle).multiply(distance_BigDecimal);
        BigDecimal ydiff
                = BigFunction.COS.invoke(angle).multiply(distance_BigDecimal);
        BigDecimal x = a_Point2D.X.add(xdiff);
        BigDecimal y = a_Point2D.Y.add(ydiff);
//        double angle = a_Point2D.getAngle_double(b_Point2D);
//        double xdiff = Math.sin(angle) * distance;
//        double ydiff = Math.cos(angle) * distance;
//        BigDecimal x = a_Point2D.X.add(new BigDecimal(xdiff));
//        BigDecimal y = a_Point2D.Y.add(new BigDecimal(ydiff));
        result = new Vector_Point2D(
                a_Point2D.ve,
                x,
                y,
                a_DecimalPlacePrecision);
        return result;
    }

//    public Vector_Point2D getScreen_Point2D(
//            double a_Lat,
//            double a_Lon,
//            int a_DecimalPlacePrecision) {
//        Vector_Point2D result;
//        long a_NCols = _Environment.NetworkGridDouble.getNCols(true);
//        long a_NRows = _Environment.NetworkGridDouble.getNRows(true);
//        double a_x = ((a_Lon - _Environment._XMin_double) * (double) a_NCols) / _Environment._XRange_double;
//        double a_y = ((a_Lat - _Environment._YMin_double) * (double) a_NRows) / _Environment._YRange_double;
//        long a_row = this._Environment.NetworkGridDouble.getRow(a_y, _Environment.HOOME);
//        long a_col = this._Environment.NetworkGridDouble.getRow(a_x, _Environment.HOOME);
//        result = new Vector_Point2D(
//                this._Environment.NetworkGridDouble.getCellXBigDecimal(a_col, _Environment.HOOME),
//                this._Environment.NetworkGridDouble.getCellYBigDecimal(a_row, _Environment.HOOME),
//                a_DecimalPlacePrecision);
//        return result;
//    }
    /**
     * Precision needs looking at!
     *
     * @param grids
     * @param origin
     * @param destination
     * @param a_TSMisc
     * @return
     */
    public Vector_Network2D getTravellingSalesmanRoute(
            GENESIS_Grids grids,
            double[] origin,
            double[] destination,
            GENESIS_TravelingSalesman a_TSMisc) {
        Vector_Network2D result = null;
        Route a_Route = null;
        try {
            a_Route = a_TSMisc.getRoute(
                    origin,
                    destination);
        } catch (Exception a_Exception) {
            a_Exception.printStackTrace();
        }
        if (a_Route != null) {
            result = new Vector_Network2D(this._Environment.ve);
            List<RoutingStep> t_RoutingSteps = a_Route.getRoutingSteps();
            Iterator a_Iterator = t_RoutingSteps.iterator();
            int counter = 0;
            Vector_Point2D start_Point2D;
            Vector_Point2D centroid_Start_Point;
            Vector_Point2D end_Point2D = null;
            Vector_Point2D centroid_End_Point = null;
            while (a_Iterator.hasNext()) {
                RoutingStep a_RoutingStep = (RoutingStep) a_Iterator.next();
                Node start_Node = a_RoutingStep.getStartNode();
                double startLat = start_Node.getLatitude();
                double startLon = start_Node.getLongitude();
                start_Point2D = new Vector_Point2D(
                        this._Environment.ve,
                        startLon,
                        startLat,
                        _Environment.DecimalPlacePrecisionForNetwork);
                // Get Nearest cell centroid of start_Point2D
                centroid_Start_Point = grids.getCellCentroid_Point2D(_Environment.NetworkGridDouble,
                        start_Point2D,
                        _Environment._ToRoundToX_BigDecimal,
                        _Environment._ToRoundToY_BigDecimal);
                if (counter != 0) {
                    result.addToNetwork(
                            end_Point2D,
                            start_Point2D,
                            1);
                    _Origin_Point2D = centroid_Start_Point;
                } else {
                    //need to get people to road to start with...
                    //This should probably not be done here, but prior to this method being called!
                    Vector_Point2D centroid_Origin_Point = grids.getCellCentroid_Point2D(_Environment.NetworkGridDouble,
                            _Origin_Point2D,
                            _Environment._ToRoundToX_BigDecimal,
                            _Environment._ToRoundToY_BigDecimal);
                    GENESIS_Movement getToRoad_Movement = new GENESIS_Movement(
                            _Environment,
                            centroid_Origin_Point,
                            centroid_Start_Point);
                    Vector_Network2D getToRoad_Network2D = getToRoad_Movement.getShortStraightNetworkPath(grids);
                    result.addToNetwork(
                            getToRoad_Network2D,
                            1);
                }
                Node end_Node = a_RoutingStep.getEndNode();
                double endLat = end_Node.getLatitude();
                double endLon = end_Node.getLongitude();
                end_Point2D = new Vector_Point2D(
                        this._Environment.ve,
                        endLon,
                        endLat,
                        _Environment.DecimalPlacePrecisionForNetwork);
                // Get Nearest cell centroid of end_Point2D
                centroid_End_Point = grids.getCellCentroid_Point2D(_Environment.NetworkGridDouble,
                        end_Point2D,
                        _Environment._ToRoundToX_BigDecimal,
                        _Environment._ToRoundToY_BigDecimal);
                GENESIS_Movement a_Movement = new GENESIS_Movement(
                        this._Environment,
                        centroid_Start_Point,
                        centroid_End_Point);
                result.addToNetwork(
                        a_Movement.getShortStraightNetworkPath(grids),
                        1);
                counter++;
            }
            _Destination_Point2D = centroid_End_Point;
//        } else {
//            result = getShortStraightNetworkPath();
        }
        return result;
    }

    /**
     * @return The path between _Origin_Point2D and _Destination_Point2D using
 the grid network in _Environment.NetworkGridDouble
     */
    public Vector_Network2D getShortStraightNetworkPath(
            GENESIS_Grids grids) {
        Vector_Network2D result = null;
        if (_Origin_Point2D == null || _Destination_Point2D == null) {
            return result;
        }
        result = new Vector_Network2D(this._Environment.ve);
        long row = _Environment.NetworkGridDouble.getRow(_Origin_Point2D.Y);
        long col = _Environment.NetworkGridDouble.getCol(_Origin_Point2D.X);
        Vector_Point2D a_Point2D = grids.getCellCentroid_Point2D(this._Environment.NetworkGridDouble,
                _Origin_Point2D,
                _Environment._ToRoundToX_BigDecimal,
                _Environment._ToRoundToY_BigDecimal);
        Vector_Point2D destination_Point2D = grids.getCellCentroid_Point2D(this._Environment.NetworkGridDouble,
                _Destination_Point2D,
                _Environment._ToRoundToX_BigDecimal,
                _Environment._ToRoundToY_BigDecimal);
        Vector_Point2D b_Point2D = destination_Point2D;
        double _PI_By_Eight = Math.PI / 8.0d;
        while (!a_Point2D.equals(destination_Point2D)) {
            double angle = a_Point2D.getAngle_double(destination_Point2D);
            if (angle < _PI_By_Eight) {
                row++;
            } else {
                if (angle < 3.0d * _PI_By_Eight) {
                    row++;
                    col++;
                } else {
                    if (angle < 5.0d * _PI_By_Eight) {
                        col++;
                    } else {
                        if (angle < 7.0d * _PI_By_Eight) {
                            row--;
                            col++;
                        } else {
                            if (angle < 9.0d * _PI_By_Eight) {
                                row--;
                            } else {
                                if (angle < 11.0d * _PI_By_Eight) {
                                    row--;
                                    col--;
                                } else {
                                    if (angle < 13.0d * _PI_By_Eight) {
                                        col--;
                                    } else {
                                        if (angle < 15.0d * _PI_By_Eight) {
                                            row++;
                                            col--;
                                        } else {
                                            row++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            b_Point2D = new Vector_Point2D(
                    this._Environment.ve,
                    _Environment.NetworkGridDouble.getCellXBigDecimal(col),
                    _Environment.NetworkGridDouble.getCellYBigDecimal(row),
                    _Environment._ToRoundToX_BigDecimal,
                    _Environment._ToRoundToY_BigDecimal);
            result.addToNetwork(
                    a_Point2D,
                    b_Point2D,
                    1);
            a_Point2D = b_Point2D;
        }
        return result;
    }

//    /**
//     *
//     * @return The path between _Origin_Point2D and _Destination_Point2D using the
//     * grid network in _Environment.NetworkGridDouble
//     */
//    public Vector_Network2D getShortStraightNetworkPath() {
//        Vector_Network2D result = null;
//        if (_Origin_Point2D == null || _Destination_Point2D == null) {
//            return result;
//        }
//        result = new Vector_Network2D();
//        long row = _Environment.NetworkGridDouble.getRow(_Origin_Point2D.Y, _Environment.HOOME);
//        long col = _Environment.NetworkGridDouble.getCellCol(_Origin_Point2D.X, _Environment.HOOME);
//        Vector_Point2D a_Point2D = _Origin_Point2D;
//        Vector_Point2D b_Point2D;
//        double _PI_By_Eight = Math.PI / 8.0d;
//        while (!a_Point2D.equals(_Destination_Point2D)) {
//            double angle = a_Point2D.getAngle(_Destination_Point2D);
//            if (angle < _PI_By_Eight) {
//                row++;
//            } else {
//                if (angle < 3.0d * _PI_By_Eight) {
//                    row++;
//                    col++;
//                } else {
//                    if (angle < 5.0d * _PI_By_Eight) {
//                        col++;
//                    } else {
//                        if (angle < 7.0d * _PI_By_Eight) {
//                            row--;
//                            col++;
//                        } else {
//                            if (angle < 9.0d * _PI_By_Eight) {
//                                row--;
//                            } else {
//                                if (angle < 11.0d * _PI_By_Eight) {
//                                    row--;
//                                    col--;
//                                } else {
//                                    if (angle < 13.0d * _PI_By_Eight) {
//                                        col--;
//                                    } else {
//                                        if (angle < 15.0d * _PI_By_Eight) {
//                                            row++;
//                                            col--;
//                                        } else {
//                                            row++;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            b_Point2D = new Vector_Point2D(
//                    _Environment.NetworkGridDouble.getCellXBigDecimal(col, _Environment.HOOME),
//                    _Environment.NetworkGridDouble.getCellYBigDecimal(row, _Environment.HOOME));
//            result.addToNetwork(a_Point2D, b_Point2D, 1);
//            a_Point2D = b_Point2D;
//        }
//        return result;
//    }
    //    /**
//     *
//     * @return The path between _Origin_Point2D and _Destination_Point2D using the
//     * grid network in _Environment.NetworkGridDouble
//     */
//    public Network getOptimalPath(Network _Network){
//        _Environment.NetworkGridDouble
//
//    }
    /**
     * This movement is first diagonal then vertical or horizontal.
     *
     * @param origin_Point2D
     * @param _Point2D
     * @param destination_Point2D
     * @param _Environment
     * @return
     */
    public Vector_Point2D getNewPoint2D_0(
            Vector_Point2D origin_Point2D,
            Vector_Point2D destination_Point2D,
            GENESIS_Environment _Environment) {
        boolean HandleOutOfMemoryError = _Environment.HOOME;
        //long _NRows = _Environment.NetworkGridDouble.getNRows(HOOME);
        //long _NCols = _Environment.NetworkGridDouble.getNCols(HOOME);
        long _Point2DRow = _Environment.NetworkGridDouble.getRow(origin_Point2D.Y);
        long _Point2DCol = _Environment.NetworkGridDouble.getCol(origin_Point2D.X);
        long _DestinationRow = _Environment.NetworkGridDouble.getRow(destination_Point2D.Y);
        long _DestinationCol = _Environment.NetworkGridDouble.getCol(destination_Point2D.X);
        int _Movement;
        long rowDiff = _DestinationRow - _Point2DRow;
        if (rowDiff < 0) {
            _Movement = 1;
        } else {
            if (rowDiff > 0) {
                _Movement = 7;
            } else {
                _Movement = 4;
            }

        }
        long colDiff = _DestinationCol - _Point2DCol;
        if (colDiff < 0) {
            _Movement--;
        } else {
            if (colDiff > 0) {
                _Movement++;
            }

        }
        return getNewPoint2D(origin_Point2D, _Movement);
    }

    /**
     * This movement is first horizontal or vertical until destination is on a
     * diagonal.
     *
     * @param origin_Point2D
     * @param _Point2D
     * @param destination_Point2D
     * @param ge
     * @return
     */
    public Vector_Point2D getNewPoint2D_1(
            Vector_Point2D origin_Point2D,
            Vector_Point2D destination_Point2D,
            GENESIS_Environment ge) {
        if (origin_Point2D.equals(destination_Point2D)) {
            return origin_Point2D;
        }
        //long _NRows = _Environment.NetworkGridDouble.getNRows(HOOME);
        //long _NCols = _Environment.NetworkGridDouble.getNCols(HOOME);
        long _Point2DRow = ge.NetworkGridDouble.getRow(origin_Point2D.Y);
        long _Point2DCol = ge.NetworkGridDouble.getCol(origin_Point2D.X);
        long _DestinationRow = ge.NetworkGridDouble.getRow(destination_Point2D.Y);
        long _DestinationCol = ge.NetworkGridDouble.getCol(destination_Point2D.X);
        int _Movement = 4;
        long rowDiff = _DestinationRow - _Point2DRow;
        long colDiff = _DestinationCol - _Point2DCol;

        if (Math.abs(rowDiff) > Math.abs(colDiff)) {
            if (rowDiff < 0) {
                _Movement = 1;
            } else {
                if (rowDiff > 0) {
                    _Movement = 7;
                }

            }
        } else {
            if (Math.abs(rowDiff) < Math.abs(colDiff)) {
                if (colDiff < 0) {
                    _Movement = 3;
                } else {
                    if (colDiff > 0) {
                        _Movement = 5;
                    }

                }
            } else {
                return getNewPoint2D_0(
                        origin_Point2D,
                        destination_Point2D,
                        ge);
            }

        }
        return getNewPoint2D(origin_Point2D, _Movement, ge);
    }

    /**
     * 0 1 2
     * 3 4 5
     * 6 7 8
     *
     * @param _Point2D
     * @param _Movement A value of 4 means no change
     * @return A new Vector_Point2D giving a row and column location relative to
     * _Movement
     */
    public Vector_Point2D getNewPoint2D(
            Vector_Point2D _Point2D,
            int _Movement) {
        Vector_Point2D result;
        long _Point2DRow = _Environment.NetworkGridDouble.getRow(_Point2D.Y);
        long _Point2DCol = _Environment.NetworkGridDouble.getCol(_Point2D.X);
        if (_Movement == 4) {
            return _Point2D;
        }

        long resultRow;
        long resultCol;
        if (_Movement < 3) {
            resultRow = _Point2DRow - 1L;
        } else {
            if (_Movement > 5) {
                resultRow = _Point2DRow + 1L;
            } else {
                resultRow = _Point2DRow;
            }

        }
        if (_Movement == 0 || _Movement == 3 || _Movement == 6) {
            resultCol = _Point2DCol - 1L;
        } else {
            if (_Movement == 2 || _Movement == 5 || _Movement == 8) {
                resultCol = _Point2DCol + 1L;
            } else {
                resultCol = _Point2DCol;
            }

        }
        result = new Vector_Point2D(new Vector_Point2D(
                this._Environment.ve,
                _Environment.NetworkGridDouble.getCellXBigDecimal(resultCol),
                _Environment.NetworkGridDouble.getCellYBigDecimal(resultRow)));
        return result;
    }

    /**
     * @param _MovementConstraint
     * @param _Movement_Counts_HashMap
     * @param _Environment
     * @param _Current_Point2D
     * @param _Eventual_Destination_Point2D
     * @return Vector_Point2D as constrained in the direction getNewPoint2D_0
     */
    public Object[] getConstrainedMovement_0(
            int _MovementConstraint,
            HashMap _Movement_Counts_HashMap,
            GENESIS_Environment _Environment,
            Vector_Point2D _Current_Point2D,
            Vector_Point2D _Eventual_Destination_Point2D) {
        Vector_Point2D _Desired_Point2D = getNewPoint2D_0(
                _Current_Point2D,
                _Eventual_Destination_Point2D,
                _Environment);
        return getConstrainedMovement(
                _MovementConstraint,
                _Movement_Counts_HashMap,
                _Environment,
                _Current_Point2D,
                _Desired_Point2D);

    }

    /**
     * @param _MovementConstraint
     * @param _Movement_Counts_HashMap
     * @param _Environment
     * @param _Current_Point2D
     * @param _Eventual_Destination_Point2D
     * @return Vector_Point2D as constrained in the direction getNewPoint2D_1
     */
    public Object[] getConstrainedMovement_1(
            int _MovementConstraint,
            HashMap _Movement_Counts_HashMap,
            GENESIS_Environment _Environment,
            Vector_Point2D _Current_Point2D,
            Vector_Point2D _Eventual_Destination_Point2D) {
        Vector_Point2D _Desired_Point2D = getNewPoint2D_1(
                _Current_Point2D,
                _Eventual_Destination_Point2D,
                _Environment);
        return getConstrainedMovement(
                _MovementConstraint,
                _Movement_Counts_HashMap,
                _Environment,
                _Current_Point2D,
                _Desired_Point2D);
    }

    public Object[] getConstrainedMovement(
            int _MovementConstraint,
            HashMap _Movement_Counts_HashMap,
            GENESIS_Environment _Environment,
            Vector_Point2D _Current_Point2D,
            Vector_Point2D _Desired_Point2D) {
        Object[] result = new Object[2];
        Vector_Point2D result_Point2D;
        GENESIS_Movement _Movement = new GENESIS_Movement(
                _Environment,
                _Current_Point2D,
                _Desired_Point2D);
        int count;
        if (_Movement_Counts_HashMap.containsKey(_Movement)) {
            count = (Integer) _Movement_Counts_HashMap.get(_Movement);
            if (count == _MovementConstraint) {
                // Maybe consider an alternative route or random movement?
                // Stay still
                result_Point2D = _Current_Point2D;
            } else {
                _Movement_Counts_HashMap.put(_Movement, count++);
                result_Point2D
                        = _Desired_Point2D;
            }

        } else {
            _Movement_Counts_HashMap.put(_Movement, 1);
            result_Point2D
                    = _Desired_Point2D;
        }

        result[0] = result_Point2D;
        result[1] = _Movement_Counts_HashMap;
        return result;
    }

    /**
     * This exhibits wrapping in that an agent moving off the top of a grid
     * appears at the bottom. An agent moving off the right of a grid appears on
     * the left and vicea versa. (The world can be thought of a bit like the
     * surface of a taurus). 0 1 2 3 4 5 6 7 8
     *
     * @param _Point2D
     * @param _Movement A value of 4 means no change
     * @param _Environment
     * @return A new long[] giving a row and column location relative to
     * _Movement
     */
    @Deprecated
    public Vector_Point2D getNewPoint2D(
            Vector_Point2D _Point2D,
            int _Movement,
            GENESIS_Environment _Environment) {
        if (_Movement == 4) {
            return _Point2D;
        }

        Vector_Point2D result;

        long _NRows = _Environment.NetworkGridDouble.getNRows();
        long _NCols = _Environment.NetworkGridDouble.getNCols();
        long _Point2DRow = _Environment.NetworkGridDouble.getRow(_Point2D.Y);
        long _Point2DCol = _Environment.NetworkGridDouble.getCol(_Point2D.X);
        long resultRow;
        long resultCol;

        if (_Movement < 3) {
            if (_Point2DRow > 0) {
                resultRow = _Point2DRow - 1L;
            } else {
                resultRow = _NRows - 1L;
            }

        } else {
            if (_Movement > 5) {
                if (_Point2DRow == _NRows - 1) {
                    resultRow = 0L;
                } else {
                    resultRow = _Point2DRow + 1L;
                }

            } else {
                resultRow = _Point2DRow;
            }

        }
        if (_Movement == 0 || _Movement == 3 || _Movement == 6) {
            if (_Point2DCol > 0) {
                resultCol = _Point2DCol - 1L;
            } else {
                resultCol = _NCols - 1L;
            }

        } else {
            if (_Movement == 2 || _Movement == 5 || _Movement == 8) {
                if (_Point2DCol == _NCols - 1) {
                    resultCol = 0L;
                } else {
                    resultCol = _Point2DCol + 1L;
                }

            } else {
                resultCol = _Point2DCol;
            }

        }
        result = new Vector_Point2D(new Vector_Point2D(
                this._Environment.ve,
                _Environment.NetworkGridDouble.getCellXBigDecimal(resultCol),
                _Environment.NetworkGridDouble.getCellYBigDecimal(resultRow)));
        return result;
    }
}
