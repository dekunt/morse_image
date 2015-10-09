package com.meu.morseimage.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by dekunt on 15/6/2.
 */
public class BitmapUtils
{

    public static Bitmap getBitmapFromView(View view)
    {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);

        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        return returnedBitmap;
    }
}
