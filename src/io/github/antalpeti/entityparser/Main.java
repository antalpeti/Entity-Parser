package io.github.antalpeti.entityparser;

import java.io.File;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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

		final BorderPane outputPane = new BorderPane();
		final TextArea outputArea = new TextArea();
		outputArea.setWrapText(true);
		outputArea.setStyle(fontStyle); 
		outputPane.setCenter(outputArea);

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
