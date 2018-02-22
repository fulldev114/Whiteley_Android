package com.wai.whiteley.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

public class ImageUtil {
	
	public static Bitmap RotateBitmap(Bitmap source, float scale, float angle) {
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}
	
	public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
		
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options,
				reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.inDither=false;
		options.inPurgeable=true;
		options.inInputShareable=true;
		options.inTempStorage=new byte[32 * 1024];
		try {
			return BitmapFactory.decodeFile(filePath, options);
		} catch(OutOfMemoryError e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		int height = options.outHeight;
		int width = options.outWidth;

		int inSampleSize = 1;

		if (reqWidth > 0 && reqHeight > 0) {
			while (width / inSampleSize >= reqWidth
					|| height / inSampleSize >= reqHeight)
				inSampleSize *= 2;
	
			if (inSampleSize == 1)
				if (height > reqHeight || width > reqWidth)
					inSampleSize = 2;
		}
		
		if(inSampleSize > 1 && 
			(width / inSampleSize < reqWidth * 4 / 5 || 
			height / inSampleSize < reqHeight * 4 / 5)) {
			
			inSampleSize /= 2;
		}
		
		// Original Code
		/*
		 * if (height > reqHeight || width > reqWidth) { if (width > height) {
		 * inSampleSize = Math.round((float) height / (float) reqHeight); } else
		 * { inSampleSize = Math.round((float) width / (float) reqWidth); } }
		 */

		return inSampleSize;
	}
	
	public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) 
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else 
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.TRANSPARENT);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
	
	public static void releaseImages(View view) {
		if(view == null)
			return;
		
        if (view.getBackground() != null) {
        	view.getBackground().setCallback(null);
        }
        
    	if (view instanceof ImageView && ((ImageView) view).getDrawable() != null) {
			((ImageView) view).getDrawable().setCallback(null);
        }
        
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            	releaseImages(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
}
