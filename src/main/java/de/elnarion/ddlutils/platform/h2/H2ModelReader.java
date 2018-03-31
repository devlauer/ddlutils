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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

import de.elnarion.ddlutils.Platform;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.ForeignKey;
import de.elnarion.ddlutils.model.Index;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.ddlutils.model.TypeMap;
import de.elnarion.ddlutils.platform.DatabaseMetaDataWrapper;
import de.elnarion.ddlutils.platform.JdbcModelReader;

/**
 * Reads a database model from a H2 database.
 *
 * @version $Revision: $
 */
public class H2ModelReader extends JdbcModelReader
{
    /**
     * Creates a new model reader for H2 databases.
     *
     * @param platform The platform that this model reader belongs to
     */
    public H2ModelReader(Platform platform)
    {
        super(platform);
        setDefaultCatalogPattern(null);
        setDefaultSchemaPattern(null);
        setSearchStringPattern(Pattern.compile("[%]"));
    }

    /**
     * {@inheritDoc}
     */
    protected Table readTable(DatabaseMetaDataWrapper metaData, Map<String, Object> values) throws SQLException
    {
        Table table = super.readTable(metaData, values);

        if (table != null)
        {
            determineAutoIncrementColumns(table);
        }

        return table;
    }

    /**
     * {@inheritDoc}
     */
    protected Column readColumn(DatabaseMetaDataWrapper metaData, Map<String, Object> values) throws SQLException
    {
        Column column = super.readColumn(metaData, values);

        String defaultValue = column.getDefaultValue();
        if (defaultValue != null && defaultValue.startsWith("(NEXT VALUE FOR "))
        {
            // This is an auto-increment field
            column.setAutoIncrement(true);
            column.setDefaultValue(null);
            defaultValue = null;
        }

        if (TypeMap.isTextType(column.getTypeCode()) && defaultValue != null)
        {
            column.setDefaultValue(unescape(defaultValue, "'", "''"));
        }

        return column;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isInternalForeignKeyIndex(DatabaseMetaDataWrapper metaData, Table table, ForeignKey fk, Index index)
    {
        String name = index.getName();
        return name != null && name.startsWith(fk.getName() + "_INDEX_");
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isInternalPrimaryKeyIndex(DatabaseMetaDataWrapper metaData, Table table, Index index)
    {
        String name = index.getName();
        return name != null && name.startsWith("PRIMARY_KEY_");
    }

    /**
     * Helper method that determines the auto increment status using H2's system tables.
     *
     * @param table The table
     *
     * @throws SQLException if the query failed.
     */
    protected void determineAutoIncrementColumns(Table table) throws SQLException
    {
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = ? AND TABLE_SCHEMA = ?" +
                       " AND TABLE_NAME = ? AND SEQUENCE_NAME IS NOT NULL";

        PreparedStatement stmt = null;

        try
        {
            stmt = getConnection().prepareStatement(query);
            stmt.setString(1, table.getCatalog());
            stmt.setString(2, table.getSchema());
            stmt.setString(3, table.getName());

            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                String colName = rs.getString(1).trim();
                Column column  = table.findColumn(colName, getPlatform().isDelimitedIdentifierModeOn());

                if (column != null)
                {
                    column.setAutoIncrement(true);
                }
            }
        }
        finally
        {
            closeStatement(stmt);
        }
    }
}