package com.wai.whiteley.view;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;

import com.wai.whiteley.R;
import com.wai.whiteley.activities.ActivityCentreMap.LocationType;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.util.FontUtils;

public class SelectMallPopup extends PopupWindows implements OnDismissListener, OnCheckedChangeListener {

	private RadioButton radioFloor2;
	private RadioButton radioFloor1;
	private RadioButton radioFloorground;

	private LayoutInflater inflater;
	private OnActionItemClickListener mItemClickListener = null;
	private OnDismissListener mDismissListener = null;
	
	Handler mHandler = new Handler();
	
	public SelectMallPopup(Context context, LocationType locationType) {
		super(context);
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mRootView = inflater.inflate(R.layout.select_mall_popup, null);
		
		radioFloor2 = (RadioButton) mRootView.findViewById(R.id.txt_selectfloor2);
		radioFloor1 = (RadioButton) mRootView.findViewById(R.id.txt_selectfloor1);
		radioFloorground = (RadioButton) mRootView.findViewById(R.id.txt_selectfloorground);
		radioFloor2.setOnCheckedChangeListener(this);
		radioFloor1.setOnCheckedChangeListener(this);
		radioFloorground.setOnCheckedChangeListener(this);
		if (locationType == LocationType.UPPER_MALL) {
			radioFloor1.setSelected(true);
		} else if (locationType == LocationType.FOOD_COURT) {
			radioFloor2.setSelected(true);
		} else {
			radioFloorground.setSelected(true);
		}

		FontUtils.setTypeface(radioFloor2, FontUtils.font_HelveticaNeueThin, false);
		FontUtils.setTypeface(radioFloor1, FontUtils.font_HelveticaNeueThin, false);
		FontUtils.setTypeface(radioFloorground, FontUtils.font_HelveticaNeueThin, false);

		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		setContentView(mRootView);
	}

	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}

	/**
	 * Show popup mWindow
	 */
	public void show(final View anchor) {
		preShow(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = mRootView.getMeasuredHeight();

		int[] anchorLocation = new int[2];
		anchor.getLocationOnScreen(anchorLocation);
		
		int xPos = anchorLocation[0];
		int yPos = anchorLocation[1] - anchor.getHeight() - rootHeight - 10;
		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				LayoutParams params = mRootView.getLayoutParams();
				params.width = anchor.getWidth();
				mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	/**
	 * Set listener for window dismissed. This listener will only be fired if
	 * the quicakction dialog is dismissed by clicking outside the dialog or
	 * clicking on sticky item.
	 */
	public void setOnDismissListener(SelectMallPopup.OnDismissListener listener) {
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
		public abstract void onItemClick(SelectMallPopup source, View view);
	}

	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(SelectMallPopup.this, buttonView);
			}
			buttonView.postDelayed(new Runnable() {
				@Override
				public void run() {
					dismiss();
				}
			}, 10);
		}
	}
}
