package com.itgapps.appidol;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;

public class SongsManager {
	
	private String trackId;
	private String trackName;
	private String trackUrlSong;
	private String trackUrlVideo;
		
	private ArrayList<HashMap<String, String>> songsList = 	new ArrayList<HashMap<String, String>>();
	
	///Contructor
	public SongsManager(){
		
	}
	 
	// Add song to the playlist
	public void addSong(Cursor cursor){
		HashMap<String, String> song = new HashMap<String, String>();
		song.put("trackId", cursor.getString(cursor.getColumnIndex("_id")));
		song.put("trackName", cursor.getString(cursor.getColumnIndex("name")));
		song.put("trackUrlSong", cursor.getString(cursor.getColumnIndex("urlSong")));
		song.put("trackUrlVideo", cursor.getString(cursor.getColumnIndex("urlVideo")));
		songsList.add(song);
	}
	
	// get PlayList
	public ArrayList<HashMap<String, String>>getPlayList(){
		
		return songsList;
	}
	

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getTrackUrlSong() {
		return trackUrlSong;
	}

	public void setTrackUrlSong(String trackUrlSong) {
		this.trackUrlSong = trackUrlSong;
	}

	public String getTrackUrlVideo() {
		return trackUrlVideo;
	}

	public void setTrackUrlVideo(String trackUrlVideo) {
		this.trackUrlVideo = trackUrlVideo;
	}
	
	
}
