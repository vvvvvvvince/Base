package com.weaver.util.dev.workflow;

import com.weaver.util.dev.PropertiesUtil;
import com.weaver.util.dev.log.DevLog;
import com.weaver.util.dev.log.LogFactory;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: ebuCodes
 * @ClassName DevWorkflowAction  流程action 父类
 * @description:
 *     基础workflow开发父类，封装了基础操作方法，实现类集成该只需要关注业务操作。
 * @author: slfang
 * @create: 2023-06-27 16:58
 * @Version 1.0
 **/
public abstract class DevWorkflowAction implements Action {

    protected LogFactory log = new DevLog();

    @Override
    public String execute(RequestInfo requestInfo) {
        String executeName =this.getClass().getSimpleName();
        log.info(executeName+"==>开始执行");
        long startTime = System.currentTimeMillis();
        log.info("开始执行业务逻辑代码==>"+ startTime);
        com.weaver.util.dev.workflow.ActionExecuteInfo actionExecuteInfo = execCode(requestInfo);
        long endTime = System.currentTimeMillis();
        log.info("结束业务逻辑代码==>"+endTime);
        log.info("程序运行时间为：" + (endTime - startTime) + "ms");
        if(actionExecuteInfo.getStatus()){
            return Action.SUCCESS;
        }else{
            requestInfo.getRequestManager().setMessageid(String.valueOf(System.currentTimeMillis()));
            requestInfo.getRequestManager().setMessagecontent(actionExecuteInfo.getError());
            return Action.FAILURE_AND_CONTINUE;
        }
    }


    public abstract ActionExecuteInfo execCode(RequestInfo requestInfo);

    /**
     * @description: 适用于action首处理，获取基础信息+配置信息
     * @author: fsl
     * @date: 2023/2/27 16:04
     * @param: request
     * @param: name
     * @param: hasConfig 是否存在配置文件
     * @return: java.util.Map<java.lang.String,java.lang.String>
     **/
    public Map<String, String> getBaseInfo(RequestInfo request, String name, boolean hasConfig) {
        log.info(name + "::start");
        Map<String, String> baseInfo = new HashMap(20);
        String requestId = Util.null2String(request.getRequestid());
        String table = Util.null2String(request.getRequestManager().getBillTableName());
        String workFlowId = Util.null2String(request.getWorkflowid());
        String userId = Util.null2String(request.getRequestManager().getUserId());
        baseInfo.put("requestId", requestId);
        baseInfo.put("table", table);
        baseInfo.put("userId", userId);
        baseInfo.put("workFlowId", workFlowId);
        if(hasConfig){
            PropertiesUtil.readProAndSetStringMap(name,baseInfo);
        }
        return baseInfo;
    }
}
