package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Index;
import de.elnarion.ddlutils.model.IndexColumn;
import de.elnarion.ddlutils.model.Table;

/**
 * Represents the addition of an index to a table.
 * 
 * @version $Revision: $
 */
public class AddIndexChange extends TableChangeImplBase
{
    /** The new index. */
    private Index _newIndex;

    /**
     * Creates a new change object.
     * 
     * @param tableName The name of the table to add the index to
     * @param newIndex  The new index
     */
    public AddIndexChange(String tableName, Index newIndex)
    {
        super(tableName);
        _newIndex = newIndex;
    }

    /**
     * Returns the new index.
     *
     * @return The new index
     */
    public Index getNewIndex()
    {
        return _newIndex;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database model, boolean caseSensitive)
    {
        Table table = findChangedTable(model, caseSensitive);

        table.addIndex(_newIndex);
        for (int idx = 0; idx < _newIndex.getColumnCount(); idx++)
        {
            IndexColumn idxColumn = _newIndex.getColumn(idx);
            Column      tmpColumn = idxColumn.getColumn();

            idxColumn.setColumn(table.findColumn(tmpColumn.getName(), caseSensitive));
        }
    }
}
