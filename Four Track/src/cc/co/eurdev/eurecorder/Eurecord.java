package cc.co.eurdev.eurecorder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
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
    SeekBar seekBar;
    TextView listItem;
    ListView listView;
    HashMap<String, String> filesMap;
	
	
	
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
        		
        		String fullPath = trackPath + filesMap.get(((TextView) view).getText().toString());
        		showAudioPlayer(fullPath);
        		Toast.makeText(getApplicationContext(), "Playing: " + ((TextView) view).getText(), 
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

            		if (sampleRate == 8000) {
            			ar = new AudioRecorder(false, AudioSource.MIC, sampleRate,
            					AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            		} else {
            			ar = new AudioRecorder(true, AudioSource.MIC, sampleRate, 
            					AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            		}
            		
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
    
    @Override
    public void onPause() {
    	super.onPause();
    	Log.i("onPause", "onPause() called");
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.i("onDestroy", "onDestroy() called");
    }
    
    public void showAudioPlayer(String fullPath) {
    	Intent intent = new Intent(this, AudioPlayer.class);
    	intent.putExtra("AUDIO_FILE_PATH", fullPath);
    	startActivity(intent);
    	
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
    		filesMap = new HashMap<String, String>();
    		
    		for (int i = 0; i < files.length; i++) {
    			filesMap.put(parseAudioFile(files[i]), files[i]);
    		}
    		//String tmp[] = filesMap.keySet().toArray(new String[0]);
    		
    		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, filesMap.keySet().toArray(new String[0])));
    		
    		
    	}
    }
    
    public String parseAudioFile(String input) {
    	String tmp = input.substring(9, input.length()-4);
    	StringTokenizer tokens = new StringTokenizer(tmp, "-.");
    	StringBuilder result = new StringBuilder();
    	
    	while (tokens.hasMoreTokens()) {
    		result.append(tokens.nextToken());
    		if (tokens.hasMoreTokens()) {
    			result.append(" ");
    		}
    	}
    	return result.toString();
    }
    
    public double freeSpace() {
    	
    	StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
    	double availableSize = (double)stat.getAvailableBlocks() *(double)stat.getBlockSize();
    	//One binary gigabyte equals 1,073,741,824 bytes.
    	//double gigaAvailable = sdAvailSize / 1073741824;
    	return availableSize / 1048576;
    }
}