package de.rsp.wdntxml.structure;

import java.util.ArrayList;
import java.util.List;

public class Synset{

	private String iD;
	private List<Word> words;
	private String definition;
	private String pos;
	private List<String> example;
	private List<Relation> relations;
	
	public Synset(String iD, String partOfSpeech){
		this.iD = iD;
		this.pos = partOfSpeech;
		words = new ArrayList<Word>();
		example = new ArrayList<String>();
		relations = new ArrayList<Relation>();
//		this.definition = new Definition();
//		this.example = new Definition();
	}
	
	public Synset(String iD){

		this.iD = iD;
		words = new ArrayList<Word>();
		example = new ArrayList<String>();
		relations = new ArrayList<Relation>();
		
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getPos() {
		return pos;
	}

	public void addWord(Word word) {
		this.words.add(word);
	}
	
	public List<Word> getWords() {
		return words;
	}

	public String getiD() {
		return iD;
	}
	
//	public void addDefintion(String def, String lang){
//		definition.addGloss(def, lang);
//	}
//	
//	public void addExample(String exmpl, String lang){
//		example.addGloss(lang, exmpl);;
//	}
	
	public void addRelation(String type, String target){
		this.relations.add(new Relation(type, target));
	}
	
	public void addExample(String exmpl){
		this.example.add(exmpl);
	}
	
	public void setDefinition(String def){
		this.definition = def;
	}
	
	public String getDefinition(){
		return definition;
	}
	
	public List<String> getExample(){
		return example;
	}
	
	public List<Relation> getRelations(){
		return relations;
	}
}
