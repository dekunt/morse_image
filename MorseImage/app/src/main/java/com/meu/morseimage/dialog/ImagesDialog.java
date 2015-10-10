package com.meu.morseimage.dialog;

import android.content.Context;

import com.meu.facelayout.views.FaceRelativeLayout;
import com.meu.morseimage.R;

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
