
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
- Starts PostgreSQL on port `5433`
- Runs in detached mode (background)

**Verify it's running**:
```bash
docker ps
```

You should see `equa-postgres` in the list.
### 4. add this file (.env) to your project
```bash
CONTACT RAYEN FOR FILE
```

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn spring-boot:run
```

### 6. Verify the Application
Open your browser and visit:
```
http://localhost:8081/swagger-ui/index.html
```

## 🔀 Git Workflow


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


## ✅ Checklist for First Time Dev

- [ ] Install Java 21
- [ ] Install Maven 3.8.8
- [ ] Install Docker and Docker Compose
- [ ] Clone the repository
- [ ] Create the .env file locally
- [ ] Start PostgreSQL: `docker-compose up -d`
- [ ] Build project: `mvn clean install`
- [ ] Run application: `mvn spring-boot:run`
- [ ] Access Swagger: http://localhost:8081/swagger-ui/index.html
- [ ] Create your first feature branch
- [ ] Read the architecture section
- [ ] Understand the layer responsibilities

**Happy Coding! 🚀**

