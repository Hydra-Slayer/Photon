package com.app.photon;

import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class PhotoImporter {
    private final String photoDir;

    public PhotoImporter(String photoDir) {
        this.photoDir = photoDir;
    }

    public List<File> importPhotosWithReturn() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos to Import");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            File dir = new File(photoDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            for (File file : selectedFiles) {
                try {
                    Path destination = Path.of(photoDir, file.getName());
                    Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return selectedFiles;
    }

    public void importPhotos() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos to Import");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            File dir = new File(photoDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            for (File file : selectedFiles) {
                try {
                    Path destination = Path.of(photoDir, file.getName());
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