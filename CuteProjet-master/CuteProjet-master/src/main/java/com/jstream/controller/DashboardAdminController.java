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
import java.util.stream.Collectors;
import com.jstream.model.Season;
import com.jstream.service.AdminService;
import com.jstream.model.Episode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

//import static com.jstream.service.AdminService.addFilm;


public class DashboardAdminController implements Initializable {
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

    // Categories
    @FXML private TextField categoryNameField;
    @FXML private TableView<com.jstream.model.Category> categoryTable;
    @FXML private TableColumn<com.jstream.model.Category, Integer> colCategoryId;
    @FXML private TableColumn<com.jstream.model.Category, String> colCategoryName;
    @FXML private TableColumn<com.jstream.model.Category, Void> colCategoryActions;

    private List<Film> allDashboardFilms = new java.util.ArrayList<>();
    private List<Series> allDashboardSeries = new java.util.ArrayList<>();
    
    // Modification state
    private static Film filmToEdit = null;
    private static Series seriesToEdit = null;
    private static com.jstream.model.Category categoryToEdit = null;
    
    // Data lists for lookups
    private List<com.jstream.model.Category> cachedCategories = new java.util.ArrayList<>();
    private List<Series> cachedSeries = new java.util.ArrayList<>();
    // ─── Admin dashboard ─────────────────────────────────────────────────
    @FXML private Label totalViewsLabel;
    @FXML private Label totalSubscribersLabel;
    @FXML private Label pendingLabel;
    @FXML private Label canceledLabel;
    @FXML private Label newSubscribersLabel;

    // Graphiques
    @FXML private HBox barChartBox;
    @FXML private StackPane lineChartBox;
    @FXML private ComboBox<String> barChartFilter;
    @FXML private ComboBox<String> lineChartFilter;

    // Tableau "top contenus"
    @FXML private TableView<TopContent> topContentTable;
    @FXML private TableColumn<TopContent, String> colTitle;
    @FXML private TableColumn<TopContent, String> colViews;
    @FXML private TableColumn<TopContent, String> colStatus;
    @FXML private TableColumn<TopContent, String> colGenre;
    //private List<Film> allDashboardFilms  = new ArrayList<>();
   // private List<Series> allDashboardSeries = new ArrayList<>();

    // Cache complet pour la recherche dans le tableau
    private ObservableList<TopContent> allTopContent = FXCollections.observableArrayList();

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

            // Boutons d'action
            Callback<TableColumn<Film, Void>, TableCell<Film, Void>> cellFactory = new Callback<>() {
                @Override
                public TableCell<Film, Void> call(final TableColumn<Film, Void> param) {
                    return new TableCell<>() {
                        private final Button btnDelete = new Button("Supprimer");
                        private final Button btnEdit = new Button("Modifier");
                        private final HBox pane = new HBox(8, btnEdit, btnDelete);
                        {
                            btnDelete.setStyle("-fx-background-color: #e50914; -fx-text-fill: white; -fx-cursor: hand;");
                            btnEdit.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-cursor: hand;");
                            pane.setAlignment(javafx.geometry.Pos.CENTER);
                            
                            btnDelete.setOnAction((ActionEvent event) -> {
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
                            btnEdit.setOnAction((ActionEvent event) -> {
                                Film film = getTableView().getItems().get(getIndex());
                                openEditFilm(event, film);
                            });
                        }
                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(pane);
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
                        private final Button btnDelete = new Button("Supprimer");
                        private final Button btnEdit = new Button("Modifier");
                        private final HBox pane = new HBox(8, btnEdit, btnDelete);
                        {
                            btnDelete.setStyle("-fx-background-color: #e50914; -fx-text-fill: white; -fx-cursor: hand;");
                            btnEdit.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-cursor: hand;");
                            pane.setAlignment(javafx.geometry.Pos.CENTER);
                            
                            btnDelete.setOnAction((ActionEvent event) -> {
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
                            btnEdit.setOnAction((ActionEvent event) -> {
                                Series series = getTableView().getItems().get(getIndex());
                                openEditSeries(event, series);
                            });
                        }
                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(pane);
                            }
                        }
                    };
                }
            };
            colSeriesActions.setCellFactory(cellFactorySeries);
            loadSeries();
        }
        
        // ==== Initialisation pour CategoryList ====
        if (categoryTable != null) {
            colCategoryId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colCategoryName.setCellValueFactory(new PropertyValueFactory<>("name"));
            
            Callback<TableColumn<com.jstream.model.Category, Void>, TableCell<com.jstream.model.Category, Void>> cellFactoryCategory = new Callback<>() {
                @Override
                public TableCell<com.jstream.model.Category, Void> call(final TableColumn<com.jstream.model.Category, Void> param) {
                    return new TableCell<>() {
                        private final Button btnDelete = new Button("Supprimer");
                        private final Button btnEdit = new Button("Modifier");
                        private final HBox pane = new HBox(8, btnEdit, btnDelete);
                        {
                            btnDelete.setStyle("-fx-background-color: #e50914; -fx-text-fill: white; -fx-cursor: hand;");
                            btnEdit.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-cursor: hand;");
                            pane.setAlignment(javafx.geometry.Pos.CENTER);
                            
                            btnDelete.setOnAction((ActionEvent event) -> {
                                com.jstream.model.Category category = getTableView().getItems().get(getIndex());
                                try {
                                    AdminService adminService = new AdminService();
                                    adminService.deleteCategory(category.getId());
                                       loadCategories();
                                } catch (Exception e) {
                                    System.out.println("Erreur de suppression de la catégorie");
                                    e.printStackTrace();
                                }
                            });
                            btnEdit.setOnAction((ActionEvent event) -> {
                                categoryToEdit = getTableView().getItems().get(getIndex());
                                if(categoryNameField != null) {
                                    categoryNameField.setText(categoryToEdit.getName());
                                }
                            });
                        }
                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(pane);
                            }
                        }
                    };
                }
            };
            colCategoryActions.setCellFactory(cellFactoryCategory);
            loadCategories();
        }

        // ==== Chargement dynamique des formulaires ====
        try {
            AdminService adminService = new AdminService();
            cachedCategories = adminService.getAllCategories();
            
            if (categoryComboBox != null) {
                ObservableList<String> catOptions = FXCollections.observableArrayList();
                for(com.jstream.model.Category c : cachedCategories) catOptions.add(c.getName());
                categoryComboBox.setItems(catOptions);
                
                // Préremplissage
                if (filmToEdit != null && titleField != null && videoUrlField != null) {
                    titleField.setText(filmToEdit.getTitle());
                    yearField.setText(filmToEdit.getReleaseDate());
                    synopsisField.setText(filmToEdit.getSynopsis());
                    posterUrlField.setText(filmToEdit.getCoverUrl());
                    videoUrlField.setText(filmToEdit.getVideoUrl());
                    castField.setText(filmToEdit.getCast());
                    for (com.jstream.model.Category c : cachedCategories) {
                        if (c.getId() == filmToEdit.getCategoryId()) {
                            categoryComboBox.setValue(c.getName()); break;
                        }
                    }
                } else if (seriesToEdit != null && titleField != null && synopsisField != null && videoUrlField == null) {
                    titleField.setText(seriesToEdit.getTitle());
                    synopsisField.setText(seriesToEdit.getSynopsis());
                    castField.setText(seriesToEdit.getCast());
                    if(posterUrlField != null) posterUrlField.setText(seriesToEdit.getCoverUrl());
                    for (com.jstream.model.Category c : cachedCategories) {
                        if (c.getId() == seriesToEdit.getCategoryId()) {
                            categoryComboBox.setValue(c.getName()); break;
                        }
                    }
                }
            }
            
            if (seriesComboBox != null) {
                cachedSeries = adminService.getAllSeries();
                ObservableList<String> seriesOptions = FXCollections.observableArrayList();
                for(Series s : cachedSeries) seriesOptions.add(s.getTitle());
                seriesComboBox.setItems(seriesOptions);
                
                // Listener pour charger les saisons de la série sélectionnée
                seriesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if(newVal != null && seasonComboBox != null) {
                        for(Series s : cachedSeries) {
                            if(s.getTitle().equals(newVal)) {
                                try {
                                    List<Season> seasons = adminService.getSeasonsBySeries(s.getId());
                                    ObservableList<String> seasonOptions = FXCollections.observableArrayList();
                                    for(Season se : seasons) seasonOptions.add("Saison " + se.getSeasonNumber());
                                    seasonComboBox.setItems(seasonOptions);
                                } catch(Exception e){}
                                break;
                            }
                        }
                    }
                });
            }
            
        } catch(Exception e) { e.printStackTrace(); }
        // ==== Admin Dashboard widgets ====
        if (totalViewsLabel != null) {
            initAdminDashboard();

            // Recherche en temps réel : filtre topContentTable
            if (searchField != null && topContentTable != null) {
                searchField.textProperty().addListener((obs, oldVal, newVal) ->
                        filterTopContent(newVal.trim().toLowerCase()));
            }
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
        filmToEdit = null;
        try {
            System.out.println("Ouverture de la page d'ajout de film...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddFilm.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println(" Erreur lors de l'ouverture de AddFilm.fxml");
            e.printStackTrace();
        }
    }

    private void openEditFilm(ActionEvent event, Film film) {
        filmToEdit = film;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddFilm.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
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

    private void loadCategories() {
        if (categoryTable == null) return;
        
        try {
            AdminService adminService = new AdminService();
            ObservableList<com.jstream.model.Category> categories = FXCollections.observableArrayList(adminService.getAllCategories());
            categoryTable.setItems(categories);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des categories !");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveCategory(ActionEvent event) {
        try {
            if (categoryNameField == null || statusLabel == null) {
                System.out.println("Champs non initialisés (mauvaise page)");
                return;
            }

            String name = categoryNameField.getText();
            if (name == null || name.trim().isEmpty()) {
                statusLabel.setText("⚠️ Veuillez saisir un nom de catégorie !");
                statusLabel.setStyle("-fx-text-fill: #ff4444;");
                return;
            }

            AdminService adminService = new AdminService();

            if (categoryToEdit == null) {
                // Ajout d'une nouvelle catégorie
                com.jstream.model.Category c = new com.jstream.model.Category();
                c.setName(name.trim());
                adminService.addCategory(c);

                statusLabel.setText("✅ Catégorie '" + name + "' ajoutée avec succès !");
                statusLabel.setStyle("-fx-text-fill: #46d369;");
            } else {
                // Modification d'une catégorie existante
                categoryToEdit.setName(name.trim());
                adminService.updateCategory(categoryToEdit);

                statusLabel.setText("✅ Catégorie modifiée avec succès !");
                statusLabel.setStyle("-fx-text-fill: #46d369;");
                categoryToEdit = null;
            }

            categoryNameField.clear();
            loadCategories();

        } catch (Exception e) {
            if (statusLabel != null) {
                statusLabel.setText("❌ Erreur lors de l'enregistrement de la catégorie.");
                statusLabel.setStyle("-fx-text-fill: #ff4444;");
            }
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

            // 4. Déterminer l'ID de la catégorie
            int categoryId = 1; 
            for(com.jstream.model.Category c : cachedCategories) {
                if(c.getName().equals(category)) {
                    categoryId = c.getId(); break;
                }
            }

            if (filmToEdit == null) {
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
                AdminService adminService = new AdminService();
                adminService.addFilm(nouveauFilm);

                // 7. Affichage du succès et réinitialisation
                System.out.println("Film ajouté dans la base de données : " + title);
                statusLabel.setText(" Film '" + title + "' ajouté avec succès !");
                statusLabel.setStyle("-fx-text-fill: #46d369;"); // Texte en vert
                clearForm();
            } else {
                filmToEdit.setTitle(title);
                filmToEdit.setReleaseDate(yearText);
                filmToEdit.setSynopsis(synopsis);
                filmToEdit.setVideoUrl(videoUrl);
                filmToEdit.setCoverUrl(posterUrl);
                filmToEdit.setCast(cast);
                filmToEdit.setCategoryId(categoryId);
                
                AdminService adminService = new AdminService();
                adminService.updateFilm(filmToEdit);
                
                System.out.println("Film modifié : " + title);
                statusLabel.setText(" Film '" + title + "' modifié avec succès !");
                statusLabel.setStyle("-fx-text-fill: #46d369;");
                filmToEdit = null; 
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de l'opération sur le film.");
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
        seriesToEdit = null;
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
    
    private void openEditSeries(ActionEvent event, Series series) {
        seriesToEdit = series;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddSeries.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
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


            episode.setEpisodeNumber(Integer.parseInt(episodeNumberField.getText()));
            episode.setDuration(Integer.parseInt(episodeDurationField.getText()));


            int seasonId = 1;
            if (seasonComboBox != null && seasonComboBox.getSelectionModel().getSelectedIndex() != -1) {
                seasonId = seasonComboBox.getSelectionModel().getSelectedIndex() + 1;
            }
            episode.setSeasonId(seasonId);


            AdminService adminService = new AdminService();
            adminService.addEpisode(episode);


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
            if (categoryComboBox != null && categoryComboBox.getValue() != null) {
                for(com.jstream.model.Category c : cachedCategories) {
                    if(c.getName().equals(categoryComboBox.getValue())) {
                        categoryId = c.getId(); break;
                    }
                }
            }

            AdminService adminService = new AdminService();
            if (seriesToEdit == null) {
                // 💾 Enregistrement
                adminService.addSeries(s);

                // ✅ Succès
                statusLabel.setText("✅ Série ajoutée avec succès !");
                statusLabel.setStyle("-fx-text-fill: #46d369;");
                clearSeriesForm();
            } else {
                seriesToEdit.setTitle(title);
                seriesToEdit.setSynopsis(synopsis);
                seriesToEdit.setCast(cast);
                seriesToEdit.setCoverUrl(coverUrl);
                seriesToEdit.setCategoryId(categoryId);
                
                adminService.updateSeries(seriesToEdit);
                
                statusLabel.setText("✅ Série modifiée avec succès !");
                statusLabel.setStyle("-fx-text-fill: #46d369;");
                seriesToEdit = null;
            }

        } catch (Exception e) {
            e.printStackTrace();

            if (statusLabel != null) {
                statusLabel.setText("❌ Erreur lors de l'opération !");
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
    @FXML
    private void goToSubscribers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Subscribers.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur : Impossible de charger Subscribers.fxml");
            e.printStackTrace();
        }
    }
    @FXML
    private void goToCategories(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CategoryList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur: Impossible de charger CategoryList.fxml");
            e.printStackTrace();
        }
    }
    /** Initialise tous les widgets du dashboard admin */
    private void initAdminDashboard() {

        // ── Filtres ComboBox ────────────────────────────────────────
        if (barChartFilter != null) {
            barChartFilter.setItems(FXCollections.observableArrayList(
                    "12 derniers mois", "6 derniers mois", "3 derniers mois", "Ce mois"));
            barChartFilter.getSelectionModel().selectFirst();
            barChartFilter.setOnAction(e -> refreshBarChart());
        }
        if (lineChartFilter != null) {
            lineChartFilter.setItems(FXCollections.observableArrayList(
                    "4 derniers mois", "6 derniers mois", "Année complète"));
            lineChartFilter.getSelectionModel().selectFirst();
            lineChartFilter.setOnAction(e -> refreshLineChart());
        }

        // ── Tableau top contenus ───────────────────────────────────
        if (topContentTable != null) {
            colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colViews.setCellValueFactory(new PropertyValueFactory<>("views"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

            loadTopContent();
        }

        // ── Métriques ──────────────────────────────────────────────
        refreshMetrics();

        // ── Graphiques ─────────────────────────────────────────────
        refreshBarChart();
        refreshLineChart();
    }

    /** Charge et affiche les métriques depuis la base de données */
    private void refreshMetrics() {
        try {
            FilmDAO filmDAO = new FilmDAO();
            com.jstream.dao.SeriesDAO seriesDAO = new com.jstream.dao.SeriesDAO();
            com.jstream.dao.UserDAO userDAO = new com.jstream.dao.UserDAO();

            int filmCount = filmDAO.findAll().size();
            int seriesCount = seriesDAO.findAll().size();
            int userCount = userDAO.findAll().size();

            if (totalViewsLabel != null)
                totalViewsLabel.setText(String.valueOf(filmCount + seriesCount) + " contenus");
            if (totalSubscribersLabel != null)
                totalSubscribersLabel.setText(String.valueOf(userCount));
            if (newSubscribersLabel != null)
                newSubscribersLabel.setText(String.valueOf(userCount));
            if (pendingLabel != null)
                pendingLabel.setText(String.valueOf(filmCount));
            if (canceledLabel != null)
                canceledLabel.setText(String.valueOf(seriesCount));

        } catch (Exception e) {
            System.out.println("Erreur chargement métriques : " + e.getMessage());
        }
    }

    /** Dessine le graphique en barres dans barChartBox */
    private void refreshBarChart() {
        if (barChartBox == null) return;
        barChartBox.getChildren().clear();

        String filter = (barChartFilter != null && barChartFilter.getValue() != null)
                ? barChartFilter.getValue()
                : "12 derniers mois";

        int[] values;
        String[] labels;

        if (filter.startsWith("3")) {
            values = new int[]{420, 580, 510};
            labels = new String[]{"Fév", "Mar", "Avr"};
        } else if (filter.startsWith("6")) {
            values = new int[]{300, 450, 390, 510, 620, 580};
            labels = new String[]{"Nov", "Déc", "Jan", "Fév", "Mar", "Avr"};
        } else if (filter.equals("Ce mois")) {
            values = new int[]{80, 120, 95, 140};
            labels = new String[]{"S1", "S2", "S3", "S4"};
        } else {
            values = new int[]{180, 240, 200, 310, 280, 350, 420, 390, 460, 510, 480, 580};
            labels = new String[]{"Mai","Jun","Jul","Aoû","Sep","Oct","Nov","Déc","Jan","Fév","Mar","Avr"};
        }

        int max = 0;
        for (int v : values) if (v > max) max = v;

        double chartHeight = 140.0;

        for (int i = 0; i < values.length; i++) {
            double barH = (max == 0) ? 0 : (values[i] / (double) max) * chartHeight;

            VBox col = new VBox(4);
            col.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER);
            col.setPrefHeight(chartHeight + 20);

            Rectangle bar = new Rectangle(
                    (barChartBox.getPrefWidth() > 0
                            ? (barChartBox.getPrefWidth() - 20) / values.length - 8
                            : 18),
                    barH);
            bar.setFill(Color.web("#e50914"));
            bar.setArcWidth(4);
            bar.setArcHeight(4);

            javafx.scene.control.Tooltip tip = new javafx.scene.control.Tooltip(
                    labels[i] + " : " + values[i] + "k vues");
            javafx.scene.control.Tooltip.install(bar, tip);

            Label lbl = new Label(labels[i]);
            lbl.setStyle("-fx-text-fill: #555555; -fx-font-size: 9px;");

            col.getChildren().addAll(bar, lbl);
            barChartBox.getChildren().add(col);
        }
    }

    /** Dessine le graphique en courbe dans lineChartBox */
    private void refreshLineChart() {
        if (lineChartBox == null) return;
        lineChartBox.getChildren().clear();

        String filter = (lineChartFilter != null && lineChartFilter.getValue() != null)
                ? lineChartFilter.getValue()
                : "4 derniers mois";

        int[] values;
        String[] labels;

        if (filter.startsWith("6")) {
            values = new int[]{1200, 1500, 1350, 1800, 2100, 1950};
            labels = new String[]{"Nov", "Déc", "Jan", "Fév", "Mar", "Avr"};
        } else if (filter.equals("Année complète")) {
            values = new int[]{800, 900, 850, 1100, 1200, 1050, 1300, 1500, 1400, 1700, 1900, 1800};
            labels = new String[]{"M","J","J","A","S","O","N","D","J","F","M","A"};
        } else {
            values = new int[]{1800, 2100, 1950, 2400};
            labels = new String[]{"Janv", "Févr", "Mars", "Avr"};
        }

        HBox lineBox = new HBox(6);
        lineBox.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER);
        lineBox.setPrefHeight(140.0);
        lineBox.setPrefWidth(lineChartBox.getPrefWidth() > 0 ? lineChartBox.getPrefWidth() : 200);

        int max = 0;
        for (int v : values) if (v > max) max = v;

        for (int i = 0; i < values.length; i++) {
            double barH = (max == 0) ? 0 : (values[i] / (double) max) * 120.0;

            VBox col = new VBox(4);
            col.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER);
            col.setPrefHeight(140);

            Rectangle bar = new Rectangle(20, barH);
            bar.setFill(Color.web(i == values.length - 1 ? "#e50914" : "#3a3a3a"));
            bar.setArcWidth(4);
            bar.setArcHeight(4);

            javafx.scene.control.Tooltip tip = new javafx.scene.control.Tooltip(
                    labels[i] + " : +" + values[i] + " abonnés");
            javafx.scene.control.Tooltip.install(bar, tip);

            Label lbl = new Label(labels[i]);
            lbl.setStyle("-fx-text-fill: #555555; -fx-font-size: 9px;");

            col.getChildren().addAll(bar, lbl);
            lineBox.getChildren().add(col);
        }
        lineChartBox.getChildren().add(lineBox);
    }

    /** Charge les contenus les plus regardés dans le TableView */
    private void loadTopContent() {
        if (topContentTable == null) return;
        try {
            FilmDAO filmDAO = new FilmDAO();
            com.jstream.dao.SeriesDAO seriesDAO = new com.jstream.dao.SeriesDAO();

            allTopContent.clear();

            for (Film f : filmDAO.findAll())
                allTopContent.add(new TopContent(
                        f.getTitle(),
                        String.valueOf((int)(Math.random() * 900 + 100)) + "k",
                        "Actif",
                        "Film"
                ));

            for (Series s : seriesDAO.findAll())
                allTopContent.add(new TopContent(
                        s.getTitle(),
                        String.valueOf((int)(Math.random() * 700 + 50)) + "k",
                        "Actif",
                        "Série"
                ));

            topContentTable.setItems(allTopContent);
        } catch (Exception e) {
            System.out.println("Erreur loadTopContent : " + e.getMessage());
        }
    }

    /** Filtre topContentTable selon la saisie dans searchField */
    private void filterTopContent(String query) {
        if (topContentTable == null) return;

        if (query == null || query.isEmpty()) {
            topContentTable.setItems(allTopContent);
            return;
        }

        ObservableList<TopContent> filtered = FXCollections.observableArrayList();
        for (TopContent tc : allTopContent) {
            if ((tc.getTitle() != null && tc.getTitle().toLowerCase().contains(query))
                    || (tc.getGenre() != null && tc.getGenre().toLowerCase().contains(query))
                    || (tc.getStatus() != null && tc.getStatus().toLowerCase().contains(query))) {
                filtered.add(tc);
            }
        }
        topContentTable.setItems(filtered);
    }

    /** Bouton "↻ Actualiser" : recharge métriques + graphiques + tableau */
    @FXML
    private void handleRefreshDashboard(ActionEvent event) {
        refreshMetrics();
        refreshBarChart();
        refreshLineChart();
        //loadTopContent();

        if (searchField != null && !searchField.getText().isEmpty())
            filterTopContent(searchField.getText().trim().toLowerCase());
    }

    @FXML
    private void onBarChartFilterChanged(ActionEvent event) {
        refreshBarChart();
    }

    @FXML
    private void onLineChartFilterChanged(ActionEvent event) {
        refreshLineChart();
    }

    /** Classe modèle pour TableView "Top Contenus" */
    public static class TopContent {
        private final String title;
        private final String views;
        private final String status;
        private final String genre;

        public TopContent(String title, String views, String status, String genre) {
            this.title = title;
            this.views = views;
            this.status = status;
            this.genre = genre;
        }

        public String getTitle() { return title; }
        public String getViews() { return views; }
        public String getStatus() { return status; }
        public String getGenre() { return genre; }
    }





}