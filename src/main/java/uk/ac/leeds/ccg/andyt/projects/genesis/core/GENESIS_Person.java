package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.projects.genesis.grids.StaticGrids;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.Family;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.Household;
import uk.ac.leeds.ccg.andyt.projects.genesis.transport.Movement;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_LineSegment2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Network2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Network2D.Connection;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 * Class for representing individual people.
 */
public abstract class GENESIS_Person extends GENESIS_Agent {

    static final long serialVersionUID = 1L;
    private static final String TypeLivingFemale_String = "LivingFemale";
    private static final String TypeDeadFemale_String = "DeadFemale";
    private static final String TypeLivingMale_String = "LivingMale";
    private static final String TypeDeadMale_String = "DeadMale";
    /*
     * For storing the place of usual residences. The first in this list is
     * currently the place of usual residence at birth, but this can be stored 
     * from earlier. That earlier information is in the mother record. The last 
     * in this list is either the current or moving to subregionID.
     * @TODO Change to be a collection that records times as well.
     */
    public ArrayList<String> _ResidentialSubregionIDs;

    public String getRegionID() {
        return getSubregionID().substring(0, 4);
    }

    public String getSubregionID() {
        String result = _ResidentialSubregionIDs.get(_ResidentialSubregionIDs.size() - 1);
        if (result == null) {
            int debug = 1;
        }
        return result;
    }

    public String getPreviousRegionID() {
        return getPreviousSubregionID().substring(0, 4);
    }

    public String getPreviousSubregionID() {
        String result = _ResidentialSubregionIDs.get(_ResidentialSubregionIDs.size() - 2);
        if (result == null) {
            int debug = 1;
        }
        return result;
    }
    /**
     * A unique numerical ID for this
     */
    protected Long _ID;
    /*
     * Stores the ID of the main collection this belongs to
     */
    protected Long _Collection_ID;
    /**
     * Directory for storing information about this. If this is null, it can be
     * retrieved using _ID from:
     * _GENESIS_FemaleCollection._Agent_ID_Agent_HashMap
     */
    protected File _Directory;

    /**
     * @return the TypeLivingFemale_String
     */
    public static String getTypeLivingFemale_String() {
        return TypeLivingFemale_String;
    }

    /**
     * @return the TypeDeadFemale_String
     */
    public static String getTypeDeadFemale_String() {
        return TypeDeadFemale_String;
    }

    /**
     * @return the TypeLivingMale_String
     */
    public static String getTypeLivingMale_String() {
        return TypeLivingMale_String;
    }

    /**
     * @return the TypeDeadMale_String
     */
    public static String getTypeDeadMale_String() {
        return TypeDeadMale_String;
    }
    /**
     * A record of the time of birth of this person
     */
    public Calendar _Birth_Calendar;
    /**
     * A record of the time of birth of this person
     */
    protected GENESIS_Age _Age;
    /**
     * A record of the time of death of this person. If this is null then the
     * GENESIS_Person can be assumed not to dead.
     */
    public GENESIS_Time _Death_Time;
    /**
     * Provides the location at which this GENESIS_Person was born. This is
     * probably referred to very infrequently and might best be stored in a
     * File.
     */
    public Vector_Point2D _Birth_Point2D;
    /**
     * Provides the location at which this GENESIS_Person dies/died. As with
     * _Time_Death, this is null if and only if the GENESIS_Person is not dead.
     */
    public Vector_Point2D _Death_Point2D;
    /**
     * Stores the location of the GENESIS_Person.
     */
    public Vector_Point2D _Point2D;
    /**
     * Stores a previous location.
     */
    protected Vector_Point2D _Previous_Point2D;
    /**
     * For storing the _Environment._reportingGrid CellID
     */
    public Grids_2D_ID_long _reporting_CellID;
    /**
     * Stores the next network location where the person is heading.
     */
    public Vector_Point2D _Heading_Point2D;
    /**
     * Stores current Movement of GENESIS_Person from an origin through all
     * network locations to a destination. Might be heavy, so this might be best
 stored in a File in Directory.
     */
    public Movement _Movement;
    /**
     * Stores the persons network along which they have headed. Might be heavy,
 so this might be best stored in a File in Directory.
     */
    public Vector_Network2D _Network2D;
    /**
     * Stores the persons network along which they have headed. Might be heavy,
 so this might be best stored in a File in Directory.
     */
    public Vector_Network2D _reporting_VectorNetwork2D;
    /**
     * Stores a reference to the Family of this GENESIS_Person
     */
    public Family _Family;
    /**
     * Stores the GENESIS_Person's household which is information about where
     * and with who the GENESIS_Person resides. Currently each GENESIS_Person
     * has just one household at a time.
     */
    public Household _Household;
    /**
     * Stores work location. Perhaps need an ordered collection of meetings...
     * This would combine locations and times and perhaps other things, e.g.
     * reference to the other people and things expected to be involved...
     */
    public Vector_Point2D _Work_Point2D;
    /**
     * Stores the _Speed at which a person is moving.
     */
    public BigDecimal _Speed_BigDecimal;
    public double _Speed_double;
    /**
     * Stores the _Speed at which a person moves by default if they are moving.
     */
    public BigDecimal _SpeedDefault_BigDecimal;
    public double _SpeedDefault_double;
    /**
     * If a GENESIS_Person has just one period of work per day _Time_Work should
     * have length == 2. The first time is the start time, the second time is
     * the end time for a period of work.
     */
    public GENESIS_Time[] _Work_Time;
    /**
     * GENESIS_Time set of for work.
     */
    public GENESIS_Time _SetOffToWork_Time;
    /**
     * GENESIS_Time set of for work previously.
     */
    public GENESIS_Time _SetOffToWork_0_Time;
    public Object _HouseholdHistory;
    public BigDecimal _personalFertility_BigDecimal;
    public BigDecimal _personalMortality_BigDecimal;
    /**
     * Stores a measure of how much resource a GENESIS_Person has
     */
    public double _resource_double;
    public double _resourceMax_double;

    public void init(GENESIS_Person a_Person) {
        //super.
        this._Birth_Calendar = a_Person._Birth_Calendar;
        this._Birth_Point2D = a_Person._Birth_Point2D;
        this._Age = a_Person._Age;
        this._Death_Point2D = a_Person._Death_Point2D;
        this._Death_Time = a_Person._Death_Time;
        this._Family = a_Person._Family;
        this._personalFertility_BigDecimal = a_Person._personalFertility_BigDecimal;
        this._Heading_Point2D = a_Person._Heading_Point2D;
        this._Household = a_Person._Household;
        this._HouseholdHistory = a_Person._HouseholdHistory;
        this._personalMortality_BigDecimal = a_Person._personalMortality_BigDecimal;
        this._Movement = a_Person._Movement;
        this._Network2D = a_Person._Network2D;
        this._Point2D = a_Person._Point2D;
        this._Previous_Point2D = a_Person._Previous_Point2D;
        this._SetOffToWork_0_Time = a_Person._SetOffToWork_0_Time;
        this._SetOffToWork_Time = a_Person._SetOffToWork_Time;
        this._Work_Point2D = a_Person._Work_Point2D;
        this._Work_Time = a_Person._Work_Time;
        this._resourceMax_double = a_Person._resourceMax_double;
        this._resource_double = a_Person._resource_double;
    }

    /**
     * return _Previous_Point2D;
     *
     * @TODO Better to return a copy?
     *
     * @return
     */
    public Vector_Point2D get_Previous_Point2D() {
        return _Previous_Point2D;
    }

    /**
     * this._Previous_Point2D = new Vector_Point2D(a_Point2D);
     *
     * @param a_Point2D
     */
    public void set_Previous_Point2D(Vector_Point2D a_Point2D) {
        this._Previous_Point2D = new Vector_Point2D(a_Point2D);
    }

//    public void set_DateOfDeath_Calendar(Calendar _TimeOfDeath_Calendar) {
//        this._TimeOfDeath_Calendar = _TimeOfDeath_Calendar;
//    }
    public void set_Death_Time(GENESIS_Time _Time_Death) {
        this._Death_Time = new GENESIS_Time(_Time_Death);
    }

    /**
     *
     */
    public void setMovement() {
        _Movement = new Movement(
                ge,
                _Point2D,
                _Heading_Point2D);
        //_Movement._networkRoute_VectorNetwork2D = _Movement.getShortStraightNetworkPath();
        double[] origin = _Point2D.to_doubleArray();
        double[] destination = _Heading_Point2D.to_doubleArray();
        _Movement._networkRoute_VectorNetwork2D = _Movement.getTravellingSalesmanRoute(
                origin,
                destination,
                ge._TSMisc);
        if (_Movement._networkRoute_VectorNetwork2D == null) {
            _Movement._networkRoute_VectorNetwork2D = _Movement.getShortStraightNetworkPath();
        }
        if (_Movement._networkRoute_VectorNetwork2D._Connection_HashMap == null) {
            int debug = 1;
        }
        HashSet<Connection> a_Connection_HashSet = (HashSet<Connection>) _Movement._networkRoute_VectorNetwork2D._Connection_HashMap.get(_Point2D);
        if (a_Connection_HashSet == null) {
            if (_Point2D.equals(_Heading_Point2D)) {
                _Movement = null;
            }
        } else {
            Connection a_Connection = (Connection) a_Connection_HashSet.iterator().next();
            _Heading_Point2D = a_Connection._Point2D;
        }
    }

//    public void setMovementLatLon(int a_DecimalPlacePrecision) {
//        _Movement = new Movement(
//                _GENESIS_Environment,
//                _Point2D,
//                _Heading_Point2D);
//        //_Movement._networkRoute_VectorNetwork2D = _Movement.getShortStraightNetworkPath();
//
//        //Convert from screen coordinates to OSGB
//        double origin_x = ((_Point2D._x.doubleValue() * _GENESIS_Environment._XRange_double) / (double) _GENESIS_Environment._network_Grid2DSquareCellDouble.getNCols(true)) + _GENESIS_Environment._XMin_double;
//        double origin_y = ((_Point2D._y.doubleValue() * _GENESIS_Environment._YRange_double) / (double) _GENESIS_Environment._network_Grid2DSquareCellDouble.getNRows(true)) + _GENESIS_Environment._YMin_double;
//
//        double[] origin = new double[2];
//        origin[0] = origin_x;
//        origin[1] = origin_y;
//
////        double[] origin = GTMisc.transform_OSGB_To_LatLon(
////                _Point2D._x.doubleValue(),
////                _Point2D._y.doubleValue());
//        double destination_x = ((_Heading_Point2D._x.doubleValue() * _GENESIS_Environment._XRange_double) / (double) _GENESIS_Environment._network_Grid2DSquareCellDouble.getNCols(true)) + _GENESIS_Environment._XMin_double;
//        double destination_y = ((_Heading_Point2D._y.doubleValue() * _GENESIS_Environment._YRange_double) / (double) _GENESIS_Environment._network_Grid2DSquareCellDouble.getNRows(true)) + _GENESIS_Environment._YMin_double;
//
//        double[] destination = new double[2];
//        destination[0] = destination_x;
//        destination[1] = destination_y;
////        double[] destination = GTMisc.transform_OSGB_To_LatLon(
////                _Heading_Point2D._x.doubleValue(),
////                _Heading_Point2D._y.doubleValue());
////        double[] origin = GTMisc.transform_OSGB_To_LatLon(
////                origin_x,
////                origin_y);
////        if (origin == null) {
////            int debug = 1;
////        }
////        double[] destination = GTMisc.transform_OSGB_To_LatLon(
////                destination_x,
////                destination_y);
////        if (destination == null) {
////            int debug = 1;
////        }
//        _Movement._networkRoute_VectorNetwork2D = _Movement.getTravellingSalesmanRoute(
//                origin,
//                destination,
//                _GENESIS_Environment._TSMisc,
//                a_DecimalPlacePrecision);
//        if (_Movement._networkRoute_VectorNetwork2D == null) {
//            _Movement._networkRoute_VectorNetwork2D = _Movement.getShortStraightNetworkPath(a_DecimalPlacePrecision);
//            //int debug = 1;
//        } else {
//            _GENESIS_Environment._AbstractModel._GENESIS_Log.log("hurray found OSM route");
//        }
//        if (_Movement._networkRoute_VectorNetwork2D._Connection_HashMap == null) {
//            int debug = 1;
//        }
//        HashSet<Connection> a_Connection_HashSet = (HashSet<Connection>) _Movement._networkRoute_VectorNetwork2D._Connection_HashMap.get(_Point2D);
//        if (a_Connection_HashSet == null) {
//            if (_Point2D.equals(_Heading_Point2D)) {
//                _Movement = null;
//            }
//        } else {
//            Connection a_Connection = (Connection) a_Connection_HashSet.iterator().next();
//            _Heading_Point2D = a_Connection._Point2D;
//        }
//    }
    public abstract int get_Gender(boolean handleOutOfMemoryError);

    protected abstract int get_Gender();

    /**
     * @return Copy of _Age.
     *
     */
    public GENESIS_Age getAge() {
        return new GENESIS_Age(this._Age, ge);
    }

    /**
     * @return _Age.
     *
     */
    public GENESIS_Age get_Age() {
        return this._Age;
    }

    /**
     * @return description of this.
     */
    @Override
    public String toString() {
        String result = "Person: ";
        result += getType();
        result += ", Agent_ID " + get_Agent_ID(true);        
        //_String += "Age " + get_AgeInYears_int(_GENESIS_Environment._Calendar);
        result += ", Age in years " + getAge().getAgeInYears(ge._Time);
        //_String += "Age " + get_Age_double();
        if (_Birth_Calendar == null) {
            result += ", Year of Birth " + getAge().getTimeOfBirth().getYear();
            result += ", Day of Year of Birth " + getAge().getTimeOfBirth().getDayOfYear();
        } else {
            result += ", Year of Birth " + _Birth_Calendar.get(Calendar.YEAR);
            result += ", Day of Year of Birth " + _Birth_Calendar.get(Calendar.DAY_OF_YEAR);
        }
        if (_Death_Time != null) {
            result += ", Year of Death " + _Death_Time.getYear();
            result += ", Day of Year of Death " + _Death_Time.getDayOfYear();
        }
        result += ", subregionID " + getSubregionID();
        if (_Work_Point2D != null) {
            result += "; " + _Work_Point2D;
        }
        if (_SetOffToWork_Time != null) {
            result += "; " + _SetOffToWork_Time;
        }
        if (_Work_Time != null) {
            result += "; " + _Work_Time[0];
            result += "; " + _Work_Time[1];
        }
        result += "; " + _Family.toString();
        if (_Household != null) {
            result += "; " + _Household.toString();
        }
        return result;
    }

    /**
     * @return true if _Birth_Time.getDayOfYear() ==
 _GENESIS_Environment._Time.getDayOfYear() &TODO deal with leap years.
 _GENESIS_Environment._Time._DayOfYear
     */
    public boolean getIsBirthday() {
        boolean result = false;
        Integer annualBirthday = getAge().getTimeOfBirth().getDayOfYear();
        if (annualBirthday.compareTo(ge._Time.getDayOfYear()) == 0) {
            result = true;
        }
        return result;
    }

    protected GENESIS_Time[] get_Work_Time() {
        if (_Work_Time == null) {
            _Work_Time = new GENESIS_Time[2];
        }
        if (_Work_Time[0] == null) {
            _Work_Time[0] = new GENESIS_Time();
        }
        if (_Work_Time[1] == null) {
            _Work_Time[1] = new GENESIS_Time();
        }
        return _Work_Time;
    }

    /**
     * Default to false.
     *
     * @return true if this person is supposed to be working...
     */
    protected boolean getIsWorkTime() {
        GENESIS_Time[] a_Work_Time = get_Work_Time();
        // Debug code
        if (ge == null) {
            boolean debug = true;
        }
        if (a_Work_Time[0].getSecondOfDay() < a_Work_Time[1].getSecondOfDay()) {
            boolean isWorkTime = ge._Time.getSecondOfDay() > a_Work_Time[0].getSecondOfDay()
                    && ge._Time.getSecondOfDay() < a_Work_Time[1].getSecondOfDay();
            return isWorkTime;
        } else {
            boolean isWorkTime = ge._Time.getSecondOfDay() > a_Work_Time[0].getSecondOfDay()
                    || ge._Time.getSecondOfDay() < a_Work_Time[1].getSecondOfDay();
            return isWorkTime;
        }
    }

    /**
     * @return true if it is the time this person is supposed to set off to work
     * (from home)...
     */
    public boolean getIsTimeToSetOfToWork() {
        if (ge._Time.getSecondOfDay() > _SetOffToWork_Time.getSecondOfDay()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Convenience method for getting a next connection on a route. This
     * currently does not distinguish between Connection Type
     *
     * @return A next Connection on the _networkRoute_VectorNetwork2D if there
     * is one and null otherwise.
     */
    public Connection getNextConnectionOnRoute() {
        if (_Movement != null) {
            if (_Movement._networkRoute_VectorNetwork2D != null) {
                if (_Movement._networkRoute_VectorNetwork2D._Connection_HashMap != null) {
                    Object value = _Movement._networkRoute_VectorNetwork2D._Connection_HashMap.get(_Point2D);
                    if (value != null) {
                        HashSet<Connection> nextConnections = (HashSet<Connection>) value;
                        if (!nextConnections.isEmpty()) {
                            return nextConnections.iterator().next();
                        }
                    }
                }
            }
        }
        return null;
    }

    public void move(
            BigDecimal tollerance,
            boolean handleOutOfMemoryError) {
        try {
            move(tollerance);
            if (get_Gender(handleOutOfMemoryError) == 0) {
                GENESIS_Female a_Female = (GENESIS_Female) this;
                GENESIS_FemaleCollection a_GENESIS_FemaleCollection = a_Female.get_FemaleCollection(
                        handleOutOfMemoryError);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
            } else {
                GENESIS_Male a_Male = (GENESIS_Male) this;
                GENESIS_MaleCollection a_GENESIS_MaleCollection = a_Male.get_MaleCollection(
                        handleOutOfMemoryError);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        a_GENESIS_MaleCollection,
                        handleOutOfMemoryError);
            }
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (get_Gender(handleOutOfMemoryError) == 0) {
                    GENESIS_Female a_Female = (GENESIS_Female) this;
                    GENESIS_FemaleCollection a_GENESIS_FemaleCollection = a_Female.get_FemaleCollection(
                            handleOutOfMemoryError);
                    if (ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(ge.HandleOutOfMemoryErrorFalse).swapToFile_FemaleCollectionExcept_Account(a_GENESIS_FemaleCollection,
                            ge.HandleOutOfMemoryErrorFalse) < 1) {
                        ge.swapToFile_Grid2DSquareCellChunk(ge.HandleOutOfMemoryErrorFalse);
                    }
                    ge.init_MemoryReserve(a_GENESIS_FemaleCollection,
                            ge.HandleOutOfMemoryErrorFalse);
                } else {
                    GENESIS_Male a_Male = (GENESIS_Male) this;
                    GENESIS_MaleCollection a_GENESIS_MaleCollection = a_Male.get_MaleCollection(
                            handleOutOfMemoryError);
                    if (ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(ge.HandleOutOfMemoryErrorFalse).swapToFile_MaleCollectionExcept_Account(a_GENESIS_MaleCollection,
                            ge.HandleOutOfMemoryErrorFalse) < 1) {
                        ge.swapToFile_Grid2DSquareCellChunk(ge.HandleOutOfMemoryErrorFalse);
                    }
                    ge.init_MemoryReserve(a_GENESIS_MaleCollection,
                            ge.HandleOutOfMemoryErrorFalse);
                }
                move( tollerance,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param tollerance
     * @return 
     * @TODO Speeds
     * @TODO Change distance and speeds to be BigDecimals
     * @TODO Unsafe accounting if out of memory error encountered... Method to
     * call to move this GENESIS_Person. If due and at work location _Speed set
     * to 0. If due and not at work location _Speed set to _SpeedDefault. If not
     * due and at home location _Speed set to 0. If not due and not at home
     * location _Speed set to _SpeedDefault.
     */
    protected BigDecimal move(BigDecimal tollerance) {
        boolean handleOutOfMemoryError = false;
        if (getIsWorkTime()) {
            if (_Point2D.equals(_Work_Point2D)) {
                ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.addToCell(_reporting_CellID,
                        //_GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellRowIndex(_Point2D._y, _GENESIS_Environment.HandleOutOfMemoryError),
                        //_GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellColIndex(_Point2D._x, _GENESIS_Environment.HandleOutOfMemoryError),
                        _SpeedDefault_BigDecimal.doubleValue(),
                        ge.HandleOutOfMemoryError);
                _Speed_BigDecimal = BigDecimal.ZERO;
                return BigDecimal.ZERO;
            } else {
                _Speed_BigDecimal = _SpeedDefault_BigDecimal;
                if (_Movement == null) {
                    if (_Work_Point2D == null) {
                        int debug = 1;
                    }
                    _Heading_Point2D = _Work_Point2D;
                    setMovement();
                } else {
                    if (_Point2D.equals(_Heading_Point2D)) {
                        if (!_Movement._Destination_Point2D.equals(_Work_Point2D)) {
                            _Heading_Point2D = _Work_Point2D;
                            setMovement();
                        }
                    }
                }
                return move(_Speed_BigDecimal, tollerance, handleOutOfMemoryError);
            }
        } else {
            if (_Point2D.equals(_Household._Point2D)) {
                ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.addToCell(_reporting_CellID,
                        //_GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellRowIndex(_Point2D._y, _GENESIS_Environment.HandleOutOfMemoryError),
                        //_GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellColIndex(_Point2D._x, _GENESIS_Environment.HandleOutOfMemoryError),
                        _SpeedDefault_BigDecimal.doubleValue(),
                        ge.HandleOutOfMemoryError);
                _Speed_BigDecimal = BigDecimal.ZERO;
                return BigDecimal.ZERO;
            } else {
                _Speed_BigDecimal = _SpeedDefault_BigDecimal;
                if (_Movement == null) {
                    _Heading_Point2D = _Household._Point2D;
                    setMovement();
                } else {
                    if (_Point2D.equals(_Heading_Point2D)) {
                        if (!_Movement._Destination_Point2D.equals(_Household._Point2D)) {
                            _Heading_Point2D = _Household._Point2D;
                            setMovement();
                        }
                    }
                }
                return move(_Speed_BigDecimal, tollerance, handleOutOfMemoryError);
            }
        }
    }

    /**
     * Attempts to move person a set distance and return it. If destination is
     * reached then the distance to that destination is returned and the
     * movement is halted.
     *
     * @param distance_BigDecimal
     * @param tollerance
     * @param handleOutOfMemoryError
     * @return
     */
    public BigDecimal move(
            BigDecimal distance_BigDecimal,
            BigDecimal tollerance,
            boolean handleOutOfMemoryError) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal tdistance_BigDecimal = new BigDecimal(distance_BigDecimal.toString());
        BigDecimal halfCellsize = ge._network_Grid2DSquareCellDouble.getDimensions(handleOutOfMemoryError).getHalfCellsize();
        boolean movementDone = false;
        long networkRow;
        long networkCol;
        long reportingRow;
        long reportingCol;
        BigDecimal distanceToHeading_BigDecimal;
        while (tdistance_BigDecimal.compareTo(BigDecimal.ZERO) == 1 && !movementDone) {
            networkRow = ge._network_Grid2DSquareCellDouble.getCellRowIndex(_Point2D._y,
                    ge.HandleOutOfMemoryError);
            networkCol = ge._network_Grid2DSquareCellDouble.getCellColIndex(_Point2D._x,
                    ge.HandleOutOfMemoryError);
            reportingRow = ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellRowIndex(_Point2D._y,
                    ge.HandleOutOfMemoryError);
            reportingCol = ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellColIndex(_Point2D._x,
                    ge.HandleOutOfMemoryError);
            /*
             * If distanceToHeading is less than tdistance_BigDecimal, then get
             * point that will be moved to and use this as basis for
             * intersection.
             */
            distanceToHeading_BigDecimal = _Point2D.getDistance(_Heading_Point2D,
                    ge._DecimalPlacePrecisionForNetworkCalculations);
            Vector_LineSegment2D a_LineSegment2D;
            if (distanceToHeading_BigDecimal.compareTo(tdistance_BigDecimal) == 1) {
                Vector_Point2D new_Point2D = Movement.getPoint2D(_Point2D,
                        _Heading_Point2D,
                        result,
                        ge._DecimalPlacePrecisionForNetworkCalculations);
                a_LineSegment2D = new Vector_LineSegment2D(
                        _Point2D,
                        new_Point2D);
            } else {
                a_LineSegment2D = new Vector_LineSegment2D(
                        _Point2D,
                        _Heading_Point2D);
            }
            /**
             * +---+---+---+ | 7 | 8 | 1 | +---+---+---+ | 6 | 0 | 2 |
             * +---+---+---+ | 5 | 4 | 3 | +---+---+---+
             */
            Grids_Dimensions bounds = ge._network_Grid2DSquareCellDouble.getCellDimensions(
                    halfCellsize,
                    networkRow,
                    networkCol,
                    ge.HandleOutOfMemoryError);
            BigDecimal xmin;
        BigDecimal ymin;
        BigDecimal xmax;
        BigDecimal ymax;
        xmin = bounds.getXMin();
        xmax = bounds.getXMax();
        ymin = bounds.getYMin();
        ymax = bounds.getYMax();
            int cellBoundaryIntersect = StaticGrids.getCellBoundaryIntersect(
                    a_LineSegment2D,
                    xmin, ymin, xmax, ymax,
                    true,
                    tollerance,
                    ge._DecimalPlacePrecisionForNetworkCalculations,
                    handleOutOfMemoryError);
            if (cellBoundaryIntersect == 0) {
                /*
                 * In this case the movement is within the current cell. This
                 * can take several forms: 1. The movement is towards the cell
                 * centroid: a) It reaches this destination. b) It does not. 2.
                 * The movement is towards the cell boundary a) Begins at cell
                 * centroid b) Not.
                 */
                if (distanceToHeading_BigDecimal.compareTo(tdistance_BigDecimal) == -1) {
                    if (_Point2D.equals(_Heading_Point2D)) {
                        if (_Point2D == null) {
                            int debug = 1;
                        }
                        /*
                         * Case 2a)
                         */
                        _Network2D.addToNetwork(
                                _Point2D,
                                _Previous_Point2D);
                        _Network2D.addToNetwork(
                                _Previous_Point2D,
                                _Point2D);
                        _Previous_Point2D = _Point2D;
                        if (_Movement == null) {
                            setMovement();
                        }
                        Vector_Point2D destination_Point2D = _Movement._Destination_Point2D;
                        if (destination_Point2D != null) {
                            if (_Point2D.equals(_Movement._Destination_Point2D)) {
                                /*
                                 * a_Person is at _Destination_Point2D
                                 */
                                distanceToHeading_BigDecimal = BigDecimal.ZERO;
                                movementDone = true;
                                _Speed_BigDecimal = BigDecimal.ZERO;
                            } else {
                                set_Heading_Point2D(
                                        tdistance_BigDecimal);
                            }
                        } else {
                            set_Heading_Point2D(
                                    tdistance_BigDecimal);
                        }
                    } else {
                        /*
                         * Case 1a)
                         */
                        ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.addToCell(reportingRow,
                                reportingCol,
                                distanceToHeading_BigDecimal.doubleValue(),
                                ge.HandleOutOfMemoryError);
                        ge._reportingPopulationDensityMovingAggregate_Grid2DSquareCellDouble.addToCell(reportingRow,
                                reportingCol,
                                distanceToHeading_BigDecimal.doubleValue(),
                                ge.HandleOutOfMemoryError);
                        _Point2D = _Heading_Point2D;
                        //result = distanceToHeading_BigDecimal;
                        tdistance_BigDecimal = tdistance_BigDecimal.subtract(distanceToHeading_BigDecimal);
                        //distance -= distanceToHeading;
                    }
                } else {
                    /*
                     * Case 1b) 2b) Move a_Person towards heading
                     */
                    _Point2D = Movement.getPoint2D(_Point2D,
                            _Heading_Point2D,
                            tdistance_BigDecimal,
                            ge._DecimalPlacePrecisionForNetworkCalculations);
                    ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.addToCell(reportingRow,
                            reportingCol,
                            tdistance_BigDecimal.doubleValue(),
                            ge.HandleOutOfMemoryError);
                    ge._reportingPopulationDensityMovingAggregate_Grid2DSquareCellDouble.addToCell(reportingRow,
                            reportingCol,
                            distanceToHeading_BigDecimal.doubleValue(),
                            ge.HandleOutOfMemoryError);
                    tdistance_BigDecimal = BigDecimal.ZERO;
                    movementDone = true;
                    _Speed_BigDecimal = BigDecimal.ZERO;
                }
            } else {
                Vector_Point2D a_Point2D;
                //long networkRow0 = networkRow;
                //long networkCol0 = networkCol;
                switch (cellBoundaryIntersect) {
                    case 1:
                        a_Point2D = new Vector_Point2D(
                                ge.ve,
                                xmax,
                                ymax);
                        networkRow++;
                        networkCol++;
                        break;
                    case 2:
                        a_Point2D = new Vector_Point2D(
                                ge.ve,
                                xmax,
                                _Point2D._y);
                        networkCol++;
                        break;
                    case 3:
                        a_Point2D = new Vector_Point2D(
                                ge.ve,
                                xmax,
                                ymin);
                        networkCol++;
                        networkRow--;
                        break;
                    case 4:
                        a_Point2D = new Vector_Point2D(
                                ge.ve,
                                _Point2D._x,
                                ymin);
                        networkRow--;
                        break;
                    case 5:
                        a_Point2D = new Vector_Point2D(
                                ge.ve,
                                xmin,
                                ymin);
                        networkRow--;
                        networkCol--;
                        break;
                    case 6:
                        a_Point2D = new Vector_Point2D(
                                ge.ve,
                                xmin,
                                _Point2D._y);
                        networkCol--;
                        break;
                    case 7:
                        a_Point2D = new Vector_Point2D(
                                ge.ve,
                                xmin,
                                ymax);
                        networkRow++;
                        networkCol--;
                        break;
                    default:
                        a_Point2D = new Vector_Point2D(
                                ge.ve,
                                _Point2D._x,
                                ymax);
                        networkRow++;
                        break;
                }
                BigDecimal distanceTravelledInCell_BigDecimal = _Point2D.getDistance(a_Point2D,
                        ge._DecimalPlacePrecisionForNetworkCalculations);
                ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.addToCell(_reporting_CellID,
                        //reportingRow,
                        //reportingCol,
                        //distanceToHeading_BigDecimal.doubleValue(),
                        distanceTravelledInCell_BigDecimal.doubleValue(),
                        ge.HandleOutOfMemoryError);
                ge._reportingPopulationDensityMovingAggregate_Grid2DSquareCellDouble.addToCell(_reporting_CellID,
                        //reportingRow,
                        //reportingCol,
                        //distanceToHeading_BigDecimal.doubleValue(),
                        distanceTravelledInCell_BigDecimal.doubleValue(),
                        ge.HandleOutOfMemoryError);
                _Point2D = a_Point2D;

                BigDecimal next_x = ge._network_Grid2DSquareCellDouble.getCellXBigDecimal(networkCol, ge.HandleOutOfMemoryError);
                BigDecimal next_y = ge._network_Grid2DSquareCellDouble.getCellYBigDecimal(networkRow, ge.HandleOutOfMemoryError);
                Grids_2D_ID_long next_CellID = ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellID(ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellRowIndex(next_y, ge.HandleOutOfMemoryError),
                        ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellColIndex(next_x, ge.HandleOutOfMemoryError),
                        ge.HandleOutOfMemoryError);
                if (next_CellID.compareTo(_reporting_CellID) != 0) {
                    Vector_Point2D a_VectorPoint2D = new Vector_Point2D(
                                ge.ve,
                            ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellXBigDecimal(_reporting_CellID, ge.HandleOutOfMemoryError),
                            ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellYBigDecimal(_reporting_CellID, ge.HandleOutOfMemoryError));
                    Vector_Point2D b_VectorPoint2D = new Vector_Point2D(
                                ge.ve,
                            ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellXBigDecimal(next_CellID, ge.HandleOutOfMemoryError),
                            ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellYBigDecimal(next_CellID, ge.HandleOutOfMemoryError));
                    _reporting_VectorNetwork2D.addToNetwork(
                            b_VectorPoint2D,
                            a_VectorPoint2D);
                    _reporting_CellID = next_CellID;
                }
                tdistance_BigDecimal = tdistance_BigDecimal.subtract(distanceTravelledInCell_BigDecimal);

//                Vector_Point2D newHeading_Point2D = new Vector_Point2D(
//                        this._GENESIS_Environment._network_Grid2DSquareCellDouble.getCellXBigDecimal(col, _GENESIS_Environment.HandleOutOfMemoryError),
//                        this._GENESIS_Environment._network_Grid2DSquareCellDouble.getCellYBigDecimal(networkRow, _GENESIS_Environment.HandleOutOfMemoryError));
//                Movement nextPartMovement = new Movement(_GENESIS_Environment, _Point2D, newHeading_Point2D);
//                Movement totalMovement = this._Movement;
//                _Movement = nextPartMovement;
//                distance -= move1(
//                        _AggregatePopulationDensity_Grid2DSquareCellDouble,
//                        a_DecimalPlacePrecision,
//                        toRoundToX_BigDecimal,
//                        toRoundToY_BigDecimal,
//                        distance);
//                _Movement = totalMovement;

            }
        }
        result = tdistance_BigDecimal;
        return result;
    }

    /**
     * Sets a_Person._Heading_Point2D using next connection on route. If no
     * further connection on route set new movement.
     * @param distance0_BigDecimal
     */
    public void set_Heading_Point2D(
            BigDecimal distance0_BigDecimal) {
        Connection nextConnection = getNextConnectionOnRoute();
        if (nextConnection == null) {
            _Heading_Point2D = StaticGrids.getRandomCellCentroid_Point2D(ge._network_Grid2DSquareCellDouble,
                    ge._AbstractModel.get_Random(0),
                    _Point2D,
                    distance0_BigDecimal,
                    ge._DecimalPlacePrecisionForNetwork,
                    ge._ToRoundToX_BigDecimal,
                    ge._ToRoundToY_BigDecimal,
                    ge.HandleOutOfMemoryError);
            setMovement();
        } else {
            _Heading_Point2D = nextConnection._Point2D;
        }
    }

    public boolean isAlive(GENESIS_Time a_Time) {
        if (a_Time.compareTo(getAge().getTimeOfBirth()) != -1) {
            if (_Death_Time == null) {
                return true;
            } else {
                if (a_Time.compareTo(_Death_Time) != 1) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static void log(
            String message) {
        log(GENESIS_Log.GENESIS_DefaultLogLevel, message);
    }

    protected static void log(
            Level level,
            String message) {
        Logger.getLogger(GENESIS_Log.GENESIS_DefaultLoggerName).log(level, message);
    }
}
