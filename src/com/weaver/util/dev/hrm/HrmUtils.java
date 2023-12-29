package com.weaver.util.dev.hrm;

import com.google.common.collect.Lists;
import com.weaver.util.dev.dao.DaoUtils;
import weaver.conn.RecordSet;
import weaver.crm.Maint.CustomerInfoComInfo;
import weaver.general.Util;
import weaver.hrm.HrmUserVarify;
import weaver.hrm.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 人员组织相关工具类  利用好  DepartmentComInfo  ResourceComInfo  SubCompanyComInfo等缓存类
 *
 */
public class HrmUtils {


    /**
     * 获取当前会话的用户
     */
    public static User getCurUser(HttpServletRequest request, HttpServletResponse response) {
        return HrmUserVarify.getUser(request, response);
    }

    /**
     * 根据当前userid 构造用户
     * @param userid
     * @return
     */
    public static User getUser(int userid) {
        return User.getUser(userid, 0);
    }

    /**
     * 根据当前userid 构造用户
     * @param userid
     * @return
     */
    public static User getUser(String userid) {
        return User.getUser(Integer.parseInt(userid), 0);
    }


    /**
     * 获取当前部门的一级部门
     */
    public static String topDeptId(String id, RecordSet rs) {
        Map<String, String> map = DaoUtils.executeQueryToMap("select id, supdepid, departmentname from HrmDepartment where id = ?",rs, id);
        if (Objects.isNull(map)) {
            return null;
        }
        // 递归查询所在一级部门
        while (!"0".equals(map.get("supdepid"))) {
            map = DaoUtils.executeQueryToMap("select id, supdepid, departmentname from HrmDepartment where id = ?",rs, Util.null2String(map.get("supdepid")));
            if (Objects.isNull(map)) {
                return null;
            }
        }
        return Util.null2String(map.get("id"));
    }

    /**
     * 获取当前部门的层级链路
     * @param deptId 当前层级
     * @param containsCancel 是否包含封存
     * @return
     */
    public static List<Map<String, String>> getTopDeptChain(String deptId, boolean containsCancel,RecordSet recordSet) {
        String appendSql = "";
//        if (!containsCancel) { // 不包含
//            //
//            appendSql = " and = 0 ";
//        }
        List<Map<String, String>> res = Lists.newArrayList();
        Map<String, String> map = DaoUtils.executeQueryToMap("select * from HrmDepartment where id = ?" + appendSql,recordSet, deptId);
        if (Objects.isNull(map)) {
            return Lists.newArrayList();
        }
        res.add(map);
        // 递归查询所在一级部门
        while (!"0".equals(map.get("supdepid"))) {
            map = DaoUtils.executeQueryToMap("select * from HrmDepartment where id = ?" + appendSql, recordSet,Util.null2String(map.get("supdepid")));
            if (Objects.isNull(map)) {
                return res;
            } else {
                res.add(map);
            }
        }
        return res;
    }

    /**
     * 获取当前部门的层级链路 仅id
     * @param deptId 当前层级
     * @param containsCancel 是否包含封存
     * @return
     */
    public static List<String> getTopDeptIdChain(String deptId, boolean containsCancel,RecordSet rs) {
        List<Map<String, String>> res = getTopDeptChain(deptId, containsCancel,rs);
        return res.stream().map(x -> Util.null2String(x.get("id"))).collect(Collectors.toList());
    }

    public static List<String> getTopDeptIdChain(String deptId,RecordSet recordSet) {
        return getTopDeptIdChain(deptId,false,recordSet);
    }

    public static List<Map<String, String>> getTopDeptChain(String deptId,RecordSet recordSet) {
        return getTopDeptChain(deptId,false,recordSet);
    }

    public static Map <String, Object> getCustomerUserInfo(String userid) {
        Map <String, Object> userInfo = new HashMap<String, Object>();
        try {
            CustomerInfoComInfo comInfo = new CustomerInfoComInfo();
            userInfo.put("id", userid);
            userInfo.put("name", comInfo.getCustomerInfoname(userid));
            userInfo.put("usertype","1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }

}
