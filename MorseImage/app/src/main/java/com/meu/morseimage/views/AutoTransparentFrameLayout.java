package com.meu.morseimage.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;


/**
 * A simple layout to wrap user widget, the content will be partially transparent when pressed
 */
public class AutoTransparentFrameLayout extends FrameLayout
{
    private static final float pressedAlpha = 0.8f;

    public AutoTransparentFrameLayout(Context context)
    {
        super(context);
    }

    public AutoTransparentFrameLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AutoTransparentFrameLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed)
    {
        if (pressed == isPressed())
            return;
        setAlpha(pressed ? pressedAlpha * getAlpha() : getAlpha() / pressedAlpha);
        super.setPressed(pressed);
    }

}

