package com.wai.whiteley.view;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.daimajia.androidanimations.library.fading_entrances.FadeInAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutAnimator;
import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.R;
import com.wai.whiteley.activities.ActivityCentreMap;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.model.FacilityData;
import com.wai.whiteley.model.ShopData;

public class ShowStorePopup extends PopupWindows implements OnDismissListener, OnClickListener {

	private ActivityCentreMap mContext;
	private View mAnchor;
	private int mPosX = 0;
	private int mPosY = 0;
	
	private ShopData mShopData;
	
	private LinearLayout view_favourite_show;
	private ImageView img_favourite_show;
	private ImageView img_favourite;
	private TextView txt_title;
	private LinearLayout viewGotoStore;

	private LayoutInflater inflater;
	private OnActionItemClickListener mItemClickListener = null;
	private OnDismissListener mDismissListener = null;
	
	Handler mHandler = new Handler();
	
	public ShowStorePopup(Context context, ShopData shopData) {
		super(context);
		
		isModal = true;
		this.mContext = (ActivityCentreMap)context;
		this.mShopData = shopData;
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mRootView = inflater.inflate(R.layout.show_store_popup, null);
		
		viewGotoStore = (LinearLayout) mRootView.findViewById(R.id.view_goto_store);
		viewGotoStore.setTag(this.mShopData);
		viewGotoStore.setOnClickListener(this);
		txt_title = (TextView) mRootView.findViewById(R.id.txt_title);
		txt_title.setText(mShopData.storeId == 0 ? "Empty Shop" : mShopData.storeName);

		img_favourite = (ImageView) mRootView.findViewById(R.id.img_favourite);
		img_favourite.setOnClickListener(this);
		if (mShopData.favorite == 1) {
			if(mShopData.hasOffer == 0)
				img_favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favourite_on));
			else
				img_favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favourite_on_plusoffer));
		} else {
			if(mShopData.hasOffer == 0)
				img_favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favourite_off));
			else
				img_favourite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favourite_off_plusoffer));
		}

		view_favourite_show = (LinearLayout) mRootView.findViewById(R.id.view_favourite_show);
		view_favourite_show.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				LayoutParams params = view_favourite_show.getLayoutParams();
				params.width = mRootView.getWidth();
				view_favourite_show.setLayoutParams(params);
				
				view_favourite_show.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		view_favourite_show.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					dismiss();
					
					int[] anchorLocation = new int[2];
					mAnchor.getLocationOnScreen(anchorLocation);
					
					float x = event.getX() + mPosX - anchorLocation[0];
					float y = event.getY() + mPosY - anchorLocation[1];
					event.setLocation(x, y);
					
					mContext.onSingleTapMapView(event);
				}
				return false;
			}
		});
		img_favourite_show = (ImageView) mRootView.findViewById(R.id.img_favourite_show);
		img_favourite_show.setVisibility(View.INVISIBLE);
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.setBackgroundResource(android.R.color.transparent);

		setContentView(mRootView);
	}
	
	public ShowStorePopup(Context context, int favorites, int offer, FacilityData facilityData) {
		super(context);
		
		isModal = true;
		this.mContext = (ActivityCentreMap)context;
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mRootView = inflater.inflate(R.layout.show_store_popup, null);
		
		viewGotoStore = (LinearLayout) mRootView.findViewById(R.id.view_goto_store);
		viewGotoStore.setOnClickListener(this);
		txt_title = (TextView) mRootView.findViewById(R.id.txt_title);
		img_favourite = (ImageView) mRootView.findViewById(R.id.img_favourite);
		ImageView img_arrow = (ImageView) mRootView.findViewById(R.id.img_arrow);
		img_arrow.setVisibility(View.INVISIBLE);
		
		int iconResId = R.drawable.icon_facility_atm_purple;
		switch(facilityData.mType) {
		case ATM:
			iconResId = R.drawable.icon_facility_atm_purple;
			break;
		case PHOTO_BOOTHS:
			iconResId = R.drawable.icon_facility_photobooth_purple;
			break;
		case CAR_PARKING:
			iconResId = R.drawable.icon_facility_parking_purple;
			break;
		/*
		case RECYCLING_BINS:
			iconResId = R.drawable.icon_facility_recyclingbin_purple;
			break;
		case GIFT_CARD:
			iconResId = R.drawable.icon_facility_giftcard_urple;
			break;
		*/
		case TOILETS_BABY_CHANGE:
			iconResId = R.drawable.icon_facility_toilet_purple;
			break;
		case INFORMATION_POD:
			iconResId = R.drawable.icon_facility_infopod_purple;
			break;
		case LOST_FOUND:
			iconResId = R.drawable.icon_facility_lostfound_purple;
			break;
		/*
		case KIDDIES_RIDE:
			iconResId = R.drawable.icon_facility_kiddiesride_purple;
			break;
		case SHOP_MOBILITY:
			iconResId = R.drawable.icon_facility_shopmobility_purple;
			break;
		case IBEACON_LOCATION:
			iconResId = R.drawable.icon_facility_ibeacon_purple;
			break;
			*/
		default:
			iconResId = R.drawable.icon_facility_phonecharging_purple;
		}
		
		txt_title.setText(context.getResources().getStringArray(R.array.facility_type)[facilityData.mType.ordinal()]);
		img_favourite.setImageResource(iconResId);
		
		view_favourite_show = (LinearLayout) mRootView.findViewById(R.id.view_favourite_show);
		view_favourite_show.setVisibility(View.GONE);
		img_favourite_show = (ImageView) mRootView.findViewById(R.id.img_favourite_show);
		img_favourite_show.setVisibility(View.GONE);
		
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRootView.setBackgroundResource(android.R.color.transparent);

		setContentView(mRootView);
	}

	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}

	/**
	 * Show popup mWindow
	 */
	public void show(View anchor, PointF centerPoint) {
		this.mAnchor = anchor;
		
		preShow(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

		int[] anchorLocation = new int[2];
		anchor.getLocationOnScreen(anchorLocation);
		
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = mRootView.getMeasuredWidth();
		int rootHeight = mRootView.getMeasuredHeight();

		mPosX = (int)centerPoint.x - rootWidth / 2 + anchorLocation[0];
		mPosY = (int)centerPoint.y - rootHeight + anchorLocation[1] - 10;
		
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, mPosX, mPosY);
	}

	/**
	 * Set listener for window dismissed. This listener will only be fired if
	 * the quicakction dialog is dismissed by clicking outside the dialog or
	 * clicking on sticky item.
	 */
	public void setOnDismissListener(ShowStorePopup.OnDismissListener listener) {
		setOnDismissListener(this);

		mDismissListener = listener;
	}

	@Override
	public void onDismiss() {
		if (mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}

	/**
	 * Listener for item click
	 * 
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick(ShowStorePopup source, View view);
	}

	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}

	@Override
	public void onClick(final View v) {
		
		if (mItemClickListener != null) {
			mItemClickListener.onItemClick(ShowStorePopup.this, v);
		}

		boolean bCloseDlg = true;
		if (v.getId() == R.id.img_favourite) {
			bCloseDlg = false;
			
			if (mShopData.favorite == 0) {
				if(mShopData.hasOffer == 0)
					img_favourite.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_favourite_on));
				else
					img_favourite.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_favourite_on_plusoffer));
				
				img_favourite_show.setVisibility(View.VISIBLE);
				FadeInAnimator anim = new FadeInAnimator();
				anim.setTarget(img_favourite_show);
				anim.setDuration(Constants.FADING_TIMEOUT);
				anim.animate();

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						FadeOutAnimator anim = new FadeOutAnimator();
						anim.setTarget(img_favourite_show);
						anim.setDuration(Constants.FADING_TIMEOUT);
						anim.animate();
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								img_favourite_show.setVisibility(View.INVISIBLE);
							}
						}, Constants.FADING_TIMEOUT);
					}
				}, Constants.SHOWFAVOURITE_TIMEOUT);
				
			} else {
				if(mShopData.hasOffer == 0)
					img_favourite.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_favourite_off));
				else
					img_favourite.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_favourite_off_plusoffer));
			}

			mShopData.favorite = (mShopData.favorite == 0 ? 1 : 0);
			DrakeCircusApplication.getInstance().dbHelper.updateStoreFavorites(mShopData.storeId, mShopData.favorite);
			
		}

		if (bCloseDlg) {
			v.postDelayed(new Runnable() {
				@Override
				public void run() {
					dismiss();
				}
			}, 10);
		}
	}
	
	public boolean isShowing() {
		return mWindow.isShowing();
	}
}
