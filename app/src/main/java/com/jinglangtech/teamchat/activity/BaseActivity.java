package com.jinglangtech.teamchat.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.util.ActivityContainer;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity{


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

        initVariables();
        initViews();
        loadData();
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
}
