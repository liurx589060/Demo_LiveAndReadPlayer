package com.lrxliveandreadplayer.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    private Button mBtnIjk;
    private Button mBtnLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mBtnIjk = findViewById(R.id.btn_ijkPlayer);
        mBtnLive = findViewById(R.id.btn_live);
        mBtnIjk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,IjkLivePlayer.class);
                startActivity(intent);
            }
        });

        mBtnLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LiveActivity.class);
                startActivity(intent);
            }
        });
    }
}
