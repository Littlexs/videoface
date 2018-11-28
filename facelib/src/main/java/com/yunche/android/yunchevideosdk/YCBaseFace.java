package com.yunche.android.yunchevideosdk;

import android.content.Context;

import com.yunche.android.yunchevideosdk.demo.MemberInfo;
import com.yunche.android.yunchevideosdk.http.ApiService;
import com.yunche.android.yunchevideosdk.oss.OssUtils;
import com.yunche.android.yunchevideosdk.utils.PreferenceUtils;


/**
 * 创建人 ： shengxiao
 * 创建时间 ：2018/2/27
 * 类描述 ：
 * 备注 ：
 */

public class YCBaseFace {

    public static String userName;
    public static String operatorId;
    public static String operatorRole;

    public static Context mContext;


    public static void init(Context context,String apiHost,String wsHost){
        mContext = context;

        ApiService.BASE_URL = apiHost;
        ApiService.WS = wsHost;

        userName = PreferenceUtils.getString(context,"userName");
        operatorId = PreferenceUtils.getString(context,"operatorId");
        operatorRole = PreferenceUtils.getString(context,"operatorRole");
    }

    public static void initVideoOssKey(String ossKey,String videoId){
        OssUtils.setAccessKeyId(ossKey);
        Constants.ANYCHAT_ID = videoId;
    }

    public static void initOSSBase(String endpoint,String baseBucket){
        OssUtils.setEndpoint(endpoint);
        OssUtils.setBaseBucket(baseBucket);
    }

    public static void initVideoFaceBase(String baseUrl,String faceBucket,String childBucket){
        ApiService.OSS_P_URL = baseUrl;
        ApiService.OSS_VIDEO_FACE_BUCKET = faceBucket;
        ApiService.OSS_CHILD_VIDEO_BUCKET = childBucket;
    }

    public static void saveLoginInfo(MemberInfo memberInfo){
        PreferenceUtils.setString(mContext,"userName",memberInfo.getUsername());
        PreferenceUtils.setString(mContext,"operatorId",String.valueOf(memberInfo.getUserId()));
        PreferenceUtils.setString(mContext,"operatorRole","");
        YCBaseFace.operatorId = String.valueOf(memberInfo.getUserId());
        YCBaseFace.userName = memberInfo.getUsername();
    }
}
