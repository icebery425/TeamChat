package com.jinglangtech.teamchat.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;

import butterknife.BindView;

public class NameModifyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_new_nickname)
    EditText mEtNewNickName;
    @BindView(R.id.tv_save)
    TextView mTvSave;
    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_modify_name;
    }

    @Override
    public void initVariables() {

    }

    @Override
    public void initViews() {
        mEtNewNickName.setText("");
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

    }


}
