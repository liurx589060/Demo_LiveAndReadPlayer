package com.lrxliveandreadplayer.demo.status.statusBeans;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.status.BaseStatus;
import com.lrxliveandreadplayer.demo.status.StatusResp;
import com.lrxliveandreadplayer.demo.utils.Constant;

/**
 * Created by Administrator on 2018/10/13.
 */

public class StatusHelpQuestDisturbBean extends BaseStatus {
    private final int COUNT = 3;
    private int mDisturbCount = 0;

    @Override
    public String getTypesWithString() {
        return "Angel_quest_disturb";
    }

    @Override
    public String getPublicString() {
        return "爱心大使请求插话";
    }

    @Override
    public int getLiveTimeCount() {
        return 0;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_ANGEL_QUEST_DISTURB;
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
        return HandleType.HANDLE_HELP_QUEST_DISTURB;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        return false;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean, MessageType messageType) {
        JMChartRoomSendBean bean = createBaseChartRoomSendBean();
        bean.setMsg("爱心大使" + mUserInfo.getUser_name() + "要求插话");
        bean.setProcessStatus(getStatus());
        bean.setMessageType(messageType);
        return bean;
    }

    @Override
    public void onPostHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {
        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            mDisturbCount++;
            mCurrentIndex = -1;
        }

        resp.setResetLive(false);
        resp.setStopTiming(false);
    }

    @Override
    public boolean checkSelfIndex(JMChartRoomSendBean receiveBean) {
        return true;
    }

    @Override
    protected void onPreHandle(JMChartRoomSendBean receiveBean) {
        if(receiveBean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_INTRO_LADY
                || receiveBean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND) {
            mDisturbCount = 0;
        }
    }

    @Override
    protected boolean checkIsSelf(JMChartRoomSendBean bean) {
        return true;
    }

    public boolean isCanDisturb() {
        return mDisturbCount < COUNT;
    }
}
