package com.meu.morseimage.phpTest.util;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil
{
    /**
     * 时间显示格式，时间戳单位：秒
     */
    static public String toShowTime(String timeStamp, boolean detail)
    {
        if (TextUtils.isEmpty(timeStamp))
            return "";
        try {
            long time = Long.parseLong(timeStamp);
            return toShowTime(new Date(time * 1000), detail);
        }
        catch (NumberFormatException ignored) {
        }
        return "";
    }

    static public String toShowTime(Date date, boolean detail)
    {
        int dayCount = getDayCount(date);
        int yeahCount = getYearCount(date);
        String yeahMonthDay = new SimpleDateFormat("yyyy年M月d日", Locale.CANADA).format(date);
        String monthDay = yeahMonthDay.substring(5, yeahMonthDay.length());
        String hourMinute = new SimpleDateFormat("K:mm", Locale.CANADA).format(date);
        String am = new SimpleDateFormat("aa", Locale.CANADA).format(date);
        if (am.contains("A"))
            hourMinute = "上午" + hourMinute;
        else if (hourMinute.startsWith("0:"))
            hourMinute = "中午" + hourMinute.replace("0:", "12:");
        else
            hourMinute = "下午" + hourMinute;

        if (dayCount < 2) {
            String prefix = dayCount == 0 ? "今天 " : "昨天 ";
            String detailString = detail ? " " + (yeahCount == 0 ? monthDay : yeahMonthDay) : "";
            return prefix + hourMinute + detailString;
        }
        else {
            String detailString = detail ? hourMinute : "";
            return (yeahCount == 0 ? monthDay : yeahMonthDay) + detailString;
        }
    }

    static private int getDayCount(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date = cal.getTime();

        long days = TimeUnit.MILLISECONDS.toDays(today.getTime() - date.getTime());
        return (int)days;
    }

    static private int getYearCount(Date date)
    {
        try {
            long yeah = Long.parseLong(new SimpleDateFormat("yyyy", Locale.CANADA).format(date));
            long currentYeah = Long.parseLong(new SimpleDateFormat("yyyy", Locale.CANADA).format(new Date()));
            return (int)(currentYeah - yeah);
        }
        catch (Exception e) {
            return -1;
        }
    }


    /**
     * 判断是否含有中文
     */
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 如果如果内容为空的话，返回true
     */
    public static boolean isNull(String content)
    {
        return TextUtils.isEmpty(content) ||
                "null".equals(content.trim().toLowerCase());
    }

    /*
	 * 要严格的验证手机号码，必须先要清楚现在已经开放了哪些数字开头的号码段，目前国内号码段分配如下：
	 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
	 * 联通：130、131、132、152、155、156、185、186电信：133、153、180、189、（1349卫通）
	 */
    public static boolean isMobileNO(String mobiles) {

        if (!StringUtil.isNull(mobiles)) {
            Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
            Matcher m = p.matcher(mobiles);
            return m.matches();
        } else {
            return false;
        }
    }

    // MD5変换
    public static String Md5(String str) {
        if (str != null && !str.equals("")) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
                byte[] md5Byte = md5.digest(str.getBytes("UTF8"));
                StringBuilder sb = new StringBuilder();
                for (byte aMd5Byte : md5Byte)
                {
                    sb.append(HEX[(aMd5Byte & 0xff) / 16]);
                    sb.append(HEX[(aMd5Byte & 0xff) % 16]);
                }
                str = sb.toString();
            } catch (Exception ignored) {

            }
        }
        return str;
    }
}
