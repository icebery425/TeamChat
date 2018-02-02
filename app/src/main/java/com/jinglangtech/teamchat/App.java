package com.jinglangtech.teamchat;

import android.support.multidex.MultiDexApplication;

import com.jinglangtech.teamchat.network.RetrofitUtil;
import com.jinglangtech.teamchat.util.ToastUtil;

//import cn.jpush.android.api.JPushInterface;


public class App extends MultiDexApplication{

    public static String mJPushRegId = "";
    @Override
    public void onCreate() {
        super.onCreate();

        RetrofitUtil.initRetrofit();//Retrofit 初始化
        ToastUtil.setContext(this);


        //JPushInterface.setDebugMode(true);
        //JPushInterface.init(this);
        //mJPushRegId =JPushInterface.getRegistrationID(this);

    }

}
