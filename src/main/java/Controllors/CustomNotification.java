package Controllors;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class CustomNotification {

    private String title;
    private String message;
    private int duration;  // in seconds

    public CustomNotification(String title, String message, int duration) {
        this.title = title;
        this.message = message;
        this.duration = duration;
    }

    public void show(StackPane rootPane) {
        // Create the notification panel
        StackPane notificationPane = new StackPane();
        notificationPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 15;");
        notificationPane.setMinWidth(300);

        // Create a label to display the message
        Label messageLabel = new Label(title + "\n" + message);
        messageLabel.setTextFill(Color.WHITE);
        messageLabel.setFont(new Font("Arial", 14));

        notificationPane.getChildren().add(messageLabel);

        // Add notification pane to root stack
        rootPane.getChildren().add(notificationPane);

        // Ensure the notification is positioned correctly after layout
        rootPane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            notificationPane.setTranslateX(newValue.getWidth() - notificationPane.getWidth() - 20);
            notificationPane.setTranslateY(newValue.getHeight() - notificationPane.getHeight() - 20);
        });

        // Fade in animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> notificationPane.setOpacity(0)),
                new KeyFrame(Duration.seconds(0.3), event -> notificationPane.setOpacity(1)),
                new KeyFrame(Duration.seconds(duration), event -> fadeOutNotification(notificationPane))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void fadeOutNotification(Node notificationPane) {
        Timeline fadeOutTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> notificationPane.setOpacity(1)),
                new KeyFrame(Duration.seconds(1), event -> notificationPane.setOpacity(0)),
                new KeyFrame(Duration.seconds(2), event -> removeNotification(notificationPane))  // Augmenter ici la durée
        );
        fadeOutTimeline.setCycleCount(1);
        fadeOutTimeline.play();
    }

    private void removeNotification(Node notificationPane) {
        StackPane parent = (StackPane) notificationPane.getParent();
        parent.getChildren().remove(notificationPane);
    }

}
