package com.app.photon;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ThumbnailLoader {
    private final String photoDir;

    public ThumbnailLoader(String photoDir) {
        this.photoDir = photoDir;
    }

    public void loadThumbnails(GridPane gridPane) {
        gridPane.getChildren().clear();
        File folder = new File(photoDir);
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