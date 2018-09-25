package com.lrxliveandreadplayer.demo.status;

import com.lrxliveandreadplayer.demo.beans.BaseResp;

/**
 * Created by Administrator on 2018/9/23.
 */

public class StatusResp {
    private boolean isLast = false;
    private int timeDownCount = 0;
    private boolean isEndButtonVisible = false;
    private BaseStatus.MessageType messageType = BaseStatus.MessageType.TYPE_SEND;
    private boolean isSelf = false;

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public int getTimeDownCount() {
        return timeDownCount;
    }

    public void setTimeDownCount(int timeDownCount) {
        this.timeDownCount = timeDownCount;
    }

    public boolean isEndButtonVisible() {
        return isEndButtonVisible;
    }

    public void setEndButtonVisible(boolean endButtonVisible) {
        isEndButtonVisible = endButtonVisible;
    }

    public BaseStatus.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(BaseStatus.MessageType messageType) {
        this.messageType = messageType;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }
}
