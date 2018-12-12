/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.XMLConverter;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.metadata.MetadataType;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.parameters.ParametersType;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;

/**
 *
 * @author geoagdt
 */
public class GENESIS_DataHandler {

    public final static String UNDERSCORE = "_";

    public static File getResultPopulationFileForSimulationEndYear(
            File resultDirectory_File,
            String filenamePart) {
        File resultsDemographicsDirectory = getResultsDemographicsDirectory(
                resultDirectory_File);
        File totalResultsDemographicsDirectory = new File(Generic_IO.getArchiveHighestLeafFile(
                resultsDemographicsDirectory, UNDERSCORE).getParentFile(), "0/Total/");
        System.out.println("totalResultsDemographicsDirectory " + totalResultsDemographicsDirectory);
        MetadataType metadata = getMetadataType(
                resultDirectory_File);
        int endYear = metadata.getEndYear();
        File population_File = new File(
                totalResultsDemographicsDirectory,
                "Total_Population__" + filenamePart + "__Year_" + endYear + ".xml");
        return population_File;
    }

//    public static File getResultPopulationSimulatedLivingDaysFileForSimulationEndYear(
//            File resultDirectory_File) {
//        File resultsDemographicsDirectory = getResultsDemographicsDirectory(
//                resultDirectory_File);
//        File lastYearResultsDemographicsDirectory
//                = Generic_IO.getArchiveHighestLeafFile(
//                resultsDemographicsDirectory, UNDERSCORE);
//        MetadataType metadata = getMetadataType(
//                resultDirectory_File);
//        int endYear = metadata.getEndYear();
//        File population_File = new File(
//                lastYearResultsDemographicsDirectory,
//                "Population__Simulated_Living_Days__Year_" + endYear + ".xml");
//        return population_File;
//    }
//    
//    public static File getResultPopulationSimulatedLivingFileForSimulationEndYear(
//            File resultDirectory_File) {
//        File resultsDemographicsDirectory = getResultsDemographicsDirectory(
//                resultDirectory_File);
//        File lastYearResultsDemographicsDirectory
//                = Generic_IO.getArchiveHighestLeafFile(
//                resultsDemographicsDirectory, UNDERSCORE);
//        MetadataType metadata = getMetadataType(
//                resultDirectory_File);
//        int endYear = metadata.getEndYear();
//        File population_File = new File(
//                lastYearResultsDemographicsDirectory,
//                "Population__Simulated_Living__Year_" + endYear + ".xml");
//        return population_File;
//    }
//
//    public static File getResultPopulationSimulatedDeadFileForSimulationEndYear(
//            File resultDirectory_File) {
//        File resultsDemographicsDirectory = getResultsDemographicsDirectory(
//                resultDirectory_File);
//        File lastYearResultsDemographicsDirectory
//                = Generic_IO.getArchiveHighestLeafFile(
//                resultsDemographicsDirectory, UNDERSCORE);
//        MetadataType metadata = getMetadataType(
//                resultDirectory_File);
//        int endYear = metadata.getEndYear();
//        File population_File = new File(
//                lastYearResultsDemographicsDirectory,
//                "Population__Simulated_Dead__Year_" + endYear + ".xml");
//        return population_File;
//    }
    public static File getResultsDataDirectory(
            File resultDirectory_File) {
        return new File(resultDirectory_File,
                "data");
    }

    public static File getResultsDemographicsDirectory(
            File resultDirectory_File) {
        return new File(getResultsDataDirectory(resultDirectory_File),
                "Demographics");
    }

    public static File getMetadataDirectory(
            File resultDirectory_File) {
        return new File(
                resultDirectory_File.toString(),
                "metadata");
    }

    public static File getParametersFile(
            File resultDirectory_File) {
        return new File(
                getMetadataDirectory(resultDirectory_File),
                "parameters.xml");
    }

    public static ParametersType getParametersType(
            File resultsDirectory_File) {
        File parameters_File = getParametersFile(resultsDirectory_File);
        if (!Generic_IO.fileExistsAndCanBeRead(parameters_File)) {
            String message = parameters_File.toString()
                    + " cannot be read";
            log(Level.ALL, message);
            System.err.println(message);
            System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        }
        ParametersType parametersType = XMLConverter.loadParametersFromXMLFile(
                parameters_File);
        return parametersType;
    }

    public static File getMetadataFile(
            File resultDirectory_File) {
        return new File(
                getMetadataDirectory(resultDirectory_File),
                "metadata.xml");
    }

    public static MetadataType getMetadataType(
            File resultsDirectory_File) {
        File metadata_File = getMetadataFile(resultsDirectory_File);
        if (!Generic_IO.fileExistsAndCanBeRead(metadata_File)) {
            String message = metadata_File.toString() + " cannot be read";
            log(Level.ALL, message);
            System.err.println(message);
            System.exit(GENESIS_ErrorAndExceptionHandler.ArgsErrorExitStatus);
        }
        MetadataType metadataType = XMLConverter.loadMetadataFromXMLFile(
                metadata_File);
        return metadataType;
    }

    protected static void log(Level level, String message) {
        Logger logger = getLogger();
        if (logger != null) {
            getLogger().log(level, message);
        }
    }

    public static Logger getLogger() {
        return GENESIS_Log.LOGGER;
    }
}
