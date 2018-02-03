package com.jinglangtech.teamchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.model.ChatMsg;


public class ChatRoomMsgAdapter extends BasicRecylerAdapter<ChatMsg>{


    public static int TYPE_OTHERS   = 1;
    public static int TYPE_MINE     = 2;
    public ChatRoomMsgAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMsg chatmsg = mList.get(position);
        if (chatmsg == null){
            return TYPE_OTHERS;
        }
        int tempType = TYPE_OTHERS;
        if  (chatmsg.isMine){
            tempType = TYPE_MINE;
        }else{
            tempType = TYPE_OTHERS;
        }
        return tempType;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup viewgroup, int type) {
        View view = null;
        if (type == TYPE_OTHERS ){
            view  = mLayoutinInflater.inflate(R.layout.item_chat_room_others, viewgroup, false);
        }else {
            view  = mLayoutinInflater.inflate(R.layout.item_chat_room_mine, viewgroup, false);
        }
        CommonViewHolder commonViewHolder = new CommonViewHolder(view);
        return commonViewHolder;
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        CommonViewHolder cHolder = (CommonViewHolder) holder;
        ChatMsg info = mList.get(position);

        TextView tvTime = cHolder.obtainView(R.id.tv_time);
        TextView tvMsg = cHolder.obtainView(R.id.tv_last_message);
        TextView tvNickName = cHolder.obtainView(R.id.tv_nickname);
        TextView tvFullName = cHolder.obtainView(R.id.tv_fullname);

        if (info.isMine){

        }else{

        }

        tvTime.setText(info.time);
        tvMsg.setText(info.content);
        tvFullName.setText(info.name);
        if(!TextUtils.isEmpty(info.name)){
            int len = info.name.length();
            tvNickName.setText(info.name.substring(len-1));
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
