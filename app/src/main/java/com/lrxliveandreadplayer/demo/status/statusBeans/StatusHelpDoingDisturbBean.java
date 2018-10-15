package com.lrxliveandreadplayer.demo.status.statusBeans;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.status.BaseStatus;
import com.lrxliveandreadplayer.demo.status.StatusResp;
import com.lrxliveandreadplayer.demo.utils.Constant;

/**
 * Created by Administrator on 2018/10/13.
 */

public class StatusHelpDoingDisturbBean extends BaseStatus {
    @Override
    public String getTypesWithString() {
        return "Angel_Doing_disturb";
    }

    @Override
    public String getPublicString() {
        return "爱心大使插话";
    }

    @Override
    public int getLiveTimeCount() {
        return 120;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        return 0;
    }

    @Override
    public String getRequestGender() {
        return Constant.GENDER_ALL;
    }

    @Override
    public String getRequestRoleType() {
        return Constant.ROLRTYPE_ANGEL;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_HELP_DOING_DISTURB;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        return true;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean, MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(messageType == MessageType.TYPE_SEND) {
            int nextIndex = getNextIndex(receiveBean);
            sendBean.setMsg("请爱心大使" + nextIndex + "玩家插话");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg(mUserInfo.getUser_name() + "爱心大使开始插话");
        }
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(messageType);
        return sendBean;
    }

    @Override
    public void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            resp.setResetLive(true);
            resp.setStopTiming(true);
        }else if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE) {
            resp.setResetLive(false);
            resp.setStopTiming(false);
            mCurrentIndex = -1;
        }
    }
}
