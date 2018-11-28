package com.yunche.android.yunchevideosdk.entity;

/**
 * 创建人 ： shengxiao
 * 创建时间 ：2018/3/6
 * 类描述 ：文件上传过程实体类
 * 备注 ：
 */

public class FileProgress {
    private int progress;
    private String url;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
