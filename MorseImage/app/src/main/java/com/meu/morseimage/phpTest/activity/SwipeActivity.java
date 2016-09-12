package com.meu.morseimage.phpTest.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.meu.morseimage.BaseActivity;
import com.meu.morseimage.R;

/**
 * Created by dekunt on 15/9/15.
 * 右划返回
 */
public class SwipeActivity extends BaseActivity {
    private SwipeLayout swipeLayout;

    // 不需要滑动关闭的Activity，重写该函数
    protected boolean isSwipeEnabled() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swipeLayout = new SwipeLayout(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        swipeLayout.replaceLayer(this);
    }

    @Override
    public void finish() {
        super.finish();
        if (isSwipeEnabled()) {
            overridePendingTransition(R.anim.half_left_in, R.anim.right_out);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.half_left_out);
    }

    /**
     * SwipeLayout
     */
    private class SwipeLayout extends FrameLayout {

        private final GestureDetector gestureDetector;
        private View content;

        public SwipeLayout(Context context) {
            super(context);
            inflate(context, R.layout.activity_swipe, this);
            gestureDetector = new GestureDetector(context, new GestureListener(context));
        }

        public void replaceLayer(Activity activity) {
            setClickable(true);
            ViewGroup root = (ViewGroup) activity.getWindow().getDecorView();
            content = root.getChildAt(0);
            // 默认背景色
            content.setBackgroundColor(getResources().getColor(R.color.white));
            // 设成clickable，否则可能点到SwipeLayout
            content.setClickable(true);
            if (content instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) content;
                for (int i = viewGroup.getChildCount() - 1; i >= 0; i--)
                    viewGroup.getChildAt(i).setClickable(true);
            }

            ViewGroup.LayoutParams params = content.getLayoutParams();
            ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            root.removeView(content);
            // 留出状态栏位置
            this.setPadding(0, android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT ? 0 : getStatusBarHeight(), 0, 0);
            this.addView(content, params2);
            root.addView(this, params);
        }


        /**
         * TouchEvent相关
         */

        private boolean movedToRight = false;

        @Override
        public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
            if (isSwipeEnabled())
                gestureDetector.onTouchEvent(ev);
            return super.dispatchTouchEvent(ev);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (!isSwipeEnabled())
                return super.onInterceptTouchEvent(ev);
            return movedToRight || super.onInterceptTouchEvent(ev);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
            private int swipeDistThreshold;
            private int swipeVelocThreshold;

            public GestureListener(Context context) {
                float density = context.getResources().getDisplayMetrics().density;
                swipeDistThreshold = (int) (density * 70);
                swipeVelocThreshold = (int) (density * 60);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                movedToRight = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > 3 * Math.abs(distanceY)
                        && Math.abs(distanceX) > swipeDistThreshold
                        && Math.abs(velocityX) > swipeVelocThreshold) {
                    if (distanceX > 0) {
                        movedToRight = true;
                        onBackPressed();
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
