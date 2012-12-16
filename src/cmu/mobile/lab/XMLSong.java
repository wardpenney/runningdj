package cmu.mobile.lab;

public class XMLSong {

	// strings
	private String name;
	private String bpm;
	
	// constructor
	public XMLSong(String NAME, String BPM)
	{
		name = NAME;
		bpm = BPM;
	}
	
	// accessors
	public String getBPM() {
		return bpm;
	}
	public String getName() {
		return name;
	}
}
