package com.application.sae201;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class BlueprintController {

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
    public void onPresetBP1(MouseEvent event) {
        Navigation.openPreset(event, "BP1.json");
    }

    @FXML
    public void onPresetBP2(MouseEvent event) {
        Navigation.openPreset(event, "BP2.json");
    }

    @FXML
    public void onPresetBP3(MouseEvent event) {
        Navigation.openPreset(event, "BP3.json");
    }

    @FXML
    public void onPresetBP4(MouseEvent event) {
        Navigation.openPreset(event, "BP4.json");
    }

    @FXML
    public void onPresetBP5(MouseEvent event) {
        Navigation.openPreset(event, "BP5.json");
    }

    @FXML
    public void onPresetBP6(MouseEvent event) {
        Navigation.openPreset(event, "BP6.json");
    }
}
