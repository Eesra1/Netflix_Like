package com.jstream.controller;

import com.jstream.service.AuthService; // <-- Import du service
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button signUpButton;
    @FXML private Hyperlink loginLink;

    // Instanciation du service d'authentification
    private AuthService authService = new AuthService();

    @FXML
    void handleSignUp(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("Erreur : Tous les champs doivent être remplis.");
            return;
        }

        try {
            // On utilise la méthode register() du service
            boolean success = authService.register(name, email, password);

            if (success) {
                System.out.println("✅ Nouveau compte (" + name + ") créé avec succès !");
                goToLogin(event); // Redirection
            } else {
                System.out.println("❌ Erreur : Cet email est peut-être déjà utilisé.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erreur serveur lors de l'inscription.");
        }
    }

    @FXML
    void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}