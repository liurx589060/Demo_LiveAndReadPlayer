package com.lrxliveandreadplayer.demo.manager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.activitys.XqChartActivity;
import com.lrxliveandreadplayer.demo.beans.JMNormalSendBean;
import com.lrxliveandreadplayer.demo.beans.jmessage.Data;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartResp;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.beans.jmessage.Member;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;
import com.lrxliveandreadplayer.demo.glide.GlideCircleTransform;
import com.lrxliveandreadplayer.demo.interfaces.IPopupGuestListener;
import com.lrxliveandreadplayer.demo.jmessage.JMsgSender;
import com.lrxliveandreadplayer.demo.network.NetWorkMg;
import com.lrxliveandreadplayer.demo.network.RequestApi;
import com.lrxliveandreadplayer.demo.status.BaseStatus;
import com.lrxliveandreadplayer.demo.status.IHandleListener;
import com.lrxliveandreadplayer.demo.status.StatusResp;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusAngelChartBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusChartFinalBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusHelpDoingDisturbBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusHelpQuestDisturbBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusHelpChangeLiveTypeBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusHelpExitBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusLadyChartSecondBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusLadyFinalSelectBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusLadyFirstQuestionBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusLadySecondQuestionBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusLadySecondSelectBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusManFinalSelectBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusManIntroBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusLadyChartFirstBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusLadyFirstSelectBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusManFirstSelectBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusManPerformanceBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusManFirstQuestionBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusManSecondQuestionBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusManSecondSelectBean;
import com.lrxliveandreadplayer.demo.status.statusBeans.StatusMatchBean;
import com.lrxliveandreadplayer.demo.utils.Constant;
import com.lrxliveandreadplayer.demo.utils.Tools;
import com.lrxliveandreadplayer.demo.utils.XqErrorCode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class XqStatusChartUIViewMg extends AbsChartView implements IHandleListener {
    private XqTxPushViewMg mXqCameraViewMg;
    private XqTxPlayerViewMg mXqPlayerViewMg;
    private ArrayList<AbsChartView> viewMgList = new ArrayList<>();

    private View mRootView;
    private int SPACE_TOP = 0;
    private RequestApi mApi;
    private Map<Integer,BaseStatus> mOrderStatusMap = null;
    private Map<Integer,BaseStatus> mHelpStatusMap = null;
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
    private Button mBtnDisturb;
    private RadioGroup mRadioGroupLiveType;

    private MemberRecyclerdapter mMemberAdapter;
    private SystemRecyclerdapter mSystemAdapter;
    private LiveTypeRadioChangeListener mCheckChangedListener;

    private PopupViewMg mPopupViewMg;
    private Handler mHandler;
    private Runnable mTimeRunnable;
    private int timeCount = 0;

    private AlertDialog mLadySelectDialog;
    private ArrayList<String> mLadySelectedResultList = new ArrayList<>();
    private boolean mLadySelecteResult = true;
    private boolean mIsDistub = false;
    private int mDistubIndex = -1;
    private int mManSelectedIndex = -1;
    private StatusResp mStartStatusTimeStatusResp;
    private JMChartRoomSendBean mStartStatusRoomSendBean;
    private BaseStatus mStartStatusBasebean;
    private int mCurrentQuestDisturbCount = 0;
    private final int DISTURB_COUNT = 3; //一轮可插话的次数

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
        resetLiveStatus();
        stopTiming();
        for (AbsChartView viewMg:viewMgList) {
            if(viewMg != null) {
                viewMg.onDestroy();
            }
        }
        //重置roomId
        DataManager.getInstance().getChartData().setRoomId(0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        for (AbsChartView viewMg:viewMgList) {
            if(viewMg != null) {
                viewMg.onConfigurationChanged(newConfig);
            }
        }
    }

    public void init() {
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        mAngelMembersMap = new HashMap<>();
        mManMembersMap = new HashMap<>();
        mLadyMembersMap = new HashMap<>();
        mSystemEventList = new ArrayList<>();
        mStartStatusBasebean = new StatusMatchBean();
        mStartStatusRoomSendBean = mStartStatusBasebean.createBaseChartRoomSendBean();
        mStartStatusTimeStatusResp = new StatusResp();
        mCheckChangedListener = new LiveTypeRadioChangeListener();

        mHandler = new Handler();
        SPACE_TOP = Tools.dip2px(mXqActivity,10);
        mPopupViewMg = new PopupViewMg();

        mRootView = LayoutInflater.from(mXqActivity).inflate(R.layout.layout_chart_ui,null);
        mRecyclerMembers = mRootView.findViewById(R.id.recycle_chart_members);
        mRecyclerSystem = mRootView.findViewById(R.id.recycle_chart_system);
        mBtnExit = mRootView.findViewById(R.id.btn_chart_exit);
        mBtnGift = mRootView.findViewById(R.id.btn_chart_gift);
        mBtnEnd = mRootView.findViewById(R.id.btn_chart_end);
        mTextCountDown = mRootView.findViewById(R.id.text_timer);
        mTextTip = mRootView.findViewById(R.id.text_tip);
        mBtnDisturb = mRootView.findViewById(R.id.btn_disturb);
        mRadioGroupLiveType = mRootView.findViewById(R.id.radioGroup_liveType);
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
                mXqCameraViewMg.setVisible(true);
                mXqCameraViewMg.start();
            }
        });

        mBtnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //结束语音或视频
                stopTiming();
                onOperateEnd(mStartStatusBasebean,mStartStatusRoomSendBean,mStartStatusTimeStatusResp);
            }
        });

        mBtnDisturb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
                if(!userInfoBean.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) return;
                final StatusHelpQuestDisturbBean questDisturbBean = (StatusHelpQuestDisturbBean) mHelpStatusMap
                        .get(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_QUEST_DISTURB);
                if(mCurrentQuestDisturbCount >= DISTURB_COUNT) {
                    Tools.toast(mXqActivity,"您已经插话次数已超，不能插话了",true);
                    return;
                }

                if(mIsDistub) {
                    Tools.toast(mXqActivity,"本次已经申请插话了",true);
                    return;
                }

                //发送插话请求
                JMChartRoomSendBean sendBean = questDisturbBean.getChartSendBeanWillSend(null
                        , BaseStatus.MessageType.TYPE_SEND);
                sendBean.setIndexNext(DataManager.getInstance().getSelfMember().getIndex());
                sendRoomMessage(sendBean);
                mCurrentQuestDisturbCount ++;
                Tools.toast(mXqActivity,"您要求插话",false);
            }
        });

        mRadioGroupLiveType.setOnCheckedChangeListener(mCheckChangedListener);

        initAndSetContentView();
        initAngelManViewInstance();
        initMemberRecyclerView();
        initSystemRecyclerView();
        initOrderStatus();

        JMessageClient.registerEventReceiver(this);
        upDataMembers();
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

    private void initOrderStatus() {
        mOrderStatusMap = new HashMap<>();
        mHelpStatusMap = new HashMap<>();

        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MATCHING,new StatusMatchBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN,new StatusManIntroBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST,new StatusLadyFirstSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_INTRO_LADY,new StatusLadyChartFirstBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST,new StatusManFirstSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE,new StatusManPerformanceBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND,new StatusLadySecondSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND,new StatusLadyChartSecondBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT,new StatusAngelChartBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND,new StatusManSecondSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL,new StatusLadyFinalSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_FIRST,new StatusManFirstQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_FIRST,new StatusLadyFirstQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_SECOND,new StatusManSecondQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_SECOND,new StatusLadySecondQuestionBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL,new StatusManFinalSelectBean());
        mOrderStatusMap.put(JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL,new StatusChartFinalBean());

        //设置流程序列
        Iterator entry = mOrderStatusMap.entrySet().iterator();
        while (entry.hasNext()) {
            Map.Entry en = (Map.Entry) entry.next();
            int key = (int) en.getKey();
            ((BaseStatus)en.getValue()).setmOrder(key);
            ((BaseStatus)en.getValue()).setHandleListener(this);
        }

        //添加辅助状态机
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_CHANGR_LIVETYPE,new StatusHelpChangeLiveTypeBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_DISTURBING,new StatusHelpDoingDisturbBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_QUEST_DISTURB,new StatusHelpQuestDisturbBean());
        mHelpStatusMap.put(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_EXIT_ROOM,new StatusHelpExitBean());

        //设置流程序列
        Iterator helpEntry = mHelpStatusMap.entrySet().iterator();
        while (helpEntry.hasNext()) {
            Map.Entry en = (Map.Entry) helpEntry.next();
            int key = (int) en.getKey();
            ((BaseStatus)en.getValue()).setmOrder(key);
            ((BaseStatus)en.getValue()).setHandleListener(this);
        }
    }

    public XqStatusChartUIViewMg(XqChartActivity xqActivity) {
        this.mXqActivity = xqActivity;
    }

    /**
     * 初始化
     */
    private void initAndSetContentView() {
        //摄像头推送
        mXqCameraViewMg = new XqTxPushViewMg();
        String pushAddress = new String(Base64.decode(DataManager.getInstance().getChartData().getPushAddress().getBytes(),Base64.DEFAULT));
        mXqCameraViewMg.init(mXqActivity,pushAddress);
        Log.i("yy","pushAddress=" + pushAddress);
        mXqCameraViewMg.setVisible(false);

        //摄像头播放
        mXqPlayerViewMg = new XqTxPlayerViewMg();
        String playAddress = new String(Base64.decode(DataManager.getInstance().getChartData().getPlayAddress().getBytes(),Base64.DEFAULT));
        mXqPlayerViewMg.init(mXqActivity,playAddress);
        Log.i("yy","playAddress=" + playAddress);
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
//        mAngelViewInstance.mImgHead.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mBtnEnd.getVisibility() == View.VISIBLE) {
//                    showGuestPopWindow(view, PopupViewMg.Position.RIGHT);
//                    return;
//                }
//
//                final UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
//                if(!userInfoBean.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) return;
//                final StatusHelpQuestDisturbBean questDisturbBean = (StatusHelpQuestDisturbBean) mHelpStatusMap.get(KEY_HELP_QUEST_DISTURB);
//                if(!questDisturbBean.isCanDisturb()) {
//                    Tools.toast(mXqActivity,"您已经插话次数已超，不能插话了",true);
//                    return;
//                }
//
//                if(mIsDistub) {
//                    Tools.toast(mXqActivity,"本次已经申请插话了",true);
//                    return;
//                }
//
//                if(mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_INTRO_LADY
//                        || mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND) {
//                    mPopupViewMg.showAngelPopupView(mXqActivity, mAngelViewInstance.mImgHead, new IPopupAngelListener() {
//                        @Override
//                        public void onDisturb(View view1) {
//                            //发送插话请求
//                            JMChartRoomSendBean sendBean = questDisturbBean.getChartSendBeanWillSend(null
//                                    , BaseStatus.MessageType.TYPE_SEND);
//                            sendBean.setIndexNext(DataManager.getInstance().getSelfMember().getIndex());
//                            sendRoomMessage(sendBean);
//                            Tools.toast(mXqActivity,"您要求插话",false);
//                        }
//                    });
//                }
//            }
//        });

        mManViewInstance.mImgHead = view.findViewById(R.id.img_head_2);
        mManViewInstance.mTxvNickName = view.findViewById(R.id.text_nickName_2);
        mManViewInstance.mTxvNum = view.findViewById(R.id.text_num_2);

        mManViewInstance.mImgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
                if(userInfoBean.getRole_type().equals(Constant.ROLETYPE_GUEST)
                        && userInfoBean.getGender().equals(Constant.GENDER_MAN)
                        && mStartStatusTimeStatusResp.isSelf()) {
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
                StatusHelpChangeLiveTypeBean changeLiveTypeBean = (StatusHelpChangeLiveTypeBean) mHelpStatusMap
                        .get(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_CHANGR_LIVETYPE);
                JMChartRoomSendBean sendBean = changeLiveTypeBean.getChartSendBeanWillSend(null, BaseStatus.MessageType.TYPE_SEND);
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

                        //通知聊天室的其他人,是创建者
                        if(DataManager.getInstance().getSelfMember().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                            norifyRoomExit(JMNormalSendBean.NORMAL_EXIT);
                        }else {
                            for (Member member:DataManager.getInstance().getChartData().getMembers()) {
                                if(!member.getUserInfo().getUser_name().equals(DataManager.getInstance().getUserInfo().getUser_name())) {
                                    JMNormalSendBean normalSendBean = new JMNormalSendBean();
                                    normalSendBean.setCode(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_EXIT_ROOM);
                                    normalSendBean.setTargetUserName(member.getUserInfo().getUser_name());
                                    normalSendBean.setTime(Tools.getCurrentDateTime());
                                    normalSendBean.setRoomId(DataManager.getInstance().getChartData().getRoomId());
                                    normalSendBean.setMsg(DataManager.getInstance().getUserInfo().getNick_name() + "--离开房间");
                                    JMsgSender.sendNomalMessage(normalSendBean);
                                }
                            }
                        }
                        mXqActivity.finish();
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
                normalSendBean.setRoomId(DataManager.getInstance().getChartData().getRoomId());
                normalSendBean.setTargetUserName(member.getUserInfo().getUser_name());
                JMsgSender.sendNomalMessage(normalSendBean);
            }
        }
    }

    /**
     * 信息处理回调
     * @param statusResp
     * @param sendBean
     */
    @Override
    public void onHandleResp(BaseStatus statusInstance,StatusResp statusResp, JMChartRoomSendBean sendBean) {
        switch (statusResp.getMessageType()) {
            case TYPE_SEND:
                addSystemEventAndRefresh(sendBean);
                boolean isTipUpdate = true;
                boolean isSetCurrentStatus = true;

                if(statusResp.isResetLive()) {
                    //是否停止直播
                    resetLiveStatus();
                }
                if(statusResp.isStopTiming()) {
                    //是否停止倒计时
                    stopTiming();
                }
                if(sendBean.getProcessStatus() == mOrderStatusMap.get(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN).getStatus()) {
                    //自我介绍开始则因此离开按键
//                    mBtnExit.setVisibility(View.GONE);
                }

                if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND
                        || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT) {
                    //重置插话标识
                    mIsDistub = false;
                }

                switch (statusResp.getHandleType()) {
                    case HANDLE_MATCH:
                        getChartRoomMembersList(DataManager.getInstance().getChartData().getRoomId());
                        if(statusResp.isLast()) {
                            isTipUpdate = false;
                            BaseStatus baseStatus = mOrderStatusMap.get(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN);
                            JMChartRoomSendBean bean = baseStatus.getChartSendBeanWillSend(sendBean, BaseStatus.MessageType.TYPE_SEND);
                            bean.setIndexNext(baseStatus.getStartIndex());
                            sendRoomMessage(bean);
                        }
                        break;
                    case HANDLE_TIME:
                        startTiming(statusInstance,sendBean,statusResp);
                        break;
                    case HANDLE_SELECT_LADY_FIRST:
                    case HANDLE_SELECT_LADY_SECOND:
                    case HANDLE_SELECT_LADY_FINAL:
                        operate_SelectLady(statusInstance,sendBean,statusResp);
                        break;
                    case HANDLE_SELECT_MAN_FIRST:
                    case HANDLE_SELECT_MAN_SECOND:
                    case HANDLE_SELECT_MAN_FINAL:
                        operate_SelectMan(statusInstance,sendBean,statusResp);
                        break;
                    case HANDLE_FINISH:
                        break;
                    case HANDLE_HELP_QUEST_DISTURB:
                        isTipUpdate = false;
                        isSetCurrentStatus = false;

                        mIsDistub = true;
                        mDistubIndex = sendBean.getIndexSelf();
                        break;
                    case HANDLE_HELP_DOING_DISTURB:
                        isSetCurrentStatus = false;

                        startTiming(statusInstance,sendBean,statusResp);
                        break;
                    case HANDLE_HELP_EXIT:
                        isSetCurrentStatus = false;
                        isTipUpdate = false;

                        getChartRoomMembersList(DataManager.getInstance().getChartData().getRoomId());
                        break;
                    case HANDLE_HELP_CHANGE_LIVETYPE:
                        isTipUpdate = false;
                        isSetCurrentStatus = false;

                        operate_LiveType(statusInstance,sendBean,statusResp);
                        break;
                    default:
                        break;
                }

                if(isTipUpdate) {
                    if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                        mTextTip.setText(sendBean.getMsg());
                    }else {
                        mTextTip.setText(statusInstance.getPublicString());
                    }
                    mTextTip.setVisibility(View.VISIBLE);
                    if(mStartStatusRoomSendBean.getProcessStatus() != sendBean.getProcessStatus()) {
                        Tools.toast(mXqActivity,statusInstance.getPublicString(),false);
                    }
                }

                if(isSetCurrentStatus) {
                    mStartStatusTimeStatusResp = statusResp;
                    mStartStatusRoomSendBean = sendBean;
                    mStartStatusBasebean = statusInstance;
                }

                if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_INTRO_LADY
                        || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND) {
                    if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
                        //显示插话按钮
                        mBtnDisturb.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case TYPE_RESPONSE:
                addSystemEventAndRefresh(sendBean);
                switch (statusResp.getHandleType()) {
                    case HANDLE_SELECT_LADY_FIRST:
                    case HANDLE_SELECT_LADY_SECOND:
                    case HANDLE_SELECT_LADY_FINAL:
                    case HANDLE_SELECT_MAN_FIRST:
                    case HANDLE_SELECT_MAN_SECOND:
                    case HANDLE_SELECT_MAN_FINAL:
                        if(statusResp.isLast()) {
                            if(statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_SELECT_LADY_FIRST
                                    || statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_SELECT_LADY_SECOND) {
                                mCurrentQuestDisturbCount = 0;
                            }

                            if(statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_SELECT_MAN_FINAL) {
                                //流程结束
                                operate_Final(statusInstance,sendBean,statusResp);
                            }else {
                                BaseStatus nextStatus = mOrderStatusMap.get(statusInstance.getmOrder() + 1);
                                if(nextStatus == null) return;
                                JMChartRoomSendBean bean = nextStatus.getChartSendBeanWillSend(sendBean,BaseStatus.MessageType.TYPE_SEND);
                                bean.setIndexNext(nextStatus.getStartIndex());
                                sendRoomMessage(bean);
                            }
                        }

                        if(statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_SELECT_LADY_FINAL) {
                            //女生最后一次选择
                            if(sendBean.isLadySelected()) {
                                mLadySelectedResultList.add(String.valueOf(sendBean.getIndexSelf()));
                            }
                        }
                        break;
                    case HANDLE_HELP_DOING_DISTURB:
                        mIsDistub = false;
                        break;
                }
                break;
            default:
                break;
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

            final ArrayList<String> manSelectedResultList = new ArrayList<>();
            if(mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL
                    || mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                manSelectedResultList.add(String.valueOf(((StatusManFinalSelectBean)mOrderStatusMap
                        .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL)).getSelectLadyIndex()));
            }else {
                manSelectedResultList.add(String.valueOf(((StatusManFirstSelectBean)mOrderStatusMap
                        .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST)).getSelectLadyIndex()));
                manSelectedResultList.add(String.valueOf(((StatusManSecondSelectBean)mOrderStatusMap
                        .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND)).getSelectLadyIndex()));
                manSelectedResultList.add(String.valueOf(((StatusManFinalSelectBean)mOrderStatusMap
                        .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL)).getSelectLadyIndex()));
            }

            if(mIsSelect) {
                if(mStartStatusTimeStatusResp.isManSelect()) {
                    viewInstance.mViewSelect.setVisibility(View.VISIBLE);
                    if(manSelectedResultList.contains(String.valueOf(index))) {
                        viewInstance.mImgSelect.setImageResource(R.drawable.head_select_p);
                    }else {
                        if(mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                            viewInstance.mViewSelect.setVisibility(View.INVISIBLE);
                        }
                        viewInstance.mImgSelect.setImageResource(R.drawable.head_select);
                    }

                    if(manSelectedResultList.contains(String.valueOf(index))) {
                        if(mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_FIRST
                                ||mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_FIRST
                                ||mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_SECOND
                                ||mStartStatusRoomSendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_SECOND) {
                            viewInstance.mImgSelect.setVisibility(View.INVISIBLE);
                        }else {
                            viewInstance.mImgSelect.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }else {
                viewInstance.mViewSelect.setVisibility(View.INVISIBLE);
                viewInstance.mImgSelect.setImageResource(R.drawable.head_select);
            }

            viewInstance.mViewSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mStartStatusTimeStatusResp.isManSelect()) {
                        if(manSelectedResultList.contains(String.valueOf(index))) {
                            Tools.toast(mXqActivity,"您已选择了该位嘉宾",false);
                            return;
                        }

                        viewInstance.mImgSelect.setImageResource(R.drawable.head_select_p);
                        //停止计时
                        mManSelectedIndex = index;
                        stopTiming();
                        mStartStatusTimeStatusResp.setManSelect(false);
                        onOperateEnd(mStartStatusBasebean,mStartStatusRoomSendBean,mStartStatusTimeStatusResp);
                        if(mStartStatusRoomSendBean.getProcessStatus() != JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL) {
                            changeNormalStatus();
                        }
                    }
                }
            });

//            viewInstance.mImgHead.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();
//                    if (userInfoBean.getRole_type().equals(Constant.ROLETYPE_GUEST)
//                            && userInfoBean.getGender().equals(Constant.GENDER_LADY)
//                            && index == DataManager.getInstance().getSelfMember().getIndex()
//                            && mStartStatusTimeStatusResp.isSelf()) {
//                        //女生并且是自己,且在直播中
//                        if(index > (DataManager.getInstance().getChartData().getLimitLady()/2 - 1)) {
//                            showGuestPopWindow(view, PopupViewMg.Position.LEFT);
//                        }else {
//                            showGuestPopWindow(view, PopupViewMg.Position.RIGHT);
//                        }
//                    }
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return (DataManager.getInstance().getChartData().getLimitLady() + 1)/2;
        }

        /**
         * 更改为选择模式
         */
        private void changeSelectStatus() {
            mIsSelect = true;
            notifyDataSetChanged();
        }

        public boolean isSelectStatus() {
            return mIsSelect;
        }

        /**
         * 更改为正常模式
         */
        private void changeNormalStatus() {
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

    private class LiveTypeRadioChangeListener implements RadioGroup.OnCheckedChangeListener {
        private boolean isSendMessage;

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            //发送直播方式更改
            StatusHelpChangeLiveTypeBean changeLiveTypeBean = (StatusHelpChangeLiveTypeBean) mHelpStatusMap
                    .get(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_CHANGR_LIVETYPE);
            JMChartRoomSendBean sendBean = changeLiveTypeBean.getChartSendBeanWillSend(null, BaseStatus.MessageType.TYPE_SEND);
            String liveStr;
            int type;
            if(checkedId == R.id.radio_camera) {
                type = JMChartRoomSendBean.LIVE_CAMERA;
                liveStr = "相机";
            }else if(checkedId == R.id.radio_mic) {
                type = JMChartRoomSendBean.LIVE_MIC;
                liveStr = "音频";
            }else {
                type = JMChartRoomSendBean.LIVE_NONE;
                liveStr = "不使用";
            }

            if(mStartStatusRoomSendBean.getLiveType() == type) {
                return;
            }

            if(!isSendMessage) {
                return;
            }

            sendBean.setLiveType(type);
            sendBean.setMsg(DataManager.getInstance().getUserInfo().getNick_name() + "更改直播方式--" + liveStr);
            sendRoomMessage(sendBean);
            Tools.toast(mXqActivity,"您更改直播方式为--" + liveStr,false);

            mStartStatusRoomSendBean.setLiveType(type);
        }

        public boolean isSendMessage() {
            return isSendMessage;
        }

        public void setSendMessage(boolean sendMessage) {
            isSendMessage = sendMessage;
        }
    }

    /**
     * 添加到系统事件并更新
     * @param bean
     */
    private void addSystemEventAndRefresh(JMChartRoomSendBean bean) {
        mSystemEventList.add(bean);
        updateSystemEvent();
        mRecyclerSystem.scrollToPosition(mSystemAdapter.getItemCount() - 1);
    }

    /**
     * 操作结束
     */
    private void onOperateEnd(BaseStatus baseStatus,JMChartRoomSendBean sendBean,StatusResp statusResp) {
        //恢复初始化
        resetLiveStatus();
        stopTiming();
        if(mMemberAdapter.isSelectStatus()) {
            mMemberAdapter.changeNormalStatus();
        }

        if(mLadySelectDialog != null &&mLadySelectDialog.isShowing()) {
            mLadySelectDialog.dismiss();
        }

        switch (statusResp.getHandleType()) {
            case HANDLE_TIME:
                BaseStatus nextStatus = null;
                if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_INTRO_LADY
                        || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND) {
                    if(mIsDistub) {
                        //已经请求插话
                        nextStatus = mHelpStatusMap.get(JMChartRoomSendBean.CHART_HELP_STATUS_ANGEL_DISTURBING);
                        JMChartRoomSendBean bean = nextStatus.getChartSendBeanWillSend(sendBean, BaseStatus.MessageType.TYPE_SEND);
                        bean.setIndexNext(mDistubIndex);
                        sendRoomMessage(bean);
                        return;
                    }
                }

                if(statusResp.isLast()) {
                    nextStatus = mOrderStatusMap.get(baseStatus.getmOrder() + 1);
                }else {
                    nextStatus = mOrderStatusMap.get(baseStatus.getmOrder());
                }
                if(nextStatus != null) {
                    JMChartRoomSendBean bean = nextStatus.getChartSendBeanWillSend(sendBean, BaseStatus.MessageType.TYPE_SEND);
                    if(bean == null) return;
                    bean.setMessageType(BaseStatus.MessageType.TYPE_SEND);

                    int startIndex = nextStatus.getStartIndex();
                    int nextIndex = nextStatus.getNextIndex(sendBean);

                    int firstLadyIndex = ((StatusManFirstSelectBean) mOrderStatusMap
                            .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST)).getSelectLadyIndex();
                    int secondLadyIndex = ((StatusManSecondSelectBean) mOrderStatusMap
                            .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND)).getSelectLadyIndex();
                    if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_FIRST
                            || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN_SECOND) {
                        if(statusResp.isLast()) {
                            startIndex = firstLadyIndex;
                        }
                    }

                    if(sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_FIRST
                            || sendBean.getProcessStatus() == JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY_SECOND) {
                        if(!statusResp.isLast()) {
                            nextIndex = secondLadyIndex;
                        }
                    }
                    if(statusResp.isLast()) {
                        bean.setIndexNext(startIndex);
                    }else {
                        bean.setIndexNext(nextIndex);
                    }
                    bean.setProcessStatus(nextStatus.getStatus());
                    sendRoomMessage(bean);
                }
                break;

            case HANDLE_SELECT_LADY_FIRST:
            case HANDLE_SELECT_LADY_SECOND:
            case HANDLE_SELECT_LADY_FINAL:
            case HANDLE_SELECT_MAN_FIRST:
            case HANDLE_SELECT_MAN_SECOND:
            case HANDLE_SELECT_MAN_FINAL:
                JMChartRoomSendBean bean = baseStatus.getChartSendBeanWillSend(sendBean, BaseStatus.MessageType.TYPE_RESPONSE);
                if(bean.getGender().equals(Constant.GENDER_LADY)) {
                    bean.setLadySelected(mLadySelecteResult);
                }else if (bean.getGender().equals(Constant.GENDER_MAN)) {
                    bean.setManSelects(String.valueOf(mManSelectedIndex));
                }
                sendRoomMessage(bean);
                break;

            case HANDLE_HELP_DOING_DISTURB:
                if(statusResp.isLast()) {
                    JMChartRoomSendBean bean1 = mStartStatusBasebean.getChartSendBeanWillSend(mStartStatusRoomSendBean,
                            BaseStatus.MessageType.TYPE_SEND);
                    bean1.setIndexNext(mStartStatusBasebean.getNextIndex(mStartStatusRoomSendBean));
                    sendRoomMessage(bean1);
                }
                break;

            default:
                break;
        }
    }


    /***********************************各个环节的操作**************************************/
    /**
     * 女生选择
     * @param bean
     * @param flags
     */
    private void operate_SelectLady(BaseStatus baseStatus,JMChartRoomSendBean bean,StatusResp flags) {
        if(!flags.isSelf()) return;
        //先匹配是否为自己
        if(mLadySelecteResult) {//第一次是接受的情况
            startTiming(baseStatus,bean,flags);
            mLadySelectDialog = createLadySelectDialog(bean);
            mLadySelectDialog.show();
        }else if(!mLadySelecteResult){//第一次就拒绝的,直接返回回复
            //发送回应
            onOperateEnd(baseStatus,bean,flags);
        }
    }

    /**
     * 男生选择
     * @param bean
     * @param flags
     */
    private void operate_SelectMan(BaseStatus baseStatus,JMChartRoomSendBean bean,StatusResp flags) {
        Log.e("yy","operate_SelectMan=" + flags.isSelf());
        if(!flags.isSelf()) {
            return;
        }

        //成员头像，切换为选择状态
        startTiming(baseStatus,bean,flags);
        mMemberAdapter.changeSelectStatus();
    }

    /**
     * 流程结束
     * @param baseStatus
     * @param sendBean
     * @param statusResp
     */
    private void operate_Final(BaseStatus baseStatus,JMChartRoomSendBean sendBean,StatusResp statusResp) {
        String finalSelectLady = String.valueOf(((StatusManFinalSelectBean)mOrderStatusMap
                .get(JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL)).getSelectLadyIndex());

        String text = "非常遗憾，匹配失败";
        if(mLadySelectedResultList.contains(finalSelectLady)) {
            text = "恭喜匹配成功";
            //更新
            mMemberAdapter.changeSelectStatus();
        }
        Tools.toast(mXqActivity,text,true);
        //显示离开按钮
        mBtnExit.setVisibility(View.VISIBLE);
        //显示在系统事件中
        JMChartRoomSendBean bean = mOrderStatusMap.get(JMChartRoomSendBean.CHART_STATUS_CHAT_FINAL)
                .getChartSendBeanWillSend(null, BaseStatus.MessageType.TYPE_SEND);
        bean.setMsg(text);
        handleStatusBean(bean);
    }

    /**
     * 直播方式更改
     * @param baseStatus
     * @param sendBean
     * @param statusResp
     */
    private void operate_LiveType(BaseStatus baseStatus,JMChartRoomSendBean sendBean,StatusResp statusResp) {
        if(sendBean.getLiveType() == JMChartRoomSendBean.LIVE_MIC) {
            mXqCameraViewMg.setVisible(false);
            mXqPlayerViewMg.setVisible(false);
        }else if(sendBean.getLiveType() == JMChartRoomSendBean.LIVE_CAMERA) {
            if(mBtnEnd.getVisibility() == View.VISIBLE) {
                mXqCameraViewMg.setVisible(true);
                mXqPlayerViewMg.setVisible(false);
            }else {
                mXqPlayerViewMg.setVisible(true);
                mXqCameraViewMg.setVisible(false);
            }
        }else if(sendBean.getLiveType() == JMChartRoomSendBean.LIVE_NONE){
            mXqCameraViewMg.setVisible(false);
            mXqPlayerViewMg.setVisible(false);
            mXqCameraViewMg.setMute(true);
        }
    }

    private AlertDialog createLadySelectDialog(final JMChartRoomSendBean bean) {
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
                        dialogInterface.dismiss();
                        stopTiming();
                        onOperateEnd(mStartStatusBasebean,mStartStatusRoomSendBean,mStartStatusTimeStatusResp);
                    }
                })
                .create();
        return dialog;
    }

    /**
     * 开始倒计时
     * @param bean
     */
    private void startTiming(final BaseStatus baseStatus,final JMChartRoomSendBean bean, final StatusResp statusResp) {
        if(baseStatus == null) return;
        mTextCountDown.setVisibility(View.VISIBLE);

        if(statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_TIME
                || statusResp.getHandleType() == BaseStatus.HandleType.HANDLE_HELP_DOING_DISTURB) {
            setLiveStatus(bean,statusResp.isSelf());
            if(statusResp.isSelf()) {
                mBtnEnd.setVisibility(View.VISIBLE);
                //发送回应
                JMChartRoomSendBean responseBean = baseStatus.getChartSendBeanWillSend(bean, BaseStatus.MessageType.TYPE_RESPONSE);
                sendRoomMessage(responseBean);

                //显示切换直播方式按钮
                mCheckChangedListener.setSendMessage(true);
                mRadioGroupLiveType.setVisibility(View.VISIBLE);
            }
        }
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                timeCount++;
                int time = 0;
                time = baseStatus.getLiveTimeCount() - timeCount;

                if(time > 0) {
                    mTextCountDown.setText(String.valueOf(time) + "s");
                    mTextCountDown.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(this,1000);//下一次循环
                }else {
                    //自行操作结束
                    onOperateEnd(baseStatus,bean,statusResp);
                }
            }
        };
        mHandler.postDelayed(mTimeRunnable,1000);
    }

    /**
     * 停止倒计时
     */
    private void stopTiming() {
        if(mTimeRunnable != null) {
            mHandler.removeCallbacks(mTimeRunnable);
        }
        timeCount = 0;
        mTextCountDown.setVisibility(View.INVISIBLE);
        mTextCountDown.setText("");
        mBtnEnd.setVisibility(View.INVISIBLE);
        mBtnDisturb.setVisibility(View.GONE);
        mTextTip.setVisibility(View.INVISIBLE);
        mRadioGroupLiveType.setVisibility(View.GONE);
    }

    /**
     * StatusBean,使能够处理消息
     */
    private void handleStatusBean(JMChartRoomSendBean chartRoomSendBean) {
        Iterator iterator = mOrderStatusMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer,BaseStatus> entry = (Map.Entry<Integer, BaseStatus>) iterator.next();
            if(entry.getValue() != null) {
                entry.getValue().handlerRoomChart(chartRoomSendBean);
            }
        }

        Iterator helpIterator = mHelpStatusMap.entrySet().iterator();
        while (helpIterator.hasNext()) {
            Map.Entry<Integer,BaseStatus> entry = (Map.Entry<Integer, BaseStatus>) helpIterator.next();
            if(entry.getValue() != null) {
                entry.getValue().handlerRoomChart(chartRoomSendBean);
            }
        }
    }

    /**
     *发送聊天室信息
     * @param chartRoomSendBean
     */
    private void sendRoomMessage(JMChartRoomSendBean chartRoomSendBean) {
        JMsgSender.sendRoomMessage(chartRoomSendBean);
        handleStatusBean(chartRoomSendBean);
    }

    /**
     * 更改直播方式的处理
     * @param chartRoomSendBean
     * @param isSelf
     */
    private void setLiveStatus(JMChartRoomSendBean chartRoomSendBean,boolean isSelf) {
        mCheckChangedListener.setSendMessage(false);//不发送改变直播方式的消息
        switch (chartRoomSendBean.getLiveType()) {
            case JMChartRoomSendBean.LIVE_MIC:
                if(isSelf) {
                    mXqCameraViewMg.start();
                    mXqCameraViewMg.setVisible(false);
                }else {
                    mXqPlayerViewMg.start();
                    mXqPlayerViewMg.setVisible(false);
                }
                mRadioGroupLiveType.check(R.id.radio_mic);
                break;
            case JMChartRoomSendBean.LIVE_CAMERA:
                if(isSelf) {
                    mXqCameraViewMg.setVisible(true);
                    mXqCameraViewMg.start();
                }else {
                    mXqPlayerViewMg.start();
                    mXqPlayerViewMg.setVisible(true);
                }
                mRadioGroupLiveType.check(R.id.radio_camera);
                break;
            case JMChartRoomSendBean.LIVE_NONE:
                mXqCameraViewMg.setVisible(false);
                mXqPlayerViewMg.setVisible(false);
                mXqCameraViewMg.stop();
                mXqPlayerViewMg.stop();
                mRadioGroupLiveType.clearCheck();
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
            if(chartRoomSendBean.getRoomId() != DataManager.getInstance().getChartData().getRoomId())
            {
                return;
            }
            handleStatusBean(chartRoomSendBean);

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
        if(normalSendBean.getRoomId() != DataManager.getInstance().getChartData().getRoomId())
        {
            return;
        }

        if(normalSendBean.getCode() == JMNormalSendBean.NORMAL_EXIT) {//离开
            Tools.toast(mXqActivity,"房间被解散",true);
            mXqActivity.finish();
        }else if(normalSendBean.getCode() == JMChartRoomSendBean.CHART_HELP_STATUS_CHART_EXIT_ROOM) {
            JMChartRoomSendBean roomSendBean = new JMChartRoomSendBean();
            roomSendBean.setProcessStatus(normalSendBean.getCode());
            roomSendBean.setMsg(normalSendBean.getMsg());
            roomSendBean.setMessageType(BaseStatus.MessageType.TYPE_SEND);
            roomSendBean.setTime(normalSendBean.getTime());
            mHelpStatusMap.get(JMChartRoomSendBean.CHART_HELP_STATUS_CHART_EXIT_ROOM).handlerRoomChart(roomSendBean);
        }
    }
}
