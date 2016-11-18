package com.meu.morseimage.encryption;

import android.text.TextUtils;

import com.meu.morse.HanziToPinyin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dekunt on 2016/11/17.
 */
@SuppressWarnings("WeakerAccess")
public class EncryptUtils {

    // 倒序加密/解密
    public static String reverse(String src) {
        src = keepLetterOnly(src);
        return new StringBuilder(src).reverse().toString();
    }

    // 栅栏加密
    public static String encodeFence(String src) {
        src = keepLetterOnly(src);
        String str1 = "";
        String str2 = "";
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            if (i % 2 == 0) {
                str1 = append(str1, ch);
            } else {
                str2 = append(str2, ch);
            }
        }
        return str1 + str2;
    }

    // 栅栏解密
    public static String decodeFence(String src) {
        src = keepLetterOnly(src);
        int length = src.length();
        if (length < 2) {
            return src;
        }
        int middleIndex = (length - 1) / 2 + 1;
        String str1 = src.substring(0, middleIndex);
        String str2 = src.substring(middleIndex, length);
        String result = "";
        for (int i = 0; i < str1.length(); i++) {
            result = append(result, str1.charAt(i));
            if (i < str2.length()) {
                result = append(result, str2.charAt(i));
            }
        }
        return result;
    }

    // 全键盘加密
    public static String encodeKeyboard(String src) {
        return encodeKeyboard(src, true);
    }

    // 全键盘解密
    public static String decodeKeyboard(String src) {
        return encodeKeyboard(src, false);
    }

    // 全键盘加密/解密
    private static String encodeKeyboard(String src, boolean encode) {
        src = keepLetterOnly(src);
        Map map = getKeyboardMap(encode);
        String result = "";
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            if (map.containsKey(ch)) {
                Character ch2 = (Character) map.get(ch);
                result = append(result, ch2);
            }
        }
        return result;
    }

    // 手机9键键盘加密
    public static String encodePhoneKeyboard(String src) {
        src = keepLetterOnly(src);
        Map map = getPhoneKeyboardMap(true);
        String result = "";
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            if (map.containsKey(ch)) {
                Integer number = (Integer) map.get(ch);
                result = append(result, number);
            }
        }
        return result;
    }

    // 手机9键键盘解密
    public static String decodePhoneKeyboard(String src) {
        src = keepNumberOnly(src);
        Map map = getPhoneKeyboardMap(false);
        String result = "";
        for (int i = 0; i < src.length() - 1; i += 2) {
            Integer number = Integer.parseInt(src.substring(i, i + 2));
            if (map.containsKey(number)) {
                Character ch = (Character) map.get(number);
                result = append(result, ch);
            }
        }
        return result;
    }

    // 摩尔斯电码加密
    public static String encodeMorse(String src) {
        src = keepNumberOnly(src);
        Map map = getMorseMap(true);
        String result = "";
        for (int i = 0; i < src.length(); i++) {
            Integer number = Integer.parseInt("" + src.charAt(i));
            if (map.containsKey(number)) {
                String morse = (String) map.get(number);
                if (i < src.length() - 1) {
                    morse = morse + "/";
                }
                result = result + morse;
            }
        }
        return result;
    }

    // 摩尔斯电码解密
    public static String decodeMorse(String src) {
        src = keepMorseOnly(src);
        String[] list = src.split("/");
        Map map = getMorseMap(false);
        String result = "";
        for (String str : list) {
            if (map.containsKey(str)) {
                Integer number = (Integer) map.get(str);
                result = append(result, number);
            }
        }
        return result;
    }

    /**
     * ----------------------------------------------------
     * 私有方法分割线
     * ----------------------------------------------------
     */

    // 只保留字母
    private static String keepLetterOnly(String src) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        if (checkHanzi(src)) {
            src = HanziToPinyin.getPinYin(src);
        }
        src = src.toLowerCase();
        String result = "";
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            if (ch >= 'a' && ch <= 'z') {
                result = append(result, ch);
            }
        }
        return result;
    }

    // 只保留数字
    private static String keepNumberOnly(String src) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        String result = "";
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            if (ch >= '0' && ch <= '9') {
                result = append(result, ch);
            }
        }
        return result;
    }

    // 只保留摩尔斯电码
    private static String keepMorseOnly(String src) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        String result = "";
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            if (ch == '*' || ch == '-' || ch == '/') {
                result = append(result, ch);
            }
        }
        return result;
    }

    // 检查是否"可能有汉字"（避免过多使用汉字转拼音）
    private static boolean checkHanzi(String src) {
        if (TextUtils.isEmpty(src)) {
            return false;
        }
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            if (ch > 'z') {
                return true;
            }
        }
        return false;
    }

    private static String append(String str, char ch) {
        return String.format("%s%c", str, ch);
    }

    private static String append(String str, Integer number) {
        return str + number;
    }

    private static Map getPhoneKeyboardMap(boolean encode) {
        Map<Object, Object> map = new HashMap<>();
        map.put('a', 21);
        map.put('b', 22);
        map.put('c', 23);
        map.put('d', 31);
        map.put('e', 32);
        map.put('f', 33);
        map.put('g', 41);
        map.put('h', 42);
        map.put('i', 43);
        map.put('j', 51);
        map.put('k', 52);
        map.put('l', 53);
        map.put('m', 61);
        map.put('n', 62);
        map.put('o', 63);
        map.put('p', 71);
        map.put('q', 72);
        map.put('r', 73);
        map.put('s', 74);
        map.put('t', 81);
        map.put('u', 82);
        map.put('v', 83);
        map.put('w', 91);
        map.put('x', 92);
        map.put('y', 93);
        map.put('z', 94);
        if (!encode) {
            Map<Object, Object> map2 = new HashMap<>();
            for (char i = 'a'; i <= 'z'; i++) {
                map2.put(map.get(i), i);
            }
            return map2;
        }
        return map;
    }

    private static Map<Character, Character> getKeyboardMap(boolean encode) {
        Map<Character, Character> map = new HashMap<>();
        map.put('a', 'q');
        map.put('b', 'w');
        map.put('c', 'e');
        map.put('d', 'r');
        map.put('e', 't');
        map.put('f', 'y');
        map.put('g', 'u');
        map.put('h', 'i');
        map.put('i', 'o');
        map.put('j', 'p');
        map.put('k', 'a');
        map.put('l', 's');
        map.put('m', 'd');
        map.put('n', 'f');
        map.put('o', 'g');
        map.put('p', 'h');
        map.put('q', 'j');
        map.put('r', 'k');
        map.put('s', 'l');
        map.put('t', 'z');
        map.put('u', 'x');
        map.put('v', 'c');
        map.put('w', 'v');
        map.put('x', 'b');
        map.put('y', 'n');
        map.put('z', 'm');
        if (!encode) {
            Map<Character, Character> map2 = new HashMap<>();
            for (char i = 'a'; i <= 'z'; i++) {
                map2.put(map.get(i), i);
            }
            return map2;
        }
        return map;
    }

    private static Map<Object, Object> getMorseMap(boolean encode) {
        Map<Object, Object> map = new HashMap<>();
        map.put(1, "*----");
        map.put(2, "**---");
        map.put(3, "***--");
        map.put(4, "****-");
        map.put(5, "*****");
        map.put(6, "-****");
        map.put(7, "--***");
        map.put(8, "---**");
        map.put(9, "----*");
        if (!encode) {
            Map<Object, Object> map2 = new HashMap<>();
            for (int i = 1; i <= 9; i++) {
                map2.put(map.get(i), i);
            }
            return map2;
        }
        return map;
    }
}
