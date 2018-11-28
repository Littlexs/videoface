package com.yunche.android.yunchevideosdk.cview.loadingdialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunche.android.yunchevideosdk.R;
import com.yunche.android.yunchevideosdk.cview.UIAlertView;
import com.yunche.android.yunchevideosdk.cview.loadingdialog.views.CircleBarView;

/**
 * Created by shengxiao on 2018/7/13.
 */

public class CircleProgressAlertView extends UIAlertView implements View.OnClickListener {

    public ClickNoteListenerInterface clickNoteListenerInterface;
    public CircleBarView circleBarView;
    private TextView progress;

    public CircleProgressAlertView(Context context) {
        super(context, "", "", "", "");
    }

    @Override
    public void inite() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.circle_progress_alert_view, null);
        setContentView(view);

        TextView tvLeft = (TextView) view.findViewById(R.id.tvBtnLeft);
        progress = (TextView) view.findViewById(R.id.progress);

        circleBarView = (CircleBarView) view.findViewById(R.id.circleBarView);
        circleBarView.setTextView(progress);
        circleBarView.setMaxNum(100f);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();

        lp.width = (int) (d.widthPixels);
        dialogWindow.setAttributes(lp);
    }

    public void setProgress(float prog,int time){
        progress.setText(prog+"%");
        circleBarView.setProgressNum(prog,time);
    }


    public void setFinishProgressColor(){
        progress.setText("");
        Drawable drawableLeft = ContextCompat.getDrawable(context,R.drawable.progress_finish);
        progress.setCompoundDrawablesWithIntrinsicBounds(null, drawableLeft, null, null);
        circleBarView.setFinishProgressColor("#13CE66");
    }

    public interface ClickNoteListenerInterface {
        void doLeft();

        void doRight(String note);
    }
    public void setClicklistener(ClickNoteListenerInterface clickListenerInterface) {
        this.clickNoteListenerInterface = clickListenerInterface;
    }

    @Override
    public void onClick(View v) {
        String tag = (String)v.getTag();
        switch (tag) {
            case "tvBtnLeft":
                clickNoteListenerInterface.doLeft();
                break;
            case "tvBtnRight":
                break;
            default:
                break;
        }
    }
}
