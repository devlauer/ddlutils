package de.elnarion.ddlutils.platform.mssql;

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
import java.util.Iterator;
import java.util.List;

import de.elnarion.ddlutils.PlatformInfo;
import de.elnarion.ddlutils.alteration.AddForeignKeyChange;
import de.elnarion.ddlutils.alteration.AddIndexChange;
import de.elnarion.ddlutils.alteration.AddPrimaryKeyChange;
import de.elnarion.ddlutils.alteration.ColumnDefinitionChange;
import de.elnarion.ddlutils.alteration.ModelComparator;
import de.elnarion.ddlutils.alteration.RemoveForeignKeyChange;
import de.elnarion.ddlutils.alteration.RemoveIndexChange;
import de.elnarion.ddlutils.alteration.RemovePrimaryKeyChange;
import de.elnarion.ddlutils.alteration.TableChange;
import de.elnarion.ddlutils.alteration.TableDefinitionChangesPredicate;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.ForeignKey;
import de.elnarion.ddlutils.model.Index;
import de.elnarion.ddlutils.model.Table;

/**
 * A model comparator customized for Sql Server.
 * 
 * @version $Revision: $
 */
public class MSSqlModelComparator extends ModelComparator
{
    /**
     * Creates a new Sql Server model comparator object.
     * 
     * @param platformInfo            The platform info
     * @param tableDefChangePredicate The predicate that defines whether tables changes are supported
     *                                by the platform or not; all changes are supported if this is null
     * @param caseSensitive           Whether comparison is case sensitive
     */
    public MSSqlModelComparator(PlatformInfo                    platformInfo,
                                TableDefinitionChangesPredicate tableDefChangePredicate,
                                boolean                         caseSensitive)
    {
        super(platformInfo, tableDefChangePredicate, caseSensitive);
        setGeneratePrimaryKeyChanges(false);
        setCanDropPrimaryKeyColumns(false);
    }

    /**
     * {@inheritDoc}
     */
    protected List<TableChange> checkForPrimaryKeyChanges(Database sourceModel,
                                             Table    sourceTable,
                                             Database intermediateModel,
                                             Table    intermediateTable,
                                             Database targetModel,
                                             Table    targetTable)
    {
        List<TableChange> changes = super.checkForPrimaryKeyChanges(sourceModel, sourceTable, intermediateModel, intermediateTable, targetModel, targetTable);

        // now we add pk changes if one of the pk columns was changed
        // we only need to do this if there is no other pk change (which can only be a remove or add change or both)
        if (changes.isEmpty())
        {
            List<Column> columns = getRelevantChangedColumns(sourceTable, targetTable);

            for (Iterator<Column> it = columns.iterator(); it.hasNext();)
            {
                Column targetColumn = (Column)it.next();

                if (targetColumn.isPrimaryKey())
                {
                    changes.add(new RemovePrimaryKeyChange(sourceTable.getName()));
                    changes.add(new AddPrimaryKeyChange(sourceTable.getName(), sourceTable.getPrimaryKeyColumnNames()));
                    break;
                }
            }
        }
        return changes;
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
        List<TableChange>    changes           = super.checkForRemovedIndexes(sourceModel, sourceTable, intermediateModel, intermediateTable, targetModel, targetTable);
        Index[] targetIndexes     = targetTable.getIndices();
        List<RemoveIndexChange>    additionalChanges = new ArrayList<>();

        // removing all indexes that are maintained and that use a changed column
        if (targetIndexes.length > 0)
        {
            List<Column> columns = getRelevantChangedColumns(sourceTable, targetTable);

            if (!columns.isEmpty())
            {
                for (int indexIdx = 0; indexIdx < targetIndexes.length; indexIdx++)
                {
                    Index sourceIndex = findCorrespondingIndex(sourceTable, targetIndexes[indexIdx]);

                    if (sourceIndex != null)
                    {
                        for (Iterator<Column> columnIt = columns.iterator(); columnIt.hasNext();)
                        {
                            Column targetColumn = (Column)columnIt.next();

                            if (targetIndexes[indexIdx].hasColumn(targetColumn))
                            {
                                additionalChanges.add(new RemoveIndexChange(intermediateTable.getName(), targetIndexes[indexIdx]));
                                break;
                            }
                        }
                    }
                }
                for (Iterator<RemoveIndexChange> changeIt = additionalChanges.iterator(); changeIt.hasNext();)
                {
                    ((RemoveIndexChange)changeIt.next()).apply(intermediateModel, isCaseSensitive());
                }
                changes.addAll(additionalChanges);
            }
        }
        return changes;
    }

    /**
     * {@inheritDoc}
     */
    protected List<TableChange> checkForAddedIndexes(Database sourceModel,
                                        Table    sourceTable,
                                        Database intermediateModel,
                                        Table    intermediateTable,
                                        Database targetModel,
                                        Table    targetTable)
    {
        List<TableChange>    changes           = super.checkForAddedIndexes(sourceModel, sourceTable, intermediateModel, intermediateTable, targetModel, targetTable);
        Index[] targetIndexes     = targetTable.getIndices();
        List<AddIndexChange>    additionalChanges = new ArrayList<>();

        // re-adding all indexes that are maintained and that use a changed column
        if (targetIndexes.length > 0)
        {
            for (int indexIdx = 0; indexIdx < targetIndexes.length; indexIdx++)
            {
                Index sourceIndex       = findCorrespondingIndex(sourceTable, targetIndexes[indexIdx]);
                Index intermediateIndex = findCorrespondingIndex(intermediateTable, targetIndexes[indexIdx]);

                if ((sourceIndex != null) && (intermediateIndex == null))
                {
                    additionalChanges.add(new AddIndexChange(intermediateTable.getName(), targetIndexes[indexIdx]));
                }
            }
            for (Iterator<AddIndexChange> changeIt = additionalChanges.iterator(); changeIt.hasNext();)
            {
                ((AddIndexChange)changeIt.next()).apply(intermediateModel, isCaseSensitive());
            }
            changes.addAll(additionalChanges);
        }
        return changes;
    }

    /**
     * {@inheritDoc}
     */
    protected List<RemoveForeignKeyChange> checkForRemovedForeignKeys(Database sourceModel,
                                              Database intermediateModel,
                                              Database targetModel)
    {
        List<RemoveForeignKeyChange> changes           = super.checkForRemovedForeignKeys(sourceModel, intermediateModel, targetModel);
        List<RemoveForeignKeyChange> additionalChanges = new ArrayList<>();

        // removing all foreign keys that are maintained and that use a changed column
        for (int tableIdx = 0; tableIdx < targetModel.getTableCount(); tableIdx++)
        {
            Table targetTable = targetModel.getTable(tableIdx);
            Table sourceTable = sourceModel.findTable(targetTable.getName(), isCaseSensitive());

            if (sourceTable != null)
            {
                List<Column> columns = getRelevantChangedColumns(sourceTable, targetTable);

                if (!columns.isEmpty())
                {
                    for (int fkIdx = 0; fkIdx < targetTable.getForeignKeyCount(); fkIdx++)
                    {
                        ForeignKey targetFk = targetTable.getForeignKey(fkIdx);
                        ForeignKey sourceFk = findCorrespondingForeignKey(sourceTable, targetFk);

                        if (sourceFk != null)
                        {
                            for (Iterator<Column> columnIt = columns.iterator(); columnIt.hasNext();)
                            {
                                Column targetColumn = (Column)columnIt.next();

                                if (targetFk.hasLocalColumn(targetColumn) || targetFk.hasForeignColumn(targetColumn))
                                {
                                    additionalChanges.add(new RemoveForeignKeyChange(sourceTable.getName(), targetFk));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (Iterator<RemoveForeignKeyChange> changeIt = additionalChanges.iterator(); changeIt.hasNext();)
        {
            ((RemoveForeignKeyChange)changeIt.next()).apply(intermediateModel, isCaseSensitive());
        }
        changes.addAll(additionalChanges);
        return changes;
    }

    /**
     * {@inheritDoc}
     */
    protected List<AddForeignKeyChange> checkForAddedForeignKeys(Database sourceModel,
                                            Database intermediateModel,
                                            Database targetModel)
    {
        List<AddForeignKeyChange> changes           = super.checkForAddedForeignKeys(sourceModel, intermediateModel, targetModel);
        List<AddForeignKeyChange> additionalChanges = new ArrayList<>();

        // re-adding all foreign keys that are maintained and that use a changed column
        for (int tableIdx = 0; tableIdx < targetModel.getTableCount(); tableIdx++)
        {
            Table targetTable       = targetModel.getTable(tableIdx);
            Table sourceTable       = sourceModel.findTable(targetTable.getName(), isCaseSensitive());
            Table intermediateTable = intermediateModel.findTable(targetTable.getName(), isCaseSensitive());

            if (sourceTable != null)
            {
                for (int fkIdx = 0; fkIdx < targetTable.getForeignKeyCount(); fkIdx++)
                {
                    ForeignKey targetFk       = targetTable.getForeignKey(fkIdx);
                    ForeignKey sourceFk       = findCorrespondingForeignKey(sourceTable, targetFk);
                    ForeignKey intermediateFk = findCorrespondingForeignKey(intermediateTable, targetFk);

                    if ((sourceFk != null) && (intermediateFk == null))
                    {
                        additionalChanges.add(new AddForeignKeyChange(intermediateTable.getName(), targetFk));
                    }
                }
            }
        }
        for (Iterator<AddForeignKeyChange> changeIt = additionalChanges.iterator(); changeIt.hasNext();)
        {
            ((AddForeignKeyChange)changeIt.next()).apply(intermediateModel, isCaseSensitive());
        }
        changes.addAll(additionalChanges);
        return changes;
    }

    /**
     * Returns all columns that are changed in a way that makes it necessary to recreate foreign keys and
     * indexes using them.
     *  
     * @param sourceTable The source table
     * @param targetTable The target table
     * @return The columns (from the target table)
     */
    private List<Column> getRelevantChangedColumns(Table sourceTable, Table targetTable)
    {
        List<Column> result = new ArrayList<>();

        for (int columnIdx = 0; columnIdx < targetTable.getColumnCount(); columnIdx++)
        {
            Column targetColumn = targetTable.getColumn(columnIdx);
            Column sourceColumn = sourceTable.findColumn(targetColumn.getName(), isCaseSensitive());

            if (sourceColumn != null)
            {
                int targetTypeCode = getPlatformInfo().getTargetJdbcType(targetColumn.getTypeCode());

                if ((targetTypeCode != sourceColumn.getTypeCode()) ||
                    ColumnDefinitionChange.isSizeChanged(getPlatformInfo(), sourceColumn, targetColumn))
                {
                    result.add(targetColumn);
                }
            }
        }
        return result;
    }
}
