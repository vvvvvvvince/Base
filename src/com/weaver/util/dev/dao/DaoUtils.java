/**
 * @projectName ebuCodes
 * @package com.weaver.util.dev.dao
 * @className com.weaver.util.dev.dao.DaoUtils
 * @copyright weaver, Inc All rights reserved.
 */
package com.weaver.util.dev.dao;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.google.common.collect.Maps;
import com.weaver.util.dev.auth.FormmodeAuth;
import com.weaver.util.dev.bean.BeanUtils;
import com.weaver.util.dev.log.DevLog;
import com.weaver.util.dev.log.LogFactory;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import weaver.conn.BatchRecordSet;
import weaver.conn.RecordSet;
import weaver.conn.RecordSetTrans;
import weaver.general.StaticObj;
import weaver.general.Util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @BelongsPackage: com.weaver.util.dev.dao
 * @description 数据库增强操作类
 * @author fsl
 * @date 2023/3/30 17:39
 * @version 1.0
 */
public class DaoUtils {

    private static LogFactory log = new DevLog();

    /**
     * 通过注册的数据源名称获取连接信息
     * @param datasourceName
     * @return
     * @throws SQLException
     */
    public static Connection getConnectionByDatasourceName(String datasourceName) throws SQLException {
        weaver.interfaces.datasource.DataSource ds = (weaver.interfaces.datasource.DataSource) StaticObj.getServiceByFullname(("datasource."+datasourceName), weaver.interfaces.datasource.DataSource.class);
        java.sql.Connection conn = ds.getConnection();
        return conn;
    }


    public interface SQL_EXECUTE_TYPE{
        public static final String TYPE_INSERT="INSERT";
        public static final String TYPE_UPDATE="UPDATE";
    }


    /**
     * @description: 创建插入sql语句
     * @author: fsl
     * @date: 2023/3/30 19:47
     * @param: tableName
     * @param: params  字段集合
     * @return: java.lang.String
     **/
    public static String createInsertSql(String tableName, List<String> params) {
        StringBuilder sql = new StringBuilder(String.format("insert into %s (", tableName));
        StringBuilder addParam = new StringBuilder(" values (");
        int i = 0;
        for (String param : params) {
            if (i++ == 0) {
                sql.append(" " + param);
                addParam.append(" ?");
            } else {
                sql.append(", " + param);
                addParam.append(", ?");
            }
        }
        String sqlStr = sql.toString() + ")" + addParam.toString() + ")";
        log.warn("创建语句=>"+sqlStr);
        return sqlStr;
    }

    /**
     * @description: 创建跟新sql语句
     * @author: fsl
     * @date: 2023/3/30 19:47
     * @param: tableName
     * @param: params  字段集合
     * @return: java.lang.String
     **/
    public static String createUpdateSql(String tableName, List<String> params, List<String> condition) throws Exception{
        StringBuilder sql = new StringBuilder(String.format("update %s set ", tableName));
        int i = 0;
        for (String param : params) {
            if (i++ == 0) {
                sql.append(param+"=?");
            } else {
                sql.append("," + param+"=?");
            }
        }
        int j=0;
        for(String con: condition){
            if (j++ == 0) {
                sql.append(" where "+con+"=?");
            } else {
                sql.append(" and " + con+"=?");
            }
        }

        String sqlStr = sql.toString();
        log.warn("创建语句=>"+sqlStr);
        return sqlStr;
    }

    /**
     * 获取数据库类型
     *
     * @return dbType
     */
    public static String dbType() {
        RecordSet recordSet = new RecordSet();
        return recordSet.getDBType();
    }



    /**
     * 执行插入语句，map的键值作为插入的字段与字段值
     *
     * @param tableName
     * @param params
     */
    public static void insert(String tableName, Map<String, Object> params,RecordSet recordSet) {
        if (StringUtils.isEmpty(tableName)) {
            log.info("执行插入语句 => 表名为空");
            return;
        }
        if (Objects.isNull(params) || params.isEmpty()) {
            log.info("执行插入语句 => map为空");
            return;
        }
        List<String> paramList = Lists.newArrayList();
        List<Object> valueList = Lists.newArrayList();
        params.forEach((key, value) -> {
            paramList.add(key);
            valueList.add(value);
        });
        String sql = createInsertSql(tableName, paramList);
        log.info("执行SQL => " + sql);
        log.info("执行参数 => " + Arrays.toString(valueList.toArray()));
        executeUpdate(sql, valueList,recordSet);
    }

    /**
     * 插入建模
     *
     * @param tableName
     * @param params
     */
    public static void insertFormmode(String tableName, Map<String, Object> params,int formmodeId,RecordSet recordSet) throws Exception{
        log.info("insertFormmode => start");
        params.put("formmodeid",formmodeId);
        params.put("modedatacreater", FormmodeAuth.modedatacreater);
        params.put("modedatacreatertype",FormmodeAuth.modedatacreatertype);
        params.put("modedatacreatedate",FormmodeAuth.modedatacreatedate);
        params.put("modedatacreatetime",FormmodeAuth.modedatacreatetime);
        insert(tableName,params,recordSet);
        String getLastId = "select id from " + tableName + " order by id desc";
        log.info("getLastId => "+getLastId);
        recordSet.execute(getLastId);
        recordSet.next();
        int id = recordSet.getInt(1);
        log.debug("id => "+id);
        FormmodeAuth.buildAuth(formmodeId,id);
    }

    public static int  insertFormmode(String tableName, List<String> params, List<Object> data,int formmodeId,RecordSet recordSet) throws Exception{
        params.add("formmodeid");
        params.add("modedatacreater");
        params.add("modedatacreatertype");
        params.add("modedatacreatedate");
        params.add("modedatacreatetime");
        data.add(formmodeId);
        data.add(FormmodeAuth.modedatacreater);
        data.add(FormmodeAuth.modedatacreatertype);
        data.add(FormmodeAuth.modedatacreatedate);
        data.add(FormmodeAuth.modedatacreatetime);
        insert(tableName,params,data,recordSet);
        String getLastId = "select id from " + tableName + " order by id desc";
        recordSet.execute(getLastId);
        recordSet.next();
        int id = recordSet.getInt(1);
        FormmodeAuth.buildAuth(formmodeId,id);
        return id;
    }

    public static void insert(String tableName, List<String> params, List<Object> data,RecordSet recordSet) {
        if (StringUtils.isEmpty(tableName)) {
            log.info("执行插入语句 => 表名为空");
            return;
        }
        if (Objects.isNull(params) || params.isEmpty() || Objects.isNull(data) || data.isEmpty()) {
            log.info("执行插入语句 => 字段集合为空或数据集合为空");
            return;
        }
        String sql = createInsertSql(tableName, params);
        log.info("执行SQL => " + sql);
        log.info("执行参数 => " + Arrays.toString(data.toArray()));
        executeUpdate(sql, data,recordSet);
    }

    public static void update(String tableName, List<String> params, List<Object> data,List<String> condition,RecordSet recordSet) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            log.info("执行插入语句 => 表名为空");
            return;
        }
        if (Objects.isNull(params) || params.isEmpty() || Objects.isNull(data) || data.isEmpty()) {
            log.info("执行插入语句 => 字段集合为空或数据集合为空");
            return;
        }
        String sql = createUpdateSql(tableName, params,condition);
        log.info("执行SQL => " + sql);
        log.info("执行参数 => " + Arrays.toString(data.toArray()));
        executeUpdate(sql, data,recordSet);
    }

    public static void executeUpdate(String sql, List<Object> param,RecordSet recordSet) {
        log.info("执行SQL => " + sql);
        log.info("SQL参数 => " + Arrays.toString(param.toArray()));
        if(!recordSet.executeUpdate(sql, param.toArray(new Object[]{}))){
            log.error(sql+"::sql执行报错"+recordSet.getExceptionMsg());
        }
    }

    public static String querySingleVal(String sql,RecordSet recordSet, Object... objs) {
        log.info("执行SQL => " + sql);
        log.info("SQL参数 => " + Arrays.toString(objs));
        recordSet.executeQuery(sql, objs);
        if (recordSet.next()) {
            return recordSet.getString(1);
        }
        return null;
    }


    public static boolean executeQuery(String sql, List<Object> param,RecordSet recordSet) {
        log.info("解析前SQL => " + sql);
        log.info("执行SQL参数 => " + Arrays.toString(param.toArray()));
        if(recordSet.executeQuery(sql,param.toArray(new Object[]{}))){
            return true;
        }else{
            log.error(sql+"::sql执行报错"+recordSet.getExceptionMsg());
            return false;
        }
    }
    public static boolean executeQuery(String sql,RecordSet recordSet,Object... params) {
        log.info("解析前SQL => " + sql);
        log.info("执行SQL参数 => " + params);
        if(recordSet.executeQuery(sql,params)){
            return true;
        }else{
            log.error(sql+"::sql执行报错"+recordSet.getExceptionMsg());
            return false;
        }
    }


    public static void execute(String sql, List<Object> param) {
        RecordSet recordSet = new RecordSet();
        log.info("解析前SQL => " + sql);
        sql = toSingleSql(sql, param);
        log.info("执行SQL => " + sql);
        if(!recordSet.execute(sql)){
            log.error("sql执行错误::"+recordSet.getExceptionMsg());
        }
    }

    public static void execute(String sql, RecordSet recordSet) {
        log.info("执行SQL => " + sql);
        if(!recordSet.execute(sql)){
            log.error("sql执行错误::"+recordSet.getExceptionMsg());
        }

    }

    public static String toSingleSql(String sql, List<Object> param) {
        int pos = 0;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < sql.length(); i++) {
            if (sql.charAt(i) == '?') {
                stringBuffer.append(String.format("'%s'", param.get(pos++)));
            } else {
                stringBuffer.append(sql.charAt(i));
            }
        }
        return stringBuffer.toString();
    }

    public static String toSingleSql(String sql, Object... params) {
        int pos = 0;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < sql.length(); i++) {
            if (sql.charAt(i) == '?') {
                stringBuffer.append(String.format("'%s'", params[pos++]));
            } else {
                stringBuffer.append(sql.charAt(i));
            }
        }
        String res = stringBuffer.toString();
        log.info("res==>"+res);
        return res;
    }


    public static void insertWithRst(RecordSetTrans rst, String tableName, List<String> params, List<Object> data) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            log.info("执行插入语句 => 表名为空");
            return;
        }
        if (Objects.isNull(params) || params.isEmpty() || Objects.isNull(data) || data.isEmpty()) {
            log.info("执行插入语句 => 字段集合为空或数据集合为空");
            return;
        }
        String sql = createInsertSql(tableName, params);
        log.info("执行SQL => " + sql);
        log.info("执行参数 => " + Arrays.toString(data.toArray()));
        rst.executeUpdate(sql, data);
    }

    public static void batchInsert(String tableName, List<String> params, List<List<Object>> data) {
        if (StringUtils.isEmpty(tableName)) {
            log.info("批量执行插入语句 => 表名为空");
            return;
        }
        if (Objects.isNull(params) || params.isEmpty() || Objects.isNull(data) || data.isEmpty()) {
            log.info("批量执行插入语句 => 字段集合为空或数据集合为空");
            return;
        }
        batchExecute(createInsertSql(tableName, params), data);
    }

    public static void batchExecute(String sql, List<List<Object>> param) {
        BatchRecordSet batchRecordSet = new BatchRecordSet();
        log.info("执行SQL => " + sql);
        batchRecordSet.executeBatchSql(sql, param);
    }


    /**
     * @description: 将bean 对象存储到数据库中
     * @author: fsl
     * @date: 2023/3/30 20:26
     * @param: tableName
     * @param: obj
     **/
    public static void db2Bean(String tableName, Object obj,RecordSet recordSet) throws IllegalAccessException {
        Map<String, Object> param = BeanUtils.beanToAttrMap(obj);
        StringBuilder sqlStr = new StringBuilder(String.format("insert into %s (", tableName));
        StringBuilder temp = new StringBuilder(" values (");
        List<Object> objects = Lists.newArrayList();
        int i = 0;
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            if (i++ == 0) {
                sqlStr.append(entry.getKey());
                temp.append("?");
            } else {
                sqlStr.append(", " + entry.getKey());
                temp.append(", ?");
            }
            objects.add(entry.getValue());
        }
        String sql = sqlStr.toString() + " )" + temp.toString() + " )";
        log.info("saveBean sql => " + sql);
        log.info("执行SQL参数 => " + Arrays.toString(objects.toArray()));
        executeUpdate(sql, objects,recordSet);
    }

    /**
     * @description:将数据库查询单个结果集生成bean
     * @author: fsl
     * @date: 2023/2/20 16:28
     * @param: sql
     * @param: clazz
     * @param: fields key存储数据字段（也是对象属性名）  value  存储数据字段值
     * @param: objs
     * @return: T
     **/
    public static <T> T querySqlToSingleBean(String sql, Class<T> clazz,RecordSet recordSet,Object... objs) {
        log.info("执行SQL => " + sql);
        recordSet.executeQuery(sql, objs);
        if (recordSet.next())
            return recordSetMapToBean(recordSet, clazz);
        return null;
    }


    /**
     * 映射RecordSet到JavaBean
     *
     * @param recordSet
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T recordSetMapToBean(RecordSet recordSet, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 获取是否有注解
//                DaoField daoField = field.getAnnotation(DaoField.class);
//                if (Objects.nonNull(daoField)) {
//                    if (!daoField.value()) { // 为false
//                        continue;
//                    }
//                }
                Class<?> type = field.getType();
                field.setAccessible(true);
                //判断是否为字符
                if(type.equals(Long.class)||type.equals(long.class)||type.equals(int.class)||type.equals(Integer.class)){
                    String fieldName = field.getName();
                    int value = recordSet.getInt(fieldName);
                    field.set(t, value);
                }else if(type.equals(Float.class)||type.equals(float.class)){
                    String fieldName = field.getName();
                    float value = recordSet.getFloat(fieldName);
                    field.set(t, value);
                }else if(type.equals(Double.class)||type.equals(double.class)){
                    String fieldName = field.getName();
                    double value = recordSet.getDouble(fieldName);
                    field.set(t, value);
                }else{
                    String fieldName = field.getName();
                    String value = Util.null2String(recordSet.getString(fieldName));
                    field.set(t, value);
                }
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * @description: 将表数据转存bean
     * @author: fsl
     * @param: tableName
     * @param: clazz
     * @param: fields
     * @return: java.util.List<T>
     **/
    public static <T> List<T> queryListToBean(String sql, Class<T> clazz,RecordSet recordSet, Object... objs) {
        List<T> tList = Lists.newArrayList();
        log.info("执行SQL => " + sql);
        recordSet.executeQuery(sql,objs);
        while (recordSet.next())
            tList.add(recordSetMapToBean(recordSet, clazz));
        return tList;
    }


    /**
     * 保存 将list数据保存到数据库
     *
     * @param clazz     clazz
     * @param tableName tableName
     * @param list      list
     * @throws Exception Exception
     */
    public static void saveList(Class clazz, String tableName, List<?> list) throws Exception {
        List<String> fields = BeanUtils.beanAttrNameList(clazz);
        StringBuilder stringBuffer = new StringBuilder(String.format("insert into %s (", tableName));
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i == 0) {
                stringBuffer.append(fields.get(i));
                temp.append("?");
            } else {
                stringBuffer.append(", " + fields.get(i));
                temp.append(", ?");
            }
        }
        List<List<Object>> params = Lists.newArrayList();
        for (Object obj : list) {
            params.add(BeanUtils.beanToAttrListWithSq(obj, fields));
        }
        stringBuffer.append(") values (" + temp.toString() + ")");
        log.info("执行SQL => " + stringBuffer.toString());
        batchExecute(stringBuffer.toString(), params);
    }

    public static List<Map<String, String>> executeQueryToMapList(String sql, Object... objs) {
        RecordSet recordSet = new RecordSet();
        log.info("执行SQL => " + sql);
        log.info("SQL参数 => " + Arrays.toString(objs));
        List<Map<String, String>> res = Lists.newArrayList();
        if (recordSet.executeQuery(sql, objs)) {
            while (recordSet.next()) {
                res.add(recordSetToMap(recordSet,true));
            }
        }
        return res;
    }

    public static Map<String, String> executeQueryToMap(String sql,RecordSet recordSet, Object... objs) {
        log.info("执行SQL => " + sql);
        log.info("SQL参数 => " + Arrays.toString(objs));
        if (recordSet.executeQuery(sql, objs)) {
            if (recordSet.next()) {
                return recordSetToMap(recordSet,true);
            }
        }
        return null;
    }

    /**
     * @description: 查询数据据转换成map
     * @param: recordSet
     * @param: caseinsensitive
     * @return: java.util.Map<java.lang.String,java.lang.String>
     **/
    private static Map<String, String> recordSetToMap(RecordSet recordSet,boolean caseinsensitive) {
        // 根据配置项目判断是否需要大小写敏感
        Map<String, String> map = Maps.newHashMap();
        if (caseinsensitive) {
            map = new CaseInsensitiveMap();
        }
        for (String s : recordSet.getColumnName()) {
            map.put(s, recordSet.getString(s));
        }
        return map;
    }



    /**
     * 是否存在数据 检查数据唯一性 场景1 只包含一个表,只考虑唯一性字段只有一个
     * @param tableName
     * @param checkField
     * @param value
     * @return
     */
    public static boolean isDataExit(String tableName, String checkField, String value) {
        log.info("isDataExit::-->execute");
        boolean flag = false;
        StringBuilder sql = new StringBuilder("select count(*) from " + tableName + " where");
        sql.append(" " + checkField + "='" + value + "'");
        RecordSet srs = new RecordSet();
        srs.execute(sql.toString());
        srs.next();
        int num = srs.getInt(1);
        if (num > 0) {
            flag = true;
        }
        return flag;
    }
    /***
     *
     * @description only check muti fields
     * @author slfang
     * @date 2021/4/26
     * @param tableName
     * @param checkField
     * @param values
     * @return {@link boolean}
     *
     */
    public static boolean isDataExit(String tableName, String[] checkField, List<Object> values, RecordSet srs) {
        log.info("isDataExit::-->execute");
        boolean flag = false;
        StringBuilder sql = new StringBuilder("select count(*) from " + tableName + " where");

        int i;
        for(i = 0; i < checkField.length; ++i) {
            if (i == 0) {
                sql.append(" " + checkField[i] + "='" + values.get(i) + "'");
            } else {
                sql.append(" and " + checkField[i] + "='" + values.get(i) + "'");
            }
        }

        srs.execute(sql.toString());
        srs.next();
        i = srs.getInt(1);
        if (i > 0) {
            flag = true;
        }

        return flag;
    }

    /**
     * @description:  将sql查询结果集合插入新的建模表
     * @author: fsl
     * @date: 2023/5/24 20:43
     * @param: sql
     * @param: ufTable
     * @param: fields  新插入字段集合逗号隔开
     * @param: rs
     * @param: formmodeId  建模id
     **/
    public static void transRstoNewUf(String sql, String table, String fields, RecordSet rs,RecordSet rsInsert,int formmodeId,String executeType) throws Exception {
        log.info("transRstoNewUf==>start");
        log.info("transRstoNewUf::sql::"+sql);
        log.info("transRstoNewUf::table::"+table);
        log.info("transRstoNewUf::fields::"+fields);
        log.info("transRstoNewUf::formmodeId::"+formmodeId);
        boolean isFormmode = formmodeId>0?true:false;
        if(rs.execute(sql)){
            String[] columnNames = rs.getColumnName();
            List<String> old = Arrays.asList(fields.split(","));
            List<String> params = new ArrayList<>(old);
            while (rs.next()){
                List<Object> data = new ArrayList<>();
                for (String columnName : columnNames) {
                    data.add(Util.null2String(rs.getString(columnName)));
                }
                if(SQL_EXECUTE_TYPE.TYPE_INSERT.equals(executeType)){
                    if(isFormmode){
                        //插入建模
                        insertFormmode(table, params, data,formmodeId , rsInsert);
                    }else{
                        insert(table, params, data, rsInsert);
                    }
                }else if(SQL_EXECUTE_TYPE.TYPE_UPDATE.equals(executeType)){
                    //update(table, params, data, rsInsert);
                }
            }
        }else{
            log.info(rs.getExceptionMsg());
        }
    }

    public static String getMaxId(String table, RecordSet rs) {
        String getData = "select max(id) from "+table;
        rs.executeQuery(getData);
        return rs.next()?rs.getString(1):"";
    }


    //todo 分页处理

}
