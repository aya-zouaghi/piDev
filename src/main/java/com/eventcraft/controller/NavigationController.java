import java.util.Stack;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class NavigationController {
    private Stack<String> backStack = new Stack<>();
    private Stack<String> forwardStack = new Stack<>();
    private String currentPage;
    private BorderPane mainLayout;
    private Stage primaryStage;

    public NavigationController(Stage stage) {
        this.primaryStage = stage;
        mainLayout = new BorderPane();

        // Create navigation bar with back/forward arrows
        HBox navigationBar = createNavigationBar();
        mainLayout.setTop(navigationBar);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
    }

    private HBox createNavigationBar() {
        HBox navigationBar = new HBox();
        navigationBar.setPadding(new Insets(10));
        navigationBar.setStyle("-fx-background-color: #f0f0f0;");

        // Back button
        Button backButton = new Button("←");
        backButton.setOnAction(e -> navigateBack());
        backButton.setDisable(true);

        // Forward button
        Button forwardButton = new Button("→");
        forwardButton.setOnAction(e -> navigateForward());
        forwardButton.setDisable(true);

        // Current page indicator
        Text pageIndicator = new Text("Home");

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        navigationBar.getChildren().addAll(backButton, forwardButton, spacer, pageIndicator);

        // Store references to buttons for enabling/disabling
        backButton.setId("backButton");
        forwardButton.setId("forwardButton");
        pageIndicator.setId("pageIndicator");

        return navigationBar;
    }

    public void navigate(String fxmlPath) {
        try {
            // Add current page to back stack before navigating
            if (currentPage != null) {
                backStack.push(currentPage);
                enableDisableButtons();
            }

            // Clear forward stack when navigating to new page
            if (!navigatingWithArrows) {
                forwardStack.clear();
                enableDisableButtons();
            }

            // Load new page
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent page = loader.load();
            mainLayout.setCenter(page);

            // Update current page
            currentPage = fxmlPath;

            // Update page indicator
            Text pageIndicator = (Text) mainLayout.lookup("#pageIndicator");
            String pageName = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1, fxmlPath.lastIndexOf('.'));
            pageIndicator.setText(pageName);

            navigatingWithArrows = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean navigatingWithArrows = false;

    public void navigateBack() {
        if (!backStack.isEmpty()) {
            navigatingWithArrows = true;
            String previousPage = backStack.pop();
            forwardStack.push(currentPage);
            navigate(previousPage);
            enableDisableButtons();
        }
    }

    public void navigateForward() {
        if (!forwardStack.isEmpty()) {
            navigatingWithArrows = true;
            String nextPage = forwardStack.pop();
            backStack.push(currentPage);
            navigate(nextPage);
            enableDisableButtons();
        }
    }

    private void enableDisableButtons() {
        Button backButton = (Button) mainLayout.lookup("#backButton");
        Button forwardButton = (Button) mainLayout.lookup("#forwardButton");

        if (backButton != null) {
            backButton.setDisable(backStack.isEmpty());
        }

        if (forwardButton != null) {
            forwardButton.setDisable(forwardStack.isEmpty());
        }
    }

    public void start(String initialFxml) {
        navigate(initialFxml);
        primaryStage.show();
    }
}