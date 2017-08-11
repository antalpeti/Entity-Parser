package io.github.antalpeti.entityparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import io.github.antalpeti.entityparser.common.FileHandler;
import io.github.antalpeti.entityparser.uielement.LabeledField;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application {
  private static final double PREFERED_SIZE = 8192D;
  private static final String FONT_STYLE = "-fx-font: normal bold 15px 'serif' ";

  @Override
  public void start(final Stage stage) {
    FileHandler.getInstance().initStreams("config.properties");

    final LabeledField projectcodeLabeledField = createProjectcodeLabeledField();

    final TextArea outputTextArea = createOutputTextArea();

    final GridPane outputGridPane = createOutputGridPane(outputTextArea);

    final DirectoryChooser directoryChooser = new DirectoryChooser();

    final Button chooseEntityDirectoryButton = createChoseEntityDirectoryButton(stage, outputTextArea, directoryChooser);

    final GridPane mainGridPane = createMainGridPane(projectcodeLabeledField, outputGridPane, chooseEntityDirectoryButton);

    final Pane mainPane = createMainPane(mainGridPane);

    Scene scene = new Scene(mainPane, 800, 800);
    // scene.getStylesheets().add("css/styles.css");
    stage.setTitle("Entity Parser");
    stage.setScene(scene);
    stage.show();
  }

  private LabeledField createProjectcodeLabeledField() {
    Label projectcodeLabel = new Label("Projectcode:");
    projectcodeLabel.setStyle(FONT_STYLE);
    final TextField projectcodeField = new TextField();
    projectcodeField.setStyle(FONT_STYLE);

    final LabeledField projectcodeLabeledField = new LabeledField(5, projectcodeLabel, projectcodeField);
    return projectcodeLabeledField;
  }

  private TextArea createOutputTextArea() {
    final TextArea outputArea = new TextArea();
    outputArea.setWrapText(true);
    outputArea.setStyle(FONT_STYLE);
    outputArea.setPrefHeight(PREFERED_SIZE);
    outputArea.setPrefWidth(PREFERED_SIZE);
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
    outputLabel.setStyle(FONT_STYLE);
    outputGridPane.add(outputLabel, 0, 0);
    outputGridPane.add(outputTextArea, 0, 1);
    return outputGridPane;
  }

  private static final String CONFIG_PROPERTIES_LAST_SELECTED_DIRECTORY = "last.selected.directory";

  private Button createChoseEntityDirectoryButton(final Stage stage, final TextArea outputTextArea, final DirectoryChooser directoryChooser) {
    final Button chooseEntityDirectoryButton = new Button("Chose Entity directory");
    chooseEntityDirectoryButton.setStyle(FONT_STYLE);
    chooseEntityDirectoryButton.setMaxWidth(PREFERED_SIZE);
    chooseEntityDirectoryButton.setPrefWidth(PREFERED_SIZE);
    chooseEntityDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(final ActionEvent e) {
        configureDirectoryChooser(directoryChooser);
        final File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory == null) {
          outputTextArea.setText("No Directory selected!");
        } else {
          outputTextArea.setText(selectedDirectory.getAbsolutePath());
          FileHandler.getInstance().storeProperties(selectedDirectory, "last.selected.directory", selectedDirectory.getAbsolutePath());
        }
      }

    });
    return chooseEntityDirectoryButton;
  }

  private static void configureDirectoryChooser(final DirectoryChooser directoryChooser) {
    directoryChooser.setTitle("Choose Entity directory");

    String lastSelectedDirectory = FileHandler.getInstance().loadProperty(CONFIG_PROPERTIES_LAST_SELECTED_DIRECTORY);
    if (lastSelectedDirectory != null) {
      directoryChooser.setInitialDirectory(new File(lastSelectedDirectory));
    } else {
      directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }
  }

  private GridPane createMainGridPane(final LabeledField projectcodeLabeledField, final GridPane outputGridPane,
      final Button chooseEntityDirectoryButton) {
    final GridPane mainGridPane = new GridPane();
    GridPane.setConstraints(projectcodeLabeledField, 0, 0);
    GridPane.setConstraints(chooseEntityDirectoryButton, 0, 1);
    GridPane.setConstraints(outputGridPane, 0, 2);
    mainGridPane.setHgap(1);
    mainGridPane.setVgap(1);
    mainGridPane.getChildren().addAll(projectcodeLabeledField, chooseEntityDirectoryButton, outputGridPane);
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
