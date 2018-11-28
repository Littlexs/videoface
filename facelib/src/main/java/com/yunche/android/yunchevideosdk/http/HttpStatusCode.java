package com.yunche.android.yunchevideosdk.http;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by littlexs on 2017/12/20.
 */

public class HttpStatusCode implements Parcelable {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
    }

    public HttpStatusCode() {
    }

    protected HttpStatusCode(Parcel in) {
        this.code = in.readString();
    }

    public static final Creator<HttpStatusCode> CREATOR = new Creator<HttpStatusCode>() {
        @Override
        public HttpStatusCode createFromParcel(Parcel source) {
            return new HttpStatusCode(source);
        }

        @Override
        public HttpStatusCode[] newArray(int size) {
            return new HttpStatusCode[size];
        }
    };
}
