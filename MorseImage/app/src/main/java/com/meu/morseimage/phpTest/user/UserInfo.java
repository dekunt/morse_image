package com.meu.morseimage.phpTest.user;

import android.content.Context;
import android.text.TextUtils;

import com.meu.morseimage.MyApplication;
import com.meu.morseimage.phpTest.user.bean.UserInfoBean;

/**
 * Created by dekunt on 15/11/4.
 */
public class UserInfo
{
    private static UserInfo instance;

    private Context mAppContext;

    private UserInfoBean info;


    private UserInfo() {
        this.mAppContext = MyApplication.getAppContext();
        this.info = SharedPrefHelper.getInstance().getUserInfoBean();
    }

    public synchronized static UserInfo getInstance() {
        if (instance == null) {
            instance = new UserInfo();
        }
        return instance;
    }

    public void saveUserInfo(UserInfoBean user) {
        this.info = user;
        SharedPrefHelper.getInstance().saveUserInfo(user.uid, user.username, user.phone, user.hash);
    }

    public String getUid() {
        if (TextUtils.isEmpty(info.uid)) {
            info.uid = SharedPrefHelper.getInstance().sp.getString(SharedPrefHelper.UID, "");
        }
        return TextUtils.isEmpty(info.uid) ? "0" : info.uid;
    }

    public boolean isLogin() {
        if (TextUtils.isEmpty(info.uid)) {
            info.uid = SharedPrefHelper.getInstance().sp.getString(SharedPrefHelper.UID, "");
        }
        return !TextUtils.isEmpty(info.uid);
    }

    /**
     * 获取用户登录后的hash值
     */
    public String getHash() {
        if (TextUtils.isEmpty(info.hash)) {
            info.hash = SharedPrefHelper.getInstance().sp.getString(SharedPrefHelper.HASH, "");
        }
        return info.hash;
    }

    /**
     * 清除登录用户的信息，保留游客状态所需要的信息
     */
    public void clearLoginInfo() {
        SharedPrefHelper.getInstance().cleanUserInfo();
        info = SharedPrefHelper.getInstance().getUserInfoBean();
    }
}
