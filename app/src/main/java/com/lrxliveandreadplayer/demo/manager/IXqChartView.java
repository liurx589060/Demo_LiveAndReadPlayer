package com.lrxliveandreadplayer.demo.manager;

import android.content.res.Configuration;
import android.view.View;

/**
 * Created by Administrator on 2018/5/26.
 */

public interface IXqChartView {
    View getView();
    void onResume();
    void onPause();
    void onDestroy();
    void onConfigurationChanged(Configuration newConfig);
}
