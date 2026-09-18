package com.application.sae201;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreationTierListController {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @FXML
    private Pane survolePane;
    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox vBoxTier;
    @FXML
    private FlowPane hBoxBase;
    @FXML
    private VBox boutonAddVBox;
    @FXML
    private Label nomTierListLabel;
    @FXML
    private ImageView corbeille;


    List<String> couleursFxml = new ArrayList<>(Arrays.asList(
            "#3498db", "#e74c3c", "#2ecc71", "#f1c40f", "#9b59b6",
            "#e67e22", "#1abc9c", "#34495e", "#e84393", "#2c3e50"
    ));
    int couleursFxmlIndex = 0;

    @FXML
    public void initialize() {
        vBoxTier.getChildren().clear();

        vBoxTier.setPrefHeight(Region.USE_COMPUTED_SIZE);

        String[] lettresDeBase = {"S", "A", "B", "C", "D", "E"};

        for (String lettre : lettresDeBase) {
            HBox nHBox = new HBox();
            nHBox.setMinHeight(93);
            nHBox.setId("hBoxG");

            String couleur = couleursFxml.get(couleursFxmlIndex);
            couleursFxmlIndex = (couleursFxmlIndex + 1) % couleursFxml.size();

            addTierStackPane(nHBox, lettre, couleur);
            vBoxTier.getChildren().add(nHBox);

            FlowPane dropZone = (FlowPane) nHBox.getChildren().get(1);
            configurerHBoxCible(dropZone);
        }

        vBoxTier.getChildren().add(boutonAddVBox);

        configurerHBoxCible(hBoxBase);

        corbeille.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        corbeille.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString() && survolePane != null) {
                Pane parent = (Pane) survolePane.getParent();
                if (parent != null) {
                    parent.getChildren().remove(survolePane);
                    success = true;
                    AppLogger.info("Item supprimé de la Tier List.");
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void deplacerTier(HBox tier, int direction) {
        int indexActuel = vBoxTier.getChildren().indexOf(tier);
        int nouvelIndex = indexActuel + direction;

        if (nouvelIndex >= 0 && nouvelIndex < vBoxTier.getChildren().size() - 1) {
            vBoxTier.getChildren().remove(tier);
            vBoxTier.getChildren().add(nouvelIndex, tier);
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
    public void reinitialiser() {
        java.util.List<javafx.scene.Node> itemsASauver = new java.util.ArrayList<>();

        for (javafx.scene.Node node : vBoxTier.getChildren()) {
            if (node instanceof javafx.scene.layout.HBox && "hBoxG".equals(node.getId())) {
                javafx.scene.layout.HBox tierRow = (javafx.scene.layout.HBox) node;

                if (tierRow.getChildren().size() > 1) {
                    javafx.scene.layout.FlowPane centerPane = (javafx.scene.layout.FlowPane) tierRow.getChildren().get(1);

                    itemsASauver.addAll(centerPane.getChildren());

                    centerPane.getChildren().clear();
                }
            }
        }

        hBoxBase.getChildren().addAll(itemsASauver);

        AppLogger.info("Plateau vidé : les items sont dans la réserve, les Tiers sont intacts.");
    }

    @FXML
    public void addTextPane() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Ajouter du texte");
        dialog.setHeaderText("Création d'une étiquette texte");
        dialog.setContentText("Veuillez entrer votre texte :");

        dialog.showAndWait().ifPresent(texte -> {
            if (!texte.trim().isEmpty()) {
                String couleur = couleursFxml.get(couleursFxmlIndex);
                couleursFxmlIndex = (couleursFxmlIndex + 1) % couleursFxml.size();

                ItemJson nouveauTexte = new ItemJson("texte", texte, couleur);
                recreerItemDansPane(nouveauTexte, hBoxBase);
            }
        });
    }

    @FXML
    public void addImagePane() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ajouter une image");
        alert.setHeaderText("D'où voulez-vous importer l'image ?");

        ButtonType btnLocal = new ButtonType("Mon Ordinateur");
        ButtonType btnApi = new ButtonType("Rechercher (API RAWG)");
        ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnLocal, btnApi, btnCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == btnLocal) {
                ajouterImageDepuisOrdinateur();
            } else if (result.get() == btnApi) {
                ajouterImageDepuisAPI();
            }
        }
    }

    private void ajouterImageDepuisOrdinateur() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers Image (*.png, *.jpg, *.jpeg, *.gif)", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            ItemJson nouvelleImage = new ItemJson("image", file.toURI().toString(), null);
            recreerItemDansPane(nouvelleImage, hBoxBase);
        }
    }

    private void ajouterImageDepuisAPI() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Recherche Web (RAWG)");
        dialog.setHeaderText("Rechercher la jaquette d'un jeu");
        dialog.setContentText("Nom du jeu :");

        String apiKey = ApiConfig.getRawgApiKey();
        if (apiKey == null) {
            Navigation.showError("Recherche indisponible",
                    "La recherche d'images via l'API RAWG n'est pas configurée.\n\n"
                            + "Définissez une clé API en variable d'environnement RAWG_API_KEY "
                            + "(ou -Drawg.api.key=... au lancement) pour activer cette fonctionnalité.\n"
                            + "Vous pouvez toujours importer une image depuis votre ordinateur.");
            return;
        }

        dialog.showAndWait().ifPresent(recherche -> {
            if (!recherche.trim().isEmpty()) {
                String query = URLEncoder.encode(recherche.trim(), StandardCharsets.UTF_8);
                String urlString = "https://api.rawg.io/api/games?key=" + apiKey + "&search=" + query + "&page_size=1";

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlString))
                        .build();

                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenAccept(body -> {
                            try {
                                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                                JsonArray results = json.getAsJsonArray("results");

                                if (results != null && results.size() > 0
                                        && results.get(0).getAsJsonObject().has("background_image")
                                        && !results.get(0).getAsJsonObject().get("background_image").isJsonNull()) {
                                    String imageUrl = results.get(0).getAsJsonObject().get("background_image").getAsString();

                                    Platform.runLater(() -> {
                                        ItemJson nouvelleImage = new ItemJson("image", imageUrl, null);
                                        recreerItemDansPane(nouvelleImage, hBoxBase);
                                        AppLogger.info("Image trouvée et ajoutée depuis RAWG.");
                                    });
                                } else {
                                    Platform.runLater(() -> Navigation.showInfo("Aucun résultat",
                                            "Aucun jeu trouvé pour \"" + recherche.trim() + "\"."));
                                }
                            } catch (Exception e) {
                                AppLogger.error("Erreur lors de la lecture des données RAWG", e);
                                Platform.runLater(() -> Navigation.showError("Recherche échouée",
                                        "Impossible de récupérer l'image depuis RAWG. Vérifiez votre connexion internet."));
                            }
                        });
            }
        });
    }

    @FXML
    public void addTierHBox() {
        vBoxTier.getChildren().remove(boutonAddVBox);

        HBox nHBox = new HBox();
        nHBox.setMinHeight(93);
        nHBox.setId("hBoxG");

        String lettre = "F";
        if (!vBoxTier.getChildren().isEmpty()) {
            Node lastTier = vBoxTier.getChildren().get(vBoxTier.getChildren().size() - 1);
            if (lastTier instanceof HBox) {
                try {
                    StackPane leftPane = (StackPane) ((HBox) lastTier).getChildren().get(0);
                    Label label = (Label) leftPane.getChildren().get(0);
                    char lastChar = label.getText().charAt(0);
                    if (lastChar >= 'A' && lastChar < 'Z') {
                        lettre = String.valueOf((char) (lastChar + 1));
                    }
                } catch (Exception ignored) {}
            }
        }

        String couleur = couleursFxml.get(couleursFxmlIndex);
        couleursFxmlIndex = (couleursFxmlIndex + 1) % couleursFxml.size();

        addTierStackPane(nHBox, lettre, couleur);

        vBoxTier.getChildren().add(nHBox);
        vBoxTier.getChildren().add(boutonAddVBox);

        FlowPane dropZone = (FlowPane) nHBox.getChildren().get(1);
        configurerHBoxCible(dropZone);
    }

    public void addTierStackPane(HBox parent, String lettre, String couleurHex) {
        StackPane leftPane = new StackPane();
        leftPane.setPrefWidth(91);
        leftPane.setMinWidth(91);
        leftPane.setMaxWidth(91);
        leftPane.setMaxHeight(Double.MAX_VALUE);
        leftPane.setStyle("-fx-background-color: " + couleurHex + "; -fx-border-color: black; -fx-border-width: 1.25;");
        Label label = new Label(lettre);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        label.textProperty().addListener((obs, oldVal, newVal) -> {
            int len = newVal.length();
            if (len <= 2) {
                label.setFont(new javafx.scene.text.Font("System", 43));
            } else if (len <= 5) {
                label.setFont(new javafx.scene.text.Font("System", 24));
            } else if (len <= 8) {
                label.setFont(new javafx.scene.text.Font("System", 16));
            } else {
                label.setFont(new javafx.scene.text.Font("System", 12));
            }
        });

        int len = lettre.length();
        if (len <= 2) label.setFont(new javafx.scene.text.Font("System", 43));
        else if (len <= 5) label.setFont(new javafx.scene.text.Font("System", 24));
        else if (len <= 8) label.setFont(new javafx.scene.text.Font("System", 16));
        else label.setFont(new javafx.scene.text.Font("System", 12));

        leftPane.getChildren().add(label);

        FlowPane centerPane = new FlowPane();
        centerPane.setMinHeight(93);
        centerPane.setHgap(5);
        centerPane.setVgap(5);
        centerPane.setPadding(new javafx.geometry.Insets(5));
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        centerPane.setStyle("-fx-background-color: #322e2e; -fx-border-color: black; -fx-border-width: 1.25;");
        centerPane.setId("hbox");

        centerPane.prefWrapLengthProperty().bind(centerPane.widthProperty());

        StackPane rightPane = new StackPane();
        rightPane.setPrefWidth(91);
        rightPane.setMinWidth(91);
        rightPane.setMaxWidth(91);
        rightPane.setMaxHeight(Double.MAX_VALUE);
        rightPane.setStyle("-fx-background-color: #3f3b3b; -fx-border-color: black; -fx-border-width: 1.25;");

        HBox buttonsHBox = new HBox();
        buttonsHBox.setAlignment(Pos.CENTER);

        Button gearButton = new Button();
        gearButton.setPrefSize(50, 50);
        gearButton.setStyle("-fx-background-color: transparent;");

        gearButton.setOnAction(e -> ouvrirParametresTier(parent));

        try {
            Image image = new Image(getClass().getResourceAsStream("/Images/services-parametres-et-icone-d-engrenage-noir.png"));
            ImageView gearIcon = new ImageView(image);
            gearIcon.setFitHeight(42);
            gearIcon.setFitWidth(43);
            gearIcon.setPreserveRatio(true);
            gearButton.setGraphic(gearIcon);
        } catch (Exception e) {
            gearButton.setText("⚙");
            gearButton.setFont(new Font(20));
        }

        VBox arrowsVBox = new VBox();
        arrowsVBox.setAlignment(Pos.CENTER);

        Button upButton = new Button("^");
        upButton.setPrefSize(46, 30);
        upButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-padding: 0;");
        upButton.setFont(new Font(18));

        Button downButton = new Button("v");
        downButton.setPrefSize(46, 30);
        downButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-padding: 0;");
        downButton.setFont(new Font(18));

        upButton.setOnAction(e -> deplacerTier(parent, -1));
        downButton.setOnAction(e -> deplacerTier(parent, 1));



        arrowsVBox.getChildren().addAll(upButton, downButton);
        buttonsHBox.getChildren().addAll(gearButton, arrowsVBox);
        rightPane.getChildren().add(buttonsHBox);

        parent.getChildren().addAll(leftPane, centerPane, rightPane);
    }

    private void ouvrirParametresTier(HBox tier) {
        Stage stage = new Stage();
        stage.setTitle("Paramètres du Tier");

        StackPane leftPane = (StackPane) tier.getChildren().get(0);
        Label label = (Label) leftPane.getChildren().get(0);

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new javafx.geometry.Insets(20));

        javafx.scene.control.TextField nameField = new javafx.scene.control.TextField(label.getText());

        javafx.scene.control.ColorPicker colorPicker = new javafx.scene.control.ColorPicker();
        String couleurActuelle = extraireCouleurDepuisStyle(leftPane.getStyle());
        colorPicker.setValue(javafx.scene.paint.Color.web(couleurActuelle));

        Button deleteBtn = new Button("Supprimer ce Tier");
        deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            vBoxTier.getChildren().remove(tier);
            stage.close();
        });

        Button saveBtn = new Button("Appliquer");
        saveBtn.setOnAction(e -> {
            label.setText(nameField.getText());
            String newColor = "#" + colorPicker.getValue().toString().substring(2, 8);
            leftPane.setStyle("-fx-background-color: " + newColor + "; -fx-border-color: black; -fx-border-width: 1.25;");
            stage.close();
        });

        root.getChildren().addAll(new Label("Nom du Tier:"), nameField, new Label("Couleur:"), colorPicker, saveBtn, deleteBtn);
        stage.setScene(new javafx.scene.Scene(root, 300, 300));
        stage.show();
    }

    private void searchHBoxes(Node node, List<HBox> list) {
        if (node instanceof HBox) {
            list.add((HBox) node);
        }
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                searchHBoxes(child, list);
            }
        }
    }

    private void searchImagePane(Node node, List<Pane> list) {
        if (node instanceof Pane) {
            list.add((Pane) node);
        }
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                searchImagePane(child, list);
            }
        }
    }

    private void initializeDrag() {
        survolePane.setOnDragDetected(event -> {
            Dragboard db = survolePane.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(survolePane.getId());
            db.setContent(content);
            db.setDragView(survolePane.snapshot(null, null));
            event.consume();
        });
    }

    @FXML
    private void detectHoverImage(MouseEvent event) {
        survolePane = (Pane) event.getSource();
        initializeDrag();
    }

    private void configurerHBoxCible(Pane cible) {
        cible.setOnDragOver(event -> {
            if (event.getGestureSource() != cible && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cible.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString() && survolePane != null && db.getString().equals(survolePane.getId())) {
                Pane ancienParent = (Pane) survolePane.getParent();
                if (ancienParent != null) {
                    ancienParent.getChildren().remove(survolePane);
                }

                double mouseX = event.getX();
                double mouseY = event.getY();

                int insertIndex = cible.getChildren().size();

                for (int i = 0; i < cible.getChildren().size(); i++) {
                    Node child = cible.getChildren().get(i);

                    if (child.getBoundsInParent().contains(mouseX, mouseY)) {

                        double milieuDuCarre = child.getBoundsInParent().getMinX() + (child.getBoundsInParent().getWidth() / 2);

                        if (mouseX < milieuDuCarre) {
                            insertIndex = i;
                        } else {
                            insertIndex = i + 1;
                        }
                        break;
                    }
                }

                cible.getChildren().add(insertIndex, survolePane);

                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    public void importSave() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir une sauvegarde");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers JSON (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;

        vBoxTier.getChildren().clear();
        hBoxBase.getChildren().clear();

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            TierListSaveJson save = gson.fromJson(reader, TierListSaveJson.class);
            if (save == null || save.tierJsonList == null) {
                Navigation.showError("Import impossible", "Le fichier sélectionné n'est pas une Tier List valide.");
                return;
            }

            if (save.nomTierList != null) {
                nomTierListLabel.setText(save.nomTierList);
            }

            for (TierJson tierJson : save.tierJsonList) {
                HBox nHBox = new HBox();
                nHBox.setMinHeight(93);
                nHBox.setId("hBoxG");
                addTierStackPane(nHBox, tierJson.labelTier, tierJson.couleurTier);
                vBoxTier.getChildren().add(nHBox);

                FlowPane dropZone = (FlowPane) nHBox.getChildren().get(1);
                configurerHBoxCible(dropZone);

                if (tierJson.items != null) {
                    for (ItemJson item : tierJson.items) {
                        recreerItemDansPane(item, dropZone);
                    }
                }
            }
            vBoxTier.getChildren().add(boutonAddVBox);

            if (save.reserveItems != null) {
                for (ItemJson item : save.reserveItems) {
                    recreerItemDansPane(item, hBoxBase);
                }
            }

            AppLogger.info("Tier List importée avec succès : " + file.getName());
        } catch (IOException | JsonSyntaxException e) {
            AppLogger.error("Erreur lors de l'import de " + file.getName(), e);
            Navigation.showError("Import impossible", "Ce fichier ne peut pas être importé : " + e.getMessage());
        }
    }

    public void addTierHBoxFromImport(TierJson tier) {
        HBox nHBox = new HBox();
        nHBox.setMinHeight(93);
        nHBox.setId("hBoxG");

        addTierStackPane(nHBox, tier.labelTier, tier.couleurTier);
        vBoxTier.getChildren().add(nHBox);

        FlowPane dropZone = (FlowPane) nHBox.getChildren().get(1);
        configurerHBoxCible(dropZone);
    }

    public void chargerDepuisFichier(File fichier) {
        if (fichier == null || !fichier.exists()) {
            return;
        }

        vBoxTier.getChildren().clear();
        hBoxBase.getChildren().clear();

        try (FileReader reader = new FileReader(fichier, StandardCharsets.UTF_8)) {
            TierListSaveJson save = gson.fromJson(reader, TierListSaveJson.class);
            if (save == null || save.tierJsonList == null) {
                AppLogger.warn("Fichier de Tier List vide ou invalide : " + fichier.getAbsolutePath());
                Navigation.showError("Chargement impossible", "Ce fichier n'est pas une Tier List valide.");
                return;
            }

            if (save.nomTierList != null) {
                nomTierListLabel.setText(save.nomTierList);
            }

            for (TierJson tierJson : save.tierJsonList) {
                HBox nHBox = new HBox();
                nHBox.setMinHeight(93);
                nHBox.setId("hBoxG");
                addTierStackPane(nHBox, tierJson.labelTier, tierJson.couleurTier);
                vBoxTier.getChildren().add(nHBox);

                FlowPane dropZone = (FlowPane) nHBox.getChildren().get(1);
                configurerHBoxCible(dropZone);

                if (tierJson.items != null) {
                    for (ItemJson item : tierJson.items) {
                        recreerItemDansPane(item, dropZone);
                    }
                }
            }

            vBoxTier.getChildren().add(boutonAddVBox);

            if (save.reserveItems != null) {
                for (ItemJson item : save.reserveItems) {
                    recreerItemDansPane(item, hBoxBase);
                }
            }

            AppLogger.info("Tier List chargée pour édition : " + fichier.getName());

        } catch (IOException | JsonSyntaxException e) {
            AppLogger.error("Erreur lors du chargement de la Tier List " + fichier.getName(), e);
            Navigation.showError("Chargement impossible", "Impossible de charger cette Tier List : " + e.getMessage());
        }
    }

    @FXML
    public void exportSave() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(nomTierListLabel.getText());
        dialog.setTitle("Sauvegarde");
        dialog.setHeaderText("Enregistrer votre Tier-List");
        dialog.setContentText("Nom de la Tier-List :");

        java.util.Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String nomSaisi = result.get().trim();

            String nomFichierPropre = nomSaisi.replaceAll("[^a-zA-Z0-9\\-_ ]", "_");

            TierListSaveJson save = new TierListSaveJson();
            save.nomTierList = nomFichierPropre;
            save.tierJsonList = new java.util.ArrayList<>();
            save.reserveItems = new java.util.ArrayList<>();

            for (javafx.scene.Node node : vBoxTier.getChildren()) {
                if (node instanceof javafx.scene.layout.HBox && "hBoxG".equals(node.getId())) {
                    javafx.scene.layout.HBox hBox = (javafx.scene.layout.HBox) node;
                    javafx.scene.layout.StackPane leftPane = (javafx.scene.layout.StackPane) hBox.getChildren().get(0);
                    javafx.scene.layout.FlowPane centerPane = (javafx.scene.layout.FlowPane) hBox.getChildren().get(1);

                    String labelText = ((javafx.scene.control.Label) leftPane.getChildren().get(0)).getText();
                    String couleurHex = extraireCouleurDepuisStyle(leftPane.getStyle());

                    TierJson tier = new TierJson(labelText, "#000000", couleurHex);

                    for (javafx.scene.Node itemNode : centerPane.getChildren()) {
                        if ("pane".equals(itemNode.getId()) && itemNode instanceof javafx.scene.layout.Pane) {
                            tier.items.add(creerItemJsonDepuisPane((javafx.scene.layout.Pane) itemNode));
                        }
                    }
                    save.tierJsonList.add(tier);
                }
            }

            for (javafx.scene.Node itemNode : hBoxBase.getChildren()) {
                if ("pane".equals(itemNode.getId()) && itemNode instanceof javafx.scene.layout.Pane) {
                    save.reserveItems.add(creerItemJsonDepuisPane((javafx.scene.layout.Pane) itemNode));
                }
            }

            File dossierSaves = AppPaths.getSavesDir();
            File file = new File(dossierSaves, nomFichierPropre + ".json");

            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                gson.toJson(save, writer);
                AppLogger.info("Tier List exportée avec succès sous : " + file.getAbsolutePath());

                nomTierListLabel.setText(save.nomTierList);
                Navigation.showInfo("Sauvegarde réussie", "La Tier List \"" + save.nomTierList + "\" a été enregistrée.");

            } catch (IOException e) {
                AppLogger.error("Erreur lors de la sauvegarde de " + file.getAbsolutePath(), e);
                Navigation.showError("Sauvegarde impossible", "La Tier List n'a pas pu être enregistrée : " + e.getMessage());
            }
        }
    }

    private String extraireCouleurDepuisStyle(String style) {
        if (style == null) return "#FFFFFF";
        int debut = style.indexOf("-fx-background-color:");
        if (debut != -1) {
            int fin = style.indexOf(";", debut);
            if (fin == -1) fin = style.length();
            return style.substring(debut + 21, fin).trim();
        }
        return "#FFFFFF";
    }

    public String extraireUrlDepuisStyle(Pane pane) {
        String style = pane.getStyle();
        int debutUrl = style.indexOf("url(");
        if (debutUrl == -1) return null;
        debutUrl += 4;
        if (style.charAt(debutUrl) == '\'' || style.charAt(debutUrl) == '"') {
            debutUrl++;
        }
        int finUrl = style.indexOf(")", debutUrl);
        if (finUrl == -1) return null;
        if (style.charAt(finUrl - 1) == '\'' || style.charAt(finUrl - 1) == '"') {
            finUrl--;
        }
        return style.substring(debutUrl, finUrl).trim();
    }

    private ItemJson creerItemJsonDepuisPane(Pane pane) {
        if (!pane.getChildren().isEmpty()) {
            javafx.scene.Node enfant = pane.getChildren().get(0);

            if (enfant instanceof javafx.scene.image.ImageView) {
                javafx.scene.image.ImageView imageView = (javafx.scene.image.ImageView) enfant;
                String url = imageView.getImage().getUrl();
                return new ItemJson("image", url, null);
            }

            else if (enfant instanceof javafx.scene.control.Label) {
                javafx.scene.control.Label label = (javafx.scene.control.Label) enfant;

                String textePropre = label.getText().replace("\n", "");

                String couleurHex = extraireCouleurDepuisStyle(pane.getStyle());
                return new ItemJson("texte", textePropre, couleurHex);
            }
        }

        String style = pane.getStyle();
        if (style != null && style.contains("url('")) {
            int start = style.indexOf("url('") + 5;
            int end = style.indexOf("')", start);
            if (start > 4 && end > start) {
                return new ItemJson("image", style.substring(start, end), null);
            }
        }

        return new ItemJson("inconnu", "", null);
    }

    public void recreerItemDansPane(ItemJson item, FlowPane parent) {
        if ("texte".equals(item.type)) {
            StackPane textPane = new StackPane();
            textPane.setPrefSize(91, 93);
            textPane.setMinSize(91, 93);
            textPane.setMaxSize(91, 93);
            textPane.setStyle("-fx-background-color: " + item.couleur + "; -fx-border-color: black; -fx-border-width: 1.25;");

            String texteAffiche = item.contenu;

            if (!texteAffiche.contains(" ") && texteAffiche.length() > 8) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < texteAffiche.length(); i++) {
                    sb.append(texteAffiche.charAt(i));
                    if ((i + 1) % 8 == 0) {
                        sb.append("\n");
                    }
                }
                texteAffiche = sb.toString();
            }

            Label label = new Label(texteAffiche);
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            label.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);

            if (item.contenu.length() > 20) {
                label.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: white;");
            } else {
                label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");
            }

            textPane.getChildren().add(label);
            textPane.setId("pane");
            textPane.setOnMouseEntered(this::detectHoverImage);
            parent.getChildren().add(textPane);

        } else if ("image".equals(item.type)) {
            StackPane imagePane = new StackPane();
            imagePane.setPrefSize(91, 93);
            imagePane.setMinSize(91, 93);
            imagePane.setMaxSize(91, 93);
            imagePane.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: black; -fx-border-width: 1.25;");

            String cheminImage = item.contenu;
            javafx.scene.image.Image img;

            if (cheminImage.startsWith("http") || cheminImage.startsWith("file:")) {
                img = new javafx.scene.image.Image(cheminImage);
            } else {
                java.net.URL resourceUrl = getClass().getResource(cheminImage);
                if (resourceUrl != null) {
                    img = new javafx.scene.image.Image(resourceUrl.toExternalForm());
                } else {
                    AppLogger.warn("Image introuvable dans les ressources : " + cheminImage);
                    return;
                }
            }

            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(img);

            imageView.setFitWidth(89);
            imageView.setFitHeight(91);

            imageView.setPreserveRatio(true);

            imagePane.getChildren().add(imageView);
            imagePane.setId("pane");
            imagePane.setOnMouseEntered(this::detectHoverImage);
            parent.getChildren().add(imagePane);
        }
    }
}