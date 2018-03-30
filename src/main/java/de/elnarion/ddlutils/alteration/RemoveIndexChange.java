package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Index;

/**
 * Represents the removal of an index from a table.
 * 
 * @version $Revision: $
 */
public class RemoveIndexChange extends IndexChangeImplBase
{
    /**
     * Creates a new change object.
     * 
     * @param tableName The name of the table to remove the index from
     * @param index     The index
     */
    public RemoveIndexChange(String tableName, Index index)
    {
        super(tableName, index);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database model, boolean caseSensitive)
    {
        findChangedTable(model, caseSensitive).removeIndex(findChangedIndex(model, caseSensitive));
    }
}
