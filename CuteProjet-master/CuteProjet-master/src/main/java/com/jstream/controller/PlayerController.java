package com.jstream.controller;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition; // N'oubliez pas cet import !
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private MediaView mediaView;
    @FXML private Button playPauseButton;
    @FXML private Slider progressSlider;
    @FXML private Slider volumeSlider;
    @FXML private Label timeLabel;
    @FXML private VBox bingeWatchingOverlay;
    @FXML private Label countdownLabel;

    // NOUVEAU : On déclare la boîte qui contient les contrôles en bas
    @FXML private VBox controlsBox;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = true;
    private boolean isFullScreen = false;
    private Timeline bingeTimer;
    private int countdownSeconds = 10;

    // NOUVEAU : Le chronomètre pour cacher la barre
    private PauseTransition hideControlsTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // --- 1. INITIALISATION DE LA VIDÉO ---
        String videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        Media media = new Media(videoUrl);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.setOnReady(() -> {
            progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            mediaPlayer.play();
        });

        mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            progressSlider.setValue(newValue.toSeconds());
            updateTime(newValue, mediaPlayer.getTotalDuration());
        });

        progressSlider.setOnMousePressed(e -> mediaPlayer.seek(Duration.seconds(progressSlider.getValue())));
        progressSlider.setOnMouseDragged(e -> mediaPlayer.seek(Duration.seconds(progressSlider.getValue())));

        // --- 2. GESTION DU BINGE WATCHING (Fin de vidéo) ---
        mediaPlayer.setOnEndOfMedia(() -> {
            bingeWatchingOverlay.setVisible(true);
            countdownSeconds = 10;
            countdownLabel.setText(String.valueOf(countdownSeconds));
            bingeTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                countdownSeconds--;
                countdownLabel.setText(String.valueOf(countdownSeconds));

                if (countdownSeconds <= 0) {
                    bingeTimer.stop();
                    playNextEpisode(null);
                }
            }));
            bingeTimer.setCycleCount(Timeline.INDEFINITE);
            bingeTimer.play();
        });

        // Redimensionnement de la vidéo
        mediaView.fitWidthProperty().bind(rootPane.widthProperty());
        mediaView.fitHeightProperty().bind(rootPane.heightProperty());

        // --- 3. AUTO-HIDE DES CONTRÔLES (La magie opère ici) ---
        if (controlsBox != null) {
            // Création du chrono de 3 secondes
            hideControlsTimer = new PauseTransition(Duration.seconds(3));
            // Que faire à la fin des 3 secondes ? Cacher la barre
            hideControlsTimer.setOnFinished(event -> controlsBox.setVisible(false));
            // Si la souris bouge sur l'écran
            rootPane.setOnMouseMoved(event -> {
                controlsBox.setVisible(true); // Afficher la barre
                hideControlsTimer.playFromStart(); // Remettre le chrono à zéro
            });
            // Si la souris sort complètement de la fenêtre
            rootPane.setOnMouseExited(event -> {
                controlsBox.setVisible(false);
                hideControlsTimer.stop();
            });
            // On lance le chrono au démarrage
            hideControlsTimer.play();
        } else {
            System.out.println("⚠️ Attention : controlsBox n'est pas lié. Vérifiez l'ID dans Scene Builder.");
        }
    }

    private void updateTime(Duration current, Duration total) {
        int currSeconds = (int) current.toSeconds();
        int totalSeconds = (int) total.toSeconds();
        timeLabel.setText(String.format("%02d:%02d / %02d:%02d",
                currSeconds / 60, currSeconds % 60,
                totalSeconds / 60, totalSeconds % 60));
    }

    @FXML
    private void togglePlayPause(ActionEvent event) {
        if (isPlaying) {
            mediaPlayer.pause();
            playPauseButton.setText("▶");
        } else {
            mediaPlayer.play();
            playPauseButton.setText("⏸");
        }
        isPlaying = !isPlaying;
    }

    @FXML
    private void toggleFullScreen(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        isFullScreen = !isFullScreen;
        stage.setFullScreen(isFullScreen);
    }

    @FXML
    private void goBack(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Details.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setFullScreen(false); // Quitter le plein écran proprement
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void playNextEpisode(ActionEvent event) {
        if (bingeTimer != null) bingeTimer.stop();
        bingeWatchingOverlay.setVisible(false);
        System.out.println("Lancement de l'épisode suivant !");
        // Ici, vous ajouterez plus tard la logique pour charger la vidéo suivante
    }

    @FXML
    private void cancelBingeWatching(ActionEvent event) {
        if (bingeTimer != null) bingeTimer.stop();
        bingeWatchingOverlay.setVisible(false);
        goBack(event);
    }
}