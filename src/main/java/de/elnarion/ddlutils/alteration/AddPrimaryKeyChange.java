package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;

/**
 * Represents the addition of a primary key to a table which does not have one.
 * 
 * @version $Revision: $
 */
public class AddPrimaryKeyChange extends TableChangeImplBase
{
    /** The names of the columns making up the primary key. */
    private String[] _primaryKeyColumns;

    /**
     * Creates a new change object.
     * 
     * @param tableName         The name of the table to add the primary key to
     * @param primaryKeyColumns The names of the columns making up the primary key
     */
    public AddPrimaryKeyChange(String tableName, String[] primaryKeyColumns)
    {
        super(tableName);
        if (primaryKeyColumns == null)
        {
            _primaryKeyColumns = new String[0];
        }
        else
        {
            _primaryKeyColumns = new String[primaryKeyColumns.length];

            System.arraycopy(primaryKeyColumns, 0, _primaryKeyColumns, 0, primaryKeyColumns.length);
        }
    }

    /**
     * Returns the primary key column names making up the new primary key.
     *
     * @return The primary key column names
     */
    public String[] getPrimaryKeyColumns()
    {
        String[] result = new String[_primaryKeyColumns.length];

        System.arraycopy(_primaryKeyColumns, 0, result, 0, _primaryKeyColumns.length);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database model, boolean caseSensitive)
    {
        Table table = findChangedTable(model, caseSensitive);

        for (int idx = 0; idx < _primaryKeyColumns.length; idx++)
        {
            Column column = table.findColumn(_primaryKeyColumns[idx], caseSensitive);

            column.setPrimaryKey(true);
        }
    }
}
