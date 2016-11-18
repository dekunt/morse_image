package com.meu.morseimage.hanziUtils;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

@SuppressWarnings("WeakerAccess")
public class HanziToPinyin {

    /*汉字转为拼音*/
    public static String getPinYin(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            builder.append(getPinYin(str.charAt(i)));
        }
        return builder.toString();
    }

    public static String getPinYin(char ch) {
        ch = Character.toLowerCase(ch);
        // 检查是否"可能有汉字"（避免过多使用汉字转拼音）
        if (ch > 'z') {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            try {
                String[] values = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                return values[0];
            } catch (Exception ignored) {
            }
        }
        return fullToHalf(ch);
    }


    /*
    * 功能：全角字符转换为半角
    * 说明：全角空格为12288, 全角句号为12290; 其他全角字符(65281-65374)与半角(33-126)的对应关系是：均相差65248
    * 输入参数：ch -- 需要转换的字符
    * 输出参数：无：
    * 返回值: 转换后的字符串
    */
    public static String fullToHalf(char ch) {
        if (ch == 12288 || ch == 12290) {
            ch = (char) (ch == 12288 ? 32 : 46);
        } else if (ch > 65280 && ch < 65375) {
            ch = (char) (ch - 65248);
        }
        return String.valueOf(ch);
    }
}
