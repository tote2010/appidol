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
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.itgapps.appidol.ListViewAdapters.LyricsListViewAdapter;
import com.itgapps.appidol.db.DaoDbAdapter;

public class LyricsListActivity extends Activity{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);
    
        //Ads
        
        //AdView adView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //adView.loadAd(adRequest);
                 
        
		DaoDbAdapter mDbHelper = new DaoDbAdapter(this);
		mDbHelper.createDatabase();       
		mDbHelper.open(); 
		 
		Cursor cursor = mDbHelper.getAllLyrics(); 
		
		LyricsListViewAdapter list = new LyricsListViewAdapter(this, cursor);
		ListView listView = (ListView)findViewById(R.id.listViewAllLyrics);
		listView.setAdapter(list);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> lv, View view, int position, long arg3) {
								
				TextView txtId = (TextView)view.findViewById(R.id.lyric_id);
                String id = txtId.getText().toString();
                
                lunchLyric(id);
				//Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
			}
		});
	
	}
	
	public void lunchLyric(String id){
    	Intent i = new Intent(this, LyricActivity.class);
    	i.putExtra("trackId", id);
    	startActivity(i);
    }


}
