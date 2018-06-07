package com.lrxliveandreadplayer.demo.manager;

import android.content.DialogInterface;
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
import com.lrxliveandreadplayer.demo.beans.jmessage.UserInfo;
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
    private final int QUESTION_COUNT = 2;//两轮问答
    private final int mCountDownTime_intro_man = 180;
    private final int mCountDownTime_ladySelect_first = 10;
    private final int mCountDownTime_intro_lady = 120;
    private final int mCountDownTime_peformance_man = 180;
    private final int mCountDownTime_chart_angel = 180;
    private final int mCountDownTime_ManSelect_first = 20;
    private final int mCountDownTime_Question_man = 60;
    private final int mCountDownTime_Question_lady = 120;
    private final int mPrepareTime = 5;
    private final int mCountManSelect = 1;
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

    private int mStartOrderIndex_intro_lady = 0;
    private int mStartOrderIndex_intro_man = 0;
    private int mProgressStatus = -1;
    private int mQuestionNum = 0;
    private AlertDialog mLadySelectDialog;
    private boolean mLadySelecteResult = true; //默认为都接受
    private ArrayList<String> mManSelectedResultList = new ArrayList<>();

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
            setData(holder.mRight,position,5);
        }

        public void setData(final ViewInstance viewInstance, int position, int offset) {
            viewInstance.mTxvNickName.setTextSize(Tools.dip2px(mXqActivity,3));
            viewInstance.mTxvNum.setTextSize(Tools.dip2px(mXqActivity,4));

            final int index = offset + position;
            UserInfoBean bean = mLadyMembersMap.get(index);
            if(bean == null) {
                viewInstance.setVisible(false);
                return;
            }else {
                viewInstance.setVisible(true);
            }

            viewInstance.mTxvNum.setText(String.valueOf(index + 1));
            viewInstance.mTxvNickName.setText(bean.getNick_name());

            Glide.with(mXqActivity)
                    .load(bean.getHead_image())
                    .placeholder(R.drawable.ic_launcher)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(viewInstance.mImgHead);

            if(mIsSelect) {
                if(mProgressStatus == JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST) {//当为第一次选择时
                    viewInstance.mViewSelect.setVisibility(View.VISIBLE);
                    viewInstance.mImgSelect.setImageResource(R.drawable.head_select_p);
                }
            }else {
                viewInstance.mViewSelect.setVisibility(View.INVISIBLE);
                viewInstance.mImgSelect.setImageResource(R.drawable.head_select);
            }

            viewInstance.mViewSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mManSelectedResultList.add(String.valueOf(index));
                    viewInstance.mImgSelect.setImageResource(R.drawable.head_select_p);
                    //停止计时
                    stopTiming();
                    changeNormalStatus();
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
                    int delta = parentHeight - top;
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
                case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节，男生
                case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY://问答环节，女生
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
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://男生最终选择
                    if(bean.getProcessStatus() != mProgressStatus) {
                        Tools.toast(mXqActivity,bean.getMsg(),false);
                        addSystemEventAndRefresh(bean);
                    }
                    break;
            }

        }else if (flags.getMessageType() == JMSendFlags.MessageType.TYPE_RESPONSE) {//回复形式
            switch (bean.getProcessStatus()) {
                case JMChartRoomSendBean.CHART_STATUS_MATCHING://匹配
                    break;
                case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN://男方自我介绍
                case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY://女生自我介绍
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择环节
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST://男生第一次选择环节
                case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND://女生第二次选择环节
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND://男生第二次选择环节
                case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
                case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL://女生最终选择
                    addSystemEventAndRefresh(bean);
                    break;
            }
        }

        mProgressStatus = bean.getProcessStatus();
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
            switch (bean.getProcessStatus()) {
                case JMChartRoomSendBean.CHART_STATUS_MATCHING://匹配
                    if(flags.isLast()) {
                        //发送下一轮，男方自我介绍环节
                        sendBean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_INTRO_MAN);
                        sendBean.setIndexNext(mStartOrderIndex_intro_man);
                        sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
                        sendBean.setMsg("进入第一环节，男生自我介绍");
                        JMsgSender.sendRoomMessage(sendBean);
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
                    operate_Timing(bean,flags);
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
            }
        }
    }

    /**
     * 操作结束
     * @param bean
     * @param flags
     */
    private void onOperateEnd(JMChartRoomSendBean bean,JMSendFlags flags) {
        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_INTRO_MAN://男生自我介绍
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY://女生自我介绍
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE://男生才艺表演
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND://女生第二次谈话
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT://爱心大使有话说
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_MAN://问答环节，男生
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
        }else if(!mLadySelecteResult){//第一次就拒绝的,直接返回回复
            //发送回应
            JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
            sendBean.setProcessStatus(bean.getProcessStatus());
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_RESPONSE);
            sendBean.setMsg(userInfo.getNick_name() + "--已做出选择");
            JMsgSender.sendRoomMessage(sendBean);
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
                nextProgress = JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT;
                msg = "爱心大使有话说";
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL:
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
            JMsgSender.sendRoomMessage(sendBean);
        }
    }

    /**
     * 男生选择回复
     * @param bean
     * @param flags
     */
    private void operate_SelectMan_Response(JMChartRoomSendBean bean,JMSendFlags flags) {
        int nextProgress = -1;
        int nextIndex = mStartOrderIndex_intro_man;
        String msg = "";
        switch (bean.getProcessStatus()){
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE;
                msg = "进入第四个环节，男生才艺表演";
                break;
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL;
                msg = "女生最终选择";
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
            JMsgSender.sendRoomMessage(sendBean);
        }
    }

    /**
     * 男生最终选择
     * @param bean
     * @param flags
     */
    private void operate_SelectMan_Final_Response(JMChartRoomSendBean bean,JMSendFlags flags) {
        89
    }

    /**
     * 倒计时操作
     * @param bean
     * @param flags
     */
    private void operate_Timing(JMChartRoomSendBean bean,JMSendFlags flags) {
        UserInfoBean userInfo = DataManager.getInstance().getUserInfo();
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
        }

        //先匹配是否为自己
        if(checkIsSelf(bean,flags)) {
            //发送回应
            JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
            sendBean.setProcessStatus(bean.getProcessStatus());
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_RESPONSE);
            sendBean.setLiveType(JMChartRoomSendBean.LIVE_MIC);//默认语音
            sendBean.setMsg(msg);
            JMsgSender.sendRoomMessage(sendBean);
            //倒计时
            startTiming(bean,flags);
            //进行自我介绍
            Tools.toast(mXqActivity,msg,true);
        }
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
                msg = "进入第一环节，女生自我介绍";
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST;
                msg = "进入第三轮，男生第一次选择";
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_MAN_PERFORMANCE:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND;
                msg = "进入第四环节，女生第二次选择";
                break;
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND:
                nextProgress = JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND;
                startIndex = mStartOrderIndex_intro_lady;
                msg = "进入第五环节，女生第二次谈话";
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
                mQuestionNum++;
                msg = "问答环节，女生";
                break;
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY:
                if(mQuestionNum < 2) {
                    nextProgress = JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY;
                    msg = "问答环节";
                }else {
                    nextProgress = JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL;
                    msg = "男生最终选择";
                }
                startIndex = mStartOrderIndex_intro_man;
                nextIndex = Integer.valueOf(mManSelectedResultList.get(1)).intValue();
                break;
        }

        if(flags.isLast()) {
            //发送下一轮，请男生选择心动女生
            sendBean.setProcessStatus(nextProgress);
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
            sendBean.setMsg(msg);
            sendBean.setIndexNext(startIndex);
            JMsgSender.sendRoomMessage(sendBean);
        }else {
            sendBean.setProcessStatus(bean.getProcessStatus());
            sendBean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);
            sendBean.setIndexNext(nextIndex);
            JMsgSender.sendRoomMessage(sendBean);
        }
    }

    /**
     * 时间结束做出回应
     * @param bean
     * @param flags
     */
    private void operate_Response_End(JMChartRoomSendBean bean,JMSendFlags flags) {
        UserInfoBean userInfoBean = DataManager.getInstance().getUserInfo();

        switch (bean.getProcessStatus()) {
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FIRST://女生第一次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND://女生第二次选择
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_FINAL://女生最终选择
                //时间到，默认为接收
                if(!mLadySelecteResult) {
                    mLadySelecteResult = true;
                }
                mLadySelectDialog.dismiss();
                break;
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FIRST:
                //默认选择第一个
                mManSelectedResultList.add(String.valueOf(0));
                mMemberAdapter.changeNormalStatus();
                break;
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_SECOND:
                //默认选择第一个
                for(int i = 0 ; i < mManMembersMap.size();i++) {
                    if(!mManSelectedResultList.contains(i)) {
                        mManSelectedResultList.add(String.valueOf(i));
                        break;
                    }
                }
                mMemberAdapter.changeNormalStatus();
                break;
            case JMChartRoomSendBean.CHART_STATUS_MAN_SELECT_FINAL:
                //只剩最后一个，保留第一个元素
                mManSelectedResultList.remove(1);
                mMemberAdapter.changeNormalStatus();
                break;
        }
        //发送回应
        JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
        sendBean.setProcessStatus(bean.getProcessStatus());
        sendBean.setMessageType(JMSendFlags.MessageType.TYPE_RESPONSE);
        sendBean.setMsg(userInfoBean.getNick_name() + "--已做出选择");
        JMsgSender.sendRoomMessage(sendBean);
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
                        dialogInterface.dismiss();
                        stopTiming();
                        //发送回应
                        JMChartRoomSendBean sendBean = mChartRoomController.createBaseSendbeanForExtent();
                        sendBean.setProcessStatus(bean.getProcessStatus());
                        sendBean.setMessageType(JMSendFlags.MessageType.TYPE_RESPONSE);
                        sendBean.setMsg(userInfo.getNick_name() + "--已做出选择");
                        JMsgSender.sendRoomMessage(sendBean);
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
            case JMChartRoomSendBean.CHART_STATUS_LADY_SELECT_SECOND:
                //特殊情况，置为-1，屏蔽位置的限制
                selfIndex = -1;
                break;
            case JMChartRoomSendBean.CHART_STATUS_INTRO_LADY:
            case JMChartRoomSendBean.CHART_STATUS_LADY_CHAT_SECOND:
            case JMChartRoomSendBean.CHART_STATUS_CHAT_QUESTION_LADY:
                nextIndex = bean.getIndexNext()%data.getLimitLady();
                break;
            case JMChartRoomSendBean.CHART_STATUS_ANGEL_CHAT:
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
        if(mTimeRunnable == null) {
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
                    }

                    if(time > 0) {
                        mTextCountDown.setText(String.valueOf(time));
                        mTextCountDown.setVisibility(View.VISIBLE);
                        mHandler.postDelayed(this,1000);//下一次循环
                    }else {
                        //自行操作结束
                        onOperateEnd(bean,flags);
                        mTextCountDown.setVisibility(View.INVISIBLE);
                        stopTiming();//停止循环
                    }
                }
            };
        }
        stopTiming();
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
