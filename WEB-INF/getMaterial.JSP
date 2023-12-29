<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="weaver.general.Util" %>
<%@ page import="weaver.conn.RecordSet" %>
<%@ page import="com.alibaba.fastjson.JSON" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="weaver.general.BaseBean" %>
<%@ page import="com.alibaba.fastjson.JSONArray" %>
<%@ page import="com.alibaba.fastjson.JSONObject" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="weaver.integration.logging.Logger" %>
<%@ page import="weaver.integration.logging.LoggerFactory" %>

<%!
    private static BaseBean utils = new BaseBean();
    public Logger logger = LoggerFactory.getLogger(this.getClass());


    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
%>
<%
    String reqListStr = Util.null2String(request.getParameter("reqListStr"));
    logger.info("getPcListByLh================================>start{params} " + reqListStr);
    HashMap info = new HashMap();

    try {

        ArrayList arrayList = new ArrayList();
        JSONArray reqList = JSONArray.parseArray(reqListStr);
        logger.info("reqList::"+reqList);

        if(reqList!=null&&reqList.size()>0){
            int size = reqList.size();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = reqList.getJSONObject(i);
                String lh = jsonObject.getString("lh");
                int limitCount = jsonObject.getIntValue("count");
                logger.info("请求参数{LH,COUNT}    " + lh + "=====" + limitCount);

                // 1. 加载数据库驱动
                Class.forName("oracle.jdbc.driver.OracleDriver");

                // 2. 建立数据库连接
                conn = DriverManager.getConnection("jdbc:oracle:thin:@//10.18.13.104:1521/LED","EFLOW_READ", "EFLOW#124");

                // 3. 创建SQL查询语句
                String sql = "SELECT matnr,charg,hsdat,werks,lgort,clabs,labst  FROM V_EFLOWMARALIST WHERE mater='"+lh+"' AND clabs <> 0\n" +
                        "ORDER BY werks ASC,CASE lgort\n" +
                        "    WHEN 'RC01' THEN 1\n" +
                        "    WHEN 'R001' THEN 2\n" +
                        "    WHEN 'PRT1' THEN 3\n" +
                        "    WHEN 'S001' THEN 4\n" +
                        "    WHEN '7001' THEN 5\n" +
                        "    WHEN 'SW01' THEN 6\n" +
                        "  END,hsdat desc";

                // 4. 执行查询
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    JSONObject object = new JSONObject();
                    String MaterBatch= rs.getString("charg");
                    String Warehouse=rs.getString("lgort");
                    float INVNUM=rs.getFloat("clabs");
                    float temp=0;
                    logger.info("INVNUM::" +INVNUM);

                    if(INVNUM+temp>limitCount){ //10   5   6

                        object.put("lh", lh);
                        object.put("PC1", MaterBatch);
                        object.put("INVNUM", (limitCount-temp));
                        object.put("Warehouse",Warehouse);

                        logger.info("INVNUM+temp>limitCount and INVNUM===::" +INVNUM);
                    }else{

                        object.put("lh", lh);
                        object.put("PC1", MaterBatch);
                        object.put("INVNUM", INVNUM);
                        object.put("Warehouse",Warehouse);
                        logger.info("INVNUM+temp<limitCount and INVNUM===::" +INVNUM);
                    }
                    arrayList.add(object);
                    temp += INVNUM;

                }
            }

        }else {
            info.put("status", false);
            info.put("error", "数据查询失败");
            logger.info("CrmInfo::INFO::" + info);
            //设置响应编码格式，防止乱码
            response.setContentType("application/json;charset=utf-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            //将数据以json格式响应给ajax
            response.getWriter().write(JSON.toJSONString(info));
            return ;
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // 6. 关闭数据库连接
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
%>

