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

    public void setMessage(String str) {
        if (!TextUtils.isEmpty(str)) {
            msg_text.setText(str);
        }
        PublicMethod.KeyBoardHidden((Activity) mContext);
    }
}
