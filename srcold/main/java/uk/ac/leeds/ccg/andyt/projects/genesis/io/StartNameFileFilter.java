/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.io;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author geoagdt
 */
public class StartNameFileFilter implements FileFilter {

    private String string;

    public StartNameFileFilter(String string) {
        this.string = string;
    }

    @Override
    public boolean accept(File pathname) {
        if (pathname.getName().startsWith(string)) {
            return true;
        }
        return false;
    }
}
