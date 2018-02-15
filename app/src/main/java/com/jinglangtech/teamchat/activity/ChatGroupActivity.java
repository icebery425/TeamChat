package com.jinglangtech.teamchat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.jdsjlzx.progressindicator.AVLoadingIndicatorView;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.adapter.BasicRecylerAdapter;
import com.jinglangtech.teamchat.adapter.ChatGroupListAdapter;
import com.jinglangtech.teamchat.dbmanager.DBFactory;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.model.ChatMsg;
import com.jinglangtech.teamchat.model.GroupList;
import com.jinglangtech.teamchat.model.PageInfo;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.service.PushMessageToLocalService;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.Key;
import com.jinglangtech.teamchat.util.ThreadUtil;
import com.jinglangtech.teamchat.util.TimeConverterUtil;
import com.jinglangtech.teamchat.util.ToastUtil;
import com.jinglangtech.teamchat.util.ToastUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import io.realm.Realm;

public class ChatGroupActivity extends BaseActivity implements LRecyclerView.LScrollListener{

    @BindView(R.id.eventlist_lv)
    LRecyclerView mRv;
    @BindView(R.id.empty_rel)
    RelativeLayout mEmptyRealtive;
    @BindView(R.id.add_event_rel)
    RelativeLayout mLayoutSetting;

    ChatGroupListAdapter mGroupAdapter;
    LRecyclerViewAdapter mLRecyclerViewAdapter;

    private int mPageSize = 15;
    private int mPageIndex = 1;
    private int mPageCount = 0;
    private List<ChatGroup> mRoomList = new ArrayList<ChatGroup>();

    public final static String MESSAGE_INIT_FINISHED_ACTION = "msgInitFinished";
    public final static String GROUP_INIT_FINISHED_ACTION = "groupInitFinished";
    public final static String RECEIVE_MSG_NOTIFY_ACTION = "receiveMsgNotify";
    public final static String RECEIVE_MSG_CUSTOM_ACTION = "receiveMsgCustom";
    public final static String CLEAR_MSG_ACTION = "clearMsg";

    public final static String REFRESH_ROOM_MSG_ACTION = "refreshRoomMsg";




    private class ChatMessagePulledReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result =getIntent().getIntExtra("result", 0);
            hideLoading();
            String actionStr = intent.getAction();
            if (actionStr.equals(MESSAGE_INIT_FINISHED_ACTION)){
                String jpushRoomId = intent.getStringExtra("jpushRoomId");
                if (result == -1){
                    ToastUtils.showToast(ChatGroupActivity.this, "获取聊天数据出错");
                }else{
                    sendNotifyToRefreshChatRoom(jpushRoomId);
                    getRoomLastMsg();
                }
            }else if (actionStr.equals(GROUP_INIT_FINISHED_ACTION)){
                if (result == -1){
                    ToastUtils.showToast(ChatGroupActivity.this, "获取群组数据出错");
                }else {
                    PushMessageToLocalService.startToInitSkuDbIntent(ChatGroupActivity.this, "");// 启动IntentService
                }
            }else if (actionStr.equals(RECEIVE_MSG_CUSTOM_ACTION)){
                String tempRoomId2= intent.getStringExtra("jpushRoomId");
                PushMessageToLocalService.startToInitSkuDbIntent(ChatGroupActivity.this,tempRoomId2);// 启动IntentService
            }else if (actionStr.equals(Constant.BROADCAST_UPDATE_GROUP_INFO)){
                String roomId = intent.getStringExtra("roomId");
                getOneRoomLastMsg(roomId);
            }else if (actionStr.equals(CLEAR_MSG_ACTION)){
                refreshRoomList();
            }

        }
    }

    private void sendNotifyToRefreshChatRoom(String roomId){
        Intent intent = new Intent(REFRESH_ROOM_MSG_ACTION);
        intent.putExtra("jpushRoomId", roomId);
        this.sendBroadcast(intent);
    }

    private void getRoomLastMsg(){
        ChatMsg maxOne;
        long unRead = 0;
        if (mRoomList != null && mRoomList.size() > 0){
            for (ChatGroup group: mRoomList){
                maxOne = (ChatMsg) DBFactory.getDBInstance().findMaxDateOne("roomid", group._id);
                if (maxOne != null){
                    Log.e("getRoomLastMsg", "#### TIME:" + maxOne.dTime.toString());
                    Log.e("getRoomLastMsg", "#### CONTENT:" + maxOne.content);
                    String tempTime = dateToString(maxOne.dTime);
                    String from = maxOne.from.toString();
                    String content = maxOne.content.toString();
                    group.msg = content;
                    group.time= tempTime;
                }
                unRead = DBFactory.getDBInstance().findUnread("roomid", group._id);
                group.unread = unRead;
                Log.e("getRoomLastMsg", "#### UNREAD:" + unRead);
            }
        }
        ThreadUtil.runAtMain(new Runnable() {
            @Override
            public void run() {
                mGroupAdapter.setDataList(mRoomList);
            }
        });

    }

    private void getOneRoomLastMsg(String roomId){
        ChatMsg maxOne;
        long unRead = 0;
        if (TextUtils.isEmpty(roomId)){
            Log.d("", "roomId为空");
            return;
        }
        int index = 0;
        if (mRoomList != null && mRoomList.size() > 0){
            for (ChatGroup group: mRoomList){
                if (group._id.equals(roomId)){
                    maxOne = (ChatMsg) DBFactory.getDBInstance().findMaxDateOne("roomid", group._id);
                    if (maxOne != null){
                        Log.e("getRoomLastMsg", "#### TIME:" + maxOne.dTime.toString());
                        Log.e("getRoomLastMsg", "#### CONTENT:" + maxOne.content);
                        String tempTime = dateToString(maxOne.dTime);
                        String from = maxOne.from.toString();
                        String content = maxOne.content.toString();
                        group.msg = content;
                        group.time= tempTime;
                    }
                    unRead = DBFactory.getDBInstance().findUnread("roomid", group._id);
                    group.unread = unRead;
                    Log.e("getRoomLastMsg", "#### update room index:" + index + ", update room  name: " + group.name);

                    mGroupAdapter.update(index);

                    break;
                }
                index++;
            }
        }

    }

    private String dateToString(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String str=sdf.format(date);
        return str;
    }


    private ChatMessagePulledReceiver mReceiver;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_chat_group;
    }

    @Override
    public void initVariables() {

    }
    @Override
    public void initStatusColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.title_bg));
        }
    }

    @Override
    public void initViews() {
        mGroupAdapter = new ChatGroupListAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(layoutManager);
        mRv.setRefreshProgressStyle(AVLoadingIndicatorView.BallSpinFadeLoader);
        mRv.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(this, mGroupAdapter);
        mRv.setAdapter(mLRecyclerViewAdapter);
        mRv.setLScrollListener(this);
        mRv.setRefreshing(true);
        mGroupAdapter.setMyItemOnclickListener(new BasicRecylerAdapter.MyItemOnclickListener() {
            @Override
            public void onItemClick(int position) {
                ChatGroup chatgroup = mGroupAdapter.mList.get(position);
                Intent intent = new Intent();
                intent.putExtra(Key.ID, chatgroup._id);
                intent.putExtra(Key.ROOM_NAME, chatgroup.name);
                intent.setClass(ChatGroupActivity.this, ChatRoomActivity.class);
                startActivity(intent);
            }
        });

        mLayoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //intent.putExtra(Key.BEAN, eventBean);
                intent.setClass(ChatGroupActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void loadData() {
        //test();
        mReceiver = new ChatMessagePulledReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_INIT_FINISHED_ACTION);
        intentFilter.addAction(GROUP_INIT_FINISHED_ACTION);
        intentFilter.addAction(RECEIVE_MSG_CUSTOM_ACTION);
        intentFilter.addAction(Constant.BROADCAST_UPDATE_GROUP_INFO);
        intentFilter.addAction(CLEAR_MSG_ACTION);
        this.registerReceiver(mReceiver, intentFilter);

        getRoomList(mPageIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

    public void test(){
        List<ChatGroup> tempList = new ArrayList<ChatGroup>();
        ChatGroup group1 = new ChatGroup();
        group1.name = "TeamChat项目开发组";
        group1.unread= 2;
        group1.time = "上午 10:30";
        group1.msg = "这个说明应该更好理解一些";
        ChatGroup group2 = new ChatGroup();
        group2.name = "一家亲";
        group2.unread= 105;
        group2.time = "昨天";
        group2.msg = "到时一起回家啊";
        tempList.add(group1);
        tempList.add(group2);
        mGroupAdapter.setDataList(tempList);
        mRv.refreshComplete();
    }
    @Override
    public void onRefresh() {

    }

    @Override
    public void onScrollUp() {

    }

    @Override
    public void onScrollDown() {

    }

    @Override
    public void onBottom() {

    }

    @Override
    public void onScrolled(int distanceX, int distanceY) {

    }

    private void refreshRoomList(){
        long unRead = 0;
        if (mRoomList != null && mRoomList.size() > 0){
            for (ChatGroup group: mRoomList){
                group.msg = "";
                group.time= "";
                group.unread = 0;
            }
        }

        mGroupAdapter.setDataList(mRoomList);

    }


    private void getRoomList(final int pageIndex){
        //disLoading();
        CommonModel.getInstance().getRoomList(new BaseListener(GroupList.class){

            @Override
            public void responseResult(Object infoObj, Object listObj, int code, boolean status) {

                GroupList tempList = (GroupList) infoObj;

                //hideLoading();
                if (tempList != null && tempList.roomlist.size() > 0){
                    mRoomList = tempList.roomlist;
                    if (mPageIndex == 1){
                        mGroupAdapter.setDataList(mRoomList);
                        mRv.refreshComplete();
                    }else {
                        mGroupAdapter.insertList(mRoomList);

                        if (mPageIndex == mPageCount){
                            RecyclerViewStateUtils.setFooterViewState(mRv, LoadingFooter.State.TheEnd);
                        }else{
                            RecyclerViewStateUtils.setFooterViewState(mRv, LoadingFooter.State.Normal);
                        }

                    }

                    initLocalRoomDataBaseData(tempList.roomlist);

                    disLoading("正在加载聊天数据，请稍候");

                }else{
                    mRv.refreshComplete();
                }

                if (mGroupAdapter.mList.size() > 0){
                    if (mEmptyRealtive.getVisibility() == View.VISIBLE){
                        mEmptyRealtive.setVisibility(View.GONE);
                    }
                }else{
                    if (mEmptyRealtive.getVisibility() == View.GONE){
                        mEmptyRealtive.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void requestFailed(boolean status, int code, String errorMessage) {
                //hideLoading();
                ToastUtils.showToast(ChatGroupActivity.this,errorMessage == null ? Constant.REQUEST_FAILED_STR:errorMessage);
                mRv.refreshComplete();
            }
        });
    }

    public void getAllMessages(){
        CommonModel.getInstance().getMessage(new BaseListener(ChatMsg.class){
            @Override
            public void responseResult(Object infoObj, Object listObj, int code, boolean status) {

                ArrayList<ChatMsg> tempList = (ArrayList<ChatMsg>) listObj;

                //hideLoading();
                if (tempList != null && tempList.size() > 0){

                }else{

                }
            }

            @Override
            public void requestFailed(boolean status, int code, String errorMessage) {
                //hideLoading();
                ToastUtils.showToast(ChatGroupActivity.this,errorMessage == null ? Constant.REQUEST_FAILED_STR:errorMessage);
            }
        });
    }

    public void initLocalRoomDataBaseData(final List<ChatGroup> msglist){

        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //删除数据
                //RealmResults<ChatMsg> results = realm.where(ChatMsg.class).findAll();
                //results.deleteAllFromRealm();

                //插入数据
                try {
                    for (ChatGroup localGroup : msglist) {
                        realm.copyToRealmOrUpdate(localGroup);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                //sendBroadcast(new Intent(ChatGroupActivity.MESSAGE_INIT_FINISHED_ACTION));
                Intent intent = new Intent(ChatGroupActivity.GROUP_INIT_FINISHED_ACTION);
                intent.putExtra("result", 0);
                sendBroadcast(intent);
                //ToastUtils.showToast("获取聊天数据成功");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                //ToastUtils.showToast("初始化聊天数据失败，请重试");
                Intent intent = new Intent(ChatGroupActivity.GROUP_INIT_FINISHED_ACTION);
                intent.putExtra("result", -1);
                sendBroadcast(intent);
            }
        });
    }
}
