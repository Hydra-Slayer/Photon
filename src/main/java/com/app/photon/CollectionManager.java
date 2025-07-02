package com.app.photon;

import java.util.*;

public class CollectionManager {
    private final Map<String, PhotoCollection> collections;
    private final PhotoCollection allPhotosCollection;

    public CollectionManager() {
        this.collections = new HashMap<>();
        this.allPhotosCollection = new PhotoCollection("All Photos");
        collections.put(allPhotosCollection.getName(), allPhotosCollection);
    }

    public PhotoCollection getAllPhotosCollection() {
        return allPhotosCollection;
    }

    public void addCollection(String name) {
        if (!collections.containsKey(name)) {
            collections.put(name, new PhotoCollection(name));
        }
    }

    public void removeCollection(String name) {
        if (!name.equals(allPhotosCollection.getName())) {
            collections.remove(name);
        }
    }

    public PhotoCollection getCollection(String name) {
        return collections.get(name);
    }

    public Collection<PhotoCollection> getAllCollections() {
        return Collections.unmodifiableCollection(collections.values());
    }

    public void addPhotoToCollection(Photo photo, String collectionName) {
        PhotoCollection collection = collections.get(collectionName);
        if (collection != null) {
            collection.addPhoto(photo);
            allPhotosCollection.addPhoto(photo);
        }
    }

    public void removePhotoFromCollection(Photo photo, String collectionName) {
        PhotoCollection collection = collections.get(collectionName);
        if (collection != null) {
            collection.removePhoto(photo);
        }
    }
}