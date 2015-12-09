package de.rsp.wdntxml.langspec;

import java.io.IOException;
import java.util.ArrayList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import de.rsp.wdntxml.structure.Synset;
import de.rsp.wdntxml.structure.Word;
import de.rsp.wdntxml.structure.Wordnet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Parser for Open Multilingual Wordnet lmf-xml files.
 * 
 * @author Robert Seidler
 *
 */
public class OpenMultilingualWordnetParser extends WordnetParser {

	private final String description = "";

	private ObservableList<Synset> synsetList = FXCollections.observableArrayList();

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

		try {

			doc = jdomBuilder.build(getSourcePath());

			double progress = 0.0;
			updateProgress(0, 1.0);

			Element lexicon = doc.getRootElement().getChild("Lexicon");

			setLanguage(lexicon.getAttributeValue("language"));

			double maxSynsets = (double) lexicon.getChildren("Synset").size();

			for (Element synset : lexicon.getChildren("Synset")) {

				String unCleanId = synset.getAttributeValue("id");
				Synset syn = new Synset(cleanUpId(unCleanId), getPosFromUncleanId(unCleanId));

				Element definition = synset.getChild("Definition");

				if (definition != null) {
					syn.setDefinition(definition.getAttributeValue("gloss"));

					for (Element example : definition.getChildren()) {

						syn.addExample(example.getAttributeValue("example"));
					}
				}
				for (Element relation : synset.getChild("SynsetRelations").getChildren("SynsetRelation")) {

					syn.addRelation(relation.getAttributeValue("relType"),
							cleanUpId(relation.getAttributeValue("targets")));
				}

				if (isCancelled())
					return null;
				progress += 0.25 / maxSynsets;
				updateProgress(progress, 1.0);
				synsetList.add(syn);
			}

			double maxLexicals = (double) lexicon.getChildren("LexicalEntry").size();

			for (Element lexicalEntry : lexicon.getChildren("LexicalEntry")) {

				if (isCancelled())
					return null;
				progress += (0.75 / maxLexicals);
				updateProgress(progress, 1.0);

				String wordId = lexicalEntry.getAttributeValue("id").substring(1,
						lexicalEntry.getAttributeValue("id").length());

				for (Element sense : lexicalEntry.getChildren("Sense")) {

					getSynsetById(cleanUpId(sense.getAttributeValue("synset"))).addWord(new Word(wordId,
							lexicalEntry.getChild("Lemma").getAttributeValue("writtenForm"), getLanguage()));

				}
			}

			ObservableList<Synset> newSynsetList = FXCollections.observableArrayList(new ArrayList<Synset>());

			double maxSynsetList = synsetList.size();

			for (Synset syn : synsetList) {
				if (!(syn.getWords().size() == 0 && syn.getDefinition() == null && syn.getExample().size() == 0)) {
					newSynsetList.add(syn);
				}
				if (isCancelled())
					return null;
				progress += (0.75 / maxSynsetList);
				updateProgress(progress, 1.0);
			}

			String relMapFile = "cfg/relationMappingOMW.cfg";

			System.out.println(
					"finished " + getSourcePath() + " in " + (System.currentTimeMillis() - time) / 1000 + " sec");

			return new Wordnet(getLanguage(), newSynsetList, getSourcePath(), relMapFile);

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private String cleanUpId(String unCleanId) {

		// als-10-01785341-a

		return unCleanId.split("-")[2];
	}

	private String getPosFromUncleanId(String unCleanId) {

		return unCleanId.split("-")[3];
	}

	private Synset getSynsetById(String id) {

		for (Synset syn : synsetList) {

			if (syn.getiD().equals(id)) {
				return syn;
			}
		}

		return null;
	}
}
