package com.lrxliveandreadplayer.demo.manager;

import android.app.Activity;
import android.app.Service;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.lrx.live.player.R;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.ref.WeakReference;


/**
 * Created by Administrator on 2018/9/15.
 */

public class XqTxPushViewMg extends AbsChartView {
    private TXLivePusher mLivePusher;
    private TXCloudVideoView mVideoView;
    private TXLivePushConfig mLivePushConfig;

    @Override
    public View getView() {
        return mRootView;
    }

    @Override
    public void onResume() {
        if(mVideoView != null) {
            mVideoView.onResume();
        }

        if (mLivePusher != null) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
    }

    @Override
    public void onPause() {
        if(mVideoView != null) {
            mVideoView.onPause();
        }

        if(mLivePusher != null) {
            mLivePusher.pausePusher();
            mLivePusher.pauseBGM();
        }
    }

    @Override
    public void onDestroy() {
        if(mVideoView != null) {
            mVideoView.onDestroy();
        }

        if(mLivePusher != null) {
            mLivePusher.stopBGM();
            mLivePusher.stopCameraPreview(true);
            mLivePusher.setPushListener(null);
            mLivePusher.stopPusher();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    public void start(boolean isPureAudio) {
        start();
        if(mLivePusher != null && mLivePushConfig != null) {
            mLivePushConfig.enablePureAudioPush(isPureAudio);   // true 为启动纯音频推流，而默认值是 false；
            mLivePusher.setConfig(mLivePushConfig);

            mLivePusher.startPusher(mAddress);
        }
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();

        if(mLivePusher != null) {
            mLivePusher.stopPusher();
            mLivePusher.stopBGM();
        }
    }

    @Override
    public void init(Activity activity,String address) {
        super.init(activity,address);
        mRootView = LayoutInflater.from(activity).inflate(R.layout.layout_viewmg_xqtxpush,null);

        mLivePusher = new TXLivePusher(mActivity);
        mLivePushConfig = new TXLivePushConfig();

        mVideoView = (TXCloudVideoView) mRootView.findViewById(R.id.tx_push_videoview);
        mLivePusher.startCameraPreview(mVideoView);

        mLivePusher.setPushListener(new ITXLivePushListener() {
            @Override
            public void onPushEvent(int i, Bundle bundle) {
                Log.e("yy","onPushEvent=" + i);
            }

            @Override
            public void onNetStatus(Bundle status) {
                Log.d("yy", "Current status, CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE)+
                        ", RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)+
                        ", SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps"+
                        ", FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS)+
                        ", ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps"+
                        ", VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps");
            }
        });

        mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION,
                false,
                false);

        TXPhoneStateListener mPhoneListener = new TXPhoneStateListener(mLivePusher);
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void setAddress(String newAddress) {
        mAddress = newAddress;
        stop();
        start();
    }

    public class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TXLivePusher> mPusher;
        public TXPhoneStateListener(TXLivePusher pusher) {
            mPusher = new WeakReference<TXLivePusher>(pusher);
        }
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TXLivePusher pusher = mPusher.get();
            switch(state){
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (pusher != null) pusher.pausePusher();
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (pusher != null) pusher.pausePusher();
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (pusher != null) pusher.resumePusher();
                    break;
            }
        }
    }
}
