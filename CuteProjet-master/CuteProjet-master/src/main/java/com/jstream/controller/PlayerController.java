package com.jstream.controller;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
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
import java.util.prefs.Preferences; // NOUVEL IMPORT

public class PlayerController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private MediaView mediaView;
    @FXML private Button playPauseButton;
    @FXML private Slider progressSlider;
    @FXML private Slider volumeSlider;
    @FXML private Label timeLabel;
    @FXML private VBox bingeWatchingOverlay;
    @FXML private Label countdownLabel;
    @FXML private VBox controlsBox;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = true;
    private boolean isFullScreen = false;
    private Timeline bingeTimer;
    private int countdownSeconds = 10;
    private PauseTransition hideControlsTimer;

    // --- VARIABLES POUR LA SAUVEGARDE DE LA POSITION ---
    private String currentVideoPath;
    private Preferences prefs = Preferences.userNodeForPackage(PlayerController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // --- Redimensionnement de la vidéo ---
        mediaView.fitWidthProperty().bind(rootPane.widthProperty());
        mediaView.fitHeightProperty().bind(rootPane.heightProperty());

        // --- AUTO-HIDE DES CONTRÔLES ---
        if (controlsBox != null) {
            hideControlsTimer = new PauseTransition(Duration.seconds(3));
            hideControlsTimer.setOnFinished(event -> controlsBox.setVisible(false));

            rootPane.setOnMouseMoved(event -> {
                controlsBox.setVisible(true);
                hideControlsTimer.playFromStart();
            });
            rootPane.setOnMouseExited(event -> {
                controlsBox.setVisible(false);
                hideControlsTimer.stop();
            });
            hideControlsTimer.play();
        } else {
            System.out.println("⚠️ Attention : controlsBox n'est pas lié. Vérifiez l'ID dans Scene Builder.");
        }
    }

    /**
     * ✅ Méthode appelée par DetailsController pour injecter la vidéo sélectionnée
     */
    public void initVideo(String videoPath) {
        // --- NOUVEAU : On garde en mémoire le chemin de la vidéo ---
        this.currentVideoPath = videoPath;

        try {
            // Sécurité : rajouter le "/" s'il est manquant pour trouver dans le dossier resources
            if (!videoPath.startsWith("/") && !videoPath.startsWith("http")) {
                videoPath = "/" + videoPath;
            }

            String mediaUrlFinal;

            // Permet de lire soit un lien internet (http), soit un fichier local (ex: /videos/inception.mp4)
            if (videoPath.startsWith("http")) {
                mediaUrlFinal = videoPath;
            } else {
                URL resourceUrl = getClass().getResource(videoPath);
                if (resourceUrl == null) {
                    System.out.println("❌ Erreur : La vidéo " + videoPath + " est introuvable dans src/main/resources !");
                    return;
                }
                mediaUrlFinal = resourceUrl.toExternalForm();
            }

            // On lance la lecture avec le bon chemin
            Media media = new Media(mediaUrlFinal);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            // Événements liés à la vidéo
            mediaPlayer.setOnReady(() -> {
                progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());

                // --- NOUVEAU : Récupérer et appliquer la position sauvegardée ---
                double savedTime = prefs.getDouble(currentVideoPath, 0.0);
                if (savedTime > 0) {
                    mediaPlayer.seek(Duration.seconds(savedTime));
                    System.out.println("Reprise de la vidéo à : " + savedTime + " secondes.");
                }

                mediaPlayer.play();
            });

            mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressSlider.setValue(newValue.toSeconds());
                updateTime(newValue, mediaPlayer.getTotalDuration());
            });

            progressSlider.setOnMousePressed(e -> mediaPlayer.seek(Duration.seconds(progressSlider.getValue())));
            progressSlider.setOnMouseDragged(e -> mediaPlayer.seek(Duration.seconds(progressSlider.getValue())));

            // Gestion du Binge Watching à la fin de la vidéo
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

        } catch (Exception e) {
            e.printStackTrace();
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
        if (mediaPlayer == null) return; // ✅ Sécurité si la vidéo ne s'est pas chargée

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
            // --- NOUVEAU : Sauvegarder la position avant de fermer la vidéo ---
            if (currentVideoPath != null) {
                double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                double totalTime = mediaPlayer.getTotalDuration().toSeconds();

                // Si la vidéo est à moins de 10 secondes de la fin, on considère qu'elle est terminée
                if (totalTime - currentTime < 10) {
                    prefs.putDouble(currentVideoPath, 0.0);
                } else {
                    prefs.putDouble(currentVideoPath, currentTime);
                }
            }

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
        // Logique à ajouter plus tard
    }

    @FXML
    private void cancelBingeWatching(ActionEvent event) {
        if (bingeTimer != null) bingeTimer.stop();
        bingeWatchingOverlay.setVisible(false);
        goBack(event);
    }
}