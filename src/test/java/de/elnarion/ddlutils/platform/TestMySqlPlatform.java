package de.elnarion.ddlutils.platform;

import de.elnarion.ddlutils.TestPlatformBase;
import de.elnarion.ddlutils.io.DatabaseIO;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.platform.CreationParameters;
import de.elnarion.ddlutils.platform.mysql.MySqlPlatform;

// TODO: Auto-generated Javadoc
/**
 * Tests the MySQL platform.
 * 
 * @version $Revision: 231110 $
 */
public class TestMySqlPlatform extends TestPlatformBase
{
    /**
     * {@inheritDoc}
     */
    protected String getDatabaseName()
    {
        return MySqlPlatform.DATABASENAME;
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
            "DROP TABLE IF EXISTS `coltype`;\n"+
            "CREATE TABLE `coltype`\n"+
            "(\n"+
            "    `COL_ARRAY`           LONGBLOB,\n"+
            "    `COL_BIGINT`          BIGINT,\n"+
            "    `COL_BINARY`          BINARY(254) NULL,\n"+
            "    `COL_BIT`             TINYINT(1),\n"+
            "    `COL_BLOB`            LONGBLOB NULL,\n"+
            "    `COL_BOOLEAN`         TINYINT(1),\n"+
            "    `COL_CHAR`            CHAR(15) NULL,\n"+
            "    `COL_CLOB`            LONGTEXT NULL,\n"+
            "    `COL_DATALINK`        MEDIUMBLOB,\n"+
            "    `COL_DATE`            DATE,\n"+
            "    `COL_DECIMAL`         DECIMAL(15,3),\n"+
            "    `COL_DECIMAL_NOSCALE` DECIMAL(15,0),\n"+
            "    `COL_DISTINCT`        LONGBLOB,\n"+
            "    `COL_DOUBLE`          DOUBLE,\n"+
            "    `COL_FLOAT`           DOUBLE,\n"+
            "    `COL_INTEGER`         INTEGER,\n"+
            "    `COL_JAVA_OBJECT`     LONGBLOB,\n"+
            "    `COL_LONGVARBINARY`   MEDIUMBLOB NULL,\n"+
            "    `COL_LONGVARCHAR`     MEDIUMTEXT NULL,\n"+
            "    `COL_NULL`            MEDIUMBLOB,\n"+
            "    `COL_NUMERIC`         DECIMAL(15,0),\n"+
            "    `COL_OTHER`           LONGBLOB,\n"+
            "    `COL_REAL`            FLOAT,\n"+
            "    `COL_REF`             MEDIUMBLOB,\n"+
            "    `COL_SMALLINT`        SMALLINT,\n"+
            "    `COL_STRUCT`          LONGBLOB,\n"+
            "    `COL_TIME`            TIME,\n"+
            "    `COL_TIMESTAMP`       DATETIME,\n"+
            "    `COL_TINYINT`         SMALLINT,\n"+
            "    `COL_VARBINARY`       VARBINARY(15) NULL,\n"+
            "    `COL_VARCHAR`         VARCHAR(15) NULL\n"+
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
        // MySql-specfic schema
        final String schema =
            "<?xml version='1.0' encoding='ISO-8859-1'?>\n" +
            "<database xmlns='" + DatabaseIO.DDLUTILS_NAMESPACE + "' name='columnconstraintstest'>\n" +
            "  <table name='constraints'>\n" +
            "    <column name='COL_PK' type='VARCHAR' size='32' primaryKey='true'/>\n" +
            "    <column name='COL_PK_AUTO_INCR' type='INTEGER' primaryKey='true'/>\n" +
            "    <column name='COL_NOT_NULL' type='BINARY' size='100' required='true'/>\n" +
            "    <column name='COL_NOT_NULL_DEFAULT' type='DOUBLE' required='true' default='-2.0'/>\n" +
            "    <column name='COL_DEFAULT' type='CHAR' size='4' default='test'/>\n" +
            "    <column name='COL_AUTO_INCR' type='BIGINT'/>\n" +
            "  </table>\n" +
            "</database>";


        assertEqualsIgnoringWhitespaces(
            "DROP TABLE IF EXISTS `constraints`;\n" +
            "CREATE TABLE `constraints`\n"+
            "(\n"+
            "    `COL_PK`               VARCHAR(32) NULL,\n"+
            "    `COL_PK_AUTO_INCR`     INTEGER,\n"+
            "    `COL_NOT_NULL`         BINARY(100) NOT NULL,\n"+
            "    `COL_NOT_NULL_DEFAULT` DOUBLE DEFAULT -2.0 NOT NULL,\n"+
            "    `COL_DEFAULT`          CHAR(4) DEFAULT 'test' NULL,\n"+
            "    `COL_AUTO_INCR`        BIGINT,\n"+
            "    PRIMARY KEY (`COL_PK`, `COL_PK_AUTO_INCR`)\n"+
            ");\n",
            getDatabaseCreationSql(schema));
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
            "ALTER TABLE `table3` DROP FOREIGN KEY `testfk`;\n"+
            "ALTER TABLE `table2` DROP FOREIGN KEY `table2_FK_COL_FK_1_COL_FK_2_table1`;\n"+
            "DROP TABLE IF EXISTS `table3`;\n"+
            "DROP TABLE IF EXISTS `table2`;\n"+
            "DROP TABLE IF EXISTS `table1`;\n"+
            "CREATE TABLE `table1`\n"+
            "(\n"+
            "    `COL_PK_1`    VARCHAR(32) NOT NULL,\n"+
            "    `COL_PK_2`    INTEGER,\n"+
            "    `COL_INDEX_1` BINARY(100) NOT NULL,\n"+
            "    `COL_INDEX_2` DOUBLE NOT NULL,\n"+
            "    `COL_INDEX_3` CHAR(4) NULL,\n"+
            "    PRIMARY KEY (`COL_PK_1`, `COL_PK_2`)\n"+
            ");\n"+
            "CREATE INDEX `testindex1` ON `table1` (`COL_INDEX_2`);\n"+
            "CREATE UNIQUE INDEX `testindex2` ON `table1` (`COL_INDEX_3`, `COL_INDEX_1`);\n"+
            "CREATE TABLE `table2`\n"+
            "(\n"+
            "    `COL_PK`   INTEGER,\n"+
            "    `COL_FK_1` INTEGER,\n"+
            "    `COL_FK_2` VARCHAR(32) NOT NULL,\n"+
            "    PRIMARY KEY (`COL_PK`)\n"+
            ");\n"+
            "CREATE TABLE `table3`\n"+
            "(\n"+
            "    `COL_PK` VARCHAR(16) NULL,\n"+
            "    `COL_FK` INTEGER NOT NULL,\n"+
            "    PRIMARY KEY (`COL_PK`)\n"+
            ");\n"+
            "ALTER TABLE `table2` ADD CONSTRAINT `table2_FK_COL_FK_1_COL_FK_2_table1` FOREIGN KEY (`COL_FK_1`, `COL_FK_2`) REFERENCES `table1` (`COL_PK_2`, `COL_PK_1`) ON DELETE NO ACTION ON UPDATE NO ACTION;\n"+
            "ALTER TABLE `table3` ADD CONSTRAINT `testfk` FOREIGN KEY (`COL_FK`) REFERENCES `table2` (`COL_PK`) ON DELETE NO ACTION ON UPDATE NO ACTION;\n",
            getTableConstraintTestDatabaseCreationSql());
    }

    /**
	 * Tests the usage of creation parameters.
	 *
	 * @throws Exception
	 *             the exception
	 */
    public void testCreationParameters1() throws Exception
    {
        // MySql-specfic schema
        final String schema =
            "<?xml version='1.0' encoding='ISO-8859-1'?>\n" +
            "<database xmlns='" + DatabaseIO.DDLUTILS_NAMESPACE + "' name='columnconstraintstest'>\n" +
            "  <table name='constraints'>\n" +
            "    <column name='COL_PK' type='VARCHAR' size='32' primaryKey='true'/>\n" +
            "    <column name='COL_PK_AUTO_INCR' type='INTEGER' primaryKey='true'/>\n" +
            "    <column name='COL_NOT_NULL' type='BINARY' size='100' required='true'/>\n" +
            "    <column name='COL_NOT_NULL_DEFAULT' type='DOUBLE' required='true' default='-2.0'/>\n" +
            "    <column name='COL_DEFAULT' type='CHAR' size='4' default='test'/>\n" +
            "    <column name='COL_AUTO_INCR' type='BIGINT'/>\n" +
            "  </table>\n" +
            "</database>";

        Database           testDb = parseDatabaseFromString(schema);
        CreationParameters params = new CreationParameters();

        params.addParameter(testDb.getTable(0),
                            "ROW_FORMAT",
                            "COMPRESSED");
        params.addParameter(null,
                            "ENGINE",
                            "INNODB");

        getPlatform().setSqlCommentsOn(false);
        getPlatform().getSqlBuilder().createTables(testDb, params, true);

        assertEqualsIgnoringWhitespaces(
            "DROP TABLE IF EXISTS `constraints`;\n" +
            "CREATE TABLE `constraints`\n"+
            "(\n"+
            "    `COL_PK`               VARCHAR(32) NULL,\n"+
            "    `COL_PK_AUTO_INCR`     INTEGER,\n"+
            "    `COL_NOT_NULL`         BINARY(100) NOT NULL,\n"+
            "    `COL_NOT_NULL_DEFAULT` DOUBLE DEFAULT -2.0 NOT NULL,\n"+
            "    `COL_DEFAULT`          CHAR(4) DEFAULT 'test' NULL,\n"+
            "    `COL_AUTO_INCR`        BIGINT,\n"+
            "    PRIMARY KEY (`COL_PK`, `COL_PK_AUTO_INCR`)\n"+
            ") ENGINE=INNODB ROW_FORMAT=COMPRESSED;\n",
            getBuilderOutput());
    }

    /**
	 * Tests the proper escaping of character sequences where MySQL requires it.
	 *
	 * @throws Exception
	 *             the exception
	 */
    public void testCharacterEscaping() throws Exception
    {
        // MySql-specific schema
        final String schema =
            "<?xml version='1.0' encoding='ISO-8859-1'?>\n" +
            "<database xmlns='" + DatabaseIO.DDLUTILS_NAMESPACE + "' name='escapetest'>\n" +
            "  <table name='escapedcharacters'>\n" +
            "    <column name='COL_PK' type='INTEGER' primaryKey='true'/>\n" +
            "    <column name='COL_TEXT' type='VARCHAR' size='128' default='_ &#39; \" &#10; &#13; &#09; \\ &#37;'/>\n" +
            "  </table>\n" +
            "</database>";

        assertEqualsIgnoringWhitespaces(
            "DROP TABLE IF EXISTS `escapedcharacters`;\n"+
            "CREATE TABLE `escapedcharacters`\n"+
            "(\n"+
            "    `COL_PK`   INTEGER,\n"+
            "    `COL_TEXT` VARCHAR(128) DEFAULT '\\_ \\\' \\\" \\n \\r \\t \\\\ \\%' NULL,\n"+
            "    PRIMARY KEY (`COL_PK`)\n"+
            ");\n",
            getDatabaseCreationSql(schema));
    }
}
