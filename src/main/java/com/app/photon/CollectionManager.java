package com.app.photon;

import java.io.*;
// import java.nio.file.*;
import java.util.*;

public class CollectionManager {
    private final File collectionsDir;

    public CollectionManager(File collectionsDir) {
        this.collectionsDir = collectionsDir;
        if (!collectionsDir.exists()) {
            collectionsDir.mkdirs();
        }
    }

    public List<String> getAllCollectionNames() {
        String[] names = collectionsDir.list((dir, name) -> new File(dir, name).isDirectory());
        return names == null ? new ArrayList<>() : Arrays.asList(names);
    }

    public PhotoCollection loadCollection(String name) {
        File listFile = new File(new File(collectionsDir, name), "list.txt");
        PhotoCollection collection = new PhotoCollection(name);
        if (listFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(listFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    collection.addPhoto(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return collection;
    }

    public void saveCollection(PhotoCollection collection) {
        File dir = new File(collectionsDir, collection.getName());
        if (!dir.exists())
            dir.mkdirs();
        File listFile = new File(dir, "list.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(listFile))) {
            for (String fileName : collection.getPhotoFileNames()) {
                writer.write(fileName);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPhotoToCollection(String fileName, String collectionName) {
        PhotoCollection collection = loadCollection(collectionName);
        collection.addPhoto(fileName);
        saveCollection(collection);
    }

    public void removePhotoFromCollection(String fileName, String collectionName) {
        PhotoCollection collection = loadCollection(collectionName);
        collection.removePhoto(fileName);
        saveCollection(collection);
    }

    public void deletePhotoEverywhere(String fileName, String allPhotosDir) {
        // Delete the actual file
        File file = new File(allPhotosDir, fileName);
        if (file.exists()) {
            file.delete();
        }

        // Remove from all collections
        for (String collectionName : getAllCollectionNames()) {
            PhotoCollection collection = loadCollection(collectionName);
            if (collection.contains(fileName)) {
                collection.removePhoto(fileName);
                saveCollection(collection);
            }
        }
    }

    public List<CollectionDisplayItem> getCollectionDisplayItems() {
        List<CollectionDisplayItem> items = new ArrayList<>();
        File[] dirs = collectionsDir.listFiles(File::isDirectory);
        if (dirs == null)
            return items;

        for (File dir : dirs) {
            String collectionName = dir.getName();
            File coverFile = null;
            for (String ext : List.of("jpg", "jpeg", "png", "gif")) {
                File f = new File(dir, "cover." + ext);
                if (f.exists()) {
                    coverFile = f;
                    break;
                }
            }
            String coverPath = (coverFile != null) ? coverFile.getAbsolutePath() : null;
            items.add(new CollectionDisplayItem(collectionName, coverPath));
        }
        return items;
    }
}