package com.lrxliveandreadplayer.demo.manager;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;

/**
 * Created by Administrator on 2018/5/26.
 */

public abstract class AbsChartView {
    public abstract View getView();
    public abstract void onResume();
    public abstract void onPause();
    public abstract void onDestroy();
    public abstract void onConfigurationChanged(Configuration newConfig);

    public void setVisible(boolean isVisible){}
    public void start(){}
    public void stop(){}
    public void init(Activity activity){}
}
