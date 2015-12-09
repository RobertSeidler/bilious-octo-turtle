package de.rsp.wdntxml.structure;

/**
 * Models one relation in a wordnet.
 * @author Robert Seidler
 *
 */
public class Relation {
	
	private String type;
	
	private String targetSynset;

	/**
	 * Constructor
	 * @param type relation type, like hyponym or synonym (hypo, syno).
	 * @param targetSynset synset that this relation relates to.
	 */
	public Relation(String type, String targetSynset){
		
		this.type = type;
		this.targetSynset = targetSynset;
	}

	/**
	 * Getter for type.
	 * @return type relation type, like hyponym or synonym (hypo, syno).
	 */
	public String getType() {
		return type;
	}

	/**
	 * Getter for targetSynset.
	 * @return targetSynset synset that this relation relates to.
	 */
	public String getTargetSynset() {
		return targetSynset;
	}
	
	@Override
	public String toString(){
		return "{" + targetSynset + ", " + type + "}";
	}
}
