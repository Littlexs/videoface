package com.yunche.android.yunchevideosdk.utils.statusbar;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface Bar {

    // 状态栏深色字体。
    Bar statusBarDarkFont();

    // 状态栏浅色字体。
    Bar statusBarLightFont();

    // 状态栏背景色。
    Bar statusBarBackground(int statusBarColor);

    // 状态栏背景Drawable。
    Bar statusBarBackground(Drawable drawable);

    // 状态栏背景透明度。
    Bar statusBarBackgroundAlpha(int alpha);

    // 导航栏背景色。
    Bar navigationBarBackground(int navigationBarColor);

    // 导航栏背景Drawable。
    Bar navigationBarBackground(Drawable drawable);

    // 导航栏背景透明度。
    Bar navigationBarBackgroundAlpha(int alpha);

    // 内容入侵状态栏。
    Bar invasionStatusBar();

    // 内容入侵导航栏。
    Bar invasionNavigationBar();

    // 让某一个View考虑状态栏的高度，显示在适当的位置，接受ViewId。
    Bar fitsSystemWindowView(int viewId);

    // 让某一个View考虑状态栏的高度，显示在适当的位置，接受View。
    Bar fitsSystemWindowView(View view);
}