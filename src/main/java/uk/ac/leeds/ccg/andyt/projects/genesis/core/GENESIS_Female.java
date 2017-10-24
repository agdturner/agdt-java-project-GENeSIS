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
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.GENESIS_Family;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.GENESIS_Household;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

public class GENESIS_Female extends GENESIS_Person {

    /**
     * Gestation period in humans taken as 266 days
 http://en.wikipedia.org/wiki/Gestation_period If TimeDueToGiveBirth !=
 null then female is pregnant
     */
    public static final int NormalGestationPeriod_int = 266;
    public static final BigDecimal NormalGestationPeriod_BigDecimal = new BigDecimal("266");
    /**
     * A reference to the main GENESIS_FemaleCollection to which this belongs.
     */
    protected transient GENESIS_FemaleCollection FemaleCollection;
    /**
     * The length of this indicates if a single baby birth is expected or if
     * there are twins or other multiple births. It also indicates the gender at
     * birth.
     */
    public boolean[] GenderOfUnborns;
    
    /**
     * Expected due date. If this is set then isPregnant() returns true.
     */
    public GENESIS_Time TimeDueToGiveBirth;
    /**
     * For storing information about miscarriages.
     */
    //public HashSet Miscarriages;
    public TreeSet<Miscarriage> Miscarriages;
    public int LengthOfFertilityPeriod;
    public int StartOfFertiliyPeriod;

    private GENESIS_Female() {
        LogManager.getLogManager().addLogger(Logger.getLogger(GENESIS_Log.DefaultLoggerName));
    }

    protected GENESIS_Female(
            GENESIS_Environment ge,
            GENESIS_AgentCollectionManager acm,
            GENESIS_Age age) {
        this(ge,
             acm,
             null,
             age,
             null,
             null);
    }

    protected GENESIS_Female(
            GENESIS_Environment ge,
            GENESIS_AgentCollectionManager acm,
            GENESIS_Age age,
            GENESIS_Household household) {
        this(ge,
             acm,
             null,
             age,
            household,
             household._Point2D);
    }

    protected GENESIS_Female(
            GENESIS_Environment ge,
            GENESIS_AgentCollectionManager acm,
            GENESIS_Age age,
            GENESIS_Household household,
            Vector_Point2D point2D) {
        this(ge,
             acm,
             null,
             age,
             household,
             point2D);
    }

    protected GENESIS_Female(
            GENESIS_Environment ge,
            GENESIS_AgentCollectionManager acm,
            File directory,
            GENESIS_Age age,
            GENESIS_Household household,
            Vector_Point2D point2D) {
        super(ge);
        init(acm,
             directory,
             age,
             household,
             point2D);
    }

    protected final void init(
            GENESIS_AgentCollectionManager acm,
            File directory,
            GENESIS_Age age,
            GENESIS_Household household,
            Vector_Point2D point2D) {
        LogManager.getLogManager().addLogger(Logger.getLogger(GENESIS_Log.DefaultLoggerName));
        AgentCollectionManager = acm;
        ID = acm.get_NextFemaleID(ge.HandleOutOfMemoryError);
        Type = getTypeLivingFemale_String();
        CollectionID = acm.getFemaleCollection_ID(
                ID,
                Type,
                ge.HandleOutOfMemoryError);
        ResidentialSubregionIDs = new ArrayList<>();
        Generic_StaticIO.addToArchive(
                AgentCollectionManager.getLivingFemaleDirectory(),
                AgentCollectionManager.MaximumNumberOfObjectsPerDirectory,
                CollectionID);
        this.FemaleCollection = get_FemaleCollection();
        this.FemaleCollection.getAgentID_Agent_Map().put(ID, this);
        this._Directory = directory;
        this.Age = new GENESIS_Age(ge, age);
        this._Family = new GENESIS_Family(this);
        if (_Household != null) {
            this._Household = household;
            this._Household._Add_Person(this);
        }
        if (Location != null) {
            this.Location = point2D;
            this.PreviousPoint2D = new Vector_Point2D(point2D);
        }
    }

    @Override
    public String toString(){
        String result = super.toString();
        result += ", _Time_DueToGiveBirth " + TimeDueToGiveBirth;
        if (Miscarriages != null) {
            result += ", _Miscarriages (";
            Iterator<Miscarriage> ite = Miscarriages.iterator();
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
        return ID;
    }

    @Override
    public Long getAgentID(boolean handleOutOfMemoryError) {
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
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(
                        get_FemaleCollection());
                ge.initMemoryReserve(
                        handleOutOfMemoryError);
                return getAgentID(handleOutOfMemoryError);
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
                ge.clearMemoryReserve();
                if (ge.AgentEnvironment.AgentCollectionManager.swapAgentCollection_Account() < 1) {
                    ge.swapChunk();
                }
                ge.initMemoryReserve(
                        handleOutOfMemoryError);
                return get_FemaleCollection(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected GENESIS_FemaleCollection get_FemaleCollection() {
        if (FemaleCollection == null) {
            FemaleCollection = getAgentCollectionManager().getFemaleCollection(CollectionID,
                    Type,
                    ge.HandleOutOfMemoryErrorFalse);
        }
        if (FemaleCollection.ge == null) {
            FemaleCollection.ge = ge;
        }
        return FemaleCollection;
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
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(FemaleCollection);
                ge.initMemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                return get_FemaleCollection_ID(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected Long get_FemaleCollection_ID() {
        return getAgentCollectionManager().getAgentCollection_ID(get_Female_ID());
    }

    @Override
    public int getGender(boolean handleOutOfMemoryError) {
        try {
            int result = getGender();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(
                        get_FemaleCollection());
                ge.initMemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                return GENESIS_Female.this.getGender(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Override
    protected int getGender() {
        return 0;
    }

//    public void init_Fertility(boolean handleOutOfMemoryError) {
//        try {
//            init_Fertility();
//            ge.tryToEnsureThereIsEnoughMemoryToContinue(
//                    handleOutOfMemoryError);
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                ge.clearMemoryReserve();
//                ge.AgentEnvironment.getAgentCollectionManager(
//                        ge.HandleOutOfMemoryErrorFalse).swapAgentCollection(
//                        ge.HandleOutOfMemoryErrorFalse);
//                ge.initMemoryReserve(
//                        ge.HandleOutOfMemoryErrorFalse);
//                ge.tryToEnsureThereIsEnoughMemoryToContinue(
//                        ge.HandleOutOfMemoryError);
//                init_Fertility(handleOutOfMemoryError);
//            } else {
//                throw a_OutOfMemoryError;
//            }
//        }
//    }
//
//    protected void init_Fertility() {
//        this.LengthOfFertilityPeriod = 2 + this.ge._AbstractModel._Random.nextInt(5);
//        this.StartOfFertiliyPeriod = this.ge._AbstractModel._Random.nextInt(28 - LengthOfFertilityPeriod);
//        //this._FertileDays_HashSet.add(_startOfFertiliyPeriod);
//    }
//
//    public BigDecimal get_Fertility(
//            GENESIS_Fertility _Fertility,
//            boolean handleOutOfMemoryError) {
//        try {
//            BigDecimal result = get_Fertility(_Fertility);
//            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
//            //OutOfMemoryErrorHandler.tryToEnsureThereIsEnoughMemoryToContinue();
//            return result;
//        } catch (OutOfMemoryError a_OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                ge.clearMemoryReserve();
//                ge.AgentEnvironment.getAgentCollectionManager(
//                        ge.HandleOutOfMemoryErrorFalse).swapAgentCollection(
//                        ge.HandleOutOfMemoryErrorFalse);
//                ge.initMemoryReserve(
//                        ge.HandleOutOfMemoryErrorFalse);
//                ge.tryToEnsureThereIsEnoughMemoryToContinue(
//                        ge.HandleOutOfMemoryError);
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
//////            int _Day = ge._Day;
//////            if (_Children == null){
//////                return _GeneralFertilityForAge * ;
//////            }
////        }
////        return _GeneralFertilityForAge;
//    }
    public Integer getDaysToDueDate() {
        if (TimeDueToGiveBirth == null) {
            return null;
        } else {
            try {

                // debug
                if (ge == null) {
                    int debug = 1;
                } else {
                    if (ge.Time == null) {
                        int debug = 1;
                    }
                }

                return new Integer((int) ge.Time.getDifferenceInDays_long(TimeDueToGiveBirth));
                //return new Integer((int) TimeDueToGiveBirth.getDifferenceInDays_long(ge.Time));
                //return TimeDueToGiveBirth.subtract(ge.Time);
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
                ge.clearMemoryReserve();
                ge.AgentEnvironment.get_AgentCollectionManager(
                        ge.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.initMemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(ge.HandleOutOfMemoryError);
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
        GENESIS_Time _DueDate = new GENESIS_Time(ge.Time);
        _DueDate.addDays(numberOfDaysUntilDue);
        GENESIS_Time ageCheck = new GENESIS_Time(this.getCopyOfAge().getAge_Time(_DueDate));
        GENESIS_AgeBound ageBoundCheck = new GENESIS_AgeBound(ageCheck.getYear());
        if (a_Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap.containsKey(ageBoundCheck)) {
            this.TimeDueToGiveBirth = _DueDate;
            result = true;
        } else {
            // At this age the female is set to not be pregnant.
            this.TimeDueToGiveBirth = null;
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
                ge.clearMemoryReserve();
                ge.AgentEnvironment.get_AgentCollectionManager(
                        ge.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.initMemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(ge.HandleOutOfMemoryError);
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
        GENESIS_Time _DueDate = new GENESIS_Time(ge.Time);
        //Fertility a_Fertility = ((DemographicModel_Aspatial_1)ge._AbstractModel)._Demographics._Fertility;
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
//        HashMap<Integer, BigDecimal> probabilityOfTwins = ((DemographicModel_Aspatial_1) ge._AbstractModel)._Fertility._ProbabilityOfTwins_HashMap;
//        Integer age_Integer = get_AgeInYears_int();
//        BigDecimal probability = probabilityOfTwins.get(age_Integer);
//        // Test
        GenderOfUnborns = new boolean[numberOfBabies];
        for (int i = 0; i < numberOfBabies; i++) {
            if (ge._AbstractModel.get_Random(12).nextBoolean()) {
                GenderOfUnborns[i] = false;
            } else {
                GenderOfUnborns[i] = true;
            }
        }
        for (int i = 0; i < NormalGestationPeriod_int; i++) {
            _DueDate.addDay();
        }
        
        // debug
        if (this.getAgentID(false) == 58061){ 
            int debug = 1;
        }
        
        
        this.TimeDueToGiveBirth = _DueDate;
    }

    public boolean isPregnant(boolean handleOutOfMemoryError) {
        try {
            boolean result = isPregnant();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            //OutOfMemoryErrorHandler.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.AgentEnvironment.get_AgentCollectionManager(
                        ge.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.initMemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(ge.HandleOutOfMemoryError);
                return isPregnant(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected boolean isPregnant() {
        return TimeDueToGiveBirth != null;
    }

    public void init_Miscarriages() {
        Miscarriages = new TreeSet<Miscarriage>();
    }

    public void miscarriage() {
        Miscarriage a_Miscarriage = new Miscarriage(
                ge.Time,
                TimeDueToGiveBirth,
                GenderOfUnborns);
        if (Miscarriages == null) {
            init_Miscarriages();
        }
        Miscarriages.add(a_Miscarriage);
        TimeDueToGiveBirth = null;
        GenderOfUnborns = null;
    }

//    /**
//     *  This resets DueDate creates and returns a new GENESIS_Male or GENESIS_Female GENESIS_Person.
//     */
//    @Deprecated
//    public GENESIS_Person _Gives_Birth() {
//        GENESIS_Person result;
//        TimeDueToGiveBirth = null;
//        // Equal probabilty of male and female to start
//        if (ge._AbstractModel._Random.nextBoolean()) {
//            result = new GENESIS_Male(
//                    ge,
//                    ge.AgentEnvironment.getAgentCollectionManager(
//                    ge.HandleOutOfMemoryErrorFalse),
//                     new GENESIS_Age(ge,ge.Time),
//                       _Household,
//                    Location);
//        } else {
//            result = new GENESIS_Female(
//                    ge,
//                    ge.AgentEnvironment.getAgentCollectionManager(
//                    ge.HandleOutOfMemoryErrorFalse),
//                     new GENESIS_Age(ge,ge.Time),
//                       _Household,
//                    Location);
//        }
//        // @TODO Add to household and set family relationships up...
//        _Household._Add_Person(result);
//        return result;
//    }
    /**
     * Sets TimeDueToGiveBirth and GenderOfUnborns to null. Creates new
 people given GenderOfUnborns and returns an Object with three elements:
 HashSet<Long> babyGirl_IDs, HashSet<Long> babyBoy_IDs, Integer of the
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
                ge.clearMemoryReserve();
                ge.AgentEnvironment.get_AgentCollectionManager(
                        ge.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.initMemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(ge.HandleOutOfMemoryError);
                return giveBirthSimple(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Sets TimeDueToGiveBirth and GenderOfUnborns to null. Creates new
 people given GenderOfUnborns and returns an Object with three elements:
 HashSet<Long> babyGirl_IDs, HashSet<Long> babyBoy_IDs, Integer of the
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
                ge.clearMemoryReserve();
                ge.AgentEnvironment.get_AgentCollectionManager(
                        ge.HandleOutOfMemoryErrorFalse).swapToFile_AgentCollection(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.initMemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                ge.tryToEnsureThereIsEnoughMemoryToContinue(ge.HandleOutOfMemoryError);
                return giveBirthSimpleOld(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Sets TimeDueToGiveBirth and GenderOfUnborns to null. Creates new
 people given GenderOfUnborns and returns an Object with three elements:
 HashSet<Long> babyGirl_IDs, HashSet<Long> babyBoy_IDs, Integer of the
     * number of babies born.
     * @return 
     */
    protected Object[] giveBirthSimple() {
        Object[] result = new Object[3];
        HashSet<Long> babyGirl_IDs = new HashSet<Long>();
        HashSet<Long> babyBoy_IDs = new HashSet<Long>();
        GENESIS_FemaleCollection a_GENESIS_FemaleCollection =
                get_FemaleCollection();
        if (GenderOfUnborns.length == 1) {
            //_GENESIS_Environment_AbstractModel._GENESIS_Log.log("Single Birth");
            result[2] = 1;
        } else {
            if (GenderOfUnborns.length == 2) {
                //_GENESIS_Environment_AbstractModel._GENESIS_Log.log("Twin Birth");
                result[2] = 2;
            } else {
                //_GENESIS_Environment_AbstractModel._GENESIS_Log.log("Triplet Birth");
                result[2] = 3;
            }
        }
        for (int i = 0; i < GenderOfUnborns.length; i++) {
            if (GenderOfUnborns[i]) {
                GENESIS_Male babyBoy = ge._PersonFactory.createMale(new GENESIS_Age(ge, ge.Time),
                        _Household,
                        Location,
                        a_GENESIS_FemaleCollection,
                        ge.HandleOutOfMemoryErrorFalse);
                Long agent_ID = babyBoy.getAgentID(false);
                babyBoy._Family.set_Mother(ID);
                babyBoy.ResidentialSubregionIDs.add(getSubregionID());
                this._Family.add_Child(agent_ID);
                babyBoy_IDs.add(agent_ID);
            } else {
                GENESIS_Female babyGirl = ge._PersonFactory.createFemale(new GENESIS_Age(ge, ge.Time),
                        _Household,
                        Location,
                        a_GENESIS_FemaleCollection,
                        ge.HandleOutOfMemoryErrorFalse);
                babyGirl._Family.set_Mother(ID);
                babyGirl.ResidentialSubregionIDs.add(getSubregionID());
                long agent_ID = babyGirl.getAgentID(false);
                this._Family.add_Child(agent_ID);
                babyGirl_IDs.add(agent_ID);
            }
        }
        TimeDueToGiveBirth = null;
        GenderOfUnborns = null;
        result[0] = babyGirl_IDs;
        result[1] = babyBoy_IDs;
        return result;
    }

    /**
     * Sets TimeDueToGiveBirth and GenderOfUnborns to null. Creates and
     * returns a GENESIS_Person[] of newborns.
     *
     * @return Array containing newborns
     */
    @Deprecated
    protected GENESIS_Person[] giveBirthSimpleOld() {
        GENESIS_Person[] result = new GENESIS_Person[GenderOfUnborns.length];
        for (int i = 0; i < GenderOfUnborns.length; i++) {
            if (GenderOfUnborns[i]) {
                result[i] = ge._PersonFactory.createMale(new GENESIS_Age(ge, ge.Time),
                        _Household,
                        Location,
                        get_FemaleCollection(),
                        ge.HandleOutOfMemoryErrorFalse);
            } else {
                result[i] = ge._PersonFactory.createFemale(new GENESIS_Age(ge, ge.Time),
                        _Household,
                        Location,
                        get_FemaleCollection(),
                        ge.HandleOutOfMemoryErrorFalse);
            }
            result[i]._Family.set_Mother(ID);
            this._Family.add_Child(result[i].getAgentID(false));
        }
        TimeDueToGiveBirth = null;
        GenderOfUnborns = null;
        return result;
    }

    @Deprecated
    public static GENESIS_Female read(
            long a_Agent_ID,
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection) {
        GENESIS_Female result;
        GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
                a_GENESIS_FemaleCollection.getAgentCollectionManager();
        File a_FemaleDirectory_File = Generic_StaticIO.getObjectDirectory(a_GENESIS_AgentCollectionManager._LivingFemaleDirectory,
                a_Agent_ID,
                a_GENESIS_AgentCollectionManager._IndexOfLastBornFemale,
                a_GENESIS_AgentCollectionManager.MaximumNumberOfObjectsPerDirectory);
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
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(
                        get_FemaleCollection());
                ge.initMemoryReserve(handleOutOfMemoryError);
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
                getDirectory(),
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
    protected File getDirectory() {
        if (_Directory == null) {
            _Directory = AgentCollectionManager.getLivingFemaleDirectory();
        }
        return _Directory;
    }

    public class Miscarriage
            implements Serializable, Comparable {

        /**
         * GENESIS_Time of miscarriage
         */
        public GENESIS_Time Time;
        /**
         * Due Date GENESIS_Time of miscarriage
         */
        public GENESIS_Time DueDate;
        /**
         * Due Date GENESIS_Time of miscarriage
         */
        public boolean[] GenderOfUnborns;

        public Miscarriage(
                GENESIS_Time time,
                GENESIS_Time dueDate,
                boolean[] genderOfUnborns) {
            this.Time = new GENESIS_Time(time);
            this.DueDate = new GENESIS_Time(dueDate);
            this.GenderOfUnborns = new boolean[genderOfUnborns.length];
            System.arraycopy(genderOfUnborns, 0, GenderOfUnborns, 0, genderOfUnborns.length);
        }

        @Override
        public String toString() {
            String result = "Time " + Time;
            result += "DueDate " + DueDate;
            for (int i = 0; i < GenderOfUnborns.length; i ++) {
                result += "GenderOfUnborn[" + i + "] " + GenderOfUnborns[i];
            }
            return result;
        }
        
        @Override
        public int compareTo(Object o) {
            return Time.compareTo(((Miscarriage) o).Time);
            // throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
