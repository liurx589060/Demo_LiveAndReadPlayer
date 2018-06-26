package com.lrxliveandreadplayer.demo.manager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.lrx.live.player.R;

public class XqPlayerViewMg implements IXqChartView {
    private PowerManager.WakeLock wakeLock;
    private PlayerView player;
    private View rootView;
    private String mAddress;

    @Override
    public View getView() {
        return rootView;
    }

    @Override
    public void onResume() {
        if(player != null) {
            player.onResume();
        }
    }

    @Override
    public void onPause() {
        if(player != null) {
            player.onPause();
        }
    }

    @Override
    public void onDestroy() {
        wakeLock.release();
        if(player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    public XqPlayerViewMg(Activity context,String address) {
        rootView = LayoutInflater.from(context).inflate(R.layout.simple_player_view_player,null);
        mAddress = address;
    }

    public void init(Activity context) {
        /**常亮*/
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();

        player = new PlayerView(context, rootView)
                .setTitle("收看直播")
                .setScaleType(PlayStateParams.fillparent)
                .forbidTouch(true)
                .hideSteam(true)
                .hideCenterPlayer(true)
                .forbidTouch(true)
                .hideMenu(true)
                .hideAllUI();
    }

    public void start() {
        if(player == null) return;
        if(mAddress == null || mAddress.isEmpty()) {
            throw new IllegalArgumentException("the mAddress is not valid,can not null or empty");
        }
        String sourceUrl = mAddress;
        player.setPlaySource(sourceUrl)
                .startPlay();
    }

    public void stop() {
        if(player == null) return;
        player.stopPlay();
    }

    public void setVisible(boolean isVisible) {
        if(rootView == null) return;
        rootView.setVisibility(isVisible?View.VISIBLE:View.INVISIBLE);
    }

    public PlayerView getPlayerView() {
        return player;
    }

    public void setAddress(String url) {
        mAddress = url;
    }
}
