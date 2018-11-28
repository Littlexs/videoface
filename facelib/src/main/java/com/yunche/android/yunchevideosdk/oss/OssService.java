package com.yunche.android.yunchevideosdk.oss;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.yunche.android.yunchevideosdk.entity.FileProgress;
import com.yunche.android.yunchevideosdk.utils.DateUtils;
import com.yunche.android.yunchevideosdk.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by oss on 2015/12/7 0007.
 * 支持普通上传，普通下载和断点上传
 */
public class OssService {

    private OSS oss;
    private String bucket;
    private UIDisplayer UIDisplayer;
    private MultiPartUploadManager multiPartUploadManager;
    private String callbackAddress;
    //根据实际需求改变分片大小
    private final static int partSize = 256 * 1024;


    public OssService(OSS oss, String bucket) {
        this.oss = oss;
        this.bucket = bucket;
        //this.UIDisplayer = UIDisplayer;
        this.multiPartUploadManager = new MultiPartUploadManager(oss, bucket, partSize, UIDisplayer);
    }

    public void SetBucketName(String bucket) {
        this.bucket = bucket;
    }

    public void InitOss(OSS _oss) {
        this.oss = _oss;
    }

    public void setCallbackAddress(String callbackAddress) {
        this.callbackAddress = callbackAddress;
    }

    public void asyncGetFile(String bucket, String object, final String filePath, final OssUpdateInterface ossInterface) {
        if ((object == null) || object.equals("")) {
            Log.w("AsyncGetImage", "ObjectNull");
            return;
        }
//        if (!TextUtils.isEmpty(houzhui)){
//            String m = Environment.getExternalStorageDirectory() + "/yunche/new."+houzhui;
//            File dir = new File(m.substring(0, m.lastIndexOf("/") + 1));
//            if (!dir.exists()) {
//                dir.mkdir();
//            }
//            File file = new File(m);
//            if (!file.exists()) {
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }else {
        checkFile(filePath);
//        }

        GetObjectRequest get = new GetObjectRequest(bucket, object);

        OSSAsyncTask task = oss.asyncGetObejct(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                //重载InputStream来获取读取进度信息
                ProgressInputStream progressStream = new ProgressInputStream(inputStream, new OSSProgressCallback<GetObjectRequest>() {
                    @Override
                    public void onProgress(GetObjectRequest o, long currentSize, long totalSize) {
                        Log.d("GetObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                        int progress = (int) (100 * currentSize / totalSize);
//                        UIDisplayer.updateProgress(progress);
//                        UIDisplayer.displayInfo("下载进度: " + String.valueOf(progress) + "%");
                        if (ossInterface != null) {
                            ossInterface.ossProgress(progress);
                        }
                    }
                }, result.getContentLength());
                writeFile(progressStream,filePath);
                if (ossInterface != null) {
                    ossInterface.ossNetSuccess(filePath);
                }
                //Bitmap bm = BitmapFactory.decodeStream(inputStream);
//                try {
//                    //需要根据对应的View大小来自适应缩放
//                    Bitmap bm = UIDisplayer.autoResizeFromStream(progressStream);
//                    UIDisplayer.downloadComplete(bm);
//                    UIDisplayer.displayInfo("Bucket: " + bucket + "\nObject: " + request.getObjectKey() + "\nRequestId: " + result.getRequestId());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                String info = "";
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    info = clientExcepion.toString();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                    info = serviceException.toString();
                }
                if (ossInterface != null) {
                    ossInterface.ossNetError(info);
                }
//                UIDisplayer.downloadFail(info);
//                UIDisplayer.displayInfo(info);
            }
        });
    }


    //异步上传单个文件
    //position: 列表的position，如果上传单个文件，传0
    private OssInterface ossInterface;
    private int position;

    public void cancel() {
        if (task != null) {
            task.cancel();
        }
    }

    private OSSAsyncTask task;

    public void asyncPutFile(final int position, final String localFile, String orderId, final OssInterface ossInterface) {
        this.position = position;
        this.ossInterface = ossInterface;
        if (localFile.equals("")) {
            Log.w("AsyncPutImage", "ObjectNull");
            return;
        }

        File file = new File(localFile);
        if (!file.exists()) {
            Log.w("AsyncPutImage", "FileNotExist");
            Log.w("LocalFile", localFile);
            return;
        }

        // 文件后缀
        String objectKey = "";
        Log.i("---file.toString:", file.toString());
        if (file.isFile()) {
            // 获取文件后缀名
            objectKey = DateUtils.getUploadDateFile(file.getName().substring(file.getName().lastIndexOf(".")), isImg, orderId);
        }
        Log.i("---objectKey:", objectKey);


        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, objectKey, localFile);

        if (callbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", callbackAddress);
                    //callbackBody可以自定义传入的信息
                    put("callbackBody", "filename=${object}");
                }
            });
        }
        final FileProgress fileProgress = new FileProgress();
        fileProgress.setProgress(0);
        fileProgress.setUrl(localFile);
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                //Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                int progress = (int) (100 * currentSize / totalSize);
//                UIDisplayer.updateProgress(progress);
//                UIDisplayer.displayInfo("上传进度: " + String.valueOf(progress) + "%");
                fileProgress.setProgress(progress);
                //ossInterface.ossProgress(position,fileProgress);
                Message mes = handler.obtainMessage(1, position, position, fileProgress);
                mes.sendToTarget();
            }
        });

        final String finalObjectKey = objectKey;
        task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());

//                UIDisplayer.uploadComplete();
//                UIDisplayer.displayInfo("Bucket: " + bucket
//                        + "\nObject: " + request.getObjectKey()
//                        + "\nETag: " + result.getETag()
//                        + "\nRequestId: " + result.getRequestId()
//                        + "\nCallback: " + result.getServerCallbackReturnBody());
                //ossInterface.ossNetSuccess(position);
                //Log.i("---",result.getServerCallbackReturnBody());
                Log.i("---onSuccess objectKey:", request.getObjectKey());
                Message mes = handler.obtainMessage(2, position, position, request.getObjectKey());
                mes.sendToTarget();
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                String info = "";
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    info = clientExcepion.toString();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                    info = serviceException.toString();
                }
//                UIDisplayer.uploadFail(info);
//                UIDisplayer.displayInfo(info);
            }
        });
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            switch (inputMessage.what) {
                case 1:
                    ossInterface.ossProgress(inputMessage.arg1, (FileProgress) inputMessage.obj);
                    break;
                case 2:
                    ossInterface.ossNetSuccess(inputMessage.arg1, (String) inputMessage.obj);
                    break;
            }
        }
    };

    private boolean isImg = true;//是否图片上传，默认true

    public void isImgUpload(boolean isImg) {
        this.isImg = isImg;
    }

    //异步上传
    public void asyncPutFiles(final List<String> urls, String orderId, final OssInterface ossInterface) {
        for (int i = 0; i < urls.size(); i++) {
            asyncPutFile(i, urls.get(i), orderId, ossInterface);
        }
    }

    /**
     //上传面签视频
     * @param urls   视频本地路径，建议不要超过3个
     * @param fileName  oss文件名称,自定义,一般是  业务团队名称-客户名称
     * @param orderId   订单id,用来形成oss文件路径
     * @param ossInterface  回调接口
     */
    public void asyncPutVideoFaceFiles(final List<String> urls, String fileName,String orderId, final OssInterface ossInterface) {
        for (int i = 0; i < urls.size(); i++) {
            asyncPutFaceFile(i, urls.get(i), fileName,orderId,  ossInterface);
        }
    }

    public void asyncPutFaceFile(final int position, final String localFile, String fileName,String orderId, final OssInterface ossInterface) {
        this.position = position;
        this.ossInterface = ossInterface;
        if (localFile.equals("")) {
            Log.w("AsyncPutImage", "ObjectNull");
            return;
        }

        File file = new File(localFile);
        if (!file.exists()) {
            Log.w("AsyncPutImage", "FileNotExist");
            Log.w("LocalFile", localFile);
            return;
        }

        // 文件后缀
        String objectKey = "";
        Log.i("---file.toString:", file.toString());
        if (file.isFile()) {
            // 获取文件后缀名
            objectKey = DateUtils.getFaceUploadDateFile(fileName,localFile.substring(localFile.indexOf(".")), isImg, orderId);
        }
        Log.i("---objectKey:", objectKey);


        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, objectKey, localFile);

        if (callbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", callbackAddress);
                    //callbackBody可以自定义传入的信息
                    put("callbackBody", "filename=${object}");
                }
            });
        }
        final FileProgress fileProgress = new FileProgress();
        fileProgress.setProgress(0);
        fileProgress.setUrl(localFile);
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                //Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                int progress = (int) (100 * currentSize / totalSize);
//                UIDisplayer.updateProgress(progress);
//                UIDisplayer.displayInfo("上传进度: " + String.valueOf(progress) + "%");
                fileProgress.setProgress(progress);
                //ossInterface.ossProgress(position,fileProgress);
                Message mes = handler.obtainMessage(1, position, position, fileProgress);
                mes.sendToTarget();
            }
        });

        final String finalObjectKey = objectKey;
        task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());

//                UIDisplayer.uploadComplete();
//                UIDisplayer.displayInfo("Bucket: " + bucket
//                        + "\nObject: " + request.getObjectKey()
//                        + "\nETag: " + result.getETag()
//                        + "\nRequestId: " + result.getRequestId()
//                        + "\nCallback: " + result.getServerCallbackReturnBody());
                //ossInterface.ossNetSuccess(position);
                //Log.i("---",result.getServerCallbackReturnBody());
                Log.i("---onSuccess objectKey:", request.getObjectKey());
                Message mes = handler.obtainMessage(2, position, position, request.getObjectKey());
                mes.sendToTarget();
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                String info = "";
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    info = clientExcepion.toString();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                    info = serviceException.toString();
                }
//                UIDisplayer.uploadFail(info);
//                UIDisplayer.displayInfo(info);
            }
        });
    }

    //断点上传，返回的task可以用来暂停任务
    public PauseableUploadTask asyncMultiPartUpload(String object, String localFile) {
        if (object.equals("")) {
            Log.w("AsyncMultiPartUpload", "ObjectNull");
            return null;
        }

        File file = new File(localFile);
        if (!file.exists()) {
            Log.w("AsyncMultiPartUpload", "FileNotExist");
            Log.w("LocalFile", localFile);
            return null;
        }

        Log.d("MultiPartUpload", localFile);
        PauseableUploadTask task = multiPartUploadManager.asyncUpload(object, localFile);
        return task;
    }



    //同步上传
    public void syncPutFiles(final List<String> urls, final String orderId, final OssInterface ossInterface) {
        if (urls.size() <= 0) {
            // 文件全部上传完毕，这里编写上传结束的逻辑，如果要在主线程操作，最好用Handler或runOnUiThead做对应逻辑
//            UIDisplayer.uploadComplete();
//            UIDisplayer.displayInfo("Bucket: " + bucket);
            ossInterface.ossNetSuccess(0, "");
            return;// 这个return必须有，否则下面报越界异常
        }

        final String url = urls.get(0);
        if (TextUtils.isEmpty(url)) {
            urls.remove(0);
            // url为空就没必要上传了，这里做的是跳过它继续上传的逻辑。
            syncPutFiles(urls, orderId, ossInterface);
            return;
        }

        File file = new File(url);
        if (null == file || !file.exists()) {
            urls.remove(0);
            // 文件为空或不存在就没必要上传了，这里做的是跳过它继续上传的逻辑。
            syncPutFiles(urls, orderId, ossInterface);
            return;
        }

        // 文件后缀
        String objectKey = "";
        if (file.isFile()) {
            // 获取文件后缀名
            objectKey = DateUtils.getUploadDateFile(file.getName().substring(file.getName().lastIndexOf(".")), isImg, orderId);
        }


        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, objectKey, url);

        if (callbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", callbackAddress);
                    //callbackBody可以自定义传入的信息
                    put("callbackBody", "filename=${object}");
                }
            });
        }

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                //Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                int progress = (int) (100 * currentSize / totalSize);
//                UIDisplayer.updateProgress(progress);
//                UIDisplayer.displayInfo("上传进度: " + String.valueOf(progress) + "%");
                ossInterface.ossProgress(0, null);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());

                urls.remove(0);
                syncPutFiles(urls, orderId, ossInterface);// 递归同步效果
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                String info = "";
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    info = clientExcepion.toString();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                    info = serviceException.toString();
                }
//                UIDisplayer.uploadFail(info);
//                UIDisplayer.displayInfo(info);
                ossInterface.ossNetError(info);
            }
        });
    }


    public void checkFile(String filePath) {
        Log.i("local file url :", filePath);
        File dir = new File(filePath.substring(0, filePath.lastIndexOf("/") + 1));
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeFile(InputStream is,String filePath) {

        try {
            FileOutputStream fileOut = new FileOutputStream(new File(filePath));
            byte[] buf = new byte[1024 * 8];
            while (true) {
                int read = 0;
                if (is != null) {
                    read = is.read(buf);
                }
                if (read == -1) {
                    break;
                }
                fileOut.write(buf, 0, read);
            }
            // 查看文件获取是否成功
            if (fileOut.getFD().valid()) {
                Log.i("===", "保存成功");
            } else {
                Log.i("===", "获取文件失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //下载视频面签音频
    public void asyncGetFaceFile(String bucket, String ossPath,String ossFileName,String localPathName, final OssUpdateInterface ossInterface) {
        String object = ossPath+ossFileName;
        if ((ossFileName == null) || ossFileName.equals("")) {
            Log.w("AsyncGetImage", "ObjectNull");
            return;
        }

        final File file = FileUtil.createFile(Environment.getExternalStorageDirectory() + "/"+localPathName+ "/",ossFileName);

        GetObjectRequest get = new GetObjectRequest(bucket, object);

        OSSAsyncTask task = oss.asyncGetObejct(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                //重载InputStream来获取读取进度信息
                ProgressInputStream progressStream = new ProgressInputStream(inputStream, new OSSProgressCallback<GetObjectRequest>() {
                    @Override
                    public void onProgress(GetObjectRequest o, long currentSize, long totalSize) {
                        //Log.d("GetObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                        int progress = (int) (100 * currentSize / totalSize);
                        if (ossInterface != null) {
                            ossInterface.ossProgress(progress);
                        }
                    }
                }, result.getContentLength());
                writeFile(progressStream,file.getPath());
                if (ossInterface != null) {
                    ossInterface.ossNetSuccess(file.getPath());
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                String info = "";
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    info = clientExcepion.toString();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                    info = serviceException.toString();
                }
                if (ossInterface != null) {
                    ossInterface.ossNetError(info);
                }
            }
        });
    }




}
