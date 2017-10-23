package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.Family;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations.Household;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

public class GENESIS_Male extends GENESIS_Person {

    /**
     * The main collection within which this is stored.
     */
    protected transient GENESIS_MaleCollection Males;

    private GENESIS_Male() {
    }

    protected GENESIS_Male(
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

    protected GENESIS_Male(
            GENESIS_Environment ge,
            GENESIS_AgentCollectionManager acm,
            GENESIS_Age age,
            Household household) {
        this(ge,
             acm,
             null,
             age,
            household,
             household._Point2D);
    }

    protected GENESIS_Male(
            GENESIS_Environment ge,
            GENESIS_AgentCollectionManager acm,
            GENESIS_Age age,
            Household household,
            Vector_Point2D point2D) {
        this(ge,
             acm,
             null,
             age,
             household,
             point2D);
    }

    protected GENESIS_Male(
            GENESIS_Environment ge,
            GENESIS_AgentCollectionManager acm,
            File directory,
            GENESIS_Age age,
            Household household,
            Vector_Point2D point2D) {
        super(ge);
        init(ge,
             acm,
             directory,
             age,
             household,
             point2D);
    }

    protected final void init(
            GENESIS_Environment ge,
            GENESIS_AgentCollectionManager acm,
            File directory,
            GENESIS_Age age,
            Household household,
            Vector_Point2D point2D) {
        LogManager.getLogManager().addLogger(Logger.getLogger(GENESIS_Log.GENESIS_DefaultLoggerName));
        ge = ge;
        AgentCollectionManager = acm;
        _ID = acm.get_NextMaleID(ge.HandleOutOfMemoryError);
        Type = getTypeLivingMale_String();
        _Collection_ID = acm.getMaleCollection_ID(_ID,
                Type,
                ge.HandleOutOfMemoryError);
        Generic_StaticIO.addToArchive(
                AgentCollectionManager.getLivingMaleDirectory(),
                AgentCollectionManager._MaximumNumberOfObjectsPerDirectory,
                _Collection_ID);
        this.Males = get_MaleCollection();
        this.Males.getAgentID_Agent_Map().put(_ID, this);
        this._Directory = directory;
        this._Age = new GENESIS_Age(age, ge);
        this._Family = new Family(this);
        if (_Household != null) {
            this._Household = household;
            this._Household._Add_Person(this);
        }
        if (_Point2D != null) {
            this._Point2D = point2D;
            this._Previous_Point2D = new Vector_Point2D(point2D);
        }
        this._ResidentialSubregionIDs = new ArrayList<>();
    }

    /**
     * @return A copy of this._Agent_ID
     */
    protected Long get_Male_ID() {
        return _ID;
    }

    @Override
    public Long getAgentID(boolean handleOutOfMemoryError) {
        try {
            Long result = get_Male_ID();
            GENESIS_MaleCollection a_MaleCollection =
                    get_MaleCollection();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_MaleCollection,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(
                        get_MaleCollection());
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
        return get_MaleCollection();
    }

    public GENESIS_MaleCollection get_MaleCollection(
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_MaleCollection result = get_MaleCollection();
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
                return get_MaleCollection(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected GENESIS_MaleCollection get_MaleCollection() {
        if (Males == null) {
            Males = getAgentCollectionManager().getMaleCollection(
                    _Collection_ID,
                    Type,
                    ge.HandleOutOfMemoryErrorFalse);
        }
        if (Males.ge == null) {
            Males.ge = ge;
        }
        return Males;
    }

    public Long get_MaleCollection_ID(boolean handleOutOfMemoryError) {
        try {
            Long result = get_MaleCollection_ID();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    get_MaleCollection(),
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(
                        get_MaleCollection());
                ge.initMemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                return get_MaleCollection_ID(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected Long get_MaleCollection_ID() {
        return getAgentCollectionManager().getAgentCollection_ID(get_Male_ID());
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
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(
                        get_MaleCollection());
                ge.initMemoryReserve(
                        ge.HandleOutOfMemoryErrorFalse);
                return get_Gender(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    @Override
    protected int get_Gender() {
        return 1;
    }

//    public static GENESIS_Male read(
//            long a_Agent_ID,
//            GENESIS_MaleCollection a_GENESIS_MaleCollection) {
//        GENESIS_Male result;
//        AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                a_GENESIS_MaleCollection.getAgentCollectionManager();
//        File a_MaleDirectory_File = Generic_StaticIO.getObjectDirectory(
//                a_GENESIS_AgentCollectionManager._Male_Directory,
//                a_Agent_ID,
//                a_GENESIS_AgentCollectionManager._IndexOfLastBornMale,
//                a_GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory);
//        File a_Male_File = new File(
//                a_MaleDirectory_File,
//                GENESIS_Male.class.getCanonicalName() + ".thisFile");
//        return (GENESIS_Male) Generic_StaticIO.readObject(a_Male_File);
//    }
    @Deprecated
    @Override
    public void write(boolean handleOutOfMemoryError) {
        try {
            write();
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAnyExcept(
                        get_MaleCollection());
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
    protected File getDirectory() {
        if (_Directory == null) {
            _Directory = AgentCollectionManager.getLivingMaleDirectory();
        }
        return _Directory;
    }
}
