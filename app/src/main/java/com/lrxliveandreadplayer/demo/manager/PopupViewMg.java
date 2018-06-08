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
import com.lrxliveandreadplayer.demo.utils.Tools;

public class PopupViewMg {
    private final int POPUP_ANGEL = 0x0001;//爱心大使点击自己头像的弹出

    private static PopupViewMg instance = null;

    private PopupWindow mAngelPopupWindow;

    public static PopupViewMg getInstance() {
        if(instance == null) {
            instance = new PopupViewMg();
        }
        return instance;
    }

    public void showAngelPopupView(Activity activity, View showView, final IPopupAngelListener listener) {
        if(mAngelPopupWindow == null) {
            View contentView = getView(activity,POPUP_ANGEL);
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

    private View getView(Activity activity,int viewType) {
        View view = null;
        switch (viewType) {
            case POPUP_ANGEL:
                view = LayoutInflater.from(activity).inflate(R.layout.layout_popup_angel,null);
                break;
        }
        return view;
    }

}
