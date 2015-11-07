package com.meu.morseimage.phpTest.http;

/**
 * Created by dekunt on 15/11/4.
 */
public class UrlPath
{
    private static String PATH_BASE = "http://tanxyz.com/phpTest";

    // 备忘列表
    public static final String NOTE_LIST = PATH_BASE + "/show/noteList.php";
    // 注册
    public static final String REGIST = PATH_BASE + "/login/regist.php";
    // 获取验证码
    public static final String GET_V_CODE = PATH_BASE + "/login/getVCode.php";
    // 登录
    public static final String LOGIN = PATH_BASE + "/login/login.php";
    // 修改密码
    public static final String CHANGE_PSW = PATH_BASE + "/login/changePsw.php";
}
