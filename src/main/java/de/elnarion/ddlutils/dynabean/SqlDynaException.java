package de.elnarion.ddlutils.dynabean;

import de.elnarion.ddlutils.DdlUtilsException;

/**
 * This exception is thrown when something dealing with sql dyna beans or classes failed.
 * 
 * @version $Revision: 289996 $
 */
public class SqlDynaException extends DdlUtilsException
{
    /** Constant for serializing instances of this class. */
	private static final long serialVersionUID = 7904337501884384392L;

	/**
     * Creates a new empty exception object.
     */
    public SqlDynaException()
    {
        super();
    }

    /**
     * Creates a new exception object.
     * 
     * @param msg The exception message
     */
    public SqlDynaException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new exception object.
     * 
     * @param baseEx The base exception
     */
    public SqlDynaException(Throwable baseEx)
    {
        super(baseEx);
    }

    /**
     * Creates a new exception object.
     * 
     * @param msg    The exception message
     * @param baseEx The base exception
     */
    public SqlDynaException(String msg, Throwable baseEx)
    {
        super(msg, baseEx);
    }

}
