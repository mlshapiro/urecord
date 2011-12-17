package cc.co.eurdev.eurecorder;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class AudioPlayer extends Activity {
	
	String fullPath;
	static MediaPlayer mediaPlayer;
	int pausePosition = 0;
	SeekBar seekBar;
	Runnable notification;
	ToggleButton togglePlay;
	Handler handler = new Handler();

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audioplayer);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			fullPath = extras.getString("AUDIO_FILE_PATH");
			Log.v("from AudioPlayer", fullPath);
			
		}
		
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		togglePlay = (ToggleButton) findViewById(R.id.toggleButton1);
		
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
        				mediaPlayer.pause();
        				pausePosition = mediaPlayer.getCurrentPosition();
        				handler.removeCallbacks(notification);
        				seekBar.setProgress(pausePosition);
        			}
        			
        		}
        		
	
        	}
        });
		playAudio();
	}
	
	@Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.i("onDestroy", "onDestroy() called");
    	
    	handler.removeCallbacks(notification);
    	if (mediaPlayer != null) {
    		mediaPlayer.stop();
    		mediaPlayer.release();
    	}
    }
	
	public void seekTo(View v){
		if (mediaPlayer.isPlaying()) {
			SeekBar sb = (SeekBar)v;
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
    	}
    }
	
	public void playAudio() {
    	
		mediaPlayer = new MediaPlayer();
    	try {
    		mediaPlayer.reset();
    		mediaPlayer.setDataSource(fullPath);
    		mediaPlayer.prepare();
    		mediaPlayer.setOnPreparedListener( new OnPreparedListener() {
    			public void onPrepared(MediaPlayer mp) {
    				seekBar.setMax(mediaPlayer.getDuration());
    	    		mediaPlayer.start();
    	    		seekBarUpdater();	
    			}
    		});
    		
    		togglePlay.setChecked(true);
    		
    		
    	} catch (IOException e) {
			Log.v(getString(R.string.app_name), e.getMessage());
    	}
    }

}
