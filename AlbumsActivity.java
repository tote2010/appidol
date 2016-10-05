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
import com.itgapps.appidol.ListViewAdapters.AlbumsListViewAdapter;
import com.itgapps.appidol.db.DaoDbAdapter;

public class AlbumsActivity extends Activity{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
    
        //Ads
        
        //AdView adView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //adView.loadAd(adRequest);
        
        Bundle extras = getIntent().getExtras();
        final String request = extras.getString("request");
           
        
		DaoDbAdapter mDbHelper = new DaoDbAdapter(this);
		mDbHelper.createDatabase();       
		mDbHelper.open(); 
		 
		Cursor cursor = mDbHelper.getAlbums(); 
		
		AlbumsListViewAdapter list = new AlbumsListViewAdapter(this, cursor);
		ListView listView = (ListView)findViewById(R.id.list);
		listView.setAdapter(list);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> lv, View view, int position, long arg3) {
								
				TextView txtId = (TextView)view.findViewById(R.id.album_id);
                String id = txtId.getText().toString();
                
                if(request.equals("lyrics")){
                	lunchAlbum(id);
                } else if(request.equals("songs")){
                	lunchSongs(id);	
               	}
				//Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
			}
		});
	
	}
	
	public void lunchAlbum(String id){
    	Intent i = new Intent(this, TracksActivity.class);
    	i.putExtra("albumId", id);
    	i.putExtra("request", "lyrics");
    	startActivity(i);
    }
	
	public void lunchSongs(String id){
    	Intent i = new Intent(this, TracksActivity.class);
    	i.putExtra("albumId", id);
    	i.putExtra("request", "songs");
    	startActivity(i);
    }

}
