package com.yunche.android.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yunche.android.yunchevideosdk.cview.UIAlertView;
import com.yunche.android.yunchevideosdk.cview.loadingdialog.CircleProgressAlertView;
import com.yunche.android.yunchevideosdk.entity.FileProgress;
import com.yunche.android.yunchevideosdk.entity.LoanerListItem;
import com.yunche.android.yunchevideosdk.http.ApiService;
import com.yunche.android.yunchevideosdk.oss.OssInterface;
import com.yunche.android.yunchevideosdk.oss.OssService;
import com.yunche.android.yunchevideosdk.oss.OssUpdateInterface;
import com.yunche.android.yunchevideosdk.oss.OssUtils;
import com.yunche.android.yunchevideosdk.utils.permission.PermissionsConstant;
import com.yunche.android.yunchevideosdk.utils.permission.PermissionsUtils;
import com.yunche.android.yunchevideosdk.videoface.video.VideoStepFourActivity;
import com.yunche.android.yunchevideosdk.videoface.video.VideotTestActivity;
import com.yunche.android.yunchevideosdk.videoface.video.utils.VideoConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Context mContext;

    private boolean toVideoNext;

    private int type;

    //必传字段
    //customerId customer  bankName
    //bankId  bankPeriodPrincipal  id  partner carDetailId  carName
    //carPrice  idCard
    private LoanerListItem loanerListItem = new LoanerListItem();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mContext = this;
    }

    public void onClick(View view) {

        type = Integer.parseInt(String.valueOf(view.getTag()));

        if (PermissionsUtils.checkVideoPermission(this)) {

            toNext();

        }else {
            Toast.makeText(getApplicationContext(),"缺少必要权限",Toast.LENGTH_SHORT).show();
            return;
        }

    }


    public void toNext(){
        switch (type){
            case 1://面签测试
                Intent intent = new Intent(this, VideotTestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("item",loanerListItem);
                intent.putExtra("item",bundle);
                startActivity(intent);
                break;
            case 2://人工面签
                loanerListItem.setCustomerId("6222310101642232");
                loanerListItem.setCustomer("王依薇demo");
                loanerListItem.setBankName("中国工商银行台州路桥支行");
                loanerListItem.setBankId("3");
                loanerListItem.setBankPeriodPrincipal("121800.00");
                loanerListItem.setId("1811191717305755106");
                loanerListItem.setPartner("浙江温州二部王俊团队");
                loanerListItem.setPartnerId(56);
                loanerListItem.setCarDetailId("1397");
                loanerListItem.setCarName("奥迪 奥迪A6L 2005款 2.0T 基本型");
                loanerListItem.setCarPrice("250000.00");
                loanerListItem.setIdCard("230300199203191442");

                intent = new Intent(this, VideoStepFourActivity.class);
                bundle = new Bundle();
                bundle.putParcelable("item",loanerListItem);
                intent.putExtra("item",bundle);
                startActivity(intent);
                break;
            case 3://机器面签
                loanerListItem.setCustomerId("118385");
                loanerListItem.setCustomer("严治勇demo");
                loanerListItem.setBankName("中国工商银行台州路桥支行");
                loanerListItem.setBankId("3");
                loanerListItem.setBankPeriodPrincipal("69000.00");
                loanerListItem.setId("1811011547357809167");
                loanerListItem.setPartner("4浙江台州王威团队");
                loanerListItem.setPartnerId(74);
                loanerListItem.setCarDetailId("34733");
                loanerListItem.setCarName("吉利汽车 远景S1 2018款 1.5L CVT锋享型");
                loanerListItem.setCarPrice("88000.00");
                loanerListItem.setIdCard("612322198112153615");
                loanerListItem.setCarPrice("90000");// < 30W   机器面签

                intent = new Intent(this, VideoStepFourActivity.class);
                bundle = new Bundle();
                bundle.putParcelable("item",loanerListItem);
                intent.putExtra("item",bundle);
                startActivity(intent);
                break;
            case 4://机器面签参数设置
                setting(true);
                break;
            case 5://人工面签参数设置
                setting(false);
                break;
            case 6://上传面签视频
                uploadVideoFiles("1234567890");
                break;
            case 7://上传文件
                uploadFiles("1234567890");
                break;
            case 8://下载文件
                getFile();
                break;
        }
    }

    //参数设置
    public void setting(boolean isMachineVideo) {
        Intent intent = new Intent(this, VideoConfig.class);
        intent.putExtra("isMachineVideo", isMachineVideo);
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", loanerListItem);
        intent.putExtra("item", bundle);
        startActivity(intent);
    }

    /*
  * 权限back
  * */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            switch (requestCode) {
                case PermissionsConstant.REQUEST_VIDEO:
                    List<String> deniedPermissionList = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissionList.add(permissions[i]);
                        }
                    }
                    if (deniedPermissionList.isEmpty()) {

                        toNext();

                    } else {
                        //勾选了对话框中”Don’t ask again”的选项, 返回false
                        for (String deniedPermission : deniedPermissionList) {
                            boolean flag = shouldShowRequestPermissionRationale(deniedPermission);
                            if (!flag) {
                                //拒绝授权
                                preSetting();
                                return;
                            }
                        }
                        //拒绝授权
                        PermissionsUtils.checkVideoPermission(this);
                    }
                    break;
            }
        }
    }

    public void  preSetting(){
        final UIAlertView alertView = new UIAlertView(this,"提示","缺少必要权限，请前往设置页面设置。","取消","前往设置");
        alertView.setClicklistener(new UIAlertView.ClickListenerInterface() {
            @Override
            public void doLeft() {
                alertView.dismiss();
            }

            @Override
            public void doRight() {
                alertView.dismiss();
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        alertView.show();
    }




    //上传面签视频
    public void uploadVideoFiles(String orderId) {

        ArrayList<String> files = new ArrayList<>();

        //测试路径，请自行替换
        files.add("/storage/emulated/0/aa_yunche_video_face/yan_zhi_yongd/yanzhiyongd_20181128101953.mp4");

        boolean isImg = false;

        final CircleProgressAlertView progressAlertView = new CircleProgressAlertView(this);//进度条
        progressAlertView.setCanceledOnTouchOutside(false);
        progressAlertView.show();
        final OssService ossService = OssUtils.initOSS(this, ApiService.OSS_VIDEO_FACE_BUCKET);//oss上传服务

        ossService.isImgUpload(isImg);

        ossService.asyncPutVideoFaceFiles(files, "test-name", orderId, new OssInterface() {
            @Override
            public void ossNetSuccess(int position, final String keyName) {
                Log.i("===", "上传成功" + keyName);
                progressAlertView.setFinishProgressColor();
                progressAlertView.dismiss();

                Log.i("---视频URL：",OssUtils.getVideoFaceUrl(keyName));
            }

            @Override
            public void ossProgress(int position, FileProgress fileProgress) {
                Log.i("===", "上传中："+fileProgress.getProgress());
                progressAlertView.setProgress(fileProgress.getProgress(), 0);
            }

            @Override
            public void ossNetError(String info) {
                Log.i("===", "上传失败" + info);
                progressAlertView.dismiss();
            }
        });
    }


    private int sum = 0;
    /**
     //上传文件  支持图片和视频,暂不支持其他文件格式
     * @param orderId  订单id，形成文件路径用
     */
    public void uploadFiles(String orderId){

        Toast.makeText(mContext,"开始上传",Toast.LENGTH_SHORT).show();

        String bucket = "yunche-2018";//oss文件桶

        final ArrayList<String> files = new ArrayList<>();//本地文件路径
        //这里是上次视频测试
        files.add("/storage/emulated/0/aa_yunche_video_face/yan_zhi_yongd/yanzhiyongd_20181128101953.mp4");


        boolean isImg = false;

        OssService ossService = OssUtils.initOSS(this,bucket);//oss服务
        ossService.isImgUpload(isImg);//图片为true，视频为false
        ossService.asyncPutFiles(files,orderId, new OssInterface() {
            @Override
            public void ossNetSuccess(int position,String keyName) {
                sum++;//上传一个成功
                //keyName : oss文件key
                if (sum==files.size()){//全部上传成功
                    sum=0;
                    //全部上传成功
                    Toast.makeText(mContext,"上传成功",Toast.LENGTH_SHORT).show();
                    Log.i("---文件URL：",getOssUrl(keyName));
                }
            }

            @Override
            public void ossProgress(int position,FileProgress fileProgress) {
                //上传中,建议不要在里面操作任何事情
            }

            @Override
            public void ossNetError(String info) {
                //上传失败
            }
        });
    }


    //下载文件
    public void getFile(){

        String bucket = "yunche-base";//oss文件桶
        String ossUrlKey = "apk/yunche_1.76_76.apk";//oss 文件key

        //本地存放路径
        String filePath = Environment.getExternalStorageDirectory() + "/yunche/testtest.apk";

        OssService ossService = OssUtils.initOSS(getApplicationContext(),bucket);
        ossService.asyncGetFile(bucket, ossUrlKey, filePath, new OssUpdateInterface() {
            @Override
            public void ossNetSuccess(String localUrl) {
                File file = new File(localUrl);
                Log.i("----下载成功","  size："+file.length());
            }

            @Override
            public void ossProgress(int progress) {
                Log.i("----下载中","   "+progress);
            }

            @Override
            public void ossNetError(String info) {
                Log.i("----下载失败",info);
            }
        });
    }

    //通过oss key 获取完整url
    public String getOssUrl(String keyName){
        return OssUtils.getOssUrl(mContext,keyName);
    }

    //取消上传
    public void cancelUpload(OssService ossService){
        ossService.cancel();
    }

}
