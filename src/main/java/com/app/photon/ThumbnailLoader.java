package com.app.photon;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import java.io.FileInputStream;
import java.util.List;
import java.util.function.Consumer;

public class ThumbnailLoader {
    private final String photoDir;

    public ThumbnailLoader(String photoDir) {
        this.photoDir = photoDir;
    }

    // New method to load thumbnails from a list of Photo objects
    public void loadThumbnails(GridPane gridPane, List<Photo> photos) {
        gridPane.getChildren().clear();
        int column = 0;
        int row = 0;
        for (Photo photo : photos) {
            try {
                Image image;
                try (FileInputStream fis = new FileInputStream(photo.getFilePath().toFile())) {
                    image = new Image(fis, 150, 150, true, true);
                }
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

    public static void loadCollectionThumbnails(
            GridPane gridPane,
            List<CollectionDisplayItem> items,
            Consumer<String> onCollectionClick) {
        gridPane.getChildren().clear();
        int column = 0, row = 0;
        for (CollectionDisplayItem item : items) {
            try {
                Image coverImage;
                if (item.getCoverImagePath() != null) {
                    try (FileInputStream fis = new FileInputStream(item.getCoverImagePath())) {
                        coverImage = new Image(fis, 150, 150, true, true);
                    }
                } else {
                    coverImage = new Image("https://via.placeholder.com/150?text=No+Cover", 150, 150, true, true);
                }
                ImageView imageView = new ImageView(coverImage);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);

                Label label = new Label(item.getName());
                VBox vbox = new VBox(5, imageView, label);
                vbox.setPadding(new Insets(5));
                vbox.getStyleClass().add("collection-thumbnail");
                vbox.setOnMouseClicked(e -> onCollectionClick.accept(item.getName()));

                gridPane.add(vbox, column, row);
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