package com.jinglangtech.teamchat.widget;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.jinglangtech.teamchat.R;

public class MenuDialog extends Dialog {

	public MenuDialog(Context context, int theme) {

		super(context, theme);

	}

	public MenuDialog(Context context) {

		super(context);

	}

	/**
	 * 
	 * Helper class for creating a custom dialog
	 */

	public static class Builder {

		private Context context;
		
		private String content;

		private View contentView;

		private OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		public Builder(Context context) {

			this.context = context;

		}
		

		

		/**
		 * 
		 * Set a custom content view for the Dialog.
		 * 
		 * If a message is set, the contentView is not
		 * 
		 * added to the Dialog...
		 * 
		 * @param v
		 * 
		 * @return
		 */

		public Builder setContentView(View v) {

			this.contentView = v;

			return this;

		}

		public void setContent(String content) {
			this.content = content;
		}
		
		/**
		 * 
		 * Set the positive button resource and it's listener
		 *
		 * @param listener
		 * 
		 * @return
		 */

		public Builder setPositiveButton(
				OnClickListener listener) {

			this.positiveButtonClickListener = listener;

			return this;

		}


		/**
		 * 
		 * Set the negative button resource and it's listener
		 *
		 * @param listener
		 * 
		 * @return
		 */

		public Builder setNegativeButton(OnClickListener listener) {
			this.negativeButtonClickListener = listener;

			return this;

		}


		/**
		 * 
		 * Create the custom dialog
		 */

		public MenuDialog create() {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// instantiate the dialog with the custom Theme

			final MenuDialog dialog = new MenuDialog(context,
					R.style.Dialog);

			dialog.setCanceledOnTouchOutside(false);

			View layout = inflater.inflate(R.layout.dialog_menu, null);

			dialog.addContentView(layout, new LayoutParams(274, 180));
			
			if(content != null){
				((TextView)layout.findViewById(R.id.tv_content)).setText(content);
			}


			// set the confirm button



				if (positiveButtonClickListener != null) {

					((TextView) layout.findViewById(R.id.tv_confirm))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {

									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);

								}

							});

				}


			// set the cancel button


				if (negativeButtonClickListener != null) {

					((TextView) layout.findViewById(R.id.tv_cancel))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {

									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);

								}

							});

				}



			// set the content message

			dialog.setContentView(layout);

			return dialog;

		}

	}

}
