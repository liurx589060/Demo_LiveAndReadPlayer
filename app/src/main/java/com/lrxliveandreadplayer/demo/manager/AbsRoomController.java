package com.lrxliveandreadplayer.demo.manager;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/6/1.
 */

public abstract class AbsRoomController {
    public abstract void handleRoomMessage(JMChartRoomSendBean chartRoomSendBean);
    public abstract ArrayList<String> chatOrderRule();
    public abstract JMChartRoomSendBean createBaseSendbeanForExtent();
}
