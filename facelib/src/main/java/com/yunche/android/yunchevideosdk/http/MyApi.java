package com.yunche.android.yunchevideosdk.http;

import com.yunche.android.yunchevideosdk.demo.MemberInfo;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by littlexs on 2017/12/20.
 */

public interface MyApi {

    String API ="api/v1/";

    @POST(API+"/employee/login")
    Observable<ResultBody<MemberInfo>> login(@Body RequestBody body);

    //保存面签记录
    @POST(API+"/videoFace/log/save")
    Observable<ResultBody<Long>> saveVideoFace(@Body RequestBody body);

}
