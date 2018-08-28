package com.lrxliveandreadplayer.demo.beans;

/**
 * Created by Administrator on 2018/3/10.
 */

public class JMNormalSendBean {
    public static final int NORMAL_EXIT = 0x040;//创建者解散聊天室

    private int code;
    private String msg = "";
    private String targetUserName = "";
    private String time = "";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
