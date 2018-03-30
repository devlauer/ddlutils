package de.elnarion.ddlutils.alteration;

import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.ForeignKey;

/**
 * Represents a change to a foreign key of a table.
 * 
 * @version $Revision: $
 */
public interface ForeignKeyChange extends TableChange
{
    /**
     * Finds the foreign key object corresponding to the changed foreign key in the given database model.
     * 
     * @param model         The database model
     * @param caseSensitive Whether identifiers are case sensitive
     * @return The foreign key object or <code>null</code> if it could not be found
     */
    public ForeignKey findChangedForeignKey(Database model, boolean caseSensitive);
}
