package com.jstream.controller;

import com.jstream.dao.UserDAO;
import com.jstream.model.User;
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
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class SubscriberController implements Initializable {

    // ── Header / Search ──────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private Label totalCountLabel;
    @FXML private Label activeCountLabel;
    @FXML private Label expiredCountLabel;

    // ── Table ────────────────────────────────────────────────────────────────
    @FXML private TableView<User>              subscriberTable;
    @FXML private TableColumn<User, String>    colName;
    @FXML private TableColumn<User, String>    colEmail;
    @FXML private TableColumn<User, String>    colRole;
    @FXML private TableColumn<User, String>    colStart;
    @FXML private TableColumn<User, String>    colEnd;
    @FXML private TableColumn<User, String>    colStatus;
    @FXML private TableColumn<User, Void>      colActions;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ObservableList<User> allUsers = FXCollections.observableArrayList();
    private FilteredList<User>   filtered;
    private final UserDAO userDAO = new UserDAO();

    // ── Initialise ───────────────────────────────────────────────────────────
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Sync expired statuses on load
        try { userDAO.syncExpiredStatuses(); } catch (Exception ignored) {}

        setupColumns();
        loadData();

        // Live search
        if (searchField != null) {
            searchField.textProperty().addListener((obs, o, n) -> applyFilter(n));
        }
    }

    // ── Column setup ─────────────────────────────────────────────────────────
    private void setupColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        colStart.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSubStart() != null
                        ? c.getValue().getSubStart().format(FMT) : "—"));

        colEnd.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSubEnd() != null
                        ? c.getValue().getSubEnd().format(FMT) : "—"));

        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSubStatus() != null
                        ? c.getValue().getSubStatus() : "INACTIVE"));

        // Colored badge for status
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(status);
                String color = switch (status) {
                    case "ACTIVE"   -> "#46d369";
                    case "EXPIRED"  -> "#e50914";
                    default         -> "#888888";
                };
                badge.setStyle("-fx-background-color: " + color + "22; " +
                        "-fx-text-fill: " + color + "; " +
                        "-fx-font-weight: bold; -fx-font-size: 11px; " +
                        "-fx-padding: 3 10 3 10; -fx-background-radius: 20;");
                setGraphic(badge);
                setText(null);
            }
        });

        // Actions column — Renouveler button
        colActions.setCellFactory(buildActionCellFactory());
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> buildActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button btn = new Button("↺ Renouveler");
            {
                btn.setStyle("-fx-background-color: #1a6b3a; -fx-text-fill: #46d369; " +
                        "-fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 11px; " +
                        "-fx-border-color: #46d36944; -fx-border-radius: 6; " +
                        "-fx-background-radius: 6; -fx-padding: 5 12 5 12;");
                btn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    try {
                        userDAO.renewSubscription(u.getId());
                        loadData();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
    }

    // ── Data loading ─────────────────────────────────────────────────────────
    private void loadData() {
        try {
            allUsers.setAll(userDAO.findAll());
            filtered = new FilteredList<>(allUsers, u -> true);
            subscriberTable.setItems(filtered);
            updateCountLabels();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyFilter(String query) {
        if (filtered == null) return;
        String q = query == null ? "" : query.trim().toLowerCase();
        filtered.setPredicate(u -> {
            if (q.isEmpty()) return true;
            return (u.getName()  != null && u.getName().toLowerCase().contains(q))
                    || (u.getEmail() != null && u.getEmail().toLowerCase().contains(q))
                    || (u.getSubStatus() != null && u.getSubStatus().toLowerCase().contains(q));
        });
        updateCountLabels();
    }

    private void updateCountLabels() {
        if (totalCountLabel   != null) totalCountLabel.setText(String.valueOf(allUsers.size()));
        if (activeCountLabel  != null) activeCountLabel.setText(String.valueOf(
                allUsers.stream().filter(u -> "ACTIVE".equals(u.getSubStatus())).count()));
        if (expiredCountLabel != null) expiredCountLabel.setText(String.valueOf(
                allUsers.stream().filter(u -> "EXPIRED".equals(u.getSubStatus())).count()));
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    @FXML private void goBack(ActionEvent event)       { navigate("/fxml/Admin.fxml", event); }
    @FXML private void goToFilmList(ActionEvent event) { navigate("/fxml/FilmList.fxml", event); }
    @FXML private void goToSeries(ActionEvent event)   { navigate("/fxml/SeriesList.fxml", event); }
    @FXML private void goToEpisodes(ActionEvent event) { navigate("/fxml/AddEpisodes.fxml", event); }
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
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }


}

