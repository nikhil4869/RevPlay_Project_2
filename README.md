# 🎵 RevPlay - Music Streaming Web Application

## Application Overview
RevPlay is a full-stack monolithic music streaming web application built using **Spring Boot, Oracle SQL, and Thymeleaf**. The platform allows users to discover, stream, and organize music while enabling artists to upload songs and analyze listener engagement.

The application follows a **Monolithic Architecture with MVC pattern** and implements **Role-Based Access Control** for both **Listeners and Artists**.

----------------------------------------------------------------

## Features

### Listener Features

**User Authentication**
- User registration and login
- Secure authentication using Spring Security with JWT
- Role-based authorization

**Profile Management**
- Update user profile details
- View user activity and statistics

**Music Discovery**
- Browse songs
- Browse artists
- Browse albums
- Search music content

**Music Player**
- Play songs
- Pause songs
- Skip tracks
- Seek within track timeline

**Favorites Management**
- Add songs to favorites
- Remove songs from favorites
- View favorite songs

**Playlist Management**
- Create playlists
- Add songs to playlists
- Remove songs from playlists
- Manage playlist visibility

**Follow Public Playlists**
- Follow playlists created by other users
- Unfollow playlists
- View followed playlists

**Listening History**
- Track recently played songs
- Maintain listening history records

**User Dashboard**
- View listening statistics
- Total songs listened
- Favorite songs count
- Playlist activity summary

----------------------------------------------------------------

### Artist Features

**Artist Profile Management**
- Manage artist profile information
- Update artist details

**Upload Songs and Albums**
- Upload songs to the platform
- Create and manage albums
- Organize songs within albums

**Song Visibility Control**
- Manage public or hidden songs
- Control song availability

**Analytics Dashboard**
- Track song play counts
- View number of favorites
- Analyze listening trends
- Identify top listeners

----------------------------------------------------------------

## Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- Hibernate / JPA
- JWT Authentication
- Log4J

### Database
- Oracle SQL

### Frontend
- Thymeleaf
- HTML
- CSS
- JavaScript

### Build Tool
- Maven

### Testing
- JUnit4

### Version Control
- Git

----------------------------------------------------------------

## Architecture

RevPlay follows a **Monolithic Architecture** and uses the **MVC design pattern**.

**Controller Layer**
Handles HTTP requests and API endpoints.

**Service Layer**
Contains business logic and application rules.

**Repository Layer**
Handles database operations using Spring Data JPA.

**View Layer**
Implemented using Thymeleaf templates.

----------------------------------------------------------------

## My Role

**User Experience & Engagement Engineer**

Responsibilities included:

- Implemented user profile and dashboard features
- Developed favorites management module
- Built listening history tracking system
- Implemented playlist follow and unfollow functionality
- Maintained accurate user engagement statistics

----------------------------------------------------------------

## How To Run The Project

### Clone the Repository

git clone https://github.com/nikhil4869/RevPlay_Project_2.git

cd RevPlay_Project_2

----------------------------------------------------------------

### Configure Oracle Database

Update the `application.properties` file:

spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1

spring.datasource.username=YOUR_USERNAME

spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true

----------------------------------------------------------------

### Run Backend

Navigate to backend folder:

cd Revplay_Project-1

Run the application:

mvn spring-boot:run

Backend runs on:

http://localhost:8080

----------------------------------------------------------------

### Run Frontend

Navigate to frontend folder:

cd Revplay_Project-MVC

Run the application:

mvn spring-boot:run

Frontend runs on:

http://localhost:8082

----------------------------------------------------------------

## Project Structure

RevPlay_Project_2

Revplay_Project-1 (Backend)
- controller
- service
- repository
- entity
- dto
- security

Revplay_Project-MVC (Frontend)
- templates
- static
- controllers
- services

----------------------------------------------------------------

## Key Highlights

- Full-stack music streaming platform
- JWT-based secure authentication
- Role-based access control
- Artist analytics dashboard
- Favorites and playlist management
- Listening history tracking
- User engagement statistics

----------------------------------------------------------------

## Future Improvements

- Music recommendation system
- Social sharing features
- Real-time streaming optimization
- Mobile application support
- Advanced analytics dashboard

----------------------------------------------------------------

## License

This project is created for educational and demonstration purposes.
