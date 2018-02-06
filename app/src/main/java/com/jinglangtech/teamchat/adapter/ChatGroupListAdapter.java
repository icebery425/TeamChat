package com.jinglangtech.teamchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.ImgLoadUtil;
import com.jinglangtech.teamchat.util.TimeConverterUtil;


public class ChatGroupListAdapter extends BasicRecylerAdapter<ChatGroup>{


    public ChatGroupListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_group;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        CommonViewHolder cHolder = (CommonViewHolder) holder;
        TextView tvTime = cHolder.obtainView(R.id.tv_time);
        TextView tvMsg = cHolder.obtainView(R.id.tv_last_message);
        TextView tvName = cHolder.obtainView(R.id.tv_name);

        RelativeLayout mLayoutUnread=(RelativeLayout)cHolder.obtainView(R.id.layout_unread);
        TextView mTvUnread  = (TextView)cHolder.obtainView(R.id.tv_unread_count);


        ChatGroup info = mList.get(position);
        if (!TextUtils.isEmpty(info.time)){
            String displayTime = TimeConverterUtil.getLastTime(info.time);
            tvTime.setText(displayTime);
        }

        tvMsg.setText(info.msg);
        tvName.setText(info.name);

        if (info.unread <= 0){
            mLayoutUnread.setVisibility(View.INVISIBLE);
        }else {
            mLayoutUnread.setVisibility(View.VISIBLE);
            if (info.unread > 99){
                mTvUnread.setText("99+");
            }else{
                mTvUnread.setText(info.unread + "");
            }
        }


        cHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (myItemOnclickListener != null){
                   myItemOnclickListener.onItemClick(position);
               }
            }
        });
    }
}
