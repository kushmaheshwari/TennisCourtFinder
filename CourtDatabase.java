package com.court.finder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.location.Location;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class CourtDatabase extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "courts";
	private static final int DATABASE_VERSION = 1;
	private static final int NAME = 1;
	private static final int COURTID = 2;

	private static final int LATITUDE = 3;
	private static final int LONGITUDE = 4;
	private static final int STREET = 5;
	private static final int CITY = 6;
	private static final int STATE = 7;
	private static final int ZIP = 8;

	private static CourtDatabase singleton = null;

	private CourtDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	synchronized static CourtDatabase getInstance(Context ctxt) {
		if (singleton == null) {
			singleton = new CourtDatabase(ctxt.getApplicationContext());
			Log.i("favorites", "getInstance");
		}

		return singleton;
	}

	public Cursor getCourtsByDistance(Location loc, int miles, String order,
			ArrayList<Float> dist, boolean courts) {
		Cursor c = null;

		if (loc != null) {

			double lat = loc.getLatitude();
			double lng = loc.getLongitude();
			double latdegree = miles * (1 / 69.0);
			double lngdegree = miles * (1 / 69.172);
			double westEdge = lat - latdegree;
			double eastEdge = lat + latdegree;
			double northEdge = lng + lngdegree;
			double southEdge = lng - lngdegree;

			SQLiteDatabase db = getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

			String[] sqlSelect = { "0 _id", "name", "courid", "lat", "lng",
					"street", "city", "state", "zip" };
			String[] sqlSelect2 = { "_id", "name", "courid", "lat", "lng",
					"street", "city", "state", "zip" };

			String sqlTables = "courts";

			String args = "lat " + "BETWEEN '" + westEdge + "' AND '"
					+ eastEdge + "' AND " + "lng " + "BETWEEN '" + northEdge
					+ "' AND '" + southEdge + "'";

			if (courts == false) {
				args += " AND " + "access == 0";
			}

			if (CourtfinderApplication.isDebug()) {
				Log.i("order", order);
				Log.i("courts", args);
			}

			if (order.equals("distance")) {
				order = null;
			}
			qb.setTables(sqlTables);
			c = qb.query(db, sqlSelect, args, null, null, null, order);

			c.moveToFirst();

			MatrixCursor menuCursor = new MatrixCursor(sqlSelect2, c.getCount());

			class CourtDistance {
				int index;
				double distance;

				public CourtDistance(Cursor c, Location myLoc) {
					Location loc = new Location("loc");
					loc.setLatitude(c.getDouble(LATITUDE));
					loc.setLongitude(c.getDouble(LONGITUDE));
					Float f = new Float((myLoc.distanceTo(loc) / (1600)));
					distance = (double) Math.round(f * 10) / 10.0;
					index = c.getPosition();

				}
			}

			ArrayList<CourtDistance> dists = new ArrayList<CourtDistance>();
			for (int i = 0; i < c.getCount(); i++) {
				CourtDistance distance = new CourtDistance(c, loc);
				c.moveToNext();
				dists.add(distance);
			}
			class CustomComparator implements Comparator<CourtDistance> {
				@Override
				public int compare(CourtDistance o1, CourtDistance o2) {
					if (o1.distance > o2.distance)
						return 1;
					else
						return -1;
				}
			}

			if (order == null) {// so it sorts by distance
				Collections.sort(dists, new CustomComparator());
			}
			c.moveToFirst();
			for (CourtDistance cd : dists) {
				// Log.i("distance", c.getString(1) + " "
				// +Double.toString(cd.distance) + " " +
				// Integer.toString(cd.index));
				c.moveToPosition(cd.index);
				menuCursor.addRow(new Object[] { c.getString(0),
						c.getString(NAME), c.getString(COURTID),
						c.getString(LATITUDE), c.getString(LONGITUDE),
						c.getString(STREET), c.getString(CITY),
						c.getString(STATE), c.getString(ZIP) });
			}

			menuCursor.moveToFirst();
			int size;
			if (menuCursor.getCount() > 0 && menuCursor.getCount() < 9) {
				size = 9;
			} else if (menuCursor.getCount() >= 9) {
				return menuCursor;
			} else {
				return menuCursor;
			}
			int j = menuCursor.getCount();
			/*
			 * for(j = 0; j < menuCursor.getCount(); j++){ //copy
			 * menuCursor.addRow(new Object[] { menuCursor.getString(0),
			 * menuCursor.getString(1), menuCursor.getString(2),
			 * menuCursor.getString(3), menuCursor.getString(4),
			 * menuCursor.getString(5), menuCursor.getString(6),
			 * menuCursor.getString(7), menuCursor.getString(8)});
			 * menuCursor.moveToNext(); //Log.i("id matrix", c.getString(0));
			 */
			// }
			for (; j < size; j++) {
				// add null values
				menuCursor.addRow(new Object[] { "0", "", "", "", "", "", "",
						"", "" });
			}
			menuCursor.moveToFirst();
			if (!c.isClosed())
				c.close();
			db.close();
			return menuCursor;

		}

		return c;

	}

	public Cursor getAllCourts(ArrayList<String> courtids) {
		Cursor c = null;

		return c;
	}

	public Cursor getCourtsbyID(ArrayList<String> courtids) {
		Cursor c = null;

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String[] sqlSelect = { "0 _id", "name", "courid", "street", "city",
				"state", "zip" };
		String[] sqlSelect2 = { "_id", "name", "courid", "street", "city",
				"state", "zip" };

		String sqlTables = "courts";

		String args = new String();

		for (int i = 0; i < courtids.size(); i++) {
			if (i == 0) {
				args += "courid=" + courtids.get(i);
			} else {
				args += " OR courid=" + courtids.get(i);
			}

		}

		qb.setTables(sqlTables);
		c = qb.query(db, sqlSelect, args, null, null, null, null);

		c.moveToFirst();
		int size;
		if (c.getCount() > 0 && c.getCount() < 9) {
			size = 9;
		} else if (c.getCount() >= 9) {
			size = c.getCount();
		} else {
			return c;
		}
		// size = c.getCount();
		MatrixCursor menuCursor = new MatrixCursor(sqlSelect2, size);
		menuCursor.moveToFirst();
		if (CourtfinderApplication.isDebug())
			Log.i("size", Integer.toString(size));
		int j = 0;
		for (j = 0; j < c.getCount(); j++) {
			// copy
			menuCursor.addRow(new Object[] { c.getString(0), c.getString(NAME),
					c.getString(COURTID), c.getString(3), c.getString(4),
					c.getString(5), c.getString(6) });
			c.moveToNext();
			// Log.i("id matrix", c.getString(0));
		}
		for (; j < size; j++) {
			// add null values
			menuCursor.addRow(new Object[] { "0", "", "", "", "", "", "" });
		}
		menuCursor.moveToFirst();
		if (!c.isClosed())
			c.close();
		return menuCursor;
	}

	public Cursor getCourtsByName(String search) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String[] sqlSelect = { "0 _id", "name", "courid", "lat", "lng",
				"street", "city", "state", "zip" };
		String[] sqlSelect2 = { "_id", "name", "courid", "lat", "lng",
				"street", "city", "state", "zip" };
		String sqlTables = "courts";

		String args = "name LIKE '%" + search + "%' OR street LIKE '%" + search
				+ "%' OR city LIKE '%" + search + "%' OR zip LIKE '" + search
				+ "'";

		qb.setTables(sqlTables);
		c = qb.query(db, sqlSelect, args, null, null, null, null);

		c.moveToFirst();
		int size;
		if (c.getCount() > 0 && c.getCount() < 9) {
			size = 9;
		} else if (c.getCount() >= 9) {
			return c;
		} else {
			return c;
		}
		MatrixCursor menuCursor = new MatrixCursor(sqlSelect2, size);
		menuCursor.moveToFirst();
		int j = 0;
		for (j = 0; j < c.getCount(); j++) {
			// copy
			menuCursor.addRow(new Object[] { c.getString(0), c.getString(NAME),
					c.getString(COURTID), c.getString(LATITUDE),
					c.getString(LONGITUDE), c.getString(STREET),
					c.getString(CITY), c.getString(STATE), c.getString(ZIP) });
			c.moveToNext();
			// Log.i("id matrix", c.getString(0));
		}
		for (; j < size; j++) {
			// add null values
			menuCursor.addRow(new Object[] { "0", "", "", "", "", "", "", "",
					"" });
		}
		menuCursor.moveToFirst();
		if (!c.isClosed())
			c.close();
		return menuCursor;

	}

	public String getAdressFinder(Cursor c) {
		String str = "";
		str += c.getString(STREET) + " " + c.getString(CITY) + " "
				+ c.getString(ZIP);
		return str;
	}

	public String getAdress(Cursor c) {
		String str = "";
		str += c.getString(3) + " " + c.getString(4);// + " " + c.getString(6);
		return str;
	}

	public String getName(Cursor c) {
		return c.getString(NAME);
	}

	public Float getLatitude(Cursor c) {
		return c.getFloat(LATITUDE);
	}

	public Float getLongitude(Cursor c) {
		return c.getFloat(LONGITUDE);
	}

	public String getDistanceTo(Cursor c, Location l) {
		Location loc = new Location("loc");
		loc.setLatitude(c.getDouble(LATITUDE));
		loc.setLongitude(c.getDouble(LONGITUDE));
		Float f = new Float((l.distanceTo(loc) / (1600)));
		Double d = (double) Math.round(f * 10) / 10.0;

		return d.toString();
		// Log.i("dist", Float.toString(f));

	}

	public Cursor getCourtDetails(int id) {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String[] sqlSelect = { "0 _id", "name", "courid", "lat", "lng",
				"street", "city", "state", "zip", "type", "accesstype",
				"numcourts", "time" };
		String sqlTables = "courts";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, "courid=" + id, null, null, null,
				null);
		c.moveToFirst();
		return c;

	}

}
