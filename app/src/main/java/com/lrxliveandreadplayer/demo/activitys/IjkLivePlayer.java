package com.lrxliveandreadplayer.demo.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dou361.ijkplayer.listener.OnPlayerBackListener;
import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
import com.dou361.ijkplayer.utils.ResourceUtils;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.utils.Constant;

/**
 * Created by daven.liu on 2018/2/28 0028.
 */

public class IjkLivePlayer extends Activity {
    private PlayerView player;
    private View rootView;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().from(this).inflate(R.layout.simple_player_view_player,null);
        setContentView(rootView);

        init();
    }

    private void init() {
        /**常亮*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();

        player = new PlayerView(this, rootView)
                .setTitle("收看直播")
                .setScaleType(PlayStateParams.fitparent)
//                .forbidTouch(false)
//                .hideSteam(true)
//                .hideCenterPlayer(true)
                .forbidTouch(false)
                .hideMenu(true)
                .showThumbnail(new OnShowThumbnailListener() {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                        Glide.with(IjkLivePlayer.this)
                                .load(Constant.thumbUrl)
                                .placeholder(R.color.Grey_100)
                                .error(android.R.color.holo_blue_light)
                                .into(ivThumbnail);
                    }
                });

        String sourceUrl = "rtmp://10.10.15.19/live/stream";
        if(getIntent() != null) {
            sourceUrl = getIntent().getStringExtra("livePath")==null?"rtmp://192.168.1.102/live/stream"
                    :getIntent().getStringExtra("livePath");
        }
        player.setPlaySource(sourceUrl)
                .setPlayerBackListener(new OnPlayerBackListener() {
                    @Override
                    public void onPlayerBack() {
                        finish();
                    }
                })
                .startPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
