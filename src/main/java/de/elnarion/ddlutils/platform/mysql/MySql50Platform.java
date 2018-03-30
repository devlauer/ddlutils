package de.elnarion.ddlutils.platform.mysql;

import de.elnarion.ddlutils.PlatformInfo;

/**
 * The platform implementation for MySQL 5 and above.
 * 
 * @version $Revision: 231306 $
 */
public class MySql50Platform extends MySqlPlatform
{
    /** Database name of this platform. */
    public static final String DATABASENAME = "MySQL5";

    /**
     * Creates a new platform instance.
     */
    public MySql50Platform()
    {
        super();

        PlatformInfo info = getPlatformInfo();

        // MySql 5.0 returns an empty string for default values for pk columns
        // which is different from the MySql 4 behaviour
        info.setSyntheticDefaultValueForRequiredReturned(false);

        setSqlBuilder(new MySql50Builder(this));
        setModelReader(new MySql50ModelReader(this));
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return DATABASENAME;
    }
}
