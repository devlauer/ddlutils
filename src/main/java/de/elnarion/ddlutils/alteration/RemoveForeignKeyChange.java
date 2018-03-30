package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.ForeignKey;

/**
 * Represents the removal of a foreign key from a table. Note that for
 * simplicity and because it fits the model, this change actually implements
 * table change for the table that the foreign key originates.
 * 
 * @version $Revision: $
 */
public class RemoveForeignKeyChange extends ForeignKeyChangeImplBase
{
    /**
     * Creates a new change object.
     * 
     * @param tableName  The name of the table to remove the foreign key from
     * @param foreignKey The foreign key
     */
    public RemoveForeignKeyChange(String tableName, ForeignKey foreignKey)
    {
        super(tableName, foreignKey);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database model, boolean caseSensitive)
    {
        findChangedTable(model, caseSensitive).removeForeignKey(findChangedForeignKey(model, caseSensitive));
    }
}
