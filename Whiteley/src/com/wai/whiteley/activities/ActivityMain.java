package com.wai.whiteley.activities;

import java.io.IOException;
import java.io.StringReader;
import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.renderscript.Element;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshObservableScrollView;
import com.handmark.pulltorefresh.library.internal.ObservableScrollView;
import com.handmark.pulltorefresh.library.internal.ObservableScrollView.ScrollViewListener;
import com.radiusnetworks.ibeacon.IBeacon;
import com.viewpagerindicator.CirclePageIndicator;
import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.DrakeCircusWindow;
import com.wai.whiteley.R;
import com.wai.whiteley.asynctask.GetStoreCategoryAsync;
import com.wai.whiteley.asynctask.GetStoreNamesAsync;
import com.wai.whiteley.base.BaseFragmentActivity;
import com.wai.whiteley.config.AppPreferences;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.database.dao.StoreNameDAO;
import com.wai.whiteley.dialog.FeedbackDialogClass;
import com.wai.whiteley.http.HttpApi;
import com.wai.whiteley.http.ResponseModel.HomeCarouselResponse;
import com.wai.whiteley.http.ResponseModel.HomeCarouselResponse.HomeCarousel;
import com.wai.whiteley.http.ResponseModel.StoreCategoryModel;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.http.ServerConfig;
import com.wai.whiteley.model.AppModels.Store_model;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.FileUtil;
import com.wai.whiteley.util.FontUtils;
import com.wai.whiteley.util.GeolocationUtil;
import com.wai.whiteley.view.DCProgressDialog;
import com.wai.whiteley.view.NotificationPopup;

public class ActivityMain extends BaseFragmentActivity {

	public static ActivityMain instance = null;

	private AppPreferences mPrefs;
	private DCProgressDialog dialogWait;
	
	private Handler blurThread = null;
	
	private Button btnNotification;
	private TextView txtNotifyCount;
	private LinearLayout layer_main;
	private PullToRefreshObservableScrollView mPullRefreshScrollView;
	private ObservableScrollView mRealScrollView;
	
	private RelativeLayout viewGallery;
	private ViewPager mPager;
	private GalleryItemFragmentAdapter mAdapter;
	private ImageView imgCarouselOverlay;
	private LinearLayout mListLayer;
	
	private String currentVersion;
	private String latestVersion;

	private ArrayList<Store_model> mDataList = new ArrayList<Store_model>();
	ArrayList<HomeCarousel> mCarousels = new ArrayList<HomeCarousel>();
	
	public CountDownTimer mTimerUserGps;
	
	private LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		@Override
		public void onProviderEnabled(String provider) {}
		@Override
		public void onProviderDisabled(String provider) {}
	};
	
	GetStoreNamesAsync.OnCompleteListener initStoreNamesOnCompleteListener = new GetStoreNamesAsync.OnCompleteListener() {

		@Override
		public void onComplete(List<StoreNameDAO> data) {
			ActivitySplash.asyncGetStoreNames = null;
			showInitLoadingStatus();
		}
		
	};
	
	GetStoreCategoryAsync.OnCompleteListener initStoreCategoriesOnCompleteListener = new GetStoreCategoryAsync.OnCompleteListener() {

		@Override
		public void onComplete(List<StoreCategoryModel> data) {
			ActivitySplash.asyncGetStoreCategory = null;
			showInitLoadingStatus();
		}
		
	};
	
	private boolean bluetoothEnabled = false;
	private boolean gpsEnabled = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        mPrefs = DrakeCircusApplication.getInstance().mPrefs;
        dialogWait = new DCProgressDialog(this);

		initTimerUserGps();

		getCurrentVersion();
		
    	int openNum = mPrefs.getIntValue(Constants.APP_OPEN_NUM);

    	mPrefs.setIntValue(Constants.NEAR_BEACON_MAJOR, 0);
		mPrefs.setIntValue(Constants.NEAR_BEACON_MINOR, 0);
		
    	if (openNum < 0) {
    		openNum = 1;
    	}
    	else {
    		if (openNum == 2) {
    			new AlertDialog.Builder(ActivityMain.this)
    			.setIcon(R.drawable.ic_launcher)
    			.setTitle("Like our app?")
    			.setMessage("Spread the love and share it with your friends!")
    			.setNegativeButton("Not right now", new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					dialog.dismiss();
    				}
    			})
    			.setPositiveButton("Share", new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					dialog.dismiss();
    					Intent smsIntent = new Intent(Intent.ACTION_VIEW);
    			        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
    			        smsIntent.setType("vnd.android-dir/mms-sms");
    			        smsIntent.putExtra("address", "");
    			        smsIntent.putExtra("sms_body", "http://play.google.com/store/apps/details?id=" + getPackageName()); 
    			        
    			        try {
    			        	startActivity(smsIntent);
   					 	} catch (ActivityNotFoundException e) {
   					 		startActivity(new Intent(Intent.ACTION_VIEW,
   					             Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
   					 }
    				}
    			})
    			.create()
    			.show();
    		}
    		else if (openNum == 4) {
    			new AlertDialog.Builder(ActivityMain.this)
    			.setIcon(R.drawable.ic_launcher)
    			.setTitle("Like us? Rate us!")
    			.setMessage("Help us improve the Whiteley app by rating us!")
    			.setNegativeButton("Not right now", new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					dialog.dismiss();
    				}
    			})
    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					dialog.dismiss();
    					
    					 Uri uri = Uri.parse("market://details?id=" + getPackageName());
    					 Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
    					 // To count with Play market backstack, After pressing back button, 
    					 // to taken back to our application, we need to add following flags to intent. 
    					 goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
    					                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
    					                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    					 try {
    					     startActivity(goToMarket);
    					 } catch (ActivityNotFoundException e) {
    					     startActivity(new Intent(Intent.ACTION_VIEW,
    					             Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
    					 }

    					 String strUrl = ServerConfig.getServerUrl() + "?" + Server.HTTPREQUESTPARAM_REQUESTTYPE + "=" + Server.HTTPREQUESTPARAM_REQUESTTYPE_REGISTER_REVIEWS;
    					 HttpApi.sendGetRequest(strUrl);
       				}
    			})
    			.create()
    			.show();
    		}
    		openNum++;
    	}
    	mPrefs.setIntValue(Constants.APP_OPEN_NUM, openNum);
    		
    	// Feedback ask dialog
    	
    	String fb_sent = mPrefs.getStringValue(Constants.APP_FEEDBACK_ASK);
    	
    	if ( fb_sent.equals("1") ) {
    		showFeedbackAlertDialog();
    		mPrefs.setStringValue(Constants.APP_FEEDBACK, Constants.FEEDBACK_NOTIFY);
    	}
    	else {
			mPrefs.setStringValue(Constants.APP_FEEDBACK, null);
    	}
    	
        mPrefs.setIntValue(Constants.SELECTED_MENU_NUMBER, Constants.SELECT_CASE_HOME);

		// Buttons
		btnNotification = (Button) findViewById(R.id.button_notification);
        btnNotification.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NotificationPopup popup = new NotificationPopup(ActivityMain.this, bluetoothEnabled, gpsEnabled);
				popup.setOnActionItemClickListener(new NotificationPopup.OnActionItemClickListener() {
					@Override
					public void onItemClick(NotificationPopup source, View view) {
						
					}
				});

				popup.show(v);
			}
		});
    	txtNotifyCount = (TextView) findViewById(R.id.text_notify_count);
        layer_main = (LinearLayout) findViewById(R.id.layer_main);
        mPullRefreshScrollView = (PullToRefreshObservableScrollView) findViewById(R.id.scroll);
        mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ObservableScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ObservableScrollView> refreshView) {
				new GetHomeCarouselDataTask().execute();
			}
		});
        mRealScrollView = mPullRefreshScrollView.getRefreshableView();
        mRealScrollView.setScrollViewListener(new ScrollViewListener() {
			
			@Override
			public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
				if(mPager != null && y >= 0 && y < CommonUtil.convertDpToPixel(200, ActivityMain.this)) {
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewGallery.getLayoutParams();
					params.height = (int)CommonUtil.convertDpToPixel(200, ActivityMain.this) - y;
					viewGallery.setLayoutParams(params);
					
					if(y != 0) {
						imgCarouselOverlay.setVisibility(View.VISIBLE);
					} else {
						imgCarouselOverlay.setVisibility(View.GONE);
					}
					RelativeLayout.LayoutParams paramsOverlay = (RelativeLayout.LayoutParams)imgCarouselOverlay.getLayoutParams();
					paramsOverlay.height = (int)CommonUtil.convertDpToPixel(200, ActivityMain.this) - y;
					imgCarouselOverlay.setLayoutParams(paramsOverlay);
					
					float alpha = Math.max(0, Math.min(1, y / CommonUtil.convertDpToPixel(200, ActivityMain.this)));
					imgCarouselOverlay.setAlpha(alpha);
					
//					final int radius = Math.max(1, Math.min(15, (int)(15 * (y / CommonUtil.convertDpToPixel(200, ActivityMain.this)))));
//					if(blurThread == null) {
//						blurThread = new Handler();
//						blurThread.postDelayed(new Runnable() {
//							
//							@Override
//							public void run() {
//								try {
//									final Bitmap bitmap = Blur.fastblur(ActivityMain.this, ImageUtil.getBitmapFromView(viewGallery), radius);
//									if(bitmap != null) {
//										imgCarouselOverlay.setBackgroundDrawable(new BitmapDrawable(bitmap));
//									}
//								} catch(Exception e) {
//									e.printStackTrace();
//								}
//								blurThread = null;
//							}
//						}, 100);
//					}
				}
			}
		});
        
        viewGallery = (RelativeLayout)findViewById(R.id.view_gallery);
        mPager = (ViewPager)findViewById(R.id.pager);
        mAdapter = new GalleryItemFragmentAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        CirclePageIndicator mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.setSnap(true);
		
        imgCarouselOverlay = (ImageView)findViewById(R.id.img_carousel_overlay);
        imgCarouselOverlay.setVisibility(View.GONE);
        
        String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
		String[] strDescs = getResources().getStringArray(R.array.store_descarray);
		for (int i = 0; i < strTitles.length; i ++)
			mDataList.add(new Store_model(i, strTitles[i], strDescs[i]));

		mListLayer = (LinearLayout) findViewById(R.id.layer_lstdata);
		int funcIndex = -1;
		for (int i = 0; i < strTitles.length; i ++) {
			
			//if(i >= Constants.HOMEPAGE_LISTCOUNT && i < strTitles.length - 1)
			//	continue;
			if ( i == Constants.SELECT_CASE_EVENTS || 
				i == Constants.SELECT_CASE_TRAVEL ||
				i == Constants.SELECT_CASE_OPENING_HOURS ||
				i == Constants.SELECT_CASE_FEEDBACK )
				continue;
			
			funcIndex++;
			
			View convertView = getLayoutInflater().inflate(R.layout.row_stores1, null);
			LinearLayout tbl_data_layer = (LinearLayout) convertView.findViewById(R.id.tbl_data_layer);
			ImageView img_store = (ImageView) convertView.findViewById(R.id.img_store);
			TextView txt_title1 = (TextView) convertView.findViewById(R.id.txt_title1);
			TextView txt_title2 = (TextView) convertView.findViewById(R.id.txt_title2);
			TextView txt_desc = (TextView) convertView.findViewById(R.id.txt_desc);

			convertView.setTag(i);

			if ((funcIndex % 2) == 0)
				tbl_data_layer.setBackgroundResource(R.color.mainlist_bg1);
			else
				tbl_data_layer.setBackgroundResource(R.color.mainlist_bg2);

			Store_model data = mDataList.get(i);
			String strTitle = data.mTitle;
			String[] strTitleData = strTitle.split(" ");
			txt_title1.setText(strTitleData[0]);
			txt_title2.setText(strTitleData[1]);
			txt_desc.setText(data.mDesc);

			switch (data.icon_type) {
			case Constants.SELECT_CASE_OURSTORE:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_stores));
				break;
			case Constants.SELECT_CASE_CENTREMAP:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_map));
				break;
			case Constants.SELECT_CASE_LATESTOFFER:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_offers));
				break;
			case Constants.SELECT_CASE_FOOD:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_food));
				break;
			case Constants.SELECT_CASE_CINEMA:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_cinema));
				break;
			case Constants.SELECT_CASE_ROCKUP:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_rockup));
				break;
			case Constants.SELECT_CASE_TRAVEL:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_car));
				break;
			case Constants.SELECT_CASE_PARKING:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_parking));
				break;
			case Constants.SELECT_CASE_EVENTS:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_events));
				break;
			case Constants.SELECT_CASE_OPENING_HOURS:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_giftcard));
				break;
			//case Constants.SELECT_CASE_MONSTER:
			//	img_store.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_monster));
			//	break;
			case Constants.SELECT_CASE_SIGNUP:
				img_store.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_signup));
				break;
			default:
				break;
			}

			FontUtils.setTypeface(txt_title1, FontUtils.font_HelveticaNeueUltraLight, false);
			FontUtils.setTypeface(txt_title2, FontUtils.font_HelveticaNeue, true);
			FontUtils.setTypeface(txt_desc, FontUtils.font_Novecentowide_DemiBold, true);

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int pos = (Integer) v.getTag();

					mPrefs.setIntValue(Constants.SELECTED_MENU_NUMBER, pos);
					
					if (pos == Constants.SELECT_CASE_OURSTORE) {
						Intent intent = new Intent(ActivityMain.this, ActivityOurStores.class);
						startActivity(intent);
						ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
					else if (pos == Constants.SELECT_CASE_CENTREMAP) {
						Intent intent = new Intent(ActivityMain.this, ActivityCentreMap.class);
						startActivity(intent);
						ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
					else if (pos == Constants.SELECT_CASE_LATESTOFFER) {
						Intent intent = new Intent(ActivityMain.this, ActivityLatestOffers.class);
						startActivity(intent);
						ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
					else if (pos == Constants.SELECT_CASE_FOOD) {
						ArrayList<StoreCategoryModel> categories = DrakeCircusApplication.getInstance().dbHelper.getAllStoreCategories();
						for (StoreCategoryModel model : categories) {
							if (model.name.contains("Food")) {
								Intent intent = new Intent(ActivityMain.this, ActivitySearchinCategory.class);
								intent.putExtra(Constants.SELECTED_STORE_CATEGORY, "Food Outlets");
								intent.putExtra(Constants.SELECTED_STORE_CATEGORYID, model.id);
								startActivity(intent);
								ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
								break;
							}
						}
					}
					else if (pos == Constants.SELECT_CASE_CINEMA) {
						Intent intent = new Intent(ActivityMain.this, ActivityWebView.class);
						intent.putExtra(ActivityWebView.EXTRA_URL, "http://www1.cineworld.co.uk/cinemas/whiteley");
						String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
						intent.putExtra(ActivityWebView.EXTRA_TITLE, strTitles[Constants.SELECT_CASE_CINEMA]);
						startActivity(intent);
						ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
					else if (pos == Constants.SELECT_CASE_ROCKUP) {
						Intent intent = new Intent(ActivityMain.this, ActivityWebView.class);
						intent.putExtra(ActivityWebView.EXTRA_URL, "http://www.rock-up.co.uk/book-online");
						String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
						intent.putExtra(ActivityWebView.EXTRA_TITLE, strTitles[Constants.SELECT_CASE_ROCKUP]);
						startActivity(intent);
						ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
					else if (pos == Constants.SELECT_CASE_TRAVEL) {
						Intent intent = new Intent(ActivityMain.this, ActivityGettingHere.class);
						startActivity(intent);
						ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
					else if (pos == Constants.SELECT_CASE_PARKING) {
						Intent intent = new Intent(ActivityMain.this, ActivityCentreMap.class);
						intent.putExtra(Constants.SHOW_FACILITIES, true);
						startActivity(intent);
						ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
					//else if (pos == Constants.SELECT_CASE_MONSTER) {
					//	Intent intent = new Intent(ActivityMain.this, ActivityMonsterStart.class);
					//	startActivity(intent);
					//	ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					//}
					else if (pos == Constants.SELECT_CASE_SIGNUP) {
						Intent intent = new Intent(ActivityMain.this, ActivityWebView.class);
						intent.putExtra(ActivityWebView.EXTRA_URL, "http://eepurl.com/JEbsb");
						String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
						intent.putExtra(ActivityWebView.EXTRA_TITLE, strTitles[Constants.SELECT_CASE_SIGNUP]);
						startActivity(intent);
						ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
					else {
						//Toast.makeText(ActivityMain.this, "Not implemented...", Toast.LENGTH_LONG).show();
					}

					registerSection(pos);
				}
			});

			mListLayer.addView(convertView);
		}

		if(ActivitySplash.asyncGetStoreNames != null) {
			ActivitySplash.asyncGetStoreNames.setOnCompleteListener(initStoreNamesOnCompleteListener);	
		}
		if(ActivitySplash.asyncGetStoreCategory != null) {
			ActivitySplash.asyncGetStoreCategory.setOnCompleteListener(initStoreCategoriesOnCompleteListener);	
		}
		
		new GetHomeCarouselDataTask().execute();
		
		showInitLoadingStatus();
		
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(!gpsEnabled || !bluetoothEnabled) {
					NotificationPopup popup = new NotificationPopup(ActivityMain.this, bluetoothEnabled, gpsEnabled);
					popup.show(btnNotification);
				}
			}
		}, 1000);
        
    }

	@Override
	protected void onResume() {
		super.onResume();

		// Verify Device Status
		verifyDeviceStatus();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void getCurrentVersion(){
		PackageManager pm = this.getPackageManager();
		PackageInfo pInfo = null;
		
		try {
		    pInfo =  pm.getPackageInfo(this.getPackageName(),0);
		
		} catch (PackageManager.NameNotFoundException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		currentVersion = pInfo.versionName;

	    new GetLatestAppVersion().execute();

	}
	
	private void showInitLoadingStatus() {
    	if((ActivitySplash.asyncGetStoreNames != null && 
			ActivitySplash.asyncGetStoreNames.getStatus() == Status.RUNNING) 
			||
			(ActivitySplash.asyncGetStoreCategory != null && 
			ActivitySplash.asyncGetStoreCategory.getStatus() == Status.RUNNING)) {
			
    		dialogWait.show();
		} else {
			dialogWait.hide();
		}
    }
	
	private void initTimerUserGps() {
		setUserGps(); // initially register gps info
		mTimerUserGps = new CountDownTimer(Constants.PERIOD_SET_USER_GPS, Constants.PERIOD_SET_USER_GPS) {
			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				setUserGps();
				start();
			}
		};
		mTimerUserGps.start();
	}
	
	
	private void setUserGps() {
		Location location = null;
		String deviceToken;

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Server.CheckNotification();
				return null;
			}
		}.execute(null, null, null);
		
		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
		
		if ( bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
			String strDeviceToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);
			Server.SetUserBeacon(strDeviceToken, "", 0, 0);
		}


		// Ensures Bluetooth is enabled on the device. If Bluetooth is not currently enabled,
		// fire an intent to display a dialog asking the user to grant permission to enable it.
		
		bluetoothEnabled = bluetoothAdapter.isEnabled();
		
		long lastShopppingTime = mPrefs.getLongValue(Constants.LAST_SHOPPING_TIME);

		long diffTime = System.currentTimeMillis() - lastShopppingTime;
		
		if (diffTime > Constants.PERIOD_FEEDBACK_TIME && lastShopppingTime > 0) {
					
			String fb = mPrefs.getStringValue(Constants.APP_FEEDBACK);
			mPrefs.setLongValue(Constants.LAST_SHOPPING_TIME, 0);

 			if ( fb.equals(Constants.FEEDBACK_NOTIFY) || fb.equals(Constants.FEEDBACK_PAGE))
				return;
			
			mPrefs.setStringValue(Constants.APP_FEEDBACK, Constants.FEEDBACK_NOTIFY);
			
			DrakeCircusWindow currentWindow = DrakeCircusApplication.getInstance().getCurrentWindow();
			
			if (currentWindow == null) {
				
				int notifyId = mPrefs.getIntValue(Constants.NOTIFY_ID);
				notifyId++;
				
				Intent intent = new Intent(this, ActivityMain.class);
				intent.putExtra(Constants.KEY_FEEDBACK, true);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

				PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
				NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
				Notification notification = builder.setWhen(0)
					.setAutoCancel(true)
					.setContentTitle("Whiteley")
					.setContentText("How was your visit to Whiteley?")
					.setStyle(inboxStyle)
					.setTicker("Whiteley")
					.setContentIntent(pendingIntent)
					.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
					.setSmallIcon(R.drawable.ic_launcher)
					.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
					.build();
				//notification.flags |= Notification.FLAG_NO_CLEAR;

				NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(notifyId, notification);
			}
			else {
				showFeedbackAlertDialog();
			}
		}
		
		deviceToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);
		if (deviceToken.length() == 0)
			return;

		location = GeolocationUtil.getlocation(this, mLocationListener);

		if (location != null) {
			new AsyncTask<Location, Void, Void>() {
				@Override
				protected Void doInBackground(Location... params) {
					
					Location locat;
					double longitude, p_longitude;
					double latitude, p_latitude;
					String devToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);
	    
					Date curDate = new Date();    
				    SimpleDateFormat hourfmt = new SimpleDateFormat("HH");
					int hours = Integer.parseInt(hourfmt.format(curDate));
					
					//if ( ( hours >= 0 && hours < 8 ) || (hours > 20 && hours < 24)) 
					//	return null;
					
					locat = (Location)params[0];
					longitude = locat.getLongitude();
					latitude = locat.getLatitude();
					
					p_longitude = (double)mPrefs.getFloatValue(Constants.GPS_LONGITUDE);
					p_latitude =  (double)mPrefs.getFloatValue(Constants.GPS_LATITUDE);
					
					if ( mPrefs.abs( (float)(longitude - p_longitude) ) > 0.0000 && 
							mPrefs.abs( (float)(latitude - p_latitude) ) > 0.0000) {
						Server.SetUserGps(devToken, longitude, latitude);
						mPrefs.setFloatValue(Constants.GPS_LONGITUDE, (float)longitude);
						mPrefs.setFloatValue(Constants.GPS_LATITUDE, (float)latitude);
						
						String isVisited = mPrefs.getStringValue(Constants.VISIT_STATUS);
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
						float userLongitude = (float)longitude;//mPrefs.abs((float)longitude);
						float userLatitude = (float)latitude;//mPrefs.abs((float)latitude);
						
						if (userLatitude >= Constants.MAP_RIGHT_LAT &&
								userLatitude <= Constants.MAP_LEFT_LAT &&
								userLongitude >= Constants.MAP_LEFT_LONG && 
								userLongitude <= Constants.MAP_RIGHT_LONG ) {
							
							if ( isVisited == null ) {
							    String strStartDate = dateFormat.format(curDate);
								mPrefs.setStringValue(Constants.VISIT_STATUS, "1");
								mPrefs.setStringValue(Constants.VISIT_START_TIME, strStartDate);
								mPrefs.setLongValue(Constants.LAST_SHOPPING_TIME, 0);
							}
						}
						else {
							if ( isVisited.equals("1")) {
								mPrefs.setIntValue(Constants.NEAR_BEACON_MAJOR, 0);
								mPrefs.setIntValue(Constants.NEAR_BEACON_MINOR, 0);
								
								long lastestFoundTime = System.currentTimeMillis();
								mPrefs.setLongValue(Constants.LAST_SHOPPING_TIME, lastestFoundTime);

								new AsyncTask<Void, Void, Void>() {
									@Override
									protected Void doInBackground(Void... params) {
										String strDeviceToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);
										Server.SetUserBeacon(strDeviceToken, "", 0, 0);
										return null;
									}
								}.execute(null, null, null);
								
								mPrefs.setStringValue(Constants.VISIT_STATUS, null);
								final String strStartDate = mPrefs.getStringValue(Constants.VISIT_START_TIME);
								Date startDate = new Date();
								try {
									startDate = dateFormat.parse(strStartDate);
							    } catch (Exception e) {
							        // TODO Auto-generated catch block
							        e.printStackTrace();
							    }
								
								if ( startDate != null ) {
									long difference = Math.abs(curDate.getTime() - startDate.getTime());
									difference = difference / 1000;
									
									int dwellHour = (int)(difference / (60 * 60));
									int dwellMin = (int)(difference / 60 - dwellHour * 60);
									
									String strDwell;
									if ( dwellHour == 0) {
										if (dwellMin == 0)
											return null;
										else
											strDwell = String.valueOf(dwellMin + "m");
									}
									else
										strDwell = String.valueOf(dwellHour) + "h " + String.valueOf(dwellMin + "m");
									
									final String strDwellDate = strDwell; 
									final String deviceToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);
									
									if (deviceToken.length() > 0) {
										
										new AsyncTask<Void, Void, Void>() {
											@Override
											protected Void doInBackground(Void... params) {
												Server.SendUserVisit(deviceToken, strStartDate, strDwellDate);
												return null;
											}
								    	}.execute(null, null, null);
									}
								}		
							}
						}						    
					}
		
					return null;
				}
			}.execute(location, null, null);
		}
	}
	
	public void showFeedbackAlertDialog() {
		new AlertDialog.Builder(ActivityMain.this)
		.setIcon(R.drawable.ic_launcher)
		.setTitle(R.string.str_fb_ask)
		.setMessage("Help us improve your shopping\nexperience by answering 3 short\nquestions about your visit today.")
		.setNegativeButton("Not right now", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				String fb_sent = mPrefs.getStringValue(Constants.APP_FEEDBACK_ASK);
				mPrefs.setStringValue(Constants.APP_FEEDBACK, null);

				if ( fb_sent.equals("1") ) 
					mPrefs.setStringValue(Constants.APP_FEEDBACK_ASK, null);
				else
					mPrefs.setStringValue(Constants.APP_FEEDBACK_ASK, "1");
				
				dialog.dismiss();
				
			}
		})
		.setPositiveButton("OK, let's go!", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				mPrefs.setStringValue(Constants.APP_FEEDBACK, Constants.FEEDBACK_PAGE);
				mPrefs.setStringValue(Constants.APP_FEEDBACK_ASK, null);
				dialog.dismiss();
				Intent intent = new Intent(ActivityMain.this, ActivityFeedback.class);
				startActivityForResult(intent, Constants.ACTIVITY_SELECT_FEEDBACK);
			}
		})
		.create()
		.show();
	}
	
	public void onMore(View paramView) {
		CommonUtil.makeBlurAndStartActivity(this, layer_main);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
    		selectMainMenu();
    	}
    	else if ( requestCode == Constants.ACTIVITY_SELECT_FEEDBACK ) {
			String fb = mPrefs.getStringValue(Constants.APP_FEEDBACK);
			
			if ( fb.equals(Constants.FEEDBACK_SEND) ) {
				FeedbackDialogClass feedback_dlg = new FeedbackDialogClass(this);
				feedback_dlg.show(); 
				mPrefs.setStringValue(Constants.APP_FEEDBACK, null);
			}
    	}
    }
	
	private void verifyDeviceStatus() {
		int notifyCount = 0;
		
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			
			new AlertDialog.Builder(ActivityMain.this)
			.setIcon(R.drawable.ic_launcher)
			.setTitle("Warning")
			.setMessage(R.string.ble_not_supported)
			.setPositiveButton("Close", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.create()
			.show();
			
			return;
		}

		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (bluetoothAdapter == null) {
			new AlertDialog.Builder(ActivityMain.this)
			.setIcon(R.drawable.ic_launcher)
			.setTitle("Warning")
			.setMessage(R.string.error_bluetooth_not_supported)
			.setPositiveButton("Close", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.create()
			.show();
			
			return;
		}

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not currently enabled,
		// fire an intent to display a dialog asking the user to grant permission to enable it.
		
		bluetoothEnabled = bluetoothAdapter.isEnabled();
		
		if (!bluetoothAdapter.isEnabled()) {
			notifyCount++;
//			new AlertDialog.Builder(ActivityMain.this)
//			.setIcon(R.drawable.ic_launcher)
//			.setTitle("Enable Bluetooth")
//			.setMessage(R.string.to_use_ibeacon)
//			.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
////					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////					startActivity(enableBtIntent);
//					
//					Intent intentOpenBluetoothSettings = new Intent();
//					intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS); 
//					startActivity(intentOpenBluetoothSettings);
//					
//					dialog.dismiss();
//				}
//			})
//			.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//				}
//			})
//			.create()
//			.show();
			
		}
		
		
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		try {
			gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if(!gpsEnabled)
			notifyCount++;
		
		if(notifyCount == 0) {
			btnNotification.setVisibility(View.GONE);
			txtNotifyCount.setVisibility(View.GONE);
		} else {
			btnNotification.setVisibility(View.VISIBLE);
			txtNotifyCount.setVisibility(View.VISIBLE);
		}
		
		txtNotifyCount.setText("" + notifyCount);
    }

    public void selectMainMenu() {
    	int screenSize = DrakeCircusApplication.getInstance().mScreenHistory.size();
    	for(int i = screenSize - 1; i >= 0; i--) {
    		Activity screen = DrakeCircusApplication.getInstance().mScreenHistory.get(i);
    		DrakeCircusApplication.getInstance().mScreenHistory.remove(screen);
    		screen.finish();
    	}
    	
    	int selMenu = mPrefs.getIntValue(Constants.SELECTED_MENU_NUMBER);
    	if (selMenu == Constants.SELECT_CASE_HOME) {
    		// do nothing
    	} else if (selMenu == Constants.SELECT_CASE_OURSTORE) {
			Intent intent = new Intent(this, ActivityOurStores.class);
			startActivity(intent);
		}
		else if (selMenu == Constants.SELECT_CASE_CENTREMAP) {
			Intent intent = new Intent(this, ActivityCentreMap.class);
			startActivity(intent);
		}
		else if (selMenu == Constants.SELECT_CASE_LATESTOFFER) {
			Intent intent = new Intent(this, ActivityLatestOffers.class);
			startActivity(intent);
		}
		else if (selMenu == Constants.SELECT_CASE_FOOD) {
			ArrayList<StoreCategoryModel> categories = DrakeCircusApplication.getInstance().dbHelper.getAllStoreCategories();
			for (StoreCategoryModel model : categories) {
				if (model.name.contains("Food")) {
					Intent intent = new Intent(ActivityMain.this, ActivitySearchinCategory.class);
					intent.putExtra(Constants.SELECTED_STORE_CATEGORY, "Food Outlets");
					intent.putExtra(Constants.SELECTED_STORE_CATEGORYID, model.id);
					startActivity(intent);
					ActivityMain.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
					break;
				}
			}
		}
		else if (selMenu == Constants.SELECT_CASE_CINEMA) {
			Intent intent = new Intent(ActivityMain.this, ActivityWebView.class);
			intent.putExtra(ActivityWebView.EXTRA_URL, "http://www1.cineworld.co.uk/cinemas/whiteley");
			String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
			intent.putExtra(ActivityWebView.EXTRA_TITLE, strTitles[Constants.SELECT_CASE_CINEMA]);
			startActivity(intent);
		}
		else if (selMenu == Constants.SELECT_CASE_ROCKUP) {
			Intent intent = new Intent(ActivityMain.this, ActivityWebView.class);
			intent.putExtra(ActivityWebView.EXTRA_URL, "http://www.rock-up.co.uk/book-online");
			String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
			intent.putExtra(ActivityWebView.EXTRA_TITLE, strTitles[Constants.SELECT_CASE_ROCKUP]);
			startActivity(intent);
		}
		else if (selMenu == Constants.SELECT_CASE_TRAVEL) {
			Intent intent = new Intent(this, ActivityGettingHere.class);
			startActivity(intent);
		}
		else if (selMenu == Constants.SELECT_CASE_PARKING) {
			Intent intent = new Intent(this, ActivityCentreMap.class);
			intent.putExtra(Constants.SHOW_FACILITIES, true);
			startActivity(intent);
		}
		else if (selMenu == Constants.SELECT_CASE_EVENTS) {
			Intent intent = new Intent(this, ActivityLatestEvents.class);
			startActivity(intent);
		}
		else if (selMenu == Constants.SELECT_CASE_OPENING_HOURS) {
			Intent intent = new Intent(this, ActivityOpeningHours.class);
			startActivity(intent);
		}
		//else if (selMenu == Constants.SELECT_CASE_MONSTER) {
		//	Intent intent = new Intent(ActivityMain.this, ActivityMonsterStart.class);
		//	startActivity(intent);
		//}
		else if (selMenu == Constants.SELECT_CASE_SIGNUP) {
			Intent intent = new Intent(ActivityMain.this, ActivityWebView.class);
			intent.putExtra(ActivityWebView.EXTRA_URL, "http://eepurl.com/JEbsb");
			String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
			intent.putExtra(ActivityWebView.EXTRA_TITLE, strTitles[Constants.SELECT_CASE_SIGNUP]);
			startActivity(intent);
		}
		else if (selMenu == Constants.SELECT_CASE_FEEDBACK) {
			Intent intent = new Intent(ActivityMain.this, ActivityFeedback.class);
			startActivityForResult(intent, Constants.ACTIVITY_SELECT_FEEDBACK);
		}
		else {
			//Toast.makeText(this, "Not implemented...", Toast.LENGTH_LONG).show();
		}
    	registerSection(selMenu);
    }

    private void registerSection(int nSelMenu) {
    	new AsyncTask<Integer, Void, Void>() {
			@Override
			protected Void doInBackground(Integer... params) {
				int menu = params[0];
		    	Server.RegisterSection(menu);
				return null;
			}
    	}.execute(nSelMenu, null, null);
    }
    
    private class GetHomeCarouselDataTask extends AsyncTask<Void, Void, Void> {

		AppPreferences prefs;
		
		ArrayList<HomeCarousel> carousels;
		
		public GetHomeCarouselDataTask() {
			prefs = DrakeCircusApplication.getInstance().mPrefs;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mCarousels.clear();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			String strUrl = ServerConfig.getServerUrl() + "?" + Server.HTTPREQUESTPARAM_REQUESTTYPE + "=" + Server.HTTPREQUESTPARAM_REQUESTTYPE_HOMECAROUSEL;
			String response = prefs.getHomeCarousel();
			
			if(TextUtils.isEmpty(response)) {
				response = HttpApi.sendGetRequest(strUrl);
			}
			if(!TextUtils.isEmpty(response)) {
				try {
					Gson gson = new Gson();
					HomeCarouselResponse homeCarouselResponse = gson.fromJson(response, HomeCarouselResponse.class);
					
					if (homeCarouselResponse.status.equalsIgnoreCase(ServerConfig.RESPONSE_STATUS_OK) && homeCarouselResponse.result != null) {
						
						prefs.setHomeCarousel(response);
						carousels = homeCarouselResponse.result;
						
						for(HomeCarousel carousel: homeCarouselResponse.result) {
							FileUtil.downloadFileURL(ActivityMain.this, carousel.image);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			mCarousels = carousels;
			mAdapter.notifyDataSetChanged();
			
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshScrollView.onRefreshComplete();
		}
	}
           
    private class GetLatestAppVersion extends AsyncTask<Void, Void, Void> {
		
		public GetLatestAppVersion() {
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			String strUrl = "https://play.google.com/store/apps/details?id=" + Constants.APP_PACKAGE_NAME;
			try {
				Document doc = Jsoup.connect(strUrl).get();
	            latestVersion = doc.getElementsByAttributeValue("itemprop","softwareVersion").first().text();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            				
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			if(latestVersion!=null) {
	            if (!currentVersion.equalsIgnoreCase(latestVersion))
	                showUpdateDialog();
	        }
		}
	}
    
    private void showUpdateDialog(){
    	
    	new AlertDialog.Builder(ActivityMain.this)
		.setIcon(R.drawable.ic_launcher)
		.setTitle("App Update")
		.setMessage("Hey there! We've made some updates to the Whiteley Shopping app and you'll "
				+ "need to download the latest version.")
		.setPositiveButton("Update", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
//                        ("market://details?id=" + Constants.APP_NAME)));
				("https://play.google.com/store/apps/details?id=" + Constants.APP_PACKAGE_NAME)));
				dialog.dismiss();
				
			}
		})
		.create()
		.show();
    }
    
	class GalleryItemFragmentAdapter extends FragmentPagerAdapter {

	    public GalleryItemFragmentAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int position) {
	    	if(position < mCarousels.size())
	    		return GalleryItemFragment.newInstance(mCarousels.get(position));
	    	else
	    		return null;
	    }

	    @Override
	    public int getCount() {
	    	if (mCarousels == null)
	    		return 0;
	        return mCarousels.size();
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	      return "GalleryItem_" + position;
	    }
	}
	
}
