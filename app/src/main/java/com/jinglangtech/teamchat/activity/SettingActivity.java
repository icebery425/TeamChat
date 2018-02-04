package com.jinglangtech.teamchat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.util.DisPlayUtil;
import com.jinglangtech.teamchat.widget.CustomPhotoSelect;

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

    private CustomPhotoSelect photoSelector;
    private CustomPhotoSelect.Builder photoBuilder;

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
            case R.id.tv_nickname:
                modifyNickName();
                break;
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

    private void modifyNickName(){
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, NameModifyActivity.class);
        startActivity(intent);
    }

    //注销
    private void loginOutRequest(){
        photoSelectAction();
    }

    private void modifyPwd(){
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, PwdModifyActivity.class);
        startActivity(intent);
    }

    private void clearCacher(){

    }

    private void setNoticeOpen(){

    }

    private void displayVersion(){

    }

    //为弹出窗口实现监听类
    private View.OnClickListener mOnSelectClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            photoSelector.dismiss();
            switch (v.getId()) {
                // 退出
                case R.id.tv_logout:
                    SettingActivity.this.finish();
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    };

    protected void photoSelectAction() {
        photoBuilder = new CustomPhotoSelect.Builder(this);
        photoBuilder.setSelectListener(mOnSelectClick);
        photoSelector = photoBuilder.create();

        Window window = photoSelector.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);  //去边界
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.actionStyle);  //添加动画
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = DisPlayUtil.dip2px(this, 165);
        window.setAttributes(lp);

        photoSelector.show();
        photoSelector.setCanceledOnTouchOutside(true);

    }
}
