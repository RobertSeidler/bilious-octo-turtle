package de.rsp.wdntxml.structure;

public class Relation {
	
	private String type;
	private String targetSynset;

	public Relation(String type, String targetSynset){
		
		this.type = type;
		this.targetSynset = targetSynset;
	}

	public String getType() {
		return type;
	}

	public String getTargetSynset() {
		return targetSynset;
	}
	
	public String toString(){
		return "{" + targetSynset + ", " + type + "}";
	}
}
