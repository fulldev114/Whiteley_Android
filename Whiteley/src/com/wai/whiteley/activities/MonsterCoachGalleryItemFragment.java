package com.wai.whiteley.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.wai.whiteley.R;
import com.wai.whiteley.http.ResponseModel.HomeCarouselResponse.HomeCarousel;
import com.wai.whiteley.util.DCImageLoader;

public final class MonsterCoachGalleryItemFragment extends Fragment {
    private static final String KEY_CONTENT = "MonsterCoachGalleryItemFragment:Content";

    private int resId;
    
    public static MonsterCoachGalleryItemFragment newInstance(int resourceId) {
    	MonsterCoachGalleryItemFragment fragment = new MonsterCoachGalleryItemFragment();
        fragment.mContent = "MonsterCoachGalleryItem_" + resourceId;
        fragment.resId = resourceId;

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
        imgItem.setScaleType(ScaleType.FIT_CENTER);
    	imgItem.setImageResource(resId);	
        imgItem.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        return imgItem;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
