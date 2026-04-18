package com.jstream.controller;

import com.jstream.dao.CommentDAO;
import com.jstream.dao.WatchlistDAO;
import com.jstream.service.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    // ── Contexte du contenu affiché ──────────────────────────────────────────
    /** Valeurs injectées depuis la page précédente (null = non renseigné). */
    private static Integer currentFilmId    = null;
    private static Integer currentSeriesId  = null;
    private static Integer currentEpisodeId = null;
    private static String  currentTitle     = "TITRE";
    private static String  currentType      = "FILM"; // "FILM" | "SÉRIE" | "ÉPISODE"

    private final WatchlistDAO watchlistDAO = new WatchlistDAO();
    private final CommentDAO   commentDAO   = new CommentDAO();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Méthodes statiques pour injecter le contexte depuis d'autres contrôleurs ──
    public static void showFilm(int filmId, String title) {
        currentFilmId    = filmId;
        currentSeriesId  = null;
        currentEpisodeId = null;
        currentTitle     = title;
        currentType      = "FILM";
    }

    public static void showSeries(int seriesId, String title) {
        currentFilmId    = null;
        currentSeriesId  = seriesId;
        currentEpisodeId = null;
        currentTitle     = title;
        currentType      = "SÉRIE";
    }

    public static void showEpisode(int episodeId, String title) {
        currentFilmId    = null;
        currentSeriesId  = null;
        currentEpisodeId = episodeId;
        currentTitle     = title;
        currentType      = "ÉPISODE";
    }

    // ── Initialisation ───────────────────────────────────────────────────────
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ratingComboBox.setItems(FXCollections.observableArrayList(
                "1 Étoile", "2 Étoiles", "3 Étoiles", "4 Étoiles", "5 Étoiles"));
        seasonComboBox.setItems(FXCollections.observableArrayList("Saison 1", "Saison 2"));
        seasonComboBox.getSelectionModel().selectFirst();

        // Affichage du titre
        if (titleLabel != null) titleLabel.setText(currentTitle);

        // Mise à jour du bouton "Ma Liste"
        refreshWatchlistButton();

        // Chargement des commentaires existants
        loadComments();
    }

    // ── Watchlist : bouton ♥ ────────────────────────────────────────────────
    private void refreshWatchlistButton() {
        if (myListButton == null) return;
        if (SessionManager.getCurrentUser() == null) return;

        int userId = SessionManager.getCurrentUser().getId();
        try {
            boolean inList = false;
            if (currentFilmId    != null) inList = watchlistDAO.isFilmInList(userId, currentFilmId);
            else if (currentSeriesId  != null) inList = watchlistDAO.isSeriesInList(userId, currentSeriesId);
            else if (currentEpisodeId != null) inList = watchlistDAO.isEpisodeInList(userId, currentEpisodeId);

            if (inList) {
                myListButton.setText("✔ Dans Ma Liste");
                myListButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
            } else {
                myListButton.setText("+ Ma Liste");
                myListButton.setStyle("-fx-background-color: rgba(109,109,110,0.7); -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleMyList(ActionEvent event) {
        if (SessionManager.getCurrentUser() == null) {
            showInfo("Vous devez être connecté pour sauvegarder un contenu.");
            return;
        }
        int userId = SessionManager.getCurrentUser().getId();
        try {
            if (currentFilmId != null) {
                if (watchlistDAO.isFilmInList(userId, currentFilmId))
                    watchlistDAO.removeFilm(userId, currentFilmId);
                else
                    watchlistDAO.addFilm(userId, currentFilmId);

            } else if (currentSeriesId != null) {
                if (watchlistDAO.isSeriesInList(userId, currentSeriesId))
                    watchlistDAO.removeSeries(userId, currentSeriesId);
                else
                    watchlistDAO.addSeries(userId, currentSeriesId);

            } else if (currentEpisodeId != null) {
                if (watchlistDAO.isEpisodeInList(userId, currentEpisodeId))
                    watchlistDAO.removeEpisode(userId, currentEpisodeId);
                else
                    watchlistDAO.addEpisode(userId, currentEpisodeId);
            }
            refreshWatchlistButton();

        } catch (Exception e) {
            e.printStackTrace();
            showInfo("Erreur lors de la mise à jour de votre liste.");
        }
    }

    // ── Commentaires ─────────────────────────────────────────────────────────
    @FXML
    private void postComment(ActionEvent event) {
        if (SessionManager.getCurrentUser() == null) {
            showInfo("Vous devez être connecté pour commenter.");
            return;
        }
        String text = commentTextArea.getText().trim();
        if (text.isEmpty()) return;

        int userId = SessionManager.getCurrentUser().getId();
        try {
            if (currentFilmId    != null) commentDAO.addFilmComment(userId, currentFilmId, text);
            else if (currentSeriesId  != null) commentDAO.addSeriesComment(userId, currentSeriesId, text);
            else if (currentEpisodeId != null) commentDAO.addEpisodeComment(userId, currentEpisodeId, text);

            commentTextArea.clear();
            loadComments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadComments() {
        if (commentsContainer == null) return;
        commentsContainer.getChildren().clear();
        try {
            List<com.jstream.model.Comment> comments = List.of();
            if      (currentFilmId    != null) comments = commentDAO.findByFilm(currentFilmId);
            else if (currentSeriesId  != null) comments = commentDAO.findBySeries(currentSeriesId);
            else if (currentEpisodeId != null) comments = commentDAO.findByEpisode(currentEpisodeId);

            for (com.jstream.model.Comment c : comments) {
                VBox card = new VBox(4);
                card.setStyle("-fx-background-color: #222; -fx-padding: 10; -fx-background-radius: 5;");
                card.setPadding(new Insets(10));

                String dateStr = c.getCreatedAt() != null ? c.getCreatedAt().format(FMT) : "";
                Label header = new Label("Utilisateur #" + c.getUserId() + "  ·  " + dateStr);
                header.setStyle("-fx-text-fill: #e50914; -fx-font-size: 12px; -fx-font-weight: bold;");

                Label body = new Label(c.getContent());
                body.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
                body.setWrapText(true);

                card.getChildren().addAll(header, body);
                commentsContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Navigation ───────────────────────────────────────────────────────────
    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void playVideo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Player.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void submitRating(ActionEvent event) {
        String rating = ratingComboBox.getValue();
        if (rating != null) System.out.println("Note : " + rating);
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    public void initData(String imagePath, String videoPath, boolean isSerie) {
    }
}