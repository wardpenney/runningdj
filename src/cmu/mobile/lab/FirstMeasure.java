package cmu.mobile.lab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FirstMeasure extends Activity implements AccelerometerListener {
	
	private static Context CONTEXT;
	private ProgressBar mProgress;
    private int mProgressStatus = 0;
    
    private Handler mHandler = new Handler();
    private Thread timer;
    private float currentX;
    private float currentY;
    private float currentZ;
    private float onePastY = 0;
    private float twoPastY = 0;
    private float zeroPastY = 0;
    private int peakCount = 0;
    private int bpm;
    //private TextView peakCountView;
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstmeasure);
        CONTEXT = this;
        
        // wire up button listener
    	findViewById(R.id.RunningManLogo2).setOnClickListener(listenerOnClickLogo);
    	findViewById(R.id.MeasuringCancel).setOnClickListener(listenerOnClickCancel);
    	// peakCountView = (TextView)findViewById(R.id.PeakCount);
    	
    	mProgress = (ProgressBar) findViewById(R.id.MeasuringProgressBar);

        // Start lengthy operation in a background thread
        timer = new Thread (new Runnable() {
           public void run() {
                while (mProgressStatus < 100) {
                    
                	// Update the progress bar
                    mProgress.setProgress(mProgressStatus);
                    
                    try {
                    	mProgressStatus += 1;
                    	Thread.sleep(100);
                    	
                    	twoPastY = onePastY;
                    	onePastY = zeroPastY;
                    	zeroPastY = currentY;
                    	
                    	if (isPeak(twoPastY, onePastY, zeroPastY)) {
                    		peakCount += 1;
                    	}
                    	
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                bpm = peakCount*6;
                // create Intent for Automatic View
           		Intent i = new Intent(FirstMeasure.this, Player.class);
           		i.putExtra("peakCount", peakCount);
           		i.putExtra("bpm", bpm);
           		startActivity(i);
            }
        });
        
        timer.start();                   	
    }
    
    private boolean isPeak (float y1, float y2, float y3) {
    	boolean isPeak = false;
    	float deltaY1;
    	float deltaY2;
    	

		deltaY1 = y1 - y2;
		deltaY2 = y3 - y2;
    	if (((deltaY1 > 0) && (deltaY2 > 0)) || ((deltaY1 < 0) && (deltaY2 < 0))) {
    		isPeak = true;
    	}
    	return isPeak;
    }
    
    protected void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported()) {
            AccelerometerManager.startListening(this);
        }
    }
 
    protected void onDestroy() {
        super.onDestroy();
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        
        if ((timer != null) && timer.isAlive()) {
        	timer.interrupt();
        }
    }
 
    public static Context getContext() {
        return CONTEXT;
    }
 
    /**
     * onShake callback
     */
    public void onShake(float force) {
        Toast.makeText(this, "Phone shaked : " + force, 1000).show();
    }
 
    /**
     * onAccelerationChanged callback
     */
    public void onAccelerationChanged(float x, float y, float z) {
        ((TextView) findViewById(R.id.x)).setText(String.valueOf(x));
        ((TextView) findViewById(R.id.y)).setText(String.valueOf(y));
        ((TextView) findViewById(R.id.z)).setText(String.valueOf(z));
        
        currentX = x;
        currentY = y;
        currentZ = z;
    }

	/** Button OnClick Listeners */
	private OnClickListener listenerOnClickLogo = new View.OnClickListener() {
	
		@Override
		public void onClick(View v) {
	
			// create Intent for Automatic View
			Intent i = new Intent(FirstMeasure.this, Player.class);
			startActivity(i);
		}
	};
	
	/** Button OnClick Listeners */
	private OnClickListener listenerOnClickCancel = new View.OnClickListener() {
	
		@Override
		public void onClick(View v) {
			FirstMeasure.this.finish();
		}
	};
}
