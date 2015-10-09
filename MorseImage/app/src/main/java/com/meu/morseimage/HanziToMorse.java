package com.meu.morseimage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dekunt on 15/10/8.
 */
public class HanziToMorse
{
    private static Map<Character, String> morseMap;

    /*汉字转为莫尔斯电码*/
    public static String getMorse(String str)
    {
        char[] c = HanziToPinyin.getPinYin(str).toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < c.length; i++)
        {
            if (c[i] == 12288 || c[i] == 12290)
            {
                c[i] = (char) (c[i] == 12288 ? 32 : 46);
                continue;
            }
            if (getMorseMap().containsKey(c[i]))
                sb.append(getMorseMap().get(c[i]));
            else
                sb.append(c[i]);
            if (i < c.length - 1)
                sb.append(" ");
        }
        return sb.toString();
    }


    private static Map<Character, String> getMorseMap()
    {
        if (morseMap != null)
            return morseMap;
        morseMap = new HashMap<>();
        morseMap.put('a', ".-");
        morseMap.put('b', "-...");
        morseMap.put('c', "-.-.");
        morseMap.put('d', "-..");
        morseMap.put('e', ".");
        morseMap.put('f', "..-.");
        morseMap.put('g', "--.");
        morseMap.put('h', "....");
        morseMap.put('i', "..");
        morseMap.put('j', ".---");
        morseMap.put('k', "-.-");
        morseMap.put('l', ".-..");
        morseMap.put('m', "--");
        morseMap.put('n', "-.");
        morseMap.put('o', "---");
        morseMap.put('p', ".--.");
        morseMap.put('q', "--.-");
        morseMap.put('r', ".-.");
        morseMap.put('s', "...");
        morseMap.put('t', "-");
        morseMap.put('u', "..-");
        morseMap.put('v', "...-");
        morseMap.put('w', ".--");
        morseMap.put('x', "-..-");
        morseMap.put('y', "-.--");
        morseMap.put('z', "--..");
        morseMap.put('1', ".----");
        morseMap.put('2', "..---");
        morseMap.put('3', "...--");
        morseMap.put('4', "....-");
        morseMap.put('5', ".....");
        morseMap.put('6', "-....");
        morseMap.put('7', "--...");
        morseMap.put('8', "---..");
        morseMap.put('9', "----.");
        morseMap.put('0', "-----");
        morseMap.put(',', "--..--");
        morseMap.put('!', "-.-.--");
        morseMap.put('.', ".-.-.-");
        morseMap.put('?', "..--..");
        morseMap.put(' ', "-..-.");
        morseMap.put('/', "-..-.");
        morseMap.put('=', "-...-");
        morseMap.put('-', "-....-");
        return morseMap;
    }
}
