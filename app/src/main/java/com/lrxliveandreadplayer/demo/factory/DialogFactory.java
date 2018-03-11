package com.lrxliveandreadplayer.demo.factory;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lrx.live.player.R;

/**
 * Created by Administrator on 2018/3/3.
 */

public class DialogFactory {
    public interface OnButtonClickListener {
        void onFirst(Dialog dialog,View view);
        void onSecond(Dialog dialog,View view);
        void onOthers(Dialog dialog,View view,String buttonName,int index);
    }

    public static class Builder {
        private Activity activity;
        private View contentView;
        private String title;
        private String content;
        private String firstBtnName;
        private String secondBtnName;
        private String[] otherNames;
        private boolean isFirstVisible = true;
        private boolean isSecondVisible = true;
        private boolean isBtnLayoutVisible = true;
        private int backGroundColor = Color.WHITE;
        private int radius = 10;
        private float cardElevation = 0f;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setFirstBtnName(String firstBtnName) {
            this.firstBtnName = firstBtnName;
            return this;
        }

        public Builder setSecondBtnName(String secondBtnName) {
            this.secondBtnName = secondBtnName;
            return this;
        }

        public Builder setOtherNames(String[] otherNames) {
            this.otherNames = otherNames;
            return this;
        }

        public Dialog create(final OnButtonClickListener listener) {
            final Dialog dialog = new Dialog(activity,R.style.loading_dialog);
            View rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_factory,null);
            CardView backGroundView = rootView.findViewById(R.id.factory_background_layout);
            backGroundView.setCardBackgroundColor(backGroundColor);
            backGroundView.setRadius(radius);
            backGroundView.setCardElevation(cardElevation);
            View titleLayout = rootView.findViewById(R.id.factory_title_layout);
            if(title != null) {
                titleLayout.setVisibility(View.VISIBLE);
                TextView txvTitle = rootView.findViewById(R.id.dialog_factory_title);
                txvTitle.setText(title==null?"":title);
            }else {
                titleLayout.setVisibility(View.GONE);
            }

            FrameLayout contentLayout = rootView.findViewById(R.id.factory_content_layout);
            if(content != null) {
                View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_factory_content_layout,null);
                contentLayout.addView(contentView);
                TextView txvContent = rootView.findViewById(R.id.dialog_factory_content);
                txvContent.setText(content);
                txvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
            }else if(contentView != null){
                contentLayout.addView(contentView);
            }

            View btnLayout = rootView.findViewById(R.id.factory_btn_layout);
            if(isBtnLayoutVisible) {
                btnLayout.setVisibility(View.VISIBLE);
            }else {
                btnLayout.setVisibility(View.GONE);
            }
            Button btnPositive = rootView.findViewById(R.id.dialog_factory_position);
            Button btnNegative = rootView.findViewById(R.id.dialog_factory_negative);

            btnPositive.setVisibility((isFirstVisible&&firstBtnName!=null)?View.VISIBLE:View.GONE);
            btnNegative.setVisibility((isSecondVisible&&secondBtnName!=null)?View.VISIBLE:View.GONE);

            btnPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onFirst(dialog,v);
                    }
                }
            });

            btnNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onSecond(dialog,v);
                    }
                }
            });
            btnPositive.setText(firstBtnName==null?"":firstBtnName);
            btnNegative.setText(secondBtnName==null?"":secondBtnName);

            if(otherNames != null) {
                final View.OnClickListener otherListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null) {
                            int index = Integer.valueOf((String) v.getTag()).intValue();
                            listener.onOthers(dialog,v,otherNames[index],index);
                        }
                    }
                };
                for (int i = 0 ; i < otherNames.length ; i++) {
                    Button button = rootView.findViewWithTag(String.valueOf(i));
                    button.setText(otherNames[i]);
                    button.setOnClickListener(otherListener);
                    button.setVisibility(View.VISIBLE);
                }
            }
            dialog.setContentView(rootView);
            return dialog;
        }

        public Builder setFirstVisible(boolean firstVisible) {
            isFirstVisible = firstVisible;
            return this;
        }

        public Builder setSecondVisible(boolean secondVisible) {
            isSecondVisible = secondVisible;
            return this;
        }

        public Builder setBackGroundColor(int backGroundColor) {
            this.backGroundColor = backGroundColor;
            return this;
        }

        public Builder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        public Builder setBtnLayoutVisible(boolean btnLayoutVisible) {
            this.isBtnLayoutVisible = btnLayoutVisible;
            return this;
        }

        public Builder setCardElevation(float cardElevation) {
            this.cardElevation = cardElevation;
            return this;
        }
    }

    public static Dialog createLoadingDialog(Activity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_dialog,null);
        Dialog loadingDialog = new DialogFactory.Builder(activity)
                .setContentView(view)
                .setRadius((int) activity.getResources().getDimension(R.dimen.sy_p_50))
                .setBackGroundColor(Color.parseColor("#88ffffff"))
                .setBtnLayoutVisible(false)
                .create(null);
        loadingDialog.setCanceledOnTouchOutside(false);
        return loadingDialog;

    }
}
