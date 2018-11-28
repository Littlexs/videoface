package com.yunche.android.yunchevideosdk.videoface.video;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatCoreSDKEvent;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatRecordEvent;
import com.yunche.android.yunchevideosdk.R;
import com.yunche.android.yunchevideosdk.YCBaseActivity;
import com.yunche.android.yunchevideosdk.cview.loadingdialog.CircleProgressAlertView;
import com.yunche.android.yunchevideosdk.entity.FileProgress;
import com.yunche.android.yunchevideosdk.entity.LoanerListItem;
import com.yunche.android.yunchevideosdk.http.ApiService;
import com.yunche.android.yunchevideosdk.http.ResultBody;
import com.yunche.android.yunchevideosdk.http.base.BaseObserver;
import com.yunche.android.yunchevideosdk.http.base.RxSchedulers;
import com.yunche.android.yunchevideosdk.mvp.BasePresenter;
import com.yunche.android.yunchevideosdk.oss.OssInterface;
import com.yunche.android.yunchevideosdk.oss.OssService;
import com.yunche.android.yunchevideosdk.oss.OssUtils;
import com.yunche.android.yunchevideosdk.param.VideoParam;
import com.yunche.android.yunchevideosdk.utils.AudioUtil;
import com.yunche.android.yunchevideosdk.utils.DateUtils;
import com.yunche.android.yunchevideosdk.utils.DeviceUtils;
import com.yunche.android.yunchevideosdk.utils.FileUtil;
import com.yunche.android.yunchevideosdk.utils.HanziNameToPinyin;
import com.yunche.android.yunchevideosdk.utils.PreferenceUtils;
import com.yunche.android.yunchevideosdk.utils.SingleMediaScanner;
import com.yunche.android.yunchevideosdk.utils.event.FunctionManager;
import com.yunche.android.yunchevideosdk.utils.statusbar.StatusBarUtils;
import com.yunche.android.yunchevideosdk.videoface.video.utils.AnychatStreamBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MachineVideoActivity extends YCBaseActivity implements AnyChatBaseEvent,
        AnyChatRecordEvent, Player.RadioProgress, AnyChatCoreSDKEvent {

    // handle send msg
    private final int MSG_VIDEOGESPREK = 1;        // 视频对话时间刷新消息
    private final int MSG_LOCALRECORD = 2;        // 本地录制时间刷新消息
    private final int MSG_PREVIEWVIDEO = 3;    // 视频录制预览倒计时时间刷新消息

    private final int UPDATEVIDEOBITDELAYMILLIS = 200; //监听音频视频的码率的间隔刷新时间（毫秒）

    int mUserID;
    private final String mStrBasePath = "/aa_yunche_video_face";
    private TextView mPreviewFilePath;            // 用于显示拍照的相片和本地录制视频的存储路径的显示
    private int mVideogesprekSec = 0;            // 音视频对话的时间
    private int mdwFlags = 0;                    // 本地视频录制参数标致
    private int mCurRecordUserID = -1;            // 当前录制角色的id
    private int mLocalRecordTimeSec = 0;        // 本地录制的时间
    private int mPreviewVideoSec = 0;            // 视频录制预览声音时间
    private int mLocalRecordState;                // 1表示本地录制打开着，0表示本地录制关闭着
    private String mPreviewVideoPathStr = "";   // 录制视频的保存路径
    private ImageView mPreviewVideoIV;            // 用于显示本地录制视频预览view
    private TextView mLocalRecordTimeTV;        // 显示本地视频录制时间
    private Timer mLcoalRecordTimer;
    private Timer mPreviewVideoTimer = null;

    private SurfaceView mMyView;
    private ImageView mImgSwitchVideo; // 切换设备前后摄像头
    private ImageView mEndCallBtn;
    private ImageView back;
    private Dialog mDialog;
    private TextView mVideogesprekTimeTV; // 视频对话时间
    private Timer mVideogesprekTimer;

    private TimerTask mTimerTask;
    private Handler mHandler;
    private boolean recoding = false;//录制中

    private TextView tvAgain, tvReview, tvSubmit;
    private LinearLayout menuContainer, startLayout;
    private ImageButton videoClose;

    public AnyChatCoreSDK anyChatSDK;
    private LoanerListItem loanerListItem;

    private String localPath;//子音频所在的文件夹
    //所有分隔的音频
    private List<AudioUrlBean> audioUrlBeen;
    //播放子音频
    private AudioUrlBean targetAudio;
    private String guid;


    private int wD;

    private boolean test;//是否为测试录制


    private NetworkChangeReceiver networkChangeReceiver;


    @Override
    public void progress(int curent, int max) {

    }

    //音频间隔
    private int time;//s
    private Handler weakHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 110:
                    if (time >= targetAudio.getSec() && recoding) {
                        weakHandle.removeMessages(110);
                        if (targetAudio.getSec() != 0) {//最后一个音频间隔时间为0
                            playAudio(targetAudio.getIndex() + 1);
                        }else {
                            if (test){
                                mTimerTask.cancel();
                                mHandler.removeCallbacksAndMessages(null);
                                handler.removeCallbacksAndMessages(null);
                                time = 0;
                                weakHandle.removeMessages(110);
                                anyChatSDK.StreamPlayControl(guid, 3, 0, 0, "");
                                anyChatSDK.StreamPlayDestroy(guid, 0);
                                anyChatSDK.StreamRecordCtrlEx(-1, 0, mdwFlags, 0, "关闭视频录制");
                                startLayout.setVisibility(View.GONE);
                                menuContainer.setVisibility(View.VISIBLE);
                                if (test){
                                    tvSubmit.setVisibility(View.GONE);
                                }
                            }
                        }
                        time = 0;
                    } else {
                        time++;
                        Log.i("----", "time: " + time + "   calling: " + calling);
                        if (calling) {
                            time = 0;
                            weakHandle.removeMessages(110);
                            return;
                        }
                        weakHandle.sendEmptyMessageDelayed(110, 1000);
                    }
                    break;
            }
        }
    };

    @Override
    public void OnAnyChatCoreSDKEvent(int dwEventType, String szJsonStr) {
        Log.i("------EventType---- : ", dwEventType + " " + szJsonStr);
        AnychatStreamBack streamBack = JSON.parseObject(szJsonStr, AnychatStreamBack.class);
        if (streamBack.getPlayevent() == 4) {//结束
            anyChatSDK.StreamPlayControl(guid, 3, 0, 0, "");
            anyChatSDK.StreamPlayDestroy(guid, 0);
            weakHandle.sendEmptyMessage(110);
        } else if (streamBack.getPlayevent() == 3) {//播放
            if (targetAudio.getIndex() == 0) {
                startTT = 0;
                handler.sendEmptyMessageDelayed(10010, 1000);
            }
        }
    }

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
                    Toast.makeText(mContext,"网络异常",Toast.LENGTH_SHORT).show();
                    destroyCurActivity();
                }
            }
        }
    }

    public void initAudioPlayer() {
        guid = AnyChatCoreSDK.GetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_NEWGUID);
        Log.i("------guid : ", guid);
    }

    public void playAudio(int index) {
        targetAudio = audioUrlBeen.get(index);
        //String hh = "https://yunche-base.oss-cn-hangzhou.aliyuncs.com/test/1.mp3";
        anyChatSDK.StreamPlayInit(guid, targetAudio.getUrl(), 1, "");
        //anyChatSDK.StreamPlayInit(guid,hh,1,"");
        anyChatSDK.StreamPlayControl(guid, 1, 0, 0, "");
        anyChatSDK.SetCoreSDKEvent(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.localvideorecord_frame;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        StatusBarUtils.with(this).invasionStatusBar();
        wD = DeviceUtils.getDisplay(mContext).widthPixels;
        mUserID = -1;
        //videoUrl = getIntent().getStringExtra("videoUrl");

        test = getIntent().getBooleanExtra("test",false);


        //String path = Environment.getExternalStorageDirectory().getPath() + "/luqiao/";
        //videoUrl = getIntent().getStringExtra("videoUrl");

//        videoUrl = path+"TaiZhouLuQiao_8_44444444.mp3";
//
//        Logger.d("音频地址", videoUrl);
//        bank = videoUrl.contains("HangZhou_ChengZhan") ? 1 : 2;
//        switch (bank) {
//            case 1:
//                targetUpimes = chengzan_up;
//                targetDownTimes = chengzan_down;
//                break;
//            case 2:
//                targetUpimes = taizhou_up;
//                targetDownTimes = taizhou_down;
//                break;
//        }
        loanerListItem = getIntent().getBundleExtra("item").getParcelable("item");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

        int nowVol = AudioUtil.getInstance(mContext).getMediaVolume();

        int videoMaxVol = AudioUtil.getInstance(mContext).getMediaMaxVolume();

        if (nowVol<videoMaxVol*0.8){
            AudioUtil.getInstance(mContext).setMediaVolume((int) (videoMaxVol*0.8));
        }

        //AudioUtil.getInstance(mContext).setSpeakerphoneOn(true);

        //Toasty.info(mContext,"closeMeVoice : "+closeMeVoice+"  "+cV).show();

        //存放子音频的文件夹路径
        localPath = Environment.getExternalStorageDirectory().getPath() + "/" + getIntent().getStringExtra("localPath") + "/";
        String houzhui = getIntent().getStringExtra("houzhui");

        String[] mp3Info = getIntent().getStringExtra("audioInfoName").split("_");

        int mp3Num = Integer.parseInt(mp3Info[1]);

        String[] desc = mp3Info[2].split("-");

        audioUrlBeen = new ArrayList<>(mp3Num);
        for (int i = 1; i <= mp3Num; i++) {
            AudioUrlBean audio = new AudioUrlBean();
            audio.setIndex(i - 1);
            audio.setUrl(localPath + i + houzhui);
            audio.setSec(Integer.parseInt(desc[i-1]));//最后一个间隔时间为0
            audioUrlBeen.add(audio);
        }
        targetAudio = audioUrlBeen.get(0);


        //根据videoUrl获取子音频
//        String[] mp3Info = videoUrl.substring(videoUrl.lastIndexOf("/")+1,videoUrl.lastIndexOf(".")).split("_");
//
//        videoChildPath = videoUrl.substring(0,videoUrl.lastIndexOf("/")+1)+mp3Info[0]+"/";
//
//        int mp3Num = Integer.parseInt(mp3Info[1]);
//        audioUrlBeen = new ArrayList<>(mp3Num);
//        for (int i = 0; i < mp3Num; i++) {
//            AudioUrlBean audio = new AudioUrlBean();
//            audio.setIndex(i);
//            audio.setUrl(videoChildPath+(i+1)+".mp3");
//            audio.setSec(i>=mp3Num-1?0:mp3Info[2].charAt(i)-'0');//最后一个间隔时间为0
//            audioUrlBeen.add(audio);
//
//            Log.i("---",videoChildPath);
//
//        }

        InitSDK();
        InitLayout();

        initAudioPlayer();

        //监听电话来电
        TelephonyManager telephony = (TelephonyManager) getSystemService(
                Context.TELEPHONY_SERVICE);
        telephony.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);

    }

    private String videoPath;//文件存放的文件夹

    private void InitSDK() {

        if (anyChatSDK == null) {
            anyChatSDK = AnyChatCoreSDK.getInstance(this);
        }
        anyChatSDK.SetBaseEvent(this);
        anyChatSDK.SetRecordSnapShotEvent(this);
        anyChatSDK.mSensorHelper.InitSensor(getApplicationContext());
        AnyChatCoreSDK.mCameraHelper.SetContext(getApplicationContext());

        String nameTempStr = loanerListItem.getCustomer();
        nameTempStr.replaceAll("·", "");
        if (nameTempStr.length() > 4) {
            nameTempStr = nameTempStr.substring(0, 4);
        }
        String namePinyinStr = HanziNameToPinyin.getPinYin(nameTempStr).toLowerCase();

        videoPath = Environment.getExternalStorageDirectory() + mStrBasePath
                + "/" + namePinyinStr + "/";

        // 设置录像存储路径
        anyChatSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_RECORD_TMPDIR, videoPath);
        Log.i("====", videoPath);

        // 设置录像格式（0表示mp4）
        AnyChatCoreSDK
                .SetSDKOptionInt(AnyChatDefine.BRAC_SO_RECORD_FILETYPE, 0);

    }

    private void InitLayout() {
        tvAgain = (TextView) findViewById(R.id.tvAgain);
        tvSubmit = (TextView) findViewById(R.id.tvSubmit);
        tvReview = (TextView) findViewById(R.id.tvReview);
        menuContainer = (LinearLayout) findViewById(R.id.menuContainer);
        startLayout = (LinearLayout) findViewById(R.id.startLayout);

        mMyView = (SurfaceView) findViewById(R.id.localVecordSurface_local);
        mImgSwitchVideo = (ImageView) findViewById(R.id.video_switch);
        mEndCallBtn = (ImageView) findViewById(R.id.localRecordEndCall);
        videoClose = (ImageButton) findViewById(R.id.video_close);
        mVideogesprekTimeTV = (TextView) findViewById(R.id.localRecordVideogesprekTime);
        mLocalRecordTimeTV = (TextView) findViewById(R.id.localVideoRecordTime);
        mPreviewVideoIV = (ImageView) findViewById(R.id.previewLocalRecordVideo);
        mPreviewFilePath = (TextView) findViewById(R.id.previewLocalRecordFilePath);
        TextView setting = (TextView) findViewById(R.id.setting);
        back = (ImageView) findViewById(R.id.back);

        mLocalRecordState = 0;

        mImgSwitchVideo.setOnClickListener(onClickListener);
        mEndCallBtn.setOnClickListener(onClickListener);
        mPreviewVideoIV.setOnClickListener(onClickListener);
        tvAgain.setOnClickListener(onClickListener);
        tvSubmit.setOnClickListener(onClickListener);
        tvReview.setOnClickListener(onClickListener);
        videoClose.setOnClickListener(onClickListener);
        setting.setOnClickListener(onClickListener);
        back.setOnClickListener(onClickListener);

        //如果是测试录制
        if (test){
            setting.setVisibility(View.GONE);
        }

        mMyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        focusOnTouch(event);
                    } catch (Exception e) {

                    }
                }
                return true;
            }
        });

        int w = AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL);
        int h = AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL);

        //Toasty.info(mContext,w+"    "+h+"   dw:"+w).show();

        int viewH = wD * w / h;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mMyView.getLayoutParams();
        layoutParams.height = viewH;
        mMyView.setLayoutParams(layoutParams);


        // 如果是采用Java视频采集，则需要设置Surface的CallBack
        if (AnyChatCoreSDK
                .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            mMyView.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
        }

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
        anyChatSDK.UserCameraControl(-1, 1);// -1表示对本地视频进行控制，打开本地视频
        anyChatSDK.UserSpeakControl(-1, 1);// -1表示对本地音频进行控制，打开本地音频
    }

    private void updateTime() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    // 刷新视频对话时间
                    case MSG_VIDEOGESPREK:
                        mVideogesprekTimeTV.setText(DateUtils.getTimeShowString(mVideogesprekSec++));
                        break;
                    // 刷新本来录制时间
                    case MSG_LOCALRECORD:
                        mLocalRecordTimeTV.setText(DateUtils.getTimeShowString(mLocalRecordTimeSec++));
                        break;
                    // 视频预览10秒后隐藏
                    case MSG_PREVIEWVIDEO:
                        if (mPreviewVideoSec <= 0) {
                            mPreviewVideoTimer.cancel();
                            mPreviewVideoTimer = null;
                            mPreviewVideoIV.setVisibility(View.GONE);
                            mPreviewFilePath.setVisibility(View.GONE);
                        } else {
                            mPreviewVideoSec -= 1;
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
    private void initVideogesprekTimer() {
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

    // 初始化本地录制定时器
    private void initLocalRecordTimer() {
        if (mLcoalRecordTimer == null) {
            mLcoalRecordTimer = new Timer();
        }

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_LOCALRECORD);
            }
        };

        mLcoalRecordTimer.schedule(mTimerTask, 10, 1000);
        mLocalRecordTimeTV.setVisibility(View.VISIBLE);
    }

    // 初始化录制视频预览定时器
    private void initPreviewVideoTimer() {
        if (mPreviewVideoTimer == null) {
            mPreviewVideoTimer = new Timer();
        }

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_PREVIEWVIDEO);
            }
        };

        mPreviewVideoTimer.schedule(mTimerTask, 10, 1000);
        mPreviewVideoIV.setVisibility(View.VISIBLE);
        mPreviewFilePath.setVisibility(View.VISIBLE);
    }

    // 点击事件
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            int i1 = view.getId();
            if (i1 == R.id.back) {
                showEndVideoDialog(4);

            } else if (i1 == R.id.video_close) {
                showEndVideoDialog(4);

            } else if (i1 == R.id.tvSubmit) {
                if (TextUtils.isEmpty(mPreviewVideoPathStr)) {
                    Toast.makeText(mContext, "视频文件为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                showEndVideoDialog(2);

            } else if (i1 == R.id.tvReview) {
                if (TextUtils.isEmpty(mPreviewVideoPathStr)) {
                    Toast.makeText(mContext, "视频文件为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent = VideoUtils.getVideoReviewIntent(mContext, mPreviewVideoPathStr, "video/*");
                startActivity(intent);

            } else if (i1 == R.id.tvAgain) {
                mEndCallBtn.setImageResource(R.mipmap.video_off);
                showEndVideoDialog(3);

                // 摄像头切换
            } else if (i1 == R.id.video_switch) {// 如果是采用Java视频采集，则在Java层进行摄像头切换
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
                        anyChatSDK.SelectVideoCapture(strVideoCaptures[i]);
                        anyChatSDK.UserCameraControl(-1, 1);
                        break;
                    }
                }

                // End Call
            } else if (i1 == R.id.localRecordEndCall) {
                mEndCallBtn.setImageResource(recoding ? R.mipmap.video_start : R.mipmap.video_off);
                if (recoding) {
                    showEndVideoDialog(1);
                } else {
                    start();
                }

                // 视频录制预览事件
            } else if (i1 == R.id.previewLocalRecordVideo) {//				intent = BaseMethod.getIntent(mPreviewVideoPathStr, "video/*");
//				startActivity(intent);

            } else if (i1 == R.id.setting) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MachineVideoActivity.this);
                builder.setTitle("提示").setMessage("前往设置，将退出当前面签。")
                        .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                destroyCurActivity();
                                FunctionManager.getInstance().invokeFunc("settingface", "machine");
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();

            } else {
            }
        }
    };

    long startTT = 0;//秒
    private boolean canPlay = false;
    private boolean puseTemp = false;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 10010) {
                startTT++;
                if (startTT >= 3600) {//如果面签超过一个小时，结束录制
                    Log.i("====", "关闭视频录制");
                    weakHandle.removeMessages(110);
                    mTimerTask.cancel();
                    mHandler.removeCallbacksAndMessages(null);
                    anyChatSDK.StreamRecordCtrlEx(-1, 0, mdwFlags, 0, "关闭视频录制");
                    startLayout.setVisibility(View.GONE);
                    menuContainer.setVisibility(View.VISIBLE);
                } else {
//                    if (!player.finish && !closeMeVoice) {
//                        if (startTT == 1) {
//                            player.toPosition(0);
//                        }
//                        boolean changed = false;
//                        int playerPosition = player.getPlayPosition() / 1000;
//                        Log.i("============sdsd", player.getPlayPosition() + "   " + playerPosition);
//                        //是否为调小
//                        for (int targetTime : targetDownTimes) {
//                            if (playerPosition == targetTime) {
//                                AudioUtil.getInstance(mContext).setMediaVolume(cVDown);
//                                changed = true;
//                                break;
//                            }
//                        }
//                        if (!changed) {//如果已经调小了，则不进入无意义的循环
//                            //是否为调大
//                            for (int targetTime : targetUpimes) {
//                                if (playerPosition == targetTime) {
//                                    AudioUtil.getInstance(mContext).setMediaVolume(cV);
//                                    break;
//                                }
//                            }
//                        }
//                    }
                    handler.sendEmptyMessageDelayed(10010, 1000);
                }
            }
            return false;
        }
    });

    //开始录制
    private void start() {
        Boolean bCanRecord = false;
        if (mPreviewVideoIV.getVisibility() == View.VISIBLE) {
            mPreviewVideoSec = 0;
            mPreviewVideoIV.setVisibility(View.GONE);
            mPreviewFilePath.setVisibility(View.GONE);
        }
        mdwFlags = AnyChatDefine.ANYCHAT_RECORD_FLAGS_AUDIO
                + AnyChatDefine.ANYCHAT_RECORD_FLAGS_VIDEO;
        mCurRecordUserID = -1;
        bCanRecord = true;

        if (bCanRecord) {
            anyChatSDK.StreamRecordCtrlEx(-1, 1, mdwFlags, 0, "开始录制");
            mLocalRecordState = 1;
            mLocalRecordTimeSec = 0;
        }

        mVideogesprekSec = 0;//重置录制时间
        updateTime();
        recoding = true;
        videoClose.setVisibility(View.VISIBLE);
        playAudio(0);
//        player = new Player(videoUrl, MachineVideoActivity.this);
//        player.play(new Player.VideoPlayerInterface() {
//            @Override
//            public void start() {
//                Log.i("----", "开始了");
//                startTT = 0;
//                handler.sendEmptyMessageDelayed(10010, 500);
//            }
//        });

    }

    public void back(View v) {
        showEndVideoDialog(recoding ? 1 : 4);
    }

    // 关闭视频确认框 type:1:结束录制,2:提交,3:重新录制,4:退出
    private void showEndVideoDialog(final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MachineVideoActivity.this);
        String str = type == 1 ? "确定退出视频面签吗?" : type == 2 ? "确定提交面签视频吗?" : type == 3 ? "您确定要重新录制视频吗?" : "您确定要结束视频通话吗？";
        builder.setMessage(str)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (type == 1) {
                            Log.i("====", "关闭视频录制");
                            mTimerTask.cancel();
                            mHandler.removeCallbacksAndMessages(null);
                            handler.removeCallbacksAndMessages(null);
//                            if (player != null) {
//                                player.stop();
//                                try {
//                                    player.release();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
                            time = 0;
                            weakHandle.removeMessages(110);
                            anyChatSDK.StreamPlayControl(guid, 3, 0, 0, "");
                            anyChatSDK.StreamPlayDestroy(guid, 0);
                            anyChatSDK.StreamRecordCtrlEx(-1, 0, mdwFlags, 0, "关闭视频录制");
                            startLayout.setVisibility(View.GONE);
                            menuContainer.setVisibility(View.VISIBLE);
                            if (test){
                                tvSubmit.setVisibility(View.GONE);
                            }
                        } else if (type == 2) {
                            ArrayList<String> files = new ArrayList<>();
                            files.add(mPreviewVideoPathStr);
//                            String name = "";
//                            if (BaseApp.getMember().getDepartment()!=null && BaseApp.getMember().getDepartment().size()!=0){
//                                name = BaseApp.getMember().getDepartment().get(0).getName();
//                            }
                            uploadFiles(loanerListItem.getPartner() + "-" + loanerListItem.getCustomer(), loanerListItem.getId(), files, false);
                        } else if (type == 3) {
                            startLayout.setVisibility(View.VISIBLE);
                            menuContainer.setVisibility(View.GONE);
                            start();
                        } else {//退出
                            destroyCurActivity();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

    private void refreshAV() {
        anyChatSDK.UserCameraControl(-1, 1);
        anyChatSDK.UserSpeakControl(-1, 1);

        mLocalRecordState = 0;
        mLocalRecordTimeTV.setVisibility(View.GONE);

        if (mLcoalRecordTimer != null) {
            mLcoalRecordTimer.cancel();
            mLcoalRecordTimer = null;
        }
//        if (player != null) {
//            player.stop();
//            try {
//                player.release();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        anyChatSDK.StreamRecordCtrlEx(-1, 0, mdwFlags, 0, "关闭视频录制");
    }

    private void destroyCurActivity() {
//        if (player != null) {
//            player.stop();
//            try {
//                player.release();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        anyChatSDK.StreamPlayControl(guid, 3, 0, 0, "");
        anyChatSDK.StreamPlayDestroy(guid, 0);
        FunctionManager.getInstance().invokeFunc("finishVideo", "finishVideo");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(networkChangeReceiver);

        anyChatSDK.UserCameraControl(mUserID, 0);
        anyChatSDK.UserSpeakControl(mUserID, 0);
        anyChatSDK.removeEvent(this);

        mLocalRecordState = 0;
        mLocalRecordTimeTV.setVisibility(View.GONE);
        anyChatSDK.StreamRecordCtrlEx(-1, 0, mdwFlags, 0, "关闭视频录制");

        if (mLcoalRecordTimer != null) {
            mLcoalRecordTimer.cancel();
            mLcoalRecordTimer = null;
        }

        anyChatSDK.mSensorHelper.DestroySensor();

        //注销电话监听
        ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE))
                .listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);

    }

    public void adjustLocalVideo(boolean bLandScape) {
        float width;
        float height = 0;
        DisplayMetrics dMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
        width = (float) dMetrics.widthPixels / 4;
        LinearLayout layoutLocal = (LinearLayout) this
                .findViewById(R.id.localVecordFrame_local_area);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layoutLocal
                .getLayoutParams();
        if (bLandScape) {

            if (AnyChatCoreSDK
                    .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL) != 0) {
                height = width
                        * AnyChatCoreSDK
                        .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL)
                        / AnyChatCoreSDK
                        .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL)
                        + 5;
            } else {
                height = (float) 3 / 4 * width + 5;
            }
        } else {

            if (AnyChatCoreSDK
                    .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL) != 0) {
                height = width
                        * AnyChatCoreSDK
                        .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL)
                        / AnyChatCoreSDK
                        .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL)
                        + 5;
            } else {
                height = (float) 4 / 3 * width + 5;
            }
        }
        layoutParams.width = (int) width;
        layoutParams.height = (int) height;
        layoutLocal.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showEndVideoDialog(recoding ? 1 : 4);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        targetAudio = audioUrlBeen.get(0);
    }

    @Override
    public void OnAnyChatRecordEvent(int dwUserId, int dwErrorCode, String lpFileName,
                                     int dwElapse, int dwFlags, int dwParam, String lpUserStr) {
        Log.d("AnyChatx", "录像文件文件路径：" + lpFileName);
        recoding = false;

        String nameTempStr = loanerListItem.getCustomer();
        nameTempStr.replaceAll("·", "");
        if (nameTempStr.length() > 4) {
            nameTempStr = nameTempStr.substring(0, 4);
        }
        String namePinyinStr = HanziNameToPinyin.getPinYin(nameTempStr).toLowerCase();
        File filePath = new File(videoPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String dateStr = DateUtils.formatNow("yyyyMMddHHmmss");
        String videoFinal = videoPath + namePinyinStr.replaceAll("_", "") + "_" + dateStr + ".mp4";
        FileUtil.renameFile(lpFileName, videoFinal);

        mPreviewVideoPathStr = videoFinal;

        File file = new File(videoFinal);
        fileSize = file.length() / 1024 / 1024;
        new SingleMediaScanner(mContext, file);

        //隐藏录制按钮，显示菜单
        startLayout.setVisibility(View.GONE);
        menuContainer.setVisibility(View.VISIBLE);
        //录制完成后预览
        intent = VideoUtils.getVideoReviewIntent(mContext, videoFinal, "video/*");
        startActivity(intent);
    }

    @Override
    public void OnAnyChatSnapShotEvent(int dwUserId, int dwErrorCode, String lpFileName,
                                       int dwFlags, int dwParam, String lpUserStr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {

    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
        // 网络连接断开之后，上层需要主动关闭已经打开的音视频设备

        anyChatSDK.UserCameraControl(-1, 0);
        anyChatSDK.UserSpeakControl(-1, 0);

//		Intent mIntent = new Intent("NetworkDiscon");
//		// 发送广播
//		sendBroadcast(mIntent);

        // 销毁当前界面
        destroyCurActivity();
    }

    //上传面签视频
    private boolean uploading = false;

    public void uploadFiles(String name, String orderId, final ArrayList<String> files, boolean isImg) {
        final CircleProgressAlertView progressAlertView = new CircleProgressAlertView(mContext);
        progressAlertView.setCanceledOnTouchOutside(false);
        progressAlertView.show();
        final OssService ossService = OssUtils.initOSS(mContext, ApiService.OSS_VIDEO_FACE_BUCKET);
        ossService.isImgUpload(isImg);
        uploading = true;
        ossService.asyncPutVideoFaceFiles(files, name, orderId, new OssInterface() {
            @Override
            public void ossNetSuccess(int position, String keyName) {
                Log.i("===", "上传成功" + keyName);
                uploading = false;
                progressAlertView.setFinishProgressColor();
                progressAlertView.dismiss();
                submitInfo(keyName);
            }

            @Override
            public void ossProgress(int position, FileProgress fileProgress) {
                progressAlertView.setProgress(fileProgress.getProgress(), 0);
            }

            @Override
            public void ossNetError(String info) {
                dissMissDialog();
                progressAlertView.dismiss();
                uploading = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(MachineVideoActivity.this);
                builder.setCancelable(false);
                builder.setMessage("网络异常,重新上传")
                        .setPositiveButton("重新上传", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                destroyCurActivity();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        destroyCurActivity();
                    }
                }).create().show();
            }
        });
        progressAlertView.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (uploading) {
                    Toast.makeText(mContext,"已取消上传",Toast.LENGTH_SHORT).show();
                    ossService.cancel();
                }
            }
        });
    }

    //提交面签结果
    private double fileSize = 0;

    public void submitInfo(String keyName) {
        showLoadingDialog(0);
        VideoParam videoParam = new VideoParam();
        videoParam.setOrderId(loanerListItem.getId());
        videoParam.setPath(ApiService.OSS_P_URL + keyName);
        videoParam.setAction(null);
        videoParam.setBankId(loanerListItem.getBankId());
        videoParam.setAddress(PreferenceUtils.getString(getApplicationContext(), "addr"));
        videoParam.setAuditorId(null);
        videoParam.setAuditorName(null);
        videoParam.setCarDetailId(loanerListItem.getCarDetailId());
        videoParam.setCarName(loanerListItem.getCarName());
        videoParam.setCustomerId(Long.parseLong(loanerListItem.getCustomerId()));
        videoParam.setCustomerName(loanerListItem.getCustomer());
        String local = PreferenceUtils.getString(getApplicationContext(), "latitude") + "," + PreferenceUtils.getString(getApplicationContext(), "longitude");
        videoParam.setLatlon(local);
        videoParam.setType(2);
        videoParam.setVideoSize(fileSize);
        videoParam.setCarPrice(loanerListItem.getCarPrice());
        videoParam.setCustomerIdCard(loanerListItem.getIdCard());
        ApiService.myApi().saveVideoFace(ResultBody.getObjectJson(videoParam))
                .compose(RxSchedulers.compose(this.<ResultBody<Long>>bindToLifecycle()))
                .subscribe(new BaseObserver<Long>(mContext) {
                    @Override
                    protected void onHandleSuccess(Long list) {
                        dissMissDialog();
                        Toast.makeText(mContext,"面签视频已提交",Toast.LENGTH_SHORT).show();
                        destroyCurActivity();
                    }

                    @Override
                    protected void onHandleError(String code, String msg) {
                        super.onHandleError(code, msg);
                        destroyCurActivity();
                    }
                });
    }

    /**
     * 电话状态监听.
     *
     * @author stephen
     */
    private boolean calling;//来电中
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            //Log.i(TAG, "[phone  Listener]:" + incomingNumber + "    " + state);
            if (!recoding) {//如果没有开始录制，不需要处理
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://来电话
                    //Log.i(TAG, "[Listener]等待接电话:" + incomingNumber);
                    calling = true;
                    //停止播放音频，停止录制,停止计时
                    anyChatSDK.StreamPlayControl(guid, 3, 0, 0, "");//暂停播放
                    anyChatSDK.StreamPlayDestroy(guid, 0);
                    //anyChatSDK.StreamRecordCtrlEx(-1, 0, mdwFlags, 0, "关闭视频录制");
                    weakHandle.removeMessages(110);
                    mTimerTask.cancel();
                    break;
                case TelephonyManager.CALL_STATE_IDLE://挂断
                    //Log.i(TAG, "[Listener]电话挂断:" + incomingNumber);
                    calling = false;
                    //重新开始,从当前音频重新开始播放
                    AlertDialog.Builder builder = new AlertDialog.Builder(MachineVideoActivity.this);
                    builder.setMessage("继续录制？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    initVideogesprekTimer();
                                    anyChatSDK.StreamRecordCtrlEx(-1, 1, mdwFlags, 0, "开始视频录制");
                                    playAudio(targetAudio.getIndex());
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    destroyCurActivity();
                                }
                    }).create().show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接听
                    //Log.i(TAG, "[Listener]通话中:" + incomingNumber);
                    calling = true;
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        dissMissDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    //手动对焦
    private void focusOnTouch(MotionEvent event) {
        Camera mCamera = anyChatSDK.mCameraHelper.getC();
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0) {
                Rect rect = calculateFocusArea(event.getX(), event.getY());
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);
                mCamera.setParameters(parameters);
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            } else {
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus", "success!");
            } else {
                // do something...
                Log.i("tap_to_focus", "fail!");
            }
        }
    };



    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mMyView.getWidth()) * 2000 - 1000).intValue(), 500);
        int top = clamp(Float.valueOf((y / mMyView.getHeight()) * 2000 - 1000).intValue(), 500);
        return new Rect(left, top, left + 500, top + 500);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize / 2;
            } else {
                result = -1000 + focusAreaSize / 2;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }
}
