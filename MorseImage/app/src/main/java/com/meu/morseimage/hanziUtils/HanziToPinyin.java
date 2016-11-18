package com.meu.morseimage.hanziUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

public class HanziToPinyin
{

    /*汉字转为拼音*/
    public static String getPinYin(String str)
    {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        StringBuilder sb = new StringBuilder();
        try {

            for (int i = 0; i < str.length(); i++)
            {
                char c = str.charAt(i);
                String[] values = PinyinHelper.toHanyuPinyinStringArray(c, format);
                sb.append(values[0]);
            }
        } catch (Exception e) {
            return fullToHalf(str.toLowerCase());
        }
        return fullToHalf(sb.toString());
    }


    /*
    * 功能：字符串全角转换为半角
    * 说明：全角空格为12288, 全角句号为12290; 其他全角字符(65281-65374)与半角(33-126)的对应关系是：均相差65248
    * 输入参数：input -- 需要转换的字符串
    * 输出参数：无：
    * 返回值: 转换后的字符串
    */
    public static String fullToHalf(String input)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++)
        {
            if (c[i] == 12288 || c[i] == 12290)
            {
                c[i] = (char) (c[i] == 12288 ? 32 : 46);
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }
}
