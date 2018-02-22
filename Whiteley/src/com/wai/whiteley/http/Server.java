package com.wai.whiteley.http;

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.http.ResponseModel.EventDetail;
import com.wai.whiteley.http.ResponseModel.EventModelList;
import com.wai.whiteley.http.ResponseModel.HomeCarouselResponse;
import com.wai.whiteley.http.ResponseModel.Monster;
import com.wai.whiteley.http.ResponseModel.NotificationResponse;
import com.wai.whiteley.http.ResponseModel.OfferDetail;
import com.wai.whiteley.http.ResponseModel.OfferModelList;
import com.wai.whiteley.http.ResponseModel.RandomOfferEventResponse;
import com.wai.whiteley.http.ResponseModel.StoreCategoryModelList;
import com.wai.whiteley.http.ResponseModel.StoreDetail;
import com.wai.whiteley.http.ResponseModel.StoreModelList;
import com.google.gson.Gson;

public class Server {

	public static String HTTPREQUESTPARAM_REQUESTTYPE = "request_type";
	public static String HTTPREQUESTPARAM_CATEGORYID = "category_id";
	public static String HTTPREQUESTPARAM_STOREID = "store_id";
	public static String HTTPREQUESTPARAM_OFFERID = "offer_id";
	public static String HTTPREQUESTPARAM_EVENTID = "event_id";
	public static String HTTPREQUESTPARAM_BEACON_UUID = "uuid";
	public static String HTTPREQUESTPARAM_BEACON_MAJOR = "major";
	public static String HTTPREQUESTPARAM_BEACON_MINOR = "minor";
	public static String HTTPREQUESTPARAM_DEVICE_TOKEN = "device_token";
	public static String HTTPREQUESTPARAM_DEVICE_KIND = "device_kind";
	public static String HTTPREQUESTPARAM_DEVICE_ID = "device_id";
	public static String HTTPREQUESTPARAM_SHOP_VISIT = "shop_visit";
	public static String HTTPREQUESTPARAM_LONGITUDE = "longitude";
	public static String HTTPREQUESTPARAM_LATITUDE = "latitude";
	public static String HTTPREQUESTPARAM_KIND = "kind";
	public static String HTTPREQUESTPARAM_NOTIFICATION_ID = "notification_id";
	public static String HTTPREQUESTPARAM_KIDS = "kids";
	public static String HTTPREQUESTPARAM_WALK = "walk";
	public static String HTTPREQUESTPARAM_SUGGEST = "suggest";
	public static String HTTPREQUESTPARAM_FEEDBACK = "feedback";
	public static String HTTPREQUESTPARAM_STARTTIME = "start";
	public static String HTTPREQUESTPARAM_DWELLTIME = "dwell";

	public static String HTTPREQUESTPARAM_REQUESTTYPE_GETNOTIFICATION = "get_notification";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_RANDOMOFFEREVENT = "get_offevent";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_HOMECAROUSEL = "home_carousel";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_STORENAME = "store_name";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_STORECATEGORY = "store_category";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_STOREDETAIL = "store_detail";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_OFFERSNAME = "offers_name";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_OFFERSDETAIL = "offers_detail";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_OFFERREDEEM = "redeem";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_EVENTSNAME = "events_name";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_EVENTSDETAIL = "events_detail";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_FOODOUTLETS = "food_outlets";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_MONSTERINFO = "monster_info";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_REGISTER_INFO = "register_info";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_SET_USER_GPS = "set_user_gps";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_SET_USER_BEACON = "set_user_beacon";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_REGISTER_SECTION = "register_section";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_NOTIFICATION_VIEWED = "notification_viewed";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_REGISTER_SHARES = "register_shares";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_REGISTER_REVIEWS = "register_reviews";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_SEND_FEEDBACK = "send_feedback";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_SEND_USER_VISIT = "send_user_visit";
	public static String HTTPREQUESTPARAM_REQUESTTYPE_CHECK_NOTIFICATION  = "check_notification";

	/*
	 * for User module
	 */
	
	public static Object getNotifications() {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_GETNOTIFICATION;
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				NotificationResponse result = gson.fromJson(response, NotificationResponse.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public static Object GetRandomOfferEvent(int storeId) {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_RANDOMOFFEREVENT;
		strUrl += ("&" + HTTPREQUESTPARAM_STOREID + "=" + storeId);
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				RandomOfferEventResponse result = gson.fromJson(response, RandomOfferEventResponse.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}
	
	public static Object GetStoreNames(int cateId) {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_STORENAME;
		if (cateId > 0)
			strUrl += ("&" + HTTPREQUESTPARAM_CATEGORYID + "=" + cateId);
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				StoreModelList result = gson.fromJson(response, StoreModelList.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public static Object GetStoreCategories() {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_STORECATEGORY;
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				StoreCategoryModelList result = gson.fromJson(response, StoreCategoryModelList.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public static Object GetStoreDetails(int storeId) {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_STOREDETAIL;
		strUrl += ("&" + HTTPREQUESTPARAM_STOREID + "=" + storeId);
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				StoreDetail result = gson.fromJson(response, StoreDetail.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public static Object GetOffers() {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_OFFERSNAME;
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				OfferModelList result = gson.fromJson(response, OfferModelList.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public static Object GetOfferDetail(String strDeviceToken, int offerId) {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_OFFERSDETAIL;
		strUrl += ("&" + HTTPREQUESTPARAM_DEVICE_TOKEN + "=" + strDeviceToken);
		strUrl += ("&" + HTTPREQUESTPARAM_OFFERID + "=" + offerId);
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				OfferDetail result = gson.fromJson(response, OfferDetail.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public static Object SetOfferRedeem(String strDeviceToken, int offerId) {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_OFFERREDEEM;
		strUrl += ("&" + HTTPREQUESTPARAM_DEVICE_TOKEN + "=" + strDeviceToken);
		strUrl += ("&" + HTTPREQUESTPARAM_OFFERID + "=" + offerId);
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				OfferDetail result = gson.fromJson(response, OfferDetail.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public static Object GetEvents() {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_EVENTSNAME;
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				EventModelList result = gson.fromJson(response, EventModelList.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public static Object GetEventDetail(int eventId) {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_EVENTSDETAIL;
		strUrl += ("&" + HTTPREQUESTPARAM_EVENTID + "=" + eventId);
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				EventDetail result = gson.fromJson(response, EventDetail.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public static Object GetFoodOutlets() {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_FOODOUTLETS;
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				StoreModelList result = gson.fromJson(response, StoreModelList.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public static Object GetMonsterInfo(String strUuid, int nMajor, int nMinor) {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_MONSTERINFO;
		strUrl += ("&uuid=" + strUuid.toLowerCase() + "&major=" + nMajor + "&minor=" + nMinor);
		String response = HttpApi.sendGetRequest(strUrl);
		
		if (response != null) {
			try {
				Gson gson = new Gson();
				Monster result = gson.fromJson(response, Monster.class);
				return result;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}
	
	public static Object RegisterInfo(String strDeviceToken, int nShopVisit) {
		
		String strUrl = ServerConfig.getServerUrl()
			+ "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_REGISTER_INFO;
		strUrl += ("&" + HTTPREQUESTPARAM_DEVICE_TOKEN + "=" + strDeviceToken);
		strUrl += ("&" + HTTPREQUESTPARAM_DEVICE_ID + "=" + DrakeCircusApplication.getInstance().mDeviceId);
		strUrl += ("&" + HTTPREQUESTPARAM_DEVICE_KIND + "=" + "android");
		strUrl += ("&" + HTTPREQUESTPARAM_SHOP_VISIT + "=" + String.valueOf(nShopVisit));
		String response = HttpApi.sendGetRequest(strUrl);

		return response;
	}

	public static Object SetUserGps(String strDeviceToken, double dLongitude, double dLatitude) {
		
		String strUrl = ServerConfig.getServerUrl()
			+ "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_SET_USER_GPS;
		strUrl += ("&" + HTTPREQUESTPARAM_DEVICE_TOKEN + "=" + strDeviceToken);
		strUrl += ("&" + HTTPREQUESTPARAM_LONGITUDE + "=" + String.valueOf(dLongitude));
		strUrl += ("&" + HTTPREQUESTPARAM_LATITUDE + "=" + String.valueOf(dLatitude));
		String response = HttpApi.sendGetRequest(strUrl);

		return response;
	}

	public static Object SetUserBeacon(String strDeviceToken, String strUUID, int major , int minor) {
		
		String strUrl = ServerConfig.getServerUrl()
			+ "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_SET_USER_BEACON;
		strUrl += ("&" + HTTPREQUESTPARAM_DEVICE_TOKEN + "=" + strDeviceToken);
		strUrl += ("&" + HTTPREQUESTPARAM_BEACON_UUID + "=" + strUUID.toLowerCase());
		strUrl += ("&" + HTTPREQUESTPARAM_BEACON_MAJOR + "=" + String.valueOf(major));
		strUrl += ("&" + HTTPREQUESTPARAM_BEACON_MINOR + "=" + String.valueOf(minor));
		String response = HttpApi.sendGetRequest(strUrl);

		return response;
	}
	
	public static Object RegisterSection(int nSelectCase) {

		String strKind = "";
		switch (nSelectCase) {
			case Constants.SELECT_CASE_CENTREMAP:
				strKind = Constants.SECTION_KIND_MAP;
				break;
			case Constants.SELECT_CASE_OURSTORE:
				strKind = Constants.SECTION_KIND_STORES;
				break;
			case Constants.SELECT_CASE_LATESTOFFER:
				strKind = Constants.SECTION_KIND_OFFERS;
				break;
			case Constants.SELECT_CASE_FOOD:
				strKind = Constants.SECTION_KIND_FOOD;
				break;
			case Constants.SELECT_CASE_TRAVEL:
				strKind = Constants.SECTION_KIND_HERE;
				break;
			case Constants.SELECT_CASE_PARKING:
				strKind = Constants.SECTION_KIND_FACILITIES;
				break;
			case Constants.SELECT_CASE_EVENTS:
				strKind = Constants.SECTION_KIND_EVENTS;
				break;
			case Constants.SELECT_CASE_OPENING_HOURS:
				strKind = Constants.SECTION_KIND_OPENHRS;
				break;
			//case Constants.SELECT_CASE_MONSTER:
			//	strKind = Constants.SECTION_KIND_MONSTER;
			//	break;
			case Constants.SELECT_CASE_CINEMA:
				strKind = Constants.SECTION_KIND_CINEMA;
				break;
			case Constants.SELECT_CASE_ROCKUP:
				strKind = Constants.SECTION_KIND_ROCKUP;
				break;
		}
		String strUrl = ServerConfig.getServerUrl()
			+ "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_REGISTER_SECTION;
		strUrl += ("&" + HTTPREQUESTPARAM_KIND + "=" + strKind);
		String response = HttpApi.sendGetRequest(strUrl);

		return response;
	}

	public static Object NotificationViewed(String strNid) {
		String strUrl = ServerConfig.getServerUrl()
			+ "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_NOTIFICATION_VIEWED;
		strUrl += ("&" + HTTPREQUESTPARAM_NOTIFICATION_ID + "=" + strNid);
		String response = HttpApi.sendGetRequest(strUrl);

		return response;
	}

	public static Object SendFeedback(String strDeviceToken, String strKids, String strWalk, String strSuggest, String strFeedback ) {
		String strUrl = ServerConfig.getServerUrl()
			+ "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_SEND_FEEDBACK;
		strUrl += ("&" + HTTPREQUESTPARAM_DEVICE_TOKEN + "=" + strDeviceToken);
		strUrl += ("&" + HTTPREQUESTPARAM_KIDS + "=" + strKids);
		strUrl += ("&" + HTTPREQUESTPARAM_WALK + "=" + strWalk);
		strUrl += ("&" + HTTPREQUESTPARAM_SUGGEST + "=" + strSuggest);
		strUrl += ("&" + HTTPREQUESTPARAM_FEEDBACK + "=" + strFeedback);
		strUrl = strUrl.replace(" ", "%20");
		
		String response = HttpApi.sendGetRequest(strUrl);

		return response;
	}
	
	public static Object SendUserVisit(String strDeviceToken, String strStartTime, String strDwellTime ) {
		String strUrl = ServerConfig.getServerUrl()
			+ "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_SEND_USER_VISIT;
		strUrl += ("&" + HTTPREQUESTPARAM_DEVICE_TOKEN + "=" + strDeviceToken);
		strUrl += ("&" + HTTPREQUESTPARAM_STARTTIME + "=" + strStartTime);
		strUrl += ("&" + HTTPREQUESTPARAM_DWELLTIME + "=" + strDwellTime);

		String response = HttpApi.sendGetRequest(strUrl);

		return response;
	}
	
	public static Object CheckNotification() {
		String strUrl = ServerConfig.getServerUrl()
			+ "?" + HTTPREQUESTPARAM_REQUESTTYPE + "=" + HTTPREQUESTPARAM_REQUESTTYPE_CHECK_NOTIFICATION;
		
		String response = HttpApi.sendGetRequest(strUrl);

		return response;
	}
}
