package com.meu.morseimage.phpTest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.dialog.AlertDialog;
import com.meu.morseimage.phpTest.dialog.LoadDialog;
import com.meu.morseimage.phpTest.event.NoteEditEvent;
import com.meu.morseimage.phpTest.http.RequestHelper;
import com.meu.morseimage.phpTest.http.RequestManager;
import com.meu.morseimage.phpTest.http.ResponseListener;
import com.meu.morseimage.phpTest.http.ServerRequest;
import com.meu.morseimage.phpTest.http.UrlPath;
import com.meu.morseimage.phpTest.user.bean.NoteBean;
import com.meu.morseimage.phpTest.util.ToastUtil;
import com.meu.morseimage.utils.AndroidBug5497Workaround;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by dekunt on 15/11/7.
 */
public class NoteEditActivity extends SwipeActivity
{
    public static final String INTENT_EXTRA_NOTE = "intent_extra_note";

    private EditText etTitle;
    private EditText etContent;

    private String mTitle;
    private String mContent;
    private NoteBean mNoteBean;
    private LoadDialog mLoadDialog;

    public static void invoke(Context context)
    {
        invoke(context, null);
    }

    public static void invoke(Context context, NoteBean bean)
    {
        Intent intent = new Intent(context, NoteEditActivity.class);
        if (bean != null)
            intent.putExtra(INTENT_EXTRA_NOTE, bean);
        context.startActivity(intent);
    }

    private void superFinish()
    {
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AndroidBug5497Workaround.assistActivity(this);
        setContentView(R.layout.activity_note_edit);
        etTitle = (EditText) findViewById(R.id.et_title);
        etContent = (EditText) findViewById(R.id.et_content);
        mLoadDialog = new LoadDialog(this);

        final View scrollView = findViewById(R.id.scrollView);
        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                if (oldBottom == 0 || Math.abs(bottom - oldBottom) > 200)
                {
                    scrollView.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            etContent.setMinHeight(scrollView.getHeight() + 4);
                        }
                    });
                }
            }
        });

        mNoteBean = (NoteBean)getIntent().getSerializableExtra(INTENT_EXTRA_NOTE);
        if (mNoteBean != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            etTitle.setText(mNoteBean.title);
            etContent.setText(mNoteBean.getContent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean result = super.onCreateOptionsMenu(menu);
        setLeftButton(R.mipmap.ic_close, null);
        setRightButton(R.mipmap.ic_done, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onFinishEdit();
            }
        });
        return result;
    }

    private boolean checkEdit()
    {
        mTitle = etTitle.getText().toString();
        mContent = etContent.getText().toString();
        if (TextUtils.isEmpty(mTitle) && TextUtils.isEmpty(mContent))
            return false;
        else if (mNoteBean != null && mTitle.equals(mNoteBean.title) && mContent.equals(mNoteBean.getContent()))
            return false;
        return true;
    }

    @Override
    public void finish()
    {
        if (checkEdit())
        {
            AlertDialog dialog = new AlertDialog(this);
            dialog.setTitle("舍弃本次编辑的内容", new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    NoteEditActivity.this.superFinish();
                }
            });
            dialog.show();
        }
        else {
            superFinish();
        }
    }


    private void onFinishEdit()
    {
        if (checkEdit()) {
            reqEdit();
        }
        else if (TextUtils.isEmpty(mTitle) && TextUtils.isEmpty(mContent)) {
            ToastUtil.showMsg("填点内容吧");
        }
        else {
            superFinish();
        }
    }

    private void reqEdit()
    {
        mLoadDialog.show();
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
                        mLoadDialog.cancel();
                    }

                    @Override
                    protected void onSucc(String url, NoteBean result) {
                        if (result != null)
                            EventBus.getDefault().post(new NoteEditEvent(result));
                        NoteEditActivity.this.superFinish();
                    }
                });
        RequestManager.getInstance(this).addToRequestQueue(request);
    }

}
