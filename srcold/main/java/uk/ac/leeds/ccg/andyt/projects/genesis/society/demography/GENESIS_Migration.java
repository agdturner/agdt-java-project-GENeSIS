/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_ErrorAndExceptionHandler;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Female;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Male;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Object;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.CommonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.Time;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Collections;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;

/**
 * A class for handling migration in simulation model. Migration is done as a
 * staged process. Firstly the out migration probabilities of an individual of a
 * specific age and gender are considered. If the simulation considers that
 * someone will migrate then the district to which they migrate including
 * emigration is decided. With the district decided, another decision is made to
 * reside them within a sub area of the district. Immigration is handled
 * separately. For people immigrating into the model it is decided about how
 * many of each type of person there are immigrating on a time step. Again, in a
 * staged way, first these are assigned a Local Authority District region, then
 * they are assigned a sub area within the district. Return migration is
 * currently not done. Anyone migrating from the model is effectively lost to
 * the world. Anyone immigrating is newly created and their birth date and
 * pregnancy status are initialised.
 */
public class GENESIS_Migration extends GENESIS_Object implements Serializable {

    //static final long serialVersionUID = 1L;
    public static final String RestOfUK_String = "ROU";
    public static final String RestOfTheWorld_String = "ROW";
    /**
     * For storing the population migrating from each region to each region. The
     * keys are destination region regionIDs, the value keys are origin region
     * regionIDs.
     */
    protected TreeMap<String, TreeMap<String, GENESIS_Population>> regionToRegionMigration;

    public TreeMap<String, TreeMap<String, GENESIS_Population>> getRegionToRegionMigration(boolean handleOutOfMemoryError) {
        try {
            if (regionToRegionMigration == null) {
                File regionToRegionMigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "regionToRegionMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
                if (!regionToRegionMigrationFile.exists()) {
                    System.err.println(
                            "regionToRegionMigrationFile " + regionToRegionMigrationFile
                            + " does not exist in GENESIS_Migration.getRegionToRegionMigration(boolean)");
                }
                regionToRegionMigration = (TreeMap<String, TreeMap<String, GENESIS_Population>>) Generic_StaticIO.readObject(regionToRegionMigrationFile);
            }
            return regionToRegionMigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getRegionToRegionMigration(boolean)");
                return getRegionToRegionMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the population migrating into each region in the study
     * region. The keys are regionIDs, the values are population counts. The
     * count include internal migration as stored in
     * <code>regionInternalMigration</code>
     */
    protected TreeMap<String, GENESIS_Population> regionInMigration;

    public TreeMap<String, GENESIS_Population> getRegionInMigration(boolean handleOutOfMemoryError) {
        try {
            if (regionInMigration == null) {
                File regionInMigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "regionInMigration_TreeMapStringGENESIS_Population.thisFile");
                if (!regionInMigrationFile.exists()) {
                    System.err.println(
                            "regionInMigrationFile " + regionInMigrationFile
                            + " does not exist in GENESIS_Migration.getRegionInMigration(boolean)");
                }
                regionInMigration = (TreeMap<String, GENESIS_Population>) Generic_StaticIO.readObject(regionInMigrationFile);
            }
            return regionInMigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getRegionInMigration(boolean)");
                return getRegionInMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the population migrating out from each region in the study
     * region. Currently, there is no data for emigration. The keys are
     * regionIDs, the values are population counts.
     */
    protected TreeMap<String, GENESIS_Population> regionOutMigration;

    public TreeMap<String, GENESIS_Population> getRegionOutMigration(boolean handleOutOfMemoryError) {
        try {
            if (regionOutMigration == null) {
                File regionOutMigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "regionOutMigration_TreeMapStringGENESIS_Population.thisFile");
                if (!regionOutMigrationFile.exists()) {
                    System.err.println(
                            "regionOutMigrationFile " + regionOutMigrationFile
                            + " does not exist in GENESIS_Migration.getRegionOutMigration(boolean)");
                }
                regionOutMigration = (TreeMap<String, GENESIS_Population>) Generic_StaticIO.readObject(regionOutMigrationFile);
            }
            return regionOutMigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getRegionOutMigration(boolean)");
                return getRegionOutMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the population internally migrating into each region in the
     * study region. The keys are regionIDs, the values are population counts.
     */
    protected TreeMap<String, GENESIS_Population> regionInternalMigration;

    public TreeMap<String, GENESIS_Population> getRegionInternalMigration(boolean handleOutOfMemoryError) {
        try {
            if (regionInternalMigration == null) {
                File regionInternalMigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "regionInternalMigration_TreeMapStringGENESIS_Population.thisFile");
                if (!regionInternalMigrationFile.exists()) {
                    System.err.println(
                            "regionInternalMigrationFile " + regionInternalMigrationFile
                            + " does not exist in GENESIS_Migration.getRegionInternalMigration(boolean)");
                }
                regionInMigration = (TreeMap<String, GENESIS_Population>) Generic_StaticIO.readObject(regionInternalMigrationFile);
            }
            return regionInternalMigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getRegionInternalMigration(boolean)");
                return getRegionInternalMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the population immigrating into each region in the study
     * region. The keys are regionIDs, the values are population counts.
     */
    protected TreeMap<String, GENESIS_Population> regionImmigration;

    public TreeMap<String, GENESIS_Population> getRegionImmigration(boolean handleOutOfMemoryError) {
        try {
            if (regionImmigration == null) {
                File regionImmigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "regionImmigration_TreeMapStringGENESIS_Population.thisFile");
                if (!regionImmigrationFile.exists()) {
                    System.err.println(
                            "regionImmigrationFile " + regionImmigrationFile
                            + " does not exist in GENESIS_Migration.getRegionImmigration(boolean)");
                }
                regionImmigration = (TreeMap<String, GENESIS_Population>) Generic_StaticIO.readObject(regionImmigrationFile);
            }
            return regionImmigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getRegionImmigration(boolean)");
                return getRegionInternalMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the population migrating into each subregion in a region. The
     * keys are destination region regionIDs, value keys are subregion
     * destination subregionIDs, value values are population counts.
     */
    protected TreeMap<String, TreeMap<String, GENESIS_Population>> subregionInMigration;

    public TreeMap<String, TreeMap<String, GENESIS_Population>> getSubregionInMigration(
            boolean handleOutOfMemoryError) {
        try {
            if (subregionInMigration == null) {
                File subregionInMigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "subregionInMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
                if (!subregionInMigrationFile.exists()) {
                    System.err.println(
                            "subregionInMigrationFile " + subregionInMigrationFile
                            + " does not exist in GENESIS_Migration.getSubregionInMigration(boolean)");
                }
                subregionInMigration = (TreeMap<String, TreeMap<String, GENESIS_Population>>) Generic_StaticIO.readObject(subregionInMigrationFile);
            }
            return subregionInMigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getSubregionInMigration(boolean)");
                return getSubregionInMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the population migrating out of each subregion in a region.
     * The keys are origin region regionIDs, value keys are subregion origin
     * subregionIDs, value values are population counts.
     */
    protected TreeMap<String, TreeMap<String, GENESIS_Population>> subregionOutMigration;

    public TreeMap<String, TreeMap<String, GENESIS_Population>> getSubregionOutMigration(
            boolean handleOutOfMemoryError) {
        try {
            if (subregionOutMigration == null) {
                File subregionOutMigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "subregionOutMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
                if (!subregionOutMigrationFile.exists()) {
                    System.err.println(
                            "subregionOutMigrationFile " + subregionOutMigrationFile
                            + " does not exist in GENESIS_Migration.getSubregionOutMigration(boolean)");
                }
                subregionOutMigration = (TreeMap<String, TreeMap<String, GENESIS_Population>>) Generic_StaticIO.readObject(subregionOutMigrationFile);
            }
            return subregionOutMigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getSubregionOutMigration(boolean)");
                return getSubregionOutMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the population migrating internally within each region. The
     * keys are origin/destination region regionIDs, value keys are subregion
     * destination subregionIDs, value values are population counts.
     */
    protected TreeMap<String, TreeMap<String, GENESIS_Population>> subregionInternalInMigration;

    public TreeMap<String, TreeMap<String, GENESIS_Population>> getSubregionInternalInMigration(
            boolean handleOutOfMemoryError) {
        try {
            if (subregionInternalInMigration == null) {
                File subregionInternalInMigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "subregionInternalInMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
                if (!subregionInternalInMigrationFile.exists()) {
                    System.err.println(
                            "subregionInternalInMigrationFile " + subregionInternalInMigrationFile
                            + " does not exist in GENESIS_Migration.getSubregionInternalInMigration(boolean)");
                }
                subregionInternalInMigration = (TreeMap<String, TreeMap<String, GENESIS_Population>>) Generic_StaticIO.readObject(subregionInternalInMigrationFile);
            }
            return subregionInternalInMigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getSubregionInternalInMigration(boolean)");
                return getSubregionInternalInMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the population migrating internally within each region. The
     * keys are origin/destination region regionIDs, value keys are subregion
     * origin subregionIDs, value values are population counts.
     */
    protected TreeMap<String, TreeMap<String, GENESIS_Population>> subregionInternalOutMigration;

    public TreeMap<String, TreeMap<String, GENESIS_Population>> getSubregionInternalOutMigration(
            boolean handleOutOfMemoryError) {
        try {
            if (subregionInternalOutMigration == null) {
                File subregionInternalOutMigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "subregionInternalOutMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
                if (!subregionInternalOutMigrationFile.exists()) {
                    System.err.println(
                            "subregionInternalOutMigrationFile " + subregionInternalOutMigrationFile
                            + " does not exist in GENESIS_Migration.getSubregionInternalOutMigration(boolean)");
                }
                subregionInternalOutMigration = (TreeMap<String, TreeMap<String, GENESIS_Population>>) Generic_StaticIO.readObject(subregionInternalOutMigrationFile);
            }
            return subregionInternalOutMigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getSubregionInternalOutMigration(boolean)");
                return getSubregionInternalOutMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the daily gender age migration rates for people migrating
     * into a specific region. The keys are the regionIDs for the destination
     * regions. The keys of the values are the origin region IDs where the
     * migrants have migrated from. The rates are derived from
     * <code>regionToRegionMigration</code> but only include rates for
     * destination regions within the study region.
     *
     * @TODO Account for regionImmigration separately.
     */
    protected TreeMap<String, TreeMap<String, GENESIS_Population>> regionInMigrationRates;

    public TreeMap<String, TreeMap<String, GENESIS_Population>> getRegionInMigrationRates(
            boolean handleOutOfMemoryError) {
        try {
            if (regionInMigrationRates == null) {
                File regionInMigrationRatesFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "regionInMigrationRates_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
                if (!regionInMigrationRatesFile.exists()) {
                    System.err.println(
                            "regionInMigrationRatesFile " + regionInMigrationRatesFile
                            + " does not exist in GENESIS_Migration.getRegionInMigrationRates(boolean)");
                }
                regionInMigrationRates = (TreeMap<String, TreeMap<String, GENESIS_Population>>) Generic_StaticIO.readObject(regionInMigrationRatesFile);
            }
            return regionInMigrationRates;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapDataAny()) {
                    rationaliseMigrationData(); // Swap other migration data!
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getRegionInMigrationRates(boolean)");
                return getRegionInMigrationRates(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the daily gender age migration rates for people moving from a
     * specific region (internal migration included). The keys are regionIDs,
     * the values are probabilities specified by gender and age.
     *
     * @TODO Account for emigration.
     */
    protected TreeMap<String, GENESIS_Population> regionOutMigrationRates;

    public TreeMap<String, GENESIS_Population> getRegionOutMigrationRates(
            boolean handleOutOfMemoryError) {
        try {
            if (regionOutMigrationRates == null) {
                File regionOutMigrationRatesFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "regionOutMigrationRates_TreeMapStringGENESIS_Population.thisFile");
                if (!regionOutMigrationRatesFile.exists()) {
                    System.err.println(
                            "regionOutMigrationRatesFile " + regionOutMigrationRatesFile
                            + " does not exist in GENESIS_Migration.getRegionOutMigrationRates(boolean)");
                }
                regionOutMigrationRates = (TreeMap<String, GENESIS_Population>) Generic_StaticIO.readObject(regionOutMigrationRatesFile);
            }
            return regionOutMigrationRates;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapDataAny()) {
                    rationaliseMigrationData(); // Swap other migration data!
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getRegionOutMigrationRates(boolean)");
                return getRegionOutMigrationRates(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For storing the daily gender and age specific immigration rates for
     * people immigrating into a specific region. The keys are destination
     * region regionIDs, the values are population counts.
     *
     * @TODO Account for regionImmigration separately.
     */
    protected TreeMap<String, GENESIS_Population> immigrationRates;

    public TreeMap<String, GENESIS_Population> getImmigrationRates(
            boolean handleOutOfMemoryError) {
        try {
            if (immigrationRates == null) {
                File immigrationRatesFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "immigrationRates_TreeMapStringGENESIS_Population.thisFile");
                if (!immigrationRatesFile.exists()) {
                    System.err.println(
                            "immigrationRatesFile " + immigrationRatesFile
                            + " does not exist in GENESIS_Migration.getImmigrationRates(boolean)");
                }
                immigrationRates = (TreeMap<String, GENESIS_Population>) Generic_StaticIO.readObject(immigrationRatesFile);
            }
            return immigrationRates;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapDataAny()) {
                    rationaliseMigrationData(); // Swap other migration data!
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.getImmigrationRates(boolean)");
                return getImmigrationRates(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For those migrating from a region this gives a way to assign the expected
     * region destination (which may be the same). Keys are origin region region
     * IDs. Value keys are region destination regionIDs, value values are the
     * cumulative sum rescaled populations.
     */
    protected TreeMap<String, TreeMap<String, GENESIS_Population>> cumulativeSumRescaledRegionOutMigration;

    public TreeMap<String, TreeMap<String, GENESIS_Population>> getCumulativeSumRescaledRegionOutMigration(
            boolean handleOutOfMemoryError) {
        try {
            if (cumulativeSumRescaledRegionOutMigration == null) {
                File cumulativeSumRescaledRegionOutMigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "cumulativeSumRescaledRegionOutMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
                if (!cumulativeSumRescaledRegionOutMigrationFile.exists()) {
                    System.err.println(
                            "cumulativeSumRescaledRegionOutMigrationFile " + cumulativeSumRescaledRegionOutMigrationFile
                            + " does not exist in GENESIS_Migration.getRegionInMigrationRates(boolean)");
                }
                cumulativeSumRescaledRegionOutMigration = (TreeMap<String, TreeMap<String, GENESIS_Population>>) Generic_StaticIO.readObject(cumulativeSumRescaledRegionOutMigrationFile);
            }
            return cumulativeSumRescaledRegionOutMigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapDataAny()) {
                    rationaliseMigrationData(); // Swap other migration data!
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.cumulativeSumRescaledRegionOutMigration(boolean)");
                return getCumulativeSumRescaledRegionOutMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    /**
     * For those migrating into a region this gives a way to assign a subregion
     * destination. Keys are destination region region IDs. Value keys are
     * subregion destination subregionIDs, value values are the cumulative sum
     * rescaled populations.
     */
    protected TreeMap<String, TreeMap<String, GENESIS_Population>> cumulativeSumRescaledSubregionInMigration;

    public TreeMap<String, TreeMap<String, GENESIS_Population>> getCumulativeSumRescaledSubregionInMigration(
            boolean handleOutOfMemoryError) {
        try {
            if (cumulativeSumRescaledSubregionInMigration == null) {
                File cumulativeSumRescaledRegionInMigrationFile = new File(
                        getDirectory(handleOutOfMemoryError),
                        "cumulativeSumRescaledRegionInMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
                if (!cumulativeSumRescaledRegionInMigrationFile.exists()) {
                    System.err.println(
                            "cumulativeSumRescaledRegionInMigrationFile " + cumulativeSumRescaledRegionInMigrationFile
                            + " does not exist in GENESIS_Migration.getRegionInMigrationRates(boolean)");
                }
                cumulativeSumRescaledSubregionInMigration = (TreeMap<String, TreeMap<String, GENESIS_Population>>) Generic_StaticIO.readObject(cumulativeSumRescaledRegionInMigrationFile);
            }
            return cumulativeSumRescaledSubregionInMigration;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (!ge.swapDataAny()) {
                    rationaliseMigrationData(); // Swap other migration data!
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                System.err.println(e.getMessage() + " GENESIS_Migration.cumulativeSumRescaledRegionInMigration(boolean)");
                return getCumulativeSumRescaledSubregionInMigration(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    public void loadMigrationData() {
        boolean handleOutOfMemoryError = true;
        getRegionToRegionMigration(handleOutOfMemoryError);
        //getRegionInMigration(handleOutOfMemoryError);
        //getRegionOutMigration(handleOutOfMemoryError);
        getSubregionInMigration(handleOutOfMemoryError);
        getSubregionOutMigration(handleOutOfMemoryError);
        getSubregionInternalInMigration(handleOutOfMemoryError);
        getSubregionInternalOutMigration(handleOutOfMemoryError);
        getRegionInMigrationRates(handleOutOfMemoryError);
        getRegionOutMigrationRates(handleOutOfMemoryError);
        getImmigrationRates(handleOutOfMemoryError);

        getCumulativeSumRescaledRegionOutMigration(handleOutOfMemoryError);
        getCumulativeSumRescaledSubregionInMigration(handleOutOfMemoryError);
    }

    public void writeMigrationData() {
        boolean handleOutOfMemoryError = true;
        // Swap regionToRegionMigration
        File regionToRegionMigrationFile = new File(
                getDirectory(handleOutOfMemoryError),
                "regionToRegionMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(regionToRegionMigration, regionToRegionMigrationFile);
        //regionToRegionMigration = null;
        // Swap regionInMigration
        File regionInMigrationFile = new File(
                getDirectory(handleOutOfMemoryError),
                "regionInMigration_TreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(regionInMigration, regionInMigrationFile);
        //regionInMigration = null;
        // Swap regionOutMigration
        File regionOutMigrationFile = new File(
                getDirectory(handleOutOfMemoryError),
                "regionOutMigration_TreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(regionOutMigration, regionOutMigrationFile);
        //regionOutMigration = null;
        // Swap regionInternalMigration
        File regionInternalMigrationFile = new File(
                getDirectory(handleOutOfMemoryError),
                "regionInternalMigration_TreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(regionInternalMigration, regionInternalMigrationFile);
        //regionInternalMigration = null;
        // Swap regionImmigration
        File regionImmigrationFile = new File(
                getDirectory(handleOutOfMemoryError),
                "regionImmigration_TreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(regionImmigration, regionImmigrationFile);
        //regionImmigration = null;
        // Swap subregionInMigration
        File subregionInMigrationFile = new File(
                getDirectory(handleOutOfMemoryError),
                "subregionInMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(subregionInMigration, subregionInMigrationFile);
        //subregionInMigration = null;
        // Swap subregionOutMigration
        File subregionOutMigrationFile = new File(
                getDirectory(handleOutOfMemoryError),
                "subregionOutMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(subregionOutMigration, subregionOutMigrationFile);
        //subregionOutMigration = null;
        // Swap subregionInternalInMigration
        File subregionInternalInMigrationFile = new File(
                getDirectory(handleOutOfMemoryError),
                "subregionInternalInMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(subregionInternalInMigration, subregionInternalInMigrationFile);
        //subregionInternalInMigration = null;
        // Swap subregionInternalInMigration
        File subregionInternalOutMigrationFile = new File(
                getDirectory(handleOutOfMemoryError),
                "subregionInternalOutMigration_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(subregionInternalOutMigration, subregionInternalOutMigrationFile);
        //subregionInternalOutMigration = null;
        // Swap regionInMigrationRates
        File regionInMigrationRatesFile = new File(
                getDirectory(handleOutOfMemoryError),
                "regionInMigrationRates_TreeMapStringTreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(regionInMigrationRates, regionInMigrationRatesFile);
        //regionInMigrationRates = null;
        // Swap regionOutMigrationRates
        File regionOutMigrationRatesFile = new File(
                getDirectory(handleOutOfMemoryError),
                "regionOutMigrationRates_TreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(regionOutMigrationRates, regionOutMigrationRatesFile);
        //regionOutMigrationRates = null;
        // Swap immigrationRates
        File immigrationRatesFile = new File(
                getDirectory(handleOutOfMemoryError),
                "immigrationRates_TreeMapStringGENESIS_Population.thisFile");
        Generic_StaticIO.writeObject(immigrationRates, immigrationRatesFile);
        //immigrationRates = null;
    }

    public void rationaliseMigrationData() {
        regionToRegionMigration = null;
        regionInMigration = null;
        regionOutMigration = null;
        regionInternalMigration = null;
        regionImmigration = null;
        subregionInMigration = null;
        subregionOutMigration = null;
        subregionInternalInMigration = null;
        subregionInternalOutMigration = null;
        //regionInMigrationRates = null;
        //regionOutMigrationRates = null;
        immigrationRates = null;
    }

    public GENESIS_Migration() {
        init();
    }

    public GENESIS_Migration(GENESIS_Environment a_GENESIS_Environment) {
        this.ge = a_GENESIS_Environment;
        init();
    }

    public GENESIS_Migration(
            GENESIS_Environment a_GENESIS_Environment,
            GENESIS_Migration a_GENESIS_Migration) {
        this.ge = a_GENESIS_Environment;
        // region counts
        this.regionToRegionMigration = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_GENESIS_Migration.regionToRegionMigration);
        this.regionInMigration = GENESIS_Collections.deepCopyTo_TreeMap_String_Population(
                a_GENESIS_Migration.regionInMigration);
        this.regionOutMigration = GENESIS_Collections.deepCopyTo_TreeMap_String_Population(
                a_GENESIS_Migration.regionOutMigration);
        this.regionInternalMigration = GENESIS_Collections.deepCopyTo_TreeMap_String_Population(
                a_GENESIS_Migration.regionInternalMigration);
        this.regionImmigration = GENESIS_Collections.deepCopyTo_TreeMap_String_Population(
                a_GENESIS_Migration.regionImmigration);
        // subregion counts
        this.subregionInMigration = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_GENESIS_Migration.subregionInMigration);
        this.subregionOutMigration = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_GENESIS_Migration.subregionOutMigration);
        this.subregionInternalInMigration = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_GENESIS_Migration.subregionInternalInMigration);
        this.subregionInternalOutMigration = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_GENESIS_Migration.subregionInternalOutMigration);
        // region rates        
        this.regionInMigrationRates = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_GENESIS_Migration.regionInMigrationRates);
        this.regionOutMigrationRates = GENESIS_Collections.deepCopyTo_TreeMap_String_Population(
                a_GENESIS_Migration.regionOutMigrationRates);
        // cumulative look up references
        this.cumulativeSumRescaledSubregionInMigration = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_GENESIS_Migration.cumulativeSumRescaledSubregionInMigration);
        this.cumulativeSumRescaledRegionOutMigration = GENESIS_Collections.deepCopyTo_TreeMap_String_TreeMap_String_Population(
                a_GENESIS_Migration.cumulativeSumRescaledRegionOutMigration);
    }

    /**
     * The keys are LAD areaIDs, the values are a TreeMap with keys being
     * specific Gender_Age
     */
    public final void init() {
        CommonFactory.init();
        // region counts
        regionToRegionMigration = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        regionInMigration = new TreeMap<String, GENESIS_Population>();
        regionOutMigration = new TreeMap<String, GENESIS_Population>();
        regionInternalMigration = new TreeMap<String, GENESIS_Population>();
        regionImmigration = new TreeMap<String, GENESIS_Population>();
        // subregion counts
        subregionInMigration = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        subregionOutMigration = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        subregionInternalInMigration = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        subregionInternalOutMigration = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        // region rates
        regionInMigrationRates = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        regionOutMigrationRates = new TreeMap<String, GENESIS_Population>();
        // cumulative look up references
        cumulativeSumRescaledSubregionInMigration = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        cumulativeSumRescaledRegionOutMigration = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
    }

    public static void main(String[] args) {
        try {
            System.out.println(System.getProperties().keySet().toString());
            System.out.println(System.getProperties().values().toString());
            File directory = new File(args[0]);
            File logDirectory = new File(
                    directory,
                    GENESIS_Log.Generic_DefaultLogDirectoryName);
            String logname = "uk.ac.leeds.ccg.andyt.projects.genesis";
            GENESIS_Log.parseLoggingProperties(
                    directory,
                    logDirectory,
                    logname);
            GENESIS_Environment _GENESIS_Environment =
                    new GENESIS_Environment(directory);
            _GENESIS_Environment._Directory = directory;
            _GENESIS_Environment._Time = new GENESIS_Time(CommonFactory.newTime(2001L));
            GENESIS_Migration instance = new GENESIS_Migration(
                    _GENESIS_Environment);
            //instance.run();
            instance.runTest();
            GENESIS_Log.reset();
        } catch (Error e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void run() {
//       init();
        File dir = new File(
                ge._Directory,
                "InputData");
        dir = new File(
                dir,
                "DemographicData");
        dir = new File(
                dir,
                "Migration");
        File serialisedMigrationFile = new File(
                dir,
                "GENESIS_Migration.thisFile");
        runFormatData();
        Generic_StaticIO.writeObject(this, serialisedMigrationFile);
        Object obj = Generic_StaticIO.readObject(serialisedMigrationFile);
        GENESIS_Migration mig = (GENESIS_Migration) obj;
        mig.ge = ge;
//        mig.runFormatData();
        String originRegionID = "00DA";
        String destinationRegionID = "00DB";
        mig.writeRegionCountSummaries(
                originRegionID,
                destinationRegionID);
        String originSubregionID = "00DAFA0003";//"00DAFA0001";
        String destinationSubregionID = "00DBFA0001";//"00DBFB0003";
//        mig.writeSubregionCountSummaries(
//                originRegionID,
//                destinationRegionID,
//                originSubregionID,
//                destinationSubregionID);


//        Generic_StaticIO.writeObject(mig, serialisedMigrationFile);
        ////mig.writeRegionRateSummaries();
        //System.out.println("regionOutMigration.size() " + mig.regionOutMigration.size());
        //System.out.println("regionImmigration.size() " + mig.regionImmigration.size());
        //System.out.println("regionInMigration.size() " + mig.regionInMigration.size());
    }

    public void runTest() {
//       init();
        File dir = new File(
                ge._Directory,
                "InputData");
        dir = new File(
                dir,
                "DemographicData");
        dir = new File(
                dir,
                "MigrationTest");
        File serialisedMigrationFile = new File(
                dir,
                "GENESIS_Migration.thisFile");
        runFormatData();
        Generic_StaticIO.writeObject(this, serialisedMigrationFile);
        Object obj = Generic_StaticIO.readObject(serialisedMigrationFile);
        GENESIS_Migration mig = (GENESIS_Migration) obj;
        mig.ge = ge;
//        mig.runFormatData();
        String originRegionID = "00DA";
        String destinationRegionID = "00DB";
        mig.writeRegionCountSummaries(
                originRegionID,
                destinationRegionID);
        String originSubregionID = "00DAFA0003";//"00DAFA0001";
        String destinationSubregionID = "00DBFA0001";//"00DBFB0003";
//        mig.writeSubregionCountSummaries(
//                originRegionID,
//                destinationRegionID,
//                originSubregionID,
//                destinationSubregionID);


//        Generic_StaticIO.writeObject(mig, serialisedMigrationFile);
        ////mig.writeRegionRateSummaries();
        //System.out.println("regionOutMigration.size() " + mig.regionOutMigration.size());
        //System.out.println("regionImmigration.size() " + mig.regionImmigration.size());
        //System.out.println("regionInMigration.size() " + mig.regionInMigration.size());
    }

    /**
     * "Origin geography: UK interaction data districts 2001","Destination
     * geography: UK interaction data districts 2001" "Geography",,"Interaction
     * data",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
     * "Origins","Destinations","2001 SMS Level
     * 1",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
     * "UK interaction data districts 2001","UK interaction data districts
     * 2001","[Table 1; Cell 1] Total;","[Table 1; Cell 2] Total;Sex
     * Male","[Table 1; Cell 3] Total;Sex Female","[Table 1; Cell 4] Age
     * 0;","[Table 1; Cell 5] Age 0;Sex Male","[Table 1; Cell 6] Age 0;Sex
     * Female","[Table 1; Cell 7] Age 1-2;","[Table 1; Cell 8] Age 1-2;Sex
     * Male","[Table 1; Cell 9] Age 1-2;Sex Female","[Table 1; Cell 10] Age
     * 3-4;","[Table 1; Cell 11] Age 3-4;Sex Male","[Table 1; Cell 12] Age
     * 3-4;Sex Female","[Table 1; Cell 13] Age 5-9;","[Table 1; Cell 14] Age
     * 5-9;Sex Male","[Table 1; Cell 15] Age 5-9;Sex Female","[Table 1; Cell 16]
     * Age 10-11;","[Table 1; Cell 17] Age 10-11;Sex Male","[Table 1; Cell 18]
     * Age 10-11;Sex Female","[Table 1; Cell 19] Age 12-14;","[Table 1; Cell 20]
     * Age 12-14;Sex Male","[Table 1; Cell 21] Age 12-14;Sex Female","[Table 1;
     * Cell 22] Age 15;","[Table 1; Cell 23] Age 15;Sex Male","[Table 1; Cell
     * 24] Age 15;Sex Female","[Table 1; Cell 25] Age 16-17;","[Table 1; Cell
     * 26] Age 16-17;Sex Male","[Table 1; Cell 27] Age 16-17;Sex Female","[Table
     * 1; Cell 28] Age 18-19;","[Table 1; Cell 29] Age 18-19;Sex Male","[Table
     * 1; Cell 30] Age 18-19;Sex Female","[Table 1; Cell 31] Age 20-24;","[Table
     * 1; Cell 32] Age 20-24;Sex Male","[Table 1; Cell 33] Age 20-24;Sex
     * Female","[Table 1; Cell 34] Age 25-29;","[Table 1; Cell 35] Age 25-29;Sex
     * Male","[Table 1; Cell 36] Age 25-29;Sex Female","[Table 1; Cell 37] Age
     * 30-34;","[Table 1; Cell 38] Age 30-34;Sex Male","[Table 1; Cell 39] Age
     * 30-34;Sex Female","[Table 1; Cell 40] Age 35-39;","[Table 1; Cell 41] Age
     * 35-39;Sex Male","[Table 1; Cell 42] Age 35-39;Sex Female","[Table 1; Cell
     * 43] Age 40-44;","[Table 1; Cell 44] Age 40-44;Sex Male","[Table 1; Cell
     * 45] Age 40-44;Sex Female","[Table 1; Cell 46] Age 45-49;","[Table 1; Cell
     * 47] Age 45-49;Sex Male","[Table 1; Cell 48] Age 45-49;Sex Female","[Table
     * 1; Cell 49] Age 50-54;","[Table 1; Cell 50] Age 50-54;Sex Male","[Table
     * 1; Cell 51] Age 50-54;Sex Female","[Table 1; Cell 52] Age 55-59;","[Table
     * 1; Cell 53] Age 55-59;Sex Male","[Table 1; Cell 54] Age 55-59;Sex
     * Female","[Table 1; Cell 55] Age 60-64;","[Table 1; Cell 56] Age 60-64;Sex
     * Male","[Table 1; Cell 57] Age 60-64;Sex Female","[Table 1; Cell 58] Age
     * 65-69;","[Table 1; Cell 59] Age 65-69;Sex Male","[Table 1; Cell 60] Age
     * 65-69;Sex Female","[Table 1; Cell 61] Age 70-74;","[Table 1; Cell 62] Age
     * 70-74;Sex Male","[Table 1; Cell 63] Age 70-74;Sex Female","[Table 1; Cell
     * 64] Age 75-79;","[Table 1; Cell 65] Age 75-79;Sex Male","[Table 1; Cell
     * 66] Age 75-79;Sex Female","[Table 1; Cell 67] Age 80-84;","[Table 1; Cell
     * 68] Age 80-84;Sex Male","[Table 1; Cell 69] Age 80-84;Sex Female","[Table
     * 1; Cell 70] Age 85-89;","[Table 1; Cell 71] Age 85-89;Sex Male","[Table
     * 1; Cell 72] Age 85-89;Sex Female","[Table 1; Cell 73] Age 90+;","[Table
     * 1; Cell 74] Age 90+;Sex Male","[Table 1; Cell 75] Age 90+;Sex Female"
     * "City of London","City of
     * London",154,71,83,0,0,0,7,3,4,0,0,0,4,0,4,3,3,0,0,0,0,0,0,0,0,0,0,3,3,0,15,9,6,23,7,16,16,7,9,18,7,11,18,12,6,13,4,9,19,10,9,3,3,0,6,3,3,0,0,0,3,0,3,3,0,3,0,0,0,0,0,0,0,0,0
     * Need a LUT for District Name to LAD Code
     */
    public void runFormatData() {
        File dataDirectory = new File(
                ge.get_Directory(true).getParentFile(),
                "data");
//        // Load LUT
//        File areaClassificationDataDirectory = new File(
//                dataDirectory,
//                "AreaClassificationData");
//        Object[] lut = loadLUT(
//                areaClassificationDataDirectory,
//                "2001LADAreaClassificationFromCIDER.lut");
//        TreeMap<String, String> nameToCodeLUT = (TreeMap<String, String>) lut[0];
        // Load MigrationData
        File migrationDataDirectory = new File(
                dataDirectory,
                "MigrationData");
        readSubregionMigrationData(
                migrationDataDirectory,
                //"2001SMSLADwicid_output.csv");
                "bespoke_output_aturner_261012-sms3.csv");
        readRegionMigrationData(
                migrationDataDirectory,
                //"2001SMSLADwicid_output.csv");
                "2001SMSTable1LAD_wicid_output.csv");
    }

    public void writeRegionRateSummaries(
            String originRegionID,
            String destinationRegionID) {
        Iterator<GENESIS_AgeBound> ite;
        String name;
        GENESIS_Population rates;
        GENESIS_Population counts;
//        // In Migration
//        rates = regionInMigrationRates.get(destinationRegionID).get(originRegionID);
//        counts = regionInMigration.get(destinationRegionID);
//        name = "InMigration";
//        writeAndUpdateRates(
//                name,
//                originRegionID, destinationRegionID,
//                counts, rates);
//        // Out Migration
//        rates = regionOutMigrationRates.get(originRegionID);
//        counts = regionOutMigration.get(originRegionID);
//        name = "OutMigration";
//        writeAndUpdateRates(
//                name,
//                originRegionID, destinationRegionID,
//                counts, rates);
//        // Immigration (region)
//        rates = immigrationRates.get(destinationRegionID);
//        counts = regionImmigration.get(destinationRegionID);
//        name = "Immigration";
//        writeAndUpdateRates(
//                name,
//                originRegionID, destinationRegionID,
//                counts, rates);
//        // Internal Migration 
//        rates = internalMigrationRates.get(destinationRegionID).get(destinationRegionID).get(destinationRegionID);
//        counts = regionInternalMigration.get(destinationRegionID);
//        name = "InternalMigration(region)";
//        writeAndUpdateRates(
//                name,
//                originRegionID, destinationRegionID,
//                counts, rates);
//        // Internal Migration 
//        rates = internalMigrationRates.get(destinationRegionID).get(originRegionID).get(originRegionID);
//        counts = regionInternalMigration.get(destinationRegionID);
//        name = "InternalMigration(StudyRegion)";
//        writeAndUpdateRates(
//                name,
//                originRegionID, destinationRegionID,
//                counts, rates);
//        // Internal Migration (RestOfUK_String)
//        rates = internalMigrationRates.get(destinationRegionID).get(RestOfUK_String).get(RestOfUK_String);
//        counts = regionInMigration.get(destinationRegionID);
//        name = "InternalMigration(RestOfUK_String)";
//        writeAndUpdateRates(
//                name,
//                RestOfUK_String, destinationRegionID,
//                counts, rates);
    }

    public void writeSubregionRateSummaries(
            String originRegionID,
            String destinationRegionID,
            String originSubregionID,
            String destinationSubregionID) {
        int decimalPlacePrecision = 5;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        // Internal Migration (subregion)
        GENESIS_Population internalMigrationCount = subregionInternalInMigration.get(destinationRegionID).get(destinationSubregionID);
        //GENESIS_Population internalMigrationRate = internalMigrationRates.get(destinationRegionID).get(destinationRegionID).get(destinationSubregionID);
        System.out.println("InternalMigration originRegionID " + originRegionID
                + ", destinationRegionID " + destinationRegionID
                + ", destinationSubregionID " + destinationSubregionID);
        // Female
        System.out.println("AgeMinYear, AgeMaxYear, AnnualCount, DailyRate");
        Iterator<GENESIS_AgeBound> ite;
        ite = internalMigrationCount._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            BigDecimal count = internalMigrationCount._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            BigDecimal rate = Generic_BigDecimal.divideRoundIfNecessary(
                    count,
                    new BigDecimal(GENESIS_Time.NormalDaysInYear_BigInteger),
                    decimalPlacePrecision,
                    roundingMode);
            //BigDecimal rate = internalMigrationRate._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (ageBound.getAgeMax() != null) {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + ageBound.getAgeMax().getYear() + ", " + count
                        + ", " + rate);
            } else {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + "null, " + count
                        + ", " + rate);
            }
        }
        // InternalMigration subregionID Male
        System.out.println("InternalMigration originRegionID " + destinationRegionID
                + ", destinationRegionID " + destinationRegionID
                + ", destinationSubregionID " + destinationSubregionID
                + "  Male");
        System.out.println("AgeMinYear, AgeMaxYear, AnnualCount, DailyRate");
        ite = internalMigrationCount._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            BigDecimal count = internalMigrationCount._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            BigDecimal rate = Generic_BigDecimal.divideRoundIfNecessary(
                    count,
                    new BigDecimal(GENESIS_Time.NormalDaysInYear_BigInteger),
                    decimalPlacePrecision,
                    roundingMode);
            //BigDecimal rate = internalMigrationRate._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (ageBound.getAgeMax() != null) {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + ageBound.getAgeMax().getYear() + ", " + count
                        + ", " + rate);
            } else {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + "null, " + count
                        + ", " + rate);
            }
        }
    }

    public void writeRegionCountSummaries(
            String originRegionID,
            String destinationRegionID) {

        Iterator<GENESIS_AgeBound> ite;
        GENESIS_Population pop;
        String name;

        // regionInMigration
        pop = this.regionInMigration.get(destinationRegionID);
        name = "regionInMigration";
        writeAndUpdateCounts(
                name,
                originRegionID,
                destinationRegionID,
                pop);
        // regionOutMigration
        pop = this.regionOutMigration.get(originRegionID);
        name = "regionOutMigration";
        writeAndUpdateCounts(
                name,
                originRegionID,
                destinationRegionID,
                pop);
        // regionInternalMigration
        pop = this.regionInternalMigration.get(originRegionID);
        name = "regionInternalMigration";
        writeAndUpdateCounts(
                name,
                originRegionID,
                destinationRegionID,
                pop);
        // regionImmigration
        pop = this.regionImmigration.get(originRegionID);
        name = "regionImmigration";
        writeAndUpdateCounts(
                name,
                originRegionID,
                destinationRegionID,
                pop);

//        TreeMap<String, TreeMap<String, GENESIS_Population>> regionInterregionMigration;
//        regionInterregionMigration = internalMigrationRates.get(originRegionID);
//        // regionInterregionMigration
//        pop = regionInterregionMigration.get(originRegionID);
//        name = "subregionInternalInMigration";
//        writeAndUpdateCounts(
//                name,
//                originRegionID,
//                destinationRegionID,
//                pop);

//        // OutMigration region
//        // Female
//        System.out.println("Female");
//        System.out.println("OutMigration " + originRegionID);
//        System.out.println("AgeMinYear, AgeMaxYear, Count");
//        ite = regionOutMigration._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
//        while (ite.hasNext()) {
//            GENESIS_AgeBound ageBound = ite.next();
//            BigDecimal count = regionOutMigration._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
//            if (ageBound.getAgeMax() != null) {
//                System.out.println(
//                        "" + ageBound.getAgeMin().getYear() + ", "
//                        + ageBound.getAgeMax().getYear() + ", " + count);
//            } else {
//                System.out.println(
//                        "" + ageBound.getAgeMin().getYear() + ", "
//                        + "null, " + count);
//            }
//        }
//        // Male
//        System.out.println("Male");
//        System.out.println("OutMigration " + originRegionID);
//        System.out.println("AgeMinYear, AgeMaxYear, Count, Rate");
//        ite = regionOutMigration._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
//        while (ite.hasNext()) {
//            GENESIS_AgeBound ageBound = ite.next();
//            BigDecimal count = regionOutMigration._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
//            if (ageBound.getAgeMax() != null) {
//                System.out.println(
//                        "" + ageBound.getAgeMin().getYear() + ", "
//                        + ageBound.getAgeMax().getYear() + ", " + count);
//            } else {
//                System.out.println(
//                        "" + ageBound.getAgeMin().getYear() + ", "
//                        + "null, " + count);
//            }
//        }
//
//        // Immigration (region)
//        //Female
//        System.out.println("Female");
//        System.out.println("Immigration " + originRegionID + " Female");
//        System.out.println("AgeMinYear, AgeMaxYear, "
//                + "ImmigrationCount, RestOfUKInMigrationCount");
//        ite = regionImmigration._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
//        while (ite.hasNext()) {
//            GENESIS_AgeBound ageBound = ite.next();
//            BigDecimal immigrationCount = regionImmigration._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
//            if (ageBound.getAgeMax() != null) {
//                System.out.println(
//                        "" + ageBound.getAgeMin().getYear() + ", "
//                        + ageBound.getAgeMax().getYear()
//                        + ", " + immigrationCount);
//            } else {
//                System.out.println(
//                        "" + ageBound.getAgeMin().getYear() + ", "
//                        + "null, " + immigrationCount);
//            }
//        }
//        //Male
//        System.out.println("Male");
//        System.out.println("Immigration " + originRegionID + " Female");
//        System.out.println("AgeMinYear, AgeMaxYear, "
//                + "ImmigrationCount, RestOfUKInMigrationCount");
//        ite = regionImmigration._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
//        while (ite.hasNext()) {
//            GENESIS_AgeBound ageBound = ite.next();
//            BigDecimal immigrationCount = regionImmigration._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
//            if (ageBound.getAgeMax() != null) {
//                System.out.println(
//                        "" + ageBound.getAgeMin().getYear() + ", "
//                        + ageBound.getAgeMax().getYear()
//                        + ", " + immigrationCount);
//            } else {
//                System.out.println(
//                        "" + ageBound.getAgeMin().getYear() + ", "
//                        + "null, " + immigrationCount);
//            }
//        }
    }

    protected void writeAndUpdateCounts(
            String name,
            String originRegionID,
            String destinationRegionID,
            GENESIS_Population pop) {
        Iterator<GENESIS_AgeBound> ite;
        // Female
        System.out.println("Female");
        System.out.println(name + " originRegionID " + originRegionID
                + ", destinationRegionID " + destinationRegionID);
        System.out.println("AgeMinYear, AgeMaxYear, " + name + "Count");
        ite = pop._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            BigDecimal count = pop._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (ageBound.getAgeMax() != null) {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + ageBound.getAgeMax().getYear() + ", " + count);
            } else {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + "null, " + count);
            }
        }
        // Male
        System.out.println("Male");
        System.out.println(name + " originRegionID " + originRegionID
                + ", destinationRegionID " + destinationRegionID);
        System.out.println("AgeMinYear, AgeMaxYear, " + name + "Count");
        ite = pop._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            BigDecimal count = pop._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (ageBound.getAgeMax() != null) {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + ageBound.getAgeMax().getYear() + ", " + count);
            } else {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + "null, " + count);
            }
        }
        pop.updateGenderedAgePopulation();
    }

    protected void writeAndUpdateRates(
            String name,
            String originRegionID,
            String destinationRegionID,
            GENESIS_Population counts,
            GENESIS_Population rates) {
        Iterator<GENESIS_AgeBound> ite;
        // Female
        System.out.println("Female");
        System.out.println(name + " originRegionID " + originRegionID
                + ", destinationRegionID " + destinationRegionID);
        System.out.println("AgeMinYear, AgeMaxYear, " + name + "Count, "
                + name + "Rate");
        ite = counts._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            BigDecimal count = counts._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            BigDecimal rate = rates._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (ageBound.getAgeMax() != null) {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + ageBound.getAgeMax().getYear() + ", " + count
                        + ", " + rate);
            } else {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + "null, " + count
                        + ", " + rate);
            }
        }
        // Female
        System.out.println("Male");
        System.out.println(name + " originRegionID " + originRegionID
                + ", destinationRegionID " + destinationRegionID);
        System.out.println("AgeMinYear, AgeMaxYear, " + name + "Count, "
                + name + "Rate");
        ite = counts._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            BigDecimal count = counts._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            BigDecimal rate = rates._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (ageBound.getAgeMax() != null) {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + ageBound.getAgeMax().getYear() + ", " + count
                        + ", " + rate);
            } else {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + "null, " + count
                        + ", " + rate);
            }
        }
        rates.updateGenderedAgePopulation();
    }

    protected void writeAndUpdateCounts(
            String name,
            String originRegionID,
            String destinationRegionID,
            String originSubregionID,
            String destinationSubregionID,
            GENESIS_Population counts) {
        String methodName = "writeAndUpdateCounts(String,String,String,String,"
                + "String,GENESIS_Population)";
        System.out.println(methodName);
        Iterator<GENESIS_AgeBound> ite;
        System.out.println(name + " originRegionID " + originRegionID
                + ", destinationRegionID " + destinationRegionID
                + ", originSubregionID " + originSubregionID
                + ", destinationSubregionID " + destinationSubregionID);
        // Female
        System.out.println("Female");
        System.out.println("AgeMinYear, AgeMaxYear, Count");
        ite = counts._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            BigDecimal count = counts._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (ageBound.getAgeMax() != null) {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + ageBound.getAgeMax().getYear() + ", " + count);
            } else {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + "null, " + count);
            }
        }
        // Male
        System.out.println("Male");
        System.out.println("AgeMinYear, AgeMaxYear, Count");
        ite = counts._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
        while (ite.hasNext()) {
            GENESIS_AgeBound ageBound = ite.next();
            BigDecimal count = counts._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
            if (ageBound.getAgeMax() != null) {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + ageBound.getAgeMax().getYear() + ", " + count);
            } else {
                System.out.println(
                        "" + ageBound.getAgeMin().getYear() + ", "
                        + "null, " + count);
            }
        }
        counts.updateGenderedAgePopulation();
    }

    public void writeSubregionCountSummaries(
            String originRegionID,
            String destinationRegionID,
            String originSubregionID,
            String destinationSubregionID) {
        String name;
        GENESIS_Population counts;

        // subregionInternalInMigration
        counts = subregionInternalInMigration.get(destinationRegionID).get(destinationSubregionID);
        name = "subregionInternalInMigration";
        writeAndUpdateCounts(
                name,
                originRegionID, destinationRegionID,
                originSubregionID, destinationSubregionID,
                counts);

    }

    /*
     * 2001LADAreaClassificationFromCIDER.lut
     * This was orignally to get a LAD Name to LAD Code look up.
     * This may no longer be used as WICID will output LAD Code instead of the 
     * default LAD names...
     */
    public Object[] loadLUT(
            File directory,
            String filename) {
        String sourceMethod = "loadLUT(File,String)";
        Object[] result = new Object[2];
        //getLogger().entering(sourceClass, sourceMethod);
        try {
            File file = new File(directory,
                    filename);
            if (!file.exists()) {
                int debug = 0;
            }
            BufferedReader aBufferedReader = Generic_StaticIO.getBufferedReader(file);
            StreamTokenizer aStreamTokenizer =
                    new StreamTokenizer(aBufferedReader);
            Generic_StaticIO.setStreamTokenizerSyntax1(aStreamTokenizer);
            aStreamTokenizer.wordChars(':', ':');
            aStreamTokenizer.wordChars('<', '<');
            aStreamTokenizer.wordChars('_', '_');
            aStreamTokenizer.wordChars('&', '&');
            aStreamTokenizer.wordChars('\'', '\'');
            aStreamTokenizer.wordChars('(', '(');
            aStreamTokenizer.wordChars(')', ')');
            String line = "";
            //Skip the first 5 lines
            for (int i = 0; i < 5; i++) {
                Generic_StaticIO.skipline(aStreamTokenizer);
            }
            int tokenType = aStreamTokenizer.nextToken();
            TreeMap<String, String> nameToCodeMap = new TreeMap<String, String>();
            TreeMap<String, String> codeToNameMap = new TreeMap<String, String>();
            int lineCounter = 0;
            while (tokenType != StreamTokenizer.TT_EOF) {
                switch (tokenType) {
                    case StreamTokenizer.TT_EOL:
                        //System.out.println(lineCounter + " " + line);
                        String[] fields = line.split(",");
//                        if (fields.length != 12) {
//                            System.out.println("fields.length != 12");
//                        }
                        String name = fields[1];
                        String code = fields[2];
                        //System.out.println("name " + name + ", code " + code);
//                        // Debug
//                        if (name.contains("Belfast")) {
//                            System.out.println(name);
//                        }
                        nameToCodeMap.put(name, code);
                        codeToNameMap.put(code, name);
                        lineCounter++;
                        break;
                    case StreamTokenizer.TT_WORD:
                        line = aStreamTokenizer.sval;
                        break;
                }
                tokenType = aStreamTokenizer.nextToken();
            }
            result[0] = nameToCodeMap;
            result[1] = codeToNameMap;
            aBufferedReader.close();
        } catch (IOException aIOException) {
            System.err.println(aIOException.getMessage() + " in "
                    + this.getClass().getName()
                    + "." + sourceMethod);
            System.exit(GENESIS_ErrorAndExceptionHandler.IOException);
        }
        //getLogger().exiting(sourceClass, sourceMethod);
        return result;
    }

    /**
     * read LAD migration data
     *
     * @param directory
     * @param filename
     */
    public void readRegionMigrationData(
            File directory,
            String filename) {
        String sourceMethod = "readRegionMigrationData(File,String)";
        //getLogger().entering(sourceClass, sourceMethod);
        //regionInMigration = new TreeMap<String, GENESIS_Population>();
        //internalMigration = new TreeMap<String, TreeMap<String, TreeMap<String, GENESIS_Population>>>();
        //regionOutMigration = new TreeMap<String, GENESIS_Population>();
        boolean firstNotes = true;
        try {
            File file = new File(directory,
                    filename);
            if (!file.exists()) {
                int debug = 0;
            }
            BufferedReader aBufferedReader = Generic_StaticIO.getBufferedReader(file);
            StreamTokenizer aStreamTokenizer =
                    new StreamTokenizer(aBufferedReader);
            Generic_StaticIO.setStreamTokenizerSyntax1(aStreamTokenizer);
            aStreamTokenizer.wordChars(';', ';');
            aStreamTokenizer.wordChars(':', ':');
            aStreamTokenizer.wordChars('[', '[');
            aStreamTokenizer.wordChars(']', ']');
            aStreamTokenizer.wordChars('&', '&');
            aStreamTokenizer.wordChars('\"', '\"');
            aStreamTokenizer.wordChars('\'', '\'');
            aStreamTokenizer.wordChars('(', '(');
            aStreamTokenizer.wordChars(')', ')');
            //Skip the first 3 lines
            for (int i = 0; i < 3; i++) {
                Generic_StaticIO.skipline(aStreamTokenizer);
            }
            int tokenType = aStreamTokenizer.nextToken();
            int lineCounter = 0;
            String line = null;
            TreeMap<Integer, GENESIS_AgeBound> maleAgeBounds = new TreeMap<Integer, GENESIS_AgeBound>();
            TreeMap<Integer, GENESIS_AgeBound> femaleAgeBounds = new TreeMap<Integer, GENESIS_AgeBound>();
            boolean readHeader = false;
            while (tokenType != StreamTokenizer.TT_EOF) {
                switch (tokenType) {
                    case StreamTokenizer.TT_EOL:
                        if (!readHeader) {
                            String[] fields = line.split("\",\"");
//                            System.out.println("fields.length " + fields.length);
                            for (int i = 0; i < fields.length; i++) {
//                                System.out.println("" + i + " " + fields[i]);
                                String[] split = fields[i].split("] ");
                                if (split.length > 1) {
//                                    System.out.println("" + i + " " + split[1]);
                                    if (!split[1].startsWith("Total")) {
                                        if (split[1].endsWith("Female") || split[1].endsWith("Male")) {
                                            String age = split[1].split(";")[0];
                                            String minAge_String;
                                            String maxAge_String;
//                                            System.out.println("age " + age);
                                            if (age.contains("-")) {
                                                String[] ageSplit = age.split("-");
                                                maxAge_String = ageSplit[1];
                                                minAge_String = ageSplit[0].split(" ")[1];
                                            } else {
                                                if (age.contains("+")) {
                                                    minAge_String = age.split(" ")[1];
                                                    minAge_String = minAge_String.substring(0, minAge_String.length() - 1);
                                                    maxAge_String = "null";
                                                } else {
                                                    minAge_String = age.split(" ")[1];
                                                    maxAge_String = minAge_String;
                                                }
                                            }
                                            GENESIS_AgeBound ageBound = new GENESIS_AgeBound();
                                            ageBound.setAgeMin(CommonFactory.newTime(Long.valueOf(minAge_String)));
                                            if (!maxAge_String.equalsIgnoreCase("null")) {
                                                ageBound.setAgeMax(CommonFactory.newTime(Long.valueOf(maxAge_String) + 1));
                                            }
                                            if (split[1].endsWith("Female")) {
                                                //System.out.println("Female");
                                                femaleAgeBounds.put(i, ageBound);
                                                // Initialise counts for female ageGroup
                                            }
                                            if (split[1].endsWith("Male")) {
                                                //System.out.println("Male");
                                                maleAgeBounds.put(i, ageBound);
                                                // Initialise counts for male ageGroup
                                            }
                                        }
                                    }
                                }
                            }
                            readHeader = true;
                        } else {
                            if (line.equalsIgnoreCase("Notes:")) {
                                if (firstNotes) {
                                    // If this is the first time this is encountered then it is midway down the file
                                    // Skip 7 lines
                                    for (int i = 0; i < 7; i++) {
                                        Generic_StaticIO.skipline(aStreamTokenizer);
                                    }
                                    firstNotes = false;
                                } else {
                                    // This is in the footer at the end of the file
                                    // The following two lines sets aStreamTokenizer
                                    // so that it will regard the next token as the 
                                    // end of file and stop reading.
                                    aStreamTokenizer.ttype = StreamTokenizer.TT_EOF;
                                    aStreamTokenizer.pushBack();
                                }
                            } else {
                                if (lineCounter % 10000 == 0) {
                                    System.out.println(lineCounter + " " + line);
                                }
//                                if (lineCounter > 110000) {
//                                    System.out.println(lineCounter + " " + line);
//                                }
//                                if (lineCounter == 110880) {
//                                    System.out.println(lineCounter + " " + line);
//                                }
                                String[] fields = line.split("\",");
                                String originID = fields[0].substring(1, fields[0].length());
                                //System.out.println("originOAname " + originOAname);
                                String destinationID = fields[1].substring(1, fields[1].length());
                                //System.out.println("destinationOAname " + destinationOAname);
                                String[] data = fields[2].split(",");
                                //System.out.println("data.length " + data.length);
                                TreeMap<String, GENESIS_Population> destinationRegionToRegionMigration;
                                destinationRegionToRegionMigration = regionToRegionMigration.get(destinationID);
                                if (destinationRegionToRegionMigration == null) {
                                    destinationRegionToRegionMigration = new TreeMap<String, GENESIS_Population>();
                                    regionToRegionMigration.put(destinationID, destinationRegionToRegionMigration);
                                }
                                GENESIS_Population pop;
                                Iterator<Integer> ite;
                                pop = destinationRegionToRegionMigration.get(originID);
                                if (pop == null) {
                                    pop = new GENESIS_Population();
                                    destinationRegionToRegionMigration.put(originID, pop);
                                }
                                ite = femaleAgeBounds.keySet().iterator();
                                while (ite.hasNext()) {
                                    Integer field = ite.next();
                                    GENESIS_AgeBound ageBound = femaleAgeBounds.get(field);
                                    BigDecimal population = pop.getFemalePopulation(ageBound);
                                    population = population.add(new BigDecimal(data[field - 2]));
                                    pop._FemaleAgeBoundPopulationCount_TreeMap.put(ageBound, population);
                                }
                                ite = maleAgeBounds.keySet().iterator();
                                while (ite.hasNext()) {
                                    Integer field = ite.next();
                                    GENESIS_AgeBound ageBound = maleAgeBounds.get(field);
                                    BigDecimal population = pop.getMalePopulation(ageBound);
                                    population = population.add(new BigDecimal(data[field - 2]));
                                    pop._MaleAgeBoundPopulationCount_TreeMap.put(ageBound, population);
                                }
                                if (originID.equalsIgnoreCase(destinationID)) {
                                    // regionInternalMigration
                                    pop = regionInternalMigration.get(destinationID);
                                    if (pop == null) {
                                        pop = new GENESIS_Population();
                                        regionInternalMigration.put(destinationID, pop);
                                        //pop.get
                                    }
                                    ite = femaleAgeBounds.keySet().iterator();
                                    while (ite.hasNext()) {
                                        Integer field = ite.next();
                                        GENESIS_AgeBound ageBound = femaleAgeBounds.get(field);
                                        BigDecimal population = pop.getFemalePopulation(ageBound);
                                        population = population.add(new BigDecimal(data[field - 2]));
                                        pop._FemaleAgeBoundPopulationCount_TreeMap.put(ageBound, population);
                                    }
                                    ite = maleAgeBounds.keySet().iterator();
                                    while (ite.hasNext()) {
                                        Integer field = ite.next();
                                        GENESIS_AgeBound ageBound = maleAgeBounds.get(field);
                                        BigDecimal population = pop.getMalePopulation(ageBound);
                                        population = population.add(new BigDecimal(data[field - 2]));
                                        pop._MaleAgeBoundPopulationCount_TreeMap.put(ageBound, population);
                                    }
                                }
                                //} else {
//                                if (originID.equalsIgnoreCase("No usual address one year ago")) {
//                                    // Interesting, but ignored for now!
//                                } else {
                                // Immigration
                                if (originID.endsWith(" UK")) {
                                    //if (originID.equalsIgnoreCase("Origin outside UK")) {
                                    pop = regionImmigration.get(destinationID);
                                    if (pop == null) {
                                        pop = new GENESIS_Population();
                                        regionImmigration.put(destinationID, pop);
                                    }
                                    ite = femaleAgeBounds.keySet().iterator();
                                    while (ite.hasNext()) {
                                        Integer field = ite.next();
                                        GENESIS_AgeBound ageBound = femaleAgeBounds.get(field);
                                        BigDecimal population = pop.getFemalePopulation(ageBound);
                                        population = population.add(new BigDecimal(data[field - 2]));
                                        pop._FemaleAgeBoundPopulationCount_TreeMap.put(ageBound, population);
                                    }
                                    ite = maleAgeBounds.keySet().iterator();
                                    while (ite.hasNext()) {
                                        Integer field = ite.next();
                                        GENESIS_AgeBound ageBound = maleAgeBounds.get(field);
                                        BigDecimal population = pop.getMalePopulation(ageBound);
                                        population = population.add(new BigDecimal(data[field - 2]));
                                        pop._MaleAgeBoundPopulationCount_TreeMap.put(ageBound, population);
                                    }
                                    pop.updateGenderedAgePopulation();
                                }
                                // regionInMigration
                                pop = regionInMigration.get(destinationID);
                                if (pop == null) {
                                    pop = new GENESIS_Population();
                                    regionInMigration.put(destinationID, pop);
                                    //pop.get
                                }
                                ite = femaleAgeBounds.keySet().iterator();
                                while (ite.hasNext()) {
                                    Integer field = ite.next();
                                    GENESIS_AgeBound ageBound = femaleAgeBounds.get(field);
                                    BigDecimal population = pop.getFemalePopulation(ageBound);
                                    population = population.add(new BigDecimal(data[field - 2]));
                                    pop._FemaleAgeBoundPopulationCount_TreeMap.put(ageBound, population);
                                }
                                ite = maleAgeBounds.keySet().iterator();
                                while (ite.hasNext()) {
                                    Integer field = ite.next();
                                    GENESIS_AgeBound ageBound = maleAgeBounds.get(field);
                                    BigDecimal population = pop.getMalePopulation(ageBound);
                                    population = population.add(new BigDecimal(data[field - 2]));
                                    pop._MaleAgeBoundPopulationCount_TreeMap.put(ageBound, population);
                                }
                                pop.updateGenderedAgePopulation();
                                // regionOutMigration
                                pop = regionOutMigration.get(originID);
                                if (pop == null) {
                                    pop = new GENESIS_Population();
                                    regionOutMigration.put(originID, pop);
                                }
                                ite = femaleAgeBounds.keySet().iterator();
                                while (ite.hasNext()) {
                                    Integer field = ite.next();
                                    GENESIS_AgeBound ageBound = femaleAgeBounds.get(field);
                                    BigDecimal population = pop.getFemalePopulation(ageBound);
                                    population = population.add(new BigDecimal(data[field - 2]));
                                    pop._FemaleAgeBoundPopulationCount_TreeMap.put(ageBound, population);
                                }
                                ite = maleAgeBounds.keySet().iterator();
                                while (ite.hasNext()) {
                                    Integer field = ite.next();
                                    GENESIS_AgeBound ageBound = maleAgeBounds.get(field);
                                    BigDecimal population = pop.getMalePopulation(ageBound);
                                    population = population.add(new BigDecimal(data[field - 2]));
                                    pop._MaleAgeBoundPopulationCount_TreeMap.put(ageBound, population);
                                }
                                pop.updateGenderedAgePopulation();
                            }
                        }
                        lineCounter++;
                        break;
                    case StreamTokenizer.TT_WORD:
                        line = aStreamTokenizer.sval;
                        break;
                }
                tokenType = aStreamTokenizer.nextToken();
            }
            aBufferedReader.close();
        } catch (IOException aIOException) {
            System.err.println(aIOException.getMessage() + " in "
                    + this.getClass().getName()
                    + "." + sourceMethod);
            System.exit(GENESIS_ErrorAndExceptionHandler.IOException);
        }
    }

    /**
     * Output Area migration data
     * 223059,95ZZ160008,223018,95ZZ100007,3,0,3,0,0,0,3,0,3,0,0,0 OriginZoneID,
     * OriginZoneCode, DestinationZoneID, DestinationZoneCode, 12 Fields: 1 =
     * Total all ages; 2 = Total male; 3 = Total female; 4 = Total age 0-15; 5 =
     * Male 0-15; 6 = Female 0-15; 7 = Total 16-PensionableAge; 8 = Male
     * 16-PensionableAge; 9 = Female 16-PensionableAge; 10 = Total
     * PensionableAge+; 11 = Male PensionableAge+; 12 = Female PensionableAge+
     *
     * @param directory
     * @param filename
     */
    public void readSubregionMigrationData(
            File directory,
            String filename) {
        String sourceMethod = "readSubregionMigrationData(File,String)";
        //getLogger().entering(sourceClass, sourceMethod);
        GENESIS_AgeBound ageBoundZeroToSixteen = new GENESIS_AgeBound(0L, 16L);
        GENESIS_AgeBound ageBoundSixteenToPension = new GENESIS_AgeBound(16L, 65L);
        GENESIS_AgeBound ageBoundPensionPlus = new GENESIS_AgeBound(65L, 200L);
        //ageBoundPensionPlus.setAgeMin(CommonFactory.newTime(65L));
        //ageBoundPensionPlus.setAgeMax(null);
        try {
            File file = new File(directory,
                    filename);
            if (!file.exists()) {
                int debug = 0;
            }
            BufferedReader aBufferedReader = Generic_StaticIO.getBufferedReader(file);
            StreamTokenizer aStreamTokenizer =
                    new StreamTokenizer(aBufferedReader);
            Generic_StaticIO.setStreamTokenizerSyntax1(aStreamTokenizer);
            int tokenType = aStreamTokenizer.nextToken();
            int lineCounter = 0;
            String line = null;
            while (tokenType != StreamTokenizer.TT_EOF) {
                switch (tokenType) {
                    case StreamTokenizer.TT_EOL:
                        //if (lineCounter % 100000 == 0) {
                        if (lineCounter % 1000 == 0) {
                            System.out.println(lineCounter + " " + line);
                        }
                        String[] fields = line.split(",");
                        String originSubregionID = fields[1];
                        String originRegionID = fields[1].substring(0, 4);
                        String destinationSubregionID = fields[3];
                        String destinationRegionID = fields[3].substring(0, 4);
                        GENESIS_Population population;
                        // subregionInMigration
                        TreeMap<String, GENESIS_Population> regionSubregionInMigration;
                        regionSubregionInMigration = subregionInMigration.get(destinationRegionID);
                        if (regionSubregionInMigration == null) {
                            regionSubregionInMigration = new TreeMap<String, GENESIS_Population>();
                            subregionInMigration.put(destinationRegionID, regionSubregionInMigration);
                        }
                        population = regionSubregionInMigration.get(destinationSubregionID);
                        if (population == null) {
                            population = new GENESIS_Population();
                            regionSubregionInMigration.put(destinationSubregionID, population);
                        }
                        addToPop(
                                population,
                                fields,
                                ageBoundZeroToSixteen,
                                ageBoundSixteenToPension,
                                ageBoundPensionPlus);
                        // subregionOutMigration
                        TreeMap<String, GENESIS_Population> regionSubregionOutMigration;
                        regionSubregionOutMigration = subregionOutMigration.get(originRegionID);
                        if (regionSubregionOutMigration == null) {
                            regionSubregionOutMigration = new TreeMap<String, GENESIS_Population>();
                            subregionOutMigration.put(originRegionID, regionSubregionOutMigration);
                        }
                        population = regionSubregionOutMigration.get(originSubregionID);
                        if (population == null) {
                            population = new GENESIS_Population();
                            regionSubregionOutMigration.put(originSubregionID, population);
                        }
                        addToPop(
                                population,
                                fields,
                                ageBoundZeroToSixteen,
                                ageBoundSixteenToPension,
                                ageBoundPensionPlus);

                        if (originRegionID.equalsIgnoreCase(destinationRegionID)) {
                            GENESIS_Population pop;
                            TreeMap<String, GENESIS_Population> pops;
                            // subregionInternalInMigration
                            TreeMap<String, GENESIS_Population> regionSubregionInternalInMigration;
                            regionSubregionInternalInMigration = subregionInternalInMigration.get(destinationRegionID);
                            if (regionSubregionInternalInMigration == null) {
                                regionSubregionInternalInMigration = new TreeMap<String, GENESIS_Population>();
                                subregionInternalInMigration.put(destinationRegionID, regionSubregionInternalInMigration);
                            }
                            population = regionSubregionInternalInMigration.get(destinationSubregionID);
                            if (population == null) {
                                population = new GENESIS_Population();
                                regionSubregionInternalInMigration.put(destinationSubregionID, population);
                            }
                            addToPop(
                                    population,
                                    fields,
                                    ageBoundZeroToSixteen,
                                    ageBoundSixteenToPension,
                                    ageBoundPensionPlus);
//                            
//                            
//                            pops = subregionInternalOutMigration.get(originRegionID);
//                            if (pops == null) {
//                                pops = new TreeMap<String, GENESIS_Population>();
//                                subregionInternalOutMigration.put(originRegionID, pops);
//                            }
//                            GENESIS_Population internalMig = new GENESIS_Population();
//                            internalMig._FemaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundZeroToSixteen, new BigDecimal(fields[9]));
//                            internalMig._MaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundZeroToSixteen, new BigDecimal(fields[8]));
//                            internalMig._FemaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundSixteenToPension, new BigDecimal(fields[12]));
//                            internalMig._MaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundSixteenToPension, new BigDecimal(fields[11]));
//                            internalMig._FemaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundPensionPlus, new BigDecimal(fields[15]));
//                            internalMig._MaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundPensionPlus, new BigDecimal(fields[14]));
//                            pop = pops.get(destinationRegionID);
//                            if (pop == null) {
//                                pops.put(destinationRegionID, internalMig);
//                            } else {
//                                pop.addPopulation(internalMig);
//                            }
                            // subregionInternalOutMigration
                            TreeMap<String, GENESIS_Population> regionSubregionInternalOutMigration;
                            regionSubregionInternalOutMigration = subregionInternalOutMigration.get(destinationRegionID);
                            if (regionSubregionInternalOutMigration == null) {
                                regionSubregionInternalOutMigration = new TreeMap<String, GENESIS_Population>();
                                subregionInternalOutMigration.put(destinationRegionID, regionSubregionInternalOutMigration);
                            }
                            population = regionSubregionInternalOutMigration.get(originSubregionID);
                            if (population == null) {
                                population = new GENESIS_Population();
                                regionSubregionInternalOutMigration.put(originSubregionID, population);
                            }
                            addToPop(
                                    population,
                                    fields,
                                    ageBoundZeroToSixteen,
                                    ageBoundSixteenToPension,
                                    ageBoundPensionPlus);
//                            pops = subregionInternalOutMigration.get(originRegionID);
//                            if (pops == null) {
//                                pops = new TreeMap<String, GENESIS_Population>();
//                                subregionInternalOutMigration.put(originRegionID, pops);
//                            }
//                            internalMig = new GENESIS_Population();
//                            internalMig._FemaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundZeroToSixteen, new BigDecimal(fields[9]));
//                            internalMig._MaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundZeroToSixteen, new BigDecimal(fields[8]));
//                            internalMig._FemaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundSixteenToPension, new BigDecimal(fields[12]));
//                            internalMig._MaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundSixteenToPension, new BigDecimal(fields[11]));
//                            internalMig._FemaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundPensionPlus, new BigDecimal(fields[15]));
//                            internalMig._MaleAgeBoundPopulationCount_TreeMap.put(
//                                    ageBoundPensionPlus, new BigDecimal(fields[14]));
//                            pop = pops.get(originRegionID);
//                            if (pop == null) {
//                                pops.put(originRegionID, internalMig);
//                            } else {
//                                pop.addPopulation(internalMig);
//                            }
////                            // In migration
////                            GENESIS_Population inMig = pops.get(destinationSubregionID);
////                            if (inMig == null) {
////                                inMig = new GENESIS_Population();
////                                pops.put(destinationSubregionID, inMig);
////                            }
////                            addToPop(
////                                    inMig,
////                                    fields,
////                                    ageBoundZeroToSixteen,
////                                    ageBoundSixteenToPension,
////                                    ageBoundPensionPlus);
                        }
                        lineCounter++;
                        break;
                    case StreamTokenizer.TT_WORD:
                        line = aStreamTokenizer.sval;
                        break;
                }
                tokenType = aStreamTokenizer.nextToken();
            }
            aBufferedReader.close();
        } catch (IOException aIOException) {
            System.err.println(aIOException.getMessage() + " in "
                    + this.getClass().getName()
                    + "." + sourceMethod);
            System.exit(GENESIS_ErrorAndExceptionHandler.IOException);
        }
    }

    protected void addToPop(
            GENESIS_Population population,
            String[] fields,
            GENESIS_AgeBound ageBoundZeroToSixteen,
            GENESIS_AgeBound ageBoundSixteenToPension,
            GENESIS_AgeBound ageBoundPensionPlus) {
        if (true) {
            BigDecimal pop;
            pop = population._FemaleAgeBoundPopulationCount_TreeMap.get(ageBoundZeroToSixteen);
            if (pop == null) {
                pop = BigDecimal.ZERO;
            }
            pop = pop.add(new BigDecimal(fields[9]));
            population._FemaleAgeBoundPopulationCount_TreeMap.put(ageBoundZeroToSixteen, pop);
        }
        if (true) {
            BigDecimal pop;
            pop = population._MaleAgeBoundPopulationCount_TreeMap.get(ageBoundZeroToSixteen);
            if (pop == null) {
                pop = BigDecimal.ZERO;
            }
            pop = pop.add(new BigDecimal(fields[8]));
            population._MaleAgeBoundPopulationCount_TreeMap.put(ageBoundZeroToSixteen, pop);
        }
        if (true) {
            BigDecimal pop;
            pop = population._FemaleAgeBoundPopulationCount_TreeMap.get(ageBoundSixteenToPension);
            if (pop == null) {
                pop = BigDecimal.ZERO;
            }
            pop = pop.add(new BigDecimal(fields[12]));
            population._FemaleAgeBoundPopulationCount_TreeMap.put(ageBoundSixteenToPension, pop);
        }
        if (true) {
            BigDecimal pop;
            pop = population._MaleAgeBoundPopulationCount_TreeMap.get(ageBoundSixteenToPension);
            if (pop == null) {
                pop = BigDecimal.ZERO;
            }
            pop = pop.add(new BigDecimal(fields[11]));
            population._MaleAgeBoundPopulationCount_TreeMap.put(ageBoundSixteenToPension, pop);
        }
        if (true) {
            BigDecimal pop;
            pop = population._FemaleAgeBoundPopulationCount_TreeMap.get(ageBoundPensionPlus);
            if (pop == null) {
                pop = BigDecimal.ZERO;
            }
            pop = pop.add(new BigDecimal(fields[15]));
            population._FemaleAgeBoundPopulationCount_TreeMap.put(ageBoundPensionPlus, pop);
        }
        if (true) {
            BigDecimal pop;
            pop = population._MaleAgeBoundPopulationCount_TreeMap.get(ageBoundPensionPlus);
            if (pop == null) {
                pop = BigDecimal.ZERO;
            }
            pop = pop.add(new BigDecimal(fields[14]));
            population._MaleAgeBoundPopulationCount_TreeMap.put(ageBoundPensionPlus, pop);
        }
    }

    /**
     * For converting counts to daily probabilities and rates. There are several
     * types of rates/probabilities.
     *
     * For each subregion there are: 1) out migration probabilities for
     * migration within all regions in the study region; 2) out migration
     * probabilities for migration to all other regions in the UK; 3) in
     * migration rates for regionImmigration; 4) in migration rates for
     * migration from other regions of the UK outwith the study region. For each
     * region there are: 1) out migration probabilities for migration within all
     * regions in the study and to all other regions in the UK grouped together;
     * 2) in migration rates from all other regions in the UK grouped together;
     * 3) regionImmigration rates.
     *
     * @param population
     * @param migrationFactor A number by which all inter regional migration
     * counts are multiplied before being added to migrationMinimum in
     * calculating migration destination probabilities.
     * @param migrationMinimum
     */
    public void processCounts(
            TreeMap<String, TreeMap<String, GENESIS_Population>> population,
            BigDecimal migrationFactor,
            BigDecimal migrationMinimum) {
        int decimalPlaces = 10;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        Set<String> studyRegionIDs = population.keySet();
        updateCountsForStudyRegions(studyRegionIDs, decimalPlaces, roundingMode);

        Iterator<String> ite;

        // Initialise regionInMigrationRates
        System.out.println("Initialise inMigrationRates");
        regionInMigrationRates = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        ite = studyRegionIDs.iterator();
        while (ite.hasNext()) {
            String destinationRegionID = ite.next();
            System.out.println("destinationRegionID " + destinationRegionID);
            TreeMap<String, GENESIS_Population> originRegionToRegionMigration;
            originRegionToRegionMigration = regionToRegionMigration.get(destinationRegionID);
            TreeMap<String, GENESIS_Population> originInMigrationRates;
            originInMigrationRates = new TreeMap<String, GENESIS_Population>();
            regionInMigrationRates.put(destinationRegionID, originInMigrationRates);
            Iterator<String> ite2;
            ite2 = originRegionToRegionMigration.keySet().iterator();
            while (ite2.hasNext()) {
                String originRegionID = ite2.next();
                GENESIS_Population counts = originRegionToRegionMigration.get(originRegionID);
                counts.updateGenderedAgePopulation();
                GENESIS_Population rates = new GENESIS_Population(counts);
                rates.divide(
                        new BigDecimal(GENESIS_Time.NormalDaysInYear_BigInteger),
                        decimalPlaces, roundingMode);
                originInMigrationRates.put(originRegionID, rates);
            }
        }

        // Initialise immigrationRates
        System.out.println("Initialise immigrationRates");
        immigrationRates = new TreeMap<String, GENESIS_Population>();
        ite = studyRegionIDs.iterator();
        while (ite.hasNext()) {
            String destinationRegionID = ite.next();
            System.out.println("destinationRegionID " + destinationRegionID);
            GENESIS_Population counts;
            counts = regionImmigration.get(destinationRegionID);
            counts.updateGenderedAgePopulation();
            GENESIS_Population rates = new GENESIS_Population(counts);
            rates.divide(
                    new BigDecimal(GENESIS_Time.NormalDaysInYear_BigInteger),
                    decimalPlaces, roundingMode);
            immigrationRates.put(destinationRegionID, rates);
        }

        // Initialise regionOutMigrationRates as daily probabilities
        System.out.println("Initialise outMigrationRates as daily probabilities");
        regionOutMigrationRates = new TreeMap<String, GENESIS_Population>();
        ite = studyRegionIDs.iterator();
        while (ite.hasNext()) {
            String originRegionID = ite.next();
            System.out.println("originRegionID " + originRegionID);
            GENESIS_Population regionPopulation;
            regionPopulation = population.get(originRegionID).get(originRegionID);
            GENESIS_Population counts;
            counts = regionOutMigration.get(originRegionID);
            counts.updateGenderedAgePopulation();
            GENESIS_Population rates = new GENESIS_Population(counts);
            rates.divideNoUpdate(
                    new BigDecimal(GENESIS_Time.NormalDaysInYear_BigInteger),
                    decimalPlaces, roundingMode);
            Iterator<GENESIS_AgeBound> ite2;
            System.out.println("Female");
            System.out.println("ageMinYear, ageMaxYear, "
                    + "pop, count, rate, dailyProbability");
            ite2 = rates._FemaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
            while (ite2.hasNext()) {
                GENESIS_AgeBound ageBound = ite2.next();
                BigDecimal count = counts._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                BigDecimal rate = rates._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                long ageMinYear = ageBound.getAgeMin().getYear();
                Time ageMax = ageBound.getAgeMax();
                long ageMaxYear;
                if (ageMax == null) {
                    ageMaxYear = regionPopulation._FemaleAgeBoundPopulationCount_TreeMap.lastKey().getAgeMaxBound().getYear();
                } else {
                    ageMaxYear = ageMax.getYear();
                }
                BigDecimal pop = regionPopulation.getFemalePopulationSum(ageMinYear, ageMaxYear);
                BigDecimal dailyRate = BigDecimal.ZERO;
                if (pop.compareTo(BigDecimal.ZERO) != 0) {
                    dailyRate = Generic_BigDecimal.divideRoundIfNecessary(
                            rate, pop, decimalPlaces, roundingMode);
                }
                System.out.println("" + ageMinYear + ", " + ageMaxYear + ", "
                        + pop + ", "
                        + count + ", "
                        + rate + ", "
                        + dailyRate);
                rates._FemaleAgeBoundPopulationCount_TreeMap.put(ageBound, dailyRate);
            }
            System.out.println("Male");
            System.out.println("ageMinYear, ageMaxYear, "
                    + "pop, count, rate, dailyProbability");
            ite2 = rates._MaleAgeBoundPopulationCount_TreeMap.keySet().iterator();
            while (ite2.hasNext()) {
                GENESIS_AgeBound ageBound = ite2.next();
                BigDecimal count = counts._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                BigDecimal rate = rates._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
                long ageMinYear = ageBound.getAgeMin().getYear();
                Time ageMax = ageBound.getAgeMax();
                long ageMaxYear;
                if (ageMax == null) {
                    ageMaxYear = regionPopulation._MaleAgeBoundPopulationCount_TreeMap.lastKey().getAgeMaxBound().getYear();
                } else {
                    ageMaxYear = ageMax.getYear();
                }
                BigDecimal pop = regionPopulation.getMalePopulationSum(ageMinYear, ageMaxYear);
                BigDecimal dailyRate = BigDecimal.ZERO;
                if (pop.compareTo(BigDecimal.ZERO) != 0) {
                    dailyRate = Generic_BigDecimal.divideRoundIfNecessary(
                            rate, pop, decimalPlaces, roundingMode);
                }
                System.out.println("" + ageMinYear + ", " + ageMaxYear + ", "
                        + pop + ", "
                        + count + ", "
                        + rate + ", "
                        + dailyRate);
                rates._MaleAgeBoundPopulationCount_TreeMap.put(ageBound, dailyRate);
            }
            rates.updateGenderedAgePopulation();
            regionOutMigrationRates.put(originRegionID, rates);
        }

        // Initialise cumulativeSumRescaledRegionOutMigration
        cumulativeSumRescaledRegionOutMigration = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        System.out.println("Initialise cumulativeSumRescaledRegionOutMigration");
        ite = studyRegionIDs.iterator();
        while (ite.hasNext()) {
            String originRegionID = ite.next();
            System.out.println("originRegionID " + originRegionID);
            TreeMap<String, GENESIS_Population> originCumulativeSumRescaledRegionOutMigration;
            originCumulativeSumRescaledRegionOutMigration = new TreeMap<String, GENESIS_Population>();
            cumulativeSumRescaledRegionOutMigration.put(originRegionID, originCumulativeSumRescaledRegionOutMigration);
            GENESIS_Population total = new GENESIS_Population(ge);
            Iterator<String> ite2;
            ite2 = regionToRegionMigration.keySet().iterator();
            while (ite2.hasNext()) {
                String destinationRegionID = ite2.next();
                TreeMap<String, GENESIS_Population> originRegionToRegionMigration;
                originRegionToRegionMigration = regionToRegionMigration.get(destinationRegionID);
                Iterator<String> ite3;
                ite3 = originRegionToRegionMigration.keySet().iterator();
                while (ite3.hasNext()) {
                    String originRegionID2 = ite3.next();
                    if (originRegionID2.equalsIgnoreCase(originRegionID)) {
                        GENESIS_Population pop = originRegionToRegionMigration.get(originRegionID2);
                        pop.updateGenderedAgePopulation();
                        pop.multiplyNoUpdate(migrationFactor);
                        pop.addNoUpdate(migrationMinimum);
                        total.addPopulationNoUpdate(pop);
                    }
                }
                total.updateGenderedAgePopulation();
                GENESIS_Population thisTotal = new GENESIS_Population(total);
                thisTotal.updateGenderedAgePopulation();
                originCumulativeSumRescaledRegionOutMigration.put(destinationRegionID, thisTotal);
            }
        }
        rescale(
                cumulativeSumRescaledRegionOutMigration,
                decimalPlaces,
                roundingMode);

        // Initialise cumulativeSumRescaledSubregionInMigration
        cumulativeSumRescaledSubregionInMigration = new TreeMap<String, TreeMap<String, GENESIS_Population>>();
        System.out.println("Initialise cumulativeSumRescaledSubregionInMigration");
        ite = studyRegionIDs.iterator();
        while (ite.hasNext()) {
            String destinationRegionID = ite.next();
            System.out.println("destinationRegionID " + destinationRegionID);
            TreeMap<String, GENESIS_Population> destinationCumulativeSumRescaledSubregionInMigration;
            destinationCumulativeSumRescaledSubregionInMigration = new TreeMap<String, GENESIS_Population>();
            cumulativeSumRescaledSubregionInMigration.put(
                    destinationRegionID,
                    destinationCumulativeSumRescaledSubregionInMigration);
            GENESIS_Population total = new GENESIS_Population(ge);
            Iterator<String> ite2;
            ite2 = subregionInternalInMigration.keySet().iterator();
            while (ite2.hasNext()) {
                String destinationRegionID2 = ite2.next();
                if (destinationRegionID2.equalsIgnoreCase(destinationRegionID)) {
                    TreeMap<String, GENESIS_Population> destinationSubregionInternalInMigration;
                    destinationSubregionInternalInMigration = subregionInternalInMigration.get(
                            destinationRegionID);
                    Iterator<String> ite3;
                    ite3 = destinationSubregionInternalInMigration.keySet().iterator();
                    while (ite3.hasNext()) {
                        String subregionDestinationID = ite3.next();
                        GENESIS_Population pop = destinationSubregionInternalInMigration.get(
                                subregionDestinationID);
                        pop.updateGenderedAgePopulation();
                        pop.multiplyNoUpdate(migrationFactor);
                        pop.addNoUpdate(migrationMinimum);
                        pop.addPopulationNoUpdate(total);
                        pop.updateGenderedAgePopulation();
                        destinationCumulativeSumRescaledSubregionInMigration.put(
                                subregionDestinationID, pop);
                        total = new GENESIS_Population(pop);
                        total.updateGenderedAgePopulation();
                    }
                }
            }
        }
        rescale(
                cumulativeSumRescaledSubregionInMigration,
                decimalPlaces,
                roundingMode);

//        String originRegionID = "OODA";
//        String destinationRegionID = "OODB";
//        writeRegionRateSummaries(
//                originRegionID,
//                destinationRegionID);
//        String originSubregionID = "00DBFA0001";
//        String destinationSubregionID = "00DBFA0002";
//        writeSubregionRateSummaries(
//                originRegionID,
//                destinationRegionID,
//                originSubregionID,
//                destinationSubregionID);
    }

    /**
     * Aggregates counts to sum all counts from the rest of the UK.
     *
     * @param regionIDs
     * @param population
     * @param decimalPlaces
     * @param rescale
     * @param roundingMode
     */
    public void updateCountsForStudyRegions(
            Set<String> regionIDs,
            int decimalPlaces,
            RoundingMode roundingMode) {
//        BigDecimal oneDiv365 = Generic_BigDecimal.divideRoundIfNecessary(
//                BigDecimal.ONE, 
//                BigDecimal.valueOf(365L),
//                decimalPlaces,
//                roundingMode);
        Iterator<String> ite;
        ite = regionIDs.iterator();
        while (ite.hasNext()) {
            String destinationRegionID = ite.next();
            GENESIS_Population pop = new GENESIS_Population(ge);
            TreeMap<String, GENESIS_Population> originRegionToRegionMigration;
            originRegionToRegionMigration = regionToRegionMigration.get(
                    destinationRegionID);
            Iterator<String> ite2;
            ite2 = originRegionToRegionMigration.keySet().iterator();
            while (ite2.hasNext()) {
                String originRegionID = ite2.next();
                if (!regionIDs.contains(originRegionID)) {
                    pop.addPopulation(originRegionToRegionMigration.get(originRegionID));
                }
            }
            originRegionToRegionMigration.put(RestOfUK_String, pop);
        }
//        ite = regionInMigration.keySet().iterator();
//        while (ite.hasNext()) {
//            String regionID = ite.next();
////            // Debug
////            System.out.println("regionID " + regionID);
//            if (!(regionID.equalsIgnoreCase(RestOfUK_String) || regionID.equalsIgnoreCase(RestOfTheWorld_String))) {
//                regionImmigration.get(regionID).divide(BigDecimal.valueOf(365L),
//                        decimalPlaces,
//                        roundingMode);
//            }
//            regionInMigration.get(regionID).divide(BigDecimal.valueOf(365L),
//                    decimalPlaces,
//                    roundingMode);
//        }
    }

    public void rescale(
            TreeMap<String, TreeMap<String, GENESIS_Population>> map,
            int decimalPlaces,
            RoundingMode roundingMode) {
        Iterator<String> ite = map.keySet().iterator();
        while (ite.hasNext()) {
            String regionID = ite.next();
            System.out.println("regionID " + regionID);
            TreeMap<String, GENESIS_Population> pops = map.get(regionID);
            GENESIS_Population maxPop = pops.lastEntry().getValue();
            Iterator<String> ite3 = pops.keySet().iterator();
            while (ite3.hasNext()) {
                String destinationRegionID = ite3.next();
                GENESIS_Population subregionPop;
                subregionPop = pops.get(destinationRegionID);
                subregionPop = subregionPop.divideByPopulation(maxPop, decimalPlaces, roundingMode);
                subregionPop.updateGenderedAgePopulation();
                pops.put(destinationRegionID, subregionPop);
            }
        }
    }

    /**
     * Untested
     *
     * @param female
     * @param originRegionID
     * @param random
     * @param decimalPlaces
     * @return
     *
     *
     */
    public String getOutMigrationRegionDestination(
            GENESIS_Female female,
            String originRegionID,
            int decimalPlaces,
            Random random) {
        try {
            String result = originRegionID;
            TreeMap<String, GENESIS_Population> destinationMap;
            destinationMap = cumulativeSumRescaledRegionOutMigration.get(originRegionID);
            BigDecimal value = Generic_BigDecimal.getRandom(ge._Generic_BigDecimal._Generic_BigInteger,
                    random,
                    decimalPlaces,
                    BigDecimal.ZERO,
                    BigDecimal.ONE);
            Long ageInYears = female.get_Age().getAgeInYears(ge._Time);
            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(ageInYears);
            Iterator<String> ite;
            ite = destinationMap.keySet().iterator();
            while (ite.hasNext()) {
                result = ite.next();
                GENESIS_Population popsToCheck = destinationMap.get(result);
                GENESIS_AgeBound containingAgeBound;
                containingAgeBound = GENESIS_AgeBound.getContainingAgeBound(
                        ageBound,
                        popsToCheck._FemaleAgeBoundPopulationCount_TreeMap);
                BigDecimal pop = popsToCheck._FemaleAgeBoundPopulationCount_TreeMap.get(containingAgeBound);
                if (pop.compareTo(value) != -1) {
                    break;
                }
            }

//        // debug
//        if (result == null) {
//            int debug = 1;
//        }

            return result;
        } catch (OutOfMemoryError e) {
            throw e;
        }
    }

    /**
     * For in migration to the region given by destinationRegionID, this method
     * returns a subregionID for the subregion that the female is assigned as
     * migrating to.
     *
     * @param female
     * @param destinationRegionID
     * @param decimalPlaces
     * @param random
     * @return
     */
    public String getInternalMigrationSubregionDestinationFromStudyRegion(
            GENESIS_Female female,
            String destinationRegionID,
            int decimalPlaces,
            Random random) {
        try {
            String result;
            Long ageInYears = female.get_Age().getAgeInYears(ge._Time);
            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(ageInYears);
            result = getInternalMigrationSubregionDestinationFromStudyRegionFemale(
                    ageBound,
                    destinationRegionID,
                    decimalPlaces,
                    random);
            return result;
        } catch (OutOfMemoryError e) {
            throw e;
        }
    }

    /**
     * For in migration to the region given by destinationRegionID, this method
     * returns a subregionID for the subregion that a female with an age in the
     * given ageBound is assigned as migrating to.
     *
     * @param ageBound
     * @param destinationRegionID
     * @param decimalPlaces
     * @param random
     * @return subregionDestinationID
     * @TODO Optimise: This is slow
     */
    public String getInternalMigrationSubregionDestinationFromStudyRegionFemale(
            GENESIS_AgeBound ageBound,
            String destinationRegionID,
            int decimalPlaces,
            Random random) {
        try {
            String result = null;
            TreeMap<String, GENESIS_Population> destinationMap;
            destinationMap = cumulativeSumRescaledSubregionInMigration.get(destinationRegionID);
            //destinationMap = cumulativeSumRescaledSubregionInMigration.get(destinationRegionID);
            //destinationMap = cumulativeSumRescaledRegionOutMigration.get(destinationRegionID);
            BigDecimal value = Generic_BigDecimal.getRandom(ge._Generic_BigDecimal._Generic_BigInteger,
                    random,
                    decimalPlaces,
                    BigDecimal.ZERO,
                    BigDecimal.ONE);
            Iterator<String> ite;
            ite = destinationMap.keySet().iterator();
            while (ite.hasNext()) {
                result = ite.next();
                GENESIS_Population popsToCheck = destinationMap.get(result);
                GENESIS_AgeBound containingAgeBound;
                containingAgeBound = GENESIS_AgeBound.getContainingAgeBound(
                        ageBound,
                        popsToCheck._FemaleAgeBoundPopulationCount_TreeMap);
                BigDecimal pop = popsToCheck._FemaleAgeBoundPopulationCount_TreeMap.get(containingAgeBound);
                if (pop.compareTo(value) != -1) {
                    break;
                }
            }

//        // debug
//        if (result == null) {
//            int debug = 1;
//        }

            return result;
        } catch (OutOfMemoryError e) {
            throw e;
        }
    }

    /**
     * For internal migration within the study region. The destination region is
     * given by destinationRegionID. This method returns a subregionID for the
     * assigned subregion that a female with an age in the given ageBound is
     * migrating to.
     *
     * @param male
     * @param destinationRegionID
     * @param decimalPlaces
     * @param random
     * @return subregionDestinationID
     * @TODO Optimise: This is slow
     */
    public String getInternalMigrationSubregionDestinationFromStudyRegion(
            GENESIS_Male male,
            String destinationRegionID,
            int decimalPlaces,
            Random random) {
        try {
            String result;
            Long ageInYears = male.get_Age().getAgeInYears(ge._Time);
            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(ageInYears);
            result = getInternalMigrationSubregionDestinationFromStudyRegionMale(
                    ageBound,
                    destinationRegionID,
                    decimalPlaces,
                    random);
            return result;
        } catch (OutOfMemoryError e) {
            throw e;
        }
    }

    /**
     * For internal migration within the study region. The destination region is
     * given by destinationRegionID. This method returns a subregionID for the
     * assigned subregion that a male with an age in the given ageBound is
     * migrating to.
     *
     * @param ageBound
     * @param destinationRegionID
     * @param decimalPlaces
     * @param random
     * @return
     */
    public String getInternalMigrationSubregionDestinationFromStudyRegionMale(
            GENESIS_AgeBound ageBound,
            String destinationRegionID,
            int decimalPlaces,
            Random random) {
        try {
            String result = null;
            TreeMap<String, GENESIS_Population> destinationMap;
            destinationMap = cumulativeSumRescaledSubregionInMigration.get(destinationRegionID);
            //destinationMap = cumulativeSumRescaledSubregionInMigration.get(destinationRegionID);
            //destinationMap = cumulativeSumRescaledRegionOutMigration.get(destinationRegionID);
            BigDecimal value = Generic_BigDecimal.getRandom(ge._Generic_BigDecimal._Generic_BigInteger,
                    random,
                    decimalPlaces,
                    BigDecimal.ZERO,
                    BigDecimal.ONE);
            Iterator<String> ite;
            ite = destinationMap.keySet().iterator();
            while (ite.hasNext()) {
                result = ite.next();
                GENESIS_Population popsToCheck = destinationMap.get(result);
                GENESIS_AgeBound containingAgeBound;
                containingAgeBound = GENESIS_AgeBound.getContainingAgeBound(
                        ageBound,
                        popsToCheck._MaleAgeBoundPopulationCount_TreeMap);
                BigDecimal pop = popsToCheck._MaleAgeBoundPopulationCount_TreeMap.get(containingAgeBound);
                if (pop.compareTo(value) != -1) {
                    break;
                }
            }

//        // debug
//        if (result == null) {
//            int debug = 1;
//        }

            return result;
        } catch (OutOfMemoryError e) {
            throw e;
        }
    }

    public String getInternalMigrationSubregionDestinationFromRestOfWorldFemale(
            GENESIS_AgeBound ageBound,
            String destinationRegionID,
            int decimalPlaces,
            Random random) {
        // Assume same distribution as Internal to UK Migration!
        return getInternalMigrationSubregionDestinationFromRestOfUKFemale(
                ageBound,
                destinationRegionID,
                decimalPlaces,
                random);
    }

    public String getInternalMigrationSubregionDestinationFromRestOfWorldMale(
            GENESIS_AgeBound ageBound,
            String destinationRegionID,
            int decimalPlaces,
            Random random) {
        // Assume same distribution as Internal to UK Migration!
        return getInternalMigrationSubregionDestinationFromRestOfUKMale(
                ageBound,
                destinationRegionID,
                decimalPlaces,
                random);
    }

    /**
     * For migration to a region in the study region from the rest of the UK.
     * The destination region is given by destinationRegionID. This method
     * returns a subregionID allocation which the person is assigned as
     * migrating to.
     *
     * @param ageBound
     * @param destinationRegionID
     * @param decimalPlaces
     * @param random
     * @return
     */
    public String getInternalMigrationSubregionDestinationFromRestOfUKFemale(
            GENESIS_AgeBound ageBound,
            String destinationRegionID,
            int decimalPlaces,
            Random random) {
        // Assume same distribution as Internal to Study Region Migration!
        return getInternalMigrationSubregionDestinationFromStudyRegionFemale(
                ageBound,
                destinationRegionID,
                decimalPlaces,
                random);
    }

    /**
     * For migration to a region in the study region from the rest of the UK.
     * The destination region is given by destinationRegionID. This method
     * returns a subregionID allocation which the person is assigned as
     * migrating to.
     *
     * @param ageBound
     * @param destinationRegionID
     * @param decimalPlaces
     * @param random
     * @return
     */
    public String getInternalMigrationSubregionDestinationFromRestOfUKMale(
            GENESIS_AgeBound ageBound,
            String destinationRegionID,
            int decimalPlaces,
            Random random) {
        // Assume same distribution as Internal to Study Region Migration!
        return getInternalMigrationSubregionDestinationFromStudyRegionMale(
                ageBound,
                destinationRegionID,
                decimalPlaces,
                random);
    }

    /**
     * Untested
     *
     * @param male
     * @param originRegionID
     * @param random
     * @param decimalPlaces
     * @return
     */
    public String getOutMigrationRegionDestination(
            GENESIS_Male male,
            String originRegionID,
            int decimalPlaces,
            Random random) {
        try {
            String result = originRegionID;
            TreeMap<String, GENESIS_Population> destinationMap;
            //destinationMap = cumulativeSumRescaledRegionOutMigration.get(originRegionID);
            destinationMap = getCumulativeSumRescaledRegionOutMigration(ge._HandleOutOfMemoryError_boolean).get(originRegionID);
            BigDecimal value = Generic_BigDecimal.getRandom(ge._Generic_BigDecimal._Generic_BigInteger,
                    random,
                    decimalPlaces,
                    BigDecimal.ZERO,
                    BigDecimal.ONE);
            Long ageInYears = male.get_Age().getAgeInYears(ge._Time);
            GENESIS_AgeBound ageBound = new GENESIS_AgeBound(ageInYears);
            Iterator<String> ite;
            ite = destinationMap.keySet().iterator();
            while (ite.hasNext()) {
                result = ite.next();
                GENESIS_Population popsToCheck = destinationMap.get(result);
                GENESIS_AgeBound containingAgeBound;
                containingAgeBound = GENESIS_AgeBound.getContainingAgeBound(
                        ageBound,
                        popsToCheck._MaleAgeBoundPopulationCount_TreeMap);
                BigDecimal pop = popsToCheck._MaleAgeBoundPopulationCount_TreeMap.get(containingAgeBound);
                if (pop.compareTo(value) != -1) {
                    break;
                }
            }
            return result;
        } catch (OutOfMemoryError e) {
            throw e;
        }
    }

    public BigDecimal getDailyOutMigrationProbability(
            GENESIS_Female female,
            GENESIS_AgeBound ageBound) {
        BigDecimal result;
        String regionID = female.getRegionID();
        //GENESIS_Population pop = regionOutMigrationRates.get(regionID);
        GENESIS_Population pop = getRegionOutMigrationRates(ge._HandleOutOfMemoryError_boolean).get(regionID);
        GENESIS_AgeBound containingAgeBound;
        containingAgeBound = GENESIS_AgeBound.getContainingAgeBound(
                ageBound,
                pop._FemaleAgeBoundPopulationCount_TreeMap);
        result = pop._FemaleAgeBoundPopulationCount_TreeMap.get(containingAgeBound);
//        result = pop._FemaleAgeBoundPopulationCount_TreeMap.get(ageBound);
        return result;
    }

    public BigDecimal getDailyOutMigrationProbability(
            GENESIS_Male male,
            GENESIS_AgeBound ageBound) {
        BigDecimal result;
        String regionID = male.getRegionID();
        //GENESIS_Population pop = regionOutMigrationRates.get(regionID);
        GENESIS_Population pop = getRegionOutMigrationRates(ge._HandleOutOfMemoryError_boolean).get(regionID);
        GENESIS_AgeBound containingAgeBound;
        containingAgeBound = GENESIS_AgeBound.getContainingAgeBound(
                ageBound,
                pop._MaleAgeBoundPopulationCount_TreeMap);
        result = pop._MaleAgeBoundPopulationCount_TreeMap.get(containingAgeBound);
        //result = pop._MaleAgeBoundPopulationCount_TreeMap.get(ageBound);
        return result;
    }

    public BigDecimal getDailyInternalMigrationRate(
            GENESIS_Female female,
            GENESIS_AgeBound ageBound,
            BigDecimal pop,
            int decimalPlaces,
            RoundingMode roundingMode) {
        BigDecimal result;
        String regionID = female.getRegionID();
        //GENESIS_Population rate = regionInMigrationRates.get(regionID).get(regionID);
        GENESIS_Population rate = getRegionInMigrationRates(ge._HandleOutOfMemoryError_boolean).get(regionID).get(regionID);
        GENESIS_AgeBound containingAgeBound;
        containingAgeBound = GENESIS_AgeBound.getContainingAgeBound(
                ageBound,
                rate._FemaleAgeBoundPopulationCount_TreeMap);
        result = rate._FemaleAgeBoundPopulationCount_TreeMap.get(containingAgeBound);
        Time ageMin = containingAgeBound.getAgeMin();
        Time ageMax = containingAgeBound.getAgeMax();
        long minYear = ageMin.getYear();
        long maxYear;
        if (ageMax == null) {
            maxYear = minYear + 10;
        } else {
            maxYear = ageMax.getYear();
        }
        BigInteger yearRange = BigInteger.valueOf(maxYear - minYear);
        result = Generic_BigDecimal.divideRoundIfNecessary(
                result, yearRange, decimalPlaces, roundingMode);
//        result = Generic_BigDecimal.divideRoundIfNecessary(
//                result,
//                pop,
//                decimalPlaces,
//                roundingMode);
//        if (result.compareTo(BigDecimal.ONE) == 1) {
//            result = BigDecimal.ONE;
//            int debug = 1;
//        }
        return result;
    }

    public BigDecimal getDailyInternalMigrationRate(
            GENESIS_Male male,
            GENESIS_AgeBound ageBound,
            BigDecimal pop,
            int decimalPlaces,
            RoundingMode roundingMode) {
        BigDecimal result;
        String regionID = male.getRegionID();
        //GENESIS_Population rate = regionInMigrationRates.get(regionID).get(regionID);
        GENESIS_Population rate = getRegionInMigrationRates(ge._HandleOutOfMemoryError_boolean).get(regionID).get(regionID);
        GENESIS_AgeBound containingAgeBound;
        containingAgeBound = GENESIS_AgeBound.getContainingAgeBound(
                ageBound,
                rate._MaleAgeBoundPopulationCount_TreeMap);
        result = rate._MaleAgeBoundPopulationCount_TreeMap.get(containingAgeBound);
        Time ageMin = containingAgeBound.getAgeMin();
        Time ageMax = containingAgeBound.getAgeMax();
        long minYear = ageMin.getYear();
        long maxYear;
        if (ageMax == null) {
            maxYear = minYear + 10;
        } else {
            maxYear = ageMax.getYear();
        }
        BigInteger yearRange = BigInteger.valueOf(maxYear - minYear);
        result = Generic_BigDecimal.divideRoundIfNecessary(
                result, yearRange, decimalPlaces, roundingMode);
//        result = Generic_BigDecimal.divideRoundIfNecessary(
//                result,
//                pop,
//                decimalPlaces,
//                roundingMode);
//        if (result.compareTo(BigDecimal.ONE) == 1) {
//            result = BigDecimal.ONE;
//            int debug = 1;
//        }
        return result;
    }

    public GENESIS_Population getDailyInMigrationRate(
            String regionID) {
        //return regionInMigrationRates.get(regionID).get(RestOfUK_String);
        return getRegionInMigrationRates(ge._HandleOutOfMemoryError_boolean).get(regionID).get(RestOfUK_String);
    }

    public GENESIS_Population getDailyImmigrationRate(
            String regionID) {
        //return immigrationRates.get(regionID);
        return getImmigrationRates(ge._HandleOutOfMemoryError_boolean).get(regionID);
    }

    public File getDirectory(boolean handleOutOfMemoryError) {
        File result;
        File archive = ge.get_Directory(handleOutOfMemoryError);
        long archiveRange = Generic_StaticIO.getArchiveRange(archive, "_");
        long highestLeaf = Generic_StaticIO.getArchiveHighestLeaf(archive, "_");
        File directory = new File(
                Generic_StaticIO.getObjectDirectory(
                archive,
                highestLeaf,
                archiveRange, //highestLeaf,
                archiveRange),
                "" + highestLeaf);
        File dataDirectory = new File(
                directory,
                "data");
        result = new File(
                dataDirectory,
                "migration");
        if (!result.exists()) {
            result.mkdirs();
        }
        return result;
    }
}
