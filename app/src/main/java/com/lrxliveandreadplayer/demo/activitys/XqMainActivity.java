package com.lrxliveandreadplayer.demo.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.beans.jmessage.Data;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartResp;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMChartRoomSendBean;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMSendFlags;
import com.lrxliveandreadplayer.demo.beans.jmessage.Member;
import com.lrxliveandreadplayer.demo.beans.jmessage.UserInfo;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;
import com.lrxliveandreadplayer.demo.jmessage.JMsgSender;
import com.lrxliveandreadplayer.demo.manager.DataManager;
import com.lrxliveandreadplayer.demo.network.NetWorkMg;
import com.lrxliveandreadplayer.demo.network.RequestApi;
import com.lrxliveandreadplayer.demo.status.BaseStatus;
import com.lrxliveandreadplayer.demo.status.statusBeans.MatchBean;
import com.lrxliveandreadplayer.demo.utils.Constant;
import com.lrxliveandreadplayer.demo.utils.Tools;
import com.lrxliveandreadplayer.demo.utils.XqErrorCode;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2018/5/22.
 */

public class XqMainActivity extends Activity {
    @BindView(R.id.btn_angel) Button mBtnAngel;
    @BindView(R.id.btn_guest) Button mBtnGuest;
    @BindView(R.id.edit_limitLady)
    EditText mEditLimitLady;
    @BindView(R.id.radioGroup_1)
    RadioGroup mRadioGroup;

    private RequestApi mApi;

    private String mTXPushAddress = "";
    private String mTXPlayerAddress = "";
    private int mPushAddressType= 0;
    private MatchBean mMatch = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xq_main);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        mMatch = new MatchBean();

        mBtnAngel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoBean bean = new UserInfoBean();
                bean.setUser_name(DataManager.getInstance().getUserInfo().getUser_name());
                bean.setRole_type(DataManager.getInstance().getUserInfo().getRole_type());
                bean.setGender(DataManager.getInstance().getUserInfo().getGender());
                bean.setLevel(DataManager.getInstance().getUserInfo().getLevel());
                try {
                    bean.setLimitLady(Integer.valueOf(mEditLimitLady.getText().toString()));
                }catch (Exception e) {
                    Log.e("yy",e.toString());
                }
                bean.setLimitLevel(-1);
                createChartRoom(bean);
            }
        });

        mBtnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinChartRoom();
//                Intent intent = new Intent(XqMainActivity.this,XqChartActivity.class);
//                startActivity(intent);
            }
        });

        mRadioGroup = findViewById(R.id.radioGroup_1);
        mRadioGroup.check(R.id.radio_local);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_local) {
                    mPushAddressType = 0;
                }else if(checkedId == R.id.radio_tx) {
                    mPushAddressType = 1;
                }
            }
        });

        if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
            mRadioGroup.setVisibility(View.VISIBLE);
        }
    }

    private void createChartRoom(UserInfoBean userInfo) {
        if(userInfo == null){
            Log.e("yy","createChartRoom UserInfoBean=null");
            return;
        }
        if(!userInfo.getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
            Tools.toast(getApplicationContext(),"您不是爱心大使",true);
            return;
        }

        setLiveAddress();

        Map<String,Object> params = new HashMap<>();
        params.put("userName",userInfo.getUser_name());
        params.put("gender",userInfo.getGender());
        params.put("level",userInfo.getLevel());
        params.put("limitLevel",userInfo.getLimitLevel());
        params.put("limitLady",userInfo.getLimitLady());
        params.put("limitMan",userInfo.getLimitMan());
        params.put("limitAngel",userInfo.getLimitAngel());
        params.put("pushAddress", Base64.encodeToString(mTXPushAddress.getBytes(),Base64.DEFAULT));
        params.put("playAddress",Base64.encodeToString(mTXPlayerAddress.getBytes(),Base64.DEFAULT));

        mApi.createChartRoom(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if(jmChartResp == null) {
                            Log.e("yy","jmChartResp is null");
                            Tools.toast(getApplicationContext(),"jmChartResp is null",true);
                            return;
                        }
                        if(jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("yy",jmChartResp.getMsg());
                            Tools.toast(getApplicationContext(),jmChartResp.getMsg(),true);
                            return;
                        }
                        DataManager.getInstance().setChartData(jmChartResp.getData());
                        Intent intent = new Intent(XqMainActivity.this,XqChartActivity.class);
                        startActivity(intent);
                    }}, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("yy",throwable.toString());
                        Tools.toast(getApplicationContext(),throwable.toString(),true);
                    }
                });
    }

    private void joinChartRoom() {
        UserInfoBean userInfo = DataManager.getInstance().getUserInfo();
        if(!userInfo.getRole_type().equals(Constant.ROLETYPE_GUEST)) {
            Tools.toast(getApplicationContext(),"您不是Guest",true);
            return;
        }

        Map<String,Object> params = new HashMap<>();
        params.put("userName",userInfo.getUser_name());
        params.put("gender",userInfo.getGender());
        params.put("level",userInfo.getLevel());
        params.put("roleType",userInfo.getRole_type());
        mApi.joinChartRoom(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if(jmChartResp == null) {
                            Log.e("yy","jmChartResp is null");
                            Tools.toast(getApplicationContext(),"jmChartResp is null",true);
                            return;
                        }
                        if(jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e("yy",jmChartResp.getMsg());
                            Tools.toast(getApplicationContext(),jmChartResp.getMsg(),true);
                            return;
                        }
                        DataManager.getInstance().setChartData(jmChartResp.getData());
                        Intent intent = new Intent(XqMainActivity.this,XqChartActivity.class);
                        startActivity(intent);
                        //发送聊天室信息
                        sendChartRoomMessage(true);
                    }}, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("yy",throwable.toString());
                        Tools.toast(getApplicationContext(),throwable.toString(),true);
                    }
                });
    }

    private void sendChartRoomMessage(boolean isUpdateMembers) {
        //发送聊天室信息
//        JMChartRoomSendBean bean = new JMChartRoomSendBean();
//        Data data = DataManager.getInstance().getChartData();
//        UserInfoBean selfInfo = DataManager.getInstance().getUserInfo();
//        bean.setGender(selfInfo.getGender());
//        bean.setCurrentCount(data.getMembers().size());
//        bean.setLimitCount(data.getLimitAngel() + data.getLimitMan() + data.getLimitLady());
//        bean.setIndexSelf(DataManager.getInstance().getSelfMember().getIndex());
//        bean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_MATCHING);
//        bean.setRoomId(data.getRoomId());
//        bean.setTime(Tools.getCurrentDateTime());
//        bean.setMsg(selfInfo.getNick_name() + "进入房间");
//        bean.setUserName(selfInfo.getUser_name());
//        bean.setUpdateMembers(isUpdateMembers);
//        bean.setMessageType(BaseStatus.MessageType.TYPE_SEND);

        Data data = DataManager.getInstance().getChartData();
        UserInfoBean selfInfo = DataManager.getInstance().getUserInfo();
        JMChartRoomSendBean bean = mMatch.createBaseChartRoomSendBean();
        bean.setCurrentCount(data.getMembers().size());
        bean.setProcessStatus(mMatch.getStatus());
        bean.setMessageType(BaseStatus.MessageType.TYPE_SEND);
        bean.setUpdateMembers(isUpdateMembers);
        bean.setMsg(selfInfo.getNick_name() + "进入房间");

        JMsgSender.sendRoomMessage(bean);
    }

    /*
	 * KEY+ stream_id + txTime
	 */
    private void setLiveAddress() {
        if(mPushAddressType == 0) {
            //本地
            mTXPushAddress = NetWorkMg.getCameraUrl();
            mTXPlayerAddress = mTXPushAddress;
        }else if(mPushAddressType == 1){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR,24);
            long txTime = calendar.getTimeInMillis()/1000;
            String input = new StringBuilder().
                    append(Constant.TX_LIVE_PUSH_KEY).
                    append(Constant.TX_LIVE_BIZID + "_"
                            + String.valueOf(DataManager.getInstance().getChartData().getRoomId())).
                    append(Long.toHexString(txTime).toUpperCase()).toString();
            Log.e("yy",input);

            String txSecret = null;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                txSecret  = byteArrayToHexString(
                        messageDigest.digest(input.getBytes("UTF-8")));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.e("yy",e.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e("yy",e.toString());
            }

            mTXPlayerAddress = "rtmp://" + Constant.TX_LIVE_BIZID + ".liveplay.myqcloud.com/live/"
                    + Constant.TX_LIVE_BIZID + "_" + DataManager.getInstance().getChartData().getRoomId();
            Log.e("yy","TXPlayerAddress=" + mTXPlayerAddress);
            String ip = "rtmp://" + Constant.TX_LIVE_BIZID + ".livepush.myqcloud.com/live/"
                    + Constant.TX_LIVE_BIZID + "_" + DataManager.getInstance().getChartData().getRoomId()
                    + "?bizid=" + Constant.TX_LIVE_BIZID;
            mTXPushAddress = new StringBuilder().
                    append(ip).
                    append("&").
                    append("txSecret=").
                    append(txSecret).
                    append("&").
                    append("txTime=").
                    append(Long.toHexString(txTime).toUpperCase()).
                    toString();
            Log.e("yy","TXPushAddress=" + mTXPushAddress);
        }
    }

    private String byteArrayToHexString(byte[] data) {
        char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] out = new char[data.length << 1];

        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }
}