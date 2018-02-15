package com.jinglangtech.teamchat.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;


import com.jinglangtech.teamchat.activity.ChatGroupActivity;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.ChatMsg;
import com.jinglangtech.teamchat.model.ChatMsgWraper;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.TimeConverterUtil;
import com.jinglangtech.teamchat.util.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmAsyncTask;

import static android.content.ContentValues.TAG;

public class PushMessageToLocalService extends IntentService {

    public static String TAG = "PushMessageToLocalService";
    public PushMessageToLocalService() {
        super("PushMessageToLocalService");
    }

    private RealmAsyncTask realmAsyncTask = null;

    private final static String INIT_MSG_DB_ACTION = "init_msg_db";

    public static void  startToInitSkuDbIntent(Context context, String roomId){

        Intent intent = new Intent(context,PushMessageToLocalService.class);
        intent.setAction(INIT_MSG_DB_ACTION);
        intent.putExtra("jpushRoomId", roomId);
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
            String jpushRoomId = intent.getStringExtra("jpushRoomId");
            getDataBaseAllMsgList(jpushRoomId);
        }

    }

    private void getDataBaseAllMsgList(final String roomId){

        CommonModel.getInstance().getMessage(new BaseListener(ChatMsgWraper.class) {
            public void responseResult(Object infoObj, Object listObj, int code, boolean status) {

                ChatMsgWraper msgWraper = (ChatMsgWraper)infoObj;
                if (msgWraper == null){
                    Intent intent = new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION);
                    intent.putExtra("result", 1);
                    intent.putExtra("jpushRoomId", roomId);
                    sendBroadcast(intent);
                    return;
                }
                ArrayList<ChatMsg> tempList = (ArrayList<ChatMsg>) msgWraper.msglist;

                if (tempList != null && tempList.size() > 0){
                    initLocalDataBaseData(tempList, roomId);
                } else {
                    Intent intent = new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION);
                    intent.putExtra("result", 1);
                    intent.putExtra("jpushRoomId", roomId);
                    sendBroadcast(intent);
                    //ToastUtils.showToast( "没有数据");
                }
            }

            @Override
            public void requestFailed(boolean status, int code, String errorMessage) {
                //hideLoading();
                //ToastUtils.showToast(errorMessage == null ? Constant.REQUEST_FAILED_STR:errorMessage);
                Intent intent = new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION);
                intent.putExtra("result", -1);
                sendBroadcast(intent);
            }
        });
    }

    public void initLocalDataBaseData(final List<ChatMsg> msglist, final String roomId){

        realmAsyncTask =  Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //删除数据
                //RealmResults<ChatMsg> results = realm.where(ChatMsg.class).findAll();
                //results.deleteAllFromRealm();
                String myString = "1900-01-01 01:01:01";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                Date Maxdate = null;
                String MaxStrDate = null;
                try {
                    Maxdate = sdf.parse(myString);
                } catch (Exception e) {
                }
                int compareValue = 0;
                //插入数据
                try {
                    for (ChatMsg localMsg : msglist) {
                        //System.out.println(localSku.getBarcode());
                        String tempTime = TimeConverterUtil.utc2Local(localMsg.time, "yyyy-MM-dd HH:mm:ss");
                        //localMsg.dTime = Date.valueOf(tempTime);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = dateFormat.parse(tempTime);
                            localMsg.dTime = date;

                            compareValue = date.compareTo(Maxdate);
                            if (compareValue >= 0){
                                Maxdate = date;
                                MaxStrDate = localMsg.time;
                            }
                            //System.out.println(date.toLocaleString().split(" ")[0]);//切割掉不要的时分秒数据
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        realm.copyToRealmOrUpdate(localMsg);
                    }

                    Log.d(TAG, "MAX DATE: " + MaxStrDate);
                    if (!TextUtils.isEmpty(MaxStrDate)){
                        setMsgReadStatus(MaxStrDate);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                //sendBroadcast(new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION));
                Intent intent = new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION);
                intent.putExtra("result", 0);
                intent.putExtra("jpushRoomId", roomId);
                sendBroadcast(intent);
                //ToastUtils.showToast("获取聊天数据成功");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                //ToastUtils.showToast("初始化聊天数据失败，请重试");
                Intent intent = new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION);
                intent.putExtra("result", -1);
                sendBroadcast(intent);
            }
        });
    }

    private void setMsgReadStatus(String time){
        CommonModel.getInstance().readMessage(time, new BaseListener(String.class){

            @Override
            public void responseResult(Object infoObj, Object listObj, int code, boolean status) {
                super.responseResult(infoObj, listObj, code, status);
                Log.d(TAG,"readMessage SUCCEED");
            }

            @Override
            public void requestFailed(boolean status, int code, String errorMessage) {
                super.requestFailed(status, code, errorMessage);
                Log.d(TAG,"readMessage FAILED");
            }
        });
    }
}
