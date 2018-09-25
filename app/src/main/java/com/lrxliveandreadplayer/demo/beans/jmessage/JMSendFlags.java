package com.lrxliveandreadplayer.demo.beans.jmessage;

import com.lrxliveandreadplayer.demo.status.BaseStatus;

/**
 * Created by Administrator on 2018/6/1.
 */

public class JMSendFlags {
    public BaseStatus.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(BaseStatus.MessageType messageType) {
        this.messageType = messageType;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public enum MessageType {
        TYPE_SEND,
        TYPE_RESPONSE
    }

    private BaseStatus.MessageType messageType;
    private String roleType = "";
    private String gender = "";
    private boolean isLast;
    private String userName = "";
}
