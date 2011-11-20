package cc.co.eurdev.fourtrack;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import cc.co.eurdev.fourtrack.widget.VerticalSeekBar;

public class Eurecord extends Activity {
    /** Called when the activity is first created. */
	
	String trackPath;
	boolean sdIsMounted;
	LinearLayout layout;
	LinearLayout.LayoutParams params;
	AudioRecorder ar;
	HashMap<Integer, String> trackMap = new HashMap<Integer, String>(4);
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        this.trackPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
							"/Android/data/" + getString(R.string.package_name) + "/";

        this.sdIsMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        
        
        if (this.trackMap.isEmpty()) {
        	this.trackMap.put(R.id.radio0, "track1.wav");
        	this.trackMap.put(R.id.radio1, "track2.wav");
        	this.trackMap.put(R.id.radio2, "track3.wav");
        	this.trackMap.put(R.id.radio3, "track4.wav");
        }
        

        
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
        
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        
        prepareResources();
        Toast.makeText(Eurecord.this, "Free space: " + (int)freeSpace() + "MB", Toast.LENGTH_LONG).show();
        
        recordButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		ar = AudioRecorder.getInstance(false);
        		String track = trackMap.get(radioGroup.getCheckedRadioButtonId());
        		
        		try {
        			ar.setOutputFile(trackPath + track);
        			ar.prepare();
        			ar.start();
        			Toast.makeText(Eurecord.this, "Recording", Toast.LENGTH_LONG).show();
        		} catch (Exception e) {
        			Log.e("record: ", e.getMessage());
        		}
	
        	}
        });
        
        playButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		ar.stop();
        		ar.release();
        		Toast.makeText(Eurecord.this, "Free space: " + (int)freeSpace() + "MB", Toast.LENGTH_LONG).show();
        		
        	}
        });
        
        
    }
    
    public void prepareResources() {
    	
    	if (sdIsMounted) {
    		try {
    			if (!(new File(trackPath)).exists()) {
    				new File(trackPath).mkdirs();
    				
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public double freeSpace() {
    	
    	StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
    	double availableSize = (double)stat.getAvailableBlocks() *(double)stat.getBlockSize();
    	//One binary gigabyte equals 1,073,741,824 bytes.
    	//double gigaAvailable = sdAvailSize / 1073741824;
    	return availableSize / 1048576;
    }
}