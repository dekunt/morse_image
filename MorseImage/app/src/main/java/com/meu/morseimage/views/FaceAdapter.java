package com.meu.morseimage.views;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.meu.morseimage.R;

import java.util.List;

/**
 * Created by dekunt on 15/10/9.
 */
public class FaceAdapter extends ArrayAdapter<Integer>
{
    public FaceAdapter(Context context, List<Integer> objects)
    {
        super(context, R.layout.view_face_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.view_face_item, null);
        }

        Integer faceResId = getItem(position);
        convertView.setTag(faceResId);

        ImageView faceImage = (ImageView) convertView.findViewById(R.id.face_image);
        faceImage.setImageResource(faceResId);
        return convertView;
    }

}
