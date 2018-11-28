package com.yunche.android.yunchevideosdk.http.base;

public class ApiException extends RuntimeException {
    private String mErrorCode;
    private String msg;

    public ApiException(String errorCode, String errorMessage) {
        super(errorMessage);
        mErrorCode = errorCode;
        msg = errorMessage;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 判断是否是token失效 假设为
     * @return 失效返回true, 否则返回false;
     */
    public boolean isTokenExpried() {
        return "0".equals(mErrorCode);
    }

    public String getCode(){return mErrorCode;}
}