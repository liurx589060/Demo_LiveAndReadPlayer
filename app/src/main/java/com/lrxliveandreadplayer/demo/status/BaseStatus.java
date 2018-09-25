package com.lrxliveandreadplayer.demo.status;
import com.lrxliveandreadplayer.demo.beans.jmessage.Data;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;
import com.lrxliveandreadplayer.demo.manager.DataManager;
import com.lrxliveandreadplayer.demo.utils.Tools;

import java.util.Map;

/**
 * Created by Administrator on 2018/9/23.
 */

public abstract class BaseStatus {
    public enum MessageType {
        TYPE_SEND,
        TYPE_RESPONSE
    }

    private int mCompleteCount = 0;
    private IStatusListener mListener = null;

    public abstract String getTypesWithString();  //字符的类型标识
    public abstract String getPublicString();    //对外解释
    public abstract int getLiveTimeCount();      //倒计时
    public abstract int getStatus();             //状态（int型）
    public abstract String getRequestGender();        //必须的性别
    public abstract String getRequestRoleType();     //必须的角色

    public abstract boolean isLast(JMChartRoomSendBean sendBean);       //检测是不是最后一个人
    public abstract JMChartRoomSendBean getChartSendBeanWillSend();
    public abstract void onHandler(StatusResp resp,JMChartRoomSendBean sendBean); //状态自己解析，然后把内容传入resp和sendBean中

    public void setStatusListener(IStatusListener listener) {
        mListener = listener;
    }
    public void handlerRoomChart(JMChartRoomSendBean sendBean) { //处理信息
        if(sendBean == null) {
            return;
        }
        StatusResp resp = new StatusResp();
        if(sendBean.getMessageType() == MessageType.TYPE_SEND) {
            resp.setMessageType(MessageType.TYPE_SEND);
        }else if(sendBean.getMessageType() == MessageType.TYPE_RESPONSE){
            resp.setMessageType(MessageType.TYPE_RESPONSE);
        }

        mCompleteCount ++;
        boolean last = isLast(sendBean);
        if(last) {
            mCompleteCount = 0;
        }
        resp.setLast(last);
        if(mListener != null) {
            mListener.onHandleResp(resp,sendBean);
        }
    }

    protected JMChartRoomSendBean createBaseChartRoomSendBean() {
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

    private boolean checkIsSelf(JMChartRoomSendBean bean) {
        int nextIndex = -1;
        int selfIndex = DataManager.getInstance().getSelfMember().getIndex();
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();

        if(getRequestGender().equals(userInfoBean.getGender())
                &&getRequestRoleType().equals(userInfoBean.getRole_type())
                &&selfIndex == nextIndex) {
            return true;
        }
        return false;
    }
}
