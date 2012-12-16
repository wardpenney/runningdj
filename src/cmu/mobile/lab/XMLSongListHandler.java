package cmu.mobile.lab;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLSongListHandler extends DefaultHandler {
	
	// data
	private ArrayList<XMLSong> songs = new ArrayList<XMLSong>();

	// public accessors
	public ArrayList<XMLSong> getSongs() {
		return songs;
	}

	// get tag
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		
		// only handle song tags
		if (localName == "song")
		{
			// load song
			songs.add(new XMLSong(atts.getValue("name"), atts.getValue("bpm")));
		}
	}
}