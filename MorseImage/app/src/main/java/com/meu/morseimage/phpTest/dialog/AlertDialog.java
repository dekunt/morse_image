package com.meu.morseimage.phpTest.dialog;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.meu.morseimage.R;

/**
 * Created by dekunt on 15/11/11.
 */
public class AlertDialog extends BaseDialog
{
    private TextView titleText;
    private View okButton;
    private View backgroundView;
    private View alertGroup;
    private boolean dismissing = false;

    public AlertDialog(Context context)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        init();
    }

    private void init()
    {
        setContentView(R.layout.dialog_alert);
        setCancelable(true);
        setCanceledOnTouchOutside(true, R.id.dialog_outside_view);

        backgroundView = findViewById(R.id.dialog_outside_view);
        alertGroup = findViewById(R.id.alert_group);
        titleText = (TextView)findViewById(R.id.tv_desc);
        okButton = findViewById(R.id.ok_button);
        View cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cancel();
            }
        });
    }

    public void setTitle(String title, View.OnClickListener listener)
    {
        titleText.setText(title);
        okButton.setOnClickListener(listener);
    }


    @Override
    public void dismiss()
    {
        if (!dismissing)
        {
            dismissing = true;
            alertGroup.setVisibility(View.INVISIBLE);
            ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    float value = (Float) animation.getAnimatedValue();
                    backgroundView.setAlpha(value);
                }
            });
            animator.addListener(new MyAnimatorListener(true));
            animator.setDuration(300);
            animator.start();
        }
        else
            super.dismiss();
    }

    @Override
    public void show()
    {
        super.show();
        alertGroup.setVisibility(View.VISIBLE);
        backgroundView.setVisibility(View.VISIBLE);
        dismissing = false;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (Float) animation.getAnimatedValue();
                backgroundView.setAlpha(value);
            }
        });
        animator.addListener(new MyAnimatorListener(false));
        animator.setDuration(200);
        animator.start();
    }


    private class MyAnimatorListener implements Animator.AnimatorListener
    {
        private boolean toDismiss = false;

        public MyAnimatorListener(boolean toDismiss)
        {
            this.toDismiss = toDismiss;
        }

        @Override
        public void onAnimationEnd(Animator animation)
        {
            onDismissAnimationEnd();
        }

        @Override
        public void onAnimationCancel(Animator animation)
        {
            onDismissAnimationEnd();
        }

        @Override
        public void onAnimationStart(Animator animation)
        {
        }

        @Override
        public void onAnimationRepeat(Animator animation)
        {
        }

        private void onDismissAnimationEnd()
        {
            if (toDismiss)
                backgroundView.setVisibility(View.GONE);
            new Handler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (toDismiss)
                        AlertDialog.super.dismiss();
                    backgroundView.clearAnimation();
                }
            });
        }
    }

}
