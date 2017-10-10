/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.math.statistics;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.logging.Generic_Log;
import uk.ac.leeds.ccg.andyt.generic.math.statistics.Generic_Statistics;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Collections;

/**
 *
 * @author geoagdt
 */
public class GENESIS_Statistics extends Generic_Statistics {

    private GENESIS_Environment _GENESIS_Environment;

    public GENESIS_Statistics(
            GENESIS_Environment a_GENESIS_Environment) {
        super();
        this._GENESIS_Environment = a_GENESIS_Environment;
    }

    /**
     * Calculates and returns the sum of squared difference between the values
     * in map0 and map1
     *
     * @param map0
     * @param map1
     * @param map0Name Used for logging and can be null
     * @param map1Name Used for logging and can be null
     * @param keyName Used for logging and can be null
     * @return
     */
    public static Object[] getFirstOrderStatistics2(
            TreeMap<GENESIS_AgeBound, BigDecimal> map0,
            TreeMap<GENESIS_AgeBound, BigDecimal> map1,
            String map0Name,
            String map1Name,
            String keyName) {
        log("<getFirstOrderStatistics0>");
        Object[] result = new Object[3];
        BigDecimal map0Value;
        BigDecimal map1Value;
        BigDecimal difference;
        BigDecimal differenceSquared;
        BigDecimal sumDifference = BigDecimal.ZERO;
        BigDecimal sumDifferenceSquared = BigDecimal.ZERO;
        GENESIS_AgeBound key;
        HashSet<GENESIS_AgeBound> completeKeySet_HashSet = GENESIS_Collections.getCombined_HashSet_AgeBound(
                map0.keySet(),
                map1.keySet());
        result[0] = completeKeySet_HashSet;
        Iterator<GENESIS_AgeBound> completeKeySetIterator = completeKeySet_HashSet.iterator();
        Object value;
        log(
                keyName + ", "
                + map0Name + ", "
                + map1Name + ", "
                + "difference, "
                + "difference squared, "
                + "sum difference"
                + "sum difference squared");
        while (completeKeySetIterator.hasNext()) {
            key = completeKeySetIterator.next();
            value = map0.get(key);
            if (value == null) {
                map0Value = BigDecimal.ZERO;
            } else {
                map0Value = (BigDecimal) value;
            }
            value = map1.get(key);
            if (value == null) {
                map1Value = BigDecimal.ZERO;
            } else {
                map1Value = (BigDecimal) value;
            }
            difference = map1Value.subtract(map0Value);
            sumDifference = sumDifference.add(difference);
            differenceSquared = difference.multiply(difference);
            sumDifferenceSquared = sumDifferenceSquared.add(differenceSquared);
            log(
                    key.toString() + ", "
                    + map0Value + ", "
                    + map1Value + ", "
                    + difference + ", "
                    + differenceSquared + ", "
                    + sumDifference + ", "
                    + sumDifferenceSquared);
        }
        result[1] = sumDifference;
        result[2] = sumDifferenceSquared;
        log("</getFirstOrderStatistics0>");
        return result;
    }

    /**
     * Calculates and returns the sum of squared difference between the values
     * in map0 and map1
     *
     * @param map0
     * @param map1
     * @param map0Name Used for logging and can be null
     * @param map1Name Used for logging and can be null
     * @param keyName Used for logging and can be null
     * @return
     */
    public static Object[] getFirstOrderStatistics3(
            TreeMap<GENESIS_AgeBound, BigDecimal> map0,
            TreeMap<GENESIS_AgeBound, BigDecimal> map1,
            String map0Name,
            String map1Name,
            String keyName) {
        log("<getFirstOrderStatistics1>");
        Object[] result = new Object[3];
        BigDecimal map0Value;
        BigDecimal map1Value;
        BigDecimal difference;
        BigDecimal differenceSquared;
        BigDecimal sumDifference = BigDecimal.ZERO;
        BigDecimal sumDifferenceSquared = BigDecimal.ZERO;
        GENESIS_AgeBound key;
        HashSet<GENESIS_AgeBound> completeKeySet_HashSet = GENESIS_Collections.getCombined_HashSet_AgeBound(
                map0.keySet(),
                map1.keySet());
        result[0] = completeKeySet_HashSet;
        Iterator<GENESIS_AgeBound> completeKeySetIterator = completeKeySet_HashSet.iterator();
        Object value;
        log(keyName + ", "
                + map0Name + ", "
                + map1Name + ", "
                + "difference, "
                + "difference squared, "
                + "sum difference"
                + "sum difference squared");
        while (completeKeySetIterator.hasNext()) {
            key = completeKeySetIterator.next();
            value = map0.get(key);
            if (value == null) {
                map0Value = BigDecimal.ZERO;
            } else {
                //map0Value = new BigDecimal((BigInteger) value);
                map0Value = (BigDecimal) value;
            }
            value = map1.get(key);
            if (value == null) {
                map1Value = BigDecimal.ZERO;
            } else {
                map1Value = (BigDecimal) value;
            }
            difference = map1Value.subtract(map0Value);
            sumDifference = sumDifference.add(difference);
            differenceSquared = difference.multiply(difference);
            sumDifferenceSquared = sumDifferenceSquared.add(differenceSquared);
            log(key.toString() + ", "
                    + map0Value + ", "
                    + map1Value + ", "
                    + difference + ", "
                    + differenceSquared + ", "
                    + sumDifference + ", "
                    + sumDifferenceSquared);
        }
        result[1] = sumDifference;
        result[2] = sumDifferenceSquared;
        log("</getFirstOrderStatistics1>");
        return result;
    }

    private static void log(
            String message) {
        log(Generic_Log.Generic_DefaultLogLevel, message);
    }

    private static void log(
            Level a_Level,
            String message) {
        Logger.getLogger(Generic_Log.Generic_DefaultLoggerName).log(a_Level, message);
    }
}
