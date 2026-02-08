# EQUA - Spring Boot Project Template

## 📋 Table of Contents
- [Project Overview](#project-overview)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Architecture & Layers](#architecture--layers)
- [Getting Started](#getting-started)
- [Git Workflow](#git-workflow)
- [Development Guidelines](#development-guidelines)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [Database](#database)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Project Overview

**Equa** is a Spring Boot 3.2.1 application built with a domain-driven design approach. Each business domain (e.g., User Management, Wallet Management, Payment Management) is organized in its own package with a complete set of layers.

### Tech Stack
- **Java**: 21.0.9
- **Spring Boot**: 3.2.1
- **Maven**: 3.8.8 
- **Database**: PostgreSQL 16
- **Docker**: For containerized database
- **Swagger/OpenAPI**: API documentation

---

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

### 1. Java 21
```bash
java -version
# Should show: openjdk version "21.0.9" or higher
```

If not installed:
```bash
sudo apt update
sudo apt install openjdk-21-jdk -y
```

### 2. Maven 3.8.8+

Verify installation:
```bash
mvn --version
# Should show: Apache Maven 3.8.8
```

### 3. Docker & Docker Compose
```bash
docker --version
docker-compose --version
```

If not installed:
```bash
sudo apt update
sudo apt install docker.io docker-compose -y
sudo systemctl start docker
sudo systemctl enable docker
```

**Important**: After adding yourself to the docker group, **log out and log back in** for changes to take effect.

### 4. Git
```bash
git --version
```

If not installed:
```bash
sudo apt install git -y
```

---

## 🏗️ Project Structure

```
equa/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── rayen/
│                   ├── Application.java (Main Spring Boot application)
│                   ├── config/
│                   │   └── OpenApiConfig.java (Swagger configuration)
│                   ├── blockChainManagement/
│                   │   ├── controller/
│                   │   ├── entity/
│                   │   ├── model/
│                   │   ├── repository/
│                   │   └── service/
│                   ├── financialMarketManagement/
│                   │   ├── controller/
│                   │   ├── entity/
│                   │   ├── model/
│                   │   ├── repository/
│                   │   └── service/
│                   ├── loanManagement/
│                   │   ├── controller/
│                   │   ├── entity/
│                   │   ├── model/
│                   │   ├── repository/
│                   │   └── service/
│                   └── ... (other domain modules)
│       └── resources/
│           └── application.properties
├── docker-compose.yml
├── pom.xml
└── README.md
```

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

import com.rayen.usermanagement.entity.User;
import com.rayen.usermanagement.model.UserDTO;
import com.rayen.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        // Business logic and validation here
        User user = convertToEntity(userDTO);
        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    // Conversion methods
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    private User convertToEntity(UserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
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

import com.rayen.usermanagement.model.UserDTO;
import com.rayen.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
```

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
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
mvn-personal clean install
```

### 4. Run the Application
```bash
mvn-personal spring-boot:run
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

**Branch naming conventions**:
- `feature/user-authentication` - New features
- `bugfix/fix-login-error` - Bug fixes
- `hotfix/critical-security-patch` - Critical fixes
- `refactor/cleanup-user-service` - Code refactoring

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

**Commit message conventions**:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `refactor:` - Code refactoring
- `test:` - Adding tests
- `chore:` - Maintenance tasks

#### Step 4: Push to Remote
```bash
git push origin feature/your-feature-name
```

#### Step 5: Create a Pull Request
1. Go to GitHub/GitLab
2. Click **"New Pull Request"**
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
git branch -d feature/your-feature-name  # Delete local branch (optional)
```

### Important Git Rules
1. **Always pull from main before creating a new branch**
2. **Never work directly on main**
3. **One feature = One branch**
4. **Keep commits small and focused**
5. **Write descriptive commit messages**

---

## 👨‍💻 Development Guidelines

### Creating a New Feature Module

Let's say you need to create a **Payment Management** module:

#### 1. Create the package structure
```bash
cd src/main/java/com/rayen
mkdir -p paymentmanagement/{controller,entity,model,repository,service}
```

#### 2. Create Entity
```java
// paymentmanagement/entity/Payment.java
package com.rayen.paymentmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
    
    // Getters and Setters
}
```

#### 3. Create Repository
```java
// paymentmanagement/repository/PaymentRepository.java
package com.rayen.paymentmanagement.repository;

import com.rayen.paymentmanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
}
```

#### 4. Create DTO
```java
// paymentmanagement/model/PaymentDTO.java
package com.rayen.paymentmanagement.model;

import java.time.LocalDateTime;

public class PaymentDTO {
    private Long id;
    private Long userId;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
    
    // Getters and Setters
}
```

#### 5. Create Service
```java
// paymentmanagement/service/PaymentService.java
package com.rayen.paymentmanagement.service;

import com.rayen.paymentmanagement.entity.Payment;
import com.rayen.paymentmanagement.model.PaymentDTO;
import com.rayen.paymentmanagement.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = convertToEntity(paymentDTO);
        Payment saved = paymentRepository.save(payment);
        return convertToDTO(saved);
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUserId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }

    private Payment convertToEntity(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setUserId(dto.getUserId());
        payment.setAmount(dto.getAmount());
        payment.setStatus(dto.getStatus());
        return payment;
    }
}
```

#### 6. Create Controller
```java
// paymentmanagement/controller/PaymentController.java
package com.rayen.paymentmanagement.controller;

import com.rayen.paymentmanagement.model.PaymentDTO;
import com.rayen.paymentmanagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PostMapping
    public PaymentDTO createPayment(@RequestBody PaymentDTO paymentDTO) {
        return paymentService.createPayment(paymentDTO);
    }
}
```

#### 7. Test Your Endpoints
1. Start the application: `mvn-personal spring-boot:run`
2. Go to Swagger: `http://localhost:8080/swagger-ui/index.html`
3. Find your `/api/payments` endpoints
4. Test them directly from Swagger UI

---

## 📚 API Documentation (Swagger)

### Accessing Swagger UI
Once the application is running, access the interactive API documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

### What You Can Do with Swagger
- **View all endpoints**: See every REST API endpoint organized by controller
- **Test endpoints**: Execute API calls directly from the browser
- **See request/response models**: View DTO structures
- **Try different inputs**: Test with different parameters and request bodies

### Using Swagger UI

#### 1. Expand an Endpoint
Click on any endpoint (e.g., `GET /api/users`) to see details.

#### 2. Try It Out
Click the **"Try it out"** button to enable input fields.

#### 3. Enter Parameters
Fill in any required parameters or request body.

#### 4. Execute
Click **"Execute"** to send the request.

#### 5. View Response
See the response code, body, and headers.

### Swagger Annotations (Optional)
You can enhance your API documentation with annotations:

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }
}
```


### Database Commands

**Start database**:
```bash
docker-compose up -d
```

**Stop database**:
```bash
docker-compose down
```

**Stop and delete all data**:
```bash
docker-compose down -v
```

**View database logs**:
```bash
docker-compose logs postgres
```

**Connect to database**:
```bash
docker exec -it equa-postgres psql -U equauser -d equadb
```

### Hibernate DDL Auto
The application uses `spring.jpa.hibernate.ddl-auto=update`, which means:
- Tables are **created automatically** from entities
- Existing tables are **updated** when entities change
- Data is **preserved** (not deleted)

**Options**:
- `create`: Drop and recreate tables (deletes data)
- `update`: Update schema without deleting data (recommended for development)
- `validate`: Only validate schema (recommended for production)
- `none`: Do nothing

---

## 🐛 Troubleshooting

### Database Connection Failed
**Error**: `Connection refused` or `Could not connect to database`

**Solution**:
```bash
# Check if PostgreSQL container is running
docker ps

# If not running, start it
docker-compose up -d

# Check logs for errors
docker-compose logs postgres
```

### Docker Permission Denied
**Error**: `permission denied while trying to connect to Docker daemon socket`

**Solution**:
```bash
# Add your user to docker group
sudo usermod -aG docker $USER

# Log out and log back in, then verify
docker ps
```

### Maven Command Not Found
**Error**: `mvn-personal: command not found`

**Solution**:
```bash
# Check if alias exists
alias | grep mvn

# If not, add to ~/.bashrc
echo 'alias mvn-personal="/opt/maven-personal/bin/mvn"' >> ~/.bashrc
source ~/.bashrc
```

### Port 8080 Already in Use
**Error**: `Port 8080 is already in use`

**Solution**:
```bash
# Find process using port 8080
sudo lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change application port in application.properties
server.port=8081
```

### Swagger Not Loading
**Error**: Swagger UI shows 404 or doesn't load

**Solution**:
1. Verify dependency in `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springdoc</groupId>
       <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
       <version>2.2.0</version>
   </dependency>
   ```

2. Rebuild project:
   ```bash
   mvn-personal clean install
   mvn-personal spring-boot:run
   ```

3. Access correct URL:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

---

## 📝 Quick Reference

### Daily Development Workflow
```bash
# 1. Start database (once per day)
docker-compose up -d

# 2. Pull latest changes
git checkout main
git pull origin main

# 3. Create feature branch
git checkout -b feature/my-feature

# 4. Develop your feature
# ... code, code, code ...

# 5. Run and test
mvn-personal spring-boot:run
# Visit: http://localhost:8080/swagger-ui/index.html

# 6. Commit and push
git add .
git commit -m "feat: add my feature"
git push origin feature/my-feature

# 7. Create Pull Request on GitHub/GitLab

```

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

## 🎓 Learning Resources

### Spring Boot
- [Official Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Building REST APIs with Spring Boot](https://spring.io/guides/tutorials/rest/)

### JPA & Hibernate
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

### PostgreSQL
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### Docker
- [Docker Get Started](https://docs.docker.com/get-started/)

---


### Avoiding Conflicts
- Work only in your assigned domain folder
- Don't modify `Application.java` or shared configuration files without discussion
- Communicate with team before modifying `pom.xml` or `application.properties`

### Code Reviews
- All Pull Requests must be reviewed before merging
- Follow the coding standards
- Write clean, readable code
- Add comments for complex logic

---

## ✅ Checklist for New Developers

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

