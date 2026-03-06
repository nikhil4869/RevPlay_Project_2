# RevPlay - Music Streaming Web Application

##  Application Overview
RevPlay is a full-stack monolithic music streaming web application built using Spring Boot and Oracle SQL. It allows users to discover, stream, and manage music while enabling artists to upload songs and analyze engagement metrics.

---

##  User Features (Listener)

- User Registration & Login
- Profile Management
- Browse & Search Songs, Artists, Albums
- Integrated Music Player (Play, Pause, Skip, Seek)
- Favorites Management
- Playlist Creation & Management
- Follow/Unfollow Public Playlists
- Listening History Tracking
- User Dashboard with Statistics

---

##  Artist Features

- Artist Profile Management
- Upload Songs & Albums
- Manage Song Visibility
- Analytics Dashboard
- Track Play Count & Favorites
- View Listening Trends
- View Top Listeners

---

## 🛠 Tech Stack

- Java 21
- Spring Boot
- Spring Security (JWT Authentication)
- Hibernate / JPA
- Oracle Database
- Maven
- Git
- JUnit4
- Log4J
- Thymeleaf

---

##  Architecture

- Monolithic Architecture
- MVC Pattern
- Role-Based Access Control (User / Artist)

---

##  My Role  
### User Experience & Engagement Engineer

- Implemented user profile & dashboard
- Built favorites management module
- Developed listening history system
- Implemented playlist follow/unfollow feature
- Maintained accurate user engagement statistics

---

##  How To Run The Project

1. Clone the repository
```bash
git clone https://github.com/nikhil4869/RevPlay_Project_2.git
cd RevPlay_Project_2
```

2. Configure Oracle Database

Update `application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

##  Running Backend

1. Navigate to backend folder
```bash
cd Revplay_Project-1
```

2. Run:
```bash
mvn spring-boot:run
```

Backend runs on:
http://localhost:8080


##  Running Frontend

1. Navigate to frontend folder
```bash
cd Revplay_Project-MVC
```

2. Run:
```bash
mvn spring-boot:run
```

Frontend runs on:
http://localhost:8082
