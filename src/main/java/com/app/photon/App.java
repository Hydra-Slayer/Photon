package com.app.photon;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class App extends Application {

    private static final String PHOTO_DIR = "photos";
    private GridPane gridPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button importButton = new Button("Import Photos");
        importButton.setOnAction(e -> {
            handleImport();
            loadThumbnails(); // Refresh thumbnails after import
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

        loadThumbnails(); // Load existing thumbnails on startup
    }

    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos to Import");
        fileChooser.getExtensionFilters().add(
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

    private void loadThumbnails() {
        gridPane.getChildren().clear(); // Clear previous thumbnails
        File folder = new File(PHOTO_DIR);
        if (!folder.exists())
            return;

        File[] imageFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") ||
                name.toLowerCase().endsWith(".jpeg") ||
                name.toLowerCase().endsWith(".png") ||
                name.toLowerCase().endsWith(".gif"));

        if (imageFiles == null)
            return;

        int column = 0;
        int row = 0;

        for (File imageFile : imageFiles) {
            try {
                Image image = new Image(new FileInputStream(imageFile), 150, 150, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);

                gridPane.add(imageView, column, row);

                column++;
                if (column == 4) {
                    column = 0;
                    row++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
