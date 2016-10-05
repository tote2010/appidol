package com.itgapps.appidol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Ads

        //AdView adView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //adView.loadAd(adRequest);

    }



    public void lunchAlbums(View view){
        Intent i = new Intent(this, AlbumsActivity.class);
        i.putExtra("request", "lyrics");
        startActivity(i);
    }

    public void lunchLyrics(View view){
        Intent i = new Intent(this, LyricsListActivity.class);
        startActivity(i);
    }

    public void lunchSongs(View view){
        Intent i = new Intent(this, AlbumsActivity.class);
        i.putExtra("request", "songs");
        startActivity(i);
    }

    public void lunchVideo(View view){
        Intent i = new Intent(this, VideosListActivity.class);
        startActivity(i);
    }

    public void share(){
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("text/plain");
        intentShare.putExtra(Intent.EXTRA_SUBJECT, R.string.share_subjet);
        intentShare.putExtra(Intent.EXTRA_TEXT, R.string.share_app);
        String title = getResources().getString(R.string.share_title_window);
        startActivity(Intent.createChooser(intentShare , title));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_share_app){
            share();
        }
        return super.onOptionsItemSelected(item);
    }
}