/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.process;

import java.io.File;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.bind.JAXBException;
import uk.ac.leeds.ccg.andyt.generic.utilities.Generic_Execution;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.MiscarriageFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.PopulationFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.miscarriage.MiscarriageType;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.population.PopulationType;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Miscarriage;
import uk.ac.leeds.ccg.andyt.projects.genesis.society.demography.GENESIS_Population;
import uk.ac.leeds.ccg.andyt.projects.genesis.visualisation.GENESIS_AgeGenderBarChart;

/**
 *
 * @author geoagdt
 */
public class IOTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new IOTest().run();
    }

    public void run() {
        File Directory;
        Directory = new File("/scratch01/Work/Projects/GENESIS/");
        GENESIS_Environment aGENESIS_Environment = new GENESIS_Environment(Directory);

        File miscariageXML = new File(Directory,
                "/workspace/DemographicModel_Aspatial_1/0_99/6/data/Demographics/0_99/0/Total/Total_Clinical_Miscarriage_1992.xml");
//        File miscariageXML = new File("C://Work/Projects/GENESIS/workspace/DemographicModel_Aspatial_1/0_99/27/data/Demographics/0_99/0/Total/Total_Clinical_Miscarriage_1992.xml");
        File miscariageOutputFile = new File(
                Directory,
                "/workspace/DemographicModel_Aspatial_1/0_99/6/data/Demographics/0_99/0/Total/Total_Clinical_Miscarriage_1992.PNG");
//        File miscariageOutputFile = new File("C://Work/Projects/GENESIS/workspace/DemographicModel_Aspatial_1/0_99/27/data/Demographics/0_99/0/Total/Total_Clinical_Miscarriage_1992.PNG");
//        MiscarriageFactory.init();
//        MiscarriageType miscarriageType = null;
//        try {
//            miscarriageType = MiscarriageFactory.read(miscariageXML);
//        } catch (JAXBException e) {
//            e.printStackTrace();            
//        }
//        GENESIS_Miscarriage theGENESIS_Miscarriage = new GENESIS_Miscarriage(
//                aGENESIS_Environment,
//                miscarriageType);

        PopulationFactory.init();
        PopulationType populationType = null;
        try {
            populationType = PopulationFactory.read(miscariageXML);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        GENESIS_Population theGENESIS_Population = new GENESIS_Population(
                aGENESIS_Environment,
                populationType);
        String format = "PNG";
        File resultsDataDirectory = null;
        String title = "title";
        int dataWidth = 200;
        int dataHeight = 300;
        String xAxisLabel = "x";
        String yAxisLabel = "y";
        boolean drawOriginLinesOnGrid = false;
        Integer startAgeOfEndYearInterval = null;
        int decimalPlacePrecisionForCalculations = 10;
        int decimalPlacePrecisionForDisplay = 10;
        RoundingMode roundingMode = RoundingMode.HALF_UP;

//        GENESIS_Population pop = new GENESIS_Population();
//        pop._FemaleAgeBoundPopulationCount_TreeMap = theGENESIS_Miscarriage._DailyClinicalMiscarriageAgeBoundProbability_TreeMap;

        executorService = getExecutorService();

        GENESIS_AgeGenderBarChart chart = new GENESIS_AgeGenderBarChart(
                executorService,
                miscariageOutputFile,
                format,
                resultsDataDirectory,
                title,
                dataWidth,
                dataHeight,
                xAxisLabel,
                yAxisLabel,
                drawOriginLinesOnGrid,
                startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode,
                aGENESIS_Environment);
        chart.setData(theGENESIS_Population, 15L, 44L);
        chart.run();
        Generic_Execution.shutdownExecutorService(executorService, chart.future, chart);
    }
    protected transient ExecutorService executorService;

    protected ExecutorService getExecutorService() {
        if (executorService == null) {
            //executorService = Executors.newFixedThreadPool(6);
            // The advantage of the Single ThreadExecutor is that it's queue is 
            // effectively unlimited.
            executorService = Executors.newSingleThreadExecutor();
        }
        return executorService;
    }
}
