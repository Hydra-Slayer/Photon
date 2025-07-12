package com.app.photon;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.*;
import java.util.function.Consumer;

public class CollectionViewHelper {

    public static void showMediaInOriginalResolution(File file) {
        Stage stage = new Stage();
        stage.setTitle(file.getName());

        // Get screen bounds
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double maxWidth = screenBounds.getWidth() * 0.9;
        double maxHeight = screenBounds.getHeight() * 0.9;

        if (file.getName().toLowerCase().matches(".*\\.(mp4|mov|avi|mkv)$")) {
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            // Play/Pause button
            Button playPauseButton = new Button("Pause");
            playPauseButton.setOnAction(ev -> {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    playPauseButton.setText("Play");
                } else {
                    mediaPlayer.play();
                    playPauseButton.setText("Pause");
                }
            });

            // Seek slider
            Slider seekSlider = new Slider();
            seekSlider.setMin(0);
            seekSlider.setMax(100);
            seekSlider.setValue(0);

            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (mediaPlayer.getTotalDuration().toMillis() > 0) {
                    seekSlider.setValue(newTime.toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100);
                }
            });

            seekSlider.setOnMousePressed(ev -> mediaPlayer.pause());
            seekSlider.setOnMouseReleased(ev -> {
                double percent = seekSlider.getValue() / 100.0;
                mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(percent));
                mediaPlayer.play();
            });

            // Volume slider
            Slider volumeSlider = new Slider(0, 1, mediaPlayer.getVolume());
            volumeSlider.setPrefWidth(100);
            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                mediaPlayer.setVolume(newVal.doubleValue());
            });

            // Mute button
            Button muteButton = new Button("Mute");
            muteButton.setOnAction(ev -> {
                boolean muted = mediaPlayer.isMute();
                mediaPlayer.setMute(!muted);
                muteButton.setText(mediaPlayer.isMute() ? "Unmute" : "Mute");
            });

            HBox controls = new HBox(10, playPauseButton, seekSlider, new Label("Volume:"), volumeSlider, muteButton);
            controls.setPadding(new Insets(10));
            controls.setAlignment(javafx.geometry.Pos.CENTER);

            VBox root = new VBox(10, mediaView, controls);
            root.setPadding(new Insets(10));
            root.setAlignment(javafx.geometry.Pos.CENTER);

            mediaPlayer.setOnReady(() -> {
                double width = media.getWidth();
                double height = media.getHeight();
                double fitWidth = Math.min(width, maxWidth);
                double fitHeight = Math.min(height, maxHeight);
                mediaView.setFitWidth(fitWidth);
                mediaView.setFitHeight(fitHeight);
                stage.setWidth(fitWidth + 80);
                stage.setHeight(fitHeight + 160);
                mediaPlayer.play();
            });

            stage.setOnCloseRequest(e -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            });

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            // Image or GIF
            try (FileInputStream fis = new FileInputStream(file)) {
                Image image = new Image(fis);
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);

                // Limit to screen size
                double fitWidth = Math.min(image.getWidth(), maxWidth);
                double fitHeight = Math.min(image.getHeight(), maxHeight);
                imageView.setFitWidth(fitWidth);
                imageView.setFitHeight(fitHeight);

                ScrollPane scrollPane = new ScrollPane(imageView);
                scrollPane.setPannable(true);

                stage.setWidth(fitWidth + 40);
                stage.setHeight(fitHeight + 80);

                Scene scene = new Scene(scrollPane);
                stage.setScene(scene);
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // For loading thumbnails from a list of Photo objects
    public static void loadThumbnails(GridPane gridPane, List<Photo> photos) {
        gridPane.getChildren().clear();
        int column = 0, row = 0;
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

    // For loading collection thumbnails
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

    // For showing all collections grid
    public static void showCollectionsGrid(
            GridPane gridPane,
            CollectionManager collectionManager,
            Consumer<String> onCollectionClick,
            Set<Photo> selectedPhotos) {
        selectedPhotos.clear();
        List<CollectionDisplayItem> items = collectionManager.getCollectionDisplayItems();
        loadCollectionThumbnails(gridPane, items, onCollectionClick);
    }

    // For showing a single collection's contents
    public static void showCollection(
            String collectionName,
            GridPane gridPane,
            CollectionManager collectionManager,
            String allPhotosDir,
            Set<Photo> selectedPhotos,
            HBox collectionControls,
            Button backButton,
            java.util.function.Function<String, String> truncateFileName) {
        gridPane.getChildren().clear();
        backButton.setVisible(true);
        collectionControls.setVisible(true);
        List<String> filesToShow;
        if ("All Media".equals(collectionName)) {
            PhotoCollection allMediaCollection = collectionManager.loadCollection("All Media");
            filesToShow = new ArrayList<>(allMediaCollection.getPhotoFileNames());
        } else {
            PhotoCollection collection = collectionManager.loadCollection(collectionName);
            filesToShow = new ArrayList<>(collection.getPhotoFileNames());
        }
        int column = 0, row = 0;
        for (String fileName : filesToShow) {
            try {
                File file = new File(allPhotosDir, fileName);
                Photo photo = new Photo(file.toPath());
                StackPane pane;

                if (fileName.toLowerCase().matches(".*\\.(mp4|mov|avi|mkv)$")) {
                    Media media = new Media(file.toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);

                    MediaView mediaView = new MediaView(mediaPlayer);
                    mediaView.setFitWidth(150);
                    mediaView.setFitHeight(150);
                    mediaView.setPreserveRatio(true);

                    mediaPlayer.setOnReady(() -> {
                        mediaPlayer.seek(javafx.util.Duration.seconds(0.1));
                        mediaPlayer.pause();
                    });

                    pane = new StackPane(mediaView);
                } else {
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
                    if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) { // Right-click
                        showMediaInOriginalResolution(file);
                        return;
                    }
                    if (selectedPhotos.contains(photo)) {
                        selectedPhotos.remove(photo);
                        pane.setStyle("");
                    } else {
                        selectedPhotos.add(photo);
                        pane.setStyle("-fx-border-color: blue; -fx-border-width: 2;");
                    }
                });

                String displayName = truncateFileName.apply(fileName);
                Label nameLabel = new Label(displayName);
                nameLabel.setMaxWidth(150);
                nameLabel.setStyle("-fx-font-size: 11; -fx-text-alignment: center;");
                nameLabel.setWrapText(false);

                VBox vbox = new VBox(2, pane, nameLabel);
                vbox.setAlignment(javafx.geometry.Pos.CENTER);

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