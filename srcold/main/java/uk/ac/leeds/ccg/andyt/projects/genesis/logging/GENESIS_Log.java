/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.logging;

import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.logging.Generic_Log;

/**
 *
 * @author geoagdt
 */
public final class GENESIS_Log extends Generic_Log {

    private static final String sourcePackage = GENESIS_Log.class.getPackage().getName();
    private static final String sourceClass = GENESIS_Log.class.getName();
    //private static final Logger logger = Logger.getLogger(sourcePackage);
    public static final Level GENESIS_DefaultLogLevel = Level.ALL;
    public static final String GENESIS_DefaultLoggerName = sourcePackage + ".GENESIS_Log";
//    public GENESIS_Log(){
//    }
//
//    public GENESIS_Log(GENESIS_Log a_GENESIS_Log){
//        super(a_GENESIS_Log);
//        this._GENESIS_Environment = a_GENESIS_Log._GENESIS_Environment;
//    }
//
//    public GENESIS_Log(
//            GENESIS_Environment a_GENESIS_Environment,
//            File directory,
//            String filename) {
//        this(a_GENESIS_Environment, GENESIS_DefaultLogLevel, directory, filename);
//    }
//
//    public GENESIS_Log(
//            GENESIS_Environment a_GENESIS_Environment,
//            Level level,
//            File directory,
//            String filename) {
//        super(level, directory, GENESIS_DefaultLoggerName, filename);
//        this._GENESIS_Environment = a_GENESIS_Environment;
////        initLog(
////                directory,
////                filename,
////                GENESIS_DefaultLogLevel);
//    }
//    
//    public GENESIS_Log(
//            GENESIS_Environment a_GENESIS_Environment,
//            Level level,
//            File directory,
//            String name,
//            String filename) {
//        super(level, directory, name, filename);
//        this._GENESIS_Environment = a_GENESIS_Environment;
////        initLog(
////                directory,
////                filename,
////                GENESIS_DefaultLogLevel);
//    }
//    protected static void initLog(
//            File directory,
//            String filename,
//            Level level) {
//        Logger.getLogger(Generic_DefaultLoggerName);
//        initLogFileHandler(directory, filename, level);
//        getLogger().setLevel(level);   
//    }
//    /**
//     * Initialise logs from input configuration file
//     * @TODO Move this method to super class...
//     * @param logDirectory
//     * @param inputDataDirectory
//     * @param name 
//     */
//    public static void initLog(
//            File logDirectory,
//            File workplaceDirectory,
//            String logname,
//            String logFilename) {
//        Logger logger = Logger.getLogger(logname);
//        LogManager.getLogManager().addLogger(logger);
//        File loggingProperties_File = new File(
//                workplaceDirectory.toString() + System.getProperty("java.util.logging.config.file"));
//        if (!loggingProperties_File.exists()) {
//            logger.log(Level.SEVERE, "Could not load logging.properties file {0}", loggingProperties_File);
//        }
//        Properties logging_Properties = new Properties();
//        try {
//            FileInputStream loggingProperties_FileInputStream;
//// This does not set logging properties configuration as expected!
////            loggingProperties_FileInputStream = new FileInputStream(loggingProperties_File);
////            LogManager.getLogManager().readConfiguration(loggingProperties_FileInputStream);
////            loggingProperties_FileInputStream.close();
////            System.out.println(LogManager.getLogManager().getProperty("java.util.logging.config.class"));
////            System.out.println(LogManager.getLogManager().getProperty("java.util.logging.config.file"));
////            System.out.println(LogManager.getLogManager().getProperty(sourcePackage + ".level"));
//            loggingProperties_FileInputStream = new FileInputStream(loggingProperties_File);
//            logging_Properties.load(loggingProperties_FileInputStream);
//            loggingProperties_FileInputStream.close();
//        } catch (IOException ex) {
//            logger.log(Level.SEVERE, "Could not load logging.properties file " + loggingProperties_File, ex);
//        }
//        //Logger logger = LogManager.getLogManager().getLogger(sourcePackage);
//        //System.out.println(System.getProperties().keySet().toString());
//
////        ResourceBundle resourceBundle = Logger.getLogger(name).getResourceBundle();
////        if (resourceBundle != null) {
////            System.out.println(resourceBundle.keySet().toString());
////            Enumeration<String> enumeration = resourceBundle.getKeys();
////            String key;
////            Object object;
////            while (enumeration.hasMoreElements()) {
////                key = enumeration.nextElement();
////                object = resourceBundle.getObject(key);
////                System.out.println(key + " " + object.toString());
////            }
////        }
//        String level_String = logging_Properties.getProperty(
//                logname + ".level");
//        Level level = null;
//        if (level_String != null) {
//            level = Level.parse(level_String);
//        } else {
//            level = Level.ALL;
//        }
//        //Level level = logger.getLevel();
//        //Level level = Level.ALL;
//        //level = Logger.getLogger(GENESIS_Mortality.class.getName()).getLevel();
//        //System.out.println(logging_Properties.getProperty("java.util.logging.FileHandler.limit"));
//        String fileHandlerLimit = logging_Properties.getProperty("java.util.logging.FileHandler.limit");
//        int logFileHandlerLimit;
//        if (fileHandlerLimit != null) {
//            logFileHandlerLimit = Integer.parseInt(fileHandlerLimit);
//        } else {
//            logFileHandlerLimit = 10000000;
//        }
//        //System.out.println(logging_Properties.getProperty("java.util.logging.FileHandler.count"));
//        String fileHandlerCount = logging_Properties.getProperty("java.util.logging.FileHandler.count");
//        int logFileHandlerCount;
//        if (fileHandlerCount != null) {
//            logFileHandlerCount = Integer.parseInt(fileHandlerCount);
//        } else {
//            logFileHandlerCount = 10;
//        }
//        // Set up FileHandler
////        addFileHandler(
////                level,
////                logDirectory,
////                logname,
////                logFilename,
////                logFileHandlerLimit,
////                logFileHandlerCount);
////        FileHandler fileHandler = Generic_Log.getFileHandler(logname);
//        String fileSeparator = System.getProperty("file.separator");
//        FileHandler fileHandler = null;
//        boolean append = true;
//        try {
//            File logFileHandlerDirectory = new File(logDirectory,
//                    logname);
//            logFileHandlerDirectory.mkdirs();
//            fileHandler = new FileHandler(
//                    logDirectory + fileSeparator
//                    + logname + fileSeparator + logFilename,
//                    append);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            logger.log(Level.SEVERE, "Could not initialise FileHandler using FileHandler({0}{1}{2}{3}{4})", new Object[]{logDirectory, fileSeparator, logname, fileSeparator, logFilename});
//        }
//        //Generic_Log.getFileHandler(logname);
//        String fileHandlerFormatter_String = logging_Properties.getProperty("java.util.logging.FileHandler.formatter");
//        if (fileHandlerFormatter_String != null) {
//            if (fileHandlerFormatter_String.equalsIgnoreCase("java.util.logging.SimpleFormatter")) {
//                fileHandler.setFormatter(new SimpleFormatter());
//            } else {
//                if (fileHandlerFormatter_String.equalsIgnoreCase("java.util.logging.XMLFormatter")) {
//                    fileHandler.setFormatter(new XMLFormatter());
//                }
//            }
//        } else {
//            fileHandler.setFormatter(new SimpleFormatter());
//        }
//        Level fileHandlerLevel = Level.parse(logging_Properties.getProperty("java.util.logging.FileHandler.level"));
//        if (fileHandlerLevel == null) {
//            fileHandlerLevel = level;
//        } else {
//            fileHandler.setLevel(fileHandlerLevel);
//        }
//        //String fileHandlerToString = fileHandler.toString();
//        logger.addHandler(fileHandler);
//        // Set up ConsoleHandler
//        ConsoleHandler consoleHandler = new ConsoleHandler();
//        Level consoleHandlerLevel = Level.parse(logging_Properties.getProperty("java.util.logging.ConsoleHandler.level"));
//        consoleHandler.setLevel(consoleHandlerLevel);
//        String consoleFormatter_String = logging_Properties.getProperty("java.util.logging.ConsoleHandler.formatter");
//        if (consoleFormatter_String.equalsIgnoreCase("java.util.logging.SimpleFormatter")) {
//            consoleHandler.setFormatter(new SimpleFormatter());
//        } else {
//            if (consoleFormatter_String.equalsIgnoreCase("java.util.logging.XMLFormatter")) {
//                consoleHandler.setFormatter(new XMLFormatter());
//            }
//        }
//        logger.addHandler(consoleHandler);
//        // Log System.getProperties()
//        Properties properties = System.getProperties();
//        Enumeration<Object> enumeration = properties.keys();
//        String key_String;
//        String property;
//        while (enumeration.hasMoreElements()) {
//            key_String = enumeration.nextElement().toString();
//            property = properties.getProperty(key_String);
//            logger.log(Level.ALL, "{0} {1}", new Object[]{key_String, property});
//        }
//        // Log loggingPropertiesKeys
//        Enumeration<Object> loggingPropertiesKeys = logging_Properties.keys();
//        while (loggingPropertiesKeys.hasMoreElements()) {
//            key_String = loggingPropertiesKeys.nextElement().toString();
//            property = logging_Properties.getProperty(key_String);
//            logger.log(Level.ALL, "{0} {1}", new Object[]{key_String, property});
//        }
//    }
}
