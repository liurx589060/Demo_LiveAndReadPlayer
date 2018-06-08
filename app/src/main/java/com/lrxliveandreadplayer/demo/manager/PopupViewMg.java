package com.lrxliveandreadplayer.demo.manager;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.interfaces.IPopupAngelListener;
import com.lrxliveandreadplayer.demo.interfaces.IPopupGuestListener;
import com.lrxliveandreadplayer.demo.utils.Tools;

public class PopupViewMg {
    private final int POPUP_ANGEL = 0x0001;//爱心大使点击自己头像的弹出
    private final int POPUP_GUEST = 0x0002;//嘉宾点击自己头像的弹出

    private PopupWindow mAngelPopupWindow;
    private PopupWindow mGuestPopupWindow;

    public enum Position {
        LEFT,
        RIGHT
    }

    public enum LiveType {
        LIVE_VIDEO,
        LIVE_MIC,
        LIVE_NONE
    }

    public void showAngelPopupView(Activity activity, View showView, final IPopupAngelListener listener) {
        if(mAngelPopupWindow == null) {
            View contentView = getView(activity,POPUP_ANGEL);
            if(contentView == null) return;
            int widthSpec = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            contentView.measure(widthSpec,heightSpec);
            mAngelPopupWindow = new PopupWindow(contentView);
            mAngelPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mAngelPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            Button btnDisturb = contentView.findViewById(R.id.btn_disturb);
            btnDisturb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onDisturb((Button) view);
                    }
                    mAngelPopupWindow.dismiss();
                }
            });
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            mAngelPopupWindow.setBackgroundDrawable(dw);
            mAngelPopupWindow.setFocusable(true);
            mAngelPopupWindow.setOutsideTouchable(true);
        }
        mAngelPopupWindow.showAtLocation(showView, Gravity.RIGHT, Tools.dip2px(activity,10),0);
    }

    public void showGuestPopupView(Activity activity, View showView, Position position,final IPopupGuestListener listener) {
        if(mGuestPopupWindow == null) {
            View contentView = getView(activity,POPUP_GUEST);
            if(contentView == null) return;
            int widthSpec = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            contentView.measure(widthSpec,heightSpec);
            mGuestPopupWindow = new PopupWindow(contentView);
            mGuestPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mGuestPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            final Button btnVideo = contentView.findViewById(R.id.btn_video);
            final Button btnMic = contentView.findViewById(R.id.btn_mic);
            btnVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onType(btnVideo,LiveType.LIVE_VIDEO);
                    }
                    mGuestPopupWindow.dismiss();
                }
            });

            btnMic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onType(btnMic,LiveType.LIVE_MIC);
                    }
                    mGuestPopupWindow.dismiss();
                }
            });
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            mGuestPopupWindow.setBackgroundDrawable(dw);
            mGuestPopupWindow.setFocusable(true);
            mGuestPopupWindow.setOutsideTouchable(true);
        }
        mGuestPopupWindow.showAtLocation(showView,position==Position.LEFT?Gravity.LEFT:Gravity.RIGHT
                ,Tools.dip2px(activity,10),0);
    }

    private View getView(Activity activity,int viewType) {
        View view = null;
        switch (viewType) {
            case POPUP_ANGEL:
                view = LayoutInflater.from(activity).inflate(R.layout.layout_popup_angel,null);
                break;
            case POPUP_GUEST:
                view = LayoutInflater.from(activity).inflate(R.layout.layout_popup_guest,null);
                break;

        }
        return view;
    }

}
