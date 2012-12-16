package cmu.mobile.lab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Logo extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);
        
        // wire up button listener
		findViewById(R.id.RunningManLogo).setOnClickListener(listenerOnClickLogo);
		
		// load up the player just to have it load up the ID3 tags now
		DJPlayer playa = DJPlayer.getInstance(Logo.this);
    }

	/** Button OnClick Listeners */
    private OnClickListener listenerOnClickLogo = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			// create Intent for Automatic View
			Intent i = new Intent(Logo.this, FirstMeasure.class);
			startActivity(i);
		}
	};
}
