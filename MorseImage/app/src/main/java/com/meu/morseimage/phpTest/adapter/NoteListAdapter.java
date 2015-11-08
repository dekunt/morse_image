package com.meu.morseimage.phpTest.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.activity.NoteEditActivity;
import com.meu.morseimage.phpTest.user.bean.NoteBean;
import com.meu.morseimage.phpTest.view.ListFooterView;

import java.util.ArrayList;

/**
 * Created by dekunt on 15/10/26.
 */
public class NoteListAdapter extends BaseAdapter
{
    private Activity mContext;
    private ArrayList<NoteBean> mList;
    private ListFooterView footerView;
    private boolean noMoreData = false;
    private FooterViewListener footerViewListener;

    public interface FooterViewListener
    {
        void onShowLoading();
        void onClickNoMoreView();
    }

    public NoteListAdapter(Activity context, ArrayList<NoteBean> list, FooterViewListener listener)
    {
        this.mContext = context;
        this.mList = list;
        this.footerViewListener = listener;
    }

    public void setNoMoreData(boolean noMoreData)
    {
        this.noMoreData = noMoreData;
        if (footerView != null)
            footerView.setLoading(!noMoreData);
    }

    public ArrayList<NoteBean> getList() {
        if (mList == null)
            mList = new ArrayList<>();
        return mList;
    }


    @Override
    public int getCount()
    {
        int size = (mList == null ? 0 : mList.size());
        return size == 0 ? 0 : size + 1;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (getItemViewType(position) == 1) {
            if (convertView == null) {
                convertView = new ListFooterView(mContext);
                convertView.findViewById(R.id.iv_no_more).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (footerViewListener != null)
                            footerViewListener.onClickNoMoreView();
                    }
                });
            }
            footerView = (ListFooterView)convertView;
            footerView.setLoading(!noMoreData);
            if (!noMoreData && footerViewListener != null)
                footerViewListener.onShowLoading();
            return convertView;
        }


        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.view_note_list_item, null);
        }
        TextView title = (TextView)convertView.findViewById(R.id.title);
        TextView content = (TextView)convertView.findViewById(R.id.content);
        final NoteBean bean = mList.get(position);
        title.setText(bean.title);
        content.setText(bean.content);

        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NoteEditActivity.invoke(mContext, bean);
            }
        });
        return convertView;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position < getList().size())
            return 0;
        return 1;
    }
}
