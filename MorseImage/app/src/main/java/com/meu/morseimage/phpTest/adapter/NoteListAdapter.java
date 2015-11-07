package com.meu.morseimage.phpTest.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.user.bean.NoteBean;

import java.util.ArrayList;

/**
 * Created by dekunt on 15/10/26.
 */
public class NoteListAdapter extends BaseAdapter
{
    private Activity mContext;
    private ArrayList<NoteBean> mList;

    public NoteListAdapter(Activity context, ArrayList<NoteBean> list)
    {
        this.mContext = context;
        this.mList = list;
    }

    public ArrayList<NoteBean> getList() {
        if (mList == null)
            mList = new ArrayList<>();
        return mList;
    }

    public void setList(ArrayList<NoteBean> list) {
        mList = list;
    }


    @Override
    public int getCount()
    {
        return mList == null ? 0 :mList.size();
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
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.view_note_list_item, null);
        }
        TextView title = (TextView)convertView.findViewById(R.id.title);
        TextView content = (TextView)convertView.findViewById(R.id.content);
        NoteBean bean = mList.get(position);
        title.setText(bean.title);
        content.setText(bean.content);
        return convertView;
    }

    @Override
    public int getViewTypeCount()
    {
        return 1;
    }
}
