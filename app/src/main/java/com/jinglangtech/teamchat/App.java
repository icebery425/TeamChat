package com.jinglangtech.teamchat;

import android.support.multidex.MultiDexApplication;

import com.jinglangtech.teamchat.dbmanager.DbMigration;
import com.jinglangtech.teamchat.network.RetrofitUtil;
import com.jinglangtech.teamchat.util.ToastUtil;

import cn.jpush.android.api.JPushInterface;
import io.realm.Realm;
import io.realm.RealmConfiguration;

//import cn.jpush.android.api.JPushInterface;


public class App extends MultiDexApplication{

    public static String mJPushRegId = "";
    public static boolean mIsChatPage = false;

    @Override
    public void onCreate() {
        super.onCreate();

        RetrofitUtil.initRetrofit();//Retrofit 初始化
        ToastUtil.setContext(this);


        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        //mJPushRegId =JPushInterface.getRegistrationID(this);
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

}
