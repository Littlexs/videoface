package com.yunche.android.yunchevideosdk.oss;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.yunche.android.yunchevideosdk.http.ApiService;

import java.util.ArrayList;

/**
 * 创建人 ： shengxiao
 * 创建时间 ：2018/3/6
 * 类描述 ：
 * 备注 ：
 */

public class OssUtils {

    private static String endpoint = "";
    private static final String imgEndpoint = "http://img-cn-hangzhou.aliyuncs.com";
    private static final String callbackAddress = "http://oss-demo.aliyuncs.com:23450";
    private static final String accessKeySecret = "UlIzIv8Mp30OQkhOatRufB5bBwQVRN";
    private static String bucket = "";
    private String region = "杭州";

    private static  ClientConfiguration conf;

    public static void setEndpoint(String endpointStr){
        endpoint = endpointStr;
    }

    public static void setBaseBucket(String baseBucket){
        bucket = baseBucket;
    }

    private static ClientConfiguration getClientConfiguration(){
        if (conf==null){
            synchronized (OssUtils.class){
                conf = new ClientConfiguration();
                conf.setConnectionTimeout(30 * 60000); // 连接超时，默认15秒
                conf.setSocketTimeout(15 * 60000); // socket超时，默认15秒
                conf.setMaxConcurrentRequest(30); // 最大并发请求书，默认30个
                conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            }
        }
       return conf;
    }


    //初始化一个OssService用来上传下载
    public static OssService initOSS(Context context) {
        //如果希望直接使用accessKey来访问的时候，可以直接使用OSSPlainTextAKSKCredentialProvider来鉴权。
        ////OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);

//        OSSCredentialProvider credentialProvider;
//        //使用自己的获取STSToken的类
//        String stsServer = ((EditText) findViewById(R.id.stsserver)).getText().toString();
//        if (stsServer .equals("")) {
//            credentialProvider = new STSGetter();
//        }else {
//            credentialProvider = new STSGetter(stsServer);
//        }

        //bucket = ((EditText) findViewById(R.id.bucketname)).getText().toString();

        ////OSS oss = new OSSClient(context, endpoint, credentialProvider, getClientConfiguration());
        return new OssService(getOss(context), bucket);

    }

    //初始化一个OssService用来上传下载
    public static OssService initOSS(Context context,String bucket) {
        return new OssService(getOss(context), bucket);

    }


    public static OSS getOss(Context context){
        OSSCredentialProvider credentialProvider = null;
        OSS oss = null;
        //if (credentialProvider == null){
            credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
        //}
        //if (oss == null){
            oss = new OSSClient(context, endpoint, credentialProvider, getClientConfiguration());
        //}
        return oss;
    }

    //获取面签url
    public static String getVideoFaceUrl(String keyName){
        return ApiService.OSS_P_URL+keyName;
    }

    public static String getOssUrl(Context context,String name){
        if (TextUtils.isEmpty(name)){
            return "http";
        }
        try {
            return OssUtils.getOss(context).presignConstrainedObjectURL(bucket,name,300*60);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return "http";
    }


    public static ArrayList<String> getOssUrlList(Context context,ArrayList<String> keyList){
        ArrayList<String> ossList = new ArrayList<>();
        for (String ossUrl : keyList){
            ossList.add(getOssUrl(context,ossUrl));
        }
        return ossList;
    }


    private static String accessKeyId = "";

    public static void setAccessKeyId(String id){
        accessKeyId = id;
    }

}
