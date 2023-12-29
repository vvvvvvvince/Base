package weaver.action;

import com.alibaba.fastjson.JSONObject;
import com.dt_wsdl.WebCon.Conn;
import com.weaver.general.BaseBean;
import com.weaver.util.dev.workflow.ActionExecuteInfo;
import com.weaver.util.dev.workflow.DevWorkflowAction;
import weaver.conn.RecordSet;
import weaver.soa.workflow.request.RequestInfo;
import java.util.Map;

public class Dingtai_MaterialDebit extends DevWorkflowAction {


    @Override
    public ActionExecuteInfo execCode(RequestInfo requestInfo) {

        log.info("........................STARTS SAP Debit Materials API GET EFLOW　INFROM.................................");

        String requestid=requestInfo.getRequestid();
        String tableName=requestInfo.getRequestManager().getBillTableName();

        log.info(".......................Request ID==="+requestid+"...........Tablename===="+tableName+"..............................");

        Map<String, String> baseInfo = getBaseInfo(requestInfo, this.getClass().getSimpleName(), true);

        String TableName=baseInfo.get("TableName");
        String DetableName=baseInfo.get("DeTableName");
        String MOVETYPE=baseInfo.get("MOVETYPE");

        log.info(".......................Tablename==="+TableName+"...........DeTablename===="+DetableName+"..............................");

        BaseBean bb= new BaseBean();

        String url = baseInfo.get("wsdl");
        String soapAction =baseInfo.get("soapAction");
        log.info("..................wsdlAddress  is========"+url+"...SOAPACTION.."+soapAction+"................................");

        //数据库连接实例
        RecordSet rs=new RecordSet();
        RecordSet rsdetail=new RecordSet();

        try{
            String TableSql="select lcbh,cbzx from "+TableName+" where requestid='"+requestid+"'";
            log.info("......MAINTABLE.SQL:"+TableSql+"...............");
            rs.executeQuery(TableSql);

            rs.next();
            String FLOWID= rs.getString("lcbh");
            String COSTCENTER=rs.getString("cbzx");

            log.info("......MAINTABLE.SQL:"+TableSql+".......OANO:"+FLOWID+"..........COSTCENTER:"+COSTCENTER+".................");

            StringBuilder xmlBuilder = new StringBuilder();
            xmlBuilder.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">");
            xmlBuilder.append("<soap:Header/>");
            xmlBuilder.append("<soap:Body>");
            xmlBuilder.append("<urn:ZMM_WM_GI>");
            xmlBuilder.append("<!--Optional:-->");
            xmlBuilder.append("<ET_MSG>");
            xmlBuilder.append("<!--Zero or more repetitions:-->");
            xmlBuilder.append("<item>");
            xmlBuilder.append("<TYPE></TYPE>");
            xmlBuilder.append("<ID></ID>");
            xmlBuilder.append("<NUMBER></NUMBER>");
            xmlBuilder.append("<MESSAGE></MESSAGE>");
            xmlBuilder.append("<LOG_NO></LOG_NO>");
            xmlBuilder.append("<LOG_MSG_NO></LOG_MSG_NO>");
            xmlBuilder.append("<MESSAGE_V1></MESSAGE_V1>");
            xmlBuilder.append("<MESSAGE_V2></MESSAGE_V2>");
            xmlBuilder.append("<MESSAGE_V3></MESSAGE_V3>");
            xmlBuilder.append("<MESSAGE_V4></MESSAGE_V4>");
            xmlBuilder.append("<PARAMETER></PARAMETER>");
            xmlBuilder.append("<ROW></ROW>");
            xmlBuilder.append("<FIELD></FIELD>");
            xmlBuilder.append("<SYSTEM></SYSTEM>");
            xmlBuilder.append("</item>");
            xmlBuilder.append("</ET_MSG>");
            xmlBuilder.append("<!--Optional:-->");
            xmlBuilder.append("<IT_ITEM>");

            log.info("......................Debit Material XML==========="+xmlBuilder);

            String DetailSql="SELECT b.LH ,b.pm,b.CW,b.plant,b.DW,b.pc FROM "+tableName+" a LEFT JOIN "+DetableName+" b ON a.ID =b.MAINID WHERE requestid= '"+requestid+"'";
            rsdetail.executeQuery(DetailSql);

            log.info("DetailSql=="+DetailSql);

            while(rsdetail.next()) {

                String MaterialNo = rsdetail.getString("LH");
                String MaterialDes = rsdetail.getString("pm");
                String MaterialUnit = rsdetail.getString("DW");
                String MaterialBatch = rsdetail.getString("pc");
                String MaterialPlant = rsdetail.getString("Plant");
                String MaterialWH = rsdetail.getString("CW");
                String ValType=baseInfo.get("VALType");

                xmlBuilder.append("<item>");
                xmlBuilder.append("<MATNR>").append(MaterialNo).append("</MATNR>");
                xmlBuilder.append("<MEINS>").append(MaterialUnit).append("</MEINS>");
                xmlBuilder.append("<CHARG>").append(MaterialBatch).append("</CHARG>");
                xmlBuilder.append("<WERKS>").append(MaterialPlant).append("</WERKS>");
                xmlBuilder.append("<LGORT>").append(MaterialWH).append("</LGORT>");
                xmlBuilder.append("<VAL_TYPE>").append(ValType).append("</VAL_TYPE>");
                xmlBuilder.append("<MENGE>1</MENGE>");
                xmlBuilder.append("</item>");
            }

            xmlBuilder.append("</IT_ITEM>");

            xmlBuilder.append("<!--Optional:-->");
            xmlBuilder.append(String.format("<I_COSTCENTER>%s</I_COSTCENTER>", COSTCENTER));
            xmlBuilder.append("<!--Optional:-->");
            xmlBuilder.append(String.format("<I_MOVE_TYPE>%s</I_MOVE_TYPE>", MOVETYPE));
            xmlBuilder.append("<!--Optional:-->");
            xmlBuilder.append(String.format("<I_OANUM>%s</I_OANUM>", FLOWID));
            xmlBuilder.append("</urn:ZMM_WM_GI>");
            xmlBuilder.append("</soap:Body>");
            xmlBuilder.append("</soap:Envelope>");

            String xmlString = xmlBuilder.toString();

            log.info("..........url===="+url+"...SOAPACTION"+soapAction+".......Debit Material XML==========="+xmlString);

            Conn conn=new Conn();

            String response = conn.doPostSapSoap(url, xmlBuilder.toString(), soapAction);

            log.info("SAP RESPONSE　INFO=========="+response+"");

            JSONObject jsonObject =  null;
            jsonObject=conn.xml2Json(response);

            String json = jsonObject.toString();


            JSONObject zmmWmGiResponse = jsonObject.getJSONObject("Body").getJSONObject("ZMM_WM_GIResponse");
            JSONObject etMsg = zmmWmGiResponse.getJSONObject("ET_MSG").getJSONObject("item");


            String eMBLNR = zmmWmGiResponse.getString("E_MBLNR");
            String type = etMsg.getString("TYPE");
            String message = etMsg.getString("MESSAGE");


            log.info("...................Response Type=="+type+".........Response Message==="+message+".............SAP NO=="+eMBLNR);

            if(!type.equals("S")){
                    return new ActionExecuteInfo(false, "物料除帐失败"+message);
            }

            String upsql="update "+tableName+" set result2='"+message+"',sapbh='"+eMBLNR+"' where requestid='"+requestid+"'";
            log.info("..............UPDATE SQL======"+upsql);
            rs.executeQuery(upsql);

        }catch (Exception e){
            log.info("........................ Debit Materials API ERROR......."+e);
            return new ActionExecuteInfo(false, "物料除帐失败"+e.getMessage());

        }

        return new ActionExecuteInfo(true, "物料除帐成功");
    }
}
