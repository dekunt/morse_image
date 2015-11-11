package com.meu.morseimage.phpTest.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.meu.morseimage.R;


/**
 * Created by luojg on 2015/4/24.
 */
public class CleanableEditText extends EditText
{
    Drawable drawableLeft;
    Drawable drawableRight;

    public CleanableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        drawableLeft = getCompoundDrawables()[0];
        drawableRight = getResources().getDrawable(R.mipmap.ic_edit_clean);
        drawableRight.setBounds(0, 0, drawableRight.getIntrinsicWidth(), drawableRight.getIntrinsicHeight());
        addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setDrawableRight(s.length() > 0 ? drawableRight : null);
            }
        });
    }

    private void setDrawableRight(Drawable drawable) {
        setCompoundDrawables(drawableLeft, null, drawable, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                if (event.getX() > getWidth() - getTotalPaddingRight()) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public static class SimpleTextWatcher implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
