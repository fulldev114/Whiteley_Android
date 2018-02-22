package com.wai.whiteley.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.DrakeCircusWindow;
import com.wai.whiteley.R;
import com.wai.whiteley.activities.ActivityEventDetail;
import com.wai.whiteley.activities.ActivityLatestOffers;
import com.wai.whiteley.activities.ActivityMonsterStart;
import com.wai.whiteley.activities.ActivityOfferDetail;
import com.wai.whiteley.activities.ActivityOurStores;
import com.wai.whiteley.config.AppPreferences;
import com.wai.whiteley.config.Constants;

public class NotifyService extends IntentService {

	private int m_nMajor, m_nMinor;
	private int m_nOfferEventId;
	private int m_nRequestCode;
	private String m_strUuid;

	private String m_strTitle;
	private String m_strContent;
	private String m_strClassName;
	
	private Intent m_intent = null;
	private AlertDialog m_alertDialog;
	private DrakeCircusWindow m_currentWindow;
	private Context m_context;

	public NotifyService() {
		super("Notify Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		m_intent = null;

		m_currentWindow = DrakeCircusApplication.getInstance().getCurrentWindow();

		if (m_currentWindow == null)
			m_context = getBaseContext();
		else
			m_context = (Context)m_currentWindow;

		m_nRequestCode = intent.getIntExtra(Constants.EXTRA_REQUEST_CODE, 0);
		m_nMajor = intent.getIntExtra(Constants.EXTRA_BEACON_MAJOR, 0);
		m_nMinor = intent.getIntExtra(Constants.EXTRA_BEACON_MINOR, 0);
		m_strUuid = intent.getStringExtra(Constants.EXTRA_BEACON_UUID);
		m_strTitle = intent.getStringExtra(Constants.EXTRA_NOTIFY_TITLE);
		m_strContent = intent.getStringExtra(Constants.EXTRA_NOTIFY_CONTENT);
		m_strClassName = intent.getStringExtra(Constants.EXTRA_CLASS_NAME);

		if (m_strClassName.equals("ActivityLatestOffers"))
			m_intent = new Intent(m_context, ActivityLatestOffers.class);
		if (m_strClassName.equals("ActivityMonsterStart"))
			m_intent = new Intent(m_context, ActivityMonsterStart.class);
		if (m_strClassName.equals("ActivityOfferDetail"))
			m_intent = new Intent(m_context, ActivityOfferDetail.class);
		if (m_strClassName.equals("ActivityEventDetail"))
			m_intent = new Intent(m_context, ActivityEventDetail.class);
		if (m_strClassName.equals("ActivityOurStores"))
			m_intent = new Intent(m_context, ActivityOurStores.class);

		if (m_intent == null)
			return;

		if (m_strClassName.equals("ActivityOfferDetail")) {
			m_nOfferEventId = intent.getIntExtra(Constants.SELECTED_OFFER_ID, 0);
			m_intent.putExtra(Constants.SELECTED_OFFER_ID, m_nOfferEventId);
		}
		if (m_strClassName.equals("ActivityEventDetail")) {
			m_nOfferEventId = intent.getIntExtra(Constants.SELECTED_EVENT_ID, 0);
			m_intent.putExtra(Constants.SELECTED_EVENT_ID, m_nOfferEventId);
		}

		m_intent.putExtra(Constants.EXTRA_BEACON_UUID, m_strUuid);
		m_intent.putExtra(Constants.EXTRA_BEACON_MAJOR, m_nMajor);
		m_intent.putExtra(Constants.EXTRA_BEACON_MINOR, m_nMinor);
		m_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		if (m_currentWindow == null) {
			// Notification
			NotificationManager notifyManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			notifyManager.notify(
				m_nRequestCode,
				new NotificationCompat.Builder(getBaseContext())
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(m_strTitle)
				.setContentText(m_strContent)
				.setContentIntent(PendingIntent.getActivity(NotifyService.this, m_nRequestCode, m_intent, PendingIntent.FLAG_CANCEL_CURRENT))
				.setAutoCancel(true)
				.build()
			);
		}
		else {
			// Dialog
			((Activity)m_context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					m_alertDialog = new AlertDialog.Builder(m_context)
					.setIcon(R.drawable.ic_launcher)
					.setTitle(m_strTitle)
					.setMessage(m_strContent)
					.setCancelable(false)
					.setPositiveButton("View", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Context context = (Context)m_currentWindow;
							context.startActivity(m_intent);
							dialog.dismiss();
						}
					})
					.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.create();
					m_alertDialog.show();
					
				}
			});
		}

	}
}
