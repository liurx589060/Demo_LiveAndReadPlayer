package com.lrxliveandreadplayer.demo.manager;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.yrom.screenrecorder.operate.AudioRecordOpt;
import net.yrom.screenrecorder.operate.RecorderBean;
import net.yrom.screenrecorder.task.RtmpStreamingSender;

public class XqAudioViewMg implements IXqChartView {
    private Activity mActivity;
    private String mAddress;

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void onResume() {
        AudioRecordOpt.getInstance().resume();
    }

    @Override
    public void onPause() {
        AudioRecordOpt.getInstance().pause();
    }

    @Override
    public void onDestroy() {
        stop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    public XqAudioViewMg(Activity activity,String address) {
        mActivity = activity;
        mAddress = address;
    }

    private void init() {

    }

    public void start() {
        if(mAddress == null || mAddress.isEmpty()) {
            throw new IllegalArgumentException("the mAddress is not valid,can not null or empty");
        }
        RecorderBean audioBean = new RecorderBean();
        audioBean.setRtmpAddr(mAddress);
        AudioRecordOpt.getInstance().startAudioRecord(audioBean, new RtmpStreamingSender.IRtmpSendCallBack() {
            @Override
            public void sendError() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity,"AudioRecordOpt--sendError",Toast.LENGTH_SHORT).show();
                        Log.e("yy","AudioRecordOpt--sendError");
                    }
                });
            }

            @Override
            public void connectError() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity,"AudioRecordOpt--connectError",Toast.LENGTH_SHORT).show();
                        Log.e("yy","AudioRecordOpt--connectError");
                    }
                });
            }

            @Override
            public void netBad() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity,"AudioRecordOpt--netBad",Toast.LENGTH_SHORT).show();
                        Log.e("yy","AudioRecordOpt--netBad");
                    }
                });
            }

            @Override
            public void onStart(String rtmpAddress) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity,"start",Toast.LENGTH_SHORT).show();
                        Log.e("yy","AudioRecordOpt--start");
                    }
                });
            }
        });
    }

    public void stop() {
        AudioRecordOpt.getInstance().stopRecord();
    }

    public boolean isRecording() {
        return AudioRecordOpt.getInstance().isRecording();
    }

    public void setMic(boolean isMic) {
        AudioRecordOpt.getInstance().setMic(isMic);
    }

    public void setAddress(String url) {
        mAddress = url;
    }
}
