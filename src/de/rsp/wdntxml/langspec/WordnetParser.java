package de.rsp.wdntxml.langspec;

import java.util.ArrayList;

import de.rsp.wdntxml.structure.Synset;
import de.rsp.wdntxml.structure.Wordnet;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public abstract class WordnetParser extends Task<Wordnet> {

	public abstract String getLanguage();

	public abstract void setSourcePath(String sourcePath);

	public abstract String getDescription();

	public abstract String getSourcePath();
	
	public abstract Wordnet callCall(); //TODO find better workaround

	// public abstract ObservableList<Synset> getPartialResults();
	//
	// public abstract ReadOnlyListProperty<Synset> partialResultsProperty();
	//
//	protected ReadOnlyObjectWrapper<ObservableObjectValue<Wordnet>> partialResults = new ReadOnlyObjectWrapper<ObservableObjectValue<Wordnet>>(
//			this, "partialResults", null);
			// protected ReadOnlyListWrapper<Synset> partialResults = new
			// ReadOnlyListWrapper<Synset>(
			// this, "partialResults", FXCollections.observableArrayList(new
			// ArrayList<Synset>()));

	// public final ObservableList<Synset> getPartialResults() {
	//
	// return partialResults.get();
	// }
//
//	public final ObservableObjectValue<Wordnet> getPartialResults() {
//		return partialResults.get();
//	}
//
//	// public final ReadOnlyListProperty<Synset> partialResultsProperty() {
//	//
//	// return partialResults.getReadOnlyProperty();
//	// }
//
//	public final ReadOnlyObjectProperty<ObservableObjectValue<Wordnet>> partialResultsProperty() {
//		return partialResults.getReadOnlyProperty();
//	}
}
