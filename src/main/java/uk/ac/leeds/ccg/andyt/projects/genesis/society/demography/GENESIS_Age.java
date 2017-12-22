/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.CommonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.Time;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;

/**
 *
 * @author geoagdt
 */
public class GENESIS_Age extends Age implements Comparable, Serializable {

    //static final long serialVersionUID = 1L;
    GENESIS_Environment ge;

    protected Time TimeInitialised;

    protected GENESIS_Age() {
    }

    public GENESIS_Age(GENESIS_Environment ge, GENESIS_Age age) {
        this.ge = ge;
        Age newAge = CommonFactory.newAge(age);
        init(newAge);
        this.TimeInitialised = CommonFactory.newTime(age.TimeInitialised);
    }

    public GENESIS_Age(GENESIS_Environment ge, Age age) {
        this.ge = ge;
        init(age);
    }

    public GENESIS_Age(
            GENESIS_Environment ge,
            Time timeOfBirth) {
        this.ge = ge;
        Age newAge = CommonFactory.newAge();
        //CommonFactory.init();
        newAge.setTimeOfBirth(new GENESIS_Time(CommonFactory.newTime(timeOfBirth)));
        init(newAge);
        this.TimeInitialised = CommonFactory.newTime(ge.Time);
    }

    public GENESIS_Age(
            GENESIS_Environment ge,
            Time timeOfBirth,
            Time timeOfBirthMin,
            Time timeOfBirthMax) {
        this.ge = ge;
        Age newAge = CommonFactory.newAge();
        newAge.setTimeOfBirth(new GENESIS_Time(
                CommonFactory.newTime(timeOfBirth)));
        GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(
                CommonFactory.newAgeBound(
                        timeOfBirthMin,
                        timeOfBirthMax));
        newAge.setTimeOfBirthAgeBound(newAgeBound);
        init(newAge);
        this.TimeInitialised = CommonFactory.newTime(ge.Time);
    }

    /**
     * Initialisation done from values in age
     *
     * @param age
     */
    private void init(Age age) {
        this.setAgeInYears(age.getAgeInYears());
        this.setAgeInYearsCalculationTime(age.getAgeInYearsCalculationTime());
        this.setDateOfBirth(age.getDateOfBirth());
        this.setDateOfBirthEstimated(age.isDateOfBirthEstimated());
        this.setDayOfMonthOfBirth(age.getDayOfMonthOfBirth());
        this.setMonthOfYearOfBirth(age.getMonthOfYearOfBirth());
        this.setTimeOfBirth(age.getTimeOfBirth());
        this.setTimeOfBirthAgeBound(age.getTimeOfBirthAgeBound());
    }

    @Override
    public String toString() {
        Time theTimeOfBirth = getTimeOfBirth();
        AgeBound theTimeOfBirthAgeBound = getTimeOfBirthAgeBound();
        String result = "Age("
                + "timeOfBirth(year " + theTimeOfBirth.getYear()
                + ", dayOfYear " + theTimeOfBirth.getDayOfYear()
                + ", second " + theTimeOfBirth.getSecondOfDay() + "); "
                + " timeOfBirthMin " + theTimeOfBirthAgeBound.getAgeMin() + ","
                + " timeOfBirthMax " + theTimeOfBirthAgeBound.getAgeMax() + ")";
        return result;
    }

//    public int getAgeInYears_long(
//            GENESIS_Time a_Time) {
//        long yearsDiff = 0;
//        long aYear = a_Time.getYear();
//        long anchorYear = _anchor_Time.getYear();
//        if (aYear > anchorYear) {
//            yearsDiff = aYear - anchorYear;
//            int aDayOfYear = a_Time.getDayOfYear();
//            int anchorDayOfYear = _anchor_Time.getDayOfYear();
//            if (aDayOfYear < anchorDayOfYear) {
//                yearsDiff--;
//            }
//        } else {
//            if (aYear < anchorYear) {
//                yearsDiff = aYear - anchorYear;
//                int aDayOfYear = a_Time.getDayOfYear();
//                int anchorDayOfYear = _anchor_Time.getDayOfYear();
//                if (aDayOfYear > anchorDayOfYear) {
//                    yearsDiff++;
//                }
//            }
//        }
//        return (int) yearsDiff;
//    }
    public long getAgeInYears(
            GENESIS_Time time) {
        double age_double = getAge_double(time);
        return (long) age_double;
    }

    @Override
    public Long getAgeInYears() {
        long result = getAgeInYears(ge.Time);
        setAgeInYears(result);
        setAgeInYearsCalculationTime(new GENESIS_Time(ge.Time));
        return result;
    }

    /**
     * Returns the Persons Age as a double.
     *
     * @param time
     * @return measure of age (difference between <code>getTimeOfBirth()</code>
     * and a_Time) as a double
     */
    public double getAge_double(GENESIS_Time time) {
        double result;
        int aDayOfYear = 0;
        if (time.getDayOfYear() != null) {
            aDayOfYear = time.getDayOfYear();
        }
        Time t = getTimeOfBirth();
        int birthDayOfYear = 0;
        if (t.getDayOfYear() != null) {
            birthDayOfYear = t.getDayOfYear();
        }
        long aYear = time.getYear();
        long birthYear = t.getYear();
        result = aYear - birthYear;
        double daysInYear = (double) GENESIS_Time.NormalDaysInYear_int;
        if (aDayOfYear < birthDayOfYear) {
            result -= 1.0d;
            result += (daysInYear - (birthDayOfYear - aDayOfYear)) / daysInYear;
        } else {
            result += (aDayOfYear - birthDayOfYear) / daysInYear;
        }
        return result;
    }

    /**
     * @param a_Time
     * @return the Age in days as a long at time a_time. This assumes every year
     * has GENESIS_Time.NormalDaysInYear_int days.
     */
    public long getAgeInDays(GENESIS_Time a_Time) {
        GENESIS_Time birthGENESIS_Time = new GENESIS_Time(getTimeOfBirth());
        return birthGENESIS_Time.getDifferenceInDays_long(a_Time);
    }

//    /**
//     * Prints Persons Age at Current Computer Date using default Calendar
//     */
//    public void print_Age() {
//        print_Age(Calendar.getInstance());
//        //print_Age(_GENESIS_Environment._Calendar);
//    }
//
//    /**
//     * Prints Persons Age at Date given by _Calendar
//     */
//    public void print_Age(Calendar _Calendar) {
//        //Date _TimeOfBirth_Date = _TimeOfBirth_Calendar.getTime();
//        //Date Time = _Time_Calendar.getTime();
//        //_GENESIS_Environment.AbstractModel._GENESIS_Log.log("_Date " + Time + " _TimeOfBirth_Date " + _TimeOfBirth_Date);
//        int _DAY_OF_YEAR = _Calendar.get(Calendar.DAY_OF_YEAR);
//        int _Calendar_Birth_DAY_OF_YEAR = _Birth_Calendar.get(Calendar.DAY_OF_YEAR);
//        int _Age_Day;
//        int _Age_Year = _Calendar.get(Calendar.YEAR) - _Birth_Calendar.get(Calendar.YEAR);
//        if (_DAY_OF_YEAR < _Calendar_Birth_DAY_OF_YEAR) {
//            _Age_Year--;
//            //_Age_Day = _Calendar.getMaximum(Calendar.YEAR) - (_DateOfBirth_DAY_OF_YEAR - _DAY_OF_YEAR);
//            _Age_Day = 365 - (_Calendar_Birth_DAY_OF_YEAR - _DAY_OF_YEAR);
//        } else {
//            _Age_Day = _DAY_OF_YEAR - _Calendar_Birth_DAY_OF_YEAR;
//        }
//        log("_Age_Year " + _Age_Year + " _Age_Day " + _Age_Day);
//    }
//
//    public int get_AgeInYears_int(
//            Calendar _Calendar) {
//        int _Age_Year;
//        int _DAY_OF_YEAR = _Calendar.get(Calendar.DAY_OF_YEAR);
//        if (_Birth_Calendar == null) {
//            _Age_Year = _Calendar.get(Calendar.YEAR) - (int) _GENESIS_Age.getTimeOfBirth().getYear();
//        } else {
//            //Date _TimeOfBirth_Date = _TimeOfBirth_Calendar.getTime();
//            //Date Time = _Time_Calendar.getTime();
//            //_GENESIS_Environment.AbstractModel._GENESIS_Log.log("_Date " + Time + " _TimeOfBirth_Date " + _TimeOfBirth_Date);
//            int _Time_Birth_DAY_OF_YEAR = _Birth_Calendar.get(Calendar.DAY_OF_YEAR);
//            _Age_Year = _Calendar.get(Calendar.YEAR) - _Birth_Calendar.get(Calendar.YEAR);
//            int _Age_Day;
//            if (_DAY_OF_YEAR < _Time_Birth_DAY_OF_YEAR) {
//                _Age_Year--;
//                //_Age_Day = _Time_Calendar.getMaximum(Calendar.YEAR) - (_DateOfBirth_DAY_OF_YEAR - _DAY_OF_YEAR);
//                _Age_Day = 365 - (_Time_Birth_DAY_OF_YEAR - _DAY_OF_YEAR);
//            } else {
//                _Age_Day = _DAY_OF_YEAR - _Time_Birth_DAY_OF_YEAR;
//            }
//            log("_Age_Year " + _Age_Year);
//        }
//        return _Age_Year;
//    }
    public long getAgeInYears_long(
            GENESIS_Time a_Time) {
        long yearsDiff = 0;
        long aYear = a_Time.getYear();
        Time t = getTimeOfBirth();
        long birthYear = t.getYear();
        if (aYear > birthYear) {
            yearsDiff = aYear - birthYear;
            int aDayOfYear = a_Time.getDayOfYear();
            int birthDayOfYear = t.getDayOfYear();
            if (aDayOfYear < birthDayOfYear) {
                yearsDiff--;
            }
        } else {
            if (aYear < birthYear) {
                yearsDiff = aYear - birthYear;
                int aDayOfYear = a_Time.getDayOfYear();
                int birthDayOfYear = t.getDayOfYear();
                if (aDayOfYear > birthDayOfYear) {
                    yearsDiff++;
                }
            }
        }
        return yearsDiff;
    }

//    public int get_AgeInYears_int(
//            GENESIS_Time Time) {
//        double _Age_double = get_Age_double(Time);
//        return (int) _Age_double;
//    }
//
//    public int get_AgeInYears_int() {
//        //return get_AgeInYears_int(_GENESIS_Environment._Calendar);
//        return get_AgeInYears_int(_GENESIS_Environment.Time);
//    }
//    /**
//     * Returns the Persons Age as a double
//     */
//    public double get_Age_double(Calendar _Calendar) {
//        double result;
//        int _DAY_OF_YEAR = _Calendar.get(Calendar.DAY_OF_YEAR);
//        int _Calendar_Birth_DAY_OF_YEAR = _Birth_Calendar.get(Calendar.DAY_OF_YEAR);
//        result = _Calendar.get(Calendar.YEAR) - _Birth_Calendar.get(Calendar.YEAR);
//        double _DaysInYear = (double) GENESIS_Time.NormalDaysInYear_int;
//        if (_DAY_OF_YEAR < _Calendar_Birth_DAY_OF_YEAR) {
//            result -= 1.0d;
//            result += (_DaysInYear - (_Calendar_Birth_DAY_OF_YEAR - _DAY_OF_YEAR)) / _DaysInYear;
//        } else {
//            result += (_DAY_OF_YEAR - _Calendar_Birth_DAY_OF_YEAR) / _DaysInYear;
//        }
//        //_GENESIS_Environment.AbstractModel._GENESIS_Log.log("_Age " + _Age);
//        return result;
//    }
    /**
     * @param a_Time
     * @return measure of age at a_Time
     */
    public Time getAge_Time(GENESIS_Time a_Time) {
        return a_Time.subtract(getTimeOfBirth());
    }

    /**
     * Returns the Persons Age as a double.
     *
     * @return measure of age (difference between <code>_anchorTime</code> and
 _GENESIS_Environment.Time) as a double
     */
    public double getAge_double() {
        return getAge_double(ge.Time);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof GENESIS_Age) {
            GENESIS_Age oGENESIS_Age = (GENESIS_Age) o;
            GENESIS_Time theTimeOfBirth = new GENESIS_Time(getTimeOfBirth());
            GENESIS_Time oTimeOfBirth = new GENESIS_Time(oGENESIS_Age.getTimeOfBirth());
            int result = theTimeOfBirth.compareTo(oTimeOfBirth);
            if (result == 0) {
                AgeBound theTimeOfBirth_AgeBound = getTimeOfBirthAgeBound();
                GENESIS_AgeBound theTimeOfBirth_GENESIS_AgeBound = new GENESIS_AgeBound(theTimeOfBirth_AgeBound);
                AgeBound oTimeOfBirth_AgeBound = getTimeOfBirthAgeBound();
                GENESIS_AgeBound oTimeOfBirth_GENESIS_AgeBound = new GENESIS_AgeBound(oTimeOfBirth_AgeBound);
                result = theTimeOfBirth_GENESIS_AgeBound.compareTo(oTimeOfBirth_GENESIS_AgeBound);
            }
            return result;
        } else {
            System.err.println(
                    "Trying to compare " + this.getClass().getName()
                    + " with " + o.getClass().getName());
        }
        return 1;
    }

    /**
     * Overrides equals in Object
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (o.getClass() != this.getClass())) {
            return false;
        }
        GENESIS_Age oGENESIS_Age = (GENESIS_Age) o;
        return (new GENESIS_Time(this.timeOfBirth) == new GENESIS_Time(oGENESIS_Age.timeOfBirth));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.timeOfBirth != null ? this.timeOfBirth.hashCode() : 0);
        return hash;
    }
}
