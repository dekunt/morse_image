package com.meu.morseimage.phpTest.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.util.PublicMethod;


public class LoadDialog extends Dialog
{

    TextView msg_text;

    Context mContext;

    View loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        msg_text = (TextView) findViewById(R.id.msg);
        loading = findViewById(R.id.loading);
    }

    public LoadDialog(Context context) {
        super(context, R.style.load_dialog_style);
        mContext = context;
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        super.show();
        loading.setBackgroundResource(R.drawable.dialog_border);
        loading.setPadding(30,0,30,10);
    }

    public void show(boolean hasborder){
        if(!hasborder){
            super.show();
            loading.setBackgroundResource(R.color.transparent);
            loading.setPadding(0,0,0,0);
        }else {
            show();
        }
    }
    public void setMessage(String str) {
        if (!TextUtils.isEmpty(str)) {
            msg_text.setText(str);
        }
        PublicMethod.KeyBoardHidden((Activity) mContext);
    }

    /**
     * 即使弹出加载框也显示键盘
     *
     * @param str
     */
    public void setMessageDisplayKey(String str) {
        msg_text.setText(str);
    }


}
