package com.wai.whiteley.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.daimajia.androidanimations.library.fading_entrances.FadeInAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutAnimator;
import com.daimajia.androidanimations.library.sliders.SlideInRightAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutLeftAnimator;
import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseActivity;
import com.wai.whiteley.config.AppPreferences;
import com.wai.whiteley.view.OnSwipeTouchListener;

public class ActivityCoach extends BaseActivity {

	private static int VIDEO_STEP_SHOW_FAVOURITE = 0;
	private static int VIDEO_STEP_SHOW_OFFER = 1;
	//private static int VIDEO_STEP_SHOW_BLUETOOTH = 2;
	private static int VIDEO_STEP_SHOW_BEACON = 2;
	private static int VIDEO_STEP_SHOW_END = 3;

	private VideoView video_view;
	private ImageView btnNext;
	private ImageView imgDesc;
	private RelativeLayout viewSwipeOverlay;

	private int animTimeout = 500; // 500 ms
	private int mStep = VIDEO_STEP_SHOW_FAVOURITE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coach);

		video_view = (VideoView) findViewById(R.id.video_view);
		video_view.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				if(mStep >= VIDEO_STEP_SHOW_END) {
					mPrefs.setBooleanValue(AppPreferences.ALREADY_SHOW_COACH_MOVIE, true);
					Intent intent = new Intent(ActivityCoach.this, ActivityMain.class);
					startActivity(intent);
					ActivityCoach.this.finish();
				} else {
					btnNext.performClick();
				}
				return true;
			}
		});
		btnNext = (ImageView) findViewById(R.id.button_next);
		btnNext.setVisibility(View.INVISIBLE);
		imgDesc = (ImageView) findViewById(R.id.img_desc);
		imgDesc.setVisibility(View.INVISIBLE);
		viewSwipeOverlay = (RelativeLayout) findViewById(R.id.view_swipe_overlay);
		viewSwipeOverlay.setOnTouchListener(new OnSwipeTouchListener(this) {
			@Override
			public void onSwipeLeft() {
				super.onSwipeLeft();
				btnNext.performClick();
			}
		});
		
		video_view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				int videoSize = Math.min(video_view.getWidth(), video_view.getHeight());
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(videoSize, videoSize);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				video_view.setLayoutParams(params);
				
				video_view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		playVideo();
	}

	private void playVideo() {
		String strUri = "android.resource://" + getPackageName() + "/" + R.raw.section_01;
		final Uri videourI = Uri.parse(strUri);
		video_view.setVideoURI(videourI);
		video_view.setZOrderOnTop(true);
		video_view.start();
		mStep = VIDEO_STEP_SHOW_FAVOURITE;
		video_view.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (mStep >= VIDEO_STEP_SHOW_END) {
					mPrefs.setBooleanValue(AppPreferences.ALREADY_SHOW_COACH_MOVIE, true);
					Intent intent = new Intent(ActivityCoach.this, ActivityMain.class);
					startActivity(intent);
					ActivityCoach.this.finish();
					//overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			}
		});

		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnNext.setEnabled(false);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						btnNext.setEnabled(true);
					}
				}, 500);
				
				
				if(video_view.isPlaying()) {
					video_view.stopPlayback();
				}
				
				mStep++;
				int resId = 0;
				String strUri = "android.resource://" + getPackageName() + "/";
				if (mStep == VIDEO_STEP_SHOW_OFFER) {
					strUri += R.raw.section_02;
					resId = R.drawable.coach_desc_2;
				}
				//else if (mStep == VIDEO_STEP_SHOW_BLUETOOTH) {
				//	strUri += R.raw.section_03;
				//	resId = R.drawable.coach_desc_3;
				//}
				else if (mStep == VIDEO_STEP_SHOW_BEACON) {
					strUri += R.raw.section_03;
					resId = R.drawable.coach_desc_3;
				}
				else if (mStep >= VIDEO_STEP_SHOW_END) {
					FadeOutAnimator anim1 = new FadeOutAnimator();
					anim1.setTarget(btnNext);
					anim1.setDuration(animTimeout);
					anim1.animate();

					strUri += R.raw.section_04;
					resId = R.drawable.coach_desc_3;
				}

				video_view.setVideoURI(Uri.parse(strUri));
				video_view.start();
				
				final int f_resId = resId;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						SlideOutLeftAnimator anim2 = new SlideOutLeftAnimator();
						anim2.setTarget(imgDesc);
						anim2.setDuration(animTimeout);
						anim2.animate();

						if (mStep < VIDEO_STEP_SHOW_END) {
							new Handler().postDelayed(new Runnable() {
								@SuppressWarnings("deprecation")
								@Override
								public void run() {
									imgDesc.setImageResource(f_resId);
									SlideInRightAnimator anim3 = new SlideInRightAnimator();
									anim3.setTarget(imgDesc);
									anim3.setDuration(animTimeout);
									anim3.animate();
								}
							}, animTimeout);
							
						}
					}
				}, animTimeout);
			}
		});

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				imgDesc.setVisibility(View.VISIBLE);
				SlideInRightAnimator anim4 = new SlideInRightAnimator();
				anim4.setTarget(imgDesc);
				anim4.setDuration(animTimeout);
				anim4.animate();
			}
		}, 500);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				btnNext.setVisibility(View.VISIBLE);
				FadeInAnimator anim5 = new FadeInAnimator();
				anim5.setTarget(btnNext);
				anim5.setDuration(animTimeout * 2);
				anim5.animate();
			}
		}, animTimeout * 2);

	}
}
