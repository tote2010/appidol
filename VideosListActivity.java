package com.itgapps.appidol;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.itgapps.appidol.ListViewAdapters.VideosListViewAdapter;
import com.itgapps.appidol.db.DaoDbAdapter;

public class VideosListActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
    
        //Ads
        
        //AdView adView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //adView.loadAd(adRequest);
        
        //Bundle extras = getIntent().getExtras();
        //final String request = extras.getString("request");
           
        
		DaoDbAdapter mDbHelper = new DaoDbAdapter(this);
		mDbHelper.createDatabase();       
		mDbHelper.open(); 
		 
		Cursor cursor = mDbHelper.getVideos(); 
		
		VideosListViewAdapter list = new VideosListViewAdapter(this, cursor);
		ListView listView = (ListView)findViewById(R.id.listVideos);
		listView.setAdapter(list);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> lv, View view, int position, long arg3) {
								
				TextView txtUrlVideo = (TextView)view.findViewById(R.id.album_id);
                String urlVideo = txtUrlVideo.getText().toString();
                
                lunchVideo(urlVideo);	
               	
				//Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
			}
		});
	
	}
	
	public void lunchVideo(String urlVideo){
    	Intent i = new Intent(this, VideoActivity.class);
    	i.putExtra("urlVideo", urlVideo);
    	startActivity(i);
    }

}
