package com.wai.whiteley.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.config.AppPreferences;
import com.wai.whiteley.http.ResponseModel.NotificationModel;
import com.wai.whiteley.http.ResponseModel.NotificationResponse;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.http.ServerConfig;

public class GetNotificationAsync extends AsyncTask<Integer, Object, Object>{

	private Context mContext;
	
	private OnCompleteListener onCompleteListener = null;
	
	public GetNotificationAsync(Context context) {
		this.mContext = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Object doInBackground(Integer... params) {
		
		Object response = Server.getNotifications();
		
		return response;
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		
		if (result != null && result instanceof NotificationResponse) {
			NotificationResponse res_model = (NotificationResponse) result;
			if (res_model.status.equalsIgnoreCase(ServerConfig.RESPONSE_STATUS_OK) && res_model.result != null) {
				NotificationModel model = res_model.result;
				
				AppPreferences prefs = DrakeCircusApplication.getInstance().mPrefs;
				
				prefs.setWelcomeNotification(model.welcome);
				prefs.setTreasureHuntNotification(model.hunt);
				prefs.setGiftCardNotification(model.card);
				prefs.setNoFavouritesNotification(model.favorite);
				prefs.setPostVisitNotification(model.visit);
			}
		}
		
		if(onCompleteListener != null) {
			onCompleteListener.onComplete();
		}
	}
	
	@Override
	protected void onCancelled() {
		if(onCompleteListener != null) {
			onCompleteListener.onComplete();
		}
		super.onCancelled();
	}
	
	public void setOnCompleteListener(OnCompleteListener listener) {
		this.onCompleteListener = listener;
	}
	
	
	public interface OnCompleteListener {
		public void onComplete();
	}
}
