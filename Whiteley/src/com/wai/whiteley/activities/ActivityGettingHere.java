package com.wai.whiteley.activities;

import android.R.color;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewpagerindicator.LinePageIndicator;
import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseFragmentActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.FontUtils;
import com.wai.whiteley.view.CustomDurationViewPager;

public class ActivityGettingHere extends BaseFragmentActivity implements OnClickListener {

	public static ActivityGettingHere instance = null;

	private CustomDurationViewPager pager;
	private GettingHereFragment car_fragment = null;
	private GettingHereFragment train_fragment = null;
	private GettingHereFragment bus_fragment = null;

	private TextView txt_headertitle;

	private LinearLayout layer_main;
	private TextView txt_car;
	private TextView txt_train;
	private TextView txt_bus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gettinghere);
		instance = this;

		txt_headertitle = (TextView) findViewById(R.id.txt_headertitle);
		String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
		txt_headertitle.setText(strTitles[Constants.SELECT_CASE_TRAVEL]);

		layer_main = (LinearLayout) findViewById(R.id.layer_main);
		txt_car = (TextView) findViewById(R.id.txt_car);
		txt_train = (TextView) findViewById(R.id.txt_train);
		txt_bus = (TextView) findViewById(R.id.txt_bus);

		txt_car.setOnClickListener(this);
		txt_train.setOnClickListener(this);
		txt_bus.setOnClickListener(this);

		txt_car.setBackgroundResource(R.color.pager_bg);
		txt_train.setBackgroundResource(R.drawable.bg_rect_979797_1);
		txt_bus.setBackgroundResource(R.drawable.bg_rect_979797_1);
		txt_car.setTextColor(getResources().getColor(color.white));
		txt_train.setTextColor(getResources().getColor(color.black));
		txt_bus.setTextColor(getResources().getColor(color.black));

		FragmentPagerAdapter adapter = new LasyAdapter(getSupportFragmentManager());
		pager = (CustomDurationViewPager) findViewById(R.id.pager);
		pager.setScrollDurationFactor(Constants.VP_SCROLL_FACTOR);
		pager.setAdapter(adapter);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;

		LinePageIndicator indicator = (LinePageIndicator)findViewById(R.id.indicator);
		indicator.setLineWidth(width/3.0f);
		indicator.setViewPager(pager);
		indicator.setVisibility(View.INVISIBLE);

		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int index) {
				if (index == Constants.FRAGMENT_GETTINGHERE_CAR) {
					txt_car.setBackgroundResource(R.color.pager_bg);
					txt_train.setBackgroundResource(R.drawable.bg_rect_979797_1);
					txt_bus.setBackgroundResource(R.drawable.bg_rect_979797_1);
					txt_car.setTextColor(getResources().getColor(color.white));
					txt_train.setTextColor(getResources().getColor(R.color.font_color2));
					txt_bus.setTextColor(getResources().getColor(R.color.font_color2));
				} else if (index == Constants.FRAGMENT_GETTINGHERE_TRAIN) {
					txt_car.setBackgroundResource(R.drawable.bg_rect_979797_1);
					txt_train.setBackgroundResource(R.color.pager_bg);
					txt_bus.setBackgroundResource(R.drawable.bg_rect_979797_1);
					txt_car.setTextColor(getResources().getColor(R.color.font_color2));
					txt_train.setTextColor(getResources().getColor(color.white));
					txt_bus.setTextColor(getResources().getColor(R.color.font_color2));
				} else if (index == Constants.FRAGMENT_GETTINGHERE_BUS) {
					txt_car.setBackgroundResource(R.drawable.bg_rect_979797_1);
					txt_train.setBackgroundResource(R.drawable.bg_rect_979797_1);
					txt_bus.setBackgroundResource(R.color.pager_bg);
					txt_car.setTextColor(getResources().getColor(R.color.font_color2));
					txt_train.setTextColor(getResources().getColor(R.color.font_color2));
					txt_bus.setTextColor(getResources().getColor(color.white));
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		// set fonts
		FontUtils.setTypeface(txt_headertitle, FontUtils.font_HelveticaNeueUltraLight, false);
		FontUtils.setTypeface(txt_car, FontUtils.font_Novecentowide_DemiBold, false);
		FontUtils.setTypeface(txt_train, FontUtils.font_Novecentowide_DemiBold, false);
		FontUtils.setTypeface(txt_bus, FontUtils.font_Novecentowide_DemiBold, false);
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
		CommonUtil.makeBlurAndStartActivity(this, layer_main);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
    		int selMenu = mPrefs.getIntValue(Constants.SELECTED_MENU_NUMBER);
    		if (selMenu != Constants.SELECT_CASE_TRAVEL) {
    			if (ActivityMain.instance != null)
    				ActivityMain.instance.selectMainMenu();
    		}
    	}
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_car:
			pager.setCurrentItem(0);
			break;
		case R.id.txt_train:
			pager.setCurrentItem(1);
			break;
		case R.id.txt_bus:
			pager.setCurrentItem(2);
			break;
		default:
			break;
		}
	}

	class LasyAdapter extends FragmentPagerAdapter {

		public LasyAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			if (position == Constants.FRAGMENT_GETTINGHERE_CAR) {
				if (car_fragment == null)
					car_fragment = GettingHereFragment.newInstance("car");
				return (Fragment)car_fragment;

			} else if (position == Constants.FRAGMENT_GETTINGHERE_TRAIN) {
				if (train_fragment == null)
					train_fragment = GettingHereFragment.newInstance("train");
				return (Fragment)train_fragment;

			} else if (position == Constants.FRAGMENT_GETTINGHERE_BUS) {
				if (bus_fragment == null)
					bus_fragment = GettingHereFragment.newInstance("bus");
				return (Fragment)bus_fragment;

			}

			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (position == Constants.FRAGMENT_GETTINGHERE_CAR) {
				return getResources().getString(R.string.strgettinghere1);
			} else if (position == Constants.FRAGMENT_GETTINGHERE_TRAIN){
				return getResources().getString(R.string.strgettinghere2);
			} else if (position == Constants.FRAGMENT_GETTINGHERE_BUS){
				return getResources().getString(R.string.strgettinghere3);
			}

			return "";
		}

		@Override
		public int getCount() {
			return 3;
		}
	}
}
