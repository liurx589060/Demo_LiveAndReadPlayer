package com.lrxliveandreadplayer.demo.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.lrxliveandreadplayer.demo.utils.Constant;
import com.lrxliveandreadplayer.demo.utils.Tools;
import com.lrxliveandreadplayer.demo.utils.XqErrorCode;

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

    private RequestApi mApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xq_main);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);

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

        Map<String,Object> params = new HashMap<>();
        params.put("userName",userInfo.getUser_name());
        params.put("gender",userInfo.getGender());
        params.put("level",userInfo.getLevel());
        params.put("limitLevel",userInfo.getLimitLevel());
        params.put("limitLady",userInfo.getLimitLady());
        params.put("limitMan",userInfo.getLimitMan());
        params.put("limitAngel",userInfo.getLimitAngel());

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
        JMChartRoomSendBean bean = new JMChartRoomSendBean();
        Data data = DataManager.getInstance().getChartData();
        UserInfoBean selfInfo = DataManager.getInstance().getUserInfo();
        bean.setGender(selfInfo.getGender());
        bean.setCurrentCount(data.getMembers().size());
        bean.setLimitCount(data.getLimitAngel() + data.getLimitMan() + data.getLimitLady());
        bean.setIndexSelf(DataManager.getInstance().getSelfMember().getIndex());
        bean.setProcessStatus(JMChartRoomSendBean.CHART_STATUS_MATCHING);
        bean.setRoomId(data.getRoomId());
        bean.setTime(Tools.getCurrentDateTime());
        bean.setMsg(selfInfo.getNick_name() + "进入房间");
        bean.setUserName(selfInfo.getUser_name());
        bean.setUpdateMembers(isUpdateMembers);
        bean.setMessageType(JMSendFlags.MessageType.TYPE_SEND);

        JMsgSender.sendRoomMessage(bean);
    }
}