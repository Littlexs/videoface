package com.yunche.android.yunchevideosdk.videoface.video;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.yunche.android.yunchevideosdk.R;
import com.yunche.android.yunchevideosdk.cview.NetSeepView;
import com.yunche.android.yunchevideosdk.cview.UIAlertView;
import com.yunche.android.yunchevideosdk.utils.DateUtils;
import com.yunche.android.yunchevideosdk.utils.DeviceUtils;
import com.yunche.android.yunchevideosdk.utils.event.FunctionManager;
import com.yunche.android.yunchevideosdk.utils.statusbar.StatusBarUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends AppCompatActivity implements AnyChatBaseEvent {

    // handle send msg
    private static final int MSG_VIDEOGESPREK = 1;	// 视频对话时间刷新消息
    private static final int MSG_PREVIEWPIC = 2;    // 拍照预览倒计时刷新消息
    private final int UPDATEVIDEOBITDELAYMILLIS = 200; //监听音频视频的码率的间隔刷新时间（毫秒）

    private final String mStrBasePath = "/AnyChat";

    private TextView mPreviewFilePath;		   // 用于显示拍照的相片和本地录制视频的存储路径的显示

    int userID;
    private boolean bSelfVideoOpened = false;  // 本地视频是否已打开
    private boolean bOtherVideoOpened = false; // 对方视频是否已打开
    private int mVideogesprekSec = 0;		   // 音视频对话的时间
    private Boolean mFirstGetVideoBitrate = false; //"第一次"获得视频码率的标致
    private Boolean mFirstGetAudioBitrate = false; //"第一次"获得音频码率的标致

    private SurfaceView mOtherView;
    private SurfaceView mMyView;
    private ImageView mImgBtnReturn;		  // 返回
    private TextView mTitleName;			  // 标题
    private ImageView mImgSwitchVideo;	  // 切换设备前后摄像头
    private ImageView mEndCallBtn;
    private ImageButton mBtnSpeakCtrl; 		  // 控制音频的按钮
    private Dialog mDialog;
    private TextView mVideogesprekTimeTV;  	  // 视频对话时间
    private Timer mVideogesprekTimer;

    private TimerTask mTimerTask;
    private Handler mHandler;
    private NetSeepView netSeepView;

    public AnyChatCoreSDK anyChatSDK;

    //------------------------------拍照--
    private String mPreviewPicPathStr = "";  // 拍照图片存储路径
    private ImageView mPreviewPicIV;		 // 拍照图片预览view
//    private ImageButton mIBTakePhotoSelf;	 // 自拍
//    private ImageButton mIBTakePhotoOther;   // 拍照
    private Timer mPreviewPicTimer = null;
    private int mPreviewPicSec = 0;			   // 预览图片的剩余时间
    //--拍照------------------------------

    private NetworkChangeReceiver networkChangeReceiver;

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    //Toast.makeText(context, "当前网络可用", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getApplicationContext(),"网络异常",Toast.LENGTH_SHORT).show();
                    destroyCurActivity();
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        StatusBarUtils.with(this).invasionStatusBar();

        Intent intent = getIntent();
        userID = intent.getIntExtra("UserID",0);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

        InitSDK();
        InitLayout();
        updateTime();

        //如果视频流过来了，则把背景设置成透明的
        handler.postDelayed(runnable, UPDATEVIDEOBITDELAYMILLIS);
    }

    private void InitSDK() {
        anyChatSDK = AnyChatCoreSDK.getInstance(this);
        anyChatSDK.SetBaseEvent(this);
        anyChatSDK.mSensorHelper.InitSensor(getApplicationContext());
        AnyChatCoreSDK.mCameraHelper.SetContext(getApplicationContext());
    }

    private void InitLayout() {
        netSeepView = (NetSeepView)findViewById(R.id.netSeepView);
        mMyView = (SurfaceView) findViewById(R.id.surface_local);
        mOtherView = (SurfaceView) findViewById(R.id.surface_remote);
        mImgBtnReturn = (ImageView) this.findViewById(R.id.back);
        mTitleName = (TextView) this.findViewById(R.id.title);
        mImgSwitchVideo = (ImageView) findViewById(R.id.video_switch);
        mEndCallBtn = (ImageView) findViewById(R.id.endCall);
        mBtnSpeakCtrl = (ImageButton) findViewById(R.id.btn_speakControl);
        mVideogesprekTimeTV = (TextView) findViewById(R.id.videogesprekTime);
        mPreviewPicIV = (ImageView) findViewById(R.id.previewPhoto);
        mPreviewFilePath = (TextView) findViewById(R.id.previewFilePath);
        mTitleName.setText("与 \"" + anyChatSDK.GetUserName(userID) + "\" 对话中");
        mBtnSpeakCtrl.setOnClickListener(onClickListener);
        mImgSwitchVideo.setOnClickListener(onClickListener);
        mEndCallBtn.setOnClickListener(onClickListener);
        mPreviewPicIV.setOnClickListener(onClickListener);

        int w = AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL);
        int h = AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL);

        int wD = DeviceUtils.getDisplay(getApplicationContext()).widthPixels;
        int viewH = wD*w/h;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mMyView.getLayoutParams();
        layoutParams.height = viewH;
        mMyView.setLayoutParams(layoutParams);


        // 如果是采用Java视频采集，则需要设置Surface的CallBack
        if (AnyChatCoreSDK
                .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            mMyView.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
        }

        // 如果是采用Java视频显示，则需要设置Surface的CallBack
        if (AnyChatCoreSDK
                .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
            int index = anyChatSDK.mVideoHelper.bindVideo(mOtherView
                    .getHolder());
            anyChatSDK.mVideoHelper.SetVideoUser(index, userID);
        }

        mOtherView.setZOrderOnTop(true);

        anyChatSDK.UserCameraControl(userID, 1);
        anyChatSDK.UserSpeakControl(userID, 1);

        // 判断是否显示本地摄像头切换图标
        if (AnyChatCoreSDK
                .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            if (AnyChatCoreSDK.mCameraHelper.GetCameraNumber() > 1) {
                // 默认打开前置摄像头
                AnyChatCoreSDK.mCameraHelper
                        .SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_BACK);
            }
        } else {
            String[] strVideoCaptures = anyChatSDK.EnumVideoCapture();
            if (strVideoCaptures != null && strVideoCaptures.length > 1) {
                // 默认打开前置摄像头
                for (int i = 0; i < strVideoCaptures.length; i++) {
                    String strDevices = strVideoCaptures[i];
                    if (strDevices.indexOf("Front") >= 0) {
                        anyChatSDK.SelectVideoCapture(strDevices);
                        break;
                    }
                }
            }
        }

        // 根据屏幕方向改变本地surfaceview的宽高比
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            adjustLocalVideo(true);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            adjustLocalVideo(false);
        }

        anyChatSDK.UserCameraControl(-1, 1);// -1表示对本地视频进行控制，打开本地视频
        anyChatSDK.UserSpeakControl(-1, 1);// -1表示对本地音频进行控制，打开本地音频

    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                int videoBitrate = anyChatSDK.QueryUserStateInt(userID, AnyChatDefine.BRAC_USERSTATE_VIDEOBITRATE);
                int audioBitrate = anyChatSDK.QueryUserStateInt(userID, AnyChatDefine.BRAC_USERSTATE_AUDIOBITRATE);
                if (videoBitrate > 0)
                {
                    //handler.removeCallbacks(runnable);
                    mFirstGetVideoBitrate = true;
                    mMyView.setBackgroundColor(Color.TRANSPARENT);
                }

                if(audioBitrate > 0){
                    mFirstGetAudioBitrate = true;
                }

                if (mFirstGetVideoBitrate)
                {
                    if (videoBitrate <= 0){
                        //Toast.makeText(VideoActivity.this, "对方视频中断了!", Toast.LENGTH_SHORT).show();
                        // 重置下，如果对方退出了，有进去了的情况
                        mFirstGetVideoBitrate = false;
                    }
                }

                if (mFirstGetAudioBitrate){
                    if (audioBitrate <= 0){
                        //Toast.makeText(VideoActivity.this, "对方音频中断了！", Toast.LENGTH_SHORT).show();
                        // 重置下，如果对方退出了，有进去了的情况
                        mFirstGetAudioBitrate = false;
                    }
                }
                handler.postDelayed(runnable, UPDATEVIDEOBITDELAYMILLIS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void updateTime() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    // 刷新视频对话时间
                    case MSG_VIDEOGESPREK:
                        mVideogesprekTimeTV.setText(DateUtils.getTimeShowString(mVideogesprekSec++));
                        break;
                    // 拍照预览10秒后隐藏
                    case MSG_PREVIEWPIC:
                        if (mPreviewPicSec <= 0){
                            mPreviewPicTimer.cancel();
                            mPreviewPicTimer = null;
                            mPreviewPicIV.setVisibility(View.GONE);
                            mPreviewFilePath.setVisibility(View.GONE);
                        }
                        else {
                            mPreviewPicSec -= 1;
                        }
                        break;
                    default:
                        break;
                }
            }

        };

        initVideogesprekTimer();
    }

    // 初始化视频对话定时器
    private void initVideogesprekTimer()
    {
        if (mVideogesprekTimer == null)
            mVideogesprekTimer = new Timer();

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_VIDEOGESPREK);
            }
        };

        mVideogesprekTimer.schedule(mTimerTask, 1000, 1000);
    }


    // 初始化拍照图片预览定时器
    private void initPreviewPicTimer() {
        if (mPreviewPicTimer == null){
            mPreviewPicTimer = new Timer();
        }

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_PREVIEWPIC);
            }
        };

        mPreviewPicTimer.schedule(mTimerTask, 10, 1000);
        mPreviewPicIV.setVisibility(View.VISIBLE);
        mPreviewFilePath.setVisibility(View.VISIBLE);
    }

    // 点击事件
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            int i1 = view.getId();
            if (i1 == R.id.video_switch) {
                if (AnyChatCoreSDK
                        .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
                    AnyChatCoreSDK.mCameraHelper.SwitchCamera();
                    return;
                }
                String strVideoCaptures[] = anyChatSDK.EnumVideoCapture();
                String temp = anyChatSDK.GetCurVideoCapture();
                for (int i = 0; i < strVideoCaptures.length; i++) {
                    if (!temp.equals(strVideoCaptures[i])) {
                        anyChatSDK.UserCameraControl(-1, 0);
                        bSelfVideoOpened = false;
                        anyChatSDK.SelectVideoCapture(strVideoCaptures[i]);
                        anyChatSDK.UserCameraControl(-1, 1);
                        break;
                    }
                }

                // End Call
            } else if (i1 == R.id.endCall) {
                showEndVideoDialog();
                // 控制自己语音的开关
//                case R.id.btn_speakControl:
//                    if ((anyChatSDK.GetSpeakState(-1) == 1)) {
//                        mBtnSpeakCtrl.setImageResource(R.drawable.speak_off);
//                        anyChatSDK.UserSpeakControl(-1, 0);
//                    } else {
//                        mBtnSpeakCtrl.setImageResource(R.drawable.speak_on);
//                        anyChatSDK.UserSpeakControl(-1, 1);
//                    }
//
//                    break;
                // 控制自己视频的开关
//                case R.id.btn_cameraControl:
//                    if ((anyChatSDK.GetCameraState(-1) == 2)) {
//                        mBtnCameraCtrl.setImageResource(R.drawable.camera_off);
//                        anyChatSDK.UserCameraControl(-1, 0);
//                    } else {
//                        mBtnCameraCtrl.setImageResource(R.drawable.camera_on);
//                        anyChatSDK.UserCameraControl(-1, 1);
//                    }
//                    break;
                // 自拍
//                case R.id.btn_TakePhotoSelf:
//                    //anyChatSDK.SnapShot(-1, AnyChatDefine.ANYCHAT_RECORD_FLAGS_SNAPSHOT, 0);
//
//                    //BaseMethod.playSound(VideoActivity.this, BaseMethod.PHOTOSSOUNDID);
//
//                    break;
//                // 拍照
//                case R.id.btn_TakePhotoOther:
//                    //anyChatSDK.SnapShot(userID, AnyChatDefine.ANYCHAT_RECORD_FLAGS_SNAPSHOT, 0);
//                    //BaseMethod.playSound(VideoActivity.this, BaseMethod.PHOTOSSOUNDID);
//
//                    break;
                // 图片预览事件
            } else if (i1 == R.id.previewPhoto) {
                intent = new Intent("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromFile(new File(mPreviewPicPathStr));
                intent.setDataAndType(uri, "image/*");
                startActivity(intent);

            } else {
            }
        }
    };

    public void back(View v) {
        destroyCurActivity();
    }

    // 关闭视频呼叫确认框
    private void showEndVideoCallDialog() {
        final UIAlertView uiAlertView = new UIAlertView(this,"","结束通话？","是","否");
        uiAlertView.setClicklistener(new UIAlertView.ClickListenerInterface() {
            @Override
            public void doLeft() {
                uiAlertView.dismiss();
                anyChatSDK.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH, userID, 0,
                        0, -1, "");
                finish();
            }

            @Override
            public void doRight() {
                uiAlertView.dismiss();
            }
        });
        uiAlertView.show();
    }

    // 关闭视频确认框
    private void showEndVideoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
        builder.setMessage("您确定要结束视频通话吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

    private void refreshAV() {
        anyChatSDK.UserCameraControl(userID, 1);
        anyChatSDK.UserSpeakControl(userID, 1);
        anyChatSDK.UserCameraControl(-1, 1);
        anyChatSDK.UserSpeakControl(-1, 1);
        mBtnSpeakCtrl.setImageResource(R.drawable.speak_on);
//        mBtnCameraCtrl.setImageResource(R.drawable.camera_on);
        bOtherVideoOpened = false;
        bSelfVideoOpened = false;
    }

    private void destroyCurActivity() {

        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // 如果是采用Java视频显示，则需要设置Surface的CallBack
        if (AnyChatCoreSDK
                .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
            int index = anyChatSDK.mVideoHelper.bindVideo(mOtherView
                    .getHolder());
            anyChatSDK.mVideoHelper.SetVideoUser(index, userID);
        }

        refreshAV();
    }

    @Override
    protected void onResume() {
        super.onResume();
        netSeepView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        netSeepView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);

        handler.removeCallbacks(runnable);
        anyChatSDK.UserCameraControl(userID, 0);
        anyChatSDK.UserSpeakControl(userID, 0);
        anyChatSDK.UserCameraControl(-1, 0);
        anyChatSDK.UserSpeakControl(-1, 0);
        anyChatSDK.removeEvent(this);
        anyChatSDK.mSensorHelper.DestroySensor();
        FunctionManager.getInstance().invokeFunc("finishVideo","finishVideo");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            showEndVideoDialog();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void adjustLocalVideo(boolean bLandScape) {
        float width;
        float height = 0;
        DisplayMetrics dMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
        width = (float) dMetrics.widthPixels / 4;
        LinearLayout layoutLocal = (LinearLayout) this
                .findViewById(R.id.frame_local_area);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layoutLocal
                .getLayoutParams();
        if (bLandScape) {

            if (AnyChatCoreSDK
                    .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL) != 0){
                height = width
                        * AnyChatCoreSDK
                        .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL)
                        / AnyChatCoreSDK
                        .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL)
                        + 5;
            }
            else{
                height = (float) 3 / 4 * width + 5;
            }
        } else {

            if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL) != 0){
                height = width* AnyChatCoreSDK
                        .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL)
                        / AnyChatCoreSDK
                        .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL)
                        + 5;
            }
            else{
                height = (float) 4 / 3 * width + 5;
            }
        }
        layoutParams.width = (int) width;
        layoutParams.height = (int) height;
        layoutLocal.setLayoutParams(layoutParams);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            adjustLocalVideo(true);
            AnyChatCoreSDK.mCameraHelper.setCameraDisplayOrientation();
        } else {
            adjustLocalVideo(false);
            AnyChatCoreSDK.mCameraHelper.setCameraDisplayOrientation();
        }

    }

    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {

    }

    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {

    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {

    }

    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {

    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        if (!bEnter) {
            if (dwUserId == userID) {
                Toast.makeText(VideoActivity.this, "对方已离开！", Toast.LENGTH_SHORT).show();
                userID = 0;
                anyChatSDK.UserCameraControl(dwUserId, 0);
                anyChatSDK.UserSpeakControl(dwUserId, 0);
                bOtherVideoOpened = false;

                // 销毁当前界面
                destroyCurActivity();
            }

        } else {
            if (userID != 0)
                return;

            int index = anyChatSDK.mVideoHelper.bindVideo(mOtherView.getHolder());
            anyChatSDK.mVideoHelper.SetVideoUser(index, dwUserId);

            anyChatSDK.UserCameraControl(dwUserId, 1);
            anyChatSDK.UserSpeakControl(dwUserId, 1);
            userID = dwUserId;
        }
    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
        // 网络连接断开之后，上层需要主动关闭已经打开的音视频设备
        if (bOtherVideoOpened) {
            anyChatSDK.UserCameraControl(userID, 0);
            anyChatSDK.UserSpeakControl(userID, 0);
            bOtherVideoOpened = false;
        }
        if (bSelfVideoOpened) {
            anyChatSDK.UserCameraControl(-1, 0);
            anyChatSDK.UserSpeakControl(-1, 0);
            bSelfVideoOpened = false;
        }

        Intent mIntent = new Intent("NetworkDiscon");
        // 发送广播
        sendBroadcast(mIntent);

        // 销毁当前界面
        destroyCurActivity();
    }


}
