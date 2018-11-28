package com.yunche.android.yunchevideosdk.videoface.video.utils;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.yunche.android.yunchevideosdk.R;
import com.yunche.android.yunchevideosdk.YCBaseActivity;
import com.yunche.android.yunchevideosdk.entity.LoanerListItem;
import com.yunche.android.yunchevideosdk.mvp.BasePresenter;
import com.yunche.android.yunchevideosdk.utils.statusbar.StatusBarUtils;
import com.yunche.android.yunchevideosdk.videoface.video.CameraSizeComparator;
import com.yunche.android.yunchevideosdk.videoface.video.VideotTestActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class VideoConfig extends YCBaseActivity {

    TextView title;

    TextView test;
    RadioButton openRadio;
    RadioButton closeRadio;

    private TextView mSaveBtn;
    private ConfigEntity mConfigEntity;
    // 服务器配置
    private RadioButton mServerModelConfigBtn;
    // 自定义配置
    private RadioButton mCustomModelConfigBtn;

    private TextView mResolutionTV;
//    private CheckBox mEnableP2PBox;
//    private CheckBox mVideoOverlayBox;
//    private CheckBox mUseARMv6Box;
//    private CheckBox mUseAECBox;
//    private CheckBox mUseHWCodecBox;
//    private CheckBox mVideoRotateBox;
//    private CheckBox mFixColorDeviation;
//    private CheckBox mVideoShowGPURender;
//    private CheckBox mVideoAutoRotation;
    private Spinner mVideoSizeSpinner;
    private Spinner mVideoBitrateSpinner;
    private Spinner mVideoFPSSpinner;
    private Spinner mVideoQualitySpinner;
    private Spinner mVideoPresetSpinner;
    private Spinner videoPOGSpinner;

    private String[] mArrVideoSizeStr = {"352 x 288", "640 x 480", "720 x 480", "1280 x 720"};
    private int[] mArrVideoWidthValue = {352, 640,720,1280};
    private int[] mArrVideoHeightValue = {288, 480,480,720};

    private final String[] mArrVideoBitrateStr = {"100kbps", "150kbps", "200kbps", "300kbps", "400kbps", "500kbps","600kbps","800kbps"};
    private final int[] mArrVideoBitrateValue = {100 * 1000, 150 * 1000, 200 * 1000, 300 * 1000, 400 * 1000, 500 * 1000, 600 * 1000, 800 * 1000};

    private final String[] mArrVideofpsStr = {"10FPS", "15FPS", "20FPS", "25FPS"};
    private final int[] mArrVideofpsValue = {10, 15, 20, 25};

    private final String[] mArrVideogopStr = {"15", "20", "25", "30", "40", "60"};
    private final int[] mArrVideogopValue = {15, 20, 25, 30, 40, 60};

    private final String[] mArrVideoQualityStr = {"普通视频质量", "中等视频质量（默认）", "较好视频质量"};
    private final int[] mArrVideoQualityValue = {2, 3, 4};

    private final String[] mArrVideoPresetStr = {"最高效率，较低质量", "较高效率，较低质量", "性能均衡（默认）", "较高质量，较低效率", "最高质量，较低效率"};
    private final int[] mArrVideoPresetValue = {1, 2, 3, 4, 5};

    private boolean isMachineVideo = false;//是否为机器面签配置

    private LoanerListItem loanerListItem;

    @Override
    public int getLayoutId() {
        return R.layout.videoconfig;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        StatusBarUtils.with(this).invasionStatusBar();
        mConfigEntity = ConfigService.LoadConfig(this);

        loanerListItem = getIntent().getBundleExtra("item").getParcelable("item");

        isMachineVideo = getIntent().getBooleanExtra("isMachineVideo",false);

        //getSize();
        //初始化界面
        InitialLayout();
    }

    private void InitialLayout() {
//
//        // 启用P2P网络连接
//        mEnableP2PBox = (CheckBox) this.findViewById(R.id.enableP2PBox);
//        mEnableP2PBox.setTextColor(Color.BLACK);
//        mEnableP2PBox.setChecked(mConfigEntity.mEnableP2P != 0);
//
//        // Overlay视频模式
//        mVideoOverlayBox = (CheckBox) this.findViewById(R.id.videoOverlayBox);
//        mVideoOverlayBox.setTextColor(Color.BLACK);
//        mVideoOverlayBox.setChecked(mConfigEntity.mVideoOverlay != 0);
//
//        // 翻转视频
//        mVideoRotateBox = (CheckBox) this.findViewById(R.id.videoRotateBox);
//        mVideoRotateBox.setTextColor(Color.BLACK);
//        mVideoRotateBox.setChecked(mConfigEntity.mVideoRotateMode != 0);
//
//        // 本地视频采集偏色修正
//        mFixColorDeviation = (CheckBox) this.findViewById(R.id.fixColorDeviation);
//        mFixColorDeviation.setTextColor(Color.BLACK);
//        mFixColorDeviation.setChecked(mConfigEntity.mFixColorDeviation != 0);
//
//        // 启用视频GPU渲染
//        mVideoShowGPURender = (CheckBox) this.findViewById(R.id.videoShowGPURender);
//        mVideoShowGPURender.setTextColor(Color.BLACK);
//        mVideoShowGPURender.setChecked(mConfigEntity.mVideoShowGPURender != 0);
//
//        // 本地视频跟随设备自动旋转
//        mVideoAutoRotation = (CheckBox) this.findViewById(R.id.videoAutoRotation);
//        mVideoAutoRotation.setTextColor(Color.BLACK);
//        mVideoAutoRotation.setChecked(mConfigEntity.mVideoAutoRotation != 0);
//
//        // 强制使用ARMv6指令集（安全模式）
//        mUseARMv6Box = (CheckBox) this.findViewById(R.id.useARMv6Box);
//        mUseARMv6Box.setTextColor(Color.BLACK);
//        mUseARMv6Box.setChecked(mConfigEntity.mUseARMv6Lib != 0);
//
//        // 启用回音消除（AEC）
//        mUseAECBox = (CheckBox) this.findViewById(R.id.useAECBox);
//        mUseAECBox.setTextColor(Color.BLACK);
//        mUseAECBox.setChecked(mConfigEntity.mEnableAEC != 0);
//
//        // 启用平台内置硬件编解码（需重启应用程序）
//        mUseHWCodecBox = (CheckBox) this.findViewById(R.id.useHWCodecBox);
//        mUseHWCodecBox.setTextColor(Color.BLACK);
//        mUseHWCodecBox.setChecked((isMachineVideo?mConfigEntity.mmUseHWCodec:mConfigEntity.mUseHWCodec) != 0);

        // 插入配置模式选择项

        title = (TextView) this.findViewById(R.id.title);
        test = (TextView) this.findViewById(R.id.test);
        openRadio = (RadioButton) this.findViewById(R.id.openRadio);
        closeRadio = (RadioButton) this.findViewById(R.id.closeRadio);

        title.setText(isMachineVideo?"机器面签参数配置":"人工面签参数配置");

        TextView configModelLable = (TextView) this.findViewById(R.id.configModelLable);
        configModelLable.setTextColor(Color.BLACK);
        configModelLable.setText("选择配置模式： ");

        mServerModelConfigBtn = (RadioButton) findViewById(R.id.serverModelConfigBtn);
        mCustomModelConfigBtn = (RadioButton) findViewById(R.id.customModelConfigBtn);
        mServerModelConfigBtn.setTextColor(Color.BLACK);
        mCustomModelConfigBtn.setTextColor(Color.BLACK);
        mServerModelConfigBtn.setOnClickListener(onClickListener);
        mCustomModelConfigBtn.setOnClickListener(onClickListener);

        if (mConfigEntity.mConfigMode == ConfigEntity.VIDEO_MODE_SERVERCONFIG)
            mServerModelConfigBtn.setChecked(true);
        else
            mCustomModelConfigBtn.setChecked(true);

        //选择视频分辨率：
        mResolutionTV = (TextView) this.findViewById(R.id.resolutionTV);
        //mResolutionTV.setTextColor(Color.BLACK);
        videoPOGSpinner = (Spinner) this.findViewById(R.id.videoPOGSpinner);
        // 插入视频分辨率
        mVideoSizeSpinner = (Spinner) this.findViewById(R.id.videoSizeSpinner);
        ArrayAdapter<String> videoSizeAdapter;
        videoSizeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mArrVideoSizeStr);
        videoSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mVideoSizeSpinner.setAdapter(videoSizeAdapter);
        mVideoSizeSpinner.setVisibility(View.VISIBLE);
        int iSelectVideoSize = 0;
        for (int i = 0; i < mArrVideoWidthValue.length; i++) {
            if (mArrVideoWidthValue[i] == (isMachineVideo?mConfigEntity.mmResolutionWidth:mConfigEntity.mResolutionWidth)) {
                iSelectVideoSize = i;
                break;
            }
        }
        mVideoSizeSpinner.setSelection(iSelectVideoSize);

        if (mConfigEntity.closeMeVoice){
            closeRadio.setChecked(true);
        }else {
            openRadio.setChecked(true);
        }

        //插入码率
        mVideoBitrateSpinner = InsertSpinnerInterface(1, mArrVideoBitrateStr, mArrVideoBitrateValue, isMachineVideo?mConfigEntity.mmVideoBitrate:mConfigEntity.mVideoBitrate);
        //插入帧率
        mVideoFPSSpinner = InsertSpinnerInterface(2, mArrVideofpsStr, mArrVideofpsValue,isMachineVideo?mConfigEntity.mmVideoFps: mConfigEntity.mVideoFps);
        //插入视频质量
        mVideoQualitySpinner = InsertSpinnerInterface(3, mArrVideoQualityStr, mArrVideoQualityValue, isMachineVideo?mConfigEntity.mmVideoQuality:mConfigEntity.mVideoQuality);
        // 插入视频预设参数
        mVideoPresetSpinner = InsertSpinnerInterface(4, mArrVideoPresetStr, mArrVideoPresetValue, isMachineVideo?mConfigEntity.mmVideoPreset:mConfigEntity.mVideoPreset);
        //gop
        videoPOGSpinner = InsertSpinnerInterface(5, mArrVideogopStr, mArrVideogopValue, isMachineVideo?mConfigEntity.mgop:mConfigEntity.gop);

        // 根据配置模式，确定是否需要显示自定义的配置项
        CustomControlsShow(mConfigEntity.mConfigMode == 0 ? false : true);

        mSaveBtn = (TextView) this.findViewById(R.id.saveBtn);
        mSaveBtn.setText("保存设置");
        mSaveBtn.setOnClickListener(onClickListener);
        test.setOnClickListener(onClickListener);

        if (!isMachineVideo){
            test.setVisibility(View.GONE);
        }
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mSaveBtn) {
                SaveConfig(false);
            } else if (v == mCustomModelConfigBtn) {
                CustomControlsShow(true);
            } else if (v == mServerModelConfigBtn) {
                CustomControlsShow(false);
            } else if (v == test){

                SaveConfig(true);

            }
        }
    };

    private void CustomControlsShow(boolean bEnable) {
        mVideoSizeSpinner.setEnabled(bEnable);
        mVideoBitrateSpinner.setEnabled(bEnable);
        mVideoFPSSpinner.setEnabled(bEnable);
        mVideoQualitySpinner.setEnabled(bEnable);
        mVideoPresetSpinner.setEnabled(bEnable);
    }

    private void SaveConfig(boolean test) {
        mConfigEntity.mConfigMode = 1;
        if (isMachineVideo){
            mConfigEntity.mmResolutionWidth = mArrVideoWidthValue[mVideoSizeSpinner.getSelectedItemPosition()];
            mConfigEntity.mmResolutionHeight = mArrVideoHeightValue[mVideoSizeSpinner.getSelectedItemPosition()];
            mConfigEntity.mmVideoBitrate = mArrVideoBitrateValue[mVideoBitrateSpinner.getSelectedItemPosition()];
            mConfigEntity.mmVideoFps = mArrVideofpsValue[mVideoFPSSpinner.getSelectedItemPosition()];
            mConfigEntity.mmVideoQuality = mArrVideoQualityValue[mVideoQualitySpinner.getSelectedItemPosition()];
            mConfigEntity.mmVideoPreset = mArrVideoPresetValue[mVideoPresetSpinner.getSelectedItemPosition()];
            mConfigEntity.mgop = mArrVideogopValue[videoPOGSpinner.getSelectedItemPosition()];

//            mConfigEntity.mmVideoOverlay = mVideoOverlayBox.isChecked() ? 1 : 0;
//            mConfigEntity.mmVideoRotateMode = mVideoRotateBox.isChecked() ? 1 : 0;
//            mConfigEntity.mmFixColorDeviation = mFixColorDeviation.isChecked() ? 1 : 0;
//            mConfigEntity.mmVideoShowGPURender = mVideoShowGPURender.isChecked() ? 1 : 0;
//
//            mConfigEntity.mmVideoAutoRotation = mVideoAutoRotation.isChecked() ? 1 : 0;
//
//            mConfigEntity.mmEnableP2P = mEnableP2PBox.isChecked() ? 1 : 0;
//            mConfigEntity.mmUseARMv6Lib = mUseARMv6Box.isChecked() ? 1 : 0;
//            mConfigEntity.mmEnableAEC = mUseAECBox.isChecked() ? 1 : 0;
//            mConfigEntity.mmUseHWCodec = mUseHWCodecBox.isChecked() ? 1 : 0;

            mConfigEntity.closeMeVoice = closeRadio.isChecked();
        }else {
            mConfigEntity.mResolutionWidth = mArrVideoWidthValue[mVideoSizeSpinner.getSelectedItemPosition()];
            mConfigEntity.mResolutionHeight = mArrVideoHeightValue[mVideoSizeSpinner.getSelectedItemPosition()];
            mConfigEntity.mVideoBitrate = mArrVideoBitrateValue[mVideoBitrateSpinner.getSelectedItemPosition()];
            mConfigEntity.mVideoFps = mArrVideofpsValue[mVideoFPSSpinner.getSelectedItemPosition()];
            mConfigEntity.mVideoQuality = mArrVideoQualityValue[mVideoQualitySpinner.getSelectedItemPosition()];
            mConfigEntity.mVideoPreset = mArrVideoPresetValue[mVideoPresetSpinner.getSelectedItemPosition()];
            mConfigEntity.gop = mArrVideogopValue[videoPOGSpinner.getSelectedItemPosition()];

//            mConfigEntity.mVideoOverlay = mVideoOverlayBox.isChecked() ? 1 : 0;
//            mConfigEntity.mVideoRotateMode = mVideoRotateBox.isChecked() ? 1 : 0;
//            mConfigEntity.mFixColorDeviation = mFixColorDeviation.isChecked() ? 1 : 0;
//            mConfigEntity.mVideoShowGPURender = mVideoShowGPURender.isChecked() ? 1 : 0;
//
//            mConfigEntity.mVideoAutoRotation = mVideoAutoRotation.isChecked() ? 1 : 0;
//
//            mConfigEntity.mEnableP2P = mEnableP2PBox.isChecked() ? 1 : 0;
//            mConfigEntity.mUseARMv6Lib = mUseARMv6Box.isChecked() ? 1 : 0;
//            mConfigEntity.mEnableAEC = mUseAECBox.isChecked() ? 1 : 0;
//            mConfigEntity.mUseHWCodec = mUseHWCodecBox.isChecked() ? 1 : 0;

            mConfigEntity.closeMeVoice = closeRadio.isChecked();
        }



        ConfigService.SaveConfig(this, mConfigEntity);

        if (test){
            intent = new Intent(mContext, VideotTestActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("item", loanerListItem);
            intent.putExtra("item", bundle);
            startActivity(intent);
        }else {
            this.setResult(RESULT_OK);
            this.finish();
        }

    }

    private Spinner InsertSpinnerInterface(int spinnerIndex, String[] context, int[] value, int select) {
        Spinner spinner = null;
        if (spinnerIndex == 1) {
            spinner = (Spinner) this.findViewById(R.id.videoBitrateSpinner);
        } else if (spinnerIndex == 2) {
            spinner = (Spinner) this.findViewById(R.id.videoFPSSpinner);
        } else if (spinnerIndex == 3) {
            spinner = (Spinner) this.findViewById(R.id.videoQualitySpinner);
        } else if (spinnerIndex == 4) {
            spinner = (Spinner) this.findViewById(R.id.videoPresetSpinner);
        } else if (spinnerIndex == 5) {
            spinner = (Spinner) this.findViewById(R.id.videoPOGSpinner);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, context);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //spinner.setVisibility(View.VISIBLE);

        int offset = 0;
        for (int i = 0; i < value.length; i++) {
            if (value[i] == select) {
                offset = i;
                break;
            }
        }

        spinner.setSelection(offset);

        return spinner;
    }


    private Camera mCamera = null;// 相机

    public void getSize() {
        List<Camera.Size> sizeList = new ArrayList<>();
        if (Camera.getNumberOfCameras() == 2) {
            try {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mCamera = Camera.open();
        }

        CameraSizeComparator sizeComparator = new CameraSizeComparator();
        Camera.Parameters parameters = null;
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
            if (mCamera != null) mCamera.release();
        }
        List<Camera.Size> vSizeList = parameters.getSupportedPreviewSizes();
        Collections.sort(vSizeList, sizeComparator);
        for (int num = 0; num < vSizeList.size(); num++) {
            Camera.Size size = vSizeList.get(num);
            if (size.width == 352 && size.width % size.height > 0) {
                sizeList.add(size);
            } else if (size.width == 640 && size.width % size.height > 0) {
                sizeList.add(size);
            } else if (size.width == 720 && size.width % size.height > 0) {
                sizeList.add(size);
            }
        }
        if (mCamera != null) mCamera.release();
        int s = sizeList.size();
        if (s!=0){
            mArrVideoSizeStr = new String[s];
            mArrVideoWidthValue = new int[s];
            mArrVideoHeightValue = new int[s];
            int i = 0;
            for (Camera.Size size : sizeList) {
                int ws = size.width;
                int hs = size.height;
                mArrVideoSizeStr[i] = ws+" x "+hs;
                mArrVideoWidthValue[i] = ws;
                mArrVideoHeightValue[i] = hs;
                i++;
            }
        }

    }


}
