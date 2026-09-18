package com.application.sae201;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class ThemesController {

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
    public void onPresetMapsCS2(MouseEvent event) {
        Navigation.openPreset(event, "MapsCS2.json");
    }

    @FXML
    public void onPresetRunesLol(MouseEvent event) {
        Navigation.openPreset(event, "RunesLol.json");
    }

    @FXML
    public void onPresetCyberpunk(MouseEvent event) {
        Navigation.openPreset(event, "ImplantsCyberPunk.json");
    }

    @FXML
    public void onPresetWarframe(MouseEvent event) {
        Navigation.openPreset(event, "OpenWorldWarFrame.json");
    }

    @FXML
    public void onPresetHellDivers(MouseEvent event) {
        Navigation.openPreset(event, "HellDivers2Factions.json");
    }

    @FXML
    public void onPresetStarWars(MouseEvent event) {
        Navigation.openPreset(event, "Films_StarWars.json");
    }
}
