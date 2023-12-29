package weaver.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.WorkflowDetailTableInfoEntity;
import com.entity.WorkflowRequestTableField;
import com.entity.WorkflowRequestTableRecord;
import com.weaver.util.dev.http.IdentityVerifyUtil;
import com.weaver.util.dev.workflow.ActionExecuteInfo;
import com.weaver.util.dev.workflow.DevWorkflowAction;
import weaver.conn.RecordSet;

import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.soa.workflow.request.RequestInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DingTai_shipmentcreateaction extends DevWorkflowAction {

    Logger log = LoggerFactory.getLogger();
    @Override
    public ActionExecuteInfo execCode(RequestInfo requestInfo) {

        RecordSet rssqr =new RecordSet();

        Map<String, String> baseInfo = getBaseInfo(requestInfo, this.getClass().getSimpleName(), true);

        String requestId=baseInfo.get("requestId");
        int imagepath=Integer.parseInt(baseInfo.get("docpath")) ;

        log.info("...........................requestid="+requestId+".....................");

        String getSqrSql="select sqrid,sqrq from formtable_main_656 where requestid=?";

        try {

            rssqr.executeQuery(getSqrSql, requestId);
            rssqr.next();
            String sqr = rssqr.getString(1);
            String ApplyDate = rssqr.getString(2);
            log.info("...........................申请人="+sqr+".....................");
            Map<String, Object> params = new HashMap<>();
            params.put("requestName", "原出货申请单触发" + ApplyDate+requestId);
            params.put("mainData", getFormMainData(requestId,ApplyDate));
            params.put("detailData", getFormDetailData(requestId));
            params.put("otherParams","{\"isnextflow\": "+baseInfo.get("isNextFlow")+" }");
            params.put("workflowId", baseInfo.get("workflowId"));
            params.put("isnextflow", "0");
            params.put("remark", "Re-export flow create flow");
            String json = JSON.toJSONString(params);
            log.info("参数：：" + json);

            //请求json参数  {requestName=2022-12月1111考勤报表确认(采购部)--test, mainData=[{"edit":false,"fieldName":"sqrq","fieldOrder":0,"fieldValue":"2021-12-12","mand":false,"view":false},{"edit":false,"fieldName":"zbxje","fieldOrder":0,"fieldValue":"2","mand":false,"view":false}], detailData=, remark=restful接2222222测试, workflowId=44}

            IdentityVerifyUtil identityVerifyUtil = new IdentityVerifyUtil(baseInfo.get("appid"), baseInfo.get("host"));
            identityVerifyUtil.regist();
            String spk = identityVerifyUtil.getSPK();
            String secret = identityVerifyUtil.getSECRET();
            String token = identityVerifyUtil.getToken();
            if (spk == null || secret == null || token == null) {
                return new ActionExecuteInfo(false, "token 获取失败");
            }
            String url = baseInfo.get("host") + "/api/workflow/paService/doCreateRequest";
            com.weaver.util.dev.http.HttpManager http = new com.weaver.util.dev.http.HttpManager();
            log.info("请求地址::" + url);
            Map<String, String> heads = identityVerifyUtil.getHttpHeads(token, sqr, spk);
            String res = http.postDataSSL(url, params, heads);

            log.info("...................................数据请求.................................." +res);

            if(!JSONObject.parseObject(res).get("code").equals("SUCCESS")){
                return new ActionExecuteInfo(false,"流程创建失败");
            }

        } catch (Exception e){
            e.printStackTrace();
            return new ActionExecuteInfo(false,"流程创建失败");
        }
        return new ActionExecuteInfo(true);
    }

    /**
     * 主表数据
     *
     * 附件上传 包含base64, http等
     * 包含浏览框数据，单行文本数据
     * @return
     */
    private  String  getFormMainData(String requestId,String ApplyDate){

        RecordSet rs=new RecordSet();

        String MainSql="SELECT sqlx1,chrq,jjrlxdh,sjrlxdh,sjr,khmc,swzp,sqrid,sqrbm,lcbh FROM formtable_main_656 a where requestid=?";
        rs.executeQuery(MainSql,requestId);
        rs.next();

        String ApplyType=rs.getString(1);
        String ExitDate=rs.getString(2);
        String SenderPhone=rs.getString(3);
        String RecvPhone=rs.getString(4);
        String RecHum=rs.getString(5);
        String RecvCom=rs.getString(6);
        String ItemImgID=rs.getString(7);
        String ApplyUser=rs.getString(8);
        String ApplyeDepartment=rs.getString(9);
        String FlowID=rs.getString(10);


        List<WorkflowRequestTableField> mainData = new ArrayList<>();


//        附件上传字段
//        ImageFileManager imageFileManager = DocUtils.getImageFileInfoByDocid(Integer.parseInt(ItemImgID), new RecordSet());
//        InputStream imageStream = DocUtils.getInputStream(Integer.parseInt(ItemImgID),new RecordSet());
//        Integer applyid = new Integer(ApplyUser);
//        int result=DocUtils.uploadFileStream(imageStream,imageFileManager.getImageFileName(),applyid,imagepath);
//        log.info("................. Attachement ID =="+result +"................................................");
//        WorkflowRequestTableField img = new WorkflowRequestTableField();
//        img.setFieldName("jcwpzp");
//        img.setFieldValue(Integer.toString (result));
//        mainData.add(img);

        WorkflowRequestTableField SQRQ = new WorkflowRequestTableField();
        SQRQ.setFieldName("sqrq");
        SQRQ.setFieldValue(ApplyDate);
        mainData.add(SQRQ);
        
        WorkflowRequestTableField SQR = new WorkflowRequestTableField();
        SQR.setFieldName("sqr");
        SQR.setFieldValue(ApplyUser);
        mainData.add(SQR);

        WorkflowRequestTableField SqrPhone = new WorkflowRequestTableField();
        SqrPhone.setFieldName("sqrlxdh");
        SqrPhone.setFieldValue(SenderPhone);
        mainData.add(SqrPhone);

        WorkflowRequestTableField OutReason = new WorkflowRequestTableField();
        OutReason.setFieldName("wpcmcyy");
        OutReason.setFieldValue("成品订单出货");
        mainData.add(OutReason);

        WorkflowRequestTableField OutDoorTime = new WorkflowRequestTableField();
        OutDoorTime.setFieldName("wpjcmsj1");
        OutDoorTime.setFieldValue(ExitDate);
        mainData.add(OutDoorTime);

        WorkflowRequestTableField recvDepart = new WorkflowRequestTableField();
        recvDepart.setFieldName("jsbm");
        recvDepart.setFieldValue(RecvCom);
        mainData.add(recvDepart);

        WorkflowRequestTableField sqrDepart = new WorkflowRequestTableField();
        sqrDepart.setFieldName("sqrbm");
        sqrDepart.setFieldValue(ApplyeDepartment);
        mainData.add(sqrDepart);

        WorkflowRequestTableField AppleType = new WorkflowRequestTableField();
        AppleType.setFieldName("jcmwplx");
        AppleType.setFieldValue(ApplyType);
        mainData.add(AppleType);

        WorkflowRequestTableField RecvAddr = new WorkflowRequestTableField();
        RecvAddr.setFieldName("jsgsdz");
        RecvAddr.setFieldValue(RecvCom);
        mainData.add(RecvAddr);

        WorkflowRequestTableField RecvName = new WorkflowRequestTableField();
        RecvName.setFieldName("jsgsmc");
        RecvName.setFieldValue(RecvCom);
        mainData.add(RecvName);

        WorkflowRequestTableField RecvPerson = new WorkflowRequestTableField();
        RecvPerson.setFieldName("jsr");
        RecvPerson.setFieldValue(RecHum);
        mainData.add(RecvPerson);

        WorkflowRequestTableField Phonenum = new WorkflowRequestTableField();
        Phonenum.setFieldName("lxdh");
        Phonenum.setFieldValue(RecvPhone);
        mainData.add(Phonenum);

        WorkflowRequestTableField isBack = new WorkflowRequestTableField();
        isBack.setFieldName("wpsfxyfhbgs");
        isBack.setFieldValue("1");
        mainData.add(isBack);


        WorkflowRequestTableField OutArea = new WorkflowRequestTableField();
        OutArea.setFieldName("wpcmqy");
        OutArea.setFieldValue("1");
        mainData.add(OutArea);

        WorkflowRequestTableField isOutPart = new WorkflowRequestTableField();
        isOutPart.setFieldName("sfwwqxhwxdparts");
        isOutPart.setFieldValue("0");
        mainData.add(isOutPart);

        WorkflowRequestTableField FlType = new WorkflowRequestTableField();
        FlType.setFieldName("djlx");
        FlType.setFieldValue("0");
        mainData.add(FlType);

        WorkflowRequestTableField BackTime = new WorkflowRequestTableField();
        BackTime.setFieldName("fhbgsrq");
        BackTime.setFieldValue("");
        mainData.add(BackTime);

        WorkflowRequestTableField EXitArea = new WorkflowRequestTableField();
        EXitArea.setFieldName("wbjcmqy");
        EXitArea.setFieldValue("1");
        mainData.add(EXitArea);

        WorkflowRequestTableField FLOWNO = new WorkflowRequestTableField();
        FLOWNO.setFieldName("ysdh");
        FLOWNO.setFieldValue(FlowID);
        mainData.add(FLOWNO);



        String MainInform=JSON.toJSONString(mainData);

        log.info(".................GET Flow Information =="+MainInform +"................................................");

        return MainInform;
    }
    /**
     * 明细数据
     * @return
     */
    private String getFormDetailData(String requestId){

        RecordSet rsdetail=new RecordSet();
        String detailsql = "SELECT b.lotid3,b.lotqty FROM formtable_main_656_dt1 b LEFT JOIN formtable_main_656 a ON b.MAINID =a.ID where requestId=?";
        rsdetail.executeQuery(detailsql,requestId);
        List<WorkflowDetailTableInfoEntity> details = new ArrayList<>();
        //明细信息
        WorkflowDetailTableInfoEntity detail1 = new WorkflowDetailTableInfoEntity();
        detail1.setTableDBName("formtable_main_614_dt1");
        //明细数据
        List<WorkflowRequestTableRecord> detailRows = new ArrayList<>();

        while (rsdetail.next()) {

            String Lotid=rsdetail.getString(1);
            String lotqty=rsdetail.getString(2);


            WorkflowRequestTableRecord row1 = new WorkflowRequestTableRecord();

            //明细行数据
            List<WorkflowRequestTableField> rowDatas = new ArrayList<>();

            //行字段数据
            WorkflowRequestTableField ItemName = new WorkflowRequestTableField();
            ItemName.setFieldName("wpmc");
            ItemName.setFieldValue(Lotid);
            rowDatas.add(ItemName);

            WorkflowRequestTableField num = new WorkflowRequestTableField();
            num.setFieldName("sl");
            num.setFieldValue(lotqty);
            rowDatas.add(num);

            WorkflowRequestTableField FixedAssets = new WorkflowRequestTableField();
            FixedAssets.setFieldName("sfwgdzc");
            FixedAssets.setFieldValue("1");
            rowDatas.add(FixedAssets);

            WorkflowRequestTableField brand = new WorkflowRequestTableField();
            brand.setFieldName("pp");
            brand.setFieldValue("无");
            rowDatas.add(brand);

            WorkflowRequestTableField Description = new WorkflowRequestTableField();
            Description.setFieldName("jtwpxjms");
            Description.setFieldValue("成品");
            rowDatas.add(Description);

            row1.setWorkflowRequestTableFields(rowDatas);
            detailRows.add(row1);
            detail1.setWorkflowRequestTableRecords(detailRows);
            row1.setRecordOrder(0);
        }

        details.add(detail1);
        String detailInformation=JSONObject.toJSONString(details);

        log.info(".................GET Detail Information =="+detailInformation+"................................................");

        return detailInformation;

    }

}
