package com.app.photon;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {

    private static final String PHOTO_DIR = "photos";
    private GridPane gridPane;
    private PhotoImporter photoImporter;
    private ThumbnailLoader thumbnailLoader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        photoImporter = new PhotoImporter(PHOTO_DIR);
        thumbnailLoader = new ThumbnailLoader(PHOTO_DIR);

        Button importButton = new Button("Import Photos");
        importButton.setOnAction(e -> {
            photoImporter.importPhotos();
            thumbnailLoader.loadThumbnails(gridPane);
        });

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        VBox root = new VBox(10, importButton, gridPane);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Photon - Photo Organizer");
        primaryStage.setScene(scene);
        primaryStage.show();

        thumbnailLoader.loadThumbnails(gridPane);
    }
}