package com.meu.morseimage.phpTest.dialog;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.meu.morseimage.R;

/**
 * Created by dekunt on 15/8/26.
 */
public class PopupButtonsDialog extends BaseDialog
{
    private View.OnClickListener button1Listener;
    private View.OnClickListener button2Listener;

    private View button1;
    private View button2;
    private TextView button1TextView;
    private TextView button2TextView;

    private View cancelButton;
    private View backgroundView;
    private View bottomGroup;
    private boolean dismissing = false;
    private boolean needDismissAnimation = true;

    public PopupButtonsDialog(Context context)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        init();
    }

    public PopupButtonsDialog(Context context, int theme)
    {
        super(context, theme);
        init();
    }

    private void init()
    {
        setContentView(R.layout.dialog_popup_buttons);
        setCancelable(true);
        setCanceledOnTouchOutside(true, R.id.dialog_outside_view);

        backgroundView = findViewById(R.id.dialog_outside_view);
        bottomGroup = findViewById(R.id.dialog_bottom_group);
        button1 = findViewById(R.id.button_1);
        button2 = findViewById(R.id.button_2);
        button1TextView = (TextView) findViewById(R.id.button_1_text_view);
        button2TextView = (TextView) findViewById(R.id.button_2_text_view);

        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (button1Listener != null)
                    button1Listener.onClick(v);
                cancel();
            }
        });

        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (button2Listener != null)
                    button2Listener.onClick(v);
                cancel();
            }
        });

        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cancel();
            }
        });
    }

    public void setButton1(String title, View.OnClickListener listener)
    {
        this.button1Listener = listener;
        button1.setVisibility(View.VISIBLE);
        button1TextView.setText(title);
    }

    public void setButton1(int titleResId, View.OnClickListener listener)
    {
        this.button1Listener = listener;
        button1.setVisibility(View.VISIBLE);
        button1TextView.setText(titleResId);
    }

    public void setButton2(int titleResId, View.OnClickListener listener)
    {
        this.button2Listener = listener;
        button2.setVisibility(View.VISIBLE);
        button2TextView.setText(titleResId);
    }

    public void setOnClickCanceListener(View.OnClickListener onClickListener) {
        cancelButton.setOnClickListener(onClickListener);
    }

    @Override
    public void dismiss()
    {
        if (!dismissing && needDismissAnimation)
        {
            dismissing = true;
            final View dialogGroup = findViewById(R.id.dialog_group);
            Animation alphaAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
            backgroundView.startAnimation(alphaAnimation);
            Animation popupAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_out);
            bottomGroup.startAnimation(popupAnimation);
            popupAnimation.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {
                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    dialogGroup.setVisibility(View.GONE);
                    new Handler().post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            PopupButtonsDialog.super.dismiss();
                            bottomGroup.clearAnimation();
                            backgroundView.clearAnimation();
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {

                }
            });
        }
        else
            super.dismiss();
    }


    @Override
    public void show()
    {
        super.show();
        Animation alphaAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        backgroundView.startAnimation(alphaAnimation);
        Animation popupAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
        bottomGroup.startAnimation(popupAnimation);
        popupAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                bottomGroup.clearAnimation();
                backgroundView.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });
    }
}
