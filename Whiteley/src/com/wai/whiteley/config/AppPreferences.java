package com.wai.whiteley.config;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class AppPreferences {

	private static String APP_SHARED_PREFS;

	public static final String ALREADY_SHOW_COACH_MOVIE = "show_coach_movie";
	public static final String SELECTED_STORE_NAME = "selected_store_name";
	public static final String SELECTED_FLOOR_NUMBER = "selected_floor_number";

	private final String NOTIFICATION_WELCOME = "notification_welcome";
	private final String NOTIFICATION_TREASURE_HUNT = "notification_treasure_hunt";
	private final String NOTIFICATION_GIFT_CARD = "notification_gift_card";
	private final String NOTIFICATION_NO_FAVOURITES = "notification_no_favourites";
	private final String NOTIFICATION_POST_VISIT = "notification_post_visit";
	
	private final String JSON_HOME_CAROUSEL = "json_home_carousel";
	
	private SharedPreferences mPrefs;
	private Editor mPrefsEditor;

	public AppPreferences(Context context) {
		APP_SHARED_PREFS = context.getApplicationContext().getPackageName();
		mPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		mPrefsEditor = mPrefs.edit();
	}

	/*
	 * 
	 */
	public void setBooleanValue(String name, boolean bValue) {
		mPrefsEditor.putBoolean(name, bValue);
		mPrefsEditor.commit();
	}

	public boolean getBooleanValue(String name) {
		boolean bValue = mPrefs.getBoolean(name, false);
		return bValue;
	}

	public void setIntValue(String name, int nValue) {
		mPrefsEditor.putInt(name, nValue);
		mPrefsEditor.commit();
	}

	public int getIntValue(String name) {
		int nValue = mPrefs.getInt(name, -1);
		return nValue;
	}

	public void setLongValue(String name, long lValue) {
		mPrefsEditor.putLong(name, lValue);
		mPrefsEditor.commit();
	}

	public long getLongValue(String name) {
		long lValue = mPrefs.getLong(name, 0);
		return lValue;
	}
	
	public void setFloatValue(String name, float lValue) {
		mPrefsEditor.putFloat(name, lValue);
		mPrefsEditor.commit();
	}
	
	public float abs(float a) {
		return (a <= 0.0f) ? 0.0f - a: a;
	}
	
	public float getFloatValue(String name) {
		float lValue = mPrefs.getFloat(name, 0.0f);
		return lValue;
	}

	public void setStringValue(String name, String strValue) {
		mPrefsEditor.putString(name, strValue);
		mPrefsEditor.commit();
	}

	public String getStringValue(String name) {
		String strValue = mPrefs.getString(name, "");
		return strValue;
	}

	public void setIntArrayValue(String name, ArrayList<Integer> arrayVal) {
		int nCount = arrayVal.size();
		String strValue = "";
		for (int i = 0; i < nCount; i ++) {
			if (i == 0) {
				strValue = Integer.toString(arrayVal.get(i));
			}
			else {
				strValue += ("," + Integer.toString(arrayVal.get(i)));
			}
		}

		mPrefsEditor.putString(name, strValue);
		mPrefsEditor.commit();
	}

	public ArrayList<Integer> getIntArrayValue(String name) {
		ArrayList<Integer> arrayVal = new ArrayList<Integer>();
		String strValue = mPrefs.getString(name, "");
		if (!TextUtils.isEmpty(strValue)) {
			String[] strIndexes = strValue.split(",");
			for (int j = 0; j < strIndexes.length; j ++)
				arrayVal.add(Integer.parseInt(strIndexes[j]));
		}

		return arrayVal;
	}
	
	
	// Welcome Notification
	public void setWelcomeNotification(String notification) {
		mPrefsEditor.putString(NOTIFICATION_WELCOME, notification);
		mPrefsEditor.commit();
	}
	public String getWelcomeNotification() {
		return mPrefs.getString(NOTIFICATION_WELCOME, "Hey! Welcome to Whiteley, we've found some great offers for you");
	}
	
	// Treasure Hunt Notification
	public void setTreasureHuntNotification(String notification) {
		mPrefsEditor.putString(NOTIFICATION_TREASURE_HUNT, notification);
		mPrefsEditor.commit();
	}
	public String getTreasureHuntNotification() {
		return mPrefs.getString(NOTIFICATION_TREASURE_HUNT, "We have some easters hidden in Whiteley. Entertain your kids and find them!");
	}
	
	// Gift Card Notification
	public void setGiftCardNotification(String notification) {
		mPrefsEditor.putString(NOTIFICATION_GIFT_CARD, notification);
		mPrefsEditor.commit();
	}
	public String getGiftCardNotification() {
		return mPrefs.getString(NOTIFICATION_GIFT_CARD, "Spoil someone special with a Whiteley gift card");
	}
	
	// No Favourites Notification
	public void setNoFavouritesNotification(String notification) {
		mPrefsEditor.putString(NOTIFICATION_NO_FAVOURITES, notification);
		mPrefsEditor.commit();
	}
	public String getNoFavouritesNotification() {
		return mPrefs.getString(NOTIFICATION_NO_FAVOURITES, "Top Tip! Favourite stores on our app and you'll receive offers tailored just for you.");
	}
	
	// Post Visit Notification
	public void setPostVisitNotification(String notification) {
		mPrefsEditor.putString(NOTIFICATION_POST_VISIT, notification);
		mPrefsEditor.commit();
	}
	public String getPostVisitNotification() {
		return mPrefs.getString(NOTIFICATION_POST_VISIT, "We love our shoppers. Please could you tell us how to make your experience even better?");
	}
	
	// Home Carousel
	public void setHomeCarousel(String json) {
		mPrefsEditor.putString(JSON_HOME_CAROUSEL, json);
		mPrefsEditor.commit();
	}
	public String getHomeCarousel() {
		return mPrefs.getString(JSON_HOME_CAROUSEL, "");
	}
}
