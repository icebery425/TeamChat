package com.jinglangtech.teamchat.model;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

public class PushData {
	
	public String roomid; //消息是属于哪个聊天室的
	public String fromid; //消息来自谁


	public static final String TYPE_MSG			= "msg";
	public static final String TYPE_WEB			= "web";
	public static final String TYPE_EVENT		= "event";
	
	public static final int TARGET_NOTIFICATION 	= 1;  	//通知
	public static final int TARGET_CUSTIOM_MSG	 	= 2;	//消息


}
