package com.wai.whiteley.asynctask;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.http.ResponseModel.StoreCategoryModel;
import com.wai.whiteley.http.ResponseModel.StoreCategoryModelList;
import com.wai.whiteley.http.Server;

public class GetStoreCategoryAsync extends AsyncTask<Void, Object, Object>{

	private List<StoreCategoryModel> data;
	
	private OnCompleteListener onCompleteListener = null;
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		data = new ArrayList<StoreCategoryModel>();
	}
	
	@Override
	protected Object doInBackground(Void... params) {
		
		return Server.GetStoreCategories();
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		
		if (result != null) {
			if (result instanceof StoreCategoryModelList) {
				StoreCategoryModelList res_model = (StoreCategoryModelList) result;
				data = res_model.result;
				for (StoreCategoryModel model : res_model.result) {
					DrakeCircusApplication.getInstance().dbHelper.addOrUpdateStoreCategory(model);
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
		public void onComplete(List<StoreCategoryModel> data);
	}
}
