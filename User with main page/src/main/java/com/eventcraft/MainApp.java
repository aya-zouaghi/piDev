package com.eventcraft;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the signup FXML file (corrected path)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));


        Parent root = loader.load();

        // Set the scene
        Scene scene = new Scene(root);

        // Load the CSS file
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());


        // Set the stage
        primaryStage.setTitle("Event Craft");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true); // Optional: Prevent resizing of the window
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
