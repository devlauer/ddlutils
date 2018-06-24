/*******************************************************************************
 * Copyright 2018 dev.lauer@elnarion.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.elnarion.ddlutils.platform.oracle;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import de.elnarion.ddlutils.Platform;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.platform.DatabaseMetaDataWrapper;



/**
 * Reads a database model from an Oracle 12 database.
 *
 * @version $Revision: $
 */
public class Oracle12ModelReader extends Oracle10ModelReader
{
    /**
     * Creates a new model reader for Oracle 12 databases.
     * 
     * @param platform The platform that this model reader belongs to
     */
    public Oracle12ModelReader(Platform platform)
    {
        super(platform);
    }

	/**
     * {@inheritDoc}
     */
    protected Column readColumn(DatabaseMetaDataWrapper metaData, Map<String, Object> values) throws SQLException
    {
    	// For more Information see https://docs.oracle.com/cd/B19306_01/java.102/b14188/datamap.htm
		Column column = super.readColumn(metaData, values);

		if (column.getTypeCode() == Types.NUMERIC)
		{
			// We're back-mapping the NUMBER columns returned by Oracle
			// Note that the JDBC driver returns NUMERIC for these NUMBER columns
			switch (column.getSizeAsInt())
			{
			case 1:
				if (column.getScale() == 0)
				{
					column.setTypeCode(Types.BIT);
				}
				break;
			case 3:
				if (column.getScale() == 0)
				{
					column.setTypeCode(Types.TINYINT);
				}
				break;
			case 5:
				if (column.getScale() == 0)
				{
					column.setTypeCode(Types.SMALLINT);
				}
				break;				
			// no description found for BIGINT <-> NUMBER but a mapping of SQLServer BIGINT to NUMBER (19)
			case 19:
				if (column.getScale() == 0)
				{
					column.setTypeCode(Types.BIGINT);
				}
				break;
			case 38:
				if (column.getScale() == 0)
				{
					column.setTypeCode(Types.INTEGER);
				}
				break;
			}
			
		}
		return column;
	}

}
