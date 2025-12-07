# 🏨 Hotel Management System (JavaFX + CSV)

Welcome to the Hotel Management System project! This application manages clients, rooms, reservations, and billing without using an external database. We use JavaFX for the interface and CSV files to save data.

# 🛠️ 1. Setup & Installation

## Step A: Install Java (JDK 21)

Everyone must have Java 21 installed.

Download OpenJDK 21 (LTS) from Adoptium.net.

Run the installer.

Windows Users: Make sure to check the box "Add to PATH" during installation.

Verify by opening a terminal and typing: java -version.

## Step B: Gradle (You don't need to install it!)

We are using the Gradle Wrapper. This means the specific version of Gradle we need is included inside the project folder.

Windows: Use the file gradlew.bat

Mac/Linux: Use the file gradlew

# 🚀 2. How to Run the App

Open your terminal (VS Code, IntelliJ, or Command Prompt) inside the project folder.

On Windows:

```
.\gradlew.bat run
```

On Mac/Lunix:

```
# First run only: give permission
chmod +x gradlew

# Run the app
./gradlew run
```

Note: The first time you run this, it will take a few minutes to download dependencies.

# 📂 3. Project Structure (Where do I work?)

Here is the map of the project. Please only edit the files assigned to your role.

````
hotelFx/
├── data/                            <-- CSV Files (Database) appear here. DO NOT EDIT.
├── build.gradle                     <-- Project Configuration. DO NOT TOUCH.
└── src/main/java/com/hotel/project/
    ├── Main.java                    <-- App Entry Point
    │
    ├── backend/                     <-- ⚙️ BACKEND TEAM WORKS HERE
    │   ├── CsvService.java          (Engine: Saves data to files)
    │   ├── Serialisable.java        (Interface: Objects -> CSV)
    │   │
    │   ├── models/                  (Data Objects)
    │   │   ├── Client.java
    │   │   ├── Chambre.java
    │   │   ├── Reservation.java
    │   │   └── Compte.java
    │   │
    │   └── services/                (The Logic - CRUD Operations)
    │       ├── AuthService.java     (Login Logic)
    │       ├── RoomService.java     (Add/Remove Rooms)
    │       ├── BookingService.java  (Reservations)
    │       └── BillingService.java  (Payments)
    │
    └── ui/                          <-- 🎨 FRONTEND TEAM WORKS HERE
        ├── ScreenManager.java       (Navigation)
        └── screens/                 (Visual Pages)
            ├── LoginScreen.java
            ├── DashboardScreen.java
            ├── RoomScreen.java
            └── BookingScreen.java
````


