package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.Database;

/**
 * Represents the removal of a column from a table.
 * 
 * @version $Revision: $
 */
public class RemoveColumnChange extends ColumnChangeImplBase
{
    /**
     * Creates a new change object.
     * 
     * @param tableName  The name of the table to remove the column from
     * @param columnName The column's name
     */
    public RemoveColumnChange(String tableName, String columnName)
    {
        super(tableName, columnName);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database model, boolean caseSensitive)
    {
        findChangedTable(model, caseSensitive).removeColumn(findChangedColumn(model, caseSensitive));
    }
}
