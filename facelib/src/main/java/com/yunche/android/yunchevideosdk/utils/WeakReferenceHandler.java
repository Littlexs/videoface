package com.yunche.android.yunchevideosdk.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class WeakReferenceHandler<T> extends Handler {
    private WeakReference<T> mReference;
      
    public WeakReferenceHandler(T reference) {  
        mReference = new WeakReference<T>(reference);   
    }  
  //content://com.yunche.finance.common.fileprovider/external_files/yunche/new.apk
    @Override  
    public void handleMessage(Message msg) {
        // TODO Auto-generated method stub  
        super.handleMessage(msg);  
        if(mReference.get() == null)  
            return ;  
        handleMessage(mReference.get(), msg);  
    }  
      
    protected abstract void handleMessage(T reference, Message msg);  
      
}