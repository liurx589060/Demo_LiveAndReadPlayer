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
    private ArrayList<Integer> mRecievedIndexList = new ArrayList<>();

    public JMChartRoomController(IHanderRoomMessage iHanderRoomMessage) {
        this.listener = iHanderRoomMessage;
    }

    /**
     * 处理回复形式的消息
     * @param chartRoomSendBean
     */
    private void handleResponse(JMChartRoomSendBean chartRoomSendBean) {
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
        Member selfMember = DataManager.getInstance().getSelfMember();
        JMSendFlags flags = new JMSendFlags();
        boolean isLast = false;

        switch (chartRoomSendBean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_MATCHING:
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE:
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT:
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING:
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://男生第一次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://男生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                //重复消息，不处理
                if(mRecievedIndexList.contains(chartRoomSendBean.getIndexSelf())) {
                    //假如重复的消息则不处理
                    return;
                }
                mRecievedIndexList.add(chartRoomSendBean.getIndexSelf());
                mCompleteCount++;
                isLast = checkIsLast(chartRoomSendBean);
                flags.setLast(isLast);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
        }

    }

    /**
     * 处理发送形式的消息
     * @param chartRoomSendBean
     */
    private void handleSend(JMChartRoomSendBean chartRoomSendBean) {
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
        Member selfMember = DataManager.getInstance().getSelfMember();
        JMSendFlags flags = new JMSendFlags();
        boolean isLast = false;

        switch (chartRoomSendBean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_MATCHING:  //匹配阶段
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                isLast = checkIsLast(chartRoomSendBean);
                flags.setLast(isLast);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN://男生自我介绍环节
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE://男生才艺表演
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节，男生
                //重复消息，不处理
                if(checkIsRepeat(chartRoomSendBean)) return;
                mCompleteCount++;
                mRecievedIndexList.add(chartRoomSendBean.getIndexNext());
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                isLast = checkIsLast(chartRoomSendBean);
                flags.setLast(isLast);
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                flags.setGender(Constant.GENDER_MAN);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                flags.setGender(Constant.GENDER_LADY);
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY://女生自我介绍
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND://女生第二轮谈话
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY://问答环节，女生
                //重复消息，不处理
                if(checkIsRepeat(chartRoomSendBean)) return;
                mCompleteCount++;
                mRecievedIndexList.add(chartRoomSendBean.getIndexNext());
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                isLast = checkIsLast(chartRoomSendBean);
                flags.setLast(isLast);
                flags.setGender(Constant.GENDER_LADY);
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://男生第一次选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://男生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                flags.setGender(Constant.GENDER_MAN);
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                flags.setGender(Constant.GENDER_LADY);
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT://爱心大使说话
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING://爱心大使插话
                //重复消息，不处理
                if(checkIsRepeat(chartRoomSendBean)) return;
                mCompleteCount++;
                mRecievedIndexList.add(chartRoomSendBean.getIndexNext());
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                isLast = checkIsLast(chartRoomSendBean);
                flags.setLast(isLast);
                flags.setRoleType(Constant.ROLRTYPE_ANGEL);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL://流程结束
                //重复消息，不处理
                if(checkIsRepeat(chartRoomSendBean)) return;
                mCompleteCount++;
                mRecievedIndexList.add(chartRoomSendBean.getIndexNext());
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_QUEST_DISTURB://爱心大使要插话
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHART_CHANGR_LIVETYPE:
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
        }
    }

    public void handleRoomMessage(JMChartRoomSendBean chartRoomSendBean) {
        if(listener == null) return;
        if(chartRoomSendBean.getMessageType() == JMSendFlags.MessageType.TYPE_RESPONSE) {
            handleResponse(chartRoomSendBean);
        }else if(chartRoomSendBean.getMessageType() == JMSendFlags.MessageType.TYPE_SEND) {
            handleSend(chartRoomSendBean);
        }
    }

    /**
     * 检测是否重复
     * @return
     */
    private boolean checkIsRepeat(JMChartRoomSendBean chartRoomSendBean) {
        if(mRecievedIndexList.contains(chartRoomSendBean.getIndexNext())) {
            //假如重复的消息则不处理
            return true;
        }
        return false;
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
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST:
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND:
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE:
                allCount = data.getLimitMan();
                isLast = mCompleteCount>=allCount?true:false;
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST:
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY:
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND:
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND:
                allCount = data.getLimitLady();
                isLast = mCompleteCount>=allCount?true:false;
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT:
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING:
                allCount = data.getLimitAngel();
                isLast = mCompleteCount>=allCount?true:false;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY://问答环节，女生
                allCount = 2;//最后剩下两位被选中的女生
                isLast = mCompleteCount>=allCount?true:false;
                break;
        }
        if(isLast) {
            mRecievedIndexList.clear();
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

    public int getCurrentIndex() {
        //返回最后一个
        if(mRecievedIndexList.size() > 0) {
            return mRecievedIndexList.get(mRecievedIndexList.size() - 1);
        }
        return -1;
    }
}
