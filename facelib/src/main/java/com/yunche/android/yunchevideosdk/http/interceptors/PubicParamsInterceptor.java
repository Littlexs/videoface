package com.yunche.android.yunchevideosdk.http.interceptors;


import com.yunche.android.yunchevideosdk.YCBaseFace;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class PubicParamsInterceptor implements Interceptor {


        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            // 添加公共的新的参数  
            HttpUrl.Builder authorizedUrlBuilder = request.url()
                    .newBuilder();
            Map<String, Object> globalParams = new HashMap<>();//这里是我们的公共参数，根据项目实际情况获得，具体添加什么样的参数，在这里做逻辑判断即可
            globalParams.put("operatorId", YCBaseFace.operatorId);
            globalParams.put("operatorName", YCBaseFace.userName);
            globalParams.put("operatorRole", YCBaseFace.operatorRole);
            Iterator it = globalParams.entrySet().iterator();
            while (it.hasNext()) {  
                Map.Entry entry = (Map.Entry) it.next();
                //如果是中文/其他字符，会直接把字符串用BASE64加密，  
                //String s = URLDecoder.decode(String.valueOf(entry.getValue()));
                authorizedUrlBuilder.addQueryParameter((String) entry.getKey(), String.valueOf(entry.getValue()));
            }  
            //生成新的请求
            Request newrequest = request.newBuilder()
                    //.addHeader("Content-type","application/json;charset=utf-8")
                    .method(request.method(), request.body())
                    .url(authorizedUrlBuilder.build())
                    .build();  
      
            return chain.proceed(newrequest);  
        }
    }  