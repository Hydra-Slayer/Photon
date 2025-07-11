package com.app.photon;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;

import java.util.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class App extends Application {

    private static final String ALL_PHOTOS_DIR = "all_media";
    private static final String COLLECTIONS_DIR = "collections";
    private GridPane gridPane;
    private CollectionManager collectionManager;

    private Set<Photo> selectedPhotos = new HashSet<>();
    private List<String> allPhotoFiles;
    private PhotoImporter photoImporter;
    private String currentCollectionName = null;

    private Button backButton;
    private Button setCoverButton;
    private Button addSelectedToCollectionButton;
    private Button removeSelectedFromCollectionButton;
    private HBox collectionControls;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        collectionManager = new CollectionManager(new File(COLLECTIONS_DIR));
        allPhotoFiles = loadAllPhotoFiles();

        // Import button (optional, if you want to import new photos)
        photoImporter = new PhotoImporter(ALL_PHOTOS_DIR);
        Button importButton = new Button("Import Files");

        importButton.setOnAction(e -> {
            List<File> importedFiles = photoImporter.importPhotosWithReturn();
            if (importedFiles != null && !importedFiles.isEmpty()) {
                allPhotoFiles = loadAllPhotoFiles();

                // --- Ensure All Media collection exists and is updated ---
                PhotoCollection allMediaCollection = collectionManager.loadCollection("All Media");
                for (File file : importedFiles) {
                    allMediaCollection.addPhoto(file.getName());
                }
                collectionManager.saveCollection(allMediaCollection);

                // Refresh the collections grid after import
                showCollectionsGrid();
            }
        });

        // Add Collection button
        Button addCollectionButton = new Button("Add Collection");
        addCollectionButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Collection");
            dialog.setHeaderText("Create a new photo collection");
            dialog.setContentText("Collection name:");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.isBlank() && !collectionManager.getAllCollectionNames().contains(name)) {
                    // Create empty collection
                    collectionManager.saveCollection(new PhotoCollection(name));
                    // No ComboBox to update
                }
            });
        });

        // Add to Collection button
        Button addSelectedToCollectionButton = new Button("Add Selected to Collection");
        addSelectedToCollectionButton.setOnAction(e -> {
            if (selectedPhotos.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No photos selected.");
                alert.showAndWait();
                return;
            }
            // Prompt for target collection
            List<String> availableCollections = new ArrayList<>(collectionManager.getAllCollectionNames());
            ChoiceDialog<String> dialog = new ChoiceDialog<>(
                    availableCollections.isEmpty() ? null : availableCollections.get(0), availableCollections);
            dialog.setTitle("Select Collection");
            dialog.setHeaderText("Add selected photos to which collection?");
            dialog.showAndWait().ifPresent(targetCollection -> {
                for (Photo photo : selectedPhotos) {
                    collectionManager.addPhotoToCollection(photo.getFileName(), targetCollection);
                }
                selectedPhotos.clear();
            });
        });

        // back button
        backButton = new Button("Back to Collections");
        backButton.setVisible(false); // Hidden by default
        backButton.setOnAction(e -> showCollectionsGrid());

        // set cover button
        Button setCoverButton = new Button("Set Cover");
        setCoverButton.setOnAction(e -> {
            if (currentCollectionName == null || "All Media".equals(currentCollectionName)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Select a user collection first.");
                alert.showAndWait();
                return;
            }
            if (selectedPhotos.size() != 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Select exactly one photo to set as cover.");
                alert.showAndWait();
                return;
            }
            Photo selected = selectedPhotos.iterator().next();
            // Copy the selected photo as cover.jpg (or .png) in the collection folder
            File collectionDir = new File(COLLECTIONS_DIR, currentCollectionName);
            String ext = "";
            String fileName = selected.getFileName().toLowerCase();
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
                ext = "jpg";
            else if (fileName.endsWith(".png"))
                ext = "png";
            else if (fileName.endsWith(".gif"))
                ext = "gif";
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Only image files can be used as cover.");
                alert.showAndWait();
                return;
            }
            File coverFile = new File(collectionDir, "cover." + ext);
            try {
                java.nio.file.Files.copy(
                        selected.getFilePath(),
                        coverFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                showCollectionsGrid(); // Refresh grid to show new cover
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cover set successfully!");
                alert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to set cover.");
                alert.showAndWait();
            }
        });

        // remove photo from collection button
        Button removeSelectedFromCollectionButton = new Button("Remove Selected from Collection");
        removeSelectedFromCollectionButton.setOnAction(e -> {
            if (currentCollectionName == null || "All Media".equals(currentCollectionName)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cannot remove photos from All Media collection.");
                alert.showAndWait();
                return;
            }
            if (selectedPhotos.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No photos selected.");
                alert.showAndWait();
                return;
            }
            for (Photo photo : selectedPhotos) {
                collectionManager.removePhotoFromCollection(photo.getFileName(), currentCollectionName);
            }
            selectedPhotos.clear();
            showCollection(currentCollectionName);
        });

        Button deleteCollectionButton = new Button("Delete Collection");
        deleteCollectionButton.setOnAction(e -> {
            List<String> collections = new ArrayList<>(collectionManager.getAllCollectionNames());
            collections.remove("All Media");
            if (collections.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No user collections to delete.");
                alert.showAndWait();
                return;
            }
            ChoiceDialog<String> dialog = new ChoiceDialog<>(collections.get(0), collections);
            dialog.setTitle("Delete Collection");
            dialog.setHeaderText("Select a collection to delete:");
            dialog.setContentText("Collection:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(collectionToDelete -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete the collection \"" + collectionToDelete + "\"?",
                        ButtonType.YES, ButtonType.NO);
                confirm.setTitle("Confirm Deletion");
                Optional<ButtonType> confirmation = confirm.showAndWait();
                if (confirmation.isPresent() && confirmation.get() == ButtonType.YES) {
                    // Delete the collection folder
                    File collectionDir = new File(COLLECTIONS_DIR, collectionToDelete);
                    deleteDirectoryRecursively(collectionDir);
                    showCollectionsGrid();
                }
            });
        });
        Button deletePhotoEverywhereButton = new Button("Delete Photo Everywhere");
        deletePhotoEverywhereButton.setOnAction(e -> {
            if (selectedPhotos.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No photos selected.");
                alert.showAndWait();
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete selected photo(s) from disk and all collections?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirm Deletion");
            Optional<ButtonType> confirmation = confirm.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.YES) {
                for (Photo photo : selectedPhotos) {
                    collectionManager.deletePhotoEverywhere(photo.getFileName(), ALL_PHOTOS_DIR);
                }
                selectedPhotos.clear();
                showCollection(currentCollectionName);
            }
        });

        // UI: Grid setup
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        Button toggleDarkModeButton = new Button("Toggle Dark Mode");
        // UI: button setup
        HBox alwaysVisibleControls = new HBox(10, importButton, addCollectionButton, deleteCollectionButton,
                toggleDarkModeButton);
        collectionControls = new HBox(10, addSelectedToCollectionButton, removeSelectedFromCollectionButton,
                setCoverButton, deletePhotoEverywhereButton);
        collectionControls.setVisible(false); // Hide by default
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox root = new VBox(10, alwaysVisibleControls, collectionControls, backButton, scrollPane);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Photon - Photo Organizer");
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/styles/light.css").toExternalForm());
        primaryStage.show();

        // dark mode toggle logic
        final boolean[] darkMode = { false }; // Use array for mutability in lambda
        toggleDarkModeButton.setOnAction(e -> {
            darkMode[0] = !darkMode[0];
            scene.getStylesheets().clear();
            if (darkMode[0]) {
                scene.getStylesheets().add(getClass().getResource("/styles/dark.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/styles/light.css").toExternalForm());
            }
        });

        showCollectionsGrid();
    }

    private String truncateFileName(String name, int maxLength) {
        if (name.length() <= maxLength)
            return name;
        int dot = name.lastIndexOf('.');
        String ext = (dot != -1) ? name.substring(dot) : "";
        return name.substring(0, Math.max(0, maxLength - ext.length() - 3)) + "..." + ext;
    }

    private void deleteDirectoryRecursively(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectoryRecursively(file);
                }
            }
        }
        if (!dir.delete()) {
            System.err.println("Failed to delete: " + dir.getAbsolutePath());
        }
    }

    private List<String> loadAllPhotoFiles() {
        File dir = new File(ALL_PHOTOS_DIR);
        if (!dir.exists())
            dir.mkdirs();
        String[] files = dir.list((d, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|mp4|mov|avi|mkv)$"));
        return files == null ? new ArrayList<>() : Arrays.asList(files);
    }

    private void showCollectionsGrid() {
        CollectionViewHelper.showCollectionsGrid(
                gridPane,
                collectionManager,
                this::showCollection,
                selectedPhotos);
    }

    private void showCollection(String collectionName) {
        if (collectionName == null) {
            // Optionally show a message or default to "All Media"
            collectionName = "All Media";
        }
        CollectionViewHelper.showCollection(
                collectionName,
                gridPane,
                collectionManager,
                ALL_PHOTOS_DIR,
                selectedPhotos,
                collectionControls,
                backButton,
                fileName -> truncateFileName(fileName, 18));
    }
}