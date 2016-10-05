package com.itgapps.appidol;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.itgapps.appidol.ListViewAdapters.AlbumsListViewAdapter;
import com.itgapps.appidol.db.DaoDbAdapter;

public class SongActivity extends Activity implements OnSeekBarChangeListener {

	Intent serviceIntent;
	private ImageButton buttonPlayStop;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPowerOf;
	// private ImageButton btnRepeat;
	// private ImageButton btnShuffle;
	private TextView songTitleLabel;
	private ImageView imgCover;

	private int currentPosition;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0;
	// private boolean isShuffle = false;
	// private boolean isRepeat = false;

	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;

	private Utilities utils;
	// -- PUT THE NAME OF YOUR AUDIO FILE HERE...URL GOES IN THE SERVICE
	private String strAudioLink; // url
	private String trackId; // id
	private String name = "Selena Gomez";
	private String title;
	private String album;
	private String cover;

	private boolean isOnline;
	private boolean boolMusicPlaying = false;
	private boolean isPaused = false;
	// private boolean isSongManager = false;
	TelephonyManager telephonyManager;
	PhoneStateListener listener;

	// --Seekbar variables --
	private SeekBar seekBar;
	private int seekMax;
	private int songEnded = 0;
	boolean mBroadcastIsRegistered;

	// --Set up constant ID for broadcast of seekbar position--
	public static final String BROADCAST_SEEKBAR = "com.itgapps.selenagomezappidol.sendseekbar";
	// --Set up constant ID for broadcast of onPause action--
	public static final String BROADCAST_ON_PAUSE = "com.itgapps.selenagomezappidol.sendonpause";
	Intent intent;
	Intent pauseIntent;

	// Progress dialogue and broadcast receiver variables
	boolean mBufferBroadcastIsRegistered;
	private ProgressDialog pdBuff = null;
	boolean mCompletedBroadcastIsRegistered;

	// Playlist
	public SongsManager songsManager;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private Cursor cursor;
	int totalSongs;
	int songIndex;

	boolean request;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song);

		// Initialize Utilities
		utils = new Utilities();

		Bundle extras = getIntent().getExtras();
		trackId = extras.getString("trackId");

		if (extras.getBoolean("request"))
			request = true;
		else
			request = false;

		try {

			serviceIntent = new Intent(this, MusicService.class);

			// --- set up seekbar intent for broadcasting new position to
			// service ---
			intent = new Intent(BROADCAST_SEEKBAR);
			pauseIntent = new Intent(BROADCAST_ON_PAUSE);

			initViews();
			setListeners();

			// to restart activity -- quitar para implementar playlist
			// buttonPlayStopClick();
			if (isPaused && !boolMusicPlaying) {
				pauseService();
				Toast.makeText(this, "isPaused && !boolMusicPlaying (1)",
						Toast.LENGTH_SHORT).show();
			} else if (isPaused && boolMusicPlaying) {
				playAudio();
				boolMusicPlaying = true;
				Toast.makeText(this, "isPaused && boolMusicPlaying (2)",
						Toast.LENGTH_SHORT).show();
			} else {
				findSong(trackId);
				songTitleLabel.setText(name);
				Toast.makeText(this, "else",
						Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					e.getClass().getName() + " " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	private void findSong(String trackId) {
		// get Track from DB
		DaoDbAdapter mDbHelper = new DaoDbAdapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();

		// Add Track to SongsManager
		// strTrackId = Integer.toString(trackId);
		// Toast.makeText(this, trackId, Toast.LENGTH_LONG).show();
		cursor = mDbHelper.getSong(trackId);
		cursor.moveToFirst();
		strAudioLink = cursor.getString(cursor.getColumnIndex("urlSong"));
		title = cursor.getString(cursor.getColumnIndex("name"));
		cover = cursor.getString(cursor.getColumnIndex("cover"));
		cursor.close();

		playAudio();
		boolMusicPlaying = true;

	}

	private void findNextSong(String trackId) {
		// get Track from DB
		DaoDbAdapter mDbHelper = new DaoDbAdapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();

		// Add Track to SongsManager
		// strTrackId = Integer.toString(trackId);
		// Toast.makeText(this, trackId, Toast.LENGTH_LONG).show();

		// seek to next song
		int intId = Integer.parseInt(trackId);
		// insertar metodo de rescate x si no hay mayor ID
		intId++;
		String nextTrackId = Integer.toString(intId);

		cursor = mDbHelper.getSong(nextTrackId);
		cursor.moveToFirst();
		strAudioLink = cursor.getString(cursor.getColumnIndex("urlSong"));
		title = cursor.getString(cursor.getColumnIndex("name"));
		cursor.close();

		playAudio();
		boolMusicPlaying = true;
	}

	private void findPreviousSong(String trackId) {
		// get Track from DB
		DaoDbAdapter mDbHelper = new DaoDbAdapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();

		// Add Track to SongsManager
		// strTrackId = Integer.toString(trackId);
		// Toast.makeText(this, trackId, Toast.LENGTH_LONG).show();

		// seek a next song
		int intId = Integer.parseInt(trackId);
		if (intId != 1)
			intId--;
		String nextTrackId = Integer.toString(intId);

		cursor = mDbHelper.getSong(nextTrackId);
		cursor.moveToFirst();
		strAudioLink = cursor.getString(cursor.getColumnIndex("urlSong"));
		title = cursor.getString(cursor.getColumnIndex("name"));
		cursor.close();

		playAudio();
		boolMusicPlaying = true;
	}

	// -- Broadcast Receiver to update position of seekbar from service --
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent serviceIntent) {
			updateUI(serviceIntent);
			currentPosition = Integer.parseInt(serviceIntent
					.getStringExtra("counter"));
		}
	};

	private void updateUI(Intent serviceIntent) {
		String counter = serviceIntent.getStringExtra("counter");
		String mediamax = serviceIntent.getStringExtra("mediamax");
		String strSongEnded = serviceIntent.getStringExtra("songEnded");
		String title = serviceIntent.getStringExtra("titleSong");

		Long total = Long.parseLong(mediamax);
		Long current = Long.parseLong(counter);

		int seekProgress = Integer.parseInt(counter);
		seekMax = Integer.parseInt(mediamax);
		songEnded = Integer.parseInt(strSongEnded);
		seekBar.setMax(seekMax);

		// Displaying Total Duration time
		songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(total));
		// Displaying time completed playing
		songCurrentDurationLabel.setText(""
				+ utils.milliSecondsToTimer(current));
		seekBar.setProgress(seekProgress);
		// Displaing de title song
		songTitleLabel.setText(title);

		if (songEnded == 1) {
			buttonPlayStop.setImageResource(R.drawable.img_btn_play);
		}
	}

	// --End of seekbar update code--

	// --- Set up initial screen ---
	private void initViews() {
		buttonPlayStop = (ImageButton) findViewById(R.id.btnPlay);
		buttonPlayStop.setImageResource(R.drawable.btn_pause);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPowerOf = (ImageButton) findViewById(R.id.btnPowerOf);
		// btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		// btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		imgCover = (ImageView) findViewById(R.id.cover);

		// --Reference seekbar in main.xml
		seekBar = (SeekBar) findViewById(R.id.songProgressBar);
	}

	// --- Set up listeners ---
	private void setListeners() {
		// Play Pause //Stop
		buttonPlayStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonPlayStopClick();
			}
		});

		// Stop Power Of
		btnPowerOf.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmPowerOf();
			}
		});

		/**
		 * Forward button click event Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				int seekForward = currentPosition + seekForwardTime;
				intent.putExtra("seekpos", seekForward);
				sendBroadcast(intent);
			}
		});

		/**
		 * Backward button click event Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int seekForward = currentPosition - seekBackwardTime;
				intent.putExtra("seekpos", seekForward);
				sendBroadcast(intent);
			}
		});

		/**
		 * Next button click event Plays next song by taking currentSongIndex +
		 * 1
		 * */
		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (boolMusicPlaying) {
					buttonPlayStop.setBackgroundResource(R.drawable.btn_play);
					stopPlayService();
					boolMusicPlaying = false;

					findNextSong(trackId);
					trackIncrement();
				}
			}
		});

		/**
		 * Back button click event Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (boolMusicPlaying) {
					buttonPlayStop.setBackgroundResource(R.drawable.btn_play);
					stopPlayService();
					boolMusicPlaying = false;

					findPreviousSong(trackId);
					trackDecrement();
				}
			}
		});

		seekBar.setOnSeekBarChangeListener(this);

	}

	// --- End Set up listeners ---

	/**
	 * Increment a number of trackId through a parseInt
	 * */
	private void trackIncrement() {
		// seek a next song
		int intId = Integer.parseInt(trackId);
		intId++;
		trackId = Integer.toString(intId);
	}

	/**
	 * Decrement a number of trackId through a parseInt
	 * */
	private void trackDecrement() {
		// seek a previous song
		int intId = Integer.parseInt(trackId);
		if (intId != 1)
			intId--;
		trackId = Integer.toString(intId);
	}

	// --- invoked from ButtonPlayStop listener above ----
	private void buttonPlayStopClick() {
		if (!boolMusicPlaying) {
			buttonPlayStop.setBackgroundResource(R.drawable.btn_pause);
			playAudio();
			boolMusicPlaying = true;
		} else if (boolMusicPlaying) {
			buttonPlayStop.setBackgroundResource(R.drawable.btn_play);
			// stopPlayService();
			pauseService();
			boolMusicPlaying = false;
			isPaused = true;
		}
	}

	// --- Stop service (and music) ---
	private void stopPlayService() {
		// --Unregister broadcastReceiver for seekbar
		if (mBroadcastIsRegistered) {
			try {
				unregisterReceiver(broadcastReceiver);
				mBroadcastIsRegistered = false;
			} catch (Exception e) {
				// Log.e(TAG, "Error in Activity", e);

				e.printStackTrace();
				Toast.makeText(

				getApplicationContext(),

				e.getClass().getName() + " " + e.getMessage(),

				Toast.LENGTH_LONG).show();
			}
		}

		try {
			stopService(serviceIntent);

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					e.getClass().getName() + " " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		boolMusicPlaying = false;
	}

	// --- Start service and play music ---
	@SuppressWarnings("deprecation")
	private void playAudio() {

		checkConnectivity();
		fillImageCover(cover);

		if (isOnline) {
			// VER
			// stopPlayService();
			serviceIntent.putExtra("trackId", trackId);
			serviceIntent.putExtra("sentAudioLink", strAudioLink);
			serviceIntent.putExtra("titleSong", title);
			serviceIntent.putExtra("request", request);

			try {
				startService(serviceIntent);
			} catch (Exception e) {

				e.printStackTrace();
				Toast.makeText(

				getApplicationContext(),

				e.getClass().getName() + " " + e.getMessage(),

				Toast.LENGTH_LONG).show();
			}

			// -- Register receiver for seekbar--
			registerReceiver(broadcastReceiver, new IntentFilter(
					MusicService.BROADCAST_ACTION));
			;
			mBroadcastIsRegistered = true;

		} else {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Network Not Connected...");
			alertDialog.setMessage("Please connect to a network and try again");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// here you can add functions
				}
			});
			alertDialog.setIcon(R.drawable.ic_launcher);
			buttonPlayStop.setImageResource(R.drawable.btn_play);
			alertDialog.show();
		}
	}

	public void pauseService() {
		boolean paused = true;
		pauseIntent.putExtra("paused", paused);
		sendBroadcast(pauseIntent);
	}

	private void fillImageCover(String cover) {
		if (cover.equals("disk1")) {
			imgCover.setImageResource(R.drawable.disk1);
		} else if (cover.equals("disk2")) {
			imgCover.setImageResource(R.drawable.disk2);
		} else if (cover.equals("disk3")) {
			imgCover.setImageResource(R.drawable.disk3);
		} else if (cover.equals("disk4")) {
			imgCover.setImageResource(R.drawable.disk4);
		} else if (cover.equals("disk5")) {
			imgCover.setImageResource(R.drawable.disk5);
		} else if (cover.equals("disk6")) {
			imgCover.setImageResource(R.drawable.disk6);
		}
	}

	// Handle progress dialogue for buffering...
	private void showPD(Intent bufferIntent) {
		String bufferValue = bufferIntent.getStringExtra("buffering");
		int bufferIntValue = Integer.parseInt(bufferValue);

		// When the broadcasted "buffering" value is 1, show "Buffering"
		// progress dialogue.
		// When the broadcasted "buffering" value is 0, dismiss the progress
		// dialogue.

		switch (bufferIntValue) {
		case 0:
			// Log.v(TAG, "BufferIntValue=0 RemoveBufferDialogue");
			// txtBuffer.setText("");
			if (pdBuff != null) {
				pdBuff.dismiss();
			}
			break;

		case 1:
			BufferDialogue();
			break;

		// Listen for "2" to reset the button to a play button
		case 2:
			buttonPlayStop.setImageResource(R.drawable.btn_play);
			break;

		}
	}

	// Progress dialogue...
	private void BufferDialogue() {

		pdBuff = ProgressDialog.show(SongActivity.this, "Buffering...",
				"Acquiring song...", true);
	}

	// Set up broadcast receiver
	private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent bufferIntent) {
			showPD(bufferIntent);
		}
	};

	// Set up broadcast receiver when finish song
	private BroadcastReceiver broadcastCompletedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent bufferIntent) {
			findNextSong(trackId);
			trackIncrement();
		}
	};

	private void checkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting()
				|| cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
						.isConnectedOrConnecting())
			isOnline = true;
		else
			isOnline = false;
	}

	private void confirmPowerOf() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setMessage(getResources().getString(
				R.string.confirm_message));
		alertDialog.setPositiveButton(getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (boolMusicPlaying) {
							buttonPlayStop
									.setBackgroundResource(R.drawable.btn_play);
							stopPlayService();
							boolMusicPlaying = false;
							finish();
						}
					}
				});
		alertDialog.setNegativeButton(getString(android.R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		alertDialog.show();
	}

	// -- onPause, unregister broadcast receiver. To improve, also save screen
	// data ---
	@Override
	protected void onPause() {
		// Unregister broadcast receiver
		if (mBufferBroadcastIsRegistered) {
			unregisterReceiver(broadcastBufferReceiver);
			mBufferBroadcastIsRegistered = false;
		}
		if (mCompletedBroadcastIsRegistered) {
			unregisterReceiver(broadcastCompletedReceiver);
			mCompletedBroadcastIsRegistered = false;
		}
		super.onPause();
	}

	// -- onResume register broadcast receiver. To improve, retrieve saved
	// screen data ---
	@Override
	protected void onResume() {
		// Register broadcast receiver
		if (!mBufferBroadcastIsRegistered) {
			registerReceiver(broadcastBufferReceiver, new IntentFilter(
					MusicService.BROADCAST_BUFFER));
			mBufferBroadcastIsRegistered = true;
		}
		if (!mCompletedBroadcastIsRegistered) {
			registerReceiver(broadcastCompletedReceiver, new IntentFilter(
					MusicService.BROADCAST_COMPLETED));
			mCompletedBroadcastIsRegistered = true;
		}
		super.onResume();
	}

	// -- onDestroy unregister broadcast receiver. To improve, retrieve saved
	// screen data ---
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// --- When user manually moves seekbar, broadcast new position to service
	// ---
	@Override
	public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser) {
			int seekPos = sb.getProgress();
			intent.putExtra("seekpos", seekPos);
			sendBroadcast(intent);
		}
	}

	// --- The following two methods are alternatives to track seekbar if moved.
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

}