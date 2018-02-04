package com.jinglangtech.teamchat.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;


import com.jinglangtech.teamchat.activity.ChatGroupActivity;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.ChatMsg;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.TimeConverterUtil;
import com.jinglangtech.teamchat.util.ToastUtil;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmAsyncTask;

public class PushMessageToLocalService extends IntentService {

    public PushMessageToLocalService() {
        super("PushMessageToLocalService");
    }

    private RealmAsyncTask realmAsyncTask = null;

    private final static String INIT_MSG_DB_ACTION = "init_msg_db";

    public static void  startToInitSkuDbIntent(Context context){

        Intent intent = new Intent(context,PushMessageToLocalService.class);
        intent.setAction(INIT_MSG_DB_ACTION);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PushMessage","service init sku db");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null && intent.getAction().equals(INIT_MSG_DB_ACTION)){
            getDataBaseAllMsgList();

        }

    }

    private void getDataBaseAllMsgList(){

        CommonModel.getInstance().getMessage(new BaseListener(ChatMsg.class) {
            public void responseResult(Object infoObj, Object listObj, int code, boolean status) {

                ArrayList<ChatMsg> tempList = (ArrayList<ChatMsg>) listObj;

                if (tempList != null && tempList.size() > 0){
                    initLocalDataBaseData(tempList);
                } else {
                    Intent intent = new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION);
                    intent.putExtra("result", 1);
                    sendBroadcast(intent);
                    //ToastUtil.show( "没有数据");
                }
            }

            @Override
            public void requestFailed(boolean status, int code, String errorMessage) {
                //hideLoading();
                //ToastUtil.show(errorMessage == null ? Constant.REQUEST_FAILED_STR:errorMessage);
                Intent intent = new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION);
                intent.putExtra("result", -1);
                sendBroadcast(intent);
            }
        });
    }

    public void initLocalDataBaseData(final List<ChatMsg> msglist){

        realmAsyncTask =  Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //删除数据
                //RealmResults<ChatMsg> results = realm.where(ChatMsg.class).findAll();
                //results.deleteAllFromRealm();

                //插入数据
                for (ChatMsg localMsg: msglist){
                    //System.out.println(localSku.getBarcode());
                    String tempTime = TimeConverterUtil.utc2Local(localMsg.time, "yyyy-MM-dd HH:mm:ss");
                    localMsg.dTime = Date.valueOf(tempTime);
                    realm.copyToRealmOrUpdate(localMsg);
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                //sendBroadcast(new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION));
                Intent intent = new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION);
                intent.putExtra("result", 0);
                sendBroadcast(intent);
                //ToastUtil.show("获取聊天数据成功");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                //ToastUtil.show("初始化聊天数据失败，请重试");
                Intent intent = new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION);
                intent.putExtra("result", -1);
                sendBroadcast(intent);
            }
        });
    }
}
