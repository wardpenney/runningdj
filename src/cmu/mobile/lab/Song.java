package cmu.mobile.lab;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;

import android.content.Context;

public class Song {
	
	// Enums
	public static final int TEMPO_STROLL = 0;
	public static final int TEMPO_WALK = 1;
	public static final int TEMPO_LIGHT_JOG = 2;
	public static final int TEMPO_JOG = 3;
	public static final int TEMPO_RUN = 4;
	public static final int TEMPO_SPRINT = 5;
	public static final int TEMPO_UNKNOWN = 6;
	
	// Member level variables
	private String artist;
	private String album;
	private String title;
	private String track;
	private String trackTotal;
	private TagField coverArt;
	private String bpm;
	private String fileName;
	
	/** Constructor */
	public Song(AudioFile songFile, String file, String bpmConfig) {
		
		// load tag
		Tag t = songFile.getTag();
		
		// load values
		fileName = file;
		artist = t.getFirst(FieldKey.ARTIST);
		album = t.getFirst(FieldKey.ALBUM);
		title = t.getFirst(FieldKey.TITLE);
		track = t.getFirst(FieldKey.TRACK);
		trackTotal = t.getFirst(FieldKey.TRACK_TOTAL);
		coverArt = t.getFirstField(FieldKey.COVER_ART);
		
		// if BPM is provided, use, otherwise look at ID3
		if (bpmConfig != null) 
		{
			bpm = bpmConfig;
		}
		else
		{
			// use ID3 or config for BPM
			bpm = t.getFirst(FieldKey.BPM);	
		}
	}
	
	/** Public Accessors */
	public String getFileName() {
		return fileName;
	}
	public String getArtist() {
		return artist;
	}
	public String getAlbum() {
		return album;
	}
	public String getTitle() {
		if (title == null || title.trim().length() == 0)
		{
			// return filename if ID3 tag doesn't have title
			return fileName;
		}
		else 
		{
			return title;	
		}
	}
	public String getTrack() {
		return track;
	}
	public String getTrackTotal() {
		return trackTotal;
	}
	public TagField getCoverArt() {
		return coverArt;
	}
	public String getBPM() {
		return bpm;
	}
	public int getTempo() {
		return Song.getTempoFromBPM(bpm);
	}
	
	/** Public Methods */

	
	/** Static Methods */
	
	// translate tempo to string
	public static String translateTempo(Context context, int tempo)
	{
		switch (tempo) {
			case TEMPO_STROLL : { return  context.getResources().getText(R.string.TEMPO_STROLL).toString(); }
			case TEMPO_LIGHT_JOG : { return context.getResources().getText(R.string.TEMPO_LIGHT_JOG).toString(); }
			case TEMPO_JOG : { return context.getResources().getText(R.string.TEMPO_JOG).toString(); }
			case TEMPO_RUN : { return context.getResources().getText(R.string.TEMPO_RUN).toString(); }
			case TEMPO_SPRINT : { return context.getResources().getText(R.string.TEMPO_SPRINT).toString(); }
			case TEMPO_UNKNOWN : { return context.getResources().getText(R.string.TEMPO_UNKNOWN).toString(); }
			default : { return context.getResources().getText(R.string.TEMPO_UNKNOWN).toString(); }
		}
		
	}
	
	// take BPM as a string
	public static int getTempoFromBPM(String bpm) {
		
		// cast to an int from string
		try
		{
	
			// convert the string to an int
			return Song.getTempoFromBPM(Integer.parseInt(bpm.trim()));
		}
		catch (NumberFormatException nfe)
		{	

			//
			return TEMPO_UNKNOWN;
		}
	}
	
	// take BPM as an int
	public static int getTempoFromBPM(int bpm) {
		
		// STROLL less than 60
		if (bpm < 60) {
			return TEMPO_STROLL;
		}
		
		// WALK between 60 and 70, or between 120 and 140
		else if ((bpm >= 60 && bpm < 70) || (bpm >= 120 && bpm < 140)) {
			return TEMPO_WALK;
		}
		
		// LIGHT_JOG between 70 and 80, or between 140 and 160
		else if ((bpm >= 70 && bpm < 80) || (bpm >= 140 && bpm < 160)) {
			return TEMPO_LIGHT_JOG;
		}
	
		// JOG between 80 and 90, or between 160 and 180
		else if ((bpm >= 80 && bpm < 90) || (bpm >= 160 && bpm < 180)) {
			return TEMPO_JOG;
		}
	
		// RUN between 90 and 100, or between 180 and 200
		else if ((bpm >= 90 && bpm < 100) || (bpm >= 180 && bpm < 200)) {
			return TEMPO_RUN;
		}
	
		// SPRINT between 100 and 120
		else if (bpm >= 100 && bpm < 120) {
			return TEMPO_SPRINT;
		}    	
		
		// UNKNOWN
		else {
			return TEMPO_UNKNOWN;
		}
	}
	
	/** Private Methods */
}
