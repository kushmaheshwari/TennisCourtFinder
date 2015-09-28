package com.court.finder;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CourtDetailsActivity extends SherlockActivity {

	private CourtDatabase db;
	private Cursor courts;
	private Button b;
	private Button c;
	private String id;
	private static final int NAME = 1;
	private static final int COURTID = 2;
	private static final int LATITUDE = 3;
	private static final int LONGITUDE = 4;
	private static final int STREET = 5;
	private static final int CITY = 6;
	private static final int STATE = 7;
	private static final int ZIP = 8;
	private static final int TYPE = 9;
	private static final int ACCESS = 10;
	private static final int NUMCOURTS = 11;
	private static final int TIMING = 12;

	CourtfinderApplication finder;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		finder = (CourtfinderApplication) getApplication();

		Cursor cu = null;
		setContentView(R.layout.courtdetails);

		id = getIntent().getStringExtra("courid");
		TextView t = (TextView) findViewById(R.id.textView1);
		TextView s = (TextView) findViewById(R.id.textView2);
		TextView u = (TextView) findViewById(R.id.textView4);
		TextView w = (TextView) findViewById(R.id.textView6);
		TextView x = (TextView) findViewById(R.id.textView8);
		TextView y = (TextView) findViewById(R.id.textView12);
		TextView z = (TextView) findViewById(R.id.textView14);
		TextView h = (TextView) findViewById(R.id.textView16);
		b = (Button) findViewById(R.id.directions);
		c = (Button) findViewById(R.id.favorites);

		if (FavoritesDatabase.getInstance(getApplication()).inFavorites(id) == true) {
			c.setText("Remove from Favorites");
		}

		db = finder.getcdDatabase();
		;
		courts = db.getCourtDetails(Integer.parseInt(id));

		int numCols = courts.getColumnCount();
		String name = "";
		name += courts.getString(NAME);

		String adress = "";
		adress += courts.getString(STREET) + "\n";
		adress += courts.getString(CITY) + ", " + courts.getString(STATE) + " "
				+ courts.getString(ZIP);

		String numCourts = courts.getString(NUMCOURTS);
		numCourts = numCourts.substring(0, 2);

		int a = courts.getString(NUMCOURTS).indexOf("(");
		String light = "";
		if (courts.getString(NUMCOURTS).charAt(a + 1) == '0') {
			light += "No";
		} else {
			light += "Yes";
		}

		int b = courts.getString(NUMCOURTS).indexOf(",");
		String numIndoor = "";
		numIndoor += courts.getString(NUMCOURTS).charAt(b + 2);

		String location = courts.getString(TYPE);
		int e = location.indexOf(" ");
		if (e != -1) {
			location = location.substring(0, e);
		}
		String access = courts.getString(ACCESS);
		int f = access.indexOf(" ");
		if (f != -1) {
			access = access.substring(0, f);
		}
		String timing = courts.getString(TIMING);
		timing = timing.substring(5);

		/*
		 * details += "Courts: " + courts.getString(11) + "\n"; details +=
		 * "Location Type: " + courts.getString(9) + "\n"; details += "Access: "
		 * + courts.getString(10) + "\n"; details += "Surface: " +
		 * courts.getString(12) + "\n"; details += "Timings: " +
		 * courts.getString(13);
		 */

		if (CourtfinderApplication.isDebug())
			Log.i("COURTS", Integer.toString(courts.getCount()));
		if (CourtfinderApplication.isDebug())
			Log.i("name", name);
		t.setText(name);
		s.setText(adress);
		u.setText(numCourts);
		w.setText(light);
		x.setText(numIndoor);
		y.setText(location);
		z.setText(access);
		h.setText(timing);
		if (CourtfinderApplication.isDebug())
			Log.i("courtdetails", adress);

		// cu =
		// FavoritesDatabase.getInstance(getApplication()).findFavorite(id);
		// Log.i("detail", cu.getString(1));
		// if(cu.getCount() > 0){
		// c.setText("Remove from Favorites");
		// }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		courts.close();
		db.close();
		if (CourtfinderApplication.isDebug())
			Log.i("courtdetails", "onDestroy");
	}

	public void getDirections(View v) {
		if (CourtfinderApplication.isDebug())
			Log.i("button", "directions");
		String adress = (db.getAdress(courts));
		String str = adress.replaceAll(" ", "+");
		if (CourtfinderApplication.isDebug())
			Log.i("Addres", str);
		Intent x = new Intent(Intent.ACTION_VIEW,
				Uri.parse("google.navigation:q=+" + str));
		startActivity(x);

	}

	public void addToFavoritesDatabase(View v) {
		if (CourtfinderApplication.isDebug()) {
			Log.i("button", "database");
			Log.i("id", id);
		}

		if (c.getText().equals("Remove from Favorites")) {
			FavoritesDatabase.getInstance(getApplication()).removeFavorite(id);
			c.setText("Add to Favorites");
		} else {
			FavoritesDatabase.getInstance(getApplication()).addFavorite(id);
			c.setText("Remove from Favorites");
		}

	}

	// from
	// http://stackoverflow.com/questions/2077008/android-intent-for-twitter-application/9151983#9151983

	public void share(View v) {
		Intent share = new Intent(Intent.ACTION_SEND);
		List<Intent> targetedShareIntents = new ArrayList<Intent>();
		String name = "Sent by Tennis Court Finder";
		String link = "Free App at Google Play Store";
		share.setType("text/plain");
		String shareinfo = "\n\n\n" + courts.getString(NAME) + "\n"
				+ courts.getString(STREET) + "\n" + courts.getString(CITY)
				+ ", " + courts.getString(ZIP) + " " + courts.getString(STATE)
				+ "\n" + name + "\n" + link;
		boolean found = false;
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
				share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				Intent targetedShare = new Intent(
						android.content.Intent.ACTION_SEND);
				targetedShare.setType("text/plain"); // put here your mime type
				if (info.activityInfo.packageName.toLowerCase()
						.contains("mail")
						|| info.activityInfo.name.toLowerCase()
								.contains("mail")
						|| info.activityInfo.packageName.toLowerCase()
								.contains("face")
						|| info.activityInfo.name.toLowerCase()
								.contains("face")
						|| info.activityInfo.packageName.toLowerCase()
								.contains("mess")
						|| info.activityInfo.name.toLowerCase()
								.contains("mess")) {
					targetedShare
							.putExtra(
									android.content.Intent.EXTRA_SUBJECT,
									"Court Information about "
											+ courts.getString(NAME));
					targetedShare.putExtra(Intent.EXTRA_TEXT, shareinfo);
					targetedShare.setPackage(info.activityInfo.packageName);
					targetedShareIntents.add(targetedShare);
				}
			}

			Intent chooserIntent = Intent.createChooser(
					targetedShareIntents.remove(0), "Select app to share");
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					targetedShareIntents.toArray(new Parcelable[] {}));
			startActivity(chooserIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.detailsmenu, menu);
		// configureActionItem(menu);
		return (super.onCreateOptionsMenu(menu));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.FindCourt) {
			Intent a = new Intent(getApplicationContext(),
					CourtfinderActivity.class);
			startActivity(a);
			finish();
			return (true);
		} else if (item.getItemId() == R.id.favoriteCourt) {
			// Toast.makeText(getApplicationContext(), "favoriteCourt",
			// Toast.LENGTH_SHORT).show();
			Intent a = new Intent(getApplicationContext(),
					FavoritesActivity.class);
			startActivity(a);
			finish();
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
