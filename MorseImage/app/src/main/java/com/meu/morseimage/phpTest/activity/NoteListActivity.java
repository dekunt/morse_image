package com.meu.morseimage.phpTest.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

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
import com.meu.morseimage.phpTest.user.bean.BaseBean;
import com.meu.morseimage.phpTest.user.bean.NoteBean;
import com.meu.morseimage.phpTest.user.bean.NoteListBean;

import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by dekunt on 15/11/4.
 */
public class NoteListActivity extends SwipeActivity implements View.OnClickListener
{
    private static final int PER_PAGE = 10;

    private NoteListAdapter adapter;
    private ListView listView;
    private View viewNodata;
    private View loading;
    private boolean isLoading = false;

    public static void invoke(Context context)
    {
        Intent intent = new Intent(context, NoteListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean result = super.onCreateOptionsMenu(menu);
        setRightButton(R.mipmap.ic_edit, this);
        return result;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.right_button: onClickRightBtn(); break;
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
        adapter = new NoteListAdapter(this, new ArrayList<NoteBean>(),
                new NoteListAdapter.ActionListener()
                {
                    @Override
                    public void onStartEdit()
                    {
                        setLeftButton(R.mipmap.ic_done, null);
                        setRightButton(R.mipmap.ic_delete, NoteListActivity.this);
                    }

                    @Override
                    public void onShowLoading()
                    {
                        getData();
                    }

                    @Override
                    public void onClickNoMoreView()
                    {
                        NoteEditActivity.invoke(NoteListActivity.this);
                    }
                });
        listView.setAdapter(adapter);
    }

    private void onClickRightBtn()
    {
        if (adapter.isEditing()) {
            final ArrayList<NoteBean> checkedItems = adapter.getCheckedItems();
            if (checkedItems.isEmpty())
                return;
            PopupButtonsDialog dialog = new PopupButtonsDialog(this);
            dialog.setButton1("删除选中的" + checkedItems.size() + "项", new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deleteItems(checkedItems);
                }
            });
            dialog.show();
        }
        else {
            NoteEditActivity.invoke(this);
        }
    }

    private void deleteItems(ArrayList<NoteBean> checkedItems)
    {
        for (NoteBean bean : checkedItems)
            adapter.getList().remove(bean);
        quitEditState();

        String deleteIds = checkedItems.get(0).noteId;
        for (int i = 1; i < checkedItems.size(); i++) {
            deleteIds += ("," + checkedItems.get(i).noteId);
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("deleteIds", deleteIds);
        ServerRequest request = new ServerRequest<>(
                RequestHelper.buildHttpGet(UrlPath.NOTE_DELETE, params),
                BaseBean.class,
                new ResponseListener<BaseBean>());
        RequestManager.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onBackPressed()
    {
        if (adapter.isEditing())
            quitEditState();
        else
            super.onBackPressed();
    }

    private void quitEditState()
    {
        setLeftButton(R.mipmap.ic_arrow_back, null);
        setRightButton(R.mipmap.ic_edit, NoteListActivity.this);
        adapter.quitEdit();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(NoteEditEvent event)
    {
        viewNodata.setVisibility(View.GONE);
        NoteBean bean = event.noteBean;
        ArrayList<NoteBean> list = adapter.getList();
        int oldIndex = list.indexOf(bean);
        if (oldIndex >= 0)
            list.set(oldIndex, bean);
        else
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
        if (isLoading)
            return;
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        int size = adapter.getList().size();
        String timeLine = "";
        if (size > 0)
            timeLine = adapter.getList().get(size - 1).createTime;
        params.put("timeLine", timeLine);
        params.put("perPage", PER_PAGE);
        ServerRequest request = new ServerRequest<>(
                RequestHelper.buildHttpGet(UrlPath.NOTE_LIST, params),
                NoteListBean.class,
                new ResponseListener<NoteListBean>() {

                    @Override
                    protected void onSucc(String url, NoteListBean result) {
                        if (result != null && result.list != null)
                            adapter.setNoMoreData(result.list.size() < PER_PAGE);
                        setData(result == null ? null : result.list);
                    }

                    @Override
                    public void onNetworkComplete() {
                        isLoading = false;
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    protected void onError(String url, Result.ErrorMsg errorMsg) {
                        super.onError(url, errorMsg);
                        setData(null);
                    }

                    @Override
                    protected void onFail(int errorType, String errorDesc) {
                        super.onFail(errorType, errorDesc);
                        setData(null);
                    }
                });
        RequestManager.getInstance(this).addToRequestQueue(request);
    }


    private void setData(ArrayList<NoteBean> tmplist)
    {
        ArrayList<NoteBean> list = adapter.getList();
        if (tmplist != null)
            list.addAll(tmplist);
        viewNodata.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }
}
