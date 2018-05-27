package com.lrxliveandreadplayer.demo.manager;

import com.lrxliveandreadplayer.demo.beans.jmessage.Member;
import com.lrxliveandreadplayer.demo.beans.user.UserInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/15.
 */

public class DataManager {
    private static DataManager instance = new DataManager();
    private UserInfoBean userInfo;
    private List<Member> membersList;

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

    public List<Member> getMembersList() {
        if(membersList == null) {
            membersList = new ArrayList<>();
        }
        return membersList;
    }

    public void setMembersList(List<Member> membersList) {
        if(this.membersList != null) {
            this.membersList.clear();
            if(membersList != null) {
                this.membersList.addAll(membersList);
            }
        }
        this.membersList = membersList;
    }
}
