package com.wai.whiteley.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.database.dao.StoreNameDAO;
import com.wai.whiteley.util.FontUtils;
import com.wai.whiteley.view.BaseFragment;
import com.wai.whiteley.view.DCProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class OurStoresNameFragment extends BaseFragment implements OnRefreshListener<ListView> {

	private int mCategoryId;
	private boolean isFromCentreMap = false;
	
	private PullToRefreshListView listViewStoreNames;
	private LinearLayout emptyHolder;
	private TextView txtSorry;
	
	private StoreNamesAdapter mAdapter;
	private String mSearchKeyword = "";
	private List<StoreNameDAO> mData = new ArrayList<StoreNameDAO>();
	
	private DCProgressDialog myDlg;
	
	public static OurStoresNameFragment newInstance(int categoryId, boolean fromCentreMap) {
		OurStoresNameFragment fragment = new OurStoresNameFragment();
		fragment.mCategoryId = categoryId;
		fragment.isFromCentreMap = fromCentreMap;
		
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ourstores_list, container, false);

		myDlg = new DCProgressDialog(getActivity());
		listViewStoreNames = (PullToRefreshListView) view.findViewById(R.id.lst_data);
		listViewStoreNames.setOnRefreshListener(this);
		listViewStoreNames.setScrollingWhileRefreshingEnabled(false);
		emptyHolder = (LinearLayout) view.findViewById(R.id.empty_holder);
		txtSorry = (TextView) view.findViewById(R.id.text_sorry);
		
		FontUtils.setTypefaceAllView(emptyHolder, FontUtils.font_HelveticaNeueThin);
		FontUtils.setTypeface(txtSorry, FontUtils.font_HelveticaNeueMedium, false);
		
		List<StoreNameDAO> initData = DrakeCircusApplication.getInstance().dbHelper.getStoreInfos(mCategoryId);
		
		if(initData == null || initData.size() == 0) {
			myDlg.show();
			
			GetStoreNamesAsync.OnCompleteListener getStoreNamesOnCompleteListener = new GetStoreNamesAsync.OnCompleteListener() {

				@Override
				public void onComplete(List<StoreNameDAO> data) {
					myDlg.hide();
					
					for (StoreNameDAO store : data) {
						if (!store.name.equals("New Retailer Coming Soon")) {
							mData.add(store);
						}
					}
					
					mAdapter = new StoreNamesAdapter(getActivity(), R.layout.row_stores3, mData);
					listViewStoreNames.setAdapter(mAdapter);
					mAdapter.getFilter().filter(mSearchKeyword);
				}
				
			};
			
			GetStoreNamesAsync asyncGetStoreNames = new GetStoreNamesAsync(getActivity());
			asyncGetStoreNames.setOnCompleteListener(getStoreNamesOnCompleteListener);
			asyncGetStoreNames.execute();
		} else {
			
			for (StoreNameDAO store : initData) {
				if (!store.name.equals("New Retailer Coming Soon")) {
					mData.add(store);
				}
			}
			
			mAdapter = new StoreNamesAdapter(getActivity(), R.layout.row_stores3, mData);
			listViewStoreNames.setAdapter(mAdapter);
			mAdapter.getFilter().filter(mSearchKeyword);
		}
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		mData.clear();
		List<StoreNameDAO> initData = DrakeCircusApplication.getInstance().dbHelper.getStoreInfos(mCategoryId);

		for (StoreNameDAO store : initData) {
			if (!store.name.equals("New Retailer Coming Soon")) {
				mData.add(store);
			}
		}
		
		mAdapter = new StoreNamesAdapter(getActivity(), R.layout.row_stores3, mData);
		listViewStoreNames.setAdapter(mAdapter);
		mAdapter.getFilter().filter(mSearchKeyword);
	}

	public void searchShop(String strSearch) {
		this.mSearchKeyword = strSearch;
		if(mAdapter != null)
			mAdapter.getFilter().filter(mSearchKeyword);
	}

	private class StoreNamesAdapter extends ArrayAdapter<StoreNameDAO> implements Filterable {

		List<StoreNameDAO> searchResults;
		HolderFilter holderFilter;
		
		private LayoutInflater mInflater;

		public StoreNamesAdapter(Context context, int resource, List<StoreNameDAO> data) {
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

			FontUtils.setTypeface(holder.txt_title, FontUtils.font_HelveticaNeueLight, false);

			if ((position % 2) == 0)
				holder.tbl_data_layer.setBackgroundResource(R.color.mainlist_bg1);
			else
				holder.tbl_data_layer.setBackgroundResource(R.color.mainlist_bg2);

			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(isFromCentreMap) {
						((ActivityOurStores)getActivity()).finish(searchResults.get(position).location, searchResults.get(position).unitNum);
					} else {
						Intent intent = new Intent(OurStoresNameFragment.this.getActivity(), ActivityShopDetail.class);
						intent.putExtra(Constants.SELECTED_STORE_ID, searchResults.get(position).id);
						startActivity(intent);
						OurStoresNameFragment.this.getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
				}
			});
			
			if (model.favourite == 0) {
				if(model.hasOffer == 0)
					holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_off));
				else
					holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_off_plusoffer));
			} else {
				if(model.hasOffer == 0)
					holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_on));
				else
					holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_on_plusoffer));
			}
				
			holder.txt_title.setText("" + model.name);
			holder.img_favourite.setTag(model);
			
			final ViewHolder _fianl_holder = holder;
			
			holder.img_favourite.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					StoreNameDAO storeNameDao = (StoreNameDAO) v.getTag();
					
					if (storeNameDao.favourite == 0) {
						if(storeNameDao.hasOffer == 0)
							_fianl_holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_on));
						else
							_fianl_holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_on_plusoffer));
						
						
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
						
					} else {
						if(storeNameDao.hasOffer == 0)
							_fianl_holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_off));
						else
							_fianl_holder.img_favourite.setImageDrawable(getResources().getDrawable(R.drawable.icon_favourite_off_plusoffer));
					}

					storeNameDao.favourite = (storeNameDao.favourite == 0 ? 1 : 0);
					DrakeCircusApplication.getInstance().dbHelper.updateStoreFavorites(storeNameDao.id, storeNameDao.favourite);
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
	                
	                if(getActivity() instanceof ActivityOurStores) {
						((ActivityOurStores)getActivity()).setEmptyHolderStatus(searchResults.size() == 0);
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
				mData.clear();
				for (StoreNameDAO store : data) {
					if (!store.name.equals("New Retailer Coming Soon")) {
						mData.add(store);
					}
				}
				
				mAdapter = new StoreNamesAdapter(getActivity(), R.layout.row_stores3, mData);
				listViewStoreNames.setAdapter(mAdapter);
				mAdapter.getFilter().filter(mSearchKeyword);
				
				listViewStoreNames.onRefreshComplete();
			}
			
		};
		
		GetStoreNamesAsync asyncGetStoreNames = new GetStoreNamesAsync(getActivity());
		asyncGetStoreNames.setOnCompleteListener(getStoreNamesOnCompleteListener);
		asyncGetStoreNames.execute();
	}
	
}
