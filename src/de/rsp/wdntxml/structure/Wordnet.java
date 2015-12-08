package de.rsp.wdntxml.structure;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Wordnet {

	
	private String lang;
	
	private String sourcePath;
	
	private ObservableList<Synset> synsetList = FXCollections.observableArrayList(new ArrayList<Synset>());
	
	private String pathToRelationMapping;
	
	public Wordnet(String lang, ObservableList<Synset> synsetList, String source, String pathRelMap){
		this.lang = lang;
		this.synsetList = synsetList;
		this.pathToRelationMapping = pathRelMap;
	}
	
	public String getPathToRelationMapping() {
		return pathToRelationMapping;
	}

	public void addSynset(Synset syn){
		synsetList.add(syn);
	}
	
	public String getSourcePath(){
		return sourcePath;
	}
	
	public String getLang() {
		return lang;
	}

	public ObservableList<Synset> getSynsetList() {
		return synsetList;
	}

	
	
}
