package com.wai.whiteley.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.wai.whiteley.R;

public class FeedbackDialogClass extends Dialog implements
android.view.View.OnClickListener {

public Button welcome;

	public FeedbackDialogClass(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feedback_dialog);
		welcome = (Button) findViewById(R.id.btn_welcome);
		welcome.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_welcome:
			dismiss();
		  break;
		default:
		  break;
		}
		dismiss();
	}
}