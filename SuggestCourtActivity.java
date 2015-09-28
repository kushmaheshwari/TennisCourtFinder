package com.court.finder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bugsense.trace.BugSenseHandler;

public class SuggestCourtActivity extends SherlockActivity {
	private Button b;
	private TextView loc;
	private EditText edit;
	private CourtfinderApplication finder;
	private String add;
	private Double latitude;
	private Double longitude;

	// private String sloc;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendcourt);
		edit = (EditText) findViewById(R.id.location);
		// final LayoutParams lparams = new LayoutParams(50,50);
		// edit.setLayoutParams(lparams);
		b = (Button) findViewById(R.id.button1);
		loc = (TextView) findViewById(R.id.textView2);
		// sloc = getIntent().getStringExtra("Location");
		Location myLoc = ((CourtfinderApplication) getApplication())
				.getLocation();
		Geocoder geocoder = new Geocoder(this);
		List<Address> addresses = null;// new ArrayList<Address>();
		latitude = myLoc.getLatitude();
		longitude = myLoc.getLongitude();
		try {
			addresses = geocoder.getFromLocation(myLoc.getLatitude(),
					myLoc.getLongitude(), 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (addresses != null && addresses.size() != 0) {
			if (CourtfinderApplication.isDebug())
				Log.i("SIZE", Integer.toString(addresses.size()));
			String address = addresses.get(0).toString();
			String str1 = address.substring(address.indexOf("\"") + 1);
			String str2 = str1.substring(str1.indexOf("\"") + 1);
			str1 = str1.substring(0, str1.indexOf("\""));
			String str3 = str2.substring(str2.indexOf("\"") + 1);
			String str4 = str3.substring(0, str3.indexOf("\""));
			add = str1 + " " + str4;
		} else {
			add = "Unknown address";
		}
		// Log.i("STR1", str1);
		// Log.i("STR4", str4);

		// Log.i("MY LOCATION", addresses.get(0).toString());
		// Log.i("Parsed", add);
		String slat = Double.toString(myLoc.getLatitude());
		String slng = Double.toString(myLoc.getLongitude());
		loc.setText("Your Location: \n" + add);
		findViewById(R.id.main).requestFocus();
	}

	public void sendcourt(View view) {
		if (CourtfinderApplication.isDebug()) {
			Log.i("button", "pressed");
			Log.i("info", edit.getText().toString());
		}
		finder = (CourtfinderApplication) getApplication();
		SendBugSenseLog("New Court Location", "Address", add + ","
				+ edit.getText().toString(), "Lat/Lon",
				Double.toString(latitude) + "," + Double.toString(longitude),
				true);
		Toast.makeText(getApplicationContext(),
				"Your Location has been sent!!", Toast.LENGTH_LONG).show();
		finish();
		/*-
		try {
		    throw new RuntimeException("Sending the Court");
		} catch (Exception e){
		    Map<String, String> extraData = new HashMap<String,String>();
		    String theloc = add + " " + latitude + "," + longitude;
		    extraData.put("Court Information", theloc + " " + edit.getText().toString());
		//    extraData.put("56d238e5", edit.getText().toString());
		    // example: extraData.put("email", "demo@this_is_a_demo.com");
		    BugSenseHandler.log("Court_TAG", extraData, e);
		    Toast.makeText(getApplicationContext(), "Your Location has been sent!!", Toast.LENGTH_LONG).show();
		    finish();
		}*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.recmenu, menu);
		// configureActionItem(menu);
		return (super.onCreateOptionsMenu(menu));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.favoriteCourt) {
			// Toast.makeText(getApplicationContext(), "favoriteCourt",
			// Toast.LENGTH_SHORT).show();
			Intent a = new Intent(getApplicationContext(),
					FavoritesActivity.class);
			startActivity(a);
			finish();
			return (true);
		} else if (item.getItemId() == R.id.FindCourt) {
			Intent a = new Intent(getApplicationContext(),
					CourtfinderActivity.class);
			startActivity(a);
			finish();
			return (true);
		} else if (item.getItemId() == R.id.help) {
			// Toast.makeText(getApplicationContext(), "help",
			// Toast.LENGTH_SHORT)
			// .show();
			Intent c = new Intent(getApplicationContext(), HelpActivity.class);
			startActivity(c);
			finish();
			return (true);
		} else if (item.getItemId() == R.id.settings) {
			// Toast.makeText(getApplicationContext(), "settings",
			// Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getApplicationContext(),
					Preference.class);
			startActivity(intent);
			finish();
			return (true);
		}
		return (super.onOptionsItemSelected(item));
	}

	public static void SendBugSenseLog(String message, String key1,
			String value1, String key2, String value2,
			boolean onBackgroundThread) {
		try {
			throw new RuntimeException(message);
		} catch (final Exception e) {
			final HashMap<String, String> extraData = new HashMap<String, String>();
			extraData.put(key1, value1);
			extraData.put(key2, value2);
			if (!onBackgroundThread) {
				BugSenseHandler.clearCrashExtraData();
				BugSenseHandler.addCrashExtraMap(extraData);
				BugSenseHandler.sendException(e);
			} else {
				Handler handler = new Handler();
				handler.post(new Runnable() {
					public void run() {
						BugSenseHandler.clearCrashExtraData();
						BugSenseHandler.addCrashExtraMap(extraData);
						BugSenseHandler.sendException(e);
					}
				});
			}
		}
	}

}
