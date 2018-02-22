package com.wai.whiteley.activities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.R;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.http.ResponseModel.HomeCarouselResponse.HomeCarousel;
import com.wai.whiteley.http.ResponseModel.StoreCategoryModel;

public final class GalleryItemFragment extends Fragment {
    private static final String KEY_CONTENT = "GalleryItemFragment:Content";

    private HomeCarousel mCarousel = null;
    
    public static GalleryItemFragment newInstance(HomeCarousel carousel) {
    	GalleryItemFragment fragment = new GalleryItemFragment();
        fragment.mContent = "GalleryItem_" + carousel.id;
        fragment.mCarousel = carousel;

        return fragment;
    }

    private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView imgItem = new ImageView(getActivity());
        imgItem.setScaleType(ScaleType.CENTER_CROP);
        imgItem.setImageResource(R.drawable.default_carousel);
        
        if(mCarousel != null) {
	        if(mCarousel.image != null) {
//		        if(mCarousel.image.startsWith("http")) {
//		        	DCImageLoader.showImage(imgItem, mCarousel.image);
//		        } else {
//		        	imgItem.setImageResource(Integer.parseInt(mCarousel.image));	
//		        }
	        	
	        	try {
	        		URL urlLabel = new URL(mCarousel.image);
		        	File fileLabel = new File(getActivity().getFilesDir(), urlLabel.getFile());
		        	
		        	if(fileLabel.exists()) {
		        		Bitmap bitmap = BitmapFactory.decodeFile(fileLabel.getAbsolutePath());
		        		imgItem.setImageBitmap(bitmap);
		        	}
	        	} catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        }
	        
	        imgItem.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	        imgItem.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mCarousel != null && mCarousel.link_type != null) {
						
						if(mCarousel.link_type.equals("app")) {
							
							if(mCarousel.link_app != null) {
								if(mCarousel.link_app.equals("store")) {
									Intent intent = new Intent(getActivity(), ActivityOurStores.class);
									startActivity(intent);
									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								} else if(mCarousel.link_app.equals("map")) {
									Intent intent = new Intent(getActivity(), ActivityCentreMap.class);
									startActivity(intent);
									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								} else if(mCarousel.link_app.equals("offers")) {
									Intent intent = new Intent(getActivity(), ActivityLatestOffers.class);
									startActivity(intent);
									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								} else if(mCarousel.link_app.equals("offer_id")) {
									try {
										int offerId = Integer.parseInt(mCarousel.link_id);
										Intent intent = new Intent(getActivity(), ActivityOfferDetail.class);
										intent.putExtra(Constants.SELECTED_OFFER_ID, offerId);
										startActivity(intent);
										getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
									} catch(Exception e) {
										e.printStackTrace();
									}
								} else if(mCarousel.link_app.equals("events")) {
									Intent intent = new Intent(getActivity(), ActivityLatestEvents.class);
									startActivity(intent);
									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								} else if(mCarousel.link_app.equals("event_id")) {
									try {
										int eventId = Integer.parseInt(mCarousel.link_id);
										Intent intent = new Intent(getActivity(), ActivityEventDetail.class);
										intent.putExtra(Constants.SELECTED_EVENT_ID, eventId);
										startActivity(intent);
										getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
									} catch(Exception e) {
										e.printStackTrace();
									}
								} else if(mCarousel.link_app.equals("food")) {
									ArrayList<StoreCategoryModel> categories = DrakeCircusApplication.getInstance().dbHelper.getAllStoreCategories();
									for (StoreCategoryModel model : categories) {
										if (model.name.contains("Food")) {
											Intent intent = new Intent(getActivity(), ActivitySearchinCategory.class);
											intent.putExtra(Constants.SELECTED_STORE_CATEGORY, "Food Outlets");
											intent.putExtra(Constants.SELECTED_STORE_CATEGORYID, model.id);
											startActivity(intent);
											getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
											break;
										}
									}
								} else if(mCarousel.link_app.equals("here")) {
									Intent intent = new Intent(getActivity(), ActivityGettingHere.class);
									startActivity(intent);
									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								} else if(mCarousel.link_app.equals("facilities")) {
									Intent intent = new Intent(getActivity(), ActivityCentreMap.class);
									intent.putExtra(Constants.SHOW_FACILITIES, true);
									startActivity(intent);
									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								} else if(mCarousel.link_app.equals("gift")) {
//									Intent intent = new Intent(getActivity(), ActivityGiftCard.class);
//									startActivity(intent);
//									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								} else if(mCarousel.link_app.equals("game")) {
									Intent intent = new Intent(getActivity(), ActivityMonsterStart.class);
									startActivity(intent);
									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								} else if(mCarousel.link_app.equals("cinema")) {
									Intent intent = new Intent(getActivity(), ActivityWebView.class);
									intent.putExtra(ActivityWebView.EXTRA_URL, "http://www1.cineworld.co.uk/cinemas/whiteley");
									String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
									intent.putExtra(ActivityWebView.EXTRA_TITLE, strTitles[Constants.SELECT_CASE_CINEMA]);
									startActivity(intent);
									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								} else if (mCarousel.link_app.equals("rock")) {
									Intent intent = new Intent(getActivity(), ActivityWebView.class);
									intent.putExtra(ActivityWebView.EXTRA_URL, "http://www.rock-up.co.uk/book-online");
									String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
									intent.putExtra(ActivityWebView.EXTRA_TITLE, strTitles[Constants.SELECT_CASE_ROCKUP]);
									startActivity(intent);
									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								} else if (mCarousel.link_app.equals("sign")) {
									Intent intent = new Intent(getActivity(), ActivityWebView.class);
									intent.putExtra(ActivityWebView.EXTRA_URL, "http://eepurl.com/JEbsb");
									String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
									intent.putExtra(ActivityWebView.EXTRA_TITLE, strTitles[Constants.SELECT_CASE_SIGNUP]);
									startActivity(intent);
									getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
								}
							}
							
						} else if(mCarousel.link_type.equals("url")) {
							String url = mCarousel.link_url;
							Intent intent = new Intent(getActivity(), ActivityWebView.class);
							intent.putExtra(ActivityWebView.EXTRA_URL, url);
							intent.putExtra(ActivityWebView.EXTRA_TITLE, getString(R.string.app_name));
							startActivity(intent);
							getActivity().overridePendingTransition(R.anim.in_left, R.anim.out_left);
						}
					}
				}
			});
        }
        return imgItem;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
