package com.wai.whiteley.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.wai.whiteley.R;

public class DCProgressDialog extends ProgressDialog {

	private Context mContext;

	public DCProgressDialog(Context context) {
		this(context, false);
	}

	public DCProgressDialog(Context context, boolean cancelable) {
		super(context);
		mContext = context;
		
		this.setCancelable(cancelable);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_progress);
	}
}
