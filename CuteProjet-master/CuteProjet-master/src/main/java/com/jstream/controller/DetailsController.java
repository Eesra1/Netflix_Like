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
import javafx.scene.image.Image;
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

    private String currentMovieImagePath;
    private static Integer currentFilmId = null;
    private static Integer currentSeriesId = null;
    private static String currentTitle = "Chargement...";

    // ✅ NOUVEAU : Stocke le chemin de la vidéo à lancer
    private String currentVideoPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Initialisation des menus déroulants (ComboBox)
        if (ratingComboBox != null) {
            ratingComboBox.setItems(FXCollections.observableArrayList(
                    "1 Étoile", "2 Étoiles", "3 Étoiles", "4 Étoiles", "5 Étoiles"
            ));
        }

        // 2. Affichage du titre envoyé par le Dashboard
        if (titleLabel != null) {
            titleLabel.setText(currentTitle);
        }

        // 3. Gestion de l'affichage (Série vs Film)
        if (seriesSection != null) {
            if (currentSeriesId != null) {
                // C'est une série : on montre la section des épisodes
                seriesSection.setVisible(true);
                seriesSection.setManaged(true);

                if (seasonComboBox != null) {
                    seasonComboBox.setItems(FXCollections.observableArrayList("Saison 1", "Saison 2"));
                    seasonComboBox.getSelectionModel().selectFirst();
                }
                if (episodesListView != null) {
                    episodesListView.setItems(FXCollections.observableArrayList(
                            "1. Le commencement (45 min)",
                            "2. La trahison (50 min)",
                            "3. Le dénouement (48 min)"
                    ));
                }
            } else {
                // C'est un film : on cache toute la section Épisodes
                seriesSection.setVisible(false);
                seriesSection.setManaged(false);
            }
        }


    }

    /**
     * ✅ MODIFIÉ : On ajoute "String videoPath" en paramètre pour recevoir la vidéo depuis le Dashboard.
     */
    public void initData(String imagePath, String videoPath, boolean isSerie) {
        this.currentMovieImagePath = imagePath;
        this.currentVideoPath = videoPath; // On sauvegarde la vidéo

        // 1. Mettre à jour l'image de la bannière
        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            if (!img.isError()) {
                bannerImage.setImage(img);
            }
        } catch (Exception e) {
            System.out.println("Image non trouvée pour les détails : " + imagePath);
        }

        // 2. Si le film est déjà dans la liste, on met le bouton en vert tout de suite
        if (DashboardController.mesFavoris.contains(currentMovieImagePath)) {
            myListButton.setText("✔ Dans Ma Liste");
            myListButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        }

        // 3. Masquer ou afficher la section des épisodes selon si c'est une série ou non
        if (seriesSection != null) {
            seriesSection.setVisible(isSerie);
            seriesSection.setManaged(isSerie); // retire l'espace vide laissé par la VBox cachée
        }
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

            // ✅ NOUVEAU : On récupère le contrôleur du Player pour lui injecter la bonne vidéo
            PlayerController playerController = loader.getController();
            if (currentVideoPath != null && !currentVideoPath.isEmpty()) {
                playerController.initVideo(currentVideoPath);
            } else {
                System.out.println("⚠️ Aucune vidéo associée à ce film !");
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleMyList(ActionEvent event) {
        if (currentMovieImagePath == null) return;

        if (!DashboardController.mesFavoris.contains(currentMovieImagePath)) {
            DashboardController.mesFavoris.add(currentMovieImagePath);
            myListButton.setText("✔ Dans Ma Liste");
            myListButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
            System.out.println("Ajouté à Ma Liste : " + currentMovieImagePath);
        } else {
            DashboardController.mesFavoris.remove(currentMovieImagePath);
            myListButton.setText("+ Ma Liste");
            myListButton.setStyle("-fx-background-color: rgba(109, 109, 110, 0.7); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
            System.out.println("Retiré de Ma Liste : " + currentMovieImagePath);
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
        }
    }
    public static void showFilm(int id, String title) {
        currentFilmId = id;
        currentSeriesId = null; // On remet la série à null car c'est un film
        currentTitle = title;
    }
    public static void showSeries(int id, String title) {
        currentSeriesId = id;
        currentFilmId = null; // On remet le film à null car c'est une série
        currentTitle = title;
    }

    public void initVideo(ActionEvent event) {
    }
}