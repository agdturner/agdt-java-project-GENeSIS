/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.utilities;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import javax.xml.bind.JAXBException;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.CommonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.PopulationFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBoundPopulation;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBoundProbability;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBoundRate;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.GenderedAgeBoundProbabilities;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.population.PopulationType;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Fertility;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Miscarriage;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Mortality;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Population;

public class GENESIS_Collections {

    /**
     * @param pop
     * @return a duplicate of pop duplicating
     * pop.getGenderedAgeBoundPopulation().getFemale() and
     * pop.getGenderedAgeBoundPopulation().getMale().
     */
    public static PopulationType deepCopy(PopulationType pop) {
        PopulationType result = null;
        try {
            result = PopulationFactory.newPopulationType();
        } catch (JAXBException e) {
            e.printStackTrace();
            System.err.println(
                    "System.exit("
                    + GENESIS_ErrorAndExceptionHandler.IOException
                    + ") from " + GENESIS_Collections.class.getName() + ".deepCopy(PopulationType)");
            System.exit(GENESIS_ErrorAndExceptionHandler.JAXBExceptionExitStatus);
        }
//        // Ensure females initialised
        result.getGenderedAgeBoundPopulation().getFemale();
//        pop.getGenderedAgePopulation().getFemale();
        result.getGenderedAgeBoundPopulation().getFemale().addAll(
                deepCopyTo_ArrayList_AgePopulation(pop.getGenderedAgeBoundPopulation().getFemale()));
//        // Ensure males initialised
        result.getGenderedAgeBoundPopulation().getMale();
//        pop.getGenderedAgePopulation().getMale();
        result.getGenderedAgeBoundPopulation().getMale().addAll(
                deepCopyTo_ArrayList_AgePopulation(pop.getGenderedAgeBoundPopulation().getMale()));
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<String, GenderedAgeBoundProbabilities> deepCopyTo_TreeMap_String_GenderedAgeBoundProbabilities(
            TreeMap<String, GenderedAgeBoundProbabilities> map) {
        TreeMap<String, GenderedAgeBoundProbabilities> result;
        result = new TreeMap<String, GenderedAgeBoundProbabilities>();
        Iterator<String> ite = map.keySet().iterator();
        while (ite.hasNext()) {
            String key = ite.next();
            GenderedAgeBoundProbabilities value = map.get(key);
            GenderedAgeBoundProbabilities newValue = CommonFactory.newGenderedAgeBoundProbabilities();
            newValue.getFemale().addAll(deepCopyTo_ArrayList_AgeBoundProbability(value.getFemale()));
            newValue.getMale().addAll(deepCopyTo_ArrayList_AgeBoundProbability(value.getMale()));
            result.put(key, newValue);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating neither the keys in map nor the
     * keys for the values of map.
     */
    public static TreeMap<String, TreeMap<String, GenderedAgeBoundProbabilities>> deepCopyTo_TreeMap_String_TreeMap_String_GenderedAgeBoundProbabilities(
            TreeMap<String, TreeMap<String, GenderedAgeBoundProbabilities>> map) {
        TreeMap<String, TreeMap<String, GenderedAgeBoundProbabilities>> result;
        result = new TreeMap<String, TreeMap<String, GenderedAgeBoundProbabilities>>();
        Iterator<String> ite = map.keySet().iterator();
        while (ite.hasNext()) {
            String key = ite.next();
            TreeMap<String, GenderedAgeBoundProbabilities> value = map.get(key);
            TreeMap<String, GenderedAgeBoundProbabilities> newValue;
            newValue = deepCopyTo_TreeMap_String_GenderedAgeBoundProbabilities(value);
            result.put(key, newValue);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static HashMap<String, GENESIS_Population> deepCopyTo_HashMap_String_Population(
            HashMap<String, GENESIS_Population> map) {
        HashMap<String, GENESIS_Population> result = new HashMap<String, GENESIS_Population>();
        Iterator<String> ite = map.keySet().iterator();
        String key;
        GENESIS_Population value;
        GENESIS_Population duplicate_value;
        while (ite.hasNext()) {
            key = ite.next();
            value = map.get(key);
            value.updateGenderedAgePopulation();
            duplicate_value = new GENESIS_Population(value);
            result.put(key, duplicate_value);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<String, GENESIS_Population> deepCopyTo_TreeMap_String_Population(
            TreeMap<String, GENESIS_Population> map) {
        TreeMap<String, GENESIS_Population> result = new TreeMap<String, GENESIS_Population>();
        Iterator<String> ite = map.keySet().iterator();
        String key;
        GENESIS_Population value;
        GENESIS_Population duplicate_value;
        while (ite.hasNext()) {
            key = ite.next();
            value = map.get(key);
            value.updateGenderedAgePopulation();
            duplicate_value = new GENESIS_Population(value);
            result.put(key, duplicate_value);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<String, TreeMap<String, GENESIS_Population>> deepCopyTo_TreeMap_String_TreeMap_String_Population(
            TreeMap<String, TreeMap<String, GENESIS_Population>> map) {
        TreeMap<String, TreeMap<String, GENESIS_Population>> result;
        result = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        Iterator<String> ite = map.keySet().iterator();
        String key;
        TreeMap<String, GENESIS_Population> value;
        TreeMap<String, GENESIS_Population> duplicate_value;
        while (ite.hasNext()) {
            key = ite.next();
            value = map.get(key);
            duplicate_value = deepCopyTo_TreeMap_String_Population(value);
            result.put(key, duplicate_value);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<String, TreeMap<String, TreeMap<String, GENESIS_Population>>> deepCopyTo_TreeMap_String_TreeMap_String_TreeMap_String_Population(
            TreeMap<String, TreeMap<String, TreeMap<String, GENESIS_Population>>> map) {
        TreeMap<String, TreeMap<String, TreeMap<String, GENESIS_Population>>> result;
        result = new TreeMap<String, TreeMap<String, TreeMap<String, GENESIS_Population>>>();
        Iterator<String> ite = map.keySet().iterator();
        String key;
        TreeMap<String, TreeMap<String, GENESIS_Population>> value;
        TreeMap<String, TreeMap<String, GENESIS_Population>> duplicate_value;
        while (ite.hasNext()) {
            key = ite.next();
            value = map.get(key);
            duplicate_value = deepCopyTo_TreeMap_String_TreeMap_String_Population(value);
            result.put(key, duplicate_value);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<String, GENESIS_Mortality> deepCopyTo_TreeMap_String_Mortality(
            TreeMap<String, GENESIS_Mortality> map) {
        TreeMap<String, GENESIS_Mortality> result = new TreeMap<String, GENESIS_Mortality>();
        Iterator<String> ite = map.keySet().iterator();
        String key;
        GENESIS_Mortality value;
        GENESIS_Mortality duplicate_value;
        while (ite.hasNext()) {
            key = ite.next();
            value = map.get(key);
            value.updateGenderedAgeBoundRates();
            duplicate_value = new GENESIS_Mortality(value);
            result.put(key, duplicate_value);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<String, TreeMap<String, GENESIS_Mortality>> deepCopyTo_TreeMap_String_TreeMap_String_Mortality(
            TreeMap<String, TreeMap<String, GENESIS_Mortality>> map) {
        TreeMap<String, TreeMap<String, GENESIS_Mortality>> result = new TreeMap<String, TreeMap<String, GENESIS_Mortality>>();
        Iterator<String> ite = map.keySet().iterator();
        String key;
        TreeMap<String, GENESIS_Mortality> value;
        TreeMap<String, GENESIS_Mortality> duplicate_value;
        while (ite.hasNext()) {
            key = ite.next();
            value = map.get(key);
            duplicate_value = deepCopyTo_TreeMap_String_Mortality(value);
            result.put(key, duplicate_value);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<String, GENESIS_Miscarriage> deepCopyTo_TreeMap_String_Miscarriage(
            TreeMap<String, GENESIS_Miscarriage> map) {
        TreeMap<String, GENESIS_Miscarriage> result = new TreeMap<String, GENESIS_Miscarriage>();
        Iterator<String> ite = map.keySet().iterator();
        String key;
        GENESIS_Miscarriage value;
        GENESIS_Miscarriage duplicate_value;
        while (ite.hasNext()) {
            key = ite.next();
            value = map.get(key);
            value.updateLists();
            duplicate_value = new GENESIS_Miscarriage(value);
            result.put(key, duplicate_value);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<String, TreeMap<String, GENESIS_Miscarriage>> deepCopyTo_TreeMap_String_TreeMap_String_Miscarriage(
            TreeMap<String, TreeMap<String, GENESIS_Miscarriage>> map) {
        TreeMap<String, TreeMap<String, GENESIS_Miscarriage>> result = new TreeMap<String, TreeMap<String, GENESIS_Miscarriage>>();
        Iterator<String> ite = map.keySet().iterator();
        String key;
        TreeMap<String, GENESIS_Miscarriage> value;
        TreeMap<String, GENESIS_Miscarriage> duplicate_value;
        while (ite.hasNext()) {
            key = ite.next();
            value = map.get(key);
            duplicate_value = deepCopyTo_TreeMap_String_Miscarriage(value);
            result.put(key, duplicate_value);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<String, GENESIS_Fertility> deepCopyTo_TreeMap_String_Fertility(
            TreeMap<String, GENESIS_Fertility> map) {
        TreeMap<String, GENESIS_Fertility> result = new TreeMap<String, GENESIS_Fertility>();
        Iterator<String> ite = map.keySet().iterator();
        String key;
        GENESIS_Fertility value;
        GENESIS_Fertility duplicate_value;
        while (ite.hasNext()) {
            key = ite.next();
            value = map.get(key);
            value.updateLists();
            duplicate_value = new GENESIS_Fertility(value);
            result.put(key, duplicate_value);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<String, TreeMap<String, GENESIS_Fertility>> deepCopyTo_TreeMap_String_TreeMap_String_Fertility(
            TreeMap<String, TreeMap<String, GENESIS_Fertility>> map) {
        TreeMap<String, TreeMap<String, GENESIS_Fertility>> result = new TreeMap<String, TreeMap<String, GENESIS_Fertility>>();
        Iterator<String> ite = map.keySet().iterator();
        String key;
        TreeMap<String, GENESIS_Fertility> value;
        TreeMap<String, GENESIS_Fertility> duplicate_value;
        while (ite.hasNext()) {
            key = ite.next();
            value = map.get(key);
            duplicate_value = deepCopyTo_TreeMap_String_Fertility(value);
            result.put(key, duplicate_value);
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map duplicating the values, but not the keys.
     */
    public static TreeMap<GENESIS_AgeBound, BigDecimal> deepCopyTo_TreeMap_AgeBound_BigDecimal(
            TreeMap<GENESIS_AgeBound, BigDecimal> map) {
        TreeMap<GENESIS_AgeBound, BigDecimal> result = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        if (map != null) {
            Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = map.entrySet().iterator();
            Entry<GENESIS_AgeBound, BigDecimal> entry;
            while (ite.hasNext()) {
                entry = ite.next();
                GENESIS_AgeBound newGENESIS_AgeBound = new GENESIS_AgeBound(entry.getKey());
                BigDecimal newBigDecimal = new BigDecimal(entry.getValue().toString());
                result.put(newGENESIS_AgeBound, newBigDecimal);
            }
        }
        return result;
    }

    /**
     * @param l
     * @return a duplicate of l.
     */
    public static ArrayList<AgeBoundPopulation> deepCopyTo_ArrayList_AgeBoundPopulation(
            List<AgeBoundPopulation> l) {
        ArrayList<AgeBoundPopulation> result = new ArrayList<AgeBoundPopulation>();
        if (l != null) {
            Iterator<AgeBoundPopulation> ite = l.iterator();
            while (ite.hasNext()) {
                AgeBoundPopulation newAgeBoundPopulation = CommonFactory.newAgeBoundPopulation(ite.next());
                result.add(newAgeBoundPopulation);
            }
        }
        return result;
    }

    /**
     * @param map
     * @return a list of AgeBoundPopulation derived by combining the keys and
     * values of map.
     */
    public static ArrayList<AgeBoundPopulation> deepCopyTo_ArrayList_AgeBound_Population(
            TreeMap<GENESIS_AgeBound, BigDecimal> map) {
        ArrayList<AgeBoundPopulation> result = new ArrayList<AgeBoundPopulation>();
        if (map != null) {
            Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = map.entrySet().iterator();
            Entry<GENESIS_AgeBound, BigDecimal> entry;
            while (ite.hasNext()) {
                entry = ite.next();
                AgeBound newAgeBound = CommonFactory.newAgeBound(entry.getKey());
                BigDecimal newPopulation = new BigDecimal(
                        entry.getValue().toString());
                AgeBoundPopulation newAgeBoundPopulation = CommonFactory.newAgeBoundPopulation();
                newAgeBoundPopulation.setAgeBound(newAgeBound);
                newAgeBoundPopulation.setPopulation(newPopulation);
                result.add(newAgeBoundPopulation);
            }
        }
        return result;
    }

    /**
     * @param l
     * @return a TreeMap<GENESIS_AgeBound, BigDecimal> with keys and values
     * derived from the entries in l.
     */
    public static TreeMap<GENESIS_AgeBound, BigDecimal> deepCopyTo_TreeMap_AgeBound_Probability(
            List<AgeBoundProbability> l) {
        TreeMap<GENESIS_AgeBound, BigDecimal> result = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        if (l != null) {
            Iterator<AgeBoundProbability> ite = l.iterator();
            while (ite.hasNext()) {
                AgeBoundProbability newAgeBoundProbability = CommonFactory.newAgeBoundProbability(ite.next());
                result.put(
                        new GENESIS_AgeBound(newAgeBoundProbability.getAgeBound()),
                        newAgeBoundProbability.getProbability());
            }
        }
        return result;
    }

    /**
     * @param l
     * @return a TreeMap<GENESIS_AgeBound, BigDecimal> with keys and values
     * derived from the entries in l.
     */
    public static TreeMap<GENESIS_AgeBound, BigDecimal> deepCopyTo_TreeMap_AgeBound_Rate(
            List<AgeBoundRate> l) {
        TreeMap<GENESIS_AgeBound, BigDecimal> result = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        if (l != null) {
            Iterator<AgeBoundRate> ite = l.iterator();
            while (ite.hasNext()) {
                AgeBoundRate newAgeBoundRate = CommonFactory.newAgeBoundRate(ite.next());
                result.put(
                        new GENESIS_AgeBound(newAgeBoundRate.getAgeBound()),
                        newAgeBoundRate.getRate());
            }
        }
        return result;
    }

    /**
     * @param map
     * @return a duplicate of map that duplicates the values but not the keys.
     */
    public static TreeMap<GENESIS_AgeBound, BigDecimal> deepCopyTo_TreeMap_AgeBound_Population(
            TreeMap<GENESIS_AgeBound, BigDecimal> map) {
        TreeMap<GENESIS_AgeBound, BigDecimal> result = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        if (map != null) {
            Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = map.entrySet().iterator();
            Entry<GENESIS_AgeBound, BigDecimal> entry;
            while (ite.hasNext()) {
                entry = ite.next();
                GENESIS_AgeBound newGENESIS_AgeBound = new GENESIS_AgeBound(entry.getKey());
                BigDecimal newPopulation = new BigDecimal(
                        entry.getValue().toString());
                result.put(newGENESIS_AgeBound, newPopulation);
            }
        }
        return result;
    }

    /**
     * @param l
     * @return a TreeMap<GENESIS_AgeBound, BigDecimal> with keys and values
     * derived from the entries in l.
     */
    public static TreeMap<GENESIS_AgeBound, BigDecimal> deepCopyTo_TreeMap_AgeBound_Population(
            List<AgeBoundPopulation> l) {
        TreeMap<GENESIS_AgeBound, BigDecimal> result = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        if (l != null) {
            Iterator<AgeBoundPopulation> ite = l.iterator();
            while (ite.hasNext()) {
                AgeBoundPopulation newAgeBoundPopulation = CommonFactory.newAgeBoundPopulation(ite.next());
                result.put(new GENESIS_AgeBound(newAgeBoundPopulation.getAgeBound()), newAgeBoundPopulation.getPopulation());
            }
        }
        return result;
    }

    /**
     * @param l
     * @return a ArrayList duplicating the entries in l.
     */
    public static ArrayList<AgeBoundProbability> deepCopyTo_ArrayList_AgeBoundProbability(
            List<AgeBoundProbability> l) {
        ArrayList<AgeBoundProbability> result = new ArrayList<AgeBoundProbability>();
        if (l != null) {
            ListIterator<AgeBoundProbability> ite = l.listIterator();
            while (ite.hasNext()) {
                AgeBoundProbability newAgeProbability = CommonFactory.newAgeBoundProbability(ite.next());
                result.add(newAgeProbability);
            }
        }
        return result;
    }

    /**
     * @param l
     * @return a ArrayList duplicating the entries in l.
     */
    public static ArrayList<AgeBoundRate> deepCopyTo_ArrayList_AgeBoundRate(
            List<AgeBoundRate> l) {
        ArrayList<AgeBoundRate> result = new ArrayList<AgeBoundRate>();
        if (l != null) {
            ListIterator<AgeBoundRate> ite = l.listIterator();
            while (ite.hasNext()) {
                AgeBoundRate newAgeRate = CommonFactory.newAgeBoundRate(ite.next());
                result.add(newAgeRate);
            }
        }
        return result;
    }

    /**
     * @param map
     * @return a ArrayList with entries derived from the keys and values in map.
     */
    public static ArrayList<AgeBoundProbability> deepCopyTo_ArrayList_AgeProbability(
            TreeMap<GENESIS_AgeBound, BigDecimal> map) {
        ArrayList<AgeBoundProbability> result = new ArrayList<AgeBoundProbability>();
        if (map != null) {
            Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = map.entrySet().iterator();
            Entry<GENESIS_AgeBound, BigDecimal> entry;
            while (ite.hasNext()) {
                entry = ite.next();
                AgeBound newAgeBound = CommonFactory.newAgeBound(entry.getKey());
                BigDecimal newProbability = new BigDecimal(
                        entry.getValue().toString());
                AgeBoundProbability ageBoundProbability = CommonFactory.newAgeBoundProbability();
                ageBoundProbability.setAgeBound(newAgeBound);
                ageBoundProbability.setProbability(newProbability);
                result.add(ageBoundProbability);
            }
        }
        return result;
    }

    /**
     * @param map
     * @return a ArrayList with entries derived from the keys and values in map.
     */
    public static ArrayList<AgeBoundRate> deepCopyTo_ArrayList_AgeBoundRate(
            TreeMap<GENESIS_AgeBound, BigDecimal> map) {
        ArrayList<AgeBoundRate> result = new ArrayList<AgeBoundRate>();
        if (map != null) {
            Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = map.entrySet().iterator();
            Entry<GENESIS_AgeBound, BigDecimal> entry;
            while (ite.hasNext()) {
                entry = ite.next();
                AgeBoundRate ageBoundRate = CommonFactory.newAgeBoundRate();
                ageBoundRate.setAgeBound(entry.getKey());
                ageBoundRate.setRate(new BigDecimal(entry.getValue().toString()));
                result.add(ageBoundRate);
            }
        }
        return result;
    }

    /**
     * @param l
     * @return a ArrayList duplicating l.
     */
    public static ArrayList<AgeBoundPopulation> deepCopyTo_ArrayList_AgePopulation(
            List<AgeBoundPopulation> l) {
        ArrayList<AgeBoundPopulation> result = new ArrayList<AgeBoundPopulation>();
        if (l != null) {
            Iterator<AgeBoundPopulation> ite = l.iterator();
            while (ite.hasNext()) {
                AgeBoundPopulation newAgeBoundPopulation = CommonFactory.newAgeBoundPopulation(ite.next());
                result.add(newAgeBoundPopulation);
            }
        }
        return result;
    }

    /**
     * @param s0
     * @param s1
     * @return A HashSet combining the entries in s0 and s1
     */
    public static HashSet<GENESIS_AgeBound> getCombined_HashSet_AgeBound(
            Set<GENESIS_AgeBound> s0,
            Set<GENESIS_AgeBound> s1) {
        HashSet<GENESIS_AgeBound> result = new HashSet<GENESIS_AgeBound>();
        result.addAll(s0);
        result.addAll(s1);
        return result;
    }

    /**
     * @param map
     * @return The maximum value in map.
     */
    public static BigDecimal getMaxValue(TreeMap<GENESIS_AgeBound, BigDecimal> map) {
        BigDecimal result = null;
        if (map != null) {
            if (!map.isEmpty()) {
                result = map.firstEntry().getValue();
                Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = map.entrySet().iterator();
                Entry<GENESIS_AgeBound, BigDecimal> entry;
                while (ite.hasNext()) {
                    entry = ite.next();
                    BigDecimal value = entry.getValue();
                    if (result != null) {
                        if (value != null) {
                            result = result.max(value);
                        }
                    } else {
                        result = value;
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param map
     * @return The minimum value in map.
     */
    public static BigDecimal getMinValue(TreeMap<GENESIS_AgeBound, BigDecimal> map) {
        BigDecimal result = null;
        if (map != null) {
            if (!map.isEmpty()) {
                result = map.firstEntry().getValue();
                Iterator<Entry<GENESIS_AgeBound, BigDecimal>> ite = map.entrySet().iterator();
                Entry<GENESIS_AgeBound, BigDecimal> entry;
                while (ite.hasNext()) {
                    entry = ite.next();
                    BigDecimal value = entry.getValue();
                    if (result != null) {
                        if (value != null) {
                            result = result.min(value);
                        }
                    } else {
                        result = value;
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param map
     * @return the minimum age in years of the keys in map
     */
    public static Long getMinAgeYears(TreeMap<GENESIS_AgeBound, BigDecimal> map) {
        Long minAge = null;
        if (map != null) {
            GENESIS_AgeBound ageBound;
            if (!map.isEmpty()) {
                ageBound = map.firstKey();
                minAge = ageBound.getAgeMin().getYear();
            }
        }
        return minAge;
    }

    /**
     * @param t
     * @return the maximum age in years of the keys in map
     */
    public static Long getMaxAgeYears(TreeMap<GENESIS_AgeBound, BigDecimal> t) {
        Long maxAge = null;
        if (t != null) {
            GENESIS_AgeBound ageBound;
            if (!t.isEmpty()) {
                ageBound = t.lastKey();
                maxAge = ageBound.getAgeMax().getYear();
            }
        }
        return maxAge;
    }

    /**
     * Adds value to the value at map.get(key) if it exists or puts the key
     * value mapping into map otherwise.
     *
     * @param map
     * @param key
     * @param value
     */
    protected static void addTo_TreeMap_AgeBound_BigDecimal(
            TreeMap<GENESIS_AgeBound, BigDecimal> map,
            GENESIS_AgeBound key,
            BigDecimal value) {
        if (value != null) {
            BigDecimal currentValue = map.get(key);
            if (currentValue != null) {
                BigDecimal newValue = currentValue.add(value);
                map.put(key, newValue);
            } else {
                map.put(key, new BigDecimal(value.toString()));
            }
        }
    }

    /**
     * Adds value to the value at map.get(key) if it exists or puts the key
     * value mapping into map otherwise.
     *
     * @param map
     * @param key
     * @param value
     * @param handleOutOfMemoryError
     */
    public static void addTo_TreeMap_AgeBound_BigDecimal(
            TreeMap<GENESIS_AgeBound, BigDecimal> map,
            GENESIS_AgeBound key,
            BigDecimal value,
            boolean handleOutOfMemoryError) {
        try {
            addTo_TreeMap_AgeBound_BigDecimal(
                    map,
                    key,
                    value);
//            boolean valueNotNull = (value != null);
//            if (valueNotNull) {
//                boolean currentValueNotNull = false;
//                BigDecimal currentValue = new BigDecimal("10000000000000000000000000000.1000000000000000001");
//                BigDecimal newValue = new BigDecimal("10000000000000000000000000000.1000000000000000001");
//                currentValue = map.get(key);
//                currentValueNotNull = (currentValue != null);
//                if (currentValueNotNull) {
//                    newValue = currentValue.add(value);
//                } else {
//                    newValue = new BigDecimal(value.toString());
//                }
//                map.put(key, newValue);
//                try {
//                    map.put(key, newValue);
//                } catch (OutOfMemoryError e) {
//                    if (handleOutOfMemoryError) {
//                    } else {
//                        throw e;
//                    }
//                }
//            }
        } catch (OutOfMemoryError e) {
            throw e;
        }
    }

    /**
     * Adds value to the value at map.get(key) if it exists or puts the key
     * value mapping into map otherwise.
     *
     * @param map
     * @param key
     * @param value
     */
    public static void addTo_TreeMap_AgeBound_BigInteger(
            TreeMap<GENESIS_AgeBound, BigInteger> map,
            GENESIS_AgeBound key,
            BigInteger value) {
        if (value != null) {
            BigInteger currentValue = map.get(key);
            if (currentValue != null) {
                BigInteger newValue = currentValue.add(value);
                map.put(key, newValue);
            } else {
                map.put(key, new BigInteger(value.toString()));
            }
        }
    }

    /**
     * Adds all mappings in mapToAdd into mapToAddTo unless they already exist
     * in which case the values for the mappings are summed.
     *
     * @param mapToAddTo
     * @param mapToAdd
     */
    public static void addTo_TreeMap_AgeBound_BigInteger(
            TreeMap<GENESIS_AgeBound, BigInteger> mapToAddTo,
            TreeMap<GENESIS_AgeBound, BigInteger> mapToAdd) {
        Set<GENESIS_AgeBound> keys = GENESIS_Collections.getCombined_HashSet_AgeBound(
                mapToAddTo.keySet(),
                mapToAdd.keySet());
        Iterator<GENESIS_AgeBound> ite = keys.iterator();
        GENESIS_AgeBound key;
        BigInteger v0;
        BigInteger v1;
        while (ite.hasNext()) {
            key = ite.next();
            v0 = mapToAdd.get(key);
            if (v0 != null) {
                if (mapToAddTo.containsKey(key)) {
                    v1 = mapToAddTo.get(key);
                    //if (v1 == null) {
                    //    a_TreeMapToAddTo.put(key, v0);
                    //} else {
                    mapToAddTo.put(key, v0.add(v1));
                    //}
                } else {
                    //if (v0 != null) {
                    mapToAddTo.put(key, new BigInteger(v0.toString()));
                    //}
                }
            }
        }
    }

    /**
     * Adds all mappings in mapToAdd into mapToAddTo unless they already exist
     * in which case the values for the mappings are summed.
     *
     * @param mapToAddTo
     * @param mapToAdd
     */
    public static void addTo_TreeMap_AgeBound_BigDecimal(
            TreeMap<GENESIS_AgeBound, BigDecimal> mapToAddTo,
            TreeMap<GENESIS_AgeBound, BigDecimal> mapToAdd) {
        Set<GENESIS_AgeBound> keys = GENESIS_Collections.getCombined_HashSet_AgeBound(
                mapToAddTo.keySet(),
                mapToAdd.keySet());
        Iterator<GENESIS_AgeBound> ite = keys.iterator();
        GENESIS_AgeBound key;
        BigDecimal v0;
        BigDecimal v1;
        while (ite.hasNext()) {
            key = ite.next();
            v0 = mapToAdd.get(key);
            if (v0 != null) {
                if (mapToAddTo.containsKey(key)) {
                    v1 = mapToAddTo.get(key);
                    //if (v1 == null) {
                    //    a_TreeMapToAddTo.put(key, v0);
                    //} else {
                    mapToAddTo.put(key, v0.add(v1));
                    //}
                } else {
                    //if (v0 != null) {
                    mapToAddTo.put(key, new BigDecimal(v0.toString()));
                    //}
                }
            }
        }
    }
}
