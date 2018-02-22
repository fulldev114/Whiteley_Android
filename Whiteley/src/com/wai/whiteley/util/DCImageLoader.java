package com.wai.whiteley.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class DCImageLoader {
	public static class IMG_TYPE {
		public static int NORMAL = 0;
	}
	
	private static ImageLoader instance = ImageLoader.getInstance();
	
	public static DisplayImageOptions normal_options;

	public static void init() {
		normal_options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.considerExifParams(true)
			.displayer(new SimpleBitmapDisplayer())
			.imageScaleType(ImageScaleType.NONE)
			.build();
	}
	
	public static void clearCache() {
		if (instance != null) {
			instance.clearDiscCache();
			instance.clearMemoryCache();
		}
	}
	
	public static void stop() {
		if (instance != null) {
			instance.stop();
		}
	}
	
	public static void showImage(ImageView imgView, String url) {
		if (TextUtils.isEmpty(url))
			return;
		
		DisplayImageOptions option = normal_options;
		try {
			instance.displayImage(url, imgView, option, animateFirstListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
