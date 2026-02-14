[![Documentation](https://img.shields.io/badge/docs-GitHub%20Pages-blue)](https://rayenamer.github.io/equa/)
[![Presentation](https://img.shields.io/badge/presentation-Canva-purple)](https://www.canva.com/design/DAG__gdQ8Zk/Pnvcg-baYSi5G3TQ7o7i-g/edit?utm_content=DAG__gdQ8Zk&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)
[![Report](https://img.shields.io/badge/report-Google%20Docs-green)](https://docs.google.com/document/d/1SJ-QzxcbxZDwQ7bfMLdtrK59frejipB1qcXVu3yWqw8/edit?tab=t.0)
[![Diagrams](https://img.shields.io/badge/diagrams-Miro-orange)](https://miro.com/app/board/uXjVGGvURDc=/)

## 📘 Class Diagram
![Class Diagram](https://ik.imagekit.io/qypbatx8h/classDiagram.png)

## 🔄 System Workflow
![System Workflow](https://ik.imagekit.io/qypbatx8h/workflowDiagram.png)

## 📅 Project Timeline
![Project Timeline](https://ik.imagekit.io/qypbatx8h/equaTimeLine.png?updatedAt=1771099370415)


### Tech Stack
- **Java**: 21.0.9
- **Spring Boot**: 3.2.1
- **Maven**: 3.8.8 
- **Database**: PostgreSQL 16
- **Docker**: For containerized database
- **Swagger/OpenAPI**: API documentation

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone (https://github.com/rayenamer/equa)
cd equa
```

### 2. Start the Database
The project uses PostgreSQL running in Docker. Start it with:

```bash
docker-compose up -d
```

**What this does**:
- Downloads PostgreSQL 16 Alpine image (if not already downloaded)
- Creates a container named `equa-postgres`
- Starts PostgreSQL on port `5432`
- Creates database `equadb` with user `equauser` and password `equapass`
- Runs in detached mode (background)

**Verify it's running**:
```bash
docker ps
```

You should see `equa-postgres` in the list.

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

**Expected output**:
```
Started Application in X.XXX seconds
Tomcat started on port 8080 (http)
```

### 5. Verify the Application
Open your browser and visit:
```
http://localhost:8080/swagger-ui/index.html
```

You should see the Swagger UI with all available API endpoints!

---

## 🔀 Git Workflow

### Branch Protection Rules
- **main** branch is **protected**
- **No one** can push directly to `main`
- All changes must go through **Pull Requests**
- Only the **repository owner** can approve and merge PRs

### Development Workflow

#### Step 1: Create a New Branch
Before starting any work, create a feature branch:

```bash
# Make sure you're on main and up to date
git checkout main
git pull origin main

# Create and switch to a new feature branch
git checkout -b feature/your-feature-name
```

#### Step 2: Make Your Changes
Work on your assigned domain module (e.g., `blockChainManagement`, `loanManagement`).

```bash
# Create your entities, repositories, services, controllers, models
# Test your code locally
```

#### Step 3: Commit Your Changes
```bash
git add .
git commit -m "feat: add user authentication endpoints"
```
#### Step 4: Push to Remote
```bash
git push origin feature/your-feature-name
```

#### Step 5: Create a merge Request
1. Go to GitHub
2. Click **"New merge Request"**
3. Select your branch (`feature/your-feature-name`) → `main`
4. Add a clear description of your changes
5. Submit the PR

#### Step 6: Code Review & Merge
- The repository owner will review your code
- Address any requested changes
- Once approved, the owner will merge your PR into `main`

#### Step 7: Update Your Local Main
After your PR is merged:

```bash
git checkout main
git pull origin main
```
#### in case you branch is behind 

```bash
# update you rlocal main 
git pull origin main
# switch to your branch
git switch feat/yourBranch
# pull code from main to your branch
git merge main
```

### Important Git Rules
1. **Always pull from main before creating a new branch**
2. **Never work directly on main**
3. **Keep commits small and focused**

---

### Essential Commands
```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Start database
docker-compose up -d

# Stop database
docker-compose down

# View logs
docker-compose logs -f postgres

# Check running containers
docker ps
```

### Important URLs
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API Docs JSON**: http://localhost:8080/v3/api-docs

---

### Avoiding Conflicts
- Work only in your assigned domain folder
- Don't modify `Application.java` or shared configuration files without discussion
- Communicate with team before modifying `pom.xml` or `application.properties`

### Code Reviews
- Si Rayen will accept merge request only if build job was successful
---

## 🧱 Architecture & Layers
Each domain module follows a **layered architecture**. Here's what each layer does:
### 📂 **entity/** - Database Entities
**Purpose**: Represents database tables using JPA annotations.
**Rules**:
- Maps directly to database tables
- Uses `@Entity`, `@Table`, `@Id`, `@GeneratedValue` annotations
- Contains only database-related fields
- Includes getters and setters
**Example**:
```java
package com.rayen.usermanagement.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    
    // Getters and Setters
}
```

---

### 📂 **repository/** - Database Access Layer
**Purpose**: Direct communication with the database. **NO business logic here!**

**Rules**:
- Extends `JpaRepository<Entity, ID>`
- Contains ONLY database queries
- No business logic
- Annotated with `@Repository`

**Example**:
```java
package com.rayen.usermanagement.repository;

import com.rayen.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom queries can be added here
    User findByEmail(String email);
}
```

---

### 📂 **model/** - Data Transfer Objects (DTOs)
**Purpose**: Objects used to transfer data between layers (API ↔ Service).

**Rules**:
- **Never** expose entities directly in APIs
- Contains only fields needed for API requests/responses
- No JPA annotations
- Clean, simple POJOs

**Example**:
```java
package com.rayen.usermanagement.model;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    
    // Getters and Setters
}
```

**Why DTOs?**
- Security: Hide internal database structure
- Flexibility: API can have different fields than database
- Versioning: Change API without changing database

---

### 📂 **service/** - Business Logic Layer
**Purpose**: Contains all business logic, validation, and data transformation.

**Rules**:
- Annotated with `@Service`
- Uses repositories to access data
- Converts between Entity ↔ DTO
- Contains validation logic
- Contains all business rules

**Example**:
```java
package com.rayen.usermanagement.service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}
```

---

### 📂 **controller/** - REST API Endpoints
**Purpose**: Exposes REST endpoints to the outside world.

**Rules**:
- Annotated with `@RestController` and `@RequestMapping`
- **NO business logic** - delegates to service layer
- Uses DTOs (not entities) in request/response
- Handles HTTP methods: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`

**Example**:
```java
package com.rayen.usermanagement.controller;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }
}
```
---

## ✅ Checklist for First Time Dev

- [ ] Install Java 21
- [ ] Install Maven 3.8.8
- [ ] Install Docker and Docker Compose
- [ ] Clone the repository
- [ ] Start PostgreSQL: `docker-compose up -d`
- [ ] Build project: `mvn clean install`
- [ ] Run application: `mvn spring-boot:run`
- [ ] Access Swagger: http://localhost:8080/swagger-ui/index.html
- [ ] Create your first feature branch
- [ ] Read the architecture section
- [ ] Understand the layer responsibilities

**Happy Coding! 🚀**

