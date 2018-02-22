package com.wai.whiteley.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.R;
import com.wai.whiteley.asynctask.GetStoreCategoryAsync;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.http.ResponseModel.StoreCategoryModel;
import com.wai.whiteley.util.FontUtils;
import com.wai.whiteley.view.BaseFragment;
import com.wai.whiteley.view.DCProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class OurStoresCategoryFragment extends BaseFragment implements OnRefreshListener<ListView> {

	 boolean isFromCentreMap = false;
	 
	private PullToRefreshListView listStoreCategories;
	private LinearLayout emptyHolder;
	private TextView txtSorry;
	private StoreCategoriesAdapter mAdapter;
	private String mSearchKeyword = "";

	private List<StoreCategoryModel> mData = new ArrayList<StoreCategoryModel>();
	
	DCProgressDialog myDlg;
	
	public static OurStoresCategoryFragment newInstance(boolean fromCentreMap) {
		OurStoresCategoryFragment fragment = new OurStoresCategoryFragment();
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
		listStoreCategories = (PullToRefreshListView) view.findViewById(R.id.lst_data);
		listStoreCategories.setOnRefreshListener(this);
		listStoreCategories.setScrollingWhileRefreshingEnabled(false);

		emptyHolder = (LinearLayout) view.findViewById(R.id.empty_holder);
		txtSorry = (TextView) view.findViewById(R.id.text_sorry);
		
		FontUtils.setTypefaceAllView(emptyHolder, FontUtils.font_HelveticaNeueThin);
		FontUtils.setTypeface(txtSorry, FontUtils.font_HelveticaNeueMedium, false);
		
		mData = DrakeCircusApplication.getInstance().dbHelper.getAllStoreCategories();
		
		if(mData == null || mData.size() == 0) {
			myDlg.show();
			
			GetStoreCategoryAsync.OnCompleteListener getStoreCategoriesOnCompleteListener = new GetStoreCategoryAsync.OnCompleteListener() {

				@Override
				public void onComplete(List<StoreCategoryModel> data) {
					myDlg.hide();
					
					mData = data;
					mAdapter = new StoreCategoriesAdapter(getActivity(), R.layout.row_stores3, mData);
					listStoreCategories.setAdapter(mAdapter);
					mAdapter.getFilter().filter(mSearchKeyword);
				}
				
			};
			
			GetStoreCategoryAsync asyncGetStoreNames = new GetStoreCategoryAsync();
			asyncGetStoreNames.setOnCompleteListener(getStoreCategoriesOnCompleteListener);
			asyncGetStoreNames.execute();
		} else {
			mAdapter = new StoreCategoriesAdapter(getActivity(), R.layout.row_stores3, mData);
			listStoreCategories.setAdapter(mAdapter);
			mAdapter.getFilter().filter(mSearchKeyword);
		}
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		mData = DrakeCircusApplication.getInstance().dbHelper.getAllStoreCategories();
		mAdapter = new StoreCategoriesAdapter(getActivity(), R.layout.row_stores3, mData);
		listStoreCategories.setAdapter(mAdapter);
		mAdapter.getFilter().filter(mSearchKeyword);
	}

	public void searchShop(String strSearch) {
		this.mSearchKeyword = strSearch;
		if(mAdapter != null)
			mAdapter.getFilter().filter(mSearchKeyword);
	}

	private class StoreCategoriesAdapter extends ArrayAdapter<StoreCategoryModel> implements Filterable {

		List<StoreCategoryModel> searchResults;
		HolderFilter holderFilter;
		
		private LayoutInflater mInflater;

		public StoreCategoriesAdapter(Context context, int resource, List<StoreCategoryModel> data) {
			super(context, resource, data);
			
			this.searchResults = data;
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return searchResults.size();
		}

		public StoreCategoryModel getItem(int position) {
			return searchResults.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			RelativeLayout tbl_data_layer;
			ImageView img_favourite;
			TextView txt_title;
			//ImageView img_arrow;
		}	

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.row_stores3, parent, false);
				holder = new ViewHolder();
				holder.tbl_data_layer = (RelativeLayout) convertView.findViewById(R.id.tbl_data_layer);
				holder.img_favourite = (ImageView) convertView.findViewById(R.id.img_favourite);
				holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			FontUtils.setTypeface(holder.txt_title, FontUtils.font_HelveticaNeue, false);

			if ((position % 2) == 0)
				holder.tbl_data_layer.setBackgroundResource(R.color.mainlist_bg1);
			else
				holder.tbl_data_layer.setBackgroundResource(R.color.mainlist_bg2);

			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(isFromCentreMap) {
						if(getActivity() instanceof ActivityOurStores) {
							((ActivityOurStores)getActivity()).showCategorySubStore(searchResults.get(position).id);
						}
					} else {
						Intent intent = new Intent(OurStoresCategoryFragment.this.getActivity(), ActivitySearchinCategory.class);
						intent.putExtra(Constants.SELECTED_STORE_CATEGORY, searchResults.get(position).name);
						intent.putExtra(Constants.SELECTED_STORE_CATEGORYID, searchResults.get(position).id);
						startActivity(intent);
						OurStoresCategoryFragment.this.getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
				}
			});
			holder.img_favourite.setVisibility(View.GONE);
			holder.txt_title.setText("" + searchResults.get(position).name);

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
	                List<StoreCategoryModel> nHolderList = new ArrayList<StoreCategoryModel>();
	                for (StoreCategoryModel h : mData) {
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
	            	searchResults = (ArrayList<StoreCategoryModel>) results.values;
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
		
		GetStoreCategoryAsync.OnCompleteListener getStoreCategoriesOnCompleteListener = new GetStoreCategoryAsync.OnCompleteListener() {

			@Override
			public void onComplete(List<StoreCategoryModel> data) {
				mData = data;
				mAdapter = new StoreCategoriesAdapter(getActivity(), R.layout.row_stores3, mData);
				listStoreCategories.setAdapter(mAdapter);
				mAdapter.getFilter().filter(mSearchKeyword);
				listStoreCategories.onRefreshComplete();
			}
			
		};
		
		GetStoreCategoryAsync asyncGetStoreNames = new GetStoreCategoryAsync();
		asyncGetStoreNames.setOnCompleteListener(getStoreCategoriesOnCompleteListener);
		asyncGetStoreNames.execute();
	}
}
