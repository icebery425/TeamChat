package com.jinglangtech.teamchat;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.jinglangtech.teamchat.dbmanager.DbMigration;
import com.jinglangtech.teamchat.network.RetrofitUtil;
import com.jinglangtech.teamchat.util.ToastUtil;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

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
                umengNotifyProcess();
            }
            @Override
            public void onFailure(String s, String s1) {
                Log.e("", "registerUmengPushService failed: " + s +", "+ s1);
            }
        });
    }

    private UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
            Log.e("############", msg.custom);

        }
    };

    public void umengNotifyProcess(){
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }
}
