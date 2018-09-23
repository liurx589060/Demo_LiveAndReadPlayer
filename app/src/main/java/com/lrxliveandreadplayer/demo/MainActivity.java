package com.lrxliveandreadplayer.demo;

import android.annotation.SuppressLint;
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

import com.google.gson.Gson;
import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.activitys.ChartGroupActivity;
import com.lrxliveandreadplayer.demo.activitys.IjkLivePlayer;
import com.lrxliveandreadplayer.demo.activitys.LiveActivity;
import com.lrxliveandreadplayer.demo.activitys.UserInfoActivity;
import com.lrxliveandreadplayer.demo.activitys.XqMainActivity;
import com.lrxliveandreadplayer.demo.beans.user.UserResp;
import com.lrxliveandreadplayer.demo.factory.DialogFactory;
import com.lrxliveandreadplayer.demo.manager.DataManager;
import com.lrxliveandreadplayer.demo.network.NetWorkMg;
import com.lrxliveandreadplayer.demo.network.RequestApi;
import com.lrxliveandreadplayer.demo.utils.Constant;
import com.lrxliveandreadplayer.demo.utils.Tools;
import com.lrxliveandreadplayer.demo.utils.XqErrorCode;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.ui.RxGalleryListener;
import cn.finalteam.rxgalleryfinal.ui.base.IRadioImageCheckedListener;
import cn.jiguang.api.JCoreInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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
    private Button mBtnImageSelector;
    private Button mBtnXq;
    private Button mBtnSwitchUser;

    private Dialog loadingDialog;
    private RequestApi mApi;
    private boolean mIsLoginSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        init();
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
        mBtnImageSelector = findViewById(R.id.btn_imageSelector);
        mBtnXq = findViewById(R.id.btn_xq);
        mBtnSwitchUser = findViewById(R.id.btn_switchUser);
        mBtnIjk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,IjkLivePlayer.class);
                startActivity(intent);
            }
        });

        mBtnImageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
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

        mBtnSwitchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出登陆对话框
                showLoginDialog(MainActivity.this);
            }
        });

        loadingDialog = DialogFactory.createLoadingDialog(this);

        if(JMessageClient.getMyInfo() != null) {
            //自动登陆
            autoLogin();
            mTvxUserName.setText("用户名：" + JMessageClient.getMyInfo().getUserName());
        }else {
            //弹出登陆
            showLoginDialog(this);
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

        mTvxUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsLoginSuccess) {
                    Tools.toast(MainActivity.this,"您未登陆到服务器",false);
                    return;
                }
                //跳转到填写详情页面
                Intent intent = new Intent(MainActivity.this,UserInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo",DataManager.getInstance().getUserInfo());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mBtnXq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, XqMainActivity.class);
                startActivity(intent);
            }
        });
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

    private void openImageSelector() {
        //裁剪图片的回调
        RxGalleryListener
                .getInstance()
                .setRadioImageCheckedListener(
                        new IRadioImageCheckedListener() {
                            @Override
                            public void cropAfter(Object t) {
                                Log.e("yy","" + t.toString());
                                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_SHORT).show();
                                File file1 = new File(t.toString());
                                if(file1.exists()) {
                                    file1.delete();
                                }
                            }

                            @Override
                            public boolean isActivityFinish() {
                                return true;
                            }
                        });

        RxGalleryFinal
                .with(this)
                .image()
                .radio()
                .crop()
                .imageLoader(ImageLoaderType.GLIDE)
                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        //图片选择结果
//                        Log.e("yy",new Gson().toJson(imageRadioResultEvent));
                    }
                })
                .openGallery();
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
        regist(dialog,userId,password);
    }

    private void jMessageLogin(final Dialog dialog, String userId, String password) {
        login(dialog,userId,password);
    }

    private void saveUser(String userName,String password,String appKey) {
        UserInfo userInfo = JMessageClient.getMyInfo();
        Log.e("yy","avatar=" + userInfo.getAvatar());
        SharedPreferences sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString("userName",userName)
                .putString("password",password)
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

    @SuppressLint("CheckResult")
    private void regist(final Dialog dialog, final String userName, final String password) {
        loadingDialog.show();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> observableEmitter) throws Exception {
                JMessageClient.register(userName, password
                        , new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0 || i == 898001) {//成功或者已注册过
                                    observableEmitter.onNext(i);
                                } else {
                                    observableEmitter.onError(new Throwable("JMessage regist error"));
                                }
                            }
                        });
            }
        }).observeOn(Schedulers.io()).flatMap(new Function<Integer, ObservableSource<UserResp>>() {
            @Override
            public ObservableSource<UserResp> apply(Integer integer) throws Exception {
                try {
                    return mApi.regist(userName,password);
                }catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<UserResp>() {
            @Override
            public void accept(UserResp userResp) throws Exception {
                loadingDialog.dismiss();
                if(userResp.getStatus() == XqErrorCode.SUCCESS) {//注册成功
                    dialog.dismiss();
                    Tools.toast(getApplicationContext(),"注册成功",false);
                    DataManager.getInstance().setUserInfo(userResp.getData());
                    //跳转到填写详情页面
                    Intent intent = new Intent(MainActivity.this,UserInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo",DataManager.getInstance().getUserInfo());
                    bundle.putBoolean("isRegist",true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else if (userResp.getStatus() == XqErrorCode.ERROR_USER_REGIST_EXIST) {
                    Tools.toast(getApplicationContext(),"已存在该账号",true);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                loadingDialog.dismiss();
                Tools.toast(getApplicationContext(),"注册失败--" + throwable.toString(),true);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void login(final Dialog dialog, final String userName, final String password) {
        loadingDialog.show();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> observableEmitter) throws Exception {
                JMessageClient.login(userName, password
                        , new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if(i != 0) {
                                    observableEmitter.onError(new Throwable("JMessage login fail--" + s + ">>>" + i));
                                }else {
                                    observableEmitter.onNext(i);
                                }
                            }
                        });
            }
        }).observeOn(Schedulers.io())
        .flatMap(new Function<Integer, ObservableSource<UserResp>>() {
            @Override
            public ObservableSource<UserResp> apply(Integer integer) throws Exception {
                try {
                    return mApi.login(userName,password);
                }catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<UserResp>() {
            @Override
            public void accept(UserResp userResp) throws Exception {
                loadingDialog.dismiss();
                if(userResp.getStatus() == XqErrorCode.SUCCESS) {
                    mIsLoginSuccess = true;
                    DataManager.getInstance().setUserInfo(userResp.getData());
                    dialog.dismiss();
                    saveUser(JMessageClient.getMyInfo().getUserName(),password,JMessageClient.getMyInfo().getAppKey());
                    mTvxUserName.setText("用户名：" + JMessageClient.getMyInfo().getUserName());
                    Tools.toast(getApplicationContext(),"登录成功",false);
                }else if(userResp.getStatus() == XqErrorCode.ERROR_USER_PASSWORD_WRONG) {
                    Tools.toast(getApplicationContext(),"密码错误",true);
                }else if(userResp.getStatus() == XqErrorCode.ERROR_USER_REGIST_UNEXIST) {
                    Tools.toast(getApplicationContext(),"用户不存在",true);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mIsLoginSuccess = false;
                loadingDialog.dismiss();
                Tools.toast(getApplicationContext(),throwable.toString(),true);
            }
        });
    }

    /**
     * 自动登陆自己的服务器
     */
    @SuppressLint("CheckResult")
    private void autoLogin() {
        loadingDialog.show();
        SharedPreferences sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        final String userName = sp.getString("userName","");
        final String password = sp.getString("password","");
        mApi.login(userName,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        mIsLoginSuccess = true;
                        loadingDialog.dismiss();
                        if(userResp.getStatus() == XqErrorCode.SUCCESS) {
                            DataManager.getInstance().setUserInfo(userResp.getData());
                            saveUser(userName,password,JMessageClient.getMyInfo().getAppKey());
                            mTvxUserName.setText("用户名：" + JMessageClient.getMyInfo().getUserName());
                            Tools.toast(getApplicationContext(),"登录成功",false);
                        }else if(userResp.getStatus() == XqErrorCode.ERROR_USER_PASSWORD_WRONG) {
                            Tools.toast(getApplicationContext(),"密码错误",true);
                        }else if(userResp.getStatus() == XqErrorCode.ERROR_USER_REGIST_UNEXIST) {
                            Tools.toast(getApplicationContext(),"用户不存在",true);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mIsLoginSuccess = false;
                        loadingDialog.dismiss();
                        Tools.toast(getApplicationContext(),throwable.toString(),true);
                    }
                });
    }

    private void setSpIpAddress(String ipAddress) {
        SharedPreferences sp = getSharedPreferences("mySp",Activity.MODE_PRIVATE);
        sp.edit().putString("ipAddress",ipAddress).commit();
    }

    private String getSpIpAddress() {
        SharedPreferences sp = getSharedPreferences("mySp",Activity.MODE_PRIVATE);
        return sp.getString("ipAddress","192.168.1.101");
    }
}
