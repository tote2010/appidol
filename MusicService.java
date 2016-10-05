package com.itgapps.appidol;

import java.io.IOException;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


public class MusicService extends Service implements OnCompletionListener,
		OnPreparedListener, OnErrorListener, OnSeekCompleteListener,
		OnInfoListener, OnBufferingUpdateListener {

	private static final String TAG = "TELSERVICE";
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private String trackId;
	private String sntAudioLink;
	private String title;

	// -- URL location of audio clip PUT YOUR AUDIO URL LOCATION HERE ---
	private static final String URL_STRING = "http://www.itgapps.com/145615/";

	// Set up the notification ID
	private static final int NOTIFICATION_ID = 1;
	private boolean isPaused = false;
	private PhoneStateListener phoneStateListener;
	private TelephonyManager telephonyManager;

	// ---Variables for seekbar processing---
	String sntSeekPos;
	int intSeekPos;
	int mediaPosition;
	int mediaMax;
	// Intent intent;
	private final Handler handler = new Handler();
	private static int songEnded;
	public static final String BROADCAST_ACTION = "com.itgapps.appidol.seekprogress";

	// Set up broadcast identifier and intent
	public static final String BROADCAST_BUFFER = "com.itgapps.appidol.broadcastbuffer";

	// Set up broadcast identifier for finish song
	public static final String BROADCAST_COMPLETED = "com.itgapps.appidol.broadcastbuffercompleted";

	Intent bufferIntent;
	Intent seekIntent;
	Intent bufferCompleted;
	// Declare headsetSwitch variable
	private int headsetSwitch = 1;

	// OnCreate
	@Override
	public void onCreate() {
		Log.v(TAG, "Creating Service");
		// android.os.Debug.waitForDebugger();
		// Instantiate bufferIntent to communicate with Activity for progress
		// dialogue
		bufferIntent = new Intent(BROADCAST_BUFFER);
		// ---Set up intent for seekbar broadcast ---
		seekIntent = new Intent(BROADCAST_ACTION);
		// ---Set up intent for finish song ---
		bufferCompleted = new Intent(BROADCAST_COMPLETED);

		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.reset();

		// Register headset receiver
		registerReceiver(headsetReceiver, new IntentFilter(
				Intent.ACTION_HEADSET_PLUG));

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// ---Set up receiver for seekbar change ---
		registerReceiver(broadcastReceiver, new IntentFilter(
				SongActivity.BROADCAST_SEEKBAR));

		// ---Set up receiver for onPause action ---
		registerReceiver(broadcastOnPause, new IntentFilter(
				SongActivity.BROADCAST_ON_PAUSE));

		// Manage incoming phone calls during playback. Pause mp on incoming,
		// resume on hangup.
		// -----------------------------------------------------------------------------------
		// Get the telephony manager
		Log.v(TAG, "Starting telephony");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		Log.v(TAG, "Starting listener");
		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// String stateString = "N/A";
				Log.v(TAG, "Starting CallStateChange");
				switch (state) {
				case TelephonyManager.CALL_STATE_OFFHOOK:
				case TelephonyManager.CALL_STATE_RINGING:
					if (mediaPlayer != null) {
						pauseMedia();
						isPaused = true;
					}

					break;
				case TelephonyManager.CALL_STATE_IDLE:
					// Phone idle. Start playing.
					if (mediaPlayer != null) {
						if (isPaused) {
							isPaused = false;
							playMedia();
						}

					}
					break;
				}

			}
		};

		// Register the listener with the telephony manager
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		if (isPaused) {
			isPaused = false;
			playMedia();
			initNotification();
			return START_STICKY;
		} else {

			// get Extra
			trackId = intent.getExtras().getString("trackId");
			sntAudioLink = intent.getExtras().getString("sentAudioLink");
			title = intent.getExtras().getString("titleSong");

			// Insert notification start
			initNotification();
			/*
			 * Intent notificationIntent = new Intent(this, SongActivity.class);
			 * notificationIntent.putExtra("trackId", trackId);
			 * notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * 
			 * PendingIntent pi = PendingIntent.getActivity(this, 0,
			 * notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			 * Notification notification = new Notification();
			 * notification.tickerText = "asd"; notification.icon =
			 * R.drawable.ic_launcher; notification.flags |=
			 * Notification.FLAG_SHOW_LIGHTS;
			 * notification.setLatestEventInfo(this, "MusicPlayerSample",
			 * "Playing: " + trackId, pi); startForeground(NOTIFICATION_ID,
			 * notification);
			 */

			// extra necesario para saber si se llama desde una notificaci� o
			// desde
			// un nuevo intento
			boolean restart = intent.getExtras().getBoolean("request");
			if (!restart) {
				// VER -- quitar para implementar el playlist o ver c�mo
				// trabajarlo
				// mediaPlayer.reset();
			}

			// Set up the MediaPlayer data source using the strAudioLink value
			if (!mediaPlayer.isPlaying()) {
				try {
					mediaPlayer.setDataSource(URL_STRING + sntAudioLink);

					// Send message to Activity to display progress dialogue
					sendBufferingBroadcast();
					// Prepare mediaplayer
					mediaPlayer.prepareAsync();

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
				}
			}
			// --- Set up seekbar handler ---
			setupHandler();

			return START_STICKY;
		}
	}

	// ---Send seekbar info to activity----
	private void setupHandler() {
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
	}

	/*
	private Runnable sendUpdatesToUI = new Runnable() {
		public void run() {
			// // Log.d(TAG, "entered sendUpdatesToUI");

			LogMediaPosition();

			handler.postDelayed(this, 1000); // 1 seconds

		}
	};
	*/
	private Runnable sendUpdatesToUI = new Runnable() {
		public void run() {
			// // Log.d(TAG, "entered sendUpdatesToUI");

			LogMediaPosition();

			handler.postDelayed(this, 1000); // 1 seconds

		}
	};

	private void LogMediaPosition() {
		// // Log.d(TAG, "entered LogMediaPosition");
		if (mediaPlayer.isPlaying()) {
			mediaPosition = mediaPlayer.getCurrentPosition();
			// if (mediaPosition < 1) {
			// Toast.makeText(this, "Buffering...", Toast.LENGTH_SHORT).show();
			// }
			mediaMax = mediaPlayer.getDuration();
			// seekIntent.putExtra("time", new Date().toLocaleString());
			seekIntent.putExtra("counter", String.valueOf(mediaPosition));
			seekIntent.putExtra("mediamax", String.valueOf(mediaMax));
			seekIntent.putExtra("songEnded", String.valueOf(songEnded));
			seekIntent.putExtra("titleSong", title);

			sendBroadcast(seekIntent);
		}
	}

	// --Receive seekbar position if it has been changed by the user in the
	// activity
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateSeekPos(intent);
		}
	};

	// --Receive onPause action by the user in the
	// activity
	private BroadcastReceiver broadcastOnPause = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateOnPause(intent);
			isPaused = true;
		}
	};

	// Update pause from Activity
	public void updateOnPause(Intent intent) {
		boolean paused = intent.getExtras().getBoolean("paused");
		if (mediaPlayer.isPlaying()) {
			if (paused) {
				pauseMedia();
			}
		}

	}

	// Update seek position from Activity
	public void updateSeekPos(Intent intent) {
		int seekPos = intent.getIntExtra("seekpos", 0);
		if (mediaPlayer.isPlaying()) {
			handler.removeCallbacks(sendUpdatesToUI);
			mediaPlayer.seekTo(seekPos);
			setupHandler();
		}

	}

	// ---End of seekbar code

	// If headset gets unplugged, stop music and service.
	private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
		private boolean headsetConnected = false;

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// Log.v(TAG, "ACTION_HEADSET_PLUG Intent received");
			if (intent.hasExtra("state")) {
				if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
					headsetConnected = false;
					headsetSwitch = 0;
					// Log.v(TAG, "State =  Headset disconnected");
					// headsetDisconnected();
				} else if (!headsetConnected
						&& intent.getIntExtra("state", 0) == 1) {
					headsetConnected = true;
					headsetSwitch = 1;
					// Log.v(TAG, "State =  Headset connected");
				}

			}

			switch (headsetSwitch) {
			case (0):
				headsetDisconnected();
				break;
			case (1):
				break;
			}
		}

	};

	private void headsetDisconnected() {
		stopMedia();
		stopSelf();

	}

	// --- onDestroy, stop media player and release. Also stop
	// phoneStateListener, notification, receivers...---
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.release();
		}

		if (phoneStateListener != null) {
			telephonyManager.listen(phoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}

		// Cancel the notification
		cancelNotification();

		// Unregister headsetReceiver
		unregisterReceiver(headsetReceiver);

		// Unregister seekbar receiver
		unregisterReceiver(broadcastReceiver);

		// Unregister onPause receiver
		unregisterReceiver(broadcastOnPause);

		// Stop the seekbar handler from sending updates to UI
		handler.removeCallbacks(sendUpdatesToUI);

		// Service ends, need to tell activity to display "Play" button
		resetButtonPlayStopBroadcast();
	}

	// Send a message to Activity that audio is being prepared and buffering
	// started.
	private void sendBufferingBroadcast() {
		// Log.v(TAG, "BufferStartedSent");
		bufferIntent.putExtra("buffering", "1");
		sendBroadcast(bufferIntent);
	}

	// Send a message to Activity that audio is prepared and ready to start
	// playing.
	private void sendBufferCompleteBroadcast() {
		// Log.v(TAG, "BufferCompleteSent");
		bufferIntent.putExtra("buffering", "0");
		sendBroadcast(bufferIntent);
	}

	// Send a message to Activity to reset the play button.
	private void resetButtonPlayStopBroadcast() {
		// Log.v(TAG, "BufferCompleteSent");
		bufferIntent.putExtra("buffering", "2");
		sendBroadcast(bufferIntent);
	}

	// Send a message to Activity that song is completed
	// playing.
	private void sendSongCompletedBroadcast() {
		// Log.v(TAG, "BufferCompleteSent");
		bufferIntent.putExtra("completed", "1");
		sendBroadcast(bufferCompleted);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {

		if (!mediaPlayer.isPlaying()) {
			playMedia();
			// Toast.makeText(this, "SeekComplete", Toast.LENGTH_SHORT).show();
		}

	}

	// ---Error processing ---
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
			Toast.makeText(this,
					"MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra,
					Toast.LENGTH_SHORT).show();
			break;
		case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
			Toast.makeText(this, "MEDIA ERROR SERVER DIED " + extra,
					Toast.LENGTH_SHORT).show();
			break;
		case MediaPlayer.MEDIA_ERROR_UNKNOWN:
			Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra,
					Toast.LENGTH_SHORT).show();
			break;
		}
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {

		// Send a message to activity to end progress dialogue

		sendBufferCompleteBroadcast();
		playMedia();

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// When song ends, need to tell activity to display "Play" button
		stopMedia();
		stopSelf();
		sendSongCompletedBroadcast();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void playMedia() {
		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.start();
		}
	}

	// Add for Telephony Manager
	public void pauseMedia() {
		// Log.v(TAG, "Pause Media");
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}

	}

	public void stopMedia() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

	// Create Notification
	private void initNotification() {
		String ns = Context.NOTIFICATION_SERVICE;

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "Tutorial: Music In Service";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		// notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		Context context = getApplicationContext();
		CharSequence contentTitle = "Playing Selena Gomez";
		CharSequence contentText = title;

		Intent notificationIntent = new Intent(context, SongActivity.class);
		notificationIntent.putExtra("trackId", trackId);
		notificationIntent.putExtra("request", true);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		// Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

		// PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
		// notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// PendingIntent.getActivity(this, 0, notificationIntent,
		// PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		// mNotificationManager.notify(NOTIFICATION_ID, notification);
		startForeground(NOTIFICATION_ID, notification);
	}

	// Cancel Notification
	private void cancelNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}
}
