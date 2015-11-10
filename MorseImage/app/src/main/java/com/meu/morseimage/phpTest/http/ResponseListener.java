package com.meu.morseimage.phpTest.http;


import com.meu.morseimage.phpTest.user.UserInfo;
import com.meu.morseimage.phpTest.util.ToastUtil;

public class ResponseListener<T> {

    /**
     * DO nothing by default!
     */
    public void onNetworkComplete() {
    }

    /**
     * 接口返回数据成功. DO nothing by default!
     *
     * @param url
     * @param result
     */
    protected void onSucc(String url, T result) {
    }

    /**
     * 接口返回数据成功.仅使用接口下发MSG数据
     *
     * @param url
     * @param errorMsg
     */
    protected void onSuccWithMsg(String url, Result.ErrorMsg errorMsg) {
    }

    /**
     * 接口返回出错信息.
     *
     * @param url
     * @param errorMsg
     */
    protected void onError(String url, Result.ErrorMsg errorMsg) {
        ToastUtil.showMsg(errorMsg.getMsg());
        if (errorMsg.getErrno() == 100)
            UserInfo.getInstance().clearLoginInfo();
    }

    /**
     * 请求数据失败时回调
     *
     * @param errorType 出错类型 {@link ErrorType}
     * @param errorDesc 错误描述
     */
    protected void onFail(int errorType, String errorDesc) {
        ToastUtil.showMsg("网络错误，请稍后再试");
    }

    /**
     * 定义了请求出错类型
     */
    public static class ErrorType {

        /**
         * 其他类型的错误
         */
        public static final int OTHER_ERROR = 0x1;

        /**
         * 网络未连接
         */
        public static final int NO_CONNECTION = 0x2;

        /**
         * 网络超时，包括连接超时或响应超时
         */
        public static final int TIME_OUT = 0x3;

        /**
         * 其他网络错误
         */
        public static final int NETWORK_ERROR = 0x4;

        /**
         * 服务器返回错误
         */
        public static final int SERVER_ERROR = 0x5;
    }

}
