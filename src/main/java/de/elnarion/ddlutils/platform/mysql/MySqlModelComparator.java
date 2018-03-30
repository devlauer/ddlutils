package de.elnarion.ddlutils.platform.mysql;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.elnarion.ddlutils.PlatformInfo;
import de.elnarion.ddlutils.alteration.AddForeignKeyChange;
import de.elnarion.ddlutils.alteration.ColumnDefinitionChange;
import de.elnarion.ddlutils.alteration.ModelComparator;
import de.elnarion.ddlutils.alteration.RemoveForeignKeyChange;
import de.elnarion.ddlutils.alteration.RemoveIndexChange;
import de.elnarion.ddlutils.alteration.TableChange;
import de.elnarion.ddlutils.alteration.TableDefinitionChangesPredicate;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.ForeignKey;
import de.elnarion.ddlutils.model.Index;
import de.elnarion.ddlutils.model.Reference;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.ddlutils.util.StringUtilsExt;

/**
 * A model comparator customized for MySql.
 * 
 * @version $Revision: $
 */
public class MySqlModelComparator extends ModelComparator
{
    /**
     * Creates a new MySql model comparator object.
     * 
     * @param platformInfo            The platform info
     * @param tableDefChangePredicate The predicate that defines whether tables changes are supported
     *                                by the platform or not; all changes are supported if this is null
     * @param caseSensitive           Whether comparison is case sensitive
     */
    public MySqlModelComparator(PlatformInfo                    platformInfo,
                                TableDefinitionChangesPredicate tableDefChangePredicate,
                                boolean                         caseSensitive)
    {
        super(platformInfo, tableDefChangePredicate, caseSensitive);
    }

    /**
     * {@inheritDoc}
     */
    protected List<TableChange> checkForRemovedIndexes(Database sourceModel,
                                          Table    sourceTable,
                                          Database intermediateModel,
                                          Table    intermediateTable,
                                          Database targetModel,
                                          Table    targetTable)
    {
        // Handling for http://bugs.mysql.com/bug.php?id=21395: we need to drop and then recreate FKs that reference columns
        // included in indexes that will be dropped
        List<TableChange> changes     = super.checkForRemovedIndexes(sourceModel, sourceTable, intermediateModel, intermediateTable, targetModel, targetTable);
        Set<String>  columnNames = new HashSet<>();

        for (Iterator<TableChange> it = changes.iterator(); it.hasNext();)
        {
            RemoveIndexChange change = (RemoveIndexChange)it.next();
            Index             index  = change.findChangedIndex(sourceModel, isCaseSensitive());

            for (int colIdx = 0; colIdx < index.getColumnCount(); colIdx++)
            {
                columnNames.add(index.getColumn(colIdx).getName());
            }
        }
        if (!columnNames.isEmpty())
        {
            // this is only relevant if the columns are referenced by foreign keys in the same table
            changes.addAll(getForeignKeyRecreationChanges(intermediateTable, targetTable, columnNames));
        }
        return changes;
    }

    /**
     * {@inheritDoc}
     */
    protected List<TableChange> compareTables(Database sourceModel,
                                 Table    sourceTable,
                                 Database intermediateModel,
                                 Table    intermediateTable,
                                 Database targetModel, Table targetTable)
    {
        // we need to drop and recreate foreign keys that reference columns whose data type will be changed (but not size)
        List<TableChange> changes     = super.compareTables(sourceModel, sourceTable, intermediateModel, intermediateTable, targetModel, targetTable);
        Set<String>  columnNames = new HashSet<>();

        for (Iterator<TableChange> it = changes.iterator(); it.hasNext();)
        {
            Object change = it.next();

            if (change instanceof ColumnDefinitionChange)
            {
                ColumnDefinitionChange colDefChange = (ColumnDefinitionChange)change;
                Column                 sourceColumn = sourceTable.findColumn(colDefChange.getChangedColumn(), isCaseSensitive());

                if (ColumnDefinitionChange.isTypeChanged(getPlatformInfo(), sourceColumn, colDefChange.getNewColumn()))
                {
                    columnNames.add(sourceColumn.getName());
                }
            }
        }
        if (!columnNames.isEmpty())
        {
            // we don't need to check for foreign columns as the data type of both local and foreign column need
            // to be changed, otherwise MySql will complain
            changes.addAll(getForeignKeyRecreationChanges(intermediateTable, targetTable, columnNames));
        }
        return changes;
    }

    /**
     * Returns remove and add changes for the foreign keys that reference the indicated columns as a local column.
     * 
     * @param intermediateTable The intermediate table
     * @param targetTable       The target table
     * @param columnNames       The names of the columns to look for
     * @return The additional changes
     */
    private List<TableChange> getForeignKeyRecreationChanges(Table intermediateTable, Table targetTable, Set<String> columnNames)
    {
        List<TableChange> newChanges = new ArrayList<>();

        for (int fkIdx = 0; fkIdx < targetTable.getForeignKeyCount(); fkIdx++)
        {
            ForeignKey targetFk       = targetTable.getForeignKey(fkIdx);
            ForeignKey intermediateFk = intermediateTable.findForeignKey(targetFk, isCaseSensitive());

            if (intermediateFk != null)
            {
                for (int refIdx = 0; refIdx < intermediateFk.getReferenceCount(); refIdx++)
                {
                    Reference ref = intermediateFk.getReference(refIdx);

                    for (Iterator<String> colNameIt = columnNames.iterator(); colNameIt.hasNext();)
                    {
                        if (StringUtilsExt.equals(ref.getLocalColumnName(), (String)colNameIt.next(), isCaseSensitive()))
                        {
                            newChanges.add(new RemoveForeignKeyChange(intermediateTable.getName(), intermediateFk));
                            newChanges.add(new AddForeignKeyChange(intermediateTable.getName(), intermediateFk));
                        }
                    }
                }
            }
        }
        return newChanges;
    }
}