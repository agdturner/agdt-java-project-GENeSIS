package uk.ac.leeds.ccg.andyt.projects.genesis.utilities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;
import uk.ac.leeds.ccg.andyt.math.Math_BigInteger;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.Time;

public class GENESIS_Time        extends Time        implements Comparable, Serializable {

    public static final int NormalDaysInYear_int = 365;
    public static final int NormalHoursInDay_int = 24;
    public static final int NormalHoursInYear_int = NormalHoursInDay_int * NormalDaysInYear_int;
    public static final int NormalMinutesInHour_int = 60;
    public static final int NormalMinutesInDay_int = NormalMinutesInHour_int * NormalHoursInDay_int;
    public static final int NormalMinutesInYear_int = NormalMinutesInDay_int * NormalDaysInYear_int;
    public static final int NormalSecondsInMinute_int = 60;
    public static final int NormalSecondsInHour_int = NormalSecondsInMinute_int * NormalMinutesInHour_int;
    public static final int NormalSecondsInDay_int = NormalSecondsInHour_int * NormalHoursInDay_int;
    public static final int NormalSecondsInYear_int = NormalSecondsInDay_int * NormalDaysInYear_int;
    public static final BigInteger NormalDaysInYear_BigInteger = new BigInteger(
            "" + NormalDaysInYear_int);
    public static final BigInteger NormalSecondsInDay_BigInteger = new BigInteger(
            "" + NormalSecondsInDay_int);
    public static final BigInteger NormalSecondsInYear_BigInteger = new BigInteger(
            "" + NormalSecondsInYear_int);

    public GENESIS_Time() {
        setYear(0);
        setDayOfYear(0);
        setSecondOfDay(0);
    }

    public GENESIS_Time(GENESIS_Time t) {
        long tYear = t.getYear();
        setYear(tYear);
        int tDayOfYear = t.getDayOfYear();
        setDayOfYear(tDayOfYear);
        int tSecondOfDay = t.getSecondOfDay();
        setSecondOfDay(tSecondOfDay);
        normalise();
        checkConsistency();
    }

    public GENESIS_Time(
            long year,
            int dayOfYear,
            int secondOfDay) {
        setYear(year);
        setDayOfYear(dayOfYear);
        setSecondOfDay(secondOfDay);
        normalise();
        checkConsistency();
    }

    public GENESIS_Time(
            long year,
            int dayOfYear) {
        setYear(year);
        setDayOfYear(dayOfYear);
        setSecondOfDay(0);
        normalise();
        checkConsistency();
    }

    public GENESIS_Time(
            Time t) {
        long tYear = t.getYear();
        setYear(tYear);
        int tDayOfYear = t.getDayOfYear();
        setDayOfYear(tDayOfYear);
        int tSecondOfDay = t.getSecondOfDay();
        setSecondOfDay(tSecondOfDay);
        normalise();
        checkConsistency();
    }

    public final void checkConsistency() {
        if (getSecondOfDay() < 0
                || getSecondOfDay() >= NormalSecondsInDay_int
                || getDayOfYear() < 0
                || getDayOfYear() >= NormalDaysInYear_int) {
            System.err.println(
                    "System.exit(" + GENESIS_ErrorAndExceptionHandler.TimeInconsistentException + ") "
                    + "Time " + this.toString() + " inconsistent in " + this.getClass().getName());
            System.exit(GENESIS_ErrorAndExceptionHandler.TimeInconsistentException);
        }
    }

    public BigInteger getDayOfYear_BigInteger() {
        return new BigInteger("" + getDayOfYear());
    }

    public BigInteger getSecondOfDay_BigInteger() {
        return new BigInteger("" + getSecondOfDay());
    }

    public BigInteger getYear_BigInteger() {
        return new BigInteger("" + getYear());
    }

    public int getHourOfDay_int() {
        return getSecondOfDay() / NormalSecondsInHour_int;
    }

    public int getMinuteOfHour_int() {
        int hourOfDay = getHourOfDay_int();
        return (getSecondOfDay() - (hourOfDay * NormalSecondsInHour_int)) / NormalSecondsInMinute_int;
    }

    /**
     * This only works where the difference in years is less than
     * Integer.MaxValue.
     *
     * @param a_LowerTimeLimit
     * @param a_UpperTimeLimit
     * @param randomDay
     * @param randomSecond
     * @return A time between a_LowerTimeLimit inclusive and a_UpperTimeLimit
     * exclusive.
     */
    public static GENESIS_Time getRandomTime(
            GENESIS_Time a_LowerTimeLimit,
            GENESIS_Time a_UpperTimeLimit,
            Random randomDay,
            Random randomSecond) {
        // Attempt 4: This only works where the difference in days is < Integer.MaxValue;
        GENESIS_Time result = new GENESIS_Time(a_LowerTimeLimit);
        BigInteger differenceInDays_BigInteger = a_LowerTimeLimit.getDifferenceInDays_BigInteger(
                a_UpperTimeLimit);
        if (differenceInDays_BigInteger.compareTo(Math_BigInteger.INTEGER_MAX_VALUE) == 1
                || differenceInDays_BigInteger.compareTo(BigInteger.ZERO) != 1) {
            throw new UnsupportedOperationException("Trouble at mill!");
        }
        //Math_BigInteger a_Math_BigInteger = new Math_BigInteger();
        int randomDayValue = randomDay.nextInt(differenceInDays_BigInteger.intValue());
        result.addDays(randomDayValue);
        int remainingDifferenceInSeconds_int = (int) (a_UpperTimeLimit.getSecondOfDay() - result.getSecondOfDay());
        //Math_BigInteger a_Math_BigInteger = new Math_BigInteger();
        if (remainingDifferenceInSeconds_int != 0) {
            int randomSecondValue = randomSecond.nextInt(remainingDifferenceInSeconds_int);
            result.addSeconds(randomSecondValue);
        }
        return result;
    }

    public void addSecond() {
        int theSecondOfDay = getSecondOfDay();
        if (theSecondOfDay == NormalSecondsInDay_int - 1) {
            setSecondOfDay(0);
            addDay();
        } else {
            theSecondOfDay += 1;
            setSecondOfDay(theSecondOfDay);
        }
    }

    public void subtractSecond() {
        int theSecondOfDay = getSecondOfDay();
        if (theSecondOfDay == 0) {
            setSecondOfDay(NormalSecondsInDay_int - 1);
            subtractDay();
        } else {
            theSecondOfDay -= 1;
            setSecondOfDay(theSecondOfDay);
        }
    }

    public void addSeconds(long seconds) {
        if (seconds > 0) {
            if (seconds >= NormalSecondsInYear_int) {
                long years = seconds / (long) NormalSecondsInYear_int;
                long yearToSet = years + getYear();
                setYear(yearToSet);
                seconds -= (years * (long) NormalSecondsInYear_int);
                //addSeconds(seconds);
            }
            if (seconds >= NormalSecondsInDay_int) {
                long days = seconds / (long) NormalSecondsInDay_int;
                addDaysExclusivelyBetweenZeroAndNormalDaysInYear(days);
                seconds -= (days * (long) NormalSecondsInDay_int);
            }
            if (seconds >= NormalSecondsInHour_int) {
                long hours = seconds / (long) NormalSecondsInHour_int;
                addHoursExclusivelyBetweenZeroAndNormalHoursInDay(hours);
                seconds -= (hours * (long) NormalSecondsInHour_int);
            }
            if (seconds != 0) {
                addSecondsExclusivelyBetweenZeroAndNormalSecondsInDay(seconds);
            }
        } else {
            if (seconds <= -NormalSecondsInYear_int) {
                long years = seconds / (long) NormalSecondsInYear_int;
                long yearToSet = years + getYear();
                setYear(yearToSet);
                seconds += (years * (long) NormalSecondsInYear_int);
            }
            if (seconds <= -NormalSecondsInDay_int) {
                long days = seconds / (long) NormalSecondsInDay_int;
                addDaysExclusivelyBetweenMinusNormalDaysInYearAndZero(days);
                seconds += (days * NormalSecondsInDay_int);
            }
            if (seconds <= -NormalSecondsInHour_int) {
                long hours = seconds / (long) NormalSecondsInHour_int;
                addHoursExclusivelyBetweenMinusNormalHoursInDayAndZero(hours);
                seconds += (hours * (long) NormalSecondsInHour_int);
            }
            if (seconds != 0) {
                addSecondsExclusivelyBetweenMinusNormalHoursInDayAndZero(seconds);
            }
        }
    }

    public void addSecondsExclusivelyBetweenZeroAndNormalSecondsInDay(long seconds) {
        long aSecondOfDay = getSecondOfDay() + seconds;
        if (aSecondOfDay >= NormalSecondsInDay_int) {
            addDay();
            aSecondOfDay -= NormalSecondsInDay_int;
        }
        setSecondOfDay((int) aSecondOfDay);
    }

    public void addSecondsExclusivelyBetweenMinusNormalHoursInDayAndZero(long seconds) {
        long aSecondOfDay = getSecondOfDay() + seconds;
        if (aSecondOfDay < 0) {
            subtractDay();
            aSecondOfDay += NormalSecondsInDay_int;
        }
        setSecondOfDay((int) aSecondOfDay);
    }

    public void addSeconds(BigInteger seconds) {
        BigInteger a_BigInteger = new BigInteger(seconds.toString());
        while (a_BigInteger.compareTo(Math_BigInteger.LONG_MAX_VALUE) == 1) {
            addSeconds(Math_BigInteger.LONG_MAX_VALUE);
            a_BigInteger = a_BigInteger.subtract(Math_BigInteger.LONG_MAX_VALUE);
        }
        addSeconds(a_BigInteger.longValue());
    }

    public void addMinute() {
        addSeconds(60L);
    }

    public void addMinutes(long minutes) {
        addSeconds(60L * minutes);
    }

    public void addHour() {
        int theSecondOfDay = getSecondOfDay();
        theSecondOfDay += NormalSecondsInHour_int;
        if (theSecondOfDay >= NormalSecondsInDay_int) {
            int theDayOfYear = getDayOfYear() + 1;
            setDayOfYear(theDayOfYear);
            theSecondOfDay -= NormalSecondsInDay_int;
        }
        setSecondOfDay(theSecondOfDay);
    }

    public void addHours(long hours) {
        long theYear = getYear();
        if (hours > 0) {
            if (hours >= NormalHoursInYear_int) {
                long years = hours / NormalHoursInYear_int;
                theYear += years;
                setYear(theYear);
                hours -= (years * NormalHoursInYear_int);
            }
            if (hours >= NormalHoursInDay_int) {
                long days = hours / NormalHoursInDay_int;
                addDaysExclusivelyBetweenZeroAndNormalDaysInYear(days);
                hours -= (days * NormalHoursInDay_int);
            }
            if (hours != 0) {
                addHoursExclusivelyBetweenZeroAndNormalHoursInDay(hours);
            }
        } else {
            if (hours <= -NormalDaysInYear_int) {
                long years = hours / NormalHoursInYear_int;
                theYear -= years;
                setYear(theYear);
                hours += (years * NormalHoursInYear_int);
            }
            if (hours <= -NormalHoursInDay_int) {
                long days = hours / NormalHoursInDay_int;
                addDaysExclusivelyBetweenMinusNormalDaysInYearAndZero(days);
                hours += (days * NormalHoursInDay_int);
            }
            if (hours != 0) {
                addHoursExclusivelyBetweenMinusNormalHoursInDayAndZero(hours);
            }
        }
    }

    public void addHoursExclusivelyBetweenZeroAndNormalHoursInDay(long hours) {
        int theSecondOfDay = getSecondOfDay();
        theSecondOfDay += hours * NormalSecondsInHour_int;
        if (theSecondOfDay >= NormalSecondsInDay_int) {
            addDay();
            theSecondOfDay -= NormalSecondsInDay_int;
        }
        setSecondOfDay(theSecondOfDay);
    }

    public void addHoursExclusivelyBetweenMinusNormalHoursInDayAndZero(long hours) {
        int theSecondOfDay = getSecondOfDay();
        theSecondOfDay += hours * NormalSecondsInHour_int;
        if (theSecondOfDay < 0) {
            subtractDay();
            theSecondOfDay += NormalSecondsInDay_int;
        }
        setSecondOfDay(theSecondOfDay);
    }

    public void addDay() {
        int theDayOfYear = getDayOfYear();
        if (theDayOfYear == NormalDaysInYear_int - 1) {
            theDayOfYear = 0;
            long theYear = getYear() + 1;
            setYear(theYear);
        } else {
            theDayOfYear++;
        }
        setDayOfYear(theDayOfYear);
    }

    public void subtractDay() {
        int theDayOfYear = getDayOfYear();
        if (theDayOfYear == 0) {
            theDayOfYear = NormalDaysInYear_int - 1;
            long theYear = getYear() - 1;
            setYear(theYear);
        } else {
            theDayOfYear--;
        }
        setDayOfYear(theDayOfYear);
    }

    public void addDays(long days) {
        // Special cases
        if (days == 0) {
            return;
        }
        if (days > 0) {
            if (days >= NormalDaysInYear_int) {
                long years = days / NormalDaysInYear_int;
                long theYear = getYear();
                theYear += years;
                setYear(theYear);
                long daysLeft = days - (years * NormalDaysInYear_int);
                if (daysLeft != 0) {
                    addDays(daysLeft);
                }
                //return;
            } else {
                addDaysExclusivelyBetweenZeroAndNormalDaysInYear(days);
            }
        } else {
            if (days <= -NormalDaysInYear_int) {
                long years = days / NormalDaysInYear_int;
                long theYear = getYear();
                theYear += years;
                setYear(theYear);
                long daysLeft = days - (years * NormalDaysInYear_int);
                if (daysLeft != 0) {
                    addDays(daysLeft);
                }
            } else {
                addDaysExclusivelyBetweenMinusNormalDaysInYearAndZero(days);
            }
        }
    }

    public void addDays(BigInteger days) {
        // Special cases
        if (days.compareTo(BigInteger.ZERO) == 0) {
            return;
        }
        if (days.compareTo(Math_BigInteger.LONG_MAX_VALUE) != 1
                && days.compareTo(Math_BigInteger.LONG_MIN_VALUE) != -1) {
            addDays(days.longValue());
            return;
        }
        if (days.compareTo(BigInteger.ZERO) == 1) {
            BigInteger remainingDaysToAdd = new BigInteger(days.toString());
            while (remainingDaysToAdd.compareTo(Math_BigInteger.LONG_MAX_VALUE) == 1) {
                addDays(Math_BigInteger.LONG_MAX_VALUE);
                remainingDaysToAdd = remainingDaysToAdd.subtract(Math_BigInteger.LONG_MAX_VALUE);
            }
            addDays(remainingDaysToAdd);
        } else {
            BigInteger remainingDaysToAdd = new BigInteger(days.toString());
            while (remainingDaysToAdd.compareTo(Math_BigInteger.LONG_MIN_VALUE) == -1) {
                addDays(Math_BigInteger.LONG_MIN_VALUE);
                remainingDaysToAdd = remainingDaysToAdd.add(Math_BigInteger.LONG_MIN_VALUE);
            }
            addDays(remainingDaysToAdd);
        }
    }

    public void addDaysExclusivelyBetweenZeroAndNormalDaysInYear(long days) {
        int theDayOfYear = getDayOfYear();
        theDayOfYear += days;
        if (theDayOfYear >= NormalDaysInYear_int) {
            long theYear = getYear() + 1;
            setYear(theYear);
            theDayOfYear -= NormalDaysInYear_int;
        }
        setDayOfYear(theDayOfYear);
    }

    public void addDaysExclusivelyBetweenMinusNormalDaysInYearAndZero(long days) {
        int theDayOfYear = getDayOfYear();
        theDayOfYear += days;
        if (theDayOfYear < 0) {
            long theYear = getYear() - 1;
            setYear(theYear);
            theDayOfYear += NormalDaysInYear_int;
        }
        setDayOfYear(theDayOfYear);
    }

    public void addYear() {
        long theYear = getYear() + 1;
        setYear(theYear);
    }

    public void addYears(long years) {
        long theYear = getYear() + years;
        setYear(theYear);
    }

    public BigInteger getSecondOfYear_BigInteger() {
        return getDayOfYear_BigInteger().multiply(NormalSecondsInDay_BigInteger).
                add(getSecondOfDay_BigInteger());
    }

    /**
     * Inclusive between
     *
     * @param _Start_Time
     * @param _End_Time
     * @return
     */
    public boolean isBetween(
            GENESIS_Time _Start_Time,
            GENESIS_Time _End_Time) {
        return this.compareTo(_Start_Time) != -1 && this.compareTo(_End_Time) != 1;
    }

    public GENESIS_Time subtract(Time a_Time) {
        int secondDiff = getSecondOfDay() - a_Time.getSecondOfDay();
        int dayDiff = getDayOfYear() - a_Time.getDayOfYear();
        long yearDiff = getYear() - a_Time.getYear();
        GENESIS_Time result = new GENESIS_Time(yearDiff, dayDiff, secondDiff);
        GENESIS_Time.normalise(result);
        return result;
    }

//     /**
//      * Essentially the same as a_Time.substract(this)
//      * @param a_Time
//      * @return 
//      */
//     public BigInteger getDifferenceInDays_BigInteger(GENESIS_Time a_Time) {
//        BigInteger result;
//        long theYear = getYear();
//        long aYear = a_Time.getYear();
//        long yearDifference = aYear - theYear;
//        result = BigInteger.valueOf(yearDifference).multiply(NormalDaysInYear_BigInteger);
//        int theDayOfYear = getDayOfYear();
//        int aDayOfYear = a_Time.getDayOfYear();
//        result = result.add(BigInteger.valueOf(aDayOfYear - theDayOfYear));
//        return result;
//    }
    public BigInteger getDifferenceInDays_BigInteger(GENESIS_Time a_Time) {
        if (a_Time.compareTo(this) == -1) {
            throw new IllegalArgumentException();
        }
        BigInteger result;
        GENESIS_Time difference = a_Time.subtract(this);
        result = difference.getYear_BigInteger().multiply(NormalDaysInYear_BigInteger);
        result = result.add(difference.getDayOfYear_BigInteger());
        return result;
    }

    /**
     * @TODO Add test to see if number in range...
     *
     * @param a_Time
     * @return
     */
    public long getDifferenceInDays_long(GENESIS_Time a_Time) {
        if (a_Time.compareTo(this) == -1) {
            throw new IllegalArgumentException();
        }
        long result;
        BigInteger differenceInDays_BigInteger = getDifferenceInDays_BigInteger(a_Time);
        result = differenceInDays_BigInteger.longValue();
        return result;
    }

    public BigInteger getDifferenceInSeconds_BigInteger(GENESIS_Time a_Time) {
        if (a_Time.compareTo(this) == -1) {
            throw new IllegalArgumentException();
        }
        BigInteger result;
        GENESIS_Time difference = a_Time.subtract(this);
        result = difference.getYear_BigInteger().multiply(NormalSecondsInYear_BigInteger);
        result = result.add(difference.getDayOfYear_BigInteger().multiply(NormalSecondsInDay_BigInteger));
        result = result.add(difference.getSecondOfDay_BigInteger());
        return result;
    }

    /**
     * @TODO Add test to see if number in range...
     *
     * @param a_Time
     * @return
     */
    public long getDifferenceInSeconds_long(GENESIS_Time a_Time) {
        if (a_Time.compareTo(this) == -1) {
            throw new IllegalArgumentException();
        }
        long result;
        BigInteger differenceInSeconds_BigInteger = getDifferenceInSeconds_BigInteger(a_Time);
        result = differenceInSeconds_BigInteger.longValue();
        return result;
    }

//    public GENESIS_Time subtract(GENESIS_Time a_Time) {
//        Time timeDiff = this.getDifference(a_Time);
//        return new GENESIS_Time(timeDiff);
////Alternative 0
////        GENESIS_Time result = new GENESIS_Time(this);
////        result.addSeconds(-a_Time._SecondOfDay);
////        result.addDays(-a_Time._DayOfYear);
////        result.addYears(-a_Time._Year);
////        return result;
////Alternative 1        
////        GENESIS_Time result = new GENESIS_Time();
////        result.setSecondOfDay(this.getSecondOfDay() - a_Time.getSecondOfDay());
////        result.setDayOfYear(this.getDayOfYear() - a_Time.getDayOfYear());
////        result.setYear(this.getYear() - a_Time.getYear());
////        normalise(result);
////        return result;
//    }
    /**
     * &TODO Leap years and leap seconds may cause issues. Check and confirm.
     *
     * @param a_Time
     * @return
     */
    public static boolean isNormalised(GENESIS_Time a_Time) {
        int aSecondOfDay = a_Time.getSecondOfDay();
        int aDayOfYear = a_Time.getDayOfYear();
        return aSecondOfDay >= 0
                && aSecondOfDay < NormalSecondsInDay_int
                && aDayOfYear >= 0
                && aDayOfYear < NormalDaysInYear_int;
    }

    public boolean isNormalised() {
        return isNormalised(this);
    }

    public final void normalise() {
        normalise(this);
    }

    public static void normalise(GENESIS_Time a_Time) {
        while (!isNormalised(a_Time)) {
            int aSecondOfDay = a_Time.getSecondOfDay();
            int aDayOfYear = a_Time.getDayOfYear();
            // Normalise seconds
            if (aSecondOfDay < 0) {
                aDayOfYear++;
                a_Time.setDayOfYear(aDayOfYear);
                aSecondOfDay += NormalSecondsInDay_int;
                a_Time.setSecondOfDay(aSecondOfDay);
            } else {
                if (aSecondOfDay >= NormalSecondsInDay_int) {
                    aDayOfYear--;
                    a_Time.setDayOfYear(aDayOfYear);
                    aSecondOfDay -= NormalSecondsInDay_int;
                    a_Time.setSecondOfDay(aSecondOfDay);
                }
            }
            // Normalise days
            if (aDayOfYear < 0) {
                long aYear = a_Time.getYear();
                aYear--;
                a_Time.setYear(aYear);
                aDayOfYear += NormalDaysInYear_int;
                a_Time.setDayOfYear(aDayOfYear);
            } else {
                if (aDayOfYear >= NormalDaysInYear_int) {
                    long aYear = a_Time.getYear();
                    aYear++;
                    a_Time.setYear(aYear);
                    aDayOfYear -= NormalDaysInYear_int;
                    a_Time.setDayOfYear(aDayOfYear);
                }
            }
        }
    }

    /**
     * @param aGENESIS_Time
     * @TODO Time additions are weak
     * @return
     */
    public GENESIS_Time add(GENESIS_Time aGENESIS_Time) {
//        GENESIS_Time result = new GENESIS_Time(this);
//        result.addSeconds(oGENESIS_Time._SecondOfDay);
//        result.addDays(oGENESIS_Time._DayOfYear);
//        result.addYears(oGENESIS_Time._Year);
        GENESIS_Time result = new GENESIS_Time();
        int aSecondOfDay = this.getSecondOfDay() + aGENESIS_Time.getSecondOfDay();
        result.setSecondOfDay(aSecondOfDay);
        int aDayOfYear = this.getDayOfYear() + aGENESIS_Time.getDayOfYear();
        result.setDayOfYear(aDayOfYear);
        long aYear = this.getYear() + aGENESIS_Time.getYear();
        result.setYear(aYear);
        normalise(result);
        return result;
    }

    @Override
    public String toString() {
        return "GENESIS_Time("
                + "year " + getYear()
                + ", dayOfYear " + getDayOfYear()
                + ", secondOfDay " + getSecondOfDay() + ")";
    }

    public String toStringCSV() {
        return "" + getYear() + ", " + getDayOfYear() + ", " + getSecondOfDay();
    }

    /**
     * A error warning is written to System.err.println when the comparison is
     * not for the same class of object. This may or may not be the best way to
     * handle this.
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof GENESIS_Time) {
            GENESIS_Time oGENESIS_Time = (GENESIS_Time) o;
            if (!GENESIS_Time.isNormalised(this)) {
                normalise(this);
            }
            if (!isNormalised(oGENESIS_Time)) {
                normalise(oGENESIS_Time);
            }
            long theYear = getYear();
            long oYear = oGENESIS_Time.getYear();
            if (theYear < oYear) {
                return -1;
            } else {
                if (theYear == oYear) {
                    int theDayOfYear = getDayOfYear();
                    int oDayOfYear = oGENESIS_Time.getDayOfYear();
                    if (theDayOfYear < oDayOfYear) {
                        return -1;
                    } else {
                        if (theDayOfYear == oDayOfYear) {
                            int theSecondOfDay = getSecondOfDay();
                            int oSecondOfDay = oGENESIS_Time.getSecondOfDay();
                            if (theSecondOfDay < oSecondOfDay) {
                                return -1;
                            } else {
                                if (theSecondOfDay == oSecondOfDay) {
                                    return 0;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            System.err.println(
                    "Trying to compare " + this.getClass().getName()
                    + " with " + o.getClass().getName());
        }
        return 1;
    }

    @Override
    public boolean equals(Object a_Object) {
        if (a_Object instanceof GENESIS_Time) {
            GENESIS_Time a_GENESIS_Time = (GENESIS_Time) a_Object;
            if (!GENESIS_Time.isNormalised(this)) {
                normalise(this);
            }
            if (!isNormalised(a_GENESIS_Time)) {
                normalise(a_GENESIS_Time);
            }
            return a_GENESIS_Time.getYear() == this.getYear()
                    && a_GENESIS_Time.getDayOfYear().equals(this.getDayOfYear())
                    && a_GENESIS_Time.getSecondOfDay().equals(this.getSecondOfDay());
        }
        return false;
    }

    @Override
    public int hashCode() {
//        if (!GENESIS_Time.isNormalised(this)) {
//            normalise(this);
//        }
        int hash = 5;
        long theYear = this.getYear();
        long integerMax = Integer.MAX_VALUE;
        // &TODO This can be optimised for large magnitude values of theYear
        if (theYear > 0) {
            while (theYear > integerMax) {
                theYear -= integerMax;
            }
        } else {
            while (theYear < 0) {
                theYear += integerMax;
            }
        }
        hash = 37 * hash + (int) theYear;
        hash = 37 * hash + this.getDayOfYear();
        hash = 37 * hash + this.getSecondOfDay();
        return hash;
    }
}
