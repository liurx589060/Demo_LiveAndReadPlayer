package com.lrxliveandreadplayer.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.activitys.ChartGroupActivity;
import com.lrxliveandreadplayer.demo.activitys.IjkLivePlayer;
import com.lrxliveandreadplayer.demo.activitys.LiveActivity;
import com.lrxliveandreadplayer.demo.beans.jmessage.JMMemeberBean;
import com.lrxliveandreadplayer.demo.factory.DialogFactory;
import com.lrxliveandreadplayer.demo.network.NetWorkMg;
import com.lrxliveandreadplayer.demo.network.RequestApi;
import com.lrxliveandreadplayer.demo.utils.Constant;

import java.util.List;

import cn.jiguang.api.JCoreInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends Activity {
    private Button mBtnIjk;
    private Button mBtnLive;
    private Button mBtnMatch;
    private TextView mTvxUserName;
    private EditText mEditIpAdress;
    private RadioButton mRadioMan;
    private RadioButton mRadioLady;
    private Button mBtnIpAdressSave;
    private RadioGroup mRadioGroup;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        showLoginDialog(this);
    }

    private void init() {
        mBtnIjk = findViewById(R.id.btn_ijkPlayer);
        mBtnLive = findViewById(R.id.btn_live);
        mBtnMatch = findViewById(R.id.btn_match);
        mTvxUserName = findViewById(R.id.txv_userName);
        mEditIpAdress = findViewById(R.id.edit_ipAddress);
        mRadioMan = findViewById(R.id.radio_man);
        mRadioLady = findViewById(R.id.radio_lady);
        mBtnIpAdressSave = findViewById(R.id.btn_ipAdress);
        mRadioGroup = findViewById(R.id.radio_group);
        mBtnIjk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,IjkLivePlayer.class);
                startActivity(intent);
            }
        });

        mBtnLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LiveActivity.class);
                startActivity(intent);
            }
        });

        mBtnMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ChartGroupActivity.class);
                startActivity(intent);
            }
        });

        loadingDialog = DialogFactory.createLoadingDialog(this);

        if(JMessageClient.getMyInfo() != null) {
            mTvxUserName.setText("用户名：" + JMessageClient.getMyInfo().getUserName());
        }

        mEditIpAdress.setText(getSpIpAddress());
        NetWorkMg.IP_ADDRESS = mEditIpAdress.getText().toString();
        mBtnIpAdressSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpIpAddress(mEditIpAdress.getText().toString());
                NetWorkMg.IP_ADDRESS = mEditIpAdress.getText().toString();
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_man:
                        NetWorkMg.GENDER = "男";
                        break;

                    case R.id.radio_lady:
                        NetWorkMg.GENDER = "女";
                        break;
                }
            }
        });



        getMemberList();
    }

    private void showLoginDialog(final Activity activity) {
        //创建login View
        View loginView = LayoutInflater.from(this).inflate(R.layout.layout_login_register,null);
        final TextView mTxvUser = loginView.findViewById(R.id.login_user);
        final TextView mTxvPassword = loginView.findViewById(R.id.login_password);
        Dialog dialog1 = new DialogFactory.Builder(activity)
                .setTitle("login or register")
                .setContentView(loginView)
                .setFirstBtnName("登录")
                .setSecondBtnName("注册")
                .create(new DialogFactory.OnButtonClickListener() {
                    @Override
                    public void onFirst(Dialog dialog, View view) {
                        jMessageLogin(dialog,mTxvUser.getText().toString(),mTxvPassword.getText().toString());
                    }

                    @Override
                    public void onSecond(Dialog dialog, View view) {
                        showRegisterDialog(activity);
                    }

                    @Override
                    public void onOthers(Dialog dialog, View view, String buttonName, int index) {

                    }
                });
        dialog1.setCanceledOnTouchOutside(true);
        dialog1.show();
    }

    private void showRegisterDialog(Activity activity) {
        View loginView = LayoutInflater.from(this).inflate(R.layout.layout_login_register,null);
        final TextView mTxvUser = loginView.findViewById(R.id.login_user);
        final TextView mTxvPassword = loginView.findViewById(R.id.login_password);
        Dialog dialog1 = new DialogFactory.Builder(activity)
                .setTitle("注册")
                .setContentView(loginView)
                .setFirstBtnName("注册")
                .setSecondBtnName("取消")
                .create(new DialogFactory.OnButtonClickListener() {
                    @Override
                    public void onFirst(Dialog dialog, View view) {
                        jMessageRegister(dialog,mTxvUser.getText().toString(),mTxvPassword.getText().toString());
                    }

                    @Override
                    public void onSecond(Dialog dialog, View view) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onOthers(Dialog dialog, View view, String buttonName, int index) {

                    }
                });
        dialog1.setCanceledOnTouchOutside(true);
        dialog1.show();
    }

    private void jMessageRegister(final Dialog dialog, String userId, String password) {
        loadingDialog.show();
        JMessageClient.register(userId, password
                , new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                loadingDialog.dismiss();
                if (i == 0) {
                    Toast.makeText(MainActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void jMessageLogin(final Dialog dialog, String userId, String password) {
        loadingDialog.show();
        JMessageClient.login(userId, password
                , new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        loadingDialog.dismiss();
                        List list = JMessageClient.getConversationList();
                        if(i != 0) {
                            Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                            saveUser(JMessageClient.getMyInfo().getUserName(),JMessageClient.getMyInfo().getAppKey());
                            mTvxUserName.setText("用户名：" + JMessageClient.getMyInfo().getUserName());
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void saveUser(String userName,String appKey) {
        UserInfo userInfo = JMessageClient.getMyInfo();
        Log.e("yy","avatar=" + userInfo.getAvatar());
        SharedPreferences sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString("userName",userName)
                .putString("appKey",appKey)
                .apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JCoreInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCoreInterface.onPause(this);
    }

    private void setSpIpAddress(String ipAddress) {
        SharedPreferences sp = getSharedPreferences("mySp",Activity.MODE_PRIVATE);
        sp.edit().putString("ipAddress",ipAddress).commit();
    }

    private String getSpIpAddress() {
        SharedPreferences sp = getSharedPreferences("mySp",Activity.MODE_PRIVATE);
        return sp.getString("ipAddress","192.168.1.103");
    }

    private void getMemberList() {
        Retrofit retrofit = NetWorkMg.newRetrofit();
        RequestApi requestApi = retrofit.create(RequestApi.class);

        requestApi.getChartRoomMemeberList(12536521)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMMemeberBean>() {
                    @Override
                    public void accept(JMMemeberBean jmMemeberBean) throws Exception {
                        Log.e("yy",jmMemeberBean.toString());
                    }
                });
    }
}
