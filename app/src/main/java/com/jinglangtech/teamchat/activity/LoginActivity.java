package com.jinglangtech.teamchat.activity;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.LoginUser;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.network.NetWorkInterceptor;
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.ToastUtil;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.editLoginName)
    EditText mEtName;
    @BindView(R.id.editPwd)
    EditText mEtPwd;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    private String mName;
    private String mPwd;

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

    @Override
    public void initViews() {

    }

    @Override
    public void loadData() {

    }

    private void userLogin(){
        mName = mEtName.getText().toString();
        if (TextUtils.isEmpty(mName)){
            ToastUtil.show("请输入用户名");
            return;
        }
        mPwd = mEtPwd.getText().toString();
        if (TextUtils.isEmpty(mPwd)){
            ToastUtil.show("请输入密码");
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
                NetWorkInterceptor.setToken(user.token);
                saveSp();
                startChatPage();
            }

            @Override
            public void requestFailed(boolean status, int code, String errorMessage) {
                super.requestFailed(status, code, errorMessage);
                ToastUtil.show("登录失败" + (TextUtils.isEmpty(errorMessage)?"":errorMessage));
            }
        });
    }

    public void saveSp(){

        ConfigUtil.getInstance(this).put(Constant.KEY_LOGIN_NAME, mName);
        ConfigUtil.getInstance(this).put(Constant.KEY_LOGIN_PWD, mPwd);
        ConfigUtil.getInstance(this).commit();
    }

    public void startChatPage(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, ChatGroupActivity.class);
        startActivity(intent);
        finish();
    }


}
