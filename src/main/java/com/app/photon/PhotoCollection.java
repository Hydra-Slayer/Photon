package com.app.photon;

import java.util.*;

public class PhotoCollection {
    private final String name;
    private final Set<String> photoFileNames;

    public PhotoCollection(String name) {
        this.name = name;
        this.photoFileNames = new LinkedHashSet<>();
    }

    public String getName() {
        return name;
    }

    public Set<String> getPhotoFileNames() {
        return Collections.unmodifiableSet(photoFileNames);
    }

    public void addPhoto(String fileName) {
        photoFileNames.add(fileName);
    }

    public void removePhoto(String fileName) {
        photoFileNames.remove(fileName);
    }

    public boolean contains(String fileName) {
        return photoFileNames.contains(fileName);
    }
}