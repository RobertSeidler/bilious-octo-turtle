package de.rsp.wdntxml.langspec;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.rsp.wdntxml.fx.Main;
import de.rsp.wdntxml.structure.Synset;
import de.rsp.wdntxml.structure.Word;
import de.rsp.wdntxml.structure.Wordnet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Parser for my own xml format.
 * 
 * @author Robert Seidler
 *
 */
public class RspXmlWordnetParser extends WordnetParser {

	private String description;

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	protected Wordnet call() throws Exception {

		System.out.println("started " + getSourcePath() + " parsing.");
		long time = System.currentTimeMillis();

		SAXBuilder jdomBuilder = new SAXBuilder();
		jdomBuilder.setFeature("http://xml.org/sax/features/validation", false);
		jdomBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		jdomBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		Document doc;

		double progress = 0.0;

		try {

			doc = jdomBuilder.build(getSourcePath());

			Element lexicon = doc.getRootElement();

			setLanguage(lexicon.getAttributeValue("LANG"));

			ObservableList<Synset> synsetList = FXCollections.observableArrayList();

			List<Element> synsets = lexicon.getChildren();

			updateProgress(0, 1);

			int maxProgress = synsets.size();

			for (Element synset : synsets) {

				if (isCancelled())
					break;

				Synset syn = new Synset(synset.getAttributeValue("ID"), synset.getAttributeValue("POS"));

				for (Element word : synset.getChild("Words").getChildren()) {

					syn.addWord(new Word(word.getAttributeValue("ID"), word.getText(), getLanguage()));
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
			}

			System.out.println(
					"finished " + getSourcePath() + " in " + (System.currentTimeMillis() - time) / 1000 + " sec");

			String relMapFile = "cfg/relationMappingOMW.cfg";

			return new Wordnet(getLanguage(), synsetList, getSourcePath(), relMapFile);

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
