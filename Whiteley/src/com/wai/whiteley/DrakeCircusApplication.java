package com.wai.whiteley;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wai.whiteley.activities.ActivityMain;
import com.wai.whiteley.config.AppPreferences;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.database.DatabaseHelper;
import com.wai.whiteley.http.HttpApi;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.http.ServerConfig;
import com.wai.whiteley.service.BeaconDetectService;
import com.wai.whiteley.util.DCImageLoader;
import com.wai.whiteley.util.FontUtils;
import com.wai.whiteley.util.GeolocationUtil;

public class DrakeCircusApplication extends Application {

	private Intent mIntent = null;
	public static DrakeCircusApplication mInstance = null;
	public static int mScreenWidth = 0;
	public static int mScreenHeight = 0;
	public DatabaseHelper dbHelper;
	public AppPreferences mPrefs;

	private DrakeCircusWindow mCurrentWindow = null;
	
	public ArrayList<Activity> mScreenHistory = new ArrayList<Activity>();
	public String mDeviceId;
		
	public static DrakeCircusApplication getInstance() {
		return mInstance;
	}

	public static Context getContext() {
		return getInstance();
	}

	public final Intent getIntent() {
		if (mIntent == null)
			mIntent = new Intent();
		return mIntent;
	}

	public final void setIntent(Intent paramIntent) {
		mIntent = paramIntent;
	}

	public void onCreate() {
		super.onCreate();
		mInstance = this;

		mPrefs = new AppPreferences(this);
		
		FontUtils.initialize(getContext());

		// Init Preference variable
		mPrefs.setIntValue(Constants.NOTIFY_ID, 0);
		mPrefs.setLongValue(Constants.LAST_SHOPPING_TIME, 0);
		mPrefs.setFloatValue(Constants.GPS_LONGITUDE, 0);
		mPrefs.setFloatValue(Constants.GPS_LATITUDE, 0);

		// initialize Image Loader
		initImageLoader(getApplicationContext());
		new DCImageLoader();
		DCImageLoader.init();
		//DCImageLoader.clearCache();

		
		// Token for GCM
		mDeviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		initDeviceTokenForGcm();

		// open database
		dbHelper = new DatabaseHelper(this);
		try {
			dbHelper.openDataBase();
		} catch (Exception e) {
			Toast.makeText(this, "Unable to open database", Toast.LENGTH_LONG).show();
		}

		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidth = size.x;
		mScreenHeight = size.y;
		
		Intent beaconDetectService = new Intent(this, BeaconDetectService.class);
		startService(beaconDetectService);
		
		

	}

	@Override
	public void onTerminate() {
		if (dbHelper != null)
			dbHelper.closeDatabase();
		
		Intent beaconDetectService = new Intent(this, BeaconDetectService.class);
		stopService(beaconDetectService);
		
		super.onTerminate();
	}

	private void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs() // Remove for release app
		.build();
		
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	private void initDeviceTokenForGcm() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				String msg = "";
				GoogleCloudMessaging gcm;
				String deviceToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);
				if (deviceToken.length() == 0) {
					try {
						gcm = GoogleCloudMessaging.getInstance(DrakeCircusApplication.this);
						if (gcm == null)
							return null;

						deviceToken = gcm.register(Constants.GOOGLE_PROJECT_ID);
						if ((deviceToken != null) && (deviceToken.length() != 0))
							mPrefs.setStringValue(Constants.DEVICE_TOKEN, deviceToken);
					} catch (IOException ex) {
						msg = "Error : " + ex.getMessage();
					}
				}
				if ((deviceToken != null) && (deviceToken.length() != 0)) {
					//mDeviceId
					Server.RegisterInfo(deviceToken, 0);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
			}
		}.execute(null, null, null);
	}

	public void setCurrentWindow(DrakeCircusWindow window) {
		this.mCurrentWindow = window;
	}
	
	public DrakeCircusWindow getCurrentWindow() {
		return this.mCurrentWindow;
	}
}
