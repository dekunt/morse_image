package com.meu.morseimage.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meu.morseimage.R;
import com.meu.morseimage.views.FaceRelativeLayout;

import java.util.ArrayList;

/**
 * Created by dekunt on 15/8/26.
 */
public class ImagesDialog extends BaseDialog
{

    public ImagesDialog(Context context, FaceRelativeLayout.OnFaceClickedListener faceClickedListener)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        init(faceClickedListener);
    }

    private void init(final FaceRelativeLayout.OnFaceClickedListener faceClickedListener)
    {
        setContentView(R.layout.dialog_images);
        setCancelable(true);
        setCanceledOnTouchOutside(true, R.id.dialog_outside_view);

        FaceRelativeLayout faceRelativeLayout = (FaceRelativeLayout)findViewById(R.id.face_relative_layout);
        faceRelativeLayout.setOnFaceClickedListener(new FaceRelativeLayout.OnFaceClickedListener()
        {
            @Override
            public void onFaceClick(int resId)
            {
                faceClickedListener.onFaceClick(resId);
                cancel();
            }
        });
    }
}
