package com.jstream.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DetailsController implements Initializable {

    @FXML private ImageView bannerImage;
    @FXML private Label titleLabel, synopsisLabel, castLabel, dateLabel;
    @FXML private Button myListButton;
    @FXML private ComboBox<String> ratingComboBox;

    @FXML private VBox seriesSection;
    @FXML private ComboBox<String> seasonComboBox;
    @FXML private ListView<String> episodesListView;

    @FXML private TextArea commentTextArea;
    @FXML private VBox commentsContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ratingComboBox.setItems(FXCollections.observableArrayList("1 Étoile", "2 Étoiles", "3 Étoiles", "4 Étoiles", "5 Étoiles"));
        seasonComboBox.setItems(FXCollections.observableArrayList("Saison 1", "Saison 2"));
        seasonComboBox.getSelectionModel().selectFirst();
        episodesListView.setItems(FXCollections.observableArrayList(
                "1. Le commencement (45 min) - Vu",
                "2. La trahison (50 min) - En cours",
                "3. Le dénouement (48 min)"
        ));
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void playVideo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Player.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleMyList(ActionEvent event) {
        System.out.println("Ajouté / Retiré de Ma Liste !");
        if (myListButton.getText().equals("+ Ma Liste")) {
            myListButton.setText("✔ Dans Ma Liste");
            myListButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        } else {
            myListButton.setText("+ Ma Liste");
            myListButton.setStyle("-fx-background-color: rgba(109, 109, 110, 0.7); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        }
    }

    @FXML
    private void submitRating(ActionEvent event) {
        String rating = ratingComboBox.getValue();
        if (rating != null) {
            System.out.println("Vous avez noté : " + rating);
        }
    }

    @FXML
    private void postComment(ActionEvent event) {
        String texte = commentTextArea.getText();
        if (!texte.trim().isEmpty()) {
            System.out.println("Nouveau commentaire : " + texte);
            commentTextArea.clear();
            // TODO : plus tard inserer dans la base de donnes et l'afficher
        }
    }
}