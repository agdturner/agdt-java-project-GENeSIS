package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.*;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.XMLConverter;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.population.PopulationType;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.math.statistics.GENESIS_Statistics;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Collections;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.projects.genesis.visualisation.GENESIS_AgeGenderBarChart;

public class GENESIS_Demographics implements Serializable {

    static final long serialVersionUID = 1L;
    /**
     * Used for Logging
     */
    private static final String sourceClass = GENESIS_Demographics.class.getName();
    private static final String sourcePackage = GENESIS_Demographics.class.getPackage().getName();
    public transient GENESIS_Environment _GENESIS_Environment;
    //public transient Abstract_GENESIS_DemographicModel _DemographicModel;
    /**
     * For storing region populations. Keys are LAD codes, value keys are either
     * OA codes or LAD codes for LAD totals.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Population>> _Population;
    /**
     * For storing region mortality probabilities. Keys are LAD codes, value
     * keys are either OA codes or LAD codes for LAD totals.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Mortality>> _Mortality;
    /**
     * For storing death counts. Keys are LAD codes, value keys are either OA
     * codes or LAD codes for LAD totals.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Population>> _Deaths;
    /**
     * For storing region miscarriage probabilities. Keys are LAD codes, value
     * keys are either OA codes or LAD codes for LAD totals.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Miscarriage>> _Miscarriage;
    /**
     * For storing clinical miscarriage counts by age of mother. Keys are LAD
     * codes, value keys are either OA codes or LAD codes for LAD totals.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Population>> _ClinicalMiscarriage;
    /**
     * For storing total early pregnancy loss miscarriage counts by age of
     * mother. Keys are LAD codes, value keys are either OA codes or LAD codes
     * for LAD totals.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Population>> _EarlyPregnancyLoss;
    /**
     * For storing region fertility probabilities. Keys are LAD codes, value
     * keys are either OA codes or LAD codes for LAD totals.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Fertility>> _Fertility;
    /**
     * For storing single birth counts by age of mother. Keys are LAD codes,
     * value keys are either OA codes or LAD codes for LAD totals.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Population>> _SingleBirths;
    /**
     * For storing twin birth counts by age of mother. Keys are LAD codes, value
     * keys are either OA codes or LAD codes for LAD totals.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Population>> _TwinBirths;
    /**
     * For storing triplet birth counts by age of mother. Keys are LAD codes,
     * value keys are either OA codes or LAD codes for LAD totals.
     */
    public TreeMap<String, TreeMap<String, GENESIS_Population>> _TripletBirths;
    /**
     * For storing migration probabilities by age and gender.
     */
    public GENESIS_Migration _Migration;
    /**
     * Used for executing threaded processes
     */
    private transient ExecutorService executorService;

    public GENESIS_Demographics() {
        String sourceMethod = "GENESIS_Demographics()";
        Logger.getLogger(sourcePackage).entering(sourceClass, sourceMethod);
        Logger.getLogger(sourcePackage).exiting(sourceClass, sourceMethod);
    }

    public GENESIS_Demographics(GENESIS_Demographics a_Demographics) {
        this(a_Demographics._GENESIS_Environment,
                a_Demographics);
    }

    public GENESIS_Demographics(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_Demographics a_Demographics) {
        String sourceMethod = "GENESIS_Demographics(GENESIS_Environment,Demographics)";
        Logger.getLogger(sourcePackage).entering(sourceClass, sourceMethod);
        this._GENESIS_Environment = a_GENESIS_Environment;
        this._Population = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_Demographics._Population);
        this._Mortality = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Mortality(
                a_Demographics._Mortality);
        this._Deaths = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_Demographics._Deaths);
        this._Miscarriage = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Miscarriage(
                a_Demographics._Miscarriage);
        this._ClinicalMiscarriage = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_Demographics._ClinicalMiscarriage);
        this._EarlyPregnancyLoss = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_Demographics._EarlyPregnancyLoss);
        this._Fertility = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Fertility(
                a_Demographics._Fertility);
        this._SingleBirths = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_Demographics._SingleBirths);
        this._TwinBirths = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_Demographics._TwinBirths);
        this._TripletBirths = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_Demographics._TripletBirths);
//        this._Migration = new GENESIS_Migration(
//                a_GENESIS_Environment, 
//                a_Demographics._Migration);
        Logger.getLogger(sourcePackage).exiting(sourceClass, sourceMethod);
    }

    public GENESIS_Demographics(
            GENESIS_Environment a_GENESIS_Environment) {
        String sourceMethod = "GENESIS_Demographics(GENESIS_Environment)";
        Logger.getLogger(sourcePackage).entering(sourceClass, sourceMethod);
        _GENESIS_Environment = a_GENESIS_Environment;
        _Population = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        _Mortality = new TreeMap<String, TreeMap<String, GENESIS_Mortality>>();
        _Deaths = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        _Miscarriage = new TreeMap<String, TreeMap<String, GENESIS_Miscarriage>>();
        _ClinicalMiscarriage = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        _EarlyPregnancyLoss = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        _Fertility = new TreeMap<String, TreeMap<String, GENESIS_Fertility>>();
        _SingleBirths = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        _TwinBirths = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        _TripletBirths = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        Logger.getLogger(sourcePackage).exiting(sourceClass, sourceMethod);
    }

    public GENESIS_Demographics aggregate(
            TreeMap<String, String> subregionToAggregateRegionLookup,
            TreeSet<String> aggregateRegionIDs) {
        return null;
        //throw new NotImplementedException();
//        GENESIS_Demographics result = new GENESIS_Demographics(_GENESIS_Environment);
//        Iterator<String> ite = _Population.keySet().iterator();
//        while (ite.hasNext()) {
//            String regionID = ite.next();
//            TreeMap<String,GENESIS_Population> regionPop = this._Population.get(regionID);
//            TreeMap<String,GENESIS_Mortality> regionMortality = this._Mortality.get(regionID);
//            
//            TreeMap<String,GENESIS_Population> regionDeaths = this._Deaths.get(regionID);
//            //TreeMap<String,GENESIS_Population> regionBirths = this._Births.get(regionID);
//            Iterator<String> ite2 = regionPop.keySet().iterator();
//            while (ite2.hasNext()) {
//                String subregionID = ite2.next();
//                String aggregateRegionID = subregionToAggregateRegionLookup.get(subregionID);
//                // Population
//                GENESIS_Population subregionPop = regionPop.get(subregionID);
//                TreeMap<String,GENESIS_Population> aggregateRegionPopulation = result._Population.get(regionID);
//                if (aggregateRegionPopulation == null) {
//                    aggregateRegionPopulation = new TreeMap<String,GENESIS_Population>();
//                    result._Population.put(regionID,aggregateRegionPopulation);
//                }
//                GENESIS_Population aggregateRegionPop = aggregateRegionPopulation.get(aggregateRegionID);
//                if (aggregateRegionPop == null) {
//                    aggregateRegionPop = new GENESIS_Population(_GENESIS_Environment);
//                    aggregateRegionPopulation.put(aggregateRegionID,aggregateRegionPop);
//                }
//                aggregateRegionPop.addPopulation(subregionPop);
//                // Mortality
//                GENESIS_Mortality subregionMortalityPop = regionMortality.get(subregionID);
//                TreeMap<String,GENESIS_Mortality> aggregateRegionMortality = result._Mortality.get(regionID);
//                if (aggregateRegionMortality == null) {
//                    aggregateRegionMortality = new TreeMap<String,GENESIS_Mortality>();
//                    result._Mortality.put(regionID,aggregateRegionMortality);
//                }
//                GENESIS_Mortality aggregateRegionMort = aggregateRegionMortality.get(aggregateRegionID);
//                if (aggregateRegionMort == null) {
//                    aggregateRegionMort = new GENESIS_Mortality(_GENESIS_Environment);
//                    aggregateRegionMortality.put(aggregateRegionID,aggregateRegionMort);
//                }
//                aggregateRegionMortality.addPopulation(subregionMortalityPop);
////                // Births
////                GENESIS_Population subregionBirthPop = regionBirths.get(subregionID);
////                TreeMap<String,GENESIS_Population> aggregateRegionBirthPops = result._Births.get(regionID);
////                if (aggregateRegionPops == null) {
////                    aggregateRegionBirthPops = new TreeMap<String,GENESIS_Population>();
////                    result._Births.put(regionID,aggregateRegionBirthPops);
////                }
////                GENESIS_Population aggregateRegionBirthPop = aggregateRegionBirthPops.get(aggregateRegionID);
////                if (aggregateRegionBirthPop == null) {
////                    aggregateRegionBirthPop = new GENESIS_Population(_GENESIS_Environment);
////                    aggregateRegionBirthPops.put(aggregateRegionID,aggregateRegionBirthPop);
////                }
////                aggregateRegionBirthPop.addPopulation(subregionBirthPop);
//            }
//        }
//        return result;
    }

    /**
     * @param executorService
     * @param regionIDs
     * @param subregionToAggregateRegionLookup
     * @param aggregateRegionIDs
     * @param dataWidthForScatterAndRegressionPlots
     * @param dataHeightForScatterAndRegressionPlots
     * @param dataWidthForAllAgesAgeGenderPlots
     * @param dataHeightForAllAgesAgeGenderPlots
     * @param dataWidthForFertileAgesAgeGenderPlots
     * @param dataHeightForFertileAgesAgeGenderPlots
     * @param decimalPlacePrecisionForCalculation
     * @param decimalPlacePrecisionForDisplay
     * @param roundingMode
     * @param startYear_Demographics
     * @param minAgeYearsForPopulationDisplays
     * @param maxAgeYearsForPopulationDisplays
     * @param minAgeYearsForFertilityDisplays
     * @param maxAgeYearsForFertilityDisplays
     * @param aliveDays
     * @param deaths
     * @param labours
     * @param births
     * @param earlyPregnancyLosses
     * @param clinicalMiscarriages
     * @param daysInEarlyPregnancy
     * @param daysInLatePregnancy
     * @param twins
     * @param triplets
     * @param pregnancies
     * @param handleOutOfMemoryError
     * @return
     */
    public HashSet<Future> output(
            ExecutorService executorService,
            TreeMap<String, TreeSet<String>> regionIDs,
            TreeMap<String, String> subregionToAggregateRegionLookup,
            TreeSet<String> aggregateRegionIDs,
            int dataWidthForScatterAndRegressionPlots,
            int dataHeightForScatterAndRegressionPlots,
            int dataWidthForAllAgesAgeGenderPlots,
            int dataHeightForAllAgesAgeGenderPlots,
            int dataWidthForFertileAgesAgeGenderPlots,
            int dataHeightForFertileAgesAgeGenderPlots,
            int decimalPlacePrecisionForCalculation,
            int decimalPlacePrecisionForDisplay,
            RoundingMode roundingMode,
            GENESIS_Demographics startYear_Demographics,
            Long minAgeYearsForPopulationDisplays,
            Long maxAgeYearsForPopulationDisplays,
            Long minAgeYearsForFertilityDisplays,
            Long maxAgeYearsForFertilityDisplays,
            TreeMap<String, TreeMap<String, GENESIS_Population>> aliveDays,
            TreeMap<String, TreeMap<String, GENESIS_Population>> deaths,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> labours,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> births,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> earlyPregnancyLosses,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> clinicalMiscarriages,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> daysInEarlyPregnancy,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> daysInLatePregnancy,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> twins,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> triplets,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> pregnancies,
            boolean handleOutOfMemoryError) {
        try {
        HashSet<Future> result = null;
        this.executorService = executorService;
        /*
         * Initialise Demographics results directory.
         * Because file output generation is now threaded, the structure needs 
         * to be known otherwise files may be being written to in the current
         * structure while the structure is attempted to be changed.
         */
        /* 
         * aggregate subregion results to aggregate region level
         * aggregatedResults[0] = StartPopulation
         * aggregatedResults[1] = EndPopulation
         * aggregatedResults[2] = AliveDays
         * aggregatedResults[3] = Deaths
         * aggregatedResults[4] = Births
         * aggregatedResults[5] = Labours
         * aggregatedResults[6] = EarlyPregnancyLosses
         * aggregatedResults[7] = ClinicalMiscarriages
         * aggregatedResults[8] = DaysInEarlyPregnancy
         * aggregatedResults[9] = DaysInLatePregnancy
         * aggregatedResults[10] = Twins
         * aggregatedResults[11] = Triplets
         * aggregatedResults[12] = Pregnancies
         */
        Object[] aggregatedResults = aggregateSubregionResults(
                regionIDs,
                subregionToAggregateRegionLookup,
                aggregateRegionIDs,
                startYear_Demographics._Population,
                _Population,
                aliveDays,
                deaths,
                births,
                labours,
                earlyPregnancyLosses,
                clinicalMiscarriages,
                daysInEarlyPregnancy,
                daysInLatePregnancy,
                twins,
                triplets,
                pregnancies);
        TreeMap<String, String> aggregateRegionToRegionLUT = (TreeMap<String, String>) aggregatedResults[13];
        long maxDemographicsDirectoryID = aggregateRegionIDs.size();
        maxDemographicsDirectoryID += regionIDs.size();
        maxDemographicsDirectoryID -= 1;
        File demographicsDirectory = new File(
                _GENESIS_Environment._AbstractModel._ResultDataDirectory_File,
                "Demographics");
        long range = _GENESIS_Environment.AgentEnvironment.get_AgentCollectionManager(true)._MaximumNumberOfObjectsPerDirectory;
        try {
            Generic_StaticIO.initialiseArchive(
                    demographicsDirectory,
                    range,
                    maxDemographicsDirectoryID);
        } catch (IOException e) {
            System.err.println(e.getMessage() + " in " + this.getClass().getName() + ".output(...)");
            System.exit(GENESIS_ErrorAndExceptionHandler.IOException);
        }
        long theDemographicsDirectoryID = 0L;
        Iterator<String> ite;
        ite = regionIDs.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            /*
             * region output example dir:
             * ../Demographics/0_9999/0_99/0/OODA/
             */
            File dir = Generic_StaticIO.getObjectDirectory(
                    demographicsDirectory,
                    theDemographicsDirectoryID,
                    maxDemographicsDirectoryID,
                    range);
            dir = new File(
                    dir,
                    "" + theDemographicsDirectoryID);
            dir = new File(
                    dir,
                    "" + regionID);
            dir.mkdirs();
            result = output(
                    regionID,
                    dir,
                    dataWidthForScatterAndRegressionPlots,
                    dataHeightForScatterAndRegressionPlots,
                    dataWidthForAllAgesAgeGenderPlots,
                    dataHeightForAllAgesAgeGenderPlots,
                    dataWidthForFertileAgesAgeGenderPlots,
                    dataHeightForFertileAgesAgeGenderPlots,
                    decimalPlacePrecisionForCalculation,
                    decimalPlacePrecisionForDisplay,
                    roundingMode,
                    startYear_Demographics._Population.get(regionID).get(regionID),
                    this._Population.get(regionID).get(regionID),
                    startYear_Demographics._Mortality.get(regionID).get(regionID),
                    startYear_Demographics._Fertility.get(regionID).get(regionID),
                    _Mortality.get(regionID).get(regionID),
                    _Fertility.get(regionID).get(regionID),
                    minAgeYearsForPopulationDisplays,
                    maxAgeYearsForPopulationDisplays,
                    minAgeYearsForFertilityDisplays,
                    maxAgeYearsForFertilityDisplays,
                    aliveDays.get(regionID).get(regionID),
                    deaths.get(regionID).get(regionID),
                    births.get(regionID).get(regionID),
                    labours.get(regionID).get(regionID),
                    earlyPregnancyLosses.get(regionID).get(regionID),
                    clinicalMiscarriages.get(regionID).get(regionID),
                    daysInEarlyPregnancy.get(regionID).get(regionID),
                    daysInLatePregnancy.get(regionID).get(regionID),
                    twins.get(regionID).get(regionID),
                    triplets.get(regionID).get(regionID),
                    pregnancies.get(regionID).get(regionID),
                    handleOutOfMemoryError);
            theDemographicsDirectoryID++;
        }
        // output
        ite = aggregateRegionIDs.iterator();
        while (ite.hasNext()) {
            String aggregateRegionID = ite.next();
            File dir = Generic_StaticIO.getObjectDirectory(
                    demographicsDirectory,
                    theDemographicsDirectoryID,
                    maxDemographicsDirectoryID,
                    range);
            dir = new File(
                    dir,
                    "" + theDemographicsDirectoryID);
            dir = new File(
                    dir,
                    "" + aggregateRegionID);
            dir.mkdirs();
            theDemographicsDirectoryID++;
            String regionID = aggregateRegionToRegionLUT.get(aggregateRegionID);
            System.out.println("aggregateRegionID " + aggregateRegionID + ", regionID " + regionID);
            /* 
             * aggregate subregion results to aggregate region level
             * aggregatedResults[0] = StartPopulation
             * aggregatedResults[1] = EndPopulation
             * aggregatedResults[2] = AliveDays
             * aggregatedResults[3] = Deaths
             * aggregatedResults[4] = Births
             * aggregatedResults[5] = Labours
             * aggregatedResults[6] = EarlyPregnancyLosses
             * aggregatedResults[7] = ClinicalMiscarriages
             * aggregatedResults[8] = DaysInEarlyPregnancy
             * aggregatedResults[9] = DaysInLatePregnancy
             * aggregatedResults[10] = Twins
             * aggregatedResults[11] = Triplets
             * aggregatedResults[12] = Pregnancies
             */
            HashSet<Future> moreResults = output(
                    aggregateRegionID,
                    dir,
                    dataWidthForScatterAndRegressionPlots,
                    dataHeightForScatterAndRegressionPlots,
                    dataWidthForAllAgesAgeGenderPlots,
                    dataHeightForAllAgesAgeGenderPlots,
                    dataWidthForFertileAgesAgeGenderPlots,
                    dataHeightForFertileAgesAgeGenderPlots,
                    decimalPlacePrecisionForCalculation,
                    decimalPlacePrecisionForDisplay,
                    roundingMode,
                    ((TreeMap<String, GENESIS_Population>) aggregatedResults[0]).get(aggregateRegionID),
                    ((TreeMap<String, GENESIS_Population>) aggregatedResults[1]).get(aggregateRegionID),
                    startYear_Demographics._Mortality.get(regionID).get(regionID),
                    startYear_Demographics._Fertility.get(regionID).get(regionID),
                    _Mortality.get(regionID).get(regionID),
                    _Fertility.get(regionID).get(regionID),
                    minAgeYearsForPopulationDisplays,
                    maxAgeYearsForPopulationDisplays,
                    minAgeYearsForFertilityDisplays,
                    maxAgeYearsForFertilityDisplays,
                    ((TreeMap<String, GENESIS_Population>) aggregatedResults[2]).get(aggregateRegionID),
                    ((TreeMap<String, GENESIS_Population>) aggregatedResults[3]).get(aggregateRegionID),
                    ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) aggregatedResults[4]).get(aggregateRegionID),
                    ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) aggregatedResults[5]).get(aggregateRegionID),
                    ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) aggregatedResults[6]).get(aggregateRegionID),
                    ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) aggregatedResults[7]).get(aggregateRegionID),
                    ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) aggregatedResults[8]).get(aggregateRegionID),
                    ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) aggregatedResults[9]).get(aggregateRegionID),
                    ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) aggregatedResults[10]).get(aggregateRegionID),
                    ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) aggregatedResults[11]).get(aggregateRegionID),
                    ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) aggregatedResults[12]).get(aggregateRegionID),
                    handleOutOfMemoryError);
            result.addAll(moreResults);
        }
        return result;
        } catch (OutOfMemoryError e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    /**
     * Aggregates subregion data to aggregate regions. The aggregate regions may
     * contain subregions from more than one region.
     *
     * @param regionIDs
     * @param subregionAggregateRegionLookup
     * @param aggregateRegionIDs
     * @param startPopulation
     * @param aliveDays
     * @param endPopulation
     * @param deaths
     * @param labours
     * @param births
     * @param earlyPregnancyLosses
     * @param clinicalMiscarriages
     * @param daysInEarlyPregnancy
     * @param daysInLatePregnancy
     * @param twins
     * @param triplets
     * @param pregnancies
     * @return Object[] result where; <ul> <li>result[0] = StartPopulation</li>
     * <li>result[1] = EndPopulation</li> <li>result[2] = AliveDays</li>
     * <li>result[3] = Deaths</li> <li>result[4] = Births</li> <li>result[5] =
     * Labours</li> <li>result[6] = EarlyPregnancyLosses</li> <li>result[7] =
     * ClinicalMiscarriages</li> <li>result[8] = DaysInEarlyPregnancy</li>
     * <li>result[9] = DaysInLatePregnancy</li> <li>result[10] = Twins</li>
     * <li>result[11] = Triplets</li> <li>result[12] = Pregnancies</li>
     * <li>result[13] = aggregateRegionToRegionLUT</li></ul>
     */
    public Object[] aggregateSubregionResults(
            TreeMap<String, TreeSet<String>> regionIDs,
            TreeMap<String, String> subregionAggregateRegionLookup,
            TreeSet<String> aggregateRegionIDs,
            TreeMap<String, TreeMap<String, GENESIS_Population>> startPopulation,
            TreeMap<String, TreeMap<String, GENESIS_Population>> endPopulation,
            TreeMap<String, TreeMap<String, GENESIS_Population>> aliveDays,
            TreeMap<String, TreeMap<String, GENESIS_Population>> deaths,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> labours,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> births,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> earlyPregnancyLosses,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> clinicalMiscarriages,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> daysInEarlyPregnancy,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> daysInLatePregnancy,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> twins,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> triplets,
            TreeMap<String, TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>> pregnancies) {
        // Initialise aggregatedResults
        Object[] result = new Object[14];
        int resultID;
        int numberOfPopulationResults = 4;
        for (resultID = 0; resultID < numberOfPopulationResults; resultID++) {
            result[resultID] = new TreeMap<String, GENESIS_Population>();
        }
        for (resultID = numberOfPopulationResults; resultID < result.length - 1; resultID++) {
            result[resultID] = new TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>();
        }
        Iterator<String> ite;
        String aggregateRegionID;
        ite = aggregateRegionIDs.iterator();
        while (ite.hasNext()) {
            aggregateRegionID = ite.next();
            for (resultID = 0; resultID < numberOfPopulationResults; resultID++) {
                ((TreeMap<String, GENESIS_Population>) result[resultID]).put(
                        aggregateRegionID, new GENESIS_Population(_GENESIS_Environment));
            }
            for (resultID = numberOfPopulationResults; resultID < result.length - 1; resultID++) {
                ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) result[resultID]).put(
                        aggregateRegionID,
                        new TreeMap<GENESIS_AgeBound, BigDecimal>());
            }
        }
        TreeMap<String, String> aggregateRegionToRegionLUT = new TreeMap<String, String>();
        result[13] = aggregateRegionToRegionLUT;
        ite = regionIDs.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            TreeMap<String, GENESIS_Population> regionStartPopulation = startPopulation.get(regionID);
            TreeMap<String, GENESIS_Population> regionEndPopulation = endPopulation.get(regionID);
            TreeMap<String, GENESIS_Population> regionAliveDays = aliveDays.get(regionID);
            TreeMap<String, GENESIS_Population> regionDeaths = deaths.get(regionID);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionBirths = births.get(regionID);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionLabours = labours.get(regionID);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionEarlyPregnancyLosses = earlyPregnancyLosses.get(regionID);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionClinicalMiscarriages = clinicalMiscarriages.get(regionID);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionDaysInEarlyPregnancy = daysInEarlyPregnancy.get(regionID);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionDaysInLatePregnancy = daysInLatePregnancy.get(regionID);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionTwins = twins.get(regionID);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionTriplets = triplets.get(regionID);
            TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>> regionPregnancies = pregnancies.get(regionID);
            TreeSet<String> subregionIDs = regionIDs.get(regionID);
            Iterator<String> ite2 = subregionIDs.iterator();
            while (ite2.hasNext()) {
                String subregionID = ite2.next();
                // Ignore region totals
                if (!subregionID.equalsIgnoreCase(regionID)) {
                    aggregateRegionID = subregionAggregateRegionLookup.get(subregionID);
                    aggregateRegionToRegionLUT.put(aggregateRegionID, regionID);

//                    // Checking code
//                    if (aggregateRegionID == null) {
//                        System.out.println("aggregateRegionID == null");
//                    }

                    // result 0 StartPopulation
                    GENESIS_Population aggregateRegionStartPopulation =
                            ((TreeMap<String, GENESIS_Population>) result[0]).get(
                            aggregateRegionID);
                    GENESIS_Population subregionStartPopulation =
                            regionStartPopulation.get(subregionID);
                    aggregateRegionStartPopulation.addPopulationNoUpdate(subregionStartPopulation);
                    // result 1 EndPopulation
                    GENESIS_Population aggregateRegionEndPopulation =
                            ((TreeMap<String, GENESIS_Population>) result[1]).get(
                            aggregateRegionID);
                    GENESIS_Population subregionEndPopulation =
                            regionEndPopulation.get(subregionID);
                    aggregateRegionEndPopulation.addPopulationNoUpdate(subregionEndPopulation);
                    // result 2 AliveDays
                    GENESIS_Population aggregateRegionAliveDays =
                            ((TreeMap<String, GENESIS_Population>) result[2]).get(
                            aggregateRegionID);
                    GENESIS_Population subregionAliveDays =
                            regionAliveDays.get(subregionID);
                    aggregateRegionAliveDays.addPopulationNoUpdate(subregionAliveDays);
                    // result 3 Deaths
                    GENESIS_Population aggregateRegionDeaths =
                            ((TreeMap<String, GENESIS_Population>) result[3]).get(
                            aggregateRegionID);
                    GENESIS_Population subregionDeaths =
                            regionDeaths.get(subregionID);
                    aggregateRegionDeaths.addPopulationNoUpdate(subregionDeaths);
                    // result 4 Births
                    TreeMap<GENESIS_AgeBound, BigDecimal> aggregateRegionBirths =
                            ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) result[4]).get(
                            aggregateRegionID);
                    TreeMap<GENESIS_AgeBound, BigDecimal> subregionBirths =
                            regionBirths.get(subregionID);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                            aggregateRegionBirths,
                            subregionBirths);
                    // result 5 Labours
                    TreeMap<GENESIS_AgeBound, BigDecimal> aggregateRegionLabours =
                            ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) result[5]).get(
                            aggregateRegionID);
                    TreeMap<GENESIS_AgeBound, BigDecimal> subregionLabours =
                            regionLabours.get(subregionID);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                            aggregateRegionLabours,
                            subregionLabours);
                    // result 6 EarlyPregnancyLosses
                    TreeMap<GENESIS_AgeBound, BigDecimal> aggregateRegionEarlyPregnancyLosses =
                            ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) result[6]).get(
                            aggregateRegionID);
                    TreeMap<GENESIS_AgeBound, BigDecimal> subregionEarlyPregnancyLosses =
                            regionEarlyPregnancyLosses.get(subregionID);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                            aggregateRegionEarlyPregnancyLosses,
                            subregionEarlyPregnancyLosses);
                    // result 7 ClinicalMiscarriages
                    TreeMap<GENESIS_AgeBound, BigDecimal> aggregateRegionClinicalMiscarriages =
                            ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) result[7]).get(
                            aggregateRegionID);
                    TreeMap<GENESIS_AgeBound, BigDecimal> subregionClinicalMiscarriages =
                            regionClinicalMiscarriages.get(subregionID);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                            aggregateRegionClinicalMiscarriages,
                            subregionClinicalMiscarriages);
                    // result 8 DaysInEarlyPregnancy
                    TreeMap<GENESIS_AgeBound, BigDecimal> aggregateRegionDaysInEarlyPregnancy =
                            ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) result[8]).get(
                            aggregateRegionID);
                    TreeMap<GENESIS_AgeBound, BigDecimal> subregionDaysInEarlyPregnancy =
                            regionDaysInEarlyPregnancy.get(subregionID);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                            aggregateRegionDaysInEarlyPregnancy,
                            subregionDaysInEarlyPregnancy);
                    // result 9 DaysInLatePregnancy
                    TreeMap<GENESIS_AgeBound, BigDecimal> aggregateRegionDaysInLatePregnancy =
                            ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) result[9]).get(
                            aggregateRegionID);
                    TreeMap<GENESIS_AgeBound, BigDecimal> subregionDaysInLatePregnancy =
                            regionDaysInLatePregnancy.get(subregionID);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                            aggregateRegionDaysInLatePregnancy,
                            subregionDaysInLatePregnancy);
                    // result 10 Twins
                    TreeMap<GENESIS_AgeBound, BigDecimal> aggregateRegionTwins =
                            ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) result[10]).get(
                            aggregateRegionID);
                    TreeMap<GENESIS_AgeBound, BigDecimal> subregionTwins =
                            regionTwins.get(subregionID);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                            aggregateRegionTwins,
                            subregionTwins);
                    // result 11 Triplets
                    TreeMap<GENESIS_AgeBound, BigDecimal> aggregateRegionTriplets =
                            ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) result[11]).get(
                            aggregateRegionID);
                    TreeMap<GENESIS_AgeBound, BigDecimal> subregionTriplets =
                            regionTriplets.get(subregionID);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                            aggregateRegionTriplets,
                            subregionTriplets);
                    // result 12 Pregnancies
                    TreeMap<GENESIS_AgeBound, BigDecimal> aggregateRegionPregnancies =
                            ((TreeMap<String, TreeMap<GENESIS_AgeBound, BigDecimal>>) result[12]).get(
                            aggregateRegionID);
                    TreeMap<GENESIS_AgeBound, BigDecimal> subregionPregnancies =
                            regionPregnancies.get(subregionID);
                    GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                            aggregateRegionPregnancies,
                            subregionPregnancies);
                }
            }
        }
        // updateGenderedAgePopulation
        ite = aggregateRegionIDs.iterator();
        while (ite.hasNext()) {
            aggregateRegionID = ite.next();
            for (resultID = 0; resultID < numberOfPopulationResults; resultID++) {
                GENESIS_Population pop = ((TreeMap<String, GENESIS_Population>) result[resultID]).get(
                        aggregateRegionID);
                pop.updateGenderedAgePopulation();
            }
        }
        return result;
    }

    /**
     * @param handleOutOfMemoryError
     * @param endYear_Demographics_Fertility
     * @return 
     * @TODO Return information for visualising other data so that the results
     * can be made to be more comparable/combinable.
     * @param outputImages
     * @param regionID
     * @param dataWidthForScatterAndRegressionPlots
     * @param dataHeightForScatterAndRegressionPlots
     * @param dataWidthForAgeGenderPlots
     * @param dataHeightForAgeGenderPlots
     * @param decimalPlacePrecisionForCalculations
     * @param significantDigits
     * @param roundingMode
     * @param startYear_Demographics
     * @param minAgeYearsForAllAgesPopulationDisplays
     * @param maxAgeYearsForAllAgesPopulationDisplays
     * @param minAgeYearsForFertilityAgesPopulationDisplays
     * @param maxAgeYearsForFertilityAgesPopulationDisplays
     * @param populationAliveDaysThisYear
     * @param populationDeathsThisYear
     * @param femaleAgeInYearsCountOfBirths_TreeMap
     * @param femaleAgeInYearsCountOfLabours_TreeMap
     * @param femaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap
     * @param femaleAgeInYearsCountOfClinicalMiscarriage_TreeMap
     * @param femaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap
     * @param femaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap
     * @param femaleAgeInYearsCountOfTwins_TreeMap
     * @param femaleAgeInYearsCountOfTriplets_TreeMap
     * @param femaleAgeInYearsCountOfPregnancies_TreeMap
     */
    public HashSet<Future> output(
            String regionID,
            File demographicsDirectory_File,
            int dataWidthForScatterAndRegressionPlots,
            int dataHeightForScatterAndRegressionPlots,
            int dataWidthForAllAgesPopulationDisplays,
            int dataHeightForAllAgesPopulationDisplays,
            int dataWidthForFertileAgesPopulationDisplays,
            int dataHeightForFertileAgesPopulationDisplays,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode roundingMode,
            GENESIS_Population startYearPopulation,
            GENESIS_Population endYearPopulation,
            GENESIS_Mortality startYear_Demographics_Mortality,
            GENESIS_Fertility startYear_Demographics_Fertility,
            GENESIS_Mortality endYear_Demographics_Mortality,
            GENESIS_Fertility endYear_Demographics_Fertility,
            Long minAgeYearsForAllAgesPopulationDisplays,
            Long maxAgeYearsForAllAgesPopulationDisplays,
            Long minAgeYearsForFertilityAgesPopulationDisplays,
            Long maxAgeYearsForFertilityAgesPopulationDisplays,
            GENESIS_Population populationAliveDaysThisYear,
            GENESIS_Population populationDeathsThisYear,
            //GENESIS_Population populationTotalDaysLived_Population,
            //GENESIS_Population populationTotalDeaths_Population,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfBirths_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfLabours_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfClinicalMiscarriage_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfTwins_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfTriplets_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfPregnancies_TreeMap,
            boolean handleOutOfMemoryError) {
        try {
        HashSet<Future> result = new HashSet<Future>();
        HashSet<Future> moreResults;
        Future atomicResult;
        String method = "output("
                + "boolean,String,int,int,int,int,int,int,RoundingMode,"
                + "GENESIS_Demographics,"
                + "BigDecimal,"
                + "BigDecimal,"
                + "BigDecimal,"
                + "GENESIS_Population"
                + "GENESIS_Population,"
                + "TreeMap<GENESIS_AgeBound, BigDecimal>,"
                + "TreeMap<GENESIS_AgeBound, BigDecimal>,"
                + "TreeMap<GENESIS_AgeBound, BigDecimal>,"
                + "TreeMap<GENESIS_AgeBound, BigDecimal>,"
                + "TreeMap<GENESIS_AgeBound, BigDecimal>,"
                + "TreeMap<GENESIS_AgeBound, BigDecimal>,"
                + "TreeMap<GENESIS_AgeBound, BigDecimal>,"
                + "TreeMap<GENESIS_AgeBound, BigDecimal>)";

        String xAxisLabel;
        String yAxisLabel;
        System.out.println(method);
//        // Initialisation
//        File demographicsDirectory_File = new File(
//                Generic_StaticIO.getObjectDirectory(
//                demographicsDirectoryParent_File,
//                theDemographicsDirectoryID,
//                maxDemographicsDirectoryID,
//                _GENESIS_Environment.AgentEnvironment.getAgentCollectionManager(true)._MaximumNumberOfObjectsPerDirectory),
//                "" + theDemographicsDirectoryID);
//        demographicsDirectory_File = new File(
//                demographicsDirectory_File,
//                regionID);
//        demographicsDirectory_File.mkdirs();
        String title;
        //String rangeAxisLabel;
        Object[] theoreticalEndYearPopulationsAndFertility;
//        if (regionID.equalsIgnoreCase("Total")) {
//            theoreticalEndYearPopulationsAndFertility = getTheoreticalEndYearPopulationsAndFertility(
//                    startYearPopulation,
//                    startYear_Demographics_Mortality,
//                    startYear_Demographics_Fertility,
//                    decimalPlacePrecisionForCalculations);
////        BigDecimal minAgeYearsFertility = _Fertility.getMinAgeYears();
////        BigDecimal maxAgeYearsFertility = _Fertility.getMaxAgeYears();
//        } else {
        theoreticalEndYearPopulationsAndFertility =
                getTheoreticalEndYearPopulationsAndFertility(
                startYearPopulation,
                startYear_Demographics_Mortality,
                startYear_Demographics_Fertility,
                decimalPlacePrecisionForCalculations,
                handleOutOfMemoryError);
//        }

        GENESIS_Population endYearPopulationLivingExpected;
        GENESIS_Population endYearPopulationDeadExpected;
        TreeMap<GENESIS_AgeBound, BigDecimal> liveBirthsExpected;
        TreeMap<GENESIS_AgeBound, BigDecimal> liveTwinBirthsExpected;
        TreeMap<GENESIS_AgeBound, BigDecimal> liveTripletBirthsExpected;
        endYearPopulationLivingExpected =
                (GENESIS_Population) theoreticalEndYearPopulationsAndFertility[0];
        endYearPopulationDeadExpected =
                (GENESIS_Population) theoreticalEndYearPopulationsAndFertility[1];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalFertility =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[2];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalEndYearAgedPopulation = 
//                totalTheoreticalEndYearAgedPopulation._FemaleAgeBoundPopulationCount_TreeMap;
//        TreeMap<GENESIS_AgeBound, BigDecimal> maleTheoreticalEndYearAgedPopulation = 
//                totalTheoreticalEndYearAgedPopulation._MaleAgeBoundPopulationCount_TreeMap;
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalDeadPopulation =
//                totalTheoreticalDeadPopulation._FemaleAgeBoundPopulationCount_TreeMap;
//        TreeMap<GENESIS_AgeBound, BigDecimal> maleTheoreticalDeadPopulation = 
//                totalTheoreticalDeadPopulation._MaleAgeBoundPopulationCount_TreeMap;
        liveBirthsExpected =
                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[2];
        liveTwinBirthsExpected =
                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[3];
        liveTripletBirthsExpected =
                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[4];

        /**
         * Output Theoretically Expected Plots
         */
        // Theoretically alive population
        long year = _GENESIS_Environment._Time.getYear();
        title = regionID + " Population _Theoretically Expected Living_ Year " + year;
        xAxisLabel = "Population (Number of Years)";
        yAxisLabel = "Age";
        //atomicResult = output(outputImages,
        output(
                title,
                dataWidthForAllAgesPopulationDisplays,
                dataHeightForAllAgesPopulationDisplays,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                endYearPopulationLivingExpected,
                minAgeYearsForAllAgesPopulationDisplays,
                maxAgeYearsForAllAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        //result.add(atomicResult);
        // Theoretically dead population
        title = regionID + " Population _Theoretically Expected Dead_ Year " + year;
        xAxisLabel = "Population";
        //atomicResult = output(outputImages,
        output(
                title,
                dataWidthForAllAgesPopulationDisplays,
                dataHeightForAllAgesPopulationDisplays,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                endYearPopulationDeadExpected,
                minAgeYearsForAllAgesPopulationDisplays,
                maxAgeYearsForAllAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        //result.add(atomicResult);
        // Theoretical births
        title = regionID + " Population _Theoretically Expected Birth Parents_ Year " + year;
        //atomicResult = outputAgeGenderPlot(
        outputAgeGenderPlot(
                executorService,
                this,
                title,
                dataWidthForFertileAgesPopulationDisplays,
                dataHeightForFertileAgesPopulationDisplays,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                liveBirthsExpected,
                null,
                minAgeYearsForFertilityAgesPopulationDisplays,
                maxAgeYearsForFertilityAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        //result.add(atomicResult);

        // Theoretical twins
        title = regionID + " Population _Theoretically Expected Twin Parents_ Year " + year;
        //atomicResult = outputAgeGenderPlot(
        outputAgeGenderPlot(
                executorService,
                this,
                title,
                dataWidthForFertileAgesPopulationDisplays,
                dataHeightForFertileAgesPopulationDisplays,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                liveTwinBirthsExpected,
                null,
                minAgeYearsForFertilityAgesPopulationDisplays,
                maxAgeYearsForFertilityAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        //result.add(atomicResult);
        // Theoretical triplets
        title = regionID + " Population _Theoretically Expected Triplet Parents_ Year " + year;
        //atomicResult = outputAgeGenderPlot(
        outputAgeGenderPlot(
                executorService,
                this,
                title,
                dataWidthForFertileAgesPopulationDisplays,
                dataHeightForFertileAgesPopulationDisplays,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                liveTripletBirthsExpected,
                null,
                minAgeYearsForFertilityAgesPopulationDisplays,
                maxAgeYearsForFertilityAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        //result.add(atomicResult);
        /**
         * Output Simulation Result Plots
         */
        // Population Living Years
        title = regionID + " Population _Simulated Living_ Year " + year;
        xAxisLabel = "Population Counts (Number of Years)";
        //System.out.println(_Total_Population.getMaxPopulationInAnyAgeBound());
        //atomicResult = output(outputImages,
        output(
                title,
                dataWidthForAllAgesPopulationDisplays,
                dataHeightForAllAgesPopulationDisplays,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                endYearPopulation,
                minAgeYearsForAllAgesPopulationDisplays,
                maxAgeYearsForAllAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        //result.add(atomicResult);
        // Total Population Living Days
        title = regionID + " Population _Simulated Living Days_ Year " + year;
        //atomicResult = output(outputImages,
        output(
                title,
                dataWidthForAllAgesPopulationDisplays,
                dataHeightForAllAgesPopulationDisplays,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                populationAliveDaysThisYear,
                minAgeYearsForAllAgesPopulationDisplays,
                maxAgeYearsForAllAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        //result.add(atomicResult);
        // Population Deaths This Year
        title = regionID + " Population _Simulated Dead_ Year " + year;
        xAxisLabel = "Population";
        //atomicResult = output(outputImages,
        output(
                title,
                dataWidthForAllAgesPopulationDisplays,
                dataHeightForAllAgesPopulationDisplays,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                populationDeathsThisYear,
                minAgeYearsForAllAgesPopulationDisplays,
                maxAgeYearsForAllAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        //result.add(atomicResult);
        /*
         * Output population comparison graphs for end year population
         */
        xAxisLabel = "Population Living Theoretically Expected (X)";
        yAxisLabel = "Population Living Simulated Observed (Y)";
        // Female Population
        title = regionID + " Population Female End of Year Comparison " + year;
        boolean drawOriginLinesOnPlot;
        drawOriginLinesOnPlot = false;
        //endYearPopulationLivingObserved.updateGenderAgeBoundPopulation_TreeMaps();
        CompareProbabilities.output(
                executorService,
                endYearPopulationLivingExpected._FemaleAgeBoundPopulationCount_TreeMap,
                endYearPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                title,
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                year,
                demographicsDirectory_File);
        // Male Population
        title = regionID + " Population Male End of Year Comparison " + year;
        CompareProbabilities.output(
                executorService,
                endYearPopulationLivingExpected._MaleAgeBoundPopulationCount_TreeMap,
                endYearPopulation._MaleAgeBoundPopulationCount_TreeMap,
                title,
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                year,
                demographicsDirectory_File);
        /**
         * Output mortality (Rate Comparison PNG, Rate XML, Rate CSV)
         */
        RoundingMode a_RoundingMode = RoundingMode.HALF_UP;
        outputMortality(
                //executorService,
                regionID,
                endYear_Demographics_Mortality,
                dataWidthForAllAgesPopulationDisplays,
                dataHeightForAllAgesPopulationDisplays,
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                demographicsDirectory_File,
                populationAliveDaysThisYear,
                populationDeathsThisYear,
                minAgeYearsForAllAgesPopulationDisplays,
                maxAgeYearsForAllAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                a_RoundingMode);
        // @TODO Theoretical Mortality comparison
        /**
         * Output miscarriage
         */
        moreResults = outputMiscarriage(
                regionID,
                dataWidthForFertileAgesPopulationDisplays,
                dataHeightForFertileAgesPopulationDisplays,
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                femaleAgeInYearsCountOfClinicalMiscarriage_TreeMap,
                femaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap,
                femaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap,
                femaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap,
                demographicsDirectory_File,
                minAgeYearsForFertilityAgesPopulationDisplays,
                maxAgeYearsForFertilityAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                a_RoundingMode);
        result.addAll(moreResults);
        /**
         * Output fertility
         */
        TreeMap<GENESIS_AgeBound, BigDecimal> observedAnnualFertility = outputFertility(
                regionID,
                endYear_Demographics_Fertility,
                dataWidthForFertileAgesPopulationDisplays,
                dataHeightForFertileAgesPopulationDisplays,
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                demographicsDirectory_File,
                populationAliveDaysThisYear,
                femaleAgeInYearsCountOfBirths_TreeMap,
                //femaleAgeInYearsCountOfSingleBirths_TreeMap,
                femaleAgeInYearsCountOfLabours_TreeMap,
                femaleAgeInYearsCountOfTwins_TreeMap,
                femaleAgeInYearsCountOfTriplets_TreeMap,
                minAgeYearsForFertilityAgesPopulationDisplays,
                maxAgeYearsForFertilityAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                a_RoundingMode);
//        title = "Fertility Rate Theoretical Comparison Year " + _GENESIS_Environment._Time._Year;
//        CompareProbabilities.output(
//                femaleTheoreticalFertility,
//                observedAnnualFertility,
//                femaleAgeInYearsCountOfTwins_TreeMap,
//                femaleAgeInYearsCountOfTriplets_TreeMap,
//                title,
//                _GENESIS_Environment._Time._Year,
//                demographicsDirectory_File);
        // Output comparison statistics
        return result;
        } catch (OutOfMemoryError e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

//    // @Deprecated
//    // public JFreeChart ageGenderPlot(
//    // Object[] _Population,
//    // Calendar _Date_Calendar) {
//    // GENESIS_Time _Date = new GENESIS_Time(_Date_Calendar.YEAR, _Date_Calendar.DAY_OF_YEAR);
//    // return ageGenderPlot(_Population, _Date);
//    // }
//    @Deprecated
//    public JFreeChart getAgeGenderPlot(
//            HashSet<Long> a_Female_Population_Alive_ID_HashSet,
//            HashSet<Long> a_Male_Population_Alive_ID_HashSet,
//            GENESIS_Time a_Time,
//            boolean handleOutOfMemoryError) {
//        try {
//            JFreeChart result = GENESIS_AgeGenderPlot.getAgeGenderPlot(
//                    a_Female_Population_Alive_ID_HashSet,
//                    a_Male_Population_Alive_ID_HashSet,
//                    a_Time,
//                    _GENESIS_Environment);
//            _GENESIS_Environment.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
//            // OutOfMemoryErrorHandler.tryToEnsureThereIsEnoughMemoryToContinue();
//            return result;
//        } catch (OutOfMemoryError _OutOfMemoryError) {
//            if (handleOutOfMemoryError) {
//                _GENESIS_Environment.clear_MemoryReserve();
//                _GENESIS_Environment.swapToFile_DataAny();
//                _GENESIS_Environment.init_MemoryReserve(handleOutOfMemoryError);
//                return getAgeGenderPlot(
//                        a_Female_Population_Alive_ID_HashSet,
//                        a_Male_Population_Alive_ID_HashSet, a_Time,
//                        handleOutOfMemoryError);
//            } else {
//                throw _OutOfMemoryError;
//            }
//        }
//    }
    /**
     *
     * @param title
     * @param demographicsDirectory_File
     * @param populationAliveDaysThisYear
     * @param femaleAgeBoundCountOfBirths_TreeMap
     * @param a_XMLConverter
     * @param roundingMode
     * @param decimalPlacePrecisionForDisplay
     * @param decimalPlacePrecisionForCalculations
     * @param maxAgeYearsForFertilityDisplays
     * @return TreeMap&lt;AgeBound, BigDecimal&gt; result of annual live birth
     * probabilities where: keys are AgeBounds; values are annual live birth
     * fertility rates
     */
    protected TreeMap<GENESIS_AgeBound, BigDecimal> outputFertility(
            String regionID,
            GENESIS_Fertility fertility,
            int dataWidthForAgeGenderPlots,
            int dataHeightForAgeGenderPlots,
            int dataWidthForScatterAndRegressionPlots,
            int dataHeightForScatterAndRegressionPlots,
            File demographicsDirectory_File,
            GENESIS_Population populationAliveDaysThisYear,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeBoundCountOfBirths_TreeMap,
            //TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeBoundCountOfSingleBirths_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeBoundCountOfLabours_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeBoundCountOfTwins_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeBoundCountOfTriplets_TreeMap,
            Long minAgeYearsForFertilityDisplays,
            Long maxAgeYearsForFertilityDisplays,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode roundingMode) {
        BigDecimal numberOfDaysInYear_BigDecimal = new BigDecimal(GENESIS_Time.NormalDaysInYear_BigInteger);
        GENESIS_Fertility annual_Fertility = new GENESIS_Fertility(_GENESIS_Environment);
        annual_Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        annual_Fertility._TwinPregnancyAgeBoundProbability_TreeMap =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        annual_Fertility._TripletPregnancyAgeBoundProbability_TreeMap =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        BigDecimal annualFertilityProbability_BigDecimal;
        BigDecimal countOfBirths_BigDecimal;
        //BigDecimal countOfBoyBirths_BigDecimal;
        //BigDecimal countOfGirlBirths_BigDecimal;
        //BigInteger numberOfDays_BigInteger;
        BigDecimal numberOfDays_BigDecimal;
        //BigDecimal numberOfYearsOfAgeInYears;
        // ------------------
        // Output populations
        // ------------------
        String title;
        String xAxisLabel;
        String yAxisLabel = "Age";
        long year = _GENESIS_Environment._Time.getYear();
        // All parents
        // -----------
        GENESIS_Population populationOfParentsOfNewborns = new GENESIS_Population(_GENESIS_Environment);
        populationOfParentsOfNewborns._FemaleAgeBoundPopulationCount_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Population(
                femaleAgeBoundCountOfLabours_TreeMap);
        populationOfParentsOfNewborns.updateGenderedAgePopulation();
//        populationOfParentsOfNewborns.getGenderedAgeBoundPopulation().getFemale().addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundPopulation(
//                femaleAgeBoundCountOfLabours_TreeMap));
        title = regionID + " Population _Simulated_Birth_Parents_ Year " + year;
        xAxisLabel = "Population";
        output(
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                populationOfParentsOfNewborns,
                minAgeYearsForFertilityDisplays,
                maxAgeYearsForFertilityDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
//        // Output csv
//        populationOfParentsOfNewborns.writeToCSV(
//                new File(demographicsDirectory_File,
//                regionID + "_Population__Simulated_Birth_Parents__Year_" + year + ".csv"));
//        // Output xml
//        XMLConverter.savePopulationToXMLFile(
//                new File(demographicsDirectory_File,
//                regionID + "_Population__Simulated_Birth_Parents__Year_" + year + ".xml"),
//                populationOfParentsOfNewborns);
        // Output Age Gender Plot
        title = regionID + "_Population _Simulated_Birth_Parents_ Year " + year;
        outputAgeGenderPlot(
                executorService,
                this,
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                femaleAgeBoundCountOfLabours_TreeMap,
                null,
                minAgeYearsForFertilityDisplays,
                maxAgeYearsForFertilityDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        // Twin birth parents
        // ------------------
        GENESIS_Population parentsOfTwins = new GENESIS_Population(_GENESIS_Environment);
        parentsOfTwins._FemaleAgeBoundPopulationCount_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Population(
                femaleAgeBoundCountOfTwins_TreeMap);
        parentsOfTwins.updateGenderedAgePopulation();
//        parentsOfTwins.getGenderedAgeBoundPopulation().getFemale().addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundPopulation(
//                femaleAgeBoundCountOfTwins_TreeMap));
        title = regionID + " Population _Simulated_Twin_Parents_ Year " + year;
        output(
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                parentsOfTwins,
                minAgeYearsForFertilityDisplays,
                maxAgeYearsForFertilityDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        // Triplet birth parents
        // ---------------------
        GENESIS_Population parentsOfTriplets = new GENESIS_Population(_GENESIS_Environment);
        parentsOfTriplets._FemaleAgeBoundPopulationCount_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Population(
                femaleAgeBoundCountOfTriplets_TreeMap);
        parentsOfTriplets.updateGenderedAgePopulation();
//        parentsOfTriplets.getGenderedAgeBoundPopulation().getFemale().addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundPopulation(
//                femaleAgeBoundCountOfTriplets_TreeMap));
        title = regionID + " Population _Simulated_Triplet_Parents_ Year " + year;
        output(
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                parentsOfTriplets,
                minAgeYearsForFertilityDisplays,
                maxAgeYearsForFertilityDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        // ---------------
        // Fertility rates
        // ---------------
        // AllBirths
        Iterator<GENESIS_AgeBound> ite = femaleAgeBoundCountOfBirths_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            numberOfDays_BigDecimal =
                    populationAliveDaysThisYear._FemaleAgeBoundPopulationCount_TreeMap.get(
                    ageBound);
            countOfBirths_BigDecimal = femaleAgeBoundCountOfBirths_TreeMap.get(ageBound);
            if (numberOfDays_BigDecimal == null) {
                annual_Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap.put(
                        ageBound,
                        BigDecimal.ZERO);
            } else {
                if (numberOfDays_BigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                    annual_Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap.put(
                            ageBound,
                            BigDecimal.ZERO);
                } else {
//                    numberOfYearsOfAgeInYears =
//                            Generic_BigDecimal.divideRoundIfNecessary(
//                            numberOfDays_BigDecimal,
//                            numberOfDaysInYear_BigDecimal,
//                            decimalPlacePrecisionForCalculations,
//                            roundingMode);
//                    annualFertilityProbability_BigDecimal =
//                            Generic_BigDecimal.divideRoundIfNecessary(
//                            countOfBirths_BigDecimal,
//                            numberOfYearsOfAgeInYears,
//                            decimalPlacePrecisionForCalculations,
//                            roundingMode);
                    annualFertilityProbability_BigDecimal =
                            Generic_BigDecimal.divideRoundIfNecessary(
                            countOfBirths_BigDecimal.multiply(numberOfDaysInYear_BigDecimal),
                            numberOfDays_BigDecimal,
                            decimalPlacePrecisionForCalculations,
                            roundingMode);
                    annual_Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap.put(
                            ageBound,
                            annualFertilityProbability_BigDecimal);
                }
            }
        }
        // Twins
        ite = femaleAgeBoundCountOfBirths_TreeMap.keySet().iterator();
        BigDecimal countOfTwins_BigDecimal;
        BigDecimal twinsProbability_BigDecimal;
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            countOfBirths_BigDecimal = femaleAgeBoundCountOfBirths_TreeMap.get(ageBound);
            countOfTwins_BigDecimal = femaleAgeBoundCountOfTwins_TreeMap.get(ageBound);
            if (countOfTwins_BigDecimal == null) {
                twinsProbability_BigDecimal = BigDecimal.ZERO;
            } else {
                twinsProbability_BigDecimal = Generic_BigDecimal.divideRoundIfNecessary(
                        countOfTwins_BigDecimal,
                        countOfBirths_BigDecimal,
                        decimalPlacePrecisionForCalculations,
                        roundingMode);
            }
            annual_Fertility._TwinPregnancyAgeBoundProbability_TreeMap.put(
                    ageBound, twinsProbability_BigDecimal);
        }
        // Triplets
        ite = femaleAgeBoundCountOfBirths_TreeMap.keySet().iterator();
        BigDecimal countOfTriplets_BigDecimal;
        BigDecimal tripletProbability_BigDecimal;
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            countOfBirths_BigDecimal = femaleAgeBoundCountOfBirths_TreeMap.get(ageBound);
            countOfTriplets_BigDecimal = femaleAgeBoundCountOfTriplets_TreeMap.get(ageBound);
            if (countOfTriplets_BigDecimal == null) {
                tripletProbability_BigDecimal = BigDecimal.ZERO;
            } else {
                tripletProbability_BigDecimal = Generic_BigDecimal.divideRoundIfNecessary(
                        countOfTriplets_BigDecimal,
                        countOfBirths_BigDecimal,
                        decimalPlacePrecisionForCalculations,
                        roundingMode);
            }
            annual_Fertility._TripletPregnancyAgeBoundProbability_TreeMap.put(
                    ageBound, tripletProbability_BigDecimal);
        }
        annual_Fertility.updateLists();
        annual_Fertility.writeToCSV(
                new File(demographicsDirectory_File,
                regionID + "_Fertility__Simulated__Year_" + year + ".csv"));
        XMLConverter.saveFertilityToXMLFile(
                new File(demographicsDirectory_File,
                regionID + "_Fertility__Simulated__Year_" + year + ".xml"),
                annual_Fertility);
//        a_XMLConverter.saveFertilityToXMLFile(
//                new File(demographicsDirectory_File,
//                "Fertility_" + _GENESIS_Environment._Time._Year + ".xml"),
//                _GENESIS_Environment._Time._Year,
//                annual_Fertility);
        boolean drawOriginLinesOnPlot = false;
        xAxisLabel = "Annual Live Birth Fertility Rate Input (X)";
        yAxisLabel = "Simulated Annual Live Birth Fertility Rate (Y)";
        CompareProbabilities.output(
                executorService,
                fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap,
                annual_Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap,
                regionID + "_Fertility Female Probability Comparison " + year,
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                year,
                demographicsDirectory_File);
        xAxisLabel = "Twin Birth Probability Input (X)";
        yAxisLabel = "Simulated Twin Birth Rate (Y)";
        CompareProbabilities.output(
                executorService,
                fertility._TwinPregnancyAgeBoundProbability_TreeMap,
                annual_Fertility._TwinPregnancyAgeBoundProbability_TreeMap,
                regionID + "_Twin Probability Comparison " + year,
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                year,
                demographicsDirectory_File);
        xAxisLabel = "Triplet Birth Probability Input (X)";
        yAxisLabel = "Simulated Triplet Birth Rate (Y)";
        CompareProbabilities.output(
                executorService,
                fertility._TripletPregnancyAgeBoundProbability_TreeMap,
                annual_Fertility._TripletPregnancyAgeBoundProbability_TreeMap,
                regionID + "_Triplet Probability Comparison " + year,
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                year,
                demographicsDirectory_File);
        return annual_Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap;
    }

    /**
     * a measure of the number of days in late pregnancy (ndlp) cm * 224 / ndlp
     * (N.B. The simple formula: ((number of births) / (number of miscarriages))
     * is problematic when either the numerator or denominator are zero.)
     *
     * @param femaleAgeInYearsCountOfClinicalMiscarriage_TreeMap
     * @param femaleAgeInYearsCountOfBirths_TreeMap
     * @param femaleAgeInYearsCountOfDaysInPregnancy_TreeMap
     * @param scale
     * @param decimalPlacePrecisionForCalculation
     * @param a_RoundingMode
     * @return 
     */
    protected TreeMap<GENESIS_AgeBound, BigDecimal> getClinicalMiscarriageRates(
            String regionID,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfMiscarriage_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfDaysInPregnancy_TreeMap,
            int decimalPlacePrecisionForCalculation,
            RoundingMode a_RoundingMode) {
        TreeMap<GENESIS_AgeBound, BigDecimal> observedClinicalMiscarriageRates = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        BigDecimal numberOfDays_BigDecimal;
        BigDecimal countOfMiscarriages_BigDecimal;
        BigDecimal numberOfDaysInLatePregnancy = BigDecimal.valueOf(
                //_Miscarriage.get(regionID).get(regionID).getNumberOfDaysInLatePregnancy_double());
                _Fertility.get(regionID).get(regionID)._Miscarriage.getNumberOfDaysInLatePregnancy_double());
        Iterator<GENESIS_AgeBound> ite = femaleAgeInYearsCountOfDaysInPregnancy_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            countOfMiscarriages_BigDecimal = femaleAgeInYearsCountOfMiscarriage_TreeMap.get(
                    ageBound);
            if (countOfMiscarriages_BigDecimal == null) {
                observedClinicalMiscarriageRates.put(ageBound, BigDecimal.ZERO);
            } else {
                numberOfDays_BigDecimal = femaleAgeInYearsCountOfDaysInPregnancy_TreeMap.get(
                        ageBound);
                BigDecimal divisor = Generic_BigDecimal.divideRoundIfNecessary(
                        numberOfDays_BigDecimal,
                        numberOfDaysInLatePregnancy,
                        decimalPlacePrecisionForCalculation,
                        a_RoundingMode);
                BigDecimal miscarriageRate = Generic_BigDecimal.divideRoundIfNecessary(
                        countOfMiscarriages_BigDecimal,
                        divisor,
                        decimalPlacePrecisionForCalculation,
                        a_RoundingMode);
                observedClinicalMiscarriageRates.put(ageBound, miscarriageRate);
            }
        }
        return observedClinicalMiscarriageRates;
    }

    /**
     * A measure of the number of days in early pregnancy (ndep) epl * 42 / ndep
     * (N.B. The simple formula: ((number of births) / (number of miscarriages))
     * is problematic when either the numerator or denominator are zero.)
     *
     * @param femaleAgeInYearsCountOfClinicalMiscarriage_TreeMap
     * @param femaleAgeInYearsCountOfMiscarriage_TreeMap
     * @param femaleAgeInYearsCountOfBirths_TreeMap
     * @param femaleAgeInYearsCountOfDaysInPregnancy_TreeMap
     * @param scale
     * @param a_RoundingMode
     * @return 
     */
    protected TreeMap<GENESIS_AgeBound, BigDecimal> getEarlyPregnancyLossRates(
            String regionID,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfMiscarriage_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfDaysInPregnancy_TreeMap,
            int scale,
            RoundingMode a_RoundingMode) {
        TreeMap<GENESIS_AgeBound, BigDecimal> observedEarlyPregnancyLoss = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        BigDecimal numberOfDays_BigDecimal;
        BigDecimal countOfMiscarriages_BigDecimal;
        BigDecimal numberOfDaysInEarlyPregnancy = BigDecimal.valueOf(
                //_Miscarriage.get(regionID).get(regionID).getNumberOfDaysInEarlyPregnancy());
                _Fertility.get(regionID).get(regionID)._Miscarriage.getNumberOfDaysInEarlyPregnancy());
        Iterator<GENESIS_AgeBound> ite = femaleAgeInYearsCountOfDaysInPregnancy_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            countOfMiscarriages_BigDecimal = femaleAgeInYearsCountOfMiscarriage_TreeMap.get(ageBound);
            if (countOfMiscarriages_BigDecimal == null) {
                observedEarlyPregnancyLoss.put(ageBound, BigDecimal.ZERO);
            } else {
                numberOfDays_BigDecimal = femaleAgeInYearsCountOfDaysInPregnancy_TreeMap.get(
                        ageBound);
                BigDecimal divisor = Generic_BigDecimal.divideRoundIfNecessary(
                        numberOfDays_BigDecimal, numberOfDaysInEarlyPregnancy, scale, a_RoundingMode);
                BigDecimal miscarriageRate = Generic_BigDecimal.divideRoundIfNecessary(
                        countOfMiscarriages_BigDecimal, divisor, scale, a_RoundingMode);
                observedEarlyPregnancyLoss.put(ageBound, miscarriageRate);
            }
        }
        return observedEarlyPregnancyLoss;
    }

    /**
     * Calculates and exports simulated mortality rates for males and females
     * and produces a comparison plot for these plotting the simulated
     * (observed) rates against the input mortality rates.
     *
     * @param outputImages
     * @param regionID
     * @param demographicsDirectory_File
     * @param populationDaysAliveInTimePeriod
     * @param populationDeathsInTimePeriod
     * @param decimalPlacePrecisionForCalculations
     * @param a_RoundingMode
     * @param decimalPlacePrecisionForDisplay
     * @param maxAgeYearsForAllAgesPopulationDisplays
     * @param minAgeYearsForAllAgesPopulationDisplays
     */
    protected void outputMortality(
            //ExecutorService executorService,
            String regionID,
            GENESIS_Mortality mortality,
            int dataWidthForAgeGenderPlots,
            int dataHeightForAgeGenderPlots,
            int dataWidthForScatterAndRegressionPlots,
            int dataHeightForScatterAndRegressionPlots,
            File demographicsDirectory_File,
            GENESIS_Population populationDaysAliveInTimePeriod,
            GENESIS_Population populationDeathsInTimePeriod,
            long minAgeYearsForAllAgesPopulationDisplays,
            long maxAgeYearsForAllAgesPopulationDisplays,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay,
            RoundingMode a_RoundingMode) {
        GENESIS_Mortality annual_Mortality = new GENESIS_Mortality();
        annual_Mortality.ge = _GENESIS_Environment;
        annual_Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        annual_Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        BigDecimal annualMortalityRate;
        Iterator<GENESIS_AgeBound> ite;
        GENESIS_AgeBound ageBound;
        BigDecimal countOfDeaths_BigDecimal;
        BigDecimal numberOfDays_BigDecimal;
        //BigDecimal numberOfYearsOfAgeInYears;
        BigDecimal numberOfDaysInYear_BigDecimal =
                new BigDecimal(GENESIS_Time.NormalDaysInYear_BigInteger);
        // Females
        ite = populationDeathsInTimePeriod._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            ageBound = ite.next();
            numberOfDays_BigDecimal = populationDaysAliveInTimePeriod._FemaleAgeBoundPopulationCount_TreeMap.get(
                    ageBound);
            countOfDeaths_BigDecimal = populationDeathsInTimePeriod._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (countOfDeaths_BigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                annual_Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap.put(
                        ageBound,
                        BigDecimal.ZERO);
            } else {
                if (numberOfDays_BigDecimal == null) {
                    if (countOfDeaths_BigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                        annual_Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap.put(
                                ageBound,
                                countOfDeaths_BigDecimal);
                        //BigDecimal.ONE);
                    }
                } else {
                    if (numberOfDays_BigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                        annual_Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap.put(
                                ageBound,
                                countOfDeaths_BigDecimal);
                        //BigDecimal.ONE);
                    } else {
//                        numberOfYearsOfAgeInYears =
//                                Generic_BigDecimal.divideRoundIfNecessary(
//                                numberOfDays_BigDecimal,
//                                numberOfDaysInYear_BigDecimal,
//                                decimalPlacePrecisionForCalculations,
//                                a_RoundingMode);
//                        annualMortalityRate =
//                                Generic_BigDecimal.divideRoundIfNecessary(
//                                countOfDeaths_BigDecimal,
//                                numberOfYearsOfAgeInYears,
//                                decimalPlacePrecisionForCalculations,
//                                a_RoundingMode);
                        annualMortalityRate =
                                Generic_BigDecimal.divideRoundIfNecessary(
                                countOfDeaths_BigDecimal.multiply(numberOfDaysInYear_BigDecimal),
                                numberOfDays_BigDecimal,
                                decimalPlacePrecisionForCalculations,
                                a_RoundingMode);
                        annual_Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap.put(
                                ageBound,
                                annualMortalityRate);
                    }
                }
            }
        }
        // Males
        ite = populationDeathsInTimePeriod._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            ageBound = ite.next();
            numberOfDays_BigDecimal = populationDaysAliveInTimePeriod._MaleAgeBoundPopulationCount_TreeMap.get(
                    ageBound);
            countOfDeaths_BigDecimal = populationDeathsInTimePeriod._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (countOfDeaths_BigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                annual_Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap.put(
                        ageBound,
                        BigDecimal.ZERO);
            } else {
                if (numberOfDays_BigDecimal == null) {
                    if (countOfDeaths_BigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                        annual_Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap.put(
                                ageBound,
                                countOfDeaths_BigDecimal);
                        //BigDecimal.ONE);
                    }
                } else {
                    if (numberOfDays_BigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                        annual_Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap.put(
                                ageBound,
                                countOfDeaths_BigDecimal);
                        //BigDecimal.ONE);
                    } else {
//                        numberOfYearsOfAgeInYears =
//                                Generic_BigDecimal.divideRoundIfNecessary(
//                                numberOfDays_BigDecimal,
//                                numberOfDaysInYear_BigDecimal,
//                                decimalPlacePrecisionForCalculations,
//                                a_RoundingMode);
//                        annualMortalityRate =
//                                Generic_BigDecimal.divideRoundIfNecessary(
//                                countOfDeaths_BigDecimal,
//                                numberOfYearsOfAgeInYears,
//                                decimalPlacePrecisionForCalculations,
//                                a_RoundingMode);
//                    annualMortalityProbability_BigDecimal =
//                            Generic_BigDecimal.divideRoundIfNecessary(
//                            countOfDeaths_BigDecimal,
//                            numberOfDays_BigDecimal,
//                            decimalPlacePrecisionForCalculation,
//                            a_RoundingMode);
                        annualMortalityRate =
                                Generic_BigDecimal.divideRoundIfNecessary(
                                countOfDeaths_BigDecimal.multiply(numberOfDaysInYear_BigDecimal),
                                numberOfDays_BigDecimal,
                                decimalPlacePrecisionForCalculations,
                                a_RoundingMode);
                        annual_Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap.put(
                                ageBound,
                                annualMortalityRate);
                    }
                }
            }
        }
        annual_Mortality.updateGenderedAgeBoundRates();
//        annual_Mortality.getGenderedAgeBoundRates().getFemale().addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
//                annual_Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap));
//        annual_Mortality.getGenderedAgeBoundRates().getMale().addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundRate(
//                annual_Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap));
        long year = _GENESIS_Environment._Time.getYear();
        String xAxisLabel;
        String yAxisLabel;
        boolean drawOriginLinesOnPlot = false;
        xAxisLabel = "Annual Mortality Rate Input (X)";
        yAxisLabel = "Simulated Annual Mortality Rate (Y)";
        CompareProbabilities.output(
                executorService,
                mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap,
                annual_Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap,
                regionID + " Female Mortality Rate Comparison",
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                a_RoundingMode,
                year,
                demographicsDirectory_File);
        CompareProbabilities.output(
                executorService,
                mortality._MaleAnnualMortalityAgeBoundRate_TreeMap,
                annual_Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap,
                regionID + " Male Mortality Rate Comparison",
                dataWidthForScatterAndRegressionPlots,
                dataHeightForScatterAndRegressionPlots,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                a_RoundingMode,
                year,
                demographicsDirectory_File);
        String title;
        title = regionID + " Mortality Rate Simulated Year " + year;
        title = title.replace(" ", "_");
        annual_Mortality.writeToCSV(
                new File(demographicsDirectory_File,
                title + ".csv"));
        XMLConverter.saveMortalityToXMLFile(
                new File(demographicsDirectory_File,
                title + ".xml"),
                annual_Mortality);
        //String rangeAxisLabel = "";
        xAxisLabel = "Rate";
        yAxisLabel = "Age";
        outputAgeGenderPlot(
                executorService,
                this,
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                annual_Mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap,
                annual_Mortality._MaleAnnualMortalityAgeBoundRate_TreeMap,
                minAgeYearsForAllAgesPopulationDisplays,
                maxAgeYearsForAllAgesPopulationDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
    }

    protected void output(
            String title,
            int dataWidthForAgeGenderPlots,
            int dataHeightForAgeGenderPlots,
            String xAxisLabel,
            String yAxisLabel,
            File demographicsDirectory_File,
            GENESIS_Population pop,
            int decimalPlacePrecisionForCalculation,
            int significantDigits) {
        Long minAgeYearsForDisplays = pop.getMinAgeYears();
        Long maxAgeYearsForDisplays = pop.getMaxAgeYears();
        output(
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                pop,
                minAgeYearsForDisplays,
                maxAgeYearsForDisplays,
                decimalPlacePrecisionForCalculation,
                significantDigits);
    }

    //protected Future output(
    protected void output(
            String title,
            int dataWidthForAgeGenderPlots,
            int dataHeightForAgeGenderPlots,
            String xAxisLabel,
            String yAxisLabel,
            File demographicsDirectory_File,
            GENESIS_Population pop,
            Long minAgeYearsForDisplays,
            Long maxAgeYearsForDisplays,
            int decimalPlacePrecisionForCalculations,
            int significantDigits) {
        //Future result = null;
        int decimalPlacePrecisionForDisplay = Generic_BigDecimal.getDecimalPlacePrecision(
                pop.getMaxPopulationInAnyAgeBound(),
                significantDigits);
        // Output AgeGenderPlot
        //result = outputAgeGenderPlot(
        outputAgeGenderPlot(
                executorService,
                this,
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                pop,
                minAgeYearsForDisplays,
                maxAgeYearsForDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        // Output csv
        title = title.replace(" ", "_");
        pop.writeToCSV(
                new File(demographicsDirectory_File,
                title + ".csv"));
        // Output xml
        XMLConverter.savePopulationToXMLFile(
                new File(demographicsDirectory_File,
                title + ".xml"),
                pop);
        //return result;
    }

    protected void output(
            String title,
            int dataWidthForAgeGenderPlots,
            int dataHeightForAgeGenderPlots,
            String xAxisLabel,
            String yAxisLabel,
            File demographicsDirectory_File,
            GENESIS_Miscarriage miscarriage,
            Long minAgeYearsForDisplays,
            Long maxAgeYearsForDisplays,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay) {
        // Do both clinical and early
        outputAgeGenderPlot(
                executorService,
                this,
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap,
                null,
                minAgeYearsForDisplays,
                maxAgeYearsForDisplays,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        // Output csv
        title = title.replace(" ", "_");
        miscarriage.writeToCSV(
                new File(demographicsDirectory_File,
                title + ".csv"));
        // Output xml
        XMLConverter.saveMiscarriageToXMLFile(
                new File(demographicsDirectory_File,
                title + ".xml"),
                miscarriage);
        //return result;
    }

    protected HashSet<Future> outputMiscarriage(
            String regionID,
            int dataWidthForAgeGenderPlots,
            int dataHeightForAgeGenderPlots,
            int dataWidthForScatterAndRegressionPlots,
            int dataHeightForScatterAndRegressionPlots,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfClinicalMiscarriage_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap,
            File demographicsDirectory_File,
            Long minAgeYearsForFertilityDisplays,
            Long maxAgeYearsForFertilityDisplays,
            int decimalPlacePrecisionForCalculation,
            int significantDigits,
            RoundingMode a_RoundingMode) {
        HashSet<Future> result = new HashSet<Future>();
        Future atomicResult;
        String title;
        String xAxisLabel;
        String yAxisLabel = "Age";

        // ------------------
        // Output populations
        // ------------------
        long year = _GENESIS_Environment._Time.getYear();

        // Early pregnancy loss
        GENESIS_Population earlyPregnancyLoss_Population = new GENESIS_Population(
                _GENESIS_Environment);
        earlyPregnancyLoss_Population._FemaleAgeBoundPopulationCount_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Population(
                femaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap);
        earlyPregnancyLoss_Population.updateGenderedAgePopulation();
//        earlyPregnancyLoss_Population.getGenderedAgeBoundPopulation().getFemale().addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundPopulation(
//                femaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap));
        xAxisLabel = "Early Pregnancy Loss";
        title = regionID + " " + xAxisLabel + " " + year;
        //atomicResult = output(outputImages,
        output(
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                earlyPregnancyLoss_Population,
                minAgeYearsForFertilityDisplays,
                maxAgeYearsForFertilityDisplays,
                decimalPlacePrecisionForCalculation,
                significantDigits);
        //result.add(atomicResult);
        // Count of days in early pregnancy
        GENESIS_Population earlyPregnancy_Population = new GENESIS_Population(
                _GENESIS_Environment);
        earlyPregnancy_Population._FemaleAgeBoundPopulationCount_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Population(
                femaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap);
        earlyPregnancy_Population.updateGenderedAgePopulation();
//        earlyPregnancy_Population.getGenderedAgeBoundPopulation().getFemale().addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundPopulation(
//                femaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap));
        xAxisLabel = "Early Pregnancy Days";
        title = regionID + " " + xAxisLabel + " " + year;
        //atomicResult = output(outputImages,
        output(
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                earlyPregnancy_Population,
                minAgeYearsForFertilityDisplays,
                maxAgeYearsForFertilityDisplays,
                decimalPlacePrecisionForCalculation,
                significantDigits);
        //result.add(atomicResult);

        // Clinical miscarriage
        GENESIS_Population clinicalMiscarriage_Population = new GENESIS_Population(
                _GENESIS_Environment);
        clinicalMiscarriage_Population._FemaleAgeBoundPopulationCount_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Population(
                femaleAgeInYearsCountOfClinicalMiscarriage_TreeMap);
        clinicalMiscarriage_Population.updateGenderedAgePopulation();
//        clinicalMiscarriage_Population.getGenderedAgeBoundPopulation().getFemale().addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundPopulation(
//                femaleAgeInYearsCountOfClinicalMiscarriage_TreeMap));
        xAxisLabel = "Clinical Miscarriage";
        title = regionID + " " + xAxisLabel + " " + year;
        //atomicResult = output(outputImages,
        output(
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                clinicalMiscarriage_Population,
                minAgeYearsForFertilityDisplays,
                maxAgeYearsForFertilityDisplays,
                decimalPlacePrecisionForCalculation,
                significantDigits);
        //result.add(atomicResult);

        // Late pregnancy days
        GENESIS_Population latePregnancy_Population = new GENESIS_Population(
                _GENESIS_Environment);
        latePregnancy_Population._FemaleAgeBoundPopulationCount_TreeMap =
                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Population(
                femaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap);
        latePregnancy_Population.updateGenderedAgePopulation();
//        latePregnancy_Population.getGenderedAgeBoundPopulation().getFemale().addAll(
//                GENESIS_Collections.deepCopyTo_ArrayList_AgeBoundPopulation(
//                femaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap));
        xAxisLabel = "Late Pregnancy Days";
        title = regionID + " " + xAxisLabel + " " + year;
        //atomicResult = output(outputImages,
        output(
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                latePregnancy_Population,
                minAgeYearsForFertilityDisplays,
                maxAgeYearsForFertilityDisplays,
                decimalPlacePrecisionForCalculation,
                significantDigits);
        //result.add(atomicResult);

//        // -----
//        // Rates
//        // -----
//        TreeMap<GENESIS_AgeBound, BigDecimal> observedClinicalMiscarriageRates =
//                getClinicalMiscarriageRates(
//                regionID,
//                femaleAgeInYearsCountOfClinicalMiscarriage_TreeMap,
//                femaleAgeInYearsCountOfDaysInLatePregnancy_TreeMap,
//                decimalPlacePrecisionForCalculation,
//                a_RoundingMode);
//        title = regionID + " Clinical Miscarriage Rate Simulated Comparison Year " + year;
//        String xAxisLabel;
//        String yAxisLabel;
//        boolean drawOriginLinesOnPlot = false;
//        int decimalPlacePrecisionForDisplay = 10;
//        xAxisLabel = "Input Miscarriage Rate (X)";
//        yAxisLabel = "Simulated Miscarriage Rate (Y)";
//        CompareProbabilities.output(
//                executorService,
//                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Rate(
//                _Miscarriage.get(regionID).get(regionID).getClinicalMiscarriageAgeRate()),
//                observedClinicalMiscarriageRates,
//                title,
//                dataWidthForScatterAndRegressionPlots,
//                dataHeightForScatterAndRegressionPlots,
//                xAxisLabel,
//                yAxisLabel,
//                drawOriginLinesOnPlot,
//                decimalPlacePrecisionForCalculation,
//                decimalPlacePrecisionForDisplay,
//                a_RoundingMode,
//                year,
//                demographicsDirectory_File);
//        TreeMap<GENESIS_AgeBound, BigDecimal> observedEarlyPregnancyLossRates;
//        observedEarlyPregnancyLossRates = getEarlyPregnancyLossRates(
//                regionID,
//                femaleAgeInYearsCountOfEarlyPregnancyLoss_TreeMap,
//                femaleAgeInYearsCountOfDaysInEarlyPregnancy_TreeMap,
//                decimalPlacePrecisionForCalculation,
//                a_RoundingMode);
//        title = regionID + " Early Pregnancy Loss Rate Comparison Year " + year;
//        xAxisLabel = "Input Miscarriage Rate (X)";
//        yAxisLabel = "Simulated Miscarriage Rate (Y)";
//        CompareProbabilities.output(
//                executorService,
//                GENESIS_Collections.deepCopyTo_TreeMap_AgeBound_Rate(
//                _Miscarriage.get(regionID).get(regionID).getEarlyPregnancyLossAgeRate()),
//                observedEarlyPregnancyLossRates,
//                title,
//                dataWidthForScatterAndRegressionPlots,
//                dataHeightForScatterAndRegressionPlots,
//                xAxisLabel,
//                yAxisLabel,
//                drawOriginLinesOnPlot,
//                decimalPlacePrecisionForCalculation,
//                decimalPlacePrecisionForDisplay,
//                a_RoundingMode,
//                year,
//                demographicsDirectory_File);
//        GENESIS_Miscarriage miscarriage = new GENESIS_Miscarriage(_GENESIS_Environment);
//        miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap =
//                observedClinicalMiscarriageRates;
//        miscarriage._DailyEarlyPregnancyLossAgeBoundProbability_TreeMap =
//                observedEarlyPregnancyLossRates;
//        miscarriage.updateLists();
//        miscarriage.setExpectedNumberOfDaysInFullTermPregnancy(
//                _Miscarriage.get(regionID).get(regionID).getExpectedNumberOfDaysInFullTermPregnancy());
//        miscarriage.setNumberOfDaysInEarlyPregnancy(
//                _Miscarriage.get(regionID).get(regionID).getNumberOfDaysInEarlyPregnancy());
//        miscarriage.writeToCSV(
//                new File(demographicsDirectory_File,
//                regionID + "_Miscarriage__Simulated__Year_" + year + ".csv"));
//        XMLConverter.saveMiscarriageToXMLFile(
//                new File(demographicsDirectory_File,
//                regionID + "_Miscarriage__Simulated__Year_" + year + ".xml"),
//                miscarriage);
        return result;
    }

    //protected Future outputAgeGenderPlot(
    protected static void outputAgeGenderPlot(
            ExecutorService executorService,
            Object obj,
            String title,
            int dataWidthForAgeGenderPlots,
            int dataHeightForAgeGenderPlots,
            String xAxisLabel,
            String yAxisLabel,
            File demographicsDirectory_File,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalEndYearAgedPopulation_TreeMap,
            TreeMap<GENESIS_AgeBound, BigDecimal> maleTheoreticalEndYearAgedPopulation_TreeMap,
            Long minAgeInYears,
            Long maxAgeInYears,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay) {
        String methodName = "outputAgeGenderPlot(ExecutorService,Object,String,"
                + "int,int,String,File,TreeMap<GENESIS_AgeBound,BigDecimal>,"
                + "TreeMap<GENESIS_AgeBound,BigDecimal>,Long,Long,int,int)";
        GENESIS_Population pop = new GENESIS_Population();
        pop._FemaleAgeBoundPopulationCount_TreeMap = femaleTheoreticalEndYearAgedPopulation_TreeMap;
        pop._MaleAgeBoundPopulationCount_TreeMap = maleTheoreticalEndYearAgedPopulation_TreeMap;
        pop.updateGenderedAgePopulation();
        //result = outputAgeGenderPlot(
        outputAgeGenderPlot(
                executorService,
                obj,
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                pop,
                minAgeInYears,
                maxAgeInYears,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay);
        //return result;
    }

//    protected static void outputAgeGenderPlot(
//            String title,
//            String rangeAxisLabel,
//            File demographicsDirectory_File,
//            boolean convertDaysToYears,
//            GENESIS_Population a_Population,
//            RoundingMode a_RoundingMode) {
//        int femaleMaxAgeInYears = Generic_Collections.getMaxKey_Integer(
//                a_Population._FemaleAgeBoundPopulationCount_TreeMap,
//                new Integer(-1));
//        int maleMaxAgeInYears = Generic_Collections.getMaxKey_Integer(
//                a_Population._FemaleAgeBoundPopulationCount_TreeMap,
//                new Integer(-1));
//        int maxAgeInYears = Math.max(femaleMaxAgeInYears, maleMaxAgeInYears);
//        BigDecimal defaultMax_BigDecimal = new BigDecimal(Double.toString(Double.MIN_VALUE));
//        //BigInteger defaultMin_BigInteger = new BigInteger(Long.toString(Long.MAX_VALUE));
//        BigDecimal femaleMaxAgeInYearsCount_BigDecimal = Generic_Collections.getMaxValue_BigDecimal(
//                a_Population._FemaleAgeBoundPopulationCount_TreeMap,
//                defaultMax_BigDecimal);
//        BigDecimal maleMaxAgeInYearsCount_BigDecimal = Generic_Collections.getMaxValue_BigDecimal(
//                a_Population._MaleAgeBoundPopulationCount_TreeMap,
//                defaultMax_BigDecimal);
//        // Convert from days to years
//        int decimalPlaces = 3;
//        if (convertDaysToYears) {
//            femaleMaxAgeInYearsCount_BigDecimal =
//                    Generic_BigDecimal.divideRoundIfNecessary(
//                    femaleMaxAgeInYearsCount_BigDecimal,
//                    GENESIS_Time.NormalDaysInYear_BigInteger,
//                    decimalPlaces,
//                    a_RoundingMode);
//            maleMaxAgeInYearsCount_BigDecimal =
//                    Generic_BigDecimal.divideRoundIfNecessary(
//                    maleMaxAgeInYearsCount_BigDecimal,
//                    GENESIS_Time.NormalDaysInYear_BigInteger,
//                    decimalPlaces,
//                    a_RoundingMode);
//        }
//        BigDecimal maxAgeInYearsCount_BigDecimal = Generic_BigDecimal.max(
//                maleMaxAgeInYearsCount_BigDecimal,
//                femaleMaxAgeInYearsCount_BigDecimal);
//        log(Level.FINE, "maxAgeInYears " + maxAgeInYears);
//        log(Level.FINE, "maxCount of any Age/Gender category " + maxAgeInYearsCount_BigDecimal);
//        // Generate DefaultCategoryDataset
//        DefaultCategoryDataset a_DefaultCategoryDataset = new DefaultCategoryDataset();
//        Integer key;
//        BigDecimal maleAgeInYearsCount_BigDecimal;
//        BigDecimal femaleAgeInYearsCount_BigDecimal;
//        BigDecimal count = null;
//        // for (int ageBound = 0; ageBound <= _MaxAge; ageBound++) {
//        for (int ageBound = maxAgeInYears; ageBound > -1; ageBound--) {
//            // for (int ageBound = 60; ageBound > -1; ageBound--) {
//            key = new Integer(ageBound);
//            if (a_Population._MaleAgeBoundPopulationCount_TreeMap.containsKey(key)) {
//                count = a_Population._MaleAgeBoundPopulationCount_TreeMap.get(key);
//            }
//            if (count == null) {
//                count = BigDecimal.ZERO;
//            }
//            // Convert to years
//            if (convertDaysToYears) {
//                maleAgeInYearsCount_BigDecimal =
//                        Generic_BigDecimal.divideRoundIfNecessary(
//                        count,
//                        GENESIS_Time.NormalDaysInYear_BigInteger,
//                        decimalPlaces,
//                        a_RoundingMode);
//            } else {
//                maleAgeInYearsCount_BigDecimal = new BigDecimal(count.toString());
//            }
//            a_DefaultCategoryDataset.addValue(
//                    maleAgeInYearsCount_BigDecimal.negate(),
//                    "Male",
//                    Integer.toString(ageBound));
//            if (a_Population._FemaleAgeBoundPopulationCount_TreeMap.containsKey(key)) {
//                count = a_Population._FemaleAgeBoundPopulationCount_TreeMap.get(key);
//            }
//            if (count == null) {
//                count = BigDecimal.ZERO;
//            }
//            // Convert to years
//            if (convertDaysToYears) {
//                femaleAgeInYearsCount_BigDecimal =
//                        Generic_BigDecimal.divideRoundIfNecessary(
//                        count,
//                        GENESIS_Time.NormalDaysInYear_BigInteger,
//                        decimalPlaces,
//                        a_RoundingMode);
//            } else {
//                femaleAgeInYearsCount_BigDecimal = new BigDecimal(count.toString());
//            }
////            if (femaleAgeInYearsCount_BigDecimal.compareTo(BigDecimal.ZERO) != 0){
//            a_DefaultCategoryDataset.addValue(
//                    femaleAgeInYearsCount_BigDecimal,
//                    "Female",
//                    Integer.toString(ageBound));
////            }
//        }
//        // Create a stacked bar chart which uses:
//        // CategoryPlot instance as the plot;
//        // CategoryAxis for the domain axis;
//        // NumberAxis as the range axis;
//        // StackedBarRenderer as the renderer.
//        JFreeChart a_JFreeChart = ChartFactory.createStackedBarChart(
//                title,
//                "Age Group", // domain
//                // axis
//                // label
//                rangeAxisLabel, // range axis label
//                a_DefaultCategoryDataset, // data
//                PlotOrientation.HORIZONTAL, true, // include legend
//                true, // tooltips
//                false // urls
//                );
//        // Get Chart components
//        CategoryPlot a_CategoryPlot = (CategoryPlot) a_JFreeChart.getPlot();
//        CategoryAxis a_CategoryAxis = a_CategoryPlot.getDomainAxis();
//        NumberAxis a_NumberAxis = (NumberAxis) a_CategoryPlot.getRangeAxis();
//        StackedBarRenderer a_StackedBarRenderer = (StackedBarRenderer) a_CategoryPlot.getRenderer();
//        // Modify Chart components
//        // _NumberAxis.setRange(-5000d, 5000d);
//        // _NumberAxis.setRange(-100d, 100d);
//        // _NumberAxis.setRange(-_Female_Population_Alive_ID_HashSet.size(),
//        // _Male_Population_Alive_ID_HashSet.size());
//        // _NumberAxis.setRange(-femaleMaxAgeCount, maleMaxAgeCount);
////        long range = maxAgeInYearsCount_BigDecimal.longValue();
//        long range = maxAgeInYearsCount_BigDecimal.longValue() + 1L;
//        a_NumberAxis.setRange(-range, range);
//        // Write out image
//        int width = 500;
//        int height = 1000;
//        String outputImageFileNamePrefix = title.replaceAll(" ", "_");
//        String type = "PNG";
//        try {
//            IO.outputJFreeChart(
//                    a_JFreeChart,
//                    width,
//                    height,
//                    demographicsDirectory_File.getCanonicalPath(),
//                    outputImageFileNamePrefix,
//                    type);
//        } catch (IOException a_IOException) {
//            log(Level.WARNING,
//                    a_IOException.getMessage());
////            a_IOException.printStackTrace();
//        }
//    }
    //protected static Future outputAgeGenderPlot(
    protected static void outputAgeGenderPlot(
            ExecutorService executorService,
            Object obj,
            String title,
            int dataWidthForAgeGenderPlots,
            int dataHeightForAgeGenderPlots,
            String xAxisLabel,
            String yAxisLabel,
            File demographicsDirectory_File,
            GENESIS_Population a_Population,
            int decimalPlacePrecisionForCalculation,
            int decimalPlacePrecisionForDisplay) {
        //Future result;
        Long minAgeInYears = a_Population.getMinAgeYears();
        Long maxAgeInYears = a_Population.getMaxAgeYears();
        //result = outputAgeGenderPlot(
        outputAgeGenderPlot(
                executorService,
                obj,
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                demographicsDirectory_File,
                a_Population,
                minAgeInYears,
                maxAgeInYears,
                decimalPlacePrecisionForCalculation,
                decimalPlacePrecisionForDisplay);
        //return result;
    }

    protected static void outputAgeGenderPlot(
            //protected static Future outputAgeGenderPlot(
            ExecutorService executorService,
            Object obj,
            String title,
            int dataWidthForAgeGenderPlots,
            int dataHeightForAgeGenderPlots,
            String xAxisLabel,
            String yAxisLabel,
            File demographicsDirectory_File,
            GENESIS_Population a_Population,
            Long minAgeInYears,
            Long maxAgeInYears,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay) {
        String methodName = "outputAgeGenderPlot(ExecutorService,Object,String,"
                + "int,int,String,File,GENESIS_Population,Long,Long,int,int)";
        //Future result = null;
        boolean drawOriginLinesOnPlot = false;//true;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        // Calculate an aggregated population. This is work repeated below, but 
        // this also creates a new object which is safe for threaded 
        // visualisationPop.
        Object[] aggregatePop = GENESIS_Population.getAggregateGENESIS_Population(
                a_Population,
                minAgeInYears,
                maxAgeInYears);
        String outputImageFileNamePrefix = title.replaceAll(" ", "_");
        String format = "PNG";
        File output_File = new File(
                demographicsDirectory_File,
                outputImageFileNamePrefix + "." + format);
        Integer startAgeOfEndYearInterval = maxAgeInYears.intValue() + 1;
        GENESIS_AgeGenderBarChart barchart = new GENESIS_AgeGenderBarChart(
                executorService,
                output_File,
                format,
                demographicsDirectory_File,
                title,
                dataWidthForAgeGenderPlots,
                dataHeightForAgeGenderPlots,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnPlot,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                null);
        // The following recomputes much of what was computed in creating 
        // aggregatePop above.
        barchart.setData((GENESIS_Population) aggregatePop[0], minAgeInYears, maxAgeInYears);
        barchart.run();
//        try {
//            result = executorService.submit(new Runnable() {
//                //executorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    barchart.run();
//                }
//            });
//        } catch (RejectedExecutionException e) {
//            // System.err.println(e.getMessage()); // e.getMessage 
//            //ThreadPoolExecutor.CallerRunsPolicy
//            long waitTime = 10000;
//            Generic_Execution.waitSychronized(barchart, waitTime);
//            System.out.println("RejectedExecutionException GENESIS_Demographics." + methodName + " waiting " + waitTime + " milliseconds before attempting execution again.");
//            System.exit(0);
//        } catch (NullPointerException e) {
//            long waitTime = 10000;
//            //Generic_Execution.waitSychronized(barchart, waitTime);
//            System.out.println(e.getMessage() + " waiting " + waitTime + " milliseconds before attempting execution again.");
//            System.exit(0);
//        }
        //return result;

    }

    public Object[] get_Population_Alive_Male_Female_ID_HashSet(GENESIS_Time a_Time) {
        Object[] result = new Object[2];
        HashSet<Long> alive_female_HashSet = new HashSet<Long>();
        HashSet<Long> alive_male_HashSet = new HashSet<Long>();
        GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager = _GENESIS_Environment.AgentEnvironment.get_AgentCollectionManager(_GENESIS_Environment.HandleOutOfMemoryError);
        for (long a_Agent_ID = 0; a_Agent_ID < a_GENESIS_AgentCollectionManager._IndexOfLastBornFemale; a_Agent_ID++) {
            GENESIS_Female a_Female = (GENESIS_Female) a_GENESIS_AgentCollectionManager.getFemale(a_Agent_ID,
                    GENESIS_Person.getTypeLivingFemale_String(),
                    _GENESIS_Environment.HandleOutOfMemoryError);
            if (a_Female.isAlive(a_Time)) {
                alive_female_HashSet.add(a_Agent_ID);
            }
        }
        for (long a_Agent_ID = 0; a_Agent_ID < a_GENESIS_AgentCollectionManager._IndexOfLastBornMale; a_Agent_ID++) {
            GENESIS_Male a_Male = (GENESIS_Male) a_GENESIS_AgentCollectionManager.getMale(a_Agent_ID,
                    GENESIS_Person.getTypeLivingMale_String(),
                    _GENESIS_Environment.HandleOutOfMemoryError);
            if (a_Male.isAlive(a_Time)) {
                alive_female_HashSet.add(a_Agent_ID);
            }
        }
        result[0] = alive_female_HashSet;
        result[1] = alive_male_HashSet;
        return result;
    }

    /**
     * Collects the theoretically expected populations and a Sum of Squared
     * Difference between this and the simulated output and returns these. The
     * theoretical populations are represented with non-integer values.
     *
     * @param regionID
     * @param input_Demographics the GENESIS_Demographics input into a
     * simulation which are to be compared with the resulting output
     * GENESIS_Demographics from a set of simulations as represented in
     * output_Demographics
     * @param output_Demographics_HashMap a HashMap with values representing the
     * Demographic outputs from a set of simulation models run from the
     * input_Demographics.
     * @param handleOutOfMemoryError
     * @return Object[] result where; result[0] is a HashMap with keys as in
     * output_Demographics_HashMap and values which are the respective sum of
     * squared errors representing the difference between the modelled
     * population and the theoretically expected population; result[1] is a
     * TreeMap&lt;Integer, BigDecimal&gt; representing the female theoretical
     * end year population; result[2] is a TreeMap&lt;Integer, BigDecimal&gt;
     * representing the male theoretical end year population; result[3] is a
     * TreeMap&lt;Integer, BigDecimal&gt; representing the female theoretical
     * population that died in the period; result[4] is a TreeMap&lt;Integer,
     * BigDecimal&gt; representing the male theoretical population that died in
     * the period; result[5] is a TreeMap&lt;Integer, BigDecimal&gt; of the
     * number of births by ageBound of female (GENESIS_Fertility).
     */
    public Object[] getExpectedAndSumOfSquaredDifferences(
            String regionID,
            GENESIS_Demographics input_Demographics,
            HashMap<GENESIS_AgeBound, GENESIS_Demographics> output_Demographics_HashMap,
            boolean handleOutOfMemoryError) {
        try {
        log(Level.FINE, "<getExpectedAndSumOfSquaredDifferences>");
        Object[] result = new Object[6];
        GENESIS_Population input_Population = input_Demographics._Population.get(regionID).get(regionID);
        GENESIS_Mortality input_Mortality = input_Demographics._Mortality.get(regionID).get(regionID);
        GENESIS_Fertility input_Fertility = input_Demographics._Fertility.get(regionID).get(regionID);
        // Get the theoretical (maximum likelihood) end year populations and 
        // fertility 
        Object[] theoreticalEndYearPopulationsAndFertility = getTheoreticalEndYearPopulationsAndFertility(
                input_Population,
                input_Mortality,
                input_Fertility,
                this._GENESIS_Environment._DecimalPlacePrecisionForCalculations,
                handleOutOfMemoryError);
        GENESIS_Population theoreticalEndYearAgedPopulation =
                (GENESIS_Population) theoreticalEndYearPopulationsAndFertility[0];
        GENESIS_Population theoreticalDeadPopulation =
                (GENESIS_Population) theoreticalEndYearPopulationsAndFertility[1];
        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalFertility =
                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[2];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalEndYearAgedPopulation =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[0];
//        TreeMap<GENESIS_AgeBound, BigDecimal> maleTheoreticalEndYearAgedPopulation =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[1];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalDeadPopulation =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[2];
//        TreeMap<GENESIS_AgeBound, BigDecimal> maleTheoreticalDeadPopulation =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[3];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalFertility =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[4];
        result[1] = theoreticalEndYearAgedPopulation._FemaleAgeBoundPopulationCount_TreeMap;
        result[2] = theoreticalEndYearAgedPopulation._MaleAgeBoundPopulationCount_TreeMap;
        result[3] = theoreticalDeadPopulation._FemaleAgeBoundPopulationCount_TreeMap;
        result[4] = theoreticalDeadPopulation._MaleAgeBoundPopulationCount_TreeMap;
        result[5] = femaleTheoreticalFertility;
        // For each output compare the map1Value end year populations and fertility 
        // with the theoretical (maximum likelihood) end year populations and 
        // fertility
        HashMap<GENESIS_AgeBound, BigDecimal> sumOfSquaredErrors_HashMap =
                new HashMap<GENESIS_AgeBound, BigDecimal>();
        Iterator<GENESIS_AgeBound> ite = output_Demographics_HashMap.keySet().iterator();
        GENESIS_AgeBound ageBound;
        while (ite.hasNext()) {
            ageBound = ite.next();
            log(Level.FINE, "Output Index " + ageBound);
            GENESIS_Demographics output_Demographics = output_Demographics_HashMap.get(ageBound);
            BigDecimal sumOfSquaredErrors = BigDecimal.ZERO;
            Object[] firstOrderStatistics;
            // Compare Female End Year Living GENESIS_Population
            TreeMap<GENESIS_AgeBound, BigDecimal> output_FemalePopulation =
                    output_Demographics._Population.get(regionID).get(regionID)._FemaleAgeBoundPopulationCount_TreeMap;
            BigDecimal femaleEndYearLivingPopulationSumOfSquaredErrors;
            log(Level.FINE, "<Compare Female End Year Living Population Modelled and Expected>");
            firstOrderStatistics = GENESIS_Statistics.getFirstOrderStatistics3(
                    output_FemalePopulation,
                    theoreticalEndYearAgedPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                    "Model",
                    "Expected",
                    "Age");
            femaleEndYearLivingPopulationSumOfSquaredErrors = (BigDecimal) firstOrderStatistics[2];
            log(Level.FINE, "<femaleEndYearLivingPopulationSumOfSquaredErrors>"
                    + femaleEndYearLivingPopulationSumOfSquaredErrors
                    + "</femaleEndYearLivingPopulationSumOfSquaredErrors>");
            sumOfSquaredErrors = sumOfSquaredErrors.add(femaleEndYearLivingPopulationSumOfSquaredErrors);
            log(Level.FINE, "</Compare Female End Year Living Population Modelled and Expected>");
            // Compare Male End Year Living GENESIS_Population
            TreeMap<GENESIS_AgeBound, BigDecimal> output_MalePopulation =
                    output_Demographics._Population.get(regionID).get(regionID)._MaleAgeBoundPopulationCount_TreeMap;
            BigDecimal maleEndYearLivingPopulationSumOfSquaredErrors;
            log(Level.FINE, "<Compare Male End Year Living Population Modelled and Expected>");
            firstOrderStatistics = GENESIS_Statistics.getFirstOrderStatistics3(
                    output_MalePopulation,
                    theoreticalEndYearAgedPopulation._MaleAgeBoundPopulationCount_TreeMap,
                    "Model",
                    "Expected",
                    "Age");
            maleEndYearLivingPopulationSumOfSquaredErrors = (BigDecimal) firstOrderStatistics[2];
            log(Level.FINE, "<maleEndYearLivingPopulationSumOfSquaredErrors>"
                    + maleEndYearLivingPopulationSumOfSquaredErrors
                    + "</maleEndYearLivingPopulationSumOfSquaredErrors>");
            sumOfSquaredErrors = sumOfSquaredErrors.add(maleEndYearLivingPopulationSumOfSquaredErrors);
            log(Level.FINE, "</Compare Male End Year Living Population Modelled and Expected>");
            // Compare Female GENESIS_Fertility
            TreeMap<GENESIS_AgeBound, BigDecimal> output_FemaleFertility =
                    output_Demographics._Fertility.get(regionID).get(regionID)._AnnualLiveBirthFertilityAgeBoundRate_TreeMap;
            BigDecimal femaleFertilitySumOfSquaredErrors;
            log(Level.FINE, "<Compare Female Fertility Modelled and Expected>");
            firstOrderStatistics = GENESIS_Statistics.getFirstOrderStatistics2(
                    output_FemaleFertility,
                    femaleTheoreticalFertility,
                    "Model",
                    "Expected",
                    "Age");
            femaleFertilitySumOfSquaredErrors = (BigDecimal) firstOrderStatistics[2];
            log(Level.FINE, "<femaleFertilitySumOfSquaredDifference>"
                    + femaleFertilitySumOfSquaredErrors
                    + "<femaleFertilitySumOfSquaredDifference>");
            sumOfSquaredErrors = sumOfSquaredErrors.add(femaleFertilitySumOfSquaredErrors);
            log(Level.FINE, "</Compare Female Fertility Modelled and Expected>");
            sumOfSquaredErrors_HashMap.put(ageBound, sumOfSquaredErrors);
        }
        result[0] = sumOfSquaredErrors_HashMap;
        log(Level.FINE, "</getExpectedAndSumOfSquaredDifferences>");
        return result;
        } catch (OutOfMemoryError e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    /**
     * Collects the theoretically expected populations and calculates the Sum of
     * Squared Differences between this and the simulated output population. The
     * Sum of Squared Differences also incorporated a measure of comparing the
     * annual fertility rates (probability corrected). (NB. The theoretical
     * populations are represented with non-integer values.)
     *
     * @param regionID
     * @param handleOutOfMemoryError
     * @param decimalPlacePrecisionForCalculations
     * @TODO (optionally) include multiple birth, miscarriage and mortality
     * rates... Change to difference input birth and death counts with what is
     * simulated.
     *
     * @param startDemographics the GENESIS_Demographics input into a simulation
     * which are to be compared with the resulting output GENESIS_Demographics
     * from a set of simulations as represented in output_Demographics
     * @param endDemographics represent the Demographic outputs from a
     * simulation model run from the input_Demographics.
     * @return Object[] result where; result[0] is the sum of squared errors
     * representing the difference between the modelled population and the
     * theoretically expected population; result[1] is a TreeMap&lt;Integer,
     * BigDecimal&gt; representing the female theoretical end year population;
     * result[2] is a TreeMap&lt;Integer, BigDecimal&gt; representing the male
     * theoretical end year population; result[3] is a TreeMap&lt;Integer,
     * BigDecimal&gt; representing the female theoretical population that died
     * in the period; result[4] is a TreeMap&lt;Integer, BigDecimal&gt;
     * representing the male theoretical population that died in the period;
     * result[5] is a TreeMap&lt;Integer, BigDecimal&gt; of the number of births
     * by ageBound of female (GENESIS_Fertility).
     */
    public static Object[] getExpectedAndSumOfSquaredDifference(
            String regionID,
            GENESIS_Demographics startDemographics,
            GENESIS_Demographics endDemographics,
            int decimalPlacePrecisionForCalculations,
            boolean handleOutOfMemoryError) {
        try {
        log(Level.FINE, "<getExpectedAndSumOfSquaredDifference>");
        Object[] result = new Object[6];
        GENESIS_Population input_Population = startDemographics._Population.get(regionID).get(regionID);
        GENESIS_Mortality input_Mortality = startDemographics._Mortality.get(regionID).get(regionID);
        //GENESIS_Miscarriage input_Miscarriage = input_Demographics._Miscarriage.get(regionID).get(regionID);
        GENESIS_Fertility input_Fertility = startDemographics._Fertility.get(regionID).get(regionID);
        // Get the theoretical (maximum likelihood) end year populations and 
        // fertility 
        Object[] theoreticalEndYearPopulationsAndFertility = getTheoreticalEndYearPopulationsAndFertility(
                input_Population,
                input_Mortality,
                input_Fertility,
                decimalPlacePrecisionForCalculations,
                handleOutOfMemoryError);
        GENESIS_Population theoreticalEndYearAgedPopulation =
                (GENESIS_Population) theoreticalEndYearPopulationsAndFertility[0];
        GENESIS_Population theoreticalDeadPopulation =
                (GENESIS_Population) theoreticalEndYearPopulationsAndFertility[1];
        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalFertility =
                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[2];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalEndYearAgedPopulation =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[0];
//        TreeMap<GENESIS_AgeBound, BigDecimal> maleTheoreticalEndYearAgedPopulation =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[1];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalDeadPopulation =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[2];
//        TreeMap<GENESIS_AgeBound, BigDecimal> maleTheoreticalDeadPopulation =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[3];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalFertility =
//                (TreeMap<GENESIS_AgeBound, BigDecimal>) theoreticalEndYearPopulationsAndFertility[4];
        result[1] = theoreticalEndYearAgedPopulation._FemaleAgeBoundPopulationCount_TreeMap;
        result[2] = theoreticalEndYearAgedPopulation._MaleAgeBoundPopulationCount_TreeMap;
        result[3] = theoreticalDeadPopulation._FemaleAgeBoundPopulationCount_TreeMap;
        result[4] = theoreticalDeadPopulation._MaleAgeBoundPopulationCount_TreeMap;
        result[5] = femaleTheoreticalFertility;
        // For each output compare the map1Value end year populations and fertility 
        // with the theoretical (maximum likelihood) end year populations and 
        // fertility
        BigDecimal sumOfSquaredErrors = BigDecimal.ZERO;
        // Compare Female End Year Living GENESIS_Population
        TreeMap<GENESIS_AgeBound, BigDecimal> output_FemalePopulation;
        output_FemalePopulation = endDemographics._Population.get(regionID).get(regionID)._FemaleAgeBoundPopulationCount_TreeMap;
        BigDecimal femaleEndYearLivingPopulationSumOfSquaredErrors;
        log(Level.FINE, "<Compare Female End Year Living Population Modelled and Expected>");
        Object[] firstOrderStatistics;
        firstOrderStatistics = GENESIS_Statistics.getFirstOrderStatistics3(
                output_FemalePopulation,
                theoreticalEndYearAgedPopulation._FemaleAgeBoundPopulationCount_TreeMap,
                "Model",
                "Expected",
                "Age");
        femaleEndYearLivingPopulationSumOfSquaredErrors = (BigDecimal) firstOrderStatistics[2];
        log(Level.FINE, "femaleEndYearLivingPopulationSumOfSquaredErrors " + femaleEndYearLivingPopulationSumOfSquaredErrors);
        sumOfSquaredErrors = sumOfSquaredErrors.add(femaleEndYearLivingPopulationSumOfSquaredErrors);
        log(Level.FINE, "</Compare Female End Year Living Population Modelled and Expected>");
        // Compare Male End Year Living GENESIS_Population
        TreeMap<GENESIS_AgeBound, BigDecimal> output_MalePopulation;
        output_MalePopulation =
                endDemographics._Population.get(regionID).get(regionID)._MaleAgeBoundPopulationCount_TreeMap;
        BigDecimal maleEndYearLivingPopulationSumOfSquaredErrors;
        log(Level.FINE, "<Compare Male End Year Living Population Modelled and Expected>");
        firstOrderStatistics = GENESIS_Statistics.getFirstOrderStatistics3(
                output_MalePopulation,
                theoreticalEndYearAgedPopulation._MaleAgeBoundPopulationCount_TreeMap,
                "Model",
                "Expected",
                "Age");
        maleEndYearLivingPopulationSumOfSquaredErrors = (BigDecimal) firstOrderStatistics[2];
        log(Level.FINE, "maleEndYearLivingPopulationSumOfSquaredErrors " + maleEndYearLivingPopulationSumOfSquaredErrors);
        sumOfSquaredErrors = sumOfSquaredErrors.add(maleEndYearLivingPopulationSumOfSquaredErrors);
        log(Level.FINE, "</Compare Male End Year Living Population Modelled and Expected>");
        // Compare Female GENESIS_Fertility
//        TreeMap<GENESIS_AgeBound, BigDecimal> output_FemaleFertility = output_Demographics._Fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap;
//        // Modify output_FemaleFertility so that probabilities of greater than one are set to one
//        output_FemaleFertility = getTreeMapWithExtremeProbabilitiesSetToOne(output_FemaleFertility);
//        BigDecimal femaleFertilitySumOfSquaredErrors;
//        log(Level.FINE, "<Compare Female Fertility Modelled and Expected>");
//        firstOrderStatistics = GENESIS_Statistics.getFirstOrderStatistics2(
//                output_FemaleFertility,
//                femaleTheoreticalFertility,
//                "Model",
//                "Expected",
//                "Age");
//        femaleFertilitySumOfSquaredErrors = (BigDecimal) firstOrderStatistics[2];
//        log(Level.FINE, "femaleFertilitySumOfSquaredErrors " + femaleFertilitySumOfSquaredErrors);
//        sumOfSquaredErrors = sumOfSquaredErrors.add(femaleFertilitySumOfSquaredErrors);
//        log(Level.FINE, "</Compare Female Fertility Modelled and Expected>");
        result[0] = sumOfSquaredErrors;
        log(Level.FINE, "</compareToExpected>");
        return result;
        } catch (OutOfMemoryError e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    /**
     * Applies annual mortality rates to start year populations to create end
     * year population that have died and survived. Mid-year populations are
     * estimated for females and fertility rates applied to this to calculate
     * birth populations. GENESIS_Mortality rates are applied and the population
     * counts of living and dead aggregated accordingly.
     *
     * @param startYearPopulation
     * @param mortalityRates
     * @param fertility
     * @param handleOutOfMemoryError
     * @param decimalPlacePrecisionForCalculations
     * @return Object[] result where: result[0] is a GENESIS_Population of the
     * survived population; result[1] is a GENESIS_Population of the dead
     * population; result[2] is a TreeMap&lt;GENESIS_AgeBound,BigDecimal&gt;
     * live births by ageBound of mother; result[3] is a
     * TreeMap&lt;GENESIS_AgeBound,BigDecimal&gt; of twins by ageBound of
     * mother; result[4] is a TreeMap&lt;GENESIS_AgeBound,BigDecimal&gt; of
     * triplets by ageBound of mother;
     */
    public static Object[] getTheoreticalEndYearPopulationsAndFertility(
            GENESIS_Population population, //TreeMap<Integer, BigDecimal> femaleStartYearPopulation,
            GENESIS_Mortality mortality, //TreeMap<Integer, BigDecimal> femaleMortalityRates,
            GENESIS_Fertility fertility,
            int decimalPlacePrecisionForCalculations,
            boolean handleOutOfMemoryError) {
        try {
        String sourceMethod = "getTheoreticalEndYearPopulationsAndFertility";
        String msg;
        msg = "<" + sourceMethod + ">";
        log(Level.FINE, msg);
        //System.out.println(msg);
        Object[] result = new Object[5];
        // Calculate theoreticallyDeadAndAgedPopulation without any births
        Object[] theoreticallySurvivedAndDiedPopulation = getTheoreticallySurvivedAndDiedPopulation(
                population,
                mortality);
        /*
         * theoreticallySurvivedAndDiedPopulation[0] survived
         * theoreticallySurvivedAndDiedPopulation[1] died
         */
        // Calculate theoreticalFertilityAndNewBornPopulation
        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalEndYearAgedPopulation =
                ((GENESIS_Population) theoreticallySurvivedAndDiedPopulation[0])._FemaleAgeBoundPopulationCount_TreeMap;

        Object[] theoreticalFertilityAndNewBornPopulation = getTheoreticalFertilityAndNewBornPopulation(
                population._FemaleAgeBoundPopulationCount_TreeMap,
                femaleTheoreticalEndYearAgedPopulation,
                mortality,
                fertility,
                decimalPlacePrecisionForCalculations);
        /*
         * theoreticalFertilityAndNewBornPopulation[0] = birthsByAgeOfMother;
         * theoreticalFertilityAndNewBornPopulation[1] = twinsByAgeOfMother;
         * theoreticalFertilityAndNewBornPopulation[2] = tripletsByAgeOfMother;
         * theoreticalFertilityAndNewBornPopulation[3] =
         * totalSurvivingFemaleBirths;
         * theoreticalFertilityAndNewBornPopulation[4] =
         * totalSurvivingMaleBirths; theoreticalFertilityAndNewBornPopulation[5]
         * = deadFemaleBabies; theoreticalFertilityAndNewBornPopulation[6] =
         * deadMaleBabies;
         */
        result[2] = theoreticalFertilityAndNewBornPopulation[0];
        result[3] = theoreticalFertilityAndNewBornPopulation[1];
        result[4] = theoreticalFertilityAndNewBornPopulation[2];
        //result[5] = theoreticalFertilityAndNewBornPopulation[3];
        //result[6] = theoreticalFertilityAndNewBornPopulation[4];
        //result[7] = theoreticalFertilityAndNewBornPopulation[5];
        //result[8] = theoreticalFertilityAndNewBornPopulation[6];
        // Add those that survive to the living
        GENESIS_AgeBound ageBound0 = new GENESIS_AgeBound(0L);
        femaleTheoreticalEndYearAgedPopulation.put(
                ageBound0,
                (BigDecimal) theoreticalFertilityAndNewBornPopulation[3]);
        TreeMap<GENESIS_AgeBound, BigDecimal> maleTheoreticalEndYearAgedPopulation =
                ((GENESIS_Population) theoreticallySurvivedAndDiedPopulation[0])._MaleAgeBoundPopulationCount_TreeMap;
        maleTheoreticalEndYearAgedPopulation.put(
                ageBound0,
                (BigDecimal) theoreticalFertilityAndNewBornPopulation[4]);
        // Add those that died to the dead
        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                ((GENESIS_Population) theoreticallySurvivedAndDiedPopulation[1])._FemaleAgeBoundPopulationCount_TreeMap,
                ageBound0,
                (BigDecimal) theoreticalFertilityAndNewBornPopulation[5],
                handleOutOfMemoryError);
        GENESIS_Collections.addTo_TreeMap_AgeBound_BigDecimal(
                ((GENESIS_Population) theoreticallySurvivedAndDiedPopulation[1])._MaleAgeBoundPopulationCount_TreeMap,
                ageBound0,
                (BigDecimal) theoreticalFertilityAndNewBornPopulation[6],
                handleOutOfMemoryError);
        result[0] = theoreticallySurvivedAndDiedPopulation[0];
        result[1] = theoreticallySurvivedAndDiedPopulation[1];
        msg = "</" + sourceMethod + ">";
        log(Level.FINE, msg);
        //System.out.println(msg);
        return result;
        } catch (OutOfMemoryError e) {
            throw e;
        }
    }

    /**
     * @param femaleStartYearPopulation
     * @param femaleTheoreticalEndYearAgedPopulation
     * @param mortality
     * @param femaleFertilityRates
     * @param fertility
     * @param decimalPlacePrecisionForCalculations
     * @return an Object[] result where: result[0] is a
     * TreeMap&lt;GENESIS_AgeBound,BigDecimal&gt; live births by age of mother;
     * result[2] is a TreeMap&lt;GENESIS_AgeBound,BigDecimal&gt; of twins by age
     * of mother; result[3] is a TreeMap&lt;GENESIS_AgeBound,BigDecimal&gt; of
     * triplets by ageBound of mother; result[4] is a count of surviving female
     * babies result[5] is a count of surviving male babies result[6] is a count
     * of dead female babies result[7] is a count of dead male babies
     */
    public static Object[] getTheoreticalFertilityAndNewBornPopulation(
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleStartYearPopulation,
            TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalEndYearAgedPopulation,
            GENESIS_Mortality mortality,//TreeMap<Integer, BigDecimal> femaleMortalityRates,
            GENESIS_Fertility fertility,
            int decimalPlacePrecisionForCalculations) {//TreeMap<Integer, BigDecimal> femaleFertilityRates) {
        log(Level.FINE, "<getTheoreticalFertilityAndNewBornPopulation>");
        Object[] result = new Object[7];
//        TreeMap<GENESIS_AgeBound, BigDecimal> femaleFertilityRates =
//                fertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap;
        // Initialise general variables
        BigDecimal two_BigDecimal = new BigDecimal("2");
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        // Calculate and store the average of the femaleStartYearPopulation and 
        // femaleTheoreticalEndYearAgedPopulation. This is the theoretical 
        // midyear population without births and migration.
        log(Level.FINE, "<calculateMidYearPopulation>");
        TreeMap<GENESIS_AgeBound, BigDecimal> femaleTheoreticalMidYearPopulation =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        if (true) {
            HashSet<GENESIS_AgeBound> completeKeySet = GENESIS_Collections.getCombined_HashSet_AgeBound(
                    femaleStartYearPopulation.keySet(),
                    femaleTheoreticalEndYearAgedPopulation.keySet());
            Iterator<GENESIS_AgeBound> completeKeySetIterator = completeKeySet.iterator();
            GENESIS_AgeBound ageBound;
            BigDecimal startYearPopulation;
            BigDecimal endYearPopulation;
            BigDecimal midYearPopulation;
            Object value;
            while (completeKeySetIterator.hasNext()) {
                ageBound = completeKeySetIterator.next();
                value = femaleStartYearPopulation.get(ageBound);
                if (value == null) {
                    startYearPopulation = BigDecimal.ZERO;
                } else {
                    startYearPopulation = (BigDecimal) value;
                }
                value = femaleTheoreticalEndYearAgedPopulation.get(ageBound);
                if (value == null) {
                    endYearPopulation = BigDecimal.ZERO;
                } else {
                    endYearPopulation = (BigDecimal) value;
                }
                midYearPopulation = (startYearPopulation.add(endYearPopulation)).divide(
                        two_BigDecimal, decimalPlacePrecisionForCalculations, roundingMode);
                femaleTheoreticalMidYearPopulation.put(ageBound, midYearPopulation);
            }
        }
        log(Level.FINE, "</calculateMidYearPopulation>");
        // Calculate theoretical birth populations using female fertility 
        // probabilities and theoretical mid year female population
        TreeMap<GENESIS_AgeBound, BigDecimal> birthsByAgeOfMother =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        TreeMap<GENESIS_AgeBound, BigDecimal> twinsByAgeOfMother =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        TreeMap<GENESIS_AgeBound, BigDecimal> tripletsByAgeOfMother =
                new TreeMap<GENESIS_AgeBound, BigDecimal>();
        //BigDecimal totalBirths_BigDecimal = BigDecimal.ZERO;
        BigDecimal totalFemaleBirths_BigDecimal = BigDecimal.ZERO;
        BigDecimal totalMaleBirths_BigDecimal = BigDecimal.ZERO;
        BigDecimal totalSurvivingFemaleBirths = BigDecimal.ZERO;
        BigDecimal totalSurvivingMaleBirths = BigDecimal.ZERO;
        BigDecimal deadFemaleBabies = BigDecimal.ZERO;
        BigDecimal deadMaleBabies = BigDecimal.ZERO;
        if (true) {
            GENESIS_AgeBound ageBound0 = new GENESIS_AgeBound(0L);
            BigDecimal babyFemaleSurvivalRate = BigDecimal.ONE.subtract(mortality._FemaleAnnualMortalityAgeBoundRate_TreeMap.get(ageBound0));
            BigDecimal babyMaleSurvivalRate = BigDecimal.ONE.subtract(mortality._MaleAnnualMortalityAgeBoundRate_TreeMap.get(ageBound0));
            Iterator<GENESIS_AgeBound> ite = femaleTheoreticalMidYearPopulation.keySet().iterator();
            GENESIS_AgeBound ageBound;
            BigDecimal potentialMotherPopulation_BigDecimal;
            BigDecimal births_BigDecimal;
            BigDecimal femaleBirths_BigDecimal;
            BigDecimal maleBirths_BigDecimal;
            BigDecimal survivingFemaleBirths_BigDecimal;
            BigDecimal survivingMaleBirths_BigDecimal;
            BigDecimal fertility_BigDecimal;
//            BigDecimal maleBirths_BigDecimal;
//            BigDecimal femaleBirths_BigDecimal;
            while (ite.hasNext()) {
                ageBound = ite.next();
                potentialMotherPopulation_BigDecimal = femaleTheoreticalMidYearPopulation.get(ageBound);
                if (potentialMotherPopulation_BigDecimal != null) {
                    if (potentialMotherPopulation_BigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                        //fertility_BigDecimal = femaleFertilityRates.get(ageBound);
                        fertility_BigDecimal = fertility.getAnnualLiveBirthFertility(ageBound);
                        if (fertility_BigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                            births_BigDecimal = potentialMotherPopulation_BigDecimal.multiply(fertility_BigDecimal);
                            birthsByAgeOfMother.put(ageBound, births_BigDecimal);
                            //totalBirths_BigDecimal = totalBirths_BigDecimal.add(births_BigDecimal);
                            BigDecimal twinPopulation = births_BigDecimal.multiply(
                                    fertility.getTwinProbability(ageBound));
                            twinsByAgeOfMother.put(ageBound, twinPopulation);
                            BigDecimal tripletPopulation = births_BigDecimal.multiply(
                                    fertility.getTripletProbability(ageBound));
                            tripletsByAgeOfMother.put(ageBound, tripletPopulation);
                            // Assume a 50% boy birth
                            femaleBirths_BigDecimal = births_BigDecimal.divide(two_BigDecimal);
                            maleBirths_BigDecimal = births_BigDecimal.divide(two_BigDecimal);
                            totalFemaleBirths_BigDecimal = totalFemaleBirths_BigDecimal.add(femaleBirths_BigDecimal);
                            totalMaleBirths_BigDecimal = totalMaleBirths_BigDecimal.add(maleBirths_BigDecimal);
                            survivingFemaleBirths_BigDecimal = femaleBirths_BigDecimal.multiply(babyFemaleSurvivalRate);
                            survivingMaleBirths_BigDecimal = maleBirths_BigDecimal.multiply(babyMaleSurvivalRate);
                            totalSurvivingFemaleBirths = totalSurvivingFemaleBirths.add(
                                    femaleBirths_BigDecimal);
                            totalSurvivingMaleBirths = totalSurvivingMaleBirths.add(
                                    maleBirths_BigDecimal);
                            deadFemaleBabies = deadFemaleBabies.add(
                                    femaleBirths_BigDecimal.subtract(survivingFemaleBirths_BigDecimal));
                            deadMaleBabies = deadMaleBabies.add(
                                    maleBirths_BigDecimal.subtract(survivingMaleBirths_BigDecimal));
                        }
                    }
                }
            }
        }
        result[0] = birthsByAgeOfMother;
        result[1] = twinsByAgeOfMother;
        result[2] = tripletsByAgeOfMother;
        result[3] = totalSurvivingFemaleBirths;
        result[4] = totalSurvivingMaleBirths;
        result[5] = deadFemaleBabies;
        result[6] = deadMaleBabies;
        log(Level.FINE, "</getTheoreticalFertilityAndNewBornPopulation>");
        return result;
    }

    /**
     * Applies annual mortality rates to start year populations to created
     * map0Value end year population that have died and survived. This does not
     * currently deal with fertility adding in new people ageBound 0. The
     * survived population returned are effectively aged by 1 year.
     *
     * @param population
     * @param startYearPopulation
     * @param mortality
     * @return
     */
    public static Object[] getTheoreticallySurvivedAndDiedPopulation(
            GENESIS_Population population,
            GENESIS_Mortality mortality) {
        String sourceMethod = "getTheoreticallySurvivedAndDiedPopulation("
                + "GENESIS_Population,GENESIS_Mortality)";
        String msg = "<" + sourceMethod + ">";
        log(Level.FINE, msg);
        //System.out.println(msg);
        Object[] result = new Object[2];
        GENESIS_Population died = new GENESIS_Population();
        GENESIS_Population survived = new GENESIS_Population();
        GENESIS_AgeBound ageBound;
        BigDecimal population_BigDecimal;
        BigDecimal mortality_BigDecimal;
        BigDecimal populationExpectedToDie_BigDecimal;
        BigDecimal populationExpectedToSurvive_BigDecimal;
        Iterator<GENESIS_AgeBound> ite;
        // Females
        msg = "Female";
        log(Level.FINE, msg);
        System.out.println(msg);
        msg = "ageMinYear, ageMaxYear, population, mortalityRate, "
                + "populationExpectedToDie, "
                + "populationExpectedToSurvive";
        log(Level.FINE, msg);
        System.out.println(msg);
        ite = population._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            ageBound = ite.next();
            population_BigDecimal = population._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (population_BigDecimal != null) {
                if (population_BigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                    mortality_BigDecimal = mortality.getAnnualMortalityFemale(ageBound);
                    if (mortality_BigDecimal == null) {
                        mortality_BigDecimal = BigDecimal.ZERO;
                    }
                    BigDecimal survivalProbability = BigDecimal.ONE.subtract(mortality_BigDecimal);
                    populationExpectedToSurvive_BigDecimal = Generic_BigDecimal.multiplyRoundIfNecessary(
                            survivalProbability,
                            population_BigDecimal,
                            mortality_BigDecimal.scale(),
                            RoundingMode.UP);
                    populationExpectedToDie_BigDecimal = population_BigDecimal.subtract(
                            populationExpectedToSurvive_BigDecimal);
                    msg = ageBound.getAgeMin().getYear() + ", "
                            + ageBound.getAgeMax().getYear() + ", "
                            + population_BigDecimal + ", "
                            + mortality_BigDecimal + ", "
                            + populationExpectedToDie_BigDecimal + ", "
                            + populationExpectedToSurvive_BigDecimal;
                    log(Level.FINE, msg);
                    System.out.println(msg);
                    // @TODO Add half to ageBound and half to ageBound + 1
                    died._FemaleAgeBoundPopulationCount_TreeMap.put(
                            ageBound, populationExpectedToDie_BigDecimal);
                    GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(
                            ageBound.getAgeMin().getYear() + 1);
                    survived._FemaleAgeBoundPopulationCount_TreeMap.put(
                            newAgeBound, populationExpectedToSurvive_BigDecimal);
                }
            }
        }
        // Males
        msg = "Male";
        log(Level.FINE, msg);
        System.out.println(msg);
        msg = "ageMinYear, ageMaxYear, population, mortalityRate, "
                + "populationExpectedToDie, "
                + "populationExpectedToSurvive";
        log(Level.FINE, msg);
        //System.out.println(msg);
        ite = population._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            ageBound = ite.next();
            population_BigDecimal = population._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (population_BigDecimal != null) {
                if (population_BigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                    mortality_BigDecimal = mortality.getAnnualMortalityMale(ageBound);
                    if (mortality_BigDecimal == null) {
                        mortality_BigDecimal = BigDecimal.ZERO;
                    }
                    BigDecimal survivalProbability = BigDecimal.ONE.subtract(mortality_BigDecimal);
                    populationExpectedToSurvive_BigDecimal = Generic_BigDecimal.multiplyRoundIfNecessary(
                            survivalProbability,
                            population_BigDecimal,
                            mortality_BigDecimal.scale(),
                            RoundingMode.UP);
                    populationExpectedToDie_BigDecimal = population_BigDecimal.subtract(
                            populationExpectedToSurvive_BigDecimal);
                    msg = ageBound.getAgeMin().getYear() + ", "
                            + ageBound.getAgeMax().getYear() + ", "
                            + population_BigDecimal + ", "
                            + mortality_BigDecimal + ", "
                            + populationExpectedToDie_BigDecimal + ", "
                            + populationExpectedToSurvive_BigDecimal;
                    log(Level.FINE, msg);
                    System.out.println(msg);
                    // @TODO Add half to ageBound and half to ageBound + 1
                    died._MaleAgeBoundPopulationCount_TreeMap.put(
                            ageBound, populationExpectedToDie_BigDecimal);
                    GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(
                            ageBound.getAgeMin().getYear() + 1);
                    survived._MaleAgeBoundPopulationCount_TreeMap.put(
                            newAgeBound, populationExpectedToSurvive_BigDecimal);
                }
            }
        }
        died.updateGenderedAgePopulation();
        survived.updateGenderedAgePopulation();
        result[0] = survived;
        result[1] = died;
        msg = "</" + sourceMethod + ">";
        log(Level.FINE, msg);
        //System.out.println(msg);
        return result;
    }

    /**
     * Returns a new but with identical mapping map to map except with all
     * values greater than one set to one.
     *
     * @param map
     * @return 
     */
    public static TreeMap<GENESIS_AgeBound, BigDecimal> getTreeMapWithExtremeProbabilitiesSetToOne(
            TreeMap<GENESIS_AgeBound, BigDecimal> map) {
        TreeMap<GENESIS_AgeBound, BigDecimal> result = new TreeMap<GENESIS_AgeBound, BigDecimal>();
        Iterator<GENESIS_AgeBound> ite = map.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            BigDecimal bi = map.get(ageBound);
            if (bi.compareTo(BigDecimal.ONE) == 1) {
                result.put(ageBound, BigDecimal.ONE);
            } else {
                result.put(ageBound, new BigDecimal(bi.toString()));
            }
        }
        return result;




    }

//    public static BigDecimal getRate(
//            GENESIS_AgeBound ageBound,
//            TreeMap<GENESIS_AgeBound, BigDecimal> m) {
//        BigDecimal result = m.get(ageBound);
//        if (result == null) {
//            int ageBoundMin_int = ageBound.getAgeMin().intValue();
//            if (ageBoundMin_int > m.firstKey().getAgeMin().intValue()) {
//                GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(ageBoundMin_int - 1);
//                result = getRate(newAgeBound, m);
//            } else {
//                GENESIS_AgeBound newAgeBound = new GENESIS_AgeBound(ageBoundMin_int + 1);
//                result = getRate(newAgeBound, m);
//            }
//        }
//        return result;
//    }
//    public Object[] getMidYearPopulation(
//            TreeMap<Integer, BigInteger> startYearPopulation,
//            TreeMap<Integer, BigInteger> endYearPopulation) {
//    }
//    publci Object[]
//
//    getBirths(GENESIS_Demographics input_Demographics) {
    /**
     * This method loads Local Authority District (LAD) populations that
     * comprise of Output Area (OA) populations.
     *
     * @param a_GENESIS_Environment
     * @param directory
     * @return TreeMap<String,TreeMap<String, GENESIS_Population>> result where:
     * result keys are LAD codes, result values are maps with keys as OA codes
     * and values being the GENESIS_Population representing the OA population.
     * Additionally a total population for each LAD is mapped with the key being
     * the LAD code.
     */
    public static TreeMap<String, TreeMap<String, GENESIS_Population>> loadInputPopulation(
            GENESIS_Environment a_GENESIS_Environment,
            File directory) {
        //String methodName = "loadInputPopulations(_GENESIS_Environment,File)";
        // Initialise result
        TreeMap<String, TreeMap<String, GENESIS_Population>> result;
        result = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        // Initialise totalPop
        GENESIS_Population totalPop = new GENESIS_Population(a_GENESIS_Environment);
        // get list of input files and process each of these in turn
        File[] filelist = directory.listFiles();
        for (int fileIndex = 0; fileIndex < filelist.length; fileIndex++) {
            File aLADDir = filelist[fileIndex];
            System.out.println("Load Population for " + aLADDir);
            TreeMap<String, GENESIS_Population> regionPopulationMap;
            regionPopulationMap = new TreeMap<String, GENESIS_Population>();
            String regionID = aLADDir.getName();
            result.put(regionID, regionPopulationMap);
            // Initialise aLADTotalPop
            GENESIS_Population regionTotalPop = new GENESIS_Population(a_GENESIS_Environment);
            long highestLeaf = Generic_StaticIO.getArchiveHighestLeaf(
                    aLADDir, "_");
            long range = Generic_StaticIO.getArchiveRange(
                    aLADDir, "_");
            String outputAreaCode;
            GENESIS_Population pop;
            for (long i = highestLeaf; i > -1; i--) {
                File dir = Generic_StaticIO.getObjectDirectory(
                        aLADDir,
                        i,
                        highestLeaf,
                        range);
                dir = new File(
                        dir,
                        "" + i);
                System.out.println("" + dir);
                File f = dir.listFiles()[0];
                outputAreaCode = f.getName().substring(0, 10);
                PopulationType a_PopulationType = XMLConverter.loadPopulationFromXMLFile(f);
                pop = new GENESIS_Population(
                        a_GENESIS_Environment,
                        a_PopulationType);
                pop.ge = a_GENESIS_Environment;
                regionPopulationMap.put(outputAreaCode, pop);
                regionTotalPop.addPopulation(pop);
            }
            regionTotalPop.updateGenderedAgePopulation();
            regionPopulationMap.put(regionID, regionTotalPop);
            totalPop.addPopulation(regionTotalPop);
        }
        totalPop.updateGenderedAgePopulation();
        TreeMap<String, GENESIS_Population> totalPopulationMap;
        totalPopulationMap = new TreeMap<String, GENESIS_Population>();
        totalPopulationMap.put("Total", totalPop);
        result.put("Total", totalPopulationMap);
        return result;
    }

    /**
     * This method loads Local Authority District (LAD) death count data. The
     * result keys are LAD codes and the values are GENESIS_Populations
     *
     * @param a_GENESIS_Environment
     * @param year
     * @param directory
     * @param _regionIDs
     * @return TreeMap<String, GENESIS_Population> result where: result keys are
     * LAD codes, result values are GENESIS_Population representing the OA
     * population of deaths.
     */
    public static TreeMap<String, GENESIS_Population> loadInputDeathCount(
            GENESIS_Environment a_GENESIS_Environment,
            int year,
            File directory,
            TreeMap<String, TreeSet<String>> _regionIDs) {
        String methodName = "loadInputDeathCount(GENESIS_Environment,File)";
        String firstPartOfFilename = "DeathCount";
        TreeMap<String, GENESIS_Population> result;
        result = loadPop(
                a_GENESIS_Environment,
                year,
                directory,
                firstPartOfFilename,
                _regionIDs);
        return result;
    }

    /**
     * This method loads Local Authority District (LAD) birth count data. The
     * result keys are LAD codes and the values are GENESIS_Populations
     *
     * @param a_GENESIS_Environment
     * @param year
     * @param directory
     * @param _regionIDs
     * @return TreeMap<String, GENESIS_Population> result where: result keys are
     * LAD codes, result values are GENESIS_Population representing the OA
     * population of births.
     */
    public static TreeMap<String, GENESIS_Population> loadInputBirthCount(
            GENESIS_Environment a_GENESIS_Environment,
            int year,
            File directory,
            TreeMap<String, TreeSet<String>> _regionIDs) {
        String methodName = "loadInputBirthCount(GENESIS_Environment,File)";
        String firstPartOfFilename = "BirthCount";
        TreeMap<String, GENESIS_Population> result;
        result = loadPop(
                a_GENESIS_Environment,
                year,
                directory,
                firstPartOfFilename,
                _regionIDs);
        return result;
    }

    private static TreeMap<String, GENESIS_Population> loadPop(
            GENESIS_Environment a_GENESIS_Environment,
            int year,
            File directory,
            String firstPartOfFilename,
            TreeMap<String, TreeSet<String>> _regionIDs) {
        // Initialise result
        TreeMap<String, GENESIS_Population> result;
        result = new TreeMap<String, GENESIS_Population>();
        Set<String> regionIDSet = _regionIDs.keySet();
        // Initialise totalPop
        //GENESIS_Population totalPop = new GENESIS_Population(a_GENESIS_Environment);
        // get list of input files and process each of these in turn
        File[] filelist = directory.listFiles();
        for (int fileIndex = 0; fileIndex < filelist.length; fileIndex++) {
            File aLADDir = filelist[fileIndex];
            String aLADCode = aLADDir.getName();
            if (regionIDSet.contains(aLADCode)) {
                File aLADYearDir = new File(aLADDir,
                        "" + year);
                File f = new File(
                        aLADYearDir,
                        firstPartOfFilename + "_" + aLADCode + "_" + year + ".xml");
                PopulationType a_PopulationType = XMLConverter.loadPopulationFromXMLFile(f);
                GENESIS_Population pop = new GENESIS_Population(
                        a_GENESIS_Environment,
                        a_PopulationType);
                result.put(aLADCode, pop);
            }
        }
        return result;
    }

    public static TreeMap<String, GENESIS_Mortality> getInitialMortalityRate(
            GENESIS_Environment a_GENESIS_Environment,
            TreeMap<String, TreeMap<String, GENESIS_Population>> singleYearOfAgePopulation,
            TreeMap<String, GENESIS_Population> inputDeathCount,
            int decimalPlaces,
            RoundingMode roundingMode) {
        TreeMap<String, GENESIS_Mortality> result;
        result = new TreeMap<String, GENESIS_Mortality>();
        Iterator<String> ite = inputDeathCount.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();

            // Debug
            System.out.println("regionID " + regionID);

            GENESIS_Population population = singleYearOfAgePopulation.get(regionID).get(regionID);
            GENESIS_Population deathCount = inputDeathCount.get(regionID);
            GENESIS_Mortality aLADMortality = new GENESIS_Mortality(a_GENESIS_Environment);
            result.put(regionID, aLADMortality);
            Iterator<GENESIS_AgeBound> ite2;
            // Female
            ite2 = deathCount._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
            while (ite2.hasNext()) {
                GENESIS_AgeBound ageBound = ite2.next();
                BigDecimal deathPop = deathCount._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                long minYear = ageBound.getAgeMin().getYear();
                long maxYear = ageBound.getAgeMax().getYear();
                BigDecimal pop = population.getFemalePopulationSum(minYear, maxYear);
                BigDecimal rate;
                if (deathPop.compareTo(BigDecimal.ZERO) == 0) {
                    rate = BigDecimal.ZERO;
                } else {
                    if (pop.compareTo(BigDecimal.ZERO) == 0) {
                        rate = BigDecimal.ONE;
                    } else {
                        rate = Generic_BigDecimal.divideRoundIfNecessary(
                                deathPop,
                                pop,
                                decimalPlaces,
                                roundingMode);
                    }
                }
                for (long age = minYear; age < maxYear; age++) {
                    GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(age);
                    aLADMortality._FemaleAnnualMortalityAgeBoundRate_TreeMap.put(
                            yearAgeBound,
                            rate);
                }
            }
            // Male
            ite2 = deathCount._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
            while (ite2.hasNext()) {
                GENESIS_AgeBound ageBound = ite2.next();
                BigDecimal deathPop = deathCount._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                long minYear = ageBound.getAgeMin().getYear();
                long maxYear = ageBound.getAgeMax().getYear();
                BigDecimal pop = population.getMalePopulationSum(minYear, maxYear);
                BigDecimal rate;
                if (deathPop.compareTo(BigDecimal.ZERO) == 0) {
                    rate = BigDecimal.ZERO;
                } else {
                    if (pop.compareTo(BigDecimal.ZERO) == 0) {
                        rate = BigDecimal.ONE;
                    } else {
                        rate = Generic_BigDecimal.divideRoundIfNecessary(
                                deathPop,
                                pop,
                                decimalPlaces,
                                roundingMode);
                    }
                }
                for (long age = minYear; age < maxYear; age++) {
                    GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(age);
                    aLADMortality._MaleAnnualMortalityAgeBoundRate_TreeMap.put(
                            yearAgeBound,
                            rate);
                }
            }
        }
        return result;
    }

    public static TreeMap<String, GENESIS_Fertility> getInitialFertilityRate(
            GENESIS_Environment a_GENESIS_Environment,
            TreeMap<String, TreeMap<String, GENESIS_Population>> singleYearOfAgePopulation,
            TreeMap<String, GENESIS_Population> inputBirthCount,
            int decimalPlaces,
            RoundingMode roundingMode) {
        TreeMap<String, GENESIS_Fertility> result;
        result = new TreeMap<String, GENESIS_Fertility>();
        Iterator<String> ite;
        ite = inputBirthCount.keySet().iterator();
        while (ite.hasNext()) {
            String aLADCode = ite.next();
            GENESIS_Population population = singleYearOfAgePopulation.get(aLADCode).get(aLADCode);
            GENESIS_Population birthCount = inputBirthCount.get(aLADCode);
            GENESIS_Fertility aLADFertility = new GENESIS_Fertility(a_GENESIS_Environment);
            aLADFertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
            result.put(aLADCode, aLADFertility);
            Iterator<GENESIS_AgeBound> ite2;
            // Female
            ite2 = birthCount._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
            while (ite2.hasNext()) {
                GENESIS_AgeBound ageBound = ite2.next();
                BigDecimal deathPop = birthCount._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                long minYear = ageBound.getAgeMin().getYear();
                long maxYear = ageBound.getAgeMax().getYear();
                BigDecimal pop = population.getFemalePopulationSum(minYear, maxYear);
                BigDecimal rate;
                if (deathPop.compareTo(BigDecimal.ZERO) == 0) {
                    rate = BigDecimal.ZERO;
                } else {
                    if (pop.compareTo(BigDecimal.ZERO) == 0) {
                        rate = BigDecimal.ONE;
                    } else {
                        rate = Generic_BigDecimal.divideRoundIfNecessary(
                                deathPop,
                                pop,
                                decimalPlaces,
                                roundingMode);
                    }
                }
                for (long age = minYear; age < maxYear; age++) {
                    GENESIS_AgeBound yearAgeBound = new GENESIS_AgeBound(age);
                    aLADFertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap.put(
                            yearAgeBound,
                            rate);
                }
            }
        }
        // Initialise twin and triplet probabilities
        ite = result.keySet().iterator();
        while (ite.hasNext()) {
            String aLADCode = ite.next();
            GENESIS_Fertility aLADFertility = result.get(aLADCode);
            aLADFertility._TwinPregnancyAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
            aLADFertility._TripletPregnancyAgeBoundProbability_TreeMap = new TreeMap<GENESIS_AgeBound, BigDecimal>();
            Iterator<GENESIS_AgeBound> ite2 = aLADFertility._AnnualLiveBirthFertilityAgeBoundRate_TreeMap.keySet().iterator();
            while (ite2.hasNext()) {
                GENESIS_AgeBound ageBound = ite2.next();
                aLADFertility._TwinPregnancyAgeBoundProbability_TreeMap.put(ageBound, GENESIS_Fertility.DefaultTwinPregnancyProbability);
                aLADFertility._TripletPregnancyAgeBoundProbability_TreeMap.put(ageBound, GENESIS_Fertility.DefaultTripletPregnancyProbability);
            }
        }
        return result;
    }

    public class Gender_Age implements Serializable {

        public int _Gender;
        public int _AgeInYears_int;

        Gender_Age(int _Gender, int _AgeInYears_int) {
            this._Gender = _Gender;
            this._AgeInYears_int = _AgeInYears_int;
        }

        @Override
        public boolean equals(Object _Object) {
            if (_Object instanceof Gender_Age) {
                Gender_Age _Object_Gender_Age = (Gender_Age) _Object;
                return _Object_Gender_Age._Gender == this._Gender
                        && _Object_Gender_Age._AgeInYears_int == this._AgeInYears_int;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + this._Gender;
            hash = 97 * hash + this._AgeInYears_int;
            return hash;
        }
    }

    private static void log(Level level, String message) {
        getLogger().log(level, message);
    }

    public static Logger getLogger() {
        return GENESIS_Log.logger;
    }
}
