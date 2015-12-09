package de.rsp.wdntxml.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Models one Synset of the wordnet.
 * @author Robert Seidler
 *
 */
public class Synset{

	private String iD;
	private List<Word> words;
	private String definition;
	private String pos;
	private List<String> example;
	private List<Relation> relations;
	
	/**
	 * Constructor
	 * @param iD identification number of this synset, in optimal case, comparable to princton wordnet.
	 * @param partOfSpeech the part of speech of this synset, in 1 letter symbols.
	 */
	public Synset(String iD, String partOfSpeech){
		this.iD = iD;
		this.pos = partOfSpeech;
		words = new ArrayList<Word>();
		example = new ArrayList<String>();
		relations = new ArrayList<Relation>();
//		this.definition = new Definition();
//		this.example = new Definition();
	}
	
	/**
	 * Constructor
	 * @param iD identification number of this synset, in optimal case, comparable to princton wordnet.
	 */
	public Synset(String iD){

		this.iD = iD;
		words = new ArrayList<Word>();
		example = new ArrayList<String>();
		relations = new ArrayList<Relation>();
		
	}

	/**
	 * Setter for pos
	 * @param pos partOfSpeech the part of speech of this synset, in 1 letter symbols.
	 */
	public void setPos(String pos) {
		this.pos = pos;
	}

	/**
	 * Getter for pos
	 * @return partOfSpeech the part of speech of this synset, in 1 letter symbols.
	 */
	public String getPos() {
		return pos;
	}

	/**
	 * Adds one word to this synset.
	 * @param word the word to be added.
	 */
	public void addWord(Word word) {
		this.words.add(word);
	}
	
	/**
	 * Getter for words.
	 * @return all the words that are part of the synset.
	 */
	public List<Word> getWords() {
		return words;
	}

	/**
	 * Getter for ID
	 * @return identification number of this synset, in optimal case, comparable to princton wordnet.
	 */
	public String getiD() {
		return iD;
	}
	
	/**
	 * Adds one relation to this synset.
	 * @param type relation type, like hyponym or synonym (hypo, syno).
	 * @param target synset that this relation relates to.
	 */
	public void addRelation(String type, String target){
		this.relations.add(new Relation(type, target));
	}
	
	/**
	 * Adds one example to this synset.
	 * @param exmpl example for how to use words in this synset.
	 */
	public void addExample(String exmpl){
		this.example.add(exmpl);
	}
	
	/**
	 * Setter for definition.
	 * @param def meaning of this synset.
	 */
	public void setDefinition(String def){
		this.definition = def;
	}
	
	/**
	 * Getter for definition.
	 * @return meaning of this synset.
	 */
	public String getDefinition(){
		return definition;
	}
	
	/**
	 * Getter for example.
	 * @return list of all examples of this synset.
	 */
	public List<String> getExample(){
		return example;
	}
	
	/**
	 * Getter for relations.
	 * @return list of all relations in this synset.
	 */
	public List<Relation> getRelations(){
		return relations;
	}
}
