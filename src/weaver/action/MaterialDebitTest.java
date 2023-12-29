package weaver.action;
;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dt_wsdl.WebCon.Conn;

import com.weaver.general.BaseBean;
import org.dom4j.DocumentException;
import org.json.XML;


public class MaterialDebitTest {
    public static void main(String[] args) throws JSONException, org.json.JSONException, DocumentException {
//        String url = "http://lgdev.erp.sap.wingskysemi.local:8000/sap/bc/srt/rfc/sap/zmm_wm_gi/520/zmm_wm_gi/zmm_wm_gi"; // 替换为实际的SOAP服务端点URL
//        String soapAction = "urn:sap-com:dotions:ZMM_WM_GI";

        // 构建SOAP请求XML
        StringBuilder xmlBuilder = new StringBuilder();

        String url = "http://lgdev.erp.sap.wingskysemi.local:8000/sap/bc/srt/rfc/sap/zmm_wm_gi/520/zmm_wm_gi/zmm_wm_gi"; // 替换为实际的SOAP服务端点URL
        String soapAction = "urn:sap-com:document:sap:rfc:functions"; // 替换为实际的SOAPAction

        xmlBuilder.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">");
        xmlBuilder.append("<soap:Header/>");
        xmlBuilder.append("<soap:Body>");
        xmlBuilder.append("<urn:ZMM_WM_GI>");

        // 添加子元素和内容
        xmlBuilder.append("<ET_MSG>");
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

        xmlBuilder.append("<IT_ITEM>");
        xmlBuilder.append("<item>");
        xmlBuilder.append("<MATNR>600001000001</MATNR>");
        xmlBuilder.append("<MEINS>BOT</MEINS>");
        xmlBuilder.append("<CHARG></CHARG>");
        xmlBuilder.append("<WERKS>CN73</WERKS>");
        xmlBuilder.append("<LGORT></LGORT>");
        xmlBuilder.append("<VAL_TYPE>NEW</VAL_TYPE>");
        xmlBuilder.append("<MENGE>1</MENGE>");
        xmlBuilder.append("</item>");
        xmlBuilder.append("</IT_ITEM>");

        xmlBuilder.append("<I_COSTCENTER>CN63103000</I_COSTCENTER>");
        xmlBuilder.append("<I_MOVE_TYPE>201</I_MOVE_TYPE>");
        xmlBuilder.append("<I_OANUM>CKLL202309122780</I_OANUM>");

        xmlBuilder.append("</urn:ZMM_WM_GI>");
        xmlBuilder.append("</soap:Body>");
        xmlBuilder.append("</soap:Envelope>");

        String s="<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
                "      <urn:ZMM_WM_GI>\n" +
                "            <ET_MSG>\n" +
                "                <item>\n" +
                "                    <TYPE></TYPE>\n" +
                "                    <ID></ID>\n" +
                "                    <NUMBER></NUMBER>\n" +
                "                    <MESSAGE></MESSAGE>\n" +
                "                    <LOG_NO></LOG_NO>\n" +
                "                    <LOG_MSG_NO></LOG_MSG_NO>\n" +
                "                    <MESSAGE_V1></MESSAGE_V1>\n" +
                "                    <MESSAGE_V2></MESSAGE_V2>\n" +
                "                    <MESSAGE_V3></MESSAGE_V3>\n" +
                "                    <MESSAGE_V4></MESSAGE_V4>\n" +
                "                    <PARAMETER></PARAMETER>\n" +
                "                    <ROW></ROW>\n" +
                "                    <FIELD></FIELD>\n" +
                "                    <SYSTEM></SYSTEM>\n" +
                "                </item>\n" +
                "            </ET_MSG>\n" +
                "            <IT_ITEM>\n" +
                "                <item>\n" +
                "                    <MATNR>600001000001</MATNR>\n" +
                "                    <MEINS>BOT</MEINS>\n" +
                "                    <CHARG></CHARG>\n" +
                "                    <WERKS>CN73</WERKS>\n" +
                "                    <LGORT></LGORT>\n" +
                "                    <VAL_TYPE>NEW</VAL_TYPE>\n" +
                "                    <MENGE>1</MENGE>\n" +
                "                </item>\n" +
                "            </IT_ITEM>\n" +
                "            <I_COSTCENTER>CN63103000</I_COSTCENTER>\n" +
                "            <I_MOVE_TYPE>201</I_MOVE_TYPE>\n" +
                "            <I_OANUM>CKLL202309122780</I_OANUM>\n" +
                "        </urn:ZMM_WM_GI>\n" +
                "   </soap:Body>\n" +
                "</soap:Envelope>";


        System.out.println("xml"+xmlBuilder);
        System.out.println(s);

        // 创建连接对象
        Conn conn = new Conn();

        String response = conn.doPostSapSoap(url,xmlBuilder.toString(), soapAction);

        JSONObject jsonObject =  null;
        jsonObject=conn.xml2Json(response);

        String json = jsonObject.toString();

        System.out.println(json);

        JSONObject zmmWmGiResponse = jsonObject.getJSONObject("Body").getJSONObject("ZMM_WM_GIResponse");
        JSONObject etMsg = zmmWmGiResponse.getJSONObject("ET_MSG").getJSONObject("item");

        String type = etMsg.getString("TYPE");
        String message = etMsg.getString("MESSAGE");



        // 打印构建的SOAP请求XML
        System.out.println("SOAP请求XML:");
        System.out.println(xmlBuilder.toString());

        System.out.println("TYPE================="+type+"========message==========="+message);


//
//        // 打印SOAP响应
//        System.out.println("SOAP响应:");
//        System.out.println(response);
    }
}
