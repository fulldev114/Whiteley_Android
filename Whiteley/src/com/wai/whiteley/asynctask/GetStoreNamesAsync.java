package com.wai.whiteley.asynctask;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.database.dao.StoreNameDAO;
import com.wai.whiteley.http.ResponseModel.StoreModel;
import com.wai.whiteley.http.ResponseModel.StoreModelList;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.http.ServerConfig;
import com.wai.whiteley.util.FileUtil;

public class GetStoreNamesAsync extends AsyncTask<Integer, Object, Object>{

	private Context mContext;
	
	private List<StoreNameDAO> data;
	private int mCategory = 0;
	
	private OnCompleteListener onCompleteListener = null;
	
	public GetStoreNamesAsync(Context context) {
		this.mContext = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		data = new ArrayList<StoreNameDAO>();
	}
	
	@Override
	protected Object doInBackground(Integer... params) {
		
		if(params != null && params.length > 0) {
			mCategory = params[0];
		}
		
		Object response = Server.GetStoreNames(mCategory);
		
		if (response != null && response instanceof StoreModelList) {
			StoreModelList res_model = (StoreModelList) response;
			if (res_model.status.equalsIgnoreCase(ServerConfig.RESPONSE_STATUS_OK) && res_model.result != null) {
				for (StoreModel model : res_model.result) {
					try {
						FileUtil.downloadFileURL(mContext, model.label);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return response;
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		
		if (result != null) {
			if (result instanceof StoreModelList) {
				StoreModelList res_model = (StoreModelList) result;
				if (res_model.status.equalsIgnoreCase(ServerConfig.RESPONSE_STATUS_OK) && res_model.result != null) {
					for (StoreModel model : res_model.result) {
						StoreNameDAO store = new StoreNameDAO();
						store.id = model.id;
						store.name = model.name;
						store.label = model.label;
						store.hasOffer = model.has_offer;
						store.unitNum = model.unit_num;
						store.location = model.location;
						store.favourite = 0;
						
						DrakeCircusApplication.getInstance().dbHelper.addOrUpdateOneStore(store);
						
						if(model.cat_id != null) {
							DrakeCircusApplication.getInstance().dbHelper.addOrUpdateRelationStoreCat(model.id, model.cat_id);
						}
					}
				}
				data = DrakeCircusApplication.getInstance().dbHelper.getStoreInfos(mCategory);
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
		public void onComplete(List<StoreNameDAO> data);
	}
}
