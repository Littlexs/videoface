package com.yunche.android.yunchevideosdk.oss;

/**
 * 创建人 ： shengxiao
 * 创建时间 ：2018/3/6
 * 类描述 ：
 * 备注 ：
 */

public interface OssUpdateInterface {
    void ossNetSuccess(String localUrl);
    void ossProgress(int progress);
    void ossNetError(String info);
}
