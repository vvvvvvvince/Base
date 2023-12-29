<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>

<!DOCTYPE html>
<html>
<head>
    <title>Query Oracle Database</title>
</head>
<body>

<%
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
        // 1. 加载数据库驱动
        Class.forName("oracle.jdbc.driver.OracleDriver");

        // 2. 建立数据库连接
        conn = DriverManager.getConnection("jdbc:oracle:thin:@//10.18.13.104:1521/LED","EFLOW_READ", "EFLOW#124");

        // 3. 创建SQL查询语句
        String sql = "SELECT * FROM SAPNEX.mbew WHERE MATNR ='600001401961'";

        // 4. 执行查询
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);

        // 5. 处理查询结果
        out.println("<table border='1'>");
        out.println("<tr><th>ID</th><th>Name</th></tr>");
        while (rs.next()) {
            out.println("数据库连接成功");
            out.println("<tr>");
            out.println("<td>" + rs.getInt("MANDT") + "</td>");
            out.println("<td>" + rs.getString("MATNR") + "</td>");
            out.println("</tr>");
        }
        out.println("</table>");

    } catch (Exception e) {
        e.printStackTrace();
        out.println("数据库连接异常"+e.getMessage());
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

</body>
</html>
