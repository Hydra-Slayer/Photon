package com.app.photon;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class App extends Application {

    private static final String ALL_PHOTOS_DIR = "all_photos";
    private static final String COLLECTIONS_DIR = "collections";
    private GridPane gridPane;
    private CollectionManager collectionManager;
    private ComboBox<String> collectionComboBox;
    private Set<Photo> selectedPhotos = new HashSet<>();
    private List<String> allPhotoFiles;
    private PhotoImporter photoImporter;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        collectionManager = new CollectionManager(new File(COLLECTIONS_DIR));
        allPhotoFiles = loadAllPhotoFiles();

        // UI: Collection selector
        collectionComboBox = new ComboBox<>();
        collectionComboBox.getItems().add("All Photos");
        collectionComboBox.getItems().addAll(collectionManager.getAllCollectionNames());
        collectionComboBox.setValue("All Photos");
        collectionComboBox.setOnAction(e -> {
            selectedPhotos.clear();
            showCollection(collectionComboBox.getValue());
        });

        // Import button (optional, if you want to import new photos)
        photoImporter = new PhotoImporter(ALL_PHOTOS_DIR);
        Button importButton = new Button("Import Files");

        importButton.setOnAction(e -> {
            List<File> importedFiles = photoImporter.importPhotosWithReturn();
            if (importedFiles != null && !importedFiles.isEmpty()) {
                // Update the allPhotoFiles list
                allPhotoFiles = loadAllPhotoFiles();
                // Refresh the current view if "All Photos" is selected
                if ("All Photos".equals(collectionComboBox.getValue())) {
                    showCollection("All Photos");
                }
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
                    collectionComboBox.getItems().add(name);
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
            availableCollections.remove(collectionComboBox.getValue());
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

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        HBox controls = new HBox(10, importButton, addSelectedToCollectionButton, addCollectionButton,
                collectionComboBox);

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox root = new VBox(10, controls, scrollPane);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Photon - Photo Organizer");
        primaryStage.setScene(scene);
        primaryStage.show();

        showCollection("All Photos");
    }

    private List<String> loadAllPhotoFiles() {
        File dir = new File(ALL_PHOTOS_DIR);
        if (!dir.exists())
            dir.mkdirs();
        String[] files = dir.list((d, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|mp4|mov|avi|mkv)$"));
        return files == null ? new ArrayList<>() : Arrays.asList(files);
    }

    // Shows thumbnails for the selected collection
    private void showCollection(String collectionName) {
        gridPane.getChildren().clear();
        List<String> filesToShow;
        if ("All Photos".equals(collectionName)) {
            filesToShow = allPhotoFiles;
        } else {
            PhotoCollection collection = collectionManager.loadCollection(collectionName);
            filesToShow = new ArrayList<>(collection.getPhotoFileNames());
        }
        int column = 0, row = 0;
        for (String fileName : filesToShow) {
            try {
                File file = new File(ALL_PHOTOS_DIR, fileName);
                Photo photo = new Photo(file.toPath());
                StackPane pane;

                if (fileName.toLowerCase().matches(".*\\.(mp4|mov|avi|mkv)$")) {
                    // Video: show MediaView (first frame as preview)
                    Media media = new Media(file.toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    MediaView mediaView = new MediaView(mediaPlayer);
                    mediaView.setFitWidth(150);
                    mediaView.setFitHeight(150);
                    mediaView.setPreserveRatio(true);

                    // Play and pause immediately to show the first frame as a thumbnail
                    mediaPlayer.setOnReady(() -> {
                        mediaPlayer.seek(javafx.util.Duration.seconds(0.1));
                        mediaPlayer.pause();
                    });

                    pane = new StackPane(mediaView);
                } else {
                    // Image: show ImageView
                    ImageView imageView = new ImageView(
                            new Image(new FileInputStream(photo.getFilePath().toFile()), 150, 150, true, true));
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(150);
                    imageView.setFitHeight(150);
                    pane = new StackPane(imageView);
                }

                if (selectedPhotos.contains(photo)) {
                    pane.setStyle("-fx-border-color: blue; -fx-border-width: 2;");
                } else {
                    pane.setStyle("");
                }

                pane.setOnMouseClicked(e -> {
                    if (selectedPhotos.contains(photo)) {
                        selectedPhotos.remove(photo);
                        pane.setStyle("");
                    } else {
                        selectedPhotos.add(photo);
                        pane.setStyle("-fx-border-color: blue; -fx-border-width: 2;");
                    }
                });

                gridPane.add(pane, column, row);
                column++;
                if (column == 4) {
                    column = 0;
                    row++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}