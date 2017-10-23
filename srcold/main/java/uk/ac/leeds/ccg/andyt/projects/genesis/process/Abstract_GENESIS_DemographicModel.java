package uk.ac.leeds.ccg.andyt.projects.genesis.process;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_PersonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.parameters.ParametersType;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Demographics;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Fertility;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Migration;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Mortality;

/**
 * For modelling demographics with a regional and subregional partitioning.
 */
public abstract class Abstract_GENESIS_DemographicModel
        extends Abstract_GENESIS_Model
        implements Serializable {

    public File _SeedDirectory_File;
    public File _InitialPopulation_File;
    public File _InitialDeathCount_File;
    public File _InitialMortalityRate_File;
    public File _InitialMiscarriageRate_File;
    public File _InitialBirthCount_File;
    public File _InitialFertilityRate_File;
    public Long _RunID;
    protected int _Years;
    /*
     * For storing region IDs and subregion IDs
     */
    public TreeMap<String, TreeSet<String>> _regionIDs;
    /*
     * For storing living female IDs:
     * keys are subregion IDs;
     * values are TreeSets of GENESIS_Female._ID
     */
    protected TreeMap<String, TreeMap<String, TreeSet<Long>>> _LivingFemaleIDs;
    /*
     * For storing living male IDs:
     * keys are subregion IDs;
     * values are TreeSets of GENESIS_Male._ID
     */
    protected TreeMap<String, TreeMap<String, TreeSet<Long>>> _LivingMaleIDs;
    /*
     * For storing not pregnant female IDs:
     * keys are subregion IDs;
     * values are TreeSets of GENESIS_Female._ID
     */
    protected TreeMap<String, TreeMap<String, TreeSet<Long>>> _NotPregnantFemaleIDs;
    /*
     * For storing pregnant female IDs:
     * keys are subregion IDs;
     * values are TreeSets of GENESIS_Female._ID
     */
    protected TreeMap<String, TreeMap<String, TreeSet<Long>>> _PregnantFemaleIDs;
    /*
     * For storing nearly due pregnant female IDs:
     * keys are subregion IDs;
     * values are TreeSets of GENESIS_Female._ID
     */
    protected TreeMap<String, TreeMap<String, TreeSet<Long>>> _NearlyDuePregnantFemaleIDs;
    /**
     * _initial_Demographics is for storing the initial attributes of the
     * population simulated.
     */
    public GENESIS_Demographics _initial_Demographics;
    /**
     * _Demographics is for storing the current attributes of the population
     * simulated.
     */
    public GENESIS_Demographics _Demographics;

    public int get_Years() {
        return _Years;
    }

    @Override
    public void init_Environment(GENESIS_Environment a_GENESIS_Environment) {
        super.init_Environment(a_GENESIS_Environment);
        ge._PersonFactory = new GENESIS_PersonFactory(
                ge,
                _GENESIS_AgentCollectionManager);
        get_Demographics()._GENESIS_Environment = a_GENESIS_Environment;
    }

    public GENESIS_Fertility get_Fertility(
            String regionID,
            String subregionID) {
        GENESIS_Fertility result;
        if (get_Demographics()._Fertility == null) {
            _Demographics._Fertility = new TreeMap<String, TreeMap<String, GENESIS_Fertility>>();
        }
        TreeMap<String, GENESIS_Fertility> subregionFertilityMap;
        subregionFertilityMap = _Demographics._Fertility.get(regionID);
        if (subregionFertilityMap == null) {
            subregionFertilityMap = new TreeMap<String, GENESIS_Fertility>();
            _Demographics._Fertility.put(regionID, subregionFertilityMap);
        }
        result = subregionFertilityMap.get(subregionID);
        if (result == null) {
            result = new GENESIS_Fertility(ge);
            subregionFertilityMap.put(subregionID, result);
        }
        return result;
    }

    public GENESIS_Mortality get_Mortality(
            String regionID,
            String subregionID) {
        GENESIS_Mortality result;
        if (get_Demographics()._Mortality == null) {
            _Demographics._Mortality = new TreeMap<String, TreeMap<String, GENESIS_Mortality>>();
        }
        TreeMap<String, GENESIS_Mortality> subregionMortalityMap;
        subregionMortalityMap = _Demographics._Mortality.get(regionID);
        if (subregionMortalityMap == null) {
            subregionMortalityMap = new TreeMap<String, GENESIS_Mortality>();
            _Demographics._Mortality.put(regionID, subregionMortalityMap);
        }
        result = subregionMortalityMap.get(subregionID);
        if (result == null) {
            result = new GENESIS_Mortality(ge);
            subregionMortalityMap.put(subregionID, result);
        }
        return result;
    }

    public GENESIS_Migration get_Migration() {
        GENESIS_Migration result = get_Demographics()._Migration;
        if (result == null) {
            result = new GENESIS_Migration(ge);
        }
        return result;
    }

    public GENESIS_Demographics get_Demographics() {
        if (_Demographics == null) {
            _Demographics = new GENESIS_Demographics(ge);
        }
        return _Demographics;
    }

    public File initialisePopulation_File(
            ParametersType parameters,
            File demographicDataDirectory) {
        String method = "initialisePopulation_File(ParametersType, String)";
        File result;
        File directory = new File(
                demographicDataDirectory,
                "Population");
        //String filename = parameters.getPopulationFile();
        String filename = "2001UKCensusData";
        //String filename = "2001UKCensusDataTest";
        result = getFileThatExists(directory, filename, method);
        return result;
    }

    public File initialiseDeathCount_File(
            ParametersType parameters,
            File demographicDataDirectory) {
        String method = "initialiseDeathCount_File(ParametersType, String)";
        File result;
        File directory = new File(
                demographicDataDirectory,
                "DeathCount");
        //result = getFileThatExists(directory, filename, method);
        result = directory;
        return result;
    }

    public File initialiseMortalityRate_File(
            ParametersType parameters,
            File demographicDataDirectory) {
        String method = "initialiseMortalityRate_File(ParametersType, String)";
        File result;
        String filename = parameters.getMortalityProbabilityFile();
        File directory = new File(
                demographicDataDirectory,
                "MortalityRate");
        result = getFileThatExists(directory, filename, method);
        return result;
    }

    public File initialiseMiscarriageRate_File(
            ParametersType parameters,
            File demographicDataDirectory) {
        String method = "initialiseMiscarriageRate_File(ParametersType, String)";
        File result;
        File directory = new File(
                demographicDataDirectory,
                "MiscarriageRate");
        String filename = parameters.getMiscarriageProbabilityFile();
        result = getFileThatExists(directory, filename, method);
        //result = directory;
        return result;
    }

    public File initialiseBirthCount_File(
            ParametersType parameters,
            File demographicDataDirectory) {
        String method = "initialiseBirthCount_File(ParametersType, String)";
        File result;
        File directory = new File(
                demographicDataDirectory,
                "BirthCount");
        //result = getFileThatExists(directory, filename, method);
        result = directory;
        return result;
    }

    public File initialiseFertilityRate_File(
            ParametersType parameters,
            File demographicDataDirectory) {
        String method = "initialiseFertilityRate_File(ParametersType, String)";
        File result;
        String filename = parameters.getFertilityProbabilityFile();
        File directory = new File(
                demographicDataDirectory,
                "FertilityRate");
        result = getFileThatExists(directory, filename, method);
        return result;
    }
}
