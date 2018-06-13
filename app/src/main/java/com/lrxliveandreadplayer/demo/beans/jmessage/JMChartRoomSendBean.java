package com.lrxliveandreadplayer.demo.beans.jmessage;

/**
 * Created by Administrator on 2018/5/30.
 */

public class JMChartRoomSendBean {
    public static final int CHART_STATUS_MATCHING = 1;          //匹配
    public static final int CHART_STATUS_INTRO_MAN = 2;         //男生自我介绍
    public static final int CHART_STATUS_LADY_SELECT_FIRST = 3; //女生第一次选择
    public static final int CHART_STATUS_INTRO_LADY = 4;        //女生聊天第一轮
    public static final int CHART_STATUS_CHAT_MAN_PERFORMANCE = 5;  //男生才艺表演
    public static final int CHART_STATUS_LADY_SELECT_SECOND = 6; //女生第二次选择
    public static final int CHART_STATUS_LADY_CHAT_SECOND = 7;       //女生聊天第二轮
    public static final int CHART_STATUS_ANGEL_CHAT = 8;     //爱心大使有话说
    public static final int CHART_STATUS_LADY_SELECT_FINAL = 9;     //最终选择
    public static final int CHART_STATUS_CHAT_QUESTION_MAN = 10;     //问答环节,男生
    public static final int CHART_STATUS_CHAT_QUESTION_LADY = 11;     //问答环节,女生
    public static final int CHART_STATUS_MAN_SELECT_FIRST = 12;  //男生第一次选择
    public static final int CHART_STATUS_MAN_SELECT_SECOND = 13;  //男生第二次选择
    public static final int CHART_STATUS_MAN_SELECT_FINAL = 14;  //男生最终选择
    public static final int CHART_STATUS_CHAT_FINAL = 15;  //结束

    public static final int CHART_STATUS_ANGEL_QUEST_DISTURB = 100;             //爱心要求大使插话
    public static final int CHART_STATUS_ANGEL_DISTURBING = 101;             //爱心大使插话
    public static final int CHART_STATUS_CHART_MUTIL_PEOPLE = 102;       //多人连麦
    public static final int CHART_STATUS_CHART_CHANGR_LIVETYPE = 103;       //直播方式更改
    public static final int CHART_STATUS_CHART_EXIT_ROOM = 104;       //离开房间

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
    private boolean isLadySelected;
    private String manSelects = "";
    private boolean isResetQuestionStatus;

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

    public String getManSelects() {
        return manSelects;
    }

    public void setManSelects(String manSelects) {
        this.manSelects = manSelects;
    }

    public boolean isLadySelected() {
        return isLadySelected;
    }

    public void setLadySelected(boolean ladySelected) {
        isLadySelected = ladySelected;
    }

    public boolean isResetQuestionStatus() {
        return isResetQuestionStatus;
    }

    public void setResetQuestionStatus(boolean resetQuestionStatus) {
        isResetQuestionStatus = resetQuestionStatus;
    }
}
