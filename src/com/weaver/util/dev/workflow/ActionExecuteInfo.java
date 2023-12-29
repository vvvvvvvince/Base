package com.weaver.util.dev.workflow;

/**
 * @program: ebuCodeGit
 * @ClassName ActionExcuteInfo
 * @description:
 * @author: slfang
 * @create: 2023-08-25 15:49
 * @Version 1.0
 **/
public class ActionExecuteInfo {

    private boolean status;
    private String error;

    public ActionExecuteInfo(boolean status, String error) {
        this.status = status;
        this.error = error;
    }

    public ActionExecuteInfo(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }


    public String getError() {
        return error;
    }

}
