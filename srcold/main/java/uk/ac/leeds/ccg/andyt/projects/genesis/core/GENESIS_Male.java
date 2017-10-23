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

    protected transient GENESIS_MaleCollection _GENESIS_MaleCollection;

    private GENESIS_Male() {
    }

    protected GENESIS_Male(
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

    protected GENESIS_Male(
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

    protected GENESIS_Male(
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

    protected GENESIS_Male(
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
        _ID = a_GENESIS_AgentCollectionManager.get_NextMaleID(
                ge._HandleOutOfMemoryError_boolean);
        _Type = getTypeLivingMale_String();
        _Collection_ID = a_GENESIS_AgentCollectionManager.getMaleCollection_ID(
                _ID,
                _Type,
                ge._HandleOutOfMemoryError_boolean);
        Generic_StaticIO.addToArchive(
                _GENESIS_AgentCollectionManager.getLivingMaleDirectory(),
                _GENESIS_AgentCollectionManager._MaximumNumberOfObjectsPerDirectory,
                _Collection_ID);
        this._GENESIS_MaleCollection = get_MaleCollection();
        this._GENESIS_MaleCollection.get_Agent_ID_Agent_HashMap().put(_ID, this);
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
        this._ResidentialSubregionIDs = new ArrayList<String>();
    }

    /**
     * @return A copy of this._Agent_ID
     */
    protected Long get_Male_ID() {
        return _ID;
    }

    @Override
    public Long get_Agent_ID(boolean handleOutOfMemoryError) {
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
                ge.swapToFile_DataAnyExcept(
                        get_MaleCollection());
                ge.initMemoryReserve(
                        handleOutOfMemoryError);
                return get_Agent_ID(handleOutOfMemoryError);
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
                if (ge._GENESIS_AgentEnvironment._AgentCollectionManager.swapToFile_AgentCollection_Account() < 1) {
                    ge.swapToFile_Grid2DSquareCellChunk();
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
        if (_GENESIS_MaleCollection == null) {
            _GENESIS_MaleCollection = get_AgentCollectionManager().getMaleCollection(
                    _Collection_ID,
                    _Type,
                    ge.HandleOutOfMemoryErrorFalse);
        }
        if (_GENESIS_MaleCollection.ge == null) {
            _GENESIS_MaleCollection.ge = ge;
        }
        return _GENESIS_MaleCollection;
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
                ge.swapToFile_DataAnyExcept(
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
        return get_AgentCollectionManager().getAgentCollection_ID(get_Male_ID());
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
                ge.swapToFile_DataAnyExcept(
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
//        GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager =
//                a_GENESIS_MaleCollection.get_AgentCollectionManager();
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
                ge.swapToFile_DataAnyExcept(
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
    protected File get_Directory() {
        if (_Directory == null) {
            _Directory = _GENESIS_AgentCollectionManager.getLivingMaleDirectory();
        }
        return _Directory;
    }
}
