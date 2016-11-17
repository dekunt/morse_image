package com.meu.morseimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meu.facelayout.views.FaceRelativeLayout;
import com.meu.morse.HanziToMorse;
import com.meu.morseimage.dialog.ImagesDialog;
import com.meu.morseimage.phpTest.activity.LoginActivity;
import com.meu.morseimage.phpTest.activity.NoteListActivity;
import com.meu.morseimage.phpTest.activity.SwipeActivity;
import com.meu.morseimage.phpTest.user.UserInfo;
import com.meu.morseimage.utils.BitmapUtils;
import com.meu.morseimage.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by dekunt on 15/9/30.
 * 首页
 */
public class MainActivity extends SwipeActivity {
    ViewGroup shareGroup;
    LinearLayout textLayout;
    ImageView imageView;

    private static final int IMAGE_WIDTH = 140;
    private int imageWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        float density = getResources().getDisplayMetrics().density;
        imageWidth = (int) (IMAGE_WIDTH * density);

        setContentView(R.layout.activity_main);
        shareGroup = (ViewGroup) findViewById(R.id.share_group);
        textLayout = (LinearLayout) findViewById(R.id.text_layout);
        imageView = (ImageView) findViewById(R.id.image_view);

        resetImage(R.mipmap.ic_face_02);
        shareGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImagesDialog dialog = new ImagesDialog(MainActivity.this, new FaceRelativeLayout.OnFaceClickedListener() {
                    @Override
                    public void onFaceClick(int resId) {
                        resetImage(resId);
                    }
                });
                dialog.show();
            }
        });

        final EditText editText = (EditText) findViewById(R.id.edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null)
                    onEditTextChanged(s.toString());
            }
        });


        View noteButton = findViewById(R.id.note_button);
        ((ImageView) noteButton.findViewById(R.id.btn_image)).setImageResource(R.mipmap.ic_note);
        ((TextView) noteButton.findViewById(R.id.btn_text)).setText("记事本");
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfo.getInstance().isLogin())
                    NoteListActivity.invoke(MainActivity.this);
                else
                    LoginActivity.invoke(MainActivity.this);
            }
        });
    }

    private void onEditTextChanged(String text) {
        int length = text.length();
        int currentLength = textLayout.getChildCount();
        for (int i = 0; i < currentLength && i < length; i++) {
            setTextValue((ViewGroup) textLayout.getChildAt(i), text.charAt(i));
        }
        for (int i = currentLength; i < length; i++) {
            setTextValue(addTextGroup(), text.charAt(i));
        }
        for (; length < textLayout.getChildCount(); ) {
            textLayout.removeViewAt(length);
        }
    }

    private void setTextValue(ViewGroup viewGroup, char text) {
        ((TextView) viewGroup.findViewById(R.id.text_top)).setText(String.valueOf(text));
        ((TextView) viewGroup.findViewById(R.id.text_bottom)).setText(HanziToMorse.getMorse("" + text));
    }

    private ViewGroup addTextGroup() {
        int index = textLayout.getChildCount();
        getLayoutInflater().inflate(R.layout.view_double_text, textLayout);
        return (ViewGroup) textLayout.getChildAt(index);
    }

    @Override
    protected boolean needLeftButton() {
        return false;
    }

    @Override
    protected boolean isSwipeEnabled() {
        return false;
    }


    // Share

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        setShareButton();
        return result;
    }

    private void setShareButton() {
        setRightButton(R.mipmap.ic_share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShare();
            }
        });
    }

    private void onShare() {
        Bitmap bitmap = BitmapUtils.getBitmapFromView(shareGroup);
        File file = FileUtils.createFileExternalStorage("morseImage", ".png");
        if (file == null)
            return;
        //noinspection ResultOfMethodCallIgnored
        file.delete();
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fOut);
            fOut.flush();
            fOut.close();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            Uri uri = Uri.fromFile(file);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "请选择"));
            overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Image

    private void resetImage(int resId) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), resId);
        resetImage(icon);
    }

    private void resetImage(Bitmap bitmap) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.width = imageWidth;
        layoutParams.height = imageWidth * bitmap.getHeight() / bitmap.getWidth();
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(bitmap);
    }

}
