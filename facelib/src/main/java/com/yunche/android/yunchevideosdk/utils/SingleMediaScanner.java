package com.yunche.android.yunchevideosdk.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

import java.io.File;

public class SingleMediaScanner implements MediaScannerConnectionClient {

    public interface ScanListener {
        public void onScanFinish();
    }

    private MediaScannerConnection mMs;
    private File mFile;
    private ScanListener listener;

    public SingleMediaScanner(Context context, File f, ScanListener l) {
        listener = l;
        mFile = f;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    public SingleMediaScanner(Context context, File f) {
        mFile = f;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMs.disconnect();
        if (listener!=null){
            listener.onScanFinish();
        }
    }

} 