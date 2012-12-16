package cmu.mobile.lab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cmu.mobile.lab.DJPlayer.OnPlayerActionListener;

public class Player extends Activity {
	
	/** Member Level Variables */
	
	// MusicPlayer
	DJPlayer playa; 

    /** Overrides */

	// initialization
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        
        TextView averageBPM = (TextView)findViewById(R.id.AverageBPMNum);
        TextView currentBPM = (TextView)findViewById(R.id.CurrentBPMNum);
        
        // get the bpm
        Bundle request = this.getIntent().getExtras();
        
        if ((request != null) && request.containsKey("bpm")) {
            averageBPM.setText(String.valueOf(request.getInt("bpm")));
            if (request.containsKey("peakCount")) {
            	currentBPM.setText(String.valueOf(request.getInt("peakCount")));
            }
        }
        
        // load player
        playa = DJPlayer.getInstance(Player.this);
        
        // set player listener
        playa.setOnPlayerActionListener(playerActionListener);
        
        // set button click listeners
        findViewById(R.id.playerBack).setOnClickListener(backClick);
        findViewById(R.id.playerPausePlay).setOnClickListener(playPauseClick);
        findViewById(R.id.playerNext).setOnClickListener(nextClick);
        
        // load current song info
        updateSongDisplay(playa.getSong());
    }
    
    // options menu inflate
	@Override
    public boolean onCreateOptionsMenu (Menu menu) {
		
		// load XML menu
        MenuInflater inflater = getMenuInflater();
        
        // inflate to view
        inflater.inflate(R.menu.playermenu, menu);
        
        return true;
    }
    
    // menu item select
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
    	
    	// take appropriate menu action
		switch (item.getItemId()) {
		
		case R.id.itemSongList :

			// Go to song list activity
			Intent i = new Intent(Player.this, SongList.class);
			startActivity(i);
			
			break;
		}
		
		return true;
	}
    
    /** Button Listeners */
    
    // back button
    private OnClickListener backClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			playa.back();
		}
	};
	
	// play/pause
	private OnClickListener playPauseClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {

			// play/pause
			playa.playPause();
		}
	}; 
		
	// next
	private OnClickListener nextClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			// next song
			playa.next();
		}
		
	};
    
    /** Player Listeners */
    private OnPlayerActionListener playerActionListener = new OnPlayerActionListener() {

		@Override
		public void onSongChange(DJPlayer playa, Song song) {

			// Update screen
			updateSongDisplay(song);
		}	
    };

    /** Private Methods */
    private void updateSongDisplay(Song song) {
    	
    	// update info
    	if (song != null) {
        	
        	// album art
        	
        	// title 
            TextView playerTitle = (TextView)findViewById(R.id.playerTitle);
            playerTitle.setText(song.getTitle());
            
            // artist
            TextView playerArtist = (TextView)findViewById(R.id.playerArtist);
            playerArtist.setText(song.getAlbum());
            
            // tempo
            TextView playerTempo = (TextView)findViewById(R.id.playerTempo);
            playerTempo.setText(Song.translateTempo(Player.this, song.getTempo()));
        }
    }
}