package com.meu.morseimage.phpTest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.meu.morseimage.BaseActivity;
import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.dialog.LoadDialog;
import com.meu.morseimage.phpTest.http.RequestHelper;
import com.meu.morseimage.phpTest.http.RequestManager;
import com.meu.morseimage.phpTest.http.ResponseListener;
import com.meu.morseimage.phpTest.http.ServerRequest;
import com.meu.morseimage.phpTest.http.UrlPath;
import com.meu.morseimage.phpTest.user.UserInfo;
import com.meu.morseimage.phpTest.user.bean.UserInfoBean;
import com.meu.morseimage.phpTest.util.StringUtil;
import com.meu.morseimage.phpTest.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dekunt on 15/11/5.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener
{
    private EditText etUserName;
    private EditText etPassword;

    private String mUserName;
    private String mPassword;
    private LoadDialog mLoadDialog;

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
        mLoadDialog = new LoadDialog(this);

        etUserName = (EditText) findViewById(R.id.et_user_name);
        etPassword = (EditText) findViewById(R.id.et_password);
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
            case R.id.btn_login: onClickLogin(); break;
            case R.id.ll_find_pwd: ChangePswActivity.invoke(this); break;
        }
    }


    private void onClickLogin()
    {
        mUserName = etUserName.getText().toString().trim();
        mPassword = etPassword.getText().toString().trim();
        if(StringUtil.isNull(mUserName)){
            ToastUtil.showMsg("用户名/手机号为空");
            return;
        }
        if(StringUtil.isNull(mPassword) || StringUtil.isContainChinese(mPassword) || mPassword.length() < 6){
            ToastUtil.showMsg("输入密码格式错误");
            return;
        }
        reqLogin();
    }


    /**
     * 用户登录请求
     */
    private void reqLogin() {
        mLoadDialog.show();
        mLoadDialog.setMessage("登录中...");

        Map<String, Object> params = new HashMap<>();
        params.put("nameOrPhone", mUserName);
        params.put("password", StringUtil.Md5(mPassword));
        ServerRequest request = new ServerRequest<>(
                UrlPath.LOGIN,
                RequestHelper.buildPublicParams(params),
                UserInfoBean.class,
                new ResponseListener<UserInfoBean>()
                {
                    @Override
                    public void onNetworkComplete() {
                        if (mLoadDialog.isShowing()) {
                            mLoadDialog.dismiss();
                        }
                    }

                    @Override
                    protected void onSucc(String url, UserInfoBean result) {
                        if (result == null) {
                            ToastUtil.showMsg("数据异常，请稍后再试");
                            return;
                        }
                        UserInfo.getInstance().saveUserInfo(result);
                        ToastUtil.showMsg("登录成功");
                        onLoginSucceed();
                    }
                });
        RequestManager.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // 判断注册成功
        if (UserInfo.getInstance().isLogin())
            onLoginSucceed();
    }

    private void onLoginSucceed() {
        NoteListActivity.invoke(this);
        finish();
    }
}
