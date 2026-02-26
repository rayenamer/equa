
[![Presentation](https://img.shields.io/badge/presentation-Canva-purple)](https://www.canva.com/design/DAG__gdQ8Zk/Pnvcg-baYSi5G3TQ7o7i-g/edit?utm_content=DAG__gdQ8Zk&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

[![Report](https://img.shields.io/badge/report-Google%20Docs-green)](https://docs.google.com/document/d/1SJ-QzxcbxZDwQ7bfMLdtrK59frejipB1qcXVu3yWqw8/edit?tab=t.0)

[![Diagrams](https://img.shields.io/badge/diagrams-Miro-orange)](https://miro.com/app/board/uXjVGGvURDc=/)

[![Frontend App](https://img.shields.io/badge/project-Angular%20Client-red?logo=angular&logoColor=white)](https://github.com/rayenamer/SocialMediaProjectClient)

[![Spring Microservice 1](https://img.shields.io/badge/project-Spring%20Boot%20Service-success?logo=springboot&logoColor=white)](https://github.com/psubhajit14/spring-boot-microservice)

[![Spring Microservice 2](https://img.shields.io/badge/project-Candidats%20Service-success?logo=springboot&logoColor=white)](https://github.com/AzizBenIsmail/MicroserviceCandidats)

[![Angular Distributed Frontend](https://img.shields.io/badge/project-Distributed%20Angular-red?logo=angular&logoColor=white)](https://github.com/AzizBenIsmail/Appl.Web-Distribues-EcoExchange_FrontEndAngular/tree/main/src)


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
git clone https://github.com/rayenamer/equa
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

**Note:** Si le serveur tourne sur le port **8081**, utilisez : http://localhost:8081/swagger-ui.html

---

## 📋 Gestions et API (Authentication, User, Forum)

Le projet expose trois groupes d’API documentés dans Swagger : **Authentication**, **User Management** et **Forum Management**. Voici ce que fait chaque gestion et la liste des endpoints.

**En résumé** :  
- **Auth** : inscription (signup) et connexion (signin) avec token JWT.  
- **User** : CRUD, rôles/permissions, audit logs.  
- **Forum** : sujets de discussion (topics) et messages ; **tous les utilisateurs** peuvent créer des sujets et envoyer des messages.

### 1. Authentication (Authentification)

**Rôle** : Inscription et connexion des utilisateurs. Retourne un **token JWT** utilisé pour accéder aux endpoints protégés.

| Méthode | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Inscrire un nouvel utilisateur (username, email, password, userType, permissions si OBSERVER). Retourne token + infos user. |
| POST | `/api/auth/signin` | Connexion avec email et mot de passe. Retourne token JWT. |

**Fonctionnement** : Après **signup** ou **signin**, le client reçoit un `token`. Pour les appels protégés, il envoie l’en-tête : `Authorization: Bearer <token>`.

---

### 2. User Management (Gestion des utilisateurs)

**Rôle** : CRUD utilisateurs (ASSISTANT / OBSERVER), rôles/permissions, et journaux d’audit.

| Méthode | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Liste de tous les utilisateurs. |
| GET | `/api/users/{id}` | Détail d’un utilisateur par ID. |
| POST | `/api/users` | Créer un user (ASSISTANT ou OBSERVER). Body : username, email, userType, permissions (optionnel). |
| PUT | `/api/users/{id}` | Modifier un utilisateur (username, email, etc.). |
| DELETE | `/api/users/{id}` | Supprimer un utilisateur. |
| GET | `/api/users/{id}/roles` | Obtenir le type et les permissions d’un user. |
| PUT | `/api/users/{id}/roles` | Mettre à jour rôles et permissions (OBSERVER). |
| GET | `/api/users/audit-logs` | Tous les journaux d’audit. |
| GET | `/api/users/observers/{observerUserId}/audit-logs` | Journaux d’audit d’un observateur. |
| POST | `/api/users/observers/{observerUserId}/audit-log` | Enregistrer une action d’audit (ObserverUser). |

**Fonctionnement** : Les utilisateurs sont stockés en base (table `users` + `assistant_users` / `observer_users`). Les OBSERVER ont des permissions (VIEW_LOGS, AUDIT, etc.). Les audit logs lient une action à un observateur.

---

### 3. Forum Management (Gestion forum)

**Rôle** : Sujets de discussion (topics) et messages. **Tous les utilisateurs** peuvent créer des sujets et envoyer des messages dans n’importe quel sujet.

| Méthode | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/forum/topics` | Liste de tous les sujets. |
| GET | `/api/forum/topics/{id}` | Détail d’un sujet (avec ses messages). |
| GET | `/api/forum/topics/by-user/{userId}` | Sujets créés par un utilisateur. |
| POST | `/api/forum/topics` | Créer un sujet. Body : createdById, title, description. |
| PUT | `/api/forum/topics/{id}` | Modifier titre / description d’un sujet. |
| DELETE | `/api/forum/topics/{id}` | Supprimer un sujet. |
| POST | `/api/forum/topics/{id}/messages` | Envoyer un message dans un sujet. Body : authorId, messageText. |
| GET | `/api/forum/topics/{id}/messages` | Liste des messages d’un sujet. |

**Fonctionnement** : Un **sujet** (ForumTopic) a un titre, une description et un créateur (User). Les **messages** (ForumMessage) sont liés à un sujet et à un auteur (User). Tout utilisateur peut créer un sujet et poster dans n’importe quel sujet. En base : tables **forum_topics** et **forum_messages**.

**Documentation détaillée** : [docs/FORUM_MANAGEMENT.md](docs/FORUM_MANAGEMENT.md).  
**Guide de test Swagger** : [docs/SWAGGER_TEST_GUIDE.md](docs/SWAGGER_TEST_GUIDE.md).

---

## 🔀 Git Workflow

### Branch Protection Rules
- **main** branch is **protected**
- **No one** can push directly to `main`
- All changes must go through **Pull Requests**
- Only **RAYEN** can approve and merge PRs

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
- **Application**: http://localhost:8081 (ou 8080 selon `server.port` dans `application.properties`)
- **Swagger UI**: http://localhost:8081/swagger-ui.html

---

### Avoiding Conflicts
- Work only in your assigned domain folder
- Don't modify `Application.java` or shared configuration files without discussion
- Communicate with team before modifying `pom.xml` or `application.properties`

### Code Reviews
- RAYEN will accept merge request only if build job was successful
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

