package com.lrxliveandreadplayer.demo.beans;

/**
 * Created by Administrator on 2018/3/10.
 */

public class JMsgBean {
    private int code;
    private String text;
    private String exinfo;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text==null?"":text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getExinfo() {
        return exinfo==null?"":exinfo;
    }

    public void setExinfo(String exinfo) {
        this.exinfo = exinfo;
    }
}
