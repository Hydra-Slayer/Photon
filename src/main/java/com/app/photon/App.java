package com.app.photon;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class App extends Application {

    private static final String PHOTO_DIR = "photos";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create the "Import Photos" button
        Button importButton = new Button("Import Photos");
        importButton.setOnAction(e -> handleImport());

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20");
        root.getChildren().add(importButton);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Photon - Photo Organizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos to Import");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            File photoDir = new File(PHOTO_DIR);
            if (!photoDir.exists()) {
                photoDir.mkdirs();
            }

            for (File file : selectedFiles) {
                try {
                    Path destination = Path.of(PHOTO_DIR, file.getName());
                    Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Imported: " + file.getName());
                } catch (IOException ex) {
                    System.err.println("Failed to copy: " + file.getName());
                    ex.printStackTrace();
                }
            }
        }
    }
}
