package com.jinglangtech.teamchat.activity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;

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

    }


}
