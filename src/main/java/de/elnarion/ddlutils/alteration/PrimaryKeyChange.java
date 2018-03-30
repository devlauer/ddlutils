package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;

/**
 * Represents the change of the primary key of a table.
 * 
 * @version $Revision: $
 */
public class PrimaryKeyChange extends TableChangeImplBase
{
    /** The names of the columns making up the new primary key. */
    private String[] _newPrimaryKeyColumns;

    /**
     * Creates a new change object.
     * 
     * @param tableName            The name of the table whose primary key is to be changed
     * @param newPrimaryKeyColumns The names of the columns making up the new primary key
     */
    public PrimaryKeyChange(String tableName, String[] newPrimaryKeyColumns)
    {
        super(tableName);
        if (newPrimaryKeyColumns == null)
        {
            _newPrimaryKeyColumns = new String[0];
        }
        else
        {
            _newPrimaryKeyColumns = new String[newPrimaryKeyColumns.length];

            System.arraycopy(newPrimaryKeyColumns, 0, _newPrimaryKeyColumns, 0, newPrimaryKeyColumns.length);
        }
    }

    /**
     * Returns the names of the columns making up the new primary key.
     *
     * @return The column names
     */
    public String[] getNewPrimaryKeyColumns()
    {
        String[] result = new String[_newPrimaryKeyColumns.length];

        System.arraycopy(_newPrimaryKeyColumns, 0, result, 0, _newPrimaryKeyColumns.length);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database model, boolean caseSensitive)
    {
        Table    table  = findChangedTable(model, caseSensitive);
        Column[] pkCols = table.getPrimaryKeyColumns();

        for (int idx = 0; idx < pkCols.length; idx++)
        {
            pkCols[idx].setPrimaryKey(false);
        }
        for (int idx = 0; idx < _newPrimaryKeyColumns.length; idx++)
        {
            Column column = table.findColumn(_newPrimaryKeyColumns[idx], caseSensitive);

            column.setPrimaryKey(true);
        }
    }
}
