package com.weaver.util.dev.workflow;

import com.engine.common.util.ServiceUtil;
import com.engine.workflow.entity.publicApi.PAResponseEntity;
import com.engine.workflow.entity.publicApi.ReqOperateRequestEntity;
import com.engine.workflow.publicApi.WorkflowRequestOperatePA;
import com.engine.workflow.publicApi.impl.WorkflowRequestOperatePAImpl;
import com.weaver.util.dev.PropertiesUtil;
import com.weaver.util.dev.dao.DaoUtils;
import com.weaver.util.dev.date.DateUtils;
import com.weaver.util.dev.log.DevLog;
import com.weaver.util.dev.log.LogFactory;
import org.apache.commons.lang.StringUtils;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.hrm.resource.ResourceComInfo;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.webservices.*;

import java.util.*;

/**
 * @author fsl
 * @description 流程相关的处理类,相关业务
 *    action获取基础信息操作
 *    流程创建操作
 *    获取流程表信息操作
 *    节点流转操作
 *    相关源码类 com/engine/fna/util/BankEnterpriseUtil.java
 *             com/engine/workflow/publicApi/impl/WorkflowRequestOperatePAImpl.java
 * @version 1.0
 */
public class WorkflowUtils extends BaseBean {

    private static LogFactory log = new DevLog();

    public static HashMap<String,String> ERROR_REQUEST_CODE_MAP= new HashMap<>();

    /**
     * -1：创建流程失败
     * -2：没有创建权限
     * -3：创建流程失败
     * -4：字段或表名不正确
     * -5：更新流程级别失败
     * -6：无法创建流程待办任务
     * -7：流程下一节点出错，请检查流程的配置，在OA中发起流程进行测试
     * -8：流程节点自动赋值操作错误
     */
    static {
        ERROR_REQUEST_CODE_MAP.put("-1","创建流程失败");
        ERROR_REQUEST_CODE_MAP.put("-2","没有创建权限");
        ERROR_REQUEST_CODE_MAP.put("-3","创建流程失败");
        ERROR_REQUEST_CODE_MAP.put("-4","字段或表名不正确");
        ERROR_REQUEST_CODE_MAP.put("-5","更新流程级别失败");
        ERROR_REQUEST_CODE_MAP.put("-6","无法创建流程待办任务");
        ERROR_REQUEST_CODE_MAP.put("-7","流程下一节点出错，请检查流程的配置，在OA中发起流程进行测试");
        ERROR_REQUEST_CODE_MAP.put("-8","流程节点自动赋值操作错误");
    }

    private WorkflowServiceImpl ws = new WorkflowServiceImpl();

    /**
     * @description: 流程action相关数据操作
     **/




    /**
     * @description: 适用于action首处理，获取基础信息+配置信息
     * @author: fsl
     * @date: 2023/2/27 16:04
     * @param: request
     * @param: name
     * @param: hasConfig 是否存在配置文件
     * @return: java.util.Map<java.lang.String,java.lang.String>
     **/
    public Map<String, String> getBaseInfo(RequestInfo request, String name,boolean hasConfig) {
        Map<String, String> baseInfo = new HashMap(20);
        log.info(name + "::start");
        String requestId = request.getRequestid();
        String table = Util.null2String(request.getRequestManager().getBillTableName());
        String workFlowId = request.getWorkflowid();
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

    /**
     * @description: 流程创建以及流程节点流程流转相关操作
     **/

    /**
     * 创建流程的类 根据参数baseInfo中的键值对生成新流程的数据信息
     * 参数前期添加：
     *                 baseInfo.put("creator",creator);//创建者
     *                 baseInfo.put("wid",sub_wid);//流程id
     *                 baseInfo.put("MAIN_DATA_LIST",mainDataList);  //主表的数据
     *                 baseInfo.put("mainFields",SUB_WORKFLOW_MAIN_FIELDS);// 主表字段集合  多个字段用逗号隔开
     *                 baseInfo.put("allDtInfo",allDtInfo);
     *                 //明细数据的设置 明细不存在便不用设置该参数，参数类型 Map<String,Map<String,Object>> allDtInfo  ，allDtInfo的key为明细的索引值明细1为，
     *                            ，value值为一个Map<String,Object>，map中有两个参数  参数1 为fields（明细的字段集合字符串多个用逗号隔开），参数2 为dataList
     *                            ， dataList的数据类型为List<List<String>>  第一层list为明细数据的记录数，  第二层的list为每条数据的字段值的集合，list的插入顺序必须与参数1中
     *                            fields中设置字段的顺序保持一致
     *                 MAX_DT_INDEX:allDtInfo 中设置的最大的表下标  表1就是1 表二就是2
     *                 isNextflow   是否流转到下一节点 没有设置代表留在创建节点，有值代表流转到下一节点 是否自动提交到下节点  为1表示流转到下一节点，0表示停留在创建节点
     *                 注释：附件字段占时未考虑，需要注意，需要自行插入附件生成id
     * @author: fsl
     * @date: 2022/7/12 16:07
     * @param: baseInfo
     * @return: String
     * @versio: 1.0
     **/

    public  String createWorkflow(Map<String,Object> baseInfo)  {
        return create(baseInfo);
    }

    public String create(Map<String,Object> baseInfo){
        try {
            log.info("createWorkflow=================" + baseInfo);
            String creator = "" + baseInfo.get("creator");
            String wid = Util.null2String(baseInfo.get("wid"));
            String getTitleSql="select WORKFLOWNAME from workflow_base where id="+wid;
            log.info("getTitleSql::"+getTitleSql);
            RecordSet rs = new RecordSet();
            rs.execute(getTitleSql);
            rs.next();
            String WORKFLOWNAME = Util.null2String(rs.getString("WORKFLOWNAME"));
            ResourceComInfo resourceComInfo = null;
            try {
                resourceComInfo = new ResourceComInfo();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String lastname = resourceComInfo.getLastname(creator);
            WORKFLOWNAME+="-"+lastname+"-"+ DateUtils.getDate("yyyy-MM-dd");
            log.info("流程创建者::" + creator);
            WorkflowRequestInfo workflowRequestInfo = new WorkflowRequestInfo();
            workflowRequestInfo.setCanView(true);
            workflowRequestInfo.setCanEdit(true);
            workflowRequestInfo.setRequestName(WORKFLOWNAME);
            workflowRequestInfo.setRequestLevel("0");
            String isNextflow = Util.null2String(baseInfo.get("isNextflow"));
            //是否自动提交到下节点  为1表示流转到下一节点，0表示停留在创建节点
            if ("0".equals(isNextflow)) {
                workflowRequestInfo.setIsnextflow("0");
            } else {
                workflowRequestInfo.setIsnextflow("1");
            }
            workflowRequestInfo.setCreatorId("" + creator);
            log.info("WorkflowBaseInfo");
            WorkflowBaseInfo workflowBaseInfo = new WorkflowBaseInfo();
            workflowBaseInfo.setWorkflowId(wid);
            workflowRequestInfo.setWorkflowBaseInfo(workflowBaseInfo);
            String mainFields = "" + baseInfo.get("mainFields");
            List<String> MAIN_DATA_LIST = (List)baseInfo.get("MAIN_DATA_LIST");
            String[] mainArr = mainFields.split(",");
            WorkflowRequestTableField[] wrti = new WorkflowRequestTableField[mainArr.length];
            for(int i = 0; i < mainArr.length; ++i) {
                wrti[i] = new WorkflowRequestTableField();
                wrti[i].setFieldName(mainArr[i]);
                wrti[i].setFieldValue((String)MAIN_DATA_LIST.get(i));
                wrti[i].setEdit(true);
                wrti[i].setView(true);
            }
            WorkflowRequestTableRecord[] wrtri = new WorkflowRequestTableRecord[]{new WorkflowRequestTableRecord()};
            wrtri[0].setWorkflowRequestTableFields(wrti);
            WorkflowMainTableInfo wmi = new WorkflowMainTableInfo();
            wmi.setRequestRecords(wrtri);
            workflowRequestInfo.setWorkflowMainTableInfo(wmi);
            Map<String, Map<String, Object>> allDtInfo = (Map)baseInfo.get("allDtInfo");
            if (allDtInfo != null) {
                int maxDtIndex = Integer.valueOf("" + baseInfo.get("MAX_DT_INDEX"));
                Set<Map.Entry<String, Map<String, Object>>> entries = allDtInfo.entrySet();
                WorkflowDetailTableInfo[] workflowDetailTableInfo = new WorkflowDetailTableInfo[maxDtIndex];
                Iterator var16 = entries.iterator();
                while(var16.hasNext()) {
                    Map.Entry<String, Map<String, Object>> entry = (Map.Entry)var16.next();
                    int dtIndex = Integer.valueOf((String)entry.getKey());
                    Map<String, Object> dtInfo = (Map)entry.getValue();
                    String dtFields = "" + dtInfo.get("fields");
                    List<List<String>> dtData = (List)dtInfo.get("dataList");
                    String[] dtArr = dtFields.split(",");
                    WorkflowRequestTableRecord[] wrtri_dt = new WorkflowRequestTableRecord[dtData.size()];
                    int dtNum = 0;

                    for(Iterator var25 = dtData.iterator(); var25.hasNext(); ++dtNum) {
                        List<String> dtDatum = (List)var25.next();
                        WorkflowRequestTableField[] WorkflowRequestTableField_dt = new WorkflowRequestTableField[dtArr.length];

                        for(int j = 0; j < dtArr.length; ++j) {
                            WorkflowRequestTableField_dt[j] = new WorkflowRequestTableField();
                            WorkflowRequestTableField_dt[j].setFieldName(dtArr[j]);
                            WorkflowRequestTableField_dt[j].setFieldValue(Util.null2String((String)dtDatum.get(j)));
                            WorkflowRequestTableField_dt[j].setView(true);
                            WorkflowRequestTableField_dt[j].setEdit(true);
                        }
                        wrtri_dt[dtNum] = new WorkflowRequestTableRecord();
                        wrtri_dt[dtNum].setWorkflowRequestTableFields(WorkflowRequestTableField_dt);
                    }
                    workflowDetailTableInfo[dtIndex - 1] = new WorkflowDetailTableInfo();
                    workflowDetailTableInfo[dtIndex - 1].setWorkflowRequestTableRecords(wrtri_dt);
                }
                workflowRequestInfo.setWorkflowDetailTableInfos(workflowDetailTableInfo);
            }
            log.info("doCreateWorkflowRequest before");
            String requestid = ws.doCreateWorkflowRequest(workflowRequestInfo, Integer.valueOf(creator));
            log.info("createWorkflow[requestId]========================================>" + requestid);
            return requestid;
        }catch (Exception e){
            log.error("流程创建失败::"+e.getMessage());
        }
        return "-1";
    }

    /**
     * @description: 根据流程requestid处理提交还是退回
     * @author: fsl
     * @date: 2023/2/27 15:59
     * @param: requestId
     * @param: type   type="submit";//submit 提交 subnoback提交不需回复  subback提交需要回复 reject退回
     *     退回是根据出口退回
     * @param: remark
     * @return: java.lang.String
     **/
    public String handlerWorkflow(String requestId,String type,String remark){
        String nextOpterUserId = getNextOpterUserId(requestId);
        WorkflowRequestInfo wfInfo=ws.getWorkflowRequest(Integer.valueOf(requestId), Integer.valueOf(nextOpterUserId), 0);
        String flag=ws.submitWorkflowRequest(wfInfo, Integer.valueOf(requestId), Integer.valueOf(nextOpterUserId), type, remark);
        return flag;
    }


    /**
     * @Description: 内部调用--流程退回（按出口退回）
     * @param userId 用户id
     * @param requestId 流程请求id
     * @param remark 签字意见
     * @return: com.engine.workflow.entity.publicApi.PAResponseEntity
     * @Author: konghang
     * @Date: 10:28 2022/2/24
     */
    public PAResponseEntity rejectRequest(int userId, int requestId, String remark) {
        User user = new User(userId);
        ReqOperateRequestEntity requestParam = new ReqOperateRequestEntity();
        requestParam.setRequestId(requestId);
        requestParam.setRemark(remark);
        //流程流转扩展参数
        Map<String, Object> otherParams = new HashMap<>();
        requestParam.setOtherParams(otherParams);

        WorkflowRequestOperatePA operatePA = ServiceUtil.getService(WorkflowRequestOperatePAImpl.class);
        return operatePA.rejectRequest(user, requestParam);
    }

    /**
     * @Description: 内部调用--流程退回（指定节点退回）
     * @param userId 用户id
     * @param requestId 流程请求id
     * @param nodeId 指定节点id
     * @param remark 签字意见
     * @return: com.engine.workflow.entity.publicApi.PAResponseEntity
     * @Author: konghang
     * @Date: 10:28 2022/2/24
     */
    public  PAResponseEntity rejectRequest(int userId,int nodeId, int requestId, String remark) {
        User user = new User(userId);
        ReqOperateRequestEntity requestParam = new ReqOperateRequestEntity();
        requestParam.setRequestId(requestId);
        requestParam.setRemark(remark);
        //流程流转扩展参数
        Map<String, Object> otherParams = new HashMap<>();
        otherParams.put("RejectToType","0");
        otherParams.put("RejectToNodeid",nodeId);
        requestParam.setOtherParams(otherParams);
        WorkflowRequestOperatePA operatePA = ServiceUtil.getService(WorkflowRequestOperatePAImpl.class);
        return operatePA.rejectRequest(user, requestParam);
    }


    /**
     * @description: 流程中基础表的信息获取
     **/

    /**
     * 工作流请求基本信息表 workflow_requestbase
     * 工作流基本信息表：workflow_base
     * SELECT
     *    requestId
     *   FROM
     *    workflow_requestbase w
     *   WHERE
     *    w.currentnodetype = 3// 0创建1审批 2提交3归档
     *   AND w.workflowid > 0//防止作废
     *
     */

    /**
     * @description: 获取当前流程未操作者
     * @author: fsl
     * @date: 2022/7/13 11:40
     * @param: requestid
     * @return: java.lang.String
     **/
    public String getNextOpterUserId(String requestid) {
        this.log.info("getNextOpterUserId::start");
        RecordSet rsOp = new RecordSet();
        String sql = "select USERID from workflow_currentoperator where  REQUESTID='" + requestid + "' and isremark=0";
        this.log.info("hasNextOptor::findNextsql::" + sql);
        rsOp.execute(sql);
        rsOp.next();
        String userid = Util.null2String(rsOp.getString("USERID"));
        return userid;
    }

    /**
     * 根据请求id查找表名
     * @param requestId
     * @return
     */
    public String getTableNameByRequestId(String requestId, RecordSet rs) {
        this.log.info("getTableNameByRequestId::start");
        String findSql = "select  b.tableName from workflow_requestbase wrb INNER JOIN workflow_base wb on wrb.workflowid=wb.id INNER JOIN workflow_bill b on  b.id=wb.formid  where wrb.requestid=" + requestId;
        this.log.info("findSql::" + findSql);
        rs.execute(findSql);
        rs.next();
        String name = rs.getString(1);
        this.log.info("tableName::" + name);
        return name;
    }


    /***
     * @description 流程提交生成流水号方案
     *      * 按照年月来流水，每过一个月份重新流水，若需要区分流程可以根据流程的workflowid来流水
     * @author slfang
     * @date 2021/8/25
     * @param ufTable  配置建模表 用于存储当前的流水num值 后期流水在此上新增
     * @param dateMonth
     * @param type  若需要区分流程可以根据流程的workflowid来流水
     * @param formmodeid 建模id
     * @param num  流水号一般为 000121  假如源数据为121  此处num 就是6
     * @return {@link String}
     */
    public String getSerNo(String ufTable, String dateMonth, String type, int formmodeid, int num,RecordSet rs) throws Exception {
        this.log.info("getSerNo::start");
        return this.getSerNo(ufTable, dateMonth, type, formmodeid, num, 1,rs);
    }

    /**
     * @description 流程提交生成流水号方案
     *      * 按照年月来流水，每过一个月份重新流水，若需要区分流程可以根据流程的workflowid来流水
     * @author: fsl
     * @date: 2023/2/27 17:12
     * @param ufTable  配置建模表 用于存储当前的流水num值 后期流水在此上新增  表字段 type,serDate,serNo
     * @param dateMonth
     * @param type  若需要区分流程可以根据流程的workflowid来流水
     * @param formmodeid 建模id
     * @param num  流水号一般为 000121  假如源数据为121  此处num 就是6
     * @param: initCode 给定流水初始值
     * @return: java.lang.String
     **/
    public String getSerNo(String ufTable, String dateMonth, String type, int formmodeid, int num, int initCode,RecordSet rs) throws Exception {
        this.log.info("getSerNo::start");
        String serNO = "";
        String findSerNoSql="select serNo from " + ufTable + " where type='" + type + "' and serDate='" + dateMonth + "'";
        log.info("findSerNoSql::"+findSerNoSql);
        rs.execute(findSerNoSql);
        rs.next();
        String tableSerNo = Util.null2String(rs.getString("serNo"));
        log.info("tableSerNo::" + tableSerNo);
        if (StringUtils.isBlank(tableSerNo)) {//初次流水
            List<String> fields = Arrays.asList("type,serDate,serNo".split(","));
            List<Object> values = new ArrayList();
            values.add(type);
            values.add(dateMonth);
            values.add(initCode);
            DaoUtils.insertFormmode(ufTable, fields, values, formmodeid,rs);
            serNO = String.format("%0" + num + "d", initCode);
        } else {
            int addVal = Integer.valueOf(tableSerNo) + 1;
            this.log.info("addval::" + addVal);
            List<Object> data = new ArrayList<>();
            data.add(addVal);
            data.add(type);
            data.add(dateMonth);
            DaoUtils.executeUpdate("update "+ufTable+"set serNO=? where type=? and serDate=? ",data,rs);
            serNO = String.format("%0" + num + "d", addVal);
        }
        this.log.info("serNO::" + serNO);
        return serNO;
    }
}
