package com.application.sae201;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class AccueilController {

    @FXML
    public void onPresetAvis1(MouseEvent event) {
        Navigation.openPreset(event, "Avis1.json");
    }

    @FXML
    public void onPresetAvis2(MouseEvent event) {
        Navigation.openPreset(event, "Avis2.json");
    }

    @FXML
    public void onPresetMapsCS2(MouseEvent event) {
        Navigation.openPreset(event, "MapsCS2.json");
    }

    @FXML
    public void onPresetRunesLol(MouseEvent event) {
        Navigation.openPreset(event, "RunesLol.json");
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
}
