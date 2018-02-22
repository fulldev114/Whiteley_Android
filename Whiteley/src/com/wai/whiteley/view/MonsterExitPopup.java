package com.wai.whiteley.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;

import com.wai.whiteley.R;
import com.wai.whiteley.util.FontUtils;

public class MonsterExitPopup extends PopupWindows implements OnDismissListener, OnCheckedChangeListener {

	private Button btnExitKeep;
	private Button btnExitStop;

	private LayoutInflater inflater;
	private OnActionItemClickListener mItemClickListener = null;
	private OnDismissListener mDismissListener = null;
	
	Handler mHandler = new Handler();
	
	private int mAnimStyle;
	
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_AUTO = 4;
	
	public MonsterExitPopup(Context context) {
		super(context);
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mRootView = inflater.inflate(R.layout.monster_exit_popup, null);
		
		btnExitKeep = (Button)mRootView.findViewById(R.id.button_exit_keep);
		btnExitKeep.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mItemClickListener.onItemClick(MonsterExitPopup.this, v);
				dismiss();
			}
		});
		btnExitStop = (Button)mRootView.findViewById(R.id.button_exit_stop);
		btnExitStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mItemClickListener.onItemClick(MonsterExitPopup.this, v);
				
				dismiss();
			}
		});
		
		
		FontUtils.setTypefaceAllView((LinearLayout)mRootView, FontUtils.font_Novecentowide_Medium);

		mRootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		setContentView(mRootView);
		
		mAnimStyle = ANIM_GROW_FROM_LEFT;
	}

	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}

	/**
	 * Show popup mWindow
	 */
	public void show(final View anchor) {
		preShow(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

		mRootView.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = mRootView.getMeasuredWidth();
		int rootHeight = mRootView.getMeasuredHeight();
		
		int[] anchorLocation = new int[2];
		anchor.getLocationOnScreen(anchorLocation);
		
		Rect anchorRect = new Rect(anchorLocation[0], anchorLocation[1], anchorLocation[0]
				+ anchor.getWidth(), anchorLocation[1] + anchor.getHeight());
		
		int xPos = 0;
		int yPos = anchorLocation[1] + anchor.getHeight();
		
		setAnimationStyle(rootWidth, anchorRect.centerX(), false);
		
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	/**
	 * Set listener for window dismissed. This listener will only be fired if
	 * the quicakction dialog is dismissed by clicking outside the dialog or
	 * clicking on sticky item.
	 */
	public void setOnDismissListener(MonsterExitPopup.OnDismissListener listener) {
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
		public abstract void onItemClick(MonsterExitPopup source, View view);
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
				mItemClickListener.onItemClick(MonsterExitPopup.this, buttonView);
			}
			buttonView.postDelayed(new Runnable() {
				@Override
				public void run() {
					dismiss();
				}
			}, 10);
		}
	}
	
	/**
	 * Set animation style
	 * 
	 * @param screenWidth
	 *            Screen width
	 * @param requestedX
	 *            distance from left screen
	 * @param onTop
	 *            flag to indicate where the popup should be displayed. Set TRUE
	 *            if displayed on top of anchor and vice versa
	 */
	private void setAnimationStyle(int screenWidth, int requestedX,
			boolean onTop) {
		int arrowPos = requestedX - mRootView.getMeasuredWidth() / 2;

		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
					: R.style.Animations_PopDownMenu_Left);
			break;

		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
					: R.style.Animations_PopDownMenu_Right);
			break;

		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
					: R.style.Animations_PopDownMenu_Center);
			break;

		case ANIM_AUTO:
			if (arrowPos <= screenWidth / 4) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
						: R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth / 4
					&& arrowPos < 3 * (screenWidth / 4)) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
						: R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopDownMenu_Right
						: R.style.Animations_PopDownMenu_Right);
			}

			break;
		}
	}
}
