package com.app.photon;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.List;

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
                Image image = new Image(new FileInputStream(photo.getFilePath().toFile()), 150, 150, true, true);
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