package com.lrxliveandreadplayer.demo.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lrxliveandreadplayer.demo.R;
import com.lrxliveandreadplayer.demo.glide.GlideCircleTransform;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/8.
 */

public class ChartGroupActivity extends Activity {
    private ArrayList<ImageView> imageViews;
    private EditText mEditText;
    private Button mBtnSend;
    private TextView mTxvChart;
    private StringBuilder stringBuilder;

    private String imageUrl_1 = "http://y2.ifengimg.com/a/2015_52/e8b2ace8a05e133.jpg";
    private String imageUrl_4 = "http://i1.hdslb.com/bfs/face/444eb05cb8699dc1be292c6ffd87afce578a31d2.jpg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_group);

        init();
    }

    private void init() {
        imageViews = new ArrayList<>();
        stringBuilder = new StringBuilder();
        mEditText = findViewById(R.id.chart_group_edit);
        mBtnSend = findViewById(R.id.chart_group_send);
        mTxvChart = findViewById(R.id.chart_group_text);
        mTxvChart.setMovementMethod(new ScrollingMovementMethod());

        for (int i = 1 ; i <= 6;i++) {
            String id = "chart_group_image" + i;
            ImageView imageView = findViewById(getResources().getIdentifier(id,"id",getPackageName()));
            imageViews.add(imageView);
            if(i == 2 || i == 5) {
                Glide.with(this)
                        .load(i==2?imageUrl_1:imageUrl_4)
                        .bitmapTransform(new GlideCircleTransform(this))
                        .into(imageView);
            }else {
                Glide.with(this)
                        .load(R.drawable.ic_launcher)
                        .bitmapTransform(new CenterCrop(this))
                        .into(imageView);
            }
        }

        stringBuilder.append("聊天中...." + "\n");
        mTxvChart.setText(stringBuilder.toString());
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mEditText.getText().toString();
                stringBuilder.append(str + "\n");
                mTxvChart.setText(stringBuilder.toString());
                mEditText.setText("");
            }
        });
    }
}
