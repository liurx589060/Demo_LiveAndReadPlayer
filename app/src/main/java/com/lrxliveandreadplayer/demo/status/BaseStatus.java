package com.lrxliveandreadplayer.demo.status;
import com.lrxliveandreadplayer.demo.beans.jmessage.Data;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;
import com.lrxliveandreadplayer.demo.manager.DataManager;
import com.lrxliveandreadplayer.demo.utils.Tools;

/**
 * Created by Administrator on 2018/9/23.
 */

public abstract class BaseStatus {
    public abstract String getTypesWithString();  //字符的类型标识
    public abstract String getPublicString();    //对外解释
    public abstract int getLiveTimeCount();      //倒计时
    public abstract int getStatus();             //状态（int型）
    public abstract String getRequestGender();        //必须的性别
    public abstract String getRequestRoleType();     //必须的角色

    public abstract boolean isLast();       //检测是不是最后一个人
    public abstract void onHandlerRoomChart(JMChartRoomSendBean sendBean);      //接收到消息
    public abstract JMChartRoomSendBean getChartSendBeanWillSend();

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
