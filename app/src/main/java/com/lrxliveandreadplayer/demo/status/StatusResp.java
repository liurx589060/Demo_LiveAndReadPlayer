package com.lrxliveandreadplayer.demo.status;

/**
 * Created by Administrator on 2018/9/23.
 */

public class StatusResp {
    private boolean isLast = false;
    private int timeDownCount = 0;
    private boolean isEndButtonVisible = false;

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
}
