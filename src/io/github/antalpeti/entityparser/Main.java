package io.github.antalpeti.entityparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.antalpeti.entityparser.common.Constants;
import io.github.antalpeti.entityparser.common.FileHandler;
import io.github.antalpeti.entityparser.common.Util;
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

  private TextField projectcodeField;

  private LabeledField createProjectcodeLabeledField() {
    Label projectcodeLabel = new Label("Projectcode:");
    projectcodeLabel.setStyle(Constants.FONT_STYLE);
    projectcodeField = new TextField();
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

    String lastSelectedDirectory = null;
    Path currentRelativePath = Paths.get("");
    String configPropertiesAbsolutePath = currentRelativePath.toAbsolutePath().toString() + File.separator + Constants.FILEPATH_CONFIG_PROPERTIES;
	File f = new File(configPropertiesAbsolutePath);
    if(f.exists() && !f.isDirectory()) { 
    	lastSelectedDirectory = FileHandler.getInstance().loadProperty(Constants.FILEPATH_CONFIG_PROPERTIES,
        Constants.CONFIG_PROPERTIES_LAST_SELECTED_DIRECTORY);
    }
    if (lastSelectedDirectory != null) {
      directoryChooser.setInitialDirectory(new File(lastSelectedDirectory));
    } else {
      directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }
  }

  private void processFiles() {
    @SuppressWarnings("rawtypes")
    Task longTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
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

        String projectcode = projectcodeField.getText();
        if (Util.isEmpty(projectcode)) {
          outputTextArea.setText("The projectcode is missing.");
          return null;
        }

        for (File file : files) {
          try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            String currentLine;
            boolean packageFounded = false;
            String packageName = null;
            boolean nameAnnotationFounded = false;
            String nameAnnotationValue = null;
            boolean tableAnnotationFounded = false;
            String tableAnnotationValue = null;
            boolean classNameFounded = false;
            String className = null;

            while ((currentLine = bufferedReader.readLine()) != null) {
              if (currentLine.isEmpty()) {
                continue;
              }
              String regex = "package";
              int indexOf = currentLine.indexOf(regex);
              if (!packageFounded && indexOf != -1) {
                packageFounded = true;
                Pattern pattern = Pattern.compile("(hu[.a-zA-Z]+)");
                Matcher matcher = pattern.matcher(currentLine);
                if (matcher.find(indexOf + regex.length())) {
                  packageName = matcher.group(1);
                  continue;
                }
              }
              regex = "@Name";
              indexOf = currentLine.indexOf(regex);
              if (!nameAnnotationFounded && indexOf != -1) {
                nameAnnotationFounded = true;
                Pattern pattern = Pattern.compile("([.a-zA-Z]+)");
                Matcher matcher = pattern.matcher(currentLine);
                if (matcher.find(indexOf + regex.length())) {
                  nameAnnotationValue = matcher.group(1);
                  continue;
                }
              }
              regex = "@Table";
              indexOf = currentLine.indexOf(regex);
              if (!tableAnnotationFounded && indexOf != -1) {
                tableAnnotationFounded = true;
                int beginIndex = currentLine.indexOf("\"", indexOf);
                int endIndex = currentLine.indexOf("\"", beginIndex + 1);
                tableAnnotationValue = currentLine.substring(beginIndex + 1, endIndex);
                continue;
              }
              regex = "public\\sclass";
              indexOf = Util.indexOf(Pattern.compile(regex), currentLine);
              if (!classNameFounded && indexOf != -1) {
                classNameFounded = true;
                Pattern pattern = Pattern.compile("([.a-zA-Z]+)");
                Matcher matcher = pattern.matcher(currentLine);
                if (matcher.find(indexOf + regex.length())) {
                  className = matcher.group(1);
                  continue;
                }
              }
              if ((nameAnnotationFounded && tableAnnotationFounded) || (packageFounded && classNameFounded && tableAnnotationFounded)) {
                break;
              }
            }
            if (Util.isEmpty(nameAnnotationValue)
                && (!Util.isEmpty(packageName) && !Util.isEmpty(className) && !Util.isEmpty(tableAnnotationValue))) {
              output.append(packageName);
              output.append(".");
              output.append(className);
              output.append("\t");
              output.append(tableAnnotationValue);
              output.append("\t");
              output.append(projectcode);
              output.append("\n");
            } else if (!Util.isEmpty(nameAnnotationValue) && !Util.isEmpty(tableAnnotationValue)) {
              output.append(nameAnnotationValue);
              output.append("\t");
              output.append(tableAnnotationValue);
              output.append("\t");
              output.append(projectcode);
              output.append("\n");
            }
          } catch (IOException e) {
            e.printStackTrace();
          }

          ++processedFiles;
          updateProgress(processedFiles, countedFiles);
        }

        outputTextArea.setText(output.toString());

        FileHandler.getInstance().storeProperties(selectedDirectory, Constants.FILEPATH_CONFIG_PROPERTIES,
            Constants.CONFIG_PROPERTIES_LAST_SELECTED_DIRECTORY, selectedDirectory.getAbsolutePath());
        return null;
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
    progressHBox.prefWidthProperty().bind(outputTextArea.prefWidthProperty());
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
