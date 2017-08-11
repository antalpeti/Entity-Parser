package io.github.antalpeti.entityparser;

import java.io.File;
import java.util.concurrent.Callable;

import io.github.antalpeti.entityparser.uielement.LabeledField;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
	@Override
	public void start(final Stage stage) {
		String fontStyle = "-fx-font: normal bold 15px 'serif' ";
		
		Label projectcodeLabel = new Label("Projectcode:");
		projectcodeLabel.setStyle(fontStyle); 
		final TextField projectcodeField = new TextField();
		projectcodeField.setStyle(fontStyle);
		
		final LabeledField projectcodeLabeledField = new LabeledField(5, projectcodeLabel, projectcodeField);
		
		final TextArea outputArea = new TextArea();
		outputArea.setWrapText(true);
		outputArea.setStyle(fontStyle);
		outputArea.setPrefHeight(8192D);
		outputArea.setPrefWidth(8192D);
		outputArea.setMaxWidth(Double.MAX_VALUE);
		outputArea.setMaxHeight(Double.MAX_VALUE);
        
        GridPane outputPane = new GridPane();
        GridPane.setVgrow(outputArea, Priority.ALWAYS);
        GridPane.setHgrow(outputArea, Priority.ALWAYS);
        outputPane.setMaxWidth(Double.MAX_VALUE);
        outputPane.setMaxHeight(Double.MAX_VALUE);
        Label outputLabel = new Label("Output:");
        outputLabel.setStyle(fontStyle); 
        outputPane.add(outputLabel, 0, 0);
        outputPane.add(outputArea, 0, 1);
		
		final DirectoryChooser directoryChooser = new DirectoryChooser();

		final Button browseButton = new Button("Choose Entity directory");
		browseButton.setStyle(fontStyle);
		setBrowserButton(stage, outputArea, directoryChooser, browseButton);

		final GridPane inputGridPane = new GridPane();
		GridPane.setConstraints(projectcodeLabeledField, 0, 0);
		GridPane.setConstraints(browseButton, 0, 1);
		GridPane.setConstraints(outputPane, 0, 2);
		inputGridPane.setHgap(0);
		inputGridPane.setVgap(0);
		inputGridPane.getChildren().addAll(projectcodeLabeledField, browseButton, outputPane);

		final Pane rootGroup = new VBox(0);
		rootGroup.getChildren().addAll(inputGridPane);
		rootGroup.setPadding(new Insets(0, 0, 0, 0));

		Scene scene = new Scene(rootGroup, 600, 600);
//		scene.getStylesheets().add("css/styles.css");
		stage.setTitle("Entity Parser");
		stage.setScene(scene);
		stage.show();
	}

	private void setBrowserButton(final Stage stage, final TextArea output, final DirectoryChooser directoryChooser, final Button browseButton) {
		browseButton.setMaxWidth(8192D);
		browseButton.setPrefWidth(8192D);
		browseButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				configureDirectoryChooser(directoryChooser);
				final File selectedDirectory = directoryChooser.showDialog(stage);

				if (selectedDirectory == null) {
					output.setText("No Directory selected!");
				} else {
					output.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});
	}

	private static void configureDirectoryChooser(final DirectoryChooser directoryChooser) {
		directoryChooser.setTitle("Choose Entity directory");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
