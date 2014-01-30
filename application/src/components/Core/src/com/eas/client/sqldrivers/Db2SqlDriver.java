/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.PrimaryKeySpec;
import com.eas.client.ClientConstants;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.sqldrivers.converters.Db2Converter;
import com.eas.client.sqldrivers.resolvers.Db2TypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.util.StringUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author kl
 */
public class Db2SqlDriver extends SqlDriver {

    protected static final String SET_SCHEMA_CLAUSE = "SET SCHEMA %s";
    protected static final String GET_SCHEMA_CLAUSE = "VALUES CURRENT SCHEMA";
    protected static final String CREATE_SCHEMA_CLAUSE = "CREATE SCHEMA %s";
    protected static final Converter converter = new Db2Converter();
    protected static final Db2TypesResolver resolver = new Db2TypesResolver();
    /**
     * Listing of SQLSTATE values
     *
     * @see http://publib.boulder.ibm.com/infocenter/iseries/v5r3/index.jsp?topic=%2Frzala%2Frzalaco.htm
     */
    protected static final int[] db2ErrorCodes = {};
    protected static final String[] platypusErrorMessages = {};
    protected static final String SQL_SCHEMAS = ""
            + "select s.NAME as " + ClientConstants.JDBCCOLS_TABLE_SCHEM
            + " from SYSIBM.SYSSCHEMATA as s"
            + " order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM;
    protected static final String SQL_ALL_TABLES = ""
            + "select t.NAME as " + ClientConstants.JDBCCOLS_TABLE_NAME
            + ", t.CREATOR as " + ClientConstants.JDBCCOLS_TABLE_SCHEM
            + ", '" + ClientConstants.JDBCPKS_TABLE_TYPE_TABLE + "' as " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME
            + " from SYSIBM.SYSTABLES as t where t.TYPE = 'T'";
    protected static final String SQL_ALL_VIEWS = ""
            + "select v.NAME as " + ClientConstants.JDBCCOLS_TABLE_NAME
            + ", v.CREATOR as " + ClientConstants.JDBCCOLS_TABLE_SCHEM
            + ", '" + ClientConstants.JDBCPKS_TABLE_TYPE_VIEW + "' as " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME
            + " from SYSIBM.SYSTABLES as v where v.TYPE = 'V'";
    protected static final String SQL_ALL_TABLES_AND_ALL_VIEWS = ""
            + SQL_ALL_TABLES
            + " union all "
            + SQL_ALL_VIEWS
            + " order by " + ClientConstants.JDBCCOLS_TABLE_NAME;
    protected static final String SQL_TABLES_AND_VIEWS = ""
            + SQL_ALL_TABLES
            + " and Upper(t.CREATOR) = Upper('%s')"
            + " union all "
            + SQL_ALL_VIEWS
            + " and Upper(v.CREATOR) = Upper('%s')"
            + " order by " + ClientConstants.JDBCCOLS_TABLE_NAME;
    protected static final String SQL_COLUMNS = ""
            + "select "
            + "c.TABLE_SCHEM as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ", "
            + "c.TABLE_NAME as " + ClientConstants.JDBCCOLS_TABLE_NAME + ", "
            + "t.REMARKS as " + ClientConstants.JDBCCOLS_TABLE_DESC + ", "
            + "c.COLUMN_NAME as " + ClientConstants.JDBCCOLS_COLUMN_NAME + ", "
            + "c.REMARKS as " + ClientConstants.JDBCCOLS_REMARKS + ", "
            + "c.DATA_TYPE as " + ClientConstants.JDBCCOLS_DATA_TYPE + ", "
            + "c.ORDINAL_POSITION as " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ", "
            + "c.TYPE_NAME as " + ClientConstants.JDBCCOLS_TYPE_NAME + ", "
            + "c.COLUMN_SIZE as " + ClientConstants.JDBCCOLS_COLUMN_SIZE + ", "
            + "c.DECIMAL_DIGITS as " + ClientConstants.JDBCCOLS_DECIMAL_DIGITS + ", "
            + "10 as " + ClientConstants.JDBCCOLS_NUM_PREC_RADIX + ", "
            + "c.NULLABLE as " + ClientConstants.JDBCCOLS_NULLABLE
            + " from SYSIBM.SQLCOLUMNS as c"
            + " inner join SYSIBM.SYSTABLES as t on (t.NAME = c.TABLE_NAME and c.TABLE_SCHEM = t.CREATOR)"
            + " where Upper(c.TABLE_SCHEM) = Upper('%s') and Upper(c.TABLE_NAME) in (%s)"
            + " order by c.TABLE_NAME, c.ORDINAL_POSITION";
    protected static final String SQL_PRIMARY_KEYS = ""
            + "select "
            + "tpk.TABLE_SCHEM as " + ClientConstants.JDBCPKS_TABLE_SCHEM + ", "
            + "tpk.TABLE_NAME as " + ClientConstants.JDBCPKS_TABLE_NAME + ", "
            + "tpk.COLUMN_NAME as " + ClientConstants.JDBCPKS_COLUMN_NAME + ", "
            + "tpk.PK_NAME as " + ClientConstants.JDBCPKS_CONSTRAINT_NAME
            + " FROM SYSIBM.SQLPRIMARYKEYS as tpk"
            + " where Upper(tpk.TABLE_SCHEM) = Upper('%s') and Upper(tpk.TABLE_NAME) in (%s)"
            + " order by tpk.TABLE_SCHEM, tpk.TABLE_NAME, tpk.KEY_SEQ";
    protected static final String SQL_FOREIGN_KEYS = ""
            + "select "
            + "tfk.FKTABLE_SCHEM as " + ClientConstants.JDBCFKS_FKTABLE_SCHEM + ", "
            + "tfk.FKTABLE_NAME as " + ClientConstants.JDBCFKS_FKTABLE_NAME + ", "
            + "tfk.FKCOLUMN_NAME as " + ClientConstants.JDBCFKS_FKCOLUMN_NAME + ", "
            + "tfk.FK_NAME as " + ClientConstants.JDBCFKS_FK_NAME + ", "
            + "tfk.UPDATE_RULE as " + ClientConstants.JDBCFKS_FKUPDATE_RULE + ", "
            + "tfk.DELETE_RULE as " + ClientConstants.JDBCFKS_FKDELETE_RULE + ", "
            + "tfk.DEFERRABILITY as " + ClientConstants.JDBCFKS_FKDEFERRABILITY + ", "
            + "tfk.PKTABLE_SCHEM as " + ClientConstants.JDBCFKS_FKPKTABLE_SCHEM + ", "
            + "tfk.PKTABLE_NAME as " + ClientConstants.JDBCFKS_FKPKTABLE_NAME + ", "
            + "tfk.PKCOLUMN_NAME as " + ClientConstants.JDBCFKS_FKPKCOLUMN_NAME + ", "
            + "tfk.PK_NAME as " + ClientConstants.JDBCFKS_FKPK_NAME + " "
            + "from SYSIBM.SQLFOREIGNKEYS as tfk "
            + "where Upper(tfk.FKTABLE_SCHEM) = Upper('%s') and Upper(tfk.FKTABLE_NAME) in (%s) "
            + "order by tfk.FKTABLE_SCHEM, tfk.FKTABLE_NAME, tfk.KEY_SEQ";
    protected static final String SQL_COLUMNS_COMMENTS = ""
            + "select "
            + "c.REMARKS as " + ClientConstants.F_COLUMNS_COMMENTS_COMMENT_FIELD_NAME + ", "
            + "c.TABLE_NAME as " + ClientConstants.JDBCCOLS_TABLE_NAME + ", "
            + "c.COLUMN_NAME as " + ClientConstants.F_COLUMNS_COMMENTS_FIELD_FIELD_NAME + " "
            + "from SYSIBM.SQLCOLUMNS as c "
            + "where Upper(c.TABLE_SCHEM) = Upper('%s') and Upper(c.TABLE_NAME) in (%s) "
            + "order by c.COLUMN_NAME";
    protected static final String SQL_INDEXES = ""
            + "select "
            + "  TABLE_CAT,"
            + "  " + ClientConstants.JDBCIDX_TABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCIDX_TABLE_NAME + ","
            + "  " + ClientConstants.JDBCIDX_NON_UNIQUE + ","
            + "  " + ClientConstants.JDBCIDX_INDEX_QUALIFIER + ","
            + "  " + ClientConstants.JDBCIDX_INDEX_NAME + ","
            + "  " + ClientConstants.JDBCIDX_TYPE + ","
            + "  " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + "  " + ClientConstants.JDBCIDX_COLUMN_NAME + ","
            + "  " + ClientConstants.JDBCIDX_ASC_OR_DESC + ","
            + "  CARDINALITY,"
            + "  PAGES,"
            + "  FILTER_CONDITION,"
            + "  (case when (select count(*) FROM SYSIBM.SQLPRIMARYKEYS as tpk where tpk.TABLE_SCHEM = i.TABLE_SCHEM and "
            + "        tpk.TABLE_NAME = i.TABLE_NAME and tpk.PK_NAME = i.INDEX_NAME) > 0 then 0 else 1 end) " + ClientConstants.JDBCIDX_PRIMARY_KEY + ","
            + "  null " + ClientConstants.JDBCIDX_FOREIGN_KEY + " "
            + "from SYSIBM.SQLSTATISTICS as i "
            + "where Upper(" + ClientConstants.JDBCIDX_TABLE_SCHEM + ") = Upper('%s') and Upper(" + ClientConstants.JDBCIDX_TABLE_NAME + ") in (%s)"
            + " and column_name is not null "
            + "order by TABLE_CAT,TABLE_NAME,ORDINAL_POSITION ";
    protected static final String SQL_ALL_TABLES_COMMENTS = ""
            + "select tbl.*, "
            + "tbl.REMARKS as " + ClientConstants.F_TABLE_COMMENTS_COMMENT_FIELD_NAME + " "
            + "from SYSIBM.SQLTABLES as tbl "
            + "where Upper(tbl.TABLE_SCHEM) = Upper('%s')";
    protected static final String SQL_TABLES_COMMENTS = ""
            + SQL_ALL_TABLES_COMMENTS
            + " and Upper(tbl.TABLE_NAME) in (%s)";
    protected static final String SQL_PARENTS_LIST = ""
            + "with recursive parents(mdent_id, "
            + "mdent_name, "
            + "mdent_type, "
            + "mdent_content_txt, "
            + "mdent_content_data, "
            + "tag1, "
            + "tag2, "
            + "tag3, "
            + "mdent_parent_id, "
            + "mdent_order, "
            + "mdent_content_txt_size, "
            + "mdent_content_txt_crc32) as "
            + "( "
            + "select m1.* from mtd_entities m1 where m1.mdent_id = :%s "
            + "    union all "
            + "select m2.* from parents p, mtd_entities m2 where m2.mdent_id = p.mdent_parent_id "
            + ") "
            + "select * from parents ";
    protected static final String SQL_CHILDREN_LIST = ""
            + "with recursive children(mdent_id, "
            + "mdent_name, "
            + "mdent_type, "
            + "mdent_content_txt, "
            + "mdent_content_data, "
            + "tag1, "
            + "tag2, "
            + "tag3, "
            + "mdent_parent_id, "
            + "mdent_order, "
            + "mdent_content_txt_size, "
            + "mdent_content_txt_crc32) as "
            + "( "
            + "select m1.* from mtd_entities m1 where m1.mdent_id = :%s "
            + "    union all "
            + "select m2.* from children c, mtd_entities m2 where c.mdent_id = m2.mdent_parent_id "
            + ") "
            + "select * from children ";
    protected static final String SQL_RENAME_FIELD = "alter table %s rename column %s to %s";
    protected static final String SQL_MODIFY_FIELD = "alter table %s modify ";
    protected static final String ALTER_FIELD_SQL_PREFIX = "alter table %s alter column ";
    protected static final String REORG_TABLE = "CALL SYSPROC.ADMIN_CMD('REORG TABLE %s')";
    protected static final String VOLATILE_TABLE = "ALTER TABLE %s VOLATILE CARDINALITY";

    @Override
    public String getSql4TablesEnumeration(String schema4Sql) {
        if (schema4Sql == null) {
            return SQL_ALL_TABLES_AND_ALL_VIEWS;
        } else {
            return String.format(SQL_TABLES_AND_VIEWS, schema4Sql, schema4Sql);
        }
    }

    @Override
    public String getSql4SchemasEnumeration() {
        return SQL_SCHEMAS;
    }

    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return String.format(CREATE_SCHEMA_CLAUSE, aSchemaName);
        }
        throw new IllegalArgumentException("Schema name is null or empty.");
    }

    @Override
    public String getSql4TableColumns(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_COLUMNS, aOwnerName, constructIn(aTableNames).toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TablePrimaryKeys(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_PRIMARY_KEYS, aOwnerName, constructIn(aTableNames).toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TableForeignKeys(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_FOREIGN_KEYS, aOwnerName, constructIn(aTableNames).toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4ColumnsComments(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_COLUMNS_COMMENTS, aOwnerName, constructIn(aTableNames).toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4Indexes(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_INDEXES, aOwnerName, constructIn(aTableNames).toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        aOwnerName = wrapName(aOwnerName);
        aTableName = wrapName(aTableName);
        aFieldName = wrapName(aFieldName);
        String sqlText = aOwnerName == null ? StringUtils.join(".", aTableName, aFieldName) : StringUtils.join(".", aOwnerName, aTableName, aFieldName);
        if (aDescription == null) {
            aDescription = "";
        }
        return new String[]{"comment on column " + sqlText + " is '" + aDescription + "'"};
    }

    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        String sqlText = StringUtils.join(".", wrapName(aOwnerName), wrapName(aTableName));
        if (aDescription == null) {
            aDescription = "";
        }
        return "comment on table " + sqlText + " is '" + aDescription + "'";
    }

    @Override
    public String getSql4TableComments(String aOwnerName, Set<String> aTableNames) {
        aOwnerName = wrapName(aOwnerName);
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_TABLES_COMMENTS, aOwnerName.toUpperCase(), constructIn(aTableNames).toUpperCase());
        } else if (aOwnerName != null && !aOwnerName.isEmpty()) {
            return String.format(SQL_ALL_TABLES_COMMENTS, aOwnerName.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getColumnNameFromCommentsDs(Rowset rs) throws RowsetException {
        if (!rs.isAfterLast() && !rs.isBeforeFirst()) {
            return (String) rs.getObject(rs.getFields().find(ClientConstants.F_COLUMNS_COMMENTS_FIELD_FIELD_NAME));
        }
        return null;
    }

    @Override
    public String getColumnCommentFromCommentsDs(Rowset rs) throws RowsetException {
        if (!rs.isAfterLast() && !rs.isBeforeFirst()) {
            return (String) rs.getObject(rs.getFields().find(ClientConstants.F_COLUMNS_COMMENTS_COMMENT_FIELD_NAME));
        }
        return null;
    }

    @Override
    public String getTableNameFromCommentsDs(Rowset rs) throws RowsetException {
        if (!rs.isAfterLast() && !rs.isBeforeFirst()) {
            return (String) rs.getObject(rs.getFields().find(ClientConstants.F_TABLE_COMMENTS_NAME_FIELD_NAME));
        }
        return null;
    }

    @Override
    public String getTableCommentFromCommentsDs(Rowset rs) throws RowsetException {
        if (!rs.isAfterLast() && !rs.isBeforeFirst()) {
            return (String) rs.getObject(rs.getFields().find(ClientConstants.F_TABLE_COMMENTS_COMMENT_FIELD_NAME));
        }
        return null;
    }

    @Override
    public String getSql4MtdEntitiesParentsList(String aChildParamName) {
        return String.format(SQL_PARENTS_LIST, aChildParamName);
    }

    @Override
    public String getSql4MtdEntitiesChildrenList(String aParentParamName) {
        return String.format(SQL_CHILDREN_LIST, aParentParamName);
    }

    @Override
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        return "drop table " + makeFullName(aSchemaName, aTableName);
    }

    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        return "drop index " + makeFullName(aSchemaName, aIndexName);
    }

    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        String constraintName = wrapName(aFk.getCName());
        String tableName = makeFullName(aSchemaName, aFk.getTable());
        return "alter table " + tableName + " drop constraint " + constraintName;
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        List<ForeignKeySpec> fkList = new ArrayList();
        fkList.add(aFk);
        return getSql4CreateFkConstraint(aSchemaName, fkList);
    }

    @Override
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        assert aIndex.getColumns().size() > 0 : "index definition must consist of at least 1 column";
        String indexName = makeFullName(aSchemaName, aIndex.getName());
        String tableName = makeFullName(aSchemaName, aTableName);
        String modifier = "";
        if (aIndex.isUnique()) {
            modifier = "unique";
        }
        String fieldsList = "";
        for (int i = 0; i < aIndex.getColumns().size(); i++) {
            DbTableIndexColumnSpec column = aIndex.getColumns().get(i);
            fieldsList += wrapName(column.getColumnName());
            if (i != aIndex.getColumns().size() - 1) {
                fieldsList += ", ";
            }
        }
        return "create " + modifier + " index " + indexName + " on " + tableName + "( " + fieldsList + " )";
    }

    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String tableName = makeFullName(aSchemaName, aTableName);
        aPkFieldName = wrapName(aPkFieldName);
        return "CREATE TABLE " + tableName + " ("
                + aPkFieldName + " DECIMAL(18,0) NOT NULL,"
                + "CONSTRAINT " + wrapName(aTableName + PKEY_NAME_SUFFIX) + " PRIMARY KEY (" + aPkFieldName + "))";
    }

    @Override
    public String parseException(Exception ex) {
        if (ex != null && ex instanceof SQLException) {
            SQLException sqlEx = (SQLException) ex;
            int errorCode = sqlEx.getErrorCode();
            for (int i = 0; i < db2ErrorCodes.length; i++) {
                if (errorCode == db2ErrorCodes[i]) {
                    return platypusErrorMessages[i];
                }
            }
        }
        return ex.getLocalizedMessage();
    }

    private String getFieldTypeDefinition(Field aField) {
        resolver.resolve2RDBMS(aField);
        DataTypeInfo typeInfo = aField.getTypeInfo();
        String sqlTypeName = typeInfo.getSqlTypeName();

        String leftPartNameType = resolver.getLeftPartNameType(sqlTypeName);
        String rightPartNameType = resolver.getRightPartNameType(sqlTypeName);

        String typeName = leftPartNameType;

        int size = aField.getSize();
        int scale = aField.getScale();

        if ((resolver.isScaled(sqlTypeName)) && (resolver.isSized(sqlTypeName) && size > 0)) {
            typeName += "(" + String.valueOf(size) + "," + String.valueOf(scale) + ")";
        } else {
            if (resolver.isSized(sqlTypeName) && size > 0) {
                typeName += "(" + String.valueOf(size) + ")";
            }
            if (resolver.isScaled(sqlTypeName) && scale > 0) {
                typeName += "(" + String.valueOf(scale) + ")";
            }
        }
        if (rightPartNameType != null) {
            typeName += " " + rightPartNameType;
        }
        return typeName;
    }

    @Override
    public String getSql4FieldDefinition(Field aField) {
        String fieldDefinition = wrapName(aField.getName()) + " " + getFieldTypeDefinition(aField);
        return fieldDefinition;
    }

    @Override
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, Field aOldFieldMd, Field aNewFieldMd) {
        List<String> sqls = new ArrayList();
        Field newFieldMd = aNewFieldMd.copy();
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String updateDefinition = String.format(ALTER_FIELD_SQL_PREFIX, fullTableName) + wrapName(aOldFieldMd.getName()) + " ";
        String fieldDefination = getFieldTypeDefinition(newFieldMd);

        DataTypeInfo newTypeInfo = newFieldMd.getTypeInfo();
        String newSqlTypeName = newTypeInfo.getSqlTypeName();
        if (newSqlTypeName == null) {
            newSqlTypeName = "";
        }
        int newScale = newFieldMd.getScale();
        int newSize = newFieldMd.getSize();
        boolean newNullable = newFieldMd.isNullable();

        DataTypeInfo oldTypeInfo = aOldFieldMd.getTypeInfo();
        String oldSqlTypeName = oldTypeInfo.getSqlTypeName();
        if (oldSqlTypeName == null) {
            oldSqlTypeName = "";
        }
        int oldScale = aOldFieldMd.getScale();
        int oldSize = aOldFieldMd.getSize();
        boolean oldNullable = aOldFieldMd.isNullable();

        sqls.add(getSql4VolatileTable(fullTableName));
        if (!oldSqlTypeName.equalsIgnoreCase(newSqlTypeName)
                || (resolver.isSized(newSqlTypeName) && newSize != oldSize)
                || (resolver.isScaled(newSqlTypeName) && newScale != oldScale)) {
            sqls.add(updateDefinition + " set data type " + fieldDefination);
        }
        if (oldNullable != newNullable) {
            sqls.add(updateDefinition + (newNullable ? " drop not null" : " set not null"));
        }
        if (sqls.size() == 1) {
            sqls.clear();
        } else {
            sqls.add(getSql4ReorgTable(fullTableName));
        }

        return (String[]) sqls.toArray(new String[sqls.size()]);
    }

    @Override
    public String[] getSql4DroppingField(String aSchemaName, String aTableName, String aFieldName) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        return new String[]{
            getSql4VolatileTable(fullTableName),
            String.format(DROP_FIELD_SQL_PREFIX, fullTableName) + wrapName(aFieldName),
            getSql4ReorgTable(fullTableName)
        };
    }

    /**
     * DB2 9.7 or later
     */
    @Override
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, Field aNewFieldMd) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        aOldFieldName = wrapName(aOldFieldName);
        String sqlText = String.format(SQL_RENAME_FIELD, fullTableName, aOldFieldName, wrapName(aNewFieldMd.getName()));
        return new String[]{
            getSql4VolatileTable(fullTableName),
            sqlText,
            getSql4ReorgTable(fullTableName)
        };
    }

    private String getSql4VolatileTable(String aTableName) {
        return String.format(VOLATILE_TABLE, wrapName(aTableName));
    }

    private String getSql4ReorgTable(String aTableName) {
        return String.format(REORG_TABLE, wrapName(aTableName));
    }

    @Override
    public Integer getJdbcTypeByRDBMSTypename(String aLowLevelTypeName) {
        return resolver.getJdbcTypeByRDBMSTypename(aLowLevelTypeName);
    }

    @Override
    public Converter getConverter() {
        return converter;
    }

    @Override
    public TypesResolver getTypesResolver() {
        return resolver;
    }

    @Override
    public String getApplicationInitResourceName() {
        return "/" + Db2SqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/Db2InitApp.sql";
    }

    @Override
    public String getUsersSpaceInitResourceName() {
        return "/" + Db2SqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/Db2InitUsersSpace.sql";
    }
    
    @Override
    public String getVersionInitResourceName() {
        return "/" + Db2SqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/Db2InitVersion.sql";
    }
    
    @Override
    public Set<Integer> getSupportedJdbcDataTypes() {
        return resolver.getSupportedJdbcDataTypes();
    }

    @Override
    public void applyContextToConnection(Connection aConnection, String aSchema) throws Exception {
        if (aSchema != null && !aSchema.isEmpty()) {
            try (Statement stmt = aConnection.createStatement()) {
                stmt.execute(String.format(SET_SCHEMA_CLAUSE, wrapName(aSchema)));
            }
        }
    }

    @Override
    public String getSql4GetConnectionContext() {
        return GET_SCHEMA_CLAUSE;
    }

    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
        return "alter table " + makeFullName(aSchemaName, aPk.getTable()) + " drop primary key";
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, List<ForeignKeySpec> listFk) {
        if (listFk != null && listFk.size() > 0) {
            ForeignKeySpec fk = listFk.get(0);
            String fkTableName = makeFullName(aSchemaName, fk.getTable());
            String fkName = fk.getCName();
            String fkColumnName = wrapName(fk.getField());

            PrimaryKeySpec pk = fk.getReferee();
            String pkSchemaName = pk.getSchema();
            String pkTableName = makeFullName(aSchemaName, pk.getTable());
            String pkColumnName = wrapName(pk.getField());

            for (int i = 1; i < listFk.size(); i++) {
                fk = listFk.get(i);
                pk = fk.getReferee();
                fkColumnName += ", " + wrapName(fk.getField());
                pkColumnName += ", " + wrapName(pk.getField());
            }

            /**
             * The DB2 system does not allow the "on update cascade" option for foreign key constraints.
             */
            String fkRule = " ON UPDATE NO ACTION";
            switch (fk.getFkDeleteRule()) {
                case CASCADE:
                    fkRule += " ON DELETE CASCADE ";
                    break;
                case NOACTION:
                case SETDEFAULT:
                    fkRule += " ON DELETE no action ";
                    break;
                case SETNULL:
                    fkRule += " ON DELETE set null ";
                    break;
            }
            //fkRule += " NOT ENFORCED";
            return String.format("ALTER TABLE %s ADD CONSTRAINT %s"
                    + " FOREIGN KEY (%s) REFERENCES %s (%s) %s", fkTableName, fkName.isEmpty() ? "" : wrapName(fkName), fkColumnName, pkTableName, pkColumnName, fkRule);
        }
        return null;
    }

    @Override
    public String[] getSql4CreatePkConstraint(String aSchemaName, List<PrimaryKeySpec> listPk) {

        if (listPk != null && listPk.size() > 0) {
            PrimaryKeySpec pk = listPk.get(0);
            String tableName = pk.getTable();
            String pkTableName = makeFullName(aSchemaName, tableName);
            String pkName = wrapName(tableName + PKEY_NAME_SUFFIX);
            String pkColumnName = wrapName(pk.getField());
            for (int i = 1; i < listPk.size(); i++) {
                pk = listPk.get(i);
                pkColumnName += ", " + wrapName(pk.getField());
            }
            return new String[]{
                getSql4VolatileTable(pkTableName),
                //                String.format("ALTER TABLE %s ADD %s PRIMARY KEY (%s)", pkTableName, (pkName.isEmpty() ? "" : "CONSTRAINT "+ wrapName(pkName)), pkColumnName),
                String.format("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)", pkTableName, pkName, pkColumnName),
                getSql4ReorgTable(pkTableName)
            };
        }
        return null;
    }

    @Override
    public boolean isConstraintsDeferrable() {
        return false;
    }

    @Override
    public String[] getSqls4AddingField(String aSchemaName, String aTableName, Field aField) {
        List<String> sqls = new ArrayList();
        String fullTableName = makeFullName(aSchemaName, aTableName);
        sqls.add(getSql4VolatileTable(fullTableName));
        sqls.add(String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, fullTableName) + getSql4FieldDefinition(aField));
        if (!aField.isNullable()) {
            sqls.add(String.format(ALTER_FIELD_SQL_PREFIX, fullTableName) + wrapName(aField.getName()) + " set not null");
        }
        sqls.add(getSql4ReorgTable(fullTableName));
        return (String[]) sqls.toArray(new String[sqls.size()]);
    }
}
