package com.app.photon;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class Photo {
    private final Path filePath;
    private final String fileName;
    private final LocalDateTime dateAdded;

    public Photo(Path filePath) {
        this.filePath = filePath;
        this.fileName = filePath.getFileName().toString();
        this.dateAdded = LocalDateTime.now();
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    @Override
    public String toString() {
        return fileName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Photo other = (Photo) obj;
        return filePath.equals(other.filePath);
    }

    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
}