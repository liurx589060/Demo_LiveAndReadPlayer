package com.lrxliveandreadplayer.demo.manager;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.lrxliveandreadplayer.demo.network.NetWorkMg;
import com.lrxliveandreadplayer.demo.utils.Tools;

import net.yrom.screenrecorder.operate.CameraRecordOpt;
import net.yrom.screenrecorder.operate.ICameraCallBack;
import net.yrom.screenrecorder.operate.RecorderBean;
import net.yrom.screenrecorder.ui.CameraUIHelper;

public class XqCameraViewMg extends AbsChartView {
    private Activity mActivity;
    private CameraUIHelper mCameraUIHelper;
    private RecorderBean mRecorderBean;

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
        mCameraUIHelper.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    public XqCameraViewMg(Activity activity) {
        mActivity = activity;
        WindowManager windowManager = activity.getWindowManager();
        //获取屏幕宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        mRecorderBean = new RecorderBean();
        mRecorderBean.setWidth(width);
        mRecorderBean.setHeight(height);
        mRecorderBean.setRtmpAddr(NetWorkMg.getCameraUrl());
        mCameraUIHelper = new CameraUIHelper(activity,mRecorderBean);
        CameraRecordOpt.getInstance().setCameraCallBack(new ICameraCallBack() {
            @Override
            public void onCameraOpenError() {

            }

            @Override
            public void onLiveStart(String s) {
                Tools.toast(mActivity,"onLiveStart",false);
            }

            @Override
            public void onLiveStop() {
                Tools.toast(mActivity,"onLiveStop",false);
            }

            @Override
            public void sendError() {
                Tools.toast(mActivity,"sendError",false);
            }

            @Override
            public void connectError() {
                Tools.toast(mActivity,"connectError",false);
            }
        });
    }

    @Override
    public void start() {
        mCameraUIHelper.startRecord();
    }

    @Override
    public void stop() {
        mCameraUIHelper.stopRecord();
    }

    @Override
    public void setVisible(boolean isVisible) {
        mCameraUIHelper.getCameraLivingView().setVisibility(isVisible?View.VISIBLE:View.INVISIBLE);
    }

    public CameraUIHelper getmCameraUIHelper() {
        return mCameraUIHelper;
    }
}
