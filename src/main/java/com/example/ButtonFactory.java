package com.example;

import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

public class ButtonFactory {

    // Creates a styled button for the sidebar with an icon
    public static Button createSidebarButton(String text, String iconCode) {
        Button button = new Button(text);
        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(18); // Set icon size as needed
        button.setGraphic(icon);
        button.getStyleClass().add("sidebar-button");
        return button;
    }

    // Creates a plain styled button, typically used for actions like Save, Cancel, etc.
    public static Button createActionButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("action-button");
        return button;
    }

    // Optional: Create other specific button types as needed
    // Example: Button with specific handlers or tooltips

    public static Button createEditButton() {
        Button button = new Button();
        button.getStyleClass().add("edit-button"); // Assuming 'edit-button' is a CSS class defined
        FontIcon icon = new FontIcon("fas-edit");
        icon.setIconSize(16);
        button.setGraphic(icon);
        // Add any specific listeners here if needed
        return button;
    }
}
