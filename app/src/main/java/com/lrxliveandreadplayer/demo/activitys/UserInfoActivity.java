package com.lrxliveandreadplayer.demo.activitys;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lrx.live.player.R;
import com.lrxliveandreadplayer.demo.beans.BaseResp;
import com.lrxliveandreadplayer.demo.beans.jmessage.UserInfo;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;
import com.lrxliveandreadplayer.demo.beans.user.UserResp;
import com.lrxliveandreadplayer.demo.glide.GlideCircleTransform;
import com.lrxliveandreadplayer.demo.manager.DataManager;
import com.lrxliveandreadplayer.demo.network.NetWorkMg;
import com.lrxliveandreadplayer.demo.network.RequestApi;
import com.lrxliveandreadplayer.demo.utils.Tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2018/5/14.
 */

public class UserInfoActivity extends Activity {
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    @BindView(R.id.edit_nickName)
    EditText mEditNickName;
    @BindView(R.id.image_head)
    ImageView mImageHead;
    @BindView(R.id.btn_edit)
    Button mBtnEdit;
    @BindView(R.id.linear_headImage)
    View mLinearHeadImage;

    @BindView(R.id.edit_age)
    EditText mEditAge;
    @BindView(R.id.edit_gender)
    EditText mEditGender;
    @BindView(R.id.edit_tall)
    EditText mEditTall;
    @BindView(R.id.edit_scholling)
    EditText mEditScholling;
    @BindView(R.id.edit_professional)
    EditText mEditProfess;
    @BindView(R.id.edit_nativePlace)
    EditText mEditNative;
    @BindView(R.id.edit_marrige)
    EditText mEditMarrige;
    @BindView(R.id.edit_jobAddress)
    EditText mEditJobAddress;
    @BindView(R.id.edit_phone)
    EditText mEditPhone;
    @BindView(R.id.edit_roleType)
    EditText mEditRoleType;

    private RequestApi mApi;
    private UserInfoBean mUserInfo;
    private Drawable mEditDrawable;
    private boolean isEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mEditDrawable = mEditNickName.getBackground();
        Retrofit retrofit = NetWorkMg.newRetrofit();
        mApi = retrofit.create(RequestApi.class);
        Bundle bundle = getIntent().getExtras();
        boolean isRegist = false;
        if(bundle != null) {
            isRegist = bundle.getBoolean("isRegist",false);
        }


        mUserInfo = DataManager.getInstance().getUserInfo();
        setUserInfo(mUserInfo);
        setEdit(isRegist);
    }

    @OnClick(R.id.btn_commit)
    void onCommit() {
        updateUserInfo();
    }

    @OnClick(R.id.btn_edit)
    void onBtnEdit() {
        setEdit(!isEdit);
    }

    @OnClick(R.id.linear_headImage)
    void onHeadImage() {
        openImageSelector();
    }

    private int parseInt(String str) {
        int result = 0;
        try {
            result = Integer.parseInt(str);
        }catch (Exception e) {
            Log.e("yy",e.toString());
        }
        return result;
    }

    private void updateUserInfo() {
        mUserInfo.setNick_name(mEditNickName.getText().toString());
        mUserInfo.setAge(parseInt(mEditAge.getText().toString()));
        mUserInfo.setGender(mEditGender.getText().toString());
        mUserInfo.setJob_address(mEditJobAddress.getText().toString());
        int marrige = 0;
        if(mEditMarrige.getText().toString().contains("已婚")) {
            marrige = 1;
        }
        mUserInfo.setMarrige(marrige);
        mUserInfo.setNative_place(mEditNative.getText().toString());
        mUserInfo.setPhone(mEditPhone.getText().toString());
        mUserInfo.setProfessional(mEditProfess.getText().toString());
        mUserInfo.setScholling(mEditScholling.getText().toString());
        mUserInfo.setTall(parseInt(mEditTall.getText().toString()));
        mUserInfo.setRole_type(mEditRoleType.getText().toString());

        Map<String,Object> params = new HashMap<>();
        params.put("userName",mUserInfo.getUser_name());
        params.put("nickName",mUserInfo.getNick_name());
        params.put("gender",mUserInfo.getGender());
        params.put("age",mUserInfo.getAge());
        params.put("tall",mUserInfo.getTall());
        params.put("scholling",mUserInfo.getScholling());
        params.put("professional",mUserInfo.getProfessional());
        params.put("native_place",mUserInfo.getNative_place());
        params.put("marrige",mUserInfo.getMarrige());
        params.put("job_address",mUserInfo.getJob_address());
        params.put("phone",mUserInfo.getPhone());
        params.put("role_type",mUserInfo.getRole_type());

        mApi.updateUserInfo(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        Log.e("yy",new Gson().toJson(userResp));
                        DataManager.getInstance().setUserInfo(userResp.getData());
                        setUserInfo(DataManager.getInstance().getUserInfo());
                        setEdit(false);
                        Tools.toast(getApplicationContext(),"更新成功",false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("yy",throwable.toString());
                        Tools.toast(UserInfoActivity.this,throwable.toString(),true);
                    }
                });
    }

    private void setEdit(boolean edit) {
        isEdit = edit;
        if(isEdit) {
            mBtnEdit.setText("取消");
            mBtnCommit.setVisibility(View.VISIBLE);
            mLinearHeadImage.setClickable(true);

            mEditNickName.setBackground(mEditDrawable);
            mEditMarrige.setBackground(mEditDrawable);
            mEditJobAddress.setBackground(mEditDrawable);
            mEditAge.setBackground(mEditDrawable);
            mEditGender.setBackground(mEditDrawable);
            mEditNative.setBackground(mEditDrawable);
            mEditPhone.setBackground(mEditDrawable);
            mEditProfess.setBackground(mEditDrawable);
            mEditTall.setBackground(mEditDrawable);
            mEditScholling.setBackground(mEditDrawable);
            mEditRoleType.setBackground(mEditDrawable);

            mEditNickName.setEnabled(true);
            mEditMarrige.setEnabled(true);
            mEditJobAddress.setEnabled(true);
            mEditAge.setEnabled(true);
            mEditGender.setEnabled(true);
            mEditNative.setEnabled(true);
            mEditPhone.setEnabled(true);
            mEditProfess.setEnabled(true);
            mEditTall.setEnabled(true);
            mEditScholling.setEnabled(true);
            mEditRoleType.setEnabled(true);
        }else {
            mBtnEdit.setText("编辑");
            mBtnCommit.setVisibility(View.GONE);
            mLinearHeadImage.setClickable(false);

            mEditNickName.setBackground(null);
            mEditMarrige.setBackground(null);
            mEditJobAddress.setBackground(null);
            mEditAge.setBackground(null);
            mEditGender.setBackground(null);
            mEditNative.setBackground(null);
            mEditPhone.setBackground(null);
            mEditProfess.setBackground(null);
            mEditTall.setBackground(null);
            mEditScholling.setBackground(null);
            mEditRoleType.setBackground(null);

            mEditNickName.setEnabled(false);
            mEditMarrige.setEnabled(false);
            mEditJobAddress.setEnabled(false);
            mEditAge.setEnabled(false);
            mEditGender.setEnabled(false);
            mEditNative.setEnabled(false);
            mEditPhone.setEnabled(false);
            mEditProfess.setEnabled(false);
            mEditTall.setEnabled(false);
            mEditScholling.setEnabled(false);
            mEditRoleType.setEnabled(false);
        }
    }

    private void openImageSelector() {
        RxGalleryFinal
                .with(this)
                .image()
                .radio()
                .crop()
                .imageLoader(ImageLoaderType.GLIDE)
                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        //图片选择结果
                        Log.e("yy",new Gson().toJson(imageRadioResultEvent));
                        upLoadHeadImage(imageRadioResultEvent.getResult().getThumbnailSmallPath(),mUserInfo.getUser_name());
                    }
                })
                .openGallery();
    }

    private void upLoadHeadImage(String imagePath,String userName) {
        File file = new File(imagePath);

        Map<String, RequestBody> params = new HashMap<>();
        //以下参数是伪代码，参数需要换成自己服务器支持的
        params.put("userName", RequestBody.create(MediaType.parse("text/plain"),userName));

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("uploadFile", file.getName(), requestFile);
        mApi.uploadFile(params,body).enqueue(new Callback<UserResp>() {
            @Override
            public void onResponse(Call<UserResp> call, Response<UserResp> response) {
                Tools.toast(UserInfoActivity.this,"设置头像成功",false);
                DataManager.getInstance().setUserInfo(response.body().getData());

                Glide.with(UserInfoActivity.this)
                        .load(response.body().getData().getHead_image())
                        .centerCrop()
                        .bitmapTransform(new GlideCircleTransform(UserInfoActivity.this))
                        .into(mImageHead);
            }

            @Override
            public void onFailure(Call<UserResp> call, Throwable throwable) {
                Log.e("yy",throwable.getMessage());
                Tools.toast(UserInfoActivity.this,throwable.getMessage(),true);
            }
        });
    }

    private void setUserInfo(UserInfoBean info) {
        if(info == null) return;

        mEditNickName.setText(info.getNick_name());
        mEditMarrige.setText(info.getMarrige()==1?"已婚":"未婚");
        mEditJobAddress.setText(info.getJob_address());
        mEditAge.setText(String.valueOf(info.getAge()));
        mEditGender.setText(info.getGender());
        mEditNative.setText(info.getNative_place());
        mEditPhone.setText(info.getPhone());
        mEditProfess.setText(info.getProfessional());
        mEditTall.setText(String.valueOf(info.getTall()));
        mEditScholling.setText(info.getScholling());
        mEditRoleType.setText(info.getRole_type());
        Glide.with(UserInfoActivity.this)
                .load(info.getHead_image())
                .centerCrop()
                .bitmapTransform(new GlideCircleTransform(UserInfoActivity.this))
                .into(mImageHead);
    }
}
