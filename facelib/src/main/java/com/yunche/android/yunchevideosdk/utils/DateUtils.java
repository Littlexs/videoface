package com.yunche.android.yunchevideosdk.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 创建人 ： shengxiao
 * 创建时间 ：2018/2/27
 * 类描述 ：
 * 备注 ：
 */

public class DateUtils {

    enum TYPE{
        YEAR_MONTH_DAY_HH_MM_SS;
    }

    public static String formatDateByType1(String now,String timeStr){
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(now);
        Date date;
        try {
            date = dateFormat2.parse(timeStr);
            return dateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long formatDateLong(String now,String timeStr){
        //SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(now);
        Date date;
        try {
            date = dateFormat2.parse(timeStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String formatLong(long time,String type){
//        if (time<=0){
//            return "";
//        }
        Date date = new Date(time);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(type);
        return dateFormat1.format(date);
    }

    public static String formatNow(String type){
        Date date = new Date();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(type);
        return dateFormat1.format(date);
    }

    //本地视频保存地址
    public static String getVideoDateFile(Context context,String orderId){
//        String rootDir = context.getExternalFilesDir(null).getAbsolutePath()
//                + File.separator +"voice"+File.separator;
        Date date = new Date();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String[] fileStrs = dateFormat1.format(date).split("-");
        StringBuilder builder = new StringBuilder();
        builder.append(Environment.getExternalStorageDirectory().getPath() +File.separator +"Video"+File.separator);
        builder.append("MP4"+File.separator);
        builder.append(fileStrs[0]+File.separator);
        builder.append(fileStrs[0]+fileStrs[1]+File.separator);
        builder.append(fileStrs[0]+fileStrs[1]+fileStrs[2]+File.separator);
        builder.append(orderId+File.separator);
//        builder.append(String.valueOf(date.getTime()));
//        builder.append(".mp4");

        Log.d("--getVideoDateFile--",builder.toString());
        return builder.toString();
    }

    public static File getVideoFile(Context context,String orderId){
       File file = new File(Environment.getExternalStorageDirectory().getPath() + "/yunche_video/");
        if(!file.exists()){
            file.mkdirs();
        }
        File videoFile = new File(Environment.getExternalStorageDirectory().getPath()+"/yunche_video/"+ new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".mp4");
        Log.i("---- Video File :",videoFile.toString());
        return videoFile;
    }

    //IMG/2018/201803/20180312/orderid/timestap.png
    public static String getUploadDateFile(String type,boolean isImg,String orderId){
        Date date = new Date();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        String[] fileStrs = dateFormat1.format(date).split("-");
        StringBuilder builder = new StringBuilder();
        builder.append((isImg?"IMG":"VIDEO")+File.separator);
        builder.append(fileStrs[0]+File.separator);
        builder.append(fileStrs[0]+fileStrs[1]+File.separator);
        builder.append(fileStrs[0]+fileStrs[1]+fileStrs[2]+File.separator);
        builder.append(orderId+File.separator);
        builder.append(String.valueOf(new Date().getTime()));
        builder.append(type);

        Log.d("--getFileTypeDate--",builder.toString());
        return builder.toString();
    }

    public static String getFaceUploadDateFile(String type,String houzhui,boolean isImg,String orderId){
        Date date = new Date();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String[] fileStrs = dateFormat1.format(date).split("-");
        StringBuilder builder = new StringBuilder();
        builder.append((isImg?"IMG":"VIDEO")+File.separator);
        builder.append(fileStrs[0]+File.separator);
        builder.append(fileStrs[0]+fileStrs[1]+File.separator);
        builder.append(fileStrs[0]+fileStrs[1]+fileStrs[2]+File.separator);
        builder.append(orderId+File.separator);
        builder.append(type+"-"+dateFormat1.format(date)+"");
        builder.append(houzhui.trim());
        Log.d("--getFileTypeDate--",builder.toString());
        return builder.toString();
    }

    public static long getTimeLong(String timeStr,String type){
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(type);
        try {
            return dateFormat1.parse(timeStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String changeTime(String timeStr,String nowType,String toType){
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(nowType);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(toType);
        try {
            Date date = dateFormat1.parse(timeStr);
            return dateFormat2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 根据年月日计算年龄,birthTimeString:"1994-11-14"
    public static int getAgeFromBirthTime(String birthTimeString) {
        // 先截取到字符串中的年、月、日
        String strs[] = birthTimeString.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        int selectMonth = Integer.parseInt(strs[1]);
        int selectDay = Integer.parseInt(strs[2]);
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;

        int age = yearMinus;// 先大致赋值
        if (yearMinus < 0) {// 选了未来的年份
            age = 0;
        } else if (yearMinus == 0) {// 同年的，要么为1，要么为0
            if (monthMinus < 0) {// 选了未来的月份
                age = 0;
            } else if (monthMinus == 0) {// 同月份的
                if (dayMinus < 0) {// 选了未来的日期
                    age = 0;
                } else if (dayMinus >= 0) {
                    age = 1;
                }
            } else if (monthMinus > 0) {
                age = 1;
            }
        } else if (yearMinus > 0) {
            if (monthMinus < 0) {// 当前月>生日月
            } else if (monthMinus == 0) {// 同月份的，再根据日期计算年龄
                if (dayMinus < 0) {
                } else if (dayMinus >= 0) {
                    age = age + 1;
                }
            } else if (monthMinus > 0) {
                age = age + 1;
            }
        }
        return age;
    }

    // 根据时间戳计算年龄
    public static int getAgeFromBirthTime(long birthTimeLong) {
        Date date = new Date(birthTimeLong);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String birthTimeString = format.format(date);
        return getAgeFromBirthTime(birthTimeString);
    }

    // 获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(Calendar.YEAR));
    }

    // 获取本月是哪一月
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(Calendar.MONTH) + 1;
    }

    // 获取今天是几号
    public static int getNowDay() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(Calendar.DAY_OF_MONTH);
    }

    public static String getTimeShowString(int seconds) {
        String strShow = new String();
        int hour = seconds / (60 * 60);
        int min = (seconds / 60) % 60;
        int s = seconds % 60;
        //String hourStr = (hour >= 10) ? "" + hour : "0" + hour;
        String minStr = (min >= 10) ? "" + min : "0" + min;
        String seondStr = (s >= 10) ? "" + s : "0" + s;
        strShow = minStr + ":" + seondStr;
        return strShow;
    }

}
