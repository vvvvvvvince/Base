package com.weaver.util.dev.job;
import com.alibaba.fastjson.JSON;
import com.weaver.util.dev.PropertiesUtil;
import com.weaver.util.dev.http.HttpRequest;
import com.weaver.util.dev.integration.DataResourceIntegrationUtils;
import com.weaver.util.dev.integration.IntegrationUtils;
import com.weaver.util.dev.integration.JsonResourceIntegrationUtils;
import com.weaver.util.dev.log.DevLog;
import com.weaver.util.dev.log.LogFactory;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import weaver.interfaces.schedule.BaseCronJob;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RemoteDataSyncJob extends BaseCronJob {

    private static final String DATASOURCE_TYPE="DATASOURCE";
    private static final String INTERFACE_TYPE ="INTERFACE";

    private String fileName;
    private String type;

    LogFactory log = new DevLog();


    @Override
    public void execute() {
        log.info(fileName+"::集成开始======================================" +
                "====================");
        IntegrationUtils utils = null;
        Map<String,Object> baseInfo = new HashMap<>();
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        propertiesUtil.readProAndSetMap(fileName,baseInfo);
        try {
            if(INTERFACE_TYPE.equals(type)){//
                String jsonClassPathObj = ""+baseInfo.get("jsonClassPath");
                String requestRes = "";
                JSONArray json = null;
                if(StringUtils.isBlank(jsonClassPathObj)){
                    HttpRequest httpRequest = new HttpRequest();
                    httpRequest.sentGet(String.valueOf(baseInfo.get("dataRequestUrl")),new HashMap<>());
                    json = JSONArray.fromObject(requestRes);
                }else{
                    Class t = Class.forName(""+jsonClassPathObj);
                    Method method = t.getMethod("execute",Map.class);
                    json =(JSONArray) method.invoke(t.newInstance(), baseInfo);
                }
                log.info("requestRes:::::::::::::::::::::::::::::::::::::::::"+ JSON.toJSONString(json));
                baseInfo.put("jsonArr",json);
                utils = new JsonResourceIntegrationUtils();
            }else if(DATASOURCE_TYPE.equals(type)){
                utils = new DataResourceIntegrationUtils();
            }
            utils.handler(baseInfo,fileName);
            log.info(fileName+"::集成结束==================================================");
        } catch (Exception e) {
            log.error("集成失败::"+e.getMessage());
        }
    }
}
