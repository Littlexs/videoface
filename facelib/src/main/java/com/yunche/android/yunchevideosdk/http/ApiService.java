package com.yunche.android.yunchevideosdk.http;


import com.yunche.android.yunchevideosdk.http.base.BaseNetService;

/**
 * Created by littlexs on 2017/12/20.
 */

public class ApiService extends BaseNetService {

    public static String BASE_URL = "";//接口host
    public static String WS = "";//socket
    public static String OSS_P_URL = "";//面签地址host
    public static String OSS_VIDEO_FACE_BUCKET = "";//oss面签文件桶
    public static String OSS_CHILD_VIDEO_BUCKET = "";//oss面签子文件桶

    private static ApiService apiService;

    static {
        if (apiService == null) {
            synchronized (ApiService.class) {
                if (apiService == null) {
                    apiService = new ApiService();
                }
            }
        }
    }

    public static MyApi myApi() {
        return baseRetrofit(BASE_URL).create(MyApi.class);
    }

}
