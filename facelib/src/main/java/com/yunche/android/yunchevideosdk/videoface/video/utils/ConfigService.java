package com.yunche.android.yunchevideosdk.videoface.video.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigService {
    public static ConfigEntity LoadConfig(Context context) {
        ConfigEntity configEntity = new ConfigEntity();
        SharedPreferences sharedPreferences;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 24) {
            sharedPreferences = context.getSharedPreferences(
                    "featuresConfig", Context.MODE_PRIVATE);
        } else {
            sharedPreferences = context.getSharedPreferences(
                    "featuresConfig", Context.MODE_WORLD_WRITEABLE);
        }

        configEntity.mConfigMode = sharedPreferences.getInt("ConfigMode", ConfigEntity.VIDEO_MODE_CUSTOMCONFIG);
        configEntity.mResolutionWidth = sharedPreferences.getInt("ResolutionWidth", 640);
        configEntity.mResolutionHeight = sharedPreferences.getInt("ResolutionHeight", 480);
        configEntity.mVideoBitrate = sharedPreferences.getInt("VideoBitrate", 300 * 1000);
        configEntity.mVideoFps = sharedPreferences.getInt("VideoFps", 15);
        configEntity.mVideoQuality = sharedPreferences.getInt("VideoQuality", ConfigEntity.VIDEO_QUALITY_GOOD);
        configEntity.mVideoPreset = sharedPreferences.getInt("VideoPreset", 3);
        configEntity.gop = sharedPreferences.getInt("gop", 30);

        configEntity.mmResolutionWidth = sharedPreferences.getInt("mResolutionWidth", 640);
        configEntity.mmResolutionHeight = sharedPreferences.getInt("mResolutionHeight", 480);
        configEntity.mmVideoBitrate = sharedPreferences.getInt("mVideoBitrate", 200 * 1000);
        configEntity.mmVideoFps = sharedPreferences.getInt("mVideoFps", 15);
        configEntity.mmVideoQuality = sharedPreferences.getInt("mVideoQuality", ConfigEntity.VIDEO_QUALITY_GOOD);
        configEntity.mmVideoPreset = sharedPreferences.getInt("mVideoPreset", 3);
        configEntity.mgop = sharedPreferences.getInt("mgop", 30);
//        configEntity.mVideoOverlay = sharedPreferences.getInt("VideoOverlay", 1);
//        configEntity.mVideoRotateMode = sharedPreferences.getInt("VideoRotateMode", 0);
//        configEntity.mFixColorDeviation = sharedPreferences.getInt("FixColorDeviation", 0);
//        configEntity.mVideoShowGPURender = sharedPreferences.getInt("VideoShowGPURender", 0);
//        configEntity.mVideoAutoRotation = sharedPreferences.getInt("VideoAutoRotation", 1);

//        configEntity.mEnableP2P = sharedPreferences.getInt("EnableP2P", 1);
//        configEntity.mUseARMv6Lib = sharedPreferences.getInt("UseARMv6Lib", 0);
//        configEntity.mEnableAEC = sharedPreferences.getInt("EnableAEC", 1);
//        configEntity.mUseHWCodec = sharedPreferences.getInt("UseHWCodec", 0);
        configEntity.closeMeVoice = sharedPreferences.getBoolean("closeMeVoice", false);

        return configEntity;
    }

    public static boolean getCloseMeVoice(Context context){
        SharedPreferences sharedPreferences;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 24) {
            sharedPreferences = context.getSharedPreferences(
                    "featuresConfig", Context.MODE_PRIVATE);
        } else {
            sharedPreferences = context.getSharedPreferences(
                    "featuresConfig", Context.MODE_WORLD_WRITEABLE);
        }
        Editor editor = sharedPreferences.edit();//取得编辑器
        return sharedPreferences.getBoolean("closeMeVoice", false);
    }

    public static void SaveConfig(Context context, ConfigEntity configEntity) {
        SharedPreferences sharedPreferences;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 24) {
            sharedPreferences = context.getSharedPreferences(
                    "featuresConfig", Context.MODE_PRIVATE);
        } else {
            sharedPreferences = context.getSharedPreferences(
                    "featuresConfig", Context.MODE_WORLD_WRITEABLE);
        }
        Editor editor = sharedPreferences.edit();//取得编辑器

        editor.putInt("ConfigMode", configEntity.mConfigMode);
        editor.putInt("ResolutionWidth", configEntity.mResolutionWidth);
        editor.putInt("ResolutionHeight", configEntity.mResolutionHeight);
        editor.putInt("VideoBitrate", configEntity.mVideoBitrate);
        editor.putInt("VideoFps", configEntity.mVideoFps);
        editor.putInt("VideoQuality", configEntity.mVideoQuality);
        editor.putInt("VideoPreset", configEntity.mVideoPreset);
        editor.putInt("VideoRotateMode", configEntity.mVideoRotateMode);
        editor.putInt("gop", configEntity.gop);

        editor.putInt("mResolutionWidth", configEntity.mmResolutionWidth);
        editor.putInt("mResolutionHeight", configEntity.mmResolutionHeight);
        editor.putInt("mVideoBitrate", configEntity.mmVideoBitrate);
        editor.putInt("mVideoFps", configEntity.mmVideoFps);
        editor.putInt("mVideoQuality", configEntity.mmVideoQuality);
        editor.putInt("mVideoPreset", configEntity.mmVideoPreset);
        editor.putInt("mVideoRotateMode", configEntity.mmVideoRotateMode);
        editor.putInt("mgop", configEntity.mgop);
//        editor.putInt("VideoOverlay", configEntity.mVideoOverlay);
//        editor.putInt("FixColorDeviation", configEntity.mFixColorDeviation);
//        editor.putInt("VideoShowGPURender", configEntity.mVideoShowGPURender);
//        editor.putInt("VideoAutoRotation", configEntity.mVideoAutoRotation);

//        editor.putInt("EnableP2P", configEntity.mEnableP2P);
//        editor.putInt("UseARMv6Lib", configEntity.mUseARMv6Lib);
//        editor.putInt("EnableAEC", configEntity.mEnableAEC);
//        editor.putInt("UseHWCodec", configEntity.mUseHWCodec);
//        editor.putBoolean("closeMeVoice", configEntity.closeMeVoice);

        editor.commit();
    }
}
