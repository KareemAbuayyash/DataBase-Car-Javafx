package com.example;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import org.kordamp.ikonli.javafx.FontIcon;
public class TextFieldFactory {
    // make method to create text field
    public static TextField createTextField(String text) {
        TextField textField = new TextField();
        textField.setPromptText(text);
        textField.getStyleClass().add("text-field");
        return textField;
    }
}
