package com.meu.morseimage.phpTest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.meu.morseimage.BaseActivity;
import com.meu.morseimage.R;

/**
 * Created by dekunt on 15/11/5.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener
{

    public static void invoke(Context context)
    {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.ll_find_pwd).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean result = super.onCreateOptionsMenu(menu);
        setRightButton("注册", this);
        return result;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.right_button: RegistActivity.invoke(this); break;
            case R.id.btn_login: break;
            case R.id.ll_find_pwd: ChangePswActivity.invoke(this); break;
        }
    }
}
