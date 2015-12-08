package de.rsp.wdntxml.fx;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import de.rsp.wdntxml.langspec.RspXmlWordnetParser;
import de.rsp.wdntxml.langspec.WordnetParser;
import de.rsp.wdntxml.structure.Synset;
import de.rsp.wdntxml.structure.Wordnet;
import de.rsp.wdntxml.tools.Output;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class ReadInTask extends Task<ObservableList<Wordnet>> {

	private String dbUrl;

	private String dbUser;

	private String dbPassword;

	private int dbPort;
	
	private String sshUser;
	
	private String sshPassword;
	
	private int sshPort;
	
	private boolean outData = false;
	
	private WordnetParser chosenParser;
	
	private boolean exportXml;

	private ObservableList<File> inList = FXCollections.observableArrayList();

	protected ReadOnlyListWrapper<Wordnet> partialResults = new ReadOnlyListWrapper<Wordnet>(this, "partialResults",
			FXCollections.observableArrayList(new ArrayList<Wordnet>()));

	public final ObservableList<Wordnet> getPartialResults() {

		return partialResults.get();
	}

	public final ReadOnlyListProperty<Wordnet> partialResultsProperty() {

		return partialResults.getReadOnlyProperty();
	}

	public ReadInTask(ObservableList<File> inList, String dbUrl, String dbUser, String dbPassword, WordnetParser chosenParser, boolean exportXml, int dbPort, String sshUser, String sshPassword, int sshPort) {
		this(inList, chosenParser, exportXml);
		
		this.dbUrl = dbUrl;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.outData = true;
		this.dbPort = dbPort;
		this.sshUser = sshUser;
		this.sshPassword = sshPassword;
		this.sshPort = sshPort;
		
	}

	
	public ReadInTask(ObservableList<File> inList, WordnetParser chosenParser, boolean exportXml) {
		super();

		this.exportXml = exportXml;
		this.inList = inList;
		this.chosenParser = chosenParser;
	}

	@Override
	protected ObservableList<Wordnet> call() throws Exception {

		updateProgress(0, 1);

		
		for (int i = 0; i < inList.size(); i++) {

			WordnetParser rspParser = chosenParser.getClass().newInstance();
			rspParser.setSourcePath(inList.get(i).getAbsolutePath());
			(new Thread(rspParser)).start();
			
			rspParser.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					// System.out.println(getProgress() + (1.0 / (double)
					// inList.size()) + " / " + "1" + "\n");
					updateProgress(getProgress() + (1.0 / (double) inList.size()), 1);
					// Platform.runLater(new Runnable() {
					//
					// @Override
					// public void run() {
					Wordnet wordnet = rspParser.getValue();
					partialResults.get().add(wordnet);
					if(exportXml) Output.outToFileXmlFile(wordnet.getSynsetList(), wordnet.getLang(), "D:\\Programmierung\\git repo\\wdntxml\\out");
					try {
						if (outData)
							Output.outToDB(wordnet, dbUrl, dbUser, dbPassword, dbPort, sshUser, sshPassword, sshPort);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// }
					// });
				}
			});
			int temp = i;
			rspParser.setOnFailed(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {

					System.out.println("failed " + inList.get(temp));
				}
			});
		}

		return getPartialResults();
	}

}
