package com.meu.morseimage.phpTest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Created by dekunt on 15/8/26.
 *
 * Base class for CustomDialog, include the features:
 * 1. Custom setCanceledOnTouchOutside: need to get a outsideView and setOnclickListener on subClass's constructor
 */
abstract public class BaseDialog extends Dialog
{
    private boolean isCanceledOnTouchOutside;

    @Override
    public void setCanceledOnTouchOutside(boolean cancel)
    {
        super.setCanceledOnTouchOutside(cancel);
        isCanceledOnTouchOutside = cancel;
    }

    public void setCanceledOnTouchOutside(boolean cancel, int outsideViewId)
    {
        setCanceledOnTouchOutside(cancel);
        findViewById(outsideViewId).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isCanceledOnTouchOutside)
                    cancel();
            }
        });
    }


    public BaseDialog(Context context, int theme)
    {
        super(context, theme);
    }
}
