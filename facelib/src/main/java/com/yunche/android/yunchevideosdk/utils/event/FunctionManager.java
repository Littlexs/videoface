package com.yunche.android.yunchevideosdk.utils.event;


import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class FunctionManager<Parmas> {

    public FunctionManager() {
        funtionMap = new HashMap<>();
    }

    private static FunctionManager instance;

    public synchronized static FunctionManager getInstance() {
        if(instance == null){
            instance = new FunctionManager();
        }
        return instance;
    }

    private Map<String , AbstractFunction> funtionMap;

    public FunctionManager addFuntion(AbstractFunction function){
        funtionMap.put(function.mFunctionName , function);
        return this;
    }

    public FunctionManager removeFuntion(String funcName){
        funtionMap.remove(funcName);
        return this;
    }

    public boolean haveFunction(String funcName){
        if(funtionMap != null) {
            AbstractFunction f = funtionMap.get(funcName);
            return f != null;
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public void invokeFunc(String funcName , Parmas parmas){
        if(TextUtils.isEmpty(funcName)){
            return;
        }
        if(funtionMap != null){
            AbstractFunction f = funtionMap.get(funcName);
           if(f != null){
               f.funtion(parmas);
           }
           if(f == null){
               try {
                   throw new FunctionException("Has no this function" + funcName);
               } catch (FunctionException e) {
                   e.printStackTrace();
               }
           }
        }
    }
    
}
