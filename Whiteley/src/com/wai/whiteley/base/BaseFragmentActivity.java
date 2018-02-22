package com.wai.whiteley.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.radiusnetworks.ibeacon.IBeacon;
import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.DrakeCircusWindow;
import com.wai.whiteley.R;
import com.wai.whiteley.activities.ActivityCentreMap;
import com.wai.whiteley.activities.ActivityEventDetail;
import com.wai.whiteley.activities.ActivityFeedback;
import com.wai.whiteley.activities.ActivityGettingHere;
import com.wai.whiteley.activities.ActivityLatestEvents;
import com.wai.whiteley.activities.ActivityLatestOffers;
import com.wai.whiteley.activities.ActivityMain;
import com.wai.whiteley.activities.ActivityMonsterStart;
import com.wai.whiteley.activities.ActivityOfferDetail;
import com.wai.whiteley.activities.ActivityOpeningHours;
import com.wai.whiteley.activities.ActivityOurStores;
import com.wai.whiteley.activities.ActivitySearchinCategory;
import com.wai.whiteley.activities.ActivityShopDetail;
import com.wai.whiteley.activities.OurStoresNameFragment;
import com.wai.whiteley.config.AppPreferences;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.http.ResponseModel.StoreCategoryModel;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.ImageUtil;
import com.wai.whiteley.view.DCProgressDialog;

public class BaseFragmentActivity extends FragmentActivity implements DrakeCircusWindow {
	
//	private static final String DEBUG_TAG = "BaseFragmentActivity";
	
	public AppPreferences mPrefs;
	protected Handler mHandler = new Handler();
	protected DCProgressDialog dialogWait;
	
	public FragmentManager fragmentManager;
	private AlertDialog mAlertDialog;
	private String mNid;
	private String msg;
	private String type;
	private String value;	
	private String vid;	

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		mPrefs = DrakeCircusApplication.getInstance().mPrefs;
		dialogWait = new DCProgressDialog(this);
		mAlertDialog = null;

		fragmentManager = getSupportFragmentManager();
		if(!(this instanceof ActivityMain))
			DrakeCircusApplication.getInstance().mScreenHistory.add(this);
	}
	
	public void showFragment(Fragment fragment, boolean isStack) {
		showFragment(fragment, isStack, true);
	}
	
	public void showFragment(Fragment fragment, boolean isStack, boolean isAnimation) {
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			
			if(isAnimation)
				transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
			
			transaction.replace(R.id.view_body, fragment);
			
			if(isStack)
				transaction.addToBackStack(null);
			
			transaction.commit();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void goBack() {
		try {
			if(fragmentManager.getBackStackEntryCount() > 0) {
				fragmentManager.popBackStack();
			} else {
				finish();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void removeAllFragments() {
		if(fragmentManager.getFragments() != null) {
			for(Fragment fragment: fragmentManager.getFragments()) {
				if(fragment != null)
					fragmentManager.beginTransaction().remove(fragment).commit();
			}
			fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}
	
    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	setIntent(intent);
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		DrakeCircusApplication.getInstance().setCurrentWindow(this);
		
		// Show alert dialog when activity is launched from notification 
		Intent intent = getIntent();

		if ((mAlertDialog != null) && (mAlertDialog.isShowing()))
				mAlertDialog.cancel();
		
		if ((intent != null) && (intent.getExtras() != null)) {
			if (intent.getExtras().containsKey(Constants.KEY_FEEDBACK)) {
				if (mAlertDialog == null) {
					mAlertDialog = new AlertDialog.Builder(this)
	    			.setTitle("How was your visit to Whiteley?")
	    			.setMessage("Help us improve your shopping\nexperience by answering 3 short\nquestions about your visit today.")
	    			.setNegativeButton("Not right now", new DialogInterface.OnClickListener() {
	    				
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) {
	    					dialog.dismiss();
	    					
	    				}
	    			})
	    			.setPositiveButton("OK, let's go!", new DialogInterface.OnClickListener() {
	    				
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) {
	    					dialog.dismiss();
	    					
	    					Intent intent = new Intent(BaseFragmentActivity.this, ActivityFeedback.class);
	    					startActivityForResult(intent, Constants.ACTIVITY_SELECT_FEEDBACK);	    					
	       				}
	    			})
	    			.create();
					
					mAlertDialog.show();
				}
			
			}else if (intent.getExtras().containsKey(Constants.KEY_FROM_NOTIFY)) {
				msg = intent.getExtras().getString(Constants.KEY_MESSAGE);
				mNid = intent.getExtras().getString(Constants.KEY_NID);
				type = intent.getExtras().getString(Constants.KEY_TYPE);
				value = intent.getExtras().getString(Constants.KEY_VALUE);
				vid = intent.getExtras().getString(Constants.KEY_VID);
				// Show Alert
				if (mAlertDialog == null) {
					mAlertDialog = new AlertDialog.Builder(this)
						.setIcon(R.drawable.ic_launcher)
						.setTitle("Message from Whiteley")
						.setPositiveButton("Close", new DialogInterface.OnClickListener() {
    				
		    				@Override
		    				public void onClick(DialogInterface dialog, int which) {
		    					dialog.dismiss();
		    					
		    					if ( type == null )
		    						return;
		    					
		    					String aName = CommonUtil.foregroundActivityName(BaseFragmentActivity.this);
		    					if ( type.equals("section")) {
		    						if ( value.equals("map")) { // Map
		    							if ( aName.contains("ActivityCentreMap") ) 
		    								return;
		    							
		    							Intent intent = new Intent(BaseFragmentActivity.this, ActivityCentreMap.class);
		    							startActivity(intent);
		    							BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    						}
		    						else if ( value.equals("stores") ) { // Store
		    							if ( vid.equals("")) {
		    								Intent intent = new Intent(BaseFragmentActivity.this, ActivityOurStores.class);
		    								startActivity(intent);
		    								BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    							}
		    							else {
		    								Intent intent = new Intent(BaseFragmentActivity.this, ActivityShopDetail.class);
		    								intent.putExtra(Constants.SELECTED_STORE_ID, Integer.valueOf(vid));
		    								startActivity(intent);
		    								BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    							}
		    						}
		    						else if ( value.equals("food") ) { // Foods
		    							ArrayList<StoreCategoryModel> categories = DrakeCircusApplication.getInstance().dbHelper.getAllStoreCategories();
		    							for (StoreCategoryModel model : categories) {
		    								if (model.name.contains("Food")) {
		    									Intent intent = new Intent(BaseFragmentActivity.this, ActivitySearchinCategory.class);
		    									intent.putExtra(Constants.SELECTED_STORE_CATEGORY, "Food Outlets");
		    									intent.putExtra(Constants.SELECTED_STORE_CATEGORYID, model.id);
		    									startActivity(intent);
		    									BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    									break;
		    								}
		    							}
		    						}
		    						else if ( value.equals("offers") ) { // Offers
		    							if ( vid.equals("")) {
		    								Intent intent = new Intent(BaseFragmentActivity.this, ActivityLatestOffers.class);
		    								startActivity(intent);
		    								BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    							}
		    							else {
		    								Intent intent = new Intent(BaseFragmentActivity.this, ActivityOfferDetail.class);
		    								intent.putExtra(Constants.SELECTED_OFFER_ID, Integer.valueOf(vid));
		    								startActivity(intent);
		    								BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    							}
		    						}
		    						else if ( value.equals("here")) { // Here
		    							Intent intent = new Intent(BaseFragmentActivity.this, ActivityGettingHere.class);
		    							startActivity(intent);
		    							BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    						}
		    						else if ( value.equals("facilities")) { // Facilities
		    							Intent intent = new Intent(BaseFragmentActivity.this, ActivityCentreMap.class);
		    							intent.putExtra(Constants.SHOW_FACILITIES, true);
		    							startActivity(intent);
		    							BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    						}
		    						else if ( value.equals("events") ) { // Events
		    							if ( vid.equals("")) {
		    								Intent intent = new Intent(BaseFragmentActivity.this, ActivityLatestEvents.class);
		    								startActivity(intent);
		    								BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    							}
		    							else {
		    								Intent intent = new Intent(BaseFragmentActivity.this, ActivityEventDetail.class);
		    								intent.putExtra(Constants.SELECTED_EVENT_ID, Integer.valueOf(vid));
		    								startActivity(intent);
		    								BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    							}
		    						}
		    						else if ( value.equals("open_hrs")) { // Open Hours
		    							Intent intent = new Intent(BaseFragmentActivity.this, ActivityOpeningHours.class);
		    							startActivity(intent);
		    							BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    						}
		    						else if ( value.equals("monster")) { // Monster
		    							Intent intent = new Intent(BaseFragmentActivity.this, ActivityMonsterStart.class);
		    							startActivity(intent);
		    							BaseFragmentActivity.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
		    						}
		    					}
		    					else {
		    						startActivity(new Intent(Intent.ACTION_VIEW,
		    					             Uri.parse(value)));
		    					}
		    					
		    					
		    				}
						})
						.create();
				}
				mAlertDialog.setMessage(msg);
				mAlertDialog.show();

				// Reply to Server
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						Server.NotificationViewed(mNid);
						return null;
					}
				}.execute(null, null, null);

				intent.removeExtra(Constants.KEY_FROM_NOTIFY);
				intent.removeExtra(Constants.KEY_MESSAGE);
			}
		}

	}
	
	@Override
	protected void onPause() {
		DrakeCircusApplication.getInstance().setCurrentWindow(null);
		
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		DrakeCircusApplication.getInstance().mScreenHistory.remove(this);
		ImageUtil.releaseImages(getWindow().getDecorView());
		System.gc();
		
		super.onDestroy();
	}
		
    protected void showShortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    
    protected void showLongToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

	@Override
	public void onDetectedBeacon(Collection<IBeacon> beacons) {
		
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
