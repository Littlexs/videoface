package com.yunche.android.yunchevideosdk.http.base;

import android.content.Context;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.yunche.android.yunchevideosdk.http.ResultBody;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.HttpException;


public abstract class BaseObserver<T> implements Observer<ResultBody<T>> {

    private static final String TAG = "BaseObserver";
    private Context mContext;
    private String RESPONSE_FATAL_EOR = "-1";
    private String errorCode;
    private String errorMsg="未知的错误！";

    public BaseObserver(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onSubscribe(Disposable d) {
        //可以添加加载框的显示与监听
        Log.i("-BaseObserver-",d.toString());
    }

    private int totalPage;

    @Override
    public void onNext(ResultBody<T> value) {
        //if (value.getCode().equals("EC00000200")) {
            if (value.isSuccess()){
                totalPage = value.getTotalPage();
                onHandleSuccess(value.getData());
            }else {
                onHandleError(value.getCode(), value.getMsg());
            }
       // } else {
           // onHandleError(value.getCode(), value.getMsg());
        //}
    }

    public int getTotalPage(){
        return totalPage;
    }

    @Override
    public void onError(Throwable t) {
        if (t instanceof ApiException) {//自定义业务
            ApiException apiException = (ApiException) t;
            errorCode = "EC00000"+apiException.getCode();
            errorMsg = apiException.getMsg();
        }else if (t instanceof HttpException) {//以下是其它状态判断
            HttpException httpException = (HttpException) t;
            ResponseBody errorBody = httpException.response().errorBody();
            errorCode = "EC00000"+httpException.code();
            try {
                ResultBody resultBody = JSONObject.parseObject(errorBody.string(),ResultBody.class);
                errorMsg = TextUtils.isEmpty(resultBody.getMsg())?httpException.code()+"":resultBody.getMsg();
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = httpException.code()+"系统异常";
            }
        } else if (t instanceof SocketTimeoutException) {  //VPN open
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "服务器响应超时";
        } else if (t instanceof ConnectException) {
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "网络连接异常，请检查网络";
        }else if (t instanceof UnknownHostException) {
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "无法解析主机，请检查网络连接";
        } else if (t instanceof UnknownServiceException) {
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "未知的服务器错误";
        } else if (t instanceof IOException) {  //飞行模式等
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "没有网络，请检查网络连接";
        } else if (t instanceof NetworkOnMainThreadException) {//主线程不能网络请求，这个很容易发现
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "主线程不能网络请求";
        } else if (t instanceof RuntimeException) { //很多的错误都是extends RuntimeException
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "运行时错误,可能是返回数据格式与本地数据格式不匹配";
        }
        onHandleError(errorCode,errorMsg);
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete");

        //FunctionManager.getInstance().removeFuntion("NOT_LOGIN");
    }


    protected abstract void onHandleSuccess(T result);

    protected void onHandleError(String code, String msg) {
        if (!TextUtils.isEmpty(code) && mContext != null) {
            disposeEorCode(code,msg);
        }
    }

    /**
     * 对通用问题的统一拦截处理,根据项目的特定的做法
     * 比如token过期，被迫下线等
     * @param code
     */
    private final void disposeEorCode(String code, String msg) {

        if (mContext != null&& Thread.currentThread().getName().toString().equals("main")) {
            //Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
            toast(msg );
        }

        switch (code) {
            case "EC00000101":
                break;
            case "EC00000NOT_LOGIN":
                //YCBaseFace.getYunCheApp().toLogin();
                break;
        }

    }

    public void toast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

}