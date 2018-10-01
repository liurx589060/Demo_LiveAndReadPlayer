package com.lrxliveandreadplayer.demo.status.statusBeans;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.status.BaseStatus;
import com.lrxliveandreadplayer.demo.status.StatusResp;
import com.lrxliveandreadplayer.demo.utils.Constant;

/**
 * Created by Administrator on 2018/10/1.
 */

public class StatusLadyFirstSelectBean extends BaseStatus{
    private int mCompleteCount = 0;

    @Override
    public String getTypesWithString() {
        return "Lady_First_Select_Status";
    }

    @Override
    public String getPublicString() {
        return "女生第一次选择";
    }

    @Override
    public int getLiveTimeCount() {
        return 10;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        return 0;
    }

    @Override
    public String getRequestGender() {
        return Constant.GENDER_LADY;
    }

    @Override
    public String getRequestRoleType() {
        return Constant.ROLETYPE_GUEST;
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.HANDLE_SELECT_LADY_FIRST;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean, MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(messageType == MessageType.TYPE_SEND) {
            sendBean.setMsg("请女生做出第一次选择");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg(mUserInfo.getUser_name() + "已做出选择");
            sendBean.setProcessStatus(getStatus());
            sendBean.setMessageType(MessageType.TYPE_RESPONSE);
        }
        return sendBean;
    }

    @Override
    public void onHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
        if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE) {
            mCompleteCount ++;
            int allCount = mData.getLimitLady();
            boolean isLast = mCompleteCount>=allCount?true:false;
            resp.setLast(isLast);
            if(isLast) {
                mCompleteCount = 0;
            }
        }

        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            resp.setResetLive(true);
            resp.setStopTiming(true);
        }else if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE) {
            resp.setResetLive(false);
            resp.setStopTiming(false);
        }
    }

    @Override
    protected boolean checkIsSelf(JMChartRoomSendBean bean) {
        if(getRequestGender().equals(mUserInfo.getGender())
                &&getRequestRoleType().equals(mUserInfo.getRole_type())
                ) {
            return true;
        }
        return false;
    }
}