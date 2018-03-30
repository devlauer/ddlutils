package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.CloneHelper;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;

/**
 * Represents the addition of a table to a model. Note that this change does not include foreign keys
 * originating from the new table.
 * 
 * @version $Revision: $
 */
public class AddTableChange implements ModelChange
{
    /** The new table. */
    private Table _newTable;

    /**
     * Creates a new change object.
     * 
     * @param newTable The new table; note that the change object will keep a reference to this table
     *                 which means that the table should not be changed after creating this change object
     */
    public AddTableChange(Table newTable)
    {
        _newTable = newTable;
    }

    /**
     * Returns the new table. Note that only the columns and table-level constraints are to be used.
     * Any model-level constraints (e.g. foreign keys) shall be ignored as there are different change
     * objects for them.
     * 
     * @return The new table
     */
    public Table getNewTable()
    {
        return _newTable;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database database, boolean caseSensitive)
    {
        Table table = new CloneHelper().clone(_newTable, true, false, database, caseSensitive);

        database.addTable(table);
    }
}
