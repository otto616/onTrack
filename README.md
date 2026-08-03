# OnTrack - Document Management System

<div align="center">
  <img src="assets/logo.png" alt="Logo OnTrack" width="200">
</div>
<br>

## Overview
OnTrack is a comprehensive web-based Document Management System originally developed as a prototype. It streamlines the process of tracking, verifying, and storing mandatory legal and safety documentation for different types of contractors, such as industrial subcontractors and freelancers.

## Prototype Disclaimer & Future Roadmap
This project is currently a **functional prototype** designed to demonstrate the core workflows, data models, and user interface of the Document Management System. 

To transition this prototype into a production-ready web application, the following architectural upgrades are required:

* **Database Persistence:** Replace the current temporary In-Memory H2 database with a robust relational database (e.g., PostgreSQL, MySQL, or SQL Server) to ensure permanent data storage across server restarts.
* **File Storage Integration:** Migrate the local file upload system to a dedicated cloud storage solution (e.g., AWS S3, Google Cloud Storage) or a secure corporate file server for scalable and reliable document handling.
* **Authentication & Authorization:** Implement a security layer (e.g., Spring Security, OAuth2, or Active Directory) to manage secure logins, user roles, and generate an automated audit log (tracking who uploads or verifies which document).
* **Production Deployment:** Containerize the application (e.g., using Docker) and deploy it to a production server environment with proper SSL/TLS certificates (HTTPS) for secure data transmission.

## Key Features
*   **Dynamic Dashboard:** View global statistics (Total Clients, Pending Documents, Expired Documents) and real-time alerts for documents expiring within 30 days.
*   **Client Management:** Register and manage clients based on their specific typology (e.g., Subcontractor, Freelance with/without workers).
*   **Master Catalog:** Configure the universal list of required documents per client type. Set expiration rules, categories (Company, Worker, Machinery), and official verification URLs.
*   **Automated Checklists:** The system automatically generates a customized documentation checklist for each new client based on the active Master Catalog rules.
*   **File Management:** Upload, store, and download PDF or image files directly attached to their corresponding checklist requirement.
*   **Status Tracking:** Visual badges automatically update to show if a document is *Pending*, *Verified (OK)*, *Expiring Soon*, or *Expired*.

## Tech Stack
*   **Backend:** Java 25, Spring Boot 4.1.0, Spring Data JPA
*   **Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap 5.3
*   **Database:** H2 Database (In-Memory for rapid development and testing)
*   **Typography & UI:** Google DM Sans font with a corporate palette and layout.

## Prerequisites
Ensure you have the following installed on your system:
*   Java Development Kit (JDK) 25
*   Maven (or use the included Maven Wrapper `./mvnw`)
*   An IDE like IntelliJ IDEA (highly recommended)

## How to Run Locally

First of all, clone the repository

### Option 1: Using IntelliJ IDEA (Recommended)
1. Open IntelliJ IDEA and select **Open**. Navigate to the `onTrack` project folder.
2. Wait for Maven to sync and download all necessary dependencies.
3. Locate the `OnTrackApplication.java` file in `src/main/java/com/otto616/onTrack/`.
4. Click the green **Play** button next to the `main` method to start the server.
5. Open your web browser and go to: `http://localhost:8080`

### Option 2: Using the Terminal (Ubuntu / Linux / macOS)
1. Open your terminal and navigate to the project directory:
   ```bash
   cd /path/to/onTrack
   ```
2. Run the Spring Boot application using the provided Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Open your web browser and go to: `http://localhost:8080`

## Testing and Usage Guide
*   **Initial Data Seed:** Upon startup, the `DataLoader.java` component automatically populates the database with the official document catalog rules and generates several dummy clients. This allows you to immediately test the platform without manually inserting dozens of records.
*   **Uploading Files:** When uploading a PDF or image via the checklist, a local `uploads/` directory will be automatically created in the root folder of the project.
*   **Database Reset Notice:** Because this prototype utilizes an H2 In-Memory database, **all uploaded file references, manual date changes, and newly created clients will be erased** every time the server is stopped and restarted. The application will boot with a clean state on every execution.

## Project Structure
*   `/controllers`: Handles web routing, HTTP requests, and Thymeleaf model generation.
*   `/models`: JPA Entities representing the database structure (`Client`, `DocumentType`, `ChecklistDocument`) and Enums.
*   `/repositories`: Spring Data JPA interfaces for seamless database operations and custom queries.
*   `/bootstrap`: Contains `DataLoader.java` for initial database seeding.
*   `/resources/templates`: Thymeleaf HTML views (`index.html`, `checklist.html`, `catalog.html`, `upload.html`, etc.).
*   `/resources/static/css`: Custom corporate stylesheets (`style.css`).
