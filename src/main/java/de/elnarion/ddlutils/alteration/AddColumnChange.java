package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.CloneHelper;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;

/**
 * Represents the addition of a column to a table.
 * 
 * @version $Revision: $
 */
public class AddColumnChange extends TableChangeImplBase
{
    /** The new column. */
    private Column _newColumn;
    /** The name of the column after which the new column should be added. */
    private String _previousColumnName;
    /** The name of the column before which the new column should be added. */
    private String _nextColumnName;

    /**
     * Creates a new change object.
     * 
     * @param tableName          The name of the table to add the column to
     * @param newColumn          The new column
     * @param previousColumnName The name of the column after which the new column should be added
     * @param nextColumnName     The name of the column before which the new column should be added
     */
    public AddColumnChange(String tableName, Column newColumn, String previousColumnName, String nextColumnName)
    {
        super(tableName);
        _newColumn          = newColumn;
        _previousColumnName = previousColumnName;
        _nextColumnName     = nextColumnName;
    }

    /**
     * Returns the new column.
     *
     * @return The new column
     */
    public Column getNewColumn()
    {
        return _newColumn;
    }

    /**
     * Returns the name of the column after which the new column should be added.
     *
     * @return The name of the previous column
     */
    public String getPreviousColumn()
    {
        return _previousColumnName;
    }

    /**
     * Returns the name of the column before which the new column should be added.
     *
     * @return The name of the next column
     */
    public String getNextColumn()
    {
        return _nextColumnName;
    }

    /**
     * Determines whether the column is added at the end (when applied in the order
     * of creation of the changes).
     * 
     * @return <code>true</code> if the column is added at the end
     */
    public boolean isAtEnd()
    {
        return _nextColumnName == null;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database model, boolean caseSensitive)
    {
        Table  table     = findChangedTable(model, caseSensitive);
        Column newColumn = new CloneHelper().clone(_newColumn, true);

        if (_previousColumnName != null)
        {
            Column prevColumn = table.findColumn(_previousColumnName, caseSensitive);
            int    idx        = table.getColumnIndex(prevColumn) + 1;

            table.addColumn(idx, newColumn);
        }
        else if (_nextColumnName != null)
        {
            table.addColumn(0, newColumn);
        }
        else
        {
            table.addColumn(newColumn);
        }
    }
}
