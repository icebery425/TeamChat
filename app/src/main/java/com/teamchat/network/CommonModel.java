package com.teamchat.network;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamchat.App;
import com.teamchat.listener.BaseListener;
import com.teamchat.network.CommonSubscriber;
import com.teamchat.network.NetWorkInterceptor;
import com.teamchat.network.RetrofitUtil;
import com.teamchat.network.CommonApi;
import com.teamchat.model.ResponseInfo;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;



/**
 * 接口实现
 */
public class CommonModel extends BaseModel{

    private CommonApi mCommonApi = null;
    private static CommonModel gCommonModel = null;
    private MediaType mMediaType;

    public CommonModel(){
        mCommonApi = RetrofitUtil.getRetrofit().create(CommonApi.class);
        mMediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
    }

    public static CommonModel getInstance(){
        if (gCommonModel == null){
            gCommonModel = new CommonModel();
            return gCommonModel;
        }
        return  gCommonModel;
    }

    public static void clearInstance(){
        gCommonModel = null;
    }

    public void getVerifyCode(String mobile, final BaseListener baseListener){
        String sb = "{\"mobile\":\"" + mobile + "\"}";
        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),sb.toString());
        mCommonApi.getVerifyCode(body).subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });
    }

    public void login(String userName, String psd,String verifyCode, String deviceInfo, final BaseListener baseListener){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"loginName\":\"").append(userName).append("\"");
        sb.append(",\"password\":\"").append(psd).append("\"");
        sb.append(",\"verifyCode\":\"").append(verifyCode).append("\"");
        sb.append(",\"deviceInfo\":{");
             sb.append("\"type\":10,");
             sb.append("\"deviceDesc\":\"" + Build.MODEL+ "\",");
             sb.append("\"pushId\":\"" + App.mJPushRegId + "\",");
             sb.append("\"deviceToken\":\" \"}}");


        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),sb.toString());
        mCommonApi.loginEx(body).subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });

    }


    public void logout(final BaseListener baseListener){

        mCommonApi.logout().subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });

    }



}
