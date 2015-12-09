package de.rsp.wdntxml.langspec;

import de.rsp.wdntxml.structure.Wordnet;
import javafx.concurrent.Task;

/**
 * Pattern for a Wordnet Parser. 
 * @author Robert Seidler 
 *
 */
public abstract class WordnetParser extends Task<Wordnet> {

	private String sourcePath;
	
	private String lang;
	
	/**
	 * Getter for Language.
	 * @return language of the corresponding wordnet.
	 */
	public String getLanguage(){
		
		return lang;
	}
	
	/**
	 * Setter for Language.
	 * @param lang language of the corresponding wordnet.
	 */
	public void setLanguage(String lang){
		
		this.lang = lang;
	}

	/**
	 * Setter for sourcePath.
	 * @param sourcePath path to source file(s) to parse.
	 */
	public void setSourcePath(String sourcePath){
		
		this.sourcePath = sourcePath;
	}

	/**
	 * Getter for description.
	 * @return description for what that parser parses and everything relevant for this parser.
	 */
	public abstract String getDescription();

	/**
	 * Getter for SourcePath.
	 * @return path to source file(s) to parse.
	 */
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
