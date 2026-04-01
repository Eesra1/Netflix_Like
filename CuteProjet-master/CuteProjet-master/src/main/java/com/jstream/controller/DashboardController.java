package com.jstream.controller;

import com.jstream.model.Film;
import com.jstream.model.Series;
import com.jstream.service.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import com.jstream.dao.FilmDAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.jstream.model.Season;
import com.jstream.service.AdminService;
import com.jstream.model.Episode;



import static com.jstream.service.AdminService.addFilm;


public class DashboardController implements Initializable {
    @FXML private TextField titleField;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> categoryComboBox; // Ou ComboBox<Category> selon votre implémentation
    @FXML private TextArea synopsisField;
    @FXML private TextField posterUrlField;
    @FXML private TextField videoUrlField;
    @FXML private Label statusLabel; // Pour afficher les messages de succès/erreur

    @FXML private ImageView heroImageView;
    @FXML private Label heroTitleLabel;


    @FXML private TextField searchField;
    @FXML private HBox myListBox;
    @FXML private HBox actionMoviesBox;
    @FXML private HBox trendingSeriesBox;

    // Champs pour FilmList
    @FXML private TableView<Film> filmTable;
    @FXML private TableColumn<Film, String> colFilmTitle;
    @FXML private TableColumn<Film, String> colFilmDate;
    @FXML private TableColumn<Film, String> colFilmCategory;
    @FXML private TableColumn<Film, Void> colFilmActions;

    // Champs pour SeriesList
    @FXML private TableView<Series> seriesTable;
    @FXML private TableColumn<Series, String> colSeriesTitle;
    @FXML private TableColumn<Series, String> colSeriesSynopsis;
    @FXML private TableColumn<Series, String> colSeriesCategory;
    @FXML private TableColumn<Series, Void> colSeriesActions;

    // Nouveaux champs pour les séries (basés sur AddSeries.fxml)

    // Champs pour les séries
    @FXML private TextField startYearField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField seasonsCountField;
    @FXML private TextField episodesTotalField;
    @FXML private TextField trailerUrlField;
    @FXML private TextField creatorField;
    @FXML private TextField castField;

    // Champs pour la saison (section "Saison 1")
    @FXML private TextField season1NumberField;
    @FXML private TextField season1TitleField;
    @FXML private TextField season1YearField;
    @FXML private TextField season1EpisodesField;
    @FXML private TextField episodeTitleField;
    @FXML private TextField episodeNumberField;
    @FXML private TextField episodeDurationField;
    @FXML private TextArea episodeDescriptionField;
    @FXML private TextField episodeVideoUrlField;
    @FXML private TextField episodeThumbnailUrlField;
    @FXML private ComboBox<String> seriesComboBox;
    @FXML private ComboBox<String> seasonComboBox;


    private List<Film> allDashboardFilms = new java.util.ArrayList<>();
    private List<Series> allDashboardSeries = new java.util.ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            if (heroImageView != null) {
                Image heroImg = new Image(getClass().getResourceAsStream("/img/image.jpg.jpeg"));
                heroImageView.setImage(heroImg);
            }
        } catch (Exception e) {
            System.out.println("Image principale introuvable");
        }

        if (myListBox != null || actionMoviesBox != null || trendingSeriesBox != null) {
            try {
                allDashboardFilms = new FilmDAO().findAll();
                allDashboardSeries = new com.jstream.dao.SeriesDAO().findAll();
            } catch (Exception e) {
                System.out.println("Erreur chargement BD Dashboard");
            }
            renderDashboardCarousels("");
        }

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                renderDashboardCarousels(newVal.trim().toLowerCase());
            });
        }

        // ==== Initialisation pour FilmList ====
        if (filmTable != null) {
            colFilmTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colFilmDate.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
            colFilmCategory.setCellValueFactory(new PropertyValueFactory<>("categoryId"));

            // Bouton Supprimer dans la colonne Action
            Callback<TableColumn<Film, Void>, TableCell<Film, Void>> cellFactory = new Callback<>() {
                @Override
                public TableCell<Film, Void> call(final TableColumn<Film, Void> param) {
                    return new TableCell<>() {
                        private final Button btn = new Button("Supprimer");
                        {
                            btn.setStyle("-fx-background-color: #e50914; -fx-text-fill: white; -fx-cursor: hand;");
                            btn.setOnAction((ActionEvent event) -> {
                                Film film = getTableView().getItems().get(getIndex());
                                try {
                                    AdminService adminService = new AdminService();
                                    adminService.deleteFilm(film.getId());
                                    loadFilms(); // Recharger la table
                                } catch (Exception e) {
                                    System.out.println("Erreur de suppression du film");
                                    e.printStackTrace();
                                }
                            });
                        }
                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        }
                    };
                }
            };
            colFilmActions.setCellFactory(cellFactory);
            loadFilms();
        }

        // ==== Initialisation pour SeriesList ====
        if (seriesTable != null) {
            colSeriesTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colSeriesSynopsis.setCellValueFactory(new PropertyValueFactory<>("synopsis"));
            colSeriesCategory.setCellValueFactory(new PropertyValueFactory<>("categoryId"));

            Callback<TableColumn<Series, Void>, TableCell<Series, Void>> cellFactorySeries = new Callback<>() {
                @Override
                public TableCell<Series, Void> call(final TableColumn<Series, Void> param) {
                    return new TableCell<>() {
                        private final Button btn = new Button("Supprimer");
                        {
                            btn.setStyle("-fx-background-color: #e50914; -fx-text-fill: white; -fx-cursor: hand;");
                            btn.setOnAction((ActionEvent event) -> {
                                Series series = getTableView().getItems().get(getIndex());
                                try {
                                    AdminService adminService = new AdminService();
                                    adminService.deleteSeries(series.getId());
                                    loadSeries(); // Recharger la table
                                } catch (Exception e) {
                                    System.out.println("Erreur de suppression de la série");
                                    e.printStackTrace();
                                }
                            });
                        }
                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        }
                    };
                }
            };
            colSeriesActions.setCellFactory(cellFactorySeries);
            loadSeries();
        }
    }

    private void renderDashboardCarousels(String query) {
        List<Film> filteredFilms = allDashboardFilms;
        List<Series> filteredSeries = allDashboardSeries;

        if (query != null && !query.isEmpty()) {
            filteredFilms = allDashboardFilms.stream()
                .filter(f -> f.getTitle() != null && f.getTitle().toLowerCase().contains(query))
                .collect(Collectors.toList());

            filteredSeries = allDashboardSeries.stream()
                .filter(s -> s.getTitle() != null && s.getTitle().toLowerCase().contains(query))
                .collect(Collectors.toList());
        }

        if (myListBox != null) {
            myListBox.getChildren().clear();
            for (Film f : filteredFilms) myListBox.getChildren().add(createCard(f.getTitle(), "FILM", f.getCoverUrl()));
            for (Series s : filteredSeries) myListBox.getChildren().add(createCard(s.getTitle(), "SÉRIE", s.getCoverUrl()));
        }

        if (actionMoviesBox != null) {
            actionMoviesBox.getChildren().clear();
            for (Film f : filteredFilms) {
                if (f.getCategoryId() == 1 || f.getCategoryId() == 2) { 
                    actionMoviesBox.getChildren().add(createCard(f.getTitle(), "FILM", f.getCoverUrl()));
                }
            }
        }

        if (trendingSeriesBox != null) {
            trendingSeriesBox.getChildren().clear();
            for (Series s : filteredSeries) {
                trendingSeriesBox.getChildren().add(createCard(s.getTitle(), "SÉRIE", s.getCoverUrl()));
            }
        }
    }

    private javafx.scene.layout.StackPane createCard(String title, String type, String coverUrl) {
        ImageView poster = new ImageView();
        poster.setFitHeight(200.0);
        poster.setFitWidth(135.0);

        try {
            if (coverUrl != null && !coverUrl.isEmpty()) {
                poster.setImage(new Image(coverUrl, true));
            } else {
                poster.setImage(new Image(getClass().getResourceAsStream("/img/image.jpg.jpeg")));
            }
        } catch (Exception e) {
            poster.setImage(new Image(getClass().getResourceAsStream("/img/image.jpg.jpeg")));
        }

        Label badge = new Label(type);
        String badgeColor = type.equals("SÉRIE") ? "#e50914" : "#333333";
        badge.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 6 3 6; -fx-background-radius: 3;");

        javafx.scene.layout.StackPane stackPane = new javafx.scene.layout.StackPane();
        stackPane.getChildren().addAll(poster, badge);
        stackPane.setStyle("-fx-cursor: hand;");
        javafx.scene.layout.StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_LEFT);
        javafx.scene.layout.StackPane.setMargin(badge, new javafx.geometry.Insets(5, 0, 0, 5));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.7); -fx-padding: 2;");
        titleLabel.setMaxWidth(135.0);
        stackPane.getChildren().add(titleLabel);
        javafx.scene.layout.StackPane.setAlignment(titleLabel, javafx.geometry.Pos.BOTTOM_CENTER);

        stackPane.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Details.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) stackPane.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return stackPane;
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        // 1. On vide la session utilisateur grâce à votre SessionManager !
        SessionManager.logout();
        System.out.println("Utilisateur déconnecté.");

        // 2. On le renvoie sur la page de Login
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du retour à la page de connexion.");
        }
    }
    @FXML
    private void openAddFilm(ActionEvent event) {
        try {
            System.out.println("Ouverture de la page d'ajout de film...");

            // On charge le fichier AddFilm.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddFilm.fxml"));
            Parent root = loader.load();

            // On récupère la fenêtre actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // On remplace la scène par la nouvelle
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println(" Erreur lors de l'ouverture de AddFilm.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToFilmList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FilmList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur: Impossible de charger FilmList.fxml");
            e.printStackTrace();
        }
    }

    private void loadFilms() {
        if (filmTable == null) return; // Si la table n'est pas dans la vue actuelle, on ne fait rien
        
        try {
            FilmDAO dao = new FilmDAO();
            ObservableList<Film> films = FXCollections.observableArrayList(dao.findAll());
            filmTable.setItems(films);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des films !");
            e.printStackTrace();
        }
    }

    private void loadSeries() {
        if (seriesTable == null) return;
        
        try {
            com.jstream.dao.SeriesDAO dao = new com.jstream.dao.SeriesDAO();
            ObservableList<Series> series = FXCollections.observableArrayList(dao.findAll());
            seriesTable.setItems(series);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des series !");
            e.printStackTrace();
        }
    }

    // 4. VOS MÉTHODES FXML
    @FXML
    private void handleAddFilm(ActionEvent event) {
        try {
            // 1. Récupération des données depuis les champs de l'interface
            String title = titleField.getText();
            String yearText = yearField.getText();
            String category = categoryComboBox.getValue();
            String synopsis = synopsisField.getText();
            String posterUrl = posterUrlField.getText();
            String videoUrl = videoUrlField.getText();
            String cast = castField.getText();

            // 2. Vérification des champs obligatoires (ex: Titre, Synopsis, Vidéo, etc.)
            if (title == null || title.trim().isEmpty() ||
                    synopsis == null || synopsis.trim().isEmpty() ||
                    videoUrl == null || videoUrl.trim().isEmpty()) {

                statusLabel.setText(" Veuillez remplir tous les champs obligatoires (*)");
                statusLabel.setStyle("-fx-text-fill: #ff4444;"); // Texte en rouge
                return;
            }

            // 3. Conversion des données numériques (Année)
            int releaseYear = 0;
            try {
                if (yearText != null && !yearText.isEmpty()) releaseYear = Integer.parseInt(yearText);
            } catch (NumberFormatException e) {
                statusLabel.setText(" L'année doit être un nombre valide.");
                statusLabel.setStyle("-fx-text-fill: #ff4444;");
                return;
            }

            // 4. Déterminer l'ID de la catégorie (À adapter selon comment votre base de données gère les catégories)
            int categoryId = 1; // Valeur par défaut ou logique pour retrouver l'ID depuis 'category'

            // 5. Création de l'objet Film
            Film nouveauFilm = new Film(
                    0,              // id : 0 car il sera généré par la base de données
                    title,          // title : le titre du film
                    synopsis,       // synopsis : le résumé
                    cast,           // cast : les acteurs
                    yearText,       // releaseDate : en String
                    videoUrl,       // videoUrl : le lien de la vidéo
                    posterUrl,      // coverUrl : on utilise l'URL du poster pour l'image de couverture
                    categoryId      // categoryId : l'ID de la catégorie
            );

            // 6. Appel à la vraie méthode d'ajout de votre DAO
            addFilm(nouveauFilm);

            // 7. Affichage du succès et réinitialisation
            System.out.println("Film ajouté dans la base de données : " + title);
            statusLabel.setText(" Film '" + title + "' ajouté avec succès !");
            statusLabel.setStyle("-fx-text-fill: #46d369;"); // Texte en vert

            // Optionnel : vider les champs après l'ajout
            clearForm();

        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout du film.");
            e.printStackTrace();
            statusLabel.setText(" Erreur serveur lors de l'ajout du film.");
            statusLabel.setStyle("-fx-text-fill: #ff4444;");
        }
    }
    // Méthode bonus pour vider le formulaire après succès
    private void clearForm() {
        if (titleField != null) titleField.clear();
        if (yearField != null) yearField.clear();
        if (synopsisField != null) synopsisField.clear();
        if (posterUrlField != null) posterUrlField.clear();
        if (videoUrlField != null) videoUrlField.clear();
        if (castField != null) castField.clear();
        if (categoryComboBox != null) categoryComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            System.out.println("Retour au Dashboard Admin...");

            // On charge le fichier Admin.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin.fxml"));
            Parent root = loader.load();

            // On récupère la fenêtre actuelle (Stage) à partir du bouton cliqué
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // On change la scène pour afficher l'Admin
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de la page Admin.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        System.out.println("Reset");
        clearForm(); 
        clearSeriesForm();
        clearEpisodeForm();
    }
 /*   @FXML
    private void handleAddSeries(ActionEvent event) {
        try {
            // 1. Création de l'instance Series
            Series s = new Series();

            // 2. Remplissage des champs selon votre classe Series.java
            s.setTitle(titleField.getText());
            s.setSynopsis(synopsisField.getText());
            s.setCast(castField.getText()); // Champ "Acteurs"
            s.setCoverUrl(posterUrlField.getText());


            // 3. Gestion du CategoryId (doit être un int)
            // Note: Ici on simule une logique simple, à adapter selon votre liste de catégories
            int categoryId = 1; // Valeur par défaut
            if (categoryComboBox.getSelectionModel().getSelectedIndex() != -1) {
                // Exemple : index + 1 si vos IDs en BDD commencent à 1
                categoryId = categoryComboBox.getSelectionModel().getSelectedIndex() + 1;
            }
            s.setCategoryId(categoryId);

            // 4. Appel au service
            AdminService adminService = new AdminService();
            adminService.addSeries(s);

            statusLabel.setText(" Série '" + s.getTitle() + "' ajoutée !");
            statusLabel.setStyle("-fx-text-fill: #46d369;");

        } catch (Exception e) {
            statusLabel.setText(" Erreur lors de l'ajout de la série.");
            e.printStackTrace();
        }
    }*/
    @FXML
    private void handleAddSeason(ActionEvent event) {
        try {
            Season season = new Season();
            season.setSeasonNumber(Integer.parseInt(season1NumberField.getText()));
            season.setSeriesId(1);

            AdminService adminService = new AdminService();
            adminService.addSeason(season);

            statusLabel.setText(" Saison ajoutée !");
            statusLabel.setStyle("-fx-text-fill: #46d369;");
        } catch (NumberFormatException e) {
            statusLabel.setText(" Le numéro doit être un chiffre.");
        } catch (Exception e) {
            statusLabel.setText(" Erreur.");
            e.printStackTrace();
        }
    }
    @FXML
    private void goToSeries(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SeriesList.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println(" Erreur : Impossible de charger SeriesList.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddSeries(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddSeries.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println(" Erreur : Impossible de charger AddSeries.fxml");
            e.printStackTrace();
        }
    }
    @FXML
    private void goToEpisodes(ActionEvent event) {
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddEpisodes.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur : Impossible de charger la page des épisodes");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAddEpisode(ActionEvent event) {
        try {
            // Sécurité contre les NPE
            if (episodeTitleField == null || episodeDescriptionField == null || statusLabel == null) {
                System.out.println("Champs non initialisés (mauvaise page)");
                return;
            }

            String title = episodeTitleField.getText();
            String summary = episodeDescriptionField.getText();
            String videoUrl = (episodeVideoUrlField != null) ? episodeVideoUrlField.getText() : "";
            String thumbnailUrl = (episodeThumbnailUrlField != null) ? episodeThumbnailUrlField.getText() : "";

            if (title == null || title.isEmpty() || episodeNumberField.getText().isEmpty() || episodeDurationField.getText().isEmpty()) {
                statusLabel.setText("⚠️ Remplir les champs obligatoires !");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            Episode episode = new Episode();
            episode.setTitle(title);
            episode.setSummary(summary);
            episode.setVideoUrl(videoUrl);
            episode.setThumbnailUrl(thumbnailUrl);

            // Récupération et conversion des données numériques
            episode.setEpisodeNumber(Integer.parseInt(episodeNumberField.getText()));
            episode.setDuration(Integer.parseInt(episodeDurationField.getText()));

            // Liaison avec une saison depuis la combobox
            int seasonId = 1;
            if (seasonComboBox != null && seasonComboBox.getSelectionModel().getSelectedIndex() != -1) {
                seasonId = seasonComboBox.getSelectionModel().getSelectedIndex() + 1;
            }
            episode.setSeasonId(seasonId);

            // Appel au service pour l'enregistrement
            AdminService adminService = new AdminService();
            adminService.addEpisode(episode);

            // Feedback utilisateur
            statusLabel.setText("✅ Épisode ajouté avec succès !");
            statusLabel.setStyle("-fx-text-fill: #46d369;");
            
            clearEpisodeForm();

        } catch (NumberFormatException e) {
            if (statusLabel != null) {
                statusLabel.setText("❌ Erreur : Le numéro et la durée doivent être des chiffres valides.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            if (statusLabel != null) {
                statusLabel.setText("❌ Erreur lors de l'ajout de l'épisode.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
            e.printStackTrace();
        }
    }

    private void clearEpisodeForm() {
        if (episodeTitleField != null) episodeTitleField.clear();
        if (episodeDescriptionField != null) episodeDescriptionField.clear();
        if (episodeNumberField != null) episodeNumberField.clear();
        if (episodeDurationField != null) episodeDurationField.clear();
        if (episodeVideoUrlField != null) episodeVideoUrlField.clear();
        if (episodeThumbnailUrlField != null) episodeThumbnailUrlField.clear();
        if (seasonComboBox != null) seasonComboBox.getSelectionModel().clearSelection();
        if (seriesComboBox != null) seriesComboBox.getSelectionModel().clearSelection();
    }
    @FXML
    private void handleAddSeries(ActionEvent event) {

        try {
            // 🔒 Vérification des champs (évite NullPointerException)
            if (titleField == null || synopsisField == null || statusLabel == null) {
                System.out.println("Champs non initialisés (mauvaise page)");
                return;
            }

            String title = titleField.getText();
            String synopsis = synopsisField.getText();
            String cast = (castField != null) ? castField.getText() : "";
            String coverUrl = (posterUrlField != null) ? posterUrlField.getText() : "";

            // ✅ Vérification des champs obligatoires
            if (title == null || title.isEmpty() ||
                    synopsis == null || synopsis.isEmpty()) {

                statusLabel.setText("⚠️ Remplir les champs obligatoires !");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // 🎯 Création objet Series
            Series s = new Series();
            s.setTitle(title);
            s.setSynopsis(synopsis);
            s.setCast(cast);
            s.setCoverUrl(coverUrl);

            // 🎯 Category
            int categoryId = 1;
            if (categoryComboBox != null &&
                    categoryComboBox.getSelectionModel().getSelectedIndex() != -1) {

                categoryId = categoryComboBox.getSelectionModel().getSelectedIndex() + 1;
            }
            s.setCategoryId(categoryId);

            // 💾 Enregistrement
            AdminService adminService = new AdminService();
            adminService.addSeries(s);

            // ✅ Succès
            statusLabel.setText("✅ Série ajoutée avec succès !");
            statusLabel.setStyle("-fx-text-fill: #46d369;");

            // 🧹 Reset formulaire
            clearSeriesForm();

        } catch (Exception e) {
            e.printStackTrace();

            if (statusLabel != null) {
                statusLabel.setText("❌ Erreur lors de l'ajout !");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }
    private void clearSeriesForm() {
        if (titleField != null) titleField.clear();
        if (synopsisField != null) synopsisField.clear();
        if (castField != null) castField.clear();
        if (posterUrlField != null) posterUrlField.clear();

        if (categoryComboBox != null) {
            categoryComboBox.getSelectionModel().clearSelection();
        }
    }


}