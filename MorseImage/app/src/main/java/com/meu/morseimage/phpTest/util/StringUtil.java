package com.meu.morseimage.phpTest.util;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil
{

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
