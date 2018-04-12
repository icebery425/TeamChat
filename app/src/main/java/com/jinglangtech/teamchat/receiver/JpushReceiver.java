package com.jinglangtech.teamchat.receiver;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.jinglangtech.teamchat.App;
import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.activity.AppStartActivity;
import com.jinglangtech.teamchat.activity.ChatGroupActivity;
import com.jinglangtech.teamchat.activity.MainActivity;
import com.jinglangtech.teamchat.model.PushData;
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.Key;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by icebery on 2017/5/24 0024.
 */

public class JpushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private Context mCtx;

    @Override
    public void onReceive(Context context, Intent intent) {

        mCtx = context;
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            if (!TextUtils.isEmpty(regId)) {
                //MeApplication.mJPushRegId = regId;
            }
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String extraMsg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extraExtra = bundle.getString(JPushInterface.EXTRA_EXTRA);

            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + extraMsg);
            if (Constant.JPUSH_ENABLE){
                processExtra(context, extraExtra);
                sendNotify();
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            //NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //manager.cancel(notifactionId);
            //manager.cancelAll();

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            //processNotify(context, bundle);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void sendNotify(){
        if (App.mIsChatPage){
            notificationOper();
            return;
        }

        boolean issNoticeOpen = ConfigUtil.getInstance(mCtx).get(Key.NOTICE_OPEN, true);
        if (!issNoticeOpen){
            return;
        }

        Context application = mCtx.getApplicationContext();
        Intent resultIntent = new Intent(application, AppStartActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(application, 0, resultIntent, 0);


        int notifyId = 1000;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mCtx)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("TeamChat")
                .setContentText("收到一条新消息")
                .setOnlyAlertOnce(false)
                //.setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

        builder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS);


        NotificationManager manager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notifyId, builder.build());

    }

    private void notificationOper(){
        boolean issNoticeVoiceOpen = ConfigUtil.getInstance(mCtx).get(Key.NOTICE_VOICE_OPEN, true);
        boolean issNoticeVibrateOpen = ConfigUtil.getInstance(mCtx).get(Key.NOTICE_VIBRATE_OPEN, true);
        if (issNoticeVoiceOpen && issNoticeVibrateOpen) {
            sendNotificationVoice();
            sendNotificationVibrate();
        }else if(issNoticeVoiceOpen){
            sendNotificationVoice();
        }else if (issNoticeVibrateOpen){
            sendNotificationVibrate();
        }

    }

    private void sendNotificationVoice(){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(mCtx.getApplicationContext(), notification);
        r.play();
    }

    private void sendNotificationVibrate(){
        Vibrator vibrator;
        vibrator = (Vibrator) mCtx.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(260);
    }



    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it =  json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " +json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }


    private void processExtra(Context ctx, String extraMsg){
        PushData dateInfo = null;
        if (!TextUtils.isEmpty(extraMsg)) {
            try {
                dateInfo = JSON.parseObject(extraMsg, PushData.class);
                if (dateInfo != null){
                    //TODO
                    Intent intent = new Intent(ChatGroupActivity.RECEIVE_MSG_CUSTOM_ACTION);
                    intent.putExtra("jpushRoomId", dateInfo.roomid);
                    ctx.sendBroadcast(intent);
                }
            } catch (Exception e) {

            }

        }

    }

    private boolean isAppForceground(Context ctx){
        boolean ret = false;
        ActivityManager am = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
                 List<ActivityManager.RunningAppProcessInfo> runnings = am.getRunningAppProcesses();
                 for(ActivityManager.RunningAppProcessInfo running : runnings){
                         if(running.processName.equals(ctx.getPackageName())){
                                 if(running.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                                         || running.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE){
                                         //前台显示... 8
                                     ret = true;
                                 }else{
                                         //后台显示...10
                                 }
                                    break;
                                }
                        }
        return ret ;
    }



}
