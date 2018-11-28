package com.yunche.android.yunchevideosdk.oss;


import com.yunche.android.yunchevideosdk.entity.FileProgress;

/**
 * 创建人 ： shengxiao
 * 创建时间 ：2018/3/6
 * 类描述 ：
 * 备注 ：
 */

public interface OssInterface {
    void ossNetSuccess(int position, String keyName);
    void ossProgress(int position, FileProgress fileProgress);
    void ossNetError(String info);
}
