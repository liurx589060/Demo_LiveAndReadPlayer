package com.lrxliveandreadplayer.demo.status;
import com.lrxliveandreadplayer.demo.beans.jmessage.Data;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.beans.jmessage.Member;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;
import com.lrxliveandreadplayer.demo.manager.DataManager;
import com.lrxliveandreadplayer.demo.utils.Tools;

/**
 * Created by Administrator on 2018/9/23.
 */

public abstract class BaseStatus {
    public int getmOrder() {
        return mOrder;
    }

    public void setmOrder(int mOrder) {
        this.mOrder = mOrder;
    }

    public int getmStartIndex() {
        return mStartIndex;
    }

    public void setmStartIndex(int mStartIndex) {
        this.mStartIndex = mStartIndex;
    }

    public enum MessageType {
        TYPE_SEND,
        TYPE_RESPONSE
    }

    public enum HandleType {
        HANDLE_NONE,
        HANDLE_TIME,
        HANDLE_SELECT_MAN,
        HANDLE_SELECT_LAD,
        HANDLE_MATCH
    }

    private int mCompleteCount = 0;
    protected MessageType mMessageType = MessageType.TYPE_SEND;
    private IHandleListener mListener = null;
    protected Data mData = DataManager.getInstance().getChartData();
    protected UserInfoBean mUserInfo = DataManager.getInstance().getUserInfo();
    protected Member mSelfMember = DataManager.getInstance().getSelfMember();
    private int mOrder = -1;//流程序号
    private int mStartIndex = 0;//轮转的开始索引

    /**
     * 字符的类型标识
     * @return
     */
    public abstract String getTypesWithString();
    /**
     * 对外解释
     * @return
     */
    public abstract String getPublicString();

    /**
     * 倒计时
     * @return
     */
    public abstract int getLiveTimeCount();

    /**
     * 状态（int型）
     * @return
     */
    public abstract int getStatus();

    /**
     *下一个的算法
     * @param receiveBean
     * @return
     */
    public abstract int getNextIndex(JMChartRoomSendBean receiveBean);
    /**
     * 必须的性别
     * @return
     */
    public abstract String getRequestGender();

    /**
     * 必须的角色
     * @return
     */
    public abstract String getRequestRoleType();

    /**
     * 接受消息后需要处理的方式
     * @return
     */
    public abstract HandleType getHandleType();

    /**
     * 检测是不是最后一个人
     * @param completeCount
     * @param receiveBean
     * @return
     */
    public abstract boolean isLast(int completeCount,JMChartRoomSendBean receiveBean);

    /**
     * 获取要发送的JMChartRoomSendBean
     * @return
     */
    public abstract JMChartRoomSendBean getChartSendBeanWillSend(JMChartRoomSendBean receiveBean);

    /**
     * 状态自己解析，然后把内容传入resp和sendBean中
     * @param resp
     * @param receiveBean
     */
    public abstract void onHandler(StatusResp resp,JMChartRoomSendBean receiveBean);

    /**
     * 设置处理后的监听
     * @param listener
     */
    public void setHandleListener(IHandleListener listener) {
        mListener = listener;
    }

    /**
     * 处理信息
     * @param receiveBean
     */
    public void handlerRoomChart(JMChartRoomSendBean receiveBean) {
        if(receiveBean == null) {
            return;
        }

        if(getStatus() != receiveBean.getProcessStatus()) {
            //不处理其他的消息，只处理自己的消息
            return;
        }


        StatusResp resp = new StatusResp();
        onHandler(resp,receiveBean);
        if(receiveBean.getMessageType() == MessageType.TYPE_SEND) {
            resp.setMessageType(MessageType.TYPE_SEND);
            mCompleteCount ++;
        }else if(receiveBean.getMessageType() == MessageType.TYPE_RESPONSE){
            resp.setMessageType(MessageType.TYPE_RESPONSE);
        }
        mMessageType = receiveBean.getMessageType();

        boolean last = isLast(mCompleteCount,receiveBean);
        if(last) {
            mCompleteCount = 0;
        }
        resp.setLast(last);
        resp.setSelf(checkIsSelf(receiveBean));
        resp.setHandleType(getHandleType());
        resp.setTimeDownCount(getLiveTimeCount());
        resp.setPublicString(getPublicString());
        if(mListener != null) {
            mListener.onHandleResp(this,resp,receiveBean);
        }
    }

    /**
     * 创建基础的发送JMChartRoomSendBean
     * @return
     */
    public JMChartRoomSendBean createBaseChartRoomSendBean() {
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
     * 检查是否是自己
     * @param bean
     * @return
     */
    protected boolean checkIsSelf(JMChartRoomSendBean bean) {
        int selfIndex = DataManager.getInstance().getSelfMember().getIndex();
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();

        if(getRequestGender().equals(userInfoBean.getGender())
                &&getRequestRoleType().equals(userInfoBean.getRole_type())
                &&selfIndex == getNextIndex(bean)) {
            return true;
        }
        return false;
    }
}
