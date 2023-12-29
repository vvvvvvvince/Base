package weaver.conn;

import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.servicefiles.DataSourceXML;

import java.io.InputStream;
import java.util.*;

/**
 * @Title: MyRecordSet
 * Company: 泛微软件
 * @author: fsl
 * @version: 1.0
 * create date: 2023/5/18
 **/
public class MyRecordSet extends BaseBean {

    private String DataSourceid;
    private RecordSet rs = null;
    private long lostTime;
    private int curpos;
    private int flag;
    private String msg;
    public static char separator = Util.getSeparator();
    private Vector array;
    private String[] columnName;
    private int[] columnType;
    private String[] args;
    private ConnectionPool pool = ConnectionPool.getInstance();
    private WeaverConnection conn;
    private boolean bSuccess;
    private String databaseType;
    private boolean checksql;

    public RecordSet getRs() {
        return rs;
    }

    public void setNoAutoEncrypt(boolean noAutoEncrypt) {
        this.rs.setNoAutoEncrypt(noAutoEncrypt);
    }

    public void isAutoDecrypt(boolean isAutoDecrypt) {
        this.rs.isAutoDecrypt(isAutoDecrypt);
    }

    public void isReturnDecryptData(boolean isReturnDecryptData) {
        this.rs.isReturnDecryptData(isReturnDecryptData);
    }

    public boolean isEncrypt() {
        return this.rs.isEncrypt();
    }

    public void setEncrypt(boolean encrypt) {
        this.rs.setEncrypt(encrypt);
    }

    public MyRecordSet() {
        this.rs = new RecordSet();
    }

    public MyRecordSet(boolean isEncrypt) {
        this.rs = new RecordSet(isEncrypt);
    }

    public MyRecordSet(String datasourceid) {
        if ("".equals(datasourceid)) {
            datasourceid = null;
        }

        this.rs = new RecordSet(datasourceid);
        this.DataSourceid = datasourceid;
    }

    public MyRecordSet(String datasourceid, boolean isEncrypt) {
        if ("".equals(datasourceid)) {
            datasourceid = null;
        }

        this.rs = new RecordSet(datasourceid);
        this.rs.setEncrypt(isEncrypt);
        this.DataSourceid = datasourceid;
    }

    public boolean executeProc(String s, String s1) {
        return this.rs.executeProc(s, s1, this.DataSourceid);
    }

    public boolean executeProc2(String s, String s1) {
        return this.rs.executeProc2(s, s1, this.DataSourceid);
    }

    public boolean executeSql(String s) {
        return this.rs.executeSql(s, this.DataSourceid);
    }

    public boolean executeQueryWithDatasource(String s, String poolname, Object... params) {
        return this.rs.executeSql(s, true, poolname, true, params);
    }

    public boolean executeUpdateWithDatasource(String s, String poolname, Object... params) {
        return this.rs.executeSql(s, false, poolname, true, params);
    }

    public boolean execute(String s, String s1) {
        return this.executeProc(s, s1);
    }

    public boolean execute(String s) {
        return this.executeSql(s);
    }

    public int getCounts() {
        return this.rs.getCounts();
    }

    public int getColCounts() {
        return this.rs.getColCounts();
    }

    public String[] getColumnName() {
        return this.rs.getColumnName();
    }

    public int[] getColumnType() {
        return this.rs.getColumnType();
    }

    public String getColumnName(int columnIndex) {
        return this.rs.getColumnName(columnIndex);
    }

    public String getString(int columnIndex) {
        String s = this.rs.getString(columnIndex);
        return s;
    }

    public String getString(int columnIndex, String dbColumnName) {
        return this.rs.getString(columnIndex, dbColumnName);
    }

    public String getString(int columnIndex, boolean isAutoDecrypt) {
        return this.rs.getString(columnIndex, isAutoDecrypt);
    }

    public String getString(String tableName, int columnIndex, boolean isAutoDecrypt) {
        return this.rs.getString(tableName, columnIndex, isAutoDecrypt);
    }

    public String getString(String tableName, int columnIndex, boolean isAutoDecrypt, boolean isReturnDecryptData) {
        return this.rs.getString(tableName, columnIndex, isAutoDecrypt, isReturnDecryptData);
    }

    public String getString(String tableName, int columnIndex, String dbColumnName, boolean isAutoDecrypt, boolean isReturnDecryptData) {
        return this.rs.getString(tableName, columnIndex, dbColumnName, isAutoDecrypt, isReturnDecryptData);
    }

    public String getString(String columnname) {
        return this.getString(this.getColumnIndex(columnname));
    }

    public String getString(String columnname, boolean isAutoDecrypt) {
        return this.rs.getString(columnname, isAutoDecrypt);
    }

    public String getString(String tableName, String columnName) {
        return this.rs.getString(tableName, columnName);
    }

    public String getString(String tableName, String columnName, boolean isAutoDecrypt) {
        return this.rs.getString(tableName, this.getColumnIndex(columnName), isAutoDecrypt);
    }

    public String getStringFromAlias(String tableName, String columnName, String dbColumnName) {
        return this.rs.getStringFromAlias(tableName, columnName, dbColumnName);
    }

    public String getString(String tableName, String columnName, boolean isAutoDecrypt, boolean isReturnDecryptData) {
        return this.rs.getString(tableName, columnName, isAutoDecrypt, isReturnDecryptData);
    }

    public String getStringNoTrim(int columnIndex) {
        String s = this.rs.getStringNoTrim(columnIndex);
        return s;
    }

    public String getStringNoTrim(int columnIndex, String dbColumnName) {
        return this.rs.getStringNoTrim(columnIndex, dbColumnName);
    }

    public String getStringNoTrim(int columnIndex, boolean isAutoDecrypt) {
        return this.rs.getStringNoTrim(columnIndex, isAutoDecrypt);
    }

    public String getStringNoTrim(String tableName, int columnIndex, boolean isAutoDecrypt) {
        return this.rs.getStringNoTrim(tableName, columnIndex, isAutoDecrypt);
    }

    public String getStringNoTrim(String tableName, int columnIndex, boolean isAutoDecrypt, boolean isReturnDecryptData) {
        return this.rs.getStringNoTrim(tableName, columnIndex, isAutoDecrypt, isReturnDecryptData);
    }

    public String getStringNoTrim(String tableName, int columnIndex, String dbColumnName, boolean isAutoDecrypt, boolean isReturnDecryptData) {
        return this.rs.getStringNoTrim(tableName, columnIndex, dbColumnName, isAutoDecrypt, isReturnDecryptData);
    }

    public String getStringNoTrim(String columnname) {
        return this.getStringNoTrim(this.getColumnIndex(columnname));
    }

    public String getStringNoTrim(String columnname, boolean isAutoDecrypt) {
        return this.rs.getStringNoTrim(columnname, isAutoDecrypt);
    }

    public String getStringNoTrim(String tableName, String columnName) {
        return this.rs.getStringNoTrim(tableName, columnName);
    }

    public String getStringNoTrim(String tableName, String columnName, boolean isAutoDecrypt) {
        return this.rs.getStringNoTrim(tableName, this.getColumnIndex(columnName), isAutoDecrypt);
    }

    public String getStringNoTrim(String tableName, String columnName, boolean isAutoDecrypt, boolean isReturnDecryptData) {
        return this.rs.getStringNoTrim(tableName, this.getColumnIndex(columnName), isAutoDecrypt, isReturnDecryptData);
    }

    public boolean getBoolean(int columnIndex) {
        return this.rs.getBoolean(columnIndex);
    }

    public boolean getBoolean(String columnName) {
        return this.getBoolean(this.getColumnIndex(columnName));
    }

    public int getInt(int columnIndex) {
        return this.rs.getInt(columnIndex);
    }

    public int getInt(int columnIndex, boolean isAutoDecrypt) {
        return this.rs.getInt(columnIndex, isAutoDecrypt);
    }

    public int getInt(int columnIndex, boolean isReturnDecryptData, boolean isAutoDecrypt) {
        return this.rs.getInt(columnIndex, isReturnDecryptData, isAutoDecrypt);
    }

    public int getInt(String columnname) {
        return this.getInt(this.getColumnIndex(columnname));
    }

    public int getInt(String columnname, boolean isAutoDecrypt) {
        return this.rs.getInt(columnname, isAutoDecrypt);
    }

    public InputStream getInputStream(int columnIndex) {
        return this.rs.getInputStream(columnIndex);
    }

    public InputStream getInputStream(String columnName) {
        return this.getInputStream(this.getColumnIndex(columnName));
    }

    public float getFloat(int columnIndex) {
        return this.rs.getFloat(columnIndex);
    }

    public float getFloat(int columnIndex, boolean isAutoDecrypt) {
        return this.rs.getFloat(columnIndex, isAutoDecrypt);
    }

    public float getFloat(int columnIndex, boolean isReturnDecryptData, boolean isAutoDecrypt) {
        return this.rs.getFloat(columnIndex, isReturnDecryptData, isAutoDecrypt);
    }

    public float getFloat(String columnName) {
        return this.getFloat(this.getColumnIndex(columnName));
    }

    public double getDouble(int columnIndex) {
        return this.rs.getDouble(columnIndex);
    }

    public double getDouble(int columnIndex, boolean isAutoDecrypt) {
        return this.rs.getDouble(columnIndex, isAutoDecrypt);
    }

    public double getDouble(int columnIndex, boolean isReturnDecryptData, boolean isAutoDecrypt) {
        return this.rs.getDouble(columnIndex, isReturnDecryptData, isAutoDecrypt);
    }

    public double getDouble(String columnName, boolean isAutoDecrypt) {
        return this.rs.getDouble(columnName, isAutoDecrypt);
    }

    public double getDouble(String columnName) {
        return this.getDouble(this.getColumnIndex(columnName));
    }

    public Date getDate(int columnIndex) {
        return this.rs.getDate(columnIndex);
    }

    public Date getDate(String columnName) {
        return this.getDate(this.getColumnIndex(columnName));
    }

    public InputStream getBinaryStream(int columnIndex) {
        return this.rs.getBinaryStream(columnIndex);
    }

    public InputStream getBinaryStream(String columnName) {
        return this.getBinaryStream(this.getColumnIndex(columnName));
    }

    public int getFlag() {
        return this.rs.getFlag();
    }

    public String getMsg() {
        return this.rs.getMsg();
    }

    public void beforFirst() {
        this.rs.beforFirst();
    }

    public boolean first() {
        return this.rs.first();
    }

    public boolean last() {
        return this.rs.last();
    }

    public void afterLast() {
        this.rs.afterLast();
    }

    public boolean next() {
        return this.rs.next();
    }

    public boolean previous() {
        return this.rs.previous();
    }

    public boolean absolute(int i) {
        return this.rs.absolute(i);
    }

    public String getDBType() {
        return this.rs.getDBType(this.DataSourceid);
    }

    public String getOrgindbtype() {
        return this.rs.getOrgindbtype();
    }

    private int getColumnIndex(String columnname) {
        for(int i1 = 0; i1 < this.rs.getColumnName().length; ++i1) {
            if (this.rs.getColumnName()[i1].equalsIgnoreCase(columnname)) {
                return i1 + 1;
            }
        }

        return -1;
    }

    public boolean isChecksql() {
        return this.rs.isChecksql();
    }

    public void setChecksql(boolean checksql) {
        this.rs.setChecksql(checksql);
    }

    public ArrayList getAllColumns(String datasourceid, String tablename) {
        return ExternalDataSourceManager.getAllColumns(datasourceid, tablename);
    }

    public Map getAllColumnWithTypes(String datasourceid, String tablename) {
        return ExternalDataSourceManager.getAllColumnWithTypes(datasourceid, tablename);
    }

    public Map getAllColumnWithTypes(RecordSet rs, String tablename) {
        return ExternalDataSourceManager.getAllColumnWithTypes((String)null, tablename);
    }

    private String getInforMixFieldTypes() {
        StringBuffer sb = new StringBuffer();
        sb.append("when  c.coltype='0' then 'CHAR' ");
        sb.append("\twhen c.coltype='1' then 'SMALLINT'  ");
        sb.append("\twhen c.coltype='2' then 'INTEGER' ");
        sb.append("\twhen c.coltype='3' then 'FLOAT' ");
        sb.append("\twhen c.coltype='4' then 'SMALLFLOAT' ");
        sb.append("\twhen c.coltype='5' then 'DECIMAL' ");
        sb.append("\twhen c.coltype='6' then 'SERIAL' ");
        sb.append("\twhen c.coltype='7' then 'DATE' ");
        sb.append("\twhen c.coltype='8' then 'MONEY' ");
        sb.append("\twhen c.coltype='9' then 'NULL' ");
        sb.append("\twhen c.coltype='10' then 'DATETIME' ");
        sb.append("\twhen c.coltype='11' then 'BYTE' ");
        sb.append("\twhen c.coltype='12' then 'TEXT' ");
        sb.append("\twhen c.coltype='13' then 'VARCHAR' ");
        sb.append("\twhen c.coltype='14' then 'INTERVAL' ");
        sb.append("\twhen c.coltype='15' then 'NCHAR' ");
        sb.append("\twhen c.coltype='16' then 'NVARCHAR'");
        sb.append("\twhen c.coltype='17' then 'INT8' ");
        sb.append("\twhen c.coltype='18' then 'SERIAL8' ");
        sb.append("\twhen c.coltype='19' then 'SET' ");
        sb.append("\twhen c.coltype='20' then 'MULTISET' ");
        sb.append("\twhen c.coltype='21' then 'LIST' ");
        sb.append("\twhen c.coltype='22' then 'Unnamed ROW' ");
        sb.append("\twhen c.coltype='40' then 'LVARCHAR' ");
        sb.append("\twhen c.coltype='41' then 'CLOB' ");
        sb.append("\twhen c.coltype='43' then 'BLOB' ");
        sb.append("\twhen c.coltype='44' then 'BOOLEAN' ");
        sb.append("\twhen c.coltype='256' then 'CHAR' ");
        sb.append("\twhen c.coltype='257' then 'SMALLINT' ");
        sb.append("\twhen c.coltype='258' then 'INTEGER' ");
        sb.append("\twhen c.coltype='259' then 'FLOAT' ");
        sb.append("\twhen c.coltype='260' then 'REAL' ");
        sb.append("\twhen c.coltype='261' then 'DECIMAL' ");
        sb.append("\twhen c.coltype='262' then 'SERIAL' ");
        sb.append("\twhen c.coltype='263' then 'DATE' ");
        sb.append("\twhen c.coltype='264' then 'MONEY' ");
        sb.append("\twhen c.coltype='266' then 'DATETIME'");
        sb.append("\twhen c.coltype='267' then 'BYTE' ");
        sb.append("\twhen c.coltype='268' then 'TEXT' ");
        sb.append("\twhen c.coltype='269' then 'VARCHAR' ");
        sb.append("\twhen c.coltype='270' then 'INTERVAL' ");
        sb.append("\twhen c.coltype='271' then 'NCHAR' ");
        sb.append("\twhen c.coltype='272' then 'NVARCHAR' ");
        sb.append("\twhen c.coltype='273' then 'INT8' ");
        sb.append("\twhen c.coltype='274' then 'SERIAL8' ");
        sb.append("\twhen c.coltype='275' then 'SET' ");
        sb.append("\twhen c.coltype='276' then 'MULTISET' ");
        sb.append("\twhen c.coltype='277' then 'LIST' ");
        sb.append("\twhen c.coltype='278' then 'Unnamed ROW' ");
        sb.append("\twhen c.coltype='296' then 'LVARCHAR' ");
        sb.append("\twhen c.coltype='297' then 'CLOB' ");
        sb.append("\twhen c.coltype='298' then 'BLOB' ");
        sb.append("\twhen c.coltype='299' then 'BOOLEAN'");
        sb.append("\twhen c.coltype='4118' then 'Named ROW'");
        return sb.toString();
    }

    private String getAccessFieldType(int type) {
        String typeName = "";
        if (type == 1) {
            typeName = "CHARACTER";
        } else if (type == 2) {
            typeName = "NUMERIC";
        } else if (type == 3) {
            typeName = "DECIMAL";
        } else if (type == 4) {
            typeName = "INTEGER";
        } else if (type == 5) {
            typeName = "SMALLINT";
        } else if (type == 8) {
            typeName = "DOUBLE";
        } else if (type == 12) {
            typeName = "VARCHAR";
        } else if (type == 16) {
            typeName = "BOOLEAN";
        } else if (type == 93) {
            typeName = "TIMESTAMP";
        } else if (type == 1111) {
            typeName = "OTHER";
        } else if (type == 2004) {
            typeName = "BLOB";
        }

        return typeName;
    }

    private String getDBTypeInit(String datasourceid) {
        String dbTypeInit = "";

        try {
            if (datasourceid != null && !datasourceid.trim().equals("")) {
                DataSourceXML dsxml = new DataSourceXML();
                Hashtable dataHST = dsxml.getDataHST();
                String modeid = dsxml.getModuleId();
                Hashtable thisDetailHST = (Hashtable)dataHST.get(datasourceid);
                if (thisDetailHST != null) {
                    dbTypeInit = Util.null2String((String)thisDetailHST.get("type"));
                }
            }
        } catch (Exception var7) {
            this.writeLog("getDBTypeInit 数据源: " + this.DataSourceid + "读取出错");
            this.writeLog(var7);
        }

        return dbTypeInit;
    }

    public static boolean isOA(String datasourceName) {
        return ExternalDataSourceManager.isOA(datasourceName);
    }

    public Vector getArray() {
        return this.rs.getArray();
    }

    public void setArray(Vector array) {
        this.rs.setArray(array);
    }

    public long getLostTime() {
        return this.rs.getLostTime();
    }

    public void setColumnName(String[] columnName) {
        this.rs.setColumnName(columnName);
    }

    public void setColumnType(int[] columnType) {
        this.rs.setColumnType(columnType);
    }

    public void setDatabaseType(String databaseType) {
        this.rs.setDatabaseType(databaseType);
    }

}
