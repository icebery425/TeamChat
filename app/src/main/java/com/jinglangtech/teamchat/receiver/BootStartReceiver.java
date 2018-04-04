package com.jinglangtech.teamchat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jinglangtech.teamchat.activity.AppStartActivity;
import com.jinglangtech.teamchat.service.KeepAliveService;

/**
 * Created by think on 2018/4/5.
 */

public class BootStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startForeground(context);
    }

    //启动前台服务
    private void startForeground(Context ctx){
        Intent intent = new Intent(ctx,KeepAliveService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startService(intent);
    }
}
