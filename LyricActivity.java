package com.itgapps.appidol;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.itgapps.appidol.ListViewAdapters.AlbumsListViewAdapter;
import com.itgapps.appidol.db.DaoDbAdapter;

public class LyricActivity extends Activity{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric);
    
        //Ads
        /*
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        */
        
        Bundle extras = getIntent().getExtras();
        String trackId = extras.getString("trackId");
        
		DaoDbAdapter mDbHelper = new DaoDbAdapter(this);
		mDbHelper.createDatabase();       
		mDbHelper.open(); 
		
		
		Cursor cursor = mDbHelper.getLyric(trackId); 
		String txtTrackName = cursor.getString(cursor.getColumnIndex("name"));
		String txtLyric = cursor.getString(cursor.getColumnIndex("en"));
		
		TextView tvTrackName = (TextView)findViewById(R.id.trackName);
		TextView tvLyric = (TextView)findViewById(R.id.lyricsEn);
		tvTrackName.setText(txtTrackName);
		tvLyric.setText(txtLyric);
		
		/*
		AlbumsListViewAdapter list = new AlbumsListViewAdapter(this, cursor);
		ListView listView = (ListView)findViewById(R.id.list);
		listView.setAdapter(list);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> lv, View view, int position, long arg3) {
								
				TextView txtId = (TextView)view.findViewById(R.id.album_id);
                String id = txtId.getText().toString();
				lunchAlbum(id);
				//Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
			}
		});
		*/
	
	}
	
	

}
