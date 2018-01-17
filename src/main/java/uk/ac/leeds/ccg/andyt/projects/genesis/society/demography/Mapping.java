/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Female;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Male;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Person;

/**
 *
 * @author geoagdt
 */
public class Mapping {

    public static void outputMap(
            Grids_AbstractGridNumber g,
            File file,
            String type,
            Grids_ImageExporter ie,
            GENESIS_Environment ge) {
        ie.toGreyScaleImage(g, ge.ge.getProcessor(), file, type);
    }

    /**
     * Map locations with living population.
     *
     * @param f
     * @param type
     * @param ie
     * @param ge
     * @param population
     */
    public static void mapPopulation(File f, String type,
            Grids_ImageExporter ie, GENESIS_Environment ge,
            Object[] population) {
        Grids_Files gf;
        gf = ge.ge.getFiles();
        File dir;
        dir = gf.createNewFile(gf.getGeneratedGridDoubleDir());
        Grids_GridDouble dMP; // DensityMapPopulation
        dMP = (Grids_GridDouble) ge.ReportingGridDoubleFactory.create(
                dir, ge.ReportingGridDouble);
        Iterator ite;
        HashSet females = (HashSet) population[0];
        GENESIS_Female female;
        ite = females.iterator();
        while (ite.hasNext()) {
            female = (GENESIS_Female) ite.next();
            if (female.TimeOfDeath == null) {
                dMP.setCell(dMP.getRow(female.Location.Y),
                        dMP.getCol(female.Location.X), 1.0d);
            }
        }
        HashSet males = (HashSet) population[1];
        GENESIS_Male male;
        ite = males.iterator();
        while (ite.hasNext()) {
            male = (GENESIS_Male) ite.next();
            if (male.TimeOfDeath == null) {
                dMP.setCell(dMP.getRow(male.Location.Y),
                        dMP.getCol(male.Location.X), 1.0d);
            }
        }
        ie.toGreyScaleImage(dMP, ge.ge.getProcessor(), f, type);

    }

    /**
     * Modifies _Population_Location_Composite_Map.
     *
     * @param pLCM Population location composite map.
     * @param females Population of alive females
     * @param males Population of alive males
     * @param hoome
     * @param compositionLatency
     */
    public static void _AddTo_Population_Movement_Composite_Map(
            Grids_GridDouble pLCM, HashSet females, HashSet males,
            double compositionLatency, boolean hoome) {
        // Divide values by _CompositionLatency
        long _NRows = pLCM.getNRows();
        long _NCols = pLCM.getNCols();
        long row;
        long col;
        double value;
        double newValue;
        double _NoDataValue = pLCM.getNoDataValue();
        for (row = 0; row < _NRows; row++) {
            for (col = 0; col < _NCols; col++) {
                value = pLCM.getCell(row, col);
                if (value != _NoDataValue && value != 0) {
                    newValue = value / compositionLatency;
                    pLCM.setCell(row, col, newValue);
                } else {
                    pLCM.setCell(row, col, 0);
                }
            }
        }
        Iterator ite;
        GENESIS_Person person;
        ite = females.iterator();
        while (ite.hasNext()) {
            person = (GENESIS_Person) ite.next();
            //if (!person.Location.equals(person.Location_Heading) && person.Location_Heading != null) {
            if (!person.Location.equals(person.getPreviousPoint2D())) {
                pLCM.addToCell(pLCM.getRow(person.Location.Y),
                        pLCM.getCol(person.Location.X), 1.0d);
            }
        }
        ite = males.iterator();
        while (ite.hasNext()) {
            person = (GENESIS_Person) ite.next();
            //if (!_Person._Location.equals(_Person._Location_Heading) && _Person.Location_Heading != null) {
            if (!person.Location.equals(person.getPreviousPoint2D())) {
                pLCM.addToCell(pLCM.getRow(person.Location.Y),
                        pLCM.getCol(person.Location.X), 1.0d);
//                pLCM.setCell(
//                        person.Location.Row, 
//                        person.Location.Col, 
//                        1.0d, _HandleOutOfMemoryError);
            }
        }
    }
//    /**
//     * Modifies _Population_Location_Composite_Map.
//     * @param _Population_Location_Composite_Map
//     */
//    @Deprecated
//    public static void _AddTo_Population_Location_Composite_Map(
//            Grid2DSquareCellDouble _Population_Location_Composite_Map,
//            Object[] _Population,
//            double _CompositionLatency,
//            boolean _HandleOutOfMemoryError) {
//        // Divide values by _CompositionLatency
//        long _NRows = _Population_Location_Composite_Map.getNRows(
//                _HandleOutOfMemoryError);
//        long _NCols = _Population_Location_Composite_Map.getNCols(
//                _HandleOutOfMemoryError);
//        long row;
//        long col;
//        double value;
//        double newValue;
//        double _NoDataValue = _Population_Location_Composite_Map.getNoDataValue(_HandleOutOfMemoryError);
//        for (row = 0; row < _NRows; row++) {
//            for (col = 0; col < _NCols; col++) {
//                value = _Population_Location_Composite_Map.getCell(
//                        row, col, _HandleOutOfMemoryError);
//                if (value != _NoDataValue && value != 0) {
//                    newValue = value / _CompositionLatency;
//                    _Population_Location_Composite_Map.setCell(
//                            row, col, newValue, _HandleOutOfMemoryError);
//                }
//            }
//        }
//        Iterator _Iterator;
//        HashSet _Females = (HashSet) _Population[0];
//        GENESIS_Female _Female;
//        _Iterator = _Females.iterator();
//        while (_Iterator.hasNext()) {
//            _Female = (GENESIS_Female) _Iterator.next();
//            if (_Female._Time_Death == null) {
//                _Population_Location_Composite_Map.addToCell(
//                        _Female._Location._Row,
//                        _Female._Location._Col,
//                        1.0d,
//                        _HandleOutOfMemoryError);
//            }
//        }
//        HashSet _Males = (HashSet) _Population[1];
//        GENESIS_Male _Male;
//        _Iterator = _Males.iterator();
//        while (_Iterator.hasNext()) {
//            _Male = (GENESIS_Male) _Iterator.next();
//            if (_Male._Time_Death == null) {
//                _Population_Location_Composite_Map.addToCell(
//                        _Male._Location._Row,
//                        _Male._Location._Col,
//                        1.0d,
//                        _HandleOutOfMemoryError);
//            }
//        }
//    }
}
