package de.elnarion.ddlutils.platform.maxdb;

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
import de.elnarion.ddlutils.model.CascadeActionEnum;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.ForeignKey;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.ddlutils.model.TypeMap;
import de.elnarion.ddlutils.platform.SqlBuilder;
import de.elnarion.ddlutils.util.StringUtilsExt;

/**
 * The SQL Builder for MaxDB.
 * 
 * @version $Revision: $
 */
public class MaxDbBuilder extends SqlBuilder
{
    /**
     * Creates a new builder instance.
     * 
     * @param platform The plaftform this builder belongs to
     */
    public MaxDbBuilder(Platform platform)
    {
        super(platform);
        addEscapedCharSequence("'", "''");
    }

    /**
     * {@inheritDoc}
     */
    public void createPrimaryKey(Table table, Column[] primaryKeyColumns) throws IOException
    {
        if ((primaryKeyColumns.length > 0) && shouldGeneratePrimaryKeys(primaryKeyColumns))
        {
            print("ALTER TABLE ");
            printlnIdentifier(getTableName(table));
            printIndent();
            print("ADD CONSTRAINT ");
            printIdentifier(getConstraintName(null, table, "PK", null));
            print(" ");
            writePrimaryKeyStmt(table, primaryKeyColumns);
            printEndOfStatement();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void dropForeignKey(Table table, ForeignKey foreignKey) throws IOException
    {
        writeTableAlterStmt(table);
        print("DROP CONSTRAINT ");
        printIdentifier(getForeignKeyName(table, foreignKey));
        printEndOfStatement();
    }
    
    /**
     * {@inheritDoc}
     */
    public void dropTable(Table table) throws IOException
    { 
        print("DROP TABLE ");
        printIdentifier(getTableName(table));
        print(" CASCADE");
        printEndOfStatement();
    }

    /**
     * {@inheritDoc}
     */
    protected void writeColumnAutoIncrementStmt(Table table, Column column) throws IOException
    {
        print("DEFAULT SERIAL(1)");
    }


    /**
     * {@inheritDoc}
     */
    protected void writeForeignKeyOnDeleteAction(Table table, ForeignKey foreignKey) throws IOException
    {
        if (foreignKey.getOnDelete() != CascadeActionEnum.NONE) {
            super.writeForeignKeyOnDeleteAction(table, foreignKey);
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getSelectLastIdentityValues(Table table)
    {
        StringBuffer result = new StringBuffer();

        result.append("SELECT ");
        result.append(getDelimitedIdentifier(getTableName(table)));
        result.append(".CURRVAL FROM DUAL");
        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void addColumn(Database model, Table table, Column newColumn) throws IOException
    {
        print("ALTER TABLE ");
        printlnIdentifier(getTableName(table));
        printIndent();
        print("ADD ");
        writeColumn(table, newColumn);
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
        print("DROP ");
        printIdentifier(getColumnName(column));
        print(" RELEASE SPACE");
        printEndOfStatement();
    }

    /**
     * Writes the SQL to drop the primary key of the given table.
     * 
     * @param table The table
     * @throws IOException 
     */
    public void dropPrimaryKey(Table table) throws IOException
    {
        print("ALTER TABLE ");
        printlnIdentifier(getTableName(table));
        printIndent();
        print("DROP PRIMARY KEY");
        printEndOfStatement();
    }

    /**
     * Writes the SQL to set the required status of the given column.
     * 
     * @param table      The table
     * @param column     The column to change
     * @param isRequired Whether the column shall be required
     * @throws IOException 
     */
    public void changeColumnRequiredStatus(Table table, Column column, boolean isRequired) throws IOException
    {
        print("ALTER TABLE ");
        printlnIdentifier(getTableName(table));
        printIndent();
        print("COLUMN ");
        printIdentifier(getColumnName(column));
        if (isRequired)
        {
            print(" NOT NULL");
        }
        else
        {
            print(" DEFAULT NULL");
        }
        printEndOfStatement();
    }

    /**
     * Writes the SQL to set the default value of the given column.
     * 
     * @param table           The table
     * @param column          The column to change
     * @param newDefaultValue The new default value
     * @throws IOException 
     */
    public void changeColumnDefaultValue(Table table, Column column, String newDefaultValue) throws IOException
    {
        print("ALTER TABLE ");
        printlnIdentifier(getTableName(table));
        printIndent();
        print("COLUMN ");
        printIdentifier(getColumnName(column));

        boolean hasDefault = column.getParsedDefaultValue() != null;

        if (isValidDefaultValue(newDefaultValue, column.getTypeCode()))
        {
            if (hasDefault)
            {
                print(" ALTER DEFAULT ");
            }
            else
            {
                print(" ADD DEFAULT ");
            }
            printDefaultValue(newDefaultValue, column.getTypeCode());
        }
        else if (hasDefault)
        {
            print(" DROP DEFAULT");
        }
        printEndOfStatement();
    }

    /**
     * {@inheritDoc}
     */
    protected void writeCastExpression(Column sourceColumn, Column targetColumn) throws IOException
    {
        boolean charSizeChanged = TypeMap.isTextType(targetColumn.getTypeCode()) &&
                                  TypeMap.isTextType(targetColumn.getTypeCode()) &&
                                  ColumnDefinitionChange.isSizeChanged(getPlatformInfo(), sourceColumn, targetColumn) &&
                                  !StringUtilsExt.isEmpty(targetColumn.getSize());

        if (charSizeChanged)
        {
            print("SUBSTR(");
        }
        printIdentifier(getColumnName(sourceColumn));
        if (charSizeChanged)
        {
            print(",1,");
            print(targetColumn.getSize());
            print(")");
        }
    }
    
}
