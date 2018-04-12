package com.jinglangtech.teamchat.activity;


import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.jinglangtech.teamchat.App;
import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.LoginUser;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.network.NetWorkInterceptor;
import com.jinglangtech.teamchat.service.KeepAliveService;
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.Key;
import com.jinglangtech.teamchat.util.ThreadUtil;
import com.jinglangtech.teamchat.util.ToastUtils;
import com.jinglangtech.teamchat.widget.CustomDialog;
import com.jinglangtech.teamchat.widget.OpenNotifyDialog;
import com.umeng.message.UTrack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.jpush.android.api.JPushInterface;

/**
 *  App 启动页
 */
public class AppStartActivity extends BaseActivity {

    KeepAliveService mForegroundService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            KeepAliveService.LocalBinder binder = (KeepAliveService.LocalBinder) service;
            mForegroundService = binder.getService();
            //调用服务中的方法
            //foregroundService.doSomeThingOne();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

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

                openNotificationSetting();

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
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1000);
        startForeground();
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

    private  void getUserInfo(){

    }

    private void setJPushAlias(){
        JPushInterface.setAlias(this, 888, mId);

        App mApp = (App)this.getApplication();
        if (mApp != null && mApp.mPushAgent != null){
            mApp.mPushAgent.setAlias(mId, "user_id",
                    new UTrack.ICallBack() {
                        @Override
                        public void onMessage(boolean isSuccess, String message) {
                        }
                    });
        }

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

    private OpenNotifyDialog mDialog = null;
    private void displayOpenNotifyDialog(){
        OpenNotifyDialog.Builder customBuilder = new OpenNotifyDialog.Builder(this);
        customBuilder.setNegativeButton(
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startPage();

                    }
                }).setPositiveButton(
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        goToSet();
                    }
                });
        mDialog = customBuilder.create();
        mDialog.show();
    }

    private void openNotificationSetting(){
        boolean isOpen = isNotificationEnabled(this);
        if (!isOpen){
            displayOpenNotifyDialog();
        }else{
            startPage();
        }
    }

    //判断通知栏是否打开本应用通知
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
     /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    //去通知栏打开此应用通知
    private void goToSet(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivityForResult(intent, 100);
            //startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 运行系统在5.x环境使用
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivityForResult(intent, 100);
            //startActivity(intent);
            return;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startPage();
    }

    //启动前台服务
    private void startForeground(){
        Intent intent = new Intent(AppStartActivity.this,KeepAliveService.class);
        startService(intent);
    }
    //结束前台服务
    private void stopForeground(){
        Intent intent = new Intent(AppStartActivity.this,KeepAliveService.class);
        stopService(intent);
    }

    //绑定前台服务
    private void bindForeground(){
        Intent intent = new Intent(this, KeepAliveService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    //解绑前台服务
    private void unbindForeground(){
        unbindService(mConnection);
    }
}
