package weaver.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.weaver.util.dev.workflow.ActionExecuteInfo;
import weaver.conn.RecordSet;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.interfaces.workflow.action.Action;
import com.weaver.util.dev.workflow.DevWorkflowAction;
import weaver.soa.workflow.request.RequestInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DingTai_HiKivisionvisitor  extends DevWorkflowAction {

    private static final String ARTEMIS_PATH = "";
    Logger log=LoggerFactory.getLogger();


    @Override
    public ActionExecuteInfo execCode(RequestInfo requestInfo) {

        log.info("...............................获取表单数据..............................................");//获取表名与requestid

        String requestid=requestInfo.getRequestid();
        String tableName=requestInfo.getRequestManager().getBillTableName();
        //数据库连接实例
        RecordSet rs=new RecordSet();
        RecordSet rsdetail=new RecordSet();
        //JSONArray visitorInfoList = new JSONArray();
        HashMap<String, Object> jsonBody = new HashMap();
        List<HashMap<String,Object>> visitorInfoList = new ArrayList<>();
        Map<String, String> baseInfo = getBaseInfo(requestInfo, this.getClass().getSimpleName(), true);


        try{
            log.info("...............................数据开始查询.............................................");
            rs.executeQuery("SELECT a.lastname,a.LFrqGSH,a.jsrqgsh,a.LFSY2 FROM FORMTABLE_MAIN_34 a WHERE a.REQUESTID =?",requestid);
            log.info("...................requestid" + requestid + "..............................................");
            if (rs.next()) {
                String lastname = rs.getString(1);
                String visStartDate = rs.getString(2);
                String visEndDate = rs.getString(3);
                String visResult = rs.getString(4);

                //数据库数据获取
                String VisitedInfoResponse = getPeronInfo(lastname,baseInfo);
                String personID = getPersonID(VisitedInfoResponse);

                jsonBody.put("receptionistId", personID);

                log.info("...................HIKIVISIONPERSONID===="+personID + "..............................................");


                JSONObject visitorPermissionSet = new JSONObject();
                visitorPermissionSet.put("defaultPrivilegeGroupFlag", "1");
                JSONArray privilegeGroupIds = new JSONArray();
                privilegeGroupIds.add("793d155-3bd3-4b2a-bb23-8507552863");
                visitorPermissionSet.put("privilegeGroupIds", privilegeGroupIds);
                jsonBody.put("visitorPermissionSet", visitorPermissionSet);
                jsonBody.put("visitStartTime", visStartDate);
                jsonBody.put("visitEndTime", visEndDate);
                jsonBody.put("visitPurpose", visResult);

                rsdetail.executeQuery("SELECT b.fkxm,b.zjhm ,b.sjhm ,b.LFDW , b.cph,CASE b.FKLX\n" +
                        "                            WHEN 2 THEN '一般访客'\n" +
                        "                            WHEN 3 THEN 'VIP访客'\n" +
                        "                            END AS FKLX ,b.xb\n" +
                        "                            FROM FORMTABLE_MAIN_34_DT1 b LEFT JOIN FORMTABLE_MAIN_34 a on b.mainid=a.id where a.REQUESTID =?",requestid);

                while (rsdetail.next()){
                    //JSONObject visitorInfo = new JSONObject();
                    HashMap<String,Object> visitorInfo = new HashMap<>();
                    String visName = rsdetail.getString(1);
                    String visID = rsdetail.getString(2);
                    String visPhone = rsdetail.getString(3);
                    String visCompany=rsdetail.getString(4);
                    String visPlateNo=rsdetail.getString(5);
                    String visGender=rsdetail.getString(7);
                    log.info("....................detailInfomation.................."+visName+"//"+visID+"//"+visPhone+"//"+visCompany+".................................");
                    visitorInfo.put("visitorName", visName);
                    visitorInfo.put("gender", visGender);
                    visitorInfo.put("phoneNo", visPhone);
                    visitorInfo.put("plateNo", visPlateNo);
                    visitorInfo.put("certificateType", 111);
                    visitorInfo.put("certificateNo", visID);
                    visitorInfo.put("nation", 1);
                    visitorInfo.put("visitorWorkUnit", visCompany);
                    log.info("...............................visitorInfo.........."+visitorInfo+".............................................");
                    visitorInfoList.add(visitorInfo);
                }

                jsonBody.put("visitorInfoList", visitorInfoList);
                String visInfo = JSON.toJSONString(jsonBody);
                log.info(".............................GET VISITOR SUCCESS！THEN POST.........."+visInfo+".............................................");
                String result = visitorRegiste(visInfo,baseInfo);
                log.info("...............................POST Visitor Information Result........." + result + ".............................................");
                JSONObject res = JSONObject.parseObject(result);
                String resCode = res.getString("code");

                rs.executeQuery("update FORMTABLE_MAIN_34 set result='"+result+"'"+"where requestid='"+requestid+"'");


                if(!resCode.equals("0")){
                    return new ActionExecuteInfo(false, "访客验证码获取失败");
                }
                Map<String, String> visitorMap=ReVisitorCode(result);//获取访客验证码

                for (Map.Entry<String, String> entry : visitorMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    log.info(".........................key....."+key+"....value.........."+value+".............................................");
                    rsdetail.executeQuery("update  FORMTABLE_MAIN_34_dt1 set fkyzm= '" + value +"'"+ "where fkxm= '"+ key+"'");
                }
            }
        }
        catch(Exception e){
            log.info(".................................数据获取异常"+e+"..................................................");
            return new ActionExecuteInfo(false, "访客验证码注册失败");
        }

        return new ActionExecuteInfo(true);
    }

    public  String visitorRegiste(String visInfo,Map<String, String> baseInfo) throws Exception {
        try {
            ArtemisConfig config = new ArtemisConfig();


            config.setHost(baseInfo.get("VisiterRigsHost")); // 代理API网关nginx服务器ip端口
            config.setAppKey(baseInfo.get("VisiterRigsAppKey"));  // 秘钥appkey
            config.setAppSecret(baseInfo.get("VisiterRigsAppSecret"));// 秘钥appSecret

            final String getCamsApi = baseInfo.get("VisiterRigsAPI");
            Map<String, String> path = new HashMap<String, String>(2);
            path.put("https://", getCamsApi);
//         Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数

            String result= ArtemisHttpUtil.doPostStringArtemis(config, path, visInfo, null, null, "application/json");

            return  result;
        }
        catch (Exception e){
            log.info(".................................访客数据推送异常.........."+e+"..................................................");
            return "Error";
        }

    }

    public  String getPeronInfo(String lastname,Map<String, String> baseInfo) {

        try {
            log.info(".......................STARTS.....................................................");
            ArtemisConfig config = new ArtemisConfig();


            config.setHost(baseInfo.get("PersonInfoHost")); // 代理API网关nginx服务器ip端口
            config.setAppKey(baseInfo.get("PersonInfoAppKey"));  // 秘钥appkey
            config.setAppSecret(baseInfo.get("PersonInfoAppSecret"));// 秘钥appSecret
            String getPersonInfoAPI = baseInfo.get("PersonInfoAPI");
            Map<String, String> path = new HashMap<String, String>();

            path.put("https://", getPersonInfoAPI);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("personName", lastname);
            log.info("....................set JSONBODY........................."+jsonBody+"..............................");
            jsonBody.put("pageNo", 1);
            jsonBody.put("pageSize", 1000);
            String body = jsonBody.toJSONString();
            log.info(".......................Get PersonInfo JSON BODY..........."+body+"..........................................");
            String s = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
            log.info("...................REQUEST INFO.........."+ s+".......................");
            return s;
        }
        catch (Exception e)
        {
            log.info(".................................数据获取异常"+e+"..................................................");
            return "Error";

        }
    }

    public  String getPersonID( String infoResponse){

        JSONObject jsonResponseObj = JSONObject.parseObject(infoResponse);
        JSONArray listArray = jsonResponseObj.getJSONObject("data").getJSONArray("list");
        JSONObject personObj = listArray.getJSONObject(0);
        return personObj.getString("personId");

    }

    public Map<String, String> ReVisitorCode(String result) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);

        JsonNode appointmentInfoList = rootNode.path("data").path("appointmentInfoList");
        Map<String, String> VisitorMap = new HashMap<>();
        for (JsonNode appointmentInfo : appointmentInfoList) {
            String verificationCode = appointmentInfo.path("verificationCode").asText();
            String visitorName = appointmentInfo.path("visitorName").asText();
            VisitorMap.put(visitorName, verificationCode);
        }

        return VisitorMap;
    }


}


