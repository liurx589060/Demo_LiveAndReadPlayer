package com.lrxliveandreadplayer.demo;

import android.app.Application;

import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by Administrator on 2018/3/2.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this);
    }
}
