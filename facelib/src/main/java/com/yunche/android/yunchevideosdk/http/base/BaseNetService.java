package com.yunche.android.yunchevideosdk.http.base;


import com.yunche.android.yunchevideosdk.http.converter.FastJsonConverterFactory;
import com.yunche.android.yunchevideosdk.http.interceptors.PubicParamsInterceptor;
import com.yunche.android.yunchevideosdk.http.interceptors.ReadCookiesInterceptor;
import com.yunche.android.yunchevideosdk.http.interceptors.SaveCookiesInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class BaseNetService {


    private static OkHttpClient client;
    private static Retrofit retrofit;

    static {
        if (client == null) {
            synchronized (BaseNetService.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            //.cookieJar(new CookieManger(YunCheApp.getYunCheApp()))
//                            .cookieJar(new CookieJar() {
//                                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
//                                @Override
//                                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                                    cookieStore.put(url.host(), cookies);
//
//                                }
//
//                                @Override
//                                public List<Cookie> loadForRequest(HttpUrl url) {
//                                    List<Cookie> cookies = cookieStore.get(url.host());
//                                    return cookies != null ? cookies : new ArrayList<Cookie>();
//                                }
//                            })
                            .addInterceptor(new PubicParamsInterceptor())//用于添加公共参数
                            .addInterceptor(new ReadCookiesInterceptor())
                            .addInterceptor(new SaveCookiesInterceptor())
                            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))//拦截器，用于日志的打印
                            .build();
                }
            }
        }
    }

    public static String tempUrl;
    protected static Retrofit baseRetrofit(String baseUrl) {
        if (retrofit == null) {
            synchronized (BaseNetService.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .client(client)
                            .baseUrl(baseUrl)
                            .addConverterFactory(FastJsonConverterFactory.create())//默认直接转化为实体类，不会进行解密等处理，如需加解密请自定义转换器
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Retrofit2与Rxjava2之间结合的适配器
                            .build();
                }
            }
        }else {
            if (!baseUrl.equals(tempUrl)){
                retrofit = new Retrofit.Builder()
                        .client(client)
                        .baseUrl(baseUrl)
                        .addConverterFactory(FastJsonConverterFactory.create())//默认直接转化为实体类，不会进行解密等处理，如需加解密请自定义转换器
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Retrofit2与Rxjava2之间结合的适配器
                        .build();
            }
        }
        tempUrl = baseUrl;
        return retrofit;
    }
}