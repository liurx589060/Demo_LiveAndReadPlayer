package com.lrxliveandreadplayer.demo.manager;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.activitys.XqChartActivity;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;
import com.lrxliveandreadplayer.demo.glide.GlideCircleTransform;
import com.lrxliveandreadplayer.demo.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/26.
 */

public class XqChartUIViewMg implements IXqChartView {
    private View mRootView;
    private int SPACE_TOP = 0;

    private XqChartActivity mXqActivity;
    private List<UserInfoBean> mAngelMembers;
    private List<UserInfoBean> mManMembers;
    private List<UserInfoBean> mLadyMembers;

    private RecyclerView mRecyclerMembers;
    private RecyclerView mRecyclerSystem;


    private MemberRecyclerdapter mMemberAdapter;
    private SystemRecyclerdapter mSystemAdapter;

    @Override
    public View createView() {
        if(mRootView == null) {
            mRootView = LayoutInflater.from(mXqActivity).inflate(R.layout.layout_chart_ui,null);
        }
        return mRootView;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    private void initMemberRecyclerView() {
        mMemberAdapter = new MemberRecyclerdapter();
        mRecyclerMembers.setLayoutManager(new GridLayoutManager(mXqActivity,2));
        mRecyclerMembers.setAdapter(mMemberAdapter);
        mRecyclerMembers.addItemDecoration(new MemberSpaceDecoration());
    }

    private void initSystemRecyclerView() {
        mSystemAdapter = new SystemRecyclerdapter();
        mRecyclerSystem.setLayoutManager(new LinearLayoutManager(mXqActivity));
        mRecyclerSystem.setAdapter(mSystemAdapter);
    }

    public XqChartUIViewMg(XqChartActivity xqActivity) {
        this.mXqActivity = xqActivity;
        mAngelMembers = new ArrayList<>();
        mManMembers = new ArrayList<>();
        mLadyMembers = new ArrayList<>();
        SPACE_TOP = Tools.dip2px(mXqActivity,15);

        mRootView = LayoutInflater.from(mXqActivity).inflate(R.layout.layout_chart_ui,null);
        mRecyclerMembers = mRootView.findViewById(R.id.recycle_chart_members);
        mRecyclerSystem = mRootView.findViewById(R.id.recycle_chart_system);

        initMemberRecyclerView();
        initSystemRecyclerView();
    }

    private class MemberRecyclerdapter extends RecyclerView.Adapter<MemberViewHolder> {
        @Override
        public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MemberViewHolder viewHolder = new MemberViewHolder(LayoutInflater.from(mXqActivity)
                    .inflate(R.layout.layout_chart_ui_head,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MemberViewHolder holder, int position) {
            holder.mTxvNum.setText(String.valueOf(position+1));
            Glide.with(mXqActivity)
                    .load(R.drawable.ic_launcher)
                    .bitmapTransform(new GlideCircleTransform(mXqActivity))
                    .into(holder.mImgHead);
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    private class SystemRecyclerdapter extends RecyclerView.Adapter<SystemRecyclerdapter.SystemViewHolder> {
        @Override
        public SystemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SystemViewHolder viewHolder = new SystemViewHolder(LayoutInflater.from(mXqActivity)
                    .inflate(R.layout.layout_chart_ui_item_system,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SystemViewHolder holder, int position) {
            holder.mTxvEventTime.setText(Tools.getCurrentDateTime());
        }

        @Override
        public int getItemCount() {
            return 30;
        }

        public class SystemViewHolder extends RecyclerView.ViewHolder {
            public TextView mTxvEvent;
            public TextView mTxvEventTime;

            public SystemViewHolder(View itemView) {
                super(itemView);
                mTxvEventTime = itemView.findViewById(R.id.text_event_time);
                mTxvEvent = itemView.findViewById(R.id.text_event);
            }
        }
    }

    private class MemberViewHolder extends RecyclerView.ViewHolder {
        public TextView mTxvNum;
        public ImageView mImgHead;

        public MemberViewHolder(View itemView) {
            super(itemView);
            mTxvNum = itemView.findViewById(R.id.text_num);
            mImgHead = itemView.findViewById(R.id.img_head);

            setImageHeight(mImgHead);
        }

        private void setImageHeight(View view) {
            int count = 10/2;
            int height = (mRecyclerMembers.getHeight() - SPACE_TOP*count)/count;
            ViewGroup.LayoutParams params = mImgHead.getLayoutParams();
            params.width = height;
            params.height = height;
            view.setLayoutParams(params);
        }
    }

    private class MemberSpaceDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int index = parent.getChildAdapterPosition(view);
            outRect.top = SPACE_TOP;
            outRect.bottom = 0;
            outRect.right = 0;
            if(index%2 != 0) {
                int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                view.measure(w, h);
                int width = view.getMeasuredWidth();
                int delta = parent.getWidth()/2 - width;
                outRect.left = delta;
            }
        }
    }
}
