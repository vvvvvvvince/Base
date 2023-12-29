package com.weaver.util.dev.http;

import weaver.general.BaseBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpRequest {
    private BaseBean bean = new BaseBean();


    public  String sendPost(String url,Map<String,Object> property ,String content) throws Exception {
        String strResult = "";
        InputStreamReader inSteam = null;
        PrintWriter out = null;
        URL httpurl = new URL(url);
        HttpURLConnection httpConn = (HttpURLConnection)httpurl.openConnection();
        httpConn.setConnectTimeout(10000);
        httpConn.setReadTimeout(10000);
        handlerProperty(httpConn,property);
        httpConn.setRequestProperty("content-type", "application/json;charset=UTF-8");
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        out = new PrintWriter(new OutputStreamWriter(httpConn.getOutputStream(), "utf-8"));
        out.print(content);
        out.flush();
        out.close();
        inSteam = new InputStreamReader(httpConn.getInputStream(), "utf-8");
        int nByte ;
        StringBuffer strBuffer = new StringBuffer();
        if (inSteam != null) {
            while(true) {
                if ((nByte = inSteam.read()) == -1) {
                    strResult = strBuffer.toString();
                    inSteam.close();
                    break;
                }

                strBuffer.append((char)nByte);
            }
        }
        return strResult;
    }

    public  String sentGet(String requestUrl,Map<String,Object> property) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(requestUrl);
            connection = (HttpURLConnection)url.openConnection();
            handlerProperty(connection,property);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            Iterator var8 = map.keySet().iterator();

            String line;
            while(var8.hasNext()) {
                line = (String)var8.next();
                System.out.println(line + "---------->" + map.get(line));
            }
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                line = null;
                while((line = br.readLine()) != null) {
                    sbf.append(line);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (Exception var18) {
            var18.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }

                if (null != is) {
                    is.close();
                }
            } catch (Exception var17) {
                var17.printStackTrace();
            }

            connection.disconnect();
        }

        return result;
    }

    public  String delete(String urlStr,Map<String,Object> property) {
        String result = null;
        URL url = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            url = new URL(urlStr);

        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            handlerProperty(httpURLConnection,property);
            httpURLConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.setRequestMethod("DELETE");
            System.out.println(httpURLConnection.getResponseCode());
            if(httpURLConnection.getResponseCode()==200){
                is = httpURLConnection.getInputStream();
                String line;
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                line = null;
                while((line = br.readLine()) != null) {
                    sbf.append(line);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (Exception var17) {
                var17.printStackTrace();
            }
            httpURLConnection.disconnect();
        }
        return result;
    }

    public  String put(String url,Map<String,Object> property ,String content) throws Exception {
        String strResult = "";
        InputStreamReader inSteam = null;
        PrintWriter out = null;
        URL httpurl = new URL(url);
        HttpURLConnection httpConn = (HttpURLConnection)httpurl.openConnection();
        httpConn.setConnectTimeout(10000);
        httpConn.setReadTimeout(10000);
        handlerProperty(httpConn,property);
        httpConn.setRequestProperty("content-type", "application/json;charset=UTF-8");
        httpConn.setRequestMethod("PUT");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        out = new PrintWriter(new OutputStreamWriter(httpConn.getOutputStream(), "utf-8"));
        out.print(content);
        out.flush();
        out.close();
        inSteam = new InputStreamReader(httpConn.getInputStream(), "utf-8");
        int nByte ;
        StringBuffer strBuffer = new StringBuffer();
        if (inSteam != null) {
            while(true) {
                if ((nByte = inSteam.read()) == -1) {
                    strResult = strBuffer.toString();
                    inSteam.close();
                    break;
                }
                strBuffer.append((char)nByte);
            }
        }

        return strResult;
    }

     void handlerProperty(HttpURLConnection httpConn,Map<String,Object> property){
        if(property==null||property.isEmpty()){
            return;
        }
        Set<Map.Entry<String, Object>> entries = property.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            httpConn.setRequestProperty(entry.getKey(),""+entry.getValue());
        }
    }
}
