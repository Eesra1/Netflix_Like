package com.jstream.controller;

import com.jstream.dao.CommentDAO;
import com.jstream.model.Comment;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CommentsAdminController implements Initializable {

    // ── FXML ─────────────────────────────────────────────────────────────────
    @FXML private TextField   searchField;
    @FXML private Label       totalCountLabel;
    @FXML private Label       flaggedCountLabel;

    @FXML private TableView<Comment>            commentsTable;
    @FXML private TableColumn<Comment, String>  colId;
    @FXML private TableColumn<Comment, String>  colUser;
    @FXML private TableColumn<Comment, String>  colType;
    @FXML private TableColumn<Comment, String>  colContentId;
    @FXML private TableColumn<Comment, String>  colContent;
    @FXML private TableColumn<Comment, String>  colDate;
    @FXML private TableColumn<Comment, String>  colFlagged;
    @FXML private TableColumn<Comment, Void>    colActions;

    private final CommentDAO commentDAO = new CommentDAO();
    private ObservableList<Comment> allComments = FXCollections.observableArrayList();
    private FilteredList<Comment>   filtered;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Initialisation ────────────────────────────────────────────────────────
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        loadData();
        if (searchField != null)
            searchField.textProperty().addListener((obs, o, n) -> applyFilter(n));
    }

    // ── Colonnes ──────────────────────────────────────────────────────────────
    private void setupColumns() {
        colId      .setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colUser    .setCellValueFactory(d -> new SimpleStringProperty("User #" + d.getValue().getUserId()));
        colType    .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getContentType()));
        colContentId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getContentId())));
        colContent .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getContent()));
        colDate    .setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCreatedAt() != null ? d.getValue().getCreatedAt().format(FMT) : "—"));
        colFlagged .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isFlagged() ? "⚠️ Signalé" : ""));

        // Coloration du statut signalé
        colFlagged.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold;");
            }
        });

        // Troncature du contenu long
        colContent.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item.length() > 60 ? item.substring(0, 60) + "…" : item);
                setTooltip(new Tooltip(item));
            }
        });

        // Boutons d'action
        colActions.setCellFactory(buildActionFactory());
    }

    private Callback<TableColumn<Comment, Void>, TableCell<Comment, Void>> buildActionFactory() {
        return param -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑 Supprimer");
            private final Button btnFlag   = new Button("⚠ Signaler");
            private final HBox   pane      = new HBox(6, btnFlag, btnDelete);

            {
                btnDelete.setStyle("-fx-background-color: #e50914; -fx-text-fill: white; " +
                        "-fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 11px;");
                btnFlag.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; " +
                        "-fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 11px;");
                pane.setAlignment(javafx.geometry.Pos.CENTER);

                btnDelete.setOnAction(e -> {
                    Comment c = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Supprimer ce commentaire ?", ButtonType.YES, ButtonType.NO);
                    confirm.setHeaderText("Confirmation");
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) {
                            try {
                                commentDAO.delete(c.getId());
                                loadData();
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }
                    });
                });

                btnFlag.setOnAction(e -> {
                    Comment c = getTableView().getItems().get(getIndex());
                    try {
                        commentDAO.setFlagged(c.getId(), !c.isFlagged());
                        loadData();
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    // ── Chargement des données ────────────────────────────────────────────────
    private void loadData() {
        try {
            allComments.setAll(commentDAO.findAll());
            filtered = new FilteredList<>(allComments, c -> true);
            commentsTable.setItems(filtered);
            updateCountLabels();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void applyFilter(String query) {
        if (filtered == null) return;
        String q = query == null ? "" : query.trim().toLowerCase();
        filtered.setPredicate(c -> {
            if (q.isEmpty()) return true;
            return (c.getContent()     != null && c.getContent().toLowerCase().contains(q))
                    || (c.getContentType() != null && c.getContentType().toLowerCase().contains(q));
        });
        updateCountLabels();
    }

    private void updateCountLabels() {
        if (totalCountLabel   != null) totalCountLabel.setText(String.valueOf(allComments.size()));
        if (flaggedCountLabel != null) flaggedCountLabel.setText(
                String.valueOf(allComments.stream().filter(Comment::isFlagged).count()));
    }

    // ── Navigation ───────────────────────────────────────────────────────────
    @FXML private void goBack(ActionEvent event)       { navigate("/fxml/Admin.fxml", event); }
    @FXML private void goToFilmList(ActionEvent event) { navigate("/fxml/FilmList.fxml", event); }
    @FXML private void goToSeries(ActionEvent event)   { navigate("/fxml/SeriesList.fxml", event); }
    @FXML private void goToSubscribers(ActionEvent event) { navigate("/fxml/Subscribers.fxml", event); }
    @FXML private void handleLogout(ActionEvent event) {
        com.jstream.service.SessionManager.logout();
        navigate("/fxml/Login.fxml", event);
    }

    private void navigate(String fxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}