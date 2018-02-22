package com.wai.whiteley.util;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class GeolocationUtil {

	public static Location getlocation(Context context, LocationListener listener) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		boolean gpsEnabled = false;
		try {
			gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}

		boolean networkEnabled = false;
		try {
			networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		if (gpsEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0 /* minTime ms */, 
					0 /* minDistance in meters */,
					listener);
		}

		if (networkEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0 /* minTime ms */,
					0 /* minDistance in meters */, 
					listener);
		}

		List<String> providers = locationManager.getProviders(true);
		Location l = null;
		for (int i = 0; i < providers.size(); i++) {
			l = locationManager.getLastKnownLocation(providers.get(i));

			if (l != null)
				break;
		}

		return l;
	}
	
	public static float getDistance(Location loc1, Location loc2) {
		double pk = (double) (180 / 3.1415926543);
		double lat1 = loc1.getLatitude() / pk;
		double lon1 = loc1.getLongitude() / pk;
		double lat2 = loc2.getLatitude() / pk;
		double lon2 = loc2.getLongitude() / pk;
		double R = 6371000;
		double dLat = lat2 - lat1;
		double dLon = lon2 - lon1;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		float value = (float) d;
		return value;
	}
}
