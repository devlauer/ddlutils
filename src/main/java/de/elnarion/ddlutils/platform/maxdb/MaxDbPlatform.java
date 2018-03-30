package de.elnarion.ddlutils.platform.maxdb;

import java.io.IOException;
import java.sql.Types;

import de.elnarion.ddlutils.PlatformInfo;
import de.elnarion.ddlutils.alteration.AddColumnChange;
import de.elnarion.ddlutils.alteration.AddPrimaryKeyChange;
import de.elnarion.ddlutils.alteration.ColumnDefinitionChange;
import de.elnarion.ddlutils.alteration.ModelComparator;
import de.elnarion.ddlutils.alteration.PrimaryKeyChange;
import de.elnarion.ddlutils.alteration.RemoveColumnChange;
import de.elnarion.ddlutils.alteration.RemovePrimaryKeyChange;
import de.elnarion.ddlutils.alteration.TableChange;
import de.elnarion.ddlutils.alteration.TableDefinitionChangesPredicate;
import de.elnarion.ddlutils.model.CascadeActionEnum;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.ddlutils.platform.CreationParameters;
import de.elnarion.ddlutils.platform.DefaultTableDefinitionChangesPredicate;
import de.elnarion.ddlutils.platform.PlatformImplBase;
import de.elnarion.ddlutils.util.StringUtilsExt;


/**
 * The platform implementation for MaxDB. It is currently identical to the SapDB
 * implementation as there is no difference in the functionality we're using.
 * Note that DdlUtils is currently not able to distinguish them based on the
 * jdbc driver or subprotocol as they are identical.   
 * 
 * @version $Revision: 231306 $
 */
public class MaxDbPlatform extends PlatformImplBase
{
    /** Database name of this platform. */
    public static final String DATABASENAME = "MaxDB";

    /**
     * Creates a new platform instance.
     */
    public MaxDbPlatform()
    {
        PlatformInfo info = getPlatformInfo();

        info.setMaxIdentifierLength(32);
        info.setPrimaryKeyColumnAutomaticallyRequired(true);
        info.setMultipleIdentityColumnsSupported(false);
        info.setCommentPrefix("/*");
        info.setCommentSuffix("*/");
        info.setSupportedOnDeleteActions(new CascadeActionEnum[] { CascadeActionEnum.CASCADE, CascadeActionEnum.RESTRICT, CascadeActionEnum.SET_DEFAULT, CascadeActionEnum.SET_NULL, CascadeActionEnum.NONE });
        info.addEquivalentOnDeleteActions(CascadeActionEnum.NONE, CascadeActionEnum.RESTRICT);
        info.setSupportedOnUpdateActions(new CascadeActionEnum[] { CascadeActionEnum.NONE });
        info.addEquivalentOnUpdateActions(CascadeActionEnum.NONE, CascadeActionEnum.RESTRICT);

        // BIGINT is also handled by the model reader
        // Unfortunately there is no way to distinguish between REAL, and FLOAT/DOUBLE when
        // reading back via JDBC, because they all have the same size of 8
        info.addNativeTypeMapping(Types.ARRAY,         "LONG BYTE",       Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.BIGINT,        "FIXED(38,0)");
        info.addNativeTypeMapping(Types.BINARY,        "CHAR{0} BYTE");
        info.addNativeTypeMapping(Types.BIT,           "BOOLEAN");
        info.addNativeTypeMapping(Types.BLOB,          "LONG BYTE",       Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.BOOLEAN,       "BOOLEAN",         Types.BIT);
        info.addNativeTypeMapping(Types.CLOB,          "LONG",            Types.LONGVARCHAR);
        info.addNativeTypeMapping(Types.DATALINK,      "LONG BYTE",       Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.DECIMAL,       "FIXED");
        info.addNativeTypeMapping(Types.DISTINCT,      "LONG BYTE",       Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.DOUBLE,        "FLOAT(38)",       Types.FLOAT);
        info.addNativeTypeMapping(Types.FLOAT,         "FLOAT(38)");
        info.addNativeTypeMapping(Types.JAVA_OBJECT,   "LONG BYTE",       Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.LONGVARBINARY, "LONG BYTE");
        info.addNativeTypeMapping(Types.LONGVARCHAR,   "LONG");
        info.addNativeTypeMapping(Types.NULL,          "LONG BYTE",       Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.NUMERIC,       "FIXED",           Types.DECIMAL);
        info.addNativeTypeMapping(Types.OTHER,         "LONG BYTE",       Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.REAL,          "FLOAT(16)",       Types.FLOAT);
        info.addNativeTypeMapping(Types.REF,           "LONG BYTE",       Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.STRUCT,        "LONG BYTE",       Types.LONGVARBINARY);
        info.addNativeTypeMapping(Types.TINYINT,       "SMALLINT",        Types.SMALLINT);
        info.addNativeTypeMapping(Types.VARBINARY,     "VARCHAR{0} BYTE");

        info.setDefaultSize(Types.CHAR,      254);
        info.setDefaultSize(Types.VARCHAR,   254);
        info.setDefaultSize(Types.BINARY,    254);
        info.setDefaultSize(Types.VARBINARY, 254);

        setSqlBuilder(new MaxDbBuilder(this));
        setModelReader(new MaxDbModelReader(this));
    }

    
    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return DATABASENAME;
    }

    /**
     * Returns the model comparator for this platform.
     * 
     * @return The comparator
     */
    protected ModelComparator getModelComparator()
    {
        ModelComparator comparator = super.getModelComparator();

        comparator.setCanDropPrimaryKeyColumns(false);
        comparator.setGeneratePrimaryKeyChanges(false);
        return comparator;
    }

    /**
     * {@inheritDoc}
     */
    protected TableDefinitionChangesPredicate getTableDefinitionChangesPredicate()
    {
        return new DefaultTableDefinitionChangesPredicate()
        {
            protected boolean isSupported(Table intermediateTable, TableChange change)
            {
                if ((change instanceof RemoveColumnChange) ||
                    (change instanceof AddPrimaryKeyChange) ||
                    (change instanceof PrimaryKeyChange) ||
                    (change instanceof RemovePrimaryKeyChange))
                {
                    return true;
                }
                else if (change instanceof AddColumnChange) 
                {
                    AddColumnChange addColumnChange = (AddColumnChange)change;

                    // SapDB can only add not insert columns, and required columns have to have
                    // a default value or be IDENTITY
                    return (addColumnChange.getNextColumn() == null) &&
                           (!addColumnChange.getNewColumn().isRequired() ||
                            !StringUtilsExt.isEmpty(addColumnChange.getNewColumn().getDefaultValue()));
                }
                else if (change instanceof ColumnDefinitionChange)
                {
                    ColumnDefinitionChange colChange = (ColumnDefinitionChange)change;

                    // SapDB has a ALTER TABLE MODIFY COLUMN but it is limited regarding the type conversions
                    // it can perform, so we don't use it here but rather rebuild the table
                    Column curColumn = intermediateTable.findColumn(colChange.getChangedColumn(), isDelimitedIdentifierModeOn());
                    Column newColumn = colChange.getNewColumn();

                    // we can however handle the change if only the default value or the required status was changed
                    return ((curColumn.getTypeCode() == newColumn.getTypeCode()) &&
                           !ColumnDefinitionChange.isSizeChanged(getPlatformInfo(), curColumn, newColumn) &&
                           (curColumn.isAutoIncrement() == newColumn.isAutoIncrement()));
                }
                else
                {
                    return false;
                }
            }
        };
    }

    /**
     * Processes the removal of a column from a table.
     * 
     * @param currentModel The current database schema
     * @param params       The parameters used in the creation of new tables. Note that for existing
     *                     tables, the parameters won't be applied
     * @param change       The change object
     * @throws IOException 
     */
    public void processChange(Database           currentModel,
                              CreationParameters params,
                              RemoveColumnChange change) throws IOException
    {
        Table  changedTable  = findChangedTable(currentModel, change);
        Column removedColumn = changedTable.findColumn(change.getChangedColumn(), isDelimitedIdentifierModeOn());

        ((MaxDbBuilder)getSqlBuilder()).dropColumn(changedTable, removedColumn);
        change.apply(currentModel, isDelimitedIdentifierModeOn());
    }

    /**
     * Processes the removal of a primary key from a table.
     * 
     * @param currentModel The current database schema
     * @param params       The parameters used in the creation of new tables. Note that for existing
     *                     tables, the parameters won't be applied
     * @param change       The change object
     * @throws IOException 
     */
    public void processChange(Database               currentModel,
                              CreationParameters     params,
                              RemovePrimaryKeyChange change) throws IOException
    {
        Table changedTable = findChangedTable(currentModel, change);

        ((MaxDbBuilder)getSqlBuilder()).dropPrimaryKey(changedTable);
        change.apply(currentModel, isDelimitedIdentifierModeOn());
    }

    /**
     * Processes the change of the primary key of a table.
     * 
     * @param currentModel The current database schema
     * @param params       The parameters used in the creation of new tables. Note that for existing
     *                     tables, the parameters won't be applied
     * @param change       The change object
     * @throws IOException 
     */
    public void processChange(Database           currentModel,
                              CreationParameters params,
                              PrimaryKeyChange   change) throws IOException
    {
        Table    changedTable     = findChangedTable(currentModel, change);
        String[] newPKColumnNames = change.getNewPrimaryKeyColumns();
        Column[] newPKColumns     = new Column[newPKColumnNames.length];

        for (int colIdx = 0; colIdx < newPKColumnNames.length; colIdx++)
        {
            newPKColumns[colIdx] = changedTable.findColumn(newPKColumnNames[colIdx], isDelimitedIdentifierModeOn());
        }
        
        ((MaxDbBuilder)getSqlBuilder()).dropPrimaryKey(changedTable);
        getSqlBuilder().createPrimaryKey(changedTable, newPKColumns);
        change.apply(currentModel, isDelimitedIdentifierModeOn());
    }

    /**
     * Processes the change of the column of a table.
     * 
     * @param currentModel The current database schema
     * @param params       The parameters used in the creation of new tables. Note that for existing
     *                     tables, the parameters won't be applied
     * @param change       The change object
     * @throws IOException 
     */
    public void processChange(Database               currentModel,
                              CreationParameters     params,
                              ColumnDefinitionChange change) throws IOException
    {
        Table  changedTable  = findChangedTable(currentModel, change);
        Column changedColumn = changedTable.findColumn(change.getChangedColumn(), isDelimitedIdentifierModeOn());

        if (!StringUtilsExt.equals(changedColumn.getDefaultValue(), change.getNewColumn().getDefaultValue()))
        {
            ((MaxDbBuilder)getSqlBuilder()).changeColumnDefaultValue(changedTable,
                                                                  changedColumn,
                                                                  change.getNewColumn().getDefaultValue());
        }
        if (changedColumn.isRequired() != change.getNewColumn().isRequired())
        {
            ((MaxDbBuilder)getSqlBuilder()).changeColumnRequiredStatus(changedTable,
                                                                    changedColumn,
                                                                    change.getNewColumn().isRequired());
        }
        change.apply(currentModel, isDelimitedIdentifierModeOn());
    }
    
}
