package com.weaver.util.dev.bean;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import weaver.general.Util;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体类操作工具类
 */
public class BeanUtils {

    public static List<Object> beanToAttrListWithSq(Object object, List<String> fields) throws Exception {
        List<Object> objectList = Lists.newArrayList();
//        for (Field declaredField : object.getClass().getDeclaredFields()) {
//            System.out.println(declaredField.getName());
//        }
        for (String fieldStr : fields) {
            Field field = object.getClass().getDeclaredField(fieldStr);
            field.setAccessible(true);
            objectList.add(field.get(object));
        }
        return objectList;
    }

    public static Map<String, Object> beanToAttrMap(Object object) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        Map<String, Object> res = Maps.newHashMap();
        for (Field field : fields) {
            field.setAccessible(true);
            res.put(field.getName(), field.get(object));
        }
        return res;
    }

    public static List<String> beanAttrNameList(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
    }

    public static Map<String, String> beanToAttrMapNoNull(Object object) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        Map<String, String> res = Maps.newHashMap();
        for (Field field : fields) {
            field.setAccessible(true);
            if (Objects.isNull(field.get(object))) {
                continue;
            }
            res.put(field.getName(), Util.null2String(field.get(object)));
        }
        return res;
    }

    /**
     * 将对象值转为Map<String, String>
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, String> beanToMap(Object obj) throws IllegalAccessException {
        Map<String, String> res = Maps.newHashMap();
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = field.get(obj);
            res.put(name, Util.null2String(value));
        }
        return res;
    }

    public static void main(String[] args) {
        Queue<Integer> queue = new LinkedList<>();
        queue.peek();
        while (queue.isEmpty());
    }
}
