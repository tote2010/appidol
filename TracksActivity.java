package com.itgapps.appidol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.itgapps.appidol.ListViewAdapters.TracksListViewAdapter;
import com.itgapps.appidol.db.DaoDbAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class TracksActivity extends ActionBarActivity {

	public SongsManager songsManager = new SongsManager();
	private Cursor cursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracks);

		// Ads

		//AdView adView = (AdView) findViewById(R.id.adView);
		//AdRequest adRequest = new AdRequest.Builder().build();
		//adView.loadAd(adRequest);

		Bundle extras = getIntent().getExtras();
		String albumId = extras.getString("albumId");
		final String request = extras.getString("request");

		DaoDbAdapter mDbHelper = new DaoDbAdapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();

		Cursor cursor = mDbHelper.getTracks(albumId);

		TracksListViewAdapter list = new TracksListViewAdapter(this, cursor);
		ListView listView = (ListView) findViewById(R.id.list2);
		listView.setAdapter(list);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> lv, View view, int position,
					long arg3) {

				TextView txtId = (TextView) view.findViewById(R.id.track_id);
				String trackId = txtId.getText().toString();
				if (request.equals("lyrics")) {
					lunchLyric(trackId);
				} else if (request.equals("songs")) {
					lunchSong(trackId);
					//addSong(trackId);
				}
				// Toast.makeText(getApplicationContext(), trackId,
				// Toast.LENGTH_SHORT).show();
			}
		});

	}

	public void addSong(String trackId) {
		// get Track from DB
		DaoDbAdapter mDbHelper = new DaoDbAdapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();
		
		// Add Track to SongsManager
		cursor = mDbHelper.getSong(trackId);
		if(cursor != null){ 
			cursor.moveToFirst();
			songsManager.addSong(cursor);
		}
		
		songsManager.addSong(cursor);
		cursor.close();
	}

	public void lunchLyric(String trackId) {
		Intent i = new Intent(this, LyricActivity.class);
		i.putExtra("trackId", trackId);
		startActivity(i);
	}

	public void lunchSong(String trackId) {
		/*
		 * DaoDbAdapter mDbHelper = new DaoDbAdapter(this);
		 * mDbHelper.createDatabase(); mDbHelper.open(); Cursor cursor =
		 * mDbHelper.getSong(trackId);
		 * 
		 * SongsManager songsManager = new SongsManager();
		 * songsManager.addSong(cursor); ArrayList<HashMap<String, String>>
		 * songsList = songsManager .getPlayList(); int songIndex = 0; String
		 * strAudioLink = songsList.get(songIndex).get("trackUrlSong");
		 * Toast.makeText(this, strAudioLink, Toast.LENGTH_SHORT).show();
		 */
		
		Intent i = new Intent(this, SongActivity.class);
		i.putExtra("trackId", trackId);
		startActivity(i);
		
	}

	/*
	 * public void lunchAlbum(String albumId){ Intent i = new Intent(this,
	 * TracksActivity.class); i.putExtra("_id", albumId); startActivity(i); }
	 */
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.tracks, menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        int id = item.getItemId();
	        if (id == R.id.action_tracks) {
	            //return true;
	        	//lunchSong();
	        }
	        return super.onOptionsItemSelected(item);
	    }
}
