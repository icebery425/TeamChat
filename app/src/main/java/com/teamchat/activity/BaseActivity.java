package com.teamchat.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.teamchat.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


public abstract class BaseActivity extends AppCompatActivity{


    public abstract int getLayoutResourceId();
    public abstract  void initVariables();
    public abstract  void initViews();
    public abstract  void loadData();

    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);
        initStatusColor();
        initVariables();
        initViews();
        loadData();
    }

    public void initStatusColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.common_status_color));
        }
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
