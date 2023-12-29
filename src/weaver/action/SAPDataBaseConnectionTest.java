package weaver.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import weaver.general.BaseBean;

import java.sql.*;
import java.util.ArrayList;

public class SAPDataBaseConnectionTest {
    private static BaseBean utils = new BaseBean();
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    public static void main(String[] args){
        SAPDataBaseConnectionTest test = new SAPDataBaseConnectionTest();
        test.getPcListByLh("600001150159", 150);
    }



    public ArrayList<JSONObject> getPcListByLh(String lh, int limitCount) {
        ArrayList<JSONObject> arrayList = new ArrayList<>();

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//10.18.13.115:1521/LEP", "EFLOW_READ", "EFLOW#124");

            String sql = "SELECT matnr,charg,hsdat,werks,lgort,clabs,labst  FROM V_EFLOWMARALIST WHERE matnr='" + lh + "' AND clabs <> 0\n" +
                    "ORDER BY werks ASC,CASE lgort\n" +
                    "    WHEN 'RC01' THEN 1\n" +
                    "    WHEN 'R001' THEN 2\n" +
                    "    WHEN 'PRT1' THEN 3\n" +
                    "    WHEN 'S001' THEN 4\n" +
                    "    WHEN '7001' THEN 5\n" +
                    "    WHEN 'SW01' THEN 6  \n" +
                    "  END,hsdat desc";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            float temp = 0;

            while (rs.next()) {
                JSONObject object = new JSONObject();
                String MaterBatch = rs.getString("charg");
                float INVNUM = rs.getFloat("clabs");

                System.out.println("Material NO："+lh+"Limit Num======"+limitCount);

                if (INVNUM + temp > limitCount) {
                    object.put("lh", lh);
                    object.put("PC1", MaterBatch);
                    object.put("INVNUM", (limitCount - temp));
                } else {
                    object.put("lh", lh);
                    object.put("PC1", MaterBatch);
                    object.put("INVNUM", INVNUM);
                }
                arrayList.add(object);
                temp += INVNUM;
                System.out.println("数据查询成功======="+arrayList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据获取失败" + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("数据库关闭异常");
            }
        }
        System.out.println("DONE");
        return arrayList;
    }
}
