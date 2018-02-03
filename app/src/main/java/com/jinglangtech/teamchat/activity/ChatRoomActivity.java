package com.jinglangtech.teamchat.activity;

import android.content.Intent;
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
import com.jinglangtech.teamchat.adapter.ChatRoomMsgAdapter;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.model.ChatMsg;
import com.jinglangtech.teamchat.model.PageInfo;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ChatRoomActivity extends BaseActivity implements LRecyclerView.LScrollListener{

    @BindView(R.id.eventlist_lv)
    LRecyclerView mRv;
    @BindView(R.id.empty_rel)
    RelativeLayout mEmptyRealtive;

    ChatRoomMsgAdapter mChatRoomAdapter;
    LRecyclerViewAdapter mLRecyclerViewAdapter;

    private int mPageSize = 15;
    private int mPageIndex = 1;
    private int mPageCount = 0;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_chat_room;
    }

    @Override
    public void initVariables() {

    }

    @Override
    public void initViews() {
        mChatRoomAdapter = new ChatRoomMsgAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(layoutManager);
        mRv.setRefreshProgressStyle(AVLoadingIndicatorView.BallSpinFadeLoader);
        mRv.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(this, mChatRoomAdapter);
        mRv.setAdapter(mLRecyclerViewAdapter);
        mRv.setLScrollListener(this);
        //mRv.setRefreshing(true);
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
    }

    @Override
    public void loadData() {
        test();
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

    private void getEventList(final int pageIndex){
        CommonModel.getInstance().getMessage(new BaseListener(ChatMsg.class){

            @Override
            public void responseListResult(Object infoObj, Object listObj, PageInfo pageInfo, int code, boolean status) {

                List<ChatMsg> tempList = (List<ChatMsg>) listObj;

                if (pageInfo != null){
                    mPageIndex = pageInfo.index;
                    mPageCount = pageInfo.count;
                    mPageSize = pageInfo.size;
                }

                if (tempList != null && tempList.size() > 0){
                    if (mPageIndex == 1){
                        mChatRoomAdapter.setDataList(tempList);
                        mRv.refreshComplete();
                    }else {
                        mChatRoomAdapter.insertList(tempList);

                        if (mPageIndex == mPageCount){
                            RecyclerViewStateUtils.setFooterViewState(mRv, LoadingFooter.State.TheEnd);
                        }else{
                            RecyclerViewStateUtils.setFooterViewState(mRv, LoadingFooter.State.Normal);
                        }

                    }
                }else{
                    mRv.refreshComplete();
                }

                if (mChatRoomAdapter.mList.size() > 0){
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
                ToastUtil.show(errorMessage == null ? Constant.REQUEST_FAILED_STR:errorMessage);
                mRv.refreshComplete();
            }
        });
    }
}
