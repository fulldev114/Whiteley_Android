package com.wai.whiteley.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// here is the OnRevieve methode which will be called when boot completed
public class BootCompleted extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// we double check here for only boot complete event
		if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
			// here we start the service
			
//			Intent bgservice = new Intent(context, BeaconDetectService.class);
//			PendingIntent pintent = PendingIntent.getService(context, 0, bgservice, 0);
//			AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//			
//			alarm.setRepeating(AlarmManager.RTC_WAKEUP,
//					Calendar.getInstance().getTimeInMillis(), 10 * 1000, pintent);
		}
	}
}