package cc.co.eurdev.urecorder;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AudioPlayer extends Activity {

	String fullPath;
	MediaPlayer mediaPlayer;
	int pausePosition = 0;
	SeekBar seekBar;
	Runnable notification;
	ToggleButton togglePlay;
	Handler handler = new Handler();
	TextView filePathText;
	TelephonyManager telephonyManager;
	boolean isPhone;
	PhoneStateListener callEventListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audioplayer);
		
		PackageManager pm = getPackageManager();
		isPhone = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);

		filePathText = (TextView) findViewById(R.id.textFilePath);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		togglePlay = (ToggleButton) findViewById(R.id.toggleButton1);
		
		mediaPlayer = new MediaPlayer();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			fullPath = extras.getString("AUDIO_FILE_PATH");
			filePathText.setText("Playing: " + fullPath);
			filePathText.setSelected(true);
//			Log.v("from AudioPlayer", fullPath);

		}

		seekBar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				seekTo(v);
				return false;
			}
		});

		togglePlay.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (togglePlay.isChecked()) {
					if (pausePosition > 0) {
						mediaPlayer.seekTo(pausePosition);
					}
					mediaPlayer.start();
					seekBarUpdater();

				} else {
					if (mediaPlayer.isPlaying()) {
						pauseAudio();
					}

				}

			}
		});
		
		if (isPhone) {
			
			callEventListener = new PhoneStateListener(){
				
				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					super.onCallStateChanged(state, incomingNumber);
//              	  if (state == TelephonyManager.CALL_STATE_IDLE){
//              	      //....
//              	  }
					if (state == TelephonyManager.CALL_STATE_RINGING){
						
						if (mediaPlayer.isPlaying()) {
							pauseAudio();
							togglePlay.setChecked(false);
						}
					}
				}
			};
			telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			telephonyManager.listen(callEventListener, PhoneStateListener.LISTEN_CALL_STATE);
		}
        
		playAudio();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// Log.i("AudioPlayer", "onPause() called");
		//this.finish();

	}

	@Override
	public void onStop() {
		super.onStop();
//		Log.i("AudioPlayer", "onStop() called");
		//this.finish();
//		if (mediaPlayer != null) {
//			if (mediaPlayer.isPlaying()) {
//				pauseAudio();
//				togglePlay.setChecked(false);
//			}
//		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Log.i("AudioPlayer", "onDestroy() called");

		handler.removeCallbacks(notification);
		mediaPlayer.stop();
		mediaPlayer.release();
		if (isPhone) {
			telephonyManager.listen(callEventListener, PhoneStateListener.LISTEN_NONE);
		}
	}

	public void seekTo(View v) {
			if (mediaPlayer.isPlaying()) {
				SeekBar sb = (SeekBar) v;
				mediaPlayer.seekTo(sb.getProgress());
			}

	}

	public void seekBarUpdater() {

		if (mediaPlayer.isPlaying()) {
			seekBar.setProgress(mediaPlayer.getCurrentPosition());
			notification = new Runnable() {
				public void run() {
					seekBarUpdater();
				}
			};
			handler.postDelayed(notification, 1000);
		} else {
			mediaPlayer.pause();
			seekBar.setProgress(0);
			pausePosition = 0;
			togglePlay.setChecked(false);
			//this.finish();
		}
	}

	public void playAudio() {

		
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(fullPath);
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mp) {
					seekBar.setMax(mediaPlayer.getDuration());
					mediaPlayer.start();
					seekBarUpdater();
				}
			});

			togglePlay.setChecked(true);

		} catch (IOException e) {
			Log.e(getString(R.string.app_name), e.getMessage());
		}
	}
	
	public void pauseAudio() {
		
		mediaPlayer.pause();
		pausePosition = mediaPlayer.getCurrentPosition();
		handler.removeCallbacks(notification);
		seekBar.setProgress(pausePosition);
	}

}
