package com.lrxliveandreadplayer.demo.manager;

import android.view.View;

/**
 * Created by Administrator on 2018/5/26.
 */

public interface IXqChartView {
    View createView();
    void onResume();
    void onDestroy();
}
