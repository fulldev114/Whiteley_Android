package com.wai.whiteley.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.fading_entrances.FadeInAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutAnimator;
import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.R;
import com.wai.whiteley.asynctask.GetStoreNamesAsync;
import com.wai.whiteley.base.BaseActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.database.dao.StoreNameDAO;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.FontUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ActivitySearchinCategory extends BaseActivity implements OnRefreshListener<ListView> {

	public static ActivitySearchinCategory instance = null;

	private RelativeLayout layer_main;
	private TextView txt_headertitle;
	private EditText edt_search;

	private PullToRefreshListView listViewStores;
	private LasyAdapter mAdapter;
	private LinearLayout emptyHolder;
	private TextView txtSorry;
	
	private String mSearchKeyword = "";
	private List<StoreNameDAO> mData = new ArrayList<StoreNameDAO>();
	
	private String mCategoryTitle = "";
	private int mCategoryId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchincategory);

		instance = this;

		mCategoryTitle = getIntent().getStringExtra(Constants.SELECTED_STORE_CATEGORY);
		mCategoryId = getIntent().getIntExtra(Constants.SELECTED_STORE_CATEGORYID, 0);

		layer_main = (RelativeLayout) findViewById(R.id.layer_main);
		txt_headertitle = (TextView) findViewById(R.id.txt_headertitle);
		if (!TextUtils.isEmpty(mCategoryTitle))
			txt_headertitle.setText(mCategoryTitle);
		else
			txt_headertitle.setText(getResources().getString(R.string.strToygame_title));
		edt_search = (EditText) findViewById(R.id.edt_search);
		edt_search.addTextChangedListener(new TextWatcher() {

		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		    	mSearchKeyword = cs.toString();
				if(mAdapter != null)
					mAdapter.getFilter().filter(mSearchKeyword);
		    }
		    
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		    	
		    }
		    
		    @Override
		    public void afterTextChanged(Editable arg0) {}
		});

		listViewStores = (PullToRefreshListView) findViewById(R.id.lst_data);
		listViewStores.setOnRefreshListener(this);
		listViewStores.setScrollingWhileRefreshingEnabled(false);

		emptyHolder = (LinearLayout) findViewById(R.id.empty_holder);
		txtSorry = (TextView) findViewById(R.id.text_sorry);
		
		// set fonts
		FontUtils.setTypeface(txt_headertitle, FontUtils.font_HelveticaNeueUltraLight, false);
		FontUtils.setTypeface(edt_search, FontUtils.font_HelveticaNeueThin, false);
		FontUtils.setTypefaceAllView(emptyHolder, FontUtils.font_HelveticaNeueThin);
		FontUtils.setTypeface(txtSorry, FontUtils.font_HelveticaNeueMedium, false);

		mData = DrakeCircusApplication.getInstance().dbHelper.getStoreInfos(mCategoryId);
		
		if(mData == null || mData.size() == 0) {
			dialogWait.show();
			
			GetStoreNamesAsync.OnCompleteListener getStoreNamesOnCompleteListener = new GetStoreNamesAsync.OnCompleteListener() {

				@Override
				public void onComplete(List<StoreNameDAO> data) {
					dialogWait.hide();
					
					mData = data;
					mAdapter = new LasyAdapter(ActivitySearchinCategory.this, R.layout.row_stores3, mData);
					listViewStores.setAdapter(mAdapter);
					mAdapter.getFilter().filter(mSearchKeyword);
				}
				
			};
			
			GetStoreNamesAsync asyncGetStoreNames = new GetStoreNamesAsync(this);
			asyncGetStoreNames.setOnCompleteListener(getStoreNamesOnCompleteListener);
			asyncGetStoreNames.execute(mCategoryId);
		} else {
			mAdapter = new LasyAdapter(this, R.layout.row_stores3, mData);
			listViewStores.setAdapter(mAdapter);
			mAdapter.getFilter().filter(mSearchKeyword);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		mData = DrakeCircusApplication.getInstance().dbHelper.getStoreInfos(mCategoryId);
		mAdapter = new LasyAdapter(this, R.layout.row_stores3, mData);
		listViewStores.setAdapter(mAdapter);
		mAdapter.getFilter().filter(mSearchKeyword);
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
		CommonUtil.makeBlurAndStartActivity(this, layer_main);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
			if (ActivityMain.instance != null)
				ActivityMain.instance.selectMainMenu();
    	}
    }

	private class LasyAdapter extends ArrayAdapter<StoreNameDAO> implements Filterable {

		List<StoreNameDAO> searchResults;
		HolderFilter holderFilter;
		private LayoutInflater mInflater;

		public LasyAdapter(Context context, int resource, List<StoreNameDAO> data) {
			super(context, resource, data);
			
			this.searchResults = data;
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return searchResults.size();
		}

		public StoreNameDAO getItem(int position) {
			return searchResults.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			RelativeLayout tbl_data_layer;
			ImageView img_favourite;
			TextView txt_title;
			ImageView img_favourite_show;
		}	

		public View getView(final int position, View convertView, ViewGroup parent) {
			StoreNameDAO model = searchResults.get(position);
			
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.row_stores3, parent, false);
				holder = new ViewHolder();
				holder.tbl_data_layer = (RelativeLayout) convertView.findViewById(R.id.tbl_data_layer);
				holder.img_favourite = (ImageView) convertView.findViewById(R.id.img_favourite);
				holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
				holder.img_favourite_show = (ImageView) convertView.findViewById(R.id.img_favourite_show);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			FontUtils.setTypeface(holder.txt_title, FontUtils.font_HelveticaNeue, false);

			if ((position % 2) == 0)
				holder.tbl_data_layer.setBackgroundResource(R.color.catelist_bg1);
			else
				holder.tbl_data_layer.setBackgroundResource(R.color.catelist_bg2);
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ActivitySearchinCategory.this, ActivityShopDetail.class);
					intent.putExtra(Constants.SELECTED_STORE_NAME, searchResults.get(position).name);
					intent.putExtra(Constants.SELECTED_STORE_ID, searchResults.get(position).id);
					startActivity(intent);
					ActivitySearchinCategory.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			});
			
			if (model.favourite == 1) {
				if(model.hasOffer == 0)
					holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_on));
				else
					holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_on_plusoffer));
			} else {
				if(model.hasOffer == 0)
					holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_off));
				else
					holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_off_plusoffer));
			}
			holder.txt_title.setText("" + model.name);

			holder.img_favourite.setTag(model);
			final ViewHolder _fianl_holder = holder;
			holder.img_favourite.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ImageView vw = (ImageView) v;
					StoreNameDAO data = (StoreNameDAO) vw.getTag();
					if (data.favourite == 1) {
						data.favourite = 0;
						
						if(data.hasOffer == 0)
							vw.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_off));
						else
							vw.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_off_plusoffer));
					} else {
						data.favourite = 1;
						if(data.hasOffer == 0)
							vw.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_on));
						else
							vw.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_on_plusoffer));
					}
					
					if (data.favourite == 1) {
						_fianl_holder.img_favourite_show.setVisibility(View.VISIBLE);
						FadeInAnimator anim = new FadeInAnimator();
						anim.setTarget(_fianl_holder.img_favourite_show);
						anim.setDuration(Constants.FADING_TIMEOUT);
						anim.animate();

						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								FadeOutAnimator anim = new FadeOutAnimator();
								anim.setTarget(_fianl_holder.img_favourite_show);
								anim.setDuration(Constants.FADING_TIMEOUT);
								anim.animate();
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										_fianl_holder.img_favourite_show.setVisibility(View.GONE);
									}
								}, Constants.FADING_TIMEOUT);
							}
						}, Constants.SHOWFAVOURITE_TIMEOUT);
					}

					StoreNameDAO store = DrakeCircusApplication.getInstance().dbHelper.getOneStore(data.id);
					store.favourite = data.favourite;
					DrakeCircusApplication.getInstance().dbHelper.updateStoreFavorites(store.id, store.favourite);
				}
			});

			return convertView;
		}
		
		@Override
		public Filter getFilter() {
			if (holderFilter == null){
	            holderFilter = new HolderFilter();
	        }
	        return holderFilter;
		}
		
		private class HolderFilter extends Filter {
	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	            FilterResults results = new FilterResults();
	            if (constraint == null || constraint.length() == 0) {
	                results.values = mData;
	                results.count = mData.size();
	            }else {
	                List<StoreNameDAO> nHolderList = new ArrayList<StoreNameDAO>();
	                for (StoreNameDAO h : mData) {
	                	if (h.name.toLowerCase().startsWith(constraint.toString().toLowerCase()))
	                        nHolderList.add(h);
	                }
	                results.values = nHolderList;
	                results.count = nHolderList.size();
	            }
	            return results;
	        }
	        
	        @SuppressWarnings("unchecked")
	        @Override
	        protected void publishResults(CharSequence constraint,FilterResults results) {
//	            if (results.count == 0)
//	                notifyDataSetInvalidated();
//	            else {
	            	searchResults = (ArrayList<StoreNameDAO>) results.values;
	                notifyDataSetChanged();
	                
	                if(searchResults.size() == 0) {
						emptyHolder.setVisibility(View.VISIBLE);
					} else {
						emptyHolder.setVisibility(View.GONE);
					}
//	            }
	        }
	    }
	}
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		GetStoreNamesAsync.OnCompleteListener getStoreNamesOnCompleteListener = new GetStoreNamesAsync.OnCompleteListener() {

			@Override
			public void onComplete(List<StoreNameDAO> data) {
				mData = data;
				mAdapter = new LasyAdapter(ActivitySearchinCategory.this, R.layout.row_stores3, mData);
				listViewStores.setAdapter(mAdapter);
				mAdapter.getFilter().filter(mSearchKeyword);
				
				listViewStores.onRefreshComplete();
			}
			
		};
		
		GetStoreNamesAsync asyncGetStoreNames = new GetStoreNamesAsync(this);
		asyncGetStoreNames.setOnCompleteListener(getStoreNamesOnCompleteListener);
		asyncGetStoreNames.execute(mCategoryId);		
	}
}
