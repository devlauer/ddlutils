package de.elnarion.ddlutils.platform;

import de.elnarion.ddlutils.TestPlatformBase;
import de.elnarion.ddlutils.io.DatabaseIO;
import de.elnarion.ddlutils.platform.postgresql.PostgreSqlPlatform;

// TODO: Auto-generated Javadoc
/**
 * Tests the PostgreSQL platform.
 * 
 * @version $Revision: 231110 $
 */
public class TestPostgresqlPlatform extends TestPlatformBase
{
    /**
     * {@inheritDoc}
     */
    protected String getDatabaseName()
    {
        return PostgreSqlPlatform.DATABASENAME;
    }

    /**
	 * Tests the column types.
	 *
	 * @throws Exception
	 *             the exception
	 */
    public void testColumnTypes() throws Exception
    {
        assertEqualsIgnoringWhitespaces(
            "DROP TABLE \"coltype\" CASCADE;\n"+
            "CREATE TABLE \"coltype\"\n"+
            "(\n"+
            "    \"COL_ARRAY\"           BYTEA,\n"+
            "    \"COL_BIGINT\"          BIGINT,\n"+
            "    \"COL_BINARY\"          BYTEA,\n"+
            "    \"COL_BIT\"             BOOLEAN,\n"+
            "    \"COL_BLOB\"            BYTEA,\n"+
            "    \"COL_BOOLEAN\"         BOOLEAN,\n"+
            "    \"COL_CHAR\"            CHAR(15),\n"+
            "    \"COL_CLOB\"            TEXT,\n"+
            "    \"COL_DATALINK\"        BYTEA,\n"+
            "    \"COL_DATE\"            DATE,\n"+
            "    \"COL_DECIMAL\"         NUMERIC(15,3),\n"+
            "    \"COL_DECIMAL_NOSCALE\" NUMERIC(15,0),\n"+
            "    \"COL_DISTINCT\"        BYTEA,\n"+
            "    \"COL_DOUBLE\"          DOUBLE PRECISION,\n"+
            "    \"COL_FLOAT\"           DOUBLE PRECISION,\n"+
            "    \"COL_INTEGER\"         INTEGER,\n"+
            "    \"COL_JAVA_OBJECT\"     BYTEA,\n"+
            "    \"COL_LONGVARBINARY\"   BYTEA,\n"+
            "    \"COL_LONGVARCHAR\"     TEXT,\n"+
            "    \"COL_NULL\"            BYTEA,\n"+
            "    \"COL_NUMERIC\"         NUMERIC(15,0),\n"+
            "    \"COL_OTHER\"           BYTEA,\n"+
            "    \"COL_REAL\"            REAL,\n"+
            "    \"COL_REF\"             BYTEA,\n"+
            "    \"COL_SMALLINT\"        SMALLINT,\n"+
            "    \"COL_STRUCT\"          BYTEA,\n"+
            "    \"COL_TIME\"            TIME,\n"+
            "    \"COL_TIMESTAMP\"       TIMESTAMP,\n"+
            "    \"COL_TINYINT\"         SMALLINT,\n"+
            "    \"COL_VARBINARY\"       BYTEA,\n"+
            "    \"COL_VARCHAR\"         VARCHAR(15)\n"+
            ");\n",
            getColumnTestDatabaseCreationSql());
    }

    /**
	 * Tests the column constraints.
	 *
	 * @throws Exception
	 *             the exception
	 */
    public void testColumnConstraints() throws Exception
    {
        assertEqualsIgnoringWhitespaces(
            "DROP TABLE \"constraints\" CASCADE;\n" +
            "DROP SEQUENCE \"constraints_COL_K_AUTO_INCR_seq\";\n" +
            "DROP SEQUENCE \"constraints_COL_AUTO_INCR_seq\";\n" +
            "CREATE SEQUENCE \"constraints_COL_K_AUTO_INCR_seq\";\n" +
            "CREATE SEQUENCE \"constraints_COL_AUTO_INCR_seq\";\n" +
            "CREATE TABLE \"constraints\"\n"+
            "(\n"+
            "    \"COL_PK\"               VARCHAR(32),\n"+
            "    \"COL_PK_AUTO_INCR\"     INTEGER UNIQUE DEFAULT nextval('\"constraints_COL_K_AUTO_INCR_seq\"'),\n"+
            "    \"COL_NOT_NULL\"         BYTEA NOT NULL,\n"+
            "    \"COL_NOT_NULL_DEFAULT\" DOUBLE PRECISION DEFAULT -2.0 NOT NULL,\n"+
            "    \"COL_DEFAULT\"          CHAR(4) DEFAULT 'test',\n"+
            "    \"COL_AUTO_INCR\"        BIGINT UNIQUE DEFAULT nextval('\"constraints_COL_AUTO_INCR_seq\"'),\n"+
            "    PRIMARY KEY (\"COL_PK\", \"COL_PK_AUTO_INCR\")\n"+
            ");\n",
            getConstraintTestDatabaseCreationSql());
    }

    /**
	 * Tests the table constraints.
	 *
	 * @throws Exception
	 *             the exception
	 */
    public void testTableConstraints() throws Exception
    {
        assertEqualsIgnoringWhitespaces(
            "ALTER TABLE \"table3\" DROP CONSTRAINT \"testfk\";\n"+
            "ALTER TABLE \"table2\" DROP CONSTRAINT \"table2_FK_COL_F_COL_FK_2_table1\";\n"+
            "DROP TABLE \"table3\" CASCADE;\n"+
            "DROP TABLE \"table2\" CASCADE;\n"+
            "DROP TABLE \"table1\" CASCADE;\n"+
            "CREATE TABLE \"table1\"\n"+
            "(\n"+
            "    \"COL_PK_1\"    VARCHAR(32) NOT NULL,\n"+
            "    \"COL_PK_2\"    INTEGER,\n"+
            "    \"COL_INDEX_1\" BYTEA NOT NULL,\n"+
            "    \"COL_INDEX_2\" DOUBLE PRECISION NOT NULL,\n"+
            "    \"COL_INDEX_3\" CHAR(4),\n"+
            "    PRIMARY KEY (\"COL_PK_1\", \"COL_PK_2\")\n"+
            ");\n"+
            "CREATE INDEX \"testindex1\" ON \"table1\" (\"COL_INDEX_2\");\n"+
            "CREATE UNIQUE INDEX \"testindex2\" ON \"table1\" (\"COL_INDEX_3\", \"COL_INDEX_1\");\n"+
            "CREATE TABLE \"table2\"\n"+
            "(\n"+
            "    \"COL_PK\"   INTEGER,\n"+
            "    \"COL_FK_1\" INTEGER,\n"+
            "    \"COL_FK_2\" VARCHAR(32) NOT NULL,\n"+
            "    PRIMARY KEY (\"COL_PK\")\n"+
            ");\n"+
            "CREATE TABLE \"table3\"\n"+
            "(\n"+
            "    \"COL_PK\" VARCHAR(16),\n"+
            "    \"COL_FK\" INTEGER NOT NULL,\n"+
            "    PRIMARY KEY (\"COL_PK\")\n"+
            ");\n"+
            "ALTER TABLE \"table2\" ADD CONSTRAINT \"table2_FK_COL_F_COL_FK_2_table1\" FOREIGN KEY (\"COL_FK_1\", \"COL_FK_2\") REFERENCES \"table1\" (\"COL_PK_2\", \"COL_PK_1\");\n"+
            "ALTER TABLE \"table3\" ADD CONSTRAINT \"testfk\" FOREIGN KEY (\"COL_FK\") REFERENCES \"table2\" (\"COL_PK\");\n",
            getTableConstraintTestDatabaseCreationSql());
    }

    /**
	 * Tests the proper escaping of character sequences where PostgreSQL requires
	 * it.
	 *
	 * @throws Exception
	 *             the exception
	 */
    public void testCharacterEscaping() throws Exception
    {
        // PostgreSql specific database schema for testing escaping of character sequences
        final String schema =
            "<?xml version='1.0' encoding='ISO-8859-1'?>\n" +
            "<database xmlns='" + DatabaseIO.DDLUTILS_NAMESPACE + "' name='escapetest'>\n" +
            "  <table name='escapedcharacters'>\n" +
            "    <column name='COL_PK' type='INTEGER' primaryKey='true'/>\n" +
            "    <column name='COL_TEXT' type='VARCHAR' size='128' default='&#39; &#09; &#10; &#13; \\'/>\n" +
            "  </table>\n" +
            "</database>";

        assertEqualsIgnoringWhitespaces(
            "DROP TABLE \"escapedcharacters\" CASCADE;\n"+
            "CREATE TABLE \"escapedcharacters\"\n"+
            "(\n"+
            "    \"COL_PK\"   INTEGER,\n"+
            "    \"COL_TEXT\" VARCHAR(128) DEFAULT ''' \\t \\n \\r \\\\',\n"+
            "    PRIMARY KEY (\"COL_PK\")\n"+
            ");\n",
            getDatabaseCreationSql(schema));
    }
}
