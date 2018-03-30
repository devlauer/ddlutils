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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.elnarion.ddlutils.TestPlatformBase;
import de.elnarion.ddlutils.platform.mssql.MSSqlPlatform;

// TODO: Auto-generated Javadoc
/**
 * Tests the Microsoft SQL Server platform.
 * 
 * @version $Revision: 231110 $
 */
public class TestMSSqlPlatform extends TestPlatformBase
{
    /**
     * {@inheritDoc}
     */
    protected String getDatabaseName()
    {
        return MSSqlPlatform.DATABASENAME;
    }

    /**
	 * Tests the column types.
	 *
	 * @throws Exception
	 *             the exception
	 */
    public void testColumnTypes() throws Exception
    {
        String sql = getColumnTestDatabaseCreationSql();

        // Since we have no way of knowing the auto-generated variables in the SQL,
        // we simply try to extract it from the SQL
        Pattern declarePattern    = Pattern.compile("DECLARE @([\\S]+) [^@]+@([\\S]+)");
        Matcher matcher           = declarePattern.matcher(sql);
        String  tableNameVar      = "tablename";
        String  constraintNameVar = "constraintname";

        if (matcher.find())
        {
            tableNameVar      = matcher.group(1);
            constraintNameVar = matcher.group(2);
        }
        assertEqualsIgnoringWhitespaces(
            "SET quoted_identifier on;\n"+
            "SET quoted_identifier on;\n"+
            "IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'coltype')\n"+
            "BEGIN\n"+
            "  DECLARE @" + tableNameVar + " nvarchar(256), @" + constraintNameVar + " nvarchar(256)\n"+
            "  DECLARE refcursor CURSOR FOR\n"+
            "  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname\n"+
            "    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid\n"+
            "    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'coltype'  OPEN refcursor\n"+
            "  FETCH NEXT FROM refcursor INTO @" + tableNameVar + ", @" + constraintNameVar + "\n"+
            "  WHILE @@FETCH_STATUS = 0\n"+
            "    BEGIN\n"+
            "      EXEC ('ALTER TABLE '+@" + tableNameVar + "+' DROP CONSTRAINT '+@" + constraintNameVar + ")\n"+
            "      FETCH NEXT FROM refcursor INTO @" + tableNameVar + ", @" + constraintNameVar + "\n"+
            "    END\n"+
            "  CLOSE refcursor\n"+
            "  DEALLOCATE refcursor\n"+
            "  DROP TABLE \"coltype\"\n"+
            "END;\n"+
            "SET quoted_identifier on;\n"+
            "CREATE TABLE \"coltype\"\n"+
            "(\n"+
            "    \"COL_ARRAY\"           IMAGE,\n"+
            "    \"COL_BIGINT\"          DECIMAL(19,0),\n"+
            "    \"COL_BINARY\"          BINARY(254),\n"+
            "    \"COL_BIT\"             BIT,\n"+
            "    \"COL_BLOB\"            IMAGE,\n"+
            "    \"COL_BOOLEAN\"         BIT,\n"+
            "    \"COL_CHAR\"            CHAR(15),\n"+
            "    \"COL_CLOB\"            TEXT,\n"+
            "    \"COL_DATALINK\"        IMAGE,\n"+
            "    \"COL_DATE\"            DATETIME,\n"+
            "    \"COL_DECIMAL\"         DECIMAL(15,3),\n"+
            "    \"COL_DECIMAL_NOSCALE\" DECIMAL(15,0),\n"+
            "    \"COL_DISTINCT\"        IMAGE,\n"+
            "    \"COL_DOUBLE\"          FLOAT,\n"+
            "    \"COL_FLOAT\"           FLOAT,\n"+
            "    \"COL_INTEGER\"         INT,\n"+
            "    \"COL_JAVA_OBJECT\"     IMAGE,\n"+
            "    \"COL_LONGVARBINARY\"   IMAGE,\n"+
            "    \"COL_LONGVARCHAR\"     TEXT,\n"+
            "    \"COL_NULL\"            IMAGE,\n"+
            "    \"COL_NUMERIC\"         NUMERIC(15,0),\n"+
            "    \"COL_OTHER\"           IMAGE,\n"+
            "    \"COL_REAL\"            REAL,\n"+
            "    \"COL_REF\"             IMAGE,\n"+
            "    \"COL_SMALLINT\"        SMALLINT,\n"+
            "    \"COL_STRUCT\"          IMAGE,\n"+
            "    \"COL_TIME\"            DATETIME,\n"+
            "    \"COL_TIMESTAMP\"       DATETIME,\n"+
            "    \"COL_TINYINT\"         SMALLINT,\n"+
            "    \"COL_VARBINARY\"       VARBINARY(15),\n"+
            "    \"COL_VARCHAR\"         VARCHAR(15)\n"+
            ");\n",
            sql);
    }


    /**
	 * Tests the column constraints.
	 *
	 * @throws Exception
	 *             the exception
	 */
    public void testColumnConstraints() throws Exception
    {
        String sql = getConstraintTestDatabaseCreationSql();

        // Since we have no way of knowing the auto-generated variables in the SQL,
        // we simply try to extract it from the SQL
        Pattern declarePattern    = Pattern.compile("DECLARE @([\\S]+) [^@]+@([\\S]+)");
        Matcher matcher           = declarePattern.matcher(sql);
        String  tableNameVar      = "tablename";
        String  constraintNameVar = "constraintname";

        if (matcher.find())
        {
            tableNameVar      = matcher.group(1);
            constraintNameVar = matcher.group(2);
        }
        // Note that this is not valid SQL as a table can have only one identity column at most 
        assertEqualsIgnoringWhitespaces(
            "SET quoted_identifier on;\n"+
            "SET quoted_identifier on;\n"+
            "IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'constraints')\n"+
            "BEGIN\n"+
            "  DECLARE @" + tableNameVar + " nvarchar(256), @" + constraintNameVar + " nvarchar(256)\n"+
            "  DECLARE refcursor CURSOR FOR\n"+
            "  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname\n"+
            "    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid\n"+
            "    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'constraints'  OPEN refcursor\n"+
            "  FETCH NEXT FROM refcursor INTO @" + tableNameVar + ", @" + constraintNameVar + "\n"+
            "  WHILE @@FETCH_STATUS = 0\n"+
            "    BEGIN\n"+
            "      EXEC ('ALTER TABLE '+@" + tableNameVar + "+' DROP CONSTRAINT '+@" + constraintNameVar + ")\n"+
            "      FETCH NEXT FROM refcursor INTO @" + tableNameVar + ", @" + constraintNameVar + "\n"+
            "    END\n"+
            "  CLOSE refcursor\n"+
            "  DEALLOCATE refcursor\n"+
            "  DROP TABLE \"constraints\"\n"+
            "END;\n"+
            "SET quoted_identifier on;\n"+
            "CREATE TABLE \"constraints\"\n"+
            "(\n"+
            "    \"COL_PK\"               VARCHAR(32),\n"+
            "    \"COL_PK_AUTO_INCR\"     INT IDENTITY(1,1),\n"+
            "    \"COL_NOT_NULL\"         BINARY(100) NOT NULL,\n"+
            "    \"COL_NOT_NULL_DEFAULT\" FLOAT DEFAULT -2.0 NOT NULL,\n"+
            "    \"COL_DEFAULT\"          CHAR(4) DEFAULT 'test',\n"+
            "    \"COL_AUTO_INCR\"        DECIMAL(19,0) IDENTITY(1,1),\n"+
            "    PRIMARY KEY (\"COL_PK\", \"COL_PK_AUTO_INCR\")\n"+
            ");\n",
            sql);
    }

    /**
	 * Tests the table constraints.
	 *
	 * @throws Exception
	 *             the exception
	 */
    public void testTableConstraints() throws Exception
    {
        String sql = getTableConstraintTestDatabaseCreationSql();

        // Since we have no way of knowing the auto-generated variables in the SQL,
        // we simply try to extract it from the SQL
        Pattern  declarePattern     = Pattern.compile("DECLARE @([\\S]+) [^@]+@([\\S]+)");
        Matcher  matcher            = declarePattern.matcher(sql);
        int      startPos           = 0;
        String[] tableNameVars      = { "tablename", "tablename", "tablename" };
        String[] constraintNameVars = { "constraintname", "constraintname", "constraintname" };

        for (int idx = 0; (idx < 3) && matcher.find(startPos); idx++)
        {
            tableNameVars[idx]      = matcher.group(1);
            constraintNameVars[idx] = matcher.group(2);
            startPos                = matcher.end();
        }
        assertEqualsIgnoringWhitespaces(
            "SET quoted_identifier on;\n"+
            "IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'testfk')\n"+
            "     ALTER TABLE \"table3\" DROP CONSTRAINT \"testfk\";\n"+
            "SET quoted_identifier on;\n"+
            "IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'table2_FK_COL_FK_1_COL_FK_2_table1')\n"+
            "     ALTER TABLE \"table2\" DROP CONSTRAINT \"table2_FK_COL_FK_1_COL_FK_2_table1\";\n"+
            "SET quoted_identifier on;\n"+
            "SET quoted_identifier on;\n"+
            "IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'table3')\n"+
            "BEGIN\n"+
            "  DECLARE @" + tableNameVars[0] + " nvarchar(256), @" + constraintNameVars[0] + " nvarchar(256)\n"+
            "  DECLARE refcursor CURSOR FOR\n"+
            "  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname\n"+
            "    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid\n"+
            "    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'table3'  OPEN refcursor\n"+
            "  FETCH NEXT FROM refcursor INTO @" + tableNameVars[0] + ", @" + constraintNameVars[0] + "\n"+
            "  WHILE @@FETCH_STATUS = 0\n"+
            "    BEGIN\n"+
            "      EXEC ('ALTER TABLE '+@" + tableNameVars[0] + "+' DROP CONSTRAINT '+@" + constraintNameVars[0] + ")\n"+
            "      FETCH NEXT FROM refcursor INTO @" + tableNameVars[0] + ", @" + constraintNameVars[0] + "\n"+
            "    END\n"+
            "  CLOSE refcursor\n"+
            "  DEALLOCATE refcursor\n"+
            "  DROP TABLE \"table3\"\n"+
            "END;\n"+
            "SET quoted_identifier on;\n"+
            "IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'table2')\n"+
            "BEGIN\n"+
            "  DECLARE @" + tableNameVars[1] + " nvarchar(256), @" + constraintNameVars[1] + " nvarchar(256)\n"+
            "  DECLARE refcursor CURSOR FOR\n"+
            "  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname\n"+
            "    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid\n"+
            "    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'table2'  OPEN refcursor\n"+
            "  FETCH NEXT FROM refcursor INTO @" + tableNameVars[1] + ", @" + constraintNameVars[1] + "\n"+
            "  WHILE @@FETCH_STATUS = 0\n"+
            "    BEGIN\n"+
            "      EXEC ('ALTER TABLE '+@" + tableNameVars[1] + "+' DROP CONSTRAINT '+@" + constraintNameVars[1] + ")\n"+
            "      FETCH NEXT FROM refcursor INTO @" + tableNameVars[1] + ", @" + constraintNameVars[1] + "\n"+
            "    END\n"+
            "  CLOSE refcursor\n"+
            "  DEALLOCATE refcursor\n"+
            "  DROP TABLE \"table2\"\n"+
            "END;\n"+
            "SET quoted_identifier on;\n"+
            "IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'table1')\n"+
            "BEGIN\n"+
            "  DECLARE @" + tableNameVars[2] + " nvarchar(256), @" + constraintNameVars[2] + " nvarchar(256)\n"+
            "  DECLARE refcursor CURSOR FOR\n"+
            "  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname\n"+
            "    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid\n"+
            "    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'table1'  OPEN refcursor\n"+
            "  FETCH NEXT FROM refcursor INTO @" + tableNameVars[2] + ", @" + constraintNameVars[2] + "\n"+
            "  WHILE @@FETCH_STATUS = 0\n"+
            "    BEGIN\n"+
            "      EXEC ('ALTER TABLE '+@" + tableNameVars[2] + "+' DROP CONSTRAINT '+@" + constraintNameVars[2] + ")\n"+
            "      FETCH NEXT FROM refcursor INTO @" + tableNameVars[2] + ", @" + constraintNameVars[2] + "\n"+
            "    END\n"+
            "  CLOSE refcursor\n"+
            "  DEALLOCATE refcursor\n"+
            "  DROP TABLE \"table1\"\n"+
            "END;\n"+
            "SET quoted_identifier on;\n"+
            "CREATE TABLE \"table1\"\n"+
            "(\n"+
            "    \"COL_PK_1\"    VARCHAR(32) NOT NULL,\n"+
            "    \"COL_PK_2\"    INT,\n"+
            "    \"COL_INDEX_1\" BINARY(100) NOT NULL,\n"+
            "    \"COL_INDEX_2\" FLOAT NOT NULL,\n"+
            "    \"COL_INDEX_3\" CHAR(4),\n"+
            "    PRIMARY KEY (\"COL_PK_1\", \"COL_PK_2\")\n"+
            ");\n"+
            "CREATE INDEX \"testindex1\" ON \"table1\" (\"COL_INDEX_2\");\n"+
            "CREATE UNIQUE INDEX \"testindex2\" ON \"table1\" (\"COL_INDEX_3\", \"COL_INDEX_1\");\n"+
            "SET quoted_identifier on;\n"+
            "CREATE TABLE \"table2\"\n"+
            "(\n"+
            "    \"COL_PK\"   INT,\n"+
            "    \"COL_FK_1\" INT,\n"+
            "    \"COL_FK_2\" VARCHAR(32) NOT NULL,\n"+
            "    PRIMARY KEY (\"COL_PK\")\n"+
            ");\n"+
            "SET quoted_identifier on;\n"+
            "CREATE TABLE \"table3\"\n"+
            "(\n"+
            "    \"COL_PK\" VARCHAR(16),\n"+
            "    \"COL_FK\" INT NOT NULL,\n"+
            "    PRIMARY KEY (\"COL_PK\")\n"+
            ");\n"+
            "ALTER TABLE \"table2\" ADD CONSTRAINT \"table2_FK_COL_FK_1_COL_FK_2_table1\" FOREIGN KEY (\"COL_FK_1\", \"COL_FK_2\") REFERENCES \"table1\" (\"COL_PK_2\", \"COL_PK_1\");\n"+
            "ALTER TABLE \"table3\" ADD CONSTRAINT \"testfk\" FOREIGN KEY (\"COL_FK\") REFERENCES \"table2\" (\"COL_PK\");\n",
            sql);
    }

    /**
	 * Tests the proper escaping of character sequences where Sql Server requires
	 * it.
	 *
	 * @throws Exception
	 *             the exception
	 */
    public void testCharacterEscaping() throws Exception
    {
        String sql = getCharEscapingTestDatabaseCreationSql();

        // Since we have no way of knowing the auto-generated variables in the SQL,
        // we simply try to extract it from the SQL
        Pattern declarePattern    = Pattern.compile("DECLARE @([\\S]+) [^@]+@([\\S]+)");
        Matcher matcher           = declarePattern.matcher(sql);
        String  tableNameVar      = "tablename";
        String  constraintNameVar = "constraintname";

        if (matcher.find())
        {
            tableNameVar      = matcher.group(1);
            constraintNameVar = matcher.group(2);
        }
        assertEqualsIgnoringWhitespaces(
            "SET quoted_identifier on;\n"+
            "SET quoted_identifier on;\n"+
            "IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'escapedcharacters')\n"+
            "BEGIN\n"+
            "  DECLARE @" + tableNameVar + " nvarchar(256), @" + constraintNameVar + " nvarchar(256)\n"+
            "  DECLARE refcursor CURSOR FOR\n"+
            "  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname\n"+
            "    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid\n"+
            "    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'escapedcharacters'  OPEN refcursor\n"+
            "  FETCH NEXT FROM refcursor INTO @" + tableNameVar + ", @" + constraintNameVar + "\n"+
            "  WHILE @@FETCH_STATUS = 0\n"+
            "    BEGIN\n"+
            "      EXEC ('ALTER TABLE '+@" + tableNameVar + "+' DROP CONSTRAINT '+@" + constraintNameVar + ")\n"+
            "      FETCH NEXT FROM refcursor INTO @" + tableNameVar + ", @" + constraintNameVar + "\n"+
            "    END\n"+
            "  CLOSE refcursor\n"+
            "  DEALLOCATE refcursor\n"+
            "  DROP TABLE \"escapedcharacters\"\n"+
            "END;\n"+
            "SET quoted_identifier on;\n"+
            "CREATE TABLE \"escapedcharacters\"\n"+
            "(\n"+
            "    \"COL_PK\"   INT,\n"+
            "    \"COL_TEXT\" VARCHAR(128) DEFAULT '\'\'',\n"+
            "    PRIMARY KEY (\"COL_PK\")\n"+
            ");\n",
            sql);
    }
}
