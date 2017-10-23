/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.io;

import java.io.File;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Strings;

/**
 *
 * @author geoagdt
 */
public class GENESIS_Files {

    protected GENESIS_Strings gs;

    protected File Directory;
    protected File GridsDirectory;

    protected GENESIS_Files() {
    }

    public GENESIS_Files(File Directory) {
        this.gs = new GENESIS_Strings();
        this.Directory = Directory;
    }

    public final void setDirectory(File Directory) {
        this.Directory = Directory;
    }

    public final File getDirectory() {
        return Directory;
    }

    public final File getGridsDirectory() {
        if (GridsDirectory == null) {
            GridsDirectory = new File(
                    getDirectory(),
                    gs.getString_Grids());
        }
        return GridsDirectory;
    }

}
