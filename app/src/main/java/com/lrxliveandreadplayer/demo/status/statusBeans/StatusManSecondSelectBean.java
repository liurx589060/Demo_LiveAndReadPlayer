package com.lrxliveandreadplayer.demo.status.statusBeans;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;

/**
 * Created by Administrator on 2018/10/12.
 */

public class StatusManSecondSelectBean extends StatusManFirstSelectBean {
    @Override
    public String getTypesWithString() {
        return "Man_Second_Select_Status";
    }

    @Override
    public String getPublicString() {
        return "男生第二次选择";
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_SELECT_MAN_SECOND;
    }
}
