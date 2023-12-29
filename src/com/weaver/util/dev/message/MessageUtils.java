package com.weaver.util.dev.message;

import com.cloudstore.dev.api.bean.MessageBean;
import com.cloudstore.dev.api.bean.MessageType;
import com.cloudstore.dev.api.util.Util_Message;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 消息中心相关工具类
 */
public class MessageUtils {


    /**
     * 发送消息
     * @param msgType
     * @param title
     * @param content
     * @param users
     * @param creator
     * @param linkUrl
     * @param linkMobileUrl
     * @throws IOException
     */
    public static void sendMsg(int msgType, String title, String content, Set<String> users, int creator, String linkUrl, String linkMobileUrl) throws IOException {
        MessageType messageType = MessageType.newInstance(msgType);
        MessageBean messageBean = Util_Message.createMessage(messageType, users, title, content, linkUrl, linkMobileUrl);
        messageBean.setCreater(creator);
        Util_Message.store(messageBean);
    }


    /**
     * 发送消息
     * @param msgType
     * @param title
     * @param content
     * @param users
     * @param creator
     * @throws IOException
     */
    public static void sendMsg(int msgType, String title, String content, Set<String> users, int creator) throws IOException {
        sendMsg(msgType, title, content, users, creator, "", "");
    }

    /**
     * 发送消息
     * @param msgType
     * @param title
     * @param content
     * @param users
     * @throws IOException
     */
    public static void sendMsg(int msgType, String title, String content, Set<String> users) throws IOException {
        sendMsg(msgType, title, content, users, 1);
    }

    /**
     * 发送消息
     * @param msgType
     * @param title
     * @param content
     * @param userids
     * @throws IOException
     */
    public static void sendMsg(int msgType, String title, String content, String userids) throws IOException {
        List<String> userList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(userids);
        sendMsg(msgType, title, content,  Sets.newHashSet(userList));
    }





}
