package com.lrxliveandreadplayer.demo.manager;

import com.lrxliveandreadplayer.demo.beans.jmessage.Data;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMSendFlags;
import com.lrxliveandreadplayer.demo.beans.jmessage.Member;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;
import com.lrxliveandreadplayer.demo.interfaces.IHanderRoomMessage;
import com.lrxliveandreadplayer.demo.utils.Constant;
import com.lrxliveandreadplayer.demo.utils.Tools;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/31.
 */

public class JMChartRoomController extends AbsRoomController{
    private IHanderRoomMessage listener;
    private int mCurrentStatus;
    private int mCompleteCount;
    private int mCurrentIndex = -1;

    public JMChartRoomController(IHanderRoomMessage iHanderRoomMessage) {
        this.listener = iHanderRoomMessage;
    }

    public void handleRoomMessage(JMChartRoomSendBean chartRoomSendBean) {
        if(listener == null) return;
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
        Member selfMember = DataManager.getInstance().getSelfMember();
        JMSendFlags flags = new JMSendFlags();
        boolean isLast = false;
        if(chartRoomSendBean.getMessageType() == JMSendFlags.MessageType.TYPE_RESPONSE) {
            flags.setMessageType(chartRoomSendBean.getMessageType());
            listener.onMessageHandler(chartRoomSendBean,flags);
            return;
        }

        switch (chartRoomSendBean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_MATCHING:  //匹配阶段
                mCurrentStatus = JMChartRoomSendBean.CHART_STATUS_MATCHING;

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                flags.setLast(checkIsLast(chartRoomSendBean));
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;

            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
                if(chartRoomSendBean.getIndexNext() != mCurrentIndex) {
                    mCompleteCount++;
                    mCurrentIndex = chartRoomSendBean.getIndexNext();
                }
                mCurrentStatus = JMChartRoomSendBean.CHART_STATUS_INTRO_MAN;

                int nextIndex = (chartRoomSendBean.getIndexNext())%data.getLimitMan();
                chartRoomSendBean.setIndexNext(nextIndex);
                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                flags.setLast(checkIsLast(chartRoomSendBean));
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                flags.setGender(Constant.GENDER_MAN);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
        }
    }

    private boolean checkIsLast(JMChartRoomSendBean bean) {
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
        Member selfMember = DataManager.getInstance().getSelfMember();
        int allCount;
        boolean isLast = false;
        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_MATCHING:
                allCount = data.getLimitAngel() + data.getLimitMan() + data.getLimitLady();
                isLast = data.getMembers().size()>=allCount?true:false;
                break;

            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
                allCount = data.getLimitMan();
                isLast = mCompleteCount>=allCount?true:false;
                break;
        }
        if(isLast) {
            mCompleteCount = 0;
        }
        return isLast;
    }

    @Override
    public ArrayList<String> chatOrderRule() {
        return null;
    }

    /**
     * 创建基础数据
     * @return
     */
    @Override
    public JMChartRoomSendBean createBaseSendbeanForExtent() {
        JMChartRoomSendBean bean = new JMChartRoomSendBean();
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean selfInfo = DataManager.getInstance().getUserInfo();
        bean.setGender(selfInfo.getGender());
        bean.setCurrentCount(data.getMembers().size());
        bean.setLimitCount(data.getLimitAngel() + data.getLimitMan() + data.getLimitLady());
        bean.setIndexSelf(DataManager.getInstance().getSelfMember().getIndex());
        bean.setRoomId(data.getRoomId());
        bean.setTime(Tools.getCurrentDateTime());
        bean.setUserName(selfInfo.getUser_name());
        return bean;
    }

    public int getmCurrentStatus() {
        return mCurrentStatus;
    }
}
