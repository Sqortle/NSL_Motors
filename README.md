# NSL Motors 🏎️

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

**NSL Motors** is a comprehensive web-based automotive management platform developed as a term project for the **BIL359 Internet Programming** course. The system is designed to streamline vehicle inventory management, sales tracking, and user interactions within a modern digital dealership environment.

##  Features

- **Vehicle Inventory:** Detailed listing of motors with specifications, pricing, and availability status.
- **User Authentication:** Secure login and registration system for customers and administrators.
- **Admin Dashboard:** Tools for staff to add, update, or remove vehicle listings and manage user roles.
- **Search & Filter:** Advanced search capabilities to find vehicles by make, model, year, or price range.
- **Responsive Design:** Optimized for both desktop and mobile viewing.

##  Tech Stack

- **Backend:** Java 17+, Spring Boot 3.x
- **Frameworks:** Spring Data JPA, Spring Security, Spring Web
- **Build Tool:** Maven
- **Database:** H2 / MySQL / PostgreSQL (Update based on your properties)

##  Getting Started

### Prerequisites
- JDK 17 or higher
- Maven 3.6+

### Installation
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/MirzaSakiroglu/NSL_Motors.git](https://github.com/MirzaSakiroglu/NSL_Motors.git)
   cd NSL_Motors
   ```

2. **Configure the database:**
   Update the src/main/resources/application.properties file with your database credentials.

3. **Build and Run:**
```bash
./mvnw spring-boot:run
```
The application will be available at ``` http://localhost:8080 ```.

### Project Structure
```plaintext
src/
 ├── main/
 │    ├── java/          # Backend logic (Controllers, Services, Models)
 │    └── resources/     # Static assets, templates, and config
 └── test/               # Unit and integration tests
```

