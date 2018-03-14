package com.jinglangtech.teamchat.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.Key;
import com.jinglangtech.teamchat.util.ToastUtil;
import com.jinglangtech.teamchat.util.ToastUtils;

import butterknife.BindView;

public class NameModifyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_new_nickname)
    EditText mEtNewNickName;
    @BindView(R.id.tv_save)
    TextView mTvSave;

    private String mOldName;
    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_modify_name;
    }

    @Override
    public void initVariables() {

    }

    @Override
    public void initViews() {
        mOldName = ConfigUtil.getInstance(this).get(Key.USER_NAME, "");
        mEtNewNickName.setText(mOldName);
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
                saveNickName();
                break;
            default:
                break;
        }
    }

    //注销
    private void saveNickName(){
        final String newName = mEtNewNickName.getText().toString();
        if (TextUtils.isEmpty(newName)){
            ToastUtils.showToast(NameModifyActivity.this,"请输入新用户名");
            return;
        }

        if (!TextUtils.isEmpty(mOldName) && newName.equals(mOldName)){
            ToastUtils.showToast(NameModifyActivity.this,"新用户名不能和以前用户相同");
            return;
        }
        CommonModel.getInstance().modifyUserInfo(newName, new BaseListener(String.class){

            @Override
            public void responseResult(Object infoObj, Object listObj, int code, boolean status) {
                super.responseResult(infoObj, listObj, code, status);
                ToastUtils.showToast(NameModifyActivity.this,"修改用户名称成功");
                ConfigUtil.getInstance(NameModifyActivity.this).put(Key.USER_NAME, newName);
                ConfigUtil.getInstance(NameModifyActivity.this).commit();
                Intent intent = new Intent();
                intent.putExtra("isModified", 1);
                NameModifyActivity.this.setResult(10, intent);
                NameModifyActivity.this.finish();
            }

            @Override
            public void requestFailed(boolean status, int code, String errorMessage) {
                super.requestFailed(status, code, errorMessage);
                ToastUtils.showToast(NameModifyActivity.this,"修改用户名称失败");
            }
        });
    }


}
