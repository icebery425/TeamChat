package com.jinglangtech.teamchat.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.util.ToastUtil;
import com.jinglangtech.teamchat.util.ToastUtils;

import butterknife.BindView;

public class PwdModifyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_user_name)
    TextView mTvName;
    @BindView(R.id.et_pwd_old)
    EditText mEtPwdOld;
    @BindView(R.id.et_pwd_new)
    EditText mEtPwdNew;
    @BindView(R.id.et_pwd_new_again)
    EditText mEtPwdNewAgain;
    @BindView(R.id.tv_save)
    TextView mTvSave;
    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_modify_pwd;
    }

    @Override
    public void initVariables() {

    }

    @Override
    public void initViews() {
        mTvSave.setOnClickListener(this);
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
            case R.id.tv_save:
                savePwd();
                break;
            default:
                break;
        }
    }

    //注销
    private void savePwd(){
        String oldpwd = mEtPwdOld.getText().toString();
        if (TextUtils.isEmpty(oldpwd)){
            ToastUtils.showToast(PwdModifyActivity.this,"请输入旧密码");
            return;
        }
        String newpwd = mEtPwdNew.getText().toString();
        String newpwdagain = mEtPwdNewAgain.getText().toString();
        if (TextUtils.isEmpty(newpwd) ||TextUtils.isEmpty(newpwdagain)  ){
            ToastUtils.showToast(PwdModifyActivity.this,"请输入新密码");
            return;
        }
        if (!newpwd.equals(newpwdagain)){
            ToastUtils.showToast(PwdModifyActivity.this,"两次输入的新密码不相等");
            return;
        }
        CommonModel.getInstance().modifyUserPwd(oldpwd, newpwd, new BaseListener(String.class){

            @Override
            public void responseResult(Object infoObj, Object listObj, int code, boolean status) {
                super.responseResult(infoObj, listObj, code, status);
                ToastUtils.showToast(PwdModifyActivity.this,"修改密码成功");
                PwdModifyActivity.this.finish();
            }

            @Override
            public void requestFailed(boolean status, int code, String errorMessage) {
                super.requestFailed(status, code, errorMessage);
                ToastUtils.showToast(PwdModifyActivity.this,"修改密码失败");
            }
        });
    }


}
