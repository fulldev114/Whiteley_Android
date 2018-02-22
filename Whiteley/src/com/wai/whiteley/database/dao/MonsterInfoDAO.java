package com.wai.whiteley.database.dao;

public class MonsterInfoDAO {
	public String monster_beacon_uuid;
	public int monster_beacon_major;
	public int monster_beacon_minor;
	public int monster_id;
	public String monster_name;
	public String monster_details;
	public String monster_image;
	public String notification;

	public MonsterInfoDAO() {
		monster_beacon_uuid = "";
		monster_beacon_major = 0;
		monster_beacon_minor = 0;
		monster_id = 0;
		monster_name = "";
		monster_details = "";
		monster_image = "";
		notification = "";
	}

	public MonsterInfoDAO(String inUuid, int inMajor, int inMinor, int inId, String inName, String inDetails, String inImage, String inNotif) {
		monster_beacon_uuid = inUuid;
		monster_beacon_major = inMajor;
		monster_beacon_minor = inMinor;
		monster_id = inId;
		monster_name = inName;
		monster_details = inDetails;
		monster_image = inImage;
		notification = inNotif;
	}
}
