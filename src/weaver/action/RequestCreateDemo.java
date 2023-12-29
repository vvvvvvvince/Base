package weaver.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.WorkflowDetailTableInfoEntity;
import com.entity.WorkflowRequestTableField;
import com.entity.WorkflowRequestTableRecord;
import com.util.HttpManager;
import com.util.IdentityVerifyUtil;
import com.weaver.util.dev.workflow.DevWorkflowAction;
import com.weaver.util.dev.workflow.ActionExecuteInfo;
import weaver.conn.RecordSet;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.soa.workflow.request.RequestInfo;

import java.util.*;

/**
 * 流程创建实例
 * description :
 * author ：fsl
 * date : 2020/4/29
 * version : 1.0
 */
public class RequestCreateDemo extends DevWorkflowAction{

    Logger log = LoggerFactory.getLogger();

    @Override
    public ActionExecuteInfo execCode(RequestInfo requestInfo) {

        Map<String, String> createbaseInfo = getBaseInfo(requestInfo, this.getClass().getSimpleName(), false);
        HashMap<String,Object> baseInfo = new HashMap<>();
        RecordSet rs=new RecordSet();
        String Re_export_sql = "SELECT shgsdz,scswzp,sqsx,sqr,sqrbm,sqrdh,sqsx,shgs,shgsdz,shgs,LXR,dh,sffh,qt,sqyy,wbcmqy,yjccsj,sfwwqxhwxdparts,djlx　FROM formtable_main_470 a where requestid=?";
        String Re_export_detailsql = "SELECT a.hwmc,a.sldw,a.pp,a.sfgdzc,a.xjms FROM formtable_main_470_dt1 a";

        try{
            log.info("...............................数据开始查询.............................................");
            rs.executeQuery(Re_export_sql,createbaseInfo.get("requestId"));
            log.info(".................GET requestId.........."+createbaseInfo.get("requestId")+".......................................");

            if(rs.next()){

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

            }

        }catch(Exception e){
            log.info(".................................数据获取异常"+e+"..................................................");
            return new ActionExecuteInfo(false,"数据获取异常");

        }


        return null;
    }

    public static void main(String[] args) {
        RequestCreateDemo demo = new RequestCreateDemo();
        demo.createRequest("test", "22");
    }

    /**
     * 创建流程入口
     * @param requestnamesuffix
     * @param userid
     */
    public void createRequest(String requestnamesuffix, String userid) {
        IdentityVerifyUtil identityVerifyUtil = IdentityVerifyUtil.getInstance();
        String spk = identityVerifyUtil.getSPK();
        String secret = identityVerifyUtil.getSECRET();
        String token = identityVerifyUtil.getToken();
        if (spk == null || secret == null || token == null) {
            return;
        }
        String url = IdentityVerifyUtil.HOST + "/api/workflow/paService/doCreateRequest";
        HttpManager http = new HttpManager();
        Map<String, String> heads =  IdentityVerifyUtil.getHttpHeads(token,userid,spk);
        System.out.println("heads"+heads);
        Map<String, Object> params = new HashMap<>();
        params.put("requestName", "Re-export flow create flow" + requestnamesuffix);
        params.put("mainData", getFormMainData());
        params.put("detailData",getFormDetailData());
        params.put("workflowId", "43525");
        params.put("isnextflow", "0");
        params.put("remark","Re-export flow create flow");
        System.out.println(JSON.toJSONString(params));

        //请求json参数  {requestName=2022-12月1111考勤报表确认(采购部)--test, mainData=[{"edit":false,"fieldName":"sqrq","fieldOrder":0,"fieldValue":"2021-12-12","mand":false,"view":false},{"edit":false,"fieldName":"zbxje","fieldOrder":0,"fieldValue":"2","mand":false,"view":false}], detailData=, remark=restful接2222222测试, workflowId=44}
        try {
            String res = http.postDataSSL(url, params, heads);
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主表数据
     *
     * 附件上传 包含base64, http等
     * 包含浏览框数据，单行文本数据
     * @return
     */
    private  String  getFormMainData(){
        List<WorkflowRequestTableField> mainData = new ArrayList<>();

        WorkflowRequestTableField image = new WorkflowRequestTableField();
//
//        附件上传字段
        image.setFieldName("jcwpzp");
        List<Map<String,String>> fileInfo = new ArrayList<>();
        Map<String,String> fileItem1 = new HashMap<>();
        fileItem1.put("fileName","settings.jpg");
        String str1 = "base64:+CiAgPCEtLSBsb2NhbFJlcG9zaXRvcnkKICAgfCBUaGUgcGF0aCB0byB0aGUgbG9jYWwgcmVwb3NpdG9yeSBtYXZlbiB3aWxsIHVzZSB0byBzdG9yZSBhcnRpZmFjdHMuCiAgIHwKICAgfCBEZWZhdWx0OiAke3VzZXIuaG9tZX0vLm0yL3JlcG9zaXRvcnkKICA8bG9jYWxSZXBvc2l0b3J5Pi9wYXRoL3RvL2xvY2FsL3JlcG88L2xvY2FsUmVwb3NpdG9yeT4KICAtLT4KCiAgIDxsb2NhbFJlcG9zaXRvcnk+RTovcmVwb3NpdG9yeTwvbG9jYWxSZXBvc2l0b3J5PgoKICA8IS0tIGludGVyYWN0aXZlTW9kZQogICB8IFRoaXMgd2lsbCBkZXRlcm1pbmUgd2hldGhlciBtYXZlbiBwcm9tcHRzIHlvdSB3aGVuIGl0IG5lZWRzIGlucHV0LiBJZiBzZXQgdG8gZmFsc2UsCiAgIHwgbWF2ZW4gd2lsbCB1c2UgYSBzZW5zaWJsZSBkZWZhdWx0IHZhbHVlLCBwZXJoYXBzIGJhc2VkIG9uIHNvbWUgb3RoZXIgc2V0dGluZywgZm9yCiAgIHwgdGhlIHBhcmFtZXRlciBpbiBxdWVzdGlvbi4KICAgfAogICB8IERlZmF1bHQ6IHRydWUKICA8aW50ZXJhY3RpdmVNb2RlPnRydWU8L2ludGVyYWN0aXZlTW9kZT4KICAtLT4KCiAgPCEtLSBvZmZsaW5lCiAgIHwgRGV0ZXJtaW5lcyB3aGV0aGVyIG1hdmVuIHNob3VsZCBhdHRlbXB0IHRvIGNvbm5lY3QgdG8gdGhlIG5ldHdvcmsgd2hlbiBleGVjdXRpbmcgYSBidWlsZC4KICAgfCBUaGlzIHdpbGwgaGF2ZSBhbiBlZmZlY3Qgb24gYXJ0aWZhY3QgZG93bmxvYWRzLCBhcnRpZmFjdCBkZXBsb3ltZW50LCBhbmQgb3RoZXJzLgogICB8CiAgIHwgRGVmYXVsdDogZmFsc2UKICA8b2ZmbGluZT5mYWxzZTwvb2ZmbGluZT4KICAtLT4KCiAgPCEtLSBwbHVnaW5Hcm91cHMKICAgfCBUaGlzIGlzIGEgbGlzdCBvZiBhZGRpdGlvbmFsIGdyb3VwIGlkZW50aWZpZXJzIHRoYXQgd2lsbCBiZSBzZWFyY2hlZCB3aGVuIHJlc29sdmluZyBwbHVnaW5zIGJ5IHRoZWlyIHByZWZpeCwgaS5lLgogICB8IHdoZW4gaW52b2tpbmcgYSBjb21tYW5kIGxpbmUgbGlrZSAibXZuIHByZWZpeDpnb2FsIi4gTWF2ZW4gd2lsbCBhdXRvbWF0aWNhbGx5IGFkZCB0aGUgZ3JvdXAgaWRlbnRpZmllcnMKICAgfCAib3JnLmFwYWNoZS5tYXZlbi5wbHVnaW5zIiBhbmQgIm9yZy5jb2RlaGF1cy5tb2pvIiBpZiB0aGVzZSBhcmUgbm90IGFscmVhZHkgY29udGFpbmVkIGluIHRoZSBsaXN0LgogICB8LS0+CiAgPHBsdWdpbkdyb3Vwcz4KICAgIDwhLS0gcGx1Z2luR3JvdXAKICAgICB8IFNwZWNpZmllcyBhIGZ1cnRoZXIgZ3JvdXAgaWRlbnRpZmllciB0byB1c2UgZm9yIHBsdWdpbiBsb29rdXAuCiAgICA8cGx1Z2luR3JvdXA+Y29tLnlvdXIucGx1Z2luczwvcGx1Z2luR3JvdXA+CiAgICAtLT4KICA8L3BsdWdpbkdyb3Vwcz4KCiAgPCEtLSBwcm94aWVzCiAgIHwgVGhpcyBpcyBhIGxpc3Qgb2YgcHJveGllcyB3aGljaCBjYW4gYmUgdXNlZCBvbiB0aGlzIG1hY2hpbmUgdG8gY29ubmVjdCB0byB0aGUgbmV0d29yay4KICAgfCBVbmxlc3Mgb3RoZXJ3aXNlIHNwZWNpZmllZCAoYnkgc3lzdGVtIHByb3BlcnR5IG9yIGNvbW1hbmQtbGluZSBzd2l0Y2gpLCB0aGUgZmlyc3QgcHJveHkKICAgfCBzcGVjaWZpY2F0aW9uIGluIHRoaXMgbGlzdCBtYXJrZWQgYXMgYWN0aXZlIHdpbGwgYmUgdXNlZC4KICAgfC0tPgogIDxwcm94aWVzPgogICAgPCEtLSBwcm94eQogICAgIHwgU3BlY2lmaWNhdGlvbiBmb3Igb25lIHByb3h5LCB0byBiZSB1c2VkIGluIGNvbm5lY3RpbmcgdG8gdGhlIG5ldHdvcmsuCiAgICAgfAogICAgPHByb3h5PgogICAgICA8aWQ+b3B0aW9uYWw8L2lkPgogICAgICA8YWN0aXZlPnRydWU8L2FjdGl2ZT4KICAgICAgPHByb3RvY29sPmh0dHA8L3Byb3RvY29sPgogICAgICA8dXNlcm5hbWU+cHJveHl1c2VyPC91c2VybmFtZT4KICAgICAgPHBhc3N3b3JkPnByb3h5cGFzczwvcGFzc3dvcmQ+CiAgICAgIDxob3N0PnByb3h5Lmhvc3QubmV0PC9ob3N0PgogICAgICA8cG9ydD44MDwvcG9ydD4KICAgICAgPG5vblByb3h5SG9zdHM+bG9jYWwubmV0fHNvbWUuaG9zdC5jb208L25vblByb3h5SG9zdHM+CiAgICA8L3Byb3h5PgogICAgLS0+CiAgPC9wcm94aWVzPgoKICA8IS0tIHNlcnZlcnMKICAgfCBUaGlzIGlzIGEgbGlzdCBvZiBhdXRoZW50aWNhdGlvbiBwcm9maWxlcywga2V5ZWQgYnkgdGhlIHNlcnZlci1pZCB1c2VkIHdpdGhpbiB0aGUgc3lzdGVtLgogICB8IEF1dGhlbnRpY2F0aW9uIHByb2ZpbGVzIGNhbiBiZSB1c2VkIHdoZW5ldmVyIG1hdmVuIG11c3QgbWFrZSBhIGNvbm5lY3Rpb24gdG8gYSByZW1vdGUgc2VydmVyLgogICB8LS0+CiAgPHNlcnZlcnM+CiAgICA8IS0tIHNlcnZlcgogICAgIHwgU3BlY2lmaWVzIHRoZSBhdXRoZW50aWNhdGlvbiBpbmZvcm1hdGlvbiB0byB1c2Ugd2hlbiBjb25uZWN0aW5nIHRvIGEgcGFydGljdWxhciBzZXJ2ZXIsIGlkZW50aWZpZWQgYnkKICAgICB8IGEgdW5pcXVlIG5hbWUgd2l0aGluIHRoZSBzeXN0ZW0gKHJlZmVycmVkIHRvIGJ5IHRoZSAnaWQnIGF0dHJpYnV0ZSBiZWxvdykuCiAgICAgfAogICAgIHwgTk9URTogWW91IHNob3VsZCBlaXRoZXIgc3BlY2lmeSB1c2VybmFtZS9wYXNzd29yZCBPUiBwcml2YXRlS2V5L3Bhc3NwaHJhc2UsIHNpbmNlIHRoZXNlIHBhaXJpbmdzIGFyZQogICAgIHwgICAgICAgdXNlZCB0b2dldGhlci4KICAgICB8CiAgICA8c2VydmVyPgogICAgICA8aWQ+ZGVwbG95bWVudFJlcG88L2lkPgogICAgICA8dXNlcm5hbWU+cmVwb3VzZXI8L3VzZXJuYW1lPgogICAgICA8cGFzc3dvcmQ+cmVwb3B3ZDwvcGFzc3dvcmQ+CiAgICA8L3NlcnZlcj4KICAgIC0tPgoKICAgIDwhLS0gQW5vdGhlciBzYW1wbGUsIHVzaW5nIGtleXMgdG8gYXV0aGVudGljYXRlLgogICAgPHNlcnZlcj4KICAgICAgPGlkPnNpdGVTZXJ2ZXI8L2lkPgogICAgICA8cHJpdmF0ZUtleT4vcGF0aC90by9wcml2YXRlL2tleTwvcHJpdmF0ZUtleT4KICAgICAgPHBhc3NwaHJhc2U+b3B0aW9uYWw7IGxlYXZlIGVtcHR5IGlmIG5vdCB1c2VkLjwvcGFzc3BocmFzZT4KICAgIDwvc2VydmVyPgogICAgLS0+CiAgPC9zZXJ2ZXJzPgoKICA8IS0tIG1pcnJvcnMKICAgfCBUaGlzIGlzIGEgbGlzdCBvZiBtaXJyb3JzIHRvIGJlIHVzZWQgaW4gZG93bmxvYWRpbmcgYXJ0aWZhY3RzIGZyb20gcmVtb3RlIHJlcG9zaXRvcmllcy4KICAgfAogICB8IEl0IHdvcmtzIGxpa2UgdGhpczogYSBQT00gbWF5IGRlY2xhcmUgYSByZXBvc2l0b3J5IHRvIHVzZSBpbiByZXNvbHZpbmcgY2VydGFpbiBhcnRpZmFjdHMuCiAgIHwgSG93ZXZlciwgdGhpcyByZXBvc2l0b3J5IG1heSBoYXZlIHByb2JsZW1zIHdpdGggaGVhdnkgdHJhZmZpYyBhdCB0aW1lcywgc28gcGVvcGxlIGhhdmUgbWlycm9yZWQKICAgfCBpdCB0byBzZXZlcmFsIHBsYWNlcy4KICAgfAogICB8IFRoYXQgcmVwb3NpdG9yeSBkZWZpbml0aW9uIHdpbGwgaGF2ZSBhIHVuaXF1ZSBpZCwgc28gd2UgY2FuIGNyZWF0ZSBhIG1pcnJvciByZWZlcmVuY2UgZm9yIHRoYXQKICAgfCByZXBvc2l0b3J5LCB0byBiZSB1c2VkIGFzIGFuIGFsdGVybmF0ZSBkb3dubG9hZCBzaXRlLiBUaGUgbWlycm9yIHNpdGUgd2lsbCBiZSB0aGUgcHJlZmVycmVkCiAgIHwgc2VydmVyIGZvciB0aGF0IHJlcG9zaXRvcnkuCiAgIHwtLT4KICA8bWlycm9ycz4KICAgIDwhLS0gbWlycm9yCiAgICAgfCBTcGVjaWZpZXMgYSByZXBvc2l0b3J5IG1pcnJvciBzaXRlIHRvIHVzZSBpbnN0ZWFkIG9mIGEgZ2l2ZW4gcmVwb3NpdG9yeS4gVGhlIHJlcG9zaXRvcnkgdGhhdAogICAgIHwgdGhpcyBtaXJyb3Igc2VydmVzIGhhcyBhbiBJRCB0aGF0IG1hdGNoZXMgdGhlIG1pcnJvck9mIGVsZW1lbnQgb2YgdGhpcyBtaXJyb3IuIElEcyBhcmUgdXNlZAogICAgIHwgZm9yIGluaGVyaXRhbmNlIGFuZCBkaXJlY3QgbG9va3VwIHB1cnBvc2VzLCBhbmQgbXVzdCBiZSB1bmlxdWUgYWNyb3NzIHRoZSBzZXQgb2YgbWlycm9ycy4KICAgICB8CiAgICA8bWlycm9yPgogICAgICA8aWQ+bWlycm9ySWQ8L2lkPgogICAgICA8bWlycm9yT2Y+cmVwb3NpdG9yeUlkPC9taXJyb3JPZj4KICAgICAgPG5hbWU+SHVtYW4gUmVhZGFibGUgTmFtZSBmb3IgdGhpcyBNaXJyb3IuPC9uYW1lPgogICAgICA8dXJsPmh0dHA6Ly9teS5yZXBvc2l0b3J5LmNvbS9yZXBvL3BhdGg8L3VybD4KICAgIDwvbWlycm9yPgogICAgIC0tPgoJIDxtaXJyb3I+CiAgICAgICAgICA8aWQ+bmV4dXMtYWxpeXVuPC9pZD4KCQkgIDxtaXJyb3JPZj5jZW50cmFsPC9taXJyb3JPZj4KICAgICAgICAgIDxuYW1lPk5leHVzIGFsaXl1bjwvbmFtZT4KICAgICAgICAgIDx1cmw+aHR0cDovL21hdmVuLmFsaXl1bi5jb20vbmV4dXMvY29udGVudC9ncm91cHMvcHVibGljPC91cmw+IAogICAgICA8L21pcnJvcj4KICA8L21pcnJvcnM+CgogIDwhLS0gcHJvZmlsZXMKICAgfCBUaGlzIGlzIGEgbGlzdCBvZiBwcm9maWxlcyB3aGljaCBjYW4gYmUgYWN0aXZhdGVkIGluIGEgdmFyaWV0eSBvZiB3YXlzLCBhbmQgd2hpY2ggY2FuIG1vZGlmeQogICB8IHRoZSBidWlsZCBwcm9jZXNzLiBQcm9maWxlcyBwcm92aWRlZCBpbiB0aGUgc2V0dGluZ3MueG1sIGFyZSBpbnRlbmRlZCB0byBwcm92aWRlIGxvY2FsIG1hY2hpbmUtCiAgIHwgc3BlY2lmaWMgcGF0aHMgYW5kIHJlcG9zaXRvcnkgbG9jYXRpb25zIHdoaWNoIGFsbG93IHRoZSBidWlsZCB0byB3b3JrIGluIHRoZSBsb2NhbCBlbnZpcm9ubWVudC4KICAgfAogICB8IEZvciBleGFtcGxlLCBpZiB5b3UgaGF2ZSBhbiBpbnRlZ3JhdGlvbiB0ZXN0aW5nIHBsdWdpbiAtIGxpa2UgY2FjdHVzIC0gdGhhdCBuZWVkcyB0byBrbm93IHdoZXJlCiAgIHwgeW91ciBUb21jYXQgaW5zdGFuY2UgaXMgaW5zdGFsbGVkLCB5b3UgY2FuIHByb3ZpZGUgYSB2YXJpYWJsZSBoZXJlIHN1Y2ggdGhhdCB0aGUgdmFyaWFibGUgaXMKICAgfCBkZXJlZmVyZW5jZWQgZHVyaW5nIHRoZSBidWlsZCBwcm9jZXNzIHRvIGNvbmZpZ3VyZSB0aGUgY2FjdHVzIHBsdWdpbi4KICAgfAogICB8IEFzIG5vdGVkIGFib3ZlLCBwcm9maWxlcyBjYW4gYmUgYWN0aXZhdGVkIGluIGEgdmFyaWV0eSBvZiB3YXlzLiBPbmUgd2F5IC0gdGhlIGFjdGl2ZVByb2ZpbGVzCiAgIHwgc2VjdGlvbiBvZiB0aGlzIGRvY3VtZW50IChzZXR0aW5ncy54bWwpIC0gd2lsbCBiZSBkaXNjdXNzZWQgbGF0ZXIuIEFub3RoZXIgd2F5IGVzc2VudGlhbGx5CiAgIHwgcmVsaWVzIG9uIHRoZSBkZXRlY3Rpb24gb2YgYSBzeXN0ZW0gcHJvcGVydHksIGVpdGhlciBtYXRjaGluZyBhIHBhcnRpY3VsYXIgdmFsdWUgZm9yIHRoZSBwcm9wZXJ0eSwKICAgfCBvciBtZXJlbHkgdGVzdGluZyBpdHMgZXhpc3RlbmNlLiBQcm9maWxlcyBjYW4gYWxzbyBiZSBhY3RpdmF0ZWQgYnkgSkRLIHZlcnNpb24gcHJlZml4LCB3aGVyZSBhCiAgIHwgdmFsdWUgb2YgJzEuNCcgbWlnaHQgYWN0aXZhdGUgYSBwcm9maWxlIHdoZW4gdGhlIGJ1aWxkIGlzIGV4ZWN1dGVkIG9uIGEgSkRLIHZlcnNpb24gb2YgJzEuNC4yXzA3Jy4KICAgfCBGaW5hbGx5LCB0aGUgbGlzdCBvZiBhY3RpdmUgcHJvZmlsZXMgY2FuIGJlIHNwZWNpZmllZCBkaXJlY3RseSBmcm9tIHRoZSBjb21tYW5kIGxpbmUuCiAgIHwKICAgfCBOT1RFOiBGb3IgcHJvZmlsZXMgZGVmaW5lZCBpbiB0aGUgc2V0dGluZ3MueG1sLCB5b3UgYXJlIHJlc3RyaWN0ZWQgdG8gc3BlY2lmeWluZyBvbmx5IGFydGlmYWN0CiAgIHwgICAgICAgcmVwb3NpdG9yaWVzLCBwbHVnaW4gcmVwb3NpdG9yaWVzLCBhbmQgZnJlZS1mb3JtIHByb3BlcnRpZXMgdG8gYmUgdXNlZCBhcyBjb25maWd1cmF0aW9uCiAgIHwgICAgICAgdmFyaWFibGVzIGZvciBwbHVnaW5zIGluIHRoZSBQT00uCiAgIHwKICAgfC0tPgogIDxwcm9maWxlcz4KICAgIDwhLS0gcHJvZmlsZQogICAgIHwgU3BlY2lmaWVzIGEgc2V0IG9mIGludHJvZHVjdGlvbnMgdG8gdGhlIGJ1aWxkIHByb2Nlc3MsIHRvIGJlIGFjdGl2YXRlZCB1c2luZyBvbmUgb3IgbW9yZSBvZiB0aGUKICAgICB8IG1lY2hhbmlzbXMgZGVzY3JpYmVkIGFib3ZlLiBGb3IgaW5oZXJpdGFuY2UgcHVycG9zZXMsIGFuZCB0byBhY3RpdmF0ZSBwcm9maWxlcyB2aWEgPGFjdGl2YXRlZFByb2ZpbGVzLz4KICAgICB8IG9yIHRoZSBjb21tYW5kIGxpbmUsIHByb2ZpbGVzIGhhdmUgdG8gaGF2ZSBhbiBJRCB0aGF0IGlzIHVuaXF1ZS4KICAgICB8CiAgICAgfCBBbiBlbmNvdXJhZ2VkIGJlc3QgcHJhY3RpY2UgZm9yIHByb2ZpbGUgaWRlbnRpZmljYXRpb24gaXMgdG8gdXNlIGEgY29uc2lzdGVudCBuYW1pbmcgY29udmVudGlvbgogICAgIHwgZm9yIHByb2ZpbGVzLCBzdWNoIGFzICdlbnYtZGV2JywgJ2Vudi10ZXN0JywgJ2Vudi1wcm9kdWN0aW9uJywgJ3VzZXItamRjYXNleScsICd1c2VyLWJyZXR0JywgZXRjLgogICAgIHwgVGhpcyB3aWxsIG1ha2UgaXQgbW9yZSBpbnR1aXRpdmUgdG8gdW5kZXJzdGFuZCB3aGF0IHRoZSBzZXQgb2YgaW50cm9kdWNlZCBwcm9maWxlcyBpcyBhdHRlbXB0aW5nCiAgICAgfCB0byBhY2NvbXBsaXNoLCBwYXJ0aWN1bGFybHkgd2hlbiB5b3Ugb25seSBoYXZlIGEgbGlzdCBvZiBwcm9maWxlIGlkJ3MgZm9yIGRlYnVnLgogICAgIHwKICAgICB8IFRoaXMgcHJvZmlsZSBleGFtcGxlIHVzZXMgdGhlIEpESyB2ZXJzaW9uIHRvIHRyaWdnZXIgYWN0aXZhdGlvbiwgYW5kIHByb3ZpZGVzIGEgSkRLLXNwZWNpZmljIHJlcG8uCiAgICA8cHJvZmlsZT4KICAgICAgPGlkPmpkay0xLjQ8L2lkPgoKICAgICAgPGFjdGl2YXRpb24+CiAgICAgICAgPGpkaz4xLjQ8L2pkaz4KICAgICAgPC9hY3RpdmF0aW9uPgoKICAgICAgPHJlcG9zaXRvcmllcz4KICAgICAgICA8cmVwb3NpdG9yeT4KICAgICAgICAgIDxpZD5qZGsxNDwvaWQ+CiAgICAgICAgICA8bmFtZT5SZXBvc2l0b3J5IGZvciBKREsgMS40IGJ1aWxkczwvbmFtZT4KICAgICAgICAgIDx1cmw+aHR0cDovL3d3dy5teWhvc3QuY29tL21hdmVuL2pkazE0PC91cmw+CiAgICAgICAgICA8bGF5b3V0PmRlZmF1bHQ8L2xheW91dD4KICAgICAgICAgIDxzbmFwc2hvdFBvbGljeT5hbHdheXM8L3NuYXBzaG90UG9saWN5PgogICAgICAgIDwvcmVwb3NpdG9yeT4KICAgICAgPC9yZXBvc2l0b3JpZXM+CiAgICA8L3Byb2ZpbGU+CiAgICAtLT4KCiAgICA8IS0tCiAgICAgfCBIZXJlIGlzIGFub3RoZXIgcHJvZmlsZSwgYWN0aXZhdGVkIGJ5IHRoZSBzeXN0ZW0gcHJvcGVydHkgJ3RhcmdldC1lbnYnIHdpdGggYSB2YWx1ZSBvZiAnZGV2JywKICAgICB8IHdoaWNoIHByb3ZpZGVzIGEgc3BlY2lmaWMgcGF0aCB0byB0aGUgVG9tY2F0IGluc3RhbmNlLiBUbyB1c2UgdGhpcywgeW91ciBwbHVnaW4gY29uZmlndXJhdGlvbgogICAgIHwgbWlnaHQgaHlwb3RoZXRpY2FsbHkgbG9vayBsaWtlOgogICAgIHwKICAgICB8IC4uLgogICAgIHwgPHBsdWdpbj4KICAgICB8ICAgPGdyb3VwSWQ+b3JnLm15Y28ubXlwbHVnaW5zPC9ncm91cElkPgogICAgIHwgICA8YXJ0aWZhY3RJZD5teXBsdWdpbjwvYXJ0aWZhY3RJZD4KICAgICB8CiAgICAgfCAgIDxjb25maWd1cmF0aW9uPgogICAgIHwgICAgIDx0b21jYXRMb2NhdGlvbj4ke3RvbWNhdFBhdGh9PC90b21jYXRMb2NhdGlvbj4KICAgICB8ICAgPC9jb25maWd1cmF0aW9uPgogICAgIHwgPC9wbHVnaW4+CiAgICAgfCAuLi4KICAgICB8CiAgICAgfCBOT1RFOiBJZiB5b3UganVzdCB3YW50ZWQgdG8gaW5qZWN0IHRoaXMgY29uZmlndXJhdGlvbiB3aGVuZXZlciBzb21lb25lIHNldCAndGFyZ2V0LWVudicgdG8KICAgICB8ICAgICAgIGFueXRoaW5nLCB5b3UgY291bGQganVzdCBsZWF2ZSBvZmYgdGhlIDx2YWx1ZS8+IGluc2lkZSB0aGUgYWN0aXZhdGlvbi1wcm9wZXJ0eS4KICAgICB8CiAgICA8cHJvZmlsZT4KICAgICAgPGlkPmVudi1kZXY8L2lkPgoKICAgICAgPGFjdGl2YXRpb24+CiAgICAgICAgPHByb3BlcnR5PgogICAgICAgICAgPG5hbWU+dGFyZ2V0LWVudjwvbmFtZT4KICAgICAgICAgIDx2YWx1ZT5kZXY8L3ZhbHVlPgogICAgICAgIDwvcHJvcGVydHk+CiAgICAgIDwvYWN0aXZhdGlvbj4KCiAgICAgIDxwcm9wZXJ0aWVzPgogICAgICAgIDx0b21jYXRQYXRoPi9wYXRoL3RvL3RvbWNhdC9pbnN0YW5jZTwvdG9tY2F0UGF0aD4KICAgICAgPC9wcm9wZXJ0aWVzPgogICAgPC9wcm9maWxlPgogICAgLS0+CgkKCTxwcm9maWxlPiA8aWQ+amRr4oCQMS44PC9pZD4gPGFjdGl2YXRpb24+IDxhY3RpdmVCeURlZmF1bHQ+dHJ1ZTwvYWN0aXZlQnlEZWZhdWx0PiA8amRrPjEuODwvamRrPiA8L2FjdGl2YXRpb24+IDxwcm9wZXJ0aWVzPiA8bWF2ZW4uY29tcGlsZXIuc291cmNlPjEuODwvbWF2ZW4uY29tcGlsZXIuc291cmNlPiA8bWF2ZW4uY29tcGlsZXIudGFyZ2V0PjEuODwvbWF2ZW4uY29tcGlsZXIudGFyZ2V0PiA8bWF2ZW4uY29tcGlsZXIuY29tcGlsZXJWZXJzaW9uPjEuODwvbWF2ZW4uY29tcGlsZXIuY29tcGlsZXJWZXJzaW9uPiA8L3Byb3BlcnRpZXM+IDwvcHJvZmlsZT4KICA8L3Byb2ZpbGVzPgoKICA8IS0tIGFjdGl2ZVByb2ZpbGVzCiAgIHwgTGlzdCBvZiBwcm9maWxlcyB0aGF0IGFyZSBhY3RpdmUgZm9yIGFsbCBidWlsZHMuCiAgIHwKICA8YWN0aXZlUHJvZmlsZXM+CiAgICA8YWN0aXZlUHJvZmlsZT5hbHdheXNBY3RpdmVQcm9maWxlPC9hY3RpdmVQcm9maWxlPgogICAgPGFjdGl2ZVByb2ZpbGU+YW5vdGhlckFsd2F5c0FjdGl2ZVByb2ZpbGU8L2FjdGl2ZVByb2ZpbGU+CiAgPC9hY3RpdmVQcm9maWxlcz4KICAtLT4KPC9zZXR0aW5ncz4K";
        //fileItem1.put("filePath",getBase64str("C:\\Users\\IN00021\\Pictures\\Screenshots\\a.png"));
        fileItem1.put("filePath","https://oa.wingskysemi.com//weaver/weaver.file.FileDownload?fileid=344205");
        fileInfo.add(fileItem1);
        image.setFieldValue(JSONObject.toJSONString(fileInfo));
        mainData.add(image);

        WorkflowRequestTableField SQR = new WorkflowRequestTableField();
        SQR.setFieldName("sqr");
        SQR.setFieldValue("744");
        mainData.add(SQR);

        WorkflowRequestTableField SqrPhone = new WorkflowRequestTableField();
        SqrPhone.setFieldName("sqrlxdh");
        SqrPhone.setFieldValue("18993999999");
        mainData.add(SqrPhone);

        WorkflowRequestTableField OutReason = new WorkflowRequestTableField();
        OutReason.setFieldName("wpcmcyy");
        OutReason.setFieldValue("出门测试");
        mainData.add(OutReason);

        //单人力资源字段
        WorkflowRequestTableField OutDoorTime = new WorkflowRequestTableField();
        OutDoorTime.setFieldName("wpjcmsj1");
        OutDoorTime.setFieldValue("2023-09-21");
        mainData.add(OutDoorTime);

        WorkflowRequestTableField recvDepart = new WorkflowRequestTableField();
        recvDepart.setFieldName("jsbm");
        recvDepart.setFieldValue("上海鼎泰匠心");
        mainData.add(recvDepart);

        WorkflowRequestTableField sqrDepart = new WorkflowRequestTableField();
        sqrDepart.setFieldName("sqrbm");
        sqrDepart.setFieldValue("10996");
        mainData.add(sqrDepart);

        WorkflowRequestTableField AppleType = new WorkflowRequestTableField();
        AppleType.setFieldName("jcmwplx");
        AppleType.setFieldValue("13");
        mainData.add(AppleType);

        WorkflowRequestTableField RecvAddr = new WorkflowRequestTableField();
        RecvAddr.setFieldName("jsgsdz");
        RecvAddr.setFieldValue("上海浦东新区");
        mainData.add(RecvAddr);

        WorkflowRequestTableField RecvName = new WorkflowRequestTableField();
        RecvName.setFieldName("jsgsmc");
        RecvName.setFieldValue("上海鼎泰匠心");
        mainData.add(RecvName);

        WorkflowRequestTableField RecvPerson = new WorkflowRequestTableField();
        RecvPerson.setFieldName("jsr");
        RecvPerson.setFieldValue("jsr");
        mainData.add(RecvPerson);

        WorkflowRequestTableField Phonenum = new WorkflowRequestTableField();
        Phonenum.setFieldName("lxdh");
        Phonenum.setFieldValue("lxdh");
        mainData.add(Phonenum);

        WorkflowRequestTableField isBack = new WorkflowRequestTableField();
        isBack.setFieldName("wpsfxyfhbgs");
        isBack.setFieldValue("1");
        mainData.add(isBack);

        WorkflowRequestTableField Other = new WorkflowRequestTableField();
        Other.setFieldName("qtwplx");
        Other.setFieldValue("其他");
        mainData.add(Other);

        WorkflowRequestTableField OutArea = new WorkflowRequestTableField();
        OutArea.setFieldName("wpcmqy");
        OutArea.setFieldValue("1");
        mainData.add(OutArea);

        WorkflowRequestTableField isParts = new WorkflowRequestTableField();
        isParts.setFieldName("sfwwqxhwxdparts");
        isParts.setFieldValue("0");
        mainData.add(isParts);

        WorkflowRequestTableField FlowType = new WorkflowRequestTableField();
        FlowType.setFieldName("djlx");
        FlowType.setFieldValue("0");
        mainData.add(FlowType);


        System.out.println(mainData);
        return JSON.toJSONString(mainData);
    }
//    public static ImageFileManager getImageFileInfoByDocid(int docid, RecordSet rs) {
//        int imagefileidByDocId = getImagefileidByDocId(docid, rs);
//        ImageFileManager imageFileManager = new ImageFileManager();
//        imageFileManager.getImageFileInfoById(imagefileidByDocId);
//        return imageFileManager;
//    }
    /**
     * @description: 根据文档id获取附件id
     * @author: fsl
     * @date: 2023/4/5 17:16
     * @param: docid
     * @param: rs
     * @return: int
     **/

    /**
     * 根据文件绝对地址返回该文件的base64编码
     *
     * @param aFilePath 文件绝对路径公司总
     * @return
     */
//    public static String getBase64str(String aFilePath) {
//        String re = null;
//        try {
//            File file = new File(aFilePath);
//            if (file.exists()) {
//                Base64.Encoder encoder =  Base64.getEncoder();
//                encoder.encodeToString(FileUtils.fileToByteArray(aFilePath));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return re;
//    }

    /**
     * 明细数据
     * @return
     */
    private String getFormDetailData(){
        List<WorkflowDetailTableInfoEntity> details = new ArrayList<>();

        //明细信息
        WorkflowDetailTableInfoEntity detail1 = new WorkflowDetailTableInfoEntity();
        detail1.setTableDBName("formtable_main_433_dt1");

        //明细数据
        List<WorkflowRequestTableRecord> detailRows = new ArrayList<>();
        WorkflowRequestTableRecord row1 = new WorkflowRequestTableRecord();

        //明细行数据
        List<WorkflowRequestTableField> rowDatas = new ArrayList<>();

        //行字段数据
        WorkflowRequestTableField ItemName = new WorkflowRequestTableField();
        ItemName.setFieldName("wpmc");
        ItemName.setFieldValue("test01");
        rowDatas.add(ItemName);

        WorkflowRequestTableField num = new WorkflowRequestTableField();
        num.setFieldName("sl");
        num.setFieldValue("2");
        rowDatas.add(num);


        WorkflowRequestTableField brand = new WorkflowRequestTableField();
        brand.setFieldName("pp");
        brand.setFieldValue("test brand");
        rowDatas.add(num);

        WorkflowRequestTableField FixedAssets = new WorkflowRequestTableField();
        FixedAssets.setFieldName("sfwgdzc");
        FixedAssets.setFieldValue("0");
        rowDatas.add(FixedAssets);

        WorkflowRequestTableField Description = new WorkflowRequestTableField();
        Description.setFieldName("jtwpxjms");
        Description.setFieldValue("测试描述");
        rowDatas.add(Description);

        row1.setRecordOrder(0);
        row1.setWorkflowRequestTableFields(rowDatas);
        detailRows.add(row1);

        detail1.setWorkflowRequestTableRecords(detailRows);
        details.add(detail1);
        return JSONObject.toJSONString(details);

    }

}

