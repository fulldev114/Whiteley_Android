package com.wai.whiteley.activities;

import java.util.List;

import android.R.color;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wai.whiteley.R;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync.StatisticType;
import com.wai.whiteley.base.BaseFragmentActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.service.BeaconDetectService;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.FontUtils;

public class ActivityOurStores extends BaseFragmentActivity implements OnClickListener {

	public static final String EXTRA_FROM_WHERE = "extra_from_where";
	
	public static ActivityOurStores instance = null;

	private OurStoresNameFragment fragmentStoreName = null;
	private OurStoresCategoryFragment fragmentCategory = null;

	private LinearLayout rootView;
	private TextView txt_headertitle;
	private RelativeLayout viewCloseBar;
	private ImageButton btnClose;
	private TextView txt_orbrowse;
	private LinearLayout categoryBar;

	private LinearLayout layer_main;
	private TextView txt_storename;
	private TextView txt_category;
	private EditText editSearch;

	private boolean isFromCentreMap = false;
	private boolean isShowSubCategory = false;
	private boolean isShowEmptyHolder = false;
	private int nSelectIndex = 0;
	private int previousHeightDiffrence = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ourstores);
		instance = this;

		if(getIntent().hasExtra(Constants.EXTRA_BEACON_UUID)) {
			String uuid = getIntent().getStringExtra(Constants.EXTRA_BEACON_UUID);
			int major = getIntent().getIntExtra(Constants.EXTRA_BEACON_MAJOR, 0);
			int minor = getIntent().getIntExtra(Constants.EXTRA_BEACON_MINOR, 0);
			new StatisticsBeaconAsync(this, StatisticType.STATISTIC_NOTIFICATION_INTERACTION, uuid, major, minor).execute();
		}
		
		rootView = (LinearLayout) findViewById(R.id.view_root);
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
					@Override
					public void onGlobalLayout() {
						
						Rect r = new Rect();
						rootView.getWindowVisibleDisplayFrame(r);
						
						int screenHeight = rootView.getRootView().getHeight();
						int heightDifference = screenHeight - (r.bottom);
						
						if(previousHeightDiffrence == heightDifference)
							return;
						
						previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {
							//isKeyBoardVisible = true;
							categoryBar.setVisibility(View.GONE);
						} else {
							//isKeyBoardVisible = false;
							if((isFromCentreMap && isShowSubCategory) || isShowEmptyHolder) {
								categoryBar.setVisibility(View.GONE);
							} else {
								categoryBar.setVisibility(View.VISIBLE);
							}
						}
						
					}
				});
		txt_headertitle = (TextView) findViewById(R.id.txt_headertitle);
		String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
		txt_headertitle.setText(strTitles[Constants.SELECT_CASE_OURSTORE]);
		viewCloseBar = (RelativeLayout) findViewById(R.id.view_close_bar);
		btnClose = (ImageButton) findViewById(R.id.button_close_search);
		btnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		editSearch = (EditText) findViewById(R.id.editSearch);
		editSearch.addTextChangedListener(new TextWatcher() {

		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		    	if(fragmentStoreName != null)
		    		fragmentStoreName.searchShop(cs.toString());
		    	
		    	if(fragmentCategory != null)
		    		fragmentCategory.searchShop(cs.toString());
		    }
		    
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		    	
		    }
		    
		    @Override
		    public void afterTextChanged(Editable arg0) {}
		});
		
		txt_orbrowse = (TextView) findViewById(R.id.txt_orbrowse);
		categoryBar = (LinearLayout) findViewById(R.id.category_bar);

		layer_main = (LinearLayout) findViewById(R.id.layer_main);
		txt_storename = (TextView) findViewById(R.id.txt_storename);
		txt_category = (TextView) findViewById(R.id.txt_category);

		txt_storename.setOnClickListener(this);
		txt_category.setOnClickListener(this);

		txt_storename.setBackgroundResource(R.color.pager_bg);
		txt_category.setBackgroundResource(R.drawable.bg_rect_979797_1);
		txt_storename.setTextColor(getResources().getColor(color.white));
		txt_category.setTextColor(getResources().getColor(color.black));

		isFromCentreMap = getIntent().getBooleanExtra(EXTRA_FROM_WHERE, false);
		if(isFromCentreMap) {
			txt_headertitle.setText("Centre Map");
			viewCloseBar.setVisibility(View.VISIBLE);
			editSearch.setHint("");
		}

		if (nSelectIndex == 0) {
			txt_storename.setBackgroundResource(R.color.pager_bg);
			txt_category.setBackgroundResource(R.drawable.bg_rect_979797_1);
			txt_storename.setTextColor(getResources().getColor(color.white));
			txt_category.setTextColor(getResources().getColor(R.color.font_color2));
			
			fragmentStoreName = OurStoresNameFragment.newInstance(0, isFromCentreMap);
			showFragment(fragmentStoreName, false, false);
			
		} else if (nSelectIndex == 1) {
			txt_storename.setBackgroundResource(R.drawable.bg_rect_979797_1);
			txt_category.setBackgroundResource(R.color.pager_bg);
			txt_storename.setTextColor(getResources().getColor(R.color.font_color2));
			txt_category.setTextColor(getResources().getColor(color.white));
			
			fragmentCategory = OurStoresCategoryFragment.newInstance(isFromCentreMap);
			showFragment(fragmentCategory, false, false);
		}

		// set fonts
		FontUtils.setTypeface(txt_headertitle, FontUtils.font_HelveticaNeueUltraLight, false);
		FontUtils.setTypefaceAllView(viewCloseBar, FontUtils.font_HelveticaNeueThin);
		FontUtils.setTypeface(editSearch, FontUtils.font_HelveticaNeueThin, false);
		FontUtils.setTypeface(txt_orbrowse, FontUtils.font_HelveticaNeueThin, false);
		FontUtils.setTypeface(txt_storename, FontUtils.font_Novecentowide_DemiBold, false);
		FontUtils.setTypeface(txt_category, FontUtils.font_Novecentowide_DemiBold, false);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		if(isShowSubCategory) {
			isShowSubCategory = false;
			categoryBar.setVisibility(View.VISIBLE);
		}

		super.onBackPressed();
		
		if(!isFromCentreMap)
			overridePendingTransition(R.anim.in_right, R.anim.out_right);
	}

	public void onBackActivity(View paramView) {
		try {
			if(!isFromCentreMap) {
				onBackPressed();
			} else {
				if(fragmentManager.getBackStackEntryCount() > 0) {
					if(isShowSubCategory) {
						isShowSubCategory = false;
					}
					fragmentManager.popBackStack();
					categoryBar.setVisibility(View.VISIBLE);
				} else {
					mPrefs.setIntValue(Constants.SELECTED_MENU_NUMBER, -1);
					if(ActivityMain.instance != null)
	    				ActivityMain.instance.selectMainMenu();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void onMore(View paramView) {
		CommonUtil.makeBlurAndStartActivity(this, layer_main);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
    		int selMenu = mPrefs.getIntValue(Constants.SELECTED_MENU_NUMBER);
    		if (selMenu != Constants.SELECT_CASE_OURSTORE) {
    			if (ActivityMain.instance != null)
    				ActivityMain.instance.selectMainMenu();
    		}
    	}
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_storename:
			txt_storename.setBackgroundResource(R.color.pager_bg);
			txt_category.setBackgroundResource(R.drawable.bg_rect_979797_1);
			txt_storename.setTextColor(getResources().getColor(color.white));
			txt_category.setTextColor(getResources().getColor(R.color.font_color2));
			
			fragmentStoreName = OurStoresNameFragment.newInstance(0, isFromCentreMap);
			showFragment(fragmentStoreName, false, false);
			break;
			
		case R.id.txt_category:
			txt_storename.setBackgroundResource(R.drawable.bg_rect_979797_1);
			txt_category.setBackgroundResource(R.color.pager_bg);
			txt_storename.setTextColor(getResources().getColor(R.color.font_color2));
			txt_category.setTextColor(getResources().getColor(color.white));
			
			fragmentCategory = OurStoresCategoryFragment.newInstance(isFromCentreMap);
			showFragment(fragmentCategory, false, false);
			break;
		default:
			break;
		}
	}
	
	public void setEmptyHolderStatus(boolean isShow) {
		isShowEmptyHolder = isShow;
	}
	
	public void showCategorySubStore(int categoryId) {
		editSearch.setText("");
				
		fragmentStoreName = OurStoresNameFragment.newInstance(categoryId, isFromCentreMap);
		showFragment(fragmentStoreName, true, false);
		
		categoryBar.setVisibility(View.GONE);
		isShowSubCategory = true;
	}
	
	public void finish(String location, String unitNum) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
		
		Intent intent = new Intent();
		intent.putExtra(ActivityCentreMap.EXTRA_KEY_LOCATION, location);
		intent.putExtra(ActivityCentreMap.EXTRA_KEY_UNITNUM, unitNum);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}
