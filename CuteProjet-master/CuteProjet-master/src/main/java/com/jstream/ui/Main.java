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
        // 1. Afficher la première interface (Admin.fxml) dans la fenêtre principale
        loadAndShowWindow("/fxml/Admin.fxml", "Movix - Dashboard Admin", primaryStage);


    }

    /**
     * Méthode utilitaire pour charger un FXML et l'afficher dans une fenêtre donnée.
     */
    private void loadAndShowWindow(String fxmlPath, String title, Stage stage) {
        try {
            // On charge le fichier FXML depuis le dossier des ressources
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // On crée la scène avec la taille définie dans votre FXML
            Scene scene = new Scene(root);

            // On configure la fenêtre
            stage.setTitle(title);
            stage.setScene(scene);

            // Empêcher le redimensionnement pour garder le design intact
            stage.setResizable(true);

            // On affiche la fenêtre
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface " + fxmlPath + " :");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Cette méthode lance l'application JavaFX
        launch(args);
    }
}