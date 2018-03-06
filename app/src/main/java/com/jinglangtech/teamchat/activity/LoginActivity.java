package com.jinglangtech.teamchat.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.LoginUser;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.network.NetWorkInterceptor;
import com.jinglangtech.teamchat.util.ActivityContainer;
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.Key;
import com.jinglangtech.teamchat.util.ToastUtil;
import com.jinglangtech.teamchat.util.ToastUtils;
import com.jinglangtech.teamchat.util.UuidUtil;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity{

    @BindView(R.id.editLoginName)
    EditText mEtName;
    @BindView(R.id.editPwd)
    EditText mEtPwd;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    @BindView(R.id.iv_lock)
    ImageView mIvOpen;

    private String mId;
    private String mName;
    private String mPwd;
    private String mAccount;
    private String mToken;
    private boolean mPwdOpen = false;

    private ChatGroupActivity mGroupActivity;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    public void initVariables() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
    }
    @Override
    public void initStatusColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.common_status_color));
        }
    }


    // 捕获返回键的方法2
    @Override
    public void onBackPressed() {
        Log.d("", "onBackPressed()");
        //System.exit(0);
        super.onBackPressed();
        ActivityContainer.getInstance().finishAllActivity();
        //android.os.Process.killProcess(android.os.Process.myPid());  //获取PID

    }

    @Override
    public void initViews() {
        String userAccount = ConfigUtil.getInstance(this).get(Key.USER_ACCOUNT, "");
        mEtName.setText(userAccount);
        mIvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPwdOpen){
                    mPwdOpen = false;
                    mEtPwd.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mIvOpen.setImageResource(R.mipmap.logologin_key_look);
                }else{
                    mPwdOpen = true;
                    mEtPwd.setInputType(InputType.TYPE_CLASS_TEXT);
                    mIvOpen.setImageResource(R.mipmap.logologin_key_unlook);
                }
            }
        });
    }

    @Override
    public void loadData() {

        //String uuid2 = UuidUtil.get24UUID();
        //sendNotify();
        mId = ConfigUtil.getInstance(this).get(Key.ID, "");
        String userToken = ConfigUtil.getInstance(this).get(Key.TOKEN, "");
        if (!TextUtils.isEmpty(userToken) && !TextUtils.isEmpty(mId)){
            setJPushAlias();
            startChatPage();
        }

    }

    private void userLogin(){
        mName = mEtName.getText().toString();
        if (TextUtils.isEmpty(mName)){
            ToastUtils.showToast(LoginActivity.this, "请输入用户名");
            return;
        }
        mPwd = mEtPwd.getText().toString();
        if (TextUtils.isEmpty(mPwd)){
            ToastUtils.showToast(LoginActivity.this,"请输入密码");
            return;
        }

        appLogin(mName, mPwd);
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
                    mName = user.name;
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
                ToastUtils.showToast(LoginActivity.this,"登录失败" + (TextUtils.isEmpty(errorMessage)?"":errorMessage));
            }
        });
    }

    private void setJPushAlias(){
        JPushInterface.setAlias(this, 888, mId);
    }

    public void saveSp(){
        ConfigUtil.getInstance(this).put(Key.ID, mId);
        ConfigUtil.getInstance(this).put(Key.USER_ACCOUNT, mAccount);
        ConfigUtil.getInstance(this).put(Key.USER_NAME, mName);
        ConfigUtil.getInstance(this).put(Key.USER_PWD, mPwd);
        ConfigUtil.getInstance(this).put(Key.TOKEN, mToken);
        ConfigUtil.getInstance(this).commit();
    }

    public void startChatPage(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, ChatGroupActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendNotify(){
        int notifyId = -1;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TeamChat")
                .setContentText("收到一条新消息")
                .setOnlyAlertOnce(true)
                .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notifyId, builder.build());

    }


}
