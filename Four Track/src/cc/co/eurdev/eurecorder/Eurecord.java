package cc.co.eurdev.eurecorder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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
	String[] files;
	ToggleButton toggleRecord;
    Spinner sampleRateSpinner;
    TextView listItem;
    ListView listView;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sdIsMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        trackPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        
        
        freeSpaceView = (TextView)findViewById(R.id.textFreeSpace);
        
        toggleRecord = (ToggleButton) findViewById(R.id.toggleRecord); 
        sampleRateSpinner = (Spinner) findViewById(R.id.spinner1);
        listItem = (TextView) findViewById(R.id.textListItem);
        listView = (ListView) findViewById(R.id.listView1);
        
        listView.setTextFilterEnabled(true);
        
        
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
        		this, R.array.sample_rate_array, android.R.layout.simple_spinner_dropdown_item);
        sampleRateSpinner.setAdapter(spinnerAdapter);
        sampleRateSpinner.setSelection(2);
        
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        		Toast.makeText(getApplicationContext(), ((TextView) view).getText(), 
        				Toast.LENGTH_SHORT).show();
        	}
        });

        
        toggleRecord.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		if (toggleRecord.isChecked()) {
        			
            		Calendar calendar = Calendar.getInstance();
            		int second = calendar.get(Calendar.SECOND);
            		int minute = calendar.get(Calendar.MINUTE);
            		int hour = calendar.get(Calendar.HOUR_OF_DAY);
            		int day = calendar.get(Calendar.DAY_OF_MONTH);
            		int month = calendar.get(Calendar.MONTH) + 1;
            		int year = calendar.get(Calendar.YEAR);
            		
            		String sampleRateString = sampleRateSpinner.getSelectedItem().toString().replace(",", "");
            		int sampleRate = Integer.parseInt(sampleRateString);
            		
            		String track = "Eurecord_" + year + "-" + month + "-" + day + 
            						"-" + hour + "." + minute + "." + second;

            		
            		if (sampleRateString.equals("8000")) {
            			track += ".3gp";
            		} else {
            			track += ".wav";
            		}
            			
            		//Toast.makeText(Eurecord.this, trackPath+track+ " "+ sampleRate, Toast.LENGTH_LONG).show();

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
            		prepareResources();
        		}
        		
	
        	}
        });
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	prepareResources();
    }
    
    public void prepareResources() {
    	
    	if (sdIsMounted) {
    		freeSpaceView.setText("There is " + (int)freeSpace() + "MB of space left.");
    		
    		FilenameFilter filter = new FilenameFilter() {
    			public boolean accept(File dir, String name) {
    				return name.startsWith("Eurecord_") && (name.endsWith(".wav") || name.endsWith(".3gp"));
    			}
    		};
    		File dir = new File(trackPath);
    		files = dir.list(filter);
    		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, files));
    		
    		
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