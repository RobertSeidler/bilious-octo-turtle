package de.rsp.wdntxml.structure;

import java.util.ArrayList;
import java.util.List;

import de.rsp.wdntxml.tools.SynsetList;

public class Lexicon {

	private List<Word> wordList;
	private List<Synset> synsetList;
	private String lang;

	public Lexicon(String lang) {
		wordList = new ArrayList<Word>();
		synsetList = new SynsetList();
		this.lang = lang;
	}

	public String getLang() {
		return lang;
	}

	public List<Word> getWordList() {
		return wordList;
	}

	public List<Synset> getSynsetList() {
		return synsetList;
	}

	public void addWord(Word word) {
		wordList.add(word);
	}

	public void addSynset(Synset synset) {
		synsetList.add(synset);
	}

	/**
	 * in case Synsets don't have words assigned directly, this function finds
	 * and adds them (hopefully). Careful: function may take a huge amount of
	 * time!
	 */
	public void connectWordsToSynsets() {
		System.out.println("Starte: connectWordsToSynsets.\n");
		for (Word w : wordList) {
			List<String> ids = w.getSynsetIDs();

			for (String s : ids) {
				getSynsetById(s).addWord(w);
			}
		}
	}

	/**
	 * returns the Synset with given ID, or null if there is non with that ID.
	 * 
	 * @param id
	 *            - ID from Synset you are looking for
	 * @return Synset to ID or null if there is non
	 */
	public Synset getSynsetById(String id) {

		for (Synset syn : synsetList) {
			if (syn.getiD().equals(id))
				return syn;
		}

		return null;
	}

	/**
	 * some Wordnets may have a lot of Synsets that don't have any usefull
	 * additional information in them. This procedure removes them from the
	 * Lexicon. Written for OpenMultilingualWordnet.
	 */
	public void deleteWorthlessSynsets() {

		List<Synset> newSynsetList = new ArrayList<Synset>();
		
		for (Synset syn : synsetList) {
			if (!(syn.getWords().size() == 0 && syn.getDefinition() == null && syn.getExample().size() == 0)) {
				newSynsetList.add(syn);
			}
		}
		synsetList = newSynsetList;
	}
}
