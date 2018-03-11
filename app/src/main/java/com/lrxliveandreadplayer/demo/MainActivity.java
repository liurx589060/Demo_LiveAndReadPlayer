package com.lrxliveandreadplayer.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.activitys.ChartGroupActivity;
import com.lrxliveandreadplayer.demo.activitys.IjkLivePlayer;
import com.lrxliveandreadplayer.demo.activitys.LiveActivity;
import com.lrxliveandreadplayer.demo.factory.DialogFactory;
import com.lrxliveandreadplayer.demo.utils.Constant;

import java.util.List;

import cn.jiguang.api.JCoreInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class MainActivity extends Activity {
    private Button mBtnIjk;
    private Button mBtnLive;
    private Button mBtnMatch;
    private TextView mTvxUserName;

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

        mTvxUserName.setText("用户名：" + JMessageClient.getMyInfo().getUserName());
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
}
