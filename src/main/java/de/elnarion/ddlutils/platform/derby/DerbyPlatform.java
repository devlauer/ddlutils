package de.elnarion.ddlutils.platform.derby;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.elnarion.ddlutils.DatabaseOperationException;
import de.elnarion.ddlutils.PlatformInfo;
import de.elnarion.ddlutils.alteration.AddColumnChange;
import de.elnarion.ddlutils.alteration.TableChange;
import de.elnarion.ddlutils.alteration.TableDefinitionChangesPredicate;
import de.elnarion.ddlutils.model.CascadeActionEnum;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.ddlutils.platform.DefaultTableDefinitionChangesPredicate;
import de.elnarion.ddlutils.platform.PlatformImplBase;

/**
 * The platform implementation for Derby.
 * 
 * @version $Revision: 231306 $
 */
public class DerbyPlatform extends PlatformImplBase
{
    /** Database name of this platform. */
    public static final String DATABASENAME         = "Derby";
    /** The derby jdbc driver for use as a client for a normal server. */
    public static final String JDBC_DRIVER          = "org.apache.derby.jdbc.ClientDriver";
    /** The derby jdbc driver for use as an embedded database. */
    public static final String JDBC_DRIVER_EMBEDDED = "org.apache.derby.jdbc.EmbeddedDriver";
    /** The subprotocol used by the derby drivers. */
    public static final String JDBC_SUBPROTOCOL     = "derby";

    /**
     * Creates a new Derby platform instance.
     */
    public DerbyPlatform()
    {
        super();
        PlatformInfo info = getPlatformInfo();

        info.setMaxIdentifierLength(128);
        info.setSystemForeignKeyIndicesAlwaysNonUnique(true);
        info.setPrimaryKeyColumnAutomaticallyRequired(true);
        info.setIdentityColumnAutomaticallyRequired(true);
        info.setMultipleIdentityColumnsSupported(false);

        // BINARY and VARBINARY will also be handled by CloudscapeBuilder.getSqlType
        info.addNativeTypeMapping(Types.ARRAY,         "BLOB",                      Types.BLOB);
        info.addNativeTypeMapping(Types.BINARY,        "CHAR {0} FOR BIT DATA");
        info.addNativeTypeMapping(Types.BIT,           "SMALLINT",                  Types.SMALLINT);
        info.addNativeTypeMapping(Types.BOOLEAN,       "SMALLINT",                  Types.SMALLINT);
        info.addNativeTypeMapping(Types.DATALINK,      "LONG VARCHAR FOR BIT DATA", Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.DISTINCT,      "BLOB",                      Types.BLOB);
        info.addNativeTypeMapping(Types.DOUBLE, "DOUBLE");
        info.addNativeTypeMapping(Types.FLOAT,  "DOUBLE", Types.DOUBLE);
        info.addNativeTypeMapping(Types.JAVA_OBJECT,   "BLOB",                      Types.BLOB);
        info.addNativeTypeMapping(Types.LONGVARBINARY, "LONG VARCHAR FOR BIT DATA");
        info.addNativeTypeMapping(Types.LONGVARCHAR,   "LONG VARCHAR");
        info.addNativeTypeMapping(Types.NULL,          "LONG VARCHAR FOR BIT DATA", Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.OTHER,         "BLOB",                      Types.BLOB);
        info.addNativeTypeMapping(Types.REF,           "LONG VARCHAR FOR BIT DATA", Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.STRUCT,        "BLOB",                      Types.BLOB);
        info.addNativeTypeMapping(Types.TINYINT,       "SMALLINT",                  Types.SMALLINT);
        info.addNativeTypeMapping(Types.VARBINARY,     "VARCHAR {0} FOR BIT DATA");

        info.setDefaultSize(Types.BINARY,    254);
        info.setDefaultSize(Types.CHAR,      254);
        info.setDefaultSize(Types.VARBINARY, 254);
        info.setDefaultSize(Types.VARCHAR,   254);

        info.setSupportedOnUpdateActions(new CascadeActionEnum[] { CascadeActionEnum.NONE, CascadeActionEnum.RESTRICT });
        info.setDefaultOnUpdateAction(CascadeActionEnum.NONE);
        info.addEquivalentOnUpdateActions(CascadeActionEnum.NONE, CascadeActionEnum.RESTRICT);
        info.setSupportedOnDeleteActions(new CascadeActionEnum[] { CascadeActionEnum.NONE, CascadeActionEnum.RESTRICT,
                                                                   CascadeActionEnum.CASCADE, CascadeActionEnum.SET_NULL });
        info.setDefaultOnDeleteAction(CascadeActionEnum.NONE);

        setSqlBuilder(new DerbyBuilder(this));
        setModelReader(new DerbyModelReader(this));
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return DATABASENAME;
    }

    /**
     * {@inheritDoc}
     */
    public void createDatabase(String jdbcDriverClassName, String connectionUrl, String username, String password, Map<String, String> parameters) throws DatabaseOperationException, UnsupportedOperationException
    {
        // For Derby, you create databases by simply appending ";create=true" to the connection url
        if (JDBC_DRIVER.equals(jdbcDriverClassName) ||
            JDBC_DRIVER_EMBEDDED.equals(jdbcDriverClassName))
        {
            StringBuffer creationUrl = new StringBuffer();
            Connection   connection  = null;

            creationUrl.append(connectionUrl);
            creationUrl.append(";create=true");
            if ((parameters != null) && !parameters.isEmpty())
            {
                for (Iterator<Entry<String, String>> it = parameters.entrySet().iterator(); it.hasNext();)
                {
                    Map.Entry<String, String> entry = it.next();

                    // no need to specify create twice (and create=false wouldn't help anyway)
                    if (!"create".equalsIgnoreCase(entry.getKey().toString()))
                    {
                        creationUrl.append(";");
                        creationUrl.append(entry.getKey().toString());
                        creationUrl.append("=");
                        if (entry.getValue() != null)
                        {
                            creationUrl.append(entry.getValue().toString());
                        }
                    }
                }
            }
            if (getLog().isDebugEnabled())
            {
                getLog().debug("About to create database using this URL: "+creationUrl.toString());
            }
            try
            {
                Class.forName(jdbcDriverClassName);

                connection = DriverManager.getConnection(creationUrl.toString(), username, password);
                logWarnings(connection);
            }
            catch (Exception ex)
            {
                throw new DatabaseOperationException("Error while trying to create a database", ex);
            }
            finally
            {
                if (connection != null)
                {
                    try
                    {
                        connection.close();
                    }
                    catch (SQLException ex)
                    {}
                }
            }
        }
        else
        {
            throw new UnsupportedOperationException("Unable to create a Derby database via the driver "+jdbcDriverClassName);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected TableDefinitionChangesPredicate getTableDefinitionChangesPredicate()
    {
        return new DefaultTableDefinitionChangesPredicate()
        {
            protected boolean isSupported(Table intermediateTable, TableChange change)
            {
                // Derby cannot add IDENTITY columns
                if ((change instanceof AddColumnChange) &&
                    ((AddColumnChange)change).getNewColumn().isAutoIncrement())
                {
                    return false;
                }
                else
                {
                    return super.isSupported(intermediateTable, change);
                }
            }
        };
    }
}
