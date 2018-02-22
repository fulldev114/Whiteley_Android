package com.wai.whiteley.activities;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wai.whiteley.R;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync.StatisticType;
import com.wai.whiteley.base.BaseActivity;
import com.wai.whiteley.base.BaseTask;
import com.wai.whiteley.base.BaseTask.TaskListener;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.http.ResponseModel.OfferModel;
import com.wai.whiteley.http.ResponseModel.OfferModelList;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.model.AppModels.offer_model;
import com.wai.whiteley.service.BeaconDetectService;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.DCImageLoader;
import com.wai.whiteley.util.FontUtils;

public class ActivityLatestOffers extends BaseActivity implements OnRefreshListener<ListView> {

	public static ActivityLatestOffers instance = null;

	private TextView txt_headertitle;
	private RelativeLayout viewMain;
	private PullToRefreshListView listViewOffers;
	
	private LinearLayout emptyHolder;
	private TextView txtSorry;

	private ArrayList<offer_model> mData = new ArrayList<offer_model>();
	private OfferListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_latest_offers);

		instance = this;

		if(getIntent().hasExtra(Constants.EXTRA_BEACON_UUID)) {
			String uuid = getIntent().getStringExtra(Constants.EXTRA_BEACON_UUID);
			int major = getIntent().getIntExtra(Constants.EXTRA_BEACON_MAJOR, 0);
			int minor = getIntent().getIntExtra(Constants.EXTRA_BEACON_MINOR, 0);
			new StatisticsBeaconAsync(this, StatisticType.STATISTIC_NOTIFICATION_INTERACTION, uuid, major, minor).execute();
		}
		
		txt_headertitle = (TextView) findViewById(R.id.txt_headertitle);
		String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
		txt_headertitle.setText(strTitles[Constants.SELECT_CASE_LATESTOFFER]);
		viewMain = (RelativeLayout) findViewById(R.id.layer_main);
		
		mData.clear();

		listViewOffers = (PullToRefreshListView) findViewById(R.id.lst_data);
		listViewOffers.setOnRefreshListener(this);
		listViewOffers.setScrollingWhileRefreshingEnabled(false);
		
		mAdapter = new OfferListAdapter(ActivityLatestOffers.this);
		listViewOffers.setAdapter(mAdapter);
		
		emptyHolder = (LinearLayout) findViewById(R.id.empty_holder);
		txtSorry = (TextView) findViewById(R.id.text_sorry);
		
		// set fonts
		FontUtils.setTypeface(txt_headertitle, FontUtils.font_HelveticaNeueUltraLight, false);
		FontUtils.setTypefaceAllView(viewMain, FontUtils.font_HelveticaNeueThin);
		FontUtils.setTypefaceAllView(emptyHolder, FontUtils.font_HelveticaNeueThin);
		FontUtils.setTypeface(txtSorry, FontUtils.font_HelveticaNeueMedium, false);
		
		BaseTask task = new BaseTask(Constants.TASK_GET_OFFERS);
		task.setListener(mTaskListener);
		task.execute();

		dialogWait.show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_right, R.anim.out_right);
	}

	public void onBackActivity(View paramView) {
		onBackPressed();
	}

	public void onMore(View paramView) {
		CommonUtil.makeBlurAndStartActivity(this, viewMain);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
    		int selMenu = mPrefs.getIntValue(Constants.SELECTED_MENU_NUMBER);
    		if (selMenu != Constants.SELECT_CASE_LATESTOFFER) {
    			if (ActivityMain.instance != null)
    				ActivityMain.instance.selectMainMenu();
    		}
    	}
    }

	private class OfferListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public OfferListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mData.size();
		}

		public Object getItem(int position) {
			return mData.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			LinearLayout tbl_data_layer;
			ImageView img_offer;
			TextView txt_title;
			TextView txt_content1;
			TextView txt_content2;
		}	

		public View getView(final int position, View convertView, ViewGroup parent) {
			offer_model model = mData.get(position);
			
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.row_offers, parent, false);
				holder = new ViewHolder();
				holder.tbl_data_layer = (LinearLayout) convertView.findViewById(R.id.tbl_data_layer);
				holder.img_offer = (ImageView) convertView.findViewById(R.id.img_offer);
				holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
				holder.txt_content1 = (TextView) convertView.findViewById(R.id.txt_content1);
				holder.txt_content2 = (TextView) convertView.findViewById(R.id.txt_content2);
				FontUtils.setTypeface(holder.txt_title, FontUtils.font_HelveticaNeueThin, false);
				FontUtils.setTypeface(holder.txt_content1, FontUtils.font_HelveticaNeueMedium, true);
				FontUtils.setTypeface(holder.txt_content2, FontUtils.font_HelveticaNeueThin, false);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if ((position % 2) == 0)
				holder.tbl_data_layer.setBackgroundResource(R.color.catelist_bg2);
			else
				holder.tbl_data_layer.setBackgroundResource(R.color.catelist_bg1);
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ActivityLatestOffers.this, ActivityOfferDetail.class);
					intent.putExtra(Constants.SELECTED_OFFER_ID, mData.get(position).mId);
					startActivity(intent);
					ActivityLatestOffers.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			});
			
			holder.txt_title.setText("" + mData.get(position).mTitle);
			holder.txt_content1.setText("" + mData.get(position).mContent1);
			holder.txt_content2.setText("" + mData.get(position).mContent2);
			
			holder.img_offer.setImageResource(R.drawable.default_thumb);
			if (!TextUtils.isEmpty(model.mPictureUrl))
				DCImageLoader.showImage(holder.img_offer, model.mPictureUrl);

			return convertView;
		}
	}

	TaskListener mTaskListener = new TaskListener() {
		@Override
		public Object onTaskRunning(int taskId, Object data) {
			Object result = null;
			if (taskId == Constants.TASK_GET_OFFERS) {
				result = Server.GetOffers();
			}
			return result;
		}
		
		@Override
		public void onTaskResult(int taskId, Object result) {
			if (taskId == Constants.TASK_GET_OFFERS) {
				if (result != null) {
					if (result instanceof OfferModelList) {
						OfferModelList res_model = (OfferModelList) result;
						for (OfferModel model : res_model.result)
							mData.add(new offer_model(model.id, model.shop_name, model.offer_image, model.offer_name, model.offer_detail));
					}
					
					if(mData.size() == 0) {
						emptyHolder.setVisibility(View.VISIBLE);
					} else {
						emptyHolder.setVisibility(View.GONE);
					}
				}
			}
			
			mAdapter.notifyDataSetChanged();
			listViewOffers.onRefreshComplete();

			if(dialogWait != null)
				dialogWait.hide();
		}
		
		@Override
		public void onTaskProgress(int taskId, Object... values) {
			
		}
		
		@Override
		public void onTaskPrepare(int taskId, Object data) {
			mData.clear();
			mAdapter.notifyDataSetChanged();
			emptyHolder.setVisibility(View.GONE);
		}
		
		@Override
		public void onTaskCancelled(int taskId) {
			dialogWait.hide();
		}
	};
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		BaseTask task = new BaseTask(Constants.TASK_GET_OFFERS);
		task.setListener(mTaskListener);
		task.execute();
	}
}
