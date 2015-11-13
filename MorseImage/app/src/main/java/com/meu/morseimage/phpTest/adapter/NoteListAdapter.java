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
import com.meu.morseimage.phpTest.user.bean.NoteGroupBean;
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
    private ArrayList<NoteGroupBean> groupList;
    private ArrayList<String> openedGroups;

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
        this.groupList = NoteGroupBean.groupListFromList(list);
        this.actionListener = listener;
        openedGroups = new ArrayList<>();
        openedGroups.add("最近");
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
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void notifyDataSetChanged(boolean listChanged, boolean openRecently)
    {
        if (listChanged) {
            groupList = NoteGroupBean.groupListFromList(mList);
            if (openRecently && !openedGroups.contains("最近"))
                openedGroups.add("最近");
            for (NoteGroupBean groupBean : groupList) {
                if (openedGroups.contains(groupBean.title))
                    groupBean.isOpen = true;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        int size = 0;
        for (NoteGroupBean groupBean : groupList) {
            size += 1;
            if (groupBean.isOpen)
                size += groupBean.group.size();
        }
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
        int viewType = getItemViewType(position);
        if (viewType == 2) {
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
        else if (viewType == 0)
            return typeTitleGetView(position, convertView);


        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.view_note_list_item, null);
        }
        TextView title = (TextView)convertView.findViewById(R.id.title);
        TextView content = (TextView)convertView.findViewById(R.id.content);
        TextView time = (TextView)convertView.findViewById(R.id.time);
        final NoteBean bean = getNoteBean(position);
        time.setText(StringUtil.toShowTime(bean.createTime, false));
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

    private View typeTitleGetView(int position, View convertView)
    {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.view_note_group_title, null);
        }
        TextView title = (TextView)convertView.findViewById(R.id.title);
        TextView count = (TextView)convertView.findViewById(R.id.count);
        View arrow = convertView.findViewById(R.id.iv_arrow);
        final NoteGroupBean groupBean = groupList.get(getGroupIndex(position));
        title.setText(groupBean.title);
        count.setText("" + groupBean.group.size());
        arrow.setSelected(groupBean.isOpen);
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                groupBean.isOpen = !groupBean.isOpen;
                if (!groupBean.isOpen)
                    openedGroups.remove(groupBean.title);
                else if (!openedGroups.contains(groupBean.title))
                    openedGroups.add(groupBean.title);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private int getGroupIndex(int position)
    {
        int size = 0;
        int index = 0;
        for (; index < groupList.size(); index++) {
            NoteGroupBean groupBean = groupList.get(index);
            size += 1;
            if (groupBean.isOpen)
                size += groupBean.group.size();
            if (position < size)
                return index;
        }
        return size;
    }

    private NoteBean getNoteBean(int position)
    {
        int size = 0;
        int index = 0;
        for (; index < groupList.size(); index++) {
            NoteGroupBean groupBean = groupList.get(index);
            size += 1;
            if (groupBean.isOpen) {
                int groupSize = groupBean.group.size();
                if (position - size < groupSize)
                    return groupBean.group.get(position - size);
                size += groupSize;
            }
        }
        throw new NullPointerException("position not correct");
    }


    @Override
    public int getViewTypeCount()
    {
        return 3;
    }

    @Override
    public int getItemViewType(int position)
    {
        int size = 0;
        for (NoteGroupBean groupBean : groupList) {
            size += 1;
            if (position < size)
                return 0;
            if (groupBean.isOpen)
                size += groupBean.group.size();
            if (position < size)
                return 1;
        }
        return 2;
    }
}
