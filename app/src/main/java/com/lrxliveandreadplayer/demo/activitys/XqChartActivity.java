package com.lrxliveandreadplayer.demo.activitys;

import android.app.Activity;
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
        setContentView(mXqChartUIViewMg.createView());
    }

    public XqChartUIViewMg getmXqChartUIViewMg() {
        return mXqChartUIViewMg;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mXqChartUIViewMg.onDestroy();
    }
}
