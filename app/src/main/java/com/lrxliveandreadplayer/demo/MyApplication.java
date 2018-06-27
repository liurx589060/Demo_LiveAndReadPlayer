package com.lrxliveandreadplayer.demo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;

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
        //bugly
        CrashReport.initCrashReport(getApplicationContext(), "4c34f4883b", true);

        TXLiveBase.setAppID("1252463788");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
