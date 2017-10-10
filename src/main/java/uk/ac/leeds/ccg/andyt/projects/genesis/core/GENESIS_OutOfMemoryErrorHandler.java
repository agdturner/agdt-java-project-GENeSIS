/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.core;

import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.generic.memory.Generic_OutOfMemoryErrorHandler;

/**
 *
 * @author Andy
 */
public abstract class GENESIS_OutOfMemoryErrorHandler
        extends Generic_OutOfMemoryErrorHandler
        implements Serializable, GENESIS_OutOfMemoryErrorHandlerInterface {

    //static final long serialVersionUID = 1L;
    public static long Memory_Threshold = 10000000;
}
