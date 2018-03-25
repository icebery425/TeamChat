package com.jinglangtech.teamchat.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.dbmanager.RealmDbManger;
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.DisPlayUtil;
import com.jinglangtech.teamchat.util.Key;
import com.jinglangtech.teamchat.widget.CustomPhotoSelect;

import butterknife.BindView;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_user_account)
    TextView mTvAccount;
    @BindView(R.id.tv_nickname)
    TextView mTvNickName;
    @BindView(R.id.tv_modify_pwd)
    TextView mTvModifyPwd;
    @BindView(R.id.tv_clear_cache)
    TextView mTvClearCache;
    @BindView(R.id.cb_notice)
    CheckBox mCbNotice;
    @BindView(R.id.cb_notice_voice)
    CheckBox mCbNoticeVoice;
    @BindView(R.id.cb_notice_vibrate)
    CheckBox mCbNoticeVibrate;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.layout_mine_exit)
    LinearLayout mLayoutExit;

    private CustomPhotoSelect photoSelector;
    private CustomPhotoSelect.Builder photoBuilder;
    private boolean mIsNoticeOpen = false;
    private boolean mIsNoticeVoiceOpen = false;
    private boolean mIsNoticeVibrateOpen = false;

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
        mCbNoticeVoice.setOnClickListener(this);
        mCbNoticeVibrate.setOnClickListener(this);
        mTvVersion.setOnClickListener(this);

        String account = ConfigUtil.getInstance(this).get(Key.USER_ACCOUNT, "");
        String nickName = ConfigUtil.getInstance(this).get(Key.USER_NAME, "");
        mTvAccount.setText(account);
        mTvNickName.setText(nickName);

        mTvVersion.setText(getVerName());

        mIsNoticeOpen = ConfigUtil.getInstance(this).get(Key.NOTICE_OPEN, true);
        if (mIsNoticeOpen){
            mCbNotice.setChecked(true);
        }else{
            mCbNotice.setChecked(false);
        }

        mIsNoticeVoiceOpen = ConfigUtil.getInstance(this).get(Key.NOTICE_VOICE_OPEN, true);
        if (mIsNoticeVoiceOpen){
            mCbNoticeVoice.setChecked(true);
        }else{
            mCbNoticeVoice.setChecked(false);
        }

        mIsNoticeVibrateOpen = ConfigUtil.getInstance(this).get(Key.NOTICE_VIBRATE_OPEN, true);
        if (mIsNoticeVibrateOpen){
            mCbNoticeVibrate.setChecked(true);
        }else{
            mCbNoticeVibrate.setChecked(false);
        }
    }

    public String getVerName() {
        String verName = "";
        try {
            verName = this.getPackageManager().
                    getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
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
            case R.id.cb_notice_voice:
                setNoticeVoiceOpen();
                break;
            case R.id.cb_notice_vibrate:
                setNoticeVibrateOpen();
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
        this.startActivityForResult(intent, 10);
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
        confirmClearDialog();
    }

    private void setNoticeOpen(){
        if (mIsNoticeOpen){
            ConfigUtil.getInstance(this).put(Key.NOTICE_OPEN, false);
            mIsNoticeOpen = false;
        }else{
            ConfigUtil.getInstance(this).put(Key.NOTICE_OPEN, true);
            mIsNoticeOpen = true;
        }
        ConfigUtil.getInstance(this).commit();
    }
    private void setNoticeVoiceOpen(){
        if (mIsNoticeVoiceOpen){
            ConfigUtil.getInstance(this).put(Key.NOTICE_VOICE_OPEN, false);
            mIsNoticeVoiceOpen = false;
        }else{
            ConfigUtil.getInstance(this).put(Key.NOTICE_VOICE_OPEN, true);
            mIsNoticeVoiceOpen = true;
        }
        ConfigUtil.getInstance(this).commit();
    }
    private void setNoticeVibrateOpen(){
        if (mIsNoticeVibrateOpen){
            ConfigUtil.getInstance(this).put(Key.NOTICE_VIBRATE_OPEN, false);
            mIsNoticeVibrateOpen = false;
        }else{
            ConfigUtil.getInstance(this).put(Key.NOTICE_VIBRATE_OPEN, true);
            mIsNoticeVibrateOpen = true;
        }
        ConfigUtil.getInstance(this).commit();
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
                    clearSp();
                    startLoginChatPage();
                    break;
                default:
                    break;
            }
        }
    };

    public void clearSp(){
        ConfigUtil.getInstance(this).put(Key.ID, "");
        ConfigUtil.getInstance(this).put(Key.USER_ACCOUNT, "");
        ConfigUtil.getInstance(this).put(Key.USER_NAME, "");
        ConfigUtil.getInstance(this).put(Key.USER_PWD, "");
        ConfigUtil.getInstance(this).put(Key.TOKEN, "");
        ConfigUtil.getInstance(this).commit();
    }

    public void startLoginChatPage(){
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

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

    private void confirmClearDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("清除消息缓存")//设置对话框的标题
                .setMessage("你确定要清除所有消息缓存吗？")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        RealmDbManger.getRealmInstance().deleteAll();

                        Intent intent = new Intent(ChatGroupActivity.CLEAR_MSG_ACTION);
                        SettingActivity.this.sendBroadcast(intent);

                        Toast.makeText(SettingActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                }).create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10){
            if (data != null){
                int ismodify = data.getIntExtra("isModified", 0);
                if (ismodify == 1){
                    String nickName = ConfigUtil.getInstance(this).get(Key.USER_NAME, "");
                    mTvNickName.setText(nickName);
                }
            }
        }
    }
}
