package com.wai.whiteley.config;

public class Constants {

	public static final String APP_NAME = "Whiteley";
	public static final String APP_PACKAGE_NAME = "com.wai.whiteley";
	public static final String APP_OPEN_NUM = "APP_OPEN_NUM";

	public static final int MAP_ACTIVITY_LIMIT = 6;
	
	public static final int SPLASH_TIME = 2;
	public static final int HOMEPAGE_LISTCOUNT = 6;
	public static final int FADING_TIMEOUT = 500; // miliseconds
	public static final int SHOWFAVOURITE_TIMEOUT = 1500; // miliseconds
	public static final int SHOWREDEEM_TIMEOUT = 2000; // miliseconds
	public static final float VP_SCROLL_FACTOR = 3.0f;

	public static final String IMAGE_PATH = "/DrakeCircus/temp/";
	public static final String BACKGROUND_IMAGE_FILENAME = "bg.jpg";

	public static final long MIN_DISTANCE_CHANGE_UPDATES = 10; // 10 meters
	public static final long PERIOD_SET_USER_GPS = 1000 * 60 * 10;//1000 * 60 * 10; // 10 min
	public static final long PERIOD_NEW_VISIT = 1000 * 60 * 60 * 24; // 1 day
	public static final long PERIOD_FEEDBACK_TIME = 1000 * 60 * 60 * 2;		// 2 hours

	/* 
	 * map coordinate
	 */
	
	public static final float MAP_RIGHT_LAT =  (float) 50.88418;
	public static final float MAP_RIGHT_LONG =  (float) -1.243586;
	public static final float MAP_LEFT_LAT =  (float) 50.886777;
	public static final float MAP_LEFT_LONG =  (float) -1.249650;
	
	/*
	 * feedback
	 */
	
	public static final String APP_FEEDBACK = "APP_FEEDBACK";
	public static final String APP_FEEDBACK_ASK = "APP_FEEDBACK_ASK";
	public static final String FEEDBACK_SEND = "FEEDBACK_SEND";
	public static final String FEEDBACK_NOTIFY = "FEEDBACK_NOTIFY";
	public static final String FEEDBACK_PAGE = "FEEDBACK_PAGEY";

	/*
	 * floor 
	 */
	public static final int SELECTED_MALL_GROUND = 0;
	public static final int SELECTED_MALL_FLOOR1 = 1;
	public static final int SELECTED_MALL_FLOOR2 = 2;

	/*
	 * Select cases
	 */
	public static final int SELECT_CASE_HOME = -1;
	public static final int SELECT_CASE_CENTREMAP = 0;
	public static final int SELECT_CASE_OURSTORE = 1;
	public static final int SELECT_CASE_LATESTOFFER = 2;
	public static final int SELECT_CASE_FOOD = 3;
	public static final int SELECT_CASE_CINEMA = 4;
	public static final int SELECT_CASE_ROCKUP = 5;
	public static final int SELECT_CASE_TRAVEL = 6;
	public static final int SELECT_CASE_PARKING = 7;
	public static final int SELECT_CASE_EVENTS = 8;
	public static final int SELECT_CASE_OPENING_HOURS = 9;
	//public static final int SELECT_CASE_MONSTER = 10;
	public static final int SELECT_CASE_SIGNUP = 10;
	public static final int SELECT_CASE_FEEDBACK = 11;

	
	/*
	 * Fragment id
	 */
	public static final int FRAGMENT_STORE_NAME = 0;
	public static final int FRAGMENT_STORE_CATEGORY = 1;
	public static final int FRAGMENT_GETTINGHERE_CAR = 0;
	public static final int FRAGMENT_GETTINGHERE_TRAIN = 1;
	public static final int FRAGMENT_GETTINGHERE_BUS = 2;

	/*
	 * activity id
	 */
	public static final int ACTIVITY_SELECT_EXPAND_MENU = 10000;
	public static final int ACTIVITY_SELECT_SHOP = 10001;
	public static final int ACTIVITY_SELECT_FEEDBACK = 10002;

	/*
	 * intent values
	 */
	public static final String SELECTED_STORE_NAME = "SELECTED_STORE_NAME";
	public static final String SELECTED_STORE_ID = "SELECTED_STORE_ID";
	public static final String SELECTED_STORE_CATEGORY = "SELECTED_STORE_CATEGORY";
	public static final String SELECTED_STORE_CATEGORYID = "SELECTED_STORE_CATEGORYID";
	public static final String SELECTED_OFFER_ID = "SELECTED_OFFER_ID";
	public static final String SELECTED_EVENT_ID = "SELECTED_EVENT_ID";
	public static final String SELECTED_BACKGROUND_BMP = "SELECTED_BACKGROUND_BMP";
	public static final String SELECTED_MENU_NUMBER = "SELECTED_MENU_NUMBER";
	public static final String SHOW_FACILITIES = "SHOW_FACILITIES";
	public static final String MONSTER_ID = "MONSTER_ID";

	public final static String EXTRA_BEACON_UUID = "extra_beacon_uuid";
	public final static String EXTRA_BEACON_MAJOR = "extra_beacon_major";
	public final static String EXTRA_BEACON_MINOR = "extra_beacon_minor";
	public final static String EXTRA_REQUEST_CODE = "extra_request_code";
	public final static String EXTRA_CLASS_NAME = "extra_class_name";
	public final static String EXTRA_NOTIFY_TITLE = "extra_notify_title";
	public final static String EXTRA_NOTIFY_CONTENT = "extra_notify_content";

	/*
	 * intent key
	 */
	public static final String KEY_FROM_NOTIFY = "KEY_FROM_NOTIFY";
	public static final String KEY_MESSAGE = "KEY_MESSAGE";
	public static final String KEY_NID = "KEY_NID"; // Notification Id
	public static final String KEY_TYPE = "KEY_TYPE";
	public static final String KEY_VALUE = "KEY_VALUE";
	public static final String KEY_VID = "KEY_VID";
	public static final String KEY_FEEDBACK = "KEY_FEEDBACK";

	/*
	 * task ids
	 */
	public static final int TASK_GET_STORE_GETALLNAMES = 1;
	public static final int TASK_GET_STORE_GETALLCATEGORIES = 2;
	public static final int TASK_GET_STORE_GETCATEGORYSTORENAMES = 3;
	public static final int TASK_GET_STORE_DETAIL = 4;
	public static final int TASK_GET_OFFERS = 5;
	public static final int TASK_GET_OFFERDETAIL = 6;
	public static final int TASK_GET_EVENTS = 7;
	public static final int TASK_GET_EVENTDETAIL = 8;
	public static final int TASK_GET_MONSTERINFO = 9;
	public static final int TASK_SET_OFFER_REDEEM = 10;

	/*
	 * notification
	 */
	public static final String GOOGLE_PROJECT_ID = "680029747641";
	public static final String DEVICE_TOKEN = "DEVICE_TOKEN";
	public static final String GPS_LONGITUDE = "GPS_LONGITUDE";
	public static final String GPS_LATITUDE = "GPS_LATITUDE";
	public static final String NOTIFY_ID = "NOTIFY_ID";
	public static final String LAST_NOTIFY_TIME = "LAST_NOTIFY_TIME";
	public static final String LASTEST_BEACON_INDEX = "LASTEST_BEACON_INDEX";
	
	/*
	 * latest visit and last shopping time
	 */
	public static final String LATEST_VISIT_TIME = "LATEST_VISIT_TIME";
	public static final String LAST_SHOPPING_TIME = "LAST_SHOPPING_TIME";

	/*
	 * register section kind
	 */
	public static final String SECTION_KIND_MAP = "map";
	public static final String SECTION_KIND_STORES = "stores";
	public static final String SECTION_KIND_OFFERS = "offers";
	public static final String SECTION_KIND_FOOD = "food";
	public static final String SECTION_KIND_CINEMA = "cinema";
	public static final String SECTION_KIND_ROCKUP = "rockup";
	public static final String SECTION_KIND_HERE = "here";
	public static final String SECTION_KIND_FACILITIES = "facilities";
	public static final String SECTION_KIND_EVENTS = "events";
	public static final String SECTION_KIND_OPENHRS = "open_hrs";
	public static final String SECTION_KIND_MONSTER = "monster";
	public static final String SECTION_KIND_SIGNUP = "signup";

	/*
	 * store visit
	 */
	public static final String VISIT_START_TIME = "VISIT_START_TIME";
	public static final String VISIT_STATUS = "VISIT_STATUS";

	/*
	 * User Near Beacon
	 * 
	 */
	public static final String NEAR_BEACON_MAJOR = "near_major";
	public static final String NEAR_BEACON_MINOR = "near_minor";

}