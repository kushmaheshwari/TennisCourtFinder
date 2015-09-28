package com.court.finder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class HelpActivity extends SherlockActivity {
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.helpmenu, menu);
		// configureActionItem(menu);
		return (super.onCreateOptionsMenu(menu));

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 if (item.getItemId() == R.id.favoriteCourt) {
		//	Toast.makeText(getApplicationContext(), "favoriteCourt",
		//			Toast.LENGTH_SHORT).show();
			Intent a = new Intent(getApplicationContext(), FavoritesActivity.class);  
			startActivity(a);
			finish();
			return (true);
		} else if (item.getItemId() == R.id.recommendCourt) {
		//	Toast.makeText(getApplicationContext(), "recommendCourt",
		//			Toast.LENGTH_SHORT).show();
			Intent b = new Intent(getApplicationContext(), SuggestCourtActivity.class);
			startActivity(b);
			finish();
			return (true);
		} else if (item.getItemId() == R.id.FindCourt) {
			Intent a = new Intent(getApplicationContext(), CourtfinderActivity.class);
			 startActivity(a);
			 finish();
			return (true);
		} else if (item.getItemId() == R.id.settings) {
		//	 Toast.makeText(getApplicationContext(), "settings",
		//	Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getApplicationContext(),
					Preference.class);
			startActivity(intent);
			finish();
			return (true);
		}
		

		return (super.onOptionsItemSelected(item));
	}

}
