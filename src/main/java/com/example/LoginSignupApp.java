package com.example;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.sql.*;
import java.util.prefs.Preferences;

public class LoginSignupApp extends Application {
    private Stage stage;
    private Scene getStartedScene, signUpScene, signInScene;
    private Connection connection;
    private Preferences preferences;
    private String loggedInUsername; 

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        preferences = Preferences.userNodeForPackage(LoginSignupApp.class);
    
        initializeDBConnection();
    
        getStartedScene = createGetStartedScene();
        signUpScene = createSignUpScene();
        signInScene = createSignInScene();
    
        // Set initial scene
        stage.setScene(getStartedScene);
        stage.setTitle("Animated Login and Signup");
        stage.show();
    }
    
    

    private void initializeDBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/car", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Apply Fade and Slide Animations
    private void applyAnimations(Pane root) {
        root.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.seconds(1), root);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1), root);
        slide.setFromY(50);
        slide.setToY(0);

        fade.play();
        slide.play();
    }

    // Create Get Started Scene
    private Scene createGetStartedScene() {
        // Base layout with cool blue gradient background
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #0F2027, #203A43, #2C5364);");

        // Create shadowed container box
        Rectangle shadowBox = new Rectangle(500, 450);
        shadowBox.setArcWidth(25);
        shadowBox.setArcHeight(25);
        shadowBox.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#E0F7FA")), // Light Cyan
                new Stop(1, Color.web("#B2EBF2")))); // Sky Blue
        shadowBox.setEffect(new javafx.scene.effect.DropShadow(20, Color.web("#0F2027"))); // Shadow color

        // Animated Title
        Label title = new Label("Get Started");
        title.setFont(Font.font("Arial", 48));
        title.setTextFill(Color.web("#0D47A1")); // Navy Blue
        applyZigzagAnimation(title);

        // Subtitle
        Label subtitle = new Label("Start with sign up or sign in");
        subtitle.setFont(Font.font("Arial", 18));
        subtitle.setTextFill(Color.web("#455A64")); // Cool Grey

        // Buttons with cool blue gradient
        Button signUpButton = createGradientButton("SIGN UP", e -> switchScene(signUpScene));
        Button signInButton = createGradientButton("SIGN IN", e -> switchScene(signInScene));

        // Copyright Label
        Label copyrightLabel = new Label("© 2024 Kareem Abuayyash. All rights reserved.");
        copyrightLabel.setTextFill(Color.web("#90A4AE")); // Light Grey
        copyrightLabel.setFont(Font.font("Arial", 12));

        // Content inside shadow box
        VBox contentBox = new VBox(20, title, subtitle, signUpButton, signInButton, copyrightLabel);
        contentBox.setAlignment(Pos.CENTER);

        // StackPane to layer shadow box and content
        StackPane centeredBox = new StackPane();
        centeredBox.getChildren().addAll(shadowBox, contentBox);

        // Add the centered box to the base layout
        layout.getChildren().add(centeredBox);

        applyAnimations(layout);

        return new Scene(layout, 1550, 800);
    }

    private Button createGradientButton(String text, EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setOnAction(action);
        button.setPrefSize(300, 50);
        button.setStyle("-fx-background-color: linear-gradient(to right, #1E88E5, #0D47A1); "
                + "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;"
                + "-fx-background-radius: 25; -fx-border-color: transparent;");
        button.setOnMouseEntered(
                e -> button.setStyle("-fx-background-color: linear-gradient(to right, #42A5F5, #1E88E5); "
                        + "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;"
                        + "-fx-background-radius: 25; -fx-border-color: transparent;"));
        button.setOnMouseExited(
                e -> button.setStyle("-fx-background-color: linear-gradient(to right, #1E88E5, #0D47A1); "
                        + "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;"
                        + "-fx-background-radius: 25; -fx-border-color: transparent;"));
        return button;
    }

    // Create Sign Up Scene
    private Scene createSignUpScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(Insets.EMPTY); // Remove extra padding

        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #0F2027, #203A43, #2C5364);");
    
        Rectangle shadowBox = new Rectangle(500, 450);
        shadowBox.setArcWidth(25);
        shadowBox.setArcHeight(25);
        shadowBox.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#E0F7FA")),
                new Stop(1, Color.web("#B2EBF2"))));
        shadowBox.setEffect(new javafx.scene.effect.DropShadow(20, Color.web("#0F2027")));
    
        Label title = new Label("Sign Up");
        title.setFont(Font.font("Arial", 48));
        title.setTextFill(Color.web("#0D47A1"));
        applyZigzagAnimation(title);
    
        Label subtitle = new Label("Create a new account");
        subtitle.setFont(Font.font("Arial", 18));
        subtitle.setTextFill(Color.web("#455A64"));
    
        TextField usernameField = createInputField("Username");
        Label usernameErrorLabel = new Label();
        usernameErrorLabel.setTextFill(Color.RED);
    
        PasswordField passwordField = createPasswordField("Password");
        Label passwordErrorLabel = new Label();
        passwordErrorLabel.setTextFill(Color.RED);
    
        PasswordField confirmPasswordField = createPasswordField("Confirm Password");
        Label confirmPasswordErrorLabel = new Label();
        confirmPasswordErrorLabel.setTextFill(Color.RED);
    
        Button signUpButton = createGradientButton("SIGN UP", e -> {
            boolean isValid = true;
    
            // Validate username
            if (usernameField.getText().trim().isEmpty()) {
                usernameErrorLabel.setText("Username cannot be empty.");
                isValid = false;
            } else {
                usernameErrorLabel.setText("");
            }
    
            // Validate password
            if (passwordField.getText().trim().isEmpty()) {
                passwordErrorLabel.setText("Password cannot be empty.");
                isValid = false;
            } else {
                passwordErrorLabel.setText("");
            }
    
            // Validate confirm password
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                confirmPasswordErrorLabel.setText("Passwords do not match.");
                isValid = false;
            } else {
                confirmPasswordErrorLabel.setText("");
            }
    
            // Register user if valid
            if (isValid) {
                if (registerUser(usernameField.getText(), passwordField.getText())) {
                    switchScene(signInScene);
                } else {
                    usernameErrorLabel.setText("Signup failed. User already exists.");
                }
            }
        });
    
        Hyperlink goToSignIn = new Hyperlink("You already have an account? Sign in");
        goToSignIn.setTextFill(Color.web("#90A4AE"));
        goToSignIn.setOnAction(e -> switchScene(signInScene));
    
        VBox contentBox = new VBox(10, title, subtitle, 
                usernameField, usernameErrorLabel, 
                passwordField, passwordErrorLabel, 
                confirmPasswordField, confirmPasswordErrorLabel, 
                signUpButton, goToSignIn);
    
        contentBox.setAlignment(Pos.CENTER);
    
        StackPane centeredBox = new StackPane();
        centeredBox.getChildren().addAll(shadowBox, contentBox);
    
        layout.getChildren().add(centeredBox);
    
        applyAnimations(layout);
    
        return new Scene(layout, 1550, 800);
    }
    

    private boolean registerUser(String username, String password) {
        String query = "INSERT INTO user_account (username, password) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Create Sign In Scene
    private Scene createSignInScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #0F2027, #203A43, #2C5364);");
    
        Rectangle shadowBox = new Rectangle(500, 450);
        shadowBox.setArcWidth(25);
        shadowBox.setArcHeight(25);
        shadowBox.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#E0F7FA")),
                new Stop(1, Color.web("#B2EBF2"))));
        shadowBox.setEffect(new javafx.scene.effect.DropShadow(20, Color.web("#0F2027")));
    
        Label title = new Label("Sign In");
        title.setFont(Font.font("Arial", 48));
        title.setTextFill(Color.web("#0D47A1"));
        applyZigzagAnimation(title);
    
        Label subtitle = new Label("Welcome back!");
        subtitle.setFont(Font.font("Arial", 18));
        subtitle.setTextFill(Color.web("#455A64"));
    
        TextField usernameField = createInputField("Username");
        PasswordField passwordField = createPasswordField("Password");
    
        // Pre-fill saved credentials
        String savedUsername = preferences.get("username", "");
        String savedPassword = preferences.get("password", "");
        if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
            usernameField.setText(savedUsername);
            passwordField.setText(savedPassword);
        }
    
        Label usernameErrorLabel = new Label();
        usernameErrorLabel.setTextFill(Color.RED);
        Label passwordErrorLabel = new Label();
        passwordErrorLabel.setTextFill(Color.RED);
    
        // Remember Me CheckBox
        CheckBox rememberMeCheckBox = new CheckBox("Remember Me");
        rememberMeCheckBox.setTextFill(Color.web("#90A4AE"));
        rememberMeCheckBox.setSelected(!savedUsername.isEmpty() && !savedPassword.isEmpty());
    
        Button signInButton = createGradientButton("SIGN IN", e -> {
            boolean isValid = true;
    
            // Validate username
            if (usernameField.getText().trim().isEmpty()) {
                usernameErrorLabel.setText("Username cannot be empty.");
                isValid = false;
            } else {
                usernameErrorLabel.setText("");
            }
    
            // Validate password
            if (passwordField.getText().trim().isEmpty()) {
                passwordErrorLabel.setText("Password cannot be empty.");
                isValid = false;
            } else {
                passwordErrorLabel.setText("");
            }
    
            // Authenticate if valid
            if (isValid) {
                if (validateLogin(usernameField.getText(), passwordField.getText())) {
                    // Save credentials if Remember Me is selected
                    if (rememberMeCheckBox.isSelected()) {
                        preferences.put("username", usernameField.getText());
                        preferences.put("password", passwordField.getText());
                    } else {
                        preferences.remove("username");
                        preferences.remove("password");
                    }
                    openCarShopGUI();
                } else {
                    usernameErrorLabel.setText("Invalid username or password.");
                }
            }
        });
    
        Hyperlink goToSignUp = new Hyperlink("Don’t have an account? Sign up");
        goToSignUp.setTextFill(Color.web("#90A4AE"));
        goToSignUp.setOnAction(e -> switchScene(signUpScene));
    
        VBox contentBox = new VBox(10, title, subtitle, 
                usernameField, usernameErrorLabel, 
                passwordField, passwordErrorLabel, 
                rememberMeCheckBox, 
                signInButton, goToSignUp);
    
        contentBox.setAlignment(Pos.CENTER);
    
        StackPane centeredBox = new StackPane();
        centeredBox.getChildren().addAll(shadowBox, contentBox);
    
        layout.getChildren().add(centeredBox);
    
        applyAnimations(layout);
    
        return new Scene(layout, 1550, 800);
    }
    
    

    // Declare this at the class level
    private boolean rememberMeSelected = false;

    private void openCarShopGUI() {
        CarShopGUI carShopGUI = new CarShopGUI(loggedInUsername); // Pass the username
        try {
            Stage carShopStage = new Stage();
            carShopGUI.start(carShopStage);
            carShopStage.setMaximized(true); // Open CarShopGUI maximized
            stage.close(); // Close current login window
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM user_account WHERE username = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    loggedInUsername = username; // Set the logged-in username
    
                    // Save credentials if Remember Me is selected
                    if (rememberMeSelected) {
                        preferences.put("username", username);
                        preferences.put("password", password);
                    } else {
                        preferences.remove("username");
                        preferences.remove("password");
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    

    // Scene Switching Helper
    private void switchScene(Scene newScene) {
        System.out.println("Switching to new scene...");
        applyAnimations((Pane) newScene.getRoot());
        stage.setScene(newScene);
    }

    // Create Base Layout
    // Create Base Layout
    private VBox createBaseLayout(String title, String subtitle) {
        VBox layout = new VBox(20); // Main layout with spacing
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #20003A, #5B0B4E, #F4A1A5);");

        // Title Label with Animation
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(48));
        titleLabel.setTextFill(Color.WHITE);
        applyZigzagAnimation(titleLabel); // Optional animation for title

        // Subtitle Label
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font(18));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);

        // Copyright Label
        Label copyrightLabel = new Label("© 2024 Kareem Abuayyash. All rights reserved.");
        copyrightLabel.setTextFill(Color.BLACK);
        copyrightLabel.setFont(Font.font(12));

        // Add elements in proper order
        layout.getChildren().addAll(titleLabel, subtitleLabel); // Title & subtitle first
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private void applyZigzagAnimation(Label label) {
        // Horizontal Movement
        TranslateTransition moveRight = new TranslateTransition(Duration.seconds(0.5), label);
        moveRight.setFromX(0);
        moveRight.setToX(30);
        moveRight.setAutoReverse(true);
        moveRight.setCycleCount(2);

        // Vertical Movement
        TranslateTransition moveDown = new TranslateTransition(Duration.seconds(0.5), label);
        moveDown.setFromY(0);
        moveDown.setToY(20);
        moveDown.setAutoReverse(true);
        moveDown.setCycleCount(2);

        // Combine Animations in Sequence
        SequentialTransition zigzag = new SequentialTransition(moveRight, moveDown);
        zigzag.setCycleCount(TranslateTransition.INDEFINITE); // Repeat forever
        zigzag.play();
    }

    // Create Buttons
    private Button createButton(String text, EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setOnAction(action);
        button.setPrefSize(300, 50);
        button.setStyle("-fx-background-color: linear-gradient(to right, #A4508B, #5B0B4E); "
                + "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;"
                + "-fx-background-radius: 25; -fx-border-color: transparent;");
        button.setOnMouseEntered(
                e -> button.setStyle("-fx-background-color: linear-gradient(to right, #B56DA8, #6A0572); "
                        + "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;"
                        + "-fx-background-radius: 25; -fx-border-color: transparent;"));
        button.setOnMouseExited(
                e -> button.setStyle("-fx-background-color: linear-gradient(to right, #A4508B, #5B0B4E); "
                        + "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;"
                        + "-fx-background-radius: 25; -fx-border-color: transparent;"));
        return button;
    }

    // Create Input Fields
    private TextField createInputField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        styleInputField(field);
        return field;
    }

    private PasswordField createPasswordField(String placeholder) {
        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
        styleInputField(field);
        return field;
    }

    private void styleInputField(TextField field) {
        field.setPrefSize(350, 40); // Make the input fields smaller
        field.setMaxWidth(350); // Set a fixed max width
        field.setAlignment(Pos.CENTER); // Center text within the field
        field.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); "
                + "-fx-background-radius: 20; -fx-border-color: transparent; -fx-font-size: 14;");
    }

    // Create Links
    private Hyperlink createLink(String text, Scene targetScene) {
        Hyperlink link = new Hyperlink(text);
        link.setTextFill(Color.LIGHTGRAY);
        link.setOnAction(e -> {
            if (targetScene != null) {
                switchScene(targetScene);
            }
        });
        return link;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

