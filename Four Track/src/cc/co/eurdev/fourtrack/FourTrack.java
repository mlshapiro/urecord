package cc.co.eurdev.fourtrack;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import cc.co.eurdev.fourtrack.widget.VerticalSeekBar;

public class FourTrack extends Activity {
    /** Called when the activity is first created. */
	
	LinearLayout layout;
	LinearLayout.LayoutParams params;
	AudioRecorder ar = AudioRecorder.getInstance(false);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        layout = (LinearLayout)findViewById(R.id.volumeControls);
        params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, 1f);
        //layout.setGravity(Gravity.CENTER_HORIZONTAL);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        
        VerticalSeekBar verticalSeekBar1 = new VerticalSeekBar(this);
        VerticalSeekBar verticalSeekBar2 = new VerticalSeekBar(this);
        VerticalSeekBar verticalSeekBar3 = new VerticalSeekBar(this);
        VerticalSeekBar verticalSeekBar4 = new VerticalSeekBar(this);
        
        Drawable thumb = getResources().getDrawable(R.drawable.empty);
        
        thumb.mutate().setAlpha(0);
        
        verticalSeekBar1.setThumb(thumb);
        verticalSeekBar2.setThumb(thumb);
        verticalSeekBar3.setThumb(thumb);
        verticalSeekBar4.setThumb(thumb);
        
         
        
        layout.addView(verticalSeekBar1, params);
        layout.addView(verticalSeekBar2, params);
        layout.addView(verticalSeekBar3, params);
        layout.addView(verticalSeekBar4, params);
        
        Button recordButton = (Button)findViewById(R.id.buttonRecord);
        Button playButton = (Button)findViewById(R.id.buttonPlay);
        
        recordButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		try {
        			ar.setOutputFile("/sdcard/tebAudio.wav");
        			ar.prepare();
        			ar.start();
        		} catch (Exception e) {
        			Log.e("record: ", e.getMessage());
        		}
        		
        	}
        });
        
        playButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		ar.stop();
        		ar.release();
        		
        	}
        });
        
        
    }
}