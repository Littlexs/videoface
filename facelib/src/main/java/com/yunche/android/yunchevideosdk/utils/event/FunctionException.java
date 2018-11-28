package com.yunche.android.yunchevideosdk.utils.event;

public class FunctionException extends RuntimeException {
    private int mErrorCode;

    public FunctionException( String errorMessage) {
        super(errorMessage);
    }

}