package com.wai.whiteley.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.R;
import com.wai.whiteley.asynctask.GetStoreCategoryAsync;
import com.wai.whiteley.asynctask.GetStoreNamesAsync;
import com.wai.whiteley.base.BaseActivity;
import com.wai.whiteley.config.AppPreferences;
import com.wai.whiteley.database.dao.StoreNameDAO;
import com.wai.whiteley.http.ResponseModel.StoreCategoryModel;
import com.wai.whiteley.view.GIFView;

public class ActivitySplash extends BaseActivity {

	private GIFView gifLoading;
	
	public static GetStoreNamesAsync asyncGetStoreNames = null;
	public static GetStoreCategoryAsync asyncGetStoreCategory = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		gifLoading = (GIFView) findViewById(R.id.gif_loading);
		gifLoading.loadGIFResource(ActivitySplash.this, R.drawable.loader);
		
		ArrayList<StoreNameDAO> mData = DrakeCircusApplication.getInstance().dbHelper.getStoreInfos(0);
		if(mData == null || mData.size() == 0) {
			GetStoreNamesAsync.OnCompleteListener initStoreNamesOnCompleteListener = new GetStoreNamesAsync.OnCompleteListener() {
	
				@Override
				public void onComplete(List<StoreNameDAO> data) {
					asyncGetStoreNames = null;
				}
				
			};
			asyncGetStoreNames = new GetStoreNamesAsync(this);
			asyncGetStoreNames.setOnCompleteListener(initStoreNamesOnCompleteListener);
			asyncGetStoreNames.execute();
			
			GetStoreCategoryAsync.OnCompleteListener initStoreCategoriesOnCompleteListener = new GetStoreCategoryAsync.OnCompleteListener() {
	
				@Override
				public void onComplete(List<StoreCategoryModel> data) {
					asyncGetStoreCategory = null;
				}
				
			};
			asyncGetStoreCategory = new GetStoreCategoryAsync();
			asyncGetStoreCategory.setOnCompleteListener(initStoreCategoriesOnCompleteListener);
			asyncGetStoreCategory.execute();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				gotoNextPage();
			}
		}, 1600);
	}

	private void gotoNextPage() {
		boolean bShowMovie = mPrefs.getBooleanValue(AppPreferences.ALREADY_SHOW_COACH_MOVIE);
		if (!bShowMovie) {
			Intent intent = new Intent(ActivitySplash.this, ActivityCoach.class);
			startActivity(intent);
			ActivitySplash.this.finish();
			overridePendingTransition(R.anim.in_left, R.anim.out_left);
		} else {
			Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
			startActivity(intent);
			ActivitySplash.this.finish();
			overridePendingTransition(R.anim.in_left, R.anim.out_left);
		}
	}
}
