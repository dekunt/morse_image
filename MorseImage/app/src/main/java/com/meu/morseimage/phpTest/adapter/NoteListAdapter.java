package com.meu.morseimage.phpTest.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meu.morseimage.R;
import com.meu.morseimage.phpTest.activity.NoteEditActivity;
import com.meu.morseimage.phpTest.user.bean.NoteBean;
import com.meu.morseimage.phpTest.util.StringUtil;
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
    private ActionListener actionListener;

    private boolean isEditing = false;

    public interface ActionListener
    {
        void onStartEdit();
        void onShowLoading();
        void onClickNoMoreView();
    }

    public NoteListAdapter(Activity context, ArrayList<NoteBean> list, ActionListener listener)
    {
        this.mContext = context;
        this.mList = list;
        this.actionListener = listener;
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

    public ArrayList<NoteBean> getCheckedItems() {
        ArrayList<NoteBean> result = new ArrayList<>();
        for (NoteBean bean : mList) {
            if (bean.checked)
                result.add(bean);
        }
        return result;
    }

    public void quitEdit()
    {
        isEditing = false;
        for (NoteBean bean : mList)
            bean.checked = false;
        notifyDataSetChanged();
    }

    public boolean isEditing() {
        return isEditing;
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
                        if (!isEditing && actionListener != null)
                            actionListener.onClickNoMoreView();
                    }
                });
            }
            footerView = (ListFooterView)convertView;
            footerView.setLoading(!noMoreData);
            if (!noMoreData && actionListener != null)
                actionListener.onShowLoading();
            return convertView;
        }


        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.view_note_list_item, null);
        }
        TextView title = (TextView)convertView.findViewById(R.id.title);
        TextView content = (TextView)convertView.findViewById(R.id.content);
        TextView time = (TextView)convertView.findViewById(R.id.time);
        final NoteBean bean = mList.get(position);
        time.setText(StringUtil.toShowTime(bean.modifyTime, false));
        title.setText(TextUtils.isEmpty(bean.title) ? "(无主题)" : bean.title);
        content.setText(TextUtils.isEmpty(bean.getSimpleContent()) ? "(无摘要)" : bean.getSimpleContent());
        final View itemGroup = convertView.findViewById(R.id.item_group);
        itemGroup.post(new Runnable()
        {
            @Override
            public void run()
            {
                itemGroup.setSelected(isEditing && bean.checked);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isEditing)
                {
                    bean.checked = !bean.checked;
                    itemGroup.setSelected(bean.checked);
                } else
                {
                    NoteEditActivity.invoke(mContext, bean);
                }
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (!isEditing)
                {
                    if (actionListener != null)
                        actionListener.onStartEdit();
                    isEditing = true;
                    bean.checked = true;
                    itemGroup.setSelected(true);
                    return true;
                }
                return false;
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
