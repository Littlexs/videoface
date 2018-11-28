package com.yunche.android.yunchevideosdk.videoface.video;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.yunche.android.yunchevideosdk.Constants;
import com.yunche.android.yunchevideosdk.R;
import com.yunche.android.yunchevideosdk.YCBaseActivity;
import com.yunche.android.yunchevideosdk.entity.LoanerListItem;
import com.yunche.android.yunchevideosdk.http.ApiService;
import com.yunche.android.yunchevideosdk.mvp.BasePresenter;
import com.yunche.android.yunchevideosdk.oss.OssService;
import com.yunche.android.yunchevideosdk.oss.OssUpdateInterface;
import com.yunche.android.yunchevideosdk.oss.OssUtils;
import com.yunche.android.yunchevideosdk.utils.event.AbstractFunction;
import com.yunche.android.yunchevideosdk.utils.event.FunctionManager;
import com.yunche.android.yunchevideosdk.utils.permission.PermissionsConstant;
import com.yunche.android.yunchevideosdk.utils.permission.PermissionsUtils;
import com.yunche.android.yunchevideosdk.utils.statusbar.StatusBarUtils;
import com.yunche.android.yunchevideosdk.videoface.video.utils.ConfigEntity;
import com.yunche.android.yunchevideosdk.videoface.video.utils.ConfigService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class VideotTestActivity extends YCBaseActivity implements AnyChatBaseEvent {


    TextView title;

    private AnyChatCoreSDK anychat;
    private int dwUserId;
    private final int TIME_UPDATE = 291;        //Handler发送消息,队列人数的实时更新

    private LoanerListItem loanerListItem;

    private boolean initOk;

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_test;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1212 && !initOk){
                Toast.makeText(getApplicationContext(),"您可以尝试退出重新进入",Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        StatusBarUtils.with(this).invasionStatusBar();
        title = (TextView) findViewById(R.id.title);
        title.setText("视频测试");
        FunctionManager.getInstance().addFuntion(new AbstractFunction("finishVideo") {
            @Override
            public Object funtion(Object o) {
                toVideo = false;
                onViewClicked();
                return null;
            }
        });
        loanerListItem = getIntent().getBundleExtra("item").getParcelable("item");
        showLoadingDialog(R.string.anychat_loading);
        initSDK();
        loginSDK();
        handler.sendEmptyMessageDelayed(1212,6000);
    }


    private boolean toVideo = false;
    public void onViewClicked(){
        finish();
    }

    private void initSDK() {
        if (anychat == null) {
            anychat = AnyChatCoreSDK.getInstance(this);
            anychat.SetBaseEvent(this);
            anychat.InitSDK(Build.VERSION.SDK_INT, 0);
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
                    1);
        }
        AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CLOUD_APPGUID, Constants.ANYCHAT_ID);
    }

    // 根据配置文件配置视频参数
    private void applyVideoConfig(boolean machineVideo) {
        ConfigEntity configEntity = ConfigService.LoadConfig(this);
//        if (configEntity.mConfigMode == 1) // 自定义视频参数配置
//        {
            // 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL,
                    machineVideo?configEntity.mmVideoBitrate:configEntity.mVideoBitrate);
//			if (configEntity.mVideoBitrate == 0) {
            // 设置本地视频编码的质量
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL,
                    machineVideo?configEntity.mmVideoQuality:configEntity.mVideoQuality);
//			}
            // 设置本地视频编码的帧率
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL,
                    machineVideo?configEntity.mmVideoFps:configEntity.mVideoFps);
            // 设置本地视频编码的关键帧间隔
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL,
                    machineVideo?configEntity.mgop:configEntity.gop);
            // 设置本地视频采集分辨率
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL,
                    machineVideo?configEntity.mmResolutionWidth:configEntity.mResolutionWidth);
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL,
                    machineVideo?configEntity.mmResolutionHeight:configEntity.mResolutionHeight);
            // 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL,
                    3);
//        }
        // P2P设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC,
                0);
        // 本地视频Overlay模式设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY,
                1);
        //噪音抑制
//        if (machineVideo){
//            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_NSCTRL,
//                    1);
//        }
        // 回音消除设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL,
                1);
        // 平台硬件编码设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC,
                0);
        // 视频旋转模式设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL,
                0);
        // 本地视频采集偏色修正设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA,
                0);
        // 视频GPU渲染设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER,
                0);
        // 本地视频自动旋转设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
                1);


        // 让视频参数生效
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM,
                1);
    }

    public void loginSDK() {
        anychat.Connect("cloud.anychat.cn", 8906);
        anychat.Login(loanerListItem.getCustomer(), loanerListItem.getId());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        FunctionManager.getInstance().removeFuntion("finishVideo");
        handler.removeCallbacksAndMessages(null);
        if (anychat != null && !toVideo) {
            anychat.LeaveRoom(-1);
            anychat.Logout();
            anychat.removeEvent(this);
            anychat.Release();
        }
    }

    int linkCount = 0;
    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
        if (!bSuccess) {
            linkCount++;
            if (linkCount==1){//只提示一次
                Toast.makeText(getApplicationContext(),"面签链接失败,重新连接中...",Toast.LENGTH_SHORT).show();
            }
            if (linkCount>=6){
                Toast.makeText(getApplicationContext(),"面签链接失败,请检查网络...",Toast.LENGTH_SHORT).show();
                onViewClicked();
            }else {
                loginSDK();
            }
        }
    }

    private boolean loginAgain;
    private int a_roomId;
    private int loginAgainCount;
    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            loginAgain = false;
            loginAgainCount = 0;
            // 连接成功
            a_roomId = new Random().nextInt(1000000000);
            anychat.EnterRoom(a_roomId, "");//进入房间
        } else {
            Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT).show();
            loginAgain = true;
            loginAgainCount++;
            if (loginAgainCount>6){
                onViewClicked();
            }else {
                loginSDK();
            }
        }
    }

    int enterCount;
    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        //进入房间成功
        if (dwErrorCode == 0) {
            enterCount = 0;
            if (PermissionsUtils.checkVideoPermission(this)) {
                checkAudioFile();
            }
        }else {
            enterCount++;
            if (enterCount>=6){//尝试多次失败后
                Toast.makeText(getApplicationContext(),"进入房间失败",Toast.LENGTH_SHORT).show();
                onViewClicked();
                return;
            }
            anychat.EnterRoom(a_roomId, "");//进入房间
        }
    }


    //进入机器面签
    //localPath 存放子音频的文件夹路径
    //num  子音频数量
    public void toVideoFace(String localPath,String audioInfoName,String houzhui){
        applyVideoConfig(true);
        //intent = new Intent(mContext,MachineVideoLocalActivity.class);
        dissMissDialog();
        intent = new Intent(mContext, MachineVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("item",loanerListItem);
        intent.putExtra("item",bundle);
        intent.putExtra("videoUrl",videoUrl);
        intent.putExtra("localPath",localPath);
        intent.putExtra("audioInfoName",audioInfoName);
        intent.putExtra("houzhui",houzhui);
        intent.putExtra("test",true);
        startActivity(intent);
    }

    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {

    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {

    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
        Toast.makeText(getApplicationContext(),"网络异常",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("您确定要退出吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
//                        AnyChatCoreSDK.ObjectControl(AnyChatObjectDefine.ANYCHAT_OBJECT_TYPE_QUEUE, queueId,
//                                AnyChatObjectDefine.ANYCHAT_QUEUE_CTRL_USERLEAVE, 0, 0, 0, 0, "");
                        onViewClicked();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

    @Override
    public void back(View v) {
        alertDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            alertDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            permissionBack(permissions,grantResults,requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void permissionBack(String[] permissions,int[] grantResults, int requestCode){
        List<String> deniedPermissionList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissionList.add(permissions[i]);
            }
        }
        if (deniedPermissionList.isEmpty()) {
            //已经全部授权
            if (requestCode== PermissionsConstant.REQUEST_VIDEO){
                checkAudioFile();
            }
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
            if (requestCode==PermissionsConstant.REQUEST_VIDEO){
                PermissionsUtils.checkVideoPermission(this);
            }
        }
    }

    //机器面签
    //这里是机器面签测试录制，请问您的名称是？
    //
    private String videoUrl = "https://yunche-base.oss-cn-hangzhou.aliyuncs.com/video-face-voice/Test_4_3-5-9-0_2.m4a";


    //下载机器面签音频文件
    public void checkAudioFile(){
        initOk = true;
        //videoUrl = "https://yunche-base.oss-cn-hangzhou.aliyuncs.com/video-face-voice/HangZhouChengZhan_26_4-4-5-4-11-5-8-5-5-6-5-6-6-6-7-7-6-6-6-5-6-5-5-5-60-0_1.m4a";
        //获取面签的文件名 如：TaiZhouLuQiao_8_44444444_1  以"_"分隔，0：名称，1：子音频数量，2：每个子音频的间隔数，3：音频版本号
        String audioInfoName = videoUrl.substring(videoUrl.lastIndexOf("/")+1,videoUrl.lastIndexOf("."));
        String[] mp3Info = audioInfoName.split("_");
        String houzhui = videoUrl.substring(videoUrl.lastIndexOf("."),videoUrl.length());
        houzhui = ".mp3";
        //  ../../../TaiZhouLuQiao1/
        String rootPath = Environment.getExternalStorageDirectory().getPath() + "/"+mp3Info[0]+mp3Info[3];

        String ossRootPath = videoUrl.substring(0,videoUrl.lastIndexOf("/")+1)+mp3Info[0]+"/";

        File file = new File(rootPath);
        if (file.exists()){//如果存在路径
            //校验是否存在对应数量的子音频
            File[] files=file.listFiles();
            if (files==null || files.length<Integer.parseInt(mp3Info[1])){
                //下载
                downOssFile(Integer.parseInt(mp3Info[1]),mp3Info[0],houzhui,mp3Info[0]+mp3Info[3],audioInfoName);
            }else {
                //进入面签
                toVideoFace(mp3Info[0]+mp3Info[3],audioInfoName,houzhui);
            }
        }else {
            //文件不存在，则需要下载
            file.mkdirs();
            downOssFile(Integer.parseInt(mp3Info[1]),mp3Info[0],houzhui,mp3Info[0]+mp3Info[3],audioInfoName);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dissMissDialog();
    }

    private AtomicInteger count;////已下载的数量
    public void downOssFile(final int mp3Num, String ossRootPath, final String houzhui,
                            final String localPath, final String audioInfoName){
        showLoadingDialog(R.string.loading_resourse);
        count = new AtomicInteger(0);
        for (int i = 1; i <= mp3Num; i++) {
            OssService ossService = OssUtils.initOSS(getApplicationContext(),ApiService.OSS_CHILD_VIDEO_BUCKET);
            ossService.asyncGetFaceFile(ApiService.OSS_CHILD_VIDEO_BUCKET, "video-face-voice/"+ossRootPath+"/",i+houzhui,localPath, new OssUpdateInterface() {
                @Override
                public void ossNetSuccess(String localUrl) {
                    count.incrementAndGet();
                    Log.i("-----------",count+" ----cvcv");
                    if (count.get()==mp3Num){
                        //进入面签
                        dissMissDialog();
                        toVideoFace(localPath,audioInfoName,houzhui);
                    }
                }

                @Override
                public void ossProgress(int progress) {

                }

                @Override
                public void ossNetError(String info) {
                    dissMissDialog();
                    Toast.makeText(getApplicationContext(),"文件加载失败，请重试",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

}
