package com.lrxliveandreadplayer.demo.manager;

import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.activitys.XqChartActivity;
import com.lrxliveandreadplayer.demo.beans.JMNormalSendBean;
import com.lrxliveandreadplayer.demo.beans.jmessage.Data;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartResp;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMSendFlags;
import com.lrxliveandreadplayer.demo.beans.jmessage.Member;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;
import com.lrxliveandreadplayer.demo.glide.GlideCircleTransform;
import com.lrxliveandreadplayer.demo.interfaces.IHanderRoomMessage;
import com.lrxliveandreadplayer.demo.jmessage.JMsgSender;
import com.lrxliveandreadplayer.demo.network.NetWorkMg;
import com.lrxliveandreadplayer.demo.network.RequestApi;
import com.lrxliveandreadplayer.demo.utils.Constant;
import com.lrxliveandreadplayer.demo.utils.Tools;
import com.lrxliveandreadplayer.demo.utils.XqErrorCode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.ChatRoomMessageEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/5/26.
 */

public class XqChartUIViewMg implements IXqChartView {
    private View mRootView;
    private int SPACE_TOP = 0;
    private final int mCountDownTime_intro = 180;
    private final int mCountDownTime_first = 60;
    private final int mPrepareTime = 5;
    private RequestApi mApi;

    private ViewInstance mAngelViewInstance = new ViewInstance();
    private ViewInstance mManViewInstance = new ViewInstance();

    private XqChartActivity mXqActivity;
    private Map<Integer,UserInfoBean> mAngelMembersMap;
    private Map<Integer,UserInfoBean> mManMembersMap;
    private Map<Integer,UserInfoBean> mLadyMembersMap;
    private List<JMChartRoomSendBean> mSystemEventList;

    private RecyclerView mRecyclerMembers;
    private RecyclerView mRecyclerSystem;
    private Button mBtnExit;
    private Button mBtnGift;
    private TextView mTextTip;
    private TextView mTextCountDown;

    private MemberRecyclerdapter mMemberAdapter;
    private SystemRecyclerdapter mSystemAdapter;

    private AbsRoomController mChartRoomController;
    private Handler mHandler;
    private Runnable mTimeRunnable;
    private int timeCount = 0;

    private int mStartOrderIndex_first = 0;
    private int mStartOrderIndex_matching = 0;

    @Override
    public View createView() {
        if(mRootView == null) {
            mRootView = LayoutInflater.from(mXqActivity).inflate(R.layout.layout_chart_ui,null);
        }
        return mRootView;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
    }

    private void initMemberRecyclerView() {
        mMemberAdapter = new MemberRecyclerdapter();
        mRecyclerMembers.setLayoutManager(new LinearLayoutManager(mXqActivity) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRecyclerMembers.setAdapter(mMemberAdapter);
        mRecyclerMembers.addItemDecoration(new MemberSpaceDecoration());
    }

    private void initSystemRecyclerView() {
        mSystemAdapter = new SystemRecyclerdapter();
        mRecyclerSystem.setLayoutManager(new LinearLayoutManager(mXqActivity));
        mRecyclerSystem.setAdapter(mSystemAdapter);
    }

    public XqChartUIViewMg(XqChartActivity xqActivity) {
        this.mXqActivity = xqActivity;
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        mAngelMembersMap = new HashMap<>();
        mManMembersMap = new HashMap<>();
        mLadyMembersMap = new HashMap<>();
        mSystemEventList = new ArrayList<>();
        mHandler = new Handler();
        SPACE_TOP = Tools.dip2px(mXqActivity,10);

        mRootView = LayoutInflater.from(mXqActivity).inflate(R.layout.layout_chart_ui,null);
        mRecyclerMembers = mRootView.findViewById(R.id.recycle_chart_members);
        mRecyclerSystem = mRootView.findViewById(R.id.recycle_chart_system);
        mBtnExit = mRootView.findViewById(R.id.btn_chart_exit);
        mBtnGift = mRootView.findViewById(R.id.btn_chart_gift);
        mTextCountDown = mRootView.findViewById(R.id.text_timer);
        mTextTip = mRootView.findViewById(R.id.text_tip);
        mTextTip.setVisibility(View.GONE);

        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitChartRoom();
            }
        });

        mBtnGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChartRoomMessage();
            }
        });

        initAngelManViewInstance();
        initMemberRecyclerView();
        initSystemRecyclerView();

        mChartRoomController = new JMChartRoomController(handerRoomMessageListener);

        upDataMembers();
        JMessageClient.registerEventReceiver(this);
    }

    private void sendChartRoomMessage() {
        JMChartRoomSendBean chartRoomSendBean = new JMChartRoomSendBean();
        chartRoomSendBean.setTime(Tools.getCurrentDateTime());
        chartRoomSendBean.setMsg("上的肥肉酷酷酷酷酷酷");
        chartRoomSendBean.setRoomId(DataManager.getInstance().getChartData().getRoomId());
        JMsgSender.sendRoomMessage(chartRoomSendBean);
    }

    private void initAngelManViewInstance() {
        View view = mRootView.findViewById(R.id.include_head_angel_man);
        mAngelViewInstance.mImgHead = view.findViewById(R.id.img_head);
        mAngelViewInstance.mTxvNickName = view.findViewById(R.id.text_nickName);
        mAngelViewInstance.mTxvNum = view.findViewById(R.id.text_num);

        mManViewInstance.mImgHead = view.findViewById(R.id.img_head_2);
        mManViewInstance.mTxvNickName = view.findViewById(R.id.text_nickName_2);
        mManViewInstance.mTxvNum = view.findViewById(R.id.text_num_2);
    }

    private void upDataMembers() {
        List<Member> members = DataManager.getInstance().getChartData().getMembers();
        for (int i = 0 ; i < members.size() ; i++) {
            Member member = members.get(i);
            int index = member.getIndex();
            UserInfoBean bean = member.getUserInfo();
            if(bean.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                mAngelMembersMap.put(index,bean);
            }else if(bean.getRole_type().equals(Constant.ROLETYPE_GUEST)){
                if(bean.getGender().equals(Constant.GENDER_MAN)) {
                    mManMembersMap.put(index,bean);
                }else {
                    mLadyMembersMap.put(index,bean);
                }
            }
        }

        UserInfoBean angelBean = mAngelMembersMap.get(0);
        if(angelBean != null) {
            mAngelViewInstance.mTxvNum.setText("Angel.");
            mAngelViewInstance.mTxvNickName.setText(angelBean.getNick_name());
            Glide.with(mXqActivity)
                    .load(angelBean.getHead_image())
                    .placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(mAngelViewInstance.mImgHead);
        }

        UserInfoBean manBean = mManMembersMap.get(0);
        if(manBean != null) {
            mManViewInstance.mTxvNum.setText("Man.");
            mManViewInstance.mTxvNickName.setText(manBean.getNick_name());
            Glide.with(mXqActivity)
                    .load(angelBean.getHead_image())
                    .placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(mManViewInstance.mImgHead);
        }

        mMemberAdapter.notifyDataSetChanged();
    }

    private void updateSystemEvent() {
        mSystemAdapter.notifyDataSetChanged();
    }

    private void getChartRoomMembersList(long roomId) {
        mApi.getChartRoomMemeberList(roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if(jmChartResp == null) {
                            Log.e("yy","jmChartResp is null");
                            Tools.toast(mXqActivity,"jmChartResp is null",true);
                            return;
                        }
                        if(jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("yy",jmChartResp.getMsg());
                            Tools.toast(mXqActivity,jmChartResp.getMsg(),true);
                            return;
                        }
                        DataManager.getInstance().setChartData(jmChartResp.getData());
                        upDataMembers();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("yy",throwable.toString());
                        Tools.toast(mXqActivity,throwable.toString(),true);
                    }
                });
    }

    private void exitChartRoom() {
        HashMap<String,Object> params = new HashMap<>();
        params.put("roomId",DataManager.getInstance().getChartData().getRoomId());
        params.put("userName",DataManager.getInstance().getUserInfo().getUser_name());
        mApi.exitChartRoom(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if(jmChartResp == null) {
                            Log.e("yy","jmChartResp is null");
                            Tools.toast(mXqActivity,"exit room failed",true);
                            return;
                        }
                        if(jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("yy",jmChartResp.getMsg());
                            Tools.toast(mXqActivity,jmChartResp.getMsg(),true);
                            return;
                        }
                        mXqActivity.finish();
                        //通知聊天室的其他人,是创建者
                        if(DataManager.getInstance().getSelfMember().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                            norifyRoomExit();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("yy",throwable.toString());
                        Tools.toast(mXqActivity,throwable.toString(),true);
                    }
                });
    }

    private void norifyRoomExit() {
        for (Member member:DataManager.getInstance().getChartData().getMembers()) {
            if(!member.getUserInfo().getUser_name().equals(DataManager.getInstance().getUserInfo().getUser_name())) {
                JMNormalSendBean normalSendBean = new JMNormalSendBean();
                normalSendBean.setCode(JMNormalSendBean.NORMAL_EXIT);
                normalSendBean.setTargetUserName(member.getUserInfo().getUser_name());
                JMsgSender.sendNomalMessage(normalSendBean);
            }
        }
    }

    private class MemberRecyclerdapter extends RecyclerView.Adapter<MemberViewHolder> {
        @Override
        public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MemberViewHolder viewHolder = new MemberViewHolder(LayoutInflater.from(mXqActivity)
                    .inflate(R.layout.layout_chart_ui_head,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MemberViewHolder holder, int position) {
            setData(holder.mLeft,position,0);
            setData(holder.mRight,position,5);
        }

        public void setData(ViewInstance viewInstance,int position,int offset) {
            viewInstance.mTxvNickName.setTextSize(Tools.dip2px(mXqActivity,3));
            viewInstance.mTxvNum.setTextSize(Tools.dip2px(mXqActivity,4));

            int index = offset + position;
            UserInfoBean bean = mLadyMembersMap.get(index);
            if(bean == null) return;

            viewInstance.mTxvNum.setText(String.valueOf(index + 1));
            viewInstance.mTxvNickName.setText(bean.getNick_name());

            Glide.with(mXqActivity)
                    .load(bean.getHead_image())
                    .placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(viewInstance.mImgHead);
        }

        @Override
        public int getItemCount() {
            return (DataManager.getInstance().getChartData().getLimitLady() + 1)/2;
        }
    }

    private class SystemRecyclerdapter extends RecyclerView.Adapter<SystemRecyclerdapter.SystemViewHolder> {
        @Override
        public SystemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SystemViewHolder viewHolder = new SystemViewHolder(LayoutInflater.from(mXqActivity)
                    .inflate(R.layout.layout_chart_ui_item_system,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SystemViewHolder holder, int position) {
            JMChartRoomSendBean bean = mSystemEventList.get(position);
            if(bean == null) return;
            holder.mTxvEventTime.setText(bean.getTime());
            holder.mTxvEvent.setText(bean.getMsg());
        }

        @Override
        public int getItemCount() {
            return mSystemEventList.size();
        }

        public class SystemViewHolder extends RecyclerView.ViewHolder {
            public TextView mTxvEvent;
            public TextView mTxvEventTime;

            public SystemViewHolder(View itemView) {
                super(itemView);
                mTxvEventTime = itemView.findViewById(R.id.text_event_time);
                mTxvEvent = itemView.findViewById(R.id.text_event);
            }
        }
    }

    private class MemberViewHolder extends RecyclerView.ViewHolder {
        public ViewInstance mLeft = new ViewInstance();
        public ViewInstance mRight = new ViewInstance();

        public MemberViewHolder(final View itemView) {
            super(itemView);
            mLeft.mTxvNum = itemView.findViewById(R.id.text_num);
            mLeft.mTxvNickName = itemView.findViewById(R.id.text_nickName);
            mLeft.mImgHead = itemView.findViewById(R.id.img_head);

            mRight.mTxvNum = itemView.findViewById(R.id.text_num_2);
            mRight.mTxvNickName = itemView.findViewById(R.id.text_nickName_2);
            mRight.mImgHead = itemView.findViewById(R.id.img_head_2);

            itemView.post(new Runnable() {
                @Override
                public void run() {
                    Data data = DataManager.getInstance().getChartData();
                    int count = (data.getLimitLady() + 1)/2;
                    int height = (mRecyclerMembers.getHeight() - SPACE_TOP*count)/count;
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = height;
                    itemView.setLayoutParams(params);

                    setImageHeight(height,mLeft.mImgHead);
                    setImageHeight(height,mRight.mImgHead);
                }
            });
        }

        private void setImageHeight(final int parentHeight, final View view) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    int top = mLeft.mImgHead.getTop();
                    ViewGroup.LayoutParams imageParams = view.getLayoutParams();
                    int delta = parentHeight - top;
                    imageParams.height = delta;
                    imageParams.width = delta;
                    view.setLayoutParams(imageParams);
                }
            });
        }
    }

    private class ViewInstance {
        public TextView mTxvNum;
        public TextView mTxvNickName;
        public ImageView mImgHead;
    }

    private class MemberSpaceDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = SPACE_TOP;
        }
    }

    private IHanderRoomMessage handerRoomMessageListener = new IHanderRoomMessage() {
        @Override
        public void onMessageHandler(JMChartRoomSendBean chartRoomSendBean, JMSendFlags flags) {
            oprateStart(chartRoomSendBean,flags);
        }

        @Override
        public void onRoomMatching(JMChartRoomSendBean chartRoomSendBean, JMSendFlags flags) {
            mSystemEventList.add(chartRoomSendBean);
            updateSystemEvent();
            if(chartRoomSendBean.isUpdateMembers()) {
                getChartRoomMembersList(DataManager.getInstance().getChartData().getRoomId());
            }
        }
    };

    private void oprateStart_send(JMChartRoomSendBean bean,JMSendFlags flags) {

    }

    private void oprateStart_response(JMChartRoomSendBean bean,JMSendFlags flags) {
        mSystemEventList.add(bean);
        updateSystemEvent();
    }

    private boolean checkIsSelf(JMChartRoomSendBean bean,JMSendFlags flags) {
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
        if(flags.getGender().equals(userInfoBean.getGender())
                &&flags.getRoleType().equals(userInfoBean.getRole_type())
                &&(DataManager.getInstance().getSelfMember().getIndex()==bean.getIndexNext())) {
            return true;
        }
        return false;
    }

    private void oprateMatching(JMChartRoomSendBean bean,JMSendFlags flags) {
        mSystemEventList.add(bean);
        updateSystemEvent();
        //最后一个，则进入下一环节
        if(flags.isLast() ) {
            Tools.toast(mXqActivity,"即将进入男嘉宾自我介绍环节",false);
            JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
            sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN);
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
            sendBean.setIndexNext(0);
            JMsgSender.sendRoomMessage(sendBean);
        }
    }

    private void oprateIntroMan(JMChartRoomSendBean bean,JMSendFlags flags) {
        //当匹配到最后一个的时候，请男嘉宾自我介绍
        if(checkIsSelf(bean,flags)) {
            Tools.toast(mXqActivity,"请自我介绍",false);
            bean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN);
            startTiming(bean,flags);
            manIntroduce();
        }
    }

    /**
     * 操作开始
     */
    private void oprateStart(JMChartRoomSendBean bean,JMSendFlags flags) {
        if(flags.getMessageType() == JMSendFlags.MessageType.TYPE_RESPONSE) {
            //更新系统事件
           oprateStart_response(bean,flags);
           return;
        }

        //是否更新成员 列表
        if(bean.isUpdateMembers()) {
            getChartRoomMembersList(DataManager.getInstance().getChartData().getRoomId());
        }

        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_MATCHING:
                oprateMatching(bean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
                oprateIntroMan(bean,flags);
                break;
        }
    }

    /**
     * 倒计时超时
     */
    private void operateFinish(JMChartRoomSendBean bean,JMSendFlags sendFlags) {
        JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
        sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_MATCHING://匹配结束
//                sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_MATCHING);
//                sendBean.setLiveType(JMChartRoomSendBean.LIVE_NONE);
//                sendBean.setIndexNext(mStartOrderIndex_matching);
//                sendBean.setMsg("男嘉宾--" + sendBean.getUserName() + " 自我介绍");
                break;

            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN: //男生自我介绍完成
                45
                sendBean.setUserName(DataManager.getInstance().getUserInfo().getUser_name());
                sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN);
                sendBean.setIndexNext(bean.getIndexSelf() + 1);
                break;
        }
        JMsgSender.sendRoomMessage(sendBean);
        stopTiming();
    }

    /**
     * 男生自我介绍环节
     */
    private void manIntroduce() {
        JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
        sendBean.setMessageType(JMSendFlags.MessageType.TYPE_RESPONSE);
        sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN);
        sendBean.setMsg(sendBean.getUserName() + "--进行自我介绍");
        JMsgSender.sendRoomMessage(sendBean);
    }

    //开始计时
    private void startTiming(final JMChartRoomSendBean bean, final JMSendFlags flags) {
        if(mTimeRunnable == null) {
            mTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    timeCount++;
                    mHandler.postDelayed(this,1000);
                    int time = 0;
                    switch (bean.getProcessStatus()) {
                        case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
                            time = mCountDownTime_intro - timeCount;
                            break;
                    }

                    if(time > 0) {
                        mTextCountDown.setText(String.valueOf(mCountDownTime_intro ));
                    }else {
                        //自行操作结束
                        operateFinish(bean,flags);
                    }
                }
            };
        }
        mHandler.postDelayed(mTimeRunnable,1000);
    }

    private void stopTiming() {
        if(mTimeRunnable != null) {
            mHandler.removeCallbacks(mTimeRunnable);
        }
        timeCount = 0;
    }

    /**
     * 接收聊天室消息
     * @param event
     */
    public void onEventMainThread(ChatRoomMessageEvent event) {
        Log.d("yy", "chartRoomMessage received .");
        List<Message> msgs = event.getMessages();
        for (Message msg : msgs) {
            //这个页面仅仅展示聊天室会话的消息
            String jsonStr = msg.getContent().toJson();
            String text = null;
            try {
                JSONObject object = new JSONObject(jsonStr);
                text = object.getString("text");
            }catch (Exception e) {
                Log.e("yy",e.toString());
            }
            JMChartRoomSendBean chartRoomSendBean = new Gson().fromJson(text,JMChartRoomSendBean.class);
            mChartRoomController.handleRoomMessage(chartRoomSendBean);
        }
    }

    /**
     * 接收普通消息
     * @param event
     */
    public void onEventMainThread(MessageEvent event){
        Log.d("yy", "NormalMessage received .");
        //do your own business
        String message = event.getMessage().getContent().toJson();
        String text = null;
        try {
            JSONObject object = new JSONObject(message);
            text = object.getString("text");
        }catch (Exception e) {
            Log.e("yy",e.toString());
        }
        JMNormalSendBean normalSendBean = new Gson().fromJson(text,JMNormalSendBean.class);
        if(normalSendBean.getCode() == JMNormalSendBean.NORMAL_EXIT) {//离开
            mXqActivity.finish();
            Tools.toast(mXqActivity,"房间被解散",true);
        }
    }
}
