package com.jinglangtech.teamchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.util.Constant;
import com.jinglangtech.teamchat.util.ImgLoadUtil;


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
        ImageView tvAvatar = cHolder.obtainView(R.id.img_avatar);


        ChatGroup info = mList.get(position);
        tvTime.setText(info.time);
        tvMsg.setText(info.msg);


        ImgLoadUtil.displayPic(R.mipmap.ic_avatar_middle, info.avatar, tvAvatar);

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
