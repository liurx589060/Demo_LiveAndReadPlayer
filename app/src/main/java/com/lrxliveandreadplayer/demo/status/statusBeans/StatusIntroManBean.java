package com.lrxliveandreadplayer.demo.status.statusBeans;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.status.BaseStatus;
import com.lrxliveandreadplayer.demo.status.StatusResp;
import com.lrxliveandreadplayer.demo.utils.Constant;

/**
 * Created by Administrator on 2018/9/27.
 */

public class StatusIntroManBean extends BaseStatus {
    @Override
    public String getTypesWithString() {
        return "Intro_Man_Status";
    }

    @Override
    public String getPublicString() {
        return "男生自我介绍阶段";
    }

    @Override
    public int getLiveTimeCount() {
        return 180;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_INTRO_MAN;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        return receiveBean.getIndexNext()%mData.getLimitMan();
    }

    @Override
    public String getRequestGender() {
        return Constant.GENDER_MAN;
    }

    @Override
    public String getRequestRoleType() {
        return Constant.ROLETYPE_GUEST;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_TIME;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        int allCount = mData.getLimitMan();
        boolean isLast = completeCount>=allCount?true:false;
        return isLast;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean,MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(messageType == MessageType.TYPE_SEND) {
            sendBean.setMsg("请男" + getNextIndex(receiveBean) + "玩家自我介绍");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg(mUserInfo.getUser_name() + "玩家开始");
            sendBean.setProcessStatus(getStatus());
            sendBean.setMessageType(MessageType.TYPE_RESPONSE);
        }
        return sendBean;
    }

    @Override
    public void onHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            resp.setResetLive(true);
            resp.setStopTiming(true);
        }else if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE) {
            resp.setResetLive(false);
            resp.setStopTiming(false);
        }
    }
}