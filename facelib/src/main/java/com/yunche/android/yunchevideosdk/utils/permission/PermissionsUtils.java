package com.yunche.android.yunchevideosdk.utils.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by donglua on 2016/10/19.
 */

public class PermissionsUtils {

    public static boolean checkReadStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        int readStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);

        boolean readStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED;

        boolean preok = !readStoragePermissionGranted;
        if (preok) {
            ActivityCompat.requestPermissions(activity,
                    PermissionsConstant.PERMISSIONS_EXTERNAL_READ,
                    PermissionsConstant.REQUEST_EXTERNAL_READ);
        }
        return !preok;
    }

    public static boolean checkRWStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        int readStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int write =
                ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        boolean readStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        boolean writeStoragePermissionGranted = write == PackageManager.PERMISSION_GRANTED;

        boolean preok = !writeStoragePermissionGranted || !readStoragePermissionGranted;
        if (preok) {
            ActivityCompat.requestPermissions(activity,
                    PermissionsConstant.PERMISSIONS_EXTERNAL_READ_WRITE,
                    PermissionsConstant.REQUEST_READ_WRITE);
        }
        return !preok;
    }

    public static boolean checkRWStoragePermission(Fragment fragment) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        int readStoragePermissionState =
                ContextCompat.checkSelfPermission(fragment.getContext(), READ_EXTERNAL_STORAGE);
        int write =
                ContextCompat.checkSelfPermission(fragment.getContext(), WRITE_EXTERNAL_STORAGE);

        boolean readStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        boolean writeStoragePermissionGranted = write == PackageManager.PERMISSION_GRANTED;

        boolean preok = !writeStoragePermissionGranted || !readStoragePermissionGranted;
        if (preok) {
            fragment.requestPermissions(
                    PermissionsConstant.PERMISSIONS_EXTERNAL_READ_WRITE,
                    PermissionsConstant.REQUEST_READ_WRITE);
        }
        return !preok;
    }

    public static boolean checkWriteStoragePermission(Fragment fragment) {

        int writeStoragePermissionState =
                ContextCompat.checkSelfPermission(fragment.getContext(), WRITE_EXTERNAL_STORAGE);

        boolean writeStoragePermissionGranted = writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;

        boolean preok = !writeStoragePermissionGranted;
        if (preok) {
            fragment.requestPermissions(
                    PermissionsConstant.PERMISSIONS_EXTERNAL_WRITE,
                    PermissionsConstant.REQUEST_EXTERNAL_WRITE);
        }
        return !preok;
    }

    public static boolean checkCameraPermission(Fragment fragment) {
        int cameraPermissionState = ContextCompat.checkSelfPermission(fragment.getContext(), CAMERA);

        boolean cameraPermissionGranted = cameraPermissionState == PackageManager.PERMISSION_GRANTED;
        boolean preok = !cameraPermissionGranted;
        if (preok) {
            fragment.requestPermissions(
                    PermissionsConstant.PERMISSIONS_CAMERA,
                    PermissionsConstant.REQUEST_CAMERA);
        }
        return !preok;
    }

    public static boolean checkWriteStoragePermission(Activity activity) {

        int writeStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        boolean writeStoragePermissionGranted = writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;

        boolean preok = !writeStoragePermissionGranted;

        if (preok) {
            ActivityCompat.requestPermissions(activity,
                    PermissionsConstant.PERMISSIONS_EXTERNAL_WRITE,
                    PermissionsConstant.REQUEST_EXTERNAL_WRITE);
        }
        return !preok;
    }

    public static boolean checkVideoPermission(Fragment fragment) {
        int cameraPermissionState = ContextCompat.checkSelfPermission(fragment.getContext(), CAMERA);
        int readStoragePermissionState =
                ContextCompat.checkSelfPermission(fragment.getContext(), READ_EXTERNAL_STORAGE);
        int write =
                ContextCompat.checkSelfPermission(fragment.getContext(), WRITE_EXTERNAL_STORAGE);
        int audio =
                ContextCompat.checkSelfPermission(fragment.getContext(), RECORD_AUDIO);

        boolean readStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        boolean writeStoragePermissionGranted = write == PackageManager.PERMISSION_GRANTED;
        boolean cameraPermissionGranted = cameraPermissionState == PackageManager.PERMISSION_GRANTED;
        boolean audioGranted = audio == PackageManager.PERMISSION_GRANTED;

        boolean preok = !cameraPermissionGranted
                || !readStoragePermissionGranted
                || !writeStoragePermissionGranted
                || !audioGranted;

        if (preok) {
            fragment.requestPermissions(
                    PermissionsConstant.PERMISSIONS_VIDEO,
                    PermissionsConstant.REQUEST_VIDEO);
        }
        return !preok;
    }


    public static boolean checkVideoPermission(Activity activity) {
        int cameraPermissionState = ContextCompat.checkSelfPermission(activity, CAMERA);
        int readStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int write =
                ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        int audio =
                ContextCompat.checkSelfPermission(activity, RECORD_AUDIO);
        int phone =
                ContextCompat.checkSelfPermission(activity, READ_PHONE_STATE);

        boolean readStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        boolean writeStoragePermissionGranted = write == PackageManager.PERMISSION_GRANTED;
        boolean cameraPermissionGranted = cameraPermissionState == PackageManager.PERMISSION_GRANTED;
        boolean audioGranted = audio == PackageManager.PERMISSION_GRANTED;
        boolean phoneGranted = phone == PackageManager.PERMISSION_GRANTED;

        boolean preok = !cameraPermissionGranted
                || !readStoragePermissionGranted
                || !writeStoragePermissionGranted
                || !audioGranted
                || !phoneGranted;

        Log.i("---","preok: "+preok+"   "+cameraPermissionGranted+readStoragePermissionGranted+writeStoragePermissionGranted+audioGranted);

        if (preok) {

            ActivityCompat.requestPermissions(activity,
                    PermissionsConstant.PERMISSIONS_VIDEO,
                    PermissionsConstant.REQUEST_VIDEO);
        }
        return !preok;
    }


    public static boolean checkOCRCameraPermission(Activity activity) {
        int cameraPermissionState = ContextCompat.checkSelfPermission(activity, CAMERA);

        int readStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int write =
                ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        boolean readStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        boolean writeStoragePermissionGranted = write == PackageManager.PERMISSION_GRANTED;

        boolean cameraPermissionGranted = cameraPermissionState == PackageManager.PERMISSION_GRANTED;

        boolean preok = !cameraPermissionGranted
                || !readStoragePermissionGranted
                || !writeStoragePermissionGranted;

        if (preok) {
            ActivityCompat.requestPermissions(activity,
                    PermissionsConstant.PERMISSIONS_CAMERA,
                    PermissionsConstant.REQUEST_OCR_CAMERA);
        }
        return !preok;
    }

    public static boolean checkCameraPermission(Activity activity) {
        int cameraPermissionState = ContextCompat.checkSelfPermission(activity, CAMERA);

        int readStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int write =
                ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        boolean readStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        boolean writeStoragePermissionGranted = write == PackageManager.PERMISSION_GRANTED;

        boolean cameraPermissionGranted = cameraPermissionState == PackageManager.PERMISSION_GRANTED;

        boolean preok = !cameraPermissionGranted
                || !readStoragePermissionGranted
                || !writeStoragePermissionGranted;

        if (preok) {
            ActivityCompat.requestPermissions(activity,
                    PermissionsConstant.PERMISSIONS_CAMERA,
                    PermissionsConstant.REQUEST_CAMERA);
        }
        return !preok;
    }

    //定位
    public static boolean checkLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        int readStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int write =
                ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        int phonestate =
                ContextCompat.checkSelfPermission(activity, READ_PHONE_STATE);
        int cross =
                ContextCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION);
        int fine =
                ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION);

        boolean readStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        boolean writeStoragePermissionGranted = write == PackageManager.PERMISSION_GRANTED;
        boolean phonestatePermissionGranted = phonestate == PackageManager.PERMISSION_GRANTED;

        boolean crossPermissionGranted = cross == PackageManager.PERMISSION_GRANTED;

        boolean finePermissionGranted = fine == PackageManager.PERMISSION_GRANTED;


        boolean preok = !finePermissionGranted
                || !crossPermissionGranted
                || !phonestatePermissionGranted
                || !readStoragePermissionGranted
                || !writeStoragePermissionGranted;

        if (preok) {
            ActivityCompat.requestPermissions(activity,
                    PermissionsConstant.PERMISSIONS_LOCATION,
                    PermissionsConstant.REQUEST_LOCATION);
        }
        return !preok;
    }

}
