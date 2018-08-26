package com.lrxliveandreadplayer.demo.manager;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.utils.Tools;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

public class XqTxPlayerViewMg extends AbsChartView {
    private static final float  CACHE_TIME_FAST = 1.0f;
    private static final float  CACHE_TIME_SMOOTH = 5.0f;

    private View mRootView;
    private TXLivePlayer mLivePlayer;
    private TXCloudVideoView mVideoView;
    private String mAddress;

    @Override
    public View getView() {
        return mRootView;
    }

    @Override
    public void onResume() {
        mLivePlayer.resume();
    }

    @Override
    public void onPause() {
        mLivePlayer.pause();
    }

    @Override
    public void onDestroy() {
        mLivePlayer.stopPlay(true);
        mVideoView.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        mRootView.setVisibility(isVisible?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public void start() {
        super.start();
        if(mAddress == null || mAddress.isEmpty()) {
            throw new IllegalArgumentException("the mAddress is not valid,can not null or empty");
        }
        if(mLivePlayer != null) {
            mLivePlayer.setPlayListener(new ITXLivePlayListener() {
                @Override
                public void onPlayEvent(int i, Bundle bundle) {
                    Log.e("yy","TxPlayer--onPlayEvent--" + i);
                }

                @Override
                public void onNetStatus(Bundle bundle) {
                    Log.e("yy","TxPlayer--onNetStatus--" + bundle);
                }
            });
            mLivePlayer.startPlay(mAddress, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
        }
    }

    @Override
    public void stop() {
        super.stop();
        if(mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
        }
    }

    @Override
    public void init(Activity activity) {
        super.init(activity);
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public XqTxPlayerViewMg(Activity activity,String address) {
        mAddress = address;
        mRootView = LayoutInflater.from(activity).inflate(R.layout.layout_viewmg_xqtxplayer,null);
        mVideoView = mRootView.findViewById(R.id.video_view);
        try {
            mLivePlayer = new TXLivePlayer(activity);
            mLivePlayer.setPlayerView(mVideoView);
            // 设置填充模式
            mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
            // 设置画面渲染方向
            mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);

            TXLivePlayConfig mPlayConfig = new TXLivePlayConfig();
            mPlayConfig.setAutoAdjustCacheTime(true);
            mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_SMOOTH);
            mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
            mLivePlayer.setConfig(mPlayConfig);
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("yy",e.toString());
            Tools.toast(activity,"启动TXLivePlayer失败", true);
        }
    }
}
