package de.rsp.wdntxml.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a Word of a language
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

	/**
	 * Constructor
	 * @param wordId ID of this word, specified in the corresponding source; generated if non given.
	 * @param writtenForm the word.
	 * @param langIso language of this word. //TODO unnecessary
	 * @param pos the part of speech of this synset, in 1 letter symbols.
	 */
	public Word(String wordId, String writtenForm, String langIso, String pos) {
		this.writtenForm = writtenForm;
		this.lang = langIso;
		this.partOfSpeech = pos;
		this.wordID = wordId;
		synsetIDs = new ArrayList<String>();
	}
	
	/**
	 * Constructor
	 * @param wordId ID of this word, specified in the corresponding source; generated if non given.
	 * @param writtenForm the word.
	 * @param langIso language of this word. //TODO unnecessary
	 */
	public Word(String wordId, String writtenForm, String langIso) {
		this.writtenForm = writtenForm;
		this.lang = langIso;
		this.wordID = wordId;
		synsetIDs = new ArrayList<String>();
	}

	/**
	 * Setter for partOfSpeech.
	 * @param partOfSpeech the part of speech of this synset, in 1 letter symbols.
	 */
	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	/**
	 * Getter for wordID.
	 * @return ID of this word, specified in the corresponding source; generated if non given.
	 */
	public String getWordID() {
		return wordID;
	}

	/**
	 * Getter for writtenForm.
	 * @return the word.
	 */
	public String getWrittenForm() {
		return writtenForm;
	}

	/**
	 * Getter for lang.
	 * @return language of this word.
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * Getter for partOfSpeech.
	 * @return the part of speech of this synset, in 1 letter symbols.
	 */
	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	/**
	 * Adds a SynsetID, of a synset this word is in. only used by some parsers. 
	 * @param iD
	 */
	public void addSynsetID(String iD) {
		synsetIDs.add(iD);
	}

	/**
	 * Getter for synsetIDs, only used by some parsers.
	 * @return a list of all the synset IDs this word is in.
	 */
	public List<String> getSynsetIDs() {
		return synsetIDs;
	}

	@Override
	public String toString() {
		return writtenForm == null? "_EMPTY_" : writtenForm;
	}
}