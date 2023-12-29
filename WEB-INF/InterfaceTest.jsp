<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="com.util.IdentityVerifyUtil" %>
<%@ page import="com.util.HttpManager" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.weaver.util.dev.log.LogFactory" %>
<%@ page import="com.weaver.util.dev.log.DevLog" %>
<%!

    LogFactory log = new DevLog();

%>
<%
    log.info("InterfaceTest::start");
    IdentityVerifyUtil identityVerifyUtil = IdentityVerifyUtil.getInstance();
    String spk = identityVerifyUtil.getSPK();
    String secret = identityVerifyUtil.getSECRET();
    String token = identityVerifyUtil.getToken();
    if (spk == null || secret == null || token == null) {
        return;
    }
    String url = IdentityVerifyUtil.HOST + "/api/openapi/wingtech/hrm/resetPassword";
    url+="?workcode=JY01171&newPassword=DTJXfsl1314..";
    HttpManager http = new HttpManager();
    Map<String, String> heads =  IdentityVerifyUtil.getHttpHeads(token,"1283",spk);
    HashMap<String,String> params = new HashMap<>();
    try {
        String json = "";
        String res = http.postJsonDataSSL(url, json, heads);
        log.info("res::"+res);
    } catch (Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
    }

%>
