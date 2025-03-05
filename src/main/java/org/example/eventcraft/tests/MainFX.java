package org.example.eventcraft.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Correct path to FXML file (ensure the FXML file is in the correct directory)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/eventcraft/offre.fxml"));

            // Load the FXML file
            Parent root = fxmlLoader.load();

            // Set up the scene
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Offre Management");

            // Show the stage
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
