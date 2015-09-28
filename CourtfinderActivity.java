package com.court.finder;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CourtfinderActivity extends SherlockListActivity implements
		TextView.OnEditorActionListener {

	private Cursor courts;
	private CourtDatabase db;
	private LocationManager lm;
	// Timer timer1;
	private long lastTime = 0;
	ListAdapter adapter = null;
	Location myLoc = new Location("");
	ArrayList<Float> dists = new ArrayList<Float>();
	private SharedPreferences prefs = null;
	private int distance;
	private String sort = "distance";
	private boolean privateCourts;
	private Handler handler;
	private String providerName;
	private String search;
	SharedPreferences.Editor editor;
	LocationHelper locationHelper;
	EditText add;
	CourtfinderApplication finder;
	boolean testMode = false;
	int testDistance = 2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.actions, menu);
		// configureActionItem(menu);
		/*
		 * MenuItem searchmenu = (MenuItem)findViewById(R.id.search);
		 * searchmenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
		 * MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		 * searchmenu.setActionView(R.layout.collapsible_edittext);
		 */
		configureSearchItem(menu);
		/*
		 * SubMenu subMenu1 = (SubMenu) menu.addSubMenu("Sort");
		 * subMenu1.add("Sort By Distance"); subMenu1.add("Sort By Name");
		 * subMenu1.add("Sort By City"); MenuItem sort = (MenuItem)
		 * subMenu1.getItem();
		 * sort.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
		 * MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		 */

		return (super.onCreateOptionsMenu(menu));

	}

	public boolean onSearchRequested() {
		setTitle(R.string.tsearch);

		add.setOnEditorActionListener(this);
		// Toast.makeText(getApplicationContext(), "Clicked search button",
		// Toast.LENGTH_LONG).show();
		return true;
	}

	public void configureSearchItem(Menu menu) {
		add = (EditText) menu.findItem(R.id.search).getActionView()
				.findViewById(R.id.search);
		add.setOnEditorActionListener(this);
	}

	public EditText getEditText() {
		return add;
	}

	public void setTestMode(boolean flag, int distance) {
		testMode = flag;
		testDistance = distance;
	}

	public ListAdapter getListAdapter() {
		return adapter;
	}

	public int getCount() {
		return adapter.getCount();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// setContentView(R.layout.get_location);
		finder = (CourtfinderApplication) getApplication();
		// BugSenseHandler.setup(this,finder.getKey());
		db = finder.getcdDatabase();
		refresh1();
		locationHelper = new LocationHelper(lm, handler, "DataBaseActivityx");
		getListView().setEmptyView(findViewById(R.layout.row));

		LayoutInflater inflater = getLayoutInflater();
		View emptyView = inflater.inflate(R.layout.emptyview1, null);
		addContentView(emptyView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		getListView().setEmptyView(emptyView);
		if (CourtfinderApplication.isDebug())
			Log.i("map", "onCreate");
		try { // force overflow menu
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (CourtfinderApplication.isDebug())
			Log.i("map", "onResume");
		refresh(false);
	}

	public void refresh(boolean mock) {
		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (mock) {
			setMockLocation(37.9771, -122.5676, 500);
		}
		prefs = getSharedPreferences("com.court.finder_preferences",
				Activity.MODE_PRIVATE);
		editor = prefs.edit();

		// Log.i("onResume", sort);
		if (!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			callGPS();
		} else {
			locationHelper.getCurrentLocation(30);
		}
		// Log.i("onResume", distance);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		this.providerName = lm.getBestProvider(criteria, true);
		if (providerName != null) {

			Location lastKnown = lm.getLastKnownLocation(providerName);
			if (myLoc != null && lastKnown != null) {
				myLoc.setLatitude(lastKnown.getLatitude());
				myLoc.setLongitude(lastKnown.getLongitude());
			}
			// save in prefs
		} else if (false) {
			// look at prefs
		} else {
			myLoc.setLatitude(37.3041);
			myLoc.setLongitude(-121.8727);

		}
		((CourtfinderApplication) getApplication()).setLocation(myLoc);
		update(myLoc);

	}

	private void setMockLocation(double latitude, double longitude,
			float accuracy) {
		lm.addTestProvider(LocationManager.GPS_PROVIDER,
				"requiresNetwork" == "", "requiresSatellite" == "",
				"requiresCell" == "", "hasMonetaryCost" == "",
				"supportsAltitude" == "", "supportsSpeed" == "",
				"supportsBearing" == "", android.location.Criteria.POWER_LOW,
				android.location.Criteria.ACCURACY_FINE);
		Location newLocation = new Location(LocationManager.GPS_PROVIDER);
		newLocation.setLatitude(latitude);
		newLocation.setLongitude(longitude);
		newLocation.setAccuracy(accuracy);
		lm.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
		lm.setTestProviderStatus(LocationManager.GPS_PROVIDER,
				LocationProvider.AVAILABLE, null, System.currentTimeMillis());
		lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);
	}

	protected void callGPS() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("GPS is not enabled")
				.setMessage(
						"Would you like to go to the location settings and enable GPS?")
				.setCancelable(true)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startActivity(new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Toast.makeText(getApplicationContext(),
								"This App needs GPS to update court list.",
								Toast.LENGTH_LONG).show();
						finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
		if (CourtfinderApplication.isDebug())
			Log.w("Call", "GPS");
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
			String newname = db.getName(c);
			if (db.getName(c).length() > 24) {
				newname = newname.substring(0, 24);
				newname += "...";
			}
			adress.setText(db.getAdressFinder(c));
			name.setText(newname);
			if (db.getName(c).length() != 0) {
				dist.setText(db.getDistanceTo(c, myLoc) + " mi");
			} else {
				dist.setText("");
			}

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		courts.close();
		db.close();
		if (CourtfinderApplication.isDebug())
			Log.i("map", "onDestroy");
	}

	protected void onPause() {
		super.onPause();
		if (CourtfinderApplication.isDebug())
			Log.i("map", "onPause");

		// if (!courts.isClosed())
		// courts.close();
	}

	@SuppressWarnings("deprecation")
	public void update(Location loc) {
		distance = Integer.parseInt(prefs.getString("sort_miles", "2"));
		if (CourtfinderApplication.isDebug())
			Log.i("distance by", Integer.toString(distance));
		sort = prefs.getString("sort_courts", "distance");
		privateCourts = prefs.getBoolean("private_courts", false);

		if (!testMode) {
			if (CourtfinderApplication.isDebug())
				Log.i("distance by", Integer.toString(distance));
			courts = db.getCourtsByDistance(loc, distance, sort, dists,
					privateCourts);
		} else {
			if (CourtfinderApplication.isDebug())
				Log.i("test distance by", Integer.toString(testDistance));
			courts = db.getCourtsByDistance(loc, testDistance, sort, dists,
					privateCourts);
		}
		startManagingCursor(courts);
		adapter = new FindCourtAdapter(this, courts);
		getListView().setAdapter(adapter);

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (event == null || event.getAction() == KeyEvent.ACTION_UP) {
			// adapter.add(v.getText().toString());
			// Toast.makeText(getApplicationContext(), v.getText().toString(),
			// Toast.LENGTH_SHORT).show();
			search = v.getText().toString();

			courts = db.getCourtsByName(search);
			adapter = new FindCourtAdapter(this, courts);
			getListView().setAdapter(adapter);

			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.search) {
			setTitle(R.string.tsearch);
			// Toast.makeText(getApplicationContext(), "search",
			// Toast.LENGTH_SHORT).show();
			return (true);
		} else if (item.getItemId() == R.id.refresh1) {
			refresh(false);
			// Toast.makeText(getApplicationContext(), "Refreshing",
			// Toast.LENGTH_SHORT).show();
			return (true);
		} else if (item.getItemId() == R.id.sortByDistance1) {
			setTitle(R.string.dist);
			editor.putString("sort_courts", "distance");
			editor.commit();
			update(myLoc);
			// Toast.makeText(getApplicationContext(), "sortbyDistance",
			// Toast.LENGTH_SHORT).show();
			return (true);
		} else if (item.getItemId() == R.id.sortByAlphabet1) {
			setTitle(R.string.name);
			editor.putString("sort_courts", "name");
			editor.commit();
			update(myLoc);
			// Toast.makeText(getApplicationContext(), "sortbyName",
			// Toast.LENGTH_SHORT).show();
			return (true);
		} else if (item.getItemId() == R.id.sortByCity1) {
			setTitle(R.string.city);
			editor.putString("sort_courts", "city");
			editor.commit();
			update(myLoc);
			// Toast.makeText(getApplicationContext(), "sortbyCity",
			// Toast.LENGTH_SHORT).show();
			return (true);
		} else if (item.getItemId() == R.id.Refresh) {
			refresh(false);
			add.setText("");
			setTitle(R.string.app_name);
			return true;
		} else if (item.getItemId() == R.id.favoriteCourt) {
			// Toast.makeText(getApplicationContext(), "favoriteCourt",
			// Toast.LENGTH_SHORT).show();
			Intent a = new Intent(getApplicationContext(),
					FavoritesActivity.class);
			startActivity(a);
			return (true);
		} else if (item.getItemId() == R.id.recommendCourt) {
			// Toast.makeText(getApplicationContext(), "recommendCourt",
			// Toast.LENGTH_SHORT).show();
			Intent b = new Intent(getApplicationContext(),
					SuggestCourtActivity.class);
			String slat = Double.toString(myLoc.getLatitude());
			String slng = Double.toString(myLoc.getLongitude());
			b.putExtra("Location", slat + "," + slng);
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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Log.i("click courts cnt", Integer.toString(courts.getCount()));
		// Log.i("position", Integer.toString(position));
		// Log.i("click courts num column",
		// Integer.toString(courts.getColumnCount()));
		courts.moveToPosition(position);
		if (courts.getString(1).equals("")) {
			if (CourtfinderApplication.isDebug())
				Log.i("return", "empty");
			return;
		}
		Intent i = new Intent(getApplicationContext(),
				CourtDetailsActivity.class);
		courts.moveToPosition(position);
		i.putExtra("courid", Integer.toString(courts.getInt(2)));

		startActivity(i);
		// finish();

	}

	public void refresh1() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		handler = new Handler() {
			public void handleMessage(Message m) {
				if (m.what == LocationHelper.MESSAGE_CODE_LOCATION_FOUND) {
					if (CourtfinderApplication.isDebug())
						Log.i("statement", "Handler RETURNED\nlat: " + m.arg1
								+ "\nlon: " + m.arg2);
					myLoc.setLatitude(m.arg1 / 1e6);
					myLoc.setLongitude(m.arg2 / 1e6);
					update(myLoc);// use found loc
					// put toast
					// update prefs
				} else if (m.what == LocationHelper.MESSAGE_CODE_LOCATION_NULL) {
					if (CourtfinderApplication.isDebug())
						Log.i("statement",
								"Handler RETURNED\nunable to get location");
				} else if (m.what == LocationHelper.MESSAGE_CODE_PROVIDER_NOT_PRESENT) {
					if (CourtfinderApplication.isDebug())
						Log.i("statement",
								"Handler RETURNED\nprovider not present");
				}
			}
		};
	}

}