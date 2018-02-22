package com.wai.whiteley;

import java.util.Collection;

import com.radiusnetworks.ibeacon.IBeacon;

public interface DrakeCircusWindow {
	
	public void onDetectedBeacon(Collection<IBeacon> beacons);
	
}
