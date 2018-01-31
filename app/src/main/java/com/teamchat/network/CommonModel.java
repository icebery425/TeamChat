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


    public void login(String userName, String psd,String verifyCode, String deviceInfo, final BaseListener baseListener){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"account\":\"").append(userName).append("\"");
        sb.append(",\"password\":\"").append(psd).append("\"");
        sb.append(",\"deviceInfo\":{");
             sb.append("\"type\":10,");
             sb.append("\"deviceDesc\":\"" + Build.MODEL+ "\",");
             sb.append("\"pushId\":\"" + App.mJPushRegId + "\",");
             sb.append("\"deviceToken\":\" \"}}");


        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),sb.toString());
        mCommonApi.login(body).subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });

    }

    public void getUserInfo(String uid, final BaseListener baseListener){
        mCommonApi.getUserInfo(uid).subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });
    }


    public void modifyUserInfo(String userName, final BaseListener baseListener){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"name\":\"").append(userName).append("\"}");

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),sb.toString());
        mCommonApi.modifyUserInfo(body).subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });

    }

    public void modifyUserPwd(String oldpwd, String newPwd, final BaseListener baseListener){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"oldpsd\":\"").append(oldpwd).append("\"");
        sb.append(",\"newpsd\":\"").append(newPwd).append("\"}");

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),sb.toString());
        mCommonApi.modifyPwd(body).subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });

    }

    public void getRoomList(final BaseListener baseListener){
        mCommonApi.getRoomList().subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });
    }

    public void sendToRoom(String roomid, String content, final BaseListener baseListener){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"roomid\":\"").append(roomid).append("\"");
        sb.append(",\"content\":\"").append(content).append("\"}");

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),sb.toString());
        mCommonApi.sendToRoom(body).subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });

    }

    public void sendToUser(String userId, String content, final BaseListener baseListener){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"to\":\"").append(userId).append("\"");
        sb.append(",\"content\":\"").append(content).append("\"}");

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),sb.toString());
        mCommonApi.sendToUser(body).subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });

    }

    public void getMessage(final BaseListener baseListener) {
        mCommonApi.getMessage().subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener) {
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });
    }


    public void readMessage(String time, final BaseListener baseListener){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"time\":\"").append(time).append("\"}");

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),sb.toString());
        mCommonApi.readMessage(body).subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new CommonSubscriber(baseListener){
                    @Override
                    public void onNext(ResponseInfo responseInfo) {
                        dealJsonStr(responseInfo, baseListener);
                    }
                });

    }



}
