package com.jinglangtech.teamchat.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;
import com.jinglangtech.teamchat.model.ChatGroup;
import com.jinglangtech.teamchat.model.ChatMsg;
import com.jinglangtech.teamchat.util.ConfigUtil;
import com.jinglangtech.teamchat.util.Key;
import com.jinglangtech.teamchat.util.TextUtil;
import com.jinglangtech.teamchat.util.TimeConverterUtil;
import com.jinglangtech.teamchat.widget.CustomDialog;
import com.jinglangtech.teamchat.widget.MenuDialog;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class ChatRoomMsgAdapter extends BasicRecylerAdapter<ChatMsg>  {


    public static int TYPE_OTHERS   = 1;
    public static int TYPE_MINE     = 2;

    private final static long MinuteIntervel = 5* 60 * 1000;// 如果两条聊天记录之间的间距相差5分钟，则显示后一条的聊天记录

    private Context mCtx;
    private String mId;
    private ChatGroup mGroupInfo;

    public interface IReSendLister{
        public void reRend(int postion);
    }

    private IReSendLister mResendLister = null;

    public void setResendLister(IReSendLister lister){
        mResendLister = lister;
    }

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
            chatmsg.isMine = true;
        }else{
            tempType = TYPE_OTHERS;
            chatmsg.isMine = false;
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
        ImageView ivSendFailed = cHolder.obtainView(R.id.iv_send_failed);


        if (position == 0){
            tvTime.setVisibility(View.VISIBLE);
        }else{
            ChatMsg preMsg = mList.get(position-1);
            long timeStamp = info.dTime.getTime()-preMsg.dTime.getTime();
            if (timeStamp > MinuteIntervel){
                tvTime.setVisibility(View.VISIBLE);
            }else{
                tvTime.setVisibility(View.GONE);
            }
        }
        String tempTime = dateToString(info.dTime);
        if (!TextUtils.isEmpty(tempTime)){
            String displayTime = TimeConverterUtil.getLastTime(tempTime);
            tvTime.setText(displayTime);
        }

        tvMsg.setText(info.content);
        tvFullName.setText(info.name);
        if(!TextUtils.isEmpty(info.name)){
            int len = info.name.length();
            tvNickName.setText(info.name.substring(0, 1));
        }

        tvMsg.setTag(position);
        tvMsg.setOnLongClickListener(mLongClickListener);


        if (info.isSend)
        {
            ivSendFailed.setVisibility(View.INVISIBLE);
            if (info.isMine){
                ProgressBar sendProgress = cHolder.obtainView(R.id.progressBar);
                sendProgress.setVisibility(View.GONE);
            }
        }else{
            if (info.isMine){
                ProgressBar sendProgress = cHolder.obtainView(R.id.progressBar);
                if (info.isSending){
                    sendProgress.setVisibility(View.VISIBLE);
                    ivSendFailed.setVisibility(View.GONE);
                }else {
                    sendProgress.setVisibility(View.GONE);
                    ivSendFailed.setVisibility(View.VISIBLE);
                }
            }else{
                ivSendFailed.setVisibility(View.VISIBLE);
            }

            ivSendFailed.setTag(position);
            ivSendFailed.setOnClickListener(mSendFailedListener);
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

    public View.OnClickListener mSendFailedListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            int pos =  (int)v.getTag();
            displayResendDialog(pos);
        }
    };

    public View.OnLongClickListener mLongClickListener = new View.OnLongClickListener(){

        @Override
        public boolean onLongClick(View v) {
            int position = (int)v.getTag();
            displayMenuDialog(position);
            return false;
        }
    };

    private CustomDialog mDialog = null;
    private void displayResendDialog(final int position){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(mCtx);
        customBuilder.setNegativeButton(
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).setPositiveButton(
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (mResendLister != null){
                            mResendLister.reRend(position);
                        }
                    }
                });
        mDialog = customBuilder.create();
        mDialog.show();
    }

    private MenuDialog mMenuDialog = null;
    private void displayMenuDialog(final int position){
        MenuDialog.Builder customBuilder = new MenuDialog.Builder(mCtx);
        customBuilder.setMenuButtonListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatMsg info = mList.get(position);
                        int viewId  = v.getId();
                        if (viewId == R.id.tv_copy){
                            TextUtil.setClipboardText(info.content, mCtx);
                            mMenuDialog.cancel();
                        }else if (viewId == R.id.tv_delete){
                            mMenuDialog.cancel();
                        }
                    }

                });
        mMenuDialog = customBuilder.create();
        mMenuDialog.show();
    }

    private String dateToString(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String str=sdf.format(date);
        return str;
    }

}
