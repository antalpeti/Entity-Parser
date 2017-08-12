package io.github.antalpeti.entityparser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.antalpeti.entityparser.common.Constants;
import io.github.antalpeti.entityparser.common.FileHandler;
import io.github.antalpeti.entityparser.uielement.LabeledField;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application {

  private TextArea outputTextArea;
  private ProgressBar progressBar;
  private ProgressIndicator progressIndicator;

  @Override
  public void start(final Stage stage) {
    final LabeledField projectcodeLabeledField = createProjectcodeLabeledField();

    outputTextArea = createOutputTextArea();

    final GridPane outputGridPane = createOutputGridPane(outputTextArea);

    final DirectoryChooser directoryChooser = new DirectoryChooser();

    final Button chooseEntityDirectoryButton = createChoseEntityDirectoryButton(stage, directoryChooser);

    HBox progressHBox = createProgressHBox();

    final GridPane mainGridPane = createMainGridPane(projectcodeLabeledField, progressHBox, chooseEntityDirectoryButton, outputGridPane);

    final Pane mainPane = createMainPane(mainGridPane);

    Scene scene = new Scene(mainPane, 800, 800);
    // scene.getStylesheets().add("css/styles.css");
    stage.setTitle("Entity Parser");
    stage.setScene(scene);
    stage.show();
  }

  private LabeledField createProjectcodeLabeledField() {
    Label projectcodeLabel = new Label("Projectcode:");
    projectcodeLabel.setStyle(Constants.FONT_STYLE);
    final TextField projectcodeField = new TextField();
    projectcodeField.setStyle(Constants.FONT_STYLE);

    final LabeledField projectcodeLabeledField = new LabeledField(5, projectcodeLabel, projectcodeField);
    return projectcodeLabeledField;
  }

  private TextArea createOutputTextArea() {
    final TextArea outputArea = new TextArea();
    outputArea.setWrapText(true);
    outputArea.setEditable(false);
    outputArea.setStyle(Constants.FONT_STYLE);
    outputArea.setPrefHeight(Constants.PREFERRED_SIZE);
    outputArea.setPrefWidth(Constants.PREFERRED_SIZE);
    outputArea.setMaxWidth(Double.MAX_VALUE);
    outputArea.setMaxHeight(Double.MAX_VALUE);
    return outputArea;
  }

  private GridPane createOutputGridPane(final TextArea outputTextArea) {
    GridPane outputGridPane = new GridPane();
    GridPane.setVgrow(outputTextArea, Priority.ALWAYS);
    GridPane.setHgrow(outputTextArea, Priority.ALWAYS);
    outputGridPane.setMaxWidth(Double.MAX_VALUE);
    outputGridPane.setMaxHeight(Double.MAX_VALUE);
    Label outputLabel = new Label("Output:");
    outputLabel.setStyle(Constants.FONT_STYLE);
    outputGridPane.add(outputLabel, 0, 0);
    outputGridPane.add(outputTextArea, 0, 1);
    return outputGridPane;
  }

  private static final String TITLE_CHOOSE_ENTITY_DIRECTORY = "Choose Entity directory";
  private File selectedDirectory;

  private Button createChoseEntityDirectoryButton(final Stage stage, final DirectoryChooser directoryChooser) {
    final Button chooseEntityDirectoryButton = new Button(TITLE_CHOOSE_ENTITY_DIRECTORY);
    chooseEntityDirectoryButton.setStyle(Constants.FONT_STYLE);
    chooseEntityDirectoryButton.setMaxWidth(Constants.PREFERRED_SIZE);
    chooseEntityDirectoryButton.setPrefWidth(Constants.PREFERRED_SIZE);
    chooseEntityDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(final ActionEvent e) {
        configureDirectoryChooser(directoryChooser);
        selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory == null) {
          outputTextArea.setText("No Directory selected!");
        } else {
          processFiles();
        }
      }
    });
    return chooseEntityDirectoryButton;
  }

  private static void configureDirectoryChooser(final DirectoryChooser directoryChooser) {
    directoryChooser.setTitle(TITLE_CHOOSE_ENTITY_DIRECTORY);

    String lastSelectedDirectory = FileHandler.getInstance().loadProperty(Constants.FILEPATH_CONFIG_PROPERTIES,
        Constants.CONFIG_PROPERTIES_LAST_SELECTED_DIRECTORY);
    if (lastSelectedDirectory != null) {
      directoryChooser.setInitialDirectory(new File(lastSelectedDirectory));
    } else {
      directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }
  }

  @SuppressWarnings("unchecked")
  private void processFiles() {

    // progressStage.show();

    @SuppressWarnings("rawtypes")
    Task longTask = new Task<Double>() {
      @Override
      protected Double call() throws Exception {
        updateProgress(-1d, 0d);

        List<Double> countedFilesList = new ArrayList<>();
        countedFilesList.add(new Double(0));
        List<File> files = new ArrayList<>();
        String extension = "java";

        FileHandler.getInstance().listFiles(selectedDirectory, countedFilesList, files, extension);
        Double countedFiles = countedFilesList.get(0);

        Double processedFiles = new Double(0.0);
        updateProgress(0d, 0d);

        StringBuilder output = new StringBuilder();

        for (File file : files) {
          output.append(file.getName());
          output.append("\n");
          ++processedFiles;
          updateProgress(processedFiles, countedFiles);
        }

        outputTextArea.setText(output.toString());

        FileHandler.getInstance().storeProperties(selectedDirectory, Constants.FILEPATH_CONFIG_PROPERTIES,
            Constants.CONFIG_PROPERTIES_LAST_SELECTED_DIRECTORY, selectedDirectory.getAbsolutePath());

        return countedFiles;
      }

      @Override
      protected void succeeded() {
        super.succeeded();
      }
    };
    progressBar.progressProperty().bind(longTask.progressProperty());
    progressIndicator.progressProperty().bind(longTask.progressProperty());

    new Thread(longTask).start();
  }

  private HBox createProgressHBox() {
    HBox progressHBox = new HBox();
    progressHBox.setSpacing(1);
    progressHBox.prefWidthProperty().bind(outputTextArea.widthProperty());
    progressHBox.setPadding(new Insets(1));

    Label progressLabel = new Label("Progress:");
    progressLabel.setStyle(Constants.FONT_STYLE);
    progressLabel.prefWidthProperty().bind(progressHBox.widthProperty().multiply(0.1));

    progressBar = new ProgressBar();
    progressBar.prefWidthProperty().bind(progressHBox.widthProperty().multiply(0.8));
    progressBar.setProgress(0d);

    progressIndicator = new ProgressIndicator();
    progressIndicator.prefWidthProperty().bind(progressHBox.widthProperty().multiply(0.1));
    progressIndicator.setProgress(0d);

    progressHBox.getChildren().addAll(progressLabel, progressBar, progressIndicator);
    return progressHBox;
  }

  private GridPane createMainGridPane(final LabeledField projectcodeLabeledField, HBox progressHBox, final Button chooseEntityDirectoryButton,
      final GridPane outputGridPane) {
    final GridPane mainGridPane = new GridPane();
    GridPane.setConstraints(projectcodeLabeledField, 0, 0);
    GridPane.setConstraints(progressHBox, 0, 1);
    GridPane.setConstraints(chooseEntityDirectoryButton, 0, 2);
    GridPane.setConstraints(outputGridPane, 0, 3);
    mainGridPane.setHgap(1);
    mainGridPane.setVgap(1);
    mainGridPane.getChildren().addAll(projectcodeLabeledField, progressHBox, chooseEntityDirectoryButton, outputGridPane);
    return mainGridPane;
  }

  private Pane createMainPane(final GridPane mainGridPane) {
    final Pane mainPane = new VBox(0);
    mainPane.getChildren().addAll(mainGridPane);
    mainPane.setPadding(new Insets(0, 0, 0, 0));
    return mainPane;
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
