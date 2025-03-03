package com.eventcraft.controller;

import com.eventcraft.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    private User currentUser;

    public void initData(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getPrenom() + " " + user.getNom() + "!");
    }
}