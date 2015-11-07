package com.meu.morseimage.phpTest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.meu.morseimage.BaseActivity;
import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.adapter.NoteListAdapter;
import com.meu.morseimage.phpTest.http.RequestHelper;
import com.meu.morseimage.phpTest.http.RequestManager;
import com.meu.morseimage.phpTest.http.ResponseListener;
import com.meu.morseimage.phpTest.http.Result;
import com.meu.morseimage.phpTest.http.ServerRequest;
import com.meu.morseimage.phpTest.http.UrlPath;
import com.meu.morseimage.phpTest.user.bean.NoteBean;
import com.meu.morseimage.phpTest.user.bean.NoteListBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dekunt on 15/11/4.
 */
public class NoteListActivity extends BaseActivity
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        initView();
        initData();
        getData();
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
