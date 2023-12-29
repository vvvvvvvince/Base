package com.help;

import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.workflow.webservices.*;

import java.util.List;
import java.util.Map;

/**
 * @author wangyifan
 * @version 1.0.0
 * @ClassName CreateWorkFlowUtil.java
 * @Description TODO
 * @createTime 2022年05月25日 16:21:00
 */
public class CreateWorkFlowUtil {

    public String createWorkFlow(List<Map<String,String>> zblist,List<List<List<Map<String,String>>>> mxblist,String workflowid,String lcbt,String createrid) {
        String requestid="";
        Logger log= LoggerFactory.getLogger();
        log.info("createWorkFlow-->start");
        try {
            //流程信息-start
            WorkflowRequestInfo workflowRequestInfo = new WorkflowRequestInfo();//工作流程请求信息
            //workflowRequestInfo.setRequestId(String.valueOf(1918557));//流程请求ID-创建流程时自动产生
            //workflowRequestInfo.setCanView(true);//显示
            //workflowRequestInfo.setCanEdit(true);//可编辑
            workflowRequestInfo.setRequestName(lcbt);//请求标题
            //workflowRequestInfo.setRequestLevel("0");//请求重要级别
            workflowRequestInfo.setCreatorId(createrid);//流程创建人
            WorkflowBaseInfo workflowBaseInfo = new WorkflowBaseInfo();//工作流信息
            workflowBaseInfo.setWorkflowId(workflowid);//流程ID
            //workflowBaseInfo.setWorkflowName("webservice-test");//流程名称
            //workflowBaseInfo.setWorkflowTypeId("1951");//流程类型id
            //workflowBaseInfo.setWorkflowTypeName("webservice-test");//流程类型名称
            workflowRequestInfo.setWorkflowBaseInfo(workflowBaseInfo);//工作流信息
            //流程信息-end
            //主表-start
            WorkflowMainTableInfo workflowMainTableInfo = new WorkflowMainTableInfo();//主表
            WorkflowRequestTableRecord[] wrtri = new WorkflowRequestTableRecord[1];//主字段只有一行数据（1条记录）
            WorkflowRequestTableField[] wrti = new WorkflowRequestTableField[zblist.size()]; //字段信息
            for (int i = 0; i < zblist.size(); i++) {
                wrti[i] = new WorkflowRequestTableField();
                wrti[i].setFieldName(zblist.get(i).get("zbmc"));
                wrti[i].setFieldValue(zblist.get(i).get("zbvalue"));
                wrti[i].setView(true);//字段是否可见
                wrti[i].setEdit(true);//字段是否可编辑
            }
            wrtri[0] = new WorkflowRequestTableRecord();
            wrtri[0].setWorkflowRequestTableFields(wrti);
            workflowMainTableInfo.setRequestRecords(wrtri);
            workflowRequestInfo.setWorkflowMainTableInfo(workflowMainTableInfo);
            //主表-end
            //明细表-start
            if (mxblist.size()!=0){
                WorkflowDetailTableInfo[] workflowDetailTableInfo = new WorkflowDetailTableInfo[mxblist.size()];//N个明细表
                for (int k = 0; k < mxblist.size(); k++) {//循环每个明细表(明细表个数)
                    List mxbgslist = mxblist.get(k);
                    if (mxbgslist.size()<=0){
                        continue;
                    } else {
                        wrtri = new WorkflowRequestTableRecord[mxbgslist.size()];//每行数据（每条记录）
                        for (int i = 0; i < mxbgslist.size(); i++) {//循环每个明细表的每行数据(每个明细表有几条数据)
                            List<Map<String, String>> zdlist = (List<Map<String, String>>) mxbgslist.get(i);
                            wrti = new WorkflowRequestTableField[zdlist.size()];//每行n个字段
                            for (int j = 0; j < zdlist.size(); j++) {//循环每个明细表的每行数据的每个字段
                                /**********第一张明细表开始**********/
                                /****第一行开始****/
                                wrti[j] = new WorkflowRequestTableField();
                                wrti[j].setFieldName(zdlist.get(j).get("mxmc"));
                                wrti[j].setFieldValue(zdlist.get(j).get("mxvalue"));
                                wrti[j].setView(true);
                                wrti[j].setEdit(true);
                                /****第一行结束****/
                            }
                            wrtri[i] = new WorkflowRequestTableRecord();
                            wrtri[i].setWorkflowRequestTableFields(wrti);
                        }
                        workflowDetailTableInfo[k] = new WorkflowDetailTableInfo();
                        workflowDetailTableInfo[k].setWorkflowRequestTableRecords(wrtri);
                    }
                }
                workflowRequestInfo.setWorkflowDetailTableInfos(workflowDetailTableInfo);
            }
            //明细表-end
            WorkflowService WorkflowServicePortTypeProxy = new WorkflowServiceImpl();
            requestid = WorkflowServicePortTypeProxy.doCreateWorkflowRequest(workflowRequestInfo, Integer.valueOf(createrid));
        } catch (Exception e) {
            log.info("createWorkFlow-->Exception-->"+e.toString());
        }
        return requestid;
    }
    public String createWorkFlowByMain(Map<String, String> zblist, String lcbt, String creater,String workflowid){
        WorkflowRequestTableField[] wrti = new WorkflowRequestTableField[zblist.size()]; //字段信息
        Logger log= LoggerFactory.getLogger();
        log.info("createWorkFlow-->start");
        log.info("zblist.size()-->"+zblist.size());
        int i = 0;
        for (Map.Entry<String, String> resultMap : zblist.entrySet()) {
            log.info("resultMap.getKey()-->"+resultMap.getKey());
            log.info("resultMap.getValue()-->"+resultMap.getValue());
            wrti[i] = new WorkflowRequestTableField();
            wrti[i].setFieldName(resultMap.getKey());
            wrti[i].setFieldValue(resultMap.getValue());
            wrti[i].setView(true);//字段是否可见
            wrti[i].setEdit(true);//字段是否可编辑
            i++;
        }
        WorkflowRequestTableRecord[] wrtri = new WorkflowRequestTableRecord[1];//主字段只有一行数据
        wrtri[0] = new WorkflowRequestTableRecord();
        wrtri[0].setWorkflowRequestTableFields(wrti);
        WorkflowMainTableInfo wmi = new WorkflowMainTableInfo();
        wmi.setRequestRecords(wrtri);
        WorkflowBaseInfo wbi = new WorkflowBaseInfo();
        wbi.setWorkflowId(workflowid);
        WorkflowRequestInfo wri = new WorkflowRequestInfo();//流程基本信息
        wri.setIsnextflow("1");//0为不流转；1为流转
        wri.setCreatorId(creater);//创建人id
        wri.setRequestLevel("0");//0 正常，1重要，2紧急
        wri.setRequestName(lcbt);//流程标题
        wri.setWorkflowMainTableInfo(wmi);//添加主字段数据
        wri.setWorkflowBaseInfo(wbi);
        //执行创建流程接口
        //WorkflowServicePortTypeProxy WorkflowServicePortTypeProxy = new WorkflowServicePortTypeProxy();
        WorkflowService WorkflowServicePortTypeProxy = new WorkflowServiceImpl();
        String requestid = WorkflowServicePortTypeProxy.doCreateWorkflowRequest(wri, Integer.valueOf(creater));
        log.info("requestid-->"+requestid);
        return requestid;
    }
}
