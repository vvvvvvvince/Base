package com.weaver.util.dev.http;

/**
 * @Title: WebServiceUtil
 * @author: fsl
 * @version: 1.0
 * create date: 2022/6/23
 **/
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class WebServiceUtil {

    public static String execute(String url, String xml) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "text/xml; charset=UTF-8");
        HttpEntity body = new StringEntity(xml);
        post.setEntity(body);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse resp = httpClient.execute(post);
        return EntityUtils.toString(resp.getEntity(), "utf-8");
    }

    public static Element getRootElement(String xml) throws DocumentException {
        Document document = DocumentHelper.parseText(xml);
        return document.getRootElement();
    }

    public static List<Element> getElements(Element root, String name) {
        List<Element> elements = new LinkedList<>();
        getElements(root, name, elements);
        return elements;
    }

    private static void getElements(Element el, String name, List<Element> elements){
        if (name.equals(el.getName())) {
            elements.add(el);
        } else {
            for (Object element : el.elements()) {
                getElements((Element) element, name, elements);
            }
        }
    }


    public static void main(String[] args) throws IOException, DocumentException {
        String url = "http://www.webxml.com.cn/webservices/ChinaTVprogramWebService.asmx";
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://WebXml.com.cn/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <web:getTVstationDataSet>\n" +
                "         <web:theAreaID>18</web:theAreaID>\n" +
                "      </web:getTVstationDataSet>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        String resp = WebServiceUtil.execute(url, xml);
        Element rootElement = WebServiceUtil.getRootElement(resp);
        List<Element> elements = WebServiceUtil.getElements(rootElement, "TvStation");
        for (Element element : elements) {
            Element e1 = element.element("tvStationID");
            System.out.println(e1.getTextTrim());
            Element e2 = element.element("tvStationName");
            System.out.println(e2.getTextTrim());
        }
    }
}
