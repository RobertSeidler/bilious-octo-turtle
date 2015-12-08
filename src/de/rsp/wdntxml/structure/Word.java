package de.rsp.wdntxml.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Word of a language
 * 
 * @author Robert Seidler
 *
 */
public class Word {

	private String wordID;
	private String partOfSpeech;
	private String writtenForm;
	private String lang;
	private List<String> synsetIDs;

	public Word(String wordId, String writtenForm, String langIso, String pos) {
		this.writtenForm = writtenForm;
		this.lang = langIso;
		this.partOfSpeech = pos;
		this.wordID = wordId;
		synsetIDs = new ArrayList<String>();
	}

	public Word(String wordId, String writtenForm, String langIso) {
		this.writtenForm = writtenForm;
		this.lang = langIso;
		this.wordID = wordId;
		synsetIDs = new ArrayList<String>();
	}

	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	public String getWordID() {
		return wordID;
	}

	public String getWrittenForm() {
		return writtenForm;
	}

	public String getLang() {
		return lang;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void addSynsetID(String iD) {
		synsetIDs.add(iD);
	}

	public List<String> getSynsetIDs() {
		return synsetIDs;
	}

	@Override
	public String toString() {
		return writtenForm == null? "_EMPTY_" : writtenForm;
	}
}