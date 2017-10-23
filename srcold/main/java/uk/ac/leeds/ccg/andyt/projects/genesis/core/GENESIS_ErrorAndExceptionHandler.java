package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import uk.ac.leeds.ccg.andyt.generic.core.Generic_ErrorAndExceptionHandler;

public abstract class GENESIS_ErrorAndExceptionHandler
        extends Generic_ErrorAndExceptionHandler {

    static final long serialVersionUID = 1L;
    // Application specific Errors and Excpetions
    public static final int ArgsErrorExitStatus = 100;
    public static final int EnvironmentNullExitStatus = 101;
    public static final int TimeInconsistentException = 102;
    public static final int OutOfMemoryErrorExitStatus = 2;
    public static final int NotFoundObjectsToSwapExitStatus = 3;
    public static final int JAXBExceptionExitStatus = 4;
//    // Java Errors
//    public static int Error = 1;
//    // Third party Errors
//    // Java Exceptions
//    public static int Exception = 20;
//    public static int IOException = 21;
//    public static int FileNotFoundException = 22;
//    public static int ClassNotFoundException = 23;
//    public static int NumberFormatException = 24;
//
//    // Third party Exceptions
//    public static int MPIException = 30;
}
