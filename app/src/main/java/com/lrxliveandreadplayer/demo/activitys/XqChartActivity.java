package com.lrxliveandreadplayer.demo.activitys;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lrxliveandreadplayer.demo.manager.XqChartUIViewMg;

/**
 * Created by Administrator on 2018/5/26.
 */

public class XqChartActivity extends Activity {
    private XqChartUIViewMg mXqChartUIViewMg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mXqChartUIViewMg = new XqChartUIViewMg(this);
        mXqChartUIViewMg.setContentView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mXqChartUIViewMg.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mXqChartUIViewMg.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mXqChartUIViewMg.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mXqChartUIViewMg.onConfigurationChanged(newConfig);
    }
}
