package com.wai.whiteley.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.DrakeCircusWindow;
import com.wai.whiteley.R;
import com.wai.whiteley.activities.ActivityEventDetail;
import com.wai.whiteley.activities.ActivityOfferDetail;
import com.wai.whiteley.asynctask.GetRandomOfferEventAsync;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.database.dao.StoreNameDAO;
import com.wai.whiteley.http.ResponseModel.RandomOfferEventResponse.OfferEvent;

public class ThursdayNotifier extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		
		Calendar curTime = Calendar.getInstance();
		
		if(curTime.get(Calendar.HOUR_OF_DAY) == 13 && curTime.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
			if(DrakeCircusApplication.getInstance() != null) {
				final DrakeCircusWindow currentWindow = DrakeCircusApplication.getInstance().getCurrentWindow();
				
				ArrayList<StoreNameDAO> stores = DrakeCircusApplication.getInstance().dbHelper.getFavoritedStores();
				if(stores.size() > 0) {
					
					boolean hasOffer = false;
					ArrayList<StoreNameDAO> storeHasOffer = new ArrayList<StoreNameDAO>();
					for(StoreNameDAO storeNameDao: stores) {
						if(storeNameDao.hasOffer == 1) {
							hasOffer = true;
							storeHasOffer.add(storeNameDao);
						}
					}
					
					if(hasOffer) {
						// select store randomly
						int randomIndex = new Random().nextInt(storeHasOffer.size());
						int storeId = storeHasOffer.get(randomIndex).id;
						
						GetRandomOfferEventAsync asyncTask = new GetRandomOfferEventAsync(context);
						asyncTask.setOnCompleteListener(new GetRandomOfferEventAsync.OnCompleteListener() {
							
							@Override
							public void onComplete(final OfferEvent data) {
								final StoreNameDAO storeInfo = DrakeCircusApplication.getInstance().dbHelper.getOneStore(data.id);
								
								if(currentWindow == null) {
                					Intent launchIntent;
                					if(data.type.equals("offer")) {
                						launchIntent = new Intent(context, ActivityOfferDetail.class);
                						launchIntent.putExtra(Constants.SELECTED_OFFER_ID, data.id);
                					} else {
                						launchIntent = new Intent(context, ActivityEventDetail.class);
                						launchIntent.putExtra(Constants.SELECTED_EVENT_ID, data.id);
                					}
                					launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    
                    				((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                    				0,
                    				new NotificationCompat.Builder(context)
                						.setWhen(System.currentTimeMillis())
                						.setSmallIcon(R.drawable.ic_launcher)
                    					.setTicker("Whiteley")
                    					.setContentTitle(context.getResources().getString(R.string.notification_offer, storeInfo.name))
                    					.setContentText(data.notification)
                    					.setContentIntent(PendingIntent.getActivity(context, 0, launchIntent, 0))
                    					.setAutoCancel(true)
                    					.build());
                				} else {
                					final Context context = (Context)currentWindow;
                					
                					new AlertDialog.Builder(context)
                					.setIcon(R.drawable.ic_launcher)
                					.setTitle(context.getResources().getString(R.string.notification_offer, storeInfo.name))
                					.setMessage(data.notification)
                					.setPositiveButton("View", new DialogInterface.OnClickListener() {
                						
                						@Override
                						public void onClick(DialogInterface dialog, int which) {
                							Intent launchIntent;
                        					if(data.type.equals("offer")) {
                        						launchIntent = new Intent(context, ActivityOfferDetail.class);
                        						launchIntent.putExtra(Constants.SELECTED_OFFER_ID, data.id);
                        					} else {
                        						launchIntent = new Intent(context, ActivityEventDetail.class);
                        						launchIntent.putExtra(Constants.SELECTED_EVENT_ID, data.id);
                        					}
                        					context.startActivity(launchIntent);
                        					
                							dialog.dismiss();
                						}
                					})
                					.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                						
                						@Override
                						public void onClick(DialogInterface dialog, int which) {
                							dialog.dismiss();
                						}
                					})
                					.create()
                					.show();
                				}
							}
						});
						asyncTask.execute(storeId);
						
					}
				}
			}
		}
	}
}
