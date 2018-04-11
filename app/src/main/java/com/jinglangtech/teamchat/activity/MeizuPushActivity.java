package com.jinglangtech.teamchat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jinglangtech.teamchat.R;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

public class MeizuPushActivity extends UmengNotifyClickActivity {
    private static String TAG = MeizuPushActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mipush);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.i(TAG, body);
    }
}
