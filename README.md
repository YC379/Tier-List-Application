# ☕ Tea AirList — Créateur de Tier List interactif

---

## 📝 Présentation

Tea AirList est une application de bureau développée en **JavaFX** permettant de créer, personnaliser, sauvegarder et partager facilement des Tier Lists. Conçue avec une interface intuitive, elle repose sur un système de glisser-déposer fluide et propose des fonctionnalités avancées telles que l'importation de presets et l'intégration d'API externes.

---

## 🌟 Fonctionnalités Principales

* **Édition par glisser-déposer (Drag & Drop) :** Déplacez librement des images ou des étiquettes textuelles entre vos différents rangs.
* **Personnalisation totale :** Modifiez le nom, la couleur et l'ordre de chaque tier selon vos préférences.
* **Sauvegarde et Export :** Importez et exportez vos créations au format **JSON** pour les reprendre plus tard ou les partager.
* **Modèles prêts à l'emploi :** Accédez à des thèmes et des "blueprints" (presets) intégrés de base dans l'application.
* **Recherche automatisée (API RAWG) :** Intégration d'une barre de recherche couplée à l'API RAWG pour trouver et importer automatiquement des jaquettes de jeux vidéo directement depuis l'interface.

---

## 🛠️ Technologies & Outils

* **Langage utilisé :** Java
* **Interface graphique & Maquettage :** JavaFX (fichiers FXML), **Scene Builder** (pour la conception visuelle des vues) et **Figma** (pour le prototypage et les maquettes UI/UX en amont)
* **Gestion de projet et dépendances :** Maven (génération de "Fat JAR", wrapper `mvnw`)
* **API externe :** API RAWG (recherche automatisée et importation de jaquettes)
* **Format de données :** JSON (sérialisation des modèles, sauvegardes locales, import/export)

---

## 🚀 Comment lancer le projet

**Prérequis :** Avoir Java (version 17 minimum) installé sur votre machine.

1. Clonez ce dépôt : `git clone https://github.com/YC379/Tier-List-Application.git`
2. Ouvrez le dossier dans votre IDE (IntelliJ, Eclipse, etc.).
3. Exécutez le fichier `Launcher.java` situé dans le dossier `src/main/java/com/application/sae201` pour lancer le programme.