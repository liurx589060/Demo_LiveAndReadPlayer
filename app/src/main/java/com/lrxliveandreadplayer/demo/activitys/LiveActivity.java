package com.lrxliveandreadplayer.demo.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.jmessage.JMsgSender;

import net.yrom.screenrecorder.operate.AudioRecordOpt;
import net.yrom.screenrecorder.operate.CameraRecordOpt;
import net.yrom.screenrecorder.operate.ICameraCallBack;
import net.yrom.screenrecorder.operate.RecorderBean;
import net.yrom.screenrecorder.ui.CameraRecordActivity;
import net.yrom.screenrecorder.ui.CameraUIHelper;

/**
 * Created by daven.liu on 2018/2/28 0028.
 */

public class LiveActivity extends Activity {
    private EditText mEditAddress;
    private Button mBtnScreen;
    private Button mBtnCamera;
    private Button mBtnAudio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        init();
    }

    private void init() {
        mEditAddress = findViewById(R.id.edit_address);
        mBtnScreen = findViewById(R.id.btn_screen);
        mBtnCamera = findViewById(R.id.btn_camera);
        mBtnAudio = findViewById(R.id.btn_audio);

        mEditAddress.setText("rtmp://192.168.1.102/live/stream");
        mBtnScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScreenRecordActivity.launchActivity(LiveActivity.this,mEditAddress.getText().toString());
            }
        });

        mBtnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioRecordActivity.launchActivity(LiveActivity.this,mEditAddress.getText().toString());
            }
        });

        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecorderBean recorderBean = new RecorderBean();
                recorderBean.setRtmpAddr(mEditAddress.getText().toString());
                recorderBean.setWidth(1080);
                recorderBean.setHeight(1920);
                recorderBean.setBitrate(2000000);
                CameraRecordOpt.getInstance().setCameraCallBack(new ICameraCallBack() {
                    @Override
                    public void onCameraOpenSuccess() {
                        super.onCameraOpenSuccess();
                        Toast("onCameraOpenSuccess");
                    }

                    @Override
                    public void onCameraOpenError() {
                        Toast("onCameraOpenError");
                    }

                    @Override
                    public void onSwitchCamera(int cameraType) {
                        super.onSwitchCamera(cameraType);
                        String cameraTypeStr = cameraType== CameraUIHelper.CAMERA_BACK?"后置摄像头":"前置摄像头";
                        Toast("onSwitchCamera--" + cameraTypeStr);
                    }

                    @Override
                    public void onLiveStart(String rtmpAddress) {
                        Toast("onLiveStart--" + rtmpAddress);
                    }

                    @Override
                    public void onLiveStop() {
                        Toast("onLiveStop");
                    }

                    @Override
                    public void sendError() {
                        Toast("sendError");
                    }

                    @Override
                    public void connectError() {
                        Toast("connectError");
                    }
                });
                CameraRecordOpt.getInstance().startCameraRecordWithActivity(LiveActivity.this,recorderBean,CameraRecordActivity.class);
                JMsgSender.sendMessage(LiveActivity.this,1001,mEditAddress.getText().toString(),null);
            }
        });
    }

    private void Toast(String msg) {
        Toast.makeText(LiveActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AudioRecordOpt.getInstance().isRecording()) {
            AudioRecordOpt.getInstance().stopRecord();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
