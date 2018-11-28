package com.yunche.android.yunchevideosdk.videoface.video;

/**
 * Created by shengxiao on 2018/10/26.
 */

public class AudioUrlBean {
    private String url;
    private int sec;//间隔一下个音频的秒
    private int index;//序号


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
