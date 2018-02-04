package com.jinglangtech.teamchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.model.ChatUser;


public class ChatMemberListAdapter extends BasicRecylerAdapter<ChatUser>{


    public ChatMemberListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_user;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        CommonViewHolder cHolder = (CommonViewHolder) holder;
        TextView tvNickTime = cHolder.obtainView(R.id.tv_nickname);
        TextView tvName = cHolder.obtainView(R.id.tv_name);


        ChatUser info = mList.get(position);
        tvName.setText(info.name);
        if (!TextUtils.isEmpty(info.name)){
            int len = info.name.length();
            tvNickTime.setText(info.name.substring(len-1));
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
