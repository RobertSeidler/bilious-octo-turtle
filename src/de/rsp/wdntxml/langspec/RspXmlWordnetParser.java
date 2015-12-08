package de.rsp.wdntxml.langspec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import de.rsp.wdntxml.structure.Synset;
import de.rsp.wdntxml.structure.Word;
import de.rsp.wdntxml.structure.Wordnet;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RspXmlWordnetParser extends WordnetParser {

	private String sourcePath;

	private String lang;

	private String description;

	@Override
	public void setSourcePath(String sourcePath) {

		this.sourcePath = sourcePath;
	}

	@Override
	public String getDescription() {

		return description;
	}
	
	/**
	 * in case i don't want to use multi threading
	 * @return
	 */
	public Wordnet callCall(){
		
		try {
			return call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected Wordnet call() throws Exception {

		System.out.println("started " + sourcePath + " parsing.");
		long time = System.currentTimeMillis();
		
		SAXBuilder jdomBuilder = new SAXBuilder();
		jdomBuilder.setFeature("http://xml.org/sax/features/validation", false);
		jdomBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		jdomBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		Document doc;

		
		double progress = 0.0;

		// Lexicon lex;

		try {

//			long time = System.currentTimeMillis();
			doc = jdomBuilder.build(sourcePath);
			
//			System.out.println((System.currentTimeMillis() - time) / 1000 + " sec");

			Element lexicon = doc.getRootElement();

			// lex = new Lexicon(lexicon.getAttributeValue("LANG"));
			lang = lexicon.getAttributeValue("LANG");

			ObservableList<Synset> synsetList = FXCollections.observableArrayList();
						
			List<Element> synsets = lexicon.getChildren();
			
			updateProgress(0, 1);
			
			int maxProgress = synsets.size();
			
			for (Element synset : synsets) {

				if (isCancelled())
					break;

				Synset syn = new Synset(synset.getAttributeValue("ID"), synset.getAttributeValue("POS"));

				for (Element word : synset.getChild("Words").getChildren()) {

					syn.addWord(new Word(word.getAttributeValue("ID"), word.getText(), lang));
				}

				for (Element relation : synset.getChild("Relations").getChildren()) {

					syn.addRelation(relation.getAttributeValue("TYPE"), relation.getText());
				}

				for (Element examples : synset.getChild("Examples").getChildren()) {

					syn.addExample(examples.getText());
				}

				syn.setDefinition(synset.getChildText("Definition"));
				progress += (1.0 / (double) maxProgress);
				updateProgress(progress, 1.0);
				
				synsetList.add(syn);

//				Platform.runLater(new Runnable() {
//
//					@Override
//					public void run() {
//
////						partialResults.get().add(syn);
//						getPartialResults().getValue().addSynset(syn);
//					}
//				});
				// lex.addSynset(syn);
			}

//			return getPartialResults().getValue();
			System.out.println("finished " + sourcePath + " in " + (System.currentTimeMillis() - time) / 1000 + " sec");

			return new Wordnet(lang, synsetList, sourcePath, "D:\\Programmierung\\Java Mars Programme\\WdntFx\\cfg\\relationMappingOMW.cfg");

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String getLanguage() {

		return lang;
	}

	@Override
	public String getSourcePath() {

		return sourcePath;
	}

}
