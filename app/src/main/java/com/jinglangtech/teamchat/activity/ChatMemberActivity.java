package com.jinglangtech.teamchat.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jdsjlzx.progressindicator.AVLoadingIndicatorView;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.adapter.BasicRecylerAdapter;
import com.jinglangtech.teamchat.adapter.ChatMemberListAdapter;
import com.jinglangtech.teamchat.adapter.ChatRoomMsgAdapter;
import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.ChatMsg;
import com.jinglangtech.teamchat.model.ChatUser;
import com.jinglangtech.teamchat.model.PageInfo;
import com.jinglangtech.teamchat.network.CommonModel;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ChatMemberActivity extends BaseActivity implements LRecyclerView.LScrollListener{

    @BindView(R.id.eventlist_lv)
    LRecyclerView mRv;
    @BindView(R.id.empty_rel)
    RelativeLayout mEmptyRealtive;
    @BindView(R.id.tv_clear_message)
    TextView mTvClearMsg;

    ChatMemberListAdapter mChatUserAdapter;
    LRecyclerViewAdapter mLRecyclerViewAdapter;

    private int mPageSize = 15;
    private int mPageIndex = 1;
    private int mPageCount = 0;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_chat_member;
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
        mChatUserAdapter = new ChatMemberListAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(layoutManager);
        mRv.setRefreshProgressStyle(AVLoadingIndicatorView.BallSpinFadeLoader);
        mRv.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(this, mChatUserAdapter);
        mRv.setAdapter(mLRecyclerViewAdapter);
        mRv.setLScrollListener(this);
        //mRv.setRefreshing(true);
        mChatUserAdapter.setMyItemOnclickListener(new BasicRecylerAdapter.MyItemOnclickListener() {
            @Override
            public void onItemClick(int position) {
                //ChatGroup eventBean = mChatRoomAdapter.mList.get(position);
                Intent intent = new Intent();
                //intent.putExtra(Key.BEAN, eventBean);
               // intent.setClass(ReportListActivity.this, ReportConfirmActivity.class);
                //startActivityForResult(intent, 1);
            }
        });
        mTvClearMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void loadData() {
        test();
    }

    public void test(){
        List<ChatUser> tempList = new ArrayList<ChatUser>();
        ChatUser msg1 = new ChatUser();
        msg1.name = "曹海金";

        ChatUser msg2 = new ChatUser();
        msg2.name = "李丽丽";

        tempList.add(msg1);
        tempList.add(msg2);
        mChatUserAdapter.setDataList(tempList);
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
        CommonModel.getInstance().getMessage(new BaseListener(ChatUser.class){

            @Override
            public void responseListResult(Object infoObj, Object listObj, PageInfo pageInfo, int code, boolean status) {

                List<ChatUser> tempList = (List<ChatUser>) listObj;

                if (pageInfo != null){
                    mPageIndex = pageInfo.index;
                    mPageCount = pageInfo.count;
                    mPageSize = pageInfo.size;
                }

                if (tempList != null && tempList.size() > 0){
                    if (mPageIndex == 1){
                        mChatUserAdapter.setDataList(tempList);
                        mRv.refreshComplete();
                    }else {
                        mChatUserAdapter.insertList(tempList);

                        if (mPageIndex == mPageCount){
                            RecyclerViewStateUtils.setFooterViewState(mRv, LoadingFooter.State.TheEnd);
                        }else{
                            RecyclerViewStateUtils.setFooterViewState(mRv, LoadingFooter.State.Normal);
                        }

                    }
                }else{
                    mRv.refreshComplete();
                }

                if (mChatUserAdapter.mList.size() > 0){
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
