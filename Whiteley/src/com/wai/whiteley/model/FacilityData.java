package com.wai.whiteley.model;

import com.wai.whiteley.activities.ActivityCentreMap.FacilityType;
import com.wai.whiteley.activities.ActivityCentreMap.LocationType;
import com.wai.whiteley.polygon.Point;

public class FacilityData {
	public LocationType mLocation;
	public Point mCentrePt;
	public FacilityType mType;

	public FacilityData(LocationType location, Point point, FacilityType type) {
		this.mLocation = location;
		this.mCentrePt = point;
		this.mType = type;
	}
}
