package com.wai.whiteley.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FontUtils {
	public static final String str_title = "fonts/HelveticaNeue.ttf";
	public static final String str_HelveticaNeueLight = "fonts/HelveticaNeue-Light.ttf";
	public static final String str_HelveticaNeueUltraLight = "fonts/HelveticaNeue-UltraLight.ttf";
	public static final String str_HelveticaNeue = "fonts/HelveticaNeue.ttf";
	public static final String str_HelveticaNeueMedium = "fonts/HelveticaNeue-Medium.ttf";
	public static final String str_HelveticaNeueThin = "fonts/HelveticaNeue-Thin.ttf";
	
	public static final String str_Novecentowide_Bold = "fonts/Novecento/Novecentowide-Bold.otf";
	public static final String str_Novecentowide_Book = "fonts/Novecento/Novecentowide-Book.otf";
	public static final String str_Novecentowide_DemiBold = "fonts/Novecento/Novecentowide-DemiBold.otf";
	public static final String str_Novecentowide_Light = "fonts/Novecento/Novecentowide-Light.otf";
	public static final String str_Novecentowide_Medium = "fonts/Novecento/Novecentowide-Medium.otf";
	public static final String str_Novecentowide_Normal = "fonts/Novecento/Novecentowide-Normal.otf";
	
	public static Typeface font_title = null;
	public static Typeface font_HelveticaNeueLight = null;
	public static Typeface font_HelveticaNeueUltraLight = null;
	public static Typeface font_HelveticaNeue = null;
	public static Typeface font_HelveticaNeueMedium = null;
	public static Typeface font_HelveticaNeueThin = null;
	
	public static Typeface font_Novecentowide_Bold = null;
	public static Typeface font_Novecentowide_Book = null;
	public static Typeface font_Novecentowide_DemiBold = null;
	public static Typeface font_Novecentowide_Light = null;
	public static Typeface font_Novecentowide_Medium = null;
	public static Typeface font_Novecentowide_Normal = null;
	
	public static void initialize(Context context) {
		makeTitleFont(context);
		makeBodyFont(context);
	}
	
	public static void makeTitleFont(Context context) {
		if (font_title != null)
			return;
		
		font_title = Typeface.createFromAsset(context.getAssets(), str_title);
	}
	
	public static void makeBodyFont(Context context) {
		if (font_HelveticaNeueUltraLight != null)
			return;
		
		font_HelveticaNeueUltraLight = Typeface.createFromAsset(context.getAssets(), str_HelveticaNeueUltraLight);
		font_HelveticaNeueLight = Typeface.createFromAsset(context.getAssets(), str_HelveticaNeueLight);
		font_HelveticaNeue = Typeface.createFromAsset(context.getAssets(), str_HelveticaNeue);
		font_HelveticaNeueMedium = Typeface.createFromAsset(context.getAssets(), str_HelveticaNeueMedium);
		font_HelveticaNeueThin = Typeface.createFromAsset(context.getAssets(), str_HelveticaNeueThin);
		
		font_Novecentowide_Bold = Typeface.createFromAsset(context.getAssets(), str_Novecentowide_Bold);
		font_Novecentowide_Book = Typeface.createFromAsset(context.getAssets(), str_Novecentowide_Book);
		font_Novecentowide_DemiBold = Typeface.createFromAsset(context.getAssets(), str_Novecentowide_DemiBold);
		font_Novecentowide_Light = Typeface.createFromAsset(context.getAssets(), str_Novecentowide_Light);
		font_Novecentowide_Medium = Typeface.createFromAsset(context.getAssets(), str_Novecentowide_Medium);
		font_Novecentowide_Normal = Typeface.createFromAsset(context.getAssets(), str_Novecentowide_Normal);
	}
	
	/*
	 * 
	 * set font type to all view of viewgroup  
	 */
	public static void setTypefaceAllView(ViewGroup vg, Typeface face) {

		for (int i = 0; i < vg.getChildCount(); ++i) {

			View child = vg.getChildAt(i);

			if (child instanceof ViewGroup) {

				setTypefaceAllView((ViewGroup) child, face);

			} else if (child != null) {
				if (child instanceof TextView) {
					TextView textView = (TextView) child;
					textView.setTypeface(face);
				}
			}
		}
	}

	public static void setTypeface(View vw, Typeface face, boolean bold) {
		if (vw instanceof TextView) {
			TextView textView = (TextView) vw;
			if (!bold)
				textView.setTypeface(face);
			else
				textView.setTypeface(face, 1);
		}
		if (vw instanceof Button) {
			Button btn = (Button) vw;
			if (!bold)
				btn.setTypeface(face);
			else
				btn.setTypeface(face, 1);
		}
	}
	
	public static void setFontSizeAllView(ViewGroup vg, float size) {
		if (vg == null)
			return;
		
		for (int i = 0; i < vg.getChildCount(); ++i) {

			View child = vg.getChildAt(i);

			if (child instanceof ViewGroup) {
				setFontSizeAllView((ViewGroup) child, size);

			} else if (child != null) {
				if (child instanceof TextView) {
					TextView view = (TextView) child;
					view.setTextSize(size);
				}
			}
		}
	}
	
	public static void setLargeFont(ViewGroup vg) {
//		int app_font_size = ConfigMgr.getAppFontSize();
//		int font_size = 0;
//		switch (app_font_size) {
//		case Constants.APP_FONT_SIZE_LARGE:
//			font_size = Constants.FONT_EXTRA_LARGE;
//			break;
//		case Constants.APP_FONT_SIZE_NORMAL:
//			font_size = Constants.FONT_LARGE;
//			break;
//		case Constants.APP_FONT_SIZE_SMALL:
//			font_size = Constants.FONT_NORMAL;
//			break;
//		default:
//			break;
//		}
//		
//		setFontSizeAllView(vg, font_size);
	}
	
	public static void setNormalFont(ViewGroup vg) {
//		int app_font_size = ConfigMgr.getAppFontSize();
//		int font_size = 0;
//		switch (app_font_size) {
//		case Constants.APP_FONT_SIZE_LARGE:
//			font_size = Constants.FONT_LARGE;
//			break;
//		case Constants.APP_FONT_SIZE_NORMAL:
//			font_size = Constants.FONT_NORMAL;
//			break;
//		case Constants.APP_FONT_SIZE_SMALL:
//			font_size = Constants.FONT_SMALL;
//			break;
//		default:
//			break;
//		}
//		
//		setFontSizeAllView(vg, font_size);
	}
	
	public static void setSmallFont(ViewGroup vg) {
//		int app_font_size = ConfigMgr.getAppFontSize();
//		int font_size = 0;
//		switch (app_font_size) {
//		case Constants.APP_FONT_SIZE_LARGE:
//			font_size = Constants.FONT_NORMAL;
//			break;
//		case Constants.APP_FONT_SIZE_NORMAL:
//			font_size = Constants.FONT_SMALL;
//			break;
//		case Constants.APP_FONT_SIZE_SMALL:
//			font_size = Constants.FONT_EXTRA_SMALL;
//			break;
//		default:
//			break;
//		}
//		
//		setFontSizeAllView(vg, font_size);
	}
	
	public static void setLargeFont(TextView tv) {
//		int app_font_size = ConfigMgr.getAppFontSize();
//		int font_size = 0;
//		switch (app_font_size) {
//		case Constants.APP_FONT_SIZE_LARGE:
//			font_size = Constants.FONT_EXTRA_LARGE;
//			break;
//		case Constants.APP_FONT_SIZE_NORMAL:
//			font_size = Constants.FONT_LARGE;
//			break;
//		case Constants.APP_FONT_SIZE_SMALL:
//			font_size = Constants.FONT_NORMAL;
//			break;
//		default:
//			break;
//		}
//		
//		tv.setTextSize(font_size);
	}
	
	public static void setNormalFont(TextView tv) {
//		int app_font_size = ConfigMgr.getAppFontSize();
//		int font_size = 0;
//		switch (app_font_size) {
//		case Constants.APP_FONT_SIZE_LARGE:
//			font_size = Constants.FONT_LARGE;
//			break;
//		case Constants.APP_FONT_SIZE_NORMAL:
//			font_size = Constants.FONT_NORMAL;
//			break;
//		case Constants.APP_FONT_SIZE_SMALL:
//			font_size = Constants.FONT_SMALL;
//			break;
//		default:
//			break;
//		}
//		
//		tv.setTextSize(font_size);
	}
	
	public static void setSmallFont(TextView tv) {
//		int app_font_size = ConfigMgr.getAppFontSize();
//		int font_size = 0;
//		switch (app_font_size) {
//		case Constants.APP_FONT_SIZE_LARGE:
//			font_size = Constants.FONT_NORMAL;
//			break;
//		case Constants.APP_FONT_SIZE_NORMAL:
//			font_size = Constants.FONT_SMALL;
//			break;
//		case Constants.APP_FONT_SIZE_SMALL:
//			font_size = Constants.FONT_EXTRA_SMALL;
//			break;
//		default:
//			break;
//		}
//		
//		tv.setTextSize(font_size);
	}
}
