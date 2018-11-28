package com.yunche.android.yunchevideosdk.videoface.video;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class Player implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    public MediaPlayer mediaPlayer;
    private Timer mTimer = new Timer();
    private String videoUrl;
    private boolean pause;
    public static boolean finish;
    private int playPosition;
    private RadioProgress radioProgress;

    public Player(String videoUrl, RadioProgress radioProgress) {
        this.videoUrl = videoUrl;
        this.radioProgress = radioProgress;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setLooping(false);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //mediaPlayer.setOnBufferingUpdateListener(this);
            //mediaPlayer.setOnPreparedListener(this);
            //mediaPlayer.setOnCompletionListener(this);

        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }

        //mTimer.schedule(mTimerTask, 0, 10000);
    }

    /*******************************************************
     * 通过定时器和Handler来更新进度条
     ******************************************************/
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            handleProgress.sendEmptyMessage(0);
        }
    };

    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            Log.i("===",position+"   "+duration);
            if (duration > 0) {
                if (mediaPlayer.isPlaying()){
                    radioProgress.progress(position,duration);
                }
            }
        }
    };

    /**
     * 来电话了
     */
    public void callIsComing() {
        if (mediaPlayer.isPlaying()) {
            playPosition = mediaPlayer.getCurrentPosition();// 获得当前播放位置
            mediaPlayer.stop();
        }
    }


    public int getPlayPosition(){
        if (mediaPlayer!=null){
           return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 通话结束
     */
    public void callIsDown() {
        if (playPosition > 0) {
            playNet(playPosition);
            playPosition = 0;
        }
    }

    /**
     * 播放
     */
    public void play(VideoPlayerInterface face) {
        this.vface = face;
        playNet(playPosition);
    }

    private VideoPlayerInterface vface;

    public interface VideoPlayerInterface{
        void start();
    }

    /**
     * 重播
     */
    public void replay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);// 从开始位置开始播放音乐
        } else {
            playNet(0);
        }
    }

    /**
     * 暂停
     */
    public boolean pause() {
        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {// 如果正在播放
            mediaPlayer.pause();// 暂停
            pause = true;
        } else {
            if (pause) {// 如果处于暂停状态
                mediaPlayer.start();// 继续播放
                pause = false;
            }
        }
        return pause;
    }

    public void  mediaPlayer(){
        mediaPlayer = null;
    }

    public void toPosition(int l) {
        if (mediaPlayer==null || !mediaPlayer.isPlaying()) {// 如果正在播放
            mediaPlayer.seekTo(l);
        }
    }

    public void  release(){
        if (mediaPlayer!=null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 停止
     */
    public void stop() {
        //mTimer.cancel();
        //handleProgress.removeCallbacksAndMessages(null);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

//    @Override
//    /**
//     * 通过onPrepared播放
//     */
//    public void onPrepared(MediaPlayer arg0) {
//        arg0.start();
//        Log.e("mediaPlayer", "onPrepared");
//    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        Log.i("===onCompletion", "播放完毕");
        finish = true;
        //Log.i("===onCompletion", arg0.getCurrentPosition()+"  "+arg0.getDuration());
        //playPosition = arg0.getCurrentPosition();
        //mTimer.cancel();
        //handleProgress.removeCallbacksAndMessages(null);

        //radioProgress.progress(1,0);//finish
    }

//    @Override
//    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
//        int currentProgress = mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
//        Log.e(currentProgress + "% play", bufferingProgress + "% buffer");
//    }

    /**
     * 播放音乐
     *
     * @param playPosition
     */
    private void playNet(int playPosition) {
        try {
            finish = false;
            mediaPlayer.reset();// 把各项参数恢复到初始状态
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepareAsync();// 进行缓冲
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    if (vface!=null){
                        vface.start();
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("===","播放出错"+what+" "+extra);
        return false;
    }


    private final class MyPreparedListener implements
            MediaPlayer.OnPreparedListener {
        private int playPosition;

        public MyPreparedListener(int playPosition) {
            this.playPosition = playPosition;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();// 开始播放
            Log.i("=====","开始播放");
            if (playPosition > 0) {
                mediaPlayer.seekTo(playPosition);
            }
        }
    }

    public interface RadioProgress{
        void progress(int curent, int max);
    }
}