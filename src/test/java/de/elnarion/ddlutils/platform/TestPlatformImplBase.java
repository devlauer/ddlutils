package de.elnarion.ddlutils.platform;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Map;

import org.apache.commons.beanutils.DynaBean;

import de.elnarion.ddlutils.TestBase;
import de.elnarion.ddlutils.dynabean.SqlDynaBean;
import de.elnarion.ddlutils.dynabean.SqlDynaClass;
import de.elnarion.ddlutils.io.DatabaseIO;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.ddlutils.platform.PlatformImplBase;

/**
 * Tests the {@link PlatformImplBase} (abstract) class.
 * 
 * @version $Revision: 279421 $
 */
public class TestPlatformImplBase extends TestBase 
{
    /**
     * Test the toColumnValues method.
     */
    public void testToColumnValues()
    {
        final String schema =
            "<?xml version='1.0' encoding='ISO-8859-1'?>\n"+
            "<database xmlns='" + DatabaseIO.DDLUTILS_NAMESPACE + "' name='ddlutils'>\n"+
            "  <table name='TestTable'>\n"+
            "    <column name='id' autoIncrement='true' type='INTEGER' primaryKey='true'/>\n"+
            "    <column name='name' type='VARCHAR' size='15'/>\n"+
            "  </table>\n"+
            "</database>";

        Database         database = parseDatabaseFromString(schema);
        PlatformImplBase platform = new TestPlatform();
        Table            table    = database.getTable(0);
        SqlDynaClass     clz      = SqlDynaClass.newInstance(table);
        DynaBean         db       = new SqlDynaBean(SqlDynaClass.newInstance(table));

        db.set("name", "name");

        Map<String, Object> map = platform.toColumnValues(clz.getSqlDynaProperties(), db);

        assertEquals("name",
                     map.get("name"));
        assertTrue(map.containsKey("id"));
    }
}
