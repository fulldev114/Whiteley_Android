package com.wai.whiteley.http;

import java.util.ArrayList;

public class ResponseModel {

	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAIL = 0;

	public class GeneralModel {
		public int status;
		public int error_code;
		public String msg;
	}

	/*
	 * notification
	 */
	public class NotificationModel {
		public String welcome;
		public String hunt;
		public String card;
		public String favorite;
		public String visit;
	}
	public class NotificationResponse {
		public String status;
		public String msg;
		public NotificationModel result;
	}
	
	/*
	 * random offer/event response
	 */
	public class RandomOfferEventResponse {
		public String status;
		public String msg;
		public OfferEvent result;
		
		public class OfferEvent {
			public int id;
			public String type;
			public String notification;
		}
	}
	
	/*
	 * home carousel
	 */
	public class HomeCarouselResponse {
		public String status;
		public String msg;
		public ArrayList<HomeCarousel> result;
		
		public class HomeCarousel {
			public int id;
			public String link_type;
			public String link_app;
			public String link_id;
			public String link_url;
			public String image;
			public String status;
		}
	}
	/*
	 * store name
	 */
	public class StoreModel {
		public int id;
		public String name;
		public String label;
		public int has_offer;
		public String unit_num;
		public String location;
		public ArrayList<String> cat_id;
	}

	public class StoreModelList {
		public String status;
		public String msg;
		public ArrayList<StoreModel> result;
	}

	/*
	 * category name
	 */
	public class StoreCategoryModel {
		public int id;
		public String name;
		
		public StoreCategoryModel() {
			
		}
	}

	public class StoreCategoryModelList {
		public String status;
		public String msg;
		public ArrayList<StoreCategoryModel> result;
	}

	/*
	 * store detail
	 */
	public class OpenModel {
		public String mon;
		public String tue;
		public String wed;
		public String thu;
		public String fri;
		public String sat;
		public String sun;
	}

	public class StoreDetailModel {
		public String id;
		public String name;
		public String logo;
		public String image;
		public String text;
		public String url;
		public String phone;
		public OpenModel open;
		public String offer_id;
		public String unit_num;
		public String location;
		public ArrayList<StoreModel> similar_stores;
	}

	public class StoreDetail {
		public String status;
		public String msg;
		public StoreDetailModel result;
	}

	/*
	 * offer model
	 */
	public class OfferModel {
		public int id;
		public String shop_name;
		public String offer_name;
		public String offer_detail;
		public String offer_image;
	}

	public class OfferModelList {
		public String status;
		public String msg;
		public ArrayList<OfferModel> result;
	}
	
	public class OfferRedeemModel {
		public String enabled;
		public String redeemed;
		public String type;
		public String text;
		public String link;
	}
	
	public class OfferDetailModel {
		public String id;
		public String name;
		public String text;
		public String image;
		public String notification;
		public OfferRedeemModel add_button;
		public ArrayList<StoreModel> retailers;
		public ArrayList<OfferModel> great_offers;
	}

	public class OfferDetail {
		public String status;
		public String msg;
		public OfferDetailModel result;
	}

	/*
	 * event model
	 */
	public class EventModel {
		public int id;
		public String event_date;
		public String event_name;
		public String event_detail;
		public String event_image;
	}

	public class EventModelList {
		public String status;
		public String msg;
		public ArrayList<EventModel> result;
	}

	public class EventDetailModel {
		public int id;
		public String title;
		public String date;
		public String image;
		public String text;
		public String location;
		public String expire_date;
		public ArrayList<EventModel> other_events;
	}

	public class EventDetail {
		public String status;
		public String msg;
		public EventDetailModel result;
	}

	/*
	 * monster model
	 */
	public class MonsterModel {
		public int id;
		public String name;
		public String details;
		public String image;
		public String notification;
	}

	public class Monster {
		public String status;
		public String msg;
		public MonsterModel result;
	}
}
