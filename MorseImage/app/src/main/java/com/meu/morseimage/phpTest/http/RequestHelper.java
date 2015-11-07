package com.meu.morseimage.phpTest.http;

import com.meu.morseimage.phpTest.user.UserInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;


/**
 * 请求接口的工具类
 */
public class RequestHelper {

    /**
     * 构建请求接口的公共参数.
     */
    public static Map<String, Object> buildPublicParams(Map<String, Object> params) {

        if (!params.containsKey("uid")) {
            String uid = UserInfo.getInstance().getUid();
            params.put("uid", (uid == null || "0".equals(uid)) ? "" : uid);
        }
        String hash = UserInfo.getInstance().getHash();
        params.put("hash", hash == null ? "" : hash);
        return params;
    }

    /**
     * 构建HTTP GET请求url，该方法自动添加系统参数 (uid、hash)
     */
    public static String buildHttpGet(String url, Map<String, Object> params) {
        params = buildPublicParams(params);
        return buildUrl(url, params);
    }


    /**
     * 构建http_get url
     */
    private static String buildUrl(String url, Map<String, Object> params) {
        StringBuffer urlBuffer = new StringBuffer(url);
        if (!url.endsWith("?")) {
            urlBuffer.append("?");
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            if (!urlBuffer.toString().endsWith("?")) {
                urlBuffer.append("&");
            }
            urlBuffer.append(key).append("=");
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                try {
                    value = URLEncoder.encode(value, "GBK");
                } catch (UnsupportedEncodingException e1) {
                }
            }
            urlBuffer.append(value);
        }
        return urlBuffer.toString();

    }
}
