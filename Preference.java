/*
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.court.finder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


public class Preference extends SherlockPreferenceActivity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.settingsmenu, menu);
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
		} else if (item.getItemId() == R.id.help) {
		//	Toast.makeText(getApplicationContext(), "help", Toast.LENGTH_SHORT)
		//	.show();
			Intent c = new Intent(getApplicationContext(), HelpActivity.class);
			startActivity(c);
			finish();
			return (true);
		} else if (item.getItemId() == R.id.FindCourt) {
			Intent a = new Intent(getApplicationContext(), CourtfinderActivity.class);
			 startActivity(a);
			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// setTheme(SampleList.THEME); // Used for theme switching in samples
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
	}
}
