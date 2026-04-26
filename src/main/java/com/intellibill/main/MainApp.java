package com.intellibill.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/com/intellibill/ui/dashboard.fxml"));
        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(MainApp.class.getResource("/com/intellibill/ui/app-theme.css").toExternalForm());
        stage.setTitle("IntelliBill - Smart Utility Billing");
        stage.setMinWidth(980);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
