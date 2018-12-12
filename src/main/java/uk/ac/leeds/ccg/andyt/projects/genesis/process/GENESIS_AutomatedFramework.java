/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;

/**
 *
 * @author geoagdt
 */
public class GENESIS_AutomatedFramework {

    /**
     * Used for Logging
     */
    private static final String sourceClass = GENESIS_CompareModelDemographic.class.getName();
    private static final String sourcePackage = GENESIS_CompareModelDemographic.class.getPackage().getName();
    //private static final Logger logger = Logger.getLogger(sourcePackage);
    private int _Year;
    private HashSet<Future> futures;
    //private ExecutorService executorService;
    private File directory;

    //private PrintWriter comparisonDetail_PrintWriter;
    public GENESIS_AutomatedFramework(File directory) {
        this.directory = directory;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println(System.getProperties().keySet().toString());
        System.out.println(System.getProperties().values().toString());
        //System.out.println(System.getProperty("java.util.logging.config.file").trim());
        File directory = new File(args[0]);
        File logDirectory = new File(
                directory,
                GENESIS_Log.NAME);
        //String logname = sourcePackage;
        String logname = "uk.ac.leeds.ccg.andyt.projects.genesis";
        GENESIS_Log.parseLoggingProperties(
                directory,
                logDirectory,
                logname);
        String fileSeparator = System.getProperty("file.separator");
        String underscore = "_";
        GENESIS_Log.reset();

        GENESIS_AutomatedFramework instance = new GENESIS_AutomatedFramework(directory);
        //instance.test();
        instance.run();

//        Generic_Execution.shutdownExecutorService(
//                instance.executorService, instance.futures, instance);

    }

    public void run() {
        long initalRandomSeed = 0;
        long seedIncrement = 1000;
        long startYear = 2001;
        long years = 7;
        //String workspaceDirectoryString = "/scratch01/Work/Projects/GENESIS/workspace/";
        //File workspaceDirectory = new File(workspaceDirectoryString);
        File simulationDirectory = new File(
                directory,
                "GENESIS_DemographicModel");
        Long simulationHighestLeaf = Generic_IO.getArchiveHighestLeaf(simulationDirectory, "_");
        long simulationRunID = simulationHighestLeaf + 1L;
        long simulationRunID0 = simulationRunID;

        File comparisonDirectory = new File(
                directory,
                "Compare_GENESIS_DemographicModel");
        Long comparisonHighestLeaf = Generic_IO.getArchiveHighestLeaf(comparisonDirectory, "_");
        long comparisonRunID = comparisonHighestLeaf + 1L;

        long seed;
        long numberOfRunsPerYear = 5;
        String[] simulationArgs;
        String[] comparisonArgs = new String[4];
        //StartNameFileFilter filter = new StartNameFileFilter("0");

        String comparisonString = "";
        for (long year = startYear; year < (startYear + years); year++) {

            // Comment this block if not restarting
            long yearToRestart = 2001; //2004;
            long runIndexToRestart = 0; //numberOfRunsPerYear;
            if (year < yearToRestart) {
                // Skip to right year for continuing a run
                System.out.println("Skipping year " + year + " simulation " + simulationRunID);
                simulationRunID0 = simulationRunID - numberOfRunsPerYear;
            } else {

                if (year == startYear && runIndexToRestart == 0) {
                    simulationArgs = new String[2];
                    simulationArgs[1] = directory.toString();
                    for (long l = 0; l < numberOfRunsPerYear; l++) {
                        seed = initalRandomSeed + (l * seedIncrement);
                        System.out.println("Simulation with random seed " + seed);
                        simulationArgs[0] = "" + seed;
                        GENESIS_ModelDemographic.main(simulationArgs);
                        simulationRunID++;
                    }
                } else {
//            if (year > 2001) { // skip
                    System.out.println("Year " + year + " start");
                    if (year == yearToRestart) {
                        comparisonString = getComparisonString(comparisonDirectory);
                        if (year == startYear) {
                            simulationArgs = new String[2];
                            simulationArgs[1] = directory.toString();
                        } else {
                            simulationArgs = new String[4];
                            simulationArgs[1] = directory.toString();
                            simulationArgs[2] = "" + year;
                            simulationArgs[3] = comparisonString;
                        }
                        for (long l = runIndexToRestart; l < numberOfRunsPerYear; l++) {
                            seed = initalRandomSeed + (l * seedIncrement);
                            System.out.println("Simulation with random seed " + seed);
                            simulationArgs[0] = "" + seed;
                            GENESIS_ModelDemographic.main(simulationArgs);
                            simulationRunID++;
                        }
                    } else {


                        if (year == startYear) {
                            simulationArgs = new String[2];
                            simulationArgs[1] = directory.toString();
                        } else {
                            simulationArgs = new String[4];
                            simulationArgs[1] = directory.toString();
                            simulationArgs[2] = "" + year;
                            simulationArgs[3] = comparisonString;
                        }
                        for (long l = 0; l < numberOfRunsPerYear; l++) {
                            seed = initalRandomSeed + (l * seedIncrement);
                            System.out.println("Simulation with random seed " + seed);
                            simulationArgs[0] = "" + seed;
                            GENESIS_ModelDemographic.main(simulationArgs);
                            simulationRunID++;
                        }
                    }
                }

                comparisonArgs[0] = directory.toString();
                comparisonArgs[1] = directory.toString();
                comparisonArgs[2] = "" + simulationRunID0;
                comparisonArgs[3] = "" + simulationRunID;
                System.out.println("Comparing runs " + simulationRunID0 + " to " + simulationRunID);
                GENESIS_CompareModelDemographic.main(comparisonArgs);
                comparisonString = getComparisonString(comparisonDirectory);
                comparisonRunID++;
                System.out.println("Year " + year + " end");
            }
        }
    }

    public String getComparisonString(File comparisonDirectory) {
        File comparisonTopLevelArchivedir = comparisonDirectory.listFiles()[0];
        Long highestLeaf = Generic_IO.getArchiveHighestLeaf(comparisonTopLevelArchivedir, "_");
        Long range = Generic_IO.getArchiveRange(comparisonTopLevelArchivedir, "_");
        File comparisonDir = Generic_IO.getObjectDirectory(
                comparisonDirectory, highestLeaf, highestLeaf, range);
        comparisonDir = new File(
                comparisonDir,
                "" + highestLeaf);
        File comparisonResultFile = new File(
                comparisonDir,
                "comparison.out");
        return readComparisonOutputFile(comparisonResultFile);
    }

    public String readComparisonOutputFile(File f) {
        String result = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(f)));


        } catch (FileNotFoundException ex) {
            Logger.getLogger(GENESIS_AutomatedFramework.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        StreamTokenizer st = new StreamTokenizer(br);
        Generic_IO.setStreamTokenizerSyntax1(st);
        try {
            result = br.readLine();


        } catch (IOException ex) {
            Logger.getLogger(GENESIS_AutomatedFramework.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        try {
            br.close();


        } catch (IOException ex) {
            Logger.getLogger(GENESIS_AutomatedFramework.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public void test() {
        long initalRandomSeed = 0;
        long seedIncrement = 1000;
        long startYear = 1991;
        long years = 10;
        String workspaceDirectoryString = "/scratch01/Work/Projects/GENESIS/workspace/";
        File workspaceDirectory = new File(workspaceDirectoryString);
        File comparisonDirectory = new File(
                workspaceDirectory,
                "Compare_DemographicModel_Aspatial");
        long comparisonRunID = 0;
        long simulationRunID0 = 0;
        long simulationRunID = 0;
        long seed;
        long numberOfRunsPerYear = 10;
        String[] simulationArgs;
        String[] comparisonArgs = new String[4];
        //StartNameFileFilter filter = new StartNameFileFilter("0");

        String comparisonString = "";
        for (long year = startYear; year < startYear + years; year++) {
            if (year == startYear) {
                simulationArgs = new String[2];
                simulationArgs[1] = workspaceDirectoryString;
            } else {
                simulationArgs = new String[4];
                simulationArgs[1] = workspaceDirectoryString;
                simulationArgs[2] = "" + year;
                simulationArgs[3] = comparisonString;
            }
            for (long l = 0; l < numberOfRunsPerYear; l++) {
                seed = initalRandomSeed + (l * seedIncrement);
                System.out.println("Simulation with random seed " + seed);
                simulationArgs[0] = "" + seed;
                //simulation = new GENESIS_ModelDemographic();
                //GENESIS_DemographicModel.main(simulationArgs);
                simulationRunID++;
            }
            comparisonArgs[0] = workspaceDirectoryString;
            comparisonArgs[1] = workspaceDirectoryString;
            comparisonArgs[2] = "" + simulationRunID0;
            comparisonArgs[3] = "" + simulationRunID;
            //Compare_DemographicModel_Aspatial.main(comparisonArgs);
            comparisonString = getComparisonString(comparisonDirectory);
            comparisonRunID++;
        }

    }
}
