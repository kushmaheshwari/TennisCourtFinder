package com.court.finder;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LocationHelper {
	public static final int MESSAGE_CODE_LOCATION_FOUND = 1;
	public static final int MESSAGE_CODE_LOCATION_NULL = 2;
	public static final int MESSAGE_CODE_PROVIDER_NOT_PRESENT = 3;

	public static final int FIX_RECENT_BUFFER_TIME = 3000;

	private LocationManager locationMgr;
	private LocationListener locationListener;
	private Handler handler;
	private Runnable handlerCallback;
	private String providerName;
	private String logTag;

	public LocationHelper(LocationManager locationMgr, Handler handler,
			String logTag) {
		this.locationMgr = locationMgr;
		this.locationListener = new LocationListenerImp1();
		this.handler = handler;
		this.handlerCallback = new Thread() {
			public void run() {
				endListenForLocation(null);
			}
		};

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		this.providerName = locationMgr.getBestProvider(criteria, true);

		this.logTag = logTag;
	}

	public void getCurrentLocation(int durationSeconds) {
		if (this.providerName == null) {
			sendLocationToHandler(MESSAGE_CODE_PROVIDER_NOT_PRESENT, 0, 0);
			return;
		}

		Location lastKnown = locationMgr.getLastKnownLocation(providerName);
		if (lastKnown != null
				&& lastKnown.getTime() >= (System.currentTimeMillis() - FIX_RECENT_BUFFER_TIME)) {
			sendLocationToHandler(MESSAGE_CODE_LOCATION_FOUND,
					(int) (lastKnown.getLatitude() * 1e6),
					(int) (lastKnown.getLongitude() * 1e6));
			if (CourtfinderApplication.isDebug())
				Log.i("method", "lastknownloc");
		} else {
			listenForLocation(providerName, durationSeconds);
			if (CourtfinderApplication.isDebug())
				Log.i("method", "listenforloc");
		}

	}

	private void sendLocationToHandler(int msgId, int lat, int lon) {
		Message msg = Message.obtain(handler, msgId, lat, lon);
		handler.sendMessage(msg);
		if (CourtfinderApplication.isDebug())
			Log.i("method", "sendLocationToHandler");
	}

	private void listenForLocation(String providerName, int durationSeconds) {
		locationMgr
				.requestLocationUpdates(providerName, 0, 0, locationListener);
		handler.postDelayed(handlerCallback, durationSeconds * 1000);
		if (CourtfinderApplication.isDebug())
			Log.i("method", "listenxForLocation");

	}

	private void endListenForLocation(Location loc) {
		locationMgr.removeUpdates(locationListener);
		handler.removeCallbacks(handlerCallback);
		if (loc != null) {
			sendLocationToHandler(MESSAGE_CODE_LOCATION_FOUND,
					(int) (loc.getLatitude() * 1e6),
					(int) (loc.getLongitude() * 1e6));

		} else {
			sendLocationToHandler(MESSAGE_CODE_LOCATION_NULL, 0, 0);
		}
		if (CourtfinderApplication.isDebug())
			Log.i("method", "endListenForLocation");
	}

	private class LocationListenerImp1 implements LocationListener {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (CourtfinderApplication.isDebug())
				Log.d(logTag, "Location status changed to:" + status);
			switch (status) {
			case LocationProvider.AVAILABLE:
				if (CourtfinderApplication.isDebug())
					Log.i("method", "provider available");
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				if (CourtfinderApplication.isDebug())
					Log.i("method", "temp unavailable");
				break;
			case LocationProvider.OUT_OF_SERVICE:
				if (CourtfinderApplication.isDebug())
					Log.i("method", "out of service");
				endListenForLocation(null);
			}
			if (CourtfinderApplication.isDebug())
				Log.i("method", "onStatusChanged");
		}

		@Override
		public void onLocationChanged(Location loc) {
			if (loc == null) {
				return;
			}
			if (CourtfinderApplication.isDebug())
				Log.d(logTag, "Location changed to:" + loc.toString());
			endListenForLocation(loc);
			if (CourtfinderApplication.isDebug())
				Log.i("method", "onLocationChanged");

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			endListenForLocation(null);
			if (CourtfinderApplication.isDebug())
				Log.i("method", "onProviderDisabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			if (CourtfinderApplication.isDebug())
				Log.i("method", "onProviderEnabled");

		}

	}
}
