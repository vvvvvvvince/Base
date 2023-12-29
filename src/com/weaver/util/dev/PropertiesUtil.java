/**
 * @projectName classBeanEcology
 * @package ebu.util
 * @className ebu.util.PropertiesUtil
 * @copyright weaver, Inc All rights reserved.
 */
package com.weaver.util.dev;

import weaver.general.BaseBean;
import weaver.general.GCONST;
import weaver.general.Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * @description oa 配置读取工具类
 * @author fsl
 * @date 2021/2/27 16:09
 * @version 1.0
 */
public class PropertiesUtil {

    private static BaseBean bean = new BaseBean();

    /**
     * 直接用BaseBean 读取配置文件读取解决中文乱码：
     *     propValue = new String(propValue.getBytes("ISO-8859-1"),"UTF-8");
     * 读取配置设置map
     * @param fileName 配置文件名
     * @throws IOException
     * version 3.0
     */
    public static Map<String,String> readProAndSetStringMap(String fileName, Map<String, String> baseInfo)  {
        bean.writeLog("readProAndSetStringMap::start");
        Properties properties = new Properties();
        InputStreamReader reader = null;
        FileInputStream inputStream = null;
        String path= GCONST.getPropertyPath()+fileName + ".properties";
        try {
            inputStream = new FileInputStream(path);
            reader = new InputStreamReader(inputStream, "UTF-8");
            properties.load(reader);
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()){
                String key = Util.null2String(enumeration.nextElement());
                String val = com.weaver.general.Util.null2String((String) properties.get(key));
                baseInfo.put(key,val);
            }
            inputStream.close();
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        bean.writeLog("配置信息::"+baseInfo);
        return baseInfo;
    }

    public static Map<String,Object> readProAndSetMap(String fileName, Map<String, Object> baseInfo)  {
        bean.writeLog("readProAndSetMap::start");
        Properties properties = new Properties();
        InputStreamReader reader = null;
        FileInputStream inputStream = null;
        String path= GCONST.getPropertyPath()+fileName + ".properties";
        try {
            inputStream = new FileInputStream(path);
            reader = new InputStreamReader(inputStream, "UTF-8");
            properties.load(reader);
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()){
                String key = Util.null2String(enumeration.nextElement());
                String val = com.weaver.general.Util.null2String((String) properties.get(key));
                baseInfo.put(key,val);
            }
            inputStream.close();
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        bean.writeLog("配置信息::"+baseInfo);
        return baseInfo;
    }


}
