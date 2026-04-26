package com.intellibill.ui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;

import java.io.IOException;

public final class AppNavigator {
    private static BorderPane dashboardRoot;

    private AppNavigator() {
    }

    public static void setDashboardRoot(BorderPane root) {
        dashboardRoot = root;
    }

    public static void showInCenter(String fxmlPath) {
        if (dashboardRoot == null) {
            throw new IllegalStateException("Dashboard root is not initialized.");
        }
        try {
            Parent view = FXMLLoader.load(AppNavigator.class.getResource(fxmlPath));
            ScrollPane scrollPane = new ScrollPane(view);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPannable(true);
            scrollPane.getStyleClass().add("center-scroll");

            scrollPane.setOpacity(0);
            dashboardRoot.setCenter(scrollPane);
            FadeTransition transition = new FadeTransition(Duration.millis(260), scrollPane);
            transition.setFromValue(0);
            transition.setToValue(1);
            transition.play();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load FXML: " + fxmlPath, ex);
        }
    }
}
