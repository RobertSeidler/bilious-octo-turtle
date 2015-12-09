package de.rsp.wdntxml.structure;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Models a Wordnet of one Language.
 * @author Robert Seidler
 *
 */
public class Wordnet {

	
	private String lang;
	
	private String sourcePath;
	
	private ObservableList<Synset> synsetList = FXCollections.observableArrayList(new ArrayList<Synset>());
	
	private String pathToRelationMapping;
	
	/**
	 * Constructor
	 * @param lang language of this wordnet.
	 * @param synsetList list of all synsets in this wordnet.
	 * @param source path to the source of this wordnet.
	 * @param pathRelMap path to the relation mapping this wordnet uses.
	 */
	public Wordnet(String lang, ObservableList<Synset> synsetList, String source, String pathRelMap){
		this.lang = lang;
		this.synsetList = synsetList;
		this.pathToRelationMapping = pathRelMap;
		this.sourcePath = source;
	}
	
	/**
	 * Getter for pathToRelationMapping.
	 * @return path to the relation mapping this wordnet uses.
	 */
	public String getPathToRelationMapping() {
		return pathToRelationMapping;
	}

	/**
	 * Adds one Synset to the list of all synsets.
	 * @param syn synset to add.
	 */
	public void addSynset(Synset syn){
		synsetList.add(syn);
	}
	
	/**
	 * Getter for sourcePath
	 * @return path to the source of this wordnet.
	 */
	public String getSourcePath(){
		return sourcePath;
	}
	
	/**
	 * Getter for lang
	 * @return language of this wordnet.
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * Getter for synsetList
	 * @return synsetList list of all synsets in this wordnet.
	 */
	public ObservableList<Synset> getSynsetList() {
		return synsetList;
	}	
}
