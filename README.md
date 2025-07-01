# 📷 Photon - JavaFX Photo Organizer

**Photon** is a lightweight, locally-run Java application that helps you organize and view your photos in a clean, Instagram-like grid layout. Built using JavaFX, SQLite, and Gson, it lets you import, sort, and manage photos — all stored locally on your system.

---

## ✨ Features

- ✅ Import multiple images from your file system  
- 🖼️ Display photos as thumbnails in a responsive grid  
- 💾 Store imported photos in a local `photos/` directory  
- 📁 Prepared for collection/album support  
- 🛠️ Built with modular, maintainable Java code

---

## 📂 Project Structure

```
Photon/
├── pom.xml
├── photos/ # Imported photo storage (auto-created)
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/app/photon/
│ │ │ └── App.java
│ └── test/
│ └── java/com/app/photon/
│ └── AppTest.java
├── .gitignore
└── README.md
```


## 🧪 Technologies Used

- **Java 17+**
- **JavaFX** (UI)
- **SQLite** (for future metadata persistence)
- **Gson** (for optional JSON storage)
- **JUnit 5** (testing)
- **Maven** (build tool)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/Photon.git
cd Photon
```
2. Build the Project
```bash
mvn clean install
```
3. Run the App
```bash

mvn exec:java
```

🖼️ How It Works
Launch the app → JavaFX opens a window

Click "Import Photos" → Select one or more images from your computer

Photos are copied to a local photos/ folder

Thumbnails appear in a 4-column grid for easy viewing

📌 Upcoming Features
📂 Create and manage photo collections (albums)

🏷️ Add tags or descriptions to photos

🔍 Sort and filter by name, date, or collection

🖼️ Full-screen photo viewer

🗑️ Delete or move photos
