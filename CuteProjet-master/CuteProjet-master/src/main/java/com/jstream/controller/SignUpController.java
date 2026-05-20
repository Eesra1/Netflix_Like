package com.jstream.controller;

import com.jstream.service.AuthService;
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

public class SignUpController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    private AuthService authService = new AuthService();

    @FXML
    void handleSignUp(ActionEvent event) {
        try {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                System.out.println("Veuillez remplir tous les champs !");
                return;
            }
            boolean isRegistered = authService.register(name, email, password);
            if (isRegistered) {
                handleLoginRedirect(event);
            } else {
                System.out.println("Cet email est déjà utilisé !");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'inscription : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleLoginRedirect(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur vers Login : " + e.getMessage());
            e.printStackTrace();
        }
    }
}