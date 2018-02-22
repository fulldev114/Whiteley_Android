package com.wai.whiteley.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.fading_entrances.FadeInAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutAnimator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.R;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync.StatisticType;
import com.wai.whiteley.base.BaseFragmentActivity;
import com.wai.whiteley.base.BaseTask;
import com.wai.whiteley.base.BaseTask.TaskListener;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.http.ResponseModel.OfferDetail;
import com.wai.whiteley.http.ResponseModel.OfferDetailModel;
import com.wai.whiteley.http.ResponseModel.OfferModel;
import com.wai.whiteley.http.ResponseModel.OfferRedeemModel;
import com.wai.whiteley.http.ResponseModel.StoreModel;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.service.BeaconDetectService;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.DCImageLoader;
import com.wai.whiteley.util.FontUtils;

public class ActivityOfferDetail extends BaseFragmentActivity {

	public static ActivityOfferDetail instance = null;

	private ScrollView scroll_main;
	private TextView txt_headertitle;
	private TextView txt_offertitle;
	private TextView txt_description;
	private TextView txt_moreoffer;
	private ImageView img_offerphoto;
	private ImageView img_redeem;
	private LinearLayout viewRetailerShop;
	private LinearLayout viewRedeemButton;
	private Button tbtn_laptop;
	private LinearLayout layer_container;
	private LinearLayout layer_main;
	private Button btnRedeem;
	private int mOfferId;
	private OfferDetailModel mOfferDetailModel;
	private OfferRedeemModel RedeemModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offerdetail);

		instance = this;

		if(getIntent().hasExtra(Constants.EXTRA_BEACON_UUID)) {
			String uuid = getIntent().getStringExtra(Constants.EXTRA_BEACON_UUID);
			int major = getIntent().getIntExtra(Constants.EXTRA_BEACON_MAJOR, 0);
			int minor = getIntent().getIntExtra(Constants.EXTRA_BEACON_MINOR, 0);
			new StatisticsBeaconAsync(this, StatisticType.STATISTIC_NOTIFICATION_INTERACTION, uuid, major, minor).execute();
		}
		
		mOfferId = getIntent().getIntExtra(Constants.SELECTED_OFFER_ID, 0);
		scroll_main = (ScrollView) findViewById(R.id.scroll_main);
		scroll_main.setVisibility(View.GONE);
		String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
		txt_headertitle = (TextView) findViewById(R.id.txt_headertitle);
		txt_headertitle.setText(strTitles[Constants.SELECT_CASE_LATESTOFFER]);
		img_offerphoto = (ImageView)findViewById(R.id.img_offerphoto);
		txt_offertitle = (TextView) findViewById(R.id.txt_offertitle);
		txt_description = (TextView) findViewById(R.id.txt_description);
		txt_moreoffer = (TextView) findViewById(R.id.txt_moreoffer);
		viewRedeemButton = (LinearLayout) findViewById(R.id.view_redeem);
		viewRetailerShop = (LinearLayout) findViewById(R.id.view_retailer);
		img_redeem = (ImageView) findViewById(R.id.back_redeem_top);
		
		tbtn_laptop = (Button) findViewById(R.id.tbtn_laptop);
		tbtn_laptop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOfferDetailModel == null)
					return;
				
				int screenSize = DrakeCircusApplication.getInstance().mScreenHistory.size();
				if(screenSize > Constants.MAP_ACTIVITY_LIMIT) {
			    	for(int i = 0; i < screenSize - Constants.MAP_ACTIVITY_LIMIT; i++) {
			    		Activity screen = DrakeCircusApplication.getInstance().mScreenHistory.get(i);
			    		DrakeCircusApplication.getInstance().mScreenHistory.remove(screen);
			    		screen.finish();
			    	}
				}
				
				if(mOfferDetailModel.retailers != null && mOfferDetailModel.retailers.size() > 0) {
					Intent intent = new Intent(ActivityOfferDetail.this, ActivityCentreMap.class);
					intent.putExtra(ActivityCentreMap.EXTRA_KEY_LOCATION, mOfferDetailModel.retailers.get(0).location);
					intent.putExtra(ActivityCentreMap.EXTRA_KEY_UNITNUM, mOfferDetailModel.retailers.get(0).unit_num);
					startActivity(intent);
				}
			}
		});
		layer_container = (LinearLayout) findViewById(R.id.layer_container);
		layer_main = (LinearLayout) findViewById(R.id.layer_main);

		// set fonts
		FontUtils.setTypeface(txt_headertitle, FontUtils.font_HelveticaNeueUltraLight, false);
		FontUtils.setTypefaceAllView(layer_main, FontUtils.font_HelveticaNeueThin);
		FontUtils.setTypeface(txt_offertitle, FontUtils.font_HelveticaNeue, true);

		BaseTask task = new BaseTask(Constants.TASK_GET_OFFERDETAIL);
		task.setListener(mTaskListener);
		task.execute();

		dialogWait.show();
	}

	private void updateUIs() {
		if(mOfferDetailModel == null)
			return;
		
		if (!TextUtils.isEmpty(mOfferDetailModel.image))
			DCImageLoader.showImage(img_offerphoto, mOfferDetailModel.image);
		if (!TextUtils.isEmpty(mOfferDetailModel.name))
			txt_offertitle.setText(mOfferDetailModel.name);
		if (!TextUtils.isEmpty(mOfferDetailModel.text))
			txt_description.setText(mOfferDetailModel.text);
		/*
		 *  Redeem
		 */
		RedeemModel = mOfferDetailModel.add_button;
		
		if (RedeemModel != null && RedeemModel.enabled.equals("1")) {
			btnRedeem = new Button(this);
			btnRedeem.setBackgroundResource(R.color.header_bg);
			btnRedeem.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.margin_small));
			btnRedeem.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			int padding = getResources().getDimensionPixelSize(R.dimen.margin_normal);
			btnRedeem.setPadding(padding, padding, padding, padding);
			btnRedeem.setTextColor(getResources().getColor(android.R.color.white));
			btnRedeem.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.fontsize4));
			btnRedeem.setBackgroundResource(R.color.color_yellow);

			String title = "";

			if ( RedeemModel.type.equals("redeem")) {
				btnRedeem.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_offer_r), null, null, null);

				if ( RedeemModel.redeemed.equals("0")) {
					title = "Click here to redeem offer";
				}
				else {
					title = "Offer claimed. Go to store.";
					btnRedeem.setBackgroundResource(R.color.color_gray);
				}
			}
			else {
				btnRedeem.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_offer_c), null, null, null);
				title = RedeemModel.text;
			}
			
			btnRedeem.setText(title);
			btnRedeem.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
					if ( RedeemModel.type.equals("redeem") ) {
						if ( RedeemModel.redeemed.equals("0")) {
							btnRedeem.setText("Offer claimed. Go to store.");
							btnRedeem.setBackgroundResource(R.color.color_gray);
							
							BaseTask task = new BaseTask(Constants.TASK_SET_OFFER_REDEEM);
							task.setListener(mTaskListener);
							task.execute();
							
							RedeemModel.redeemed = "1";
						}
						else {
							showRedeemMessage();
						}
							
					}
					else {
						
						BaseTask task = new BaseTask(Constants.TASK_SET_OFFER_REDEEM);
						task.setListener(mTaskListener);
						task.execute();
						
						Intent intent = new Intent(ActivityOfferDetail.this, ActivityWebView.class);
						intent.putExtra(ActivityWebView.EXTRA_URL, RedeemModel.link);
						intent.putExtra(ActivityWebView.EXTRA_TITLE, "Offer Detail");
						startActivity(intent);
						overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
				}
			});
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			//params.topMargin = padding;
			viewRedeemButton.addView(btnRedeem, params);
			//FontUtils.setTypefaceAllView(viewRedeemButton, FontUtils.font_HelveticaNeueThin);
		}
		
		/*
		 * Retailer
		 */
		if (mOfferDetailModel.retailers != null) {
			for(StoreModel storeModel: mOfferDetailModel.retailers) {
				
				Button btnRetailer = new Button(this);
				btnRetailer.setBackgroundResource(R.color.header_bg);
				btnRetailer.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_stores), null, null, null);
				btnRetailer.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.margin_small));
				btnRetailer.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				int padding = getResources().getDimensionPixelSize(R.dimen.margin_normal);
				btnRetailer.setPadding(padding, padding, padding, padding);
				btnRetailer.setTextColor(getResources().getColor(android.R.color.white));
				btnRetailer.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.fontsize4));
				btnRetailer.setText(storeModel.name);
				btnRetailer.setTag(storeModel.id);
				btnRetailer.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int storeId = Integer.parseInt(v.getTag().toString());
						
						Intent intent = new Intent(ActivityOfferDetail.this, ActivityShopDetail.class);
						intent.putExtra(Constants.SELECTED_STORE_ID, storeId);
						startActivity(intent);
						overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
				});
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				//params.topMargin = padding;
				
				viewRetailerShop.addView(btnRetailer, params);
				FontUtils.setTypefaceAllView(viewRetailerShop, FontUtils.font_HelveticaNeueThin);
			}
			
			if ( mOfferDetailModel.retailers.size() == 0 ) {
				tbtn_laptop.setVisibility(View.GONE);
			}
		}
		/*
		 * Great Offer
		 */
		if (mOfferDetailModel.great_offers != null) {
			int gSize = mOfferDetailModel.great_offers.size();
			
			if (gSize == 0) {
				txt_moreoffer.setVisibility(View.GONE);
			}
			
			for (int i = 0; i < gSize; i ++) {
				
				OfferModel offerModel = mOfferDetailModel.great_offers.get(i);
				
				View convertView = getLayoutInflater().inflate(R.layout.row_offers, null);
				convertView.setTag(offerModel.id);
				convertView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int offerId = Integer.parseInt(v.getTag().toString());
						
						Intent intent = new Intent(ActivityOfferDetail.this, ActivityOfferDetail.class);
						intent.putExtra(Constants.SELECTED_OFFER_ID, offerId);
						startActivity(intent);
						ActivityOfferDetail.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
				});
				LinearLayout tbl_data_layer = (LinearLayout) convertView.findViewById(R.id.tbl_data_layer);
				ImageView img_offer = (ImageView) convertView.findViewById(R.id.img_offer);
				TextView txt_title = (TextView) convertView.findViewById(R.id.txt_title);
				TextView txt_content1 = (TextView) convertView.findViewById(R.id.txt_content1);
				TextView txt_content2 = (TextView) convertView.findViewById(R.id.txt_content2);
	
				FontUtils.setTypeface(txt_title, FontUtils.font_HelveticaNeueThin, false);
				FontUtils.setTypeface(txt_content1, FontUtils.font_HelveticaNeueMedium, true);
				FontUtils.setTypeface(txt_content2, FontUtils.font_HelveticaNeueThin, false);
	
				if ((i % 2) == 0)
					tbl_data_layer.setBackgroundResource(R.color.catelist_bg2);
				else
					tbl_data_layer.setBackgroundResource(R.color.catelist_bg1);
	
				txt_title.setText("" + offerModel.shop_name);
				txt_content1.setText("" + offerModel.offer_name);
				txt_content2.setText("" + offerModel.offer_detail);
				if (!TextUtils.isEmpty(offerModel.offer_image))
					DCImageLoader.showImage(img_offer, offerModel.offer_image);
				
				layer_container.addView(convertView);
			}
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

    void showRedeemMessage() {
    	
    	if ( img_redeem.getVisibility() == View.VISIBLE )
    		return;
    	
		//img_redeem.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_outofcentre));
		img_redeem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		img_redeem.setVisibility(View.VISIBLE);
		FadeInAnimator anim = new FadeInAnimator();
		anim.setTarget(img_redeem);
		anim.setDuration(Constants.SHOWREDEEM_TIMEOUT);
		anim.animate();
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				FadeOutAnimator anim = new FadeOutAnimator();
				anim.addAnimatorListener(new AnimatorListener() {
					
					@Override
					public void onAnimationStart(Animator arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animator arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animator arg0) {
						img_redeem.setVisibility(View.GONE);
					}
					
					@Override
					public void onAnimationCancel(Animator arg0) {
						img_redeem.setVisibility(View.GONE);
					}
				});
				anim.setTarget(img_redeem);
				anim.setDuration(Constants.SHOWREDEEM_TIMEOUT);
				anim.animate();
			}
		}, Constants.SHOWREDEEM_TIMEOUT);
	}
    
	TaskListener mTaskListener = new TaskListener() {
		@Override
		public Object onTaskRunning(int taskId, Object data) {
			Object result = null;
			String strDeviceToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);

			if (taskId == Constants.TASK_GET_OFFERDETAIL) {
				result = Server.GetOfferDetail(strDeviceToken, mOfferId);
			}
			else if (taskId == Constants.TASK_SET_OFFER_REDEEM) {
				result = Server.SetOfferRedeem(strDeviceToken, mOfferId);
			}
			return result;
		}
		
		@Override
		public void onTaskResult(int taskId, Object result) {
			if (taskId == Constants.TASK_GET_OFFERDETAIL) {
				if (result != null) {
					if (result instanceof OfferDetail) {
						OfferDetail res_model = (OfferDetail) result;
						mOfferDetailModel = res_model.result;
						updateUIs();
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
