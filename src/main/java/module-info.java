module tn.esprit {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens com.example.eventcraft to javafx.graphics;
    opens org.example.controller to javafx.fxml;
            exports org.example.controller;


}