module org.example.eventcraft {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jfr;

    exports org.example.eventcraft;  // Export the main package
    exports org.example.eventcraft.controllers;  // Export the controllers package for JavaFX
    exports org.example.eventcraft.tests;  // Export the tests package for access by JavaFX

    opens org.example.eventcraft to javafx.fxml;  // Allow JavaFX to access the main package
    opens org.example.eventcraft.controllers to javafx.fxml;  // Allow JavaFX to access the controllers package
    opens org.example.eventcraft.tests to javafx.fxml;  // Allow JavaFX to access the tests package
}

