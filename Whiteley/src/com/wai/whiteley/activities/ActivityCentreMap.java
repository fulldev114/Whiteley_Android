package com.wai.whiteley.activities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.fading_entrances.FadeInAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutAnimator;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.AnimationBuilder;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.database.dao.StoreNameDAO;
import com.wai.whiteley.model.FacilityData;
import com.wai.whiteley.model.ShopData;
import com.wai.whiteley.model.UserLocation;
import com.wai.whiteley.polygon.Line;
import com.wai.whiteley.polygon.Point;
import com.wai.whiteley.polygon.Polygon;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.FontUtils;
import com.wai.whiteley.util.GeolocationUtil;
import com.wai.whiteley.util.ImageUtil;
import com.wai.whiteley.view.DrakeCircusMapView;
import com.wai.whiteley.view.DrakeCircusMapView.OnExtraDrawListener;
import com.wai.whiteley.view.ShowStorePopup;
import com.wai.whiteley.view.ShowStorePopup.OnActionItemClickListener;

public class ActivityCentreMap extends BaseActivity implements LocationListener, OnExtraDrawListener {

	public static final String EXTRA_KEY_LOCATION = "extra_key_location";
	public static final String EXTRA_KEY_UNITNUM = "extra_key_unitnum";
	
	public enum FacilityType {
		ATM,
		PHOTO_BOOTHS,
		CAR_PARKING,
		TOILETS_BABY_CHANGE,
		INFORMATION_POD,
		LOST_FOUND,
		//PHONE_CHARGING,
		//RECYCLING_BINS,
		//GIFT_CARD,
		//KIDDIES_RIDE,
		//SHOP_MOBILITY,
		//IBEACON_LOCATION,
	}
	
	public enum LocationType {
		G_LOWER_MALL,
		UPPER_MALL,
		FOOD_COURT
	}
	
	private float polygonCoordinateRatio = 1920.f / 2560.f;
	private float mMapWidth = 1920.f;
	private float mMapHeight = 3408.f;
	
	private double a11 = 0.00890434949266055f;
	private double a12 = 0.00987327055587534f;
	private double a13 = -0.440765957499249f;
	private double a21 = 0.0175081941027169f;
	private double a22 = -0.00520653163009943f;
	private double a23 = -0.897337705040420f;
	private double a31 = 1.77422923713616e-07f;
	private double a32 = 9.30692883361414e-08f;
	private double a33 = -8.87764112131846e-06f;
	
	public static ActivityCentreMap instance = null;

	private RelativeLayout layer_main;
	private TextView txt_headertitle;
	private TextView txt_search;
	private DrakeCircusMapView drakeCircusMapView;
	private LinearLayout layer_facility;
	//private TextView txt_selectfacility;
	//private TextView txt_selectmall;
	
	private boolean shouldMoveMap = false;
	private LocationType mLocationType = LocationType.G_LOWER_MALL;
	
	private UserLocation leftTopLocation = new UserLocation(Constants.MAP_LEFT_LAT, Constants.MAP_LEFT_LONG);
	private UserLocation rightBottomLocation = new UserLocation(Constants.MAP_RIGHT_LAT, Constants.MAP_RIGHT_LONG);
	private UserLocation myLocation = null;
	private ImageView img_message;

	private GridView listFacilityType;
	private LasyAdapter mAdapter;
	
	private ShopData mSelectedShop = null;
	private FacilityData mSelectedFacility = null;
	
	private ArrayList<ShopData> mCurrentShops = null;
	
	private ArrayList<ShopData> mLowerMallShops = new ArrayList<ShopData>();
	private ArrayList<ShopData> mUpperMallShops = new ArrayList<ShopData>();
	private ArrayList<ShopData> m2ndFloorShops = new ArrayList<ShopData>();
	
	private ArrayList<FacilityData> mFacilities = new ArrayList<FacilityData>();
	private HashMap<FacilityType, Bitmap> mBitmapFacilities = new HashMap<FacilityType, Bitmap>();
	private HashMap<FacilityType, Bitmap> mHighlightBitmapFacilities = new HashMap<FacilityType, Bitmap>();
	
	private HashMap<String, Bitmap> mLowerMallLabels = new HashMap<String, Bitmap>();
	private HashMap<String, Bitmap> mUpperMallLabels = new HashMap<String, Bitmap>();
	private HashMap<String, Bitmap> m2ndFloorLabels = new HashMap<String, Bitmap>();

	private ShowStorePopup popupStore = null;
	private boolean isMapAnimating = false;
	private boolean isUserShowing = false;
	private Float sourceX = 0.f;
	private Float sourceY = 0.f;
	
	private ArrayList<UserLocation> aryLocation = new ArrayList<UserLocation>();
	private int locIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_centremap_new);
		instance = this;

		initPolygons();
		
		aryLocation.add(new UserLocation(50.884777, -1.244871));
		aryLocation.add(new UserLocation(50.884901, -1.244963));
		aryLocation.add(new UserLocation(50.884979, -1.245028));
		aryLocation.add(new UserLocation(50.885321, -1.245435));
		aryLocation.add(new UserLocation(50.885514, -1.245618));
		aryLocation.add(new UserLocation(50.885808, -1.245843));
		aryLocation.add(new UserLocation(50.885947, -1.245891));
		aryLocation.add(new UserLocation(50.886062, -1.245553));
		aryLocation.add(new UserLocation(50.886394, -1.245232));
		aryLocation.add(new UserLocation(50.886702, -1.245548));
		aryLocation.add(new UserLocation(50.886576, -1.246026));
		aryLocation.add(new UserLocation(50.886455, -1.246347));
		aryLocation.add(new UserLocation(50.886370, -1.246514));
		aryLocation.add(new UserLocation(50.886235, -1.246911));
		aryLocation.add(new UserLocation(50.886141, -1.247164));
		aryLocation.add(new UserLocation(50.886363, -1.248617));
		aryLocation.add(new UserLocation(50.886052, -1.248697));
		aryLocation.add(new UserLocation(50.885930, -1.247635));
		aryLocation.add(new UserLocation(50.886011, -1.247558));
		aryLocation.add(new UserLocation(50.885192, -1.247157));
		aryLocation.add(new UserLocation(50.885040, -1.247179));
		aryLocation.add(new UserLocation(50.884999, -1.246948));
		aryLocation.add(new UserLocation(50.885183, -1.246354));
		aryLocation.add(new UserLocation(50.884644, -1.246921));
		aryLocation.add(new UserLocation(50.884963, -1.246748));
		aryLocation.add(new UserLocation(50.884671, -1.246962));
		aryLocation.add(new UserLocation(50.884644, -1.246739));
		aryLocation.add(new UserLocation(50.884390, -1.245945));
		aryLocation.add(new UserLocation(50.884393, -1.245725));
		aryLocation.add(new UserLocation(50.884556, -1.245543));
		aryLocation.add(new UserLocation(50.884228, -1.245366));
		aryLocation.add(new UserLocation(50.884157, -1.245199));

		if(getIntent().hasExtra(EXTRA_KEY_LOCATION)) {
			String location = getIntent().getStringExtra(EXTRA_KEY_LOCATION);
			
			if (location.equals("G Lower Mall"))
				mLocationType = LocationType.G_LOWER_MALL;
			else if (location.equals("1 Upper Mall"))
				mLocationType = LocationType.UPPER_MALL;
			else if (location.equals("2 Food Court"))
				mLocationType = LocationType.FOOD_COURT;
		}
				
		txt_headertitle = (TextView) findViewById(R.id.txt_headertitle);
		String[] strTitles = getResources().getStringArray(R.array.store_titlearray);
		txt_headertitle.setText(strTitles[Constants.SELECT_CASE_CENTREMAP]);
		txt_search = (TextView) findViewById(R.id.txt_search);
		layer_main = (RelativeLayout) findViewById(R.id.layer_main);
		drakeCircusMapView = (DrakeCircusMapView)findViewById(R.id.imageMapView);
		drakeCircusMapView.setOnExtraDrawListener(this);
		//drakeCircusMapView.setDebug(true);
		drakeCircusMapView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
		drakeCircusMapView.setMaxScale(2.4f);

		final GestureDetector mapGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
	        @Override
	        public boolean onSingleTapConfirmed(MotionEvent e) {
	        	if(!isMapAnimating)
	        		onSingleTapMapView(e);
	        	
	            return true;
	        }
	        @Override
	        public void onLongPress(MotionEvent e) {
//			            if (drakeCircusMapView != null && drakeCircusMapView.isReady()) {
//			                PointF sCoord = drakeCircusMapView.viewToSourceCoord(e.getX(), e.getY());
//			                Toast.makeText(getApplicationContext(), "Long press: " + ((int)sCoord.x) + ", " + ((int)sCoord.y), Toast.LENGTH_SHORT).show();
//			            } else {
//			                Toast.makeText(getApplicationContext(), "Long press: Image not ready", Toast.LENGTH_SHORT).show();
//			            }
	        }
	        @Override
	        public boolean onDoubleTap(MotionEvent e) {
//			            if (drakeCircusMapView != null && drakeCircusMapView.isReady()) {
//			                PointF sCoord = drakeCircusMapView.viewToSourceCoord(e.getX(), e.getY());
//			                Toast.makeText(getApplicationContext(), "Double tap: " + ((int)sCoord.x) + ", " + ((int)sCoord.y), Toast.LENGTH_SHORT).show();
//			            } else {
//			                Toast.makeText(getApplicationContext(), "Double tap: Image not ready", Toast.LENGTH_SHORT).show();
//			            }
//			            return true;
	        	return false;
	        }
	    });
		drakeCircusMapView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mapGestureDetector.onTouchEvent(motionEvent);
            }
        });
		
		setupImageMap();
		
		if(getIntent().hasExtra(EXTRA_KEY_UNITNUM)) {
			String unitNum = getIntent().getStringExtra(EXTRA_KEY_UNITNUM);
			
			for(ShopData shopData: mCurrentShops) {
				if(shopData.unitNum.equals(unitNum)) {
					mSelectedShop = shopData;
					
					shouldMoveMap = true;
					break;
				}
			}
		}

		layer_facility = (LinearLayout) findViewById(R.id.layer_facility);
		listFacilityType = (GridView) findViewById(R.id.lst_data);
		mAdapter = new LasyAdapter(this);
		listFacilityType.setAdapter(mAdapter);
		listFacilityType.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				mSelectedShop = null;
				mSelectedFacility = null;
				
				FacilityData selectedFacility = null;
				double distanceMin = -1;
				
				PointF sUserPosition;
				if(myLocation != null && myLocation.latitude >= Constants.MAP_RIGHT_LAT && 
			    		myLocation.latitude <= Constants.MAP_LEFT_LAT && 
			    		myLocation.longitude >= Constants.MAP_LEFT_LONG &&
			    		myLocation.longitude <= Constants.MAP_RIGHT_LONG) {
			        	
			        	double locationUnitX = Math.abs(mMapWidth / (leftTopLocation.latitude - rightBottomLocation.latitude));
			        	double locationUnitY = Math.abs(mMapHeight / (rightBottomLocation.longitude - leftTopLocation.longitude));
			        	
			        	float source_x = (float)Math.abs((myLocation.latitude - leftTopLocation.latitude) * locationUnitX);
			        	float source_y = (float)Math.abs((myLocation.longitude - leftTopLocation.longitude) * locationUnitY);
			        	
			        	sUserPosition = new PointF(source_x, source_y);
				} else {
					sUserPosition = drakeCircusMapView.viewToSourceCoord(drakeCircusMapView.getWidth() / 2, drakeCircusMapView.getHeight() / 2);
				}
				
				for (FacilityData facilityData: mFacilities) {
					if(facilityData.mType.ordinal() == position && facilityData.mLocation == mLocationType) {
						float dx = Math.abs(sUserPosition.x - facilityData.mCentrePt.x * polygonCoordinateRatio);
						float dy = Math.abs(sUserPosition.y - facilityData.mCentrePt.y * polygonCoordinateRatio);
						double distance = Math.sqrt(dx * dx + dy * dy);
								
						if(distanceMin == -1 || distanceMin > distance) {
							distanceMin = distance;
							selectedFacility = facilityData;
						}
					}
				}
				
				if(selectedFacility == null) {
					distanceMin = -1;
					for (FacilityData facilityData: mFacilities) {
						if(facilityData.mType.ordinal() == position && facilityData.mLocation != mLocationType) {
							float dx = Math.abs(sUserPosition.x - facilityData.mCentrePt.x * polygonCoordinateRatio);
							float dy = Math.abs(sUserPosition.y - facilityData.mCentrePt.y * polygonCoordinateRatio);
							double distance = Math.sqrt(dx * dx + dy * dy);
									
							if(distanceMin == -1 || distanceMin > distance) {
								distanceMin = distance;
								selectedFacility = facilityData;
							}
						}
					}
				}

				if (selectedFacility != null) {
					if(mLocationType != selectedFacility.mLocation) {
						mLocationType = selectedFacility.mLocation;
						setupImageMap();
					
						mSelectedFacility = selectedFacility;
						shouldMoveMap = true;
						
					} else {
						mSelectedFacility = selectedFacility;
						
						final PointF center = new PointF(mSelectedFacility.mCentrePt.x * polygonCoordinateRatio, mSelectedFacility.mCentrePt.y * polygonCoordinateRatio);
				        AnimationBuilder animationBuilder = drakeCircusMapView.animateCenter(center);
				        animationBuilder.withDuration(500).start();
				        isMapAnimating = true;
				        
				        new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								if(popupStore != null && popupStore.isShowing())
									popupStore.dismiss();
								
								popupStore = new ShowStorePopup(ActivityCentreMap.this, 0, 0, mSelectedFacility);
								PointF viewCenterCoord = drakeCircusMapView.sourceToViewCoord(center);
								popupStore.show(drakeCircusMapView, viewCenterCoord);
								isMapAnimating = false;
							}
						}, 500);
					}
				}
				
				onClose(layer_facility);
			}
		});

		layer_facility.setVisibility(View.INVISIBLE);
		//txt_selectfacility = (TextView) findViewById(R.id.txt_selectfacility);

		img_message = (ImageView) findViewById(R.id.img_message);

		FontUtils.setTypeface(txt_headertitle, FontUtils.font_HelveticaNeueUltraLight, false);
		FontUtils.setTypeface(txt_search, FontUtils.font_HelveticaNeueThin, false);
		//FontUtils.setTypeface(txt_selectmall, FontUtils.font_HelveticaNeueThin, false);
		//FontUtils.setTypeface(txt_selectfacility, FontUtils.font_HelveticaNeueThin, false);
		
		if (getIntent().getBooleanExtra(Constants.SHOW_FACILITIES, false))
			onFacility(layer_facility);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		initLabelImages();
		
		Location loc = GeolocationUtil.getlocation(this, this);
		if(loc != null) {
			myLocation = new UserLocation(loc.getLatitude(), loc.getLongitude());
			
			if (isUserShowing) {
				showUserLocation();
			}
		}
	}
	
	@Override
	protected void onPause() {
		
		String[] keys = mLowerMallLabels.keySet().toArray(new String[mLowerMallLabels.size()]);
		
    	for(String unitNum: keys) {
    		mLowerMallLabels.get(unitNum).recycle();
    		mLowerMallLabels.remove(unitNum);
    	}
    	
    	keys = mUpperMallLabels.keySet().toArray(new String[mUpperMallLabels.size()]);
    	for(String unitNum: keys) {
    		mUpperMallLabels.get(unitNum).recycle();
    		mUpperMallLabels.remove(unitNum);
    	}
    	
    	keys = m2ndFloorLabels.keySet().toArray(new String[m2ndFloorLabels.size()]);
    	for(String unitNum: keys) {
    		m2ndFloorLabels.get(unitNum).recycle();
    		m2ndFloorLabels.remove(unitNum);
    	}

    	FacilityType[] keysFacility = mBitmapFacilities.keySet().toArray(new FacilityType[mBitmapFacilities.size()]);
    	for(FacilityType type: keysFacility) {
    		mBitmapFacilities.get(type).recycle();
    		mBitmapFacilities.remove(type);
    	}
    	
    	keysFacility = mHighlightBitmapFacilities.keySet().toArray(new FacilityType[mHighlightBitmapFacilities.size()]);
    	for(FacilityType type: keysFacility) {
    		mHighlightBitmapFacilities.get(type).recycle();
    		mHighlightBitmapFacilities.remove(type);
    	}
    	
		LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	locManager.removeUpdates(this);
    	
    	super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		if(drakeCircusMapView != null)
			drakeCircusMapView.recycle();
		
    	super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (layer_facility.getVisibility() == View.VISIBLE) {
			onClose(layer_facility);
		}
		else {
			super.onBackPressed();
			overridePendingTransition(R.anim.in_right, R.anim.out_right);
		}
	}

	public void onBackActivity(View paramView) {
		onBackPressed();
	}

	public void onMore(View paramView) {
		CommonUtil.makeBlurAndStartActivity(this, layer_main);
	}

	public void onFacility(View paramView) {
		if (layer_facility.getVisibility() != View.VISIBLE) {
			layer_facility.setVisibility(View.VISIBLE);
			FadeInAnimator anim = new FadeInAnimator();
			anim.setTarget(layer_facility);
			anim.setDuration(Constants.FADING_TIMEOUT);
			anim.animate();
		}
	}

	public void onClose(View paramView) {
		FadeOutAnimator anim = new FadeOutAnimator();
		anim.setTarget(layer_facility);
		anim.setDuration(Constants.FADING_TIMEOUT);
		anim.animate();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				layer_facility.setVisibility(View.INVISIBLE);
			}
		}, Constants.FADING_TIMEOUT);
	}

	private void setupImageMap() {
			
		ImageSource imageSource;
		if(mLocationType == LocationType.UPPER_MALL) {
			imageSource = ImageSource.asset("level_upper_mall.png");
		} else if(mLocationType == LocationType.FOOD_COURT) {
			imageSource = ImageSource.asset("level_2nd_floor.png");
		} else {
			imageSource = ImageSource.asset("level_lower_mall.png");
		}
		drakeCircusMapView.setImage(imageSource);//, new ImageViewState(0.33f, new PointF(0, 0), 0));
    
		String strText = getResources().getString(R.string.strSelectedFloorGround);
		if (mLocationType == LocationType.G_LOWER_MALL) {
			strText = getResources().getString(R.string.strSelectedFloorGround);
			mCurrentShops = mLowerMallShops;
			
		} else if (mLocationType == LocationType.UPPER_MALL) {
			strText = getResources().getString(R.string.strSelectedFloor1);
			mCurrentShops = mUpperMallShops;
			
		} else if (mLocationType == LocationType.FOOD_COURT) {
			strText = getResources().getString(R.string.strSelectedFloor2);
			mCurrentShops = m2ndFloorShops;
			shouldMoveMap = true;
		}
		//txt_selectmall.setText(strText);
		mSelectedShop = null;
		mSelectedFacility = null;
	}

	public void onClickMall(View paramView) {
/*
		if (layer_facility.getVisibility() == View.VISIBLE)
			onClose(layer_facility);

		SelectMallPopup popup = new SelectMallPopup(this, mLocationType);
		popup.setOnActionItemClickListener(new SelectMallPopup.OnActionItemClickListener() {
			@Override
			public void onItemClick(SelectMallPopup source, View view) {
				int resId = view.getId();
				if (resId == R.id.txt_selectfloorground ||
						resId == R.id.txt_selectfloor1 ||
						resId == R.id.txt_selectfloor2) {
					LocationType nSelected = LocationType.G_LOWER_MALL;
					if (view.getId() == R.id.txt_selectfloor1) {
						nSelected = LocationType.UPPER_MALL;
					} else if (view.getId() == R.id.txt_selectfloor2) {
						nSelected = LocationType.FOOD_COURT;
					}
					if(nSelected != mLocationType) {
						// change image
						mLocationType = nSelected;
						setupImageMap();
						mPrefs.setIntValue(AppPreferences.SELECTED_FLOOR_NUMBER, mLocationType.ordinal());
					}
				}
			}
		});

		popup.show(paramView);
*/
	}

	public void onNavigator(View paramView) {
		
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean gpsEnabled = false;
		try {
			gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (!gpsEnabled) {
			img_message.setVisibility(View.VISIBLE);
			img_message.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
					startActivity(gpsOptionsIntent);
					img_message.setVisibility(View.GONE);
				}
			});
			FadeInAnimator anim = new FadeInAnimator();
			anim.setTarget(img_message);
			anim.setDuration(Constants.SHOWFAVOURITE_TIMEOUT);
			anim.animate();
			
		} else {
        	isUserShowing = !isUserShowing;		
        	
        	showUserLocation();
		}
	}

	public void showUserLocation() {
		if(myLocation != null && myLocation.latitude >= leftTopLocation.latitude && 
    		myLocation.latitude <= rightBottomLocation.latitude && 
    		myLocation.longitude >= leftTopLocation.longitude && 
    		myLocation.longitude <= rightBottomLocation.longitude) {
        	
        	double x1 = a11 * myLocation.latitude + a12 * myLocation.longitude + a13;
        	double y1 = a21 * myLocation.latitude + a22 * myLocation.longitude + a23;
        	double w = a31 * myLocation.latitude + a32 * myLocation.longitude + a33;
        	double x = x1 / w;
        	double y = y1 / w;
        	
        	sourceX = (float)(x);
        	sourceY = (float)(3408 - y);

        	// animate to current location
        	PointF locationCoord = new PointF(sourceX, sourceY);
	        AnimationBuilder animationBuilder = drakeCircusMapView.animateCenter(locationCoord);
	        animationBuilder.withDuration(500).start();
	        isMapAnimating = true;
	        
	        new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					isMapAnimating = false;
				}
			}, 500);

		} else {
			showOutsideCentreInfo();
			isUserShowing = false;
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
    		int selMenu = mPrefs.getIntValue(Constants.SELECTED_MENU_NUMBER);
    		if (selMenu == Constants.SELECT_CASE_CENTREMAP) {
    			// nothing
    		}
    		else if (selMenu == Constants.SELECT_CASE_PARKING) {
    			onFacility(layer_facility);
    		}
    		else {
    			if (ActivityMain.instance != null)
    				ActivityMain.instance.selectMainMenu();
    		}
    	}
    	else if (requestCode == Constants.ACTIVITY_SELECT_SHOP && resultCode == RESULT_OK) {
    		
    		if(data.hasExtra(EXTRA_KEY_LOCATION)) {
    			String location = data.getStringExtra(EXTRA_KEY_LOCATION);
    			LocationType locationType = LocationType.G_LOWER_MALL;
    			if (location.equals("G Lower Mall"))
    				locationType = LocationType.G_LOWER_MALL;
    			else if (location.equals("1 Upper Mall"))
    				locationType = LocationType.UPPER_MALL;
    			else if (location.equals("2 Food Court"))
    				locationType = LocationType.FOOD_COURT;
    			
    			if(mLocationType != locationType) {
    				mLocationType = locationType;
    				setupImageMap();
    			}
    		}
    		
    		if(data.hasExtra(EXTRA_KEY_UNITNUM)) {
    			String unitNum = data.getStringExtra(EXTRA_KEY_UNITNUM);
    			
    			for(ShopData shopData: mCurrentShops) {
    				if(shopData.unitNum.equals(unitNum)) {
    					mSelectedShop = shopData;
    					shouldMoveMap = true;
    					break;
    				}
    			}
    		}
    		
    		drakeCircusMapView.invalidate();
    	}
    }

    public void onSearchShop(View paramView) {
    	Intent intent = new Intent(this, ActivityOurStores.class);
    	intent.putExtra(ActivityOurStores.EXTRA_FROM_WHERE, true);
    	startActivityForResult(intent, Constants.ACTIVITY_SELECT_SHOP);
    	overridePendingTransition(R.anim.fliping_in, R.anim.none);
    }

	private class LasyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private String[] titleFacilities;

		public LasyAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			titleFacilities = context.getResources().getStringArray(R.array.facility_type);
		}

		public int getCount() {
			return FacilityType.values().length;
		}

		public Object getItem(int position) {
			return FacilityType.values()[position];
		}

		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			ImageView img_store;
			TextView txt_title;
		}	

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			convertView = mInflater.inflate(R.layout.row_facility, parent, false);
			holder = new ViewHolder();
			holder.img_store = (ImageView) convertView.findViewById(R.id.img_store);
			holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);

			holder.txt_title.setText(titleFacilities[position]);

			switch (position) {
			case 0:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_atm_purple));
				break;
			case 1:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_photobooth_purple));
				break;
			case 2:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_parking_purple));
				break;
			/*
			case 3:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_recyclingbin_purple));
				break;
			case 4:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_giftcard_urple));
				break;
			*/
			case 3:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_toilet_purple));
				break;
			case 4:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_infopod_purple));
				break;
			case 5:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_lostfound_purple));
				break;
			/*
			case 8:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_kiddiesride_purple));
				break;
			case 6:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_shopmobility_purple));
				break;
			case 6:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_ibeacon_purple));
				break;
				*/
			default:
				holder.img_store.setImageDrawable(getResources().getDrawable(R.drawable.icon_facility_phonecharging_purple));
			}

			FontUtils.setTypeface(holder.txt_title, FontUtils.font_HelveticaNeueThin, false);

			return convertView;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			//myLocation = new UserLocation(location.getLatitude(), location.getLongitude());
				
			myLocation = aryLocation.get(locIndex);
			if(locIndex == aryLocation.size() - 1)
				locIndex = 0;
			else
				locIndex++;
			
			if (isUserShowing)
				showUserLocation();
		}
	}

	void showOutsideCentreInfo() {
		img_message.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_outofcentre));
		img_message.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		img_message.setVisibility(View.VISIBLE);
		FadeInAnimator anim = new FadeInAnimator();
		anim.setTarget(img_message);
		anim.setDuration(Constants.SHOWFAVOURITE_TIMEOUT);
		anim.animate();
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				FadeOutAnimator anim = new FadeOutAnimator();
				anim.addAnimatorListener(new AnimatorListener() {
					
					@Override
					public void onAnimationStart(Animator arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animator arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animator arg0) {
						img_message.setVisibility(View.GONE);
					}
					
					@Override
					public void onAnimationCancel(Animator arg0) {
						img_message.setVisibility(View.GONE);
					}
				});
				anim.setTarget(img_message);
				anim.setDuration(Constants.SHOWFAVOURITE_TIMEOUT);
				anim.animate();
			}
		}, Constants.SHOWFAVOURITE_TIMEOUT);
	}
	
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	// for the new map
	
	private void initPolygons() {
		/*
		 * [INFO] The below polygon corrdinate values are based on 700 * 499
		 */
		
		////////////////////////////////////////////
		// G Lower Mall (69 stores)
		////////////////////////////////////////////
		// store1 M&S
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1195, 2890))
				.addVertex(new Point(1521, 2890))
				.addVertex(new Point(1521, 3150))
				.addVertex(new Point(1160, 3150))
				.addVertex(new Point(1160, 3074))
				.addVertex(new Point(1125, 3074))
				.addVertex(new Point(1125, 2923))
				.addVertex(new Point(1195, 2923))
				.build(), "C", new Point(1324, 2998)));
		// store 2 wagamama
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(919, 2785))
				.addVertex(new Point(1138, 2785))
				.addVertex(new Point(1138, 2827))
				.addVertex(new Point(1101, 2827))
				.addVertex(new Point(1101, 2853))
				.addVertex(new Point(1030, 2853))
				.addVertex(new Point(1030, 2827))
				.addVertex(new Point(919, 2827))
				.build(), "A1", new Point(1028, 2806)));
		// store 3 MONSOON
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(919, 2712))
				.addVertex(new Point(1138, 2712))
				.addVertex(new Point(1138, 2771))
				.addVertex(new Point(919, 2771))
				.build(), "A2", new Point(1028, 2741)));
		// store 4 schuh
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(919, 2652))
				.addVertex(new Point(1138, 2652))
				.addVertex(new Point(1138, 2698))
				.addVertex(new Point(919, 2698))
				.build(), "A3", new Point(1028, 2675)));
		// store 5 RIVER ISLAND
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(919, 2551))
				.addVertex(new Point(1138, 2551))
				.addVertex(new Point(1138, 2639))
				.addVertex(new Point(919, 2639))
				.build(), "A4", new Point(1028, 2595)));	
		// store 6 H&M
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(919, 2409))
				.addVertex(new Point(1138, 2409))
				.addVertex(new Point(1138, 2538))
				.addVertex(new Point(919, 2538))
				.build(), "A5", new Point(1028, 2473)));
				
		// store 7 TOPSHOP TOPMAN 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(856, 2298))
				.addVertex(new Point(1076, 2298))
				.addVertex(new Point(1076, 2396))
				.addVertex(new Point(856, 2396))
				.build(), "A6", new Point(966, 2347)));
		
		// store 8 XPRESS BEAUTY
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(852, 2311))
				.addVertex(new Point(852, 2372))
				.addVertex(new Point(843, 2372))
				.addVertex(new Point(823, 2396))
				.addVertex(new Point(773, 2357))
				.addVertex(new Point(811, 2311))
				.build(), "G4", new Point(820, 2347), -90));
		// store 9 LITTLE SOLES
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(766, 2366))
				.addVertex(new Point(815, 2406))
				.addVertex(new Point(799, 2425))
				.addVertex(new Point(749, 2386))
				.build(), "G3", new Point(782, 2396), -140));
		// store 10 SWEETS & TREATS
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(743, 2395))
				.addVertex(new Point(792, 2434))
				.addVertex(new Point(776, 2453))
				.addVertex(new Point(726, 2414))
				.build(), "G2", new Point(759, 2424), -140));
		// store 11 SOLENT CYCLES
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(719, 2423))
				.addVertex(new Point(769, 2463))
				.addVertex(new Point(729, 2511))
				.addVertex(new Point(679, 2472))
				.build(), "G1", new Point(724, 2467), -140));
		// store 12 HAIR OTT
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(780, 2114))
				.addVertex(new Point(725, 2180))
				.addVertex(new Point(700, 2161))
				.addVertex(new Point(755, 2094))
				.build(), "H3", new Point(740, 2137), 50));	// store 13 GREENGROCER
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(788, 2120))
				.addVertex(new Point(808, 2137))
				.addVertex(new Point(754, 2203))
				.addVertex(new Point(733, 2187))
				.build(), "H2", new Point(771, 2162), 50));
		// store 14 BAKER
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(817, 2143))
				.addVertex(new Point(837, 2160))
				.addVertex(new Point(783, 2226))
				.addVertex(new Point(761, 2210))
				.build(), "H1", new Point(800, 2185), 50));
		// store 15 blank
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(903, 2166))
				.addVertex(new Point(903, 2219))
				.addVertex(new Point(816, 2219))
				.addVertex(new Point(807, 2213))
				.addVertex(new Point(846, 2166))
				.build(), "WC", new Point(875, 2195)));
		// store  16 next
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(907, 2063))
				.addVertex(new Point(1138, 2063))
				.addVertex(new Point(1138, 2123))
				.addVertex(new Point(1076, 2123))
				.addVertex(new Point(1076, 2231))
				.addVertex(new Point(907, 2231))
				.build(), "B1", new Point(991, 2146)));
		// store 17 TIGER
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(907, 1989))
				.addVertex(new Point(1138, 1989))
				.addVertex(new Point(1138, 2048))
				.addVertex(new Point(907, 2048))
				.build(), "B2", new Point(1022, 2018)));
		// store 18 BANK
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(907, 1918))
				.addVertex(new Point(1138, 1918))
				.addVertex(new Point(1138, 1975))
				.addVertex(new Point(907, 1975))
				.build(), "B3", new Point(1022, 1947)));
		// store 19 Blacks
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(907, 1849))
				.addVertex(new Point(1138, 1849))
				.addVertex(new Point(1138, 1903))
				.addVertex(new Point(907, 1903))
				.build(), "B4", new Point(1022, 1875)));
		// store 20 Clanks
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(907, 1775))
				.addVertex(new Point(1138, 1775))
				.addVertex(new Point(1138, 1834))
				.addVertex(new Point(907, 1834))
				.build(), "B5", new Point(1022, 1803)));
		// store 21 SPORTS DIRECT
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(907, 1684))
				.addVertex(new Point(1138, 1684))
				.addVertex(new Point(1138, 1762))
				.addVertex(new Point(907, 1762))
				.build(), "B6", new Point(1022, 1722)));
		// store 22 mamas
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(907, 1613))
				.addVertex(new Point(1102, 1613))
				.addVertex(new Point(1102, 1670))
				.addVertex(new Point(907, 1670))
				.build(), "B7", new Point(1004, 1641)));
		// store 23 Entertainer
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(907, 1542))
				.addVertex(new Point(1102, 1542))
				.addVertex(new Point(1102, 1598))
				.addVertex(new Point(907, 1598))
				.build(), "B8", new Point(1004, 1570)));
		// store 24 Rock Up
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(907, 1455))
				.addVertex(new Point(1102, 1455))
				.addVertex(new Point(1102, 1527))
				.addVertex(new Point(907, 1527))
				.build(), "B9", new Point(1004, 1491)));
		// store 25 FIVE GUYS
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(959, 1329))
				.addVertex(new Point(1030, 1370))
				.addVertex(new Point(1030, 1416))
				.addVertex(new Point(936, 1416))
				.addVertex(new Point(920, 1402))
				.addVertex(new Point(848, 1491))
				.addVertex(new Point(835, 1480))
				.build(), "F1", new Point(976, 1389)));
		// store 26 Nando's
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(909, 1296))
				.addVertex(new Point(951, 1324))
				.addVertex(new Point(828, 1475))
				.addVertex(new Point(787, 1444))
				.build(), "F2", new Point(868, 1386), 50));
		// store 27 PIZZ
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(866, 1256))
				.addVertex(new Point(906, 1283))
				.addVertex(new Point(780, 1438))
				.addVertex(new Point(757, 1420))
				.addVertex(new Point(807, 1360))
				.addVertex(new Point(807, 1349))
				.addVertex(new Point(797, 1340))
				.build(), "F3", new Point(850, 1313), 50));
		// store 28 COAST
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(806, 1210))
				.addVertex(new Point(856, 1250))
				.addVertex(new Point(786, 1335))
				.addVertex(new Point(786, 1346))
				.addVertex(new Point(798, 1356))
				.addVertex(new Point(750, 1414))
				.addVertex(new Point(681, 1361))
				.build(), "F4", new Point(769, 1305), 50));
		// store 29 dimt
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(768, 1175))
				.addVertex(new Point(798, 1203))
				.addVertex(new Point(675, 1355))
				.addVertex(new Point(634, 1323))
				.addVertex(new Point(636, 1320))
				.addVertex(new Point(636, 1312))
				.addVertex(new Point(708, 1225))
				.addVertex(new Point(720, 1234))
				.build(), "F5", new Point(694, 1290), 50));
		// store 30 Cinema
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(726, 1137))
				.addVertex(new Point(760, 1169))
				.addVertex(new Point(719, 1220))
				.addVertex(new Point(707, 1211))
				.addVertex(new Point(629, 1306))
				.addVertex(new Point(628, 1314))
				.addVertex(new Point(626, 1317))
				.addVertex(new Point(620, 1311))
				.addVertex(new Point(659, 1263))
				.addVertex(new Point(659, 1253))
				.addVertex(new Point(657, 1251))
				.addVertex(new Point(670, 1235))
				.addVertex(new Point(670, 1225))
				.addVertex(new Point(661, 1217))
				.build(), "F8", new Point(721, 1179), 50));
		// store 31 Wild wood
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(678, 1089))
				.addVertex(new Point(719, 1129))
				.addVertex(new Point(650, 1213))
				.addVertex(new Point(650, 1224))
				.addVertex(new Point(660, 1232))
				.addVertex(new Point(649, 1245))
				.addVertex(new Point(589, 1197))
				.build(), "F6", new Point(654, 1162), 50));
		// store 32 Toilet
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(618, 1013))
				.addVertex(new Point(655, 1046))
				.addVertex(new Point(678, 1073))
				.addVertex(new Point(580, 1194))
				.addVertex(new Point(580, 1205))
				.addVertex(new Point(648, 1260))
				.addVertex(new Point(611, 1305))
				.addVertex(new Point(466, 1190))
				.addVertex(new Point(466, 1171))
				.addVertex(new Point(483, 1150))
				.addVertex(new Point(497, 1161))
				.addVertex(new Point(508, 1148))
				.addVertex(new Point(517, 1156))
				.addVertex(new Point(548, 1118))
				.addVertex(new Point(548, 1108))
				.addVertex(new Point(544, 1104))
				.build(), "TO", new Point(601, 1106), 50));
		// store 33 TESCO 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1500, 1411))
				.addVertex(new Point(1594, 1411))
				.addVertex(new Point(1594, 1402))
				.addVertex(new Point(1598, 1398))
				.addVertex(new Point(1638, 1398))
				.addVertex(new Point(1643, 1403))
				.addVertex(new Point(1643, 1411))
				.addVertex(new Point(1702, 1411))
				.addVertex(new Point(1702, 1436))
				.addVertex(new Point(1890, 1436))
				.addVertex(new Point(1890, 1661))
				.addVertex(new Point(1650, 1661))
				.addVertex(new Point(1650, 1789))
				.addVertex(new Point(1500, 1789))
				.build(), "T", new Point(1695, 1549)));
		// store 34 Card Factory
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1418, 1456))
				.addVertex(new Point(1453, 1456))
				.addVertex(new Point(1453, 1546))
				.addVertex(new Point(1418, 1546))
				.build(), "E17", new Point(1435, 1501), 90));
		// store 35 Ladbrokes
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1383, 1456))
				.addVertex(new Point(1415, 1456))
				.addVertex(new Point(1415, 1546))
				.addVertex(new Point(1383, 1546))
				.build(), "E16", new Point(1398, 1501), 90));
		// store 36 SUBWAY
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1344, 1456))
				.addVertex(new Point(1379, 1456))
				.addVertex(new Point(1379, 1546))
				.addVertex(new Point(1344, 1546))
				.build(), "E15", new Point(1361, 1501), 90));
		// store 37 Walker & Waterer
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1309, 1456))
				.addVertex(new Point(1341, 1456))
				.addVertex(new Point(1341, 1546))
				.addVertex(new Point(1309, 1546))
				.build(), "E14", new Point(1324, 1501), 90));
		// store 38 Tui
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1272, 1456))
				.addVertex(new Point(1306, 1456))
				.addVertex(new Point(1306, 1546))
				.addVertex(new Point(1272, 1546))
				.build(), "E13", new Point(1288, 1501), 90));
		// store 39 CAFFE NERO
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1235, 1456))
				.addVertex(new Point(1268, 1456))
				.addVertex(new Point(1268, 1559))
				.addVertex(new Point(1282, 1559))
				.addVertex(new Point(1282, 1565))
				.addVertex(new Point(1235, 1565))
				.build(), "E12", new Point(1252, 1511), 90));
		// store 40 MONTAGU'S
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1151, 1484))
				.addVertex(new Point(1186, 1484))
				.addVertex(new Point(1186, 1545))
				.addVertex(new Point(1151, 1545))
				.build(), "K2", new Point(1168, 1515), 90));
		// store 41 TRESPASS
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1235, 1579))
				.addVertex(new Point(1453, 1579))
				.addVertex(new Point(1453, 1636))
				.addVertex(new Point(1235, 1636))
				.build(), "E11", new Point(1343, 1607)));
		// store 42 Books
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1236, 1651))
				.addVertex(new Point(1453, 1651))
				.addVertex(new Point(1453, 1779))
				.addVertex(new Point(1196, 1779))
				.addVertex(new Point(1196, 1682))
				.addVertex(new Point(1236, 1682))
				.build(), "E10", new Point(1344, 1715)));
		// store 43 Ernest Jones
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1196, 1793))
				.addVertex(new Point(1307, 1793))
				.addVertex(new Point(1307, 1831))
				.addVertex(new Point(1196, 1831))
				.build(), "E9B", new Point(1252, 1812)));
		// store 44 HOLLAND
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1196, 1846))
				.addVertex(new Point(1307, 1846))
				.addVertex(new Point(1307, 1887))
				.addVertex(new Point(1196, 1887))
				.build(), "E9A", new Point(1252, 1867)));
		// store 45 PANDORA
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1196, 1903))
				.addVertex(new Point(1307, 1903))
				.addVertex(new Point(1307, 1923))
				.addVertex(new Point(1196, 1923))
				.build(), "E8B", new Point(1252, 1913)));
		// store 46 smiggle
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1196, 1939))
				.addVertex(new Point(1307, 1939))
				.addVertex(new Point(1307, 1960))
				.addVertex(new Point(1196, 1960))
				.build(), "E8A", new Point(1252, 1949)));
		// store 47 claire's
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1196, 1975))
				.addVertex(new Point(1307, 1975))
				.addVertex(new Point(1307, 1995))
				.addVertex(new Point(1196, 1995))
				.build(), "E7B", new Point(1252, 1985)));
		// store 48 Clintones
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1196, 2009))
				.addVertex(new Point(1307, 2009))
				.addVertex(new Point(1307, 2031))
				.addVertex(new Point(1196, 2031))
				.build(), "E7A", new Point(1252, 2020)));
		// store 49 MOSS
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1196, 2046))
				.addVertex(new Point(1307, 2046))
				.addVertex(new Point(1307, 2078))
				.addVertex(new Point(1290, 2078))
				.addVertex(new Point(1290, 2103))
				.addVertex(new Point(1196, 2103))
				.build(), "E6", new Point(1242, 2074)));
		// store 50 FATFACE 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1196, 2117))
				.addVertex(new Point(1293, 2117))
				.addVertex(new Point(1293, 2091))
				.addVertex(new Point(1305, 2091))
				.addVertex(new Point(1305, 2212))
				.addVertex(new Point(1196, 2212))
				.build(), "E5", new Point(1251, 2164)));
		// store 51 WHSmith 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1310, 2113))
				.addVertex(new Point(1378, 2113))
				.addVertex(new Point(1378, 2212))
				.addVertex(new Point(1310, 2212))
				.build(), "E4", new Point(1344, 2162), 90));
		// store 52 STARBUCKS 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1381, 2115))
				.addVertex(new Point(1396, 2115))
				.addVertex(new Point(1396, 2101))
				.addVertex(new Point(1407, 2101))
				.addVertex(new Point(1407, 2117))
				.addVertex(new Point(1491, 2117))
				.addVertex(new Point(1491, 2180))
				.addVertex(new Point(1381, 2180))
				.build(), "E3", new Point(1436, 2149)));
		// store 53 Harvester
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1396, 1966))
				.addVertex(new Point(1491, 1966))
				.addVertex(new Point(1491, 2103))
				.addVertex(new Point(1410, 2103))
				.addVertex(new Point(1410, 2087))
				.addVertex(new Point(1396, 2087))
				.build(), "E1/2", new Point(1443, 2026)));
		// store 54 YO sushi 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1407, 2264))
				.addVertex(new Point(1409, 2259))
				.addVertex(new Point(1411, 2256))
				.addVertex(new Point(1414, 2252))
				.addVertex(new Point(1420, 2247))
				.addVertex(new Point(1429, 2242))
				.addVertex(new Point(1439, 2238))
				.addVertex(new Point(1453, 2235))
				.addVertex(new Point(1467, 2234))
				.addVertex(new Point(1479, 2235))
				.addVertex(new Point(1491, 2237))
				.addVertex(new Point(1503, 2241))
				.addVertex(new Point(1517, 2249))
				.addVertex(new Point(1523, 2256))
				.addVertex(new Point(1527, 2263))
				.addVertex(new Point(1523, 2270))
				.addVertex(new Point(1520, 2274))
				.addVertex(new Point(1517, 2276))
				.addVertex(new Point(1510, 2282))
				.addVertex(new Point(1496, 2288))
				.addVertex(new Point(1485, 2290))
				.addVertex(new Point(1467, 2292))
				.addVertex(new Point(1446, 2290))
				.addVertex(new Point(1432, 2286))
				.addVertex(new Point(1426, 2283))
				.addVertex(new Point(1419, 2279))
				.addVertex(new Point(1411, 2271))
				.build(), "K1", new Point(1467, 2263)));
		// store 55 CHiMiCHANGA  
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1393, 2566))
				.addVertex(new Point(1488, 2566))
				.addVertex(new Point(1488, 2643))
				.addVertex(new Point(1393, 2643))
				.build(), "D13", new Point(1440, 2604)));
		// store56 PREZZO 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1393, 2476))
				.addVertex(new Point(1488, 2476))
				.addVertex(new Point(1488, 2553))
				.addVertex(new Point(1393, 2553))
				.build(), "D12", new Point(1440, 2514)));
		// store 57 Frankie 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1381, 2347))
				.addVertex(new Point(1488, 2347))
				.addVertex(new Point(1488, 2463))
				.addVertex(new Point(1393, 2463))
				.addVertex(new Point(1393, 2452))
				.addVertex(new Point(1381, 2452))
				.build(), "D10/11", new Point(1434, 2399)));		
		// store 58 Jonles
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1310, 2317))
				.addVertex(new Point(1378, 2317))
				.addVertex(new Point(1378, 2412))
				.addVertex(new Point(1310, 2412))
				.build(), "D9", new Point(1344, 2364), 90));
		// store 59 BEAVERBROOKS 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1195, 2317))
				.addVertex(new Point(1305, 2317))
				.addVertex(new Point(1305, 2412))
				.addVertex(new Point(1293, 2412))
				.addVertex(new Point(1293, 2398))
				.addVertex(new Point(1288, 2398))
				.addVertex(new Point(1288, 2358))
				.addVertex(new Point(1195, 2358))
				.build(), "D8B", new Point(1250, 2337)));
		// store 60 THE BODY SHOP 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1195, 2373))
				.addVertex(new Point(1284, 2373))
				.addVertex(new Point(1284, 2412))
				.addVertex(new Point(1195, 2412))
				.build(), "D8A", new Point(1238, 2393)));
		// store 61 vision express 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1195, 2426))
				.addVertex(new Point(1271, 2426))
				.addVertex(new Point(1271, 2484))
				.addVertex(new Point(1195, 2484))
				.build(), "D7", new Point(1232, 2454)));
		// store 62 Phase 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1195, 2497))
				.addVertex(new Point(1271, 2497))
				.addVertex(new Point(1271, 2555))
				.addVertex(new Point(1195, 2555))
				.build(), "D6", new Point(1232, 2526)));
		// store 63 JONES 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1195, 2569))
				.addVertex(new Point(1271, 2569))
				.addVertex(new Point(1271, 2627))
				.addVertex(new Point(1195, 2627))
				.build(), "D5", new Point(1232, 2598)));
		// store 64 Pirper 
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1195, 2641))
				.addVertex(new Point(1271, 2641))
				.addVertex(new Point(1271, 2686))
				.addVertex(new Point(1257, 2686))
				.addVertex(new Point(1257, 2700))
				.addVertex(new Point(1195, 2700))
				.build(), "D4", new Point(1233, 2663)));
		// store 65 Carphone
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1195, 2713))
				.addVertex(new Point(1260, 2713))
				.addVertex(new Point(1260, 2699))
				.addVertex(new Point(1271, 2699))
				.addVertex(new Point(1271, 2777))
				.addVertex(new Point(1195, 2777))
				.build(), "D3B", new Point(1235, 2745)));
		// store 66 COSTA
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1195, 2790))
				.addVertex(new Point(1274, 2790))
				.addVertex(new Point(1274, 2730))
				.addVertex(new Point(1305, 2730))
				.addVertex(new Point(1305, 2827))
				.addVertex(new Point(1195, 2827))
				.build(), "D3A", new Point(1289, 2778), 90));
//		// store 67 GLOW & CO
//		mLowerMallShops.add(new ShopData(Polygon.Builder()
//				.addVertex(new Point(1308, 2730))
//				.addVertex(new Point(1359, 2730))
//				.addVertex(new Point(1359, 2827))
//				.addVertex(new Point(1308, 2827))
//				.build(), "D2B", new Point(1334, 2778), 90));
		// store 68 FUSSY NATION
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1308, 2730))
				.addVertex(new Point(1419, 2730))
				.addVertex(new Point(1419, 2827))
				.addVertex(new Point(1308, 2827))
				.build(), "D2A", new Point(1363, 2778), 0));
		// store 69 Dearis
		mLowerMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(1422, 2730))
				.addVertex(new Point(1488, 2730))
				.addVertex(new Point(1488, 2827))
				.addVertex(new Point(1422, 2827))
				.build(), "D1", new Point(1455, 2778)));
		
		////////////////////////////////////////////
		// 1 Upper Mall(29 stores)
		////////////////////////////////////////////
		// store 1
		mUpperMallShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(706, 2298))
				.addVertex(new Point(706, 3217))
				.addVertex(new Point(1982, 3217))
				.addVertex(new Point(1982, 3090))
				.addVertex(new Point(2176, 3090))
				.addVertex(new Point(2176, 2768))
				.addVertex(new Point(1948, 2298))
				.build(), "1", new Point(1427, 2728)));
		
		//////////////////////////////////////////////////////
		// 2 Food Court
		//////////////////////////////////////////////////////
		// store 1
		m2ndFloorShops.add(new ShopData(Polygon.Builder()
				.addVertex(new Point(4469, 2058))
				.addVertex(new Point(4469, 2193))
				.addVertex(new Point(5121, 2500))
				.addVertex(new Point(5135, 2466))
				.addVertex(new Point(5141, 2422))
				.addVertex(new Point(5142, 2380))
				.addVertex(new Point(5131, 2324))
				.addVertex(new Point(5113, 2271))
				.addVertex(new Point(5078, 2202))
				.addVertex(new Point(5020, 2143))
				.addVertex(new Point(4964, 2100))
				.addVertex(new Point(4900, 2070))
				.addVertex(new Point(4845, 2051))
				.addVertex(new Point(4782, 2044))
				.addVertex(new Point(4743, 2042))
				.addVertex(new Point(4678, 2052))
				.addVertex(new Point(4640, 2063))
				.addVertex(new Point(4603, 2077))
				.addVertex(new Point(4533, 2025))
				.addVertex(new Point(4494, 2058))
				.build(), "58", new Point(4877, 2236), -24));		
		
		/*
		 * init facilities
		 */
		mFacilities.clear();
		mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(855, 2220), FacilityType.TOILETS_BABY_CHANGE));
		mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1220, 1660), FacilityType.INFORMATION_POD));
		mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(890, 2220), FacilityType.PHOTO_BOOTHS));
		mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1865, 1440), FacilityType.PHOTO_BOOTHS));
		mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(820, 2220), FacilityType.ATM));
		mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1500, 1450), FacilityType.ATM));
		mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(870, 2185), FacilityType.LOST_FOUND));

		mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(556, 832), FacilityType.CAR_PARKING));
		mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(650, 2278), FacilityType.CAR_PARKING));
	    mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1172, 1213), FacilityType.CAR_PARKING));
	    mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1636, 1181), FacilityType.CAR_PARKING));
	    mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1770, 2335), FacilityType.CAR_PARKING));
	    //mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(906, 3258), FacilityType.CAR_PARKING));
	    //mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1544, 3338), FacilityType.CAR_PARKING));
		
		//mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1074, 1424), FacilityType.IBEACON_LOCATION));
		//mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1347, 2065), FacilityType.IBEACON_LOCATION));		
		//mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1034, 2265), FacilityType.IBEACON_LOCATION));		
		//mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1322, 2472), FacilityType.IBEACON_LOCATION));		
		//mFacilities.add(new FacilityData(LocationType.G_LOWER_MALL, new Point(1470, 2863), FacilityType.IBEACON_LOCATION));		
		initLabelImages();
	}
	
	private void initLabelImages() {
		
		for(FacilityType type: FacilityType.values()) {
			int iconResId = R.drawable.icon_facility_atm_purple;
			int iconHighlightResId = R.drawable.icon_facility_atm_purple;
			switch(type) {
			case ATM:
				iconResId = R.drawable.icon_facility_atm_purple;
				iconHighlightResId = R.drawable.icon_facility_atm_white_hl;
				break;
			case PHOTO_BOOTHS:
				iconResId = R.drawable.icon_facility_photobooth_purple;
				iconHighlightResId = R.drawable.icon_facility_photobooth_white_hl;
				break;
			case CAR_PARKING:
				iconResId = R.drawable.icon_facility_parking_purple;
				iconHighlightResId = R.drawable.icon_facility_parking_white_hl;
				break;
			/*
			case RECYCLING_BINS:
				iconResId = R.drawable.icon_facility_recyclingbin_purple;
				iconHighlightResId = R.drawable.icon_facility_recyclingbin_white_hl;
				break;
			case GIFT_CARD:
				iconResId = R.drawable.icon_facility_giftcard_urple;
				iconHighlightResId = R.drawable.icon_facility_giftcard_white_hl;
				break;
			*/
			case TOILETS_BABY_CHANGE:
				iconResId = R.drawable.icon_facility_toilet_purple;
				iconHighlightResId = R.drawable.icon_facility_toilet_white_hl;
				break;
			case INFORMATION_POD:
				iconResId = R.drawable.icon_facility_infopod_purple;
				iconHighlightResId = R.drawable.icon_facility_infopod_white_hl;
				break;
			case LOST_FOUND:
				iconResId = R.drawable.icon_facility_lostfound_purple;
				iconHighlightResId = R.drawable.icon_facility_lostfound_white_hl;
				break;
			/*
			case KIDDIES_RIDE:
				iconResId = R.drawable.icon_facility_kiddiesride_purple;
				iconHighlightResId = R.drawable.icon_facility_kiddiesride_white_hl;
				break;
			case SHOP_MOBILITY:
				iconResId = R.drawable.icon_facility_shopmobility_purple;
				iconHighlightResId = R.drawable.icon_facility_shopmobility_white_hl;
				break;
			
			case IBEACON_LOCATION:
				iconResId = R.drawable.icon_facility_ibeacon_purple;
				iconHighlightResId = R.drawable.icon_facility_ibeacon_white_hl;
				break;
				*/
			default:
				iconResId = R.drawable.icon_facility_phonecharging_purple;
				iconHighlightResId = R.drawable.icon_facility_phonecharging_white_hl;
			}
			
			Bitmap bitmapIcon = BitmapFactory.decodeResource(getResources(), iconResId);
			Bitmap bitmapHighlightIcon = BitmapFactory.decodeResource(getResources(), iconHighlightResId);
			
			mBitmapFacilities.put(type, bitmapIcon);
			mHighlightBitmapFacilities.put(type, bitmapHighlightIcon);
		}
		
		
		// load bitmap for label
		int index = 1;
		for(ShopData lowerShop: mLowerMallShops) {
			StoreNameDAO storeData = DrakeCircusApplication.getInstance().dbHelper.getStoreData(lowerShop.unitNum, "G Lower Mall");
			
			if(storeData != null) {
				try {
					lowerShop.storeId = storeData.id;
					lowerShop.storeName = storeData.name;
					lowerShop.hasOffer = storeData.hasOffer;
					lowerShop.favorite = storeData.favourite;
					
					URL urlLabel = new URL(storeData.label);
		        	File fileLabel = new File(getFilesDir(), urlLabel.getFile());
					Bitmap bitmap = ImageUtil.RotateBitmap(BitmapFactory.decodeFile(fileLabel.getAbsolutePath()), 2, -lowerShop.angle);
//		        	AssetManager assetManager = getAssets();
//		        	InputStream istr = assetManager.open("logo/" + index + ".png");
//		        	Bitmap bitmap = ImageUtil.RotateBitmap(BitmapFactory.decodeStream(istr), 2, -lowerShop.angle);
					mLowerMallLabels.put(lowerShop.unitNum, bitmap);
					
				} catch(Exception e) {
					e.printStackTrace();
				}
				index++;
			}
		}
		/*
		for(ShopData upperShop: mUpperMallShops) {
			StoreNameDAO storeData = DrakeCircusApplication.getInstance().dbHelper.getStoreData(upperShop.unitNum, "1 Upper Mall");
			
			if(storeData != null) {
				try {
					upperShop.storeId = storeData.id;
					upperShop.storeName = storeData.name;
					upperShop.hasOffer = storeData.hasOffer;
					upperShop.favorite = storeData.favourite;
					
					URL urlLabel = new URL(storeData.label);
		        	File fileLabel = new File(getFilesDir(), urlLabel.getFile());
					Bitmap bitmap = ImageUtil.RotateBitmap(BitmapFactory.decodeFile(fileLabel.getAbsolutePath()), 2, -upperShop.angle);
					mUpperMallLabels.put(upperShop.unitNum, bitmap);
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		for(ShopData foodCourtShop: m2ndFloorShops) {
			StoreNameDAO storeData = DrakeCircusApplication.getInstance().dbHelper.getStoreData(foodCourtShop.unitNum, "2 Food Court");
			
			if(storeData != null) {
				try {
					foodCourtShop.storeId = storeData.id;
					foodCourtShop.storeName = storeData.name;
					foodCourtShop.hasOffer = storeData.hasOffer;
					foodCourtShop.favorite = storeData.favourite;
					
					URL urlLabel = new URL(storeData.label);
		        	File fileLabel = new File(getFilesDir(), urlLabel.getFile());
					Bitmap bitmap = ImageUtil.RotateBitmap(BitmapFactory.decodeFile(fileLabel.getAbsolutePath()), 2, -foodCourtShop.angle);
					m2ndFloorLabels.put(foodCourtShop.unitNum, bitmap);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		*/
	}
	
	@Override
	public void onExtraDraw(Canvas canvas) {
		
		if(!drakeCircusMapView.isReady())
			return;

		if(shouldMoveMap) {
			if(mSelectedShop != null) {
				final PointF center = new PointF(mSelectedShop.mTextCentrePt.x * polygonCoordinateRatio, mSelectedShop.mTextCentrePt.y * polygonCoordinateRatio);
		        AnimationBuilder animationBuilder = drakeCircusMapView.animateCenter(center);
		        animationBuilder.withDuration(500).start();
		        isMapAnimating = true;
		        
		        new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if(popupStore != null && popupStore.isShowing())
							popupStore.dismiss();
						
						popupStore = new ShowStorePopup(ActivityCentreMap.this, mSelectedShop);
						popupStore.setOnActionItemClickListener(new OnActionItemClickListener() {
							@Override
							public void onItemClick(ShowStorePopup source, View view) {
								if (view.getId() == R.id.view_goto_store) {
									ShopData shopData = (ShopData)view.getTag();
									if(shopData.storeId != 0) {
										Intent intent = new Intent(ActivityCentreMap.this, ActivityShopDetail.class);
										intent.putExtra(Constants.SELECTED_STORE_ID, shopData.storeId);
										startActivity(intent);
										ActivityCentreMap.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
									}
								}
							}
						});
						PointF viewCenterCoord = drakeCircusMapView.sourceToViewCoord(center);
						popupStore.show(drakeCircusMapView, viewCenterCoord);
						isMapAnimating = false;
					}
				}, 500);
			} else if(mSelectedFacility != null) {
				final PointF center = new PointF(mSelectedFacility.mCentrePt.x * polygonCoordinateRatio, mSelectedFacility.mCentrePt.y * polygonCoordinateRatio);
		        AnimationBuilder animationBuilder = drakeCircusMapView.animateCenter(center);
		        animationBuilder.withDuration(500).start();
		        isMapAnimating = true;
		        
		        new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if(popupStore != null && popupStore.isShowing())
							popupStore.dismiss();
						
						popupStore = new ShowStorePopup(ActivityCentreMap.this, 0, 0, mSelectedFacility);
						PointF viewCenterCoord = drakeCircusMapView.sourceToViewCoord(center);
						popupStore.show(drakeCircusMapView, viewCenterCoord);
						isMapAnimating = false;
					}
				}, 500);
			} else if(mLocationType == LocationType.FOOD_COURT){
				PointF center = new PointF(5230 / polygonCoordinateRatio, 3036 / polygonCoordinateRatio);
		        AnimationBuilder animationBuilder = drakeCircusMapView.animateCenter(center);
		        animationBuilder.withDuration(500).start();
		        isMapAnimating = true;
		        
		        new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						isMapAnimating = false;
					}
				}, 500);
			}
	        
			shouldMoveMap = false;
	        return;
		}
		
//		PointF sCenter = new PointF(drakeCircusMapView.getSWidth()/2, drakeCircusMapView.getSHeight()/2);
//        PointF vCenter = drakeCircusMapView.sourceToViewCoord(sCenter);
//        float radius = (drakeCircusMapView.getScale() * drakeCircusMapView.getSWidth()) * 0.25f;
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setStyle(Style.STROKE);
//        paint.setStrokeCap(Cap.ROUND);
//        paint.setStrokeWidth(2);
//        paint.setColor(Color.BLACK);
//        canvas.drawCircle(vCenter.x, vCenter.y, radius, paint);
//        paint.setStrokeWidth(1);
//        paint.setColor(Color.argb(255, 51, 181, 229));
//        canvas.drawCircle(vCenter.x, vCenter.y, radius, paint);
        
		Paint paint = new Paint();
		
        float scale = drakeCircusMapView.getScale() * polygonCoordinateRatio;
        PointF transPoint = drakeCircusMapView.sourceToViewCoord(0.0f, 0.0f);
        // draw selected polygon
        if(mSelectedShop != null) {
        	paint = new Paint();
    		paint.setStrokeWidth(2);
    		paint.setStyle(Paint.Style.FILL);
    		paint.setColor(getResources().getColor(R.color.color_clickedroom));
    		
        	Path path = new Path();
			path.setFillType(Path.FillType.EVEN_ODD);
			path.moveTo(mSelectedShop.mPolygon.getSides().get(0).getStart().x * scale + transPoint.x, 
					mSelectedShop.mPolygon.getSides().get(0).getStart().y * scale + transPoint.y);
			for (Line line : mSelectedShop.mPolygon.getSides()) {
				path.lineTo(line.getEnd().x * scale + transPoint.x, line.getEnd().y * scale + transPoint.y);
			}
			path.close();

			// draw polygon
			canvas.drawPath(path, paint);
	        
        }
        
        // draw label for shop
        if(mLocationType == LocationType.UPPER_MALL) {
    		for(ShopData upperShop: mUpperMallShops) {
    			Bitmap bitmapLabel = mUpperMallLabels.get(upperShop.unitNum);
				
				if(bitmapLabel != null) {
					PointF pointLeftTop = drakeCircusMapView.viewToSourceCoord(
							upperShop.mTextCentrePt.x * scale + transPoint.x - bitmapLabel.getWidth() / 2, 
							upperShop.mTextCentrePt.y * scale + transPoint.y - bitmapLabel.getHeight() / 2);
					
					PointF pointRightBottom = drakeCircusMapView.viewToSourceCoord(
							upperShop.mTextCentrePt.x * scale + transPoint.x + bitmapLabel.getWidth() / 2, 
							upperShop.mTextCentrePt.y * scale + transPoint.y + bitmapLabel.getHeight() / 2);
					
					
					RectF rect = new RectF();
					rect.left = pointLeftTop.x / polygonCoordinateRatio;
					rect.top = pointLeftTop.y / polygonCoordinateRatio;
					rect.right = pointRightBottom.x / polygonCoordinateRatio;
					rect.bottom = pointRightBottom.y / polygonCoordinateRatio;
					
                	if (upperShop.mPolygon.inBoundingBox(rect)) {
						canvas.drawBitmap(bitmapLabel, 
								upperShop.mTextCentrePt.x * scale + transPoint.x - bitmapLabel.getWidth() / 2, 
								upperShop.mTextCentrePt.y * scale + transPoint.y - bitmapLabel.getHeight() / 2, 
								paint);
                	}
				}
    		}
    		
		} else if(mLocationType == LocationType.FOOD_COURT) {
			for(ShopData foodCourtShop: m2ndFloorShops) {
				Bitmap bitmapLabel = m2ndFloorLabels.get(foodCourtShop.unitNum);
				
				if(bitmapLabel != null) {
					PointF pointLeftTop = drakeCircusMapView.viewToSourceCoord(
							foodCourtShop.mTextCentrePt.x * scale + transPoint.x - bitmapLabel.getWidth() / 2, 
							foodCourtShop.mTextCentrePt.y * scale + transPoint.y - bitmapLabel.getHeight() / 2);
					
					PointF pointRightBottom = drakeCircusMapView.viewToSourceCoord(
							foodCourtShop.mTextCentrePt.x * scale + transPoint.x + bitmapLabel.getWidth() / 2, 
							foodCourtShop.mTextCentrePt.y * scale + transPoint.y + bitmapLabel.getHeight() / 2);
					
					
					RectF rect = new RectF();
					rect.left = pointLeftTop.x / polygonCoordinateRatio;
					rect.top = pointLeftTop.y / polygonCoordinateRatio;
					rect.right = pointRightBottom.x / polygonCoordinateRatio;
					rect.bottom = pointRightBottom.y / polygonCoordinateRatio;
					
                	if (foodCourtShop.mPolygon.inBoundingBox(rect)) {
						canvas.drawBitmap(bitmapLabel, 
								foodCourtShop.mTextCentrePt.x * scale + transPoint.x - bitmapLabel.getWidth() / 2, 
								foodCourtShop.mTextCentrePt.y * scale + transPoint.y - bitmapLabel.getHeight() / 2, 
								paint);
                	}
				}
			}
			
		} else {
			for(ShopData lowerShop: mLowerMallShops) {
				Bitmap bitmapLabel = mLowerMallLabels.get(lowerShop.unitNum);
				
				if(bitmapLabel != null) {
					
					PointF pointLeftTop = drakeCircusMapView.viewToSourceCoord(
							lowerShop.mTextCentrePt.x * scale + transPoint.x - bitmapLabel.getWidth() / 2, 
							lowerShop.mTextCentrePt.y * scale + transPoint.y - bitmapLabel.getHeight() / 2);
					
					PointF pointRightBottom = drakeCircusMapView.viewToSourceCoord(
							lowerShop.mTextCentrePt.x * scale + transPoint.x + bitmapLabel.getWidth() / 2, 
							lowerShop.mTextCentrePt.y * scale + transPoint.y + bitmapLabel.getHeight() / 2);
					
					
					RectF rect = new RectF();
					rect.left = pointLeftTop.x / polygonCoordinateRatio;
					rect.top = pointLeftTop.y / polygonCoordinateRatio;
					rect.right = pointRightBottom.x / polygonCoordinateRatio;
					rect.bottom = pointRightBottom.y / polygonCoordinateRatio;
					
                	if (lowerShop.mPolygon.inBoundingBox(rect)) {
                		canvas.drawBitmap(bitmapLabel, 
    							lowerShop.mTextCentrePt.x * scale + transPoint.x - bitmapLabel.getWidth() / 2, 
    							lowerShop.mTextCentrePt.y * scale + transPoint.y - bitmapLabel.getHeight() / 2, 
    							paint);
                	}
				}
			}	
		}
        
        
        // draw facility icons(ratio is 1)
        for(FacilityData facilityData: mFacilities) {
        	if(facilityData.mLocation == mLocationType) {
        		Bitmap bitmapIcon;
        		if(mSelectedFacility != null && mSelectedFacility.mType == facilityData.mType) {
        			bitmapIcon = ImageUtil.RotateBitmap(mHighlightBitmapFacilities.get(facilityData.mType), scale / 2, 0);
        		} else {
        			bitmapIcon = ImageUtil.RotateBitmap(mBitmapFacilities.get(facilityData.mType), scale / 2, 0);
        		}
        		
        		canvas.drawBitmap(bitmapIcon, 
        				facilityData.mCentrePt.x * scale + transPoint.x - bitmapIcon.getWidth() / 2, 
        				facilityData.mCentrePt.y * scale + transPoint.y - bitmapIcon.getHeight() / 2, 
						paint);
        	}
        }
        
        
        // draw user location
    	if ( isUserShowing ) {
	        if(myLocation != null && myLocation.latitude >= leftTopLocation.latitude && 
	    		myLocation.latitude <= rightBottomLocation.latitude && 
	    		myLocation.longitude >= leftTopLocation.longitude && 
	    		myLocation.longitude <= rightBottomLocation.longitude) {
	        			
	    		PointF sCenter = new PointF(sourceX, sourceY);
	    		PointF vCenter = drakeCircusMapView.sourceToViewCoord(sCenter);
	    		paint = new Paint();
	    		paint.setAntiAlias(true);
	    		paint.setStyle(Style.FILL);
	    		paint.setStrokeCap(Cap.ROUND);
	    		paint.setStrokeWidth(0);
	    		paint.setColor(Color.parseColor("#55037aff"));
	    		canvas.drawCircle(vCenter.x, vCenter.y, 25, paint);
	    		paint.setColor(Color.WHITE);
	    		canvas.drawCircle(vCenter.x, vCenter.y, 15, paint);
	    		paint.setColor(Color.parseColor("#037aff"));
	    		canvas.drawCircle(vCenter.x, vCenter.y, 12, paint);
	        }
    	}
	}
	
	public void onSingleTapMapView(MotionEvent e) {
		if (drakeCircusMapView != null && drakeCircusMapView.isReady()) {
            PointF sCoord = drakeCircusMapView.viewToSourceCoord(e.getX(), e.getY());
            
            //
            mSelectedShop = null;
            mSelectedFacility = null;
            Point regionCoord = new Point(sCoord.x / polygonCoordinateRatio, sCoord.y / polygonCoordinateRatio);
            for (final ShopData polygonData: mCurrentShops) {
            	if (polygonData.mPolygon.contains(regionCoord)) {
            		
            		if ( polygonData.storeName.equals("New Retailer Coming Soon"))
            			break;
            		
            		mSelectedShop = polygonData;
	                //Toast.makeText(getApplicationContext(), "Selected store = " + polygon.mStrTitle, Toast.LENGTH_SHORT).show();
            		
                    final PointF center = new PointF(polygonData.mTextCentrePt.x * polygonCoordinateRatio, polygonData.mTextCentrePt.y * polygonCoordinateRatio);
                    AnimationBuilder animationBuilder = drakeCircusMapView.animateCenter(center);
                    animationBuilder.withDuration(500).start();
                    isMapAnimating = true;
            		
            		new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if(popupStore != null && popupStore.isShowing())
								popupStore.dismiss();
							
							popupStore = new ShowStorePopup(ActivityCentreMap.this, polygonData);
							popupStore.setOnActionItemClickListener(new OnActionItemClickListener() {
								@Override
								public void onItemClick(ShowStorePopup source, View view) {
									if (view.getId() == R.id.view_goto_store) {
										ShopData shopData = (ShopData)view.getTag();
										if(shopData.storeId != 0) {
											Intent intent = new Intent(ActivityCentreMap.this, ActivityShopDetail.class);
											intent.putExtra(Constants.SELECTED_STORE_ID, shopData.storeId);
											startActivity(intent);
											ActivityCentreMap.this.overridePendingTransition(R.anim.in_left, R.anim.out_left);
										}
									}
								}
							});
							PointF viewCenterCoord = drakeCircusMapView.sourceToViewCoord(center);
							popupStore.show(drakeCircusMapView, viewCenterCoord);
							isMapAnimating = false;
						}
					}, 500);
            		
            		break;
            	}
            }
            
            if(mSelectedShop == null) {
            	for (final FacilityData facilityData: mFacilities) {
            		
            		if(facilityData.mLocation != mLocationType)
            			continue;
            		
            		Bitmap bitmapIcon = ImageUtil.RotateBitmap(mBitmapFacilities.get(facilityData.mType), drakeCircusMapView.getScale(), 0);
            		RectF rectIcon = new RectF(facilityData.mCentrePt.x - bitmapIcon.getWidth() / 4,
            				facilityData.mCentrePt.y - bitmapIcon.getHeight() / 4,
            				facilityData.mCentrePt.x + bitmapIcon.getWidth() / 4,
            				facilityData.mCentrePt.y + bitmapIcon.getHeight() / 4);
            		
                	if (rectIcon.contains(regionCoord.x, regionCoord.y)) {
                		mSelectedFacility = facilityData;
                		
                        final PointF center = new PointF(facilityData.mCentrePt.x * polygonCoordinateRatio, facilityData.mCentrePt.y * polygonCoordinateRatio);
                        AnimationBuilder animationBuilder = drakeCircusMapView.animateCenter(center);
                        animationBuilder.withDuration(500).start();
                        isMapAnimating = true;
                		
                		new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								if(popupStore != null && popupStore.isShowing())
									popupStore.dismiss();
								
								popupStore = new ShowStorePopup(ActivityCentreMap.this, 0, 0, facilityData);
								PointF viewCenterCoord = drakeCircusMapView.sourceToViewCoord(center);
								popupStore.show(drakeCircusMapView, viewCenterCoord);
								isMapAnimating = false;
							}
						}, 500);
                		
                		break;
                	}
                }
            }
            //Toast.makeText(getApplicationContext(), "Single tap: " + ((int)sCoord.x) + ", " + ((int)sCoord.y), Toast.LENGTH_SHORT).show();
            
            drakeCircusMapView.invalidate();
            
        } else {
            //Toast.makeText(getApplicationContext(), "Single tap: Image not ready", Toast.LENGTH_SHORT).show();
        }
	}
}
