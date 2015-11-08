package com.meu.morseimage.phpTest.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.meu.morseimage.R;

/**
 * Created by dekunt on 15/11/8.
 */
public class ListFooterView extends FrameLayout
{
    private View ivNoMore;
    private View llLoading;

    public ListFooterView(Context context)
    {
        super(context);
        inflate(context, R.layout.view_list_footer, this);
        ivNoMore = findViewById(R.id.iv_no_more);
        llLoading = findViewById(R.id.ll_loading);
    }

    public void setLoading(boolean loading)
    {
        ivNoMore.setVisibility(loading ? GONE : VISIBLE);
        llLoading.setVisibility(loading ? VISIBLE : GONE);
    }
}
