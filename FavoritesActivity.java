package com.court.finder;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class FavoritesActivity extends SherlockListActivity {
	private Cursor courts;
	private CourtDatabase db;
	private FavoritesDatabase db2;
	ListAdapter adapter = null;
	ArrayList<String> courtsid = new ArrayList<String>();
	CourtfinderApplication finder;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setEmptyView(findViewById(R.layout.row));
		finder = (CourtfinderApplication) getApplication();
		db2 = finder.getfdDatabase();
		db = finder.getcdDatabase();
		LayoutInflater inflater = getLayoutInflater();
		View emptyView = inflater.inflate(R.layout.emptyview, null);
		addContentView(emptyView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		getListView().setEmptyView(emptyView);
		if (CourtfinderApplication.isDebug())
			Log.i("map", "onCreate");
	}

	public int getCount() {
		return adapter.getCount();
	}

	public ListAdapter getAdapter() {
		return adapter;
	}

	@Override
	protected void onResume() {
		super.onResume();

		courtsid = db2.getAllFavorites();
		adapter = null;
		if (courtsid.size() != 0) {
			if (CourtfinderApplication.isDebug())
				Log.i("if", "statement");
			for (int i = 0; i < courtsid.size(); i++) {
				if (CourtfinderApplication.isDebug()) {
					Log.i("for", "statement");
					Log.i("courtid", courtsid.get(i));
				}
			}
			courts = db.getCourtsbyID(courtsid);
			startManagingCursor(courts);
			adapter = new FindCourtAdapter(this, courts);
		}
		getListView().setAdapter(adapter);
		if (CourtfinderApplication.isDebug())
			Log.i("map6", "onResume");

	}

	public class FindCourtAdapter extends CursorAdapter {

		public FindCourtAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View row, Context context, Cursor cursor) {
			CourtDetails details = (CourtDetails) row.getTag();
			details.populate(cursor, db);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			CourtDetails details = new CourtDetails(row);
			row.setTag(details);
			return row;
		}
	}

	public class CourtDetails {

		TextView name = null;
		TextView dist = null;
		TextView adress = null;

		public CourtDetails(View v) {
			name = (TextView) v.findViewById(R.id.name);
			dist = (TextView) v.findViewById(R.id.dist);
			adress = (TextView) v.findViewById(R.id.adress);
		}

		public void populate(Cursor c, CourtDatabase db) {
			name.setText(db.getName(c));
			dist.setText("");
			adress.setText(db.getAdress(c));
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Log.i("position", Integer.toString(position));
		courts.moveToPosition(position);
		// Log.i("2", courts.getString(1));
		if (courts.getString(1).equals("")) {
			if (CourtfinderApplication.isDebug())
				Log.i("return", "empty");
			return;
		}
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(getApplicationContext(),
				CourtDetailsActivity.class);
		courts.moveToPosition(position);
		i.putExtra("courid", Integer.toString(courts.getInt(2)));

		startActivity(i);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.favsmenu, menu);
		// configureActionItem(menu);
		return (super.onCreateOptionsMenu(menu));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.FindCourt) {
			Intent a = new Intent(getApplicationContext(),
					CourtfinderActivity.class);
			startActivity(a);
			return (true);
		} else if (item.getItemId() == R.id.recommendCourt) {
			// Toast.makeText(getApplicationContext(), "recommendCourt",
			// Toast.LENGTH_SHORT).show();
			Intent b = new Intent(getApplicationContext(),
					SuggestCourtActivity.class);
			startActivity(b);
			return (true);
		} else if (item.getItemId() == R.id.help) {
			// Toast.makeText(getApplicationContext(), "help",
			// Toast.LENGTH_SHORT)
			// .show();
			Intent c = new Intent(getApplicationContext(), HelpActivity.class);
			startActivity(c);
			return (true);
		} else if (item.getItemId() == R.id.settings) {
			// Toast.makeText(getApplicationContext(), "settings",
			// Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getApplicationContext(),
					Preference.class);
			startActivity(intent);
			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}

}
