package com.meu.morseimage.phpTest.http;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 接口请求
 */
public class ServerRequest<T> extends Request<Result>
{
    /**
     * 默认的连接超时和响应超时时间
     */
    public static final int DEFAULT_TIME_OUT = 15000;

    private Class<T> mClassOfT;

    private ResponseListener mPtResponseListener;

    private String mRequestUrl;

    private Map<String, Object> mPostParams;

    /**
     * 构建get请求
     *
     * @param url
     */
    public ServerRequest(String url, Class<T> classOfT, ResponseListener ptResponseListener) {
        this(false, url, classOfT, ptResponseListener, DEFAULT_TIME_OUT);
    }

    /**
     * 构建http post 请求
     *
     * @param url
     */
    public ServerRequest(String url, Map<String, Object> params, Class<T> classOfT, ResponseListener ptResponseListener) {
        this(true, url, classOfT, ptResponseListener, DEFAULT_TIME_OUT);
        this.mPostParams = params;
    }

    /**
     * 构建http请求
     *
     * @param post false为get请求,true为post请求
     */
    private ServerRequest(boolean post, String url, Class<T> classOfT, ResponseListener ptResponseListener, int timeout) {
        super(post ? Method.POST : Method.GET, url, null);

        this.mClassOfT = classOfT;
        this.mPtResponseListener = ptResponseListener;
        this.mRequestUrl = url;

        //设置请求超时
        setRetryPolicy(new DefaultRetryPolicy(timeout, 0, 1f));
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError
    {
        if (mPostParams != null) {
            Map<String, String> tempParams = new HashMap<>();
            for (Map.Entry<String, Object> entry : mPostParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                tempParams.put(key, (String) value);
            }
            return tempParams;
        }
        return super.getParams();
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            try {
                parsed = new String(response.data, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
                parsed = "";
            }
        }
        Result<T> result = parseJson(parsed);
        Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
        return Response.success(result, cacheEntry);
    }

    private Result<T> parseJson(String json) {
        Result<T> result = new Result<>();
        Gson gson = new Gson();
        if (mClassOfT.equals(String.class)) {
            result.setStatus(Result.STATUS_OK);
            result.setData((T) json);
            return result;
        }
        try {
            JSONObject jObject = new JSONObject(json);
            int status = jObject.getInt("status");

            result.setStatus(status);
            String errmsgJson;
            Result.ErrorMsg errMsg;
            switch (result.getStatus()) {
                case Result.STATUS_OK:
                    try {
                        JSONArray array = jObject.getJSONArray("data");

                        // 未发生异常说明data是数组
                        result.setArrayResult(true);
                        List<T> ts = new ArrayList<T>(array.length());
                        for (int i = 0; i < array.length(); i++) {
                            ts.add(gson.fromJson(array.get(i).toString(), mClassOfT));
                        }
                        result.setArrayData(ts);
                    } catch (JSONException e) {
                        //data是单个对象
                        result.setArrayResult(false);
                        T t = gson.fromJson(jObject.optString("data"), mClassOfT);
                        result.setData(t);
                    }
                    errmsgJson = jObject.optString("errmsg");
                    if (TextUtils.isEmpty(errmsgJson)) {
                        errmsgJson = jObject.optString("errormsg");
                    }
                    errMsg = gson.fromJson(errmsgJson, Result.ErrorMsg.class);
                    result.setErrmsg(errMsg);
                    break;
                case Result.STATUS_ERR:
                    errmsgJson = jObject.optString("errmsg");
                    if (TextUtils.isEmpty(errmsgJson)) {
                        errmsgJson = jObject.optString("errormsg");
                    }
                    errMsg = gson.fromJson(errmsgJson, Result.ErrorMsg.class);
                    result.setErrmsg(errMsg);
                    break;
                default:
                    result.setStatus(Result.STATUS_UNKNOWN);
                    result.setUnknownBody(new Result.UnknownBody(json));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus(Result.STATUS_UNKNOWN);
            result.setUnknownBody(new Result.UnknownBody(json));
        }
        return result;
    }

    @Override
    protected void deliverResponse(Result result) {
        mPtResponseListener.onNetworkComplete();

        switch (result.getStatus()) {
            case Result.STATUS_OK:
                if (result.isArrayResult()) {
                    if (result.getArrayData().size() == 0) {
                        mPtResponseListener.onSucc(mRequestUrl, null);
                    } else {
                        mPtResponseListener.onSucc(mRequestUrl, result.getArrayData());
                    }
                } else {
                    mPtResponseListener.onSucc(mRequestUrl, result.getData());
                }
                mPtResponseListener.onSuccWithMsg(mRequestUrl, result.getErrmsg());
                break;
            case Result.STATUS_ERR:
                mPtResponseListener.onError(mRequestUrl, result.getErrmsg());
                break;
            case Result.STATUS_UNKNOWN:
                mPtResponseListener.onFail(ResponseListener.ErrorType.OTHER_ERROR, "Unknown pt json status:" + result.getStatus());
                break;
            default:
                break;
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        mPtResponseListener.onNetworkComplete();

        int errorType;
        if (error instanceof NoConnectionError) {
            errorType = ResponseListener.ErrorType.NO_CONNECTION;
        } else if (error instanceof TimeoutError) {
            errorType = ResponseListener.ErrorType.TIME_OUT;
        } else if (error instanceof NetworkError) {
            errorType = ResponseListener.ErrorType.NETWORK_ERROR;
        } else if (error instanceof ServerError) {
            errorType = ResponseListener.ErrorType.SERVER_ERROR;
        } else {
            errorType = ResponseListener.ErrorType.OTHER_ERROR;
        }

        String errDesc;
        if (error.networkResponse != null) {
            errDesc = error.getClass().getSimpleName() + " code=" + error.networkResponse.statusCode;
        } else {
            errDesc = error.getClass().getSimpleName() + " " + error.getMessage();
        }

        mPtResponseListener.onFail(errorType, errDesc);
    }
}
