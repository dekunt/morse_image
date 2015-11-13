package com.meu.morseimage.phpTest.user.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dekunt on 15/11/13.
 */
public class NoteGroupBean
{
    public String title;
    public ArrayList<NoteBean> group;
    public boolean isOpen = false;

    public NoteGroupBean(String title)
    {
        this.title = title;
        group = new ArrayList<>();
    }

    // list分组
    public static ArrayList<NoteGroupBean> groupListFromList(ArrayList<NoteBean> list)
    {
        ArrayList<NoteGroupBean> groupList = new ArrayList<>();
        if (list == null || list.isEmpty())
            return groupList;

        initForNowGroup();
        NoteGroupBean lastGroup = null;
        for (NoteBean bean : list) {
            String title = monthFromDate(bean.createTime);
            if (lastGroup == null || !title.equals(lastGroup.title)) {
                lastGroup = new NoteGroupBean(title);
                groupList.add(lastGroup);
            }
            lastGroup.group.add(bean);
        }
        return groupList;
    }


    private static SimpleDateFormat dateFormat;
    private static String now;
    private static int nowYeah;
    private static int nowMonth;
    private static boolean nowIsMonthEnd;

    private static void initForNowGroup()
    {
        if (dateFormat == null)
            dateFormat = new SimpleDateFormat("yyyy年M月", Locale.CANADA);
        Date time = new Date();
        now = dateFormat.format(time);
        try {
            long day = Long.parseLong(new SimpleDateFormat("d", Locale.CANADA).format(time));
            nowIsMonthEnd = day > 15;
            nowYeah = Integer.parseInt(now.substring(0, 4));
            nowMonth = Integer.parseInt(now.substring(5, now.length() - 1));
        }
        catch (Exception e) {
            nowIsMonthEnd = false;
        }
    }

    private static String monthFromDate(String timeStamp)
    {
        try {
            Date date = new Date(Long.parseLong(timeStamp) * 1000);
            String yeahMonth = dateFormat.format(date);
            if (now.equals(yeahMonth))
                return "最近";
            if (!nowIsMonthEnd){
                int yeah = Integer.parseInt(yeahMonth.substring(0, 4));
                int month = Integer.parseInt(yeahMonth.substring(5, yeahMonth.length() - 1));
                if (month + 1 == nowMonth && yeah == nowYeah)
                    return "最近";
                if (month == 12 && nowMonth == 1 && yeah + 1 == nowYeah)
                    return "最近";
            }
            return yeahMonth;
        }
        catch (Exception e) {
            return "";
        }
    }
}
