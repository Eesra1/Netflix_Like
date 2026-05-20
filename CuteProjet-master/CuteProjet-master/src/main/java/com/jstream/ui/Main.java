package com.jstream.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        loadAndShowWindow("/fxml/Login.fxml", "Movix - Dashboard Admin", primaryStage);
    }

    private void loadAndShowWindow(String fxmlPath, String title, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface " + fxmlPath + " :");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}