package com.wai.whiteley.database.dao;


public class StoreNameDAO {
	public int id;
	public String name;
	public String label;
	public int hasOffer;
	public String unitNum;
	public String location;
	
	public int favourite; // 0: false, 1: true

	public StoreNameDAO() {
		id = -1;
		name = "";
		label = "";
		hasOffer = 0;
		unitNum = "";
		location = "";
		
		favourite = 0;
	}
	
}
