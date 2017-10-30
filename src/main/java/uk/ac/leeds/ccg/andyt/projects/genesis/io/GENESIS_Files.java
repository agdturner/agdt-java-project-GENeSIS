/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.io;

import java.io.File;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_Files;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Strings;

/**
 *
 * @author geoagdt
 */
public class GENESIS_Files extends Generic_Files {

    
    protected File GridsDirectory;

    protected GENESIS_Files() {
    }

    public GENESIS_Files(File dataDir) {
        Strings = new GENESIS_Strings();
        DataDir = dataDir;
    }

    public GENESIS_Strings getStrings() {
        return (GENESIS_Strings) Strings;
    }

    public final File getGridsDirectory() {
        if (GridsDirectory == null) {
            GridsDirectory = new File(
                    getDataDir(),
                    getStrings().getString_Grids());
        }
        return GridsDirectory;
    }

}
