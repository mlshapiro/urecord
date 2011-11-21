package cc.co.eurdev.eurecorder;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Eurecord extends Activity {
    /** Called when the activity is first created. */
	
	String trackPath;
	boolean sdIsMounted;
	LinearLayout layout;
	LinearLayout.LayoutParams params;
	TextView freeSpaceView;
	AudioRecorder ar;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        trackPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        sdIsMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        freeSpaceView = (TextView)findViewById(R.id.textFreeSpace);
        
        final ToggleButton toggleRecord = (ToggleButton) findViewById(R.id.toggleRecord);
        toggleRecord.setText("Record");
        
        final Spinner sampleRateSpinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
        		this, R.array.sample_rate_array, android.R.layout.simple_spinner_dropdown_item);
        sampleRateSpinner.setAdapter(spinnerAdapter);

        prepareResources();
        //Toast.makeText(Eurecord.this, "Free space: " + (int)freeSpace() + "MB", Toast.LENGTH_LONG).show();
        
        
        toggleRecord.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		if (toggleRecord.isChecked()) {
        			
            		Calendar calendar = Calendar.getInstance();
            		int second = calendar.get(Calendar.SECOND);
            		int minute = calendar.get(Calendar.MINUTE);
            		int hour = calendar.get(Calendar.HOUR_OF_DAY);
            		int day = calendar.get(Calendar.DAY_OF_MONTH);
            		int month = calendar.get(Calendar.MONTH);
            		int year = calendar.get(Calendar.YEAR);
            		
            		String sampleRateString = sampleRateSpinner.getSelectedItem().toString().replace(",", "");
            		int sampleRate = Integer.parseInt(sampleRateString);
            		
            		String track = "eurecord_" + year + "-" + month + "-" + day + 
            						"-" + hour + "." + minute + "." + second;

            		
            		if (sampleRateString.equals("8000")) {
            			track += ".3gp";
            		} else {
            			track += ".wav";
            		}
            			
            		//Toast.makeText(Eurecord.this, trackPath+track+ " "+ sampleRate, Toast.LENGTH_LONG).show();
            		
            		prepareResources();
            		toggleRecord.setText("Stop");
            		ar = AudioRecorder.getInstance(sampleRate);
            		try {
            			ar.setOutputFile(trackPath + track);
            			ar.prepare();
            			ar.start();
            			Toast.makeText(Eurecord.this, "Recording " + trackPath + track, Toast.LENGTH_LONG).show();
            		} catch (Exception e) {
            			Log.e("record: ", e.getMessage());
            		}
        		} else {
        			ar.stop();
            		ar.release();
            		Toast.makeText(Eurecord.this, "stopped", Toast.LENGTH_LONG).show();
            		toggleRecord.setText("Record");
        		}
        		
	
        	}
        });
        
    }
    
    public void prepareResources() {
    	
    	if (sdIsMounted) {
    		freeSpaceView.setText("There is " + (int)freeSpace() + "MB of space left.");
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