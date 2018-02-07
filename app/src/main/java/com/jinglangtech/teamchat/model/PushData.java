package com.jinglangtech.teamchat.model;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

public class PushData {
	
	public String type; //消类型:  msg(纯消息),web(打开网页),event(处理事件)
	public String view; //视图类型: openApp(打开APP),web(网页打开),detail(详情),list(列表)
	public String alertMsg; //提示语
	public String extContent;	//扩展内容
	public ExtContent extValue;

	public static class ExtContent{
		public String platform;  //平台类型
		public int subType;    //列表或详情，具体见如下
		public String webUrl;	 //网页的URL地址
		public String value;	//详情uid
		public String valueExt; //详情扩展uid
	}
	
	public void parseExtContent(){
		if (!TextUtils.isEmpty(extContent))
		{
			try {
				extValue = JSON.parseObject(extContent, ExtContent.class);
			} catch (Exception e) {
				Log.d("", e.getMessage());
			}
		}
	}
	
	public static final String TYPE_MSG			= "msg";
	public static final String TYPE_WEB			= "web";
	public static final String TYPE_EVENT		= "event";
	
	public static final int TARGET_NOTIFICATION 	= 1;  	//通知
	public static final int TARGET_CUSTIOM_MSG	 	= 2;	//消息


}
