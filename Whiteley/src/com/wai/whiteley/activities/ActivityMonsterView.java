package com.wai.whiteley.activities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.fading_entrances.FadeInAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutAnimator;
import com.radiusnetworks.ibeacon.IBeacon;
import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.database.dao.MonsterInfoDAO;
import com.wai.whiteley.service.BeaconDetectService;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.DCImageLoader;
import com.wai.whiteley.util.FontUtils;
import com.wai.whiteley.util.ImageUtil;
import com.wai.whiteley.view.GIFView;
import com.wai.whiteley.view.MonsterExitPopup;
import com.wai.whiteley.view.MonsterThumbnailView;
import com.wai.whiteley.view.MonsterTooltipPopup;

public class ActivityMonsterView extends BaseActivity {

	public static ActivityMonsterView instance = null;

	private ArrayList<Bitmap> monsterThumbs = new ArrayList<Bitmap>();
	private ArrayList<MonsterThumbnailView> monsterViews = new ArrayList<MonsterThumbnailView>();
	
	
	private int mOffset = 0;
	
	private RelativeLayout rootView;
	
	private ImageButton btnHome;
	private GIFView gifSearching;
	
	private RelativeLayout layerHeader;
	private RelativeLayout layerMain;
	
	private RelativeLayout layer_monster_view;
	private TextView txtDescriptionTitle;
	private GridView lst_data;
	
	private RelativeLayout layerGallery;
	private ImageButton btnCloseGallery;
	private RelativeLayout viewGallery;
	
	private LinearLayout layer_empty_view;
	private TextView txtEmptyDesc;
	private TextView txtEmptyDesc1;
	private ImageView imgDownArrow;
	
	private Button btnHowTo;
	private Button btnStartSearch;
	private LinearLayout btnFound;
	private TextView txtFoundCount;
	private TextView txtFoundTitle;

	private ArrayList<MonsterInfoDAO> mData = new ArrayList<MonsterInfoDAO>();
	private MonsterListAdapter mAdapter;

	private Rect _screenRect;
	private boolean _dragging = false;
	private boolean _movingMonster = false;
	private ImageView _draggingImage;
	private double _farthestDistance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monster_view);

		instance = this;
		
		rootView = (RelativeLayout) findViewById(R.id.view_root);
		
		layerHeader = (RelativeLayout) findViewById(R.id.layer_header);
		btnHome = (ImageButton) findViewById(R.id.button_home);
		btnHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MonsterExitPopup popup = new MonsterExitPopup(ActivityMonsterView.this);
				popup.setOnActionItemClickListener(new MonsterExitPopup.OnActionItemClickListener() {
					@Override
					public void onItemClick(MonsterExitPopup source, View view) {
						switch (view.getId()) {
						case R.id.button_exit_keep:
							
							break;
						case R.id.button_exit_stop:
							BeaconDetectService.mStarted = false;
							break;
						default:
							break;
						}
						
						finish();
					}
				});

				popup.show(v);
			}
		});
		gifSearching = (GIFView) findViewById(R.id.gif_searching);
		gifSearching.loadGIFResource(this, R.drawable.thumbnail);
		layerMain = (RelativeLayout) findViewById(R.id.layer_main);
		txtDescriptionTitle = (TextView) findViewById(R.id.txt_desc);
		layerGallery = (RelativeLayout) findViewById(R.id.layer_gallery);
		viewGallery = (RelativeLayout) findViewById(R.id.view_gallery);
		btnCloseGallery = (ImageButton) findViewById(R.id.button_close_gallery);
		btnCloseGallery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewGallery.removeAllViews();
				btnCloseGallery.setVisibility(View.GONE);
				
				layerMain.setVisibility(View.VISIBLE);
				layerHeader.setVisibility(View.VISIBLE);
			}
		});
		
		layer_empty_view = (LinearLayout) findViewById(R.id.layer_empty_view);
		layer_monster_view = (RelativeLayout) findViewById(R.id.layer_monster_view);
		txtEmptyDesc = (TextView) findViewById(R.id.txt_empty_desc);
		txtEmptyDesc1 = (TextView) findViewById(R.id.txt_empty_desc_1);
		imgDownArrow = (ImageView) findViewById(R.id.view_down_arrow);
		
		btnHowTo = (Button) findViewById(R.id.button_howto);
		btnHowTo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityMonsterView.this, ActivityMonsterCoach.class);
				intent.putExtra(ActivityMonsterCoach.EXTRA_KEY_FROM_MONSTER_VIEW, true);
				startActivity(intent);
			}
		});
		
		btnStartSearch = (Button) findViewById(R.id.button_search);
		if(BeaconDetectService.mStarted) {
			btnStartSearch.setText("STOP SEARCH");
			btnStartSearch.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.stop_search), null, null);
			txtEmptyDesc.setText("NOW WALK AROUND THE CENTRE");
			txtEmptyDesc1.setText("YOU'RE BOUND TO FIND THEM!");
			imgDownArrow.setVisibility(View.INVISIBLE);
			gifSearching.setVisibility(View.VISIBLE);
		} else {
			btnStartSearch.setText("START SEARCH");
			btnStartSearch.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.start_search), null, null);
			txtEmptyDesc.setText("YOU HAVEN'T FOUND ANY EGGS YET");
			txtEmptyDesc1.setText("GET SEARCHING!");
			imgDownArrow.setVisibility(View.VISIBLE);
			gifSearching.setVisibility(View.GONE);
		}
		
		btnStartSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
				BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
				boolean bluetoothEnabled = bluetoothAdapter.isEnabled();
				
				boolean gpsEnabled = false;
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				try {
					gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				if((bluetoothEnabled && gpsEnabled && !BeaconDetectService.mStarted) || BeaconDetectService.mStarted) {
					BeaconDetectService.mStarted = !BeaconDetectService.mStarted;
					
					if(BeaconDetectService.mStarted) {
						btnStartSearch.setText("STOP SEARCH");
						btnStartSearch.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.stop_search), null, null);
						gifSearching.setVisibility(View.VISIBLE);
					} else {
						btnStartSearch.setText("START SEARCH");
						btnStartSearch.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.start_search), null, null);
						gifSearching.setVisibility(View.GONE);
					}
					
					FadeOutAnimator anim = new FadeOutAnimator();
					anim.setTarget(layer_empty_view);
					anim.setDuration(Constants.FADING_TIMEOUT);
					anim.animate();

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if(BeaconDetectService.mStarted) {
								txtEmptyDesc.setText("NOW WALK AROUND THE CENTRE");
								txtEmptyDesc1.setText("YOU'RE BOUND TO FIND THEM!");
								imgDownArrow.setVisibility(View.INVISIBLE);
							} else {
								txtEmptyDesc.setText("YOU HAVEN'T FOUND ANY EGGS YET");
								txtEmptyDesc1.setText("GET SEARCHING!");
								imgDownArrow.setVisibility(View.VISIBLE);
							}
							
							FadeInAnimator anim = new FadeInAnimator();
							anim.setTarget(layer_empty_view);
							anim.setDuration(Constants.FADING_TIMEOUT);
							anim.animate();
						}
					}, Constants.FADING_TIMEOUT);
					
				} else {
					MonsterTooltipPopup popup = new MonsterTooltipPopup(ActivityMonsterView.this, bluetoothEnabled, gpsEnabled);
					popup.setOnActionItemClickListener(new MonsterTooltipPopup.OnActionItemClickListener() {
						@Override
						public void onItemClick(MonsterTooltipPopup source, View view) {
							
						}
					});

					popup.show(btnStartSearch);
				}
			}
		});
		btnFound = (LinearLayout) findViewById(R.id.button_found);
		btnFound.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		txtFoundCount = (TextView) findViewById(R.id.text_found_count);
		txtFoundTitle = (TextView) findViewById(R.id.text_found_title);
		
		lst_data = (GridView) findViewById(R.id.lst_data);
		mAdapter = new MonsterListAdapter(this);
		lst_data.setAdapter(mAdapter);
		loadMonsterData();

	    // set fonts
		FontUtils.setTypeface(txtDescriptionTitle, FontUtils.font_Novecentowide_DemiBold, false);
		FontUtils.setTypeface(btnHowTo, FontUtils.font_Novecentowide_Light, false);
		FontUtils.setTypeface(btnStartSearch, FontUtils.font_Novecentowide_Medium, false);
		FontUtils.setTypeface(txtFoundTitle, FontUtils.font_Novecentowide_Light, false);
		FontUtils.setTypeface(txtFoundCount, FontUtils.font_HelveticaNeueThin, false);
		FontUtils.setTypeface(txtEmptyDesc, FontUtils.font_HelveticaNeueThin, false);
		FontUtils.setTypeface(txtEmptyDesc1, FontUtils.font_Novecentowide_DemiBold, false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		loadMonsterData();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_right, R.anim.out_right);
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
			if (ActivityMain.instance != null)
				ActivityMain.instance.selectMainMenu();
    	}
    }
	
	private void loadMonsterData() {
		mData = DrakeCircusApplication.mInstance.dbHelper.getAllMonsters();
		mAdapter.notifyDataSetChanged();
		txtFoundCount.setText(mData.size() + "/6");
		
		int nSize = mData.size();
		if (nSize == 0) {
			if(BeaconDetectService.mStarted) {
				btnStartSearch.setText("STOP SEARCH");
				btnStartSearch.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.stop_search), null, null);
				txtEmptyDesc.setText("NOW WALK AROUND THE CENTRE");
				txtEmptyDesc1.setText("YOU'RE BOUND TO FIND THEM!");
				imgDownArrow.setVisibility(View.INVISIBLE);
				gifSearching.setVisibility(View.VISIBLE);
			} else {
				btnStartSearch.setText("START SEARCH");
				btnStartSearch.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.start_search), null, null);
				txtEmptyDesc.setText("YOU HAVEN'T FOUND ANY EGGS YET");
				txtEmptyDesc1.setText("GET SEARCHING!");
				imgDownArrow.setVisibility(View.VISIBLE);
				gifSearching.setVisibility(View.GONE);
			}
			layer_empty_view.setVisibility(View.VISIBLE);
			layer_monster_view.setVisibility(View.GONE);
			rootView.setBackgroundResource(R.drawable.blurred_background);
		} else {
			layer_empty_view.setVisibility(View.GONE);
			layer_monster_view.setVisibility(View.VISIBLE);
			rootView.setBackgroundColor(getResources().getColor(R.color.color_easter_back));
		}
	}
	
	void placeCard() {
		
		monsterThumbs.clear();
		monsterViews.clear();
		
		int cardWidth = (int)(_screenRect.width() * 0.8f);
		int cardHeight = (int)(_screenRect.height() * 0.8f);
		
		for(int i = 0; i < mData.size(); i++) {
			try {
				MonsterInfoDAO monster = mData.get(i);
				
				URL url = new URL(monster.monster_image);
				File imageFile = new File(getFilesDir(), url.getFile());
				
				Options options = new Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
				
				MonsterThumbnailView cardView = new MonsterThumbnailView(this);
				
				int diffMargin = (int)((mData.size() - i) * CommonUtil.convertDpToPixel(15, this));
				
				RelativeLayout.LayoutParams cardParams = new RelativeLayout.LayoutParams(cardWidth - diffMargin, cardHeight - diffMargin);
				cardParams.setMargins(
					(_screenRect.width() - cardWidth) / 2 + diffMargin / 2,
					(_screenRect.height() - cardHeight) / 2 + (int)CommonUtil.convertDpToPixel(15, this) * mData.size() - diffMargin, 
					0, 
					0);
				
				cardView.setLayoutParams(cardParams);
				Bitmap bitmapThumb = ImageUtil.decodeSampledBitmapFromFile(imageFile.getAbsolutePath(), cardWidth, cardHeight);
				
				monsterThumbs.add(bitmapThumb);
				
				cardView.setImage(bitmapThumb);
				cardView.setDrawingCacheEnabled(true);
				cardView.setOnTouchListener(new View.OnTouchListener() {
		
					private Matrix _matrix = new Matrix();
					private PointF _start = new PointF();
					
					@Override
					public boolean onTouch(final View v, MotionEvent event) {
						if(_movingMonster)
							return true;

						switch ( event.getAction()  ) {
							case MotionEvent.ACTION_DOWN:
								_dragging = true;
								//
								// To reduce flicker and improve performance, we'll be 
								// simulating the movement of the view by moving it's cached
								// Bitmap image instead of the view itself.
								//
								
								if(_draggingImage != null) {
									try {
										viewGallery.removeView(_draggingImage);
									} catch(Exception e) {
										e.printStackTrace();
									}
								}
								
								_draggingImage = new ImageView(ActivityMonsterView.this);
								_draggingImage.setImageBitmap(v.getDrawingCache());
								_draggingImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
								_draggingImage.setScaleType(ScaleType.MATRIX);
								
								viewGallery.addView(_draggingImage);
								
								_start.x = event.getX();
								_start.y = event.getY();
		
								//
								// Initial location of the moving Bitmap should 
								// match the location of the original view.
								//
								
								Matrix initialMatrix = new Matrix();
								initialMatrix.postTranslate(
									v.getLeft(), 
									v.getTop()
								);
								
								_draggingImage.setImageMatrix(initialMatrix);
								
								//
								// Hide the original view while moving the Bitmap.
								//
								
								v.setVisibility(View.INVISIBLE);
								break;
								
								
							case MotionEvent.ACTION_UP:
								_dragging = false;
								
								//
								// Return the view back to its starting position.
								//
								_movingMonster = true;
								
								if(Math.abs(_start.y - event.getY()) < _screenRect.height() / 6) {
									TranslateAnimation anim = new TranslateAnimation(
										0, 0,//_start.x - event.getX(),
										0, _start.y - event.getY()
									);
									
									anim.setAnimationListener(new TranslateAnimation.AnimationListener() {
										@Override
										public void onAnimationStart(Animation animation) { 
										}
			
										@Override
										public void onAnimationRepeat(Animation animation) { 
										}
			
										@Override
										public void onAnimationEnd(Animation animation) {
											viewGallery.removeView(_draggingImage);
											_draggingImage = null;
											
											v.setVisibility(View.VISIBLE);
										}
									});
									
									anim.setDuration(500);
									if(_draggingImage != null)
										_draggingImage.startAnimation(anim);
									
								} else {
									final float diffY = event.getY() - _start.y;
									
									TranslateAnimation anim = new TranslateAnimation(
											0, 0,//_start.x - event.getX(),
											0, diffY > 0 ? v.getHeight() : -v.getHeight()
										);
										
										anim.setAnimationListener(new TranslateAnimation.AnimationListener() {
											@Override
											public void onAnimationStart(Animation animation) { 
											}
				
											@Override
											public void onAnimationRepeat(Animation animation) { 
											}
				
											@Override
											public void onAnimationEnd(Animation animation) {
												viewGallery.removeView(_draggingImage);
												_draggingImage = null;
												
												v.setVisibility(View.VISIBLE);
												
												if(diffY > 0)
													mOffset--;
												else
													mOffset++;
												
												if(mOffset < 0)
													mOffset = (mOffset + monsterThumbs.size()) % monsterThumbs.size();
												else if(mOffset >= monsterThumbs.size())
													mOffset = mOffset % monsterThumbs.size();
												
												for(int i = 0; i < monsterThumbs.size(); i++) {
													
													monsterViews.get(monsterThumbs.size() - 1 - i).destroyDrawingCache();
													monsterViews.get(monsterThumbs.size() - 1 - i).setImage(monsterThumbs.get((mOffset - i + monsterThumbs.size()) % monsterThumbs.size()));
												}
											}
										});
										
										anim.setDuration(500);
										if(_draggingImage != null)
											_draggingImage.startAnimation(anim);
								}
								
								new Handler().postDelayed(new Runnable() {
									
									@Override
									public void run() {
										_movingMonster = false;
									}
								}, 600);
								
								break;
								
							case MotionEvent.ACTION_MOVE:
								if ( _dragging ) {
									_matrix.reset();
									_matrix.postTranslate(
										v.getLeft(),//event.getX() - _start.x + v.getLeft(), 
										event.getY() - _start.y + v.getTop()
									);
									
									// 
									// Increase the transparency as the 
									// view moves closer to the edges of the
									// screen.
									//
									
//									double distance = Math.sqrt(
//										Math.pow((_start.x - event.getX()), 2) + 
//										Math.pow((_start.y - event.getY()), 2)
//									);
//									
//									double delta = (_farthestDistance - distance) * 0.80f;
//									float alpha = (float)(delta / _farthestDistance);
//									
//									_draggingImage.setAlpha(alpha);
									
									if(_draggingImage != null)
										_draggingImage.setImageMatrix(_matrix);
									
									//////////////////
									if(monsterViews.size() > 1) {
										float diffY = event.getY() - _start.y;
										int nextOffset;
										if(diffY > 0)
											nextOffset = mOffset - 1;
										else
											nextOffset = mOffset + 1;
										
										if(nextOffset < 0)
											nextOffset = (nextOffset + monsterThumbs.size()) % monsterThumbs.size();
										else if(nextOffset >= monsterThumbs.size())
											nextOffset = nextOffset % monsterThumbs.size();
										
										monsterViews.get(monsterThumbs.size() - 2).destroyDrawingCache();
										monsterViews.get(monsterThumbs.size() - 2).setImage(monsterThumbs.get((nextOffset + monsterThumbs.size()) % monsterThumbs.size()));
									}
								}
								break;
						}
						return true;
					}
				});
				
				monsterViews.add(cardView);
				viewGallery.addView(cardView);
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class MonsterListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MonsterListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			if(mData.size() < 6)
				return 6;
			else
				return mData.size();
		}

		public Object getItem(int position) {
			return mData.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			final ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.row_monster, parent, false);
				holder = new ViewHolder();
				holder.imgMonsterThumb = (ImageView) convertView.findViewById(R.id.img_monster);
				holder.btnRemove = (Button) convertView.findViewById(R.id.button_remove);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if(position < mData.size()) {
				MonsterInfoDAO data = mData.get(mData.size() - 1 - position);

				holder.imgMonsterThumb.setImageResource(R.drawable.empty_card);
				if (!TextUtils.isEmpty(data.monster_image)) {
					try {
//						URL urlLabel = new URL(data.monster_image);
//			        	final File fileLabel = new File(getFilesDir(), urlLabel.getFile());
//						
//			        	if(fileLabel.exists()) {
//			        		new Handler().post(new Runnable() {
//								
//								@Override
//								public void run() {
//									Bitmap bitmap = ImageUtil.decodeSampledBitmapFromFile(fileLabel.getAbsolutePath(), 200, 269);
//					        		holder.imgMonsterThumb.setImageDrawable(new BitmapDrawable(bitmap));
//								}
//							});
//			        	} else {
			        		DCImageLoader.showImage(holder.imgMonsterThumb, data.monster_image);
//			        	}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}

				holder.btnRemove.setTag(data.monster_id);
				holder.btnRemove.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						final int monsterId = Integer.parseInt(v.getTag().toString());
						
						new AlertDialog.Builder(ActivityMonsterView.this)
						.setTitle("Remove this Easter Egg?")
						.setMessage("Select 'Remove' to permanently delete this egg from your phone.")
						.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								DrakeCircusApplication.getInstance().dbHelper.deleteMonster(monsterId);
								loadMonsterData();
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.create()
						.show();
						
					}
				});
				holder.btnRemove.setVisibility(View.VISIBLE);
				holder.imgMonsterThumb.setTag(position);
				holder.imgMonsterThumb.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						layerMain.setVisibility(View.GONE);
						layerHeader.setVisibility(View.GONE);
						layerGallery.setVisibility(View.VISIBLE);
						btnCloseGallery.setVisibility(View.VISIBLE);
						
						_screenRect = new Rect();
						viewGallery.getLocalVisibleRect(_screenRect);

						Point center = new Point(_screenRect.centerX(), _screenRect.centerY());
						Point farthest = new Point(_screenRect.width(), _screenRect.height());
						 
						_farthestDistance = Math.sqrt(
							Math.pow((center.x - farthest.x), 2) + 
							Math.pow((center.y - farthest.y), 2)
						);
						
						placeCard();
						
						mOffset = monsterViews.size() - 1 - Integer.parseInt(v.getTag().toString());
						for(int i = 0; i < monsterThumbs.size(); i++) {
							monsterViews.get(monsterThumbs.size() - 1 - i).destroyDrawingCache();
							monsterViews.get(monsterThumbs.size() - 1 - i).setImage(monsterThumbs.get((mOffset - i + monsterThumbs.size()) % monsterThumbs.size()));
						}
					}
				});
			} else {
				holder.imgMonsterThumb.setImageResource(R.drawable.empty_card);
				holder.btnRemove.setVisibility(View.INVISIBLE);
			}
			
			FontUtils.setTypeface(holder.btnRemove, FontUtils.font_Novecentowide_DemiBold, false);
			
			return convertView;
		}
		
		class ViewHolder {
			ImageView imgMonsterThumb;
			Button btnRemove;
		}
	}

	@Override
	public void onDetectedBeacon(Collection<IBeacon> beacons) {
		super.onDetectedBeacon(beacons);
	}
}
