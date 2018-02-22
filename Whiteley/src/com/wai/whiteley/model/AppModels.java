package com.wai.whiteley.model;

public class AppModels {
	public static class Store_model {
		public int icon_type;
		public String mTitle;
		public String mDesc;

		public Store_model(int inType, String inTitle, String inDesc) {
			icon_type = inType;
			mTitle = inTitle;
			mDesc = inDesc;
		}
	}

	public static class Store_open_model {
		public String mDay;
		public String mOpenTime;
		public String mCloseTime;
		public boolean mBRest;

		public Store_open_model(String inDay, String inOpenTime, String inCloseTime, boolean inRest) {
			mDay = inDay;
			mOpenTime = inOpenTime;
			mCloseTime = inCloseTime;
			mBRest = inRest;
		}
	}

	public static class offer_model {
		public int mId;
		public String mTitle;
		public String mPictureUrl;
		public String mContent1;
		public String mContent2;

		public offer_model(int inId, String inTitle, String inPictUrl, String inContent1, String inContent2) {
			mId = inId;
			mTitle = inTitle;
			mPictureUrl = inPictUrl;
			mContent1 = inContent1;
			mContent2 = inContent2;
		}
	}
}
