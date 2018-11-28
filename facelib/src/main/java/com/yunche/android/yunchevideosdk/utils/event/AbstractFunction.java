package com.yunche.android.yunchevideosdk.utils.event;

abstract public class AbstractFunction<Result ,Params> {

    public String mFunctionName;

    public AbstractFunction(String mFunctionName) {
        this.mFunctionName = mFunctionName;
    }



    /**
     *
     * @param params
     * @return
     */
    public abstract Result funtion(Params params);



}
