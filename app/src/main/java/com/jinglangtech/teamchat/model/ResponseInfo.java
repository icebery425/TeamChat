package com.jinglangtech.teamchat.model;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

public class ResponseInfo {

	public boolean status;
	public int err;
	public String msg;
	public String data;

	public PageInfo pageinfo;
	
    public ResponseInfo()
    {
    	super();
    }
    public ResponseInfo(String str) {
    }
	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public <T>ArrayList parseList(Class<T> clazz)
	{
		ArrayList<T> objs = null;
		if (data != null && !data.equals("") && !data.equals("{}"))
		{
			try {
				objs = (ArrayList<T>) JSON.parseArray(data, clazz);
			}catch(Exception e){
				String erro = e.getMessage();
			}
		}
		return objs;
	}


	public <T>Object parseInfo(Class<T> clazz)
	{
		Object objs = null;
		if (data != null && !data.equals("") && !data.equals("{}"))
		{
			objs = JSON.parseObject(data, clazz);
		}
		return objs;
	}

	public int parseInfoInt(String keyName)
	{
		int ret = 0;
		if (data != null && !data.equals(""))
		{
			JSONObject infoJson= JSON.parseObject(data);
			if (infoJson != null)
			{
				ret = infoJson.getIntValue(keyName);
			}
		}
		return ret;
	}
}
