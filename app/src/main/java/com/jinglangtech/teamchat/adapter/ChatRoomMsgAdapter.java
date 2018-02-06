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
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.Key;
import com.jinglangtech.teamchat.util.TimeConverterUtil;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class ChatRoomMsgAdapter extends BasicRecylerAdapter<ChatMsg>  {


    public static int TYPE_OTHERS   = 1;
    public static int TYPE_MINE     = 2;

    private Context mCtx;
    private String mId;
    private ChatGroup mGroupInfo;

    public ChatRoomMsgAdapter(Context context) {
        super(context);
        mCtx = context;
        mId =  ConfigUtil.getInstance(mCtx).get(Key.ID, "");
    }


    public void setGroupInfo(ChatGroup group){
        mGroupInfo = group;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMsg chatmsg = mList.get(position);
        if (chatmsg == null){
            return TYPE_OTHERS;
        }
        int tempType = TYPE_OTHERS;
        if  (!TextUtils.isEmpty(mId) && mId.equals(chatmsg.from)){
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

        String tempTime = dateToString(info.dTime);
        if (!TextUtils.isEmpty(tempTime)){
            String displayTime = TimeConverterUtil.getLastTime(tempTime);
            tvTime.setText(displayTime);
        }

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

    private String dateToString(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String str=sdf.format(date);
        return str;
    }

}
