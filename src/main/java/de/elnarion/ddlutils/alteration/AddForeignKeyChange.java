package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.ForeignKey;

/**
 * Represents the addition of a foreign key to a table. Note that for
 * simplicity and because it fits the model, this change actually implements
 * table change for the table that the new foreign key will originate.
 * 
 * @version $Revision: $
 */
public class AddForeignKeyChange extends TableChangeImplBase
{
    /** The new foreign key. */
    private ForeignKey _newForeignKey;

    /**
     * Creates a new change object.
     * 
     * @param tableName     The name of the table to add the foreign key to
     * @param newForeignKey The new foreign key
     */
    public AddForeignKeyChange(String tableName, ForeignKey newForeignKey)
    {
        super(tableName);
        _newForeignKey = newForeignKey;
    }

    /**
     * Returns the new foreign key.
     *
     * @return The new foreign key
     */
    public ForeignKey getNewForeignKey()
    {
        return _newForeignKey;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database database, boolean caseSensitive)
    {
        findChangedTable(database, caseSensitive).addForeignKey(_newForeignKey);
    }
}
