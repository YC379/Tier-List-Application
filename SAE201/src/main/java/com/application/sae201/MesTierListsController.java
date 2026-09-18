package com.application.sae201;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;

public class MesTierListsController implements Initializable {

    @FXML
    private FlowPane tierListsContainer;

    private final Gson gson = new Gson();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerTierLists();
    }

    private void chargerTierLists() {
        if (tierListsContainer != null) {
            tierListsContainer.getChildren().clear();
        }

        File dossierSaves = AppPaths.getSavesDir();
        File[] fichiers = dossierSaves.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (fichiers != null) {
            for (File fichier : fichiers) {
                try (FileReader reader = new FileReader(fichier, StandardCharsets.UTF_8)) {
                    TierListSaveJson save = gson.fromJson(reader, TierListSaveJson.class);
                    if (save == null) {
                        AppLogger.warn("Fichier de sauvegarde vide ou invalide, ignoré : " + fichier.getName());
                        continue;
                    }
                    VBox carte = creerCarteAffichage(save, fichier);
                    tierListsContainer.getChildren().add(carte);
                } catch (IOException | JsonSyntaxException e) {
                    AppLogger.error("Impossible de lire la sauvegarde : " + fichier.getName(), e);
                }
            }
        }

        Button btnAdd = new Button("+");
        btnAdd.setPrefSize(290, 190);
        btnAdd.setStyle("-fx-background-color: #7a5a9a; -fx-background-radius: 14; -fx-text-fill: white; -fx-font-size: 48px; -fx-cursor: hand;");
        btnAdd.setOnAction(this::creerNouvelleTierList);

        VBox boxAdd = new VBox(btnAdd);
        boxAdd.setAlignment(Pos.CENTER);
        tierListsContainer.getChildren().add(boxAdd);
    }

    private ImageView creerIcone(String cheminImage) {
        try {
            Image image = new Image(getClass().getResourceAsStream(cheminImage));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(14);
            imageView.setFitHeight(14);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            AppLogger.warn("Icône introuvable : " + cheminImage);
            return null;
        }
    }

    private VBox creerCarteAffichage(TierListSaveJson save, File fichier) {
        VBox vboxPrincipale = new VBox(8);
        vboxPrincipale.setAlignment(Pos.CENTER);

        StackPane preview = new StackPane();
        preview.setPrefSize(290, 190);
        preview.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 14; -fx-border-color: #5a3a7a; -fx-border-radius: 14; -fx-border-width: 2;");

        VBox tiersBox = new VBox();
        tiersBox.setPadding(new Insets(8, 0, 0, 0));
        tiersBox.setAlignment(Pos.TOP_LEFT);

        int nbLignes = save.tierJsonList != null ? Math.min(5, save.tierJsonList.size()) : 0;
        for (int i = 0; i < nbLignes; i++) {
            TierJson tier = save.tierJsonList.get(i);

            HBox ligne = new HBox();
            String initiale = (tier.labelTier != null && !tier.labelTier.isEmpty())
                    ? tier.labelTier.substring(0, 1).toUpperCase()
                    : "?";
            Label lblNom = new Label(initiale);
            lblNom.setMinWidth(28);
            String couleur = tier.couleurTier != null ? tier.couleurTier : "#666666";
            lblNom.setStyle("-fx-background-color: " + couleur + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 6;");

            Pane fondNoir = new Pane();
            fondNoir.setPrefHeight(22);
            fondNoir.setStyle("-fx-background-color: #2a2a2a;");
            HBox.setHgrow(fondNoir, Priority.ALWAYS);

            ligne.getChildren().addAll(lblNom, fondNoir);
            tiersBox.getChildren().add(ligne);
        }

        VBox actionsBox = new VBox(8);
        actionsBox.setPadding(new Insets(8));
        actionsBox.setAlignment(Pos.TOP_RIGHT);

        Button btnDelete = new Button();
        ImageView iconDelete = creerIcone("/Images/corbeille.png");
        if (iconDelete != null) btnDelete.setGraphic(iconDelete);
        else btnDelete.setText("🗑");
        btnDelete.setStyle("-fx-background-color: #ff4444; -fx-background-radius: 20; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> supprimerTierList(fichier, save.nomTierList));

        Button btnEdit = new Button();
        ImageView iconEdit = creerIcone("/Images/edit.png");
        if (iconEdit != null) btnEdit.setGraphic(iconEdit);
        else btnEdit.setText("✏️");
        btnEdit.setStyle("-fx-background-color: #f39c12; -fx-background-radius: 20; -fx-cursor: hand;");
        btnEdit.setOnAction(e -> editerTierList(e, fichier));

        Button btnDuplicate = new Button();
        ImageView iconDuplicate = creerIcone("/Images/dupliquer.png");
        if (iconDuplicate != null) btnDuplicate.setGraphic(iconDuplicate);
        else btnDuplicate.setText("📄");
        btnDuplicate.setStyle("-fx-background-color: #3498db; -fx-background-radius: 20; -fx-cursor: hand;");
        btnDuplicate.setOnAction(e -> dupliquerTierList(fichier));

        actionsBox.getChildren().addAll(btnDelete, btnEdit, btnDuplicate);

        preview.getChildren().addAll(tiersBox, actionsBox);

        Label lblTitre = new Label(save.nomTierList != null ? save.nomTierList : fichier.getName());
        lblTitre.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        vboxPrincipale.getChildren().addAll(preview, lblTitre);
        return vboxPrincipale;
    }

    private void supprimerTierList(File fichier, String nom) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer la Tier List");
        confirmation.setHeaderText("Supprimer \"" + (nom != null ? nom : fichier.getName()) + "\" ?");
        confirmation.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (fichier.delete()) {
                chargerTierLists();
            } else {
                AppLogger.error("Échec de la suppression du fichier : " + fichier.getAbsolutePath());
                Navigation.showError("Suppression impossible",
                        "Le fichier n'a pas pu être supprimé. Vérifiez qu'il n'est pas utilisé par un autre programme.");
            }
        }
    }

    private void dupliquerTierList(File fichierOriginal) {
        try (FileReader reader = new FileReader(fichierOriginal, StandardCharsets.UTF_8)) {
            TierListSaveJson copie = gson.fromJson(reader, TierListSaveJson.class);
            if (copie == null) {
                Navigation.showError("Duplication impossible", "Le fichier source est vide ou corrompu.");
                return;
            }

            TextInputDialog dialog = new TextInputDialog(copie.nomTierList + " (copie)");
            dialog.setTitle("Dupliquer");
            dialog.setHeaderText("Créer une copie de : " + copie.nomTierList);
            dialog.setContentText("Nouveau nom :");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && !result.get().trim().isEmpty()) {
                String nouveauNom = result.get().trim();
                copie.nomTierList = nouveauNom;

                String nomFichierPropre = nouveauNom.replaceAll("[^a-zA-Z0-9\\-_ ]", "_") + ".json";
                File nouveauFichier = new File(AppPaths.getSavesDir(), nomFichierPropre);

                try (FileWriter writer = new FileWriter(nouveauFichier, StandardCharsets.UTF_8)) {
                    gson.toJson(copie, writer);
                }

                chargerTierLists();
            }

        } catch (IOException | JsonSyntaxException e) {
            AppLogger.error("Erreur lors de la duplication de " + fichierOriginal.getName(), e);
            Navigation.showError("Duplication impossible", e.getMessage());
        }
    }

    private void editerTierList(ActionEvent event, File fichier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/application/sae201/CreationTierList.fxml"));
            Parent root = loader.load();

            CreationTierListController controller = loader.getController();
            controller.chargerDepuisFichier(fichier);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.show();
        } catch (IOException e) {
            AppLogger.error("Erreur lors de l'ouverture pour édition : " + fichier.getName(), e);
            Navigation.showError("Ouverture impossible", "Impossible d'ouvrir cette Tier List pour édition.");
        }
    }

    @FXML
    public void onAccueil(ActionEvent event) {
        Navigation.goTo(event, "com/application/sae201/Accueil.fxml");
    }

    @FXML
    public void onMesTierLists(ActionEvent event) {
        Navigation.goTo(event, "com/application/sae201/MesTierLists.fxml");
    }

    @FXML
    public void onThemes(ActionEvent event) {
        Navigation.goTo(event, "com/application/sae201/Themes.fxml");
    }

    @FXML
    public void onBlueprint(ActionEvent event) {
        Navigation.goTo(event, "com/application/sae201/Blueprint.fxml");
    }

    @FXML
    public void creerNouvelleTierList(ActionEvent event) {
        Navigation.goTo(event, "com/application/sae201/CreationTierList.fxml");
    }
}
