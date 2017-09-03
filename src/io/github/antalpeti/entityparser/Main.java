package io.github.antalpeti.entityparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application {

  private TextArea outputTextArea;
  private ProgressBar progressBar;
  private ProgressIndicator progressIndicator;

  @Override
  public void start(final Stage stage) {
    final LabeledField projectidLabeledField = createProjectidLabeledField();

    outputTextArea = createOutputTextArea();

    final GridPane outputGridPane = createOutputGridPane(outputTextArea);

    final Button aboutButton = createAboutButton();

    final DirectoryChooser directoryChooser = new DirectoryChooser();

    final Button chooseEntityDirectoryButton = createChooseEntityDirectoryButton(stage, directoryChooser);

    HBox progressHBox = createProgressHBox();

    final GridPane mainGridPane = createMainGridPane(projectidLabeledField, progressHBox, chooseEntityDirectoryButton, outputGridPane, aboutButton);

    final Pane mainPane = createMainPane(mainGridPane);

    Scene scene = new Scene(mainPane, 800, 800);
    stage.setTitle("Entity Parser");
    stage.setScene(scene);
    stage.show();
  }

  private TextField projectidField;

  private LabeledField createProjectidLabeledField() {
    Label projectidLabel = new Label("Projectid:");
    projectidLabel.setStyle(Constants.FONT_STYLE);
    projectidField = new TextField();
    projectidField.setStyle(Constants.FONT_STYLE);

    final LabeledField projectidLabeledField = new LabeledField(5, projectidLabel, projectidField);
    return projectidLabeledField;
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

  private Button createAboutButton() {
    final Button aboutButton = new Button("About");
    aboutButton.setStyle(Constants.FONT_STYLE);
    aboutButton.setMaxWidth(Constants.PREFERRED_SIZE);
    aboutButton.setPrefWidth(Constants.PREFERRED_SIZE);
    aboutButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(final ActionEvent e) {
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        try {
          Path currentRelativePath = Paths.get("");
          File file = new File(currentRelativePath.toAbsolutePath().toString() + File.separator + Constants.FILEPATH_ABOUT_HTML);
          URL url = file.toURI().toURL();
          webEngine.load(url.toString());

          VBox root = new VBox();
          root.getChildren().addAll(browser);

          Scene scene = new Scene(root);
          Stage stage = new Stage();
          scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
              if (t.getCode() == KeyCode.ESCAPE) {
                stage.close();
              }
            }
          });

          stage.setTitle("About");
          stage.setScene(scene);
          stage.setWidth(800);
          stage.setHeight(800);

          stage.show();
        } catch (MalformedURLException ex) {
          ex.printStackTrace();
        }

      }
    });
    return aboutButton;
  }

  private static final String TITLE_CHOOSE_ENTITY_DIRECTORY = "Choose Entity directory";
  private File selectedDirectory;

  private Button createChooseEntityDirectoryButton(final Stage stage, final DirectoryChooser directoryChooser) {
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
          boolean errorExist = false;
          if (Util.isEmpty(projectidField.getText())) {
            outputTextArea.setText("The projectid is missing.");
            errorExist = true;
          }
          if (!errorExist) {
            processFiles();
          }
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
    if (f.exists() && !f.isDirectory()) {
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

        String projectid = projectidField.getText();

        for (File file : files) {
          try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            String currentLine;
            boolean packageFound = false;
            String packageName = null;
            boolean entityAnnotationFound = false;
            boolean nameAnnotationFound = false;
            String nameAnnotationValue = null;
            boolean tableAnnotationFound = false;
            String tableAnnotationValue = null;
            boolean classNameFound = false;
            String className = null;

            while ((currentLine = bufferedReader.readLine()) != null) {
              if (currentLine.isEmpty()) {
                continue;
              }
              String regex = "package";
              int indexOf = currentLine.indexOf(regex);
              if (!packageFound && indexOf != -1) {
                packageFound = true;
                Pattern pattern = Pattern.compile("(hu[.a-zA-Z0-9]+)");
                Matcher matcher = pattern.matcher(currentLine);
                if (matcher.find(indexOf + regex.length())) {
                  packageName = matcher.group(1);
                  continue;
                }
              }
              regex = "@Name";
              indexOf = currentLine.indexOf(regex);
              if (!nameAnnotationFound && indexOf != -1) {
                nameAnnotationFound = true;
                int beginIndex = currentLine.indexOf("\"", indexOf);
                int endIndex = currentLine.indexOf("\"", beginIndex + 1);
                nameAnnotationValue = currentLine.substring(beginIndex + 1, endIndex);
                continue;
              }
              regex = "@Entity";
              indexOf = currentLine.indexOf(regex);
              if (!entityAnnotationFound && indexOf != -1) {
                entityAnnotationFound = true;
                continue;
              }
              regex = "@Table";
              indexOf = currentLine.indexOf(regex);
              if (!tableAnnotationFound && indexOf != -1) {
                tableAnnotationFound = true;
                int beginIndex = currentLine.indexOf("\"", indexOf);
                int endIndex = currentLine.indexOf("\"", beginIndex + 1);
                tableAnnotationValue = currentLine.substring(beginIndex + 1, endIndex);
                continue;
              }
              regex = "public\\sclass";
              indexOf = Util.indexOf(Pattern.compile(regex), currentLine);
              if (!classNameFound && indexOf != -1) {
                classNameFound = true;
                Pattern pattern = Pattern.compile("([.a-zA-Z0-9]+)");
                Matcher matcher = pattern.matcher(currentLine);
                if (matcher.find(indexOf + regex.length())) {
                  className = matcher.group(1);
                  break;
                }
              }
            }
            if (entityAnnotationFound && !Util.isEmpty(nameAnnotationValue) && !Util.isEmpty(tableAnnotationValue)) {
              output.append(nameAnnotationValue);
              output.append("\t");
              output.append(tableAnnotationValue);
              output.append("\t");
              output.append(projectid);
              output.append("\n");
            } else if (entityAnnotationFound && (!Util.isEmpty(packageName) && !Util.isEmpty(className)) && !Util.isEmpty(tableAnnotationValue)) {
              output.append(packageName);
              output.append(".");
              output.append(className);
              output.append("\t");
              output.append(tableAnnotationValue);
              output.append("\t");
              output.append(projectid);
              output.append("\n");
            } else if (entityAnnotationFound && (!Util.isEmpty(packageName) && !Util.isEmpty(className))) {
              output.append(packageName);
              output.append(".");
              output.append(className);
              output.append("\t");
              output.append(className);
              output.append("\t");
              output.append(projectid);
              output.append("\n");
            }
          } catch (IOException e) {
            e.printStackTrace();
          }

          ++processedFiles;
          updateProgress(processedFiles, countedFiles);
        }

        if (output.length() > 0) {
          output.deleteCharAt(output.length() - 1);
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

  private GridPane createMainGridPane(final LabeledField projectidLabeledField, HBox progressHBox, final Button chooseEntityDirectoryButton,
      final GridPane outputGridPane, final Button aboutButton) {
    final GridPane mainGridPane = new GridPane();
    GridPane.setConstraints(projectidLabeledField, 0, 0);
    GridPane.setConstraints(progressHBox, 0, 1);
    GridPane.setConstraints(chooseEntityDirectoryButton, 0, 2);
    GridPane.setConstraints(outputGridPane, 0, 3);
    GridPane.setConstraints(aboutButton, 0, 4);
    mainGridPane.setHgap(1);
    mainGridPane.setVgap(1);
    mainGridPane.getChildren().addAll(projectidLabeledField, progressHBox, chooseEntityDirectoryButton, outputGridPane, aboutButton);
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
