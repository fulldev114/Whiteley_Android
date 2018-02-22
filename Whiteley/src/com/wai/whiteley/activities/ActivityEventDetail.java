package com.wai.whiteley.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wai.whiteley.R;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync;
import com.wai.whiteley.asynctask.StatisticsBeaconAsync.StatisticType;
import com.wai.whiteley.base.BaseFragmentActivity;
import com.wai.whiteley.base.BaseTask;
import com.wai.whiteley.base.BaseTask.TaskListener;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.http.ResponseModel.EventDetail;
import com.wai.whiteley.http.ResponseModel.EventDetailModel;
import com.wai.whiteley.http.ResponseModel.EventModel;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.model.AppModels.offer_model;
import com.wai.whiteley.service.BeaconDetectService;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.DCImageLoader;
import com.wai.whiteley.util.FontUtils;

public class ActivityEventDetail extends BaseFragmentActivity {

	public static ActivityEventDetail instance = null;

	private ScrollView scroll_main;
	private TextView txt_headertitle;
	private TextView txt_eventtitle;
	private TextView txt_eventtime;
	private TextView txt_description;
	private TextView txt_otherevent;
	private ImageView img_eventphoto;
	private LinearLayout layer_container;
	private LinearLayout layer_main;

	private ArrayList<offer_model> mData = new ArrayList<offer_model>();

	private int mEventId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eventdetail);

		instance = this;

		if(getIntent().hasExtra(Constants.EXTRA_BEACON_UUID)) {
			String uuid = getIntent().getStringExtra(Constants.EXTRA_BEACON_UUID);
			int major = getIntent().getIntExtra(Constants.EXTRA_BEACON_MAJOR, 0);
			int minor = getIntent().getIntExtra(Constants.EXTRA_BEACON_MINOR, 0);
			new StatisticsBeaconAsync(this, StatisticType.STATISTIC_NOTIFICATION_INTERACTION, uuid, major, minor).execute();
		}
		
		mEventId = getIntent().getIntExtra(Constants.SELECTED_EVENT_ID, 0);
		scroll_main = (ScrollView) findViewById(R.id.scroll_main);
		scroll_main.setVisibility(View.GONE);
		String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
		txt_headertitle = (TextView) findViewById(R.id.txt_headertitle);
		txt_headertitle.setText(strTitles[Constants.SELECT_CASE_EVENTS]);
		txt_eventtitle = (TextView) findViewById(R.id.txt_eventtitle);
		txt_eventtime = (TextView) findViewById(R.id.txt_eventtime);
		txt_description = (TextView) findViewById(R.id.txt_description);
		txt_otherevent = (TextView) findViewById(R.id.txt_otherevent);
		img_eventphoto = (ImageView) findViewById(R.id.img_eventphoto);
		layer_container = (LinearLayout) findViewById(R.id.layer_container);
		layer_main = (LinearLayout) findViewById(R.id.layer_main);

		mData.clear();

		// set fonts
		FontUtils.setTypeface(txt_headertitle, FontUtils.font_HelveticaNeueUltraLight, false);
		FontUtils.setTypefaceAllView(layer_main, FontUtils.font_HelveticaNeueThin);
		FontUtils.setTypeface(txt_eventtitle, FontUtils.font_HelveticaNeue, true);

		BaseTask task = new BaseTask(Constants.TASK_GET_EVENTDETAIL);
		task.setListener(mTaskListener);
		task.execute();

		dialogWait.show();
	}

	private void updateUIs(EventDetailModel detailModel) {
		if (!TextUtils.isEmpty(detailModel.image))
			DCImageLoader.showImage(img_eventphoto, detailModel.image);
		if (!TextUtils.isEmpty(detailModel.title))
			txt_eventtitle.setText(detailModel.title);
		if (!TextUtils.isEmpty(detailModel.date))
			txt_eventtime.setText(detailModel.date);
		if (!TextUtils.isEmpty(detailModel.text))
			txt_description.setText(detailModel.text);

		for (EventModel model : detailModel.other_events)
			mData.add(new offer_model(model.id, model.event_date, model.event_image, model.event_name, model.event_detail));

		int nSize = mData.size();
		
		if (nSize == 0) {
			txt_otherevent.setVisibility(View.GONE);
		}
		
		for (int i = 0; i < nSize; i ++) {
			View convertView = getLayoutInflater().inflate(R.layout.row_offers, null);
			convertView.setTag(mData.get(i).mId);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int eventId = Integer.parseInt(v.getTag().toString());
					Intent intent = new Intent(ActivityEventDetail.this, ActivityEventDetail.class);
					intent.putExtra(Constants.SELECTED_EVENT_ID, eventId);
					startActivity(intent);
					ActivityEventDetail.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			});
			LinearLayout tbl_data_layer = (LinearLayout) convertView.findViewById(R.id.tbl_data_layer);
			ImageView img_offer = (ImageView) convertView.findViewById(R.id.img_offer);
			TextView txt_title = (TextView) convertView.findViewById(R.id.txt_title);
			TextView txt_content1 = (TextView) convertView.findViewById(R.id.txt_content1);
			TextView txt_content2 = (TextView) convertView.findViewById(R.id.txt_content2);

			FontUtils.setTypeface(txt_title, FontUtils.font_HelveticaNeueThin, false);
			FontUtils.setTypeface(txt_content1, FontUtils.font_HelveticaNeueMedium, true);
			FontUtils.setTypeface(txt_content2, FontUtils.font_HelveticaNeueThin, false);

			if ((i % 2) == 0)
				tbl_data_layer.setBackgroundResource(R.color.catelist_bg2);
			else
				tbl_data_layer.setBackgroundResource(R.color.catelist_bg1);

			txt_title.setText("" + mData.get(i).mTitle);
			txt_content1.setText("" + mData.get(i).mContent1);
			txt_content2.setText("" + mData.get(i).mContent2);
			if (!TextUtils.isEmpty(mData.get(i).mPictureUrl))
				DCImageLoader.showImage(img_offer, mData.get(i).mPictureUrl);
			else
				img_offer.setVisibility(View.GONE);
			
			layer_container.addView(convertView);
		}

		scroll_main.setVisibility(View.VISIBLE);
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
		CommonUtil.makeBlurAndStartActivity(this, scroll_main);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
			if (ActivityMain.instance != null)
				ActivityMain.instance.selectMainMenu();
    	}
    }

	TaskListener mTaskListener = new TaskListener() {
		@Override
		public Object onTaskRunning(int taskId, Object data) {
			Object result = null;
			if (taskId == Constants.TASK_GET_EVENTDETAIL) {
				result = Server.GetEventDetail(mEventId);
			}
			return result;
		}
		
		@Override
		public void onTaskResult(int taskId, Object result) {
			if (taskId == Constants.TASK_GET_EVENTDETAIL) {
				if (result != null) {
					if (result instanceof EventDetail) {
						EventDetail res_model = (EventDetail) result;
						updateUIs(res_model.result);
					}
					else {
						
					}
				}
				else {
					
				}
			}
			
			dialogWait.hide();
		}
		
		@Override
		public void onTaskProgress(int taskId, Object... values) {
			
		}
		
		@Override
		public void onTaskPrepare(int taskId, Object data) {
		}
		
		@Override
		public void onTaskCancelled(int taskId) {
			dialogWait.hide();
		}
	};
}
