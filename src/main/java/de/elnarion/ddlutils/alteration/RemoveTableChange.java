package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;

/**
 * Represents the removal of a table from a model.
 * 
 * TODO: this should be a model change
 * @version $Revision: $
 */
public class RemoveTableChange extends TableChangeImplBase
{
    /**
     * Creates a new change object.
     * 
     * @param tableName The name of the table to be removed
     */
    public RemoveTableChange(String tableName)
    {
        super(tableName);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database model, boolean caseSensitive)
    {
        Table table = findChangedTable(model, caseSensitive);

        model.removeTable(table);
    }
}
