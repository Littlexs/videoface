package com.yunche.android.yunchevideosdk.http;

import com.alibaba.fastjson.JSON;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by littlexs on 2017/12/20.
 */

public class ResultBody<T> {
    private String code;
    private String msg;
    private boolean success;
    private int totalPage;
    private T data;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static RequestBody getJson(Map<String,String> map){
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(map));
    }

    public static RequestBody getObjectJson(Object object){
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(object));
    }
}
