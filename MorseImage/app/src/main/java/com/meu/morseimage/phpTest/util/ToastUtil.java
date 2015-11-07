package com.meu.morseimage.phpTest.util;

import android.content.Context;
import android.widget.Toast;

import com.meu.morseimage.MyApplication;


public class ToastUtil {

    public static void showMsg(String msg)
    {
        Context context = MyApplication.getAppContext();
        if (context == null) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showMsg(int msgResId)
    {
        Context context = MyApplication.getAppContext();
        if (context == null) {
            return;
        }
        Toast.makeText(context, msgResId, Toast.LENGTH_SHORT).show();
    }
}
