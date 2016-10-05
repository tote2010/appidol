package com.itgapps.appidol.ListViewAdapters;

import java.util.HashMap;
import java.util.Map;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailLoader.ErrorReason;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.itgapps.appidol.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideosListViewAdapter extends BaseAdapter {

	private static final String DEVELOPER_KEY = "AIzaSyC8vUDCFoTx2MTET3KkGqfxfLqv_wPKjFU";
	private Context mContext;
	Cursor cursor;
	private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
	private final ThumbnailListener thumbnailListener;

	public VideosListViewAdapter(Context context, Cursor cur) {
		super();
		mContext = context;
		cursor = cur;
		thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
		thumbnailListener = new ThumbnailListener();

	}

	public int getCount() {
		// return the number of records in cursor
		return cursor.getCount();
	}

	// getView method is called for each item of ListView
	public View getView(int position, View view, ViewGroup parent) {
		// inflate the layout for each item of listView
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.list_videos, null);

		// move the cursor to required position
		cursor.moveToPosition(position);

		// fetch the sender number and sms body from cursor

		// String id = cursor.getString(cursor.getColumnIndex("_id"));
		String name = cursor.getString(cursor.getColumnIndex("name"));
		String urlVideo = cursor.getString(cursor.getColumnIndex("urlVideo"));

		// get the reference of textViews and thumbnailView
		TextView textUrlVideo = (TextView) view.findViewById(R.id.album_id);
		TextView textTitle = (TextView) view.findViewById(R.id.title);
		TextView textYear = (TextView) view.findViewById(R.id.year);

		YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view
				.findViewById(R.id.thumbnail);
		YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);

		// Set the Sender number and smsBody to respective TextViews
		textUrlVideo.setText(urlVideo);
		textTitle.setText(name);
		thumbnail.setTag(urlVideo);
		thumbnail.initialize(DEVELOPER_KEY, thumbnailListener);

		return view;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private final class ThumbnailListener implements
			YouTubeThumbnailView.OnInitializedListener,
			YouTubeThumbnailLoader.OnThumbnailLoadedListener {

		@Override
		public void onInitializationSuccess(YouTubeThumbnailView view,
				YouTubeThumbnailLoader loader) {
			loader.setOnThumbnailLoadedListener(this);
			thumbnailViewToLoaderMap.put(view, loader);
			view.setImageResource(R.drawable.loading_thumbnail);
			String videoId = (String) view.getTag();
			loader.setVideo(videoId);
		}

		@Override
		public void onInitializationFailure(YouTubeThumbnailView view,
				YouTubeInitializationResult loader) {
			view.setImageResource(R.drawable.no_thumbnail);
		}

		@Override
		public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
		}

		@Override
		public void onThumbnailError(YouTubeThumbnailView view,
				ErrorReason errorReason) {
			view.setImageResource(R.drawable.no_thumbnail);
		}

		public void onInitializationSuccess(Provider provider,
				YouTubePlayer player, boolean restored) {

		}

		public void onInitializationFailure(Provider provider,
				YouTubeInitializationResult result) {

		}
	}

}
