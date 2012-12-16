package cmu.mobile.lab;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SongList extends ListActivity {

	// Player object
	DJPlayer playa;	
	
    /** Event Overrides */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songlist);
        
        // Load DJPlayer
        playa = DJPlayer.getInstance(SongList.this);
		
		// Load list of songs
        SongListAdapter songList = new SongListAdapter(this, R.layout.songlistitem, playa.getSongs());
		setListAdapter(songList);
    }
    
    /*** Event Overrides */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	
    	// play song at list position
        playa.playSong(position);
        
        // return to the player view
        this.finish();
    }
    
    /*** List Adapter */
    private class SongListAdapter extends ArrayAdapter<Song> {

    	// base array list
    	private ArrayList<Song> items;

    	// constructor
        public SongListAdapter(Context context, int textViewResourceId, ArrayList<Song> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }

        // called when adapter is set
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	
        	// Load layout inflater
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.songlistitem, null);
            }
            
            // load song object
            Song song = items.get(position);
            
            // load data into view
            if (song != null) {
            	
            	// album art
            	
            	
            	// title 
                TextView songListItemTitle = (TextView) convertView.findViewById(R.id.songListItemTitle);
                if (songListItemTitle != null) {
                	songListItemTitle.setText(song.getTitle());
                	}
                
                // artist
                TextView songListItemArtist = (TextView) convertView.findViewById(R.id.songListItemArtist);
                if(songListItemArtist != null){
                	songListItemArtist.setText(song.getAlbum());
                }
            }
            return convertView;
        }
    }
}
