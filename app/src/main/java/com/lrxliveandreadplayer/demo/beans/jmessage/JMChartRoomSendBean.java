package com.lrxliveandreadplayer.demo.beans.jmessage;

/**
 * Created by Administrator on 2018/5/30.
 */

public class JMChartRoomSendBean {
    public static final int CHART_STATUS_MATCHING = 0x001;          //匹配
    public static final int CHART_STATUS_INTRO_MAN = 0x002;         //男生自我介绍
    public static final int CHART_STATUS_LADY_SELECT_FIRST = 0x003; //女生第一次选择
    public static final int CHART_STATUS_INTRO_LADY = 0x004;        //女生聊天第一轮
    public static final int CHART_STATUS_CHAT_MAN_PERFORMANCE = 0x005;  //男生才艺表演
    public static final int CHART_STATUS_LADY_SELECT_SECOND = 0x006; //女生第二次选择
    public static final int CHART_STATUS_LADY_CHAT_SECOND = 0x007;       //女生聊天第二轮
    public static final int CHART_STATUS_ANGEL_CHAT = 0x008;     //爱心大使有话说
    public static final int CHART_STATUS_LADY_SELECT_FINAL = 0x009;     //最终选择
    public static final int CHART_STATUS_CHAT_QUESTION_MAN = 0x010;     //问答环节,男生
    public static final int CHART_STATUS_CHAT_QUESTION_LADY = 0x011;     //问答环节,女生
    public static final int CHART_STATUS_MAN_SELECT_FIRST = 0x012;  //男生第一次选择
    public static final int CHART_STATUS_MAN_SELECT_SECOND = 0x013;  //男生第二次选择
    public static final int CHART_STATUS_MAN_SELECT_FINAL = 0x014;  //男生最终选择

    public static final int CHART_STATUS_ANGEL_DISTURB = 0x020;     //爱心大使插话
    public static final int CHART_STATUS_CHART_MUTIL_PEOPLE = 0x0021;       //多人连麦

    public static final int LIVE_CAMERA = 0x101;
    public static final int LIVE_MIC = 0x102;
    public static final int LIVE_NONE = 0x100;

    private int processStatus;
    private int currentCount;
    private int indexSelf;
    private int indexNext;
    private String userName = "";
    private String gender = "";
    private String msg = "";
    private long roomId;
    private String time = "";
    private int limitCount;
    private boolean isUpdateMembers = false;
    private int liveType = LIVE_NONE;
    private JMSendFlags.MessageType messageType;

    public int getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(int processStatus) {
        this.processStatus = processStatus;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public int getIndexSelf() {
        return indexSelf;
    }

    public void setIndexSelf(int indexSelf) {
        this.indexSelf = indexSelf;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    public boolean isUpdateMembers() {
        return isUpdateMembers;
    }

    public void setUpdateMembers(boolean updateMembers) {
        isUpdateMembers = updateMembers;
    }

    public int getIndexNext() {
        return indexNext;
    }

    public void setIndexNext(int indexNext) {
        this.indexNext = indexNext;
    }

    public int getLiveType() {
        return liveType;
    }

    public void setLiveType(int liveType) {
        this.liveType = liveType;
    }

    public JMSendFlags.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(JMSendFlags.MessageType messageType) {
        this.messageType = messageType;
    }
}
