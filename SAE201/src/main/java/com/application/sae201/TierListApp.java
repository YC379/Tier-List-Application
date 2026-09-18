package com.application.sae201;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class TierListApp extends Application {

    private static final double MIN_WIDTH = 1024;
    private static final double MIN_HEIGHT = 700;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TierListApp.class.getResource("/com/application/sae201/Accueil.fxml"));

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = Math.min(1920, visualBounds.getWidth());
        double height = Math.min(1080, visualBounds.getHeight());

        Scene scene = new Scene(fxmlLoader.load(), width, height);

        stage.setTitle("Tea AirList - Créateur de Tier List");
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
