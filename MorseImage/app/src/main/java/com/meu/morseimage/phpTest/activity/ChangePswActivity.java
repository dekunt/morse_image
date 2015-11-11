package com.meu.morseimage.phpTest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.meu.morseimage.BaseActivity;
import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.dialog.LoadDialog;
import com.meu.morseimage.phpTest.http.RequestHelper;
import com.meu.morseimage.phpTest.http.RequestManager;
import com.meu.morseimage.phpTest.http.ResponseListener;
import com.meu.morseimage.phpTest.http.Result;
import com.meu.morseimage.phpTest.http.ServerRequest;
import com.meu.morseimage.phpTest.http.UrlPath;
import com.meu.morseimage.phpTest.user.SharedPrefHelper;
import com.meu.morseimage.phpTest.user.bean.BaseBean;
import com.meu.morseimage.phpTest.util.StringUtil;
import com.meu.morseimage.phpTest.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dekunt on 15/11/5.
 */
public class ChangePswActivity extends BaseActivity implements View.OnClickListener
{
    private static final String SHARED_PREF_PSW_PHONE = "change_psw_phone";
    private static final String SHARED_PREF_PSW_CODE_TIME = "change_psw_code_time";

    private static final int mDefaultInterval = 60;
    private static final String mCodeBtnTxt = "获取验证码";

    private EditText etPhone;
    private EditText etCode;
    private EditText etPassword;
    private Button btnGetCode;

    private String mPhoneNum;
    private String mCode;
    private String mPassword;
    private long mCountdown = 0;
    private boolean mStat = true;
    private LoadDialog mLoadDialog;
    final Handler mHandler = new CountHandler();

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
        mLoadDialog = new LoadDialog(this);

        etPhone = (EditText) findViewById(R.id.et_phone);
        etCode = (EditText) findViewById(R.id.et_code);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnGetCode = (Button) findViewById(R.id.btn_get_code);
        btnGetCode.setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        initData();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mStat = false;
        super.onDestroy();
    }

    public void initData(){
        long lastTime = SharedPrefHelper.getInstance().sp.getLong(SHARED_PREF_PSW_CODE_TIME, 0);
        long nowTime = System.currentTimeMillis() / 1000;
        long interval = lastTime == 0 ? mDefaultInterval : nowTime - lastTime;
        if (interval < mDefaultInterval && interval > 0) {
            mCountdown = mDefaultInterval - interval;
            new Thread(new CountRunnable()).start();
            String phoneNum = SharedPrefHelper.getInstance().sp.getString(SHARED_PREF_PSW_PHONE, "");
            etPhone.setText(phoneNum);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save: onClickSave(); break;
            case R.id.btn_get_code: onClickGetCode(); break;
        }
    }

    public void onClickGetCode(){
        mPhoneNum = etPhone.getText().toString().trim();
        if(!StringUtil.isMobileNO(mPhoneNum)){
            ToastUtil.showMsg("输入手机号码格式错误");
            return;
        }
        reqVerificationCode();
    }

    private void onClickSave()
    {
        mPhoneNum = etPhone.getText().toString().trim();
        mPassword = etPassword.getText().toString().trim();
        mCode = etCode.getText().toString().trim();
        if(!StringUtil.isMobileNO(mPhoneNum)){
            ToastUtil.showMsg("输入手机号码格式错误");
            return;
        }
        if(StringUtil.isNull(mCode)){
            ToastUtil.showMsg("手机验证码不能为空");
            return;
        }
        if(StringUtil.isNull(mPassword) || StringUtil.isContainChinese(mPassword) || mPassword.length() < 6){
            ToastUtil.showMsg("输入密码格式错误");
            return;
        }
        reqChangePsw();
    }


    /**
     * 发送修改密码请求
     */
    private void reqChangePsw() {
        mLoadDialog.show();

        Map<String, Object> params = new HashMap<>();
        params.put("phone", mPhoneNum);
        params.put("password", StringUtil.Md5(mPassword));
        params.put("vCode", mCode);
        ServerRequest request = new ServerRequest<>(
                UrlPath.CHANGE_PSW,
                RequestHelper.buildPublicParams(params),
                BaseBean.class,
                new ResponseListener<BaseBean>()
                {
                    @Override
                    public void onNetworkComplete() {
                        if (mLoadDialog.isShowing()) {
                            mLoadDialog.dismiss();
                        }
                    }

                    @Override
                    protected void onSucc(String url, BaseBean result) {
                        ToastUtil.showMsg("密码修改成功");
                        finish();
                    }
                });
        RequestManager.getInstance(this).addToRequestQueue(request);
    }

    /**
     * 获取手机验证码请求
     */
    private void reqVerificationCode() {
        startCountdown();

        Map<String, Object> params = new HashMap<>();
        params.put("phone", mPhoneNum);
        params.put("action", "2");
        ServerRequest request = new ServerRequest<>(
                UrlPath.GET_V_CODE,
                RequestHelper.buildPublicParams(params),
                BaseBean.class,
                new ResponseListener<BaseBean>()
                {
                    @Override
                    protected void onSucc(String url, BaseBean result) {
                        ToastUtil.showMsg("验证码已发送成功");
                    }

                    @Override
                    protected void onError(String url, Result.ErrorMsg errorMsg) {
                        super.onError(url, errorMsg);
                        resetCountdown();
                    }

                    @Override
                    protected void onFail(int errorType, String errorDesc) {
                        super.onFail(errorType, errorDesc);
                        resetCountdown();
                    }
                });
        RequestManager.getInstance(this).addToRequestQueue(request);
    }


    private void startCountdown() {
        btnGetCode.setEnabled(false);
        SharedPrefHelper.getInstance().setLongValue(SHARED_PREF_PSW_CODE_TIME, System.currentTimeMillis() / 1000);
        SharedPrefHelper.getInstance().setValue(SHARED_PREF_PSW_PHONE, mPhoneNum);
        mCountdown = mDefaultInterval;
        new Thread(new CountRunnable()).start();
    }

    private void resetCountdown() {
        SharedPrefHelper.getInstance().setLongValue(SHARED_PREF_PSW_CODE_TIME, 0);
        mCountdown = 0;
        Message message = new Message();
        message.what = 1;
        mHandler.sendMessage(message);
    }

    private class CountHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(mCountdown <= 0 ){
                        btnGetCode.setEnabled(true);
                        btnGetCode.setText(mCodeBtnTxt);
                        btnGetCode.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else{
                        btnGetCode.setText("重新获取" + mCountdown + "s");
                        btnGetCode.setTextColor(getResources().getColor(R.color.grey));
                        btnGetCode.setEnabled(false);
                    }
            }
            super.handleMessage(msg);
        }
    }

    private class CountRunnable implements Runnable
    {
        @Override
        public void run() {
            while (mStat && mCountdown > 0) {
                try {
                    Message message = new Message();
                    message.what = 1;
                    mCountdown -= 1;
                    mHandler.sendMessage(message);
                    Thread.sleep(1000); // sleep 1000ms
                } catch (Exception ignored) {
                }
            }
        }
    }
}
