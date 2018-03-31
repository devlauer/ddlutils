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

import de.elnarion.ddlutils.Platform;
import de.elnarion.ddlutils.alteration.ColumnDefinitionChange;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Index;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.ddlutils.model.TypeMap;
import de.elnarion.ddlutils.platform.SqlBuilder;


/**
 * The SQL Builder for the H2 database.
 *
 * @version $Revision:$
 */
public class H2Builder extends SqlBuilder
{
    /**
     * Creates a new builder instance.
     *
     * @param platform The plaftform this builder belongs to
     */
    public H2Builder(Platform platform)
    {
        super(platform);
        addEscapedCharSequence("'", "''");
    }

    /**
     * {@inheritDoc}
     */
    public void dropTable(Table table) throws IOException
    {
        print("DROP TABLE ");
        printIdentifier(getTableName(table));
        print(" IF EXISTS");
        printEndOfStatement();
    }

    /**
     * {@inheritDoc}
     */
    public String getSelectLastIdentityValues(Table table)
    {
        return "CALL IDENTITY()";
    }

    /**
     * {@inheritDoc}
     */
    public void dropIndex(Table table, Index index) throws IOException
    {
        print("DROP INDEX ");
        printIdentifier(getIndexName(index));
        print(" IF EXISTS");
        printEndOfStatement();
    }

    /**
     * Writes the SQL to add/insert a column.
     *
     * @param table      The table
     * @param newColumn  The new column
     * @param nextColumn The column before which the new column shall be added; <code>null</code>
     *                   if the new column is to be added instead of inserted
     * @throws IOException 
     */
    public void insertColumn(Table table, Column newColumn, Column nextColumn) throws IOException
    {
        print("ALTER TABLE ");
        printlnIdentifier(getTableName(table));
        printIndent();
        print("ADD COLUMN ");
        writeColumn(table, newColumn);
        if (nextColumn != null)
        {
            print(" BEFORE ");
            printIdentifier(getColumnName(nextColumn));
        }
        printEndOfStatement();
    }

    /**
     * Writes the SQL to drop a column.
     *
     * @param table  The table
     * @param column The column to drop
     * @throws IOException 
     */
    public void dropColumn(Table table, Column column) throws IOException
    {
        print("ALTER TABLE ");
        printlnIdentifier(getTableName(table));
        printIndent();
        print("DROP COLUMN ");
        printIdentifier(getColumnName(column));
        printEndOfStatement();
    }

    /**
     * {@inheritDoc}
     */
    protected void writeCastExpression(Column sourceColumn, Column targetColumn) throws IOException
    {
        boolean sizeChanged = ColumnDefinitionChange.isSizeChanged(getPlatformInfo(), sourceColumn, targetColumn);
        boolean typeChanged = ColumnDefinitionChange.isTypeChanged(getPlatformInfo(), sourceColumn, targetColumn);

        if (sizeChanged || typeChanged)
        {
            boolean needSubstr = false;
            int targetSize = 0;

            if (TypeMap.isTextType(targetColumn.getTypeCode()) && sizeChanged)
            {
                targetSize = targetColumn.getSizeAsInt();
                if (targetSize == 0)
                {
                    Integer platformDefaultSize = getPlatformInfo().getDefaultSize(targetColumn.getTypeCode());
                    if (platformDefaultSize != null)
                    {
                        targetSize = platformDefaultSize.intValue();
                    }
                }

                needSubstr = sourceColumn.getSizeAsInt() > targetSize;
            }

            if (needSubstr)
            {
                print("SUBSTR(");
            }
            print("CAST(");
            printIdentifier(getColumnName(sourceColumn));
            print(" AS ");
            if (needSubstr)
            {
                print(getNativeType(targetColumn));
            }
            else
            {
                print(getSqlType(targetColumn));
            }
            print(")");
            if (needSubstr)
            {
                print(",1,");
                print(Integer.toString(targetSize));
                print(")");
            }
        }
        else
        {
            super.writeCastExpression(sourceColumn, targetColumn);
        }
    }
}