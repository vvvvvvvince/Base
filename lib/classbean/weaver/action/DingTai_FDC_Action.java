package weaver.action;

import com.alibaba.fastjson.JSONObject;
import com.dt_wsdl.WebCon.Conn;
import com.help.WsdkByXfireHelp;
import org.apache.commons.lang.StringUtils;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
public class DingTai_FDC_Action implements Action {
    @Override
    public String execute(RequestInfo requestInfo)  {
        Logger log = LoggerFactory.getLogger();
        String Retcode = null;
        String RetMessage=null;
        String requestid = requestInfo.getRequestid();
        String tableName = requestInfo.getRequestManager().getBillTableName();
        String status=" ";
        BaseBean bb=new BaseBean();
        //数据库连接实例
        RecordSet rs = new RecordSet();
        //表名获取失败操作
        if (tableName.equals("")) {
            rs.execute("select tablename from workflow_bill where id=(select formid from workflow_base  where id=(select workflowid from workflow_requestbase where requestid=" + requestid + "))");
            if (rs.next()) {
                tableName = rs.getString("tablename");
            }
        }
        log.info("DingTai_FDC_Action-->requestid=" + requestid + "tablename=" + tableName);
        String query = "select SQRGH,jobid,spyj from " + tableName + " where requestid=" + "'" + requestid + "'";
        log.info("FDC查询语句：" + query);
        rs.executeQuery(query);
        String usrid = "";
        String jobid = "";
        if (rs.next()) {
            usrid = rs.getString(1);
            jobid = rs.getString(2);
            status=rs.getString(3).equals("0")?"Approve":"Reject";
        }
        //在liuchengjiekoupeizhi配置文件里获取wsdl地址
            String wsdlAddress = bb.getPropValue("FDC", "wsdlAddress");
        String strParameter = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:fdc=\"http://Adapter/FDC_eSignOffService\"><soapenv:Header/><soapenv:Body><fdc:eSignOffReqStatus><fdc:eSignOffReqStatus_input><UserId>"+usrid+"</UserId><SignoffNo>"+requestid+"</SignoffNo><JobId>"+jobid+"</JobId><eSignoffStatus>"+status+"</eSignoffStatus><eSignoffMessage></eSignoffMessage></fdc:eSignOffReqStatus_input></fdc:eSignOffReqStatus></soapenv:Body></soapenv:Envelope>";
        // 向HttpClient发送请求
        Conn conn=new Conn();
        log.info("wsdl="+wsdlAddress+"param="+strParameter);
        String returnDatabase = conn.doPostSoap(wsdlAddress,strParameter,"http://10.18.106.18:58251/adapter/eSignOffReqStatus");
        log.info("FDC返回数据："+returnDatabase);
        JSONObject jsonObject = null;
        try {
            // 将请求结果转换成json类型
            jsonObject = conn.xml2Json(returnDatabase);
           Retcode=jsonObject.getJSONObject("Body").getJSONObject("eSignOffReqStatusResponse").getJSONObject("eSignOffReqStatusResult").getString("RetCode");
           RetMessage=jsonObject.getJSONObject("Body").getJSONObject("eSignOffReqStatusResponse").getJSONObject("eSignOffReqStatusResult").getString("RetMessage");
           log.info("FDC返回值："+Retcode+"//"+RetMessage);
        } catch (Exception e) {
            log.info("FDC异常："+e.toString());
           return e.toString();
        }
        if(Retcode.equals("0"))
        {return Action.SUCCESS;}
        else
        {return RetMessage;}
    }
}
