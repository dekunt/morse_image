package com.meu.morseimage.phpTest.dialog;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.meu.morseimage.R;


public class LoadDialog extends BaseDialog
{
    private View backgroundView;
    private View progressBar;
    private boolean dismissing = false;

    public LoadDialog(Context context)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        init();
    }

    private void init()
    {
        setContentView(R.layout.dialog_loading);
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        progressBar = findViewById(R.id.progress_bar);
        backgroundView = findViewById(R.id.dialog_outside_view);
        backgroundView.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismiss()
    {
        if (!dismissing)
        {
            dismissing = true;
            progressBar.setVisibility(View.INVISIBLE);
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
            animator.setDuration(200);
            animator.start();
        }
        else
            super.dismiss();
    }

    @Override
    public void show()
    {
        super.show();
        progressBar.setVisibility(View.VISIBLE);
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
                        LoadDialog.super.dismiss();
                    backgroundView.clearAnimation();
                }
            });
        }
    }
}
