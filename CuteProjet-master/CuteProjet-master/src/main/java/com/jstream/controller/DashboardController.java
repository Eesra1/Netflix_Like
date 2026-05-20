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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    public static List<String> mesFavoris = new ArrayList<>();

    @FXML private StackPane heroStackPane;
    @FXML private ImageView heroImageView;
    @FXML private Label heroTitleLabel;
    @FXML private TextField searchField;

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            diaporamaImages.add(new Image(getClass().getResourceAsStream("/img/slide4.jpg.jpeg")));
            diaporamaImages.add(new Image(getClass().getResourceAsStream("/img/slide2.jpg.jpeg")));
            diaporamaImages.add(new Image(getClass().getResourceAsStream("/img/slide3.jpg.jpeg")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        slideshow = new Timeline(new KeyFrame(Duration.seconds(4), e -> {
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
                createMovieCard("/img/parasite.jpg.jpeg", "/video/Parasite.mp4", false),
                createMovieCard("/img/joker.jpg.jpeg", "/videos/joker.mp4", false)
        );

        horreurBox.getChildren().addAll(
                createMovieCard("/img/get_out.jpg.jpeg", "/videos/getout.mp4", false)
        );

        trendingSeriesBox.getChildren().add(
                createMovieCard("/img/breaking_bad.jpg.jpeg", "/video/breakingBadTrailer.mp4", true)
        );

        chargerMaListeDynamique();
    }

    private void chargerMaListeDynamique() {
        myListBox.getChildren().clear();
        if (mesFavoris.isEmpty()) {
            Label msg = new Label("Votre liste est vide. Ajoutez des films !");
            msg.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 14px; -fx-padding: 20px;");
            myListBox.getChildren().add(msg);
        } else {
            for (String img : mesFavoris) {
                myListBox.getChildren().add(createMovieCard(img, "/videos/default.mp4", false));
            }
        }
    }

    private StackPane createMovieCard(String imagePath, String videoPath, boolean isSerie) {
        ImageView poster = new ImageView();
        poster.setFitHeight(200);
        poster.setFitWidth(135);

        try {
            poster.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            poster.setImage(defaultImage);
        }

        Label badge = new Label(isSerie ? "SÉRIE" : "FILM");
        String color = isSerie ? "#e50914" : "#333333";
        badge.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 10px;");

        StackPane card = new StackPane(poster, badge);
        card.setStyle("-fx-cursor: hand;");
        StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_LEFT);

        card.setOnMouseEntered(e -> { card.setScaleX(1.05); card.setScaleY(1.05); });
        card.setOnMouseExited(e -> { card.setScaleX(1.0); card.setScaleY(1.0); });

        card.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Details.fxml"));
                Parent root = loader.load();
                DetailsController controller = loader.getController();
                controller.initData(imagePath, videoPath, isSerie);
                Stage stage = (Stage) card.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return card;
    }

    @FXML
    private void openPlayer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Player.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML public void showAccueil(MouseEvent e) { heroTitleLabel.setText("Accueil"); }
    @FXML public void showSeries(MouseEvent e) { heroTitleLabel.setText("Séries"); }
    @FXML public void showFilms(MouseEvent e) { heroTitleLabel.setText("Films"); }
    @FXML public void showMaListe(MouseEvent e) { heroTitleLabel.setText("Ma Liste"); }
}