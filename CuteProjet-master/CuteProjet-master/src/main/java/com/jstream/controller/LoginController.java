package com.jstream.controller;

import com.jstream.model.User;
import com.jstream.service.AuthService;
import com.jstream.service.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private AuthService authService = new AuthService();

    @FXML
    void handleLogin(ActionEvent event) {
        try {
            String email = emailField.getText();
            String password = passwordField.getText();

            User loggedInUser = authService.login(email, password);

            if (loggedInUser != null) {
                SessionManager.setCurrentUser(loggedInUser);

                String fxmlACharger;

                if (SessionManager.isAdmin()) {
                    System.out.println("Connexion Admin réussie !");
                    fxmlACharger = "/fxml/AdminDashboard.fxml";
                } else {
                    System.out.println("Connexion Utilisateur réussie !");
                    fxmlACharger = "/fxml/Dashboard.fxml";
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlACharger));
                Parent root = loader.load();
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(new Scene(root));
                window.show();

            } else {
                System.out.println("Identifiants incorrects");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToRegister(ActionEvent event) {
        try {
            System.out.println("Redirection vers la page Créer un compte...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SignUp.fxml"));
            Parent root = loader.load();

            Stage window = (Stage) ((javafx.scene.control.Hyperlink) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root));
            window.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ouverture de la page d'inscription.");
        }
    }
}