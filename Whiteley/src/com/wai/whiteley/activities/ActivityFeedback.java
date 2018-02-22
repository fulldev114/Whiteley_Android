package com.wai.whiteley.activities;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wai.whiteley.R;
import com.wai.whiteley.adapter.PriceAdapter;
import com.wai.whiteley.base.BaseFragmentActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.util.CommonUtil;

public class ActivityFeedback extends BaseFragmentActivity implements OnClickListener {

	public static ActivityFeedback instance = null;
	private ScrollView scroll_main;
	//private Spinner spinner_price;
	private PriceAdapter price_adapter;
	private Button btn_submit;
	private TextView txt_feedback_skip;
	private TextView txt_walk_yes;
	private TextView txt_walk_no;
	private TextView txt_kids_yes;
	private TextView txt_kids_no;
	private TextView edt_suggest;
	private TextView edt_feedback;

	private String walk = "";
	private String kids = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		instance = this;	
		
		scroll_main = (ScrollView) findViewById(R.id.scroll_main);
		//spinner_price = (Spinner) findViewById(R.id.spinner_price);
		btn_submit = (Button) findViewById(R.id.button_submit);
		txt_feedback_skip = (TextView) findViewById(R.id.txt_feedback_skip);
		txt_walk_yes = (TextView) findViewById(R.id.txt_walk_yes);
		txt_walk_no = (TextView) findViewById(R.id.txt_walk_no);
		txt_kids_yes = (TextView) findViewById(R.id.txt_kids_yes);
		txt_kids_no = (TextView) findViewById(R.id.txt_kids_no);
		edt_suggest = (TextView) findViewById(R.id.edt_suggest);
		edt_feedback = (TextView) findViewById(R.id.edt_feedback);

		txt_walk_yes.setBackgroundResource(R.drawable.bg_btn_unclicked);
		txt_walk_no.setBackgroundResource(R.drawable.bg_btn_unclicked);
		txt_kids_yes.setBackgroundResource(R.drawable.bg_btn_unclicked);
		txt_kids_no.setBackgroundResource(R.drawable.bg_btn_unclicked);

		txt_walk_yes.setOnClickListener(this);
		txt_walk_no.setOnClickListener(this);
		txt_kids_yes.setOnClickListener(this);
		txt_kids_no.setOnClickListener(this);

		edt_suggest.clearFocus();
		edt_suggest.addTextChangedListener(new TextWatcher() {

		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		    	//validate();
		    }
		    
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		    	
		    }
		    
		    @Override
		    public void afterTextChanged(Editable arg0) {
		    }
		});
		
//		ratingBar.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				validate();
//				return false;
//			}});
		
		/*
		// Price Spinner
		List<String> lists_price = new ArrayList<String>();
		String[] array_price = getResources().getStringArray(R.array.price_arrays);
		for(int i = 0; i < array_price.length; i++) {
			lists_price.add(array_price[i]);
		}
		
		price_adapter = new PriceAdapter(this, lists_price);
		spinner_price.setAdapter(price_adapter);
		spinner_price.setSelection(price_adapter.getCount(), false);
		spinner_price.setOnItemSelectedListener( new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				validate();
				((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.header_bg));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
	*/
		// Submit Button
		btn_submit.setBackgroundColor(getResources().getColor(R.color.fb_submit_disable));
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( !validate() )
					return;
								
				mPrefs.setStringValue(Constants.APP_FEEDBACK, Constants.FEEDBACK_SEND);
				
				final String deviceToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);
				
				if (deviceToken.length() > 0) {
										
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							String suggest = edt_suggest.getText().toString();
							String feedback = edt_feedback.getText().toString();
							Server.SendFeedback(deviceToken, kids, walk, suggest, feedback);
							return null;
						}
			    	}.execute(null, null, null);
			    	
				}
					
				//Intent intent = new Intent(ActivityFeedback.this, ActivityFeedbackWelcome.class);
				//startActivity(intent);
				
				onBackPressed();
			}
		});
		
		
		
		// Skip Feedback
		txt_feedback_skip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPrefs.setStringValue(Constants.APP_FEEDBACK, null);
				onBackPressed();
			}
		});

	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
			if (ActivityMain.instance != null)
				ActivityMain.instance.selectMainMenu();
    	}
    }
 
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_walk_yes:
			txt_walk_yes.setBackgroundResource(R.drawable.bg_btn_clicked);
			txt_walk_no.setBackgroundResource(R.drawable.bg_btn_unclicked);
			txt_walk_yes.setTextColor(getResources().getColor(color.white));
			txt_walk_no.setTextColor(getResources().getColor(R.color.font_color2));
			walk = "Yes";
			break;
		case R.id.txt_walk_no:
			txt_walk_yes.setBackgroundResource(R.drawable.bg_btn_unclicked);
			txt_walk_no.setBackgroundResource(R.drawable.bg_btn_clicked);
			txt_walk_yes.setTextColor(getResources().getColor(R.color.font_color2));
			txt_walk_no.setTextColor(getResources().getColor(color.white));
			walk = "No";
			break;
		case R.id.txt_kids_yes:
			txt_kids_yes.setBackgroundResource(R.drawable.bg_btn_clicked);
			txt_kids_no.setBackgroundResource(R.drawable.bg_btn_unclicked);
			txt_kids_yes.setTextColor(getResources().getColor(color.white));
			txt_kids_no.setTextColor(getResources().getColor(R.color.font_color2));
			kids = "Yes";
			break;
		case R.id.txt_kids_no:
			txt_kids_yes.setBackgroundResource(R.drawable.bg_btn_unclicked);
			txt_kids_no.setBackgroundResource(R.drawable.bg_btn_clicked);
			txt_kids_yes.setTextColor(getResources().getColor(R.color.font_color2));
			txt_kids_no.setTextColor(getResources().getColor(color.white));
			kids = "No";
			break;
		default:
			break;
		}
		
		validate();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_right, R.anim.out_right);
	}

	public void onBackActivity(View paramView) {
		onBackPressed();
	}

	public void onMore(View paramView) {
		CommonUtil.makeBlurAndStartActivity(this, scroll_main);
	}
	
	public boolean validate() {
	
		//if ( spinner_price != null && spinner_price.getSelectedItem() != null ) 
		//	price = spinner_price.getSelectedItem().toString();
				
		if ( walk.length() > 0 && kids.length() > 0 ) {
			btn_submit.setBackgroundColor(getResources().getColor(R.color.fb_submit_enable));
			return true;
		}
		
		btn_submit.setBackgroundColor(getResources().getColor(R.color.fb_submit_disable));
		return false;
	}
	
}
