package de.elnarion.ddlutils.platform.db2;

import java.util.regex.Pattern;

import de.elnarion.ddlutils.Platform;

/**
 * Reads a database model from a Db2 UDB database.
 *
 * @version $Revision: $
 */
public class Db2v11ModelReader extends Db2ModelReader
{

	/**
     * Creates a new model reader for Db2 databases.
     * 
     * @param platform The platform that this model reader belongs to
     */
    public Db2v11ModelReader(Platform platform)
    {
        super(platform);
        setSearchStringPattern(Pattern.compile("[%]"));
    }
}
