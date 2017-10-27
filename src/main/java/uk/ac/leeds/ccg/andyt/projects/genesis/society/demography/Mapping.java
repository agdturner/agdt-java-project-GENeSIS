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
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Female;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Male;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Person;

/**
 *
 * @author geoagdt
 */
public class Mapping {

    public static void _OutputMap(
            Grids_AbstractGridNumber _Grid2DSquareCell,
            File _File,
            String _Type,
            Grids_ImageExporter _ImageExporter,
            GENESIS_Environment _Environment) {
        _ImageExporter.toGreyScaleImage(_Grid2DSquareCell,
                _Environment.ge.getGridProcessor(),
                _File,
                _Type,
                _Environment.HandleOutOfMemoryError);
    }

    /**
     * Map locations with living population.
     *
     * @param _File
     * @param _Type
     * @param _ImageExporter
     * @param _Environment
     * @param _Population
     */
    public static void _Map_Population(
            File _File,
            String _Type,
            Grids_ImageExporter _ImageExporter,
            GENESIS_Environment _Environment,
            Object[] _Population) {
        boolean _HandleOutOfMemoryError = _Environment.HandleOutOfMemoryError;
        Grids_GridDouble _Denisty_Map_Population = (Grids_GridDouble) _Environment._reporting_Grid2DSquareCellDoubleFactory.create(
                _Environment._reporting_Grid2DSquareCellDouble);
        //long _NRows = _Denisty_Map_Population.getNRows(_HandleOutOfMemoryError);
        //long _NCols = _Denisty_Map_Population.getNCols(_HandleOutOfMemoryError);
        //long row;
        //long col;
        Iterator _Iterator;
        HashSet _Females = (HashSet) _Population[0];
        GENESIS_Female _Female;
        _Iterator = _Females.iterator();
        while (_Iterator.hasNext()) {
            _Female = (GENESIS_Female) _Iterator.next();
            if (_Female.TimeOfDeath == null) {
                _Denisty_Map_Population.setCell(
                        _Denisty_Map_Population.getCellRowIndex(_Female.Location.Y, _HandleOutOfMemoryError),
                        _Denisty_Map_Population.getCellColIndex(_Female.Location.X, _HandleOutOfMemoryError),
                        1.0d,
                        _HandleOutOfMemoryError);
            }
        }
        HashSet _Males = (HashSet) _Population[1];
        GENESIS_Male _Male;
        _Iterator = _Males.iterator();
        while (_Iterator.hasNext()) {
            _Male = (GENESIS_Male) _Iterator.next();
            if (_Male.TimeOfDeath == null) {
                _Denisty_Map_Population.setCell(
                        _Denisty_Map_Population.getCellRowIndex(_Male.Location.Y, _HandleOutOfMemoryError),
                        _Denisty_Map_Population.getCellColIndex(_Male.Location.X, _HandleOutOfMemoryError),
                        1.0d,
                        _HandleOutOfMemoryError);
            }
        }
        _ImageExporter.toGreyScaleImage(
                _Denisty_Map_Population,
                _Environment.ge.getGridProcessor(),
                _File, _Type, _HandleOutOfMemoryError);

    }

    /**
     * Modifies _Population_Location_Composite_Map.
     *
     * @param _Population_Location_Composite_Map
     * @param _Population_Alive_Female
     * @param _HandleOutOfMemoryError
     * @param _Population_Alive_Male
     * @param _CompositionLatency
     */
    public static void _AddTo_Population_Movement_Composite_Map(
            Grids_GridDouble _Population_Location_Composite_Map,
            HashSet _Population_Alive_Female,
            HashSet _Population_Alive_Male,
            double _CompositionLatency,
            boolean _HandleOutOfMemoryError) {
        // Divide values by _CompositionLatency
        long _NRows = _Population_Location_Composite_Map.getNRows(
                _HandleOutOfMemoryError);
        long _NCols = _Population_Location_Composite_Map.getNCols(
                _HandleOutOfMemoryError);
        long row;
        long col;
        double value;
        double newValue;
        double _NoDataValue = _Population_Location_Composite_Map.getNoDataValue(_HandleOutOfMemoryError);
        for (row = 0; row < _NRows; row++) {
            for (col = 0; col < _NCols; col++) {
                value = _Population_Location_Composite_Map.getCell(
                        row, col, _HandleOutOfMemoryError);
                if (value != _NoDataValue && value != 0) {
                    newValue = value / _CompositionLatency;
                    _Population_Location_Composite_Map.setCell(
                            row, col, newValue, _HandleOutOfMemoryError);
                } else {
                    _Population_Location_Composite_Map.setCell(
                            row, col, 0, _HandleOutOfMemoryError);
                }
            }
        }
        Iterator _Iterator;
        GENESIS_Person _Person;
        _Iterator = _Population_Alive_Female.iterator();
        while (_Iterator.hasNext()) {
            _Person = (GENESIS_Person) _Iterator.next();
            //if (!_Person._Location.equals(_Person._Location_Heading) && _Person._Location_Heading != null) {
            if (!_Person.Location.equals(_Person.getPreviousPoint2D())) {
                _Population_Location_Composite_Map.addToCell(
                        _Population_Location_Composite_Map.getCellRowIndex(
                        _Person.Location.Y, _HandleOutOfMemoryError),
                        _Population_Location_Composite_Map.getCellColIndex(
                        _Person.Location.X, _HandleOutOfMemoryError),
                        1.0d,
                        _HandleOutOfMemoryError);
            }
        }
        _Iterator = _Population_Alive_Male.iterator();
        while (_Iterator.hasNext()) {
            _Person = (GENESIS_Person) _Iterator.next();
            //if (!_Person._Location.equals(_Person._Location_Heading) && _Person._Location_Heading != null) {
            if (!_Person.Location.equals(_Person.getPreviousPoint2D())) {
                _Population_Location_Composite_Map.addToCell(
                        _Population_Location_Composite_Map.getCellRowIndex(
                        _Person.Location.Y, _HandleOutOfMemoryError),
                        _Population_Location_Composite_Map.getCellColIndex(
                        _Person.Location.X, _HandleOutOfMemoryError),
                        1.0d,
                        _HandleOutOfMemoryError);
//                _Population_Location_Composite_Map.setCell(
//                        _Person._Location._Row, 
//                        _Person._Location._Col, 
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
