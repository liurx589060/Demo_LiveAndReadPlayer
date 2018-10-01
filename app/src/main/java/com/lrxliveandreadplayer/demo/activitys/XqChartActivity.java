package com.lrxliveandreadplayer.demo.activitys;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lrxliveandreadplayer.demo.manager.XqChartUIViewMg;
import com.lrxliveandreadplayer.demo.manager.XqStatusChartUIViewMg;

/**
 * Created by Administrator on 2018/5/26.
 */

public class XqChartActivity extends Activity {
    private XqStatusChartUIViewMg mXqStatusChartUIViewMg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mXqStatusChartUIViewMg = new XqStatusChartUIViewMg(this);
        mXqStatusChartUIViewMg.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mXqStatusChartUIViewMg.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mXqStatusChartUIViewMg.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mXqStatusChartUIViewMg.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mXqStatusChartUIViewMg.onConfigurationChanged(newConfig);
    }
}
