package com.lrxliveandreadplayer.demo.manager;

import com.lrxliveandreadplayer.demo.beans.jmessage.Data;
import com.lrxliveandreadplayer.demo.beans.jmessage.Member;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;

/**
 * Created by Administrator on 2018/5/15.
 */

public class DataManager {
    private static DataManager instance = new DataManager();
    private UserInfoBean userInfo;
    private Data chartData;
    private int pushAddressType;

    private DataManager(){}

    public static DataManager getInstance() {
        return instance;
    }

    public UserInfoBean getUserInfo() {
        if(userInfo == null) {
            userInfo = new UserInfoBean();
        }
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public Data getChartData() {
        if(chartData == null) {
            chartData = new Data();
        }
        return chartData;
    }

    public void setChartData(Data chartData) {
        this.chartData = chartData;
    }

    public Member getSelfMember() {
        for(Member member:getChartData().getMembers()) {
            if(member.getUserInfo().getUser_name().equals(getUserInfo().getUser_name())) {
                return member;
            }
        }
        return new Member();
    }

    public int getPushAddressType() {
        return pushAddressType;
    }

    public void setPushAddressType(int pushAddressType) {
        this.pushAddressType = pushAddressType;
    }
}
