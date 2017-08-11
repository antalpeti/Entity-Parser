package io.github.antalpeti.entityparser.uielement;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class LabeledField extends HBox {
	
	public LabeledField(double spacing, Label label, TextField textField) {
		super(spacing);
		getChildren().addAll(label, textField);
		HBox.setHgrow(label, Priority.ALWAYS);
		label.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(textField, Priority.ALWAYS);
		textField.setMaxWidth(Double.MAX_VALUE);
	}

	@Override
	protected void layoutChildren() {
		double minPrefWidth = calculatePrefChildWidth();
		for (Node n : getChildren()) {
			if (n instanceof Text) {
				((Label) n).setMinWidth(minPrefWidth);
			}
			if (n instanceof TextField) {
				((TextField) n).setMinWidth(minPrefWidth);
			}
		}
		super.layoutChildren();
	}

	private double calculatePrefChildWidth() {
		double minPrefWidth = 0;
		for (Node n : getChildren()) {
			minPrefWidth = Math.max(minPrefWidth, n.prefWidth(-1));
		}
		return minPrefWidth;
	}
}