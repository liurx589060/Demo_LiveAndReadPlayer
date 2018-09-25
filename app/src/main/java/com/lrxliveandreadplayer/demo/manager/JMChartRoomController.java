package com.lrxliveandreadplayer.demo.manager;

import android.util.Log;

import com.lrxliveandreadplayer.demo.beans.jmessage.Data;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMSendFlags;
import com.lrxliveandreadplayer.demo.beans.jmessage.Member;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;
import com.lrxliveandreadplayer.demo.interfaces.IHanderRoomMessage;
import com.lrxliveandreadplayer.demo.network.NetWorkMg;
import com.lrxliveandreadplayer.demo.status.BaseStatus;
import com.lrxliveandreadplayer.demo.utils.Constant;
import com.lrxliveandreadplayer.demo.utils.Tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/31.
 */

public class JMChartRoomController extends AbsRoomController{
    private IHanderRoomMessage listener;
    private int mCurrentStatus;
    private Map<Integer,ArrayList<Integer>> mSendRecievedIndexMap = new HashMap<>();
    private Map<Integer,ArrayList<Integer>> mResponseRecievedIndexMap = new HashMap<>();
    private Map<Integer,Boolean> mSendRecievedLastMap = new HashMap<>();
    private Map<Integer,Boolean> mResponseRecievedLastMap = new HashMap<>();
    private Map<Integer,Integer> mSendCompleteCountMap = new HashMap<>();
    private Map<Integer,Integer> mResponseCompleteCountMap = new HashMap<>();

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
        flags.setMessageType(chartRoomSendBean.getMessageType());
        boolean isLast = false;

        switch (chartRoomSendBean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_MATCHING:
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE:
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND:
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT:
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY:
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://男生第一次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://男生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                //重复消息，不处理
                if(checkIsResponseRepeat(chartRoomSendBean)) {
                    //假如重复的消息则不处理
                    return;
                }
                addIndexCount(chartRoomSendBean);
                addCompleteCount(chartRoomSendBean);

                isLast = checkIsLast(chartRoomSendBean);
                flags.setLast(isLast);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
        }

    }

    private void addCompleteCount(JMChartRoomSendBean sendBean) {
        Map<Integer,Integer> map = null;
        if(sendBean.getMessageType() == BaseStatus.MessageType.TYPE_SEND) {
            map = mSendCompleteCountMap;
        }else if(sendBean.getMessageType() == BaseStatus.MessageType.TYPE_RESPONSE){
            map = mResponseCompleteCountMap;
        }
        if(map == null) return;
        int count;
        if(map.get(sendBean.getProcessStatus()) == null) {
            count = 0;
        }else {
            count = map.get(sendBean.getProcessStatus());
        }
        count++;
        map.put(sendBean.getProcessStatus(),count);
    }

    private void addIndexCount(JMChartRoomSendBean sendBean) {
        Map<Integer,ArrayList<Integer>> map = null;
        if(sendBean.getMessageType() == BaseStatus.MessageType.TYPE_SEND) {
            map = mSendRecievedIndexMap;
        }else if(sendBean.getMessageType() == BaseStatus.MessageType.TYPE_RESPONSE){
            map = mResponseRecievedIndexMap;
        }
        if(map==null) return;
        ArrayList<Integer> list;
        if(map.get(sendBean.getProcessStatus()) == null) {
            list = new ArrayList<>();
        }else {
            list = map.get(sendBean.getProcessStatus());
        }
        if(sendBean.getMessageType() == BaseStatus.MessageType.TYPE_RESPONSE) {
            list.add(sendBean.getIndexSelf());
        }else {
            list.add(sendBean.getIndexNext());
        }
        map.put(sendBean.getProcessStatus(),list);
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

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                isLast = checkIsLast(chartRoomSendBean);
                flags.setLast(isLast);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN://男生自我介绍环节
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE://男生才艺表演
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节，男生
                if(chartRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN
                        && chartRoomSendBean.isResetQuestionStatus()) {
                    resetQuestionCheckStatus(chartRoomSendBean);
                }
                //重复消息，不处理
                if(checkIsSendRepeat(chartRoomSendBean)) return;
                addCompleteCount(chartRoomSendBean);
                addIndexCount(chartRoomSendBean);
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                isLast = checkIsLast(chartRoomSendBean);
                flags.setLast(isLast);
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                flags.setGender(Constant.GENDER_MAN);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择
                //重复消息，不处理
                if(checkIsSendRepeat(chartRoomSendBean)) return;
                addCompleteCount(chartRoomSendBean);
                addIndexCount(chartRoomSendBean);
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                flags.setGender(Constant.GENDER_LADY);
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY://女生自我介绍
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND://女生第二轮谈话
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY://问答环节，女生
                if(chartRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY
                        && chartRoomSendBean.isResetQuestionStatus()) {
                    resetQuestionCheckStatus(chartRoomSendBean);
                }
                //重复消息，不处理
                if(checkIsSendRepeat(chartRoomSendBean)) return;
                addCompleteCount(chartRoomSendBean);
                addIndexCount(chartRoomSendBean);
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                isLast = checkIsLast(chartRoomSendBean);
                flags.setLast(isLast);
                flags.setGender(Constant.GENDER_LADY);
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://男生第一次选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://男生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                //重复消息，不处理
                if(checkIsSendRepeat(chartRoomSendBean)) return;
                addCompleteCount(chartRoomSendBean);
                addIndexCount(chartRoomSendBean);
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                flags.setGender(Constant.GENDER_MAN);
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
                //重复消息，不处理
                if(checkIsSendRepeat(chartRoomSendBean)) return;
                addCompleteCount(chartRoomSendBean);
                addIndexCount(chartRoomSendBean);
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                flags.setGender(Constant.GENDER_LADY);
                flags.setRoleType(Constant.ROLETYPE_GUEST);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT://爱心大使说话
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING://爱心大使插话
                //重复消息，不处理
                if(checkIsSendRepeat(chartRoomSendBean)) return;
                addCompleteCount(chartRoomSendBean);
                addIndexCount(chartRoomSendBean);
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                isLast = checkIsLast(chartRoomSendBean);
                flags.setLast(isLast);
                flags.setRoleType(Constant.ROLRTYPE_ANGEL);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL://流程结束
                //重复消息，不处理
                if(checkIsSendRepeat(chartRoomSendBean)) return;
                addCompleteCount(chartRoomSendBean);
                addIndexCount(chartRoomSendBean);
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_QUEST_DISTURB://爱心大使要插话
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHART_CHANGR_LIVETYPE://直播方式更改
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHART_EXIT_ROOM://离开房间
                mCurrentStatus = chartRoomSendBean.getProcessStatus();

                flags.setMessageType(BaseStatus.MessageType.TYPE_SEND);
                listener.onMessageHandler(chartRoomSendBean,flags);
                break;
        }
    }

    public void handleRoomMessage(JMChartRoomSendBean chartRoomSendBean) {
        if(listener == null) return;
        if(chartRoomSendBean.getMessageType() == BaseStatus.MessageType.TYPE_RESPONSE) {
            handleResponse(chartRoomSendBean);
        }else if(chartRoomSendBean.getMessageType() == BaseStatus.MessageType.TYPE_SEND) {
            handleSend(chartRoomSendBean);
        }
    }

    /**
     * 检测是否重复
     * @return
     */
    private boolean checkIsSendRepeat(JMChartRoomSendBean chartRoomSendBean) {
        ArrayList<Integer> list = mSendRecievedIndexMap.get(chartRoomSendBean.getProcessStatus());
        if(list == null) return false;
        if(list.contains(chartRoomSendBean.getIndexNext())
                ||(mSendRecievedLastMap.get(chartRoomSendBean.getProcessStatus())!=null
                &&mSendRecievedLastMap.get(chartRoomSendBean.getProcessStatus()))) {
            //假如重复的消息则不处理
            return true;
        }
        return false;
    }

    private boolean checkIsResponseRepeat(JMChartRoomSendBean chartRoomSendBean) {
        ArrayList<Integer> list = mResponseRecievedIndexMap.get(chartRoomSendBean.getProcessStatus());
        if(list == null) return false;
        if(list.contains(chartRoomSendBean.getIndexSelf())
                ||(mResponseRecievedLastMap.get(chartRoomSendBean.getProcessStatus())!=null
                &&mResponseRecievedLastMap.get(chartRoomSendBean.getProcessStatus()))) {
            //假如重复的消息则不处理
            return true;
        }
        return false;
    }

    private boolean checkIsLast(JMChartRoomSendBean bean) {
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
        Member selfMember = DataManager.getInstance().getSelfMember();
        Map<Integer,Integer> map = null;
        if(bean.getMessageType() == BaseStatus.MessageType.TYPE_SEND) {
            map = mSendCompleteCountMap;
        }else if(bean.getMessageType() == BaseStatus.MessageType.TYPE_RESPONSE){
            map = mResponseCompleteCountMap;
        }
        if(map == null) return false;
        int allCount;
        int mCompleteCount;
        if(map.get(bean.getProcessStatus()) == null) {
            mCompleteCount = 0;
        }else {
            mCompleteCount = map.get(bean.getProcessStatus());
        }
        boolean isLast = false;
        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_MATCHING:
                allCount = data.getLimitAngel() + data.getLimitMan() + data.getLimitLady();
                isLast = bean.getCurrentCount()>=allCount?true:false;
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST:
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND:
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN:
                allCount = data.getLimitMan();
                isLast = mCompleteCount>=allCount?true:false;
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST:
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY:
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND:
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND:
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL:
                allCount = data.getLimitLady();
                isLast = mCompleteCount>=allCount?true:false;
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT:
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING:
                allCount = data.getLimitAngel();
                isLast = mCompleteCount>=allCount?true:false;
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY://问答环节，女生
                allCount = 2;//最后剩下两位被选中的女生
                isLast = mCompleteCount>=allCount?true:false;
                break;
        }
        if(isLast) {
            if(bean.getMessageType() == BaseStatus.MessageType.TYPE_RESPONSE) {
                mResponseRecievedLastMap.put(bean.getProcessStatus(),true);
            }else if (bean.getMessageType() == BaseStatus.MessageType.TYPE_SEND){
                mSendRecievedLastMap.put(bean.getProcessStatus(),true);
            }
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

    /**
     * 重置问答环节检查是否为重复的参数
     */
    @Override
    public void resetQuestionCheckStatus(JMChartRoomSendBean sendBean) {
        mSendCompleteCountMap.remove(sendBean.getProcessStatus());
        mSendRecievedIndexMap.remove(sendBean.getProcessStatus());
        mSendRecievedLastMap.remove(sendBean.getProcessStatus());
    }
}
