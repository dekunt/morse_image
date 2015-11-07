package com.meu.morseimage.phpTest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.meu.morseimage.BaseActivity;
import com.meu.morseimage.R;

/**
 * Created by dekunt on 15/11/5.
 */
public class ChangePswActivity extends BaseActivity
{

    public static void invoke(Context context)
    {
        Intent intent = new Intent(context, ChangePswActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);
    }
}
