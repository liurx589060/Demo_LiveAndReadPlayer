package com.lrxliveandreadplayer.demo.activitys;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.gson.Gson;
import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.beans.JMNormalSendBean;
import com.lrxliveandreadplayer.demo.factory.DialogFactory;
import com.lrxliveandreadplayer.demo.glide.GlideCircleTransform;
import com.lrxliveandreadplayer.demo.jmessage.JMsgSender;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;

/**
 * Created by Administrator on 2018/3/8.
 */

public class ChartGroupActivity extends Activity {
    private ArrayList<ImageView> imageViews;
    private EditText mEditText;
    private Button mBtnSend;
    private TextView mTxvChart;
    private StringBuilder stringBuilder;

    private Dialog inviteDialog;

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
                if (i == 5) {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //发送邀请
                            JMsgSender.sendMessage(ChartGroupActivity.this,1000,null,null);
                            getInviteDialog().show();
                        }
                    });
                }
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
                stringBuilder.append(JMessageClient.getMyInfo().getUserName() + ":" + str + "\n");
                mTxvChart.setText(stringBuilder.toString());
                mEditText.setText("");
                //发送信息
                JMsgSender.sendMessage(ChartGroupActivity.this,0,str,null);
            }
        });

        JMessageClient.registerEventReceiver(this);
    }

    private Dialog getInviteDialog() {
        if(inviteDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_loading_dialog,null);
            TextView tipTxv = view.findViewById(R.id.loading_tip);
            tipTxv.setText("邀请中...");
            inviteDialog = new DialogFactory.Builder(this)
                    .setContentView(view)
                    .setRadius((int) this.getResources().getDimension(R.dimen.sy_p_50))
                    .setBackGroundColor(Color.parseColor("#88ffffff"))
                    .setFirstBtnName("取消")
                    .create(new DialogFactory.OnButtonClickListener() {
                        @Override
                        public void onFirst(Dialog dialog, View view) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onSecond(Dialog dialog, View view) {

                        }

                        @Override
                        public void onOthers(Dialog dialog, View view, String buttonName, int index) {

                        }
                    });
            inviteDialog.setCanceledOnTouchOutside(false);
        }
        return inviteDialog;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }

    public void onEventMainThread(MessageEvent event){
        //do your own business
        String message = event.getMessage().getContent().toJson();
        String text = null;
        try {
            JSONObject object = new JSONObject(message);
            text = object.getString("text");
        }catch (Exception e) {
            Log.e("yy",e.toString());
        }
        JMNormalSendBean bean = new Gson().fromJson(text,JMNormalSendBean.class);
        if(bean.getCode() == 1000) {//邀请
            Intent intent = new Intent(this,LiveActivity.class);
            startActivity(intent);
        }else if (bean.getCode() == 1001) {//接受邀请
            if(getInviteDialog().isShowing()) {
                getInviteDialog().dismiss();
                Intent intent = new Intent(this,IjkLivePlayer.class);
                intent.putExtra("livePath",bean.getMsg());
                startActivity(intent);
            }
        }
        else {
            stringBuilder.append(event.getMessage().getFromUser().getUserName() + ":" + bean.getMsg() + "\n");
            mTxvChart.setText(stringBuilder.toString());
        }
    }
}
