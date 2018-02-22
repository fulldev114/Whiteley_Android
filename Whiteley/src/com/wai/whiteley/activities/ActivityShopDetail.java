package com.wai.whiteley.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.daimajia.androidanimations.library.fading_entrances.FadeInAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutAnimator;
import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseFragmentActivity;
import com.wai.whiteley.base.BaseTask;
import com.wai.whiteley.base.BaseTask.TaskListener;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.database.dao.StoreNameDAO;
import com.wai.whiteley.http.ResponseModel.OpenModel;
import com.wai.whiteley.http.ResponseModel.StoreDetail;
import com.wai.whiteley.http.ResponseModel.StoreDetailModel;
import com.wai.whiteley.http.ResponseModel.StoreModel;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.model.AppModels.Store_open_model;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.DCImageLoader;
import com.wai.whiteley.util.FontUtils;

public class ActivityShopDetail extends BaseFragmentActivity {

	public static ActivityShopDetail instance = null;

	private ScrollView scroll_main;
	private TextView txt_headertitle;
	private TextView txt_shoptitle;
	//private TextView txt_shoplocation;
	private TextView txt_description;
	private ImageView img_shoplogo;
	private ImageView img_shopphoto;
	private Button tbtn_map;
	private Button tbtn_phonecall;
	private Button tbtn_laptop;
	private ToggleButton tbtn_open_hours;
	private Button tbtn_offers;
	private ToggleButton tbtn_setfavourite;
	private TextView txt_likestoretitle;
	private TextView txt_likestorecontent;
	private TextView txt_similartitle;
	private LinearLayout layer_container;
	
	private LinearLayout layer_addhours;
	private ImageView img_favourite_big;

	private ArrayList<Store_open_model> mOpenData = new ArrayList<Store_open_model>();

	private int mStoreId;
	private StoreDetailModel mStoreDetailModel = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopdetail);

		instance = this;

		mStoreId = getIntent().getIntExtra(Constants.SELECTED_STORE_ID, 0);

		scroll_main = (ScrollView) findViewById(R.id.scroll_main);
		scroll_main.setVisibility(View.GONE);
		String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
		txt_headertitle = (TextView) findViewById(R.id.txt_headertitle);
		txt_headertitle.setText(strTitles[Constants.SELECT_CASE_OURSTORE]);

		txt_shoptitle = (TextView) findViewById(R.id.txt_shoptitle);
		//txt_shoplocation = (TextView) findViewById(R.id.txt_shoplocation);
		txt_description = (TextView) findViewById(R.id.txt_description);
		img_shoplogo = (ImageView) findViewById(R.id.img_shoplogo);
		img_shoplogo.setImageResource(android.R.color.transparent);
		img_shopphoto = (ImageView) findViewById(R.id.img_shopphoto);

		tbtn_map = (Button) findViewById(R.id.tbtn_map);
		tbtn_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mStoreDetailModel == null)
					return;
				
				int screenSize = DrakeCircusApplication.getInstance().mScreenHistory.size();
				if(screenSize > Constants.MAP_ACTIVITY_LIMIT) {
			    	for(int i = 0; i < screenSize - Constants.MAP_ACTIVITY_LIMIT; i++) {
			    		Activity screen = DrakeCircusApplication.getInstance().mScreenHistory.get(i);
			    		DrakeCircusApplication.getInstance().mScreenHistory.remove(screen);
			    		screen.finish();
			    	}
				}
				
				Intent intent = new Intent(ActivityShopDetail.this, ActivityCentreMap.class);
				intent.putExtra(ActivityCentreMap.EXTRA_KEY_LOCATION, mStoreDetailModel.location);
				intent.putExtra(ActivityCentreMap.EXTRA_KEY_UNITNUM, mStoreDetailModel.unit_num);
				startActivity(intent);
			}
		});
		tbtn_phonecall = (Button) findViewById(R.id.tbtn_phonecall);
		tbtn_phonecall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mStoreDetailModel == null)
					return;
				
				if (((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
					Toast.makeText(ActivityShopDetail.this, "This device has not call ability.", Toast.LENGTH_LONG).show();
					return;
				}

				
				new AlertDialog.Builder(ActivityShopDetail.this)
				.setTitle("Call this store?")
				.setMessage("Would you like to call " + mStoreDetailModel.name + "?")
				.setPositiveButton("Call now", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
					        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					        callIntent.setData(Uri.parse("tel:" + mStoreDetailModel.phone));
					        if (Build.VERSION.SDK_INT > 20) // Build.VERSION_CODES.KITKAT
					        	callIntent.setPackage("com.android.server.telecom");
					        else
					        	callIntent.setPackage("com.android.phone");
					        startActivity(callIntent);
						} catch (ActivityNotFoundException e) {
					    	e.printStackTrace();
					    }
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create()
				.show();
				
			}
		});
		tbtn_laptop = (Button) findViewById(R.id.tbtn_laptop);
		tbtn_laptop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mStoreDetailModel == null)
					return;
				
				Intent intent = new Intent(ActivityShopDetail.this, ActivityWebView.class);
				intent.putExtra(ActivityWebView.EXTRA_URL, mStoreDetailModel.url);
				intent.putExtra(ActivityWebView.EXTRA_TITLE, txt_headertitle.getText());
				startActivity(intent);
				
//			    Uri uri = Uri.parse(mStoreDetailModel.url);
//			    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
//			    try {
//			        startActivity(myAppLinkToMarket);
//			    } catch (ActivityNotFoundException e) {
//			        Toast.makeText(ActivityShopDetail.this, " unable to view.", Toast.LENGTH_LONG).show();
//			    }
			}
		});
		tbtn_open_hours = (ToggleButton) findViewById(R.id.tbtn_open_hours);
		tbtn_offers = (Button) findViewById(R.id.tbtn_offers);
		tbtn_offers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(mStoreDetailModel.offer_id)) {
					Intent intent = new Intent(ActivityShopDetail.this, ActivityOfferDetail.class);
					intent.putExtra(Constants.SELECTED_OFFER_ID, Integer.parseInt(mStoreDetailModel.offer_id));
					startActivity(intent);
					ActivityShopDetail.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			}
		});
		tbtn_setfavourite = (ToggleButton) findViewById(R.id.tbtn_setfavourite);
		txt_likestoretitle = (TextView) findViewById(R.id.txt_likestoretitle);
		txt_likestorecontent = (TextView) findViewById(R.id.txt_likestorecontent);
		txt_similartitle = (TextView) findViewById(R.id.txt_similartitle);
		layer_container = (LinearLayout) findViewById(R.id.layer_container);

		layer_addhours = (LinearLayout) findViewById(R.id.layer_addhours);
		mOpenData.clear();

		img_favourite_big = (ImageView) findViewById(R.id.img_favourite_big);

		// set fonts
		FontUtils.setTypeface(txt_headertitle, FontUtils.font_HelveticaNeueUltraLight, false);
		FontUtils.setTypeface(txt_shoptitle, FontUtils.font_HelveticaNeueThin, false);
		//FontUtils.setTypeface(txt_shoplocation, FontUtils.font_HelveticaNeueMedium, true);
		FontUtils.setTypeface(txt_description, FontUtils.font_HelveticaNeueThin, true);
		FontUtils.setTypeface(tbtn_map, FontUtils.font_HelveticaNeueThin, true);
		FontUtils.setTypeface(tbtn_phonecall, FontUtils.font_HelveticaNeueThin, true);
		FontUtils.setTypeface(tbtn_laptop, FontUtils.font_HelveticaNeueThin, true);
		FontUtils.setTypeface(tbtn_open_hours, FontUtils.font_HelveticaNeueThin, true);
		FontUtils.setTypeface(tbtn_offers, FontUtils.font_HelveticaNeueThin, true);
		FontUtils.setTypeface(txt_likestoretitle, FontUtils.font_HelveticaNeueThin, true);
		FontUtils.setTypeface(txt_likestorecontent, FontUtils.font_HelveticaNeueThin, true);
		FontUtils.setTypeface(txt_similartitle, FontUtils.font_HelveticaNeueThin, true);

		BaseTask task = new BaseTask(Constants.TASK_GET_STORE_DETAIL);
		task.setListener(mTaskListener);
		task.execute();

		dialogWait.show();
	}

	private void addOpenningTime(String strDay, String value) {
		String[] strs = value.split("-");
		if (strs.length == 2) { // open
			mOpenData.add(new Store_open_model(strDay, strs[0], strs[1], false));
		}
		else { // close
			mOpenData.add(new Store_open_model(strDay, "", "", true));
		}
	}

	private void updateUIs() {
		
		if(mStoreDetailModel == null)
			return;
		
		StoreNameDAO myStore = DrakeCircusApplication.getInstance().dbHelper.getOneStore(mStoreId);
		
		if (!TextUtils.isEmpty(mStoreDetailModel.name))
			txt_shoptitle.setText(mStoreDetailModel.name);
		else
			txt_shoptitle.setText(getResources().getString(R.string.strshop_title1));
		//txt_shoplocation.setText(mStoreDetailModel.location);
		txt_description.setText(mStoreDetailModel.text);

		if (!TextUtils.isEmpty(mStoreDetailModel.logo))
			DCImageLoader.showImage(img_shoplogo, mStoreDetailModel.logo);

		if (!TextUtils.isEmpty(mStoreDetailModel.image))
			DCImageLoader.showImage(img_shopphoto, mStoreDetailModel.image);
		else
			img_shopphoto.setVisibility(View.GONE);

		if (mStoreDetailModel.unit_num == null || mStoreDetailModel.unit_num.length() == 0)
			tbtn_map.setVisibility(View.GONE);
		
		tbtn_phonecall.setText("Tap to call store: " + mStoreDetailModel.phone);
		
		if (mStoreDetailModel.phone == null || mStoreDetailModel.phone.length() == 0)
			tbtn_phonecall.setVisibility(View.GONE);

		if (mStoreDetailModel.url == null || mStoreDetailModel.url.length() == 0)
			tbtn_laptop.setVisibility(View.GONE);
		
		// Openning Hours
		mOpenData.clear();
		OpenModel openTime = mStoreDetailModel.open;
		if (openTime != null) {
			addOpenningTime("Monday", openTime.mon);
			addOpenningTime("Tuesday", openTime.tue);
			addOpenningTime("Wednesday", openTime.wed);
			addOpenningTime("Thursday", openTime.thu);
			addOpenningTime("Friday", openTime.fri);
			addOpenningTime("Saturday", openTime.sat);
			addOpenningTime("Sunday", openTime.sun);
		}

		for (Store_open_model model : mOpenData) {
			View convertView = getLayoutInflater().inflate(R.layout.row_openhour, null);
			TextView txt_day = (TextView) convertView.findViewById(R.id.txt_day);
			TextView txt_opentime = (TextView) convertView.findViewById(R.id.txt_opentime);
			TextView txt_closetime = (TextView) convertView.findViewById(R.id.txt_closetime);
			TextView txt_during = (TextView) convertView.findViewById(R.id.txt_during);

			txt_day.setText(model.mDay);
			if (!model.mBRest) {
				txt_opentime.setText(model.mOpenTime);
				txt_closetime.setText(model.mCloseTime);
			}
			else {
				txt_opentime.setText("Closed");
				txt_during.setVisibility(View.INVISIBLE);
				txt_closetime.setVisibility(View.INVISIBLE);
			}

			FontUtils.setTypeface(txt_day, FontUtils.font_HelveticaNeueThin, false);
			FontUtils.setTypeface(txt_opentime, FontUtils.font_HelveticaNeueThin, false);
			FontUtils.setTypeface(txt_closetime, FontUtils.font_HelveticaNeueThin, false);

			layer_addhours.addView(convertView);
		}
		
		/*****************************
		 * Offers available
		 *****************************/
		if(TextUtils.isEmpty(mStoreDetailModel.offer_id)) {
			tbtn_offers.setText(R.string.strshopdetail_no_offer);
			tbtn_offers.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_detail_offers), null, null, null);
			tbtn_offers.setVisibility(View.GONE);
		} else {
			tbtn_offers.setText(R.string.strshopdetail_offers_available);
			tbtn_offers.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_detail_offers), null, getResources().getDrawable(R.drawable.icon_arrow), null);
			tbtn_offers.setVisibility(View.VISIBLE);
		}
		
		/*****************************
		 * Like this Store?
		 *****************************/
		if (myStore != null) {
			if (myStore.favourite == 0)
				tbtn_setfavourite.setChecked(false);
			else if (myStore.favourite == 1)
				tbtn_setfavourite.setChecked(true);
		}
		tbtn_setfavourite.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				DrakeCircusApplication.getInstance().dbHelper.updateStoreFavorites(mStoreId, isChecked ? 1 : 0);

				if (isChecked) {
					img_favourite_big.setVisibility(View.VISIBLE);
					FadeInAnimator anim = new FadeInAnimator();
					anim.setTarget(img_favourite_big);
					anim.setDuration(Constants.FADING_TIMEOUT);
					anim.animate();

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							FadeOutAnimator anim = new FadeOutAnimator();
							anim.setTarget(img_favourite_big);
							anim.setDuration(Constants.FADING_TIMEOUT);
							anim.animate();
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									img_favourite_big.setVisibility(View.GONE);
								}
							}, Constants.FADING_TIMEOUT);
						}
					}, Constants.SHOWFAVOURITE_TIMEOUT);
				}
			}
		});
		/*****************************
		 * Similar Stores
		 *****************************/
		int sSize = mStoreDetailModel.similar_stores.size();
		if (sSize == 0) {
			txt_similartitle.setVisibility(View.GONE);
		}
		
		for (int i = 0; i < sSize; i ++) {
			
			StoreModel storeModel = mStoreDetailModel.similar_stores.get(i);
			
			StoreNameDAO storeNameDao = DrakeCircusApplication.getInstance().dbHelper.getOneStore(storeModel.id);
			
			View convertView = getLayoutInflater().inflate(R.layout.row_stores3, null);
			convertView.setTag(storeNameDao);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					StoreNameDAO storeNameDao = (StoreNameDAO)v.getTag();
					
					Intent intent = new Intent(ActivityShopDetail.this, ActivityShopDetail.class);
					intent.putExtra(Constants.SELECTED_STORE_ID, storeNameDao.id);
					startActivity(intent);
					overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			});
			RelativeLayout tbl_data_layer = (RelativeLayout) convertView.findViewById(R.id.tbl_data_layer);
			ImageView img_favourite = (ImageView) convertView.findViewById(R.id.img_favourite);
			TextView txt_title = (TextView) convertView.findViewById(R.id.txt_title);
			final ImageView img_favourite_show = (ImageView) convertView.findViewById(R.id.img_favourite_show);

			if ((i % 2) == 0)
				tbl_data_layer.setBackgroundResource(R.color.catelist_bg1);
			else
				tbl_data_layer.setBackgroundResource(R.color.catelist_bg2);

			if (storeNameDao.favourite == 1) {
				if(storeNameDao.hasOffer == 0)
					img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_on));
				else
					img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_on_plusoffer));
			} else {
				if(storeNameDao.hasOffer == 0)
					img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_off));
				else
					img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_off_plusoffer));
			}

			img_favourite.setTag(storeNameDao.id);
			img_favourite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int smiliarStoreId = Integer.parseInt(v.getTag().toString());
					
					StoreNameDAO storeInfo = DrakeCircusApplication.getInstance().dbHelper.getOneStore(smiliarStoreId);
					
					if(storeInfo.favourite == 1)
						storeInfo.favourite = 0;
					else if(storeInfo.favourite == 0)
						storeInfo.favourite = 1;
					
					int resId = R.drawable.icon_favourite_on;
					if(storeInfo.favourite == 1) {
						if(storeInfo.hasOffer == 0)
							resId = R.drawable.icon_favourite_on;
						else
							resId = R.drawable.icon_favourite_on_plusoffer;
					} else {
						if(storeInfo.hasOffer == 0)
							resId = R.drawable.icon_favourite_off;
						else
							resId = R.drawable.icon_favourite_off_plusoffer;
					}
					((ImageView)v).setImageDrawable(getResources().getDrawable(resId));
					
					if (storeInfo.favourite == 1) {
						img_favourite_show.setVisibility(View.VISIBLE);
						FadeInAnimator anim = new FadeInAnimator();
						anim.setTarget(img_favourite_show);
						anim.setDuration(Constants.FADING_TIMEOUT);
						anim.animate();

						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								FadeOutAnimator anim = new FadeOutAnimator();
								anim.setTarget(img_favourite_show);
								anim.setDuration(Constants.FADING_TIMEOUT);
								anim.animate();
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										img_favourite_show.setVisibility(View.GONE);
									}
								}, Constants.FADING_TIMEOUT);
							}
						}, Constants.SHOWFAVOURITE_TIMEOUT);
					}

					
					DrakeCircusApplication.getInstance().dbHelper.updateStoreFavorites(smiliarStoreId, storeInfo.favourite);
				}
			});

			FontUtils.setTypeface(txt_title, FontUtils.font_HelveticaNeueLight, false);
			txt_title.setText(storeNameDao.name);
			
			layer_container.addView(convertView);
		}

		scroll_main.setVisibility(View.VISIBLE);
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
			if (ActivityMain.instance != null)
				ActivityMain.instance.selectMainMenu();
    	}
    }

	public void onOpenHours(View paramView) {
		ToggleButton btn = (ToggleButton) paramView;
		if (btn.isChecked())
			layer_addhours.setVisibility(View.VISIBLE);
		else
			layer_addhours.setVisibility(View.GONE);
	}
	
	TaskListener mTaskListener = new TaskListener() {
		@Override
		public Object onTaskRunning(int taskId, Object data) {
			Object result = null;
			if (taskId == Constants.TASK_GET_STORE_DETAIL) {
				result = Server.GetStoreDetails(mStoreId);
			}
			return result;
		}
		
		@Override
		public void onTaskResult(int taskId, Object result) {
			if (taskId == Constants.TASK_GET_STORE_DETAIL) {
				if (result != null) {
					if (result instanceof StoreDetail) {
						StoreDetail res_model = (StoreDetail) result;
						if (res_model.status.equalsIgnoreCase("ok")) {
							mStoreDetailModel = res_model.result;
							updateUIs();
						}
					}
				}
			}
			
			dialogWait.hide();
		}
		
		@Override
		public void onTaskProgress(int taskId, Object... values) {
			
		}
		
		@Override
		public void onTaskPrepare(int taskId, Object data) {
		}
		
		@Override
		public void onTaskCancelled(int taskId) {
			dialogWait.hide();
		}
	};
}
