package de.elnarion.ddlutils.alteration;

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
import java.util.List;

import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Index;
import de.elnarion.ddlutils.model.Table;

/**
 * The base class for changes affecting indexes.
 * 
 * @version $Revision: $
 */
public abstract class IndexChangeImplBase extends    TableChangeImplBase
                                          implements IndexChange
{
    /** The names of the columns in the index. */
    private List<String> _columnNames = new ArrayList<>();

    /**
     * Creates a new change object.
     * 
     * @param tableName The name of the changed table
     * @param index     The index; note that this change object will not maintain a reference
     *                  to the index object
     */
    public IndexChangeImplBase(String tableName, Index index)
    {
        super(tableName);
        for (int colIdx = 0; colIdx < index.getColumnCount(); colIdx++)
        {
            _columnNames.add(index.getColumn(colIdx).getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    public Index findChangedIndex(Database model, boolean caseSensitive)
    {
        Table table = findChangedTable(model, caseSensitive);

        if (table != null)
        {
            for (int indexIdx = 0; indexIdx < table.getIndexCount(); indexIdx++)
            {
                Index curIndex = table.getIndex(indexIdx);

                if (curIndex.getColumnCount() == _columnNames.size())
                {
                    for (int colIdx = 0; colIdx < curIndex.getColumnCount(); colIdx++)
                    {
                        String curColName      = curIndex.getColumn(colIdx).getName();
                        String expectedColName = _columnNames.get(colIdx);

                        if ((caseSensitive  && curColName.equals(expectedColName)) ||
                            (!caseSensitive && curColName.equalsIgnoreCase(expectedColName)))
                        {
                            return curIndex;
                        }
                    }
                }
            }
        }
        return null;
    }
}
