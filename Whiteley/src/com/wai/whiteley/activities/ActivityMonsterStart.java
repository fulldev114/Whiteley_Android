package com.wai.whiteley.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wai.whiteley.R;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync.StatisticType;
import com.wai.whiteley.base.BaseActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.service.BeaconDetectService;
import com.wai.whiteley.util.FontUtils;

public class ActivityMonsterStart extends BaseActivity {

	public static ActivityMonsterStart instance = null;

	private ImageButton btnHome;
	private ImageButton btnStart;
	private TextView txt_desc1;
	private TextView txt_desc2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monster_start);

		instance = this;
		
		if(getIntent().hasExtra(Constants.EXTRA_BEACON_UUID)) {
			String uuid = getIntent().getStringExtra(Constants.EXTRA_BEACON_UUID);
			int major = getIntent().getIntExtra(Constants.EXTRA_BEACON_MAJOR, 0);
			int minor = getIntent().getIntExtra(Constants.EXTRA_BEACON_MINOR, 0);
			new StatisticsBeaconAsync(this, StatisticType.STATISTIC_NOTIFICATION_INTERACTION, uuid, major, minor).execute();
		}
		
		btnHome = (ImageButton) findViewById(R.id.button_home);
		btnHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnStart = (ImageButton) findViewById(R.id.button_start);
		btnStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityMonsterStart.this, ActivityMonsterCoach.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.in_left, R.anim.out_left);
			}
		});
		txt_desc1 = (TextView) findViewById(R.id.txt_desc1);
		txt_desc2 = (TextView) findViewById(R.id.txt_desc2);

		// set fonts
		FontUtils.setTypeface(txt_desc1, FontUtils.font_Novecentowide_Light, false);
		FontUtils.setTypeface(txt_desc2, FontUtils.font_Novecentowide_Bold, false);
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_right, R.anim.out_right);
	}
	
}
