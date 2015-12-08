package de.rsp.wdntxml.fx;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import org.reflections.Reflections;
import de.rsp.wdntxml.langspec.RspXmlWordnetParser;
import de.rsp.wdntxml.langspec.WordnetParser;
import de.rsp.wdntxml.structure.Relation;
import de.rsp.wdntxml.structure.Synset;
import de.rsp.wdntxml.structure.Word;
import de.rsp.wdntxml.structure.Wordnet;
import de.rsp.wdntxml.tools.Output;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FxController implements Initializable {

	// ******************//
	@FXML
	private ProgressBar compareProgressBar;

	@FXML
	private TextField compareInputFolder;

	@FXML
	private Tab compareTab;

	@FXML
	private ListView<File> toAdd;

	@FXML
	private ListView<File> added;

	@FXML
	private Button addOne;

	@FXML
	private Button removeOne;

	@FXML
	private TableView<Wordnet> compareTable;

	@FXML
	private TableColumn<Wordnet, String> langCompareTab;

	@FXML
	private TableColumn<Wordnet, Number> synsetCompareTab;

	@FXML
	private TableColumn<Wordnet, Number> missingCompareTab;

	@FXML
	private Button compareButton;

	// *******************//

	@FXML
	private Button cancelButton;

	@FXML
	private Tab drawStatsTab;

	@FXML
	private Tab xmlOutputTab;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private CheckBox synsetListCheck;

	@FXML
	private CheckBox drawStatsCheck;

	@FXML
	private Button createRspXmlOutput;

	@FXML
	private Button chooseDirectoryXmlOut;

	@FXML
	private TextField xmlOutputDir;

	@FXML
	private Button fileChooseButton;

	@FXML
	private Label descriptionLabel;

	@FXML
	private ComboBox<WordnetParser> parserChooser;

	@FXML
	private ComboBox<WordnetParser> parserChooserMulti;

	@FXML
	private TextField pathTextField;

	@FXML
	private Button importButton;

	@FXML
	private TableView<Synset> synsetTable;

	@FXML
	private TableColumn<Synset, String> idColumn;

	@FXML
	private TableColumn<Synset, String> posColumn;

	@FXML
	private TableColumn<Synset, String> wordColumn;

	@FXML
	private TableColumn<Synset, String> relationColumn;

	@FXML
	private TableColumn<Synset, String> defColumn;

	@FXML
	private TableColumn<Synset, String> exmComlumn;

	private ObservableList<WordnetParser> parsers = FXCollections.observableArrayList();

	private File activeFile = null;

	private Stage stage;

	private WordnetParser chosenParser = null;

	private WordnetParser chosenParserMulti = null;

	private ObservableList<File> toAddList = FXCollections.observableArrayList();

	private ObservableList<File> addedList = FXCollections.observableArrayList();

	private ObservableList<Wordnet> compareTableList = FXCollections.observableArrayList();

	private ObservableList<Synset> synsetTableList = FXCollections.observableArrayList();

	private File toAddSelected;

	private File addedSelected;

	@FXML
	private Button databaseUploadButton;

	private Wordnet wordnet;

	@FXML
	private CheckBox dbUploadCheckSingle;

	@FXML
	private CheckBox dbUploadCheckMulti;

	@FXML
	private TextField dbUsernameField;

	@FXML
	private PasswordField dbPasswordField;

	@FXML
	private TextField dbPort;

	@FXML
	private TextField dbURL;

	@FXML
	private CheckBox exportRspXml;

	@FXML
	private TextField sshUsernameField;

	@FXML
	private PasswordField sshPasswordField;

	@FXML
	private TextField sshPort;
	
	@FXML
	private ProgressBar dbProgress;

	/**
	 * makes sure that port textfields only contain positive integer numbers, so
	 * that i can parse them without problem
	 */
	private void checkIfNumber() {

		sshPort.setText(sshPort.getText().replaceAll("[^0-9]", ""));
		dbPort.setText(dbPort.getText().replaceAll("[^0-9]", ""));
	}


	@FXML
	private void handleCompareTabOpened() {
		toAddList.clear();
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Select Output directory");
		File chosenDir = dc.showDialog(stage);
		if (chosenDir != null) {
			compareInputFolder.setText(chosenDir.getAbsolutePath());
			File[] files = chosenDir.listFiles();

			for (File f : files) {

				toAddList.add(f);
			}
		}
	}

	@FXML
	private void handleCompareButtonPress() {

		if (chosenParserMulti != null) {

			compareTable.itemsProperty().unbind();
			compareProgressBar.progressProperty().unbind();
			Task<ObservableList<Wordnet>> task;
			if (dbUploadCheckMulti.isSelected()) {
				task = new ReadInTask2(addedList, dbURL.getText(), dbUsernameField.getText(), dbPasswordField.getText(),
						chosenParserMulti, exportRspXml.isSelected(), Integer.parseInt(dbPort.getText()),
						sshUsernameField.getText(), sshPasswordField.getText(), Integer.parseInt(sshPort.getText()));
			} else {
				task = new ReadInTask2(addedList, chosenParserMulti, exportRspXml.isSelected());
			}
			compareTable.itemsProperty().bind(((ReadInTask2) task).partialResultsProperty());
			compareProgressBar.progressProperty().bind(((ReadInTask2) task).progressProperty());

			(new Thread(task)).start();
		}

	}

	@FXML
	private void handleAddButtonPress() {

		if (toAddSelected != null) {
			addedList.add(toAddSelected);
			toAddList.remove(toAddSelected);
		}
	}

	@FXML
	private void handleRemoveButtonPress() {

		if (addedSelected != null) {
			toAddList.add(addedSelected);
			addedList.remove(addedSelected);
		}
	}

	@FXML
	private void handleButtonAction() {

		if (chosenParser != null) {

			try {
				chosenParser = chosenParser.getClass().newInstance();

			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			cancelButton.setDisable(false);
			xmlOutputTab.setDisable(true);
			drawStatsTab.setDisable(true);
			importButton.setDisable(true);

			// synsetTable.itemsProperty().unbind();
			synsetTableList.clear();
			progressBar.progressProperty().unbind();

			chosenParser.setSourcePath(pathTextField.getText());

			// synsetTable.itemsProperty().bind(chosenParser.partialResultsProperty());
			Thread parserThread = new Thread(chosenParser);
			progressBar.progressProperty().bind(chosenParser.progressProperty());

			parserThread.setDaemon(false);

			((Task<Wordnet>) chosenParser).addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
					new EventHandler<WorkerStateEvent>() {

						@Override
						public void handle(WorkerStateEvent event) {

							wordnet = chosenParser.getValue();

							// Output.outRelFile(wordnet, "D:\\");

							for (Synset syn : wordnet.getSynsetList()) {
								synsetTableList.add(syn);
							}

							cancelButton.setDisable(true);
							xmlOutputTab.setDisable(false);
							drawStatsTab.setDisable(false);
							importButton.setDisable(false);
							
							dbProgress.setProgress(0);

							try {
								if (dbUploadCheckSingle.isSelected())
									Output.outToDB(wordnet, dbURL.getText(), dbUsernameField.getText(),
											dbPasswordField.getText(), Integer.parseInt(dbPort.getText()),
											sshUsernameField.getText(), sshPasswordField.getText(),
											Integer.parseInt(sshPort.getText()));
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
			parserThread.start();
		}
	}

	@FXML
	private void handleCancelButtonPress() {

		((Task<Wordnet>) chosenParser).cancel();
		cancelButton.setDisable(true);
		xmlOutputTab.setDisable(false);
		drawStatsTab.setDisable(false);
		importButton.setDisable(false);
	}

	@FXML
	private void handleFileChooserButtonPress() {

		FileChooser fc = new FileChooser();
		fc.setTitle("Select the soource file");
		activeFile = fc.showOpenDialog(stage);
		if (activeFile != null) {
			pathTextField.setText(activeFile.getAbsolutePath());

		}
	}

	@FXML
	private void handleDirectoryChooserButtonPress() {

		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Select Output directory");
		File chosenDir = dc.showDialog(stage);
		if (chosenDir != null) {
			xmlOutputDir.setText(chosenDir.getAbsolutePath());
		}
	}

	@FXML
	private void handleCreateRspXmlOutputButton() {

		if (xmlOutputDir.getText() != "") {

			if (synsetTable.getItems() != null) {

				Output.outToFileXmlFile(synsetTable.getItems(), chosenParser.getLanguage(), xmlOutputDir.getText());
			} else {
				xmlOutputDir.setText("Import a Lexicon first, please.");
			}
		} else {
			xmlOutputDir.setText("Please choose Directory.");
		}
	}

	@FXML
	private void handleParserChooserChosenEvent() {

	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		sshPort.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				checkIfNumber();
			}
		});

		dbPort.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				checkIfNumber();
			}
		});

		synsetTable.setItems(synsetTableList);

		toAdd.setItems(toAddList);
		added.setItems(addedList);
		compareTable.setItems(compareTableList);

		langCompareTab.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Wordnet, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<Wordnet, String> param) {

						StringProperty str = new SimpleStringProperty(param.getValue().getLang());
						return str;
					}
				});

		synsetCompareTab.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Wordnet, Number>, ObservableValue<Number>>() {

					@Override
					public ObservableValue<Number> call(CellDataFeatures<Wordnet, Number> param) {

						return new SimpleIntegerProperty(param.getValue().getSynsetList().size());
					}
				});

		toAdd.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
			@Override
			public ListCell<File> call(ListView<File> param) {
				return new ListCell<File>() {
					@Override
					protected void updateItem(File item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setText(null);
							setGraphic(null);
						} else {
							setText(item.getName());
						}
					}
				};
			}
		});

		added.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
			@Override
			public ListCell<File> call(ListView<File> param) {
				return new ListCell<File>() {
					@Override
					protected void updateItem(File item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setText(null);
							setGraphic(null);
						} else {
							setText(item.getName());
						}
					}
				};
			}
		});

		toAdd.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<File>() {
			@Override
			public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {

				if (newValue != null)
					toAddSelected = newValue;
			}
		});

		added.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<File>() {
			@Override
			public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {

				if (newValue != null)
					addedSelected = newValue;
			}
		});

		cancelButton.setDisable(true);
		xmlOutputTab.setDisable(true);
		drawStatsTab.setDisable(true);
		importButton.setDisable(false);

		descriptionLabel.setWrapText(true);

		Reflections reflections = new Reflections("de.rsp.wdntxml.langspec");

		Set<Class<? extends WordnetParser>> allClasses = reflections.getSubTypesOf(WordnetParser.class);

		for (Class<? extends WordnetParser> wp : allClasses) {
			try {
				parsers.add(wp.newInstance());
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			}
		}

		// add model and listeners to parserChooser ComboBox.
		parserChooser.setItems(parsers);
		parserChooser.valueProperty().addListener(new ChangeListener<WordnetParser>() {
			@Override
			public void changed(ObservableValue<? extends WordnetParser> observable, WordnetParser oldValue,
					WordnetParser newValue) {
				chosenParser = newValue;
				descriptionLabel.setText(newValue.getDescription());
			}
		});

		parserChooserMulti.setItems(parsers);
		parserChooserMulti.valueProperty().addListener(new ChangeListener<WordnetParser>() {

			@Override
			public void changed(ObservableValue<? extends WordnetParser> observable, WordnetParser oldValue,
					WordnetParser newValue) {
				chosenParserMulti = newValue;

			}
		});

		// add showable labels for Parser classes
		parserChooser.setCellFactory(new Callback<ListView<WordnetParser>, ListCell<WordnetParser>>() {
			@Override
			public ListCell<WordnetParser> call(ListView<WordnetParser> param) {
				return new ListCell<WordnetParser>() {
					@Override
					protected void updateItem(WordnetParser item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
						} else {
							setText(item.getClass().getSimpleName());
						}
					}
				};
			}
		});
		parserChooser.setConverter(new javafx.util.StringConverter<WordnetParser>() {

			@Override
			public String toString(WordnetParser object) {
				if (object == null)
					return null;
				else
					return object.getClass().getSimpleName();
			}

			@Override
			public WordnetParser fromString(String string) {

				return null;
			}
		});

		parserChooserMulti.setCellFactory(new Callback<ListView<WordnetParser>, ListCell<WordnetParser>>() {
			@Override
			public ListCell<WordnetParser> call(ListView<WordnetParser> param) {
				return new ListCell<WordnetParser>() {
					@Override
					protected void updateItem(WordnetParser item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
						} else {
							setText(item.getClass().getSimpleName());
						}
					}
				};
			}
		});
		parserChooserMulti.setConverter(new javafx.util.StringConverter<WordnetParser>() {

			@Override
			public String toString(WordnetParser object) {
				if (object == null)
					return null;
				else
					return object.getClass().getSimpleName();
			}

			@Override
			public WordnetParser fromString(String string) {

				return null;
			}
		});

		// make columns of synsetTable show the stuff.
		idColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Synset, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Synset, String> param) {
						StringProperty str = new SimpleStringProperty(param.getValue().getiD());
						return str;
					}
				});

		posColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Synset, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Synset, String> param) {
						StringProperty str = new SimpleStringProperty(param.getValue().getPos());
						return str;
					}
				});

		wordColumn.setCellFactory(new Callback<TableColumn<Synset, String>, TableCell<Synset, String>>() {

			@Override
			public TableCell<Synset, String> call(TableColumn<Synset, String> param) {

				TableCell<Synset, String> cell = new TableCell<>();
				Text text = new Text();
				cell.setGraphic(text);
				cell.setPrefHeight(javafx.scene.control.Control.USE_COMPUTED_SIZE);
				text.wrappingWidthProperty().bind(wordColumn.widthProperty());
				text.textProperty().bind(cell.itemProperty());
				return cell;
			}
		});

		wordColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Synset, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<Synset, String> param) {

						List<Word> words = param.getValue().getWords();
						String result = "[";
						for (Word w : words) {
							result += w + " | ";
						}
						result = result.length() < 2 ? "[]" : result.substring(0, result.length() - 3) + "]";
						return new SimpleStringProperty(result);
					}
				});

		relationColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Synset, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<Synset, String> param) {

						List<Relation> relations = param.getValue().getRelations();
						String result = "[";
						for (Relation rel : relations) {
							result += "{" + rel.getTargetSynset() + ", " + rel.getType() + "}" + " | ";
						}
						result = result.length() < 2 ? "[]" : result.substring(0, result.length() - 3) + "]";
						return new SimpleStringProperty(result);
					}
				});

		relationColumn.setCellFactory(new Callback<TableColumn<Synset, String>, TableCell<Synset, String>>() {

			@Override
			public TableCell<Synset, String> call(TableColumn<Synset, String> param) {

				TableCell<Synset, String> cell = new TableCell<>();
				Text text = new Text();
				cell.setGraphic(text);
				cell.setPrefHeight(javafx.scene.control.Control.USE_COMPUTED_SIZE);
				text.wrappingWidthProperty().bind(relationColumn.widthProperty());
				text.textProperty().bind(cell.itemProperty());
				return cell;
			}
		});

		defColumn.setCellFactory(new Callback<TableColumn<Synset, String>, TableCell<Synset, String>>() {
			@Override
			public TableCell<Synset, String> call(TableColumn<Synset, String> param) {

				TableCell<Synset, String> cell = new TableCell<>();
				Text text = new Text();
				cell.setGraphic(text);
				cell.setPrefHeight(javafx.scene.control.Control.USE_COMPUTED_SIZE);
				text.wrappingWidthProperty().bind(defColumn.widthProperty());
				text.textProperty().bind(cell.itemProperty());
				return cell;
			}
		});

		defColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Synset, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Synset, String> param) {
						StringProperty str = new SimpleStringProperty(param.getValue().getDefinition());
						return str;
					}
				});

		exmComlumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Synset, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<Synset, String> param) {
						List<String> examples = param.getValue().getExample();
						ObservableValue<String> result = new SimpleStringProperty(examples.toString());
						return result;
					}
				});
		exmComlumn.setCellFactory(new Callback<TableColumn<Synset, String>, TableCell<Synset, String>>() {

			@Override
			public TableCell<Synset, String> call(TableColumn<Synset, String> param) {
				TableCell<Synset, String> cell = new TableCell<>();
				Text text = new Text();
				cell.setGraphic(text);
				cell.setPrefHeight(javafx.scene.control.Control.USE_COMPUTED_SIZE);
				text.wrappingWidthProperty().bind(exmComlumn.widthProperty());
				text.textProperty().bind(cell.itemProperty().asString());
				return cell;
			}
		});

	}
}