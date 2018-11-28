package com.yunche.android.yunchevideosdk.mvp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * Created by shengxiao on 2018/1/2.
 */

public abstract class BasePresenter<T> {

    protected WeakReference<T> mViewRef;//弱引用 防止内存泄漏

    //绑定
    public void attachView(T iView){
        mViewRef = new WeakReference<T>(iView);
        getView(mViewRef);
    }
    //获取view
    protected abstract void getView(WeakReference<T> mViewRef);
    //解绑
    public void detachView(){
        mViewRef.clear();
    }
//    //生命周期
//    public void setLifecycleTransformer(V transformer){
//        this.lifecycleTransformer = transformer;
//    }
//    public void setContext(E context){
//        this.mContext = context;
//    }

    protected void openActivity(Context mContext,Class<? extends AppCompatActivity> cls) {
        mContext.startActivity(new Intent(mContext, cls));
    }


}
