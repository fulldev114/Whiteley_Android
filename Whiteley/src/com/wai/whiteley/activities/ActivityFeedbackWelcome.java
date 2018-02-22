package com.wai.whiteley.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseFragmentActivity;
import com.wai.whiteley.config.Constants;

public class ActivityFeedbackWelcome extends BaseFragmentActivity implements OnClickListener {

	public static ActivityFeedbackWelcome instance = null;
	public Button welcome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_dialog);

		instance = this;	
		
		welcome = (Button) findViewById(R.id.btn_welcome);
		welcome.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_welcome:
			String fb = mPrefs.getStringValue(Constants.APP_FEEDBACK);
			if ( fb.equals(Constants.FEEDBACK_SEND) ) {
				mPrefs.setStringValue(Constants.APP_FEEDBACK, null);
			}
			finish();
		  break;
		default:
		  break;
		}
		finish();
	}
	
}
