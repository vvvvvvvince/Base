package com.weaver.util.dev.dao;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.google.common.collect.Lists;
import com.weaver.util.dev.log.DevLog;
import com.weaver.util.dev.log.LogFactory;
import org.apache.commons.lang3.StringUtils;
import weaver.conn.RecordSetTrans;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description 事务类的DaoUtils
 */
public class TransDaoUtils {

    private static final LogFactory log = new DevLog();
    private RecordSetTrans recordSet = null;

    public TransDaoUtils() {
        recordSet = new RecordSetTrans();
        recordSet.setAutoCommit(false);
    }

    public String dbType() {
        return recordSet.getDBType();
    }

    public void insert(String tableName, List<String> params, List<Object> data) throws Exception {
        if (StringUtils.isEmpty(tableName)) {
            log.info("执行插入语句 => 表名为空");
            return;
        }
        if (Objects.isNull(params) || params.isEmpty() || Objects.isNull(data) || data.isEmpty()) {
            log.info("执行插入语句 => 字段集合为空或数据集合为空");
            return;
        }
        executeUpdate(createInsertSql(tableName, params), data);
    }

    public void executeUpdate(String sql, List<Object> param) throws Exception {
        log.info("执行SQL => " + sql);
        log.info("SQL参数 => " + Arrays.toString(param.toArray()));
        recordSet.executeUpdate(sql, param.toArray(new Object[]{}));
    }

    public void executeUpdate(String sql, Object... objs) throws Exception {
        log.info("执行SQL => " + sql);
        log.info("SQL参数 => " + Arrays.toString(objs));
        recordSet.executeUpdate(sql, objs);
    }

    public String createInsertSql(String tableName, List<String> params) {
        StringBuilder sql = new StringBuilder(String.format("insert into %s (", tableName));
        StringBuilder addParam = new StringBuilder(" values (");
        int i = 0;
        List<Object> paramList = Lists.newArrayList();
        for (String param : params) {
            if (i++ == 0) {
                sql.append(" " + param);
                addParam.append(" ?");
            } else {
                sql.append(", " + param);
                addParam.append(", ?");
            }
        }
        return sql.toString() + ")" + addParam.toString() + ")";
    }

    public String querySingleVal(String sql, Object... objs) throws Exception {
        log.info("执行SQL => " + sql);
        log.info("SQL参数 => " + Arrays.toString(objs));
        recordSet.executeQuery(sql, objs);
        if (recordSet.next()) {
            return recordSet.getString(1);
        }
        return null;
    }

    public Map<String, String> executeQueryToMap(String sql, Object... objs) throws Exception {
        log.info("执行SQL => " + sql);
        log.info("SQL参数 => " + Arrays.toString(objs));
        if (recordSet.executeQuery(sql, objs)) {
            if (recordSet.next()) {
                return recordSetToMap(recordSet);
            }
        }
        return null;
    }

    public List<Map<String, String>> executeQueryToMapList(String sql, Object... objs) throws Exception {
        log.info("执行SQL => " + sql);
        log.info("SQL参数 => " + Arrays.toString(objs));
        List<Map<String, String>> res = Lists.newArrayList();
        if (recordSet.executeQuery(sql, objs)) {
            while (recordSet.next()) {
                res.add(recordSetToMap(recordSet));
            }
        }
        return res;
    }
    private Map<String, String> recordSetToMap(RecordSetTrans recordSet) {
        Map<String, String> map = new CaseInsensitiveMap();
        for (String s : recordSet.getColumnName()) {
            map.put(s, recordSet.getString(s));
        }
        return map;
    }

    /**
     * 映射RecordSet到JavaBean
     *
     * @param recordSet
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T recordSetMapToBean(RecordSetTrans recordSet, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
//                // 获取是否有注解
//                DaoField daoField = field.getAnnotation(DaoField.class);
//                if (Objects.nonNull(daoField)) {
//                    if (!daoField.value()) { // 为false
//                        continue;
//                    }
//                }
                field.setAccessible(true);
                String fieldName = field.getName();
                String value = recordSet.getString(fieldName);
                field.set(t, value);
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public <T> T recordSetMapToBean(RecordSetTrans recordSet, Class<T> clazz, Map<String, String> fieldMap) {
        try {
            T t = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                String value = recordSet.getString(fieldMap.get(fieldName));
                field.setAccessible(true);
                field.set(t, value);
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

//    public static void batchExecute(String sql, List<List<Object>> param) {
//        BatchRecordSet batchRecordSet = new BatchRecordSet();
//
//        log.info("执行SQL => " + sql);
//        batchRecordSet.executeBatchSql(sql, param);
//    }

    public void commit() {
        recordSet.commit();
    }

    public void rollback() {
        recordSet.rollback();
    }

}
