package cmu.mobile.lab;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
	

public class DJPlayer {
	
	/*** Member Variables */
	
	// singleton instance
	private static DJPlayer instance;
	
	// Context object
	private Context context;
	
	// song mamagement stuff
	private static final String SD_MEDIA_PATH = new String("/sdcard/");
	private ArrayList<Song> songs = new ArrayList<Song>();
	private MediaPlayer mp;
	private int currentPosition;
	private boolean paused;
	
	// listeners
	private OnPlayerActionListener playerActionListener; 
	
	/*** public accessors */
	
	// song list
	public ArrayList<Song> getSongs() {
		return songs;
	}
	public Song getSong() {
		if (songs != null)
		{
			return songs.get(currentPosition);
		}
		else
		{
			return null;
		}
	}
	
	/*** singleton implementation */
	
	// private constructor
	private DJPlayer(Context con) {
		
		// setup player
		mp = new MediaPlayer();
		mp.setOnCompletionListener(onSongComplete);
		
		// save context
		context = con;
		
		// load songs from storage
		loadSongsFromSDCard();
	}
	
	// instance accessor
	public static synchronized DJPlayer getInstance(Context con) {
		
		// check to see if one is already instantiated
		if (instance == null) {
			
			// instantiate only instance
			instance = new DJPlayer(con);
		}
		
		//return singleton instance
		return instance;
	}

	/*** Song Library Management*/
	
	// load songs from the SD card
	private void loadSongsFromSDCard() {

    	// load file
    	File home = Environment.getExternalStorageDirectory();
    	
    	// check location for playable songs
    	if (home.listFiles(new SongFileFilter()).length > 0) {
    		
    		// loadany config file
    		ArrayList<XMLSong> xmlSongs = null;
    		for (File xmlFile : home.listFiles(new XMLFileFilter())) {

				// load XML file if it is named this
    			if (xmlFile.getName() == "song_list.xml")
    			{
    				try
    				{
    					// create a SAX parser to read our XML
    					SAXParserFactory spf = SAXParserFactory.newInstance();
    					SAXParser sp = spf.newSAXParser();
    					
    					// get the XML Reader
    					XMLReader xr = sp.getXMLReader();
    					
    					// get our content handler to read the file
    					XMLSongListHandler xmlSongHandler = new XMLSongListHandler();
    					xr.setContentHandler(xmlSongHandler);
    					
    					// parse the file
    					xr.parse(new InputSource(new FileReader(xmlFile)));					
    						
    					// assign the data
    					xmlSongs =	xmlSongHandler.getSongs();		
    				}
    				catch (SAXException e)
    				{
    					// do nothing, file doesn't load
    				}
    				catch (ParserConfigurationException e)
    				{
    					// do nothing, file doesn't load
    				}
    				catch (IOException e)
    				{
    					// do nothing, file doesn't load
    				}
    			}
    		}
    		
    		// load playable song file names
    		for (File file : home.listFiles(new SongFileFilter())) {
    			
    			// add songs to arrayList
    			try {
    				
    				// get BPM from config file, if present
    				if (xmlSongs != null)
    				{
    					
    					// look for song with matching name
    					for (XMLSong song : xmlSongs) {
    						
    						// test name
    						if (song.getName() == file.getName())
    						{
    							// add song with configred BPM from XML
    	            			songs.add(new Song(AudioFileIO.read(file), file.getName(), null));		
    						}
    					}
    				}
    				else
    				{
    					
    					// add song with BPM, will default to ID3 tag read
            			songs.add(new Song(AudioFileIO.read(file), file.getName(), null));	
    				}
    			}
    			catch (InvalidAudioFrameException e){
					e.printStackTrace();	
    			} 
    			catch (CannotReadException e) {
					e.printStackTrace();
				} 
    			catch (IOException e) {
					e.printStackTrace();
				} 
    			catch (TagException e) {
					e.printStackTrace();
				} 
    			catch (ReadOnlyFileException e) {
					e.printStackTrace();
				}
    		}
    	}
	}
	
	// Audio File Filter for Audio Files
	class SongFileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			
			// mp3's only right now, but this filter can get more files with the || operator
            return (name.endsWith(".mp3"));
		}
	}

	// XML filter for BPM config
	class XMLFileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			
			// XML filter
            return (name.endsWith(".xml"));
		}
	}
	
	/*** Public Player Song Functions */
	
	// play by position in list
	public void playSong(int songPosition) {
		
		// set desired position
		currentPosition = songPosition;
		
		// play
		playerPlay();

		// notify song change
		songChange();
	}

	// play song by BPM string
	public void playSongByBPM(String bpm) {
		
		// get tempo from string
		Song song = playSongByTempo(Song.getTempoFromBPM(bpm));
		
		// set position
//		currentPosition = song.position;

		// notify song change
		songChange();
		
		// play song
		playerPlay();
	}
	
	// play song by BPM int
	public void playSongByBPM(int bpm) {
	
		// get tempo from int
		Song song = playSongByTempo(Song.getTempoFromBPM(bpm));
		
		// set position
//		currentPosition = song.position;
		
		// play song
		playerPlay();
	}
	
	public Song playSongByTempo(int tempo) {
		
		// look for songs with matching tempo
		for (Song song : songs) {
		
			// test tempo match
			if (song.getTempo() == tempo) {
				return song;
			}
		}
		
		// return the first song if none is found
		// TODO
		return null;
	}

	// direct the MediaPlayer to play the next song
	public void next() {
		
		// check to see if we're over
        if (currentPosition + 1 >= songs.size()) {
        	
            // last song, set to repeat from start
            currentPosition = 0;
        } else {

    		// increment position
    		++currentPosition;
        }
    	
        // play song, if currently playing
        if (mp.isPlaying())
        {
            playerPlay();	
        }
		
		// notify song change
		songChange();
	}
	
	// direct the MediaPlayer to start the song over, or go back one
	public void back() {

    	// check if song progress is more than 3 seconds
    	if (mp.getCurrentPosition() > 3000 ) {
    		
    		// start song over, will auto-play if already playing
    		mp.seekTo(0);
    		
    	} else {
    		
    		// check to see if we're at first song
            if (currentPosition == 0) {
            	
            	// set to last song
            	currentPosition = songs.size() - 1;
            	
            } else {

        		// move to previous song
        		--currentPosition;	
            }
            
    		// notify song change
    		songChange();

            // play song, if currently playing
            if (mp.isPlaying())
            {
                playerPlay();	
            }
    	}
	}
	
	// play/pause
	public void playPause() {
	
		// determine player status
		if (mp.isPlaying()) {
			
			// pause
			mp.pause();
			
		} else if (paused){
			
			// resume
			mp.start();
		} else {

			// play current song
			playerPlay();

    		// notify song change
    		songChange();
		}
	}
	
	/*** Private Player Song Functions */

	// direct the MediaPlayer to play a song at the current position
	private void playerPlay() {
	    try {
	    	
	    	// setup player
            mp.reset();
            mp.setDataSource(SD_MEDIA_PATH + songs.get(currentPosition).getFileName());
            mp.prepare();
            mp.start();
            
	    } catch (IOException e) {
//	            Log.v(getString(R.string.app_name), e.getMessage());
	    }
	}
	
	// notify listener of song change
	private void songChange() {

        // notify listener of new song
        if (playerActionListener != null){
            playerActionListener.onSongChange(this, songs.get(currentPosition));	
        }
	}
	
	/*** Player Event Handling */
	private OnCompletionListener onSongComplete = new OnCompletionListener() {
		
		// on complete
		public void onCompletion(MediaPlayer arg0) {
			next();
		}
	};
	
	/*** Public Player Listener Set Methods */
	public void setOnPlayerActionListener(OnPlayerActionListener listener) {
		
		// assign listener object
		playerActionListener = listener;
	}
	
	/*** Public Player Listener Interfaces */
	public interface OnPlayerActionListener {		
		
		// song begins
		public abstract void onSongChange(DJPlayer playa, Song song);
	}
	
}