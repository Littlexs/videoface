package com.yunche.android.yunchevideosdk.http.interceptors;

import android.util.Log;

import com.yunche.android.yunchevideosdk.YCBaseFace;
import com.yunche.android.yunchevideosdk.utils.PreferenceUtils;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

public class SaveCookiesInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                if (!header.contains("deleteMe")){
                    cookies.add(header);
                    Log.v("- cookie", "----- Adding Header: " + header);
                }
            }
            PreferenceUtils.setStringSet(YCBaseFace.mContext,"PREF_COOKIES",cookies);
        }
        return originalResponse;
    }
}