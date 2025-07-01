# Photon - JavaFX Photo Organizer

**Photon** is a lightweight, locally-run Java application that helps you organize and view your photos in a clean, Instagram-like grid layout. Built using JavaFX, SQLite, and Gson, it lets you import, sort, and manage photos — all stored locally on your system.

## Table of Contents

- [Features](#-features)
- [Technologies Used](#-technologies-used)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Usage](#-usage)
- [Project Structure](#-project-structure)
- [Configuration](#-configuration)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)

## ✨ Features

- ✅ **Import Photos**: Import multiple images from your file system
- 🖼️ **Grid Display**: Display photos as thumbnails in a responsive 4-column grid
- 💾 **Local Storage**: Store imported photos in a local `photos/` directory
- 📁 **Album Ready**: Prepared infrastructure for collection/album support
- 🛠️ **Clean Code**: Built with modular, maintainable Java architecture
- 🔒 **Privacy First**: All data stored locally - no cloud dependency

## 🧪 Technologies Used

- **Java 17+** - Core programming language
- **JavaFX** - Modern UI framework
- **SQLite** - Lightweight database for metadata persistence
- **Gson** - JSON serialization/deserialization
- **JUnit 5** - Testing framework
- **Maven** - Build and dependency management

## 📋 Prerequisites

Before running Photon, ensure you have the following installed:

- **Java Development Kit (JDK) 17 or higher**
  - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use [OpenJDK](https://openjdk.org/)
  - Verify installation: `java --version`

- **Apache Maven 3.6+**
  - Download from [Maven website](https://maven.apache.org/download.cgi)
  - Verify installation: `mvn --version`

- **JavaFX SDK** (if not bundled with your JDK)
  - Download from [OpenJFX website](https://openjfx.io/openjfx/install/)

## 🚀 Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Hydra-Slayer/Photon.git
   cd Photon
   ```

2. **Build the Project**
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   mvn exec:java
   ```

## 📖 Usage

### Basic Workflow

1. **Launch the Application**
   ```bash
   mvn exec:java
   ```
   JavaFX opens a clean, modern window interface.

2. **Import Photos**
   - Click the "Import Photos" button
   - Select one or more image files from your computer
   - Supported formats: JPG, JPEG, PNG, GIF, BMP

3. **View Your Collection**
   - Photos are automatically copied to the local `photos/` folder
   - Thumbnails appear in a responsive 4-column grid
   - Click on any thumbnail for a larger preview

### Supported Image Formats

- JPEG/JPG
- PNG
- GIF
- BMP
- TIFF (planned)

## 📂 Project Structure

```
Photon/
├── pom.xml                    # Maven configuration
├── photos/                    # Auto-created photo storage directory
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/app/photon/
│   │           └── App.java   # Main application entry point
│   └── test/
│       └── java/com/app/photon/
│           └── AppTest.java   # Unit tests
├── .gitignore
└── README.md
```

## ⚙️ Configuration

### Application Settings

Currently, Photon uses default settings. Future versions will include:
- Custom photo storage locations
- Grid layout preferences (2, 3, 4, or 6 columns)
- Theme selection (light/dark mode)
- Import quality settings

### System Requirements

- **OS**: Windows 10+, macOS 10.14+, or Linux with desktop environment
- **RAM**: 512MB minimum, 1GB recommended
- **Storage**: 50MB for application + space for your photos
- **Display**: 1024x768 minimum resolution

## 🛠️ Troubleshooting

### Common Issues

**Issue**: Application won't start
```bash
Error: JavaFX runtime components are missing
```
**Solution**: Install JavaFX SDK or use a JDK that includes JavaFX

**Issue**: Photos not importing
**Solution**: Check file permissions and ensure photos directory is writable

**Issue**: Thumbnails not displaying
**Solution**: Verify image file integrity and supported formats

### Getting Help

2. Create a new issue with:
   - Your operating system
   - Java version (`java --version`)
   - Steps to reproduce the problem
   - Error messages (if any)

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### Getting Started

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes**
4. **Add tests** for new functionality
5. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
6. **Push to your branch**
   ```bash
   git push origin feature/amazing-feature
   ```
7. **Open a Pull Request**

### Development Setup

```bash
# Clone your fork
git clone https://github.com/yourusername/Photon.git
cd Photon

# Install dependencies
mvn clean install

# Run tests
mvn test

# Run the application in development mode
mvn exec:java
```

### Code Guidelines

- Follow Java naming conventions
- Write meaningful commit messages
- Add JavaDoc comments for public methods

## 🙏 Acknowledgments

- Thanks to the JavaFX community for excellent documentation
- Inspired by modern photo management applications
- Built with love for photographers and digital organization enthusiasts

---

**Made with ❤️ by [Hydra-Slayer](https://github.com/Hydra-Slayer)**

*Star ⭐ this repository if you find Photon useful!*
