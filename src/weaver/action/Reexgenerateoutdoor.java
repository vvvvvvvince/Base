package weaver.action;

import com.weaver.util.dev.PropertiesUtil;
import com.weaver.util.dev.workflow.ActionExecuteInfo;
import com.weaver.util.dev.workflow.DevWorkflowAction;
import com.weaver.util.dev.workflow.WorkflowUtils;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import java.util.HashMap;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Reexgenerateoutdoor extends DevWorkflowAction {

    Logger log = LoggerFactory.getLogger();
    WorkflowUtils workflowUtils = new WorkflowUtils();

    @Override
    public ActionExecuteInfo execCode(RequestInfo requestInfo) {
        Map<String, String> createbaseInfo = getBaseInfo(requestInfo, this.getClass().getSimpleName(), false);
        HashMap<String,Object> baseInfo = new HashMap<>();
        PropertiesUtil.readProAndSetMap("Re_exportJsonSyncJob",baseInfo);

        RecordSet rs=new RecordSet();
        String Re_export_sql = "SELECT shgsdz,scswzp,sqsx,sqr,sqrbm,sqrdh,sqsx,shgs,shgsdz,shgs,LXR,dh,sffh,qt,sqyy,wbcmqy,yjccsj,sfwwqxhwxdparts,djlx　FROM formtable_main_470 a where requestid=?";
        String Re_export_detailsql = "SELECT a.hwmc,a.sldw,a.pp,a.sfgdzc,a.xjms FROM formtable_main_470_dt1 a";
        try{
            log.info("...............................数据开始查询.............................................");
             rs.executeQuery(Re_export_sql,createbaseInfo.get("requestId"));
             log.info(".................GET--->requestId.........."+createbaseInfo.get("requestId")+".......................................");


            if(rs.next()){//获取流程主表数据
                String ComAdress=rs.getString(1);
                String ItemImg=rs.getString(2);
                String ApplyType=rs.getString(3);
                String ApplyUser=rs.getString(4);
                String ApplyeDepartment=rs.getString(5);
                String ApplyPhone=rs.getString(7);
                String ItemType=rs.getString(8);
                String RecvComAddres=rs.getString(9);
                String RecvCom=rs.getString(10);
                String RecvDepart=rs.getString(11);
                String RecvUser=rs.getString(12);
                String RecvPhone=rs.getString(13);
                String isReply=rs.getString(14);
                String OtherItem=rs.getString(15);
                String ApplyReason=rs.getString(16);
                String ExitArea=rs.getString(17);
                String ExitDate=rs.getString(18);
                String isParts=rs.getString(19);
                String FlowType=rs.getString(20);

                //主表数据
                List<String> mainDataList = new ArrayList<>();
                baseInfo.put("MAIN_DATA_LIST",mainDataList);

                mainDataList.add(ComAdress);
                mainDataList.add(ItemImg);
                mainDataList.add(ApplyType);
                mainDataList.add(ApplyUser);
                mainDataList.add(ApplyeDepartment);
                mainDataList.add(ApplyPhone);
                mainDataList.add(ItemType);
                mainDataList.add(RecvComAddres);
                mainDataList.add(RecvCom);
                mainDataList.add(RecvDepart);
                mainDataList.add(RecvUser);
                mainDataList.add(RecvPhone);
//                mainDataList.add(isReply);
//                mainDataList.add(OtherItem);
//                mainDataList.add(ApplyReason);
//                mainDataList.add(ExitArea);
//                mainDataList.add(ExitDate);
//                mainDataList.add(isParts);
//                mainDataList.add(FlowType);

                log.info("..........主表数据............"+mainDataList+"...............................");

                String requestId = workflowUtils.createWorkflow(baseInfo);
                log.info("生成requestid==>"+requestId);

                if(Integer.valueOf(requestId)<0){
                    //执行报错
                    log.info("流程创建失败::"+WorkflowUtils.ERROR_REQUEST_CODE_MAP.get(requestId));
                    return new ActionExecuteInfo(false,"流程创建失败");

                }
            }

        }catch (Exception e){
            log.info(".................................数据获取异常"+e+"..................................................");
            return new ActionExecuteInfo(false,"数据获取异常");
        }
        return new ActionExecuteInfo(true);
    }


}
