package com.court.finder;

import android.app.Application;
import android.location.Location;
import android.os.StrictMode;

import com.bugsense.trace.BugSenseHandler;

public class CourtfinderApplication extends Application {
	private static boolean debug = false;
	private static CourtfinderApplication sInstance;
	private Location myLoc;
	CourtDatabase cd = null;
	FavoritesDatabase fd = null;
	private static String bugsenseKey = "176fe8aa";

	public static CourtfinderApplication getInstance() {
		return sInstance;
	}

	public String getKey() {
		return bugsenseKey;
	}

	public Location getMyLocation() {
		return myLoc;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (false) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() // or
																			// .detectAll()
																			// for
																			// all
																			// detectable
																			// problems
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().build());
		}

		sInstance = this;
		// sInstance.initializeInstance();
		BugSenseHandler.initAndStartSession(getApplicationContext(),
				bugsenseKey);
	}

	// protected void initializeInstance() {

	// }

	public void setLocation(Location loc) {
		myLoc = loc;
	}

	public Location getLocation() {
		return myLoc;
	}

	public FavoritesDatabase getfdDatabase() {
		return FavoritesDatabase.getInstance(this);

	}

	public CourtDatabase getcdDatabase() {
		return CourtDatabase.getInstance(this);
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		CourtfinderApplication.debug = debug;
	}

}
