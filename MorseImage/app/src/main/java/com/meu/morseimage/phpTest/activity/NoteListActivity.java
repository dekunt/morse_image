package com.meu.morseimage.phpTest.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.meu.morseimage.BaseActivity;
import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.adapter.NoteListAdapter;
import com.meu.morseimage.phpTest.dialog.PopupButtonsDialog;
import com.meu.morseimage.phpTest.event.NoteEditEvent;
import com.meu.morseimage.phpTest.http.RequestHelper;
import com.meu.morseimage.phpTest.http.RequestManager;
import com.meu.morseimage.phpTest.http.ResponseListener;
import com.meu.morseimage.phpTest.http.Result;
import com.meu.morseimage.phpTest.http.ServerRequest;
import com.meu.morseimage.phpTest.http.UrlPath;
import com.meu.morseimage.phpTest.user.UserInfo;
import com.meu.morseimage.phpTest.user.bean.NoteBean;
import com.meu.morseimage.phpTest.user.bean.NoteListBean;

import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by dekunt on 15/11/4.
 */
public class NoteListActivity extends BaseActivity implements View.OnClickListener
{
    private static final int PER_PAGE = 20;

    private int pageNow = 1;
    private NoteListAdapter adapter;

    private ListView listView;
    private View viewNodata;
    private View loading;


    public static void invoke(Context context)
    {
        Intent intent = new Intent(context, NoteListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean result = super.onCreateOptionsMenu(menu);
        setRightButton(R.mipmap.ic_create, this);
        return result;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.right_button: NoteEditActivity.invoke(this); break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        initView();
        initData();
        getData();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    void initView()
    {
        listView = (ListView)findViewById(R.id.listView);
        viewNodata = findViewById(R.id.view_nodata);
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
    }

    void initData()
    {
        adapter = new NoteListAdapter(this, new ArrayList<NoteBean>());
        listView.setAdapter(adapter);
    }


    @SuppressWarnings("unused")
    public void onEventMainThread(NoteEditEvent event)
    {
        switch (event.action) {
            case ACTION_SENT: loading.setVisibility(View.VISIBLE); break;
            case ACTION_RESPOND: loading.setVisibility(View.GONE); break;
            case ACTION_DONE: onEditFinished(event.noteBean); break;
        }
    }

    private void onEditFinished(NoteBean bean) {
        ArrayList<NoteBean> list = adapter.getList();
        int oldIndex = list.indexOf(bean);
        if (oldIndex >= 0)
            list.remove(oldIndex);
        list.add(0, bean);
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                onClickLogout();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onClickLogout() {
        final PopupButtonsDialog dialog = new PopupButtonsDialog(this);
        dialog.setButton1("退出账号", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface)
                    {
                        UserInfo.getInstance().clearLoginInfo();
                        finish();
                    }
                });
            }
        });
        dialog.show();
    }

    private void getData()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", "" + pageNow);
        ServerRequest request = new ServerRequest<>(
                RequestHelper.buildHttpGet(UrlPath.NOTE_LIST, params),
                NoteListBean.class,
                new ResponseListener<NoteListBean>() {

                    @Override
                    protected void onSucc(String url, NoteListBean result) {
                        super.onSucc(url, result);
                        setData(result == null ? null : result.list, false);
                    }

                    @Override
                    public void onNetworkComplete() {
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    protected void onError(String url, Result.ErrorMsg errorMsg) {
                        setData(null, false);
                    }

                    @Override
                    protected void onFail(int errorType, String errorDesc) {
                        setData(null, false);
                    }
                });
        RequestManager.getInstance(this).addToRequestQueue(request);
    }


    private void setData(ArrayList<NoteBean> tmplist, boolean clear)
    {
        ArrayList<NoteBean> list = adapter.getList();
        if (clear)
            list.clear();
        if (tmplist != null)
            list.addAll(tmplist);
        viewNodata.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }
}
