/*
 *
 * Copyright (c) 2001-2018 泛微软件.
 * 泛微协同商务系统,版权所有.
 *
 */
package com.weaver.util.dev.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpManager http操作管理工具
 *
 */
public class HttpManager {

    private static final Log logger = LogFactory.getLog(HttpManager.class);

    public CookieStore cookieStore = new BasicCookieStore();

    private CloseableHttpClient getHttpClientSSL() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslcontext = null;
        try {
            sslcontext = createIgnoreVerifySSL();
        } catch (Exception ex) {
            //logger.error(ex.getMessage());
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry;
        if (sslcontext != null)
            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
        else
            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(30000)
                .setConnectTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .setStaleConnectionCheckEnabled(true)
                .build();

        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        return client;
    }

    private CloseableHttpClient getHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslcontext = createIgnoreVerifySSL();

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setStaleConnectionCheckEnabled(true)
                .build();

        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        return client;
    }

    /**
     * get请求，参数放在map里
     *
     * @param url   请求地址
     * @param param 参数map
     * @return 响应
     */
    public String getData(String url, Map<String, String> param, Map<String, String> headers) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);

            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }


            HttpGet get = new HttpGet(builder.build());

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    get.setHeader(entry.getKey(), entry.getValue());
                }
            response = httpClient.execute(get);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * get请求，参数放在map里
     *
     * @param url
     * @param param
     * @return
     */
    public String getData(String url, Map<String, String> param) {
        return getData(url, param, null);
    }

    public String getDataSSL(String url, Map<String, String> param) throws KeyManagementException, NoSuchAlgorithmException {
        return getDataSSL(url, param, null);
    }

    /**
     * SSL协议 get请求，参数放在map里
     *
     * @param url   请求地址
     * @param param 参数map
     * @return 响应
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String getDataSSL(String url, Map<String, String> param, Map<String, String> headers) throws KeyManagementException, NoSuchAlgorithmException {
        String result = null;
        CloseableHttpClient httpClient = getHttpClientSSL();

        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);

            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }


            HttpGet get = new HttpGet(builder.build());

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    get.setHeader(entry.getKey(), entry.getValue());
                }

            response = httpClient.execute(get);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (URISyntaxException | ClientProtocolException | HttpHostConnectException e) {
            logger.error("请求地址:" + url + ",请求参数：" + param + ",响应数据：" + result);
            e.printStackTrace();
            throw new KeyManagementException(e);
        } catch (IOException e) {
            logger.error("请求地址:" + url + ",请求参数：" + param + ",响应数据：" + result);
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * SSL协议 get请求，参数放在map里 参数直接拼接在url后面
     *
     * @param url   请求地址
     * @param param 参数map
     * @return 响应
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String getDataSSLUrl(String url, Map<String, String> param, Map<String, String> headers) throws KeyManagementException, NoSuchAlgorithmException {
        String result = null;
        CloseableHttpClient httpClient = getHttpClientSSL();

        CloseableHttpResponse response = null;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            if (param != null)
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    stringBuilder.append(entry.getKey());
                    if (entry.getValue() != null)
                        stringBuilder.append("=").append(entry.getValue());
                    stringBuilder.append("&");
                }
            if (stringBuilder.length() > 0)
                url = addUrlParam(url, stringBuilder.substring(0, stringBuilder.length() - 1));


            URIBuilder builder = new URIBuilder(url);

//            for(Map.Entry<String,String> entry : param.entrySet())
//            {
//                builder.addParameter(entry.getKey(),entry.getValue());
//            }
//
//
            HttpGet get = new HttpGet(builder.build());

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    get.setHeader(entry.getKey(), entry.getValue());
                }

            response = httpClient.execute(get);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (URISyntaxException | ClientProtocolException | HttpHostConnectException e) {
            logger.error("请求地址:" + url + ",请求参数：" + param + ",响应数据：" + result);
            e.printStackTrace();
            throw new KeyManagementException(e);
        } catch (IOException e) {
            logger.error("请求地址:" + url + ",请求参数：" + param + ",响应数据：" + result);
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * SSL协议 delete请求，参数放在map里
     *
     * @param url   请求地址
     * @param param 参数map
     * @return 响应
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String patchDataSSL(String url, Map<String, String> param, Map<String, String> headers) throws KeyManagementException, NoSuchAlgorithmException {

        String result = null;
        CloseableHttpClient httpClient = getHttpClientSSL();

        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);

            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }


            HttpPatch patch = new HttpPatch(builder.build());

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    patch.setHeader(entry.getKey(), entry.getValue());
                }

            response = httpClient.execute(patch);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * SSL协议 delete请求，参数放在map里
     *
     * @param url   请求地址
     * @param param 参数map
     * @return 响应
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String putDataSSL(String url, Map<String, String> param, Map<String, String> headers) throws KeyManagementException, NoSuchAlgorithmException {

        String result = null;
        CloseableHttpClient httpClient = getHttpClientSSL();

        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);

            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }


            HttpPut put = new HttpPut(builder.build());

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    put.setHeader(entry.getKey(), entry.getValue());
                }

            response = httpClient.execute(put);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * SSL协议 delete请求，参数放在map里
     *
     * @param url   请求地址
     * @param param 参数map
     * @return 响应
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String deleteDataSSL(String url, Map<String, String> param, Map<String, String> headers) throws KeyManagementException, NoSuchAlgorithmException {

        String result = null;
        CloseableHttpClient httpClient = getHttpClientSSL();

        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);

            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }


            HttpDelete delete = new HttpDelete(builder.build());

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    delete.setHeader(entry.getKey(), entry.getValue());
                }

            response = httpClient.execute(delete);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 发送post请求，参数用map接收
     *
     * @param url   地址
     * @param param 参数
     * @return 返回值
     */
    public String postData(String url, Map<String, String> param, Map<String, String> headers) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        CloseableHttpResponse response = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.setHeader(entry.getKey(), entry.getValue());
                }
            response = httpClient.execute(post);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 发送post请求，参数用map接收
     *
     * @param url   地址
     * @param param 参数
     * @return 返回值
     */
    public String postData(String url, Map<String, String> param) {
        return postData(url, param, null);
    }

    /**
     * SSL协议发送post请求，参数用map接收
     *
     * @param url   地址
     * @param param 参数
     * @return 返回值
     */
    public String postDataSSL(String url, Map<String, Object> param) throws KeyManagementException, NoSuchAlgorithmException {
        return postDataSSL(url, param, null);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
        HttpManager manager = new HttpManager();
        Map<String, Object> param = new HashMap<>();
        /*System.out.println(manager.postDataSSL("https://iqms.wingskysemi.com:8088/portal/r/jd?cmd=com.awspaas.user.apps.dt.getUrl&pcrbNo=PCRB2023-07-04001-STR001&userInfo=JY01171&ip=10.18.127.189"
                , param));*/
        System.out.println(manager.postDataSSL("https://iqms.wingskysemi.com:8088/portal/r/jd?cmd=com.awspaas.user.apps.dt.getUrl&pcrbNo=PCRB2023-07-04001-STR001&userInfo=JY01171&ip=10.18.101.29"
                , param));
    }

    /**
     * SSL协议发送post请求，参数用map接收
     *
     * @param url   地址
     * @param param 参数
     * @return 返回值
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String postDataSSL(String url, Map<String, Object> param, Map<String, String> headers) throws KeyManagementException, NoSuchAlgorithmException {
        String result = null;
        CloseableHttpClient httpClient = getHttpClientSSL();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), ""+entry.getValue()));
        }
        CloseableHttpResponse response = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.setHeader(entry.getKey(), entry.getValue());
                }

            response = httpClient.execute(post);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * post请求，参数为json字符串
     *
     * @param url        请求地址
     * @param jsonString json字符串
     * @return 响应
     */
    public String postJsonData(String url, String jsonString) {
        return postJsonData(url, jsonString, null);
    }

    /**
     * post请求，参数为json字符串
     *
     * @param url        请求地址
     * @param jsonString json字符串
     * @return 响应
     */
    public String postJsonData(String url, String jsonString, Map<String, String> headers) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            post.setEntity(new ByteArrayEntity(jsonString.getBytes("UTF-8")));
            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.setHeader(entry.getKey(), entry.getValue());
                }
            response = httpClient.execute(post);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * post请求，参数为json字符串
     *
     * @param url        请求地址
     * @param jsonString json字符串
     * @return 响应
     */
    public String postJsonDataSSL(String url, String jsonString) throws KeyManagementException, NoSuchAlgorithmException {
        return postJsonDataSSL(url, jsonString, null);
    }

    /**
     * post请求，参数为json字符串
     *
     * @param url        请求地址
     * @param jsonString json字符串
     * @return 响应
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String postJsonDataSSL(String url, String jsonString, Map<String, String> headers) throws KeyManagementException, NoSuchAlgorithmException {
        String result = null;
        CloseableHttpClient httpClient = getHttpClientSSL();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            post.setEntity(new ByteArrayEntity(jsonString.getBytes("UTF-8")));
            post.setHeader("Content-type", "application/json");
            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.setHeader(entry.getKey(), entry.getValue());
                }
            response = httpClient.execute(post);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private String entityToString(HttpEntity entity) throws IOException {
        String result = null;
        if (entity != null) {
            long lenth = entity.getContentLength();
            if (lenth != -1 && lenth < 2048) {
                result = EntityUtils.toString(entity, "UTF-8");
            } else {
                InputStreamReader reader1 = new InputStreamReader(entity.getContent(), "UTF-8");
                CharArrayBuffer buffer = new CharArrayBuffer(2048);
                char[] tmp = new char[1024];
                int l;
                while ((l = reader1.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }
                result = buffer.toString();
            }
        }
        return result;
    }


    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * 上传文件
     *
     * @param url
     * @param filePath
     * @param filekey
     * @param headers
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public String uploadFile(String url, String filePath, String contentType, String filekey, Map<String, String> param,
                             Map<String, String> headers)
            throws KeyManagementException, NoSuchAlgorithmException {

        String result = "";
        try {
            File file = new File(filePath);
            InputStream inStream = new FileInputStream(file);
            result = uploadFile(url, inStream, file.getName(), contentType, filekey, param, headers);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return result;
    }

    /**
     * 上传文件
     *
     * @param url
     * @param inStream
     * @param fileName
     * @param filekey
     * @param headers
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public String uploadFile(String url, InputStream inStream, String fileName, String contentType, String filekey,
                             Map<String, String> param, Map<String, String> headers)
            throws KeyManagementException, NoSuchAlgorithmException {

        CloseableHttpClient httpClient = getHttpClientSSL();
        String result = "";
        try {

            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody(filekey, inStream, ContentType.create(contentType), fileName);
            for (Map.Entry<String, String> item : param.entrySet()) {
                builder.addTextBody(item.getKey(), item.getValue());
            }

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }

            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                result = EntityUtils.toString(responseEntity, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取response header中Content-Disposition中的filename值
     *
     * @param response
     * @return
     */
    private static String getFileName(HttpResponse response) {
        Header contentHeader = response.getFirstHeader("Content-Disposition");
        String filename = null;
        if (contentHeader != null) {
            HeaderElement[] values = contentHeader.getElements();
            if (values.length == 1) {
                NameValuePair param = values[0].getParameterByName("filename");
                if (param != null) {
                    try {
                        filename = new String(param.getValue().toString().getBytes("ISO-8859-1"), "GBK");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return filename;
    }

    private static byte[] getbytes(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[2048];
            int n = 0;
            while ((n = is.read(b)) != -1) {
                bos.write(b, 0, n);

            }
            return bos.toByteArray();
        } catch (Exception e) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 根据url下载文件，保存到filepath中
     *
     * @param url
     * @param param
     * @return
     */
    public boolean downloadFile(String url, OutputStream stream, Map<String, String> param, Map<String, String> headers) {
        try {

            CloseableHttpClient httpClient = getHttpClientSSL();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpget);

            //info.setResult(result);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            byte[] buffer = new byte[10 * 1024];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                stream.write(buffer, 0, ch);
            }

            is.close();
            stream.flush();
            stream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据url下载文件，保存到filepath中
     *
     * @param url
     * @param filepath
     * @return
     */
    public boolean downloadFile(String url, String filepath, Map<String, String> param, Map<String, String> headers) {
        try {
            CloseableHttpClient httpClient = getHttpClientSSL();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpget);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            //String str=response.getHeaders("Content-disposition");

            File file = new File(filepath);
            file.getParentFile().mkdirs();

            FileOutputStream fileout = new FileOutputStream(file);

            byte[] buffer = new byte[10 * 1024];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                fileout.write(buffer, 0, ch);
            }
            is.close();
            fileout.flush();
            fileout.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 链接地址后追加参数
     */
    public static String addUrlParam(String url, String... param) {
        if (param != null) {
            for (String str : param)
                if (StringUtils.isNotBlank(str)) {
                    if (url.indexOf("?") >= 0) {
                        url += "&" + str;
                    } else {
                        url += "?" + str;
                    }
                }
        }
        return url;
    }
}
