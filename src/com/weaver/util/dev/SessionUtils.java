package com.weaver.util.dev;

import weaver.hrm.HrmUserVarify;
import weaver.hrm.User;
import weaver.integration.util.SessionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 会话相关工具类
 */
public class SessionUtils {


    /**
     * 获取当前绘画的用户信息
     * @param request
     * @param response
     * @return
     */
    public static User getSessionUser(HttpServletRequest request, HttpServletResponse response) {
        return HrmUserVarify.getUser(request, response);
    }

    /**
     * 构建指定用户的会话信息，用于自定义登录
     * @param user
     */
    public static void createSession(User user, HttpServletRequest request,
                                     HttpServletResponse response) {
        SessionUtil.createSession(Integer.toString(user.getUID()), request, response);
    }

    /**
     * 构建指定用户的会话信息，用于自定义登录
     * @param userid
     */
    public static void createSession(int userid, HttpServletRequest request,
                                     HttpServletResponse response) {
        SessionUtil.createSession(Integer.toString(userid), request, response);
    }

    /**
     * 构建指定用户的会话信息，用于自定义登录
     * @param userid
     */
    public static void createSession(String userid, HttpServletRequest request,
                                     HttpServletResponse response) {
        SessionUtil.createSession(userid, request, response);
    }
}
