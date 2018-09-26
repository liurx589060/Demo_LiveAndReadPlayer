package com.lrxliveandreadplayer.demo.status;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;

/**
 * Created by Administrator on 2018/9/23.
 */

public interface IStatusListener {
    void onHandleResp(BaseStatus statusInstance,StatusResp statusResp, JMChartRoomSendBean sendBean);
}
