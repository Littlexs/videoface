package com.yunche.android.yunchevideosdk.videoface.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.Hashtable;

/**
 * Created by xc on 2015/7/27.
 */
public final class VideoUtils {


    //通用预览  "video/*"  "image/*" content://com.yunche.finance.android.fileprovider/external_files/yunche_video_face/%E7%8E%8B%E4%B8%9C1809131513338057319/%E7%8E%8B%E4%B8%9C_%E5%8E%8B%E7%BC%A9%E8%A7%86%E9%A2%91.mp4
    public static Intent getVideoReviewIntent(Context context, String filePath, String fileType){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if(Build.VERSION.SDK_INT< 24){
            uri = Uri.fromFile(new File(filePath));
        }else{
            uri = FileProvider.getUriForFile(context, "com.yunche.android.yunchevideosdk.fileprovider", new File(filePath));
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, fileType);
        return intent;
    }




    public static Bitmap getVideoThumbnail(String filePath, int kind){
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (filePath.startsWith("http://")
                    || filePath.startsWith("https://")
                    || filePath.startsWith("widevine://")) {
                retriever.setDataSource(filePath,new Hashtable<String,String>());
            }else {
                retriever.setDataSource(filePath);
            }
            bitmap =retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
                ex.printStackTrace();
            }
        }

        if (bitmap==null)return null;

        if (kind== MediaStore.Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.
            int width= bitmap.getWidth();
            int height= bitmap.getHeight();
            int max =Math.max(width, height);
            if(max >512) {
                float scale=512f / max;
                int w =Math.round(scale * width);
                int h =Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap,w, h, true);
            }
        } else if (kind== MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    96,
                    96,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }



}