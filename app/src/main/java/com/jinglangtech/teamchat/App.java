package com.jinglangtech.teamchat;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.jinglangtech.teamchat.activity.ChatGroupActivity;
import com.jinglangtech.teamchat.dbmanager.DbMigration;
import com.jinglangtech.teamchat.model.PushData;
import com.jinglangtech.teamchat.network.RetrofitUtil;
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.Key;
import com.jinglangtech.teamchat.util.ToastUtil;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import cn.jpush.android.api.JPushInterface;
import io.realm.Realm;
import io.realm.RealmConfiguration;

//import cn.jpush.android.api.JPushInterface;


public class App extends MultiDexApplication{

    public static String mJPushRegId = "";
    public String mUmengToken = null;
    public static boolean mIsChatPage = false;

    public PushAgent mPushAgent= null;


    @Override
    public void onCreate() {
        super.onCreate();

        RetrofitUtil.initRetrofit();//Retrofit 初始化
        ToastUtil.setContext(this);


        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:友盟 AppKey
         * 参数3:友盟 Channel
         * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数5:Push推送业务的secret
         */
        UMConfigure.init(this, "5ac9b6e1f43e480965000041", "jlt_umeng_push", UMConfigure.DEVICE_TYPE_PHONE, "e0381b79c7fcea26bad99c531e0b7140");

        registerUmengPushService();

        initRealm();

    }

    private void initRealm(){
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new DbMigration())
                .build();
        Realm.setDefaultConfiguration(config);
    }


    //注册友盟推送服务
    public void registerUmengPushService(){


        mPushAgent = PushAgent.getInstance(this);
        //应用在前台时否显示通知
        mPushAgent.setNotificaitonOnForeground(false);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                mUmengToken = deviceToken;

            }
            @Override
            public void onFailure(String s, String s1) {
                Log.e("", "registerUmengPushService failed: " + s +", "+ s1);
            }
        });

        umengNotifyProcess();

        HuaWeiRegister.register(this);
        MiPushRegistar.register(this, "2882303761517763828", "5101776331828");
        MeizuRegister.register(this, "1000103", "e1bd9a94a46d4cffbfe288a1204a0c87");
    }

    private UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
            Log.e("############ Notify: ", "############ Notify:" + msg.custom);

        }
    };

    private UmengMessageHandler messageHandler = new UmengMessageHandler(){
        /**
         * 通知的回调方法（通知送达时会回调）
         */
        @Override
        public void dealWithNotificationMessage(Context context, UMessage msg) {
            //调用super，会展示通知，不调用super，则不展示通知。
            super.dealWithNotificationMessage(context, msg);
            Log.e("############ Notify:", "############ dealWithNotificationMessage:" + msg.toString());
        }

        /**
         * 自定义消息的回调方法
         */
        @Override
        public void dealWithCustomMessage(final Context context, final UMessage msg) {
            new Handler(getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    // 对于自定义消息，PushSDK默认只统计送达。若开发者需要统计点击和忽略，则需手动调用统计方法。
                    boolean isClickOrDismissed = true;
                    if(isClickOrDismissed) {
                        //自定义消息的点击统计
                        UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                    } else {
                        //自定义消息的忽略统计
                        UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                    }
                    Log.e("############ Notify:", "############ Message:" + msg.custom);
                    processExtra(context, msg.custom);
                }
            });
        }
    };

    public void umengNotifyProcess(){
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        mPushAgent.setMessageHandler(messageHandler);
    }


    private void processExtra(Context ctx, String extraMsg){
        PushData dateInfo = null;
        if (!TextUtils.isEmpty(extraMsg)) {
            try {
                dateInfo = JSON.parseObject(extraMsg, PushData.class);
                if (dateInfo != null){
                    //TODO
                    notificationOper();

                    Intent intent = new Intent(ChatGroupActivity.RECEIVE_MSG_CUSTOM_ACTION);
                    intent.putExtra("jpushRoomId", dateInfo.roomid);
                    ctx.sendBroadcast(intent);
                }
            } catch (Exception e) {

            }

        }

    }




    private void notificationOper(){
        boolean issNoticeVoiceOpen = ConfigUtil.getInstance(this).get(Key.NOTICE_VOICE_OPEN, true);
        boolean issNoticeVibrateOpen = ConfigUtil.getInstance(this).get(Key.NOTICE_VIBRATE_OPEN, true);
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
        Ringtone r = RingtoneManager.getRingtone(this.getApplicationContext(), notification);
        r.play();
    }

    private void sendNotificationVibrate(){
        Vibrator vibrator;
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(260);
    }


    public void setNotifyEnable(boolean value){
        if (value){
            mPushAgent.enable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onFailure(String s, String s1) {
                }
            });
        }else{
            mPushAgent.disable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onFailure(String s, String s1) {
                }
            });
        }

    }


}
