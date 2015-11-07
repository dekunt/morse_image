package com.meu.morseimage;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application
{
    private static Context mContext;

    public static Context getAppContext()
    {
        return mContext;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = this;
    }
}
