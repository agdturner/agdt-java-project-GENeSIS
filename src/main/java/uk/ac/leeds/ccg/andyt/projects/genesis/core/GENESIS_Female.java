package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Fertility;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.Family;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.Household;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

public class GENESIS_Female extends GENESIS_Person {

    /**
     * Gestation period in humans taken as 266 days
     * http://en.wikipedia.org/wiki/Gestation_period If _Time_DueToGiveBirth !=
     * null then female is pregnant
     */
    public static final int NormalGestationPeriod_int = 266;
    public static final BigDecimal NormalGestationPeriod_BigDecimal = new BigDecimal("266");
    /**
     * A reference to the main GENESIS_FemaleCollection to which this belongs
     */
    protected transient GENESIS_FemaleCollection _GENESIS_FemaleCollection;
    /**
     * The length of this indicates if a single baby birth is expected or if
     * there are twins or other multiple births. It also indicates the gender at
     * birth.
     */
    public int[] _GenderOfUnborns;
    /**
     * Expected due date. If this is set then isPregnant() returns true.
     */
    public GENESIS_Time _Time_DueToGiveBirth;
    /**
     * For storing information about miscarriages.
     */
    //public HashSet _Miscarriages;
    public TreeSet<Miscarriage> _Miscarriages;
    public int _LengthOfFertilityPeriod;
    public int _StartOfFertiliyPeriod;

    private GENESIS_Female() {
        LogManager.getLogManager().addLogger(Logger.getLogger(GENESIS_Log.GENESIS_DefaultLoggerName));
    }

    protected GENESIS_Female(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager,
            GENESIS_Age age) {
        this(a_GENESIS_Environment,
             a_GENESIS_AgentCollectionManager,
             null,
             age,
             null,
             null);
    }

    protected GENESIS_Female(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager,
            GENESIS_Age age,
            Household household) {
        this(a_GENESIS_Environment,
             a_GENESIS_AgentCollectionManager,
             null,
             age,
            household,
             household._Point2D);
    }

    protected GENESIS_Female(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager,
            GENESIS_Age age,
            Household household,
            Vector_Point2D point2D) {
        this(a_GENESIS_Environment,
             a_GENESIS_AgentCollectionManager,
             null,
             age,
             household,
             point2D);
    }

    protected GENESIS_Female(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager,
            File directory,
            GENESIS_Age age,
            Household household,
            Vector_Point2D point2D) {
        init(
              a_GENESIS_Environment,
             a_GENESIS_AgentCollectionManager,
             _Directory,
             age,
             household,
             point2D);
    }

    protected final void init(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager,
            File directory,
            GENESIS_Age age,
            Household household,
            Vector_Point2D point2D) {
        LogManager.getLogManager().addLogger(Logger.getLogger(GENESIS_Log.GENESIS_DefaultLoggerName));
        ge = a_GENESIS_Environment;
        _GENESIS_AgentCollectionManager = a_GENESIS_AgentCollectionManager;
        _ID = a_GENESIS_AgentCollectionManager.get_NextFemaleID(
                ge._HandleOutOfMemoryError_boolean);
        _Type = getTypeLivingFemale_String();
        _Collection_ID = a_GENESIS_AgentCollectionManager.getFemaleCollection_ID(
                _ID,
                _Type,
                ge._HandleOutOfMemoryError_boolean);
        this._ResidentialSubregionIDs = new ArrayList<String>();
        Generic_StaticIO.addToArchive(
                _GENESIS_AgentCollectionManager.getLivingFemaleDirectory(),
                _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory,
                _Collection_ID);
        this._GENESIS_FemaleCollection = get_FemaleCollection();
        this._GENESIS_FemaleCollection.get_Agent_ID_Agent_HashMap().put(_ID, this);
        this._Directory = directory;
        this._Age = new GENESIS_Age(age, a_GENESIS_Environment);
        this._Family = new Family(this);
        if (_Household != null) {
            this._Household = household;
            this._Household._Add_Person(this);
        }
        if (_Point2D != null) {
            this._Point2D = point2D;
            this._Previous_Point2D = new Vector_Point2D(point2D);
        }
    }

    @Override
    public String toString(){
        String result = super.toString();
        result += ", _Time_DueToGiveBirth " + _Time_DueToGiveBirth;
        if (_Miscarriages != null) {
            result += ", _Miscarriages (";
            Iterator<Miscarriage> ite = _Miscarriages.iterator();
            while (ite.hasNext()) {
                Miscarriage m = ite.next();
                result += m.toString();
            }
            result += ")";
        }
        return result;
    }
    /**
     * @return A copy of this._Agent_ID
     */
    protected Long get_Female_ID() {
        return _ID;
    }

    @Override
    public Long get_Agent_ID(boolean handleOutOfMemoryError) {
        try {
            Long result = get_Female_ID();
            GENESIS_FemaleCollection a_FemaleCollection =
                    get_FemaleCollection();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_FemaleCollection,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                ge.swapToFile_DataAnyExcept(
                        get_FemaleCollection());
                ge.init_MemoryReserve(
                        handleOutOfMemoryError);
                return get_Agent_ID(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Override
    protected GENESIS_AgentCollection get_AgentCollection() {
        return get_FemaleCollection();
    }

    public GENESIS_FemaleCollection get_FemaleCollection(
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_FemaleCollection result = get_FemaleCollection();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    result,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge._GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollection_Account() < 1) {
                    ge.swapToFile_Grid2DSquareCellChunk();
                }
                ge.init_MemoryReserve(
                        handleOutOfMemoryError);
                return get_FemaleCollection(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected GENESIS_FemaleCollection get_FemaleCollection() {
        if (_GENESIS_FemaleCollection == null) {
            _GENESIS_FemaleCollection = get_AgentCollectionManager().getFemaleCollection(
                    _Collection_ID,
                    _Type,
                    ge.HandleOutOfMemoryErrorFalse);
        }
        if (_GENESIS_FemaleCollection.ge == null) {
            _GENESIS_FemaleCollection.ge = ge;
        }
        return _GENESIS_FemaleCollection;
    }

    public Long get_FemaleCollection_ID(boolean handleOutOfMemoryError) {
        try {
            Long result = get_FemaleCollection_ID();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    get_FemaleCollection(),
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                ge.swapToFile_DataAnyExcept(
                        _GENESIS_FemaleCollection);
                ge.init_MemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                return get_FemaleCollection_ID(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected Long get_FemaleCollection_ID() {
        return get_AgentCollectionManager().getAgentCollection_ID(get_Female_ID());
    }

    @Override
    public int get_Gender(boolean handleOutOfMemoryError) {
        try {
            int result = get_Gender();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                ge.swapToFile_DataAnyExcept(
                        get_FemaleCollection());
                ge.init_MemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                return get_Gender(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Override
    protected int get_Gender() {
        return 0;
    }

//    public void init_Fertility(boolean handleOutOfMemoryError) {
//        try {
//            init_Fertility();
//            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                    handleOutOfMemoryError);
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                _GENESIS_Environment.clear_MemoryReserve();
//                _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
//                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
//                _GENESIS_Environment.init_MemoryReserve(
//                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
//                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                init_Fertility(handleOutOfMemoryError);
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
//
//    protected void init_Fertility() {
//        this._LengthOfFertilityPeriod = 2 + this._GENESIS_Environment._AbstractModel._Random.nextInt(5);
//        this._StartOfFertiliyPeriod = this._GENESIS_Environment._AbstractModel._Random.nextInt(28 - _LengthOfFertilityPeriod);
//        //this._FertileDays_HashSet.add(_startOfFertiliyPeriod);
//    }
//
//    public BigDecimal get_Fertility(
//            GENESIS_Fertility _Fertility,
//            boolean handleOutOfMemoryError) {
//        try {
//            BigDecimal result = get_Fertility(_Fertility);
//            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
//            //OutOfMemoryErrorHandler.tryToEnsureThereIsEnoughMemoryToContinue();
//            return result;
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                _GENESIS_Environment.clear_MemoryReserve();
//                _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
//                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
//                _GENESIS_Environment.init_MemoryReserve(
//                        _GENESIS_Environment.HandleOutOfMemoryErrorFalse);
//                _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                return get_Fertility(
//                        _Fertility,
//                        handleOutOfMemoryError);
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
//
//    protected BigDecimal get_Fertility(GENESIS_Fertility a_Fertility) {
//        return a_Fertility.getDailyPregnancyRate(this);
////        //double _GeneralFertilityForAge = _Fertility.get_Fertility_Female(get_AgeInYears_int(_Calendar_Birth));
////        BigDecimal _GeneralFertilityForAge = a_Fertility.getDailyPregnancyRateProbabilty(
////                get_AgeInYears_int(_Birth_Time));
////        if (this.isPregnant()) {
////            return new BigDecimal("0.0");
////        } else {
//////            HashSet _Children = this._Family.get_Children();
//////            int _Day = _GENESIS_Environment._Day;
//////            if (_Children == null){
//////                return _GeneralFertilityForAge * ;
//////            }
////        }
////        return _GeneralFertilityForAge;
//    }
    public Integer getDaysToDueDate() {
        if (_Time_DueToGiveBirth == null) {
            return null;
        } else {
            try {

                // debug
                if (ge == null) {
                    int debug = 1;
                } else {
                    if (ge._Time == null) {
                        int debug = 1;
                    }
                }

                return new Integer((int) ge._Time.getDifferenceInDays_long(_Time_DueToGiveBirth));
                //return new Integer((int) _Time_DueToGiveBirth.getDifferenceInDays_long(_GENESIS_Environment._Time));
                //return _Time_DueToGiveBirth.subtract(_GENESIS_Environment._Time);
            } catch (IllegalArgumentException e) {
                int debug = 1;
                return null;
            }
        }

    }

    /**
     * This sets the due date assuming 266 day gestation, but that pregnancy is
     * already at day days. Sets the number and gender of unborns.
     *
     * @param handleOutOfMemoryError
     * @param numberOfDaysUntilDue
     * @return 
     * @TODO Implement multiple births
     */
    public boolean set_Pregnant(
            GENESIS_Fertility a_Fertility,
            int numberOfDaysUntilDue,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = set_Pregnant(
                    a_Fertility,
                    numberOfDaysUntilDue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            //OutOfMemoryErrorHandler.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
                        ge.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.init_MemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        ge._HandleOutOfMemoryError_boolean);
                return set_Pregnant(
                        a_Fertility,
                        numberOfDaysUntilDue,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * This sets the due date assuming 266 day gestation, but that pregnancy is
     * already at day days. Sets the number and gender of unborns.
     *
     * @param a_Fertility
     * @param numberOfDaysUntilDue
     * @return 
     * @TODO Implement multiple births
     */
    protected boolean set_Pregnant(
            GENESIS_Fertility a_Fertility,
            int numberOfDaysUntilDue) {
        boolean result;
        set_Pregnant(a_Fertility);
        GENESIS_Time _DueDate = new GENESIS_Time(ge._Time);
        _DueDate.addDays(numberOfDaysUntilDue);
        GENESIS_Time ageCheck = new GENESIS_Time(this.getAge().getAge_Time(_DueDate));
        GENESIS_AgeBound ageBoundCheck = new GENESIS_AgeBound(ageCheck.getYear());
        if (a_Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap.containsKey(ageBoundCheck)) {
            this._Time_DueToGiveBirth = _DueDate;
            result = true;
        } else {
            // At this age the female is set to not be pregnant.
            this._Time_DueToGiveBirth = null;
            result = false;
        }
        return result;
    }

    /**
     * This sets the due date assuming 266 day gestation. Sets the number and
     * gender of unborns.
     *
     * @param a_Fertility
     * @param handleOutOfMemoryError
     * @TODO Implement multiple births
     */
    public void set_Pregnant(
            GENESIS_Fertility a_Fertility,
            boolean handleOutOfMemoryError) {
        try {
            set_Pregnant(a_Fertility);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            //OutOfMemoryErrorHandler.tryToEnsureThereIsEnoughMemoryToContinue();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
                        ge.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.init_MemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        ge._HandleOutOfMemoryError_boolean);
                set_Pregnant(
                        a_Fertility,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * This sets the due date assuming 266 day gestation. Sets the number and
     * gender of unborns.
     *
     * @param a_Fertility
     * @TODO Implement multiple births
     */
    protected void set_Pregnant(GENESIS_Fertility a_Fertility) {
        GENESIS_Time _DueDate = new GENESIS_Time(ge._Time);
        //Fertility a_Fertility = ((DemographicModel_Aspatial_1)_GENESIS_Environment._AbstractModel)._Demographics._Fertility;
        int numberOfBabies = 1;
        if (Generic_BigDecimal.randomUniformTest(
                ge._AbstractModel.get_Random(10),
                a_Fertility.getTwinProbability_BigDecimal(this),
                //a_Fertility._MultiplePregnancyTwinProbabilityScale_int,
                ge.RoundingModeForPopulationProbabilities)) {
            log("Twin pregnancy for Female " + toString());
            numberOfBabies = 2;
        } else {
            if (Generic_BigDecimal.randomUniformTest(
                    ge._AbstractModel.get_Random(11),
                    a_Fertility.getTripletProbability_BigDecimal(this),
                    //_GENESIS_Environment.DecimalPlacePrecisionForPopulationProbabilities,
                    ge.RoundingModeForPopulationProbabilities)) {
                log("Triplet pregnancy for Female " + toString());
                numberOfBabies = 3;
            }
        }
//        HashMap<Integer, BigDecimal> probabilityOfTwins = ((DemographicModel_Aspatial_1) _GENESIS_Environment._AbstractModel)._Fertility._ProbabilityOfTwins_HashMap;
//        Integer age_Integer = get_AgeInYears_int();
//        BigDecimal probability = probabilityOfTwins.get(age_Integer);
//        // Test
        _GenderOfUnborns = new int[numberOfBabies];
        for (int i = 0; i < numberOfBabies; i++) {
            if (ge._AbstractModel.get_Random(12).nextBoolean()) {
                _GenderOfUnborns[i] = 0;
            } else {
                _GenderOfUnborns[i] = 1;
            }
        }
        for (int i = 0; i < NormalGestationPeriod_int; i++) {
            _DueDate.addDay();
        }
        
        // debug
        if (this.get_Agent_ID(false) == 58061){ 
            int debug = 1;
        }
        
        
        this._Time_DueToGiveBirth = _DueDate;
    }

    public boolean isPregnant(boolean handleOutOfMemoryError) {
        try {
            boolean result = isPregnant();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            //OutOfMemoryErrorHandler.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
                        ge.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.init_MemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        ge._HandleOutOfMemoryError_boolean);
                return isPregnant(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected boolean isPregnant() {
        return _Time_DueToGiveBirth != null;
    }

    public void init_Miscarriages() {
        _Miscarriages = new TreeSet<Miscarriage>();
    }

    public void miscarriage() {
        Miscarriage a_Miscarriage = new Miscarriage(
                ge._Time,
                _Time_DueToGiveBirth,
                _GenderOfUnborns);
        if (_Miscarriages == null) {
            init_Miscarriages();
        }
        _Miscarriages.add(a_Miscarriage);
        _Time_DueToGiveBirth = null;
        _GenderOfUnborns = null;
    }

//    /**
//     *  This resets _DueDate creates and returns a new GENESIS_Male or GENESIS_Female GENESIS_Person.
//     */
//    @Deprecated
//    public GENESIS_Person _Gives_Birth() {
//        GENESIS_Person result;
//        _Time_DueToGiveBirth = null;
//        // Equal probabilty of male and female to start
//        if (_GENESIS_Environment._AbstractModel._Random.nextBoolean()) {
//            result = new GENESIS_Male(
//                    _GENESIS_Environment,
//                    _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    _GENESIS_Environment.HandleOutOfMemoryErrorFalse),
//                     new GENESIS_Age(_GENESIS_Environment,_GENESIS_Environment._Time),
//                       _Household,
//                    _Point2D);
//        } else {
//            result = new GENESIS_Female(
//                    _GENESIS_Environment,
//                    _GENESIS_Environment._GENESIS_AgentEnvironment.get_AgentCollectionManager(
//                    _GENESIS_Environment.HandleOutOfMemoryErrorFalse),
//                     new GENESIS_Age(_GENESIS_Environment,_GENESIS_Environment._Time),
//                       _Household,
//                    _Point2D);
//        }
//        // @TODO Add to household and set family relationships up...
//        _Household._Add_Person(result);
//        return result;
//    }
    /**
     * Sets _Time_DueToGiveBirth and _GenderOfUnborns to null. Creates new
     * people given _GenderOfUnborns and returns an Object with three elements:
     * HashSet<Long> babyGirl_IDs, HashSet<Long> babyBoy_IDs, Integer of the
     * number of babies born.
     * @param handleOutOfMemoryError
     * @return 
     */
    public Object[] giveBirthSimple(boolean handleOutOfMemoryError) {
        try {
            Object[] result = giveBirthSimple();
            //OutOfMemoryErrorHandler.tryToEnsureThereIsEnoughMemoryToContinue();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
                        ge.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.init_MemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        ge._HandleOutOfMemoryError_boolean);
                return giveBirthSimple(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Sets _Time_DueToGiveBirth and _GenderOfUnborns to null. Creates new
     * people given _GenderOfUnborns and returns an Object with three elements:
     * HashSet<Long> babyGirl_IDs, HashSet<Long> babyBoy_IDs, Integer of the
     * number of babies born.
     * @param handleOutOfMemoryError
     * @return 
     */
    @Deprecated
    public GENESIS_Person[] giveBirthSimpleOld(boolean handleOutOfMemoryError) {
        try {
            GENESIS_Person[] result = giveBirthSimpleOld();
            //OutOfMemoryErrorHandler.tryToEnsureThereIsEnoughMemoryToContinue();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
                        ge.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.init_MemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(
                        ge._HandleOutOfMemoryError_boolean);
                return giveBirthSimpleOld(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Sets _Time_DueToGiveBirth and _GenderOfUnborns to null. Creates new
     * people given _GenderOfUnborns and returns an Object with three elements:
     * HashSet<Long> babyGirl_IDs, HashSet<Long> babyBoy_IDs, Integer of the
     * number of babies born.
     * @return 
     */
    protected Object[] giveBirthSimple() {
        Object[] result = new Object[3];
        HashSet<Long> babyGirl_IDs = new HashSet<Long>();
        HashSet<Long> babyBoy_IDs = new HashSet<Long>();
        GENESIS_FemaleCollection a_GENESIS_FemaleCollection =
                get_FemaleCollection();
        if (_GenderOfUnborns.length == 1) {
            //_GENESIS_Environment_AbstractModel._GENESIS_Log.log("Single Birth");
            result[2] = 1;
        } else {
            if (_GenderOfUnborns.length == 2) {
                //_GENESIS_Environment_AbstractModel._GENESIS_Log.log("Twin Birth");
                result[2] = 2;
            } else {
                //_GENESIS_Environment_AbstractModel._GENESIS_Log.log("Triplet Birth");
                result[2] = 3;
            }
        }
        for (int i = 0; i < _GenderOfUnborns.length; i++) {
            if (_GenderOfUnborns[i] == 0) {
                GENESIS_Female babyGirl = ge._PersonFactory.createFemale(
                        new GENESIS_Age(ge, ge._Time),
                        _Household,
                        _Point2D,
                        a_GENESIS_FemaleCollection,
                        ge.HandleOutOfMemoryErrorFalse);
                babyGirl._Family.set_Mother(_ID);
                babyGirl._ResidentialSubregionIDs.add(getSubregionID());
                long agent_ID = babyGirl.get_Agent_ID(false);
                this._Family.add_Child(agent_ID);
                babyGirl_IDs.add(agent_ID);
            } else {
                GENESIS_Male babyBoy = ge._PersonFactory.createMale(
                        new GENESIS_Age(ge, ge._Time),
                        _Household,
                        _Point2D,
                        a_GENESIS_FemaleCollection,
                        ge.HandleOutOfMemoryErrorFalse);
                Long agent_ID = babyBoy.get_Agent_ID(false);
                babyBoy._Family.set_Mother(_ID);
                babyBoy._ResidentialSubregionIDs.add(getSubregionID());
                this._Family.add_Child(agent_ID);
                babyBoy_IDs.add(agent_ID);
            }
        }
        _Time_DueToGiveBirth = null;
        _GenderOfUnborns = null;
        result[0] = babyGirl_IDs;
        result[1] = babyBoy_IDs;
        return result;
    }

    /**
     * Sets _Time_DueToGiveBirth and _GenderOfUnborns to null. Creates and
     * returns a GENESIS_Person[] of newborns.
     *
     * @return Array containing newborns
     */
    @Deprecated
    protected GENESIS_Person[] giveBirthSimpleOld() {
        GENESIS_Person[] result = new GENESIS_Person[_GenderOfUnborns.length];
        for (int i = 0; i < _GenderOfUnborns.length; i++) {
            if (_GenderOfUnborns[i] == 0) {
                result[i] = ge._PersonFactory.createFemale(
                        new GENESIS_Age(ge, ge._Time),
                        _Household,
                        _Point2D,
                        get_FemaleCollection(),
                        ge.HandleOutOfMemoryErrorFalse);
            } else {
                result[i] = ge._PersonFactory.createMale(
                        new GENESIS_Age(ge, ge._Time),
                        _Household,
                        _Point2D,
                        get_FemaleCollection(),
                        ge.HandleOutOfMemoryErrorFalse);
            }
            result[i]._Family.set_Mother(_ID);
            this._Family.add_Child(result[i].get_Agent_ID(false));
        }
        _Time_DueToGiveBirth = null;
        _GenderOfUnborns = null;
        return result;
    }

    @Deprecated
    public static GENESIS_Female read(
            long a_Agent_ID,
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection) {
        GENESIS_Female result;
        GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                a_GENESIS_FemaleCollection.get_AgentCollectionManager();
        File a_FemaleDirectory_File = Generic_StaticIO.getObjectDirectory(
                a_GENESIS_AgentCollectionManager._LivingFemaleDirectory,
                a_Agent_ID,
                a_GENESIS_AgentCollectionManager._IndexOfLastBornFemale,
                a_GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory);
        File a_Female_File = new File(
                a_FemaleDirectory_File,
                GENESIS_Female.class.getCanonicalName() + ".thisFile");
        return (GENESIS_Female) Generic_StaticIO.readObject(a_Female_File);
    }

    @Deprecated
    @Override
    public void write(boolean handleOutOfMemoryError) {
        try {
            write();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                ge.swapToFile_DataAnyExcept(
                        get_FemaleCollection());
                ge.init_MemoryReserve(handleOutOfMemoryError);
                write(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Deprecated
    @Override
    protected void write() {
        File thisAgentFile = new File(
                get_Directory(),
                this.getClass().getCanonicalName() + ".thisFile");
        if (!_Directory.exists()) {
            _Directory.mkdirs();
        }
        Generic_StaticIO.writeObject(
                this,
                thisAgentFile);
    }

    @Override
    @Deprecated
    protected File get_Directory() {
        if (_Directory == null) {
            _Directory = _GENESIS_AgentCollectionManager.getLivingFemaleDirectory();
        }
        return _Directory;
    }

    public class Miscarriage
            implements Serializable, Comparable {

        /**
         * GENESIS_Time of miscarriage
         */
        public GENESIS_Time _Time;
        /**
         * Due Date GENESIS_Time of miscarriage
         */
        public GENESIS_Time _DueDate;
        /**
         * Due Date GENESIS_Time of miscarriage
         */
        public int[] _GenderOfUnborns;

        public Miscarriage(
                GENESIS_Time time,
                GENESIS_Time dueDate,
                int[] genderOfUnborns) {
            this._Time = new GENESIS_Time(time);
            this._DueDate = new GENESIS_Time(dueDate);
            this._GenderOfUnborns = new int[genderOfUnborns.length];
            System.arraycopy(genderOfUnborns, 0, _GenderOfUnborns, 0, genderOfUnborns.length);
        }

        @Override
        public String toString() {
            String result = "_Time " + _Time;
            result += "_DueDate " + _DueDate;
            for (int i = 0; i < _GenderOfUnborns.length; i ++) {
                result += "_GenderOfUnborn[" + i + "] " + _GenderOfUnborns[i];
            }
            return result;
        }
        
        @Override
        public int compareTo(Object o) {
            return _Time.compareTo(((Miscarriage) o)._Time);
            // throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
