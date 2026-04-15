package com.jstream.controller;

import com.jstream.dao.SubscriptionDAO;
import com.jstream.model.Subscription;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AbonnesController {

    // ── FXML fields ─────────────────────────────────────────────────────────
    @FXML private TextField                        searchField;
    @FXML private ComboBox<String>                 planFilter;
    @FXML private Label                            totalCountLabel;

    @FXML private TableView<Subscription>          abonnesTable;
    @FXML private TableColumn<Subscription, String> colId;
    @FXML private TableColumn<Subscription, String> colUserId;
    @FXML private TableColumn<Subscription, String> colPlan;
    @FXML private TableColumn<Subscription, String> colStartDate;
    @FXML private TableColumn<Subscription, String> colEndDate;
    @FXML private TableColumn<Subscription, String> colActive;

    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnBack;

    private final SubscriptionDAO dao = new SubscriptionDAO();

    // ════════════════════════════════════════════════════════════════════════
    //  INITIALISATION
    // ════════════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        setupColumns();
        setupPlanFilter();
        setupSearch();
        loadTable(null, null);
    }

    // ── Colonnes ────────────────────────────────────────────────────────────

    private void setupColumns() {
        colId       .setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colUserId   .setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getUserId())));
        colPlan     .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPlan()));
        colStartDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStartDate()));
        colEndDate  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEndDate()));
        colActive   .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isActive() ? "✅ Actif" : "❌ Inactif"));

        // Couleur du statut
        colActive.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.contains("Actif")
                        ? "-fx-text-fill: #46d369; -fx-font-weight: bold;"
                        : "-fx-text-fill: #ff4444; -fx-font-weight: bold;");
            }
        });
    }

    // ── Filtre plan ─────────────────────────────────────────────────────────

    private void setupPlanFilter() {
        planFilter.getItems().addAll("Tous les plans", "Basic", "Standard", "Premium");
        planFilter.setValue("Tous les plans");
        planFilter.setOnAction(e -> applyFilters());
    }

    // ── Recherche en temps réel ──────────────────────────────────────────────

    private void setupSearch() {
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    private void applyFilters() {
        String search = searchField.getText().trim();
        String plan   = planFilter.getValue();
        loadTable(
                search.isEmpty() ? null : search,
                "Tous les plans".equals(plan) ? null : plan
        );
    }

    // ── Chargement données ───────────────────────────────────────────────────

    private void loadTable(String search, String plan) {
        try {
            List<Subscription> list = dao.findAll(search, plan);
            abonnesTable.getItems().setAll(list);
            totalCountLabel.setText(String.valueOf(list.size()));
        } catch (SQLException e) {
            showError("Erreur chargement : " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ACTIONS BOUTONS
    // ════════════════════════════════════════════════════════════════════════

    /** Ouvre un dialog pour ajouter un nouvel abonnement. */
    @FXML
    private void handleAdd() {
        Dialog<Subscription> dialog = buildSubscriptionDialog(null);
        Optional<Subscription> result = dialog.showAndWait();
        result.ifPresent(sub -> {
            try {
                dao.add(sub);
                applyFilters();
            } catch (SQLException e) {
                showError("Erreur ajout : " + e.getMessage());
            }
        });
    }

    /** Modifie l'abonnement sélectionné. */
    @FXML
    private void handleEdit() {
        Subscription selected = abonnesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showInfo("Sélectionnez un abonné à modifier."); return; }

        Dialog<Subscription> dialog = buildSubscriptionDialog(selected);
        Optional<Subscription> result = dialog.showAndWait();
        result.ifPresent(sub -> {
            try {
                dao.update(sub);
                applyFilters();
            } catch (SQLException e) {
                showError("Erreur modification : " + e.getMessage());
            }
        });
    }

    /** Supprime l'abonnement sélectionné après confirmation. */
    @FXML
    private void handleDelete() {
        Subscription selected = abonnesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showInfo("Sélectionnez un abonné à supprimer."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer l'abonnement ID " + selected.getId() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmation suppression");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    dao.delete(selected.getId());
                    applyFilters();
                } catch (SQLException e) {
                    showError("Erreur suppression : " + e.getMessage());
                }
            }
        });
    }

    /** Retour au Dashboard. */
    @FXML
    private void goToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Admin.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Navigation impossible : " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DIALOG AJOUT / MODIFICATION
    // ════════════════════════════════════════════════════════════════════════

    private Dialog<Subscription> buildSubscriptionDialog(Subscription existing) {
        boolean isEdit = existing != null;

        Dialog<Subscription> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Modifier l'abonnement" : "Nouvel abonnement");
        dialog.setHeaderText(isEdit ? "Modifier l'abonnement ID " + existing.getId() : "Ajouter un abonnement");

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        // Champs
        TextField tfUserId    = new TextField(isEdit ? String.valueOf(existing.getUserId()) : "");
        TextField tfStartDate = new TextField(isEdit ? existing.getStartDate() : "");
        TextField tfEndDate   = new TextField(isEdit ? existing.getEndDate() : "");
        ComboBox<String> cbPlan = new ComboBox<>();
        cbPlan.getItems().addAll("Basic", "Standard", "Premium");
        cbPlan.setValue(isEdit ? existing.getPlan() : "Basic");

        CheckBox cbActive = new CheckBox("Actif");
        cbActive.setSelected(!isEdit || existing.isActive());

        tfUserId.setPromptText("ID utilisateur");
        tfStartDate.setPromptText("YYYY-MM-DD");
        tfEndDate.setPromptText("YYYY-MM-DD");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(16));
        grid.add(new Label("User ID :"),    0, 0); grid.add(tfUserId,    1, 0);
        grid.add(new Label("Plan :"),       0, 1); grid.add(cbPlan,      1, 1);
        grid.add(new Label("Début :"),      0, 2); grid.add(tfStartDate, 1, 2);
        grid.add(new Label("Fin :"),        0, 3); grid.add(tfEndDate,   1, 3);
        grid.add(new Label("Statut :"),     0, 4); grid.add(cbActive,    1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;
            try {
                Subscription s = new Subscription();
                if (isEdit) s.setId(existing.getId());
                s.setUserId(Integer.parseInt(tfUserId.getText().trim()));
                s.setPlan(cbPlan.getValue());
                s.setStartDate(tfStartDate.getText().trim());
                s.setEndDate(tfEndDate.getText().trim());
                s.setActive(cbActive.isSelected());
                return s;
            } catch (NumberFormatException e) {
                showError("User ID invalide.");
                return null;
            }
        });

        return dialog;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UTILITAIRES
    // ════════════════════════════════════════════════════════════════════════

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}