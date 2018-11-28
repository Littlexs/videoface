package com.yunche.android.yunchevideosdk.videoface.video;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.yunche.android.yunchevideosdk.Constants;
import com.yunche.android.yunchevideosdk.R;
import com.yunche.android.yunchevideosdk.YCBaseActivity;
import com.yunche.android.yunchevideosdk.cview.WaveView;
import com.yunche.android.yunchevideosdk.entity.LoanerListItem;
import com.yunche.android.yunchevideosdk.http.ApiService;
import com.yunche.android.yunchevideosdk.mvp.BasePresenter;
import com.yunche.android.yunchevideosdk.oss.OssService;
import com.yunche.android.yunchevideosdk.oss.OssUpdateInterface;
import com.yunche.android.yunchevideosdk.oss.OssUtils;
import com.yunche.android.yunchevideosdk.param.LocationParam;
import com.yunche.android.yunchevideosdk.param.WaitParam;
import com.yunche.android.yunchevideosdk.utils.DateUtils;
import com.yunche.android.yunchevideosdk.utils.DeviceUtils;
import com.yunche.android.yunchevideosdk.utils.PreferenceUtils;
import com.yunche.android.yunchevideosdk.utils.SpannableStringUtils;
import com.yunche.android.yunchevideosdk.utils.WeakReferenceHandler;
import com.yunche.android.yunchevideosdk.utils.event.AbstractFunction;
import com.yunche.android.yunchevideosdk.utils.event.FunctionManager;
import com.yunche.android.yunchevideosdk.utils.permission.PermissionsConstant;
import com.yunche.android.yunchevideosdk.utils.permission.PermissionsUtils;
import com.yunche.android.yunchevideosdk.utils.statusbar.StatusBarUtils;
import com.yunche.android.yunchevideosdk.videoface.video.stomp.LifecycleEvent;
import com.yunche.android.yunchevideosdk.videoface.video.stomp.Stomp;
import com.yunche.android.yunchevideosdk.videoface.video.stomp.client.StompClient;
import com.yunche.android.yunchevideosdk.videoface.video.stomp.client.StompMessage;
import com.yunche.android.yunchevideosdk.videoface.video.utils.ConfigEntity;
import com.yunche.android.yunchevideosdk.videoface.video.utils.ConfigService;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VideoStepFourActivity extends YCBaseActivity implements AnyChatBaseEvent, View.OnClickListener {


    TextView title;
    WaveView waveView;
    TextView tvTime;
    TextView tvBeforeNum;
    TextView tvNowNum;
    Button btn;

    private AnyChatCoreSDK anychat;
    private int dwUserId;
    private final int TIME_UPDATE = 291;        //Handler发送消息,队列人数的实时更新

    private LoanerListItem loanerListItem;

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_four;
    }


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        StatusBarUtils.with(this).invasionStatusBar();
        title = (TextView) findViewById(R.id.title);
        title.setText("视频面签");
        btn = (Button) findViewById(R.id.btn);
        waveView = (WaveView) findViewById(R.id.waveView);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvBeforeNum = (TextView) findViewById(R.id.tvBeforeNum);
        tvNowNum = (TextView) findViewById(R.id.tvNowNum);

        ViewGroup.LayoutParams layoutParams = waveView.getLayoutParams();
        layoutParams.width = DeviceUtils.getDisplay(mContext).widthPixels - 70;
        layoutParams.height = DeviceUtils.getDisplay(mContext).widthPixels - 70;
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) tvTime.getLayoutParams();
        layoutParams1.height = layoutParams.width / 3 + 30;
        layoutParams1.width = layoutParams.width / 3 + 30;
        tvTime.setLayoutParams(layoutParams1);
        waveView.setLayoutParams(layoutParams);
        waveView.setColor(Color.parseColor("#65B2F8"));
        waveView.setInitialRadius(layoutParams.width / 6 + 15);
        waveView.setMaxRadius(layoutParams.width / 2 - 60);
        waveView.start();
        FunctionManager.getInstance().addFuntion(new AbstractFunction("finishVideo") {
            @Override
            public Object funtion(Object o) {
                toVideo = false;
                onViewClicked();
                return null;
            }
        });
        FunctionManager.getInstance().addFuntion(new AbstractFunction("uploadFace") {
            @Override
            public Object funtion(Object o) {
                latlon();
                return null;
            }
        });
        loanerListItem = getIntent().getBundleExtra("item").getParcelable("item");
        initSDK();
        loginSDK();
        initData();

        btn.setOnClickListener(this);
    }


    private boolean toVideo = false;
    public void onViewClicked(){
        exitWait(true);//退出排队
        weakReferenceHandler.removeCallbacksAndMessages(null);
        if (!isFinish){
            tvTime.setText("0:0");
            waveView.stop();
        }
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
        // 本地视频采集偏色修正设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA,
                0);
        // 视频GPU渲染设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER,
                0);
        // 视频旋转模式设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL,
                0);
        // 本地视频自动旋转设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
                1);
        //录制裁剪模式
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_RECORD_CLIPMODE,
                2);
        // 让视频参数生效
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM,
                1);
    }

    public void loginSDK() {
        anychat.Connect("cloud.anychat.cn", 8906);
        anychat.Login(loanerListItem.getCustomer(), loanerListItem.getId());
    }

    /**
     * 初始化从前个界面传进来的数据
     */
    private void initData() {
        Message mes = weakReferenceHandler.obtainMessage(2);
        weakReferenceHandler.sendMessageDelayed(mes, 1000);
    }


    private int seconds = 0;
    WeakReferenceHandler weakReferenceHandler = new WeakReferenceHandler(this) {
        @Override
        protected void handleMessage(Object reference, Message msg) {
            Log.i("======tag", (msg.what == 1 ? "排队" : "时间") + msg.what);
            switch (msg.what) {
                case 1:
                    addWait();//加入排队
                    Message mes = weakReferenceHandler.obtainMessage(1);
                    weakReferenceHandler.sendMessageDelayed(mes, 5000);
                    break;
                case 2:
                    seconds++;
                    //int seconds = AnyChatCoreSDK.ObjectGetIntValue(AnyChatObjectDefine.ANYCHAT_OBJECT_TYPE_QUEUE, queueId, AnyChatObjectDefine.ANYCHAT_QUEUE_INFO_WAITTIMESECOND);
                    tvTime.setText(
                            SpannableStringUtils.getBuilder(DateUtils.getTimeShowString(seconds))
                                    .setProportion(1.5f).append("\n排队中，请稍后", mContext).create(mContext)
                    );
                    tvBeforeNum.setText("当前排队人数" + totalNum + "人");
                    tvNowNum.setText("您现在排在第" + rank + "人");
                    mes = weakReferenceHandler.obtainMessage(2);
                    weakReferenceHandler.sendMessageDelayed(mes, 1000);
                    break;
            }
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        //anychat.SetBaseEvent(this);
        if (mStompClient == null) {
            initSocket();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FunctionManager.getInstance().removeFuntion("finishVideo");
        FunctionManager.getInstance().removeFuntion("uploadFace");
        if (mStompClient!=null){
            mStompClient.disconnect();//关闭socket
        }
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
                Toast.makeText(mContext,"面签链接失败,重新连接中...",Toast.LENGTH_SHORT).show();
            }
            if (linkCount>=6){
                Toast.makeText(mContext,"面签链接失败,重新连接中...",Toast.LENGTH_SHORT).show();
                onViewClicked();
            }else {
                loginSDK();
            }
        }
    }

    private boolean loginAgain;
    private int loginAgainCount;
    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            loginAgain = false;
            loginAgainCount = 0;
            // 连接成功
            if (loginAgain){
                Log.i("==== anychat 重新登录成功: ", "dwUserId==" + dwUserId);
                anychat.EnterRoom(a_roomId, "");//进入房间
                return;
            }else {
                this.dwUserId = dwUserId;
                Log.i("==== anychat 登录成功: ", "dwUserId==" + dwUserId);
                //排队开始
                initSocket();
            }
        } else {
            Toast.makeText(mContext,"登录失败",Toast.LENGTH_SHORT).show();
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
            waveView.stop();
            if (PermissionsUtils.checkVideoPermission(this)) {
                weakReferenceHandler.removeCallbacksAndMessages(null);
                toVideo = true;
                if (pcAnyChatUserId != 0) {
                    applyVideoConfig(false);
                    latlon();
                    //人工面签
                    toVideo();
                } else {
                    applyVideoConfig(true);
                    if (TextUtils.isEmpty(videoUrl)){
                        Toast.makeText(mContext,"没有面签语音",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //机器面签
                    checkAudioFile();
                }
            }
        }else {
            enterCount++;
            if (enterCount>=6){//尝试多次失败后
                Toast.makeText(mContext,"进入房间失败",Toast.LENGTH_SHORT).show();
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
        //intent = new Intent(mContext,MachineVideoLocalActivity.class);
        intent = new Intent(mContext, MachineVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("item",loanerListItem);
        intent.putExtra("item",bundle);
        intent.putExtra("videoUrl",videoUrl);
        intent.putExtra("localPath",localPath);
        intent.putExtra("audioInfoName",audioInfoName);
        intent.putExtra("houzhui",houzhui);
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
        Toast.makeText(mContext,"网络异常",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("您确定要退出当前排队吗?")
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
            if (requestCode==PermissionsConstant.REQUEST_VIDEO){
                if (pcAnyChatUserId != 0) {
                    applyVideoConfig(false);
                    latlon();
                    //人工面签
                    toVideo();
                } else {
                    applyVideoConfig(true);
                    //机器面签
                    checkAudioFile();
                }
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
            if (requestCode== PermissionsConstant.REQUEST_VIDEO){
                PermissionsUtils.checkVideoPermission(this);
            }
        }
    }


    private void toVideo() {
        Intent intent = new Intent();
        intent.setClass(mContext, VideoActivity.class);
        intent.putExtra("UserID", pcAnyChatUserId);
        intent.putExtra("userType", 0);
        startActivity(intent);
    }

    //排队逻辑
    public StompClient mStompClient;

    private void initSocket() {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> cookies = new HashMap<>();
        HashSet<String> preferences = (HashSet) PreferenceUtils.getStringSet(mContext, "PREF_COOKIES", new HashSet<>());
        for (String cookie : preferences) {
            if (cookie.contains("rememberMe")){
                continue;
            }
            stringBuilder.append(cookie);
            Log.v("socket传入cookie：", "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
        }
        cookies.put("Cookie", stringBuilder.toString());
        mStompClient = Stomp.over(Stomp.ConnectionProvider.JWS, ApiService.WS+"api/v1/videoFace/websocket", cookies);
        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LifecycleEvent>() {
                    @Override
                    public void accept(LifecycleEvent lifecycleEvent) throws Exception {
                        switch (lifecycleEvent.getType()) {
                            case OPENED:
                                Log.d("sdh", "连接已开启");
                                waitInfo();//排队信息
                                machine();//机器面签
                                call();//通话
                                Message mes = weakReferenceHandler.obtainMessage(1);
                                weakReferenceHandler.sendMessageDelayed(mes, 0);
                                break;
                            case ERROR:
                                Log.e("sdh", "连接出错 Stomp Error", lifecycleEvent.getException());
                                Toast.makeText(mContext,"链接失败",Toast.LENGTH_SHORT).show();
                                if (!isFinish){
                                    onViewClicked();
                                }
                                break;
                            case CLOSED:
                                Log.d("sdh", "连接关闭 Stomp connection closed");
                                if (!isFinish){
                                    onViewClicked();
                                }
                                break;
                        }
                    }
                });
        mStompClient.connect();
    }

    //APP端排队信息
    private int rank, totalNum;

    public void waitInfo() {
        mStompClient.topic("/user/queue/team/info/app")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StompMessage>() {
                    @Override
                    public void accept(StompMessage stompMessage) throws Exception {
                        Log.d(TAG, "排队信息 " + stompMessage.getPayload());
                        JSONObject jsonObject = JSON.parseObject(stompMessage.getPayload());
                        if (jsonObject.getBoolean("success")) {
                            rank = jsonObject.getJSONObject("data").getIntValue("rank");
                            totalNum = jsonObject.getJSONObject("data").getIntValue("totalNum");
                        }
                    }
                });
    }

    //机器面签
    private String videoUrl;
    private boolean comfirmShow = true;
    public void machine() {
        mStompClient.topic("/user/queue/faceSign/machine")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StompMessage>() {
                    @Override
                    public void accept(StompMessage stompMessage) throws Exception {
                        exitWait(false);//退出排队
                        Log.d(TAG, "机器面签 " + stompMessage.getPayload());
                        JSONObject jsonObject = JSON.parseObject(stompMessage.getPayload());
                        if (jsonObject.getBoolean("success") && comfirmShow) {
                            int faceSign = jsonObject.getJSONObject("data").getIntValue("faceSign");
                            videoUrl = jsonObject.getJSONObject("data").getString("voicePath");
                            if (faceSign == 2) {//1-人工面签;  2-机器面签;
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setCancelable(false);
                                builder.setMessage("即将进入机器面签,是否继续?")
                                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                //进入机器面签
                                                //机器面签
                                                a_roomId = new Random().nextInt(1000000000);
                                                anychat.EnterRoom(a_roomId, "");//进入房间
                                            }
                                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        onViewClicked();
                                    }
                                }).create().show();
                                comfirmShow = false;
                            }
                        }
                    }
                });
    }

    //请求通话，返回room id   /user/queue/call/app
    private int pcAnyChatUserId;
    private int a_roomId;
    public void call() {
        mStompClient.topic("/user/queue/call/app")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StompMessage>() {
                    @Override
                    public void accept(StompMessage stompMessage) throws Exception {
                        exitWait(false);//退出排队
                        Log.d(TAG, "请求通话 " + stompMessage.getPayload());
                        JSONObject jsonObject = JSON.parseObject(stompMessage.getPayload());
                        if (jsonObject.getBoolean("success") && comfirmShow) {
                            final String roomId = jsonObject.getJSONObject("data").getString("roomId");
                            pcAnyChatUserId = jsonObject.getJSONObject("data").getIntValue("pcAnyChatUserId");
                            String appAnyChatUserId = jsonObject.getJSONObject("data").getString("appAnyChatUserId");
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setCancelable(false);
                            builder.setMessage("工作人员请求与您通话，是否同意?")
                                    .setPositiveButton("同意", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            //进入房间通话
                                            a_roomId = Integer.parseInt(roomId);
                                            anychat.EnterRoom(Integer.parseInt(roomId), "");//进入房间
                                        }
                                    }).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    onViewClicked();
                                }
                            }).create().show();
                            comfirmShow = false;
                        }
                    }
                });
    }

    //加入排队
    public void addWait() {
        WaitParam param = new WaitParam();
        param.setUserId(Long.valueOf(loanerListItem.getCustomerId()));
        param.setBankName(loanerListItem.getBankName());
        param.setType(2);
        param.setBankId(loanerListItem.getBankId());
        param.setBankPeriodPrincipal(loanerListItem.getBankPeriodPrincipal());
        param.setAnyChatUserId(String.valueOf(dwUserId));
        param.setOrderId(loanerListItem.getId());
        mStompClient.send("/api/v1/ws/team/wait", JSON.toJSONString(param))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "排队信息发送 successfully");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "排队 Error send STOMP echo", throwable);
                    }
                });
    }

    //定位上传
    public void latlon() {
        //退出排队
        String addressStr = PreferenceUtils.getString(getApplicationContext(), "addr");
        String address = null;
        try {
            address = URLDecoder.decode(addressStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String latitude = PreferenceUtils.getString(getApplicationContext(), "latitude");
        String longitude = PreferenceUtils.getString(getApplicationContext(), "longitude");
        String local = latitude + "," + longitude;
        LocationParam locationParam = new LocationParam();
        locationParam.setAddress(address);
        locationParam.setBankName(loanerListItem.getBankName());
        locationParam.setPcAnyChatUserId(pcAnyChatUserId);
        locationParam.setLatlon(local);
        //String jsonStr = "{\"address\":" + address + ",\"bankName\":" + loanerListItem.getBankName() + ",\"pcAnyChatUserId\":" + dwUserId + ",\"latlon\":" + local + "}";
        mStompClient.send("/api/v1/ws/latlon", JSON.toJSONString(locationParam))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "定位发送 successfully");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "定位 Error send STOMP echo", throwable);
                    }
                });
    }

    //退出排队
    public void exitWait(final boolean disconnect) {
        if (mStompClient==null){
            return;
        }
        WaitParam param = new WaitParam();
        param.setUserId(Long.valueOf(loanerListItem.getCustomerId()));
        param.setBankName(loanerListItem.getBankName());
        param.setType(2);
        param.setBankId(loanerListItem.getBankId());
        param.setBankPeriodPrincipal(loanerListItem.getBankPeriodPrincipal());
        param.setAnyChatUserId(String.valueOf(dwUserId));
        param.setOrderId(loanerListItem.getId());
        mStompClient.send("/api/v1/ws/team/exit", JSON.toJSONString(param))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "退出排队发送 successfully");
                        if (disconnect){
                            mStompClient.disconnect();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "排队 Error send STOMP echo", throwable);
                    }
                });
    }


    //下载机器面签音频文件
    public void checkAudioFile(){
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
                    Toast.makeText(mContext,"文件加载失败，请重试",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn) {
            alertDialog();

        }
    }

}
