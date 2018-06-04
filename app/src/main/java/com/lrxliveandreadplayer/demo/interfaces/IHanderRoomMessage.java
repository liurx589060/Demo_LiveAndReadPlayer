package com.lrxliveandreadplayer.demo.interfaces;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMSendFlags;

/**
 * Created by Administrator on 2018/5/31.
 */

public interface IHanderRoomMessage {
    void onMessageHandler(JMChartRoomSendBean chartRoomSendBean, JMSendFlags flags);
}
