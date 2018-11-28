package com.yunche.android.yunchevideosdk.mvp;

import com.yunche.android.yunchevideosdk.http.ResultBody;

/**
 * Created by shengxiao on 2018/1/2.
 */

public interface IBaseView {

    void showLoading();
    void dissMissLoading();
    interface ModelInterface<V,T extends ResultBody<V>>{
        void onSuccess(V result);
        void onError();
    }
}
