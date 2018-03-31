package de.elnarion.ddlutils.platform.hsqldb;

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

import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.regex.Matcher;
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
 * Reads a database model from a HsqlDb database.
 *
 * @version $Revision: $
 */
public class HsqlDbModelReader extends JdbcModelReader
{

	/** The regular expression pattern for the time values that hsql returns. */
	private Pattern _hsqldbTimePattern;
	/** The regular expression pattern for the timestamp values that hsql returns. */
	private Pattern _hsqldbTimestampPattern;
	
	
    /**
     * Creates a new model reader for HsqlDb databases.
     * 
     * @param platform The platform that this model reader belongs to
     */
    public HsqlDbModelReader(Platform platform)
    {
        super(platform);
        setDefaultCatalogPattern(null);
        setDefaultSchemaPattern(null);
        _hsqldbTimePattern = Pattern.compile("'(\\d{2}):(\\d{2}):(\\d{2})(\\.\\d{1,8})?'");
		_hsqldbTimestampPattern = Pattern.compile("TIMESTAMP'(\\d{4}\\-\\d{2}\\-\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})(\\.\\d{1,8})?'");
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
            // For at least version 1.7.2 we have to determine the auto-increment columns
            // from a result set meta data because the database does not put this info
            // into the database metadata
            // Since Hsqldb only allows IDENTITY for primary key columns, we restrict
            // our search to those columns
            determineAutoIncrementFromResultSetMetaData(table, table.getPrimaryKeyColumns());
        }
        
        return table;
    }

    /**
     * {@inheritDoc}
     */
    protected Column readColumn(DatabaseMetaDataWrapper metaData, Map<String, Object> values) throws SQLException
    {
        Column column = super.readColumn(metaData, values);

        if (column.getDefaultValue() != null)
		{
			if (column.getTypeCode() == Types.TIME)
			{
				Matcher matcher = _hsqldbTimePattern.matcher(column.getDefaultValue());

				if (matcher.matches())
				{
					StringBuffer newDefault = new StringBuffer();

					newDefault.append("'");
					// the hour
					newDefault.append(matcher.group(1));
					newDefault.append(":");
					// the minute
					newDefault.append(matcher.group(2));
					newDefault.append(":");
					// the second
					newDefault.append(matcher.group(3));
					newDefault.append("'");

					column.setDefaultValue(newDefault.toString());
				}
			}
			else if (column.getTypeCode() == Types.TIMESTAMP)
			{
                Matcher matcher = _hsqldbTimestampPattern.matcher(column.getDefaultValue());

				if (matcher.matches())
				{
					StringBuffer newDefault = new StringBuffer();

					newDefault.append("'");
					// group 1 is the date which has the correct format
					newDefault.append(matcher.group(1));
					newDefault.append(" ");
					// the hour
					newDefault.append(matcher.group(2));
					newDefault.append(":");
					// the minute
					newDefault.append(matcher.group(3));
					newDefault.append(":");
					// the second
					newDefault.append(matcher.group(4));
					// optionally, the fraction
					if ((matcher.groupCount() >= 5) && (matcher.group(5) != null))
					{
						newDefault.append(matcher.group(5));
					}
					newDefault.append("'");

					column.setDefaultValue(newDefault.toString());
				}
			}
		}
        
        if (TypeMap.isTextType(column.getTypeCode()) &&
            (column.getDefaultValue() != null))
        {
            column.setDefaultValue(unescape(column.getDefaultValue(), "'", "''"));
        }
        return column;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isInternalForeignKeyIndex(DatabaseMetaDataWrapper metaData, Table table, ForeignKey fk, Index index)
    {
        String name = index.getName();

        return (name != null) && name.startsWith("SYS_IDX_");
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isInternalPrimaryKeyIndex(DatabaseMetaDataWrapper metaData, Table table, Index index)
    {
        String name = index.getName();

        return (name != null) && (name.startsWith("SYS_PK_") || name.startsWith("SYS_IDX_"));
    }
}
