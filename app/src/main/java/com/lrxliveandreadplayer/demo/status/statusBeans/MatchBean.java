package com.lrxliveandreadplayer.demo.status.statusBeans;

import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.status.BaseStatus;
import com.lrxliveandreadplayer.demo.status.StatusResp;
import com.lrxliveandreadplayer.demo.utils.Constant;

import static com.lrxliveandreadplayer.demo.status.BaseStatus.HandleType.HANDLE_MATCH;

/**
 * 匹配状态
 * Created by Administrator on 2018/9/26.
 */

public class MatchBean extends BaseStatus {
    @Override
    public String getTypesWithString() {
        return "Match_Status";
    }

    @Override
    public String getPublicString() {
        return "匹配阶段";
    }

    @Override
    public int getLiveTimeCount() {
        return 0;
    }

    @Override
    public int getStatus() {
        return JMChartRoomSendBean.CHART_STATUS_MATCHING;
    }

    @Override
    public String getRequestGender() {
        return Constant.GENDER_ALL;
    }

    @Override
    public String getRequestRoleType() {
        return Constant.ROLETYPE_ALL;
    }

    @Override
    public HandleType getHandleType() {
        return HANDLE_MATCH;
    }

    @Override
    public boolean isLast(int completeCount,JMChartRoomSendBean receiveBean) {
        int allCount = mData.getLimitAngel() + mData.getLimitMan() + mData.getLimitLady();
        boolean isLast = receiveBean.getCurrentCount()>=allCount?true:false;
        return isLast;
    }

    @Override
    public JMChartRoomSendBean getChartSendBeanWillSend() {
        JMChartRoomSendBean sendBean = createBaseChartRoomSendBean();
        if(mMessageType == MessageType.TYPE_SEND) {
            sendBean.setMsg(mUserInfo.getUser_name() + "加入房间");
        }
        return sendBean;
    }

    @Override
    public void onHandler(StatusResp resp, JMChartRoomSendBean receiveBean) {

    }
}
