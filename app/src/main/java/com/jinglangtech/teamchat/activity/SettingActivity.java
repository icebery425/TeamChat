package com.jinglangtech.teamchat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;

import butterknife.BindView;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_nickname)
    TextView mTvNickName;
    @BindView(R.id.tv_modify_pwd)
    TextView mTvModifyPwd;
    @BindView(R.id.tv_clear_cache)
    TextView mTvClearCache;
    @BindView(R.id.cb_notice)
    CheckBox mCbNotice;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.layout_mine_exit)
    LinearLayout mLayoutExit;
    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initVariables() {

    }

    @Override
    public void initViews() {
        mTvNickName.setOnClickListener(this);
        mLayoutExit.setOnClickListener(this);
        mTvModifyPwd.setOnClickListener(this);
        mTvClearCache.setOnClickListener(this);
        mCbNotice.setOnClickListener(this);
        mTvVersion.setOnClickListener(this);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void initStatusColor() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_mine_exit:
                loginOutRequest();
                break;
            case R.id.tv_modify_pwd:
                modifyPwd();
                break;
            case R.id.tv_clear_cache:
                clearCacher();
                break;
            case R.id.cb_notice:
                setNoticeOpen();
                break;
            case R.id.tv_version:
                displayVersion();
                break;
            default:
                break;
        }
    }

    //注销
    private void loginOutRequest(){
        this.finish();
    }

    private void modifyPwd(){
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, PwdModifyActivity.class);
        startActivity(intent);
        finish();

    }

    private void clearCacher(){

    }

    private void setNoticeOpen(){

    }

    private void displayVersion(){

    }
}
