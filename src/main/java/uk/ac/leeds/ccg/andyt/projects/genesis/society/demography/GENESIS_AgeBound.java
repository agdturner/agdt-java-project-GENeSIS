package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.Serializable;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.CommonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.Time;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;

/**
 * A class for age limits. The ageMin and ageMax are stored as relative times.
 * If ageMax is null then the age limit is open ended in that any age older than
 * ageMin belongs to the bound. The ageMax is used in an exclusive bound, while
 * ageMin is used as an inclusive bound. Such bounding allows ageMax to be
 * stored more neatly in the expected general usage. It also allows time to be
 * extended to store sub second values in a potential future refactoring without
 * perhaps so many major changes being required.
 */
public final class GENESIS_AgeBound extends AgeBound implements Comparable, Serializable {

    static final long serialVersionUID = 1L;
    /**
     * For storing the minimum outer bound of an age. For example, suppose an
     * age is in the range 0 to 5 years old: furthermore, suppose that initially
     * the age is assigned an individual year of age group from 1 to 2 years
     * old, then ageMinBound is for storing the minimum outer bound for the age
     * i.e. 0.
     */
    protected Time ageMinBound;
    /**
     * For storing the maximum outer bound of an age. For example, suppose an
     * age is in the range 0 to 5 years old: furthermore, suppose that initially
     * the age is assigned an individual year of age group from 1 to 2 years
     * old, then ageMaxBound is for storing the maximum outer bound for the age
     * i.e. 5.
     */
    protected Time ageMaxBound;

    public GENESIS_AgeBound() {
    }

    public GENESIS_AgeBound(GENESIS_AgeBound aGENESIS_AgeBound) {
        Time aAgeMin = aGENESIS_AgeBound.getAgeMin();
        if (aAgeMin != null) {
            setAgeMin(new GENESIS_Time(CommonFactory.newTime(aAgeMin)));
        }
        Time aAgeMax = aGENESIS_AgeBound.getAgeMax();
        if (aAgeMax != null) {
            setAgeMax(new GENESIS_Time(CommonFactory.newTime(aAgeMax)));
        }
        Time aAgeMinBound = aGENESIS_AgeBound.getAgeMinBound();
        if (aAgeMinBound != null) {
            setAgeMinBound(new GENESIS_Time(CommonFactory.newTime(aAgeMinBound)));
        }
        Time aAgeMaxBound = aGENESIS_AgeBound.getAgeMaxBound();
        if (aAgeMaxBound != null) {
            setAgeMaxBound(new GENESIS_Time(CommonFactory.newTime(aAgeMaxBound)));
        }
    }

    public GENESIS_AgeBound(AgeBound aAgeBound) {
        Time aAgeMin = aAgeBound.getAgeMin();
        if (aAgeMin != null) {
            setAgeMin(new GENESIS_Time(CommonFactory.newTime(aAgeMin)));
            initAgeMinBound();
        }
        Time aAgeMax = aAgeBound.getAgeMax();
        if (aAgeMax != null) {
            setAgeMax(new GENESIS_Time(CommonFactory.newTime(aAgeMax)));
            initAgeMaxBound();
        }
    }

    public GENESIS_AgeBound(
            AgeBound aAgeBound,
            GENESIS_Environment _GENESIS_Environment) {
        Time aAgeMin = aAgeBound.getAgeMin();
        if (aAgeMin != null) {
            setAgeMin(new GENESIS_Time(CommonFactory.newTime(aAgeMin)));
            initAgeMinBound();
        }
        Time aAgeMax = aAgeBound.getAgeMax();
        if (aAgeMax != null) {
            setAgeMax(new GENESIS_Time(CommonFactory.newTime(aAgeMax)));
            initAgeMaxBound();
        }
    }

    /**
     * ageInYears is an age in years.
     * <code>this.ageMin = new GENESIS_Time(CommonFactory.newTime(ageInYears));</code>
     * <code>this.ageMax = new GENESIS_Time(CommonFactory.newTime(ageInYears + 1));</code>
     *
     * @param ageInYears
     */
    public GENESIS_AgeBound(Long ageInYears) {
        if (ageInYears != null) {
            setAgeMin(new GENESIS_Time(CommonFactory.newTime(ageInYears)));
            setAgeMax(new GENESIS_Time(CommonFactory.newTime(ageInYears + 1L)));
            initBounds();
        }
    }

    /**
     * <code>this.ageMin = new GENESIS_Time(CommonFactory.newTime(ageInYearsMin));</code>
     * <code>this.ageMax = new GENESIS_Time(CommonFactory.newTime(ageInYearsMax + 1L));</code>
     *
     * @param ageInYearsMin
     * @param ageInYearsMax
     */
    public GENESIS_AgeBound(Long ageInYearsMin, Long ageInYearsMax) {
        if (ageInYearsMin != null) {
            setAgeMin(new GENESIS_Time(CommonFactory.newTime(ageInYearsMin)));
            initAgeMinBound();
        }
        if (ageInYearsMax != null) {
            setAgeMax(new GENESIS_Time(CommonFactory.newTime(ageInYearsMax)));
            initAgeMaxBound();
        }
    }

    private void initBounds() {
        initAgeMinBound();
        initAgeMaxBound();
    }

    private void initAgeMinBound() {
        setAgeMinBound(new GENESIS_Time(this.ageMin));
    }

    private void initAgeMaxBound() {
        setAgeMaxBound(new GENESIS_Time(this.ageMax));
    }

    @Override
    public String toString() {
        return "GENESIS_AgeBound("
                + "ageMin " + ageMin
                + ", ageMax " + ageMax
                + ", ageMinBound " + ageMinBound
                + ", ageMaxBound " + ageMaxBound + ")";
    }

    public Time getAgeMinBound() {
        if (ageMinBound == null) {
            if (ageMin != null) {
                ageMinBound = new GENESIS_Time(ageMin);
            }
        }
        return ageMinBound;
    }

    public void setAgeMinBound(Time ageMinBound) {
        this.ageMinBound = ageMinBound;
    }

    public Time getAgeMaxBound() {
        if (ageMaxBound == null) {
            if (ageMax != null) {
                ageMaxBound = new GENESIS_Time(ageMax);
            }
        }
        return ageMaxBound;
    }

    public void setAgeMaxBound(Time ageMaxBound) {
        this.ageMaxBound = ageMaxBound;
    }

    /**
     * Returns a copy of the broader GENESIS_AgeBound into which ageBound fits.
     * Th method assumes (map is non-empty and) that ageBound will fit within an
     * GENESIS_AgeBound in map.
     *
     * @param ageBound
     * @param map
     * @return
     */
    public static GENESIS_AgeBound getContainingAgeBound(
            GENESIS_AgeBound ageBound,
            TreeMap<GENESIS_AgeBound, ?> map) {
        GENESIS_AgeBound result;
        if (map.containsKey(ageBound)) {
            result = new GENESIS_AgeBound(ageBound);
//            System.out.println("ageBound " + ageBound);
//            System.out.println("containingAgeBound " + result);
            return result;
        }
        GENESIS_AgeBound higherKey = map.higherKey(ageBound);
        if (higherKey == null) {
            result = map.lastKey();
        } else {
            GENESIS_Time higherKeyAgeMinTime = new GENESIS_Time(higherKey.getAgeMin());
            GENESIS_Time ageBoundAgeMaxTime = new GENESIS_Time(ageBound.getAgeMax());
            if (higherKeyAgeMinTime.compareTo(ageBoundAgeMaxTime) == 0) {
                result = map.lowerKey(higherKey);
//                System.out.println("ageBound " + ageBound);
//                System.out.println("containingAgeBound " + result);
                return result;
            }
            GENESIS_Time ageBoundAgeMinTime = new GENESIS_Time(ageBound.getAgeMin());
            if (higherKeyAgeMinTime.compareTo(ageBoundAgeMinTime) == 0) {
                result = new GENESIS_AgeBound(higherKey);
//                System.out.println("ageBound " + ageBound);
//                System.out.println("containingAgeBound " + result);
                return result;
            }
            result = map.lowerKey(higherKey);
        }
//        System.out.println("ageBound " + ageBound);
//        System.out.println("containingAgeBound " + result);
        return result;
    }

    /**
     * Two GENESIS_AgeBounds are equivalent if all their fields are. Order is
     * based primarily on ageMin. If this ageMin is higher than that of o then
     * the result is 1. If ageMin are the same, then the order is based on
     * ageMax. If ageMax are the same, the order is based on ageMinBound. If
     * ageMinBound are still the same, the order is based on ageMaxBound. If
     * ageMaxBound are the same the result is 0.
     *
     * @param o
     * @return 0, 1 or -1 if this is the same as greater than or less than o
     * @TODO Optimise? This is used a great deal in population simulation...
     */
// This comparison compares also the bounds.
//    @Override
//    public int compareTo(Object o) {
//        if (o instanceof GENESIS_AgeBound) {
//            GENESIS_AgeBound oGENESIS_AgeBound = (GENESIS_AgeBound) o;
//            if (getAgeMin() == null) {
//                if (oGENESIS_AgeBound.getAgeMin() == null) {
//                    if (getAgeMax() == null) {
//                        if (oGENESIS_AgeBound.getAgeMax() == null) {
//                            return 0;
//                        } else {
//                            return -1;
//                        }
//                    }
//                } else {
//                    return 1;
//                }
//            }
//            if (oGENESIS_AgeBound.getAgeMin() == null) {
//                return -1;
//            }
//            int result = (new GENESIS_Time(getAgeMin())).compareTo(new GENESIS_Time(oGENESIS_AgeBound.getAgeMin()));
//            if (result == 0) {
//                if (getAgeMax() == null) {
//                    if (oGENESIS_AgeBound.getAgeMax() == null) {
//                        return 0;
//                    } else {
//                        return -1;
//                    }
//                }
//                result = (new GENESIS_Time(getAgeMax())).compareTo(new GENESIS_Time(oGENESIS_AgeBound.getAgeMax()));
//                if (result == 0) {
//                    // Check ageMinBound
//                    if (getAgeMinBound() == null) {
//                        if (oGENESIS_AgeBound.getAgeMinBound() == null) {
//                            if (getAgeMaxBound() == null) {
//                                if (oGENESIS_AgeBound.getAgeMaxBound() == null) {
//                                    return 0;
//                                } else {
//                                    return -1;
//                                }
//                            } else {
//                                return 1;
//                            }
//                        } else {
//                            return 1;
//                        }
//                    } else {
//                        result = (new GENESIS_Time(getAgeMinBound())).compareTo(new GENESIS_Time(oGENESIS_AgeBound.getAgeMinBound()));
//                    }
//                    if (result == 0) {
//                        // Check ageMaxBound
//                        if (getAgeMaxBound() == null) {
//                            if (oGENESIS_AgeBound.getAgeMaxBound() == null) {
//                                return 0;
//                            } else {
//                                return -1;
//                            }
//                        } else {
//                            if (oGENESIS_AgeBound.getAgeMaxBound() == null) {
//                                return 1;
//                            }
//                            result = (new GENESIS_Time(getAgeMaxBound())).compareTo(new GENESIS_Time(oGENESIS_AgeBound.getAgeMaxBound()));
//                        }
//                    }
//                }
//            }
//            return result;
//        } else {
//            System.err.println(
//                    "Trying to compare " + this.getClass().getName()
//                    + " with " + o.getClass().getName());
//        }
//        return -1;
//    }
    @Override
    public int compareTo(Object o) {
        if (o instanceof GENESIS_AgeBound) {
            GENESIS_AgeBound oGENESIS_AgeBound = (GENESIS_AgeBound) o;
            if (getAgeMin() == null) {
                if (oGENESIS_AgeBound.getAgeMin() == null) {
                    if (getAgeMax() == null) {
                        if (oGENESIS_AgeBound.getAgeMax() == null) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                } else {
                    return 1;
                }
            }
            if (oGENESIS_AgeBound.getAgeMin() == null) {
                return -1;
            }
            int result = (new GENESIS_Time(getAgeMin())).compareTo(new GENESIS_Time(oGENESIS_AgeBound.getAgeMin()));
            if (result == 0) {
                if (getAgeMax() == null) {
                    if (oGENESIS_AgeBound.getAgeMax() == null) {
                        result = 0;
                    } else {
                        result = -1;
                    }
                } else {
                    if (oGENESIS_AgeBound.getAgeMax() == null) {
                        result = 1;
                    } else {
                        result = (new GENESIS_Time(getAgeMax())).compareTo(new GENESIS_Time(oGENESIS_AgeBound.getAgeMax()));
                    }
                }
            }
            return result;
        } else {
            System.err.println(
                    "Trying to compare " + this.getClass().getName()
                    + " with " + o.getClass().getName());
        }
        return -1;
    }

    /**
     * Overrides equals in Object. Two GENESIS_AgeBounds are equal if both
     * ageMin and ageMax are the same.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        GENESIS_AgeBound oGENESIS_AgeBound = (GENESIS_AgeBound) o;
        if (getAgeMin() == null) {
            if (oGENESIS_AgeBound.getAgeMin() == null) {
                if (getAgeMax() == null) {
                    if (oGENESIS_AgeBound.getAgeMax() == null) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        if (oGENESIS_AgeBound.getAgeMin() == null) {
            return false;
        }
        int compare = (new GENESIS_Time(getAgeMin())).compareTo(new GENESIS_Time(oGENESIS_AgeBound.getAgeMin()));
        if (compare == 0) {
            if (getAgeMax() == null) {
                if (oGENESIS_AgeBound.getAgeMax() == null) {
                    return true;
                } else {
                    return false;
                }
            }
            compare = (new GENESIS_Time(getAgeMax())).compareTo(new GENESIS_Time(oGENESIS_AgeBound.getAgeMax()));
            if (compare == 0) {
                return true;
            }
        }
        return false;
    }
////    // For hashCode code generation
////    public Time ageMin;
////    public Time ageMax;
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 23 * hash + (this.ageMinBound != null ? this.ageMinBound.hashCode() : 0);
//        hash = 23 * hash + (this.ageMaxBound != null ? this.ageMaxBound.hashCode() : 0);
//        hash = 23 * hash + (this.ageMin != null ? this.ageMin.hashCode() : 0);
//        hash = 23 * hash + (this.ageMax != null ? this.ageMax.hashCode() : 0);
//        return hash;
//    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.ageMin != null ? this.ageMin.hashCode() : 0);
        hash = 11 * hash + (this.ageMax != null ? this.ageMax.hashCode() : 0);
        return hash;
    }
}
