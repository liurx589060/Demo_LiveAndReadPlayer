package com.lrxliveandreadplayer.demo.manager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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
import com.lrxliveandreadplayer.demo.interfaces.IPopupAngelListener;
import com.lrxliveandreadplayer.demo.interfaces.IPopupGuestListener;
import com.lrxliveandreadplayer.demo.jmessage.JMsgSender;
import com.lrxliveandreadplayer.demo.network.NetWorkMg;
import com.lrxliveandreadplayer.demo.network.RequestApi;
import com.lrxliveandreadplayer.demo.utils.Constant;
import com.lrxliveandreadplayer.demo.utils.Tools;
import com.lrxliveandreadplayer.demo.utils.XqErrorCode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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

public class XqChartUIViewMg extends AbsChartView {
    private AbsChartView mXqCameraViewMg;
    private AbsChartView mXqPlayerViewMg;
    private ArrayList<AbsChartView> viewMgList = new ArrayList<>();

    private View mRootView;
    private int SPACE_TOP = 0;
    private int ANGEL_DISTURB_COUNT = 3;
    private final int QUESTION_COUNT = 2;//两轮问答
    private final int mCountDownTime_intro_man = 180;
    private final int mCountDownTime_ladySelect_first = 10;
    private final int mCountDownTime_intro_lady = 120;
    private final int mCountDownTime_peformance_man = 180;
    private final int mCountDownTime_chart_angel = 180;
    private final int mCountDownTime_ManSelect_first = 20;
    private final int mCountDownTime_Question_man = 60;
    private final int mCountDownTime_Question_lady = 120;
    private final int mCountDownTime_Angel_Disturb = 120;
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
    private Button mBtnEnd;
    private TextView mTextTip;
    private TextView mTextCountDown;

    private MemberRecyclerdapter mMemberAdapter;
    private SystemRecyclerdapter mSystemAdapter;

    private AbsRoomController mChartRoomController;
    private PopupViewMg mPopupViewMg;
    private Handler mHandler;
    private Runnable mTimeRunnable;
    private int timeCount = 0;

    private int mStartOrderIndex_intro_lady = 0;
    private int mStartOrderIndex_intro_man = 0;
    private int mProgressStatus = -1;
    private int mQuestionNum = 0;
    private AlertDialog mLadySelectDialog;
    private boolean mLadySelecteResult = true; //默认为都接受
    private ArrayList<String> mManSelectedResultList = new ArrayList<>();
    private ArrayList<String> mLadySelectedResultList = new ArrayList<>();
    private int mAngelDisturbNum = 0;
    private boolean mAngelIsDistub;
    private JMChartRoomSendBean mCurrentRoomSendBean;
    private JMSendFlags mCurrentRoomFlags;
    private JMChartRoomSendBean mStartTimeRoomSendBean;
    private JMSendFlags mStartTimeSendFlags;
    private boolean mIsSelfSelected = false;

    private boolean mIsVisible = false;

    public void setContentView() {
        initAndSetContentView();
        //mXqActivity.setContentView(mRootView);
    }

    @Override
    public View getView() {
        return mRootView;
    }

    @Override
    public void onResume() {
        for (AbsChartView viewMg:viewMgList) {
            if(viewMg != null) {
                viewMg.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        for (AbsChartView viewMg:viewMgList) {
            if(viewMg != null) {
                viewMg.onPause();
            }
        }
    }

    @Override
    public void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        for (AbsChartView viewMg:viewMgList) {
            if(viewMg != null) {
                viewMg.onDestroy();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        for (AbsChartView viewMg:viewMgList) {
            if(viewMg != null) {
                viewMg.onConfigurationChanged(newConfig);
            }
        }
    }

    @Override
    public void setVisible(boolean isVisible) {
        if(mRootView != null) {
            mRootView.setVisibility(isVisible?View.VISIBLE:View.INVISIBLE);
        }
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
        mChartRoomController = new JMChartRoomController(handerRoomMessageListener);
        mPopupViewMg = new PopupViewMg();

        mRootView = LayoutInflater.from(mXqActivity).inflate(R.layout.layout_chart_ui,null);
        mRecyclerMembers = mRootView.findViewById(R.id.recycle_chart_members);
        mRecyclerSystem = mRootView.findViewById(R.id.recycle_chart_system);
        mBtnExit = mRootView.findViewById(R.id.btn_chart_exit);
        mBtnGift = mRootView.findViewById(R.id.btn_chart_gift);
        mBtnEnd = mRootView.findViewById(R.id.btn_chart_end);
        mTextCountDown = mRootView.findViewById(R.id.text_timer);
        mTextTip = mRootView.findViewById(R.id.text_tip);
        mTextTip.setVisibility(View.INVISIBLE);
        mTextCountDown.setVisibility(View.INVISIBLE);

        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitChartRoom();
            }
        });

        mBtnGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mXqCameraViewMg.setVisible(true);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mXqCameraViewMg.start();
//                    }
//                },100);

                mXqPlayerViewMg.setVisible(true);
//                mIsVisible = !mIsVisible;
//                mXqPlayerViewMg.setVisible(mIsVisible);
            }
        });

        mBtnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStartTimeRoomSendBean != null && mStartTimeSendFlags != null) {
                    //结束语音或视频
                    stopTiming();
                    onOperateEnd(mStartTimeRoomSendBean,mStartTimeSendFlags);
                }
            }
        });

        initAngelManViewInstance();
        initMemberRecyclerView();
        initSystemRecyclerView();

        upDataMembers();
        JMessageClient.registerEventReceiver(this);
    }

    /**
     * 初始化
     */
    private void initAndSetContentView() {
        mXqCameraViewMg = new XqTxPushViewMg();
        mXqCameraViewMg.init(mXqActivity,NetWorkMg.getCameraUrl());
        mXqCameraViewMg.start();
        mXqCameraViewMg.setVisible(false);

        mXqPlayerViewMg = new XqTxPlayerViewMg();
        mXqPlayerViewMg.init(mXqActivity,NetWorkMg.getCameraUrl());
        mXqPlayerViewMg.start();
        mXqPlayerViewMg.setVisible(false);

        viewMgList.add(mXqCameraViewMg);
        viewMgList.add(mXqPlayerViewMg);

        mXqActivity.setContentView(mXqCameraViewMg.getView());
        mXqActivity.addContentView(mXqPlayerViewMg.getView(),new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mXqActivity.addContentView(mRootView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initAngelManViewInstance() {
        View view = mRootView.findViewById(R.id.include_head_angel_man);
        mAngelViewInstance.mImgHead = view.findViewById(R.id.img_head);
        mAngelViewInstance.mTxvNickName = view.findViewById(R.id.text_nickName);
        mAngelViewInstance.mTxvNum = view.findViewById(R.id.text_num);
        mAngelViewInstance.mImgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
                if(!userInfoBean.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) return;
                if(mAngelDisturbNum >= ANGEL_DISTURB_COUNT) {
                    Tools.toast(mXqActivity,"您已经插话3次，不能插话了",true);
                    return;
                }
                if(mProgressStatus == JMChartRoomSendBean.CHART_STATUS_INTRO_LADY
                        || mProgressStatus == JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND) {
                    mPopupViewMg.showAngelPopupView(mXqActivity, mAngelViewInstance.mImgHead, new IPopupAngelListener() {
                        @Override
                        public void onDisturb(View view1) {
                            //发送插话
                            JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
                            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                            sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_ANGEL_QUEST_DISTURB);
                            sendBean.setLiveType(JMChartRoomSendBean.LIVE_MIC);
                            sendBean.setMsg("爱心大使 " + userInfoBean.getNick_name() + " 要求插话");
                            sendRoomMessage(sendBean);
                            mAngelDisturbNum++;
                            Tools.toast(mXqActivity,"您要求插话",false);
                        }
                    });
                }
            }
        });

        mManViewInstance.mImgHead = view.findViewById(R.id.img_head_2);
        mManViewInstance.mTxvNickName = view.findViewById(R.id.text_nickName_2);
        mManViewInstance.mTxvNum = view.findViewById(R.id.text_num_2);

        mManViewInstance.mImgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
                if(userInfoBean.getRole_type().equals(Constant.ROLETYPE_GUEST)
                        && userInfoBean.getGender().equals(Constant.GENDER_MAN)) {
                    if(mProgressStatus != JMChartRoomSendBean.CHART_STATUS_INTRO_MAN
                            && mProgressStatus != JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE
                            && mProgressStatus != JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN) {
                        return;
                    }
                    showGuestPopWindow(view, PopupViewMg.Position.LEFT);
                }
            }
        });
    }

    /**
     * 显示嘉宾的弹窗
     * @param showView
     */
    private void showGuestPopWindow(View showView, PopupViewMg.Position position) {
        final UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
        mPopupViewMg.showGuestPopupView(mXqActivity, showView, position, new IPopupGuestListener() {
            @Override
            public void onType(View view, PopupViewMg.LiveType liveType) {
                //发送直播方式更改
                JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
                sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_CHART_CHANGR_LIVETYPE);
                int type = JMChartRoomSendBean.LIVE_NONE;
                String liveStr = "";
                switch (liveType) {
                    case LIVE_VIDEO:
                        type = JMChartRoomSendBean.LIVE_CAMERA;
                        liveStr = "相机";
                        break;
                    case LIVE_MIC:
                        type = JMChartRoomSendBean.LIVE_MIC;
                        liveStr = "音频";
                        break;
                    case LIVE_NONE:
                        type = JMChartRoomSendBean.LIVE_NONE;
                        liveStr = "不使用";
                        break;
                }
                sendBean.setLiveType(type);
                sendBean.setMsg(userInfoBean.getNick_name() + "更改直播方式--" + liveStr);
                sendRoomMessage(sendBean);
                Tools.toast(mXqActivity,"您更改直播方式为--" + liveStr,false);
            }
        });
    }

    private void upDataMembers() {
        mAngelMembersMap.clear();
        mManMembersMap.clear();
        mLadyMembersMap.clear();
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
            String text = angelBean.getNick_name();
            if(angelBean.getNick_name().equals(DataManager.getInstance().getUserInfo().getNick_name())) {
                text += "--self";
            }
            mAngelViewInstance.mTxvNickName.setText(text);
            Glide.with(mXqActivity)
                    .load(angelBean.getHead_image())
                    .placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(mAngelViewInstance.mImgHead);
        }

        UserInfoBean manBean = mManMembersMap.get(0);
        if(manBean != null) {
            mManViewInstance.mTxvNum.setText("Man.");
            String text = manBean.getNick_name();
            if(manBean.getNick_name().equals(DataManager.getInstance().getUserInfo().getNick_name())) {
                text += "--self";
            }
            mManViewInstance.mTxvNickName.setText(text);
            Glide.with(mXqActivity)
                    .load(manBean.getHead_image())
                    .placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(mManViewInstance.mImgHead);
        }

        mMemberAdapter.notifyDataSetChanged();
    }

    private void updateSystemEvent() {
        mSystemAdapter.notifyDataSetChanged();
    }

    @SuppressLint("CheckResult")
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

    @SuppressLint("CheckResult")
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
                            norifyRoomExit(JMNormalSendBean.NORMAL_EXIT);
                        }else {
//                            JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
//                            sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_CHART_EXIT_ROOM);
//                            sendBean.setMsg(DataManager.getInstance().getUserInfo().getNick_name() + "--离开房间");
//                            sendRoomMessage(sendBean);

                            for (Member member:DataManager.getInstance().getChartData().getMembers()) {
                                if(!member.getUserInfo().getUser_name().equals(DataManager.getInstance().getUserInfo().getUser_name())) {
                                    JMNormalSendBean normalSendBean = new JMNormalSendBean();
                                    normalSendBean.setCode(JMChartRoomSendBean.CHART_STATUS_CHART_EXIT_ROOM);
                                    normalSendBean.setTargetUserName(member.getUserInfo().getUser_name());
                                    normalSendBean.setTime(Tools.getCurrentDateTime());
                                    normalSendBean.setMsg(DataManager.getInstance().getUserInfo().getNick_name() + "--离开房间");
                                    JMsgSender.sendNomalMessage(normalSendBean);
                                }
                            }
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

    private void norifyRoomExit(int code) {
        for (Member member:DataManager.getInstance().getChartData().getMembers()) {
            if(!member.getUserInfo().getUser_name().equals(DataManager.getInstance().getUserInfo().getUser_name())) {
                JMNormalSendBean normalSendBean = new JMNormalSendBean();
                normalSendBean.setCode(code);
                normalSendBean.setTargetUserName(member.getUserInfo().getUser_name());
                JMsgSender.sendNomalMessage(normalSendBean);
            }
        }
    }

    private class MemberRecyclerdapter extends RecyclerView.Adapter<MemberViewHolder> {
        private boolean mIsSelect;

        @Override
        public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MemberViewHolder viewHolder = new MemberViewHolder(LayoutInflater.from(mXqActivity)
                    .inflate(R.layout.layout_chart_ui_head,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MemberViewHolder holder, int position) {
            setData(holder.mLeft,position,0);
            if(DataManager.getInstance().getChartData().getLimitLady() >= (Constant.MAX_LADY_COUNT + 1)/2) {
                setData(holder.mRight,position,Constant.MAX_LADY_COUNT/2);
            }else {
                setData(holder.mRight,position,1);
            }
        }

        public void setData(final ViewInstance viewInstance, int position, int offset) {
            viewInstance.mTxvNickName.setTextSize(Tools.dip2px(mXqActivity,3));
            viewInstance.mTxvNum.setTextSize(Tools.dip2px(mXqActivity,4));

            final int index = offset + position;
            UserInfoBean bean = mLadyMembersMap.get(index);
            viewInstance.mTxvNum.setText(String.valueOf(index + 1));
            if(bean == null) {
                viewInstance.mTxvNickName.setText("");
                viewInstance.mImgHead.setImageResource(R.drawable.ic_launcher);
                return;
            }

            String text = bean.getNick_name();
            if(bean.getNick_name().equals(DataManager.getInstance().getUserInfo().getNick_name())) {
                text += "--self";
            }
            viewInstance.mTxvNickName.setText(text);

            Glide.with(mXqActivity)
                    .load(bean.getHead_image())
                    .placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(viewInstance.mImgHead);

            if(mIsSelect) {
                switch (mProgressStatus) {
                    case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://当为第一次选择时
                    case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://当为第二次选择时
                    case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY://问答环节
                    case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节
                    case JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL://问答环节
                        viewInstance.mViewSelect.setVisibility(View.VISIBLE);
                        if(mManSelectedResultList.contains(String.valueOf(index))) {
                            viewInstance.mImgSelect.setImageResource(R.drawable.head_select_p);
                        }else {
                            if(mProgressStatus == JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                                viewInstance.mViewSelect.setVisibility(View.INVISIBLE);
                            }
                            viewInstance.mImgSelect.setImageResource(R.drawable.head_select);
                        }

                        if(mManSelectedResultList.contains(String.valueOf(index))) {
                            if(mProgressStatus == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN
                                    ||mProgressStatus == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY) {
                                viewInstance.mImgSelect.setVisibility(View.INVISIBLE);
                            }else {
                                viewInstance.mImgSelect.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                        if(mManSelectedResultList.contains(String.valueOf(index))) {
                            viewInstance.mViewSelect.setVisibility(View.VISIBLE);
                            viewInstance.mImgSelect.setImageResource(R.drawable.head_select);
                        }
                        break;
                }
            }else {
                viewInstance.mViewSelect.setVisibility(View.INVISIBLE);
                viewInstance.mImgSelect.setImageResource(R.drawable.head_select);
            }

            viewInstance.mViewSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
                    if(userInfoBean.getGender().equals(Constant.GENDER_MAN)
                            &&userInfoBean.getRole_type().equals(Constant.ROLETYPE_GUEST)) {//只为男嘉宾
                        switch (mProgressStatus) {
                            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://当为第一次选择时
                            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://当为第二次选择时
                                if(mManSelectedResultList.contains(String.valueOf(index))) {
                                    Tools.toast(mXqActivity,"您已选择了该位嘉宾",false);
                                    return;
                                }
                                break;
                            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节
                            case JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL://问答环节
                                //不可选择
                                return;
                            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                                //清空选择
                                mManSelectedResultList.clear();
                                break;
                        }
                        mIsSelfSelected = true;
                        mManSelectedResultList.add(String.valueOf(index));
                        viewInstance.mImgSelect.setImageResource(R.drawable.head_select_p);
                        //停止计时
                        stopTiming();
                        onOperateEnd(mStartTimeRoomSendBean,mStartTimeSendFlags);
                        if(mProgressStatus != JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                            changeNormalStatus();
                        }
                    }
                }
            });

            viewInstance.mImgHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
                    if (userInfoBean.getRole_type().equals(Constant.ROLETYPE_GUEST)
                            && userInfoBean.getGender().equals(Constant.GENDER_LADY)
                            && index == DataManager.getInstance().getSelfMember().getIndex()) {//女生并且是自己
                        if(index > (DataManager.getInstance().getChartData().getLimitLady()/2 - 1)) {
                            showGuestPopWindow(view, PopupViewMg.Position.LEFT);
                        }else {
                            showGuestPopWindow(view, PopupViewMg.Position.RIGHT);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return (DataManager.getInstance().getChartData().getLimitLady() + 1)/2;
        }

        /**
         * 更改为选择模式
         */
        public void changeSelectStatus() {
            mIsSelect = true;
            notifyDataSetChanged();
        }

        /**
         * 更改为正常模式
         */
        public void changeNormalStatus() {
            mIsSelect = false;
            notifyDataSetChanged();
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
            holder.mTxvEvent.setText(bean.getMsg() + "\n" + new Gson().toJson(bean));
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
            mLeft.mRootView = itemView.findViewById(R.id.view_root);
            mLeft.mTxvNum = itemView.findViewById(R.id.text_num);
            mLeft.mTxvNickName = itemView.findViewById(R.id.text_nickName);
            mLeft.mImgHead = itemView.findViewById(R.id.img_head);
            mLeft.mViewSelect = itemView.findViewById(R.id.view_select);
            mLeft.mImgSelect = itemView.findViewById(R.id.image_select);

            mRight.mRootView = itemView.findViewById(R.id.view_root_2);
            mRight.mTxvNum = itemView.findViewById(R.id.text_num_2);
            mRight.mTxvNickName = itemView.findViewById(R.id.text_nickName_2);
            mRight.mImgHead = itemView.findViewById(R.id.img_head_2);
            mRight.mViewSelect = itemView.findViewById(R.id.view_select_2);
            mRight.mImgSelect = itemView.findViewById(R.id.image_select_2);

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
                    int delta = Math.min(parentHeight - top,(mRecyclerMembers.getWidth() - Tools.dip2px(mXqActivity,20))/2);
                    imageParams.height = delta;
                    imageParams.width = delta;
                    view.setLayoutParams(imageParams);
                }
            });
        }
    }

    private class ViewInstance {
        public View mRootView;
        public TextView mTxvNum;
        public TextView mTxvNickName;
        public ImageView mImgHead;
        public View mViewSelect;
        public ImageView mImgSelect;

        public void setVisible(boolean isVisible) {
            if(isVisible) {
                mRootView.setVisibility(View.VISIBLE);
            }else {
                mRootView.setVisibility(View.INVISIBLE);
            }
        }
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
            onOperateStart(chartRoomSendBean,flags);
        }
    };

    /**
     * 添加到系统事件并更新
     * @param bean
     */
    private void addSystemEventAndRefresh(JMChartRoomSendBean bean) {
        mSystemEventList.add(bean);
        updateSystemEvent();
        mRecyclerSystem.scrollToPosition(mSystemAdapter.getItemCount() - 1);
    }

    /************************Operate*******************************/
    /**
     * 操作开始
     * @param bean
     * @param flags
     */
    private void onOperateStart(JMChartRoomSendBean bean,JMSendFlags flags) {
        if(flags.getMessageType() == JMSendFlags.MessageType.TYPE_SEND) {//发送形式
            switch (bean.getProcessStatus()) {
                case JMChartRoomSendBean.CHART_STATUS_MATCHING://匹配
                case JMChartRoomSendBean.CHART_STATUS_ANGEL_QUEST_DISTURB://爱心大使插话
                case JMChartRoomSendBean.CHART_STATUS_CHART_CHANGR_LIVETYPE://更改直播方式
                case JMChartRoomSendBean.CHART_STATUS_CHART_EXIT_ROOM://离开房间
                    //更新系统事件
                    addSystemEventAndRefresh(bean);
                    break;
                case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN://男方自我介绍
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择
                case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY://女生自我介绍环节
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://男生第一次选择
                case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE://男生才艺表演
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择
                case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND://女生第二次谈话
                case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT://爱心大使有话说
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://男生第二次选择
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
                case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节，男生
                case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY://问答环节，女生
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING://爱心大使插话
                case JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL://结束
                    if(bean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                        mBtnExit.setVisibility(View.VISIBLE);
                    }else {
                        mBtnExit.setVisibility(View.INVISIBLE);
                    }
                    if(bean.getProcessStatus() != mProgressStatus) {
                        Tools.toast(mXqActivity,bean.getMsg(),false);
                        addSystemEventAndRefresh(bean);
                        mTextTip.setText(bean.getMsg());
                    }
                    break;
            }

            if(bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_ANGEL_QUEST_DISTURB
                    && bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING
                    && bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_CHART_CHANGR_LIVETYPE
                    && bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_CHART_EXIT_ROOM) {
                mCurrentRoomSendBean = bean;
                mCurrentRoomFlags = flags;
            }

        }else if (flags.getMessageType() == JMSendFlags.MessageType.TYPE_RESPONSE) {//回复形式
            switch (bean.getProcessStatus()) {
                case JMChartRoomSendBean.CHART_STATUS_MATCHING://匹配
                    break;
                case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN://男方自我介绍
                case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY://女生自我介绍
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择环节
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择环节
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://男生第一次选择环节
                case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND://女生第二次谈话
                case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT://爱心大使有话说
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://男生第二次选择环节
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE://男生才艺表演
                case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节，男生
                case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY://问答环节，女生
                    addSystemEventAndRefresh(bean);
                    break;
                case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING://爱心大使插话
                    addSystemEventAndRefresh(bean);
                    //收到爱心大使插话的回复后，插话标识置位false
                    mAngelIsDistub = false;
                    break;
                default:
                    break;
            }
        }

        if(bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_ANGEL_QUEST_DISTURB
                && bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING
                && bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_CHART_CHANGR_LIVETYPE) {
            mProgressStatus = bean.getProcessStatus();
        }
        //移交到进行中的处理
        onOperating(bean,flags);
    }

    /**
     * 操作中
     * @param bean
     * @param flags
     */
    private void onOperating(JMChartRoomSendBean bean,JMSendFlags flags) {
        JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();

        if(flags.getMessageType() == JMSendFlags.MessageType.TYPE_SEND) {//发送形式
            //先回复直播方式为none
            resetLiveStatus();
            stopTiming();
            switch (bean.getProcessStatus()) {
                case JMChartRoomSendBean.CHART_STATUS_MATCHING://匹配
                    //重新获取成员列表
                    getChartRoomMembersList(DataManager.getInstance().getChartData().getRoomId());
                    if(flags.isLast()) {
                        //发送下一轮，男方自我介绍环节
                        sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN);
                        sendBean.setIndexNext(mStartOrderIndex_intro_man);
                        sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                        sendBean.setMsg("进入第一环节，男生自我介绍");
                        sendRoomMessage(sendBean);
                        //隐藏离开按钮
                        mBtnExit.setVisibility(View.INVISIBLE);
                    }
                    break;
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择环节
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择环节
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
                    operate_SelectLady(bean,flags);
                    break;
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://男生第一次选择环节
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://男生第二次选择环节
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终环节
                    operate_SelectMan(bean,flags);
                    break;
                case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE://男生才艺表演
                case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND://女生第二次谈话
                case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT://爱心大使有话说
                case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY://女生自我介绍环节
                case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN://男方自我介绍
                case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节，男生
                case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY://问答环节，女生
                case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING://爱心大使插话
                    mTextTip.setVisibility(View.VISIBLE);
                    operate_Timing(bean,flags);
                    break;
                case JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL://结束
                    operate_Final(bean,flags);
                    break;
                case JMChartRoomSendBean.CHART_STATUS_ANGEL_QUEST_DISTURB://爱心大使插话
                    mAngelIsDistub = true;
                    break;
                case JMChartRoomSendBean.CHART_STATUS_CHART_CHANGR_LIVETYPE://更改直播方式
                    operate_LiveType(bean,flags);
                    break;
                case JMChartRoomSendBean.CHART_STATUS_CHART_EXIT_ROOM:
                    getChartRoomMembersList(DataManager.getInstance().getChartData().getRoomId());
                    break;
                default:
                    break;
            }

        }else if (flags.getMessageType() == JMSendFlags.MessageType.TYPE_RESPONSE) {//回复形式
            switch (bean.getProcessStatus()) {
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择环节
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择环节
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
                    operate_SelectLady_Response(bean,flags);
                    break;
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://男生第一次选择环节
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://男生第二次选择环节
                    operate_SelectMan_Response(bean,flags);
                    break;
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                    operate_SelectMan_Final_Response(bean,flags);
                    break;
                case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节，男生
                    mQuestionNum++;
                    break;
            }
        }
    }

    /**
     * 操作结束
     * @param bean
     * @param flags
     */
    private void onOperateEnd(JMChartRoomSendBean bean,JMSendFlags flags) {
        mTextCountDown.setVisibility(View.INVISIBLE);
        mTextCountDown.setText("");
        mBtnEnd.setVisibility(View.INVISIBLE);
//        resetLiveStatus();

        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN://男生自我介绍
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY://女生自我介绍
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE://男生才艺表演
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND://女生第二次谈话
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT://爱心大使有话说
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节，男生
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY://问答环节，女生
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING://爱心大使说话
                operate_Order_End(bean,flags);
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://男生第一次选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://男生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                operate_Response_End(bean,flags);
                break;
        }
    }
    /************************Operate*******************************/


    /***********************************各个环节的操作**************************************/
    /**
     * 女生选择
     * @param bean
     * @param flags
     */
    private void operate_SelectLady(JMChartRoomSendBean bean,JMSendFlags flags) {
        UserInfoBean userInfo = DataManager.getInstance().getUserInfo();

        //先匹配是否为自己
        if(checkIsSelf(bean,flags) && mLadySelecteResult) {//第一次是接受的情况
            mLadySelectDialog = createLadySelectDialog(bean,flags);
            mLadySelectDialog.show();
            startTiming(bean,flags);
        }else if(checkIsSelf(bean,flags) && !mLadySelecteResult){//第一次就拒绝的,直接返回回复
            //发送回应
            JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
            sendBean.setProcessStatus(bean.getProcessStatus());
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_RESPONSE);
            sendBean.setMsg(userInfo.getNick_name() + "--已做出选择");
            sendRoomMessage(sendBean);
        }
    }

    /**
     * 男生选择
     * @param bean
     * @param flags
     */
    private void operate_SelectMan(JMChartRoomSendBean bean,JMSendFlags flags) {
        //先匹配是否为自己
        if(checkIsSelf(bean,flags)) {
            //成员头像，切换为选择状态
            mMemberAdapter.changeSelectStatus();
            startTiming(bean,flags);
        }
    }

    /**
     * 女生选择回复
     * @param bean
     * @param flags
     */
    private void operate_SelectLady_Response(JMChartRoomSendBean bean,JMSendFlags flags) {
        int nextProgress = -1;
        int nextIndex = mStartOrderIndex_intro_lady;
        String msg = "";
        switch (bean.getProcessStatus()){
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_INTRO_LADY;
                msg = "进入第三个环节，女生自我介绍";
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND;
                msg = "进入女生第二次谈话";
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL:
                if(bean.isLadySelected()) {//收集其他女人的答案
                    mLadySelectedResultList.add(String.valueOf(bean.getIndexSelf()));
                }
                nextProgress = JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN;
                msg = "问答环节，男生";
                break;
        }
        //已经最后一个有回复了
        if(flags.isLast()) {
            //进入下一个环节,女生自我介绍
            JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
            sendBean.setProcessStatus(nextProgress);
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
            sendBean.setIndexNext(nextIndex);
            sendBean.setMsg(msg);
            sendRoomMessage(sendBean);
        }
    }

    /**
     * 男生选择回复
     * @param bean
     * @param flags
     */
    private void operate_SelectMan_Response(JMChartRoomSendBean bean,JMSendFlags flags) {
        int nextProgress = -1;
        int nextIndex = mStartOrderIndex_intro_lady;
        String msg = "";
        switch (bean.getProcessStatus()){
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE;
                msg = "进入第四个环节，男生才艺表演";
                break;
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL;
                msg = "女生最终选择";
                //解析男生的女生选择
                String[] texts = bean.getManSelects().split(",");
                List<String> manSelectList = Arrays.asList(texts);
                mManSelectedResultList.clear();
                mManSelectedResultList.addAll(manSelectList);
                break;
        }
        //已经最后一个有回复了
        if(flags.isLast()) {
            //进入下一个环节,男生才艺表演
            JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
            sendBean.setProcessStatus(nextProgress);
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
            sendBean.setIndexNext(nextIndex);
            sendBean.setMsg(msg);
            sendRoomMessage(sendBean);
        }
    }

    /**
     * 男生最终选择
     * @param bean
     * @param flags
     */
    private void operate_SelectMan_Final_Response(JMChartRoomSendBean bean,JMSendFlags flags) {
        //解析男生的女生选择
        String[] texts = bean.getManSelects().split(",");
        List<String> manSelectList = Arrays.asList(texts);
        mManSelectedResultList.clear();
        mManSelectedResultList.addAll(manSelectList);
        //已经最后一个有回复了
        if(flags.isLast()) {
            //进入下一个环节,流程完结
            JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
            sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL);
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
            sendBean.setMsg("流程结束，即将退出");
            sendRoomMessage(sendBean);
        }
    }

    /**
     * 倒计时操作
     * @param bean
     * @param flags
     */
    private void operate_Timing(JMChartRoomSendBean bean,JMSendFlags flags) {
        UserInfoBean userInfo = DataManager.getInstance().getUserInfo();
        JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
        String msg = "";
        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY:
                msg = userInfo.getNick_name() + "--开始自我介绍";
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND://女生第二轮谈话
                msg = userInfo.getNick_name() + "--说话";
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE:
                msg = userInfo.getNick_name() + "--开始才艺表演";
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT:
                msg = userInfo.getNick_name() + "--爱心大使说话";
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN:
                if(mQuestionNum == 0) {
                    msg = userInfo.getNick_name() + "男生第一次提问";
                }else if (mQuestionNum == 1) {
                    msg = userInfo.getNick_name() + "男生第二次提问";
                }
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY:
                msg = userInfo.getNick_name() + "女生回答";
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING:
                msg = userInfo.getNick_name() + "爱心大使说话--插话";
                break;
        }

        //先匹配是否为自己
        if(checkIsSelf(bean,flags)) {
            //发送回应
            sendBean.setProcessStatus(bean.getProcessStatus());
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_RESPONSE);
            sendBean.setLiveType(JMChartRoomSendBean.LIVE_CAMERA);//默认语音
            sendBean.setMsg(msg);
            sendRoomMessage(sendBean);
            //进行自我介绍
            Tools.toast(mXqActivity,msg,true);
            setLiveStatus(bean,true);
        }else {
            setLiveStatus(bean,false);
        }
        //倒计时
        startTiming(bean,flags);
    }

    /**
     * 流程结束
     * @param bean
     * @param flags
     */
    private void operate_Final(JMChartRoomSendBean bean,JMSendFlags flags) {
        String finalSelectLady = mManSelectedResultList.get(0);
        String text = "非常遗憾，匹配失败";
        if(mLadySelectedResultList.contains(finalSelectLady)) {
            text = "恭喜匹配成功，两人可进入下一环节";
            //更新
            mMemberAdapter.changeSelectStatus();
        }
        Tools.toast(mXqActivity,text,true);
        //显示离开按钮
        mBtnExit.setVisibility(View.VISIBLE);
        //显示在系统事件中
        JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
        sendBean.setMsg(text);
        addSystemEventAndRefresh(sendBean);
    }

    /**
     * 直播方式更改
     * @param bean
     * @param flags
     */
    private void operate_LiveType(JMChartRoomSendBean bean,JMSendFlags flags) {
        Tools.toast(mXqActivity,bean.getMsg(),false);
    }

    /**
     * 倒计时结束轮流的操作
     * @param bean
     * @param flags
     */
    private void operate_Order_End(JMChartRoomSendBean bean,JMSendFlags flags) {
        JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();

        int startIndex = 0;
        int nextIndex = sendBean.getIndexSelf() + 1;
        int nextProgress = -1;
        String msg = "";
        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST;
                startIndex = mStartOrderIndex_intro_lady;
                msg = "进入第一环节，女生第一次选择";
                if(flags.isLast()) {
                    mAngelDisturbNum = 0;
                }
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY:
                if(mAngelIsDistub) {
                    sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING);
                    sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                    sendBean.setIndexNext(0);
                    sendBean.setMsg("爱心大使插话");
                    sendRoomMessage(sendBean);
                    return;
                }else {
                    nextProgress = JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST;
                    msg = "进入第三轮，男生第一次选择";
                }
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND;
                msg = "进入第四环节，女生第二次选择";
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND;
                startIndex = mStartOrderIndex_intro_lady;
                msg = "进入第五环节，女生第二次谈话";
                if(flags.isLast()) {
                    mAngelDisturbNum = 0;
                }
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT;
                msg = "进入第六轮，爱心大使有话说";
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND;
                msg = "请男生选择心动女生";
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY;
                startIndex = Integer.valueOf(mManSelectedResultList.get(0)).intValue();
                msg = "问答环节，女生";
                //清空问答环节，男生的监测条件
                if(flags.isLast()) {
                    sendBean.setResetQuestionStatus(true);
                }
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY:
                if(mQuestionNum < QUESTION_COUNT) {
                    nextProgress = JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN;
                    msg = "问答环节,男生";
                    startIndex = mStartOrderIndex_intro_man;
                    //清空问答环节，男生的监测条件
                    if(flags.isLast()) {
                        sendBean.setResetQuestionStatus(true);
                    }
                }else {
                    nextProgress = JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL;
                    msg = "男生最终选择";
                    startIndex = mStartOrderIndex_intro_man;
                    mMemberAdapter.changeNormalStatus();
                }
                nextIndex = Integer.valueOf(mManSelectedResultList.get(1)).intValue();
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING:
                //接着打断的时候继续
                onOperateEnd(mCurrentRoomSendBean,mCurrentRoomFlags);
                break;
        }

        if(flags.isLast()) {
            //发送下一轮，请男生选择心动女生
            sendBean.setProcessStatus(nextProgress);
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
            sendBean.setMsg(msg);
            sendBean.setIndexNext(startIndex);
        }else {
            sendBean.setProcessStatus(bean.getProcessStatus());
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
            sendBean.setIndexNext(nextIndex);
        }
        sendRoomMessage(sendBean);
    }

    /**
     * 时间结束做出回应
     * @param bean
     * @param flags
     */
    private void operate_Response_End(JMChartRoomSendBean bean,JMSendFlags flags) {
        mTextCountDown.setVisibility(View.INVISIBLE);
        mBtnEnd.setVisibility(View.INVISIBLE);

        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();

        JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
                //时间到，默认为接收
                if(!mIsSelfSelected) {
                    if(!mLadySelecteResult) {
                        mLadySelecteResult = true;
                    }
                }
                mLadySelectDialog.dismiss();
                if(bean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL) {
                    sendBean.setLadySelected(mLadySelecteResult);
                }
                break;
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST:
                //默认选择第一个
                if(!mIsSelfSelected) {
                    mManSelectedResultList.add(String.valueOf(0));
                }
                mMemberAdapter.changeNormalStatus();
                break;
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND:
                //默认选择第一个
                if(!mIsSelfSelected) {
                    for(int i = 0 ; i < mManMembersMap.size();i++) {
                        if(!mManSelectedResultList.contains(i)) {
                            mManSelectedResultList.add(String.valueOf(i));
                            break;
                        }
                    }
                }
                mMemberAdapter.changeNormalStatus();
                //传输男生的选择女生
                String selectStr = "";
                for(int i = 0 ; i < mManSelectedResultList.size() ; i++) {
                    selectStr += mManSelectedResultList.get(i);
                    if(i < mManSelectedResultList.size() - 1) {
                        selectStr += ",";
                    }
                }
                sendBean.setManSelects(selectStr);
                break;
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL:
                //回复不选择状态
                mMemberAdapter.changeSelectStatus();
                //只剩最后一个，保留第一个元素
                if(!mIsSelfSelected) {
                    mManSelectedResultList.remove(1);
                }
                mMemberAdapter.changeNormalStatus();
                //传输男生的选择女生
                String selectStr2 = "";
                for(int i = 0 ; i < mManSelectedResultList.size() ; i++) {
                    selectStr2 += mManSelectedResultList.get(i);
                    if(i < mManSelectedResultList.size() - 1) {
                        selectStr2 += ",";
                    }
                }
                sendBean.setManSelects(selectStr2);
                break;
        }
        //发送回应
        sendBean.setProcessStatus(bean.getProcessStatus());
        sendBean.setMessageType(JMSendFlags.MessageType.TYPE_RESPONSE);
        sendBean.setMsg(userInfoBean.getNick_name() + "--已做出选择");
        sendRoomMessage(sendBean);
        mIsSelfSelected = false;
    }
    /***********************************各个环节的操作**************************************/

    private AlertDialog createLadySelectDialog(final JMChartRoomSendBean bean, final JMSendFlags flags) {
        final UserInfoBean userInfo = DataManager.getInstance().getUserInfo();
        String[] items = {"接收","拒接"};
        AlertDialog dialog = new AlertDialog.Builder(mXqActivity).setTitle("请做出选择").setIcon(R.drawable.ic_launcher)
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            mLadySelecteResult = true;
                        }else if(which == 1) {
                            mLadySelecteResult = false;
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mIsSelfSelected = true;
                        dialogInterface.dismiss();
                        stopTiming();
                        onOperateEnd(mStartTimeRoomSendBean,mStartTimeSendFlags);
                        //发送回应
                        JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
                        sendBean.setProcessStatus(bean.getProcessStatus());
                        sendBean.setMessageType(JMSendFlags.MessageType.TYPE_RESPONSE);
                        sendBean.setMsg(userInfo.getNick_name() + "--已做出选择");
                        sendRoomMessage(sendBean);
                    }
                })
                .create();
        return dialog;
    }

    /**
     * 检测是否为自己
     * @param bean
     * @param flags
     * @return
     */
    private boolean checkIsSelf(JMChartRoomSendBean bean,JMSendFlags flags) {
        int nextIndex = -1;
        int selfIndex = DataManager.getInstance().getSelfMember().getIndex();
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_MATCHING:
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN:
                nextIndex = bean.getIndexNext()%data.getLimitMan();
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST:
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST:
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND:
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL:
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND:
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL:
                //特殊情况，置为-1，屏蔽位置的限制
                selfIndex = -1;
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY:
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY:
                nextIndex = bean.getIndexNext()%data.getLimitLady();
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT:
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING:
                selfIndex = -1;
                flags.setGender(userInfoBean.getGender());
                break;
        }

        if(flags.getGender().equals(userInfoBean.getGender())
                &&flags.getRoleType().equals(userInfoBean.getRole_type())
                &&selfIndex == nextIndex) {
            return true;
        }
        return false;
    }

    //开始计时
    private void startTiming(final JMChartRoomSendBean bean, final JMSendFlags flags) {
        mTextCountDown.setVisibility(View.VISIBLE);
        mStartTimeRoomSendBean = bean;
        mStartTimeSendFlags = flags;
        if(bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST
                && bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND
                && bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL
                && bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST
                && bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND
                && bean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL) {
            if(checkIsSelf(bean,flags)) {
                mBtnEnd.setVisibility(View.VISIBLE);
            }
        }
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                timeCount++;
                int time = 0;
                switch (bean.getProcessStatus()) {
                    case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN:
                        time = mCountDownTime_intro_man - timeCount;
                        break;
                    case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST:
                    case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND:
                    case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL:
                        time = mCountDownTime_ladySelect_first - timeCount;
                        break;
                    case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY:
                    case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND:
                        time = mCountDownTime_intro_lady - timeCount;
                        break;
                    case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST:
                    case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND:
                    case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL:
                        time = mCountDownTime_ManSelect_first - timeCount;
                        break;
                    case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE:
                        time = mCountDownTime_peformance_man - timeCount;
                        break;
                    case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT:
                        time = mCountDownTime_chart_angel - timeCount;
                        break;
                    case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN:
                        time = mCountDownTime_Question_man - timeCount;
                        break;
                    case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY:
                        time = mCountDownTime_Question_lady - timeCount;
                        break;
                    case JMChartRoomSendBean.CHART_STATUS_ANGEL_DISTURBING:
                        time = mCountDownTime_Angel_Disturb - timeCount;
                        break;
                }

                if(time > 0) {
                    mTextCountDown.setText(String.valueOf(time) + "s");
                    mTextCountDown.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(this,1000);//下一次循环
                }else {
                    //自行操作结束
                    if(checkIsSelf(bean,flags)) {
                        onOperateEnd(bean,flags);
                    }
                    mTextCountDown.setVisibility(View.INVISIBLE);
                    stopTiming();//停止循环
                }
            }
        };
        stopTiming();
        mHandler.postDelayed(mTimeRunnable,1000);
    }

    private void stopTiming() {
        if(mTimeRunnable != null) {
            mHandler.removeCallbacks(mTimeRunnable);
        }
        timeCount = 0;
        mTextCountDown.setVisibility(View.INVISIBLE);
    }

    private void sendRoomMessage(JMChartRoomSendBean chartRoomSendBean) {
        JMsgSender.sendRoomMessage(chartRoomSendBean);
        mChartRoomController.handleRoomMessage(chartRoomSendBean);
    }

    /**
     * 更改直播方式的处理
     * @param chartRoomSendBean
     * @param isSelf
     */
    private void setLiveStatus(JMChartRoomSendBean chartRoomSendBean,boolean isSelf) {
        switch (chartRoomSendBean.getLiveType()) {
            case JMChartRoomSendBean.LIVE_MIC:
                if(isSelf) {
//                    mXqAudioViewMg.start();
                }else {
                    mXqPlayerViewMg.setVisible(true);
                }
                break;
            case JMChartRoomSendBean.LIVE_CAMERA:
                if(isSelf) {
                    mXqCameraViewMg.setVisible(true);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mXqCameraViewMg.setVisible(true);
                            mXqCameraViewMg.start();
                        }
                    },100);
                }else {
                    mXqPlayerViewMg.setVisible(true);
                }
                break;
            case JMChartRoomSendBean.LIVE_NONE:
                mXqCameraViewMg.setVisible(false);
                mXqPlayerViewMg.setVisible(false);
                break;
        }
    }

    /**
     * 重置直播方式，以备下次直播
     */
    private void resetLiveStatus() {
        mXqCameraViewMg.stop();
        mXqCameraViewMg.setVisible(false);
        mXqPlayerViewMg.stop();
        mXqPlayerViewMg.setVisible(false);
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
                return;
            }
            JMChartRoomSendBean chartRoomSendBean = new Gson().fromJson(text,JMChartRoomSendBean.class);
            //不接收流程前的消息
            if(mCurrentRoomSendBean != null && (mCurrentRoomSendBean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN
                    && mCurrentRoomSendBean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY)) {
                if(chartRoomSendBean.getProcessStatus() < mCurrentRoomSendBean.getProcessStatus()){
                    return;
                }
            }
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
            return;
        }
        JMNormalSendBean normalSendBean = new Gson().fromJson(text,JMNormalSendBean.class);
        if(normalSendBean.getCode() == JMNormalSendBean.NORMAL_EXIT) {//离开
            mXqActivity.finish();
            Tools.toast(mXqActivity,"房间被解散",true);
        }else if (normalSendBean.getCode() == JMChartRoomSendBean.CHART_STATUS_CHART_EXIT_ROOM) {
            JMChartRoomSendBean sendBean = new JMChartRoomSendBean();
            sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_CHART_EXIT_ROOM);
            sendBean.setMsg(normalSendBean.getMsg());
            sendBean.setTime(normalSendBean.getTime());
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
            if(mChartRoomController != null) {
                mChartRoomController.handleRoomMessage(sendBean);
            }
        }
    }
}
