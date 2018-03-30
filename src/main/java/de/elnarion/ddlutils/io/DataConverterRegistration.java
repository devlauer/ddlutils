package de.elnarion.ddlutils.io;

import de.elnarion.ddlutils.io.converters.SqlTypeConverter;
import de.elnarion.ddlutils.model.TypeMap;

/**
 * Represents the registration of a data converter for tasks that work on data files.
 * 
 * @version $Revision: 289996 $
 */
public class DataConverterRegistration
{
    /** The converter. */
    private SqlTypeConverter _converter;
    /** The sql type for which the converter shall be registered. */
    private int _typeCode = Integer.MIN_VALUE;
    /** The table name. */
    private String _table;
    /** The column name. */
    private String _column;

    /**
     * Returns the converter.
     *
     * @return The converter
     */
    public SqlTypeConverter getConverter()
    {
        return _converter;
    }

    /**
     * Sets the converter class.
     *
     * @param converterClassName The fully qualified converter class name
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public void setClassName(String converterClassName) throws InstantiationException, IllegalAccessException, ClassNotFoundException 
    {
        _converter = (SqlTypeConverter)getClass().getClassLoader().loadClass(converterClassName).newInstance();
    }

    /**
     * Returns the jdbc type.
     *
     * @return The jdbc type code
     */
    public int getTypeCode()
    {
        return _typeCode;
    }

    /**
     * Sets the jdbc type.
     *
     * @param jdbcTypeName The jdbc type name
     */
    public void setJdbcType(String jdbcTypeName) 
    {
        Integer typeCode = TypeMap.getJdbcTypeCode(jdbcTypeName);

        if (typeCode != null)
        {
            _typeCode = typeCode.intValue();
        }
    }

    /**
     * Returns the column for which this converter is defined.
     *
     * @return The column
     */
    public String getColumn()
    {
        return _column;
    }

    /**
     * Sets the column for which this converter is defined.
     *
     * @param column The column
     */
    public void setColumn(String column) 
    {
        if ((column == null) || (column.length() == 0))
        {
            throw new RuntimeException("Please specify a non-empty column name");
        }
        _column = column;
    }

    /**
     * Returns the table for whose column this converter is defined.
     *
     * @return The table
     */
    public String getTable()
    {
        return _table;
    }

    /**
     * Sets the table for whose column this converter is defined.
     *
     * @param table The table
     */
    public void setTable(String table) 
    {
        if ((table == null) || (table.length() == 0))
        {
            throw new RuntimeException("Please specify a non-empty table name");
        }
        _table = table;
    }

    
}
