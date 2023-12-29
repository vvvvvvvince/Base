package com.dt_wsdl.WebCon;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import com.alibaba.fastjson.JSONArray;
import org.dom4j.Element;
import weaver.general.BaseBean;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;

public class Conn {
    Logger log = LoggerFactory.getLogger();

        public  String doPostSoap (String url, String soap, String SOAPAction){
            log.info("FDC断点1");
            //请求体
            String retStr = "";
            // 创建HttpClientBuilder
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            // HttpClient
            CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
            HttpPost httpPost = new HttpPost(url);
            try {
                httpPost.setHeader("Content-Type", "text/xml;charset=utf-8");
                //httpPost.setHeader("Transfer-Encoding","chunked");
                httpPost.setHeader("action", SOAPAction);
                //StringEntity se = new StringEntity(soap, HTTP.); se.setContentType("text/xml");
                StringEntity data = new StringEntity(soap, Charset.forName("UTF-8"));
                data.setContentType("text/xml");
                httpPost.setEntity(data);
                CloseableHttpResponse response = (CloseableHttpResponse) closeableHttpClient
                        .execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    // 打印响应内容
                    retStr = EntityUtils.toString(httpEntity, "UTF-8");
                    System.err.println("response:" + retStr);
                }
                // 释放资源
                closeableHttpClient.close();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                log.info("发送异常"+e.toString());
            }
            catch (IOException e)
            {log.info("IO异常"+e.toString());

            }
            return retStr;
        }
    public  JSONObject xml2Json(String xmlStr) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        JSONObject json = new JSONObject();
        dom4j2Json(doc.getRootElement(), json);
        return json;
    }


    /**
     * xml转json
     * @param element
     * @param json
     */
    public void dom4j2Json(Element element, JSONObject json) {
        List<Element> chdEl = element.elements();
        for(Element e : chdEl) {
            if (!e.elements().isEmpty()) {
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) {
                        JSONObject jsono = (JSONObject) o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }
            } else {
                if (!e.getText().isEmpty()) {
                    json.put(e.getName(), e.getText());
                }
            }
        }}
    public  String doPostSapSoap (String url, String soap, String SOAPAction){
        //请求体
        String retStr = "";
        BaseBean bb = new BaseBean();
        String username= "OA_USER";
        String password= "P@ssw0rd";
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
            //httpPost.setHeader("Transfer-Encoding","chunked");
            httpPost.setHeader("action", SOAPAction);
            log.info("SAPDN密码用户："+password+username);
            String Authorization = "Basic " + Base64.getUrlEncoder().encodeToString((username + ":" + password).getBytes());
            log.info("SAPDN_Auth："+Authorization);
            httpPost.addHeader("Authorization",Authorization);
            //StringEntity se = new StringEntity(soap, HTTP.); se.setContentType("text/xml");
            StringEntity data = new StringEntity(soap, Charset.forName("UTF-8"));
            data.setContentType("text/xml");
            httpPost.setEntity(data);
            CloseableHttpResponse response = (CloseableHttpResponse) closeableHttpClient
                    .execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            log.info("SAPDN接口返回值："+response);
            if (httpEntity != null) {
                // 打印响应内容
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
               log.info("SAPDN接口返回值处理："+retStr);
                System.err.println("response:" + retStr);
            }
            // 释放资源
            closeableHttpClient.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            log.info("SAPDN接口返回值异常："+e.toString());
        }
        catch (IOException e)
        {
            log.info("SAPDN接口返回值IO："+e.toString());
        }
        return retStr;
    }}


