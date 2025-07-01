package com.app.photon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhotoCollection {
    private final String name;
    private final List<Photo> photos;

    public PhotoCollection(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Photo> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    public void addPhoto(Photo photo) {
        if (!photos.contains(photo)) {
            photos.add(photo);
        }
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public boolean contains(Photo photo) {
        return photos.contains(photo);
    }

    @Override
    public String toString() {
        return name + " (" + photos.size() + " photos)";
    }
}
