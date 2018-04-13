package com.jinglangtech.teamchat.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.activity.AppStartActivity;
import com.jinglangtech.teamchat.util.PhoneBrand;
import com.jinglangtech.teamchat.util.SystemUtil;

/**
 * Created by think on 2018/4/4.
 */

public class KeepAliveService extends Service {

    private static final String TAG = "ForegroundService";
    public KeepAliveService() {
    }
    private LocalBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {

        public KeepAliveService getService(){
            return KeepAliveService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate:");
        //showSystemNotify();
        getPhoneBrand();
    }


    private void showSystemNotify() {
        Intent intent = new Intent(this, AppStartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("TeamChat")
                .setContentText("服务运行中")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.service_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.service_logo))
                .setContentIntent(pi)
                .build();
        startForeground(9999, notification);
    }

    private void getPhoneBrand(){
        boolean isFound = false;
        String brand = SystemUtil.getDeviceBrand();
        if (!TextUtils.isEmpty(brand)){
            String lowbrand = brand.toLowerCase();
            if (lowbrand.equals(PhoneBrand.PHONE_HUAWEI)){
                Log.d("", "HUAWEI Brand Phone");
                isFound = true;
            }else if (lowbrand.equals(PhoneBrand.PHONE_XIAOMI)){
                Log.d("", "XIAOMI Brand Phone");
                isFound = true;
            }else if (lowbrand.equals(PhoneBrand.PHONE_MEIZU)) {
                Log.d("", "MEIZU Brand Phone");
            }
        }

        if (!isFound){
            showCustomNotify();
        }

    }

    private void showCustomNotify(){
        Notification noti = new Notification();
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        noti.icon = R.drawable.ic_launcher;
        // 1、创建一个自定义的消息布局 notification.xml
        // 2、在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段
        RemoteViews rv = new RemoteViews(this.getPackageName(),
                R.layout.notify_layout);
        //rv.setImageViewResource(R.id.image,
        //        R.drawable.ic_launcher);
        //rv.setImageViewBitmap(R.id.image, bitmap);
       // rv.setTextViewText(R.id.text,
        //        "Hello,this message is in a custom expanded view");
        noti.contentView = rv;
        // 3、为Notification的contentIntent字段定义一个Intent(注意，使用自定义View不需要setLatestEventInfo()方法)

        // 这儿点击后简答启动Settings模块
        Intent intent = new Intent(this, AppStartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        noti.contentIntent = pi;

        startForeground(9999, noti);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
