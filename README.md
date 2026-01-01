# 🏨 Hotel Management System (JavaFX + CSV)

Welcome to the Hotel Management System project! This application manages clients, rooms, reservations, and billing without using an external database. We use JavaFX for the interface and CSV files to save data.

# 🛠️ 1. Setup & Installation

## Step A: Install Java (JDK 21)

Everyone must have Java 21 installed.

Download OpenJDK 21 (LTS) from Adoptium.net.

Run the installer.

Windows Users: Make sure to check the box "Add to PATH" during installation.

Verify by opening a terminal and typing: java -version.

## Step B: Setup Database (Using XAMPP)

Instead of installing a complex server, we will use XAMPP.

1.  **Download & Install XAMPP** (if you don't have it).
2.  Open **XAMPP Control Panel**.
3.  Click **Start** next to **Apache** and **MySQL**.
4.  Click the **Admin** button next to MySQL. This opens **phpMyAdmin** in your browser.
5.  Click the **SQL** tab at the top.
6.  **Copy and Paste** this code into the box and click **Go**:

```sql
CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE rooms (
    number INT PRIMARY KEY,
    type VARCHAR(50),
    price DOUBLE,
    is_available BOOLEAN DEFAULT TRUE
);

CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20)
);

CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT,
    room_number INT,
    status VARCHAR(20),
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (room_number) REFERENCES rooms(number)
);

-- Default Admin Account
INSERT INTO users (username, password, role) VALUES ('admin', 'admin', 'MANAGER');
```

## Step C: Gradle (You don't need to install it!)

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

```
hotelFx/
├── data/                            <-- CSV Files (Database) appear here. DO NOT EDIT.
├── build.gradle                     <-- Project Configuration. DO NOT TOUCH.
└── src/main/java/com/hotel/project/
    ├── Main.java                    <-- App Entry Point
    │
    ├── backend/                     <-- ⚙️ BACKEND TEAM WORKS HERE
    │   ├── CsvService.java          (Engine: Saves data to files)
    │   ├── Serialisable.java        (Interface: Objects -> CSV)
    │   │── DatabaseConnection.java  <-- Setup DB Password here
    │   ├── DatabaseHelper.java      <-- The Engine (Don't touch!)
    │   ├── RowMapper.java           <-- Interface for Mapping
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
    └── frontend/                          <-- 🎨 FRONTEND TEAM WORKS HERE
        ├── ScreenManager.java       (Navigation)
        └── screens/                 (Visual Pages)
            ├── LoginScreen.java
            ├── DashboardScreen.java
            ├── RoomScreen.java
            └── BookingScreen.java
```

# 👷 4. How to Write Code (The Important Part!)

We use a **`DatabaseHelper`** class to make database work easy. You **never** need to write `conn.prepareStatement` or `try-catch` blocks manually.

## The Rules:

1.  **Models (`backend/models/`)**: Simple classes with fields (e.g., `Client.java`).
2.  **Services (`backend/services/`)**: Where you write the logic using `DatabaseHelper`.

## Example: How to implement "Client Management"

**Step 1: The Model (`Client.java`)**
Create a simple Java class to hold your data.

```java
public class Client {
    public int id;
    public String name;

    public Client(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
```

**Step 2: The Service - WRITING Data (`INSERT / UPDATE`)**
DatabaseHelper.executeUpdate to save changes. You pass the SQL string and the values you want to fill in.

```java
// Inside ClientService.java (Example !!)
public static boolean addClient(String name) {
String sql = "INSERT INTO clients (first_name) VALUES (?)";

    // Just pass the SQL and the values. The Helper does the rest.
    return DatabaseHelper.executeUpdate(sql, name);

}

```

**Step 3: The Service - READING Data (`SELECT`)**
DatabaseHelper.executeQuery to get lists. You must provide a "Mapper" (the code inside the parentheses) to explain how to convert a database row into your Java Object.

```java
// Inside ClientService.java (Example !!)
public static List<Client> getAllClients() {
    String sql = "SELECT * FROM clients";
    // 'rs' is the database row. We map it to a new Client object.
    return DatabaseHelper.executeQuery(sql, rs -> new Client(
        rs.getInt("id"),
        rs.getString("first_name")
    ));
}
```
