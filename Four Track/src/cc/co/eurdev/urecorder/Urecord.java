package cc.co.eurdev.urecorder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cc.co.eurdev.urecorder.db.DBAdapter;

public class Urecord extends Activity {
	/** Called when the activity is first created. */

	private DBAdapter db = new DBAdapter(this);

	String[] from = new String[] { "TimeStamp", "Date", "Length", "Time" };
	int[] to;

	private static final Map<Integer, String> monthMap = Collections
			.unmodifiableMap(new HashMap<Integer, String>() {
				{
					put(1, "Jan");
					put(2, "Feb");
					put(3, "Mar");
					put(4, "Apr");
					put(5, "May");
					put(6, "Jun");
					put(7, "Jul");
					put(8, "Aug");
					put(9, "Sep");
					put(10, "Oct");
					put(11, "Nov");
					put(12, "Dec");
				}
			});
	
	private static final Map<String, Integer> sampleMap = Collections
			.unmodifiableMap(new HashMap<String, Integer>() {
				{
					put("44.1 kHz", 44100);
					put("22.05 kHz", 22050);
					put("16 kHz", 16000);
					put("11.025 kHz", 11025);
					put("8 kHz (compressed)", 8000);
				}
			});

	SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm:ss aa");
	SimpleDateFormat fileNameDateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd.HH.mm.ss");
	String trackPath;
	boolean sdIsMounted;
	LinearLayout layout;
	LinearLayout.LayoutParams params;
	TextView freeSpaceView;
	AudioRecorder ar;

	ToggleButton toggleRecord;
	Spinner sampleRateSpinner;
	SeekBar seekBar;
	TextView listItem;
	ListView listView;
	HashMap<Integer, String> filesMap;
	
	TelephonyManager telephonyManager;
	PhoneStateListener callEventListener;

	String timeStamp;

	String date;
	String time;
	String fullPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		PackageManager pm = getPackageManager();
		boolean isPhone = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);

		sdIsMounted = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		trackPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/";

		to = new int[] { R.id.textTimeStamp, R.id.textDate, R.id.textLength,
				R.id.textTime };
		freeSpaceView = (TextView) findViewById(R.id.textFreeSpace);

		toggleRecord = (ToggleButton) findViewById(R.id.toggleRecord);
		sampleRateSpinner = (Spinner) findViewById(R.id.spinner1);

		// listItem = (TextView) findViewById(R.id.textListItem);
		listView = (ListView) findViewById(R.id.listView1);

		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(this, R.array.sample_rate_array,
						android.R.layout.simple_spinner_dropdown_item);
		sampleRateSpinner.setAdapter(spinnerAdapter);
		sampleRateSpinner.setSelection(2);

		listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String path = null;
				path = filesMap.get((int) id);

				showAudioPlayer(path);
				Toast.makeText(getApplicationContext(), "Playing: " + path,
						Toast.LENGTH_SHORT).show();
			}
		});

		toggleRecord.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (toggleRecord.isChecked()) {
					startRecording();
				} else {
					stopRecording();

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
						if (ar != null) {
							if (ar.getState() == AudioRecorder.State.RECORDING) {
								stopRecording();
								toggleRecord.setChecked(false);
							}
						}
					}
				}
			};
			telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			telephonyManager.listen(callEventListener, PhoneStateListener.LISTEN_CALL_STATE);
		}
		syncDatabaseWithFileSystem();
		updateListView();
		updateFreeSpace();

	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.i("onStart", "onStart() called");
//		syncDatabaseWithFileSystem();
//		updateListView();
//		updateFreeSpace();

	}

	@Override
	public void onRestart() {
		super.onRestart();
		// Log.i("onRestart", "onRestart() called");

	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.i("onResume", "onResume() called");

	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("Urecord", "onPause() called");

	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("Urecord", "onStop() called");
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("Urecord", "onDestroy() called");
		if (ar != null) {
			if (ar.getState() == AudioRecorder.State.RECORDING) {
				stopRecording();
				toggleRecord.setChecked(false);
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.itemDelete:
			TextView textView = (TextView) info.targetView
					.findViewById(R.id.textTimeStamp);
			String _id = textView.getText().toString();
			String path = null;
			long id = info.id;

			path = filesMap.get((int) id);

			File file = new File(path);
			boolean deleted = file.delete();
			if (deleted) {
				db.open();
				db.deleteEntry(_id);
				db.close();
				updateListView();
				Toast.makeText(getApplicationContext(), path + " deleted",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Delete failed",
						Toast.LENGTH_SHORT).show();
			}

			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	public void startRecording() {
		Calendar calendar = Calendar.getInstance();

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);

		timeStamp = Long.toString(calendar.getTimeInMillis());

		date = monthMap.get(month) + " " + day + ", " + year;
		// time = hour + ":" + minute + ":" + second;
		time = dateFormatter.format(calendar.getTime());
		String track = "Urecord_"
				+ fileNameDateFormatter.format(calendar.getTime());

		int sampleRate = sampleMap.get(sampleRateSpinner.getSelectedItem().toString());

		if (sampleRate == 8000) {
			track += ".3gp";
		} else {
			track += ".wav";
		}

		fullPath = trackPath + track;

		// Toast.makeText(Urecord.this, trackPath+track+ " "+ sampleRate,
		// Toast.LENGTH_LONG).show();

		if (sampleRate == 8000) {
			ar = new AudioRecorder(false, AudioSource.MIC, sampleRate,
					AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		} else {
			ar = new AudioRecorder(true, AudioSource.MIC, sampleRate,
					AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		}

//		Log.v("from recorder", fullPath);
		try {
			ar.setOutputFile(fullPath);
			ar.prepare();
			ar.start();
			Toast.makeText(Urecord.this, "Recording " + trackPath + track,
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Log.e("record: ", e.getMessage());
		}
	}

	public void stopRecording() {
		ar.stop();
		ar.release();
		Toast.makeText(Urecord.this, "stopped", Toast.LENGTH_LONG).show();

		long millis = 0;
		MediaPlayer mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(fullPath);
			mediaPlayer.prepare();
			millis = mediaPlayer.getDuration();
		} catch (IOException e) {
			Log.e(getString(R.string.app_name), e.getMessage());
		}
		mediaPlayer.stop();
		mediaPlayer.release();

		int seconds = (int) (millis / 1000) % 60;
		int minutes = (int) ((millis / (1000 * 60)) % 60);

		String length = minutes + " min, " + seconds + " sec";

//		Log.v("from DB", fullPath);
		// add to database
		db.open();
		db.addEntry(timeStamp, "empty", date, time, length, fullPath);
		db.close();
		updateListView();
		updateFreeSpace();
//		Log.i("stopRecording", "end of stopRecording() reached");
	}

	public void syncDatabaseWithFileSystem() {

		if (sdIsMounted) {

			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith("Urecord_")
							&& (name.endsWith(".wav") || name.endsWith(".3gp"));
				}
			};
			File dir = new File(trackPath);
			String[] files = dir.list(filter);

			for (int i = 0; i < files.length; i++) {
				files[i] = trackPath + files[i];
			}

			db.open();
			Cursor c = db.getEntriesOrderById();

			// If the file path from the db entry does not match an actual file
			// on the file system,
			// we know that the file has been deleted or moved, and should
			// remove from database
			if (c.moveToFirst()) {
				do {

					String path = c.getString(5);
					String timeStamp = c.getString(0);

					if (!Arrays.asList(files).contains(path)) {
						// remove current entry from database
						// Toast.makeText(getApplicationContext(), path +
						// " not found.  Removing from DB",
						// Toast.LENGTH_SHORT).show();
//						Log.i("syncDatabaseWithFileSystem", "removing " + path);
						db.deleteEntry(timeStamp);
					}

				} while (c.moveToNext());
			}
			db.close();

		}
	}

	public void showAudioPlayer(String fullPath) {
		Intent intent = new Intent(this, AudioPlayer.class);
		intent.putExtra("AUDIO_FILE_PATH", fullPath);
		startActivity(intent);

	}

	public void updateListView() {
		db.open();
		List<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();
		Cursor c = db.getEntriesOrderById();
		filesMap = new HashMap<Integer, String>();
		int i = 0;

		if (c.moveToFirst()) {
			do {

				HashMap<String, String> fields = new HashMap<String, String>();

				String timeStamp = c.getString(0);
				String date = c.getString(2);
				String time = c.getString(3);
				String length = c.getString(4);
				String path = c.getString(5);

				fields.put("TimeStamp", timeStamp);
				fields.put("Date", date);
				fields.put("Length", length);
				fields.put("Time", "at " + time);

				// db.addEntry(i+1, "empty", date, time, "empty", trackPath +
				// files[i]);
				filesMap.put(i, path);
				i++;

				rows.add(fields);
			} while (c.moveToNext());
		}
		db.close();
		listView.setAdapter(new SimpleAdapter(this, rows, R.layout.list_item,
				from, to));
	}

	public void updateFreeSpace() {
		if (sdIsMounted) {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
					.getPath());
			double availableSize = (double) stat.getAvailableBlocks()
					* (double) stat.getBlockSize();
			// One binary gigabyte equals 1,073,741,824 bytes.
			// double gigaAvailable = sdAvailSize / 1073741824;
			double mbAvailable = availableSize / 1048576;

			freeSpaceView.setText("There is " + (int) mbAvailable
					+ "MB of space left.");
		}
	}
}