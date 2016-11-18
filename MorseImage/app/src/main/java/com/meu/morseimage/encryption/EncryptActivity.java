package com.meu.morseimage.encryption;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.activity.SwipeActivity;

/**
 * Created by dekunt on 16/11/17.
 */
public class EncryptActivity extends SwipeActivity implements View.OnClickListener {

    private EditText encryptText;
    private EditText encryptText1;
    private EditText encryptText2;
    private EditText encryptText3;
    private EditText encryptText4;
    private EditText encryptText5;

    public static void invoke(Context context) {
        Intent intent = new Intent(context, EncryptActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        encryptText = (EditText) findViewById(R.id.encrypt_text);
        encryptText1 = (EditText) findViewById(R.id.encrypt_text1);
        encryptText2 = (EditText) findViewById(R.id.encrypt_text2);
        encryptText3 = (EditText) findViewById(R.id.encrypt_text3);
        encryptText4 = (EditText) findViewById(R.id.encrypt_text4);
        encryptText5 = (EditText) findViewById(R.id.encrypt_text5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        setRightButton("Done", this);
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_button:
                doWork();
                break;
        }
    }

    private void doWork() {
        int focusedIndex = checkFocusedIndex();
        switch (focusedIndex) {
            case 0: setEncryptText(encryptText.getText().toString(), focusedIndex); break;
            case 1: setEncryptText1(encryptText1.getText().toString(), focusedIndex); break;
            case 2: setEncryptText2(encryptText2.getText().toString(), focusedIndex); break;
            case 3: setEncryptText3(encryptText3.getText().toString(), focusedIndex); break;
            case 4: setEncryptText4(encryptText4.getText().toString(), focusedIndex); break;
            case 5: setEncryptText5(encryptText5.getText().toString(), focusedIndex); break;
        }
    }

    private int checkFocusedIndex() {
        if (encryptText.isFocused()) {
            return 0;
        } else if (encryptText1.isFocused()) {
            return 1;
        } else if (encryptText2.isFocused()) {
            return 2;
        } else if (encryptText3.isFocused()) {
            return 3;
        } else if (encryptText4.isFocused()) {
            return 4;
        } else if (encryptText5.isFocused()) {
            return 5;
        }
        return 0;
    }

    private void setEncryptText(String text, int focusedIndex) {
        if (focusedIndex != 0) {
            encryptText.setText(text);
        } else {
            setEncryptText1(EncryptUtils.reverse(text), focusedIndex);
        }
    }

    private void setEncryptText1(String text, int focusedIndex) {
        if (focusedIndex != 1) {
            encryptText1.setText(text);
        }
        if (focusedIndex <= 1) {
            setEncryptText2(EncryptUtils.encodeFence(text), focusedIndex);
        }
        if (focusedIndex >= 1) {
            setEncryptText(EncryptUtils.reverse(text), focusedIndex);
        }
    }

    private void setEncryptText2(String text, int focusedIndex) {
        if (focusedIndex != 2) {
            encryptText2.setText(text);
        }
        if (focusedIndex <= 2) {
            setEncryptText3(EncryptUtils.encodeKeyboard(text), focusedIndex);
        }
        if (focusedIndex >= 2) {
            setEncryptText1(EncryptUtils.decodeFence(text), focusedIndex);
        }
    }

    private void setEncryptText3(String text, int focusedIndex) {
        if (focusedIndex != 3) {
            encryptText3.setText(text);
        }
        if (focusedIndex <= 3) {
            setEncryptText4(EncryptUtils.encodePhoneKeyboard(text), focusedIndex);
        }
        if (focusedIndex >= 3) {
            setEncryptText2(EncryptUtils.decodeKeyboard(text), focusedIndex);
        }
    }

    private void setEncryptText4(String text, int focusedIndex) {
        if (focusedIndex != 4) {
            encryptText4.setText(text);
        }
        if (focusedIndex <= 4) {
            setEncryptText5(EncryptUtils.encodeMorse(text), focusedIndex);
        }
        if (focusedIndex >= 4) {
            setEncryptText3(EncryptUtils.decodePhoneKeyboard(text), focusedIndex);
        }
    }

    private void setEncryptText5(String text, int focusedIndex) {
        if (focusedIndex != 5) {
            encryptText5.setText(text);
        } else {
            setEncryptText4(EncryptUtils.decodeMorse(text), focusedIndex);
        }
    }
}
