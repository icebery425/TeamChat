package com.jinglangtech.teamchat.widget;


import java.io.File;
import java.util.ArrayList;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;

public class CustomPhotoSelect extends Dialog {

	public CustomPhotoSelect(Context context, int theme) {

		super(context, theme);

	}

	public CustomPhotoSelect(Context context) {

		super(context);

	}
	
	public static interface OnModelClickListener
	{
		public void onModelSelect(ArrayList<String> models);
		public int onAddToCart(int skuId, int quantity);
	}

	/**
	 * 
	 * Helper class for creating a custom dialog
	 */

	public static class Builder {

		private Context context;

		private TextView mTvLogout;
		private Button mBtnCancel;
		private View.OnClickListener onSelectListener;
		
		public Builder(Context context) {

			this.context = context;

		}
		
		public Builder setSelectListener(View.OnClickListener listener){
			this.onSelectListener = listener;
			return this;
		}

		/**
		 * 
		 * Create the custom dialog
		 */

		public CustomPhotoSelect create() {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// instantiate the dialog with the custom Theme

			final CustomPhotoSelect dialog = new CustomPhotoSelect(context,
					R.style.Dialog);

			dialog.setCanceledOnTouchOutside(false);

			View layout = inflater.inflate(R.layout.dialog_photo_select, null);

			dialog.addContentView(layout, new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


			mTvLogout = (TextView)layout.findViewById(R.id.tv_logout);
			mBtnCancel = (Button) layout.findViewById(R.id.btn_cancel);

			(layout.findViewById(R.id.tv_logout)).setOnClickListener(onSelectListener);
			
			(layout.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					dialog.dismiss();
				}

			});
			
			
			// set the content message

			dialog.setContentView(layout);

			return dialog;

		}
		
	
	
	}
	

}
