package com.lrxliveandreadplayer.demo.status.statusBeans;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.manager.DataManager;
import com.lrxliveandreadplayer.demo.status.BaseStatus;
import com.lrxliveandreadplayer.demo.status.StatusResp;
import com.lrxliveandreadplayer.demo.utils.Constant;

/**
 * Created by Administrator on 2018/9/27.
 */

public class StatusLadyChartFirstBean extends BaseStatus {
    @Override
    public String getTypesWithString() {
        return "Lady_Chart_First_Status";
    }

    @Override
    public String getPublicString() {
        return "女生自我介绍阶段";
    }

    @Override
    public int getLiveTimeCount() {
        return 120;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_INTRO_LADY;
    }

    @Override
    public int getNextIndex(JMChartRoomSendBean receiveBean) {
        int index = (receiveBean.getIndexNext() + 1)%mData.getLimitLady();
        return index;
    }

    @Override
    public boolean checkSelfIndex(JMChartRoomSendBean receiveBean) {
        if(mSelfMember.getIndex() == receiveBean.getIndexNext()) {
            return true;
        }
        return false;
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
        return HandleType.HANDLE_TIME;
    }

    @Override
    public boolean isLast(int completeCount, JMChartRoomSendBean receiveBean) {
        int allCount = mData.getLimitLady();
        boolean isLast = completeCount>=allCount?true:false;
        return isLast;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean,MessageType messageType) {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(messageType == MessageType.TYPE_SEND) {
            int nextIndex;
            if(receiveBean.getProcessStatus() != getStatus()) {
                nextIndex = getStartIndex();
            }else {
                nextIndex = getNextIndex(receiveBean);
            }
            sendBean.setMsg("请女" + nextIndex + "玩家自我介绍");
        }else if (messageType == MessageType.TYPE_RESPONSE) {
            sendBean.setMsg(mUserInfo.getUser_name() + "玩家开始");
        }
        sendBean.setProcessStatus(getStatus());
        sendBean.setMessageType(MessageType.TYPE_RESPONSE);
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
