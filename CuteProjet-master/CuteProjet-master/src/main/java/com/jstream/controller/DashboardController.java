package com.jstream.controller;

import com.jstream.service.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // ✅ LISTE STATIQUE POUR SAUVEGARDER VOS FAVORIS ENTRE LES PAGES
    public static List<String> mesFavoris = new ArrayList<>();

    @FXML private StackPane heroStackPane;
    @FXML private ImageView heroImageView;
    @FXML private Label heroTitleLabel;
    @FXML private TextField searchField;

    @FXML private VBox myListSection;
    @FXML private VBox seriesSection;
    @FXML private VBox actionSection, comedieSection, sfSection, drameSection, horreurSection;

    @FXML private HBox myListBox;
    @FXML private HBox trendingSeriesBox;
    @FXML private HBox actionMoviesBox;
    @FXML private HBox comedieBox, scienceFictionBox, drameBox, horreurBox;

    private Timeline slideshow;
    private int currentIndex = 0;
    private List<Image> diaporamaImages = new ArrayList<>();
    private Image defaultImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            defaultImage = new Image(getClass().getResourceAsStream("/img/image.jpg.jpeg"));
            heroImageView.setImage(defaultImage);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            diaporamaImages.add(new Image(getClass().getResourceAsStream("/img/slide4.jpg.jpeg")));
            diaporamaImages.add(new Image(getClass().getResourceAsStream("/img/slide2.jpg.jpeg")));
            diaporamaImages.add(new Image(getClass().getResourceAsStream("/img/slide3.jpg.jpeg")));
        } catch (Exception e) { e.printStackTrace(); }

        slideshow = new Timeline(new KeyFrame(Duration.seconds(4.0), event -> {
            if (!diaporamaImages.isEmpty()) {
                currentIndex = (currentIndex + 1) % diaporamaImages.size();
                heroImageView.setImage(diaporamaImages.get(currentIndex));
            }
        }));
        slideshow.setCycleCount(Timeline.INDEFINITE);

        if (!diaporamaImages.isEmpty()) {
            heroImageView.setImage(diaporamaImages.get(0));
            slideshow.play();
        }

        // --- CHARGEMENT DES FILMS (Vos données statiques avec ajout des vidéos) ---
        scienceFictionBox.getChildren().addAll(
                createMovieCard("/img/inception.jpg.jpeg", "/videos/inception.mp4", false),
                createMovieCard("/img/interstellar.jpg.jpeg", "/videos/interstellar.mp4", false),
                createMovieCard("/img/matrix.jpg.jpeg", "/videos/matrix.mp4", false)
        );

        actionMoviesBox.getChildren().addAll(
                createMovieCard("/img/dark_knight.jpg.jpeg", "/videos/darkknight.mp4", false),
                createMovieCard("/img/avengers.jpg.jpeg", "/videos/avengers.mp4", false)
        );

        comedieBox.getChildren().addAll(
                createMovieCard("/img/hangover.jpg.jpeg", "/videos/hangover.mp4", false),
                createMovieCard("/img/superbad.jpg.jpeg", "/videos/superbad.mp4", false)
        );

        drameBox.getChildren().addAll(
                createMovieCard("/img/parasite.jpg.jpeg", "/videos/parasite.mp4", false),
                createMovieCard("/img/joker.jpg.jpeg", "/videos/joker.mp4", false)
        );

        horreurBox.getChildren().addAll(
                createMovieCard("/img/get_out.jpg.jpeg", "/videos/getout.mp4", false)
        );

        // --- SÉRIES ---
        // ✅ On ne garde QUE Breaking Bad et on supprime le remplissage automatique
        trendingSeriesBox.getChildren().add(createMovieCard("/img/breaking_bad.jpg.jpeg", "/videos/breakingbad.mp4", true));
        // chargerCarrouselGenerique(trendingSeriesBox, true); // <-- LIGNE COMMENTÉE POUR NE PLUS AFFICHER LES AUTRES IMAGES

        // --- MA LISTE (Dynamique) ---
        chargerMaListeDynamique();
    }

    private void chargerMaListeDynamique() {
        myListBox.getChildren().clear();

        if (mesFavoris.isEmpty()) {
            Label emptyMessage = new Label("Votre liste est vide. Ajoutez des films !");
            emptyMessage.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 14px; -fx-padding: 20px;");
            myListBox.getChildren().add(emptyMessage);
        } else {
            for (String imagePath : mesFavoris) {
                // Pour ma liste, on met une vidéo par défaut pour éviter les erreurs
                myListBox.getChildren().add(createMovieCard(imagePath, "/videos/default.mp4", false));
            }
        }
    }

    // ✅ MODIFIÉ : Ajout de l'argument "String videoPath"
    private StackPane createMovieCard(String imagePath, String videoPath, boolean isSerie) {
        ImageView poster = new ImageView();
        poster.setFitHeight(200.0);
        poster.setFitWidth(135.0);

        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            if (img.isError()) throw new Exception("Image introuvable");
            poster.setImage(img);
        } catch (Exception e) {
            poster.setImage(defaultImage);
        }

        Label badge = new Label(isSerie ? "SÉRIE" : "FILM");
        String badgeColor = isSerie ? "#e50914" : "#333333";
        badge.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 6 3 6; -fx-background-radius: 3;");

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(poster, badge);
        stackPane.setStyle("-fx-cursor: hand;");

        StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_LEFT);
        StackPane.setMargin(badge, new javafx.geometry.Insets(5, 0, 0, 5));

        stackPane.setOnMouseEntered(e -> { stackPane.setScaleX(1.05); stackPane.setScaleY(1.05); });
        stackPane.setOnMouseExited(e -> { stackPane.setScaleX(1.0); stackPane.setScaleY(1.0); });

        // Au clic sur une carte, on va vers Details.fxml
        stackPane.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Details.fxml"));
                Parent root = loader.load();

                DetailsController detailsController = loader.getController();
                // ✅ LA CORRECTION EST LÀ : on passe bien 3 arguments (image, VIDEO, serie)
                detailsController.initData(imagePath, videoPath, isSerie);

                Stage stage = (Stage) stackPane.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (IOException ex) { ex.printStackTrace(); }
        });

        return stackPane;
    }

    private void chargerCarrouselGenerique(HBox box, boolean forceSerie) {
        for (int i = 1; i <= 7; i++) {
            boolean isSerie = forceSerie || (i % 2 == 0);
            // On met une vidéo par défaut pour le remplissage générique
            box.getChildren().add(createMovieCard("/img/image.jpg.jpeg", "/videos/default.mp4", isSerie));
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void updateSectionsVisibility(boolean showList, boolean showMovies, boolean showSeries) {
        myListSection.setVisible(showList); myListSection.setManaged(showList);
        seriesSection.setVisible(showSeries); seriesSection.setManaged(showSeries);
        actionSection.setVisible(showMovies); actionSection.setManaged(showMovies);
        comedieSection.setVisible(showMovies); comedieSection.setManaged(showMovies);
        sfSection.setVisible(showMovies); sfSection.setManaged(showMovies);
        drameSection.setVisible(showMovies); drameSection.setManaged(showMovies);
        horreurSection.setVisible(showMovies); horreurSection.setManaged(showMovies);
    }

    @FXML
    private void openPlayer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Player.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    public void showAccueil(MouseEvent event) {
        heroTitleLabel.setText("Accueil");
        updateSectionsVisibility(true, true, true);
    }

    @FXML
    public void showSeries(MouseEvent event) {
        heroTitleLabel.setText("Toutes nos Séries");
        updateSectionsVisibility(false, false, true);
    }

    @FXML
    public void showFilms(MouseEvent event) {
        heroTitleLabel.setText("Tous nos Films");
        updateSectionsVisibility(false, true, false);
    }

    @FXML
    public void showMaListe(MouseEvent event) {
        heroTitleLabel.setText("Ma Liste de favoris");
        updateSectionsVisibility(true, false, false);
    }
}