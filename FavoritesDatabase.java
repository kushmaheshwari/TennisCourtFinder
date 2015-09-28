package com.court.finder;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class FavoritesDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "favorites";
	private static final int DATABASE_VERSION = 1;
	private ContentValues cv;
	private static SQLiteDatabase favorites;
	private static FavoritesDatabase singleton = null;
	private Context ctxt;

	synchronized static FavoritesDatabase getInstance(Context ctxt) {
		if (singleton == null) {
			singleton = new FavoritesDatabase(ctxt.getApplicationContext());
			if (CourtfinderApplication.isDebug())
				Log.i("favorites", "getInstance");
		}

		return singleton;
	}

	private FavoritesDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		if (CourtfinderApplication.isDebug())
			Log.i("favorites", "constructor");
		ctxt = context;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// favorites = db;
		try {
			db.beginTransaction();
			db.execSQL("CREATE TABLE favorites (_id INTEGER PRIMARY KEY AUTOINCREMENT, courid TEXT)");
			if (CourtfinderApplication.isDebug())
				Log.i("favorites", "created new database");

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}

	public void addFavorite(String courid) {
		try {
			getWritableDatabase().beginTransaction();
			ContentValues cv = new ContentValues();
			cv.put("courid", courid);
			getWritableDatabase().insert("favorites", null, cv);
			getWritableDatabase().setTransactionSuccessful();
			getAllFavorites(); // added aug 11
		} finally {
			getWritableDatabase().endTransaction();
		}

	}

	public void removeFavorite(String courid) {
		// Cursor c = findFavorite(courid);
		// getWritableDatabase().delete("favorites", "_id=? AND courid=?" , new
		// String[] {Integer.toString(c.getInt(0)), courid});
		getWritableDatabase().beginTransaction();
		getWritableDatabase().delete("favorites", "courid=" + courid, null);
		getWritableDatabase().setTransactionSuccessful();
		getWritableDatabase().endTransaction();
	}

	public Cursor findFavorite(String id) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String[] sqlSelect = { "0 _id", "courid" };
		String sqlTables = "favorites";
		String args = "courid='" + id + "'";
		qb.setTables(sqlTables);
		c = qb.query(db, sqlSelect, args, null, null, null, null);

		c.moveToFirst();
		// Log.i("find", c.getString(1));
		return c;

	}

	public boolean inFavorites(String id) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String[] sqlSelect = { "0 _id", "courid" };
		String sqlTables = "favorites";
		String args = "courid='" + id + "'";
		qb.setTables(sqlTables);
		c = qb.query(db, sqlSelect, args, null, null, null, null);
		c.moveToFirst();
		if (c.getCount() == 0) {
			return false;
		} else {
			return true;
		}

	}

	public ArrayList<String> getAllFavorites() {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String[] sqlSelect = { "0 _id", "courid" };
		String sqlTables = "favorites";

		qb.setTables(sqlTables);
		c = qb.query(db, sqlSelect, null, null, null, null, null);

		c.moveToFirst();

		ArrayList<String> id = new ArrayList<String>();

		for (int i = 0; i < c.getCount(); i++) {
			String courtid = c.getString(1);
			id.add(courtid);
			c.moveToNext();
		}
		if (!c.isClosed())
			c.close();
		return id;

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
