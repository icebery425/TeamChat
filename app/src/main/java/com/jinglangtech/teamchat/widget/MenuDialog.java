package com.jinglangtech.teamchat.widget;


import android.app.Dialog;
import android.content.Context;
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

		private View.OnClickListener menuClickListener;

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

		public Builder setMenuButtonListener(
				View.OnClickListener listener) {

			this.menuClickListener = listener;

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
			TextView tvcopy = ((TextView)layout.findViewById(R.id.tv_copy));
			TextView tvdelete = ((TextView)layout.findViewById(R.id.tv_delete));

			dialog.addContentView(layout, new LayoutParams(274, 180));
			
			if(menuClickListener != null){
				tvcopy.setOnClickListener(menuClickListener);
				tvdelete.setOnClickListener(menuClickListener);
			}



			// set the content message

			dialog.setContentView(layout);

			return dialog;

		}

	}

}
