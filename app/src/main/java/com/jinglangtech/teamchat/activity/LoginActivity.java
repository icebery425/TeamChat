package com.jinglangtech.teamchat.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinglangtech.teamchat.App;
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
import com.umeng.message.UTrack;

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
    private boolean mIsNotifyOpen = false;

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
                //test();
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
                    mIvOpen.setImageResource(R.mipmap.logologin_key_unlook);
                }else{
                    mPwdOpen = true;
                    mEtPwd.setInputType(InputType.TYPE_CLASS_TEXT);
                    mIvOpen.setImageResource(R.mipmap.logologin_key_look);
                }
            }
        });
        mEtPwd.setFilters(new InputFilter[] { filter });
        mEtName.setFilters(new InputFilter[] { filter });
    }

    private final String FILTER_ASCII = "\\A\\p{ASCII}*\\z";
    InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (!(source + "").matches(FILTER_ASCII)){
                return "";
            }

            return null;
        }
    };

    @Override
    public void loadData() {

        //String uuid2 = UuidUtil.get24UUID();
        //sendNotify();
        mId = ConfigUtil.getInstance(this).get(Key.ID, "");
        String userToken = ConfigUtil.getInstance(this).get(Key.TOKEN, "");
        if (!TextUtils.isEmpty(userToken) && !TextUtils.isEmpty(mId)){
            NetWorkInterceptor.setToken(userToken);
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
                    mIsNotifyOpen = user.notification;

                    ConfigUtil.getInstance(LoginActivity.this).put(Key.NOTICE_OPEN, mIsNotifyOpen);
                    ConfigUtil.getInstance(LoginActivity.this).commit();

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

        App mApp = (App)this.getApplication();
        if (mApp != null && mApp.mPushAgent != null){
            mApp.mPushAgent.addAlias(mId, "user_id",
                    new UTrack.ICallBack() {
                        @Override
                        public void onMessage(boolean isSuccess, String message) {
                            Log.e("AppStartActivity", "#### umeng push setAlias result: " + isSuccess );
                            Log.e("AppStartActivity", "#### umeng push setAlias message: " + message );
                        }
                    });
//            mApp.mPushAgent.setAlias(mId, "user_id",
//                    new UTrack.ICallBack() {
//                        @Override
//                        public void onMessage(boolean isSuccess, String message) {
//                            Log.e("LoginActivity", "#### umeng push setAlias result: " + isSuccess );
//                            Log.e("LoginActivity", "#### umeng push setAlias message: " + message );
//                        }
//                    });
        }

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



}
