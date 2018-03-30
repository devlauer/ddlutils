package de.elnarion.ddlutils;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.DynaBean;

import de.elnarion.ddlutils.alteration.ModelChange;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.ddlutils.platform.CreationParameters;
import de.elnarion.ddlutils.platform.JdbcModelReader;
import de.elnarion.ddlutils.platform.SqlBuilder;

/**
 * A platform encapsulates the database-related functionality such as performing queries
 * and manipulations. It also contains an sql builder that is specific to this platform.
 * 
 * @version $Revision: 231110 $
 */
public interface Platform
{
    /**
     * Returns the name of the database that this platform is for.
     * 
     * @return The name
     */
    public String getName();

    /**
     * Returns the info object for this platform.
     * 
     * @return The info object
     */
    public PlatformInfo getPlatformInfo();
    
    /**
     * Returns the sql builder for the this platform.
     * 
     * @return The sql builder
     */
    public SqlBuilder getSqlBuilder();

    /**
     * Returns the model reader (which reads a database model from a live database) for this platform.
     * 
     * @return The model reader
     */
    public JdbcModelReader getModelReader();
    
    /**
     * Returns the data source that this platform uses to access the database.
     * 
     * @return The data source
     */
    public DataSource getDataSource();
    
    /**
     * Sets the data source that this platform shall use to access the database.
     * 
     * @param dataSource The data source
     */
    public void setDataSource(DataSource dataSource);

    /**
     * Returns the username that this platform shall use to access the database.
     * 
     * @return The username
     */
    public String getUsername();

    /**
     * Sets the username that this platform shall use to access the database.
     * 
     * @param username The username
     */
    public void setUsername(String username);

    /**
     * Returns the password that this platform shall use to access the database.
     * 
     * @return The password
     */
    public String getPassword();

    /**
     * Sets the password that this platform shall use to access the database.
     * 
     * @param password The password
     */
    public void setPassword(String password);

    // runtime properties

    /**
     * Determines whether script mode is on. This means that the generated SQL is not
     * intended to be sent directly to the database but rather to be saved in a SQL
     * script file. Per default, script mode is off.
     * 
     * @return <code>true</code> if script mode is on
     */
    public boolean isScriptModeOn();

    /**
     * Specifies whether script mode is on. This means that the generated SQL is not
     * intended to be sent directly to the database but rather to be saved in a SQL
     * script file.
     * 
     * @param scriptModeOn <code>true</code> if script mode is on
     */
    public void setScriptModeOn(boolean scriptModeOn);

    /**
     * Determines whether delimited identifiers are used or normal SQL92 identifiers
     * (which may only contain alphanumerical characters and the underscore, must start
     * with a letter and cannot be a reserved keyword).
     * Per default, delimited identifiers are not used
     *
     * @return <code>true</code> if delimited identifiers are used
     */
    public boolean isDelimitedIdentifierModeOn();

    /**
     * Specifies whether delimited identifiers are used or normal SQL92 identifiers.
     *
     * @param delimitedIdentifierModeOn <code>true</code> if delimited identifiers shall be used
     */
    public void setDelimitedIdentifierModeOn(boolean delimitedIdentifierModeOn);

    /**
     * Determines whether SQL comments are generated. 
     * 
     * @return <code>true</code> if SQL comments shall be generated
     */
    public boolean isSqlCommentsOn();

    /**
     * Specifies whether SQL comments shall be generated.
     * 
     * @param sqlCommentsOn <code>true</code> if SQL comments shall be generated
     */
    public void setSqlCommentsOn(boolean sqlCommentsOn);

    /**
     * Determines whether SQL insert statements can specify values for identity columns.
     * This setting is only relevant if the database supports it
     * ({@link PlatformInfo#isIdentityOverrideAllowed()}). If this is off, then the
     * <code>insert</code> methods will ignore values for identity columns. 
     *  
     * @return <code>true</code> if identity override is enabled (the default)
     */
    public boolean isIdentityOverrideOn();

    /**
     * Specifies whether SQL insert statements can specify values for identity columns.
     * This setting is only relevant if the database supports it
     * ({@link PlatformInfo#isIdentityOverrideAllowed()}). If this is off, then the
     * <code>insert</code> methods will ignore values for identity columns. 
     *  
     * @param identityOverrideOn <code>true</code> if identity override is enabled (the default)
     */
    public void setIdentityOverrideOn(boolean identityOverrideOn);

    /**
     * Determines whether foreign keys of a table read from a live database
     * are alphabetically sorted.
     *
     * @return <code>true</code> if read foreign keys are sorted
     */
    public boolean isForeignKeysSorted();

    /**
     * Specifies whether foreign keys read from a live database, shall be
     * alphabetically sorted.
     *
     * @param foreignKeysSorted <code>true</code> if read foreign keys shall be sorted
     */
    public void setForeignKeysSorted(boolean foreignKeysSorted);

    /**
     * Determines whether the default action for ON UPDATE is used if the specified one is not supported by the platform.
     * If this is set to <code>false</code>, then an exception will be thrown if the action is not supported. By default, this
     * is set to <code>true</code> meaning that the default action would be used.
     * 
     * @return <code>true</code> if the default action is used
     */
    public boolean isDefaultOnUpdateActionUsedIfUnsupported();

    /**
     * Specifies whether the default action for ON UPDATE shall be used if the specified one is not supported by the platform.
     * If this is set to <code>false</code>, then an exception will be thrown if the action is not supported. By default, this
     * is set to <code>true</code> meaning that the default action would be used.
     * 
     * @param useDefault If <code>true</code> then the default action will be used
     */
    public void setDefaultOnUpdateActionUsedIfUnsupported(boolean useDefault);

    /**
     * Determines whether the default action for ON DELETE is used if the specified one is not supported by the platform.
     * If this is set to <code>false</code>, then an exception will be thrown if the action is not supported. By default, this
     * is set to <code>true</code> meaning that the default action would be used.
     * 
     * @return <code>true</code> if the default action is used
     */
    public boolean isDefaultOnDeleteActionUsedIfUnsupported();

    /**
     * Specifies whether the default action for ON DELETE shall be used if the specified one is not supported by the platform.
     * If this is set to <code>false</code>, then an exception will be thrown if the action is not supported. By default, this
     * is set to <code>true</code> meaning that the default action would be used.
     * 
     * @param useDefault If <code>true</code> then the default action will be used
     */
    public void setDefaultOnDeleteActionUsedIfUnsupported(boolean useDefault);

    // functionality
    
    /**
	 * Returns a (new) JDBC connection from the data source.
	 *
	 * @return The connection
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public Connection borrowConnection() throws DatabaseOperationException;

    /**
     * Closes the given JDBC connection (returns it back to the pool if the datasource is poolable).
     * 
     * @param connection The connection
     */
    public void returnConnection(Connection connection);

    /**
	 * Executes a series of sql statements which must be seperated by the delimiter
	 * configured as {@link PlatformInfo#getSqlCommandDelimiter()} of the info
	 * object of this platform.
	 *
	 * @param sql
	 *            The sql statements to execute
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @return The number of errors
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public int evaluateBatch(String sql, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Executes a series of sql statements which must be seperated by the delimiter
	 * configured as {@link PlatformInfo#getSqlCommandDelimiter()} of the info
	 * object of this platform.
	 * 
	 * TODO: consider outputting a collection of String or some kind of statement
	 * object from the SqlBuilder instead of having to parse strings here
	 *
	 * @param connection
	 *            The connection to the database
	 * @param sql
	 *            The sql statements to execute
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @return The number of errors
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public int evaluateBatch(Connection connection, String sql, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Performs a shutdown at the database. This is necessary for some embedded
	 * databases which otherwise would be locked and thus would refuse other
	 * connections. Note that this does not change the database structure or data in
	 * it in any way.
	 *
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void shutdownDatabase() throws DatabaseOperationException;

    /**
	 * Performs a shutdown at the database. This is necessary for some embedded
	 * databases which otherwise would be locked and thus would refuse other
	 * connections. Note that this does not change the database structure or data in
	 * it in any way.
	 *
	 * @param connection
	 *            The connection to the database
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void shutdownDatabase(Connection connection) throws DatabaseOperationException;

    /**
	 * Creates the database specified by the given parameters. Please note that this
	 * method does not use a data source set via {@link #setDataSource(DataSource)}
	 * because it is not possible to retrieve the connection information from it
	 * without establishing a connection.<br/>
	 * The given connection url is the url that you'd use to connect to the
	 * already-created database.<br/>
	 * On some platforms, this method suppurts additional parameters. These are
	 * documented in the manual section for the individual platforms.
	 *
	 * @param jdbcDriverClassName
	 *            The jdbc driver class name
	 * @param connectionUrl
	 *            The url to connect to the database if it were already created
	 * @param username
	 *            The username for creating the database
	 * @param password
	 *            The password for creating the database
	 * @param parameters
	 *            Additional parameters relevant to database creation (which are
	 *            platform specific)
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @throws UnsupportedOperationException
	 *             the unsupported operation exception
	 */
    public void createDatabase(String jdbcDriverClassName, String connectionUrl, String username, String password, Map<String, String> parameters) throws DatabaseOperationException, UnsupportedOperationException;

    /**
	 * Drops the database specified by the given parameters. Please note that this
	 * method does not use a data source set via {@link #setDataSource(DataSource)}
	 * because it is not possible to retrieve the connection information from it
	 * without establishing a connection.
	 *
	 * @param jdbcDriverClassName
	 *            The jdbc driver class name
	 * @param connectionUrl
	 *            The url to connect to the database
	 * @param username
	 *            The username for creating the database
	 * @param password
	 *            The password for creating the database
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @throws UnsupportedOperationException
	 *             the unsupported operation exception
	 */
    public void dropDatabase(String jdbcDriverClassName, String connectionUrl, String username, String password) throws DatabaseOperationException, UnsupportedOperationException;

    /**
	 * Creates the tables defined in the database model.
	 *
	 * @param model
	 *            The database model
	 * @param dropTablesFirst
	 *            Whether to drop the tables prior to creating them (anew)
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #createModel(Database, boolean, boolean)} instead.
	 */
    public void createTables(Database model, boolean dropTablesFirst, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Creates the tables defined in the database model.
	 *
	 * @param connection
	 *            The connection to the database
	 * @param model
	 *            The database model
	 * @param dropTablesFirst
	 *            Whether to drop the tables prior to creating them (anew)
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #createModel(Connection, Database, boolean, boolean)}
	 *             instead.
	 */
    public void createTables(Connection connection, Database model, boolean dropTablesFirst, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Creates the tables defined in the database model.
	 *
	 * @param model
	 *            The database model
	 * @param params
	 *            The parameters used in the creation
	 * @param dropTablesFirst
	 *            Whether to drop the tables prior to creating them (anew)
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use
	 *             {@link #createModel(Database, CreationParameters, boolean, boolean)}
	 *             instead.
	 */
    public void createTables(Database model, CreationParameters params, boolean dropTablesFirst, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Creates the tables defined in the database model.
	 *
	 * @param connection
	 *            The connection to the database
	 * @param model
	 *            The database model
	 * @param params
	 *            The parameters used in the creation
	 * @param dropTablesFirst
	 *            Whether to drop the tables prior to creating them (anew)
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use
	 *             {@link #createModel(Connection, Database, CreationParameters, boolean, boolean)}
	 *             instead.
	 */
    public void createTables(Connection connection, Database model, CreationParameters params, boolean dropTablesFirst, boolean continueOnError) throws DatabaseOperationException;

    /**
     * Returns the SQL for creating the tables defined in the database model.
     * 
     * @param model           The database model
     * @param dropTablesFirst Whether to drop the tables prior to creating them (anew)
     * @param continueOnError Whether to continue executing the sql commands when an error occurred
     * @return The SQL statements
     * @deprecated Use {@link #getCreateModelSql(Database, boolean, boolean)} instead.
     */
    public String getCreateTablesSql(Database model, boolean dropTablesFirst, boolean continueOnError);

    /**
     * Returns the SQL for creating the tables defined in the database model.
     * 
     * @param model           The database model
     * @param params          The parameters used in the creation
     * @param dropTablesFirst Whether to drop the tables prior to creating them (anew)
     * @param continueOnError Whether to continue executing the sql commands when an error occurred
     * @return The SQL statements
     * @deprecated Use {@link #getCreateModelSql(Database, CreationParameters, boolean, boolean)} instead.
     */
    public String getCreateTablesSql(Database model, CreationParameters params, boolean dropTablesFirst, boolean continueOnError);

    /**
	 * Creates the tables defined in the database model.
	 *
	 * @param model
	 *            The database model
	 * @param dropTablesFirst
	 *            Whether to drop the tables prior to creating them (anew)
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void createModel(Database model, boolean dropTablesFirst, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Creates the tables defined in the database model.
	 *
	 * @param connection
	 *            The connection to the database
	 * @param model
	 *            The database model
	 * @param dropTablesFirst
	 *            Whether to drop the tables prior to creating them (anew)
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void createModel(Connection connection, Database model, boolean dropTablesFirst, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Creates the tables defined in the database model.
	 *
	 * @param model
	 *            The database model
	 * @param params
	 *            The parameters used in the creation
	 * @param dropTablesFirst
	 *            Whether to drop the tables prior to creating them (anew)
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void createModel(Database model, CreationParameters params, boolean dropTablesFirst, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Creates the tables defined in the database model.
	 *
	 * @param connection
	 *            The connection to the database
	 * @param model
	 *            The database model
	 * @param params
	 *            The parameters used in the creation
	 * @param dropTablesFirst
	 *            Whether to drop the tables prior to creating them (anew)
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void createModel(Connection connection, Database model, CreationParameters params, boolean dropTablesFirst, boolean continueOnError) throws DatabaseOperationException;

    /**
     * Returns the SQL for creating the tables defined in the database model.
     * 
     * @param model           The database model
     * @param dropTablesFirst Whether to drop the tables prior to creating them (anew)
     * @param continueOnError Whether to continue executing the sql commands when an error occurred
     * @return The SQL statements
     */
    public String getCreateModelSql(Database model, boolean dropTablesFirst, boolean continueOnError);

    /**
     * Returns the SQL for creating the tables defined in the database model.
     * 
     * @param model           The database model
     * @param params          The parameters used in the creation
     * @param dropTablesFirst Whether to drop the tables prior to creating them (anew)
     * @param continueOnError Whether to continue executing the sql commands when an error occurred
     * @return The SQL statements
     */
    public String getCreateModelSql(Database model, CreationParameters params, boolean dropTablesFirst, boolean continueOnError);

    /**
     * Returns the necessary changes to apply to the current database to make it the desired one.
     * These changes are in the correct order and have been adjusted for the current platform.
     * 
     * @param currentModel The current model
     * @param desiredModel The desired model
     * @return The list of changes, adjusted to the platform and sorted for execution
     */
    public List<ModelChange> getChanges(Database currentModel, Database desiredModel);

    /**
	 * Alters the database schema so that it match the given model.
	 *
	 * @param desiredDb
	 *            The desired database schema
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #alterModel(Database, Database, boolean)} together
	 *             with {@link #readModelFromDatabase(String)} instead.
	 */
    public void alterTables(Database desiredDb, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Alters the database schema so that it match the given model.
	 *
	 * @param desiredDb
	 *            The desired database schema
	 * @param params
	 *            The parameters used in the creation
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use
	 *             {@link #alterModel(Database, Database, CreationParameters, boolean)}
	 *             together with {@link #readModelFromDatabase(String)} instead.
	 */
    public void alterTables(Database desiredDb, CreationParameters params, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Alters the database schema so that it match the given model.
	 *
	 * @param catalog
	 *            The catalog in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param schema
	 *            The schema in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param tableTypes
	 *            The table types to read from the existing database; use
	 *            <code>null</code> or an empty array for the platform-specific
	 *            default value
	 * @param desiredDb
	 *            The desired database schema
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #alterModel(Database, Database, boolean)} together
	 *             with
	 *             {@link #readModelFromDatabase(String, String, String, String[])}
	 *             instead.
	 */
    public void alterTables(String catalog, String schema, String[] tableTypes, Database desiredDb, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Alters the database schema so that it match the given model.
	 *
	 * @param catalog
	 *            The catalog in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param schema
	 *            The schema in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param tableTypes
	 *            The table types to read from the existing database; use
	 *            <code>null</code> or an empty array for the platform-specific
	 *            default value
	 * @param desiredDb
	 *            The desired database schema
	 * @param params
	 *            The parameters used in the creation
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use
	 *             {@link #alterModel(Database, Database, CreationParameters, boolean)}
	 *             together with
	 *             {@link #readModelFromDatabase(String, String, String, String[])}
	 *             instead.
	 */
    public void alterTables(String catalog, String schema, String[] tableTypes, Database desiredDb, CreationParameters params, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Alters the database schema so that it match the given model.
	 *
	 * @param connection
	 *            A connection to the existing database that shall be modified
	 * @param desiredDb
	 *            The desired database schema
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #alterModel(Connection, Database, Database, boolean)}
	 *             together with {@link #readModelFromDatabase(Connection, String)}
	 *             instead.
	 */
    public void alterTables(Connection connection, Database desiredDb, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Alters the database schema so that it match the given model.
	 *
	 * @param connection
	 *            A connection to the existing database that shall be modified
	 * @param desiredDb
	 *            The desired database schema
	 * @param params
	 *            The parameters used in the creation
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use
	 *             {@link #alterModel(Connection, Database, Database, CreationParameters, boolean)}
	 *             together with {@link #readModelFromDatabase(Connection, String)}
	 *             instead.
	 */
    public void alterTables(Connection connection, Database desiredDb, CreationParameters params, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Alters the database schema so that it match the given model.
	 *
	 * @param connection
	 *            A connection to the existing database that shall be modified
	 * @param catalog
	 *            The catalog in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param schema
	 *            The schema in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param tableTypes
	 *            The table types to read from the existing database; use
	 *            <code>null</code> or an empty array for the platform-specific
	 *            default value
	 * @param desiredDb
	 *            The desired database schema
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #alterModel(Connection, Database, Database, boolean)}
	 *             together with
	 *             {@link #readModelFromDatabase(Connection, String, String, String, String[])}
	 *             instead.
	 */
    public void alterTables(Connection connection, String catalog, String schema, String[] tableTypes, Database desiredDb, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Alters the database schema so that it match the given model.
	 *
	 * @param connection
	 *            A connection to the existing database that shall be modified
	 * @param catalog
	 *            The catalog in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param schema
	 *            The schema in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param tableTypes
	 *            The table types to read from the existing database; use
	 *            <code>null</code> or an empty array for the platform-specific
	 *            default value
	 * @param desiredDb
	 *            The desired database schema
	 * @param params
	 *            The parameters used in the creation
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use
	 *             {@link #alterModel(Connection, Database, Database, CreationParameters, boolean)}
	 *             together with
	 *             {@link #readModelFromDatabase(Connection, String, String, String, String[])}
	 *             instead.
	 */
    public void alterTables(Connection connection, String catalog, String schema, String[] tableTypes, Database desiredDb, CreationParameters params, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Returns the SQL for altering the database schema so that it match the given
	 * model.
	 *
	 * @param desiredDb
	 *            The desired database schema
	 * @return The SQL statements
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #getAlterModelSql(Database, Database)} together with
	 *             {@link #readModelFromDatabase(String)} instead.
	 */
    public String getAlterTablesSql(Database desiredDb) throws DatabaseOperationException;

    /**
	 * Returns the SQL for altering the database schema so that it match the given
	 * model.
	 *
	 * @param desiredDb
	 *            The desired database schema
	 * @param params
	 *            The parameters used in the creation
	 * @return The SQL statements
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use
	 *             {@link #getAlterModelSql(Database, Database, CreationParameters)}
	 *             together with {@link #readModelFromDatabase(String)} instead.
	 */
    public String getAlterTablesSql(Database desiredDb, CreationParameters params) throws DatabaseOperationException;

    /**
	 * Returns the SQL for altering the database schema so that it match the given
	 * model.
	 *
	 * @param catalog
	 *            The catalog in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param schema
	 *            The schema in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param tableTypes
	 *            The table types to read from the existing database; use
	 *            <code>null</code> or an empty array for the platform-specific
	 *            default value
	 * @param desiredDb
	 *            The desired database schema
	 * @return The SQL statements
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #getAlterModelSql(Database, Database)} together with
	 *             {@link #readModelFromDatabase(String, String, String, String[])}
	 *             instead.
	 */
    public String getAlterTablesSql(String catalog, String schema, String[] tableTypes, Database desiredDb) throws DatabaseOperationException;

    /**
	 * Returns the SQL for altering the database schema so that it match the given
	 * model.
	 *
	 * @param catalog
	 *            The catalog in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param schema
	 *            The schema in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param tableTypes
	 *            The table types to read from the existing database; use
	 *            <code>null</code> or an empty array for the platform-specific
	 *            default value
	 * @param desiredDb
	 *            The desired database schema
	 * @param params
	 *            The parameters used in the creation
	 * @return The SQL statements
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use
	 *             {@link #getAlterModelSql(Database, Database, CreationParameters)}
	 *             together with
	 *             {@link #readModelFromDatabase(String, String, String, String[])}
	 *             instead.
	 */
    public String getAlterTablesSql(String catalog, String schema, String[] tableTypes, Database desiredDb, CreationParameters params) throws DatabaseOperationException;

    /**
	 * Returns the SQL for altering the database schema so that it match the given
	 * model.
	 *
	 * @param connection
	 *            A connection to the existing database that shall be modified
	 * @param desiredDb
	 *            The desired database schema
	 * @return The SQL statements
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #getAlterModelSql(Database, Database)} together with
	 *             {@link #readModelFromDatabase(Connection, String)} instead.
	 */
    public String getAlterTablesSql(Connection connection, Database desiredDb) throws DatabaseOperationException;

    /**
	 * Returns the SQL for altering the database schema so that it match the given
	 * model.
	 *
	 * @param connection
	 *            A connection to the existing database that shall be modified
	 * @param desiredDb
	 *            The desired database schema
	 * @param params
	 *            The parameters used in the creation
	 * @return The SQL statements
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use
	 *             {@link #getAlterModelSql(Database, Database, CreationParameters)}
	 *             together with {@link #readModelFromDatabase(Connection, String)}
	 *             instead.
	 */
    public String getAlterTablesSql(Connection connection, Database desiredDb, CreationParameters params) throws DatabaseOperationException;

    /**
	 * Returns the SQL for altering the database schema so that it match the given
	 * model.
	 *
	 * @param connection
	 *            A connection to the existing database that shall be modified
	 * @param catalog
	 *            The catalog in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param schema
	 *            The schema in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param tableTypes
	 *            The table types to read from the existing database; use
	 *            <code>null</code> or an empty array for the platform-specific
	 *            default value
	 * @param desiredDb
	 *            The desired database schema
	 * @return The SQL statements
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #getAlterModelSql(Database, Database)} together with
	 *             {@link #readModelFromDatabase(Connection, String, String, String, String[])}
	 *             instead.
	 */
    public String getAlterTablesSql(Connection connection, String catalog, String schema, String[] tableTypes, Database desiredDb) throws DatabaseOperationException;

    /**
	 * Returns the SQL for altering the database schema so that it match the given
	 * model.
	 *
	 * @param connection
	 *            A connection to the existing database that shall be modified
	 * @param catalog
	 *            The catalog in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param schema
	 *            The schema in the existing database to read (can be a pattern);
	 *            use <code>null</code> for the platform-specific default value
	 * @param tableTypes
	 *            The table types to read from the existing database; use
	 *            <code>null</code> or an empty array for the platform-specific
	 *            default value
	 * @param desiredDb
	 *            The desired database schema
	 * @param params
	 *            The parameters used in the creation
	 * @return The SQL statements
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use
	 *             {@link #getAlterModelSql(Database, Database, CreationParameters)}
	 *             together with
	 *             {@link #readModelFromDatabase(Connection, String, String, String, String[])}
	 *             instead.
	 */
    public String getAlterTablesSql(Connection connection, String catalog, String schema, String[] tableTypes, Database desiredDb, CreationParameters params) throws DatabaseOperationException;

    /**
	 * Alters the given live database model so that it match the desired model,
	 * using the default database conneciton.
	 *
	 * @param currentModel
	 *            The current database model
	 * @param desiredModel
	 *            The desired database model
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void alterModel(Database currentModel, Database desiredModel, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Alters the given live database model so that it match the desired model,
	 * using the default database conneciton.
	 *
	 * @param currentModel
	 *            The current database model
	 * @param desiredModel
	 *            The desired database model
	 * @param params
	 *            The parameters used in the creation
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void alterModel(Database currentModel, Database desiredModel, CreationParameters params, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Alters the given live database model so that it match the desired model.
	 *
	 * @param connection
	 *            A connection to the existing database that shall be modified
	 * @param currentModel
	 *            The current database model
	 * @param desiredModel
	 *            The desired database model
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void alterModel(Connection connection, Database currentModel, Database desiredModel, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Alters the given live database model so that it match the desired model.
	 *
	 * @param connection
	 *            A connection to the existing database that shall be modified
	 * @param currentModel
	 *            The current database model
	 * @param desiredModel
	 *            The desired database model
	 * @param params
	 *            The parameters used in the creation
	 * @param continueOnError
	 *            Whether to continue with the next sql statement when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void alterModel(Connection connection, Database currentModel, Database desiredModel, CreationParameters params, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Returns the SQL for altering the given current model so that it match the
	 * desired model.
	 *
	 * @param currentModel
	 *            The current database model
	 * @param desiredModel
	 *            The desired database model
	 * @return The SQL statements
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public String getAlterModelSql(Database currentModel, Database desiredModel) throws DatabaseOperationException;

    /**
	 * Returns the SQL for altering the given current model so that it match the
	 * desired model.
	 *
	 * @param currentModel
	 *            The current database model
	 * @param desiredModel
	 *            The desired database model
	 * @param params
	 *            The parameters used in the creation of tables etc.
	 * @return The SQL statements
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public String getAlterModelSql(Database currentModel, Database desiredModel, CreationParameters params) throws DatabaseOperationException;

    /**
	 * Drops the specified table and all foreign keys pointing to it.
	 *
	 * @param model
	 *            The database model
	 * @param table
	 *            The table to drop
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void dropTable(Database model, Table table, boolean continueOnError) throws DatabaseOperationException;

    /**
     * Returns the SQL for dropping the given table and all foreign keys pointing to it.
     * 
     * @param model           The database model
     * @param table           The table to drop
     * @param continueOnError Whether to continue executing the sql commands when an error occurred
     * @return The SQL statements
     */
    public String getDropTableSql(Database model, Table table, boolean continueOnError);

    /**
	 * Drops the specified table and all foreign keys pointing to it.
	 *
	 * @param connection
	 *            The connection to the database
	 * @param model
	 *            The database model
	 * @param table
	 *            The table to drop
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void dropTable(Connection connection, Database model, Table table, boolean continueOnError) throws DatabaseOperationException; 

    /**
     * Returns the SQL for dropping the given model.
     * 
     * @param model           The database model
     * @param continueOnError Whether to continue executing the sql commands when an error occurred
     * @return The SQL statements
     * @deprecated Use {@link #getDropModelSql(Database)} instead.
     */
    public String getDropTablesSql(Database model, boolean continueOnError);

    /**
	 * Drops the given model using the default database connection.
	 *
	 * @param model
	 *            The database model
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #dropModel(Database, boolean)} instead.
	 */
    public void dropTables(Database model, boolean continueOnError) throws DatabaseOperationException;

    /**
	 * Drops the given model.
	 *
	 * @param connection
	 *            The connection to the database
	 * @param model
	 *            The database model
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 * @deprecated Use {@link #dropModel(Connection, Database, boolean)} instead.
	 */
    public void dropTables(Connection connection, Database model, boolean continueOnError) throws DatabaseOperationException; 

    /**
     * Returns the SQL for dropping the given model.
     * 
     * @param model The database model
     * @return The SQL statements
     */
    public String getDropModelSql(Database model);

    /**
	 * Drops the given model using the default database connection.
	 *
	 * @param model
	 *            The database model
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void dropModel(Database model, boolean continueOnError) throws DatabaseOperationException; 

    /**
	 * Drops the given model.
	 *
	 * @param connection
	 *            The connection to the database
	 * @param model
	 *            The database model
	 * @param continueOnError
	 *            Whether to continue executing the sql commands when an error
	 *            occurred
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void dropModel(Connection connection, Database model, boolean continueOnError) throws DatabaseOperationException; 

    /**
	 * Performs the given SQL query returning an iterator over the results.
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The sql query to perform
	 * @return An iterator for the dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public Iterator<DynaBean> query(Database model, String sql) throws DatabaseOperationException;

    /**
	 * Performs the given parameterized SQL query returning an iterator over the
	 * results.
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The sql query to perform
	 * @param parameters
	 *            The query parameter values
	 * @return An iterator for the dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public Iterator<DynaBean> query(Database model, String sql, Collection<Object> parameters) throws DatabaseOperationException;

    /**
	 * Performs the given SQL query returning an iterator over the results.
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The sql query to perform
	 * @param queryHints
	 *            The tables that are queried (optional)
	 * @return An iterator for the dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public Iterator<DynaBean> query(Database model, String sql, Table[] queryHints) throws DatabaseOperationException;

    /**
	 * Performs the given parameterized SQL query returning an iterator over the
	 * results.
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The sql query to perform
	 * @param parameters
	 *            The query parameter values
	 * @param queryHints
	 *            The tables that are queried (optional)
	 * @return An iterator for the dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public Iterator<DynaBean> query(Database model, String sql, Collection<Object> parameters, Table[] queryHints) throws DatabaseOperationException;

    /**
	 * Queries for a list of dyna beans representing rows of the given query. In
	 * contrast to the {@link #query(Database, String)} method all beans will be
	 * materialized and the connection will be closed before returning the beans.
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The sql query
	 * @return The dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public List<DynaBean> fetch(Database model, String sql) throws DatabaseOperationException;

    /**
	 * Queries for a list of dyna beans representing rows of the given query. In
	 * contrast to the {@link #query(Database, String, Collection)} method all beans
	 * will be materialized and the connection will be closed before returning the
	 * beans.
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The parameterized query
	 * @param parameters
	 *            The parameter values
	 * @return The dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public List<DynaBean> fetch(Database model, String sql, Collection<Object> parameters) throws DatabaseOperationException;

    /**
	 * Queries for a list of dyna beans representing rows of the given query. In
	 * contrast to the {@link #query(Database, String)} method all beans will be
	 * materialized and the connection will be closed before returning the beans.
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The sql query
	 * @param queryHints
	 *            The tables that are queried (optional)
	 * @return The dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public List<DynaBean> fetch(Database model, String sql, Table[] queryHints) throws DatabaseOperationException;

    /**
	 * Queries for a list of dyna beans representing rows of the given query. In
	 * contrast to the {@link #query(Database, String, Collection)} method all beans
	 * will be materialized and the connection will be closed before returning the
	 * beans.
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The parameterized query
	 * @param parameters
	 *            The parameter values
	 * @param queryHints
	 *            The tables that are queried (optional)
	 * @return The dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public List<DynaBean> fetch(Database model, String sql, Collection<Object> parameters, Table[] queryHints) throws DatabaseOperationException;

    /**
	 * Queries for a list of dyna beans representing rows of the given query. In
	 * contrast to the {@link #query(Database, String)} method all beans will be
	 * materialized and the connection will be closed before returning the beans.
	 * Also, the two int parameters specify which rows of the result set to use. If
	 * there are more rows than desired, they will be ignored (and not read from the
	 * database).
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The sql query
	 * @param start
	 *            Row number to start from (0 for first row)
	 * @param end
	 *            Row number to stop at (inclusively; -1 for last row)
	 * @return The dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public List<DynaBean> fetch(Database model, String sql, int start, int end) throws DatabaseOperationException;

    /**
	 * Queries for a list of dyna beans representing rows of the given query. In
	 * contrast to the {@link #query(Database, String, Collection)} method all beans
	 * will be materialized and the connection will be closed before returning the
	 * beans. Also, the two int parameters specify which rows of the result set to
	 * use. If there are more rows than desired, they will be ignored (and not read
	 * from the database).
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The parameterized sql query
	 * @param parameters
	 *            The parameter values
	 * @param start
	 *            Row number to start from (0 for first row)
	 * @param end
	 *            Row number to stop at (inclusively; -1 for last row)
	 * @return The dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public List<DynaBean> fetch(Database model, String sql, Collection<Object> parameters, int start, int end) throws DatabaseOperationException;

    /**
	 * Queries for a list of dyna beans representing rows of the given query. In
	 * contrast to the {@link #query(Database, String, Table[])} method all beans
	 * will be materialized and the connection will be closed before returning the
	 * beans. Also, the two int parameters specify which rows of the result set to
	 * use. If there are more rows than desired, they will be ignored (and not read
	 * from the database).
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The sql query
	 * @param queryHints
	 *            The tables that are queried (optional)
	 * @param start
	 *            Row number to start from (0 for first row)
	 * @param end
	 *            Row number to stop at (inclusively; -1 for last row)
	 * @return The dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public List<DynaBean> fetch(Database model, String sql, Table[] queryHints, int start, int end) throws DatabaseOperationException;

    /**
	 * Queries for a list of dyna beans representing rows of the given query. In
	 * contrast to the {@link #query(Database, String, Collection, Table[])} method
	 * all beans will be materialized and the connection will be closed before
	 * returning the beans. Also, the two int parameters specify which rows of the
	 * result set to use. If there are more rows than desired, they will be ignored
	 * (and not read from the database).
	 *
	 * @param model
	 *            The database model to use
	 * @param sql
	 *            The parameterized sql query
	 * @param parameters
	 *            The parameter values
	 * @param queryHints
	 *            The tables that are queried (optional)
	 * @param start
	 *            Row number to start from (0 for first row)
	 * @param end
	 *            Row number to stop at (inclusively; -1 for last row)
	 * @return The dyna beans resulting from the query
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
	public List<DynaBean> fetch(Database model, String sql, Collection<Object> parameters, Table[] queryHints, int start, int end) throws DatabaseOperationException;

    /**
     * Determines whether the given dyna bean is stored in the database. Note that this checks only
     * checks the primary key, not the other attributes.
     * 
     * @param model    The database model to use
     * @param dynaBean The bean
     * @return <code>true</code> if a bean with this primary key exists in the database
     */
    public boolean exists(Database model, DynaBean dynaBean);

    /**
     * Determines whether the given dyna bean is stored in the database. Note that this checks only
     * checks the primary key, not the other attributes.
     * 
     * @param connection The connection
     * @param model      The database model to use
     * @param dynaBean   The bean
     * @return <code>true</code> if a bean with this primary key exists in the database
     */
    public boolean exists(Connection connection, Database model, DynaBean dynaBean);

    /**
	 * Stores the given bean in the database, inserting it if there is no primary
	 * key otherwise the bean is updated in the database.
	 *
	 * @param model
	 *            The database model to use
	 * @param dynaBean
	 *            The bean to store
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void store(Database model, DynaBean dynaBean) throws DatabaseOperationException;

    /**
	 * Stores the given bean in the database, inserting it if there is no primary
	 * key otherwise the bean is updated in the database.
	 *
	 * @param connection
	 *            The connection
	 * @param model
	 *            The database model to use
	 * @param dynaBean
	 *            The bean to store
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void store(Connection connection, Database model, DynaBean dynaBean) throws DatabaseOperationException;

    /**
     * Returns the sql for inserting the given bean.
     * 
     * @param model    The database model to use
     * @param dynaBean The bean
     * @return The insert sql
     */
    public String getInsertSql(Database model, DynaBean dynaBean);

    /**
	 * Inserts the given DynaBean in the database, assuming the primary key values
	 * are specified.
	 *
	 * @param model
	 *            The database model to use
	 * @param dynaBean
	 *            The bean to insert
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void insert(Database model, DynaBean dynaBean) throws DatabaseOperationException;

    /**
	 * Inserts the bean. If one of the columns is an auto-incremented column, then
	 * the bean will also be updated with the column value generated by the
	 * database. Note that the connection will not be closed by this method.
	 *
	 * @param connection
	 *            The database connection
	 * @param model
	 *            The database model to use
	 * @param dynaBean
	 *            The bean
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void insert(Connection connection, Database model, DynaBean dynaBean) throws DatabaseOperationException;

    /**
	 * Inserts the given beans in the database, assuming the primary key values are
	 * specified. Note that a batch insert is used for subsequent beans of the same
	 * type. Also the properties for the primary keys are not updated in the beans.
	 * Hence you should not use this method when the primary key values are defined
	 * by the database (via a sequence or identity constraint).
	 *
	 * @param model
	 *            The database model to use
	 * @param dynaBeans
	 *            The beans to insert
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void insert(Database model, Collection<DynaBean> dynaBeans) throws DatabaseOperationException;

    /**
	 * Inserts the given beans. Note that a batch insert is used for subsequent
	 * beans of the same type. Also the properties for the primary keys are not
	 * updated in the beans. Hence you should not use this method when the primary
	 * key values are defined by the database (via a sequence or identity
	 * constraint). This method does not close the connection.
	 *
	 * @param connection
	 *            The database connection
	 * @param model
	 *            The database model to use
	 * @param dynaBeans
	 *            The beans
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void insert(Connection connection, Database model, Collection<DynaBean> dynaBeans) throws DatabaseOperationException;

    /**
     * Returns the sql for updating the given bean in the database. Note that this method can not be used to
     * generate SQL for updating primary key columns. 
     * 
     * @param model    The database model to use
     * @param dynaBean The bean
     * @return The update sql
     */
    public String getUpdateSql(Database model, DynaBean dynaBean);

    /**
     * Returns the sql for updating the given bean in the database. Note that this method can not be used to
     * generate SQL for updating primary key columns. 
     * 
     * @param model       The database model to use
     * @param oldDynaBean The bean identifying the row to update
     * @param newDynaBean The bean containing the new values
     * @return The update sql
     */
    public String getUpdateSql(Database model, DynaBean oldDynaBean, DynaBean newDynaBean);

    /**
	 * Updates the given bean in the database, assuming the primary key values are
	 * specified. Note that this means that this method will not update the primary
	 * key columns.
	 *
	 * @param model
	 *            The database model to use
	 * @param dynaBean
	 *            The bean
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void update(Database model, DynaBean dynaBean) throws DatabaseOperationException;

    /**
	 * Updates the row which maps to the given bean. Note that this means that this
	 * method will not update the primary key columns.
	 *
	 * @param connection
	 *            The database connection
	 * @param model
	 *            The database model to use
	 * @param dynaBean
	 *            The bean
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void update(Connection connection, Database model, DynaBean dynaBean) throws DatabaseOperationException;

    /**
	 * Updates the row identified by the given <code>oldDynaBean</code> in the
	 * database with the values in <code>newDynaBean</code>. This method can be used
	 * to update primary key columns.
	 *
	 * @param model
	 *            The database model to use
	 * @param oldDynaBean
	 *            The bean identifying the row (which means the primary key fields
	 *            need to be specified)
	 * @param newDynaBean
	 *            The bean containing the new data
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void update(Database model, DynaBean oldDynaBean, DynaBean newDynaBean) throws DatabaseOperationException;

    /**
	 * Updates the row identified by the given <code>oldDynaBean</code> in the
	 * database with the values in <code>newDynaBean</code>. This method can be used
	 * to update primary key columns.
	 *
	 * @param connection
	 *            The database connection
	 * @param model
	 *            The database model to use
	 * @param oldDynaBean
	 *            The bean identifying the row (which means the primary key fields
	 *            need to be specified)
	 * @param newDynaBean
	 *            The bean containing the new data
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void update(Connection connection, Database model, DynaBean oldDynaBean, DynaBean newDynaBean) throws DatabaseOperationException;

    /**
     * Returns the sql for deleting the given bean from the database.
     * 
     * @param model    The database model to use
     * @param dynaBean The bean
     * @return The sql
     */
    public String getDeleteSql(Database model, DynaBean dynaBean);

    /**
	 * Deletes the given bean from the database, assuming the primary key values are
	 * specified.
	 *
	 * @param model
	 *            The database model to use
	 * @param dynaBean
	 *            The bean to delete
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void delete(Database model, DynaBean dynaBean) throws DatabaseOperationException;

    /**
	 * Deletes the row which maps to the given bean from the database.
	 *
	 * @param connection
	 *            The database connection
	 * @param model
	 *            The database model to use
	 * @param dynaBean
	 *            The bean
	 * @throws DatabaseOperationException
	 *             the database operation exception
	 */
    public void delete(Connection connection, Database model, DynaBean dynaBean) throws DatabaseOperationException;

    /**
     * Reads the database model from the live database as specified by the data source set for
     * this platform.
     * 
     * @param name The name of the resulting database; <code>null</code> when the default name (the catalog)
     *             is desired which might be <code>null</code> itself though
     * @return The database model
     * @throws DatabaseOperationException If an error occurred during reading the model
     */
    public Database readModelFromDatabase(String name) throws DatabaseOperationException;

    /**
     * Reads the database model from the live database as specified by the data source set for
     * this platform.
     * 
     * @param name       The name of the resulting database; <code>null</code> when the default name (the catalog)
     *                   is desired which might be <code>null</code> itself though
     * @param catalog    The catalog to access in the database; use <code>null</code> for the default value
     * @param schema     The schema to access in the database; use <code>null</code> for the default value
     * @param tableTypes The table types to process; use <code>null</code> or an empty list for the default ones
     * @return The database model
     * @throws DatabaseOperationException If an error occurred during reading the model
     */
    public Database readModelFromDatabase(String name, String catalog, String schema, String[] tableTypes) throws DatabaseOperationException;

    /**
     * Reads the database model from the live database to which the given connection is pointing.
     * 
     * @param connection The connection to the database
     * @param name       The name of the resulting database; <code>null</code> when the default name (the catalog)
     *                   is desired which might be <code>null</code> itself though
     * @return The database model
     * @throws DatabaseOperationException If an error occurred during reading the model
     */
    public Database readModelFromDatabase(Connection connection, String name) throws DatabaseOperationException;

    /**
     * Reads the database model from the live database to which the given connection is pointing.
     * 
     * @param connection The connection to the database
     * @param name       The name of the resulting database; <code>null</code> when the default name (the catalog)
     *                   is desired which might be <code>null</code> itself though
     * @param catalog    The catalog to access in the database; use <code>null</code> for the default value
     * @param schema     The schema to access in the database; use <code>null</code> for the default value
     * @param tableTypes The table types to process; use <code>null</code> or an empty list for the default ones
     * @return The database model
     * @throws DatabaseOperationException If an error occurred during reading the model
     */
    public Database readModelFromDatabase(Connection connection, String name, String catalog, String schema, String[] tableTypes) throws DatabaseOperationException;
}
