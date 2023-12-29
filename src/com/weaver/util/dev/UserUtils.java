package com.weaver.util.dev;

import weaver.hrm.HrmUserVarify;
import weaver.hrm.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户相关工具类
 */
public class UserUtils {

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

}
