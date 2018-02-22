package com.wai.whiteley.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseFragmentActivity;
import com.wai.whiteley.util.FontUtils;

public class ActivityMonsterCoach extends BaseFragmentActivity {

	public static final String EXTRA_KEY_FROM_MONSTER_VIEW = "extra_from_monster_view";
	
	public static ActivityMonsterCoach instance = null;

	private boolean isFromMosterView = false;
	
	private ImageButton btnClose;
	private Button btnNext;
	private TextView txt_desc1;
	private ViewPager mPager;
	private GalleryItemFragmentAdapter mAdapter;

	int[] resIds = new int[]{R.drawable.monster_coachcard1, R.drawable.monster_coachcard2, R.drawable.monster_coachcard3};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monster_coach);

		if(getIntent().hasExtra(EXTRA_KEY_FROM_MONSTER_VIEW))
			isFromMosterView = getIntent().getBooleanExtra(EXTRA_KEY_FROM_MONSTER_VIEW, false);
		
		instance = this;
		
		txt_desc1 = (TextView) findViewById(R.id.txt_desc1);
		btnClose = (ImageButton) findViewById(R.id.button_close);
		btnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isFromMosterView) {
					finish();
				} else {
					Intent intent = new Intent(ActivityMonsterCoach.this, ActivityMonsterView.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.in_left, R.anim.out_left);
				}
			}
		});
		btnNext = (Button) findViewById(R.id.button_next);
		btnNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mPager.getCurrentItem() < resIds.length - 1) {
					mPager.setCurrentItem(mPager.getCurrentItem() + 1);
				} else {
					if(isFromMosterView) {
						finish();
					} else {
						Intent intent = new Intent(ActivityMonsterCoach.this, ActivityMonsterView.class);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.in_left, R.anim.out_left);
					}
				}
			}
		});
		mPager = (ViewPager)findViewById(R.id.pager);
        mAdapter = new GalleryItemFragmentAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        
		// set fonts
		FontUtils.setTypeface(txt_desc1, FontUtils.font_Novecentowide_Light, false);
		FontUtils.setTypeface(btnNext, FontUtils.font_Novecentowide_Light, false);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_right, R.anim.out_right);
	}
	
	class GalleryItemFragmentAdapter extends FragmentPagerAdapter {

	    public GalleryItemFragmentAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int position) {
    		return MonsterCoachGalleryItemFragment.newInstance(resIds[position]);
	    }

	    @Override
	    public int getCount() {
	        return 3;
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	      return "GalleryItem_" + position;
	    }

	}
}
