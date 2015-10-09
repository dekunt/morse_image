package com.meu.morseimage;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by dekunt on 15/9/30.
 */
public class BaseActivity extends Activity
{
    private View leftButton;
    private View rightButton;
    private TextView leftButtonTextView;
    private TextView rightButtonTextView;

    protected View.OnClickListener clickBackListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            onBackPressed();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        LinearLayout actionBarView = (LinearLayout) menu.findItem(R.id.title_layout).getActionView();
        TextView titleTextView = (TextView) actionBarView.findViewById(R.id.title_label);
        titleTextView.setText(getTitle());

        leftButton = actionBarView.findViewById(R.id.left_button);
        leftButtonTextView = (TextView)actionBarView.findViewById(R.id.left_button_text_view);
        rightButton = actionBarView.findViewById(R.id.right_button);
        rightButtonTextView = (TextView)actionBarView.findViewById(R.id.right_button_text_view);

        if (needLeftButton())
            leftButton.setOnClickListener(clickBackListener);
        else
            leftButton.setVisibility(View.INVISIBLE);
        return super.onCreateOptionsMenu(menu);
    }

    // 不需要Left Button时，重写该函数
    protected boolean needLeftButton()
    {
        return true;
    }

    protected void setLeftButton(String text, View.OnClickListener clickListener)
    {
        if (leftButton == null)
            return;
        leftButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        leftButtonTextView.setText(text);
        if (clickListener != null)
            leftButton.setOnClickListener(clickListener);
        else
            leftButton.setOnClickListener(clickBackListener);
    }

    protected void setLeftButton(int resId, View.OnClickListener clickListener)
    {
        if (leftButton == null)
            return;
        leftButtonTextView.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        leftButtonTextView.setText("");
        if (clickListener != null)
            leftButton.setOnClickListener(clickListener);
        else
            leftButton.setOnClickListener(clickBackListener);
    }

    protected void setRightButton(String text, View.OnClickListener clickListener)
    {
        if (rightButton == null)
            return;
        rightButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        rightButtonTextView.setText(text);
        rightButton.setOnClickListener(clickListener);
    }

    protected void setRightButton(int resId, View.OnClickListener clickListener)
    {
        if (rightButton == null)
            return;
        rightButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
        rightButtonTextView.setText("");
        rightButton.setOnClickListener(clickListener);
    }






    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (isStatusBarCommon())
            addStatusBarBackgroundView();
    }


    // 是否同化状态栏颜色（不需要的子类需要重写）
    protected boolean isStatusBarCommon() {
        return true;
    }

    protected void addStatusBarBackgroundView() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT)
            return;
        View statusBarBgView = new View(this);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight());
        statusBarBgView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        statusBarBgView.setLayoutParams(lParams);
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        viewGroup.addView(statusBarBgView);
    }

    // 获取手机状态栏高度
    public int getStatusBarHeight() {
        int x, statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
