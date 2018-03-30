package de.elnarion.ddlutils.model;

import de.elnarion.ddlutils.DdlUtilsException;

/**
 * Indicates a model error.
 * 
 * @version $Revision: 289996 $
 */
public class ModelException extends DdlUtilsException 
{
    /** Constant for serializing instances of this class. */
    private static final long serialVersionUID = -694578915559780711L;
    
    /**
     * Creates a new empty exception object.
     */
    public ModelException()
    {
        super();
    }

    /**
     * Creates a new exception object.
     * 
     * @param msg The exception message
     */
    public ModelException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new exception object.
     * 
     * @param baseEx The base exception
     */
    public ModelException(Throwable baseEx)
    {
        super(baseEx);
    }

    /**
     * Creates a new exception object.
     * 
     * @param msg    The exception message
     * @param baseEx The base exception
     */
    public ModelException(String msg, Throwable baseEx)
    {
        super(msg, baseEx);
    }

}
