package com.wai.whiteley.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.wai.whiteley.http.HttpApi;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.http.ServerConfig;
import com.radiusnetworks.ibeacon.IBeacon;

public class StatisticsBeaconAsync extends AsyncTask<Void, Void, Void>{

	public enum StatisticType {
		STATISTIC_USER_DETECT,
		STATISTIC_NOTIFICATION_SENT,
		STATISTIC_NOTIFICATION_INTERACTION;
	}
	
	private Context mContext;
	private StatisticType mType = StatisticType.STATISTIC_USER_DETECT;
	private String mUUID;
	private int major;
	private int minor;
	
	private OnCompleteListener onCompleteListener;
	
	public StatisticsBeaconAsync(Context context, StatisticType type, IBeacon beacon) {
		this.mContext = context;
		this.mType = type;
		this.mUUID = beacon.getProximityUuid();
		this.major = beacon.getMajor();
		this.minor = beacon.getMinor();
	}
	
	public StatisticsBeaconAsync(Context context, StatisticType type, String uuid, int major, int minor) {
		this.mContext = context;
		this.mType = type;
		this.mUUID = uuid;
		this.major = major;
		this.minor = minor;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		String strUrl = ServerConfig.getServerUrl() + "?" + Server.HTTPREQUESTPARAM_REQUESTTYPE + "=";
		switch (mType) {
		case STATISTIC_USER_DETECT:
			strUrl += "user_detect";
			break;
		case STATISTIC_NOTIFICATION_SENT:
			strUrl += "notification_sent";
			break;
		case STATISTIC_NOTIFICATION_INTERACTION:
			strUrl += "notification_receive";
			break;

		default:
			break;
		}
		strUrl += "&uuid=" + mUUID + "&major=" + major + "&minor=" + minor;
		
		HttpApi.sendGetRequest(strUrl);
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		if(onCompleteListener != null)
			onCompleteListener.onComplete();
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
	
	public void setOnCompleteListener(OnCompleteListener listener) {
		onCompleteListener = listener;
	}
	
	public interface OnCompleteListener {
		public void onComplete();
	}
}
