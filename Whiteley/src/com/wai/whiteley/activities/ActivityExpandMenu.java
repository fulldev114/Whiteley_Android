package com.wai.whiteley.activities;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.model.AppModels.Store_model;
import com.wai.whiteley.util.FontUtils;

public class ActivityExpandMenu extends BaseActivity {

	private ListView mListView;
	private LazyAdapter mAdapter;
	private ArrayList<Store_model> mDataList = new ArrayList<Store_model>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expand_menu);

		mListView = (ListView) findViewById(R.id.lst_data);
		String strBgFileName = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ Constants.IMAGE_PATH
				+ Constants.BACKGROUND_IMAGE_FILENAME;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			mListView.setBackground(new BitmapDrawable(strBgFileName));
		else
			mListView.setBackgroundDrawable(new BitmapDrawable(strBgFileName));

		String[] strTitles = getResources().getStringArray(
				R.array.store_titlearray);
		String[] strDescs = getResources().getStringArray(
				R.array.store_descarray);
		for (int i = 0; i < strTitles.length; i++)
			mDataList.add(new Store_model(i, strTitles[i], strDescs[i]));

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPrefs.setIntValue(Constants.SELECTED_MENU_NUMBER, position);
				mAdapter.notifyDataSetChanged();
				setResult(RESULT_OK);
				ActivityExpandMenu.this.finish();
				ActivityExpandMenu.this.overridePendingTransition(
						R.anim.fade_in, R.anim.fade_out);
			}
		});
		mAdapter = new LazyAdapter();
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	public void onHome(View paramView) {
		mPrefs.setIntValue(Constants.SELECTED_MENU_NUMBER, -1);
		setResult(RESULT_OK);
		ActivityExpandMenu.this.finish();
		ActivityExpandMenu.this.overridePendingTransition(R.anim.fade_in,
				R.anim.fade_out);
	}

	public void onClose(View paramView) {
		onBackPressed();
	}

	public class LazyAdapter extends BaseAdapter {
		private LayoutInflater inflater = null;

		public LazyAdapter() {
			inflater = (LayoutInflater) ActivityExpandMenu.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return mDataList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			public LinearLayout viewRoot;
			public ImageView img_store;
			public TextView txt_title;
			public ImageView row_divider;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder _holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_stores2, parent,
						false);
				_holder = new ViewHolder();
				_holder.viewRoot = (LinearLayout) convertView
						.findViewById(R.id.view_root);
				_holder.img_store = (ImageView) convertView
						.findViewById(R.id.img_store);
				_holder.txt_title = (TextView) convertView
						.findViewById(R.id.txt_title);
				_holder.row_divider = (ImageView) convertView
						.findViewById(R.id.row_divider);

				convertView.setTag(_holder);
			} else {
				_holder = (ViewHolder) convertView.getTag();
			}

			FontUtils.setTypeface(_holder.txt_title,
					FontUtils.font_HelveticaNeueThin, false);

			Store_model data = mDataList.get(position);

			if (mPrefs.getIntValue(Constants.SELECTED_MENU_NUMBER) == position) {
				_holder.viewRoot
						.setBackgroundResource(R.color.expandlist_bg_hl);
			} else {
				_holder.viewRoot
						.setBackgroundResource(R.drawable.selector_menu_bg);
			}

			_holder.txt_title.setText(data.mTitle);
			switch (data.icon_type) {
			case Constants.SELECT_CASE_OURSTORE:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_stores));
				break;
			case Constants.SELECT_CASE_CENTREMAP:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_map));
				break;
			case Constants.SELECT_CASE_LATESTOFFER:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_offers));
				break;
			case Constants.SELECT_CASE_FOOD:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_food));
				break;
			case Constants.SELECT_CASE_CINEMA:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_cinema));
				break;	
			case Constants.SELECT_CASE_ROCKUP:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_rockup));
				break;	
			case Constants.SELECT_CASE_TRAVEL:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_car));
				break;
			case Constants.SELECT_CASE_PARKING:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_parking));
				break;
			case Constants.SELECT_CASE_EVENTS:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_events));
				break;
			case Constants.SELECT_CASE_OPENING_HOURS:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_opening_hours));
				break;
			//case Constants.SELECT_CASE_MONSTER:
			//	_holder.img_store.setImageDrawable(getResources().getDrawable(
			//			R.drawable.icon_monster));
			//	break;
			case Constants.SELECT_CASE_SIGNUP:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_signup));
				break;	
			case Constants.SELECT_CASE_FEEDBACK:
				_holder.img_store.setImageDrawable(getResources().getDrawable(
						R.drawable.icon_feedback));
				break;	
			default:
				break;
			}

			if (mDataList.size() == (position + 1)) // last
				_holder.row_divider.setVisibility(View.GONE);
			else
				_holder.row_divider.setVisibility(View.VISIBLE);

			return convertView;
		}
	}
}
