package com.wai.whiteley.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseFragmentActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.FontUtils;

public class ActivityOpeningHours extends BaseFragmentActivity {

	public static ActivityOpeningHours instance = null;

	private ScrollView scroll_main;
	private TextView txt_headertitle;
	private TextView txt_gifttitle;
	private LinearLayout tableOpeningHours;
	private TextView txt_description;
	private LinearLayout layer_main;
	private Button btnLaunchWebsite;

	private ArrayList<OpeningHour> openingHours = new ArrayList<OpeningHour>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opening_hours);
		instance = this;

		initData();
		
		scroll_main = (ScrollView) findViewById(R.id.scroll_main);
		String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
		txt_headertitle = (TextView) findViewById(R.id.txt_headertitle);
		txt_headertitle.setText(strTitles[Constants.SELECT_CASE_OPENING_HOURS]);

		txt_gifttitle = (TextView) findViewById(R.id.txt_gifttitle);
		tableOpeningHours = (LinearLayout) findViewById(R.id.table_opening_hours);
		for(OpeningHour openingHour: openingHours) {
			View convertView = getLayoutInflater().inflate(R.layout.row_openhour, null);
			TextView textDay = (TextView) convertView.findViewById(R.id.txt_day);
			TextView textOpenTime = (TextView) convertView.findViewById(R.id.txt_opentime);
			TextView textCloseTime = (TextView) convertView.findViewById(R.id.txt_closetime);
			
			textDay.setText(openingHour.day);
			textOpenTime.setText(openingHour.startTime);
			textCloseTime.setText(openingHour.endTime);
			
			tableOpeningHours.addView(convertView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}
		
		txt_description = (TextView) findViewById(R.id.txt_description);

		layer_main = (LinearLayout) findViewById(R.id.layer_main);
		btnLaunchWebsite = (Button) findViewById(R.id.button_launch_website);
		btnLaunchWebsite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityOpeningHours.this, ActivityWebView.class);
				intent.putExtra(ActivityWebView.EXTRA_URL, "http://www.whiteleyshopping.co.uk/");
				String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
				intent.putExtra(ActivityWebView.EXTRA_TITLE, strTitles[Constants.SELECT_CASE_OURSTORE]);
				startActivity(intent);
			}
		});

		// set fonts
		FontUtils.setTypeface(txt_headertitle, FontUtils.font_HelveticaNeueUltraLight, false);
		FontUtils.setTypefaceAllView(layer_main, FontUtils.font_HelveticaNeueThin);
		//FontUtils.setTypeface(txt_gifttitle, FontUtils.font_HelveticaNeue, true);
	}

	private void initData() {
		openingHours.clear();
		
		openingHours.add(new OpeningHour("Monday", "09:00", "20:00"));
		openingHours.add(new OpeningHour("Tuesday", "09:00", "20:00"));
		openingHours.add(new OpeningHour("Wednesday", "09:00", "20:00"));
		openingHours.add(new OpeningHour("Thursday", "09:00", "20:00"));
		openingHours.add(new OpeningHour("Friday", "09:00", "20:00"));
		openingHours.add(new OpeningHour("Saturday", "09:00", "19:00"));
		openingHours.add(new OpeningHour("Sunday", "10:30", "16:30"));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
    		int selMenu = mPrefs.getIntValue(Constants.SELECTED_MENU_NUMBER);
    		if (selMenu != Constants.SELECT_CASE_OPENING_HOURS) {
    			if (ActivityMain.instance != null)
    				ActivityMain.instance.selectMainMenu();
    		}
    	}
    }
    
    class OpeningHour {
    	String day;
    	String startTime;
    	String endTime;
    	
    	public OpeningHour(String day, String startTime, String endTime) {
			this.day = day;
			this.startTime = startTime;
			this.endTime = endTime;
		}
    }
    
}
