package com.wai.whiteley.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wai.whiteley.database.dao.MonsterInfoDAO;
import com.wai.whiteley.database.dao.StoreNameDAO;
import com.wai.whiteley.http.ResponseModel;
import com.wai.whiteley.http.ResponseModel.StoreCategoryModel;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static Object DB_LOCK = new Object();
	private static final String DATABASE_NAME = "drakecircus.db";
	private static final int DATABASE_VERSION = 1;

	/*
	 *  DB Tables Name
	 */
	private static final String TABLE_STORE_NAME = "StoreName";
	private static final String TABLE_STORE_CATEGORY = "StoreCategory";
	private static final String TABLE_RELATION_STORE_CATEGORY = "RelationStoreCategory";
	private static final String TABLE_MONSTER_INFO = "MonsterInfo";
	private static final String TABLE_USER_DETECT = "UserDetect";

	/*
	 *  TABLE_STORE_NAME
	 */
	private static final String COL_STORE_ID = "store_id";
	private static final String COL_STORE_NAME = "store_name";
	private static final String COL_STORE_LABEL = "label";
	private static final String COL_STORE_FAVOURITE = "favourite";
	private static final String COL_STORE_HASOFFER = "has_offer";
	private static final String COL_STORE_UNITNUMBER = "unit_num";
	private static final String COL_STORE_LOCATION = "location";

	/*
	 *  TABLE_STORE_CATEGORY
	 */
	private static final String COL_STORE_CATEGORY_ID = "store_category_id";
	private static final String COL_STORE_CATEGORY_NAME = "store_category_name";
	
	/*
	 *  TABLE_RELATION_STORE_CATEGORY
	 */
	private static final String COL_RELATION_CATEGORY_ID = "category_id";
	private static final String COL_RELATION_STORE_ID = "store_id";
	
	/*
	 *  TABLE_MONSTERINFO
	 */
	private static final String COL_MONSTER_BEACON_UUID = "beacon_uuid";
	private static final String COL_MONSTER_BEACON_MAJOR = "beacon_major";
	private static final String COL_MONSTER_BEACON_MINOR = "beacon_minor";
	private static final String COL_MONSTER_ID = "monster_id";
	private static final String COL_MONSTER_NAME = "monster_name";
	private static final String COL_MONSTER_DETAILS = "monster_details";
	private static final String COL_MONSTER_IMAGE = "monster_image";
	private static final String COL_MONSTER_NOTIFICATION = "notification";
	
	/*
	 *  TABLE_MONSTERINFO
	 */
	private static final String COL_USER_DETECT_UUID = "beacon_uuid";
	private static final String COL_USER_DETECT_MAJOR = "beacon_major";
	private static final String COL_USER_DETECT_MINOR = "beacon_minor";
	private static final String COL_USER_DETECT_TIME = "beacon_time";
	
	private SQLiteDatabase database;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "PRAGMA foreign_keys=OFF;";
		db.execSQL(sql);
		
		sql = "CREATE TABLE " + TABLE_STORE_NAME + 
				" (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COL_STORE_ID + 			" INTEGER, " + 
				COL_STORE_NAME + 		" VARCHAR, " + 
				COL_STORE_LABEL + 		" VARCHAR, " +
				COL_STORE_FAVOURITE + 	" INTEGER, " + 
				COL_STORE_HASOFFER + 	" INTEGER, " + 
				COL_STORE_UNITNUMBER + 	" VARCHAR, " + 
				COL_STORE_LOCATION + 	" VARCHAR);";
        db.execSQL(sql);
		
        sql = "CREATE TABLE " + TABLE_STORE_CATEGORY + 
				" (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COL_STORE_CATEGORY_ID +		" INTEGER, " + 
				COL_STORE_CATEGORY_NAME + 	" VARCHAR);";
        db.execSQL(sql);
        
        sql = "CREATE TABLE " + TABLE_RELATION_STORE_CATEGORY + 
				" (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COL_RELATION_CATEGORY_ID +	" INTEGER, " + 
				COL_RELATION_STORE_ID + 	" INTEGER);";
        db.execSQL(sql);
        
        sql = "CREATE TABLE " + TABLE_MONSTER_INFO + 
        		" (id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
        		COL_MONSTER_BEACON_UUID + 	" VARCHAR, " +
        		COL_MONSTER_BEACON_MAJOR + 	" INTEGER, " +
        		COL_MONSTER_BEACON_MINOR + 	" INTEGER, " +
        		COL_MONSTER_ID + 			" INTEGER, " +
        		COL_MONSTER_NAME + 			" VARCHAR, " +
        		COL_MONSTER_DETAILS + 		" VARCHAR, " +
        		COL_MONSTER_IMAGE + 		" VARCHAR, " +
        		COL_MONSTER_NOTIFICATION + 	" VARCHAR);";
        db.execSQL(sql);
		
        sql = "CREATE TABLE " + TABLE_USER_DETECT + 
        		" (id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
        		COL_USER_DETECT_UUID + 		" VARCHAR, " +
        		COL_USER_DETECT_MAJOR + 	" INTEGER, " +
        		COL_USER_DETECT_MINOR + 	" INTEGER," +
        		COL_USER_DETECT_TIME +		" LONG" + ");";
        db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
        default:
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RELATION_STORE_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONSTER_INFO);
            onCreate(db);
		}
	}

	public void openDataBase() throws SQLException {
		// Open the database
		database = getWritableDatabase();
	}

	public void closeDatabase() {
		database.close();
	}

	/*
	 * store_name data
	 */
	public void addOrUpdateOneStore(StoreNameDAO store) {
		synchronized (DatabaseHelper.DB_LOCK) {
			ContentValues values = new ContentValues();
			values.put(COL_STORE_ID, store.id);
			values.put(COL_STORE_NAME, store.name);
			values.put(COL_STORE_LABEL, store.label);
			values.put(COL_STORE_HASOFFER, store.hasOffer);
			values.put(COL_STORE_UNITNUMBER, store.unitNum);
			values.put(COL_STORE_LOCATION, store.location);

			if (database != null) {
				String sql_duplication_check = "SELECT * FROM " + TABLE_STORE_NAME + " WHERE " + COL_STORE_ID + "=" + store.id;
				Cursor cursor = database.rawQuery(sql_duplication_check, null);
				if (cursor != null) {
					int count = cursor.getCount();
					if (count == 0) {
						// insert new record
						values.put(COL_STORE_FAVOURITE, 0);
						database.insert(TABLE_STORE_NAME, null, values);
					} else {
						// update old record
//						cursor.moveToFirst();
//						values.put(COL_STORE_FAVOURITE, store.favourite);
						
						database.update(TABLE_STORE_NAME, values, COL_STORE_ID + "=" + store.id, null);
					}
					cursor.close();
				}
			}		
		}
	}
	
	public void updateStoreFavorites(int storeId, int favorites) {
		synchronized (DatabaseHelper.DB_LOCK) {
			ContentValues values = new ContentValues();
			values.put(COL_STORE_FAVOURITE, favorites);
			database.update(TABLE_STORE_NAME, values, COL_STORE_ID + "=" + storeId, null);
		}
	}
	

//	public void addAllStores(ArrayList<StoreInfo> list_data) {
//		synchronized (DatabaseHelper.DB_LOCK) {
//			if (database != null) {
//				database.beginTransaction();
//
//				for (StoreInfo store : list_data) {
//					ContentValues values = new ContentValues();
//					values.put(COL_STORE_ID, store.store_id);
//					values.put(COL_STORE_CATEGORY_ID, store.store_category);
//					values.put(COL_STORE_NAME, store.store_name);
//					values.put(COL_STORE_FAVOURITE, store.favourite);
//					values.put(COL_STORE_HASOFFER, store.has_offer);
//					values.put(COL_STORE_UNITNUMBER, store.unit_num);
//					values.put(COL_STORE_LOCATION, store.location);
//
//					database.insert(TABLE_STORE_NAME, null, values);
//				}
//
//				database.setTransactionSuccessful();
//				database.endTransaction();
//			}
//		}
//	}

	public ArrayList<StoreNameDAO> getStoreInfos(int categoryId) {
		synchronized (DatabaseHelper.DB_LOCK) {
			ArrayList<StoreNameDAO> list = new ArrayList<StoreNameDAO>();

			if (database != null) {
				String sql_str;
				if(categoryId == 0)
					sql_str = "SELECT * FROM " + TABLE_STORE_NAME;
				else
					sql_str = "SELECT store.* FROM (SELECT * FROM " + TABLE_RELATION_STORE_CATEGORY + " WHERE " + COL_RELATION_CATEGORY_ID + " = " + categoryId + ") relation LEFT JOIN " + TABLE_STORE_NAME + " store ON relation." + COL_RELATION_STORE_ID + " = store." + COL_STORE_ID;
				
				Cursor cursor = database.rawQuery(sql_str, null);
				if (cursor != null) {
					if (cursor.getCount() > 0) {
						cursor.moveToFirst();
						do {
							StoreNameDAO store = new StoreNameDAO();
							store.id = cursor.getInt(cursor.getColumnIndex(COL_STORE_ID));
							store.name = cursor.getString(cursor.getColumnIndex(COL_STORE_NAME));
							store.label = cursor.getString(cursor.getColumnIndex(COL_STORE_LABEL));
							store.favourite = cursor.getInt(cursor.getColumnIndex(COL_STORE_FAVOURITE));
							store.hasOffer = cursor.getInt(cursor.getColumnIndex(COL_STORE_HASOFFER));
							store.unitNum = cursor.getString(cursor.getColumnIndex(COL_STORE_UNITNUMBER));
							store.location = cursor.getString(cursor.getColumnIndex(COL_STORE_LOCATION));

							list.add(store);
						} while (cursor.moveToNext());
					}
					cursor.close();
				}
				cursor.close();
			}
			return list;
		}
	}
	
	public ArrayList<StoreNameDAO> getFavoritedStores() {
		synchronized (DatabaseHelper.DB_LOCK) {
			ArrayList<StoreNameDAO> list = new ArrayList<StoreNameDAO>();

			if (database != null) {
				String sql_str = "SELECT * FROM " + TABLE_STORE_NAME;
				sql_str += " WHERE " + COL_STORE_FAVOURITE + " = 1";
				Cursor cursor = database.rawQuery(sql_str, null);
				if (cursor != null) {
					if (cursor.getCount() > 0) {
						cursor.moveToFirst();
						do {
							StoreNameDAO store = new StoreNameDAO();
							store.id = cursor.getInt(cursor.getColumnIndex(COL_STORE_ID));
							store.name = cursor.getString(cursor.getColumnIndex(COL_STORE_NAME));
							store.label = cursor.getString(cursor.getColumnIndex(COL_STORE_LABEL));
							store.favourite = cursor.getInt(cursor.getColumnIndex(COL_STORE_FAVOURITE));
							store.hasOffer = cursor.getInt(cursor.getColumnIndex(COL_STORE_HASOFFER));
							store.unitNum = cursor.getString(cursor.getColumnIndex(COL_STORE_UNITNUMBER));
							store.location = cursor.getString(cursor.getColumnIndex(COL_STORE_LOCATION));

							list.add(store);
						} while (cursor.moveToNext());
					}
					cursor.close();
				}
				cursor.close();
			}
			return list;
		}
	}

	public StoreNameDAO getOneStore(int store_Id) {
		synchronized (DatabaseHelper.DB_LOCK) {
			
			StoreNameDAO store = null;
			if (database != null) {
				String sql_duplication_check = "SELECT * FROM " + TABLE_STORE_NAME
						+ " WHERE " + COL_STORE_ID + "=" + store_Id;
				Cursor cursor = database.rawQuery(sql_duplication_check, null);
				if (cursor != null) {
					int count = cursor.getCount();
					if (count == 0) {
						return null;
					} else {
						cursor.moveToFirst();
						store = new StoreNameDAO();
						store.id = cursor.getInt(cursor.getColumnIndex(COL_STORE_ID));
						store.name = cursor.getString(cursor.getColumnIndex(COL_STORE_NAME));
						store.label = cursor.getString(cursor.getColumnIndex(COL_STORE_LABEL));
						store.favourite = cursor.getInt(cursor.getColumnIndex(COL_STORE_FAVOURITE));
						store.hasOffer = cursor.getInt(cursor.getColumnIndex(COL_STORE_HASOFFER));
						store.unitNum = cursor.getString(cursor.getColumnIndex(COL_STORE_UNITNUMBER));
						store.location = cursor.getString(cursor.getColumnIndex(COL_STORE_LOCATION));
					}
					cursor.close();
				}
			}
			return store;
		}
	}
	
	public StoreNameDAO getStoreData(String unitNum, String location) {
		synchronized (DatabaseHelper.DB_LOCK) {
			
			StoreNameDAO store = null;
			if (database != null) {
				String sql_duplication_check = "select * from " + TABLE_STORE_NAME
						+ " where " + COL_STORE_UNITNUMBER + "='" + unitNum + "' AND " + COL_STORE_LOCATION + "='" + location + "'";
				Cursor cursor = database.rawQuery(sql_duplication_check, null);
				if (cursor != null) {
					int count = cursor.getCount();
					if (count == 0) {
						return null;
					} else {
						cursor.moveToFirst();
						store = new StoreNameDAO();
						store.id = cursor.getInt(cursor.getColumnIndex(COL_STORE_ID));
						store.name = cursor.getString(cursor.getColumnIndex(COL_STORE_NAME));
						store.label = cursor.getString(cursor.getColumnIndex(COL_STORE_LABEL));
						store.favourite = cursor.getInt(cursor.getColumnIndex(COL_STORE_FAVOURITE));
						store.hasOffer = cursor.getInt(cursor.getColumnIndex(COL_STORE_HASOFFER));
						store.unitNum = cursor.getString(cursor.getColumnIndex(COL_STORE_UNITNUMBER));
						store.location = cursor.getString(cursor.getColumnIndex(COL_STORE_LOCATION));
					}
					cursor.close();
				}
			}
			return store;
		}
	}

	public void deleteAllStoreNames() {
		synchronized (DatabaseHelper.DB_LOCK) {
			if (database != null) {
				String sql_str = "DELETE FROM " + TABLE_STORE_NAME + " WHERE id >= 0";
				database.execSQL(sql_str);
			}
		}
	}

	/*
	 * relation_store_category data
	 */
	public void addOrUpdateRelationStoreCat(int storeId, ArrayList<String> categoryIds) {
		synchronized (DatabaseHelper.DB_LOCK) {

			if (database != null) {
				for(String categoryId: categoryIds) {
					int nCategoryId = Integer.parseInt(categoryId);
					
					String sql_duplication_check = 
							"SELECT * FROM " + TABLE_RELATION_STORE_CATEGORY + " WHERE " + 
							COL_RELATION_STORE_ID + "=" + storeId + " AND " + 
							COL_RELATION_CATEGORY_ID + "=" + nCategoryId;
					Cursor cursor = database.rawQuery(sql_duplication_check, null);
					if (cursor != null) {
						int count = cursor.getCount();
						if (count == 0) {
							// insert new record
							ContentValues values = new ContentValues();
							values.put(COL_RELATION_CATEGORY_ID, nCategoryId);
							values.put(COL_RELATION_STORE_ID, storeId);
							
							database.insert(TABLE_RELATION_STORE_CATEGORY, null, values);
						}
						cursor.close();
					}
				}
			}		
		}
	}
	
	
	/*
	 * store_category data
	 */
	public void addOrUpdateStoreCategory(StoreCategoryModel category) {
		synchronized (DatabaseHelper.DB_LOCK) {
			ContentValues values = new ContentValues();
			values.put(COL_STORE_CATEGORY_ID, category.id);
			values.put(COL_STORE_CATEGORY_NAME, category.name);

			if (database != null) {
				String sql_duplication_check = "SELECT * FROM " + TABLE_STORE_CATEGORY + " WHERE " + COL_STORE_CATEGORY_ID + "=" + category.id;
				Cursor cursor = database.rawQuery(sql_duplication_check, null);
				if (cursor != null) {
					int count = cursor.getCount();
					if (count == 0) {
						// insert new record
						database.insert(TABLE_STORE_CATEGORY, null, values);
					} else {
						// update old record
//						cursor.moveToFirst();
//						values.put(COL_STORE_FAVOURITE, store.favourite);
						
						database.update(TABLE_STORE_CATEGORY, values, COL_STORE_CATEGORY_ID + "=" + category.id, null);
					}
					cursor.close();
				}
			}		
		}
	}
	
	public ArrayList<StoreCategoryModel> getAllStoreCategories() {
		synchronized (DatabaseHelper.DB_LOCK) {
			ArrayList<StoreCategoryModel> list = new ArrayList<StoreCategoryModel>();

			if (database != null) {
				String sql_str = "SELECT * FROM " + TABLE_STORE_CATEGORY;
				Cursor cursor = database.rawQuery(sql_str, null);
				if (cursor != null) {
					if (cursor.getCount() > 0) {
						cursor.moveToFirst();
						do {
							StoreCategoryModel category = new ResponseModel().new StoreCategoryModel();
							category.id = cursor.getInt(cursor.getColumnIndex(COL_STORE_CATEGORY_ID));
							category.name = cursor.getString(cursor.getColumnIndex(COL_STORE_CATEGORY_NAME));

							list.add(category);
						} while (cursor.moveToNext());
					}
					cursor.close();
				}
				cursor.close();
			}
			return list;
		}
	}
	
	public void deleteAllStoreCategories() {
		synchronized (DatabaseHelper.DB_LOCK) {
			if (database != null) {
				String sql_str = "DELETE FROM " + TABLE_STORE_CATEGORY + " WHERE id >= 0";
				database.execSQL(sql_str);
			}
		}
	}
	/*
	 * monster_info data
	 */
	public void addOrUpdateOneMonster(MonsterInfoDAO monster) {
		synchronized (DatabaseHelper.DB_LOCK) {
			ContentValues values = new ContentValues();
			values.put(COL_MONSTER_BEACON_UUID, monster.monster_beacon_uuid);
			values.put(COL_MONSTER_BEACON_MAJOR, monster.monster_beacon_major);
			values.put(COL_MONSTER_BEACON_MINOR, monster.monster_beacon_minor);
			values.put(COL_MONSTER_ID, monster.monster_id);
			values.put(COL_MONSTER_NAME, monster.monster_name);
			values.put(COL_MONSTER_DETAILS, monster.monster_details);
			values.put(COL_MONSTER_IMAGE, monster.monster_image);
			values.put(COL_MONSTER_NOTIFICATION, monster.notification);

			try {
				if (database != null) {
					String sql_duplication_check = "SELECT * FROM " + TABLE_MONSTER_INFO + " WHERE " + COL_MONSTER_ID + "=" + monster.monster_id;
					Cursor cursor = database.rawQuery(sql_duplication_check, null);
					if (cursor != null) {
						int count = cursor.getCount();
						if (count == 0) {
							// insert new record
							database.insert(TABLE_MONSTER_INFO, null, values);
						} else {
							// update old record
							cursor.moveToFirst();
							database.update(TABLE_MONSTER_INFO, values, COL_MONSTER_ID + "=" + monster.monster_id, null);
						}
						cursor.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//	public void addAllMonsters(ArrayList<MonsterInfo> list_data) {
//		synchronized (DatabaseHelper.DB_LOCK) {
//			if (database != null) {
//				database.beginTransaction();
//
//				for (MonsterInfo monster : list_data) {
//					ContentValues values = new ContentValues();
//					values.put(COL_MONSTER_BEACON_UUID, monster.monster_beacon_uuid);
//					values.put(COL_MONSTER_BEACON_MAJOR, monster.monster_beacon_major);
//					values.put(COL_MONSTER_BEACON_MINOR, monster.monster_beacon_minor);
//					values.put(COL_MONSTER_ID, monster.monster_id);
//					values.put(COL_MONSTER_NAME, monster.monster_name);
//					values.put(COL_MONSTER_DETAILS, monster.monster_details);
//					values.put(COL_MONSTER_IMAGE, monster.monster_image);
//					values.put(COL_MONSTER_NOTIFICATION, monster.notification);
//
//					database.insert(TABLE_MONSTER_INFO, null, values);
//				}
//
//				database.setTransactionSuccessful();
//				database.endTransaction();
//			}
//		}
//	}

	public ArrayList<MonsterInfoDAO> getAllMonsters() {
		synchronized (DatabaseHelper.DB_LOCK) {
			ArrayList<MonsterInfoDAO> list = new ArrayList<MonsterInfoDAO>();

			if (database != null) {
				String sql_str = "SELECT * FROM " + TABLE_MONSTER_INFO;
				Cursor cursor = database.rawQuery(sql_str, null);
				if (cursor != null) {
					int count = cursor.getCount();
					if (count > 0) {
						cursor.moveToFirst();
						int index = 0;
						do {
							if(count - 6 - index > 0) {
								deleteMonster(cursor.getInt(cursor.getColumnIndex(COL_MONSTER_ID)));
							} else {
								MonsterInfoDAO monster = new MonsterInfoDAO();
								monster.monster_beacon_uuid = cursor.getString(cursor.getColumnIndex(COL_MONSTER_BEACON_UUID));
								monster.monster_beacon_major = cursor.getInt(cursor.getColumnIndex(COL_MONSTER_BEACON_MAJOR));
								monster.monster_beacon_minor = cursor.getInt(cursor.getColumnIndex(COL_MONSTER_BEACON_MINOR));
								monster.monster_id = cursor.getInt(cursor.getColumnIndex(COL_MONSTER_ID));
								monster.monster_name = cursor.getString(cursor.getColumnIndex(COL_MONSTER_NAME));
								monster.monster_details = cursor.getString(cursor.getColumnIndex(COL_MONSTER_DETAILS));
								monster.monster_image = cursor.getString(cursor.getColumnIndex(COL_MONSTER_IMAGE));
								monster.notification = cursor.getString(cursor.getColumnIndex(COL_MONSTER_NOTIFICATION));
	
								list.add(monster);
							}
							
							index++;
						} while (cursor.moveToNext());
					}
					cursor.close();
				}
				cursor.close();
			}
			return list;
		}
	}

	public MonsterInfoDAO getOneMonster(int monster_Id) {
		synchronized (DatabaseHelper.DB_LOCK) {
			
			MonsterInfoDAO monster = null;
			if (database != null) {
				String sql_duplication_check = "SELECT * FROM " + TABLE_MONSTER_INFO
						+ " WHERE " + COL_MONSTER_ID + "=" + monster_Id;
				Cursor cursor = database.rawQuery(sql_duplication_check, null);
				if (cursor != null) {
					int count = cursor.getCount();
					if (count == 0) {
						return null;
					} else {
						cursor.moveToFirst();
						monster = new MonsterInfoDAO();
						monster.monster_beacon_uuid = cursor.getString(cursor.getColumnIndex(COL_MONSTER_BEACON_UUID));
						monster.monster_beacon_major = cursor.getInt(cursor.getColumnIndex(COL_MONSTER_BEACON_MAJOR));
						monster.monster_beacon_minor = cursor.getInt(cursor.getColumnIndex(COL_MONSTER_BEACON_MINOR));
						monster.monster_id = cursor.getInt(cursor.getColumnIndex(COL_MONSTER_ID));
						monster.monster_name = cursor.getString(cursor.getColumnIndex(COL_MONSTER_NAME));
						monster.monster_details = cursor.getString(cursor.getColumnIndex(COL_MONSTER_DETAILS));
						monster.monster_image = cursor.getString(cursor.getColumnIndex(COL_MONSTER_IMAGE));
						monster.notification = cursor.getString(cursor.getColumnIndex(COL_MONSTER_NOTIFICATION));
					}
					cursor.close();
				}
			}
			return monster;
		}
	}
	
	public void deleteMonster(int monsterId) {
		synchronized (DatabaseHelper.DB_LOCK) {
			if (database != null) {
				String sql_str = "DELETE FROM " + TABLE_MONSTER_INFO + " WHERE " + COL_MONSTER_ID + "=" + monsterId;
				database.execSQL(sql_str);
			}
		}
	}
	
	public void deleteAllMonsters() {
		synchronized (DatabaseHelper.DB_LOCK) {
			if (database != null) {
				String sql_str = "DELETE FROM " + TABLE_MONSTER_INFO + " WHERE id >= 0";
				database.execSQL(sql_str);
			}
		}
	}
	
	public boolean isExistUserDetect(String uuid, int major, int minor) {
		synchronized (DatabaseHelper.DB_LOCK) {
			boolean retVal = false;
			
			if (database != null) {
				String sql_duplication_check = "SELECT * FROM " + TABLE_USER_DETECT + " WHERE " + COL_USER_DETECT_UUID + "='" + uuid + "' AND " + COL_USER_DETECT_MAJOR + "=" + major + " AND " + COL_USER_DETECT_MINOR + "=" + minor;
				Cursor cursor = database.rawQuery(sql_duplication_check, null);
				if (cursor != null) {
					int count = cursor.getCount();
					if (count > 0) {
						retVal = true;
					}
					cursor.close();
				}
			}

			return retVal;
		}
	}
	
	public void addUserDetect(String uuid, int major, int minor) {
		synchronized (DatabaseHelper.DB_LOCK) {
			ContentValues values = new ContentValues();
			values.put(COL_USER_DETECT_UUID, uuid);
			values.put(COL_USER_DETECT_MAJOR, major);
			values.put(COL_USER_DETECT_MINOR, minor);
			values.put(COL_USER_DETECT_TIME, 0);

			if (database != null) {
				database.insert(TABLE_USER_DETECT, null, values);
			}
		}
	}

	public long getUserDetectTime(String uuid, int major, int minor) {
		synchronized (DatabaseHelper.DB_LOCK) {
			long retVal = 0;

			if (database != null) {
				String sql_duplication_check = "SELECT * FROM " + TABLE_USER_DETECT + " WHERE " + COL_USER_DETECT_UUID + "='" + uuid + "' AND " + COL_USER_DETECT_MAJOR + "=" + major + " AND " + COL_USER_DETECT_MINOR + "=" + minor;
				Cursor cursor = database.rawQuery(sql_duplication_check, null);
				if (cursor != null) {
					int count = cursor.getCount();
					if (count > 0) {
						cursor.moveToFirst();
						retVal = cursor.getLong(cursor.getColumnIndex(COL_USER_DETECT_TIME));
					}
					cursor.close();
				}
			}

			return retVal;
		}
	}

	public void setUserDetectTime(String uuid, int major, int minor, long time) {
		synchronized (DatabaseHelper.DB_LOCK) {
			ContentValues values = new ContentValues();
			values.put(COL_USER_DETECT_UUID, uuid);
			values.put(COL_USER_DETECT_MAJOR, major);
			values.put(COL_USER_DETECT_MINOR, minor);
			values.put(COL_USER_DETECT_TIME, time);

			if (database != null) {
				String sql_duplication_check = "SELECT * FROM " + TABLE_USER_DETECT + " WHERE " + COL_USER_DETECT_UUID + "='" + uuid + "' AND " + COL_USER_DETECT_MAJOR + "=" + major + " AND " + COL_USER_DETECT_MINOR + "=" + minor;
				Cursor cursor = database.rawQuery(sql_duplication_check, null);
				if (cursor != null) {
					int count = cursor.getCount();
					if (count > 0) {
						database.update(TABLE_USER_DETECT,
								values,
								COL_USER_DETECT_UUID + "='" + uuid + "' AND " + COL_USER_DETECT_MAJOR + "=" + major + " AND " + COL_USER_DETECT_MINOR + "=" + minor,
								null);
					}
					else {
						database.insert(TABLE_USER_DETECT, null, values);
					}
				}
			}
		}
	}
}
