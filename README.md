
# <img src="https://img.shields.io/badge/EQUA-6A0DAD?style=for-the-badge&logo=ethereum&logoColor=white" alt="EQUA" height="35">

## <img src="https://img.shields.io/badge/Esprit--PIDEV--4INFINI--2026--EquaToken-FF6B6B?style=for-the-badge&logo=github&logoColor=white" alt="Esprit-PIDEV-4INFINI-2026-EquaToken" height="30">

This project was developed as part of the PIDEV – 4th Year Engineering Program at Esprit School of Engineering (Academic Year 2025–2026).

---

## <img src="https://img.shields.io/badge/📖 Overview-4ECDC4?style=for-the-badge&logo=readthedocs&logoColor=white" alt="Overview" height="30">

EQUA is a decentralized microfinance ecosystem designed to deliver accessible, secure, and low-cost financial services to underserved populations.

The platform leverages tokenization and blockchain technology to provide:

- Micro-loans
- Savings solutions
- Peer-to-peer payments
- Investment opportunities

Unlike traditional financial systems, EQUA focuses on financial inclusion, enabling users without access to banks to participate in a transparent and efficient digital economy.

A key innovation of the platform is the dual-wallet system, allowing users to interact with both:

- EQUA Token Wallet
- Dinar Wallet (Fiat Wallet)

---

### <img src="https://img.shields.io/badge/✨ Features-FFB347?style=for-the-badge&logo=featured&logoColor=white" alt="Features" height="25">

#### User Authentication & Management
- Secure registration and login
- Role-based access control

#### Dual Wallet System
- EQUA Token Wallet for blockchain operations
- Dinar Wallet for fiat (TND) storage and conversion

#### Token Conversion
- Convert TND ↔ EQUA tokens seamlessly
- Stable entry point for non-crypto users

#### Micro-Loans
- Request and receive loans via smart contracts
- Automated repayment tracking

#### Savings Management
- Secure digital savings
- Protection against theft and loss

#### P2P Transactions
- Instant, low-cost transfers between users

#### Blockchain Transparency
- Immutable transaction history
- Public transaction observation

#### Investment Access
- Tokenized assets (real estate, equities, etc.)

#### Smart Contract Automation
- Loan issuance and repayment enforcement
- Transparent financial logic

---

## <img src="https://img.shields.io/badge/🛠️ Tech Stack-45B7D1?style=for-the-badge&logo=spring&logoColor=white" alt="Tech Stack" height="30">

- **Java**: 21.0.9
- **Spring Boot**: 3.2.1
- **Maven**: 3.8.8
- **Database**: PostgreSQL 16
- **Docker**: For containerized database
- **Swagger/OpenAPI**: API documentation

---

## <img src="https://img.shields.io/badge/🏗️ Architecture-96CEB4?style=for-the-badge&logo=architect&logoColor=white" alt="Architecture" height="30">

EQUA follows a modular microservices-inspired architecture within a single project, combining the simplicity of a monolith with the scalability of microservices.

**Shared Components**

- **Shared Database**  
  Single database instance

- **Shared Kernel**  
  Common utilities (DTOs, enums, helpers)  
  Cross-cutting concerns (logging, validation, exceptions)

**Layered Design (Inside Each Service)**

Each module follows clean architecture principles:

- **Presentation Layer**: REST API controllers (per service)
- **Application Layer**: Use cases, services
- **Domain Layer**: Core business models and rules
- **Infrastructure Layer**: Data access, external APIs, repositories

**Communication**

- **Intra-service communication**  
  Direct method calls (since same project)

- **Inter-service communication**  
  Through interfaces / service contracts

**Benefits**

- Microservices mindset without deployment complexity
- Scalable: can be split into real microservices later
- Clear separation of concerns
- Optimized performance (no network overhead)

---

## <img src="https://img.shields.io/badge/👥 Contributors-FF6F61?style=for-the-badge&logo=contributors&logoColor=white" alt="Contributors" height="30">

- RAYEN AMEUR
- NADHMI ROUISSI
- CHAHINE SAADELLAOUI
- SEMER REBHI
- SAFWEN HABOUBI

---

## <img src="https://img.shields.io/badge/📚 Project Resources-9B59B6?style=for-the-badge&logo=book&logoColor=white" alt="Project Resources" height="30">

<p align="left">
  <a href="https://miro.com/app/board/uXjVGGvURDc=/?share_link_id=969033901344" target="_blank">
    <img src="https://img.shields.io/badge/Miro-FFC900?style=for-the-badge&logo=miro&logoColor=black" alt="Miro Board" height="35">
  </a>
  <a href="https://www.canva.com/design/DAG__gdQ8Zk/Pnvcg-baYSi5G3TQ7o7i-g/edit?utm_content=DAG__gdQ8Zk&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton" target="_blank">
    <img src="https://img.shields.io/badge/Canva%20Presentation-00C4CC?style=for-the-badge&logo=canva&logoColor=white" alt="Canva Presentation" height="35">
  </a>
  <a href="https://docs.google.com/document/d/1SJ-QzxcbxZDwQ7bfMLdtrK59frejipB1qcXVu3yWqw8/edit?usp=sharing" target="_blank">
    <img src="https://img.shields.io/badge/Google%20Docs-4285F4?style=for-the-badge&logo=googledocs&logoColor=white" alt="Google Docs Report" height="35">
  </a>
</p>

---

## <img src="https://img.shields.io/badge/🚀 Getting Started-2ECC71?style=for-the-badge&logo=rocket&logoColor=white" alt="Getting Started" height="30">

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

### 3. Add this file (.env) to your project

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

---

### <img src="https://img.shields.io/badge/⚡ Essential Commands-F39C12?style=for-the-badge&logo=terminal&logoColor=white" alt="Essential Commands" height="25">

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

### <img src="https://img.shields.io/badge/🔗 Important URLs-3498DB?style=for-the-badge&logo=link&logoColor=white" alt="Important URLs" height="25">

- **Application**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html

---

### <img src="https://img.shields.io/badge/📝 Code Reviews-E74C3C?style=for-the-badge&logo=code&logoColor=white" alt="Code Reviews" height="25">

Rayen will accept merge requests only if the build job is successful and the code does not negatively impact the project's quality or performance.

---

## <img src="https://img.shields.io/badge/✅ Checklist for First Time Dev-1ABC9C?style=for-the-badge&logo=checklist&logoColor=white" alt="Checklist" height="30">

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

---

## <img src="https://img.shields.io/badge/🙏 Acknowledgment-8E44AD?style=for-the-badge&logo=heart&logoColor=white" alt="Acknowledgment" height="30">

We would like to thank:

- **Esprit School of Engineering** for providing the academic framework.
- Our instructors and mentors, **Mr. Aymen Esselmi, Mr. Mohamed Rjab, and Mr. Saadaoui Skander**, for their guidance, support, and passion in coaching us throughout this project.

---

## <img src="https://img.shields.io/badge/💭 Final Note-34495E?style=for-the-badge&logo=quote&logoColor=white" alt="Final Note" height="30">

EQUA was more than an academic project; it is a proof of our relentless pursuit of growth, of showing up with discipline and clarity.  
We thank our future selves for the glimpses of the vision that keeps us going.
```
