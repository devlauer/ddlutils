package de.elnarion.ddlutils.platform.h2;


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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import de.elnarion.ddlutils.DdlUtilsException;
import de.elnarion.ddlutils.PlatformInfo;
import de.elnarion.ddlutils.alteration.AddColumnChange;
import de.elnarion.ddlutils.alteration.AddPrimaryKeyChange;
import de.elnarion.ddlutils.alteration.ModelComparator;
import de.elnarion.ddlutils.alteration.RemoveColumnChange;
import de.elnarion.ddlutils.alteration.TableChange;
import de.elnarion.ddlutils.alteration.TableDefinitionChangesPredicate;
import de.elnarion.ddlutils.model.CascadeActionEnum;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.ddlutils.platform.CreationParameters;
import de.elnarion.ddlutils.platform.DefaultTableDefinitionChangesPredicate;
import de.elnarion.ddlutils.platform.PlatformImplBase;

/**
 * The platform implementation for the H2 database.
 *
 * @version $Revision: 231306 $
 */
public class H2Platform extends PlatformImplBase
{
    /** Database name of this platform. */
    public static final String DATABASENAME     = "H2";
    /** The standard H2 driver. */
    public static final String JDBC_DRIVER      = "org.h2.Driver";
    /** The subprotocol used by the H2 driver. */
    public static final String JDBC_SUBPROTOCOL = "h2";

    /**
     * Creates a new instance of the H2 platform.
     */
    public H2Platform()
    {
        PlatformInfo info = getPlatformInfo();

        info.setPrimaryKeyColumnAutomaticallyRequired(true);
        info.setNonPrimaryKeyIdentityColumnsSupported(false);
        info.setIdentityOverrideAllowed(false);
        info.setMixingIdentityAndNormalPrimaryKeyColumnsSupported(false);
        info.setAlterTableForDropUsed(false);

        info.addEquivalentOnDeleteActions(CascadeActionEnum.NONE, CascadeActionEnum.RESTRICT);
        info.addEquivalentOnUpdateActions(CascadeActionEnum.NONE, CascadeActionEnum.RESTRICT);

        info.addNativeTypeMapping(Types.ARRAY,         "ARRAY",     Types.ARRAY);
        info.addNativeTypeMapping(Types.DISTINCT,      "BINARY",    Types.BINARY);
        info.addNativeTypeMapping(Types.NULL,          "BINARY",    Types.BINARY);
        info.addNativeTypeMapping(Types.REF,           "BINARY",    Types.BINARY);
        info.addNativeTypeMapping(Types.STRUCT,        "BINARY",    Types.BINARY);
        info.addNativeTypeMapping(Types.DATALINK,      "BINARY",    Types.BINARY);

        info.addNativeTypeMapping(Types.NUMERIC,       "DECIMAL",   Types.DECIMAL);
        info.addNativeTypeMapping(Types.LONGVARCHAR,   "VARCHAR",   Types.VARCHAR);
        info.addNativeTypeMapping(Types.BIT,           "BOOLEAN",   Types.BOOLEAN);
        info.addNativeTypeMapping(Types.TINYINT,       "SMALLINT",  Types.SMALLINT);
        info.addNativeTypeMapping(Types.SMALLINT,      "SMALLINT",  Types.SMALLINT);
        info.addNativeTypeMapping(Types.BINARY,        "VARBINARY", Types.VARBINARY);
        info.addNativeTypeMapping(Types.LONGVARBINARY, "VARBINARY", Types.VARBINARY);
        info.addNativeTypeMapping(Types.BLOB,          "BLOB",      Types.BLOB);
        info.addNativeTypeMapping(Types.CLOB,          "CLOB",      Types.CLOB);
        info.addNativeTypeMapping(Types.FLOAT,         "DOUBLE",    Types.DOUBLE);
        info.addNativeTypeMapping(Types.JAVA_OBJECT,   "OTHER");

        info.setDefaultSize(Types.CHAR,      Integer.MAX_VALUE);
        info.setDefaultSize(Types.VARCHAR,   Integer.MAX_VALUE);
        info.setDefaultSize(Types.BINARY,    Integer.MAX_VALUE);
        info.setDefaultSize(Types.VARBINARY, Integer.MAX_VALUE);

        setSqlBuilder(new H2Builder(this));
        setModelReader(new H2ModelReader(this));
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
    protected ModelComparator getModelComparator()
    {
        return new H2ModelComparator(getPlatformInfo(), getTableDefinitionChangesPredicate(), isDelimitedIdentifierModeOn());
    }

    /**
     * {@inheritDoc}
     */
    public void shutdownDatabase(Connection connection)
    {
        Statement stmt = null;

        try
        {
            stmt = connection.createStatement();
            stmt.executeUpdate("SHUTDOWN");
        }
        catch (SQLException ex)
        {
            throw new DdlUtilsException(ex);
        }
        finally
        {
            closeStatement(stmt);
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
                if (change instanceof RemoveColumnChange)
                {
                    Column column = intermediateTable.findColumn(((RemoveColumnChange)change).getChangedColumn(),
                                                                 isDelimitedIdentifierModeOn());

                    // H2 can only drop columns that are not part of a primary key
                    return !column.isPrimaryKey();
                }
                else if (change instanceof AddColumnChange)
                {
                    AddColumnChange addColumnChange = (AddColumnChange)change;

                    // adding IDENTITY columns is not supported without a table rebuild because they have to
                    // be PK columns, but we add them to the PK later
                    return addColumnChange.isAtEnd() &&
                           (!addColumnChange.getNewColumn().isRequired() ||
                            (addColumnChange.getNewColumn().getDefaultValue() != null));
                }
                else if (change instanceof AddPrimaryKeyChange)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        };
    }

    /**
     * Processes the addition of a column to a table.
     *
     * @param currentModel The current database schema
     * @param params       The parameters used in the creation of new tables. Note that for existing
     *                     tables, the parameters won't be applied
     * @param change       The change object
     */
    public void processChange(Database currentModel,
                              CreationParameters params,
                              AddColumnChange    change) throws IOException
    {
        Table  changedTable = findChangedTable(currentModel, change);
        Column nextColumn   = null;

        if (change.getNextColumn() != null)
        {
            nextColumn = changedTable.findColumn(change.getNextColumn(), isDelimitedIdentifierModeOn());
        }
        ((H2Builder)getSqlBuilder()).insertColumn(changedTable, change.getNewColumn(), nextColumn);
        change.apply(currentModel, isDelimitedIdentifierModeOn());
    }

    /**
     * Processes the removal of a column from a table.
     *
     * @param currentModel The current database schema
     * @param params       The parameters used in the creation of new tables. Note that for existing
     *                     tables, the parameters won't be applied
     * @param change       The change object
     * @throws IOException 
     */
    public void processChange(Database           currentModel,
                              CreationParameters params,
                              RemoveColumnChange change) throws IOException
    {
        Table  changedTable  = findChangedTable(currentModel, change);
        Column removedColumn = changedTable.findColumn(change.getChangedColumn(), isDelimitedIdentifierModeOn());

        ((H2Builder)getSqlBuilder()).dropColumn(changedTable, removedColumn);
        change.apply(currentModel, isDelimitedIdentifierModeOn());
    }
}