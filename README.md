# EQUA - Spring Boot Project Template

## 🎯 Project Overview

**Equa** is a microservices-based Spring Boot application built with a domain-driven design approach. The platform includes a Wallet Management service with advanced business rules, a Python AI risk scoring microservice, Eureka service discovery, and an API Gateway — all orchestrated via Docker Compose.

### Tech Stack
- **Java**: 21
- **Spring Boot**: 3.2.1
- **Spring Cloud**: 2023.0.0 (Eureka, Gateway)
- **Maven**: 3.8.8
- **Database**: PostgreSQL 16
- **Python**: 3.11 (Flask + scikit-learn AI service)
- **Docker / Docker Compose**: Full stack orchestration
- **Swagger/OpenAPI**: API documentation
- **WebSocket (STOMP)**: Real-time notifications

### Microservices Architecture
| Service | Port | Technology |
|---------|------|------------|
| Eureka Server | 8761 | Spring Cloud Netflix |
| API Gateway | 8088 | Spring Cloud Gateway |
| Wallet Service | 8080 | Spring Boot + JPA |
| AI Service | 5000 | Python Flask + scikit-learn |
| PostgreSQL | 5432 | PostgreSQL 16 |

---

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

### 1. Java 21+
```bash
java -version
# Should show: openjdk version "21.x" or higher
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
│                   ├── walletManagement/
│                   │   ├── controller/
│                   │   │   ├── WalletController.java
│                   │   │   ├── TokenController.java
│                   │   │   ├── AssetController.java
│                   │   │   └── GlobalExceptionHandler.java
│                   │   ├── entity/
│                   │   │   ├── Wallet.java
│                   │   │   ├── Token.java
│                   │   │   └── Asset.java
│                   │   ├── model/
│                   │   │   ├── WalletDTO.java
│                   │   │   ├── TokenDTO.java
│                   │   │   ├── AssetDTO.java
│                   │   │   ├── TransferRequest.java
│                   │   │   └── RiskAssessmentDTO.java
│                   │   ├── repository/
│                   │   │   ├── WalletRepository.java
│                   │   │   ├── TokenRepository.java
│                   │   │   └── AssetRepository.java
│                   │   └── service/
│                   │       ├── WalletService.java
│                   │       ├── TokenService.java
│                   │       ├── AssetService.java
│                   │       └── RiskAssessmentService.java
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
├── Dockerfile
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

## � Wallet Management Module (Safwen)

### Description

Le module **walletManagement** gère la partie financière du système. Le wallet est l'équivalent d'un compte bancaire interne. Il gère le solde, les entrées/sorties de tokens, la disponibilité de la valeur, et la traçabilité complète des opérations.

> **Finance = règles de débit/crédit, pas blockchain.**

### Entités & Relations

```
User (1) ──────── (1) Wallet
                        │
                   (1) ──── (*) Token
                        │
                   (1) ──── (*) Asset
```

| Entité | Table | Description |
|--------|-------|-------------|
| **Wallet** | `wallets` | Compte principal : solde, clé publique, statut, historique des transactions |
| **Token** | `tokens` | Unité de valeur interne avec taux de conversion fixe (anti-volatilité) |
| **Asset** | `assets` | Bien enregistré (immobilier, véhicule, équipement, digital) avec collatéral |

### Règles Métiers Avancées

#### 1. Finance dans le Wallet
- **Dépôt / Retrait** : avec limites par transaction (max 50 000) et seuil minimum de solde (10)
- **Transfert** : entre wallets via ID ou clé publique, avec validation croisée
- **Historique** : chaque opération (SEND, RECEIVE, DEPOSIT, WITHDRAW) est tracée avec timestamp

#### 2. Gestion du Risque
- **Règles métiers** : validation du statut wallet (ACTIVE/SUSPENDED/CLOSED), vérification de solde
- **Indicateurs financiers** : ratio couverture d'actifs, taux de remboursement, nombre de retards
- **Scoring simple** : calcul automatique basé sur l'historique des transactions

#### 3. Stabilité de la Valeur (Anti-Volatilité)
- Le token a une **valeur fixe ou encadrée** (taux de conversion entre 0.5 et 2.0)
- **Logique de conversion interne** : `valeur convertie = valeur × taux de conversion`
- **Aucune spéculation** : le taux est borné et contrôlé

#### 4. Traçabilité & Audit Financier
- Chaque wallet maintient un `transactionHistory` complet
- Enregistrement automatique de : dépôts, retraits, transferts, enregistrement d'actifs, revaluations
- Format : `timestamp | TYPE_OPÉRATION | montant | détails`

### Modèle IA — Scoring & Prédiction

Le service `RiskAssessmentService` implémente un **modèle de scoring de crédit** simulant une régression logistique :

| Composant | Description |
|-----------|-------------|
| **Credit Score** | Score 300–850 (type FICO) avec facteurs pondérés |
| **Prédiction de défaut** | Fonction sigmoïde avec coefficients entraînés simulés |
| **Classification du risque** | LOW / MEDIUM / HIGH / CRITICAL |
| **Recommandation** | APPROVE / REVIEW / DENY avec justification |

**Facteurs du score :**
- Stabilité du solde (25%)
- Couverture d'actifs (25%)
- Historique de transactions (20%)
- Retards de paiement (20%)
- Taux de remboursement (10%)

**Endpoint :** `GET /api/wallets/risk/{customerId}`

### API Endpoints

#### Wallet (`/api/wallets`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/wallets` | Lister tous les wallets |
| GET | `/api/wallets/{id}` | Wallet par ID |
| GET | `/api/wallets/customer/{customerId}` | Wallet par customer |
| POST | `/api/wallets` | Créer un wallet |
| PUT | `/api/wallets/{id}` | Modifier un wallet |
| DELETE | `/api/wallets/{id}` | Supprimer un wallet (solde doit être 0) |
| POST | `/api/wallets/{id}/deposit?amount=X` | Déposer des fonds |
| POST | `/api/wallets/{id}/withdraw?amount=X` | Retirer des fonds |
| POST | `/api/wallets/{id}/transfer` | Transférer des tokens (body: TransferRequest) |
| PUT | `/api/wallets/{id}/suspend` | Suspendre un wallet |
| PUT | `/api/wallets/{id}/activate` | Activer un wallet |
| GET | `/api/wallets/low-balance?threshold=X` | Wallets à solde faible |
| GET | `/api/wallets/risk/{customerId}` | Évaluation de risque IA |

#### Token (`/api/tokens`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/tokens` | Lister tous les tokens |
| GET | `/api/tokens/{id}` | Token par ID |
| GET | `/api/tokens/wallet/{walletId}` | Tokens par wallet |
| GET | `/api/tokens/customer/{customerId}` | Tokens par customer |
| POST | `/api/tokens` | Créer un token |
| PUT | `/api/tokens/{id}` | Modifier un token |
| DELETE | `/api/tokens/{id}` | Supprimer un token |
| POST | `/api/tokens/{tokenId}/transfer?recipientCustomerId=X&amount=Y` | Transférer avec conversion |
| GET | `/api/tokens/wallet/{walletId}/total-value` | Valeur totale convertie |

#### Asset (`/api/assets`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/assets` | Lister tous les assets |
| GET | `/api/assets/{id}` | Asset par ID |
| GET | `/api/assets/wallet/{walletId}` | Assets par wallet |
| GET | `/api/assets/owner/{ownerId}` | Assets par propriétaire |
| GET | `/api/assets/type/{assetType}` | Assets par type |
| POST | `/api/assets` | Enregistrer un asset (crédite 10% en collatéral) |
| PUT | `/api/assets/{id}/value?newValue=X` | Revaloriser (ajuste le collatéral) |
| PUT | `/api/assets/{id}/transfer?newOwnerId=X` | Transférer la propriété |
| DELETE | `/api/assets/{id}` | Supprimer un asset |
| GET | `/api/assets/wallet/{walletId}/total-value` | Valeur totale des assets |

### Dockerisation

L'application est entièrement dockerisée avec un build multi-stage :

```bash
# Lancer PostgreSQL seul (développement local)
docker-compose up -d postgres

# Lancer toute la stack (PostgreSQL + Spring Boot)
docker-compose up --build

# Arrêter
docker-compose down
```

Le `Dockerfile` utilise :
- **Stage 1** : `maven:3.8.8-eclipse-temurin-21` pour compiler
- **Stage 2** : `eclipse-temurin:21-jre-alpine` pour l'exécution (image légère)

### Exemple Concret

```
1. User crée un wallet    → POST /api/wallets { "customerId": 1 }
2. User dépose des fonds  → POST /api/wallets/1/deposit?amount=5000
3. User enregistre un bien → POST /api/assets { "ownerId": "1", "assetType": "REAL_ESTATE", "value": 100000, "walletId": 1 }
   → Le wallet reçoit 10 000 en collatéral (10% de la valeur)
4. User crée des tokens   → POST /api/tokens { "value": 1000, "customerId": 1, "walletId": 1 }
5. User transfère         → POST /api/wallets/1/transfer { "amount": 500, "recipientWalletId": 2 }
6. Évaluation de risque   → GET /api/wallets/risk/1
   → Retourne : creditScore, riskLevel, defaultProbability, recommendation
```

---

## �🚀 Getting Started

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

### Important Git Rules
1. **Always pull from main before creating a new branch**
2. **Never work directly on main**
3. **One feature = One branch**
4. **Keep commits small and focused**
5. **Write descriptive commit messages**

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
mvn spring-boot:run
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

