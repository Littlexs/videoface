package com.yunche.android.yunchevideosdk.http.interceptors;


import android.util.Log;

import com.yunche.android.yunchevideosdk.YCBaseFace;
import com.yunche.android.yunchevideosdk.utils.PreferenceUtils;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ReadCookiesInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> preferences = (HashSet) PreferenceUtils.getStringSet(YCBaseFace.mContext,"PREF_COOKIES", new HashSet<>());
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            Log.v("- cookie", "----- Adding Header: " + cookie);
        }
        return chain.proceed(builder.build());
    }
}