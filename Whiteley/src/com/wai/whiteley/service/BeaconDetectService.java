package com.wai.whiteley.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;
import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.DrakeCircusWindow;
import com.wai.whiteley.R;
import com.wai.whiteley.activities.ActivityCoach;
import com.wai.whiteley.activities.ActivityEventDetail;
import com.wai.whiteley.activities.ActivityLatestOffers;
import com.wai.whiteley.activities.ActivityMonsterCoach;
import com.wai.whiteley.activities.ActivityMonsterFound;
import com.wai.whiteley.activities.ActivityMonsterStart;
import com.wai.whiteley.activities.ActivityMonsterView;
import com.wai.whiteley.activities.ActivityOfferDetail;
import com.wai.whiteley.activities.ActivityOurStores;
import com.wai.whiteley.asynctask.GetMonsterInfoThread;
import com.wai.whiteley.asynctask.GetMonsterInfoThread.OnCompleteListener;
import com.wai.whiteley.asynctask.GetMonsterInfoThread.OnDetectMonsterListener;
import com.wai.whiteley.asynctask.GetNotificationAsync;
import com.wai.whiteley.asynctask.GetRandomOfferEventAsync;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync.StatisticType;
import com.wai.whiteley.config.AppPreferences;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.database.dao.MonsterInfoDAO;
import com.wai.whiteley.database.dao.StoreNameDAO;
import com.wai.whiteley.http.ResponseModel.RandomOfferEventResponse.OfferEvent;
import com.wai.whiteley.http.Server;

public class BeaconDetectService extends Service implements IBeaconConsumer  {

	private final static String TAG = "BeaconDetectService";
	
	public static boolean mStarted = false;
	
	private final long timeUserDetect = 1000 * 10;				// 10 seconds
	private final long timeBeaconDetect = 1000 * 10;			// 10 seconds
	private final long timeNotification = 1000 * 60 * 10;		// 10 minutes
	private final long timeDiff10Mins = 1000 * 60 * 10;			// 10 minutes
	private final long timeDiff2Hours = 1000 * 60 * 60 * 2;		// 2 hours

	private AppPreferences mPrefs;
	private IBeaconManager beaconManager;

//	private Handler handler = new Handler();
//	private Runnable runnable = new Runnable() {
//		@Override
//		public void run() {
//			stopSelf();
//		}
//	};

	private int requestCode = 0;
	private int lastestFoundIndex = 0;
	private long lastestFoundTime = System.currentTimeMillis();
	private IBeacon lastestFoundBeacon = null;
	private ArrayList<IBeacon>		prevBeacons;

	private GetMonsterInfoThread threadGetMonsterInfo = null;
	
	private AlertDialog notificationDialog = null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mPrefs = new AppPreferences(this);
		
		beaconManager = IBeaconManager.getInstanceForApplication(this);
		beaconManager.bind(this);
		
//		handler.postDelayed(runnable, 10000);

		return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		prevBeacons =  new ArrayList<IBeacon>();
	}

	@Override
	public void onDestroy() {
		beaconManager.unBind(this);
		super.onDestroy();
	}

	@Override
	public void onIBeaconServiceConnect() {		
		beaconManager.setRangeNotifier(new RangeNotifier() {
			@Override 
			public void didRangeBeaconsInRegion(Collection<IBeacon> beacons, Region region) {

				if (beacons.size() == 0)
					return;

				final DrakeCircusWindow currentWindow = DrakeCircusApplication.getInstance().getCurrentWindow();
				final Activity currentActivity = (Activity)currentWindow;

				// When more than 10 seconds is past from this beacon was detected
				for(final IBeacon justFoundedBeacon: beacons) {

					long currTime = System.currentTimeMillis();
					long prevTime = DrakeCircusApplication.getInstance().dbHelper.getUserDetectTime(
							justFoundedBeacon.getProximityUuid(),
							justFoundedBeacon.getMajor(),
							justFoundedBeacon.getMinor());
					if ((currTime - prevTime) >= timeUserDetect) {
						
						// ----------- Register near beacon to CMS ---------------
						int nearMajor = mPrefs.getIntValue(Constants.NEAR_BEACON_MAJOR);
						int nearMinor = mPrefs.getIntValue(Constants.NEAR_BEACON_MINOR);
						
						if ( nearMajor > 0 && nearMinor > 0) {	
							if ( nearMajor != justFoundedBeacon.getMajor() || nearMinor != justFoundedBeacon.getMinor()) {
								mPrefs.setIntValue(Constants.NEAR_BEACON_MAJOR, justFoundedBeacon.getMajor());
								mPrefs.setIntValue(Constants.NEAR_BEACON_MINOR, justFoundedBeacon.getMinor());
								
								new AsyncTask<IBeacon, Void, Void>() {
									@Override
									protected Void doInBackground(IBeacon... params) {
										String strDeviceToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);
										Server.SetUserBeacon(strDeviceToken, justFoundedBeacon.getProximityUuid(), justFoundedBeacon.getMajor(), justFoundedBeacon.getMinor());
										return null;
									}
								}.execute(justFoundedBeacon, null, null);
							}
							
						}
						else {
							
							mPrefs.setIntValue(Constants.NEAR_BEACON_MAJOR, justFoundedBeacon.getMajor());
							mPrefs.setIntValue(Constants.NEAR_BEACON_MINOR, justFoundedBeacon.getMinor());

							new AsyncTask<IBeacon, Void, Void>() {
								@Override
								protected Void doInBackground(IBeacon... params) {
									String strDeviceToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);
									Server.SetUserBeacon(strDeviceToken, justFoundedBeacon.getProximityUuid(), justFoundedBeacon.getMajor(), justFoundedBeacon.getMinor());
									return null;
								}
							}.execute(justFoundedBeacon, null, null);
						}

						// -------------------- end -------------------------
						
						StatisticsBeaconAsync task = new StatisticsBeaconAsync(BeaconDetectService.this, StatisticType.STATISTIC_USER_DETECT, 
								justFoundedBeacon.getProximityUuid(), 
								justFoundedBeacon.getMajor(), 
								justFoundedBeacon.getMinor());

						task.setOnCompleteListener(new StatisticsBeaconAsync.OnCompleteListener() {

							@Override
							public void onComplete() {
								DrakeCircusApplication.getInstance().dbHelper.addUserDetect(justFoundedBeacon.getProximityUuid(), justFoundedBeacon.getMajor(), justFoundedBeacon.getMinor());
							}
						});
						task.execute();
						
						DrakeCircusApplication.getInstance().dbHelper.setUserDetectTime(
								justFoundedBeacon.getProximityUuid(),
								justFoundedBeacon.getMajor(),
								justFoundedBeacon.getMinor(),
								currTime);
					}
				}

				// update shop visit info if 1 day past
				long lastVisitTime = mPrefs.getLongValue(Constants.LATEST_VISIT_TIME);
				if (lastestFoundTime - lastVisitTime >= Constants.PERIOD_NEW_VISIT) {
					mPrefs.setLongValue(Constants.LATEST_VISIT_TIME, lastestFoundTime);
					registerShopVisitInfo();
				}
				

				lastestFoundTime = System.currentTimeMillis();
				
				// When beacon detected
				if(currentWindow != null && currentWindow instanceof ActivityMonsterView) {
	
					if(!mStarted)
						return;
					
					if(threadGetMonsterInfo != null && threadGetMonsterInfo.isAlive())
						return;
					
					threadGetMonsterInfo = new GetMonsterInfoThread(currentActivity, beacons);
					threadGetMonsterInfo.setOnDetectMonsterListener(new OnDetectMonsterListener() {
						
						@Override
						public void onDetectMonster(MonsterInfoDAO monsterDao) {
							Intent intent = new Intent(currentActivity, ActivityMonsterFound.class);
							intent.putExtra(Constants.MONSTER_ID, monsterDao.monster_id);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					});
					threadGetMonsterInfo.setOnCompleteListener(new OnCompleteListener() {
						
						@Override
						public void onComplete() {
							threadGetMonsterInfo = null;
						}
					});
					threadGetMonsterInfo.start();
					
				} else if(currentWindow == null
						|| !(currentWindow instanceof ActivityMonsterStart
							|| currentWindow instanceof ActivityMonsterCoach
							|| currentWindow instanceof ActivityMonsterFound)) {

					for(final IBeacon justFoundedBeacon: beacons) {

						long diffTime = System.currentTimeMillis() - lastestFoundTime;
						
						if (isInPrevBeacons(justFoundedBeacon)
								&& (diffTime < timeBeaconDetect))
							continue;

						if(currentWindow instanceof ActivityCoach)
							return;

						if(currentWindow != null && mStarted) {
							
							if(threadGetMonsterInfo != null && threadGetMonsterInfo.isAlive())
								return;
							
							threadGetMonsterInfo = new GetMonsterInfoThread(currentActivity, beacons);
							threadGetMonsterInfo.setOnDetectMonsterListener(new OnDetectMonsterListener() {
								
								@Override
								public void onDetectMonster(MonsterInfoDAO monsterDao) {
									Intent intent = new Intent(currentActivity, ActivityMonsterFound.class);
									intent.putExtra(Constants.MONSTER_ID, monsterDao.monster_id);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent);
								}
							});
							threadGetMonsterInfo.setOnCompleteListener(new OnCompleteListener() {
								
								@Override
								public void onComplete() {
									threadGetMonsterInfo = null;
								}
							});
							threadGetMonsterInfo.start();
						}
						/*
						switch(lastestFoundIndex) {

							case 0: {
								lastestFoundTime = System.currentTimeMillis();
								lastestFoundBeacon = justFoundedBeacon;
								lastestFoundIndex ++;

								GetNotificationAsync getNotificationAsync = new GetNotificationAsync(BeaconDetectService.this);
								getNotificationAsync.setOnCompleteListener(new GetNotificationAsync.OnCompleteListener() {

									@Override
									public void onComplete() {

										// passing any beacon for first time

										long timeNotify;
										timeNotify = SystemClock.elapsedRealtime();
										mPrefs.setLongValue(Constants.LAST_NOTIFY_TIME, timeNotify);

										AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
										Intent notifyIntent = new Intent(getBaseContext(), NotifyService.class);
										notifyIntent.putExtra(Constants.EXTRA_REQUEST_CODE, ++requestCode);
										notifyIntent.putExtra(Constants.EXTRA_CLASS_NAME, "ActivityLatestOffers");
										notifyIntent.putExtra(Constants.EXTRA_BEACON_UUID, justFoundedBeacon.getProximityUuid());
										notifyIntent.putExtra(Constants.EXTRA_BEACON_MAJOR, justFoundedBeacon.getMajor());
										notifyIntent.putExtra(Constants.EXTRA_BEACON_MINOR, justFoundedBeacon.getMinor());
										notifyIntent.putExtra(Constants.EXTRA_NOTIFY_TITLE, getResources().getString(R.string.notification_welcome));
										notifyIntent.putExtra(Constants.EXTRA_NOTIFY_CONTENT, mPrefs.getWelcomeNotification());
										PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), requestCode, notifyIntent, 0);
										alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeNotify, pendingIntent);

										if(currentWindow == null)
											new StatisticsBeaconAsync(BeaconDetectService.this, StatisticType.STATISTIC_NOTIFICATION_SENT, justFoundedBeacon).execute();
									}
								});
								getNotificationAsync.execute();
								break;
							}

							case 1: {

								lastestFoundTime = System.currentTimeMillis();
								lastestFoundBeacon = justFoundedBeacon;
								lastestFoundIndex ++;

								// passing any beacon for second time
	
								long timeNotify;
								if (SystemClock.elapsedRealtime() - mPrefs.getLongValue(Constants.LAST_NOTIFY_TIME) < timeNotification)
									timeNotify = mPrefs.getLongValue(Constants.LAST_NOTIFY_TIME) + timeNotification;
								else
									timeNotify = SystemClock.elapsedRealtime();
								mPrefs.setLongValue(Constants.LAST_NOTIFY_TIME, timeNotify);

								AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
								Intent notifyIntent = new Intent(getBaseContext(), NotifyService.class);
								notifyIntent.putExtra(Constants.EXTRA_REQUEST_CODE, ++requestCode);
								notifyIntent.putExtra(Constants.EXTRA_CLASS_NAME, "ActivityMonsterStart");
								notifyIntent.putExtra(Constants.EXTRA_BEACON_UUID, justFoundedBeacon.getProximityUuid());
								notifyIntent.putExtra(Constants.EXTRA_BEACON_MAJOR, justFoundedBeacon.getMajor());
								notifyIntent.putExtra(Constants.EXTRA_BEACON_MINOR, justFoundedBeacon.getMinor());
								notifyIntent.putExtra(Constants.EXTRA_NOTIFY_TITLE, getResources().getString(R.string.notification_monster));
								notifyIntent.putExtra(Constants.EXTRA_NOTIFY_CONTENT, mPrefs.getTreasureHuntNotification());
								PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), requestCode, notifyIntent, 0);
								alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeNotify, pendingIntent);

								if(currentWindow == null)
									new StatisticsBeaconAsync(BeaconDetectService.this, StatisticType.STATISTIC_NOTIFICATION_SENT, justFoundedBeacon).execute();

								break;
							}

							case 2: {
								lastestFoundTime = System.currentTimeMillis();
								lastestFoundBeacon = justFoundedBeacon;
								lastestFoundIndex ++;
								
								// passing any beacon for third time
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
										
										GetRandomOfferEventAsync asyncTask = new GetRandomOfferEventAsync(BeaconDetectService.this);
										asyncTask.setOnCompleteListener(new GetRandomOfferEventAsync.OnCompleteListener() {
											
											@Override
											public void onComplete(final OfferEvent data) {
												if(data == null)
													return;

												final StoreNameDAO storeInfo = DrakeCircusApplication.getInstance().dbHelper.getOneStore(data.id);

												long timeNotify;
												if (SystemClock.elapsedRealtime() - mPrefs.getLongValue(Constants.LAST_NOTIFY_TIME) < timeNotification)
													timeNotify = mPrefs.getLongValue(Constants.LAST_NOTIFY_TIME) + timeNotification;
												else
													timeNotify = SystemClock.elapsedRealtime();
												mPrefs.setLongValue(Constants.LAST_NOTIFY_TIME, timeNotify);

												AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
												Intent notifyIntent = new Intent(getBaseContext(), NotifyService.class);
												if (data.type.equals("offer")) {
													notifyIntent.putExtra(Constants.EXTRA_CLASS_NAME, "ActivityOfferDetail");
													notifyIntent.putExtra(Constants.SELECTED_OFFER_ID, data.id);
												}
												else {
													notifyIntent.putExtra(Constants.EXTRA_CLASS_NAME, "ActivityEventDetail");
													notifyIntent.putExtra(Constants.SELECTED_EVENT_ID, data.id);
												}
												notifyIntent.putExtra(Constants.EXTRA_REQUEST_CODE, ++requestCode);
												notifyIntent.putExtra(Constants.EXTRA_BEACON_UUID, justFoundedBeacon.getProximityUuid());
												notifyIntent.putExtra(Constants.EXTRA_BEACON_MAJOR, justFoundedBeacon.getMajor());
												notifyIntent.putExtra(Constants.EXTRA_BEACON_MINOR, justFoundedBeacon.getMinor());
												notifyIntent.putExtra(Constants.EXTRA_NOTIFY_TITLE, getResources().getString(R.string.notification_offer, storeInfo.name));
												notifyIntent.putExtra(Constants.EXTRA_NOTIFY_CONTENT, data.notification);
												PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), requestCode, notifyIntent, 0);
												alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeNotify, pendingIntent);

												if(currentWindow == null)
													new StatisticsBeaconAsync(BeaconDetectService.this, StatisticType.STATISTIC_NOTIFICATION_SENT, justFoundedBeacon).execute();
											}
										});
										asyncTask.execute(storeId);
	
									} else {
										long timeNotify;
										if (SystemClock.elapsedRealtime() - mPrefs.getLongValue(Constants.LAST_NOTIFY_TIME) < timeNotification)
											timeNotify = mPrefs.getLongValue(Constants.LAST_NOTIFY_TIME) + timeNotification;
										else
											timeNotify = SystemClock.elapsedRealtime();
										mPrefs.setLongValue(Constants.LAST_NOTIFY_TIME, timeNotify);

										AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
										Intent notifyIntent = new Intent(getBaseContext(), NotifyService.class);
										notifyIntent.putExtra(Constants.EXTRA_REQUEST_CODE, ++requestCode);
										notifyIntent.putExtra(Constants.EXTRA_CLASS_NAME, "ActivityOurStores");
										notifyIntent.putExtra(Constants.EXTRA_BEACON_UUID, justFoundedBeacon.getProximityUuid());
										notifyIntent.putExtra(Constants.EXTRA_BEACON_MAJOR, justFoundedBeacon.getMajor());
										notifyIntent.putExtra(Constants.EXTRA_BEACON_MINOR, justFoundedBeacon.getMinor());
										notifyIntent.putExtra(Constants.EXTRA_NOTIFY_TITLE, getResources().getString(R.string.notification_no_favorites));
										notifyIntent.putExtra(Constants.EXTRA_NOTIFY_CONTENT, mPrefs.getGiftCardNotification());
										PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), requestCode, notifyIntent, 0);
										alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeNotify, pendingIntent);

										if(currentWindow == null)
											new StatisticsBeaconAsync(BeaconDetectService.this, StatisticType.STATISTIC_NOTIFICATION_SENT, justFoundedBeacon).execute();
									}
								} else {
									long timeNotify;
									if (SystemClock.elapsedRealtime() - mPrefs.getLongValue(Constants.LAST_NOTIFY_TIME) < timeNotification)
										timeNotify = mPrefs.getLongValue(Constants.LAST_NOTIFY_TIME) + timeNotification;
									else
										timeNotify = SystemClock.elapsedRealtime();
									mPrefs.setLongValue(Constants.LAST_NOTIFY_TIME, timeNotify);

									AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
									Intent notifyIntent = new Intent(getBaseContext(), NotifyService.class);
									notifyIntent.putExtra(Constants.EXTRA_REQUEST_CODE, ++requestCode);
									notifyIntent.putExtra(Constants.EXTRA_CLASS_NAME, "ActivityOurStores");
									notifyIntent.putExtra(Constants.EXTRA_BEACON_UUID, justFoundedBeacon.getProximityUuid());
									notifyIntent.putExtra(Constants.EXTRA_BEACON_MAJOR, justFoundedBeacon.getMajor());
									notifyIntent.putExtra(Constants.EXTRA_BEACON_MINOR, justFoundedBeacon.getMinor());
									notifyIntent.putExtra(Constants.EXTRA_NOTIFY_TITLE, getResources().getString(R.string.notification_no_favorites));
									notifyIntent.putExtra(Constants.EXTRA_NOTIFY_CONTENT, mPrefs.getNoFavouritesNotification());
									PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), requestCode, notifyIntent, 0);
									alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeNotify, pendingIntent);

									if(currentWindow == null)
										new StatisticsBeaconAsync(BeaconDetectService.this, StatisticType.STATISTIC_NOTIFICATION_SENT, justFoundedBeacon).execute();
								}
								break;
							}
							case 3: {
													
								if((System.currentTimeMillis() - lastestFoundTime) > timeDiff2Hours) {
									lastestFoundBeacon = justFoundedBeacon;
									lastestFoundIndex = 0;
						
									//--- notification process for every Thursday at 1PM UK
														
									Calendar calendar = Calendar.getInstance();
									int minute = calendar.get(Calendar.MINUTE);
									calendar.add(Calendar.MINUTE, 60 - minute);
									
									AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
									
									// cancel AlarmManger for Thursday
									Intent intent = new Intent(getApplicationContext(), ThursdayNotifier.class);
									PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
									alarmManager.cancel(pendingIntent);
									
									// change theme every hour
									intent = new Intent(getApplicationContext(), ThursdayNotifier.class);
									pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
									
									alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 60 * 60, pendingIntent);
					
								} else {
						
									if(currentWindow != null && mStarted) {
										
										if(threadGetMonsterInfo != null && threadGetMonsterInfo.isAlive())
											return;
										
										threadGetMonsterInfo = new GetMonsterInfoThread(currentActivity, beacons);
										threadGetMonsterInfo.setOnDetectMonsterListener(new OnDetectMonsterListener() {
											
											@Override
											public void onDetectMonster(MonsterInfoDAO monsterDao) {
												Intent intent = new Intent(currentActivity, ActivityMonsterFound.class);
												intent.putExtra(Constants.MONSTER_ID, monsterDao.monster_id);
												intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												startActivity(intent);
											}
										});
										threadGetMonsterInfo.setOnCompleteListener(new OnCompleteListener() {
											
											@Override
											public void onComplete() {
												threadGetMonsterInfo = null;
											}
										});
										threadGetMonsterInfo.start();
									}
								}
								
								break;
							}

							default:

						}
			*/
					}
				}
				
				prevBeacons.clear();
				prevBeacons.addAll(beacons);
			}
		});

		beaconManager.setMonitorNotifier(new MonitorNotifier() {
			@Override
			public void didEnterRegion(Region region) {
				int i = 0;
				int j = i;
			}

			@Override
			public void didExitRegion(Region region) {
				int i = 0;
				int j = i;
			}

			@Override
			public void didDetermineStateForRegion(int state, Region region) {
				int i = 0;
				int j = i;
			}
		});

		try {
			//beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
			beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
//		try {
//			beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
	}

	
	private void registerShopVisitInfo() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				String deviceToken;
				deviceToken = mPrefs.getStringValue(Constants.DEVICE_TOKEN);
				if (deviceToken.length() == 0)
					return null;
				Server.RegisterInfo(deviceToken, 1);
				return null;
			}
		}.execute(null, null, null);
	}

	private boolean isInPrevBeacons(IBeacon beacon) {
		for (IBeacon prevBeacon : prevBeacons) {
			if (prevBeacon.getMajor() == beacon.getMajor()
					&& prevBeacon.getMinor() == beacon.getMinor()
					&& prevBeacon.getProximityUuid().equals(beacon.getProximityUuid()))
				return true;
		}
		return false;
	}
}