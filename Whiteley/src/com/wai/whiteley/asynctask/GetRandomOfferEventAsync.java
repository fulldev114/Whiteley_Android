package com.wai.whiteley.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.wai.whiteley.http.ResponseModel.RandomOfferEventResponse;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.http.ServerConfig;

public class GetRandomOfferEventAsync extends AsyncTask<Integer, Object, Object>{

	private Context mContext;
	
	private RandomOfferEventResponse.OfferEvent data;
	private int mStoreId = 0;
	
	private OnCompleteListener onCompleteListener = null;
	
	public GetRandomOfferEventAsync(Context context) {
		this.mContext = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		data = null;
	}
	
	@Override
	protected Object doInBackground(Integer... params) {
		
		if(params != null && params.length > 0) {
			mStoreId = params[0];
		}
		
		Object response = Server.GetRandomOfferEvent(mStoreId);
		
		return response;
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		
		if (result != null) {
			if (result instanceof RandomOfferEventResponse) {
				RandomOfferEventResponse res_model = (RandomOfferEventResponse) result;
				if (res_model.status.equalsIgnoreCase(ServerConfig.RESPONSE_STATUS_OK) && res_model.result != null) {
					data = res_model.result;
				}
			}
		}
		
		if(onCompleteListener != null) {
			onCompleteListener.onComplete(data);
		}
	}
	
	@Override
	protected void onCancelled() {
		if(onCompleteListener != null) {
			onCompleteListener.onComplete(data);
		}
		super.onCancelled();
	}
	
	public void setOnCompleteListener(OnCompleteListener listener) {
		this.onCompleteListener = listener;
	}
	
	
	public interface OnCompleteListener {
		public void onComplete(RandomOfferEventResponse.OfferEvent data);
	}
}
