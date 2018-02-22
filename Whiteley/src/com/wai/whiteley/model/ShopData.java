package com.wai.whiteley.model;

import com.wai.whiteley.polygon.Point;
import com.wai.whiteley.polygon.Polygon;

public class ShopData {
	public Polygon mPolygon;
	public String unitNum;
	public Point mTextCentrePt;
	public int angle;
	
	public int storeId;
	public String storeName;
	public int hasOffer;
	public int favorite;

	public ShopData(Polygon inPolygon, String inUnitNum, Point inPt) {
		this(inPolygon, inUnitNum, inPt, 0);
	}
	
	public ShopData(Polygon inPolygon, String inUnitNum, Point inPt, int inAngle) {
		this.mPolygon = inPolygon;
		this.unitNum = inUnitNum;
		this.mTextCentrePt = inPt;
		this.angle = inAngle;
	}
}
