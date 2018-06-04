package com.lrxliveandreadplayer.demo.beans.jmessage;

/**
 * Created by Administrator on 2018/5/30.
 */

public class JMChartRoomSendBean {
    public static final int CHART_STATUS_MATCHING = 0x001;
    public static final int CHART_STATUS_INTRO_MAN = 0x003;
    public static final int CHART_STATUS_CHAT_FIRST = 0x004;
    public static final int CHART_STATUS_CHAT_PERFORMANCE = 0x005;
    public static final int CHART_STATUS_CHAT_SECOND = 0x006;
    public static final int CHART_STATUS_CHAT_QUESTION = 0x007;
    public static final int CHART_STATUS_ANGEL_DISTURB = 0x008;
    public static final int CHART_STATUS_CHART_DOUBLE = 0x009;       //连麦

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
