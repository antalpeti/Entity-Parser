package io.github.antalpeti.entityparser;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(final Stage stage) {
//		stage.setTitle("File Chooser Sample");
//		stage.setX(100);
//		stage.setY(100);

		final Text text = new Text();
		text.setX(50);
		text.setY(50);

		final DirectoryChooser directoryChooser = new DirectoryChooser();

		final Button browseButton = new Button("Choose Entity directory");
		browseButton.setMaxWidth(4096D);
		browseButton.setPrefWidth(4096D);
		browseButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				configureDirectoryChooser(directoryChooser);
				final File selectedDirectory = directoryChooser.showDialog(stage);

				if (selectedDirectory == null) {
					text.setText("No Directory selected");
				} else {
					text.setText(selectedDirectory.getAbsolutePath());
				}

				if (selectedDirectory != null) {
					selectedDirectory.getAbsolutePath();
				}
			}
		});

		final GridPane inputGridPane = new GridPane();

		GridPane.setConstraints(browseButton, 0, 0);
		GridPane.setConstraints(text, 0, 1);
		inputGridPane.setHgap(0);
		inputGridPane.setVgap(0);
		inputGridPane.getChildren().addAll(browseButton, text);

		
		final Pane rootGroup = new VBox(0);
		rootGroup.getChildren().addAll(inputGridPane);
		rootGroup.setPadding(new Insets(0, 0, 0, 0));
		
		Scene scene = new Scene(rootGroup, 600, 600); 
		stage.setTitle("Entity Parser"); 
		stage.setScene(scene);
		stage.show(); 

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	private static void configureDirectoryChooser(final DirectoryChooser directoryChooser) {
		directoryChooser.setTitle("Choose Entity directory");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
	}
}
