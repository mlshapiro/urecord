package cc.co.eurdev.fourtrack;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import cc.co.eurdev.fourtrack.widget.VerticalSeekBar;

public class FourTrack extends Activity {
    /** Called when the activity is first created. */
	
	LinearLayout layout;
	LinearLayout.LayoutParams params;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        layout = (LinearLayout)findViewById(R.id.linearLayout1);
        params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        
        VerticalSeekBar verticalseekbar1 = new VerticalSeekBar(this);
        VerticalSeekBar verticalseekbar2 = new VerticalSeekBar(this);
        VerticalSeekBar verticalseekbar3 = new VerticalSeekBar(this);
        VerticalSeekBar verticalseekbar4 = new VerticalSeekBar(this);
        
        
        layout.addView(verticalseekbar1, params);
        layout.addView(verticalseekbar2, params);
        layout.addView(verticalseekbar3, params);
        layout.addView(verticalseekbar4, params);
    }
}