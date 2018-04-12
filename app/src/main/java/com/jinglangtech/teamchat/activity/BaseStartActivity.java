package com.jinglangtech.teamchat.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jinglangtech.teamchat.App;
import com.jinglangtech.teamchat.util.ActivityContainer;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

import butterknife.ButterKnife;


public abstract class BaseStartActivity extends UmengNotifyClickActivity {


    public abstract int getLayoutResourceId();
    public abstract  void initVariables();
    public abstract  void initViews();
    public abstract  void loadData();
    public abstract void initStatusColor();

    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityInit();
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);
        initStatusColor();

        ActivityContainer.getInstance().addActivity(this);
        PushAgent.getInstance(this).onAppStart();

        initVariables();
        initViews();
        loadData();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1000);
    }


    public void setActivityInit(){

    }

    public void disLoading(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("加载中…");
        mProgressDialog.show();
    }

    public void disLoading(String msg){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public void disLoading(String msg, boolean bCancel){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(bCancel);
        mProgressDialog.setCanceledOnTouchOutside(bCancel);
        mProgressDialog.show();
    }

    public void hideLoading(){
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void finishActivity(View v){
        this.finish();
    }


    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        final String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.i("onMessage", body);

    }

    @Override
    protected void onPause() {
        super.onPause();
        App.mIsChatPage = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.mIsChatPage = true;
    }
}
