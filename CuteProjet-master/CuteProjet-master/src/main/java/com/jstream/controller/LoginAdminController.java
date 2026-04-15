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

import java.io.IOException;

public class LoginAdminController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    private AuthService authService = new AuthService();

    @FXML
    void handleLogin(ActionEvent event) {
        try {
            String email = emailField.getText();
            String password = passwordField.getText();
            if (email.isEmpty() || password.isEmpty()) { // test pour eviter les champs vide
                System.out.println("Veuillez remplir tous les champs !");
                return;
            }
            User loggedInUser = authService.login(email, password);
            if (loggedInUser != null) { //verification des identifiant
                SessionManager.setCurrentUser(loggedInUser);
                System.out.println("Connexion réussie pour : " + email);
                String fxmlACharger = SessionManager.isAdmin() ? "/fxml/Admin.fxml" : "/fxml/Admin.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlACharger));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.show();
            } else {
                System.out.println(" Identifiants incorrects !");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion ou du chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void goToUserLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("Erreur vers Login : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
