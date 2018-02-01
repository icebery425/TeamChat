package com.teamchat;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.teamchat.network.RetrofitUtil;
import com.teamchat.util.ToastUtil;

import java.util.ArrayList;

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
