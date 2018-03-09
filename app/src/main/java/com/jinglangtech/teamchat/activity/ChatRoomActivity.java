package com.jinglangtech.teamchat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jdsjlzx.progressindicator.AVLoadingIndicatorView;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.adapter.BasicRecylerAdapter;
import com.jinglangtech.teamchat.adapter.ChatGroupListAdapter;
import com.jinglangtech.teamchat.adapter.ChatMemberListAdapter;
import com.jinglangtech.teamchat.adapter.ChatRoomMsgAdapter;
import com.jinglangtech.teamchat.dbmanager.DBFactory;
import com.jinglangtech.teamchat.dbmanager.RealmDbManger;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.model.ChatMsg;
import com.jinglangtech.teamchat.model.ChatUser;
import com.jinglangtech.teamchat.model.GroupList;
import com.jinglangtech.teamchat.model.PageInfo;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.service.PushMessageToLocalService;
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.Key;
import com.jinglangtech.teamchat.util.TimeConverterUtil;
import com.jinglangtech.teamchat.util.ToastUtil;
import com.jinglangtech.teamchat.util.ToastUtils;
import com.jinglangtech.teamchat.util.UuidUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmList;

public class ChatRoomActivity extends BaseActivity implements LRecyclerView.LScrollListener, TextView.OnEditorActionListener{

    @BindView(R.id.tv_room_name)
    TextView mTvRoomName;
    @BindView(R.id.eventlist_lv)
    LRecyclerView mRv;
    @BindView(R.id.empty_rel)
    RelativeLayout mEmptyRealtive;
    @BindView(R.id.add_event_rel)
    RelativeLayout mLayoutChatInfo;
    @BindView(R.id.et_input)
    EditText mEtInput;

    String mId;
    String mRoomId;
    String mRoomName;
    String mCurrentMsg;
    List<ChatMsg> mChatMsgList = new ArrayList<ChatMsg>();

    ChatRoomMsgAdapter mChatRoomAdapter;
    LRecyclerViewAdapter mLRecyclerViewAdapter;
    LinearLayoutManager mRecyclerManager;
    ChatGroup mGroupInfo;

    private int mPageSize = 15;
    private int mPageIndex = 1;
    private int mPageCount = 0;

    private RefreshMsgReceiver mReceiver;

    private class RefreshMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();
            if (actionStr.equals(ChatGroupActivity.REFRESH_ROOM_MSG_ACTION)){
                String roomId = intent.getStringExtra("jpushRoomId");
                if ((!TextUtils.isEmpty(roomId) && roomId.equals(mRoomId)) || TextUtils.isEmpty(roomId)){
                    getGroupInfo();
                    getLocalMessage();
                }

            }

        }
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_chat_room;
    }

    @Override
    public void initVariables() {
        mRoomId = this.getIntent().getStringExtra(Key.ID);
        mRoomName = this.getIntent().getStringExtra(Key.ROOM_NAME);
        mId = ConfigUtil.getInstance(this).get(Key.ID, "");

    }

    @Override
    public void finishActivity(View v) {
        updateGroupInfoBrodcast();
        super.finishActivity(v);

    }

    // 捕获返回键的方法2
    @Override
    public void onBackPressed() {
        Log.d("", "onBackPressed()");
        updateGroupInfoBrodcast();
        super.onBackPressed();
    }

    @Override
    public void initStatusColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.title_bg));
        }
    }

    @Override
    public void initViews() {
        mTvRoomName.setText(mRoomName);
        mChatRoomAdapter = new ChatRoomMsgAdapter(this);
        mRecyclerManager = new LinearLayoutManager(this);
        mRecyclerManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(mRecyclerManager);
        mRv.setRefreshProgressStyle(AVLoadingIndicatorView.BallSpinFadeLoader);
        mRv.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(this, mChatRoomAdapter);
        mRv.setAdapter(mLRecyclerViewAdapter);
        mRv.setLScrollListener(this);
        mRv.setPullRefreshEnabled(false);
        //mRv.setRefreshing(true);
        mChatRoomAdapter.setGroupInfo(mGroupInfo);
        mChatRoomAdapter.setMyItemOnclickListener(new BasicRecylerAdapter.MyItemOnclickListener() {
            @Override
            public void onItemClick(int position) {
                //ChatGroup eventBean = mChatRoomAdapter.mList.get(position);
                Intent intent = new Intent();
                //intent.putExtra(Key.BEAN, eventBean);
               // intent.setClass(ReportListActivity.this, ReportConfirmActivity.class);
                //startActivityForResult(intent, 1);
            }
        });
        mLayoutChatInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Key.ID, mRoomId);

                intent.setClass(ChatRoomActivity.this, ChatMemberActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        mEtInput.setOnEditorActionListener(this);
    }

    public void MoveToPosition(int n) {
        //mRecyclerManager.scrollToPositionWithOffset(n, 0);
        //mRv.smoothScrollToPosition(n);
        mRv.scrollToPosition(n);
        //mRecyclerManager.setStackFromEnd(true);
    }

    @Override
    public void loadData() {
        //test();
        getGroupInfo();
        getLocalMessage();

        mReceiver = new RefreshMsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ChatGroupActivity.REFRESH_ROOM_MSG_ACTION);
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        doWhichOperation(actionId);
        Log.e("BALLACK", "event: " + event);
        Log.e("BALLACK", "v.getImeActionId(): " + v.getImeActionId());
        Log.e("BALLACK", "v.getImeOptions(): " + v.getImeOptions());
        return true;
    }

    private void doWhichOperation(int actionId) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
            case EditorInfo.IME_ACTION_GO:
            case EditorInfo.IME_ACTION_NEXT:
            case EditorInfo.IME_ACTION_SEND:
                Log.e("BALLACK", "IME_ACTION_SEND");
                sendMessage();
                break;
            default:
                break;
        }
    }

    private void sendMessage(){
        if (TextUtils.isEmpty(mRoomId)){
            ToastUtils.showToast(ChatRoomActivity.this,"聊天室ID为空");
            return;
        }
        mCurrentMsg = mEtInput.getText().toString().trim();
        if (mCurrentMsg.length() <= 0){
            ToastUtils.showToast(ChatRoomActivity.this,"发送内容不能为空");
            return;
        }
        CommonModel.getInstance().sendToRoom(mRoomId,mCurrentMsg, new BaseListener(String.class){

            @Override
            public void responseResult(Object infoObj, Object listObj, int code, boolean status) {
                super.responseResult(infoObj, listObj, code, status);
                mEtInput.setText("");
                ChatMsg msg1 = new ChatMsg();
                String name = ConfigUtil.getInstance(ChatRoomActivity.this).get(Key.USER_NAME, "");
                msg1.name = name;
                msg1.content = mCurrentMsg;
                msg1.time = TimeConverterUtil.getCurrentUTCTime();
                msg1.dTime= new Date();
                msg1.isMine = true;
                msg1.isread = true;
                msg1.isSend = true;
                msg1._id =  UuidUtil.get24UUID();
                msg1.roomid = mRoomId;
                msg1.from = ConfigUtil.getInstance(ChatRoomActivity.this).get(Key.ID, "");
                mChatMsgList.add(msg1);
                mChatRoomAdapter.setDataList(mChatMsgList);
                MoveToPosition(mChatMsgList.size());
                RealmDbManger.getRealmInstance().insertOneElement(msg1);
                updateGroupInfoBrodcast();
            }

            @Override
            public void requestFailed(boolean status, int code, String errorMessage) {
                super.requestFailed(status, code, errorMessage);

                ChatMsg msg1 = new ChatMsg();
                String name = ConfigUtil.getInstance(ChatRoomActivity.this).get(Key.USER_NAME, "");
                msg1.name = name;
                msg1.content = mCurrentMsg;
                msg1.time = TimeConverterUtil.getCurrentUTCTime();
                msg1.dTime= new Date();
                msg1.isMine = true;
                msg1.isread = true;
                msg1.isSend = false;
                msg1._id =  UuidUtil.get24UUID();
                msg1.roomid = mRoomId;
                msg1.from = ConfigUtil.getInstance(ChatRoomActivity.this).get(Key.ID, "");
                mChatMsgList.add(msg1);
                mChatRoomAdapter.setDataList(mChatMsgList);
                MoveToPosition(mChatMsgList.size());
                RealmDbManger.getRealmInstance().insertOneElement(msg1);
                updateGroupInfoBrodcast();
                ToastUtils.showToast(ChatRoomActivity.this,"发送消息失败");
            }
        });
    }

    private void updateGroupInfoBrodcast(){
        Intent intent = new Intent(Constant.BROADCAST_UPDATE_GROUP_INFO);
        intent.putExtra("roomId", mRoomId);
        this.sendBroadcast(intent);
    }

    private void getGroupInfo(){
        mGroupInfo =  DBFactory.getDBInstance().findGroupInfoWithRoomId(mRoomId);
    }

    private void getLocalMessage(){
        List<ChatMsg> msgList =  DBFactory.getDBInstance().findLocalMsgWithRoomId(mRoomId);

        //
        Realm.getDefaultInstance().beginTransaction();
        if (msgList != null && msgList.size() > 0){
            for (ChatMsg cmsg: msgList){
                cmsg.isread = true;
            }
        }
        Realm.getDefaultInstance().commitTransaction();


        mChatMsgList.clear();
        copyDbDateToTemp(msgList);
        //mChatMsgList.addAll(msgList);
        setFullName();
        mChatRoomAdapter.setDataList(mChatMsgList);
        MoveToPosition(mChatMsgList.size());
        mRv.refreshComplete();
    }

    private void copyDbDateToTemp(List<ChatMsg> msglist){
        ChatMsg chatmsg = null;
        ArrayList<ChatMsg> arrayList = new ArrayList<>(1);//初始化长度一，大多数商品只有一个
        for (ChatMsg localMsg : msglist){
            chatmsg = createMsg(localMsg);
            mChatMsgList.add(chatmsg);
        }
    }

    private  ChatMsg createMsg(ChatMsg localmsg){
        if (localmsg == null){
            return null;
        }
        ChatMsg newMsg = new ChatMsg();
        newMsg.name = localmsg.name;
        newMsg._id = localmsg._id;
        newMsg.from= localmsg.from;
        newMsg.content=localmsg.content;
        newMsg.dTime=localmsg.dTime;
        newMsg.time=localmsg.time;
        newMsg.isMine=localmsg.isread;
        newMsg.roomid=localmsg.roomid;
        return newMsg;
    }


    private void setFullName(){
        if (mGroupInfo == null ||mGroupInfo.group == null || mGroupInfo.group.size() <= 0){
            Log.d("", "群组信息为空");
            return;
        }

        if (mChatMsgList == null || mChatMsgList.size() <= 0){
            Log.d("", "此群消息空");
            return;
        }

        RealmList<ChatUser> tempGroup = mGroupInfo.group;
        for (ChatUser chatuser: tempGroup){
            for (ChatMsg chatmsg: mChatMsgList){
                if (chatuser._id.equals(chatmsg.from)){
                    chatmsg.name = chatuser.name;
                }

            }
        }
    }


    public void test(){
        List<ChatMsg> tempList = new ArrayList<ChatMsg>();
        ChatMsg msg1 = new ChatMsg();
        msg1.name = "曹海金";
        msg1.content = "你去过你就会知道";
        msg1.time = "上午 10:30";
        msg1.isMine = false;

        ChatMsg msg2 = new ChatMsg();
        msg2.name = "李丽丽";
        msg2.content = "好像是";
        msg2.time = "上午 10:31";
        msg2.isMine = false;

        tempList.add(msg1);
        tempList.add(msg2);
        mChatRoomAdapter.setDataList(tempList);
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

}
