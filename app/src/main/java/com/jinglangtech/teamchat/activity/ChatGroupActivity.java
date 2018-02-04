package com.jinglangtech.teamchat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.jinglangtech.teamchat.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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

    public final static String MESSAGE_INIT_FINISHED_ACTION = "msgInitFinished";


    private class ChatMessagePulledReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result =getIntent().getIntExtra("result", 0);
            hideLoading();
            if (result == -1){
                ToastUtil.show("获取聊天数据出错");
            }
        }
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
                intent.putExtra(Key.ID, chatgroup.name);
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
        IntentFilter intentFilter = new IntentFilter(MESSAGE_INIT_FINISHED_ACTION);
        this.registerReceiver(mReceiver, intentFilter);

        getRoomList(mPageIndex);
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

    private void getRoomList(final int pageIndex){
        //disLoading();
        CommonModel.getInstance().getRoomList(new BaseListener(GroupList.class){

            @Override
            public void responseResult(Object infoObj, Object listObj, int code, boolean status) {

                GroupList tempList = (GroupList) infoObj;

                //hideLoading();
                if (tempList != null && tempList.roomlist.size() > 0){
                    if (mPageIndex == 1){
                        mGroupAdapter.setDataList(tempList.roomlist);
                        mRv.refreshComplete();
                    }else {
                        mGroupAdapter.insertList(tempList.roomlist);

                        if (mPageIndex == mPageCount){
                            RecyclerViewStateUtils.setFooterViewState(mRv, LoadingFooter.State.TheEnd);
                        }else{
                            RecyclerViewStateUtils.setFooterViewState(mRv, LoadingFooter.State.Normal);
                        }

                    }
                    ThreadUtil.runAtBg(new Runnable() {
                        @Override
                        public void run() {
                            PushMessageToLocalService.startToInitSkuDbIntent(ChatGroupActivity.this);// 启动IntentService
                        }
                    });
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
                ToastUtil.show(errorMessage == null ? Constant.REQUEST_FAILED_STR:errorMessage);
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
                ToastUtil.show(errorMessage == null ? Constant.REQUEST_FAILED_STR:errorMessage);
            }
        });
    }
}
