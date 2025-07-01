package com.app.photon;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class App extends Application {

    private static final String PHOTO_DIR = "photos";
    private GridPane gridPane;
    private PhotoImporter photoImporter;
    private ThumbnailLoader thumbnailLoader;
    private CollectionManager collectionManager;
    private ComboBox<String> collectionComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        photoImporter = new PhotoImporter(PHOTO_DIR);
        thumbnailLoader = new ThumbnailLoader(PHOTO_DIR);
        collectionManager = new CollectionManager();

        // Add default "All Photos" collection
        collectionManager.addCollection("All Photos");

        // UI: Collection selector
        collectionComboBox = new ComboBox<>();
        collectionComboBox.getItems().addAll(collectionManager.getAllCollections().stream()
                .map(PhotoCollection::getName).toList());
        collectionComboBox.setValue("All Photos");
        collectionComboBox.setOnAction(e -> showCollection(collectionComboBox.getValue()));

        // Import button
        Button importButton = new Button("Import Photos");
        importButton.setOnAction(e -> {
            List<File> importedFiles = photoImporter.importPhotosWithReturn();
            if (importedFiles != null) {
                for (File file : importedFiles) {
                    Photo photo = new Photo(Path.of(PHOTO_DIR, file.getName()));
                    collectionManager.addPhotoToCollection(photo, "All Photos");
                }
                showCollection(collectionComboBox.getValue());
            }
        });

        // Add Collection button (optional)
        Button addCollectionButton = new Button("Add Collection");
        addCollectionButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Collection");
            dialog.setHeaderText("Create a new photo collection");
            dialog.setContentText("Collection name:");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.isBlank() && collectionManager.getCollection(name) == null) {
                    collectionManager.addCollection(name);
                    collectionComboBox.getItems().add(name);
                }
            });
        });

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        HBox controls = new HBox(10, importButton, addCollectionButton, collectionComboBox);
        VBox root = new VBox(10, controls, gridPane);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Photon - Photo Organizer");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial load
        loadExistingPhotos();
        showCollection("All Photos");
    }

    // Loads existing photos from the directory into the "All Photos" collection on
    // startup
    private void loadExistingPhotos() {
        File folder = new File(PHOTO_DIR);
        if (folder.exists()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$"));
            if (files != null) {
                for (File file : files) {
                    Photo photo = new Photo(file.toPath());
                    collectionManager.addPhotoToCollection(photo, "All Photos");
                }
            }
        }
    }

    // Shows thumbnails for the selected collection
    private void showCollection(String collectionName) {
        PhotoCollection collection = collectionManager.getCollection(collectionName);
        if (collection != null) {
            thumbnailLoader.loadThumbnails(gridPane, collection.getPhotos());
        }
    }
}