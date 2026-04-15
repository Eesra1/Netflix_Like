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

    @FXML private StackPane heroStackPane;
    @FXML private ImageView heroImageView;
    @FXML private Label heroTitleLabel;
    @FXML private TextField searchField;
    @FXML private HBox myListBox;
    @FXML private HBox actionMoviesBox;
    @FXML private HBox trendingSeriesBox;
    @FXML private VBox myListSection;
    @FXML private VBox moviesSection;
    @FXML private VBox seriesSection;

    // variables de la diaporama:
    private Timeline slideshow;
    private int currentIndex = 0;
    private List<Image> diaporamaImages = new ArrayList<>();
    private Image defaultImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // 1. Chargement de l'image par défaut (au cas où les autres ne chargent pas)
        try {
            defaultImage = new Image(getClass().getResourceAsStream("/img/image.jpg.jpeg"));
            heroImageView.setImage(defaultImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Chargement des images pour la bande d'annonce
        try {
            diaporamaImages.add(new Image(getClass().getResourceAsStream("/img/slide4.jpg.jpeg")));
            diaporamaImages.add(new Image(getClass().getResourceAsStream("/img/slide2.jpg.jpeg")));
            diaporamaImages.add(new Image(getClass().getResourceAsStream("/img/slide3.jpg.jpeg")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Configuration de l'animation
        slideshow = new Timeline(new KeyFrame(Duration.seconds(4.0), event -> {
            if (!diaporamaImages.isEmpty()) {
                currentIndex = (currentIndex + 1) % diaporamaImages.size();
                heroImageView.setImage(diaporamaImages.get(currentIndex));
            }
        }));
        slideshow.setCycleCount(Timeline.INDEFINITE);

        // 4. DÉMARRAGE AUTOMATIQUE DU DIAPORAMA
        if (!diaporamaImages.isEmpty()) {
            heroImageView.setImage(diaporamaImages.get(0)); // On affiche la première image
            slideshow.play(); // On lance l'animation en boucle pour toujours
        }

        // 5. Chargement des carrousels
        chargerCarrousel(myListBox);
        chargerCarrousel(actionMoviesBox);
        chargerCarrousel(trendingSeriesBox);
    }

    private void chargerCarrousel(HBox box) {
        for (int i = 1; i <= 8; i++) {
            try {
                ImageView poster = new ImageView();
                poster.setFitHeight(200.0);
                poster.setFitWidth(135.0);
                Image img = new Image(getClass().getResourceAsStream("/img/image.jpg.jpeg"));
                poster.setImage(img);

                boolean isSerie = (i % 2 == 0);
                Label badge = new Label(isSerie ? "SÉRIE" : "FILM");
                String badgeColor = isSerie ? "#e50914" : "#333333";
                badge.setStyle("-fx-background-color: " + badgeColor + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 10px; " +
                        "-fx-padding: 3 6 3 6; " +
                        "-fx-background-radius: 3;");

                StackPane stackPane = new StackPane();
                stackPane.getChildren().addAll(poster, badge);
                stackPane.setStyle("-fx-cursor: hand;");

                StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_LEFT);
                StackPane.setMargin(badge, new javafx.geometry.Insets(5, 0, 0, 5));

                stackPane.setOnMouseClicked(event -> {
                    // redirection vers la page de détails au clic sur une affiche
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Details.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) stackPane.getScene().getWindow();
                        stage.getScene().setRoot(root);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                box.getChildren().add(stackPane);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private void updateSectionsVisibility(boolean showList, boolean showMovies, boolean showSeries) {
        myListSection.setVisible(showList);
        myListSection.setManaged(showList);
        moviesSection.setVisible(showMovies);
        moviesSection.setManaged(showMovies);
        seriesSection.setVisible(showSeries);
        seriesSection.setManaged(showSeries);
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

    @FXML void showAccueil(MouseEvent event) {
        heroTitleLabel.setText("Accueil");
        updateSectionsVisibility(true, true, true);
    }

    @FXML void showSeries(MouseEvent event) {
        heroTitleLabel.setText("Toutes nos Séries");
        updateSectionsVisibility(false, false, true);
    }

    @FXML void showFilms(MouseEvent event) {
        heroTitleLabel.setText("Tous nos Films");
        updateSectionsVisibility(false, true, false);
    }

    @FXML void showMaListe(MouseEvent event) {
        heroTitleLabel.setText("Ma Liste de favoris");
        updateSectionsVisibility(true, false, false);
    }
}