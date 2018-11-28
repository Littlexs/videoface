package com.yunche.android.yunchevideosdk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yunche.android.yunchevideosdk.cview.UIAlertView;
import com.yunche.android.yunchevideosdk.cview.loadingdialog.ACProgressFlower;
import com.yunche.android.yunchevideosdk.mvp.BasePresenter;
import com.yunche.android.yunchevideosdk.utils.ActivityStackManager;

public abstract class YCBaseActivity<V, T extends BasePresenter<V>> extends RxAppCompatActivity {

    protected String TAG = getClass().getSimpleName();
    protected Intent intent;
    protected Context mContext;
    protected boolean isFinish = false;
    protected boolean progressShow;
    protected ACProgressFlower progressDialog;
    public int pageSize = 20;
    public int page = 1;

    public T presenter;//mvp 表示层

    protected ActivityStackManager activityStackManager = ActivityStackManager.getManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mContext = this;//本实例  获取全局用getApplicatonContext();
        activityStackManager.push(this);
        presenter = createPresenter();
        if (presenter != null) {
            presenter.attachView((V) this);
        }
        initViews(savedInstanceState);
        loadData();
    }

    /*
    * 透明status bar
    * */
    public void transStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置布局layout
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 创建mvp presenter
     *
     * @return
     */
    protected abstract T createPresenter();

    /**
     * 初始化views
     *
     * @param savedInstanceState
     */
    public abstract void initViews(Bundle savedInstanceState);

    /**
     * 加载数据
     */
    public void loadData() {
    }

    public void dissMissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void showLoadingDialog(int res) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
        progressDialog = new ACProgressFlower.Builder(mContext)
                .text(res == 0 ? "正在加载中..." : getString(res))
                .build();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progressShow = false;
            }
        });
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void showNoCancelLoadingDialog(int res) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
        progressDialog = new ACProgressFlower.Builder(mContext).cancelAble(false)
                .text(res == 0 ? "正在加载中..." : getString(res))
                .build();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progressShow = false;
            }
        });
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void  preSetting(){
        final UIAlertView alertView = new UIAlertView(mContext,"提示","缺少必要权限，请前往设置页面设置。","取消","前往设置");
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

    //预览视频
    protected void toReviewVideo(String ossVideoUrl){
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(ossVideoUrl), "video/*");
        startActivity(intent);

//        intent = new Intent(mContext, VideoPlayerActivity.class);
//        intent.putExtra("url",ossVideoUrl);
//        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFinish = true;
        if (presenter != null) {
            presenter.detachView();//解除view的绑定，防止内存泄漏
        }
        activityStackManager.remove(this);
    }

    protected int getColorById(int resId) {
        return ContextCompat.getColor(mContext, resId);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //获取view的高度
    protected int[] viewH;
    protected void getSizeWithPost(final View... views) {
        viewH = new int[views.length];
        for (int i = 0;i<views.length;i++){
            final int finalI = i;
            views[i].post(new Runnable() {
                @Override
                public void run() {
                    viewH[finalI] = views[finalI].getTop();
                }
            });
        }
    }


    /**
     * @param cls
     */
    public void openActivity(Class<? extends AppCompatActivity> cls) {
        startActivity(new Intent(mContext, cls));
    }

    public void openActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(new Intent(mContext, cls), requestCode);
    }

    public void openActivity(Class<? extends AppCompatActivity> cls, int type) {
        intent = new Intent(mContext, cls);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    protected int getIntentType() {
        return getIntent().getIntExtra("type", -1);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void back(View v) {
        finish();
    }


}
