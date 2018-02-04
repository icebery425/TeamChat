package com.jinglangtech.teamchat.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
    public static Toast mInstance;
    private static Context mContext;
    public static void setContext(Context context)
    {
        mContext = context;
    }

    public static boolean show(CharSequence msg)
    {
        if(mContext == null)
            return false;
        if(mInstance==null)
        {
            mInstance = Toast.makeText(mContext,msg,Toast.LENGTH_SHORT);
        }
        mInstance.setText(msg);
        mInstance.show();
        return true;
    }

    public static boolean show(int msgId)
    {
        CharSequence msg = mContext.getText(msgId);
        return show(msg);
    }




}
