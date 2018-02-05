package com.jinglangtech.teamchat.util;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jinglangtech.teamchat.R;

public final class ToastUtils {
	private static Toast result;

	/**
	 * @param context
	 */
	public static void showToast(Context context, int resId) {
		if (result != null) {
			result.cancel();
		}
		result = new Toast(context);
		result.setGravity(Gravity.CENTER, 0, 0);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.custom_toast, null);
		v.getBackground().setAlpha(160);
		TextView tv = (TextView) v.findViewById(R.id.tv_sku_name);
		tv.setText(context.getResources().getString(resId));
		result.setView(v);
		result.setDuration(Toast.LENGTH_SHORT);
		result.show();
	}

	public static void showToast(Context context, String string) {
		if (result != null) {
			result.cancel();
		}
		result = new Toast(context);
		result.setGravity(Gravity.CENTER, 0, 0);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.custom_toast, null);
		v.getBackground().setAlpha(160);
		TextView tv = (TextView) v.findViewById(R.id.tv_sku_name);
		tv.setText(string);
		result.setView(v);
		result.setDuration(Toast.LENGTH_SHORT);
		result.show();
	}

	private ToastUtils() {

	}
}
