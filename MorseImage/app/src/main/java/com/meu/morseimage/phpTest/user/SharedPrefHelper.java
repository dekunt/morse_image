package com.meu.morseimage.phpTest.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.meu.morseimage.MyApplication;
import com.meu.morseimage.phpTest.user.bean.UserInfoBean;


public class SharedPrefHelper
{
    public static final String NAME = "my_php_test";

    /**
     * 用户信息
     */
    public static final String UID = "uid";
    public static final String USERNAME = "username";
    public static final String PHONE = "phone";
    public static final String HASH = "hash";

    private static SharedPrefHelper mInstance;
    public SharedPreferences sp;

    private SharedPrefHelper(Context context) {
        sp = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
    }

    public static SharedPrefHelper getInstance() {
        if (null == mInstance) {
            Context ctx = MyApplication.getAppContext();
            mInstance = new SharedPrefHelper(ctx);
        }
        return mInstance;
    }

    /**
     * 写入数据
     */
    public void setValue(String key, String value) {
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 写入Int
     */
    public void setIntValue(String key, int value) {
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 写入long
     */
    public void setLongValue(String key, long value) {
        Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * UserInfo
     */

    public UserInfoBean getUserInfoBean() {
        UserInfoBean bean = new UserInfoBean();
        bean.uid = sp.getString(UID, "");
        bean.username = sp.getString(USERNAME, "");
        bean.phone = sp.getString(PHONE, "");
        bean.hash = sp.getString(HASH, "");
        return bean;
    }

    public void saveUserInfo(String uid, String userName, String phone, String hash) {
        Editor editor = sp.edit();
        editor.putString(HASH, hash);
        editor.putString(PHONE, phone);
        editor.putString(USERNAME, userName);
        editor.putString(UID, uid);
        editor.apply();
    }

    public void cleanUserInfo() {
        Editor editor = sp.edit();
        editor.putString(HASH, "");
        editor.putString(PHONE, "");
        editor.putString(USERNAME, "");
        editor.putString(UID, "");
        editor.apply();
    }

    public void cleanAll() {
        sp.edit().clear().apply();
    }

    public void remove(String key) {
        sp.edit().remove(key).apply();
    }
}
