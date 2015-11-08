package com.meu.morseimage.phpTest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.meu.morseimage.BaseActivity;
import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.event.NoteEditEvent;
import com.meu.morseimage.phpTest.http.RequestHelper;
import com.meu.morseimage.phpTest.http.RequestManager;
import com.meu.morseimage.phpTest.http.ResponseListener;
import com.meu.morseimage.phpTest.http.ServerRequest;
import com.meu.morseimage.phpTest.http.UrlPath;
import com.meu.morseimage.phpTest.user.bean.NoteBean;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by dekunt on 15/11/7.
 */
public class NoteEditActivity extends BaseActivity
{
    public static final String INTENT_EXTRA_NOTE = "intent_extra_note";

    private EditText etTitle;
    private EditText etContent;

    private String mTitle;
    private String mContent;
    private NoteBean mNoteBean;

    public static void invoke(Context context)
    {
        Intent intent = new Intent(context, NoteEditActivity.class);
        context.startActivity(intent);
    }

    public static void invoke(Context context, NoteBean bean)
    {
        Intent intent = new Intent(context, NoteEditActivity.class);
        intent.putExtra(INTENT_EXTRA_NOTE, bean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        etTitle = (EditText) findViewById(R.id.et_title);
        etContent = (EditText) findViewById(R.id.et_content);

        final View scrollView = findViewById(R.id.scrollView);
        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                etContent.setMinHeight(scrollView.getHeight());
            }
        });

        mNoteBean = (NoteBean)getIntent().getSerializableExtra(INTENT_EXTRA_NOTE);
        if (mNoteBean != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            etTitle.setText(mNoteBean.title);
            etContent.setText(mNoteBean.content);
        }
    }

    @Override
    public void finish()
    {
        onFinishEdit();
        super.finish();
    }


    private void onFinishEdit()
    {
        mTitle = etTitle.getText().toString();
        mContent = etContent.getText().toString();
        if (TextUtils.isEmpty(mTitle) && TextUtils.isEmpty(mContent))
            return;
        if (mNoteBean != null && mTitle.equals(mNoteBean.title) && mContent.equals(mNoteBean.content))
            return;
        reqEdit();
    }

    private void reqEdit()
    {
        Map<String, Object> params = new HashMap<>();
        if (mNoteBean != null)
            params.put("noteId", mNoteBean.noteId);
        params.put("title", mTitle);
        params.put("content", mContent);
        ServerRequest request = new ServerRequest<>(
                UrlPath.NOTE_EDIT,
                RequestHelper.buildPublicParams(params),
                NoteBean.class,
                new ResponseListener<NoteBean>()
                {
                    @Override
                    public void onNetworkComplete() {
                        EventBus.getDefault().post(new NoteEditEvent(NoteEditEvent.EditAction.ACTION_RESPOND, null));
                    }

                    @Override
                    protected void onSucc(String url, NoteBean result) {
                        if (result != null)
                            EventBus.getDefault().post(new NoteEditEvent(NoteEditEvent.EditAction.ACTION_DONE, result));
                    }
                });
        RequestManager.getInstance(this).addToRequestQueue(request);
        EventBus.getDefault().post(new NoteEditEvent(NoteEditEvent.EditAction.ACTION_SENT, null));
    }

}
