package com.yunche.android.yunchevideosdk.cview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;


public class NetSeepView extends AppCompatTextView {

    private SpeedType type = SpeedType.UP;//默认上传
    private long total_tdata = TrafficStats.getTotalTxBytes();
    private long total_rdata = TrafficStats.getTotalRxBytes();
    private int count = 1;
    private String seep;//网速

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            StringBuilder stringBuilder = new StringBuilder();
            if (msg.arg1 > (1024 * 1024)) {
                stringBuilder.append("↑"+msg.arg1 / 1024 / 1024 + "Mb/s");
            } else if (msg.arg1 > 1024) {
                stringBuilder.append("↑"+msg.arg1 / 1024 + "Kb/s");
            } else {
                stringBuilder.append("↑"+msg.arg1 + "b/s");
            }
            if (msg.arg2 > (1024 * 1024)) {
                stringBuilder.append("\n↓"+msg.arg2 / 1024 / 1024 + "Mb/s");
            } else if (msg.arg2 > 1024) {
                stringBuilder.append("\n↓"+msg.arg2 / 1024 + "Kb/s");
            } else {
                stringBuilder.append("\n↓"+msg.arg2 + "b/s");
            }
            seep = stringBuilder.toString();
            Log.i("----网速：",seep);
            setText(seep);
            //invalidate();
        }
    };
    /**
     * 定义线程周期性的获取网速
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(mRunnable, count * 3000);
            Message msg = mHandler.obtainMessage();
            int[] j = getNetSpeed();
            msg.arg1 = j[0];
            msg.arg2 = j[1];
            mHandler.sendMessage(msg);
        }
    };

    public NetSeepView(Context context) {
        super(context);
    }

    public NetSeepView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetSeepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setPadding(0, 0, 0, 0);
        setTextColor(Color.BLACK);
        setTextSize(36);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private int[] getNetSpeed() {
        long traffic_data=0,rtraffic_data=0;
        //if (type == SpeedType.UP) {//上传
            traffic_data = TrafficStats.getTotalTxBytes() - total_tdata;//总的发送的字节数
            total_tdata = TrafficStats.getTotalTxBytes();
        //} else {//下载
            rtraffic_data = TrafficStats.getTotalRxBytes() - total_rdata;//总的接受字节数
            total_rdata = TrafficStats.getTotalRxBytes();
        //}
        int[] asd = new int[2];
        asd[0] = (int) (traffic_data / count);
        asd[1] = (int) (rtraffic_data / count);
        return asd;
    }

    public void start() {
        mHandler.postDelayed(mRunnable, 0);
    }

    public void start(SpeedType type) {
        setType(type);
        mHandler.postDelayed(mRunnable, 0);
    }

    public void stop() {
        mHandler.removeCallbacks(mRunnable);
    }

    /**
     * 设置显示上传或下载速度
     *
     * @param type
     */
    public void setType(SpeedType type) {
        this.type = type;
    }

    public enum SpeedType {
        UP, DOWN
    }
}