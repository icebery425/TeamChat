package com.jinglangtech.teamchat.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.LoginUser;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.network.NetWorkInterceptor;
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.Key;
import com.jinglangtech.teamchat.util.ThreadUtil;
import com.jinglangtech.teamchat.util.ToastUtils;
import com.jinglangtech.teamchat.widget.CustomDialog;

import cn.jpush.android.api.JPushInterface;

/**
 *  App 启动页
 */
public class AppStartActivity extends BaseActivity {



    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_start;
    }

    @Override
    public void initVariables() {

    }

    @Override
    public void initViews() {
        ThreadUtil.runAtMain(new Runnable() {
            @Override
            public void run() {
                startPage();

                //displayResendDialog();
            }
        }, 1000);
    }

    private String mLoginName = null;
    private String mPwd = null;
    private String mId = null;
    private String mAccount = null;
    private String mToken = null;

    @Override
    public void loadData() {

    }

    public void startPage(){
        mLoginName = ConfigUtil.getInstance(this).get(Key.USER_NAME, "");
        mPwd  = ConfigUtil.getInstance(this).get(Key.USER_PWD, "");

        if (!TextUtils.isEmpty(mLoginName) && !TextUtils.isEmpty(mPwd)){
            appLogin(mLoginName, mPwd);
        }else{
            startLoginPage();
        }
    }



    private void appLogin(String name, String pwd){

        CommonModel.getInstance().login(name, pwd, new BaseListener(LoginUser.class){

            @Override
            public void responseResult(Object infoObj, Object listObj, int code, boolean status) {
                super.responseResult(infoObj, listObj, code, status);
                LoginUser user = (LoginUser)infoObj;
                if (user != null){
                    NetWorkInterceptor.setToken(user.token);
                    mAccount = user.account;
                    mLoginName = user.name;
                    mId = user._id;
                    mToken = user.token;

                    saveSp();
                    setJPushAlias();
                    startChatPage();
                }

            }

            @Override
            public void requestFailed(boolean status, int code, String errorMessage) {
                super.requestFailed(status, code, errorMessage);
                //ToastUtils.showToast(AppStartActivity.this,"登录失败" + (TextUtils.isEmpty(errorMessage)?"":errorMessage));
                startLoginPage();
            }
        });
    }

    private void setJPushAlias(){
        JPushInterface.setAlias(this, 888, mId);
    }

    public void saveSp(){
        ConfigUtil.getInstance(this).put(Key.ID, mId);
        ConfigUtil.getInstance(this).put(Key.USER_ACCOUNT, mAccount);
        ConfigUtil.getInstance(this).put(Key.USER_NAME, mLoginName);
        ConfigUtil.getInstance(this).put(Key.USER_PWD, mPwd);
        ConfigUtil.getInstance(this).put(Key.TOKEN, mToken);
        ConfigUtil.getInstance(this).commit();
    }

    public void startChatPage(){
        Intent intent = new Intent();
        intent.setClass(AppStartActivity.this, ChatGroupActivity.class);
        startActivity(intent);
        finish();
    }

    public void startLoginPage(){
        Intent intent = new Intent();
        intent.setClass(AppStartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void initStatusColor() {

    }

    private CustomDialog mDialog = null;
    private void displayResendDialog(){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setNegativeButton(
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).setPositiveButton(
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        mDialog = customBuilder.create();
        mDialog.show();
    }

}
