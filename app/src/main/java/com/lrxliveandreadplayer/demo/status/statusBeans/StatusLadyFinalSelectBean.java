package com.lrxliveandreadplayer.demo.status.statusBeans;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;

/**
 * Created by Administrator on 2018/10/12.
 */

public class StatusLadyFinalSelectBean extends StatusLadyFirstSelectBean {
    @Override
    public String getTypesWithString() {
        return "Lady_Final_Select_Status";
    }

    @Override
    public String getPublicString() {
        return "女生最终选择";
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_SELECT_LADY_FINAL;
    }
}
