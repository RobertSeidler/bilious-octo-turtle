package de.rsp.wdntxml.langspec;

import de.rsp.wdntxml.structure.Wordnet;
import javafx.concurrent.Task;

public abstract class WordnetParser extends Task<Wordnet> {

	private String sourcePath;
	private String lang;
	
	public String getLanguage(){
		
		return lang;
	}
	
	public void setLanguage(String lang){
		
		this.lang = lang;
	}

	public void setSourcePath(String sourcePath){
		
		this.sourcePath = sourcePath;
	}

	public abstract String getDescription();

	public String getSourcePath(){
		
		return sourcePath;
	}
	
	/**
	 * in case i don't want to use multi threading 
	 * @return
	 */
	public Wordnet callCall(){
		//TODO find better workaround 
		try {
			return call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
