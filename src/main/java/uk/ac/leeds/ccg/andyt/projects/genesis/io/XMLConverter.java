package uk.ac.leeds.ccg.andyt.projects.genesis.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.FertilityFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.MetadataFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.MiscarriageFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.MortalityFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.ParametersFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.PopulationFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.fertility.FertilityType;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.metadata.MetadataType;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.miscarriage.MiscarriageType;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.mortality.MortalityType;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.parameters.ParametersType;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.population.PopulationType;
import uk.ac.leeds.ccg.andyt.projects.genesis.logging.GENESIS_Log;

/**
 * A convenience class for loading and saving XML
 */
public class XMLConverter {

    public static ParametersType loadParametersFromXMLFile(
            File aXML_File) {
        //ParametersFactory.init();
        ParametersType parameters = null;
        try {
            parameters = ParametersFactory.read(aXML_File);
        } catch (JAXBException aJAXBException) {
            log(aJAXBException.toString());
        }
        return parameters;
    }

    public static MetadataType loadMetadataFromXMLFile(
            File aXML_File) {
        //MetadataFactory.init();
        MetadataType metadata = null;
        try {
            metadata = MetadataFactory.read(aXML_File);
        } catch (JAXBException aJAXBException) {
            log(aJAXBException.toString());
        }
        return metadata;
    }

    public static MortalityType loadMortalityFromXMLFile(
            File aXML_File) {
        //MortalityFactory.init();
        MortalityType mortality = null;
        try {
            mortality = MortalityFactory.read(aXML_File);
        } catch (JAXBException aJAXBException) {
            log(aJAXBException.toString());
        }
        return mortality;
    }

    public static PopulationType loadPopulationFromXMLFile(
            File aXML_File) {
        //PopulationFactory.init();
        PopulationType population = null;
        try {
            population = PopulationFactory.read(aXML_File);
        } catch (JAXBException aJAXBException) {
            log(aJAXBException.toString());
        }
        return population;
    }

    public static MiscarriageType loadMiscarriageFromXMLFile(
            File aXML_File) {
        //MiscarriageFactory.init();
        MiscarriageType miscarriage = null;
        try {
            miscarriage = MiscarriageFactory.read(aXML_File);
        } catch (JAXBException aJAXBException) {
            log(aJAXBException.toString());
        }
        return miscarriage;
    }

    public static FertilityType loadFertilityFromXMLFile(
            File aXML_File) {
        //FertilityFactory.init();
        FertilityType fertility = null;
        try {
            fertility = FertilityFactory.read(aXML_File);
        } catch (JAXBException aJAXBException) {
            log(aJAXBException.toString());
        }
        return fertility;
    }

    public static void saveParametersToXMLFile(
            File file,
            ParametersType parameters) {
        //ParametersFactory.init();
        String methodName = "saveParametersToXMLFile(File,MiscarriageType)";
        String generalErrorAndExceptionSuffix =
                " in " + XMLConverter.class.getName() + "." + methodName;
        FileOutputStream fos = null;
        try {
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            ParametersFactory.writeFileOut(parameters, fos);
        } catch (FileNotFoundException e) {
            System.err.println("Tring to handle " + e.getLocalizedMessage());
            System.err.println("Wait for 2 seconds then trying again to writeToCSV.");
            // This can happen because of too many open files.
            // Try waiting for 2 seconds and then repeating...
            try {
                synchronized (file) {
                    file.wait(2000L);
                }
            } catch (InterruptedException ex) {
                log(Level.SEVERE,
                        ex.getLocalizedMessage() + generalErrorAndExceptionSuffix);
            }
            saveParametersToXMLFile(file, parameters);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } catch (JAXBException e) {
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                log(Level.SEVERE,
                        e.getLocalizedMessage() + generalErrorAndExceptionSuffix);

            }
        }
    }

    public static void saveMetadataToXMLFile(
            File file,
            MetadataType metadata) {
        //MetadataFactory.init();
        String methodName = "saveMetadataToXMLFile(File,MiscarriageType)";
        String generalErrorAndExceptionSuffix =
                " in " + XMLConverter.class.getName() + "." + methodName;
        FileOutputStream fos = null;
        try {
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            MetadataFactory.writeFileOut(metadata, fos);
        } catch (FileNotFoundException e) {
            System.err.println("Tring to handle " + e.getLocalizedMessage());
            System.err.println("Wait for 2 seconds then trying again to writeToCSV.");
            // This can happen because of too many open files.
            // Try waiting for 2 seconds and then repeating...
            try {
                synchronized (file) {
                    file.wait(2000L);
                }
            } catch (InterruptedException ex) {
                log(Level.SEVERE,
                        ex.getLocalizedMessage() + generalErrorAndExceptionSuffix);
            }
            saveMetadataToXMLFile(file, metadata);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } catch (JAXBException e) {
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                log(Level.SEVERE,
                        e.getLocalizedMessage() + generalErrorAndExceptionSuffix);

            }
        }
    }

    public static void savePopulationToXMLFile(
            File file,
            PopulationType population) {
        //PopulationFactory.init();
        String methodName = "savePopulationToXMLFile(File,MiscarriageType)";
        String generalErrorAndExceptionSuffix =
                " in " + XMLConverter.class.getName() + "." + methodName;
        FileOutputStream fos = null;
        try {
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            PopulationFactory.writeFileOut(population, fos);
        } catch (FileNotFoundException e) {
            System.err.println("Tring to handle " + e.getLocalizedMessage());
            System.err.println("Wait for 2 seconds then trying again to writeToCSV.");
            // This can happen because of too many open files.
            // Try waiting for 2 seconds and then repeating...
            try {
                synchronized (file) {
                    file.wait(2000L);
                }
            } catch (InterruptedException ex) {
                log(Level.SEVERE,
                        ex.getLocalizedMessage() + generalErrorAndExceptionSuffix);
            }
            savePopulationToXMLFile(file,
                    population);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } catch (JAXBException e) {
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                log(Level.SEVERE,
                        e.getLocalizedMessage() + generalErrorAndExceptionSuffix);

            }
        }
    }

    public static void saveMortalityToXMLFile(
            File file,
            MortalityType mortality) {
        //MortalityFactory.init();
        String methodName = "saveMortalityToXMLFile(File,MiscarriageType)";
        String generalErrorAndExceptionSuffix =
                " in " + XMLConverter.class.getName() + "." + methodName;
        FileOutputStream fos = null;
        try {
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            MortalityFactory.writeFileOut(mortality, fos);
        } catch (FileNotFoundException e) {
            System.err.println("Tring to handle " + e.getLocalizedMessage());
            System.err.println("Wait for 2 seconds then trying again to writeToCSV.");
            // This can happen because of too many open files.
            // Try waiting for 2 seconds and then repeating...
            try {
                synchronized (file) {
                    file.wait(2000L);
                }
            } catch (InterruptedException ex) {
                log(Level.SEVERE,
                        ex.getLocalizedMessage() + generalErrorAndExceptionSuffix);
            }
            saveMortalityToXMLFile(file, mortality);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } catch (JAXBException e) {
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                log(Level.SEVERE,
                        e.getLocalizedMessage() + generalErrorAndExceptionSuffix);

            }
        }
    }

    public static void saveMiscarriageToXMLFile(
            File file,
            MiscarriageType miscarriage) {
        //MiscarriageFactory.init();
        String methodName = "saveMiscarriageToXMLFile(File,MiscarriageType)";
        String generalErrorAndExceptionSuffix =
                " in " + XMLConverter.class.getName() + "." + methodName;
        FileOutputStream fos = null;
        try {
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            MiscarriageFactory.writeFileOut(miscarriage, fos);
        } catch (FileNotFoundException e) {
            System.err.println("Tring to handle " + e.getLocalizedMessage());
            System.err.println("Wait for 2 seconds then trying again to writeToCSV.");
            // This can happen because of too many open files.
            // Try waiting for 2 seconds and then repeating...
            try {
                synchronized (file) {
                    file.wait(2000L);
                }
            } catch (InterruptedException ex) {
                log(Level.SEVERE,
                        ex.getLocalizedMessage() + generalErrorAndExceptionSuffix);
            }
            saveMiscarriageToXMLFile(file, miscarriage);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } catch (JAXBException e) {
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                log(Level.SEVERE,
                        e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
            }
        }
    }

    public static void saveFertilityToXMLFile(
            File file,
            FertilityType fertility) {
        //FertilityFactory.init();
        String methodName = "saveFertilityToXMLFile(File,MiscarriageType)";
        String generalErrorAndExceptionSuffix =
                " in " + XMLConverter.class.getName() + "." + methodName;
        FileOutputStream fos = null;
        try {
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            FertilityFactory.writeFileOut(fertility, fos);
        } catch (FileNotFoundException e) {
            System.err.println("Tring to handle " + e.getLocalizedMessage());
            System.err.println("Wait for 2 seconds then trying again to writeToCSV.");
            // This can happen because of too many open files.
            // Try waiting for 2 seconds and then repeating...
            try {
                synchronized (file) {
                    file.wait(2000L);
                }
            } catch (InterruptedException ex) {
                log(Level.SEVERE,
                        ex.getLocalizedMessage() + generalErrorAndExceptionSuffix);
            }
            saveFertilityToXMLFile(file, fertility);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } catch (JAXBException e) {
            log(Level.SEVERE,
                    e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                log(Level.SEVERE,
                        e.getLocalizedMessage() + generalErrorAndExceptionSuffix);
            }
        }
    }

    private static void log(
            String message) {
        log(GENESIS_Log.GENESIS_DefaultLogLevel, message);
    }

    private static void log(
            Level level,
            String message) {
        if (level.intValue() != Level.OFF.intValue()) {
            System.out.println(message);
        }
        Logger.getLogger(GENESIS_Log.DefaultLoggerName).log(level, message);
    }
}
