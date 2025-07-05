package com.app.photon;

public class CollectionDisplayItem {
    private final String name;
    private final String coverImagePath;

    public CollectionDisplayItem(String name, String coverImagePath) {
        this.name = name;
        this.coverImagePath = coverImagePath;
    }

    public String getName() {
        return name;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }
}