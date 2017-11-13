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
import uk.ac.leeds.ccg.andyt.projects.genesis.grids.GENESIS_Grids;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.GENESIS_Family;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.GENESIS_Household;
import uk.ac.leeds.ccg.andyt.projects.genesis.transport.GENESIS_Movement;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_LineSegment2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Network2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Network2D.Connection;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

/**
 * Class for representing individual people.
 */
public abstract class GENESIS_Person extends GENESIS_Agent {

    //static final long serialVersionUID = 1L;
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
    public ArrayList<String> ResidentialSubregionIDs;

    protected GENESIS_Person() {
    }

    public GENESIS_Person(GENESIS_Environment ge) {
        super(ge);
    }

    public String getRegionID() {
        return getSubregionID().substring(0, 4);
    }

    public String getSubregionID() {
        String result = ResidentialSubregionIDs.get(ResidentialSubregionIDs.size() - 1);
        if (result == null) {
            int debug = 1;
        }
        return result;
    }

    public String getPreviousRegionID() {
        return getPreviousSubregionID().substring(0, 4);
    }

    public String getPreviousSubregionID() {
        String result = ResidentialSubregionIDs.get(ResidentialSubregionIDs.size() - 2);
        if (result == null) {
            int debug = 1;
        }
        return result;
    }
    /**
     * A unique numerical ID for this
     */
    protected Long ID;
    /*
     * Stores the ID of the main collection this belongs to
     */
    protected Long CollectionID;
    /**
     * Directory for storing information about this. If this is null, it can be
     * retrieved using ID from:
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
    protected GENESIS_Age Age;
    /**
     * A record of the time of death of this person. If this is null then the
     * GENESIS_Person can be assumed not to dead.
     */
    public GENESIS_Time TimeOfDeath;
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
    public Vector_Point2D Location;
    /**
     * Stores a previous location.
     */
    protected Vector_Point2D PreviousPoint2D;
    /**
     * For storing the _Environment._reportingGrid CellID
     */
    public Grids_2D_ID_long _reporting_CellID;
    /**
     * Stores the next network location where the person is heading.
     */
    public Vector_Point2D HeadingLocation;
    /**
     * Stores current GENESIS_Movement of GENESIS_Person from an origin through
     * all network locations to a destination. Might be heavy, so this might be
     * best stored in a File in Directory.
     */
    public GENESIS_Movement Movement;
    /**
     * Stores the persons network along which they have headed. Might be heavy,
     * so this might be best stored in a File in Directory.
     */
    public Vector_Network2D _Network2D;
    /**
     * Stores the persons network along which they have headed. Might be heavy,
     * so this might be best stored in a File in Directory.
     */
    public Vector_Network2D _reporting_VectorNetwork2D;
    /**
     * Stores a reference to the GENESIS_Family of this GENESIS_Person
     */
    public GENESIS_Family _Family;
    /**
     * Stores the GENESIS_Person's household which is information about where
     * and with who the GENESIS_Person resides. Currently each GENESIS_Person
     * has just one household at a time.
     */
    public GENESIS_Household _Household;
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
        this.Age = a_Person.Age;
        this._Death_Point2D = a_Person._Death_Point2D;
        this.TimeOfDeath = a_Person.TimeOfDeath;
        this._Family = a_Person._Family;
        this._personalFertility_BigDecimal = a_Person._personalFertility_BigDecimal;
        this.HeadingLocation = a_Person.HeadingLocation;
        this._Household = a_Person._Household;
        this._HouseholdHistory = a_Person._HouseholdHistory;
        this._personalMortality_BigDecimal = a_Person._personalMortality_BigDecimal;
        this.Movement = a_Person.Movement;
        this._Network2D = a_Person._Network2D;
        this.Location = a_Person.Location;
        this.PreviousPoint2D = a_Person.PreviousPoint2D;
        this._SetOffToWork_0_Time = a_Person._SetOffToWork_0_Time;
        this._SetOffToWork_Time = a_Person._SetOffToWork_Time;
        this._Work_Point2D = a_Person._Work_Point2D;
        this._Work_Time = a_Person._Work_Time;
        this._resourceMax_double = a_Person._resourceMax_double;
        this._resource_double = a_Person._resource_double;
    }

    /**
     * return PreviousPoint2D;
     *
     * @TODO Better to return a copy?
     *
     * @return
     */
    public Vector_Point2D getPreviousPoint2D() {
        return PreviousPoint2D;
    }

    /**
     * @param p
     */
    public void setPreviousPoint2D(Vector_Point2D p) {
        this.PreviousPoint2D = new Vector_Point2D(p);
    }

    public void setTimeOfDeath(GENESIS_Time t) {
        this.TimeOfDeath = new GENESIS_Time(t);
    }

    /**
     *
     */
    public void setMovement(GENESIS_Grids grids) {
        Movement = new GENESIS_Movement(
                ge,
                Location,
                HeadingLocation);
        //_Movement.Route = GENESIS_Movement.getShortStraightNetworkPath();
        double[] origin = Location.to_doubleArray();
        double[] destination = HeadingLocation.to_doubleArray();
        Movement.Route = Movement.getTravellingSalesmanRoute(
                grids,
                origin,
                destination,
                ge._TSMisc);
        if (Movement.Route == null) {
            Movement.Route = Movement.getShortStraightNetworkPath(grids);
        }
        if (Movement.Route.Connections == null) {
            int debug = 1;
        }
        HashSet<Connection> connections;
        connections = (HashSet<Connection>) Movement.Route.Connections.get(Location);
        if (connections == null) {
            if (Location.equals(HeadingLocation)) {
                Movement = null;
            }
        } else {
            Connection connection = (Connection) connections.iterator().next();
            HeadingLocation = connection.Location;
        }
    }

//    public void setMovementLatLon(int a_DecimalPlacePrecision) {
//        GENESIS_Movement = new GENESIS_Movement(
//                _GENESIS_Environment,
//                Location,
//                HeadingLocation);
//        //_Movement.Route = GENESIS_Movement.getShortStraightNetworkPath();
//
//        //Convert from screen coordinates to OSGB
//        double origin_x = ((Location.X.doubleValue() * _GENESIS_Environment._XRange_double) / (double) _GENESIS_Environment._network_Grid2DSquareCellDouble.getNCols(true)) + _GENESIS_Environment._XMin_double;
//        double origin_y = ((Location.Y.doubleValue() * _GENESIS_Environment._YRange_double) / (double) _GENESIS_Environment._network_Grid2DSquareCellDouble.getNRows(true)) + _GENESIS_Environment._YMin_double;
//
//        double[] origin = new double[2];
//        origin[0] = origin_x;
//        origin[1] = origin_y;
//
////        double[] origin = GTMisc.transform_OSGB_To_LatLon(
////                Location.X.doubleValue(),
////                Location.Y.doubleValue());
//        double destination_x = ((HeadingLocation.X.doubleValue() * _GENESIS_Environment._XRange_double) / (double) _GENESIS_Environment._network_Grid2DSquareCellDouble.getNCols(true)) + _GENESIS_Environment._XMin_double;
//        double destination_y = ((HeadingLocation.Y.doubleValue() * _GENESIS_Environment._YRange_double) / (double) _GENESIS_Environment._network_Grid2DSquareCellDouble.getNRows(true)) + _GENESIS_Environment._YMin_double;
//
//        double[] destination = new double[2];
//        destination[0] = destination_x;
//        destination[1] = destination_y;
////        double[] destination = GTMisc.transform_OSGB_To_LatLon(
////                HeadingLocation.X.doubleValue(),
////                HeadingLocation.Y.doubleValue());
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
//        GENESIS_Movement.Route = GENESIS_Movement.getTravellingSalesmanRoute(
//                origin,
//                destination,
//                _GENESIS_Environment._TSMisc,
//                a_DecimalPlacePrecision);
//        if (GENESIS_Movement.Route == null) {
//            GENESIS_Movement.Route = GENESIS_Movement.getShortStraightNetworkPath(a_DecimalPlacePrecision);
//            //int debug = 1;
//        } else {
//            _GENESIS_Environment._AbstractModel._GENESIS_Log.log("hurray found OSM route");
//        }
//        if (GENESIS_Movement.Route.Connections == null) {
//            int debug = 1;
//        }
//        HashSet<Connection> a_Connection_HashSet = (HashSet<Connection>) GENESIS_Movement.Route.Connections.get(Location);
//        if (a_Connection_HashSet == null) {
//            if (Location.equals(HeadingLocation)) {
//                GENESIS_Movement = null;
//            }
//        } else {
//            Connection a_Connection = (Connection) a_Connection_HashSet.iterator().next();
//            HeadingLocation = a_Connection.Location;
//        }
//    }
    public abstract int getGender(boolean handleOutOfMemoryError);

    protected abstract int getGender();

    /**
     * @return Copy of Age.
     *
     */
    public GENESIS_Age getCopyOfAge() {
        return new GENESIS_Age(ge, Age);
    }

    /**
     * @return Age.
     *
     */
    public GENESIS_Age getAge() {
        return this.Age;
    }

    /**
     * @return description of this.
     */
    @Override
    public String toString() {
        String result = "Person: ";
        result += getType();
        result += ", Agent_ID " + getAgentID(true);
        //_String += "Age " + get_AgeInYears_int(_GENESIS_Environment._Calendar);
        result += ", Age in years " + getCopyOfAge().getAgeInYears(ge.Time);
        //_String += "Age " + get_Age_double();
        if (_Birth_Calendar == null) {
            result += ", Year of Birth " + getCopyOfAge().getTimeOfBirth().getYear();
            result += ", Day of Year of Birth " + getCopyOfAge().getTimeOfBirth().getDayOfYear();
        } else {
            result += ", Year of Birth " + _Birth_Calendar.get(Calendar.YEAR);
            result += ", Day of Year of Birth " + _Birth_Calendar.get(Calendar.DAY_OF_YEAR);
        }
        if (TimeOfDeath != null) {
            result += ", Year of Death " + TimeOfDeath.getYear();
            result += ", Day of Year of Death " + TimeOfDeath.getDayOfYear();
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
     * _GENESIS_Environment.Time.getDayOfYear() &TODO deal with leap years.
     * _GENESIS_Environment.Time._DayOfYear
     */
    public boolean getIsBirthday() {
        boolean result = false;
        Integer annualBirthday = getCopyOfAge().getTimeOfBirth().getDayOfYear();
        if (annualBirthday.compareTo(ge.Time.getDayOfYear()) == 0) {
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
            boolean isWorkTime = ge.Time.getSecondOfDay() > a_Work_Time[0].getSecondOfDay()
                    && ge.Time.getSecondOfDay() < a_Work_Time[1].getSecondOfDay();
            return isWorkTime;
        } else {
            boolean isWorkTime = ge.Time.getSecondOfDay() > a_Work_Time[0].getSecondOfDay()
                    || ge.Time.getSecondOfDay() < a_Work_Time[1].getSecondOfDay();
            return isWorkTime;
        }
    }

    /**
     * @return true if it is the time this person is supposed to set off to work
     * (from home)...
     */
    public boolean getIsTimeToSetOfToWork() {
        return ge.Time.getSecondOfDay() > _SetOffToWork_Time.getSecondOfDay();
    }

    /**
     * Convenience method for getting a next connection on a route. This
     * currently does not distinguish between Connection Type
     *
     * @return A next Connection on the Route if there is one and null
     * otherwise.
     */
    public Connection getNextConnectionOnRoute() {
        if (Movement != null) {
            if (Movement.Route != null) {
                if (Movement.Route.Connections != null) {
                    Object value = Movement.Route.Connections.get(Location);
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
            GENESIS_Grids grids,
            BigDecimal halfCellsize,
            BigDecimal tollerance,
            boolean handleOutOfMemoryError) {
        try {
            move(grids, halfCellsize, tollerance);
            if (getGender(handleOutOfMemoryError) == 0) {
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
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (getGender(handleOutOfMemoryError) == 0) {
                    GENESIS_Female a_Female = (GENESIS_Female) this;
                    GENESIS_FemaleCollection a_GENESIS_FemaleCollection = a_Female.get_FemaleCollection(
                            handleOutOfMemoryError);
                    if (ge.AgentEnvironment.get_AgentCollectionManager(ge.HandleOutOfMemoryErrorFalse).swapToFile_FemaleCollectionExcept_Account(a_GENESIS_FemaleCollection,
                            ge.HandleOutOfMemoryErrorFalse) < 1) {
                        ge.swapChunk(ge.HandleOutOfMemoryErrorFalse);
                    }
                    ge.initMemoryReserve(a_GENESIS_FemaleCollection,
                            ge.HandleOutOfMemoryErrorFalse);
                } else {
                    GENESIS_Male a_Male = (GENESIS_Male) this;
                    GENESIS_MaleCollection a_GENESIS_MaleCollection = a_Male.get_MaleCollection(
                            handleOutOfMemoryError);
                    if (ge.AgentEnvironment.get_AgentCollectionManager(ge.HandleOutOfMemoryErrorFalse).swapToFile_MaleCollectionExcept_Account(a_GENESIS_MaleCollection,
                            ge.HandleOutOfMemoryErrorFalse) < 1) {
                        ge.swapChunk(ge.HandleOutOfMemoryErrorFalse);
                    }
                    ge.initMemoryReserve(a_GENESIS_MaleCollection,
                            ge.HandleOutOfMemoryErrorFalse);
                }
                move(grids, halfCellsize, tollerance, handleOutOfMemoryError);
            } else {
                throw e;
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
    protected BigDecimal move(
            GENESIS_Grids grids,
            BigDecimal halfCellsize,
            BigDecimal tollerance) {
        boolean handleOutOfMemoryError = false;
        if (getIsWorkTime()) {
            if (Location.equals(_Work_Point2D)) {
                ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.addToCell(_reporting_CellID,
                        //_GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getRow(Location.Y, _GENESIS_Environment.HandleOutOfMemoryError),
                        //_GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellCol(Location.X, _GENESIS_Environment.HandleOutOfMemoryError),
                        _SpeedDefault_BigDecimal.doubleValue(),
                        ge.HandleOutOfMemoryError);
                _Speed_BigDecimal = BigDecimal.ZERO;
                return BigDecimal.ZERO;
            } else {
                _Speed_BigDecimal = _SpeedDefault_BigDecimal;
                if (Movement == null) {
                    if (_Work_Point2D == null) {
                        int debug = 1;
                    }
                    HeadingLocation = _Work_Point2D;
                    setMovement(grids);
                } else {
                    if (Location.equals(HeadingLocation)) {
                        if (!Movement._Destination_Point2D.equals(_Work_Point2D)) {
                            HeadingLocation = _Work_Point2D;
                            setMovement(grids);
                        }
                    }
                }
                return move(grids, halfCellsize, _Speed_BigDecimal, tollerance, handleOutOfMemoryError);
            }
        } else {
            if (Location.equals(_Household._Point2D)) {
                ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.addToCell(_reporting_CellID,
                        //_GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getRow(Location.Y, _GENESIS_Environment.HandleOutOfMemoryError),
                        //_GENESIS_Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellCol(Location.X, _GENESIS_Environment.HandleOutOfMemoryError),
                        _SpeedDefault_BigDecimal.doubleValue(),
                        ge.HandleOutOfMemoryError);
                _Speed_BigDecimal = BigDecimal.ZERO;
                return BigDecimal.ZERO;
            } else {
                _Speed_BigDecimal = _SpeedDefault_BigDecimal;
                if (Movement == null) {
                    HeadingLocation = _Household._Point2D;
                    setMovement(grids);
                } else {
                    if (Location.equals(HeadingLocation)) {
                        if (!Movement._Destination_Point2D.equals(_Household._Point2D)) {
                            HeadingLocation = _Household._Point2D;
                            setMovement(grids);
                        }
                    }
                }
                return move(grids, halfCellsize, _Speed_BigDecimal, tollerance, handleOutOfMemoryError);
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
            GENESIS_Grids grids,
            BigDecimal halfCellsize,
            BigDecimal distance_BigDecimal,
            BigDecimal tollerance,
            boolean handleOutOfMemoryError) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal tdistance_BigDecimal = new BigDecimal(distance_BigDecimal.toString());
        boolean movementDone = false;
        long networkRow;
        long networkCol;
        long reportingRow;
        long reportingCol;
        BigDecimal distanceToHeading_BigDecimal;
        while (tdistance_BigDecimal.compareTo(BigDecimal.ZERO) == 1 && !movementDone) {
            networkRow = ge._network_Grid2DSquareCellDouble.getRow(Location.Y,
                    ge.HandleOutOfMemoryError);
            networkCol = ge._network_Grid2DSquareCellDouble.getCol(Location.X,
                    ge.HandleOutOfMemoryError);
            reportingRow = ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getRow(Location.Y,
                    ge.HandleOutOfMemoryError);
            reportingCol = ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCol(Location.X,
                    ge.HandleOutOfMemoryError);
            /*
             * If distanceToHeading is less than tdistance_BigDecimal, then get
             * point that will be moved to and use this as basis for
             * intersection.
             */
            distanceToHeading_BigDecimal = Location.getDistance(HeadingLocation,
                    ge._DecimalPlacePrecisionForNetworkCalculations);
            Vector_LineSegment2D a_LineSegment2D;
            if (distanceToHeading_BigDecimal.compareTo(tdistance_BigDecimal) == 1) {
                Vector_Point2D new_Point2D = Movement.getPoint2D(Location,
                        HeadingLocation,
                        result,
                        ge._DecimalPlacePrecisionForNetworkCalculations);
                a_LineSegment2D = new Vector_LineSegment2D(
                        Location,
                        new_Point2D);
            } else {
                a_LineSegment2D = new Vector_LineSegment2D(
                        Location,
                        HeadingLocation);
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
            int cellBoundaryIntersect = grids.getCellBoundaryIntersect(
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
                    if (Location.equals(HeadingLocation)) {
                        if (Location == null) {
                            int debug = 1;
                        }
                        /*
                         * Case 2a)
                         */
                        _Network2D.addToNetwork(Location,
                                PreviousPoint2D);
                        _Network2D.addToNetwork(PreviousPoint2D,
                                Location);
                        PreviousPoint2D = Location;
                        if (Movement == null) {
                            setMovement(grids);
                        }
                        Vector_Point2D destination_Point2D = Movement._Destination_Point2D;
                        if (destination_Point2D != null) {
                            if (Location.equals(Movement._Destination_Point2D)) {
                                /*
                                 * a_Person is at _Destination_Point2D
                                 */
                                distanceToHeading_BigDecimal = BigDecimal.ZERO;
                                movementDone = true;
                                _Speed_BigDecimal = BigDecimal.ZERO;
                            } else {
                                set_Heading_Point2D(
                                        grids,
                                        tdistance_BigDecimal);
                            }
                        } else {
                            set_Heading_Point2D(
                                    grids,
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
                        Location = HeadingLocation;
                        //result = distanceToHeading_BigDecimal;
                        tdistance_BigDecimal = tdistance_BigDecimal.subtract(distanceToHeading_BigDecimal);
                        //distance -= distanceToHeading;
                    }
                } else {
                    /*
                     * Case 1b) 2b) Move a_Person towards heading
                     */
                    Location = Movement.getPoint2D(Location,
                            HeadingLocation,
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
                                Location.Y);
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
                                Location.X,
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
                                Location.Y);
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
                                Location.X,
                                ymax);
                        networkRow++;
                        break;
                }
                BigDecimal distanceTravelledInCell_BigDecimal = Location.getDistance(a_Point2D,
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
                Location = a_Point2D;

                BigDecimal next_x = ge._network_Grid2DSquareCellDouble.getCellXBigDecimal(networkCol, ge.HandleOutOfMemoryError);
                BigDecimal next_y = ge._network_Grid2DSquareCellDouble.getCellYBigDecimal(networkRow, ge.HandleOutOfMemoryError);
                Grids_2D_ID_long next_CellID = ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCellID(ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getRow(next_y, ge.HandleOutOfMemoryError),
                        ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getCol(next_x, ge.HandleOutOfMemoryError),
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
//                GENESIS_Movement nextPartMovement = new GENESIS_Movement(_GENESIS_Environment, Location, newHeading_Point2D);
//                GENESIS_Movement totalMovement = this.GENESIS_Movement;
//                GENESIS_Movement = nextPartMovement;
//                distance -= move1(
//                        _AggregatePopulationDensity_Grid2DSquareCellDouble,
//                        a_DecimalPlacePrecision,
//                        toRoundToX_BigDecimal,
//                        toRoundToY_BigDecimal,
//                        distance);
//                GENESIS_Movement = totalMovement;
            }
        }
        result = tdistance_BigDecimal;
        return result;
    }

    /**
     * Sets a_Person.HeadingLocation using next connection on route. If no
     * further connection on route set new movement.
     *
     * @param distance0_BigDecimal
     */
    public void set_Heading_Point2D(
            GENESIS_Grids grids,
            BigDecimal distance0_BigDecimal) {
        Connection nextConnection = getNextConnectionOnRoute();
        if (nextConnection == null) {
            HeadingLocation = grids.getRandomCellCentroid_Point2D(
                    ge._network_Grid2DSquareCellDouble,
                    ge._AbstractModel.get_Random(0),
                    Location,
                    distance0_BigDecimal,
                    ge._DecimalPlacePrecisionForNetwork,
                    ge._ToRoundToX_BigDecimal,
                    ge._ToRoundToY_BigDecimal,
                    ge.HandleOutOfMemoryError);
            setMovement(grids);
        } else {
            HeadingLocation = nextConnection.Location;
        }
    }

    public boolean isAlive(GENESIS_Time a_Time) {
        if (a_Time.compareTo(getCopyOfAge().getTimeOfBirth()) != -1) {
            if (TimeOfDeath == null) {
                return true;
            } else {
                if (a_Time.compareTo(TimeOfDeath) != 1) {
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
        Logger.getLogger(GENESIS_Log.DefaultLoggerName).log(level, message);
    }
}
