package com.application.sae201;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public final class Navigation {

    private Navigation() {
    }

    public static void goTo(Event event, String fxmlClasspath) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigation.class.getResource("/" + fxmlClasspath));
            Parent root = loader.load();

            Stage stage = resolveStage(event);
            if (stage == null) {
                AppLogger.error("Impossible de déterminer la fenêtre courante pour la navigation vers " + fxmlClasspath);
                return;
            }

            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.show();
        } catch (IOException e) {
            AppLogger.error("Erreur lors du chargement de la page : " + fxmlClasspath, e);
            showError("Navigation impossible",
                    "Impossible d'ouvrir cet écran. Réessayez, et si le problème persiste, "
                            + "signalez-le avec le message suivant :\n" + e.getMessage());
        }
    }

    public static void openPreset(Event event, String presetFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigation.class.getResource("/com/application/sae201/CreationTierList.fxml"));
            Parent root = loader.load();

            CreationTierListController controller = loader.getController();

            File fichierPreset = AppPaths.getPresetFile(presetFileName);
            if (fichierPreset.exists()) {
                controller.chargerDepuisFichier(fichierPreset);
            } else {
                AppLogger.warn("Fichier preset introuvable : " + fichierPreset.getAbsolutePath());
                showError("Preset introuvable",
                        "Le preset \"" + presetFileName + "\" n'a pas été trouvé.\n"
                                + "Un nouvel écran de création vierge a été ouvert à la place.");
            }

            Stage stage = resolveStage(event);
            if (stage == null) {
                AppLogger.error("Impossible de déterminer la fenêtre courante pour ouvrir le preset " + presetFileName);
                return;
            }

            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.show();

        } catch (IOException e) {
            AppLogger.error("Erreur lors de l'ouverture du preset " + presetFileName, e);
            showError("Ouverture impossible",
                    "Impossible d'ouvrir ce preset. " + e.getMessage());
        }
    }

    public static void showError(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static Stage resolveStage(Event event) {
        Object source = event.getSource();
        if (source instanceof Node) {
            Scene scene = ((Node) source).getScene();
            if (scene != null) {
                return (Stage) scene.getWindow();
            }
        }
        return null;
    }
}
