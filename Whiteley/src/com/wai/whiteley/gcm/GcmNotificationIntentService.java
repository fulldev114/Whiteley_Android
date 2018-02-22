package com.wai.whiteley.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.DrakeCircusWindow;
import com.wai.whiteley.R;
import com.wai.whiteley.activities.ActivityMain;
import com.wai.whiteley.config.AppPreferences;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.http.Server;


public class GcmNotificationIntentService extends IntentService {

	private String mMsg;		// Message
	private String mNid;		// Notification Id
	private String mType;		// Notification Type
	private String mValue;		// Notification Value
	private String mVid;		// Notification Value ID

	private AppPreferences mPrefs;
	
	private final String NOTIFICATION_MESSAGE = "message";
	private final String NOTIFICATION_NID = "nid";
	private final String NOTIFICATION_SOUND = "sound";
	private final String NOTIFICATION_TYPE = "type";
	private final String NOTIFICATION_VALUE = "value";
	private final String NOTIFICATION_VID = "vid";

	public GcmNotificationIntentService() {
		super("GcmIntentService");
		mPrefs = DrakeCircusApplication.getInstance().mPrefs;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				//sendNotification("Send error : " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				//sendNotification("Deleted messages on Whiteley : " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				for (int i = 0; i < 3; i++) {
					try { Thread.sleep(5000); } catch (InterruptedException e) {}
				}

				mMsg = (String)extras.get(NOTIFICATION_MESSAGE);
				mNid = (String)extras.get(NOTIFICATION_NID);
				mType = (String)extras.get(NOTIFICATION_TYPE);
				mValue = (String)extras.get(NOTIFICATION_VALUE);
				mVid = (String)extras.get(NOTIFICATION_VID);

				sendNotification();
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification() {

		// Send notification to pending list and show
		Intent intent = new Intent(this, ActivityMain.class);
		intent.putExtra(Constants.KEY_FROM_NOTIFY, true);
		intent.putExtra(Constants.KEY_MESSAGE, mMsg);
		intent.putExtra(Constants.KEY_NID, mNid);
		intent.putExtra(Constants.KEY_TYPE, mType);
		intent.putExtra(Constants.KEY_VALUE, mValue);
		intent.putExtra(Constants.KEY_VID, mVid);

		intent.setAction(Long.toString(System.currentTimeMillis())); // For different pending intent per notification
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		/*
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(getResources().getString(R.string.app_name))
			.setStyle(new NotificationCompat.InboxStyle())
			.setAutoCancel(true)
			.setContentText(mMsg);

		builder.setContentIntent(contentIntent);
		notificationManager.notify(NOTIFICATION_ID, builder.build());
		*/
		int notifyId = mPrefs.getIntValue(Constants.NOTIFY_ID);
		notifyId++;
		mPrefs.setIntValue(Constants.NOTIFY_ID, notifyId);

		// If activity is showing, then show alert on the current activity
		DrakeCircusWindow currentWindow = DrakeCircusApplication.getInstance().getCurrentWindow();
		Activity currentActivity = (Activity)currentWindow;
		
		if (currentWindow == null) {
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
			Notification notification = builder.setWhen(0)
				.setAutoCancel(true)
				.setContentTitle("Whiteley")
				.setContentText(mMsg)
				.setStyle(inboxStyle)
				.setTicker("Whiteley")
				.setNumber(notifyId)
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
			Intent intentAlert = new Intent(this, currentActivity.getClass());
			intentAlert.putExtra(Constants.KEY_FROM_NOTIFY, true);
			intentAlert.putExtra(Constants.KEY_MESSAGE, mMsg);
			intentAlert.putExtra(Constants.KEY_NID, mNid);
			intentAlert.putExtra(Constants.KEY_TYPE, mType);
			intentAlert.putExtra(Constants.KEY_VALUE, mValue);
			intentAlert.putExtra(Constants.KEY_VID, mVid);
			intentAlert.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentAlert.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentAlert);
		}
	}
}
