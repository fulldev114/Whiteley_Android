package com.wai.whiteley.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class MonsterThumbnailView extends LinearLayout {

	private ImageView _imageView;

	public MonsterThumbnailView(Context context) {
		super(context);
		init(context);
	}

	public void setImage(Bitmap bitmap) {
		_imageView.setImageDrawable(new BitmapDrawable(bitmap));
	}
	
//	public void setImage(String url) {
//		DCImageLoader.showImage(_imageView, url);
//	}

	private void init(Context context) {

		setOrientation(LinearLayout.VERTICAL);

		_imageView = new ImageView(context);
		_imageView.setScaleType(ScaleType.FIT_CENTER);

		LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);

		imageViewParams.gravity = Gravity.CENTER;
		_imageView.setLayoutParams(imageViewParams);

		addView(_imageView);
	}
}
